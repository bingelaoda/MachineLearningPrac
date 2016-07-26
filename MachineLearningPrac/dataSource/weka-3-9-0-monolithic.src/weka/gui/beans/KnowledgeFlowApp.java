/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BasicStroke;
/*    4:     */ import java.awt.BorderLayout;
/*    5:     */ import java.awt.Color;
/*    6:     */ import java.awt.Component;
/*    7:     */ import java.awt.Container;
/*    8:     */ import java.awt.Cursor;
/*    9:     */ import java.awt.Dialog.ModalityType;
/*   10:     */ import java.awt.Dimension;
/*   11:     */ import java.awt.FlowLayout;
/*   12:     */ import java.awt.Font;
/*   13:     */ import java.awt.FontMetrics;
/*   14:     */ import java.awt.Frame;
/*   15:     */ import java.awt.Graphics;
/*   16:     */ import java.awt.Graphics2D;
/*   17:     */ import java.awt.GridLayout;
/*   18:     */ import java.awt.Image;
/*   19:     */ import java.awt.Menu;
/*   20:     */ import java.awt.MenuItem;
/*   21:     */ import java.awt.Point;
/*   22:     */ import java.awt.PopupMenu;
/*   23:     */ import java.awt.Rectangle;
/*   24:     */ import java.awt.RenderingHints;
/*   25:     */ import java.awt.Toolkit;
/*   26:     */ import java.awt.Window;
/*   27:     */ import java.awt.event.ActionEvent;
/*   28:     */ import java.awt.event.ActionListener;
/*   29:     */ import java.awt.event.KeyAdapter;
/*   30:     */ import java.awt.event.KeyEvent;
/*   31:     */ import java.awt.event.MouseAdapter;
/*   32:     */ import java.awt.event.MouseEvent;
/*   33:     */ import java.awt.event.MouseMotionAdapter;
/*   34:     */ import java.awt.event.WindowAdapter;
/*   35:     */ import java.awt.event.WindowEvent;
/*   36:     */ import java.awt.image.BufferedImage;
/*   37:     */ import java.beans.BeanDescriptor;
/*   38:     */ import java.beans.BeanInfo;
/*   39:     */ import java.beans.Beans;
/*   40:     */ import java.beans.Customizer;
/*   41:     */ import java.beans.EventSetDescriptor;
/*   42:     */ import java.beans.IntrospectionException;
/*   43:     */ import java.beans.Introspector;
/*   44:     */ import java.beans.PropertyChangeEvent;
/*   45:     */ import java.beans.PropertyChangeListener;
/*   46:     */ import java.beans.beancontext.BeanContextChild;
/*   47:     */ import java.beans.beancontext.BeanContextSupport;
/*   48:     */ import java.io.BufferedWriter;
/*   49:     */ import java.io.File;
/*   50:     */ import java.io.FileInputStream;
/*   51:     */ import java.io.FileOutputStream;
/*   52:     */ import java.io.FileWriter;
/*   53:     */ import java.io.IOException;
/*   54:     */ import java.io.InputStream;
/*   55:     */ import java.io.InputStreamReader;
/*   56:     */ import java.io.LineNumberReader;
/*   57:     */ import java.io.ObjectInputStream;
/*   58:     */ import java.io.ObjectOutputStream;
/*   59:     */ import java.io.OutputStream;
/*   60:     */ import java.io.OutputStreamWriter;
/*   61:     */ import java.io.PrintStream;
/*   62:     */ import java.io.Reader;
/*   63:     */ import java.io.Serializable;
/*   64:     */ import java.io.StringReader;
/*   65:     */ import java.io.StringWriter;
/*   66:     */ import java.lang.annotation.Annotation;
/*   67:     */ import java.net.URL;
/*   68:     */ import java.text.SimpleDateFormat;
/*   69:     */ import java.util.ArrayList;
/*   70:     */ import java.util.Date;
/*   71:     */ import java.util.Enumeration;
/*   72:     */ import java.util.HashMap;
/*   73:     */ import java.util.Hashtable;
/*   74:     */ import java.util.Iterator;
/*   75:     */ import java.util.LinkedHashMap;
/*   76:     */ import java.util.List;
/*   77:     */ import java.util.Map;
/*   78:     */ import java.util.Map.Entry;
/*   79:     */ import java.util.Properties;
/*   80:     */ import java.util.Set;
/*   81:     */ import java.util.SortedSet;
/*   82:     */ import java.util.Stack;
/*   83:     */ import java.util.StringTokenizer;
/*   84:     */ import java.util.TreeMap;
/*   85:     */ import java.util.TreeSet;
/*   86:     */ import java.util.Vector;
/*   87:     */ import javax.swing.AbstractAction;
/*   88:     */ import javax.swing.AbstractButton;
/*   89:     */ import javax.swing.Action;
/*   90:     */ import javax.swing.ActionMap;
/*   91:     */ import javax.swing.BorderFactory;
/*   92:     */ import javax.swing.ButtonGroup;
/*   93:     */ import javax.swing.ButtonModel;
/*   94:     */ import javax.swing.Icon;
/*   95:     */ import javax.swing.ImageIcon;
/*   96:     */ import javax.swing.InputMap;
/*   97:     */ import javax.swing.JButton;
/*   98:     */ import javax.swing.JCheckBox;
/*   99:     */ import javax.swing.JComponent;
/*  100:     */ import javax.swing.JDialog;
/*  101:     */ import javax.swing.JFileChooser;
/*  102:     */ import javax.swing.JFrame;
/*  103:     */ import javax.swing.JLabel;
/*  104:     */ import javax.swing.JOptionPane;
/*  105:     */ import javax.swing.JPanel;
/*  106:     */ import javax.swing.JScrollBar;
/*  107:     */ import javax.swing.JScrollPane;
/*  108:     */ import javax.swing.JSplitPane;
/*  109:     */ import javax.swing.JTabbedPane;
/*  110:     */ import javax.swing.JTable;
/*  111:     */ import javax.swing.JTextArea;
/*  112:     */ import javax.swing.JTextField;
/*  113:     */ import javax.swing.JToggleButton;
/*  114:     */ import javax.swing.JToolBar;
/*  115:     */ import javax.swing.JTree;
/*  116:     */ import javax.swing.JWindow;
/*  117:     */ import javax.swing.KeyStroke;
/*  118:     */ import javax.swing.event.ChangeEvent;
/*  119:     */ import javax.swing.event.ChangeListener;
/*  120:     */ import javax.swing.filechooser.FileFilter;
/*  121:     */ import javax.swing.plaf.basic.BasicButtonUI;
/*  122:     */ import javax.swing.tree.DefaultMutableTreeNode;
/*  123:     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*  124:     */ import javax.swing.tree.DefaultTreeModel;
/*  125:     */ import javax.swing.tree.DefaultTreeSelectionModel;
/*  126:     */ import javax.swing.tree.MutableTreeNode;
/*  127:     */ import javax.swing.tree.TreeModel;
/*  128:     */ import javax.swing.tree.TreeNode;
/*  129:     */ import javax.swing.tree.TreePath;
/*  130:     */ import weka.core.Attribute;
/*  131:     */ import weka.core.Copyright;
/*  132:     */ import weka.core.Environment;
/*  133:     */ import weka.core.EnvironmentHandler;
/*  134:     */ import weka.core.Instances;
/*  135:     */ import weka.core.Memory;
/*  136:     */ import weka.core.PluginManager;
/*  137:     */ import weka.core.SerializedObject;
/*  138:     */ import weka.core.Utils;
/*  139:     */ import weka.core.WekaEnumeration;
/*  140:     */ import weka.core.WekaPackageManager;
/*  141:     */ import weka.core.converters.FileSourcedConverter;
/*  142:     */ import weka.core.logging.Logger;
/*  143:     */ import weka.core.logging.Logger.Level;
/*  144:     */ import weka.core.xml.KOML;
/*  145:     */ import weka.core.xml.XStream;
/*  146:     */ import weka.gui.AttributeSelectionPanel;
/*  147:     */ import weka.gui.ExtensionFileFilter;
/*  148:     */ import weka.gui.GenericObjectEditor;
/*  149:     */ import weka.gui.GenericPropertiesCreator;
/*  150:     */ import weka.gui.HierarchyPropertyParser;
/*  151:     */ import weka.gui.LookAndFeel;
/*  152:     */ import weka.gui.beans.xml.XMLBeans;
/*  153:     */ import weka.gui.visualize.PrintablePanel;
/*  154:     */ 
/*  155:     */ public class KnowledgeFlowApp
/*  156:     */   extends JPanel
/*  157:     */   implements PropertyChangeListener, BeanCustomizer.ModifyListener
/*  158:     */ {
/*  159:     */   private static final long serialVersionUID = -7064906770289728431L;
/*  160: 176 */   protected Map<String, String> m_pluginPerspectiveLookup = new HashMap();
/*  161: 180 */   protected Map<String, KFPerspective> m_perspectiveCache = new HashMap();
/*  162: 187 */   private static Vector<Vector<?>> TOOLBARS = new Vector();
/*  163:     */   FontMetrics m_fontM;
/*  164:     */   protected static final int NONE = 0;
/*  165:     */   protected static final int MOVING = 1;
/*  166:     */   protected static final int CONNECTING = 2;
/*  167:     */   protected static final int ADDING = 3;
/*  168:     */   protected static final int SELECTING = 4;
/*  169:     */   protected static final int PASTING = 5;
/*  170:     */   
/*  171:     */   public static void addToPluginBeanProps(File beanPropsFile)
/*  172:     */     throws Exception
/*  173:     */   {
/*  174: 196 */     BeansProperties.addToPluginBeanProps(beanPropsFile);
/*  175:     */   }
/*  176:     */   
/*  177:     */   public static void removeFromPluginBeanProps(File beanPropsFile)
/*  178:     */     throws Exception
/*  179:     */   {
/*  180: 207 */     BeansProperties.removeFromPluginBeanProps(beanPropsFile);
/*  181:     */   }
/*  182:     */   
/*  183:     */   public static synchronized void loadProperties() {}
/*  184:     */   
/*  185:     */   public static void reInitialize()
/*  186:     */   {
/*  187: 218 */     loadProperties();
/*  188: 219 */     init();
/*  189:     */   }
/*  190:     */   
/*  191:     */   private static void init()
/*  192:     */   {
/*  193: 226 */     Logger.log(Logger.Level.INFO, "[KnowledgeFlow] Initializing KF...");
/*  194: 230 */     if (!XMLBeans.SUPPRESS_PROPERTY_WARNINGS.contains("visual.iconPath")) {
/*  195: 231 */       XMLBeans.SUPPRESS_PROPERTY_WARNINGS.add("visual.iconPath");
/*  196:     */     }
/*  197: 233 */     if (!XMLBeans.SUPPRESS_PROPERTY_WARNINGS.contains("visual.animatedIconPath")) {
/*  198: 235 */       XMLBeans.SUPPRESS_PROPERTY_WARNINGS.add("visual.animatedIconPath");
/*  199:     */     }
/*  200:     */     try
/*  201:     */     {
/*  202: 239 */       TOOLBARS = new Vector();
/*  203:     */       
/*  204: 241 */       TreeMap<Integer, Object> wrapList = new TreeMap();
/*  205: 242 */       Properties GEOProps = GenericPropertiesCreator.getGlobalOutputProperties();
/*  206: 245 */       if (GEOProps == null)
/*  207:     */       {
/*  208: 246 */         GenericPropertiesCreator creator = new GenericPropertiesCreator();
/*  209: 248 */         if (creator.useDynamic())
/*  210:     */         {
/*  211: 249 */           creator.execute(false);
/*  212:     */           
/*  213:     */ 
/*  214:     */ 
/*  215:     */ 
/*  216:     */ 
/*  217: 255 */           GEOProps = creator.getOutputProperties();
/*  218:     */         }
/*  219:     */         else
/*  220:     */         {
/*  221: 258 */           GEOProps = Utils.readProperties("weka/gui/GenericObjectEditor.props");
/*  222:     */         }
/*  223:     */       }
/*  224: 261 */       Enumeration<?> en = GEOProps.propertyNames();
/*  225: 262 */       while (en.hasMoreElements())
/*  226:     */       {
/*  227: 263 */         String geoKey = (String)en.nextElement();
/*  228:     */         
/*  229:     */ 
/*  230:     */ 
/*  231: 267 */         String beanCompName = BeansProperties.BEAN_PROPERTIES.getProperty(geoKey);
/*  232: 269 */         if (beanCompName != null)
/*  233:     */         {
/*  234: 272 */           Vector<Object> newV = new Vector();
/*  235:     */           
/*  236: 274 */           String toolBarNameAlias = BeansProperties.BEAN_PROPERTIES.getProperty(geoKey + ".alias");
/*  237:     */           
/*  238: 276 */           String toolBarName = toolBarNameAlias != null ? toolBarNameAlias : geoKey.substring(geoKey.lastIndexOf('.') + 1, geoKey.length());
/*  239:     */           
/*  240:     */ 
/*  241:     */ 
/*  242: 280 */           String order = BeansProperties.BEAN_PROPERTIES.getProperty(geoKey + ".order");
/*  243:     */           
/*  244: 282 */           Integer intOrder = order != null ? new Integer(order) : new Integer(0);
/*  245:     */           
/*  246:     */ 
/*  247:     */ 
/*  248: 286 */           newV.addElement(toolBarName);
/*  249:     */           
/*  250: 288 */           newV.addElement(beanCompName);
/*  251:     */           
/*  252:     */ 
/*  253: 291 */           String rootPackage = geoKey.substring(0, geoKey.lastIndexOf('.'));
/*  254:     */           
/*  255: 293 */           newV.addElement(rootPackage);
/*  256:     */           
/*  257:     */ 
/*  258: 296 */           String wekaAlgs = GEOProps.getProperty(geoKey);
/*  259:     */           
/*  260: 298 */           Hashtable<String, String> roots = GenericObjectEditor.sortClassesByRoot(wekaAlgs);
/*  261:     */           
/*  262: 300 */           Hashtable<String, HierarchyPropertyParser> hpps = new Hashtable();
/*  263:     */           
/*  264: 302 */           Enumeration<String> enm = roots.keys();
/*  265: 303 */           while (enm.hasMoreElements())
/*  266:     */           {
/*  267: 304 */             String root = (String)enm.nextElement();
/*  268: 305 */             String classes = (String)roots.get(root);
/*  269: 306 */             HierarchyPropertyParser hpp = new HierarchyPropertyParser();
/*  270:     */             
/*  271: 308 */             hpp.build(classes, ", ");
/*  272:     */             
/*  273: 310 */             hpps.put(root, hpp);
/*  274:     */           }
/*  275: 322 */           newV.addElement(hpps);
/*  276:     */           
/*  277: 324 */           StringTokenizer st = new StringTokenizer(wekaAlgs, ", ");
/*  278: 325 */           while (st.hasMoreTokens())
/*  279:     */           {
/*  280: 326 */             String current = st.nextToken().trim();
/*  281: 327 */             newV.addElement(current);
/*  282:     */           }
/*  283: 329 */           wrapList.put(intOrder, newV);
/*  284:     */         }
/*  285:     */       }
/*  286: 333 */       Iterator<Integer> keysetIt = wrapList.keySet().iterator();
/*  287: 334 */       while (keysetIt.hasNext())
/*  288:     */       {
/*  289: 335 */         Integer key = (Integer)keysetIt.next();
/*  290:     */         
/*  291: 337 */         Vector<Object> newV = (Vector)wrapList.get(key);
/*  292: 338 */         if (newV != null) {
/*  293: 339 */           TOOLBARS.addElement(newV);
/*  294:     */         }
/*  295:     */       }
/*  296:     */     }
/*  297:     */     catch (Exception ex)
/*  298:     */     {
/*  299: 343 */       JOptionPane.showMessageDialog(null, "Could not read a configuration file for the generic objecte editor. An example file is included with the Weka distribution.\nThis file should be named \"GenericObjectEditor.props\" and\nshould be placed either in your user home (which is set\nto \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "KnowledgeFlow", 0);
/*  300:     */     }
/*  301:     */     try
/*  302:     */     {
/*  303: 354 */       String standardToolBarNames = BeansProperties.BEAN_PROPERTIES.getProperty("weka.gui.beans.KnowledgeFlow.standardToolBars");
/*  304:     */       
/*  305: 356 */       StringTokenizer st = new StringTokenizer(standardToolBarNames, ", ");
/*  306: 357 */       while (st.hasMoreTokens())
/*  307:     */       {
/*  308: 358 */         String tempBarName = st.nextToken().trim();
/*  309:     */         
/*  310: 360 */         Vector<String> newV = new Vector();
/*  311:     */         
/*  312: 362 */         newV.addElement(tempBarName);
/*  313:     */         
/*  314:     */ 
/*  315: 365 */         newV.addElement("null");
/*  316: 366 */         String toolBarContents = BeansProperties.BEAN_PROPERTIES.getProperty("weka.gui.beans.KnowledgeFlow." + tempBarName);
/*  317:     */         
/*  318: 368 */         StringTokenizer st2 = new StringTokenizer(toolBarContents, ", ");
/*  319: 369 */         while (st2.hasMoreTokens())
/*  320:     */         {
/*  321: 370 */           String tempBeanName = st2.nextToken().trim();
/*  322: 371 */           newV.addElement(tempBeanName);
/*  323:     */         }
/*  324: 373 */         TOOLBARS.addElement(newV);
/*  325:     */       }
/*  326:     */     }
/*  327:     */     catch (Exception ex)
/*  328:     */     {
/*  329: 376 */       JOptionPane.showMessageDialog(null, ex.getMessage(), "KnowledgeFlow", 0);
/*  330:     */     }
/*  331:     */   }
/*  332:     */   
/*  333:     */   protected class BeanIconRenderer
/*  334:     */     extends DefaultTreeCellRenderer
/*  335:     */   {
/*  336:     */     private static final long serialVersionUID = -4488876734500244945L;
/*  337:     */     
/*  338:     */     protected BeanIconRenderer() {}
/*  339:     */     
/*  340:     */     public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*  341:     */     {
/*  342: 389 */       super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
/*  343: 392 */       if (leaf)
/*  344:     */       {
/*  345: 393 */         Object userO = ((DefaultMutableTreeNode)value).getUserObject();
/*  346: 394 */         if ((userO instanceof KnowledgeFlowApp.JTreeLeafDetails))
/*  347:     */         {
/*  348: 395 */           Icon i = ((KnowledgeFlowApp.JTreeLeafDetails)userO).getIcon();
/*  349: 396 */           if (i != null) {
/*  350: 397 */             setIcon(i);
/*  351:     */           }
/*  352:     */         }
/*  353:     */       }
/*  354: 401 */       return this;
/*  355:     */     }
/*  356:     */   }
/*  357:     */   
/*  358:     */   protected class InvisibleNode
/*  359:     */     extends DefaultMutableTreeNode
/*  360:     */   {
/*  361:     */     private static final long serialVersionUID = -9064396835384819887L;
/*  362:     */     protected boolean m_isVisible;
/*  363:     */     
/*  364:     */     public InvisibleNode()
/*  365:     */     {
/*  366: 414 */       this(null);
/*  367:     */     }
/*  368:     */     
/*  369:     */     public InvisibleNode(Object userObject)
/*  370:     */     {
/*  371: 418 */       this(userObject, true, true);
/*  372:     */     }
/*  373:     */     
/*  374:     */     public InvisibleNode(Object userObject, boolean allowsChildren, boolean isVisible)
/*  375:     */     {
/*  376: 423 */       super(allowsChildren);
/*  377: 424 */       this.m_isVisible = isVisible;
/*  378:     */     }
/*  379:     */     
/*  380:     */     public TreeNode getChildAt(int index, boolean filterIsActive)
/*  381:     */     {
/*  382: 428 */       if (!filterIsActive) {
/*  383: 429 */         return super.getChildAt(index);
/*  384:     */       }
/*  385: 431 */       if (this.children == null) {
/*  386: 432 */         throw new ArrayIndexOutOfBoundsException("node has no children");
/*  387:     */       }
/*  388: 435 */       int realIndex = -1;
/*  389: 436 */       int visibleIndex = -1;
/*  390:     */       
/*  391: 438 */       Enumeration<InvisibleNode> e = new WekaEnumeration(this.children);
/*  392: 440 */       while (e.hasMoreElements())
/*  393:     */       {
/*  394: 441 */         InvisibleNode node = (InvisibleNode)e.nextElement();
/*  395: 442 */         if (node.isVisible()) {
/*  396: 443 */           visibleIndex++;
/*  397:     */         }
/*  398: 445 */         realIndex++;
/*  399: 446 */         if (visibleIndex == index) {
/*  400: 447 */           return (TreeNode)this.children.elementAt(realIndex);
/*  401:     */         }
/*  402:     */       }
/*  403: 451 */       throw new ArrayIndexOutOfBoundsException("index unmatched");
/*  404:     */     }
/*  405:     */     
/*  406:     */     public int getChildCount(boolean filterIsActive)
/*  407:     */     {
/*  408: 455 */       if (!filterIsActive) {
/*  409: 456 */         return super.getChildCount();
/*  410:     */       }
/*  411: 458 */       if (this.children == null) {
/*  412: 459 */         return 0;
/*  413:     */       }
/*  414: 462 */       int count = 0;
/*  415:     */       
/*  416: 464 */       Enumeration<InvisibleNode> e = new WekaEnumeration(this.children);
/*  417: 466 */       while (e.hasMoreElements())
/*  418:     */       {
/*  419: 467 */         InvisibleNode node = (InvisibleNode)e.nextElement();
/*  420: 468 */         if (node.isVisible()) {
/*  421: 469 */           count++;
/*  422:     */         }
/*  423:     */       }
/*  424: 473 */       return count;
/*  425:     */     }
/*  426:     */     
/*  427:     */     public void setVisible(boolean visible)
/*  428:     */     {
/*  429: 477 */       this.m_isVisible = visible;
/*  430:     */     }
/*  431:     */     
/*  432:     */     public boolean isVisible()
/*  433:     */     {
/*  434: 481 */       return this.m_isVisible;
/*  435:     */     }
/*  436:     */   }
/*  437:     */   
/*  438:     */   protected class InvisibleTreeModel
/*  439:     */     extends DefaultTreeModel
/*  440:     */   {
/*  441:     */     private static final long serialVersionUID = 6940101211275068260L;
/*  442:     */     protected boolean m_filterIsActive;
/*  443:     */     
/*  444:     */     public InvisibleTreeModel(TreeNode root)
/*  445:     */     {
/*  446: 494 */       this(root, false);
/*  447:     */     }
/*  448:     */     
/*  449:     */     public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren)
/*  450:     */     {
/*  451: 498 */       this(root, false, false);
/*  452:     */     }
/*  453:     */     
/*  454:     */     public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren, boolean filterIsActive)
/*  455:     */     {
/*  456: 503 */       super(asksAllowsChildren);
/*  457: 504 */       this.m_filterIsActive = filterIsActive;
/*  458:     */     }
/*  459:     */     
/*  460:     */     public void activateFilter(boolean newValue)
/*  461:     */     {
/*  462: 508 */       this.m_filterIsActive = newValue;
/*  463:     */     }
/*  464:     */     
/*  465:     */     public boolean isActivatedFilter()
/*  466:     */     {
/*  467: 512 */       return this.m_filterIsActive;
/*  468:     */     }
/*  469:     */     
/*  470:     */     public Object getChild(Object parent, int index)
/*  471:     */     {
/*  472: 517 */       if ((this.m_filterIsActive) && 
/*  473: 518 */         ((parent instanceof KnowledgeFlowApp.InvisibleNode))) {
/*  474: 519 */         return ((KnowledgeFlowApp.InvisibleNode)parent).getChildAt(index, this.m_filterIsActive);
/*  475:     */       }
/*  476: 522 */       return ((TreeNode)parent).getChildAt(index);
/*  477:     */     }
/*  478:     */     
/*  479:     */     public int getChildCount(Object parent)
/*  480:     */     {
/*  481: 527 */       if ((this.m_filterIsActive) && 
/*  482: 528 */         ((parent instanceof KnowledgeFlowApp.InvisibleNode))) {
/*  483: 529 */         return ((KnowledgeFlowApp.InvisibleNode)parent).getChildCount(this.m_filterIsActive);
/*  484:     */       }
/*  485: 532 */       return ((TreeNode)parent).getChildCount();
/*  486:     */     }
/*  487:     */   }
/*  488:     */   
/*  489:     */   protected class JTreeLeafDetails
/*  490:     */     implements Serializable
/*  491:     */   {
/*  492:     */     private static final long serialVersionUID = 6197221540272931626L;
/*  493: 548 */     protected String m_fullyQualifiedCompName = "";
/*  494: 554 */     protected String m_leafLabel = "";
/*  495: 557 */     protected String m_wekaAlgoName = "";
/*  496: 560 */     protected transient Icon m_scaledIcon = null;
/*  497: 564 */     protected Vector<Object> m_metaBean = null;
/*  498: 567 */     protected boolean m_isMeta = false;
/*  499: 570 */     protected String m_toolTipText = null;
/*  500:     */     
/*  501:     */     protected JTreeLeafDetails(String fullName, Icon icon)
/*  502:     */     {
/*  503: 579 */       this(fullName, "", icon);
/*  504:     */     }
/*  505:     */     
/*  506:     */     protected JTreeLeafDetails(Vector<Object> name, Icon serializedMeta)
/*  507:     */     {
/*  508: 593 */       this(name, "", icon);
/*  509:     */       
/*  510:     */ 
/*  511: 596 */       this.m_metaBean = serializedMeta;
/*  512: 597 */       this.m_isMeta = true;
/*  513: 598 */       this.m_toolTipText = "Hold down shift and click to remove";
/*  514:     */     }
/*  515:     */     
/*  516:     */     protected JTreeLeafDetails(String fullName, String wekaAlgoName, Icon icon)
/*  517:     */     {
/*  518: 612 */       this.m_fullyQualifiedCompName = fullName;
/*  519: 613 */       this.m_wekaAlgoName = wekaAlgoName;
/*  520: 614 */       this.m_leafLabel = (wekaAlgoName.length() > 0 ? wekaAlgoName : this.m_fullyQualifiedCompName);
/*  521: 616 */       if (this.m_leafLabel.lastIndexOf('.') > 0) {
/*  522: 617 */         this.m_leafLabel = this.m_leafLabel.substring(this.m_leafLabel.lastIndexOf('.') + 1, this.m_leafLabel.length());
/*  523:     */       }
/*  524: 620 */       this.m_scaledIcon = icon;
/*  525:     */     }
/*  526:     */     
/*  527:     */     protected String getToolTipText()
/*  528:     */     {
/*  529: 629 */       return this.m_toolTipText;
/*  530:     */     }
/*  531:     */     
/*  532:     */     protected void setToolTipText(String tipText)
/*  533:     */     {
/*  534: 633 */       this.m_toolTipText = tipText;
/*  535:     */     }
/*  536:     */     
/*  537:     */     public String toString()
/*  538:     */     {
/*  539: 643 */       return this.m_leafLabel;
/*  540:     */     }
/*  541:     */     
/*  542:     */     protected Icon getIcon()
/*  543:     */     {
/*  544: 652 */       return this.m_scaledIcon;
/*  545:     */     }
/*  546:     */     
/*  547:     */     protected void setIcon(Icon icon)
/*  548:     */     {
/*  549: 661 */       this.m_scaledIcon = icon;
/*  550:     */     }
/*  551:     */     
/*  552:     */     protected boolean isWrappedAlgorithm()
/*  553:     */     {
/*  554: 671 */       return (this.m_wekaAlgoName != null) && (this.m_wekaAlgoName.length() > 0);
/*  555:     */     }
/*  556:     */     
/*  557:     */     protected boolean isMetaBean()
/*  558:     */     {
/*  559: 680 */       return this.m_metaBean != null;
/*  560:     */     }
/*  561:     */     
/*  562:     */     protected Vector<Object> getMetaBean()
/*  563:     */     {
/*  564: 693 */       return this.m_metaBean;
/*  565:     */     }
/*  566:     */     
/*  567:     */     protected void instantiateBean()
/*  568:     */     {
/*  569:     */       try
/*  570:     */       {
/*  571: 701 */         if (isMetaBean())
/*  572:     */         {
/*  573: 704 */           KnowledgeFlowApp.this.m_toolBarBean = this.m_metaBean.get(1);
/*  574:     */         }
/*  575:     */         else
/*  576:     */         {
/*  577: 706 */           KnowledgeFlowApp.this.m_toolBarBean = Beans.instantiate(KnowledgeFlowApp.this.getClass().getClassLoader(), this.m_fullyQualifiedCompName);
/*  578: 708 */           if (isWrappedAlgorithm())
/*  579:     */           {
/*  580: 709 */             Object algo = Beans.instantiate(KnowledgeFlowApp.this.getClass().getClassLoader(), this.m_wekaAlgoName);
/*  581:     */             
/*  582: 711 */             ((WekaWrapper)KnowledgeFlowApp.this.m_toolBarBean).setWrappedAlgorithm(algo);
/*  583:     */           }
/*  584:     */         }
/*  585: 715 */         KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(1));
/*  586:     */         
/*  587: 717 */         KnowledgeFlowApp.this.m_mode = 3;
/*  588: 718 */         KnowledgeFlowApp.this.m_pasteB.setEnabled(false);
/*  589:     */       }
/*  590:     */       catch (Exception ex)
/*  591:     */       {
/*  592: 721 */         System.err.println("Problem instantiating bean \"" + this.m_fullyQualifiedCompName + "\" (JTreeLeafDetails.instantiateBean()");
/*  593:     */         
/*  594:     */ 
/*  595: 724 */         ex.printStackTrace();
/*  596:     */       }
/*  597:     */     }
/*  598:     */   }
/*  599:     */   
/*  600:     */   protected class BeanLayout
/*  601:     */     extends PrintablePanel
/*  602:     */   {
/*  603:     */     private static final long serialVersionUID = -146377012429662757L;
/*  604:     */     
/*  605:     */     protected BeanLayout() {}
/*  606:     */     
/*  607:     */     public void paintComponent(Graphics gx)
/*  608:     */     {
/*  609: 744 */       double lz = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/*  610: 745 */       ((Graphics2D)gx).scale(lz, lz);
/*  611: 746 */       if (KnowledgeFlowApp.this.m_layoutZoom < 100) {
/*  612: 747 */         ((Graphics2D)gx).setStroke(new BasicStroke(2.0F));
/*  613:     */       }
/*  614: 749 */       super.paintComponent(gx);
/*  615:     */       
/*  616: 751 */       ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  617:     */       
/*  618:     */ 
/*  619: 754 */       ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
/*  620:     */       
/*  621:     */ 
/*  622: 757 */       BeanInstance.paintLabels(gx, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  623: 758 */       BeanConnection.paintConnections(gx, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  624: 761 */       if (KnowledgeFlowApp.this.m_mode == 2) {
/*  625: 762 */         gx.drawLine(KnowledgeFlowApp.this.m_startX, KnowledgeFlowApp.this.m_startY, KnowledgeFlowApp.this.m_oldX, KnowledgeFlowApp.this.m_oldY);
/*  626: 763 */       } else if (KnowledgeFlowApp.this.m_mode == 4) {
/*  627: 764 */         gx.drawRect(KnowledgeFlowApp.this.m_startX < KnowledgeFlowApp.this.m_oldX ? KnowledgeFlowApp.this.m_startX : KnowledgeFlowApp.this.m_oldX, KnowledgeFlowApp.this.m_startY < KnowledgeFlowApp.this.m_oldY ? KnowledgeFlowApp.this.m_startY : KnowledgeFlowApp.this.m_oldY, Math.abs(KnowledgeFlowApp.this.m_oldX - KnowledgeFlowApp.this.m_startX), Math.abs(KnowledgeFlowApp.this.m_oldY - KnowledgeFlowApp.this.m_startY));
/*  628:     */       }
/*  629:     */     }
/*  630:     */     
/*  631:     */     public void doLayout()
/*  632:     */     {
/*  633: 772 */       super.doLayout();
/*  634: 773 */       Vector<Object> comps = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  635: 775 */       for (int i = 0; i < comps.size(); i++)
/*  636:     */       {
/*  637: 776 */         BeanInstance bi = (BeanInstance)comps.elementAt(i);
/*  638: 777 */         JComponent c = (JComponent)bi.getBean();
/*  639: 778 */         Dimension d = c.getPreferredSize();
/*  640: 779 */         c.setBounds(bi.getX(), bi.getY(), d.width, d.height);
/*  641: 780 */         c.revalidate();
/*  642:     */       }
/*  643:     */     }
/*  644:     */   }
/*  645:     */   
/*  646:     */   public static abstract interface KFPerspective
/*  647:     */   {
/*  648:     */     public abstract void setInstances(Instances paramInstances)
/*  649:     */       throws Exception;
/*  650:     */     
/*  651:     */     public abstract boolean acceptsInstances();
/*  652:     */     
/*  653:     */     public abstract String getPerspectiveTitle();
/*  654:     */     
/*  655:     */     public abstract String getPerspectiveTipText();
/*  656:     */     
/*  657:     */     public abstract Icon getPerspectiveIcon();
/*  658:     */     
/*  659:     */     public abstract void setActive(boolean paramBoolean);
/*  660:     */     
/*  661:     */     public abstract void setLoaded(boolean paramBoolean);
/*  662:     */     
/*  663:     */     public abstract void setMainKFPerspective(KnowledgeFlowApp.MainKFPerspective paramMainKFPerspective);
/*  664:     */   }
/*  665:     */   
/*  666:     */   public class MainKFPerspective
/*  667:     */     extends JPanel
/*  668:     */     implements KnowledgeFlowApp.KFPerspective
/*  669:     */   {
/*  670:     */     private static final long serialVersionUID = 7666381888012259527L;
/*  671: 866 */     protected JTabbedPane m_flowTabs = new JTabbedPane();
/*  672: 869 */     protected List<KnowledgeFlowApp.BeanLayout> m_beanLayouts = new ArrayList();
/*  673: 872 */     protected List<Integer> m_zoomSettings = new ArrayList();
/*  674: 875 */     protected List<KnowledgeFlowApp.KFLogPanel> m_logPanels = new ArrayList();
/*  675: 878 */     protected List<Environment> m_environmentSettings = new ArrayList();
/*  676: 882 */     protected List<File> m_filePaths = new ArrayList();
/*  677: 885 */     protected List<Boolean> m_editedList = new ArrayList();
/*  678: 888 */     protected List<Boolean> m_executingList = new ArrayList();
/*  679: 891 */     protected List<KnowledgeFlowApp.RunThread> m_executionThreads = new ArrayList();
/*  680: 894 */     protected List<Vector<Object>> m_selectedBeans = new ArrayList();
/*  681: 898 */     protected List<Stack<File>> m_undoBufferList = new ArrayList();
/*  682: 900 */     protected Map<String, DefaultMutableTreeNode> m_nodeTextIndex = new LinkedHashMap();
/*  683:     */     
/*  684:     */     public void setActive(boolean active) {}
/*  685:     */     
/*  686:     */     public void setLoaded(boolean loaded) {}
/*  687:     */     
/*  688:     */     public void setMainKFPerspective(MainKFPerspective main) {}
/*  689:     */     
/*  690:     */     public JTabbedPane getTabbedPane()
/*  691:     */     {
/*  692: 919 */       return this.m_flowTabs;
/*  693:     */     }
/*  694:     */     
/*  695:     */     public synchronized int getNumTabs()
/*  696:     */     {
/*  697: 923 */       return this.m_flowTabs.getTabCount();
/*  698:     */     }
/*  699:     */     
/*  700:     */     public synchronized String getTabTitle(int index)
/*  701:     */     {
/*  702: 927 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  703: 928 */         return this.m_flowTabs.getTitleAt(index);
/*  704:     */       }
/*  705: 930 */       return null;
/*  706:     */     }
/*  707:     */     
/*  708:     */     public synchronized int getCurrentTabIndex()
/*  709:     */     {
/*  710: 934 */       return this.m_flowTabs.getSelectedIndex();
/*  711:     */     }
/*  712:     */     
/*  713:     */     public synchronized KnowledgeFlowApp.KFLogPanel getCurrentLogPanel()
/*  714:     */     {
/*  715: 938 */       if (getCurrentTabIndex() >= 0) {
/*  716: 939 */         return (KnowledgeFlowApp.KFLogPanel)this.m_logPanels.get(getCurrentTabIndex());
/*  717:     */       }
/*  718: 941 */       return null;
/*  719:     */     }
/*  720:     */     
/*  721:     */     public synchronized KnowledgeFlowApp.KFLogPanel getLogPanel(int index)
/*  722:     */     {
/*  723: 945 */       if ((index >= 0) && (index < this.m_logPanels.size())) {
/*  724: 946 */         return (KnowledgeFlowApp.KFLogPanel)this.m_logPanels.get(index);
/*  725:     */       }
/*  726: 948 */       return null;
/*  727:     */     }
/*  728:     */     
/*  729:     */     public synchronized KnowledgeFlowApp.BeanLayout getCurrentBeanLayout()
/*  730:     */     {
/*  731: 952 */       if (getCurrentTabIndex() >= 0) {
/*  732: 953 */         return (KnowledgeFlowApp.BeanLayout)this.m_beanLayouts.get(getCurrentTabIndex());
/*  733:     */       }
/*  734: 955 */       return null;
/*  735:     */     }
/*  736:     */     
/*  737:     */     public synchronized KnowledgeFlowApp.BeanLayout getBeanLayout(int index)
/*  738:     */     {
/*  739: 959 */       if ((index >= 0) && (index < this.m_logPanels.size())) {
/*  740: 960 */         return (KnowledgeFlowApp.BeanLayout)this.m_beanLayouts.get(getCurrentTabIndex());
/*  741:     */       }
/*  742: 962 */       return null;
/*  743:     */     }
/*  744:     */     
/*  745:     */     public synchronized int getCurrentZoomSetting()
/*  746:     */     {
/*  747: 966 */       if (getCurrentTabIndex() >= 0) {
/*  748: 967 */         return ((Integer)this.m_zoomSettings.get(getCurrentTabIndex())).intValue();
/*  749:     */       }
/*  750: 971 */       return 100;
/*  751:     */     }
/*  752:     */     
/*  753:     */     public synchronized int getZoomSetting(int index)
/*  754:     */     {
/*  755: 975 */       if ((index >= 0) && (index < this.m_zoomSettings.size())) {
/*  756: 976 */         return ((Integer)this.m_zoomSettings.get(index)).intValue();
/*  757:     */       }
/*  758: 980 */       return 100;
/*  759:     */     }
/*  760:     */     
/*  761:     */     public synchronized void setCurrentZoomSetting(int z)
/*  762:     */     {
/*  763: 984 */       if (getNumTabs() > 0) {
/*  764: 985 */         setZoomSetting(getCurrentTabIndex(), z);
/*  765:     */       }
/*  766:     */     }
/*  767:     */     
/*  768:     */     public synchronized void setZoomSetting(int index, int z)
/*  769:     */     {
/*  770: 990 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  771: 991 */         this.m_zoomSettings.set(index, new Integer(z));
/*  772:     */       }
/*  773:     */     }
/*  774:     */     
/*  775:     */     public synchronized void setActiveTab(int index)
/*  776:     */     {
/*  777: 996 */       if ((index < getNumTabs()) && (index >= 0))
/*  778:     */       {
/*  779: 997 */         this.m_flowTabs.setSelectedIndex(index);
/*  780:     */         
/*  781:     */ 
/*  782:1000 */         KnowledgeFlowApp.this.m_logPanel = ((KnowledgeFlowApp.KFLogPanel)this.m_logPanels.get(index));
/*  783:1001 */         KnowledgeFlowApp.this.m_beanLayout = ((KnowledgeFlowApp.BeanLayout)this.m_beanLayouts.get(index));
/*  784:1002 */         KnowledgeFlowApp.this.m_layoutZoom = ((Integer)this.m_zoomSettings.get(index)).intValue();
/*  785:1003 */         KnowledgeFlowApp.this.m_flowEnvironment = ((Environment)this.m_environmentSettings.get(index));
/*  786:     */         
/*  787:1005 */         KnowledgeFlowApp.this.m_saveB.setEnabled(!getExecuting());
/*  788:1006 */         KnowledgeFlowApp.this.m_saveBB.setEnabled(!getExecuting());
/*  789:1007 */         KnowledgeFlowApp.this.m_playB.setEnabled(!getExecuting());
/*  790:1008 */         KnowledgeFlowApp.this.m_playBB.setEnabled(!getExecuting());
/*  791:1009 */         KnowledgeFlowApp.this.m_saveB.setEnabled(!getExecuting());
/*  792:1010 */         KnowledgeFlowApp.this.m_saveBB.setEnabled(!getExecuting());
/*  793:     */         
/*  794:1012 */         KnowledgeFlowApp.this.m_zoomOutB.setEnabled(!getExecuting());
/*  795:1013 */         KnowledgeFlowApp.this.m_zoomInB.setEnabled(!getExecuting());
/*  796:1014 */         if (KnowledgeFlowApp.this.m_layoutZoom == 50) {
/*  797:1015 */           KnowledgeFlowApp.this.m_zoomOutB.setEnabled(false);
/*  798:     */         }
/*  799:1017 */         if (KnowledgeFlowApp.this.m_layoutZoom == 200) {
/*  800:1018 */           KnowledgeFlowApp.this.m_zoomInB.setEnabled(false);
/*  801:     */         }
/*  802:1021 */         KnowledgeFlowApp.this.m_groupB.setEnabled(false);
/*  803:1022 */         if ((getSelectedBeans().size() > 0) && (!getExecuting()))
/*  804:     */         {
/*  805:1024 */           Vector<Object> selected = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/*  806:     */           
/*  807:     */ 
/*  808:1027 */           Vector<Object> inputs = BeanConnection.inputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  809:     */           
/*  810:1029 */           Vector<Object> outputs = BeanConnection.outputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  811:1032 */           if (KnowledgeFlowApp.this.groupable(selected, inputs, outputs)) {
/*  812:1033 */             KnowledgeFlowApp.this.m_groupB.setEnabled(true);
/*  813:     */           }
/*  814:     */         }
/*  815:1037 */         KnowledgeFlowApp.this.m_cutB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  816:1038 */         KnowledgeFlowApp.this.m_copyB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  817:1039 */         KnowledgeFlowApp.this.m_deleteB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  818:1040 */         KnowledgeFlowApp.this.m_selectAllB.setEnabled((BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(getCurrentTabIndex()) }).size() > 0) && (!getExecuting()));
/*  819:     */         
/*  820:     */ 
/*  821:1043 */         KnowledgeFlowApp.this.m_pasteB.setEnabled((KnowledgeFlowApp.this.m_pasteBuffer != null) && (KnowledgeFlowApp.this.m_pasteBuffer.length() > 0) && (!getExecuting()));
/*  822:     */         
/*  823:     */ 
/*  824:1046 */         KnowledgeFlowApp.this.m_stopB.setEnabled(getExecuting());
/*  825:1047 */         KnowledgeFlowApp.this.m_undoB.setEnabled((!getExecuting()) && (getUndoBuffer().size() > 0));
/*  826:     */       }
/*  827:     */     }
/*  828:     */     
/*  829:     */     public synchronized void setExecuting(boolean executing)
/*  830:     */     {
/*  831:1052 */       if (getNumTabs() > 0) {
/*  832:1053 */         setExecuting(getCurrentTabIndex(), executing);
/*  833:     */       }
/*  834:     */     }
/*  835:     */     
/*  836:     */     public synchronized void setExecuting(int index, boolean executing)
/*  837:     */     {
/*  838:1058 */       if ((index < getNumTabs()) && (index >= 0))
/*  839:     */       {
/*  840:1059 */         this.m_executingList.set(index, new Boolean(executing));
/*  841:1060 */         ((KnowledgeFlowApp.CloseableTabTitle)this.m_flowTabs.getTabComponentAt(index)).setButtonEnabled(!executing);
/*  842:     */         
/*  843:     */ 
/*  844:1063 */         KnowledgeFlowApp.this.m_saveB.setEnabled(!getExecuting());
/*  845:1064 */         KnowledgeFlowApp.this.m_saveBB.setEnabled(!getExecuting());
/*  846:1065 */         KnowledgeFlowApp.this.m_playB.setEnabled(!getExecuting());
/*  847:1066 */         KnowledgeFlowApp.this.m_playBB.setEnabled(!getExecuting());
/*  848:1067 */         KnowledgeFlowApp.this.m_stopB.setEnabled(getExecuting());
/*  849:     */         
/*  850:1069 */         KnowledgeFlowApp.this.m_groupB.setEnabled(false);
/*  851:1070 */         if ((getSelectedBeans().size() > 0) && (!getExecuting()))
/*  852:     */         {
/*  853:1072 */           Vector<Object> selected = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/*  854:     */           
/*  855:     */ 
/*  856:1075 */           Vector<Object> inputs = BeanConnection.inputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  857:     */           
/*  858:1077 */           Vector<Object> outputs = BeanConnection.outputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/*  859:1080 */           if (KnowledgeFlowApp.this.groupable(selected, inputs, outputs)) {
/*  860:1081 */             KnowledgeFlowApp.this.m_groupB.setEnabled(true);
/*  861:     */           }
/*  862:     */         }
/*  863:1085 */         KnowledgeFlowApp.this.m_cutB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  864:1086 */         KnowledgeFlowApp.this.m_deleteB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  865:1087 */         KnowledgeFlowApp.this.m_selectAllB.setEnabled((BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(getCurrentTabIndex()) }).size() > 0) && (!getExecuting()));
/*  866:     */         
/*  867:     */ 
/*  868:1090 */         KnowledgeFlowApp.this.m_copyB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/*  869:1091 */         KnowledgeFlowApp.this.m_pasteB.setEnabled((KnowledgeFlowApp.this.m_pasteBuffer != null) && (KnowledgeFlowApp.this.m_pasteBuffer.length() > 0) && (!getExecuting()));
/*  870:     */         
/*  871:     */ 
/*  872:1094 */         KnowledgeFlowApp.this.m_undoB.setEnabled((!getExecuting()) && (getUndoBuffer().size() > 0));
/*  873:     */       }
/*  874:     */     }
/*  875:     */     
/*  876:     */     public synchronized boolean getExecuting()
/*  877:     */     {
/*  878:1099 */       return getExecuting(getCurrentTabIndex());
/*  879:     */     }
/*  880:     */     
/*  881:     */     public synchronized boolean getExecuting(int index)
/*  882:     */     {
/*  883:1103 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  884:1104 */         return ((Boolean)this.m_executingList.get(index)).booleanValue();
/*  885:     */       }
/*  886:1106 */       return false;
/*  887:     */     }
/*  888:     */     
/*  889:     */     public synchronized void setExecutionThread(KnowledgeFlowApp.RunThread execution)
/*  890:     */     {
/*  891:1110 */       if (getNumTabs() > 0) {
/*  892:1111 */         setExecutionThread(getCurrentTabIndex(), execution);
/*  893:     */       }
/*  894:     */     }
/*  895:     */     
/*  896:     */     public synchronized void setExecutionThread(int index, KnowledgeFlowApp.RunThread execution)
/*  897:     */     {
/*  898:1116 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  899:1117 */         this.m_executionThreads.set(index, execution);
/*  900:     */       }
/*  901:     */     }
/*  902:     */     
/*  903:     */     public synchronized KnowledgeFlowApp.RunThread getExecutionThread()
/*  904:     */     {
/*  905:1122 */       return getExecutionThread(getCurrentTabIndex());
/*  906:     */     }
/*  907:     */     
/*  908:     */     public synchronized KnowledgeFlowApp.RunThread getExecutionThread(int index)
/*  909:     */     {
/*  910:1126 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  911:1127 */         return (KnowledgeFlowApp.RunThread)this.m_executionThreads.get(index);
/*  912:     */       }
/*  913:1129 */       return null;
/*  914:     */     }
/*  915:     */     
/*  916:     */     public synchronized File getFlowFile()
/*  917:     */     {
/*  918:1133 */       if (getNumTabs() > 0) {
/*  919:1134 */         return getFlowFile(getCurrentTabIndex());
/*  920:     */       }
/*  921:1136 */       return null;
/*  922:     */     }
/*  923:     */     
/*  924:     */     public synchronized File getFlowFile(int index)
/*  925:     */     {
/*  926:1140 */       if ((index >= 0) && (index < getNumTabs())) {
/*  927:1141 */         return (File)this.m_filePaths.get(index);
/*  928:     */       }
/*  929:1144 */       return null;
/*  930:     */     }
/*  931:     */     
/*  932:     */     public synchronized void setFlowFile(File flowFile)
/*  933:     */     {
/*  934:1148 */       if (getNumTabs() > 0) {
/*  935:1149 */         setFlowFile(getCurrentTabIndex(), flowFile);
/*  936:     */       }
/*  937:     */     }
/*  938:     */     
/*  939:     */     public synchronized void setFlowFile(int index, File flowFile)
/*  940:     */     {
/*  941:1154 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  942:1155 */         this.m_filePaths.set(index, flowFile);
/*  943:     */       }
/*  944:     */     }
/*  945:     */     
/*  946:     */     public synchronized void setTabTitle(String title)
/*  947:     */     {
/*  948:1160 */       if (getNumTabs() > 0) {
/*  949:1161 */         setTabTitle(getCurrentTabIndex(), title);
/*  950:     */       }
/*  951:     */     }
/*  952:     */     
/*  953:     */     public synchronized void setTabTitle(int index, String title)
/*  954:     */     {
/*  955:1166 */       if ((index < getNumTabs()) && (index >= 0))
/*  956:     */       {
/*  957:1167 */         this.m_flowTabs.setTitleAt(index, title);
/*  958:1168 */         ((KnowledgeFlowApp.CloseableTabTitle)this.m_flowTabs.getTabComponentAt(index)).revalidate();
/*  959:     */       }
/*  960:     */     }
/*  961:     */     
/*  962:     */     public synchronized void setEditedStatus(boolean status)
/*  963:     */     {
/*  964:1174 */       if (getNumTabs() > 0)
/*  965:     */       {
/*  966:1175 */         int current = getCurrentTabIndex();
/*  967:1176 */         setEditedStatus(current, status);
/*  968:     */       }
/*  969:     */     }
/*  970:     */     
/*  971:     */     public synchronized void setEditedStatus(int index, boolean status)
/*  972:     */     {
/*  973:1181 */       if ((index < getNumTabs()) && (index >= 0))
/*  974:     */       {
/*  975:1182 */         Boolean newStatus = new Boolean(status);
/*  976:1183 */         this.m_editedList.set(index, newStatus);
/*  977:1184 */         ((KnowledgeFlowApp.CloseableTabTitle)this.m_flowTabs.getTabComponentAt(index)).setBold(status);
/*  978:     */       }
/*  979:     */     }
/*  980:     */     
/*  981:     */     public synchronized boolean getEditedStatus()
/*  982:     */     {
/*  983:1197 */       if (getNumTabs() <= 0) {
/*  984:1198 */         return false;
/*  985:     */       }
/*  986:1201 */       return getEditedStatus(getCurrentTabIndex());
/*  987:     */     }
/*  988:     */     
/*  989:     */     public synchronized boolean getEditedStatus(int index)
/*  990:     */     {
/*  991:1212 */       if ((index < getNumTabs()) && (index >= 0)) {
/*  992:1213 */         return ((Boolean)this.m_editedList.get(index)).booleanValue();
/*  993:     */       }
/*  994:1215 */       return false;
/*  995:     */     }
/*  996:     */     
/*  997:     */     public synchronized void setUndoBuffer(Stack<File> buffer)
/*  998:     */     {
/*  999:1219 */       if (getNumTabs() > 0) {
/* 1000:1220 */         setUndoBuffer(getCurrentTabIndex(), buffer);
/* 1001:     */       }
/* 1002:     */     }
/* 1003:     */     
/* 1004:     */     public synchronized void setUndoBuffer(int index, Stack<File> buffer)
/* 1005:     */     {
/* 1006:1225 */       if ((index < getNumTabs()) && (index >= 0)) {
/* 1007:1226 */         this.m_undoBufferList.set(index, buffer);
/* 1008:     */       }
/* 1009:     */     }
/* 1010:     */     
/* 1011:     */     public synchronized Stack<File> getUndoBuffer()
/* 1012:     */     {
/* 1013:1231 */       if (getNumTabs() > 0) {
/* 1014:1232 */         return getUndoBuffer(getCurrentTabIndex());
/* 1015:     */       }
/* 1016:1234 */       return null;
/* 1017:     */     }
/* 1018:     */     
/* 1019:     */     public synchronized Stack<File> getUndoBuffer(int index)
/* 1020:     */     {
/* 1021:1238 */       if ((index >= 0) && (index < getNumTabs())) {
/* 1022:1239 */         return (Stack)this.m_undoBufferList.get(index);
/* 1023:     */       }
/* 1024:1241 */       return null;
/* 1025:     */     }
/* 1026:     */     
/* 1027:     */     public synchronized Vector<Object> getSelectedBeans()
/* 1028:     */     {
/* 1029:1245 */       if (getNumTabs() > 0) {
/* 1030:1246 */         return getSelectedBeans(getCurrentTabIndex());
/* 1031:     */       }
/* 1032:1248 */       return null;
/* 1033:     */     }
/* 1034:     */     
/* 1035:     */     public synchronized Vector<Object> getSelectedBeans(int index)
/* 1036:     */     {
/* 1037:1252 */       if ((index < getNumTabs()) && (index >= 0)) {
/* 1038:1253 */         return (Vector)this.m_selectedBeans.get(index);
/* 1039:     */       }
/* 1040:1255 */       return null;
/* 1041:     */     }
/* 1042:     */     
/* 1043:     */     public synchronized void setSelectedBeans(Vector<Object> beans)
/* 1044:     */     {
/* 1045:1259 */       if (getNumTabs() > 0)
/* 1046:     */       {
/* 1047:1260 */         setSelectedBeans(getCurrentTabIndex(), beans);
/* 1048:     */         
/* 1049:1262 */         KnowledgeFlowApp.this.m_groupB.setEnabled(false);
/* 1050:1263 */         if ((getSelectedBeans().size() > 0) && (!getExecuting()))
/* 1051:     */         {
/* 1052:1265 */           Vector<Object> selected = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/* 1053:     */           
/* 1054:     */ 
/* 1055:1268 */           Vector<Object> inputs = BeanConnection.inputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 1056:     */           
/* 1057:1270 */           Vector<Object> outputs = BeanConnection.outputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 1058:1273 */           if (KnowledgeFlowApp.this.groupable(selected, inputs, outputs)) {
/* 1059:1274 */             KnowledgeFlowApp.this.m_groupB.setEnabled(true);
/* 1060:     */           }
/* 1061:     */         }
/* 1062:1278 */         KnowledgeFlowApp.this.m_cutB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/* 1063:1279 */         KnowledgeFlowApp.this.m_copyB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/* 1064:1280 */         KnowledgeFlowApp.this.m_deleteB.setEnabled((getSelectedBeans().size() > 0) && (!getExecuting()));
/* 1065:     */       }
/* 1066:     */     }
/* 1067:     */     
/* 1068:     */     public synchronized void setSelectedBeans(int index, Vector<Object> beans)
/* 1069:     */     {
/* 1070:1285 */       if ((index < getNumTabs()) && (index >= 0))
/* 1071:     */       {
/* 1072:1287 */         for (int i = 0; i < ((Vector)this.m_selectedBeans.get(index)).size(); i++)
/* 1073:     */         {
/* 1074:1288 */           BeanInstance temp = (BeanInstance)((Vector)this.m_selectedBeans.get(index)).elementAt(i);
/* 1075:1290 */           if ((temp.getBean() instanceof Visible)) {
/* 1076:1291 */             ((Visible)temp.getBean()).getVisual().setDisplayConnectors(false);
/* 1077:1292 */           } else if ((temp.getBean() instanceof Note)) {
/* 1078:1293 */             ((Note)temp.getBean()).setHighlighted(false);
/* 1079:     */           }
/* 1080:     */         }
/* 1081:1297 */         this.m_selectedBeans.set(index, beans);
/* 1082:1300 */         for (int i = 0; i < beans.size(); i++)
/* 1083:     */         {
/* 1084:1301 */           BeanInstance temp = (BeanInstance)beans.elementAt(i);
/* 1085:1302 */           if ((temp.getBean() instanceof Visible)) {
/* 1086:1303 */             ((Visible)temp.getBean()).getVisual().setDisplayConnectors(true);
/* 1087:1304 */           } else if ((temp.getBean() instanceof Note)) {
/* 1088:1305 */             ((Note)temp.getBean()).setHighlighted(true);
/* 1089:     */           }
/* 1090:     */         }
/* 1091:     */       }
/* 1092:     */     }
/* 1093:     */     
/* 1094:     */     public synchronized Environment getEnvironmentSettings()
/* 1095:     */     {
/* 1096:1312 */       if (getNumTabs() > 0) {
/* 1097:1313 */         return getEnvironmentSettings(getCurrentTabIndex());
/* 1098:     */       }
/* 1099:1315 */       return null;
/* 1100:     */     }
/* 1101:     */     
/* 1102:     */     public synchronized Environment getEnvironmentSettings(int index)
/* 1103:     */     {
/* 1104:1319 */       if ((index < getNumTabs()) && (index >= 0)) {
/* 1105:1320 */         return (Environment)this.m_environmentSettings.get(index);
/* 1106:     */       }
/* 1107:1322 */       return null;
/* 1108:     */     }
/* 1109:     */     
/* 1110:     */     public void setInstances(Instances insts) {}
/* 1111:     */     
/* 1112:     */     public boolean acceptsInstances()
/* 1113:     */     {
/* 1114:1334 */       return false;
/* 1115:     */     }
/* 1116:     */     
/* 1117:     */     public String getPerspectiveTitle()
/* 1118:     */     {
/* 1119:1342 */       return "Data mining processes";
/* 1120:     */     }
/* 1121:     */     
/* 1122:     */     public String getPerspectiveTipText()
/* 1123:     */     {
/* 1124:1350 */       return "Knowledge Flow processes";
/* 1125:     */     }
/* 1126:     */     
/* 1127:     */     public Icon getPerspectiveIcon()
/* 1128:     */     {
/* 1129:1358 */       Image wekaI = KnowledgeFlowApp.this.loadImage("weka/gui/weka_icon_new.png");
/* 1130:1359 */       ImageIcon icon = new ImageIcon(wekaI);
/* 1131:     */       
/* 1132:1361 */       double width = icon.getIconWidth();
/* 1133:1362 */       double height = icon.getIconHeight();
/* 1134:1363 */       width *= 0.035D;
/* 1135:1364 */       height *= 0.035D;
/* 1136:     */       
/* 1137:1366 */       wekaI = wekaI.getScaledInstance((int)width, (int)height, 4);
/* 1138:     */       
/* 1139:1368 */       icon = new ImageIcon(wekaI);
/* 1140:     */       
/* 1141:1370 */       return icon;
/* 1142:     */     }
/* 1143:     */     
/* 1144:     */     private void setUpToolsAndJTree()
/* 1145:     */     {
/* 1146:1375 */       JPanel toolBarPanel = new JPanel();
/* 1147:1376 */       toolBarPanel.setLayout(new BorderLayout());
/* 1148:1380 */       if (KnowledgeFlowApp.this.m_showFileMenu)
/* 1149:     */       {
/* 1150:1383 */         Action closeAction = new AbstractAction("Close")
/* 1151:     */         {
/* 1152:     */           private static final long serialVersionUID = 4762166880144590384L;
/* 1153:     */           
/* 1154:     */           public void actionPerformed(ActionEvent e)
/* 1155:     */           {
/* 1156:1391 */             if (KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex() >= 0) {
/* 1157:1392 */               KnowledgeFlowApp.this.m_mainKFPerspective.removeTab(KnowledgeFlowApp.MainKFPerspective.this.getCurrentTabIndex());
/* 1158:     */             }
/* 1159:     */           }
/* 1160:1395 */         };
/* 1161:1396 */         KeyStroke closeKey = KeyStroke.getKeyStroke(87, 128);
/* 1162:     */         
/* 1163:1398 */         getActionMap().put("Close", closeAction);
/* 1164:1399 */         getInputMap(2).put(closeKey, "Close");
/* 1165:     */         
/* 1166:     */ 
/* 1167:1402 */         JToolBar fixedTools = new JToolBar();
/* 1168:1403 */         fixedTools.setOrientation(0);
/* 1169:     */         
/* 1170:1405 */         KnowledgeFlowApp.this.m_groupB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/bricks.png")));
/* 1171:     */         
/* 1172:1407 */         KnowledgeFlowApp.this.m_groupB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1173:1408 */         KnowledgeFlowApp.this.m_groupB.setToolTipText("Group selected (Ctrl+Z)");
/* 1174:1409 */         KnowledgeFlowApp.this.m_cutB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/cut.png")));
/* 1175:     */         
/* 1176:1411 */         KnowledgeFlowApp.this.m_cutB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1177:1412 */         KnowledgeFlowApp.this.m_cutB.setToolTipText("Cut selected (Ctrl+X)");
/* 1178:1413 */         KnowledgeFlowApp.this.m_copyB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/page_copy.png")));
/* 1179:     */         
/* 1180:1415 */         KnowledgeFlowApp.this.m_copyB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1181:1416 */         KnowledgeFlowApp.this.m_copyB.setToolTipText("Copy selected (Ctrl+C)");
/* 1182:1417 */         KnowledgeFlowApp.this.m_pasteB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/paste_plain.png")));
/* 1183:     */         
/* 1184:1419 */         KnowledgeFlowApp.this.m_pasteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1185:1420 */         KnowledgeFlowApp.this.m_pasteB.setToolTipText("Paste from clipboard (Ctrl+V)");
/* 1186:1421 */         KnowledgeFlowApp.this.m_deleteB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/delete.png")));
/* 1187:     */         
/* 1188:1423 */         KnowledgeFlowApp.this.m_deleteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1189:1424 */         KnowledgeFlowApp.this.m_deleteB.setToolTipText("Delete selected (DEL)");
/* 1190:1425 */         KnowledgeFlowApp.this.m_snapToGridB = new JToggleButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/shape_handles.png")));
/* 1191:     */         
/* 1192:     */ 
/* 1193:1428 */         KnowledgeFlowApp.this.m_snapToGridB.setToolTipText("Snap to grid (Ctrl+G)");
/* 1194:     */         
/* 1195:1430 */         KnowledgeFlowApp.this.m_saveB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/disk.png")));
/* 1196:     */         
/* 1197:1432 */         KnowledgeFlowApp.this.m_saveB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1198:1433 */         KnowledgeFlowApp.this.m_saveB.setToolTipText("Save layout (Ctrl+S)");
/* 1199:1434 */         KnowledgeFlowApp.this.m_saveBB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/disk_multiple.png")));
/* 1200:     */         
/* 1201:1436 */         KnowledgeFlowApp.this.m_saveBB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1202:1437 */         KnowledgeFlowApp.this.m_saveBB.setToolTipText("Save layout with new name");
/* 1203:     */         
/* 1204:1439 */         KnowledgeFlowApp.this.m_loadB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/folder_add.png")));
/* 1205:     */         
/* 1206:1441 */         KnowledgeFlowApp.this.m_loadB.setToolTipText("Open (Ctrl+O)");
/* 1207:1442 */         KnowledgeFlowApp.this.m_loadB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1208:1443 */         KnowledgeFlowApp.this.m_newB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/page_add.png")));
/* 1209:     */         
/* 1210:1445 */         KnowledgeFlowApp.this.m_newB.setToolTipText("New layout (Ctrl+N)");
/* 1211:1446 */         KnowledgeFlowApp.this.m_newB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1212:1447 */         KnowledgeFlowApp.this.m_newB.setEnabled(KnowledgeFlowApp.this.getAllowMultipleTabs());
/* 1213:     */         
/* 1214:1449 */         KnowledgeFlowApp.this.m_helpB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/help.png")));
/* 1215:     */         
/* 1216:1451 */         KnowledgeFlowApp.this.m_helpB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1217:1452 */         KnowledgeFlowApp.this.m_helpB.setToolTipText("Display help (Ctrl+H)");
/* 1218:1453 */         KnowledgeFlowApp.this.m_togglePerspectivesB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/cog_go.png")));
/* 1219:     */         
/* 1220:1455 */         KnowledgeFlowApp.this.m_togglePerspectivesB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1221:     */         
/* 1222:1457 */         KnowledgeFlowApp.this.m_togglePerspectivesB.setToolTipText("Show/hide perspectives toolbar (Ctrl+P)");
/* 1223:     */         
/* 1224:     */ 
/* 1225:1460 */         KnowledgeFlowApp.this.m_templatesB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/application_view_tile.png")));
/* 1226:     */         
/* 1227:1462 */         KnowledgeFlowApp.this.m_templatesB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1228:1463 */         KnowledgeFlowApp.this.m_templatesB.setToolTipText("Load a template layout");
/* 1229:     */         
/* 1230:1465 */         KnowledgeFlowApp.this.m_noteB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/note_add.png")));
/* 1231:     */         
/* 1232:1467 */         KnowledgeFlowApp.this.m_noteB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1233:1468 */         KnowledgeFlowApp.this.m_noteB.setToolTipText("Add a note to the layout (Ctrl+I)");
/* 1234:     */         
/* 1235:1470 */         KnowledgeFlowApp.this.m_selectAllB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/shape_group.png")));
/* 1236:     */         
/* 1237:1472 */         KnowledgeFlowApp.this.m_selectAllB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1238:1473 */         KnowledgeFlowApp.this.m_selectAllB.setToolTipText("Select all (Ctrl+A)");
/* 1239:     */         
/* 1240:1475 */         KnowledgeFlowApp.this.m_zoomInB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/zoom_in.png")));
/* 1241:     */         
/* 1242:1477 */         KnowledgeFlowApp.this.m_zoomInB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1243:1478 */         KnowledgeFlowApp.this.m_zoomInB.setToolTipText("Zoom in (Ctrl++)");
/* 1244:     */         
/* 1245:1480 */         KnowledgeFlowApp.this.m_zoomOutB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/zoom_out.png")));
/* 1246:     */         
/* 1247:1482 */         KnowledgeFlowApp.this.m_zoomOutB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1248:1483 */         KnowledgeFlowApp.this.m_zoomOutB.setToolTipText("Zoom out (Ctrl+-)");
/* 1249:     */         
/* 1250:1485 */         KnowledgeFlowApp.this.m_undoB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/arrow_undo.png")));
/* 1251:     */         
/* 1252:1487 */         KnowledgeFlowApp.this.m_undoB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1253:1488 */         KnowledgeFlowApp.this.m_undoB.setToolTipText("Undo (Ctrl+U)");
/* 1254:     */         
/* 1255:1490 */         fixedTools.add(KnowledgeFlowApp.this.m_zoomInB);
/* 1256:1491 */         fixedTools.add(KnowledgeFlowApp.this.m_zoomOutB);
/* 1257:1492 */         fixedTools.addSeparator();
/* 1258:1493 */         fixedTools.add(KnowledgeFlowApp.this.m_selectAllB);
/* 1259:1494 */         fixedTools.add(KnowledgeFlowApp.this.m_groupB);
/* 1260:1495 */         fixedTools.add(KnowledgeFlowApp.this.m_cutB);
/* 1261:1496 */         fixedTools.add(KnowledgeFlowApp.this.m_copyB);
/* 1262:1497 */         fixedTools.add(KnowledgeFlowApp.this.m_deleteB);
/* 1263:1498 */         fixedTools.add(KnowledgeFlowApp.this.m_pasteB);
/* 1264:1499 */         fixedTools.add(KnowledgeFlowApp.this.m_undoB);
/* 1265:1500 */         fixedTools.add(KnowledgeFlowApp.this.m_noteB);
/* 1266:1501 */         fixedTools.addSeparator();
/* 1267:1502 */         fixedTools.add(KnowledgeFlowApp.this.m_snapToGridB);
/* 1268:1503 */         fixedTools.addSeparator();
/* 1269:1504 */         fixedTools.add(KnowledgeFlowApp.this.m_newB);
/* 1270:1505 */         fixedTools.add(KnowledgeFlowApp.this.m_saveB);
/* 1271:1506 */         fixedTools.add(KnowledgeFlowApp.this.m_saveBB);
/* 1272:1507 */         fixedTools.add(KnowledgeFlowApp.this.m_loadB);
/* 1273:1508 */         fixedTools.add(KnowledgeFlowApp.this.m_templatesB);
/* 1274:1509 */         fixedTools.addSeparator();
/* 1275:1510 */         fixedTools.add(KnowledgeFlowApp.this.m_togglePerspectivesB);
/* 1276:     */         
/* 1277:1512 */         fixedTools.add(KnowledgeFlowApp.this.m_helpB);
/* 1278:1513 */         Dimension d = KnowledgeFlowApp.this.m_undoB.getPreferredSize();
/* 1279:1514 */         Dimension d2 = fixedTools.getMinimumSize();
/* 1280:1515 */         Dimension d3 = new Dimension(d2.width, d.height + 4);
/* 1281:1516 */         fixedTools.setPreferredSize(d3);
/* 1282:1517 */         fixedTools.setMaximumSize(d3);
/* 1283:     */         
/* 1284:1519 */         final Action saveAction = new AbstractAction("Save")
/* 1285:     */         {
/* 1286:     */           private static final long serialVersionUID = 5182044142154404706L;
/* 1287:     */           
/* 1288:     */           public void actionPerformed(ActionEvent e)
/* 1289:     */           {
/* 1290:1527 */             if (KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex() >= 0) {
/* 1291:1528 */               KnowledgeFlowApp.this.saveLayout(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex(), false);
/* 1292:     */             }
/* 1293:     */           }
/* 1294:1531 */         };
/* 1295:1532 */         KeyStroke saveKey = KeyStroke.getKeyStroke(83, 128);
/* 1296:     */         
/* 1297:1534 */         getActionMap().put("Save", saveAction);
/* 1298:1535 */         getInputMap(2).put(saveKey, "Save");
/* 1299:     */         
/* 1300:1537 */         KnowledgeFlowApp.this.m_saveB.addActionListener(new ActionListener()
/* 1301:     */         {
/* 1302:     */           public void actionPerformed(ActionEvent e)
/* 1303:     */           {
/* 1304:1540 */             saveAction.actionPerformed(e);
/* 1305:     */           }
/* 1306:1543 */         });
/* 1307:1544 */         KnowledgeFlowApp.this.m_saveBB.addActionListener(new ActionListener()
/* 1308:     */         {
/* 1309:     */           public void actionPerformed(ActionEvent e)
/* 1310:     */           {
/* 1311:1547 */             KnowledgeFlowApp.this.saveLayout(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex(), true);
/* 1312:     */           }
/* 1313:1550 */         });
/* 1314:1551 */         final Action openAction = new AbstractAction("Open")
/* 1315:     */         {
/* 1316:     */           private static final long serialVersionUID = -5106547209818805444L;
/* 1317:     */           
/* 1318:     */           public void actionPerformed(ActionEvent e)
/* 1319:     */           {
/* 1320:1559 */             KnowledgeFlowApp.this.m_flowEnvironment = new Environment();
/* 1321:1560 */             KnowledgeFlowApp.this.loadLayout();
/* 1322:     */           }
/* 1323:1562 */         };
/* 1324:1563 */         KeyStroke openKey = KeyStroke.getKeyStroke(79, 128);
/* 1325:     */         
/* 1326:1565 */         getActionMap().put("Open", openAction);
/* 1327:1566 */         getInputMap(2).put(openKey, "Open");
/* 1328:     */         
/* 1329:1568 */         KnowledgeFlowApp.this.m_loadB.addActionListener(new ActionListener()
/* 1330:     */         {
/* 1331:     */           public void actionPerformed(ActionEvent e)
/* 1332:     */           {
/* 1333:1571 */             openAction.actionPerformed(e);
/* 1334:     */           }
/* 1335:1574 */         });
/* 1336:1575 */         final Action newAction = new AbstractAction("New")
/* 1337:     */         {
/* 1338:     */           private static final long serialVersionUID = 8002244400334262966L;
/* 1339:     */           
/* 1340:     */           public void actionPerformed(ActionEvent e)
/* 1341:     */           {
/* 1342:1583 */             KnowledgeFlowApp.this.clearLayout();
/* 1343:     */           }
/* 1344:1585 */         };
/* 1345:1586 */         KeyStroke newKey = KeyStroke.getKeyStroke(78, 128);
/* 1346:     */         
/* 1347:1588 */         getActionMap().put("New", newAction);
/* 1348:1589 */         getInputMap(2).put(newKey, "New");
/* 1349:     */         
/* 1350:1591 */         KnowledgeFlowApp.this.m_newB.addActionListener(new ActionListener()
/* 1351:     */         {
/* 1352:     */           public void actionPerformed(ActionEvent ae)
/* 1353:     */           {
/* 1354:1594 */             newAction.actionPerformed(ae);
/* 1355:     */           }
/* 1356:1597 */         });
/* 1357:1598 */         final Action selectAllAction = new AbstractAction("SelectAll")
/* 1358:     */         {
/* 1359:     */           private static final long serialVersionUID = -8086754050844707658L;
/* 1360:     */           
/* 1361:     */           public void actionPerformed(ActionEvent e)
/* 1362:     */           {
/* 1363:1606 */             if (BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) }).size() > 0)
/* 1364:     */             {
/* 1365:1609 */               Vector<Object> allBeans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 1366:     */               
/* 1367:1611 */               Vector<Object> newSelected = new Vector();
/* 1368:1612 */               for (int i = 0; i < allBeans.size(); i++) {
/* 1369:1613 */                 newSelected.add(allBeans.get(i));
/* 1370:     */               }
/* 1371:1617 */               if (newSelected.size() == KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans().size()) {
/* 1372:1620 */                 KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 1373:     */               } else {
/* 1374:1623 */                 KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(newSelected);
/* 1375:     */               }
/* 1376:     */             }
/* 1377:1626 */             KnowledgeFlowApp.MainKFPerspective.this.revalidate();
/* 1378:1627 */             KnowledgeFlowApp.MainKFPerspective.this.repaint();
/* 1379:1628 */             KnowledgeFlowApp.this.notifyIsDirty();
/* 1380:     */           }
/* 1381:1630 */         };
/* 1382:1631 */         KeyStroke selectAllKey = KeyStroke.getKeyStroke(65, 128);
/* 1383:     */         
/* 1384:1633 */         getActionMap().put("SelectAll", selectAllAction);
/* 1385:1634 */         getInputMap(2).put(selectAllKey, "SelectAll");
/* 1386:     */         
/* 1387:1636 */         KnowledgeFlowApp.this.m_selectAllB.addActionListener(new ActionListener()
/* 1388:     */         {
/* 1389:     */           public void actionPerformed(ActionEvent e)
/* 1390:     */           {
/* 1391:1639 */             selectAllAction.actionPerformed(e);
/* 1392:     */           }
/* 1393:1642 */         });
/* 1394:1643 */         final Action zoomInAction = new AbstractAction("ZoomIn")
/* 1395:     */         {
/* 1396:     */           private static final long serialVersionUID = 1348383794897269484L;
/* 1397:     */           
/* 1398:     */           public void actionPerformed(ActionEvent e)
/* 1399:     */           {
/* 1400:1651 */             KnowledgeFlowApp.access$212(KnowledgeFlowApp.this, 25);
/* 1401:1652 */             KnowledgeFlowApp.this.m_zoomOutB.setEnabled(true);
/* 1402:1653 */             if (KnowledgeFlowApp.this.m_layoutZoom >= 200)
/* 1403:     */             {
/* 1404:1654 */               KnowledgeFlowApp.this.m_layoutZoom = 200;
/* 1405:1655 */               KnowledgeFlowApp.this.m_zoomInB.setEnabled(false);
/* 1406:     */             }
/* 1407:1657 */             KnowledgeFlowApp.this.m_mainKFPerspective.setCurrentZoomSetting(KnowledgeFlowApp.this.m_layoutZoom);
/* 1408:1658 */             KnowledgeFlowApp.MainKFPerspective.this.revalidate();
/* 1409:1659 */             KnowledgeFlowApp.MainKFPerspective.this.repaint();
/* 1410:1660 */             KnowledgeFlowApp.this.notifyIsDirty();
/* 1411:     */           }
/* 1412:1662 */         };
/* 1413:1663 */         KeyStroke zoomInKey = KeyStroke.getKeyStroke(61, 128);
/* 1414:     */         
/* 1415:1665 */         getActionMap().put("ZoomIn", zoomInAction);
/* 1416:1666 */         getInputMap(2).put(zoomInKey, "ZoomIn");
/* 1417:     */         
/* 1418:1668 */         KnowledgeFlowApp.this.m_zoomInB.addActionListener(new ActionListener()
/* 1419:     */         {
/* 1420:     */           public void actionPerformed(ActionEvent e)
/* 1421:     */           {
/* 1422:1671 */             zoomInAction.actionPerformed(e);
/* 1423:     */           }
/* 1424:1674 */         });
/* 1425:1675 */         final Action zoomOutAction = new AbstractAction("ZoomOut")
/* 1426:     */         {
/* 1427:     */           private static final long serialVersionUID = -1120096894263455918L;
/* 1428:     */           
/* 1429:     */           public void actionPerformed(ActionEvent e)
/* 1430:     */           {
/* 1431:1683 */             KnowledgeFlowApp.access$220(KnowledgeFlowApp.this, 25);
/* 1432:1684 */             KnowledgeFlowApp.this.m_zoomInB.setEnabled(true);
/* 1433:1685 */             if (KnowledgeFlowApp.this.m_layoutZoom <= 50)
/* 1434:     */             {
/* 1435:1686 */               KnowledgeFlowApp.this.m_layoutZoom = 50;
/* 1436:1687 */               KnowledgeFlowApp.this.m_zoomOutB.setEnabled(false);
/* 1437:     */             }
/* 1438:1689 */             KnowledgeFlowApp.this.m_mainKFPerspective.setCurrentZoomSetting(KnowledgeFlowApp.this.m_layoutZoom);
/* 1439:1690 */             KnowledgeFlowApp.MainKFPerspective.this.revalidate();
/* 1440:1691 */             KnowledgeFlowApp.MainKFPerspective.this.repaint();
/* 1441:1692 */             KnowledgeFlowApp.this.notifyIsDirty();
/* 1442:     */           }
/* 1443:1694 */         };
/* 1444:1695 */         KeyStroke zoomOutKey = KeyStroke.getKeyStroke(45, 128);
/* 1445:     */         
/* 1446:1697 */         getActionMap().put("ZoomOut", zoomOutAction);
/* 1447:1698 */         getInputMap(2).put(zoomOutKey, "ZoomOut");
/* 1448:     */         
/* 1449:1700 */         KnowledgeFlowApp.this.m_zoomOutB.addActionListener(new ActionListener()
/* 1450:     */         {
/* 1451:     */           public void actionPerformed(ActionEvent e)
/* 1452:     */           {
/* 1453:1703 */             zoomOutAction.actionPerformed(e);
/* 1454:     */           }
/* 1455:1706 */         });
/* 1456:1707 */         final Action groupAction = new AbstractAction("Group")
/* 1457:     */         {
/* 1458:     */           private static final long serialVersionUID = -5752742619180091435L;
/* 1459:     */           
/* 1460:     */           public void actionPerformed(ActionEvent e)
/* 1461:     */           {
/* 1462:1715 */             Vector<Object> selected = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/* 1463:     */             
/* 1464:1717 */             Vector<Object> inputs = BeanConnection.inputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 1465:     */             
/* 1466:1719 */             Vector<Object> outputs = BeanConnection.outputs(selected, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 1467:     */             
/* 1468:1721 */             KnowledgeFlowApp.this.groupSubFlow(selected, inputs, outputs);
/* 1469:     */           }
/* 1470:1723 */         };
/* 1471:1724 */         KeyStroke groupKey = KeyStroke.getKeyStroke(90, 128);
/* 1472:     */         
/* 1473:1726 */         getActionMap().put("Group", groupAction);
/* 1474:1727 */         getInputMap(2).put(groupKey, "Group");
/* 1475:     */         
/* 1476:1729 */         KnowledgeFlowApp.this.m_groupB.addActionListener(new ActionListener()
/* 1477:     */         {
/* 1478:     */           public void actionPerformed(ActionEvent e)
/* 1479:     */           {
/* 1480:1732 */             groupAction.actionPerformed(e);
/* 1481:     */           }
/* 1482:1735 */         });
/* 1483:1736 */         final Action cutAction = new AbstractAction("Cut")
/* 1484:     */         {
/* 1485:     */           private static final long serialVersionUID = -4955878102742013040L;
/* 1486:     */           
/* 1487:     */           public void actionPerformed(ActionEvent e)
/* 1488:     */           {
/* 1489:1745 */             if (KnowledgeFlowApp.this.copyToClipboard()) {
/* 1490:1746 */               KnowledgeFlowApp.this.deleteSelectedBeans();
/* 1491:     */             }
/* 1492:     */           }
/* 1493:1749 */         };
/* 1494:1750 */         KeyStroke cutKey = KeyStroke.getKeyStroke(88, 128);
/* 1495:     */         
/* 1496:1752 */         getActionMap().put("Cut", cutAction);
/* 1497:1753 */         getInputMap(2).put(cutKey, "Cut");
/* 1498:     */         
/* 1499:1755 */         KnowledgeFlowApp.this.m_cutB.addActionListener(new ActionListener()
/* 1500:     */         {
/* 1501:     */           public void actionPerformed(ActionEvent e)
/* 1502:     */           {
/* 1503:1758 */             cutAction.actionPerformed(e);
/* 1504:     */           }
/* 1505:1761 */         });
/* 1506:1762 */         final Action deleteAction = new AbstractAction("Delete")
/* 1507:     */         {
/* 1508:     */           private static final long serialVersionUID = 4621688037874199553L;
/* 1509:     */           
/* 1510:     */           public void actionPerformed(ActionEvent e)
/* 1511:     */           {
/* 1512:1770 */             KnowledgeFlowApp.this.deleteSelectedBeans();
/* 1513:     */           }
/* 1514:1772 */         };
/* 1515:1773 */         KeyStroke deleteKey = KeyStroke.getKeyStroke(127, 0);
/* 1516:1774 */         getActionMap().put("Delete", deleteAction);
/* 1517:1775 */         getInputMap(2).put(deleteKey, "Delete");
/* 1518:     */         
/* 1519:1777 */         KnowledgeFlowApp.this.m_deleteB.addActionListener(new ActionListener()
/* 1520:     */         {
/* 1521:     */           public void actionPerformed(ActionEvent e)
/* 1522:     */           {
/* 1523:1780 */             deleteAction.actionPerformed(e);
/* 1524:     */           }
/* 1525:1783 */         });
/* 1526:1784 */         final Action copyAction = new AbstractAction("Copy")
/* 1527:     */         {
/* 1528:     */           private static final long serialVersionUID = 117010390180468707L;
/* 1529:     */           
/* 1530:     */           public void actionPerformed(ActionEvent e)
/* 1531:     */           {
/* 1532:1792 */             KnowledgeFlowApp.this.copyToClipboard();
/* 1533:1793 */             KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 1534:     */           }
/* 1535:1795 */         };
/* 1536:1796 */         KeyStroke copyKey = KeyStroke.getKeyStroke(67, 128);
/* 1537:     */         
/* 1538:1798 */         getActionMap().put("Copy", copyAction);
/* 1539:1799 */         getInputMap(2).put(copyKey, "Copy");
/* 1540:     */         
/* 1541:1801 */         KnowledgeFlowApp.this.m_copyB.addActionListener(new ActionListener()
/* 1542:     */         {
/* 1543:     */           public void actionPerformed(ActionEvent e)
/* 1544:     */           {
/* 1545:1804 */             copyAction.actionPerformed(e);
/* 1546:     */           }
/* 1547:1807 */         });
/* 1548:1808 */         final Action pasteAction = new AbstractAction("Paste")
/* 1549:     */         {
/* 1550:     */           private static final long serialVersionUID = 5935121051028929455L;
/* 1551:     */           
/* 1552:     */           public void actionPerformed(ActionEvent e)
/* 1553:     */           {
/* 1554:1816 */             KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(1));
/* 1555:     */             
/* 1556:1818 */             KnowledgeFlowApp.this.m_mode = 5;
/* 1557:     */           }
/* 1558:1820 */         };
/* 1559:1821 */         KeyStroke pasteKey = KeyStroke.getKeyStroke(86, 128);
/* 1560:     */         
/* 1561:1823 */         getActionMap().put("Paste", pasteAction);
/* 1562:1824 */         getInputMap(2).put(pasteKey, "Paste");
/* 1563:     */         
/* 1564:1826 */         KnowledgeFlowApp.this.m_pasteB.addActionListener(new ActionListener()
/* 1565:     */         {
/* 1566:     */           public void actionPerformed(ActionEvent e)
/* 1567:     */           {
/* 1568:1829 */             pasteAction.actionPerformed(e);
/* 1569:     */           }
/* 1570:1832 */         });
/* 1571:1833 */         Action snapAction = new AbstractAction("Snap")
/* 1572:     */         {
/* 1573:     */           private static final long serialVersionUID = 7820689847829357449L;
/* 1574:     */           
/* 1575:     */           public void actionPerformed(ActionEvent e)
/* 1576:     */           {
/* 1577:1842 */             KnowledgeFlowApp.this.m_snapToGridB.setSelected(!KnowledgeFlowApp.this.m_snapToGridB.isSelected());
/* 1578:1843 */             if (KnowledgeFlowApp.this.m_snapToGridB.isSelected()) {
/* 1579:1844 */               KnowledgeFlowApp.this.snapSelectedToGrid();
/* 1580:     */             }
/* 1581:     */           }
/* 1582:1847 */         };
/* 1583:1848 */         KeyStroke snapKey = KeyStroke.getKeyStroke(71, 128);
/* 1584:     */         
/* 1585:1850 */         getActionMap().put("Snap", snapAction);
/* 1586:1851 */         getInputMap(2).put(snapKey, "Snap");
/* 1587:     */         
/* 1588:1853 */         KnowledgeFlowApp.this.m_snapToGridB.addActionListener(new ActionListener()
/* 1589:     */         {
/* 1590:     */           public void actionPerformed(ActionEvent e)
/* 1591:     */           {
/* 1592:1856 */             if (KnowledgeFlowApp.this.m_snapToGridB.isSelected()) {
/* 1593:1857 */               KnowledgeFlowApp.this.snapSelectedToGrid();
/* 1594:     */             }
/* 1595:     */           }
/* 1596:1861 */         });
/* 1597:1862 */         fixedTools.setFloatable(false);
/* 1598:1863 */         toolBarPanel.add(fixedTools, "East");
/* 1599:     */       }
/* 1600:1866 */       final Action noteAction = new AbstractAction("Note")
/* 1601:     */       {
/* 1602:     */         private static final long serialVersionUID = 2991743619130024875L;
/* 1603:     */         
/* 1604:     */         public void actionPerformed(ActionEvent e)
/* 1605:     */         {
/* 1606:1874 */           Note n = new Note();
/* 1607:1875 */           KnowledgeFlowApp.this.m_toolBarBean = n;
/* 1608:     */           
/* 1609:1877 */           KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(1));
/* 1610:     */           
/* 1611:1879 */           KnowledgeFlowApp.this.m_mode = 3;
/* 1612:     */         }
/* 1613:1881 */       };
/* 1614:1882 */       KeyStroke noteKey = KeyStroke.getKeyStroke(73, 128);
/* 1615:     */       
/* 1616:1884 */       getActionMap().put("Note", noteAction);
/* 1617:1885 */       getInputMap(2).put(noteKey, "Note");
/* 1618:     */       
/* 1619:1887 */       KnowledgeFlowApp.this.m_noteB.addActionListener(new ActionListener()
/* 1620:     */       {
/* 1621:     */         public void actionPerformed(ActionEvent e)
/* 1622:     */         {
/* 1623:1890 */           noteAction.actionPerformed(e);
/* 1624:     */         }
/* 1625:1893 */       });
/* 1626:1894 */       final Action undoAction = new AbstractAction("Undo")
/* 1627:     */       {
/* 1628:     */         private static final long serialVersionUID = 7248362305594881263L;
/* 1629:     */         
/* 1630:     */         public void actionPerformed(ActionEvent e)
/* 1631:     */         {
/* 1632:1902 */           Stack<File> undo = KnowledgeFlowApp.this.m_mainKFPerspective.getUndoBuffer();
/* 1633:1903 */           if (undo.size() > 0)
/* 1634:     */           {
/* 1635:1904 */             File undoF = (File)undo.pop();
/* 1636:1905 */             if (undo.size() == 0) {
/* 1637:1906 */               KnowledgeFlowApp.this.m_undoB.setEnabled(false);
/* 1638:     */             }
/* 1639:1908 */             KnowledgeFlowApp.this.loadLayout(undoF, false, true);
/* 1640:     */           }
/* 1641:     */         }
/* 1642:1911 */       };
/* 1643:1912 */       KeyStroke undoKey = KeyStroke.getKeyStroke(85, 128);
/* 1644:     */       
/* 1645:1914 */       getActionMap().put("Undo", undoAction);
/* 1646:1915 */       getInputMap(2).put(undoKey, "Undo");
/* 1647:     */       
/* 1648:1917 */       KnowledgeFlowApp.this.m_undoB.addActionListener(new ActionListener()
/* 1649:     */       {
/* 1650:     */         public void actionPerformed(ActionEvent e)
/* 1651:     */         {
/* 1652:1920 */           undoAction.actionPerformed(e);
/* 1653:     */         }
/* 1654:1923 */       });
/* 1655:1924 */       KnowledgeFlowApp.this.m_playB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/resultset_next.png")));
/* 1656:     */       
/* 1657:1926 */       KnowledgeFlowApp.this.m_playB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 1658:1927 */       KnowledgeFlowApp.this.m_playB.setToolTipText("Run this flow (all start points launched in parallel)");
/* 1659:     */       
/* 1660:1929 */       KnowledgeFlowApp.this.m_playB.addActionListener(new ActionListener()
/* 1661:     */       {
/* 1662:     */         public void actionPerformed(ActionEvent e)
/* 1663:     */         {
/* 1664:1932 */           if (BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) }).size() == 0) {
/* 1665:1934 */             return;
/* 1666:     */           }
/* 1667:1937 */           boolean proceed = true;
/* 1668:1938 */           if (KnowledgeFlowApp.m_Memory.memoryIsLow()) {
/* 1669:1939 */             proceed = KnowledgeFlowApp.m_Memory.showMemoryIsLow();
/* 1670:     */           }
/* 1671:1942 */           if (proceed) {
/* 1672:1943 */             KnowledgeFlowApp.this.runFlow(false);
/* 1673:     */           }
/* 1674:     */         }
/* 1675:1947 */       });
/* 1676:1948 */       KnowledgeFlowApp.this.m_playBB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/resultset_last.png")));
/* 1677:     */       
/* 1678:1950 */       KnowledgeFlowApp.this.m_playBB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 1679:1951 */       KnowledgeFlowApp.this.m_playBB.setToolTipText("Run this flow (start points launched sequentially)");
/* 1680:     */       
/* 1681:1953 */       KnowledgeFlowApp.this.m_playBB.addActionListener(new ActionListener()
/* 1682:     */       {
/* 1683:     */         public void actionPerformed(ActionEvent e)
/* 1684:     */         {
/* 1685:1956 */           if (BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) }).size() == 0) {
/* 1686:1958 */             return;
/* 1687:     */           }
/* 1688:1960 */           if (!Utils.getDontShowDialog("weka.gui.beans.KnowledgeFlow.SequentialRunInfo"))
/* 1689:     */           {
/* 1690:1962 */             JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 1691:     */             
/* 1692:1964 */             Object[] stuff = new Object[2];
/* 1693:1965 */             stuff[0] = "The order that data sources are launched in can be\nspecified by setting a custom name for each data source that\nthat includes a number. E.g. \"1:MyArffLoader\". To set a name,\nright-click over a data source and select \"Set name\"\n\nIf the prefix is not specified, then the order of execution\nwill correspond to the order that the components were added\nto the layout. Note that it is also possible to prevent a data\nsource from executing by prefixing its name with a \"!\". E.g\n\"!:MyArffLoader\"";
/* 1694:     */             
/* 1695:     */ 
/* 1696:     */ 
/* 1697:     */ 
/* 1698:     */ 
/* 1699:     */ 
/* 1700:     */ 
/* 1701:     */ 
/* 1702:     */ 
/* 1703:1975 */             stuff[1] = dontShow;
/* 1704:     */             
/* 1705:1977 */             JOptionPane.showMessageDialog(KnowledgeFlowApp.this, stuff, "Sequential execution information", 0);
/* 1706:1980 */             if (dontShow.isSelected()) {
/* 1707:     */               try
/* 1708:     */               {
/* 1709:1982 */                 Utils.setDontShowDialog("weka.gui.beans.KnowledgeFlow.SequentialRunInfo");
/* 1710:     */               }
/* 1711:     */               catch (Exception ex) {}
/* 1712:     */             }
/* 1713:     */           }
/* 1714:1990 */           boolean proceed = true;
/* 1715:1991 */           if (KnowledgeFlowApp.m_Memory.memoryIsLow()) {
/* 1716:1992 */             proceed = KnowledgeFlowApp.m_Memory.showMemoryIsLow();
/* 1717:     */           }
/* 1718:1995 */           if (proceed) {
/* 1719:1996 */             KnowledgeFlowApp.this.runFlow(true);
/* 1720:     */           }
/* 1721:     */         }
/* 1722:2000 */       });
/* 1723:2001 */       KnowledgeFlowApp.this.m_stopB = new JButton(new ImageIcon(KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/shape_square.png")));
/* 1724:     */       
/* 1725:2003 */       KnowledgeFlowApp.this.m_stopB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 1726:2004 */       KnowledgeFlowApp.this.m_stopB.setToolTipText("Stop all execution");
/* 1727:     */       
/* 1728:2006 */       Image tempI = KnowledgeFlowApp.this.loadImage("weka/gui/beans/icons/cursor.png");
/* 1729:2007 */       KnowledgeFlowApp.this.m_pointerB = new JButton(new ImageIcon(tempI));
/* 1730:2008 */       KnowledgeFlowApp.this.m_pointerB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 1731:2009 */       KnowledgeFlowApp.this.m_pointerB.addActionListener(new ActionListener()
/* 1732:     */       {
/* 1733:     */         public void actionPerformed(ActionEvent e)
/* 1734:     */         {
/* 1735:2012 */           KnowledgeFlowApp.this.m_toolBarBean = null;
/* 1736:2013 */           KnowledgeFlowApp.this.m_mode = 0;
/* 1737:2014 */           KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(0));
/* 1738:     */           
/* 1739:2016 */           KnowledgeFlowApp.this.m_componentTree.clearSelection();
/* 1740:     */         }
/* 1741:2027 */       });
/* 1742:2028 */       JToolBar fixedTools2 = new JToolBar();
/* 1743:2029 */       fixedTools2.setOrientation(0);
/* 1744:2030 */       fixedTools2.setFloatable(false);
/* 1745:2031 */       fixedTools2.add(KnowledgeFlowApp.this.m_pointerB);
/* 1746:2032 */       fixedTools2.add(KnowledgeFlowApp.this.m_playB);
/* 1747:2033 */       fixedTools2.add(KnowledgeFlowApp.this.m_playBB);
/* 1748:2034 */       fixedTools2.add(KnowledgeFlowApp.this.m_stopB);
/* 1749:     */       
/* 1750:2036 */       Dimension d = KnowledgeFlowApp.this.m_playB.getPreferredSize();
/* 1751:2037 */       Dimension d2 = fixedTools2.getMinimumSize();
/* 1752:2038 */       Dimension d3 = new Dimension(d2.width, d.height + 4);
/* 1753:2039 */       fixedTools2.setPreferredSize(d3);
/* 1754:2040 */       fixedTools2.setMaximumSize(d3);
/* 1755:     */       
/* 1756:     */ 
/* 1757:     */ 
/* 1758:     */ 
/* 1759:     */ 
/* 1760:2046 */       toolBarPanel.add(fixedTools2, "West");
/* 1761:     */       
/* 1762:2048 */       KnowledgeFlowApp.this.m_stopB.addActionListener(new ActionListener()
/* 1763:     */       {
/* 1764:     */         public void actionPerformed(ActionEvent e)
/* 1765:     */         {
/* 1766:2051 */           KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Attempting to stop all components...");
/* 1767:     */           
/* 1768:2053 */           KnowledgeFlowApp.this.stopFlow();
/* 1769:2054 */           KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|OK.");
/* 1770:     */         }
/* 1771:2057 */       });
/* 1772:2058 */       final Action helpAction = new AbstractAction("Help")
/* 1773:     */       {
/* 1774:     */         private static final long serialVersionUID = 3301809940717051925L;
/* 1775:     */         
/* 1776:     */         public void actionPerformed(ActionEvent e)
/* 1777:     */         {
/* 1778:2066 */           KnowledgeFlowApp.this.popupHelp();
/* 1779:     */         }
/* 1780:2068 */       };
/* 1781:2069 */       KeyStroke helpKey = KeyStroke.getKeyStroke(72, 128);
/* 1782:     */       
/* 1783:2071 */       getActionMap().put("Help", helpAction);
/* 1784:2072 */       getInputMap(2).put(helpKey, "Help");
/* 1785:     */       
/* 1786:2074 */       KnowledgeFlowApp.this.m_helpB.addActionListener(new ActionListener()
/* 1787:     */       {
/* 1788:     */         public void actionPerformed(ActionEvent ae)
/* 1789:     */         {
/* 1790:2077 */           helpAction.actionPerformed(ae);
/* 1791:     */         }
/* 1792:2080 */       });
/* 1793:2081 */       KnowledgeFlowApp.this.m_templatesB.addActionListener(new ActionListener()
/* 1794:     */       {
/* 1795:     */         public void actionPerformed(ActionEvent e)
/* 1796:     */         {
/* 1797:2084 */           KnowledgeFlowApp.this.createTemplateMenuPopup();
/* 1798:     */         }
/* 1799:2087 */       });
/* 1800:2088 */       KnowledgeFlowApp.this.m_templatesB.setEnabled(BeansProperties.TEMPLATE_PATHS.size() > 0);
/* 1801:     */       
/* 1802:2090 */       final Action togglePerspectivesAction = new AbstractAction("Toggle perspectives")
/* 1803:     */       {
/* 1804:     */         private static final long serialVersionUID = 5394622655137498495L;
/* 1805:     */         
/* 1806:     */         public void actionPerformed(ActionEvent e)
/* 1807:     */         {
/* 1808:2099 */           if (KnowledgeFlowApp.this.m_firstUserComponentOpp)
/* 1809:     */           {
/* 1810:2100 */             KnowledgeFlowApp.this.installWindowListenerForSavingUserStuff();
/* 1811:2101 */             KnowledgeFlowApp.this.m_firstUserComponentOpp = false;
/* 1812:     */           }
/* 1813:2104 */           if (!Utils.getDontShowDialog("weka.gui.beans.KnowledgeFlow.PerspectiveInfo"))
/* 1814:     */           {
/* 1815:2106 */             JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 1816:     */             
/* 1817:2108 */             Object[] stuff = new Object[2];
/* 1818:2109 */             stuff[0] = "Perspectives are environments that take over the\nKnowledge Flow UI and provide major additional functionality.\nMany perspectives will operate on a set of instances. Instances\nCan be sent to a perspective by placing a DataSource on the\nlayout canvas, configuring it and then selecting \"Send to perspective\"\nfrom the contextual popup menu that appears when you right-click on\nit. Several perspectives are built in to the Knowledge Flow, others\ncan be installed via the package manager.\n";
/* 1819:     */             
/* 1820:     */ 
/* 1821:     */ 
/* 1822:     */ 
/* 1823:     */ 
/* 1824:     */ 
/* 1825:     */ 
/* 1826:     */ 
/* 1827:2118 */             stuff[1] = dontShow;
/* 1828:     */             
/* 1829:2120 */             JOptionPane.showMessageDialog(KnowledgeFlowApp.this, stuff, "Perspective information", 0);
/* 1830:2123 */             if (dontShow.isSelected()) {
/* 1831:     */               try
/* 1832:     */               {
/* 1833:2125 */                 Utils.setDontShowDialog("weka.gui.beans.KnowledgeFlow.PerspectiveInfo");
/* 1834:     */               }
/* 1835:     */               catch (Exception ex) {}
/* 1836:     */             }
/* 1837:     */           }
/* 1838:2133 */           if (KnowledgeFlowApp.this.m_configAndPerspectivesVisible)
/* 1839:     */           {
/* 1840:2134 */             KnowledgeFlowApp.this.remove(KnowledgeFlowApp.this.m_configAndPerspectives);
/* 1841:2135 */             KnowledgeFlowApp.this.m_configAndPerspectivesVisible = false;
/* 1842:     */           }
/* 1843:     */           else
/* 1844:     */           {
/* 1845:2137 */             KnowledgeFlowApp.this.add(KnowledgeFlowApp.this.m_configAndPerspectives, "North");
/* 1846:     */             
/* 1847:2139 */             KnowledgeFlowApp.this.m_configAndPerspectivesVisible = true;
/* 1848:     */           }
/* 1849:2141 */           KnowledgeFlowApp.MainKFPerspective.this.revalidate();
/* 1850:2142 */           KnowledgeFlowApp.MainKFPerspective.this.repaint();
/* 1851:2143 */           KnowledgeFlowApp.this.notifyIsDirty();
/* 1852:     */         }
/* 1853:2145 */       };
/* 1854:2146 */       KeyStroke togglePerspectivesKey = KeyStroke.getKeyStroke(80, 128);
/* 1855:     */       
/* 1856:2148 */       getActionMap().put("Toggle perspectives", togglePerspectivesAction);
/* 1857:     */       
/* 1858:2150 */       getInputMap(2).put(togglePerspectivesKey, "Toggle perspectives");
/* 1859:     */       
/* 1860:2152 */       KnowledgeFlowApp.this.m_togglePerspectivesB.addActionListener(new ActionListener()
/* 1861:     */       {
/* 1862:     */         public void actionPerformed(ActionEvent e)
/* 1863:     */         {
/* 1864:2155 */           togglePerspectivesAction.actionPerformed(e);
/* 1865:     */         }
/* 1866:2158 */       });
/* 1867:2159 */       int standard_toolset = 0;
/* 1868:2160 */       int wrapper_toolset = 1;
/* 1869:     */       
/* 1870:2162 */       int toolBarType = 0;
/* 1871:     */       
/* 1872:2164 */       DefaultMutableTreeNode jtreeRoot = new DefaultMutableTreeNode("Weka");
/* 1873:2166 */       for (int i = 0; i < KnowledgeFlowApp.TOOLBARS.size(); i++)
/* 1874:     */       {
/* 1875:2167 */         Vector<?> tempBarSpecs = (Vector)KnowledgeFlowApp.TOOLBARS.elementAt(i);
/* 1876:     */         
/* 1877:     */ 
/* 1878:2170 */         String tempToolSetName = (String)tempBarSpecs.elementAt(0);
/* 1879:2171 */         DefaultMutableTreeNode subTreeNode = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, tempToolSetName);
/* 1880:2172 */         jtreeRoot.add(subTreeNode);
/* 1881:     */         
/* 1882:     */ 
/* 1883:     */ 
/* 1884:     */ 
/* 1885:     */ 
/* 1886:2178 */         String tempBeanCompName = (String)tempBarSpecs.elementAt(1);
/* 1887:     */         
/* 1888:     */ 
/* 1889:2181 */         String rootPackage = "";
/* 1890:2182 */         HierarchyPropertyParser hpp = null;
/* 1891:2183 */         Hashtable<String, HierarchyPropertyParser> hpps = null;
/* 1892:2186 */         if (tempBeanCompName.compareTo("null") != 0)
/* 1893:     */         {
/* 1894:2187 */           toolBarType = 1;
/* 1895:2188 */           rootPackage = (String)tempBarSpecs.elementAt(2);
/* 1896:     */           
/* 1897:2190 */           hpps = (Hashtable)tempBarSpecs.elementAt(3);
/* 1898:     */           try
/* 1899:     */           {
/* 1900:2196 */             Beans.instantiate(getClass().getClassLoader(), tempBeanCompName);
/* 1901:     */           }
/* 1902:     */           catch (Exception ex)
/* 1903:     */           {
/* 1904:2202 */             Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Failed to instantiate: " + tempBeanCompName);
/* 1905:     */             
/* 1906:     */ 
/* 1907:     */ 
/* 1908:     */ 
/* 1909:2207 */             break;
/* 1910:     */           }
/* 1911:     */         }
/* 1912:     */         else
/* 1913:     */         {
/* 1914:2210 */           toolBarType = 0;
/* 1915:     */         }
/* 1916:2218 */         int z = 2;
/* 1917:2220 */         if (toolBarType == 1)
/* 1918:     */         {
/* 1919:2221 */           Enumeration<String> enm = hpps.keys();
/* 1920:2223 */           while (enm.hasMoreElements())
/* 1921:     */           {
/* 1922:2224 */             String root = (String)enm.nextElement();
/* 1923:2225 */             hpp = (HierarchyPropertyParser)hpps.get(root);
/* 1924:2227 */             if (!hpp.goTo(rootPackage)) {}
/* 1925:2230 */             String[] primaryPackages = hpp.childrenValues();
/* 1926:2232 */             for (String primaryPackage : primaryPackages)
/* 1927:     */             {
/* 1928:2234 */               hpp.goToChild(primaryPackage);
/* 1929:2238 */               if (hpp.isLeafReached())
/* 1930:     */               {
/* 1931:2246 */                 String algName = hpp.fullValue();
/* 1932:     */                 
/* 1933:     */ 
/* 1934:2249 */                 Object visibleCheck = KnowledgeFlowApp.this.instantiateBean(toolBarType == 1, tempBeanCompName, algName);
/* 1935:2253 */                 if (visibleCheck != null)
/* 1936:     */                 {
/* 1937:2261 */                   if ((visibleCheck instanceof BeanContextChild)) {
/* 1938:2262 */                     KnowledgeFlowApp.this.m_bcSupport.add(visibleCheck);
/* 1939:     */                   }
/* 1940:2264 */                   ImageIcon scaledForTree = null;
/* 1941:2265 */                   if ((visibleCheck instanceof Visible))
/* 1942:     */                   {
/* 1943:2266 */                     BeanVisual bv = ((Visible)visibleCheck).getVisual();
/* 1944:2267 */                     if (bv != null) {
/* 1945:2268 */                       scaledForTree = new ImageIcon(bv.scale(0.33D));
/* 1946:     */                     }
/* 1947:     */                   }
/* 1948:2274 */                   String toolTip = "";
/* 1949:     */                   try
/* 1950:     */                   {
/* 1951:2276 */                     Object wrappedA = Class.forName(algName).newInstance();
/* 1952:2277 */                     toolTip = KnowledgeFlowApp.getGlobalInfo(wrappedA);
/* 1953:     */                   }
/* 1954:     */                   catch (Exception ex) {}
/* 1955:2281 */                   KnowledgeFlowApp.JTreeLeafDetails leafData = new KnowledgeFlowApp.JTreeLeafDetails(KnowledgeFlowApp.this, tempBeanCompName, algName, scaledForTree);
/* 1956:2284 */                   if ((toolTip != null) && (toolTip.length() > 0)) {
/* 1957:2285 */                     leafData.setToolTipText(toolTip);
/* 1958:     */                   }
/* 1959:2287 */                   DefaultMutableTreeNode leafAlgo = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, leafData);
/* 1960:2288 */                   subTreeNode.add(leafAlgo);
/* 1961:     */                   
/* 1962:2290 */                   this.m_nodeTextIndex.put(algName.toLowerCase() + " " + (toolTip != null ? toolTip.toLowerCase() + " " : ""), leafAlgo);
/* 1963:     */                 }
/* 1964:2295 */                 hpp.goToParent();
/* 1965:     */               }
/* 1966:     */               else
/* 1967:     */               {
/* 1968:2307 */                 DefaultMutableTreeNode firstLevelOfMainAlgoType = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, primaryPackage);
/* 1969:     */                 
/* 1970:     */ 
/* 1971:2310 */                 subTreeNode.add(firstLevelOfMainAlgoType);
/* 1972:     */                 
/* 1973:     */ 
/* 1974:     */ 
/* 1975:2314 */                 KnowledgeFlowApp.this.processPackage(tempBeanCompName, hpp, firstLevelOfMainAlgoType, this.m_nodeTextIndex);
/* 1976:     */               }
/* 1977:     */             }
/* 1978:     */           }
/* 1979:     */         }
/* 1980:     */         else
/* 1981:     */         {
/* 1982:2333 */           for (int j = z; j < tempBarSpecs.size(); j++)
/* 1983:     */           {
/* 1984:2334 */             tempBeanCompName = (String)tempBarSpecs.elementAt(j);
/* 1985:2335 */             Object visibleCheck = KnowledgeFlowApp.this.instantiateBean(toolBarType == 1, tempBeanCompName, "");
/* 1986:2344 */             if (visibleCheck != null)
/* 1987:     */             {
/* 1988:2349 */               String treeName = tempBeanCompName;
/* 1989:2350 */               if (treeName.lastIndexOf('.') > 0) {
/* 1990:2351 */                 treeName = treeName.substring(treeName.lastIndexOf('.') + 1, treeName.length());
/* 1991:     */               }
/* 1992:2359 */               if ((visibleCheck instanceof BeanContextChild)) {
/* 1993:2360 */                 KnowledgeFlowApp.this.m_bcSupport.add(visibleCheck);
/* 1994:     */               }
/* 1995:2362 */               ImageIcon scaledForTree = null;
/* 1996:2364 */               if ((visibleCheck instanceof Visible))
/* 1997:     */               {
/* 1998:2365 */                 BeanVisual bv = ((Visible)visibleCheck).getVisual();
/* 1999:2366 */                 if (bv != null) {
/* 2000:2367 */                   scaledForTree = new ImageIcon(bv.scale(0.33D));
/* 2001:     */                 }
/* 2002:     */               }
/* 2003:2373 */               String tipText = null;
/* 2004:2374 */               tipText = KnowledgeFlowApp.getGlobalInfo(visibleCheck);
/* 2005:     */               
/* 2006:     */ 
/* 2007:     */ 
/* 2008:2378 */               Class<?> compClass = visibleCheck.getClass();
/* 2009:2379 */               Annotation[] annotations = compClass.getDeclaredAnnotations();
/* 2010:     */               
/* 2011:2381 */               String category = null;
/* 2012:2382 */               DefaultMutableTreeNode targetFolder = null;
/* 2013:2383 */               for (Annotation ann : annotations) {
/* 2014:2384 */                 if ((ann instanceof KFStep))
/* 2015:     */                 {
/* 2016:2385 */                   tipText = "<html><font color=blue>" + ((KFStep)ann).toolTipText() + "</font></html>";
/* 2017:     */                   
/* 2018:2387 */                   category = ((KFStep)ann).category();
/* 2019:     */                   
/* 2020:     */ 
/* 2021:2390 */                   Enumeration<Object> children = jtreeRoot.children();
/* 2022:2391 */                   while (children.hasMoreElements())
/* 2023:     */                   {
/* 2024:2392 */                     Object child = children.nextElement();
/* 2025:2393 */                     if (((child instanceof DefaultMutableTreeNode)) && 
/* 2026:2394 */                       (((DefaultMutableTreeNode)child).getUserObject().toString().equals(category)))
/* 2027:     */                     {
/* 2028:2396 */                       targetFolder = (DefaultMutableTreeNode)child;
/* 2029:2397 */                       break;
/* 2030:     */                     }
/* 2031:     */                   }
/* 2032:     */                 }
/* 2033:     */               }
/* 2034:2406 */               KnowledgeFlowApp.JTreeLeafDetails leafData = new KnowledgeFlowApp.JTreeLeafDetails(KnowledgeFlowApp.this, tempBeanCompName, "", scaledForTree);
/* 2035:2408 */               if (tipText != null) {
/* 2036:2409 */                 leafData.setToolTipText(tipText);
/* 2037:     */               }
/* 2038:2411 */               DefaultMutableTreeNode fixedLeafNode = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, leafData);
/* 2039:2413 */               if (targetFolder != null) {
/* 2040:2414 */                 targetFolder.add(fixedLeafNode);
/* 2041:     */               } else {
/* 2042:2416 */                 subTreeNode.add(fixedLeafNode);
/* 2043:     */               }
/* 2044:2419 */               this.m_nodeTextIndex.put(tempBeanCompName.toLowerCase() + " " + (tipText != null ? tipText.toLowerCase() : ""), fixedLeafNode);
/* 2045:     */             }
/* 2046:     */           }
/* 2047:     */         }
/* 2048:     */       }
/* 2049:2442 */       if ((BeansProperties.BEAN_PLUGINS_PROPERTIES != null) && (BeansProperties.BEAN_PLUGINS_PROPERTIES.size() > 0))
/* 2050:     */       {
/* 2051:2445 */         boolean pluginBeans = false;
/* 2052:     */         
/* 2053:2447 */         DefaultMutableTreeNode userSubTree = null;
/* 2054:2448 */         for (int i = 0; i < BeansProperties.BEAN_PLUGINS_PROPERTIES.size(); i++)
/* 2055:     */         {
/* 2056:2449 */           Properties tempP = (Properties)BeansProperties.BEAN_PLUGINS_PROPERTIES.get(i);
/* 2057:2450 */           String components = tempP.getProperty("weka.gui.beans.KnowledgeFlow.Plugins");
/* 2058:2452 */           if ((components != null) && (components.length() > 0))
/* 2059:     */           {
/* 2060:2453 */             StringTokenizer st2 = new StringTokenizer(components, ", ");
/* 2061:2455 */             while (st2.hasMoreTokens())
/* 2062:     */             {
/* 2063:2456 */               String tempBeanCompName = st2.nextToken().trim();
/* 2064:     */               
/* 2065:2458 */               String treeName = tempBeanCompName;
/* 2066:2459 */               if (treeName.lastIndexOf('.') > 0) {
/* 2067:2460 */                 treeName = treeName.substring(treeName.lastIndexOf('.') + 1, treeName.length());
/* 2068:     */               }
/* 2069:2471 */               Object visibleCheck = KnowledgeFlowApp.this.instantiateBean(toolBarType == 1, tempBeanCompName, "");
/* 2070:2473 */               if ((visibleCheck instanceof BeanContextChild)) {
/* 2071:2474 */                 KnowledgeFlowApp.this.m_bcSupport.add(visibleCheck);
/* 2072:     */               }
/* 2073:2476 */               ImageIcon scaledForTree = null;
/* 2074:2477 */               if ((visibleCheck instanceof Visible))
/* 2075:     */               {
/* 2076:2478 */                 BeanVisual bv = ((Visible)visibleCheck).getVisual();
/* 2077:2479 */                 if (bv != null) {
/* 2078:2480 */                   scaledForTree = new ImageIcon(bv.scale(0.33D));
/* 2079:     */                 }
/* 2080:     */               }
/* 2081:2485 */               String tipText = null;
/* 2082:2486 */               tipText = KnowledgeFlowApp.getGlobalInfo(visibleCheck);
/* 2083:     */               
/* 2084:     */ 
/* 2085:2489 */               Class<?> compClass = visibleCheck.getClass();
/* 2086:2490 */               Annotation[] annotations = compClass.getDeclaredAnnotations();
/* 2087:2491 */               DefaultMutableTreeNode targetFolder = null;
/* 2088:2492 */               String category = null;
/* 2089:2494 */               for (Annotation ann : annotations) {
/* 2090:2495 */                 if ((ann instanceof KFStep))
/* 2091:     */                 {
/* 2092:2496 */                   category = ((KFStep)ann).category();
/* 2093:2497 */                   tipText = "<html><font color=red>" + ((KFStep)ann).toolTipText() + "</font></html>";
/* 2094:     */                   
/* 2095:     */ 
/* 2096:     */ 
/* 2097:2501 */                   Enumeration<Object> children = jtreeRoot.children();
/* 2098:2503 */                   while (children.hasMoreElements())
/* 2099:     */                   {
/* 2100:2504 */                     Object child = children.nextElement();
/* 2101:2505 */                     if (((child instanceof DefaultMutableTreeNode)) && 
/* 2102:2506 */                       (((DefaultMutableTreeNode)child).getUserObject().toString().equals(category)))
/* 2103:     */                     {
/* 2104:2508 */                       targetFolder = (DefaultMutableTreeNode)child;
/* 2105:2509 */                       break;
/* 2106:     */                     }
/* 2107:     */                   }
/* 2108:     */                 }
/* 2109:     */               }
/* 2110:2517 */               KnowledgeFlowApp.JTreeLeafDetails leafData = new KnowledgeFlowApp.JTreeLeafDetails(KnowledgeFlowApp.this, tempBeanCompName, "", scaledForTree);
/* 2111:2519 */               if (tipText != null) {
/* 2112:2520 */                 leafData.setToolTipText(tipText);
/* 2113:     */               }
/* 2114:2522 */               DefaultMutableTreeNode pluginLeaf = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, leafData);
/* 2115:     */               
/* 2116:2524 */               this.m_nodeTextIndex.put(tempBeanCompName.toLowerCase() + (tipText != null ? " " + tipText.toLowerCase() : ""), pluginLeaf);
/* 2117:2527 */               if (targetFolder != null)
/* 2118:     */               {
/* 2119:2528 */                 targetFolder.add(pluginLeaf);
/* 2120:     */               }
/* 2121:2529 */               else if (category != null)
/* 2122:     */               {
/* 2123:2531 */                 DefaultMutableTreeNode newCategoryNode = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, category);
/* 2124:     */                 
/* 2125:2533 */                 jtreeRoot.add(newCategoryNode);
/* 2126:2534 */                 newCategoryNode.add(pluginLeaf);
/* 2127:     */               }
/* 2128:     */               else
/* 2129:     */               {
/* 2130:2537 */                 if (!pluginBeans)
/* 2131:     */                 {
/* 2132:2539 */                   userSubTree = new KnowledgeFlowApp.InvisibleNode(KnowledgeFlowApp.this, "Plugins");
/* 2133:2540 */                   jtreeRoot.add(userSubTree);
/* 2134:2541 */                   pluginBeans = true;
/* 2135:     */                 }
/* 2136:2543 */                 userSubTree.add(pluginLeaf);
/* 2137:     */               }
/* 2138:     */             }
/* 2139:     */           }
/* 2140:2549 */           String perspectives = tempP.getProperty("weka.gui.beans.KnowledgeFlow.Perspectives");
/* 2141:2551 */           if ((perspectives != null) && (perspectives.length() > 0))
/* 2142:     */           {
/* 2143:2552 */             StringTokenizer st2 = new StringTokenizer(perspectives, ",");
/* 2144:2553 */             while (st2.hasMoreTokens())
/* 2145:     */             {
/* 2146:2554 */               String className = st2.nextToken();
/* 2147:     */               try
/* 2148:     */               {
/* 2149:2556 */                 if (!PluginManager.isInDisabledList(className))
/* 2150:     */                 {
/* 2151:2559 */                   Object p = Class.forName(className).newInstance();
/* 2152:2560 */                   if (((p instanceof KnowledgeFlowApp.KFPerspective)) && ((p instanceof JPanel)))
/* 2153:     */                   {
/* 2154:2561 */                     String title = ((KnowledgeFlowApp.KFPerspective)p).getPerspectiveTitle();
/* 2155:2562 */                     Logger.log(Logger.Level.INFO, "[KnowledgeFlow] loaded perspective: " + title);
/* 2156:     */                     
/* 2157:     */ 
/* 2158:     */ 
/* 2159:2566 */                     KnowledgeFlowApp.this.m_pluginPerspectiveLookup.put(className, title);
/* 2160:     */                     
/* 2161:     */ 
/* 2162:     */ 
/* 2163:2570 */                     ((KnowledgeFlowApp.KFPerspective)p).setLoaded(false);
/* 2164:2573 */                     if (BeansProperties.VISIBLE_PERSPECTIVES.contains(className)) {
/* 2165:2580 */                       KnowledgeFlowApp.this.m_perspectiveCache.put(className, (KnowledgeFlowApp.KFPerspective)p);
/* 2166:     */                     }
/* 2167:     */                   }
/* 2168:     */                 }
/* 2169:     */               }
/* 2170:     */               catch (Exception ex)
/* 2171:     */               {
/* 2172:2584 */                 if (KnowledgeFlowApp.this.m_logPanel != null)
/* 2173:     */                 {
/* 2174:2585 */                   KnowledgeFlowApp.this.m_logPanel.logMessage("[KnowledgeFlow] WARNING: unable to instantiate perspective \"" + className + "\"");
/* 2175:     */                   
/* 2176:     */ 
/* 2177:     */ 
/* 2178:2589 */                   ex.printStackTrace();
/* 2179:     */                 }
/* 2180:     */                 else
/* 2181:     */                 {
/* 2182:2591 */                   System.err.println("[KnowledgeFlow] WARNING: unable to instantiate perspective \"" + className + "\"");
/* 2183:     */                   
/* 2184:     */ 
/* 2185:     */ 
/* 2186:2595 */                   ex.printStackTrace();
/* 2187:     */                 }
/* 2188:     */               }
/* 2189:     */             }
/* 2190:     */           }
/* 2191:     */         }
/* 2192:     */       }
/* 2193:2603 */       KnowledgeFlowApp.this.m_togglePerspectivesB.setEnabled(KnowledgeFlowApp.this.m_pluginPerspectiveLookup.keySet().size() > 0);
/* 2194:     */       
/* 2195:     */ 
/* 2196:     */ 
/* 2197:     */ 
/* 2198:     */ 
/* 2199:2609 */       add(toolBarPanel, "North");
/* 2200:     */       
/* 2201:2611 */       KnowledgeFlowApp.InvisibleTreeModel model = new KnowledgeFlowApp.InvisibleTreeModel(KnowledgeFlowApp.this, jtreeRoot);
/* 2202:     */       
/* 2203:2613 */       model.activateFilter(true);
/* 2204:     */       
/* 2205:     */ 
/* 2206:     */ 
/* 2207:2617 */       KnowledgeFlowApp.this.m_componentTree = new JTree(model)
/* 2208:     */       {
/* 2209:     */         private static final long serialVersionUID = 6628795889296634120L;
/* 2210:     */         
/* 2211:     */         public String getToolTipText(MouseEvent e)
/* 2212:     */         {
/* 2213:2625 */           if (getRowForLocation(e.getX(), e.getY()) == -1) {
/* 2214:2626 */             return null;
/* 2215:     */           }
/* 2216:2628 */           TreePath currPath = getPathForLocation(e.getX(), e.getY());
/* 2217:2629 */           if ((currPath.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 2218:     */           {
/* 2219:2630 */             DefaultMutableTreeNode node = (DefaultMutableTreeNode)currPath.getLastPathComponent();
/* 2220:2632 */             if (node.isLeaf())
/* 2221:     */             {
/* 2222:2633 */               KnowledgeFlowApp.JTreeLeafDetails leaf = (KnowledgeFlowApp.JTreeLeafDetails)node.getUserObject();
/* 2223:2634 */               return leaf.getToolTipText();
/* 2224:     */             }
/* 2225:     */           }
/* 2226:2637 */           return null;
/* 2227:     */         }
/* 2228:2640 */       };
/* 2229:2641 */       KnowledgeFlowApp.this.m_componentTree.setEnabled(true);
/* 2230:2642 */       KnowledgeFlowApp.this.m_componentTree.setToolTipText("");
/* 2231:2643 */       KnowledgeFlowApp.this.m_componentTree.setRootVisible(false);
/* 2232:2644 */       KnowledgeFlowApp.this.m_componentTree.setShowsRootHandles(true);
/* 2233:2645 */       KnowledgeFlowApp.this.m_componentTree.setCellRenderer(new KnowledgeFlowApp.BeanIconRenderer(KnowledgeFlowApp.this));
/* 2234:2646 */       DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/* 2235:     */       
/* 2236:2648 */       selectionModel.setSelectionMode(1);
/* 2237:2649 */       KnowledgeFlowApp.this.m_componentTree.setSelectionModel(selectionModel);
/* 2238:     */       
/* 2239:2651 */       KnowledgeFlowApp.this.m_componentTree.addMouseListener(new MouseAdapter()
/* 2240:     */       {
/* 2241:     */         public void mouseClicked(MouseEvent e)
/* 2242:     */         {
/* 2243:2655 */           if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/* 2244:     */           {
/* 2245:2657 */             boolean clearSelection = true;
/* 2246:2659 */             if (clearSelection)
/* 2247:     */             {
/* 2248:2661 */               KnowledgeFlowApp.this.m_toolBarBean = null;
/* 2249:2662 */               KnowledgeFlowApp.this.m_mode = 0;
/* 2250:2663 */               KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(0));
/* 2251:     */               
/* 2252:2665 */               KnowledgeFlowApp.this.m_componentTree.clearSelection();
/* 2253:     */             }
/* 2254:     */           }
/* 2255:2669 */           TreePath p = KnowledgeFlowApp.this.m_componentTree.getSelectionPath();
/* 2256:2670 */           if ((p != null) && 
/* 2257:2671 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 2258:     */           {
/* 2259:2672 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 2260:2675 */             if (tNode.isLeaf())
/* 2261:     */             {
/* 2262:2678 */               Object userObject = tNode.getUserObject();
/* 2263:2679 */               if ((userObject instanceof KnowledgeFlowApp.JTreeLeafDetails)) {
/* 2264:2681 */                 if (((e.getModifiers() & 0x1) != 0) && (((KnowledgeFlowApp.JTreeLeafDetails)userObject).isMetaBean()))
/* 2265:     */                 {
/* 2266:2683 */                   if (KnowledgeFlowApp.this.m_firstUserComponentOpp)
/* 2267:     */                   {
/* 2268:2684 */                     KnowledgeFlowApp.this.installWindowListenerForSavingUserStuff();
/* 2269:2685 */                     KnowledgeFlowApp.this.m_firstUserComponentOpp = false;
/* 2270:     */                   }
/* 2271:2688 */                   Vector<Object> toRemove = ((KnowledgeFlowApp.JTreeLeafDetails)userObject).getMetaBean();
/* 2272:     */                   
/* 2273:2690 */                   DefaultTreeModel model = (DefaultTreeModel)KnowledgeFlowApp.this.m_componentTree.getModel();
/* 2274:     */                   
/* 2275:2692 */                   MutableTreeNode userRoot = (MutableTreeNode)tNode.getParent();
/* 2276:     */                   
/* 2277:2694 */                   model.removeNodeFromParent(tNode);
/* 2278:2695 */                   KnowledgeFlowApp.this.m_userComponents.remove(toRemove);
/* 2279:2697 */                   if (KnowledgeFlowApp.this.m_userComponents.size() == 0)
/* 2280:     */                   {
/* 2281:2698 */                     model.removeNodeFromParent(userRoot);
/* 2282:2699 */                     KnowledgeFlowApp.this.m_userCompNode = null;
/* 2283:     */                   }
/* 2284:     */                 }
/* 2285:     */                 else
/* 2286:     */                 {
/* 2287:2703 */                   ((KnowledgeFlowApp.JTreeLeafDetails)userObject).instantiateBean();
/* 2288:     */                 }
/* 2289:     */               }
/* 2290:     */             }
/* 2291:     */           }
/* 2292:     */         }
/* 2293:     */       });
/* 2294:     */     }
/* 2295:     */     
/* 2296:     */     public MainKFPerspective()
/* 2297:     */     {
/* 2298:2714 */       setLayout(new BorderLayout());
/* 2299:2715 */       setUpToolsAndJTree();
/* 2300:     */       
/* 2301:2717 */       JScrollPane treeView = new JScrollPane(KnowledgeFlowApp.this.m_componentTree);
/* 2302:2718 */       JPanel treeHolder = new JPanel();
/* 2303:2719 */       treeHolder.setLayout(new BorderLayout());
/* 2304:2720 */       treeHolder.setBorder(BorderFactory.createTitledBorder("Design"));
/* 2305:2721 */       treeHolder.add(treeView, "Center");
/* 2306:     */       
/* 2307:2723 */       final JTextField searchField = new JTextField();
/* 2308:2724 */       treeHolder.add(searchField, "North");
/* 2309:2725 */       searchField.setToolTipText("Search (clear field to reset)");
/* 2310:     */       
/* 2311:2727 */       searchField.addKeyListener(new KeyAdapter()
/* 2312:     */       {
/* 2313:     */         public void keyReleased(KeyEvent e)
/* 2314:     */         {
/* 2315:2730 */           String searchTerm = searchField.getText();
/* 2316:2731 */           List<DefaultMutableTreeNode> nonhits = new ArrayList();
/* 2317:     */           
/* 2318:2733 */           List<DefaultMutableTreeNode> hits = new ArrayList();
/* 2319:     */           
/* 2320:2735 */           DefaultTreeModel model = (DefaultTreeModel)KnowledgeFlowApp.this.m_componentTree.getModel();
/* 2321:     */           
/* 2322:2737 */           model.reload();
/* 2323:2739 */           for (Map.Entry<String, DefaultMutableTreeNode> entry : KnowledgeFlowApp.MainKFPerspective.this.m_nodeTextIndex.entrySet())
/* 2324:     */           {
/* 2325:2741 */             if ((entry.getValue() instanceof KnowledgeFlowApp.InvisibleNode)) {
/* 2326:2742 */               ((KnowledgeFlowApp.InvisibleNode)entry.getValue()).setVisible(true);
/* 2327:     */             }
/* 2328:2745 */             if ((searchTerm != null) && (searchTerm.length() > 0)) {
/* 2329:2746 */               if (((String)entry.getKey()).contains(searchTerm.toLowerCase())) {
/* 2330:2747 */                 hits.add(entry.getValue());
/* 2331:     */               } else {
/* 2332:2749 */                 nonhits.add(entry.getValue());
/* 2333:     */               }
/* 2334:     */             }
/* 2335:     */           }
/* 2336:2754 */           if ((searchTerm == null) || (searchTerm.length() == 0)) {
/* 2337:2755 */             model.reload();
/* 2338:     */           }
/* 2339:2758 */           if (hits.size() > 0)
/* 2340:     */           {
/* 2341:2759 */             for (DefaultMutableTreeNode h : nonhits) {
/* 2342:2760 */               if ((h instanceof KnowledgeFlowApp.InvisibleNode)) {
/* 2343:2761 */                 ((KnowledgeFlowApp.InvisibleNode)h).setVisible(false);
/* 2344:     */               }
/* 2345:     */             }
/* 2346:2764 */             model.reload();
/* 2347:2767 */             for (DefaultMutableTreeNode h : hits)
/* 2348:     */             {
/* 2349:2768 */               TreeNode[] path = model.getPathToRoot(h);
/* 2350:2769 */               TreePath tpath = new TreePath(path);
/* 2351:2770 */               tpath = tpath.getParentPath();
/* 2352:2771 */               KnowledgeFlowApp.this.m_componentTree.expandPath(tpath);
/* 2353:     */             }
/* 2354:     */           }
/* 2355:     */         }
/* 2356:2778 */       });
/* 2357:2779 */       JSplitPane p2 = new JSplitPane(1, treeHolder, this.m_flowTabs);
/* 2358:     */       
/* 2359:2781 */       p2.setOneTouchExpandable(true);
/* 2360:     */       
/* 2361:2783 */       add(p2, "Center");
/* 2362:     */       
/* 2363:2785 */       Dimension d = treeView.getPreferredSize();
/* 2364:2786 */       d = new Dimension((int)(d.getWidth() * 1.5D), (int)d.getHeight());
/* 2365:2787 */       treeView.setPreferredSize(d);
/* 2366:2788 */       treeView.setMinimumSize(d);
/* 2367:     */       
/* 2368:2790 */       this.m_flowTabs.addChangeListener(new ChangeListener()
/* 2369:     */       {
/* 2370:     */         public void stateChanged(ChangeEvent evt)
/* 2371:     */         {
/* 2372:2796 */           int sel = KnowledgeFlowApp.MainKFPerspective.this.m_flowTabs.getSelectedIndex();
/* 2373:2797 */           KnowledgeFlowApp.MainKFPerspective.this.setActiveTab(sel);
/* 2374:     */         }
/* 2375:     */       });
/* 2376:     */     }
/* 2377:     */     
/* 2378:     */     public synchronized void removeTab(int tabIndex)
/* 2379:     */     {
/* 2380:2803 */       if ((tabIndex < 0) || (tabIndex >= getNumTabs())) {
/* 2381:2804 */         return;
/* 2382:     */       }
/* 2383:2807 */       if (((Boolean)this.m_editedList.get(tabIndex)).booleanValue())
/* 2384:     */       {
/* 2385:2809 */         String tabTitle = this.m_flowTabs.getTitleAt(tabIndex);
/* 2386:2810 */         String message = "\"" + tabTitle + "\" has been modified. Save changes " + "before closing?";
/* 2387:     */         
/* 2388:2812 */         int result = JOptionPane.showConfirmDialog(KnowledgeFlowApp.this, message, "Save changes", 1);
/* 2389:2815 */         if (result == 0) {
/* 2390:2816 */           KnowledgeFlowApp.this.saveLayout(tabIndex, false);
/* 2391:2817 */         } else if (result == 2) {
/* 2392:2818 */           return;
/* 2393:     */         }
/* 2394:     */       }
/* 2395:2822 */       KnowledgeFlowApp.BeanLayout bl = (KnowledgeFlowApp.BeanLayout)this.m_beanLayouts.get(tabIndex);
/* 2396:2823 */       BeanInstance.removeBeanInstances(bl, Integer.valueOf(tabIndex));
/* 2397:2824 */       BeanConnection.removeConnectionList(Integer.valueOf(tabIndex));
/* 2398:2825 */       this.m_beanLayouts.remove(tabIndex);
/* 2399:2826 */       this.m_zoomSettings.remove(tabIndex);
/* 2400:2827 */       this.m_logPanels.remove(tabIndex);
/* 2401:2828 */       this.m_editedList.remove(tabIndex);
/* 2402:2829 */       this.m_environmentSettings.remove(tabIndex);
/* 2403:2830 */       this.m_selectedBeans.remove(tabIndex);
/* 2404:2831 */       bl = null;
/* 2405:     */       
/* 2406:2833 */       this.m_flowTabs.remove(tabIndex);
/* 2407:2835 */       if (getCurrentTabIndex() < 0)
/* 2408:     */       {
/* 2409:2836 */         KnowledgeFlowApp.this.m_beanLayout = null;
/* 2410:2837 */         KnowledgeFlowApp.this.m_logPanel = null;
/* 2411:2838 */         KnowledgeFlowApp.this.m_saveB.setEnabled(false);
/* 2412:     */       }
/* 2413:     */     }
/* 2414:     */     
/* 2415:     */     public synchronized void addTab(String tabTitle)
/* 2416:     */     {
/* 2417:2844 */       BeanInstance.addBeanInstances(new Vector(), null);
/* 2418:     */       
/* 2419:2846 */       BeanConnection.addConnections(new Vector());
/* 2420:     */       
/* 2421:2848 */       JPanel p1 = new JPanel();
/* 2422:2849 */       p1.setLayout(new BorderLayout());
/* 2423:     */       
/* 2424:     */ 
/* 2425:     */ 
/* 2426:     */ 
/* 2427:     */ 
/* 2428:2855 */       KnowledgeFlowApp.BeanLayout tabBeanLayout = new KnowledgeFlowApp.BeanLayout(KnowledgeFlowApp.this);
/* 2429:     */       
/* 2430:2857 */       JScrollPane js = new JScrollPane(tabBeanLayout);
/* 2431:2858 */       p1.add(js, "Center");
/* 2432:2859 */       js.getVerticalScrollBar().setUnitIncrement(KnowledgeFlowApp.this.m_ScrollBarIncrementLayout);
/* 2433:2860 */       js.getHorizontalScrollBar().setUnitIncrement(KnowledgeFlowApp.this.m_ScrollBarIncrementLayout);
/* 2434:     */       
/* 2435:2862 */       KnowledgeFlowApp.this.configureBeanLayout(tabBeanLayout);
/* 2436:2863 */       this.m_beanLayouts.add(tabBeanLayout);
/* 2437:     */       
/* 2438:2865 */       tabBeanLayout.setSize(KnowledgeFlowApp.this.m_FlowWidth, KnowledgeFlowApp.this.m_FlowHeight);
/* 2439:2866 */       Dimension d = tabBeanLayout.getPreferredSize();
/* 2440:2867 */       tabBeanLayout.setMinimumSize(d);
/* 2441:     */       
/* 2442:2869 */       tabBeanLayout.setPreferredSize(d);
/* 2443:     */       
/* 2444:2871 */       this.m_zoomSettings.add(new Integer(100));
/* 2445:     */       
/* 2446:2873 */       KnowledgeFlowApp.KFLogPanel tabLogPanel = new KnowledgeFlowApp.KFLogPanel(KnowledgeFlowApp.this);
/* 2447:2874 */       KnowledgeFlowApp.this.setUpLogPanel(tabLogPanel);
/* 2448:2875 */       Dimension d2 = new Dimension(100, 170);
/* 2449:2876 */       tabLogPanel.setPreferredSize(d2);
/* 2450:2877 */       tabLogPanel.setMinimumSize(d2);
/* 2451:2878 */       this.m_logPanels.add(tabLogPanel);
/* 2452:     */       
/* 2453:2880 */       this.m_environmentSettings.add(new Environment());
/* 2454:2881 */       this.m_filePaths.add(new File("-NONE-"));
/* 2455:     */       
/* 2456:2883 */       JSplitPane p2 = new JSplitPane(0, p1, tabLogPanel);
/* 2457:     */       
/* 2458:2885 */       p2.setOneTouchExpandable(true);
/* 2459:     */       
/* 2460:2887 */       p2.setDividerLocation(0.7D);
/* 2461:2888 */       p2.setResizeWeight(1.0D);
/* 2462:2889 */       JPanel splitHolder = new JPanel();
/* 2463:2890 */       splitHolder.setLayout(new BorderLayout());
/* 2464:2891 */       splitHolder.add(p2, "Center");
/* 2465:     */       
/* 2466:     */ 
/* 2467:     */ 
/* 2468:2895 */       this.m_editedList.add(new Boolean(false));
/* 2469:2896 */       this.m_executingList.add(new Boolean(false));
/* 2470:2897 */       this.m_executionThreads.add((KnowledgeFlowApp.RunThread)null);
/* 2471:2898 */       this.m_selectedBeans.add(new Vector());
/* 2472:2899 */       this.m_undoBufferList.add(new Stack());
/* 2473:     */       
/* 2474:2901 */       this.m_flowTabs.addTab(tabTitle, splitHolder);
/* 2475:2902 */       int tabIndex = getNumTabs() - 1;
/* 2476:2903 */       this.m_flowTabs.setTabComponentAt(tabIndex, new KnowledgeFlowApp.CloseableTabTitle(KnowledgeFlowApp.this, this.m_flowTabs));
/* 2477:2904 */       setActiveTab(getNumTabs() - 1);
/* 2478:2905 */       KnowledgeFlowApp.this.m_saveB.setEnabled(true);
/* 2479:     */     }
/* 2480:     */   }
/* 2481:     */   
/* 2482:     */   private class CloseableTabTitle
/* 2483:     */     extends JPanel
/* 2484:     */   {
/* 2485:     */     private static final long serialVersionUID = -6844232025394346426L;
/* 2486:     */     private final JTabbedPane m_enclosingPane;
/* 2487:     */     private JLabel m_tabLabel;
/* 2488:     */     private TabButton m_tabButton;
/* 2489:     */     
/* 2490:     */     public CloseableTabTitle(JTabbedPane pane)
/* 2491:     */     {
/* 2492:2921 */       super();
/* 2493:     */       
/* 2494:2923 */       this.m_enclosingPane = pane;
/* 2495:2924 */       setOpaque(false);
/* 2496:2925 */       setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
/* 2497:     */       
/* 2498:     */ 
/* 2499:2928 */       this.m_tabLabel = new JLabel()
/* 2500:     */       {
/* 2501:     */         private static final long serialVersionUID = 8515052190461050324L;
/* 2502:     */         
/* 2503:     */         public String getText()
/* 2504:     */         {
/* 2505:2936 */           int index = KnowledgeFlowApp.CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(KnowledgeFlowApp.CloseableTabTitle.this);
/* 2506:2938 */           if (index >= 0) {
/* 2507:2939 */             return KnowledgeFlowApp.CloseableTabTitle.this.m_enclosingPane.getTitleAt(index);
/* 2508:     */           }
/* 2509:2941 */           return null;
/* 2510:     */         }
/* 2511:2944 */       };
/* 2512:2945 */       add(this.m_tabLabel);
/* 2513:2946 */       this.m_tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
/* 2514:2947 */       this.m_tabButton = new TabButton();
/* 2515:2948 */       add(this.m_tabButton);
/* 2516:     */     }
/* 2517:     */     
/* 2518:     */     public void setBold(boolean bold)
/* 2519:     */     {
/* 2520:2953 */       this.m_tabLabel.setEnabled(bold);
/* 2521:     */     }
/* 2522:     */     
/* 2523:     */     public void setButtonEnabled(boolean enabled)
/* 2524:     */     {
/* 2525:2957 */       this.m_tabButton.setEnabled(enabled);
/* 2526:     */     }
/* 2527:     */     
/* 2528:     */     private class TabButton
/* 2529:     */       extends JButton
/* 2530:     */       implements ActionListener
/* 2531:     */     {
/* 2532:     */       private static final long serialVersionUID = -4915800749132175968L;
/* 2533:     */       
/* 2534:     */       public TabButton()
/* 2535:     */       {
/* 2536:2967 */         int size = 17;
/* 2537:2968 */         setPreferredSize(new Dimension(size, size));
/* 2538:2969 */         setToolTipText("close this tab");
/* 2539:     */         
/* 2540:2971 */         setUI(new BasicButtonUI());
/* 2541:     */         
/* 2542:2973 */         setContentAreaFilled(false);
/* 2543:     */         
/* 2544:2975 */         setFocusable(false);
/* 2545:2976 */         setBorder(BorderFactory.createEtchedBorder());
/* 2546:2977 */         setBorderPainted(false);
/* 2547:     */         
/* 2548:     */ 
/* 2549:2980 */         addMouseListener(new MouseAdapter()
/* 2550:     */         {
/* 2551:     */           public void mouseEntered(MouseEvent e)
/* 2552:     */           {
/* 2553:2983 */             Component component = e.getComponent();
/* 2554:2985 */             if ((component instanceof AbstractButton))
/* 2555:     */             {
/* 2556:2986 */               AbstractButton button = (AbstractButton)component;
/* 2557:2987 */               button.setBorderPainted(true);
/* 2558:     */               
/* 2559:2989 */               int i = KnowledgeFlowApp.CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(KnowledgeFlowApp.CloseableTabTitle.this);
/* 2560:2991 */               if (i == KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) {
/* 2561:2992 */                 button.setToolTipText("close this tab (Ctrl+W)");
/* 2562:     */               } else {
/* 2563:2994 */                 button.setToolTipText("close this tab");
/* 2564:     */               }
/* 2565:     */             }
/* 2566:     */           }
/* 2567:     */           
/* 2568:     */           public void mouseExited(MouseEvent e)
/* 2569:     */           {
/* 2570:3001 */             Component component = e.getComponent();
/* 2571:3002 */             if ((component instanceof AbstractButton))
/* 2572:     */             {
/* 2573:3003 */               AbstractButton button = (AbstractButton)component;
/* 2574:3004 */               button.setBorderPainted(false);
/* 2575:     */             }
/* 2576:     */           }
/* 2577:3007 */         });
/* 2578:3008 */         setRolloverEnabled(true);
/* 2579:     */         
/* 2580:3010 */         addActionListener(this);
/* 2581:     */       }
/* 2582:     */       
/* 2583:     */       public void actionPerformed(ActionEvent e)
/* 2584:     */       {
/* 2585:3015 */         int i = KnowledgeFlowApp.CloseableTabTitle.this.m_enclosingPane.indexOfTabComponent(KnowledgeFlowApp.CloseableTabTitle.this);
/* 2586:3016 */         if ((i >= 0) && (KnowledgeFlowApp.this.getAllowMultipleTabs())) {
/* 2587:3018 */           KnowledgeFlowApp.this.m_mainKFPerspective.removeTab(i);
/* 2588:     */         }
/* 2589:     */       }
/* 2590:     */       
/* 2591:     */       public void updateUI() {}
/* 2592:     */       
/* 2593:     */       protected void paintComponent(Graphics g)
/* 2594:     */       {
/* 2595:3030 */         super.paintComponent(g);
/* 2596:3031 */         Graphics2D g2 = (Graphics2D)g.create();
/* 2597:3033 */         if (getModel().isPressed()) {
/* 2598:3034 */           g2.translate(1, 1);
/* 2599:     */         }
/* 2600:3036 */         g2.setStroke(new BasicStroke(2.0F));
/* 2601:3037 */         g2.setColor(Color.BLACK);
/* 2602:3038 */         if (!isEnabled()) {
/* 2603:3039 */           g2.setColor(Color.GRAY);
/* 2604:     */         }
/* 2605:3041 */         if (getModel().isRollover()) {
/* 2606:3042 */           g2.setColor(Color.MAGENTA);
/* 2607:     */         }
/* 2608:3044 */         int delta = 6;
/* 2609:3045 */         g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
/* 2610:     */         
/* 2611:3047 */         g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
/* 2612:     */         
/* 2613:3049 */         g2.dispose();
/* 2614:     */       }
/* 2615:     */     }
/* 2616:     */   }
/* 2617:     */   
/* 2618:3067 */   private int m_mode = 0;
/* 2619:     */   protected static final String USERCOMPONENTS_XML_EXTENSION = ".xml";
/* 2620:     */   private Object m_toolBarBean;
/* 2621:3078 */   private final int m_gridSpacing = 40;
/* 2622:3081 */   protected int m_untitledCount = 1;
/* 2623:3086 */   private BeanLayout m_beanLayout = null;
/* 2624:3088 */   private int m_layoutZoom = 100;
/* 2625:3091 */   private boolean m_allowMultipleTabs = true;
/* 2626:3093 */   private final Vector<Object> m_userComponents = new Vector();
/* 2627:3095 */   private boolean m_firstUserComponentOpp = true;
/* 2628:     */   protected JButton m_pointerB;
/* 2629:     */   protected JButton m_saveB;
/* 2630:     */   protected JButton m_saveBB;
/* 2631:     */   protected JButton m_loadB;
/* 2632:     */   protected JButton m_stopB;
/* 2633:     */   protected JButton m_playB;
/* 2634:     */   protected JButton m_playBB;
/* 2635:     */   protected JButton m_helpB;
/* 2636:     */   protected JButton m_newB;
/* 2637:     */   protected JButton m_togglePerspectivesB;
/* 2638:     */   protected JButton m_templatesB;
/* 2639:     */   protected JButton m_groupB;
/* 2640:     */   protected JButton m_cutB;
/* 2641:     */   protected JButton m_copyB;
/* 2642:     */   protected JButton m_pasteB;
/* 2643:     */   protected JButton m_deleteB;
/* 2644:     */   protected JButton m_noteB;
/* 2645:     */   protected JButton m_selectAllB;
/* 2646:     */   protected JButton m_undoB;
/* 2647:     */   protected JButton m_zoomInB;
/* 2648:     */   protected JButton m_zoomOutB;
/* 2649:     */   protected JToggleButton m_snapToGridB;
/* 2650:     */   private BeanInstance m_editElement;
/* 2651:     */   private EventSetDescriptor m_sourceEventSetDescriptor;
/* 2652:     */   private int m_oldX;
/* 2653:     */   private int m_oldY;
/* 2654:     */   private int m_startX;
/* 2655:     */   private int m_startY;
/* 2656:3140 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/* 2657:     */   
/* 2658:     */   protected class KFLogPanel
/* 2659:     */     extends LogPanel
/* 2660:     */   {
/* 2661:     */     private static final long serialVersionUID = -2224509243343105276L;
/* 2662:     */     
/* 2663:     */     protected KFLogPanel() {}
/* 2664:     */     
/* 2665:     */     public synchronized void setMessageOnAll(boolean mainKFLine, String message)
/* 2666:     */     {
/* 2667:3151 */       for (String key : this.m_tableIndexes.keySet()) {
/* 2668:3152 */         if ((mainKFLine) || (!key.equals("[KnowledgeFlow]")))
/* 2669:     */         {
/* 2670:3156 */           String tm = key + "|" + message;
/* 2671:3157 */           statusMessage(tm);
/* 2672:     */         }
/* 2673:     */       }
/* 2674:     */     }
/* 2675:     */   }
/* 2676:     */   
/* 2677:3162 */   protected KFLogPanel m_logPanel = null;
/* 2678:3166 */   protected JToolBar m_perspectiveToolBar = new JToolBar(0);
/* 2679:     */   protected JPanel m_configAndPerspectives;
/* 2680:3175 */   protected boolean m_configAndPerspectivesVisible = true;
/* 2681:3180 */   private final ButtonGroup m_perspectiveGroup = new ButtonGroup();
/* 2682:     */   protected JPanel m_perspectiveHolder;
/* 2683:3189 */   protected List<KFPerspective> m_perspectives = new ArrayList();
/* 2684:3192 */   protected Thread m_perspectiveDataLoadThread = null;
/* 2685:     */   protected AttributeSelectionPanel m_perspectiveConfigurer;
/* 2686:     */   protected MainKFPerspective m_mainKFPerspective;
/* 2687:3199 */   protected BeanContextSupport m_bcSupport = new BeanContextSupport();
/* 2688:     */   public static final String FILE_EXTENSION = ".kf";
/* 2689:     */   public static final String FILE_EXTENSION_XML = ".kfml";
/* 2690:3211 */   protected FileFilter m_KfFilter = new ExtensionFileFilter(".kf", "Binary KnowledgeFlow configuration files (*.kf)");
/* 2691:3218 */   protected FileFilter m_KOMLFilter = new ExtensionFileFilter(".komlkf", "XML KnowledgeFlow configuration files (*.komlkf)");
/* 2692:3226 */   protected FileFilter m_XStreamFilter = new ExtensionFileFilter(".xstreamkf", "XML KnowledgeFlow configuration files (*.xstreamkf)");
/* 2693:3234 */   protected FileFilter m_XMLFilter = new ExtensionFileFilter(".kfml", "XML KnowledgeFlow layout files (*.kfml)");
/* 2694:3239 */   protected int m_ScrollBarIncrementLayout = 20;
/* 2695:3242 */   protected int m_ScrollBarIncrementComponents = 50;
/* 2696:3245 */   protected int m_FlowWidth = 2560;
/* 2697:3248 */   protected int m_FlowHeight = 1440;
/* 2698:3251 */   protected String m_PreferredExtension = ".kf";
/* 2699:3254 */   protected boolean m_UserComponentsInXML = false;
/* 2700:3257 */   protected Environment m_flowEnvironment = new Environment();
/* 2701:     */   protected JTree m_componentTree;
/* 2702:     */   protected DefaultMutableTreeNode m_userCompNode;
/* 2703:     */   protected StringBuffer m_pasteBuffer;
/* 2704:     */   private static KnowledgeFlowApp m_knowledgeFlow;
/* 2705:     */   
/* 2706:     */   public void setEnvironment(Environment env)
/* 2707:     */   {
/* 2708:3275 */     this.m_flowEnvironment = env;
/* 2709:3276 */     setEnvironment();
/* 2710:     */   }
/* 2711:     */   
/* 2712:     */   private void setEnvironment()
/* 2713:     */   {
/* 2714:3282 */     Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 2715:3284 */     for (int i = 0; i < beans.size(); i++)
/* 2716:     */     {
/* 2717:3285 */       Object temp = ((BeanInstance)beans.elementAt(i)).getBean();
/* 2718:3287 */       if ((temp instanceof EnvironmentHandler)) {
/* 2719:3288 */         ((EnvironmentHandler)temp).setEnvironment(this.m_flowEnvironment);
/* 2720:     */       }
/* 2721:     */     }
/* 2722:     */   }
/* 2723:     */   
/* 2724:     */   public KnowledgeFlowApp(boolean showFileMenu)
/* 2725:     */   {
/* 2726:3299 */     if (BeansProperties.BEAN_PROPERTIES == null)
/* 2727:     */     {
/* 2728:3300 */       loadProperties();
/* 2729:3301 */       init();
/* 2730:     */     }
/* 2731:3304 */     this.m_showFileMenu = showFileMenu;
/* 2732:     */     
/* 2733:     */ 
/* 2734:     */ 
/* 2735:3308 */     JWindow temp = new JWindow();
/* 2736:3309 */     temp.setVisible(true);
/* 2737:3310 */     temp.getGraphics().setFont(new Font(null, 0, 9));
/* 2738:3311 */     this.m_fontM = temp.getGraphics().getFontMetrics();
/* 2739:3312 */     temp.setVisible(false);
/* 2740:     */     try
/* 2741:     */     {
/* 2742:3316 */       this.m_ScrollBarIncrementLayout = Integer.parseInt(BeansProperties.BEAN_PROPERTIES.getProperty("ScrollBarIncrementLayout", "" + this.m_ScrollBarIncrementLayout));
/* 2743:     */       
/* 2744:     */ 
/* 2745:3319 */       this.m_ScrollBarIncrementComponents = Integer.parseInt(BeansProperties.BEAN_PROPERTIES.getProperty("ScrollBarIncrementComponents", "" + this.m_ScrollBarIncrementComponents));
/* 2746:     */       
/* 2747:     */ 
/* 2748:3322 */       this.m_FlowWidth = Integer.parseInt(BeansProperties.BEAN_PROPERTIES.getProperty("FlowWidth", "" + this.m_FlowWidth));
/* 2749:     */       
/* 2750:3324 */       this.m_FlowHeight = Integer.parseInt(BeansProperties.BEAN_PROPERTIES.getProperty("FlowHeight", "" + this.m_FlowHeight));
/* 2751:     */       
/* 2752:3326 */       this.m_PreferredExtension = BeansProperties.BEAN_PROPERTIES.getProperty("PreferredExtension", this.m_PreferredExtension);
/* 2753:     */       
/* 2754:3328 */       this.m_UserComponentsInXML = Boolean.valueOf(BeansProperties.BEAN_PROPERTIES.getProperty("UserComponentsInXML", "" + this.m_UserComponentsInXML)).booleanValue();
/* 2755:     */     }
/* 2756:     */     catch (Exception ex)
/* 2757:     */     {
/* 2758:3332 */       ex.printStackTrace();
/* 2759:     */     }
/* 2760:3336 */     this.m_FileChooser.addChoosableFileFilter(this.m_KfFilter);
/* 2761:3337 */     if (KOML.isPresent()) {
/* 2762:3338 */       this.m_FileChooser.addChoosableFileFilter(this.m_KOMLFilter);
/* 2763:     */     }
/* 2764:3340 */     if (XStream.isPresent()) {
/* 2765:3341 */       this.m_FileChooser.addChoosableFileFilter(this.m_XStreamFilter);
/* 2766:     */     }
/* 2767:3344 */     this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
/* 2768:3346 */     if (this.m_PreferredExtension.equals(".kfml")) {
/* 2769:3347 */       this.m_FileChooser.setFileFilter(this.m_XMLFilter);
/* 2770:3348 */     } else if ((KOML.isPresent()) && (this.m_PreferredExtension.equals(".komlkf"))) {
/* 2771:3350 */       this.m_FileChooser.setFileFilter(this.m_KOMLFilter);
/* 2772:3351 */     } else if ((XStream.isPresent()) && (this.m_PreferredExtension.equals(".xstreamkf"))) {
/* 2773:3353 */       this.m_FileChooser.setFileFilter(this.m_XStreamFilter);
/* 2774:     */     } else {
/* 2775:3355 */       this.m_FileChooser.setFileFilter(this.m_KfFilter);
/* 2776:     */     }
/* 2777:3357 */     this.m_FileChooser.setFileSelectionMode(0);
/* 2778:     */     
/* 2779:3359 */     this.m_bcSupport.setDesignTime(true);
/* 2780:     */     
/* 2781:3361 */     this.m_perspectiveHolder = new JPanel();
/* 2782:3362 */     this.m_perspectiveHolder.setLayout(new BorderLayout());
/* 2783:     */     
/* 2784:     */ 
/* 2785:     */ 
/* 2786:     */ 
/* 2787:     */ 
/* 2788:     */ 
/* 2789:     */ 
/* 2790:     */ 
/* 2791:     */ 
/* 2792:3372 */     this.m_mainKFPerspective = new MainKFPerspective();
/* 2793:     */     
/* 2794:     */ 
/* 2795:3375 */     this.m_perspectives.add(this.m_mainKFPerspective);
/* 2796:     */     
/* 2797:     */ 
/* 2798:3378 */     setLayout(new BorderLayout());
/* 2799:     */     
/* 2800:3380 */     add(this.m_perspectiveHolder, "Center");
/* 2801:     */     
/* 2802:     */ 
/* 2803:     */ 
/* 2804:     */ 
/* 2805:3385 */     this.m_perspectiveHolder.add(this.m_mainKFPerspective, "Center");
/* 2806:3387 */     if (this.m_pluginPerspectiveLookup.size() > 0)
/* 2807:     */     {
/* 2808:3390 */       String titleM = this.m_mainKFPerspective.getPerspectiveTitle();
/* 2809:3391 */       Icon icon = this.m_mainKFPerspective.getPerspectiveIcon();
/* 2810:3392 */       JToggleButton tBut = new JToggleButton(titleM, icon, true);
/* 2811:3393 */       tBut.addActionListener(new ActionListener()
/* 2812:     */       {
/* 2813:     */         public void actionPerformed(ActionEvent e)
/* 2814:     */         {
/* 2815:3396 */           KnowledgeFlowApp.KFPerspective current = (KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectiveHolder.getComponent(0);
/* 2816:     */           
/* 2817:3398 */           current.setActive(false);
/* 2818:3399 */           KnowledgeFlowApp.this.m_perspectiveHolder.remove(0);
/* 2819:3400 */           KnowledgeFlowApp.this.m_perspectiveHolder.add((JComponent)KnowledgeFlowApp.this.m_perspectives.get(0), "Center");
/* 2820:     */           
/* 2821:3402 */           ((KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectives.get(0)).setActive(true);
/* 2822:     */           
/* 2823:3404 */           KnowledgeFlowApp.this.revalidate();
/* 2824:3405 */           KnowledgeFlowApp.this.repaint();
/* 2825:3406 */           KnowledgeFlowApp.this.notifyIsDirty();
/* 2826:     */         }
/* 2827:3408 */       });
/* 2828:3409 */       this.m_perspectiveToolBar.add(tBut);
/* 2829:3410 */       this.m_perspectiveGroup.add(tBut);
/* 2830:     */       
/* 2831:     */ 
/* 2832:     */ 
/* 2833:     */ 
/* 2834:3415 */       setupUserPerspectives();
/* 2835:     */     }
/* 2836:3418 */     if (this.m_pluginPerspectiveLookup.size() > 0)
/* 2837:     */     {
/* 2838:3419 */       this.m_configAndPerspectives = new JPanel();
/* 2839:3420 */       this.m_configAndPerspectives.setLayout(new BorderLayout());
/* 2840:3421 */       this.m_configAndPerspectives.add(this.m_perspectiveToolBar, "Center");
/* 2841:     */       try
/* 2842:     */       {
/* 2843:3424 */         Properties visible = Utils.readProperties(BeansProperties.VISIBLE_PERSPECTIVES_PROPERTIES_FILE);
/* 2844:     */         
/* 2845:3426 */         Enumeration<?> keys = visible.propertyNames();
/* 2846:3427 */         if (keys.hasMoreElements())
/* 2847:     */         {
/* 2848:3429 */           String toolBarIsVisible = visible.getProperty("weka.gui.beans.KnowledgeFlow.PerspectiveToolBarVisisble");
/* 2849:3432 */           if ((toolBarIsVisible != null) && (toolBarIsVisible.length() > 0)) {
/* 2850:3433 */             this.m_configAndPerspectivesVisible = toolBarIsVisible.equalsIgnoreCase("yes");
/* 2851:     */           }
/* 2852:     */         }
/* 2853:     */       }
/* 2854:     */       catch (Exception ex)
/* 2855:     */       {
/* 2856:3438 */         System.err.println("Problem reading visible perspectives property file");
/* 2857:     */         
/* 2858:3440 */         ex.printStackTrace();
/* 2859:     */       }
/* 2860:3445 */       if (this.m_configAndPerspectivesVisible) {
/* 2861:3446 */         add(this.m_configAndPerspectives, "North");
/* 2862:     */       }
/* 2863:3449 */       JButton configB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/cog.png")));
/* 2864:     */       
/* 2865:3451 */       configB.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 1));
/* 2866:3452 */       configB.setToolTipText("Enable/disable perspectives");
/* 2867:3453 */       this.m_configAndPerspectives.add(configB, "West");
/* 2868:     */       
/* 2869:3455 */       configB.addActionListener(new ActionListener()
/* 2870:     */       {
/* 2871:     */         public void actionPerformed(ActionEvent e)
/* 2872:     */         {
/* 2873:3458 */           if (!Utils.getDontShowDialog("weka.gui.beans.KnowledgeFlow.PerspectiveInfo"))
/* 2874:     */           {
/* 2875:3460 */             JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 2876:     */             
/* 2877:3462 */             Object[] stuff = new Object[2];
/* 2878:3463 */             stuff[0] = "Perspectives are environments that take over the\nKnowledge Flow UI and provide major additional functionality.\nMany perspectives will operate on a set of instances. Instances\nCan be sent to a perspective by placing a DataSource on the\nlayout canvas, configuring it and then selecting \"Send to perspective\"\nfrom the contextual popup menu that appears when you right-click on\nit. Several perspectives are built in to the Knowledge Flow, others\ncan be installed via the package manager.\n";
/* 2879:     */             
/* 2880:     */ 
/* 2881:     */ 
/* 2882:     */ 
/* 2883:     */ 
/* 2884:     */ 
/* 2885:     */ 
/* 2886:     */ 
/* 2887:3472 */             stuff[1] = dontShow;
/* 2888:     */             
/* 2889:3474 */             JOptionPane.showMessageDialog(KnowledgeFlowApp.this, stuff, "Perspective information", 0);
/* 2890:3477 */             if (dontShow.isSelected()) {
/* 2891:     */               try
/* 2892:     */               {
/* 2893:3479 */                 Utils.setDontShowDialog("weka.gui.beans.KnowledgeFlow.PerspectiveInfo");
/* 2894:     */               }
/* 2895:     */               catch (Exception ex) {}
/* 2896:     */             }
/* 2897:     */           }
/* 2898:3487 */           KnowledgeFlowApp.this.popupPerspectiveConfigurer();
/* 2899:     */         }
/* 2900:     */       });
/* 2901:     */     }
/* 2902:3493 */     for (String pName : this.m_perspectiveCache.keySet()) {
/* 2903:3494 */       ((KFPerspective)this.m_perspectiveCache.get(pName)).setMainKFPerspective(this.m_mainKFPerspective);
/* 2904:     */     }
/* 2905:3497 */     loadUserComponents();
/* 2906:3498 */     clearLayout();
/* 2907:     */   }
/* 2908:     */   
/* 2909:     */   public MainKFPerspective getMainPerspective()
/* 2910:     */   {
/* 2911:3507 */     return this.m_mainKFPerspective;
/* 2912:     */   }
/* 2913:     */   
/* 2914:     */   private void popupPerspectiveConfigurer()
/* 2915:     */   {
/* 2916:3511 */     if (this.m_perspectiveConfigurer == null) {
/* 2917:3512 */       this.m_perspectiveConfigurer = new AttributeSelectionPanel(true, true, true, true);
/* 2918:     */     }
/* 2919:3516 */     if (this.m_firstUserComponentOpp)
/* 2920:     */     {
/* 2921:3517 */       installWindowListenerForSavingUserStuff();
/* 2922:3518 */       this.m_firstUserComponentOpp = false;
/* 2923:     */     }
/* 2924:3521 */     ArrayList<Attribute> atts = new ArrayList();
/* 2925:3522 */     final ArrayList<String> pClasses = new ArrayList();
/* 2926:3523 */     SortedSet<String> sortedPerspectives = new TreeSet();
/* 2927:3524 */     for (String clName : this.m_pluginPerspectiveLookup.keySet()) {
/* 2928:3525 */       sortedPerspectives.add(clName);
/* 2929:     */     }
/* 2930:3527 */     for (String clName : sortedPerspectives)
/* 2931:     */     {
/* 2932:3528 */       pClasses.add(clName);
/* 2933:3529 */       String pName = (String)this.m_pluginPerspectiveLookup.get(clName);
/* 2934:3530 */       atts.add(new Attribute(pName));
/* 2935:     */     }
/* 2936:3532 */     Instances perspectiveInstances = new Instances("Perspectives", atts, 1);
/* 2937:     */     
/* 2938:3534 */     boolean[] selectedPerspectives = new boolean[perspectiveInstances.numAttributes()];
/* 2939:3536 */     for (String selected : BeansProperties.VISIBLE_PERSPECTIVES)
/* 2940:     */     {
/* 2941:3537 */       String pName = (String)this.m_pluginPerspectiveLookup.get(selected);
/* 2942:3543 */       if (pName != null)
/* 2943:     */       {
/* 2944:3544 */         int index = perspectiveInstances.attribute(pName).index();
/* 2945:3545 */         selectedPerspectives[index] = true;
/* 2946:     */       }
/* 2947:     */     }
/* 2948:3549 */     this.m_perspectiveConfigurer.setInstances(perspectiveInstances);
/* 2949:     */     try
/* 2950:     */     {
/* 2951:3551 */       this.m_perspectiveConfigurer.setSelectedAttributes(selectedPerspectives);
/* 2952:     */     }
/* 2953:     */     catch (Exception ex)
/* 2954:     */     {
/* 2955:3553 */       ex.printStackTrace();
/* 2956:3554 */       return;
/* 2957:     */     }
/* 2958:3557 */     final JDialog d = new JDialog((JFrame)getTopLevelAncestor(), "Manage Perspectives", Dialog.ModalityType.DOCUMENT_MODAL);
/* 2959:     */     
/* 2960:     */ 
/* 2961:3560 */     d.setLayout(new BorderLayout());
/* 2962:     */     
/* 2963:3562 */     JPanel holder = new JPanel();
/* 2964:3563 */     holder.setLayout(new BorderLayout());
/* 2965:3564 */     holder.add(this.m_perspectiveConfigurer, "Center");
/* 2966:3565 */     JButton okBut = new JButton("OK");
/* 2967:3566 */     JButton cancelBut = new JButton("Cancel");
/* 2968:3567 */     JPanel butHolder = new JPanel();
/* 2969:3568 */     butHolder.setLayout(new GridLayout(1, 2));
/* 2970:3569 */     butHolder.add(okBut);
/* 2971:3570 */     butHolder.add(cancelBut);
/* 2972:3571 */     holder.add(butHolder, "South");
/* 2973:3572 */     okBut.addActionListener(new ActionListener()
/* 2974:     */     {
/* 2975:     */       public void actionPerformed(ActionEvent e)
/* 2976:     */       {
/* 2977:3575 */         BeansProperties.VISIBLE_PERSPECTIVES = new TreeSet();
/* 2978:     */         
/* 2979:3577 */         int[] selected = KnowledgeFlowApp.this.m_perspectiveConfigurer.getSelectedAttributes();
/* 2980:3578 */         for (int element : selected)
/* 2981:     */         {
/* 2982:3579 */           String selectedClassName = (String)pClasses.get(element);
/* 2983:3582 */           if (KnowledgeFlowApp.this.m_perspectiveCache.get(selectedClassName) == null) {
/* 2984:     */             try
/* 2985:     */             {
/* 2986:3586 */               Object p = Class.forName(selectedClassName).newInstance();
/* 2987:3587 */               if (((p instanceof KnowledgeFlowApp.KFPerspective)) && ((p instanceof JPanel)))
/* 2988:     */               {
/* 2989:3588 */                 String title = ((KnowledgeFlowApp.KFPerspective)p).getPerspectiveTitle();
/* 2990:3589 */                 Logger.log(Logger.Level.INFO, "[KnowledgeFlow] loaded perspective: " + title);
/* 2991:     */                 
/* 2992:     */ 
/* 2993:     */ 
/* 2994:     */ 
/* 2995:3594 */                 ((KnowledgeFlowApp.KFPerspective)p).setLoaded(true);
/* 2996:3595 */                 ((KnowledgeFlowApp.KFPerspective)p).setMainKFPerspective(KnowledgeFlowApp.this.m_mainKFPerspective);
/* 2997:3596 */                 KnowledgeFlowApp.this.m_perspectiveCache.put(selectedClassName, (KnowledgeFlowApp.KFPerspective)p);
/* 2998:     */               }
/* 2999:     */             }
/* 3000:     */             catch (Exception ex)
/* 3001:     */             {
/* 3002:3599 */               ex.printStackTrace();
/* 3003:     */             }
/* 3004:     */           }
/* 3005:3602 */           BeansProperties.VISIBLE_PERSPECTIVES.add(selectedClassName);
/* 3006:     */         }
/* 3007:3604 */         KnowledgeFlowApp.this.setupUserPerspectives();
/* 3008:     */         
/* 3009:3606 */         d.dispose();
/* 3010:     */       }
/* 3011:3609 */     });
/* 3012:3610 */     cancelBut.addActionListener(new ActionListener()
/* 3013:     */     {
/* 3014:     */       public void actionPerformed(ActionEvent e)
/* 3015:     */       {
/* 3016:3613 */         d.dispose();
/* 3017:     */       }
/* 3018:3616 */     });
/* 3019:3617 */     d.getContentPane().add(holder, "Center");
/* 3020:     */     
/* 3021:     */ 
/* 3022:     */ 
/* 3023:     */ 
/* 3024:     */ 
/* 3025:     */ 
/* 3026:     */ 
/* 3027:     */ 
/* 3028:3626 */     d.pack();
/* 3029:3627 */     d.setVisible(true);
/* 3030:     */   }
/* 3031:     */   
/* 3032:     */   private void setupUserPerspectives()
/* 3033:     */   {
/* 3034:3632 */     for (int i = this.m_perspectiveToolBar.getComponentCount() - 1; i > 0; i--)
/* 3035:     */     {
/* 3036:3633 */       this.m_perspectiveToolBar.remove(i);
/* 3037:3634 */       this.m_perspectives.remove(i);
/* 3038:     */     }
/* 3039:3637 */     int index = 1;
/* 3040:3638 */     for (String c : BeansProperties.VISIBLE_PERSPECTIVES)
/* 3041:     */     {
/* 3042:3639 */       KFPerspective toAdd = (KFPerspective)this.m_perspectiveCache.get(c);
/* 3043:3640 */       if ((toAdd instanceof JComponent))
/* 3044:     */       {
/* 3045:3641 */         toAdd.setLoaded(true);
/* 3046:3642 */         this.m_perspectives.add(toAdd);
/* 3047:3643 */         String titleM = toAdd.getPerspectiveTitle();
/* 3048:3644 */         Icon icon = toAdd.getPerspectiveIcon();
/* 3049:3645 */         JToggleButton tBut = null;
/* 3050:3646 */         if (icon != null) {
/* 3051:3647 */           tBut = new JToggleButton(titleM, icon, false);
/* 3052:     */         } else {
/* 3053:3649 */           tBut = new JToggleButton(titleM, false);
/* 3054:     */         }
/* 3055:3651 */         tBut.setToolTipText(toAdd.getPerspectiveTipText());
/* 3056:3652 */         final int theIndex = index;
/* 3057:3653 */         tBut.addActionListener(new ActionListener()
/* 3058:     */         {
/* 3059:     */           public void actionPerformed(ActionEvent e)
/* 3060:     */           {
/* 3061:3656 */             KnowledgeFlowApp.this.setActivePerspective(theIndex);
/* 3062:     */           }
/* 3063:3658 */         });
/* 3064:3659 */         this.m_perspectiveToolBar.add(tBut);
/* 3065:3660 */         this.m_perspectiveGroup.add(tBut);
/* 3066:     */         
/* 3067:3662 */         index++;
/* 3068:     */       }
/* 3069:     */     }
/* 3070:3666 */     KFPerspective current = (KFPerspective)this.m_perspectiveHolder.getComponent(0);
/* 3071:3667 */     if (current != this.m_mainKFPerspective)
/* 3072:     */     {
/* 3073:3668 */       current.setActive(false);
/* 3074:3669 */       this.m_perspectiveHolder.remove(0);
/* 3075:3670 */       this.m_perspectiveHolder.add(this.m_mainKFPerspective);
/* 3076:3671 */       ((JToggleButton)this.m_perspectiveToolBar.getComponent(0)).setSelected(true);
/* 3077:     */     }
/* 3078:3674 */     revalidate();
/* 3079:3675 */     repaint();
/* 3080:3676 */     notifyIsDirty();
/* 3081:     */   }
/* 3082:     */   
/* 3083:     */   protected void setActivePerspective(int theIndex)
/* 3084:     */   {
/* 3085:3680 */     if ((theIndex < 0) || (theIndex > this.m_perspectives.size() - 1)) {
/* 3086:3681 */       return;
/* 3087:     */     }
/* 3088:3684 */     KFPerspective current = (KFPerspective)this.m_perspectiveHolder.getComponent(0);
/* 3089:3685 */     current.setActive(false);
/* 3090:3686 */     this.m_perspectiveHolder.remove(0);
/* 3091:3687 */     this.m_perspectiveHolder.add((JComponent)this.m_perspectives.get(theIndex), "Center");
/* 3092:     */     
/* 3093:3689 */     ((KFPerspective)this.m_perspectives.get(theIndex)).setActive(true);
/* 3094:3690 */     ((JToggleButton)this.m_perspectiveToolBar.getComponent(theIndex)).setSelected(true);
/* 3095:     */     
/* 3096:     */ 
/* 3097:     */ 
/* 3098:3694 */     revalidate();
/* 3099:3695 */     repaint();
/* 3100:3696 */     notifyIsDirty();
/* 3101:     */   }
/* 3102:     */   
/* 3103:     */   private void snapSelectedToGrid()
/* 3104:     */   {
/* 3105:3700 */     Vector<Object> v = this.m_mainKFPerspective.getSelectedBeans();
/* 3106:3701 */     if (v.size() > 0)
/* 3107:     */     {
/* 3108:3702 */       for (int i = 0; i < v.size(); i++)
/* 3109:     */       {
/* 3110:3703 */         BeanInstance b = (BeanInstance)v.get(i);
/* 3111:     */         
/* 3112:3705 */         int x = b.getX();
/* 3113:3706 */         int y = b.getY();
/* 3114:3707 */         b.setXY(snapToGrid(x), snapToGrid(y));
/* 3115:     */       }
/* 3116:3710 */       revalidate();
/* 3117:3711 */       this.m_beanLayout.repaint();
/* 3118:3712 */       notifyIsDirty();
/* 3119:3713 */       this.m_mainKFPerspective.setEditedStatus(true);
/* 3120:     */     }
/* 3121:     */   }
/* 3122:     */   
/* 3123:     */   private int snapToGrid(int val)
/* 3124:     */   {
/* 3125:3718 */     int r = val % 40;
/* 3126:3719 */     val /= 40;
/* 3127:3720 */     if (r > 20) {
/* 3128:3721 */       val++;
/* 3129:     */     }
/* 3130:3723 */     val *= 40;
/* 3131:     */     
/* 3132:3725 */     return val;
/* 3133:     */   }
/* 3134:     */   
/* 3135:     */   private void configureBeanLayout(final BeanLayout layout)
/* 3136:     */   {
/* 3137:3730 */     layout.setLayout(null);
/* 3138:     */     
/* 3139:     */ 
/* 3140:3733 */     layout.addMouseListener(new MouseAdapter()
/* 3141:     */     {
/* 3142:     */       public void mousePressed(MouseEvent me)
/* 3143:     */       {
/* 3144:3737 */         layout.requestFocusInWindow();
/* 3145:3738 */         double z = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/* 3146:3739 */         double px = me.getX();
/* 3147:3740 */         double py = me.getY();
/* 3148:3741 */         py /= z;
/* 3149:3742 */         px /= z;
/* 3150:3743 */         if ((KnowledgeFlowApp.this.m_toolBarBean == null) && 
/* 3151:3744 */           ((me.getModifiers() & 0x10) == 16) && (KnowledgeFlowApp.this.m_mode == 0))
/* 3152:     */         {
/* 3153:3750 */           BeanInstance bi = BeanInstance.findInstance(new Point((int)px, (int)py), new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3154:     */           
/* 3155:3752 */           JComponent bc = null;
/* 3156:3753 */           if (bi != null) {
/* 3157:3754 */             bc = (JComponent)bi.getBean();
/* 3158:     */           }
/* 3159:3757 */           if (bc != null)
/* 3160:     */           {
/* 3161:3758 */             KnowledgeFlowApp.this.m_editElement = bi;
/* 3162:     */             
/* 3163:     */ 
/* 3164:     */ 
/* 3165:     */ 
/* 3166:     */ 
/* 3167:3764 */             KnowledgeFlowApp.this.m_oldX = ((int)px);
/* 3168:3765 */             KnowledgeFlowApp.this.m_oldY = ((int)py);
/* 3169:     */             
/* 3170:3767 */             KnowledgeFlowApp.this.m_mode = 1;
/* 3171:     */           }
/* 3172:3769 */           if (KnowledgeFlowApp.this.m_mode != 1)
/* 3173:     */           {
/* 3174:3770 */             KnowledgeFlowApp.this.m_mode = 4;
/* 3175:     */             
/* 3176:     */ 
/* 3177:     */ 
/* 3178:     */ 
/* 3179:     */ 
/* 3180:3776 */             KnowledgeFlowApp.this.m_oldX = ((int)px);
/* 3181:3777 */             KnowledgeFlowApp.this.m_oldY = ((int)py);
/* 3182:     */             
/* 3183:3779 */             KnowledgeFlowApp.this.m_startX = KnowledgeFlowApp.this.m_oldX;
/* 3184:3780 */             KnowledgeFlowApp.this.m_startY = KnowledgeFlowApp.this.m_oldY;
/* 3185:3781 */             Graphics2D gx = (Graphics2D)layout.getGraphics();
/* 3186:3782 */             gx.setXORMode(Color.white);
/* 3187:     */             
/* 3188:     */ 
/* 3189:3785 */             gx.dispose();
/* 3190:3786 */             KnowledgeFlowApp.this.m_mode = 4;
/* 3191:     */           }
/* 3192:     */         }
/* 3193:     */       }
/* 3194:     */       
/* 3195:     */       public void mouseReleased(MouseEvent me)
/* 3196:     */       {
/* 3197:3794 */         layout.requestFocusInWindow();
/* 3198:3795 */         if ((KnowledgeFlowApp.this.m_editElement != null) && (KnowledgeFlowApp.this.m_mode == 1))
/* 3199:     */         {
/* 3200:3796 */           if (KnowledgeFlowApp.this.m_snapToGridB.isSelected())
/* 3201:     */           {
/* 3202:3797 */             int x = KnowledgeFlowApp.this.snapToGrid(KnowledgeFlowApp.this.m_editElement.getX());
/* 3203:3798 */             int y = KnowledgeFlowApp.this.snapToGrid(KnowledgeFlowApp.this.m_editElement.getY());
/* 3204:3799 */             KnowledgeFlowApp.this.m_editElement.setXY(x, y);
/* 3205:3800 */             KnowledgeFlowApp.this.snapSelectedToGrid();
/* 3206:     */           }
/* 3207:3803 */           KnowledgeFlowApp.this.m_editElement = null;
/* 3208:3804 */           KnowledgeFlowApp.this.revalidate();
/* 3209:3805 */           layout.repaint();
/* 3210:3806 */           KnowledgeFlowApp.this.m_mode = 0;
/* 3211:     */         }
/* 3212:3808 */         if (KnowledgeFlowApp.this.m_mode == 4)
/* 3213:     */         {
/* 3214:3809 */           KnowledgeFlowApp.this.revalidate();
/* 3215:3810 */           layout.repaint();
/* 3216:3811 */           KnowledgeFlowApp.this.m_mode = 0;
/* 3217:     */           
/* 3218:3813 */           double z = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/* 3219:3814 */           double px = me.getX();
/* 3220:3815 */           double py = me.getY();
/* 3221:3816 */           py /= z;
/* 3222:3817 */           px /= z;
/* 3223:     */           
/* 3224:     */ 
/* 3225:3820 */           KnowledgeFlowApp.this.highlightSubFlow(KnowledgeFlowApp.this.m_startX, KnowledgeFlowApp.this.m_startY, (int)px, (int)py);
/* 3226:     */         }
/* 3227:     */       }
/* 3228:     */       
/* 3229:     */       public void mouseClicked(MouseEvent me)
/* 3230:     */       {
/* 3231:3826 */         layout.requestFocusInWindow();
/* 3232:3827 */         Point p = me.getPoint();
/* 3233:3828 */         Point np = new Point();
/* 3234:3829 */         double z = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/* 3235:3830 */         np.setLocation(p.getX() / z, p.getY() / z);
/* 3236:3831 */         BeanInstance bi = BeanInstance.findInstance(np, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3237:3833 */         if ((KnowledgeFlowApp.this.m_mode == 3) || (KnowledgeFlowApp.this.m_mode == 0))
/* 3238:     */         {
/* 3239:3836 */           if (bi != null)
/* 3240:     */           {
/* 3241:3837 */             JComponent bc = (JComponent)bi.getBean();
/* 3242:3840 */             if ((me.getClickCount() == 2) && (!(bc instanceof MetaBean)))
/* 3243:     */             {
/* 3244:     */               try
/* 3245:     */               {
/* 3246:3842 */                 Class<?> custClass = Introspector.getBeanInfo(bc.getClass()).getBeanDescriptor().getCustomizerClass();
/* 3247:3844 */                 if (custClass != null) {
/* 3248:3845 */                   if ((bc instanceof BeanCommon))
/* 3249:     */                   {
/* 3250:3846 */                     if ((!((BeanCommon)bc).isBusy()) && (!KnowledgeFlowApp.this.m_mainKFPerspective.getExecuting())) {
/* 3251:3848 */                       KnowledgeFlowApp.this.popupCustomizer(custClass, bc);
/* 3252:     */                     }
/* 3253:     */                   }
/* 3254:     */                   else {
/* 3255:3851 */                     KnowledgeFlowApp.this.popupCustomizer(custClass, bc);
/* 3256:     */                   }
/* 3257:     */                 }
/* 3258:     */               }
/* 3259:     */               catch (IntrospectionException ex)
/* 3260:     */               {
/* 3261:3855 */                 ex.printStackTrace();
/* 3262:     */               }
/* 3263:     */             }
/* 3264:     */             else
/* 3265:     */             {
/* 3266:3857 */               if (((me.getModifiers() & 0x10) != 16) || (me.isAltDown()))
/* 3267:     */               {
/* 3268:3860 */                 KnowledgeFlowApp.this.doPopup(me.getPoint(), bi, (int)(p.getX() / z), (int)(p.getY() / z));
/* 3269:     */                 
/* 3270:3862 */                 return;
/* 3271:     */               }
/* 3272:3865 */               Vector<Object> v = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/* 3273:3866 */               if (!me.isShiftDown()) {
/* 3274:3868 */                 v = new Vector();
/* 3275:     */               }
/* 3276:3870 */               v.add(bi);
/* 3277:3871 */               KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(v);
/* 3278:     */               
/* 3279:3873 */               return;
/* 3280:     */             }
/* 3281:     */           }
/* 3282:     */           else
/* 3283:     */           {
/* 3284:3876 */             if (((me.getModifiers() & 0x10) != 16) || (me.isAltDown()))
/* 3285:     */             {
/* 3286:3879 */               double px = me.getX();
/* 3287:3880 */               double py = me.getY();
/* 3288:3881 */               py /= z;
/* 3289:3882 */               px /= z;
/* 3290:3885 */               if (!KnowledgeFlowApp.this.m_mainKFPerspective.getExecuting())
/* 3291:     */               {
/* 3292:3887 */                 KnowledgeFlowApp.this.rightClickCanvasPopup((int)px, (int)py);
/* 3293:     */                 
/* 3294:3889 */                 KnowledgeFlowApp.this.revalidate();
/* 3295:3890 */                 KnowledgeFlowApp.this.repaint();
/* 3296:3891 */                 KnowledgeFlowApp.this.notifyIsDirty();
/* 3297:     */               }
/* 3298:3893 */               return;
/* 3299:     */             }
/* 3300:3900 */             if (KnowledgeFlowApp.this.m_toolBarBean != null)
/* 3301:     */             {
/* 3302:3905 */               double x = me.getX();
/* 3303:3906 */               double y = me.getY();
/* 3304:3907 */               x /= z;
/* 3305:3908 */               y /= z;
/* 3306:3909 */               if (KnowledgeFlowApp.this.m_snapToGridB.isSelected())
/* 3307:     */               {
/* 3308:3911 */                 x = KnowledgeFlowApp.this.snapToGrid((int)x);
/* 3309:     */                 
/* 3310:3913 */                 y = KnowledgeFlowApp.this.snapToGrid((int)y);
/* 3311:     */               }
/* 3312:3916 */               KnowledgeFlowApp.this.addUndoPoint();
/* 3313:3917 */               if ((KnowledgeFlowApp.this.m_toolBarBean instanceof StringBuffer))
/* 3314:     */               {
/* 3315:3919 */                 KnowledgeFlowApp.this.pasteFromClipboard((int)x, (int)y, (StringBuffer)KnowledgeFlowApp.this.m_toolBarBean, false);
/* 3316:     */                 
/* 3317:3921 */                 KnowledgeFlowApp.this.m_mode = 0;
/* 3318:3922 */                 KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(0));
/* 3319:     */                 
/* 3320:3924 */                 KnowledgeFlowApp.this.m_toolBarBean = null;
/* 3321:     */               }
/* 3322:     */               else
/* 3323:     */               {
/* 3324:3927 */                 KnowledgeFlowApp.this.addComponent((int)x, (int)y);
/* 3325:     */               }
/* 3326:3929 */               KnowledgeFlowApp.this.m_componentTree.clearSelection();
/* 3327:3930 */               KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3328:     */             }
/* 3329:     */           }
/* 3330:3933 */           KnowledgeFlowApp.this.revalidate();
/* 3331:3934 */           KnowledgeFlowApp.this.repaint();
/* 3332:3935 */           KnowledgeFlowApp.this.notifyIsDirty();
/* 3333:     */         }
/* 3334:3938 */         double px = me.getX();
/* 3335:3939 */         double py = me.getY();
/* 3336:3940 */         px /= z;
/* 3337:3941 */         py /= z;
/* 3338:3942 */         if ((KnowledgeFlowApp.this.m_mode == 5) && (KnowledgeFlowApp.this.m_pasteBuffer.length() > 0))
/* 3339:     */         {
/* 3340:3944 */           KnowledgeFlowApp.this.pasteFromClipboard((int)px, (int)py, KnowledgeFlowApp.this.m_pasteBuffer, true);
/* 3341:3945 */           KnowledgeFlowApp.this.m_mode = 0;
/* 3342:3946 */           KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(0));
/* 3343:     */           
/* 3344:3948 */           return;
/* 3345:     */         }
/* 3346:3951 */         if (KnowledgeFlowApp.this.m_mode == 2)
/* 3347:     */         {
/* 3348:3953 */           layout.repaint();
/* 3349:3954 */           Vector<Object> beanInstances = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3350:3956 */           for (int i = 0; i < beanInstances.size(); i++)
/* 3351:     */           {
/* 3352:3957 */             JComponent bean = (JComponent)((BeanInstance)beanInstances.elementAt(i)).getBean();
/* 3353:3959 */             if ((bean instanceof Visible)) {
/* 3354:3960 */               ((Visible)bean).getVisual().setDisplayConnectors(false);
/* 3355:     */             }
/* 3356:     */           }
/* 3357:3964 */           if (bi != null)
/* 3358:     */           {
/* 3359:3965 */             boolean doConnection = false;
/* 3360:3966 */             if (!(bi.getBean() instanceof BeanCommon)) {
/* 3361:3967 */               doConnection = true;
/* 3362:3971 */             } else if (((BeanCommon)bi.getBean()).connectionAllowed(KnowledgeFlowApp.this.m_sourceEventSetDescriptor)) {
/* 3363:3974 */               doConnection = true;
/* 3364:     */             }
/* 3365:3977 */             if (doConnection)
/* 3366:     */             {
/* 3367:3979 */               KnowledgeFlowApp.this.addUndoPoint();
/* 3368:3981 */               if ((bi.getBean() instanceof MetaBean)) {
/* 3369:3982 */                 BeanConnection.doMetaConnection(KnowledgeFlowApp.this.m_editElement, bi, KnowledgeFlowApp.this.m_sourceEventSetDescriptor, layout, KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex());
/* 3370:     */               } else {
/* 3371:3986 */                 new BeanConnection(KnowledgeFlowApp.this.m_editElement, bi, KnowledgeFlowApp.this.m_sourceEventSetDescriptor, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3372:     */               }
/* 3373:3990 */               KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3374:     */             }
/* 3375:3992 */             layout.repaint();
/* 3376:     */           }
/* 3377:3994 */           KnowledgeFlowApp.this.m_mode = 0;
/* 3378:3995 */           KnowledgeFlowApp.this.m_editElement = null;
/* 3379:3996 */           KnowledgeFlowApp.this.m_sourceEventSetDescriptor = null;
/* 3380:     */         }
/* 3381:3999 */         if (KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans().size() > 0) {
/* 3382:4000 */           KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 3383:     */         }
/* 3384:     */       }
/* 3385:4004 */     });
/* 3386:4005 */     layout.addMouseMotionListener(new MouseMotionAdapter()
/* 3387:     */     {
/* 3388:     */       public void mouseDragged(MouseEvent me)
/* 3389:     */       {
/* 3390:4009 */         double z = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/* 3391:4010 */         double px = me.getX();
/* 3392:4011 */         double py = me.getY();
/* 3393:4012 */         px /= z;
/* 3394:4013 */         py /= z;
/* 3395:4014 */         if ((KnowledgeFlowApp.this.m_editElement != null) && (KnowledgeFlowApp.this.m_mode == 1))
/* 3396:     */         {
/* 3397:4020 */           int deltaX = (int)px - KnowledgeFlowApp.this.m_oldX;
/* 3398:4021 */           int deltaY = (int)py - KnowledgeFlowApp.this.m_oldY;
/* 3399:     */           
/* 3400:4023 */           KnowledgeFlowApp.this.m_editElement.setXY(KnowledgeFlowApp.this.m_editElement.getX() + deltaX, KnowledgeFlowApp.this.m_editElement.getY() + deltaY);
/* 3401:4026 */           if (KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans().size() > 0)
/* 3402:     */           {
/* 3403:4027 */             Vector<Object> v = KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans();
/* 3404:4028 */             for (int i = 0; i < v.size(); i++)
/* 3405:     */             {
/* 3406:4029 */               BeanInstance b = (BeanInstance)v.get(i);
/* 3407:4030 */               if (b != KnowledgeFlowApp.this.m_editElement) {
/* 3408:4031 */                 b.setXY(b.getX() + deltaX, b.getY() + deltaY);
/* 3409:     */               }
/* 3410:     */             }
/* 3411:     */           }
/* 3412:4035 */           layout.repaint();
/* 3413:     */           
/* 3414:     */ 
/* 3415:     */ 
/* 3416:     */ 
/* 3417:     */ 
/* 3418:4041 */           KnowledgeFlowApp.this.m_oldX = ((int)px);
/* 3419:4042 */           KnowledgeFlowApp.this.m_oldY = ((int)py);
/* 3420:4043 */           KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3421:     */         }
/* 3422:4045 */         if (KnowledgeFlowApp.this.m_mode == 4)
/* 3423:     */         {
/* 3424:4046 */           layout.repaint();
/* 3425:     */           
/* 3426:     */ 
/* 3427:     */ 
/* 3428:4050 */           KnowledgeFlowApp.this.m_oldX = ((int)px);
/* 3429:4051 */           KnowledgeFlowApp.this.m_oldY = ((int)py);
/* 3430:     */         }
/* 3431:     */       }
/* 3432:     */       
/* 3433:     */       public void mouseMoved(MouseEvent e)
/* 3434:     */       {
/* 3435:4057 */         double z = KnowledgeFlowApp.this.m_layoutZoom / 100.0D;
/* 3436:4058 */         double px = e.getX();
/* 3437:4059 */         double py = e.getY();
/* 3438:4060 */         px /= z;
/* 3439:4061 */         py /= z;
/* 3440:4063 */         if (KnowledgeFlowApp.this.m_mode == 2)
/* 3441:     */         {
/* 3442:4064 */           layout.repaint();
/* 3443:     */           
/* 3444:     */ 
/* 3445:     */ 
/* 3446:     */ 
/* 3447:     */ 
/* 3448:4070 */           KnowledgeFlowApp.this.m_oldX = ((int)px);
/* 3449:4071 */           KnowledgeFlowApp.this.m_oldY = ((int)py);
/* 3450:     */         }
/* 3451:     */       }
/* 3452:     */     });
/* 3453:     */   }
/* 3454:     */   
/* 3455:     */   private void setUpLogPanel(final LogPanel logPanel)
/* 3456:     */   {
/* 3457:4078 */     String date = new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date());
/* 3458:     */     
/* 3459:4080 */     logPanel.logMessage("Weka Knowledge Flow was written by Mark Hall");
/* 3460:4081 */     logPanel.logMessage("Weka Knowledge Flow");
/* 3461:4082 */     logPanel.logMessage("(c) 2002-" + Copyright.getToYear() + " " + Copyright.getOwner() + ", " + Copyright.getAddress());
/* 3462:     */     
/* 3463:4084 */     logPanel.logMessage("web: " + Copyright.getURL());
/* 3464:4085 */     logPanel.logMessage(date);
/* 3465:4086 */     logPanel.statusMessage("@!@[KnowledgeFlow]|Welcome to the Weka Knowledge Flow");
/* 3466:     */     
/* 3467:4088 */     logPanel.getStatusTable().addMouseListener(new MouseAdapter()
/* 3468:     */     {
/* 3469:     */       public void mouseClicked(MouseEvent e)
/* 3470:     */       {
/* 3471:4091 */         if ((logPanel.getStatusTable().rowAtPoint(e.getPoint()) == 0) && (
/* 3472:4092 */           ((e.getModifiers() & 0x10) != 16) || (e.isAltDown())))
/* 3473:     */         {
/* 3474:4094 */           System.gc();
/* 3475:4095 */           Runtime currR = Runtime.getRuntime();
/* 3476:4096 */           long freeM = currR.freeMemory();
/* 3477:4097 */           long totalM = currR.totalMemory();
/* 3478:4098 */           long maxM = currR.maxMemory();
/* 3479:4099 */           logPanel.logMessage("[KnowledgeFlow] Memory (free/total/max.) in bytes: " + String.format("%,d", new Object[] { Long.valueOf(freeM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(totalM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(maxM) }));
/* 3480:     */           
/* 3481:     */ 
/* 3482:     */ 
/* 3483:     */ 
/* 3484:4104 */           logPanel.statusMessage("@!@[KnowledgeFlow]|Memory (free/total/max.) in bytes: " + String.format("%,d", new Object[] { Long.valueOf(freeM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(totalM) }) + " / " + String.format("%,d", new Object[] { Long.valueOf(maxM) }));
/* 3485:     */         }
/* 3486:     */       }
/* 3487:     */     });
/* 3488:     */   }
/* 3489:     */   
/* 3490:     */   private Image loadImage(String path)
/* 3491:     */   {
/* 3492:4118 */     Image pic = null;
/* 3493:     */     
/* 3494:     */ 
/* 3495:4121 */     URL imageURL = getClass().getClassLoader().getResource(path);
/* 3496:4124 */     if (imageURL == null) {
/* 3497:4125 */       Logger.log(Logger.Level.WARNING, "Unable to load " + path);
/* 3498:     */     } else {
/* 3499:4128 */       pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 3500:     */     }
/* 3501:4130 */     return pic;
/* 3502:     */   }
/* 3503:     */   
/* 3504:     */   protected class RunThread
/* 3505:     */     extends Thread
/* 3506:     */   {
/* 3507:     */     int m_flowIndex;
/* 3508:     */     boolean m_sequential;
/* 3509:4136 */     boolean m_wasUserStopped = false;
/* 3510:     */     
/* 3511:     */     public RunThread(boolean sequential)
/* 3512:     */     {
/* 3513:4139 */       this.m_sequential = sequential;
/* 3514:     */     }
/* 3515:     */     
/* 3516:     */     public void run()
/* 3517:     */     {
/* 3518:4144 */       this.m_flowIndex = KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex();
/* 3519:4145 */       String flowName = KnowledgeFlowApp.this.m_mainKFPerspective.getTabTitle(this.m_flowIndex);
/* 3520:4146 */       KnowledgeFlowApp.this.m_mainKFPerspective.setExecuting(true);
/* 3521:4147 */       KnowledgeFlowApp.this.m_mainKFPerspective.getLogPanel(this.m_flowIndex).clearStatus();
/* 3522:4148 */       KnowledgeFlowApp.this.m_mainKFPerspective.getLogPanel(this.m_flowIndex).statusMessage("@!@[KnowledgeFlow]|Executing...");
/* 3523:     */       
/* 3524:     */ 
/* 3525:4151 */       FlowRunner runner = new FlowRunner(false, false);
/* 3526:4152 */       runner.setStartSequentially(this.m_sequential);
/* 3527:4153 */       runner.setEnvironment(KnowledgeFlowApp.this.m_flowEnvironment);
/* 3528:4154 */       runner.setLog(KnowledgeFlowApp.this.m_logPanel);
/* 3529:4155 */       Vector<Object> comps = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_flowIndex) });
/* 3530:     */       
/* 3531:4157 */       runner.setFlows(comps);
/* 3532:     */       try
/* 3533:     */       {
/* 3534:4159 */         runner.run();
/* 3535:4160 */         runner.waitUntilFinished();
/* 3536:     */       }
/* 3537:     */       catch (InterruptedException ie) {}catch (Exception ex)
/* 3538:     */       {
/* 3539:     */         int i;
/* 3540:     */         String tabT;
/* 3541:     */         KnowledgeFlowApp.KFLogPanel lp;
/* 3542:     */         int i;
/* 3543:     */         String tabT;
/* 3544:     */         KnowledgeFlowApp.KFLogPanel lp;
/* 3545:4164 */         KnowledgeFlowApp.this.m_logPanel.logMessage("An error occurred while running the flow: " + ex.getMessage());
/* 3546:     */       }
/* 3547:     */       finally
/* 3548:     */       {
/* 3549:     */         int i;
/* 3550:     */         String tabT;
/* 3551:     */         KnowledgeFlowApp.KFLogPanel lp;
/* 3552:4167 */         if ((this.m_flowIndex >= KnowledgeFlowApp.this.m_mainKFPerspective.getNumTabs() - 1) || (!KnowledgeFlowApp.this.m_mainKFPerspective.getTabTitle(this.m_flowIndex).equals(flowName))) {
/* 3553:4171 */           for (int i = 0; i < KnowledgeFlowApp.this.m_mainKFPerspective.getNumTabs(); i++)
/* 3554:     */           {
/* 3555:4172 */             String tabT = KnowledgeFlowApp.this.m_mainKFPerspective.getTabTitle(i);
/* 3556:4173 */             if ((tabT != null) && (tabT.equals(flowName)))
/* 3557:     */             {
/* 3558:4174 */               this.m_flowIndex = i;
/* 3559:4175 */               break;
/* 3560:     */             }
/* 3561:     */           }
/* 3562:     */         }
/* 3563:4180 */         KnowledgeFlowApp.this.m_mainKFPerspective.setExecuting(this.m_flowIndex, false);
/* 3564:4181 */         KnowledgeFlowApp.this.m_mainKFPerspective.setExecutionThread(this.m_flowIndex, null);
/* 3565:4182 */         if (this.m_wasUserStopped)
/* 3566:     */         {
/* 3567:4184 */           KnowledgeFlowApp.KFLogPanel lp = KnowledgeFlowApp.this.m_mainKFPerspective.getLogPanel(this.m_flowIndex);
/* 3568:4185 */           lp.setMessageOnAll(false, "Stopped.");
/* 3569:     */         }
/* 3570:     */         else
/* 3571:     */         {
/* 3572:4187 */           KnowledgeFlowApp.this.m_mainKFPerspective.getLogPanel(this.m_flowIndex).statusMessage("@!@[KnowledgeFlow]|OK.");
/* 3573:     */         }
/* 3574:     */       }
/* 3575:     */     }
/* 3576:     */     
/* 3577:     */     public void stopAllFlows()
/* 3578:     */     {
/* 3579:4194 */       Vector<Object> components = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_flowIndex) });
/* 3580:4196 */       if (components != null)
/* 3581:     */       {
/* 3582:4197 */         for (int i = 0; i < components.size(); i++)
/* 3583:     */         {
/* 3584:4198 */           Object temp = ((BeanInstance)components.elementAt(i)).getBean();
/* 3585:4200 */           if ((temp instanceof BeanCommon)) {
/* 3586:4201 */             ((BeanCommon)temp).stop();
/* 3587:     */           }
/* 3588:     */         }
/* 3589:4204 */         this.m_wasUserStopped = true;
/* 3590:     */       }
/* 3591:     */     }
/* 3592:     */   }
/* 3593:     */   
/* 3594:     */   private void runFlow(boolean sequential)
/* 3595:     */   {
/* 3596:4222 */     if (this.m_mainKFPerspective.getNumTabs() > 0)
/* 3597:     */     {
/* 3598:4223 */       RunThread runThread = new RunThread(sequential);
/* 3599:4224 */       this.m_mainKFPerspective.setExecutionThread(runThread);
/* 3600:     */       
/* 3601:4226 */       runThread.start();
/* 3602:     */     }
/* 3603:     */   }
/* 3604:     */   
/* 3605:     */   private void stopFlow()
/* 3606:     */   {
/* 3607:4231 */     if (this.m_mainKFPerspective.getCurrentTabIndex() >= 0)
/* 3608:     */     {
/* 3609:4232 */       RunThread running = this.m_mainKFPerspective.getExecutionThread();
/* 3610:4234 */       if (running != null) {
/* 3611:4235 */         running.stopAllFlows();
/* 3612:     */       }
/* 3613:     */     }
/* 3614:     */   }
/* 3615:     */   
/* 3616:     */   private void processPackage(String tempBeanCompName, HierarchyPropertyParser hpp, DefaultMutableTreeNode parentNode, Map<String, DefaultMutableTreeNode> nodeTextIndex)
/* 3617:     */   {
/* 3618:4254 */     if (hpp.isLeafReached())
/* 3619:     */     {
/* 3620:4263 */       hpp.goToParent();
/* 3621:4264 */       return;
/* 3622:     */     }
/* 3623:4266 */     String[] children = hpp.childrenValues();
/* 3624:4267 */     for (String element : children)
/* 3625:     */     {
/* 3626:4268 */       hpp.goToChild(element);
/* 3627:4269 */       DefaultMutableTreeNode child = null;
/* 3628:4271 */       if (hpp.isLeafReached())
/* 3629:     */       {
/* 3630:4272 */         String algName = hpp.fullValue();
/* 3631:     */         
/* 3632:4274 */         Object visibleCheck = instantiateBean(true, tempBeanCompName, algName);
/* 3633:4275 */         if (visibleCheck != null)
/* 3634:     */         {
/* 3635:4276 */           if ((visibleCheck instanceof BeanContextChild)) {
/* 3636:4277 */             this.m_bcSupport.add(visibleCheck);
/* 3637:     */           }
/* 3638:4279 */           ImageIcon scaledForTree = null;
/* 3639:4280 */           if ((visibleCheck instanceof Visible))
/* 3640:     */           {
/* 3641:4281 */             BeanVisual bv = ((Visible)visibleCheck).getVisual();
/* 3642:4282 */             if (bv != null) {
/* 3643:4283 */               scaledForTree = new ImageIcon(bv.scale(0.33D));
/* 3644:     */             }
/* 3645:     */           }
/* 3646:4289 */           String toolTip = "";
/* 3647:     */           try
/* 3648:     */           {
/* 3649:4291 */             Object wrappedA = Class.forName(algName).newInstance();
/* 3650:4292 */             toolTip = getGlobalInfo(wrappedA);
/* 3651:     */           }
/* 3652:     */           catch (Exception ex) {}
/* 3653:4296 */           JTreeLeafDetails leafData = new JTreeLeafDetails(tempBeanCompName, algName, scaledForTree);
/* 3654:4298 */           if ((toolTip != null) && (toolTip.length() > 0)) {
/* 3655:4299 */             leafData.setToolTipText(toolTip);
/* 3656:     */           }
/* 3657:4301 */           child = new InvisibleNode(leafData);
/* 3658:4302 */           nodeTextIndex.put(algName.toLowerCase() + " " + (toolTip != null ? toolTip.toLowerCase() : ""), child);
/* 3659:     */         }
/* 3660:     */       }
/* 3661:     */       else
/* 3662:     */       {
/* 3663:4306 */         child = new InvisibleNode(element);
/* 3664:     */       }
/* 3665:4309 */       if (child != null) {
/* 3666:4310 */         parentNode.add(child);
/* 3667:     */       }
/* 3668:4313 */       processPackage(tempBeanCompName, hpp, child, nodeTextIndex);
/* 3669:     */     }
/* 3670:4315 */     hpp.goToParent();
/* 3671:     */   }
/* 3672:     */   
/* 3673:     */   private Object instantiateBean(boolean wekawrapper, String tempBeanCompName, String algName)
/* 3674:     */   {
/* 3675:     */     Object tempBean;
/* 3676:4321 */     if (wekawrapper)
/* 3677:     */     {
/* 3678:     */       Object tempBean;
/* 3679:     */       try
/* 3680:     */       {
/* 3681:4325 */         tempBean = Beans.instantiate(getClass().getClassLoader(), tempBeanCompName);
/* 3682:     */       }
/* 3683:     */       catch (Exception ex)
/* 3684:     */       {
/* 3685:4330 */         Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Failed to instantiate :" + tempBeanCompName + "KnowledgeFlowApp.instantiateBean()");
/* 3686:     */         
/* 3687:     */ 
/* 3688:4333 */         return null;
/* 3689:     */       }
/* 3690:4335 */       if ((tempBean instanceof WekaWrapper))
/* 3691:     */       {
/* 3692:4337 */         Class<?> c = null;
/* 3693:     */         try
/* 3694:     */         {
/* 3695:4339 */           c = Class.forName(algName);
/* 3696:4342 */           for (Annotation a : c.getAnnotations()) {
/* 3697:4343 */             if ((a instanceof KFIgnore)) {
/* 3698:4344 */               return null;
/* 3699:     */             }
/* 3700:     */           }
/* 3701:     */         }
/* 3702:     */         catch (Exception ex)
/* 3703:     */         {
/* 3704:4348 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Can't find class called: " + algName);
/* 3705:     */           
/* 3706:     */ 
/* 3707:4351 */           return null;
/* 3708:     */         }
/* 3709:     */         try
/* 3710:     */         {
/* 3711:4354 */           Object o = c.newInstance();
/* 3712:4355 */           ((WekaWrapper)tempBean).setWrappedAlgorithm(o);
/* 3713:     */         }
/* 3714:     */         catch (Exception ex)
/* 3715:     */         {
/* 3716:4357 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Failed to configure " + tempBeanCompName + " with " + algName);
/* 3717:     */           
/* 3718:     */ 
/* 3719:4360 */           return null;
/* 3720:     */         }
/* 3721:     */       }
/* 3722:     */     }
/* 3723:     */     else
/* 3724:     */     {
/* 3725:     */       try
/* 3726:     */       {
/* 3727:4367 */         tempBean = Beans.instantiate(getClass().getClassLoader(), tempBeanCompName);
/* 3728:     */       }
/* 3729:     */       catch (Exception ex)
/* 3730:     */       {
/* 3731:4372 */         ex.printStackTrace();
/* 3732:4373 */         Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Failed to instantiate :" + tempBeanCompName + "KnowledgeFlowApp.instantiateBean()");
/* 3733:     */         
/* 3734:     */ 
/* 3735:4376 */         return null;
/* 3736:     */       }
/* 3737:     */     }
/* 3738:4379 */     return tempBean;
/* 3739:     */   }
/* 3740:     */   
/* 3741:     */   private void popupHelp()
/* 3742:     */   {
/* 3743:4386 */     final JButton tempB = this.m_helpB;
/* 3744:     */     try
/* 3745:     */     {
/* 3746:4388 */       tempB.setEnabled(false);
/* 3747:     */       
/* 3748:     */ 
/* 3749:     */ 
/* 3750:     */ 
/* 3751:4393 */       InputStream inR = getClass().getClassLoader().getResourceAsStream("weka/gui/beans/README_KnowledgeFlow");
/* 3752:     */       
/* 3753:     */ 
/* 3754:     */ 
/* 3755:4397 */       StringBuffer helpHolder = new StringBuffer();
/* 3756:4398 */       LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inR));
/* 3757:     */       String line;
/* 3758:4402 */       while ((line = lnr.readLine()) != null) {
/* 3759:4403 */         helpHolder.append(line + "\n");
/* 3760:     */       }
/* 3761:4406 */       lnr.close();
/* 3762:4407 */       final JFrame jf = new JFrame();
/* 3763:4408 */       jf.getContentPane().setLayout(new BorderLayout());
/* 3764:4409 */       JTextArea ta = new JTextArea(helpHolder.toString());
/* 3765:4410 */       ta.setFont(new Font("Monospaced", 0, 12));
/* 3766:4411 */       ta.setEditable(false);
/* 3767:4412 */       JScrollPane sp = new JScrollPane(ta);
/* 3768:4413 */       jf.getContentPane().add(sp, "Center");
/* 3769:4414 */       jf.addWindowListener(new WindowAdapter()
/* 3770:     */       {
/* 3771:     */         public void windowClosing(WindowEvent e)
/* 3772:     */         {
/* 3773:4417 */           tempB.setEnabled(true);
/* 3774:4418 */           jf.dispose();
/* 3775:     */         }
/* 3776:4420 */       });
/* 3777:4421 */       jf.setSize(600, 600);
/* 3778:4422 */       jf.setVisible(true);
/* 3779:     */     }
/* 3780:     */     catch (Exception ex)
/* 3781:     */     {
/* 3782:4425 */       tempB.setEnabled(true);
/* 3783:     */     }
/* 3784:     */   }
/* 3785:     */   
/* 3786:     */   public void closeAllTabs()
/* 3787:     */   {
/* 3788:4430 */     for (int i = this.m_mainKFPerspective.getNumTabs() - 1; i >= 0; i--) {
/* 3789:4431 */       this.m_mainKFPerspective.removeTab(i);
/* 3790:     */     }
/* 3791:     */   }
/* 3792:     */   
/* 3793:     */   public void clearLayout()
/* 3794:     */   {
/* 3795:4436 */     stopFlow();
/* 3796:4438 */     if ((this.m_mainKFPerspective.getNumTabs() == 0) || (getAllowMultipleTabs())) {
/* 3797:4439 */       this.m_mainKFPerspective.addTab("Untitled" + this.m_untitledCount++);
/* 3798:     */     }
/* 3799:4442 */     if (!getAllowMultipleTabs())
/* 3800:     */     {
/* 3801:4443 */       BeanConnection.setConnections(new Vector(), new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3802:     */       
/* 3803:4445 */       BeanInstance.setBeanInstances(new Vector(), this.m_mainKFPerspective.getBeanLayout(this.m_mainKFPerspective.getCurrentTabIndex()), new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3804:     */     }
/* 3805:     */   }
/* 3806:     */   
/* 3807:     */   private void createTemplateMenuPopup()
/* 3808:     */   {
/* 3809:4463 */     PopupMenu templatesMenu = new PopupMenu();
/* 3810:4465 */     for (int i = 0; i < BeansProperties.TEMPLATE_PATHS.size(); i++)
/* 3811:     */     {
/* 3812:4466 */       String mE = (String)BeansProperties.TEMPLATE_DESCRIPTIONS.get(i);
/* 3813:4467 */       final String path = (String)BeansProperties.TEMPLATE_PATHS.get(i);
/* 3814:     */       
/* 3815:4469 */       MenuItem m = new MenuItem(mE);
/* 3816:4470 */       m.addActionListener(new ActionListener()
/* 3817:     */       {
/* 3818:     */         public void actionPerformed(ActionEvent ee)
/* 3819:     */         {
/* 3820:     */           try
/* 3821:     */           {
/* 3822:4474 */             InputStream inR = getClass().getClassLoader().getResourceAsStream(path);
/* 3823:     */             
/* 3824:4476 */             KnowledgeFlowApp.this.m_mainKFPerspective.addTab("Untitled" + KnowledgeFlowApp.this.m_untitledCount++);
/* 3825:4477 */             XMLBeans xml = new XMLBeans(KnowledgeFlowApp.this.m_beanLayout, KnowledgeFlowApp.this.m_bcSupport, KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex());
/* 3826:     */             
/* 3827:4479 */             InputStreamReader isr = new InputStreamReader(inR);
/* 3828:     */             
/* 3829:     */ 
/* 3830:4482 */             Vector<Vector<?>> v = (Vector)xml.read(isr);
/* 3831:     */             
/* 3832:4484 */             Vector<Object> beans = (Vector)v.get(0);
/* 3833:     */             
/* 3834:     */ 
/* 3835:4487 */             Vector<BeanConnection> connections = (Vector)v.get(1);
/* 3836:     */             
/* 3837:4489 */             isr.close();
/* 3838:     */             
/* 3839:4491 */             KnowledgeFlowApp.this.integrateFlow(beans, connections, false, false);
/* 3840:4492 */             KnowledgeFlowApp.this.notifyIsDirty();
/* 3841:4493 */             KnowledgeFlowApp.this.revalidate();
/* 3842:     */           }
/* 3843:     */           catch (Exception ex)
/* 3844:     */           {
/* 3845:4495 */             KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentLogPanel().logMessage("Problem loading template: " + ex.getMessage());
/* 3846:     */           }
/* 3847:     */         }
/* 3848:4499 */       });
/* 3849:4500 */       templatesMenu.add(m);
/* 3850:     */     }
/* 3851:4503 */     this.m_templatesB.add(templatesMenu);
/* 3852:4504 */     templatesMenu.show(this.m_templatesB, 0, 0);
/* 3853:     */   }
/* 3854:     */   
/* 3855:     */   private void doPopup(Point pt, final BeanInstance bi, int x, int y)
/* 3856:     */   {
/* 3857:4520 */     final JComponent bc = (JComponent)bi.getBean();
/* 3858:4521 */     final int xx = x;
/* 3859:4522 */     final int yy = y;
/* 3860:4523 */     int menuItemCount = 0;
/* 3861:     */     
/* 3862:     */ 
/* 3863:4526 */     PopupMenu beanContextMenu = new PopupMenu();
/* 3864:     */     
/* 3865:     */ 
/* 3866:     */ 
/* 3867:     */ 
/* 3868:     */ 
/* 3869:     */ 
/* 3870:4533 */     boolean executing = this.m_mainKFPerspective.getExecuting();
/* 3871:     */     
/* 3872:4535 */     MenuItem edit = new MenuItem("Edit:");
/* 3873:4536 */     edit.setEnabled(false);
/* 3874:4537 */     beanContextMenu.insert(edit, menuItemCount);
/* 3875:4538 */     menuItemCount++;
/* 3876:4540 */     if (this.m_mainKFPerspective.getSelectedBeans().size() > 0)
/* 3877:     */     {
/* 3878:4541 */       MenuItem copyItem = new MenuItem("Copy");
/* 3879:4542 */       copyItem.addActionListener(new ActionListener()
/* 3880:     */       {
/* 3881:     */         public void actionPerformed(ActionEvent e)
/* 3882:     */         {
/* 3883:4545 */           KnowledgeFlowApp.this.copyToClipboard();
/* 3884:4546 */           KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 3885:     */         }
/* 3886:4548 */       });
/* 3887:4549 */       beanContextMenu.add(copyItem);
/* 3888:4550 */       copyItem.setEnabled(!executing);
/* 3889:4551 */       menuItemCount++;
/* 3890:     */     }
/* 3891:4554 */     if ((bc instanceof MetaBean))
/* 3892:     */     {
/* 3893:4556 */       MenuItem ungroupItem = new MenuItem("Ungroup");
/* 3894:4557 */       ungroupItem.addActionListener(new ActionListener()
/* 3895:     */       {
/* 3896:     */         public void actionPerformed(ActionEvent e)
/* 3897:     */         {
/* 3898:4561 */           bi.removeBean(KnowledgeFlowApp.this.m_beanLayout, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3899:     */           
/* 3900:4563 */           Vector<Object> group = ((MetaBean)bc).getBeansInSubFlow();
/* 3901:4564 */           Vector<BeanConnection> associatedConnections = ((MetaBean)bc).getAssociatedConnections();
/* 3902:     */           
/* 3903:4566 */           ((MetaBean)bc).restoreBeans(xx, yy);
/* 3904:4568 */           for (int i = 0; i < group.size(); i++)
/* 3905:     */           {
/* 3906:4569 */             BeanInstance tbi = (BeanInstance)group.elementAt(i);
/* 3907:4570 */             KnowledgeFlowApp.this.addComponent(tbi, false);
/* 3908:4571 */             tbi.addBean(KnowledgeFlowApp.this.m_beanLayout, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3909:     */           }
/* 3910:4574 */           for (int i = 0; i < associatedConnections.size(); i++)
/* 3911:     */           {
/* 3912:4575 */             BeanConnection tbc = (BeanConnection)associatedConnections.elementAt(i);
/* 3913:4576 */             tbc.setHidden(false);
/* 3914:     */           }
/* 3915:4579 */           KnowledgeFlowApp.this.m_beanLayout.repaint();
/* 3916:4580 */           KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3917:4581 */           KnowledgeFlowApp.this.notifyIsDirty();
/* 3918:     */         }
/* 3919:4583 */       });
/* 3920:4584 */       ungroupItem.setEnabled(!executing);
/* 3921:     */       
/* 3922:4586 */       beanContextMenu.add(ungroupItem);
/* 3923:4587 */       menuItemCount++;
/* 3924:     */       
/* 3925:     */ 
/* 3926:     */ 
/* 3927:4591 */       MenuItem addToUserTabItem = new MenuItem("Add to user tab");
/* 3928:4592 */       addToUserTabItem.addActionListener(new ActionListener()
/* 3929:     */       {
/* 3930:     */         public void actionPerformed(ActionEvent e)
/* 3931:     */         {
/* 3932:4597 */           KnowledgeFlowApp.this.addToUserTreeNode(bi, true);
/* 3933:4598 */           KnowledgeFlowApp.this.notifyIsDirty();
/* 3934:     */         }
/* 3935:4600 */       });
/* 3936:4601 */       addToUserTabItem.setEnabled(!executing);
/* 3937:4602 */       beanContextMenu.add(addToUserTabItem);
/* 3938:4603 */       menuItemCount++;
/* 3939:     */     }
/* 3940:4607 */     MenuItem deleteItem = new MenuItem("Delete");
/* 3941:4608 */     deleteItem.addActionListener(new ActionListener()
/* 3942:     */     {
/* 3943:     */       public void actionPerformed(ActionEvent e)
/* 3944:     */       {
/* 3945:4611 */         BeanConnection.removeConnections(bi, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3946:     */         
/* 3947:4613 */         bi.removeBean(KnowledgeFlowApp.this.m_beanLayout, new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 3948:4614 */         if ((bc instanceof BeanCommon))
/* 3949:     */         {
/* 3950:4615 */           String key = ((BeanCommon)bc).getCustomName() + "$" + bc.hashCode();
/* 3951:4616 */           KnowledgeFlowApp.this.m_logPanel.statusMessage(key + "|remove");
/* 3952:     */         }
/* 3953:4620 */         if (KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans().size() > 0) {
/* 3954:4621 */           KnowledgeFlowApp.this.deleteSelectedBeans();
/* 3955:     */         }
/* 3956:4624 */         KnowledgeFlowApp.this.revalidate();
/* 3957:4625 */         KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3958:4626 */         KnowledgeFlowApp.this.notifyIsDirty();
/* 3959:4627 */         KnowledgeFlowApp.this.m_selectAllB.setEnabled(BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) }).size() > 0);
/* 3960:     */       }
/* 3961:4631 */     });
/* 3962:4632 */     deleteItem.setEnabled(!executing);
/* 3963:4633 */     if (((bc instanceof BeanCommon)) && 
/* 3964:4634 */       (((BeanCommon)bc).isBusy())) {
/* 3965:4635 */       deleteItem.setEnabled(false);
/* 3966:     */     }
/* 3967:4639 */     beanContextMenu.add(deleteItem);
/* 3968:4640 */     menuItemCount++;
/* 3969:4642 */     if ((bc instanceof BeanCommon))
/* 3970:     */     {
/* 3971:4643 */       MenuItem nameItem = new MenuItem("Set name");
/* 3972:4644 */       nameItem.addActionListener(new ActionListener()
/* 3973:     */       {
/* 3974:     */         public void actionPerformed(ActionEvent e)
/* 3975:     */         {
/* 3976:4647 */           String oldName = ((BeanCommon)bc).getCustomName();
/* 3977:4648 */           String name = JOptionPane.showInputDialog(KnowledgeFlowApp.this, "Enter a name for this component", oldName);
/* 3978:4650 */           if (name != null)
/* 3979:     */           {
/* 3980:4651 */             ((BeanCommon)bc).setCustomName(name);
/* 3981:4652 */             KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 3982:     */           }
/* 3983:     */         }
/* 3984:     */       });
/* 3985:4656 */       if (((bc instanceof BeanCommon)) && 
/* 3986:4657 */         (((BeanCommon)bc).isBusy())) {
/* 3987:4658 */         nameItem.setEnabled(false);
/* 3988:     */       }
/* 3989:4661 */       nameItem.setEnabled(!executing);
/* 3990:4662 */       beanContextMenu.add(nameItem);
/* 3991:4663 */       menuItemCount++;
/* 3992:     */     }
/* 3993:     */     try
/* 3994:     */     {
/* 3995:4669 */       Vector<BeanInfo> compInfo = new Vector(1);
/* 3996:4670 */       Vector<Object> associatedBeans = null;
/* 3997:4671 */       if ((bc instanceof MetaBean))
/* 3998:     */       {
/* 3999:4672 */         compInfo = ((MetaBean)bc).getBeanInfoSubFlow();
/* 4000:4673 */         associatedBeans = ((MetaBean)bc).getBeansInSubFlow();
/* 4001:     */         
/* 4002:4675 */         ((MetaBean)bc).getBeansInOutputs();
/* 4003:4676 */         ((MetaBean)bc).getBeanInfoOutputs();
/* 4004:     */       }
/* 4005:     */       else
/* 4006:     */       {
/* 4007:4678 */         compInfo.add(Introspector.getBeanInfo(bc.getClass()));
/* 4008:     */       }
/* 4009:4681 */       final Vector<Object> tempAssociatedBeans = associatedBeans;
/* 4010:4683 */       if (compInfo == null)
/* 4011:     */       {
/* 4012:4684 */         Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Error in doPopup()");
/* 4013:     */       }
/* 4014:     */       else
/* 4015:     */       {
/* 4016:4688 */         for (int zz = 0; zz < compInfo.size(); zz++)
/* 4017:     */         {
/* 4018:4689 */           final int tt = zz;
/* 4019:4690 */           final Class<?> custClass = ((BeanInfo)compInfo.elementAt(zz)).getBeanDescriptor().getCustomizerClass();
/* 4020:4693 */           if (custClass != null)
/* 4021:     */           {
/* 4022:4697 */             MenuItem custItem = null;
/* 4023:4698 */             boolean customizationEnabled = !executing;
/* 4024:4700 */             if (!(bc instanceof MetaBean))
/* 4025:     */             {
/* 4026:4702 */               custItem = new MenuItem("Configure...");
/* 4027:4703 */               if ((bc instanceof BeanCommon)) {
/* 4028:4704 */                 customizationEnabled = (!executing) && (!((BeanCommon)bc).isBusy());
/* 4029:     */               }
/* 4030:     */             }
/* 4031:     */             else
/* 4032:     */             {
/* 4033:4708 */               String custName = custClass.getName();
/* 4034:4709 */               BeanInstance tbi = (BeanInstance)associatedBeans.elementAt(zz);
/* 4035:4710 */               if ((tbi.getBean() instanceof BeanCommon))
/* 4036:     */               {
/* 4037:4711 */                 custName = ((BeanCommon)tbi.getBean()).getCustomName();
/* 4038:     */               }
/* 4039:     */               else
/* 4040:     */               {
/* 4041:4713 */                 if ((tbi.getBean() instanceof WekaWrapper)) {
/* 4042:4714 */                   custName = ((WekaWrapper)tbi.getBean()).getWrappedAlgorithm().getClass().getName();
/* 4043:     */                 } else {
/* 4044:4717 */                   custName = custName.substring(0, custName.indexOf("Customizer"));
/* 4045:     */                 }
/* 4046:4721 */                 custName = custName.substring(custName.lastIndexOf('.') + 1, custName.length());
/* 4047:     */               }
/* 4048:4725 */               custItem = new MenuItem("Configure: " + custName);
/* 4049:4726 */               if ((tbi.getBean() instanceof BeanCommon)) {
/* 4050:4727 */                 customizationEnabled = (!executing) && (!((BeanCommon)tbi.getBean()).isBusy());
/* 4051:     */               }
/* 4052:     */             }
/* 4053:4732 */             custItem.addActionListener(new ActionListener()
/* 4054:     */             {
/* 4055:     */               public void actionPerformed(ActionEvent e)
/* 4056:     */               {
/* 4057:4735 */                 if ((bc instanceof MetaBean)) {
/* 4058:4736 */                   KnowledgeFlowApp.this.popupCustomizer(custClass, (JComponent)((BeanInstance)tempAssociatedBeans.elementAt(tt)).getBean());
/* 4059:     */                 } else {
/* 4060:4740 */                   KnowledgeFlowApp.this.popupCustomizer(custClass, bc);
/* 4061:     */                 }
/* 4062:4743 */                 KnowledgeFlowApp.this.notifyIsDirty();
/* 4063:     */               }
/* 4064:4745 */             });
/* 4065:4746 */             custItem.setEnabled(customizationEnabled);
/* 4066:4747 */             beanContextMenu.add(custItem);
/* 4067:4748 */             menuItemCount++;
/* 4068:     */           }
/* 4069:     */           else
/* 4070:     */           {
/* 4071:4750 */             Logger.log(Logger.Level.INFO, "[KnowledgeFlow] No customizer class");
/* 4072:     */           }
/* 4073:     */         }
/* 4074:4755 */         Vector<EventSetDescriptor[]> esdV = new Vector();
/* 4075:4758 */         for (int i = 0; i < compInfo.size(); i++)
/* 4076:     */         {
/* 4077:4759 */           EventSetDescriptor[] temp = ((BeanInfo)compInfo.elementAt(i)).getEventSetDescriptors();
/* 4078:4764 */           if ((temp != null) && (temp.length > 0)) {
/* 4079:4765 */             esdV.add(temp);
/* 4080:     */           }
/* 4081:     */         }
/* 4082:4771 */         if (esdV.size() > 0)
/* 4083:     */         {
/* 4084:4775 */           MenuItem connections = new MenuItem("Connections:");
/* 4085:4776 */           connections.setEnabled(false);
/* 4086:4777 */           beanContextMenu.insert(connections, menuItemCount);
/* 4087:4778 */           menuItemCount++;
/* 4088:     */         }
/* 4089:4782 */         final Vector<Object> finalOutputs = associatedBeans;
/* 4090:4784 */         for (int j = 0; j < esdV.size(); j++)
/* 4091:     */         {
/* 4092:4785 */           final int fj = j;
/* 4093:4786 */           String sourceBeanName = "";
/* 4094:4788 */           if ((bc instanceof MetaBean))
/* 4095:     */           {
/* 4096:4791 */             Object sourceBean = ((BeanInstance)associatedBeans.elementAt(j)).getBean();
/* 4097:4793 */             if ((sourceBean instanceof BeanCommon))
/* 4098:     */             {
/* 4099:4794 */               sourceBeanName = ((BeanCommon)sourceBean).getCustomName();
/* 4100:     */             }
/* 4101:     */             else
/* 4102:     */             {
/* 4103:4796 */               if ((sourceBean instanceof WekaWrapper)) {
/* 4104:4797 */                 sourceBeanName = ((WekaWrapper)sourceBean).getWrappedAlgorithm().getClass().getName();
/* 4105:     */               } else {
/* 4106:4800 */                 sourceBeanName = sourceBean.getClass().getName();
/* 4107:     */               }
/* 4108:4803 */               sourceBeanName = sourceBeanName.substring(sourceBeanName.lastIndexOf('.') + 1, sourceBeanName.length());
/* 4109:     */             }
/* 4110:4806 */             sourceBeanName = sourceBeanName + ": ";
/* 4111:     */           }
/* 4112:4809 */           EventSetDescriptor[] esds = (EventSetDescriptor[])esdV.elementAt(j);
/* 4113:4811 */           for (final EventSetDescriptor esd : esds)
/* 4114:     */           {
/* 4115:4816 */             MenuItem evntItem = new MenuItem(sourceBeanName + esd.getName());
/* 4116:     */             
/* 4117:4818 */             boolean ok = true;
/* 4118:4819 */             evntItem.setEnabled(!executing);
/* 4119:4821 */             if ((bc instanceof EventConstraints)) {
/* 4120:4822 */               ok = ((EventConstraints)bc).eventGeneratable(esd.getName());
/* 4121:     */             }
/* 4122:4825 */             if (ok) {
/* 4123:4826 */               evntItem.addActionListener(new ActionListener()
/* 4124:     */               {
/* 4125:     */                 public void actionPerformed(ActionEvent e)
/* 4126:     */                 {
/* 4127:4829 */                   KnowledgeFlowApp.this.connectComponents(esd, (bc instanceof MetaBean) ? (BeanInstance)finalOutputs.elementAt(fj) : bi, xx, yy);
/* 4128:     */                   
/* 4129:     */ 
/* 4130:     */ 
/* 4131:4833 */                   KnowledgeFlowApp.this.notifyIsDirty();
/* 4132:     */                 }
/* 4133:     */               });
/* 4134:     */             } else {
/* 4135:4837 */               evntItem.setEnabled(false);
/* 4136:     */             }
/* 4137:4840 */             beanContextMenu.add(evntItem);
/* 4138:4841 */             menuItemCount++;
/* 4139:     */           }
/* 4140:     */         }
/* 4141:     */       }
/* 4142:     */     }
/* 4143:     */     catch (IntrospectionException ie)
/* 4144:     */     {
/* 4145:4846 */       ie.printStackTrace();
/* 4146:     */     }
/* 4147:4851 */     if (((bc instanceof UserRequestAcceptor)) || ((bc instanceof Startable)))
/* 4148:     */     {
/* 4149:4852 */       Enumeration<String> req = null;
/* 4150:4854 */       if ((bc instanceof UserRequestAcceptor)) {
/* 4151:4855 */         req = ((UserRequestAcceptor)bc).enumerateRequests();
/* 4152:     */       }
/* 4153:4858 */       if ((req != null) && (req.hasMoreElements()))
/* 4154:     */       {
/* 4155:4863 */         MenuItem actions = new MenuItem("Actions:");
/* 4156:4864 */         actions.setEnabled(false);
/* 4157:4865 */         beanContextMenu.insert(actions, menuItemCount);
/* 4158:4866 */         menuItemCount++;
/* 4159:     */       }
/* 4160:4875 */       while ((req != null) && (req.hasMoreElements()))
/* 4161:     */       {
/* 4162:4876 */         String tempS = (String)req.nextElement();
/* 4163:4877 */         insertUserOrStartableMenuItem(bc, false, tempS, beanContextMenu);
/* 4164:4878 */         menuItemCount++;
/* 4165:     */       }
/* 4166:     */     }
/* 4167:4883 */     if (((bc instanceof Loader)) && (this.m_perspectives.size() > 1) && (this.m_perspectiveDataLoadThread == null))
/* 4168:     */     {
/* 4169:4885 */       final weka.core.converters.Loader theLoader = ((Loader)bc).getLoader();
/* 4170:     */       
/* 4171:     */ 
/* 4172:     */ 
/* 4173:4889 */       boolean ok = true;
/* 4174:4890 */       if ((theLoader instanceof FileSourcedConverter))
/* 4175:     */       {
/* 4176:4891 */         String fileName = ((FileSourcedConverter)theLoader).retrieveFile().getPath();
/* 4177:     */         
/* 4178:4893 */         Environment env = this.m_mainKFPerspective.getEnvironmentSettings();
/* 4179:     */         try
/* 4180:     */         {
/* 4181:4895 */           fileName = env.substitute(fileName);
/* 4182:     */         }
/* 4183:     */         catch (Exception ex) {}
/* 4184:4899 */         File tempF = new File(fileName);
/* 4185:4900 */         String fileNameFixedPathSep = fileName.replace(File.separatorChar, '/');
/* 4186:4901 */         if ((!tempF.isFile()) && (getClass().getClassLoader().getResource(fileNameFixedPathSep) == null)) {
/* 4187:4903 */           ok = false;
/* 4188:     */         }
/* 4189:     */       }
/* 4190:4907 */       if (ok)
/* 4191:     */       {
/* 4192:4908 */         beanContextMenu.addSeparator();
/* 4193:4909 */         menuItemCount++;
/* 4194:4910 */         if (this.m_perspectives.size() > 2)
/* 4195:     */         {
/* 4196:4911 */           MenuItem sendToAllPerspectives = new MenuItem("Send to all perspectives");
/* 4197:     */           
/* 4198:4913 */           menuItemCount++;
/* 4199:4914 */           sendToAllPerspectives.addActionListener(new ActionListener()
/* 4200:     */           {
/* 4201:     */             public void actionPerformed(ActionEvent e)
/* 4202:     */             {
/* 4203:4917 */               KnowledgeFlowApp.this.loadDataAndSendToPerspective(theLoader, 0, true);
/* 4204:     */             }
/* 4205:4919 */           });
/* 4206:4920 */           beanContextMenu.add(sendToAllPerspectives);
/* 4207:     */         }
/* 4208:4922 */         Menu sendToPerspective = new Menu("Send to perspective...");
/* 4209:4923 */         beanContextMenu.add(sendToPerspective);
/* 4210:4924 */         menuItemCount++;
/* 4211:4925 */         for (int i = 1; i < this.m_perspectives.size(); i++)
/* 4212:     */         {
/* 4213:4926 */           final int pIndex = i;
/* 4214:4928 */           if (((KFPerspective)this.m_perspectives.get(i)).acceptsInstances())
/* 4215:     */           {
/* 4216:4929 */             String pName = ((KFPerspective)this.m_perspectives.get(i)).getPerspectiveTitle();
/* 4217:4930 */             MenuItem pI = new MenuItem(pName);
/* 4218:4931 */             pI.addActionListener(new ActionListener()
/* 4219:     */             {
/* 4220:     */               public void actionPerformed(ActionEvent e)
/* 4221:     */               {
/* 4222:4934 */                 KnowledgeFlowApp.this.loadDataAndSendToPerspective(theLoader, pIndex, false);
/* 4223:     */               }
/* 4224:4936 */             });
/* 4225:4937 */             sendToPerspective.add(pI);
/* 4226:     */           }
/* 4227:     */         }
/* 4228:     */       }
/* 4229:     */     }
/* 4230:4945 */     if (menuItemCount > 0)
/* 4231:     */     {
/* 4232:4948 */       double z = this.m_layoutZoom / 100.0D;
/* 4233:4949 */       double px = x * z;
/* 4234:4950 */       double py = y * z;
/* 4235:4951 */       this.m_beanLayout.add(beanContextMenu);
/* 4236:     */       
/* 4237:4953 */       beanContextMenu.show(this.m_beanLayout, (int)px, (int)py);
/* 4238:     */     }
/* 4239:     */   }
/* 4240:     */   
/* 4241:     */   private synchronized void loadDataAndSendToPerspective(final weka.core.converters.Loader loader, final int perspectiveIndex, final boolean sendToAll)
/* 4242:     */   {
/* 4243:4960 */     if (this.m_perspectiveDataLoadThread == null)
/* 4244:     */     {
/* 4245:4961 */       this.m_perspectiveDataLoadThread = new Thread()
/* 4246:     */       {
/* 4247:     */         public void run()
/* 4248:     */         {
/* 4249:     */           try
/* 4250:     */           {
/* 4251:4965 */             Environment env = KnowledgeFlowApp.this.m_mainKFPerspective.getEnvironmentSettings();
/* 4252:4966 */             if ((loader instanceof EnvironmentHandler)) {
/* 4253:4967 */               ((EnvironmentHandler)loader).setEnvironment(env);
/* 4254:     */             }
/* 4255:4970 */             loader.reset();
/* 4256:4971 */             KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Sending data to perspective(s)...");
/* 4257:     */             
/* 4258:4973 */             Instances data = loader.getDataSet();
/* 4259:4974 */             if (data != null)
/* 4260:     */             {
/* 4261:4976 */               if (!KnowledgeFlowApp.this.m_configAndPerspectivesVisible)
/* 4262:     */               {
/* 4263:4977 */                 KnowledgeFlowApp.this.add(KnowledgeFlowApp.this.m_configAndPerspectives, "North");
/* 4264:     */                 
/* 4265:4979 */                 KnowledgeFlowApp.this.m_configAndPerspectivesVisible = true;
/* 4266:     */               }
/* 4267:4983 */               for (int i = 0; i < KnowledgeFlowApp.this.m_perspectives.size(); i++) {
/* 4268:4984 */                 KnowledgeFlowApp.this.m_perspectiveToolBar.getComponent(i).setEnabled(false);
/* 4269:     */               }
/* 4270:4987 */               if (sendToAll)
/* 4271:     */               {
/* 4272:4988 */                 for (int i = 1; i < KnowledgeFlowApp.this.m_perspectives.size(); i++) {
/* 4273:4989 */                   if (((KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectives.get(i)).acceptsInstances()) {
/* 4274:4990 */                     ((KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectives.get(i)).setInstances(data);
/* 4275:     */                   }
/* 4276:     */                 }
/* 4277:     */               }
/* 4278:     */               else
/* 4279:     */               {
/* 4280:4994 */                 KnowledgeFlowApp.KFPerspective currentP = (KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectiveHolder.getComponent(0);
/* 4281:4996 */                 if (currentP != KnowledgeFlowApp.this.m_perspectives.get(perspectiveIndex))
/* 4282:     */                 {
/* 4283:4997 */                   ((KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectives.get(perspectiveIndex)).setInstances(data);
/* 4284:4998 */                   currentP.setActive(false);
/* 4285:4999 */                   KnowledgeFlowApp.this.m_perspectiveHolder.remove(0);
/* 4286:5000 */                   KnowledgeFlowApp.this.m_perspectiveHolder.add((JComponent)KnowledgeFlowApp.this.m_perspectives.get(perspectiveIndex), "Center");
/* 4287:     */                   
/* 4288:     */ 
/* 4289:5003 */                   ((KnowledgeFlowApp.KFPerspective)KnowledgeFlowApp.this.m_perspectives.get(perspectiveIndex)).setActive(true);
/* 4290:5004 */                   ((JToggleButton)KnowledgeFlowApp.this.m_perspectiveToolBar.getComponent(perspectiveIndex)).setSelected(true);
/* 4291:     */                   
/* 4292:     */ 
/* 4293:5007 */                   KnowledgeFlowApp.this.revalidate();
/* 4294:5008 */                   KnowledgeFlowApp.this.repaint();
/* 4295:5009 */                   KnowledgeFlowApp.this.notifyIsDirty();
/* 4296:     */                 }
/* 4297:     */               }
/* 4298:     */             }
/* 4299:5021 */             for (int i = 0; i < KnowledgeFlowApp.this.m_perspectives.size(); i++) {
/* 4300:5022 */               KnowledgeFlowApp.this.m_perspectiveToolBar.getComponent(i).setEnabled(true);
/* 4301:     */             }
/* 4302:5024 */             KnowledgeFlowApp.this.m_perspectiveDataLoadThread = null;
/* 4303:5025 */             KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|OK");
/* 4304:     */           }
/* 4305:     */           catch (Exception ex)
/* 4306:     */           {
/* 4307:5014 */             Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] problem loading data for perspective(s) : " + ex.getMessage());
/* 4308:     */             
/* 4309:     */ 
/* 4310:     */ 
/* 4311:5018 */             ex.printStackTrace();
/* 4312:5021 */             for (int i = 0; i < KnowledgeFlowApp.this.m_perspectives.size(); i++) {
/* 4313:5022 */               KnowledgeFlowApp.this.m_perspectiveToolBar.getComponent(i).setEnabled(true);
/* 4314:     */             }
/* 4315:5024 */             KnowledgeFlowApp.this.m_perspectiveDataLoadThread = null;
/* 4316:5025 */             KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|OK");
/* 4317:     */           }
/* 4318:     */           finally
/* 4319:     */           {
/* 4320:5021 */             for (int i = 0; i < KnowledgeFlowApp.this.m_perspectives.size(); i++) {
/* 4321:5022 */               KnowledgeFlowApp.this.m_perspectiveToolBar.getComponent(i).setEnabled(true);
/* 4322:     */             }
/* 4323:5024 */             KnowledgeFlowApp.this.m_perspectiveDataLoadThread = null;
/* 4324:5025 */             KnowledgeFlowApp.this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|OK");
/* 4325:     */           }
/* 4326:     */         }
/* 4327:5028 */       };
/* 4328:5029 */       this.m_perspectiveDataLoadThread.setPriority(1);
/* 4329:5030 */       this.m_perspectiveDataLoadThread.start();
/* 4330:     */     }
/* 4331:     */   }
/* 4332:     */   
/* 4333:     */   private void insertUserOrStartableMenuItem(final JComponent bc, final boolean startable, String tempS, PopupMenu beanContextMenu)
/* 4334:     */   {
/* 4335:5037 */     boolean disabled = false;
/* 4336:5038 */     boolean confirmRequest = false;
/* 4337:5041 */     if (tempS.charAt(0) == '$')
/* 4338:     */     {
/* 4339:5042 */       tempS = tempS.substring(1, tempS.length());
/* 4340:5043 */       disabled = true;
/* 4341:     */     }
/* 4342:5047 */     if (tempS.charAt(0) == '?')
/* 4343:     */     {
/* 4344:5048 */       tempS = tempS.substring(1, tempS.length());
/* 4345:5049 */       confirmRequest = true;
/* 4346:     */     }
/* 4347:5052 */     final String tempS2 = tempS;
/* 4348:     */     
/* 4349:     */ 
/* 4350:5055 */     MenuItem custItem = new MenuItem(tempS2);
/* 4351:5056 */     if (confirmRequest) {
/* 4352:5057 */       custItem.addActionListener(new ActionListener()
/* 4353:     */       {
/* 4354:     */         public void actionPerformed(ActionEvent e)
/* 4355:     */         {
/* 4356:5061 */           int result = JOptionPane.showConfirmDialog(KnowledgeFlowApp.this, tempS2, "Confirm action", 0);
/* 4357:5063 */           if (result == 0)
/* 4358:     */           {
/* 4359:5064 */             Thread startPointThread = new Thread()
/* 4360:     */             {
/* 4361:     */               public void run()
/* 4362:     */               {
/* 4363:     */                 try
/* 4364:     */                 {
/* 4365:5068 */                   if (KnowledgeFlowApp.21.this.val$startable) {
/* 4366:5069 */                     ((Startable)KnowledgeFlowApp.21.this.val$bc).start();
/* 4367:5070 */                   } else if ((KnowledgeFlowApp.21.this.val$bc instanceof UserRequestAcceptor)) {
/* 4368:5071 */                     ((UserRequestAcceptor)KnowledgeFlowApp.21.this.val$bc).performRequest(KnowledgeFlowApp.21.this.val$tempS2);
/* 4369:     */                   }
/* 4370:5073 */                   KnowledgeFlowApp.this.notifyIsDirty();
/* 4371:     */                 }
/* 4372:     */                 catch (Exception ex)
/* 4373:     */                 {
/* 4374:5075 */                   ex.printStackTrace();
/* 4375:     */                 }
/* 4376:     */               }
/* 4377:5078 */             };
/* 4378:5079 */             startPointThread.setPriority(1);
/* 4379:5080 */             startPointThread.start();
/* 4380:     */           }
/* 4381:     */         }
/* 4382:     */       });
/* 4383:     */     } else {
/* 4384:5085 */       custItem.addActionListener(new ActionListener()
/* 4385:     */       {
/* 4386:     */         public void actionPerformed(ActionEvent e)
/* 4387:     */         {
/* 4388:5088 */           Thread startPointThread = new Thread()
/* 4389:     */           {
/* 4390:     */             public void run()
/* 4391:     */             {
/* 4392:     */               try
/* 4393:     */               {
/* 4394:5092 */                 if (KnowledgeFlowApp.22.this.val$startable) {
/* 4395:5093 */                   ((Startable)KnowledgeFlowApp.22.this.val$bc).start();
/* 4396:5094 */                 } else if ((KnowledgeFlowApp.22.this.val$bc instanceof UserRequestAcceptor)) {
/* 4397:5095 */                   ((UserRequestAcceptor)KnowledgeFlowApp.22.this.val$bc).performRequest(KnowledgeFlowApp.22.this.val$tempS2);
/* 4398:     */                 }
/* 4399:5097 */                 KnowledgeFlowApp.this.notifyIsDirty();
/* 4400:     */               }
/* 4401:     */               catch (Exception ex)
/* 4402:     */               {
/* 4403:5099 */                 ex.printStackTrace();
/* 4404:     */               }
/* 4405:     */             }
/* 4406:5102 */           };
/* 4407:5103 */           startPointThread.setPriority(1);
/* 4408:5104 */           startPointThread.start();
/* 4409:     */         }
/* 4410:     */       });
/* 4411:     */     }
/* 4412:5109 */     if (disabled) {
/* 4413:5110 */       custItem.setEnabled(false);
/* 4414:     */     }
/* 4415:5113 */     beanContextMenu.add(custItem);
/* 4416:     */   }
/* 4417:     */   
/* 4418:     */   public void setModifiedStatus(Object source, boolean modified)
/* 4419:     */   {
/* 4420:5123 */     if (((source instanceof BeanCustomizer)) && (modified)) {
/* 4421:5124 */       this.m_mainKFPerspective.setEditedStatus(modified);
/* 4422:     */     }
/* 4423:     */   }
/* 4424:     */   
/* 4425:     */   private void popupCustomizer(Class<?> custClass, JComponent bc)
/* 4426:     */   {
/* 4427:     */     try
/* 4428:     */     {
/* 4429:5137 */       final Object customizer = custClass.newInstance();
/* 4430:5139 */       if ((customizer instanceof EnvironmentHandler)) {
/* 4431:5140 */         ((EnvironmentHandler)customizer).setEnvironment(this.m_flowEnvironment);
/* 4432:     */       }
/* 4433:5143 */       if ((customizer instanceof BeanCustomizer)) {
/* 4434:5144 */         ((BeanCustomizer)customizer).setModifiedListener(this);
/* 4435:     */       }
/* 4436:5147 */       ((Customizer)customizer).setObject(bc);
/* 4437:     */       
/* 4438:5149 */       final JDialog d = new JDialog((Frame)getTopLevelAncestor(), Dialog.ModalityType.DOCUMENT_MODAL);
/* 4439:     */       
/* 4440:     */ 
/* 4441:5152 */       d.setLayout(new BorderLayout());
/* 4442:5153 */       d.getContentPane().add((JComponent)customizer, "Center");
/* 4443:5157 */       if ((customizer instanceof CustomizerCloseRequester)) {
/* 4444:5158 */         ((CustomizerCloseRequester)customizer).setParentWindow(d);
/* 4445:     */       }
/* 4446:5160 */       d.addWindowListener(new WindowAdapter()
/* 4447:     */       {
/* 4448:     */         public void windowClosing(WindowEvent e)
/* 4449:     */         {
/* 4450:5163 */           if ((customizer instanceof CustomizerClosingListener)) {
/* 4451:5164 */             ((CustomizerClosingListener)customizer).customizerClosing();
/* 4452:     */           }
/* 4453:5166 */           d.dispose();
/* 4454:     */         }
/* 4455:5170 */       });
/* 4456:5171 */       d.pack();
/* 4457:5172 */       d.setLocationRelativeTo(this);
/* 4458:5173 */       d.setVisible(true);
/* 4459:     */     }
/* 4460:     */     catch (Exception ex)
/* 4461:     */     {
/* 4462:5175 */       ex.printStackTrace();
/* 4463:     */     }
/* 4464:     */   }
/* 4465:     */   
/* 4466:     */   private void addToUserTreeNode(BeanInstance meta, boolean installListener)
/* 4467:     */   {
/* 4468:5180 */     DefaultTreeModel model = (DefaultTreeModel)this.m_componentTree.getModel();
/* 4469:5181 */     DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
/* 4470:5182 */     if (this.m_userCompNode == null)
/* 4471:     */     {
/* 4472:5183 */       this.m_userCompNode = new InvisibleNode("User");
/* 4473:5184 */       model.insertNodeInto(this.m_userCompNode, root, 0);
/* 4474:     */     }
/* 4475:5187 */     Vector<Object> beanHolder = new Vector();
/* 4476:5188 */     beanHolder.add(meta);
/* 4477:     */     try
/* 4478:     */     {
/* 4479:5191 */       StringBuffer serialized = copyToBuffer(beanHolder);
/* 4480:     */       
/* 4481:5193 */       String displayName = "";
/* 4482:5194 */       ImageIcon scaledIcon = null;
/* 4483:5196 */       if ((meta.getBean() instanceof Visible))
/* 4484:     */       {
/* 4485:5198 */         scaledIcon = new ImageIcon(((Visible)meta.getBean()).getVisual().scale(0.33D));
/* 4486:     */         
/* 4487:5200 */         displayName = ((Visible)meta.getBean()).getVisual().getText();
/* 4488:     */       }
/* 4489:5203 */       Vector<Object> metaDetails = new Vector();
/* 4490:5204 */       metaDetails.add(displayName);
/* 4491:5205 */       metaDetails.add(serialized);
/* 4492:5206 */       metaDetails.add(scaledIcon);
/* 4493:5207 */       SerializedObject so = new SerializedObject(metaDetails);
/* 4494:     */       
/* 4495:5209 */       Vector<Object> copy = (Vector)so.getObject();
/* 4496:     */       
/* 4497:5211 */       JTreeLeafDetails metaLeaf = new JTreeLeafDetails(displayName, copy, scaledIcon);
/* 4498:     */       
/* 4499:     */ 
/* 4500:5214 */       DefaultMutableTreeNode newUserComp = new InvisibleNode(metaLeaf);
/* 4501:5215 */       model.insertNodeInto(newUserComp, this.m_userCompNode, 0);
/* 4502:     */       
/* 4503:     */ 
/* 4504:5218 */       this.m_userComponents.add(copy);
/* 4505:5220 */       if ((installListener) && (this.m_firstUserComponentOpp)) {
/* 4506:     */         try
/* 4507:     */         {
/* 4508:5222 */           installWindowListenerForSavingUserStuff();
/* 4509:5223 */           this.m_firstUserComponentOpp = false;
/* 4510:     */         }
/* 4511:     */         catch (Exception ex)
/* 4512:     */         {
/* 4513:5225 */           ex.printStackTrace();
/* 4514:     */         }
/* 4515:     */       }
/* 4516:     */     }
/* 4517:     */     catch (Exception ex)
/* 4518:     */     {
/* 4519:5229 */       ex.printStackTrace();
/* 4520:     */     }
/* 4521:     */   }
/* 4522:     */   
/* 4523:     */   public void setPasteBuffer(StringBuffer b)
/* 4524:     */   {
/* 4525:5316 */     this.m_pasteBuffer = b;
/* 4526:5318 */     if ((this.m_pasteBuffer != null) && (this.m_pasteBuffer.length() > 0)) {
/* 4527:5319 */       this.m_pasteB.setEnabled(true);
/* 4528:     */     }
/* 4529:     */   }
/* 4530:     */   
/* 4531:     */   public StringBuffer getPasteBuffer()
/* 4532:     */   {
/* 4533:5329 */     return this.m_pasteBuffer;
/* 4534:     */   }
/* 4535:     */   
/* 4536:     */   public StringBuffer copyToBuffer(Vector<Object> selectedBeans)
/* 4537:     */     throws Exception
/* 4538:     */   {
/* 4539:5342 */     Vector<BeanConnection> associatedConnections = BeanConnection.getConnections(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4540:     */     
/* 4541:     */ 
/* 4542:     */ 
/* 4543:     */ 
/* 4544:     */ 
/* 4545:     */ 
/* 4546:     */ 
/* 4547:     */ 
/* 4548:5351 */     Vector<Vector<?>> v = new Vector();
/* 4549:5352 */     v.setSize(2);
/* 4550:5353 */     v.set(0, selectedBeans);
/* 4551:5354 */     v.set(1, associatedConnections);
/* 4552:     */     
/* 4553:5356 */     XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, this.m_mainKFPerspective.getCurrentTabIndex());
/* 4554:     */     
/* 4555:5358 */     StringWriter sw = new StringWriter();
/* 4556:5359 */     xml.write(sw, v);
/* 4557:     */     
/* 4558:5361 */     return sw.getBuffer();
/* 4559:     */   }
/* 4560:     */   
/* 4561:     */   private boolean copyToClipboard()
/* 4562:     */   {
/* 4563:5367 */     Vector<Object> selectedBeans = this.m_mainKFPerspective.getSelectedBeans();
/* 4564:5368 */     if ((selectedBeans == null) || (selectedBeans.size() == 0)) {
/* 4565:5369 */       return false;
/* 4566:     */     }
/* 4567:     */     try
/* 4568:     */     {
/* 4569:5374 */       this.m_pasteBuffer = copyToBuffer(selectedBeans);
/* 4570:     */     }
/* 4571:     */     catch (Exception ex)
/* 4572:     */     {
/* 4573:5376 */       this.m_logPanel.logMessage("[KnowledgeFlow] problem copying beans: " + ex.getMessage());
/* 4574:     */       
/* 4575:5378 */       ex.printStackTrace();
/* 4576:5379 */       return false;
/* 4577:     */     }
/* 4578:5382 */     this.m_pasteB.setEnabled(true);
/* 4579:5383 */     revalidate();
/* 4580:5384 */     repaint();
/* 4581:5385 */     notifyIsDirty();
/* 4582:     */     
/* 4583:5387 */     return true;
/* 4584:     */   }
/* 4585:     */   
/* 4586:     */   protected boolean pasteFromBuffer(int x, int y, StringBuffer pasteBuffer, boolean addUndoPoint)
/* 4587:     */   {
/* 4588:5393 */     if (addUndoPoint) {
/* 4589:5394 */       addUndoPoint();
/* 4590:     */     }
/* 4591:5397 */     StringReader sr = new StringReader(pasteBuffer.toString());
/* 4592:     */     try
/* 4593:     */     {
/* 4594:5399 */       XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, this.m_mainKFPerspective.getCurrentTabIndex());
/* 4595:     */       
/* 4596:     */ 
/* 4597:5402 */       Vector<Vector<?>> v = (Vector)xml.read(sr);
/* 4598:     */       
/* 4599:5404 */       Vector<Object> beans = (Vector)v.get(0);
/* 4600:     */       
/* 4601:     */ 
/* 4602:5407 */       Vector<BeanConnection> connections = (Vector)v.get(1);
/* 4603:5410 */       for (int i = 0; i < beans.size(); i++)
/* 4604:     */       {
/* 4605:5411 */         BeanInstance b = (BeanInstance)beans.get(i);
/* 4606:5412 */         if ((b.getBean() instanceof MetaBean))
/* 4607:     */         {
/* 4608:5413 */           Vector<Object> subFlow = ((MetaBean)b.getBean()).getSubFlow();
/* 4609:5414 */           for (int j = 0; j < subFlow.size(); j++)
/* 4610:     */           {
/* 4611:5415 */             BeanInstance subB = (BeanInstance)subFlow.get(j);
/* 4612:5416 */             subB.removeBean(this.m_beanLayout, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4613:5418 */             if ((subB.getBean() instanceof Visible)) {
/* 4614:5419 */               ((Visible)subB.getBean()).getVisual().removePropertyChangeListener(this);
/* 4615:     */             }
/* 4616:     */           }
/* 4617:     */         }
/* 4618:     */       }
/* 4619:5429 */       int minX = 2147483647;
/* 4620:5430 */       int minY = 2147483647;
/* 4621:5431 */       boolean adjust = false;
/* 4622:5432 */       for (int i = 0; i < beans.size(); i++)
/* 4623:     */       {
/* 4624:5433 */         BeanInstance b = (BeanInstance)beans.get(i);
/* 4625:5434 */         if (b.getX() < minX)
/* 4626:     */         {
/* 4627:5435 */           minX = b.getX();
/* 4628:5436 */           adjust = true;
/* 4629:     */         }
/* 4630:5438 */         if (b.getY() < minY)
/* 4631:     */         {
/* 4632:5439 */           minY = b.getY();
/* 4633:5440 */           adjust = true;
/* 4634:     */         }
/* 4635:     */       }
/* 4636:5443 */       if (adjust)
/* 4637:     */       {
/* 4638:5444 */         int deltaX = x - minX;
/* 4639:5445 */         int deltaY = y - minY;
/* 4640:5446 */         for (int i = 0; i < beans.size(); i++)
/* 4641:     */         {
/* 4642:5447 */           BeanInstance b = (BeanInstance)beans.get(i);
/* 4643:     */           
/* 4644:     */ 
/* 4645:     */ 
/* 4646:5451 */           b.setXY(b.getX() + deltaX, b.getY() + deltaY);
/* 4647:     */         }
/* 4648:     */       }
/* 4649:5456 */       integrateFlow(beans, connections, false, false);
/* 4650:5457 */       for (int i = 0; i < beans.size(); i++) {
/* 4651:5458 */         checkForDuplicateName((BeanInstance)beans.get(i));
/* 4652:     */       }
/* 4653:5460 */       setEnvironment();
/* 4654:5461 */       notifyIsDirty();
/* 4655:5462 */       this.m_mainKFPerspective.setSelectedBeans(beans);
/* 4656:     */     }
/* 4657:     */     catch (Exception e)
/* 4658:     */     {
/* 4659:5464 */       this.m_logPanel.logMessage("[KnowledgeFlow] problem pasting beans: " + e.getMessage());
/* 4660:     */       
/* 4661:5466 */       e.printStackTrace();
/* 4662:     */     }
/* 4663:5469 */     revalidate();
/* 4664:5470 */     notifyIsDirty();
/* 4665:     */     
/* 4666:5472 */     return true;
/* 4667:     */   }
/* 4668:     */   
/* 4669:     */   private boolean pasteFromClipboard(int x, int y, StringBuffer pasteBuffer, boolean addUndoPoint)
/* 4670:     */   {
/* 4671:5478 */     return pasteFromBuffer(x, y, pasteBuffer, addUndoPoint);
/* 4672:     */   }
/* 4673:     */   
/* 4674:     */   private void deleteSelectedBeans()
/* 4675:     */   {
/* 4676:5483 */     Vector<Object> v = this.m_mainKFPerspective.getSelectedBeans();
/* 4677:5484 */     if (v.size() > 0) {
/* 4678:5485 */       this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 4679:     */     }
/* 4680:5487 */     addUndoPoint();
/* 4681:5489 */     for (int i = 0; i < v.size(); i++)
/* 4682:     */     {
/* 4683:5490 */       BeanInstance b = (BeanInstance)v.get(i);
/* 4684:     */       
/* 4685:5492 */       BeanConnection.removeConnections(b, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4686:     */       
/* 4687:5494 */       b.removeBean(this.m_beanLayout, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4688:5495 */       if ((b instanceof BeanCommon))
/* 4689:     */       {
/* 4690:5496 */         String key = ((BeanCommon)b).getCustomName() + "$" + b.hashCode();
/* 4691:5497 */         this.m_logPanel.statusMessage(key + "|remove");
/* 4692:     */       }
/* 4693:     */     }
/* 4694:5500 */     this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 4695:5501 */     revalidate();
/* 4696:5502 */     notifyIsDirty();
/* 4697:     */     
/* 4698:5504 */     this.m_selectAllB.setEnabled(BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) }).size() > 0);
/* 4699:     */   }
/* 4700:     */   
/* 4701:     */   private void addUndoPoint()
/* 4702:     */   {
/* 4703:     */     try
/* 4704:     */     {
/* 4705:5510 */       Stack<File> undo = this.m_mainKFPerspective.getUndoBuffer();
/* 4706:5511 */       File tempFile = File.createTempFile("knowledgeFlow", ".kfml");
/* 4707:5512 */       tempFile.deleteOnExit();
/* 4708:5514 */       if (saveLayout(tempFile, this.m_mainKFPerspective.getCurrentTabIndex(), true))
/* 4709:     */       {
/* 4710:5515 */         undo.push(tempFile);
/* 4711:5518 */         if (undo.size() > 20) {
/* 4712:5519 */           undo.remove(0);
/* 4713:     */         }
/* 4714:5521 */         this.m_undoB.setEnabled(true);
/* 4715:     */       }
/* 4716:     */     }
/* 4717:     */     catch (Exception ex)
/* 4718:     */     {
/* 4719:5525 */       this.m_logPanel.logMessage("[KnowledgeFlow] a problem occurred while trying to create a undo point : " + ex.getMessage());
/* 4720:     */     }
/* 4721:     */   }
/* 4722:     */   
/* 4723:     */   private boolean groupable(Vector<Object> selected, Vector<Object> inputs, Vector<Object> outputs)
/* 4724:     */   {
/* 4725:5533 */     boolean groupable = true;
/* 4726:5536 */     if ((inputs.size() == 0) || (outputs.size() == 0)) {
/* 4727:5537 */       return false;
/* 4728:     */     }
/* 4729:5542 */     for (int i = 0; i < selected.size(); i++)
/* 4730:     */     {
/* 4731:5543 */       BeanInstance temp = (BeanInstance)selected.elementAt(i);
/* 4732:5544 */       if ((temp.getBean() instanceof MetaBean))
/* 4733:     */       {
/* 4734:5545 */         groupable = false;
/* 4735:5546 */         return false;
/* 4736:     */       }
/* 4737:     */     }
/* 4738:5551 */     for (int i = 0; i < inputs.size(); i++)
/* 4739:     */     {
/* 4740:5552 */       BeanInstance temp = (BeanInstance)inputs.elementAt(i);
/* 4741:5553 */       if ((temp.getBean() instanceof Visible)) {
/* 4742:5554 */         ((Visible)temp.getBean()).getVisual().setDisplayConnectors(true, Color.red);
/* 4743:     */       }
/* 4744:     */     }
/* 4745:5560 */     for (int i = 0; i < outputs.size(); i++)
/* 4746:     */     {
/* 4747:5561 */       BeanInstance temp = (BeanInstance)outputs.elementAt(i);
/* 4748:5562 */       if ((temp.getBean() instanceof Visible)) {
/* 4749:5563 */         ((Visible)temp.getBean()).getVisual().setDisplayConnectors(true, Color.green);
/* 4750:     */       }
/* 4751:     */     }
/* 4752:5568 */     return groupable;
/* 4753:     */   }
/* 4754:     */   
/* 4755:     */   private void rightClickCanvasPopup(final int x, final int y)
/* 4756:     */   {
/* 4757:5574 */     Vector<BeanConnection> closestConnections = BeanConnection.getClosestConnections(new Point(x, y), 10, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4758:     */     
/* 4759:     */ 
/* 4760:     */ 
/* 4761:5578 */     PopupMenu rightClickMenu = new PopupMenu();
/* 4762:5579 */     int menuItemCount = 0;
/* 4763:5580 */     if ((this.m_mainKFPerspective.getSelectedBeans().size() > 0) || (closestConnections.size() > 0) || ((this.m_pasteBuffer != null) && (this.m_pasteBuffer.length() > 0)))
/* 4764:     */     {
/* 4765:5584 */       if (this.m_mainKFPerspective.getSelectedBeans().size() > 0)
/* 4766:     */       {
/* 4767:5586 */         MenuItem snapItem = new MenuItem("Snap selected to grid");
/* 4768:5587 */         snapItem.addActionListener(new ActionListener()
/* 4769:     */         {
/* 4770:     */           public void actionPerformed(ActionEvent e)
/* 4771:     */           {
/* 4772:5590 */             KnowledgeFlowApp.this.snapSelectedToGrid();
/* 4773:     */           }
/* 4774:5592 */         });
/* 4775:5593 */         rightClickMenu.add(snapItem);
/* 4776:5594 */         menuItemCount++;
/* 4777:     */         
/* 4778:5596 */         MenuItem copyItem = new MenuItem("Copy selected");
/* 4779:5597 */         copyItem.addActionListener(new ActionListener()
/* 4780:     */         {
/* 4781:     */           public void actionPerformed(ActionEvent e)
/* 4782:     */           {
/* 4783:5601 */             KnowledgeFlowApp.this.copyToClipboard();
/* 4784:5602 */             KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 4785:     */           }
/* 4786:5604 */         });
/* 4787:5605 */         rightClickMenu.add(copyItem);
/* 4788:5606 */         menuItemCount++;
/* 4789:     */         
/* 4790:5608 */         MenuItem cutItem = new MenuItem("Cut selected");
/* 4791:5609 */         cutItem.addActionListener(new ActionListener()
/* 4792:     */         {
/* 4793:     */           public void actionPerformed(ActionEvent e)
/* 4794:     */           {
/* 4795:5613 */             if (KnowledgeFlowApp.this.copyToClipboard()) {
/* 4796:5614 */               KnowledgeFlowApp.this.deleteSelectedBeans();
/* 4797:     */             }
/* 4798:     */           }
/* 4799:5617 */         });
/* 4800:5618 */         rightClickMenu.add(cutItem);
/* 4801:5619 */         menuItemCount++;
/* 4802:     */         
/* 4803:5621 */         MenuItem deleteSelected = new MenuItem("Delete selected");
/* 4804:5622 */         deleteSelected.addActionListener(new ActionListener()
/* 4805:     */         {
/* 4806:     */           public void actionPerformed(ActionEvent e)
/* 4807:     */           {
/* 4808:5626 */             KnowledgeFlowApp.this.deleteSelectedBeans();
/* 4809:     */           }
/* 4810:5628 */         });
/* 4811:5629 */         rightClickMenu.add(deleteSelected);
/* 4812:5630 */         menuItemCount++;
/* 4813:     */         
/* 4814:     */ 
/* 4815:5633 */         final Vector<Object> selected = this.m_mainKFPerspective.getSelectedBeans();
/* 4816:     */         
/* 4817:5635 */         final Vector<Object> inputs = BeanConnection.inputs(selected, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4818:     */         
/* 4819:5637 */         final Vector<Object> outputs = BeanConnection.outputs(selected, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4820:     */         
/* 4821:     */ 
/* 4822:5640 */         boolean groupable = groupable(selected, inputs, outputs);
/* 4823:5642 */         if (groupable)
/* 4824:     */         {
/* 4825:5643 */           MenuItem groupItem = new MenuItem("Group selected");
/* 4826:5644 */           groupItem.addActionListener(new ActionListener()
/* 4827:     */           {
/* 4828:     */             public void actionPerformed(ActionEvent e)
/* 4829:     */             {
/* 4830:5647 */               KnowledgeFlowApp.this.groupSubFlow(selected, inputs, outputs);
/* 4831:     */             }
/* 4832:5649 */           });
/* 4833:5650 */           rightClickMenu.add(groupItem);
/* 4834:5651 */           menuItemCount++;
/* 4835:     */         }
/* 4836:     */       }
/* 4837:5655 */       if ((this.m_pasteBuffer != null) && (this.m_pasteBuffer.length() > 0))
/* 4838:     */       {
/* 4839:5656 */         rightClickMenu.addSeparator();
/* 4840:5657 */         menuItemCount++;
/* 4841:     */         
/* 4842:5659 */         MenuItem pasteItem = new MenuItem("Paste");
/* 4843:5660 */         pasteItem.addActionListener(new ActionListener()
/* 4844:     */         {
/* 4845:     */           public void actionPerformed(ActionEvent e)
/* 4846:     */           {
/* 4847:5666 */             KnowledgeFlowApp.this.pasteFromClipboard(x, y, KnowledgeFlowApp.this.m_pasteBuffer, true);
/* 4848:     */           }
/* 4849:5668 */         });
/* 4850:5669 */         rightClickMenu.add(pasteItem);
/* 4851:5670 */         menuItemCount++;
/* 4852:     */       }
/* 4853:5673 */       if (closestConnections.size() > 0)
/* 4854:     */       {
/* 4855:5674 */         rightClickMenu.addSeparator();
/* 4856:5675 */         menuItemCount++;
/* 4857:     */         
/* 4858:5677 */         MenuItem deleteConnection = new MenuItem("Delete Connection:");
/* 4859:5678 */         deleteConnection.setEnabled(false);
/* 4860:5679 */         rightClickMenu.insert(deleteConnection, menuItemCount);
/* 4861:5680 */         menuItemCount++;
/* 4862:5682 */         for (int i = 0; i < closestConnections.size(); i++)
/* 4863:     */         {
/* 4864:5683 */           final BeanConnection bc = (BeanConnection)closestConnections.elementAt(i);
/* 4865:5684 */           String connName = bc.getSourceEventSetDescriptor().getName();
/* 4866:     */           
/* 4867:     */ 
/* 4868:5687 */           String targetName = "";
/* 4869:5688 */           if ((bc.getTarget().getBean() instanceof BeanCommon))
/* 4870:     */           {
/* 4871:5689 */             targetName = ((BeanCommon)bc.getTarget().getBean()).getCustomName();
/* 4872:     */           }
/* 4873:     */           else
/* 4874:     */           {
/* 4875:5692 */             targetName = bc.getTarget().getBean().getClass().getName();
/* 4876:5693 */             targetName = targetName.substring(targetName.lastIndexOf('.') + 1, targetName.length());
/* 4877:     */           }
/* 4878:5696 */           MenuItem deleteItem = new MenuItem(connName + "-->" + targetName);
/* 4879:5697 */           deleteItem.addActionListener(new ActionListener()
/* 4880:     */           {
/* 4881:     */             public void actionPerformed(ActionEvent e)
/* 4882:     */             {
/* 4883:5700 */               KnowledgeFlowApp.this.addUndoPoint();
/* 4884:     */               
/* 4885:5702 */               bc.remove(new Integer[] { Integer.valueOf(KnowledgeFlowApp.this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4886:5703 */               KnowledgeFlowApp.this.m_beanLayout.revalidate();
/* 4887:5704 */               KnowledgeFlowApp.this.m_beanLayout.repaint();
/* 4888:5705 */               KnowledgeFlowApp.this.m_mainKFPerspective.setEditedStatus(true);
/* 4889:5706 */               if (KnowledgeFlowApp.this.m_mainKFPerspective.getSelectedBeans().size() > 0) {
/* 4890:5707 */                 KnowledgeFlowApp.this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 4891:     */               }
/* 4892:5709 */               KnowledgeFlowApp.this.notifyIsDirty();
/* 4893:     */             }
/* 4894:5711 */           });
/* 4895:5712 */           rightClickMenu.add(deleteItem);
/* 4896:5713 */           menuItemCount++;
/* 4897:     */         }
/* 4898:     */       }
/* 4899:     */     }
/* 4900:5718 */     if (menuItemCount > 0)
/* 4901:     */     {
/* 4902:5719 */       rightClickMenu.addSeparator();
/* 4903:5720 */       menuItemCount++;
/* 4904:     */     }
/* 4905:5723 */     MenuItem noteItem = new MenuItem("New note");
/* 4906:5724 */     noteItem.addActionListener(new ActionListener()
/* 4907:     */     {
/* 4908:     */       public void actionPerformed(ActionEvent e)
/* 4909:     */       {
/* 4910:5728 */         Note n = new Note();
/* 4911:5729 */         KnowledgeFlowApp.this.m_toolBarBean = n;
/* 4912:     */         
/* 4913:5731 */         KnowledgeFlowApp.this.setCursor(Cursor.getPredefinedCursor(1));
/* 4914:     */         
/* 4915:5733 */         KnowledgeFlowApp.this.m_mode = 3;
/* 4916:     */       }
/* 4917:5735 */     });
/* 4918:5736 */     rightClickMenu.add(noteItem);
/* 4919:5737 */     menuItemCount++;
/* 4920:     */     
/* 4921:5739 */     this.m_beanLayout.add(rightClickMenu);
/* 4922:     */     
/* 4923:     */ 
/* 4924:5742 */     double z = this.m_layoutZoom / 100.0D;
/* 4925:5743 */     double px = x * z;
/* 4926:5744 */     double py = y * z;
/* 4927:5745 */     rightClickMenu.show(this.m_beanLayout, (int)px, (int)py);
/* 4928:     */   }
/* 4929:     */   
/* 4930:     */   private void connectComponents(EventSetDescriptor esd, BeanInstance bi, int x, int y)
/* 4931:     */   {
/* 4932:5759 */     if (this.m_mainKFPerspective.getSelectedBeans(this.m_mainKFPerspective.getCurrentTabIndex()).size() > 0) {
/* 4933:5761 */       this.m_mainKFPerspective.setSelectedBeans(this.m_mainKFPerspective.getCurrentTabIndex(), new Vector());
/* 4934:     */     }
/* 4935:5766 */     this.m_sourceEventSetDescriptor = esd;
/* 4936:     */     
/* 4937:5768 */     Class<?> listenerClass = esd.getListenerType();
/* 4938:5769 */     JComponent source = (JComponent)bi.getBean();
/* 4939:     */     
/* 4940:     */ 
/* 4941:5772 */     int targetCount = 0;
/* 4942:5773 */     Vector<Object> beanInstances = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 4943:5775 */     for (int i = 0; i < beanInstances.size(); i++)
/* 4944:     */     {
/* 4945:5776 */       JComponent bean = (JComponent)((BeanInstance)beanInstances.elementAt(i)).getBean();
/* 4946:     */       
/* 4947:     */ 
/* 4948:5779 */       boolean connectable = false;
/* 4949:5780 */       boolean canContinue = false;
/* 4950:5781 */       if (bean != source) {
/* 4951:5782 */         if ((bean instanceof MetaBean))
/* 4952:     */         {
/* 4953:5783 */           if (((MetaBean)bean).canAcceptConnection(listenerClass)) {
/* 4954:5784 */             canContinue = true;
/* 4955:     */           }
/* 4956:     */         }
/* 4957:5786 */         else if ((listenerClass.isInstance(bean)) && (bean != source)) {
/* 4958:5787 */           canContinue = true;
/* 4959:     */         }
/* 4960:     */       }
/* 4961:5790 */       if (canContinue)
/* 4962:     */       {
/* 4963:5791 */         if (!(bean instanceof BeanCommon)) {
/* 4964:5792 */           connectable = true;
/* 4965:5797 */         } else if (((BeanCommon)bean).connectionAllowed(esd)) {
/* 4966:5800 */           connectable = true;
/* 4967:     */         }
/* 4968:5803 */         if ((connectable) && 
/* 4969:5804 */           ((bean instanceof Visible)))
/* 4970:     */         {
/* 4971:5805 */           targetCount++;
/* 4972:5806 */           ((Visible)bean).getVisual().setDisplayConnectors(true);
/* 4973:     */         }
/* 4974:     */       }
/* 4975:     */     }
/* 4976:5813 */     if (targetCount > 0) {
/* 4977:5815 */       if ((source instanceof Visible))
/* 4978:     */       {
/* 4979:5816 */         ((Visible)source).getVisual().setDisplayConnectors(true);
/* 4980:     */         
/* 4981:5818 */         this.m_editElement = bi;
/* 4982:5819 */         Point closest = ((Visible)source).getVisual().getClosestConnectorPoint(new Point(x, y));
/* 4983:     */         
/* 4984:     */ 
/* 4985:5822 */         this.m_startX = ((int)closest.getX());
/* 4986:5823 */         this.m_startY = ((int)closest.getY());
/* 4987:5824 */         this.m_oldX = this.m_startX;
/* 4988:5825 */         this.m_oldY = this.m_startY;
/* 4989:     */         
/* 4990:5827 */         Graphics2D gx = (Graphics2D)this.m_beanLayout.getGraphics();
/* 4991:5828 */         gx.setXORMode(Color.white);
/* 4992:5829 */         gx.drawLine(this.m_startX, this.m_startY, this.m_startX, this.m_startY);
/* 4993:5830 */         gx.dispose();
/* 4994:5831 */         this.m_mode = 2;
/* 4995:     */       }
/* 4996:     */     }
/* 4997:5835 */     revalidate();
/* 4998:5836 */     repaint();
/* 4999:5837 */     notifyIsDirty();
/* 5000:     */   }
/* 5001:     */   
/* 5002:     */   private void checkForDuplicateName(BeanInstance comp)
/* 5003:     */   {
/* 5004:5841 */     if ((comp.getBean() instanceof BeanCommon))
/* 5005:     */     {
/* 5006:5842 */       String currentName = ((BeanCommon)comp.getBean()).getCustomName();
/* 5007:5843 */       if ((currentName != null) && (currentName.length() > 0))
/* 5008:     */       {
/* 5009:5844 */         Vector<Object> layoutBeans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5010:     */         
/* 5011:     */ 
/* 5012:5847 */         boolean exactMatch = false;
/* 5013:5848 */         int maxCopyNum = 1;
/* 5014:5849 */         for (int i = 0; i < layoutBeans.size(); i++)
/* 5015:     */         {
/* 5016:5850 */           BeanInstance b = (BeanInstance)layoutBeans.get(i);
/* 5017:5851 */           if ((b.getBean() instanceof BeanCommon))
/* 5018:     */           {
/* 5019:5852 */             String compName = ((BeanCommon)b.getBean()).getCustomName();
/* 5020:5853 */             if ((currentName.equals(compName)) && (b.getBean() != comp.getBean()))
/* 5021:     */             {
/* 5022:5854 */               exactMatch = true;
/* 5023:     */             }
/* 5024:5856 */             else if (compName.startsWith(currentName))
/* 5025:     */             {
/* 5026:5858 */               String num = compName.replace(currentName, "");
/* 5027:     */               try
/* 5028:     */               {
/* 5029:5860 */                 int compNum = Integer.parseInt(num);
/* 5030:5861 */                 if (compNum > maxCopyNum) {
/* 5031:5862 */                   maxCopyNum = compNum;
/* 5032:     */                 }
/* 5033:     */               }
/* 5034:     */               catch (NumberFormatException e) {}
/* 5035:     */             }
/* 5036:     */           }
/* 5037:     */         }
/* 5038:5871 */         if (exactMatch)
/* 5039:     */         {
/* 5040:5872 */           maxCopyNum++;
/* 5041:5873 */           currentName = currentName + "" + maxCopyNum;
/* 5042:5874 */           ((BeanCommon)comp.getBean()).setCustomName(currentName);
/* 5043:     */         }
/* 5044:     */       }
/* 5045:     */     }
/* 5046:     */   }
/* 5047:     */   
/* 5048:     */   private void addComponent(BeanInstance comp, boolean repaint)
/* 5049:     */   {
/* 5050:5881 */     if ((comp.getBean() instanceof Visible)) {
/* 5051:5882 */       ((Visible)comp.getBean()).getVisual().addPropertyChangeListener(this);
/* 5052:     */     }
/* 5053:5884 */     if ((comp.getBean() instanceof BeanCommon)) {
/* 5054:5885 */       ((BeanCommon)comp.getBean()).setLog(this.m_logPanel);
/* 5055:     */     }
/* 5056:5887 */     if ((comp.getBean() instanceof MetaBean))
/* 5057:     */     {
/* 5058:5891 */       Vector<Object> list = ((MetaBean)comp.getBean()).getInputs();
/* 5059:5892 */       for (int i = 0; i < list.size(); i++)
/* 5060:     */       {
/* 5061:5893 */         ((BeanInstance)list.get(i)).setX(comp.getX());
/* 5062:5894 */         ((BeanInstance)list.get(i)).setY(comp.getY());
/* 5063:     */       }
/* 5064:5897 */       list = ((MetaBean)comp.getBean()).getOutputs();
/* 5065:5898 */       for (int i = 0; i < list.size(); i++)
/* 5066:     */       {
/* 5067:5899 */         ((BeanInstance)list.get(i)).setX(comp.getX());
/* 5068:5900 */         ((BeanInstance)list.get(i)).setY(comp.getY());
/* 5069:     */       }
/* 5070:     */     }
/* 5071:5904 */     if ((comp.getBean() instanceof EnvironmentHandler)) {
/* 5072:5905 */       ((EnvironmentHandler)comp.getBean()).setEnvironment(this.m_flowEnvironment);
/* 5073:     */     }
/* 5074:5909 */     checkForDuplicateName(comp);
/* 5075:     */     
/* 5076:5911 */     setCursor(Cursor.getPredefinedCursor(0));
/* 5077:5913 */     if (repaint) {
/* 5078:5914 */       this.m_beanLayout.repaint();
/* 5079:     */     }
/* 5080:5916 */     this.m_pointerB.setSelected(true);
/* 5081:5917 */     this.m_mode = 0;
/* 5082:     */     
/* 5083:5919 */     this.m_selectAllB.setEnabled(BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) }).size() > 0);
/* 5084:     */   }
/* 5085:     */   
/* 5086:     */   private void addComponent(int x, int y)
/* 5087:     */   {
/* 5088:5924 */     if ((this.m_toolBarBean instanceof MetaBean))
/* 5089:     */     {
/* 5090:5927 */       Vector<BeanConnection> associatedConnections = ((MetaBean)this.m_toolBarBean).getAssociatedConnections();
/* 5091:     */       
/* 5092:5929 */       BeanConnection.getConnections(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) }).addAll(associatedConnections);
/* 5093:     */       
/* 5094:     */ 
/* 5095:     */ 
/* 5096:     */ 
/* 5097:5934 */       ((MetaBean)this.m_toolBarBean).addPropertyChangeListenersSubFlow(this);
/* 5098:     */     }
/* 5099:5938 */     if ((this.m_toolBarBean instanceof BeanContextChild)) {
/* 5100:5939 */       this.m_bcSupport.add(this.m_toolBarBean);
/* 5101:     */     }
/* 5102:5941 */     BeanInstance bi = new BeanInstance(this.m_beanLayout, this.m_toolBarBean, x, y, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5103:     */     
/* 5104:     */ 
/* 5105:5944 */     this.m_toolBarBean = null;
/* 5106:5945 */     addComponent(bi, true);
/* 5107:     */   }
/* 5108:     */   
/* 5109:     */   private void highlightSubFlow(int startX, int startY, int endX, int endY)
/* 5110:     */   {
/* 5111:5949 */     Rectangle r = new Rectangle(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(startX - endX), Math.abs(startY - endY));
/* 5112:     */     
/* 5113:     */ 
/* 5114:     */ 
/* 5115:5953 */     Vector<Object> selected = BeanInstance.findInstances(r, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5116:     */     
/* 5117:     */ 
/* 5118:     */ 
/* 5119:     */ 
/* 5120:     */ 
/* 5121:     */ 
/* 5122:     */ 
/* 5123:     */ 
/* 5124:     */ 
/* 5125:     */ 
/* 5126:5964 */     this.m_mainKFPerspective.setSelectedBeans(selected);
/* 5127:     */   }
/* 5128:     */   
/* 5129:     */   private void groupSubFlow(Vector<Object> selected, Vector<Object> inputs, Vector<Object> outputs)
/* 5130:     */   {
/* 5131:5970 */     int upperLeftX = 2147483647;
/* 5132:5971 */     int upperLeftY = 2147483647;
/* 5133:5972 */     int lowerRightX = -2147483648;
/* 5134:5973 */     int lowerRightY = -2147483648;
/* 5135:5974 */     for (int i = 0; i < selected.size(); i++)
/* 5136:     */     {
/* 5137:5975 */       BeanInstance b = (BeanInstance)selected.get(i);
/* 5138:5977 */       if (b.getX() < upperLeftX) {
/* 5139:5978 */         upperLeftX = b.getX();
/* 5140:     */       }
/* 5141:5981 */       if (b.getY() < upperLeftY) {
/* 5142:5982 */         upperLeftY = b.getY();
/* 5143:     */       }
/* 5144:5985 */       if (b.getX() > lowerRightX) {
/* 5145:5988 */         lowerRightX = b.getX();
/* 5146:     */       }
/* 5147:5991 */       if (b.getY() > lowerRightY) {
/* 5148:5994 */         lowerRightY = b.getY();
/* 5149:     */       }
/* 5150:     */     }
/* 5151:5998 */     int bx = upperLeftX + (lowerRightX - upperLeftX) / 2;
/* 5152:5999 */     int by = upperLeftY + (lowerRightY - upperLeftY) / 2;
/* 5153:     */     
/* 5154:6001 */     new Rectangle(upperLeftX, upperLeftY, lowerRightX, lowerRightY);
/* 5155:     */     
/* 5156:     */ 
/* 5157:     */ 
/* 5158:     */ 
/* 5159:     */ 
/* 5160:     */ 
/* 5161:     */ 
/* 5162:     */ 
/* 5163:6010 */     int result = JOptionPane.showConfirmDialog(this, "Group this sub-flow?", "Group Components", 0);
/* 5164:6012 */     if (result == 0)
/* 5165:     */     {
/* 5166:6013 */       Vector<BeanConnection> associatedConnections = BeanConnection.associatedConnections(selected, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5167:     */       
/* 5168:     */ 
/* 5169:     */ 
/* 5170:6017 */       String name = JOptionPane.showInputDialog(this, "Enter a name for this group", "MyGroup");
/* 5171:6019 */       if (name != null)
/* 5172:     */       {
/* 5173:6020 */         MetaBean group = new MetaBean();
/* 5174:     */         
/* 5175:     */ 
/* 5176:6023 */         group.setSubFlow(selected);
/* 5177:6024 */         group.setAssociatedConnections(associatedConnections);
/* 5178:6025 */         group.setInputs(inputs);
/* 5179:6026 */         group.setOutputs(outputs);
/* 5180:6028 */         if (name.length() > 0) {
/* 5181:6030 */           group.setCustomName(name);
/* 5182:     */         }
/* 5183:6048 */         Dimension d = group.getPreferredSize();
/* 5184:     */         
/* 5185:6050 */         int dx = (int)(d.getWidth() / 2.0D);
/* 5186:6051 */         int dy = (int)(d.getHeight() / 2.0D);
/* 5187:     */         
/* 5188:6053 */         BeanInstance bi = new BeanInstance(this.m_beanLayout, group, bx + dx, by + dy, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5189:6056 */         for (int i = 0; i < selected.size(); i++)
/* 5190:     */         {
/* 5191:6057 */           BeanInstance temp = (BeanInstance)selected.elementAt(i);
/* 5192:6058 */           temp.removeBean(this.m_beanLayout, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5193:6060 */           if ((temp.getBean() instanceof Visible)) {
/* 5194:6061 */             ((Visible)temp.getBean()).getVisual().removePropertyChangeListener(this);
/* 5195:     */           }
/* 5196:     */         }
/* 5197:6065 */         for (int i = 0; i < associatedConnections.size(); i++)
/* 5198:     */         {
/* 5199:6066 */           BeanConnection temp = (BeanConnection)associatedConnections.elementAt(i);
/* 5200:6067 */           temp.setHidden(true);
/* 5201:     */         }
/* 5202:6069 */         group.shiftBeans(bi, true);
/* 5203:     */         
/* 5204:6071 */         addComponent(bi, true);
/* 5205:     */       }
/* 5206:     */     }
/* 5207:6075 */     for (int i = 0; i < selected.size(); i++)
/* 5208:     */     {
/* 5209:6076 */       BeanInstance temp = (BeanInstance)selected.elementAt(i);
/* 5210:6077 */       if ((temp.getBean() instanceof Visible)) {
/* 5211:6078 */         ((Visible)temp.getBean()).getVisual().setDisplayConnectors(false);
/* 5212:     */       }
/* 5213:     */     }
/* 5214:6082 */     this.m_mainKFPerspective.setSelectedBeans(new Vector());
/* 5215:     */     
/* 5216:6084 */     revalidate();
/* 5217:6085 */     notifyIsDirty();
/* 5218:     */   }
/* 5219:     */   
/* 5220:     */   public void propertyChange(PropertyChangeEvent e)
/* 5221:     */   {
/* 5222:6095 */     revalidate();
/* 5223:6096 */     this.m_beanLayout.repaint();
/* 5224:     */   }
/* 5225:     */   
/* 5226:     */   private void loadLayout()
/* 5227:     */   {
/* 5228:6103 */     this.m_loadB.setEnabled(false);
/* 5229:6104 */     this.m_saveB.setEnabled(false);
/* 5230:6105 */     this.m_playB.setEnabled(false);
/* 5231:6106 */     this.m_playBB.setEnabled(false);
/* 5232:     */     
/* 5233:6108 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 5234:6109 */     if (returnVal == 0)
/* 5235:     */     {
/* 5236:6113 */       File oFile = this.m_FileChooser.getSelectedFile();
/* 5237:6117 */       if (this.m_FileChooser.getFileFilter() == this.m_KfFilter)
/* 5238:     */       {
/* 5239:6118 */         if (!oFile.getName().toLowerCase().endsWith(".kf")) {
/* 5240:6119 */           oFile = new File(oFile.getParent(), oFile.getName() + ".kf");
/* 5241:     */         }
/* 5242:     */       }
/* 5243:6121 */       else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/* 5244:     */       {
/* 5245:6122 */         if (!oFile.getName().toLowerCase().endsWith(".komlkf")) {
/* 5246:6123 */           oFile = new File(oFile.getParent(), oFile.getName() + ".koml" + "kf");
/* 5247:     */         }
/* 5248:     */       }
/* 5249:6126 */       else if (this.m_FileChooser.getFileFilter() == this.m_XMLFilter)
/* 5250:     */       {
/* 5251:6127 */         if (!oFile.getName().toLowerCase().endsWith(".kfml")) {
/* 5252:6128 */           oFile = new File(oFile.getParent(), oFile.getName() + ".kfml");
/* 5253:     */         }
/* 5254:     */       }
/* 5255:6131 */       else if ((this.m_FileChooser.getFileFilter() == this.m_XStreamFilter) && 
/* 5256:6132 */         (!oFile.getName().toLowerCase().endsWith(".xstreamkf"))) {
/* 5257:6134 */         oFile = new File(oFile.getParent(), oFile.getName() + ".xstream" + "kf");
/* 5258:     */       }
/* 5259:6139 */       String flowName = oFile.getName();
/* 5260:6140 */       if (flowName.lastIndexOf('.') > 0) {
/* 5261:6141 */         flowName = flowName.substring(0, flowName.lastIndexOf('.'));
/* 5262:     */       }
/* 5263:6144 */       loadLayout(oFile, getAllowMultipleTabs());
/* 5264:     */     }
/* 5265:6146 */     this.m_loadB.setEnabled(true);
/* 5266:6147 */     this.m_playB.setEnabled(true);
/* 5267:6148 */     this.m_playBB.setEnabled(true);
/* 5268:6149 */     this.m_saveB.setEnabled(true);
/* 5269:     */   }
/* 5270:     */   
/* 5271:     */   public void loadLayout(File oFile, boolean newTab)
/* 5272:     */   {
/* 5273:6160 */     loadLayout(oFile, newTab, false);
/* 5274:     */   }
/* 5275:     */   
/* 5276:     */   protected void loadLayout(File oFile, boolean newTab, boolean isUndo)
/* 5277:     */   {
/* 5278:6174 */     if (!newTab) {
/* 5279:6175 */       stopFlow();
/* 5280:     */     }
/* 5281:6178 */     this.m_loadB.setEnabled(false);
/* 5282:6179 */     this.m_saveB.setEnabled(false);
/* 5283:6180 */     this.m_playB.setEnabled(false);
/* 5284:6181 */     this.m_playBB.setEnabled(false);
/* 5285:6183 */     if (newTab)
/* 5286:     */     {
/* 5287:6184 */       String flowName = oFile.getName();
/* 5288:6185 */       if (flowName.lastIndexOf('.') > 0) {
/* 5289:6186 */         flowName = flowName.substring(0, flowName.lastIndexOf('.'));
/* 5290:     */       }
/* 5291:6188 */       this.m_mainKFPerspective.addTab(flowName);
/* 5292:     */       
/* 5293:6190 */       this.m_mainKFPerspective.setFlowFile(oFile);
/* 5294:6191 */       this.m_mainKFPerspective.setEditedStatus(false);
/* 5295:     */     }
/* 5296:6194 */     if (!isUndo)
/* 5297:     */     {
/* 5298:6195 */       File absolute = new File(oFile.getAbsolutePath());
/* 5299:     */       
/* 5300:     */ 
/* 5301:6198 */       this.m_mainKFPerspective.getEnvironmentSettings().addVariable("Internal.knowledgeflow.directory", absolute.getParent());
/* 5302:     */     }
/* 5303:     */     try
/* 5304:     */     {
/* 5305:6203 */       Vector<Object> beans = new Vector();
/* 5306:6204 */       Vector<BeanConnection> connections = new Vector();
/* 5307:6207 */       if ((KOML.isPresent()) && (oFile.getAbsolutePath().toLowerCase().endsWith(".komlkf")))
/* 5308:     */       {
/* 5309:6210 */         Vector<Vector<?>> v = (Vector)KOML.read(oFile.getAbsolutePath());
/* 5310:     */         
/* 5311:6212 */         beans = (Vector)v.get(0);
/* 5312:6213 */         connections = (Vector)v.get(1);
/* 5313:     */       }
/* 5314:6215 */       else if ((XStream.isPresent()) && (oFile.getAbsolutePath().toLowerCase().endsWith(".xstreamkf")))
/* 5315:     */       {
/* 5316:6218 */         Vector<Vector<?>> v = (Vector)XStream.read(oFile.getAbsolutePath());
/* 5317:     */         
/* 5318:6220 */         beans = (Vector)v.get(0);
/* 5319:6221 */         connections = (Vector)v.get(1);
/* 5320:     */       }
/* 5321:6223 */       else if (oFile.getAbsolutePath().toLowerCase().endsWith(".kfml"))
/* 5322:     */       {
/* 5323:6225 */         XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, this.m_mainKFPerspective.getCurrentTabIndex());
/* 5324:     */         
/* 5325:6227 */         Vector<Vector<?>> v = (Vector)xml.read(oFile);
/* 5326:6228 */         beans = (Vector)v.get(0);
/* 5327:6229 */         connections = (Vector)v.get(1);
/* 5328:     */       }
/* 5329:     */       else
/* 5330:     */       {
/* 5331:6233 */         InputStream is = new FileInputStream(oFile);
/* 5332:6234 */         ObjectInputStream ois = new ObjectInputStream(is);
/* 5333:6235 */         beans = (Vector)ois.readObject();
/* 5334:6236 */         connections = (Vector)ois.readObject();
/* 5335:6237 */         ois.close();
/* 5336:     */       }
/* 5337:6240 */       integrateFlow(beans, connections, true, false);
/* 5338:6241 */       setEnvironment();
/* 5339:6242 */       if (newTab)
/* 5340:     */       {
/* 5341:6243 */         this.m_logPanel.clearStatus();
/* 5342:6244 */         this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Flow loaded.");
/* 5343:     */       }
/* 5344:     */     }
/* 5345:     */     catch (Exception ex)
/* 5346:     */     {
/* 5347:6247 */       this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Unable to load flow (see log).");
/* 5348:     */       
/* 5349:6249 */       this.m_logPanel.logMessage("[KnowledgeFlow] Unable to load flow (" + ex.getMessage() + ").");
/* 5350:     */       
/* 5351:6251 */       ex.printStackTrace();
/* 5352:     */     }
/* 5353:6253 */     this.m_loadB.setEnabled(true);
/* 5354:6254 */     this.m_saveB.setEnabled(true);
/* 5355:6255 */     this.m_playB.setEnabled(true);
/* 5356:6256 */     this.m_playBB.setEnabled(true);
/* 5357:     */   }
/* 5358:     */   
/* 5359:     */   public void loadLayout(InputStream is, boolean newTab, String flowName)
/* 5360:     */     throws Exception
/* 5361:     */   {
/* 5362:6269 */     InputStreamReader isr = new InputStreamReader(is);
/* 5363:6270 */     loadLayout(isr, newTab, flowName);
/* 5364:     */   }
/* 5365:     */   
/* 5366:     */   public void loadLayout(Reader reader, boolean newTab, String flowName)
/* 5367:     */     throws Exception
/* 5368:     */   {
/* 5369:6286 */     if (!newTab) {
/* 5370:6287 */       stopFlow();
/* 5371:     */     }
/* 5372:6290 */     this.m_loadB.setEnabled(false);
/* 5373:6291 */     this.m_saveB.setEnabled(false);
/* 5374:6292 */     this.m_playB.setEnabled(false);
/* 5375:6293 */     this.m_playBB.setEnabled(false);
/* 5376:6295 */     if (newTab)
/* 5377:     */     {
/* 5378:6296 */       this.m_mainKFPerspective.addTab(flowName);
/* 5379:6297 */       this.m_mainKFPerspective.setEditedStatus(false);
/* 5380:     */     }
/* 5381:6300 */     XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, this.m_mainKFPerspective.getCurrentTabIndex());
/* 5382:     */     
/* 5383:6302 */     Vector<Vector<?>> v = (Vector)xml.read(reader);
/* 5384:6303 */     Vector<Object> beans = (Vector)v.get(0);
/* 5385:6304 */     Vector<BeanConnection> connections = (Vector)v.get(1);
/* 5386:     */     
/* 5387:     */ 
/* 5388:6307 */     integrateFlow(beans, connections, true, false);
/* 5389:6308 */     setEnvironment();
/* 5390:6309 */     if (newTab)
/* 5391:     */     {
/* 5392:6310 */       this.m_logPanel.clearStatus();
/* 5393:6311 */       this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Flow loaded.");
/* 5394:     */     }
/* 5395:6314 */     this.m_loadB.setEnabled(true);
/* 5396:6315 */     this.m_saveB.setEnabled(true);
/* 5397:6316 */     this.m_playB.setEnabled(true);
/* 5398:6317 */     this.m_playBB.setEnabled(true);
/* 5399:     */   }
/* 5400:     */   
/* 5401:     */   protected void integrateFlow(Vector<Object> beans, Vector<BeanConnection> connections, boolean replace, boolean notReplaceAndSourcedFromBinary)
/* 5402:     */   {
/* 5403:6324 */     Color bckC = getBackground();
/* 5404:6325 */     this.m_bcSupport = new BeanContextSupport();
/* 5405:6326 */     this.m_bcSupport.setDesignTime(true);
/* 5406:6330 */     for (int i = 0; i < beans.size(); i++)
/* 5407:     */     {
/* 5408:6331 */       BeanInstance tempB = (BeanInstance)beans.elementAt(i);
/* 5409:6332 */       if ((tempB.getBean() instanceof Visible))
/* 5410:     */       {
/* 5411:6333 */         ((Visible)tempB.getBean()).getVisual().addPropertyChangeListener(this);
/* 5412:     */         
/* 5413:     */ 
/* 5414:     */ 
/* 5415:     */ 
/* 5416:6338 */         ((Visible)tempB.getBean()).getVisual().setBackground(bckC);
/* 5417:6339 */         ((JComponent)tempB.getBean()).setBackground(bckC);
/* 5418:     */       }
/* 5419:6341 */       if ((tempB.getBean() instanceof BeanCommon)) {
/* 5420:6342 */         ((BeanCommon)tempB.getBean()).setLog(this.m_logPanel);
/* 5421:     */       }
/* 5422:6344 */       if ((tempB.getBean() instanceof BeanContextChild)) {
/* 5423:6345 */         this.m_bcSupport.add(tempB.getBean());
/* 5424:     */       }
/* 5425:     */     }
/* 5426:6349 */     if (replace)
/* 5427:     */     {
/* 5428:6350 */       BeanInstance.setBeanInstances(beans, this.m_beanLayout, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5429:     */       
/* 5430:6352 */       BeanConnection.setConnections(connections, new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5431:     */     }
/* 5432:6354 */     else if (notReplaceAndSourcedFromBinary)
/* 5433:     */     {
/* 5434:6355 */       BeanInstance.appendBeans(this.m_beanLayout, beans, this.m_mainKFPerspective.getCurrentTabIndex());
/* 5435:     */       
/* 5436:6357 */       BeanConnection.appendConnections(connections, this.m_mainKFPerspective.getCurrentTabIndex());
/* 5437:     */     }
/* 5438:6360 */     revalidate();
/* 5439:6361 */     this.m_beanLayout.revalidate();
/* 5440:6362 */     this.m_beanLayout.repaint();
/* 5441:6363 */     notifyIsDirty();
/* 5442:     */     
/* 5443:6365 */     this.m_selectAllB.setEnabled(BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) }).size() > 0);
/* 5444:     */   }
/* 5445:     */   
/* 5446:     */   public void setFlow(Vector<Vector<?>> v)
/* 5447:     */     throws Exception
/* 5448:     */   {
/* 5449:6382 */     if (getAllowMultipleTabs()) {
/* 5450:6383 */       throw new Exception("[KnowledgeFlow] setFlow() - can only set a flow in singe tab only mode");
/* 5451:     */     }
/* 5452:6398 */     this.m_beanLayout.removeAll();
/* 5453:6399 */     BeanInstance.init();
/* 5454:6400 */     BeanConnection.init();
/* 5455:     */     
/* 5456:6402 */     SerializedObject so = new SerializedObject(v);
/* 5457:6403 */     Vector<Vector<?>> copy = (Vector)so.getObject();
/* 5458:     */     
/* 5459:6405 */     Vector<Object> beans = (Vector)copy.elementAt(0);
/* 5460:6406 */     Vector<BeanConnection> connections = (Vector)copy.elementAt(1);
/* 5461:     */     
/* 5462:     */ 
/* 5463:     */ 
/* 5464:6410 */     this.m_flowEnvironment = new Environment();
/* 5465:6411 */     integrateFlow(beans, connections, true, false);
/* 5466:6412 */     revalidate();
/* 5467:6413 */     notifyIsDirty();
/* 5468:     */   }
/* 5469:     */   
/* 5470:     */   public Vector<Vector<?>> getFlow()
/* 5471:     */     throws Exception
/* 5472:     */   {
/* 5473:6425 */     Vector<Vector<?>> v = new Vector();
/* 5474:6426 */     Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5475:     */     
/* 5476:6428 */     Vector<BeanConnection> connections = BeanConnection.getConnections(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5477:     */     
/* 5478:6430 */     detachFromLayout(beans);
/* 5479:6431 */     v.add(beans);
/* 5480:6432 */     v.add(connections);
/* 5481:     */     
/* 5482:6434 */     SerializedObject so = new SerializedObject(v);
/* 5483:     */     
/* 5484:6436 */     Vector<Vector<?>> copy = (Vector)so.getObject();
/* 5485:     */     
/* 5486:     */ 
/* 5487:     */ 
/* 5488:6440 */     integrateFlow(beans, connections, true, false);
/* 5489:6441 */     return copy;
/* 5490:     */   }
/* 5491:     */   
/* 5492:     */   public String getFlowXML()
/* 5493:     */     throws Exception
/* 5494:     */   {
/* 5495:6451 */     Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_mainKFPerspective.getCurrentTabIndex()) });
/* 5496:     */     
/* 5497:     */ 
/* 5498:6454 */     StringBuffer buff = copyToBuffer(beans);
/* 5499:     */     
/* 5500:6456 */     return buff.toString();
/* 5501:     */   }
/* 5502:     */   
/* 5503:     */   protected static BufferedImage createImage(JComponent component, Rectangle region)
/* 5504:     */     throws IOException
/* 5505:     */   {
/* 5506:6469 */     boolean opaqueValue = component.isOpaque();
/* 5507:6470 */     component.setOpaque(true);
/* 5508:6471 */     BufferedImage image = new BufferedImage(region.width, region.height, 1);
/* 5509:     */     
/* 5510:6473 */     Graphics2D g2d = image.createGraphics();
/* 5511:6474 */     g2d.translate(-region.getX(), -region.getY());
/* 5512:     */     
/* 5513:6476 */     component.paint(g2d);
/* 5514:6477 */     g2d.dispose();
/* 5515:6478 */     component.setOpaque(opaqueValue);
/* 5516:     */     
/* 5517:6480 */     return image;
/* 5518:     */   }
/* 5519:     */   
/* 5520:     */   private void detachFromLayout(Vector<Object> beans)
/* 5521:     */   {
/* 5522:6486 */     for (int i = 0; i < beans.size(); i++)
/* 5523:     */     {
/* 5524:6487 */       BeanInstance tempB = (BeanInstance)beans.elementAt(i);
/* 5525:6488 */       if ((tempB.getBean() instanceof Visible))
/* 5526:     */       {
/* 5527:6489 */         ((Visible)tempB.getBean()).getVisual().removePropertyChangeListener(this);
/* 5528:6492 */         if ((tempB.getBean() instanceof MetaBean)) {
/* 5529:6493 */           ((MetaBean)tempB.getBean()).removePropertyChangeListenersSubFlow(this);
/* 5530:     */         }
/* 5531:6502 */         ((Visible)tempB.getBean()).getVisual().setBackground(Color.white);
/* 5532:     */         
/* 5533:6504 */         ((JComponent)tempB.getBean()).setBackground(Color.white);
/* 5534:     */       }
/* 5535:     */     }
/* 5536:     */   }
/* 5537:     */   
/* 5538:     */   public void saveLayout(File toFile, int tabIndex)
/* 5539:     */   {
/* 5540:6510 */     saveLayout(toFile, tabIndex, false);
/* 5541:     */   }
/* 5542:     */   
/* 5543:     */   protected boolean saveLayout(File sFile, int tabIndex, boolean isUndoPoint)
/* 5544:     */   {
/* 5545:6514 */     Color bckC = getBackground();
/* 5546:     */     
/* 5547:6516 */     Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(tabIndex) });
/* 5548:6517 */     detachFromLayout(beans);
/* 5549:6518 */     detachFromLayout(beans);
/* 5550:     */     try
/* 5551:     */     {
/* 5552:6523 */       if ((KOML.isPresent()) && (sFile.getAbsolutePath().toLowerCase().endsWith(".komlkf")))
/* 5553:     */       {
/* 5554:6526 */         Vector<Vector<?>> v = new Vector();
/* 5555:6527 */         v.setSize(2);
/* 5556:6528 */         v.set(0, beans);
/* 5557:6529 */         v.set(1, BeanConnection.getConnections(new Integer[] { Integer.valueOf(tabIndex) }));
/* 5558:     */         
/* 5559:6531 */         KOML.write(sFile.getAbsolutePath(), v);
/* 5560:     */       }
/* 5561:6532 */       else if ((XStream.isPresent()) && (sFile.getAbsolutePath().toLowerCase().endsWith(".xstreamkf")))
/* 5562:     */       {
/* 5563:6535 */         Vector<Vector<?>> v = new Vector();
/* 5564:6536 */         v.setSize(2);
/* 5565:6537 */         v.set(0, beans);
/* 5566:6538 */         v.set(1, BeanConnection.getConnections(new Integer[] { Integer.valueOf(tabIndex) }));
/* 5567:     */         
/* 5568:6540 */         XStream.write(sFile.getAbsolutePath(), v);
/* 5569:     */       }
/* 5570:6541 */       else if (sFile.getAbsolutePath().toLowerCase().endsWith(".kfml"))
/* 5571:     */       {
/* 5572:6543 */         Vector<Vector<?>> v = new Vector();
/* 5573:6544 */         v.setSize(2);
/* 5574:6545 */         v.set(0, beans);
/* 5575:6546 */         v.set(1, BeanConnection.getConnections(new Integer[] { Integer.valueOf(tabIndex) }));
/* 5576:     */         
/* 5577:6548 */         XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, tabIndex);
/* 5578:     */         
/* 5579:6550 */         BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sFile), "UTF-8"));
/* 5580:     */         
/* 5581:6552 */         xml.write(br, v);
/* 5582:     */       }
/* 5583:     */       else
/* 5584:     */       {
/* 5585:6554 */         OutputStream os = new FileOutputStream(sFile);
/* 5586:6555 */         ObjectOutputStream oos = new ObjectOutputStream(os);
/* 5587:6556 */         oos.writeObject(beans);
/* 5588:6557 */         oos.writeObject(BeanConnection.getConnections(new Integer[] { Integer.valueOf(tabIndex) }));
/* 5589:6558 */         oos.flush();
/* 5590:6559 */         oos.close();
/* 5591:     */       }
/* 5592:     */     }
/* 5593:     */     catch (Exception ex)
/* 5594:     */     {
/* 5595:     */       int i;
/* 5596:     */       BeanInstance tempB;
/* 5597:     */       Environment e;
/* 5598:     */       String tabTitle;
/* 5599:6562 */       this.m_logPanel.statusMessage("@!@[KnowledgeFlow]|Unable to save flow (see log).");
/* 5600:     */       
/* 5601:6564 */       this.m_logPanel.logMessage("[KnowledgeFlow] Unable to save flow (" + ex.getMessage() + ").");
/* 5602:     */       
/* 5603:6566 */       ex.printStackTrace();
/* 5604:     */       int i;
/* 5605:     */       BeanInstance tempB;
/* 5606:     */       Environment e;
/* 5607:     */       String tabTitle;
/* 5608:6567 */       return 0;
/* 5609:     */     }
/* 5610:     */     finally
/* 5611:     */     {
/* 5612:6570 */       for (int i = 0; i < beans.size(); i++)
/* 5613:     */       {
/* 5614:6571 */         BeanInstance tempB = (BeanInstance)beans.elementAt(i);
/* 5615:6572 */         if ((tempB.getBean() instanceof Visible))
/* 5616:     */         {
/* 5617:6573 */           ((Visible)tempB.getBean()).getVisual().addPropertyChangeListener(this);
/* 5618:6576 */           if ((tempB.getBean() instanceof MetaBean)) {
/* 5619:6577 */             ((MetaBean)tempB.getBean()).addPropertyChangeListenersSubFlow(this);
/* 5620:     */           }
/* 5621:6581 */           ((Visible)tempB.getBean()).getVisual().setBackground(bckC);
/* 5622:6582 */           ((JComponent)tempB.getBean()).setBackground(bckC);
/* 5623:     */         }
/* 5624:     */       }
/* 5625:6586 */       if (!isUndoPoint)
/* 5626:     */       {
/* 5627:6587 */         Environment e = this.m_mainKFPerspective.getEnvironmentSettings(tabIndex);
/* 5628:     */         
/* 5629:6589 */         e.addVariable("Internal.knowledgeflow.directory", new File(sFile.getAbsolutePath()).getParent());
/* 5630:     */         
/* 5631:6591 */         this.m_mainKFPerspective.setEditedStatus(tabIndex, false);
/* 5632:6592 */         String tabTitle = sFile.getName();
/* 5633:6593 */         tabTitle = tabTitle.substring(0, tabTitle.lastIndexOf('.'));
/* 5634:6594 */         this.m_mainKFPerspective.setTabTitle(tabIndex, tabTitle);
/* 5635:     */       }
/* 5636:     */     }
/* 5637:6597 */     return true;
/* 5638:     */   }
/* 5639:     */   
/* 5640:     */   private void saveLayout(int tabIndex, boolean showDialog)
/* 5641:     */   {
/* 5642:6604 */     getBackground();
/* 5643:     */     
/* 5644:6606 */     File sFile = this.m_mainKFPerspective.getFlowFile(tabIndex);
/* 5645:6607 */     int returnVal = 0;
/* 5646:6608 */     boolean shownDialog = false;
/* 5647:6610 */     if ((showDialog) || (sFile.getName().equals("-NONE-")))
/* 5648:     */     {
/* 5649:6611 */       returnVal = this.m_FileChooser.showSaveDialog(this);
/* 5650:6612 */       shownDialog = true;
/* 5651:     */     }
/* 5652:6615 */     if (returnVal == 0)
/* 5653:     */     {
/* 5654:6619 */       Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(tabIndex) });
/* 5655:6620 */       detachFromLayout(beans);
/* 5656:6623 */       if (shownDialog) {
/* 5657:6624 */         sFile = this.m_FileChooser.getSelectedFile();
/* 5658:     */       }
/* 5659:6628 */       if (this.m_FileChooser.getFileFilter() == this.m_KfFilter)
/* 5660:     */       {
/* 5661:6629 */         if (!sFile.getName().toLowerCase().endsWith(".kf")) {
/* 5662:6630 */           sFile = new File(sFile.getParent(), sFile.getName() + ".kf");
/* 5663:     */         }
/* 5664:     */       }
/* 5665:6632 */       else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/* 5666:     */       {
/* 5667:6633 */         if (!sFile.getName().toLowerCase().endsWith(".komlkf")) {
/* 5668:6634 */           sFile = new File(sFile.getParent(), sFile.getName() + ".koml" + "kf");
/* 5669:     */         }
/* 5670:     */       }
/* 5671:6637 */       else if (this.m_FileChooser.getFileFilter() == this.m_XStreamFilter)
/* 5672:     */       {
/* 5673:6638 */         if (!sFile.getName().toLowerCase().endsWith(".xstreamkf")) {
/* 5674:6640 */           sFile = new File(sFile.getParent(), sFile.getName() + ".xstream" + "kf");
/* 5675:     */         }
/* 5676:     */       }
/* 5677:6643 */       else if ((this.m_FileChooser.getFileFilter() == this.m_XMLFilter) && 
/* 5678:6644 */         (!sFile.getName().toLowerCase().endsWith(".kfml"))) {
/* 5679:6645 */         sFile = new File(sFile.getParent(), sFile.getName() + ".kfml");
/* 5680:     */       }
/* 5681:6650 */       saveLayout(sFile, this.m_mainKFPerspective.getCurrentTabIndex(), false);
/* 5682:6651 */       this.m_mainKFPerspective.setFlowFile(tabIndex, sFile);
/* 5683:     */     }
/* 5684:     */   }
/* 5685:     */   
/* 5686:     */   public void saveLayout(OutputStream out, int tabIndex)
/* 5687:     */   {
/* 5688:6664 */     Vector<Object> beans = BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(tabIndex) });
/* 5689:6666 */     for (int i = 0; i < beans.size(); i++)
/* 5690:     */     {
/* 5691:6667 */       BeanInstance tempB = (BeanInstance)beans.elementAt(i);
/* 5692:6669 */       if ((tempB.getBean() instanceof Visible))
/* 5693:     */       {
/* 5694:6670 */         ((Visible)tempB.getBean()).getVisual().removePropertyChangeListener(this);
/* 5695:6673 */         if ((tempB.getBean() instanceof MetaBean)) {
/* 5696:6674 */           ((MetaBean)tempB.getBean()).removePropertyChangeListenersSubFlow(this);
/* 5697:     */         }
/* 5698:     */       }
/* 5699:     */     }
/* 5700:     */     try
/* 5701:     */     {
/* 5702:6682 */       Vector<Vector<?>> v = new Vector();
/* 5703:6683 */       v.setSize(2);
/* 5704:6684 */       v.set(0, beans);
/* 5705:6685 */       v.set(1, BeanConnection.getConnections(new Integer[] { Integer.valueOf(tabIndex) }));
/* 5706:     */       
/* 5707:     */ 
/* 5708:6688 */       XMLBeans xml = new XMLBeans(this.m_beanLayout, this.m_bcSupport, tabIndex);
/* 5709:6689 */       xml.write(out, v);
/* 5710:     */     }
/* 5711:     */     catch (Exception ex)
/* 5712:     */     {
/* 5713:     */       int i;
/* 5714:     */       BeanInstance tempB;
/* 5715:6691 */       ex.printStackTrace();
/* 5716:     */     }
/* 5717:     */     finally
/* 5718:     */     {
/* 5719:     */       int i;
/* 5720:     */       BeanInstance tempB;
/* 5721:6694 */       for (int i = 0; i < beans.size(); i++)
/* 5722:     */       {
/* 5723:6695 */         BeanInstance tempB = (BeanInstance)beans.elementAt(i);
/* 5724:6697 */         if ((tempB.getBean() instanceof Visible))
/* 5725:     */         {
/* 5726:6698 */           ((Visible)tempB.getBean()).getVisual().addPropertyChangeListener(this);
/* 5727:6701 */           if ((tempB.getBean() instanceof MetaBean)) {
/* 5728:6702 */             ((MetaBean)tempB.getBean()).addPropertyChangeListenersSubFlow(this);
/* 5729:     */           }
/* 5730:     */         }
/* 5731:     */       }
/* 5732:     */     }
/* 5733:     */   }
/* 5734:     */   
/* 5735:     */   private void loadUserComponents()
/* 5736:     */   {
/* 5737:6712 */     Vector<Vector<Object>> tempV = null;
/* 5738:     */     
/* 5739:     */ 
/* 5740:     */ 
/* 5741:     */ 
/* 5742:6717 */     File sFile = new File(WekaPackageManager.WEKA_HOME.getPath() + File.separator + "knowledgeFlow" + File.separator + "userComponents");
/* 5743:6723 */     if (sFile.exists())
/* 5744:     */     {
/* 5745:     */       try
/* 5746:     */       {
/* 5747:6730 */         InputStream is = new FileInputStream(sFile);
/* 5748:6731 */         ObjectInputStream ois = new ObjectInputStream(is);
/* 5749:6732 */         tempV = (Vector)ois.readObject();
/* 5750:6733 */         ois.close();
/* 5751:     */       }
/* 5752:     */       catch (Exception ex)
/* 5753:     */       {
/* 5754:6736 */         Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Problem reading user components.");
/* 5755:     */         
/* 5756:6738 */         ex.printStackTrace();
/* 5757:6739 */         return;
/* 5758:     */       }
/* 5759:6741 */       if (tempV.size() > 0)
/* 5760:     */       {
/* 5761:6742 */         DefaultTreeModel model = (DefaultTreeModel)this.m_componentTree.getModel();
/* 5762:6743 */         DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
/* 5763:6744 */         if (this.m_userCompNode == null)
/* 5764:     */         {
/* 5765:6745 */           this.m_userCompNode = new InvisibleNode("User");
/* 5766:6746 */           model.insertNodeInto(this.m_userCompNode, root, 0);
/* 5767:     */         }
/* 5768:6750 */         for (int i = 0; i < tempV.size(); i++)
/* 5769:     */         {
/* 5770:6751 */           Vector<Object> tempB = (Vector)tempV.elementAt(i);
/* 5771:6752 */           String displayName = (String)tempB.get(0);
/* 5772:6753 */           tempB.get(1);
/* 5773:6754 */           ImageIcon scaledIcon = (ImageIcon)tempB.get(2);
/* 5774:6755 */           JTreeLeafDetails treeLeaf = new JTreeLeafDetails(displayName, tempB, scaledIcon);
/* 5775:     */           
/* 5776:6757 */           DefaultMutableTreeNode newUserComp = new InvisibleNode(treeLeaf);
/* 5777:6758 */           model.insertNodeInto(newUserComp, this.m_userCompNode, 0);
/* 5778:     */           
/* 5779:     */ 
/* 5780:6761 */           this.m_userComponents.add(tempB);
/* 5781:     */         }
/* 5782:     */       }
/* 5783:     */     }
/* 5784:     */   }
/* 5785:     */   
/* 5786:     */   private void installWindowListenerForSavingUserStuff()
/* 5787:     */   {
/* 5788:6771 */     ((Window)getTopLevelAncestor()).addWindowListener(new WindowAdapter()
/* 5789:     */     {
/* 5790:     */       public void windowClosing(WindowEvent e)
/* 5791:     */       {
/* 5792:6776 */         Logger.log(Logger.Level.INFO, "[KnowledgeFlow] Saving user components....");
/* 5793:     */         
/* 5794:6778 */         File sFile = new File(WekaPackageManager.WEKA_HOME.getPath() + File.separator + "knowledgeFlow");
/* 5795:6781 */         if ((!sFile.exists()) && 
/* 5796:6782 */           (!sFile.mkdir())) {
/* 5797:6783 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Unable to create \"" + sFile.getPath() + "\" directory");
/* 5798:     */         }
/* 5799:     */         try
/* 5800:     */         {
/* 5801:6790 */           String ext = "";
/* 5802:     */           
/* 5803:     */ 
/* 5804:     */ 
/* 5805:6794 */           File sFile2 = new File(sFile.getAbsolutePath() + File.separator + "userComponents" + ext);
/* 5806:     */           
/* 5807:     */ 
/* 5808:     */ 
/* 5809:     */ 
/* 5810:     */ 
/* 5811:     */ 
/* 5812:     */ 
/* 5813:     */ 
/* 5814:     */ 
/* 5815:6804 */           OutputStream os = new FileOutputStream(sFile2);
/* 5816:6805 */           ObjectOutputStream oos = new ObjectOutputStream(os);
/* 5817:6806 */           oos.writeObject(KnowledgeFlowApp.this.m_userComponents);
/* 5818:6807 */           oos.flush();
/* 5819:6808 */           oos.close();
/* 5820:     */         }
/* 5821:     */         catch (Exception ex)
/* 5822:     */         {
/* 5823:6811 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Unable to save user components");
/* 5824:     */           
/* 5825:     */ 
/* 5826:6814 */           ex.printStackTrace();
/* 5827:     */         }
/* 5828:6818 */         Logger.log(Logger.Level.INFO, "Saving preferences for selected perspectives...");
/* 5829:     */         
/* 5830:6820 */         sFile = new File(WekaPackageManager.PROPERTIES_DIR.toString() + File.separator + "VisiblePerspectives.props");
/* 5831:     */         try
/* 5832:     */         {
/* 5833:6823 */           FileWriter f = new FileWriter(sFile);
/* 5834:6824 */           f.write("weka.gui.beans.KnowledgeFlow.SelectedPerspectives=");
/* 5835:6825 */           int i = 0;
/* 5836:6826 */           for (String p : BeansProperties.VISIBLE_PERSPECTIVES)
/* 5837:     */           {
/* 5838:6827 */             if (i > 0) {
/* 5839:6828 */               f.write(",");
/* 5840:     */             }
/* 5841:6830 */             f.write(p);
/* 5842:6831 */             i++;
/* 5843:     */           }
/* 5844:6833 */           f.write("\n");
/* 5845:     */           
/* 5846:6835 */           f.write("weka.gui.beans.KnowledgeFlow.PerspectiveToolBarVisisble=" + (KnowledgeFlowApp.this.m_configAndPerspectivesVisible ? "yes" : "no"));
/* 5847:     */           
/* 5848:6837 */           f.write("\n");
/* 5849:6838 */           f.close();
/* 5850:     */         }
/* 5851:     */         catch (Exception ex)
/* 5852:     */         {
/* 5853:6840 */           Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] Unable to save user perspectives preferences");
/* 5854:     */           
/* 5855:     */ 
/* 5856:6843 */           ex.printStackTrace();
/* 5857:     */         }
/* 5858:     */       }
/* 5859:     */     });
/* 5860:     */   }
/* 5861:     */   
/* 5862:     */   public static String getGlobalInfo(Object tempBean)
/* 5863:     */   {
/* 5864:6859 */     return Utils.getGlobalInfo(tempBean, true);
/* 5865:     */   }
/* 5866:     */   
/* 5867:6870 */   private static Memory m_Memory = new Memory(true);
/* 5868:6874 */   public static Vector<StartUpListener> s_startupListeners = new Vector();
/* 5869:6880 */   private boolean m_showFileMenu = true;
/* 5870:     */   
/* 5871:     */   public static void createSingleton(String[] args)
/* 5872:     */   {
/* 5873:6891 */     String fileName = null;
/* 5874:6892 */     boolean showFileMenu = true;
/* 5875:6894 */     if ((args != null) && (args.length > 0)) {
/* 5876:6895 */       for (String arg : args) {
/* 5877:6896 */         if (arg.startsWith("file=")) {
/* 5878:6897 */           fileName = arg.substring("file=".length());
/* 5879:6898 */         } else if (arg.startsWith("showFileMenu=")) {
/* 5880:6899 */           showFileMenu = Boolean.parseBoolean(arg.substring("showFileMenu=".length()));
/* 5881:     */         }
/* 5882:     */       }
/* 5883:     */     }
/* 5884:6905 */     if (m_knowledgeFlow == null) {
/* 5885:6906 */       m_knowledgeFlow = new KnowledgeFlowApp(showFileMenu);
/* 5886:     */     }
/* 5887:6912 */     for (int i = 0; i < s_startupListeners.size(); i++) {
/* 5888:6913 */       ((StartUpListener)s_startupListeners.elementAt(i)).startUpComplete();
/* 5889:     */     }
/* 5890:6917 */     if (fileName != null) {
/* 5891:6918 */       m_knowledgeFlow.loadInitialLayout(fileName);
/* 5892:     */     }
/* 5893:     */   }
/* 5894:     */   
/* 5895:     */   public static void disposeSingleton()
/* 5896:     */   {
/* 5897:6925 */     m_knowledgeFlow = null;
/* 5898:     */   }
/* 5899:     */   
/* 5900:     */   public static KnowledgeFlowApp getSingleton()
/* 5901:     */   {
/* 5902:6934 */     return m_knowledgeFlow;
/* 5903:     */   }
/* 5904:     */   
/* 5905:     */   public static void addStartupListener(StartUpListener s)
/* 5906:     */   {
/* 5907:6943 */     s_startupListeners.add(s);
/* 5908:     */   }
/* 5909:     */   
/* 5910:     */   private void loadInitialLayout(String fileName)
/* 5911:     */   {
/* 5912:6953 */     File oFile = new File(fileName);
/* 5913:6955 */     if ((oFile.exists()) && (oFile.isFile()))
/* 5914:     */     {
/* 5915:6956 */       this.m_FileChooser.setSelectedFile(oFile);
/* 5916:     */       
/* 5917:6958 */       int index = fileName.lastIndexOf('.');
/* 5918:6960 */       if (index != -1)
/* 5919:     */       {
/* 5920:6961 */         String extension = fileName.substring(index);
/* 5921:6963 */         if (".kfml".equalsIgnoreCase(extension)) {
/* 5922:6964 */           this.m_FileChooser.setFileFilter(m_knowledgeFlow.m_XMLFilter);
/* 5923:6965 */         } else if (".kf".equalsIgnoreCase(extension)) {
/* 5924:6966 */           this.m_FileChooser.setFileFilter(m_knowledgeFlow.m_KfFilter);
/* 5925:     */         }
/* 5926:     */       }
/* 5927:     */     }
/* 5928:     */     else
/* 5929:     */     {
/* 5930:6970 */       Logger.log(Logger.Level.WARNING, "[KnowledgeFlow] File '" + fileName + "' does not exists.");
/* 5931:     */     }
/* 5932:6975 */     loadLayout(oFile, true);
/* 5933:     */   }
/* 5934:     */   
/* 5935:     */   public void setAllowMultipleTabs(boolean multiple)
/* 5936:     */   {
/* 5937:6979 */     this.m_allowMultipleTabs = multiple;
/* 5938:6981 */     if (!multiple)
/* 5939:     */     {
/* 5940:6982 */       this.m_newB.setEnabled(false);
/* 5941:6983 */       if (this.m_configAndPerspectives != null) {
/* 5942:6984 */         remove(this.m_configAndPerspectives);
/* 5943:     */       }
/* 5944:     */     }
/* 5945:     */   }
/* 5946:     */   
/* 5947:     */   public boolean getAllowMultipleTabs()
/* 5948:     */   {
/* 5949:6990 */     return this.m_allowMultipleTabs;
/* 5950:     */   }
/* 5951:     */   
/* 5952:     */   private void notifyIsDirty()
/* 5953:     */   {
/* 5954:7003 */     firePropertyChange("PROP_DIRTY", null, null);
/* 5955:     */   }
/* 5956:     */   
/* 5957:     */   public static void main(String[] args)
/* 5958:     */   {
/* 5959:7013 */     LookAndFeel.setLookAndFeel();
/* 5960:     */     try
/* 5961:     */     {
/* 5962:7019 */       JFrame jf = new JFrame();
/* 5963:7020 */       jf.getContentPane().setLayout(new BorderLayout());
/* 5964:7025 */       for (int i = 0; i < args.length; i++) {
/* 5965:7026 */         if ((args[i].toLowerCase().endsWith(".kf")) || (args[i].toLowerCase().endsWith(".kfml"))) {
/* 5966:7028 */           args[i] = ("file=" + args[i]);
/* 5967:     */         }
/* 5968:     */       }
/* 5969:7032 */       createSingleton(args);
/* 5970:     */       
/* 5971:7034 */       Image icon = Toolkit.getDefaultToolkit().getImage(m_knowledgeFlow.getClass().getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 5972:     */       
/* 5973:     */ 
/* 5974:7037 */       jf.setIconImage(icon);
/* 5975:     */       
/* 5976:7039 */       jf.getContentPane().add(m_knowledgeFlow, "Center");
/* 5977:7040 */       jf.setDefaultCloseOperation(3);
/* 5978:     */       
/* 5979:7042 */       jf.setSize(1024, 768);
/* 5980:7043 */       jf.setVisible(true);
/* 5981:     */       
/* 5982:7045 */       Thread memMonitor = new Thread()
/* 5983:     */       {
/* 5984:     */         public void run()
/* 5985:     */         {
/* 5986:     */           for (;;)
/* 5987:     */           {
/* 5988:7054 */             if (KnowledgeFlowApp.m_Memory.isOutOfMemory())
/* 5989:     */             {
/* 5990:7056 */               this.val$jf.dispose();
/* 5991:7057 */               KnowledgeFlowApp.access$4802(null);
/* 5992:7058 */               System.gc();
/* 5993:     */               
/* 5994:     */ 
/* 5995:7061 */               System.err.println("\n[KnowledgeFlow] displayed message:");
/* 5996:7062 */               KnowledgeFlowApp.m_Memory.showOutOfMemory();
/* 5997:7063 */               System.err.println("\nexiting");
/* 5998:7064 */               System.exit(-1);
/* 5999:     */             }
/* 6000:     */           }
/* 6001:     */         }
/* 6002:7073 */       };
/* 6003:7074 */       memMonitor.setPriority(5);
/* 6004:7075 */       memMonitor.start();
/* 6005:     */     }
/* 6006:     */     catch (Exception ex)
/* 6007:     */     {
/* 6008:7077 */       ex.printStackTrace();
/* 6009:7078 */       System.err.println(ex.getMessage());
/* 6010:     */     }
/* 6011:     */   }
/* 6012:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.KnowledgeFlowApp
 * JD-Core Version:    0.7.0.1
 */