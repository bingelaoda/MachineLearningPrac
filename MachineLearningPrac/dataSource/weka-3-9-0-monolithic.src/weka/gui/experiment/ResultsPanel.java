/*    1:     */ package weka.gui.experiment;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Dimension;
/*    6:     */ import java.awt.Font;
/*    7:     */ import java.awt.GridBagConstraints;
/*    8:     */ import java.awt.GridBagLayout;
/*    9:     */ import java.awt.GridLayout;
/*   10:     */ import java.awt.Image;
/*   11:     */ import java.awt.Insets;
/*   12:     */ import java.awt.Toolkit;
/*   13:     */ import java.awt.event.ActionEvent;
/*   14:     */ import java.awt.event.ActionListener;
/*   15:     */ import java.awt.event.WindowAdapter;
/*   16:     */ import java.awt.event.WindowEvent;
/*   17:     */ import java.io.BufferedReader;
/*   18:     */ import java.io.File;
/*   19:     */ import java.io.FileReader;
/*   20:     */ import java.io.PrintStream;
/*   21:     */ import java.io.Reader;
/*   22:     */ import java.text.SimpleDateFormat;
/*   23:     */ import java.util.Date;
/*   24:     */ import java.util.StringTokenizer;
/*   25:     */ import java.util.Vector;
/*   26:     */ import javax.swing.BorderFactory;
/*   27:     */ import javax.swing.DefaultComboBoxModel;
/*   28:     */ import javax.swing.DefaultListModel;
/*   29:     */ import javax.swing.JButton;
/*   30:     */ import javax.swing.JCheckBox;
/*   31:     */ import javax.swing.JComboBox;
/*   32:     */ import javax.swing.JFileChooser;
/*   33:     */ import javax.swing.JFrame;
/*   34:     */ import javax.swing.JLabel;
/*   35:     */ import javax.swing.JList;
/*   36:     */ import javax.swing.JOptionPane;
/*   37:     */ import javax.swing.JPanel;
/*   38:     */ import javax.swing.JScrollPane;
/*   39:     */ import javax.swing.JSplitPane;
/*   40:     */ import javax.swing.JTextArea;
/*   41:     */ import javax.swing.JTextField;
/*   42:     */ import javax.swing.ListModel;
/*   43:     */ import javax.swing.SwingUtilities;
/*   44:     */ import weka.core.Attribute;
/*   45:     */ import weka.core.Instance;
/*   46:     */ import weka.core.Instances;
/*   47:     */ import weka.core.Range;
/*   48:     */ import weka.core.converters.CSVLoader;
/*   49:     */ import weka.experiment.CSVResultListener;
/*   50:     */ import weka.experiment.DatabaseResultListener;
/*   51:     */ import weka.experiment.Experiment;
/*   52:     */ import weka.experiment.InstanceQuery;
/*   53:     */ import weka.experiment.PairedCorrectedTTester;
/*   54:     */ import weka.experiment.ResultMatrix;
/*   55:     */ import weka.experiment.ResultMatrixPlainText;
/*   56:     */ import weka.experiment.Tester;
/*   57:     */ import weka.gui.DatabaseConnectionDialog;
/*   58:     */ import weka.gui.ExtensionFileFilter;
/*   59:     */ import weka.gui.GUIApplication;
/*   60:     */ import weka.gui.GenericObjectEditor;
/*   61:     */ import weka.gui.ListSelectorDialog;
/*   62:     */ import weka.gui.Perspective;
/*   63:     */ import weka.gui.PerspectiveManager;
/*   64:     */ import weka.gui.PropertyDialog;
/*   65:     */ import weka.gui.ResultHistoryPanel;
/*   66:     */ import weka.gui.SaveBuffer;
/*   67:     */ import weka.gui.explorer.Explorer;
/*   68:     */ import weka.gui.explorer.PreprocessPanel;
/*   69:     */ 
/*   70:     */ public class ResultsPanel
/*   71:     */   extends JPanel
/*   72:     */ {
/*   73:     */   private static final long serialVersionUID = -4913007978534178569L;
/*   74:     */   protected static final String NO_SOURCE = "No source";
/*   75: 103 */   protected JButton m_FromFileBut = new JButton("File...");
/*   76: 106 */   protected JButton m_FromDBaseBut = new JButton("Database...");
/*   77: 109 */   protected JButton m_FromExpBut = new JButton("Experiment");
/*   78: 112 */   protected JLabel m_FromLab = new JLabel("No source");
/*   79: 119 */   private static String[] FOR_JFC_1_1_DCBM_BUG = { "" };
/*   80: 122 */   protected DefaultComboBoxModel m_DatasetModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
/*   81: 126 */   protected DefaultComboBoxModel m_CompareModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
/*   82: 130 */   protected DefaultComboBoxModel m_SortModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
/*   83: 134 */   protected DefaultListModel m_TestsModel = new DefaultListModel();
/*   84: 137 */   protected DefaultListModel m_DisplayedModel = new DefaultListModel();
/*   85: 140 */   protected JLabel m_TesterClassesLabel = new JLabel("Testing with", 4);
/*   86:     */   protected Perspective m_mainPerspective;
/*   87: 155 */   protected DefaultComboBoxModel m_TesterClassesModel = new DefaultComboBoxModel(FOR_JFC_1_1_DCBM_BUG);
/*   88: 164 */   protected static Vector<Class<?>> m_Testers = null;
/*   89:     */   protected JComboBox m_TesterClasses;
/*   90: 174 */   protected JLabel m_DatasetAndResultKeysLabel = new JLabel("Select rows and cols", 4);
/*   91: 178 */   protected JPanel m_PanelDatasetResultKeys = new JPanel(new GridLayout(1, 3));
/*   92: 181 */   protected JButton m_DatasetKeyBut = new JButton("Rows");
/*   93: 184 */   protected DefaultListModel m_DatasetKeyModel = new DefaultListModel();
/*   94: 187 */   protected JList m_DatasetKeyList = new JList(this.m_DatasetKeyModel);
/*   95: 190 */   protected JButton m_ResultKeyBut = new JButton("Cols");
/*   96: 193 */   protected JButton m_SwapDatasetKeyAndResultKeyBut = new JButton("Swap");
/*   97: 196 */   protected DefaultListModel m_ResultKeyModel = new DefaultListModel();
/*   98: 199 */   protected JList m_ResultKeyList = new JList(this.m_ResultKeyModel);
/*   99: 202 */   protected JButton m_TestsButton = new JButton("Select");
/*  100: 205 */   protected JButton m_DisplayedButton = new JButton("Select");
/*  101: 208 */   protected JList m_TestsList = new JList(this.m_TestsModel);
/*  102: 211 */   protected JList m_DisplayedList = new JList(this.m_DisplayedModel);
/*  103: 214 */   protected JComboBox m_CompareCombo = new JComboBox(this.m_CompareModel);
/*  104: 217 */   protected JComboBox m_SortCombo = new JComboBox(this.m_SortModel);
/*  105: 220 */   protected JTextField m_SigTex = new JTextField("" + ExperimenterDefaults.getSignificance());
/*  106: 226 */   protected JCheckBox m_ShowStdDevs = new JCheckBox("");
/*  107: 229 */   protected JButton m_OutputFormatButton = new JButton("Select");
/*  108: 232 */   protected JButton m_Explorer = new JButton("Open Explorer...");
/*  109: 235 */   protected JButton m_PerformBut = new JButton("Perform test");
/*  110: 238 */   protected JButton m_SaveOutBut = new JButton("Save output");
/*  111: 241 */   SaveBuffer m_SaveOut = new SaveBuffer(null, this);
/*  112: 244 */   protected JTextArea m_OutText = new JTextArea();
/*  113: 247 */   protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
/*  114: 250 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  115: 255 */   protected ExtensionFileFilter m_csvFileFilter = new ExtensionFileFilter(CSVLoader.FILE_EXTENSION, "CSV data files");
/*  116: 259 */   protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(".arff", "Arff data files");
/*  117: 263 */   protected Tester m_TTester = new PairedCorrectedTTester();
/*  118:     */   protected Instances m_Instances;
/*  119:     */   protected InstanceQuery m_InstanceQuery;
/*  120:     */   protected Thread m_LoadThread;
/*  121:     */   protected Experiment m_Exp;
/*  122: 278 */   private final Dimension COMBO_SIZE = new Dimension(210, this.m_ResultKeyBut.getPreferredSize().height);
/*  123: 282 */   protected ResultMatrix m_ResultMatrix = new ResultMatrixPlainText();
/*  124:     */   
/*  125:     */   public ResultsPanel()
/*  126:     */   {
/*  127: 289 */     Vector<String> classes = GenericObjectEditor.getClassnames(Tester.class.getName());
/*  128:     */     
/*  129:     */ 
/*  130:     */ 
/*  131: 293 */     m_Testers = new Vector();
/*  132: 294 */     this.m_TesterClassesModel = new DefaultComboBoxModel();
/*  133: 295 */     for (int i = 0; i < classes.size(); i++) {
/*  134:     */       try
/*  135:     */       {
/*  136: 297 */         Class<?> cls = Class.forName(((String)classes.get(i)).toString());
/*  137: 298 */         Tester tester = (Tester)cls.newInstance();
/*  138: 299 */         m_Testers.add(cls);
/*  139: 300 */         this.m_TesterClassesModel.addElement(tester.getDisplayName());
/*  140:     */       }
/*  141:     */       catch (Exception e)
/*  142:     */       {
/*  143: 302 */         e.printStackTrace();
/*  144:     */       }
/*  145:     */     }
/*  146: 306 */     this.m_TesterClasses = new JComboBox(this.m_TesterClassesModel);
/*  147:     */     
/*  148:     */ 
/*  149: 309 */     this.m_TTester.setSignificanceLevel(ExperimenterDefaults.getSignificance());
/*  150: 310 */     this.m_TTester.setShowStdDevs(ExperimenterDefaults.getShowStdDevs());
/*  151: 311 */     this.m_ResultMatrix = ExperimenterDefaults.getOutputFormat();
/*  152: 312 */     this.m_ResultMatrix.setShowStdDev(ExperimenterDefaults.getShowStdDevs());
/*  153: 313 */     this.m_ResultMatrix.setMeanPrec(ExperimenterDefaults.getMeanPrecision());
/*  154: 314 */     this.m_ResultMatrix.setStdDevPrec(ExperimenterDefaults.getStdDevPrecision());
/*  155: 315 */     this.m_ResultMatrix.setRemoveFilterName(ExperimenterDefaults.getRemoveFilterClassnames());
/*  156:     */     
/*  157: 317 */     this.m_ResultMatrix.setShowAverage(ExperimenterDefaults.getShowAverage());
/*  158:     */     
/*  159:     */ 
/*  160:     */ 
/*  161: 321 */     this.m_FileChooser.addChoosableFileFilter(this.m_csvFileFilter);
/*  162: 322 */     this.m_FileChooser.addChoosableFileFilter(this.m_arffFileFilter);
/*  163:     */     
/*  164: 324 */     this.m_FileChooser.setFileSelectionMode(0);
/*  165: 325 */     this.m_FromExpBut.setEnabled(false);
/*  166: 326 */     this.m_FromExpBut.setMnemonic('E');
/*  167: 327 */     this.m_FromExpBut.addActionListener(new ActionListener()
/*  168:     */     {
/*  169:     */       public void actionPerformed(ActionEvent e)
/*  170:     */       {
/*  171: 330 */         if (ResultsPanel.this.m_LoadThread == null)
/*  172:     */         {
/*  173: 331 */           ResultsPanel.this.m_LoadThread = new Thread()
/*  174:     */           {
/*  175:     */             public void run()
/*  176:     */             {
/*  177: 334 */               ResultsPanel.this.setInstancesFromExp(ResultsPanel.this.m_Exp);
/*  178: 335 */               ResultsPanel.this.m_LoadThread = null;
/*  179:     */             }
/*  180: 337 */           };
/*  181: 338 */           ResultsPanel.this.m_LoadThread.start();
/*  182:     */         }
/*  183:     */       }
/*  184: 341 */     });
/*  185: 342 */     this.m_FromDBaseBut.setMnemonic('D');
/*  186: 343 */     this.m_FromDBaseBut.addActionListener(new ActionListener()
/*  187:     */     {
/*  188:     */       public void actionPerformed(ActionEvent e)
/*  189:     */       {
/*  190: 346 */         if (ResultsPanel.this.m_LoadThread == null)
/*  191:     */         {
/*  192: 347 */           ResultsPanel.this.m_LoadThread = new Thread()
/*  193:     */           {
/*  194:     */             public void run()
/*  195:     */             {
/*  196: 350 */               ResultsPanel.this.setInstancesFromDBaseQuery();
/*  197: 351 */               ResultsPanel.this.m_LoadThread = null;
/*  198:     */             }
/*  199: 353 */           };
/*  200: 354 */           ResultsPanel.this.m_LoadThread.start();
/*  201:     */         }
/*  202:     */       }
/*  203: 357 */     });
/*  204: 358 */     this.m_FromFileBut.setMnemonic('F');
/*  205: 359 */     this.m_FromFileBut.addActionListener(new ActionListener()
/*  206:     */     {
/*  207:     */       public void actionPerformed(ActionEvent e)
/*  208:     */       {
/*  209: 362 */         int returnVal = ResultsPanel.this.m_FileChooser.showOpenDialog(ResultsPanel.this);
/*  210: 363 */         if (returnVal == 0)
/*  211:     */         {
/*  212: 364 */           final File selected = ResultsPanel.this.m_FileChooser.getSelectedFile();
/*  213: 365 */           if (ResultsPanel.this.m_LoadThread == null)
/*  214:     */           {
/*  215: 366 */             ResultsPanel.this.m_LoadThread = new Thread()
/*  216:     */             {
/*  217:     */               public void run()
/*  218:     */               {
/*  219: 369 */                 ResultsPanel.this.setInstancesFromFile(selected);
/*  220: 370 */                 ResultsPanel.this.m_LoadThread = null;
/*  221:     */               }
/*  222: 372 */             };
/*  223: 373 */             ResultsPanel.this.m_LoadThread.start();
/*  224:     */           }
/*  225:     */         }
/*  226:     */       }
/*  227: 377 */     });
/*  228: 378 */     setComboSizes();
/*  229: 379 */     this.m_TesterClasses.setEnabled(false);
/*  230: 380 */     this.m_DatasetKeyBut.setEnabled(false);
/*  231: 381 */     this.m_DatasetKeyBut.setToolTipText("For selecting the keys that are shown as rows.");
/*  232:     */     
/*  233: 383 */     this.m_DatasetKeyBut.addActionListener(new ActionListener()
/*  234:     */     {
/*  235:     */       public void actionPerformed(ActionEvent e)
/*  236:     */       {
/*  237: 386 */         ResultsPanel.this.setDatasetKeyFromDialog();
/*  238:     */       }
/*  239: 388 */     });
/*  240: 389 */     this.m_DatasetKeyList.setSelectionMode(2);
/*  241:     */     
/*  242: 391 */     this.m_ResultKeyBut.setEnabled(false);
/*  243: 392 */     this.m_ResultKeyBut.setToolTipText("For selecting the keys that are shown as columns.");
/*  244:     */     
/*  245: 394 */     this.m_ResultKeyBut.addActionListener(new ActionListener()
/*  246:     */     {
/*  247:     */       public void actionPerformed(ActionEvent e)
/*  248:     */       {
/*  249: 397 */         ResultsPanel.this.setResultKeyFromDialog();
/*  250:     */       }
/*  251: 399 */     });
/*  252: 400 */     this.m_ResultKeyList.setSelectionMode(2);
/*  253:     */     
/*  254: 402 */     this.m_SwapDatasetKeyAndResultKeyBut.setEnabled(false);
/*  255: 403 */     this.m_SwapDatasetKeyAndResultKeyBut.setToolTipText("Swaps the keys for selecting rows and columns.");
/*  256:     */     
/*  257: 405 */     this.m_SwapDatasetKeyAndResultKeyBut.addActionListener(new ActionListener()
/*  258:     */     {
/*  259:     */       public void actionPerformed(ActionEvent e)
/*  260:     */       {
/*  261: 408 */         ResultsPanel.this.swapDatasetKeyAndResultKey();
/*  262:     */       }
/*  263: 410 */     });
/*  264: 411 */     this.m_CompareCombo.setEnabled(false);
/*  265: 412 */     this.m_SortCombo.setEnabled(false);
/*  266:     */     
/*  267: 414 */     this.m_SigTex.setEnabled(false);
/*  268: 415 */     this.m_TestsButton.setEnabled(false);
/*  269: 416 */     this.m_TestsButton.addActionListener(new ActionListener()
/*  270:     */     {
/*  271:     */       public void actionPerformed(ActionEvent e)
/*  272:     */       {
/*  273: 419 */         ResultsPanel.this.setTestBaseFromDialog();
/*  274:     */       }
/*  275: 422 */     });
/*  276: 423 */     this.m_DisplayedButton.setEnabled(false);
/*  277: 424 */     this.m_DisplayedButton.addActionListener(new ActionListener()
/*  278:     */     {
/*  279:     */       public void actionPerformed(ActionEvent e)
/*  280:     */       {
/*  281: 427 */         ResultsPanel.this.setDisplayedFromDialog();
/*  282:     */       }
/*  283: 430 */     });
/*  284: 431 */     this.m_ShowStdDevs.setEnabled(false);
/*  285: 432 */     this.m_ShowStdDevs.setSelected(ExperimenterDefaults.getShowStdDevs());
/*  286: 433 */     this.m_OutputFormatButton.setEnabled(false);
/*  287: 434 */     this.m_OutputFormatButton.addActionListener(new ActionListener()
/*  288:     */     {
/*  289:     */       public void actionPerformed(ActionEvent e)
/*  290:     */       {
/*  291: 437 */         ResultsPanel.this.setOutputFormatFromDialog();
/*  292:     */       }
/*  293: 440 */     });
/*  294: 441 */     this.m_Explorer.setEnabled(false);
/*  295: 442 */     this.m_Explorer.addActionListener(new ActionListener()
/*  296:     */     {
/*  297:     */       public void actionPerformed(ActionEvent e)
/*  298:     */       {
/*  299: 445 */         ResultsPanel.this.openExplorer();
/*  300:     */       }
/*  301: 448 */     });
/*  302: 449 */     this.m_PerformBut.setEnabled(false);
/*  303: 450 */     this.m_PerformBut.addActionListener(new ActionListener()
/*  304:     */     {
/*  305:     */       public void actionPerformed(ActionEvent e)
/*  306:     */       {
/*  307: 453 */         ResultsPanel.this.performTest();
/*  308: 454 */         ResultsPanel.this.m_SaveOutBut.setEnabled(true);
/*  309:     */       }
/*  310: 457 */     });
/*  311: 458 */     this.m_PerformBut.setToolTipText(this.m_TTester.getToolTipText());
/*  312:     */     
/*  313: 460 */     this.m_SaveOutBut.setEnabled(false);
/*  314: 461 */     this.m_SaveOutBut.addActionListener(new ActionListener()
/*  315:     */     {
/*  316:     */       public void actionPerformed(ActionEvent e)
/*  317:     */       {
/*  318: 464 */         ResultsPanel.this.saveBuffer();
/*  319:     */       }
/*  320: 466 */     });
/*  321: 467 */     this.m_OutText.setFont(new Font("Monospaced", 0, 12));
/*  322: 468 */     this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  323: 469 */     this.m_OutText.setEditable(false);
/*  324: 470 */     this.m_History.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  325:     */     
/*  326:     */ 
/*  327: 473 */     JPanel sourceAndButsHolder = new JPanel();
/*  328: 474 */     sourceAndButsHolder.setLayout(new BorderLayout());
/*  329: 475 */     JPanel p1 = new JPanel();
/*  330: 476 */     p1.setBorder(BorderFactory.createTitledBorder("Source"));
/*  331: 477 */     sourceAndButsHolder.add(p1, "North");
/*  332: 478 */     JPanel p2 = new JPanel();
/*  333: 479 */     GridBagLayout gb = new GridBagLayout();
/*  334: 480 */     GridBagConstraints constraints = new GridBagConstraints();
/*  335: 481 */     p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
/*  336:     */     
/*  337: 483 */     p2.setLayout(gb);
/*  338: 484 */     constraints.gridx = 0;
/*  339: 485 */     constraints.gridy = 0;
/*  340: 486 */     constraints.weightx = 5.0D;
/*  341: 487 */     constraints.fill = 2;
/*  342: 488 */     constraints.gridwidth = 1;
/*  343: 489 */     constraints.gridheight = 1;
/*  344: 490 */     constraints.insets = new Insets(0, 2, 0, 2);
/*  345: 491 */     p2.add(this.m_FromFileBut, constraints);
/*  346: 492 */     constraints.gridx = 1;
/*  347: 493 */     constraints.gridy = 0;
/*  348: 494 */     constraints.weightx = 5.0D;
/*  349: 495 */     constraints.gridwidth = 1;
/*  350: 496 */     constraints.gridheight = 1;
/*  351: 497 */     p2.add(this.m_FromDBaseBut, constraints);
/*  352: 498 */     constraints.gridx = 2;
/*  353: 499 */     constraints.gridy = 0;
/*  354: 500 */     constraints.weightx = 5.0D;
/*  355: 501 */     constraints.gridwidth = 1;
/*  356: 502 */     constraints.gridheight = 1;
/*  357: 503 */     p2.add(this.m_FromExpBut, constraints);
/*  358: 504 */     p1.setLayout(new BorderLayout());
/*  359: 505 */     p1.add(this.m_FromLab, "Center");
/*  360: 506 */     p1.add(p2, "East");
/*  361:     */     
/*  362: 508 */     JPanel newButHolder = new JPanel();
/*  363: 509 */     newButHolder.setLayout(new BorderLayout());
/*  364: 510 */     newButHolder.setBorder(BorderFactory.createTitledBorder("Actions"));
/*  365: 511 */     sourceAndButsHolder.add(newButHolder, "South");
/*  366:     */     
/*  367: 513 */     JPanel p3 = new JPanel();
/*  368: 514 */     p3.setBorder(BorderFactory.createTitledBorder("Configure test"));
/*  369: 515 */     GridBagLayout gbL = new GridBagLayout();
/*  370: 516 */     p3.setLayout(gbL);
/*  371:     */     
/*  372: 518 */     int y = 0;
/*  373: 519 */     GridBagConstraints gbC = new GridBagConstraints();
/*  374: 520 */     gbC.anchor = 13;
/*  375: 521 */     gbC.gridy = y;
/*  376: 522 */     gbC.gridx = 0;
/*  377: 523 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  378: 524 */     gbL.setConstraints(this.m_TesterClassesLabel, gbC);
/*  379: 525 */     this.m_TesterClassesLabel.setDisplayedMnemonic('w');
/*  380: 526 */     this.m_TesterClassesLabel.setLabelFor(this.m_TesterClasses);
/*  381: 527 */     p3.add(this.m_TesterClassesLabel);
/*  382: 528 */     gbC = new GridBagConstraints();
/*  383: 529 */     gbC.gridy = y;
/*  384: 530 */     gbC.gridx = 1;
/*  385: 531 */     gbC.weightx = 100.0D;
/*  386: 532 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  387: 533 */     gbC.fill = 2;
/*  388: 534 */     gbL.setConstraints(this.m_TesterClasses, gbC);
/*  389: 535 */     p3.add(this.m_TesterClasses);
/*  390: 536 */     this.m_TesterClasses.addActionListener(new ActionListener()
/*  391:     */     {
/*  392:     */       public void actionPerformed(ActionEvent e)
/*  393:     */       {
/*  394: 539 */         ResultsPanel.this.setTester();
/*  395:     */       }
/*  396: 541 */     });
/*  397: 542 */     setSelectedItem(this.m_TesterClasses, ExperimenterDefaults.getTester());
/*  398:     */     
/*  399: 544 */     y++;
/*  400: 545 */     gbC = new GridBagConstraints();
/*  401: 546 */     gbC.anchor = 13;
/*  402: 547 */     gbC.gridy = y;
/*  403: 548 */     gbC.gridx = 0;
/*  404: 549 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  405: 550 */     gbL.setConstraints(this.m_DatasetAndResultKeysLabel, gbC);
/*  406: 551 */     this.m_DatasetAndResultKeysLabel.setDisplayedMnemonic('R');
/*  407: 552 */     this.m_DatasetAndResultKeysLabel.setLabelFor(this.m_DatasetKeyBut);
/*  408: 553 */     p3.add(this.m_DatasetAndResultKeysLabel);
/*  409:     */     
/*  410: 555 */     this.m_PanelDatasetResultKeys.add(this.m_DatasetKeyBut);
/*  411: 556 */     this.m_PanelDatasetResultKeys.add(this.m_ResultKeyBut);
/*  412: 557 */     this.m_PanelDatasetResultKeys.add(this.m_SwapDatasetKeyAndResultKeyBut);
/*  413: 558 */     gbC = new GridBagConstraints();
/*  414: 559 */     gbC.fill = 2;
/*  415: 560 */     gbC.gridy = y;
/*  416: 561 */     gbC.gridx = 1;
/*  417: 562 */     gbC.weightx = 100.0D;
/*  418: 563 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  419: 564 */     gbL.setConstraints(this.m_PanelDatasetResultKeys, gbC);
/*  420: 565 */     p3.add(this.m_PanelDatasetResultKeys);
/*  421:     */     
/*  422: 567 */     y++;
/*  423: 568 */     JLabel lab = new JLabel("Comparison field", 4);
/*  424: 569 */     lab.setDisplayedMnemonic('m');
/*  425: 570 */     lab.setLabelFor(this.m_CompareCombo);
/*  426: 571 */     gbC = new GridBagConstraints();
/*  427: 572 */     gbC.anchor = 13;
/*  428: 573 */     gbC.gridy = y;
/*  429: 574 */     gbC.gridx = 0;
/*  430: 575 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  431: 576 */     gbL.setConstraints(lab, gbC);
/*  432: 577 */     p3.add(lab);
/*  433: 578 */     gbC = new GridBagConstraints();
/*  434: 579 */     gbC.gridy = y;
/*  435: 580 */     gbC.gridx = 1;
/*  436: 581 */     gbC.weightx = 100.0D;
/*  437: 582 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  438: 583 */     gbC.fill = 2;
/*  439: 584 */     gbL.setConstraints(this.m_CompareCombo, gbC);
/*  440: 585 */     p3.add(this.m_CompareCombo);
/*  441:     */     
/*  442: 587 */     y++;
/*  443: 588 */     lab = new JLabel("Significance", 4);
/*  444: 589 */     lab.setDisplayedMnemonic('g');
/*  445: 590 */     lab.setLabelFor(this.m_SigTex);
/*  446: 591 */     gbC = new GridBagConstraints();
/*  447: 592 */     gbC.anchor = 13;
/*  448: 593 */     gbC.gridy = y;
/*  449: 594 */     gbC.gridx = 0;
/*  450: 595 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  451: 596 */     gbL.setConstraints(lab, gbC);
/*  452: 597 */     p3.add(lab);
/*  453: 598 */     gbC = new GridBagConstraints();
/*  454: 599 */     gbC.fill = 2;
/*  455: 600 */     gbC.gridy = y;
/*  456: 601 */     gbC.gridx = 1;
/*  457: 602 */     gbC.weightx = 100.0D;
/*  458: 603 */     gbL.setConstraints(this.m_SigTex, gbC);
/*  459: 604 */     p3.add(this.m_SigTex);
/*  460:     */     
/*  461: 606 */     y++;
/*  462: 607 */     lab = new JLabel("Sorting (asc.) by", 4);
/*  463: 608 */     lab.setDisplayedMnemonic('S');
/*  464: 609 */     lab.setLabelFor(this.m_SortCombo);
/*  465: 610 */     gbC = new GridBagConstraints();
/*  466: 611 */     gbC.anchor = 13;
/*  467: 612 */     gbC.gridy = y;
/*  468: 613 */     gbC.gridx = 0;
/*  469: 614 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  470: 615 */     gbL.setConstraints(lab, gbC);
/*  471: 616 */     p3.add(lab);
/*  472: 617 */     gbC = new GridBagConstraints();
/*  473: 618 */     gbC.anchor = 17;
/*  474: 619 */     gbC.fill = 2;
/*  475: 620 */     gbC.gridy = y;
/*  476: 621 */     gbC.gridx = 1;
/*  477: 622 */     gbC.weightx = 100.0D;
/*  478: 623 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  479: 624 */     gbL.setConstraints(this.m_SortCombo, gbC);
/*  480: 625 */     p3.add(this.m_SortCombo);
/*  481:     */     
/*  482: 627 */     y++;
/*  483: 628 */     lab = new JLabel("Test base", 4);
/*  484: 629 */     lab.setDisplayedMnemonic('b');
/*  485: 630 */     lab.setLabelFor(this.m_TestsButton);
/*  486: 631 */     gbC = new GridBagConstraints();
/*  487: 632 */     gbC.anchor = 13;
/*  488: 633 */     gbC.gridy = y;
/*  489: 634 */     gbC.gridx = 0;
/*  490: 635 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  491: 636 */     gbL.setConstraints(lab, gbC);
/*  492: 637 */     p3.add(lab);
/*  493: 638 */     gbC = new GridBagConstraints();
/*  494: 639 */     gbC.fill = 2;
/*  495: 640 */     gbC.gridy = y;
/*  496: 641 */     gbC.gridx = 1;
/*  497: 642 */     gbC.weightx = 100.0D;
/*  498: 643 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  499: 644 */     gbL.setConstraints(this.m_TestsButton, gbC);
/*  500: 645 */     p3.add(this.m_TestsButton);
/*  501:     */     
/*  502: 647 */     y++;
/*  503: 648 */     lab = new JLabel("Displayed Columns", 4);
/*  504: 649 */     lab.setDisplayedMnemonic('i');
/*  505: 650 */     lab.setLabelFor(this.m_DisplayedButton);
/*  506: 651 */     gbC = new GridBagConstraints();
/*  507: 652 */     gbC.anchor = 13;
/*  508: 653 */     gbC.gridy = y;
/*  509: 654 */     gbC.gridx = 0;
/*  510: 655 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  511: 656 */     gbL.setConstraints(lab, gbC);
/*  512: 657 */     p3.add(lab);
/*  513: 658 */     gbC = new GridBagConstraints();
/*  514: 659 */     gbC.fill = 2;
/*  515: 660 */     gbC.gridy = y;
/*  516: 661 */     gbC.gridx = 1;
/*  517: 662 */     gbC.weightx = 100.0D;
/*  518: 663 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  519: 664 */     gbL.setConstraints(this.m_DisplayedButton, gbC);
/*  520: 665 */     p3.add(this.m_DisplayedButton);
/*  521:     */     
/*  522: 667 */     y++;
/*  523: 668 */     lab = new JLabel("Show std. deviations", 4);
/*  524: 669 */     lab.setDisplayedMnemonic('a');
/*  525: 670 */     lab.setLabelFor(this.m_ShowStdDevs);
/*  526: 671 */     gbC = new GridBagConstraints();
/*  527: 672 */     gbC.anchor = 13;
/*  528: 673 */     gbC.gridy = y;
/*  529: 674 */     gbC.gridx = 0;
/*  530: 675 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  531: 676 */     gbL.setConstraints(lab, gbC);
/*  532: 677 */     p3.add(lab);
/*  533: 678 */     gbC = new GridBagConstraints();
/*  534: 679 */     gbC.anchor = 17;
/*  535: 680 */     gbC.gridy = y;
/*  536: 681 */     gbC.gridx = 1;
/*  537: 682 */     gbC.weightx = 100.0D;
/*  538: 683 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  539: 684 */     gbL.setConstraints(this.m_ShowStdDevs, gbC);
/*  540: 685 */     p3.add(this.m_ShowStdDevs);
/*  541:     */     
/*  542: 687 */     y++;
/*  543: 688 */     lab = new JLabel("Output Format", 4);
/*  544: 689 */     lab.setDisplayedMnemonic('O');
/*  545: 690 */     lab.setLabelFor(this.m_OutputFormatButton);
/*  546: 691 */     gbC = new GridBagConstraints();
/*  547: 692 */     gbC.anchor = 13;
/*  548: 693 */     gbC.gridy = y;
/*  549: 694 */     gbC.gridx = 0;
/*  550: 695 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  551: 696 */     gbL.setConstraints(lab, gbC);
/*  552: 697 */     p3.add(lab);
/*  553: 698 */     gbC = new GridBagConstraints();
/*  554: 699 */     gbC.anchor = 17;
/*  555: 700 */     gbC.fill = 2;
/*  556: 701 */     gbC.gridy = y;
/*  557: 702 */     gbC.gridx = 1;
/*  558: 703 */     gbC.weightx = 100.0D;
/*  559: 704 */     gbC.insets = new Insets(5, 0, 5, 0);
/*  560: 705 */     gbL.setConstraints(this.m_OutputFormatButton, gbC);
/*  561: 706 */     p3.add(this.m_OutputFormatButton);
/*  562:     */     
/*  563: 708 */     JPanel output = new JPanel();
/*  564: 709 */     output.setLayout(new BorderLayout());
/*  565: 710 */     output.setBorder(BorderFactory.createTitledBorder("Test output"));
/*  566: 711 */     output.add(new JScrollPane(this.m_OutText), "Center");
/*  567:     */     
/*  568: 713 */     JPanel mondo = new JPanel();
/*  569: 714 */     gbL = new GridBagLayout();
/*  570: 715 */     mondo.setLayout(gbL);
/*  571: 716 */     gbC = new GridBagConstraints();
/*  572:     */     
/*  573:     */ 
/*  574: 719 */     gbC.gridy = 0;
/*  575: 720 */     gbC.gridx = 0;
/*  576: 721 */     gbL.setConstraints(p3, gbC);
/*  577: 722 */     mondo.add(p3);
/*  578:     */     
/*  579: 724 */     JPanel bts = new JPanel();
/*  580: 725 */     this.m_PerformBut.setMnemonic('t');
/*  581: 726 */     this.m_SaveOutBut.setMnemonic('S');
/*  582: 727 */     bts.setLayout(new GridLayout(1, 3, 5, 5));
/*  583: 728 */     bts.add(this.m_PerformBut);
/*  584: 729 */     bts.add(this.m_SaveOutBut);
/*  585: 730 */     bts.add(this.m_Explorer);
/*  586: 731 */     newButHolder.add(bts, "West");
/*  587:     */     
/*  588:     */ 
/*  589:     */ 
/*  590:     */ 
/*  591:     */ 
/*  592:     */ 
/*  593:     */ 
/*  594: 739 */     gbC = new GridBagConstraints();
/*  595:     */     
/*  596: 741 */     gbC.fill = 1;
/*  597: 742 */     gbC.gridy = 2;
/*  598: 743 */     gbC.gridx = 0;
/*  599: 744 */     gbC.weightx = 0.0D;
/*  600: 745 */     gbC.weighty = 100.0D;
/*  601: 746 */     gbL.setConstraints(this.m_History, gbC);
/*  602: 747 */     mondo.add(this.m_History);
/*  603:     */     
/*  604:     */ 
/*  605:     */ 
/*  606:     */ 
/*  607:     */ 
/*  608:     */ 
/*  609: 754 */     JSplitPane splitPane = new JSplitPane(1, mondo, output);
/*  610:     */     
/*  611: 756 */     splitPane.setOneTouchExpandable(true);
/*  612:     */     
/*  613:     */ 
/*  614: 759 */     setLayout(new BorderLayout());
/*  615: 760 */     add(sourceAndButsHolder, "North");
/*  616:     */     
/*  617: 762 */     add(splitPane, "Center");
/*  618:     */   }
/*  619:     */   
/*  620:     */   protected void setMainPerspective(Perspective mainPerspective)
/*  621:     */   {
/*  622: 771 */     this.m_mainPerspective = mainPerspective;
/*  623: 772 */     if (this.m_mainPerspective.acceptsInstances()) {
/*  624: 773 */       this.m_Explorer.setText("Send to " + mainPerspective.getPerspectiveTitle());
/*  625:     */     }
/*  626:     */   }
/*  627:     */   
/*  628:     */   protected void setComboSizes()
/*  629:     */   {
/*  630: 783 */     this.m_TesterClasses.setPreferredSize(this.COMBO_SIZE);
/*  631: 784 */     this.m_PanelDatasetResultKeys.setPreferredSize(this.COMBO_SIZE);
/*  632: 785 */     this.m_CompareCombo.setPreferredSize(this.COMBO_SIZE);
/*  633: 786 */     this.m_SigTex.setPreferredSize(this.COMBO_SIZE);
/*  634: 787 */     this.m_SortCombo.setPreferredSize(this.COMBO_SIZE);
/*  635:     */     
/*  636: 789 */     this.m_TesterClasses.setMaximumSize(this.COMBO_SIZE);
/*  637: 790 */     this.m_PanelDatasetResultKeys.setMaximumSize(this.COMBO_SIZE);
/*  638: 791 */     this.m_CompareCombo.setMaximumSize(this.COMBO_SIZE);
/*  639: 792 */     this.m_SigTex.setMaximumSize(this.COMBO_SIZE);
/*  640: 793 */     this.m_SortCombo.setMaximumSize(this.COMBO_SIZE);
/*  641:     */     
/*  642: 795 */     this.m_TesterClasses.setMinimumSize(this.COMBO_SIZE);
/*  643: 796 */     this.m_PanelDatasetResultKeys.setMinimumSize(this.COMBO_SIZE);
/*  644: 797 */     this.m_CompareCombo.setMinimumSize(this.COMBO_SIZE);
/*  645: 798 */     this.m_SigTex.setMinimumSize(this.COMBO_SIZE);
/*  646: 799 */     this.m_SortCombo.setMinimumSize(this.COMBO_SIZE);
/*  647:     */   }
/*  648:     */   
/*  649:     */   public void setExperiment(Experiment exp)
/*  650:     */   {
/*  651: 809 */     this.m_Exp = exp;
/*  652: 810 */     this.m_FromExpBut.setEnabled(exp != null);
/*  653:     */   }
/*  654:     */   
/*  655:     */   protected void setInstancesFromDBaseQuery()
/*  656:     */   {
/*  657:     */     try
/*  658:     */     {
/*  659: 820 */       if (this.m_InstanceQuery == null) {
/*  660: 821 */         this.m_InstanceQuery = new InstanceQuery();
/*  661:     */       }
/*  662: 823 */       String dbaseURL = this.m_InstanceQuery.getDatabaseURL();
/*  663: 824 */       String username = this.m_InstanceQuery.getUsername();
/*  664: 825 */       String passwd = this.m_InstanceQuery.getPassword();
/*  665:     */       
/*  666:     */ 
/*  667:     */ 
/*  668:     */ 
/*  669:     */ 
/*  670:     */ 
/*  671: 832 */       DatabaseConnectionDialog dbd = new DatabaseConnectionDialog(null, dbaseURL, username);
/*  672:     */       
/*  673: 834 */       dbd.setVisible(true);
/*  674: 837 */       if (dbd.getReturnValue() == -1)
/*  675:     */       {
/*  676: 838 */         this.m_FromLab.setText("Cancelled");
/*  677: 839 */         return;
/*  678:     */       }
/*  679: 841 */       dbaseURL = dbd.getURL();
/*  680: 842 */       username = dbd.getUsername();
/*  681: 843 */       passwd = dbd.getPassword();
/*  682: 844 */       this.m_InstanceQuery.setDatabaseURL(dbaseURL);
/*  683: 845 */       this.m_InstanceQuery.setUsername(username);
/*  684: 846 */       this.m_InstanceQuery.setPassword(passwd);
/*  685: 847 */       this.m_InstanceQuery.setDebug(dbd.getDebug());
/*  686:     */       
/*  687: 849 */       this.m_InstanceQuery.connectToDatabase();
/*  688: 850 */       if (!this.m_InstanceQuery.experimentIndexExists())
/*  689:     */       {
/*  690: 851 */         System.err.println("not found");
/*  691: 852 */         this.m_FromLab.setText("No experiment index");
/*  692: 853 */         this.m_InstanceQuery.disconnectFromDatabase();
/*  693: 854 */         return;
/*  694:     */       }
/*  695: 856 */       System.err.println("found");
/*  696: 857 */       this.m_FromLab.setText("Getting experiment index");
/*  697: 858 */       Instances index = this.m_InstanceQuery.retrieveInstances("SELECT * FROM Experiment_index");
/*  698: 861 */       if (index.numInstances() == 0)
/*  699:     */       {
/*  700: 862 */         this.m_FromLab.setText("No experiments available");
/*  701: 863 */         this.m_InstanceQuery.disconnectFromDatabase();
/*  702: 864 */         return;
/*  703:     */       }
/*  704: 866 */       this.m_FromLab.setText("Got experiment index");
/*  705:     */       
/*  706: 868 */       DefaultListModel lm = new DefaultListModel();
/*  707: 869 */       for (int i = 0; i < index.numInstances(); i++) {
/*  708: 870 */         lm.addElement(index.instance(i).toString());
/*  709:     */       }
/*  710: 872 */       JList jl = new JList(lm);
/*  711: 873 */       jl.setSelectedIndex(0);
/*  712:     */       int result;
/*  713:     */       int result;
/*  714: 876 */       if (jl.getModel().getSize() != 1)
/*  715:     */       {
/*  716: 877 */         ListSelectorDialog jd = new ListSelectorDialog(null, jl);
/*  717: 878 */         result = jd.showDialog();
/*  718:     */       }
/*  719:     */       else
/*  720:     */       {
/*  721: 880 */         result = 0;
/*  722:     */       }
/*  723: 882 */       if (result != 0)
/*  724:     */       {
/*  725: 883 */         this.m_FromLab.setText("Cancelled");
/*  726: 884 */         this.m_InstanceQuery.disconnectFromDatabase();
/*  727: 885 */         return;
/*  728:     */       }
/*  729: 887 */       Instance selInst = index.instance(jl.getSelectedIndex());
/*  730: 888 */       Attribute tableAttr = index.attribute("Result_table");
/*  731: 889 */       String table = "Results" + selInst.toString(tableAttr);
/*  732:     */       
/*  733: 891 */       setInstancesFromDatabaseTable(table);
/*  734:     */     }
/*  735:     */     catch (Exception ex)
/*  736:     */     {
/*  737: 895 */       ex.printStackTrace();
/*  738:     */       
/*  739: 897 */       this.m_FromLab.setText("Problem reading database: '" + ex.getMessage() + "'");
/*  740:     */     }
/*  741:     */   }
/*  742:     */   
/*  743:     */   protected void setInstancesFromExp(Experiment exp)
/*  744:     */   {
/*  745: 909 */     if ((exp.getResultListener() instanceof CSVResultListener))
/*  746:     */     {
/*  747: 910 */       File resultFile = ((CSVResultListener)exp.getResultListener()).getOutputFile();
/*  748: 912 */       if (resultFile == null) {
/*  749: 913 */         this.m_FromLab.setText("No result file");
/*  750:     */       } else {
/*  751: 915 */         setInstancesFromFile(resultFile);
/*  752:     */       }
/*  753:     */     }
/*  754: 917 */     else if ((exp.getResultListener() instanceof DatabaseResultListener))
/*  755:     */     {
/*  756: 918 */       String dbaseURL = ((DatabaseResultListener)exp.getResultListener()).getDatabaseURL();
/*  757:     */       try
/*  758:     */       {
/*  759: 921 */         if (this.m_InstanceQuery == null) {
/*  760: 922 */           this.m_InstanceQuery = new InstanceQuery();
/*  761:     */         }
/*  762: 924 */         this.m_InstanceQuery.setDatabaseURL(dbaseURL);
/*  763: 925 */         this.m_InstanceQuery.connectToDatabase();
/*  764: 926 */         String tableName = this.m_InstanceQuery.getResultsTableName(exp.getResultProducer());
/*  765:     */         
/*  766: 928 */         setInstancesFromDatabaseTable(tableName);
/*  767:     */       }
/*  768:     */       catch (Exception ex)
/*  769:     */       {
/*  770: 930 */         this.m_FromLab.setText("Problem reading database");
/*  771:     */       }
/*  772:     */     }
/*  773:     */     else
/*  774:     */     {
/*  775: 933 */       this.m_FromLab.setText("Can't get results from experiment");
/*  776:     */     }
/*  777:     */   }
/*  778:     */   
/*  779:     */   protected void setInstancesFromDatabaseTable(String tableName)
/*  780:     */   {
/*  781:     */     try
/*  782:     */     {
/*  783: 946 */       this.m_FromLab.setText("Reading from database, please wait...");
/*  784: 947 */       final Instances i = this.m_InstanceQuery.retrieveInstances("SELECT * FROM " + tableName);
/*  785:     */       
/*  786: 949 */       SwingUtilities.invokeAndWait(new Runnable()
/*  787:     */       {
/*  788:     */         public void run()
/*  789:     */         {
/*  790: 952 */           ResultsPanel.this.setInstances(i);
/*  791:     */         }
/*  792: 954 */       });
/*  793: 955 */       this.m_InstanceQuery.disconnectFromDatabase();
/*  794:     */     }
/*  795:     */     catch (Exception ex)
/*  796:     */     {
/*  797: 957 */       this.m_FromLab.setText(ex.getMessage());
/*  798:     */     }
/*  799:     */   }
/*  800:     */   
/*  801:     */   protected void setInstancesFromFile(File f)
/*  802:     */   {
/*  803: 968 */     String fileType = f.getName();
/*  804:     */     try
/*  805:     */     {
/*  806: 970 */       this.m_FromLab.setText("Reading from file...");
/*  807: 971 */       if (f.getName().toLowerCase().endsWith(".arff"))
/*  808:     */       {
/*  809: 972 */         fileType = "arff";
/*  810: 973 */         Reader r = new BufferedReader(new FileReader(f));
/*  811: 974 */         setInstances(new Instances(r));
/*  812: 975 */         r.close();
/*  813:     */       }
/*  814: 976 */       else if (f.getName().toLowerCase().endsWith(CSVLoader.FILE_EXTENSION))
/*  815:     */       {
/*  816: 977 */         fileType = "csv";
/*  817: 978 */         CSVLoader cnv = new CSVLoader();
/*  818: 979 */         cnv.setSource(f);
/*  819: 980 */         Instances inst = cnv.getDataSet();
/*  820: 981 */         setInstances(inst);
/*  821:     */       }
/*  822:     */       else
/*  823:     */       {
/*  824: 983 */         throw new Exception("Unrecognized file type");
/*  825:     */       }
/*  826:     */     }
/*  827:     */     catch (Exception ex)
/*  828:     */     {
/*  829: 986 */       this.m_FromLab.setText("File '" + f.getName() + "' not recognised as an " + fileType + " file.");
/*  830: 988 */       if (JOptionPane.showOptionDialog(this, "File '" + f.getName() + "' not recognised as an " + fileType + " file.\n" + "Reason:\n" + ex.getMessage(), "Load Instances", 0, 0, null, new String[] { "OK" }, null) != 1) {}
/*  831:     */     }
/*  832:     */   }
/*  833:     */   
/*  834:     */   protected Vector<String> determineColumnNames(String list, String defaultList, Instances inst)
/*  835:     */   {
/*  836:1016 */     Vector<String> atts = new Vector();
/*  837:1017 */     for (int i = 0; i < inst.numAttributes(); i++) {
/*  838:1018 */       atts.add(inst.attribute(i).name().toLowerCase());
/*  839:     */     }
/*  840:1022 */     Vector<String> result = new Vector();
/*  841:1023 */     StringTokenizer tok = new StringTokenizer(list, ",");
/*  842:1024 */     while (tok.hasMoreTokens())
/*  843:     */     {
/*  844:1025 */       String item = tok.nextToken().toLowerCase();
/*  845:1026 */       if (atts.contains(item)) {
/*  846:1027 */         result.add(item);
/*  847:     */       } else {
/*  848:1029 */         result.clear();
/*  849:     */       }
/*  850:     */     }
/*  851:1035 */     if (result.size() == 0)
/*  852:     */     {
/*  853:1036 */       tok = new StringTokenizer(defaultList, ",");
/*  854:1037 */       while (tok.hasMoreTokens()) {
/*  855:1038 */         result.add(tok.nextToken().toLowerCase());
/*  856:     */       }
/*  857:     */     }
/*  858:1042 */     return result;
/*  859:     */   }
/*  860:     */   
/*  861:     */   public void setInstances(Instances newInstances)
/*  862:     */   {
/*  863:1053 */     this.m_Instances = newInstances;
/*  864:1054 */     this.m_TTester.setInstances(this.m_Instances);
/*  865:1055 */     this.m_FromLab.setText("Got " + this.m_Instances.numInstances() + " results");
/*  866:     */     
/*  867:     */ 
/*  868:1058 */     Vector<String> rows = determineColumnNames(ExperimenterDefaults.getRow(), "Key_Dataset", this.m_Instances);
/*  869:     */     
/*  870:     */ 
/*  871:1061 */     Vector<String> cols = determineColumnNames(ExperimenterDefaults.getColumn(), "Key_Scheme,Key_Scheme_options,Key_Scheme_version_ID", this.m_Instances);
/*  872:     */     
/*  873:     */ 
/*  874:     */ 
/*  875:     */ 
/*  876:1066 */     this.m_DatasetKeyModel.removeAllElements();
/*  877:1067 */     this.m_ResultKeyModel.removeAllElements();
/*  878:1068 */     this.m_CompareModel.removeAllElements();
/*  879:1069 */     this.m_SortModel.removeAllElements();
/*  880:1070 */     this.m_SortModel.addElement("<default>");
/*  881:1071 */     this.m_TTester.setSortColumn(-1);
/*  882:1072 */     String selectedList = "";
/*  883:1073 */     String selectedListDataset = "";
/*  884:1074 */     boolean comparisonFieldSet = false;
/*  885:1075 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++)
/*  886:     */     {
/*  887:1076 */       String name = this.m_Instances.attribute(i).name();
/*  888:1077 */       if (name.toLowerCase().startsWith("key_", 0))
/*  889:     */       {
/*  890:1078 */         this.m_DatasetKeyModel.addElement(name.substring(4));
/*  891:1079 */         this.m_ResultKeyModel.addElement(name.substring(4));
/*  892:1080 */         this.m_CompareModel.addElement(name.substring(4));
/*  893:     */       }
/*  894:     */       else
/*  895:     */       {
/*  896:1082 */         this.m_DatasetKeyModel.addElement(name);
/*  897:1083 */         this.m_ResultKeyModel.addElement(name);
/*  898:1084 */         this.m_CompareModel.addElement(name);
/*  899:1085 */         if (this.m_Instances.attribute(i).isNumeric()) {
/*  900:1086 */           this.m_SortModel.addElement(name);
/*  901:     */         }
/*  902:     */       }
/*  903:1090 */       if (rows.contains(name.toLowerCase()))
/*  904:     */       {
/*  905:1091 */         this.m_DatasetKeyList.addSelectionInterval(i, i);
/*  906:1092 */         selectedListDataset = selectedListDataset + "," + (i + 1);
/*  907:     */       }
/*  908:1093 */       else if (name.toLowerCase().equals("key_run"))
/*  909:     */       {
/*  910:1094 */         this.m_TTester.setRunColumn(i);
/*  911:     */       }
/*  912:1095 */       else if (name.toLowerCase().equals("key_fold"))
/*  913:     */       {
/*  914:1096 */         this.m_TTester.setFoldColumn(i);
/*  915:     */       }
/*  916:1097 */       else if (cols.contains(name.toLowerCase()))
/*  917:     */       {
/*  918:1098 */         this.m_ResultKeyList.addSelectionInterval(i, i);
/*  919:1099 */         selectedList = selectedList + "," + (i + 1);
/*  920:     */       }
/*  921:1100 */       else if (name.toLowerCase().indexOf(ExperimenterDefaults.getComparisonField()) != -1)
/*  922:     */       {
/*  923:1102 */         this.m_CompareCombo.setSelectedIndex(i);
/*  924:1103 */         comparisonFieldSet = true;
/*  925:     */       }
/*  926:1105 */       else if ((name.toLowerCase().indexOf("root_relative_squared_error") != -1) && (!comparisonFieldSet))
/*  927:     */       {
/*  928:1107 */         this.m_CompareCombo.setSelectedIndex(i);
/*  929:1108 */         comparisonFieldSet = true;
/*  930:     */       }
/*  931:     */     }
/*  932:1111 */     this.m_TesterClasses.setEnabled(true);
/*  933:1112 */     this.m_DatasetKeyBut.setEnabled(true);
/*  934:1113 */     this.m_ResultKeyBut.setEnabled(true);
/*  935:1114 */     this.m_SwapDatasetKeyAndResultKeyBut.setEnabled(true);
/*  936:1115 */     this.m_CompareCombo.setEnabled(true);
/*  937:1116 */     this.m_SortCombo.setEnabled(true);
/*  938:1117 */     if (ExperimenterDefaults.getSorting().length() != 0) {
/*  939:1118 */       setSelectedItem(this.m_SortCombo, ExperimenterDefaults.getSorting());
/*  940:     */     }
/*  941:1121 */     Range generatorRange = new Range();
/*  942:1122 */     if (selectedList.length() != 0) {
/*  943:     */       try
/*  944:     */       {
/*  945:1124 */         generatorRange.setRanges(selectedList);
/*  946:     */       }
/*  947:     */       catch (Exception ex)
/*  948:     */       {
/*  949:1126 */         ex.printStackTrace();
/*  950:1127 */         System.err.println(ex.getMessage());
/*  951:     */       }
/*  952:     */     }
/*  953:1130 */     this.m_TTester.setResultsetKeyColumns(generatorRange);
/*  954:     */     
/*  955:1132 */     generatorRange = new Range();
/*  956:1133 */     if (selectedListDataset.length() != 0) {
/*  957:     */       try
/*  958:     */       {
/*  959:1135 */         generatorRange.setRanges(selectedListDataset);
/*  960:     */       }
/*  961:     */       catch (Exception ex)
/*  962:     */       {
/*  963:1137 */         ex.printStackTrace();
/*  964:1138 */         System.err.println(ex.getMessage());
/*  965:     */       }
/*  966:     */     }
/*  967:1141 */     this.m_TTester.setDatasetKeyColumns(generatorRange);
/*  968:     */     
/*  969:1143 */     this.m_SigTex.setEnabled(true);
/*  970:     */     
/*  971:1145 */     setTTester();
/*  972:     */   }
/*  973:     */   
/*  974:     */   protected void setSelectedItem(JComboBox cb, String item)
/*  975:     */   {
/*  976:1158 */     for (int i = 0; i < cb.getItemCount(); i++) {
/*  977:1159 */       if (cb.getItemAt(i).toString().equals(item))
/*  978:     */       {
/*  979:1160 */         cb.setSelectedIndex(i);
/*  980:1161 */         break;
/*  981:     */       }
/*  982:     */     }
/*  983:     */   }
/*  984:     */   
/*  985:     */   protected void setTTester()
/*  986:     */   {
/*  987:1172 */     this.m_TTester.setDisplayedResultsets(null);
/*  988:     */     
/*  989:1174 */     String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date()) + "Available resultsets";
/*  990:     */     
/*  991:     */ 
/*  992:1177 */     StringBuffer outBuff = new StringBuffer();
/*  993:1178 */     outBuff.append("Available resultsets\n" + this.m_TTester.resultsetKey() + "\n\n");
/*  994:     */     
/*  995:1180 */     this.m_History.addResult(name, outBuff);
/*  996:1181 */     this.m_History.setSingle(name);
/*  997:     */     
/*  998:1183 */     this.m_TestsModel.removeAllElements();
/*  999:1184 */     for (int i = 0; i < this.m_TTester.getNumResultsets(); i++)
/* 1000:     */     {
/* 1001:1185 */       String tname = this.m_TTester.getResultsetName(i);
/* 1002:     */       
/* 1003:     */ 
/* 1004:     */ 
/* 1005:1189 */       this.m_TestsModel.addElement(tname);
/* 1006:     */     }
/* 1007:1192 */     this.m_DisplayedModel.removeAllElements();
/* 1008:1193 */     for (int i = 0; i < this.m_TestsModel.size(); i++) {
/* 1009:1194 */       this.m_DisplayedModel.addElement(this.m_TestsModel.elementAt(i));
/* 1010:     */     }
/* 1011:1197 */     this.m_TestsModel.addElement("Summary");
/* 1012:1198 */     this.m_TestsModel.addElement("Ranking");
/* 1013:     */     
/* 1014:1200 */     this.m_TestsList.setSelectedIndex(0);
/* 1015:1201 */     this.m_DisplayedList.setSelectionInterval(0, this.m_DisplayedModel.size() - 1);
/* 1016:     */     
/* 1017:1203 */     this.m_TestsButton.setEnabled(true);
/* 1018:1204 */     this.m_DisplayedButton.setEnabled(true);
/* 1019:1205 */     this.m_ShowStdDevs.setEnabled(true);
/* 1020:1206 */     this.m_OutputFormatButton.setEnabled(true);
/* 1021:1207 */     this.m_PerformBut.setEnabled(true);
/* 1022:1208 */     this.m_Explorer.setEnabled(true);
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   protected void performTest()
/* 1026:     */   {
/* 1027:1216 */     String sigStr = this.m_SigTex.getText();
/* 1028:1217 */     if (sigStr.length() != 0) {
/* 1029:1218 */       this.m_TTester.setSignificanceLevel(new Double(sigStr).doubleValue());
/* 1030:     */     } else {
/* 1031:1220 */       this.m_TTester.setSignificanceLevel(ExperimenterDefaults.getSignificance());
/* 1032:     */     }
/* 1033:1224 */     this.m_TTester.setShowStdDevs(this.m_ShowStdDevs.isSelected());
/* 1034:1225 */     if (this.m_Instances.attribute(this.m_SortCombo.getSelectedItem().toString()) != null) {
/* 1035:1226 */       this.m_TTester.setSortColumn(this.m_Instances.attribute(this.m_SortCombo.getSelectedItem().toString()).index());
/* 1036:     */     } else {
/* 1037:1229 */       this.m_TTester.setSortColumn(-1);
/* 1038:     */     }
/* 1039:1231 */     int compareCol = this.m_CompareCombo.getSelectedIndex();
/* 1040:1232 */     int tType = this.m_TestsList.getSelectedIndex();
/* 1041:     */     
/* 1042:1234 */     this.m_TTester.setResultMatrix(this.m_ResultMatrix);
/* 1043:1235 */     String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date()) + (String)this.m_CompareCombo.getSelectedItem() + " - " + (String)this.m_TestsList.getSelectedValue();
/* 1044:     */     
/* 1045:     */ 
/* 1046:     */ 
/* 1047:1239 */     StringBuffer outBuff = new StringBuffer();
/* 1048:1240 */     outBuff.append(this.m_TTester.header(compareCol));
/* 1049:1241 */     outBuff.append("\n");
/* 1050:1242 */     this.m_History.addResult(name, outBuff);
/* 1051:1243 */     this.m_History.setSingle(name);
/* 1052:1244 */     this.m_TTester.setDisplayedResultsets(this.m_DisplayedList.getSelectedIndices());
/* 1053:     */     try
/* 1054:     */     {
/* 1055:1246 */       if (tType < this.m_TTester.getNumResultsets()) {
/* 1056:1247 */         outBuff.append(this.m_TTester.multiResultsetFull(tType, compareCol));
/* 1057:1248 */       } else if (tType == this.m_TTester.getNumResultsets()) {
/* 1058:1249 */         outBuff.append(this.m_TTester.multiResultsetSummary(compareCol));
/* 1059:     */       } else {
/* 1060:1251 */         outBuff.append(this.m_TTester.multiResultsetRanking(compareCol));
/* 1061:     */       }
/* 1062:1253 */       outBuff.append("\n");
/* 1063:     */     }
/* 1064:     */     catch (Exception ex)
/* 1065:     */     {
/* 1066:1255 */       outBuff.append(ex.getMessage() + "\n");
/* 1067:     */     }
/* 1068:1257 */     this.m_History.updateResult(name);
/* 1069:     */   }
/* 1070:     */   
/* 1071:     */   public void setResultKeyFromDialog()
/* 1072:     */   {
/* 1073:1262 */     ListSelectorDialog jd = new ListSelectorDialog(null, this.m_ResultKeyList);
/* 1074:     */     
/* 1075:     */ 
/* 1076:1265 */     int result = jd.showDialog();
/* 1077:1268 */     if (result == 0)
/* 1078:     */     {
/* 1079:1269 */       int[] selected = this.m_ResultKeyList.getSelectedIndices();
/* 1080:1270 */       String selectedList = "";
/* 1081:1271 */       for (int element : selected) {
/* 1082:1272 */         selectedList = selectedList + "," + (element + 1);
/* 1083:     */       }
/* 1084:1274 */       Range generatorRange = new Range();
/* 1085:1275 */       if (selectedList.length() != 0) {
/* 1086:     */         try
/* 1087:     */         {
/* 1088:1277 */           generatorRange.setRanges(selectedList);
/* 1089:     */         }
/* 1090:     */         catch (Exception ex)
/* 1091:     */         {
/* 1092:1279 */           ex.printStackTrace();
/* 1093:1280 */           System.err.println(ex.getMessage());
/* 1094:     */         }
/* 1095:     */       }
/* 1096:1283 */       this.m_TTester.setResultsetKeyColumns(generatorRange);
/* 1097:1284 */       setTTester();
/* 1098:     */     }
/* 1099:     */   }
/* 1100:     */   
/* 1101:     */   public void setDatasetKeyFromDialog()
/* 1102:     */   {
/* 1103:1290 */     ListSelectorDialog jd = new ListSelectorDialog(null, this.m_DatasetKeyList);
/* 1104:     */     
/* 1105:     */ 
/* 1106:1293 */     int result = jd.showDialog();
/* 1107:1296 */     if (result == 0)
/* 1108:     */     {
/* 1109:1297 */       int[] selected = this.m_DatasetKeyList.getSelectedIndices();
/* 1110:1298 */       String selectedList = "";
/* 1111:1299 */       for (int element : selected) {
/* 1112:1300 */         selectedList = selectedList + "," + (element + 1);
/* 1113:     */       }
/* 1114:1302 */       Range generatorRange = new Range();
/* 1115:1303 */       if (selectedList.length() != 0) {
/* 1116:     */         try
/* 1117:     */         {
/* 1118:1305 */           generatorRange.setRanges(selectedList);
/* 1119:     */         }
/* 1120:     */         catch (Exception ex)
/* 1121:     */         {
/* 1122:1307 */           ex.printStackTrace();
/* 1123:1308 */           System.err.println(ex.getMessage());
/* 1124:     */         }
/* 1125:     */       }
/* 1126:1311 */       this.m_TTester.setDatasetKeyColumns(generatorRange);
/* 1127:1312 */       setTTester();
/* 1128:     */     }
/* 1129:     */   }
/* 1130:     */   
/* 1131:     */   protected void swapDatasetKeyAndResultKey()
/* 1132:     */   {
/* 1133:1324 */     int[] tmpSelected = this.m_DatasetKeyList.getSelectedIndices();
/* 1134:1325 */     this.m_DatasetKeyList.setSelectedIndices(this.m_ResultKeyList.getSelectedIndices());
/* 1135:1326 */     this.m_ResultKeyList.setSelectedIndices(tmpSelected);
/* 1136:     */     
/* 1137:     */ 
/* 1138:1329 */     Range tmpRange = this.m_TTester.getDatasetKeyColumns();
/* 1139:1330 */     this.m_TTester.setDatasetKeyColumns(this.m_TTester.getResultsetKeyColumns());
/* 1140:1331 */     this.m_TTester.setResultsetKeyColumns(tmpRange);
/* 1141:1332 */     setTTester();
/* 1142:     */   }
/* 1143:     */   
/* 1144:     */   public void setTestBaseFromDialog()
/* 1145:     */   {
/* 1146:1336 */     ListSelectorDialog jd = new ListSelectorDialog(null, this.m_TestsList);
/* 1147:     */     
/* 1148:     */ 
/* 1149:1339 */     jd.showDialog();
/* 1150:     */   }
/* 1151:     */   
/* 1152:     */   public void setDisplayedFromDialog()
/* 1153:     */   {
/* 1154:1343 */     ListSelectorDialog jd = new ListSelectorDialog(null, this.m_DisplayedList);
/* 1155:     */     
/* 1156:     */ 
/* 1157:1346 */     jd.showDialog();
/* 1158:     */   }
/* 1159:     */   
/* 1160:     */   public void setOutputFormatFromDialog()
/* 1161:     */   {
/* 1162:1354 */     OutputFormatDialog dialog = new OutputFormatDialog(PropertyDialog.getParentFrame(this));
/* 1163:     */     
/* 1164:     */ 
/* 1165:1357 */     this.m_ResultMatrix.setShowStdDev(this.m_ShowStdDevs.isSelected());
/* 1166:1358 */     dialog.setResultMatrix(this.m_ResultMatrix);
/* 1167:1359 */     dialog.setLocationRelativeTo(this);
/* 1168:1361 */     if (dialog.showDialog() == 0)
/* 1169:     */     {
/* 1170:1362 */       this.m_ResultMatrix = dialog.getResultMatrix();
/* 1171:1363 */       this.m_ShowStdDevs.setSelected(this.m_ResultMatrix.getShowStdDev());
/* 1172:     */     }
/* 1173:     */   }
/* 1174:     */   
/* 1175:     */   protected void saveBuffer()
/* 1176:     */   {
/* 1177:1371 */     StringBuffer sb = this.m_History.getSelectedBuffer();
/* 1178:1372 */     if (sb != null)
/* 1179:     */     {
/* 1180:1373 */       if (this.m_SaveOut.save(sb)) {
/* 1181:1374 */         JOptionPane.showMessageDialog(this, "File saved", "Results", 1);
/* 1182:     */       }
/* 1183:     */     }
/* 1184:     */     else {
/* 1185:1378 */       this.m_SaveOutBut.setEnabled(false);
/* 1186:     */     }
/* 1187:     */   }
/* 1188:     */   
/* 1189:     */   protected void setTester()
/* 1190:     */   {
/* 1191:1390 */     if (this.m_TesterClasses.getSelectedItem() == null) {
/* 1192:1391 */       return;
/* 1193:     */     }
/* 1194:1394 */     Tester tester = null;
/* 1195:     */     try
/* 1196:     */     {
/* 1197:1398 */       for (int i = 0; i < m_Testers.size(); i++)
/* 1198:     */       {
/* 1199:1399 */         Tester t = (Tester)((Class)m_Testers.get(i)).newInstance();
/* 1200:1400 */         if (t.getDisplayName().equals(this.m_TesterClasses.getSelectedItem()))
/* 1201:     */         {
/* 1202:1401 */           tester = t;
/* 1203:1402 */           break;
/* 1204:     */         }
/* 1205:     */       }
/* 1206:     */     }
/* 1207:     */     catch (Exception e)
/* 1208:     */     {
/* 1209:1406 */       e.printStackTrace();
/* 1210:     */     }
/* 1211:1409 */     if (tester == null)
/* 1212:     */     {
/* 1213:1410 */       tester = new PairedCorrectedTTester();
/* 1214:1411 */       this.m_TesterClasses.setSelectedItem(tester.getDisplayName());
/* 1215:     */     }
/* 1216:1414 */     tester.assign(this.m_TTester);
/* 1217:1415 */     this.m_TTester = tester;
/* 1218:1416 */     this.m_PerformBut.setToolTipText(this.m_TTester.getToolTipText());
/* 1219:1417 */     System.out.println("Tester set to: " + this.m_TTester.getClass().getName());
/* 1220:     */   }
/* 1221:     */   
/* 1222:     */   protected synchronized void openExplorer()
/* 1223:     */   {
/* 1224:1421 */     if (this.m_Instances != null) {
/* 1225:1422 */       if ((this.m_mainPerspective == null) || (!this.m_mainPerspective.acceptsInstances()))
/* 1226:     */       {
/* 1227:1423 */         Explorer exp = new Explorer();
/* 1228:1424 */         exp.getPreprocessPanel().setInstances(this.m_Instances);
/* 1229:     */         
/* 1230:1426 */         final JFrame jf = new JFrame("Weka Explorer");
/* 1231:1427 */         jf.getContentPane().setLayout(new BorderLayout());
/* 1232:1428 */         jf.getContentPane().add(exp, "Center");
/* 1233:1429 */         jf.addWindowListener(new WindowAdapter()
/* 1234:     */         {
/* 1235:     */           public void windowClosing(WindowEvent e)
/* 1236:     */           {
/* 1237:1432 */             jf.dispose();
/* 1238:     */           }
/* 1239:1434 */         });
/* 1240:1435 */         jf.pack();
/* 1241:1436 */         jf.setSize(800, 600);
/* 1242:1437 */         jf.setVisible(true);
/* 1243:1438 */         Image icon = Toolkit.getDefaultToolkit().getImage(exp.getClass().getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 1244:     */         
/* 1245:     */ 
/* 1246:     */ 
/* 1247:1442 */         jf.setIconImage(icon);
/* 1248:     */       }
/* 1249:     */       else
/* 1250:     */       {
/* 1251:1444 */         this.m_mainPerspective.setInstances(this.m_Instances);
/* 1252:1445 */         this.m_mainPerspective.getMainApplication().getPerspectiveManager().setActivePerspective(this.m_mainPerspective.getPerspectiveID());
/* 1253:     */       }
/* 1254:     */     }
/* 1255:     */   }
/* 1256:     */   
/* 1257:     */   public static void main(String[] args)
/* 1258:     */   {
/* 1259:     */     try
/* 1260:     */     {
/* 1261:1459 */       JFrame jf = new JFrame("Weka Experiment: Results Analysis");
/* 1262:1460 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1263:1461 */       ResultsPanel sp = new ResultsPanel();
/* 1264:     */       
/* 1265:1463 */       jf.getContentPane().add(sp, "Center");
/* 1266:1464 */       jf.addWindowListener(new WindowAdapter()
/* 1267:     */       {
/* 1268:     */         public void windowClosing(WindowEvent e)
/* 1269:     */         {
/* 1270:1467 */           this.val$jf.dispose();
/* 1271:1468 */           System.exit(0);
/* 1272:     */         }
/* 1273:1470 */       });
/* 1274:1471 */       jf.pack();
/* 1275:1472 */       jf.setSize(700, 550);
/* 1276:1473 */       jf.setVisible(true);
/* 1277:     */     }
/* 1278:     */     catch (Exception ex)
/* 1279:     */     {
/* 1280:1475 */       ex.printStackTrace();
/* 1281:1476 */       System.err.println(ex.getMessage());
/* 1282:     */     }
/* 1283:     */   }
/* 1284:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.ResultsPanel
 * JD-Core Version:    0.7.0.1
 */