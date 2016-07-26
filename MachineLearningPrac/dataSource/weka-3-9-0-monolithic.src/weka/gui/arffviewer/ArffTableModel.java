/*    1:     */ package weka.gui.arffviewer;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedOutputStream;
/*    5:     */ import java.io.File;
/*    6:     */ import java.io.FileInputStream;
/*    7:     */ import java.io.FileOutputStream;
/*    8:     */ import java.io.ObjectInputStream;
/*    9:     */ import java.io.ObjectOutputStream;
/*   10:     */ import java.io.PrintStream;
/*   11:     */ import java.util.Arrays;
/*   12:     */ import java.util.HashSet;
/*   13:     */ import java.util.Hashtable;
/*   14:     */ import java.util.Iterator;
/*   15:     */ import java.util.Vector;
/*   16:     */ import javax.swing.event.TableModelEvent;
/*   17:     */ import javax.swing.event.TableModelListener;
/*   18:     */ import javax.swing.table.DefaultTableModel;
/*   19:     */ import weka.core.Attribute;
/*   20:     */ import weka.core.DenseInstance;
/*   21:     */ import weka.core.Instance;
/*   22:     */ import weka.core.Instances;
/*   23:     */ import weka.core.Undoable;
/*   24:     */ import weka.core.Utils;
/*   25:     */ import weka.core.converters.AbstractFileLoader;
/*   26:     */ import weka.core.converters.ConverterUtils;
/*   27:     */ import weka.filters.Filter;
/*   28:     */ import weka.filters.unsupervised.attribute.Reorder;
/*   29:     */ import weka.gui.ComponentHelper;
/*   30:     */ 
/*   31:     */ public class ArffTableModel
/*   32:     */   extends DefaultTableModel
/*   33:     */   implements Undoable
/*   34:     */ {
/*   35:     */   private static final long serialVersionUID = 3411795562305994946L;
/*   36:     */   protected HashSet<TableModelListener> m_Listeners;
/*   37:     */   protected Instances m_Data;
/*   38:     */   protected boolean m_NotificationEnabled;
/*   39:     */   protected boolean m_UndoEnabled;
/*   40:     */   protected boolean m_IgnoreChanges;
/*   41:     */   protected Vector<File> m_UndoList;
/*   42:     */   protected boolean m_ReadOnly;
/*   43:     */   protected boolean m_ShowAttributeIndex;
/*   44:     */   protected Hashtable<String, String> m_Cache;
/*   45:     */   
/*   46:     */   private ArffTableModel()
/*   47:     */   {
/*   48: 101 */     this.m_Listeners = new HashSet();
/*   49: 102 */     this.m_Data = null;
/*   50: 103 */     this.m_NotificationEnabled = true;
/*   51: 104 */     this.m_UndoList = new Vector();
/*   52: 105 */     this.m_IgnoreChanges = false;
/*   53: 106 */     this.m_UndoEnabled = true;
/*   54: 107 */     this.m_ReadOnly = false;
/*   55: 108 */     this.m_ShowAttributeIndex = false;
/*   56: 109 */     this.m_Cache = new Hashtable();
/*   57:     */   }
/*   58:     */   
/*   59:     */   public ArffTableModel(String filename, AbstractFileLoader... loaders)
/*   60:     */   {
/*   61: 119 */     this();
/*   62: 121 */     if ((filename != null) && (!filename.equals(""))) {
/*   63: 122 */       loadFile(filename, loaders);
/*   64:     */     }
/*   65:     */   }
/*   66:     */   
/*   67:     */   public ArffTableModel(Instances data)
/*   68:     */   {
/*   69: 132 */     this();
/*   70:     */     
/*   71: 134 */     this.m_Data = data;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public boolean isNotificationEnabled()
/*   75:     */   {
/*   76: 143 */     return this.m_NotificationEnabled;
/*   77:     */   }
/*   78:     */   
/*   79:     */   public void setNotificationEnabled(boolean enabled)
/*   80:     */   {
/*   81: 152 */     this.m_NotificationEnabled = enabled;
/*   82:     */   }
/*   83:     */   
/*   84:     */   public boolean isUndoEnabled()
/*   85:     */   {
/*   86: 162 */     return this.m_UndoEnabled;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public void setUndoEnabled(boolean enabled)
/*   90:     */   {
/*   91: 172 */     this.m_UndoEnabled = enabled;
/*   92:     */   }
/*   93:     */   
/*   94:     */   public boolean isReadOnly()
/*   95:     */   {
/*   96: 181 */     return this.m_ReadOnly;
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void setReadOnly(boolean value)
/*  100:     */   {
/*  101: 190 */     this.m_ReadOnly = value;
/*  102:     */   }
/*  103:     */   
/*  104:     */   protected void loadFile(String filename, AbstractFileLoader... loaders)
/*  105:     */   {
/*  106:     */     AbstractFileLoader loader;
/*  107:     */     AbstractFileLoader loader;
/*  108: 202 */     if ((loaders == null) || (loaders.length == 0)) {
/*  109: 203 */       loader = ConverterUtils.getLoaderForFile(filename);
/*  110:     */     } else {
/*  111: 205 */       loader = loaders[0];
/*  112:     */     }
/*  113: 208 */     if (loader != null) {
/*  114:     */       try
/*  115:     */       {
/*  116: 210 */         loader.setFile(new File(filename));
/*  117: 211 */         setInstances(loader.getDataSet());
/*  118:     */       }
/*  119:     */       catch (Exception e)
/*  120:     */       {
/*  121: 213 */         ComponentHelper.showMessageBox(null, "Error loading file...", e.toString(), 2, 0);
/*  122:     */         
/*  123:     */ 
/*  124: 216 */         System.out.println(e);
/*  125: 217 */         setInstances(null);
/*  126:     */       }
/*  127:     */     }
/*  128:     */   }
/*  129:     */   
/*  130:     */   public void setInstances(Instances data)
/*  131:     */   {
/*  132: 228 */     this.m_Data = data;
/*  133: 229 */     this.m_Cache.clear();
/*  134: 230 */     fireTableDataChanged();
/*  135:     */   }
/*  136:     */   
/*  137:     */   public Instances getInstances()
/*  138:     */   {
/*  139: 239 */     return this.m_Data;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public Attribute getAttributeAt(int columnIndex)
/*  143:     */   {
/*  144: 250 */     if ((columnIndex > 0) && (columnIndex < getColumnCount())) {
/*  145: 251 */       return this.m_Data.attribute(columnIndex - 1);
/*  146:     */     }
/*  147: 253 */     return null;
/*  148:     */   }
/*  149:     */   
/*  150:     */   public int getType(int columnIndex)
/*  151:     */   {
/*  152: 264 */     return getType(-1, columnIndex);
/*  153:     */   }
/*  154:     */   
/*  155:     */   public int getType(int rowIndex, int columnIndex)
/*  156:     */   {
/*  157: 277 */     int result = 2;
/*  158: 279 */     if ((rowIndex < 0) && (columnIndex > 0) && (columnIndex < getColumnCount())) {
/*  159: 280 */       result = this.m_Data.attribute(columnIndex - 1).type();
/*  160: 281 */     } else if ((rowIndex >= 0) && (rowIndex < getRowCount()) && (columnIndex > 0) && (columnIndex < getColumnCount())) {
/*  161: 283 */       result = this.m_Data.instance(rowIndex).attribute(columnIndex - 1).type();
/*  162:     */     }
/*  163: 286 */     return result;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public void deleteAttributeAt(int columnIndex)
/*  167:     */   {
/*  168: 295 */     deleteAttributeAt(columnIndex, true);
/*  169:     */   }
/*  170:     */   
/*  171:     */   public void deleteAttributeAt(int columnIndex, boolean notify)
/*  172:     */   {
/*  173: 305 */     if ((columnIndex > 0) && (columnIndex < getColumnCount()))
/*  174:     */     {
/*  175: 306 */       if (!this.m_IgnoreChanges) {
/*  176: 307 */         addUndoPoint();
/*  177:     */       }
/*  178: 309 */       this.m_Data.deleteAttributeAt(columnIndex - 1);
/*  179: 310 */       if (notify) {
/*  180: 311 */         notifyListener(new TableModelEvent(this, -1));
/*  181:     */       }
/*  182:     */     }
/*  183:     */   }
/*  184:     */   
/*  185:     */   public void deleteAttributes(int[] columnIndices)
/*  186:     */   {
/*  187: 324 */     Arrays.sort(columnIndices);
/*  188:     */     
/*  189: 326 */     addUndoPoint();
/*  190:     */     
/*  191: 328 */     this.m_IgnoreChanges = true;
/*  192: 329 */     for (int i = columnIndices.length - 1; i >= 0; i--) {
/*  193: 330 */       deleteAttributeAt(columnIndices[i], false);
/*  194:     */     }
/*  195: 332 */     this.m_IgnoreChanges = false;
/*  196:     */     
/*  197: 334 */     notifyListener(new TableModelEvent(this, -1));
/*  198:     */   }
/*  199:     */   
/*  200:     */   public void renameAttributeAt(int columnIndex, String newName)
/*  201:     */   {
/*  202: 344 */     if ((columnIndex > 0) && (columnIndex < getColumnCount()))
/*  203:     */     {
/*  204: 345 */       addUndoPoint();
/*  205: 346 */       this.m_Data.renameAttribute(columnIndex - 1, newName);
/*  206: 347 */       notifyListener(new TableModelEvent(this, -1));
/*  207:     */     }
/*  208:     */   }
/*  209:     */   
/*  210:     */   public void attributeAsClassAt(int columnIndex)
/*  211:     */   {
/*  212: 362 */     if ((columnIndex > 0) && (columnIndex < getColumnCount()))
/*  213:     */     {
/*  214: 363 */       addUndoPoint();
/*  215:     */       try
/*  216:     */       {
/*  217: 367 */         String order = "";
/*  218: 368 */         for (int i = 1; i < this.m_Data.numAttributes() + 1; i++) {
/*  219: 370 */           if (i != columnIndex)
/*  220:     */           {
/*  221: 374 */             if (!order.equals("")) {
/*  222: 375 */               order = order + ",";
/*  223:     */             }
/*  224: 377 */             order = order + Integer.toString(i);
/*  225:     */           }
/*  226:     */         }
/*  227: 379 */         if (!order.equals("")) {
/*  228: 380 */           order = order + ",";
/*  229:     */         }
/*  230: 382 */         order = order + Integer.toString(columnIndex);
/*  231:     */         
/*  232:     */ 
/*  233: 385 */         Reorder reorder = new Reorder();
/*  234: 386 */         reorder.setAttributeIndices(order);
/*  235: 387 */         reorder.setInputFormat(this.m_Data);
/*  236: 388 */         this.m_Data = Filter.useFilter(this.m_Data, reorder);
/*  237:     */         
/*  238:     */ 
/*  239: 391 */         this.m_Data.setClassIndex(this.m_Data.numAttributes() - 1);
/*  240:     */       }
/*  241:     */       catch (Exception e)
/*  242:     */       {
/*  243: 393 */         e.printStackTrace();
/*  244: 394 */         undo();
/*  245:     */       }
/*  246: 397 */       notifyListener(new TableModelEvent(this, -1));
/*  247:     */     }
/*  248:     */   }
/*  249:     */   
/*  250:     */   public void deleteInstanceAt(int rowIndex)
/*  251:     */   {
/*  252: 407 */     deleteInstanceAt(rowIndex, true);
/*  253:     */   }
/*  254:     */   
/*  255:     */   public void deleteInstanceAt(int rowIndex, boolean notify)
/*  256:     */   {
/*  257: 417 */     if ((rowIndex >= 0) && (rowIndex < getRowCount()))
/*  258:     */     {
/*  259: 418 */       if (!this.m_IgnoreChanges) {
/*  260: 419 */         addUndoPoint();
/*  261:     */       }
/*  262: 421 */       this.m_Data.delete(rowIndex);
/*  263: 422 */       if (notify) {
/*  264: 423 */         notifyListener(new TableModelEvent(this, rowIndex, rowIndex, -1, -1));
/*  265:     */       }
/*  266:     */     }
/*  267:     */   }
/*  268:     */   
/*  269:     */   public void insertInstance(int index)
/*  270:     */   {
/*  271: 430 */     insertInstance(index, true);
/*  272:     */   }
/*  273:     */   
/*  274:     */   public void insertInstance(int index, boolean notify)
/*  275:     */   {
/*  276: 434 */     if (!this.m_IgnoreChanges) {
/*  277: 435 */       addUndoPoint();
/*  278:     */     }
/*  279: 437 */     double[] vals = new double[this.m_Data.numAttributes()];
/*  280: 442 */     for (int i = 0; i < this.m_Data.numAttributes(); i++) {
/*  281: 443 */       if ((this.m_Data.attribute(i).isString()) || (this.m_Data.attribute(i).isRelationValued())) {
/*  282: 445 */         vals[i] = Utils.missingValue();
/*  283:     */       }
/*  284:     */     }
/*  285: 448 */     Instance toAdd = new DenseInstance(1.0D, vals);
/*  286: 449 */     if (index < 0) {
/*  287: 450 */       this.m_Data.add(toAdd);
/*  288:     */     } else {
/*  289: 452 */       this.m_Data.add(index, toAdd);
/*  290:     */     }
/*  291: 454 */     if (notify) {
/*  292: 455 */       notifyListener(new TableModelEvent(this, this.m_Data.numInstances() - 1, this.m_Data.numInstances() - 1, -1, 1));
/*  293:     */     }
/*  294:     */   }
/*  295:     */   
/*  296:     */   public void deleteInstances(int[] rowIndices)
/*  297:     */   {
/*  298: 469 */     Arrays.sort(rowIndices);
/*  299:     */     
/*  300: 471 */     addUndoPoint();
/*  301:     */     
/*  302: 473 */     this.m_IgnoreChanges = true;
/*  303: 474 */     for (int i = rowIndices.length - 1; i >= 0; i--) {
/*  304: 475 */       deleteInstanceAt(rowIndices[i], false);
/*  305:     */     }
/*  306: 477 */     this.m_IgnoreChanges = false;
/*  307:     */     
/*  308: 479 */     notifyListener(new TableModelEvent(this, rowIndices[0], rowIndices[(rowIndices.length - 1)], -1, -1));
/*  309:     */   }
/*  310:     */   
/*  311:     */   public void sortInstances(int columnIndex)
/*  312:     */   {
/*  313: 490 */     if ((columnIndex > 0) && (columnIndex < getColumnCount()))
/*  314:     */     {
/*  315: 491 */       addUndoPoint();
/*  316: 492 */       this.m_Data.stableSort(columnIndex - 1);
/*  317: 493 */       notifyListener(new TableModelEvent(this));
/*  318:     */     }
/*  319:     */   }
/*  320:     */   
/*  321:     */   public void sortInstances(int columnIndex, boolean ascending)
/*  322:     */   {
/*  323: 504 */     if ((columnIndex > 0) && (columnIndex < getColumnCount()))
/*  324:     */     {
/*  325: 505 */       addUndoPoint();
/*  326: 506 */       this.m_Data.stableSort(columnIndex - 1);
/*  327: 507 */       if (!ascending)
/*  328:     */       {
/*  329: 508 */         Instances reversedData = new Instances(this.m_Data, this.m_Data.numInstances());
/*  330: 509 */         int i = this.m_Data.numInstances();
/*  331: 510 */         while (i > 0)
/*  332:     */         {
/*  333: 511 */           i--;
/*  334: 512 */           int equalCount = 1;
/*  335: 514 */           while ((i > 0) && (this.m_Data.instance(i).value(columnIndex - 1) == this.m_Data.instance(i - 1).value(columnIndex - 1)))
/*  336:     */           {
/*  337: 516 */             equalCount++;
/*  338: 517 */             i--;
/*  339:     */           }
/*  340: 519 */           int j = 0;
/*  341: 520 */           while (j < equalCount)
/*  342:     */           {
/*  343: 521 */             reversedData.add(this.m_Data.instance(i + j));
/*  344: 522 */             j++;
/*  345:     */           }
/*  346:     */         }
/*  347: 525 */         this.m_Data = reversedData;
/*  348:     */       }
/*  349: 527 */       notifyListener(new TableModelEvent(this));
/*  350:     */     }
/*  351:     */   }
/*  352:     */   
/*  353:     */   public int getAttributeColumn(String name)
/*  354:     */   {
/*  355: 541 */     int result = -1;
/*  356: 543 */     for (int i = 0; i < this.m_Data.numAttributes(); i++) {
/*  357: 544 */       if (this.m_Data.attribute(i).name().equals(name))
/*  358:     */       {
/*  359: 545 */         result = i + 1;
/*  360: 546 */         break;
/*  361:     */       }
/*  362:     */     }
/*  363: 550 */     return result;
/*  364:     */   }
/*  365:     */   
/*  366:     */   public Class<?> getColumnClass(int columnIndex)
/*  367:     */   {
/*  368: 564 */     Class<?> result = null;
/*  369: 566 */     if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  370: 567 */       if (columnIndex == 0) {
/*  371: 568 */         result = Integer.class;
/*  372: 569 */       } else if (getType(columnIndex) == 0) {
/*  373: 570 */         result = Double.class;
/*  374:     */       } else {
/*  375: 572 */         result = String.class;
/*  376:     */       }
/*  377:     */     }
/*  378: 576 */     return result;
/*  379:     */   }
/*  380:     */   
/*  381:     */   public int getColumnCount()
/*  382:     */   {
/*  383: 588 */     int result = 1;
/*  384: 589 */     if (this.m_Data != null) {
/*  385: 590 */       result += this.m_Data.numAttributes();
/*  386:     */     }
/*  387: 593 */     return result;
/*  388:     */   }
/*  389:     */   
/*  390:     */   protected boolean isClassIndex(int columnIndex)
/*  391:     */   {
/*  392: 606 */     int index = this.m_Data.classIndex();
/*  393: 607 */     boolean result = ((index == -1) && (this.m_Data.numAttributes() == columnIndex)) || (index == columnIndex - 1);
/*  394:     */     
/*  395:     */ 
/*  396:     */ 
/*  397: 611 */     return result;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public String getColumnName(int columnIndex)
/*  401:     */   {
/*  402: 624 */     String result = "";
/*  403: 626 */     if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  404: 627 */       if (columnIndex == 0)
/*  405:     */       {
/*  406: 628 */         result = "<html><center>No.<br><font size=\"-2\">&nbsp;</font></center></html>";
/*  407:     */       }
/*  408: 631 */       else if ((this.m_Data != null) && 
/*  409: 632 */         (columnIndex - 1 < this.m_Data.numAttributes()))
/*  410:     */       {
/*  411: 633 */         result = "<html><center>";
/*  412: 636 */         if (this.m_ShowAttributeIndex) {
/*  413: 637 */           result = result + columnIndex + ": ";
/*  414:     */         }
/*  415: 641 */         if (isClassIndex(columnIndex)) {
/*  416: 642 */           result = result + "<b>" + this.m_Data.attribute(columnIndex - 1).name() + "</b>";
/*  417:     */         } else {
/*  418: 645 */           result = result + this.m_Data.attribute(columnIndex - 1).name();
/*  419:     */         }
/*  420: 649 */         switch (getType(columnIndex))
/*  421:     */         {
/*  422:     */         case 3: 
/*  423: 651 */           result = result + "<br><font size=\"-2\">Date</font>";
/*  424: 652 */           break;
/*  425:     */         case 1: 
/*  426: 654 */           result = result + "<br><font size=\"-2\">Nominal</font>";
/*  427: 655 */           break;
/*  428:     */         case 2: 
/*  429: 657 */           result = result + "<br><font size=\"-2\">String</font>";
/*  430: 658 */           break;
/*  431:     */         case 0: 
/*  432: 660 */           result = result + "<br><font size=\"-2\">Numeric</font>";
/*  433: 661 */           break;
/*  434:     */         case 4: 
/*  435: 663 */           result = result + "<br><font size=\"-2\">Relational</font>";
/*  436: 664 */           break;
/*  437:     */         default: 
/*  438: 666 */           result = result + "<br><font size=\"-2\">???</font>";
/*  439:     */         }
/*  440: 669 */         result = result + "</center></html>";
/*  441:     */       }
/*  442:     */     }
/*  443: 675 */     return result;
/*  444:     */   }
/*  445:     */   
/*  446:     */   public int getRowCount()
/*  447:     */   {
/*  448: 685 */     if (this.m_Data == null) {
/*  449: 686 */       return 0;
/*  450:     */     }
/*  451: 688 */     return this.m_Data.numInstances();
/*  452:     */   }
/*  453:     */   
/*  454:     */   public boolean isMissingAt(int rowIndex, int columnIndex)
/*  455:     */   {
/*  456: 702 */     boolean result = false;
/*  457: 704 */     if ((rowIndex >= 0) && (rowIndex < getRowCount()) && (columnIndex > 0) && (columnIndex < getColumnCount())) {
/*  458: 706 */       result = this.m_Data.instance(rowIndex).isMissing(columnIndex - 1);
/*  459:     */     }
/*  460: 709 */     return result;
/*  461:     */   }
/*  462:     */   
/*  463:     */   public double getInstancesValueAt(int rowIndex, int columnIndex)
/*  464:     */   {
/*  465: 723 */     double result = -1.0D;
/*  466: 725 */     if ((rowIndex >= 0) && (rowIndex < getRowCount()) && (columnIndex > 0) && (columnIndex < getColumnCount())) {
/*  467: 727 */       result = this.m_Data.instance(rowIndex).value(columnIndex - 1);
/*  468:     */     }
/*  469: 730 */     return result;
/*  470:     */   }
/*  471:     */   
/*  472:     */   public Object getValueAt(int rowIndex, int columnIndex)
/*  473:     */   {
/*  474: 747 */     Object result = null;
/*  475: 748 */     String key = rowIndex + "-" + columnIndex;
/*  476: 750 */     if ((rowIndex >= 0) && (rowIndex < getRowCount()) && (columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  477: 752 */       if (columnIndex == 0)
/*  478:     */       {
/*  479: 753 */         result = new Integer(rowIndex + 1);
/*  480:     */       }
/*  481: 755 */       else if (isMissingAt(rowIndex, columnIndex))
/*  482:     */       {
/*  483: 756 */         result = null;
/*  484:     */       }
/*  485: 758 */       else if (this.m_Cache.containsKey(key))
/*  486:     */       {
/*  487: 759 */         result = this.m_Cache.get(key);
/*  488:     */       }
/*  489:     */       else
/*  490:     */       {
/*  491: 761 */         switch (getType(columnIndex))
/*  492:     */         {
/*  493:     */         case 1: 
/*  494:     */         case 2: 
/*  495:     */         case 3: 
/*  496:     */         case 4: 
/*  497: 766 */           result = this.m_Data.instance(rowIndex).stringValue(columnIndex - 1);
/*  498: 767 */           break;
/*  499:     */         case 0: 
/*  500: 769 */           result = new Double(this.m_Data.instance(rowIndex).value(columnIndex - 1));
/*  501:     */           
/*  502: 771 */           break;
/*  503:     */         default: 
/*  504: 773 */           result = "-can't display-";
/*  505:     */         }
/*  506: 776 */         if ((getType(columnIndex) != 0) && 
/*  507: 777 */           (result != null))
/*  508:     */         {
/*  509: 778 */           String tmp = result.toString();
/*  510: 779 */           boolean modified = false;
/*  511: 781 */           if ((tmp.indexOf('<') > -1) || (tmp.indexOf('>') > -1))
/*  512:     */           {
/*  513: 782 */             tmp = tmp.replace("<", "(");
/*  514: 783 */             tmp = tmp.replace(">", ")");
/*  515: 784 */             modified = true;
/*  516:     */           }
/*  517: 787 */           if ((tmp.indexOf("\n") > -1) || (tmp.indexOf("\r") > -1))
/*  518:     */           {
/*  519: 788 */             tmp = tmp.replaceAll("\\r\\n", "<font color=\"red\"><b>\\\\r\\\\n</b></font>");
/*  520:     */             
/*  521:     */ 
/*  522: 791 */             tmp = tmp.replaceAll("\\r", "<font color=\"red\"><b>\\\\r</b></font>");
/*  523:     */             
/*  524:     */ 
/*  525: 794 */             tmp = tmp.replaceAll("\\n", "<font color=\"red\"><b>\\\\n</b></font>");
/*  526:     */             
/*  527:     */ 
/*  528: 797 */             tmp = "<html>" + tmp + "</html>";
/*  529: 798 */             modified = true;
/*  530:     */           }
/*  531: 800 */           result = tmp;
/*  532: 801 */           if (modified) {
/*  533: 802 */             this.m_Cache.put(key, tmp);
/*  534:     */           }
/*  535:     */         }
/*  536:     */       }
/*  537:     */     }
/*  538: 811 */     return result;
/*  539:     */   }
/*  540:     */   
/*  541:     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*  542:     */   {
/*  543: 823 */     return (columnIndex > 0) && (!isReadOnly());
/*  544:     */   }
/*  545:     */   
/*  546:     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*  547:     */   {
/*  548: 836 */     setValueAt(aValue, rowIndex, columnIndex, true);
/*  549:     */   }
/*  550:     */   
/*  551:     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex, boolean notify)
/*  552:     */   {
/*  553: 857 */     if (!this.m_IgnoreChanges) {
/*  554: 858 */       addUndoPoint();
/*  555:     */     }
/*  556: 861 */     Object oldValue = getValueAt(rowIndex, columnIndex);
/*  557: 862 */     int type = getType(rowIndex, columnIndex);
/*  558: 863 */     int index = columnIndex - 1;
/*  559: 864 */     Instance inst = this.m_Data.instance(rowIndex);
/*  560: 865 */     Attribute att = inst.attribute(index);
/*  561: 868 */     if (aValue == null)
/*  562:     */     {
/*  563: 869 */       inst.setValue(index, Utils.missingValue());
/*  564:     */     }
/*  565:     */     else
/*  566:     */     {
/*  567: 871 */       String tmp = aValue.toString();
/*  568: 873 */       switch (type)
/*  569:     */       {
/*  570:     */       case 3: 
/*  571:     */         try
/*  572:     */         {
/*  573: 876 */           att.parseDate(tmp);
/*  574: 877 */           inst.setValue(index, att.parseDate(tmp));
/*  575:     */         }
/*  576:     */         catch (Exception e) {}
/*  577:     */       case 1: 
/*  578: 884 */         if (att.indexOfValue(tmp) > -1) {
/*  579: 885 */           inst.setValue(index, att.indexOfValue(tmp));
/*  580:     */         }
/*  581:     */         break;
/*  582:     */       case 2: 
/*  583: 890 */         inst.setValue(index, tmp);
/*  584: 891 */         break;
/*  585:     */       case 0: 
/*  586:     */         try
/*  587:     */         {
/*  588: 895 */           Double.parseDouble(tmp);
/*  589: 896 */           inst.setValue(index, Double.parseDouble(tmp));
/*  590:     */         }
/*  591:     */         catch (Exception e) {}
/*  592:     */       case 4: 
/*  593:     */         try
/*  594:     */         {
/*  595: 904 */           inst.setValue(index, inst.attribute(index).addRelation((Instances)aValue));
/*  596:     */         }
/*  597:     */         catch (Exception e) {}
/*  598:     */       default: 
/*  599: 912 */         throw new IllegalArgumentException("Unsupported Attribute type: " + type + "!");
/*  600:     */       }
/*  601:     */     }
/*  602: 918 */     if ((notify) && (!("" + oldValue).equals("" + aValue))) {
/*  603: 919 */       notifyListener(new TableModelEvent(this, rowIndex, columnIndex));
/*  604:     */     }
/*  605:     */   }
/*  606:     */   
/*  607:     */   public void addTableModelListener(TableModelListener l)
/*  608:     */   {
/*  609: 931 */     this.m_Listeners.add(l);
/*  610:     */   }
/*  611:     */   
/*  612:     */   public void removeTableModelListener(TableModelListener l)
/*  613:     */   {
/*  614: 942 */     this.m_Listeners.remove(l);
/*  615:     */   }
/*  616:     */   
/*  617:     */   public void notifyListener(TableModelEvent e)
/*  618:     */   {
/*  619: 955 */     if (!isNotificationEnabled()) {
/*  620: 956 */       return;
/*  621:     */     }
/*  622: 959 */     Iterator<TableModelListener> iter = this.m_Listeners.iterator();
/*  623: 960 */     while (iter.hasNext())
/*  624:     */     {
/*  625: 961 */       TableModelListener l = (TableModelListener)iter.next();
/*  626: 962 */       l.tableChanged(e);
/*  627:     */     }
/*  628:     */   }
/*  629:     */   
/*  630:     */   public void clearUndo()
/*  631:     */   {
/*  632: 971 */     this.m_UndoList = new Vector();
/*  633:     */   }
/*  634:     */   
/*  635:     */   public boolean canUndo()
/*  636:     */   {
/*  637: 982 */     return !this.m_UndoList.isEmpty();
/*  638:     */   }
/*  639:     */   
/*  640:     */   public void undo()
/*  641:     */   {
/*  642: 994 */     if (canUndo())
/*  643:     */     {
/*  644: 996 */       File tempFile = (File)this.m_UndoList.get(this.m_UndoList.size() - 1);
/*  645:     */       try
/*  646:     */       {
/*  647: 999 */         ObjectInputStream ooi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tempFile)));
/*  648:     */         
/*  649:     */ 
/*  650:1002 */         Instances inst = (Instances)ooi.readObject();
/*  651:1003 */         ooi.close();
/*  652:     */         
/*  653:     */ 
/*  654:1006 */         setInstances(inst);
/*  655:1007 */         notifyListener(new TableModelEvent(this, -1));
/*  656:1008 */         notifyListener(new TableModelEvent(this));
/*  657:     */       }
/*  658:     */       catch (Exception e)
/*  659:     */       {
/*  660:1010 */         e.printStackTrace();
/*  661:     */       }
/*  662:1012 */       tempFile.delete();
/*  663:     */       
/*  664:     */ 
/*  665:1015 */       this.m_UndoList.remove(this.m_UndoList.size() - 1);
/*  666:     */     }
/*  667:     */   }
/*  668:     */   
/*  669:     */   public void addUndoPoint()
/*  670:     */   {
/*  671:1031 */     if (!isUndoEnabled()) {
/*  672:1032 */       return;
/*  673:     */     }
/*  674:1035 */     if (getInstances() != null) {
/*  675:     */       try
/*  676:     */       {
/*  677:1038 */         File tempFile = File.createTempFile("arffviewer", null);
/*  678:1039 */         tempFile.deleteOnExit();
/*  679:     */         
/*  680:     */ 
/*  681:1042 */         ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
/*  682:     */         
/*  683:     */ 
/*  684:1045 */         oos.writeObject(getInstances());
/*  685:1046 */         oos.flush();
/*  686:1047 */         oos.close();
/*  687:     */         
/*  688:     */ 
/*  689:1050 */         this.m_UndoList.add(tempFile);
/*  690:     */       }
/*  691:     */       catch (Exception e)
/*  692:     */       {
/*  693:1052 */         e.printStackTrace();
/*  694:     */       }
/*  695:     */     }
/*  696:     */   }
/*  697:     */   
/*  698:     */   public void setShowAttributeIndex(boolean value)
/*  699:     */   {
/*  700:1064 */     this.m_ShowAttributeIndex = value;
/*  701:1065 */     fireTableStructureChanged();
/*  702:     */   }
/*  703:     */   
/*  704:     */   public boolean getShowAttributeIndex()
/*  705:     */   {
/*  706:1074 */     return this.m_ShowAttributeIndex;
/*  707:     */   }
/*  708:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffTableModel
 * JD-Core Version:    0.7.0.1
 */