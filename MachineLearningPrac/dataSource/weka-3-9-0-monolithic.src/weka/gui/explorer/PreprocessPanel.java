/*    1:     */ package weka.gui.explorer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Dimension;
/*    6:     */ import java.awt.FlowLayout;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.GridLayout;
/*    9:     */ import java.awt.Toolkit;
/*   10:     */ import java.awt.event.ActionEvent;
/*   11:     */ import java.awt.event.ActionListener;
/*   12:     */ import java.awt.event.ItemEvent;
/*   13:     */ import java.awt.event.ItemListener;
/*   14:     */ import java.awt.event.WindowAdapter;
/*   15:     */ import java.awt.event.WindowEvent;
/*   16:     */ import java.beans.PropertyChangeEvent;
/*   17:     */ import java.beans.PropertyChangeListener;
/*   18:     */ import java.beans.PropertyChangeSupport;
/*   19:     */ import java.io.BufferedOutputStream;
/*   20:     */ import java.io.BufferedWriter;
/*   21:     */ import java.io.File;
/*   22:     */ import java.io.FileOutputStream;
/*   23:     */ import java.io.FileWriter;
/*   24:     */ import java.io.ObjectOutputStream;
/*   25:     */ import java.io.PrintStream;
/*   26:     */ import java.net.URL;
/*   27:     */ import java.util.ArrayList;
/*   28:     */ import java.util.List;
/*   29:     */ import java.util.Map;
/*   30:     */ import javax.swing.BorderFactory;
/*   31:     */ import javax.swing.JButton;
/*   32:     */ import javax.swing.JCheckBox;
/*   33:     */ import javax.swing.JComboBox;
/*   34:     */ import javax.swing.JDialog;
/*   35:     */ import javax.swing.JFileChooser;
/*   36:     */ import javax.swing.JFrame;
/*   37:     */ import javax.swing.JMenu;
/*   38:     */ import javax.swing.JMenuItem;
/*   39:     */ import javax.swing.JOptionPane;
/*   40:     */ import javax.swing.JPanel;
/*   41:     */ import javax.swing.JScrollPane;
/*   42:     */ import javax.swing.JTextArea;
/*   43:     */ import javax.swing.KeyStroke;
/*   44:     */ import javax.swing.ListSelectionModel;
/*   45:     */ import javax.swing.SwingUtilities;
/*   46:     */ import javax.swing.event.ListSelectionEvent;
/*   47:     */ import javax.swing.event.ListSelectionListener;
/*   48:     */ import javax.swing.event.TableModelEvent;
/*   49:     */ import javax.swing.event.TableModelListener;
/*   50:     */ import javax.swing.filechooser.FileFilter;
/*   51:     */ import javax.swing.table.TableModel;
/*   52:     */ import weka.core.Capabilities;
/*   53:     */ import weka.core.CapabilitiesHandler;
/*   54:     */ import weka.core.Defaults;
/*   55:     */ import weka.core.Environment;
/*   56:     */ import weka.core.Instances;
/*   57:     */ import weka.core.OptionHandler;
/*   58:     */ import weka.core.Settings;
/*   59:     */ import weka.core.Settings.SettingKey;
/*   60:     */ import weka.core.Utils;
/*   61:     */ import weka.core.WekaPackageManager;
/*   62:     */ import weka.core.converters.AbstractFileLoader;
/*   63:     */ import weka.core.converters.AbstractFileSaver;
/*   64:     */ import weka.core.converters.CSVLoader;
/*   65:     */ import weka.core.converters.ConverterUtils;
/*   66:     */ import weka.core.converters.Loader;
/*   67:     */ import weka.core.converters.SerializedInstancesLoader;
/*   68:     */ import weka.core.converters.URLSourcedLoader;
/*   69:     */ import weka.datagenerators.DataGenerator;
/*   70:     */ import weka.experiment.InstanceQuery;
/*   71:     */ import weka.filters.AllFilter;
/*   72:     */ import weka.filters.Filter;
/*   73:     */ import weka.filters.SupervisedFilter;
/*   74:     */ import weka.filters.unsupervised.attribute.Remove;
/*   75:     */ import weka.gui.AbstractPerspective;
/*   76:     */ import weka.gui.AttributeSelectionPanel;
/*   77:     */ import weka.gui.AttributeSummaryPanel;
/*   78:     */ import weka.gui.AttributeVisualizationPanel;
/*   79:     */ import weka.gui.ConverterFileChooser;
/*   80:     */ import weka.gui.GUIApplication;
/*   81:     */ import weka.gui.GenericObjectEditor;
/*   82:     */ import weka.gui.GenericObjectEditor.GOEPanel;
/*   83:     */ import weka.gui.InstancesSummaryPanel;
/*   84:     */ import weka.gui.LogPanel;
/*   85:     */ import weka.gui.Logger;
/*   86:     */ import weka.gui.Perspective;
/*   87:     */ import weka.gui.PerspectiveInfo;
/*   88:     */ import weka.gui.PerspectiveManager;
/*   89:     */ import weka.gui.PropertyDialog;
/*   90:     */ import weka.gui.PropertyPanel;
/*   91:     */ import weka.gui.SysErrLog;
/*   92:     */ import weka.gui.TaskLogger;
/*   93:     */ import weka.gui.ViewerDialog;
/*   94:     */ import weka.gui.WorkbenchApp;
/*   95:     */ import weka.gui.beans.AttributeSummarizer;
/*   96:     */ import weka.gui.sql.SqlViewerDialog;
/*   97:     */ 
/*   98:     */ @PerspectiveInfo(ID="weka.gui.explorer.preprocesspanel", title="Preprocess", toolTipText="Preprocess data", iconPath="weka/gui/weka_icon_new_small.png")
/*   99:     */ public class PreprocessPanel
/*  100:     */   extends AbstractPerspective
/*  101:     */   implements Explorer.CapabilitiesFilterChangeListener, Explorer.ExplorerPanel, Explorer.LogHandler
/*  102:     */ {
/*  103:     */   private static final long serialVersionUID = 6764850273874813049L;
/*  104: 134 */   protected InstancesSummaryPanel m_InstSummaryPanel = new InstancesSummaryPanel();
/*  105: 138 */   protected JButton m_OpenFileBut = new JButton("Open file...");
/*  106: 141 */   protected JButton m_OpenURLBut = new JButton("Open URL...");
/*  107: 144 */   protected JButton m_OpenDBBut = new JButton("Open DB...");
/*  108: 147 */   protected JButton m_GenerateBut = new JButton("Generate...");
/*  109: 150 */   protected JButton m_UndoBut = new JButton("Undo");
/*  110: 153 */   protected JButton m_EditBut = new JButton("Edit...");
/*  111: 154 */   protected JMenuItem m_EditM = new JMenuItem("Edit...");
/*  112:     */   protected JMenu m_sendToPerspective;
/*  113: 160 */   protected JButton m_SaveBut = new JButton("Save...");
/*  114: 163 */   protected AttributeSelectionPanel m_AttPanel = new AttributeSelectionPanel();
/*  115: 166 */   protected JButton m_RemoveButton = new JButton("Remove");
/*  116: 169 */   protected AttributeSummaryPanel m_AttSummaryPanel = new AttributeSummaryPanel();
/*  117: 173 */   protected GenericObjectEditor m_FilterEditor = new GenericObjectEditor();
/*  118: 176 */   protected PropertyPanel m_FilterPanel = new PropertyPanel(this.m_FilterEditor);
/*  119: 179 */   protected JButton m_ApplyFilterBut = new JButton("Apply");
/*  120:     */   protected ConverterFileChooser m_FileChooser;
/*  121: 185 */   protected String m_LastURL = "http://";
/*  122: 188 */   protected String m_SQLQ = new String("SELECT * FROM ?");
/*  123:     */   protected Instances m_Instances;
/*  124: 194 */   protected DataGenerator m_DataGenerator = null;
/*  125: 197 */   protected AttributeVisualizationPanel m_AttVisualizePanel = new AttributeVisualizationPanel();
/*  126: 201 */   protected File[] m_tempUndoFiles = new File[20];
/*  127: 205 */   protected int m_tempUndoIndex = 0;
/*  128: 211 */   protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
/*  129:     */   protected Thread m_IOThread;
/*  130: 217 */   protected Logger m_Log = new SysErrLog();
/*  131: 220 */   protected Explorer m_Explorer = null;
/*  132:     */   protected boolean m_initialSettingsSet;
/*  133: 226 */   protected List<JMenu> m_menus = new ArrayList();
/*  134:     */   
/*  135:     */   static
/*  136:     */   {
/*  137: 229 */     WekaPackageManager.loadPackages(false);
/*  138: 230 */     GenericObjectEditor.registerEditors();
/*  139:     */   }
/*  140:     */   
/*  141:     */   public PreprocessPanel()
/*  142:     */   {
/*  143: 238 */     String initialDir = ExplorerDefaults.getInitialDirectory();
/*  144: 239 */     this.m_FileChooser = new ConverterFileChooser(new File(initialDir));
/*  145:     */     
/*  146:     */ 
/*  147: 242 */     this.m_FilterEditor.setClassType(Filter.class);
/*  148: 243 */     if (ExplorerDefaults.getFilter() != null) {
/*  149: 244 */       this.m_FilterEditor.setValue(ExplorerDefaults.getFilter());
/*  150:     */     }
/*  151: 246 */     this.m_FilterEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  152:     */     {
/*  153:     */       public void propertyChange(PropertyChangeEvent e)
/*  154:     */       {
/*  155: 248 */         PreprocessPanel.this.m_ApplyFilterBut.setEnabled(PreprocessPanel.this.getInstances() != null);
/*  156: 249 */         Capabilities currentCapabilitiesFilter = PreprocessPanel.this.m_FilterEditor.getCapabilitiesFilter();
/*  157:     */         
/*  158: 251 */         Filter filter = (Filter)PreprocessPanel.this.m_FilterEditor.getValue();
/*  159: 252 */         Capabilities currentFilterCapabilities = null;
/*  160: 253 */         if ((filter != null) && (currentCapabilitiesFilter != null) && ((filter instanceof CapabilitiesHandler)))
/*  161:     */         {
/*  162: 255 */           currentFilterCapabilities = filter.getCapabilities();
/*  163: 258 */           if ((!currentFilterCapabilities.supportsMaybe(currentCapabilitiesFilter)) && (!currentFilterCapabilities.supports(currentCapabilitiesFilter))) {
/*  164:     */             try
/*  165:     */             {
/*  166: 262 */               filter.setInputFormat(PreprocessPanel.this.getInstances());
/*  167:     */             }
/*  168:     */             catch (Exception ex)
/*  169:     */             {
/*  170: 264 */               PreprocessPanel.this.m_ApplyFilterBut.setEnabled(false);
/*  171:     */             }
/*  172:     */           }
/*  173:     */         }
/*  174:     */       }
/*  175: 269 */     });
/*  176: 270 */     JMenu fileMenu = new JMenu();
/*  177: 271 */     fileMenu.setText("File");
/*  178: 272 */     this.m_menus.add(fileMenu);
/*  179: 273 */     this.m_OpenFileBut.setToolTipText("Open a set of instances from a file");
/*  180: 274 */     JMenuItem openFileM = new JMenuItem("Open file...");
/*  181: 275 */     openFileM.setAccelerator(KeyStroke.getKeyStroke(70, 128));
/*  182:     */     
/*  183: 277 */     fileMenu.add(openFileM);
/*  184: 278 */     this.m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
/*  185: 279 */     JMenuItem openURLM = new JMenuItem("Open URL...");
/*  186: 280 */     openURLM.setAccelerator(KeyStroke.getKeyStroke(85, 128));
/*  187:     */     
/*  188: 282 */     fileMenu.add(openURLM);
/*  189: 283 */     this.m_OpenDBBut.setToolTipText("Open a set of instances from a database");
/*  190: 284 */     JMenuItem openDBM = new JMenuItem("Open DB...");
/*  191: 285 */     openDBM.setAccelerator(KeyStroke.getKeyStroke(68, 128));
/*  192:     */     
/*  193: 287 */     fileMenu.add(openDBM);
/*  194: 288 */     this.m_GenerateBut.setToolTipText("Generates artificial data");
/*  195: 289 */     JMenuItem generateM = new JMenuItem("Generate...");
/*  196: 290 */     generateM.setAccelerator(KeyStroke.getKeyStroke(71, 128));
/*  197:     */     
/*  198: 292 */     this.m_UndoBut.setToolTipText("Undo the last change to the dataset");
/*  199: 293 */     fileMenu.add(generateM);
/*  200: 294 */     this.m_UndoBut.setEnabled(ExplorerDefaults.get("enableUndo", "true").equalsIgnoreCase("true"));
/*  201: 296 */     if (!this.m_UndoBut.isEnabled()) {
/*  202: 297 */       this.m_UndoBut.setToolTipText("Undo is disabled - see weka.gui.explorer.Explorer.props to enable");
/*  203:     */     }
/*  204: 300 */     this.m_EditBut.setToolTipText("Open the current dataset in a Viewer for editing");
/*  205:     */     
/*  206: 302 */     JMenu editMenu = new JMenu();
/*  207: 303 */     editMenu.add(this.m_EditM);
/*  208: 304 */     this.m_EditM.setAccelerator(KeyStroke.getKeyStroke(69, 128));
/*  209:     */     
/*  210: 306 */     this.m_EditM.setEnabled(false);
/*  211: 307 */     editMenu.setText("Edit");
/*  212: 308 */     this.m_menus.add(editMenu);
/*  213:     */     
/*  214: 310 */     this.m_SaveBut.setToolTipText("Save the working relation to a file");
/*  215: 311 */     this.m_ApplyFilterBut.setToolTipText("Apply the current filter to the data");
/*  216:     */     
/*  217: 313 */     this.m_FileChooser.setFileSelectionMode(2);
/*  218: 314 */     this.m_OpenURLBut.addActionListener(new ActionListener()
/*  219:     */     {
/*  220:     */       public void actionPerformed(ActionEvent e)
/*  221:     */       {
/*  222: 316 */         PreprocessPanel.this.setInstancesFromURLQ();
/*  223:     */       }
/*  224: 318 */     });
/*  225: 319 */     openURLM.addActionListener(new ActionListener()
/*  226:     */     {
/*  227:     */       public void actionPerformed(ActionEvent e)
/*  228:     */       {
/*  229: 322 */         PreprocessPanel.this.setInstancesFromURLQ();
/*  230:     */       }
/*  231: 324 */     });
/*  232: 325 */     this.m_OpenDBBut.addActionListener(new ActionListener()
/*  233:     */     {
/*  234:     */       public void actionPerformed(ActionEvent e)
/*  235:     */       {
/*  236: 327 */         SqlViewerDialog dialog = new SqlViewerDialog(null);
/*  237: 328 */         dialog.setVisible(true);
/*  238: 329 */         if (dialog.getReturnValue() == 0) {
/*  239: 330 */           PreprocessPanel.this.setInstancesFromDBQ(dialog.getURL(), dialog.getUser(), dialog.getPassword(), dialog.getQuery(), dialog.getGenerateSparseData());
/*  240:     */         }
/*  241:     */       }
/*  242: 334 */     });
/*  243: 335 */     this.m_OpenFileBut.addActionListener(new ActionListener()
/*  244:     */     {
/*  245:     */       public void actionPerformed(ActionEvent e)
/*  246:     */       {
/*  247: 337 */         PreprocessPanel.this.setInstancesFromFileQ();
/*  248:     */       }
/*  249: 339 */     });
/*  250: 340 */     openFileM.addActionListener(new ActionListener()
/*  251:     */     {
/*  252:     */       public void actionPerformed(ActionEvent e)
/*  253:     */       {
/*  254: 343 */         PreprocessPanel.this.setInstancesFromFileQ();
/*  255:     */       }
/*  256: 345 */     });
/*  257: 346 */     this.m_GenerateBut.addActionListener(new ActionListener()
/*  258:     */     {
/*  259:     */       public void actionPerformed(ActionEvent e)
/*  260:     */       {
/*  261: 348 */         PreprocessPanel.this.generateInstances();
/*  262:     */       }
/*  263: 350 */     });
/*  264: 351 */     generateM.addActionListener(new ActionListener()
/*  265:     */     {
/*  266:     */       public void actionPerformed(ActionEvent e)
/*  267:     */       {
/*  268: 354 */         PreprocessPanel.this.generateInstances();
/*  269:     */       }
/*  270: 356 */     });
/*  271: 357 */     this.m_UndoBut.addActionListener(new ActionListener()
/*  272:     */     {
/*  273:     */       public void actionPerformed(ActionEvent e)
/*  274:     */       {
/*  275: 359 */         PreprocessPanel.this.undo();
/*  276:     */       }
/*  277: 361 */     });
/*  278: 362 */     this.m_EditBut.addActionListener(new ActionListener()
/*  279:     */     {
/*  280:     */       public void actionPerformed(ActionEvent e)
/*  281:     */       {
/*  282: 364 */         PreprocessPanel.this.edit();
/*  283:     */       }
/*  284: 366 */     });
/*  285: 367 */     this.m_EditM.addActionListener(new ActionListener()
/*  286:     */     {
/*  287:     */       public void actionPerformed(ActionEvent e)
/*  288:     */       {
/*  289: 370 */         PreprocessPanel.this.edit();
/*  290:     */       }
/*  291: 372 */     });
/*  292: 373 */     this.m_SaveBut.addActionListener(new ActionListener()
/*  293:     */     {
/*  294:     */       public void actionPerformed(ActionEvent e)
/*  295:     */       {
/*  296: 375 */         PreprocessPanel.this.saveWorkingInstancesToFileQ();
/*  297:     */       }
/*  298: 377 */     });
/*  299: 378 */     this.m_ApplyFilterBut.addActionListener(new ActionListener()
/*  300:     */     {
/*  301:     */       public void actionPerformed(ActionEvent e)
/*  302:     */       {
/*  303: 380 */         PreprocessPanel.this.applyFilter((Filter)PreprocessPanel.this.m_FilterEditor.getValue());
/*  304:     */       }
/*  305: 382 */     });
/*  306: 383 */     this.m_AttPanel.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  307:     */     {
/*  308:     */       public void valueChanged(ListSelectionEvent e)
/*  309:     */       {
/*  310: 386 */         if (!e.getValueIsAdjusting())
/*  311:     */         {
/*  312: 387 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  313: 388 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  314: 389 */             if (lm.isSelectedIndex(i))
/*  315:     */             {
/*  316: 390 */               PreprocessPanel.this.m_AttSummaryPanel.setAttribute(i);
/*  317: 391 */               PreprocessPanel.this.m_AttVisualizePanel.setAttribute(i);
/*  318: 392 */               break;
/*  319:     */             }
/*  320:     */           }
/*  321:     */         }
/*  322:     */       }
/*  323: 398 */     });
/*  324: 399 */     this.m_InstSummaryPanel.setBorder(BorderFactory.createTitledBorder("Current relation"));
/*  325:     */     
/*  326: 401 */     JPanel attStuffHolderPanel = new JPanel();
/*  327: 402 */     attStuffHolderPanel.setBorder(BorderFactory.createTitledBorder("Attributes"));
/*  328:     */     
/*  329: 404 */     attStuffHolderPanel.setLayout(new BorderLayout());
/*  330: 405 */     attStuffHolderPanel.add(this.m_AttPanel, "Center");
/*  331: 406 */     this.m_RemoveButton.setEnabled(false);
/*  332: 407 */     this.m_RemoveButton.setToolTipText("Remove selected attributes.");
/*  333: 408 */     this.m_RemoveButton.addActionListener(new ActionListener()
/*  334:     */     {
/*  335:     */       public void actionPerformed(ActionEvent e)
/*  336:     */       {
/*  337:     */         try
/*  338:     */         {
/*  339: 411 */           Remove r = new Remove();
/*  340: 412 */           int[] selected = PreprocessPanel.this.m_AttPanel.getSelectedAttributes();
/*  341: 413 */           if (selected.length == 0) {
/*  342: 414 */             return;
/*  343:     */           }
/*  344: 416 */           if (selected.length == PreprocessPanel.this.m_Instances.numAttributes())
/*  345:     */           {
/*  346: 418 */             JOptionPane.showMessageDialog(PreprocessPanel.this, "Can't remove all attributes from data!\n", "Remove Attributes", 0);
/*  347:     */             
/*  348:     */ 
/*  349: 421 */             PreprocessPanel.this.m_Log.logMessage("Can't remove all attributes from data!");
/*  350: 422 */             PreprocessPanel.this.m_Log.statusMessage("Problem removing attributes");
/*  351: 423 */             return;
/*  352:     */           }
/*  353: 425 */           r.setAttributeIndicesArray(selected);
/*  354: 426 */           PreprocessPanel.this.applyFilter(r);
/*  355: 427 */           PreprocessPanel.this.m_RemoveButton.setEnabled(false);
/*  356:     */         }
/*  357:     */         catch (Exception ex)
/*  358:     */         {
/*  359: 429 */           if ((PreprocessPanel.this.m_Log instanceof TaskLogger)) {
/*  360: 430 */             ((TaskLogger)PreprocessPanel.this.m_Log).taskFinished();
/*  361:     */           }
/*  362: 433 */           JOptionPane.showMessageDialog(PreprocessPanel.this, "Problem filtering instances:\n" + ex.getMessage(), "Remove Attributes", 0);
/*  363:     */           
/*  364:     */ 
/*  365: 436 */           PreprocessPanel.this.m_Log.logMessage("Problem removing attributes: " + ex.getMessage());
/*  366: 437 */           PreprocessPanel.this.m_Log.statusMessage("Problem removing attributes");
/*  367:     */         }
/*  368:     */       }
/*  369: 441 */     });
/*  370: 442 */     JPanel p1 = new JPanel();
/*  371: 443 */     p1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/*  372: 444 */     p1.setLayout(new BorderLayout());
/*  373: 445 */     p1.add(this.m_RemoveButton, "Center");
/*  374: 446 */     attStuffHolderPanel.add(p1, "South");
/*  375: 447 */     this.m_AttSummaryPanel.setBorder(BorderFactory.createTitledBorder("Selected attribute"));
/*  376:     */     
/*  377: 449 */     this.m_UndoBut.setEnabled(false);
/*  378: 450 */     this.m_EditBut.setEnabled(false);
/*  379: 451 */     this.m_SaveBut.setEnabled(false);
/*  380: 452 */     this.m_ApplyFilterBut.setEnabled(false);
/*  381:     */     
/*  382:     */ 
/*  383: 455 */     JPanel buttons = new JPanel();
/*  384: 456 */     buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/*  385: 457 */     buttons.setLayout(new GridLayout(1, 6, 5, 5));
/*  386: 458 */     buttons.add(this.m_OpenFileBut);
/*  387: 459 */     buttons.add(this.m_OpenURLBut);
/*  388: 460 */     buttons.add(this.m_OpenDBBut);
/*  389: 461 */     buttons.add(this.m_GenerateBut);
/*  390: 462 */     buttons.add(this.m_UndoBut);
/*  391: 463 */     buttons.add(this.m_EditBut);
/*  392: 464 */     buttons.add(this.m_SaveBut);
/*  393:     */     
/*  394: 466 */     JPanel attInfo = new JPanel();
/*  395:     */     
/*  396: 468 */     attInfo.setLayout(new BorderLayout());
/*  397: 469 */     attInfo.add(attStuffHolderPanel, "Center");
/*  398:     */     
/*  399: 471 */     JPanel filter = new JPanel();
/*  400: 472 */     filter.setBorder(BorderFactory.createTitledBorder("Filter"));
/*  401: 473 */     filter.setLayout(new BorderLayout());
/*  402: 474 */     filter.add(this.m_FilterPanel, "Center");
/*  403: 475 */     filter.add(this.m_ApplyFilterBut, "East");
/*  404:     */     
/*  405: 477 */     JPanel attVis = new JPanel();
/*  406: 478 */     attVis.setLayout(new GridLayout(2, 1));
/*  407: 479 */     attVis.add(this.m_AttSummaryPanel);
/*  408:     */     
/*  409: 481 */     JComboBox colorBox = this.m_AttVisualizePanel.getColorBox();
/*  410: 482 */     colorBox.setToolTipText("The chosen attribute will also be used as the class attribute when a filter is applied.");
/*  411:     */     
/*  412: 484 */     colorBox.addItemListener(new ItemListener()
/*  413:     */     {
/*  414:     */       public void itemStateChanged(ItemEvent ie)
/*  415:     */       {
/*  416: 486 */         if (ie.getStateChange() == 1) {
/*  417: 487 */           PreprocessPanel.this.updateCapabilitiesFilter(PreprocessPanel.this.m_FilterEditor.getCapabilitiesFilter());
/*  418:     */         }
/*  419:     */       }
/*  420: 490 */     });
/*  421: 491 */     final JButton visAllBut = new JButton("Visualize All");
/*  422: 492 */     visAllBut.addActionListener(new ActionListener()
/*  423:     */     {
/*  424:     */       public void actionPerformed(ActionEvent ae)
/*  425:     */       {
/*  426: 494 */         if (PreprocessPanel.this.m_Instances != null) {
/*  427:     */           try
/*  428:     */           {
/*  429: 496 */             AttributeSummarizer as = new AttributeSummarizer();
/*  430:     */             
/*  431: 498 */             as.setColoringIndex(PreprocessPanel.this.m_AttVisualizePanel.getColoringIndex());
/*  432: 499 */             as.setInstances(PreprocessPanel.this.m_Instances);
/*  433:     */             
/*  434: 501 */             final JFrame jf = new JFrame();
/*  435: 502 */             jf.getContentPane().setLayout(new BorderLayout());
/*  436:     */             
/*  437: 504 */             jf.getContentPane().add(as, "Center");
/*  438: 505 */             jf.addWindowListener(new WindowAdapter()
/*  439:     */             {
/*  440:     */               public void windowClosing(WindowEvent e)
/*  441:     */               {
/*  442: 508 */                 PreprocessPanel.17.this.val$visAllBut.setEnabled(true);
/*  443: 509 */                 jf.dispose();
/*  444:     */               }
/*  445: 511 */             });
/*  446: 512 */             jf.setSize(830, 600);
/*  447: 513 */             jf.setVisible(true);
/*  448:     */           }
/*  449:     */           catch (Exception ex)
/*  450:     */           {
/*  451: 515 */             ex.printStackTrace();
/*  452:     */           }
/*  453:     */         }
/*  454:     */       }
/*  455: 519 */     });
/*  456: 520 */     JPanel histoHolder = new JPanel();
/*  457: 521 */     histoHolder.setLayout(new BorderLayout());
/*  458: 522 */     histoHolder.add(this.m_AttVisualizePanel, "Center");
/*  459: 523 */     JPanel histoControls = new JPanel();
/*  460: 524 */     histoControls.setLayout(new BorderLayout());
/*  461: 525 */     histoControls.add(colorBox, "Center");
/*  462: 526 */     histoControls.add(visAllBut, "East");
/*  463: 527 */     histoHolder.add(histoControls, "North");
/*  464: 528 */     attVis.add(histoHolder);
/*  465:     */     
/*  466: 530 */     JPanel lhs = new JPanel();
/*  467: 531 */     lhs.setLayout(new BorderLayout());
/*  468: 532 */     lhs.add(this.m_InstSummaryPanel, "North");
/*  469: 533 */     lhs.add(attInfo, "Center");
/*  470:     */     
/*  471: 535 */     JPanel rhs = new JPanel();
/*  472: 536 */     rhs.setLayout(new BorderLayout());
/*  473: 537 */     rhs.add(attVis, "Center");
/*  474:     */     
/*  475: 539 */     JPanel relation = new JPanel();
/*  476: 540 */     relation.setLayout(new GridLayout(1, 2));
/*  477: 541 */     relation.add(lhs);
/*  478: 542 */     relation.add(rhs);
/*  479:     */     
/*  480: 544 */     JPanel middle = new JPanel();
/*  481: 545 */     middle.setLayout(new BorderLayout());
/*  482: 546 */     middle.add(filter, "North");
/*  483: 547 */     middle.add(relation, "Center");
/*  484:     */     
/*  485: 549 */     setLayout(new BorderLayout());
/*  486: 550 */     add(buttons, "North");
/*  487: 551 */     add(middle, "Center");
/*  488:     */   }
/*  489:     */   
/*  490:     */   public boolean acceptsInstances()
/*  491:     */   {
/*  492: 561 */     return true;
/*  493:     */   }
/*  494:     */   
/*  495:     */   public void instantiationComplete()
/*  496:     */   {
/*  497: 573 */     boolean sendToAll = ((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL_KEY, Boolean.valueOf(PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL), Environment.getSystemWide())).booleanValue();
/*  498:     */     
/*  499:     */ 
/*  500:     */ 
/*  501:     */ 
/*  502:     */ 
/*  503:     */ 
/*  504:     */ 
/*  505:     */ 
/*  506: 582 */     final List<Perspective> perspectivesThatAcceptInstances = new ArrayList();
/*  507:     */     
/*  508: 584 */     List<Perspective> visiblePerspectives = getMainApplication().getPerspectiveManager().getVisiblePerspectives();
/*  509: 586 */     for (Perspective p : visiblePerspectives) {
/*  510: 587 */       if ((p.acceptsInstances()) && (!p.getPerspectiveID().equals(getPerspectiveID()))) {
/*  511: 589 */         perspectivesThatAcceptInstances.add(p);
/*  512:     */       }
/*  513:     */     }
/*  514: 593 */     if (perspectivesThatAcceptInstances.size() > 0)
/*  515:     */     {
/*  516: 594 */       JMenu fileMenu = (JMenu)this.m_menus.get(0);
/*  517: 595 */       this.m_sendToPerspective = new JMenu();
/*  518: 596 */       this.m_sendToPerspective.setText("Send to perspective");
/*  519: 597 */       fileMenu.add(this.m_sendToPerspective);
/*  520: 598 */       if (!sendToAll) {
/*  521: 599 */         this.m_sendToPerspective.setEnabled(false);
/*  522:     */       }
/*  523: 602 */       JMenuItem sendToAllItem = new JMenuItem("All perspectives");
/*  524: 603 */       sendToAllItem.addActionListener(new ActionListener()
/*  525:     */       {
/*  526:     */         public void actionPerformed(ActionEvent e)
/*  527:     */         {
/*  528: 606 */           for (Perspective p : perspectivesThatAcceptInstances) {
/*  529: 607 */             if ((PreprocessPanel.this.getInstances() != null) && (p.acceptsInstances()))
/*  530:     */             {
/*  531: 608 */               p.setInstances(PreprocessPanel.this.getInstances());
/*  532: 609 */               PreprocessPanel.this.getMainApplication().getPerspectiveManager().setEnablePerspectiveTab(p.getPerspectiveID(), true);
/*  533:     */             }
/*  534:     */           }
/*  535:     */         }
/*  536: 614 */       });
/*  537: 615 */       this.m_sendToPerspective.add(sendToAllItem);
/*  538: 617 */       for (final Perspective p : perspectivesThatAcceptInstances)
/*  539:     */       {
/*  540: 618 */         JMenuItem item = new JMenuItem(p.getPerspectiveTitle());
/*  541: 619 */         this.m_sendToPerspective.add(item);
/*  542: 620 */         item.addActionListener(new ActionListener()
/*  543:     */         {
/*  544:     */           public void actionPerformed(ActionEvent e)
/*  545:     */           {
/*  546: 623 */             if (PreprocessPanel.this.getInstances() != null)
/*  547:     */             {
/*  548: 624 */               p.setInstances(PreprocessPanel.this.getInstances());
/*  549: 625 */               PreprocessPanel.this.getMainApplication().getPerspectiveManager().setEnablePerspectiveTab(p.getPerspectiveID(), true);
/*  550:     */               
/*  551: 627 */               PreprocessPanel.this.getMainApplication().getPerspectiveManager().setActivePerspective(p.getPerspectiveID());
/*  552:     */             }
/*  553:     */           }
/*  554:     */         });
/*  555:     */       }
/*  556:     */     }
/*  557:     */   }
/*  558:     */   
/*  559:     */   public void setLog(Logger newLog)
/*  560:     */   {
/*  561: 643 */     this.m_Log = newLog;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public boolean requiresLog()
/*  565:     */   {
/*  566: 648 */     return true;
/*  567:     */   }
/*  568:     */   
/*  569:     */   public void setInstances(Instances inst)
/*  570:     */   {
/*  571: 658 */     this.m_Instances = inst;
/*  572:     */     try
/*  573:     */     {
/*  574: 660 */       Runnable r = new Runnable()
/*  575:     */       {
/*  576:     */         public void run()
/*  577:     */         {
/*  578: 662 */           boolean first = PreprocessPanel.this.m_AttPanel.getTableModel() == null;
/*  579:     */           
/*  580: 664 */           PreprocessPanel.this.m_InstSummaryPanel.setInstances(PreprocessPanel.this.m_Instances);
/*  581: 665 */           PreprocessPanel.this.m_AttPanel.setInstances(PreprocessPanel.this.m_Instances);
/*  582: 667 */           if (first)
/*  583:     */           {
/*  584: 668 */             TableModel model = PreprocessPanel.this.m_AttPanel.getTableModel();
/*  585: 669 */             model.addTableModelListener(new TableModelListener()
/*  586:     */             {
/*  587:     */               public void tableChanged(TableModelEvent e)
/*  588:     */               {
/*  589: 671 */                 if ((PreprocessPanel.this.m_AttPanel.getSelectedAttributes() != null) && (PreprocessPanel.this.m_AttPanel.getSelectedAttributes().length > 0)) {
/*  590: 673 */                   PreprocessPanel.this.m_RemoveButton.setEnabled(true);
/*  591:     */                 } else {
/*  592: 675 */                   PreprocessPanel.this.m_RemoveButton.setEnabled(false);
/*  593:     */                 }
/*  594:     */               }
/*  595:     */             });
/*  596:     */           }
/*  597: 681 */           PreprocessPanel.this.m_AttSummaryPanel.setInstances(PreprocessPanel.this.m_Instances);
/*  598: 682 */           PreprocessPanel.this.m_AttVisualizePanel.setInstances(PreprocessPanel.this.m_Instances);
/*  599:     */           
/*  600:     */ 
/*  601: 685 */           PreprocessPanel.this.m_AttPanel.getSelectionModel().setSelectionInterval(0, 0);
/*  602: 686 */           PreprocessPanel.this.m_AttSummaryPanel.setAttribute(0);
/*  603: 687 */           PreprocessPanel.this.m_AttVisualizePanel.setAttribute(0);
/*  604:     */           
/*  605: 689 */           PreprocessPanel.this.m_ApplyFilterBut.setEnabled(true);
/*  606:     */           
/*  607: 691 */           PreprocessPanel.this.m_Log.logMessage("Base relation is now " + PreprocessPanel.this.m_Instances.relationName() + " (" + PreprocessPanel.this.m_Instances.numInstances() + " instances)");
/*  608:     */           
/*  609: 693 */           PreprocessPanel.this.m_SaveBut.setEnabled(true);
/*  610: 694 */           PreprocessPanel.this.m_EditBut.setEnabled(true);
/*  611: 695 */           PreprocessPanel.this.m_EditM.setEnabled(true);
/*  612: 696 */           PreprocessPanel.this.m_Log.statusMessage("OK");
/*  613:     */           
/*  614: 698 */           PreprocessPanel.this.m_Support.firePropertyChange("", null, null);
/*  615:     */           
/*  616: 700 */           boolean sendToAll = (PreprocessPanel.this.getExplorer() != null) || (((Boolean)PreprocessPanel.this.getMainApplication().getApplicationSettings().getSetting(PreprocessPanel.this.getPerspectiveID(), PreprocessPanel.PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL_KEY, Boolean.valueOf(PreprocessPanel.PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL), Environment.getSystemWide())).booleanValue());
/*  617: 707 */           if (PreprocessPanel.this.m_sendToPerspective != null) {
/*  618: 708 */             PreprocessPanel.this.m_sendToPerspective.setEnabled(!sendToAll);
/*  619:     */           }
/*  620: 711 */           if ((PreprocessPanel.this.getMainApplication() != null) && 
/*  621: 712 */             (sendToAll))
/*  622:     */           {
/*  623: 713 */             List<Perspective> perspectiveList = PreprocessPanel.this.getMainApplication().getPerspectiveManager().getVisiblePerspectives();
/*  624: 716 */             for (Perspective p : perspectiveList) {
/*  625: 717 */               if ((p.acceptsInstances()) && (!p.getPerspectiveID().equals(PreprocessPanel.this.getPerspectiveID()))) {
/*  626: 719 */                 p.setInstances(PreprocessPanel.this.m_Instances);
/*  627:     */               }
/*  628:     */             }
/*  629:     */           }
/*  630: 726 */           if ((PreprocessPanel.this.getExplorer() != null) || (PreprocessPanel.this.getMainApplication() != null))
/*  631:     */           {
/*  632: 727 */             Explorer explorer = PreprocessPanel.this.getExplorer();
/*  633: 728 */             WorkbenchApp app = (WorkbenchApp)PreprocessPanel.this.getMainApplication();
/*  634:     */             try
/*  635:     */             {
/*  636: 731 */               if (explorer != null)
/*  637:     */               {
/*  638: 732 */                 explorer.notifyCapabilitiesFilterListener(null);
/*  639:     */               }
/*  640:     */               else
/*  641:     */               {
/*  642: 734 */                 app.notifyCapabilitiesFilterListeners(null);
/*  643: 737 */                 if (sendToAll) {
/*  644: 738 */                   app.getPerspectiveManager().enableAllPerspectiveTabs();
/*  645:     */                 }
/*  646:     */               }
/*  647: 742 */               int oldIndex = PreprocessPanel.this.m_Instances.classIndex();
/*  648: 743 */               PreprocessPanel.this.m_Instances.setClassIndex(PreprocessPanel.this.m_AttVisualizePanel.getColorBox().getSelectedIndex() - 1);
/*  649: 747 */               if (ExplorerDefaults.getInitGenericObjectEditorFilter())
/*  650:     */               {
/*  651: 748 */                 if (explorer != null) {
/*  652: 749 */                   explorer.notifyCapabilitiesFilterListener(Capabilities.forInstances(PreprocessPanel.this.m_Instances));
/*  653: 752 */                 } else if (sendToAll) {
/*  654: 753 */                   app.notifyCapabilitiesFilterListeners(Capabilities.forInstances(PreprocessPanel.this.m_Instances));
/*  655:     */                 }
/*  656:     */               }
/*  657: 758 */               else if (explorer != null) {
/*  658: 759 */                 explorer.notifyCapabilitiesFilterListener(Capabilities.forInstances(new Instances(PreprocessPanel.this.m_Instances, 0)));
/*  659: 762 */               } else if (sendToAll) {
/*  660: 763 */                 app.notifyCapabilitiesFilterListeners(Capabilities.forInstances(new Instances(PreprocessPanel.this.m_Instances, 0)));
/*  661:     */               }
/*  662: 769 */               PreprocessPanel.this.m_Instances.setClassIndex(oldIndex);
/*  663:     */             }
/*  664:     */             catch (Exception e)
/*  665:     */             {
/*  666: 771 */               e.printStackTrace();
/*  667: 772 */               PreprocessPanel.this.m_Log.logMessage(e.toString());
/*  668:     */             }
/*  669:     */           }
/*  670:     */         }
/*  671:     */       };
/*  672: 777 */       if (SwingUtilities.isEventDispatchThread()) {
/*  673: 778 */         r.run();
/*  674:     */       } else {
/*  675: 780 */         SwingUtilities.invokeAndWait(r);
/*  676:     */       }
/*  677:     */     }
/*  678:     */     catch (Exception ex)
/*  679:     */     {
/*  680: 783 */       ex.printStackTrace();
/*  681: 784 */       JOptionPane.showMessageDialog(this, "Problem setting base instances:\n" + ex, "Instances", 0);
/*  682:     */     }
/*  683:     */   }
/*  684:     */   
/*  685:     */   public Instances getInstances()
/*  686:     */   {
/*  687: 796 */     return this.m_Instances;
/*  688:     */   }
/*  689:     */   
/*  690:     */   public void addPropertyChangeListener(PropertyChangeListener l)
/*  691:     */   {
/*  692: 807 */     if ((this.m_Support != null) && (l != null)) {
/*  693: 808 */       this.m_Support.addPropertyChangeListener(l);
/*  694:     */     }
/*  695:     */   }
/*  696:     */   
/*  697:     */   public void removePropertyChangeListener(PropertyChangeListener l)
/*  698:     */   {
/*  699: 820 */     if ((this.m_Support != null) && (l != null)) {
/*  700: 821 */       this.m_Support.removePropertyChangeListener(l);
/*  701:     */     }
/*  702:     */   }
/*  703:     */   
/*  704:     */   protected void applyFilter(final Filter filter)
/*  705:     */   {
/*  706: 832 */     if (this.m_IOThread == null)
/*  707:     */     {
/*  708: 833 */       this.m_IOThread = new Thread()
/*  709:     */       {
/*  710:     */         public void run()
/*  711:     */         {
/*  712:     */           try
/*  713:     */           {
/*  714: 838 */             if (filter != null)
/*  715:     */             {
/*  716: 839 */               PreprocessPanel.this.m_FilterPanel.addToHistory();
/*  717: 841 */               if ((PreprocessPanel.this.m_Log instanceof TaskLogger)) {
/*  718: 842 */                 ((TaskLogger)PreprocessPanel.this.m_Log).taskStarted();
/*  719:     */               }
/*  720: 844 */               PreprocessPanel.this.m_Log.statusMessage("Passing dataset through filter " + filter.getClass().getName());
/*  721:     */               
/*  722: 846 */               String cmd = filter.getClass().getName();
/*  723: 847 */               if ((filter instanceof OptionHandler)) {
/*  724: 848 */                 cmd = cmd + " " + Utils.joinOptions(filter.getOptions());
/*  725:     */               }
/*  726: 851 */               PreprocessPanel.this.m_Log.logMessage("Command: " + cmd);
/*  727: 852 */               int classIndex = PreprocessPanel.this.m_AttVisualizePanel.getColoringIndex();
/*  728: 853 */               if ((classIndex < 0) && ((filter instanceof SupervisedFilter))) {
/*  729: 854 */                 throw new IllegalArgumentException("Class (colour) needs to be set for supervised filter.");
/*  730:     */               }
/*  731: 857 */               Instances copy = new Instances(PreprocessPanel.this.m_Instances);
/*  732: 858 */               copy.setClassIndex(classIndex);
/*  733: 859 */               Filter filterCopy = Filter.makeCopy(filter);
/*  734: 860 */               filterCopy.setInputFormat(copy);
/*  735: 861 */               Instances newInstances = Filter.useFilter(copy, filterCopy);
/*  736: 862 */               if ((newInstances == null) || (newInstances.numAttributes() < 1)) {
/*  737: 863 */                 throw new Exception("Dataset is empty.");
/*  738:     */               }
/*  739: 865 */               PreprocessPanel.this.m_Log.statusMessage("Saving undo information");
/*  740: 866 */               PreprocessPanel.this.addUndoPoint();
/*  741: 867 */               PreprocessPanel.this.m_AttVisualizePanel.setColoringIndex(copy.classIndex());
/*  742: 869 */               if (PreprocessPanel.this.m_Instances.classIndex() < 0) {
/*  743: 870 */                 newInstances.setClassIndex(-1);
/*  744:     */               }
/*  745: 871 */               PreprocessPanel.this.m_Instances = newInstances;
/*  746: 872 */               PreprocessPanel.this.setInstances(PreprocessPanel.this.m_Instances);
/*  747: 873 */               if ((PreprocessPanel.this.m_Log instanceof TaskLogger)) {
/*  748: 874 */                 ((TaskLogger)PreprocessPanel.this.m_Log).taskFinished();
/*  749:     */               }
/*  750:     */             }
/*  751:     */           }
/*  752:     */           catch (Exception ex)
/*  753:     */           {
/*  754: 880 */             if ((PreprocessPanel.this.m_Log instanceof TaskLogger)) {
/*  755: 881 */               ((TaskLogger)PreprocessPanel.this.m_Log).taskFinished();
/*  756:     */             }
/*  757: 884 */             JOptionPane.showMessageDialog(PreprocessPanel.this, "Problem filtering instances:\n" + ex.getMessage(), "Apply Filter", 0);
/*  758:     */             
/*  759:     */ 
/*  760: 887 */             PreprocessPanel.this.m_Log.logMessage("Problem filtering instances: " + ex.getMessage());
/*  761: 888 */             PreprocessPanel.this.m_Log.statusMessage("Problem filtering instances");
/*  762:     */           }
/*  763: 890 */           PreprocessPanel.this.m_IOThread = null;
/*  764:     */         }
/*  765: 892 */       };
/*  766: 893 */       this.m_IOThread.setPriority(1);
/*  767: 894 */       this.m_IOThread.start();
/*  768:     */     }
/*  769:     */     else
/*  770:     */     {
/*  771: 896 */       JOptionPane.showMessageDialog(this, "Can't apply filter at this time,\ncurrently busy with other IO", "Apply Filter", 2);
/*  772:     */     }
/*  773:     */   }
/*  774:     */   
/*  775:     */   public void saveWorkingInstancesToFileQ()
/*  776:     */   {
/*  777: 909 */     if (this.m_IOThread == null)
/*  778:     */     {
/*  779: 910 */       this.m_FileChooser.setCapabilitiesFilter(this.m_FilterEditor.getCapabilitiesFilter());
/*  780:     */       
/*  781: 912 */       this.m_FileChooser.setAcceptAllFileFilterUsed(false);
/*  782: 913 */       int returnVal = this.m_FileChooser.showSaveDialog(this);
/*  783: 914 */       if (returnVal == 0)
/*  784:     */       {
/*  785: 915 */         Instances inst = new Instances(this.m_Instances);
/*  786: 916 */         inst.setClassIndex(this.m_AttVisualizePanel.getColoringIndex());
/*  787: 917 */         saveInstancesToFile(this.m_FileChooser.getSaver(), inst);
/*  788:     */       }
/*  789: 919 */       FileFilter temp = this.m_FileChooser.getFileFilter();
/*  790: 920 */       this.m_FileChooser.setAcceptAllFileFilterUsed(true);
/*  791: 921 */       this.m_FileChooser.setFileFilter(temp);
/*  792:     */     }
/*  793:     */     else
/*  794:     */     {
/*  795: 923 */       JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Save Instances", 2);
/*  796:     */     }
/*  797:     */   }
/*  798:     */   
/*  799:     */   public void saveInstancesToFile(final AbstractFileSaver saver, final Instances inst)
/*  800:     */   {
/*  801: 937 */     if (this.m_IOThread == null)
/*  802:     */     {
/*  803: 938 */       this.m_IOThread = new Thread()
/*  804:     */       {
/*  805:     */         public void run()
/*  806:     */         {
/*  807:     */           try
/*  808:     */           {
/*  809: 942 */             PreprocessPanel.this.m_Log.statusMessage("Saving to file...");
/*  810:     */             
/*  811: 944 */             saver.setInstances(inst);
/*  812: 945 */             saver.writeBatch();
/*  813:     */             
/*  814: 947 */             PreprocessPanel.this.m_Log.statusMessage("OK");
/*  815:     */           }
/*  816:     */           catch (Exception ex)
/*  817:     */           {
/*  818: 949 */             ex.printStackTrace();
/*  819: 950 */             PreprocessPanel.this.m_Log.logMessage(ex.getMessage());
/*  820:     */           }
/*  821: 952 */           PreprocessPanel.this.m_IOThread = null;
/*  822:     */         }
/*  823: 954 */       };
/*  824: 955 */       this.m_IOThread.setPriority(1);
/*  825: 956 */       this.m_IOThread.start();
/*  826:     */     }
/*  827:     */     else
/*  828:     */     {
/*  829: 958 */       JOptionPane.showMessageDialog(this, "Can't save at this time,\ncurrently busy with other IO", "Saving instances", 2);
/*  830:     */     }
/*  831:     */   }
/*  832:     */   
/*  833:     */   public void setInstancesFromFileQ()
/*  834:     */   {
/*  835: 971 */     if (this.m_IOThread == null)
/*  836:     */     {
/*  837: 972 */       int returnVal = this.m_FileChooser.showOpenDialog(this);
/*  838: 973 */       if (returnVal == 0)
/*  839:     */       {
/*  840:     */         try
/*  841:     */         {
/*  842: 975 */           addUndoPoint();
/*  843:     */         }
/*  844:     */         catch (Exception ignored) {}
/*  845: 980 */         if (this.m_FileChooser.getLoader() == null)
/*  846:     */         {
/*  847: 981 */           JOptionPane.showMessageDialog(this, "Cannot determine file loader automatically, please choose one.", "Load Instances", 0);
/*  848:     */           
/*  849:     */ 
/*  850: 984 */           converterQuery(this.m_FileChooser.getSelectedFile());
/*  851:     */         }
/*  852:     */         else
/*  853:     */         {
/*  854: 986 */           setInstancesFromFile(this.m_FileChooser.getLoader());
/*  855:     */         }
/*  856:     */       }
/*  857:     */     }
/*  858:     */     else
/*  859:     */     {
/*  860: 991 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/*  861:     */     }
/*  862:     */   }
/*  863:     */   
/*  864:     */   public void setInstancesFromDBQ(String url, String user, String pw, String query)
/*  865:     */   {
/*  866:1010 */     setInstancesFromDBQ(url, user, pw, query, false);
/*  867:     */   }
/*  868:     */   
/*  869:     */   public void setInstancesFromDBQ(String url, String user, String pw, String query, boolean sparse)
/*  870:     */   {
/*  871:1027 */     if (this.m_IOThread == null) {
/*  872:     */       try
/*  873:     */       {
/*  874:1029 */         InstanceQuery InstQ = new InstanceQuery();
/*  875:1030 */         InstQ.setDatabaseURL(url);
/*  876:1031 */         InstQ.setUsername(user);
/*  877:1032 */         InstQ.setPassword(pw);
/*  878:1033 */         InstQ.setQuery(query);
/*  879:1034 */         InstQ.setSparseData(sparse);
/*  880:1037 */         if (InstQ.isConnected()) {
/*  881:1038 */           InstQ.disconnectFromDatabase();
/*  882:     */         }
/*  883:1040 */         InstQ.connectToDatabase();
/*  884:     */         try
/*  885:     */         {
/*  886:1042 */           addUndoPoint();
/*  887:     */         }
/*  888:     */         catch (Exception ignored) {}
/*  889:1045 */         setInstancesFromDB(InstQ);
/*  890:     */       }
/*  891:     */       catch (Exception ex)
/*  892:     */       {
/*  893:1047 */         JOptionPane.showMessageDialog(this, "Problem connecting to database:\n" + ex.getMessage(), "Load Instances", 0);
/*  894:     */       }
/*  895:     */     } else {
/*  896:1052 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/*  897:     */     }
/*  898:     */   }
/*  899:     */   
/*  900:     */   public void setInstancesFromURLQ()
/*  901:     */   {
/*  902:1065 */     if (this.m_IOThread == null) {
/*  903:     */       try
/*  904:     */       {
/*  905:1067 */         String urlName = (String)JOptionPane.showInputDialog(this, "Enter the source URL", "Load Instances", 3, null, null, this.m_LastURL);
/*  906:1071 */         if (urlName != null)
/*  907:     */         {
/*  908:1072 */           this.m_LastURL = urlName;
/*  909:1073 */           URL url = new URL(urlName);
/*  910:     */           try
/*  911:     */           {
/*  912:1075 */             addUndoPoint();
/*  913:     */           }
/*  914:     */           catch (Exception ignored) {}
/*  915:1078 */           setInstancesFromURL(url);
/*  916:     */         }
/*  917:     */       }
/*  918:     */       catch (Exception ex)
/*  919:     */       {
/*  920:1081 */         ex.printStackTrace();
/*  921:1082 */         JOptionPane.showMessageDialog(this, "Problem with URL:\n" + ex.getMessage(), "Load Instances", 0);
/*  922:     */       }
/*  923:     */     } else {
/*  924:1087 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/*  925:     */     }
/*  926:     */   }
/*  927:     */   
/*  928:     */   public void generateInstances()
/*  929:     */   {
/*  930:1097 */     if (this.m_IOThread == null)
/*  931:     */     {
/*  932:1098 */       this.m_IOThread = new Thread()
/*  933:     */       {
/*  934:     */         public void run()
/*  935:     */         {
/*  936:     */           try
/*  937:     */           {
/*  938:1103 */             final DataGeneratorPanel generatorPanel = new DataGeneratorPanel();
/*  939:1104 */             final JDialog dialog = new JDialog();
/*  940:1105 */             JButton generateButton = new JButton("Generate");
/*  941:1106 */             final JCheckBox showOutputCheckBox = new JCheckBox("Show generated data as text, incl. comments");
/*  942:     */             
/*  943:     */ 
/*  944:1109 */             showOutputCheckBox.setMnemonic('S');
/*  945:1110 */             generatorPanel.setLog(PreprocessPanel.this.m_Log);
/*  946:1111 */             generatorPanel.setGenerator(PreprocessPanel.this.m_DataGenerator);
/*  947:1112 */             generatorPanel.setPreferredSize(new Dimension(300, (int)generatorPanel.getPreferredSize().getHeight()));
/*  948:     */             
/*  949:1114 */             generateButton.setMnemonic('G');
/*  950:1115 */             generateButton.setToolTipText("Generates the dataset according the settings.");
/*  951:     */             
/*  952:1117 */             generateButton.addActionListener(new ActionListener()
/*  953:     */             {
/*  954:     */               public void actionPerformed(ActionEvent evt)
/*  955:     */               {
/*  956:1120 */                 generatorPanel.execute();
/*  957:1121 */                 boolean generated = generatorPanel.getInstances() != null;
/*  958:1122 */                 if (generated) {
/*  959:1123 */                   PreprocessPanel.this.setInstances(generatorPanel.getInstances());
/*  960:     */                 }
/*  961:1126 */                 dialog.dispose();
/*  962:     */                 
/*  963:     */ 
/*  964:1129 */                 PreprocessPanel.this.m_DataGenerator = generatorPanel.getGenerator();
/*  965:1132 */                 if ((generated) && (showOutputCheckBox.isSelected())) {
/*  966:1133 */                   PreprocessPanel.this.showGeneratedInstances(generatorPanel.getOutput());
/*  967:     */                 }
/*  968:     */               }
/*  969:1135 */             });
/*  970:1136 */             dialog.setTitle("DataGenerator");
/*  971:1137 */             dialog.getContentPane().add(generatorPanel, "Center");
/*  972:1138 */             dialog.getContentPane().add(generateButton, "East");
/*  973:1139 */             dialog.getContentPane().add(showOutputCheckBox, "South");
/*  974:1140 */             dialog.pack();
/*  975:     */             
/*  976:     */ 
/*  977:1143 */             dialog.setVisible(true);
/*  978:     */           }
/*  979:     */           catch (Exception ex)
/*  980:     */           {
/*  981:1145 */             ex.printStackTrace();
/*  982:1146 */             PreprocessPanel.this.m_Log.logMessage(ex.getMessage());
/*  983:     */           }
/*  984:1148 */           PreprocessPanel.this.m_IOThread = null;
/*  985:     */         }
/*  986:1150 */       };
/*  987:1151 */       this.m_IOThread.setPriority(1);
/*  988:1152 */       this.m_IOThread.start();
/*  989:     */     }
/*  990:     */     else
/*  991:     */     {
/*  992:1154 */       JOptionPane.showMessageDialog(this, "Can't generate data at this time,\ncurrently busy with other IO", "Generate Data", 2);
/*  993:     */     }
/*  994:     */   }
/*  995:     */   
/*  996:     */   protected void showGeneratedInstances(String data)
/*  997:     */   {
/*  998:1166 */     final JDialog dialog = new JDialog();
/*  999:1167 */     JButton saveButton = new JButton("Save");
/* 1000:1168 */     JButton closeButton = new JButton("Close");
/* 1001:1169 */     final JTextArea textData = new JTextArea(data);
/* 1002:1170 */     JPanel panel = new JPanel();
/* 1003:1171 */     panel.setLayout(new FlowLayout(2));
/* 1004:1172 */     textData.setEditable(false);
/* 1005:1173 */     textData.setFont(new Font("Monospaced", 0, textData.getFont().getSize()));
/* 1006:     */     
/* 1007:     */ 
/* 1008:1176 */     saveButton.setMnemonic('S');
/* 1009:1177 */     saveButton.setToolTipText("Saves the output to a file");
/* 1010:1178 */     saveButton.addActionListener(new ActionListener()
/* 1011:     */     {
/* 1012:     */       public void actionPerformed(ActionEvent evt)
/* 1013:     */       {
/* 1014:1180 */         JFileChooser filechooser = new JFileChooser();
/* 1015:1181 */         int result = filechooser.showSaveDialog(dialog);
/* 1016:1182 */         if (result == 0)
/* 1017:     */         {
/* 1018:     */           try
/* 1019:     */           {
/* 1020:1184 */             BufferedWriter writer = new BufferedWriter(new FileWriter(filechooser.getSelectedFile()));
/* 1021:     */             
/* 1022:1186 */             writer.write(textData.getText());
/* 1023:1187 */             writer.flush();
/* 1024:1188 */             writer.close();
/* 1025:1189 */             JOptionPane.showMessageDialog(dialog, "Output successfully saved to file '" + filechooser.getSelectedFile() + "'!", "Information", 1);
/* 1026:     */           }
/* 1027:     */           catch (Exception e)
/* 1028:     */           {
/* 1029:1195 */             e.printStackTrace();
/* 1030:     */           }
/* 1031:1197 */           dialog.dispose();
/* 1032:     */         }
/* 1033:     */       }
/* 1034:1200 */     });
/* 1035:1201 */     closeButton.setMnemonic('C');
/* 1036:1202 */     closeButton.setToolTipText("Closes the dialog");
/* 1037:1203 */     closeButton.addActionListener(new ActionListener()
/* 1038:     */     {
/* 1039:     */       public void actionPerformed(ActionEvent evt)
/* 1040:     */       {
/* 1041:1205 */         dialog.dispose();
/* 1042:     */       }
/* 1043:1207 */     });
/* 1044:1208 */     panel.add(saveButton);
/* 1045:1209 */     panel.add(closeButton);
/* 1046:1210 */     dialog.setTitle("Generated Instances (incl. comments)");
/* 1047:1211 */     dialog.getContentPane().add(new JScrollPane(textData), "Center");
/* 1048:1212 */     dialog.getContentPane().add(panel, "South");
/* 1049:1213 */     dialog.pack();
/* 1050:     */     
/* 1051:     */ 
/* 1052:1216 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/* 1053:1217 */     int width = dialog.getWidth() > screen.getWidth() * 0.8D ? (int)(screen.getWidth() * 0.8D) : dialog.getWidth();
/* 1054:     */     
/* 1055:     */ 
/* 1056:1220 */     int height = dialog.getHeight() > screen.getHeight() * 0.8D ? (int)(screen.getHeight() * 0.8D) : dialog.getHeight();
/* 1057:     */     
/* 1058:     */ 
/* 1059:1223 */     dialog.setSize(width, height);
/* 1060:     */     
/* 1061:     */ 
/* 1062:1226 */     dialog.setVisible(true);
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   private void converterQuery(final File f)
/* 1066:     */   {
/* 1067:1235 */     final GenericObjectEditor convEd = new GenericObjectEditor(true);
/* 1068:     */     try
/* 1069:     */     {
/* 1070:1238 */       convEd.setClassType(Loader.class);
/* 1071:1239 */       convEd.setValue(new CSVLoader());
/* 1072:1240 */       ((GenericObjectEditor.GOEPanel)convEd.getCustomEditor()).addOkListener(new ActionListener()
/* 1073:     */       {
/* 1074:     */         public void actionPerformed(ActionEvent e)
/* 1075:     */         {
/* 1076:1243 */           PreprocessPanel.this.tryConverter((Loader)convEd.getValue(), f);
/* 1077:     */         }
/* 1078:     */       });
/* 1079:     */     }
/* 1080:     */     catch (Exception ex) {}
/* 1081:     */     PropertyDialog pd;
/* 1082:     */     PropertyDialog pd;
/* 1083:1250 */     if (PropertyDialog.getParentDialog(this) != null) {
/* 1084:1251 */       pd = new PropertyDialog(PropertyDialog.getParentDialog(this), convEd, 100, 100);
/* 1085:     */     } else {
/* 1086:1255 */       pd = new PropertyDialog(PropertyDialog.getParentFrame(this), convEd, 100, 100);
/* 1087:     */     }
/* 1088:1258 */     pd.setVisible(true);
/* 1089:     */   }
/* 1090:     */   
/* 1091:     */   private void tryConverter(final Loader cnv, final File f)
/* 1092:     */   {
/* 1093:1269 */     if (this.m_IOThread == null)
/* 1094:     */     {
/* 1095:1270 */       this.m_IOThread = new Thread()
/* 1096:     */       {
/* 1097:     */         public void run()
/* 1098:     */         {
/* 1099:     */           try
/* 1100:     */           {
/* 1101:1274 */             cnv.setSource(f);
/* 1102:1275 */             Instances inst = cnv.getDataSet();
/* 1103:1276 */             PreprocessPanel.this.setInstances(inst);
/* 1104:     */           }
/* 1105:     */           catch (Exception ex)
/* 1106:     */           {
/* 1107:1278 */             PreprocessPanel.this.m_Log.statusMessage(cnv.getClass().getName() + " failed to load " + f.getName());
/* 1108:     */             
/* 1109:1280 */             JOptionPane.showMessageDialog(PreprocessPanel.this, cnv.getClass().getName() + " failed to load '" + f.getName() + "'.\n" + "Reason:\n" + ex.getMessage(), "Convert File", 0);
/* 1110:     */             
/* 1111:     */ 
/* 1112:     */ 
/* 1113:     */ 
/* 1114:     */ 
/* 1115:     */ 
/* 1116:1287 */             PreprocessPanel.this.m_IOThread = null;
/* 1117:1288 */             PreprocessPanel.this.converterQuery(f);
/* 1118:     */           }
/* 1119:1290 */           PreprocessPanel.this.m_IOThread = null;
/* 1120:     */         }
/* 1121:1292 */       };
/* 1122:1293 */       this.m_IOThread.setPriority(1);
/* 1123:1294 */       this.m_IOThread.start();
/* 1124:     */     }
/* 1125:     */   }
/* 1126:     */   
/* 1127:     */   public void setInstancesFromFile(final AbstractFileLoader loader)
/* 1128:     */   {
/* 1129:1307 */     if (this.m_IOThread == null)
/* 1130:     */     {
/* 1131:1308 */       this.m_IOThread = new Thread()
/* 1132:     */       {
/* 1133:     */         public void run()
/* 1134:     */         {
/* 1135:     */           try
/* 1136:     */           {
/* 1137:1312 */             PreprocessPanel.this.m_Log.statusMessage("Reading from file...");
/* 1138:1313 */             Instances inst = loader.getDataSet();
/* 1139:1314 */             PreprocessPanel.this.setInstances(inst);
/* 1140:     */           }
/* 1141:     */           catch (Exception ex)
/* 1142:     */           {
/* 1143:1316 */             PreprocessPanel.this.m_Log.statusMessage("File '" + loader.retrieveFile() + "' not recognised as an '" + loader.getFileDescription() + "' file.");
/* 1144:     */             
/* 1145:     */ 
/* 1146:1319 */             PreprocessPanel.this.m_IOThread = null;
/* 1147:1320 */             if (JOptionPane.showOptionDialog(PreprocessPanel.this, "File '" + loader.retrieveFile() + "' not recognised as an '" + loader.getFileDescription() + "' file.\n" + "Reason:\n" + ex.getMessage(), "Load Instances", 0, 0, null, new String[] { "OK", "Use Converter" }, null) == 1) {
/* 1148:1328 */               PreprocessPanel.this.converterQuery(loader.retrieveFile());
/* 1149:     */             }
/* 1150:     */           }
/* 1151:1331 */           PreprocessPanel.this.m_IOThread = null;
/* 1152:     */         }
/* 1153:1333 */       };
/* 1154:1334 */       this.m_IOThread.setPriority(1);
/* 1155:1335 */       this.m_IOThread.start();
/* 1156:     */     }
/* 1157:     */     else
/* 1158:     */     {
/* 1159:1337 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/* 1160:     */     }
/* 1161:     */   }
/* 1162:     */   
/* 1163:     */   public void setInstancesFromDB(final InstanceQuery iq)
/* 1164:     */   {
/* 1165:1350 */     if (this.m_IOThread == null)
/* 1166:     */     {
/* 1167:1351 */       this.m_IOThread = new Thread()
/* 1168:     */       {
/* 1169:     */         public void run()
/* 1170:     */         {
/* 1171:     */           try
/* 1172:     */           {
/* 1173:1356 */             PreprocessPanel.this.m_Log.statusMessage("Reading from database...");
/* 1174:1357 */             final Instances i = iq.retrieveInstances();
/* 1175:1358 */             SwingUtilities.invokeAndWait(new Runnable()
/* 1176:     */             {
/* 1177:     */               public void run()
/* 1178:     */               {
/* 1179:1360 */                 PreprocessPanel.this.setInstances(new Instances(i));
/* 1180:     */               }
/* 1181:1362 */             });
/* 1182:1363 */             iq.disconnectFromDatabase();
/* 1183:     */           }
/* 1184:     */           catch (Exception ex)
/* 1185:     */           {
/* 1186:1365 */             PreprocessPanel.this.m_Log.statusMessage("Problem executing DB query " + PreprocessPanel.this.m_SQLQ);
/* 1187:1366 */             JOptionPane.showMessageDialog(PreprocessPanel.this, "Couldn't read from database:\n" + ex.getMessage(), "Load Instances", 0);
/* 1188:     */           }
/* 1189:1371 */           PreprocessPanel.this.m_IOThread = null;
/* 1190:     */         }
/* 1191:1374 */       };
/* 1192:1375 */       this.m_IOThread.setPriority(1);
/* 1193:1376 */       this.m_IOThread.start();
/* 1194:     */     }
/* 1195:     */     else
/* 1196:     */     {
/* 1197:1378 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/* 1198:     */     }
/* 1199:     */   }
/* 1200:     */   
/* 1201:     */   public void setInstancesFromURL(final URL u)
/* 1202:     */   {
/* 1203:1391 */     if (this.m_IOThread == null)
/* 1204:     */     {
/* 1205:1392 */       this.m_IOThread = new Thread()
/* 1206:     */       {
/* 1207:     */         public void run()
/* 1208:     */         {
/* 1209:     */           try
/* 1210:     */           {
/* 1211:1397 */             PreprocessPanel.this.m_Log.statusMessage("Reading from URL...");
/* 1212:1398 */             AbstractFileLoader loader = ConverterUtils.getURLLoaderForFile(u.toString());
/* 1213:1400 */             if (loader == null) {
/* 1214:1401 */               throw new Exception("No suitable URLSourcedLoader found for URL!\n" + u);
/* 1215:     */             }
/* 1216:1403 */             ((URLSourcedLoader)loader).setURL(u.toString());
/* 1217:1404 */             PreprocessPanel.this.setInstances(loader.getDataSet());
/* 1218:     */           }
/* 1219:     */           catch (Exception ex)
/* 1220:     */           {
/* 1221:1406 */             ex.printStackTrace();
/* 1222:1407 */             PreprocessPanel.this.m_Log.statusMessage("Problem reading " + u);
/* 1223:1408 */             JOptionPane.showMessageDialog(PreprocessPanel.this, "Couldn't read from URL:\n" + u + "\n" + ex.getMessage(), "Load Instances", 0);
/* 1224:     */           }
/* 1225:1413 */           PreprocessPanel.this.m_IOThread = null;
/* 1226:     */         }
/* 1227:1415 */       };
/* 1228:1416 */       this.m_IOThread.setPriority(1);
/* 1229:1417 */       this.m_IOThread.start();
/* 1230:     */     }
/* 1231:     */     else
/* 1232:     */     {
/* 1233:1419 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/* 1234:     */     }
/* 1235:     */   }
/* 1236:     */   
/* 1237:     */   public void addUndoPoint()
/* 1238:     */     throws Exception
/* 1239:     */   {
/* 1240:1431 */     if (!ExplorerDefaults.get("enableUndo", "true").equalsIgnoreCase("true")) {
/* 1241:1432 */       return;
/* 1242:     */     }
/* 1243:1435 */     if (getMainApplication() != null)
/* 1244:     */     {
/* 1245:1436 */       boolean undoEnabled = ((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.ENABLE_UNDO_KEY, PreprocessDefaults.ENABLE_UNDO, Environment.getSystemWide())).booleanValue();
/* 1246:1440 */       if (!undoEnabled) {
/* 1247:1441 */         return;
/* 1248:     */       }
/* 1249:     */     }
/* 1250:1445 */     if (this.m_Instances != null)
/* 1251:     */     {
/* 1252:1447 */       File tempFile = File.createTempFile("weka", SerializedInstancesLoader.FILE_EXTENSION);
/* 1253:     */       
/* 1254:1449 */       tempFile.deleteOnExit();
/* 1255:1450 */       boolean nonDefaultTmpDir = false;
/* 1256:1451 */       String dir = "";
/* 1257:1452 */       if (getMainApplication() != null)
/* 1258:     */       {
/* 1259:1453 */         dir = ((File)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.UNDO_DIR_KEY, PreprocessDefaults.UNDO_DIR)).toString();
/* 1260:     */         
/* 1261:     */ 
/* 1262:     */ 
/* 1263:     */ 
/* 1264:1458 */         Environment.getSystemWide();
/* 1265:1458 */         if (Environment.containsEnvVariables(dir)) {
/* 1266:1459 */           dir = Environment.getSystemWide().substitute(dir);
/* 1267:     */         }
/* 1268:1461 */         if (!dir.equals(PreprocessDefaults.UNDO_DIR)) {
/* 1269:1462 */           nonDefaultTmpDir = true;
/* 1270:     */         }
/* 1271:     */       }
/* 1272:1464 */       else if (!ExplorerDefaults.get("undoDirectory", "%t").equalsIgnoreCase("%t"))
/* 1273:     */       {
/* 1274:1466 */         nonDefaultTmpDir = true;
/* 1275:1467 */         dir = ExplorerDefaults.get("undoDirectory", "%t");
/* 1276:     */       }
/* 1277:1470 */       if (nonDefaultTmpDir)
/* 1278:     */       {
/* 1279:1471 */         File undoDir = new File(dir);
/* 1280:1472 */         if (undoDir.exists())
/* 1281:     */         {
/* 1282:1473 */           String fileName = tempFile.getName();
/* 1283:1474 */           File newFile = new File(dir + File.separator + fileName);
/* 1284:1475 */           if (undoDir.canWrite())
/* 1285:     */           {
/* 1286:1476 */             newFile.deleteOnExit();
/* 1287:1477 */             tempFile = newFile;
/* 1288:     */           }
/* 1289:     */           else
/* 1290:     */           {
/* 1291:1479 */             System.err.println("Explorer: it doesn't look like we have permission to write to the user-specified undo directory '" + dir + "'");
/* 1292:     */           }
/* 1293:     */         }
/* 1294:     */         else
/* 1295:     */         {
/* 1296:1485 */           System.err.println("Explorer: user-specified undo directory '" + dir + "' does not exist!");
/* 1297:     */         }
/* 1298:     */       }
/* 1299:1490 */       ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
/* 1300:     */       
/* 1301:     */ 
/* 1302:     */ 
/* 1303:1494 */       oos.writeObject(this.m_Instances);
/* 1304:1495 */       oos.flush();
/* 1305:1496 */       oos.close();
/* 1306:1499 */       if (this.m_tempUndoFiles[this.m_tempUndoIndex] != null) {
/* 1307:1501 */         this.m_tempUndoFiles[this.m_tempUndoIndex].delete();
/* 1308:     */       }
/* 1309:1503 */       this.m_tempUndoFiles[this.m_tempUndoIndex] = tempFile;
/* 1310:1504 */       if (++this.m_tempUndoIndex >= this.m_tempUndoFiles.length) {
/* 1311:1506 */         this.m_tempUndoIndex = 0;
/* 1312:     */       }
/* 1313:1509 */       this.m_UndoBut.setEnabled(true);
/* 1314:     */     }
/* 1315:     */   }
/* 1316:     */   
/* 1317:     */   public void undo()
/* 1318:     */   {
/* 1319:1518 */     if (--this.m_tempUndoIndex < 0) {
/* 1320:1520 */       this.m_tempUndoIndex = (this.m_tempUndoFiles.length - 1);
/* 1321:     */     }
/* 1322:1523 */     if (this.m_tempUndoFiles[this.m_tempUndoIndex] != null)
/* 1323:     */     {
/* 1324:1525 */       AbstractFileLoader loader = ConverterUtils.getLoaderForFile(this.m_tempUndoFiles[this.m_tempUndoIndex]);
/* 1325:     */       try
/* 1326:     */       {
/* 1327:1528 */         loader.setFile(this.m_tempUndoFiles[this.m_tempUndoIndex]);
/* 1328:1529 */         setInstancesFromFile(loader);
/* 1329:     */       }
/* 1330:     */       catch (Exception e)
/* 1331:     */       {
/* 1332:1531 */         e.printStackTrace();
/* 1333:1532 */         this.m_Log.logMessage(e.toString());
/* 1334:1533 */         JOptionPane.showMessageDialog(this, "Cannot perform undo operation!\n" + e.toString(), "Undo", 0);
/* 1335:     */       }
/* 1336:1539 */       this.m_tempUndoFiles[this.m_tempUndoIndex] = null;
/* 1337:     */     }
/* 1338:1543 */     int temp = this.m_tempUndoIndex - 1;
/* 1339:1544 */     if (temp < 0) {
/* 1340:1545 */       temp = this.m_tempUndoFiles.length - 1;
/* 1341:     */     }
/* 1342:1547 */     this.m_UndoBut.setEnabled(this.m_tempUndoFiles[temp] != null);
/* 1343:     */   }
/* 1344:     */   
/* 1345:     */   public void edit()
/* 1346:     */   {
/* 1347:1559 */     int classIndex = this.m_AttVisualizePanel.getColoringIndex();
/* 1348:1560 */     Instances copy = new Instances(this.m_Instances);
/* 1349:1561 */     copy.setClassIndex(classIndex);
/* 1350:1562 */     ViewerDialog dialog = new ViewerDialog(null);
/* 1351:1563 */     int result = dialog.showDialog(copy);
/* 1352:1564 */     if (result == 0)
/* 1353:     */     {
/* 1354:     */       try
/* 1355:     */       {
/* 1356:1566 */         addUndoPoint();
/* 1357:     */       }
/* 1358:     */       catch (Exception e)
/* 1359:     */       {
/* 1360:1568 */         e.printStackTrace();
/* 1361:     */       }
/* 1362:1571 */       Instances newInstances = dialog.getInstances();
/* 1363:1572 */       if (this.m_Instances.classIndex() < 0) {
/* 1364:1573 */         newInstances.setClassIndex(-1);
/* 1365:     */       }
/* 1366:1574 */       setInstances(newInstances);
/* 1367:     */     }
/* 1368:     */   }
/* 1369:     */   
/* 1370:     */   public void setExplorer(Explorer parent)
/* 1371:     */   {
/* 1372:1585 */     this.m_Explorer = parent;
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public Explorer getExplorer()
/* 1376:     */   {
/* 1377:1594 */     return this.m_Explorer;
/* 1378:     */   }
/* 1379:     */   
/* 1380:     */   protected void updateCapabilitiesFilter(Capabilities filter)
/* 1381:     */   {
/* 1382:1606 */     if (filter == null)
/* 1383:     */     {
/* 1384:1607 */       this.m_FilterEditor.setCapabilitiesFilter(new Capabilities(null)); return;
/* 1385:     */     }
/* 1386:     */     Instances tempInst;
/* 1387:     */     Instances tempInst;
/* 1388:1611 */     if (!ExplorerDefaults.getInitGenericObjectEditorFilter()) {
/* 1389:1612 */       tempInst = new Instances(this.m_Instances, 0);
/* 1390:     */     } else {
/* 1391:1614 */       tempInst = new Instances(this.m_Instances);
/* 1392:     */     }
/* 1393:1615 */     tempInst.setClassIndex(this.m_AttVisualizePanel.getColorBox().getSelectedIndex() - 1);
/* 1394:     */     Capabilities filterClass;
/* 1395:     */     try
/* 1396:     */     {
/* 1397:1619 */       filterClass = Capabilities.forInstances(tempInst);
/* 1398:     */     }
/* 1399:     */     catch (Exception e)
/* 1400:     */     {
/* 1401:1621 */       filterClass = new Capabilities(null);
/* 1402:     */     }
/* 1403:1625 */     this.m_FilterEditor.setCapabilitiesFilter(filterClass);
/* 1404:     */     
/* 1405:     */ 
/* 1406:1628 */     this.m_ApplyFilterBut.setEnabled(true);
/* 1407:1629 */     Capabilities currentCapabilitiesFilter = this.m_FilterEditor.getCapabilitiesFilter();
/* 1408:     */     
/* 1409:1631 */     Filter currentFilter = (Filter)this.m_FilterEditor.getValue();
/* 1410:1632 */     Capabilities currentFilterCapabilities = null;
/* 1411:1633 */     if ((currentFilter != null) && (currentCapabilitiesFilter != null) && ((currentFilter instanceof CapabilitiesHandler)))
/* 1412:     */     {
/* 1413:1635 */       currentFilterCapabilities = currentFilter.getCapabilities();
/* 1414:1638 */       if ((!currentFilterCapabilities.supportsMaybe(currentCapabilitiesFilter)) && (!currentFilterCapabilities.supports(currentCapabilitiesFilter))) {
/* 1415:     */         try
/* 1416:     */         {
/* 1417:1641 */           currentFilter.setInputFormat(getInstances());
/* 1418:     */         }
/* 1419:     */         catch (Exception ex)
/* 1420:     */         {
/* 1421:1643 */           this.m_ApplyFilterBut.setEnabled(false);
/* 1422:     */         }
/* 1423:     */       }
/* 1424:     */     }
/* 1425:     */   }
/* 1426:     */   
/* 1427:     */   public void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent e)
/* 1428:     */   {
/* 1429:1655 */     if (e.getFilter() == null) {
/* 1430:1656 */       updateCapabilitiesFilter(null);
/* 1431:     */     } else {
/* 1432:1658 */       updateCapabilitiesFilter((Capabilities)e.getFilter().clone());
/* 1433:     */     }
/* 1434:     */   }
/* 1435:     */   
/* 1436:     */   public String getTabTitle()
/* 1437:     */   {
/* 1438:1667 */     return "Preprocess";
/* 1439:     */   }
/* 1440:     */   
/* 1441:     */   public String getTabTitleToolTip()
/* 1442:     */   {
/* 1443:1676 */     return "Open/Edit/Save instances";
/* 1444:     */   }
/* 1445:     */   
/* 1446:     */   public Defaults getDefaultSettings()
/* 1447:     */   {
/* 1448:1681 */     return new PreprocessDefaults();
/* 1449:     */   }
/* 1450:     */   
/* 1451:     */   public void setActive(boolean active)
/* 1452:     */   {
/* 1453:1686 */     super.setActive(active);
/* 1454:1687 */     if (this.m_isActive) {
/* 1455:1688 */       updateSettings();
/* 1456:     */     }
/* 1457:     */   }
/* 1458:     */   
/* 1459:     */   public void settingsChanged()
/* 1460:     */   {
/* 1461:1694 */     updateSettings();
/* 1462:     */   }
/* 1463:     */   
/* 1464:     */   protected void updateSettings()
/* 1465:     */   {
/* 1466:1698 */     File initialDir = (File)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.INITIAL_DIR_KEY, PreprocessDefaults.INITIAL_DIR, Environment.getSystemWide());
/* 1467:     */     
/* 1468:     */ 
/* 1469:     */ 
/* 1470:1702 */     Environment.getSystemWide();
/* 1471:1702 */     if (Environment.containsEnvVariables(initialDir.toString()))
/* 1472:     */     {
/* 1473:1703 */       String initDir = initialDir.toString();
/* 1474:     */       try
/* 1475:     */       {
/* 1476:1705 */         initDir = Environment.getSystemWide().substitute(initDir);
/* 1477:1706 */         initialDir = new File(initDir);
/* 1478:     */       }
/* 1479:     */       catch (Exception ex) {}
/* 1480:     */     }
/* 1481:1710 */     this.m_FileChooser.setCurrentDirectory(initialDir);
/* 1482:1712 */     if (!this.m_initialSettingsSet)
/* 1483:     */     {
/* 1484:1714 */       Filter toUse = (Filter)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.FILTER_KEY, PreprocessDefaults.FILTER, Environment.getSystemWide());
/* 1485:     */       
/* 1486:     */ 
/* 1487:     */ 
/* 1488:1718 */       this.m_FilterEditor.setValue(toUse);
/* 1489:     */       
/* 1490:1720 */       this.m_UndoBut.setEnabled(false);
/* 1491:1721 */       this.m_initialSettingsSet = true;
/* 1492:     */     }
/* 1493:1730 */     boolean sendToAll = ((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL_KEY, Boolean.valueOf(PreprocessDefaults.ALWAYS_SEND_INSTANCES_TO_ALL), Environment.getSystemWide())).booleanValue();
/* 1494:1736 */     if ((sendToAll) && (getInstances() != null))
/* 1495:     */     {
/* 1496:1740 */       List<Perspective> visiblePerspectives = getMainApplication().getPerspectiveManager().getVisiblePerspectives();
/* 1497:1742 */       for (Perspective p : visiblePerspectives)
/* 1498:     */       {
/* 1499:1743 */         if ((!p.okToBeActive()) && (p.acceptsInstances())) {
/* 1500:1744 */           p.setInstances(getInstances());
/* 1501:     */         }
/* 1502:1747 */         if (p.okToBeActive()) {
/* 1503:1748 */           getMainApplication().getPerspectiveManager().setEnablePerspectiveTab(p.getPerspectiveID(), true);
/* 1504:     */         }
/* 1505:     */       }
/* 1506:     */     }
/* 1507:1754 */     if (this.m_sendToPerspective != null) {
/* 1508:1755 */       this.m_sendToPerspective.setEnabled((!sendToAll) && (getInstances() != null));
/* 1509:     */     }
/* 1510:     */   }
/* 1511:     */   
/* 1512:     */   public List<JMenu> getMenus()
/* 1513:     */   {
/* 1514:1761 */     return this.m_menus;
/* 1515:     */   }
/* 1516:     */   
/* 1517:     */   public static class PreprocessDefaults
/* 1518:     */     extends Defaults
/* 1519:     */   {
/* 1520:     */     public static final String ID = "weka.gui.explorer.preprocesspanel";
/* 1521:1767 */     public static final Settings.SettingKey INITIAL_DIR_KEY = new Settings.SettingKey("weka.gui.explorer.preprocesspanel.initialDir", "Initial directory for opening datasets", "");
/* 1522:1770 */     public static final File INITIAL_DIR = new File("${user.dir}");
/* 1523:1772 */     public static final Settings.SettingKey UNDO_DIR_KEY = new Settings.SettingKey("weka.gui.explorer.preprocesspanel.undoDir", "Directory for storing undo files", "");
/* 1524:1775 */     public static final File UNDO_DIR = new File("${java.io.tmpdir}");
/* 1525:1777 */     public static final Settings.SettingKey FILTER_KEY = new Settings.SettingKey("weka.gui.explorer.preprocesspanel.initialFilter", "Initial filter", "");
/* 1526:1779 */     public static final Filter FILTER = new AllFilter();
/* 1527:1781 */     public static final Settings.SettingKey ENABLE_UNDO_KEY = new Settings.SettingKey("weka.gui.explorer.preprocesspanel.enableUndo", "Enable undo", "");
/* 1528:1783 */     public static final Boolean ENABLE_UNDO = Boolean.valueOf(true);
/* 1529:1785 */     public static final Settings.SettingKey ALWAYS_SEND_INSTANCES_TO_ALL_KEY = new Settings.SettingKey("weka.gui.explorer.preprocesspanel.alwaysSendInstancesToAllPerspectives", "Always send instances to all perspectives", "");
/* 1530:1788 */     public static boolean ALWAYS_SEND_INSTANCES_TO_ALL = true;
/* 1531:     */     
/* 1532:     */     public PreprocessDefaults()
/* 1533:     */     {
/* 1534:1791 */       super();
/* 1535:     */       
/* 1536:1793 */       INITIAL_DIR_KEY.setMetadataElement("java.io.File.fileSelectionMode", "1");
/* 1537:     */       
/* 1538:1795 */       INITIAL_DIR_KEY.setMetadataElement("java.io.File.dialogType", "0");
/* 1539:     */       
/* 1540:1797 */       UNDO_DIR_KEY.setMetadataElement("java.io.File.fileSelectionMode", "1");
/* 1541:     */       
/* 1542:1799 */       UNDO_DIR_KEY.setMetadataElement("java.io.File.dialogType", "1");
/* 1543:     */       
/* 1544:1801 */       this.m_defaults.put(INITIAL_DIR_KEY, INITIAL_DIR);
/* 1545:1802 */       this.m_defaults.put(UNDO_DIR_KEY, UNDO_DIR);
/* 1546:1803 */       this.m_defaults.put(FILTER_KEY, FILTER);
/* 1547:1804 */       this.m_defaults.put(ENABLE_UNDO_KEY, ENABLE_UNDO);
/* 1548:1805 */       this.m_defaults.put(ALWAYS_SEND_INSTANCES_TO_ALL_KEY, Boolean.valueOf(ALWAYS_SEND_INSTANCES_TO_ALL));
/* 1549:     */     }
/* 1550:     */   }
/* 1551:     */   
/* 1552:     */   public static void main(String[] args)
/* 1553:     */   {
/* 1554:     */     try
/* 1555:     */     {
/* 1556:1818 */       JFrame jf = new JFrame("Weka Explorer: Preprocess");
/* 1557:1819 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1558:1820 */       PreprocessPanel sp = new PreprocessPanel();
/* 1559:1821 */       jf.getContentPane().add(sp, "Center");
/* 1560:1822 */       LogPanel lp = new LogPanel();
/* 1561:1823 */       sp.setLog(lp);
/* 1562:1824 */       jf.getContentPane().add(lp, "South");
/* 1563:1825 */       jf.addWindowListener(new WindowAdapter()
/* 1564:     */       {
/* 1565:     */         public void windowClosing(WindowEvent e)
/* 1566:     */         {
/* 1567:1828 */           this.val$jf.dispose();
/* 1568:1829 */           System.exit(0);
/* 1569:     */         }
/* 1570:1831 */       });
/* 1571:1832 */       jf.pack();
/* 1572:1833 */       jf.setSize(800, 600);
/* 1573:1834 */       jf.setVisible(true);
/* 1574:     */     }
/* 1575:     */     catch (Exception ex)
/* 1576:     */     {
/* 1577:1836 */       ex.printStackTrace();
/* 1578:1837 */       System.err.println(ex.getMessage());
/* 1579:     */     }
/* 1580:     */   }
/* 1581:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.PreprocessPanel
 * JD-Core Version:    0.7.0.1
 */