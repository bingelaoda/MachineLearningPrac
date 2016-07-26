/*    1:     */ package weka.gui.boundaryvisualizer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.FontMetrics;
/*    9:     */ import java.awt.Graphics;
/*   10:     */ import java.awt.GridBagConstraints;
/*   11:     */ import java.awt.GridBagLayout;
/*   12:     */ import java.awt.GridLayout;
/*   13:     */ import java.awt.Window;
/*   14:     */ import java.awt.event.ActionEvent;
/*   15:     */ import java.awt.event.ActionListener;
/*   16:     */ import java.awt.event.ItemEvent;
/*   17:     */ import java.awt.event.ItemListener;
/*   18:     */ import java.awt.event.MouseAdapter;
/*   19:     */ import java.awt.event.MouseEvent;
/*   20:     */ import java.awt.event.WindowAdapter;
/*   21:     */ import java.awt.event.WindowEvent;
/*   22:     */ import java.beans.PropertyChangeEvent;
/*   23:     */ import java.beans.PropertyChangeListener;
/*   24:     */ import java.io.BufferedReader;
/*   25:     */ import java.io.File;
/*   26:     */ import java.io.FileOutputStream;
/*   27:     */ import java.io.FileReader;
/*   28:     */ import java.io.ObjectOutputStream;
/*   29:     */ import java.io.PrintStream;
/*   30:     */ import java.io.Reader;
/*   31:     */ import java.util.ArrayList;
/*   32:     */ import java.util.Vector;
/*   33:     */ import javax.swing.BorderFactory;
/*   34:     */ import javax.swing.BoxLayout;
/*   35:     */ import javax.swing.ButtonGroup;
/*   36:     */ import javax.swing.DefaultComboBoxModel;
/*   37:     */ import javax.swing.JButton;
/*   38:     */ import javax.swing.JCheckBox;
/*   39:     */ import javax.swing.JComboBox;
/*   40:     */ import javax.swing.JFileChooser;
/*   41:     */ import javax.swing.JFrame;
/*   42:     */ import javax.swing.JLabel;
/*   43:     */ import javax.swing.JOptionPane;
/*   44:     */ import javax.swing.JPanel;
/*   45:     */ import javax.swing.JRadioButton;
/*   46:     */ import javax.swing.JTextField;
/*   47:     */ import weka.classifiers.AbstractClassifier;
/*   48:     */ import weka.classifiers.Classifier;
/*   49:     */ import weka.core.Attribute;
/*   50:     */ import weka.core.Instances;
/*   51:     */ import weka.core.TechnicalInformation;
/*   52:     */ import weka.core.TechnicalInformation.Field;
/*   53:     */ import weka.core.TechnicalInformation.Type;
/*   54:     */ import weka.core.TechnicalInformationHandler;
/*   55:     */ import weka.core.Utils;
/*   56:     */ import weka.core.logging.Logger;
/*   57:     */ import weka.core.logging.Logger.Level;
/*   58:     */ import weka.gui.ExtensionFileFilter;
/*   59:     */ import weka.gui.GenericObjectEditor;
/*   60:     */ import weka.gui.PropertyPanel;
/*   61:     */ import weka.gui.PropertySheetPanel;
/*   62:     */ import weka.gui.visualize.ClassPanel;
/*   63:     */ 
/*   64:     */ public class BoundaryVisualizer
/*   65:     */   extends JPanel
/*   66:     */   implements TechnicalInformationHandler
/*   67:     */ {
/*   68:     */   private static final long serialVersionUID = 3933877580074013208L;
/*   69:     */   
/*   70:     */   private class AxisPanel
/*   71:     */     extends JPanel
/*   72:     */   {
/*   73:     */     private static final long serialVersionUID = -7421022416674492712L;
/*   74:     */     private static final int MAX_PRECISION = 10;
/*   75: 120 */     private boolean m_vertical = false;
/*   76: 121 */     private final int PAD = 5;
/*   77:     */     private FontMetrics m_fontMetrics;
/*   78:     */     private int m_fontHeight;
/*   79:     */     
/*   80:     */     public AxisPanel(boolean vertical)
/*   81:     */     {
/*   82: 126 */       this.m_vertical = vertical;
/*   83: 127 */       setBackground(Color.black);
/*   84:     */       
/*   85: 129 */       String fontFamily = getFont().getFamily();
/*   86: 130 */       Font newFont = new Font(fontFamily, 0, 10);
/*   87: 131 */       setFont(newFont);
/*   88:     */     }
/*   89:     */     
/*   90:     */     public Dimension getPreferredSize()
/*   91:     */     {
/*   92: 136 */       if (this.m_fontMetrics == null)
/*   93:     */       {
/*   94: 137 */         Graphics g = getGraphics();
/*   95: 138 */         this.m_fontMetrics = g.getFontMetrics();
/*   96: 139 */         this.m_fontHeight = this.m_fontMetrics.getHeight();
/*   97:     */       }
/*   98: 141 */       if (!this.m_vertical) {
/*   99: 142 */         return new Dimension(getSize().width, 7 + this.m_fontHeight);
/*  100:     */       }
/*  101: 144 */       return new Dimension(50, getSize().height);
/*  102:     */     }
/*  103:     */     
/*  104:     */     public void paintComponent(Graphics g)
/*  105:     */     {
/*  106: 149 */       super.paintComponent(g);
/*  107: 150 */       setBackground(Color.black);
/*  108: 151 */       if (this.m_fontMetrics == null)
/*  109:     */       {
/*  110: 152 */         this.m_fontMetrics = g.getFontMetrics();
/*  111: 153 */         this.m_fontHeight = this.m_fontMetrics.getHeight();
/*  112:     */       }
/*  113: 156 */       Dimension d = getSize();
/*  114: 157 */       Dimension d2 = BoundaryVisualizer.this.m_boundaryPanel.getSize();
/*  115: 158 */       g.setColor(Color.gray);
/*  116: 159 */       int hf = this.m_fontMetrics.getAscent();
/*  117: 160 */       if (!this.m_vertical)
/*  118:     */       {
/*  119: 161 */         g.drawLine(d.width, 5, d.width - d2.width, 5);
/*  120: 163 */         if (BoundaryVisualizer.this.getInstances() != null)
/*  121:     */         {
/*  122: 164 */           int precisionXmax = 1;
/*  123: 165 */           int precisionXmin = 1;
/*  124: 166 */           int whole = (int)Math.abs(BoundaryVisualizer.this.m_maxX);
/*  125: 167 */           double decimal = Math.abs(BoundaryVisualizer.this.m_maxX) - whole;
/*  126:     */           
/*  127: 169 */           int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  128:     */           
/*  129: 171 */           precisionXmax = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(BoundaryVisualizer.this.m_maxX)) / Math.log(10.0D)) + 2 : 1;
/*  130: 173 */           if (precisionXmax > 10) {
/*  131: 174 */             precisionXmax = 1;
/*  132:     */           }
/*  133: 176 */           String maxStringX = Utils.doubleToString(BoundaryVisualizer.this.m_maxX, nondecimal + 1 + precisionXmax, precisionXmax);
/*  134:     */           
/*  135:     */ 
/*  136: 179 */           whole = (int)Math.abs(BoundaryVisualizer.this.m_minX);
/*  137: 180 */           decimal = Math.abs(BoundaryVisualizer.this.m_minX) - whole;
/*  138: 181 */           nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  139: 182 */           precisionXmin = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(BoundaryVisualizer.this.m_minX)) / Math.log(10.0D)) + 2 : 1;
/*  140: 184 */           if (precisionXmin > 10) {
/*  141: 185 */             precisionXmin = 1;
/*  142:     */           }
/*  143: 188 */           String minStringX = Utils.doubleToString(BoundaryVisualizer.this.m_minX, nondecimal + 1 + precisionXmin, precisionXmin);
/*  144:     */           
/*  145: 190 */           g.drawString(minStringX, d.width - d2.width, 5 + hf + 2);
/*  146: 191 */           int maxWidth = this.m_fontMetrics.stringWidth(maxStringX);
/*  147: 192 */           g.drawString(maxStringX, d.width - maxWidth, 5 + hf + 2);
/*  148:     */         }
/*  149:     */       }
/*  150:     */       else
/*  151:     */       {
/*  152: 195 */         g.drawLine(d.width - 5, 0, d.width - 5, d2.height);
/*  153: 197 */         if (BoundaryVisualizer.this.getInstances() != null)
/*  154:     */         {
/*  155: 198 */           int precisionYmax = 1;
/*  156: 199 */           int precisionYmin = 1;
/*  157: 200 */           int whole = (int)Math.abs(BoundaryVisualizer.this.m_maxY);
/*  158: 201 */           double decimal = Math.abs(BoundaryVisualizer.this.m_maxY) - whole;
/*  159:     */           
/*  160: 203 */           int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  161:     */           
/*  162: 205 */           precisionYmax = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(BoundaryVisualizer.this.m_maxY)) / Math.log(10.0D)) + 2 : 1;
/*  163: 207 */           if (precisionYmax > 10) {
/*  164: 208 */             precisionYmax = 1;
/*  165:     */           }
/*  166: 210 */           String maxStringY = Utils.doubleToString(BoundaryVisualizer.this.m_maxY, nondecimal + 1 + precisionYmax, precisionYmax);
/*  167:     */           
/*  168:     */ 
/*  169: 213 */           whole = (int)Math.abs(BoundaryVisualizer.this.m_minY);
/*  170: 214 */           decimal = Math.abs(BoundaryVisualizer.this.m_minY) - whole;
/*  171: 215 */           nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  172: 216 */           precisionYmin = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(BoundaryVisualizer.this.m_minY)) / Math.log(10.0D)) + 2 : 1;
/*  173: 218 */           if (precisionYmin > 10) {
/*  174: 219 */             precisionYmin = 1;
/*  175:     */           }
/*  176: 222 */           String minStringY = Utils.doubleToString(BoundaryVisualizer.this.m_minY, nondecimal + 1 + precisionYmin, precisionYmin);
/*  177:     */           
/*  178: 224 */           int maxWidth = this.m_fontMetrics.stringWidth(minStringY);
/*  179: 225 */           g.drawString(minStringY, d.width - 5 - maxWidth - 2, d2.height);
/*  180: 226 */           maxWidth = this.m_fontMetrics.stringWidth(maxStringY);
/*  181: 227 */           g.drawString(maxStringY, d.width - 5 - maxWidth - 2, hf);
/*  182:     */         }
/*  183:     */       }
/*  184:     */     }
/*  185:     */   }
/*  186:     */   
/*  187: 234 */   protected static int m_WindowCount = 0;
/*  188: 237 */   protected static boolean m_ExitIfNoWindowsOpen = true;
/*  189:     */   private Instances m_trainingInstances;
/*  190:     */   private Classifier m_classifier;
/*  191: 246 */   protected int m_plotAreaWidth = 384;
/*  192: 248 */   protected int m_plotAreaHeight = 384;
/*  193:     */   protected BoundaryPanel m_boundaryPanel;
/*  194: 255 */   protected JComboBox m_classAttBox = new JComboBox();
/*  195: 256 */   protected JComboBox m_xAttBox = new JComboBox();
/*  196: 257 */   protected JComboBox m_yAttBox = new JComboBox();
/*  197: 259 */   protected Dimension COMBO_SIZE = new Dimension((int)(this.m_plotAreaWidth * 0.75D), this.m_classAttBox.getPreferredSize().height);
/*  198: 262 */   protected JButton m_startBut = new JButton("Start");
/*  199: 264 */   protected JCheckBox m_plotTrainingData = new JCheckBox("Plot training data");
/*  200:     */   protected JPanel m_controlPanel;
/*  201: 268 */   protected ClassPanel m_classPanel = new ClassPanel();
/*  202:     */   private AxisPanel m_xAxisPanel;
/*  203:     */   private AxisPanel m_yAxisPanel;
/*  204:     */   private double m_maxX;
/*  205:     */   private double m_maxY;
/*  206:     */   private double m_minX;
/*  207:     */   private double m_minY;
/*  208:     */   private int m_xIndex;
/*  209:     */   private int m_yIndex;
/*  210:     */   private KDDataGenerator m_dataGenerator;
/*  211:     */   private int m_numberOfSamplesFromEachRegion;
/*  212:     */   private int m_generatorSamplesBase;
/*  213:     */   private int m_kernelBandwidth;
/*  214: 295 */   private final JTextField m_regionSamplesText = new JTextField("0");
/*  215: 297 */   private final JTextField m_generatorSamplesText = new JTextField("0");
/*  216: 299 */   private final JTextField m_kernelBandwidthText = new JTextField("3  ");
/*  217: 302 */   protected GenericObjectEditor m_classifierEditor = new GenericObjectEditor();
/*  218: 308 */   protected PropertyPanel m_ClassifierPanel = new PropertyPanel(this.m_classifierEditor);
/*  219: 311 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  220: 313 */   protected ExtensionFileFilter m_arffFileFilter = new ExtensionFileFilter(".arff", "Arff data files");
/*  221: 315 */   protected JLabel dataFileLabel = new JLabel();
/*  222: 319 */   protected JPanel m_addRemovePointsPanel = new JPanel();
/*  223: 323 */   protected JComboBox m_classValueSelector = new JComboBox();
/*  224: 327 */   protected JRadioButton m_addPointsButton = new JRadioButton();
/*  225: 334 */   protected JRadioButton m_removePointsButton = new JRadioButton();
/*  226: 343 */   protected ButtonGroup m_addRemovePointsButtonGroup = new ButtonGroup();
/*  227: 344 */   protected JButton removeAllButton = new JButton("Remove all");
/*  228: 347 */   protected JButton chooseButton = new JButton("Open File");
/*  229:     */   
/*  230:     */   static
/*  231:     */   {
/*  232: 352 */     GenericObjectEditor.registerEditors();
/*  233:     */   }
/*  234:     */   
/*  235:     */   public String globalInfo()
/*  236:     */   {
/*  237: 362 */     return "Class for visualizing class probability estimates.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  238:     */   }
/*  239:     */   
/*  240:     */   public TechnicalInformation getTechnicalInformation()
/*  241:     */   {
/*  242: 377 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  243: 378 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Mark Hall");
/*  244: 379 */     result.setValue(TechnicalInformation.Field.TITLE, "Visualizing class probability estimators");
/*  245: 380 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "European Conference on Principles and Practice of Knowledge Discovery in Databases");
/*  246:     */     
/*  247:     */ 
/*  248: 383 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  249: 384 */     result.setValue(TechnicalInformation.Field.PAGES, "168-169");
/*  250: 385 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer-Verlag");
/*  251: 386 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Cavtat-Dubrovnik");
/*  252:     */     
/*  253: 388 */     return result;
/*  254:     */   }
/*  255:     */   
/*  256:     */   public BoundaryVisualizer()
/*  257:     */   {
/*  258: 396 */     setLayout(new BorderLayout());
/*  259: 397 */     this.m_classAttBox.setMinimumSize(this.COMBO_SIZE);
/*  260: 398 */     this.m_classAttBox.setPreferredSize(this.COMBO_SIZE);
/*  261: 399 */     this.m_classAttBox.setMaximumSize(this.COMBO_SIZE);
/*  262: 400 */     this.m_classAttBox.addActionListener(new ActionListener()
/*  263:     */     {
/*  264:     */       public void actionPerformed(ActionEvent e)
/*  265:     */       {
/*  266: 403 */         if (BoundaryVisualizer.this.m_classAttBox.getItemCount() != 0)
/*  267:     */         {
/*  268:     */           try
/*  269:     */           {
/*  270: 405 */             BoundaryVisualizer.this.m_classPanel.setCindex(BoundaryVisualizer.this.m_classAttBox.getSelectedIndex());
/*  271: 406 */             BoundaryVisualizer.this.plotTrainingData();
/*  272: 407 */             System.err.println("Here in class att box listener");
/*  273:     */           }
/*  274:     */           catch (Exception ex)
/*  275:     */           {
/*  276: 409 */             ex.printStackTrace();
/*  277:     */           }
/*  278: 413 */           BoundaryVisualizer.this.setUpClassValueSelectorCB();
/*  279:     */         }
/*  280:     */       }
/*  281: 417 */     });
/*  282: 418 */     this.m_xAttBox.setMinimumSize(this.COMBO_SIZE);
/*  283: 419 */     this.m_xAttBox.setPreferredSize(this.COMBO_SIZE);
/*  284: 420 */     this.m_xAttBox.setMaximumSize(this.COMBO_SIZE);
/*  285:     */     
/*  286: 422 */     this.m_yAttBox.setMinimumSize(this.COMBO_SIZE);
/*  287: 423 */     this.m_yAttBox.setPreferredSize(this.COMBO_SIZE);
/*  288: 424 */     this.m_yAttBox.setMaximumSize(this.COMBO_SIZE);
/*  289:     */     
/*  290: 426 */     this.m_classPanel.setMinimumSize(new Dimension((int)this.COMBO_SIZE.getWidth() * 2, (int)this.COMBO_SIZE.getHeight() * 2));
/*  291:     */     
/*  292: 428 */     this.m_classPanel.setPreferredSize(new Dimension((int)this.COMBO_SIZE.getWidth() * 2, (int)this.COMBO_SIZE.getHeight() * 2));
/*  293:     */     
/*  294:     */ 
/*  295: 431 */     this.m_controlPanel = new JPanel();
/*  296: 432 */     this.m_controlPanel.setLayout(new BorderLayout());
/*  297:     */     
/*  298:     */ 
/*  299: 435 */     JPanel dataChooseHolder = new JPanel(new BorderLayout());
/*  300: 436 */     dataChooseHolder.setBorder(BorderFactory.createTitledBorder("Dataset"));
/*  301: 437 */     dataChooseHolder.add(this.dataFileLabel, "West");
/*  302:     */     
/*  303: 439 */     this.m_FileChooser.setFileSelectionMode(0);
/*  304: 440 */     this.m_FileChooser.addChoosableFileFilter(this.m_arffFileFilter);
/*  305: 441 */     this.chooseButton.addActionListener(new ActionListener()
/*  306:     */     {
/*  307:     */       public void actionPerformed(ActionEvent e)
/*  308:     */       {
/*  309:     */         try
/*  310:     */         {
/*  311: 445 */           BoundaryVisualizer.this.setInstancesFromFileQ();
/*  312: 446 */           int classIndex = BoundaryVisualizer.this.m_classAttBox.getSelectedIndex();
/*  313: 447 */           if ((BoundaryVisualizer.this.m_trainingInstances != null) && (BoundaryVisualizer.this.m_classifier != null) && (BoundaryVisualizer.this.m_trainingInstances.attribute(classIndex).isNominal())) {
/*  314: 449 */             BoundaryVisualizer.this.m_startBut.setEnabled(true);
/*  315:     */           }
/*  316:     */         }
/*  317:     */         catch (Exception ex)
/*  318:     */         {
/*  319: 454 */           ex.printStackTrace(System.out);
/*  320: 455 */           System.err.println("exception");
/*  321:     */         }
/*  322:     */       }
/*  323: 459 */     });
/*  324: 460 */     dataChooseHolder.add(this.chooseButton, "East");
/*  325:     */     
/*  326: 462 */     JPanel classifierHolder = new JPanel();
/*  327: 463 */     classifierHolder.setBorder(BorderFactory.createTitledBorder("Classifier"));
/*  328: 464 */     classifierHolder.setLayout(new BorderLayout());
/*  329: 465 */     this.m_classifierEditor.setClassType(Classifier.class);
/*  330:     */     
/*  331: 467 */     this.m_classifierEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  332:     */     {
/*  333:     */       public void propertyChange(PropertyChangeEvent evt)
/*  334:     */       {
/*  335: 470 */         BoundaryVisualizer.this.m_classifier = ((Classifier)BoundaryVisualizer.this.m_classifierEditor.getValue());
/*  336:     */         try
/*  337:     */         {
/*  338: 472 */           int classIndex = BoundaryVisualizer.this.m_classAttBox.getSelectedIndex();
/*  339: 473 */           if ((BoundaryVisualizer.this.m_trainingInstances != null) && (BoundaryVisualizer.this.m_classifier != null) && (BoundaryVisualizer.this.m_trainingInstances.attribute(classIndex).isNominal())) {
/*  340: 475 */             BoundaryVisualizer.this.m_startBut.setEnabled(true);
/*  341:     */           }
/*  342:     */         }
/*  343:     */         catch (Exception ex) {}
/*  344:     */       }
/*  345: 481 */     });
/*  346: 482 */     classifierHolder.add(this.m_ClassifierPanel, "Center");
/*  347:     */     
/*  348: 484 */     JPanel cHolder = new JPanel();
/*  349: 485 */     cHolder.setBorder(BorderFactory.createTitledBorder("Class Attribute"));
/*  350: 486 */     cHolder.add(this.m_classAttBox);
/*  351:     */     
/*  352: 488 */     JPanel vAttHolder = new JPanel();
/*  353: 489 */     vAttHolder.setLayout(new GridLayout(2, 1));
/*  354: 490 */     vAttHolder.setBorder(BorderFactory.createTitledBorder("Visualization Attributes"));
/*  355:     */     
/*  356: 492 */     vAttHolder.add(this.m_xAttBox);
/*  357: 493 */     vAttHolder.add(this.m_yAttBox);
/*  358:     */     
/*  359: 495 */     JPanel colOne = new JPanel();
/*  360: 496 */     colOne.setLayout(new BorderLayout());
/*  361: 497 */     colOne.add(dataChooseHolder, "North");
/*  362: 498 */     colOne.add(cHolder, "Center");
/*  363:     */     
/*  364:     */ 
/*  365: 501 */     JPanel tempPanel = new JPanel();
/*  366: 502 */     tempPanel.setBorder(BorderFactory.createTitledBorder("Sampling control"));
/*  367: 503 */     tempPanel.setLayout(new GridLayout(3, 1));
/*  368:     */     
/*  369: 505 */     JPanel colTwo = new JPanel();
/*  370: 506 */     colTwo.setLayout(new BorderLayout());
/*  371: 507 */     JPanel gsP = new JPanel();
/*  372: 508 */     gsP.setLayout(new BorderLayout());
/*  373: 509 */     gsP.add(new JLabel(" Base for sampling (r)"), "Center");
/*  374: 510 */     gsP.add(this.m_generatorSamplesText, "West");
/*  375: 511 */     tempPanel.add(gsP);
/*  376:     */     
/*  377: 513 */     JPanel rsP = new JPanel();
/*  378: 514 */     rsP.setLayout(new BorderLayout());
/*  379: 515 */     rsP.add(new JLabel(" Num. locations per pixel"), "Center");
/*  380: 516 */     rsP.add(this.m_regionSamplesText, "West");
/*  381: 517 */     tempPanel.add(rsP);
/*  382:     */     
/*  383: 519 */     JPanel ksP = new JPanel();
/*  384: 520 */     ksP.setLayout(new BorderLayout());
/*  385: 521 */     ksP.add(new JLabel(" Kernel bandwidth (k)"), "Center");
/*  386: 522 */     ksP.add(this.m_kernelBandwidthText, "West");
/*  387: 523 */     tempPanel.add(ksP);
/*  388:     */     
/*  389: 525 */     colTwo.add(classifierHolder, "North");
/*  390:     */     
/*  391: 527 */     colTwo.add(vAttHolder, "Center");
/*  392:     */     
/*  393: 529 */     JPanel startPanel = new JPanel();
/*  394: 530 */     startPanel.setBorder(BorderFactory.createTitledBorder("Plotting"));
/*  395: 531 */     startPanel.setLayout(new BorderLayout());
/*  396: 532 */     startPanel.add(this.m_startBut, "Center");
/*  397: 533 */     startPanel.add(this.m_plotTrainingData, "West");
/*  398:     */     
/*  399:     */ 
/*  400:     */ 
/*  401: 537 */     this.m_controlPanel.add(colOne, "West");
/*  402: 538 */     this.m_controlPanel.add(colTwo, "Center");
/*  403: 539 */     JPanel classHolder = new JPanel();
/*  404: 540 */     classHolder.setLayout(new BorderLayout());
/*  405: 541 */     classHolder.setBorder(BorderFactory.createTitledBorder("Class color"));
/*  406: 542 */     classHolder.add(this.m_classPanel, "Center");
/*  407: 543 */     this.m_controlPanel.add(classHolder, "South");
/*  408:     */     
/*  409: 545 */     JPanel aboutAndControlP = new JPanel();
/*  410: 546 */     aboutAndControlP.setLayout(new BorderLayout());
/*  411: 547 */     aboutAndControlP.add(this.m_controlPanel, "South");
/*  412:     */     
/*  413: 549 */     PropertySheetPanel psp = new PropertySheetPanel();
/*  414: 550 */     psp.setTarget(this);
/*  415: 551 */     JPanel aboutPanel = psp.getAboutPanel();
/*  416:     */     
/*  417: 553 */     aboutAndControlP.add(aboutPanel, "North");
/*  418:     */     
/*  419: 555 */     add(aboutAndControlP, "North");
/*  420:     */     
/*  421:     */ 
/*  422:     */ 
/*  423:     */ 
/*  424: 560 */     this.m_addRemovePointsPanel.setBorder(BorderFactory.createTitledBorder("Add / remove data points"));
/*  425:     */     
/*  426: 562 */     this.m_addRemovePointsPanel.setLayout(new GridBagLayout());
/*  427: 563 */     GridBagConstraints constraints = new GridBagConstraints();
/*  428: 564 */     constraints.weightx = 1.0D;
/*  429: 565 */     constraints.weighty = 1.0D;
/*  430: 566 */     constraints.gridx = 0;
/*  431: 567 */     constraints.gridy = 0;
/*  432: 568 */     constraints.fill = 1;
/*  433: 569 */     this.m_addRemovePointsPanel.add(this.m_addPointsButton);
/*  434: 570 */     constraints.gridx = 1;
/*  435: 571 */     this.m_addRemovePointsPanel.add(new JLabel("Add points"), constraints);
/*  436: 572 */     constraints.gridx = 2;
/*  437: 573 */     this.m_addRemovePointsPanel.add(this.m_classValueSelector);
/*  438: 574 */     constraints.gridx = 0;
/*  439: 575 */     constraints.gridy = 1;
/*  440: 576 */     this.m_addRemovePointsPanel.add(this.m_removePointsButton, constraints);
/*  441: 577 */     constraints.gridx = 1;
/*  442: 578 */     this.m_addRemovePointsPanel.add(new JLabel("Remove points"), constraints);
/*  443:     */     
/*  444: 580 */     this.removeAllButton.addActionListener(new ActionListener()
/*  445:     */     {
/*  446:     */       public void actionPerformed(ActionEvent e)
/*  447:     */       {
/*  448: 583 */         if (BoundaryVisualizer.this.m_trainingInstances != null)
/*  449:     */         {
/*  450: 584 */           if (BoundaryVisualizer.this.m_startBut.getText().equals("Stop")) {
/*  451: 585 */             return;
/*  452:     */           }
/*  453: 587 */           BoundaryVisualizer.this.m_boundaryPanel.removeAllInstances();
/*  454: 588 */           BoundaryVisualizer.this.computeBounds();
/*  455: 589 */           BoundaryVisualizer.this.m_xAxisPanel.repaint(0L, 0, 0, BoundaryVisualizer.this.m_xAxisPanel.getWidth(), BoundaryVisualizer.this.m_xAxisPanel.getHeight());
/*  456:     */           
/*  457: 591 */           BoundaryVisualizer.this.m_yAxisPanel.repaint(0L, 0, 0, BoundaryVisualizer.this.m_yAxisPanel.getWidth(), BoundaryVisualizer.this.m_yAxisPanel.getHeight());
/*  458:     */           try
/*  459:     */           {
/*  460: 595 */             BoundaryVisualizer.this.m_boundaryPanel.plotTrainingData();
/*  461:     */           }
/*  462:     */           catch (Exception ex) {}
/*  463:     */         }
/*  464:     */       }
/*  465: 600 */     });
/*  466: 601 */     constraints.gridx = 2;
/*  467: 602 */     this.m_addRemovePointsPanel.add(this.removeAllButton, constraints);
/*  468:     */     
/*  469:     */ 
/*  470:     */ 
/*  471:     */ 
/*  472:     */ 
/*  473: 608 */     this.m_addRemovePointsButtonGroup.add(this.m_addPointsButton);
/*  474: 609 */     this.m_addRemovePointsButtonGroup.add(this.m_removePointsButton);
/*  475: 610 */     this.m_addPointsButton.setSelected(true);
/*  476:     */     
/*  477:     */ 
/*  478:     */ 
/*  479: 614 */     this.m_boundaryPanel = new BoundaryPanel(this.m_plotAreaWidth, this.m_plotAreaHeight);
/*  480: 615 */     this.m_numberOfSamplesFromEachRegion = this.m_boundaryPanel.getNumSamplesPerRegion();
/*  481: 616 */     this.m_regionSamplesText.setText("" + this.m_numberOfSamplesFromEachRegion + "  ");
/*  482: 617 */     this.m_generatorSamplesBase = ((int)this.m_boundaryPanel.getGeneratorSamplesBase());
/*  483: 618 */     this.m_generatorSamplesText.setText("" + this.m_generatorSamplesBase + "  ");
/*  484:     */     
/*  485: 620 */     this.m_dataGenerator = new KDDataGenerator();
/*  486: 621 */     this.m_kernelBandwidth = this.m_dataGenerator.getKernelBandwidth();
/*  487: 622 */     this.m_kernelBandwidthText.setText("" + this.m_kernelBandwidth + "  ");
/*  488: 623 */     this.m_boundaryPanel.setDataGenerator(this.m_dataGenerator);
/*  489:     */     
/*  490: 625 */     JPanel gfxPanel = new JPanel();
/*  491: 626 */     gfxPanel.setLayout(new BorderLayout());
/*  492: 627 */     gfxPanel.setBorder(BorderFactory.createEtchedBorder());
/*  493:     */     
/*  494:     */ 
/*  495:     */ 
/*  496: 631 */     gfxPanel.add(this.m_boundaryPanel, "Center");
/*  497: 632 */     this.m_xAxisPanel = new AxisPanel(false);
/*  498: 633 */     gfxPanel.add(this.m_xAxisPanel, "South");
/*  499: 634 */     this.m_yAxisPanel = new AxisPanel(true);
/*  500: 635 */     gfxPanel.add(this.m_yAxisPanel, "West");
/*  501:     */     
/*  502: 637 */     JPanel containerPanel = new JPanel();
/*  503: 638 */     containerPanel.setLayout(new BorderLayout());
/*  504: 639 */     containerPanel.add(gfxPanel, "Center");
/*  505: 640 */     add(containerPanel, "West");
/*  506:     */     
/*  507: 642 */     JPanel rightHandToolsPanel = new JPanel();
/*  508:     */     
/*  509:     */ 
/*  510: 645 */     rightHandToolsPanel.setLayout(new BoxLayout(rightHandToolsPanel, 3));
/*  511:     */     
/*  512:     */ 
/*  513: 648 */     rightHandToolsPanel.add(this.m_addRemovePointsPanel);
/*  514:     */     
/*  515: 650 */     JButton newWindowButton = new JButton("Open a new window");
/*  516:     */     
/*  517:     */ 
/*  518:     */ 
/*  519:     */ 
/*  520:     */ 
/*  521:     */ 
/*  522:     */ 
/*  523: 658 */     newWindowButton.addActionListener(new ActionListener()
/*  524:     */     {
/*  525:     */       public void actionPerformed(ActionEvent e)
/*  526:     */       {
/*  527:     */         try
/*  528:     */         {
/*  529: 662 */           Instances newTrainingData = null;
/*  530: 663 */           Classifier newClassifier = null;
/*  531: 664 */           if (BoundaryVisualizer.this.m_trainingInstances != null) {
/*  532: 665 */             newTrainingData = new Instances(BoundaryVisualizer.this.m_trainingInstances);
/*  533:     */           }
/*  534: 667 */           if (BoundaryVisualizer.this.m_classifier != null) {
/*  535: 668 */             newClassifier = AbstractClassifier.makeCopy(BoundaryVisualizer.this.m_classifier);
/*  536:     */           }
/*  537: 670 */           BoundaryVisualizer.createNewVisualizerWindow(newClassifier, newTrainingData);
/*  538:     */         }
/*  539:     */         catch (Exception ex)
/*  540:     */         {
/*  541: 672 */           ex.printStackTrace();
/*  542:     */         }
/*  543:     */       }
/*  544: 675 */     });
/*  545: 676 */     JPanel newWindowHolder = new JPanel();
/*  546: 677 */     newWindowHolder.add(newWindowButton);
/*  547: 678 */     rightHandToolsPanel.add(newWindowHolder);
/*  548: 679 */     rightHandToolsPanel.add(tempPanel);
/*  549: 680 */     rightHandToolsPanel.add(startPanel);
/*  550:     */     
/*  551: 682 */     containerPanel.add(rightHandToolsPanel, "East");
/*  552:     */     
/*  553:     */ 
/*  554:     */ 
/*  555:     */ 
/*  556:     */ 
/*  557:     */ 
/*  558:     */ 
/*  559:     */ 
/*  560:     */ 
/*  561: 692 */     this.m_startBut.setEnabled(false);
/*  562: 693 */     this.m_startBut.addActionListener(new ActionListener()
/*  563:     */     {
/*  564:     */       public void actionPerformed(ActionEvent e)
/*  565:     */       {
/*  566: 696 */         if (BoundaryVisualizer.this.m_startBut.getText().equals("Start"))
/*  567:     */         {
/*  568: 697 */           if ((BoundaryVisualizer.this.m_trainingInstances != null) && (BoundaryVisualizer.this.m_classifier != null)) {
/*  569:     */             try
/*  570:     */             {
/*  571: 700 */               int BPSuccessCode = BoundaryVisualizer.this.setUpBoundaryPanel();
/*  572: 705 */               if (BPSuccessCode == 1)
/*  573:     */               {
/*  574: 706 */                 JOptionPane.showMessageDialog(null, "Error: Kernel Bandwidth can't be less than zero!");
/*  575:     */               }
/*  576: 708 */               else if (BPSuccessCode == 2)
/*  577:     */               {
/*  578: 709 */                 JOptionPane.showMessageDialog(null, "Error: Kernel Bandwidth must be less than the number of training instances!");
/*  579:     */               }
/*  580:     */               else
/*  581:     */               {
/*  582: 713 */                 BoundaryVisualizer.this.m_boundaryPanel.start();
/*  583: 714 */                 BoundaryVisualizer.this.m_startBut.setText("Stop");
/*  584: 715 */                 BoundaryVisualizer.this.setControlEnabledStatus(false);
/*  585:     */               }
/*  586:     */             }
/*  587:     */             catch (Exception ex)
/*  588:     */             {
/*  589: 718 */               ex.printStackTrace();
/*  590:     */             }
/*  591:     */           }
/*  592:     */         }
/*  593:     */         else
/*  594:     */         {
/*  595: 722 */           BoundaryVisualizer.this.m_boundaryPanel.stopPlotting();
/*  596: 723 */           BoundaryVisualizer.this.m_startBut.setText("Start");
/*  597: 724 */           BoundaryVisualizer.this.setControlEnabledStatus(true);
/*  598:     */         }
/*  599:     */       }
/*  600: 728 */     });
/*  601: 729 */     this.m_boundaryPanel.addActionListener(new ActionListener()
/*  602:     */     {
/*  603:     */       public void actionPerformed(ActionEvent e)
/*  604:     */       {
/*  605: 732 */         BoundaryVisualizer.this.m_startBut.setText("Start");
/*  606: 733 */         BoundaryVisualizer.this.setControlEnabledStatus(true);
/*  607:     */       }
/*  608: 736 */     });
/*  609: 737 */     this.m_classPanel.addActionListener(new ActionListener()
/*  610:     */     {
/*  611:     */       public void actionPerformed(ActionEvent e)
/*  612:     */       {
/*  613:     */         try
/*  614:     */         {
/*  615: 743 */           ArrayList<Color> colors = BoundaryVisualizer.this.m_boundaryPanel.getColors();
/*  616: 744 */           FileOutputStream fos = new FileOutputStream("colors.ser");
/*  617: 745 */           ObjectOutputStream oos = new ObjectOutputStream(fos);
/*  618: 746 */           oos.writeObject(colors);
/*  619: 747 */           oos.flush();
/*  620: 748 */           oos.close();
/*  621:     */         }
/*  622:     */         catch (Exception ex) {}
/*  623: 752 */         BoundaryVisualizer.this.m_boundaryPanel.replot();
/*  624:     */       }
/*  625: 757 */     });
/*  626: 758 */     this.m_boundaryPanel.addMouseListener(new MouseAdapter()
/*  627:     */     {
/*  628:     */       public void mouseClicked(MouseEvent e)
/*  629:     */       {
/*  630: 763 */         if (BoundaryVisualizer.this.m_trainingInstances != null)
/*  631:     */         {
/*  632: 764 */           if (BoundaryVisualizer.this.m_startBut.getText().equals("Stop")) {
/*  633: 765 */             return;
/*  634:     */           }
/*  635: 768 */           if (BoundaryVisualizer.this.m_addPointsButton.isSelected())
/*  636:     */           {
/*  637: 769 */             double classVal = 0.0D;
/*  638: 770 */             boolean validInput = true;
/*  639: 771 */             if (BoundaryVisualizer.this.m_trainingInstances.attribute(BoundaryVisualizer.this.m_classAttBox.getSelectedIndex()).isNominal())
/*  640:     */             {
/*  641: 773 */               classVal = BoundaryVisualizer.this.m_classValueSelector.getSelectedIndex();
/*  642:     */             }
/*  643:     */             else
/*  644:     */             {
/*  645: 775 */               String indexStr = "";
/*  646:     */               try
/*  647:     */               {
/*  648: 777 */                 indexStr = (String)BoundaryVisualizer.this.m_classValueSelector.getSelectedItem();
/*  649: 778 */                 classVal = Double.parseDouble(indexStr);
/*  650:     */               }
/*  651:     */               catch (Exception ex)
/*  652:     */               {
/*  653: 780 */                 if (indexStr == null) {
/*  654: 781 */                   indexStr = "";
/*  655:     */                 }
/*  656: 783 */                 JOptionPane.showMessageDialog(null, "Error adding a point: \"" + indexStr + "\"" + " is not a valid class value.");
/*  657:     */                 
/*  658: 785 */                 validInput = false;
/*  659:     */               }
/*  660:     */             }
/*  661: 789 */             if (validInput) {
/*  662: 790 */               BoundaryVisualizer.this.m_boundaryPanel.addTrainingInstanceFromMouseLocation(e.getX(), e.getY(), BoundaryVisualizer.this.m_classAttBox.getSelectedIndex(), classVal);
/*  663:     */             }
/*  664:     */           }
/*  665:     */           else
/*  666:     */           {
/*  667: 794 */             BoundaryVisualizer.this.m_boundaryPanel.removeTrainingInstanceFromMouseLocation(e.getX(), e.getY());
/*  668:     */           }
/*  669:     */           try
/*  670:     */           {
/*  671: 798 */             BoundaryVisualizer.this.plotTrainingData();
/*  672:     */           }
/*  673:     */           catch (Exception ex) {}
/*  674: 801 */           BoundaryVisualizer.this.m_xAxisPanel.repaint(0L, 0, 0, BoundaryVisualizer.this.m_xAxisPanel.getWidth(), BoundaryVisualizer.this.m_xAxisPanel.getHeight());
/*  675:     */           
/*  676: 803 */           BoundaryVisualizer.this.m_yAxisPanel.repaint(0L, 0, 0, BoundaryVisualizer.this.m_yAxisPanel.getWidth(), BoundaryVisualizer.this.m_yAxisPanel.getHeight());
/*  677:     */         }
/*  678:     */       }
/*  679:     */     });
/*  680:     */   }
/*  681:     */   
/*  682:     */   private void setControlEnabledStatus(boolean status)
/*  683:     */   {
/*  684: 816 */     this.m_classAttBox.setEnabled(status);
/*  685: 817 */     this.m_xAttBox.setEnabled(status);
/*  686: 818 */     this.m_yAttBox.setEnabled(status);
/*  687: 819 */     this.m_regionSamplesText.setEnabled(status);
/*  688: 820 */     this.m_generatorSamplesText.setEnabled(status);
/*  689: 821 */     this.m_kernelBandwidthText.setEnabled(status);
/*  690: 822 */     this.m_plotTrainingData.setEnabled(status);
/*  691: 823 */     this.removeAllButton.setEnabled(status);
/*  692: 824 */     this.m_classValueSelector.setEnabled(status);
/*  693: 825 */     this.m_addPointsButton.setEnabled(status);
/*  694: 826 */     this.m_removePointsButton.setEnabled(status);
/*  695: 827 */     this.m_FileChooser.setEnabled(status);
/*  696: 828 */     this.chooseButton.setEnabled(status);
/*  697:     */   }
/*  698:     */   
/*  699:     */   public void setClassifier(Classifier newClassifier)
/*  700:     */     throws Exception
/*  701:     */   {
/*  702: 839 */     this.m_classifier = newClassifier;
/*  703:     */     try
/*  704:     */     {
/*  705: 842 */       int classIndex = this.m_classAttBox.getSelectedIndex();
/*  706: 844 */       if ((this.m_classifier != null) && (this.m_trainingInstances != null) && (this.m_trainingInstances.attribute(classIndex).isNominal())) {
/*  707: 846 */         this.m_startBut.setEnabled(true);
/*  708:     */       } else {
/*  709: 848 */         this.m_startBut.setEnabled(false);
/*  710:     */       }
/*  711:     */     }
/*  712:     */     catch (Exception e) {}
/*  713:     */   }
/*  714:     */   
/*  715:     */   private void computeBounds()
/*  716:     */   {
/*  717: 861 */     this.m_boundaryPanel.computeMinMaxAtts();
/*  718:     */     
/*  719: 863 */     String xName = (String)this.m_xAttBox.getSelectedItem();
/*  720: 864 */     if (xName == null) {
/*  721: 865 */       return;
/*  722:     */     }
/*  723: 867 */     xName = Utils.removeSubstring(xName, "X: ");
/*  724: 868 */     xName = Utils.removeSubstring(xName, " (Num)");
/*  725: 869 */     String yName = (String)this.m_yAttBox.getSelectedItem();
/*  726: 870 */     yName = Utils.removeSubstring(yName, "Y: ");
/*  727: 871 */     yName = Utils.removeSubstring(yName, " (Num)");
/*  728:     */     
/*  729: 873 */     this.m_xIndex = -1;
/*  730: 874 */     this.m_yIndex = -1;
/*  731: 875 */     for (int i = 0; i < this.m_trainingInstances.numAttributes(); i++)
/*  732:     */     {
/*  733: 876 */       if (this.m_trainingInstances.attribute(i).name().equals(xName)) {
/*  734: 877 */         this.m_xIndex = i;
/*  735:     */       }
/*  736: 879 */       if (this.m_trainingInstances.attribute(i).name().equals(yName)) {
/*  737: 880 */         this.m_yIndex = i;
/*  738:     */       }
/*  739:     */     }
/*  740: 884 */     this.m_minX = this.m_boundaryPanel.getMinXBound();
/*  741: 885 */     this.m_minY = this.m_boundaryPanel.getMinYBound();
/*  742: 886 */     this.m_maxX = this.m_boundaryPanel.getMaxXBound();
/*  743: 887 */     this.m_maxY = this.m_boundaryPanel.getMaxYBound();
/*  744:     */     
/*  745:     */ 
/*  746: 890 */     this.m_xAxisPanel.repaint(0L, 0, 0, this.m_xAxisPanel.getWidth(), this.m_xAxisPanel.getHeight());
/*  747:     */     
/*  748: 892 */     this.m_yAxisPanel.repaint(0L, 0, 0, this.m_yAxisPanel.getWidth(), this.m_yAxisPanel.getHeight());
/*  749:     */   }
/*  750:     */   
/*  751:     */   public Instances getInstances()
/*  752:     */   {
/*  753: 902 */     return this.m_trainingInstances;
/*  754:     */   }
/*  755:     */   
/*  756:     */   public void setInstances(Instances inst)
/*  757:     */     throws Exception
/*  758:     */   {
/*  759: 911 */     if (inst == null)
/*  760:     */     {
/*  761: 912 */       this.m_trainingInstances = inst;
/*  762: 913 */       this.m_classPanel.setInstances(this.m_trainingInstances);
/*  763: 914 */       return;
/*  764:     */     }
/*  765: 918 */     int numCount = 0;
/*  766: 919 */     for (int i = 0; i < inst.numAttributes(); i++) {
/*  767: 920 */       if (inst.attribute(i).isNumeric()) {
/*  768: 921 */         numCount++;
/*  769:     */       }
/*  770:     */     }
/*  771: 925 */     if (numCount < 2)
/*  772:     */     {
/*  773: 926 */       JOptionPane.showMessageDialog(null, "We need at least two numeric attributes in order to visualize!");
/*  774:     */       
/*  775: 928 */       return;
/*  776:     */     }
/*  777: 931 */     this.m_trainingInstances = inst;
/*  778: 932 */     this.m_classPanel.setInstances(this.m_trainingInstances);
/*  779:     */     
/*  780: 934 */     String[] classAttNames = new String[this.m_trainingInstances.numAttributes()];
/*  781: 935 */     Vector<String> xAttNames = new Vector();
/*  782: 936 */     Vector<String> yAttNames = new Vector();
/*  783: 938 */     for (int i = 0; i < this.m_trainingInstances.numAttributes(); i++)
/*  784:     */     {
/*  785: 939 */       classAttNames[i] = this.m_trainingInstances.attribute(i).name();
/*  786: 940 */       String type = " (" + Attribute.typeToStringShort(this.m_trainingInstances.attribute(i)) + ")"; int 
/*  787:     */       
/*  788: 942 */         tmp188_186 = i; String[] tmp188_185 = classAttNames;tmp188_185[tmp188_186] = (tmp188_185[tmp188_186] + type);
/*  789: 943 */       if (this.m_trainingInstances.attribute(i).isNumeric())
/*  790:     */       {
/*  791: 944 */         xAttNames.add("X: " + classAttNames[i]);
/*  792: 945 */         yAttNames.add("Y: " + classAttNames[i]);
/*  793:     */       }
/*  794:     */     }
/*  795: 949 */     this.m_classAttBox.setModel(new DefaultComboBoxModel(classAttNames));
/*  796: 950 */     this.m_xAttBox.setModel(new DefaultComboBoxModel(xAttNames));
/*  797: 951 */     this.m_yAttBox.setModel(new DefaultComboBoxModel(yAttNames));
/*  798: 952 */     if (xAttNames.size() > 1) {
/*  799: 953 */       this.m_yAttBox.setSelectedIndex(1);
/*  800:     */     }
/*  801: 956 */     this.m_classAttBox.addActionListener(new ActionListener()
/*  802:     */     {
/*  803:     */       public void actionPerformed(ActionEvent e)
/*  804:     */       {
/*  805: 959 */         BoundaryVisualizer.this.configureForClassAttribute();
/*  806:     */       }
/*  807: 962 */     });
/*  808: 963 */     this.m_xAttBox.addItemListener(new ItemListener()
/*  809:     */     {
/*  810:     */       public void itemStateChanged(ItemEvent e)
/*  811:     */       {
/*  812: 966 */         if (e.getStateChange() == 1)
/*  813:     */         {
/*  814: 973 */           BoundaryVisualizer.this.computeBounds();
/*  815: 974 */           BoundaryVisualizer.this.repaint();
/*  816:     */           try
/*  817:     */           {
/*  818: 976 */             BoundaryVisualizer.this.plotTrainingData();
/*  819:     */           }
/*  820:     */           catch (Exception ex)
/*  821:     */           {
/*  822: 978 */             ex.printStackTrace();
/*  823:     */           }
/*  824:     */         }
/*  825:     */       }
/*  826: 983 */     });
/*  827: 984 */     this.m_yAttBox.addItemListener(new ItemListener()
/*  828:     */     {
/*  829:     */       public void itemStateChanged(ItemEvent e)
/*  830:     */       {
/*  831: 987 */         if (e.getStateChange() == 1)
/*  832:     */         {
/*  833: 994 */           BoundaryVisualizer.this.computeBounds();
/*  834: 995 */           BoundaryVisualizer.this.repaint();
/*  835:     */           try
/*  836:     */           {
/*  837: 997 */             BoundaryVisualizer.this.plotTrainingData();
/*  838:     */           }
/*  839:     */           catch (Exception ex)
/*  840:     */           {
/*  841: 999 */             ex.printStackTrace();
/*  842:     */           }
/*  843:     */         }
/*  844:     */       }
/*  845:     */     });
/*  846:1005 */     if (classAttNames.length > 0) {
/*  847:1006 */       this.m_classAttBox.setSelectedIndex(classAttNames.length - 1);
/*  848:     */     }
/*  849:1014 */     setUpClassValueSelectorCB();
/*  850:     */     
/*  851:1016 */     configureForClassAttribute();
/*  852:     */     
/*  853:1018 */     this.m_classPanel.setCindex(this.m_classAttBox.getSelectedIndex());
/*  854:1019 */     plotTrainingData();
/*  855:1020 */     computeBounds();
/*  856:1021 */     revalidate();
/*  857:1022 */     repaint();
/*  858:1024 */     if ((getTopLevelAncestor() instanceof Window)) {
/*  859:1025 */       ((Window)getTopLevelAncestor()).pack();
/*  860:     */     }
/*  861:     */   }
/*  862:     */   
/*  863:     */   private void setUpClassValueSelectorCB()
/*  864:     */   {
/*  865:1034 */     this.m_classValueSelector.removeAllItems();
/*  866:1035 */     int classAttribute = this.m_classAttBox.getSelectedIndex();
/*  867:     */     
/*  868:1037 */     this.m_trainingInstances.setClassIndex(classAttribute);
/*  869:1038 */     if (this.m_trainingInstances.attribute(classAttribute).isNominal())
/*  870:     */     {
/*  871:1039 */       this.m_classValueSelector.setEditable(false);
/*  872:1040 */       for (int i = 0; i < this.m_trainingInstances.numClasses(); i++) {
/*  873:1044 */         this.m_classValueSelector.insertItemAt(this.m_trainingInstances.attribute(classAttribute).value(i), i);
/*  874:     */       }
/*  875:1047 */       this.m_classValueSelector.setSelectedIndex(0);
/*  876:     */     }
/*  877:     */     else
/*  878:     */     {
/*  879:1049 */       this.m_classValueSelector.setEditable(true);
/*  880:     */     }
/*  881:     */   }
/*  882:     */   
/*  883:     */   private void configureForClassAttribute()
/*  884:     */   {
/*  885:1057 */     int classIndex = this.m_classAttBox.getSelectedIndex();
/*  886:1058 */     if (classIndex >= 0)
/*  887:     */     {
/*  888:1060 */       if ((!this.m_trainingInstances.attribute(classIndex).isNominal()) || (this.m_classifier == null)) {
/*  889:1062 */         this.m_startBut.setEnabled(false);
/*  890:     */       } else {
/*  891:1064 */         this.m_startBut.setEnabled(true);
/*  892:     */       }
/*  893:1067 */       ArrayList<Color> colors = new ArrayList();
/*  894:1068 */       if (!this.m_trainingInstances.attribute(this.m_classAttBox.getSelectedIndex()).isNominal()) {
/*  895:1071 */         for (Color element : BoundaryPanel.DEFAULT_COLORS) {
/*  896:1072 */           colors.add(element);
/*  897:     */         }
/*  898:     */       } else {
/*  899:1075 */         for (int i = 0; i < this.m_trainingInstances.attribute(classIndex).numValues(); i++) {
/*  900:1077 */           colors.add(BoundaryPanel.DEFAULT_COLORS[(i % BoundaryPanel.DEFAULT_COLORS.length)]);
/*  901:     */         }
/*  902:     */       }
/*  903:1083 */       this.m_classPanel.setColours(colors);
/*  904:1084 */       this.m_boundaryPanel.setColors(colors);
/*  905:     */     }
/*  906:     */   }
/*  907:     */   
/*  908:     */   public void setInstancesFromFileQ()
/*  909:     */   {
/*  910:1096 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/*  911:1097 */     if (returnVal == 0)
/*  912:     */     {
/*  913:1098 */       File selected = this.m_FileChooser.getSelectedFile();
/*  914:     */       try
/*  915:     */       {
/*  916:1101 */         Reader r = new BufferedReader(new FileReader(selected));
/*  917:     */         
/*  918:1103 */         Instances i = new Instances(r);
/*  919:1104 */         setInstances(i);
/*  920:     */         
/*  921:     */ 
/*  922:1107 */         String relationName = i.relationName();
/*  923:1108 */         String truncatedN = relationName;
/*  924:1109 */         if (relationName.length() > 25) {
/*  925:1110 */           truncatedN = relationName.substring(0, 25) + "...";
/*  926:     */         }
/*  927:1112 */         this.dataFileLabel.setText(truncatedN);
/*  928:1113 */         this.dataFileLabel.setToolTipText(relationName);
/*  929:     */       }
/*  930:     */       catch (Exception e)
/*  931:     */       {
/*  932:1115 */         JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/*  933:     */         
/*  934:     */ 
/*  935:1118 */         e.printStackTrace();
/*  936:     */       }
/*  937:     */     }
/*  938:     */   }
/*  939:     */   
/*  940:     */   public int setUpBoundaryPanel()
/*  941:     */     throws Exception
/*  942:     */   {
/*  943:1133 */     int returner = 0;
/*  944:1134 */     int tempSamples = this.m_numberOfSamplesFromEachRegion;
/*  945:     */     try
/*  946:     */     {
/*  947:1136 */       tempSamples = Integer.parseInt(this.m_regionSamplesText.getText().trim());
/*  948:     */     }
/*  949:     */     catch (Exception ex)
/*  950:     */     {
/*  951:1138 */       this.m_regionSamplesText.setText("" + tempSamples);
/*  952:     */     }
/*  953:1140 */     this.m_numberOfSamplesFromEachRegion = tempSamples;
/*  954:1141 */     this.m_boundaryPanel.setNumSamplesPerRegion(tempSamples);
/*  955:     */     
/*  956:1143 */     tempSamples = this.m_generatorSamplesBase;
/*  957:     */     try
/*  958:     */     {
/*  959:1145 */       tempSamples = Integer.parseInt(this.m_generatorSamplesText.getText().trim());
/*  960:     */     }
/*  961:     */     catch (Exception ex)
/*  962:     */     {
/*  963:1147 */       this.m_generatorSamplesText.setText("" + tempSamples);
/*  964:     */     }
/*  965:1149 */     this.m_generatorSamplesBase = tempSamples;
/*  966:1150 */     this.m_boundaryPanel.setGeneratorSamplesBase(tempSamples);
/*  967:     */     
/*  968:1152 */     tempSamples = this.m_kernelBandwidth;
/*  969:     */     try
/*  970:     */     {
/*  971:1154 */       tempSamples = Integer.parseInt(this.m_kernelBandwidthText.getText().trim());
/*  972:     */     }
/*  973:     */     catch (Exception ex)
/*  974:     */     {
/*  975:1156 */       this.m_kernelBandwidthText.setText("" + tempSamples);
/*  976:     */     }
/*  977:1158 */     this.m_kernelBandwidth = tempSamples;
/*  978:1159 */     this.m_dataGenerator.setKernelBandwidth(tempSamples);
/*  979:1161 */     if (this.m_kernelBandwidth < 0) {
/*  980:1162 */       returner = 1;
/*  981:     */     }
/*  982:1164 */     if (this.m_kernelBandwidth >= this.m_trainingInstances.numInstances()) {
/*  983:1165 */       returner = 2;
/*  984:     */     }
/*  985:1168 */     this.m_trainingInstances.setClassIndex(this.m_classAttBox.getSelectedIndex());
/*  986:1169 */     this.m_boundaryPanel.setClassifier(this.m_classifier);
/*  987:1170 */     this.m_boundaryPanel.setTrainingData(this.m_trainingInstances);
/*  988:1171 */     this.m_boundaryPanel.setXAttribute(this.m_xIndex);
/*  989:1172 */     this.m_boundaryPanel.setYAttribute(this.m_yIndex);
/*  990:1173 */     this.m_boundaryPanel.setPlotTrainingData(this.m_plotTrainingData.isSelected());
/*  991:     */     
/*  992:1175 */     return returner;
/*  993:     */   }
/*  994:     */   
/*  995:     */   public void plotTrainingData()
/*  996:     */     throws Exception
/*  997:     */   {
/*  998:1183 */     this.m_boundaryPanel.initialize();
/*  999:1184 */     setUpBoundaryPanel();
/* 1000:1185 */     computeBounds();
/* 1001:1186 */     this.m_boundaryPanel.plotTrainingData();
/* 1002:     */   }
/* 1003:     */   
/* 1004:     */   public void stopPlotting()
/* 1005:     */   {
/* 1006:1193 */     this.m_boundaryPanel.stopPlotting();
/* 1007:     */   }
/* 1008:     */   
/* 1009:     */   public static void setExitIfNoWindowsOpen(boolean value)
/* 1010:     */   {
/* 1011:1203 */     m_ExitIfNoWindowsOpen = value;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public static boolean getExitIfNoWindowsOpen()
/* 1015:     */   {
/* 1016:1212 */     return m_ExitIfNoWindowsOpen;
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   public static void createNewVisualizerWindow(Classifier classifier, Instances instances)
/* 1020:     */     throws Exception
/* 1021:     */   {
/* 1022:1224 */     m_WindowCount += 1;
/* 1023:     */     
/* 1024:1226 */     final JFrame jf = new JFrame("Weka classification boundary visualizer");
/* 1025:     */     
/* 1026:1228 */     jf.getContentPane().setLayout(new BorderLayout());
/* 1027:1229 */     BoundaryVisualizer bv = new BoundaryVisualizer();
/* 1028:1230 */     jf.getContentPane().add(bv, "Center");
/* 1029:1231 */     jf.setSize(bv.getMinimumSize());
/* 1030:1232 */     jf.addWindowListener(new WindowAdapter()
/* 1031:     */     {
/* 1032:     */       public void windowClosing(WindowEvent e)
/* 1033:     */       {
/* 1034:1235 */         BoundaryVisualizer.m_WindowCount -= 1;
/* 1035:1236 */         this.val$bv.stopPlotting();
/* 1036:1237 */         jf.dispose();
/* 1037:1238 */         if ((BoundaryVisualizer.m_WindowCount == 0) && (BoundaryVisualizer.m_ExitIfNoWindowsOpen)) {
/* 1038:1239 */           System.exit(0);
/* 1039:     */         }
/* 1040:     */       }
/* 1041:1243 */     });
/* 1042:1244 */     jf.pack();
/* 1043:1245 */     jf.setVisible(true);
/* 1044:1246 */     jf.setResizable(false);
/* 1045:1248 */     if (classifier == null)
/* 1046:     */     {
/* 1047:1249 */       bv.setClassifier(null);
/* 1048:     */     }
/* 1049:     */     else
/* 1050:     */     {
/* 1051:1251 */       bv.setClassifier(classifier);
/* 1052:1252 */       bv.m_classifierEditor.setValue(classifier);
/* 1053:     */     }
/* 1054:1255 */     if (instances == null)
/* 1055:     */     {
/* 1056:1256 */       bv.setInstances(null);
/* 1057:     */     }
/* 1058:     */     else
/* 1059:     */     {
/* 1060:1258 */       bv.setInstances(instances);
/* 1061:     */       try
/* 1062:     */       {
/* 1063:1261 */         bv.dataFileLabel.setText(instances.relationName());
/* 1064:1262 */         bv.plotTrainingData();
/* 1065:1263 */         bv.m_classPanel.setCindex(bv.m_classAttBox.getSelectedIndex());
/* 1066:1264 */         bv.repaint(0L, 0, 0, bv.getWidth(), bv.getHeight());
/* 1067:     */       }
/* 1068:     */       catch (Exception ex) {}
/* 1069:     */     }
/* 1070:     */   }
/* 1071:     */   
/* 1072:     */   public static void main(String[] args)
/* 1073:     */   {
/* 1074:1277 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 1075:     */     try
/* 1076:     */     {
/* 1077:1280 */       if (args.length < 2)
/* 1078:     */       {
/* 1079:1281 */         createNewVisualizerWindow(null, null);
/* 1080:     */       }
/* 1081:     */       else
/* 1082:     */       {
/* 1083:1283 */         String[] argsR = null;
/* 1084:1284 */         if (args.length > 2)
/* 1085:     */         {
/* 1086:1285 */           argsR = new String[args.length - 2];
/* 1087:1286 */           for (int j = 2; j < args.length; j++) {
/* 1088:1287 */             argsR[(j - 2)] = args[j];
/* 1089:     */           }
/* 1090:     */         }
/* 1091:1290 */         Classifier c = AbstractClassifier.forName(args[1], argsR);
/* 1092:     */         
/* 1093:1292 */         System.err.println("Loading instances from : " + args[0]);
/* 1094:1293 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 1095:     */         
/* 1096:1295 */         Instances i = new Instances(r);
/* 1097:     */         
/* 1098:1297 */         createNewVisualizerWindow(c, i);
/* 1099:     */       }
/* 1100:     */     }
/* 1101:     */     catch (Exception ex)
/* 1102:     */     {
/* 1103:1301 */       ex.printStackTrace();
/* 1104:     */     }
/* 1105:     */   }
/* 1106:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.BoundaryVisualizer
 * JD-Core Version:    0.7.0.1
 */