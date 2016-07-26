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
/*   38:     */ import java.util.List;
/*   39:     */ import java.util.Map;
/*   40:     */ import java.util.Random;
/*   41:     */ import java.util.Vector;
/*   42:     */ import java.util.zip.GZIPInputStream;
/*   43:     */ import java.util.zip.GZIPOutputStream;
/*   44:     */ import javax.swing.BorderFactory;
/*   45:     */ import javax.swing.ButtonGroup;
/*   46:     */ import javax.swing.DefaultComboBoxModel;
/*   47:     */ import javax.swing.JButton;
/*   48:     */ import javax.swing.JCheckBox;
/*   49:     */ import javax.swing.JComboBox;
/*   50:     */ import javax.swing.JDialog;
/*   51:     */ import javax.swing.JFileChooser;
/*   52:     */ import javax.swing.JFrame;
/*   53:     */ import javax.swing.JLabel;
/*   54:     */ import javax.swing.JList;
/*   55:     */ import javax.swing.JMenu;
/*   56:     */ import javax.swing.JMenuItem;
/*   57:     */ import javax.swing.JOptionPane;
/*   58:     */ import javax.swing.JPanel;
/*   59:     */ import javax.swing.JPopupMenu;
/*   60:     */ import javax.swing.JRadioButton;
/*   61:     */ import javax.swing.JScrollPane;
/*   62:     */ import javax.swing.JTextArea;
/*   63:     */ import javax.swing.JTextField;
/*   64:     */ import javax.swing.JViewport;
/*   65:     */ import javax.swing.event.ChangeEvent;
/*   66:     */ import javax.swing.event.ChangeListener;
/*   67:     */ import javax.swing.filechooser.FileFilter;
/*   68:     */ import weka.classifiers.AbstractClassifier;
/*   69:     */ import weka.classifiers.Classifier;
/*   70:     */ import weka.classifiers.CostMatrix;
/*   71:     */ import weka.classifiers.Evaluation;
/*   72:     */ import weka.classifiers.Sourcable;
/*   73:     */ import weka.classifiers.evaluation.CostCurve;
/*   74:     */ import weka.classifiers.evaluation.MarginCurve;
/*   75:     */ import weka.classifiers.evaluation.Prediction;
/*   76:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   77:     */ import weka.classifiers.evaluation.output.prediction.AbstractOutput;
/*   78:     */ import weka.classifiers.evaluation.output.prediction.Null;
/*   79:     */ import weka.classifiers.misc.InputMappedClassifier;
/*   80:     */ import weka.classifiers.pmml.consumer.PMMLClassifier;
/*   81:     */ import weka.classifiers.rules.ZeroR;
/*   82:     */ import weka.core.Attribute;
/*   83:     */ import weka.core.BatchPredictor;
/*   84:     */ import weka.core.Capabilities;
/*   85:     */ import weka.core.CapabilitiesHandler;
/*   86:     */ import weka.core.Defaults;
/*   87:     */ import weka.core.Drawable;
/*   88:     */ import weka.core.Environment;
/*   89:     */ import weka.core.Instance;
/*   90:     */ import weka.core.Instances;
/*   91:     */ import weka.core.Memory;
/*   92:     */ import weka.core.OptionHandler;
/*   93:     */ import weka.core.Range;
/*   94:     */ import weka.core.SerializedObject;
/*   95:     */ import weka.core.Settings;
/*   96:     */ import weka.core.Settings.SettingKey;
/*   97:     */ import weka.core.Utils;
/*   98:     */ import weka.core.Version;
/*   99:     */ import weka.core.converters.ArffLoader;
/*  100:     */ import weka.core.converters.ConverterUtils.DataSource;
/*  101:     */ import weka.core.converters.IncrementalConverter;
/*  102:     */ import weka.core.converters.Loader;
/*  103:     */ import weka.core.pmml.MiningSchema;
/*  104:     */ import weka.core.pmml.PMMLFactory;
/*  105:     */ import weka.core.pmml.PMMLModel;
/*  106:     */ import weka.gui.AbstractPerspective;
/*  107:     */ import weka.gui.CostMatrixEditor;
/*  108:     */ import weka.gui.EvaluationMetricSelectionDialog;
/*  109:     */ import weka.gui.ExtensionFileFilter;
/*  110:     */ import weka.gui.GUIApplication;
/*  111:     */ import weka.gui.GenericObjectEditor;
/*  112:     */ import weka.gui.LogPanel;
/*  113:     */ import weka.gui.Logger;
/*  114:     */ import weka.gui.Perspective;
/*  115:     */ import weka.gui.PerspectiveInfo;
/*  116:     */ import weka.gui.PerspectiveManager;
/*  117:     */ import weka.gui.PropertyDialog;
/*  118:     */ import weka.gui.PropertyPanel;
/*  119:     */ import weka.gui.ResultHistoryPanel;
/*  120:     */ import weka.gui.SaveBuffer;
/*  121:     */ import weka.gui.SetInstancesPanel;
/*  122:     */ import weka.gui.SysErrLog;
/*  123:     */ import weka.gui.TaskLogger;
/*  124:     */ import weka.gui.beans.CostBenefitAnalysis;
/*  125:     */ import weka.gui.graphvisualizer.BIFFormatException;
/*  126:     */ import weka.gui.graphvisualizer.GraphVisualizer;
/*  127:     */ import weka.gui.treevisualizer.PlaceNode2;
/*  128:     */ import weka.gui.treevisualizer.TreeVisualizer;
/*  129:     */ import weka.gui.visualize.PlotData2D;
/*  130:     */ import weka.gui.visualize.ThresholdVisualizePanel;
/*  131:     */ import weka.gui.visualize.VisualizePanel;
/*  132:     */ import weka.gui.visualize.plugins.ErrorVisualizePlugin;
/*  133:     */ import weka.gui.visualize.plugins.GraphVisualizePlugin;
/*  134:     */ import weka.gui.visualize.plugins.TreeVisualizePlugin;
/*  135:     */ import weka.gui.visualize.plugins.VisualizePlugin;
/*  136:     */ 
/*  137:     */ @PerspectiveInfo(ID="weka.gui.explorer.classifierpanel", title="Classify", toolTipText="Classify instances", iconPath="weka/gui/weka_icon_new_small.png")
/*  138:     */ public class ClassifierPanel
/*  139:     */   extends AbstractPerspective
/*  140:     */   implements Explorer.CapabilitiesFilterChangeListener, Explorer.ExplorerPanel, Explorer.LogHandler
/*  141:     */ {
/*  142:     */   static final long serialVersionUID = 6959973704963624003L;
/*  143: 170 */   protected Explorer m_Explorer = null;
/*  144: 173 */   public static String MODEL_FILE_EXTENSION = ".model";
/*  145: 176 */   public static String PMML_FILE_EXTENSION = ".xml";
/*  146: 179 */   protected GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor();
/*  147: 182 */   protected PropertyPanel m_CEPanel = new PropertyPanel(this.m_ClassifierEditor);
/*  148: 185 */   protected JTextArea m_OutText = new JTextArea(20, 40);
/*  149: 188 */   protected Logger m_Log = new SysErrLog();
/*  150: 191 */   SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
/*  151: 194 */   protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
/*  152: 197 */   protected JComboBox m_ClassCombo = new JComboBox();
/*  153: 200 */   protected JRadioButton m_CVBut = new JRadioButton("Cross-validation");
/*  154: 203 */   protected JRadioButton m_PercentBut = new JRadioButton("Percentage split");
/*  155: 206 */   protected JRadioButton m_TrainBut = new JRadioButton("Use training set");
/*  156: 209 */   protected JRadioButton m_TestSplitBut = new JRadioButton("Supplied test set");
/*  157: 214 */   protected JCheckBox m_StorePredictionsBut = new JCheckBox("Store predictions for visualization");
/*  158: 221 */   protected JCheckBox m_errorPlotPointSizeProportionalToMargin = new JCheckBox("Error plot point size proportional to margin");
/*  159: 225 */   protected JCheckBox m_OutputModelBut = new JCheckBox("Output model");
/*  160: 228 */   protected JCheckBox m_OutputPerClassBut = new JCheckBox("Output per-class stats");
/*  161: 232 */   protected JCheckBox m_OutputConfusionBut = new JCheckBox("Output confusion matrix");
/*  162: 236 */   protected JCheckBox m_OutputEntropyBut = new JCheckBox("Output entropy evaluation measures");
/*  163: 240 */   protected GenericObjectEditor m_ClassificationOutputEditor = new GenericObjectEditor(true);
/*  164: 244 */   protected PropertyPanel m_ClassificationOutputPanel = new PropertyPanel(this.m_ClassificationOutputEditor);
/*  165: 248 */   protected Range m_OutputAdditionalAttributesRange = null;
/*  166: 251 */   protected JCheckBox m_EvalWRTCostsBut = new JCheckBox("Cost-sensitive evaluation");
/*  167: 255 */   protected JButton m_SetCostsBut = new JButton("Set...");
/*  168: 258 */   protected JLabel m_CVLab = new JLabel("Folds", 4);
/*  169: 261 */   protected JTextField m_CVText = new JTextField("10", 3);
/*  170: 264 */   protected JLabel m_PercentLab = new JLabel("%", 4);
/*  171: 267 */   protected JTextField m_PercentText = new JTextField("66", 3);
/*  172: 270 */   protected JButton m_SetTestBut = new JButton("Set...");
/*  173:     */   protected JFrame m_SetTestFrame;
/*  174:     */   protected PropertyDialog m_SetCostsFrame;
/*  175: 282 */   ActionListener m_RadioListener = new ActionListener()
/*  176:     */   {
/*  177:     */     public void actionPerformed(ActionEvent e)
/*  178:     */     {
/*  179: 285 */       ClassifierPanel.this.updateRadioLinks();
/*  180:     */     }
/*  181:     */   };
/*  182: 290 */   JButton m_MoreOptions = new JButton("More options...");
/*  183: 293 */   protected JTextField m_RandomSeedText = new JTextField("1", 3);
/*  184: 296 */   protected JLabel m_RandomLab = new JLabel("Random seed for XVal / % Split", 4);
/*  185: 300 */   protected JCheckBox m_PreserveOrderBut = new JCheckBox("Preserve order for % Split");
/*  186: 307 */   protected JCheckBox m_OutputSourceCode = new JCheckBox("Output source code");
/*  187: 310 */   protected JTextField m_SourceCodeClass = new JTextField("WekaClassifier", 10);
/*  188: 313 */   protected JButton m_StartBut = new JButton("Start");
/*  189: 316 */   protected JButton m_StopBut = new JButton("Stop");
/*  190: 319 */   private final Dimension COMBO_SIZE = new Dimension(150, this.m_StartBut.getPreferredSize().height);
/*  191: 323 */   protected CostMatrixEditor m_CostMatrixEditor = new CostMatrixEditor();
/*  192:     */   protected Instances m_Instances;
/*  193:     */   protected Loader m_TestLoader;
/*  194: 332 */   protected int m_TestClassIndex = -1;
/*  195:     */   protected Thread m_RunThread;
/*  196: 338 */   protected VisualizePanel m_CurrentVis = null;
/*  197: 341 */   protected FileFilter m_ModelFilter = new ExtensionFileFilter(MODEL_FILE_EXTENSION, "Model object files");
/*  198: 344 */   protected FileFilter m_PMMLModelFilter = new ExtensionFileFilter(PMML_FILE_EXTENSION, "PMML model files");
/*  199: 348 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  200: 352 */   protected List<String> m_selectedEvalMetrics = Evaluation.getAllEvaluationMetricNames();
/*  201:     */   protected boolean m_initialSettingsSet;
/*  202:     */   
/*  203:     */   static
/*  204:     */   {
/*  205: 363 */     GenericObjectEditor.registerEditors();
/*  206:     */   }
/*  207:     */   
/*  208:     */   public ClassifierPanel()
/*  209:     */   {
/*  210: 370 */     this.m_selectedEvalMetrics.remove("Coverage");
/*  211: 371 */     this.m_selectedEvalMetrics.remove("Region size");
/*  212:     */     
/*  213:     */ 
/*  214: 374 */     this.m_OutText.setEditable(false);
/*  215: 375 */     this.m_OutText.setFont(new Font("Monospaced", 0, 12));
/*  216: 376 */     this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  217: 377 */     this.m_OutText.addMouseListener(new MouseAdapter()
/*  218:     */     {
/*  219:     */       public void mouseClicked(MouseEvent e)
/*  220:     */       {
/*  221: 380 */         if ((e.getModifiers() & 0x10) != 16) {
/*  222: 381 */           ClassifierPanel.this.m_OutText.selectAll();
/*  223:     */         }
/*  224:     */       }
/*  225: 384 */     });
/*  226: 385 */     JPanel historyHolder = new JPanel(new BorderLayout());
/*  227: 386 */     historyHolder.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
/*  228:     */     
/*  229: 388 */     historyHolder.add(this.m_History, "Center");
/*  230: 389 */     this.m_ClassifierEditor.setClassType(Classifier.class);
/*  231: 390 */     this.m_ClassifierEditor.setValue(ExplorerDefaults.getClassifier());
/*  232: 391 */     this.m_ClassifierEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  233:     */     {
/*  234:     */       public void propertyChange(PropertyChangeEvent e)
/*  235:     */       {
/*  236: 394 */         ClassifierPanel.this.m_StartBut.setEnabled(true);
/*  237:     */         
/*  238: 396 */         Capabilities currentFilter = ClassifierPanel.this.m_ClassifierEditor.getCapabilitiesFilter();
/*  239: 397 */         Classifier classifier = (Classifier)ClassifierPanel.this.m_ClassifierEditor.getValue();
/*  240: 398 */         Capabilities currentSchemeCapabilities = null;
/*  241: 399 */         if ((classifier != null) && (currentFilter != null) && ((classifier instanceof CapabilitiesHandler)))
/*  242:     */         {
/*  243: 401 */           currentSchemeCapabilities = ((CapabilitiesHandler)classifier).getCapabilities();
/*  244: 404 */           if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/*  245: 406 */             ClassifierPanel.this.m_StartBut.setEnabled(false);
/*  246:     */           }
/*  247:     */         }
/*  248: 409 */         ClassifierPanel.this.repaint();
/*  249:     */       }
/*  250: 412 */     });
/*  251: 413 */     this.m_ClassCombo.setToolTipText("Select the attribute to use as the class");
/*  252: 414 */     this.m_TrainBut.setToolTipText("Test on the same set that the classifier is trained on");
/*  253:     */     
/*  254: 416 */     this.m_CVBut.setToolTipText("Perform a n-fold cross-validation");
/*  255: 417 */     this.m_PercentBut.setToolTipText("Train on a percentage of the data and test on the remainder");
/*  256:     */     
/*  257: 419 */     this.m_TestSplitBut.setToolTipText("Test on a user-specified dataset");
/*  258: 420 */     this.m_StartBut.setToolTipText("Starts the classification");
/*  259: 421 */     this.m_StopBut.setToolTipText("Stops a running classification");
/*  260: 422 */     this.m_StorePredictionsBut.setToolTipText("Store predictions in the result list for later visualization");
/*  261:     */     
/*  262:     */ 
/*  263: 425 */     this.m_errorPlotPointSizeProportionalToMargin.setToolTipText("In classifier errors plots the point size will be set proportional to the absolute value of the prediction margin (affects classification only)");
/*  264:     */     
/*  265:     */ 
/*  266:     */ 
/*  267: 429 */     this.m_OutputModelBut.setToolTipText("Output the model obtained from the full training set");
/*  268:     */     
/*  269: 431 */     this.m_OutputPerClassBut.setToolTipText("Output precision/recall & true/false positives for each class");
/*  270:     */     
/*  271: 433 */     this.m_OutputConfusionBut.setToolTipText("Output the matrix displaying class confusions");
/*  272:     */     
/*  273: 435 */     this.m_OutputEntropyBut.setToolTipText("Output entropy-based evaluation measures");
/*  274:     */     
/*  275: 437 */     this.m_EvalWRTCostsBut.setToolTipText("Evaluate errors with respect to a cost matrix");
/*  276:     */     
/*  277: 439 */     this.m_RandomLab.setToolTipText("The seed value for randomization");
/*  278: 440 */     this.m_RandomSeedText.setToolTipText(this.m_RandomLab.getToolTipText());
/*  279: 441 */     this.m_PreserveOrderBut.setToolTipText("Preserves the order in a percentage split");
/*  280:     */     
/*  281: 443 */     this.m_OutputSourceCode.setToolTipText("Whether to output the built classifier as Java source code");
/*  282:     */     
/*  283: 445 */     this.m_SourceCodeClass.setToolTipText("The classname of the built classifier");
/*  284:     */     
/*  285: 447 */     this.m_FileChooser.addChoosableFileFilter(this.m_PMMLModelFilter);
/*  286: 448 */     this.m_FileChooser.setFileFilter(this.m_ModelFilter);
/*  287:     */     
/*  288: 450 */     this.m_FileChooser.setFileSelectionMode(0);
/*  289:     */     
/*  290: 452 */     this.m_ClassificationOutputEditor.setClassType(AbstractOutput.class);
/*  291: 453 */     this.m_ClassificationOutputEditor.setValue(new Null());
/*  292:     */     
/*  293: 455 */     this.m_StorePredictionsBut.setSelected(ExplorerDefaults.getClassifierStorePredictionsForVis());
/*  294:     */     
/*  295: 457 */     this.m_OutputModelBut.setSelected(ExplorerDefaults.getClassifierOutputModel());
/*  296: 458 */     this.m_OutputPerClassBut.setSelected(ExplorerDefaults.getClassifierOutputPerClassStats());
/*  297:     */     
/*  298: 460 */     this.m_OutputConfusionBut.setSelected(ExplorerDefaults.getClassifierOutputConfusionMatrix());
/*  299:     */     
/*  300: 462 */     this.m_EvalWRTCostsBut.setSelected(ExplorerDefaults.getClassifierCostSensitiveEval());
/*  301:     */     
/*  302: 464 */     this.m_OutputEntropyBut.setSelected(ExplorerDefaults.getClassifierOutputEntropyEvalMeasures());
/*  303:     */     
/*  304: 466 */     this.m_RandomSeedText.setText("" + ExplorerDefaults.getClassifierRandomSeed());
/*  305: 467 */     this.m_PreserveOrderBut.setSelected(ExplorerDefaults.getClassifierPreserveOrder());
/*  306:     */     
/*  307: 469 */     this.m_OutputSourceCode.addActionListener(new ActionListener()
/*  308:     */     {
/*  309:     */       public void actionPerformed(ActionEvent e)
/*  310:     */       {
/*  311: 472 */         ClassifierPanel.this.m_SourceCodeClass.setEnabled(ClassifierPanel.this.m_OutputSourceCode.isSelected());
/*  312:     */       }
/*  313: 474 */     });
/*  314: 475 */     this.m_OutputSourceCode.setSelected(ExplorerDefaults.getClassifierOutputSourceCode());
/*  315:     */     
/*  316: 477 */     this.m_SourceCodeClass.setText(ExplorerDefaults.getClassifierSourceCodeClass());
/*  317: 478 */     this.m_SourceCodeClass.setEnabled(this.m_OutputSourceCode.isSelected());
/*  318: 479 */     this.m_ClassCombo.setEnabled(false);
/*  319: 480 */     this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
/*  320: 481 */     this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
/*  321: 482 */     this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
/*  322:     */     
/*  323: 484 */     this.m_CVBut.setSelected(true);
/*  324:     */     
/*  325: 486 */     this.m_CVBut.setSelected(ExplorerDefaults.getClassifierTestMode() == 1);
/*  326: 487 */     this.m_PercentBut.setSelected(ExplorerDefaults.getClassifierTestMode() == 2);
/*  327: 488 */     this.m_TrainBut.setSelected(ExplorerDefaults.getClassifierTestMode() == 3);
/*  328: 489 */     this.m_TestSplitBut.setSelected(ExplorerDefaults.getClassifierTestMode() == 4);
/*  329: 490 */     this.m_PercentText.setText("" + ExplorerDefaults.getClassifierPercentageSplit());
/*  330: 491 */     this.m_CVText.setText("" + ExplorerDefaults.getClassifierCrossvalidationFolds());
/*  331: 492 */     updateRadioLinks();
/*  332: 493 */     ButtonGroup bg = new ButtonGroup();
/*  333: 494 */     bg.add(this.m_TrainBut);
/*  334: 495 */     bg.add(this.m_CVBut);
/*  335: 496 */     bg.add(this.m_PercentBut);
/*  336: 497 */     bg.add(this.m_TestSplitBut);
/*  337: 498 */     this.m_TrainBut.addActionListener(this.m_RadioListener);
/*  338: 499 */     this.m_CVBut.addActionListener(this.m_RadioListener);
/*  339: 500 */     this.m_PercentBut.addActionListener(this.m_RadioListener);
/*  340: 501 */     this.m_TestSplitBut.addActionListener(this.m_RadioListener);
/*  341: 502 */     this.m_SetTestBut.addActionListener(new ActionListener()
/*  342:     */     {
/*  343:     */       public void actionPerformed(ActionEvent e)
/*  344:     */       {
/*  345: 505 */         ClassifierPanel.this.setTestSet();
/*  346:     */       }
/*  347: 507 */     });
/*  348: 508 */     this.m_EvalWRTCostsBut.addActionListener(new ActionListener()
/*  349:     */     {
/*  350:     */       public void actionPerformed(ActionEvent e)
/*  351:     */       {
/*  352: 511 */         ClassifierPanel.this.m_SetCostsBut.setEnabled(ClassifierPanel.this.m_EvalWRTCostsBut.isSelected());
/*  353: 512 */         if ((ClassifierPanel.this.m_SetCostsFrame != null) && (!ClassifierPanel.this.m_EvalWRTCostsBut.isSelected())) {
/*  354: 513 */           ClassifierPanel.this.m_SetCostsFrame.setVisible(false);
/*  355:     */         }
/*  356:     */       }
/*  357: 516 */     });
/*  358: 517 */     this.m_CostMatrixEditor.setValue(new CostMatrix(1));
/*  359: 518 */     this.m_SetCostsBut.setEnabled(this.m_EvalWRTCostsBut.isSelected());
/*  360: 519 */     this.m_SetCostsBut.addActionListener(new ActionListener()
/*  361:     */     {
/*  362:     */       public void actionPerformed(ActionEvent e)
/*  363:     */       {
/*  364: 522 */         ClassifierPanel.this.m_SetCostsBut.setEnabled(false);
/*  365: 523 */         if (ClassifierPanel.this.m_SetCostsFrame == null)
/*  366:     */         {
/*  367: 524 */           if (PropertyDialog.getParentDialog(ClassifierPanel.this) != null) {
/*  368: 525 */             ClassifierPanel.this.m_SetCostsFrame = new PropertyDialog(PropertyDialog.getParentDialog(ClassifierPanel.this), ClassifierPanel.this.m_CostMatrixEditor, 100, 100);
/*  369:     */           } else {
/*  370: 530 */             ClassifierPanel.this.m_SetCostsFrame = new PropertyDialog(PropertyDialog.getParentFrame(ClassifierPanel.this), ClassifierPanel.this.m_CostMatrixEditor, 100, 100);
/*  371:     */           }
/*  372: 535 */           ClassifierPanel.this.m_SetCostsFrame.setTitle("Cost Matrix Editor");
/*  373:     */           
/*  374: 537 */           ClassifierPanel.this.m_SetCostsFrame.addWindowListener(new WindowAdapter()
/*  375:     */           {
/*  376:     */             public void windowClosing(WindowEvent p)
/*  377:     */             {
/*  378: 540 */               ClassifierPanel.this.m_SetCostsBut.setEnabled(ClassifierPanel.this.m_EvalWRTCostsBut.isSelected());
/*  379: 541 */               if ((ClassifierPanel.this.m_SetCostsFrame != null) && (!ClassifierPanel.this.m_EvalWRTCostsBut.isSelected())) {
/*  380: 543 */                 ClassifierPanel.this.m_SetCostsFrame.setVisible(false);
/*  381:     */               }
/*  382:     */             }
/*  383: 546 */           });
/*  384: 547 */           ClassifierPanel.this.m_SetCostsFrame.setVisible(true);
/*  385:     */         }
/*  386: 551 */         int classIndex = ClassifierPanel.this.m_ClassCombo.getSelectedIndex();
/*  387: 552 */         int numClasses = ClassifierPanel.this.m_Instances.attribute(classIndex).numValues();
/*  388: 553 */         if (numClasses != ((CostMatrix)ClassifierPanel.this.m_CostMatrixEditor.getValue()).numColumns()) {
/*  389: 555 */           ClassifierPanel.this.m_CostMatrixEditor.setValue(new CostMatrix(numClasses));
/*  390:     */         }
/*  391: 558 */         ClassifierPanel.this.m_SetCostsFrame.setVisible(true);
/*  392:     */       }
/*  393: 561 */     });
/*  394: 562 */     this.m_StartBut.setEnabled(false);
/*  395: 563 */     this.m_StopBut.setEnabled(false);
/*  396: 564 */     this.m_StartBut.addActionListener(new ActionListener()
/*  397:     */     {
/*  398:     */       public void actionPerformed(ActionEvent e)
/*  399:     */       {
/*  400: 567 */         boolean proceed = true;
/*  401: 568 */         if (Explorer.m_Memory.memoryIsLow()) {
/*  402: 569 */           proceed = Explorer.m_Memory.showMemoryIsLow();
/*  403:     */         }
/*  404: 571 */         if (proceed) {
/*  405: 572 */           ClassifierPanel.this.startClassifier();
/*  406:     */         }
/*  407:     */       }
/*  408: 575 */     });
/*  409: 576 */     this.m_StopBut.addActionListener(new ActionListener()
/*  410:     */     {
/*  411:     */       public void actionPerformed(ActionEvent e)
/*  412:     */       {
/*  413: 579 */         ClassifierPanel.this.stopClassifier();
/*  414:     */       }
/*  415: 582 */     });
/*  416: 583 */     this.m_ClassCombo.addActionListener(new ActionListener()
/*  417:     */     {
/*  418:     */       public void actionPerformed(ActionEvent e)
/*  419:     */       {
/*  420: 586 */         int selected = ClassifierPanel.this.m_ClassCombo.getSelectedIndex();
/*  421: 587 */         if (selected != -1)
/*  422:     */         {
/*  423: 588 */           boolean isNominal = ClassifierPanel.this.m_Instances.attribute(selected).isNominal();
/*  424: 589 */           ClassifierPanel.this.m_OutputPerClassBut.setEnabled(isNominal);
/*  425: 590 */           ClassifierPanel.this.m_OutputConfusionBut.setEnabled(isNominal);
/*  426:     */         }
/*  427: 592 */         ClassifierPanel.this.updateCapabilitiesFilter(ClassifierPanel.this.m_ClassifierEditor.getCapabilitiesFilter());
/*  428:     */       }
/*  429: 595 */     });
/*  430: 596 */     this.m_History.setHandleRightClicks(false);
/*  431:     */     
/*  432: 598 */     this.m_History.getList().addMouseListener(new MouseAdapter()
/*  433:     */     {
/*  434:     */       public void mouseClicked(MouseEvent e)
/*  435:     */       {
/*  436: 601 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/*  437:     */         {
/*  438: 603 */           int index = ClassifierPanel.this.m_History.getList().locationToIndex(e.getPoint());
/*  439: 604 */           if (index != -1)
/*  440:     */           {
/*  441: 605 */             String name = ClassifierPanel.this.m_History.getNameAtIndex(index);
/*  442: 606 */             ClassifierPanel.this.visualize(name, e.getX(), e.getY());
/*  443:     */           }
/*  444:     */           else
/*  445:     */           {
/*  446: 608 */             ClassifierPanel.this.visualize(null, e.getX(), e.getY());
/*  447:     */           }
/*  448:     */         }
/*  449:     */       }
/*  450: 613 */     });
/*  451: 614 */     this.m_MoreOptions.addActionListener(new ActionListener()
/*  452:     */     {
/*  453:     */       public void actionPerformed(ActionEvent e)
/*  454:     */       {
/*  455: 617 */         ClassifierPanel.this.m_MoreOptions.setEnabled(false);
/*  456: 618 */         JPanel moreOptionsPanel = new JPanel();
/*  457: 619 */         moreOptionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  458: 620 */         moreOptionsPanel.setLayout(new GridLayout(0, 1));
/*  459: 621 */         moreOptionsPanel.add(ClassifierPanel.this.m_OutputModelBut);
/*  460: 622 */         moreOptionsPanel.add(ClassifierPanel.this.m_OutputPerClassBut);
/*  461: 623 */         moreOptionsPanel.add(ClassifierPanel.this.m_OutputEntropyBut);
/*  462: 624 */         moreOptionsPanel.add(ClassifierPanel.this.m_OutputConfusionBut);
/*  463: 625 */         moreOptionsPanel.add(ClassifierPanel.this.m_StorePredictionsBut);
/*  464: 626 */         moreOptionsPanel.add(ClassifierPanel.this.m_errorPlotPointSizeProportionalToMargin);
/*  465: 627 */         JPanel classOutPanel = new JPanel(new FlowLayout(0));
/*  466: 628 */         classOutPanel.add(new JLabel("Output predictions"));
/*  467: 629 */         classOutPanel.add(ClassifierPanel.this.m_ClassificationOutputPanel);
/*  468: 630 */         moreOptionsPanel.add(classOutPanel);
/*  469: 631 */         JPanel costMatrixOption = new JPanel(new FlowLayout(0));
/*  470: 632 */         costMatrixOption.add(ClassifierPanel.this.m_EvalWRTCostsBut);
/*  471: 633 */         costMatrixOption.add(ClassifierPanel.this.m_SetCostsBut);
/*  472: 634 */         moreOptionsPanel.add(costMatrixOption);
/*  473: 635 */         JPanel seedPanel = new JPanel(new FlowLayout(0));
/*  474: 636 */         seedPanel.add(ClassifierPanel.this.m_RandomLab);
/*  475: 637 */         seedPanel.add(ClassifierPanel.this.m_RandomSeedText);
/*  476: 638 */         moreOptionsPanel.add(seedPanel);
/*  477: 639 */         moreOptionsPanel.add(ClassifierPanel.this.m_PreserveOrderBut);
/*  478: 640 */         JPanel sourcePanel = new JPanel(new FlowLayout(0));
/*  479: 641 */         ClassifierPanel.this.m_OutputSourceCode.setEnabled(ClassifierPanel.this.m_ClassifierEditor.getValue() instanceof Sourcable);
/*  480: 642 */         ClassifierPanel.this.m_SourceCodeClass.setEnabled((ClassifierPanel.this.m_OutputSourceCode.isEnabled()) && (ClassifierPanel.this.m_OutputSourceCode.isSelected()));
/*  481:     */         
/*  482: 644 */         sourcePanel.add(ClassifierPanel.this.m_OutputSourceCode);
/*  483: 645 */         sourcePanel.add(ClassifierPanel.this.m_SourceCodeClass);
/*  484: 646 */         moreOptionsPanel.add(sourcePanel);
/*  485:     */         
/*  486: 648 */         JPanel all = new JPanel();
/*  487: 649 */         all.setLayout(new BorderLayout());
/*  488:     */         
/*  489: 651 */         JButton oK = new JButton("OK");
/*  490: 652 */         JPanel okP = new JPanel();
/*  491: 653 */         okP.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  492: 654 */         okP.setLayout(new GridLayout(1, 1, 5, 5));
/*  493: 655 */         okP.add(oK);
/*  494:     */         
/*  495: 657 */         all.add(moreOptionsPanel, "Center");
/*  496: 658 */         all.add(okP, "South");
/*  497:     */         
/*  498: 660 */         final JDialog jd = new JDialog(PropertyDialog.getParentFrame(ClassifierPanel.this), "Classifier evaluation options");
/*  499:     */         
/*  500:     */ 
/*  501: 663 */         jd.getContentPane().setLayout(new BorderLayout());
/*  502: 664 */         jd.getContentPane().add(all, "Center");
/*  503: 665 */         jd.addWindowListener(new WindowAdapter()
/*  504:     */         {
/*  505:     */           public void windowClosing(WindowEvent w)
/*  506:     */           {
/*  507: 668 */             jd.dispose();
/*  508: 669 */             ClassifierPanel.this.m_MoreOptions.setEnabled(true);
/*  509:     */           }
/*  510: 671 */         });
/*  511: 672 */         oK.addActionListener(new ActionListener()
/*  512:     */         {
/*  513:     */           public void actionPerformed(ActionEvent a)
/*  514:     */           {
/*  515: 675 */             ClassifierPanel.this.m_MoreOptions.setEnabled(true);
/*  516: 676 */             jd.dispose();
/*  517:     */           }
/*  518: 678 */         });
/*  519: 679 */         jd.pack();
/*  520:     */         
/*  521:     */ 
/*  522: 682 */         ClassifierPanel.this.m_ClassificationOutputPanel.setPreferredSize(new Dimension(300, ClassifierPanel.this.m_ClassificationOutputPanel.getHeight()));
/*  523:     */         
/*  524: 684 */         jd.pack();
/*  525:     */         
/*  526:     */ 
/*  527:     */ 
/*  528:     */ 
/*  529:     */ 
/*  530: 690 */         JButton editEvalMetrics = new JButton("Evaluation metrics...");
/*  531: 691 */         JPanel evalP = new JPanel();
/*  532: 692 */         evalP.setLayout(new BorderLayout());
/*  533: 693 */         evalP.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  534: 694 */         evalP.add(editEvalMetrics, "Center");
/*  535: 695 */         editEvalMetrics.setToolTipText("Enable/disable output of specific evaluation metrics");
/*  536:     */         
/*  537: 697 */         moreOptionsPanel.add(evalP);
/*  538:     */         
/*  539: 699 */         editEvalMetrics.addActionListener(new ActionListener()
/*  540:     */         {
/*  541:     */           public void actionPerformed(ActionEvent e)
/*  542:     */           {
/*  543: 702 */             EvaluationMetricSelectionDialog esd = new EvaluationMetricSelectionDialog(jd, ClassifierPanel.this.m_selectedEvalMetrics);
/*  544:     */             
/*  545: 704 */             esd.setLocation(ClassifierPanel.this.m_MoreOptions.getLocationOnScreen());
/*  546: 705 */             esd.pack();
/*  547: 706 */             esd.setVisible(true);
/*  548: 707 */             ClassifierPanel.this.m_selectedEvalMetrics = esd.getSelectedEvalMetrics();
/*  549:     */           }
/*  550: 710 */         });
/*  551: 711 */         jd.setLocation(ClassifierPanel.this.m_MoreOptions.getLocationOnScreen());
/*  552: 712 */         jd.setVisible(true);
/*  553:     */       }
/*  554: 716 */     });
/*  555: 717 */     JPanel p1 = new JPanel();
/*  556: 718 */     p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Classifier"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  557:     */     
/*  558:     */ 
/*  559: 721 */     p1.setLayout(new BorderLayout());
/*  560: 722 */     p1.add(this.m_CEPanel, "North");
/*  561:     */     
/*  562: 724 */     JPanel p2 = new JPanel();
/*  563: 725 */     GridBagLayout gbL = new GridBagLayout();
/*  564: 726 */     p2.setLayout(gbL);
/*  565: 727 */     p2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Test options"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  566:     */     
/*  567:     */ 
/*  568: 730 */     GridBagConstraints gbC = new GridBagConstraints();
/*  569: 731 */     gbC.anchor = 17;
/*  570: 732 */     gbC.gridy = 0;
/*  571: 733 */     gbC.gridx = 0;
/*  572: 734 */     gbL.setConstraints(this.m_TrainBut, gbC);
/*  573: 735 */     p2.add(this.m_TrainBut);
/*  574:     */     
/*  575: 737 */     gbC = new GridBagConstraints();
/*  576: 738 */     gbC.anchor = 17;
/*  577: 739 */     gbC.gridy = 1;
/*  578: 740 */     gbC.gridx = 0;
/*  579: 741 */     gbL.setConstraints(this.m_TestSplitBut, gbC);
/*  580: 742 */     p2.add(this.m_TestSplitBut);
/*  581:     */     
/*  582: 744 */     gbC = new GridBagConstraints();
/*  583: 745 */     gbC.anchor = 13;
/*  584: 746 */     gbC.fill = 2;
/*  585: 747 */     gbC.gridy = 1;
/*  586: 748 */     gbC.gridx = 1;
/*  587: 749 */     gbC.gridwidth = 2;
/*  588: 750 */     gbC.insets = new Insets(2, 10, 2, 0);
/*  589: 751 */     gbL.setConstraints(this.m_SetTestBut, gbC);
/*  590: 752 */     p2.add(this.m_SetTestBut);
/*  591:     */     
/*  592: 754 */     gbC = new GridBagConstraints();
/*  593: 755 */     gbC.anchor = 17;
/*  594: 756 */     gbC.gridy = 2;
/*  595: 757 */     gbC.gridx = 0;
/*  596: 758 */     gbL.setConstraints(this.m_CVBut, gbC);
/*  597: 759 */     p2.add(this.m_CVBut);
/*  598:     */     
/*  599: 761 */     gbC = new GridBagConstraints();
/*  600: 762 */     gbC.anchor = 13;
/*  601: 763 */     gbC.fill = 2;
/*  602: 764 */     gbC.gridy = 2;
/*  603: 765 */     gbC.gridx = 1;
/*  604: 766 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  605: 767 */     gbL.setConstraints(this.m_CVLab, gbC);
/*  606: 768 */     p2.add(this.m_CVLab);
/*  607:     */     
/*  608: 770 */     gbC = new GridBagConstraints();
/*  609: 771 */     gbC.anchor = 13;
/*  610: 772 */     gbC.fill = 2;
/*  611: 773 */     gbC.gridy = 2;
/*  612: 774 */     gbC.gridx = 2;
/*  613: 775 */     gbC.weightx = 100.0D;
/*  614: 776 */     gbC.ipadx = 20;
/*  615: 777 */     gbL.setConstraints(this.m_CVText, gbC);
/*  616: 778 */     p2.add(this.m_CVText);
/*  617:     */     
/*  618: 780 */     gbC = new GridBagConstraints();
/*  619: 781 */     gbC.anchor = 17;
/*  620: 782 */     gbC.gridy = 3;
/*  621: 783 */     gbC.gridx = 0;
/*  622: 784 */     gbL.setConstraints(this.m_PercentBut, gbC);
/*  623: 785 */     p2.add(this.m_PercentBut);
/*  624:     */     
/*  625: 787 */     gbC = new GridBagConstraints();
/*  626: 788 */     gbC.anchor = 13;
/*  627: 789 */     gbC.fill = 2;
/*  628: 790 */     gbC.gridy = 3;
/*  629: 791 */     gbC.gridx = 1;
/*  630: 792 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  631: 793 */     gbL.setConstraints(this.m_PercentLab, gbC);
/*  632: 794 */     p2.add(this.m_PercentLab);
/*  633:     */     
/*  634: 796 */     gbC = new GridBagConstraints();
/*  635: 797 */     gbC.anchor = 13;
/*  636: 798 */     gbC.fill = 2;
/*  637: 799 */     gbC.gridy = 3;
/*  638: 800 */     gbC.gridx = 2;
/*  639: 801 */     gbC.weightx = 100.0D;
/*  640: 802 */     gbC.ipadx = 20;
/*  641: 803 */     gbL.setConstraints(this.m_PercentText, gbC);
/*  642: 804 */     p2.add(this.m_PercentText);
/*  643:     */     
/*  644: 806 */     gbC = new GridBagConstraints();
/*  645: 807 */     gbC.anchor = 17;
/*  646: 808 */     gbC.fill = 2;
/*  647: 809 */     gbC.gridy = 4;
/*  648: 810 */     gbC.gridx = 0;
/*  649: 811 */     gbC.weightx = 100.0D;
/*  650: 812 */     gbC.gridwidth = 3;
/*  651:     */     
/*  652: 814 */     gbC.insets = new Insets(3, 0, 1, 0);
/*  653: 815 */     gbL.setConstraints(this.m_MoreOptions, gbC);
/*  654: 816 */     p2.add(this.m_MoreOptions);
/*  655:     */     
/*  656:     */ 
/*  657: 819 */     Vector<String> pluginsVector = GenericObjectEditor.getClassnames(ClassifierPanelLaunchHandlerPlugin.class.getName());
/*  658:     */     
/*  659:     */ 
/*  660: 822 */     JButton pluginBut = null;
/*  661: 823 */     if (pluginsVector.size() == 1)
/*  662:     */     {
/*  663:     */       try
/*  664:     */       {
/*  665: 826 */         String className = (String)pluginsVector.elementAt(0);
/*  666: 827 */         final ClassifierPanelLaunchHandlerPlugin plugin = (ClassifierPanelLaunchHandlerPlugin)Class.forName(className).newInstance();
/*  667: 830 */         if (plugin != null)
/*  668:     */         {
/*  669: 831 */           plugin.setClassifierPanel(this);
/*  670: 832 */           pluginBut = new JButton(plugin.getLaunchCommand());
/*  671: 833 */           pluginBut.addActionListener(new ActionListener()
/*  672:     */           {
/*  673:     */             public void actionPerformed(ActionEvent e)
/*  674:     */             {
/*  675: 836 */               plugin.launch();
/*  676:     */             }
/*  677:     */           });
/*  678:     */         }
/*  679:     */       }
/*  680:     */       catch (Exception ex)
/*  681:     */       {
/*  682: 841 */         ex.printStackTrace();
/*  683:     */       }
/*  684:     */     }
/*  685: 843 */     else if (pluginsVector.size() > 1)
/*  686:     */     {
/*  687: 845 */       int okPluginCount = 0;
/*  688: 846 */       final PopupMenu pluginPopup = new PopupMenu();
/*  689: 848 */       for (int i = 0; i < pluginsVector.size(); i++)
/*  690:     */       {
/*  691: 849 */         String className = (String)pluginsVector.elementAt(i);
/*  692:     */         try
/*  693:     */         {
/*  694: 851 */           final ClassifierPanelLaunchHandlerPlugin plugin = (ClassifierPanelLaunchHandlerPlugin)Class.forName(className).newInstance();
/*  695: 855 */           if (plugin != null)
/*  696:     */           {
/*  697: 858 */             okPluginCount++;
/*  698: 859 */             plugin.setClassifierPanel(this);
/*  699: 860 */             MenuItem popI = new MenuItem(plugin.getLaunchCommand());
/*  700:     */             
/*  701: 862 */             popI.addActionListener(new ActionListener()
/*  702:     */             {
/*  703:     */               public void actionPerformed(ActionEvent e)
/*  704:     */               {
/*  705: 866 */                 plugin.launch();
/*  706:     */               }
/*  707: 868 */             });
/*  708: 869 */             pluginPopup.add(popI);
/*  709:     */           }
/*  710:     */         }
/*  711:     */         catch (Exception ex)
/*  712:     */         {
/*  713: 871 */           ex.printStackTrace();
/*  714:     */         }
/*  715:     */       }
/*  716: 875 */       if (okPluginCount > 0)
/*  717:     */       {
/*  718: 876 */         pluginBut = new JButton("Launchers...");
/*  719: 877 */         final JButton copyB = pluginBut;
/*  720: 878 */         copyB.add(pluginPopup);
/*  721: 879 */         pluginBut.addActionListener(new ActionListener()
/*  722:     */         {
/*  723:     */           public void actionPerformed(ActionEvent e)
/*  724:     */           {
/*  725: 882 */             pluginPopup.show(copyB, 0, 0);
/*  726:     */           }
/*  727:     */         });
/*  728:     */       }
/*  729:     */       else
/*  730:     */       {
/*  731: 886 */         pluginBut = null;
/*  732:     */       }
/*  733:     */     }
/*  734: 890 */     JPanel buttons = new JPanel();
/*  735: 891 */     buttons.setLayout(new GridLayout(2, 2));
/*  736: 892 */     buttons.add(this.m_ClassCombo);
/*  737: 893 */     this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  738: 894 */     JPanel ssButs = new JPanel();
/*  739: 895 */     ssButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  740: 896 */     if (pluginBut == null) {
/*  741: 897 */       ssButs.setLayout(new GridLayout(1, 2, 5, 5));
/*  742:     */     } else {
/*  743: 899 */       ssButs.setLayout(new FlowLayout(0));
/*  744:     */     }
/*  745: 901 */     ssButs.add(this.m_StartBut);
/*  746: 902 */     ssButs.add(this.m_StopBut);
/*  747: 903 */     if (pluginBut != null) {
/*  748: 904 */       ssButs.add(pluginBut);
/*  749:     */     }
/*  750: 907 */     buttons.add(ssButs);
/*  751:     */     
/*  752: 909 */     JPanel p3 = new JPanel();
/*  753: 910 */     p3.setBorder(BorderFactory.createTitledBorder("Classifier output"));
/*  754: 911 */     p3.setLayout(new BorderLayout());
/*  755: 912 */     JScrollPane js = new JScrollPane(this.m_OutText);
/*  756: 913 */     p3.add(js, "Center");
/*  757: 914 */     js.getViewport().addChangeListener(new ChangeListener()
/*  758:     */     {
/*  759:     */       private int lastHeight;
/*  760:     */       
/*  761:     */       public void stateChanged(ChangeEvent e)
/*  762:     */       {
/*  763: 919 */         JViewport vp = (JViewport)e.getSource();
/*  764: 920 */         int h = vp.getViewSize().height;
/*  765: 921 */         if (h != this.lastHeight)
/*  766:     */         {
/*  767: 922 */           this.lastHeight = h;
/*  768: 923 */           int x = h - vp.getExtentSize().height;
/*  769: 924 */           vp.setViewPosition(new Point(0, x));
/*  770:     */         }
/*  771:     */       }
/*  772: 928 */     });
/*  773: 929 */     JPanel mondo = new JPanel();
/*  774: 930 */     gbL = new GridBagLayout();
/*  775: 931 */     mondo.setLayout(gbL);
/*  776: 932 */     gbC = new GridBagConstraints();
/*  777:     */     
/*  778: 934 */     gbC.fill = 2;
/*  779: 935 */     gbC.gridy = 0;
/*  780: 936 */     gbC.gridx = 0;
/*  781: 937 */     gbL.setConstraints(p2, gbC);
/*  782: 938 */     mondo.add(p2);
/*  783: 939 */     gbC = new GridBagConstraints();
/*  784: 940 */     gbC.anchor = 11;
/*  785: 941 */     gbC.fill = 2;
/*  786: 942 */     gbC.gridy = 1;
/*  787: 943 */     gbC.gridx = 0;
/*  788: 944 */     gbL.setConstraints(buttons, gbC);
/*  789: 945 */     mondo.add(buttons);
/*  790: 946 */     gbC = new GridBagConstraints();
/*  791:     */     
/*  792: 948 */     gbC.fill = 1;
/*  793: 949 */     gbC.gridy = 2;
/*  794: 950 */     gbC.gridx = 0;
/*  795: 951 */     gbC.weightx = 0.0D;
/*  796: 952 */     gbL.setConstraints(historyHolder, gbC);
/*  797: 953 */     mondo.add(historyHolder);
/*  798: 954 */     gbC = new GridBagConstraints();
/*  799: 955 */     gbC.fill = 1;
/*  800: 956 */     gbC.gridy = 0;
/*  801: 957 */     gbC.gridx = 1;
/*  802: 958 */     gbC.gridheight = 3;
/*  803: 959 */     gbC.weightx = 100.0D;
/*  804: 960 */     gbC.weighty = 100.0D;
/*  805: 961 */     gbL.setConstraints(p3, gbC);
/*  806: 962 */     mondo.add(p3);
/*  807:     */     
/*  808: 964 */     setLayout(new BorderLayout());
/*  809: 965 */     add(p1, "North");
/*  810: 966 */     add(mondo, "Center");
/*  811:     */   }
/*  812:     */   
/*  813:     */   protected void updateRadioLinks()
/*  814:     */   {
/*  815: 974 */     this.m_SetTestBut.setEnabled(this.m_TestSplitBut.isSelected());
/*  816: 975 */     if ((this.m_SetTestFrame != null) && (!this.m_TestSplitBut.isSelected())) {
/*  817: 976 */       this.m_SetTestFrame.setVisible(false);
/*  818:     */     }
/*  819: 978 */     this.m_CVText.setEnabled(this.m_CVBut.isSelected());
/*  820: 979 */     this.m_CVLab.setEnabled(this.m_CVBut.isSelected());
/*  821: 980 */     this.m_PercentText.setEnabled(this.m_PercentBut.isSelected());
/*  822: 981 */     this.m_PercentLab.setEnabled(this.m_PercentBut.isSelected());
/*  823:     */   }
/*  824:     */   
/*  825:     */   public void setLog(Logger newLog)
/*  826:     */   {
/*  827: 992 */     this.m_Log = newLog;
/*  828:     */   }
/*  829:     */   
/*  830:     */   public void setInstances(Instances inst)
/*  831:     */   {
/*  832:1002 */     this.m_Instances = inst;
/*  833:     */     
/*  834:1004 */     String[] attribNames = new String[this.m_Instances.numAttributes()];
/*  835:1005 */     for (int i = 0; i < attribNames.length; i++)
/*  836:     */     {
/*  837:1006 */       String type = "(" + Attribute.typeToStringShort(this.m_Instances.attribute(i)) + ") ";
/*  838:     */       
/*  839:1008 */       attribNames[i] = (type + this.m_Instances.attribute(i).name());
/*  840:     */     }
/*  841:1010 */     this.m_ClassCombo.setModel(new DefaultComboBoxModel(attribNames));
/*  842:1011 */     if (attribNames.length > 0)
/*  843:     */     {
/*  844:1012 */       if (inst.classIndex() == -1) {
/*  845:1013 */         this.m_ClassCombo.setSelectedIndex(attribNames.length - 1);
/*  846:     */       } else {
/*  847:1015 */         this.m_ClassCombo.setSelectedIndex(inst.classIndex());
/*  848:     */       }
/*  849:1017 */       this.m_ClassCombo.setEnabled(true);
/*  850:1018 */       this.m_StartBut.setEnabled(this.m_RunThread == null);
/*  851:1019 */       this.m_StopBut.setEnabled(this.m_RunThread != null);
/*  852:     */     }
/*  853:     */     else
/*  854:     */     {
/*  855:1021 */       this.m_StartBut.setEnabled(false);
/*  856:1022 */       this.m_StopBut.setEnabled(false);
/*  857:     */     }
/*  858:     */   }
/*  859:     */   
/*  860:     */   protected void setTestSet()
/*  861:     */   {
/*  862:1034 */     if (this.m_SetTestFrame == null)
/*  863:     */     {
/*  864:1035 */       PreprocessPanel preprocessPanel = null;
/*  865:1036 */       if (this.m_Explorer != null)
/*  866:     */       {
/*  867:1037 */         preprocessPanel = this.m_Explorer.getPreprocessPanel();
/*  868:     */       }
/*  869:1038 */       else if (getMainApplication() != null)
/*  870:     */       {
/*  871:1039 */         Perspective p = getMainApplication().getPerspectiveManager().getPerspective("weka.gui.explorer.preprocesspanel");
/*  872:     */         
/*  873:     */ 
/*  874:1042 */         preprocessPanel = (PreprocessPanel)p;
/*  875:     */       }
/*  876:     */       else
/*  877:     */       {
/*  878:1044 */         throw new IllegalStateException("We don't have access to a PreprocessPanel!");
/*  879:     */       }
/*  880:1048 */       final SetInstancesPanel sp = new SetInstancesPanel(true, true, preprocessPanel.m_FileChooser);
/*  881:1051 */       if (this.m_TestLoader != null) {
/*  882:     */         try
/*  883:     */         {
/*  884:1053 */           if (this.m_TestLoader.getStructure() != null) {
/*  885:1054 */             sp.setInstances(this.m_TestLoader.getStructure());
/*  886:     */           }
/*  887:     */         }
/*  888:     */         catch (Exception ex)
/*  889:     */         {
/*  890:1057 */           ex.printStackTrace();
/*  891:     */         }
/*  892:     */       }
/*  893:1060 */       sp.addPropertyChangeListener(new PropertyChangeListener()
/*  894:     */       {
/*  895:     */         public void propertyChange(PropertyChangeEvent e)
/*  896:     */         {
/*  897:1063 */           ClassifierPanel.this.m_TestLoader = sp.getLoader();
/*  898:1064 */           ClassifierPanel.this.m_TestClassIndex = sp.getClassIndex();
/*  899:     */         }
/*  900:1068 */       });
/*  901:1069 */       this.m_SetTestFrame = new JFrame("Test Instances");
/*  902:1070 */       sp.setParentFrame(this.m_SetTestFrame);
/*  903:1071 */       this.m_SetTestFrame.getContentPane().setLayout(new BorderLayout());
/*  904:1072 */       this.m_SetTestFrame.getContentPane().add(sp, "Center");
/*  905:1073 */       this.m_SetTestFrame.pack();
/*  906:     */     }
/*  907:1075 */     this.m_SetTestFrame.setVisible(true);
/*  908:     */   }
/*  909:     */   
/*  910:     */   protected void printPredictionsHeader(StringBuffer outBuff, AbstractOutput classificationOutput, String title)
/*  911:     */   {
/*  912:1087 */     if (classificationOutput.generatesOutput()) {
/*  913:1088 */       outBuff.append("=== Predictions on " + title + " ===\n\n");
/*  914:     */     }
/*  915:1090 */     classificationOutput.printHeader();
/*  916:     */   }
/*  917:     */   
/*  918:     */   protected static Evaluation setupEval(Evaluation eval, Classifier classifier, Instances inst, CostMatrix costMatrix, ClassifierErrorsPlotInstances plotInstances, AbstractOutput classificationOutput, boolean onlySetPriors)
/*  919:     */     throws Exception
/*  920:     */   {
/*  921:1099 */     if ((classifier instanceof InputMappedClassifier))
/*  922:     */     {
/*  923:1100 */       Instances mappedClassifierHeader = ((InputMappedClassifier)classifier).getModelHeader(new Instances(inst, 0));
/*  924:1104 */       if (classificationOutput != null) {
/*  925:1105 */         classificationOutput.setHeader(mappedClassifierHeader);
/*  926:     */       }
/*  927:1108 */       if (!onlySetPriors) {
/*  928:1109 */         if (costMatrix != null) {
/*  929:1110 */           eval = new Evaluation(new Instances(mappedClassifierHeader, 0), costMatrix);
/*  930:     */         } else {
/*  931:1113 */           eval = new Evaluation(new Instances(mappedClassifierHeader, 0));
/*  932:     */         }
/*  933:     */       }
/*  934:1117 */       if (!eval.getHeader().equalHeaders(inst))
/*  935:     */       {
/*  936:1123 */         Instances mappedClassifierDataset = ((InputMappedClassifier)classifier).getModelHeader(new Instances(mappedClassifierHeader, 0));
/*  937:1126 */         for (int zz = 0; zz < inst.numInstances(); zz++)
/*  938:     */         {
/*  939:1127 */           Instance mapped = ((InputMappedClassifier)classifier).constructMappedInstance(inst.instance(zz));
/*  940:     */           
/*  941:     */ 
/*  942:1130 */           mappedClassifierDataset.add(mapped);
/*  943:     */         }
/*  944:1132 */         eval.setPriors(mappedClassifierDataset);
/*  945:1133 */         if ((!onlySetPriors) && 
/*  946:1134 */           (plotInstances != null))
/*  947:     */         {
/*  948:1135 */           plotInstances.setInstances(mappedClassifierDataset);
/*  949:1136 */           plotInstances.setClassifier(classifier);
/*  950:     */           
/*  951:     */ 
/*  952:     */ 
/*  953:     */ 
/*  954:     */ 
/*  955:     */ 
/*  956:1143 */           plotInstances.setClassIndex(mappedClassifierDataset.classIndex());
/*  957:1144 */           plotInstances.setEvaluation(eval);
/*  958:     */         }
/*  959:     */       }
/*  960:     */       else
/*  961:     */       {
/*  962:1148 */         eval.setPriors(inst);
/*  963:1149 */         if ((!onlySetPriors) && 
/*  964:1150 */           (plotInstances != null))
/*  965:     */         {
/*  966:1151 */           plotInstances.setInstances(inst);
/*  967:1152 */           plotInstances.setClassifier(classifier);
/*  968:1153 */           plotInstances.setClassIndex(inst.classIndex());
/*  969:1154 */           plotInstances.setEvaluation(eval);
/*  970:     */         }
/*  971:     */       }
/*  972:     */     }
/*  973:     */     else
/*  974:     */     {
/*  975:1159 */       eval.setPriors(inst);
/*  976:1160 */       if ((!onlySetPriors) && 
/*  977:1161 */         (plotInstances != null))
/*  978:     */       {
/*  979:1162 */         plotInstances.setInstances(inst);
/*  980:1163 */         plotInstances.setClassifier(classifier);
/*  981:1164 */         plotInstances.setClassIndex(inst.classIndex());
/*  982:1165 */         plotInstances.setEvaluation(eval);
/*  983:     */       }
/*  984:     */     }
/*  985:1170 */     return eval;
/*  986:     */   }
/*  987:     */   
/*  988:     */   protected void startClassifier()
/*  989:     */   {
/*  990:1181 */     if (this.m_RunThread == null)
/*  991:     */     {
/*  992:1182 */       synchronized (this)
/*  993:     */       {
/*  994:1183 */         this.m_StartBut.setEnabled(false);
/*  995:1184 */         this.m_StopBut.setEnabled(true);
/*  996:     */       }
/*  997:1186 */       this.m_RunThread = new Thread()
/*  998:     */       {
/*  999:     */         public void run()
/* 1000:     */         {
/* 1001:1189 */           ClassifierPanel.this.m_CEPanel.addToHistory();
/* 1002:     */           
/* 1003:     */ 
/* 1004:1192 */           ClassifierPanel.this.m_Log.statusMessage("Setting up...");
/* 1005:1193 */           CostMatrix costMatrix = null;
/* 1006:1194 */           Instances inst = new Instances(ClassifierPanel.this.m_Instances);
/* 1007:1195 */           ConverterUtils.DataSource source = null;
/* 1008:1196 */           Instances userTestStructure = null;
/* 1009:1197 */           ClassifierErrorsPlotInstances plotInstances = null;
/* 1010:     */           
/* 1011:     */ 
/* 1012:1200 */           long trainTimeStart = 0L;long trainTimeElapsed = 0L;
/* 1013:1201 */           long testTimeStart = 0L;long testTimeElapsed = 0L;
/* 1014:     */           try
/* 1015:     */           {
/* 1016:1204 */             if ((ClassifierPanel.this.m_TestLoader != null) && (ClassifierPanel.this.m_TestLoader.getStructure() != null))
/* 1017:     */             {
/* 1018:1205 */               if (((ClassifierPanel.this.m_ClassifierEditor.getValue() instanceof BatchPredictor)) && (((BatchPredictor)ClassifierPanel.this.m_ClassifierEditor.getValue()).implementsMoreEfficientBatchPrediction()) && ((ClassifierPanel.this.m_TestLoader instanceof ArffLoader))) {
/* 1019:1210 */                 ((ArffLoader)ClassifierPanel.this.m_TestLoader).setRetainStringVals(true);
/* 1020:     */               }
/* 1021:1212 */               ClassifierPanel.this.m_TestLoader.reset();
/* 1022:1213 */               source = new ConverterUtils.DataSource(ClassifierPanel.this.m_TestLoader);
/* 1023:1214 */               userTestStructure = source.getStructure();
/* 1024:1215 */               userTestStructure.setClassIndex(ClassifierPanel.this.m_TestClassIndex);
/* 1025:     */             }
/* 1026:     */           }
/* 1027:     */           catch (Exception ex)
/* 1028:     */           {
/* 1029:1218 */             ex.printStackTrace();
/* 1030:     */           }
/* 1031:1220 */           if (ClassifierPanel.this.m_EvalWRTCostsBut.isSelected()) {
/* 1032:1221 */             costMatrix = new CostMatrix((CostMatrix)ClassifierPanel.this.m_CostMatrixEditor.getValue());
/* 1033:     */           }
/* 1034:1224 */           boolean outputModel = ClassifierPanel.this.m_OutputModelBut.isSelected();
/* 1035:1225 */           boolean outputConfusion = ClassifierPanel.this.m_OutputConfusionBut.isSelected();
/* 1036:1226 */           boolean outputPerClass = ClassifierPanel.this.m_OutputPerClassBut.isSelected();
/* 1037:1227 */           boolean outputSummary = true;
/* 1038:1228 */           boolean outputEntropy = ClassifierPanel.this.m_OutputEntropyBut.isSelected();
/* 1039:1229 */           boolean saveVis = ClassifierPanel.this.m_StorePredictionsBut.isSelected();
/* 1040:1230 */           boolean outputPredictionsText = ClassifierPanel.this.m_ClassificationOutputEditor.getValue().getClass() != Null.class;
/* 1041:     */           
/* 1042:     */ 
/* 1043:1233 */           String grph = null;
/* 1044:     */           
/* 1045:1235 */           int testMode = 0;
/* 1046:1236 */           int numFolds = 10;
/* 1047:1237 */           double percent = 66.0D;
/* 1048:1238 */           int classIndex = ClassifierPanel.this.m_ClassCombo.getSelectedIndex();
/* 1049:1239 */           inst.setClassIndex(classIndex);
/* 1050:1240 */           Classifier classifier = (Classifier)ClassifierPanel.this.m_ClassifierEditor.getValue();
/* 1051:1241 */           Classifier template = null;
/* 1052:     */           try
/* 1053:     */           {
/* 1054:1243 */             template = AbstractClassifier.makeCopy(classifier);
/* 1055:     */           }
/* 1056:     */           catch (Exception ex)
/* 1057:     */           {
/* 1058:1245 */             ClassifierPanel.this.m_Log.logMessage("Problem copying classifier: " + ex.getMessage());
/* 1059:     */           }
/* 1060:1247 */           Classifier fullClassifier = null;
/* 1061:1248 */           StringBuffer outBuff = new StringBuffer();
/* 1062:1249 */           AbstractOutput classificationOutput = null;
/* 1063:1250 */           if (outputPredictionsText)
/* 1064:     */           {
/* 1065:1251 */             classificationOutput = (AbstractOutput)ClassifierPanel.this.m_ClassificationOutputEditor.getValue();
/* 1066:     */             
/* 1067:1253 */             Instances header = new Instances(inst, 0);
/* 1068:1254 */             header.setClassIndex(classIndex);
/* 1069:1255 */             classificationOutput.setHeader(header);
/* 1070:1256 */             classificationOutput.setBuffer(outBuff);
/* 1071:     */           }
/* 1072:1258 */           String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 1073:     */           
/* 1074:1260 */           String cname = "";
/* 1075:1261 */           String cmd = "";
/* 1076:1262 */           Evaluation eval = null;
/* 1077:     */           try
/* 1078:     */           {
/* 1079:1264 */             if (ClassifierPanel.this.m_CVBut.isSelected())
/* 1080:     */             {
/* 1081:1265 */               testMode = 1;
/* 1082:1266 */               numFolds = Integer.parseInt(ClassifierPanel.this.m_CVText.getText());
/* 1083:1267 */               if (numFolds <= 1) {
/* 1084:1268 */                 throw new Exception("Number of folds must be greater than 1");
/* 1085:     */               }
/* 1086:     */             }
/* 1087:1270 */             else if (ClassifierPanel.this.m_PercentBut.isSelected())
/* 1088:     */             {
/* 1089:1271 */               testMode = 2;
/* 1090:1272 */               percent = Double.parseDouble(ClassifierPanel.this.m_PercentText.getText());
/* 1091:1273 */               if ((percent <= 0.0D) || (percent >= 100.0D)) {
/* 1092:1274 */                 throw new Exception("Percentage must be between 0 and 100");
/* 1093:     */               }
/* 1094:     */             }
/* 1095:1276 */             else if (ClassifierPanel.this.m_TrainBut.isSelected())
/* 1096:     */             {
/* 1097:1277 */               testMode = 3;
/* 1098:     */             }
/* 1099:1278 */             else if (ClassifierPanel.this.m_TestSplitBut.isSelected())
/* 1100:     */             {
/* 1101:1279 */               testMode = 4;
/* 1102:1281 */               if (source == null) {
/* 1103:1282 */                 throw new Exception("No user test set has been specified");
/* 1104:     */               }
/* 1105:1285 */               if ((!(classifier instanceof InputMappedClassifier)) && 
/* 1106:1286 */                 (!inst.equalHeaders(userTestStructure)))
/* 1107:     */               {
/* 1108:1287 */                 boolean wrapClassifier = false;
/* 1109:1288 */                 if (!Utils.getDontShowDialog("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier"))
/* 1110:     */                 {
/* 1111:1290 */                   JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 1112:     */                   
/* 1113:1292 */                   Object[] stuff = new Object[2];
/* 1114:1293 */                   stuff[0] = "Train and test set are not compatible.\nWould you like to automatically wrap the classifier in\nan \"InputMappedClassifier\" before proceeding?.\n";
/* 1115:     */                   
/* 1116:     */ 
/* 1117:     */ 
/* 1118:1297 */                   stuff[1] = dontShow;
/* 1119:     */                   
/* 1120:1299 */                   int result = JOptionPane.showConfirmDialog(ClassifierPanel.this, stuff, "ClassifierPanel", 0);
/* 1121:1303 */                   if (result == 0) {
/* 1122:1304 */                     wrapClassifier = true;
/* 1123:     */                   }
/* 1124:1307 */                   if (dontShow.isSelected())
/* 1125:     */                   {
/* 1126:1308 */                     String response = wrapClassifier ? "yes" : "no";
/* 1127:1309 */                     Utils.setDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier", response);
/* 1128:     */                   }
/* 1129:     */                 }
/* 1130:     */                 else
/* 1131:     */                 {
/* 1132:1317 */                   String response = Utils.getDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier");
/* 1133:1320 */                   if ((response != null) && (response.equalsIgnoreCase("yes"))) {
/* 1134:1321 */                     wrapClassifier = true;
/* 1135:     */                   }
/* 1136:     */                 }
/* 1137:1325 */                 if (wrapClassifier)
/* 1138:     */                 {
/* 1139:1326 */                   InputMappedClassifier temp = new InputMappedClassifier();
/* 1140:     */                   
/* 1141:     */ 
/* 1142:     */ 
/* 1143:     */ 
/* 1144:     */ 
/* 1145:1332 */                   temp.setClassifier(classifier);
/* 1146:1333 */                   temp.setTestStructure(userTestStructure);
/* 1147:1334 */                   classifier = temp;
/* 1148:     */                 }
/* 1149:     */                 else
/* 1150:     */                 {
/* 1151:1336 */                   throw new Exception("Train and test set are not compatible\n" + inst.equalHeadersMsg(userTestStructure));
/* 1152:     */                 }
/* 1153:     */               }
/* 1154:     */             }
/* 1155:     */             else
/* 1156:     */             {
/* 1157:1344 */               throw new Exception("Unknown test mode");
/* 1158:     */             }
/* 1159:1347 */             cname = classifier.getClass().getName();
/* 1160:1348 */             if (cname.startsWith("weka.classifiers.")) {
/* 1161:1349 */               name = name + cname.substring("weka.classifiers.".length());
/* 1162:     */             } else {
/* 1163:1351 */               name = name + cname;
/* 1164:     */             }
/* 1165:1353 */             cmd = classifier.getClass().getName();
/* 1166:1354 */             if ((classifier instanceof OptionHandler)) {
/* 1167:1355 */               cmd = cmd + " " + Utils.joinOptions(((OptionHandler)classifier).getOptions());
/* 1168:     */             }
/* 1169:1363 */             plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/* 1170:1364 */             plotInstances.setInstances(inst);
/* 1171:1365 */             plotInstances.setClassifier(classifier);
/* 1172:1366 */             plotInstances.setClassIndex(inst.classIndex());
/* 1173:1367 */             plotInstances.setSaveForVisualization(saveVis);
/* 1174:1368 */             plotInstances.setPointSizeProportionalToMargin(ClassifierPanel.this.m_errorPlotPointSizeProportionalToMargin.isSelected());
/* 1175:     */             
/* 1176:     */ 
/* 1177:     */ 
/* 1178:     */ 
/* 1179:1373 */             ClassifierPanel.this.m_Log.logMessage("Started " + cname);
/* 1180:1374 */             ClassifierPanel.this.m_Log.logMessage("Command: " + cmd);
/* 1181:1375 */             if ((ClassifierPanel.this.m_Log instanceof TaskLogger)) {
/* 1182:1376 */               ((TaskLogger)ClassifierPanel.this.m_Log).taskStarted();
/* 1183:     */             }
/* 1184:1378 */             outBuff.append("=== Run information ===\n\n");
/* 1185:1379 */             outBuff.append("Scheme:       " + cname);
/* 1186:1380 */             if ((classifier instanceof OptionHandler))
/* 1187:     */             {
/* 1188:1381 */               String[] o = ((OptionHandler)classifier).getOptions();
/* 1189:1382 */               outBuff.append(" " + Utils.joinOptions(o));
/* 1190:     */             }
/* 1191:1384 */             outBuff.append("\n");
/* 1192:1385 */             outBuff.append("Relation:     " + inst.relationName() + '\n');
/* 1193:1386 */             outBuff.append("Instances:    " + inst.numInstances() + '\n');
/* 1194:1387 */             outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
/* 1195:1388 */             if (inst.numAttributes() < 100) {
/* 1196:1389 */               for (int i = 0; i < inst.numAttributes(); i++) {
/* 1197:1390 */                 outBuff.append("              " + inst.attribute(i).name() + '\n');
/* 1198:     */               }
/* 1199:     */             } else {
/* 1200:1394 */               outBuff.append("              [list of attributes omitted]\n");
/* 1201:     */             }
/* 1202:1397 */             outBuff.append("Test mode:    ");
/* 1203:1398 */             switch (testMode)
/* 1204:     */             {
/* 1205:     */             case 3: 
/* 1206:1400 */               outBuff.append("evaluate on training data\n");
/* 1207:1401 */               break;
/* 1208:     */             case 1: 
/* 1209:1403 */               outBuff.append("" + numFolds + "-fold cross-validation\n");
/* 1210:1404 */               break;
/* 1211:     */             case 2: 
/* 1212:1406 */               outBuff.append("split " + percent + "% train, remainder test\n");
/* 1213:1407 */               break;
/* 1214:     */             case 4: 
/* 1215:1409 */               if (source.isIncremental()) {
/* 1216:1410 */                 outBuff.append("user supplied test set:  size unknown (reading incrementally)\n");
/* 1217:     */               } else {
/* 1218:1413 */                 outBuff.append("user supplied test set: " + source.getDataSet().numInstances() + " instances\n");
/* 1219:     */               }
/* 1220:     */               break;
/* 1221:     */             }
/* 1222:1418 */             if (costMatrix != null) {
/* 1223:1419 */               outBuff.append("Evaluation cost matrix:\n").append(costMatrix.toString()).append("\n");
/* 1224:     */             }
/* 1225:1422 */             outBuff.append("\n");
/* 1226:1423 */             ClassifierPanel.this.m_History.addResult(name, outBuff);
/* 1227:1424 */             ClassifierPanel.this.m_History.setSingle(name);
/* 1228:1427 */             if ((outputModel) || (testMode == 3) || (testMode == 4))
/* 1229:     */             {
/* 1230:1428 */               ClassifierPanel.this.m_Log.statusMessage("Building model on training data...");
/* 1231:     */               
/* 1232:1430 */               trainTimeStart = System.currentTimeMillis();
/* 1233:1431 */               classifier.buildClassifier(inst);
/* 1234:1432 */               trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/* 1235:     */             }
/* 1236:1435 */             if (outputModel)
/* 1237:     */             {
/* 1238:1436 */               outBuff.append("=== Classifier model (full training set) ===\n\n");
/* 1239:     */               
/* 1240:1438 */               outBuff.append(classifier.toString() + "\n");
/* 1241:1439 */               outBuff.append("\nTime taken to build model: " + Utils.doubleToString(trainTimeElapsed / 1000.0D, 2) + " seconds\n\n");
/* 1242:     */               
/* 1243:     */ 
/* 1244:1442 */               ClassifierPanel.this.m_History.updateResult(name);
/* 1245:1443 */               if ((classifier instanceof Drawable))
/* 1246:     */               {
/* 1247:1444 */                 grph = null;
/* 1248:     */                 try
/* 1249:     */                 {
/* 1250:1446 */                   grph = ((Drawable)classifier).graph();
/* 1251:     */                 }
/* 1252:     */                 catch (Exception ex) {}
/* 1253:     */               }
/* 1254:1451 */               SerializedObject so = new SerializedObject(classifier);
/* 1255:1452 */               fullClassifier = (Classifier)so.getObject();
/* 1256:     */             }
/* 1257:     */             int rnd;
/* 1258:1455 */             switch (testMode)
/* 1259:     */             {
/* 1260:     */             case 3: 
/* 1261:1457 */               ClassifierPanel.this.m_Log.statusMessage("Evaluating on training data...");
/* 1262:1458 */               eval = new Evaluation(inst, costMatrix);
/* 1263:     */               
/* 1264:     */ 
/* 1265:1461 */               eval = ClassifierPanel.setupEval(eval, classifier, inst, costMatrix, plotInstances, classificationOutput, false);
/* 1266:     */               
/* 1267:     */ 
/* 1268:1464 */               eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 1269:     */               
/* 1270:     */ 
/* 1271:1467 */               plotInstances.setUp();
/* 1272:1469 */               if (outputPredictionsText) {
/* 1273:1470 */                 ClassifierPanel.this.printPredictionsHeader(outBuff, classificationOutput, "training set");
/* 1274:     */               }
/* 1275:1474 */               testTimeStart = System.currentTimeMillis();
/* 1276:1475 */               if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1277:     */               {
/* 1278:1478 */                 Instances toPred = new Instances(inst);
/* 1279:1479 */                 for (int i = 0; i < toPred.numInstances(); i++) {
/* 1280:1480 */                   toPred.instance(i).setClassMissing();
/* 1281:     */                 }
/* 1282:1482 */                 double[][] predictions = ((BatchPredictor)classifier).distributionsForInstances(toPred);
/* 1283:     */                 
/* 1284:     */ 
/* 1285:1485 */                 plotInstances.process(inst, predictions, eval);
/* 1286:1486 */                 if (outputPredictionsText) {
/* 1287:1487 */                   for (int jj = 0; jj < inst.numInstances(); jj++) {
/* 1288:1488 */                     classificationOutput.printClassification(predictions[jj], inst.instance(jj), jj);
/* 1289:     */                   }
/* 1290:     */                 }
/* 1291:     */               }
/* 1292:     */               else
/* 1293:     */               {
/* 1294:1493 */                 for (int jj = 0; jj < inst.numInstances(); jj++)
/* 1295:     */                 {
/* 1296:1494 */                   plotInstances.process(inst.instance(jj), classifier, eval);
/* 1297:1496 */                   if (outputPredictionsText) {
/* 1298:1497 */                     classificationOutput.printClassification(classifier, inst.instance(jj), jj);
/* 1299:     */                   }
/* 1300:1500 */                   if (jj % 100 == 0) {
/* 1301:1501 */                     ClassifierPanel.this.m_Log.statusMessage("Evaluating on training data. Processed " + jj + " instances...");
/* 1302:     */                   }
/* 1303:     */                 }
/* 1304:     */               }
/* 1305:1507 */               testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 1306:1508 */               if (outputPredictionsText) {
/* 1307:1509 */                 classificationOutput.printFooter();
/* 1308:     */               }
/* 1309:1511 */               if ((outputPredictionsText) && (classificationOutput.generatesOutput())) {
/* 1310:1513 */                 outBuff.append("\n");
/* 1311:     */               }
/* 1312:1515 */               outBuff.append("=== Evaluation on training set ===\n");
/* 1313:1516 */               break;
/* 1314:     */             case 1: 
/* 1315:1519 */               ClassifierPanel.this.m_Log.statusMessage("Randomizing instances...");
/* 1316:1520 */               rnd = 1;
/* 1317:     */               try
/* 1318:     */               {
/* 1319:1522 */                 rnd = Integer.parseInt(ClassifierPanel.this.m_RandomSeedText.getText().trim());
/* 1320:     */               }
/* 1321:     */               catch (Exception ex)
/* 1322:     */               {
/* 1323:1525 */                 ClassifierPanel.this.m_Log.logMessage("Trouble parsing random seed value");
/* 1324:1526 */                 rnd = 1;
/* 1325:     */               }
/* 1326:1528 */               Random random = new Random(rnd);
/* 1327:1529 */               inst.randomize(random);
/* 1328:1530 */               if (inst.attribute(classIndex).isNominal())
/* 1329:     */               {
/* 1330:1531 */                 ClassifierPanel.this.m_Log.statusMessage("Stratifying instances...");
/* 1331:1532 */                 inst.stratify(numFolds);
/* 1332:     */               }
/* 1333:1534 */               eval = new Evaluation(inst, costMatrix);
/* 1334:     */               
/* 1335:     */ 
/* 1336:1537 */               eval = ClassifierPanel.setupEval(eval, classifier, inst, costMatrix, plotInstances, classificationOutput, false);
/* 1337:     */               
/* 1338:     */ 
/* 1339:1540 */               eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 1340:     */               
/* 1341:     */ 
/* 1342:1543 */               plotInstances.setUp();
/* 1343:1545 */               if (outputPredictionsText) {
/* 1344:1546 */                 ClassifierPanel.this.printPredictionsHeader(outBuff, classificationOutput, "test data");
/* 1345:     */               }
/* 1346:1551 */               for (int fold = 0; fold < numFolds; fold++)
/* 1347:     */               {
/* 1348:1552 */                 ClassifierPanel.this.m_Log.statusMessage("Creating splits for fold " + (fold + 1) + "...");
/* 1349:     */                 
/* 1350:1554 */                 Instances train = inst.trainCV(numFolds, fold, random);
/* 1351:     */                 
/* 1352:     */ 
/* 1353:     */ 
/* 1354:1558 */                 eval = ClassifierPanel.setupEval(eval, classifier, train, costMatrix, plotInstances, classificationOutput, true);
/* 1355:     */                 
/* 1356:     */ 
/* 1357:1561 */                 eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 1358:     */                 
/* 1359:     */ 
/* 1360:1564 */                 ClassifierPanel.this.m_Log.statusMessage("Building model for fold " + (fold + 1) + "...");
/* 1361:     */                 
/* 1362:1566 */                 Classifier current = null;
/* 1363:     */                 try
/* 1364:     */                 {
/* 1365:1568 */                   current = AbstractClassifier.makeCopy(template);
/* 1366:     */                 }
/* 1367:     */                 catch (Exception ex)
/* 1368:     */                 {
/* 1369:1570 */                   ClassifierPanel.this.m_Log.logMessage("Problem copying classifier: " + ex.getMessage());
/* 1370:     */                 }
/* 1371:1573 */                 current.buildClassifier(train);
/* 1372:1574 */                 Instances test = inst.testCV(numFolds, fold);
/* 1373:1575 */                 ClassifierPanel.this.m_Log.statusMessage("Evaluating model for fold " + (fold + 1) + "...");
/* 1374:1578 */                 if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1375:     */                 {
/* 1376:1581 */                   Instances toPred = new Instances(test);
/* 1377:1582 */                   for (int i = 0; i < toPred.numInstances(); i++) {
/* 1378:1583 */                     toPred.instance(i).setClassMissing();
/* 1379:     */                   }
/* 1380:1585 */                   double[][] predictions = ((BatchPredictor)current).distributionsForInstances(toPred);
/* 1381:     */                   
/* 1382:     */ 
/* 1383:1588 */                   plotInstances.process(test, predictions, eval);
/* 1384:1589 */                   if (outputPredictionsText) {
/* 1385:1590 */                     for (int jj = 0; jj < test.numInstances(); jj++) {
/* 1386:1591 */                       classificationOutput.printClassification(predictions[jj], test.instance(jj), jj);
/* 1387:     */                     }
/* 1388:     */                   }
/* 1389:     */                 }
/* 1390:     */                 else
/* 1391:     */                 {
/* 1392:1596 */                   for (int jj = 0; jj < test.numInstances(); jj++)
/* 1393:     */                   {
/* 1394:1597 */                     plotInstances.process(test.instance(jj), current, eval);
/* 1395:1598 */                     if (outputPredictionsText) {
/* 1396:1599 */                       classificationOutput.printClassification(current, test.instance(jj), jj);
/* 1397:     */                     }
/* 1398:     */                   }
/* 1399:     */                 }
/* 1400:     */               }
/* 1401:1605 */               if (outputPredictionsText) {
/* 1402:1606 */                 classificationOutput.printFooter();
/* 1403:     */               }
/* 1404:1608 */               if (outputPredictionsText) {
/* 1405:1609 */                 outBuff.append("\n");
/* 1406:     */               }
/* 1407:1611 */               if (inst.attribute(classIndex).isNominal()) {
/* 1408:1612 */                 outBuff.append("=== Stratified cross-validation ===\n");
/* 1409:     */               } else {
/* 1410:1614 */                 outBuff.append("=== Cross-validation ===\n");
/* 1411:     */               }
/* 1412:1616 */               break;
/* 1413:     */             case 2: 
/* 1414:1619 */               if (!ClassifierPanel.this.m_PreserveOrderBut.isSelected())
/* 1415:     */               {
/* 1416:1620 */                 ClassifierPanel.this.m_Log.statusMessage("Randomizing instances...");
/* 1417:     */                 try
/* 1418:     */                 {
/* 1419:1622 */                   rnd = Integer.parseInt(ClassifierPanel.this.m_RandomSeedText.getText().trim());
/* 1420:     */                 }
/* 1421:     */                 catch (Exception ex)
/* 1422:     */                 {
/* 1423:1624 */                   ClassifierPanel.this.m_Log.logMessage("Trouble parsing random seed value");
/* 1424:1625 */                   rnd = 1;
/* 1425:     */                 }
/* 1426:1627 */                 inst.randomize(new Random(rnd));
/* 1427:     */               }
/* 1428:1629 */               int trainSize = (int)Math.round(inst.numInstances() * percent / 100.0D);
/* 1429:     */               
/* 1430:1631 */               int testSize = inst.numInstances() - trainSize;
/* 1431:1632 */               Instances train = new Instances(inst, 0, trainSize);
/* 1432:1633 */               Instances test = new Instances(inst, trainSize, testSize);
/* 1433:1634 */               ClassifierPanel.this.m_Log.statusMessage("Building model on training split (" + trainSize + " instances)...");
/* 1434:     */               
/* 1435:1636 */               Classifier current = null;
/* 1436:     */               try
/* 1437:     */               {
/* 1438:1638 */                 current = AbstractClassifier.makeCopy(template);
/* 1439:     */               }
/* 1440:     */               catch (Exception ex)
/* 1441:     */               {
/* 1442:1640 */                 ClassifierPanel.this.m_Log.logMessage("Problem copying classifier: " + ex.getMessage());
/* 1443:     */               }
/* 1444:1643 */               current.buildClassifier(train);
/* 1445:1644 */               eval = new Evaluation(train, costMatrix);
/* 1446:     */               
/* 1447:     */ 
/* 1448:1647 */               eval = ClassifierPanel.setupEval(eval, classifier, train, costMatrix, plotInstances, classificationOutput, false);
/* 1449:     */               
/* 1450:     */ 
/* 1451:1650 */               eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 1452:     */               
/* 1453:     */ 
/* 1454:1653 */               plotInstances.setUp();
/* 1455:1654 */               ClassifierPanel.this.m_Log.statusMessage("Evaluating on test split...");
/* 1456:1656 */               if (outputPredictionsText) {
/* 1457:1657 */                 ClassifierPanel.this.printPredictionsHeader(outBuff, classificationOutput, "test split");
/* 1458:     */               }
/* 1459:1661 */               testTimeStart = System.currentTimeMillis();
/* 1460:1662 */               if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1461:     */               {
/* 1462:1665 */                 Instances toPred = new Instances(test);
/* 1463:1666 */                 for (int i = 0; i < toPred.numInstances(); i++) {
/* 1464:1667 */                   toPred.instance(i).setClassMissing();
/* 1465:     */                 }
/* 1466:1670 */                 double[][] predictions = ((BatchPredictor)current).distributionsForInstances(toPred);
/* 1467:     */                 
/* 1468:1672 */                 plotInstances.process(test, predictions, eval);
/* 1469:1673 */                 if (outputPredictionsText) {
/* 1470:1674 */                   for (int jj = 0; jj < test.numInstances(); jj++) {
/* 1471:1675 */                     classificationOutput.printClassification(predictions[jj], test.instance(jj), jj);
/* 1472:     */                   }
/* 1473:     */                 }
/* 1474:     */               }
/* 1475:     */               else
/* 1476:     */               {
/* 1477:1680 */                 for (int jj = 0; jj < test.numInstances(); jj++)
/* 1478:     */                 {
/* 1479:1681 */                   plotInstances.process(test.instance(jj), current, eval);
/* 1480:1682 */                   if (outputPredictionsText) {
/* 1481:1683 */                     classificationOutput.printClassification(current, test.instance(jj), jj);
/* 1482:     */                   }
/* 1483:1686 */                   if (jj % 100 == 0) {
/* 1484:1687 */                     ClassifierPanel.this.m_Log.statusMessage("Evaluating on test split. Processed " + jj + " instances...");
/* 1485:     */                   }
/* 1486:     */                 }
/* 1487:     */               }
/* 1488:1692 */               testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 1489:1693 */               if (outputPredictionsText) {
/* 1490:1694 */                 classificationOutput.printFooter();
/* 1491:     */               }
/* 1492:1696 */               if (outputPredictionsText) {
/* 1493:1697 */                 outBuff.append("\n");
/* 1494:     */               }
/* 1495:1699 */               outBuff.append("=== Evaluation on test split ===\n");
/* 1496:1700 */               break;
/* 1497:     */             case 4: 
/* 1498:1703 */               ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data...");
/* 1499:1704 */               eval = new Evaluation(inst, costMatrix);
/* 1500:     */               
/* 1501:1706 */               eval = ClassifierPanel.setupEval(eval, classifier, inst, costMatrix, plotInstances, classificationOutput, false);
/* 1502:     */               
/* 1503:     */ 
/* 1504:1709 */               eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 1505:     */               
/* 1506:     */ 
/* 1507:1712 */               plotInstances.setUp();
/* 1508:1714 */               if (outputPredictionsText) {
/* 1509:1715 */                 ClassifierPanel.this.printPredictionsHeader(outBuff, classificationOutput, "test set");
/* 1510:     */               }
/* 1511:1720 */               int jj = 0;
/* 1512:1721 */               Instances batchInst = null;
/* 1513:1722 */               int batchSize = 100;
/* 1514:1723 */               if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1515:     */               {
/* 1516:1726 */                 batchInst = new Instances(userTestStructure, 0);
/* 1517:1727 */                 String batchSizeS = ((BatchPredictor)classifier).getBatchSize();
/* 1518:1729 */                 if ((batchSizeS != null) && (batchSizeS.length() > 0))
/* 1519:     */                 {
/* 1520:     */                   try
/* 1521:     */                   {
/* 1522:1731 */                     batchSizeS = Environment.getSystemWide().substitute(batchSizeS);
/* 1523:     */                   }
/* 1524:     */                   catch (Exception ex) {}
/* 1525:     */                   try
/* 1526:     */                   {
/* 1527:1737 */                     batchSize = Integer.parseInt(batchSizeS);
/* 1528:     */                   }
/* 1529:     */                   catch (NumberFormatException ex) {}
/* 1530:     */                 }
/* 1531:     */               }
/* 1532:1743 */               testTimeStart = System.currentTimeMillis();
/* 1533:1744 */               while (source.hasMoreElements(userTestStructure))
/* 1534:     */               {
/* 1535:1745 */                 Instance instance = source.nextElement(userTestStructure);
/* 1536:1747 */                 if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 1537:     */                 {
/* 1538:1750 */                   batchInst.add(instance);
/* 1539:1751 */                   if (batchInst.numInstances() == batchSize)
/* 1540:     */                   {
/* 1541:1752 */                     Instances toPred = new Instances(batchInst);
/* 1542:1753 */                     for (int i = 0; i < toPred.numInstances(); i++) {
/* 1543:1754 */                       toPred.instance(i).setClassMissing();
/* 1544:     */                     }
/* 1545:1756 */                     double[][] predictions = ((BatchPredictor)classifier).distributionsForInstances(toPred);
/* 1546:     */                     
/* 1547:     */ 
/* 1548:1759 */                     plotInstances.process(batchInst, predictions, eval);
/* 1549:1761 */                     if (outputPredictionsText) {
/* 1550:1762 */                       for (int kk = 0; kk < batchInst.numInstances(); kk++) {
/* 1551:1763 */                         classificationOutput.printClassification(predictions[kk], batchInst.instance(kk), kk);
/* 1552:     */                       }
/* 1553:     */                     }
/* 1554:1767 */                     jj += batchInst.numInstances();
/* 1555:1768 */                     ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data. Processed " + jj + " instances...");
/* 1556:     */                     
/* 1557:1770 */                     batchInst.delete();
/* 1558:     */                   }
/* 1559:     */                 }
/* 1560:     */                 else
/* 1561:     */                 {
/* 1562:1773 */                   plotInstances.process(instance, classifier, eval);
/* 1563:1774 */                   if (outputPredictionsText) {
/* 1564:1775 */                     classificationOutput.printClassification(classifier, instance, jj);
/* 1565:     */                   }
/* 1566:1778 */                   jj++;
/* 1567:1778 */                   if (jj % 100 == 0) {
/* 1568:1779 */                     ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data. Processed " + jj + " instances...");
/* 1569:     */                   }
/* 1570:     */                 }
/* 1571:     */               }
/* 1572:1785 */               if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()) && (batchInst.numInstances() > 0))
/* 1573:     */               {
/* 1574:1791 */                 Instances toPred = new Instances(batchInst);
/* 1575:1792 */                 for (int i = 0; i < toPred.numInstances(); i++) {
/* 1576:1793 */                   toPred.instance(i).setClassMissing();
/* 1577:     */                 }
/* 1578:1796 */                 double[][] predictions = ((BatchPredictor)classifier).distributionsForInstances(toPred);
/* 1579:     */                 
/* 1580:     */ 
/* 1581:1799 */                 plotInstances.process(batchInst, predictions, eval);
/* 1582:1801 */                 if (outputPredictionsText) {
/* 1583:1802 */                   for (int kk = 0; kk < batchInst.numInstances(); kk++) {
/* 1584:1803 */                     classificationOutput.printClassification(predictions[kk], batchInst.instance(kk), kk);
/* 1585:     */                   }
/* 1586:     */                 }
/* 1587:     */               }
/* 1588:1808 */               testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 1589:1810 */               if (outputPredictionsText) {
/* 1590:1811 */                 classificationOutput.printFooter();
/* 1591:     */               }
/* 1592:1813 */               if (outputPredictionsText) {
/* 1593:1814 */                 outBuff.append("\n");
/* 1594:     */               }
/* 1595:1816 */               outBuff.append("=== Evaluation on test set ===\n");
/* 1596:1817 */               break;
/* 1597:     */             default: 
/* 1598:1820 */               throw new Exception("Test mode not implemented");
/* 1599:     */             }
/* 1600:1823 */             if (testMode != 1)
/* 1601:     */             {
/* 1602:1824 */               String mode = "";
/* 1603:1825 */               if (testMode == 2) {
/* 1604:1826 */                 mode = "training split";
/* 1605:1827 */               } else if (testMode == 3) {
/* 1606:1828 */                 mode = "training data";
/* 1607:1829 */               } else if (testMode == 4) {
/* 1608:1830 */                 mode = "supplied test set";
/* 1609:     */               }
/* 1610:1832 */               outBuff.append("\nTime taken to test model on " + mode + ": " + Utils.doubleToString(testTimeElapsed / 1000.0D, 2) + " seconds\n\n");
/* 1611:     */             }
/* 1612:1837 */             if (outputSummary) {
/* 1613:1838 */               outBuff.append(eval.toSummaryString(outputEntropy) + "\n");
/* 1614:     */             }
/* 1615:1841 */             if (inst.attribute(classIndex).isNominal())
/* 1616:     */             {
/* 1617:1843 */               if (outputPerClass) {
/* 1618:1844 */                 outBuff.append(eval.toClassDetailsString() + "\n");
/* 1619:     */               }
/* 1620:1847 */               if (outputConfusion) {
/* 1621:1848 */                 outBuff.append(eval.toMatrixString() + "\n");
/* 1622:     */               }
/* 1623:     */             }
/* 1624:1852 */             if (((fullClassifier instanceof Sourcable)) && (ClassifierPanel.this.m_OutputSourceCode.isSelected()))
/* 1625:     */             {
/* 1626:1854 */               outBuff.append("=== Source code ===\n\n");
/* 1627:1855 */               outBuff.append(Evaluation.wekaStaticWrapper((Sourcable)fullClassifier, ClassifierPanel.this.m_SourceCodeClass.getText()));
/* 1628:     */             }
/* 1629:1859 */             ClassifierPanel.this.m_History.updateResult(name);
/* 1630:1860 */             ClassifierPanel.this.m_Log.logMessage("Finished " + cname);
/* 1631:1861 */             ClassifierPanel.this.m_Log.statusMessage("OK");
/* 1632:     */           }
/* 1633:     */           catch (Exception ex)
/* 1634:     */           {
/* 1635:     */             ArrayList<Object> vv;
/* 1636:     */             Instances trainHeader;
/* 1637:     */             Settings settings;
/* 1638:     */             ArrayList<Object> vv;
/* 1639:     */             Instances trainHeader;
/* 1640:1863 */             ex.printStackTrace();
/* 1641:1864 */             ClassifierPanel.this.m_Log.logMessage(ex.getMessage());
/* 1642:1865 */             JOptionPane.showMessageDialog(ClassifierPanel.this, "Problem evaluating classifier:\n" + ex.getMessage(), "Evaluate classifier", 0);
/* 1643:     */             
/* 1644:     */ 
/* 1645:1868 */             ClassifierPanel.this.m_Log.statusMessage("Problem evaluating classifier");
/* 1646:     */           }
/* 1647:     */           finally
/* 1648:     */           {
/* 1649:     */             try
/* 1650:     */             {
/* 1651:     */               ArrayList<Object> vv;
/* 1652:     */               Instances trainHeader;
/* 1653:     */               Settings settings;
/* 1654:     */               ArrayList<Object> vv;
/* 1655:     */               Instances trainHeader;
/* 1656:1871 */               if ((!saveVis) && (outputModel))
/* 1657:     */               {
/* 1658:1872 */                 ArrayList<Object> vv = new ArrayList();
/* 1659:1873 */                 vv.add(fullClassifier);
/* 1660:1874 */                 Instances trainHeader = new Instances(ClassifierPanel.this.m_Instances, 0);
/* 1661:1875 */                 trainHeader.setClassIndex(classIndex);
/* 1662:1876 */                 vv.add(trainHeader);
/* 1663:1877 */                 if (grph != null) {
/* 1664:1878 */                   vv.add(grph);
/* 1665:     */                 }
/* 1666:1880 */                 ClassifierPanel.this.m_History.addObject(name, vv);
/* 1667:     */               }
/* 1668:1881 */               else if ((saveVis) && (plotInstances != null) && (plotInstances.canPlot(false)))
/* 1669:     */               {
/* 1670:1883 */                 ClassifierPanel.this.m_CurrentVis = new VisualizePanel();
/* 1671:1884 */                 if (ClassifierPanel.this.getMainApplication() != null)
/* 1672:     */                 {
/* 1673:1885 */                   Settings settings = ClassifierPanel.this.getMainApplication().getApplicationSettings();
/* 1674:1886 */                   ClassifierPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/* 1675:     */                 }
/* 1676:1889 */                 ClassifierPanel.this.m_CurrentVis.setName(name + " (" + inst.relationName() + ")");
/* 1677:1890 */                 ClassifierPanel.this.m_CurrentVis.setLog(ClassifierPanel.this.m_Log);
/* 1678:1891 */                 ClassifierPanel.this.m_CurrentVis.addPlot(plotInstances.getPlotData(cname));
/* 1679:     */                 
/* 1680:1893 */                 ClassifierPanel.this.m_CurrentVis.setColourIndex(plotInstances.getPlotInstances().classIndex());
/* 1681:     */                 
/* 1682:1895 */                 plotInstances.cleanUp();
/* 1683:     */                 
/* 1684:1897 */                 ArrayList<Object> vv = new ArrayList();
/* 1685:1898 */                 if (outputModel)
/* 1686:     */                 {
/* 1687:1899 */                   vv.add(fullClassifier);
/* 1688:1900 */                   Instances trainHeader = new Instances(ClassifierPanel.this.m_Instances, 0);
/* 1689:1901 */                   trainHeader.setClassIndex(classIndex);
/* 1690:1902 */                   vv.add(trainHeader);
/* 1691:1903 */                   if (grph != null) {
/* 1692:1904 */                     vv.add(grph);
/* 1693:     */                   }
/* 1694:     */                 }
/* 1695:1907 */                 vv.add(ClassifierPanel.this.m_CurrentVis);
/* 1696:1909 */                 if ((eval != null) && (eval.predictions() != null))
/* 1697:     */                 {
/* 1698:1910 */                   vv.add(eval.predictions());
/* 1699:1911 */                   vv.add(inst.classAttribute());
/* 1700:     */                 }
/* 1701:1913 */                 ClassifierPanel.this.m_History.addObject(name, vv);
/* 1702:     */               }
/* 1703:     */             }
/* 1704:     */             catch (Exception ex)
/* 1705:     */             {
/* 1706:1916 */               ex.printStackTrace();
/* 1707:     */             }
/* 1708:1919 */             if (isInterrupted())
/* 1709:     */             {
/* 1710:1920 */               ClassifierPanel.this.m_Log.logMessage("Interrupted " + cname);
/* 1711:1921 */               ClassifierPanel.this.m_Log.statusMessage("Interrupted");
/* 1712:     */             }
/* 1713:1924 */             synchronized (this)
/* 1714:     */             {
/* 1715:1925 */               ClassifierPanel.this.m_StartBut.setEnabled(true);
/* 1716:1926 */               ClassifierPanel.this.m_StopBut.setEnabled(false);
/* 1717:1927 */               ClassifierPanel.this.m_RunThread = null;
/* 1718:     */             }
/* 1719:1929 */             if ((ClassifierPanel.this.m_Log instanceof TaskLogger)) {
/* 1720:1930 */               ((TaskLogger)ClassifierPanel.this.m_Log).taskFinished();
/* 1721:     */             }
/* 1722:     */           }
/* 1723:     */         }
/* 1724:1934 */       };
/* 1725:1935 */       this.m_RunThread.setPriority(1);
/* 1726:1936 */       this.m_RunThread.start();
/* 1727:     */     }
/* 1728:     */   }
/* 1729:     */   
/* 1730:     */   protected void visualize(String name, int x, int y)
/* 1731:     */   {
/* 1732:1950 */     final String selectedName = name;
/* 1733:1951 */     JPopupMenu resultListMenu = new JPopupMenu();
/* 1734:     */     
/* 1735:1953 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/* 1736:1954 */     if (selectedName != null) {
/* 1737:1955 */       visMainBuffer.addActionListener(new ActionListener()
/* 1738:     */       {
/* 1739:     */         public void actionPerformed(ActionEvent e)
/* 1740:     */         {
/* 1741:1958 */           ClassifierPanel.this.m_History.setSingle(selectedName);
/* 1742:     */         }
/* 1743:     */       });
/* 1744:     */     } else {
/* 1745:1962 */       visMainBuffer.setEnabled(false);
/* 1746:     */     }
/* 1747:1964 */     resultListMenu.add(visMainBuffer);
/* 1748:     */     
/* 1749:1966 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/* 1750:1967 */     if (selectedName != null) {
/* 1751:1968 */       visSepBuffer.addActionListener(new ActionListener()
/* 1752:     */       {
/* 1753:     */         public void actionPerformed(ActionEvent e)
/* 1754:     */         {
/* 1755:1971 */           ClassifierPanel.this.m_History.openFrame(selectedName);
/* 1756:     */         }
/* 1757:     */       });
/* 1758:     */     } else {
/* 1759:1975 */       visSepBuffer.setEnabled(false);
/* 1760:     */     }
/* 1761:1977 */     resultListMenu.add(visSepBuffer);
/* 1762:     */     
/* 1763:1979 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/* 1764:1980 */     if (selectedName != null) {
/* 1765:1981 */       saveOutput.addActionListener(new ActionListener()
/* 1766:     */       {
/* 1767:     */         public void actionPerformed(ActionEvent e)
/* 1768:     */         {
/* 1769:1984 */           ClassifierPanel.this.saveBuffer(selectedName);
/* 1770:     */         }
/* 1771:     */       });
/* 1772:     */     } else {
/* 1773:1988 */       saveOutput.setEnabled(false);
/* 1774:     */     }
/* 1775:1990 */     resultListMenu.add(saveOutput);
/* 1776:     */     
/* 1777:1992 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/* 1778:1993 */     if (selectedName != null) {
/* 1779:1994 */       deleteOutput.addActionListener(new ActionListener()
/* 1780:     */       {
/* 1781:     */         public void actionPerformed(ActionEvent e)
/* 1782:     */         {
/* 1783:1997 */           ClassifierPanel.this.m_History.removeResult(selectedName);
/* 1784:     */         }
/* 1785:     */       });
/* 1786:     */     } else {
/* 1787:2001 */       deleteOutput.setEnabled(false);
/* 1788:     */     }
/* 1789:2003 */     resultListMenu.add(deleteOutput);
/* 1790:     */     
/* 1791:2005 */     resultListMenu.addSeparator();
/* 1792:     */     
/* 1793:2007 */     JMenuItem loadModel = new JMenuItem("Load model");
/* 1794:2008 */     loadModel.addActionListener(new ActionListener()
/* 1795:     */     {
/* 1796:     */       public void actionPerformed(ActionEvent e)
/* 1797:     */       {
/* 1798:2011 */         ClassifierPanel.this.loadClassifier();
/* 1799:     */       }
/* 1800:2013 */     });
/* 1801:2014 */     resultListMenu.add(loadModel);
/* 1802:     */     
/* 1803:2016 */     ArrayList<Object> o = null;
/* 1804:2017 */     if (selectedName != null) {
/* 1805:2018 */       o = (ArrayList)this.m_History.getNamedObject(selectedName);
/* 1806:     */     }
/* 1807:2021 */     VisualizePanel temp_vp = null;
/* 1808:2022 */     String temp_grph = null;
/* 1809:2023 */     ArrayList<Prediction> temp_preds = null;
/* 1810:2024 */     Attribute temp_classAtt = null;
/* 1811:2025 */     Classifier temp_classifier = null;
/* 1812:2026 */     Instances temp_trainHeader = null;
/* 1813:2028 */     if (o != null) {
/* 1814:2029 */       for (int i = 0; i < o.size(); i++)
/* 1815:     */       {
/* 1816:2030 */         Object temp = o.get(i);
/* 1817:2031 */         if ((temp instanceof Classifier)) {
/* 1818:2032 */           temp_classifier = (Classifier)temp;
/* 1819:2033 */         } else if ((temp instanceof Instances)) {
/* 1820:2034 */           temp_trainHeader = (Instances)temp;
/* 1821:2035 */         } else if ((temp instanceof VisualizePanel)) {
/* 1822:2036 */           temp_vp = (VisualizePanel)temp;
/* 1823:2037 */         } else if ((temp instanceof String)) {
/* 1824:2038 */           temp_grph = (String)temp;
/* 1825:2039 */         } else if ((temp instanceof ArrayList)) {
/* 1826:2040 */           temp_preds = (ArrayList)temp;
/* 1827:2041 */         } else if ((temp instanceof Attribute)) {
/* 1828:2042 */           temp_classAtt = (Attribute)temp;
/* 1829:     */         }
/* 1830:     */       }
/* 1831:     */     }
/* 1832:2047 */     final VisualizePanel vp = temp_vp;
/* 1833:2048 */     final String grph = temp_grph;
/* 1834:2049 */     final ArrayList<Prediction> preds = temp_preds;
/* 1835:2050 */     final Attribute classAtt = temp_classAtt;
/* 1836:2051 */     final Classifier classifier = temp_classifier;
/* 1837:2052 */     final Instances trainHeader = temp_trainHeader;
/* 1838:     */     
/* 1839:2054 */     JMenuItem saveModel = new JMenuItem("Save model");
/* 1840:2055 */     if (classifier != null) {
/* 1841:2056 */       saveModel.addActionListener(new ActionListener()
/* 1842:     */       {
/* 1843:     */         public void actionPerformed(ActionEvent e)
/* 1844:     */         {
/* 1845:2059 */           ClassifierPanel.this.saveClassifier(selectedName, classifier, trainHeader);
/* 1846:     */         }
/* 1847:     */       });
/* 1848:     */     } else {
/* 1849:2063 */       saveModel.setEnabled(false);
/* 1850:     */     }
/* 1851:2065 */     resultListMenu.add(saveModel);
/* 1852:     */     
/* 1853:2067 */     JMenuItem reEvaluate = new JMenuItem("Re-evaluate model on current test set");
/* 1854:2069 */     if ((classifier != null) && (this.m_TestLoader != null)) {
/* 1855:2070 */       reEvaluate.addActionListener(new ActionListener()
/* 1856:     */       {
/* 1857:     */         public void actionPerformed(ActionEvent e)
/* 1858:     */         {
/* 1859:2073 */           ClassifierPanel.this.reevaluateModel(selectedName, classifier, trainHeader);
/* 1860:     */         }
/* 1861:     */       });
/* 1862:     */     } else {
/* 1863:2077 */       reEvaluate.setEnabled(false);
/* 1864:     */     }
/* 1865:2079 */     resultListMenu.add(reEvaluate);
/* 1866:     */     
/* 1867:2081 */     JMenuItem reApplyConfig = new JMenuItem("Re-apply this model's configuration");
/* 1868:2083 */     if (classifier != null) {
/* 1869:2084 */       reApplyConfig.addActionListener(new ActionListener()
/* 1870:     */       {
/* 1871:     */         public void actionPerformed(ActionEvent e)
/* 1872:     */         {
/* 1873:2087 */           ClassifierPanel.this.m_ClassifierEditor.setValue(classifier);
/* 1874:     */         }
/* 1875:     */       });
/* 1876:     */     } else {
/* 1877:2091 */       reApplyConfig.setEnabled(false);
/* 1878:     */     }
/* 1879:2093 */     resultListMenu.add(reApplyConfig);
/* 1880:     */     
/* 1881:2095 */     resultListMenu.addSeparator();
/* 1882:     */     
/* 1883:2097 */     JMenuItem visErrors = new JMenuItem("Visualize classifier errors");
/* 1884:2098 */     if (vp != null)
/* 1885:     */     {
/* 1886:2099 */       if ((vp.getXIndex() == 0) && (vp.getYIndex() == 1)) {
/* 1887:     */         try
/* 1888:     */         {
/* 1889:2101 */           vp.setXIndex(vp.getInstances().classIndex());
/* 1890:2102 */           vp.setYIndex(vp.getInstances().classIndex() - 1);
/* 1891:     */         }
/* 1892:     */         catch (Exception e) {}
/* 1893:     */       }
/* 1894:2107 */       visErrors.addActionListener(new ActionListener()
/* 1895:     */       {
/* 1896:     */         public void actionPerformed(ActionEvent e)
/* 1897:     */         {
/* 1898:2110 */           ClassifierPanel.this.visualizeClassifierErrors(vp);
/* 1899:     */         }
/* 1900:     */       });
/* 1901:     */     }
/* 1902:     */     else
/* 1903:     */     {
/* 1904:2114 */       visErrors.setEnabled(false);
/* 1905:     */     }
/* 1906:2116 */     resultListMenu.add(visErrors);
/* 1907:     */     
/* 1908:2118 */     JMenuItem visGrph = new JMenuItem("Visualize tree");
/* 1909:2119 */     if (grph != null)
/* 1910:     */     {
/* 1911:2120 */       if (((Drawable)temp_classifier).graphType() == 1)
/* 1912:     */       {
/* 1913:2121 */         visGrph.addActionListener(new ActionListener()
/* 1914:     */         {
/* 1915:     */           public void actionPerformed(ActionEvent e)
/* 1916:     */           {
/* 1917:     */             String title;
/* 1918:     */             String title;
/* 1919:2125 */             if (vp != null) {
/* 1920:2126 */               title = vp.getName();
/* 1921:     */             } else {
/* 1922:2128 */               title = selectedName;
/* 1923:     */             }
/* 1924:2130 */             ClassifierPanel.this.visualizeTree(grph, title);
/* 1925:     */           }
/* 1926:     */         });
/* 1927:     */       }
/* 1928:2133 */       else if (((Drawable)temp_classifier).graphType() == 2)
/* 1929:     */       {
/* 1930:2134 */         visGrph.setText("Visualize graph");
/* 1931:2135 */         visGrph.addActionListener(new ActionListener()
/* 1932:     */         {
/* 1933:     */           public void actionPerformed(ActionEvent e)
/* 1934:     */           {
/* 1935:2138 */             Thread th = new Thread()
/* 1936:     */             {
/* 1937:     */               public void run()
/* 1938:     */               {
/* 1939:2141 */                 ClassifierPanel.this.visualizeBayesNet(ClassifierPanel.29.this.val$grph, ClassifierPanel.29.this.val$selectedName);
/* 1940:     */               }
/* 1941:2143 */             };
/* 1942:2144 */             th.start();
/* 1943:     */           }
/* 1944:     */         });
/* 1945:     */       }
/* 1946:     */       else
/* 1947:     */       {
/* 1948:2148 */         visGrph.setEnabled(false);
/* 1949:     */       }
/* 1950:     */     }
/* 1951:     */     else {
/* 1952:2151 */       visGrph.setEnabled(false);
/* 1953:     */     }
/* 1954:2153 */     resultListMenu.add(visGrph);
/* 1955:     */     
/* 1956:2155 */     JMenuItem visMargin = new JMenuItem("Visualize margin curve");
/* 1957:2156 */     if ((preds != null) && (classAtt != null) && (classAtt.isNominal())) {
/* 1958:2157 */       visMargin.addActionListener(new ActionListener()
/* 1959:     */       {
/* 1960:     */         public void actionPerformed(ActionEvent e)
/* 1961:     */         {
/* 1962:     */           try
/* 1963:     */           {
/* 1964:2161 */             MarginCurve tc = new MarginCurve();
/* 1965:2162 */             Instances result = tc.getCurve(preds);
/* 1966:2163 */             VisualizePanel vmc = new VisualizePanel();
/* 1967:2164 */             if (ClassifierPanel.this.getMainApplication() != null)
/* 1968:     */             {
/* 1969:2165 */               Settings settings = ClassifierPanel.this.getMainApplication().getApplicationSettings();
/* 1970:2166 */               ClassifierPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/* 1971:     */             }
/* 1972:2169 */             vmc.setName(result.relationName());
/* 1973:2170 */             vmc.setLog(ClassifierPanel.this.m_Log);
/* 1974:2171 */             PlotData2D tempd = new PlotData2D(result);
/* 1975:2172 */             tempd.setPlotName(result.relationName());
/* 1976:2173 */             tempd.addInstanceNumberAttribute();
/* 1977:2174 */             vmc.addPlot(tempd);
/* 1978:2175 */             ClassifierPanel.this.visualizeClassifierErrors(vmc);
/* 1979:     */           }
/* 1980:     */           catch (Exception ex)
/* 1981:     */           {
/* 1982:2177 */             ex.printStackTrace();
/* 1983:     */           }
/* 1984:     */         }
/* 1985:     */       });
/* 1986:     */     } else {
/* 1987:2182 */       visMargin.setEnabled(false);
/* 1988:     */     }
/* 1989:2184 */     resultListMenu.add(visMargin);
/* 1990:     */     
/* 1991:2186 */     JMenu visThreshold = new JMenu("Visualize threshold curve");
/* 1992:2187 */     if ((preds != null) && (classAtt != null) && (classAtt.isNominal())) {
/* 1993:2188 */       for (int i = 0; i < classAtt.numValues(); i++)
/* 1994:     */       {
/* 1995:2189 */         JMenuItem clv = new JMenuItem(classAtt.value(i));
/* 1996:2190 */         final int classValue = i;
/* 1997:2191 */         clv.addActionListener(new ActionListener()
/* 1998:     */         {
/* 1999:     */           public void actionPerformed(ActionEvent e)
/* 2000:     */           {
/* 2001:     */             try
/* 2002:     */             {
/* 2003:2195 */               ThresholdCurve tc = new ThresholdCurve();
/* 2004:2196 */               Instances result = tc.getCurve(preds, classValue);
/* 2005:     */               
/* 2006:2198 */               ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
/* 2007:2199 */               vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
/* 2008:     */               
/* 2009:     */ 
/* 2010:2202 */               vmc.setLog(ClassifierPanel.this.m_Log);
/* 2011:2203 */               vmc.setName(result.relationName() + ". (Class value " + classAtt.value(classValue) + ")");
/* 2012:     */               
/* 2013:2205 */               PlotData2D tempd = new PlotData2D(result);
/* 2014:2206 */               tempd.setPlotName(result.relationName());
/* 2015:2207 */               tempd.addInstanceNumberAttribute();
/* 2016:     */               
/* 2017:2209 */               boolean[] cp = new boolean[result.numInstances()];
/* 2018:2210 */               for (int n = 1; n < cp.length; n++) {
/* 2019:2211 */                 cp[n] = true;
/* 2020:     */               }
/* 2021:2213 */               tempd.setConnectPoints(cp);
/* 2022:     */               
/* 2023:2215 */               vmc.addPlot(tempd);
/* 2024:2216 */               ClassifierPanel.this.visualizeClassifierErrors(vmc);
/* 2025:     */             }
/* 2026:     */             catch (Exception ex)
/* 2027:     */             {
/* 2028:2218 */               ex.printStackTrace();
/* 2029:     */             }
/* 2030:     */           }
/* 2031:2221 */         });
/* 2032:2222 */         visThreshold.add(clv);
/* 2033:     */       }
/* 2034:     */     } else {
/* 2035:2225 */       visThreshold.setEnabled(false);
/* 2036:     */     }
/* 2037:2227 */     resultListMenu.add(visThreshold);
/* 2038:     */     
/* 2039:2229 */     JMenu visCostBenefit = new JMenu("Cost/Benefit analysis");
/* 2040:2230 */     if ((preds != null) && (classAtt != null) && (classAtt.isNominal())) {
/* 2041:2231 */       for (int i = 0; i < classAtt.numValues(); i++)
/* 2042:     */       {
/* 2043:2232 */         JMenuItem clv = new JMenuItem(classAtt.value(i));
/* 2044:2233 */         final int classValue = i;
/* 2045:2234 */         clv.addActionListener(new ActionListener()
/* 2046:     */         {
/* 2047:     */           public void actionPerformed(ActionEvent e)
/* 2048:     */           {
/* 2049:     */             try
/* 2050:     */             {
/* 2051:2238 */               ThresholdCurve tc = new ThresholdCurve();
/* 2052:2239 */               Instances result = tc.getCurve(preds, classValue);
/* 2053:     */               
/* 2054:     */ 
/* 2055:     */ 
/* 2056:2243 */               Attribute classAttToUse = classAtt;
/* 2057:2244 */               if (classValue != 0)
/* 2058:     */               {
/* 2059:2245 */                 ArrayList<String> newNames = new ArrayList();
/* 2060:2246 */                 newNames.add(classAtt.value(classValue));
/* 2061:2247 */                 for (int k = 0; k < classAtt.numValues(); k++) {
/* 2062:2248 */                   if (k != classValue) {
/* 2063:2249 */                     newNames.add(classAtt.value(k));
/* 2064:     */                   }
/* 2065:     */                 }
/* 2066:2252 */                 classAttToUse = new Attribute(classAtt.name(), newNames);
/* 2067:     */               }
/* 2068:2255 */               CostBenefitAnalysis cbAnalysis = new CostBenefitAnalysis();
/* 2069:     */               
/* 2070:2257 */               PlotData2D tempd = new PlotData2D(result);
/* 2071:2258 */               tempd.setPlotName(result.relationName());
/* 2072:2259 */               tempd.m_alwaysDisplayPointsOfThisSize = 10;
/* 2073:     */               
/* 2074:2261 */               boolean[] cp = new boolean[result.numInstances()];
/* 2075:2262 */               for (int n = 1; n < cp.length; n++) {
/* 2076:2263 */                 cp[n] = true;
/* 2077:     */               }
/* 2078:2265 */               tempd.setConnectPoints(cp);
/* 2079:     */               
/* 2080:2267 */               String windowTitle = "";
/* 2081:2268 */               if (classifier != null)
/* 2082:     */               {
/* 2083:2269 */                 String cname = classifier.getClass().getName();
/* 2084:2270 */                 if (cname.startsWith("weka.classifiers.")) {
/* 2085:2271 */                   windowTitle = "" + cname.substring("weka.classifiers.".length()) + " ";
/* 2086:     */                 }
/* 2087:     */               }
/* 2088:2275 */               windowTitle = windowTitle + " (class = " + classAttToUse.value(0) + ")";
/* 2089:     */               
/* 2090:     */ 
/* 2091:2278 */               cbAnalysis.setCurveData(tempd, classAttToUse);
/* 2092:2279 */               ClassifierPanel.this.visualizeCostBenefitAnalysis(cbAnalysis, windowTitle);
/* 2093:     */             }
/* 2094:     */             catch (Exception ex)
/* 2095:     */             {
/* 2096:2281 */               ex.printStackTrace();
/* 2097:     */             }
/* 2098:     */           }
/* 2099:2284 */         });
/* 2100:2285 */         visCostBenefit.add(clv);
/* 2101:     */       }
/* 2102:     */     } else {
/* 2103:2288 */       visCostBenefit.setEnabled(false);
/* 2104:     */     }
/* 2105:2290 */     resultListMenu.add(visCostBenefit);
/* 2106:     */     
/* 2107:2292 */     JMenu visCost = new JMenu("Visualize cost curve");
/* 2108:2293 */     if ((preds != null) && (classAtt != null) && (classAtt.isNominal())) {
/* 2109:2294 */       for (int i = 0; i < classAtt.numValues(); i++)
/* 2110:     */       {
/* 2111:2295 */         JMenuItem clv = new JMenuItem(classAtt.value(i));
/* 2112:2296 */         final int classValue = i;
/* 2113:2297 */         clv.addActionListener(new ActionListener()
/* 2114:     */         {
/* 2115:     */           public void actionPerformed(ActionEvent e)
/* 2116:     */           {
/* 2117:     */             try
/* 2118:     */             {
/* 2119:2301 */               CostCurve cc = new CostCurve();
/* 2120:2302 */               Instances result = cc.getCurve(preds, classValue);
/* 2121:2303 */               VisualizePanel vmc = new VisualizePanel();
/* 2122:2304 */               if (ClassifierPanel.this.getMainApplication() != null)
/* 2123:     */               {
/* 2124:2305 */                 Settings settings = ClassifierPanel.this.getMainApplication().getApplicationSettings();
/* 2125:2306 */                 ClassifierPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/* 2126:     */               }
/* 2127:2309 */               vmc.setLog(ClassifierPanel.this.m_Log);
/* 2128:2310 */               vmc.setName(result.relationName() + ". (Class value " + classAtt.value(classValue) + ")");
/* 2129:     */               
/* 2130:2312 */               PlotData2D tempd = new PlotData2D(result);
/* 2131:2313 */               tempd.m_displayAllPoints = true;
/* 2132:2314 */               tempd.setPlotName(result.relationName());
/* 2133:2315 */               boolean[] connectPoints = new boolean[result.numInstances()];
/* 2134:2316 */               for (int jj = 1; jj < connectPoints.length; jj += 2) {
/* 2135:2317 */                 connectPoints[jj] = true;
/* 2136:     */               }
/* 2137:2319 */               tempd.setConnectPoints(connectPoints);
/* 2138:     */               
/* 2139:2321 */               vmc.addPlot(tempd);
/* 2140:2322 */               ClassifierPanel.this.visualizeClassifierErrors(vmc);
/* 2141:     */             }
/* 2142:     */             catch (Exception ex)
/* 2143:     */             {
/* 2144:2324 */               ex.printStackTrace();
/* 2145:     */             }
/* 2146:     */           }
/* 2147:2327 */         });
/* 2148:2328 */         visCost.add(clv);
/* 2149:     */       }
/* 2150:     */     } else {
/* 2151:2331 */       visCost.setEnabled(false);
/* 2152:     */     }
/* 2153:2333 */     resultListMenu.add(visCost);
/* 2154:     */     
/* 2155:     */ 
/* 2156:2336 */     JMenu visPlugins = new JMenu("Plugins");
/* 2157:2337 */     boolean availablePlugins = false;
/* 2158:     */     
/* 2159:     */ 
/* 2160:2340 */     Vector<String> pluginsVector = GenericObjectEditor.getClassnames(VisualizePlugin.class.getName());
/* 2161:2342 */     for (int i = 0; i < pluginsVector.size(); i++)
/* 2162:     */     {
/* 2163:2343 */       String className = (String)pluginsVector.elementAt(i);
/* 2164:     */       try
/* 2165:     */       {
/* 2166:2345 */         VisualizePlugin plugin = (VisualizePlugin)Class.forName(className).newInstance();
/* 2167:2347 */         if (plugin != null)
/* 2168:     */         {
/* 2169:2350 */           availablePlugins = true;
/* 2170:2351 */           JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem(preds, classAtt);
/* 2171:2352 */           new Version();
/* 2172:2353 */           if (pluginMenuItem != null) {
/* 2173:2361 */             visPlugins.add(pluginMenuItem);
/* 2174:     */           }
/* 2175:     */         }
/* 2176:     */       }
/* 2177:     */       catch (Exception e) {}
/* 2178:     */     }
/* 2179:2369 */     pluginsVector = GenericObjectEditor.getClassnames(ErrorVisualizePlugin.class.getName());
/* 2180:2371 */     for (int i = 0; i < pluginsVector.size(); i++)
/* 2181:     */     {
/* 2182:2372 */       String className = (String)pluginsVector.elementAt(i);
/* 2183:     */       try
/* 2184:     */       {
/* 2185:2374 */         ErrorVisualizePlugin plugin = (ErrorVisualizePlugin)Class.forName(className).newInstance();
/* 2186:2376 */         if (plugin != null)
/* 2187:     */         {
/* 2188:2379 */           availablePlugins = true;
/* 2189:2380 */           JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem(vp.getInstances());
/* 2190:     */           
/* 2191:2382 */           new Version();
/* 2192:2383 */           if (pluginMenuItem != null) {
/* 2193:2391 */             visPlugins.add(pluginMenuItem);
/* 2194:     */           }
/* 2195:     */         }
/* 2196:     */       }
/* 2197:     */       catch (Exception e) {}
/* 2198:     */     }
/* 2199:2399 */     if (grph != null) {
/* 2200:2401 */       if (((Drawable)temp_classifier).graphType() == 1)
/* 2201:     */       {
/* 2202:2402 */         pluginsVector = GenericObjectEditor.getClassnames(TreeVisualizePlugin.class.getName());
/* 2203:2405 */         for (int i = 0; i < pluginsVector.size(); i++)
/* 2204:     */         {
/* 2205:2406 */           String className = (String)pluginsVector.elementAt(i);
/* 2206:     */           try
/* 2207:     */           {
/* 2208:2408 */             TreeVisualizePlugin plugin = (TreeVisualizePlugin)Class.forName(className).newInstance();
/* 2209:2410 */             if (plugin != null)
/* 2210:     */             {
/* 2211:2413 */               availablePlugins = true;
/* 2212:2414 */               JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem(grph, selectedName);
/* 2213:     */               
/* 2214:2416 */               new Version();
/* 2215:2417 */               if (pluginMenuItem != null) {
/* 2216:2426 */                 visPlugins.add(pluginMenuItem);
/* 2217:     */               }
/* 2218:     */             }
/* 2219:     */           }
/* 2220:     */           catch (Exception e) {}
/* 2221:     */         }
/* 2222:     */       }
/* 2223:     */       else
/* 2224:     */       {
/* 2225:2435 */         pluginsVector = GenericObjectEditor.getClassnames(GraphVisualizePlugin.class.getName());
/* 2226:2438 */         for (int i = 0; i < pluginsVector.size(); i++)
/* 2227:     */         {
/* 2228:2439 */           String className = (String)pluginsVector.elementAt(i);
/* 2229:     */           try
/* 2230:     */           {
/* 2231:2441 */             GraphVisualizePlugin plugin = (GraphVisualizePlugin)Class.forName(className).newInstance();
/* 2232:2443 */             if (plugin != null)
/* 2233:     */             {
/* 2234:2446 */               availablePlugins = true;
/* 2235:2447 */               JMenuItem pluginMenuItem = plugin.getVisualizeMenuItem(grph, selectedName);
/* 2236:     */               
/* 2237:2449 */               new Version();
/* 2238:2450 */               if (pluginMenuItem != null) {
/* 2239:2459 */                 visPlugins.add(pluginMenuItem);
/* 2240:     */               }
/* 2241:     */             }
/* 2242:     */           }
/* 2243:     */           catch (Exception e) {}
/* 2244:     */         }
/* 2245:     */       }
/* 2246:     */     }
/* 2247:2468 */     if (availablePlugins) {
/* 2248:2469 */       resultListMenu.add(visPlugins);
/* 2249:     */     }
/* 2250:2472 */     resultListMenu.show(this.m_History.getList(), x, y);
/* 2251:     */   }
/* 2252:     */   
/* 2253:     */   protected void visualizeTree(String dottyString, String treeName)
/* 2254:     */   {
/* 2255:2483 */     final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: " + treeName);
/* 2256:     */     
/* 2257:2485 */     jf.setSize(500, 400);
/* 2258:2486 */     jf.getContentPane().setLayout(new BorderLayout());
/* 2259:2487 */     TreeVisualizer tv = new TreeVisualizer(null, dottyString, new PlaceNode2());
/* 2260:2488 */     jf.getContentPane().add(tv, "Center");
/* 2261:2489 */     jf.addWindowListener(new WindowAdapter()
/* 2262:     */     {
/* 2263:     */       public void windowClosing(WindowEvent e)
/* 2264:     */       {
/* 2265:2492 */         jf.dispose();
/* 2266:     */       }
/* 2267:2495 */     });
/* 2268:2496 */     jf.setVisible(true);
/* 2269:2497 */     tv.fitToScreen();
/* 2270:     */   }
/* 2271:     */   
/* 2272:     */   protected void visualizeBayesNet(String XMLBIF, String graphName)
/* 2273:     */   {
/* 2274:2508 */     final JFrame jf = new JFrame("Weka Classifier Graph Visualizer: " + graphName);
/* 2275:     */     
/* 2276:2510 */     jf.setSize(500, 400);
/* 2277:2511 */     jf.getContentPane().setLayout(new BorderLayout());
/* 2278:2512 */     GraphVisualizer gv = new GraphVisualizer();
/* 2279:     */     try
/* 2280:     */     {
/* 2281:2514 */       gv.readBIF(XMLBIF);
/* 2282:     */     }
/* 2283:     */     catch (BIFFormatException be)
/* 2284:     */     {
/* 2285:2516 */       System.err.println("unable to visualize BayesNet");
/* 2286:2517 */       be.printStackTrace();
/* 2287:     */     }
/* 2288:2519 */     gv.layoutGraph();
/* 2289:     */     
/* 2290:2521 */     jf.getContentPane().add(gv, "Center");
/* 2291:2522 */     jf.addWindowListener(new WindowAdapter()
/* 2292:     */     {
/* 2293:     */       public void windowClosing(WindowEvent e)
/* 2294:     */       {
/* 2295:2525 */         jf.dispose();
/* 2296:     */       }
/* 2297:2528 */     });
/* 2298:2529 */     jf.setVisible(true);
/* 2299:     */   }
/* 2300:     */   
/* 2301:     */   protected void visualizeCostBenefitAnalysis(CostBenefitAnalysis cb, String classifierAndRelationName)
/* 2302:     */   {
/* 2303:2539 */     if (cb != null)
/* 2304:     */     {
/* 2305:2540 */       String windowTitle = "Weka Classifier: Cost/Benefit Analysis ";
/* 2306:2541 */       if (classifierAndRelationName != null) {
/* 2307:2542 */         windowTitle = windowTitle + "- " + classifierAndRelationName;
/* 2308:     */       }
/* 2309:2544 */       final JFrame jf = new JFrame(windowTitle);
/* 2310:2545 */       jf.setSize(1000, 600);
/* 2311:2546 */       jf.getContentPane().setLayout(new BorderLayout());
/* 2312:     */       
/* 2313:2548 */       jf.getContentPane().add(cb, "Center");
/* 2314:2549 */       jf.addWindowListener(new WindowAdapter()
/* 2315:     */       {
/* 2316:     */         public void windowClosing(WindowEvent e)
/* 2317:     */         {
/* 2318:2552 */           jf.dispose();
/* 2319:     */         }
/* 2320:2555 */       });
/* 2321:2556 */       jf.setVisible(true);
/* 2322:     */     }
/* 2323:     */   }
/* 2324:     */   
/* 2325:     */   protected void visualizeClassifierErrors(VisualizePanel sp)
/* 2326:     */   {
/* 2327:2568 */     if (sp != null)
/* 2328:     */     {
/* 2329:2569 */       String plotName = sp.getName();
/* 2330:2570 */       final JFrame jf = new JFrame("Weka Classifier Visualize: " + plotName);
/* 2331:     */       
/* 2332:2572 */       jf.setSize(600, 400);
/* 2333:2573 */       jf.getContentPane().setLayout(new BorderLayout());
/* 2334:     */       
/* 2335:2575 */       jf.getContentPane().add(sp, "Center");
/* 2336:2576 */       jf.addWindowListener(new WindowAdapter()
/* 2337:     */       {
/* 2338:     */         public void windowClosing(WindowEvent e)
/* 2339:     */         {
/* 2340:2579 */           jf.dispose();
/* 2341:     */         }
/* 2342:2582 */       });
/* 2343:2583 */       jf.setVisible(true);
/* 2344:     */     }
/* 2345:     */   }
/* 2346:     */   
/* 2347:     */   protected void saveBuffer(String name)
/* 2348:     */   {
/* 2349:2593 */     StringBuffer sb = this.m_History.getNamedBuffer(name);
/* 2350:2594 */     if ((sb != null) && 
/* 2351:2595 */       (this.m_SaveOut.save(sb))) {
/* 2352:2596 */       this.m_Log.logMessage("Save successful.");
/* 2353:     */     }
/* 2354:     */   }
/* 2355:     */   
/* 2356:     */   protected void stopClassifier()
/* 2357:     */   {
/* 2358:2607 */     if (this.m_RunThread != null)
/* 2359:     */     {
/* 2360:2608 */       this.m_RunThread.interrupt();
/* 2361:     */       
/* 2362:     */ 
/* 2363:2611 */       this.m_RunThread.stop();
/* 2364:     */     }
/* 2365:     */   }
/* 2366:     */   
/* 2367:     */   protected void saveClassifier(String name, Classifier classifier, Instances trainHeader)
/* 2368:     */   {
/* 2369:2625 */     File sFile = null;
/* 2370:2626 */     boolean saveOK = true;
/* 2371:     */     
/* 2372:2628 */     this.m_FileChooser.removeChoosableFileFilter(this.m_PMMLModelFilter);
/* 2373:2629 */     this.m_FileChooser.setFileFilter(this.m_ModelFilter);
/* 2374:2630 */     int returnVal = this.m_FileChooser.showSaveDialog(this);
/* 2375:2631 */     if (returnVal == 0)
/* 2376:     */     {
/* 2377:2632 */       sFile = this.m_FileChooser.getSelectedFile();
/* 2378:2633 */       if (!sFile.getName().toLowerCase().endsWith(MODEL_FILE_EXTENSION)) {
/* 2379:2634 */         sFile = new File(sFile.getParent(), sFile.getName() + MODEL_FILE_EXTENSION);
/* 2380:     */       }
/* 2381:2637 */       this.m_Log.statusMessage("Saving model to file...");
/* 2382:     */       try
/* 2383:     */       {
/* 2384:2640 */         OutputStream os = new FileOutputStream(sFile);
/* 2385:2641 */         if (sFile.getName().endsWith(".gz")) {
/* 2386:2642 */           os = new GZIPOutputStream(os);
/* 2387:     */         }
/* 2388:2644 */         ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
/* 2389:2645 */         objectOutputStream.writeObject(classifier);
/* 2390:2646 */         trainHeader = trainHeader.stringFreeStructure();
/* 2391:2647 */         if (trainHeader != null) {
/* 2392:2648 */           objectOutputStream.writeObject(trainHeader);
/* 2393:     */         }
/* 2394:2650 */         objectOutputStream.flush();
/* 2395:2651 */         objectOutputStream.close();
/* 2396:     */       }
/* 2397:     */       catch (Exception e)
/* 2398:     */       {
/* 2399:2654 */         JOptionPane.showMessageDialog(null, e, "Save Failed", 0);
/* 2400:     */         
/* 2401:2656 */         saveOK = false;
/* 2402:     */       }
/* 2403:2658 */       if (saveOK) {
/* 2404:2659 */         this.m_Log.logMessage("Saved model (" + name + ") to file '" + sFile.getName() + "'");
/* 2405:     */       }
/* 2406:2662 */       this.m_Log.statusMessage("OK");
/* 2407:     */     }
/* 2408:     */   }
/* 2409:     */   
/* 2410:     */   protected void loadClassifier()
/* 2411:     */   {
/* 2412:2671 */     this.m_FileChooser.addChoosableFileFilter(this.m_PMMLModelFilter);
/* 2413:2672 */     this.m_FileChooser.setFileFilter(this.m_ModelFilter);
/* 2414:2673 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 2415:2674 */     if (returnVal == 0)
/* 2416:     */     {
/* 2417:2675 */       File selected = this.m_FileChooser.getSelectedFile();
/* 2418:2676 */       Classifier classifier = null;
/* 2419:2677 */       Instances trainHeader = null;
/* 2420:     */       
/* 2421:2679 */       this.m_Log.statusMessage("Loading model from file...");
/* 2422:     */       try
/* 2423:     */       {
/* 2424:2682 */         InputStream is = new FileInputStream(selected);
/* 2425:2683 */         if (selected.getName().endsWith(PMML_FILE_EXTENSION))
/* 2426:     */         {
/* 2427:2684 */           PMMLModel model = PMMLFactory.getPMMLModel(is, this.m_Log);
/* 2428:2685 */           if ((model instanceof PMMLClassifier)) {
/* 2429:2686 */             classifier = (PMMLClassifier)model;
/* 2430:     */           } else {
/* 2431:2692 */             throw new Exception("PMML model is not a classification/regression model!");
/* 2432:     */           }
/* 2433:     */         }
/* 2434:     */         else
/* 2435:     */         {
/* 2436:2696 */           if (selected.getName().endsWith(".gz")) {
/* 2437:2697 */             is = new GZIPInputStream(is);
/* 2438:     */           }
/* 2439:2699 */           ObjectInputStream objectInputStream = new ObjectInputStream(is);
/* 2440:2700 */           classifier = (Classifier)objectInputStream.readObject();
/* 2441:     */           try
/* 2442:     */           {
/* 2443:2702 */             trainHeader = (Instances)objectInputStream.readObject();
/* 2444:     */           }
/* 2445:     */           catch (Exception e) {}
/* 2446:2705 */           objectInputStream.close();
/* 2447:     */         }
/* 2448:     */       }
/* 2449:     */       catch (Exception e)
/* 2450:     */       {
/* 2451:2709 */         JOptionPane.showMessageDialog(null, e, "Load Failed", 0);
/* 2452:     */       }
/* 2453:2713 */       this.m_Log.statusMessage("OK");
/* 2454:2715 */       if (classifier != null)
/* 2455:     */       {
/* 2456:2716 */         this.m_Log.logMessage("Loaded model from file '" + selected.getName() + "'");
/* 2457:2717 */         String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 2458:2718 */         String cname = classifier.getClass().getName();
/* 2459:2719 */         if (cname.startsWith("weka.classifiers.")) {
/* 2460:2720 */           cname = cname.substring("weka.classifiers.".length());
/* 2461:     */         }
/* 2462:2722 */         name = name + cname + " from file '" + selected.getName() + "'";
/* 2463:2723 */         StringBuffer outBuff = new StringBuffer();
/* 2464:     */         
/* 2465:2725 */         outBuff.append("=== Model information ===\n\n");
/* 2466:2726 */         outBuff.append("Filename:     " + selected.getName() + "\n");
/* 2467:2727 */         outBuff.append("Scheme:       " + classifier.getClass().getName());
/* 2468:2728 */         if ((classifier instanceof OptionHandler))
/* 2469:     */         {
/* 2470:2729 */           String[] o = ((OptionHandler)classifier).getOptions();
/* 2471:2730 */           outBuff.append(" " + Utils.joinOptions(o));
/* 2472:     */         }
/* 2473:2732 */         outBuff.append("\n");
/* 2474:2733 */         if (trainHeader != null)
/* 2475:     */         {
/* 2476:2734 */           outBuff.append("Relation:     " + trainHeader.relationName() + '\n');
/* 2477:2735 */           outBuff.append("Attributes:   " + trainHeader.numAttributes() + '\n');
/* 2478:2736 */           if (trainHeader.numAttributes() < 100) {
/* 2479:2737 */             for (int i = 0; i < trainHeader.numAttributes(); i++) {
/* 2480:2738 */               outBuff.append("              " + trainHeader.attribute(i).name() + '\n');
/* 2481:     */             }
/* 2482:     */           } else {
/* 2483:2742 */             outBuff.append("              [list of attributes omitted]\n");
/* 2484:     */           }
/* 2485:     */         }
/* 2486:     */         else
/* 2487:     */         {
/* 2488:2745 */           outBuff.append("\nTraining data unknown\n");
/* 2489:     */         }
/* 2490:2748 */         outBuff.append("\n=== Classifier model ===\n\n");
/* 2491:2749 */         outBuff.append(classifier.toString() + "\n");
/* 2492:     */         
/* 2493:2751 */         this.m_History.addResult(name, outBuff);
/* 2494:2752 */         this.m_History.setSingle(name);
/* 2495:2753 */         ArrayList<Object> vv = new ArrayList();
/* 2496:2754 */         vv.add(classifier);
/* 2497:2755 */         if (trainHeader != null) {
/* 2498:2756 */           vv.add(trainHeader);
/* 2499:     */         }
/* 2500:2759 */         String grph = null;
/* 2501:2760 */         if ((classifier instanceof Drawable)) {
/* 2502:     */           try
/* 2503:     */           {
/* 2504:2762 */             grph = ((Drawable)classifier).graph();
/* 2505:     */           }
/* 2506:     */           catch (Exception ex) {}
/* 2507:     */         }
/* 2508:2766 */         if (grph != null) {
/* 2509:2767 */           vv.add(grph);
/* 2510:     */         }
/* 2511:2770 */         this.m_History.addObject(name, vv);
/* 2512:     */       }
/* 2513:     */     }
/* 2514:     */   }
/* 2515:     */   
/* 2516:     */   protected void reevaluateModel(final String name, final Classifier classifier, final Instances trainHeader)
/* 2517:     */   {
/* 2518:2786 */     if (this.m_RunThread == null)
/* 2519:     */     {
/* 2520:2787 */       synchronized (this)
/* 2521:     */       {
/* 2522:2788 */         this.m_StartBut.setEnabled(false);
/* 2523:2789 */         this.m_StopBut.setEnabled(true);
/* 2524:     */       }
/* 2525:2791 */       this.m_RunThread = new Thread()
/* 2526:     */       {
/* 2527:     */         public void run()
/* 2528:     */         {
/* 2529:2795 */           ClassifierPanel.this.m_Log.statusMessage("Setting up...");
/* 2530:2796 */           Classifier classifierToUse = classifier;
/* 2531:     */           
/* 2532:2798 */           StringBuffer outBuff = ClassifierPanel.this.m_History.getNamedBuffer(name);
/* 2533:2799 */           ConverterUtils.DataSource source = null;
/* 2534:2800 */           Instances userTestStructure = null;
/* 2535:2801 */           ClassifierErrorsPlotInstances plotInstances = null;
/* 2536:     */           
/* 2537:2803 */           CostMatrix costMatrix = null;
/* 2538:2804 */           if (ClassifierPanel.this.m_EvalWRTCostsBut.isSelected()) {
/* 2539:2805 */             costMatrix = new CostMatrix((CostMatrix)ClassifierPanel.this.m_CostMatrixEditor.getValue());
/* 2540:     */           }
/* 2541:2808 */           boolean outputConfusion = ClassifierPanel.this.m_OutputConfusionBut.isSelected();
/* 2542:2809 */           boolean outputPerClass = ClassifierPanel.this.m_OutputPerClassBut.isSelected();
/* 2543:2810 */           boolean outputSummary = true;
/* 2544:2811 */           boolean outputEntropy = ClassifierPanel.this.m_OutputEntropyBut.isSelected();
/* 2545:2812 */           boolean saveVis = ClassifierPanel.this.m_StorePredictionsBut.isSelected();
/* 2546:2813 */           boolean outputPredictionsText = ClassifierPanel.this.m_ClassificationOutputEditor.getValue().getClass() != Null.class;
/* 2547:     */           
/* 2548:2815 */           String grph = null;
/* 2549:2816 */           Evaluation eval = null;
/* 2550:     */           try
/* 2551:     */           {
/* 2552:2820 */             boolean incrementalLoader = ClassifierPanel.this.m_TestLoader instanceof IncrementalConverter;
/* 2553:2822 */             if ((ClassifierPanel.this.m_TestLoader != null) && (ClassifierPanel.this.m_TestLoader.getStructure() != null))
/* 2554:     */             {
/* 2555:2823 */               ClassifierPanel.this.m_TestLoader.reset();
/* 2556:2824 */               if (((classifierToUse instanceof BatchPredictor)) && (((BatchPredictor)classifierToUse).implementsMoreEfficientBatchPrediction()) && ((ClassifierPanel.this.m_TestLoader instanceof ArffLoader))) {
/* 2557:2828 */                 ((ArffLoader)ClassifierPanel.this.m_TestLoader).setRetainStringVals(true);
/* 2558:     */               }
/* 2559:2830 */               source = new ConverterUtils.DataSource(ClassifierPanel.this.m_TestLoader);
/* 2560:2831 */               userTestStructure = source.getStructure();
/* 2561:2832 */               userTestStructure.setClassIndex(ClassifierPanel.this.m_TestClassIndex);
/* 2562:     */             }
/* 2563:2835 */             if (source == null) {
/* 2564:2836 */               throw new Exception("No user test set has been specified");
/* 2565:     */             }
/* 2566:2838 */             if (trainHeader != null)
/* 2567:     */             {
/* 2568:2839 */               if ((!trainHeader.equalHeaders(userTestStructure)) && 
/* 2569:2840 */                 (!(classifierToUse instanceof InputMappedClassifier)))
/* 2570:     */               {
/* 2571:2842 */                 boolean wrapClassifier = false;
/* 2572:2843 */                 if (!Utils.getDontShowDialog("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier"))
/* 2573:     */                 {
/* 2574:2845 */                   JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 2575:     */                   
/* 2576:2847 */                   Object[] stuff = new Object[2];
/* 2577:2848 */                   stuff[0] = "Data used to train model and test set are not compatible.\nWould you like to automatically wrap the classifier in\nan \"InputMappedClassifier\" before proceeding?.\n";
/* 2578:     */                   
/* 2579:     */ 
/* 2580:     */ 
/* 2581:2852 */                   stuff[1] = dontShow;
/* 2582:     */                   
/* 2583:2854 */                   int result = JOptionPane.showConfirmDialog(ClassifierPanel.this, stuff, "ClassifierPanel", 0);
/* 2584:2858 */                   if (result == 0) {
/* 2585:2859 */                     wrapClassifier = true;
/* 2586:     */                   }
/* 2587:2862 */                   if (dontShow.isSelected())
/* 2588:     */                   {
/* 2589:2863 */                     String response = wrapClassifier ? "yes" : "no";
/* 2590:2864 */                     Utils.setDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier", response);
/* 2591:     */                   }
/* 2592:     */                 }
/* 2593:     */                 else
/* 2594:     */                 {
/* 2595:2872 */                   String response = Utils.getDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier");
/* 2596:2875 */                   if ((response != null) && (response.equalsIgnoreCase("yes"))) {
/* 2597:2876 */                     wrapClassifier = true;
/* 2598:     */                   }
/* 2599:     */                 }
/* 2600:2880 */                 if (wrapClassifier)
/* 2601:     */                 {
/* 2602:2881 */                   InputMappedClassifier temp = new InputMappedClassifier();
/* 2603:     */                   
/* 2604:     */ 
/* 2605:2884 */                   temp.setClassifier(classifierToUse);
/* 2606:2885 */                   temp.setModelHeader(trainHeader);
/* 2607:2886 */                   temp.setTestStructure(userTestStructure);
/* 2608:2887 */                   classifierToUse = temp;
/* 2609:     */                 }
/* 2610:     */                 else
/* 2611:     */                 {
/* 2612:2889 */                   throw new Exception("Train and test set are not compatible\n" + trainHeader.equalHeadersMsg(userTestStructure));
/* 2613:     */                 }
/* 2614:     */               }
/* 2615:     */             }
/* 2616:2896 */             else if ((classifierToUse instanceof PMMLClassifier))
/* 2617:     */             {
/* 2618:2898 */               Instances miningSchemaStructure = ((PMMLClassifier)classifierToUse).getMiningSchema().getMiningSchemaAsInstances();
/* 2619:     */               
/* 2620:     */ 
/* 2621:2901 */               String className = miningSchemaStructure.classAttribute().name();
/* 2622:     */               
/* 2623:2903 */               Attribute classMatch = userTestStructure.attribute(className);
/* 2624:2904 */               if (classMatch == null) {
/* 2625:2905 */                 throw new Exception("Can't find a match for the PMML target field " + className + " in the " + "test instances!");
/* 2626:     */               }
/* 2627:2909 */               userTestStructure.setClass(classMatch);
/* 2628:     */             }
/* 2629:     */             else
/* 2630:     */             {
/* 2631:2911 */               userTestStructure.setClassIndex(userTestStructure.numAttributes() - 1);
/* 2632:     */             }
/* 2633:2915 */             if ((ClassifierPanel.this.m_Log instanceof TaskLogger)) {
/* 2634:2916 */               ((TaskLogger)ClassifierPanel.this.m_Log).taskStarted();
/* 2635:     */             }
/* 2636:2918 */             ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data...");
/* 2637:2919 */             ClassifierPanel.this.m_Log.logMessage("Re-evaluating classifier (" + name + ") on test set");
/* 2638:     */             
/* 2639:2921 */             eval = new Evaluation(userTestStructure, costMatrix);
/* 2640:2922 */             eval.setMetricsToDisplay(ClassifierPanel.this.m_selectedEvalMetrics);
/* 2641:     */             
/* 2642:     */ 
/* 2643:     */ 
/* 2644:     */ 
/* 2645:2927 */             plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/* 2646:2928 */             plotInstances.setInstances(trainHeader != null ? trainHeader : userTestStructure);
/* 2647:2929 */             plotInstances.setClassifier(classifierToUse);
/* 2648:2930 */             plotInstances.setClassIndex(trainHeader != null ? trainHeader.classIndex() : userTestStructure.classIndex());
/* 2649:2931 */             plotInstances.setSaveForVisualization(saveVis);
/* 2650:2932 */             plotInstances.setEvaluation(eval);
/* 2651:     */             
/* 2652:2934 */             outBuff.append("\n=== Re-evaluation on test set ===\n\n");
/* 2653:2935 */             outBuff.append("User supplied test set\n");
/* 2654:2936 */             outBuff.append("Relation:     " + userTestStructure.relationName() + '\n');
/* 2655:2938 */             if (incrementalLoader) {
/* 2656:2939 */               outBuff.append("Instances:     unknown (yet). Reading incrementally\n");
/* 2657:     */             } else {
/* 2658:2942 */               outBuff.append("Instances:    " + source.getDataSet().numInstances() + "\n");
/* 2659:     */             }
/* 2660:2945 */             outBuff.append("Attributes:   " + userTestStructure.numAttributes() + "\n\n");
/* 2661:2947 */             if ((trainHeader == null) && (!(classifierToUse instanceof PMMLClassifier))) {
/* 2662:2949 */               outBuff.append("NOTE - if test set is not compatible then results are unpredictable\n\n");
/* 2663:     */             }
/* 2664:2954 */             AbstractOutput classificationOutput = null;
/* 2665:2955 */             if (outputPredictionsText)
/* 2666:     */             {
/* 2667:2956 */               classificationOutput = (AbstractOutput)ClassifierPanel.this.m_ClassificationOutputEditor.getValue();
/* 2668:     */               
/* 2669:2958 */               classificationOutput.setHeader(userTestStructure);
/* 2670:2959 */               classificationOutput.setBuffer(outBuff);
/* 2671:     */             }
/* 2672:2963 */             eval = ClassifierPanel.setupEval(eval, classifierToUse, trainHeader != null ? trainHeader : userTestStructure, costMatrix, plotInstances, classificationOutput, false);
/* 2673:     */             
/* 2674:     */ 
/* 2675:2966 */             eval.useNoPriors();
/* 2676:2967 */             plotInstances.setUp();
/* 2677:2969 */             if (outputPredictionsText) {
/* 2678:2970 */               ClassifierPanel.this.printPredictionsHeader(outBuff, classificationOutput, "user test set");
/* 2679:     */             }
/* 2680:2974 */             int batchSize = 100;
/* 2681:2975 */             Instances batchInst = null;
/* 2682:2976 */             if (((classifierToUse instanceof BatchPredictor)) && (((BatchPredictor)classifierToUse).implementsMoreEfficientBatchPrediction()))
/* 2683:     */             {
/* 2684:2979 */               batchInst = new Instances(userTestStructure, 0);
/* 2685:2980 */               String batchSizeS = ((BatchPredictor)classifierToUse).getBatchSize();
/* 2686:2982 */               if ((batchSizeS != null) && (batchSizeS.length() > 0))
/* 2687:     */               {
/* 2688:     */                 try
/* 2689:     */                 {
/* 2690:2984 */                   batchSizeS = Environment.getSystemWide().substitute(batchSizeS);
/* 2691:     */                 }
/* 2692:     */                 catch (Exception ex) {}
/* 2693:     */                 try
/* 2694:     */                 {
/* 2695:2989 */                   batchSize = Integer.parseInt(batchSizeS);
/* 2696:     */                 }
/* 2697:     */                 catch (NumberFormatException e) {}
/* 2698:     */               }
/* 2699:     */             }
/* 2700:2996 */             int jj = 0;
/* 2701:2997 */             while (source.hasMoreElements(userTestStructure))
/* 2702:     */             {
/* 2703:2998 */               Instance instance = source.nextElement(userTestStructure);
/* 2704:3000 */               if (((classifierToUse instanceof BatchPredictor)) && (((BatchPredictor)classifierToUse).implementsMoreEfficientBatchPrediction()))
/* 2705:     */               {
/* 2706:3003 */                 batchInst.add(instance);
/* 2707:3004 */                 if (batchInst.numInstances() == batchSize)
/* 2708:     */                 {
/* 2709:3005 */                   Instances toPred = new Instances(batchInst);
/* 2710:3006 */                   for (int i = 0; i < toPred.numInstances(); i++) {
/* 2711:3007 */                     toPred.instance(i).setClassMissing();
/* 2712:     */                   }
/* 2713:3009 */                   double[][] predictions = ((BatchPredictor)classifierToUse).distributionsForInstances(toPred);
/* 2714:     */                   
/* 2715:     */ 
/* 2716:3012 */                   plotInstances.process(batchInst, predictions, eval);
/* 2717:3014 */                   if (outputPredictionsText) {
/* 2718:3015 */                     for (int kk = 0; kk < batchInst.numInstances(); kk++) {
/* 2719:3016 */                       classificationOutput.printClassification(predictions[kk], batchInst.instance(kk), kk);
/* 2720:     */                     }
/* 2721:     */                   }
/* 2722:3020 */                   jj += batchInst.numInstances();
/* 2723:3021 */                   ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data. Processed " + jj + " instances...");
/* 2724:     */                   
/* 2725:3023 */                   batchInst.delete();
/* 2726:     */                 }
/* 2727:     */               }
/* 2728:     */               else
/* 2729:     */               {
/* 2730:3026 */                 plotInstances.process(instance, classifierToUse, eval);
/* 2731:3027 */                 if (outputPredictionsText) {
/* 2732:3028 */                   classificationOutput.printClassification(classifierToUse, instance, jj);
/* 2733:     */                 }
/* 2734:     */               }
/* 2735:3032 */               jj++;
/* 2736:3032 */               if (jj % 100 == 0) {
/* 2737:3033 */                 ClassifierPanel.this.m_Log.statusMessage("Evaluating on test data. Processed " + jj + " instances...");
/* 2738:     */               }
/* 2739:     */             }
/* 2740:3038 */             if (((classifierToUse instanceof BatchPredictor)) && (((BatchPredictor)classifierToUse).implementsMoreEfficientBatchPrediction()) && (batchInst.numInstances() > 0))
/* 2741:     */             {
/* 2742:3044 */               Instances toPred = new Instances(batchInst);
/* 2743:3045 */               for (int i = 0; i < toPred.numInstances(); i++) {
/* 2744:3046 */                 toPred.instance(i).setClassMissing();
/* 2745:     */               }
/* 2746:3049 */               double[][] predictions = ((BatchPredictor)classifierToUse).distributionsForInstances(toPred);
/* 2747:     */               
/* 2748:     */ 
/* 2749:3052 */               plotInstances.process(batchInst, predictions, eval);
/* 2750:3054 */               if (outputPredictionsText) {
/* 2751:3055 */                 for (int kk = 0; kk < batchInst.numInstances(); kk++) {
/* 2752:3056 */                   classificationOutput.printClassification(predictions[kk], batchInst.instance(kk), kk);
/* 2753:     */                 }
/* 2754:     */               }
/* 2755:     */             }
/* 2756:3062 */             if (outputPredictionsText) {
/* 2757:3063 */               classificationOutput.printFooter();
/* 2758:     */             }
/* 2759:3065 */             if ((outputPredictionsText) && (classificationOutput.generatesOutput())) {
/* 2760:3066 */               outBuff.append("\n");
/* 2761:     */             }
/* 2762:3069 */             if (outputSummary) {
/* 2763:3070 */               outBuff.append(eval.toSummaryString(outputEntropy) + "\n");
/* 2764:     */             }
/* 2765:3073 */             if (userTestStructure.classAttribute().isNominal())
/* 2766:     */             {
/* 2767:3075 */               if (outputPerClass) {
/* 2768:3076 */                 outBuff.append(eval.toClassDetailsString() + "\n");
/* 2769:     */               }
/* 2770:3079 */               if (outputConfusion) {
/* 2771:3080 */                 outBuff.append(eval.toMatrixString() + "\n");
/* 2772:     */               }
/* 2773:     */             }
/* 2774:3084 */             ClassifierPanel.this.m_History.updateResult(name);
/* 2775:3085 */             ClassifierPanel.this.m_Log.logMessage("Finished re-evaluation");
/* 2776:3086 */             ClassifierPanel.this.m_Log.statusMessage("OK");
/* 2777:     */           }
/* 2778:     */           catch (Exception ex)
/* 2779:     */           {
/* 2780:     */             Settings settings;
/* 2781:     */             ArrayList<Object> vv;
/* 2782:     */             ArrayList<Object> vv;
/* 2783:3088 */             ex.printStackTrace();
/* 2784:3089 */             ClassifierPanel.this.m_Log.logMessage(ex.getMessage());
/* 2785:3090 */             ClassifierPanel.this.m_Log.statusMessage("See error log");
/* 2786:     */             
/* 2787:3092 */             ex.printStackTrace();
/* 2788:3093 */             ClassifierPanel.this.m_Log.logMessage(ex.getMessage());
/* 2789:3094 */             JOptionPane.showMessageDialog(ClassifierPanel.this, "Problem evaluating classifier:\n" + ex.getMessage(), "Evaluate classifier", 0);
/* 2790:     */             
/* 2791:     */ 
/* 2792:3097 */             ClassifierPanel.this.m_Log.statusMessage("Problem evaluating classifier");
/* 2793:     */           }
/* 2794:     */           finally
/* 2795:     */           {
/* 2796:     */             try
/* 2797:     */             {
/* 2798:     */               Settings settings;
/* 2799:     */               ArrayList<Object> vv;
/* 2800:     */               ArrayList<Object> vv;
/* 2801:3100 */               if ((classifierToUse instanceof PMMLClassifier)) {
/* 2802:3105 */                 ((PMMLClassifier)classifierToUse).done();
/* 2803:     */               }
/* 2804:3108 */               if ((plotInstances != null) && (plotInstances.getPlotInstances() != null) && (plotInstances.getPlotInstances().numInstances() > 0))
/* 2805:     */               {
/* 2806:3111 */                 ClassifierPanel.this.m_CurrentVis = new VisualizePanel();
/* 2807:3112 */                 if (ClassifierPanel.this.getMainApplication() != null)
/* 2808:     */                 {
/* 2809:3113 */                   Settings settings = ClassifierPanel.this.getMainApplication().getApplicationSettings();
/* 2810:3114 */                   ClassifierPanel.this.m_CurrentVis.applySettings(settings, "weka.gui.workbench.visualizepanel");
/* 2811:     */                 }
/* 2812:3117 */                 ClassifierPanel.this.m_CurrentVis.setName(name + " (" + userTestStructure.relationName() + ")");
/* 2813:     */                 
/* 2814:3119 */                 ClassifierPanel.this.m_CurrentVis.setLog(ClassifierPanel.this.m_Log);
/* 2815:3120 */                 ClassifierPanel.this.m_CurrentVis.addPlot(plotInstances.getPlotData(name));
/* 2816:     */                 
/* 2817:3122 */                 ClassifierPanel.this.m_CurrentVis.setColourIndex(plotInstances.getPlotInstances().classIndex());
/* 2818:     */                 
/* 2819:3124 */                 plotInstances.cleanUp();
/* 2820:3126 */                 if ((classifierToUse instanceof Drawable)) {
/* 2821:     */                   try
/* 2822:     */                   {
/* 2823:3128 */                     grph = ((Drawable)classifierToUse).graph();
/* 2824:     */                   }
/* 2825:     */                   catch (Exception ex) {}
/* 2826:     */                 }
/* 2827:3133 */                 if (saveVis)
/* 2828:     */                 {
/* 2829:3134 */                   ArrayList<Object> vv = new ArrayList();
/* 2830:3135 */                   vv.add(classifier);
/* 2831:3136 */                   if (trainHeader != null) {
/* 2832:3137 */                     vv.add(trainHeader);
/* 2833:     */                   }
/* 2834:3139 */                   vv.add(ClassifierPanel.this.m_CurrentVis);
/* 2835:3140 */                   if (grph != null) {
/* 2836:3141 */                     vv.add(grph);
/* 2837:     */                   }
/* 2838:3143 */                   if ((eval != null) && (eval.predictions() != null))
/* 2839:     */                   {
/* 2840:3144 */                     vv.add(eval.predictions());
/* 2841:3145 */                     vv.add(userTestStructure.classAttribute());
/* 2842:     */                   }
/* 2843:3147 */                   ClassifierPanel.this.m_History.addObject(name, vv);
/* 2844:     */                 }
/* 2845:     */                 else
/* 2846:     */                 {
/* 2847:3149 */                   ArrayList<Object> vv = new ArrayList();
/* 2848:3150 */                   vv.add(classifierToUse);
/* 2849:3151 */                   if (trainHeader != null) {
/* 2850:3152 */                     vv.add(trainHeader);
/* 2851:     */                   }
/* 2852:3154 */                   ClassifierPanel.this.m_History.addObject(name, vv);
/* 2853:     */                 }
/* 2854:     */               }
/* 2855:     */             }
/* 2856:     */             catch (Exception ex)
/* 2857:     */             {
/* 2858:3158 */               ex.printStackTrace();
/* 2859:     */             }
/* 2860:3160 */             if (isInterrupted())
/* 2861:     */             {
/* 2862:3161 */               ClassifierPanel.this.m_Log.logMessage("Interrupted reevaluate model");
/* 2863:3162 */               ClassifierPanel.this.m_Log.statusMessage("Interrupted");
/* 2864:     */             }
/* 2865:3165 */             synchronized (this)
/* 2866:     */             {
/* 2867:3166 */               ClassifierPanel.this.m_StartBut.setEnabled(true);
/* 2868:3167 */               ClassifierPanel.this.m_StopBut.setEnabled(false);
/* 2869:3168 */               ClassifierPanel.this.m_RunThread = null;
/* 2870:     */             }
/* 2871:3171 */             if ((ClassifierPanel.this.m_Log instanceof TaskLogger)) {
/* 2872:3172 */               ((TaskLogger)ClassifierPanel.this.m_Log).taskFinished();
/* 2873:     */             }
/* 2874:     */           }
/* 2875:     */         }
/* 2876:3177 */       };
/* 2877:3178 */       this.m_RunThread.setPriority(1);
/* 2878:3179 */       this.m_RunThread.start();
/* 2879:     */     }
/* 2880:     */   }
/* 2881:     */   
/* 2882:     */   protected void updateCapabilitiesFilter(Capabilities filter)
/* 2883:     */   {
/* 2884:3192 */     if (filter == null)
/* 2885:     */     {
/* 2886:3193 */       this.m_ClassifierEditor.setCapabilitiesFilter(new Capabilities(null)); return;
/* 2887:     */     }
/* 2888:     */     Instances tempInst;
/* 2889:     */     Instances tempInst;
/* 2890:3197 */     if (!ExplorerDefaults.getInitGenericObjectEditorFilter()) {
/* 2891:3198 */       tempInst = new Instances(this.m_Instances, 0);
/* 2892:     */     } else {
/* 2893:3200 */       tempInst = new Instances(this.m_Instances);
/* 2894:     */     }
/* 2895:3202 */     tempInst.setClassIndex(this.m_ClassCombo.getSelectedIndex());
/* 2896:     */     Capabilities filterClass;
/* 2897:     */     try
/* 2898:     */     {
/* 2899:3205 */       filterClass = Capabilities.forInstances(tempInst);
/* 2900:     */     }
/* 2901:     */     catch (Exception e)
/* 2902:     */     {
/* 2903:3207 */       filterClass = new Capabilities(null);
/* 2904:     */     }
/* 2905:3211 */     this.m_ClassifierEditor.setCapabilitiesFilter(filterClass);
/* 2906:     */     
/* 2907:     */ 
/* 2908:3214 */     this.m_StartBut.setEnabled(true);
/* 2909:3215 */     Capabilities currentFilter = this.m_ClassifierEditor.getCapabilitiesFilter();
/* 2910:3216 */     Classifier classifier = (Classifier)this.m_ClassifierEditor.getValue();
/* 2911:3217 */     Capabilities currentSchemeCapabilities = null;
/* 2912:3218 */     if ((classifier != null) && (currentFilter != null) && ((classifier instanceof CapabilitiesHandler)))
/* 2913:     */     {
/* 2914:3220 */       currentSchemeCapabilities = ((CapabilitiesHandler)classifier).getCapabilities();
/* 2915:3223 */       if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/* 2916:3225 */         this.m_StartBut.setEnabled(false);
/* 2917:     */       }
/* 2918:     */     }
/* 2919:     */   }
/* 2920:     */   
/* 2921:     */   public void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent e)
/* 2922:     */   {
/* 2923:3237 */     if (e.getFilter() == null) {
/* 2924:3238 */       updateCapabilitiesFilter(null);
/* 2925:     */     } else {
/* 2926:3240 */       updateCapabilitiesFilter((Capabilities)e.getFilter().clone());
/* 2927:     */     }
/* 2928:     */   }
/* 2929:     */   
/* 2930:     */   public void setExplorer(Explorer parent)
/* 2931:     */   {
/* 2932:3252 */     this.m_Explorer = parent;
/* 2933:     */   }
/* 2934:     */   
/* 2935:     */   public Explorer getExplorer()
/* 2936:     */   {
/* 2937:3262 */     return this.m_Explorer;
/* 2938:     */   }
/* 2939:     */   
/* 2940:     */   public String getTabTitle()
/* 2941:     */   {
/* 2942:3272 */     return "Classify";
/* 2943:     */   }
/* 2944:     */   
/* 2945:     */   public String getTabTitleToolTip()
/* 2946:     */   {
/* 2947:3282 */     return "Classify instances";
/* 2948:     */   }
/* 2949:     */   
/* 2950:     */   public boolean requiresLog()
/* 2951:     */   {
/* 2952:3287 */     return true;
/* 2953:     */   }
/* 2954:     */   
/* 2955:     */   public boolean acceptsInstances()
/* 2956:     */   {
/* 2957:3292 */     return true;
/* 2958:     */   }
/* 2959:     */   
/* 2960:     */   public Defaults getDefaultSettings()
/* 2961:     */   {
/* 2962:3297 */     return new ClassifierPanelDefaults();
/* 2963:     */   }
/* 2964:     */   
/* 2965:     */   public boolean okToBeActive()
/* 2966:     */   {
/* 2967:3302 */     return this.m_Instances != null;
/* 2968:     */   }
/* 2969:     */   
/* 2970:     */   public void setActive(boolean active)
/* 2971:     */   {
/* 2972:3307 */     super.setActive(active);
/* 2973:3308 */     if (this.m_isActive) {
/* 2974:3310 */       settingsChanged();
/* 2975:     */     }
/* 2976:     */   }
/* 2977:     */   
/* 2978:     */   public void settingsChanged()
/* 2979:     */   {
/* 2980:3316 */     if (getMainApplication() != null)
/* 2981:     */     {
/* 2982:3317 */       if (!this.m_initialSettingsSet)
/* 2983:     */       {
/* 2984:3318 */         Object initialC = getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.CLASSIFIER_KEY, ClassifierPanelDefaults.CLASSIFIER, Environment.getSystemWide());
/* 2985:     */         
/* 2986:     */ 
/* 2987:     */ 
/* 2988:3322 */         this.m_ClassifierEditor.setValue(initialC);
/* 2989:     */         
/* 2990:3324 */         TestMode initialTestMode = (TestMode)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.TEST_MODE_KEY, ClassifierPanelDefaults.TEST_MODE, Environment.getSystemWide());
/* 2991:     */         
/* 2992:     */ 
/* 2993:     */ 
/* 2994:     */ 
/* 2995:3329 */         this.m_CVBut.setSelected(initialTestMode == TestMode.CROSS_VALIDATION);
/* 2996:3330 */         this.m_PercentBut.setSelected(initialTestMode == TestMode.PERCENTAGE_SPLIT);
/* 2997:3331 */         this.m_TrainBut.setSelected(initialTestMode == TestMode.USE_TRAINING_SET);
/* 2998:3332 */         this.m_TestSplitBut.setSelected(initialTestMode == TestMode.SEPARATE_TEST_SET);
/* 2999:     */         
/* 3000:3334 */         this.m_CVText.setEnabled(this.m_CVBut.isSelected());
/* 3001:3335 */         this.m_PercentText.setEnabled(this.m_PercentBut.isSelected());
/* 3002:3336 */         this.m_CVText.setText("" + getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.CROSS_VALIDATION_FOLDS_KEY, Integer.valueOf(10), Environment.getSystemWide()));
/* 3003:     */         
/* 3004:     */ 
/* 3005:     */ 
/* 3006:     */ 
/* 3007:     */ 
/* 3008:3342 */         this.m_PercentText.setText("" + getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.PERCENTAGE_SPLIT_KEY, Integer.valueOf(66), Environment.getSystemWide()));
/* 3009:     */         
/* 3010:     */ 
/* 3011:     */ 
/* 3012:     */ 
/* 3013:     */ 
/* 3014:     */ 
/* 3015:     */ 
/* 3016:3350 */         this.m_OutputModelBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_MODEL_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/* 3017:     */         
/* 3018:     */ 
/* 3019:     */ 
/* 3020:3354 */         this.m_OutputPerClassBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_PER_CLASS_STATS_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/* 3021:     */         
/* 3022:     */ 
/* 3023:     */ 
/* 3024:     */ 
/* 3025:3359 */         this.m_OutputEntropyBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_ENTROPY_EVAL_METRICS_KEY, Boolean.valueOf(false), Environment.getSystemWide())).booleanValue());
/* 3026:     */         
/* 3027:     */ 
/* 3028:     */ 
/* 3029:     */ 
/* 3030:3364 */         this.m_OutputConfusionBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_CONFUSION_MATRIX_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/* 3031:     */         
/* 3032:     */ 
/* 3033:     */ 
/* 3034:     */ 
/* 3035:3369 */         this.m_StorePredictionsBut.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.STORE_PREDICTIONS_FOR_VIS_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/* 3036:     */         
/* 3037:     */ 
/* 3038:     */ 
/* 3039:     */ 
/* 3040:3374 */         this.m_errorPlotPointSizeProportionalToMargin.setSelected(((Boolean)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.ERROR_PLOT_POINT_SIZE_PROP_TO_MARGIN_KEY, Boolean.valueOf(false), Environment.getSystemWide())).booleanValue());
/* 3041:     */         
/* 3042:     */ 
/* 3043:     */ 
/* 3044:     */ 
/* 3045:     */ 
/* 3046:3380 */         this.m_RandomSeedText.setText("" + getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.RANDOM_SEED_KEY, Integer.valueOf(1), Environment.getSystemWide()));
/* 3047:     */       }
/* 3048:3385 */       this.m_initialSettingsSet = true;
/* 3049:     */       
/* 3050:3387 */       Font outputFont = (Font)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_FONT_KEY, ClassifierPanelDefaults.OUTPUT_FONT, Environment.getSystemWide());
/* 3051:     */       
/* 3052:     */ 
/* 3053:     */ 
/* 3054:3391 */       this.m_OutText.setFont(outputFont);
/* 3055:3392 */       this.m_History.setFont(outputFont);
/* 3056:3393 */       Color textColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_TEXT_COLOR_KEY, ClassifierPanelDefaults.OUTPUT_TEXT_COLOR, Environment.getSystemWide());
/* 3057:     */       
/* 3058:     */ 
/* 3059:     */ 
/* 3060:     */ 
/* 3061:3398 */       this.m_OutText.setForeground(textColor);
/* 3062:3399 */       this.m_History.setForeground(textColor);
/* 3063:3400 */       Color outputBackgroundColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), ClassifierPanelDefaults.OUTPUT_BACKGROUND_COLOR_KEY, ClassifierPanelDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide());
/* 3064:     */       
/* 3065:     */ 
/* 3066:     */ 
/* 3067:     */ 
/* 3068:     */ 
/* 3069:3406 */       this.m_OutText.setBackground(outputBackgroundColor);
/* 3070:3407 */       this.m_History.setBackground(outputBackgroundColor);
/* 3071:     */     }
/* 3072:     */   }
/* 3073:     */   
/* 3074:     */   public static enum TestMode
/* 3075:     */   {
/* 3076:3412 */     CROSS_VALIDATION,  PERCENTAGE_SPLIT,  USE_TRAINING_SET,  SEPARATE_TEST_SET;
/* 3077:     */     
/* 3078:     */     private TestMode() {}
/* 3079:     */   }
/* 3080:     */   
/* 3081:     */   protected static final class ClassifierPanelDefaults
/* 3082:     */     extends Defaults
/* 3083:     */   {
/* 3084:     */     public static final String ID = "weka.gui.explorer.classifierpanel";
/* 3085:3421 */     protected static final Settings.SettingKey CLASSIFIER_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.initialClassifier", "Initial classifier", "On startup, set this classifier as the default one");
/* 3086:3424 */     protected static final Classifier CLASSIFIER = new ZeroR();
/* 3087:3426 */     protected static final Settings.SettingKey TEST_MODE_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.initialTestMode", "Default test mode", "");
/* 3088:3428 */     protected static final ClassifierPanel.TestMode TEST_MODE = ClassifierPanel.TestMode.CROSS_VALIDATION;
/* 3089:3430 */     protected static final Settings.SettingKey CROSS_VALIDATION_FOLDS_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.crossValidationFolds", "Default cross validation folds", "");
/* 3090:     */     protected static final int CROSS_VALIDATION_FOLDS = 10;
/* 3091:3435 */     protected static final Settings.SettingKey PERCENTAGE_SPLIT_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.percentageSplit", "Default percentage split", "");
/* 3092:     */     protected static final int PERCENTAGE_SPLIT = 66;
/* 3093:3440 */     protected static final Settings.SettingKey OUTPUT_MODEL_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputModel", "Output model obtained from the full training set", "");
/* 3094:     */     protected static final boolean OUTPUT_MODEL = true;
/* 3095:3445 */     protected static final Settings.SettingKey OUTPUT_PER_CLASS_STATS_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputPerClassStats", "Output per-class statistics", "");
/* 3096:     */     protected static final boolean OUTPUT_PER_CLASS_STATS = true;
/* 3097:3450 */     protected static final Settings.SettingKey OUTPUT_ENTROPY_EVAL_METRICS_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputEntropyMetrics", "Output entropy evaluation metrics", "");
/* 3098:     */     protected static final boolean OUTPUT_ENTROPY_EVAL_METRICS = false;
/* 3099:3455 */     protected static final Settings.SettingKey OUTPUT_CONFUSION_MATRIX_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputConfusionMatrix", "Output confusion matrix", "");
/* 3100:     */     protected static final boolean OUTPUT_CONFUSION_MATRIX = true;
/* 3101:3460 */     protected static final Settings.SettingKey STORE_PREDICTIONS_FOR_VIS_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.storePredsForVis", "Store predictions for visualization", "");
/* 3102:     */     protected static final boolean STORE_PREDICTIONS_FOR_VIS = true;
/* 3103:3471 */     protected static final Settings.SettingKey PREDICTION_FORMATTER_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.predictionFormatter", "Prediction formatter", "");
/* 3104:3474 */     protected static final AbstractOutput PREDICTION_FORMATTER = new Null();
/* 3105:3476 */     protected static final Settings.SettingKey ERROR_PLOT_POINT_SIZE_PROP_TO_MARGIN_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.errorPlotPointSizePropToMargin", "Error plot point size proportional to margin", "In classifier error plots the point size will be set proportional to the absolute value of the prediction margin (affects classification only)");
/* 3106:     */     protected static final boolean ERROR_PLOT_POINT_SIZE_PROP_TO_MARGIN = false;
/* 3107:3485 */     protected static final Settings.SettingKey COST_SENSITIVE_EVALUATION_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.costSensitiveEval", "Cost sensitive evaluation", "Evaluate errors with respect to a cost matrix");
/* 3108:     */     protected static final boolean COST_SENSITIVE_EVALUATION = false;
/* 3109:3491 */     protected static final Settings.SettingKey COST_MATRIX_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.costMatrix", "Cost matrix for cost sensitive evaluation", "");
/* 3110:3494 */     protected static final CostMatrix COST_MATRIX = new CostMatrix(1);
/* 3111:3496 */     protected static final Settings.SettingKey RANDOM_SEED_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.randomSeed", "Random seed for XVal / % Split", "The seed for randomization");
/* 3112:     */     protected static final int RANDOM_SEED = 1;
/* 3113:3501 */     protected static final Settings.SettingKey PRESERVE_ORDER_FOR_PERCENT_SPLIT_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.preserveOrder", "Preserve order for % Split", "Preserves the order in a percentage split");
/* 3114:     */     protected static final boolean PRESERVE_ORDER_FOR_PERCENT_SPLIT = false;
/* 3115:3507 */     protected static final Settings.SettingKey SOURCE_CODE_CLASS_NAME_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.sourceCodeClassName", "Source code class name", "Default classname of a Sourcable classifier");
/* 3116:     */     protected static final String SOURCE_CODE_CLASS_NAME = "WekaClassifier";
/* 3117:3512 */     protected static final Settings.SettingKey OUTPUT_FONT_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputFont", "Font for text output", "Font to use in the output area");
/* 3118:3515 */     protected static final Font OUTPUT_FONT = new Font("Monospaced", 0, 12);
/* 3119:3518 */     protected static final Settings.SettingKey OUTPUT_TEXT_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputFontColor", "Output text color", "Color of output text");
/* 3120:3521 */     protected static final Color OUTPUT_TEXT_COLOR = Color.black;
/* 3121:3523 */     protected static final Settings.SettingKey OUTPUT_BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.classifierpanel.outputBackgroundColor", "Output background color", "Output background color");
/* 3122:3526 */     protected static final Color OUTPUT_BACKGROUND_COLOR = Color.white;
/* 3123:     */     private static final long serialVersionUID = 7109938811150596359L;
/* 3124:     */     
/* 3125:     */     public ClassifierPanelDefaults()
/* 3126:     */     {
/* 3127:3530 */       super();
/* 3128:     */       
/* 3129:3532 */       this.m_defaults.put(CLASSIFIER_KEY, CLASSIFIER);
/* 3130:3533 */       this.m_defaults.put(TEST_MODE_KEY, TEST_MODE);
/* 3131:3534 */       this.m_defaults.put(CROSS_VALIDATION_FOLDS_KEY, Integer.valueOf(10));
/* 3132:3535 */       this.m_defaults.put(PERCENTAGE_SPLIT_KEY, Integer.valueOf(66));
/* 3133:3536 */       this.m_defaults.put(OUTPUT_MODEL_KEY, Boolean.valueOf(true));
/* 3134:3537 */       this.m_defaults.put(OUTPUT_PER_CLASS_STATS_KEY, Boolean.valueOf(true));
/* 3135:3538 */       this.m_defaults.put(OUTPUT_ENTROPY_EVAL_METRICS_KEY, Boolean.valueOf(false));
/* 3136:     */       
/* 3137:3540 */       this.m_defaults.put(OUTPUT_CONFUSION_MATRIX_KEY, Boolean.valueOf(true));
/* 3138:3541 */       this.m_defaults.put(STORE_PREDICTIONS_FOR_VIS_KEY, Boolean.valueOf(true));
/* 3139:     */       
/* 3140:3543 */       this.m_defaults.put(PREDICTION_FORMATTER_KEY, PREDICTION_FORMATTER);
/* 3141:3544 */       this.m_defaults.put(ERROR_PLOT_POINT_SIZE_PROP_TO_MARGIN_KEY, Boolean.valueOf(false));
/* 3142:     */       
/* 3143:3546 */       this.m_defaults.put(COST_SENSITIVE_EVALUATION_KEY, Boolean.valueOf(false));
/* 3144:3547 */       this.m_defaults.put(COST_MATRIX_KEY, COST_MATRIX);
/* 3145:3548 */       this.m_defaults.put(RANDOM_SEED_KEY, Integer.valueOf(1));
/* 3146:3549 */       this.m_defaults.put(PRESERVE_ORDER_FOR_PERCENT_SPLIT_KEY, Boolean.valueOf(false));
/* 3147:     */       
/* 3148:3551 */       this.m_defaults.put(SOURCE_CODE_CLASS_NAME_KEY, "WekaClassifier");
/* 3149:3552 */       this.m_defaults.put(OUTPUT_FONT_KEY, OUTPUT_FONT);
/* 3150:3553 */       this.m_defaults.put(OUTPUT_TEXT_COLOR_KEY, OUTPUT_TEXT_COLOR);
/* 3151:3554 */       this.m_defaults.put(OUTPUT_BACKGROUND_COLOR_KEY, OUTPUT_BACKGROUND_COLOR);
/* 3152:     */     }
/* 3153:     */   }
/* 3154:     */   
/* 3155:     */   public static void main(String[] args)
/* 3156:     */   {
/* 3157:     */     try
/* 3158:     */     {
/* 3159:3566 */       JFrame jf = new JFrame("Weka Explorer: Classifier");
/* 3160:     */       
/* 3161:3568 */       jf.getContentPane().setLayout(new BorderLayout());
/* 3162:3569 */       ClassifierPanel sp = new ClassifierPanel();
/* 3163:3570 */       jf.getContentPane().add(sp, "Center");
/* 3164:3571 */       LogPanel lp = new LogPanel();
/* 3165:3572 */       sp.setLog(lp);
/* 3166:3573 */       jf.getContentPane().add(lp, "South");
/* 3167:3574 */       jf.addWindowListener(new WindowAdapter()
/* 3168:     */       {
/* 3169:     */         public void windowClosing(WindowEvent e)
/* 3170:     */         {
/* 3171:3577 */           this.val$jf.dispose();
/* 3172:3578 */           System.exit(0);
/* 3173:     */         }
/* 3174:3580 */       });
/* 3175:3581 */       jf.pack();
/* 3176:3582 */       jf.setSize(800, 600);
/* 3177:3583 */       jf.setVisible(true);
/* 3178:3584 */       if (args.length == 1)
/* 3179:     */       {
/* 3180:3585 */         System.err.println("Loading instances from " + args[0]);
/* 3181:3586 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 3182:     */         
/* 3183:3588 */         Instances i = new Instances(r);
/* 3184:3589 */         sp.setInstances(i);
/* 3185:     */       }
/* 3186:     */     }
/* 3187:     */     catch (Exception ex)
/* 3188:     */     {
/* 3189:3592 */       ex.printStackTrace();
/* 3190:3593 */       System.err.println(ex.getMessage());
/* 3191:     */     }
/* 3192:     */   }
/* 3193:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.ClassifierPanel
 * JD-Core Version:    0.7.0.1
 */