/*    1:     */ package weka.gui.arffviewer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Cursor;
/*    6:     */ import java.awt.Window;
/*    7:     */ import java.awt.event.ActionEvent;
/*    8:     */ import java.awt.event.ActionListener;
/*    9:     */ import java.awt.event.WindowEvent;
/*   10:     */ import java.io.File;
/*   11:     */ import java.io.IOException;
/*   12:     */ import java.io.PrintStream;
/*   13:     */ import java.util.Collections;
/*   14:     */ import java.util.HashSet;
/*   15:     */ import java.util.Iterator;
/*   16:     */ import java.util.Vector;
/*   17:     */ import javax.swing.JComponent;
/*   18:     */ import javax.swing.JFrame;
/*   19:     */ import javax.swing.JInternalFrame;
/*   20:     */ import javax.swing.JList;
/*   21:     */ import javax.swing.JMenu;
/*   22:     */ import javax.swing.JMenuBar;
/*   23:     */ import javax.swing.JMenuItem;
/*   24:     */ import javax.swing.JPanel;
/*   25:     */ import javax.swing.JTabbedPane;
/*   26:     */ import javax.swing.KeyStroke;
/*   27:     */ import javax.swing.event.ChangeEvent;
/*   28:     */ import javax.swing.event.ChangeListener;
/*   29:     */ import weka.core.Attribute;
/*   30:     */ import weka.core.Capabilities;
/*   31:     */ import weka.core.Instances;
/*   32:     */ import weka.core.converters.AbstractFileLoader;
/*   33:     */ import weka.core.converters.AbstractFileSaver;
/*   34:     */ import weka.core.converters.AbstractSaver;
/*   35:     */ import weka.core.converters.ConverterUtils;
/*   36:     */ import weka.gui.ComponentHelper;
/*   37:     */ import weka.gui.ConverterFileChooser;
/*   38:     */ import weka.gui.JTableHelper;
/*   39:     */ import weka.gui.ListSelectorDialog;
/*   40:     */ 
/*   41:     */ public class ArffViewerMainPanel
/*   42:     */   extends JPanel
/*   43:     */   implements ActionListener, ChangeListener
/*   44:     */ {
/*   45:     */   static final long serialVersionUID = -8763161167586738753L;
/*   46:     */   public static final int DEFAULT_WIDTH = -1;
/*   47:     */   public static final int DEFAULT_HEIGHT = -1;
/*   48:     */   public static final int DEFAULT_LEFT = -1;
/*   49:     */   public static final int DEFAULT_TOP = -1;
/*   50:     */   public static final int WIDTH = 800;
/*   51:     */   public static final int HEIGHT = 600;
/*   52:     */   protected Container parent;
/*   53:     */   protected JTabbedPane tabbedPane;
/*   54:     */   protected JMenuBar menuBar;
/*   55:     */   protected JMenu menuFile;
/*   56:     */   protected JMenuItem menuFileOpen;
/*   57:     */   protected JMenuItem menuFileSave;
/*   58:     */   protected JMenuItem menuFileSaveAs;
/*   59:     */   protected JMenuItem menuFileClose;
/*   60:     */   protected JMenuItem menuFileCloseAll;
/*   61:     */   protected JMenuItem menuFileProperties;
/*   62:     */   protected JMenuItem menuFileExit;
/*   63:     */   protected JMenu menuEdit;
/*   64:     */   protected JMenuItem menuEditUndo;
/*   65:     */   protected JMenuItem menuEditCopy;
/*   66:     */   protected JMenuItem menuEditSearch;
/*   67:     */   protected JMenuItem menuEditClearSearch;
/*   68:     */   protected JMenuItem menuEditDeleteAttribute;
/*   69:     */   protected JMenuItem menuEditDeleteAttributes;
/*   70:     */   protected JMenuItem menuEditRenameAttribute;
/*   71:     */   protected JMenuItem menuEditAttributeAsClass;
/*   72:     */   protected JMenuItem menuEditDeleteInstance;
/*   73:     */   protected JMenuItem menuEditDeleteInstances;
/*   74:     */   protected JMenuItem menuEditSortInstances;
/*   75:     */   protected JMenu menuView;
/*   76:     */   protected JMenuItem menuViewAttributes;
/*   77:     */   protected JMenuItem menuViewValues;
/*   78:     */   protected JMenuItem menuViewOptimalColWidths;
/*   79:     */   protected ConverterFileChooser fileChooser;
/*   80:     */   protected String frameTitle;
/*   81:     */   protected boolean confirmExit;
/*   82:     */   protected int width;
/*   83:     */   protected int height;
/*   84:     */   protected int top;
/*   85:     */   protected int left;
/*   86:     */   protected boolean exitOnClose;
/*   87:     */   
/*   88:     */   public ArffViewerMainPanel(Container parentFrame)
/*   89:     */   {
/*   90: 135 */     this.parent = parentFrame;
/*   91: 136 */     this.frameTitle = "ARFF-Viewer";
/*   92: 137 */     createPanel();
/*   93:     */   }
/*   94:     */   
/*   95:     */   protected void createPanel()
/*   96:     */   {
/*   97: 145 */     setSize(800, 600);
/*   98:     */     
/*   99: 147 */     setConfirmExit(false);
/*  100: 148 */     setLayout(new BorderLayout());
/*  101:     */     
/*  102:     */ 
/*  103: 151 */     this.fileChooser = new ConverterFileChooser(new File(System.getProperty("user.dir")));
/*  104:     */     
/*  105: 153 */     this.fileChooser.setMultiSelectionEnabled(true);
/*  106:     */     
/*  107:     */ 
/*  108: 156 */     this.menuBar = new JMenuBar();
/*  109: 157 */     this.menuFile = new JMenu("File");
/*  110: 158 */     this.menuFileOpen = new JMenuItem("Open...", ComponentHelper.getImageIcon("open.gif"));
/*  111:     */     
/*  112: 160 */     this.menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(79, 2));
/*  113:     */     
/*  114: 162 */     this.menuFileOpen.addActionListener(this);
/*  115: 163 */     this.menuFileSave = new JMenuItem("Save", ComponentHelper.getImageIcon("save.gif"));
/*  116:     */     
/*  117: 165 */     this.menuFileSave.setAccelerator(KeyStroke.getKeyStroke(83, 2));
/*  118:     */     
/*  119: 167 */     this.menuFileSave.addActionListener(this);
/*  120: 168 */     this.menuFileSaveAs = new JMenuItem("Save as...", ComponentHelper.getImageIcon("empty.gif"));
/*  121:     */     
/*  122: 170 */     this.menuFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(83, 3));
/*  123:     */     
/*  124: 172 */     this.menuFileSaveAs.addActionListener(this);
/*  125: 173 */     this.menuFileClose = new JMenuItem("Close", ComponentHelper.getImageIcon("empty.gif"));
/*  126:     */     
/*  127: 175 */     this.menuFileClose.setAccelerator(KeyStroke.getKeyStroke(87, 2));
/*  128:     */     
/*  129: 177 */     this.menuFileClose.addActionListener(this);
/*  130: 178 */     this.menuFileCloseAll = new JMenuItem("Close all", ComponentHelper.getImageIcon("empty.gif"));
/*  131:     */     
/*  132: 180 */     this.menuFileCloseAll.addActionListener(this);
/*  133: 181 */     this.menuFileProperties = new JMenuItem("Properties", ComponentHelper.getImageIcon("empty.gif"));
/*  134:     */     
/*  135: 183 */     this.menuFileProperties.setAccelerator(KeyStroke.getKeyStroke(10, 2));
/*  136:     */     
/*  137: 185 */     this.menuFileProperties.addActionListener(this);
/*  138: 186 */     this.menuFileExit = new JMenuItem("Exit", ComponentHelper.getImageIcon("forward.gif"));
/*  139:     */     
/*  140: 188 */     this.menuFileExit.setAccelerator(KeyStroke.getKeyStroke(88, 8));
/*  141:     */     
/*  142: 190 */     this.menuFileExit.addActionListener(this);
/*  143: 191 */     this.menuFile.add(this.menuFileOpen);
/*  144: 192 */     this.menuFile.add(this.menuFileSave);
/*  145: 193 */     this.menuFile.add(this.menuFileSaveAs);
/*  146: 194 */     this.menuFile.add(this.menuFileClose);
/*  147: 195 */     this.menuFile.add(this.menuFileCloseAll);
/*  148: 196 */     this.menuFile.addSeparator();
/*  149: 197 */     this.menuFile.add(this.menuFileProperties);
/*  150: 198 */     this.menuFile.addSeparator();
/*  151: 199 */     this.menuFile.add(this.menuFileExit);
/*  152: 200 */     this.menuBar.add(this.menuFile);
/*  153:     */     
/*  154: 202 */     this.menuEdit = new JMenu("Edit");
/*  155: 203 */     this.menuEditUndo = new JMenuItem("Undo", ComponentHelper.getImageIcon("undo.gif"));
/*  156:     */     
/*  157: 205 */     this.menuEditUndo.setAccelerator(KeyStroke.getKeyStroke(90, 2));
/*  158:     */     
/*  159: 207 */     this.menuEditUndo.addActionListener(this);
/*  160: 208 */     this.menuEditCopy = new JMenuItem("Copy", ComponentHelper.getImageIcon("copy.gif"));
/*  161:     */     
/*  162: 210 */     this.menuEditCopy.setAccelerator(KeyStroke.getKeyStroke(155, 2));
/*  163:     */     
/*  164: 212 */     this.menuEditCopy.addActionListener(this);
/*  165: 213 */     this.menuEditSearch = new JMenuItem("Search...", ComponentHelper.getImageIcon("find.gif"));
/*  166:     */     
/*  167: 215 */     this.menuEditSearch.setAccelerator(KeyStroke.getKeyStroke(70, 2));
/*  168:     */     
/*  169: 217 */     this.menuEditSearch.addActionListener(this);
/*  170: 218 */     this.menuEditClearSearch = new JMenuItem("Clear search", ComponentHelper.getImageIcon("empty.gif"));
/*  171:     */     
/*  172: 220 */     this.menuEditClearSearch.setAccelerator(KeyStroke.getKeyStroke(70, 3));
/*  173:     */     
/*  174: 222 */     this.menuEditClearSearch.addActionListener(this);
/*  175: 223 */     this.menuEditRenameAttribute = new JMenuItem("Rename attribute", ComponentHelper.getImageIcon("empty.gif"));
/*  176:     */     
/*  177: 225 */     this.menuEditRenameAttribute.addActionListener(this);
/*  178: 226 */     this.menuEditAttributeAsClass = new JMenuItem("Attribute as class", ComponentHelper.getImageIcon("empty.gif"));
/*  179:     */     
/*  180: 228 */     this.menuEditAttributeAsClass.addActionListener(this);
/*  181: 229 */     this.menuEditDeleteAttribute = new JMenuItem("Delete attribute", ComponentHelper.getImageIcon("empty.gif"));
/*  182:     */     
/*  183: 231 */     this.menuEditDeleteAttribute.addActionListener(this);
/*  184: 232 */     this.menuEditDeleteAttributes = new JMenuItem("Delete attributes", ComponentHelper.getImageIcon("empty.gif"));
/*  185:     */     
/*  186: 234 */     this.menuEditDeleteAttributes.addActionListener(this);
/*  187: 235 */     this.menuEditDeleteInstance = new JMenuItem("Delete instance", ComponentHelper.getImageIcon("empty.gif"));
/*  188:     */     
/*  189: 237 */     this.menuEditDeleteInstance.addActionListener(this);
/*  190: 238 */     this.menuEditDeleteInstances = new JMenuItem("Delete instances", ComponentHelper.getImageIcon("empty.gif"));
/*  191:     */     
/*  192: 240 */     this.menuEditDeleteInstances.addActionListener(this);
/*  193: 241 */     this.menuEditSortInstances = new JMenuItem("Sort data (ascending)", ComponentHelper.getImageIcon("sort.gif"));
/*  194:     */     
/*  195: 243 */     this.menuEditSortInstances.addActionListener(this);
/*  196: 244 */     this.menuEdit.add(this.menuEditUndo);
/*  197: 245 */     this.menuEdit.addSeparator();
/*  198: 246 */     this.menuEdit.add(this.menuEditCopy);
/*  199: 247 */     this.menuEdit.addSeparator();
/*  200: 248 */     this.menuEdit.add(this.menuEditSearch);
/*  201: 249 */     this.menuEdit.add(this.menuEditClearSearch);
/*  202: 250 */     this.menuEdit.addSeparator();
/*  203: 251 */     this.menuEdit.add(this.menuEditRenameAttribute);
/*  204: 252 */     this.menuEdit.add(this.menuEditAttributeAsClass);
/*  205: 253 */     this.menuEdit.add(this.menuEditDeleteAttribute);
/*  206: 254 */     this.menuEdit.add(this.menuEditDeleteAttributes);
/*  207: 255 */     this.menuEdit.addSeparator();
/*  208: 256 */     this.menuEdit.add(this.menuEditDeleteInstance);
/*  209: 257 */     this.menuEdit.add(this.menuEditDeleteInstances);
/*  210: 258 */     this.menuEdit.add(this.menuEditSortInstances);
/*  211: 259 */     this.menuBar.add(this.menuEdit);
/*  212:     */     
/*  213: 261 */     this.menuView = new JMenu("View");
/*  214: 262 */     this.menuViewAttributes = new JMenuItem("Attributes...", ComponentHelper.getImageIcon("objects.gif"));
/*  215:     */     
/*  216: 264 */     this.menuViewAttributes.setAccelerator(KeyStroke.getKeyStroke(65, 3));
/*  217:     */     
/*  218: 266 */     this.menuViewAttributes.addActionListener(this);
/*  219: 267 */     this.menuViewValues = new JMenuItem("Values...", ComponentHelper.getImageIcon("properties.gif"));
/*  220:     */     
/*  221: 269 */     this.menuViewValues.setAccelerator(KeyStroke.getKeyStroke(86, 3));
/*  222:     */     
/*  223: 271 */     this.menuViewValues.addActionListener(this);
/*  224: 272 */     this.menuViewOptimalColWidths = new JMenuItem("Optimal column width (all)", ComponentHelper.getImageIcon("resize.gif"));
/*  225:     */     
/*  226: 274 */     this.menuViewOptimalColWidths.addActionListener(this);
/*  227: 275 */     this.menuView.add(this.menuViewAttributes);
/*  228: 276 */     this.menuView.add(this.menuViewValues);
/*  229: 277 */     this.menuView.addSeparator();
/*  230: 278 */     this.menuView.add(this.menuViewOptimalColWidths);
/*  231: 279 */     this.menuBar.add(this.menuView);
/*  232:     */     
/*  233:     */ 
/*  234: 282 */     this.tabbedPane = new JTabbedPane();
/*  235: 283 */     this.tabbedPane.addChangeListener(this);
/*  236: 284 */     add(this.tabbedPane, "Center");
/*  237:     */     
/*  238: 286 */     updateMenu();
/*  239: 287 */     updateFrameTitle();
/*  240:     */   }
/*  241:     */   
/*  242:     */   public JFrame getParentFrame()
/*  243:     */   {
/*  244: 296 */     if ((this.parent instanceof JFrame)) {
/*  245: 297 */       return (JFrame)this.parent;
/*  246:     */     }
/*  247: 299 */     return null;
/*  248:     */   }
/*  249:     */   
/*  250:     */   public JInternalFrame getParentInternalFrame()
/*  251:     */   {
/*  252: 309 */     if ((this.parent instanceof JInternalFrame)) {
/*  253: 310 */       return (JInternalFrame)this.parent;
/*  254:     */     }
/*  255: 312 */     return null;
/*  256:     */   }
/*  257:     */   
/*  258:     */   public void setParent(Container value)
/*  259:     */   {
/*  260: 322 */     this.parent = value;
/*  261:     */   }
/*  262:     */   
/*  263:     */   public JMenuBar getMenu()
/*  264:     */   {
/*  265: 331 */     return this.menuBar;
/*  266:     */   }
/*  267:     */   
/*  268:     */   public JTabbedPane getTabbedPane()
/*  269:     */   {
/*  270: 340 */     return this.tabbedPane;
/*  271:     */   }
/*  272:     */   
/*  273:     */   public void setConfirmExit(boolean confirm)
/*  274:     */   {
/*  275: 349 */     this.confirmExit = confirm;
/*  276:     */   }
/*  277:     */   
/*  278:     */   public boolean getConfirmExit()
/*  279:     */   {
/*  280: 359 */     return this.confirmExit;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public void setExitOnClose(boolean value)
/*  284:     */   {
/*  285: 368 */     this.exitOnClose = value;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public boolean getExitOnClose()
/*  289:     */   {
/*  290: 377 */     return this.exitOnClose;
/*  291:     */   }
/*  292:     */   
/*  293:     */   public void refresh()
/*  294:     */   {
/*  295: 384 */     validate();
/*  296: 385 */     repaint();
/*  297:     */   }
/*  298:     */   
/*  299:     */   public String getFrameTitle()
/*  300:     */   {
/*  301: 394 */     if (getCurrentFilename().equals("")) {
/*  302: 395 */       return this.frameTitle;
/*  303:     */     }
/*  304: 397 */     return this.frameTitle + " - " + getCurrentFilename();
/*  305:     */   }
/*  306:     */   
/*  307:     */   public void updateFrameTitle()
/*  308:     */   {
/*  309: 405 */     if (getParentFrame() != null) {
/*  310: 406 */       getParentFrame().setTitle(getFrameTitle());
/*  311:     */     }
/*  312: 408 */     if (getParentInternalFrame() != null) {
/*  313: 409 */       getParentInternalFrame().setTitle(getFrameTitle());
/*  314:     */     }
/*  315:     */   }
/*  316:     */   
/*  317:     */   protected void updateMenu()
/*  318:     */   {
/*  319: 421 */     boolean fileOpen = getCurrentPanel() != null;
/*  320: 422 */     boolean isChanged = (fileOpen) && (getCurrentPanel().isChanged());
/*  321: 423 */     boolean canUndo = (fileOpen) && (getCurrentPanel().canUndo());
/*  322:     */     
/*  323:     */ 
/*  324: 426 */     this.menuFileOpen.setEnabled(true);
/*  325: 427 */     this.menuFileSave.setEnabled(isChanged);
/*  326: 428 */     this.menuFileSaveAs.setEnabled(fileOpen);
/*  327: 429 */     this.menuFileClose.setEnabled(fileOpen);
/*  328: 430 */     this.menuFileCloseAll.setEnabled(fileOpen);
/*  329: 431 */     this.menuFileProperties.setEnabled(fileOpen);
/*  330: 432 */     this.menuFileExit.setEnabled(true);
/*  331:     */     
/*  332: 434 */     this.menuEditUndo.setEnabled(canUndo);
/*  333: 435 */     this.menuEditCopy.setEnabled(fileOpen);
/*  334: 436 */     this.menuEditSearch.setEnabled(fileOpen);
/*  335: 437 */     this.menuEditClearSearch.setEnabled(fileOpen);
/*  336: 438 */     this.menuEditAttributeAsClass.setEnabled(fileOpen);
/*  337: 439 */     this.menuEditRenameAttribute.setEnabled(fileOpen);
/*  338: 440 */     this.menuEditDeleteAttribute.setEnabled(fileOpen);
/*  339: 441 */     this.menuEditDeleteAttributes.setEnabled(fileOpen);
/*  340: 442 */     this.menuEditDeleteInstance.setEnabled(fileOpen);
/*  341: 443 */     this.menuEditDeleteInstances.setEnabled(fileOpen);
/*  342: 444 */     this.menuEditSortInstances.setEnabled(fileOpen);
/*  343:     */     
/*  344: 446 */     this.menuViewAttributes.setEnabled(fileOpen);
/*  345: 447 */     this.menuViewValues.setEnabled(fileOpen);
/*  346: 448 */     this.menuViewOptimalColWidths.setEnabled(fileOpen);
/*  347:     */   }
/*  348:     */   
/*  349:     */   protected void setTabTitle(JComponent component)
/*  350:     */   {
/*  351: 459 */     if (!(component instanceof ArffPanel)) {
/*  352: 460 */       return;
/*  353:     */     }
/*  354: 463 */     int index = this.tabbedPane.indexOfComponent(component);
/*  355: 464 */     if (index == -1) {
/*  356: 465 */       return;
/*  357:     */     }
/*  358: 468 */     this.tabbedPane.setTitleAt(index, ((ArffPanel)component).getTitle());
/*  359: 469 */     updateFrameTitle();
/*  360:     */   }
/*  361:     */   
/*  362:     */   public int getPanelCount()
/*  363:     */   {
/*  364: 478 */     return this.tabbedPane.getTabCount();
/*  365:     */   }
/*  366:     */   
/*  367:     */   public ArffPanel getPanel(int index)
/*  368:     */   {
/*  369: 488 */     if ((index >= 0) && (index < getPanelCount())) {
/*  370: 489 */       return (ArffPanel)this.tabbedPane.getComponentAt(index);
/*  371:     */     }
/*  372: 491 */     return null;
/*  373:     */   }
/*  374:     */   
/*  375:     */   public int getCurrentIndex()
/*  376:     */   {
/*  377: 501 */     return this.tabbedPane.getSelectedIndex();
/*  378:     */   }
/*  379:     */   
/*  380:     */   public ArffPanel getCurrentPanel()
/*  381:     */   {
/*  382: 510 */     return getPanel(getCurrentIndex());
/*  383:     */   }
/*  384:     */   
/*  385:     */   public boolean isPanelSelected()
/*  386:     */   {
/*  387: 519 */     return getCurrentPanel() != null;
/*  388:     */   }
/*  389:     */   
/*  390:     */   public String getFilename(int index)
/*  391:     */   {
/*  392: 532 */     String result = "";
/*  393: 533 */     ArffPanel panel = getPanel(index);
/*  394: 535 */     if (panel != null) {
/*  395: 536 */       result = panel.getFilename();
/*  396:     */     }
/*  397: 539 */     return result;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public String getCurrentFilename()
/*  401:     */   {
/*  402: 548 */     return getFilename(getCurrentIndex());
/*  403:     */   }
/*  404:     */   
/*  405:     */   public void setFilename(int index, String filename)
/*  406:     */   {
/*  407: 560 */     ArffPanel panel = getPanel(index);
/*  408: 562 */     if (panel != null)
/*  409:     */     {
/*  410: 563 */       panel.setFilename(filename);
/*  411: 564 */       setTabTitle(panel);
/*  412:     */     }
/*  413:     */   }
/*  414:     */   
/*  415:     */   public void setCurrentFilename(String filename)
/*  416:     */   {
/*  417: 574 */     setFilename(getCurrentIndex(), filename);
/*  418:     */   }
/*  419:     */   
/*  420:     */   protected boolean saveChanges()
/*  421:     */   {
/*  422: 584 */     return saveChanges(true);
/*  423:     */   }
/*  424:     */   
/*  425:     */   protected boolean saveChanges(boolean showCancel)
/*  426:     */   {
/*  427: 598 */     if (!isPanelSelected()) {
/*  428: 599 */       return true;
/*  429:     */     }
/*  430: 602 */     boolean result = !getCurrentPanel().isChanged();
/*  431: 604 */     if (getCurrentPanel().isChanged())
/*  432:     */     {
/*  433:     */       int button;
/*  434:     */       try
/*  435:     */       {
/*  436:     */         int button;
/*  437: 606 */         if (showCancel) {
/*  438: 607 */           button = ComponentHelper.showMessageBox(this, "Changed", "The file is not saved - Do you want to save it?", 1, 3);
/*  439:     */         } else {
/*  440: 611 */           button = ComponentHelper.showMessageBox(this, "Changed", "The file is not saved - Do you want to save it?", 0, 3);
/*  441:     */         }
/*  442:     */       }
/*  443:     */       catch (Exception e)
/*  444:     */       {
/*  445: 616 */         button = 2;
/*  446:     */       }
/*  447: 619 */       switch (button)
/*  448:     */       {
/*  449:     */       case 0: 
/*  450: 621 */         saveFile();
/*  451: 622 */         result = !getCurrentPanel().isChanged();
/*  452: 623 */         break;
/*  453:     */       case 1: 
/*  454: 625 */         result = true;
/*  455: 626 */         break;
/*  456:     */       case 2: 
/*  457: 628 */         result = false;
/*  458:     */       }
/*  459:     */     }
/*  460: 633 */     return result;
/*  461:     */   }
/*  462:     */   
/*  463:     */   public void loadFile(String filename, AbstractFileLoader... loaders)
/*  464:     */   {
/*  465: 645 */     ArffPanel panel = new ArffPanel(filename, loaders);
/*  466: 646 */     panel.addChangeListener(this);
/*  467: 647 */     this.tabbedPane.addTab(panel.getTitle(), panel);
/*  468: 648 */     this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
/*  469:     */   }
/*  470:     */   
/*  471:     */   public void loadFile()
/*  472:     */   {
/*  473: 659 */     int retVal = this.fileChooser.showOpenDialog(this);
/*  474: 660 */     if (retVal != 0) {
/*  475: 661 */       return;
/*  476:     */     }
/*  477: 664 */     setCursor(Cursor.getPredefinedCursor(3));
/*  478: 666 */     for (int i = 0; i < this.fileChooser.getSelectedFiles().length; i++)
/*  479:     */     {
/*  480: 667 */       String filename = this.fileChooser.getSelectedFiles()[i].getAbsolutePath();
/*  481: 668 */       loadFile(filename, new AbstractFileLoader[] { this.fileChooser.getLoader() });
/*  482:     */     }
/*  483: 671 */     setCursor(Cursor.getPredefinedCursor(0));
/*  484:     */   }
/*  485:     */   
/*  486:     */   public void saveFile()
/*  487:     */   {
/*  488: 683 */     ArffPanel panel = getCurrentPanel();
/*  489: 684 */     if (panel == null) {
/*  490: 685 */       return;
/*  491:     */     }
/*  492: 688 */     String filename = panel.getFilename();
/*  493: 690 */     if (filename.equals("Instances"))
/*  494:     */     {
/*  495: 691 */       saveFileAs();
/*  496:     */     }
/*  497:     */     else
/*  498:     */     {
/*  499: 693 */       AbstractSaver saver = ConverterUtils.getSaverForFile(filename);
/*  500:     */       try
/*  501:     */       {
/*  502: 695 */         saver.setFile(new File(filename));
/*  503: 696 */         saver.setInstances(panel.getInstances());
/*  504: 697 */         saver.writeBatch();
/*  505: 698 */         panel.setChanged(false);
/*  506: 699 */         setCurrentFilename(filename);
/*  507:     */       }
/*  508:     */       catch (Exception e)
/*  509:     */       {
/*  510: 701 */         e.printStackTrace();
/*  511:     */       }
/*  512:     */     }
/*  513:     */   }
/*  514:     */   
/*  515:     */   public void saveFileAs()
/*  516:     */   {
/*  517: 714 */     ArffPanel panel = getCurrentPanel();
/*  518: 715 */     if (panel == null)
/*  519:     */     {
/*  520: 716 */       System.out.println("nothing selected!");
/*  521: 717 */       return;
/*  522:     */     }
/*  523: 720 */     if (!getCurrentFilename().equals("")) {
/*  524:     */       try
/*  525:     */       {
/*  526: 722 */         this.fileChooser.setSelectedFile(new File(getCurrentFilename()));
/*  527:     */       }
/*  528:     */       catch (Exception e) {}
/*  529:     */     }
/*  530:     */     try
/*  531:     */     {
/*  532: 730 */       this.fileChooser.setCapabilitiesFilter(Capabilities.forInstances(panel.getInstances()));
/*  533:     */     }
/*  534:     */     catch (Exception e)
/*  535:     */     {
/*  536: 733 */       this.fileChooser.setCapabilitiesFilter(null);
/*  537:     */     }
/*  538: 736 */     int retVal = this.fileChooser.showSaveDialog(this);
/*  539: 737 */     if (retVal != 0) {
/*  540: 738 */       return;
/*  541:     */     }
/*  542: 741 */     panel.setChanged(false);
/*  543: 742 */     setCurrentFilename(this.fileChooser.getSelectedFile().getAbsolutePath());
/*  544:     */     
/*  545:     */ 
/*  546: 745 */     AbstractFileSaver saver = this.fileChooser.getSaver();
/*  547: 746 */     saver.setInstances(panel.getInstances());
/*  548:     */     try
/*  549:     */     {
/*  550: 748 */       saver.writeBatch();
/*  551: 749 */       panel.setChanged(false);
/*  552:     */     }
/*  553:     */     catch (IOException e)
/*  554:     */     {
/*  555: 751 */       e.printStackTrace();
/*  556:     */     }
/*  557:     */   }
/*  558:     */   
/*  559:     */   public void closeFile()
/*  560:     */   {
/*  561: 759 */     closeFile(true);
/*  562:     */   }
/*  563:     */   
/*  564:     */   public void closeFile(boolean showCancel)
/*  565:     */   {
/*  566: 770 */     if (getCurrentIndex() == -1) {
/*  567: 771 */       return;
/*  568:     */     }
/*  569: 774 */     if (!saveChanges(showCancel)) {
/*  570: 775 */       return;
/*  571:     */     }
/*  572: 778 */     this.tabbedPane.removeTabAt(getCurrentIndex());
/*  573: 779 */     updateFrameTitle();
/*  574: 780 */     System.gc();
/*  575:     */   }
/*  576:     */   
/*  577:     */   public void closeAllFiles()
/*  578:     */   {
/*  579: 787 */     while (this.tabbedPane.getTabCount() > 0)
/*  580:     */     {
/*  581: 788 */       if (!saveChanges(true)) {
/*  582: 789 */         return;
/*  583:     */       }
/*  584: 792 */       this.tabbedPane.removeTabAt(getCurrentIndex());
/*  585: 793 */       updateFrameTitle();
/*  586: 794 */       System.gc();
/*  587:     */     }
/*  588:     */   }
/*  589:     */   
/*  590:     */   public void showProperties()
/*  591:     */   {
/*  592: 807 */     ArffPanel panel = getCurrentPanel();
/*  593: 808 */     if (panel == null) {
/*  594: 809 */       return;
/*  595:     */     }
/*  596: 812 */     Instances inst = panel.getInstances();
/*  597: 813 */     if (inst == null) {
/*  598: 814 */       return;
/*  599:     */     }
/*  600: 816 */     if (inst.classIndex() < 0) {
/*  601: 817 */       inst.setClassIndex(inst.numAttributes() - 1);
/*  602:     */     }
/*  603: 821 */     Vector<String> props = new Vector();
/*  604: 822 */     props.add("Filename: " + panel.getFilename());
/*  605: 823 */     props.add("Relation name: " + inst.relationName());
/*  606: 824 */     props.add("# of instances: " + inst.numInstances());
/*  607: 825 */     props.add("# of attributes: " + inst.numAttributes());
/*  608: 826 */     props.add("Class attribute: " + inst.classAttribute().name());
/*  609: 827 */     props.add("# of class labels: " + inst.numClasses());
/*  610:     */     
/*  611: 829 */     ListSelectorDialog dialog = new ListSelectorDialog(getParentFrame(), new JList(props));
/*  612: 830 */     dialog.showDialog();
/*  613:     */   }
/*  614:     */   
/*  615:     */   public void close()
/*  616:     */   {
/*  617: 838 */     if (getParentInternalFrame() != null) {
/*  618: 839 */       getParentInternalFrame().doDefaultCloseAction();
/*  619: 840 */     } else if (getParentFrame() != null) {
/*  620: 841 */       getParentFrame().dispatchEvent(new WindowEvent(getParentFrame(), 201));
/*  621:     */     }
/*  622:     */   }
/*  623:     */   
/*  624:     */   public void undo()
/*  625:     */   {
/*  626: 850 */     if (!isPanelSelected()) {
/*  627: 851 */       return;
/*  628:     */     }
/*  629: 854 */     getCurrentPanel().undo();
/*  630:     */   }
/*  631:     */   
/*  632:     */   public void copyContent()
/*  633:     */   {
/*  634: 861 */     if (!isPanelSelected()) {
/*  635: 862 */       return;
/*  636:     */     }
/*  637: 865 */     getCurrentPanel().copyContent();
/*  638:     */   }
/*  639:     */   
/*  640:     */   public void search()
/*  641:     */   {
/*  642: 872 */     if (!isPanelSelected()) {
/*  643: 873 */       return;
/*  644:     */     }
/*  645: 876 */     getCurrentPanel().search();
/*  646:     */   }
/*  647:     */   
/*  648:     */   public void clearSearch()
/*  649:     */   {
/*  650: 883 */     if (!isPanelSelected()) {
/*  651: 884 */       return;
/*  652:     */     }
/*  653: 887 */     getCurrentPanel().clearSearch();
/*  654:     */   }
/*  655:     */   
/*  656:     */   public void renameAttribute()
/*  657:     */   {
/*  658: 894 */     if (!isPanelSelected()) {
/*  659: 895 */       return;
/*  660:     */     }
/*  661: 898 */     getCurrentPanel().renameAttribute();
/*  662:     */   }
/*  663:     */   
/*  664:     */   public void attributeAsClass()
/*  665:     */   {
/*  666: 906 */     if (!isPanelSelected()) {
/*  667: 907 */       return;
/*  668:     */     }
/*  669: 910 */     getCurrentPanel().attributeAsClass();
/*  670:     */   }
/*  671:     */   
/*  672:     */   public void deleteAttribute(boolean multiple)
/*  673:     */   {
/*  674: 919 */     if (!isPanelSelected()) {
/*  675: 920 */       return;
/*  676:     */     }
/*  677: 923 */     if (multiple) {
/*  678: 924 */       getCurrentPanel().deleteAttributes();
/*  679:     */     } else {
/*  680: 926 */       getCurrentPanel().deleteAttribute();
/*  681:     */     }
/*  682:     */   }
/*  683:     */   
/*  684:     */   public void deleteInstance(boolean multiple)
/*  685:     */   {
/*  686: 936 */     if (!isPanelSelected()) {
/*  687: 937 */       return;
/*  688:     */     }
/*  689: 940 */     if (multiple) {
/*  690: 941 */       getCurrentPanel().deleteInstances();
/*  691:     */     } else {
/*  692: 943 */       getCurrentPanel().deleteInstance();
/*  693:     */     }
/*  694:     */   }
/*  695:     */   
/*  696:     */   public void sortInstances()
/*  697:     */   {
/*  698: 951 */     if (!isPanelSelected()) {
/*  699: 952 */       return;
/*  700:     */     }
/*  701: 955 */     getCurrentPanel().sortInstances();
/*  702:     */   }
/*  703:     */   
/*  704:     */   public String showAttributes()
/*  705:     */   {
/*  706: 971 */     if (!isPanelSelected()) {
/*  707: 972 */       return null;
/*  708:     */     }
/*  709: 975 */     JList list = new JList(getCurrentPanel().getAttributes());
/*  710: 976 */     ListSelectorDialog dialog = new ListSelectorDialog(getParentFrame(), list);
/*  711: 977 */     int result = dialog.showDialog();
/*  712: 979 */     if (result == 0)
/*  713:     */     {
/*  714: 980 */       ArffSortedTableModel model = (ArffSortedTableModel)getCurrentPanel().getTable().getModel();
/*  715: 981 */       String name = list.getSelectedValue().toString();
/*  716: 982 */       int i = model.getAttributeColumn(name);
/*  717: 983 */       JTableHelper.scrollToVisible(getCurrentPanel().getTable(), 0, i);
/*  718: 984 */       getCurrentPanel().getTable().setSelectedColumn(i);
/*  719: 985 */       return name;
/*  720:     */     }
/*  721: 987 */     return null;
/*  722:     */   }
/*  723:     */   
/*  724:     */   public void showValues()
/*  725:     */   {
/*  726:1006 */     String attribute = showAttributes();
/*  727:1007 */     if (attribute == null) {
/*  728:1008 */       return;
/*  729:     */     }
/*  730:1011 */     ArffTable table = getCurrentPanel().getTable();
/*  731:1012 */     ArffSortedTableModel model = (ArffSortedTableModel)table.getModel();
/*  732:     */     
/*  733:     */ 
/*  734:1015 */     int col = -1;
/*  735:1016 */     for (int i = 0; i < table.getColumnCount(); i++) {
/*  736:1017 */       if (table.getPlainColumnName(i).equals(attribute))
/*  737:     */       {
/*  738:1018 */         col = i;
/*  739:1019 */         break;
/*  740:     */       }
/*  741:     */     }
/*  742:1023 */     if (col == -1) {
/*  743:1024 */       return;
/*  744:     */     }
/*  745:1028 */     HashSet<String> values = new HashSet();
/*  746:1029 */     Vector<String> items = new Vector();
/*  747:1030 */     for (i = 0; i < model.getRowCount(); i++) {
/*  748:1031 */       values.add(model.getValueAt(i, col).toString());
/*  749:     */     }
/*  750:1033 */     if (values.isEmpty()) {
/*  751:1034 */       return;
/*  752:     */     }
/*  753:1036 */     Iterator<String> iter = values.iterator();
/*  754:1037 */     while (iter.hasNext()) {
/*  755:1038 */       items.add(iter.next());
/*  756:     */     }
/*  757:1040 */     Collections.sort(items);
/*  758:     */     
/*  759:1042 */     ListSelectorDialog dialog = new ListSelectorDialog(getParentFrame(), new JList(items));
/*  760:1043 */     dialog.showDialog();
/*  761:     */   }
/*  762:     */   
/*  763:     */   public void setOptimalColWidths()
/*  764:     */   {
/*  765:1050 */     if (!isPanelSelected()) {
/*  766:1051 */       return;
/*  767:     */     }
/*  768:1054 */     getCurrentPanel().setOptimalColWidths();
/*  769:     */   }
/*  770:     */   
/*  771:     */   public void actionPerformed(ActionEvent e)
/*  772:     */   {
/*  773:1066 */     Object o = e.getSource();
/*  774:1068 */     if (o == this.menuFileOpen) {
/*  775:1069 */       loadFile();
/*  776:1070 */     } else if (o == this.menuFileSave) {
/*  777:1071 */       saveFile();
/*  778:1072 */     } else if (o == this.menuFileSaveAs) {
/*  779:1073 */       saveFileAs();
/*  780:1074 */     } else if (o == this.menuFileClose) {
/*  781:1075 */       closeFile();
/*  782:1076 */     } else if (o == this.menuFileCloseAll) {
/*  783:1077 */       closeAllFiles();
/*  784:1078 */     } else if (o == this.menuFileProperties) {
/*  785:1079 */       showProperties();
/*  786:1080 */     } else if (o == this.menuFileExit) {
/*  787:1081 */       close();
/*  788:1082 */     } else if (o == this.menuEditUndo) {
/*  789:1083 */       undo();
/*  790:1084 */     } else if (o == this.menuEditCopy) {
/*  791:1085 */       copyContent();
/*  792:1086 */     } else if (o == this.menuEditSearch) {
/*  793:1087 */       search();
/*  794:1088 */     } else if (o == this.menuEditClearSearch) {
/*  795:1089 */       clearSearch();
/*  796:1090 */     } else if (o == this.menuEditDeleteAttribute) {
/*  797:1091 */       deleteAttribute(false);
/*  798:1092 */     } else if (o == this.menuEditDeleteAttributes) {
/*  799:1093 */       deleteAttribute(true);
/*  800:1094 */     } else if (o == this.menuEditRenameAttribute) {
/*  801:1095 */       renameAttribute();
/*  802:1096 */     } else if (o == this.menuEditAttributeAsClass) {
/*  803:1097 */       attributeAsClass();
/*  804:1098 */     } else if (o == this.menuEditDeleteInstance) {
/*  805:1099 */       deleteInstance(false);
/*  806:1100 */     } else if (o == this.menuEditDeleteInstances) {
/*  807:1101 */       deleteInstance(true);
/*  808:1102 */     } else if (o == this.menuEditSortInstances) {
/*  809:1103 */       sortInstances();
/*  810:1104 */     } else if (o == this.menuViewAttributes) {
/*  811:1105 */       showAttributes();
/*  812:1106 */     } else if (o == this.menuViewValues) {
/*  813:1107 */       showValues();
/*  814:1108 */     } else if (o == this.menuViewOptimalColWidths) {
/*  815:1109 */       setOptimalColWidths();
/*  816:     */     }
/*  817:1112 */     updateMenu();
/*  818:     */   }
/*  819:     */   
/*  820:     */   public void stateChanged(ChangeEvent e)
/*  821:     */   {
/*  822:1122 */     updateFrameTitle();
/*  823:1123 */     updateMenu();
/*  824:1126 */     if ((e.getSource() instanceof JComponent)) {
/*  825:1127 */       setTabTitle((JComponent)e.getSource());
/*  826:     */     }
/*  827:     */   }
/*  828:     */   
/*  829:     */   public String toString()
/*  830:     */   {
/*  831:1138 */     return getClass().getName();
/*  832:     */   }
/*  833:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffViewerMainPanel
 * JD-Core Version:    0.7.0.1
 */