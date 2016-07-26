/*    1:     */ package weka.gui.treevisualizer;
/*    2:     */ 
/*    3:     */ import java.awt.Color;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Dimension;
/*    6:     */ import java.awt.Font;
/*    7:     */ import java.awt.FontMetrics;
/*    8:     */ import java.awt.Graphics;
/*    9:     */ import java.awt.Graphics2D;
/*   10:     */ import java.awt.event.ActionEvent;
/*   11:     */ import java.awt.event.ActionListener;
/*   12:     */ import java.awt.event.ItemEvent;
/*   13:     */ import java.awt.event.ItemListener;
/*   14:     */ import java.awt.event.MouseEvent;
/*   15:     */ import java.awt.event.MouseListener;
/*   16:     */ import java.awt.event.MouseMotionListener;
/*   17:     */ import java.io.FileReader;
/*   18:     */ import java.io.IOException;
/*   19:     */ import java.io.PrintStream;
/*   20:     */ import java.io.StringReader;
/*   21:     */ import java.util.Properties;
/*   22:     */ import javax.swing.BorderFactory;
/*   23:     */ import javax.swing.ButtonGroup;
/*   24:     */ import javax.swing.JFrame;
/*   25:     */ import javax.swing.JMenu;
/*   26:     */ import javax.swing.JMenuItem;
/*   27:     */ import javax.swing.JOptionPane;
/*   28:     */ import javax.swing.JPopupMenu;
/*   29:     */ import javax.swing.JRadioButtonMenuItem;
/*   30:     */ import javax.swing.Timer;
/*   31:     */ import weka.core.Instances;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.core.logging.Logger;
/*   34:     */ import weka.core.logging.Logger.Level;
/*   35:     */ import weka.gui.visualize.PrintablePanel;
/*   36:     */ import weka.gui.visualize.VisualizePanel;
/*   37:     */ import weka.gui.visualize.VisualizeUtils;
/*   38:     */ 
/*   39:     */ public class TreeVisualizer
/*   40:     */   extends PrintablePanel
/*   41:     */   implements MouseMotionListener, MouseListener, ActionListener, ItemListener
/*   42:     */ {
/*   43:     */   private static final long serialVersionUID = -8668637962504080749L;
/*   44:     */   public static final String PROPERTIES_FILE = "weka/gui/treevisualizer/TreeVisualizer.props";
/*   45:     */   private final NodePlace m_placer;
/*   46:     */   private final Node m_topNode;
/*   47:     */   private final Dimension m_viewPos;
/*   48:     */   private final Dimension m_viewSize;
/*   49:     */   private Font m_currentFont;
/*   50:     */   private FontMetrics m_fontSize;
/*   51:     */   private final int m_numNodes;
/*   52:     */   private final int m_numLevels;
/*   53:     */   private final NodeInfo[] m_nodes;
/*   54:     */   private final EdgeInfo[] m_edges;
/*   55:     */   private final Timer m_frameLimiter;
/*   56:     */   private int m_mouseState;
/*   57:     */   private final Dimension m_oldMousePos;
/*   58:     */   private final Dimension m_newMousePos;
/*   59:     */   private boolean m_clickAvailable;
/*   60:     */   private final Dimension m_nViewPos;
/*   61:     */   private final Dimension m_nViewSize;
/*   62:     */   private int m_scaling;
/*   63:     */   private final JPopupMenu m_winMenu;
/*   64:     */   private final JMenuItem m_topN;
/*   65:     */   private final JMenuItem m_fitToScreen;
/*   66:     */   private final JMenuItem m_autoScale;
/*   67:     */   private final JMenu m_selectFont;
/*   68:     */   private final ButtonGroup m_selectFontGroup;
/*   69:     */   private final JRadioButtonMenuItem m_size24;
/*   70:     */   private final JRadioButtonMenuItem m_size22;
/*   71:     */   private final JRadioButtonMenuItem m_size20;
/*   72:     */   private final JRadioButtonMenuItem m_size18;
/*   73:     */   private final JRadioButtonMenuItem m_size16;
/*   74:     */   private final JRadioButtonMenuItem m_size14;
/*   75:     */   private final JRadioButtonMenuItem m_size12;
/*   76:     */   private final JRadioButtonMenuItem m_size10;
/*   77:     */   private final JRadioButtonMenuItem m_size8;
/*   78:     */   private final JRadioButtonMenuItem m_size6;
/*   79:     */   private final JRadioButtonMenuItem m_size4;
/*   80:     */   private final JRadioButtonMenuItem m_size2;
/*   81:     */   private final JRadioButtonMenuItem m_size1;
/*   82:     */   private final JMenuItem m_accept;
/*   83:     */   private final JPopupMenu m_nodeMenu;
/*   84:     */   private final JMenuItem m_visualise;
/*   85:     */   private JMenuItem m_remChildren;
/*   86:     */   private JMenuItem m_classifyChild;
/*   87:     */   private JMenuItem m_sendInstances;
/*   88:     */   private int m_focusNode;
/*   89:     */   private int m_highlightNode;
/*   90:     */   private final TreeDisplayListener m_listener;
/*   91: 270 */   protected Color m_FontColor = null;
/*   92: 273 */   protected Color m_BackgroundColor = null;
/*   93: 276 */   protected Color m_NodeColor = null;
/*   94: 279 */   protected Color m_LineColor = null;
/*   95: 282 */   protected Color m_ZoomBoxColor = null;
/*   96: 285 */   protected Color m_ZoomBoxXORColor = null;
/*   97: 288 */   protected boolean m_ShowBorder = true;
/*   98:     */   
/*   99:     */   public TreeVisualizer(TreeDisplayListener tdl, String dot, NodePlace p)
/*  100:     */   {
/*  101: 305 */     initialize();
/*  102: 308 */     if (this.m_ShowBorder) {
/*  103: 309 */       setBorder(BorderFactory.createTitledBorder("Tree View"));
/*  104:     */     }
/*  105: 311 */     this.m_listener = tdl;
/*  106:     */     
/*  107: 313 */     TreeBuild builder = new TreeBuild();
/*  108:     */     
/*  109: 315 */     Node n = null;
/*  110:     */     
/*  111: 317 */     n = builder.create(new StringReader(dot));
/*  112:     */     
/*  113:     */ 
/*  114: 320 */     this.m_highlightNode = 5;
/*  115: 321 */     this.m_topNode = n;
/*  116: 322 */     this.m_placer = p;
/*  117: 323 */     this.m_placer.place(this.m_topNode);
/*  118: 324 */     this.m_viewPos = new Dimension(0, 0);
/*  119: 325 */     this.m_viewSize = new Dimension(800, 600);
/*  120:     */     
/*  121:     */ 
/*  122:     */ 
/*  123: 329 */     this.m_nViewPos = new Dimension(0, 0);
/*  124: 330 */     this.m_nViewSize = new Dimension(800, 600);
/*  125:     */     
/*  126: 332 */     this.m_scaling = 0;
/*  127:     */     
/*  128: 334 */     this.m_numNodes = Node.getCount(this.m_topNode, 0);
/*  129:     */     
/*  130:     */ 
/*  131:     */ 
/*  132: 338 */     this.m_numLevels = Node.getHeight(this.m_topNode, 0);
/*  133:     */     
/*  134: 340 */     this.m_nodes = new NodeInfo[this.m_numNodes];
/*  135: 341 */     this.m_edges = new EdgeInfo[this.m_numNodes - 1];
/*  136:     */     
/*  137: 343 */     arrayFill(this.m_topNode, this.m_nodes, this.m_edges);
/*  138:     */     
/*  139: 345 */     changeFontSize(12);
/*  140:     */     
/*  141: 347 */     this.m_mouseState = 0;
/*  142: 348 */     this.m_oldMousePos = new Dimension(0, 0);
/*  143: 349 */     this.m_newMousePos = new Dimension(0, 0);
/*  144: 350 */     this.m_frameLimiter = new Timer(120, this);
/*  145:     */     
/*  146: 352 */     this.m_winMenu = new JPopupMenu();
/*  147: 353 */     this.m_topN = new JMenuItem("Center on Top Node");
/*  148:     */     
/*  149: 355 */     this.m_topN.setActionCommand("Center on Top Node");
/*  150:     */     
/*  151: 357 */     this.m_fitToScreen = new JMenuItem("Fit to Screen");
/*  152: 358 */     this.m_fitToScreen.setActionCommand("Fit to Screen");
/*  153:     */     
/*  154: 360 */     this.m_selectFont = new JMenu("Select Font");
/*  155: 361 */     this.m_selectFont.setActionCommand("Select Font");
/*  156: 362 */     this.m_autoScale = new JMenuItem("Auto Scale");
/*  157: 363 */     this.m_autoScale.setActionCommand("Auto Scale");
/*  158: 364 */     this.m_selectFontGroup = new ButtonGroup();
/*  159:     */     
/*  160: 366 */     this.m_accept = new JMenuItem("Accept The Tree");
/*  161: 367 */     this.m_accept.setActionCommand("Accept The Tree");
/*  162:     */     
/*  163: 369 */     this.m_winMenu.add(this.m_topN);
/*  164: 370 */     this.m_winMenu.addSeparator();
/*  165: 371 */     this.m_winMenu.add(this.m_fitToScreen);
/*  166: 372 */     this.m_winMenu.add(this.m_autoScale);
/*  167:     */     
/*  168:     */ 
/*  169: 375 */     this.m_winMenu.addSeparator();
/*  170: 376 */     this.m_winMenu.add(this.m_selectFont);
/*  171: 378 */     if (this.m_listener != null)
/*  172:     */     {
/*  173: 379 */       this.m_winMenu.addSeparator();
/*  174: 380 */       this.m_winMenu.add(this.m_accept);
/*  175:     */     }
/*  176: 383 */     this.m_topN.addActionListener(this);
/*  177: 384 */     this.m_fitToScreen.addActionListener(this);
/*  178:     */     
/*  179: 386 */     this.m_autoScale.addActionListener(this);
/*  180: 387 */     this.m_accept.addActionListener(this);
/*  181:     */     
/*  182: 389 */     this.m_size24 = new JRadioButtonMenuItem("Size 24", false);
/*  183: 390 */     this.m_size22 = new JRadioButtonMenuItem("Size 22", false);
/*  184: 391 */     this.m_size20 = new JRadioButtonMenuItem("Size 20", false);
/*  185: 392 */     this.m_size18 = new JRadioButtonMenuItem("Size 18", false);
/*  186: 393 */     this.m_size16 = new JRadioButtonMenuItem("Size 16", false);
/*  187: 394 */     this.m_size14 = new JRadioButtonMenuItem("Size 14", false);
/*  188: 395 */     this.m_size12 = new JRadioButtonMenuItem("Size 12", true);
/*  189: 396 */     this.m_size10 = new JRadioButtonMenuItem("Size 10", false);
/*  190: 397 */     this.m_size8 = new JRadioButtonMenuItem("Size 8", false);
/*  191: 398 */     this.m_size6 = new JRadioButtonMenuItem("Size 6", false);
/*  192: 399 */     this.m_size4 = new JRadioButtonMenuItem("Size 4", false);
/*  193: 400 */     this.m_size2 = new JRadioButtonMenuItem("Size 2", false);
/*  194: 401 */     this.m_size1 = new JRadioButtonMenuItem("Size 1", false);
/*  195:     */     
/*  196: 403 */     this.m_size24.setActionCommand("Size 24");
/*  197: 404 */     this.m_size22.setActionCommand("Size 22");
/*  198: 405 */     this.m_size20.setActionCommand("Size 20");
/*  199: 406 */     this.m_size18.setActionCommand("Size 18");
/*  200: 407 */     this.m_size16.setActionCommand("Size 16");
/*  201: 408 */     this.m_size14.setActionCommand("Size 14");
/*  202: 409 */     this.m_size12.setActionCommand("Size 12");
/*  203: 410 */     this.m_size10.setActionCommand("Size 10");
/*  204: 411 */     this.m_size8.setActionCommand("Size 8");
/*  205: 412 */     this.m_size6.setActionCommand("Size 6");
/*  206: 413 */     this.m_size4.setActionCommand("Size 4");
/*  207: 414 */     this.m_size2.setActionCommand("Size 2");
/*  208: 415 */     this.m_size1.setActionCommand("Size 1");
/*  209:     */     
/*  210: 417 */     this.m_selectFontGroup.add(this.m_size24);
/*  211: 418 */     this.m_selectFontGroup.add(this.m_size22);
/*  212: 419 */     this.m_selectFontGroup.add(this.m_size20);
/*  213: 420 */     this.m_selectFontGroup.add(this.m_size18);
/*  214: 421 */     this.m_selectFontGroup.add(this.m_size16);
/*  215: 422 */     this.m_selectFontGroup.add(this.m_size14);
/*  216: 423 */     this.m_selectFontGroup.add(this.m_size12);
/*  217: 424 */     this.m_selectFontGroup.add(this.m_size10);
/*  218: 425 */     this.m_selectFontGroup.add(this.m_size8);
/*  219: 426 */     this.m_selectFontGroup.add(this.m_size6);
/*  220: 427 */     this.m_selectFontGroup.add(this.m_size4);
/*  221: 428 */     this.m_selectFontGroup.add(this.m_size2);
/*  222: 429 */     this.m_selectFontGroup.add(this.m_size1);
/*  223:     */     
/*  224: 431 */     this.m_selectFont.add(this.m_size24);
/*  225: 432 */     this.m_selectFont.add(this.m_size22);
/*  226: 433 */     this.m_selectFont.add(this.m_size20);
/*  227: 434 */     this.m_selectFont.add(this.m_size18);
/*  228: 435 */     this.m_selectFont.add(this.m_size16);
/*  229: 436 */     this.m_selectFont.add(this.m_size14);
/*  230: 437 */     this.m_selectFont.add(this.m_size12);
/*  231: 438 */     this.m_selectFont.add(this.m_size10);
/*  232: 439 */     this.m_selectFont.add(this.m_size8);
/*  233: 440 */     this.m_selectFont.add(this.m_size6);
/*  234: 441 */     this.m_selectFont.add(this.m_size4);
/*  235: 442 */     this.m_selectFont.add(this.m_size2);
/*  236: 443 */     this.m_selectFont.add(this.m_size1);
/*  237:     */     
/*  238: 445 */     this.m_size24.addItemListener(this);
/*  239: 446 */     this.m_size22.addItemListener(this);
/*  240: 447 */     this.m_size20.addItemListener(this);
/*  241: 448 */     this.m_size18.addItemListener(this);
/*  242: 449 */     this.m_size16.addItemListener(this);
/*  243: 450 */     this.m_size14.addItemListener(this);
/*  244: 451 */     this.m_size12.addItemListener(this);
/*  245: 452 */     this.m_size10.addItemListener(this);
/*  246: 453 */     this.m_size8.addItemListener(this);
/*  247: 454 */     this.m_size6.addItemListener(this);
/*  248: 455 */     this.m_size4.addItemListener(this);
/*  249: 456 */     this.m_size2.addItemListener(this);
/*  250: 457 */     this.m_size1.addItemListener(this);
/*  251:     */     
/*  252:     */ 
/*  253:     */ 
/*  254:     */ 
/*  255:     */ 
/*  256:     */ 
/*  257:     */ 
/*  258:     */ 
/*  259:     */ 
/*  260:     */ 
/*  261:     */ 
/*  262:     */ 
/*  263:     */ 
/*  264:     */ 
/*  265:     */ 
/*  266:     */ 
/*  267:     */ 
/*  268:     */ 
/*  269:     */ 
/*  270:     */ 
/*  271: 478 */     this.m_nodeMenu = new JPopupMenu();
/*  272:     */     
/*  273: 480 */     this.m_visualise = new JMenuItem("Visualize The Node");
/*  274: 481 */     this.m_visualise.setActionCommand("Visualize The Node");
/*  275: 482 */     this.m_visualise.addActionListener(this);
/*  276: 483 */     this.m_nodeMenu.add(this.m_visualise);
/*  277: 485 */     if (this.m_listener != null)
/*  278:     */     {
/*  279: 486 */       this.m_remChildren = new JMenuItem("Remove Child Nodes");
/*  280: 487 */       this.m_remChildren.setActionCommand("Remove Child Nodes");
/*  281: 488 */       this.m_remChildren.addActionListener(this);
/*  282: 489 */       this.m_nodeMenu.add(this.m_remChildren);
/*  283:     */       
/*  284: 491 */       this.m_classifyChild = new JMenuItem("Use Classifier...");
/*  285: 492 */       this.m_classifyChild.setActionCommand("classify_child");
/*  286: 493 */       this.m_classifyChild.addActionListener(this);
/*  287: 494 */       this.m_nodeMenu.add(this.m_classifyChild);
/*  288:     */     }
/*  289: 505 */     this.m_focusNode = -1;
/*  290: 506 */     this.m_highlightNode = -1;
/*  291:     */     
/*  292: 508 */     addMouseMotionListener(this);
/*  293: 509 */     addMouseListener(this);
/*  294:     */     
/*  295:     */ 
/*  296: 512 */     this.m_frameLimiter.setRepeats(false);
/*  297: 513 */     this.m_frameLimiter.start();
/*  298:     */   }
/*  299:     */   
/*  300:     */   public TreeVisualizer(TreeDisplayListener tdl, Node n, NodePlace p)
/*  301:     */   {
/*  302: 527 */     initialize();
/*  303: 530 */     if (this.m_ShowBorder) {
/*  304: 531 */       setBorder(BorderFactory.createTitledBorder("Tree View"));
/*  305:     */     }
/*  306: 533 */     this.m_listener = tdl;
/*  307: 534 */     this.m_topNode = n;
/*  308: 535 */     this.m_placer = p;
/*  309: 536 */     this.m_placer.place(this.m_topNode);
/*  310: 537 */     this.m_viewPos = new Dimension(0, 0);
/*  311: 538 */     this.m_viewSize = new Dimension(800, 600);
/*  312:     */     
/*  313:     */ 
/*  314:     */ 
/*  315: 542 */     this.m_nViewPos = new Dimension(0, 0);
/*  316: 543 */     this.m_nViewSize = new Dimension(800, 600);
/*  317:     */     
/*  318: 545 */     this.m_scaling = 0;
/*  319:     */     
/*  320: 547 */     this.m_numNodes = Node.getCount(this.m_topNode, 0);
/*  321:     */     
/*  322:     */ 
/*  323:     */ 
/*  324: 551 */     this.m_numLevels = Node.getHeight(this.m_topNode, 0);
/*  325:     */     
/*  326: 553 */     this.m_nodes = new NodeInfo[this.m_numNodes];
/*  327: 554 */     this.m_edges = new EdgeInfo[this.m_numNodes - 1];
/*  328:     */     
/*  329: 556 */     arrayFill(this.m_topNode, this.m_nodes, this.m_edges);
/*  330:     */     
/*  331: 558 */     changeFontSize(12);
/*  332:     */     
/*  333: 560 */     this.m_mouseState = 0;
/*  334: 561 */     this.m_oldMousePos = new Dimension(0, 0);
/*  335: 562 */     this.m_newMousePos = new Dimension(0, 0);
/*  336: 563 */     this.m_frameLimiter = new Timer(120, this);
/*  337:     */     
/*  338: 565 */     this.m_winMenu = new JPopupMenu();
/*  339: 566 */     this.m_topN = new JMenuItem("Center on Top Node");
/*  340:     */     
/*  341: 568 */     this.m_topN.setActionCommand("Center on Top Node");
/*  342:     */     
/*  343: 570 */     this.m_fitToScreen = new JMenuItem("Fit to Screen");
/*  344: 571 */     this.m_fitToScreen.setActionCommand("Fit to Screen");
/*  345:     */     
/*  346: 573 */     this.m_selectFont = new JMenu("Select Font");
/*  347: 574 */     this.m_selectFont.setActionCommand("Select Font");
/*  348: 575 */     this.m_autoScale = new JMenuItem("Auto Scale");
/*  349: 576 */     this.m_autoScale.setActionCommand("Auto Scale");
/*  350: 577 */     this.m_selectFontGroup = new ButtonGroup();
/*  351:     */     
/*  352: 579 */     this.m_accept = new JMenuItem("Accept The Tree");
/*  353: 580 */     this.m_accept.setActionCommand("Accept The Tree");
/*  354:     */     
/*  355: 582 */     this.m_winMenu.add(this.m_topN);
/*  356: 583 */     this.m_winMenu.addSeparator();
/*  357: 584 */     this.m_winMenu.add(this.m_fitToScreen);
/*  358: 585 */     this.m_winMenu.add(this.m_autoScale);
/*  359: 586 */     this.m_winMenu.addSeparator();
/*  360:     */     
/*  361: 588 */     this.m_winMenu.addSeparator();
/*  362: 589 */     this.m_winMenu.add(this.m_selectFont);
/*  363: 590 */     this.m_winMenu.addSeparator();
/*  364: 592 */     if (this.m_listener != null) {
/*  365: 593 */       this.m_winMenu.add(this.m_accept);
/*  366:     */     }
/*  367: 596 */     this.m_topN.addActionListener(this);
/*  368: 597 */     this.m_fitToScreen.addActionListener(this);
/*  369:     */     
/*  370: 599 */     this.m_autoScale.addActionListener(this);
/*  371: 600 */     this.m_accept.addActionListener(this);
/*  372:     */     
/*  373: 602 */     this.m_size24 = new JRadioButtonMenuItem("Size 24", false);
/*  374: 603 */     this.m_size22 = new JRadioButtonMenuItem("Size 22", false);
/*  375: 604 */     this.m_size20 = new JRadioButtonMenuItem("Size 20", false);
/*  376: 605 */     this.m_size18 = new JRadioButtonMenuItem("Size 18", false);
/*  377: 606 */     this.m_size16 = new JRadioButtonMenuItem("Size 16", false);
/*  378: 607 */     this.m_size14 = new JRadioButtonMenuItem("Size 14", false);
/*  379: 608 */     this.m_size12 = new JRadioButtonMenuItem("Size 12", true);
/*  380: 609 */     this.m_size10 = new JRadioButtonMenuItem("Size 10", false);
/*  381: 610 */     this.m_size8 = new JRadioButtonMenuItem("Size 8", false);
/*  382: 611 */     this.m_size6 = new JRadioButtonMenuItem("Size 6", false);
/*  383: 612 */     this.m_size4 = new JRadioButtonMenuItem("Size 4", false);
/*  384: 613 */     this.m_size2 = new JRadioButtonMenuItem("Size 2", false);
/*  385: 614 */     this.m_size1 = new JRadioButtonMenuItem("Size 1", false);
/*  386:     */     
/*  387: 616 */     this.m_size24.setActionCommand("Size 24");
/*  388: 617 */     this.m_size22.setActionCommand("Size 22");
/*  389: 618 */     this.m_size20.setActionCommand("Size 20");
/*  390: 619 */     this.m_size18.setActionCommand("Size 18");
/*  391: 620 */     this.m_size16.setActionCommand("Size 16");
/*  392: 621 */     this.m_size14.setActionCommand("Size 14");
/*  393: 622 */     this.m_size12.setActionCommand("Size 12");
/*  394: 623 */     this.m_size10.setActionCommand("Size 10");
/*  395: 624 */     this.m_size8.setActionCommand("Size 8");
/*  396: 625 */     this.m_size6.setActionCommand("Size 6");
/*  397: 626 */     this.m_size4.setActionCommand("Size 4");
/*  398: 627 */     this.m_size2.setActionCommand("Size 2");
/*  399: 628 */     this.m_size1.setActionCommand("Size 1");
/*  400:     */     
/*  401: 630 */     this.m_selectFontGroup.add(this.m_size24);
/*  402: 631 */     this.m_selectFontGroup.add(this.m_size22);
/*  403: 632 */     this.m_selectFontGroup.add(this.m_size20);
/*  404: 633 */     this.m_selectFontGroup.add(this.m_size18);
/*  405: 634 */     this.m_selectFontGroup.add(this.m_size16);
/*  406: 635 */     this.m_selectFontGroup.add(this.m_size14);
/*  407: 636 */     this.m_selectFontGroup.add(this.m_size12);
/*  408: 637 */     this.m_selectFontGroup.add(this.m_size10);
/*  409: 638 */     this.m_selectFontGroup.add(this.m_size8);
/*  410: 639 */     this.m_selectFontGroup.add(this.m_size6);
/*  411: 640 */     this.m_selectFontGroup.add(this.m_size4);
/*  412: 641 */     this.m_selectFontGroup.add(this.m_size2);
/*  413: 642 */     this.m_selectFontGroup.add(this.m_size1);
/*  414:     */     
/*  415: 644 */     this.m_selectFont.add(this.m_size24);
/*  416: 645 */     this.m_selectFont.add(this.m_size22);
/*  417: 646 */     this.m_selectFont.add(this.m_size20);
/*  418: 647 */     this.m_selectFont.add(this.m_size18);
/*  419: 648 */     this.m_selectFont.add(this.m_size16);
/*  420: 649 */     this.m_selectFont.add(this.m_size14);
/*  421: 650 */     this.m_selectFont.add(this.m_size12);
/*  422: 651 */     this.m_selectFont.add(this.m_size10);
/*  423: 652 */     this.m_selectFont.add(this.m_size8);
/*  424: 653 */     this.m_selectFont.add(this.m_size6);
/*  425: 654 */     this.m_selectFont.add(this.m_size4);
/*  426: 655 */     this.m_selectFont.add(this.m_size2);
/*  427: 656 */     this.m_selectFont.add(this.m_size1);
/*  428:     */     
/*  429: 658 */     this.m_size24.addItemListener(this);
/*  430: 659 */     this.m_size22.addItemListener(this);
/*  431: 660 */     this.m_size20.addItemListener(this);
/*  432: 661 */     this.m_size18.addItemListener(this);
/*  433: 662 */     this.m_size16.addItemListener(this);
/*  434: 663 */     this.m_size14.addItemListener(this);
/*  435: 664 */     this.m_size12.addItemListener(this);
/*  436: 665 */     this.m_size10.addItemListener(this);
/*  437: 666 */     this.m_size8.addItemListener(this);
/*  438: 667 */     this.m_size6.addItemListener(this);
/*  439: 668 */     this.m_size4.addItemListener(this);
/*  440: 669 */     this.m_size2.addItemListener(this);
/*  441: 670 */     this.m_size1.addItemListener(this);
/*  442:     */     
/*  443:     */ 
/*  444:     */ 
/*  445:     */ 
/*  446:     */ 
/*  447:     */ 
/*  448:     */ 
/*  449:     */ 
/*  450:     */ 
/*  451:     */ 
/*  452:     */ 
/*  453:     */ 
/*  454:     */ 
/*  455:     */ 
/*  456:     */ 
/*  457:     */ 
/*  458:     */ 
/*  459:     */ 
/*  460:     */ 
/*  461:     */ 
/*  462: 691 */     this.m_nodeMenu = new JPopupMenu();
/*  463:     */     
/*  464: 693 */     this.m_visualise = new JMenuItem("Visualize The Node");
/*  465: 694 */     this.m_visualise.setActionCommand("Visualize The Node");
/*  466: 695 */     this.m_visualise.addActionListener(this);
/*  467: 696 */     this.m_nodeMenu.add(this.m_visualise);
/*  468: 698 */     if (this.m_listener != null)
/*  469:     */     {
/*  470: 699 */       this.m_remChildren = new JMenuItem("Remove Child Nodes");
/*  471: 700 */       this.m_remChildren.setActionCommand("Remove Child Nodes");
/*  472: 701 */       this.m_remChildren.addActionListener(this);
/*  473: 702 */       this.m_nodeMenu.add(this.m_remChildren);
/*  474:     */       
/*  475: 704 */       this.m_classifyChild = new JMenuItem("Use Classifier...");
/*  476: 705 */       this.m_classifyChild.setActionCommand("classify_child");
/*  477: 706 */       this.m_classifyChild.addActionListener(this);
/*  478: 707 */       this.m_nodeMenu.add(this.m_classifyChild);
/*  479:     */       
/*  480: 709 */       this.m_sendInstances = new JMenuItem("Add Instances To Viewer");
/*  481: 710 */       this.m_sendInstances.setActionCommand("send_instances");
/*  482: 711 */       this.m_sendInstances.addActionListener(this);
/*  483: 712 */       this.m_nodeMenu.add(this.m_sendInstances);
/*  484:     */     }
/*  485: 716 */     this.m_focusNode = -1;
/*  486: 717 */     this.m_highlightNode = -1;
/*  487:     */     
/*  488: 719 */     addMouseMotionListener(this);
/*  489: 720 */     addMouseListener(this);
/*  490:     */     
/*  491:     */ 
/*  492:     */ 
/*  493:     */ 
/*  494: 725 */     this.m_frameLimiter.setRepeats(false);
/*  495: 726 */     this.m_frameLimiter.start();
/*  496:     */   }
/*  497:     */   
/*  498:     */   protected Color getColor(String colorStr)
/*  499:     */   {
/*  500: 738 */     Color result = null;
/*  501: 740 */     if ((colorStr != null) && (colorStr.length() > 0)) {
/*  502: 741 */       result = VisualizeUtils.processColour(colorStr, result);
/*  503:     */     }
/*  504: 744 */     return result;
/*  505:     */   }
/*  506:     */   
/*  507:     */   protected void initialize()
/*  508:     */   {
/*  509:     */     Properties props;
/*  510:     */     try
/*  511:     */     {
/*  512: 754 */       props = Utils.readProperties("weka/gui/treevisualizer/TreeVisualizer.props");
/*  513:     */     }
/*  514:     */     catch (Exception e)
/*  515:     */     {
/*  516: 756 */       e.printStackTrace();
/*  517: 757 */       props = new Properties();
/*  518:     */     }
/*  519: 760 */     this.m_FontColor = getColor(props.getProperty("FontColor", ""));
/*  520: 761 */     this.m_BackgroundColor = getColor(props.getProperty("BackgroundColor", ""));
/*  521: 762 */     this.m_NodeColor = getColor(props.getProperty("NodeColor", ""));
/*  522: 763 */     this.m_LineColor = getColor(props.getProperty("LineColor", ""));
/*  523: 764 */     this.m_ZoomBoxColor = getColor(props.getProperty("ZoomBoxColor", ""));
/*  524: 765 */     this.m_ZoomBoxXORColor = getColor(props.getProperty("ZoomBoxXORColor", ""));
/*  525: 766 */     this.m_ShowBorder = Boolean.parseBoolean(props.getProperty("ShowBorder", "true"));
/*  526:     */   }
/*  527:     */   
/*  528:     */   public void fitToScreen()
/*  529:     */   {
/*  530: 776 */     getScreenFit(this.m_viewPos, this.m_viewSize);
/*  531: 777 */     repaint();
/*  532:     */   }
/*  533:     */   
/*  534:     */   private void getScreenFit(Dimension np, Dimension ns)
/*  535:     */   {
/*  536: 785 */     int leftmost = 1000000;int rightmost = -1000000;
/*  537: 786 */     int leftCenter = 1000000;int rightCenter = -1000000;int rightNode = 0;
/*  538: 787 */     int highest = -1000000;int highTop = -1000000;
/*  539: 788 */     for (int noa = 0; noa < this.m_numNodes; noa++)
/*  540:     */     {
/*  541: 789 */       calcScreenCoords(noa);
/*  542: 790 */       if (this.m_nodes[noa].m_center - this.m_nodes[noa].m_side < leftmost) {
/*  543: 791 */         leftmost = this.m_nodes[noa].m_center - this.m_nodes[noa].m_side;
/*  544:     */       }
/*  545: 793 */       if (this.m_nodes[noa].m_center < leftCenter) {
/*  546: 794 */         leftCenter = this.m_nodes[noa].m_center;
/*  547:     */       }
/*  548: 797 */       if (this.m_nodes[noa].m_center + this.m_nodes[noa].m_side > rightmost) {
/*  549: 798 */         rightmost = this.m_nodes[noa].m_center + this.m_nodes[noa].m_side;
/*  550:     */       }
/*  551: 800 */       if (this.m_nodes[noa].m_center > rightCenter)
/*  552:     */       {
/*  553: 801 */         rightCenter = this.m_nodes[noa].m_center;
/*  554: 802 */         rightNode = noa;
/*  555:     */       }
/*  556: 804 */       if (this.m_nodes[noa].m_top + this.m_nodes[noa].m_height > highest) {
/*  557: 805 */         highest = this.m_nodes[noa].m_top + this.m_nodes[noa].m_height;
/*  558:     */       }
/*  559: 807 */       if (this.m_nodes[noa].m_top > highTop) {
/*  560: 808 */         highTop = this.m_nodes[noa].m_top;
/*  561:     */       }
/*  562:     */     }
/*  563: 812 */     ns.width = getWidth();
/*  564: 813 */     ns.width -= leftCenter - leftmost + rightmost - rightCenter + 30;
/*  565: 814 */     ns.height = (getHeight() - highest + highTop - 40);
/*  566: 816 */     if ((this.m_nodes[rightNode].m_node.getCenter() != 0.0D) && (leftCenter != rightCenter))
/*  567:     */     {
/*  568: 817 */       Dimension tmp353_352 = ns;tmp353_352.width = ((int)(tmp353_352.width / this.m_nodes[rightNode].m_node.getCenter()));
/*  569:     */     }
/*  570: 819 */     if (ns.width < 10) {
/*  571: 820 */       ns.width = 10;
/*  572:     */     }
/*  573: 822 */     if (ns.height < 10) {
/*  574: 823 */       ns.height = 10;
/*  575:     */     }
/*  576: 826 */     np.width = ((leftCenter - leftmost + rightmost - rightCenter) / 2 + 15);
/*  577: 827 */     np.height = ((highest - highTop) / 2 + 20);
/*  578:     */   }
/*  579:     */   
/*  580:     */   public void actionPerformed(ActionEvent e)
/*  581:     */   {
/*  582: 840 */     if (e.getActionCommand() == null)
/*  583:     */     {
/*  584: 841 */       if (this.m_scaling == 0) {
/*  585: 842 */         repaint();
/*  586:     */       } else {
/*  587: 844 */         animateScaling(this.m_nViewPos, this.m_nViewSize, this.m_scaling);
/*  588:     */       }
/*  589:     */     }
/*  590: 846 */     else if (e.getActionCommand().equals("Fit to Screen"))
/*  591:     */     {
/*  592: 848 */       Dimension np = new Dimension();
/*  593: 849 */       Dimension ns = new Dimension();
/*  594:     */       
/*  595: 851 */       getScreenFit(np, ns);
/*  596:     */       
/*  597: 853 */       animateScaling(np, ns, 10);
/*  598:     */     }
/*  599: 855 */     else if (e.getActionCommand().equals("Center on Top Node"))
/*  600:     */     {
/*  601: 857 */       int tpx = (int)(this.m_topNode.getCenter() * this.m_viewSize.width);
/*  602:     */       
/*  603: 859 */       int tpy = (int)(this.m_topNode.getTop() * this.m_viewSize.height);
/*  604:     */       
/*  605: 861 */       Dimension np = new Dimension(getSize().width / 2 - tpx, getSize().width / 6 - tpy);
/*  606:     */       
/*  607:     */ 
/*  608: 864 */       animateScaling(np, this.m_viewSize, 10);
/*  609:     */     }
/*  610: 866 */     else if (e.getActionCommand().equals("Auto Scale"))
/*  611:     */     {
/*  612: 867 */       autoScale();
/*  613:     */     }
/*  614: 869 */     else if (e.getActionCommand().equals("Visualize The Node"))
/*  615:     */     {
/*  616: 871 */       if (this.m_focusNode >= 0)
/*  617:     */       {
/*  618:     */         Instances inst;
/*  619: 873 */         if ((inst = this.m_nodes[this.m_focusNode].m_node.getInstances()) != null)
/*  620:     */         {
/*  621: 874 */           VisualizePanel pan = new VisualizePanel();
/*  622: 875 */           pan.setInstances(inst);
/*  623: 876 */           JFrame nf = new JFrame();
/*  624: 877 */           nf.setSize(400, 300);
/*  625: 878 */           nf.getContentPane().add(pan);
/*  626: 879 */           nf.setVisible(true);
/*  627:     */         }
/*  628:     */         else
/*  629:     */         {
/*  630: 881 */           JOptionPane.showMessageDialog(this, "Sorry, there is no available Instances data for this Node.", "Sorry!", 2);
/*  631:     */         }
/*  632:     */       }
/*  633:     */       else
/*  634:     */       {
/*  635: 886 */         JOptionPane.showMessageDialog(this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
/*  636:     */       }
/*  637:     */     }
/*  638: 890 */     else if (e.getActionCommand().equals("Create Child Nodes"))
/*  639:     */     {
/*  640: 891 */       if (this.m_focusNode >= 0)
/*  641:     */       {
/*  642: 892 */         if (this.m_listener != null) {
/*  643: 894 */           this.m_listener.userCommand(new TreeDisplayEvent(1, this.m_nodes[this.m_focusNode].m_node.getRefer()));
/*  644:     */         } else {
/*  645: 898 */           JOptionPane.showMessageDialog(this, "Sorry, there is no available Decision Tree to perform this operation on.", "Sorry!", 2);
/*  646:     */         }
/*  647:     */       }
/*  648:     */       else {
/*  649: 903 */         JOptionPane.showMessageDialog(this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
/*  650:     */       }
/*  651:     */     }
/*  652: 907 */     else if (e.getActionCommand().equals("Remove Child Nodes"))
/*  653:     */     {
/*  654: 908 */       if (this.m_focusNode >= 0)
/*  655:     */       {
/*  656: 909 */         if (this.m_listener != null) {
/*  657: 911 */           this.m_listener.userCommand(new TreeDisplayEvent(2, this.m_nodes[this.m_focusNode].m_node.getRefer()));
/*  658:     */         } else {
/*  659: 915 */           JOptionPane.showMessageDialog(this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
/*  660:     */         }
/*  661:     */       }
/*  662:     */       else {
/*  663: 920 */         JOptionPane.showMessageDialog(this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
/*  664:     */       }
/*  665:     */     }
/*  666: 924 */     else if (e.getActionCommand().equals("classify_child"))
/*  667:     */     {
/*  668: 925 */       if (this.m_focusNode >= 0)
/*  669:     */       {
/*  670: 926 */         if (this.m_listener != null) {
/*  671: 928 */           this.m_listener.userCommand(new TreeDisplayEvent(4, this.m_nodes[this.m_focusNode].m_node.getRefer()));
/*  672:     */         } else {
/*  673: 932 */           JOptionPane.showMessageDialog(this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
/*  674:     */         }
/*  675:     */       }
/*  676:     */       else {
/*  677: 937 */         JOptionPane.showMessageDialog(this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
/*  678:     */       }
/*  679:     */     }
/*  680: 941 */     else if (e.getActionCommand().equals("send_instances"))
/*  681:     */     {
/*  682: 942 */       if (this.m_focusNode >= 0)
/*  683:     */       {
/*  684: 943 */         if (this.m_listener != null) {
/*  685: 945 */           this.m_listener.userCommand(new TreeDisplayEvent(5, this.m_nodes[this.m_focusNode].m_node.getRefer()));
/*  686:     */         } else {
/*  687: 949 */           JOptionPane.showMessageDialog(this, "Sorry, there is no available Decsion Tree to perform this operation on.", "Sorry!", 2);
/*  688:     */         }
/*  689:     */       }
/*  690:     */       else {
/*  691: 954 */         JOptionPane.showMessageDialog(this, "Error, there is no selected Node to perform this operation on.", "Error!", 0);
/*  692:     */       }
/*  693:     */     }
/*  694: 958 */     else if (e.getActionCommand().equals("Accept The Tree"))
/*  695:     */     {
/*  696: 959 */       if (this.m_listener != null) {
/*  697: 961 */         this.m_listener.userCommand(new TreeDisplayEvent(3, null));
/*  698:     */       } else {
/*  699: 964 */         JOptionPane.showMessageDialog(this, "Sorry, there is no available Decision Tree to perform this operation on.", "Sorry!", 2);
/*  700:     */       }
/*  701:     */     }
/*  702:     */   }
/*  703:     */   
/*  704:     */   public void itemStateChanged(ItemEvent e)
/*  705:     */   {
/*  706: 978 */     JRadioButtonMenuItem c = (JRadioButtonMenuItem)e.getSource();
/*  707: 979 */     if (c.getActionCommand().equals("Size 24")) {
/*  708: 980 */       changeFontSize(24);
/*  709: 981 */     } else if (c.getActionCommand().equals("Size 22")) {
/*  710: 982 */       changeFontSize(22);
/*  711: 983 */     } else if (c.getActionCommand().equals("Size 20")) {
/*  712: 984 */       changeFontSize(20);
/*  713: 985 */     } else if (c.getActionCommand().equals("Size 18")) {
/*  714: 986 */       changeFontSize(18);
/*  715: 987 */     } else if (c.getActionCommand().equals("Size 16")) {
/*  716: 988 */       changeFontSize(16);
/*  717: 989 */     } else if (c.getActionCommand().equals("Size 14")) {
/*  718: 990 */       changeFontSize(14);
/*  719: 991 */     } else if (c.getActionCommand().equals("Size 12")) {
/*  720: 992 */       changeFontSize(12);
/*  721: 993 */     } else if (c.getActionCommand().equals("Size 10")) {
/*  722: 994 */       changeFontSize(10);
/*  723: 995 */     } else if (c.getActionCommand().equals("Size 8")) {
/*  724: 996 */       changeFontSize(8);
/*  725: 997 */     } else if (c.getActionCommand().equals("Size 6")) {
/*  726: 998 */       changeFontSize(6);
/*  727: 999 */     } else if (c.getActionCommand().equals("Size 4")) {
/*  728:1000 */       changeFontSize(4);
/*  729:1001 */     } else if (c.getActionCommand().equals("Size 2")) {
/*  730:1002 */       changeFontSize(2);
/*  731:1003 */     } else if (c.getActionCommand().equals("Size 1")) {
/*  732:1004 */       changeFontSize(1);
/*  733:1005 */     } else if (!c.getActionCommand().equals("Hide Descendants")) {}
/*  734:     */   }
/*  735:     */   
/*  736:     */   public void mouseClicked(MouseEvent e)
/*  737:     */   {
/*  738:1020 */     if (this.m_clickAvailable)
/*  739:     */     {
/*  740:1022 */       int s = -1;
/*  741:1024 */       for (int noa = 0; noa < this.m_numNodes; noa++) {
/*  742:1025 */         if (this.m_nodes[noa].m_quad == 18)
/*  743:     */         {
/*  744:1027 */           calcScreenCoords(noa);
/*  745:1028 */           if ((e.getX() <= this.m_nodes[noa].m_center + this.m_nodes[noa].m_side) && (e.getX() >= this.m_nodes[noa].m_center - this.m_nodes[noa].m_side) && (e.getY() >= this.m_nodes[noa].m_top) && (e.getY() <= this.m_nodes[noa].m_top + this.m_nodes[noa].m_height)) {
/*  746:1033 */             s = noa;
/*  747:     */           }
/*  748:1035 */           this.m_nodes[noa].m_top = 32000;
/*  749:     */         }
/*  750:     */       }
/*  751:1038 */       this.m_focusNode = s;
/*  752:1040 */       if (this.m_focusNode != -1) {
/*  753:1041 */         if (this.m_listener != null) {
/*  754:1043 */           actionPerformed(new ActionEvent(this, 32000, "Create Child Nodes"));
/*  755:     */         } else {
/*  756:1047 */           actionPerformed(new ActionEvent(this, 32000, "Visualize The Node"));
/*  757:     */         }
/*  758:     */       }
/*  759:     */     }
/*  760:     */   }
/*  761:     */   
/*  762:     */   public void mousePressed(MouseEvent e)
/*  763:     */   {
/*  764:1060 */     this.m_frameLimiter.setRepeats(true);
/*  765:1061 */     if (((e.getModifiers() & 0x10) != 0) && (!e.isAltDown()) && (this.m_mouseState == 0) && (this.m_scaling == 0))
/*  766:     */     {
/*  767:1066 */       if (((e.getModifiers() & 0x2) != 0) && ((e.getModifiers() & 0x1) == 0))
/*  768:     */       {
/*  769:1069 */         this.m_mouseState = 2;
/*  770:     */       }
/*  771:1070 */       else if (((e.getModifiers() & 0x1) != 0) && ((e.getModifiers() & 0x2) == 0))
/*  772:     */       {
/*  773:1074 */         this.m_oldMousePos.width = e.getX();
/*  774:1075 */         this.m_oldMousePos.height = e.getY();
/*  775:1076 */         this.m_newMousePos.width = e.getX();
/*  776:1077 */         this.m_newMousePos.height = e.getY();
/*  777:1078 */         this.m_mouseState = 3;
/*  778:     */         
/*  779:1080 */         Graphics g = getGraphics();
/*  780:1081 */         if (this.m_ZoomBoxColor == null) {
/*  781:1082 */           g.setColor(Color.black);
/*  782:     */         } else {
/*  783:1084 */           g.setColor(this.m_ZoomBoxColor);
/*  784:     */         }
/*  785:1086 */         if (this.m_ZoomBoxXORColor == null) {
/*  786:1087 */           g.setXORMode(Color.white);
/*  787:     */         } else {
/*  788:1089 */           g.setXORMode(this.m_ZoomBoxXORColor);
/*  789:     */         }
/*  790:1091 */         g.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
/*  791:     */         
/*  792:     */ 
/*  793:1094 */         g.dispose();
/*  794:     */       }
/*  795:     */       else
/*  796:     */       {
/*  797:1097 */         this.m_oldMousePos.width = e.getX();
/*  798:1098 */         this.m_oldMousePos.height = e.getY();
/*  799:1099 */         this.m_newMousePos.width = e.getX();
/*  800:1100 */         this.m_newMousePos.height = e.getY();
/*  801:1101 */         this.m_mouseState = 1;
/*  802:1102 */         this.m_frameLimiter.start();
/*  803:     */       }
/*  804:     */     }
/*  805:1107 */     else if ((e.getButton() == 1) && (e.isAltDown()) && (e.isShiftDown()) && (!e.isControlDown())) {
/*  806:1109 */       saveComponent();
/*  807:1110 */     } else if ((this.m_mouseState != 0) || (this.m_scaling != 0)) {}
/*  808:     */   }
/*  809:     */   
/*  810:     */   public void mouseReleased(MouseEvent e)
/*  811:     */   {
/*  812:1124 */     if (this.m_mouseState == 1) {
/*  813:1127 */       this.m_clickAvailable = true;
/*  814:     */     } else {
/*  815:1131 */       this.m_clickAvailable = false;
/*  816:     */     }
/*  817:1133 */     if ((this.m_mouseState == 2) && (mouseInBounds(e)))
/*  818:     */     {
/*  819:1135 */       this.m_mouseState = 0;
/*  820:1136 */       Dimension ns = new Dimension(this.m_viewSize.width / 2, this.m_viewSize.height / 2);
/*  821:1137 */       if (ns.width < 10) {
/*  822:1138 */         ns.width = 10;
/*  823:     */       }
/*  824:1140 */       if (ns.height < 10) {
/*  825:1141 */         ns.height = 10;
/*  826:     */       }
/*  827:1144 */       Dimension d = getSize();
/*  828:1145 */       Dimension np = new Dimension((int)(d.width / 2 - (d.width / 2.0D - this.m_viewPos.width) / 2.0D), (int)(d.height / 2 - (d.height / 2.0D - this.m_viewPos.height) / 2.0D));
/*  829:     */       
/*  830:     */ 
/*  831:     */ 
/*  832:1149 */       animateScaling(np, ns, 10);
/*  833:     */     }
/*  834:1154 */     else if (this.m_mouseState == 3)
/*  835:     */     {
/*  836:1156 */       this.m_mouseState = 0;
/*  837:1157 */       Graphics g = getGraphics();
/*  838:1158 */       if (this.m_ZoomBoxColor == null) {
/*  839:1159 */         g.setColor(Color.black);
/*  840:     */       } else {
/*  841:1161 */         g.setColor(this.m_ZoomBoxColor);
/*  842:     */       }
/*  843:1163 */       if (this.m_ZoomBoxXORColor == null) {
/*  844:1164 */         g.setXORMode(Color.white);
/*  845:     */       } else {
/*  846:1166 */         g.setXORMode(this.m_ZoomBoxXORColor);
/*  847:     */       }
/*  848:1168 */       g.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
/*  849:     */       
/*  850:1170 */       g.dispose();
/*  851:     */       
/*  852:1172 */       int cw = this.m_newMousePos.width - this.m_oldMousePos.width;
/*  853:1173 */       int ch = this.m_newMousePos.height - this.m_oldMousePos.height;
/*  854:1174 */       if ((cw >= 1) && (ch >= 1) && 
/*  855:1175 */         (mouseInBounds(e)) && (getSize().width / cw <= 6) && (getSize().height / ch <= 6))
/*  856:     */       {
/*  857:1179 */         Dimension ns = new Dimension();
/*  858:1180 */         Dimension np = new Dimension();
/*  859:1181 */         double nvsw = getSize().width / cw;
/*  860:1182 */         double nvsh = getSize().height / ch;
/*  861:1183 */         np.width = ((int)((this.m_oldMousePos.width - this.m_viewPos.width) * -nvsw));
/*  862:1184 */         np.height = ((int)((this.m_oldMousePos.height - this.m_viewPos.height) * -nvsh));
/*  863:1185 */         ns.width = ((int)(this.m_viewSize.width * nvsw));
/*  864:1186 */         ns.height = ((int)(this.m_viewSize.height * nvsh));
/*  865:     */         
/*  866:1188 */         animateScaling(np, ns, 10);
/*  867:     */       }
/*  868:     */     }
/*  869:1192 */     else if ((this.m_mouseState == 0) && (this.m_scaling == 0))
/*  870:     */     {
/*  871:1194 */       this.m_mouseState = 0;
/*  872:1195 */       setFont(new Font("A Name", 0, 12));
/*  873:     */       
/*  874:1197 */       int s = -1;
/*  875:1199 */       for (int noa = 0; noa < this.m_numNodes; noa++) {
/*  876:1200 */         if (this.m_nodes[noa].m_quad == 18)
/*  877:     */         {
/*  878:1202 */           calcScreenCoords(noa);
/*  879:1203 */           if ((e.getX() <= this.m_nodes[noa].m_center + this.m_nodes[noa].m_side) && (e.getX() >= this.m_nodes[noa].m_center - this.m_nodes[noa].m_side) && (e.getY() >= this.m_nodes[noa].m_top) && (e.getY() <= this.m_nodes[noa].m_top + this.m_nodes[noa].m_height)) {
/*  880:1208 */             s = noa;
/*  881:     */           }
/*  882:1210 */           this.m_nodes[noa].m_top = 32000;
/*  883:     */         }
/*  884:     */       }
/*  885:1213 */       if (s == -1)
/*  886:     */       {
/*  887:1215 */         this.m_winMenu.show(this, e.getX(), e.getY());
/*  888:     */       }
/*  889:     */       else
/*  890:     */       {
/*  891:1218 */         this.m_focusNode = s;
/*  892:1219 */         this.m_nodeMenu.show(this, e.getX(), e.getY());
/*  893:     */       }
/*  894:1222 */       setFont(this.m_currentFont);
/*  895:     */     }
/*  896:1223 */     else if (this.m_mouseState == 1)
/*  897:     */     {
/*  898:1225 */       this.m_mouseState = 0;
/*  899:1226 */       this.m_frameLimiter.stop();
/*  900:1227 */       repaint();
/*  901:     */     }
/*  902:     */   }
/*  903:     */   
/*  904:     */   private boolean mouseInBounds(MouseEvent e)
/*  905:     */   {
/*  906:1242 */     if ((e.getX() < 0) || (e.getY() < 0) || (e.getX() > getSize().width) || (e.getY() > getSize().height)) {
/*  907:1244 */       return false;
/*  908:     */     }
/*  909:1246 */     return true;
/*  910:     */   }
/*  911:     */   
/*  912:     */   public void mouseDragged(MouseEvent e)
/*  913:     */   {
/*  914:1258 */     if (this.m_mouseState == 1)
/*  915:     */     {
/*  916:1260 */       this.m_oldMousePos.width = this.m_newMousePos.width;
/*  917:1261 */       this.m_oldMousePos.height = this.m_newMousePos.height;
/*  918:1262 */       this.m_newMousePos.width = e.getX();
/*  919:1263 */       this.m_newMousePos.height = e.getY();
/*  920:1264 */       this.m_viewPos.width += this.m_newMousePos.width - this.m_oldMousePos.width;
/*  921:1265 */       this.m_viewPos.height += this.m_newMousePos.height - this.m_oldMousePos.height;
/*  922:     */     }
/*  923:1267 */     else if (this.m_mouseState == 3)
/*  924:     */     {
/*  925:1270 */       Graphics g = getGraphics();
/*  926:1271 */       if (this.m_ZoomBoxColor == null) {
/*  927:1272 */         g.setColor(Color.black);
/*  928:     */       } else {
/*  929:1274 */         g.setColor(this.m_ZoomBoxColor);
/*  930:     */       }
/*  931:1276 */       if (this.m_ZoomBoxXORColor == null) {
/*  932:1277 */         g.setXORMode(Color.white);
/*  933:     */       } else {
/*  934:1279 */         g.setXORMode(this.m_ZoomBoxXORColor);
/*  935:     */       }
/*  936:1281 */       g.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
/*  937:     */       
/*  938:     */ 
/*  939:1284 */       this.m_newMousePos.width = e.getX();
/*  940:1285 */       this.m_newMousePos.height = e.getY();
/*  941:     */       
/*  942:1287 */       g.drawRect(this.m_oldMousePos.width, this.m_oldMousePos.height, this.m_newMousePos.width - this.m_oldMousePos.width, this.m_newMousePos.height - this.m_oldMousePos.height);
/*  943:     */       
/*  944:1289 */       g.dispose();
/*  945:     */     }
/*  946:     */   }
/*  947:     */   
/*  948:     */   public void mouseMoved(MouseEvent e) {}
/*  949:     */   
/*  950:     */   public void mouseEntered(MouseEvent e) {}
/*  951:     */   
/*  952:     */   public void mouseExited(MouseEvent e) {}
/*  953:     */   
/*  954:     */   public void setHighlight(String id)
/*  955:     */   {
/*  956:1329 */     for (int noa = 0; noa < this.m_numNodes; noa++) {
/*  957:1330 */       if (id.equals(this.m_nodes[noa].m_node.getRefer())) {
/*  958:1332 */         this.m_highlightNode = noa;
/*  959:     */       }
/*  960:     */     }
/*  961:1337 */     repaint();
/*  962:     */   }
/*  963:     */   
/*  964:     */   public void paintComponent(Graphics g)
/*  965:     */   {
/*  966:1348 */     Color oldBackground = ((Graphics2D)g).getBackground();
/*  967:1349 */     if (this.m_BackgroundColor != null) {
/*  968:1350 */       ((Graphics2D)g).setBackground(this.m_BackgroundColor);
/*  969:     */     }
/*  970:1352 */     g.clearRect(0, 0, getSize().width, getSize().height);
/*  971:1353 */     ((Graphics2D)g).setBackground(oldBackground);
/*  972:1354 */     g.setClip(3, 7, getWidth() - 6, getHeight() - 10);
/*  973:1355 */     painter(g);
/*  974:1356 */     g.setClip(0, 0, getWidth(), getHeight());
/*  975:     */   }
/*  976:     */   
/*  977:     */   private void painter(Graphics g)
/*  978:     */   {
/*  979:1388 */     double left_clip = (-this.m_viewPos.width - 50) / this.m_viewSize.width;
/*  980:1389 */     double right_clip = (getSize().width - this.m_viewPos.width + 50) / this.m_viewSize.width;
/*  981:     */     
/*  982:1391 */     double top_clip = (-this.m_viewPos.height - 50) / this.m_viewSize.height;
/*  983:1392 */     double bottom_clip = (getSize().height - this.m_viewPos.height + 50) / this.m_viewSize.height;
/*  984:     */     
/*  985:     */ 
/*  986:     */ 
/*  987:     */ 
/*  988:     */ 
/*  989:     */ 
/*  990:     */ 
/*  991:     */ 
/*  992:     */ 
/*  993:     */ 
/*  994:     */ 
/*  995:     */ 
/*  996:1405 */     int row = 0;int col = 0;
/*  997:1406 */     for (int noa = 0; noa < this.m_numNodes; noa++)
/*  998:     */     {
/*  999:1407 */       Node r = this.m_nodes[noa].m_node;
/* 1000:1408 */       if (this.m_nodes[noa].m_change)
/* 1001:     */       {
/* 1002:1410 */         double ntop = r.getTop();
/* 1003:1411 */         if (ntop < top_clip) {
/* 1004:1412 */           row = 8;
/* 1005:1413 */         } else if (ntop > bottom_clip) {
/* 1006:1414 */           row = 32;
/* 1007:     */         } else {
/* 1008:1416 */           row = 16;
/* 1009:     */         }
/* 1010:     */       }
/* 1011:1421 */       double ncent = r.getCenter();
/* 1012:1422 */       if (ncent < left_clip) {
/* 1013:1423 */         col = 4;
/* 1014:1424 */       } else if (ncent > right_clip) {
/* 1015:1425 */         col = 1;
/* 1016:     */       } else {
/* 1017:1427 */         col = 2;
/* 1018:     */       }
/* 1019:1430 */       this.m_nodes[noa].m_quad = (row | col);
/* 1020:1432 */       if (this.m_nodes[noa].m_parent >= 0)
/* 1021:     */       {
/* 1022:1437 */         int pq = this.m_nodes[this.m_edges[this.m_nodes[noa].m_parent].m_parent].m_quad;
/* 1023:1438 */         int cq = this.m_nodes[noa].m_quad;
/* 1024:1441 */         if ((cq & 0x8) != 8) {
/* 1025:1443 */           if ((pq & 0x20) != 32) {
/* 1026:1445 */             if (((cq & 0x4) != 4) || ((pq & 0x4) != 4)) {
/* 1027:1447 */               if (((cq & 0x1) != 1) || ((pq & 0x1) != 1)) {
/* 1028:1451 */                 drawLine(this.m_nodes[noa].m_parent, g);
/* 1029:     */               }
/* 1030:     */             }
/* 1031:     */           }
/* 1032:     */         }
/* 1033:     */       }
/* 1034:     */     }
/* 1035:1458 */     for (int noa = 0; noa < this.m_numNodes; noa++) {
/* 1036:1459 */       if (this.m_nodes[noa].m_quad == 18) {
/* 1037:1461 */         drawNode(noa, g);
/* 1038:     */       }
/* 1039:     */     }
/* 1040:1465 */     if ((this.m_highlightNode >= 0) && (this.m_highlightNode < this.m_numNodes)) {
/* 1041:1467 */       if (this.m_nodes[this.m_highlightNode].m_quad == 18)
/* 1042:     */       {
/* 1043:     */         Color acol;
/* 1044:     */         Color acol;
/* 1045:1469 */         if (this.m_NodeColor == null) {
/* 1046:1470 */           acol = this.m_nodes[this.m_highlightNode].m_node.getColor();
/* 1047:     */         } else {
/* 1048:1472 */           acol = this.m_NodeColor;
/* 1049:     */         }
/* 1050:1474 */         g.setColor(new Color((acol.getRed() + 125) % 256, (acol.getGreen() + 125) % 256, (acol.getBlue() + 125) % 256));
/* 1051:1477 */         if (this.m_nodes[this.m_highlightNode].m_node.getShape() == 1)
/* 1052:     */         {
/* 1053:1478 */           g.drawRect(this.m_nodes[this.m_highlightNode].m_center - this.m_nodes[this.m_highlightNode].m_side, this.m_nodes[this.m_highlightNode].m_top, this.m_nodes[this.m_highlightNode].m_width, this.m_nodes[this.m_highlightNode].m_height);
/* 1054:     */           
/* 1055:     */ 
/* 1056:     */ 
/* 1057:1482 */           g.drawRect(this.m_nodes[this.m_highlightNode].m_center - this.m_nodes[this.m_highlightNode].m_side + 1, this.m_nodes[this.m_highlightNode].m_top + 1, this.m_nodes[this.m_highlightNode].m_width - 2, this.m_nodes[this.m_highlightNode].m_height - 2);
/* 1058:     */         }
/* 1059:1487 */         else if (this.m_nodes[this.m_highlightNode].m_node.getShape() == 2)
/* 1060:     */         {
/* 1061:1488 */           g.drawOval(this.m_nodes[this.m_highlightNode].m_center - this.m_nodes[this.m_highlightNode].m_side, this.m_nodes[this.m_highlightNode].m_top, this.m_nodes[this.m_highlightNode].m_width, this.m_nodes[this.m_highlightNode].m_height);
/* 1062:     */           
/* 1063:     */ 
/* 1064:     */ 
/* 1065:1492 */           g.drawOval(this.m_nodes[this.m_highlightNode].m_center - this.m_nodes[this.m_highlightNode].m_side + 1, this.m_nodes[this.m_highlightNode].m_top + 1, this.m_nodes[this.m_highlightNode].m_width - 2, this.m_nodes[this.m_highlightNode].m_height - 2);
/* 1066:     */         }
/* 1067:     */       }
/* 1068:     */     }
/* 1069:1501 */     for (int noa = 0; noa < this.m_numNodes; noa++) {
/* 1070:1508 */       this.m_nodes[noa].m_top = 32000;
/* 1071:     */     }
/* 1072:     */   }
/* 1073:     */   
/* 1074:     */   private void drawNode(int n, Graphics g)
/* 1075:     */   {
/* 1076:1521 */     if (this.m_NodeColor == null) {
/* 1077:1522 */       g.setColor(this.m_nodes[n].m_node.getColor());
/* 1078:     */     } else {
/* 1079:1524 */       g.setColor(this.m_NodeColor);
/* 1080:     */     }
/* 1081:1526 */     g.setPaintMode();
/* 1082:1527 */     calcScreenCoords(n);
/* 1083:1528 */     int x = this.m_nodes[n].m_center - this.m_nodes[n].m_side;
/* 1084:1529 */     int y = this.m_nodes[n].m_top;
/* 1085:1530 */     if (this.m_nodes[n].m_node.getShape() == 1)
/* 1086:     */     {
/* 1087:1531 */       g.fill3DRect(x, y, this.m_nodes[n].m_width, this.m_nodes[n].m_height, true);
/* 1088:1532 */       drawText(x, y, n, false, g);
/* 1089:     */     }
/* 1090:1534 */     else if (this.m_nodes[n].m_node.getShape() == 2)
/* 1091:     */     {
/* 1092:1536 */       g.fillOval(x, y, this.m_nodes[n].m_width, this.m_nodes[n].m_height);
/* 1093:1537 */       drawText(x, y + (int)(this.m_nodes[n].m_height * 0.15D), n, false, g);
/* 1094:     */     }
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   private void drawLine(int e, Graphics g)
/* 1098:     */   {
/* 1099:1556 */     int p = this.m_edges[e].m_parent;
/* 1100:1557 */     int c = this.m_edges[e].m_child;
/* 1101:1558 */     calcScreenCoords(c);
/* 1102:1559 */     calcScreenCoords(p);
/* 1103:1561 */     if (this.m_LineColor == null) {
/* 1104:1562 */       g.setColor(Color.black);
/* 1105:     */     } else {
/* 1106:1564 */       g.setColor(this.m_LineColor);
/* 1107:     */     }
/* 1108:1566 */     g.setPaintMode();
/* 1109:1568 */     if (this.m_currentFont.getSize() < 2)
/* 1110:     */     {
/* 1111:1570 */       g.drawLine(this.m_nodes[p].m_center, this.m_nodes[p].m_top + this.m_nodes[p].m_height, this.m_nodes[c].m_center, this.m_nodes[c].m_top);
/* 1112:     */     }
/* 1113:     */     else
/* 1114:     */     {
/* 1115:1575 */       int e_width = this.m_nodes[c].m_center - this.m_nodes[p].m_center;
/* 1116:1576 */       int e_height = this.m_nodes[c].m_top - (this.m_nodes[p].m_top + this.m_nodes[p].m_height);
/* 1117:     */       
/* 1118:1578 */       int e_width2 = e_width / 2;
/* 1119:1579 */       int e_height2 = e_height / 2;
/* 1120:1580 */       int e_centerx = this.m_nodes[p].m_center + e_width2;
/* 1121:1581 */       int e_centery = this.m_nodes[p].m_top + this.m_nodes[p].m_height + e_height2;
/* 1122:1582 */       int e_offset = this.m_edges[e].m_tb;
/* 1123:     */       
/* 1124:1584 */       int tmp = (int)(e_width / e_height * (e_height2 - e_offset)) + this.m_nodes[p].m_center;
/* 1125:     */       
/* 1126:     */ 
/* 1127:     */ 
/* 1128:     */ 
/* 1129:     */ 
/* 1130:1590 */       drawText(e_centerx - this.m_edges[e].m_side, e_centery - e_offset, e, true, g);
/* 1131:1592 */       if ((tmp > e_centerx - this.m_edges[e].m_side) && (tmp < e_centerx + this.m_edges[e].m_side))
/* 1132:     */       {
/* 1133:1595 */         g.drawLine(this.m_nodes[p].m_center, this.m_nodes[p].m_top + this.m_nodes[p].m_height, tmp, e_centery - e_offset);
/* 1134:     */         
/* 1135:1597 */         g.drawLine(e_centerx * 2 - tmp, e_centery + e_offset, this.m_nodes[c].m_center, this.m_nodes[c].m_top);
/* 1136:     */       }
/* 1137:     */       else
/* 1138:     */       {
/* 1139:1600 */         e_offset = this.m_edges[e].m_side;
/* 1140:1601 */         if (e_width < 0) {
/* 1141:1602 */           e_offset *= -1;
/* 1142:     */         }
/* 1143:1605 */         tmp = (int)(e_height / e_width * (e_width2 - e_offset)) + this.m_nodes[p].m_top + this.m_nodes[p].m_height;
/* 1144:     */         
/* 1145:     */ 
/* 1146:1608 */         g.drawLine(this.m_nodes[p].m_center, this.m_nodes[p].m_top + this.m_nodes[p].m_height, e_centerx - e_offset, tmp);
/* 1147:     */         
/* 1148:1610 */         g.drawLine(e_centerx + e_offset, e_centery * 2 - tmp, this.m_nodes[c].m_center, this.m_nodes[c].m_top);
/* 1149:     */       }
/* 1150:     */     }
/* 1151:     */   }
/* 1152:     */   
/* 1153:     */   private void drawText(int x1, int y1, int s, boolean e_or_n, Graphics g)
/* 1154:     */   {
/* 1155:1633 */     Color oldColor = g.getColor();
/* 1156:     */     
/* 1157:1635 */     g.setPaintMode();
/* 1158:1636 */     if (this.m_FontColor == null) {
/* 1159:1637 */       g.setColor(Color.black);
/* 1160:     */     } else {
/* 1161:1639 */       g.setColor(this.m_FontColor);
/* 1162:     */     }
/* 1163:1642 */     if (e_or_n)
/* 1164:     */     {
/* 1165:1644 */       Edge e = this.m_edges[s].m_edge;
/* 1166:     */       String st;
/* 1167:1645 */       for (int noa = 0; (st = e.getLine(noa)) != null; noa++) {
/* 1168:1646 */         g.drawString(st, (this.m_edges[s].m_width - this.m_fontSize.stringWidth(st)) / 2 + x1, y1 + (noa + 1) * this.m_fontSize.getHeight());
/* 1169:     */       }
/* 1170:     */     }
/* 1171:     */     else
/* 1172:     */     {
/* 1173:1651 */       Node e = this.m_nodes[s].m_node;
/* 1174:     */       String st;
/* 1175:1652 */       for (int noa = 0; (st = e.getLine(noa)) != null; noa++) {
/* 1176:1653 */         g.drawString(st, (this.m_nodes[s].m_width - this.m_fontSize.stringWidth(st)) / 2 + x1, y1 + (noa + 1) * this.m_fontSize.getHeight());
/* 1177:     */       }
/* 1178:     */     }
/* 1179:1659 */     g.setColor(oldColor);
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   private void calcScreenCoords(int n)
/* 1183:     */   {
/* 1184:1672 */     if (this.m_nodes[n].m_top == 32000)
/* 1185:     */     {
/* 1186:1673 */       this.m_nodes[n].m_top = ((int)(this.m_nodes[n].m_node.getTop() * this.m_viewSize.height) + this.m_viewPos.height);
/* 1187:     */       
/* 1188:1675 */       this.m_nodes[n].m_center = ((int)(this.m_nodes[n].m_node.getCenter() * this.m_viewSize.width) + this.m_viewPos.width);
/* 1189:     */     }
/* 1190:     */   }
/* 1191:     */   
/* 1192:     */   private void autoScale()
/* 1193:     */   {
/* 1194:1692 */     Dimension temp = new Dimension(10, 10);
/* 1195:1694 */     if (this.m_numNodes <= 1) {
/* 1196:1695 */       return;
/* 1197:     */     }
/* 1198:1699 */     int dist = (this.m_nodes[0].m_height + 40) * this.m_numLevels;
/* 1199:1700 */     if (dist > temp.height) {
/* 1200:1701 */       temp.height = dist;
/* 1201:     */     }
/* 1202:1704 */     for (int noa = 0; noa < this.m_numNodes - 1; noa++)
/* 1203:     */     {
/* 1204:1705 */       calcScreenCoords(noa);
/* 1205:1706 */       calcScreenCoords(noa + 1);
/* 1206:1707 */       if (!this.m_nodes[(noa + 1)].m_change)
/* 1207:     */       {
/* 1208:1711 */         dist = this.m_nodes[(noa + 1)].m_center - this.m_nodes[noa].m_center;
/* 1209:1713 */         if (dist <= 0) {
/* 1210:1714 */           dist = 1;
/* 1211:     */         }
/* 1212:1716 */         dist = (6 + this.m_nodes[noa].m_side + this.m_nodes[(noa + 1)].m_side) * this.m_viewSize.width / dist;
/* 1213:1719 */         if (dist > temp.width) {
/* 1214:1721 */           temp.width = dist;
/* 1215:     */         }
/* 1216:     */       }
/* 1217:1726 */       dist = (this.m_nodes[(noa + 1)].m_height + 40) * this.m_numLevels;
/* 1218:1727 */       if (dist > temp.height) {
/* 1219:1729 */         temp.height = dist;
/* 1220:     */       }
/* 1221:     */     }
/* 1222:1735 */     int y1 = this.m_nodes[this.m_edges[0].m_parent].m_top;
/* 1223:1736 */     int y2 = this.m_nodes[this.m_edges[0].m_child].m_top;
/* 1224:     */     
/* 1225:1738 */     dist = y2 - y1;
/* 1226:1739 */     if (dist <= 0) {
/* 1227:1740 */       dist = 1;
/* 1228:     */     }
/* 1229:1742 */     dist = (60 + this.m_edges[0].m_height + this.m_nodes[this.m_edges[0].m_parent].m_height) * this.m_viewSize.height / dist;
/* 1230:1744 */     if (dist > temp.height) {
/* 1231:1746 */       temp.height = dist;
/* 1232:     */     }
/* 1233:1749 */     for (int noa = 0; noa < this.m_numNodes - 2; noa++)
/* 1234:     */     {
/* 1235:1751 */       if (!this.m_nodes[this.m_edges[(noa + 1)].m_child].m_change)
/* 1236:     */       {
/* 1237:1756 */         int xa = this.m_nodes[this.m_edges[noa].m_child].m_center - this.m_nodes[this.m_edges[noa].m_parent].m_center;
/* 1238:     */         
/* 1239:1758 */         xa /= 2;
/* 1240:1759 */         xa += this.m_nodes[this.m_edges[noa].m_parent].m_center;
/* 1241:     */         
/* 1242:1761 */         int xb = this.m_nodes[this.m_edges[(noa + 1)].m_child].m_center - this.m_nodes[this.m_edges[(noa + 1)].m_parent].m_center;
/* 1243:     */         
/* 1244:1763 */         xb /= 2;
/* 1245:1764 */         xb += this.m_nodes[this.m_edges[(noa + 1)].m_parent].m_center;
/* 1246:     */         
/* 1247:1766 */         dist = xb - xa;
/* 1248:1767 */         if (dist <= 0) {
/* 1249:1768 */           dist = 1;
/* 1250:     */         }
/* 1251:1770 */         dist = (12 + this.m_edges[noa].m_side + this.m_edges[(noa + 1)].m_side) * this.m_viewSize.width / dist;
/* 1252:1772 */         if (dist > temp.width) {
/* 1253:1774 */           temp.width = dist;
/* 1254:     */         }
/* 1255:     */       }
/* 1256:1778 */       y1 = this.m_nodes[this.m_edges[(noa + 1)].m_parent].m_top;
/* 1257:1779 */       y2 = this.m_nodes[this.m_edges[(noa + 1)].m_child].m_top;
/* 1258:     */       
/* 1259:1781 */       dist = y2 - y1;
/* 1260:1782 */       if (dist <= 0) {
/* 1261:1784 */         dist = 1;
/* 1262:     */       }
/* 1263:1786 */       dist = (60 + this.m_edges[(noa + 1)].m_height + this.m_nodes[this.m_edges[(noa + 1)].m_parent].m_height) * this.m_viewSize.height / dist;
/* 1264:1789 */       if (dist > temp.height) {
/* 1265:1791 */         temp.height = dist;
/* 1266:     */       }
/* 1267:     */     }
/* 1268:1795 */     Dimension e = getSize();
/* 1269:     */     
/* 1270:1797 */     Dimension np = new Dimension();
/* 1271:1798 */     np.width = ((int)(e.width / 2 - (e.width / 2.0D - this.m_viewPos.width) / this.m_viewSize.width * temp.width));
/* 1272:     */     
/* 1273:1800 */     np.height = ((int)(e.height / 2 - (e.height / 2.0D - this.m_viewPos.height) / this.m_viewSize.height * temp.height));
/* 1274:1804 */     for (int noa = 0; noa < this.m_numNodes; noa++) {
/* 1275:1811 */       this.m_nodes[noa].m_top = 32000;
/* 1276:     */     }
/* 1277:1814 */     animateScaling(np, temp, 10);
/* 1278:     */   }
/* 1279:     */   
/* 1280:     */   private void animateScaling(Dimension n_pos, Dimension n_size, int frames)
/* 1281:     */   {
/* 1282:1834 */     if (frames == 0)
/* 1283:     */     {
/* 1284:1835 */       System.out.println("the timer didn't end in time");
/* 1285:1836 */       this.m_scaling = 0;
/* 1286:     */     }
/* 1287:     */     else
/* 1288:     */     {
/* 1289:1838 */       if (this.m_scaling == 0)
/* 1290:     */       {
/* 1291:1841 */         this.m_frameLimiter.start();
/* 1292:1842 */         this.m_nViewPos.width = n_pos.width;
/* 1293:1843 */         this.m_nViewPos.height = n_pos.height;
/* 1294:1844 */         this.m_nViewSize.width = n_size.width;
/* 1295:1845 */         this.m_nViewSize.height = n_size.height;
/* 1296:     */         
/* 1297:1847 */         this.m_scaling = frames;
/* 1298:     */       }
/* 1299:1850 */       int s_w = (n_size.width - this.m_viewSize.width) / frames;
/* 1300:1851 */       int s_h = (n_size.height - this.m_viewSize.height) / frames;
/* 1301:1852 */       int p_w = (n_pos.width - this.m_viewPos.width) / frames;
/* 1302:1853 */       int p_h = (n_pos.height - this.m_viewPos.height) / frames;
/* 1303:     */       
/* 1304:1855 */       this.m_viewSize.width += s_w;
/* 1305:1856 */       this.m_viewSize.height += s_h;
/* 1306:     */       
/* 1307:1858 */       this.m_viewPos.width += p_w;
/* 1308:1859 */       this.m_viewPos.height += p_h;
/* 1309:     */       
/* 1310:1861 */       repaint();
/* 1311:     */       
/* 1312:1863 */       this.m_scaling -= 1;
/* 1313:1864 */       if (this.m_scaling == 0) {
/* 1314:1866 */         this.m_frameLimiter.stop();
/* 1315:     */       }
/* 1316:     */     }
/* 1317:     */   }
/* 1318:     */   
/* 1319:     */   private void changeFontSize(int s)
/* 1320:     */   {
/* 1321:1882 */     setFont(this.m_currentFont = new Font("A Name", 0, s));
/* 1322:     */     
/* 1323:1884 */     this.m_fontSize = getFontMetrics(getFont());
/* 1324:1888 */     for (int noa = 0; noa < this.m_numNodes; noa++)
/* 1325:     */     {
/* 1326:1891 */       Dimension d = this.m_nodes[noa].m_node.stringSize(this.m_fontSize);
/* 1327:1893 */       if (this.m_nodes[noa].m_node.getShape() == 1)
/* 1328:     */       {
/* 1329:1894 */         this.m_nodes[noa].m_height = (d.height + 10);
/* 1330:1895 */         this.m_nodes[noa].m_width = (d.width + 8);
/* 1331:1896 */         this.m_nodes[noa].m_side = (this.m_nodes[noa].m_width / 2);
/* 1332:     */       }
/* 1333:1897 */       else if (this.m_nodes[noa].m_node.getShape() == 2)
/* 1334:     */       {
/* 1335:1898 */         this.m_nodes[noa].m_height = ((int)((d.height + 2) * 1.6D));
/* 1336:1899 */         this.m_nodes[noa].m_width = ((int)((d.width + 2) * 1.6D));
/* 1337:1900 */         this.m_nodes[noa].m_side = (this.m_nodes[noa].m_width / 2);
/* 1338:     */       }
/* 1339:1903 */       if (noa < this.m_numNodes - 1)
/* 1340:     */       {
/* 1341:1906 */         d = this.m_edges[noa].m_edge.stringSize(this.m_fontSize);
/* 1342:     */         
/* 1343:1908 */         this.m_edges[noa].m_height = (d.height + 8);
/* 1344:1909 */         this.m_edges[noa].m_width = (d.width + 8);
/* 1345:1910 */         this.m_edges[noa].m_side = (this.m_edges[noa].m_width / 2);
/* 1346:1911 */         this.m_edges[noa].m_tb = (this.m_edges[noa].m_height / 2);
/* 1347:     */       }
/* 1348:     */     }
/* 1349:     */   }
/* 1350:     */   
/* 1351:     */   private void arrayFill(Node t, NodeInfo[] l, EdgeInfo[] k)
/* 1352:     */   {
/* 1353:1934 */     if ((t == null) || (l == null)) {
/* 1354:1935 */       System.exit(1);
/* 1355:     */     }
/* 1356:1941 */     l[0] = new NodeInfo(null);
/* 1357:1942 */     l[0].m_node = t;
/* 1358:1943 */     l[0].m_parent = -1;
/* 1359:1944 */     l[0].m_change = true;
/* 1360:     */     
/* 1361:     */ 
/* 1362:     */ 
/* 1363:     */ 
/* 1364:     */ 
/* 1365:     */ 
/* 1366:     */ 
/* 1367:     */ 
/* 1368:1953 */     int free_space = 1;
/* 1369:     */     
/* 1370:1955 */     double height = t.getTop();
/* 1371:1960 */     for (int floater = 0; floater < free_space; floater++)
/* 1372:     */     {
/* 1373:1961 */       Node r = l[floater].m_node;
/* 1374:     */       Edge e;
/* 1375:1962 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++)
/* 1376:     */       {
/* 1377:1966 */         Node s = e.getTarget();
/* 1378:1967 */         l[free_space] = new NodeInfo(null);
/* 1379:1968 */         l[free_space].m_node = s;
/* 1380:1969 */         l[free_space].m_parent = (free_space - 1);
/* 1381:     */         
/* 1382:1971 */         k[(free_space - 1)] = new EdgeInfo(null);
/* 1383:1972 */         k[(free_space - 1)].m_edge = e;
/* 1384:1973 */         k[(free_space - 1)].m_parent = floater;
/* 1385:1974 */         k[(free_space - 1)].m_child = free_space;
/* 1386:1980 */         if (height != s.getTop())
/* 1387:     */         {
/* 1388:1981 */           l[free_space].m_change = true;
/* 1389:1982 */           height = s.getTop();
/* 1390:     */         }
/* 1391:     */         else
/* 1392:     */         {
/* 1393:1984 */           l[free_space].m_change = false;
/* 1394:     */         }
/* 1395:1986 */         free_space++;
/* 1396:     */       }
/* 1397:     */     }
/* 1398:     */   }
/* 1399:     */   
/* 1400:     */   private class EdgeInfo
/* 1401:     */   {
/* 1402:     */     int m_parent;
/* 1403:     */     int m_child;
/* 1404:     */     int m_side;
/* 1405:     */     int m_tb;
/* 1406:     */     int m_width;
/* 1407:     */     int m_height;
/* 1408:     */     Edge m_edge;
/* 1409:     */     
/* 1410:     */     private EdgeInfo() {}
/* 1411:     */   }
/* 1412:     */   
/* 1413:     */   private class NodeInfo
/* 1414:     */   {
/* 1415:2000 */     int m_top = 32000;
/* 1416:     */     int m_center;
/* 1417:     */     int m_side;
/* 1418:     */     int m_width;
/* 1419:     */     int m_height;
/* 1420:     */     boolean m_change;
/* 1421:     */     int m_parent;
/* 1422:     */     int m_quad;
/* 1423:     */     Node m_node;
/* 1424:     */     
/* 1425:     */     private NodeInfo() {}
/* 1426:     */   }
/* 1427:     */   
/* 1428:     */   public static void main(String[] args)
/* 1429:     */   {
/* 1430:     */     try
/* 1431:     */     {
/* 1432:2082 */       Logger.log(Logger.Level.INFO, "Logging started");
/* 1433:     */       
/* 1434:     */ 
/* 1435:     */ 
/* 1436:2086 */       TreeBuild builder = new TreeBuild();
/* 1437:2087 */       Node top = null;
/* 1438:2088 */       NodePlace arrange = new PlaceNode2();
/* 1439:     */       
/* 1440:     */ 
/* 1441:2091 */       top = builder.create(new FileReader(args[0]));
/* 1442:     */       
/* 1443:     */ 
/* 1444:     */ 
/* 1445:     */ 
/* 1446:2096 */       TreeVisualizer a = new TreeVisualizer(null, top, arrange);
/* 1447:2097 */       a.setSize(800, 600);
/* 1448:     */       
/* 1449:     */ 
/* 1450:2100 */       JFrame f = new JFrame();
/* 1451:     */       
/* 1452:     */ 
/* 1453:     */ 
/* 1454:2104 */       Container contentPane = f.getContentPane();
/* 1455:2105 */       contentPane.add(a);
/* 1456:2106 */       f.setDefaultCloseOperation(2);
/* 1457:2107 */       f.setSize(800, 600);
/* 1458:2108 */       f.setVisible(true);
/* 1459:     */     }
/* 1460:     */     catch (IOException e) {}
/* 1461:     */   }
/* 1462:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.TreeVisualizer
 * JD-Core Version:    0.7.0.1
 */