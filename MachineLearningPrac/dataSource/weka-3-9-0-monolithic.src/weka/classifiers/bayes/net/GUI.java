/*    1:     */ package weka.classifiers.bayes.net;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.FontMetrics;
/*    9:     */ import java.awt.Frame;
/*   10:     */ import java.awt.Graphics;
/*   11:     */ import java.awt.Graphics2D;
/*   12:     */ import java.awt.GridBagConstraints;
/*   13:     */ import java.awt.GridBagLayout;
/*   14:     */ import java.awt.GridLayout;
/*   15:     */ import java.awt.Insets;
/*   16:     */ import java.awt.Point;
/*   17:     */ import java.awt.Rectangle;
/*   18:     */ import java.awt.RenderingHints;
/*   19:     */ import java.awt.event.ActionEvent;
/*   20:     */ import java.awt.event.ActionListener;
/*   21:     */ import java.awt.event.MouseAdapter;
/*   22:     */ import java.awt.event.MouseEvent;
/*   23:     */ import java.awt.event.MouseMotionAdapter;
/*   24:     */ import java.awt.event.WindowAdapter;
/*   25:     */ import java.awt.event.WindowEvent;
/*   26:     */ import java.awt.image.BufferedImage;
/*   27:     */ import java.awt.print.PageFormat;
/*   28:     */ import java.awt.print.Printable;
/*   29:     */ import java.awt.print.PrinterException;
/*   30:     */ import java.awt.print.PrinterJob;
/*   31:     */ import java.beans.PropertyEditor;
/*   32:     */ import java.io.File;
/*   33:     */ import java.io.FileReader;
/*   34:     */ import java.io.FileWriter;
/*   35:     */ import java.io.IOException;
/*   36:     */ import java.io.PrintStream;
/*   37:     */ import java.net.URL;
/*   38:     */ import java.util.ArrayList;
/*   39:     */ import java.util.Random;
/*   40:     */ import java.util.Vector;
/*   41:     */ import javax.swing.AbstractAction;
/*   42:     */ import javax.swing.Action;
/*   43:     */ import javax.swing.BorderFactory;
/*   44:     */ import javax.swing.ImageIcon;
/*   45:     */ import javax.swing.JButton;
/*   46:     */ import javax.swing.JCheckBox;
/*   47:     */ import javax.swing.JCheckBoxMenuItem;
/*   48:     */ import javax.swing.JDialog;
/*   49:     */ import javax.swing.JFileChooser;
/*   50:     */ import javax.swing.JFrame;
/*   51:     */ import javax.swing.JLabel;
/*   52:     */ import javax.swing.JMenu;
/*   53:     */ import javax.swing.JMenuBar;
/*   54:     */ import javax.swing.JMenuItem;
/*   55:     */ import javax.swing.JOptionPane;
/*   56:     */ import javax.swing.JPanel;
/*   57:     */ import javax.swing.JPopupMenu;
/*   58:     */ import javax.swing.JScrollPane;
/*   59:     */ import javax.swing.JTable;
/*   60:     */ import javax.swing.JTextField;
/*   61:     */ import javax.swing.JToolBar;
/*   62:     */ import javax.swing.KeyStroke;
/*   63:     */ import javax.swing.table.AbstractTableModel;
/*   64:     */ import weka.classifiers.Classifier;
/*   65:     */ import weka.core.Instances;
/*   66:     */ import weka.core.OptionHandler;
/*   67:     */ import weka.core.SerializedObject;
/*   68:     */ import weka.core.Utils;
/*   69:     */ import weka.core.converters.AbstractFileLoader;
/*   70:     */ import weka.core.converters.AbstractFileSaver;
/*   71:     */ import weka.core.converters.ArffSaver;
/*   72:     */ import weka.core.converters.ConverterUtils;
/*   73:     */ import weka.core.logging.Logger;
/*   74:     */ import weka.core.logging.Logger.Level;
/*   75:     */ import weka.gui.ConverterFileChooser;
/*   76:     */ import weka.gui.ExtensionFileFilter;
/*   77:     */ import weka.gui.GenericObjectEditor;
/*   78:     */ import weka.gui.LookAndFeel;
/*   79:     */ import weka.gui.PropertyDialog;
/*   80:     */ import weka.gui.graphvisualizer.BIFFormatException;
/*   81:     */ import weka.gui.graphvisualizer.BIFParser;
/*   82:     */ import weka.gui.graphvisualizer.GraphEdge;
/*   83:     */ import weka.gui.graphvisualizer.GraphNode;
/*   84:     */ import weka.gui.graphvisualizer.HierarchicalBCEngine;
/*   85:     */ import weka.gui.graphvisualizer.LayoutCompleteEvent;
/*   86:     */ import weka.gui.graphvisualizer.LayoutCompleteEventListener;
/*   87:     */ import weka.gui.graphvisualizer.LayoutEngine;
/*   88:     */ import weka.gui.visualize.PrintablePanel;
/*   89:     */ 
/*   90:     */ public class GUI
/*   91:     */   extends JPanel
/*   92:     */   implements LayoutCompleteEventListener
/*   93:     */ {
/*   94:     */   private static final long serialVersionUID = -2038911085935515624L;
/*   95:     */   protected LayoutEngine m_layoutEngine;
/*   96:     */   protected GraphPanel m_GraphPanel;
/*   97: 127 */   EditableBayesNet m_BayesNet = new EditableBayesNet(true);
/*   98: 130 */   protected String m_sFileName = "";
/*   99: 132 */   MarginCalculator m_marginCalculator = null;
/*  100: 138 */   MarginCalculator m_marginCalculatorWithEvidence = null;
/*  101: 144 */   boolean m_bViewMargins = false;
/*  102: 145 */   boolean m_bViewCliques = false;
/*  103:     */   private JMenuBar m_menuBar;
/*  104: 151 */   Instances m_Instances = null;
/*  105:     */   final JTextField m_jTfZoom;
/*  106:     */   final JToolBar m_jTbTools;
/*  107:     */   final JLabel m_jStatusBar;
/*  108: 160 */   private final JTextField m_jTfNodeWidth = new JTextField(3);
/*  109: 162 */   private final JTextField m_jTfNodeHeight = new JTextField(3);
/*  110:     */   JScrollPane m_jScrollPane;
/*  111: 167 */   private final String ICONPATH = "weka/classifiers/bayes/net/icons/";
/*  112: 170 */   private double m_fScale = 1.0D;
/*  113: 173 */   private int m_nNodeHeight = 2 * getFontMetrics(getFont()).getHeight();
/*  114:     */   static final int DEFAULT_NODE_WIDTH = 50;
/*  115: 176 */   private int m_nNodeWidth = 50;
/*  116:     */   static final int PADDING = 10;
/*  117: 179 */   private int m_nPaddedNodeWidth = 60;
/*  118: 182 */   private final int[] m_nZoomPercents = { 10, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 350, 400, 450, 500, 550, 600, 650, 700, 800, 900, 999 };
/*  119: 187 */   Action a_new = new ActionNew();
/*  120: 189 */   Action a_quit = new ActionQuit();
/*  121: 190 */   Action a_save = new ActionSave();
/*  122: 191 */   ActionExport a_export = new ActionExport();
/*  123: 192 */   ActionPrint a_print = new ActionPrint();
/*  124: 193 */   Action a_load = new ActionLoad();
/*  125: 194 */   Action a_zoomin = new ActionZoomIn();
/*  126: 195 */   Action a_zoomout = new ActionZoomOut();
/*  127: 196 */   Action a_layout = new ActionLayout();
/*  128: 198 */   Action a_saveas = new ActionSaveAs();
/*  129: 200 */   Action a_viewtoolbar = new ActionViewToolbar();
/*  130: 202 */   Action a_viewstatusbar = new ActionViewStatusbar();
/*  131: 204 */   Action a_networkgenerator = new ActionGenerateNetwork();
/*  132: 206 */   Action a_datagenerator = new ActionGenerateData();
/*  133: 208 */   Action a_datasetter = new ActionSetData();
/*  134: 210 */   Action a_learn = new ActionLearn();
/*  135: 211 */   Action a_learnCPT = new ActionLearnCPT();
/*  136: 213 */   Action a_help = new ActionHelp();
/*  137: 215 */   Action a_about = new ActionAbout();
/*  138: 217 */   ActionAddNode a_addnode = new ActionAddNode();
/*  139: 219 */   Action a_delnode = new ActionDeleteNode();
/*  140: 220 */   Action a_cutnode = new ActionCutNode();
/*  141: 221 */   Action a_copynode = new ActionCopyNode();
/*  142: 222 */   Action a_pastenode = new ActionPasteNode();
/*  143: 223 */   Action a_selectall = new ActionSelectAll();
/*  144: 225 */   Action a_addarc = new ActionAddArc();
/*  145: 227 */   Action a_delarc = new ActionDeleteArc();
/*  146: 229 */   Action a_undo = new ActionUndo();
/*  147: 231 */   Action a_redo = new ActionRedo();
/*  148: 233 */   Action a_alignleft = new ActionAlignLeft();
/*  149: 234 */   Action a_alignright = new ActionAlignRight();
/*  150: 235 */   Action a_aligntop = new ActionAlignTop();
/*  151: 236 */   Action a_alignbottom = new ActionAlignBottom();
/*  152: 237 */   Action a_centerhorizontal = new ActionCenterHorizontal();
/*  153: 238 */   Action a_centervertical = new ActionCenterVertical();
/*  154: 239 */   Action a_spacehorizontal = new ActionSpaceHorizontal();
/*  155: 240 */   Action a_spacevertical = new ActionSpaceVertical();
/*  156: 243 */   int m_nCurrentNode = -1;
/*  157: 245 */   Selection m_Selection = new Selection();
/*  158: 247 */   Rectangle m_nSelectedRect = null;
/*  159:     */   
/*  160:     */   class Selection
/*  161:     */   {
/*  162:     */     ArrayList<Integer> m_selected;
/*  163:     */     
/*  164:     */     public Selection()
/*  165:     */     {
/*  166: 253 */       this.m_selected = new ArrayList();
/*  167:     */     }
/*  168:     */     
/*  169:     */     public ArrayList<Integer> getSelected()
/*  170:     */     {
/*  171: 257 */       return this.m_selected;
/*  172:     */     }
/*  173:     */     
/*  174:     */     void updateGUI()
/*  175:     */     {
/*  176: 261 */       if (this.m_selected.size() > 0)
/*  177:     */       {
/*  178: 262 */         GUI.this.a_cutnode.setEnabled(true);
/*  179: 263 */         GUI.this.a_copynode.setEnabled(true);
/*  180:     */       }
/*  181:     */       else
/*  182:     */       {
/*  183: 265 */         GUI.this.a_cutnode.setEnabled(false);
/*  184: 266 */         GUI.this.a_copynode.setEnabled(false);
/*  185:     */       }
/*  186: 268 */       if (this.m_selected.size() > 1)
/*  187:     */       {
/*  188: 269 */         GUI.this.a_alignleft.setEnabled(true);
/*  189: 270 */         GUI.this.a_alignright.setEnabled(true);
/*  190: 271 */         GUI.this.a_aligntop.setEnabled(true);
/*  191: 272 */         GUI.this.a_alignbottom.setEnabled(true);
/*  192: 273 */         GUI.this.a_centerhorizontal.setEnabled(true);
/*  193: 274 */         GUI.this.a_centervertical.setEnabled(true);
/*  194: 275 */         GUI.this.a_spacehorizontal.setEnabled(true);
/*  195: 276 */         GUI.this.a_spacevertical.setEnabled(true);
/*  196:     */       }
/*  197:     */       else
/*  198:     */       {
/*  199: 278 */         GUI.this.a_alignleft.setEnabled(false);
/*  200: 279 */         GUI.this.a_alignright.setEnabled(false);
/*  201: 280 */         GUI.this.a_aligntop.setEnabled(false);
/*  202: 281 */         GUI.this.a_alignbottom.setEnabled(false);
/*  203: 282 */         GUI.this.a_centerhorizontal.setEnabled(false);
/*  204: 283 */         GUI.this.a_centervertical.setEnabled(false);
/*  205: 284 */         GUI.this.a_spacehorizontal.setEnabled(false);
/*  206: 285 */         GUI.this.a_spacevertical.setEnabled(false);
/*  207:     */       }
/*  208:     */     }
/*  209:     */     
/*  210:     */     public void addToSelection(int nNode)
/*  211:     */     {
/*  212: 290 */       for (int iNode = 0; iNode < this.m_selected.size(); iNode++) {
/*  213: 291 */         if (nNode == ((Integer)this.m_selected.get(iNode)).intValue()) {
/*  214: 292 */           return;
/*  215:     */         }
/*  216:     */       }
/*  217: 295 */       this.m_selected.add(Integer.valueOf(nNode));
/*  218: 296 */       updateGUI();
/*  219:     */     }
/*  220:     */     
/*  221:     */     public void addToSelection(int[] iNodes)
/*  222:     */     {
/*  223: 300 */       for (int iNode2 : iNodes) {
/*  224: 301 */         addToSelection(iNode2);
/*  225:     */       }
/*  226: 303 */       updateGUI();
/*  227:     */     }
/*  228:     */     
/*  229:     */     public void addToSelection(Rectangle selectedRect)
/*  230:     */     {
/*  231: 307 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/*  232: 308 */         if (contains(selectedRect, iNode)) {
/*  233: 309 */           addToSelection(iNode);
/*  234:     */         }
/*  235:     */       }
/*  236:     */     }
/*  237:     */     
/*  238:     */     public void selectAll()
/*  239:     */     {
/*  240: 315 */       this.m_selected.clear();
/*  241: 316 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/*  242: 317 */         this.m_selected.add(Integer.valueOf(iNode));
/*  243:     */       }
/*  244: 319 */       updateGUI();
/*  245:     */     }
/*  246:     */     
/*  247:     */     boolean contains(Rectangle rect, int iNode)
/*  248:     */     {
/*  249: 323 */       return rect.intersects(GUI.this.m_BayesNet.getPositionX(iNode) * GUI.this.m_fScale, GUI.this.m_BayesNet.getPositionY(iNode) * GUI.this.m_fScale, GUI.this.m_nPaddedNodeWidth * GUI.this.m_fScale, GUI.this.m_nNodeHeight * GUI.this.m_fScale);
/*  250:     */     }
/*  251:     */     
/*  252:     */     public void removeFromSelection(int nNode)
/*  253:     */     {
/*  254: 329 */       for (int iNode = 0; iNode < this.m_selected.size(); iNode++) {
/*  255: 330 */         if (nNode == ((Integer)this.m_selected.get(iNode)).intValue()) {
/*  256: 331 */           this.m_selected.remove(iNode);
/*  257:     */         }
/*  258:     */       }
/*  259: 334 */       updateGUI();
/*  260:     */     }
/*  261:     */     
/*  262:     */     public void toggleSelection(int nNode)
/*  263:     */     {
/*  264: 338 */       for (int iNode = 0; iNode < this.m_selected.size(); iNode++) {
/*  265: 339 */         if (nNode == ((Integer)this.m_selected.get(iNode)).intValue())
/*  266:     */         {
/*  267: 340 */           this.m_selected.remove(iNode);
/*  268: 341 */           updateGUI();
/*  269: 342 */           return;
/*  270:     */         }
/*  271:     */       }
/*  272: 345 */       addToSelection(nNode);
/*  273:     */     }
/*  274:     */     
/*  275:     */     public void toggleSelection(Rectangle selectedRect)
/*  276:     */     {
/*  277: 349 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/*  278: 350 */         if (contains(selectedRect, iNode)) {
/*  279: 351 */           toggleSelection(iNode);
/*  280:     */         }
/*  281:     */       }
/*  282:     */     }
/*  283:     */     
/*  284:     */     public void clear()
/*  285:     */     {
/*  286: 357 */       this.m_selected.clear();
/*  287: 358 */       updateGUI();
/*  288:     */     }
/*  289:     */     
/*  290:     */     public void draw(Graphics g)
/*  291:     */     {
/*  292: 362 */       if (this.m_selected.size() == 0) {
/*  293: 363 */         return;
/*  294:     */       }
/*  295: 366 */       for (int iNode = 0; iNode < this.m_selected.size(); iNode++)
/*  296:     */       {
/*  297: 367 */         int nNode = ((Integer)this.m_selected.get(iNode)).intValue();
/*  298: 368 */         int nPosX = GUI.this.m_BayesNet.getPositionX(nNode);
/*  299: 369 */         int nPosY = GUI.this.m_BayesNet.getPositionY(nNode);
/*  300: 370 */         g.setColor(Color.BLACK);
/*  301: 371 */         int nXRC = nPosX + GUI.this.m_nPaddedNodeWidth - GUI.this.m_nNodeWidth - (GUI.this.m_nPaddedNodeWidth - GUI.this.m_nNodeWidth) / 2;
/*  302:     */         
/*  303: 373 */         int nYRC = nPosY;
/*  304: 374 */         int d = 5;
/*  305: 375 */         g.fillRect(nXRC, nYRC, d, d);
/*  306: 376 */         g.fillRect(nXRC, nYRC + GUI.this.m_nNodeHeight, d, d);
/*  307: 377 */         g.fillRect(nXRC + GUI.this.m_nNodeWidth, nYRC, d, d);
/*  308: 378 */         g.fillRect(nXRC + GUI.this.m_nNodeWidth, nYRC + GUI.this.m_nNodeHeight, d, d);
/*  309:     */       }
/*  310:     */     }
/*  311:     */   }
/*  312:     */   
/*  313: 383 */   ClipBoard m_clipboard = new ClipBoard();
/*  314:     */   
/*  315:     */   class ClipBoard
/*  316:     */   {
/*  317: 386 */     String m_sText = null;
/*  318:     */     
/*  319:     */     public ClipBoard()
/*  320:     */     {
/*  321: 389 */       if (GUI.this.a_pastenode != null) {
/*  322: 390 */         GUI.this.a_pastenode.setEnabled(false);
/*  323:     */       }
/*  324:     */     }
/*  325:     */     
/*  326:     */     public boolean hasText()
/*  327:     */     {
/*  328: 395 */       return this.m_sText != null;
/*  329:     */     }
/*  330:     */     
/*  331:     */     public String getText()
/*  332:     */     {
/*  333: 399 */       return this.m_sText;
/*  334:     */     }
/*  335:     */     
/*  336:     */     public void setText(String sText)
/*  337:     */     {
/*  338: 403 */       this.m_sText = sText;
/*  339: 404 */       GUI.this.a_pastenode.setEnabled(true);
/*  340:     */     }
/*  341:     */   }
/*  342:     */   
/*  343:     */   class MyAction
/*  344:     */     extends AbstractAction
/*  345:     */   {
/*  346:     */     private static final long serialVersionUID = -2038911111935517L;
/*  347:     */     
/*  348:     */     public MyAction(String sName, String sToolTipText, String sIcon, String sAcceleratorKey)
/*  349:     */     {
/*  350: 418 */       super();
/*  351:     */       
/*  352: 420 */       putValue("ShortDescription", sToolTipText);
/*  353: 421 */       putValue("LongDescription", sToolTipText);
/*  354:     */       KeyStroke keyStroke;
/*  355: 422 */       if (sAcceleratorKey.length() > 0)
/*  356:     */       {
/*  357: 423 */         keyStroke = KeyStroke.getKeyStroke(sAcceleratorKey);
/*  358: 424 */         putValue("AcceleratorKey", keyStroke);
/*  359:     */       }
/*  360: 426 */       putValue("MnemonicKey", Integer.valueOf(sName.charAt(0)));
/*  361: 427 */       URL tempURL = ClassLoader.getSystemResource("weka/classifiers/bayes/net/icons/" + sIcon + ".png");
/*  362: 429 */       if (tempURL != null) {
/*  363: 430 */         putValue("SmallIcon", new ImageIcon(tempURL));
/*  364:     */       } else {
/*  365: 432 */         putValue("SmallIcon", new ImageIcon(new BufferedImage(20, 20, 6)));
/*  366:     */       }
/*  367:     */     }
/*  368:     */     
/*  369:     */     public void actionPerformed(ActionEvent ae) {}
/*  370:     */   }
/*  371:     */   
/*  372:     */   class ActionGenerateNetwork
/*  373:     */     extends GUI.MyAction
/*  374:     */   {
/*  375:     */     private static final long serialVersionUID = -2038911085935517L;
/*  376:     */     
/*  377:     */     public ActionGenerateNetwork()
/*  378:     */     {
/*  379: 455 */       super("Generate Network", "Generate Random Bayesian Network", "generate.network", "ctrl N");
/*  380:     */     }
/*  381:     */     
/*  382: 459 */     int m_nNrOfNodes = 10;
/*  383: 461 */     int m_nNrOfArcs = 15;
/*  384: 463 */     int m_nCardinality = 2;
/*  385: 465 */     int m_nSeed = 123;
/*  386: 467 */     JDialog dlg = null;
/*  387:     */     
/*  388:     */     public void actionPerformed(ActionEvent ae)
/*  389:     */     {
/*  390: 471 */       if (this.dlg == null)
/*  391:     */       {
/*  392: 472 */         this.dlg = new JDialog();
/*  393: 473 */         this.dlg.setTitle("Generate Random Bayesian Network Options");
/*  394:     */         
/*  395: 475 */         JLabel jLbNrOfNodes = new JLabel("Nr of nodes");
/*  396: 476 */         final JTextField jTfNrOfNodes = new JTextField(3);
/*  397: 477 */         jTfNrOfNodes.setHorizontalAlignment(0);
/*  398: 478 */         jTfNrOfNodes.setText("" + this.m_nNrOfNodes);
/*  399: 479 */         JLabel jLbNrOfArcs = new JLabel("Nr of arcs");
/*  400: 480 */         final JTextField jTfNrOfArcs = new JTextField(3);
/*  401: 481 */         jTfNrOfArcs.setHorizontalAlignment(0);
/*  402: 482 */         jTfNrOfArcs.setText("" + this.m_nNrOfArcs);
/*  403: 483 */         JLabel jLbCardinality = new JLabel("Cardinality");
/*  404: 484 */         final JTextField jTfCardinality = new JTextField(3);
/*  405: 485 */         jTfCardinality.setHorizontalAlignment(0);
/*  406: 486 */         jTfCardinality.setText("" + this.m_nCardinality);
/*  407: 487 */         JLabel jLbSeed = new JLabel("Random seed");
/*  408: 488 */         final JTextField jTfSeed = new JTextField(3);
/*  409: 489 */         jTfSeed.setHorizontalAlignment(0);
/*  410: 490 */         jTfSeed.setText("" + this.m_nSeed);
/*  411:     */         
/*  412:     */ 
/*  413: 493 */         JButton jBtGo = new JButton("Generate Network");
/*  414:     */         
/*  415: 495 */         jBtGo.addActionListener(new ActionListener()
/*  416:     */         {
/*  417:     */           public void actionPerformed(ActionEvent ae)
/*  418:     */           {
/*  419:     */             try
/*  420:     */             {
/*  421: 499 */               BayesNetGenerator generator = new BayesNetGenerator();
/*  422: 500 */               GUI.this.m_BayesNet = generator;
/*  423: 501 */               GUI.this.m_BayesNet.clearUndoStack();
/*  424:     */               
/*  425: 503 */               String[] options = new String[8];
/*  426: 504 */               options[0] = "-N";
/*  427: 505 */               options[1] = ("" + jTfNrOfNodes.getText());
/*  428: 506 */               options[2] = "-A";
/*  429: 507 */               options[3] = ("" + jTfNrOfArcs.getText());
/*  430: 508 */               options[4] = "-C";
/*  431: 509 */               options[5] = ("" + jTfCardinality.getText());
/*  432: 510 */               options[6] = "-S";
/*  433: 511 */               options[7] = ("" + jTfSeed.getText());
/*  434: 512 */               generator.setOptions(options);
/*  435: 513 */               generator.generateRandomNetwork();
/*  436:     */               
/*  437:     */ 
/*  438:     */ 
/*  439: 517 */               BIFReader bifReader = new BIFReader();
/*  440: 518 */               bifReader.processString(GUI.this.m_BayesNet.toXMLBIF03());
/*  441: 519 */               GUI.this.m_BayesNet = new EditableBayesNet(bifReader);
/*  442:     */               
/*  443: 521 */               GUI.this.updateStatus();
/*  444: 522 */               GUI.this.layoutGraph();
/*  445: 523 */               GUI.this.a_datagenerator.setEnabled(true);
/*  446: 524 */               GUI.this.m_Instances = null;
/*  447:     */               
/*  448: 526 */               GUI.this.a_learn.setEnabled(false);
/*  449: 527 */               GUI.this.a_learnCPT.setEnabled(false);
/*  450:     */               
/*  451: 529 */               GUI.ActionGenerateNetwork.this.dlg.setVisible(false);
/*  452:     */             }
/*  453:     */             catch (Exception e)
/*  454:     */             {
/*  455: 531 */               e.printStackTrace();
/*  456:     */             }
/*  457:     */           }
/*  458: 536 */         });
/*  459: 537 */         JButton jBtCancel = new JButton("Cancel");
/*  460: 538 */         jBtCancel.setMnemonic('C');
/*  461: 539 */         jBtCancel.addActionListener(new ActionListener()
/*  462:     */         {
/*  463:     */           public void actionPerformed(ActionEvent ae)
/*  464:     */           {
/*  465: 542 */             GUI.ActionGenerateNetwork.this.dlg.setVisible(false);
/*  466:     */           }
/*  467: 544 */         });
/*  468: 545 */         GridBagConstraints gbc = new GridBagConstraints();
/*  469: 546 */         this.dlg.setLayout(new GridBagLayout());
/*  470:     */         
/*  471: 548 */         Container c = new Container();
/*  472: 549 */         c.setLayout(new GridBagLayout());
/*  473: 550 */         gbc.gridwidth = 2;
/*  474: 551 */         gbc.insets = new Insets(8, 0, 0, 0);
/*  475: 552 */         gbc.anchor = 18;
/*  476: 553 */         gbc.gridwidth = -1;
/*  477: 554 */         gbc.fill = 2;
/*  478: 555 */         c.add(jLbNrOfNodes, gbc);
/*  479: 556 */         gbc.gridwidth = 0;
/*  480: 557 */         c.add(jTfNrOfNodes, gbc);
/*  481: 558 */         gbc.gridwidth = -1;
/*  482: 559 */         c.add(jLbNrOfArcs, gbc);
/*  483: 560 */         gbc.gridwidth = 0;
/*  484: 561 */         c.add(jTfNrOfArcs, gbc);
/*  485: 562 */         gbc.gridwidth = -1;
/*  486: 563 */         c.add(jLbCardinality, gbc);
/*  487: 564 */         gbc.gridwidth = 0;
/*  488: 565 */         c.add(jTfCardinality, gbc);
/*  489: 566 */         gbc.gridwidth = -1;
/*  490: 567 */         c.add(jLbSeed, gbc);
/*  491: 568 */         gbc.gridwidth = 0;
/*  492: 569 */         c.add(jTfSeed, gbc);
/*  493:     */         
/*  494: 571 */         gbc.fill = 2;
/*  495: 572 */         this.dlg.add(c, gbc);
/*  496: 573 */         this.dlg.add(jBtGo);
/*  497: 574 */         gbc.gridwidth = 0;
/*  498: 575 */         this.dlg.add(jBtCancel);
/*  499:     */       }
/*  500: 577 */       this.dlg.pack();
/*  501: 578 */       this.dlg.setLocation(100, 100);
/*  502: 579 */       this.dlg.setVisible(true);
/*  503: 580 */       this.dlg.setSize(this.dlg.getPreferredSize());
/*  504: 581 */       this.dlg.setVisible(false);
/*  505: 582 */       this.dlg.setVisible(true);
/*  506: 583 */       this.dlg.repaint();
/*  507:     */     }
/*  508:     */   }
/*  509:     */   
/*  510:     */   class ActionGenerateData
/*  511:     */     extends GUI.MyAction
/*  512:     */   {
/*  513:     */     private static final long serialVersionUID = -2038911085935516L;
/*  514:     */     
/*  515:     */     public ActionGenerateData()
/*  516:     */     {
/*  517: 592 */       super("Generate Data", "Generate Random Instances from Network", "generate.data", "ctrl D");
/*  518:     */     }
/*  519:     */     
/*  520: 596 */     int m_nNrOfInstances = 100;
/*  521: 598 */     int m_nSeed = 1234;
/*  522: 600 */     String m_sFile = "";
/*  523: 602 */     JDialog dlg = null;
/*  524:     */     
/*  525:     */     public void actionPerformed(ActionEvent ae)
/*  526:     */     {
/*  527: 606 */       if (this.dlg == null)
/*  528:     */       {
/*  529: 607 */         this.dlg = new JDialog();
/*  530: 608 */         this.dlg.setTitle("Generate Random Data Options");
/*  531:     */         
/*  532: 610 */         JLabel jLbNrOfInstances = new JLabel("Nr of instances");
/*  533: 611 */         final JTextField jTfNrOfInstances = new JTextField(3);
/*  534: 612 */         jTfNrOfInstances.setHorizontalAlignment(0);
/*  535: 613 */         jTfNrOfInstances.setText("" + this.m_nNrOfInstances);
/*  536: 614 */         JLabel jLbSeed = new JLabel("Random seed");
/*  537: 615 */         JTextField jTfSeed = new JTextField(3);
/*  538: 616 */         jTfSeed.setHorizontalAlignment(0);
/*  539: 617 */         jTfSeed.setText("" + this.m_nSeed);
/*  540: 618 */         JLabel jLbFile = new JLabel("Output file (optional)");
/*  541: 619 */         final JTextField jTfFile = new JTextField(12);
/*  542: 620 */         jTfFile.setHorizontalAlignment(0);
/*  543: 621 */         jTfFile.setText(this.m_sFile);
/*  544:     */         
/*  545:     */ 
/*  546: 624 */         JButton jBtGo = new JButton("Generate Data");
/*  547:     */         
/*  548: 626 */         jBtGo.addActionListener(new ActionListener()
/*  549:     */         {
/*  550:     */           public void actionPerformed(ActionEvent ae)
/*  551:     */           {
/*  552:     */             try
/*  553:     */             {
/*  554: 630 */               String tmpfilename = "tmp.bif.file.xml";
/*  555: 631 */               BayesNetGenerator generator = new BayesNetGenerator();
/*  556: 632 */               String[] options = new String[4];
/*  557: 633 */               options[0] = "-M";
/*  558: 634 */               options[1] = ("" + jTfNrOfInstances.getText());
/*  559: 635 */               options[2] = "-F";
/*  560: 636 */               options[3] = tmpfilename;
/*  561: 637 */               FileWriter outfile = new FileWriter(tmpfilename);
/*  562: 638 */               StringBuffer text = new StringBuffer();
/*  563: 639 */               if (GUI.this.m_marginCalculator == null)
/*  564:     */               {
/*  565: 640 */                 GUI.this.m_marginCalculator = new MarginCalculator();
/*  566: 641 */                 GUI.this.m_marginCalculator.calcMargins(GUI.this.m_BayesNet);
/*  567:     */               }
/*  568: 643 */               text.append(GUI.this.m_marginCalculator.toXMLBIF03());
/*  569: 644 */               outfile.write(text.toString());
/*  570: 645 */               outfile.close();
/*  571:     */               
/*  572: 647 */               generator.setOptions(options);
/*  573: 648 */               generator.generateRandomNetwork();
/*  574: 649 */               generator.generateInstances();
/*  575: 650 */               GUI.this.m_Instances = generator.m_Instances;
/*  576: 651 */               GUI.this.a_learn.setEnabled(true);
/*  577: 652 */               GUI.this.a_learnCPT.setEnabled(true);
/*  578:     */               
/*  579: 654 */               GUI.ActionGenerateData.this.m_sFile = jTfFile.getText();
/*  580: 655 */               if ((GUI.ActionGenerateData.this.m_sFile != null) && (!GUI.ActionGenerateData.this.m_sFile.equals("")))
/*  581:     */               {
/*  582: 656 */                 AbstractFileSaver saver = ConverterUtils.getSaverForFile(GUI.ActionGenerateData.this.m_sFile);
/*  583: 659 */                 if (saver == null) {
/*  584: 660 */                   saver = new ArffSaver();
/*  585:     */                 }
/*  586: 662 */                 saver.setFile(new File(GUI.ActionGenerateData.this.m_sFile));
/*  587: 663 */                 saver.setInstances(GUI.this.m_Instances);
/*  588: 664 */                 saver.writeBatch();
/*  589:     */               }
/*  590:     */             }
/*  591:     */             catch (Exception e)
/*  592:     */             {
/*  593: 668 */               e.printStackTrace();
/*  594:     */             }
/*  595: 670 */             GUI.ActionGenerateData.this.dlg.setVisible(false);
/*  596:     */           }
/*  597: 673 */         });
/*  598: 674 */         JButton jBtFile = new JButton("Browse");
/*  599: 675 */         jBtFile.addActionListener(new ActionListener()
/*  600:     */         {
/*  601:     */           public void actionPerformed(ActionEvent ae)
/*  602:     */           {
/*  603: 678 */             ConverterFileChooser fc = new ConverterFileChooser(System.getProperty("user.dir"));
/*  604:     */             
/*  605: 680 */             fc.setDialogTitle("Save Instances As");
/*  606: 681 */             int rval = fc.showSaveDialog(GUI.this);
/*  607: 683 */             if (rval == 0)
/*  608:     */             {
/*  609: 684 */               String filename = fc.getSelectedFile().toString();
/*  610: 685 */               jTfFile.setText(filename);
/*  611:     */             }
/*  612: 687 */             GUI.ActionGenerateData.this.dlg.setVisible(true);
/*  613:     */           }
/*  614: 690 */         });
/*  615: 691 */         JButton jBtCancel = new JButton("Cancel");
/*  616: 692 */         jBtCancel.setMnemonic('C');
/*  617: 693 */         jBtCancel.addActionListener(new ActionListener()
/*  618:     */         {
/*  619:     */           public void actionPerformed(ActionEvent ae)
/*  620:     */           {
/*  621: 696 */             GUI.ActionGenerateData.this.dlg.setVisible(false);
/*  622:     */           }
/*  623: 698 */         });
/*  624: 699 */         GridBagConstraints gbc = new GridBagConstraints();
/*  625: 700 */         this.dlg.setLayout(new GridBagLayout());
/*  626:     */         
/*  627: 702 */         Container c = new Container();
/*  628: 703 */         c.setLayout(new GridBagLayout());
/*  629: 704 */         gbc.gridwidth = 2;
/*  630: 705 */         gbc.insets = new Insets(8, 0, 0, 0);
/*  631: 706 */         gbc.anchor = 18;
/*  632: 707 */         gbc.gridwidth = -1;
/*  633: 708 */         gbc.fill = 2;
/*  634: 709 */         c.add(jLbNrOfInstances, gbc);
/*  635: 710 */         gbc.gridwidth = 0;
/*  636: 711 */         c.add(jTfNrOfInstances, gbc);
/*  637: 712 */         gbc.gridwidth = -1;
/*  638: 713 */         c.add(jLbSeed, gbc);
/*  639: 714 */         gbc.gridwidth = 0;
/*  640: 715 */         c.add(jTfSeed, gbc);
/*  641: 716 */         gbc.gridwidth = -1;
/*  642: 717 */         c.add(jLbFile, gbc);
/*  643: 718 */         gbc.gridwidth = 0;
/*  644: 719 */         c.add(jTfFile, gbc);
/*  645: 720 */         gbc.gridwidth = 0;
/*  646: 721 */         c.add(jBtFile, gbc);
/*  647:     */         
/*  648: 723 */         gbc.fill = 2;
/*  649: 724 */         this.dlg.add(c, gbc);
/*  650: 725 */         this.dlg.add(jBtGo);
/*  651: 726 */         gbc.gridwidth = 0;
/*  652: 727 */         this.dlg.add(jBtCancel);
/*  653:     */       }
/*  654: 729 */       this.dlg.setLocation(100, 100);
/*  655: 730 */       this.dlg.setVisible(true);
/*  656: 731 */       this.dlg.setSize(this.dlg.getPreferredSize());
/*  657: 732 */       this.dlg.setVisible(false);
/*  658: 733 */       this.dlg.setVisible(true);
/*  659: 734 */       this.dlg.repaint();
/*  660:     */     }
/*  661:     */   }
/*  662:     */   
/*  663:     */   class ActionLearn
/*  664:     */     extends GUI.MyAction
/*  665:     */   {
/*  666:     */     private static final long serialVersionUID = -2038911085935516L;
/*  667:     */     
/*  668:     */     public ActionLearn()
/*  669:     */     {
/*  670: 744 */       super("Learn Network", "Learn Bayesian Network", "learn", "ctrl L");
/*  671: 745 */       setEnabled(false);
/*  672:     */     }
/*  673:     */     
/*  674: 748 */     JDialog dlg = null;
/*  675:     */     
/*  676:     */     public void actionPerformed(ActionEvent ae)
/*  677:     */     {
/*  678: 752 */       if (this.dlg == null)
/*  679:     */       {
/*  680: 753 */         this.dlg = new JDialog();
/*  681: 754 */         this.dlg.setTitle("Learn Bayesian Network");
/*  682:     */         
/*  683: 756 */         JButton jBtOptions = new JButton("Options");
/*  684: 757 */         jBtOptions.addActionListener(new ActionListener()
/*  685:     */         {
/*  686:     */           public void actionPerformed(ActionEvent ae)
/*  687:     */           {
/*  688:     */             try
/*  689:     */             {
/*  690: 762 */               GenericObjectEditor.registerEditors();
/*  691: 763 */               GenericObjectEditor ce = new GenericObjectEditor(true);
/*  692: 764 */               ce.setClassType(Classifier.class);
/*  693: 765 */               ce.setValue(GUI.this.m_BayesNet);
/*  694:     */               PropertyDialog pd;
/*  695:     */               PropertyDialog pd;
/*  696: 768 */               if (PropertyDialog.getParentDialog(GUI.this) != null) {
/*  697: 769 */                 pd = new PropertyDialog(PropertyDialog.getParentDialog(GUI.this), ce, 100, 100);
/*  698:     */               } else {
/*  699: 772 */                 pd = new PropertyDialog(PropertyDialog.getParentFrame(GUI.this), ce, 100, 100);
/*  700:     */               }
/*  701: 775 */               pd.addWindowListener(new WindowAdapter()
/*  702:     */               {
/*  703:     */                 public void windowClosing(WindowEvent e)
/*  704:     */                 {
/*  705: 778 */                   PropertyEditor pe = ((PropertyDialog)e.getSource()).getEditor();
/*  706:     */                   
/*  707: 780 */                   Object c = pe.getValue();
/*  708: 781 */                   String options = "";
/*  709: 782 */                   if ((c instanceof OptionHandler))
/*  710:     */                   {
/*  711: 783 */                     options = Utils.joinOptions(((OptionHandler)c).getOptions());
/*  712:     */                     try
/*  713:     */                     {
/*  714: 786 */                       GUI.this.m_BayesNet.setOptions(((OptionHandler)c).getOptions());
/*  715:     */                     }
/*  716:     */                     catch (Exception e2)
/*  717:     */                     {
/*  718: 788 */                       e2.printStackTrace();
/*  719:     */                     }
/*  720:     */                   }
/*  721: 791 */                   System.out.println(c.getClass().getName() + " " + options);
/*  722: 792 */                   System.exit(0);
/*  723:     */                 }
/*  724: 794 */               });
/*  725: 795 */               pd.setVisible(true);
/*  726:     */             }
/*  727:     */             catch (Exception ex)
/*  728:     */             {
/*  729: 797 */               ex.printStackTrace();
/*  730: 798 */               System.err.println(ex.getMessage());
/*  731:     */             }
/*  732: 800 */             GUI.this.m_BayesNet.clearUndoStack();
/*  733: 801 */             GUI.this.a_undo.setEnabled(false);
/*  734: 802 */             GUI.this.a_redo.setEnabled(false);
/*  735:     */           }
/*  736: 805 */         });
/*  737: 806 */         JTextField jTfOptions = new JTextField(40);
/*  738: 807 */         jTfOptions.setHorizontalAlignment(0);
/*  739: 808 */         jTfOptions.setText("" + Utils.joinOptions(GUI.this.m_BayesNet.getOptions()));
/*  740:     */         
/*  741:     */ 
/*  742: 811 */         JButton jBtGo = new JButton("Learn");
/*  743:     */         
/*  744: 813 */         jBtGo.addActionListener(new ActionListener()
/*  745:     */         {
/*  746:     */           public void actionPerformed(ActionEvent ae)
/*  747:     */           {
/*  748:     */             try
/*  749:     */             {
/*  750: 817 */               GUI.this.m_BayesNet.buildClassifier(GUI.this.m_Instances);
/*  751: 818 */               GUI.this.layoutGraph();
/*  752: 819 */               GUI.this.updateStatus();
/*  753: 820 */               GUI.this.m_BayesNet.clearUndoStack();
/*  754:     */               
/*  755: 822 */               GUI.ActionLearn.this.dlg.setVisible(false);
/*  756:     */             }
/*  757:     */             catch (Exception e)
/*  758:     */             {
/*  759: 824 */               e.printStackTrace();
/*  760:     */             }
/*  761: 826 */             GUI.ActionLearn.this.dlg.setVisible(false);
/*  762:     */           }
/*  763: 830 */         });
/*  764: 831 */         JButton jBtCancel = new JButton("Cancel");
/*  765: 832 */         jBtCancel.setMnemonic('C');
/*  766: 833 */         jBtCancel.addActionListener(new ActionListener()
/*  767:     */         {
/*  768:     */           public void actionPerformed(ActionEvent ae)
/*  769:     */           {
/*  770: 836 */             GUI.ActionLearn.this.dlg.setVisible(false);
/*  771:     */           }
/*  772: 838 */         });
/*  773: 839 */         GridBagConstraints gbc = new GridBagConstraints();
/*  774: 840 */         this.dlg.setLayout(new GridBagLayout());
/*  775:     */         
/*  776: 842 */         Container c = new Container();
/*  777: 843 */         c.setLayout(new GridBagLayout());
/*  778: 844 */         gbc.gridwidth = 2;
/*  779: 845 */         gbc.insets = new Insets(8, 0, 0, 0);
/*  780: 846 */         gbc.anchor = 18;
/*  781: 847 */         gbc.gridwidth = -1;
/*  782: 848 */         gbc.fill = 2;
/*  783: 849 */         c.add(jBtOptions, gbc);
/*  784: 850 */         gbc.gridwidth = 0;
/*  785: 851 */         c.add(jTfOptions, gbc);
/*  786:     */         
/*  787: 853 */         gbc.fill = 2;
/*  788: 854 */         this.dlg.add(c, gbc);
/*  789: 855 */         this.dlg.add(jBtGo);
/*  790: 856 */         gbc.gridwidth = 0;
/*  791: 857 */         this.dlg.add(jBtCancel);
/*  792:     */       }
/*  793: 859 */       this.dlg.setLocation(100, 100);
/*  794: 860 */       this.dlg.setVisible(true);
/*  795: 861 */       this.dlg.setSize(this.dlg.getPreferredSize());
/*  796: 862 */       this.dlg.setVisible(false);
/*  797: 863 */       this.dlg.setVisible(true);
/*  798: 864 */       this.dlg.repaint();
/*  799:     */     }
/*  800:     */   }
/*  801:     */   
/*  802:     */   class ActionLearnCPT
/*  803:     */     extends GUI.MyAction
/*  804:     */   {
/*  805:     */     private static final long serialVersionUID = -2022211085935516L;
/*  806:     */     
/*  807:     */     public ActionLearnCPT()
/*  808:     */     {
/*  809: 873 */       super("Learn CPT", "Learn conditional probability tables", "learncpt", "");
/*  810: 874 */       setEnabled(false);
/*  811:     */     }
/*  812:     */     
/*  813:     */     public void actionPerformed(ActionEvent ae)
/*  814:     */     {
/*  815: 879 */       if (GUI.this.m_Instances == null)
/*  816:     */       {
/*  817: 880 */         JOptionPane.showMessageDialog(null, "Select instances to learn from first (menu Tools/Set Data)");
/*  818:     */         
/*  819: 882 */         return;
/*  820:     */       }
/*  821:     */       try
/*  822:     */       {
/*  823: 885 */         GUI.this.m_BayesNet.setData(GUI.this.m_Instances);
/*  824:     */       }
/*  825:     */       catch (Exception e)
/*  826:     */       {
/*  827: 887 */         JOptionPane.showMessageDialog(null, "Data set is not compatible with network.\n" + e.getMessage() + "\nChoose other instances (menu Tools/Set Data)");
/*  828:     */         
/*  829:     */ 
/*  830: 890 */         return;
/*  831:     */       }
/*  832:     */       try
/*  833:     */       {
/*  834: 893 */         GUI.this.m_BayesNet.estimateCPTs();
/*  835: 894 */         GUI.this.m_BayesNet.clearUndoStack();
/*  836:     */       }
/*  837:     */       catch (Exception e)
/*  838:     */       {
/*  839: 896 */         e.printStackTrace();
/*  840:     */       }
/*  841: 898 */       GUI.this.updateStatus();
/*  842:     */     }
/*  843:     */   }
/*  844:     */   
/*  845:     */   class ActionSetData
/*  846:     */     extends GUI.MyAction
/*  847:     */   {
/*  848:     */     private static final long serialVersionUID = -2038911085935519L;
/*  849:     */     
/*  850:     */     public ActionSetData()
/*  851:     */     {
/*  852: 907 */       super("Set Data", "Set Data File", "setdata", "ctrl A");
/*  853:     */     }
/*  854:     */     
/*  855:     */     public void actionPerformed(ActionEvent ae)
/*  856:     */     {
/*  857: 912 */       ConverterFileChooser fc = new ConverterFileChooser(System.getProperty("user.dir"));
/*  858:     */       
/*  859: 914 */       fc.setDialogTitle("Set Data File");
/*  860: 915 */       int rval = fc.showOpenDialog(GUI.this);
/*  861: 917 */       if (rval == 0)
/*  862:     */       {
/*  863: 918 */         AbstractFileLoader loader = fc.getLoader();
/*  864:     */         try
/*  865:     */         {
/*  866: 920 */           if (loader != null) {
/*  867: 921 */             GUI.this.m_Instances = loader.getDataSet();
/*  868:     */           }
/*  869: 923 */           if (GUI.this.m_Instances.classIndex() == -1) {
/*  870: 924 */             GUI.this.m_Instances.setClassIndex(GUI.this.m_Instances.numAttributes() - 1);
/*  871:     */           }
/*  872: 926 */           GUI.this.a_learn.setEnabled(true);
/*  873: 927 */           GUI.this.a_learnCPT.setEnabled(true);
/*  874: 928 */           GUI.this.repaint();
/*  875:     */         }
/*  876:     */         catch (Exception e)
/*  877:     */         {
/*  878: 930 */           e.printStackTrace();
/*  879:     */         }
/*  880:     */       }
/*  881:     */     }
/*  882:     */   }
/*  883:     */   
/*  884:     */   class ActionUndo
/*  885:     */     extends GUI.MyAction
/*  886:     */   {
/*  887:     */     private static final long serialVersionUID = -3038910085935519L;
/*  888:     */     
/*  889:     */     public ActionUndo()
/*  890:     */     {
/*  891: 941 */       super("Undo", "Undo", "undo", "ctrl Z");
/*  892: 942 */       setEnabled(false);
/*  893:     */     }
/*  894:     */     
/*  895:     */     public boolean isEnabled()
/*  896:     */     {
/*  897: 947 */       return GUI.this.m_BayesNet.canUndo();
/*  898:     */     }
/*  899:     */     
/*  900:     */     public void actionPerformed(ActionEvent ae)
/*  901:     */     {
/*  902: 952 */       String sMsg = GUI.this.m_BayesNet.undo();
/*  903: 953 */       GUI.this.m_jStatusBar.setText("Undo action performed: " + sMsg);
/*  904:     */       
/*  905:     */ 
/*  906:     */ 
/*  907:     */ 
/*  908: 958 */       GUI.this.a_redo.setEnabled(GUI.this.m_BayesNet.canRedo());
/*  909: 959 */       GUI.this.a_undo.setEnabled(GUI.this.m_BayesNet.canUndo());
/*  910: 960 */       GUI.this.m_Selection.clear();
/*  911: 961 */       GUI.this.updateStatus();
/*  912: 962 */       GUI.this.repaint();
/*  913:     */     }
/*  914:     */   }
/*  915:     */   
/*  916:     */   class ActionRedo
/*  917:     */     extends GUI.MyAction
/*  918:     */   {
/*  919:     */     private static final long serialVersionUID = -4038910085935519L;
/*  920:     */     
/*  921:     */     public ActionRedo()
/*  922:     */     {
/*  923: 971 */       super("Redo", "Redo", "redo", "ctrl Y");
/*  924: 972 */       setEnabled(false);
/*  925:     */     }
/*  926:     */     
/*  927:     */     public boolean isEnabled()
/*  928:     */     {
/*  929: 977 */       return GUI.this.m_BayesNet.canRedo();
/*  930:     */     }
/*  931:     */     
/*  932:     */     public void actionPerformed(ActionEvent ae)
/*  933:     */     {
/*  934: 982 */       String sMsg = GUI.this.m_BayesNet.redo();
/*  935: 983 */       GUI.this.m_jStatusBar.setText("Redo action performed: " + sMsg);
/*  936:     */       
/*  937:     */ 
/*  938:     */ 
/*  939:     */ 
/*  940: 988 */       GUI.this.m_Selection.clear();
/*  941: 989 */       GUI.this.updateStatus();
/*  942: 990 */       GUI.this.repaint();
/*  943:     */     }
/*  944:     */   }
/*  945:     */   
/*  946:     */   class ActionAddNode
/*  947:     */     extends GUI.MyAction
/*  948:     */   {
/*  949:     */     private static final long serialVersionUID = -2038910085935519L;
/*  950:     */     
/*  951:     */     public ActionAddNode()
/*  952:     */     {
/*  953: 999 */       super("Add Node", "Add Node", "addnode", "");
/*  954:     */     }
/*  955:     */     
/*  956:1002 */     JDialog dlg = null;
/*  957:1004 */     JTextField jTfName = new JTextField(20);
/*  958:1006 */     JTextField jTfCard = new JTextField(3);
/*  959:1008 */     int m_X = 2147483647;
/*  960:     */     int m_Y;
/*  961:     */     
/*  962:     */     public void addNode(int nX, int nY)
/*  963:     */     {
/*  964:1012 */       this.m_X = nX;
/*  965:1013 */       this.m_Y = nY;
/*  966:1014 */       addNode();
/*  967:     */     }
/*  968:     */     
/*  969:     */     void addNode()
/*  970:     */     {
/*  971:1018 */       if (this.dlg == null)
/*  972:     */       {
/*  973:1019 */         this.dlg = new JDialog();
/*  974:1020 */         this.dlg.setTitle("Add node");
/*  975:1021 */         JLabel jLbName = new JLabel("Name");
/*  976:1022 */         this.jTfName.setHorizontalAlignment(0);
/*  977:1023 */         JLabel jLbCard = new JLabel("Cardinality");
/*  978:1024 */         this.jTfCard.setHorizontalAlignment(0);
/*  979:1025 */         this.jTfCard.setText("2");
/*  980:     */         
/*  981:     */ 
/*  982:1028 */         JButton jBtCancel = new JButton("Cancel");
/*  983:1029 */         jBtCancel.setMnemonic('C');
/*  984:1030 */         jBtCancel.addActionListener(new ActionListener()
/*  985:     */         {
/*  986:     */           public void actionPerformed(ActionEvent ae)
/*  987:     */           {
/*  988:1033 */             GUI.ActionAddNode.this.dlg.setVisible(false);
/*  989:     */           }
/*  990:1035 */         });
/*  991:1036 */         JButton jBtOk = new JButton("Ok");
/*  992:1037 */         jBtOk.setMnemonic('O');
/*  993:1038 */         jBtOk.addActionListener(new ActionListener()
/*  994:     */         {
/*  995:     */           public void actionPerformed(ActionEvent ae)
/*  996:     */           {
/*  997:1041 */             String sName = GUI.ActionAddNode.this.jTfName.getText();
/*  998:1042 */             if (sName.length() <= 0)
/*  999:     */             {
/* 1000:1043 */               JOptionPane.showMessageDialog(null, "Name should have at least one character");
/* 1001:     */               
/* 1002:1045 */               return;
/* 1003:     */             }
/* 1004:1047 */             int nCard = new Integer(GUI.ActionAddNode.this.jTfCard.getText()).intValue();
/* 1005:1048 */             if (nCard <= 1)
/* 1006:     */             {
/* 1007:1049 */               JOptionPane.showMessageDialog(null, "Cardinality should be larger than 1");
/* 1008:     */               
/* 1009:1051 */               return;
/* 1010:     */             }
/* 1011:     */             try
/* 1012:     */             {
/* 1013:1054 */               if (GUI.ActionAddNode.this.m_X < 2147483647) {
/* 1014:1055 */                 GUI.this.m_BayesNet.addNode(sName, nCard, GUI.ActionAddNode.this.m_X, GUI.ActionAddNode.this.m_Y);
/* 1015:     */               } else {
/* 1016:1057 */                 GUI.this.m_BayesNet.addNode(sName, nCard);
/* 1017:     */               }
/* 1018:1059 */               GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1019:1060 */               GUI.this.a_undo.setEnabled(true);
/* 1020:1061 */               GUI.this.a_redo.setEnabled(false);
/* 1021:     */             }
/* 1022:     */             catch (Exception e)
/* 1023:     */             {
/* 1024:1069 */               e.printStackTrace();
/* 1025:     */             }
/* 1026:1071 */             GUI.this.repaint();
/* 1027:1072 */             GUI.ActionAddNode.this.dlg.setVisible(false);
/* 1028:     */           }
/* 1029:1074 */         });
/* 1030:1075 */         this.dlg.setLayout(new GridLayout(3, 2, 10, 10));
/* 1031:1076 */         this.dlg.add(jLbName);
/* 1032:1077 */         this.dlg.add(this.jTfName);
/* 1033:1078 */         this.dlg.add(jLbCard);
/* 1034:1079 */         this.dlg.add(this.jTfCard);
/* 1035:1080 */         this.dlg.add(jBtOk);
/* 1036:1081 */         this.dlg.add(jBtCancel);
/* 1037:1082 */         this.dlg.setSize(this.dlg.getPreferredSize());
/* 1038:     */       }
/* 1039:1084 */       this.jTfName.setText("Node" + (GUI.this.m_BayesNet.getNrOfNodes() + 1));
/* 1040:1085 */       this.dlg.setVisible(true);
/* 1041:     */     }
/* 1042:     */     
/* 1043:     */     public void actionPerformed(ActionEvent ae)
/* 1044:     */     {
/* 1045:1090 */       this.m_X = 2147483647;
/* 1046:1091 */       addNode();
/* 1047:     */     }
/* 1048:     */   }
/* 1049:     */   
/* 1050:     */   class ActionDeleteNode
/* 1051:     */     extends GUI.MyAction
/* 1052:     */   {
/* 1053:     */     private static final long serialVersionUID = -2038912085935519L;
/* 1054:     */     
/* 1055:     */     public ActionDeleteNode()
/* 1056:     */     {
/* 1057:1100 */       super("Delete Node", "Delete Node", "delnode", "DELETE");
/* 1058:     */     }
/* 1059:     */     
/* 1060:     */     public void actionPerformed(ActionEvent ae)
/* 1061:     */     {
/* 1062:1105 */       if (GUI.this.m_Selection.getSelected().size() > 0)
/* 1063:     */       {
/* 1064:1106 */         GUI.this.m_BayesNet.deleteSelection(GUI.this.m_Selection.getSelected());
/* 1065:1107 */         GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1066:1108 */         GUI.this.m_Selection.clear();
/* 1067:1109 */         GUI.this.updateStatus();
/* 1068:1110 */         GUI.this.repaint();
/* 1069:     */       }
/* 1070:     */       else
/* 1071:     */       {
/* 1072:1112 */         String[] options = new String[GUI.this.m_BayesNet.getNrOfNodes()];
/* 1073:1113 */         for (int i = 0; i < options.length; i++) {
/* 1074:1114 */           options[i] = GUI.this.m_BayesNet.getNodeName(i);
/* 1075:     */         }
/* 1076:1116 */         String sResult = (String)JOptionPane.showInputDialog(null, "Select node to delete", "Nodes", 0, null, options, options[0]);
/* 1077:1118 */         if ((sResult != null) && (!sResult.equals("")))
/* 1078:     */         {
/* 1079:1119 */           int iNode = GUI.this.m_BayesNet.getNode2(sResult);
/* 1080:1120 */           GUI.this.deleteNode(iNode);
/* 1081:     */         }
/* 1082:     */       }
/* 1083:     */     }
/* 1084:     */   }
/* 1085:     */   
/* 1086:     */   class ActionCopyNode
/* 1087:     */     extends GUI.MyAction
/* 1088:     */   {
/* 1089:     */     private static final long serialVersionUID = -2038732085935519L;
/* 1090:     */     
/* 1091:     */     public ActionCopyNode()
/* 1092:     */     {
/* 1093:1131 */       super("Copy", "Copy Nodes", "copy", "ctrl C");
/* 1094:     */     }
/* 1095:     */     
/* 1096:     */     public ActionCopyNode(String sName, String sToolTipText, String sIcon, String sAcceleratorKey)
/* 1097:     */     {
/* 1098:1136 */       super(sName, sToolTipText, sIcon, sAcceleratorKey);
/* 1099:     */     }
/* 1100:     */     
/* 1101:     */     public void actionPerformed(ActionEvent ae)
/* 1102:     */     {
/* 1103:1141 */       copy();
/* 1104:     */     }
/* 1105:     */     
/* 1106:     */     public void copy()
/* 1107:     */     {
/* 1108:1145 */       String sXML = GUI.this.m_BayesNet.toXMLBIF03(GUI.this.m_Selection.getSelected());
/* 1109:1146 */       GUI.this.m_clipboard.setText(sXML);
/* 1110:     */     }
/* 1111:     */   }
/* 1112:     */   
/* 1113:     */   class ActionCutNode
/* 1114:     */     extends GUI.ActionCopyNode
/* 1115:     */   {
/* 1116:     */     private static final long serialVersionUID = -2038822085935519L;
/* 1117:     */     
/* 1118:     */     public ActionCutNode()
/* 1119:     */     {
/* 1120:1155 */       super("Cut", "Cut Nodes", "cut", "ctrl X");
/* 1121:     */     }
/* 1122:     */     
/* 1123:     */     public void actionPerformed(ActionEvent ae)
/* 1124:     */     {
/* 1125:1160 */       copy();
/* 1126:1161 */       GUI.this.m_BayesNet.deleteSelection(GUI.this.m_Selection.getSelected());
/* 1127:1162 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1128:1163 */       GUI.this.m_Selection.clear();
/* 1129:1164 */       GUI.this.a_undo.setEnabled(true);
/* 1130:1165 */       GUI.this.a_redo.setEnabled(false);
/* 1131:1166 */       GUI.this.repaint();
/* 1132:     */     }
/* 1133:     */   }
/* 1134:     */   
/* 1135:     */   class ActionPasteNode
/* 1136:     */     extends GUI.MyAction
/* 1137:     */   {
/* 1138:     */     private static final long serialVersionUID = -2038732085935519L;
/* 1139:     */     
/* 1140:     */     public ActionPasteNode()
/* 1141:     */     {
/* 1142:1175 */       super("Paste", "Paste Nodes", "paste", "ctrl V");
/* 1143:     */     }
/* 1144:     */     
/* 1145:     */     public void actionPerformed(ActionEvent ae)
/* 1146:     */     {
/* 1147:     */       try
/* 1148:     */       {
/* 1149:1181 */         GUI.this.m_BayesNet.paste(GUI.this.m_clipboard.getText());
/* 1150:1182 */         GUI.this.updateStatus();
/* 1151:1183 */         GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1152:     */       }
/* 1153:     */       catch (Exception e)
/* 1154:     */       {
/* 1155:1185 */         e.printStackTrace();
/* 1156:     */       }
/* 1157:     */     }
/* 1158:     */     
/* 1159:     */     public boolean isEnabled()
/* 1160:     */     {
/* 1161:1191 */       return GUI.this.m_clipboard.hasText();
/* 1162:     */     }
/* 1163:     */   }
/* 1164:     */   
/* 1165:     */   class ActionSelectAll
/* 1166:     */     extends GUI.MyAction
/* 1167:     */   {
/* 1168:     */     private static final long serialVersionUID = -2038642085935519L;
/* 1169:     */     
/* 1170:     */     public ActionSelectAll()
/* 1171:     */     {
/* 1172:1200 */       super("Select All", "Select All Nodes", "selectall", "ctrl A");
/* 1173:     */     }
/* 1174:     */     
/* 1175:     */     public void actionPerformed(ActionEvent ae)
/* 1176:     */     {
/* 1177:1205 */       GUI.this.m_Selection.selectAll();
/* 1178:1206 */       GUI.this.repaint();
/* 1179:     */     }
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   class ActionExport
/* 1183:     */     extends GUI.MyAction
/* 1184:     */   {
/* 1185:1211 */     boolean m_bIsExporting = false;
/* 1186:     */     private static final long serialVersionUID = -3027642085935519L;
/* 1187:     */     
/* 1188:     */     public ActionExport()
/* 1189:     */     {
/* 1190:1216 */       super("Export", "Export to graphics file", "export", "");
/* 1191:     */     }
/* 1192:     */     
/* 1193:     */     public void actionPerformed(ActionEvent ae)
/* 1194:     */     {
/* 1195:1221 */       this.m_bIsExporting = true;
/* 1196:1222 */       GUI.this.m_GraphPanel.saveComponent();
/* 1197:1223 */       this.m_bIsExporting = false;
/* 1198:1224 */       GUI.this.repaint();
/* 1199:     */     }
/* 1200:     */     
/* 1201:     */     public boolean isExporting()
/* 1202:     */     {
/* 1203:1228 */       return this.m_bIsExporting;
/* 1204:     */     }
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   class ActionAlignLeft
/* 1208:     */     extends GUI.MyAction
/* 1209:     */   {
/* 1210:     */     private static final long serialVersionUID = -3138642085935519L;
/* 1211:     */     
/* 1212:     */     public ActionAlignLeft()
/* 1213:     */     {
/* 1214:1237 */       super("Align Left", "Align Left", "alignleft", "");
/* 1215:     */     }
/* 1216:     */     
/* 1217:     */     public void actionPerformed(ActionEvent ae)
/* 1218:     */     {
/* 1219:1242 */       GUI.this.m_BayesNet.alignLeft(GUI.this.m_Selection.getSelected());
/* 1220:1243 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1221:1244 */       GUI.this.a_undo.setEnabled(true);
/* 1222:1245 */       GUI.this.a_redo.setEnabled(false);
/* 1223:1246 */       GUI.this.repaint();
/* 1224:     */     }
/* 1225:     */   }
/* 1226:     */   
/* 1227:     */   class ActionAlignRight
/* 1228:     */     extends GUI.MyAction
/* 1229:     */   {
/* 1230:     */     private static final long serialVersionUID = -4238642085935519L;
/* 1231:     */     
/* 1232:     */     public ActionAlignRight()
/* 1233:     */     {
/* 1234:1255 */       super("Align Right", "Align Right", "alignright", "");
/* 1235:     */     }
/* 1236:     */     
/* 1237:     */     public void actionPerformed(ActionEvent ae)
/* 1238:     */     {
/* 1239:1260 */       GUI.this.m_BayesNet.alignRight(GUI.this.m_Selection.getSelected());
/* 1240:1261 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1241:1262 */       GUI.this.a_undo.setEnabled(true);
/* 1242:1263 */       GUI.this.a_redo.setEnabled(false);
/* 1243:1264 */       GUI.this.repaint();
/* 1244:     */     }
/* 1245:     */   }
/* 1246:     */   
/* 1247:     */   class ActionAlignTop
/* 1248:     */     extends GUI.MyAction
/* 1249:     */   {
/* 1250:     */     private static final long serialVersionUID = -5338642085935519L;
/* 1251:     */     
/* 1252:     */     public ActionAlignTop()
/* 1253:     */     {
/* 1254:1273 */       super("Align Top", "Align Top", "aligntop", "");
/* 1255:     */     }
/* 1256:     */     
/* 1257:     */     public void actionPerformed(ActionEvent ae)
/* 1258:     */     {
/* 1259:1278 */       GUI.this.m_BayesNet.alignTop(GUI.this.m_Selection.getSelected());
/* 1260:1279 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1261:1280 */       GUI.this.a_undo.setEnabled(true);
/* 1262:1281 */       GUI.this.a_redo.setEnabled(false);
/* 1263:1282 */       GUI.this.repaint();
/* 1264:     */     }
/* 1265:     */   }
/* 1266:     */   
/* 1267:     */   class ActionAlignBottom
/* 1268:     */     extends GUI.MyAction
/* 1269:     */   {
/* 1270:     */     private static final long serialVersionUID = -6438642085935519L;
/* 1271:     */     
/* 1272:     */     public ActionAlignBottom()
/* 1273:     */     {
/* 1274:1291 */       super("Align Bottom", "Align Bottom", "alignbottom", "");
/* 1275:     */     }
/* 1276:     */     
/* 1277:     */     public void actionPerformed(ActionEvent ae)
/* 1278:     */     {
/* 1279:1296 */       GUI.this.m_BayesNet.alignBottom(GUI.this.m_Selection.getSelected());
/* 1280:1297 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1281:1298 */       GUI.this.a_undo.setEnabled(true);
/* 1282:1299 */       GUI.this.a_redo.setEnabled(false);
/* 1283:1300 */       GUI.this.repaint();
/* 1284:     */     }
/* 1285:     */   }
/* 1286:     */   
/* 1287:     */   class ActionCenterHorizontal
/* 1288:     */     extends GUI.MyAction
/* 1289:     */   {
/* 1290:     */     private static final long serialVersionUID = -7538642085935519L;
/* 1291:     */     
/* 1292:     */     public ActionCenterHorizontal()
/* 1293:     */     {
/* 1294:1309 */       super("Center Horizontal", "Center Horizontal", "centerhorizontal", "");
/* 1295:     */     }
/* 1296:     */     
/* 1297:     */     public void actionPerformed(ActionEvent ae)
/* 1298:     */     {
/* 1299:1314 */       GUI.this.m_BayesNet.centerHorizontal(GUI.this.m_Selection.getSelected());
/* 1300:1315 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1301:1316 */       GUI.this.a_undo.setEnabled(true);
/* 1302:1317 */       GUI.this.a_redo.setEnabled(false);
/* 1303:1318 */       GUI.this.repaint();
/* 1304:     */     }
/* 1305:     */   }
/* 1306:     */   
/* 1307:     */   class ActionCenterVertical
/* 1308:     */     extends GUI.MyAction
/* 1309:     */   {
/* 1310:     */     private static final long serialVersionUID = -8638642085935519L;
/* 1311:     */     
/* 1312:     */     public ActionCenterVertical()
/* 1313:     */     {
/* 1314:1327 */       super("Center Vertical", "Center Vertical", "centervertical", "");
/* 1315:     */     }
/* 1316:     */     
/* 1317:     */     public void actionPerformed(ActionEvent ae)
/* 1318:     */     {
/* 1319:1332 */       GUI.this.m_BayesNet.centerVertical(GUI.this.m_Selection.getSelected());
/* 1320:1333 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1321:1334 */       GUI.this.a_undo.setEnabled(true);
/* 1322:1335 */       GUI.this.a_redo.setEnabled(false);
/* 1323:1336 */       GUI.this.repaint();
/* 1324:     */     }
/* 1325:     */   }
/* 1326:     */   
/* 1327:     */   class ActionSpaceHorizontal
/* 1328:     */     extends GUI.MyAction
/* 1329:     */   {
/* 1330:     */     private static final long serialVersionUID = -9738642085935519L;
/* 1331:     */     
/* 1332:     */     public ActionSpaceHorizontal()
/* 1333:     */     {
/* 1334:1345 */       super("Space Horizontal", "Space Horizontal", "spacehorizontal", "");
/* 1335:     */     }
/* 1336:     */     
/* 1337:     */     public void actionPerformed(ActionEvent ae)
/* 1338:     */     {
/* 1339:1350 */       GUI.this.m_BayesNet.spaceHorizontal(GUI.this.m_Selection.getSelected());
/* 1340:1351 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1341:1352 */       GUI.this.a_undo.setEnabled(true);
/* 1342:1353 */       GUI.this.a_redo.setEnabled(false);
/* 1343:1354 */       GUI.this.repaint();
/* 1344:     */     }
/* 1345:     */   }
/* 1346:     */   
/* 1347:     */   class ActionSpaceVertical
/* 1348:     */     extends GUI.MyAction
/* 1349:     */   {
/* 1350:     */     private static final long serialVersionUID = -838642085935519L;
/* 1351:     */     
/* 1352:     */     public ActionSpaceVertical()
/* 1353:     */     {
/* 1354:1363 */       super("Space Vertical", "Space Vertical", "spacevertical", "");
/* 1355:     */     }
/* 1356:     */     
/* 1357:     */     public void actionPerformed(ActionEvent ae)
/* 1358:     */     {
/* 1359:1368 */       GUI.this.m_BayesNet.spaceVertical(GUI.this.m_Selection.getSelected());
/* 1360:1369 */       GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 1361:1370 */       GUI.this.a_undo.setEnabled(true);
/* 1362:1371 */       GUI.this.a_redo.setEnabled(false);
/* 1363:1372 */       GUI.this.repaint();
/* 1364:     */     }
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   class ActionAddArc
/* 1368:     */     extends GUI.MyAction
/* 1369:     */   {
/* 1370:     */     private static final long serialVersionUID = -2038913085935519L;
/* 1371:     */     
/* 1372:     */     public ActionAddArc()
/* 1373:     */     {
/* 1374:1381 */       super("Add Arc", "Add Arc", "addarc", "");
/* 1375:     */     }
/* 1376:     */     
/* 1377:     */     public void actionPerformed(ActionEvent ae)
/* 1378:     */     {
/* 1379:     */       try
/* 1380:     */       {
/* 1381:1387 */         String[] options = new String[GUI.this.m_BayesNet.getNrOfNodes()];
/* 1382:1388 */         for (int i = 0; i < options.length; i++) {
/* 1383:1389 */           options[i] = GUI.this.m_BayesNet.getNodeName(i);
/* 1384:     */         }
/* 1385:1391 */         String sChild = (String)JOptionPane.showInputDialog(null, "Select child node", "Nodes", 0, null, options, options[0]);
/* 1386:1393 */         if ((sChild == null) || (sChild.equals(""))) {
/* 1387:1394 */           return;
/* 1388:     */         }
/* 1389:1396 */         int iChild = GUI.this.m_BayesNet.getNode(sChild);
/* 1390:1397 */         GUI.this.addArcInto(iChild);
/* 1391:     */       }
/* 1392:     */       catch (Exception e)
/* 1393:     */       {
/* 1394:1399 */         e.printStackTrace();
/* 1395:     */       }
/* 1396:     */     }
/* 1397:     */   }
/* 1398:     */   
/* 1399:     */   class ActionDeleteArc
/* 1400:     */     extends GUI.MyAction
/* 1401:     */   {
/* 1402:     */     private static final long serialVersionUID = -2038914085935519L;
/* 1403:     */     
/* 1404:     */     public ActionDeleteArc()
/* 1405:     */     {
/* 1406:1409 */       super("Delete Arc", "Delete Arc", "delarc", "");
/* 1407:     */     }
/* 1408:     */     
/* 1409:     */     public void actionPerformed(ActionEvent ae)
/* 1410:     */     {
/* 1411:1414 */       int nEdges = 0;
/* 1412:1415 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 1413:1416 */         nEdges += GUI.this.m_BayesNet.getNrOfParents(iNode);
/* 1414:     */       }
/* 1415:1418 */       String[] options = new String[nEdges];
/* 1416:1419 */       int i = 0;
/* 1417:1420 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 1418:1421 */         for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(iNode); iParent++)
/* 1419:     */         {
/* 1420:1422 */           int nParent = GUI.this.m_BayesNet.getParent(iNode, iParent);
/* 1421:1423 */           String sEdge = GUI.this.m_BayesNet.getNodeName(nParent);
/* 1422:1424 */           sEdge = sEdge + " -> ";
/* 1423:1425 */           sEdge = sEdge + GUI.this.m_BayesNet.getNodeName(iNode);
/* 1424:1426 */           options[(i++)] = sEdge;
/* 1425:     */         }
/* 1426:     */       }
/* 1427:1430 */       GUI.this.deleteArc(options);
/* 1428:     */     }
/* 1429:     */   }
/* 1430:     */   
/* 1431:     */   class ActionNew
/* 1432:     */     extends GUI.MyAction
/* 1433:     */   {
/* 1434:     */     private static final long serialVersionUID = -2038911085935515L;
/* 1435:     */     
/* 1436:     */     public ActionNew()
/* 1437:     */     {
/* 1438:1439 */       super("New", "New Network", "new", "");
/* 1439:     */     }
/* 1440:     */     
/* 1441:     */     public void actionPerformed(ActionEvent ae)
/* 1442:     */     {
/* 1443:1444 */       GUI.this.m_sFileName = "";
/* 1444:1445 */       GUI.this.m_BayesNet = new EditableBayesNet(true);
/* 1445:1446 */       GUI.this.updateStatus();
/* 1446:1447 */       GUI.this.layoutGraph();
/* 1447:1448 */       GUI.this.a_datagenerator.setEnabled(false);
/* 1448:1449 */       GUI.this.m_BayesNet.clearUndoStack();
/* 1449:1450 */       GUI.this.m_jStatusBar.setText("New Network");
/* 1450:1451 */       GUI.this.m_Selection = new GUI.Selection(GUI.this);
/* 1451:1452 */       GUI.this.repaint();
/* 1452:     */     }
/* 1453:     */   }
/* 1454:     */   
/* 1455:     */   class ActionLoad
/* 1456:     */     extends GUI.MyAction
/* 1457:     */   {
/* 1458:     */     private static final long serialVersionUID = -2038911085935515L;
/* 1459:     */     
/* 1460:     */     public ActionLoad()
/* 1461:     */     {
/* 1462:1461 */       super("Load", "Load Graph", "open", "ctrl O");
/* 1463:     */     }
/* 1464:     */     
/* 1465:     */     public void actionPerformed(ActionEvent ae)
/* 1466:     */     {
/* 1467:1466 */       JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
/* 1468:1467 */       ExtensionFileFilter ef1 = new ExtensionFileFilter(".arff", "ARFF files");
/* 1469:1468 */       ExtensionFileFilter ef2 = new ExtensionFileFilter(".xml", "XML BIF files");
/* 1470:1469 */       fc.addChoosableFileFilter(ef1);
/* 1471:1470 */       fc.addChoosableFileFilter(ef2);
/* 1472:1471 */       fc.setDialogTitle("Load Graph");
/* 1473:1472 */       int rval = fc.showOpenDialog(GUI.this);
/* 1474:1474 */       if (rval == 0)
/* 1475:     */       {
/* 1476:1475 */         String sFileName = fc.getSelectedFile().toString();
/* 1477:1476 */         if (sFileName.endsWith(ef1.getExtensions()[0])) {
/* 1478:1477 */           GUI.this.initFromArffFile(sFileName);
/* 1479:     */         } else {
/* 1480:     */           try
/* 1481:     */           {
/* 1482:1480 */             GUI.this.readBIFFromFile(sFileName);
/* 1483:     */           }
/* 1484:     */           catch (Exception e)
/* 1485:     */           {
/* 1486:1482 */             e.printStackTrace();
/* 1487:     */           }
/* 1488:     */         }
/* 1489:1485 */         GUI.this.m_jStatusBar.setText("Loaded " + sFileName);
/* 1490:1486 */         GUI.this.updateStatus();
/* 1491:     */       }
/* 1492:     */     }
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   class ActionViewStatusbar
/* 1496:     */     extends GUI.MyAction
/* 1497:     */   {
/* 1498:     */     private static final long serialVersionUID = -20389330812354L;
/* 1499:     */     
/* 1500:     */     public ActionViewStatusbar()
/* 1501:     */     {
/* 1502:1496 */       super("View statusbar", "View statusbar", "statusbar", "");
/* 1503:     */     }
/* 1504:     */     
/* 1505:     */     public void actionPerformed(ActionEvent ae)
/* 1506:     */     {
/* 1507:1501 */       GUI.this.m_jStatusBar.setVisible(!GUI.this.m_jStatusBar.isVisible());
/* 1508:     */     }
/* 1509:     */   }
/* 1510:     */   
/* 1511:     */   class ActionViewToolbar
/* 1512:     */     extends GUI.MyAction
/* 1513:     */   {
/* 1514:     */     private static final long serialVersionUID = -20389110812354L;
/* 1515:     */     
/* 1516:     */     public ActionViewToolbar()
/* 1517:     */     {
/* 1518:1510 */       super("View toolbar", "View toolbar", "toolbar", "");
/* 1519:     */     }
/* 1520:     */     
/* 1521:     */     public void actionPerformed(ActionEvent ae)
/* 1522:     */     {
/* 1523:1515 */       GUI.this.m_jTbTools.setVisible(!GUI.this.m_jTbTools.isVisible());
/* 1524:     */     }
/* 1525:     */   }
/* 1526:     */   
/* 1527:     */   class ActionSave
/* 1528:     */     extends GUI.MyAction
/* 1529:     */   {
/* 1530:     */     private static final long serialVersionUID = -20389110859355156L;
/* 1531:     */     
/* 1532:     */     public ActionSave()
/* 1533:     */     {
/* 1534:1524 */       super("Save", "Save Graph", "save", "ctrl S");
/* 1535:     */     }
/* 1536:     */     
/* 1537:     */     public ActionSave(String sName, String sToolTipText, String sIcon, String sAcceleratorKey)
/* 1538:     */     {
/* 1539:1529 */       super(sName, sToolTipText, sIcon, sAcceleratorKey);
/* 1540:     */     }
/* 1541:     */     
/* 1542:     */     public void actionPerformed(ActionEvent ae)
/* 1543:     */     {
/* 1544:1534 */       if (!GUI.this.m_sFileName.equals(""))
/* 1545:     */       {
/* 1546:1535 */         saveFile(GUI.this.m_sFileName);
/* 1547:1536 */         GUI.this.m_BayesNet.isSaved();
/* 1548:1537 */         GUI.this.m_jStatusBar.setText("Saved as " + GUI.this.m_sFileName);
/* 1549:     */       }
/* 1550:1539 */       else if (saveAs())
/* 1551:     */       {
/* 1552:1540 */         GUI.this.m_BayesNet.isSaved();
/* 1553:1541 */         GUI.this.m_jStatusBar.setText("Saved as " + GUI.this.m_sFileName);
/* 1554:     */       }
/* 1555:     */     }
/* 1556:     */     
/* 1557:1546 */     ExtensionFileFilter ef1 = new ExtensionFileFilter(".xml", "XML BIF files");
/* 1558:     */     
/* 1559:     */     boolean saveAs()
/* 1560:     */     {
/* 1561:1549 */       JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
/* 1562:1550 */       fc.addChoosableFileFilter(this.ef1);
/* 1563:1551 */       fc.setDialogTitle("Save Graph As");
/* 1564:1552 */       if (!GUI.this.m_sFileName.equals("")) {
/* 1565:1554 */         fc.setSelectedFile(new File(GUI.this.m_sFileName));
/* 1566:     */       }
/* 1567:1556 */       int rval = fc.showSaveDialog(GUI.this);
/* 1568:1558 */       if (rval == 0)
/* 1569:     */       {
/* 1570:1561 */         String sFileName = fc.getSelectedFile().toString();
/* 1571:1562 */         if (!sFileName.endsWith(".xml")) {
/* 1572:1563 */           sFileName = sFileName.concat(".xml");
/* 1573:     */         }
/* 1574:1565 */         saveFile(sFileName);
/* 1575:1566 */         return true;
/* 1576:     */       }
/* 1577:1568 */       return false;
/* 1578:     */     }
/* 1579:     */     
/* 1580:     */     protected void saveFile(String sFileName)
/* 1581:     */     {
/* 1582:     */       try
/* 1583:     */       {
/* 1584:1573 */         FileWriter outfile = new FileWriter(sFileName);
/* 1585:1574 */         outfile.write(GUI.this.m_BayesNet.toXMLBIF03());
/* 1586:1575 */         outfile.close();
/* 1587:1576 */         GUI.this.m_sFileName = sFileName;
/* 1588:1577 */         GUI.this.m_jStatusBar.setText("Saved as " + GUI.this.m_sFileName);
/* 1589:     */       }
/* 1590:     */       catch (IOException e)
/* 1591:     */       {
/* 1592:1579 */         e.printStackTrace();
/* 1593:     */       }
/* 1594:     */     }
/* 1595:     */   }
/* 1596:     */   
/* 1597:     */   class ActionSaveAs
/* 1598:     */     extends GUI.ActionSave
/* 1599:     */   {
/* 1600:     */     private static final long serialVersionUID = -20389110859354L;
/* 1601:     */     
/* 1602:     */     public ActionSaveAs()
/* 1603:     */     {
/* 1604:1589 */       super("Save As", "Save Graph As", "saveas", "");
/* 1605:     */     }
/* 1606:     */     
/* 1607:     */     public void actionPerformed(ActionEvent ae)
/* 1608:     */     {
/* 1609:1594 */       saveAs();
/* 1610:     */     }
/* 1611:     */   }
/* 1612:     */   
/* 1613:     */   class ActionPrint
/* 1614:     */     extends GUI.ActionSave
/* 1615:     */   {
/* 1616:     */     private static final long serialVersionUID = -20389001859354L;
/* 1617:1601 */     boolean m_bIsPrinting = false;
/* 1618:     */     
/* 1619:     */     public ActionPrint()
/* 1620:     */     {
/* 1621:1604 */       super("Print", "Print Graph", "print", "ctrl P");
/* 1622:     */     }
/* 1623:     */     
/* 1624:     */     public void actionPerformed(ActionEvent ae)
/* 1625:     */     {
/* 1626:1609 */       PrinterJob printJob = PrinterJob.getPrinterJob();
/* 1627:1610 */       printJob.setPrintable(GUI.this.m_GraphPanel);
/* 1628:1611 */       if (printJob.printDialog()) {
/* 1629:     */         try
/* 1630:     */         {
/* 1631:1613 */           this.m_bIsPrinting = true;
/* 1632:1614 */           printJob.print();
/* 1633:1615 */           this.m_bIsPrinting = false;
/* 1634:     */         }
/* 1635:     */         catch (PrinterException pe)
/* 1636:     */         {
/* 1637:1617 */           GUI.this.m_jStatusBar.setText("Error printing: " + pe);
/* 1638:1618 */           this.m_bIsPrinting = false;
/* 1639:     */         }
/* 1640:     */       }
/* 1641:1621 */       GUI.this.m_jStatusBar.setText("Print");
/* 1642:     */     }
/* 1643:     */     
/* 1644:     */     public boolean isPrinting()
/* 1645:     */     {
/* 1646:1625 */       return this.m_bIsPrinting;
/* 1647:     */     }
/* 1648:     */   }
/* 1649:     */   
/* 1650:     */   class ActionQuit
/* 1651:     */     extends GUI.ActionSave
/* 1652:     */   {
/* 1653:     */     private static final long serialVersionUID = -2038911085935515L;
/* 1654:     */     
/* 1655:     */     public ActionQuit()
/* 1656:     */     {
/* 1657:1635 */       super("Exit", "Exit Program", "exit", "");
/* 1658:     */     }
/* 1659:     */     
/* 1660:     */     public void actionPerformed(ActionEvent ae)
/* 1661:     */     {
/* 1662:1640 */       if (GUI.this.m_BayesNet.isChanged())
/* 1663:     */       {
/* 1664:1641 */         int result = JOptionPane.showConfirmDialog(null, "Network changed. Do you want to save it?", "Save before closing?", 1);
/* 1665:1644 */         if (result == 2) {
/* 1666:1645 */           return;
/* 1667:     */         }
/* 1668:1647 */         if ((result == 0) && 
/* 1669:1648 */           (!saveAs())) {
/* 1670:1649 */           return;
/* 1671:     */         }
/* 1672:     */       }
/* 1673:1653 */       System.exit(0);
/* 1674:     */     }
/* 1675:     */   }
/* 1676:     */   
/* 1677:     */   class ActionHelp
/* 1678:     */     extends GUI.MyAction
/* 1679:     */   {
/* 1680:     */     private static final long serialVersionUID = -20389110859354L;
/* 1681:     */     
/* 1682:     */     public ActionHelp()
/* 1683:     */     {
/* 1684:1662 */       super("Help", "Bayesian Network Workbench Help", "help", "");
/* 1685:     */     }
/* 1686:     */     
/* 1687:     */     public void actionPerformed(ActionEvent ae)
/* 1688:     */     {
/* 1689:1667 */       JOptionPane.showMessageDialog(null, "See Weka Homepage\nhttp://www.cs.waikato.ac.nz/ml", "Help Message", -1);
/* 1690:     */     }
/* 1691:     */   }
/* 1692:     */   
/* 1693:     */   class ActionAbout
/* 1694:     */     extends GUI.MyAction
/* 1695:     */   {
/* 1696:     */     private static final long serialVersionUID = -20389110859353L;
/* 1697:     */     
/* 1698:     */     public ActionAbout()
/* 1699:     */     {
/* 1700:1678 */       super("About", "Help about", "about", "");
/* 1701:     */     }
/* 1702:     */     
/* 1703:     */     public void actionPerformed(ActionEvent ae)
/* 1704:     */     {
/* 1705:1683 */       JOptionPane.showMessageDialog(null, "Bayesian Network Workbench\nPart of Weka\n2007", "About Message", -1);
/* 1706:     */     }
/* 1707:     */   }
/* 1708:     */   
/* 1709:     */   class ActionZoomIn
/* 1710:     */     extends GUI.MyAction
/* 1711:     */   {
/* 1712:     */     private static final long serialVersionUID = -2038911085935515L;
/* 1713:     */     
/* 1714:     */     public ActionZoomIn()
/* 1715:     */     {
/* 1716:1694 */       super("Zoom in", "Zoom in", "zoomin", "+");
/* 1717:     */     }
/* 1718:     */     
/* 1719:     */     public void actionPerformed(ActionEvent ae)
/* 1720:     */     {
/* 1721:1699 */       int i = 0;int s = (int)(GUI.this.m_fScale * 100.0D);
/* 1722:1700 */       if (s < 300) {
/* 1723:1701 */         i = s / 25;
/* 1724:1702 */       } else if (s < 700) {
/* 1725:1703 */         i = 6 + s / 50;
/* 1726:     */       } else {
/* 1727:1705 */         i = 13 + s / 100;
/* 1728:     */       }
/* 1729:1708 */       if (s >= 999)
/* 1730:     */       {
/* 1731:1709 */         setEnabled(false);
/* 1732:1710 */         return;
/* 1733:     */       }
/* 1734:1711 */       if (s >= 10)
/* 1735:     */       {
/* 1736:1712 */         if (i >= 22) {
/* 1737:1713 */           setEnabled(false);
/* 1738:     */         }
/* 1739:1715 */         if ((s == 10) && (!GUI.this.a_zoomout.isEnabled())) {
/* 1740:1716 */           GUI.this.a_zoomout.setEnabled(true);
/* 1741:     */         }
/* 1742:1718 */         GUI.this.m_jTfZoom.setText(GUI.this.m_nZoomPercents[(i + 1)] + "%");
/* 1743:1719 */         GUI.this.m_fScale = (GUI.this.m_nZoomPercents[(i + 1)] / 100.0D);
/* 1744:     */       }
/* 1745:     */       else
/* 1746:     */       {
/* 1747:1721 */         if (!GUI.this.a_zoomout.isEnabled()) {
/* 1748:1722 */           GUI.this.a_zoomout.setEnabled(true);
/* 1749:     */         }
/* 1750:1724 */         GUI.this.m_jTfZoom.setText(GUI.this.m_nZoomPercents[0] + "%");
/* 1751:1725 */         GUI.this.m_fScale = (GUI.this.m_nZoomPercents[0] / 100.0D);
/* 1752:     */       }
/* 1753:1727 */       GUI.this.setAppropriateSize();
/* 1754:1728 */       GUI.this.m_GraphPanel.repaint();
/* 1755:1729 */       GUI.this.m_GraphPanel.invalidate();
/* 1756:1730 */       GUI.this.m_jScrollPane.revalidate();
/* 1757:1731 */       GUI.this.m_jStatusBar.setText("Zooming in");
/* 1758:     */     }
/* 1759:     */   }
/* 1760:     */   
/* 1761:     */   class ActionZoomOut
/* 1762:     */     extends GUI.MyAction
/* 1763:     */   {
/* 1764:     */     private static final long serialVersionUID = -203891108593551L;
/* 1765:     */     
/* 1766:     */     public ActionZoomOut()
/* 1767:     */     {
/* 1768:1740 */       super("Zoom out", "Zoom out", "zoomout", "-");
/* 1769:     */     }
/* 1770:     */     
/* 1771:     */     public void actionPerformed(ActionEvent ae)
/* 1772:     */     {
/* 1773:1745 */       int i = 0;int s = (int)(GUI.this.m_fScale * 100.0D);
/* 1774:1746 */       if (s < 300) {
/* 1775:1747 */         i = (int)Math.ceil(s / 25.0D);
/* 1776:1748 */       } else if (s < 700) {
/* 1777:1749 */         i = 6 + (int)Math.ceil(s / 50.0D);
/* 1778:     */       } else {
/* 1779:1751 */         i = 13 + (int)Math.ceil(s / 100.0D);
/* 1780:     */       }
/* 1781:1754 */       if (s <= 10)
/* 1782:     */       {
/* 1783:1755 */         setEnabled(false);
/* 1784:     */       }
/* 1785:1756 */       else if (s < 999)
/* 1786:     */       {
/* 1787:1757 */         if (i <= 1) {
/* 1788:1758 */           setEnabled(false);
/* 1789:     */         }
/* 1790:1760 */         GUI.this.m_jTfZoom.setText(GUI.this.m_nZoomPercents[(i - 1)] + "%");
/* 1791:1761 */         GUI.this.m_fScale = (GUI.this.m_nZoomPercents[(i - 1)] / 100.0D);
/* 1792:     */       }
/* 1793:     */       else
/* 1794:     */       {
/* 1795:1763 */         if (!GUI.this.a_zoomin.isEnabled()) {
/* 1796:1764 */           GUI.this.a_zoomin.setEnabled(true);
/* 1797:     */         }
/* 1798:1766 */         GUI.this.m_jTfZoom.setText(GUI.this.m_nZoomPercents[22] + "%");
/* 1799:1767 */         GUI.this.m_fScale = (GUI.this.m_nZoomPercents[22] / 100.0D);
/* 1800:     */       }
/* 1801:1769 */       GUI.this.setAppropriateSize();
/* 1802:1770 */       GUI.this.m_GraphPanel.repaint();
/* 1803:1771 */       GUI.this.m_GraphPanel.invalidate();
/* 1804:1772 */       GUI.this.m_jScrollPane.revalidate();
/* 1805:1773 */       GUI.this.m_jStatusBar.setText("Zooming out");
/* 1806:     */     }
/* 1807:     */   }
/* 1808:     */   
/* 1809:     */   class ActionLayout
/* 1810:     */     extends GUI.MyAction
/* 1811:     */   {
/* 1812:     */     private static final long serialVersionUID = -203891108593551L;
/* 1813:     */     
/* 1814:     */     public ActionLayout()
/* 1815:     */     {
/* 1816:1782 */       super("Layout", "Layout Graph", "layout", "ctrl L");
/* 1817:     */     }
/* 1818:     */     
/* 1819:1785 */     JDialog dlg = null;
/* 1820:     */     
/* 1821:     */     public void actionPerformed(ActionEvent ae)
/* 1822:     */     {
/* 1823:1789 */       if (this.dlg == null)
/* 1824:     */       {
/* 1825:1790 */         this.dlg = new JDialog();
/* 1826:1791 */         this.dlg.setTitle("Graph Layout Options");
/* 1827:1792 */         final JCheckBox jCbCustomNodeSize = new JCheckBox("Custom Node Size");
/* 1828:1793 */         final JLabel jLbNodeWidth = new JLabel("Width");
/* 1829:1794 */         final JLabel jLbNodeHeight = new JLabel("Height");
/* 1830:     */         
/* 1831:1796 */         GUI.this.m_jTfNodeWidth.setHorizontalAlignment(0);
/* 1832:1797 */         GUI.this.m_jTfNodeWidth.setText("" + GUI.this.m_nNodeWidth);
/* 1833:1798 */         GUI.this.m_jTfNodeHeight.setHorizontalAlignment(0);
/* 1834:1799 */         GUI.this.m_jTfNodeHeight.setText("" + GUI.this.m_nNodeHeight);
/* 1835:1800 */         jLbNodeWidth.setEnabled(false);
/* 1836:1801 */         GUI.this.m_jTfNodeWidth.setEnabled(false);
/* 1837:1802 */         jLbNodeHeight.setEnabled(false);
/* 1838:1803 */         GUI.this.m_jTfNodeHeight.setEnabled(false);
/* 1839:     */         
/* 1840:1805 */         jCbCustomNodeSize.addActionListener(new ActionListener()
/* 1841:     */         {
/* 1842:     */           public void actionPerformed(ActionEvent ae)
/* 1843:     */           {
/* 1844:1808 */             if (((JCheckBox)ae.getSource()).isSelected())
/* 1845:     */             {
/* 1846:1809 */               jLbNodeWidth.setEnabled(true);
/* 1847:1810 */               GUI.this.m_jTfNodeWidth.setEnabled(true);
/* 1848:1811 */               jLbNodeHeight.setEnabled(true);
/* 1849:1812 */               GUI.this.m_jTfNodeHeight.setEnabled(true);
/* 1850:     */             }
/* 1851:     */             else
/* 1852:     */             {
/* 1853:1814 */               jLbNodeWidth.setEnabled(false);
/* 1854:1815 */               GUI.this.m_jTfNodeWidth.setEnabled(false);
/* 1855:1816 */               jLbNodeHeight.setEnabled(false);
/* 1856:1817 */               GUI.this.m_jTfNodeHeight.setEnabled(false);
/* 1857:1818 */               GUI.this.setAppropriateSize();
/* 1858:1819 */               GUI.this.setAppropriateNodeSize();
/* 1859:     */             }
/* 1860:     */           }
/* 1861:1823 */         });
/* 1862:1824 */         JButton jBtLayout = new JButton("Layout Graph");
/* 1863:1825 */         jBtLayout.setMnemonic('L');
/* 1864:     */         
/* 1865:1827 */         jBtLayout.addActionListener(new ActionListener()
/* 1866:     */         {
/* 1867:     */           public void actionPerformed(ActionEvent ae)
/* 1868:     */           {
/* 1869:1832 */             if (jCbCustomNodeSize.isSelected())
/* 1870:     */             {
/* 1871:     */               int tmpW;
/* 1872:     */               try
/* 1873:     */               {
/* 1874:1834 */                 tmpW = Integer.parseInt(GUI.this.m_jTfNodeWidth.getText());
/* 1875:     */               }
/* 1876:     */               catch (NumberFormatException ne)
/* 1877:     */               {
/* 1878:1836 */                 JOptionPane.showMessageDialog(GUI.this.getParent(), "Invalid integer entered for node width.", "Error", 0);
/* 1879:     */                 
/* 1880:     */ 
/* 1881:1839 */                 tmpW = GUI.this.m_nNodeWidth;
/* 1882:1840 */                 GUI.this.m_jTfNodeWidth.setText("" + GUI.this.m_nNodeWidth);
/* 1883:     */               }
/* 1884:     */               int tmpH;
/* 1885:     */               try
/* 1886:     */               {
/* 1887:1844 */                 tmpH = Integer.parseInt(GUI.this.m_jTfNodeHeight.getText());
/* 1888:     */               }
/* 1889:     */               catch (NumberFormatException ne)
/* 1890:     */               {
/* 1891:1846 */                 JOptionPane.showMessageDialog(GUI.this.getParent(), "Invalid integer entered for node height.", "Error", 0);
/* 1892:     */                 
/* 1893:     */ 
/* 1894:1849 */                 tmpH = GUI.this.m_nNodeHeight;
/* 1895:1850 */                 GUI.this.m_jTfNodeWidth.setText("" + GUI.this.m_nNodeHeight);
/* 1896:     */               }
/* 1897:1853 */               if ((tmpW != GUI.this.m_nNodeWidth) || (tmpH != GUI.this.m_nNodeHeight))
/* 1898:     */               {
/* 1899:1854 */                 GUI.this.m_nNodeWidth = tmpW;
/* 1900:1855 */                 GUI.this.m_nPaddedNodeWidth = (GUI.this.m_nNodeWidth + 10);
/* 1901:1856 */                 GUI.this.m_nNodeHeight = tmpH;
/* 1902:     */               }
/* 1903:     */             }
/* 1904:1861 */             GUI.ActionLayout.this.dlg.setVisible(false);
/* 1905:1862 */             GUI.this.updateStatus();
/* 1906:1863 */             GUI.this.layoutGraph();
/* 1907:1864 */             GUI.this.m_jStatusBar.setText("Laying out Bayes net");
/* 1908:     */           }
/* 1909:1867 */         });
/* 1910:1868 */         JButton jBtCancel = new JButton("Cancel");
/* 1911:1869 */         jBtCancel.setMnemonic('C');
/* 1912:1870 */         jBtCancel.addActionListener(new ActionListener()
/* 1913:     */         {
/* 1914:     */           public void actionPerformed(ActionEvent ae)
/* 1915:     */           {
/* 1916:1873 */             GUI.ActionLayout.this.dlg.setVisible(false);
/* 1917:     */           }
/* 1918:1875 */         });
/* 1919:1876 */         GridBagConstraints gbc = new GridBagConstraints();
/* 1920:1877 */         this.dlg.setLayout(new GridBagLayout());
/* 1921:     */         
/* 1922:     */ 
/* 1923:1880 */         Container c = new Container();
/* 1924:1881 */         c.setLayout(new GridBagLayout());
/* 1925:     */         
/* 1926:1883 */         gbc.gridwidth = 1;
/* 1927:1884 */         gbc.insets = new Insets(8, 0, 0, 0);
/* 1928:1885 */         gbc.anchor = 18;
/* 1929:1886 */         gbc.gridwidth = 0;
/* 1930:1887 */         c.add(jCbCustomNodeSize, gbc);
/* 1931:1888 */         gbc.gridwidth = -1;
/* 1932:1889 */         c.add(jLbNodeWidth, gbc);
/* 1933:1890 */         gbc.gridwidth = 0;
/* 1934:1891 */         c.add(GUI.this.m_jTfNodeWidth, gbc);
/* 1935:1892 */         gbc.gridwidth = -1;
/* 1936:1893 */         c.add(jLbNodeHeight, gbc);
/* 1937:1894 */         gbc.gridwidth = 0;
/* 1938:1895 */         c.add(GUI.this.m_jTfNodeHeight, gbc);
/* 1939:1896 */         gbc.fill = 2;
/* 1940:1897 */         this.dlg.add(c, gbc);
/* 1941:1898 */         this.dlg.add(jBtLayout);
/* 1942:1899 */         gbc.gridwidth = 0;
/* 1943:1900 */         this.dlg.add(jBtCancel);
/* 1944:     */       }
/* 1945:1902 */       this.dlg.setLocation(100, 100);
/* 1946:1903 */       this.dlg.setVisible(true);
/* 1947:1904 */       this.dlg.setSize(this.dlg.getPreferredSize());
/* 1948:1905 */       this.dlg.setVisible(false);
/* 1949:1906 */       this.dlg.setVisible(true);
/* 1950:1907 */       this.dlg.repaint();
/* 1951:     */     }
/* 1952:     */   }
/* 1953:     */   
/* 1954:     */   public GUI()
/* 1955:     */   {
/* 1956:1917 */     this.m_GraphPanel = new GraphPanel();
/* 1957:1918 */     this.m_jScrollPane = new JScrollPane(this.m_GraphPanel);
/* 1958:     */     
/* 1959:     */ 
/* 1960:     */ 
/* 1961:     */ 
/* 1962:1923 */     this.m_jTfZoom = new JTextField("100%");
/* 1963:1924 */     this.m_jTfZoom.setMinimumSize(this.m_jTfZoom.getPreferredSize());
/* 1964:1925 */     this.m_jTfZoom.setHorizontalAlignment(0);
/* 1965:1926 */     this.m_jTfZoom.setToolTipText("Zoom");
/* 1966:     */     
/* 1967:1928 */     this.m_jTfZoom.addActionListener(new ActionListener()
/* 1968:     */     {
/* 1969:     */       public void actionPerformed(ActionEvent ae)
/* 1970:     */       {
/* 1971:1931 */         JTextField jt = (JTextField)ae.getSource();
/* 1972:     */         try
/* 1973:     */         {
/* 1974:1933 */           int i = -1;
/* 1975:1934 */           i = jt.getText().indexOf('%');
/* 1976:1935 */           if (i == -1) {
/* 1977:1936 */             i = Integer.parseInt(jt.getText());
/* 1978:     */           } else {
/* 1979:1938 */             i = Integer.parseInt(jt.getText().substring(0, i));
/* 1980:     */           }
/* 1981:1941 */           if (i <= 999) {
/* 1982:1942 */             GUI.this.m_fScale = (i / 100.0D);
/* 1983:     */           }
/* 1984:1945 */           jt.setText((int)(GUI.this.m_fScale * 100.0D) + "%");
/* 1985:1946 */           if (GUI.this.m_fScale > 0.1D)
/* 1986:     */           {
/* 1987:1947 */             if (!GUI.this.a_zoomout.isEnabled()) {
/* 1988:1948 */               GUI.this.a_zoomout.setEnabled(true);
/* 1989:     */             }
/* 1990:     */           }
/* 1991:     */           else {
/* 1992:1951 */             GUI.this.a_zoomout.setEnabled(false);
/* 1993:     */           }
/* 1994:1953 */           if (GUI.this.m_fScale < 9.99D)
/* 1995:     */           {
/* 1996:1954 */             if (!GUI.this.a_zoomin.isEnabled()) {
/* 1997:1955 */               GUI.this.a_zoomin.setEnabled(true);
/* 1998:     */             }
/* 1999:     */           }
/* 2000:     */           else {
/* 2001:1958 */             GUI.this.a_zoomin.setEnabled(false);
/* 2002:     */           }
/* 2003:1960 */           GUI.this.setAppropriateSize();
/* 2004:     */           
/* 2005:1962 */           GUI.this.m_GraphPanel.repaint();
/* 2006:1963 */           GUI.this.m_GraphPanel.invalidate();
/* 2007:1964 */           GUI.this.m_jScrollPane.revalidate();
/* 2008:     */         }
/* 2009:     */         catch (NumberFormatException ne)
/* 2010:     */         {
/* 2011:1966 */           JOptionPane.showMessageDialog(GUI.this.getParent(), "Invalid integer entered for zoom.", "Error", 0);
/* 2012:     */           
/* 2013:     */ 
/* 2014:1969 */           jt.setText(GUI.this.m_fScale * 100.0D + "%");
/* 2015:     */         }
/* 2016:     */       }
/* 2017:1973 */     });
/* 2018:1974 */     GridBagConstraints gbc = new GridBagConstraints();
/* 2019:     */     
/* 2020:1976 */     JPanel p = new JPanel(new GridBagLayout());
/* 2021:1977 */     p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("ExtraControls"), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
/* 2022:     */     
/* 2023:     */ 
/* 2024:1980 */     p.setPreferredSize(new Dimension(0, 0));
/* 2025:     */     
/* 2026:1982 */     this.m_jTbTools = new JToolBar();
/* 2027:1983 */     this.m_jTbTools.setFloatable(false);
/* 2028:1984 */     this.m_jTbTools.setLayout(new GridBagLayout());
/* 2029:1985 */     gbc.anchor = 18;
/* 2030:1986 */     gbc.gridwidth = 0;
/* 2031:1987 */     gbc.insets = new Insets(0, 0, 0, 0);
/* 2032:1988 */     this.m_jTbTools.add(p, gbc);
/* 2033:1989 */     gbc.gridwidth = 1;
/* 2034:     */     
/* 2035:1991 */     this.m_jTbTools.add(this.a_new);
/* 2036:1992 */     this.m_jTbTools.add(this.a_save);
/* 2037:1993 */     this.m_jTbTools.add(this.a_load);
/* 2038:1994 */     this.m_jTbTools.addSeparator(new Dimension(2, 2));
/* 2039:1995 */     this.m_jTbTools.add(this.a_cutnode);
/* 2040:1996 */     this.m_jTbTools.add(this.a_copynode);
/* 2041:1997 */     this.m_jTbTools.add(this.a_pastenode);
/* 2042:1998 */     this.m_jTbTools.addSeparator(new Dimension(2, 2));
/* 2043:1999 */     this.m_jTbTools.add(this.a_undo);
/* 2044:2000 */     this.m_jTbTools.add(this.a_redo);
/* 2045:2001 */     this.m_jTbTools.addSeparator(new Dimension(2, 2));
/* 2046:2002 */     this.m_jTbTools.add(this.a_alignleft);
/* 2047:2003 */     this.m_jTbTools.add(this.a_alignright);
/* 2048:2004 */     this.m_jTbTools.add(this.a_aligntop);
/* 2049:2005 */     this.m_jTbTools.add(this.a_alignbottom);
/* 2050:2006 */     this.m_jTbTools.add(this.a_centerhorizontal);
/* 2051:2007 */     this.m_jTbTools.add(this.a_centervertical);
/* 2052:2008 */     this.m_jTbTools.add(this.a_spacehorizontal);
/* 2053:2009 */     this.m_jTbTools.add(this.a_spacevertical);
/* 2054:     */     
/* 2055:2011 */     this.m_jTbTools.addSeparator(new Dimension(2, 2));
/* 2056:2012 */     this.m_jTbTools.add(this.a_zoomin);
/* 2057:     */     
/* 2058:2014 */     gbc.fill = 3;
/* 2059:2015 */     gbc.weighty = 1.0D;
/* 2060:2016 */     JPanel p2 = new JPanel(new BorderLayout());
/* 2061:2017 */     p2.setPreferredSize(this.m_jTfZoom.getPreferredSize());
/* 2062:2018 */     p2.setMinimumSize(this.m_jTfZoom.getPreferredSize());
/* 2063:2019 */     p2.add(this.m_jTfZoom, "Center");
/* 2064:2020 */     this.m_jTbTools.add(p2, gbc);
/* 2065:2021 */     gbc.weighty = 0.0D;
/* 2066:2022 */     gbc.fill = 0;
/* 2067:     */     
/* 2068:2024 */     this.m_jTbTools.add(this.a_zoomout);
/* 2069:2025 */     this.m_jTbTools.addSeparator(new Dimension(2, 2));
/* 2070:     */     
/* 2071:     */ 
/* 2072:2028 */     this.m_jTbTools.add(this.a_layout);
/* 2073:2029 */     this.m_jTbTools.addSeparator(new Dimension(4, 2));
/* 2074:2030 */     gbc.weightx = 1.0D;
/* 2075:2031 */     gbc.fill = 1;
/* 2076:     */     
/* 2077:2033 */     this.m_jStatusBar = new JLabel("Status bar");
/* 2078:     */     
/* 2079:2035 */     setLayout(new BorderLayout());
/* 2080:2036 */     add(this.m_jTbTools, "North");
/* 2081:2037 */     add(this.m_jScrollPane, "Center");
/* 2082:2038 */     add(this.m_jStatusBar, "South");
/* 2083:     */     
/* 2084:2040 */     updateStatus();
/* 2085:2041 */     this.a_datagenerator.setEnabled(false);
/* 2086:     */     
/* 2087:2043 */     makeMenuBar();
/* 2088:     */   }
/* 2089:     */   
/* 2090:     */   public JMenuBar getMenuBar()
/* 2091:     */   {
/* 2092:2052 */     return this.m_menuBar;
/* 2093:     */   }
/* 2094:     */   
/* 2095:     */   private void makeMenuBar()
/* 2096:     */   {
/* 2097:2056 */     this.m_menuBar = new JMenuBar();
/* 2098:2057 */     JMenu fileMenu = new JMenu("File");
/* 2099:2058 */     fileMenu.setMnemonic('F');
/* 2100:     */     
/* 2101:2060 */     this.m_menuBar.add(fileMenu);
/* 2102:2061 */     fileMenu.add(this.a_new);
/* 2103:2062 */     fileMenu.add(this.a_load);
/* 2104:2063 */     fileMenu.add(this.a_save);
/* 2105:2064 */     fileMenu.add(this.a_saveas);
/* 2106:2065 */     fileMenu.addSeparator();
/* 2107:2066 */     fileMenu.add(this.a_print);
/* 2108:2067 */     fileMenu.add(this.a_export);
/* 2109:2068 */     fileMenu.addSeparator();
/* 2110:2069 */     fileMenu.add(this.a_quit);
/* 2111:2070 */     JMenu editMenu = new JMenu("Edit");
/* 2112:2071 */     editMenu.setMnemonic('E');
/* 2113:2072 */     this.m_menuBar.add(editMenu);
/* 2114:2073 */     editMenu.add(this.a_undo);
/* 2115:2074 */     editMenu.add(this.a_redo);
/* 2116:2075 */     editMenu.addSeparator();
/* 2117:2076 */     editMenu.add(this.a_selectall);
/* 2118:2077 */     editMenu.add(this.a_delnode);
/* 2119:2078 */     editMenu.add(this.a_cutnode);
/* 2120:2079 */     editMenu.add(this.a_copynode);
/* 2121:2080 */     editMenu.add(this.a_pastenode);
/* 2122:2081 */     editMenu.addSeparator();
/* 2123:2082 */     editMenu.add(this.a_addnode);
/* 2124:2083 */     editMenu.add(this.a_addarc);
/* 2125:2084 */     editMenu.add(this.a_delarc);
/* 2126:2085 */     editMenu.addSeparator();
/* 2127:2086 */     editMenu.add(this.a_alignleft);
/* 2128:2087 */     editMenu.add(this.a_alignright);
/* 2129:2088 */     editMenu.add(this.a_aligntop);
/* 2130:2089 */     editMenu.add(this.a_alignbottom);
/* 2131:2090 */     editMenu.add(this.a_centerhorizontal);
/* 2132:2091 */     editMenu.add(this.a_centervertical);
/* 2133:2092 */     editMenu.add(this.a_spacehorizontal);
/* 2134:2093 */     editMenu.add(this.a_spacevertical);
/* 2135:     */     
/* 2136:2095 */     JMenu toolMenu = new JMenu("Tools");
/* 2137:2096 */     toolMenu.setMnemonic('T');
/* 2138:2097 */     toolMenu.add(this.a_networkgenerator);
/* 2139:2098 */     toolMenu.add(this.a_datagenerator);
/* 2140:2099 */     toolMenu.add(this.a_datasetter);
/* 2141:2100 */     toolMenu.add(this.a_learn);
/* 2142:2101 */     toolMenu.add(this.a_learnCPT);
/* 2143:2102 */     toolMenu.addSeparator();
/* 2144:2103 */     toolMenu.add(this.a_layout);
/* 2145:2104 */     toolMenu.addSeparator();
/* 2146:2105 */     final JCheckBoxMenuItem viewMargins = new JCheckBoxMenuItem("Show Margins", false);
/* 2147:     */     
/* 2148:2107 */     viewMargins.addActionListener(new ActionListener()
/* 2149:     */     {
/* 2150:     */       public void actionPerformed(ActionEvent ae)
/* 2151:     */       {
/* 2152:2110 */         boolean bPrev = GUI.this.m_bViewMargins;
/* 2153:2111 */         GUI.this.m_bViewMargins = viewMargins.getState();
/* 2154:2112 */         if ((!bPrev) && (viewMargins.getState() == true)) {
/* 2155:2113 */           GUI.this.updateStatus();
/* 2156:     */         }
/* 2157:2115 */         GUI.this.repaint();
/* 2158:     */       }
/* 2159:2117 */     });
/* 2160:2118 */     toolMenu.add(viewMargins);
/* 2161:2119 */     final JCheckBoxMenuItem viewCliques = new JCheckBoxMenuItem("Show Cliques", false);
/* 2162:     */     
/* 2163:2121 */     viewCliques.addActionListener(new ActionListener()
/* 2164:     */     {
/* 2165:     */       public void actionPerformed(ActionEvent ae)
/* 2166:     */       {
/* 2167:2124 */         boolean bPrev = GUI.this.m_bViewCliques;
/* 2168:2125 */         GUI.this.m_bViewCliques = viewCliques.getState();
/* 2169:2126 */         if ((!bPrev) && (viewCliques.getState() == true)) {
/* 2170:2127 */           GUI.this.updateStatus();
/* 2171:     */         }
/* 2172:2129 */         GUI.this.repaint();
/* 2173:     */       }
/* 2174:2131 */     });
/* 2175:2132 */     toolMenu.add(viewCliques);
/* 2176:     */     
/* 2177:2134 */     this.m_menuBar.add(toolMenu);
/* 2178:2135 */     JMenu viewMenu = new JMenu("View");
/* 2179:2136 */     viewMenu.setMnemonic('V');
/* 2180:2137 */     this.m_menuBar.add(viewMenu);
/* 2181:2138 */     viewMenu.add(this.a_zoomin);
/* 2182:2139 */     viewMenu.add(this.a_zoomout);
/* 2183:2140 */     viewMenu.addSeparator();
/* 2184:2141 */     viewMenu.add(this.a_viewtoolbar);
/* 2185:2142 */     viewMenu.add(this.a_viewstatusbar);
/* 2186:     */     
/* 2187:2144 */     JMenu helpMenu = new JMenu("Help");
/* 2188:2145 */     helpMenu.setMnemonic('H');
/* 2189:2146 */     this.m_menuBar.add(helpMenu);
/* 2190:2147 */     helpMenu.add(this.a_help);
/* 2191:2148 */     helpMenu.add(this.a_about);
/* 2192:     */   }
/* 2193:     */   
/* 2194:     */   protected void setAppropriateNodeSize()
/* 2195:     */   {
/* 2196:2158 */     FontMetrics fm = getFontMetrics(getFont());
/* 2197:2159 */     int nMaxStringWidth = 50;
/* 2198:2160 */     if (nMaxStringWidth == 0) {
/* 2199:2161 */       for (int iNode = 0; iNode < this.m_BayesNet.getNrOfNodes(); iNode++)
/* 2200:     */       {
/* 2201:2162 */         int strWidth = fm.stringWidth(this.m_BayesNet.getNodeName(iNode));
/* 2202:2163 */         if (strWidth > nMaxStringWidth) {
/* 2203:2164 */           nMaxStringWidth = strWidth;
/* 2204:     */         }
/* 2205:     */       }
/* 2206:     */     }
/* 2207:2168 */     this.m_nNodeWidth = (nMaxStringWidth + 4);
/* 2208:2169 */     this.m_nPaddedNodeWidth = (this.m_nNodeWidth + 10);
/* 2209:2170 */     this.m_jTfNodeWidth.setText("" + this.m_nNodeWidth);
/* 2210:     */     
/* 2211:2172 */     this.m_nNodeHeight = (2 * fm.getHeight());
/* 2212:2173 */     this.m_jTfNodeHeight.setText("" + this.m_nNodeHeight);
/* 2213:     */   }
/* 2214:     */   
/* 2215:     */   public void setAppropriateSize()
/* 2216:     */   {
/* 2217:2181 */     int maxX = 0;int maxY = 0;
/* 2218:     */     
/* 2219:2183 */     this.m_GraphPanel.setScale(this.m_fScale, this.m_fScale);
/* 2220:2185 */     for (int iNode = 0; iNode < this.m_BayesNet.getNrOfNodes(); iNode++)
/* 2221:     */     {
/* 2222:2186 */       int nPosX = this.m_BayesNet.getPositionX(iNode);
/* 2223:2187 */       int nPosY = this.m_BayesNet.getPositionY(iNode);
/* 2224:2188 */       if (maxX < nPosX) {
/* 2225:2189 */         maxX = nPosX + 100;
/* 2226:     */       }
/* 2227:2191 */       if (maxY < nPosY) {
/* 2228:2192 */         maxY = nPosY;
/* 2229:     */       }
/* 2230:     */     }
/* 2231:2195 */     this.m_GraphPanel.setPreferredSize(new Dimension((int)((maxX + this.m_nPaddedNodeWidth + 2) * this.m_fScale), (int)((maxY + this.m_nNodeHeight + 2) * this.m_fScale)));
/* 2232:     */     
/* 2233:     */ 
/* 2234:2198 */     this.m_GraphPanel.revalidate();
/* 2235:     */   }
/* 2236:     */   
/* 2237:     */   public void layoutCompleted(LayoutCompleteEvent le)
/* 2238:     */   {
/* 2239:2210 */     LayoutEngine layoutEngine = this.m_layoutEngine;
/* 2240:     */     
/* 2241:2212 */     ArrayList<Integer> nPosX = new ArrayList(this.m_BayesNet.getNrOfNodes());
/* 2242:2213 */     ArrayList<Integer> nPosY = new ArrayList(this.m_BayesNet.getNrOfNodes());
/* 2243:2214 */     for (int iNode = 0; iNode < layoutEngine.getNodes().size(); iNode++)
/* 2244:     */     {
/* 2245:2215 */       GraphNode gNode = (GraphNode)layoutEngine.getNodes().get(iNode);
/* 2246:2216 */       if (gNode.nodeType == 3)
/* 2247:     */       {
/* 2248:2217 */         nPosX.add(Integer.valueOf(gNode.x));
/* 2249:2218 */         nPosY.add(Integer.valueOf(gNode.y));
/* 2250:     */       }
/* 2251:     */     }
/* 2252:2221 */     this.m_BayesNet.layoutGraph(nPosX, nPosY);
/* 2253:2222 */     this.m_jStatusBar.setText("Graph layed out");
/* 2254:2223 */     this.a_undo.setEnabled(true);
/* 2255:2224 */     this.a_redo.setEnabled(false);
/* 2256:2225 */     setAppropriateSize();
/* 2257:2226 */     this.m_GraphPanel.invalidate();
/* 2258:2227 */     this.m_jScrollPane.revalidate();
/* 2259:2228 */     this.m_GraphPanel.repaint();
/* 2260:     */   }
/* 2261:     */   
/* 2262:     */   public void readBIFFromFile(String sFileName)
/* 2263:     */     throws BIFFormatException, IOException
/* 2264:     */   {
/* 2265:2237 */     this.m_sFileName = sFileName;
/* 2266:     */     try
/* 2267:     */     {
/* 2268:2240 */       BIFReader bayesNet = new BIFReader();
/* 2269:2241 */       bayesNet.processFile(sFileName);
/* 2270:2242 */       this.m_BayesNet = new EditableBayesNet(bayesNet);
/* 2271:2243 */       updateStatus();
/* 2272:2244 */       this.a_datagenerator.setEnabled(this.m_BayesNet.getNrOfNodes() > 0);
/* 2273:2245 */       this.m_BayesNet.clearUndoStack();
/* 2274:     */     }
/* 2275:     */     catch (Exception ex)
/* 2276:     */     {
/* 2277:2247 */       ex.printStackTrace();
/* 2278:2248 */       return;
/* 2279:     */     }
/* 2280:2251 */     setAppropriateNodeSize();
/* 2281:2252 */     setAppropriateSize();
/* 2282:     */   }
/* 2283:     */   
/* 2284:     */   void initFromArffFile(String sFileName)
/* 2285:     */   {
/* 2286:     */     try
/* 2287:     */     {
/* 2288:2261 */       Instances instances = new Instances(new FileReader(sFileName));
/* 2289:2262 */       this.m_BayesNet = new EditableBayesNet(instances);
/* 2290:2263 */       this.m_Instances = instances;
/* 2291:2264 */       this.a_learn.setEnabled(true);
/* 2292:2265 */       this.a_learnCPT.setEnabled(true);
/* 2293:2266 */       setAppropriateNodeSize();
/* 2294:2267 */       setAppropriateSize();
/* 2295:     */     }
/* 2296:     */     catch (Exception ex)
/* 2297:     */     {
/* 2298:2269 */       ex.printStackTrace();
/* 2299:2270 */       return;
/* 2300:     */     }
/* 2301:     */   }
/* 2302:     */   
/* 2303:     */   private class GraphPanel
/* 2304:     */     extends PrintablePanel
/* 2305:     */     implements Printable
/* 2306:     */   {
/* 2307:     */     private static final long serialVersionUID = -3562813603236753173L;
/* 2308:     */     static final int HIGHLIGHTED = 1;
/* 2309:     */     static final int NORMAL = 0;
/* 2310:     */     
/* 2311:     */     public GraphPanel()
/* 2312:     */     {
/* 2313:2288 */       addMouseListener(new GUI.GraphVisualizerMouseListener(GUI.this, null));
/* 2314:2289 */       addMouseMotionListener(new GUI.GraphVisualizerMouseMotionListener(GUI.this, null));
/* 2315:2290 */       setToolTipText("");
/* 2316:     */     }
/* 2317:     */     
/* 2318:     */     public String getToolTipText(MouseEvent me)
/* 2319:     */     {
/* 2320:     */       int y;
/* 2321:2302 */       int x = y = 0;
/* 2322:     */       
/* 2323:2304 */       Rectangle r = new Rectangle(0, 0, (int)(GUI.this.m_nPaddedNodeWidth * GUI.this.m_fScale), (int)(GUI.this.m_nNodeHeight * GUI.this.m_fScale));
/* 2324:     */       
/* 2325:2306 */       x += me.getX();
/* 2326:2307 */       y += me.getY();
/* 2327:2309 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++)
/* 2328:     */       {
/* 2329:2310 */         r.x = ((int)(GUI.this.m_BayesNet.getPositionX(iNode) * GUI.this.m_fScale));
/* 2330:2311 */         r.y = ((int)(GUI.this.m_BayesNet.getPositionY(iNode) * GUI.this.m_fScale));
/* 2331:2312 */         if (r.contains(x, y)) {
/* 2332:2313 */           return GUI.this.m_BayesNet.getNodeName(iNode) + " (right click to manipulate this node)";
/* 2333:     */         }
/* 2334:     */       }
/* 2335:2317 */       return null;
/* 2336:     */     }
/* 2337:     */     
/* 2338:     */     public void paintComponent(Graphics gr)
/* 2339:     */     {
/* 2340:2327 */       Graphics2D g = (Graphics2D)gr;
/* 2341:2328 */       RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 2342:     */       
/* 2343:2330 */       rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
/* 2344:2331 */       g.setRenderingHints(rh);
/* 2345:2332 */       g.scale(GUI.this.m_fScale, GUI.this.m_fScale);
/* 2346:2333 */       Rectangle r = g.getClipBounds();
/* 2347:2334 */       g.clearRect(r.x, r.y, r.width, r.height);
/* 2348:2336 */       if (GUI.this.m_bViewCliques)
/* 2349:     */       {
/* 2350:2337 */         this.m_nClique = 1;
/* 2351:2338 */         viewCliques(g, GUI.this.m_marginCalculator.m_root);
/* 2352:     */       }
/* 2353:2340 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 2354:2341 */         drawNode(g, iNode, 0);
/* 2355:     */       }
/* 2356:2343 */       if ((!GUI.this.a_export.isExporting()) && (!GUI.this.a_print.isPrinting())) {
/* 2357:2344 */         GUI.this.m_Selection.draw(g);
/* 2358:     */       }
/* 2359:2346 */       if (GUI.this.m_nSelectedRect != null) {
/* 2360:2347 */         g.drawRect((int)(GUI.this.m_nSelectedRect.x / GUI.this.m_fScale), (int)(GUI.this.m_nSelectedRect.y / GUI.this.m_fScale), (int)(GUI.this.m_nSelectedRect.width / GUI.this.m_fScale), (int)(GUI.this.m_nSelectedRect.height / GUI.this.m_fScale));
/* 2361:     */       }
/* 2362:     */     }
/* 2363:     */     
/* 2364:2358 */     int m_nClique = 1;
/* 2365:     */     
/* 2366:     */     void viewCliques(Graphics g, MarginCalculator.JunctionTreeNode node)
/* 2367:     */     {
/* 2368:2364 */       int[] nodes = node.m_nNodes;
/* 2369:2365 */       g.setColor(new Color(this.m_nClique % 7 * 256 / 7, this.m_nClique % 2 * 256 / 2, this.m_nClique % 3 * 256 / 3));
/* 2370:     */       
/* 2371:2367 */       int dX = GUI.this.m_nPaddedNodeWidth / 2 + this.m_nClique;
/* 2372:2368 */       int dY = GUI.this.m_nNodeHeight / 2;
/* 2373:2369 */       int nPosX = 0;
/* 2374:2370 */       int nPosY = 0;
/* 2375:2371 */       String sStr = "";
/* 2376:2372 */       for (int j = 0; j < nodes.length; j++)
/* 2377:     */       {
/* 2378:2373 */         nPosX += GUI.this.m_BayesNet.getPositionX(nodes[j]);
/* 2379:2374 */         nPosY += GUI.this.m_BayesNet.getPositionY(nodes[j]);
/* 2380:2375 */         sStr = sStr + " " + nodes[j];
/* 2381:2376 */         for (int k = j + 1; k < nodes.length; k++) {
/* 2382:2377 */           g.drawLine(GUI.this.m_BayesNet.getPositionX(nodes[j]) + dX, GUI.this.m_BayesNet.getPositionY(nodes[j]) + dY, GUI.this.m_BayesNet.getPositionX(nodes[k]) + dX, GUI.this.m_BayesNet.getPositionY(nodes[k]) + dY);
/* 2383:     */         }
/* 2384:     */       }
/* 2385:2383 */       this.m_nClique += 1;
/* 2386:2384 */       nPosX /= nodes.length;
/* 2387:2385 */       nPosY /= nodes.length;
/* 2388:2386 */       g.drawString("Clique " + this.m_nClique + "(" + sStr + ")", nPosX, nPosY);
/* 2389:2387 */       for (int iChild = 0; iChild < node.m_children.size(); iChild++) {
/* 2390:2388 */         viewCliques(g, (MarginCalculator.JunctionTreeNode)node.m_children.elementAt(iChild));
/* 2391:     */       }
/* 2392:     */     }
/* 2393:     */     
/* 2394:     */     protected void drawNode(Graphics g, int iNode, int mode)
/* 2395:     */     {
/* 2396:2397 */       int nPosX = GUI.this.m_BayesNet.getPositionX(iNode);
/* 2397:2398 */       int nPosY = GUI.this.m_BayesNet.getPositionY(iNode);
/* 2398:2399 */       g.setColor(getBackground().darker().darker());
/* 2399:2400 */       FontMetrics fm = getFontMetrics(getFont());
/* 2400:2402 */       if (mode == 1) {
/* 2401:2403 */         g.setXORMode(Color.green);
/* 2402:     */       }
/* 2403:2405 */       g.fillOval(nPosX + GUI.this.m_nPaddedNodeWidth - GUI.this.m_nNodeWidth - (GUI.this.m_nPaddedNodeWidth - GUI.this.m_nNodeWidth) / 2, nPosY, GUI.this.m_nNodeWidth, GUI.this.m_nNodeHeight);
/* 2404:     */       
/* 2405:     */ 
/* 2406:2408 */       g.setColor(Color.white);
/* 2407:2409 */       if (mode == 1) {
/* 2408:2410 */         g.setXORMode(Color.red);
/* 2409:     */       }
/* 2410:2416 */       if (fm.stringWidth(GUI.this.m_BayesNet.getNodeName(iNode)) <= GUI.this.m_nNodeWidth) {
/* 2411:2417 */         g.drawString(GUI.this.m_BayesNet.getNodeName(iNode), nPosX + GUI.this.m_nPaddedNodeWidth / 2 - fm.stringWidth(GUI.this.m_BayesNet.getNodeName(iNode)) / 2, nPosY + GUI.this.m_nNodeHeight / 2 + fm.getHeight() / 2 - 2);
/* 2412:2420 */       } else if (fm.stringWidth("" + iNode) <= GUI.this.m_nNodeWidth) {
/* 2413:2421 */         g.drawString("" + iNode, nPosX + GUI.this.m_nPaddedNodeWidth / 2 - fm.stringWidth("" + iNode) / 2, nPosY + GUI.this.m_nNodeHeight / 2 + fm.getHeight() / 2 - 2);
/* 2414:     */       }
/* 2415:2426 */       if (mode == 1) {
/* 2416:2427 */         g.setXORMode(Color.green);
/* 2417:     */       }
/* 2418:2430 */       if (GUI.this.m_bViewMargins)
/* 2419:     */       {
/* 2420:2431 */         if (GUI.this.m_BayesNet.getEvidence(iNode) < 0) {
/* 2421:2432 */           g.setColor(new Color(0, 128, 0));
/* 2422:     */         } else {
/* 2423:2434 */           g.setColor(new Color(128, 0, 0));
/* 2424:     */         }
/* 2425:2436 */         double[] P = GUI.this.m_BayesNet.getMargin(iNode);
/* 2426:2437 */         for (int iValue = 0; iValue < P.length; iValue++)
/* 2427:     */         {
/* 2428:2438 */           String sP = P[iValue] + "";
/* 2429:2439 */           if (sP.charAt(0) == '0') {
/* 2430:2440 */             sP = sP.substring(1);
/* 2431:     */           }
/* 2432:2442 */           if (sP.length() > 5) {
/* 2433:2443 */             sP = sP.substring(1, 5);
/* 2434:     */           }
/* 2435:2445 */           g.fillRect(nPosX + GUI.this.m_nPaddedNodeWidth, nPosY + iValue * 10 + 2, (int)(P[iValue] * 100.0D), 8);
/* 2436:     */           
/* 2437:2447 */           g.drawString(GUI.this.m_BayesNet.getNodeValue(iNode, iValue) + " " + sP, nPosX + GUI.this.m_nPaddedNodeWidth + (int)(P[iValue] * 100.0D), nPosY + iValue * 10 + 10);
/* 2438:     */         }
/* 2439:     */       }
/* 2440:2453 */       if (GUI.this.m_bViewCliques) {
/* 2441:2454 */         return;
/* 2442:     */       }
/* 2443:2456 */       g.setColor(Color.black);
/* 2444:2458 */       for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(iNode); iParent++)
/* 2445:     */       {
/* 2446:2459 */         int nParent = GUI.this.m_BayesNet.getParent(iNode, iParent);
/* 2447:2460 */         int nPosX1 = nPosX + GUI.this.m_nPaddedNodeWidth / 2;
/* 2448:2461 */         int nPosY1 = nPosY + GUI.this.m_nNodeHeight;
/* 2449:2462 */         int nPosX2 = GUI.this.m_BayesNet.getPositionX(nParent);
/* 2450:2463 */         int nPosY2 = GUI.this.m_BayesNet.getPositionY(nParent);
/* 2451:2464 */         int nPosX2b = nPosX2 + GUI.this.m_nPaddedNodeWidth / 2;
/* 2452:2465 */         int nPosY2b = nPosY2;
/* 2453:     */         
/* 2454:2467 */         double phi = Math.atan2((nPosX2b - nPosX1 + 0.0D) * GUI.this.m_nNodeHeight, (nPosY2b - nPosY1 + 0.0D) * GUI.this.m_nNodeWidth);
/* 2455:     */         
/* 2456:2469 */         nPosX1 = (int)(nPosX + GUI.this.m_nPaddedNodeWidth / 2 + Math.sin(phi) * GUI.this.m_nNodeWidth / 2.0D);
/* 2457:     */         
/* 2458:2471 */         nPosY1 = (int)(nPosY + GUI.this.m_nNodeHeight / 2 + Math.cos(phi) * GUI.this.m_nNodeHeight / 2.0D);
/* 2459:     */         
/* 2460:2473 */         nPosX2b = (int)(nPosX2 + GUI.this.m_nPaddedNodeWidth / 2 - Math.sin(phi) * GUI.this.m_nNodeWidth / 2.0D);
/* 2461:     */         
/* 2462:2475 */         nPosY2b = (int)(nPosY2 + GUI.this.m_nNodeHeight / 2 - Math.cos(phi) * GUI.this.m_nNodeHeight / 2.0D);
/* 2463:     */         
/* 2464:2477 */         drawArrow(g, nPosX2b, nPosY2b, nPosX1, nPosY1);
/* 2465:     */       }
/* 2466:2479 */       if (mode == 1)
/* 2467:     */       {
/* 2468:2480 */         ArrayList<Integer> children = GUI.this.m_BayesNet.getChildren(iNode);
/* 2469:2481 */         for (int iChild = 0; iChild < children.size(); iChild++)
/* 2470:     */         {
/* 2471:2482 */           int nChild = ((Integer)children.get(iChild)).intValue();
/* 2472:2483 */           int nPosX1 = nPosX + GUI.this.m_nPaddedNodeWidth / 2;
/* 2473:2484 */           int nPosY1 = nPosY;
/* 2474:2485 */           int nPosX2 = GUI.this.m_BayesNet.getPositionX(nChild);
/* 2475:2486 */           int nPosY2 = GUI.this.m_BayesNet.getPositionY(nChild);
/* 2476:2487 */           int nPosX2b = nPosX2 + GUI.this.m_nPaddedNodeWidth / 2;
/* 2477:2488 */           int nPosY2b = nPosY2 + GUI.this.m_nNodeHeight;
/* 2478:     */           
/* 2479:2490 */           double phi = Math.atan2((nPosX2b - nPosX1 + 0.0D) * GUI.this.m_nNodeHeight, (nPosY2b - nPosY1 + 0.0D) * GUI.this.m_nNodeWidth);
/* 2480:     */           
/* 2481:2492 */           nPosX1 = (int)(nPosX + GUI.this.m_nPaddedNodeWidth / 2 + Math.sin(phi) * GUI.this.m_nNodeWidth / 2.0D);
/* 2482:     */           
/* 2483:2494 */           nPosY1 = (int)(nPosY + GUI.this.m_nNodeHeight / 2 + Math.cos(phi) * GUI.this.m_nNodeHeight / 2.0D);
/* 2484:     */           
/* 2485:2496 */           nPosX2b = (int)(nPosX2 + GUI.this.m_nPaddedNodeWidth / 2 - Math.sin(phi) * GUI.this.m_nNodeWidth / 2.0D);
/* 2486:     */           
/* 2487:2498 */           nPosY2b = (int)(nPosY2 + GUI.this.m_nNodeHeight / 2 - Math.cos(phi) * GUI.this.m_nNodeHeight / 2.0D);
/* 2488:     */           
/* 2489:2500 */           drawArrow(g, nPosX1, nPosY1, nPosX2b, nPosY2b);
/* 2490:     */         }
/* 2491:     */       }
/* 2492:     */     }
/* 2493:     */     
/* 2494:     */     protected void drawArrow(Graphics g, int nPosX1, int nPosY1, int nPosX2, int nPosY2)
/* 2495:     */     {
/* 2496:2513 */       g.drawLine(nPosX1, nPosY1, nPosX2, nPosY2);
/* 2497:2515 */       if (nPosX1 == nPosX2)
/* 2498:     */       {
/* 2499:2516 */         if (nPosY1 < nPosY2)
/* 2500:     */         {
/* 2501:2517 */           g.drawLine(nPosX2, nPosY2, nPosX2 + 4, nPosY2 - 8);
/* 2502:2518 */           g.drawLine(nPosX2, nPosY2, nPosX2 - 4, nPosY2 - 8);
/* 2503:     */         }
/* 2504:     */         else
/* 2505:     */         {
/* 2506:2520 */           g.drawLine(nPosX2, nPosY2, nPosX2 + 4, nPosY2 + 8);
/* 2507:2521 */           g.drawLine(nPosX2, nPosY2, nPosX2 - 4, nPosY2 + 8);
/* 2508:     */         }
/* 2509:     */       }
/* 2510:     */       else
/* 2511:     */       {
/* 2512:2526 */         double hyp = 0.0D;double base = 0.0D;double perp = 0.0D;
/* 2513:2527 */         int nPosX3 = 0;int nPosY3 = 0;
/* 2514:     */         double theta;
/* 2515:     */         double theta;
/* 2516:2529 */         if (nPosX2 < nPosX1)
/* 2517:     */         {
/* 2518:2530 */           base = nPosX1 - nPosX2;
/* 2519:2531 */           hyp = Math.sqrt((nPosX2 - nPosX1) * (nPosX2 - nPosX1) + (nPosY2 - nPosY1) * (nPosY2 - nPosY1));
/* 2520:     */           
/* 2521:2533 */           theta = Math.acos(base / hyp);
/* 2522:     */         }
/* 2523:     */         else
/* 2524:     */         {
/* 2525:2535 */           base = nPosX1 - nPosX2;
/* 2526:2536 */           hyp = Math.sqrt((nPosX2 - nPosX1) * (nPosX2 - nPosX1) + (nPosY2 - nPosY1) * (nPosY2 - nPosY1));
/* 2527:     */           
/* 2528:2538 */           theta = Math.acos(base / hyp);
/* 2529:     */         }
/* 2530:2540 */         double beta = 0.5235987755982988D;
/* 2531:     */         
/* 2532:2542 */         hyp = 8.0D;
/* 2533:2543 */         base = Math.cos(theta - beta) * hyp;
/* 2534:2544 */         perp = Math.sin(theta - beta) * hyp;
/* 2535:     */         
/* 2536:2546 */         nPosX3 = (int)(nPosX2 + base);
/* 2537:2547 */         if (nPosY1 < nPosY2) {
/* 2538:2548 */           nPosY3 = (int)(nPosY2 - perp);
/* 2539:     */         } else {
/* 2540:2550 */           nPosY3 = (int)(nPosY2 + perp);
/* 2541:     */         }
/* 2542:2553 */         g.drawLine(nPosX2, nPosY2, nPosX3, nPosY3);
/* 2543:     */         
/* 2544:2555 */         base = Math.cos(theta + beta) * hyp;
/* 2545:2556 */         perp = Math.sin(theta + beta) * hyp;
/* 2546:     */         
/* 2547:2558 */         nPosX3 = (int)(nPosX2 + base);
/* 2548:2559 */         if (nPosY1 < nPosY2) {
/* 2549:2560 */           nPosY3 = (int)(nPosY2 - perp);
/* 2550:     */         } else {
/* 2551:2562 */           nPosY3 = (int)(nPosY2 + perp);
/* 2552:     */         }
/* 2553:2564 */         g.drawLine(nPosX2, nPosY2, nPosX3, nPosY3);
/* 2554:     */       }
/* 2555:     */     }
/* 2556:     */     
/* 2557:     */     public void highLight(int iNode)
/* 2558:     */     {
/* 2559:2573 */       Graphics2D g = (Graphics2D)getGraphics();
/* 2560:2574 */       RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 2561:     */       
/* 2562:2576 */       rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
/* 2563:2577 */       g.setRenderingHints(rh);
/* 2564:2578 */       g.setPaintMode();
/* 2565:2579 */       g.scale(GUI.this.m_fScale, GUI.this.m_fScale);
/* 2566:2580 */       drawNode(g, iNode, 1);
/* 2567:     */     }
/* 2568:     */     
/* 2569:     */     public int print(Graphics g, PageFormat pageFormat, int pageIndex)
/* 2570:     */     {
/* 2571:2590 */       if (pageIndex > 0) {
/* 2572:2591 */         return 1;
/* 2573:     */       }
/* 2574:2593 */       Graphics2D g2d = (Graphics2D)g;
/* 2575:2594 */       g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
/* 2576:2595 */       double fHeight = pageFormat.getImageableHeight();
/* 2577:2596 */       double fWidth = pageFormat.getImageableWidth();
/* 2578:2597 */       int xMax = 1;
/* 2579:2598 */       int yMax = 1;
/* 2580:2599 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++)
/* 2581:     */       {
/* 2582:2600 */         if (xMax < GUI.this.m_BayesNet.getPositionX(iNode)) {
/* 2583:2601 */           xMax = GUI.this.m_BayesNet.getPositionX(iNode);
/* 2584:     */         }
/* 2585:2603 */         if (yMax < GUI.this.m_BayesNet.getPositionY(iNode)) {
/* 2586:2604 */           yMax = GUI.this.m_BayesNet.getPositionY(iNode);
/* 2587:     */         }
/* 2588:     */       }
/* 2589:2607 */       double fCurrentScale = GUI.this.m_fScale;
/* 2590:2608 */       xMax += GUI.this.m_nPaddedNodeWidth + 100;
/* 2591:2609 */       if (fWidth / xMax < fHeight / yMax) {
/* 2592:2610 */         GUI.this.m_fScale = (fWidth / xMax);
/* 2593:     */       } else {
/* 2594:2612 */         GUI.this.m_fScale = (fHeight / yMax);
/* 2595:     */       }
/* 2596:2616 */       paint(g2d);
/* 2597:2617 */       GUI.this.m_fScale = fCurrentScale;
/* 2598:     */       
/* 2599:2619 */       return 0;
/* 2600:     */     }
/* 2601:     */   }
/* 2602:     */   
/* 2603:     */   private class GraphVisualizerTableModel
/* 2604:     */     extends AbstractTableModel
/* 2605:     */   {
/* 2606:     */     private static final long serialVersionUID = -4789813491347366596L;
/* 2607:     */     final String[] m_sColumnNames;
/* 2608:     */     final double[][] m_fProbs;
/* 2609:     */     
/* 2610:     */     public GraphVisualizerTableModel(int iNode)
/* 2611:     */     {
/* 2612:2638 */       double[][] probs = GUI.this.m_BayesNet.getDistribution(iNode);
/* 2613:2639 */       this.m_fProbs = new double[probs.length][probs[0].length];
/* 2614:2640 */       for (int i = 0; i < probs.length; i++) {
/* 2615:2641 */         for (int j = 0; j < probs[0].length; j++) {
/* 2616:2642 */           this.m_fProbs[i][j] = probs[i][j];
/* 2617:     */         }
/* 2618:     */       }
/* 2619:2645 */       this.m_sColumnNames = GUI.this.m_BayesNet.getValues(iNode);
/* 2620:     */     }
/* 2621:     */     
/* 2622:     */     public void randomize()
/* 2623:     */     {
/* 2624:2652 */       int nProbs = this.m_fProbs[0].length;
/* 2625:2653 */       Random random = new Random();
/* 2626:2654 */       for (int i = 0; i < this.m_fProbs.length; i++)
/* 2627:     */       {
/* 2628:2656 */         for (int j = 0; j < nProbs - 1; j++) {
/* 2629:2657 */           this.m_fProbs[i][j] = random.nextDouble();
/* 2630:     */         }
/* 2631:2660 */         for (int j = 0; j < nProbs - 1; j++) {
/* 2632:2661 */           for (int k = j + 1; k < nProbs - 1; k++) {
/* 2633:2662 */             if (this.m_fProbs[i][j] > this.m_fProbs[i][k])
/* 2634:     */             {
/* 2635:2663 */               double h = this.m_fProbs[i][j];
/* 2636:2664 */               this.m_fProbs[i][j] = this.m_fProbs[i][k];
/* 2637:2665 */               this.m_fProbs[i][k] = h;
/* 2638:     */             }
/* 2639:     */           }
/* 2640:     */         }
/* 2641:2669 */         double sum = this.m_fProbs[i][0];
/* 2642:2670 */         for (int j = 1; j < nProbs - 1; j++)
/* 2643:     */         {
/* 2644:2671 */           this.m_fProbs[i][j] -= sum;
/* 2645:2672 */           sum += this.m_fProbs[i][j];
/* 2646:     */         }
/* 2647:2674 */         this.m_fProbs[i][(nProbs - 1)] = (1.0D - sum);
/* 2648:     */       }
/* 2649:     */     }
/* 2650:     */     
/* 2651:     */     public void setData() {}
/* 2652:     */     
/* 2653:     */     public int getColumnCount()
/* 2654:     */     {
/* 2655:2684 */       return this.m_sColumnNames.length;
/* 2656:     */     }
/* 2657:     */     
/* 2658:     */     public int getRowCount()
/* 2659:     */     {
/* 2660:2690 */       return this.m_fProbs.length;
/* 2661:     */     }
/* 2662:     */     
/* 2663:     */     public String getColumnName(int iCol)
/* 2664:     */     {
/* 2665:2700 */       return this.m_sColumnNames[iCol];
/* 2666:     */     }
/* 2667:     */     
/* 2668:     */     public Object getValueAt(int iRow, int iCol)
/* 2669:     */     {
/* 2670:2711 */       return new Double(this.m_fProbs[iRow][iCol]);
/* 2671:     */     }
/* 2672:     */     
/* 2673:     */     public void setValueAt(Object oProb, int iRow, int iCol)
/* 2674:     */     {
/* 2675:2725 */       Double fProb = (Double)oProb;
/* 2676:2726 */       if ((fProb.doubleValue() < 0.0D) || (fProb.doubleValue() > 1.0D)) {
/* 2677:2727 */         return;
/* 2678:     */       }
/* 2679:2729 */       this.m_fProbs[iRow][iCol] = fProb.doubleValue();
/* 2680:2730 */       double sum = 0.0D;
/* 2681:2731 */       for (int i = 0; i < this.m_fProbs[iRow].length; i++) {
/* 2682:2732 */         sum += this.m_fProbs[iRow][i];
/* 2683:     */       }
/* 2684:2735 */       if (sum > 1.0D)
/* 2685:     */       {
/* 2686:2737 */         int i = this.m_fProbs[iRow].length - 1;
/* 2687:2738 */         while (sum > 1.0D)
/* 2688:     */         {
/* 2689:2739 */           if (i != iCol) {
/* 2690:2740 */             if (this.m_fProbs[iRow][i] > sum - 1.0D)
/* 2691:     */             {
/* 2692:2741 */               this.m_fProbs[iRow][i] -= sum - 1.0D;
/* 2693:2742 */               sum = 1.0D;
/* 2694:     */             }
/* 2695:     */             else
/* 2696:     */             {
/* 2697:2744 */               sum -= this.m_fProbs[iRow][i];
/* 2698:2745 */               this.m_fProbs[iRow][i] = 0.0D;
/* 2699:     */             }
/* 2700:     */           }
/* 2701:2748 */           i--;
/* 2702:     */         }
/* 2703:     */       }
/* 2704:     */       else
/* 2705:     */       {
/* 2706:2752 */         int i = this.m_fProbs[iRow].length - 1;
/* 2707:2753 */         while (sum < 1.0D)
/* 2708:     */         {
/* 2709:2754 */           if (i != iCol)
/* 2710:     */           {
/* 2711:2755 */             this.m_fProbs[iRow][i] += 1.0D - sum;
/* 2712:2756 */             sum = 1.0D;
/* 2713:     */           }
/* 2714:2758 */           i--;
/* 2715:     */         }
/* 2716:     */       }
/* 2717:2762 */       GUI.this.validate();
/* 2718:     */     }
/* 2719:     */     
/* 2720:     */     public Class<?> getColumnClass(int c)
/* 2721:     */     {
/* 2722:2771 */       return getValueAt(0, c).getClass();
/* 2723:     */     }
/* 2724:     */     
/* 2725:     */     public boolean isCellEditable(int row, int col)
/* 2726:     */     {
/* 2727:2779 */       return true;
/* 2728:     */     }
/* 2729:     */   }
/* 2730:     */   
/* 2731:     */   private class GraphVisualizerMouseListener
/* 2732:     */     extends MouseAdapter
/* 2733:     */   {
/* 2734:     */     private GraphVisualizerMouseListener() {}
/* 2735:     */     
/* 2736:     */     public void mouseClicked(MouseEvent me)
/* 2737:     */     {
/* 2738:2798 */       Rectangle r = new Rectangle(0, 0, (int)(GUI.this.m_nPaddedNodeWidth * GUI.this.m_fScale), (int)(GUI.this.m_nNodeHeight * GUI.this.m_fScale));
/* 2739:     */       
/* 2740:2800 */       int x = me.getX();
/* 2741:2801 */       int y = me.getY();
/* 2742:2803 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++)
/* 2743:     */       {
/* 2744:2804 */         r.x = ((int)(GUI.this.m_BayesNet.getPositionX(iNode) * GUI.this.m_fScale));
/* 2745:2805 */         r.y = ((int)(GUI.this.m_BayesNet.getPositionY(iNode) * GUI.this.m_fScale));
/* 2746:2806 */         if (r.contains(x, y))
/* 2747:     */         {
/* 2748:2807 */           GUI.this.m_nCurrentNode = iNode;
/* 2749:2808 */           if (me.getButton() == 3) {
/* 2750:2809 */             handleRightNodeClick(me);
/* 2751:     */           }
/* 2752:2811 */           if (me.getButton() == 1)
/* 2753:     */           {
/* 2754:2812 */             if ((me.getModifiersEx() & 0x80) != 0)
/* 2755:     */             {
/* 2756:2813 */               GUI.this.m_Selection.toggleSelection(GUI.this.m_nCurrentNode);
/* 2757:     */             }
/* 2758:2814 */             else if ((me.getModifiersEx() & 0x40) != 0)
/* 2759:     */             {
/* 2760:2815 */               GUI.this.m_Selection.addToSelection(GUI.this.m_nCurrentNode);
/* 2761:     */             }
/* 2762:     */             else
/* 2763:     */             {
/* 2764:2817 */               GUI.this.m_Selection.clear();
/* 2765:2818 */               GUI.this.m_Selection.addToSelection(GUI.this.m_nCurrentNode);
/* 2766:     */             }
/* 2767:2820 */             GUI.this.repaint();
/* 2768:     */           }
/* 2769:2822 */           return;
/* 2770:     */         }
/* 2771:     */       }
/* 2772:2825 */       if (me.getButton() == 3) {
/* 2773:2826 */         handleRightClick(me, (int)(x / GUI.this.m_fScale), (int)(y / GUI.this.m_fScale));
/* 2774:     */       }
/* 2775:     */     }
/* 2776:     */     
/* 2777:     */     public void mouseReleased(MouseEvent me)
/* 2778:     */     {
/* 2779:2838 */       if (GUI.this.m_nSelectedRect != null)
/* 2780:     */       {
/* 2781:2839 */         if ((me.getModifiersEx() & 0x80) != 0)
/* 2782:     */         {
/* 2783:2840 */           GUI.this.m_Selection.toggleSelection(GUI.this.m_nSelectedRect);
/* 2784:     */         }
/* 2785:2841 */         else if ((me.getModifiersEx() & 0x40) != 0)
/* 2786:     */         {
/* 2787:2842 */           GUI.this.m_Selection.addToSelection(GUI.this.m_nSelectedRect);
/* 2788:     */         }
/* 2789:     */         else
/* 2790:     */         {
/* 2791:2844 */           GUI.this.m_Selection.clear();
/* 2792:2845 */           GUI.this.m_Selection.addToSelection(GUI.this.m_nSelectedRect);
/* 2793:     */         }
/* 2794:2847 */         GUI.this.m_nSelectedRect = null;
/* 2795:2848 */         GUI.this.repaint();
/* 2796:     */       }
/* 2797:     */     }
/* 2798:     */     
/* 2799:2853 */     int m_nPosY = 0;
/* 2800:2853 */     int m_nPosX = 0;
/* 2801:     */     
/* 2802:     */     void handleRightClick(MouseEvent me, int nPosX, int nPosY)
/* 2803:     */     {
/* 2804:2861 */       ActionListener act = new ActionListener()
/* 2805:     */       {
/* 2806:     */         public void actionPerformed(ActionEvent ae)
/* 2807:     */         {
/* 2808:2864 */           if (ae.getActionCommand().equals("Add node"))
/* 2809:     */           {
/* 2810:2865 */             GUI.this.a_addnode.addNode(GUI.GraphVisualizerMouseListener.this.m_nPosX, GUI.GraphVisualizerMouseListener.this.m_nPosY);
/* 2811:2866 */             return;
/* 2812:     */           }
/* 2813:2868 */           GUI.this.repaint();
/* 2814:     */         }
/* 2815:2870 */       };
/* 2816:2871 */       JPopupMenu popupMenu = new JPopupMenu("Choose a value");
/* 2817:     */       
/* 2818:2873 */       JMenuItem addNodeItem = new JMenuItem("Add node");
/* 2819:2874 */       addNodeItem.addActionListener(act);
/* 2820:2875 */       popupMenu.add(addNodeItem);
/* 2821:     */       
/* 2822:2877 */       ArrayList<Integer> selected = GUI.this.m_Selection.getSelected();
/* 2823:2878 */       JMenu addArcMenu = new JMenu("Add parent");
/* 2824:2879 */       popupMenu.add(addArcMenu);
/* 2825:2880 */       if (selected.size() == 0)
/* 2826:     */       {
/* 2827:2881 */         addArcMenu.setEnabled(false);
/* 2828:     */       }
/* 2829:     */       else
/* 2830:     */       {
/* 2831:2883 */         int nNodes = GUI.this.m_BayesNet.getNrOfNodes();
/* 2832:2884 */         boolean[] isNotAllowedAsParent = new boolean[nNodes];
/* 2833:2886 */         for (int iNode = 0; iNode < selected.size(); iNode++) {
/* 2834:2887 */           isNotAllowedAsParent[((Integer)selected.get(iNode)).intValue()] = true;
/* 2835:     */         }
/* 2836:2890 */         for (int i = 0; i < nNodes; i++) {
/* 2837:2891 */           for (int iNode = 0; iNode < nNodes; iNode++) {
/* 2838:2892 */             for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(iNode); iParent++) {
/* 2839:2893 */               if (isNotAllowedAsParent[GUI.this.m_BayesNet.getParent(iNode, iParent)] != 0) {
/* 2840:2894 */                 isNotAllowedAsParent[iNode] = true;
/* 2841:     */               }
/* 2842:     */             }
/* 2843:     */           }
/* 2844:     */         }
/* 2845:2900 */         for (int iNode = 0; iNode < selected.size(); iNode++)
/* 2846:     */         {
/* 2847:2901 */           int nNode = ((Integer)selected.get(iNode)).intValue();
/* 2848:2902 */           for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(nNode); iParent++) {
/* 2849:2903 */             isNotAllowedAsParent[GUI.this.m_BayesNet.getParent(nNode, iParent)] = true;
/* 2850:     */           }
/* 2851:     */         }
/* 2852:2906 */         ActionListener addParentAction = new ActionListener()
/* 2853:     */         {
/* 2854:     */           public void actionPerformed(ActionEvent ae)
/* 2855:     */           {
/* 2856:     */             try
/* 2857:     */             {
/* 2858:2910 */               GUI.this.m_BayesNet.addArc(ae.getActionCommand(), GUI.this.m_Selection.getSelected());
/* 2859:     */               
/* 2860:2912 */               GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 2861:2913 */               GUI.this.updateStatus();
/* 2862:     */             }
/* 2863:     */             catch (Exception e)
/* 2864:     */             {
/* 2865:2915 */               e.printStackTrace();
/* 2866:     */             }
/* 2867:     */           }
/* 2868:2919 */         };
/* 2869:2920 */         int nCandidates = 0;
/* 2870:2921 */         for (int i = 0; i < nNodes; i++) {
/* 2871:2922 */           if (isNotAllowedAsParent[i] == 0)
/* 2872:     */           {
/* 2873:2923 */             JMenuItem item = new JMenuItem(GUI.this.m_BayesNet.getNodeName(i));
/* 2874:2924 */             item.addActionListener(addParentAction);
/* 2875:2925 */             addArcMenu.add(item);
/* 2876:2926 */             nCandidates++;
/* 2877:     */           }
/* 2878:     */         }
/* 2879:2929 */         if (nCandidates == 0) {
/* 2880:2930 */           addArcMenu.setEnabled(false);
/* 2881:     */         }
/* 2882:     */       }
/* 2883:2933 */       this.m_nPosX = nPosX;
/* 2884:2934 */       this.m_nPosY = nPosY;
/* 2885:2935 */       popupMenu.setLocation(me.getX(), me.getY());
/* 2886:2936 */       popupMenu.show(GUI.this.m_GraphPanel, me.getX(), me.getY());
/* 2887:     */     }
/* 2888:     */     
/* 2889:     */     void handleRightNodeClick(MouseEvent me)
/* 2890:     */     {
/* 2891:2943 */       GUI.this.m_Selection.clear();
/* 2892:2944 */       GUI.this.repaint();
/* 2893:2945 */       ActionListener renameValueAction = new ActionListener()
/* 2894:     */       {
/* 2895:     */         public void actionPerformed(ActionEvent ae)
/* 2896:     */         {
/* 2897:2948 */           GUI.this.renameValue(GUI.this.m_nCurrentNode, ae.getActionCommand());
/* 2898:     */         }
/* 2899:2950 */       };
/* 2900:2951 */       ActionListener delValueAction = new ActionListener()
/* 2901:     */       {
/* 2902:     */         public void actionPerformed(ActionEvent ae)
/* 2903:     */         {
/* 2904:2954 */           GUI.this.delValue(GUI.this.m_nCurrentNode, ae.getActionCommand());
/* 2905:     */         }
/* 2906:2956 */       };
/* 2907:2957 */       ActionListener addParentAction = new ActionListener()
/* 2908:     */       {
/* 2909:     */         public void actionPerformed(ActionEvent ae)
/* 2910:     */         {
/* 2911:     */           try
/* 2912:     */           {
/* 2913:2961 */             GUI.this.m_BayesNet.addArc(ae.getActionCommand(), GUI.this.m_BayesNet.getNodeName(GUI.this.m_nCurrentNode));
/* 2914:     */             
/* 2915:2963 */             GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 2916:2964 */             GUI.this.updateStatus();
/* 2917:     */           }
/* 2918:     */           catch (Exception e)
/* 2919:     */           {
/* 2920:2966 */             e.printStackTrace();
/* 2921:     */           }
/* 2922:     */         }
/* 2923:2969 */       };
/* 2924:2970 */       ActionListener delParentAction = new ActionListener()
/* 2925:     */       {
/* 2926:     */         public void actionPerformed(ActionEvent ae)
/* 2927:     */         {
/* 2928:2973 */           GUI.this.deleteArc(GUI.this.m_nCurrentNode, ae.getActionCommand());
/* 2929:     */         }
/* 2930:2975 */       };
/* 2931:2976 */       ActionListener delChildAction = new ActionListener()
/* 2932:     */       {
/* 2933:     */         public void actionPerformed(ActionEvent ae)
/* 2934:     */         {
/* 2935:2979 */           GUI.this.deleteArc(ae.getActionCommand(), GUI.this.m_nCurrentNode);
/* 2936:     */         }
/* 2937:2981 */       };
/* 2938:2982 */       ActionListener setAvidenceAction = new ActionListener()
/* 2939:     */       {
/* 2940:     */         public void actionPerformed(ActionEvent ae)
/* 2941:     */         {
/* 2942:     */           try
/* 2943:     */           {
/* 2944:2986 */             String[] outcomes = GUI.this.m_BayesNet.getValues(GUI.this.m_nCurrentNode);
/* 2945:2987 */             int iValue = 0;
/* 2946:2989 */             while ((iValue < outcomes.length) && (!outcomes[iValue].equals(ae.getActionCommand()))) {
/* 2947:2990 */               iValue++;
/* 2948:     */             }
/* 2949:2993 */             if (iValue == outcomes.length) {
/* 2950:2994 */               iValue = -1;
/* 2951:     */             }
/* 2952:2996 */             if (iValue < outcomes.length)
/* 2953:     */             {
/* 2954:2997 */               GUI.this.m_jStatusBar.setText("Set evidence for " + GUI.this.m_BayesNet.getNodeName(GUI.this.m_nCurrentNode));
/* 2955:2999 */               if ((GUI.this.m_BayesNet.getEvidence(GUI.this.m_nCurrentNode) < 0) && (iValue >= 0))
/* 2956:     */               {
/* 2957:3000 */                 GUI.this.m_BayesNet.setEvidence(GUI.this.m_nCurrentNode, iValue);
/* 2958:3001 */                 GUI.this.m_marginCalculatorWithEvidence.setEvidence(GUI.this.m_nCurrentNode, iValue);
/* 2959:     */               }
/* 2960:     */               else
/* 2961:     */               {
/* 2962:3004 */                 GUI.this.m_BayesNet.setEvidence(GUI.this.m_nCurrentNode, iValue);
/* 2963:3005 */                 SerializedObject so = new SerializedObject(GUI.this.m_marginCalculator);
/* 2964:3006 */                 GUI.this.m_marginCalculatorWithEvidence = ((MarginCalculator)so.getObject());
/* 2965:3008 */                 for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 2966:3009 */                   if (GUI.this.m_BayesNet.getEvidence(iNode) >= 0) {
/* 2967:3010 */                     GUI.this.m_marginCalculatorWithEvidence.setEvidence(iNode, GUI.this.m_BayesNet.getEvidence(iNode));
/* 2968:     */                   }
/* 2969:     */                 }
/* 2970:     */               }
/* 2971:3015 */               for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 2972:3016 */                 GUI.this.m_BayesNet.setMargin(iNode, GUI.this.m_marginCalculatorWithEvidence.getMargin(iNode));
/* 2973:     */               }
/* 2974:     */             }
/* 2975:     */           }
/* 2976:     */           catch (Exception e)
/* 2977:     */           {
/* 2978:3021 */             e.printStackTrace();
/* 2979:     */           }
/* 2980:3023 */           GUI.this.repaint();
/* 2981:     */         }
/* 2982:3026 */       };
/* 2983:3027 */       ActionListener act = new ActionListener()
/* 2984:     */       {
/* 2985:     */         public void actionPerformed(ActionEvent ae)
/* 2986:     */         {
/* 2987:3030 */           if (ae.getActionCommand().equals("Rename"))
/* 2988:     */           {
/* 2989:3031 */             GUI.this.renameNode(GUI.this.m_nCurrentNode);
/* 2990:3032 */             return;
/* 2991:     */           }
/* 2992:3034 */           if (ae.getActionCommand().equals("Add parent"))
/* 2993:     */           {
/* 2994:3035 */             GUI.this.addArcInto(GUI.this.m_nCurrentNode);
/* 2995:3036 */             return;
/* 2996:     */           }
/* 2997:3038 */           if (ae.getActionCommand().equals("Add value"))
/* 2998:     */           {
/* 2999:3039 */             GUI.this.addValue();
/* 3000:3040 */             return;
/* 3001:     */           }
/* 3002:3042 */           if (ae.getActionCommand().equals("Delete node"))
/* 3003:     */           {
/* 3004:3043 */             GUI.this.deleteNode(GUI.this.m_nCurrentNode);
/* 3005:3044 */             return;
/* 3006:     */           }
/* 3007:3046 */           if (ae.getActionCommand().equals("Edit CPT"))
/* 3008:     */           {
/* 3009:3047 */             GUI.this.editCPT(GUI.this.m_nCurrentNode);
/* 3010:3048 */             return;
/* 3011:     */           }
/* 3012:3050 */           GUI.this.repaint();
/* 3013:     */         }
/* 3014:     */       };
/* 3015:     */       try
/* 3016:     */       {
/* 3017:3054 */         JPopupMenu popupMenu = new JPopupMenu("Choose a value");
/* 3018:     */         
/* 3019:3056 */         JMenu setEvidenceMenu = new JMenu("Set evidence");
/* 3020:3057 */         String[] outcomes = GUI.this.m_BayesNet.getValues(GUI.this.m_nCurrentNode);
/* 3021:3058 */         for (String outcome : outcomes)
/* 3022:     */         {
/* 3023:3059 */           JMenuItem item = new JMenuItem(outcome);
/* 3024:3060 */           item.addActionListener(setAvidenceAction);
/* 3025:3061 */           setEvidenceMenu.add(item);
/* 3026:     */         }
/* 3027:3063 */         setEvidenceMenu.addSeparator();
/* 3028:3064 */         JMenuItem item = new JMenuItem("Clear");
/* 3029:3065 */         item.addActionListener(setAvidenceAction);
/* 3030:3066 */         setEvidenceMenu.add(item);
/* 3031:3067 */         popupMenu.add(setEvidenceMenu);
/* 3032:     */         
/* 3033:3069 */         setEvidenceMenu.setEnabled(GUI.this.m_bViewMargins);
/* 3034:     */         
/* 3035:3071 */         popupMenu.addSeparator();
/* 3036:     */         
/* 3037:3073 */         JMenuItem renameItem = new JMenuItem("Rename");
/* 3038:3074 */         renameItem.addActionListener(act);
/* 3039:3075 */         popupMenu.add(renameItem);
/* 3040:     */         
/* 3041:3077 */         JMenuItem delNodeItem = new JMenuItem("Delete node");
/* 3042:3078 */         delNodeItem.addActionListener(act);
/* 3043:3079 */         popupMenu.add(delNodeItem);
/* 3044:     */         
/* 3045:3081 */         JMenuItem editCPTItem = new JMenuItem("Edit CPT");
/* 3046:3082 */         editCPTItem.addActionListener(act);
/* 3047:3083 */         popupMenu.add(editCPTItem);
/* 3048:     */         
/* 3049:3085 */         popupMenu.addSeparator();
/* 3050:     */         
/* 3051:3087 */         JMenu addArcMenu = new JMenu("Add parent");
/* 3052:3088 */         popupMenu.add(addArcMenu);
/* 3053:3089 */         int nNodes = GUI.this.m_BayesNet.getNrOfNodes();
/* 3054:3090 */         boolean[] isNotAllowedAsParent = new boolean[nNodes];
/* 3055:     */         
/* 3056:3092 */         isNotAllowedAsParent[GUI.this.m_nCurrentNode] = true;
/* 3057:3094 */         for (int i = 0; i < nNodes; i++) {
/* 3058:3095 */           for (int iNode = 0; iNode < nNodes; iNode++) {
/* 3059:3096 */             for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(iNode); iParent++) {
/* 3060:3097 */               if (isNotAllowedAsParent[GUI.this.m_BayesNet.getParent(iNode, iParent)] != 0) {
/* 3061:3098 */                 isNotAllowedAsParent[iNode] = true;
/* 3062:     */               }
/* 3063:     */             }
/* 3064:     */           }
/* 3065:     */         }
/* 3066:3104 */         for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(GUI.this.m_nCurrentNode); iParent++) {
/* 3067:3106 */           isNotAllowedAsParent[GUI.this.m_BayesNet.getParent(GUI.this.m_nCurrentNode, iParent)] = true;
/* 3068:     */         }
/* 3069:3109 */         int nCandidates = 0;
/* 3070:3110 */         for (int i = 0; i < nNodes; i++) {
/* 3071:3111 */           if (isNotAllowedAsParent[i] == 0)
/* 3072:     */           {
/* 3073:3112 */             item = new JMenuItem(GUI.this.m_BayesNet.getNodeName(i));
/* 3074:3113 */             item.addActionListener(addParentAction);
/* 3075:3114 */             addArcMenu.add(item);
/* 3076:3115 */             nCandidates++;
/* 3077:     */           }
/* 3078:     */         }
/* 3079:3118 */         if (nCandidates == 0) {
/* 3080:3119 */           addArcMenu.setEnabled(false);
/* 3081:     */         }
/* 3082:3122 */         JMenu delArcMenu = new JMenu("Delete parent");
/* 3083:3123 */         popupMenu.add(delArcMenu);
/* 3084:3124 */         if (GUI.this.m_BayesNet.getNrOfParents(GUI.this.m_nCurrentNode) == 0) {
/* 3085:3125 */           delArcMenu.setEnabled(false);
/* 3086:     */         }
/* 3087:3127 */         for (int iParent = 0; iParent < GUI.this.m_BayesNet.getNrOfParents(GUI.this.m_nCurrentNode); iParent++)
/* 3088:     */         {
/* 3089:3129 */           item = new JMenuItem(GUI.this.m_BayesNet.getNodeName(GUI.this.m_BayesNet.getParent(GUI.this.m_nCurrentNode, iParent)));
/* 3090:     */           
/* 3091:3131 */           item.addActionListener(delParentAction);
/* 3092:3132 */           delArcMenu.add(item);
/* 3093:     */         }
/* 3094:3134 */         JMenu delChildMenu = new JMenu("Delete child");
/* 3095:3135 */         popupMenu.add(delChildMenu);
/* 3096:3136 */         ArrayList<Integer> nChildren = GUI.this.m_BayesNet.getChildren(GUI.this.m_nCurrentNode);
/* 3097:3137 */         if (nChildren.size() == 0) {
/* 3098:3138 */           delChildMenu.setEnabled(false);
/* 3099:     */         }
/* 3100:3140 */         for (int iChild = 0; iChild < nChildren.size(); iChild++)
/* 3101:     */         {
/* 3102:3141 */           item = new JMenuItem(GUI.this.m_BayesNet.getNodeName(((Integer)nChildren.get(iChild)).intValue()));
/* 3103:3142 */           item.addActionListener(delChildAction);
/* 3104:3143 */           delChildMenu.add(item);
/* 3105:     */         }
/* 3106:3146 */         popupMenu.addSeparator();
/* 3107:     */         
/* 3108:3148 */         JMenuItem addValueItem = new JMenuItem("Add value");
/* 3109:3149 */         addValueItem.addActionListener(act);
/* 3110:3150 */         popupMenu.add(addValueItem);
/* 3111:     */         
/* 3112:3152 */         JMenu renameValue = new JMenu("Rename value");
/* 3113:3153 */         popupMenu.add(renameValue);
/* 3114:3154 */         for (String outcome : outcomes)
/* 3115:     */         {
/* 3116:3155 */           item = new JMenuItem(outcome);
/* 3117:3156 */           item.addActionListener(renameValueAction);
/* 3118:3157 */           renameValue.add(item);
/* 3119:     */         }
/* 3120:3160 */         JMenu delValue = new JMenu("Delete value");
/* 3121:3161 */         popupMenu.add(delValue);
/* 3122:3162 */         if (GUI.this.m_BayesNet.getCardinality(GUI.this.m_nCurrentNode) <= 2) {
/* 3123:3163 */           delValue.setEnabled(false);
/* 3124:     */         }
/* 3125:3165 */         for (String outcome : outcomes)
/* 3126:     */         {
/* 3127:3166 */           JMenuItem delValueItem = new JMenuItem(outcome);
/* 3128:3167 */           delValueItem.addActionListener(delValueAction);
/* 3129:3168 */           delValue.add(delValueItem);
/* 3130:     */         }
/* 3131:3171 */         popupMenu.setLocation(me.getX(), me.getY());
/* 3132:3172 */         popupMenu.show(GUI.this.m_GraphPanel, me.getX(), me.getY());
/* 3133:     */       }
/* 3134:     */       catch (Exception e)
/* 3135:     */       {
/* 3136:3174 */         e.printStackTrace();
/* 3137:     */       }
/* 3138:     */     }
/* 3139:     */   }
/* 3140:     */   
/* 3141:     */   private class GraphVisualizerMouseMotionListener
/* 3142:     */     extends MouseMotionAdapter
/* 3143:     */   {
/* 3144:3186 */     int m_nLastNode = -1;
/* 3145:     */     int m_nPosX;
/* 3146:     */     int m_nPosY;
/* 3147:     */     
/* 3148:     */     private GraphVisualizerMouseMotionListener() {}
/* 3149:     */     
/* 3150:     */     int getGraphNode(MouseEvent me)
/* 3151:     */     {
/* 3152:3196 */       this.m_nPosX = (this.m_nPosY = 0);
/* 3153:     */       
/* 3154:3198 */       Rectangle r = new Rectangle(0, 0, (int)(GUI.this.m_nPaddedNodeWidth * GUI.this.m_fScale), (int)(GUI.this.m_nNodeHeight * GUI.this.m_fScale));
/* 3155:     */       
/* 3156:3200 */       this.m_nPosX += me.getX();
/* 3157:3201 */       this.m_nPosY += me.getY();
/* 3158:3203 */       for (int iNode = 0; iNode < GUI.this.m_BayesNet.getNrOfNodes(); iNode++)
/* 3159:     */       {
/* 3160:3204 */         r.x = ((int)(GUI.this.m_BayesNet.getPositionX(iNode) * GUI.this.m_fScale));
/* 3161:3205 */         r.y = ((int)(GUI.this.m_BayesNet.getPositionY(iNode) * GUI.this.m_fScale));
/* 3162:3206 */         if (r.contains(this.m_nPosX, this.m_nPosY)) {
/* 3163:3207 */           return iNode;
/* 3164:     */         }
/* 3165:     */       }
/* 3166:3210 */       return -1;
/* 3167:     */     }
/* 3168:     */     
/* 3169:     */     public void mouseDragged(MouseEvent me)
/* 3170:     */     {
/* 3171:3222 */       if (GUI.this.m_nSelectedRect != null)
/* 3172:     */       {
/* 3173:3223 */         GUI.this.m_nSelectedRect.width = (me.getPoint().x - GUI.this.m_nSelectedRect.x);
/* 3174:3224 */         GUI.this.m_nSelectedRect.height = (me.getPoint().y - GUI.this.m_nSelectedRect.y);
/* 3175:3225 */         GUI.this.repaint();
/* 3176:3226 */         return;
/* 3177:     */       }
/* 3178:3228 */       int iNode = getGraphNode(me);
/* 3179:3229 */       if (iNode >= 0)
/* 3180:     */       {
/* 3181:3230 */         if (GUI.this.m_Selection.getSelected().size() > 0)
/* 3182:     */         {
/* 3183:3231 */           if (GUI.this.m_Selection.getSelected().contains(Integer.valueOf(iNode)))
/* 3184:     */           {
/* 3185:3232 */             GUI.this.m_BayesNet.setPosition(iNode, (int)(this.m_nPosX / GUI.this.m_fScale - GUI.this.m_nPaddedNodeWidth / 2), (int)(this.m_nPosY / GUI.this.m_fScale - GUI.this.m_nNodeHeight / 2), GUI.this.m_Selection.getSelected());
/* 3186:     */           }
/* 3187:     */           else
/* 3188:     */           {
/* 3189:3237 */             GUI.this.m_Selection.clear();
/* 3190:3238 */             GUI.this.m_BayesNet.setPosition(iNode, (int)(this.m_nPosX / GUI.this.m_fScale - GUI.this.m_nPaddedNodeWidth / 2), (int)(this.m_nPosY / GUI.this.m_fScale - GUI.this.m_nNodeHeight / 2));
/* 3191:     */           }
/* 3192:3242 */           GUI.this.repaint();
/* 3193:     */         }
/* 3194:     */         else
/* 3195:     */         {
/* 3196:3244 */           GUI.this.m_BayesNet.setPosition(iNode, (int)(this.m_nPosX / GUI.this.m_fScale - GUI.this.m_nPaddedNodeWidth / 2), (int)(this.m_nPosY / GUI.this.m_fScale - GUI.this.m_nNodeHeight / 2));
/* 3197:     */         }
/* 3198:3248 */         GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 3199:3249 */         GUI.this.a_undo.setEnabled(true);
/* 3200:3250 */         GUI.this.a_redo.setEnabled(false);
/* 3201:3251 */         GUI.this.m_GraphPanel.highLight(iNode);
/* 3202:     */       }
/* 3203:3253 */       if (iNode < 0) {
/* 3204:3254 */         if (this.m_nLastNode >= 0)
/* 3205:     */         {
/* 3206:3255 */           GUI.this.m_GraphPanel.repaint();
/* 3207:3256 */           this.m_nLastNode = -1;
/* 3208:     */         }
/* 3209:     */         else
/* 3210:     */         {
/* 3211:3258 */           GUI.this.m_nSelectedRect = new Rectangle(me.getPoint().x, me.getPoint().y, 1, 1);
/* 3212:     */           
/* 3213:3260 */           GUI.this.m_GraphPanel.repaint();
/* 3214:     */         }
/* 3215:     */       }
/* 3216:     */     }
/* 3217:     */     
/* 3218:     */     public void mouseMoved(MouseEvent me)
/* 3219:     */     {
/* 3220:3273 */       int iNode = getGraphNode(me);
/* 3221:3274 */       if ((iNode >= 0) && 
/* 3222:3275 */         (iNode != this.m_nLastNode))
/* 3223:     */       {
/* 3224:3276 */         GUI.this.m_GraphPanel.highLight(iNode);
/* 3225:3277 */         if (this.m_nLastNode >= 0) {
/* 3226:3278 */           GUI.this.m_GraphPanel.highLight(this.m_nLastNode);
/* 3227:     */         }
/* 3228:3280 */         this.m_nLastNode = iNode;
/* 3229:     */       }
/* 3230:3283 */       if ((iNode < 0) && (this.m_nLastNode >= 0))
/* 3231:     */       {
/* 3232:3284 */         GUI.this.m_GraphPanel.repaint();
/* 3233:3285 */         this.m_nLastNode = -1;
/* 3234:     */       }
/* 3235:     */     }
/* 3236:     */   }
/* 3237:     */   
/* 3238:     */   void layoutGraph()
/* 3239:     */   {
/* 3240:3295 */     if (this.m_BayesNet.getNrOfNodes() == 0) {
/* 3241:3296 */       return;
/* 3242:     */     }
/* 3243:     */     try
/* 3244:     */     {
/* 3245:3299 */       ArrayList<GraphNode> m_nodes = new ArrayList();
/* 3246:3300 */       ArrayList<GraphEdge> m_edges = new ArrayList();
/* 3247:3301 */       BIFParser bp = new BIFParser(this.m_BayesNet.toXMLBIF03(), m_nodes, m_edges);
/* 3248:3302 */       bp.parse();
/* 3249:3303 */       updateStatus();
/* 3250:3304 */       this.m_layoutEngine = new HierarchicalBCEngine(m_nodes, m_edges, this.m_nPaddedNodeWidth, this.m_nNodeHeight);
/* 3251:     */       
/* 3252:3306 */       this.m_layoutEngine.addLayoutCompleteEventListener(this);
/* 3253:3307 */       this.m_layoutEngine.layoutGraph();
/* 3254:     */     }
/* 3255:     */     catch (Exception e)
/* 3256:     */     {
/* 3257:3309 */       e.printStackTrace();
/* 3258:     */     }
/* 3259:     */   }
/* 3260:     */   
/* 3261:     */   void updateStatus()
/* 3262:     */   {
/* 3263:3319 */     this.a_undo.setEnabled(this.m_BayesNet.canUndo());
/* 3264:3320 */     this.a_redo.setEnabled(this.m_BayesNet.canRedo());
/* 3265:     */     
/* 3266:3322 */     this.a_datagenerator.setEnabled(this.m_BayesNet.getNrOfNodes() > 0);
/* 3267:3324 */     if ((!this.m_bViewMargins) && (!this.m_bViewCliques))
/* 3268:     */     {
/* 3269:3325 */       repaint();
/* 3270:3326 */       return;
/* 3271:     */     }
/* 3272:     */     try
/* 3273:     */     {
/* 3274:3330 */       this.m_marginCalculator = new MarginCalculator();
/* 3275:3331 */       this.m_marginCalculator.calcMargins(this.m_BayesNet);
/* 3276:3332 */       SerializedObject so = new SerializedObject(this.m_marginCalculator);
/* 3277:3333 */       this.m_marginCalculatorWithEvidence = ((MarginCalculator)so.getObject());
/* 3278:3334 */       for (int iNode = 0; iNode < this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 3279:3335 */         if (this.m_BayesNet.getEvidence(iNode) >= 0) {
/* 3280:3336 */           this.m_marginCalculatorWithEvidence.setEvidence(iNode, this.m_BayesNet.getEvidence(iNode));
/* 3281:     */         }
/* 3282:     */       }
/* 3283:3340 */       for (int iNode = 0; iNode < this.m_BayesNet.getNrOfNodes(); iNode++) {
/* 3284:3341 */         this.m_BayesNet.setMargin(iNode, this.m_marginCalculatorWithEvidence.getMargin(iNode));
/* 3285:     */       }
/* 3286:     */     }
/* 3287:     */     catch (Exception e)
/* 3288:     */     {
/* 3289:3345 */       e.printStackTrace();
/* 3290:     */     }
/* 3291:3347 */     repaint();
/* 3292:     */   }
/* 3293:     */   
/* 3294:     */   void addArcInto(int iChild)
/* 3295:     */   {
/* 3296:3358 */     String sChild = this.m_BayesNet.getNodeName(iChild);
/* 3297:     */     try
/* 3298:     */     {
/* 3299:3360 */       int nNodes = this.m_BayesNet.getNrOfNodes();
/* 3300:3361 */       boolean[] isNotAllowedAsParent = new boolean[nNodes];
/* 3301:     */       
/* 3302:3363 */       isNotAllowedAsParent[iChild] = true;
/* 3303:3365 */       for (int i = 0; i < nNodes; i++) {
/* 3304:3366 */         for (int iNode = 0; iNode < nNodes; iNode++) {
/* 3305:3367 */           for (int iParent = 0; iParent < this.m_BayesNet.getNrOfParents(iNode); iParent++) {
/* 3306:3368 */             if (isNotAllowedAsParent[this.m_BayesNet.getParent(iNode, iParent)] != 0) {
/* 3307:3369 */               isNotAllowedAsParent[iNode] = true;
/* 3308:     */             }
/* 3309:     */           }
/* 3310:     */         }
/* 3311:     */       }
/* 3312:3375 */       for (int iParent = 0; iParent < this.m_BayesNet.getNrOfParents(iChild); iParent++) {
/* 3313:3376 */         isNotAllowedAsParent[this.m_BayesNet.getParent(iChild, iParent)] = true;
/* 3314:     */       }
/* 3315:3379 */       int nCandidates = 0;
/* 3316:3380 */       for (int i = 0; i < nNodes; i++) {
/* 3317:3381 */         if (isNotAllowedAsParent[i] == 0) {
/* 3318:3382 */           nCandidates++;
/* 3319:     */         }
/* 3320:     */       }
/* 3321:3385 */       if (nCandidates == 0)
/* 3322:     */       {
/* 3323:3386 */         JOptionPane.showMessageDialog(null, "No potential parents available for this node (" + sChild + "). Choose another node as child node.");
/* 3324:     */         
/* 3325:     */ 
/* 3326:3389 */         return;
/* 3327:     */       }
/* 3328:3391 */       String[] options = new String[nCandidates];
/* 3329:3392 */       int k = 0;
/* 3330:3393 */       for (int i = 0; i < nNodes; i++) {
/* 3331:3394 */         if (isNotAllowedAsParent[i] == 0) {
/* 3332:3395 */           options[(k++)] = this.m_BayesNet.getNodeName(i);
/* 3333:     */         }
/* 3334:     */       }
/* 3335:3398 */       String sParent = (String)JOptionPane.showInputDialog(null, "Select parent node for " + sChild, "Nodes", 0, null, options, options[0]);
/* 3336:3401 */       if ((sParent == null) || (sParent.equals(""))) {
/* 3337:3402 */         return;
/* 3338:     */       }
/* 3339:3405 */       this.m_BayesNet.addArc(sParent, sChild);
/* 3340:3406 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3341:3407 */       updateStatus();
/* 3342:     */     }
/* 3343:     */     catch (Exception e)
/* 3344:     */     {
/* 3345:3409 */       e.printStackTrace();
/* 3346:     */     }
/* 3347:     */   }
/* 3348:     */   
/* 3349:     */   void deleteArc(int iChild, String sParent)
/* 3350:     */   {
/* 3351:     */     try
/* 3352:     */     {
/* 3353:3418 */       this.m_BayesNet.deleteArc(this.m_BayesNet.getNode(sParent), iChild);
/* 3354:3419 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3355:     */     }
/* 3356:     */     catch (Exception e)
/* 3357:     */     {
/* 3358:3421 */       e.printStackTrace();
/* 3359:     */     }
/* 3360:3423 */     updateStatus();
/* 3361:     */   }
/* 3362:     */   
/* 3363:     */   void deleteArc(String sChild, int iParent)
/* 3364:     */   {
/* 3365:     */     try
/* 3366:     */     {
/* 3367:3431 */       this.m_BayesNet.deleteArc(iParent, this.m_BayesNet.getNode(sChild));
/* 3368:3432 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3369:     */     }
/* 3370:     */     catch (Exception e)
/* 3371:     */     {
/* 3372:3434 */       e.printStackTrace();
/* 3373:     */     }
/* 3374:3436 */     updateStatus();
/* 3375:     */   }
/* 3376:     */   
/* 3377:     */   void deleteArc(String[] options)
/* 3378:     */   {
/* 3379:3444 */     String sResult = (String)JOptionPane.showInputDialog(null, "Select arc to delete", "Arcs", 0, null, options, options[0]);
/* 3380:3446 */     if ((sResult != null) && (!sResult.equals("")))
/* 3381:     */     {
/* 3382:3447 */       int nPos = sResult.indexOf(" -> ");
/* 3383:3448 */       String sParent = sResult.substring(0, nPos);
/* 3384:3449 */       String sChild = sResult.substring(nPos + 4);
/* 3385:     */       try
/* 3386:     */       {
/* 3387:3451 */         this.m_BayesNet.deleteArc(sParent, sChild);
/* 3388:3452 */         this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3389:     */       }
/* 3390:     */       catch (Exception e)
/* 3391:     */       {
/* 3392:3454 */         e.printStackTrace();
/* 3393:     */       }
/* 3394:3456 */       updateStatus();
/* 3395:     */     }
/* 3396:     */   }
/* 3397:     */   
/* 3398:     */   void renameNode(int nTargetNode)
/* 3399:     */   {
/* 3400:3465 */     String sName = JOptionPane.showInputDialog(null, this.m_BayesNet.getNodeName(nTargetNode), "New name for node", 2);
/* 3401:3468 */     if ((sName == null) || (sName.equals(""))) {
/* 3402:3469 */       return;
/* 3403:     */     }
/* 3404:     */     try
/* 3405:     */     {
/* 3406:3472 */       while (this.m_BayesNet.getNode2(sName) >= 0)
/* 3407:     */       {
/* 3408:3473 */         sName = JOptionPane.showInputDialog(null, "Cannot rename to " + sName + ".\nNode with that name already exists.");
/* 3409:3475 */         if ((sName == null) || (sName.equals(""))) {
/* 3410:3476 */           return;
/* 3411:     */         }
/* 3412:     */       }
/* 3413:3479 */       this.m_BayesNet.setNodeName(nTargetNode, sName);
/* 3414:3480 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3415:     */     }
/* 3416:     */     catch (Exception e)
/* 3417:     */     {
/* 3418:3482 */       e.printStackTrace();
/* 3419:     */     }
/* 3420:3484 */     repaint();
/* 3421:     */   }
/* 3422:     */   
/* 3423:     */   void renameValue(int nTargetNode, String sValue)
/* 3424:     */   {
/* 3425:3492 */     String sNewValue = JOptionPane.showInputDialog(null, "New name for value " + sValue, "Node " + this.m_BayesNet.getNodeName(nTargetNode), 2);
/* 3426:3495 */     if ((sNewValue == null) || (sNewValue.equals(""))) {
/* 3427:3496 */       return;
/* 3428:     */     }
/* 3429:3498 */     this.m_BayesNet.renameNodeValue(nTargetNode, sValue, sNewValue);
/* 3430:3499 */     this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3431:3500 */     this.a_undo.setEnabled(true);
/* 3432:3501 */     this.a_redo.setEnabled(false);
/* 3433:3502 */     repaint();
/* 3434:     */   }
/* 3435:     */   
/* 3436:     */   void deleteNode(int iNode)
/* 3437:     */   {
/* 3438:     */     try
/* 3439:     */     {
/* 3440:3508 */       this.m_BayesNet.deleteNode(iNode);
/* 3441:3509 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3442:     */     }
/* 3443:     */     catch (Exception e)
/* 3444:     */     {
/* 3445:3511 */       e.printStackTrace();
/* 3446:     */     }
/* 3447:3513 */     updateStatus();
/* 3448:     */   }
/* 3449:     */   
/* 3450:     */   void addValue()
/* 3451:     */   {
/* 3452:3522 */     String sValue = "Value" + (this.m_BayesNet.getCardinality(this.m_nCurrentNode) + 1);
/* 3453:3523 */     String sNewValue = JOptionPane.showInputDialog(null, "New value " + sValue, "Node " + this.m_BayesNet.getNodeName(this.m_nCurrentNode), 2);
/* 3454:3526 */     if ((sNewValue == null) || (sNewValue.equals(""))) {
/* 3455:3527 */       return;
/* 3456:     */     }
/* 3457:     */     try
/* 3458:     */     {
/* 3459:3530 */       this.m_BayesNet.addNodeValue(this.m_nCurrentNode, sNewValue);
/* 3460:3531 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3461:     */     }
/* 3462:     */     catch (Exception e)
/* 3463:     */     {
/* 3464:3538 */       e.printStackTrace();
/* 3465:     */     }
/* 3466:3540 */     updateStatus();
/* 3467:     */   }
/* 3468:     */   
/* 3469:     */   void delValue(int nTargetNode, String sValue)
/* 3470:     */   {
/* 3471:     */     try
/* 3472:     */     {
/* 3473:3548 */       this.m_BayesNet.delNodeValue(nTargetNode, sValue);
/* 3474:3549 */       this.m_jStatusBar.setText(this.m_BayesNet.lastActionMsg());
/* 3475:     */     }
/* 3476:     */     catch (Exception e)
/* 3477:     */     {
/* 3478:3551 */       e.printStackTrace();
/* 3479:     */     }
/* 3480:3553 */     updateStatus();
/* 3481:     */   }
/* 3482:     */   
/* 3483:     */   void editCPT(int nTargetNode)
/* 3484:     */   {
/* 3485:3561 */     this.m_nCurrentNode = nTargetNode;
/* 3486:3562 */     final GraphVisualizerTableModel tm = new GraphVisualizerTableModel(nTargetNode);
/* 3487:     */     
/* 3488:     */ 
/* 3489:3565 */     JTable jTblProbs = new JTable(tm);
/* 3490:     */     
/* 3491:3567 */     JScrollPane js = new JScrollPane(jTblProbs);
/* 3492:     */     
/* 3493:3569 */     int nParents = this.m_BayesNet.getNrOfParents(nTargetNode);
/* 3494:3570 */     if (nParents > 0)
/* 3495:     */     {
/* 3496:3571 */       GridBagConstraints gbc = new GridBagConstraints();
/* 3497:3572 */       JPanel jPlRowHeader = new JPanel(new GridBagLayout());
/* 3498:     */       
/* 3499:     */ 
/* 3500:3575 */       int[] idx = new int[nParents];
/* 3501:     */       
/* 3502:3577 */       int[] lengths = new int[nParents];
/* 3503:     */       
/* 3504:     */ 
/* 3505:3580 */       gbc.anchor = 18;
/* 3506:3581 */       gbc.fill = 2;
/* 3507:3582 */       gbc.insets = new Insets(0, 1, 0, 0);
/* 3508:3583 */       int addNum = 0;int temp = 0;
/* 3509:3584 */       boolean dark = false;
/* 3510:     */       for (;;)
/* 3511:     */       {
/* 3512:3586 */         gbc.gridwidth = 1;
/* 3513:3587 */         for (int k = 0; k < nParents; k++)
/* 3514:     */         {
/* 3515:3588 */           int iParent2 = this.m_BayesNet.getParent(nTargetNode, k);
/* 3516:3589 */           JLabel lb = new JLabel(this.m_BayesNet.getValueName(iParent2, idx[k]));
/* 3517:3590 */           lb.setFont(new Font("Dialog", 0, 12));
/* 3518:3591 */           lb.setOpaque(true);
/* 3519:3592 */           lb.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
/* 3520:3593 */           lb.setHorizontalAlignment(0);
/* 3521:3594 */           if (dark)
/* 3522:     */           {
/* 3523:3595 */             lb.setBackground(lb.getBackground().darker());
/* 3524:3596 */             lb.setForeground(Color.white);
/* 3525:     */           }
/* 3526:     */           else
/* 3527:     */           {
/* 3528:3598 */             lb.setForeground(Color.black);
/* 3529:     */           }
/* 3530:3601 */           temp = lb.getPreferredSize().width;
/* 3531:3602 */           lb.setPreferredSize(new Dimension(temp, jTblProbs.getRowHeight()));
/* 3532:3603 */           if (lengths[k] < temp) {
/* 3533:3604 */             lengths[k] = temp;
/* 3534:     */           }
/* 3535:3606 */           temp = 0;
/* 3536:3608 */           if (k == nParents - 1)
/* 3537:     */           {
/* 3538:3609 */             gbc.gridwidth = 0;
/* 3539:3610 */             dark = dark != true;
/* 3540:     */           }
/* 3541:3612 */           jPlRowHeader.add(lb, gbc);
/* 3542:3613 */           addNum++;
/* 3543:     */         }
/* 3544:3616 */         for (int k = nParents - 1; k >= 0; k--)
/* 3545:     */         {
/* 3546:3617 */           int iParent2 = this.m_BayesNet.getParent(this.m_nCurrentNode, k);
/* 3547:3618 */           if ((idx[k] == this.m_BayesNet.getCardinality(iParent2) - 1) && (k != 0))
/* 3548:     */           {
/* 3549:3619 */             idx[k] = 0;
/* 3550:     */           }
/* 3551:     */           else
/* 3552:     */           {
/* 3553:3622 */             idx[k] += 1;
/* 3554:3623 */             break;
/* 3555:     */           }
/* 3556:     */         }
/* 3557:3627 */         int iParent2 = this.m_BayesNet.getParent(this.m_nCurrentNode, 0);
/* 3558:3628 */         if (idx[0] == this.m_BayesNet.getCardinality(iParent2))
/* 3559:     */         {
/* 3560:3629 */           JLabel lb = (JLabel)jPlRowHeader.getComponent(addNum - 1);
/* 3561:3630 */           jPlRowHeader.remove(addNum - 1);
/* 3562:3631 */           lb.setPreferredSize(new Dimension(lb.getPreferredSize().width, jTblProbs.getRowHeight()));
/* 3563:     */           
/* 3564:3633 */           gbc.gridwidth = 0;
/* 3565:3634 */           gbc.weighty = 1.0D;
/* 3566:3635 */           jPlRowHeader.add(lb, gbc);
/* 3567:3636 */           gbc.weighty = 0.0D;
/* 3568:3637 */           break;
/* 3569:     */         }
/* 3570:     */       }
/* 3571:3641 */       gbc.gridwidth = 1;
/* 3572:     */       
/* 3573:     */ 
/* 3574:     */ 
/* 3575:     */ 
/* 3576:3646 */       JPanel jPlRowNames = new JPanel(new GridBagLayout());
/* 3577:3647 */       for (int j = 0; j < nParents; j++)
/* 3578:     */       {
/* 3579:3649 */         JLabel lb1 = new JLabel(this.m_BayesNet.getNodeName(this.m_BayesNet.getParent(nTargetNode, j)));
/* 3580:     */         
/* 3581:3651 */         lb1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
/* 3582:3652 */         Dimension tempd = lb1.getPreferredSize();
/* 3583:3653 */         if (tempd.width < lengths[j])
/* 3584:     */         {
/* 3585:3654 */           lb1.setPreferredSize(new Dimension(lengths[j], tempd.height));
/* 3586:3655 */           lb1.setHorizontalAlignment(0);
/* 3587:3656 */           lb1.setMinimumSize(new Dimension(lengths[j], tempd.height));
/* 3588:     */         }
/* 3589:3657 */         else if (tempd.width > lengths[j])
/* 3590:     */         {
/* 3591:3658 */           JLabel lb2 = (JLabel)jPlRowHeader.getComponent(j);
/* 3592:3659 */           lb2.setPreferredSize(new Dimension(tempd.width, lb2.getPreferredSize().height));
/* 3593:     */         }
/* 3594:3662 */         jPlRowNames.add(lb1, gbc);
/* 3595:     */       }
/* 3596:3664 */       js.setRowHeaderView(jPlRowHeader);
/* 3597:3665 */       js.setCorner("UPPER_LEFT_CORNER", jPlRowNames);
/* 3598:     */     }
/* 3599:3668 */     final JDialog dlg = new JDialog((Frame)getTopLevelAncestor(), "Probability Distribution Table For " + this.m_BayesNet.getNodeName(nTargetNode), true);
/* 3600:     */     
/* 3601:     */ 
/* 3602:3671 */     dlg.setSize(500, 400);
/* 3603:3672 */     dlg.setLocation(getLocation().x + getWidth() / 2 - 250, getLocation().y + getHeight() / 2 - 200);
/* 3604:     */     
/* 3605:     */ 
/* 3606:3675 */     dlg.getContentPane().setLayout(new BorderLayout());
/* 3607:3676 */     dlg.getContentPane().add(js, "Center");
/* 3608:     */     
/* 3609:3678 */     JButton jBtRandomize = new JButton("Randomize");
/* 3610:3679 */     jBtRandomize.setMnemonic('R');
/* 3611:3680 */     jBtRandomize.addActionListener(new ActionListener()
/* 3612:     */     {
/* 3613:     */       public void actionPerformed(ActionEvent ae)
/* 3614:     */       {
/* 3615:3683 */         tm.randomize();
/* 3616:3684 */         dlg.repaint();
/* 3617:     */       }
/* 3618:3687 */     });
/* 3619:3688 */     JButton jBtOk = new JButton("Ok");
/* 3620:3689 */     jBtOk.setMnemonic('O');
/* 3621:3690 */     jBtOk.addActionListener(new ActionListener()
/* 3622:     */     {
/* 3623:     */       public void actionPerformed(ActionEvent ae)
/* 3624:     */       {
/* 3625:3693 */         tm.setData();
/* 3626:     */         try
/* 3627:     */         {
/* 3628:3695 */           GUI.this.m_BayesNet.setDistribution(GUI.this.m_nCurrentNode, tm.m_fProbs);
/* 3629:3696 */           GUI.this.m_jStatusBar.setText(GUI.this.m_BayesNet.lastActionMsg());
/* 3630:3697 */           GUI.this.updateStatus();
/* 3631:     */         }
/* 3632:     */         catch (Exception e)
/* 3633:     */         {
/* 3634:3699 */           e.printStackTrace();
/* 3635:     */         }
/* 3636:3701 */         dlg.setVisible(false);
/* 3637:     */       }
/* 3638:3703 */     });
/* 3639:3704 */     JButton jBtCancel = new JButton("Cancel");
/* 3640:3705 */     jBtCancel.setMnemonic('C');
/* 3641:3706 */     jBtCancel.addActionListener(new ActionListener()
/* 3642:     */     {
/* 3643:     */       public void actionPerformed(ActionEvent ae)
/* 3644:     */       {
/* 3645:3709 */         dlg.setVisible(false);
/* 3646:     */       }
/* 3647:3711 */     });
/* 3648:3712 */     Container c = new Container();
/* 3649:3713 */     c.setLayout(new GridBagLayout());
/* 3650:3714 */     c.add(jBtRandomize);
/* 3651:3715 */     c.add(jBtOk);
/* 3652:3716 */     c.add(jBtCancel);
/* 3653:     */     
/* 3654:3718 */     dlg.getContentPane().add(c, "South");
/* 3655:3719 */     dlg.setVisible(true);
/* 3656:     */   }
/* 3657:     */   
/* 3658:     */   public static void main(String[] args)
/* 3659:     */   {
/* 3660:3727 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 3661:     */     
/* 3662:     */ 
/* 3663:3730 */     LookAndFeel.setLookAndFeel();
/* 3664:     */     
/* 3665:3732 */     JFrame jf = new JFrame("Bayes Network Editor");
/* 3666:3733 */     GUI g = new GUI();
/* 3667:3734 */     JMenuBar menuBar = g.getMenuBar();
/* 3668:3736 */     if (args.length > 0) {
/* 3669:     */       try
/* 3670:     */       {
/* 3671:3738 */         g.readBIFFromFile(args[0]);
/* 3672:     */       }
/* 3673:     */       catch (IOException ex)
/* 3674:     */       {
/* 3675:3740 */         ex.printStackTrace();
/* 3676:     */       }
/* 3677:     */       catch (BIFFormatException bf)
/* 3678:     */       {
/* 3679:3742 */         bf.printStackTrace();
/* 3680:3743 */         System.exit(-1);
/* 3681:     */       }
/* 3682:     */     }
/* 3683:3747 */     jf.setJMenuBar(menuBar);
/* 3684:3748 */     jf.getContentPane().add(g);
/* 3685:3749 */     jf.setDefaultCloseOperation(3);
/* 3686:3750 */     jf.setSize(800, 600);
/* 3687:3751 */     jf.setVisible(true);
/* 3688:3752 */     g.m_Selection.updateGUI();
/* 3689:3753 */     GenericObjectEditor.registerEditors();
/* 3690:     */   }
/* 3691:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.GUI
 * JD-Core Version:    0.7.0.1
 */