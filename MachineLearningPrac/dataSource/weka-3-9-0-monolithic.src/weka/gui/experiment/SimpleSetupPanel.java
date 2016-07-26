/*    1:     */ package weka.gui.experiment;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.GridBagConstraints;
/*    6:     */ import java.awt.GridBagLayout;
/*    7:     */ import java.awt.GridLayout;
/*    8:     */ import java.awt.Insets;
/*    9:     */ import java.awt.event.ActionEvent;
/*   10:     */ import java.awt.event.ActionListener;
/*   11:     */ import java.awt.event.FocusAdapter;
/*   12:     */ import java.awt.event.FocusEvent;
/*   13:     */ import java.awt.event.KeyAdapter;
/*   14:     */ import java.awt.event.KeyEvent;
/*   15:     */ import java.awt.event.WindowAdapter;
/*   16:     */ import java.awt.event.WindowEvent;
/*   17:     */ import java.beans.IntrospectionException;
/*   18:     */ import java.beans.PropertyChangeListener;
/*   19:     */ import java.beans.PropertyChangeSupport;
/*   20:     */ import java.beans.PropertyDescriptor;
/*   21:     */ import java.io.File;
/*   22:     */ import java.io.PrintStream;
/*   23:     */ import javax.swing.BorderFactory;
/*   24:     */ import javax.swing.ButtonGroup;
/*   25:     */ import javax.swing.JButton;
/*   26:     */ import javax.swing.JComboBox;
/*   27:     */ import javax.swing.JFileChooser;
/*   28:     */ import javax.swing.JFrame;
/*   29:     */ import javax.swing.JLabel;
/*   30:     */ import javax.swing.JOptionPane;
/*   31:     */ import javax.swing.JPanel;
/*   32:     */ import javax.swing.JRadioButton;
/*   33:     */ import javax.swing.JScrollPane;
/*   34:     */ import javax.swing.JTextArea;
/*   35:     */ import javax.swing.JTextField;
/*   36:     */ import javax.swing.event.DocumentEvent;
/*   37:     */ import javax.swing.event.DocumentListener;
/*   38:     */ import javax.swing.filechooser.FileFilter;
/*   39:     */ import javax.swing.text.Document;
/*   40:     */ import weka.classifiers.Classifier;
/*   41:     */ import weka.core.xml.KOML;
/*   42:     */ import weka.experiment.CSVResultListener;
/*   43:     */ import weka.experiment.ClassifierSplitEvaluator;
/*   44:     */ import weka.experiment.CrossValidationResultProducer;
/*   45:     */ import weka.experiment.DatabaseResultListener;
/*   46:     */ import weka.experiment.Experiment;
/*   47:     */ import weka.experiment.InstancesResultListener;
/*   48:     */ import weka.experiment.PropertyNode;
/*   49:     */ import weka.experiment.RandomSplitResultProducer;
/*   50:     */ import weka.experiment.RegressionSplitEvaluator;
/*   51:     */ import weka.experiment.SplitEvaluator;
/*   52:     */ import weka.gui.DatabaseConnectionDialog;
/*   53:     */ import weka.gui.ExtensionFileFilter;
/*   54:     */ 
/*   55:     */ public class SimpleSetupPanel
/*   56:     */   extends AbstractSetupPanel
/*   57:     */ {
/*   58:     */   private static final long serialVersionUID = 5257424515609176509L;
/*   59:     */   protected Experiment m_Exp;
/*   60:  95 */   protected SetupModePanel m_modePanel = null;
/*   61:     */   protected String m_destinationDatabaseURL;
/*   62: 101 */   protected String m_destinationFilename = "";
/*   63: 104 */   protected int m_numFolds = 10;
/*   64: 107 */   protected double m_trainPercent = 66.0D;
/*   65: 110 */   protected int m_numRepetitions = 10;
/*   66:     */   protected boolean m_userHasBeenAskedAboutConversion;
/*   67: 116 */   protected ExtensionFileFilter m_csvFileFilter = new ExtensionFileFilter(".csv", "Comma separated value files");
/*   68: 120 */   protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(".arff", "ARFF files");
/*   69: 124 */   protected JButton m_OpenBut = new JButton("Open...");
/*   70: 127 */   protected JButton m_SaveBut = new JButton("Save...");
/*   71: 130 */   protected JButton m_NewBut = new JButton("New");
/*   72: 133 */   protected FileFilter m_ExpFilter = new ExtensionFileFilter(Experiment.FILE_EXTENSION, "Experiment configuration files (*" + Experiment.FILE_EXTENSION + ")");
/*   73: 138 */   protected FileFilter m_KOMLFilter = new ExtensionFileFilter(".koml", "Experiment configuration files (*.koml)");
/*   74: 143 */   protected FileFilter m_XMLFilter = new ExtensionFileFilter(".xml", "Experiment configuration files (*.xml)");
/*   75: 148 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*   76: 152 */   protected JFileChooser m_DestFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*   77: 156 */   protected JComboBox m_ResultsDestinationCBox = new JComboBox();
/*   78: 159 */   protected JLabel m_ResultsDestinationPathLabel = new JLabel("Filename:");
/*   79: 162 */   protected JTextField m_ResultsDestinationPathTField = new JTextField();
/*   80: 165 */   protected JButton m_BrowseDestinationButton = new JButton("Browse...");
/*   81: 168 */   protected JComboBox m_ExperimentTypeCBox = new JComboBox();
/*   82: 171 */   protected JLabel m_ExperimentParameterLabel = new JLabel("Number of folds:");
/*   83: 174 */   protected JTextField m_ExperimentParameterTField = new JTextField();
/*   84: 177 */   protected JRadioButton m_ExpClassificationRBut = new JRadioButton("Classification");
/*   85: 181 */   protected JRadioButton m_ExpRegressionRBut = new JRadioButton("Regression");
/*   86: 185 */   protected JTextField m_NumberOfRepetitionsTField = new JTextField();
/*   87: 188 */   protected JRadioButton m_OrderDatasetsFirstRBut = new JRadioButton("Data sets first");
/*   88: 192 */   protected JRadioButton m_OrderAlgorithmsFirstRBut = new JRadioButton("Algorithms first");
/*   89: 196 */   protected static String DEST_DATABASE_TEXT = "JDBC database";
/*   90: 197 */   protected static String DEST_ARFF_TEXT = "ARFF file";
/*   91: 198 */   protected static String DEST_CSV_TEXT = "CSV file";
/*   92: 199 */   protected static String TYPE_CROSSVALIDATION_TEXT = "Cross-validation";
/*   93: 200 */   protected static String TYPE_RANDOMSPLIT_TEXT = "Train/Test Percentage Split (data randomized)";
/*   94: 201 */   protected static String TYPE_FIXEDSPLIT_TEXT = "Train/Test Percentage Split (order preserved)";
/*   95: 204 */   protected DatasetListPanel m_DatasetListPanel = new DatasetListPanel();
/*   96: 207 */   protected AlgorithmListPanel m_AlgorithmListPanel = new AlgorithmListPanel();
/*   97: 210 */   protected JButton m_NotesButton = new JButton("Notes");
/*   98: 213 */   protected JFrame m_NotesFrame = new JFrame("Notes");
/*   99: 216 */   protected JTextArea m_NotesText = new JTextArea(null, 10, 0);
/*  100: 222 */   protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
/*  101:     */   
/*  102:     */   public SimpleSetupPanel(Experiment exp)
/*  103:     */   {
/*  104: 231 */     this();
/*  105: 232 */     setExperiment(exp);
/*  106:     */   }
/*  107:     */   
/*  108:     */   public SimpleSetupPanel()
/*  109:     */   {
/*  110: 241 */     this.m_ResultsDestinationCBox.setEnabled(false);
/*  111: 242 */     this.m_ResultsDestinationPathLabel.setEnabled(false);
/*  112: 243 */     this.m_ResultsDestinationPathTField.setEnabled(false);
/*  113: 244 */     this.m_BrowseDestinationButton.setEnabled(false);
/*  114: 245 */     this.m_ExperimentTypeCBox.setEnabled(false);
/*  115: 246 */     this.m_ExperimentParameterLabel.setEnabled(false);
/*  116: 247 */     this.m_ExperimentParameterTField.setEnabled(false);
/*  117: 248 */     this.m_ExpClassificationRBut.setEnabled(false);
/*  118: 249 */     this.m_ExpRegressionRBut.setEnabled(false);
/*  119: 250 */     this.m_NumberOfRepetitionsTField.setEnabled(false);
/*  120: 251 */     this.m_OrderDatasetsFirstRBut.setEnabled(false);
/*  121: 252 */     this.m_OrderAlgorithmsFirstRBut.setEnabled(false);
/*  122:     */     try
/*  123:     */     {
/*  124: 256 */       this.m_destinationDatabaseURL = new DatabaseResultListener().getDatabaseURL();
/*  125:     */     }
/*  126:     */     catch (Exception e) {}
/*  127: 260 */     this.m_NewBut.setMnemonic('N');
/*  128: 261 */     this.m_NewBut.addActionListener(new ActionListener()
/*  129:     */     {
/*  130:     */       public void actionPerformed(ActionEvent e)
/*  131:     */       {
/*  132: 263 */         Experiment newExp = new Experiment();
/*  133: 264 */         CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
/*  134: 265 */         cvrp.setNumFolds(10);
/*  135: 266 */         cvrp.setSplitEvaluator(new ClassifierSplitEvaluator());
/*  136: 267 */         newExp.setResultProducer(cvrp);
/*  137: 268 */         newExp.setPropertyArray(new Classifier[0]);
/*  138: 269 */         newExp.setUsePropertyIterator(true);
/*  139: 270 */         SimpleSetupPanel.this.setExperiment(newExp);
/*  140: 273 */         if (ExperimenterDefaults.getUseClassification()) {
/*  141: 274 */           SimpleSetupPanel.this.m_ExpClassificationRBut.setSelected(true);
/*  142:     */         } else {
/*  143: 276 */           SimpleSetupPanel.this.m_ExpRegressionRBut.setSelected(true);
/*  144:     */         }
/*  145: 278 */         SimpleSetupPanel.this.setSelectedItem(SimpleSetupPanel.this.m_ResultsDestinationCBox, ExperimenterDefaults.getDestination());
/*  146:     */         
/*  147: 280 */         SimpleSetupPanel.this.destinationTypeChanged();
/*  148:     */         
/*  149: 282 */         SimpleSetupPanel.this.setSelectedItem(SimpleSetupPanel.this.m_ExperimentTypeCBox, ExperimenterDefaults.getExperimentType());
/*  150:     */         
/*  151:     */ 
/*  152: 285 */         SimpleSetupPanel.this.m_numRepetitions = ExperimenterDefaults.getRepetitions();
/*  153: 286 */         SimpleSetupPanel.this.m_NumberOfRepetitionsTField.setText("" + SimpleSetupPanel.this.m_numRepetitions);
/*  154: 289 */         if (ExperimenterDefaults.getExperimentType().equals(SimpleSetupPanel.TYPE_CROSSVALIDATION_TEXT))
/*  155:     */         {
/*  156: 291 */           SimpleSetupPanel.this.m_numFolds = ExperimenterDefaults.getFolds();
/*  157: 292 */           SimpleSetupPanel.this.m_ExperimentParameterTField.setText("" + SimpleSetupPanel.this.m_numFolds);
/*  158:     */         }
/*  159:     */         else
/*  160:     */         {
/*  161: 296 */           SimpleSetupPanel.this.m_trainPercent = ExperimenterDefaults.getTrainPercentage();
/*  162: 297 */           SimpleSetupPanel.this.m_ExperimentParameterTField.setText("" + SimpleSetupPanel.this.m_trainPercent);
/*  163:     */         }
/*  164: 301 */         if (ExperimenterDefaults.getDatasetsFirst()) {
/*  165: 302 */           SimpleSetupPanel.this.m_OrderDatasetsFirstRBut.setSelected(true);
/*  166:     */         } else {
/*  167: 304 */           SimpleSetupPanel.this.m_OrderAlgorithmsFirstRBut.setSelected(true);
/*  168:     */         }
/*  169: 306 */         SimpleSetupPanel.this.expTypeChanged();
/*  170:     */       }
/*  171: 308 */     });
/*  172: 309 */     this.m_SaveBut.setEnabled(false);
/*  173: 310 */     this.m_SaveBut.setMnemonic('S');
/*  174: 311 */     this.m_SaveBut.addActionListener(new ActionListener()
/*  175:     */     {
/*  176:     */       public void actionPerformed(ActionEvent e)
/*  177:     */       {
/*  178: 313 */         SimpleSetupPanel.this.saveExperiment();
/*  179:     */       }
/*  180: 315 */     });
/*  181: 316 */     this.m_OpenBut.setMnemonic('O');
/*  182: 317 */     this.m_OpenBut.addActionListener(new ActionListener()
/*  183:     */     {
/*  184:     */       public void actionPerformed(ActionEvent e)
/*  185:     */       {
/*  186: 319 */         SimpleSetupPanel.this.openExperiment();
/*  187:     */       }
/*  188: 321 */     });
/*  189: 322 */     this.m_FileChooser.addChoosableFileFilter(this.m_ExpFilter);
/*  190: 323 */     if (KOML.isPresent()) {
/*  191: 324 */       this.m_FileChooser.addChoosableFileFilter(this.m_KOMLFilter);
/*  192:     */     }
/*  193: 325 */     this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
/*  194: 326 */     if (ExperimenterDefaults.getExtension().equals(".xml")) {
/*  195: 327 */       this.m_FileChooser.setFileFilter(this.m_XMLFilter);
/*  196: 328 */     } else if ((KOML.isPresent()) && (ExperimenterDefaults.getExtension().equals(".koml"))) {
/*  197: 329 */       this.m_FileChooser.setFileFilter(this.m_KOMLFilter);
/*  198:     */     } else {
/*  199: 331 */       this.m_FileChooser.setFileFilter(this.m_ExpFilter);
/*  200:     */     }
/*  201: 332 */     this.m_FileChooser.setFileSelectionMode(0);
/*  202: 333 */     this.m_DestFileChooser.setFileSelectionMode(0);
/*  203:     */     
/*  204: 335 */     this.m_BrowseDestinationButton.addActionListener(new ActionListener()
/*  205:     */     {
/*  206:     */       public void actionPerformed(ActionEvent e)
/*  207:     */       {
/*  208: 338 */         if (SimpleSetupPanel.this.m_ResultsDestinationCBox.getSelectedItem() == SimpleSetupPanel.DEST_DATABASE_TEXT) {
/*  209: 339 */           SimpleSetupPanel.this.chooseURLUsername();
/*  210:     */         } else {
/*  211: 341 */           SimpleSetupPanel.this.chooseDestinationFile();
/*  212:     */         }
/*  213:     */       }
/*  214: 345 */     });
/*  215: 346 */     this.m_ExpClassificationRBut.addActionListener(new ActionListener()
/*  216:     */     {
/*  217:     */       public void actionPerformed(ActionEvent e)
/*  218:     */       {
/*  219: 348 */         SimpleSetupPanel.this.expTypeChanged();
/*  220:     */       }
/*  221: 351 */     });
/*  222: 352 */     this.m_ExpRegressionRBut.addActionListener(new ActionListener()
/*  223:     */     {
/*  224:     */       public void actionPerformed(ActionEvent e)
/*  225:     */       {
/*  226: 354 */         SimpleSetupPanel.this.expTypeChanged();
/*  227:     */       }
/*  228: 357 */     });
/*  229: 358 */     this.m_OrderDatasetsFirstRBut.addActionListener(new ActionListener()
/*  230:     */     {
/*  231:     */       public void actionPerformed(ActionEvent e)
/*  232:     */       {
/*  233: 360 */         if (SimpleSetupPanel.this.m_Exp != null)
/*  234:     */         {
/*  235: 361 */           SimpleSetupPanel.this.m_Exp.setAdvanceDataSetFirst(true);
/*  236: 362 */           SimpleSetupPanel.this.m_Support.firePropertyChange("", null, null);
/*  237:     */         }
/*  238:     */       }
/*  239: 366 */     });
/*  240: 367 */     this.m_OrderAlgorithmsFirstRBut.addActionListener(new ActionListener()
/*  241:     */     {
/*  242:     */       public void actionPerformed(ActionEvent e)
/*  243:     */       {
/*  244: 369 */         if (SimpleSetupPanel.this.m_Exp != null)
/*  245:     */         {
/*  246: 370 */           SimpleSetupPanel.this.m_Exp.setAdvanceDataSetFirst(false);
/*  247: 371 */           SimpleSetupPanel.this.m_Support.firePropertyChange("", null, null);
/*  248:     */         }
/*  249:     */       }
/*  250: 375 */     });
/*  251: 376 */     this.m_ResultsDestinationPathTField.getDocument().addDocumentListener(new DocumentListener()
/*  252:     */     {
/*  253:     */       public void insertUpdate(DocumentEvent e)
/*  254:     */       {
/*  255: 377 */         SimpleSetupPanel.this.destinationAddressChanged();
/*  256:     */       }
/*  257:     */       
/*  258:     */       public void removeUpdate(DocumentEvent e)
/*  259:     */       {
/*  260: 378 */         SimpleSetupPanel.this.destinationAddressChanged();
/*  261:     */       }
/*  262:     */       
/*  263:     */       public void changedUpdate(DocumentEvent e)
/*  264:     */       {
/*  265: 379 */         SimpleSetupPanel.this.destinationAddressChanged();
/*  266:     */       }
/*  267: 381 */     });
/*  268: 382 */     this.m_ExperimentParameterTField.getDocument().addDocumentListener(new DocumentListener()
/*  269:     */     {
/*  270:     */       public void insertUpdate(DocumentEvent e)
/*  271:     */       {
/*  272: 383 */         SimpleSetupPanel.this.expParamChanged();
/*  273:     */       }
/*  274:     */       
/*  275:     */       public void removeUpdate(DocumentEvent e)
/*  276:     */       {
/*  277: 384 */         SimpleSetupPanel.this.expParamChanged();
/*  278:     */       }
/*  279:     */       
/*  280:     */       public void changedUpdate(DocumentEvent e)
/*  281:     */       {
/*  282: 385 */         SimpleSetupPanel.this.expParamChanged();
/*  283:     */       }
/*  284: 387 */     });
/*  285: 388 */     this.m_NumberOfRepetitionsTField.getDocument().addDocumentListener(new DocumentListener()
/*  286:     */     {
/*  287:     */       public void insertUpdate(DocumentEvent e)
/*  288:     */       {
/*  289: 389 */         SimpleSetupPanel.this.numRepetitionsChanged();
/*  290:     */       }
/*  291:     */       
/*  292:     */       public void removeUpdate(DocumentEvent e)
/*  293:     */       {
/*  294: 390 */         SimpleSetupPanel.this.numRepetitionsChanged();
/*  295:     */       }
/*  296:     */       
/*  297:     */       public void changedUpdate(DocumentEvent e)
/*  298:     */       {
/*  299: 391 */         SimpleSetupPanel.this.numRepetitionsChanged();
/*  300:     */       }
/*  301: 393 */     });
/*  302: 394 */     this.m_NotesFrame.addWindowListener(new WindowAdapter()
/*  303:     */     {
/*  304:     */       public void windowClosing(WindowEvent e)
/*  305:     */       {
/*  306: 396 */         SimpleSetupPanel.this.m_NotesButton.setEnabled(true);
/*  307:     */       }
/*  308: 398 */     });
/*  309: 399 */     this.m_NotesFrame.getContentPane().add(new JScrollPane(this.m_NotesText));
/*  310: 400 */     this.m_NotesFrame.setSize(600, 400);
/*  311:     */     
/*  312: 402 */     this.m_NotesButton.addActionListener(new ActionListener()
/*  313:     */     {
/*  314:     */       public void actionPerformed(ActionEvent e)
/*  315:     */       {
/*  316: 404 */         SimpleSetupPanel.this.m_NotesButton.setEnabled(false);
/*  317: 405 */         SimpleSetupPanel.this.m_NotesFrame.setVisible(true);
/*  318:     */       }
/*  319: 407 */     });
/*  320: 408 */     this.m_NotesButton.setEnabled(false);
/*  321:     */     
/*  322: 410 */     this.m_NotesText.setEditable(true);
/*  323:     */     
/*  324: 412 */     this.m_NotesText.addKeyListener(new KeyAdapter()
/*  325:     */     {
/*  326:     */       public void keyReleased(KeyEvent e)
/*  327:     */       {
/*  328: 414 */         SimpleSetupPanel.this.m_Exp.setNotes(SimpleSetupPanel.this.m_NotesText.getText());
/*  329:     */       }
/*  330: 416 */     });
/*  331: 417 */     this.m_NotesText.addFocusListener(new FocusAdapter()
/*  332:     */     {
/*  333:     */       public void focusLost(FocusEvent e)
/*  334:     */       {
/*  335: 419 */         SimpleSetupPanel.this.m_Exp.setNotes(SimpleSetupPanel.this.m_NotesText.getText());
/*  336:     */       }
/*  337: 423 */     });
/*  338: 424 */     JPanel buttons = new JPanel();
/*  339: 425 */     GridBagLayout gb = new GridBagLayout();
/*  340: 426 */     GridBagConstraints constraints = new GridBagConstraints();
/*  341: 427 */     buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/*  342: 428 */     buttons.setLayout(gb);
/*  343: 429 */     constraints.gridx = 0;constraints.gridy = 0;constraints.weightx = 5.0D;
/*  344: 430 */     constraints.fill = 2;
/*  345: 431 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/*  346: 432 */     constraints.insets = new Insets(0, 2, 0, 2);
/*  347: 433 */     buttons.add(this.m_OpenBut, constraints);
/*  348: 434 */     constraints.gridx = 1;constraints.gridy = 0;constraints.weightx = 5.0D;
/*  349: 435 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/*  350: 436 */     buttons.add(this.m_SaveBut, constraints);
/*  351: 437 */     constraints.gridx = 2;constraints.gridy = 0;constraints.weightx = 5.0D;
/*  352: 438 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/*  353: 439 */     buttons.add(this.m_NewBut, constraints);
/*  354:     */     
/*  355: 441 */     JPanel destName = new JPanel();
/*  356: 442 */     destName.setLayout(new BorderLayout(5, 5));
/*  357: 443 */     destName.add(this.m_ResultsDestinationPathLabel, "West");
/*  358: 444 */     destName.add(this.m_ResultsDestinationPathTField, "Center");
/*  359:     */     
/*  360: 446 */     this.m_ResultsDestinationCBox.addItem(DEST_ARFF_TEXT);
/*  361: 447 */     this.m_ResultsDestinationCBox.addItem(DEST_CSV_TEXT);
/*  362: 448 */     this.m_ResultsDestinationCBox.addItem(DEST_DATABASE_TEXT);
/*  363:     */     
/*  364: 450 */     this.m_ResultsDestinationCBox.addActionListener(new ActionListener()
/*  365:     */     {
/*  366:     */       public void actionPerformed(ActionEvent e)
/*  367:     */       {
/*  368: 452 */         SimpleSetupPanel.this.destinationTypeChanged();
/*  369:     */       }
/*  370: 455 */     });
/*  371: 456 */     JPanel destInner = new JPanel();
/*  372: 457 */     destInner.setLayout(new BorderLayout(5, 5));
/*  373: 458 */     destInner.add(this.m_ResultsDestinationCBox, "West");
/*  374: 459 */     destInner.add(destName, "Center");
/*  375: 460 */     destInner.add(this.m_BrowseDestinationButton, "East");
/*  376:     */     
/*  377: 462 */     JPanel dest = new JPanel();
/*  378: 463 */     dest.setLayout(new BorderLayout());
/*  379: 464 */     dest.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Results Destination"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  380:     */     
/*  381:     */ 
/*  382:     */ 
/*  383: 468 */     dest.add(destInner, "North");
/*  384:     */     
/*  385: 470 */     JPanel expParam = new JPanel();
/*  386: 471 */     expParam.setLayout(new BorderLayout(5, 5));
/*  387: 472 */     expParam.add(this.m_ExperimentParameterLabel, "West");
/*  388: 473 */     expParam.add(this.m_ExperimentParameterTField, "Center");
/*  389:     */     
/*  390: 475 */     ButtonGroup typeBG = new ButtonGroup();
/*  391: 476 */     typeBG.add(this.m_ExpClassificationRBut);
/*  392: 477 */     typeBG.add(this.m_ExpRegressionRBut);
/*  393: 478 */     this.m_ExpClassificationRBut.setSelected(true);
/*  394:     */     
/*  395: 480 */     JPanel typeRButtons = new JPanel();
/*  396: 481 */     typeRButtons.setLayout(new GridLayout(1, 0));
/*  397: 482 */     typeRButtons.add(this.m_ExpClassificationRBut);
/*  398: 483 */     typeRButtons.add(this.m_ExpRegressionRBut);
/*  399:     */     
/*  400: 485 */     this.m_ExperimentTypeCBox.addItem(TYPE_CROSSVALIDATION_TEXT);
/*  401: 486 */     this.m_ExperimentTypeCBox.addItem(TYPE_RANDOMSPLIT_TEXT);
/*  402: 487 */     this.m_ExperimentTypeCBox.addItem(TYPE_FIXEDSPLIT_TEXT);
/*  403:     */     
/*  404: 489 */     this.m_ExperimentTypeCBox.addActionListener(new ActionListener()
/*  405:     */     {
/*  406:     */       public void actionPerformed(ActionEvent e)
/*  407:     */       {
/*  408: 491 */         SimpleSetupPanel.this.expTypeChanged();
/*  409:     */       }
/*  410: 494 */     });
/*  411: 495 */     JPanel typeInner = new JPanel();
/*  412: 496 */     typeInner.setLayout(new GridLayout(0, 1));
/*  413: 497 */     typeInner.add(this.m_ExperimentTypeCBox);
/*  414: 498 */     typeInner.add(expParam);
/*  415: 499 */     typeInner.add(typeRButtons);
/*  416:     */     
/*  417: 501 */     JPanel type = new JPanel();
/*  418: 502 */     type.setLayout(new BorderLayout());
/*  419: 503 */     type.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Experiment Type"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  420:     */     
/*  421:     */ 
/*  422:     */ 
/*  423: 507 */     type.add(typeInner, "North");
/*  424:     */     
/*  425: 509 */     ButtonGroup iterBG = new ButtonGroup();
/*  426: 510 */     iterBG.add(this.m_OrderDatasetsFirstRBut);
/*  427: 511 */     iterBG.add(this.m_OrderAlgorithmsFirstRBut);
/*  428: 512 */     this.m_OrderDatasetsFirstRBut.setSelected(true);
/*  429:     */     
/*  430: 514 */     JPanel numIter = new JPanel();
/*  431: 515 */     numIter.setLayout(new BorderLayout(5, 5));
/*  432: 516 */     numIter.add(new JLabel("Number of repetitions:"), "West");
/*  433: 517 */     numIter.add(this.m_NumberOfRepetitionsTField, "Center");
/*  434:     */     
/*  435: 519 */     JPanel controlInner = new JPanel();
/*  436: 520 */     controlInner.setLayout(new GridLayout(0, 1));
/*  437: 521 */     controlInner.add(numIter);
/*  438: 522 */     controlInner.add(this.m_OrderDatasetsFirstRBut);
/*  439: 523 */     controlInner.add(this.m_OrderAlgorithmsFirstRBut);
/*  440:     */     
/*  441: 525 */     JPanel control = new JPanel();
/*  442: 526 */     control.setLayout(new BorderLayout());
/*  443: 527 */     control.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Iteration Control"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  444:     */     
/*  445:     */ 
/*  446:     */ 
/*  447: 531 */     control.add(controlInner, "North");
/*  448:     */     
/*  449: 533 */     JPanel type_control = new JPanel();
/*  450: 534 */     type_control.setLayout(new GridLayout(1, 0));
/*  451: 535 */     type_control.add(type);
/*  452: 536 */     type_control.add(control);
/*  453:     */     
/*  454: 538 */     JPanel notes = new JPanel();
/*  455: 539 */     notes.setLayout(new BorderLayout());
/*  456: 540 */     notes.add(this.m_NotesButton, "Center");
/*  457:     */     
/*  458: 542 */     JPanel top1 = new JPanel();
/*  459: 543 */     top1.setLayout(new BorderLayout());
/*  460: 544 */     top1.add(dest, "North");
/*  461: 545 */     top1.add(type_control, "Center");
/*  462:     */     
/*  463: 547 */     JPanel top = new JPanel();
/*  464: 548 */     top.setLayout(new BorderLayout());
/*  465: 549 */     top.add(buttons, "North");
/*  466: 550 */     top.add(top1, "Center");
/*  467:     */     
/*  468: 552 */     JPanel datasets = new JPanel();
/*  469: 553 */     datasets.setLayout(new BorderLayout());
/*  470: 554 */     datasets.add(this.m_DatasetListPanel, "Center");
/*  471:     */     
/*  472: 556 */     JPanel algorithms = new JPanel();
/*  473: 557 */     algorithms.setLayout(new BorderLayout());
/*  474: 558 */     algorithms.add(this.m_AlgorithmListPanel, "Center");
/*  475:     */     
/*  476: 560 */     JPanel schemes = new JPanel();
/*  477: 561 */     schemes.setLayout(new GridLayout(1, 0));
/*  478: 562 */     schemes.add(datasets);
/*  479: 563 */     schemes.add(algorithms);
/*  480:     */     
/*  481: 565 */     setLayout(new BorderLayout());
/*  482: 566 */     add(top, "North");
/*  483: 567 */     add(schemes, "Center");
/*  484: 568 */     add(notes, "South");
/*  485:     */   }
/*  486:     */   
/*  487:     */   public String getName()
/*  488:     */   {
/*  489: 577 */     return "Simple";
/*  490:     */   }
/*  491:     */   
/*  492:     */   protected void setSelectedItem(JComboBox cb, String item)
/*  493:     */   {
/*  494: 590 */     for (int i = 0; i < cb.getItemCount(); i++) {
/*  495: 591 */       if (cb.getItemAt(i).toString().equals(item))
/*  496:     */       {
/*  497: 592 */         cb.setSelectedIndex(i);
/*  498: 593 */         break;
/*  499:     */       }
/*  500:     */     }
/*  501:     */   }
/*  502:     */   
/*  503:     */   protected void removeNotesFrame()
/*  504:     */   {
/*  505: 602 */     this.m_NotesFrame.setVisible(false);
/*  506:     */   }
/*  507:     */   
/*  508:     */   private boolean userWantsToConvert()
/*  509:     */   {
/*  510: 612 */     if (this.m_userHasBeenAskedAboutConversion) {
/*  511: 612 */       return true;
/*  512:     */     }
/*  513: 613 */     this.m_userHasBeenAskedAboutConversion = true;
/*  514: 614 */     return JOptionPane.showConfirmDialog(this, "This experiment has settings that are too advanced\nto be represented in the simple setup mode.\nDo you want the experiment to be converted,\nlosing some of the advanced settings?\n", "Confirm conversion", 0, 2) == 0;
/*  515:     */   }
/*  516:     */   
/*  517:     */   public void setModePanel(SetupModePanel modePanel)
/*  518:     */   {
/*  519: 631 */     this.m_modePanel = modePanel;
/*  520:     */   }
/*  521:     */   
/*  522:     */   public boolean setExperiment(Experiment exp)
/*  523:     */   {
/*  524: 642 */     this.m_userHasBeenAskedAboutConversion = false;
/*  525: 643 */     this.m_Exp = null;
/*  526: 644 */     this.m_SaveBut.setEnabled(true);
/*  527: 646 */     if ((exp.getResultListener() instanceof DatabaseResultListener))
/*  528:     */     {
/*  529: 647 */       this.m_ResultsDestinationCBox.setSelectedItem(DEST_DATABASE_TEXT);
/*  530: 648 */       this.m_ResultsDestinationPathLabel.setText("URL:");
/*  531: 649 */       this.m_destinationDatabaseURL = ((DatabaseResultListener)exp.getResultListener()).getDatabaseURL();
/*  532: 650 */       this.m_ResultsDestinationPathTField.setText(this.m_destinationDatabaseURL);
/*  533: 651 */       this.m_BrowseDestinationButton.setEnabled(true);
/*  534:     */     }
/*  535: 652 */     else if ((exp.getResultListener() instanceof InstancesResultListener))
/*  536:     */     {
/*  537: 653 */       this.m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
/*  538: 654 */       this.m_ResultsDestinationPathLabel.setText("Filename:");
/*  539: 655 */       this.m_destinationFilename = ((InstancesResultListener)exp.getResultListener()).outputFileName();
/*  540: 656 */       this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
/*  541: 657 */       this.m_BrowseDestinationButton.setEnabled(true);
/*  542:     */     }
/*  543: 658 */     else if ((exp.getResultListener() instanceof CSVResultListener))
/*  544:     */     {
/*  545: 659 */       this.m_ResultsDestinationCBox.setSelectedItem(DEST_CSV_TEXT);
/*  546: 660 */       this.m_ResultsDestinationPathLabel.setText("Filename:");
/*  547: 661 */       this.m_destinationFilename = ((CSVResultListener)exp.getResultListener()).outputFileName();
/*  548: 662 */       this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
/*  549: 663 */       this.m_BrowseDestinationButton.setEnabled(true);
/*  550:     */     }
/*  551:     */     else
/*  552:     */     {
/*  553: 666 */       System.out.println("SimpleSetup incompatibility: unrecognised result destination");
/*  554: 667 */       if (userWantsToConvert())
/*  555:     */       {
/*  556: 668 */         this.m_ResultsDestinationCBox.setSelectedItem(DEST_ARFF_TEXT);
/*  557: 669 */         this.m_ResultsDestinationPathLabel.setText("Filename:");
/*  558: 670 */         this.m_destinationFilename = "";
/*  559: 671 */         this.m_ResultsDestinationPathTField.setText(this.m_destinationFilename);
/*  560: 672 */         this.m_BrowseDestinationButton.setEnabled(true);
/*  561:     */       }
/*  562:     */       else
/*  563:     */       {
/*  564: 674 */         return false;
/*  565:     */       }
/*  566:     */     }
/*  567: 677 */     this.m_ResultsDestinationCBox.setEnabled(true);
/*  568: 678 */     this.m_ResultsDestinationPathLabel.setEnabled(true);
/*  569: 679 */     this.m_ResultsDestinationPathTField.setEnabled(true);
/*  570: 681 */     if ((exp.getResultProducer() instanceof CrossValidationResultProducer))
/*  571:     */     {
/*  572: 682 */       CrossValidationResultProducer cvrp = (CrossValidationResultProducer)exp.getResultProducer();
/*  573: 683 */       this.m_numFolds = cvrp.getNumFolds();
/*  574: 684 */       this.m_ExperimentParameterTField.setText("" + this.m_numFolds);
/*  575: 686 */       if ((cvrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator))
/*  576:     */       {
/*  577: 687 */         this.m_ExpClassificationRBut.setSelected(true);
/*  578: 688 */         this.m_ExpRegressionRBut.setSelected(false);
/*  579:     */       }
/*  580: 689 */       else if ((cvrp.getSplitEvaluator() instanceof RegressionSplitEvaluator))
/*  581:     */       {
/*  582: 690 */         this.m_ExpClassificationRBut.setSelected(false);
/*  583: 691 */         this.m_ExpRegressionRBut.setSelected(true);
/*  584:     */       }
/*  585:     */       else
/*  586:     */       {
/*  587: 694 */         System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
/*  588: 695 */         if (userWantsToConvert())
/*  589:     */         {
/*  590: 696 */           this.m_ExpClassificationRBut.setSelected(true);
/*  591: 697 */           this.m_ExpRegressionRBut.setSelected(false);
/*  592:     */         }
/*  593:     */         else
/*  594:     */         {
/*  595: 699 */           return false;
/*  596:     */         }
/*  597:     */       }
/*  598: 702 */       this.m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
/*  599:     */     }
/*  600: 703 */     else if ((exp.getResultProducer() instanceof RandomSplitResultProducer))
/*  601:     */     {
/*  602: 704 */       RandomSplitResultProducer rsrp = (RandomSplitResultProducer)exp.getResultProducer();
/*  603: 705 */       if (rsrp.getRandomizeData()) {
/*  604: 706 */         this.m_ExperimentTypeCBox.setSelectedItem(TYPE_RANDOMSPLIT_TEXT);
/*  605:     */       } else {
/*  606: 708 */         this.m_ExperimentTypeCBox.setSelectedItem(TYPE_FIXEDSPLIT_TEXT);
/*  607:     */       }
/*  608: 710 */       if ((rsrp.getSplitEvaluator() instanceof ClassifierSplitEvaluator))
/*  609:     */       {
/*  610: 711 */         this.m_ExpClassificationRBut.setSelected(true);
/*  611: 712 */         this.m_ExpRegressionRBut.setSelected(false);
/*  612:     */       }
/*  613: 713 */       else if ((rsrp.getSplitEvaluator() instanceof RegressionSplitEvaluator))
/*  614:     */       {
/*  615: 714 */         this.m_ExpClassificationRBut.setSelected(false);
/*  616: 715 */         this.m_ExpRegressionRBut.setSelected(true);
/*  617:     */       }
/*  618:     */       else
/*  619:     */       {
/*  620: 718 */         System.out.println("SimpleSetup incompatibility: unrecognised split evaluator");
/*  621: 719 */         if (userWantsToConvert())
/*  622:     */         {
/*  623: 720 */           this.m_ExpClassificationRBut.setSelected(true);
/*  624: 721 */           this.m_ExpRegressionRBut.setSelected(false);
/*  625:     */         }
/*  626:     */         else
/*  627:     */         {
/*  628: 723 */           return false;
/*  629:     */         }
/*  630:     */       }
/*  631: 726 */       this.m_trainPercent = rsrp.getTrainPercent();
/*  632: 727 */       this.m_ExperimentParameterTField.setText("" + this.m_trainPercent);
/*  633:     */     }
/*  634:     */     else
/*  635:     */     {
/*  636: 731 */       System.out.println("SimpleSetup incompatibility: unrecognised resultProducer");
/*  637: 732 */       if (userWantsToConvert())
/*  638:     */       {
/*  639: 733 */         this.m_ExperimentTypeCBox.setSelectedItem(TYPE_CROSSVALIDATION_TEXT);
/*  640: 734 */         this.m_ExpClassificationRBut.setSelected(true);
/*  641: 735 */         this.m_ExpRegressionRBut.setSelected(false);
/*  642:     */       }
/*  643:     */       else
/*  644:     */       {
/*  645: 737 */         return false;
/*  646:     */       }
/*  647:     */     }
/*  648: 741 */     this.m_ExperimentTypeCBox.setEnabled(true);
/*  649: 742 */     this.m_ExperimentParameterLabel.setEnabled(true);
/*  650: 743 */     this.m_ExperimentParameterTField.setEnabled(true);
/*  651: 744 */     this.m_ExpClassificationRBut.setEnabled(true);
/*  652: 745 */     this.m_ExpRegressionRBut.setEnabled(true);
/*  653: 747 */     if (exp.getRunLower() == 1)
/*  654:     */     {
/*  655: 748 */       this.m_numRepetitions = exp.getRunUpper();
/*  656: 749 */       this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
/*  657:     */     }
/*  658:     */     else
/*  659:     */     {
/*  660: 752 */       System.out.println("SimpleSetup incompatibility: runLower is not 1");
/*  661: 753 */       if (userWantsToConvert())
/*  662:     */       {
/*  663: 754 */         exp.setRunLower(1);
/*  664: 755 */         if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT)
/*  665:     */         {
/*  666: 756 */           exp.setRunUpper(1);
/*  667: 757 */           this.m_NumberOfRepetitionsTField.setEnabled(false);
/*  668: 758 */           this.m_NumberOfRepetitionsTField.setText("1");
/*  669:     */         }
/*  670:     */         else
/*  671:     */         {
/*  672: 760 */           exp.setRunUpper(10);
/*  673: 761 */           this.m_numRepetitions = 10;
/*  674: 762 */           this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
/*  675:     */         }
/*  676:     */       }
/*  677:     */       else
/*  678:     */       {
/*  679: 766 */         return false;
/*  680:     */       }
/*  681:     */     }
/*  682: 769 */     this.m_NumberOfRepetitionsTField.setEnabled(true);
/*  683:     */     
/*  684: 771 */     this.m_OrderDatasetsFirstRBut.setSelected(exp.getAdvanceDataSetFirst());
/*  685: 772 */     this.m_OrderAlgorithmsFirstRBut.setSelected(!exp.getAdvanceDataSetFirst());
/*  686: 773 */     this.m_OrderDatasetsFirstRBut.setEnabled(true);
/*  687: 774 */     this.m_OrderAlgorithmsFirstRBut.setEnabled(true);
/*  688:     */     
/*  689: 776 */     this.m_NotesText.setText(exp.getNotes());
/*  690: 777 */     this.m_NotesButton.setEnabled(true);
/*  691: 779 */     if ((!exp.getUsePropertyIterator()) || (!(exp.getPropertyArray() instanceof Classifier[])))
/*  692:     */     {
/*  693: 781 */       System.out.println("SimpleSetup incompatibility: unrecognised property iteration");
/*  694: 782 */       if (userWantsToConvert())
/*  695:     */       {
/*  696: 783 */         exp.setPropertyArray(new Classifier[0]);
/*  697: 784 */         exp.setUsePropertyIterator(true);
/*  698:     */       }
/*  699:     */       else
/*  700:     */       {
/*  701: 786 */         return false;
/*  702:     */       }
/*  703:     */     }
/*  704: 790 */     this.m_DatasetListPanel.setExperiment(exp);
/*  705: 791 */     this.m_AlgorithmListPanel.setExperiment(exp);
/*  706:     */     
/*  707: 793 */     this.m_Exp = exp;
/*  708: 794 */     expTypeChanged();
/*  709:     */     
/*  710: 796 */     this.m_Support.firePropertyChange("", null, null);
/*  711:     */     
/*  712: 798 */     return true;
/*  713:     */   }
/*  714:     */   
/*  715:     */   public Experiment getExperiment()
/*  716:     */   {
/*  717: 808 */     return this.m_Exp;
/*  718:     */   }
/*  719:     */   
/*  720:     */   private void openExperiment()
/*  721:     */   {
/*  722: 816 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/*  723: 817 */     if (returnVal != 0) {
/*  724: 818 */       return;
/*  725:     */     }
/*  726: 820 */     File expFile = this.m_FileChooser.getSelectedFile();
/*  727: 823 */     if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter)
/*  728:     */     {
/*  729: 824 */       if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION)) {
/*  730: 825 */         expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
/*  731:     */       }
/*  732:     */     }
/*  733: 827 */     else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/*  734:     */     {
/*  735: 828 */       if (!expFile.getName().toLowerCase().endsWith(".koml")) {
/*  736: 829 */         expFile = new File(expFile.getParent(), expFile.getName() + ".koml");
/*  737:     */       }
/*  738:     */     }
/*  739: 831 */     else if ((this.m_FileChooser.getFileFilter() == this.m_XMLFilter) && 
/*  740: 832 */       (!expFile.getName().toLowerCase().endsWith(".xml"))) {
/*  741: 833 */       expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
/*  742:     */     }
/*  743:     */     try
/*  744:     */     {
/*  745: 837 */       Experiment exp = Experiment.read(expFile.getAbsolutePath());
/*  746: 838 */       if ((!setExperiment(exp)) && 
/*  747: 839 */         (this.m_modePanel != null)) {
/*  748: 839 */         this.m_modePanel.switchToAdvanced(exp);
/*  749:     */       }
/*  750: 841 */       System.err.println("Opened experiment:\n" + exp);
/*  751:     */     }
/*  752:     */     catch (Exception ex)
/*  753:     */     {
/*  754: 843 */       ex.printStackTrace();
/*  755: 844 */       JOptionPane.showMessageDialog(this, "Couldn't open experiment file:\n" + expFile + "\nReason:\n" + ex.getMessage(), "Open Experiment", 0);
/*  756:     */     }
/*  757:     */   }
/*  758:     */   
/*  759:     */   private void saveExperiment()
/*  760:     */   {
/*  761: 859 */     int returnVal = this.m_FileChooser.showSaveDialog(this);
/*  762: 860 */     if (returnVal != 0) {
/*  763: 861 */       return;
/*  764:     */     }
/*  765: 863 */     File expFile = this.m_FileChooser.getSelectedFile();
/*  766: 866 */     if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter)
/*  767:     */     {
/*  768: 867 */       if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION)) {
/*  769: 868 */         expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
/*  770:     */       }
/*  771:     */     }
/*  772: 870 */     else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/*  773:     */     {
/*  774: 871 */       if (!expFile.getName().toLowerCase().endsWith(".koml")) {
/*  775: 872 */         expFile = new File(expFile.getParent(), expFile.getName() + ".koml");
/*  776:     */       }
/*  777:     */     }
/*  778: 874 */     else if ((this.m_FileChooser.getFileFilter() == this.m_XMLFilter) && 
/*  779: 875 */       (!expFile.getName().toLowerCase().endsWith(".xml"))) {
/*  780: 876 */       expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
/*  781:     */     }
/*  782:     */     try
/*  783:     */     {
/*  784: 880 */       Experiment.write(expFile.getAbsolutePath(), this.m_Exp);
/*  785: 881 */       System.err.println("Saved experiment:\n" + this.m_Exp);
/*  786:     */     }
/*  787:     */     catch (Exception ex)
/*  788:     */     {
/*  789: 883 */       ex.printStackTrace();
/*  790: 884 */       JOptionPane.showMessageDialog(this, "Couldn't save experiment file:\n" + expFile + "\nReason:\n" + ex.getMessage(), "Save Experiment", 0);
/*  791:     */     }
/*  792:     */   }
/*  793:     */   
/*  794:     */   public void addPropertyChangeListener(PropertyChangeListener l)
/*  795:     */   {
/*  796: 898 */     if ((this.m_Support != null) && (l != null)) {
/*  797: 899 */       this.m_Support.addPropertyChangeListener(l);
/*  798:     */     }
/*  799:     */   }
/*  800:     */   
/*  801:     */   public void removePropertyChangeListener(PropertyChangeListener l)
/*  802:     */   {
/*  803: 909 */     if ((this.m_Support != null) && (l != null)) {
/*  804: 910 */       this.m_Support.removePropertyChangeListener(l);
/*  805:     */     }
/*  806:     */   }
/*  807:     */   
/*  808:     */   private void destinationTypeChanged()
/*  809:     */   {
/*  810: 919 */     if (this.m_Exp == null) {
/*  811: 919 */       return;
/*  812:     */     }
/*  813: 921 */     String str = "";
/*  814: 923 */     if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT)
/*  815:     */     {
/*  816: 924 */       this.m_ResultsDestinationPathLabel.setText("URL:");
/*  817: 925 */       str = this.m_destinationDatabaseURL;
/*  818: 926 */       this.m_BrowseDestinationButton.setEnabled(true);
/*  819: 927 */       this.m_BrowseDestinationButton.setText("User...");
/*  820:     */     }
/*  821:     */     else
/*  822:     */     {
/*  823: 929 */       this.m_ResultsDestinationPathLabel.setText("Filename:");
/*  824: 930 */       if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT)
/*  825:     */       {
/*  826: 931 */         int ind = this.m_destinationFilename.lastIndexOf(".csv");
/*  827: 932 */         if (ind > -1) {
/*  828: 933 */           this.m_destinationFilename = (this.m_destinationFilename.substring(0, ind) + ".arff");
/*  829:     */         }
/*  830:     */       }
/*  831: 936 */       if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT)
/*  832:     */       {
/*  833: 937 */         int ind = this.m_destinationFilename.lastIndexOf(".arff");
/*  834: 938 */         if (ind > -1) {
/*  835: 939 */           this.m_destinationFilename = (this.m_destinationFilename.substring(0, ind) + ".csv");
/*  836:     */         }
/*  837:     */       }
/*  838: 942 */       str = this.m_destinationFilename;
/*  839: 943 */       if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT)
/*  840:     */       {
/*  841: 944 */         int ind = str.lastIndexOf(".csv");
/*  842: 945 */         if (ind > -1) {
/*  843: 946 */           str = str.substring(0, ind) + ".arff";
/*  844:     */         }
/*  845:     */       }
/*  846: 949 */       if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT)
/*  847:     */       {
/*  848: 950 */         int ind = str.lastIndexOf(".arff");
/*  849: 951 */         if (ind > -1) {
/*  850: 952 */           str = str.substring(0, ind) + ".csv";
/*  851:     */         }
/*  852:     */       }
/*  853: 955 */       this.m_BrowseDestinationButton.setEnabled(true);
/*  854: 956 */       this.m_BrowseDestinationButton.setText("Browse...");
/*  855:     */     }
/*  856: 959 */     if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT)
/*  857:     */     {
/*  858: 960 */       DatabaseResultListener drl = null;
/*  859:     */       try
/*  860:     */       {
/*  861: 962 */         drl = new DatabaseResultListener();
/*  862:     */       }
/*  863:     */       catch (Exception e)
/*  864:     */       {
/*  865: 964 */         e.printStackTrace();
/*  866:     */       }
/*  867: 966 */       drl.setDatabaseURL(this.m_destinationDatabaseURL);
/*  868: 967 */       this.m_Exp.setResultListener(drl);
/*  869:     */     }
/*  870: 969 */     else if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT)
/*  871:     */     {
/*  872: 970 */       InstancesResultListener irl = new InstancesResultListener();
/*  873: 971 */       if (!this.m_destinationFilename.equals("")) {
/*  874: 972 */         irl.setOutputFile(new File(this.m_destinationFilename));
/*  875:     */       }
/*  876: 974 */       this.m_Exp.setResultListener(irl);
/*  877:     */     }
/*  878: 975 */     else if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT)
/*  879:     */     {
/*  880: 976 */       CSVResultListener crl = new CSVResultListener();
/*  881: 977 */       if (!this.m_destinationFilename.equals("")) {
/*  882: 978 */         crl.setOutputFile(new File(this.m_destinationFilename));
/*  883:     */       }
/*  884: 980 */       this.m_Exp.setResultListener(crl);
/*  885:     */     }
/*  886: 984 */     this.m_ResultsDestinationPathTField.setText(str);
/*  887:     */     
/*  888: 986 */     this.m_Support.firePropertyChange("", null, null);
/*  889:     */   }
/*  890:     */   
/*  891:     */   private void destinationAddressChanged()
/*  892:     */   {
/*  893: 994 */     if (this.m_Exp == null) {
/*  894: 994 */       return;
/*  895:     */     }
/*  896: 996 */     if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_DATABASE_TEXT)
/*  897:     */     {
/*  898: 997 */       this.m_destinationDatabaseURL = this.m_ResultsDestinationPathTField.getText();
/*  899: 998 */       if ((this.m_Exp.getResultListener() instanceof DatabaseResultListener)) {
/*  900: 999 */         ((DatabaseResultListener)this.m_Exp.getResultListener()).setDatabaseURL(this.m_destinationDatabaseURL);
/*  901:     */       }
/*  902:     */     }
/*  903:     */     else
/*  904:     */     {
/*  905:1002 */       File resultsFile = null;
/*  906:1003 */       this.m_destinationFilename = this.m_ResultsDestinationPathTField.getText();
/*  907:1006 */       if (this.m_destinationFilename.equals(""))
/*  908:     */       {
/*  909:     */         try
/*  910:     */         {
/*  911:1008 */           if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) {
/*  912:1009 */             resultsFile = File.createTempFile("weka_experiment", ".arff");
/*  913:     */           }
/*  914:1011 */           if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
/*  915:1012 */             resultsFile = File.createTempFile("weka_experiment", ".csv");
/*  916:     */           }
/*  917:1014 */           resultsFile.deleteOnExit();
/*  918:     */         }
/*  919:     */         catch (Exception e)
/*  920:     */         {
/*  921:1016 */           System.err.println("Cannot create temp file, writing to standard out.");
/*  922:1017 */           resultsFile = new File("-");
/*  923:     */         }
/*  924:     */       }
/*  925:     */       else
/*  926:     */       {
/*  927:1020 */         if ((this.m_ResultsDestinationCBox.getSelectedItem() == DEST_ARFF_TEXT) && 
/*  928:1021 */           (!this.m_destinationFilename.endsWith(".arff"))) {
/*  929:1022 */           this.m_destinationFilename += ".arff";
/*  930:     */         }
/*  931:1025 */         if ((this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) && 
/*  932:1026 */           (!this.m_destinationFilename.endsWith(".csv"))) {
/*  933:1027 */           this.m_destinationFilename += ".csv";
/*  934:     */         }
/*  935:1030 */         resultsFile = new File(this.m_destinationFilename);
/*  936:     */       }
/*  937:1032 */       ((CSVResultListener)this.m_Exp.getResultListener()).setOutputFile(resultsFile);
/*  938:1033 */       ((CSVResultListener)this.m_Exp.getResultListener()).setOutputFileName(this.m_destinationFilename);
/*  939:     */     }
/*  940:1036 */     this.m_Support.firePropertyChange("", null, null);
/*  941:     */   }
/*  942:     */   
/*  943:     */   private void expTypeChanged()
/*  944:     */   {
/*  945:1044 */     if (this.m_Exp == null) {
/*  946:1044 */       return;
/*  947:     */     }
/*  948:1047 */     if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT)
/*  949:     */     {
/*  950:1048 */       this.m_ExperimentParameterLabel.setText("Number of folds:");
/*  951:1049 */       this.m_ExperimentParameterTField.setText("" + this.m_numFolds);
/*  952:     */     }
/*  953:     */     else
/*  954:     */     {
/*  955:1051 */       this.m_ExperimentParameterLabel.setText("Train percentage:");
/*  956:1052 */       this.m_ExperimentParameterTField.setText("" + this.m_trainPercent);
/*  957:     */     }
/*  958:1056 */     if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_FIXEDSPLIT_TEXT)
/*  959:     */     {
/*  960:1057 */       this.m_NumberOfRepetitionsTField.setEnabled(false);
/*  961:1058 */       this.m_NumberOfRepetitionsTField.setText("1");
/*  962:1059 */       this.m_Exp.setRunLower(1);
/*  963:1060 */       this.m_Exp.setRunUpper(1);
/*  964:     */     }
/*  965:     */     else
/*  966:     */     {
/*  967:1062 */       this.m_NumberOfRepetitionsTField.setText("" + this.m_numRepetitions);
/*  968:1063 */       this.m_NumberOfRepetitionsTField.setEnabled(true);
/*  969:1064 */       this.m_Exp.setRunLower(1);
/*  970:1065 */       this.m_Exp.setRunUpper(this.m_numRepetitions);
/*  971:     */     }
/*  972:1068 */     SplitEvaluator se = null;
/*  973:1069 */     Classifier sec = null;
/*  974:1070 */     if (this.m_ExpClassificationRBut.isSelected())
/*  975:     */     {
/*  976:1071 */       se = new ClassifierSplitEvaluator();
/*  977:1072 */       sec = ((ClassifierSplitEvaluator)se).getClassifier();
/*  978:     */     }
/*  979:     */     else
/*  980:     */     {
/*  981:1074 */       se = new RegressionSplitEvaluator();
/*  982:1075 */       sec = ((RegressionSplitEvaluator)se).getClassifier();
/*  983:     */     }
/*  984:1079 */     if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT)
/*  985:     */     {
/*  986:1080 */       CrossValidationResultProducer cvrp = new CrossValidationResultProducer();
/*  987:1081 */       cvrp.setNumFolds(this.m_numFolds);
/*  988:1082 */       cvrp.setSplitEvaluator(se);
/*  989:     */       
/*  990:1084 */       PropertyNode[] propertyPath = new PropertyNode[2];
/*  991:     */       try
/*  992:     */       {
/*  993:1086 */         propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator", CrossValidationResultProducer.class), CrossValidationResultProducer.class);
/*  994:     */         
/*  995:     */ 
/*  996:1089 */         propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()), se.getClass());
/*  997:     */       }
/*  998:     */       catch (IntrospectionException e)
/*  999:     */       {
/* 1000:1093 */         e.printStackTrace();
/* 1001:     */       }
/* 1002:1096 */       this.m_Exp.setResultProducer(cvrp);
/* 1003:1097 */       this.m_Exp.setPropertyPath(propertyPath);
/* 1004:     */     }
/* 1005:     */     else
/* 1006:     */     {
/* 1007:1100 */       RandomSplitResultProducer rsrp = new RandomSplitResultProducer();
/* 1008:1101 */       rsrp.setRandomizeData(this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_RANDOMSPLIT_TEXT);
/* 1009:1102 */       rsrp.setTrainPercent(this.m_trainPercent);
/* 1010:1103 */       rsrp.setSplitEvaluator(se);
/* 1011:     */       
/* 1012:1105 */       PropertyNode[] propertyPath = new PropertyNode[2];
/* 1013:     */       try
/* 1014:     */       {
/* 1015:1107 */         propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator", RandomSplitResultProducer.class), RandomSplitResultProducer.class);
/* 1016:     */         
/* 1017:     */ 
/* 1018:1110 */         propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()), se.getClass());
/* 1019:     */       }
/* 1020:     */       catch (IntrospectionException e)
/* 1021:     */       {
/* 1022:1114 */         e.printStackTrace();
/* 1023:     */       }
/* 1024:1117 */       this.m_Exp.setResultProducer(rsrp);
/* 1025:1118 */       this.m_Exp.setPropertyPath(propertyPath);
/* 1026:     */     }
/* 1027:1122 */     this.m_Exp.setUsePropertyIterator(true);
/* 1028:1123 */     this.m_Support.firePropertyChange("", null, null);
/* 1029:     */   }
/* 1030:     */   
/* 1031:     */   private void expParamChanged()
/* 1032:     */   {
/* 1033:1131 */     if (this.m_Exp == null) {
/* 1034:1131 */       return;
/* 1035:     */     }
/* 1036:1133 */     if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT) {
/* 1037:     */       try
/* 1038:     */       {
/* 1039:1135 */         this.m_numFolds = Integer.parseInt(this.m_ExperimentParameterTField.getText());
/* 1040:     */       }
/* 1041:     */       catch (NumberFormatException e)
/* 1042:     */       {
/* 1043:1137 */         return;
/* 1044:     */       }
/* 1045:     */     } else {
/* 1046:     */       try
/* 1047:     */       {
/* 1048:1141 */         this.m_trainPercent = Double.parseDouble(this.m_ExperimentParameterTField.getText());
/* 1049:     */       }
/* 1050:     */       catch (NumberFormatException e)
/* 1051:     */       {
/* 1052:1143 */         return;
/* 1053:     */       }
/* 1054:     */     }
/* 1055:1147 */     if (this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_CROSSVALIDATION_TEXT)
/* 1056:     */     {
/* 1057:1149 */       if ((this.m_Exp.getResultProducer() instanceof CrossValidationResultProducer))
/* 1058:     */       {
/* 1059:1150 */         CrossValidationResultProducer cvrp = (CrossValidationResultProducer)this.m_Exp.getResultProducer();
/* 1060:1151 */         cvrp.setNumFolds(this.m_numFolds);
/* 1061:     */       }
/* 1062:     */     }
/* 1063:1158 */     else if ((this.m_Exp.getResultProducer() instanceof RandomSplitResultProducer))
/* 1064:     */     {
/* 1065:1159 */       RandomSplitResultProducer rsrp = (RandomSplitResultProducer)this.m_Exp.getResultProducer();
/* 1066:1160 */       rsrp.setRandomizeData(this.m_ExperimentTypeCBox.getSelectedItem() == TYPE_RANDOMSPLIT_TEXT);
/* 1067:1161 */       rsrp.setTrainPercent(this.m_trainPercent);
/* 1068:     */     }
/* 1069:     */     else
/* 1070:     */     {
/* 1071:1164 */       return;
/* 1072:     */     }
/* 1073:1168 */     this.m_Support.firePropertyChange("", null, null);
/* 1074:     */   }
/* 1075:     */   
/* 1076:     */   private void numRepetitionsChanged()
/* 1077:     */   {
/* 1078:1176 */     if ((this.m_Exp == null) || (!this.m_NumberOfRepetitionsTField.isEnabled())) {
/* 1079:1176 */       return;
/* 1080:     */     }
/* 1081:     */     try
/* 1082:     */     {
/* 1083:1179 */       this.m_numRepetitions = Integer.parseInt(this.m_NumberOfRepetitionsTField.getText());
/* 1084:     */     }
/* 1085:     */     catch (NumberFormatException e)
/* 1086:     */     {
/* 1087:1181 */       return;
/* 1088:     */     }
/* 1089:1184 */     this.m_Exp.setRunLower(1);
/* 1090:1185 */     this.m_Exp.setRunUpper(this.m_numRepetitions);
/* 1091:     */     
/* 1092:1187 */     this.m_Support.firePropertyChange("", null, null);
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   private void chooseURLUsername()
/* 1096:     */   {
/* 1097:1194 */     String dbaseURL = ((DatabaseResultListener)this.m_Exp.getResultListener()).getDatabaseURL();
/* 1098:1195 */     String username = ((DatabaseResultListener)this.m_Exp.getResultListener()).getUsername();
/* 1099:1196 */     DatabaseConnectionDialog dbd = new DatabaseConnectionDialog(null, dbaseURL, username);
/* 1100:1197 */     dbd.setVisible(true);
/* 1101:1200 */     if (dbd.getReturnValue() == -1) {
/* 1102:1201 */       return;
/* 1103:     */     }
/* 1104:1204 */     ((DatabaseResultListener)this.m_Exp.getResultListener()).setUsername(dbd.getUsername());
/* 1105:1205 */     ((DatabaseResultListener)this.m_Exp.getResultListener()).setPassword(dbd.getPassword());
/* 1106:1206 */     ((DatabaseResultListener)this.m_Exp.getResultListener()).setDatabaseURL(dbd.getURL());
/* 1107:1207 */     ((DatabaseResultListener)this.m_Exp.getResultListener()).setDebug(dbd.getDebug());
/* 1108:1208 */     this.m_ResultsDestinationPathTField.setText(dbd.getURL());
/* 1109:     */   }
/* 1110:     */   
/* 1111:     */   private void chooseDestinationFile()
/* 1112:     */   {
/* 1113:1215 */     FileFilter fileFilter = null;
/* 1114:1216 */     if (this.m_ResultsDestinationCBox.getSelectedItem() == DEST_CSV_TEXT) {
/* 1115:1217 */       fileFilter = this.m_csvFileFilter;
/* 1116:     */     } else {
/* 1117:1219 */       fileFilter = this.m_arffFileFilter;
/* 1118:     */     }
/* 1119:1221 */     this.m_DestFileChooser.setFileFilter(fileFilter);
/* 1120:1222 */     int returnVal = this.m_DestFileChooser.showSaveDialog(this);
/* 1121:1223 */     if (returnVal != 0) {
/* 1122:1224 */       return;
/* 1123:     */     }
/* 1124:1226 */     this.m_ResultsDestinationPathTField.setText(this.m_DestFileChooser.getSelectedFile().toString());
/* 1125:     */   }
/* 1126:     */   
/* 1127:     */   public void cleanUpAfterSwitch()
/* 1128:     */   {
/* 1129:1233 */     removeNotesFrame();
/* 1130:     */   }
/* 1131:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.SimpleSetupPanel
 * JD-Core Version:    0.7.0.1
 */