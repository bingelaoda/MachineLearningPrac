/*    1:     */ package weka.gui.visualize;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dialog.ModalityType;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Font;
/*    9:     */ import java.awt.FontMetrics;
/*   10:     */ import java.awt.Graphics;
/*   11:     */ import java.awt.GridBagConstraints;
/*   12:     */ import java.awt.GridBagLayout;
/*   13:     */ import java.awt.Image;
/*   14:     */ import java.awt.Insets;
/*   15:     */ import java.awt.Point;
/*   16:     */ import java.awt.Rectangle;
/*   17:     */ import java.awt.Window;
/*   18:     */ import java.awt.event.ActionEvent;
/*   19:     */ import java.awt.event.ActionListener;
/*   20:     */ import java.awt.event.ComponentAdapter;
/*   21:     */ import java.awt.event.ComponentEvent;
/*   22:     */ import java.awt.event.MouseEvent;
/*   23:     */ import java.awt.event.MouseListener;
/*   24:     */ import java.awt.event.MouseMotionListener;
/*   25:     */ import java.awt.event.WindowAdapter;
/*   26:     */ import java.awt.event.WindowEvent;
/*   27:     */ import java.io.BufferedReader;
/*   28:     */ import java.io.File;
/*   29:     */ import java.io.FileReader;
/*   30:     */ import java.io.IOException;
/*   31:     */ import java.io.PrintStream;
/*   32:     */ import java.util.ArrayList;
/*   33:     */ import java.util.Random;
/*   34:     */ import javax.swing.BorderFactory;
/*   35:     */ import javax.swing.JButton;
/*   36:     */ import javax.swing.JCheckBox;
/*   37:     */ import javax.swing.JComboBox;
/*   38:     */ import javax.swing.JDialog;
/*   39:     */ import javax.swing.JFileChooser;
/*   40:     */ import javax.swing.JFrame;
/*   41:     */ import javax.swing.JLabel;
/*   42:     */ import javax.swing.JList;
/*   43:     */ import javax.swing.JPanel;
/*   44:     */ import javax.swing.JScrollBar;
/*   45:     */ import javax.swing.JScrollPane;
/*   46:     */ import javax.swing.JSlider;
/*   47:     */ import javax.swing.JSplitPane;
/*   48:     */ import javax.swing.JTextField;
/*   49:     */ import javax.swing.event.ChangeEvent;
/*   50:     */ import javax.swing.event.ChangeListener;
/*   51:     */ import weka.core.Attribute;
/*   52:     */ import weka.core.Environment;
/*   53:     */ import weka.core.Instance;
/*   54:     */ import weka.core.Instances;
/*   55:     */ import weka.core.Settings;
/*   56:     */ import weka.gui.ExtensionFileFilter;
/*   57:     */ import weka.gui.explorer.VisualizePanel.ScatterDefaults;
/*   58:     */ 
/*   59:     */ public class MatrixPanel
/*   60:     */   extends JPanel
/*   61:     */ {
/*   62:     */   private static final long serialVersionUID = -1232642719869188740L;
/*   63:     */   private final Plot m_plotsPanel;
/*   64:  92 */   protected final ClassPanel m_cp = new ClassPanel();
/*   65:     */   protected JPanel optionsPanel;
/*   66:     */   protected JSplitPane jp;
/*   67: 106 */   protected JButton m_updateBt = new JButton("Update");
/*   68: 109 */   protected JButton m_selAttrib = new JButton("Select Attributes");
/*   69: 112 */   protected Instances m_data = null;
/*   70: 115 */   protected JList m_attribList = new JList();
/*   71: 118 */   protected final JScrollPane m_js = new JScrollPane();
/*   72: 121 */   protected JComboBox m_classAttrib = new JComboBox();
/*   73: 124 */   protected JSlider m_plotSize = new JSlider(50, 200, 100);
/*   74: 127 */   protected JSlider m_pointSize = new JSlider(1, 10, 1);
/*   75: 130 */   protected JSlider m_jitter = new JSlider(0, 20, 0);
/*   76: 133 */   private final Random rnd = new Random();
/*   77:     */   private int[][] jitterVals;
/*   78: 139 */   private int datapointSize = 1;
/*   79: 142 */   protected JTextField m_resamplePercent = new JTextField(5);
/*   80: 145 */   protected JButton m_resampleBt = new JButton("SubSample % :");
/*   81: 148 */   protected JTextField m_rseed = new JTextField(5);
/*   82: 151 */   private final JLabel m_plotSizeLb = new JLabel("PlotSize: [100]");
/*   83: 154 */   private final JLabel m_pointSizeLb = new JLabel("PointSize: [10]");
/*   84:     */   private int[] m_selectedAttribs;
/*   85:     */   private int m_classIndex;
/*   86:     */   private int[][] m_points;
/*   87:     */   private int[] m_pointColors;
/*   88:     */   private boolean[][] m_missing;
/*   89:     */   private int[] m_type;
/*   90:     */   private final Dimension m_plotLBSizeD;
/*   91:     */   private final Dimension m_pointLBSizeD;
/*   92: 201 */   private final ArrayList<Color> m_colorList = new ArrayList();
/*   93: 204 */   private static final Color[] m_defaultColors = { Color.blue, Color.red, Color.cyan, new Color(75, 123, 130), Color.pink, Color.green, Color.orange, new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0), Color.black };
/*   94: 210 */   private final Color fontColor = new Color(98, 101, 156);
/*   95: 213 */   private final Font f = new Font("Dialog", 1, 11);
/*   96:     */   protected Settings m_settings;
/*   97: 220 */   protected Color m_backgroundColor = Color.white;
/*   98:     */   protected String m_settingsOwnerID;
/*   99: 228 */   protected transient Image m_osi = null;
/*  100:     */   protected boolean[][] m_plottedCells;
/*  101: 230 */   protected boolean m_regenerateOSI = true;
/*  102:     */   protected boolean m_clearOSIPlottedCells;
/*  103: 232 */   protected double m_previousPercent = -1.0D;
/*  104: 234 */   protected JCheckBox m_fastScroll = new JCheckBox("Fast scrolling (uses more memory)");
/*  105:     */   
/*  106:     */   public MatrixPanel()
/*  107:     */   {
/*  108: 241 */     this.m_rseed.setText("1");
/*  109:     */     
/*  110:     */ 
/*  111: 244 */     this.m_selAttrib.addActionListener(new ActionListener()
/*  112:     */     {
/*  113:     */       public void actionPerformed(ActionEvent ae)
/*  114:     */       {
/*  115: 247 */         final JDialog jd = new JDialog((JFrame)MatrixPanel.this.getTopLevelAncestor(), "Attribute Selection Panel", Dialog.ModalityType.DOCUMENT_MODAL);
/*  116:     */         
/*  117:     */ 
/*  118:     */ 
/*  119: 251 */         JPanel jp = new JPanel();
/*  120: 252 */         JScrollPane js = new JScrollPane(MatrixPanel.this.m_attribList);
/*  121: 253 */         JButton okBt = new JButton("OK");
/*  122: 254 */         JButton cancelBt = new JButton("Cancel");
/*  123: 255 */         final int[] savedSelection = MatrixPanel.this.m_attribList.getSelectedIndices();
/*  124:     */         
/*  125: 257 */         okBt.addActionListener(new ActionListener()
/*  126:     */         {
/*  127:     */           public void actionPerformed(ActionEvent e)
/*  128:     */           {
/*  129: 260 */             jd.dispose();
/*  130:     */           }
/*  131: 263 */         });
/*  132: 264 */         cancelBt.addActionListener(new ActionListener()
/*  133:     */         {
/*  134:     */           public void actionPerformed(ActionEvent e)
/*  135:     */           {
/*  136: 267 */             MatrixPanel.this.m_attribList.setSelectedIndices(savedSelection);
/*  137: 268 */             jd.dispose();
/*  138:     */           }
/*  139: 270 */         });
/*  140: 271 */         jd.addWindowListener(new WindowAdapter()
/*  141:     */         {
/*  142:     */           public void windowClosing(WindowEvent e)
/*  143:     */           {
/*  144: 274 */             MatrixPanel.this.m_attribList.setSelectedIndices(savedSelection);
/*  145: 275 */             jd.dispose();
/*  146:     */           }
/*  147: 277 */         });
/*  148: 278 */         jp.add(okBt);
/*  149: 279 */         jp.add(cancelBt);
/*  150:     */         
/*  151: 281 */         jd.getContentPane().add(js, "Center");
/*  152: 282 */         jd.getContentPane().add(jp, "South");
/*  153: 284 */         if (js.getPreferredSize().width < 200) {
/*  154: 285 */           jd.setSize(250, 250);
/*  155:     */         } else {
/*  156: 287 */           jd.setSize(js.getPreferredSize().width + 10, 250);
/*  157:     */         }
/*  158: 290 */         jd.setLocation(MatrixPanel.this.m_selAttrib.getLocationOnScreen().x, MatrixPanel.this.m_selAttrib.getLocationOnScreen().y - jd.getHeight());
/*  159:     */         
/*  160: 292 */         jd.setVisible(true);
/*  161:     */       }
/*  162: 295 */     });
/*  163: 296 */     this.m_updateBt.addActionListener(new ActionListener()
/*  164:     */     {
/*  165:     */       public void actionPerformed(ActionEvent e)
/*  166:     */       {
/*  167: 299 */         MatrixPanel.this.updatePanel();
/*  168:     */       }
/*  169: 301 */     });
/*  170: 302 */     this.m_updateBt.setPreferredSize(this.m_selAttrib.getPreferredSize());
/*  171:     */     
/*  172: 304 */     this.m_jitter.addChangeListener(new ChangeListener()
/*  173:     */     {
/*  174:     */       public void stateChanged(ChangeEvent ce)
/*  175:     */       {
/*  176: 307 */         if (MatrixPanel.this.m_fastScroll.isSelected()) {
/*  177: 308 */           MatrixPanel.this.m_clearOSIPlottedCells = true;
/*  178:     */         }
/*  179:     */       }
/*  180: 312 */     });
/*  181: 313 */     this.m_plotSize.addChangeListener(new ChangeListener()
/*  182:     */     {
/*  183:     */       public void stateChanged(ChangeEvent ce)
/*  184:     */       {
/*  185: 316 */         MatrixPanel.this.m_plotSizeLb.setText("PlotSize: [" + MatrixPanel.this.m_plotSize.getValue() + "]");
/*  186: 317 */         MatrixPanel.this.m_plotSizeLb.setPreferredSize(MatrixPanel.this.m_plotLBSizeD);
/*  187: 318 */         MatrixPanel.this.m_jitter.setMaximum(MatrixPanel.this.m_plotSize.getValue() / 5);
/*  188: 319 */         MatrixPanel.this.m_regenerateOSI = true;
/*  189:     */       }
/*  190: 322 */     });
/*  191: 323 */     this.m_pointSize.addChangeListener(new ChangeListener()
/*  192:     */     {
/*  193:     */       public void stateChanged(ChangeEvent ce)
/*  194:     */       {
/*  195: 326 */         MatrixPanel.this.m_pointSizeLb.setText("PointSize: [" + MatrixPanel.this.m_pointSize.getValue() + "]");
/*  196: 327 */         MatrixPanel.this.m_pointSizeLb.setPreferredSize(MatrixPanel.this.m_pointLBSizeD);
/*  197: 328 */         MatrixPanel.this.datapointSize = MatrixPanel.this.m_pointSize.getValue();
/*  198: 329 */         if (MatrixPanel.this.m_fastScroll.isSelected()) {
/*  199: 330 */           MatrixPanel.this.m_clearOSIPlottedCells = true;
/*  200:     */         }
/*  201:     */       }
/*  202: 334 */     });
/*  203: 335 */     this.m_resampleBt.addActionListener(new ActionListener()
/*  204:     */     {
/*  205:     */       public void actionPerformed(ActionEvent e)
/*  206:     */       {
/*  207: 338 */         JLabel rseedLb = new JLabel("Random Seed: ");
/*  208: 339 */         JTextField rseedTxt = MatrixPanel.this.m_rseed;
/*  209: 340 */         JLabel percentLb = new JLabel("Subsample as");
/*  210: 341 */         JLabel percent2Lb = new JLabel("% of input: ");
/*  211: 342 */         final JTextField percentTxt = new JTextField(5);
/*  212: 343 */         percentTxt.setText(MatrixPanel.this.m_resamplePercent.getText());
/*  213: 344 */         JButton doneBt = new JButton("Done");
/*  214:     */         
/*  215: 346 */         final JDialog jd = new JDialog((JFrame)MatrixPanel.this.getTopLevelAncestor(), "Subsample % Panel", Dialog.ModalityType.DOCUMENT_MODAL)
/*  216:     */         {
/*  217:     */           private static final long serialVersionUID = -269823533147146296L;
/*  218:     */           
/*  219:     */           public void dispose()
/*  220:     */           {
/*  221: 353 */             MatrixPanel.this.m_resamplePercent.setText(percentTxt.getText());
/*  222: 354 */             super.dispose();
/*  223:     */           }
/*  224: 356 */         };
/*  225: 357 */         jd.setDefaultCloseOperation(2);
/*  226:     */         
/*  227: 359 */         doneBt.addActionListener(new ActionListener()
/*  228:     */         {
/*  229:     */           public void actionPerformed(ActionEvent ae)
/*  230:     */           {
/*  231: 362 */             jd.dispose();
/*  232:     */           }
/*  233: 364 */         });
/*  234: 365 */         GridBagLayout gbl = new GridBagLayout();
/*  235: 366 */         GridBagConstraints gbc = new GridBagConstraints();
/*  236: 367 */         JPanel p1 = new JPanel(gbl);
/*  237: 368 */         gbc.anchor = 17;
/*  238: 369 */         gbc.fill = 2;
/*  239: 370 */         gbc.insets = new Insets(0, 2, 2, 2);
/*  240: 371 */         gbc.gridwidth = -1;
/*  241: 372 */         p1.add(rseedLb, gbc);
/*  242: 373 */         gbc.weightx = 0.0D;
/*  243: 374 */         gbc.gridwidth = 0;
/*  244: 375 */         gbc.weightx = 1.0D;
/*  245: 376 */         p1.add(rseedTxt, gbc);
/*  246: 377 */         gbc.insets = new Insets(8, 2, 0, 2);
/*  247: 378 */         gbc.weightx = 0.0D;
/*  248: 379 */         p1.add(percentLb, gbc);
/*  249: 380 */         gbc.insets = new Insets(0, 2, 2, 2);
/*  250: 381 */         gbc.gridwidth = -1;
/*  251: 382 */         p1.add(percent2Lb, gbc);
/*  252: 383 */         gbc.gridwidth = 0;
/*  253: 384 */         gbc.weightx = 1.0D;
/*  254: 385 */         p1.add(percentTxt, gbc);
/*  255: 386 */         gbc.insets = new Insets(8, 2, 2, 2);
/*  256:     */         
/*  257: 388 */         JPanel p3 = new JPanel(gbl);
/*  258: 389 */         gbc.fill = 2;
/*  259: 390 */         gbc.gridwidth = 0;
/*  260: 391 */         gbc.weightx = 1.0D;
/*  261: 392 */         gbc.weighty = 0.0D;
/*  262: 393 */         p3.add(p1, gbc);
/*  263: 394 */         gbc.insets = new Insets(8, 4, 8, 4);
/*  264: 395 */         p3.add(doneBt, gbc);
/*  265:     */         
/*  266: 397 */         jd.getContentPane().setLayout(new BorderLayout());
/*  267: 398 */         jd.getContentPane().add(p3, "North");
/*  268: 399 */         jd.pack();
/*  269: 400 */         jd.setLocation(MatrixPanel.this.m_resampleBt.getLocationOnScreen().x, MatrixPanel.this.m_resampleBt.getLocationOnScreen().y - jd.getHeight());
/*  270:     */         
/*  271: 402 */         jd.setVisible(true);
/*  272:     */       }
/*  273: 405 */     });
/*  274: 406 */     this.optionsPanel = new JPanel(new GridBagLayout());
/*  275:     */     
/*  276: 408 */     JPanel p2 = new JPanel(new BorderLayout());
/*  277:     */     
/*  278: 410 */     JPanel p3 = new JPanel(new GridBagLayout());
/*  279:     */     
/*  280: 412 */     JPanel p4 = new JPanel(new GridBagLayout());
/*  281:     */     
/*  282: 414 */     GridBagConstraints gbc = new GridBagConstraints();
/*  283:     */     
/*  284: 416 */     this.m_plotLBSizeD = this.m_plotSizeLb.getPreferredSize();
/*  285: 417 */     this.m_pointLBSizeD = this.m_pointSizeLb.getPreferredSize();
/*  286: 418 */     this.m_pointSizeLb.setText("PointSize: [1]");
/*  287: 419 */     this.m_pointSizeLb.setPreferredSize(this.m_pointLBSizeD);
/*  288: 420 */     this.m_resampleBt.setPreferredSize(this.m_selAttrib.getPreferredSize());
/*  289:     */     
/*  290: 422 */     gbc.fill = 2;
/*  291: 423 */     gbc.anchor = 18;
/*  292: 424 */     gbc.insets = new Insets(2, 2, 2, 2);
/*  293: 425 */     p4.add(this.m_plotSizeLb, gbc);
/*  294: 426 */     gbc.weightx = 1.0D;
/*  295: 427 */     gbc.gridwidth = 0;
/*  296: 428 */     p4.add(this.m_plotSize, gbc);
/*  297: 429 */     gbc.weightx = 0.0D;
/*  298: 430 */     gbc.gridwidth = -1;
/*  299: 431 */     p4.add(this.m_pointSizeLb, gbc);
/*  300: 432 */     gbc.weightx = 1.0D;
/*  301: 433 */     gbc.gridwidth = 0;
/*  302: 434 */     p4.add(this.m_pointSize, gbc);
/*  303: 435 */     gbc.weightx = 0.0D;
/*  304: 436 */     gbc.gridwidth = -1;
/*  305: 437 */     p4.add(new JLabel("Jitter: "), gbc);
/*  306: 438 */     gbc.weightx = 1.0D;
/*  307: 439 */     gbc.gridwidth = 0;
/*  308: 440 */     p4.add(this.m_jitter, gbc);
/*  309: 441 */     p4.add(this.m_classAttrib, gbc);
/*  310:     */     
/*  311: 443 */     gbc.gridwidth = 0;
/*  312: 444 */     gbc.weightx = 1.0D;
/*  313: 445 */     gbc.fill = 0;
/*  314: 446 */     p3.add(this.m_fastScroll, gbc);
/*  315: 447 */     p3.add(this.m_updateBt, gbc);
/*  316: 448 */     p3.add(this.m_selAttrib, gbc);
/*  317: 449 */     gbc.gridwidth = -1;
/*  318: 450 */     gbc.weightx = 0.0D;
/*  319: 451 */     gbc.fill = 3;
/*  320: 452 */     gbc.anchor = 17;
/*  321: 453 */     p3.add(this.m_resampleBt, gbc);
/*  322: 454 */     gbc.gridwidth = 0;
/*  323: 455 */     p3.add(this.m_resamplePercent, gbc);
/*  324:     */     
/*  325: 457 */     p2.setBorder(BorderFactory.createTitledBorder("Class Colour"));
/*  326: 458 */     p2.add(this.m_cp, "South");
/*  327:     */     
/*  328: 460 */     gbc.insets = new Insets(8, 5, 2, 5);
/*  329: 461 */     gbc.anchor = 16;
/*  330: 462 */     gbc.fill = 2;
/*  331: 463 */     gbc.weightx = 1.0D;
/*  332: 464 */     gbc.gridwidth = -1;
/*  333: 465 */     this.optionsPanel.add(p4, gbc);
/*  334: 466 */     gbc.gridwidth = 0;
/*  335: 467 */     this.optionsPanel.add(p3, gbc);
/*  336: 468 */     this.optionsPanel.add(p2, gbc);
/*  337:     */     
/*  338: 470 */     this.m_fastScroll.setSelected(false);
/*  339: 471 */     this.m_fastScroll.addActionListener(new ActionListener()
/*  340:     */     {
/*  341:     */       public void actionPerformed(ActionEvent e)
/*  342:     */       {
/*  343: 474 */         if (!MatrixPanel.this.m_fastScroll.isSelected()) {
/*  344: 475 */           MatrixPanel.this.m_osi = null;
/*  345:     */         } else {
/*  346: 477 */           MatrixPanel.this.m_plottedCells = new boolean[MatrixPanel.this.m_selectedAttribs.length][MatrixPanel.this.m_selectedAttribs.length];
/*  347:     */         }
/*  348: 480 */         MatrixPanel.this.invalidate();
/*  349: 481 */         MatrixPanel.this.repaint();
/*  350:     */       }
/*  351: 484 */     });
/*  352: 485 */     addComponentListener(new ComponentAdapter()
/*  353:     */     {
/*  354:     */       public void componentResized(ComponentEvent cv)
/*  355:     */       {
/*  356: 488 */         MatrixPanel.this.m_js.setMinimumSize(new Dimension(MatrixPanel.this.getWidth(), MatrixPanel.this.getHeight() - MatrixPanel.this.optionsPanel.getPreferredSize().height - 10));
/*  357:     */         
/*  358:     */ 
/*  359: 491 */         MatrixPanel.this.jp.setDividerLocation(MatrixPanel.this.getHeight() - MatrixPanel.this.optionsPanel.getPreferredSize().height - 10);
/*  360:     */       }
/*  361: 495 */     });
/*  362: 496 */     this.optionsPanel.setMinimumSize(new Dimension(0, 0));
/*  363: 497 */     this.jp = new JSplitPane(0, this.m_js, this.optionsPanel);
/*  364: 498 */     this.jp.setOneTouchExpandable(true);
/*  365: 499 */     this.jp.setResizeWeight(1.0D);
/*  366: 500 */     setLayout(new BorderLayout());
/*  367: 501 */     add(this.jp, "Center");
/*  368: 504 */     for (int i = 0; i < m_defaultColors.length; i++) {
/*  369: 505 */       this.m_colorList.add(m_defaultColors[i]);
/*  370:     */     }
/*  371: 509 */     this.m_selectedAttribs = this.m_attribList.getSelectedIndices();
/*  372: 510 */     this.m_plotsPanel = new Plot();
/*  373: 511 */     this.m_plotsPanel.setLayout(null);
/*  374: 512 */     this.m_js.getHorizontalScrollBar().setUnitIncrement(10);
/*  375: 513 */     this.m_js.getVerticalScrollBar().setUnitIncrement(10);
/*  376: 514 */     this.m_js.setViewportView(this.m_plotsPanel);
/*  377: 515 */     this.m_js.setColumnHeaderView(this.m_plotsPanel.getColHeader());
/*  378: 516 */     this.m_js.setRowHeaderView(this.m_plotsPanel.getRowHeader());
/*  379: 517 */     JLabel lb = new JLabel(" Plot Matrix");
/*  380: 518 */     lb.setFont(this.f);
/*  381: 519 */     lb.setForeground(this.fontColor);
/*  382: 520 */     lb.setHorizontalTextPosition(0);
/*  383: 521 */     this.m_js.setCorner("UPPER_LEFT_CORNER", lb);
/*  384: 522 */     this.m_cp.setInstances(this.m_data);
/*  385: 523 */     this.m_cp.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
/*  386: 524 */     this.m_cp.addRepaintNotify(this.m_plotsPanel);
/*  387:     */   }
/*  388:     */   
/*  389:     */   public void initInternalFields()
/*  390:     */   {
/*  391: 533 */     Instances inst = this.m_data;
/*  392: 534 */     this.m_classIndex = this.m_classAttrib.getSelectedIndex();
/*  393: 535 */     this.m_selectedAttribs = this.m_attribList.getSelectedIndices();
/*  394: 536 */     double minC = 0.0D;double maxC = 0.0D;
/*  395:     */     
/*  396:     */ 
/*  397: 539 */     double currentPercent = Double.parseDouble(this.m_resamplePercent.getText());
/*  398: 540 */     if (currentPercent <= 100.0D)
/*  399:     */     {
/*  400: 541 */       if (currentPercent != this.m_previousPercent) {
/*  401: 542 */         this.m_clearOSIPlottedCells = true;
/*  402:     */       }
/*  403: 544 */       inst = new Instances(this.m_data, 0, this.m_data.numInstances());
/*  404: 545 */       inst.randomize(new Random(Integer.parseInt(this.m_rseed.getText())));
/*  405:     */       
/*  406:     */ 
/*  407:     */ 
/*  408:     */ 
/*  409:     */ 
/*  410:     */ 
/*  411:     */ 
/*  412:     */ 
/*  413: 554 */       inst = new Instances(inst, 0, (int)Math.round(currentPercent / 100.0D * inst.numInstances()));
/*  414:     */       
/*  415:     */ 
/*  416: 557 */       this.m_previousPercent = currentPercent;
/*  417:     */     }
/*  418: 559 */     this.m_points = new int[inst.numInstances()][this.m_selectedAttribs.length];
/*  419: 560 */     this.m_pointColors = new int[inst.numInstances()];
/*  420: 561 */     this.m_missing = new boolean[inst.numInstances()][this.m_selectedAttribs.length + 1];
/*  421: 562 */     this.m_type = new int[2];
/*  422: 563 */     this.jitterVals = new int[inst.numInstances()][2];
/*  423: 568 */     if (!inst.attribute(this.m_classIndex).isNumeric())
/*  424:     */     {
/*  425: 570 */       for (int i = this.m_colorList.size(); i < inst.attribute(this.m_classIndex).numValues() + 1; i++)
/*  426:     */       {
/*  427: 572 */         Color pc = m_defaultColors[(i % 10)];
/*  428: 573 */         int ija = i / 10;
/*  429: 574 */         ija *= 2;
/*  430: 575 */         for (int j = 0; j < ija; j++) {
/*  431: 576 */           pc = pc.darker();
/*  432:     */         }
/*  433: 578 */         this.m_colorList.add(pc);
/*  434:     */       }
/*  435: 581 */       for (int i = 0; i < inst.numInstances(); i++)
/*  436:     */       {
/*  437: 584 */         if (inst.instance(i).isMissing(this.m_classIndex)) {
/*  438: 585 */           this.m_pointColors[i] = (m_defaultColors.length - 1);
/*  439:     */         } else {
/*  440: 587 */           this.m_pointColors[i] = ((int)inst.instance(i).value(this.m_classIndex));
/*  441:     */         }
/*  442: 590 */         this.jitterVals[i][0] = (this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2);
/*  443:     */         
/*  444: 592 */         this.jitterVals[i][1] = (this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2);
/*  445:     */       }
/*  446:     */     }
/*  447:     */     else
/*  448:     */     {
/*  449: 599 */       for (int i = 0; i < inst.numInstances(); i++) {
/*  450: 600 */         if (!inst.instance(i).isMissing(this.m_classIndex))
/*  451:     */         {
/*  452: 601 */           minC = maxC = inst.instance(i).value(this.m_classIndex);
/*  453: 602 */           break;
/*  454:     */         }
/*  455:     */       }
/*  456: 606 */       for (int i = 1; i < inst.numInstances(); i++) {
/*  457: 607 */         if (!inst.instance(i).isMissing(this.m_classIndex))
/*  458:     */         {
/*  459: 608 */           if (minC > inst.instance(i).value(this.m_classIndex)) {
/*  460: 609 */             minC = inst.instance(i).value(this.m_classIndex);
/*  461:     */           }
/*  462: 611 */           if (maxC < inst.instance(i).value(this.m_classIndex)) {
/*  463: 612 */             maxC = inst.instance(i).value(this.m_classIndex);
/*  464:     */           }
/*  465:     */         }
/*  466:     */       }
/*  467: 617 */       for (int i = 0; i < inst.numInstances(); i++)
/*  468:     */       {
/*  469: 618 */         double r = (inst.instance(i).value(this.m_classIndex) - minC) / (maxC - minC);
/*  470:     */         
/*  471: 620 */         r = r * 240.0D + 15.0D;
/*  472: 621 */         this.m_pointColors[i] = ((int)r);
/*  473:     */         
/*  474: 623 */         this.jitterVals[i][0] = (this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2);
/*  475:     */         
/*  476: 625 */         this.jitterVals[i][1] = (this.rnd.nextInt(this.m_jitter.getValue() + 1) - this.m_jitter.getValue() / 2);
/*  477:     */       }
/*  478:     */     }
/*  479: 631 */     double[] min = new double[this.m_selectedAttribs.length];double max = 0.0D;
/*  480: 632 */     double[] ratio = new double[this.m_selectedAttribs.length];
/*  481: 633 */     double cellSize = this.m_plotSize.getValue();double temp1 = 0.0D;double temp2 = 0.0D;
/*  482: 635 */     for (int j = 0; j < this.m_selectedAttribs.length; j++)
/*  483:     */     {
/*  484: 637 */       for (int i = 0; i < inst.numInstances(); i++)
/*  485:     */       {
/*  486: 638 */         double tmp829_828 = 0.0D;max = tmp829_828;min[j] = tmp829_828;
/*  487: 639 */         if (!inst.instance(i).isMissing(this.m_selectedAttribs[j]))
/*  488:     */         {
/*  489: 640 */           double tmp876_871 = inst.instance(i).value(this.m_selectedAttribs[j]);max = tmp876_871;min[j] = tmp876_871;
/*  490: 641 */           break;
/*  491:     */         }
/*  492:     */       }
/*  493: 644 */       for (; i < inst.numInstances(); i++) {
/*  494: 645 */         if (!inst.instance(i).isMissing(this.m_selectedAttribs[j]))
/*  495:     */         {
/*  496: 646 */           if (inst.instance(i).value(this.m_selectedAttribs[j]) < min[j]) {
/*  497: 647 */             min[j] = inst.instance(i).value(this.m_selectedAttribs[j]);
/*  498:     */           }
/*  499: 649 */           if (inst.instance(i).value(this.m_selectedAttribs[j]) > max) {
/*  500: 650 */             max = inst.instance(i).value(this.m_selectedAttribs[j]);
/*  501:     */           }
/*  502:     */         }
/*  503:     */       }
/*  504: 654 */       ratio[j] = (cellSize / (max - min[j]));
/*  505:     */     }
/*  506: 657 */     boolean classIndexProcessed = false;
/*  507: 658 */     for (int j = 0; j < this.m_selectedAttribs.length; j++) {
/*  508: 659 */       if ((inst.attribute(this.m_selectedAttribs[j]).isNominal()) || (inst.attribute(this.m_selectedAttribs[j]).isString()))
/*  509:     */       {
/*  510: 664 */         temp1 = cellSize / inst.attribute(this.m_selectedAttribs[j]).numValues();
/*  511: 665 */         temp2 = temp1 / 2.0D;
/*  512: 666 */         for (int i = 0; i < inst.numInstances(); i++)
/*  513:     */         {
/*  514: 667 */           this.m_points[i][j] = ((int)Math.round(temp2 + temp1 * inst.instance(i).value(this.m_selectedAttribs[j])));
/*  515: 670 */           if (inst.instance(i).isMissing(this.m_selectedAttribs[j]))
/*  516:     */           {
/*  517: 671 */             this.m_missing[i][j] = 1;
/*  518: 672 */             if (this.m_selectedAttribs[j] == this.m_classIndex)
/*  519:     */             {
/*  520: 673 */               this.m_missing[i][(this.m_missing[0].length - 1)] = 1;
/*  521: 674 */               classIndexProcessed = true;
/*  522:     */             }
/*  523:     */           }
/*  524:     */         }
/*  525:     */       }
/*  526:     */       else
/*  527:     */       {
/*  528: 680 */         for (int i = 0; i < inst.numInstances(); i++)
/*  529:     */         {
/*  530: 681 */           this.m_points[i][j] = ((int)Math.round((inst.instance(i).value(this.m_selectedAttribs[j]) - min[j]) * ratio[j]));
/*  531: 685 */           if (inst.instance(i).isMissing(this.m_selectedAttribs[j]))
/*  532:     */           {
/*  533: 686 */             this.m_missing[i][j] = 1;
/*  534: 687 */             if (this.m_selectedAttribs[j] == this.m_classIndex)
/*  535:     */             {
/*  536: 688 */               this.m_missing[i][(this.m_missing[0].length - 1)] = 1;
/*  537: 689 */               classIndexProcessed = true;
/*  538:     */             }
/*  539:     */           }
/*  540:     */         }
/*  541:     */       }
/*  542:     */     }
/*  543: 696 */     if ((inst.attribute(this.m_classIndex).isNominal()) || (inst.attribute(this.m_classIndex).isString()))
/*  544:     */     {
/*  545: 698 */       this.m_type[0] = 1;
/*  546: 699 */       this.m_type[1] = inst.attribute(this.m_classIndex).numValues();
/*  547:     */     }
/*  548:     */     else
/*  549:     */     {
/*  550: 701 */       int tmp1446_1445 = 0;this.m_type[1] = tmp1446_1445;this.m_type[0] = tmp1446_1445;
/*  551:     */     }
/*  552: 704 */     if (!classIndexProcessed) {
/*  553: 707 */       for (int i = 0; i < inst.numInstances(); i++) {
/*  554: 708 */         if (inst.instance(i).isMissing(this.m_classIndex)) {
/*  555: 709 */           this.m_missing[i][(this.m_missing[0].length - 1)] = 1;
/*  556:     */         }
/*  557:     */       }
/*  558:     */     }
/*  559: 714 */     this.m_cp.setColours(this.m_colorList);
/*  560:     */   }
/*  561:     */   
/*  562:     */   public void setupAttribLists()
/*  563:     */   {
/*  564: 721 */     String[] tempAttribNames = new String[this.m_data.numAttributes()];
/*  565:     */     
/*  566:     */ 
/*  567: 724 */     this.m_classAttrib.removeAllItems();
/*  568: 725 */     for (int i = 0; i < tempAttribNames.length; i++)
/*  569:     */     {
/*  570: 726 */       String type = " (" + Attribute.typeToStringShort(this.m_data.attribute(i)) + ")";
/*  571: 727 */       tempAttribNames[i] = new String("Colour: " + this.m_data.attribute(i).name() + " " + type);
/*  572:     */       
/*  573: 729 */       this.m_classAttrib.addItem(tempAttribNames[i]);
/*  574:     */     }
/*  575: 731 */     if (this.m_data.classIndex() == -1) {
/*  576: 732 */       this.m_classAttrib.setSelectedIndex(tempAttribNames.length - 1);
/*  577:     */     } else {
/*  578: 734 */       this.m_classAttrib.setSelectedIndex(this.m_data.classIndex());
/*  579:     */     }
/*  580: 736 */     this.m_attribList.setListData(tempAttribNames);
/*  581: 737 */     this.m_attribList.setSelectionInterval(0, tempAttribNames.length - 1);
/*  582:     */   }
/*  583:     */   
/*  584:     */   public void setPercent()
/*  585:     */   {
/*  586: 744 */     if (this.m_data.numInstances() > 700)
/*  587:     */     {
/*  588: 745 */       double percnt = 500.0D / this.m_data.numInstances() * 100.0D;
/*  589: 746 */       percnt *= 100.0D;
/*  590: 747 */       percnt = Math.round(percnt);
/*  591: 748 */       percnt /= 100.0D;
/*  592:     */       
/*  593: 750 */       this.m_resamplePercent.setText("" + percnt);
/*  594:     */     }
/*  595:     */     else
/*  596:     */     {
/*  597: 752 */       this.m_resamplePercent.setText("100");
/*  598:     */     }
/*  599:     */   }
/*  600:     */   
/*  601:     */   public void setInstances(Instances newInst)
/*  602:     */   {
/*  603: 765 */     this.m_osi = null;
/*  604: 766 */     this.m_fastScroll.setSelected(false);
/*  605: 767 */     this.m_data = newInst;
/*  606: 768 */     setPercent();
/*  607: 769 */     setupAttribLists();
/*  608: 770 */     this.m_rseed.setText("1");
/*  609: 771 */     initInternalFields();
/*  610: 772 */     this.m_cp.setInstances(this.m_data);
/*  611: 773 */     this.m_cp.setCindex(this.m_classIndex);
/*  612: 774 */     this.m_updateBt.doClick();
/*  613:     */   }
/*  614:     */   
/*  615:     */   public static void main(String[] args)
/*  616:     */   {
/*  617: 781 */     JFrame jf = new JFrame("Weka Explorer: MatrixPanel");
/*  618: 782 */     JButton setBt = new JButton("Set Instances");
/*  619: 783 */     Instances data = null;
/*  620:     */     try
/*  621:     */     {
/*  622: 785 */       if (args.length == 1)
/*  623:     */       {
/*  624: 786 */         data = new Instances(new BufferedReader(new FileReader(args[0])));
/*  625:     */       }
/*  626:     */       else
/*  627:     */       {
/*  628: 788 */         System.out.println("Usage: MatrixPanel <arff file>");
/*  629: 789 */         System.exit(-1);
/*  630:     */       }
/*  631:     */     }
/*  632:     */     catch (IOException ex)
/*  633:     */     {
/*  634: 792 */       ex.printStackTrace();
/*  635: 793 */       System.exit(-1);
/*  636:     */     }
/*  637: 796 */     final MatrixPanel mp = new MatrixPanel();
/*  638: 797 */     mp.setInstances(data);
/*  639: 798 */     setBt.addActionListener(new ActionListener()
/*  640:     */     {
/*  641:     */       public void actionPerformed(ActionEvent e)
/*  642:     */       {
/*  643: 801 */         JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  644:     */         
/*  645: 803 */         ExtensionFileFilter myfilter = new ExtensionFileFilter("arff", "Arff data files");
/*  646:     */         
/*  647: 805 */         chooser.setFileFilter(myfilter);
/*  648: 806 */         int returnVal = chooser.showOpenDialog(this.val$jf);
/*  649: 808 */         if (returnVal == 0) {
/*  650:     */           try
/*  651:     */           {
/*  652: 810 */             System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
/*  653:     */             
/*  654: 812 */             Instances in = new Instances(new FileReader(chooser.getSelectedFile().getAbsolutePath()));
/*  655:     */             
/*  656:     */ 
/*  657: 815 */             mp.setInstances(in);
/*  658:     */           }
/*  659:     */           catch (Exception ex)
/*  660:     */           {
/*  661: 817 */             ex.printStackTrace();
/*  662:     */           }
/*  663:     */         }
/*  664:     */       }
/*  665: 827 */     });
/*  666: 828 */     jf.getContentPane().setLayout(new BorderLayout());
/*  667: 829 */     jf.getContentPane().add(mp, "Center");
/*  668: 830 */     jf.getContentPane().add(setBt, "South");
/*  669: 831 */     jf.getContentPane().setFont(new Font("SansSerif", 0, 11));
/*  670:     */     
/*  671: 833 */     jf.setDefaultCloseOperation(3);
/*  672: 834 */     jf.setSize(800, 600);
/*  673: 835 */     jf.setVisible(true);
/*  674: 836 */     jf.repaint();
/*  675:     */   }
/*  676:     */   
/*  677:     */   private class Plot
/*  678:     */     extends JPanel
/*  679:     */     implements MouseMotionListener, MouseListener
/*  680:     */   {
/*  681:     */     JPanel jPlRowHeader;
/*  682:     */     JPanel jPlColHeader;
/*  683:     */     int lastypos;
/*  684:     */     int lastxpos;
/*  685:     */     FontMetrics fm;
/*  686:     */     Rectangle r;
/*  687: 850 */     int cellRange = 100;
/*  688: 850 */     int cellSize = 100;
/*  689: 850 */     int intpad = 4;
/*  690: 850 */     int extpad = 3;
/*  691:     */     private static final long serialVersionUID = -1721245738439420882L;
/*  692:     */     
/*  693:     */     public Plot()
/*  694:     */     {
/*  695: 861 */       setToolTipText("blah");
/*  696: 862 */       addMouseMotionListener(this);
/*  697: 863 */       addMouseListener(this);
/*  698: 864 */       initialize();
/*  699:     */     }
/*  700:     */     
/*  701:     */     public void initialize()
/*  702:     */     {
/*  703: 869 */       this.lastxpos = (this.lastypos = 0);
/*  704: 870 */       this.cellRange = this.cellSize;
/*  705: 871 */       this.cellSize = (this.cellRange + 2 * this.intpad);
/*  706:     */       
/*  707: 873 */       this.jPlColHeader = new JPanel()
/*  708:     */       {
/*  709:     */         private static final long serialVersionUID = -9098547751937467506L;
/*  710:     */         Rectangle r;
/*  711:     */         
/*  712:     */         public void paint(Graphics g)
/*  713:     */         {
/*  714: 879 */           this.r = g.getClipBounds();
/*  715: 880 */           g.setColor(getBackground());
/*  716: 881 */           g.fillRect(this.r.x, this.r.y, this.r.width, this.r.height);
/*  717: 882 */           g.setFont(MatrixPanel.this.f);
/*  718: 883 */           MatrixPanel.Plot.this.fm = g.getFontMetrics();
/*  719: 884 */           int xpos = 0;int ypos = 0;int attribWidth = 0;
/*  720:     */           
/*  721: 886 */           g.setColor(MatrixPanel.this.fontColor);
/*  722: 887 */           xpos = MatrixPanel.Plot.this.extpad;
/*  723: 888 */           ypos = MatrixPanel.Plot.this.extpad + MatrixPanel.Plot.this.fm.getHeight();
/*  724: 890 */           for (int m_selectedAttrib : MatrixPanel.this.m_selectedAttribs) {
/*  725: 891 */             if (xpos + MatrixPanel.Plot.this.cellSize < this.r.x)
/*  726:     */             {
/*  727: 892 */               xpos += MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad;
/*  728:     */             }
/*  729:     */             else
/*  730:     */             {
/*  731: 894 */               if (xpos > this.r.x + this.r.width) {
/*  732:     */                 break;
/*  733:     */               }
/*  734: 897 */               attribWidth = MatrixPanel.Plot.this.fm.stringWidth(MatrixPanel.this.m_data.attribute(m_selectedAttrib).name());
/*  735:     */               
/*  736: 899 */               g.drawString(MatrixPanel.this.m_data.attribute(m_selectedAttrib).name(), attribWidth < MatrixPanel.Plot.this.cellSize ? xpos + (MatrixPanel.Plot.this.cellSize / 2 - attribWidth / 2) : xpos, ypos);
/*  737:     */               
/*  738:     */ 
/*  739:     */ 
/*  740:     */ 
/*  741: 904 */               xpos += MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad;
/*  742:     */             }
/*  743:     */           }
/*  744: 906 */           MatrixPanel.Plot.this.fm = null;
/*  745: 907 */           this.r = null;
/*  746:     */         }
/*  747:     */         
/*  748:     */         public Dimension getPreferredSize()
/*  749:     */         {
/*  750: 912 */           MatrixPanel.Plot.this.fm = getFontMetrics(getFont());
/*  751: 913 */           return new Dimension(MatrixPanel.this.m_selectedAttribs.length * (MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad), 2 * MatrixPanel.Plot.this.extpad + MatrixPanel.Plot.this.fm.getHeight());
/*  752:     */         }
/*  753: 917 */       };
/*  754: 918 */       this.jPlRowHeader = new JPanel()
/*  755:     */       {
/*  756:     */         private static final long serialVersionUID = 8474957069309552844L;
/*  757:     */         Rectangle r;
/*  758:     */         
/*  759:     */         public void paint(Graphics g)
/*  760:     */         {
/*  761: 925 */           this.r = g.getClipBounds();
/*  762: 926 */           g.setColor(getBackground());
/*  763: 927 */           g.fillRect(this.r.x, this.r.y, this.r.width, this.r.height);
/*  764: 928 */           g.setFont(MatrixPanel.this.f);
/*  765: 929 */           MatrixPanel.Plot.this.fm = g.getFontMetrics();
/*  766: 930 */           int xpos = 0;int ypos = 0;
/*  767:     */           
/*  768: 932 */           g.setColor(MatrixPanel.this.fontColor);
/*  769: 933 */           xpos = MatrixPanel.Plot.this.extpad;
/*  770: 934 */           ypos = MatrixPanel.Plot.this.extpad;
/*  771: 936 */           for (int j = MatrixPanel.this.m_selectedAttribs.length - 1; j >= 0; j--) {
/*  772: 937 */             if (ypos + MatrixPanel.Plot.this.cellSize < this.r.y)
/*  773:     */             {
/*  774: 938 */               ypos += MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad;
/*  775:     */             }
/*  776:     */             else
/*  777:     */             {
/*  778: 940 */               if (ypos > this.r.y + this.r.height) {
/*  779:     */                 break;
/*  780:     */               }
/*  781: 943 */               g.drawString(MatrixPanel.this.m_data.attribute(MatrixPanel.this.m_selectedAttribs[j]).name(), xpos + MatrixPanel.Plot.this.extpad, ypos + MatrixPanel.Plot.this.cellSize / 2);
/*  782:     */               
/*  783:     */ 
/*  784: 946 */               xpos = MatrixPanel.Plot.this.extpad;
/*  785: 947 */               ypos += MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad;
/*  786:     */             }
/*  787:     */           }
/*  788: 949 */           this.r = null;
/*  789:     */         }
/*  790:     */         
/*  791:     */         public Dimension getPreferredSize()
/*  792:     */         {
/*  793: 954 */           return new Dimension(100 + MatrixPanel.Plot.this.extpad, MatrixPanel.this.m_selectedAttribs.length * (MatrixPanel.Plot.this.cellSize + MatrixPanel.Plot.this.extpad));
/*  794:     */         }
/*  795: 957 */       };
/*  796: 958 */       this.jPlColHeader.setFont(MatrixPanel.this.f);
/*  797: 959 */       this.jPlRowHeader.setFont(MatrixPanel.this.f);
/*  798: 960 */       setFont(MatrixPanel.this.f);
/*  799:     */     }
/*  800:     */     
/*  801:     */     public JPanel getRowHeader()
/*  802:     */     {
/*  803: 964 */       return this.jPlRowHeader;
/*  804:     */     }
/*  805:     */     
/*  806:     */     public JPanel getColHeader()
/*  807:     */     {
/*  808: 968 */       return this.jPlColHeader;
/*  809:     */     }
/*  810:     */     
/*  811:     */     public void mouseMoved(MouseEvent e)
/*  812:     */     {
/*  813: 973 */       Graphics g = getGraphics();
/*  814: 974 */       int xpos = this.extpad;int ypos = this.extpad;
/*  815: 976 */       for (int j = MatrixPanel.this.m_selectedAttribs.length - 1; j >= 0; j--)
/*  816:     */       {
/*  817: 978 */         for (int m_selectedAttrib : MatrixPanel.this.m_selectedAttribs)
/*  818:     */         {
/*  819: 979 */           if ((e.getX() >= xpos) && (e.getX() <= xpos + this.cellSize + this.extpad) && 
/*  820: 980 */             (e.getY() >= ypos) && (e.getY() <= ypos + this.cellSize + this.extpad))
/*  821:     */           {
/*  822: 981 */             if ((xpos != this.lastxpos) || (ypos != this.lastypos))
/*  823:     */             {
/*  824: 982 */               g.setColor(Color.red);
/*  825: 983 */               g.drawRect(xpos - 1, ypos - 1, this.cellSize + 1, this.cellSize + 1);
/*  826: 984 */               if ((this.lastxpos != 0) && (this.lastypos != 0))
/*  827:     */               {
/*  828: 985 */                 g.setColor(getBackground().darker());
/*  829: 986 */                 g.drawRect(this.lastxpos - 1, this.lastypos - 1, this.cellSize + 1, this.cellSize + 1);
/*  830:     */               }
/*  831: 989 */               this.lastxpos = xpos;
/*  832: 990 */               this.lastypos = ypos;
/*  833:     */             }
/*  834: 992 */             return;
/*  835:     */           }
/*  836: 995 */           xpos += this.cellSize + this.extpad;
/*  837:     */         }
/*  838: 997 */         xpos = this.extpad;
/*  839: 998 */         ypos += this.cellSize + this.extpad;
/*  840:     */       }
/*  841:1000 */       if ((this.lastxpos != 0) && (this.lastypos != 0))
/*  842:     */       {
/*  843:1001 */         g.setColor(getBackground().darker());
/*  844:1002 */         g.drawRect(this.lastxpos - 1, this.lastypos - 1, this.cellSize + 1, this.cellSize + 1);
/*  845:     */       }
/*  846:1004 */       this.lastxpos = (this.lastypos = 0);
/*  847:     */     }
/*  848:     */     
/*  849:     */     public void mouseDragged(MouseEvent e) {}
/*  850:     */     
/*  851:     */     public void mouseClicked(MouseEvent e)
/*  852:     */     {
/*  853:1013 */       int i = 0;int j = 0;int found = 0;
/*  854:     */       
/*  855:1015 */       int xpos = this.extpad;int ypos = this.extpad;
/*  856:1016 */       for (j = MatrixPanel.this.m_selectedAttribs.length - 1; j >= 0; j--)
/*  857:     */       {
/*  858:1017 */         for (i = 0; i < MatrixPanel.this.m_selectedAttribs.length; i++)
/*  859:     */         {
/*  860:1018 */           if ((e.getX() >= xpos) && (e.getX() <= xpos + this.cellSize + this.extpad) && 
/*  861:1019 */             (e.getY() >= ypos) && (e.getY() <= ypos + this.cellSize + this.extpad))
/*  862:     */           {
/*  863:1020 */             found = 1;
/*  864:1021 */             break;
/*  865:     */           }
/*  866:1024 */           xpos += this.cellSize + this.extpad;
/*  867:     */         }
/*  868:1026 */         if (found == 1) {
/*  869:     */           break;
/*  870:     */         }
/*  871:1029 */         xpos = this.extpad;
/*  872:1030 */         ypos += this.cellSize + this.extpad;
/*  873:     */       }
/*  874:1032 */       if (found == 0) {
/*  875:1033 */         return;
/*  876:     */       }
/*  877:1036 */       JFrame jf = new JFrame("Weka Explorer: Visualizing " + MatrixPanel.this.m_data.relationName());
/*  878:     */       
/*  879:1038 */       VisualizePanel vp = new VisualizePanel();
/*  880:     */       try
/*  881:     */       {
/*  882:1040 */         PlotData2D pd = new PlotData2D(MatrixPanel.this.m_data);
/*  883:1041 */         pd.setPlotName("Master Plot");
/*  884:1042 */         vp.setMasterPlot(pd);
/*  885:     */         
/*  886:1044 */         vp.setXIndex(MatrixPanel.this.m_selectedAttribs[i]);
/*  887:1045 */         vp.setYIndex(MatrixPanel.this.m_selectedAttribs[j]);
/*  888:1046 */         vp.m_ColourCombo.setSelectedIndex(MatrixPanel.this.m_classIndex);
/*  889:1047 */         if (MatrixPanel.this.m_settings != null) {
/*  890:1048 */           vp.applySettings(MatrixPanel.this.m_settings, MatrixPanel.this.m_settingsOwnerID);
/*  891:     */         }
/*  892:     */       }
/*  893:     */       catch (Exception ex)
/*  894:     */       {
/*  895:1051 */         ex.printStackTrace();
/*  896:     */       }
/*  897:1053 */       jf.getContentPane().add(vp);
/*  898:1054 */       jf.setSize(800, 600);
/*  899:1055 */       jf.setVisible(true);
/*  900:     */     }
/*  901:     */     
/*  902:     */     public void mouseEntered(MouseEvent e) {}
/*  903:     */     
/*  904:     */     public void mouseExited(MouseEvent e) {}
/*  905:     */     
/*  906:     */     public void mousePressed(MouseEvent e) {}
/*  907:     */     
/*  908:     */     public void mouseReleased(MouseEvent e) {}
/*  909:     */     
/*  910:     */     public void setJitter(int newjitter) {}
/*  911:     */     
/*  912:     */     public void setCellSize(int newCellSize)
/*  913:     */     {
/*  914:1084 */       this.cellSize = newCellSize;
/*  915:1085 */       initialize();
/*  916:     */     }
/*  917:     */     
/*  918:     */     public String getToolTipText(MouseEvent event)
/*  919:     */     {
/*  920:1093 */       int xpos = this.extpad;int ypos = this.extpad;
/*  921:1095 */       for (int j = MatrixPanel.this.m_selectedAttribs.length - 1; j >= 0; j--)
/*  922:     */       {
/*  923:1096 */         for (int m_selectedAttrib : MatrixPanel.this.m_selectedAttribs)
/*  924:     */         {
/*  925:1097 */           if ((event.getX() >= xpos) && (event.getX() <= xpos + this.cellSize + this.extpad) && 
/*  926:1098 */             (event.getY() >= ypos) && (event.getY() <= ypos + this.cellSize + this.extpad)) {
/*  927:1100 */             return "X: " + MatrixPanel.this.m_data.attribute(m_selectedAttrib).name() + " Y: " + MatrixPanel.this.m_data.attribute(MatrixPanel.this.m_selectedAttribs[j]).name() + " (click to enlarge)";
/*  928:     */           }
/*  929:1104 */           xpos += this.cellSize + this.extpad;
/*  930:     */         }
/*  931:1106 */         xpos = this.extpad;
/*  932:1107 */         ypos += this.cellSize + this.extpad;
/*  933:     */       }
/*  934:1109 */       return "Matrix Panel";
/*  935:     */     }
/*  936:     */     
/*  937:     */     public void paintGraph(Graphics g, int xattrib, int yattrib, int xpos, int ypos)
/*  938:     */     {
/*  939:1119 */       g.setColor(MatrixPanel.this.m_backgroundColor.equals(Color.BLACK) ? MatrixPanel.this.m_backgroundColor.brighter().brighter() : MatrixPanel.this.m_backgroundColor.darker().darker());
/*  940:     */       
/*  941:1121 */       g.drawRect(xpos - 1, ypos - 1, this.cellSize + 1, this.cellSize + 1);
/*  942:1122 */       g.setColor(MatrixPanel.this.m_backgroundColor);
/*  943:1123 */       g.fillRect(xpos, ypos, this.cellSize, this.cellSize);
/*  944:1124 */       for (int i = 0; i < MatrixPanel.this.m_points.length; i++) {
/*  945:1126 */         if ((MatrixPanel.this.m_missing[i][yattrib] == 0) && (MatrixPanel.this.m_missing[i][xattrib] == 0))
/*  946:     */         {
/*  947:1128 */           if (MatrixPanel.this.m_type[0] == 0)
/*  948:     */           {
/*  949:1129 */             if (MatrixPanel.this.m_missing[i][(MatrixPanel.this.m_missing[0].length - 1)] != 0) {
/*  950:1130 */               g.setColor(MatrixPanel.m_defaultColors[(MatrixPanel.m_defaultColors.length - 1)]);
/*  951:     */             } else {
/*  952:1132 */               g.setColor(new Color(MatrixPanel.this.m_pointColors[i], 150, 255 - MatrixPanel.this.m_pointColors[i]));
/*  953:     */             }
/*  954:     */           }
/*  955:     */           else {
/*  956:1136 */             g.setColor((Color)MatrixPanel.this.m_colorList.get(MatrixPanel.this.m_pointColors[i]));
/*  957:     */           }
/*  958:     */           int y;
/*  959:     */           int x;
/*  960:     */           int y;
/*  961:1139 */           if ((MatrixPanel.this.m_points[i][xattrib] + MatrixPanel.this.jitterVals[i][0] < 0) || (MatrixPanel.this.m_points[i][xattrib] + MatrixPanel.this.jitterVals[i][0] > this.cellRange))
/*  962:     */           {
/*  963:     */             int y;
/*  964:1141 */             if ((this.cellRange - MatrixPanel.this.m_points[i][yattrib] + MatrixPanel.this.jitterVals[i][1] < 0) || (this.cellRange - MatrixPanel.this.m_points[i][yattrib] + MatrixPanel.this.jitterVals[i][1] > this.cellRange))
/*  965:     */             {
/*  966:1144 */               int x = this.intpad + MatrixPanel.this.m_points[i][xattrib];
/*  967:1145 */               y = this.intpad + (this.cellRange - MatrixPanel.this.m_points[i][yattrib]);
/*  968:     */             }
/*  969:     */             else
/*  970:     */             {
/*  971:1148 */               int x = this.intpad + MatrixPanel.this.m_points[i][xattrib];
/*  972:1149 */               y = this.intpad + (this.cellRange - MatrixPanel.this.m_points[i][yattrib]) + MatrixPanel.this.jitterVals[i][1];
/*  973:     */             }
/*  974:     */           }
/*  975:     */           else
/*  976:     */           {
/*  977:     */             int y;
/*  978:1152 */             if ((this.cellRange - MatrixPanel.this.m_points[i][yattrib] + MatrixPanel.this.jitterVals[i][1] < 0) || (this.cellRange - MatrixPanel.this.m_points[i][yattrib] + MatrixPanel.this.jitterVals[i][1] > this.cellRange))
/*  979:     */             {
/*  980:1155 */               int x = this.intpad + MatrixPanel.this.m_points[i][xattrib] + MatrixPanel.this.jitterVals[i][0];
/*  981:1156 */               y = this.intpad + (this.cellRange - MatrixPanel.this.m_points[i][yattrib]);
/*  982:     */             }
/*  983:     */             else
/*  984:     */             {
/*  985:1159 */               x = this.intpad + MatrixPanel.this.m_points[i][xattrib] + MatrixPanel.this.jitterVals[i][0];
/*  986:1160 */               y = this.intpad + (this.cellRange - MatrixPanel.this.m_points[i][yattrib]) + MatrixPanel.this.jitterVals[i][1];
/*  987:     */             }
/*  988:     */           }
/*  989:1162 */           if (MatrixPanel.this.datapointSize == 1) {
/*  990:1163 */             g.drawLine(x + xpos, y + ypos, x + xpos, y + ypos);
/*  991:     */           } else {
/*  992:1165 */             g.drawOval(x + xpos - MatrixPanel.this.datapointSize / 2, y + ypos - MatrixPanel.this.datapointSize / 2, MatrixPanel.this.datapointSize, MatrixPanel.this.datapointSize);
/*  993:     */           }
/*  994:     */         }
/*  995:     */       }
/*  996:1170 */       g.setColor(MatrixPanel.this.fontColor);
/*  997:     */     }
/*  998:     */     
/*  999:     */     private void createOSI()
/* 1000:     */     {
/* 1001:1174 */       int iwidth = getWidth();
/* 1002:1175 */       int iheight = getHeight();
/* 1003:1176 */       MatrixPanel.this.m_osi = createImage(iwidth, iheight);
/* 1004:1177 */       clearOSI();
/* 1005:     */     }
/* 1006:     */     
/* 1007:     */     private void clearOSI()
/* 1008:     */     {
/* 1009:1181 */       if (MatrixPanel.this.m_osi == null) {
/* 1010:1182 */         return;
/* 1011:     */       }
/* 1012:1185 */       int iwidth = getWidth();
/* 1013:1186 */       int iheight = getHeight();
/* 1014:1187 */       Graphics m = MatrixPanel.this.m_osi.getGraphics();
/* 1015:1188 */       m.setColor(getBackground().darker().darker());
/* 1016:1189 */       m.fillRect(0, 0, iwidth, iheight);
/* 1017:     */     }
/* 1018:     */     
/* 1019:     */     public void paintME(Graphics g)
/* 1020:     */     {
/* 1021:1196 */       Graphics g2 = g;
/* 1022:1197 */       if ((MatrixPanel.this.m_osi == null) && (MatrixPanel.this.m_fastScroll.isSelected())) {
/* 1023:1198 */         createOSI();
/* 1024:     */       }
/* 1025:1200 */       if ((MatrixPanel.this.m_osi != null) && (MatrixPanel.this.m_fastScroll.isSelected())) {
/* 1026:1201 */         g2 = MatrixPanel.this.m_osi.getGraphics();
/* 1027:     */       }
/* 1028:1203 */       this.r = g.getClipBounds();
/* 1029:     */       
/* 1030:1205 */       g.setColor(getBackground());
/* 1031:1206 */       g.fillRect(this.r.x, this.r.y, this.r.width, this.r.height);
/* 1032:1207 */       g.setColor(MatrixPanel.this.fontColor);
/* 1033:     */       
/* 1034:1209 */       int xpos = 0;int ypos = 0;
/* 1035:     */       
/* 1036:1211 */       xpos = this.extpad;
/* 1037:1212 */       ypos = this.extpad;
/* 1038:1214 */       for (int j = MatrixPanel.this.m_selectedAttribs.length - 1; j >= 0; j--) {
/* 1039:1215 */         if (ypos + this.cellSize < this.r.y)
/* 1040:     */         {
/* 1041:1216 */           ypos += this.cellSize + this.extpad;
/* 1042:     */         }
/* 1043:     */         else
/* 1044:     */         {
/* 1045:1218 */           if (ypos > this.r.y + this.r.height) {
/* 1046:     */             break;
/* 1047:     */           }
/* 1048:1221 */           for (int i = 0; i < MatrixPanel.this.m_selectedAttribs.length; i++) {
/* 1049:1222 */             if (xpos + this.cellSize < this.r.x)
/* 1050:     */             {
/* 1051:1223 */               xpos += this.cellSize + this.extpad;
/* 1052:     */             }
/* 1053:     */             else
/* 1054:     */             {
/* 1055:1225 */               if (xpos > this.r.x + this.r.width) {
/* 1056:     */                 break;
/* 1057:     */               }
/* 1058:1227 */               if (MatrixPanel.this.m_fastScroll.isSelected())
/* 1059:     */               {
/* 1060:1228 */                 if (MatrixPanel.this.m_plottedCells[i][j] == 0)
/* 1061:     */                 {
/* 1062:1229 */                   paintGraph(g2, i, j, xpos, ypos);
/* 1063:     */                   
/* 1064:     */ 
/* 1065:1232 */                   MatrixPanel.this.m_plottedCells[i][j] = 1;
/* 1066:     */                 }
/* 1067:     */               }
/* 1068:     */               else {
/* 1069:1235 */                 paintGraph(g2, i, j, xpos, ypos);
/* 1070:     */               }
/* 1071:1237 */               xpos += this.cellSize + this.extpad;
/* 1072:     */             }
/* 1073:     */           }
/* 1074:1240 */           xpos = this.extpad;
/* 1075:1241 */           ypos += this.cellSize + this.extpad;
/* 1076:     */         }
/* 1077:     */       }
/* 1078:     */     }
/* 1079:     */     
/* 1080:     */     public void paintComponent(Graphics g)
/* 1081:     */     {
/* 1082:1250 */       paintME(g);
/* 1083:1251 */       if ((MatrixPanel.this.m_osi != null) && (MatrixPanel.this.m_fastScroll.isSelected())) {
/* 1084:1252 */         g.drawImage(MatrixPanel.this.m_osi, 0, 0, this);
/* 1085:     */       }
/* 1086:     */     }
/* 1087:     */   }
/* 1088:     */   
/* 1089:     */   public void setPointSize(int pointSize)
/* 1090:     */   {
/* 1091:1263 */     if ((pointSize <= this.m_pointSize.getMaximum()) && (pointSize > this.m_pointSize.getMinimum())) {
/* 1092:1265 */       this.m_pointSize.setValue(pointSize);
/* 1093:     */     }
/* 1094:     */   }
/* 1095:     */   
/* 1096:     */   public void setPlotSize(int plotSize)
/* 1097:     */   {
/* 1098:1275 */     if ((plotSize >= this.m_plotSize.getMinimum()) && (plotSize <= this.m_plotSize.getMaximum())) {
/* 1099:1277 */       this.m_plotSize.setValue(plotSize);
/* 1100:     */     }
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public void setPlotBackgroundColour(Color c)
/* 1104:     */   {
/* 1105:1287 */     this.m_backgroundColor = c;
/* 1106:     */   }
/* 1107:     */   
/* 1108:     */   public void applySettings(Settings settings, String ownerID)
/* 1109:     */   {
/* 1110:1295 */     this.m_settings = settings;
/* 1111:1296 */     this.m_settingsOwnerID = ownerID;
/* 1112:     */     
/* 1113:1298 */     setPointSize(((Integer)settings.getSetting(ownerID, VisualizePanel.ScatterDefaults.POINT_SIZE_KEY, Integer.valueOf(1), Environment.getSystemWide())).intValue());
/* 1114:     */     
/* 1115:     */ 
/* 1116:     */ 
/* 1117:     */ 
/* 1118:1303 */     setPlotSize(((Integer)settings.getSetting(ownerID, VisualizePanel.ScatterDefaults.PLOT_SIZE_KEY, Integer.valueOf(100), Environment.getSystemWide())).intValue());
/* 1119:     */     
/* 1120:     */ 
/* 1121:     */ 
/* 1122:     */ 
/* 1123:1308 */     setPlotBackgroundColour((Color)settings.getSetting(ownerID, VisualizeUtils.VisualizeDefaults.BACKGROUND_COLOUR_KEY, VisualizeUtils.VisualizeDefaults.BACKGROUND_COLOR, Environment.getSystemWide()));
/* 1124:     */   }
/* 1125:     */   
/* 1126:     */   public void updatePanel()
/* 1127:     */   {
/* 1128:1320 */     initInternalFields();
/* 1129:     */     
/* 1130:1322 */     Plot a = this.m_plotsPanel;
/* 1131:1323 */     a.setCellSize(this.m_plotSize.getValue());
/* 1132:1324 */     Dimension d = new Dimension(this.m_selectedAttribs.length * (a.cellSize + a.extpad) + 2, this.m_selectedAttribs.length * (a.cellSize + a.extpad) + 2);
/* 1133:     */     
/* 1134:     */ 
/* 1135:     */ 
/* 1136:     */ 
/* 1137:     */ 
/* 1138:1330 */     a.setPreferredSize(d);
/* 1139:1331 */     a.setSize(a.getPreferredSize());
/* 1140:1332 */     a.setJitter(this.m_jitter.getValue());
/* 1141:1334 */     if ((this.m_fastScroll.isSelected()) && (this.m_clearOSIPlottedCells))
/* 1142:     */     {
/* 1143:1335 */       this.m_plottedCells = new boolean[this.m_selectedAttribs.length][this.m_selectedAttribs.length];
/* 1144:     */       
/* 1145:1337 */       this.m_clearOSIPlottedCells = false;
/* 1146:     */     }
/* 1147:1340 */     if (this.m_regenerateOSI) {
/* 1148:1341 */       this.m_osi = null;
/* 1149:     */     }
/* 1150:1343 */     this.m_js.revalidate();
/* 1151:1344 */     this.m_cp.setColours(this.m_colorList);
/* 1152:1345 */     this.m_cp.setCindex(this.m_classIndex);
/* 1153:1346 */     this.m_regenerateOSI = false;
/* 1154:     */     
/* 1155:1348 */     repaint();
/* 1156:     */   }
/* 1157:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.MatrixPanel
 * JD-Core Version:    0.7.0.1
 */