/*    1:     */ package weka.gui.explorer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.GridBagConstraints;
/*    9:     */ import java.awt.GridBagLayout;
/*   10:     */ import java.awt.GridLayout;
/*   11:     */ import java.awt.Insets;
/*   12:     */ import java.awt.Point;
/*   13:     */ import java.awt.event.ActionEvent;
/*   14:     */ import java.awt.event.ActionListener;
/*   15:     */ import java.awt.event.MouseAdapter;
/*   16:     */ import java.awt.event.MouseEvent;
/*   17:     */ import java.awt.event.WindowAdapter;
/*   18:     */ import java.awt.event.WindowEvent;
/*   19:     */ import java.beans.PropertyChangeEvent;
/*   20:     */ import java.beans.PropertyChangeListener;
/*   21:     */ import java.io.BufferedReader;
/*   22:     */ import java.io.BufferedWriter;
/*   23:     */ import java.io.FileReader;
/*   24:     */ import java.io.FileWriter;
/*   25:     */ import java.io.PrintStream;
/*   26:     */ import java.io.Reader;
/*   27:     */ import java.text.SimpleDateFormat;
/*   28:     */ import java.util.ArrayList;
/*   29:     */ import java.util.Date;
/*   30:     */ import java.util.Map;
/*   31:     */ import java.util.Random;
/*   32:     */ import java.util.Vector;
/*   33:     */ import javax.swing.BorderFactory;
/*   34:     */ import javax.swing.ButtonGroup;
/*   35:     */ import javax.swing.DefaultComboBoxModel;
/*   36:     */ import javax.swing.JButton;
/*   37:     */ import javax.swing.JComboBox;
/*   38:     */ import javax.swing.JFileChooser;
/*   39:     */ import javax.swing.JFrame;
/*   40:     */ import javax.swing.JLabel;
/*   41:     */ import javax.swing.JList;
/*   42:     */ import javax.swing.JMenuItem;
/*   43:     */ import javax.swing.JOptionPane;
/*   44:     */ import javax.swing.JPanel;
/*   45:     */ import javax.swing.JPopupMenu;
/*   46:     */ import javax.swing.JRadioButton;
/*   47:     */ import javax.swing.JScrollPane;
/*   48:     */ import javax.swing.JTextArea;
/*   49:     */ import javax.swing.JTextField;
/*   50:     */ import javax.swing.JViewport;
/*   51:     */ import javax.swing.event.ChangeEvent;
/*   52:     */ import javax.swing.event.ChangeListener;
/*   53:     */ import weka.attributeSelection.ASEvaluation;
/*   54:     */ import weka.attributeSelection.ASSearch;
/*   55:     */ import weka.attributeSelection.AttributeEvaluator;
/*   56:     */ import weka.attributeSelection.AttributeTransformer;
/*   57:     */ import weka.attributeSelection.BestFirst;
/*   58:     */ import weka.attributeSelection.CfsSubsetEval;
/*   59:     */ import weka.attributeSelection.GreedyStepwise;
/*   60:     */ import weka.attributeSelection.InfoGainAttributeEval;
/*   61:     */ import weka.attributeSelection.Ranker;
/*   62:     */ import weka.classifiers.meta.AttributeSelectedClassifier;
/*   63:     */ import weka.core.Attribute;
/*   64:     */ import weka.core.Capabilities;
/*   65:     */ import weka.core.CapabilitiesHandler;
/*   66:     */ import weka.core.Defaults;
/*   67:     */ import weka.core.Environment;
/*   68:     */ import weka.core.Instances;
/*   69:     */ import weka.core.Memory;
/*   70:     */ import weka.core.OptionHandler;
/*   71:     */ import weka.core.Settings;
/*   72:     */ import weka.core.Settings.SettingKey;
/*   73:     */ import weka.core.Utils;
/*   74:     */ import weka.gui.AbstractPerspective;
/*   75:     */ import weka.gui.ExtensionFileFilter;
/*   76:     */ import weka.gui.GUIApplication;
/*   77:     */ import weka.gui.GenericObjectEditor;
/*   78:     */ import weka.gui.LogPanel;
/*   79:     */ import weka.gui.Logger;
/*   80:     */ import weka.gui.PerspectiveInfo;
/*   81:     */ import weka.gui.PropertyPanel;
/*   82:     */ import weka.gui.ResultHistoryPanel;
/*   83:     */ import weka.gui.SaveBuffer;
/*   84:     */ import weka.gui.SysErrLog;
/*   85:     */ import weka.gui.TaskLogger;
/*   86:     */ import weka.gui.visualize.MatrixPanel;
/*   87:     */ 
/*   88:     */ @PerspectiveInfo(ID="weka.gui.explorer.attributeselectionpanel", title="Select attributes", toolTipText="Determine relevance of attributes", iconPath="weka/gui/weka_icon_new_small.png")
/*   89:     */ public class AttributeSelectionPanel
/*   90:     */   extends AbstractPerspective
/*   91:     */   implements Explorer.CapabilitiesFilterChangeListener, Explorer.ExplorerPanel, Explorer.LogHandler
/*   92:     */ {
/*   93:     */   static final long serialVersionUID = 5627185966993476142L;
/*   94: 124 */   protected Explorer m_Explorer = null;
/*   95: 127 */   protected GenericObjectEditor m_AttributeEvaluatorEditor = new GenericObjectEditor();
/*   96: 131 */   protected GenericObjectEditor m_AttributeSearchEditor = new GenericObjectEditor();
/*   97: 135 */   protected PropertyPanel m_AEEPanel = new PropertyPanel(this.m_AttributeEvaluatorEditor);
/*   98: 139 */   protected PropertyPanel m_ASEPanel = new PropertyPanel(this.m_AttributeSearchEditor);
/*   99: 143 */   protected JTextArea m_OutText = new JTextArea(20, 40);
/*  100: 146 */   protected Logger m_Log = new SysErrLog();
/*  101: 149 */   SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
/*  102: 152 */   protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
/*  103: 155 */   protected JComboBox m_ClassCombo = new JComboBox();
/*  104: 158 */   protected JRadioButton m_CVBut = new JRadioButton("Cross-validation");
/*  105: 161 */   protected JRadioButton m_TrainBut = new JRadioButton("Use full training set");
/*  106: 164 */   protected JLabel m_CVLab = new JLabel("Folds", 4);
/*  107: 167 */   protected JTextField m_CVText = new JTextField("10");
/*  108: 170 */   protected JLabel m_SeedLab = new JLabel("Seed", 4);
/*  109: 173 */   protected JTextField m_SeedText = new JTextField("1");
/*  110: 179 */   ActionListener m_RadioListener = new ActionListener()
/*  111:     */   {
/*  112:     */     public void actionPerformed(ActionEvent e)
/*  113:     */     {
/*  114: 182 */       AttributeSelectionPanel.this.updateRadioLinks();
/*  115:     */     }
/*  116:     */   };
/*  117: 187 */   protected JButton m_StartBut = new JButton("Start");
/*  118: 190 */   protected JButton m_StopBut = new JButton("Stop");
/*  119: 193 */   private final Dimension COMBO_SIZE = new Dimension(150, this.m_StartBut.getPreferredSize().height);
/*  120:     */   protected Instances m_Instances;
/*  121:     */   protected Thread m_RunThread;
/*  122:     */   protected boolean m_initialSettingsSet;
/*  123:     */   
/*  124:     */   public AttributeSelectionPanel()
/*  125:     */   {
/*  126: 216 */     this.m_OutText.setEditable(false);
/*  127: 217 */     this.m_OutText.setFont(new Font("Monospaced", 0, 12));
/*  128: 218 */     this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  129: 219 */     this.m_OutText.addMouseListener(new MouseAdapter()
/*  130:     */     {
/*  131:     */       public void mouseClicked(MouseEvent e)
/*  132:     */       {
/*  133: 222 */         if ((e.getModifiers() & 0x10) != 16) {
/*  134: 223 */           AttributeSelectionPanel.this.m_OutText.selectAll();
/*  135:     */         }
/*  136:     */       }
/*  137: 226 */     });
/*  138: 227 */     JPanel historyHolder = new JPanel(new BorderLayout());
/*  139: 228 */     historyHolder.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
/*  140:     */     
/*  141: 230 */     historyHolder.add(this.m_History, "Center");
/*  142: 231 */     this.m_AttributeEvaluatorEditor.setClassType(ASEvaluation.class);
/*  143: 232 */     this.m_AttributeEvaluatorEditor.setValue(ExplorerDefaults.getASEvaluator());
/*  144: 233 */     this.m_AttributeEvaluatorEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  145:     */     {
/*  146:     */       public void propertyChange(PropertyChangeEvent e)
/*  147:     */       {
/*  148: 237 */         if ((AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue() instanceof AttributeEvaluator))
/*  149:     */         {
/*  150: 238 */           if (!(AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue() instanceof Ranker))
/*  151:     */           {
/*  152: 239 */             Object backup = AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getBackup();
/*  153: 240 */             int result = JOptionPane.showConfirmDialog(null, "You must use use the Ranker search method in order to use\n" + AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue().getClass().getName() + ".\nShould I select the Ranker search method for you?", "Alert!", 0);
/*  154: 248 */             if (result == 0) {
/*  155: 249 */               AttributeSelectionPanel.this.m_AttributeSearchEditor.setValue(new Ranker());
/*  156: 252 */             } else if (backup != null) {
/*  157: 253 */               AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.setValue(backup);
/*  158:     */             }
/*  159:     */           }
/*  160:     */         }
/*  161: 258 */         else if ((AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue() instanceof Ranker))
/*  162:     */         {
/*  163: 259 */           Object backup = AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getBackup();
/*  164: 260 */           int result = JOptionPane.showConfirmDialog(null, "You must use use a search method that explores \nthe space of attribute subsets (such as GreedyStepwise) in order to use\n" + AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue().getClass().getName() + ".\nShould I select the GreedyStepwise search method for " + "you?\n(you can always switch to a different method afterwards)", "Alert!", 0);
/*  165: 272 */           if (result == 0) {
/*  166: 273 */             AttributeSelectionPanel.this.m_AttributeSearchEditor.setValue(new GreedyStepwise());
/*  167: 277 */           } else if (backup != null) {
/*  168: 278 */             AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.setValue(backup);
/*  169:     */           }
/*  170:     */         }
/*  171: 283 */         AttributeSelectionPanel.this.updateRadioLinks();
/*  172:     */         
/*  173: 285 */         AttributeSelectionPanel.this.m_StartBut.setEnabled(true);
/*  174:     */         
/*  175: 287 */         Capabilities currentFilter = AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getCapabilitiesFilter();
/*  176:     */         
/*  177: 289 */         ASEvaluation evaluator = (ASEvaluation)AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue();
/*  178:     */         
/*  179: 291 */         Capabilities currentSchemeCapabilities = null;
/*  180: 292 */         if ((evaluator != null) && (currentFilter != null) && ((evaluator instanceof CapabilitiesHandler)))
/*  181:     */         {
/*  182: 294 */           currentSchemeCapabilities = evaluator.getCapabilities();
/*  183: 297 */           if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/*  184: 299 */             AttributeSelectionPanel.this.m_StartBut.setEnabled(false);
/*  185:     */           }
/*  186:     */         }
/*  187: 302 */         AttributeSelectionPanel.this.repaint();
/*  188:     */       }
/*  189: 305 */     });
/*  190: 306 */     this.m_AttributeSearchEditor.setClassType(ASSearch.class);
/*  191: 307 */     this.m_AttributeSearchEditor.setValue(ExplorerDefaults.getASSearch());
/*  192: 308 */     this.m_AttributeSearchEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  193:     */     {
/*  194:     */       public void propertyChange(PropertyChangeEvent e)
/*  195:     */       {
/*  196: 312 */         if ((AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue() instanceof Ranker))
/*  197:     */         {
/*  198: 313 */           if (!(AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue() instanceof AttributeEvaluator))
/*  199:     */           {
/*  200: 314 */             Object backup = AttributeSelectionPanel.this.m_AttributeSearchEditor.getBackup();
/*  201: 315 */             int result = JOptionPane.showConfirmDialog(null, "You must use use an evaluator that evaluates\nsingle attributes (such as InfoGain) in order to use\nthe Ranker. Should I select the InfoGain evaluator for you?\n(You can always switch to a different method afterwards)", "Alert!", 0);
/*  202: 325 */             if (result == 0) {
/*  203: 326 */               AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.setValue(new InfoGainAttributeEval());
/*  204: 330 */             } else if (backup != null) {
/*  205: 331 */               AttributeSelectionPanel.this.m_AttributeSearchEditor.setValue(backup);
/*  206:     */             }
/*  207:     */           }
/*  208:     */         }
/*  209: 336 */         else if ((AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue() instanceof AttributeEvaluator))
/*  210:     */         {
/*  211: 337 */           Object backup = AttributeSelectionPanel.this.m_AttributeSearchEditor.getBackup();
/*  212: 338 */           int result = JOptionPane.showConfirmDialog(null, "You must use use an evaluator that evaluates\nsubsets of attributes (such as CFS) in order to use\n" + AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue().getClass().getName() + ".\nShould I select the CFS subset evaluator for you?" + "\n(you can always switch to a different method afterwards)", "Alert!", 0);
/*  213: 350 */           if (result == 0) {
/*  214: 351 */             AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.setValue(new CfsSubsetEval());
/*  215: 355 */           } else if (backup != null) {
/*  216: 356 */             AttributeSelectionPanel.this.m_AttributeSearchEditor.setValue(backup);
/*  217:     */           }
/*  218:     */         }
/*  219: 361 */         AttributeSelectionPanel.this.repaint();
/*  220:     */       }
/*  221: 364 */     });
/*  222: 365 */     this.m_ClassCombo.addActionListener(new ActionListener()
/*  223:     */     {
/*  224:     */       public void actionPerformed(ActionEvent e)
/*  225:     */       {
/*  226: 368 */         AttributeSelectionPanel.this.updateCapabilitiesFilter(AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getCapabilitiesFilter());
/*  227:     */       }
/*  228: 372 */     });
/*  229: 373 */     this.m_ClassCombo.setToolTipText("Select the attribute to use as the class");
/*  230: 374 */     this.m_TrainBut.setToolTipText("select attributes using the full training dataset");
/*  231:     */     
/*  232: 376 */     this.m_CVBut.setToolTipText("Perform a n-fold cross-validation");
/*  233:     */     
/*  234: 378 */     this.m_StartBut.setToolTipText("Starts attribute selection");
/*  235: 379 */     this.m_StopBut.setToolTipText("Stops a attribute selection task");
/*  236:     */     
/*  237: 381 */     this.m_ClassCombo.setPreferredSize(this.COMBO_SIZE);
/*  238: 382 */     this.m_ClassCombo.setMaximumSize(this.COMBO_SIZE);
/*  239: 383 */     this.m_ClassCombo.setMinimumSize(this.COMBO_SIZE);
/*  240: 384 */     this.m_History.setPreferredSize(this.COMBO_SIZE);
/*  241: 385 */     this.m_History.setMaximumSize(this.COMBO_SIZE);
/*  242: 386 */     this.m_History.setMinimumSize(this.COMBO_SIZE);
/*  243:     */     
/*  244: 388 */     this.m_ClassCombo.setEnabled(false);
/*  245: 389 */     this.m_TrainBut.setSelected(ExplorerDefaults.getASTestMode() == 0);
/*  246: 390 */     this.m_CVBut.setSelected(ExplorerDefaults.getASTestMode() == 1);
/*  247: 391 */     updateRadioLinks();
/*  248: 392 */     ButtonGroup bg = new ButtonGroup();
/*  249: 393 */     bg.add(this.m_TrainBut);
/*  250: 394 */     bg.add(this.m_CVBut);
/*  251:     */     
/*  252: 396 */     this.m_TrainBut.addActionListener(this.m_RadioListener);
/*  253: 397 */     this.m_CVBut.addActionListener(this.m_RadioListener);
/*  254:     */     
/*  255: 399 */     this.m_CVText.setText("" + ExplorerDefaults.getASCrossvalidationFolds());
/*  256: 400 */     this.m_SeedText.setText("" + ExplorerDefaults.getASRandomSeed());
/*  257:     */     
/*  258: 402 */     this.m_StartBut.setEnabled(false);
/*  259: 403 */     this.m_StopBut.setEnabled(false);
/*  260:     */     
/*  261: 405 */     this.m_StartBut.addActionListener(new ActionListener()
/*  262:     */     {
/*  263:     */       public void actionPerformed(ActionEvent e)
/*  264:     */       {
/*  265: 408 */         boolean proceed = true;
/*  266: 409 */         if (Explorer.m_Memory.memoryIsLow()) {
/*  267: 410 */           proceed = Explorer.m_Memory.showMemoryIsLow();
/*  268:     */         }
/*  269: 413 */         if (proceed) {
/*  270: 414 */           AttributeSelectionPanel.this.startAttributeSelection();
/*  271:     */         }
/*  272:     */       }
/*  273: 417 */     });
/*  274: 418 */     this.m_StopBut.addActionListener(new ActionListener()
/*  275:     */     {
/*  276:     */       public void actionPerformed(ActionEvent e)
/*  277:     */       {
/*  278: 421 */         AttributeSelectionPanel.this.stopAttributeSelection();
/*  279:     */       }
/*  280: 424 */     });
/*  281: 425 */     this.m_History.setHandleRightClicks(false);
/*  282:     */     
/*  283: 427 */     this.m_History.getList().addMouseListener(new MouseAdapter()
/*  284:     */     {
/*  285:     */       public void mouseClicked(MouseEvent e)
/*  286:     */       {
/*  287: 430 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/*  288:     */         {
/*  289: 432 */           int index = AttributeSelectionPanel.this.m_History.getList().locationToIndex(e.getPoint());
/*  290: 433 */           if (index != -1)
/*  291:     */           {
/*  292: 434 */             String name = AttributeSelectionPanel.this.m_History.getNameAtIndex(index);
/*  293: 435 */             AttributeSelectionPanel.this.visualize(name, e.getX(), e.getY());
/*  294:     */           }
/*  295:     */           else
/*  296:     */           {
/*  297: 437 */             AttributeSelectionPanel.this.visualize(null, e.getX(), e.getY());
/*  298:     */           }
/*  299:     */         }
/*  300:     */       }
/*  301: 443 */     });
/*  302: 444 */     JPanel p1 = new JPanel();
/*  303: 445 */     p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Attribute Evaluator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  304:     */     
/*  305:     */ 
/*  306: 448 */     p1.setLayout(new BorderLayout());
/*  307: 449 */     p1.add(this.m_AEEPanel, "North");
/*  308:     */     
/*  309: 451 */     JPanel p1_1 = new JPanel();
/*  310: 452 */     p1_1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Search Method"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  311:     */     
/*  312:     */ 
/*  313: 455 */     p1_1.setLayout(new BorderLayout());
/*  314: 456 */     p1_1.add(this.m_ASEPanel, "North");
/*  315:     */     
/*  316: 458 */     JPanel p_new = new JPanel();
/*  317: 459 */     p_new.setLayout(new BorderLayout());
/*  318: 460 */     p_new.add(p1, "North");
/*  319: 461 */     p_new.add(p1_1, "Center");
/*  320:     */     
/*  321: 463 */     JPanel p2 = new JPanel();
/*  322: 464 */     GridBagLayout gbL = new GridBagLayout();
/*  323: 465 */     p2.setLayout(gbL);
/*  324: 466 */     p2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Attribute Selection Mode"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  325:     */     
/*  326:     */ 
/*  327: 469 */     GridBagConstraints gbC = new GridBagConstraints();
/*  328: 470 */     gbC.anchor = 17;
/*  329: 471 */     gbC.gridy = 2;
/*  330: 472 */     gbC.gridx = 0;
/*  331: 473 */     gbL.setConstraints(this.m_TrainBut, gbC);
/*  332: 474 */     p2.add(this.m_TrainBut);
/*  333:     */     
/*  334: 476 */     gbC = new GridBagConstraints();
/*  335: 477 */     gbC.anchor = 17;
/*  336: 478 */     gbC.gridy = 4;
/*  337: 479 */     gbC.gridx = 0;
/*  338: 480 */     gbL.setConstraints(this.m_CVBut, gbC);
/*  339: 481 */     p2.add(this.m_CVBut);
/*  340:     */     
/*  341: 483 */     gbC = new GridBagConstraints();
/*  342: 484 */     gbC.anchor = 13;
/*  343: 485 */     gbC.fill = 2;
/*  344: 486 */     gbC.gridy = 4;
/*  345: 487 */     gbC.gridx = 1;
/*  346: 488 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  347: 489 */     gbL.setConstraints(this.m_CVLab, gbC);
/*  348: 490 */     p2.add(this.m_CVLab);
/*  349:     */     
/*  350: 492 */     gbC = new GridBagConstraints();
/*  351: 493 */     gbC.anchor = 13;
/*  352: 494 */     gbC.fill = 2;
/*  353: 495 */     gbC.gridy = 4;
/*  354: 496 */     gbC.gridx = 2;
/*  355: 497 */     gbC.weightx = 100.0D;
/*  356: 498 */     gbC.ipadx = 20;
/*  357: 499 */     gbL.setConstraints(this.m_CVText, gbC);
/*  358: 500 */     p2.add(this.m_CVText);
/*  359:     */     
/*  360: 502 */     gbC = new GridBagConstraints();
/*  361: 503 */     gbC.anchor = 13;
/*  362: 504 */     gbC.fill = 2;
/*  363: 505 */     gbC.gridy = 6;
/*  364: 506 */     gbC.gridx = 1;
/*  365: 507 */     gbC.insets = new Insets(2, 10, 2, 10);
/*  366: 508 */     gbL.setConstraints(this.m_SeedLab, gbC);
/*  367: 509 */     p2.add(this.m_SeedLab);
/*  368:     */     
/*  369: 511 */     gbC = new GridBagConstraints();
/*  370: 512 */     gbC.anchor = 13;
/*  371: 513 */     gbC.fill = 2;
/*  372: 514 */     gbC.gridy = 6;
/*  373: 515 */     gbC.gridx = 2;
/*  374: 516 */     gbC.weightx = 100.0D;
/*  375: 517 */     gbC.ipadx = 20;
/*  376: 518 */     gbL.setConstraints(this.m_SeedText, gbC);
/*  377: 519 */     p2.add(this.m_SeedText);
/*  378:     */     
/*  379: 521 */     JPanel buttons = new JPanel();
/*  380: 522 */     buttons.setLayout(new GridLayout(2, 2));
/*  381: 523 */     buttons.add(this.m_ClassCombo);
/*  382: 524 */     this.m_ClassCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  383: 525 */     JPanel ssButs = new JPanel();
/*  384: 526 */     ssButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  385: 527 */     ssButs.setLayout(new GridLayout(1, 2, 5, 5));
/*  386: 528 */     ssButs.add(this.m_StartBut);
/*  387: 529 */     ssButs.add(this.m_StopBut);
/*  388: 530 */     buttons.add(ssButs);
/*  389:     */     
/*  390: 532 */     JPanel p3 = new JPanel();
/*  391: 533 */     p3.setBorder(BorderFactory.createTitledBorder("Attribute selection output"));
/*  392: 534 */     p3.setLayout(new BorderLayout());
/*  393: 535 */     JScrollPane js = new JScrollPane(this.m_OutText);
/*  394: 536 */     p3.add(js, "Center");
/*  395: 537 */     js.getViewport().addChangeListener(new ChangeListener()
/*  396:     */     {
/*  397:     */       private int lastHeight;
/*  398:     */       
/*  399:     */       public void stateChanged(ChangeEvent e)
/*  400:     */       {
/*  401: 542 */         JViewport vp = (JViewport)e.getSource();
/*  402: 543 */         int h = vp.getViewSize().height;
/*  403: 544 */         if (h != this.lastHeight)
/*  404:     */         {
/*  405: 545 */           this.lastHeight = h;
/*  406: 546 */           int x = h - vp.getExtentSize().height;
/*  407: 547 */           vp.setViewPosition(new Point(0, x));
/*  408:     */         }
/*  409:     */       }
/*  410: 551 */     });
/*  411: 552 */     JPanel mondo = new JPanel();
/*  412: 553 */     gbL = new GridBagLayout();
/*  413: 554 */     mondo.setLayout(gbL);
/*  414: 555 */     gbC = new GridBagConstraints();
/*  415: 556 */     gbC.fill = 2;
/*  416: 557 */     gbC.gridy = 0;
/*  417: 558 */     gbC.gridx = 0;
/*  418: 559 */     gbC.weightx = 0.0D;
/*  419: 560 */     gbL.setConstraints(p2, gbC);
/*  420: 561 */     mondo.add(p2);
/*  421: 562 */     gbC = new GridBagConstraints();
/*  422: 563 */     gbC.anchor = 11;
/*  423: 564 */     gbC.fill = 2;
/*  424: 565 */     gbC.gridy = 1;
/*  425: 566 */     gbC.gridx = 0;
/*  426: 567 */     gbC.weightx = 0.0D;
/*  427: 568 */     gbL.setConstraints(buttons, gbC);
/*  428: 569 */     mondo.add(buttons);
/*  429: 570 */     gbC = new GridBagConstraints();
/*  430: 571 */     gbC.fill = 1;
/*  431: 572 */     gbC.gridy = 2;
/*  432: 573 */     gbC.gridx = 0;
/*  433: 574 */     gbC.weightx = 0.0D;
/*  434: 575 */     gbC.weighty = 100.0D;
/*  435: 576 */     gbL.setConstraints(historyHolder, gbC);
/*  436: 577 */     mondo.add(historyHolder);
/*  437: 578 */     gbC = new GridBagConstraints();
/*  438: 579 */     gbC.fill = 1;
/*  439: 580 */     gbC.gridy = 0;
/*  440: 581 */     gbC.gridx = 1;
/*  441: 582 */     gbC.gridheight = 3;
/*  442: 583 */     gbC.weightx = 100.0D;
/*  443: 584 */     gbC.weighty = 100.0D;
/*  444: 585 */     gbL.setConstraints(p3, gbC);
/*  445: 586 */     mondo.add(p3);
/*  446:     */     
/*  447: 588 */     setLayout(new BorderLayout());
/*  448: 589 */     add(p_new, "North");
/*  449: 590 */     add(mondo, "Center");
/*  450:     */   }
/*  451:     */   
/*  452:     */   protected void updateRadioLinks()
/*  453:     */   {
/*  454: 597 */     this.m_CVBut.setEnabled(true);
/*  455: 598 */     this.m_CVText.setEnabled(this.m_CVBut.isSelected());
/*  456: 599 */     this.m_CVLab.setEnabled(this.m_CVBut.isSelected());
/*  457: 600 */     this.m_SeedText.setEnabled(this.m_CVBut.isSelected());
/*  458: 601 */     this.m_SeedLab.setEnabled(this.m_CVBut.isSelected());
/*  459: 603 */     if ((this.m_AttributeEvaluatorEditor.getValue() instanceof AttributeTransformer))
/*  460:     */     {
/*  461: 604 */       this.m_CVBut.setSelected(false);
/*  462: 605 */       this.m_CVBut.setEnabled(false);
/*  463: 606 */       this.m_CVText.setEnabled(false);
/*  464: 607 */       this.m_CVLab.setEnabled(false);
/*  465: 608 */       this.m_SeedText.setEnabled(false);
/*  466: 609 */       this.m_SeedLab.setEnabled(false);
/*  467: 610 */       this.m_TrainBut.setSelected(true);
/*  468:     */     }
/*  469:     */   }
/*  470:     */   
/*  471:     */   public void setLog(Logger newLog)
/*  472:     */   {
/*  473: 622 */     this.m_Log = newLog;
/*  474:     */   }
/*  475:     */   
/*  476:     */   public void setInstances(Instances inst)
/*  477:     */   {
/*  478: 633 */     this.m_Instances = inst;
/*  479: 634 */     String[] attribNames = new String[this.m_Instances.numAttributes() + 1];
/*  480: 635 */     attribNames[0] = "No class";
/*  481: 636 */     for (int i = 0; i < inst.numAttributes(); i++)
/*  482:     */     {
/*  483: 637 */       String type = "(" + Attribute.typeToStringShort(this.m_Instances.attribute(i)) + ") ";
/*  484:     */       
/*  485: 639 */       String attnm = this.m_Instances.attribute(i).name();
/*  486: 640 */       attribNames[(i + 1)] = (type + attnm);
/*  487:     */     }
/*  488: 642 */     this.m_StartBut.setEnabled(this.m_RunThread == null);
/*  489: 643 */     this.m_StopBut.setEnabled(this.m_RunThread != null);
/*  490: 644 */     this.m_ClassCombo.setModel(new DefaultComboBoxModel(attribNames));
/*  491: 645 */     if (inst.classIndex() == -1) {
/*  492: 646 */       this.m_ClassCombo.setSelectedIndex(attribNames.length - 1);
/*  493:     */     } else {
/*  494: 648 */       this.m_ClassCombo.setSelectedIndex(inst.classIndex());
/*  495:     */     }
/*  496: 650 */     this.m_ClassCombo.setEnabled(true);
/*  497:     */   }
/*  498:     */   
/*  499:     */   protected void startAttributeSelection()
/*  500:     */   {
/*  501: 661 */     if (this.m_RunThread == null)
/*  502:     */     {
/*  503: 662 */       this.m_StartBut.setEnabled(false);
/*  504: 663 */       this.m_StopBut.setEnabled(true);
/*  505: 664 */       this.m_RunThread = new Thread()
/*  506:     */       {
/*  507:     */         public void run()
/*  508:     */         {
/*  509: 667 */           AttributeSelectionPanel.this.m_AEEPanel.addToHistory();
/*  510: 668 */           AttributeSelectionPanel.this.m_ASEPanel.addToHistory();
/*  511:     */           
/*  512:     */ 
/*  513: 671 */           AttributeSelectionPanel.this.m_Log.statusMessage("Setting up...");
/*  514: 672 */           Instances inst = new Instances(AttributeSelectionPanel.this.m_Instances);
/*  515:     */           
/*  516: 674 */           int testMode = 0;
/*  517: 675 */           int numFolds = 10;
/*  518: 676 */           int seed = 1;
/*  519: 677 */           int classIndex = AttributeSelectionPanel.this.m_ClassCombo.getSelectedIndex() - 1;
/*  520: 678 */           ASEvaluation evaluator = (ASEvaluation)AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue();
/*  521:     */           
/*  522:     */ 
/*  523: 681 */           ASSearch search = (ASSearch)AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue();
/*  524:     */           
/*  525: 683 */           StringBuffer outBuff = new StringBuffer();
/*  526: 684 */           String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/*  527:     */           
/*  528: 686 */           String sname = search.getClass().getName();
/*  529: 687 */           if (sname.startsWith("weka.attributeSelection.")) {
/*  530: 688 */             name = name + sname.substring("weka.attributeSelection.".length());
/*  531:     */           } else {
/*  532: 690 */             name = name + sname;
/*  533:     */           }
/*  534: 692 */           String ename = evaluator.getClass().getName();
/*  535: 693 */           if (ename.startsWith("weka.attributeSelection.")) {
/*  536: 694 */             name = name + " + " + ename.substring("weka.attributeSelection.".length());
/*  537:     */           } else {
/*  538: 697 */             name = name + " + " + ename;
/*  539:     */           }
/*  540: 706 */           Vector<String> list = new Vector();
/*  541: 707 */           list.add("-s");
/*  542: 708 */           if ((search instanceof OptionHandler)) {
/*  543: 709 */             list.add(sname + " " + Utils.joinOptions(((OptionHandler)search).getOptions()));
/*  544:     */           } else {
/*  545: 712 */             list.add(sname);
/*  546:     */           }
/*  547: 714 */           if ((evaluator instanceof OptionHandler))
/*  548:     */           {
/*  549: 715 */             String[] opt = ((OptionHandler)evaluator).getOptions();
/*  550: 716 */             for (String element : opt) {
/*  551: 717 */               list.add(element);
/*  552:     */             }
/*  553:     */           }
/*  554: 720 */           String cmd = ename + " " + Utils.joinOptions((String[])list.toArray(new String[list.size()]));
/*  555:     */           
/*  556:     */ 
/*  557:     */ 
/*  558:     */ 
/*  559: 725 */           weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
/*  560:     */           
/*  561: 727 */           filter.setEvaluator((ASEvaluation)AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue());
/*  562:     */           
/*  563: 729 */           filter.setSearch((ASSearch)AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue());
/*  564: 730 */           String cmdFilter = filter.getClass().getName() + " " + Utils.joinOptions(filter.getOptions());
/*  565:     */           
/*  566:     */ 
/*  567:     */ 
/*  568:     */ 
/*  569: 735 */           AttributeSelectedClassifier cls = new AttributeSelectedClassifier();
/*  570:     */           
/*  571: 737 */           cls.setEvaluator((ASEvaluation)AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.getValue());
/*  572:     */           
/*  573: 739 */           cls.setSearch((ASSearch)AttributeSelectionPanel.this.m_AttributeSearchEditor.getValue());
/*  574: 740 */           String cmdClassifier = cls.getClass().getName() + " " + Utils.joinOptions(cls.getOptions());
/*  575:     */           
/*  576:     */ 
/*  577:     */ 
/*  578: 744 */           weka.attributeSelection.AttributeSelection eval = null;
/*  579:     */           try
/*  580:     */           {
/*  581: 747 */             if (AttributeSelectionPanel.this.m_CVBut.isSelected())
/*  582:     */             {
/*  583: 748 */               testMode = 1;
/*  584: 749 */               numFolds = Integer.parseInt(AttributeSelectionPanel.this.m_CVText.getText());
/*  585: 750 */               seed = Integer.parseInt(AttributeSelectionPanel.this.m_SeedText.getText());
/*  586: 751 */               if (numFolds <= 1) {
/*  587: 752 */                 throw new Exception("Number of folds must be greater than 1");
/*  588:     */               }
/*  589:     */             }
/*  590: 756 */             if (classIndex >= 0) {
/*  591: 757 */               inst.setClassIndex(classIndex);
/*  592:     */             }
/*  593: 761 */             AttributeSelectionPanel.this.m_Log.logMessage("Started " + ename);
/*  594: 762 */             AttributeSelectionPanel.this.m_Log.logMessage("Command: " + cmd);
/*  595: 763 */             AttributeSelectionPanel.this.m_Log.logMessage("Filter command: " + cmdFilter);
/*  596: 764 */             AttributeSelectionPanel.this.m_Log.logMessage("Meta-classifier command: " + cmdClassifier);
/*  597: 765 */             if ((AttributeSelectionPanel.this.m_Log instanceof TaskLogger)) {
/*  598: 766 */               ((TaskLogger)AttributeSelectionPanel.this.m_Log).taskStarted();
/*  599:     */             }
/*  600: 768 */             outBuff.append("=== Run information ===\n\n");
/*  601: 769 */             outBuff.append("Evaluator:    " + ename);
/*  602: 770 */             if ((evaluator instanceof OptionHandler))
/*  603:     */             {
/*  604: 771 */               String[] o = ((OptionHandler)evaluator).getOptions();
/*  605: 772 */               outBuff.append(" " + Utils.joinOptions(o));
/*  606:     */             }
/*  607: 774 */             outBuff.append("\nSearch:       " + sname);
/*  608: 775 */             if ((search instanceof OptionHandler))
/*  609:     */             {
/*  610: 776 */               String[] o = ((OptionHandler)search).getOptions();
/*  611: 777 */               outBuff.append(" " + Utils.joinOptions(o));
/*  612:     */             }
/*  613: 779 */             outBuff.append("\n");
/*  614: 780 */             outBuff.append("Relation:     " + inst.relationName() + '\n');
/*  615: 781 */             outBuff.append("Instances:    " + inst.numInstances() + '\n');
/*  616: 782 */             outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
/*  617: 783 */             if (inst.numAttributes() < 100) {
/*  618: 784 */               for (int i = 0; i < inst.numAttributes(); i++) {
/*  619: 785 */                 outBuff.append("              " + inst.attribute(i).name() + '\n');
/*  620:     */               }
/*  621:     */             } else {
/*  622: 789 */               outBuff.append("              [list of attributes omitted]\n");
/*  623:     */             }
/*  624: 791 */             outBuff.append("Evaluation mode:    ");
/*  625: 792 */             switch (testMode)
/*  626:     */             {
/*  627:     */             case 0: 
/*  628: 794 */               outBuff.append("evaluate on all training data\n");
/*  629: 795 */               break;
/*  630:     */             case 1: 
/*  631: 797 */               outBuff.append("" + numFolds + "-fold cross-validation\n");
/*  632:     */             }
/*  633: 800 */             outBuff.append("\n");
/*  634: 801 */             AttributeSelectionPanel.this.m_History.addResult(name, outBuff);
/*  635: 802 */             AttributeSelectionPanel.this.m_History.setSingle(name);
/*  636:     */             
/*  637:     */ 
/*  638: 805 */             AttributeSelectionPanel.this.m_Log.statusMessage("Doing feature selection...");
/*  639: 806 */             AttributeSelectionPanel.this.m_History.updateResult(name);
/*  640:     */             
/*  641: 808 */             eval = new weka.attributeSelection.AttributeSelection();
/*  642: 809 */             eval.setEvaluator(evaluator);
/*  643: 810 */             eval.setSearch(search);
/*  644: 811 */             eval.setFolds(numFolds);
/*  645: 812 */             eval.setSeed(seed);
/*  646: 813 */             if (testMode == 1) {
/*  647: 814 */               eval.setXval(true);
/*  648:     */             }
/*  649: 817 */             switch (testMode)
/*  650:     */             {
/*  651:     */             case 0: 
/*  652: 819 */               AttributeSelectionPanel.this.m_Log.statusMessage("Evaluating on training data...");
/*  653: 820 */               eval.SelectAttributes(inst);
/*  654: 821 */               break;
/*  655:     */             case 1: 
/*  656: 824 */               AttributeSelectionPanel.this.m_Log.statusMessage("Randomizing instances...");
/*  657: 825 */               Random random = new Random(seed);
/*  658: 826 */               inst.randomize(random);
/*  659: 827 */               if (inst.attribute(classIndex).isNominal())
/*  660:     */               {
/*  661: 828 */                 AttributeSelectionPanel.this.m_Log.statusMessage("Stratifying instances...");
/*  662: 829 */                 inst.stratify(numFolds);
/*  663:     */               }
/*  664: 831 */               for (int fold = 0; fold < numFolds; fold++)
/*  665:     */               {
/*  666: 832 */                 AttributeSelectionPanel.this.m_Log.statusMessage("Creating splits for fold " + (fold + 1) + "...");
/*  667:     */                 
/*  668: 834 */                 Instances train = inst.trainCV(numFolds, fold, random);
/*  669: 835 */                 AttributeSelectionPanel.this.m_Log.statusMessage("Selecting attributes using all but fold " + (fold + 1) + "...");
/*  670:     */                 
/*  671:     */ 
/*  672: 838 */                 eval.selectAttributesCVSplit(train);
/*  673:     */               }
/*  674: 840 */               break;
/*  675:     */             default: 
/*  676: 842 */               throw new Exception("Test mode not implemented");
/*  677:     */             }
/*  678: 845 */             if (testMode == 0) {
/*  679: 846 */               outBuff.append(eval.toResultsString());
/*  680:     */             } else {
/*  681: 848 */               outBuff.append(eval.CVResultsString());
/*  682:     */             }
/*  683: 851 */             outBuff.append("\n");
/*  684: 852 */             AttributeSelectionPanel.this.m_History.updateResult(name);
/*  685: 853 */             AttributeSelectionPanel.this.m_Log.logMessage("Finished " + ename + " " + sname);
/*  686: 854 */             AttributeSelectionPanel.this.m_Log.statusMessage("OK");
/*  687:     */           }
/*  688:     */           catch (Exception ex)
/*  689:     */           {
/*  690:     */             ArrayList<Object> vv;
/*  691:     */             Vector<Object> configHolder;
/*  692:     */             ASEvaluation eval_copy;
/*  693:     */             ASSearch search_copy;
/*  694:     */             Instances transformed;
/*  695:     */             Instances reducedInst;
/*  696: 856 */             AttributeSelectionPanel.this.m_Log.logMessage(ex.getMessage());
/*  697: 857 */             AttributeSelectionPanel.this.m_Log.statusMessage("See error log");
/*  698:     */           }
/*  699:     */           finally
/*  700:     */           {
/*  701:     */             ArrayList<Object> vv;
/*  702:     */             Vector<Object> configHolder;
/*  703:     */             ASEvaluation eval_copy;
/*  704:     */             ASSearch search_copy;
/*  705:     */             Instances transformed;
/*  706:     */             Instances reducedInst;
/*  707: 859 */             ArrayList<Object> vv = new ArrayList();
/*  708: 860 */             Vector<Object> configHolder = new Vector();
/*  709:     */             try
/*  710:     */             {
/*  711: 862 */               ASEvaluation eval_copy = (ASEvaluation)evaluator.getClass().newInstance();
/*  712: 863 */               if ((evaluator instanceof OptionHandler)) {
/*  713: 864 */                 ((OptionHandler)eval_copy).setOptions(((OptionHandler)evaluator).getOptions());
/*  714:     */               }
/*  715: 868 */               ASSearch search_copy = (ASSearch)search.getClass().newInstance();
/*  716: 869 */               if ((search instanceof OptionHandler)) {
/*  717: 870 */                 ((OptionHandler)search_copy).setOptions(((OptionHandler)search).getOptions());
/*  718:     */               }
/*  719: 873 */               configHolder.add(eval_copy);
/*  720: 874 */               configHolder.add(search_copy);
/*  721:     */             }
/*  722:     */             catch (Exception ex)
/*  723:     */             {
/*  724: 876 */               configHolder.add(evaluator);
/*  725: 877 */               configHolder.add(search);
/*  726:     */             }
/*  727: 879 */             vv.add(configHolder);
/*  728: 881 */             if ((evaluator instanceof AttributeTransformer)) {
/*  729:     */               try
/*  730:     */               {
/*  731: 883 */                 Instances transformed = ((AttributeTransformer)evaluator).transformedData(inst);
/*  732:     */                 
/*  733: 885 */                 transformed.setRelationName("AT: " + transformed.relationName());
/*  734:     */                 
/*  735:     */ 
/*  736: 888 */                 vv.add(transformed);
/*  737: 889 */                 AttributeSelectionPanel.this.m_History.addObject(name, vv);
/*  738:     */               }
/*  739:     */               catch (Exception ex)
/*  740:     */               {
/*  741: 891 */                 System.err.println(ex);
/*  742: 892 */                 ex.printStackTrace();
/*  743:     */               }
/*  744: 894 */             } else if (testMode == 0) {
/*  745:     */               try
/*  746:     */               {
/*  747: 896 */                 Instances reducedInst = eval.reduceDimensionality(inst);
/*  748: 897 */                 vv.add(reducedInst);
/*  749: 898 */                 AttributeSelectionPanel.this.m_History.addObject(name, vv);
/*  750:     */               }
/*  751:     */               catch (Exception ex)
/*  752:     */               {
/*  753: 900 */                 ex.printStackTrace();
/*  754:     */               }
/*  755:     */             }
/*  756: 903 */             if (isInterrupted())
/*  757:     */             {
/*  758: 904 */               AttributeSelectionPanel.this.m_Log.logMessage("Interrupted " + ename + " " + sname);
/*  759: 905 */               AttributeSelectionPanel.this.m_Log.statusMessage("See error log");
/*  760:     */             }
/*  761: 907 */             AttributeSelectionPanel.this.m_RunThread = null;
/*  762: 908 */             AttributeSelectionPanel.this.m_StartBut.setEnabled(true);
/*  763: 909 */             AttributeSelectionPanel.this.m_StopBut.setEnabled(false);
/*  764: 910 */             if ((AttributeSelectionPanel.this.m_Log instanceof TaskLogger)) {
/*  765: 911 */               ((TaskLogger)AttributeSelectionPanel.this.m_Log).taskFinished();
/*  766:     */             }
/*  767:     */           }
/*  768:     */         }
/*  769: 915 */       };
/*  770: 916 */       this.m_RunThread.setPriority(1);
/*  771: 917 */       this.m_RunThread.start();
/*  772:     */     }
/*  773:     */   }
/*  774:     */   
/*  775:     */   protected void stopAttributeSelection()
/*  776:     */   {
/*  777: 927 */     if (this.m_RunThread != null)
/*  778:     */     {
/*  779: 928 */       this.m_RunThread.interrupt();
/*  780:     */       
/*  781:     */ 
/*  782: 931 */       this.m_RunThread.stop();
/*  783:     */     }
/*  784:     */   }
/*  785:     */   
/*  786:     */   protected void saveBuffer(String name)
/*  787:     */   {
/*  788: 942 */     StringBuffer sb = this.m_History.getNamedBuffer(name);
/*  789: 943 */     if ((sb != null) && 
/*  790: 944 */       (this.m_SaveOut.save(sb))) {
/*  791: 945 */       this.m_Log.logMessage("Save succesful.");
/*  792:     */     }
/*  793:     */   }
/*  794:     */   
/*  795:     */   protected void visualizeTransformedData(Instances ti)
/*  796:     */   {
/*  797: 956 */     if (ti != null)
/*  798:     */     {
/*  799: 957 */       MatrixPanel mp = new MatrixPanel();
/*  800: 958 */       mp.setInstances(ti);
/*  801: 959 */       String plotName = ti.relationName();
/*  802: 960 */       final JFrame jf = new JFrame("Weka Attribute Selection Visualize: " + plotName);
/*  803:     */       
/*  804:     */ 
/*  805: 963 */       jf.setSize(800, 600);
/*  806: 964 */       jf.getContentPane().setLayout(new BorderLayout());
/*  807: 965 */       jf.getContentPane().add(mp, "Center");
/*  808: 966 */       jf.addWindowListener(new WindowAdapter()
/*  809:     */       {
/*  810:     */         public void windowClosing(WindowEvent e)
/*  811:     */         {
/*  812: 969 */           jf.dispose();
/*  813:     */         }
/*  814: 972 */       });
/*  815: 973 */       jf.setVisible(true);
/*  816:     */     }
/*  817:     */   }
/*  818:     */   
/*  819:     */   protected void saveTransformedData(Instances ti)
/*  820:     */   {
/*  821: 988 */     JFileChooser fc = new JFileChooser();
/*  822: 989 */     ExtensionFileFilter filter = new ExtensionFileFilter(".arff", "ARFF data files");
/*  823: 990 */     fc.setFileFilter(filter);
/*  824: 991 */     int retVal = fc.showSaveDialog(this);
/*  825: 993 */     if (retVal == 0) {
/*  826:     */       try
/*  827:     */       {
/*  828: 995 */         BufferedWriter writer = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
/*  829: 996 */         writer.write(ti.toString());
/*  830: 997 */         writer.flush();
/*  831: 998 */         writer.close();
/*  832:     */       }
/*  833:     */       catch (Exception e)
/*  834:     */       {
/*  835:1000 */         e.printStackTrace();
/*  836:1001 */         this.m_Log.logMessage("Problem saving data: " + e.getMessage());
/*  837:1002 */         JOptionPane.showMessageDialog(this, "Problem saving data:\n" + e.getMessage(), "Error", 0);
/*  838:     */       }
/*  839:     */     }
/*  840:     */   }
/*  841:     */   
/*  842:     */   protected void visualize(String name, int x, int y)
/*  843:     */   {
/*  844:1019 */     final String selectedName = name;
/*  845:1020 */     JPopupMenu resultListMenu = new JPopupMenu();
/*  846:     */     
/*  847:1022 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/*  848:1023 */     if (selectedName != null) {
/*  849:1024 */       visMainBuffer.addActionListener(new ActionListener()
/*  850:     */       {
/*  851:     */         public void actionPerformed(ActionEvent e)
/*  852:     */         {
/*  853:1027 */           AttributeSelectionPanel.this.m_History.setSingle(selectedName);
/*  854:     */         }
/*  855:     */       });
/*  856:     */     } else {
/*  857:1031 */       visMainBuffer.setEnabled(false);
/*  858:     */     }
/*  859:1033 */     resultListMenu.add(visMainBuffer);
/*  860:     */     
/*  861:1035 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/*  862:1036 */     if (selectedName != null) {
/*  863:1037 */       visSepBuffer.addActionListener(new ActionListener()
/*  864:     */       {
/*  865:     */         public void actionPerformed(ActionEvent e)
/*  866:     */         {
/*  867:1040 */           AttributeSelectionPanel.this.m_History.openFrame(selectedName);
/*  868:     */         }
/*  869:     */       });
/*  870:     */     } else {
/*  871:1044 */       visSepBuffer.setEnabled(false);
/*  872:     */     }
/*  873:1046 */     resultListMenu.add(visSepBuffer);
/*  874:     */     
/*  875:1048 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/*  876:1049 */     if (selectedName != null) {
/*  877:1050 */       saveOutput.addActionListener(new ActionListener()
/*  878:     */       {
/*  879:     */         public void actionPerformed(ActionEvent e)
/*  880:     */         {
/*  881:1053 */           AttributeSelectionPanel.this.saveBuffer(selectedName);
/*  882:     */         }
/*  883:     */       });
/*  884:     */     } else {
/*  885:1057 */       saveOutput.setEnabled(false);
/*  886:     */     }
/*  887:1059 */     resultListMenu.add(saveOutput);
/*  888:     */     
/*  889:1061 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/*  890:1062 */     if (selectedName != null) {
/*  891:1063 */       deleteOutput.addActionListener(new ActionListener()
/*  892:     */       {
/*  893:     */         public void actionPerformed(ActionEvent e)
/*  894:     */         {
/*  895:1066 */           AttributeSelectionPanel.this.m_History.removeResult(selectedName);
/*  896:     */         }
/*  897:     */       });
/*  898:     */     } else {
/*  899:1070 */       deleteOutput.setEnabled(false);
/*  900:     */     }
/*  901:1072 */     resultListMenu.add(deleteOutput);
/*  902:     */     
/*  903:1074 */     ArrayList<Object> o = null;
/*  904:1075 */     if (selectedName != null) {
/*  905:1076 */       o = (ArrayList)this.m_History.getNamedObject(selectedName);
/*  906:     */     }
/*  907:1080 */     Instances tempTransformed = null;
/*  908:1081 */     ASEvaluation temp_eval = null;
/*  909:1082 */     ASSearch temp_search = null;
/*  910:1084 */     if (o != null) {
/*  911:1085 */       for (int i = 0; i < o.size(); i++)
/*  912:     */       {
/*  913:1086 */         Object temp = o.get(i);
/*  914:1088 */         if ((temp instanceof Instances)) {
/*  915:1090 */           tempTransformed = (Instances)temp;
/*  916:     */         }
/*  917:1092 */         if ((temp instanceof Vector))
/*  918:     */         {
/*  919:1093 */           temp_eval = (ASEvaluation)((Vector)temp).get(0);
/*  920:1094 */           temp_search = (ASSearch)((Vector)temp).get(1);
/*  921:     */         }
/*  922:     */       }
/*  923:     */     }
/*  924:1099 */     final ASEvaluation eval = temp_eval;
/*  925:1100 */     final ASSearch search = temp_search;
/*  926:     */     
/*  927:     */ 
/*  928:1103 */     final Instances ti = tempTransformed;
/*  929:1104 */     JMenuItem visTrans = null;
/*  930:1106 */     if (ti != null)
/*  931:     */     {
/*  932:1107 */       if (ti.relationName().startsWith("AT:")) {
/*  933:1108 */         visTrans = new JMenuItem("Visualize transformed data");
/*  934:     */       } else {
/*  935:1110 */         visTrans = new JMenuItem("Visualize reduced data");
/*  936:     */       }
/*  937:1112 */       resultListMenu.addSeparator();
/*  938:     */     }
/*  939:1116 */     if ((ti != null) && (visTrans != null)) {
/*  940:1117 */       visTrans.addActionListener(new ActionListener()
/*  941:     */       {
/*  942:     */         public void actionPerformed(ActionEvent e)
/*  943:     */         {
/*  944:1120 */           AttributeSelectionPanel.this.visualizeTransformedData(ti);
/*  945:     */         }
/*  946:     */       });
/*  947:     */     }
/*  948:1125 */     if (visTrans != null) {
/*  949:1126 */       resultListMenu.add(visTrans);
/*  950:     */     }
/*  951:1129 */     JMenuItem saveTrans = null;
/*  952:1130 */     if (ti != null) {
/*  953:1131 */       if (ti.relationName().startsWith("AT:")) {
/*  954:1132 */         saveTrans = new JMenuItem("Save transformed data...");
/*  955:     */       } else {
/*  956:1134 */         saveTrans = new JMenuItem("Save reduced data...");
/*  957:     */       }
/*  958:     */     }
/*  959:1137 */     if (saveTrans != null)
/*  960:     */     {
/*  961:1138 */       saveTrans.addActionListener(new ActionListener()
/*  962:     */       {
/*  963:     */         public void actionPerformed(ActionEvent e)
/*  964:     */         {
/*  965:1141 */           AttributeSelectionPanel.this.saveTransformedData(ti);
/*  966:     */         }
/*  967:1143 */       });
/*  968:1144 */       resultListMenu.add(saveTrans);
/*  969:     */     }
/*  970:1147 */     JMenuItem reApplyConfig = new JMenuItem("Re-apply attribute selection configuration");
/*  971:1149 */     if ((eval != null) && (search != null)) {
/*  972:1150 */       reApplyConfig.addActionListener(new ActionListener()
/*  973:     */       {
/*  974:     */         public void actionPerformed(ActionEvent e)
/*  975:     */         {
/*  976:1153 */           AttributeSelectionPanel.this.m_AttributeEvaluatorEditor.setValue(eval);
/*  977:1154 */           AttributeSelectionPanel.this.m_AttributeSearchEditor.setValue(search);
/*  978:     */         }
/*  979:     */       });
/*  980:     */     } else {
/*  981:1158 */       reApplyConfig.setEnabled(false);
/*  982:     */     }
/*  983:1160 */     resultListMenu.add(reApplyConfig);
/*  984:     */     
/*  985:1162 */     resultListMenu.show(this.m_History.getList(), x, y);
/*  986:     */   }
/*  987:     */   
/*  988:     */   protected void updateCapabilitiesFilter(Capabilities filter)
/*  989:     */   {
/*  990:1174 */     if (filter == null)
/*  991:     */     {
/*  992:1175 */       this.m_AttributeEvaluatorEditor.setCapabilitiesFilter(new Capabilities(null));
/*  993:1176 */       this.m_AttributeSearchEditor.setCapabilitiesFilter(new Capabilities(null)); return;
/*  994:     */     }
/*  995:     */     Instances tempInst;
/*  996:     */     Instances tempInst;
/*  997:1180 */     if (!ExplorerDefaults.getInitGenericObjectEditorFilter()) {
/*  998:1181 */       tempInst = new Instances(this.m_Instances, 0);
/*  999:     */     } else {
/* 1000:1183 */       tempInst = new Instances(this.m_Instances);
/* 1001:     */     }
/* 1002:1185 */     int clIndex = this.m_ClassCombo.getSelectedIndex() - 1;
/* 1003:1187 */     if (clIndex >= 0) {
/* 1004:1188 */       tempInst.setClassIndex(clIndex);
/* 1005:     */     }
/* 1006:     */     Capabilities filterClass;
/* 1007:     */     try
/* 1008:     */     {
/* 1009:1192 */       filterClass = Capabilities.forInstances(tempInst);
/* 1010:     */     }
/* 1011:     */     catch (Exception e)
/* 1012:     */     {
/* 1013:1194 */       filterClass = new Capabilities(null);
/* 1014:     */     }
/* 1015:1198 */     this.m_AttributeEvaluatorEditor.setCapabilitiesFilter(filterClass);
/* 1016:1199 */     this.m_AttributeSearchEditor.setCapabilitiesFilter(filterClass);
/* 1017:     */     
/* 1018:1201 */     this.m_StartBut.setEnabled(true);
/* 1019:     */     
/* 1020:1203 */     Capabilities currentFilter = this.m_AttributeEvaluatorEditor.getCapabilitiesFilter();
/* 1021:     */     
/* 1022:1205 */     ASEvaluation evaluator = (ASEvaluation)this.m_AttributeEvaluatorEditor.getValue();
/* 1023:     */     
/* 1024:1207 */     Capabilities currentSchemeCapabilities = null;
/* 1025:1208 */     if ((evaluator != null) && (currentFilter != null) && ((evaluator instanceof CapabilitiesHandler)))
/* 1026:     */     {
/* 1027:1210 */       currentSchemeCapabilities = evaluator.getCapabilities();
/* 1028:1213 */       if ((!currentSchemeCapabilities.supportsMaybe(currentFilter)) && (!currentSchemeCapabilities.supports(currentFilter))) {
/* 1029:1215 */         this.m_StartBut.setEnabled(false);
/* 1030:     */       }
/* 1031:     */     }
/* 1032:     */   }
/* 1033:     */   
/* 1034:     */   public void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent e)
/* 1035:     */   {
/* 1036:1227 */     if (e.getFilter() == null) {
/* 1037:1228 */       updateCapabilitiesFilter(null);
/* 1038:     */     } else {
/* 1039:1230 */       updateCapabilitiesFilter((Capabilities)e.getFilter().clone());
/* 1040:     */     }
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public void setExplorer(Explorer parent)
/* 1044:     */   {
/* 1045:1242 */     this.m_Explorer = parent;
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   public Explorer getExplorer()
/* 1049:     */   {
/* 1050:1252 */     return this.m_Explorer;
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public String getTabTitle()
/* 1054:     */   {
/* 1055:1262 */     return "Select attributes";
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public String getTabTitleToolTip()
/* 1059:     */   {
/* 1060:1272 */     return "Determine relevance of attributes";
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public boolean requiresLog()
/* 1064:     */   {
/* 1065:1277 */     return true;
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public boolean acceptsInstances()
/* 1069:     */   {
/* 1070:1282 */     return true;
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public Defaults getDefaultSettings()
/* 1074:     */   {
/* 1075:1287 */     return new AttributeSelectionPanelDefaults();
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public boolean okToBeActive()
/* 1079:     */   {
/* 1080:1292 */     return this.m_Instances != null;
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public void setActive(boolean active)
/* 1084:     */   {
/* 1085:1297 */     super.setActive(active);
/* 1086:1299 */     if (this.m_isActive) {
/* 1087:1301 */       settingsChanged();
/* 1088:     */     }
/* 1089:     */   }
/* 1090:     */   
/* 1091:     */   public void settingsChanged()
/* 1092:     */   {
/* 1093:1307 */     if (getMainApplication() != null)
/* 1094:     */     {
/* 1095:1308 */       if (!this.m_initialSettingsSet)
/* 1096:     */       {
/* 1097:1309 */         Object initialEval = getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.EVALUATOR_KEY, AttributeSelectionPanelDefaults.EVALUATOR, Environment.getSystemWide());
/* 1098:     */         
/* 1099:     */ 
/* 1100:     */ 
/* 1101:     */ 
/* 1102:1314 */         this.m_AttributeEvaluatorEditor.setValue(initialEval);
/* 1103:     */         
/* 1104:1316 */         Object initialSearch = getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.SEARCH_KEY, AttributeSelectionPanelDefaults.SEARCH, Environment.getSystemWide());
/* 1105:     */         
/* 1106:     */ 
/* 1107:     */ 
/* 1108:     */ 
/* 1109:     */ 
/* 1110:1322 */         this.m_AttributeSearchEditor.setValue(initialSearch);
/* 1111:     */         
/* 1112:1324 */         TestMode initialEvalMode = (TestMode)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.EVAL_MODE_KEY, AttributeSelectionPanelDefaults.EVAL_MODE, Environment.getSystemWide());
/* 1113:     */         
/* 1114:     */ 
/* 1115:     */ 
/* 1116:     */ 
/* 1117:1329 */         this.m_TrainBut.setSelected(initialEvalMode == TestMode.TRAINING_SET);
/* 1118:1330 */         this.m_CVBut.setSelected(initialEvalMode == TestMode.CROSS_VALIDATION);
/* 1119:     */         
/* 1120:1332 */         int folds = ((Integer)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.FOLDS_KEY, AttributeSelectionPanelDefaults.FOLDS, Environment.getSystemWide())).intValue();
/* 1121:     */         
/* 1122:     */ 
/* 1123:     */ 
/* 1124:1336 */         this.m_CVText.setText("" + folds);
/* 1125:     */         
/* 1126:1338 */         int seed = ((Integer)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.SEED_KEY, AttributeSelectionPanelDefaults.SEED, Environment.getSystemWide())).intValue();
/* 1127:     */         
/* 1128:     */ 
/* 1129:     */ 
/* 1130:1342 */         this.m_SeedText.setText("" + seed);
/* 1131:     */         
/* 1132:1344 */         updateRadioLinks();
/* 1133:1345 */         this.m_initialSettingsSet = true;
/* 1134:     */       }
/* 1135:1348 */       Font outputFont = (Font)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.OUTPUT_FONT_KEY, AttributeSelectionPanelDefaults.OUTPUT_FONT, Environment.getSystemWide());
/* 1136:     */       
/* 1137:     */ 
/* 1138:     */ 
/* 1139:     */ 
/* 1140:1353 */       this.m_OutText.setFont(outputFont);
/* 1141:1354 */       this.m_History.setFont(outputFont);
/* 1142:1355 */       Color textColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.OUTPUT_TEXT_COLOR_KEY, AttributeSelectionPanelDefaults.OUTPUT_TEXT_COLOR, Environment.getSystemWide());
/* 1143:     */       
/* 1144:     */ 
/* 1145:     */ 
/* 1146:     */ 
/* 1147:     */ 
/* 1148:1361 */       this.m_OutText.setForeground(textColor);
/* 1149:1362 */       this.m_History.setForeground(textColor);
/* 1150:1363 */       Color outputBackgroundColor = (Color)getMainApplication().getApplicationSettings().getSetting(getPerspectiveID(), AttributeSelectionPanelDefaults.OUTPUT_BACKGROUND_COLOR_KEY, AttributeSelectionPanelDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide());
/* 1151:     */       
/* 1152:     */ 
/* 1153:     */ 
/* 1154:     */ 
/* 1155:     */ 
/* 1156:1369 */       this.m_OutText.setBackground(outputBackgroundColor);
/* 1157:1370 */       this.m_History.setBackground(outputBackgroundColor);
/* 1158:     */     }
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   public static enum TestMode
/* 1162:     */   {
/* 1163:1375 */     TRAINING_SET,  CROSS_VALIDATION;
/* 1164:     */     
/* 1165:     */     private TestMode() {}
/* 1166:     */   }
/* 1167:     */   
/* 1168:     */   protected static final class AttributeSelectionPanelDefaults
/* 1169:     */     extends Defaults
/* 1170:     */   {
/* 1171:     */     public static final String ID = "weka.gui.explorer.attributeselectionpanel";
/* 1172:1382 */     protected static final Settings.SettingKey EVALUATOR_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.initialEvaluator", "Initial evaluator", "On startup, set this evaluator as the default one");
/* 1173:1385 */     protected static final ASEvaluation EVALUATOR = new CfsSubsetEval();
/* 1174:1387 */     protected static final Settings.SettingKey SEARCH_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.initialSearch", "Initial search method", "On startup, set this search method as the default one");
/* 1175:1390 */     protected static final ASSearch SEARCH = new BestFirst();
/* 1176:1392 */     protected static final Settings.SettingKey EVAL_MODE_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.initialEvalMode", "Default evaluation mode", "");
/* 1177:1395 */     protected static final AttributeSelectionPanel.TestMode EVAL_MODE = AttributeSelectionPanel.TestMode.TRAINING_SET;
/* 1178:1397 */     protected static final Settings.SettingKey FOLDS_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.xvalFolds", "Default cross-validation folds", "");
/* 1179:1400 */     protected static final Integer FOLDS = Integer.valueOf(10);
/* 1180:1402 */     protected static final Settings.SettingKey SEED_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.seed", "Random seed", "");
/* 1181:1404 */     protected static final Integer SEED = Integer.valueOf(1);
/* 1182:1406 */     protected static final Settings.SettingKey OUTPUT_FONT_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.outputFont", "Font for text output", "Font to use in the output area");
/* 1183:1409 */     protected static final Font OUTPUT_FONT = new Font("Monospaced", 0, 12);
/* 1184:1412 */     protected static final Settings.SettingKey OUTPUT_TEXT_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.outputFontColor", "Output text color", "Color of output text");
/* 1185:1415 */     protected static final Color OUTPUT_TEXT_COLOR = Color.black;
/* 1186:1417 */     protected static final Settings.SettingKey OUTPUT_BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.explorer.attributeselectionpanel.outputBackgroundColor", "Output background color", "Output background color");
/* 1187:1420 */     protected static final Color OUTPUT_BACKGROUND_COLOR = Color.white;
/* 1188:     */     private static final long serialVersionUID = -5413933415469545770L;
/* 1189:     */     
/* 1190:     */     public AttributeSelectionPanelDefaults()
/* 1191:     */     {
/* 1192:1424 */       super();
/* 1193:1425 */       this.m_defaults.put(EVALUATOR_KEY, EVALUATOR);
/* 1194:1426 */       this.m_defaults.put(SEARCH_KEY, SEARCH);
/* 1195:1427 */       this.m_defaults.put(EVAL_MODE_KEY, EVAL_MODE);
/* 1196:1428 */       this.m_defaults.put(FOLDS_KEY, FOLDS);
/* 1197:1429 */       this.m_defaults.put(SEED_KEY, SEED);
/* 1198:1430 */       this.m_defaults.put(OUTPUT_FONT_KEY, OUTPUT_FONT);
/* 1199:1431 */       this.m_defaults.put(OUTPUT_TEXT_COLOR_KEY, OUTPUT_TEXT_COLOR);
/* 1200:1432 */       this.m_defaults.put(OUTPUT_BACKGROUND_COLOR_KEY, OUTPUT_BACKGROUND_COLOR);
/* 1201:     */     }
/* 1202:     */   }
/* 1203:     */   
/* 1204:     */   public static void main(String[] args)
/* 1205:     */   {
/* 1206:     */     try
/* 1207:     */     {
/* 1208:1444 */       JFrame jf = new JFrame("Weka Explorer: Select attributes");
/* 1209:     */       
/* 1210:1446 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1211:1447 */       AttributeSelectionPanel sp = new AttributeSelectionPanel();
/* 1212:1448 */       jf.getContentPane().add(sp, "Center");
/* 1213:1449 */       LogPanel lp = new LogPanel();
/* 1214:1450 */       sp.setLog(lp);
/* 1215:1451 */       jf.getContentPane().add(lp, "South");
/* 1216:1452 */       jf.addWindowListener(new WindowAdapter()
/* 1217:     */       {
/* 1218:     */         public void windowClosing(WindowEvent e)
/* 1219:     */         {
/* 1220:1455 */           this.val$jf.dispose();
/* 1221:1456 */           System.exit(0);
/* 1222:     */         }
/* 1223:1458 */       });
/* 1224:1459 */       jf.pack();
/* 1225:1460 */       jf.setVisible(true);
/* 1226:1461 */       if (args.length == 1)
/* 1227:     */       {
/* 1228:1462 */         System.err.println("Loading instances from " + args[0]);
/* 1229:1463 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 1230:     */         
/* 1231:1465 */         Instances i = new Instances(r);
/* 1232:1466 */         sp.setInstances(i);
/* 1233:     */       }
/* 1234:     */     }
/* 1235:     */     catch (Exception ex)
/* 1236:     */     {
/* 1237:1469 */       ex.printStackTrace();
/* 1238:1470 */       System.err.println(ex.getMessage());
/* 1239:     */     }
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   static {}
/* 1243:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.AttributeSelectionPanel
 * JD-Core Version:    0.7.0.1
 */