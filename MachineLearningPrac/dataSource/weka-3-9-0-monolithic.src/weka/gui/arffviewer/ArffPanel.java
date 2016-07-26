/*    1:     */ package weka.gui.arffviewer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Cursor;
/*    5:     */ import java.awt.Toolkit;
/*    6:     */ import java.awt.datatransfer.Clipboard;
/*    7:     */ import java.awt.datatransfer.StringSelection;
/*    8:     */ import java.awt.event.ActionEvent;
/*    9:     */ import java.awt.event.ActionListener;
/*   10:     */ import java.awt.event.MouseEvent;
/*   11:     */ import java.awt.event.MouseListener;
/*   12:     */ import java.io.File;
/*   13:     */ import java.util.Collections;
/*   14:     */ import java.util.HashSet;
/*   15:     */ import java.util.Iterator;
/*   16:     */ import java.util.Vector;
/*   17:     */ import javax.swing.JLabel;
/*   18:     */ import javax.swing.JList;
/*   19:     */ import javax.swing.JMenuItem;
/*   20:     */ import javax.swing.JPanel;
/*   21:     */ import javax.swing.JPopupMenu;
/*   22:     */ import javax.swing.JScrollPane;
/*   23:     */ import javax.swing.event.ChangeEvent;
/*   24:     */ import javax.swing.event.ChangeListener;
/*   25:     */ import javax.swing.event.TableModelEvent;
/*   26:     */ import javax.swing.table.JTableHeader;
/*   27:     */ import weka.core.Attribute;
/*   28:     */ import weka.core.Instance;
/*   29:     */ import weka.core.Instances;
/*   30:     */ import weka.core.Undoable;
/*   31:     */ import weka.core.Utils;
/*   32:     */ import weka.core.converters.AbstractFileLoader;
/*   33:     */ import weka.gui.ComponentHelper;
/*   34:     */ import weka.gui.JTableHelper;
/*   35:     */ import weka.gui.ListSelectorDialog;
/*   36:     */ 
/*   37:     */ public class ArffPanel
/*   38:     */   extends JPanel
/*   39:     */   implements ActionListener, ChangeListener, MouseListener, Undoable
/*   40:     */ {
/*   41:     */   static final long serialVersionUID = -4697041150989513939L;
/*   42:     */   public static final String TAB_INSTANCES = "Instances";
/*   43:     */   private ArffTable m_TableArff;
/*   44:     */   private JPopupMenu m_PopupHeader;
/*   45:     */   private JPopupMenu m_PopupRows;
/*   46:     */   private JLabel m_LabelName;
/*   47:     */   private boolean m_ShowAttributeIndex;
/*   48:     */   private JMenuItem menuItemMean;
/*   49:     */   private JMenuItem menuItemSetAllValues;
/*   50:     */   private JMenuItem menuItemSetMissingValues;
/*   51:     */   private JMenuItem menuItemReplaceValues;
/*   52:     */   private JMenuItem menuItemRenameAttribute;
/*   53:     */   private JMenuItem menuItemAttributeAsClass;
/*   54:     */   private JMenuItem menuItemDeleteAttribute;
/*   55:     */   private JMenuItem menuItemDeleteAttributes;
/*   56:     */   private JMenuItem menuItemSortInstances;
/*   57:     */   private JMenuItem menuItemDeleteSelectedInstance;
/*   58:     */   private JMenuItem menuItemDeleteAllSelectedInstances;
/*   59:     */   private JMenuItem menuItemInsertInstance;
/*   60:     */   private JMenuItem menuItemSearch;
/*   61:     */   private JMenuItem menuItemClearSearch;
/*   62:     */   private JMenuItem menuItemUndo;
/*   63:     */   private JMenuItem menuItemCopy;
/*   64:     */   private JMenuItem menuItemOptimalColWidth;
/*   65:     */   private JMenuItem menuItemOptimalColWidths;
/*   66:     */   private String m_Filename;
/*   67:     */   private String m_Title;
/*   68:     */   private int m_CurrentCol;
/*   69:     */   private boolean m_Changed;
/*   70:     */   private HashSet<ChangeListener> m_ChangeListeners;
/*   71:     */   private String m_LastSearch;
/*   72:     */   private String m_LastReplace;
/*   73:     */   
/*   74:     */   public ArffPanel()
/*   75:     */   {
/*   76: 126 */     initialize();
/*   77: 127 */     createPanel();
/*   78:     */   }
/*   79:     */   
/*   80:     */   public ArffPanel(String filename, AbstractFileLoader... loaders)
/*   81:     */   {
/*   82: 137 */     this();
/*   83:     */     
/*   84: 139 */     loadFile(filename, loaders);
/*   85:     */   }
/*   86:     */   
/*   87:     */   public ArffPanel(Instances data)
/*   88:     */   {
/*   89: 148 */     this();
/*   90:     */     
/*   91: 150 */     this.m_Filename = "";
/*   92:     */     
/*   93: 152 */     setInstances(data);
/*   94:     */   }
/*   95:     */   
/*   96:     */   protected void initialize()
/*   97:     */   {
/*   98: 159 */     this.m_Filename = "";
/*   99: 160 */     this.m_Title = "";
/*  100: 161 */     this.m_CurrentCol = -1;
/*  101: 162 */     this.m_LastSearch = "";
/*  102: 163 */     this.m_LastReplace = "";
/*  103: 164 */     this.m_ShowAttributeIndex = true;
/*  104: 165 */     this.m_Changed = false;
/*  105: 166 */     this.m_ChangeListeners = new HashSet();
/*  106:     */   }
/*  107:     */   
/*  108:     */   protected void createPanel()
/*  109:     */   {
/*  110: 175 */     setLayout(new BorderLayout());
/*  111:     */     
/*  112: 177 */     this.menuItemMean = new JMenuItem("Get mean...");
/*  113: 178 */     this.menuItemMean.addActionListener(this);
/*  114: 179 */     this.menuItemSetAllValues = new JMenuItem("Set all values to...");
/*  115: 180 */     this.menuItemSetAllValues.addActionListener(this);
/*  116: 181 */     this.menuItemSetMissingValues = new JMenuItem("Set missing values to...");
/*  117: 182 */     this.menuItemSetMissingValues.addActionListener(this);
/*  118: 183 */     this.menuItemReplaceValues = new JMenuItem("Replace values with...");
/*  119: 184 */     this.menuItemReplaceValues.addActionListener(this);
/*  120: 185 */     this.menuItemRenameAttribute = new JMenuItem("Rename attribute...");
/*  121: 186 */     this.menuItemRenameAttribute.addActionListener(this);
/*  122: 187 */     this.menuItemAttributeAsClass = new JMenuItem("Attribute as class");
/*  123: 188 */     this.menuItemAttributeAsClass.addActionListener(this);
/*  124: 189 */     this.menuItemDeleteAttribute = new JMenuItem("Delete attribute");
/*  125: 190 */     this.menuItemDeleteAttribute.addActionListener(this);
/*  126: 191 */     this.menuItemDeleteAttributes = new JMenuItem("Delete attributes...");
/*  127: 192 */     this.menuItemDeleteAttributes.addActionListener(this);
/*  128: 193 */     this.menuItemSortInstances = new JMenuItem("Sort data (ascending)");
/*  129: 194 */     this.menuItemSortInstances.addActionListener(this);
/*  130: 195 */     this.menuItemOptimalColWidth = new JMenuItem("Optimal column width (current)");
/*  131: 196 */     this.menuItemOptimalColWidth.addActionListener(this);
/*  132: 197 */     this.menuItemOptimalColWidths = new JMenuItem("Optimal column width (all)");
/*  133: 198 */     this.menuItemOptimalColWidths.addActionListener(this);
/*  134: 199 */     this.menuItemInsertInstance = new JMenuItem("Insert new instance");
/*  135:     */     
/*  136:     */ 
/*  137:     */ 
/*  138: 203 */     this.menuItemUndo = new JMenuItem("Undo");
/*  139: 204 */     this.menuItemUndo.addActionListener(this);
/*  140: 205 */     this.menuItemCopy = new JMenuItem("Copy");
/*  141: 206 */     this.menuItemCopy.addActionListener(this);
/*  142: 207 */     this.menuItemSearch = new JMenuItem("Search...");
/*  143: 208 */     this.menuItemSearch.addActionListener(this);
/*  144: 209 */     this.menuItemClearSearch = new JMenuItem("Clear search");
/*  145: 210 */     this.menuItemClearSearch.addActionListener(this);
/*  146: 211 */     this.menuItemDeleteSelectedInstance = new JMenuItem("Delete selected instance");
/*  147: 212 */     this.menuItemDeleteSelectedInstance.addActionListener(this);
/*  148: 213 */     this.menuItemDeleteAllSelectedInstances = new JMenuItem("Delete ALL selected instances");
/*  149:     */     
/*  150: 215 */     this.menuItemDeleteAllSelectedInstances.addActionListener(this);
/*  151: 216 */     this.menuItemInsertInstance.addActionListener(this);
/*  152:     */     
/*  153:     */ 
/*  154: 219 */     this.m_TableArff = new ArffTable();
/*  155: 220 */     this.m_TableArff.setToolTipText("Right click (or left+alt) for context menu");
/*  156: 221 */     this.m_TableArff.getTableHeader().addMouseListener(this);
/*  157: 222 */     this.m_TableArff.getTableHeader().setToolTipText("<html><b>Sort view:</b> left click = ascending / Shift + left click = descending<br><b>Menu:</b> right click (or left+alt)</html>");
/*  158:     */     
/*  159:     */ 
/*  160:     */ 
/*  161: 226 */     this.m_TableArff.getTableHeader().setDefaultRenderer(new ArffTableCellRenderer());
/*  162:     */     
/*  163: 228 */     this.m_TableArff.addChangeListener(this);
/*  164: 229 */     this.m_TableArff.addMouseListener(this);
/*  165: 230 */     JScrollPane pane = new JScrollPane(this.m_TableArff);
/*  166: 231 */     add(pane, "Center");
/*  167:     */     
/*  168:     */ 
/*  169: 234 */     this.m_LabelName = new JLabel();
/*  170: 235 */     add(this.m_LabelName, "North");
/*  171:     */   }
/*  172:     */   
/*  173:     */   private void initPopupMenus()
/*  174:     */   {
/*  175: 243 */     this.m_PopupHeader = new JPopupMenu();
/*  176: 244 */     this.m_PopupHeader.addMouseListener(this);
/*  177: 245 */     this.m_PopupHeader.add(this.menuItemMean);
/*  178: 246 */     if (!isReadOnly())
/*  179:     */     {
/*  180: 247 */       this.m_PopupHeader.addSeparator();
/*  181: 248 */       this.m_PopupHeader.add(this.menuItemSetAllValues);
/*  182: 249 */       this.m_PopupHeader.add(this.menuItemSetMissingValues);
/*  183: 250 */       this.m_PopupHeader.add(this.menuItemReplaceValues);
/*  184: 251 */       this.m_PopupHeader.addSeparator();
/*  185: 252 */       this.m_PopupHeader.add(this.menuItemRenameAttribute);
/*  186: 253 */       this.m_PopupHeader.add(this.menuItemAttributeAsClass);
/*  187: 254 */       this.m_PopupHeader.add(this.menuItemDeleteAttribute);
/*  188: 255 */       this.m_PopupHeader.add(this.menuItemDeleteAttributes);
/*  189: 256 */       this.m_PopupHeader.add(this.menuItemSortInstances);
/*  190:     */     }
/*  191: 258 */     this.m_PopupHeader.addSeparator();
/*  192: 259 */     this.m_PopupHeader.add(this.menuItemOptimalColWidth);
/*  193: 260 */     this.m_PopupHeader.add(this.menuItemOptimalColWidths);
/*  194:     */     
/*  195:     */ 
/*  196: 263 */     this.m_PopupRows = new JPopupMenu();
/*  197: 264 */     this.m_PopupRows.addMouseListener(this);
/*  198: 265 */     if (!isReadOnly())
/*  199:     */     {
/*  200: 266 */       this.m_PopupRows.add(this.menuItemUndo);
/*  201: 267 */       this.m_PopupRows.addSeparator();
/*  202:     */     }
/*  203: 269 */     this.m_PopupRows.add(this.menuItemCopy);
/*  204: 270 */     this.m_PopupRows.addSeparator();
/*  205: 271 */     this.m_PopupRows.add(this.menuItemSearch);
/*  206: 272 */     this.m_PopupRows.add(this.menuItemClearSearch);
/*  207: 273 */     if (!isReadOnly())
/*  208:     */     {
/*  209: 274 */       this.m_PopupRows.addSeparator();
/*  210: 275 */       this.m_PopupRows.add(this.menuItemDeleteSelectedInstance);
/*  211: 276 */       this.m_PopupRows.add(this.menuItemDeleteAllSelectedInstances);
/*  212: 277 */       this.m_PopupRows.add(this.menuItemInsertInstance);
/*  213:     */     }
/*  214:     */   }
/*  215:     */   
/*  216:     */   private void setMenu()
/*  217:     */   {
/*  218: 292 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  219: 293 */     boolean isNull = model.getInstances() == null;
/*  220: 294 */     boolean hasColumns = (!isNull) && (model.getInstances().numAttributes() > 0);
/*  221: 295 */     boolean hasRows = (!isNull) && (model.getInstances().numInstances() > 0);
/*  222: 296 */     boolean attSelected = (hasColumns) && (this.m_CurrentCol > 0);
/*  223: 297 */     boolean isNumeric = (attSelected) && (model.getAttributeAt(this.m_CurrentCol).isNumeric());
/*  224:     */     
/*  225: 299 */     this.menuItemUndo.setEnabled(canUndo());
/*  226: 300 */     this.menuItemCopy.setEnabled(true);
/*  227: 301 */     this.menuItemSearch.setEnabled(true);
/*  228: 302 */     this.menuItemClearSearch.setEnabled(true);
/*  229: 303 */     this.menuItemMean.setEnabled(isNumeric);
/*  230: 304 */     this.menuItemSetAllValues.setEnabled(attSelected);
/*  231: 305 */     this.menuItemSetMissingValues.setEnabled(attSelected);
/*  232: 306 */     this.menuItemReplaceValues.setEnabled(attSelected);
/*  233: 307 */     this.menuItemRenameAttribute.setEnabled(attSelected);
/*  234: 308 */     this.menuItemDeleteAttribute.setEnabled(attSelected);
/*  235: 309 */     this.menuItemDeleteAttributes.setEnabled(attSelected);
/*  236: 310 */     this.menuItemSortInstances.setEnabled((hasRows) && (attSelected));
/*  237: 311 */     this.menuItemDeleteSelectedInstance.setEnabled((hasRows) && (this.m_TableArff.getSelectedRow() > -1));
/*  238:     */     
/*  239: 313 */     this.menuItemDeleteAllSelectedInstances.setEnabled((hasRows) && (this.m_TableArff.getSelectedRows().length > 0));
/*  240:     */   }
/*  241:     */   
/*  242:     */   public ArffTable getTable()
/*  243:     */   {
/*  244: 323 */     return this.m_TableArff;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public String getTitle()
/*  248:     */   {
/*  249: 332 */     return this.m_Title;
/*  250:     */   }
/*  251:     */   
/*  252:     */   public String getFilename()
/*  253:     */   {
/*  254: 341 */     return this.m_Filename;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public void setFilename(String filename)
/*  258:     */   {
/*  259: 350 */     this.m_Filename = filename;
/*  260: 351 */     createTitle();
/*  261:     */   }
/*  262:     */   
/*  263:     */   public Instances getInstances()
/*  264:     */   {
/*  265: 362 */     Instances result = null;
/*  266: 364 */     if (this.m_TableArff.getModel() != null) {
/*  267: 365 */       result = ((ArffSortedTableModel)this.m_TableArff.getModel()).getInstances();
/*  268:     */     }
/*  269: 368 */     return result;
/*  270:     */   }
/*  271:     */   
/*  272:     */   public void setInstances(Instances data)
/*  273:     */   {
/*  274: 384 */     this.m_Filename = "Instances";
/*  275:     */     
/*  276: 386 */     createTitle();
/*  277: 387 */     ArffSortedTableModel model = new ArffSortedTableModel(data);
/*  278: 388 */     model.setShowAttributeIndex(this.m_ShowAttributeIndex);
/*  279:     */     
/*  280: 390 */     this.m_TableArff.setModel(model);
/*  281: 391 */     clearUndo();
/*  282: 392 */     setChanged(false);
/*  283: 393 */     createName();
/*  284:     */   }
/*  285:     */   
/*  286:     */   public Vector<String> getAttributes()
/*  287:     */   {
/*  288: 405 */     Vector<String> result = new Vector();
/*  289: 406 */     for (int i = 0; i < getInstances().numAttributes(); i++) {
/*  290: 407 */       result.add(getInstances().attribute(i).name());
/*  291:     */     }
/*  292: 409 */     Collections.sort(result);
/*  293:     */     
/*  294: 411 */     return result;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public void setChanged(boolean changed)
/*  298:     */   {
/*  299: 420 */     if (!changed)
/*  300:     */     {
/*  301: 421 */       this.m_Changed = changed;
/*  302: 422 */       createTitle();
/*  303:     */     }
/*  304:     */   }
/*  305:     */   
/*  306:     */   public boolean isChanged()
/*  307:     */   {
/*  308: 432 */     return this.m_Changed;
/*  309:     */   }
/*  310:     */   
/*  311:     */   public boolean isReadOnly()
/*  312:     */   {
/*  313: 441 */     if (this.m_TableArff == null) {
/*  314: 442 */       return true;
/*  315:     */     }
/*  316: 444 */     return ((ArffSortedTableModel)this.m_TableArff.getModel()).isReadOnly();
/*  317:     */   }
/*  318:     */   
/*  319:     */   public void setReadOnly(boolean value)
/*  320:     */   {
/*  321: 454 */     if (this.m_TableArff != null) {
/*  322: 455 */       ((ArffSortedTableModel)this.m_TableArff.getModel()).setReadOnly(value);
/*  323:     */     }
/*  324:     */   }
/*  325:     */   
/*  326:     */   public void setShowAttributeIndex(boolean value)
/*  327:     */   {
/*  328: 466 */     this.m_ShowAttributeIndex = value;
/*  329: 467 */     if (this.m_TableArff != null) {
/*  330: 468 */       ((ArffSortedTableModel)this.m_TableArff.getModel()).setShowAttributeIndex(value);
/*  331:     */     }
/*  332:     */   }
/*  333:     */   
/*  334:     */   public boolean getShowAttributeIndex()
/*  335:     */   {
/*  336: 479 */     return this.m_ShowAttributeIndex;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public boolean isUndoEnabled()
/*  340:     */   {
/*  341: 489 */     return ((ArffSortedTableModel)this.m_TableArff.getModel()).isUndoEnabled();
/*  342:     */   }
/*  343:     */   
/*  344:     */   public void setUndoEnabled(boolean enabled)
/*  345:     */   {
/*  346: 499 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).setUndoEnabled(enabled);
/*  347:     */   }
/*  348:     */   
/*  349:     */   public void clearUndo()
/*  350:     */   {
/*  351: 507 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).clearUndo();
/*  352:     */   }
/*  353:     */   
/*  354:     */   public boolean canUndo()
/*  355:     */   {
/*  356: 517 */     return ((ArffSortedTableModel)this.m_TableArff.getModel()).canUndo();
/*  357:     */   }
/*  358:     */   
/*  359:     */   public void undo()
/*  360:     */   {
/*  361: 525 */     if (canUndo())
/*  362:     */     {
/*  363: 526 */       ((ArffSortedTableModel)this.m_TableArff.getModel()).undo();
/*  364:     */       
/*  365:     */ 
/*  366: 529 */       notifyListener();
/*  367:     */     }
/*  368:     */   }
/*  369:     */   
/*  370:     */   public void addUndoPoint()
/*  371:     */   {
/*  372: 538 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).addUndoPoint();
/*  373:     */     
/*  374:     */ 
/*  375: 541 */     setMenu();
/*  376:     */   }
/*  377:     */   
/*  378:     */   private void createTitle()
/*  379:     */   {
/*  380: 550 */     if (this.m_Filename.equals("")) {
/*  381: 551 */       this.m_Title = "-none-";
/*  382: 552 */     } else if (this.m_Filename.equals("Instances")) {
/*  383: 553 */       this.m_Title = "Instances";
/*  384:     */     } else {
/*  385:     */       try
/*  386:     */       {
/*  387: 556 */         File file = new File(this.m_Filename);
/*  388: 557 */         this.m_Title = file.getName();
/*  389:     */       }
/*  390:     */       catch (Exception e)
/*  391:     */       {
/*  392: 559 */         this.m_Title = "-none-";
/*  393:     */       }
/*  394:     */     }
/*  395: 563 */     if (isChanged()) {
/*  396: 564 */       this.m_Title += " *";
/*  397:     */     }
/*  398:     */   }
/*  399:     */   
/*  400:     */   private void createName()
/*  401:     */   {
/*  402: 574 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  403: 575 */     if ((model != null) && (model.getInstances() != null)) {
/*  404: 576 */       this.m_LabelName.setText("Relation: " + model.getInstances().relationName());
/*  405:     */     } else {
/*  406: 578 */       this.m_LabelName.setText("");
/*  407:     */     }
/*  408:     */   }
/*  409:     */   
/*  410:     */   private void loadFile(String filename, AbstractFileLoader... loaders)
/*  411:     */   {
/*  412: 591 */     this.m_Filename = filename;
/*  413:     */     
/*  414: 593 */     createTitle();
/*  415:     */     ArffSortedTableModel model;
/*  416:     */     ArffSortedTableModel model;
/*  417: 595 */     if (filename.equals(""))
/*  418:     */     {
/*  419: 596 */       model = null;
/*  420:     */     }
/*  421:     */     else
/*  422:     */     {
/*  423: 598 */       model = new ArffSortedTableModel(filename, loaders);
/*  424: 599 */       model.setShowAttributeIndex(getShowAttributeIndex());
/*  425:     */     }
/*  426: 602 */     this.m_TableArff.setModel(model);
/*  427: 603 */     setChanged(false);
/*  428: 604 */     createName();
/*  429:     */   }
/*  430:     */   
/*  431:     */   private void calcMean()
/*  432:     */   {
/*  433: 616 */     if (this.m_CurrentCol == -1) {
/*  434: 617 */       return;
/*  435:     */     }
/*  436: 620 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  437: 623 */     if (!model.getAttributeAt(this.m_CurrentCol).isNumeric()) {
/*  438: 624 */       return;
/*  439:     */     }
/*  440: 627 */     double mean = 0.0D;
/*  441: 628 */     for (int i = 0; i < model.getRowCount(); i++) {
/*  442: 629 */       mean += model.getInstances().instance(i).value(this.m_CurrentCol - 1);
/*  443:     */     }
/*  444: 631 */     mean /= model.getRowCount();
/*  445:     */     
/*  446:     */ 
/*  447: 634 */     ComponentHelper.showMessageBox(getParent(), "Mean for attribute...", "Mean for attribute '" + this.m_TableArff.getPlainColumnName(this.m_CurrentCol) + "':\n\t" + Utils.doubleToString(mean, 3), 2, -1);
/*  448:     */   }
/*  449:     */   
/*  450:     */   private void setValues(Object o)
/*  451:     */   {
/*  452: 653 */     String value = "";
/*  453: 654 */     String valueNew = "";
/*  454:     */     String msg;
/*  455: 656 */     if (o == this.menuItemSetMissingValues)
/*  456:     */     {
/*  457: 657 */       String title = "Replace missing values...";
/*  458: 658 */       msg = "New value for MISSING values";
/*  459:     */     }
/*  460:     */     else
/*  461:     */     {
/*  462:     */       String msg;
/*  463: 659 */       if (o == this.menuItemSetAllValues)
/*  464:     */       {
/*  465: 660 */         String title = "Set all values...";
/*  466: 661 */         msg = "New value for ALL values";
/*  467:     */       }
/*  468:     */       else
/*  469:     */       {
/*  470:     */         String msg;
/*  471: 662 */         if (o == this.menuItemReplaceValues)
/*  472:     */         {
/*  473: 663 */           String title = "Replace values...";
/*  474: 664 */           msg = "Old value";
/*  475:     */         }
/*  476:     */         else
/*  477:     */         {
/*  478:     */           return;
/*  479:     */         }
/*  480:     */       }
/*  481:     */     }
/*  482:     */     String title;
/*  483:     */     String msg;
/*  484: 669 */     value = ComponentHelper.showInputBox(this.m_TableArff.getParent(), title, msg, this.m_LastSearch);
/*  485: 674 */     if (value == null) {
/*  486: 675 */       return;
/*  487:     */     }
/*  488: 678 */     this.m_LastSearch = value;
/*  489: 681 */     if (o == this.menuItemReplaceValues)
/*  490:     */     {
/*  491: 682 */       valueNew = ComponentHelper.showInputBox(this.m_TableArff.getParent(), title, "New value", this.m_LastReplace);
/*  492: 685 */       if (valueNew == null) {
/*  493: 686 */         return;
/*  494:     */       }
/*  495: 688 */       this.m_LastReplace = valueNew;
/*  496:     */     }
/*  497: 691 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  498: 692 */     model.setNotificationEnabled(false);
/*  499:     */     
/*  500:     */ 
/*  501: 695 */     addUndoPoint();
/*  502: 696 */     model.setUndoEnabled(false);
/*  503: 697 */     String valueCopy = value;
/*  504: 698 */     String valueNewCopy = valueNew;
/*  505: 700 */     for (int i = 0; i < this.m_TableArff.getRowCount(); i++) {
/*  506: 701 */       if (o == this.menuItemSetAllValues)
/*  507:     */       {
/*  508: 702 */         if ((valueCopy.equals("NaN")) || (valueCopy.equals("?"))) {
/*  509: 703 */           value = null;
/*  510:     */         }
/*  511: 705 */         model.setValueAt(value, i, this.m_CurrentCol);
/*  512:     */       }
/*  513: 706 */       else if ((o == this.menuItemSetMissingValues) && (model.isMissingAt(i, this.m_CurrentCol)))
/*  514:     */       {
/*  515: 708 */         model.setValueAt(value, i, this.m_CurrentCol);
/*  516:     */       }
/*  517: 709 */       else if ((o == this.menuItemReplaceValues) && (model.getValueAt(i, this.m_CurrentCol).toString().equals(value)))
/*  518:     */       {
/*  519: 711 */         if ((valueNewCopy.equals("NaN")) || (valueNewCopy.equals("?"))) {
/*  520: 712 */           valueNew = null;
/*  521:     */         }
/*  522: 714 */         model.setValueAt(valueNew, i, this.m_CurrentCol);
/*  523:     */       }
/*  524:     */     }
/*  525: 718 */     model.setUndoEnabled(true);
/*  526: 719 */     model.setNotificationEnabled(true);
/*  527: 720 */     model.notifyListener(new TableModelEvent(model, 0, model.getRowCount(), this.m_CurrentCol, 0));
/*  528:     */     
/*  529:     */ 
/*  530:     */ 
/*  531: 724 */     this.m_TableArff.repaint();
/*  532:     */   }
/*  533:     */   
/*  534:     */   public void deleteAttribute()
/*  535:     */   {
/*  536: 734 */     if (this.m_CurrentCol == -1) {
/*  537: 735 */       return;
/*  538:     */     }
/*  539: 738 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  540: 741 */     if (model.getAttributeAt(this.m_CurrentCol) == null) {
/*  541: 742 */       return;
/*  542:     */     }
/*  543: 746 */     if (ComponentHelper.showMessageBox(getParent(), "Confirm...", "Do you really want to delete the attribute '" + model.getAttributeAt(this.m_CurrentCol).name() + "'?", 0, 3) != 0) {
/*  544: 752 */       return;
/*  545:     */     }
/*  546: 755 */     setCursor(Cursor.getPredefinedCursor(3));
/*  547: 756 */     model.deleteAttributeAt(this.m_CurrentCol);
/*  548: 757 */     setCursor(Cursor.getPredefinedCursor(0));
/*  549:     */   }
/*  550:     */   
/*  551:     */   public void deleteAttributes()
/*  552:     */   {
/*  553: 772 */     JList list = new JList(getAttributes());
/*  554: 773 */     ListSelectorDialog dialog = new ListSelectorDialog(null, list);
/*  555: 774 */     int result = dialog.showDialog();
/*  556: 776 */     if (result != 0) {
/*  557: 777 */       return;
/*  558:     */     }
/*  559: 780 */     Object[] atts = list.getSelectedValues();
/*  560: 783 */     if (ComponentHelper.showMessageBox(getParent(), "Confirm...", "Do you really want to delete these " + atts.length + " attributes?", 0, 3) != 0) {
/*  561: 786 */       return;
/*  562:     */     }
/*  563: 789 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  564: 790 */     int[] indices = new int[atts.length];
/*  565: 791 */     for (int i = 0; i < atts.length; i++) {
/*  566: 792 */       indices[i] = model.getAttributeColumn(atts[i].toString());
/*  567:     */     }
/*  568: 795 */     setCursor(Cursor.getPredefinedCursor(3));
/*  569: 796 */     model.deleteAttributes(indices);
/*  570: 797 */     setCursor(Cursor.getPredefinedCursor(0));
/*  571:     */   }
/*  572:     */   
/*  573:     */   public void attributeAsClass()
/*  574:     */   {
/*  575: 808 */     if (this.m_CurrentCol == -1) {
/*  576: 809 */       return;
/*  577:     */     }
/*  578: 812 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  579: 815 */     if (model.getAttributeAt(this.m_CurrentCol) == null) {
/*  580: 816 */       return;
/*  581:     */     }
/*  582: 819 */     setCursor(Cursor.getPredefinedCursor(3));
/*  583: 820 */     model.attributeAsClassAt(this.m_CurrentCol);
/*  584: 821 */     setCursor(Cursor.getPredefinedCursor(0));
/*  585:     */   }
/*  586:     */   
/*  587:     */   public void renameAttribute()
/*  588:     */   {
/*  589: 832 */     if (this.m_CurrentCol == -1) {
/*  590: 833 */       return;
/*  591:     */     }
/*  592: 836 */     ArffSortedTableModel model = (ArffSortedTableModel)this.m_TableArff.getModel();
/*  593: 839 */     if (model.getAttributeAt(this.m_CurrentCol) == null) {
/*  594: 840 */       return;
/*  595:     */     }
/*  596: 843 */     String newName = ComponentHelper.showInputBox(getParent(), "Rename attribute...", "Enter new Attribute name", model.getAttributeAt(this.m_CurrentCol).name());
/*  597: 846 */     if (newName == null) {
/*  598: 847 */       return;
/*  599:     */     }
/*  600: 850 */     setCursor(Cursor.getPredefinedCursor(3));
/*  601: 851 */     model.renameAttributeAt(this.m_CurrentCol, newName);
/*  602: 852 */     setCursor(Cursor.getPredefinedCursor(0));
/*  603:     */   }
/*  604:     */   
/*  605:     */   public void deleteInstance()
/*  606:     */   {
/*  607: 861 */     int index = this.m_TableArff.getSelectedRow();
/*  608: 862 */     if (index == -1) {
/*  609: 863 */       return;
/*  610:     */     }
/*  611: 866 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).deleteInstanceAt(index);
/*  612:     */   }
/*  613:     */   
/*  614:     */   public void addInstance()
/*  615:     */   {
/*  616: 874 */     int index = this.m_TableArff.getSelectedRow();
/*  617: 875 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).insertInstance(index);
/*  618:     */   }
/*  619:     */   
/*  620:     */   public void addInstanceAtEnd()
/*  621:     */   {
/*  622: 882 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).insertInstance(-1);
/*  623:     */   }
/*  624:     */   
/*  625:     */   public void deleteInstances()
/*  626:     */   {
/*  627: 891 */     if (this.m_TableArff.getSelectedRow() == -1) {
/*  628: 892 */       return;
/*  629:     */     }
/*  630: 895 */     int[] indices = this.m_TableArff.getSelectedRows();
/*  631: 896 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).deleteInstances(indices);
/*  632:     */   }
/*  633:     */   
/*  634:     */   public void sortInstances()
/*  635:     */   {
/*  636: 903 */     if (this.m_CurrentCol == -1) {
/*  637: 904 */       return;
/*  638:     */     }
/*  639: 907 */     ((ArffSortedTableModel)this.m_TableArff.getModel()).sortInstances(this.m_CurrentCol);
/*  640:     */   }
/*  641:     */   
/*  642:     */   public void copyContent()
/*  643:     */   {
/*  644: 917 */     StringSelection selection = getTable().getStringSelection();
/*  645: 918 */     if (selection == null) {
/*  646: 919 */       return;
/*  647:     */     }
/*  648: 922 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/*  649: 923 */     clipboard.setContents(selection, selection);
/*  650:     */   }
/*  651:     */   
/*  652:     */   public void search()
/*  653:     */   {
/*  654: 933 */     String searchString = ComponentHelper.showInputBox(getParent(), "Search...", "Enter the string to search for", this.m_LastSearch);
/*  655: 936 */     if (searchString != null) {
/*  656: 937 */       this.m_LastSearch = searchString;
/*  657:     */     }
/*  658: 940 */     getTable().setSearchString(searchString);
/*  659:     */   }
/*  660:     */   
/*  661:     */   public void clearSearch()
/*  662:     */   {
/*  663: 947 */     getTable().setSearchString("");
/*  664:     */   }
/*  665:     */   
/*  666:     */   public void setOptimalColWidth()
/*  667:     */   {
/*  668: 955 */     if (this.m_CurrentCol == -1) {
/*  669: 956 */       return;
/*  670:     */     }
/*  671: 959 */     JTableHelper.setOptimalColumnWidth(getTable(), this.m_CurrentCol);
/*  672:     */   }
/*  673:     */   
/*  674:     */   public void setOptimalColWidths()
/*  675:     */   {
/*  676: 966 */     JTableHelper.setOptimalColumnWidth(getTable());
/*  677:     */   }
/*  678:     */   
/*  679:     */   public void actionPerformed(ActionEvent e)
/*  680:     */   {
/*  681: 978 */     Object o = e.getSource();
/*  682: 980 */     if (o == this.menuItemMean) {
/*  683: 981 */       calcMean();
/*  684: 982 */     } else if (o == this.menuItemSetAllValues) {
/*  685: 983 */       setValues(this.menuItemSetAllValues);
/*  686: 984 */     } else if (o == this.menuItemSetMissingValues) {
/*  687: 985 */       setValues(this.menuItemSetMissingValues);
/*  688: 986 */     } else if (o == this.menuItemReplaceValues) {
/*  689: 987 */       setValues(this.menuItemReplaceValues);
/*  690: 988 */     } else if (o == this.menuItemRenameAttribute) {
/*  691: 989 */       renameAttribute();
/*  692: 990 */     } else if (o == this.menuItemAttributeAsClass) {
/*  693: 991 */       attributeAsClass();
/*  694: 992 */     } else if (o == this.menuItemDeleteAttribute) {
/*  695: 993 */       deleteAttribute();
/*  696: 994 */     } else if (o == this.menuItemDeleteAttributes) {
/*  697: 995 */       deleteAttributes();
/*  698: 996 */     } else if (o == this.menuItemDeleteSelectedInstance) {
/*  699: 997 */       deleteInstance();
/*  700: 998 */     } else if (o == this.menuItemDeleteAllSelectedInstances) {
/*  701: 999 */       deleteInstances();
/*  702:1000 */     } else if (o == this.menuItemInsertInstance) {
/*  703:1001 */       addInstance();
/*  704:1002 */     } else if (o == this.menuItemSortInstances) {
/*  705:1003 */       sortInstances();
/*  706:1004 */     } else if (o == this.menuItemSearch) {
/*  707:1005 */       search();
/*  708:1006 */     } else if (o == this.menuItemClearSearch) {
/*  709:1007 */       clearSearch();
/*  710:1008 */     } else if (o == this.menuItemUndo) {
/*  711:1009 */       undo();
/*  712:1010 */     } else if (o == this.menuItemCopy) {
/*  713:1011 */       copyContent();
/*  714:1012 */     } else if (o == this.menuItemOptimalColWidth) {
/*  715:1013 */       setOptimalColWidth();
/*  716:1014 */     } else if (o == this.menuItemOptimalColWidths) {
/*  717:1015 */       setOptimalColWidths();
/*  718:     */     }
/*  719:     */   }
/*  720:     */   
/*  721:     */   public void mouseClicked(MouseEvent e)
/*  722:     */   {
/*  723:1029 */     int col = this.m_TableArff.columnAtPoint(e.getPoint());
/*  724:1030 */     boolean popup = ((e.getButton() == 3) && (e.getClickCount() == 1)) || ((e.getButton() == 1) && (e.getClickCount() == 1) && (e.isAltDown()) && (!e.isControlDown()) && (!e.isShiftDown()));
/*  725:     */     
/*  726:     */ 
/*  727:     */ 
/*  728:1034 */     popup = (popup) && (getInstances() != null);
/*  729:1036 */     if (e.getSource() == this.m_TableArff.getTableHeader())
/*  730:     */     {
/*  731:1037 */       this.m_CurrentCol = col;
/*  732:1040 */       if (popup)
/*  733:     */       {
/*  734:1041 */         e.consume();
/*  735:1042 */         setMenu();
/*  736:1043 */         initPopupMenus();
/*  737:1044 */         this.m_PopupHeader.show(e.getComponent(), e.getX(), e.getY());
/*  738:     */       }
/*  739:     */     }
/*  740:1046 */     else if (e.getSource() == this.m_TableArff)
/*  741:     */     {
/*  742:1048 */       if (popup)
/*  743:     */       {
/*  744:1049 */         e.consume();
/*  745:1050 */         setMenu();
/*  746:1051 */         initPopupMenus();
/*  747:1052 */         this.m_PopupRows.show(e.getComponent(), e.getX(), e.getY());
/*  748:     */       }
/*  749:     */     }
/*  750:1057 */     if ((e.getButton() == 1) && (e.getClickCount() == 1) && (!e.isAltDown()) && (col > -1)) {
/*  751:1059 */       this.m_TableArff.setSelectedColumn(col);
/*  752:     */     }
/*  753:     */   }
/*  754:     */   
/*  755:     */   public void mouseEntered(MouseEvent e) {}
/*  756:     */   
/*  757:     */   public void mouseExited(MouseEvent e) {}
/*  758:     */   
/*  759:     */   public void mousePressed(MouseEvent e) {}
/*  760:     */   
/*  761:     */   public void mouseReleased(MouseEvent e) {}
/*  762:     */   
/*  763:     */   public void stateChanged(ChangeEvent e)
/*  764:     */   {
/*  765:1106 */     this.m_Changed = true;
/*  766:1107 */     createTitle();
/*  767:1108 */     notifyListener();
/*  768:     */   }
/*  769:     */   
/*  770:     */   public void notifyListener()
/*  771:     */   {
/*  772:1117 */     Iterator<ChangeListener> iter = this.m_ChangeListeners.iterator();
/*  773:1118 */     while (iter.hasNext()) {
/*  774:1119 */       ((ChangeListener)iter.next()).stateChanged(new ChangeEvent(this));
/*  775:     */     }
/*  776:     */   }
/*  777:     */   
/*  778:     */   public void addChangeListener(ChangeListener l)
/*  779:     */   {
/*  780:1129 */     this.m_ChangeListeners.add(l);
/*  781:     */   }
/*  782:     */   
/*  783:     */   public void removeChangeListener(ChangeListener l)
/*  784:     */   {
/*  785:1138 */     this.m_ChangeListeners.remove(l);
/*  786:     */   }
/*  787:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffPanel
 * JD-Core Version:    0.7.0.1
 */