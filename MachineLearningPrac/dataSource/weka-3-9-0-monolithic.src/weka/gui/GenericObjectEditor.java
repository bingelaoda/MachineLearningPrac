/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Component;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.FlowLayout;
/*    8:     */ import java.awt.Font;
/*    9:     */ import java.awt.FontMetrics;
/*   10:     */ import java.awt.Frame;
/*   11:     */ import java.awt.Graphics;
/*   12:     */ import java.awt.GridLayout;
/*   13:     */ import java.awt.Point;
/*   14:     */ import java.awt.Rectangle;
/*   15:     */ import java.awt.Toolkit;
/*   16:     */ import java.awt.Window;
/*   17:     */ import java.awt.event.ActionEvent;
/*   18:     */ import java.awt.event.ActionListener;
/*   19:     */ import java.awt.event.MouseEvent;
/*   20:     */ import java.awt.event.WindowAdapter;
/*   21:     */ import java.awt.event.WindowEvent;
/*   22:     */ import java.beans.PropertyChangeEvent;
/*   23:     */ import java.beans.PropertyChangeListener;
/*   24:     */ import java.beans.PropertyChangeSupport;
/*   25:     */ import java.beans.PropertyEditor;
/*   26:     */ import java.beans.PropertyEditorManager;
/*   27:     */ import java.io.BufferedInputStream;
/*   28:     */ import java.io.BufferedOutputStream;
/*   29:     */ import java.io.File;
/*   30:     */ import java.io.FileInputStream;
/*   31:     */ import java.io.FileOutputStream;
/*   32:     */ import java.io.ObjectInputStream;
/*   33:     */ import java.io.ObjectOutputStream;
/*   34:     */ import java.io.PrintStream;
/*   35:     */ import java.lang.reflect.Array;
/*   36:     */ import java.util.ArrayList;
/*   37:     */ import java.util.Collections;
/*   38:     */ import java.util.Enumeration;
/*   39:     */ import java.util.Hashtable;
/*   40:     */ import java.util.List;
/*   41:     */ import java.util.Properties;
/*   42:     */ import java.util.Set;
/*   43:     */ import java.util.StringTokenizer;
/*   44:     */ import java.util.Vector;
/*   45:     */ import javax.swing.BorderFactory;
/*   46:     */ import javax.swing.JButton;
/*   47:     */ import javax.swing.JDialog;
/*   48:     */ import javax.swing.JFileChooser;
/*   49:     */ import javax.swing.JLabel;
/*   50:     */ import javax.swing.JOptionPane;
/*   51:     */ import javax.swing.JPanel;
/*   52:     */ import javax.swing.JPopupMenu;
/*   53:     */ import javax.swing.JScrollBar;
/*   54:     */ import javax.swing.JScrollPane;
/*   55:     */ import javax.swing.JTree;
/*   56:     */ import javax.swing.ToolTipManager;
/*   57:     */ import javax.swing.event.TreeSelectionEvent;
/*   58:     */ import javax.swing.event.TreeSelectionListener;
/*   59:     */ import javax.swing.tree.DefaultMutableTreeNode;
/*   60:     */ import javax.swing.tree.TreeNode;
/*   61:     */ import javax.swing.tree.TreePath;
/*   62:     */ import javax.swing.tree.TreeSelectionModel;
/*   63:     */ import weka.classifiers.Classifier;
/*   64:     */ import weka.classifiers.rules.ZeroR;
/*   65:     */ import weka.core.Capabilities;
/*   66:     */ import weka.core.Capabilities.Capability;
/*   67:     */ import weka.core.CapabilitiesHandler;
/*   68:     */ import weka.core.ClassDiscovery;
/*   69:     */ import weka.core.ClassDiscovery.StringCompare;
/*   70:     */ import weka.core.CustomDisplayStringProvider;
/*   71:     */ import weka.core.OptionHandler;
/*   72:     */ import weka.core.PluginManager;
/*   73:     */ import weka.core.SerializedObject;
/*   74:     */ import weka.core.Utils;
/*   75:     */ import weka.core.WekaPackageManager;
/*   76:     */ import weka.core.logging.Logger;
/*   77:     */ import weka.core.logging.Logger.Level;
/*   78:     */ 
/*   79:     */ public class GenericObjectEditor
/*   80:     */   implements PropertyEditor, CustomPanelSupplier
/*   81:     */ {
/*   82:     */   protected Object m_Object;
/*   83:     */   protected Object m_Backup;
/*   84: 132 */   protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
/*   85:     */   protected Class<?> m_ClassType;
/*   86:     */   protected Hashtable<String, HierarchyPropertyParser> m_ObjectNames;
/*   87:     */   protected GOEPanel m_EditorComponent;
/*   88: 144 */   protected boolean m_Enabled = true;
/*   89: 147 */   protected static String PROPERTY_FILE = "weka/gui/GenericObjectEditor.props";
/*   90:     */   protected static Properties EDITOR_PROPERTIES;
/*   91:     */   public static final String GUIEDITORS_PROPERTY_FILE = "weka/gui/GUIEditors.props";
/*   92:     */   protected GOETreeNode m_treeNodeOfCurrentObject;
/*   93:     */   protected PropertyPanel m_ObjectPropertyPanel;
/*   94:     */   protected boolean m_canChangeClassInDialog;
/*   95:     */   protected GenericObjectEditorHistory m_History;
/*   96:     */   protected static boolean m_EditorsRegistered;
/*   97:     */   protected static boolean m_ShowGlobalInfoToolTip;
/*   98: 174 */   protected Capabilities m_CapabilitiesFilter = null;
/*   99:     */   
/*  100:     */   public static void setShowGlobalInfoToolTips(boolean show)
/*  101:     */   {
/*  102: 177 */     m_ShowGlobalInfoToolTip = show;
/*  103:     */   }
/*  104:     */   
/*  105:     */   public boolean getShowGlobalInfoToolTips()
/*  106:     */   {
/*  107: 181 */     return m_ShowGlobalInfoToolTip;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public static void determineClasses()
/*  111:     */   {
/*  112:     */     try
/*  113:     */     {
/*  114: 187 */       WekaPackageManager.loadPackages(false);
/*  115: 190 */       if (WekaPackageManager.m_initialPackageLoadingInProcess) {
/*  116: 191 */         return;
/*  117:     */       }
/*  118: 194 */       EDITOR_PROPERTIES = GenericPropertiesCreator.getGlobalOutputProperties();
/*  119: 195 */       if (EDITOR_PROPERTIES == null)
/*  120:     */       {
/*  121: 197 */         GenericPropertiesCreator creator = new GenericPropertiesCreator();
/*  122: 200 */         if (creator.useDynamic()) {
/*  123:     */           try
/*  124:     */           {
/*  125: 202 */             creator.execute(false);
/*  126: 203 */             EDITOR_PROPERTIES = creator.getOutputProperties();
/*  127:     */           }
/*  128:     */           catch (Exception e)
/*  129:     */           {
/*  130: 205 */             JOptionPane.showMessageDialog(null, "Could not determine the properties for the generic object\neditor. This exception was produced:\n" + e.toString(), "GenericObjectEditor", 0);
/*  131:     */           }
/*  132:     */         } else {
/*  133:     */           try
/*  134:     */           {
/*  135: 213 */             EDITOR_PROPERTIES = Utils.readProperties(PROPERTY_FILE);
/*  136: 214 */             Enumeration<?> keys = EDITOR_PROPERTIES.propertyNames();
/*  137: 215 */             if (!keys.hasMoreElements()) {
/*  138: 216 */               throw new Exception("Failed to read a property file for the generic object editor");
/*  139:     */             }
/*  140:     */           }
/*  141:     */           catch (Exception ex)
/*  142:     */           {
/*  143: 220 */             JOptionPane.showMessageDialog(null, "Could not read a configuration file for the generic object\neditor. An example file is included with the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\" and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "GenericObjectEditor", 0);
/*  144:     */           }
/*  145:     */         }
/*  146:     */       }
/*  147: 234 */       if (EDITOR_PROPERTIES == null) {
/*  148: 235 */         JOptionPane.showMessageDialog(null, "Could not initialize the GenericPropertiesCreator. ", "GenericObjectEditor", 0);
/*  149:     */       } else {
/*  150: 239 */         PluginManager.addFromProperties(EDITOR_PROPERTIES);
/*  151:     */       }
/*  152:     */     }
/*  153:     */     catch (Exception e)
/*  154:     */     {
/*  155: 242 */       JOptionPane.showMessageDialog(null, "Could not initialize the GenericPropertiesCreator. This exception was produced:\n" + e.toString(), "GenericObjectEditor", 0);
/*  156:     */     }
/*  157:     */   }
/*  158:     */   
/*  159:     */   static
/*  160:     */   {
/*  161: 257 */     determineClasses();
/*  162:     */   }
/*  163:     */   
/*  164:     */   public class GOETreeNode
/*  165:     */     extends DefaultMutableTreeNode
/*  166:     */   {
/*  167:     */     static final long serialVersionUID = -1707872446682150133L;
/*  168:     */     public static final String NO_SUPPORT = "silver";
/*  169:     */     public static final String MAYBE_SUPPORT = "blue";
/*  170: 275 */     protected Capabilities m_Capabilities = null;
/*  171:     */     protected String m_toolTipText;
/*  172:     */     
/*  173:     */     public GOETreeNode() {}
/*  174:     */     
/*  175:     */     public GOETreeNode(Object userObject)
/*  176:     */     {
/*  177: 296 */       super();
/*  178:     */     }
/*  179:     */     
/*  180:     */     public GOETreeNode(Object userObject, boolean allowsChildren)
/*  181:     */     {
/*  182: 309 */       super(allowsChildren);
/*  183:     */     }
/*  184:     */     
/*  185:     */     public void setToolTipText(String tip)
/*  186:     */     {
/*  187: 318 */       this.m_toolTipText = tip;
/*  188:     */     }
/*  189:     */     
/*  190:     */     public String getToolTipText()
/*  191:     */     {
/*  192: 327 */       return this.m_toolTipText;
/*  193:     */     }
/*  194:     */     
/*  195:     */     protected void initCapabilities()
/*  196:     */     {
/*  197: 338 */       if (this.m_Capabilities != null) {
/*  198: 339 */         return;
/*  199:     */       }
/*  200: 341 */       if (!isLeaf()) {
/*  201: 342 */         return;
/*  202:     */       }
/*  203: 345 */       String classname = GenericObjectEditor.this.getClassnameFromPath(new TreePath(getPath()));
/*  204:     */       try
/*  205:     */       {
/*  206: 347 */         Class<?> cls = Class.forName(classname);
/*  207: 348 */         if (!ClassDiscovery.hasInterface(CapabilitiesHandler.class, cls)) {
/*  208: 349 */           return;
/*  209:     */         }
/*  210: 352 */         Object obj = cls.newInstance();
/*  211: 353 */         this.m_Capabilities = ((CapabilitiesHandler)obj).getCapabilities();
/*  212:     */       }
/*  213:     */       catch (Exception e) {}
/*  214:     */     }
/*  215:     */     
/*  216:     */     public String toString()
/*  217:     */     {
/*  218: 368 */       String result = super.toString();
/*  219: 370 */       if (GenericObjectEditor.this.m_CapabilitiesFilter != null)
/*  220:     */       {
/*  221: 371 */         initCapabilities();
/*  222: 372 */         if (this.m_Capabilities != null) {
/*  223: 373 */           if ((this.m_Capabilities.supportsMaybe(GenericObjectEditor.this.m_CapabilitiesFilter)) && (!this.m_Capabilities.supports(GenericObjectEditor.this.m_CapabilitiesFilter))) {
/*  224: 375 */             result = "<html><font color=\"blue\">" + result + "</font></i><html>";
/*  225: 377 */           } else if (!this.m_Capabilities.supports(GenericObjectEditor.this.m_CapabilitiesFilter)) {
/*  226: 378 */             result = "<html><font color=\"silver\">" + result + "</font></i><html>";
/*  227:     */           }
/*  228:     */         }
/*  229:     */       }
/*  230: 384 */       return result;
/*  231:     */     }
/*  232:     */   }
/*  233:     */   
/*  234:     */   public class CapabilitiesFilterDialog
/*  235:     */     extends JDialog
/*  236:     */   {
/*  237:     */     static final long serialVersionUID = -7845503345689646266L;
/*  238:     */     protected JDialog m_Self;
/*  239: 400 */     protected JPopupMenu m_Popup = null;
/*  240: 403 */     protected Capabilities m_Capabilities = new Capabilities(null);
/*  241: 406 */     protected JLabel m_InfoLabel = new JLabel();
/*  242: 409 */     protected CheckBoxList m_List = new CheckBoxList();
/*  243: 412 */     protected JButton m_OkButton = new JButton("OK");
/*  244: 415 */     protected JButton m_CancelButton = new JButton("Cancel");
/*  245:     */     
/*  246:     */     public CapabilitiesFilterDialog()
/*  247:     */     {
/*  248: 423 */       this.m_Self = this;
/*  249:     */       
/*  250: 425 */       initGUI();
/*  251:     */     }
/*  252:     */     
/*  253:     */     protected void initGUI()
/*  254:     */     {
/*  255: 435 */       setTitle("Filtering Capabilities...");
/*  256: 436 */       setLayout(new BorderLayout());
/*  257:     */       
/*  258: 438 */       JPanel panel = new JPanel(new BorderLayout());
/*  259: 439 */       panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  260: 440 */       getContentPane().add(panel, "North");
/*  261: 441 */       this.m_InfoLabel.setText("<html>" + GenericObjectEditor.this.m_ClassType.getName().replaceAll(".*\\.", "") + "s" + " have to support <i>at least</i> the following capabilities <br>" + "(the ones highlighted <font color=\"" + "silver" + "\">" + "silver" + "</font> don't meet these requirements <br>" + "the ones highlighted  <font color=\"" + "blue" + "\">" + "blue" + "</font> possibly meet them):" + "</html>");
/*  262:     */       
/*  263:     */ 
/*  264:     */ 
/*  265:     */ 
/*  266:     */ 
/*  267:     */ 
/*  268:     */ 
/*  269:     */ 
/*  270: 450 */       panel.add(this.m_InfoLabel, "Center");
/*  271:     */       
/*  272:     */ 
/*  273: 453 */       getContentPane().add(new JScrollPane(this.m_List), "Center");
/*  274: 454 */       CheckBoxList.CheckBoxListModel model = (CheckBoxList.CheckBoxListModel)this.m_List.getModel();
/*  275: 455 */       for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  276: 456 */         model.addElement(cap);
/*  277:     */       }
/*  278: 460 */       panel = new JPanel(new FlowLayout(1));
/*  279: 461 */       getContentPane().add(panel, "South");
/*  280:     */       
/*  281: 463 */       this.m_OkButton.setMnemonic('O');
/*  282: 464 */       this.m_OkButton.addActionListener(new ActionListener()
/*  283:     */       {
/*  284:     */         public void actionPerformed(ActionEvent e)
/*  285:     */         {
/*  286: 467 */           GenericObjectEditor.CapabilitiesFilterDialog.this.updateCapabilities();
/*  287: 468 */           if (GenericObjectEditor.this.m_CapabilitiesFilter == null) {
/*  288: 469 */             GenericObjectEditor.this.m_CapabilitiesFilter = new Capabilities(null);
/*  289:     */           }
/*  290: 471 */           GenericObjectEditor.this.m_CapabilitiesFilter.assign(GenericObjectEditor.CapabilitiesFilterDialog.this.m_Capabilities);
/*  291: 472 */           GenericObjectEditor.CapabilitiesFilterDialog.this.m_Self.setVisible(false);
/*  292: 473 */           GenericObjectEditor.CapabilitiesFilterDialog.this.showPopup();
/*  293:     */         }
/*  294: 475 */       });
/*  295: 476 */       panel.add(this.m_OkButton);
/*  296:     */       
/*  297: 478 */       this.m_CancelButton.setMnemonic('C');
/*  298: 479 */       this.m_CancelButton.addActionListener(new ActionListener()
/*  299:     */       {
/*  300:     */         public void actionPerformed(ActionEvent e)
/*  301:     */         {
/*  302: 482 */           GenericObjectEditor.CapabilitiesFilterDialog.this.m_Self.setVisible(false);
/*  303: 483 */           GenericObjectEditor.CapabilitiesFilterDialog.this.showPopup();
/*  304:     */         }
/*  305: 485 */       });
/*  306: 486 */       panel.add(this.m_CancelButton);
/*  307: 487 */       pack();
/*  308:     */     }
/*  309:     */     
/*  310:     */     protected void updateList()
/*  311:     */     {
/*  312: 499 */       CheckBoxList.CheckBoxListModel model = (CheckBoxList.CheckBoxListModel)this.m_List.getModel();
/*  313: 501 */       for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  314: 502 */         model.setChecked(model.indexOf(cap), this.m_Capabilities.handles(cap));
/*  315:     */       }
/*  316:     */     }
/*  317:     */     
/*  318:     */     protected void updateCapabilities()
/*  319:     */     {
/*  320: 516 */       CheckBoxList.CheckBoxListModel model = (CheckBoxList.CheckBoxListModel)this.m_List.getModel();
/*  321: 518 */       for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  322: 519 */         if (model.getChecked(model.indexOf(cap))) {
/*  323: 520 */           this.m_Capabilities.enable(cap);
/*  324:     */         } else {
/*  325: 522 */           this.m_Capabilities.disable(cap);
/*  326:     */         }
/*  327:     */       }
/*  328:     */     }
/*  329:     */     
/*  330:     */     public void setCapabilities(Capabilities value)
/*  331:     */     {
/*  332: 533 */       if (value != null) {
/*  333: 534 */         this.m_Capabilities.assign(value);
/*  334:     */       } else {
/*  335: 536 */         this.m_Capabilities = new Capabilities(null);
/*  336:     */       }
/*  337: 539 */       updateList();
/*  338:     */     }
/*  339:     */     
/*  340:     */     public Capabilities getCapabilities()
/*  341:     */     {
/*  342: 548 */       return this.m_Capabilities;
/*  343:     */     }
/*  344:     */     
/*  345:     */     public void setPopup(JPopupMenu value)
/*  346:     */     {
/*  347: 557 */       this.m_Popup = value;
/*  348:     */     }
/*  349:     */     
/*  350:     */     public JPopupMenu getPopup()
/*  351:     */     {
/*  352: 566 */       return this.m_Popup;
/*  353:     */     }
/*  354:     */     
/*  355:     */     public void showPopup()
/*  356:     */     {
/*  357: 574 */       if (getPopup() != null) {
/*  358: 575 */         getPopup().setVisible(true);
/*  359:     */       }
/*  360:     */     }
/*  361:     */   }
/*  362:     */   
/*  363:     */   public class JTreePopupMenu
/*  364:     */     extends JPopupMenu
/*  365:     */   {
/*  366:     */     static final long serialVersionUID = -3404546329655057387L;
/*  367:     */     private final JPopupMenu m_Self;
/*  368:     */     private final JTree m_tree;
/*  369:     */     private final JScrollPane m_scroller;
/*  370: 599 */     private final JButton m_FilterButton = new JButton("Filter...");
/*  371: 602 */     private final JButton m_RemoveFilterButton = new JButton("Remove filter");
/*  372: 605 */     private final JButton m_CloseButton = new JButton("Close");
/*  373:     */     
/*  374:     */     public JTreePopupMenu(JTree tree)
/*  375:     */     {
/*  376: 614 */       this.m_Self = this;
/*  377:     */       
/*  378: 616 */       setLayout(new BorderLayout());
/*  379: 617 */       JPanel panel = new JPanel(new FlowLayout(2));
/*  380: 618 */       add(panel, "South");
/*  381: 620 */       if (ClassDiscovery.hasInterface(CapabilitiesHandler.class, GenericObjectEditor.this.m_ClassType))
/*  382:     */       {
/*  383: 622 */         this.m_FilterButton.setMnemonic('F');
/*  384: 623 */         this.m_FilterButton.addActionListener(new ActionListener()
/*  385:     */         {
/*  386:     */           public void actionPerformed(ActionEvent e)
/*  387:     */           {
/*  388: 626 */             if (e.getSource() == GenericObjectEditor.JTreePopupMenu.this.m_FilterButton)
/*  389:     */             {
/*  390: 627 */               GenericObjectEditor.CapabilitiesFilterDialog dialog = new GenericObjectEditor.CapabilitiesFilterDialog(GenericObjectEditor.this);
/*  391: 628 */               dialog.setCapabilities(GenericObjectEditor.this.m_CapabilitiesFilter);
/*  392: 629 */               dialog.setPopup(GenericObjectEditor.JTreePopupMenu.this.m_Self);
/*  393: 630 */               dialog.setVisible(true);
/*  394: 631 */               GenericObjectEditor.this.m_Support.firePropertyChange("", null, null);
/*  395: 632 */               GenericObjectEditor.JTreePopupMenu.this.repaint();
/*  396:     */             }
/*  397:     */           }
/*  398: 635 */         });
/*  399: 636 */         panel.add(this.m_FilterButton);
/*  400:     */         
/*  401:     */ 
/*  402: 639 */         this.m_RemoveFilterButton.setMnemonic('R');
/*  403: 640 */         this.m_RemoveFilterButton.addActionListener(new ActionListener()
/*  404:     */         {
/*  405:     */           public void actionPerformed(ActionEvent e)
/*  406:     */           {
/*  407: 643 */             if (e.getSource() == GenericObjectEditor.JTreePopupMenu.this.m_RemoveFilterButton)
/*  408:     */             {
/*  409: 644 */               GenericObjectEditor.this.m_CapabilitiesFilter = null;
/*  410: 645 */               GenericObjectEditor.this.m_Support.firePropertyChange("", null, null);
/*  411: 646 */               GenericObjectEditor.JTreePopupMenu.this.repaint();
/*  412:     */             }
/*  413:     */           }
/*  414: 649 */         });
/*  415: 650 */         panel.add(this.m_RemoveFilterButton);
/*  416:     */       }
/*  417: 654 */       this.m_CloseButton.setMnemonic('C');
/*  418: 655 */       this.m_CloseButton.addActionListener(new ActionListener()
/*  419:     */       {
/*  420:     */         public void actionPerformed(ActionEvent e)
/*  421:     */         {
/*  422: 658 */           if (e.getSource() == GenericObjectEditor.JTreePopupMenu.this.m_CloseButton) {
/*  423: 659 */             GenericObjectEditor.JTreePopupMenu.this.m_Self.setVisible(false);
/*  424:     */           }
/*  425:     */         }
/*  426: 662 */       });
/*  427: 663 */       panel.add(this.m_CloseButton);
/*  428:     */       
/*  429: 665 */       this.m_tree = tree;
/*  430:     */       
/*  431: 667 */       JPanel treeView = new JPanel();
/*  432: 668 */       treeView.setLayout(new BorderLayout());
/*  433: 669 */       treeView.add(this.m_tree, "North");
/*  434:     */       
/*  435:     */ 
/*  436: 672 */       treeView.setBackground(this.m_tree.getBackground());
/*  437:     */       
/*  438: 674 */       this.m_scroller = new JScrollPane(treeView);
/*  439:     */       
/*  440: 676 */       this.m_scroller.setPreferredSize(new Dimension(300, 400));
/*  441: 677 */       this.m_scroller.getVerticalScrollBar().setUnitIncrement(20);
/*  442:     */       
/*  443: 679 */       add(this.m_scroller);
/*  444:     */     }
/*  445:     */     
/*  446:     */     public void show(Component invoker, int x, int y)
/*  447:     */     {
/*  448: 692 */       super.show(invoker, x, y);
/*  449:     */       
/*  450:     */ 
/*  451: 695 */       Point location = getLocationOnScreen();
/*  452: 696 */       Dimension screenSize = getToolkit().getScreenSize();
/*  453: 697 */       int maxWidth = (int)(screenSize.getWidth() - location.getX());
/*  454: 698 */       int maxHeight = (int)(screenSize.getHeight() - location.getY());
/*  455:     */       
/*  456:     */ 
/*  457: 701 */       Dimension scrollerSize = this.m_scroller.getPreferredSize();
/*  458: 702 */       int height = (int)scrollerSize.getHeight();
/*  459: 703 */       int width = (int)scrollerSize.getWidth();
/*  460: 704 */       if (width > maxWidth) {
/*  461: 705 */         width = maxWidth;
/*  462:     */       }
/*  463: 707 */       if (height > maxHeight) {
/*  464: 708 */         height = maxHeight;
/*  465:     */       }
/*  466: 712 */       this.m_scroller.setPreferredSize(new Dimension(width, height));
/*  467: 713 */       revalidate();
/*  468: 714 */       pack();
/*  469:     */     }
/*  470:     */   }
/*  471:     */   
/*  472:     */   public class GOEPanel
/*  473:     */     extends JPanel
/*  474:     */   {
/*  475:     */     static final long serialVersionUID = 3656028520876011335L;
/*  476:     */     protected PropertySheetPanel m_ChildPropertySheet;
/*  477:     */     protected JLabel m_ClassNameLabel;
/*  478:     */     protected JButton m_OpenBut;
/*  479:     */     protected JButton m_SaveBut;
/*  480:     */     protected JButton m_okBut;
/*  481:     */     protected JButton m_cancelBut;
/*  482:     */     protected JFileChooser m_FileChooser;
/*  483:     */     
/*  484:     */     public GOEPanel()
/*  485:     */     {
/*  486: 750 */       GenericObjectEditor.this.m_Backup = copyObject(GenericObjectEditor.this.m_Object);
/*  487:     */       
/*  488: 752 */       this.m_ClassNameLabel = new JLabel("None");
/*  489: 753 */       this.m_ClassNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  490:     */       
/*  491: 755 */       this.m_ChildPropertySheet = new PropertySheetPanel();
/*  492: 756 */       this.m_ChildPropertySheet.addPropertyChangeListener(new PropertyChangeListener()
/*  493:     */       {
/*  494:     */         public void propertyChange(PropertyChangeEvent evt)
/*  495:     */         {
/*  496: 760 */           GenericObjectEditor.this.m_Support.firePropertyChange("", null, null);
/*  497:     */         }
/*  498: 763 */       });
/*  499: 764 */       this.m_OpenBut = new JButton("Open...");
/*  500: 765 */       this.m_OpenBut.setToolTipText("Load a configured object");
/*  501: 766 */       this.m_OpenBut.setEnabled(true);
/*  502: 767 */       this.m_OpenBut.addActionListener(new ActionListener()
/*  503:     */       {
/*  504:     */         public void actionPerformed(ActionEvent e)
/*  505:     */         {
/*  506: 770 */           Object object = GenericObjectEditor.GOEPanel.this.openObject();
/*  507: 771 */           if (object != null)
/*  508:     */           {
/*  509: 774 */             GenericObjectEditor.this.setValue(object);
/*  510:     */             
/*  511:     */ 
/*  512: 777 */             GenericObjectEditor.this.setValue(object);
/*  513:     */           }
/*  514:     */         }
/*  515: 781 */       });
/*  516: 782 */       this.m_SaveBut = new JButton("Save...");
/*  517: 783 */       this.m_SaveBut.setToolTipText("Save the current configured object");
/*  518: 784 */       this.m_SaveBut.setEnabled(true);
/*  519: 785 */       this.m_SaveBut.addActionListener(new ActionListener()
/*  520:     */       {
/*  521:     */         public void actionPerformed(ActionEvent e)
/*  522:     */         {
/*  523: 788 */           GenericObjectEditor.GOEPanel.this.saveObject(GenericObjectEditor.this.m_Object);
/*  524:     */         }
/*  525: 791 */       });
/*  526: 792 */       this.m_okBut = new JButton("OK");
/*  527: 793 */       this.m_okBut.setEnabled(true);
/*  528: 794 */       this.m_okBut.addActionListener(new ActionListener()
/*  529:     */       {
/*  530:     */         public void actionPerformed(ActionEvent e)
/*  531:     */         {
/*  532: 798 */           GenericObjectEditor.GOEPanel.this.m_ChildPropertySheet.closingOK();
/*  533: 799 */           GenericObjectEditor.this.m_Backup = GenericObjectEditor.GOEPanel.this.copyObject(GenericObjectEditor.this.m_Object);
/*  534: 800 */           if ((GenericObjectEditor.GOEPanel.this.getTopLevelAncestor() != null) && ((GenericObjectEditor.GOEPanel.this.getTopLevelAncestor() instanceof Window)))
/*  535:     */           {
/*  536: 802 */             Window w = (Window)GenericObjectEditor.GOEPanel.this.getTopLevelAncestor();
/*  537: 803 */             w.dispose();
/*  538:     */           }
/*  539:     */         }
/*  540: 807 */       });
/*  541: 808 */       this.m_cancelBut = new JButton("Cancel");
/*  542: 809 */       this.m_cancelBut.setEnabled(true);
/*  543: 810 */       this.m_cancelBut.addActionListener(new ActionListener()
/*  544:     */       {
/*  545:     */         public void actionPerformed(ActionEvent e)
/*  546:     */         {
/*  547: 814 */           GenericObjectEditor.GOEPanel.this.m_ChildPropertySheet.closingCancel();
/*  548: 815 */           if (GenericObjectEditor.this.m_Backup != null)
/*  549:     */           {
/*  550: 817 */             GenericObjectEditor.this.m_Object = GenericObjectEditor.GOEPanel.this.copyObject(GenericObjectEditor.this.m_Backup);
/*  551:     */             
/*  552:     */ 
/*  553: 820 */             GenericObjectEditor.this.m_Support.firePropertyChange("", null, null);
/*  554: 821 */             GenericObjectEditor.this.m_ObjectNames = GenericObjectEditor.this.getClassesFromProperties();
/*  555: 822 */             GenericObjectEditor.this.updateObjectNames();
/*  556: 823 */             GenericObjectEditor.GOEPanel.this.updateChildPropertySheet();
/*  557:     */           }
/*  558: 825 */           if ((GenericObjectEditor.GOEPanel.this.getTopLevelAncestor() != null) && ((GenericObjectEditor.GOEPanel.this.getTopLevelAncestor() instanceof Window)))
/*  559:     */           {
/*  560: 827 */             Window w = (Window)GenericObjectEditor.GOEPanel.this.getTopLevelAncestor();
/*  561: 828 */             w.dispose();
/*  562:     */           }
/*  563:     */         }
/*  564: 832 */       });
/*  565: 833 */       setLayout(new BorderLayout());
/*  566:     */       JButton chooseButton;
/*  567: 835 */       if (GenericObjectEditor.this.m_canChangeClassInDialog)
/*  568:     */       {
/*  569: 836 */         chooseButton = GenericObjectEditor.this.createChooseClassButton();
/*  570: 837 */         JPanel top = new JPanel();
/*  571: 838 */         top.setLayout(new BorderLayout());
/*  572: 839 */         top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  573: 840 */         top.add(chooseButton, "West");
/*  574: 841 */         top.add(this.m_ClassNameLabel, "Center");
/*  575: 842 */         add(top, "North");
/*  576:     */       }
/*  577:     */       else
/*  578:     */       {
/*  579: 844 */         add(this.m_ClassNameLabel, "North");
/*  580:     */       }
/*  581: 847 */       add(this.m_ChildPropertySheet, "Center");
/*  582:     */       
/*  583:     */ 
/*  584:     */ 
/*  585:     */ 
/*  586: 852 */       JPanel okcButs = new JPanel();
/*  587: 853 */       okcButs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  588: 854 */       okcButs.setLayout(new GridLayout(1, 4, 5, 5));
/*  589: 855 */       okcButs.add(this.m_OpenBut);
/*  590: 856 */       okcButs.add(this.m_SaveBut);
/*  591: 857 */       okcButs.add(this.m_okBut);
/*  592: 858 */       okcButs.add(this.m_cancelBut);
/*  593: 859 */       add(okcButs, "South");
/*  594: 861 */       if (GenericObjectEditor.this.m_ClassType != null)
/*  595:     */       {
/*  596: 862 */         GenericObjectEditor.this.m_ObjectNames = GenericObjectEditor.this.getClassesFromProperties();
/*  597: 863 */         if (GenericObjectEditor.this.m_Object != null)
/*  598:     */         {
/*  599: 864 */           GenericObjectEditor.this.updateObjectNames();
/*  600: 865 */           updateChildPropertySheet();
/*  601:     */         }
/*  602:     */       }
/*  603:     */     }
/*  604:     */     
/*  605:     */     protected void setCancelButton(boolean flag)
/*  606:     */     {
/*  607: 877 */       if (this.m_cancelBut != null) {
/*  608: 878 */         this.m_cancelBut.setEnabled(flag);
/*  609:     */       }
/*  610:     */     }
/*  611:     */     
/*  612:     */     protected Object openObject()
/*  613:     */     {
/*  614: 889 */       if (this.m_FileChooser == null) {
/*  615: 890 */         createFileChooser();
/*  616:     */       }
/*  617: 892 */       int returnVal = this.m_FileChooser.showOpenDialog(this);
/*  618: 893 */       if (returnVal == 0)
/*  619:     */       {
/*  620: 894 */         File selected = this.m_FileChooser.getSelectedFile();
/*  621:     */         try
/*  622:     */         {
/*  623: 896 */           ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(selected)));
/*  624:     */           
/*  625: 898 */           Object obj = oi.readObject();
/*  626: 899 */           oi.close();
/*  627: 900 */           if (!GenericObjectEditor.this.m_ClassType.isAssignableFrom(obj.getClass())) {
/*  628: 901 */             throw new Exception("Object not of type: " + GenericObjectEditor.this.m_ClassType.getName());
/*  629:     */           }
/*  630: 903 */           return obj;
/*  631:     */         }
/*  632:     */         catch (Exception ex)
/*  633:     */         {
/*  634: 905 */           JOptionPane.showMessageDialog(this, "Couldn't read object: " + selected.getName() + "\n" + ex.getMessage(), "Open object file", 0);
/*  635:     */         }
/*  636:     */       }
/*  637: 910 */       return null;
/*  638:     */     }
/*  639:     */     
/*  640:     */     protected void saveObject(Object object)
/*  641:     */     {
/*  642: 920 */       if (this.m_FileChooser == null) {
/*  643: 921 */         createFileChooser();
/*  644:     */       }
/*  645: 923 */       int returnVal = this.m_FileChooser.showSaveDialog(this);
/*  646: 924 */       if (returnVal == 0)
/*  647:     */       {
/*  648: 925 */         File sFile = this.m_FileChooser.getSelectedFile();
/*  649:     */         try
/*  650:     */         {
/*  651: 927 */           ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(sFile)));
/*  652:     */           
/*  653: 929 */           oo.writeObject(object);
/*  654: 930 */           oo.close();
/*  655:     */         }
/*  656:     */         catch (Exception ex)
/*  657:     */         {
/*  658: 932 */           JOptionPane.showMessageDialog(this, "Couldn't write to file: " + sFile.getName() + "\n" + ex.getMessage(), "Save object", 0);
/*  659:     */         }
/*  660:     */       }
/*  661:     */     }
/*  662:     */     
/*  663:     */     protected void createFileChooser()
/*  664:     */     {
/*  665: 944 */       this.m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  666: 945 */       this.m_FileChooser.setFileSelectionMode(0);
/*  667:     */     }
/*  668:     */     
/*  669:     */     protected Object copyObject(Object source)
/*  670:     */     {
/*  671: 956 */       Object result = null;
/*  672:     */       try
/*  673:     */       {
/*  674: 958 */         result = GenericObjectEditor.makeCopy(source);
/*  675: 959 */         setCancelButton(true);
/*  676:     */       }
/*  677:     */       catch (Exception ex)
/*  678:     */       {
/*  679: 962 */         setCancelButton(false);
/*  680: 963 */         Logger.log(Logger.Level.WARNING, "GenericObjectEditor: Problem making backup object");
/*  681:     */         
/*  682: 965 */         Logger.log(Logger.Level.WARNING, ex);
/*  683:     */       }
/*  684: 967 */       return result;
/*  685:     */     }
/*  686:     */     
/*  687:     */     public void setOkButtonText(String newLabel)
/*  688:     */     {
/*  689: 977 */       this.m_okBut.setText(newLabel);
/*  690:     */     }
/*  691:     */     
/*  692:     */     public void addOkListener(ActionListener a)
/*  693:     */     {
/*  694: 987 */       this.m_okBut.addActionListener(a);
/*  695:     */     }
/*  696:     */     
/*  697:     */     public void addCancelListener(ActionListener a)
/*  698:     */     {
/*  699: 997 */       this.m_cancelBut.addActionListener(a);
/*  700:     */     }
/*  701:     */     
/*  702:     */     public void removeOkListener(ActionListener a)
/*  703:     */     {
/*  704:1007 */       this.m_okBut.removeActionListener(a);
/*  705:     */     }
/*  706:     */     
/*  707:     */     public void removeCancelListener(ActionListener a)
/*  708:     */     {
/*  709:1017 */       this.m_cancelBut.removeActionListener(a);
/*  710:     */     }
/*  711:     */     
/*  712:     */     public void updateChildPropertySheet()
/*  713:     */     {
/*  714:1026 */       String className = "None";
/*  715:1027 */       if (GenericObjectEditor.this.m_Object != null) {
/*  716:1028 */         className = GenericObjectEditor.this.m_Object.getClass().getName();
/*  717:     */       }
/*  718:1030 */       this.m_ClassNameLabel.setText(className);
/*  719:     */       
/*  720:     */ 
/*  721:1033 */       this.m_ChildPropertySheet.setTarget(GenericObjectEditor.this.m_Object);
/*  722:1036 */       if ((getTopLevelAncestor() != null) && ((getTopLevelAncestor() instanceof Window))) {
/*  723:1038 */         ((Window)getTopLevelAncestor()).pack();
/*  724:     */       }
/*  725:     */     }
/*  726:     */   }
/*  727:     */   
/*  728:     */   public GenericObjectEditor()
/*  729:     */   {
/*  730:1047 */     this(false);
/*  731:     */   }
/*  732:     */   
/*  733:     */   public GenericObjectEditor(boolean canChangeClassInDialog)
/*  734:     */   {
/*  735:1057 */     this.m_canChangeClassInDialog = canChangeClassInDialog;
/*  736:1058 */     this.m_History = new GenericObjectEditorHistory();
/*  737:1059 */     ToolTipManager.sharedInstance().setDismissDelay(7000);
/*  738:     */   }
/*  739:     */   
/*  740:     */   public static void registerEditors()
/*  741:     */   {
/*  742:1070 */     if (m_EditorsRegistered) {
/*  743:1071 */       return;
/*  744:     */     }
/*  745:1074 */     Logger.log(Logger.Level.INFO, "---Registering Weka Editors---");
/*  746:     */     
/*  747:1076 */     m_EditorsRegistered = true;
/*  748:     */     Properties props;
/*  749:     */     try
/*  750:     */     {
/*  751:1080 */       props = Utils.readProperties("weka/gui/GUIEditors.props");
/*  752:     */     }
/*  753:     */     catch (Exception e)
/*  754:     */     {
/*  755:1082 */       props = new Properties();
/*  756:1083 */       e.printStackTrace();
/*  757:     */     }
/*  758:1087 */     m_ShowGlobalInfoToolTip = props.getProperty("ShowGlobalInfoToolTip", "true").equals("true");
/*  759:     */     
/*  760:     */ 
/*  761:1090 */     Enumeration<?> enm = props.propertyNames();
/*  762:1091 */     while (enm.hasMoreElements())
/*  763:     */     {
/*  764:1092 */       String name = enm.nextElement().toString();
/*  765:1093 */       String value = props.getProperty(name, "");
/*  766:     */       
/*  767:1095 */       registerEditor(name, value);
/*  768:     */     }
/*  769:     */   }
/*  770:     */   
/*  771:     */   public static void registerEditor(String name, String value)
/*  772:     */   {
/*  773:     */     try
/*  774:     */     {
/*  775:     */       Class<?> cls;
/*  776:     */       Class<?> cls;
/*  777:1105 */       if (name.endsWith("[]"))
/*  778:     */       {
/*  779:1106 */         Class<?> baseCls = Class.forName(name.substring(0, name.indexOf("[]")));
/*  780:1107 */         cls = Array.newInstance(baseCls, 1).getClass();
/*  781:     */       }
/*  782:     */       else
/*  783:     */       {
/*  784:1109 */         cls = Class.forName(name);
/*  785:     */       }
/*  786:1112 */       PropertyEditorManager.registerEditor(cls, Class.forName(value));
/*  787:     */     }
/*  788:     */     catch (Exception e)
/*  789:     */     {
/*  790:1114 */       Logger.log(Logger.Level.WARNING, "Problem registering " + name + "/" + value + ": " + e);
/*  791:     */     }
/*  792:     */   }
/*  793:     */   
/*  794:     */   public void setCanChangeClassInDialog(boolean value)
/*  795:     */   {
/*  796:1125 */     this.m_canChangeClassInDialog = value;
/*  797:     */   }
/*  798:     */   
/*  799:     */   public boolean getCanChangeClassInDialog()
/*  800:     */   {
/*  801:1134 */     return this.m_canChangeClassInDialog;
/*  802:     */   }
/*  803:     */   
/*  804:     */   public Object getBackup()
/*  805:     */   {
/*  806:1143 */     return this.m_Backup;
/*  807:     */   }
/*  808:     */   
/*  809:     */   protected static String getRootFromClass(String clsname, String separator)
/*  810:     */   {
/*  811:1155 */     if (clsname.indexOf(separator) > -1) {
/*  812:1156 */       return clsname.substring(0, clsname.indexOf(separator));
/*  813:     */     }
/*  814:1158 */     return null;
/*  815:     */   }
/*  816:     */   
/*  817:     */   public static Hashtable<String, String> sortClassesByRoot(String classes)
/*  818:     */   {
/*  819:1186 */     if (classes == null) {
/*  820:1187 */       return null;
/*  821:     */     }
/*  822:1190 */     Hashtable<String, Vector<String>> roots = new Hashtable();
/*  823:1191 */     HierarchyPropertyParser hpp = new HierarchyPropertyParser();
/*  824:1192 */     String separator = hpp.getSeperator();
/*  825:     */     
/*  826:     */ 
/*  827:     */ 
/*  828:1196 */     StringTokenizer tok = new StringTokenizer(classes, ", ");
/*  829:1197 */     while (tok.hasMoreElements())
/*  830:     */     {
/*  831:1198 */       String clsname = tok.nextToken();
/*  832:1199 */       String root = getRootFromClass(clsname, separator);
/*  833:1200 */       if (root != null)
/*  834:     */       {
/*  835:     */         Vector<String> list;
/*  836:1205 */         if (!roots.containsKey(root))
/*  837:     */         {
/*  838:1206 */           Vector<String> list = new Vector();
/*  839:1207 */           roots.put(root, list);
/*  840:     */         }
/*  841:     */         else
/*  842:     */         {
/*  843:1209 */           list = (Vector)roots.get(root);
/*  844:     */         }
/*  845:1212 */         list.add(clsname);
/*  846:     */       }
/*  847:     */     }
/*  848:1216 */     Hashtable<String, String> result = new Hashtable();
/*  849:1217 */     Enumeration<String> enm = roots.keys();
/*  850:1218 */     while (enm.hasMoreElements())
/*  851:     */     {
/*  852:1219 */       String root = (String)enm.nextElement();
/*  853:1220 */       Vector<String> list = (Vector)roots.get(root);
/*  854:1221 */       String tmpStr = "";
/*  855:1222 */       for (int i = 0; i < list.size(); i++)
/*  856:     */       {
/*  857:1223 */         if (i > 0) {
/*  858:1224 */           tmpStr = tmpStr + ",";
/*  859:     */         }
/*  860:1226 */         tmpStr = tmpStr + (String)list.get(i);
/*  861:     */       }
/*  862:1228 */       result.put(root, tmpStr);
/*  863:     */     }
/*  864:1231 */     return result;
/*  865:     */   }
/*  866:     */   
/*  867:     */   protected Hashtable<String, HierarchyPropertyParser> getClassesFromProperties()
/*  868:     */   {
/*  869:1242 */     Hashtable<String, HierarchyPropertyParser> hpps = new Hashtable();
/*  870:1243 */     String className = this.m_ClassType.getName();
/*  871:1244 */     Set<String> cls = PluginManager.getPluginNamesOfType(className);
/*  872:1245 */     if (cls == null) {
/*  873:1246 */       return hpps;
/*  874:     */     }
/*  875:1248 */     List<String> toSort = new ArrayList(cls);
/*  876:1249 */     Collections.sort(toSort, new ClassDiscovery.StringCompare());
/*  877:     */     
/*  878:1251 */     StringBuilder b = new StringBuilder();
/*  879:1252 */     for (String s : toSort) {
/*  880:1253 */       b.append(s).append(",");
/*  881:     */     }
/*  882:1255 */     String listS = b.substring(0, b.length() - 1);
/*  883:     */     
/*  884:     */ 
/*  885:1258 */     Hashtable<String, String> typeOptions = sortClassesByRoot(listS);
/*  886:1259 */     if (typeOptions != null) {
/*  887:     */       try
/*  888:     */       {
/*  889:1266 */         Enumeration<String> enm = typeOptions.keys();
/*  890:1267 */         while (enm.hasMoreElements())
/*  891:     */         {
/*  892:1268 */           String root = (String)enm.nextElement();
/*  893:1269 */           String typeOption = (String)typeOptions.get(root);
/*  894:1270 */           HierarchyPropertyParser hpp = new HierarchyPropertyParser();
/*  895:1271 */           hpp.build(typeOption, ", ");
/*  896:1272 */           hpps.put(root, hpp);
/*  897:     */         }
/*  898:     */       }
/*  899:     */       catch (Exception ex)
/*  900:     */       {
/*  901:1275 */         Logger.log(Logger.Level.WARNING, "Invalid property: " + typeOptions);
/*  902:     */       }
/*  903:     */     }
/*  904:1279 */     return hpps;
/*  905:     */   }
/*  906:     */   
/*  907:     */   protected void updateObjectNames()
/*  908:     */   {
/*  909:1288 */     if (this.m_ObjectNames == null) {
/*  910:1289 */       this.m_ObjectNames = getClassesFromProperties();
/*  911:     */     }
/*  912:1292 */     if (this.m_Object != null)
/*  913:     */     {
/*  914:1293 */       String className = this.m_Object.getClass().getName();
/*  915:1294 */       String root = getRootFromClass(className, new HierarchyPropertyParser().getSeperator());
/*  916:     */       
/*  917:1296 */       HierarchyPropertyParser hpp = (HierarchyPropertyParser)this.m_ObjectNames.get(root);
/*  918:1297 */       if ((hpp != null) && 
/*  919:1298 */         (!hpp.contains(className))) {
/*  920:1299 */         hpp.add(className);
/*  921:     */       }
/*  922:     */     }
/*  923:     */   }
/*  924:     */   
/*  925:     */   public void setEnabled(boolean newVal)
/*  926:     */   {
/*  927:1313 */     if (newVal != this.m_Enabled) {
/*  928:1314 */       this.m_Enabled = newVal;
/*  929:     */     }
/*  930:     */   }
/*  931:     */   
/*  932:     */   public void setClassType(Class<?> type)
/*  933:     */   {
/*  934:1325 */     this.m_ClassType = type;
/*  935:1326 */     this.m_ObjectNames = getClassesFromProperties();
/*  936:     */   }
/*  937:     */   
/*  938:     */   public void setDefaultValue()
/*  939:     */   {
/*  940:1335 */     if (this.m_ClassType == null)
/*  941:     */     {
/*  942:1336 */       Logger.log(Logger.Level.WARNING, "No ClassType set up for GenericObjectEditor!!");
/*  943:     */       
/*  944:1338 */       return;
/*  945:     */     }
/*  946:1341 */     Hashtable<String, HierarchyPropertyParser> hpps = getClassesFromProperties();
/*  947:1342 */     HierarchyPropertyParser hpp = null;
/*  948:1343 */     Enumeration<HierarchyPropertyParser> enm = hpps.elements();
/*  949:     */     try
/*  950:     */     {
/*  951:1346 */       while (enm.hasMoreElements())
/*  952:     */       {
/*  953:1347 */         hpp = (HierarchyPropertyParser)enm.nextElement();
/*  954:1348 */         if (hpp.depth() > 0)
/*  955:     */         {
/*  956:1349 */           hpp.goToRoot();
/*  957:1350 */           while (!hpp.isLeafReached()) {
/*  958:1351 */             hpp.goToChild(0);
/*  959:     */           }
/*  960:1354 */           String defaultValue = hpp.fullValue();
/*  961:1355 */           setValue(Class.forName(defaultValue).newInstance());
/*  962:     */         }
/*  963:     */       }
/*  964:     */     }
/*  965:     */     catch (Exception ex)
/*  966:     */     {
/*  967:1359 */       Logger.log(Logger.Level.WARNING, "Problem loading the first class: " + hpp.fullValue());
/*  968:     */       
/*  969:1361 */       ex.printStackTrace();
/*  970:     */     }
/*  971:     */   }
/*  972:     */   
/*  973:     */   public void setValue(Object o)
/*  974:     */   {
/*  975:1374 */     if (this.m_ClassType == null)
/*  976:     */     {
/*  977:1375 */       Logger.log(Logger.Level.WARNING, "No ClassType set up for GenericObjectEditor!!");
/*  978:     */       
/*  979:1377 */       return;
/*  980:     */     }
/*  981:1379 */     if (!this.m_ClassType.isAssignableFrom(o.getClass()))
/*  982:     */     {
/*  983:1380 */       Logger.log(Logger.Level.WARNING, "setValue object not of correct type!");
/*  984:     */       
/*  985:1382 */       return;
/*  986:     */     }
/*  987:1385 */     setObject(o);
/*  988:1387 */     if (this.m_EditorComponent != null) {
/*  989:1388 */       this.m_EditorComponent.repaint();
/*  990:     */     }
/*  991:1391 */     updateObjectNames();
/*  992:     */   }
/*  993:     */   
/*  994:     */   protected void setObject(Object c)
/*  995:     */   {
/*  996:     */     boolean trueChange;
/*  997:     */     boolean trueChange;
/*  998:1403 */     if (getValue() != null) {
/*  999:1404 */       trueChange = !c.equals(getValue());
/* 1000:     */     } else {
/* 1001:1406 */       trueChange = true;
/* 1002:     */     }
/* 1003:1409 */     this.m_Backup = this.m_Object;
/* 1004:     */     
/* 1005:1411 */     this.m_Object = c;
/* 1006:1413 */     if (this.m_EditorComponent != null) {
/* 1007:1414 */       this.m_EditorComponent.updateChildPropertySheet();
/* 1008:     */     }
/* 1009:1416 */     if (trueChange) {
/* 1010:1417 */       this.m_Support.firePropertyChange("", null, null);
/* 1011:     */     }
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public Object getValue()
/* 1015:     */   {
/* 1016:1429 */     Object result = null;
/* 1017:     */     try
/* 1018:     */     {
/* 1019:1431 */       result = makeCopy(this.m_Object);
/* 1020:     */     }
/* 1021:     */     catch (Exception ex)
/* 1022:     */     {
/* 1023:1433 */       ex.printStackTrace();
/* 1024:     */     }
/* 1025:1435 */     return result;
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public String getJavaInitializationString()
/* 1029:     */   {
/* 1030:1449 */     return "new " + this.m_Object.getClass().getName() + "()";
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public boolean isPaintable()
/* 1034:     */   {
/* 1035:1460 */     return true;
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public void paintValue(Graphics gfx, Rectangle box)
/* 1039:     */   {
/* 1040:1472 */     if (this.m_Enabled)
/* 1041:     */     {
/* 1042:     */       String rep;
/* 1043:1474 */       if (this.m_Object != null)
/* 1044:     */       {
/* 1045:     */         String rep;
/* 1046:1475 */         if ((this.m_Object instanceof CustomDisplayStringProvider))
/* 1047:     */         {
/* 1048:1476 */           rep = ((CustomDisplayStringProvider)this.m_Object).toDisplay();
/* 1049:     */         }
/* 1050:     */         else
/* 1051:     */         {
/* 1052:1478 */           String rep = this.m_Object.getClass().getName();
/* 1053:1479 */           int dotPos = rep.lastIndexOf('.');
/* 1054:1480 */           if (dotPos != -1) {
/* 1055:1481 */             rep = rep.substring(dotPos + 1);
/* 1056:     */           }
/* 1057:     */         }
/* 1058:     */       }
/* 1059:     */       else
/* 1060:     */       {
/* 1061:1485 */         rep = "None";
/* 1062:     */       }
/* 1063:1487 */       Font originalFont = gfx.getFont();
/* 1064:1488 */       gfx.setFont(originalFont.deriveFont(1));
/* 1065:     */       
/* 1066:1490 */       FontMetrics fm = gfx.getFontMetrics();
/* 1067:1491 */       int vpad = box.height - fm.getHeight();
/* 1068:1492 */       gfx.drawString(rep, 2, fm.getAscent() + vpad);
/* 1069:1493 */       int repwidth = fm.stringWidth(rep);
/* 1070:     */       
/* 1071:1495 */       gfx.setFont(originalFont);
/* 1072:1496 */       if (((this.m_Object instanceof OptionHandler)) && (!(this.m_Object instanceof CustomDisplayStringProvider))) {
/* 1073:1498 */         gfx.drawString(" " + Utils.joinOptions(((OptionHandler)this.m_Object).getOptions()), repwidth + 2, fm.getAscent() + vpad);
/* 1074:     */       }
/* 1075:     */     }
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public String getAsText()
/* 1079:     */   {
/* 1080:1513 */     return null;
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public void setAsText(String text)
/* 1084:     */   {
/* 1085:1526 */     throw new IllegalArgumentException(text);
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public String[] getTags()
/* 1089:     */   {
/* 1090:1537 */     return null;
/* 1091:     */   }
/* 1092:     */   
/* 1093:     */   public boolean supportsCustomEditor()
/* 1094:     */   {
/* 1095:1548 */     return true;
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   public Component getCustomEditor()
/* 1099:     */   {
/* 1100:1559 */     if (this.m_EditorComponent == null) {
/* 1101:1560 */       this.m_EditorComponent = new GOEPanel();
/* 1102:     */     }
/* 1103:1562 */     return this.m_EditorComponent;
/* 1104:     */   }
/* 1105:     */   
/* 1106:     */   public void addPropertyChangeListener(PropertyChangeListener l)
/* 1107:     */   {
/* 1108:1573 */     this.m_Support.addPropertyChangeListener(l);
/* 1109:     */   }
/* 1110:     */   
/* 1111:     */   public void removePropertyChangeListener(PropertyChangeListener l)
/* 1112:     */   {
/* 1113:1584 */     this.m_Support.removePropertyChangeListener(l);
/* 1114:     */   }
/* 1115:     */   
/* 1116:     */   public JPanel getCustomPanel()
/* 1117:     */   {
/* 1118:1594 */     final JButton chooseButton = createChooseClassButton();
/* 1119:1595 */     this.m_ObjectPropertyPanel = new PropertyPanel(this, true);
/* 1120:     */     
/* 1121:1597 */     JPanel customPanel = new JPanel()
/* 1122:     */     {
/* 1123:     */       private static final long serialVersionUID = 1024049543672124980L;
/* 1124:     */       
/* 1125:     */       public void setEnabled(boolean enabled)
/* 1126:     */       {
/* 1127:1604 */         super.setEnabled(enabled);
/* 1128:1605 */         chooseButton.setEnabled(enabled);
/* 1129:     */       }
/* 1130:1607 */     };
/* 1131:1608 */     customPanel.setLayout(new BorderLayout());
/* 1132:1609 */     customPanel.add(chooseButton, "West");
/* 1133:1610 */     customPanel.add(this.m_ObjectPropertyPanel, "Center");
/* 1134:1611 */     return customPanel;
/* 1135:     */   }
/* 1136:     */   
/* 1137:     */   protected JButton createChooseClassButton()
/* 1138:     */   {
/* 1139:1622 */     JButton setButton = new JButton("Choose");
/* 1140:     */     
/* 1141:     */ 
/* 1142:     */ 
/* 1143:1626 */     setButton.addActionListener(new ActionListener()
/* 1144:     */     {
/* 1145:     */       public void actionPerformed(ActionEvent e)
/* 1146:     */       {
/* 1147:1630 */         JPopupMenu popup = GenericObjectEditor.this.getChooseClassPopupMenu();
/* 1148:1633 */         if ((e.getSource() instanceof Component))
/* 1149:     */         {
/* 1150:1634 */           Component comp = (Component)e.getSource();
/* 1151:1635 */           popup.show(comp, comp.getX(), comp.getY());
/* 1152:1636 */           popup.pack();
/* 1153:1637 */           popup.repaint();
/* 1154:     */         }
/* 1155:     */       }
/* 1156:1641 */     });
/* 1157:1642 */     return setButton;
/* 1158:     */   }
/* 1159:     */   
/* 1160:     */   protected String getClassnameFromPath(TreePath path)
/* 1161:     */   {
/* 1162:1652 */     StringBuffer classname = new StringBuffer();
/* 1163:     */     
/* 1164:     */ 
/* 1165:1655 */     int start = 0;
/* 1166:1656 */     if (this.m_ObjectNames.size() > 1) {
/* 1167:1657 */       start = 1;
/* 1168:     */     }
/* 1169:1660 */     for (int i = start; i < path.getPathCount(); i++)
/* 1170:     */     {
/* 1171:1661 */       if (i > start) {
/* 1172:1662 */         classname.append(".");
/* 1173:     */       }
/* 1174:1664 */       classname.append((String)((GOETreeNode)path.getPathComponent(i)).getUserObject());
/* 1175:     */     }
/* 1176:1668 */     return classname.toString();
/* 1177:     */   }
/* 1178:     */   
/* 1179:     */   public JPopupMenu getChooseClassPopupMenu()
/* 1180:     */   {
/* 1181:1678 */     updateObjectNames();
/* 1182:     */     
/* 1183:     */ 
/* 1184:1681 */     this.m_treeNodeOfCurrentObject = null;
/* 1185:1682 */     final JTree tree = createTree(this.m_ObjectNames);
/* 1186:1683 */     if (this.m_treeNodeOfCurrentObject != null) {
/* 1187:1684 */       tree.setSelectionPath(new TreePath(this.m_treeNodeOfCurrentObject.getPath()));
/* 1188:     */     }
/* 1189:1686 */     tree.getSelectionModel().setSelectionMode(1);
/* 1190:     */     
/* 1191:     */ 
/* 1192:     */ 
/* 1193:1690 */     final JPopupMenu popup = new JTreePopupMenu(tree);
/* 1194:     */     
/* 1195:     */ 
/* 1196:1693 */     tree.addTreeSelectionListener(new TreeSelectionListener()
/* 1197:     */     {
/* 1198:     */       public void valueChanged(TreeSelectionEvent e)
/* 1199:     */       {
/* 1200:1696 */         GenericObjectEditor.GOETreeNode node = (GenericObjectEditor.GOETreeNode)tree.getLastSelectedPathComponent();
/* 1201:1698 */         if (node == null) {
/* 1202:1699 */           return;
/* 1203:     */         }
/* 1204:1702 */         if (node.isLeaf())
/* 1205:     */         {
/* 1206:1703 */           GenericObjectEditor.this.classSelected(GenericObjectEditor.this.getClassnameFromPath(tree.getSelectionPath()));
/* 1207:1704 */           popup.setVisible(false);
/* 1208:     */         }
/* 1209:     */       }
/* 1210:1708 */     });
/* 1211:1709 */     return popup;
/* 1212:     */   }
/* 1213:     */   
/* 1214:     */   protected JTree createTree(Hashtable<String, HierarchyPropertyParser> hpps)
/* 1215:     */   {
/* 1216:     */     GOETreeNode superRoot;
/* 1217:     */     GOETreeNode superRoot;
/* 1218:1723 */     if (hpps.size() > 1) {
/* 1219:1724 */       superRoot = new GOETreeNode("root");
/* 1220:     */     } else {
/* 1221:1726 */       superRoot = null;
/* 1222:     */     }
/* 1223:1729 */     Enumeration<HierarchyPropertyParser> enm = hpps.elements();
/* 1224:1730 */     while (enm.hasMoreElements())
/* 1225:     */     {
/* 1226:1731 */       HierarchyPropertyParser hpp = (HierarchyPropertyParser)enm.nextElement();
/* 1227:1732 */       hpp.goToRoot();
/* 1228:1733 */       GOETreeNode root = new GOETreeNode(hpp.getValue());
/* 1229:1734 */       addChildrenToTree(root, hpp);
/* 1230:1736 */       if (superRoot == null) {
/* 1231:1737 */         superRoot = root;
/* 1232:     */       } else {
/* 1233:1739 */         superRoot.add(root);
/* 1234:     */       }
/* 1235:     */     }
/* 1236:1743 */     JTree tree = new JTree(superRoot)
/* 1237:     */     {
/* 1238:     */       private static final long serialVersionUID = 6991903188102450549L;
/* 1239:     */       
/* 1240:     */       public String getToolTipText(MouseEvent e)
/* 1241:     */       {
/* 1242:1750 */         if (getRowForLocation(e.getX(), e.getY()) == -1) {
/* 1243:1751 */           return null;
/* 1244:     */         }
/* 1245:1753 */         TreePath currPath = getPathForLocation(e.getX(), e.getY());
/* 1246:1754 */         if ((currPath.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 1247:     */         {
/* 1248:1755 */           DefaultMutableTreeNode node = (DefaultMutableTreeNode)currPath.getLastPathComponent();
/* 1249:1758 */           if (node.isLeaf()) {
/* 1250:1760 */             return ((GenericObjectEditor.GOETreeNode)node).getToolTipText();
/* 1251:     */           }
/* 1252:     */         }
/* 1253:1763 */         return null;
/* 1254:     */       }
/* 1255:1765 */     };
/* 1256:1766 */     tree.setToolTipText("");
/* 1257:     */     
/* 1258:1768 */     return tree;
/* 1259:     */   }
/* 1260:     */   
/* 1261:     */   protected void addChildrenToTree(GOETreeNode tree, HierarchyPropertyParser hpp)
/* 1262:     */   {
/* 1263:     */     try
/* 1264:     */     {
/* 1265:1782 */       for (int i = 0; i < hpp.numChildren(); i++)
/* 1266:     */       {
/* 1267:1783 */         hpp.goToChild(i);
/* 1268:1784 */         GOETreeNode child = new GOETreeNode(hpp.getValue());
/* 1269:1785 */         if ((this.m_Object != null) && (this.m_Object.getClass().getName().equals(hpp.fullValue()))) {
/* 1270:1787 */           this.m_treeNodeOfCurrentObject = child;
/* 1271:     */         }
/* 1272:1789 */         tree.add(child);
/* 1273:1791 */         if ((hpp.isLeafReached()) && (m_ShowGlobalInfoToolTip))
/* 1274:     */         {
/* 1275:1792 */           String algName = hpp.fullValue();
/* 1276:     */           try
/* 1277:     */           {
/* 1278:1794 */             Object alg = Class.forName(algName).newInstance();
/* 1279:1795 */             String toolTip = Utils.getGlobalInfo(alg, true);
/* 1280:1796 */             if (toolTip != null) {
/* 1281:1797 */               child.setToolTipText(toolTip);
/* 1282:     */             }
/* 1283:     */           }
/* 1284:     */           catch (Exception ex) {}
/* 1285:     */         }
/* 1286:1803 */         addChildrenToTree(child, hpp);
/* 1287:1804 */         hpp.goToParent();
/* 1288:     */       }
/* 1289:     */     }
/* 1290:     */     catch (Exception e)
/* 1291:     */     {
/* 1292:1807 */       e.printStackTrace();
/* 1293:     */     }
/* 1294:     */   }
/* 1295:     */   
/* 1296:     */   protected void classSelected(String className)
/* 1297:     */   {
/* 1298:     */     try
/* 1299:     */     {
/* 1300:1819 */       if ((this.m_Object != null) && (this.m_Object.getClass().getName().equals(className))) {
/* 1301:1820 */         return;
/* 1302:     */       }
/* 1303:1823 */       setValue(Class.forName(className).newInstance());
/* 1304:1825 */       if (this.m_EditorComponent != null) {
/* 1305:1826 */         this.m_EditorComponent.updateChildPropertySheet();
/* 1306:     */       }
/* 1307:     */     }
/* 1308:     */     catch (Exception ex)
/* 1309:     */     {
/* 1310:1829 */       JOptionPane.showMessageDialog(null, "Could not create an example of\n" + className + "\n" + "from the current classpath", "Class load failed", 0);
/* 1311:     */       
/* 1312:     */ 
/* 1313:1832 */       ex.printStackTrace();
/* 1314:     */       try
/* 1315:     */       {
/* 1316:1834 */         if (this.m_Backup != null) {
/* 1317:1835 */           setValue(this.m_Backup);
/* 1318:     */         } else {
/* 1319:1837 */           setDefaultValue();
/* 1320:     */         }
/* 1321:     */       }
/* 1322:     */       catch (Exception e)
/* 1323:     */       {
/* 1324:1840 */         Logger.log(Logger.Level.WARNING, ex.getMessage());
/* 1325:1841 */         ex.printStackTrace();
/* 1326:     */       }
/* 1327:     */     }
/* 1328:     */   }
/* 1329:     */   
/* 1330:     */   public void setCapabilitiesFilter(Capabilities value)
/* 1331:     */   {
/* 1332:1852 */     this.m_CapabilitiesFilter = new Capabilities(null);
/* 1333:1853 */     this.m_CapabilitiesFilter.assign(value);
/* 1334:     */   }
/* 1335:     */   
/* 1336:     */   public Capabilities getCapabilitiesFilter()
/* 1337:     */   {
/* 1338:1862 */     return this.m_CapabilitiesFilter;
/* 1339:     */   }
/* 1340:     */   
/* 1341:     */   public void removeCapabilitiesFilter()
/* 1342:     */   {
/* 1343:1869 */     this.m_CapabilitiesFilter = null;
/* 1344:     */   }
/* 1345:     */   
/* 1346:     */   public static Object makeCopy(Object source)
/* 1347:     */     throws Exception
/* 1348:     */   {
/* 1349:1880 */     SerializedObject so = new SerializedObject(source);
/* 1350:1881 */     Object result = so.getObject();
/* 1351:1882 */     return result;
/* 1352:     */   }
/* 1353:     */   
/* 1354:     */   public static Vector<String> getClassnames(String property)
/* 1355:     */   {
/* 1356:1893 */     Set<String> r = PluginManager.getPluginNamesOfType(property);
/* 1357:     */     
/* 1358:1895 */     Vector<String> result = new Vector();
/* 1359:1896 */     if (r != null) {
/* 1360:1897 */       result.addAll(r);
/* 1361:     */     }
/* 1362:1899 */     Collections.sort(result, new ClassDiscovery.StringCompare());
/* 1363:     */     
/* 1364:1901 */     return result;
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   public GenericObjectEditorHistory getHistory()
/* 1368:     */   {
/* 1369:1910 */     return this.m_History;
/* 1370:     */   }
/* 1371:     */   
/* 1372:     */   public static void main(String[] args)
/* 1373:     */   {
/* 1374:     */     try
/* 1375:     */     {
/* 1376:1921 */       registerEditors();
/* 1377:1922 */       GenericObjectEditor ce = new GenericObjectEditor(true);
/* 1378:1923 */       ce.setClassType(Classifier.class);
/* 1379:1924 */       Object initial = new ZeroR();
/* 1380:1925 */       if (args.length > 0)
/* 1381:     */       {
/* 1382:1926 */         ce.setClassType(Class.forName(args[0]));
/* 1383:1927 */         if (args.length > 1)
/* 1384:     */         {
/* 1385:1928 */           initial = Class.forName(args[1]).newInstance();
/* 1386:1929 */           ce.setValue(initial);
/* 1387:     */         }
/* 1388:     */         else
/* 1389:     */         {
/* 1390:1931 */           ce.setDefaultValue();
/* 1391:     */         }
/* 1392:     */       }
/* 1393:     */       else
/* 1394:     */       {
/* 1395:1934 */         ce.setValue(initial);
/* 1396:     */       }
/* 1397:1937 */       PropertyDialog pd = new PropertyDialog((Frame)null, ce, 100, 100);
/* 1398:1938 */       pd.addWindowListener(new WindowAdapter()
/* 1399:     */       {
/* 1400:     */         public void windowClosing(WindowEvent e)
/* 1401:     */         {
/* 1402:1941 */           PropertyEditor pe = ((PropertyDialog)e.getSource()).getEditor();
/* 1403:1942 */           Object c = pe.getValue();
/* 1404:1943 */           String options = "";
/* 1405:1944 */           if ((c instanceof OptionHandler)) {
/* 1406:1945 */             options = Utils.joinOptions(((OptionHandler)c).getOptions());
/* 1407:     */           }
/* 1408:1947 */           System.out.println(c.getClass().getName() + " " + options);
/* 1409:1948 */           System.exit(0);
/* 1410:     */         }
/* 1411:1950 */       });
/* 1412:1951 */       pd.setVisible(true);
/* 1413:     */     }
/* 1414:     */     catch (Exception ex)
/* 1415:     */     {
/* 1416:1953 */       ex.printStackTrace();
/* 1417:1954 */       System.err.println(ex.getMessage());
/* 1418:     */     }
/* 1419:     */   }
/* 1420:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GenericObjectEditor
 * JD-Core Version:    0.7.0.1
 */