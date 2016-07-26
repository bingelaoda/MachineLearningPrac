/*    1:     */ package weka.gui.beans.xml;
/*    2:     */ 
/*    3:     */ import java.awt.Color;
/*    4:     */ import java.awt.Dimension;
/*    5:     */ import java.awt.Font;
/*    6:     */ import java.awt.Point;
/*    7:     */ import java.beans.BeanInfo;
/*    8:     */ import java.beans.EventSetDescriptor;
/*    9:     */ import java.beans.Introspector;
/*   10:     */ import java.beans.beancontext.BeanContextSupport;
/*   11:     */ import java.io.File;
/*   12:     */ import java.io.PrintStream;
/*   13:     */ import java.util.Enumeration;
/*   14:     */ import java.util.Hashtable;
/*   15:     */ import java.util.StringTokenizer;
/*   16:     */ import java.util.Vector;
/*   17:     */ import javax.swing.JComponent;
/*   18:     */ import javax.swing.JPanel;
/*   19:     */ import javax.swing.plaf.ColorUIResource;
/*   20:     */ import javax.swing.plaf.FontUIResource;
/*   21:     */ import org.w3c.dom.Document;
/*   22:     */ import org.w3c.dom.Element;
/*   23:     */ import org.w3c.dom.NodeList;
/*   24:     */ import weka.core.Environment;
/*   25:     */ import weka.core.EnvironmentHandler;
/*   26:     */ import weka.core.OptionHandler;
/*   27:     */ import weka.core.converters.AbstractFileLoader;
/*   28:     */ import weka.core.converters.AbstractFileSaver;
/*   29:     */ import weka.core.converters.ConverterUtils;
/*   30:     */ import weka.core.converters.DatabaseLoader;
/*   31:     */ import weka.core.converters.DatabaseSaver;
/*   32:     */ import weka.core.converters.FileSourcedConverter;
/*   33:     */ import weka.core.converters.TextDirectoryLoader;
/*   34:     */ import weka.core.xml.PropertyHandler;
/*   35:     */ import weka.core.xml.XMLBasicSerialization;
/*   36:     */ import weka.core.xml.XMLDocument;
/*   37:     */ import weka.core.xml.XMLSerializationMethodHandler;
/*   38:     */ import weka.experiment.ResultProducer;
/*   39:     */ import weka.experiment.SplitEvaluator;
/*   40:     */ import weka.gui.beans.BeanCommon;
/*   41:     */ import weka.gui.beans.BeanConnection;
/*   42:     */ import weka.gui.beans.BeanInstance;
/*   43:     */ import weka.gui.beans.BeanVisual;
/*   44:     */ import weka.gui.beans.MetaBean;
/*   45:     */ import weka.gui.beans.Visible;
/*   46:     */ 
/*   47:     */ public class XMLBeans
/*   48:     */   extends XMLBasicSerialization
/*   49:     */ {
/*   50:     */   public static final String VAL_ID = "id";
/*   51:     */   public static final String VAL_X = "x";
/*   52:     */   public static final String VAL_Y = "y";
/*   53:     */   public static final String VAL_BEAN = "bean";
/*   54:     */   public static final String VAL_CUSTOM_NAME = "custom_name";
/*   55:     */   public static final String VAL_SOURCEID = "source_id";
/*   56:     */   public static final String VAL_TARGETID = "target_id";
/*   57:     */   public static final String VAL_EVENTNAME = "eventname";
/*   58:     */   public static final String VAL_HIDDEN = "hidden";
/*   59:     */   public static final String VAL_FILE = "file";
/*   60:     */   public static final String VAL_DIR = "dir";
/*   61:     */   public static final String VAL_PREFIX = "prefix";
/*   62:     */   public static final String VAL_RELATIVE_PATH = "useRelativePath";
/*   63:     */   public static final String VAL_OPTIONS = "options";
/*   64:     */   public static final String VAL_SAVER = "wrappedAlgorithm";
/*   65:     */   public static final String VAL_LOADER = "wrappedAlgorithm";
/*   66:     */   public static final String VAL_TEXT = "text";
/*   67:     */   public static final String VAL_BEANCONTEXT = "beanContext";
/*   68:     */   public static final String VAL_WIDTH = "width";
/*   69:     */   public static final String VAL_HEIGHT = "height";
/*   70:     */   public static final String VAL_RED = "red";
/*   71:     */   public static final String VAL_GREEN = "green";
/*   72:     */   public static final String VAL_BLUE = "blue";
/*   73:     */   public static final String VAL_NAME = "name";
/*   74:     */   public static final String VAL_STYLE = "style";
/*   75:     */   public static final String VAL_LOCATION = "location";
/*   76:     */   public static final String VAL_SIZE = "size";
/*   77:     */   public static final String VAL_COLOR = "color";
/*   78:     */   public static final String VAL_FONT = "font";
/*   79:     */   public static final String VAL_ICONPATH = "iconPath";
/*   80:     */   public static final String VAL_ANIMATEDICONPATH = "animatedIconPath";
/*   81:     */   public static final String VAL_ASSOCIATEDCONNECTIONS = "associatedConnections";
/*   82:     */   public static final String VAL_INPUTS = "inputs";
/*   83:     */   public static final String VAL_INPUTSID = "inputs_id";
/*   84:     */   public static final String VAL_OUTPUTS = "outputs";
/*   85:     */   public static final String VAL_OUTPUTSID = "outputs_id";
/*   86:     */   public static final String VAL_SUBFLOW = "subFlow";
/*   87:     */   public static final String VAL_ORIGINALCOORDS = "originalCoords";
/*   88:     */   public static final String VAL_RELATIONNAMEFORFILENAME = "relationNameForFilename";
/*   89:     */   public static final int INDEX_BEANINSTANCES = 0;
/*   90:     */   public static final int INDEX_BEANCONNECTIONS = 1;
/*   91:     */   protected JComponent m_BeanLayout;
/*   92:     */   protected Vector<Object> m_BeanInstances;
/*   93:     */   protected Vector<Integer> m_BeanInstancesID;
/*   94:     */   protected boolean m_IgnoreBeanConnections;
/*   95:     */   protected MetaBean m_CurrentMetaBean;
/*   96:     */   protected static final String REGULAR_CONNECTION = "regular_connection";
/*   97:     */   protected Hashtable<Object, Vector<String>> m_BeanConnectionRelation;
/*   98:     */   public static final int DATATYPE_LAYOUT = 0;
/*   99:     */   public static final int DATATYPE_USERCOMPONENTS = 1;
/*  100: 243 */   protected int m_DataType = 0;
/*  101: 249 */   protected BeanContextSupport m_BeanContextSupport = null;
/*  102: 255 */   protected int m_vectorIndex = 0;
/*  103:     */   
/*  104:     */   public XMLBeans(JComponent layout, BeanContextSupport context, int tab)
/*  105:     */     throws Exception
/*  106:     */   {
/*  107: 267 */     this(layout, context, 0, tab);
/*  108:     */   }
/*  109:     */   
/*  110:     */   public XMLBeans(JComponent layout, BeanContextSupport context, int datatype, int tab)
/*  111:     */     throws Exception
/*  112:     */   {
/*  113: 282 */     this.m_vectorIndex = tab;
/*  114: 283 */     this.m_BeanLayout = layout;
/*  115: 284 */     this.m_BeanContextSupport = context;
/*  116: 285 */     setDataType(datatype);
/*  117:     */   }
/*  118:     */   
/*  119:     */   public void setDataType(int value)
/*  120:     */   {
/*  121: 295 */     if (value == 0) {
/*  122: 296 */       this.m_DataType = value;
/*  123: 297 */     } else if (value == 1) {
/*  124: 298 */       this.m_DataType = value;
/*  125:     */     } else {
/*  126: 300 */       System.out.println("DataType '" + value + "' is unknown!");
/*  127:     */     }
/*  128:     */   }
/*  129:     */   
/*  130:     */   public int getDataType()
/*  131:     */   {
/*  132: 311 */     return this.m_DataType;
/*  133:     */   }
/*  134:     */   
/*  135:     */   public void clear()
/*  136:     */     throws Exception
/*  137:     */   {
/*  138: 325 */     super.clear();
/*  139:     */     
/*  140:     */ 
/*  141:     */ 
/*  142:     */ 
/*  143:     */ 
/*  144: 331 */     this.m_Properties.addIgnored("UI");
/*  145: 332 */     this.m_Properties.addIgnored("actionMap");
/*  146: 333 */     this.m_Properties.addIgnored("alignmentX");
/*  147: 334 */     this.m_Properties.addIgnored("alignmentY");
/*  148: 335 */     this.m_Properties.addIgnored("autoscrolls");
/*  149: 336 */     this.m_Properties.addIgnored("background");
/*  150: 337 */     this.m_Properties.addIgnored("border");
/*  151: 338 */     this.m_Properties.addIgnored("componentPopupMenu");
/*  152: 339 */     this.m_Properties.addIgnored("debugGraphicsOptions");
/*  153: 340 */     this.m_Properties.addIgnored("doubleBuffered");
/*  154: 341 */     this.m_Properties.addIgnored("enabled");
/*  155: 342 */     this.m_Properties.addIgnored("focusCycleRoot");
/*  156: 343 */     this.m_Properties.addIgnored("focusTraversalPolicy");
/*  157: 344 */     this.m_Properties.addIgnored("focusTraversalPolicyProvider");
/*  158: 345 */     this.m_Properties.addIgnored("focusable");
/*  159: 346 */     this.m_Properties.addIgnored("font");
/*  160: 347 */     this.m_Properties.addIgnored("foreground");
/*  161: 348 */     this.m_Properties.addIgnored("inheritsPopupMenu");
/*  162: 349 */     this.m_Properties.addIgnored("inputVerifier");
/*  163: 350 */     this.m_Properties.addIgnored("layout");
/*  164: 351 */     this.m_Properties.addIgnored("locale");
/*  165: 352 */     this.m_Properties.addIgnored("maximumSize");
/*  166: 353 */     this.m_Properties.addIgnored("minimumSize");
/*  167: 354 */     this.m_Properties.addIgnored("nextFocusableComponent");
/*  168: 355 */     this.m_Properties.addIgnored("opaque");
/*  169: 356 */     this.m_Properties.addIgnored("preferredSize");
/*  170: 357 */     this.m_Properties.addIgnored("requestFocusEnabled");
/*  171: 358 */     this.m_Properties.addIgnored("toolTipText");
/*  172: 359 */     this.m_Properties.addIgnored("transferHandler");
/*  173: 360 */     this.m_Properties.addIgnored("verifyInputWhenFocusTarget");
/*  174: 361 */     this.m_Properties.addIgnored("visible");
/*  175:     */     
/*  176:     */ 
/*  177: 364 */     this.m_Properties.addIgnored("size");
/*  178:     */     
/*  179: 366 */     this.m_Properties.addIgnored("location");
/*  180:     */     
/*  181:     */ 
/*  182:     */ 
/*  183: 370 */     this.m_Properties.addAllowed(BeanInstance.class, "x");
/*  184: 371 */     this.m_Properties.addAllowed(BeanInstance.class, "y");
/*  185: 372 */     this.m_Properties.addAllowed(BeanInstance.class, "bean");
/*  186: 373 */     this.m_Properties.addAllowed(weka.gui.beans.Saver.class, "wrappedAlgorithm");
/*  187: 374 */     this.m_Properties.addAllowed(weka.gui.beans.Loader.class, "wrappedAlgorithm");
/*  188: 375 */     this.m_Properties.addAllowed(weka.gui.beans.Saver.class, "relationNameForFilename");
/*  189: 377 */     if (getDataType() == 0) {
/*  190: 378 */       this.m_Properties.addAllowed(weka.gui.beans.Loader.class, "beanContext");
/*  191:     */     } else {
/*  192: 380 */       this.m_Properties.addIgnored(weka.gui.beans.Loader.class, "beanContext");
/*  193:     */     }
/*  194: 384 */     this.m_Properties.addAllowed(weka.gui.beans.Filter.class, "filter");
/*  195: 385 */     this.m_Properties.addAllowed(weka.gui.beans.Associator.class, "associator");
/*  196: 386 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "wrappedAlgorithm");
/*  197:     */     
/*  198: 388 */     this.m_Properties.addAllowed(weka.gui.beans.Clusterer.class, "wrappedAlgorithm");
/*  199: 389 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "executionSlots");
/*  200: 390 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "blockOnLastFold");
/*  201: 391 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "resetIncrementalClassifier");
/*  202:     */     
/*  203: 393 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "updateIncrementalClassifier");
/*  204:     */     
/*  205: 395 */     this.m_Properties.addAllowed(weka.gui.beans.Classifier.class, "loadClassifierFileName");
/*  206:     */     
/*  207:     */ 
/*  208: 398 */     this.m_Properties.addAllowed(weka.classifiers.Classifier.class, "debug");
/*  209: 399 */     this.m_Properties.addAllowed(weka.classifiers.Classifier.class, "options");
/*  210: 400 */     this.m_Properties.addAllowed(weka.associations.Associator.class, "options");
/*  211: 401 */     this.m_Properties.addAllowed(weka.clusterers.Clusterer.class, "options");
/*  212: 402 */     this.m_Properties.addAllowed(weka.filters.Filter.class, "options");
/*  213: 403 */     this.m_Properties.addAllowed(weka.core.converters.Saver.class, "options");
/*  214: 404 */     this.m_Properties.addAllowed(weka.core.converters.Loader.class, "options");
/*  215:     */     
/*  216: 406 */     this.m_Properties.addAllowed(DatabaseSaver.class, "options");
/*  217:     */     
/*  218: 408 */     this.m_Properties.addAllowed(DatabaseLoader.class, "options");
/*  219:     */     
/*  220: 410 */     this.m_Properties.addAllowed(TextDirectoryLoader.class, "options");
/*  221:     */     
/*  222:     */ 
/*  223:     */ 
/*  224:     */ 
/*  225: 415 */     this.m_Properties.addAllowed(SplitEvaluator.class, "options");
/*  226:     */     
/*  227:     */ 
/*  228: 418 */     this.m_Properties.addAllowed(ResultProducer.class, "options");
/*  229:     */     
/*  230:     */ 
/*  231: 421 */     this.m_CustomMethods.register(this, Color.class, "Color");
/*  232: 422 */     this.m_CustomMethods.register(this, Dimension.class, "Dimension");
/*  233: 423 */     this.m_CustomMethods.register(this, Font.class, "Font");
/*  234: 424 */     this.m_CustomMethods.register(this, Point.class, "Point");
/*  235: 425 */     this.m_CustomMethods.register(this, ColorUIResource.class, "ColorUIResource");
/*  236: 426 */     this.m_CustomMethods.register(this, FontUIResource.class, "FontUIResource");
/*  237:     */     
/*  238: 428 */     this.m_CustomMethods.register(this, BeanInstance.class, "BeanInstance");
/*  239:     */     
/*  240: 430 */     this.m_CustomMethods.register(this, BeanConnection.class, "BeanConnection");
/*  241:     */     
/*  242: 432 */     this.m_CustomMethods.register(this, BeanVisual.class, "BeanVisual");
/*  243:     */     
/*  244: 434 */     this.m_CustomMethods.register(this, weka.gui.beans.Saver.class, "BeanSaver");
/*  245: 435 */     this.m_CustomMethods.register(this, MetaBean.class, "MetaBean");
/*  246:     */     
/*  247: 437 */     Vector<String> classnames = ConverterUtils.getFileLoaders();
/*  248: 438 */     for (int i = 0; i < classnames.size(); i++) {
/*  249: 439 */       this.m_CustomMethods.register(this, Class.forName((String)classnames.get(i)), "Loader");
/*  250:     */     }
/*  251: 442 */     classnames = ConverterUtils.getFileSavers();
/*  252: 443 */     for (i = 0; i < classnames.size(); i++) {
/*  253: 444 */       this.m_CustomMethods.register(this, Class.forName((String)classnames.get(i)), "Saver");
/*  254:     */     }
/*  255: 448 */     this.m_BeanInstances = null;
/*  256: 449 */     this.m_BeanInstancesID = null;
/*  257: 450 */     this.m_CurrentMetaBean = null;
/*  258: 451 */     this.m_IgnoreBeanConnections = true;
/*  259: 452 */     this.m_BeanConnectionRelation = null;
/*  260:     */   }
/*  261:     */   
/*  262:     */   protected void addBeanInstances(Vector<Object> list)
/*  263:     */   {
/*  264: 465 */     for (int i = 0; i < list.size(); i++) {
/*  265: 466 */       if ((list.get(i) instanceof BeanInstance))
/*  266:     */       {
/*  267: 467 */         BeanInstance beaninst = (BeanInstance)list.get(i);
/*  268:     */         
/*  269: 469 */         this.m_BeanInstancesID.add(new Integer(this.m_BeanInstances.size()));
/*  270: 470 */         this.m_BeanInstances.add(beaninst);
/*  271: 472 */         if ((beaninst.getBean() instanceof MetaBean)) {
/*  272: 473 */           addBeanInstances(((MetaBean)beaninst.getBean()).getBeansInSubFlow());
/*  273:     */         }
/*  274:     */       }
/*  275: 475 */       else if ((list.get(i) instanceof MetaBean))
/*  276:     */       {
/*  277: 476 */         addBeanInstances(((MetaBean)list.get(i)).getBeansInSubFlow());
/*  278:     */       }
/*  279:     */       else
/*  280:     */       {
/*  281: 478 */         System.out.println("addBeanInstances does not support Vectors of class '" + list.get(i) + "'!");
/*  282:     */       }
/*  283:     */     }
/*  284:     */   }
/*  285:     */   
/*  286:     */   protected Object writePreProcess(Object o)
/*  287:     */     throws Exception
/*  288:     */   {
/*  289: 496 */     o = super.writePreProcess(o);
/*  290:     */     
/*  291:     */ 
/*  292: 499 */     this.m_BeanInstances = new Vector();
/*  293: 500 */     this.m_BeanInstancesID = new Vector();
/*  294: 502 */     switch (getDataType())
/*  295:     */     {
/*  296:     */     case 0: 
/*  297: 504 */       addBeanInstances(BeanInstance.getBeanInstances(new Integer[] { Integer.valueOf(this.m_vectorIndex) }));
/*  298: 505 */       break;
/*  299:     */     case 1: 
/*  300: 508 */       addBeanInstances((Vector)o);
/*  301: 509 */       break;
/*  302:     */     default: 
/*  303: 512 */       System.out.println("writePreProcess: data type '" + getDataType() + "' is not recognized!");
/*  304:     */     }
/*  305: 517 */     return o;
/*  306:     */   }
/*  307:     */   
/*  308:     */   protected void writePostProcess(Object o)
/*  309:     */     throws Exception
/*  310:     */   {
/*  311: 540 */     if (getDataType() == 0)
/*  312:     */     {
/*  313: 541 */       Element root = this.m_Document.getDocument().getDocumentElement();
/*  314: 542 */       Element conns = (Element)root.getChildNodes().item(1);
/*  315: 543 */       NodeList list = conns.getChildNodes();
/*  316: 544 */       for (int i = 0; i < list.getLength(); i++)
/*  317:     */       {
/*  318: 545 */         Element child = (Element)list.item(i);
/*  319: 546 */         child.setAttribute("name", "" + i);
/*  320:     */       }
/*  321:     */     }
/*  322:     */   }
/*  323:     */   
/*  324:     */   protected Document readPreProcess(Document document)
/*  325:     */     throws Exception
/*  326:     */   {
/*  327: 573 */     this.m_BeanInstances = new Vector();
/*  328: 574 */     this.m_BeanInstancesID = new Vector();
/*  329:     */     
/*  330:     */ 
/*  331: 577 */     NodeList list = document.getElementsByTagName("*");
/*  332: 578 */     String clsName = BeanInstance.class.getName();
/*  333: 579 */     for (int i = 0; i < list.getLength(); i++)
/*  334:     */     {
/*  335: 580 */       Element node = (Element)list.item(i);
/*  336: 583 */       if (node.getAttribute("class").equals(clsName))
/*  337:     */       {
/*  338: 584 */         Vector<Element> children = XMLDocument.getChildTags(node);
/*  339: 585 */         int id = this.m_BeanInstancesID.size();
/*  340: 588 */         for (int n = 0; n < children.size(); n++)
/*  341:     */         {
/*  342: 589 */           Element child = (Element)children.get(n);
/*  343: 590 */           if (child.getAttribute("name").equals("id")) {
/*  344: 591 */             id = readIntFromXML(child);
/*  345:     */           }
/*  346:     */         }
/*  347: 595 */         this.m_BeanInstancesID.add(new Integer(id));
/*  348:     */       }
/*  349:     */     }
/*  350: 599 */     this.m_BeanInstances.setSize(this.m_BeanInstancesID.size());
/*  351:     */     
/*  352:     */ 
/*  353: 602 */     this.m_CurrentMetaBean = null;
/*  354:     */     
/*  355:     */ 
/*  356: 605 */     this.m_IgnoreBeanConnections = true;
/*  357:     */     
/*  358:     */ 
/*  359: 608 */     this.m_BeanConnectionRelation = new Hashtable();
/*  360:     */     
/*  361: 610 */     return document;
/*  362:     */   }
/*  363:     */   
/*  364:     */   protected void setBeanConnection(BeanConnection conn, Vector<BeanConnection> list)
/*  365:     */   {
/*  366: 626 */     boolean added = false;
/*  367: 627 */     for (int i = 0; i < list.size(); i++) {
/*  368: 628 */       if (list.get(i) == null)
/*  369:     */       {
/*  370: 629 */         list.set(i, conn);
/*  371: 630 */         added = true;
/*  372: 631 */         break;
/*  373:     */       }
/*  374:     */     }
/*  375: 635 */     if (!added) {
/*  376: 636 */       list.add(conn);
/*  377:     */     }
/*  378:     */   }
/*  379:     */   
/*  380:     */   protected BeanConnection createBeanConnection(int sourcePos, int targetPos, String event, boolean hidden)
/*  381:     */     throws Exception
/*  382:     */   {
/*  383: 659 */     BeanConnection result = null;
/*  384: 662 */     if ((sourcePos == -1) || (targetPos == -1)) {
/*  385: 663 */       return result;
/*  386:     */     }
/*  387: 666 */     BeanInstance instSource = (BeanInstance)this.m_BeanInstances.get(sourcePos);
/*  388: 667 */     BeanInstance instTarget = (BeanInstance)this.m_BeanInstances.get(targetPos);
/*  389:     */     
/*  390: 669 */     BeanInfo compInfo = Introspector.getBeanInfo(((BeanInstance)this.m_BeanInstances.get(sourcePos)).getBean().getClass());
/*  391:     */     
/*  392: 671 */     EventSetDescriptor[] esds = compInfo.getEventSetDescriptors();
/*  393: 673 */     for (int i = 0; i < esds.length; i++) {
/*  394: 674 */       if (esds[i].getName().equals(event))
/*  395:     */       {
/*  396: 675 */         result = new BeanConnection(instSource, instTarget, esds[i], new Integer[] { Integer.valueOf(this.m_vectorIndex) });
/*  397:     */         
/*  398: 677 */         result.setHidden(hidden);
/*  399: 678 */         break;
/*  400:     */       }
/*  401:     */     }
/*  402: 682 */     return result;
/*  403:     */   }
/*  404:     */   
/*  405:     */   protected void rebuildBeanConnections(Vector<Vector<?>> deserialized, Object key)
/*  406:     */     throws Exception
/*  407:     */   {
/*  408: 708 */     Vector<String> conns = (Vector)this.m_BeanConnectionRelation.get(key);
/*  409: 711 */     if (conns == null) {
/*  410: 712 */       return;
/*  411:     */     }
/*  412: 715 */     for (int n = 0; n < conns.size(); n++)
/*  413:     */     {
/*  414: 716 */       StringTokenizer tok = new StringTokenizer(((String)conns.get(n)).toString(), ",");
/*  415: 717 */       BeanConnection conn = null;
/*  416: 718 */       int sourcePos = Integer.parseInt(tok.nextToken());
/*  417: 719 */       int targetPos = Integer.parseInt(tok.nextToken());
/*  418: 720 */       String event = tok.nextToken();
/*  419: 721 */       boolean hidden = stringToBoolean(tok.nextToken());
/*  420: 725 */       if ((!(key instanceof MetaBean)) || (getDataType() == 1))
/*  421:     */       {
/*  422: 727 */         conn = createBeanConnection(sourcePos, targetPos, event, hidden);
/*  423:     */       }
/*  424:     */       else
/*  425:     */       {
/*  426: 731 */         Vector<BeanConnection> beanconns = BeanConnection.getConnections(new Integer[] { Integer.valueOf(this.m_vectorIndex) });
/*  427: 733 */         for (int i = 0; i < beanconns.size(); i++)
/*  428:     */         {
/*  429: 734 */           conn = (BeanConnection)beanconns.get(i);
/*  430: 735 */           if ((conn.getSource() == this.m_BeanInstances.get(sourcePos)) && (conn.getTarget() == this.m_BeanInstances.get(targetPos)) && (conn.getEventName().equals(event))) {
/*  431:     */             break;
/*  432:     */           }
/*  433: 740 */           conn = null;
/*  434:     */         }
/*  435:     */       }
/*  436: 745 */       if ((key instanceof MetaBean)) {
/*  437: 746 */         setBeanConnection(conn, ((MetaBean)key).getAssociatedConnections());
/*  438:     */       } else {
/*  439: 748 */         setBeanConnection(conn, (Vector)deserialized.get(1));
/*  440:     */       }
/*  441:     */     }
/*  442:     */   }
/*  443:     */   
/*  444:     */   protected void removeUserToolBarBeans(Vector<?> metabeans)
/*  445:     */   {
/*  446: 767 */     for (int i = 0; i < metabeans.size(); i++)
/*  447:     */     {
/*  448: 768 */       MetaBean meta = (MetaBean)metabeans.get(i);
/*  449: 769 */       Vector<Object> subflow = meta.getSubFlow();
/*  450: 771 */       for (int n = 0; n < subflow.size(); n++)
/*  451:     */       {
/*  452: 772 */         BeanInstance beaninst = (BeanInstance)subflow.get(n);
/*  453: 773 */         beaninst.removeBean(this.m_BeanLayout, new Integer[0]);
/*  454:     */       }
/*  455:     */     }
/*  456:     */   }
/*  457:     */   
/*  458:     */   protected Object readPostProcess(Object o)
/*  459:     */     throws Exception
/*  460:     */   {
/*  461: 793 */     Vector<Vector<?>> deserialized = (Vector)super.readPostProcess(o);
/*  462:     */     
/*  463:     */ 
/*  464: 796 */     rebuildBeanConnections(deserialized, "regular_connection");
/*  465:     */     
/*  466:     */ 
/*  467: 799 */     Enumeration<Object> enm = this.m_BeanConnectionRelation.keys();
/*  468: 800 */     while (enm.hasMoreElements())
/*  469:     */     {
/*  470: 801 */       Object key = enm.nextElement();
/*  471: 804 */       if ((key instanceof MetaBean)) {
/*  472: 808 */         rebuildBeanConnections(deserialized, key);
/*  473:     */       }
/*  474:     */     }
/*  475: 812 */     if (getDataType() == 1) {
/*  476: 813 */       removeUserToolBarBeans(deserialized);
/*  477:     */     }
/*  478: 816 */     return deserialized;
/*  479:     */   }
/*  480:     */   
/*  481:     */   protected Vector<String> getBeanConnectionRelation(MetaBean meta)
/*  482:     */   {
/*  483:     */     Object key;
/*  484:     */     Object key;
/*  485: 832 */     if (meta == null) {
/*  486: 833 */       key = "regular_connection";
/*  487:     */     } else {
/*  488: 835 */       key = meta;
/*  489:     */     }
/*  490: 839 */     if (!this.m_BeanConnectionRelation.containsKey(key)) {
/*  491: 840 */       this.m_BeanConnectionRelation.put(key, new Vector());
/*  492:     */     }
/*  493: 843 */     Vector<String> result = (Vector)this.m_BeanConnectionRelation.get(key);
/*  494:     */     
/*  495: 845 */     return result;
/*  496:     */   }
/*  497:     */   
/*  498:     */   protected void addBeanConnectionRelation(MetaBean meta, String connection)
/*  499:     */   {
/*  500: 860 */     Vector<String> relations = getBeanConnectionRelation(meta);
/*  501:     */     
/*  502:     */ 
/*  503: 863 */     relations.add(connection);
/*  504:     */     Object key;
/*  505:     */     Object key;
/*  506: 866 */     if (meta == null) {
/*  507: 867 */       key = "regular_connection";
/*  508:     */     } else {
/*  509: 869 */       key = meta;
/*  510:     */     }
/*  511: 871 */     this.m_BeanConnectionRelation.put(key, relations);
/*  512:     */   }
/*  513:     */   
/*  514:     */   public Element writeColor(Element parent, Object o, String name)
/*  515:     */     throws Exception
/*  516:     */   {
/*  517: 891 */     if (DEBUG) {
/*  518: 892 */       trace(new Throwable(), name);
/*  519:     */     }
/*  520: 895 */     this.m_CurrentNode = parent;
/*  521:     */     
/*  522: 897 */     Color color = (Color)o;
/*  523: 898 */     Element node = addElement(parent, name, color.getClass().getName(), false);
/*  524:     */     
/*  525: 900 */     writeIntToXML(node, color.getRed(), "red");
/*  526: 901 */     writeIntToXML(node, color.getGreen(), "green");
/*  527: 902 */     writeIntToXML(node, color.getBlue(), "blue");
/*  528:     */     
/*  529: 904 */     return node;
/*  530:     */   }
/*  531:     */   
/*  532:     */   public Object readColor(Element node)
/*  533:     */     throws Exception
/*  534:     */   {
/*  535: 925 */     if (DEBUG) {
/*  536: 926 */       trace(new Throwable(), node.getAttribute("name"));
/*  537:     */     }
/*  538: 929 */     this.m_CurrentNode = node;
/*  539:     */     
/*  540: 931 */     Object result = null;
/*  541: 932 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  542: 933 */     int red = 0;
/*  543: 934 */     int green = 0;
/*  544: 935 */     int blue = 0;
/*  545: 937 */     for (int i = 0; i < children.size(); i++)
/*  546:     */     {
/*  547: 938 */       Element child = (Element)children.get(i);
/*  548: 939 */       String name = child.getAttribute("name");
/*  549: 941 */       if (name.equals("red")) {
/*  550: 942 */         red = readIntFromXML(child);
/*  551: 943 */       } else if (name.equals("green")) {
/*  552: 944 */         green = readIntFromXML(child);
/*  553: 945 */       } else if (name.equals("blue")) {
/*  554: 946 */         blue = readIntFromXML(child);
/*  555:     */       } else {
/*  556: 948 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  557:     */       }
/*  558:     */     }
/*  559: 954 */     result = new Color(red, green, blue);
/*  560:     */     
/*  561: 956 */     return result;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public Element writeDimension(Element parent, Object o, String name)
/*  565:     */     throws Exception
/*  566:     */   {
/*  567: 976 */     if (DEBUG) {
/*  568: 977 */       trace(new Throwable(), name);
/*  569:     */     }
/*  570: 980 */     this.m_CurrentNode = parent;
/*  571:     */     
/*  572: 982 */     Dimension dim = (Dimension)o;
/*  573: 983 */     Element node = addElement(parent, name, dim.getClass().getName(), false);
/*  574:     */     
/*  575: 985 */     writeDoubleToXML(node, dim.getWidth(), "width");
/*  576: 986 */     writeDoubleToXML(node, dim.getHeight(), "height");
/*  577:     */     
/*  578: 988 */     return node;
/*  579:     */   }
/*  580:     */   
/*  581:     */   public Object readDimension(Element node)
/*  582:     */     throws Exception
/*  583:     */   {
/*  584:1008 */     if (DEBUG) {
/*  585:1009 */       trace(new Throwable(), node.getAttribute("name"));
/*  586:     */     }
/*  587:1012 */     this.m_CurrentNode = node;
/*  588:     */     
/*  589:1014 */     Object result = null;
/*  590:1015 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  591:1016 */     double width = 0.0D;
/*  592:1017 */     double height = 0.0D;
/*  593:1019 */     for (int i = 0; i < children.size(); i++)
/*  594:     */     {
/*  595:1020 */       Element child = (Element)children.get(i);
/*  596:1021 */       String name = child.getAttribute("name");
/*  597:1023 */       if (name.equals("width")) {
/*  598:1024 */         width = readDoubleFromXML(child);
/*  599:1025 */       } else if (name.equals("height")) {
/*  600:1026 */         height = readDoubleFromXML(child);
/*  601:     */       } else {
/*  602:1028 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  603:     */       }
/*  604:     */     }
/*  605:1034 */     result = new Dimension();
/*  606:1035 */     ((Dimension)result).setSize(width, height);
/*  607:     */     
/*  608:1037 */     return result;
/*  609:     */   }
/*  610:     */   
/*  611:     */   public Element writeFont(Element parent, Object o, String name)
/*  612:     */     throws Exception
/*  613:     */   {
/*  614:1057 */     if (DEBUG) {
/*  615:1058 */       trace(new Throwable(), name);
/*  616:     */     }
/*  617:1061 */     this.m_CurrentNode = parent;
/*  618:     */     
/*  619:1063 */     Font font = (Font)o;
/*  620:1064 */     Element node = addElement(parent, name, font.getClass().getName(), false);
/*  621:     */     
/*  622:1066 */     invokeWriteToXML(node, font.getName(), "name");
/*  623:1067 */     writeIntToXML(node, font.getStyle(), "style");
/*  624:1068 */     writeIntToXML(node, font.getSize(), "size");
/*  625:     */     
/*  626:1070 */     return node;
/*  627:     */   }
/*  628:     */   
/*  629:     */   public Object readFont(Element node)
/*  630:     */     throws Exception
/*  631:     */   {
/*  632:1091 */     if (DEBUG) {
/*  633:1092 */       trace(new Throwable(), node.getAttribute("name"));
/*  634:     */     }
/*  635:1095 */     this.m_CurrentNode = node;
/*  636:     */     
/*  637:1097 */     Object result = null;
/*  638:1098 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  639:1099 */     String fontname = "";
/*  640:1100 */     int style = 0;
/*  641:1101 */     int size = 0;
/*  642:1103 */     for (int i = 0; i < children.size(); i++)
/*  643:     */     {
/*  644:1104 */       Element child = (Element)children.get(i);
/*  645:1105 */       String name = child.getAttribute("name");
/*  646:1107 */       if (name.equals("name")) {
/*  647:1108 */         name = (String)invokeReadFromXML(child);
/*  648:1109 */       } else if (name.equals("style")) {
/*  649:1110 */         style = readIntFromXML(child);
/*  650:1111 */       } else if (name.equals("size")) {
/*  651:1112 */         size = readIntFromXML(child);
/*  652:     */       } else {
/*  653:1114 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  654:     */       }
/*  655:     */     }
/*  656:1120 */     result = new Font(fontname, style, size);
/*  657:     */     
/*  658:1122 */     return result;
/*  659:     */   }
/*  660:     */   
/*  661:     */   public Element writePoint(Element parent, Object o, String name)
/*  662:     */     throws Exception
/*  663:     */   {
/*  664:1142 */     if (DEBUG) {
/*  665:1143 */       trace(new Throwable(), name);
/*  666:     */     }
/*  667:1146 */     this.m_CurrentNode = parent;
/*  668:     */     
/*  669:1148 */     Point p = (Point)o;
/*  670:1149 */     Element node = addElement(parent, name, p.getClass().getName(), false);
/*  671:     */     
/*  672:1151 */     writeDoubleToXML(node, p.getX(), "x");
/*  673:1152 */     writeDoubleToXML(node, p.getY(), "y");
/*  674:     */     
/*  675:1154 */     return node;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public Object readPoint(Element node)
/*  679:     */     throws Exception
/*  680:     */   {
/*  681:1174 */     if (DEBUG) {
/*  682:1175 */       trace(new Throwable(), node.getAttribute("name"));
/*  683:     */     }
/*  684:1178 */     this.m_CurrentNode = node;
/*  685:     */     
/*  686:1180 */     Object result = null;
/*  687:1181 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  688:1182 */     double x = 0.0D;
/*  689:1183 */     double y = 0.0D;
/*  690:1185 */     for (int i = 0; i < children.size(); i++)
/*  691:     */     {
/*  692:1186 */       Element child = (Element)children.get(i);
/*  693:1187 */       String name = child.getAttribute("name");
/*  694:1189 */       if (name.equals("x")) {
/*  695:1190 */         x = readDoubleFromXML(child);
/*  696:1191 */       } else if (name.equals("y")) {
/*  697:1192 */         y = readDoubleFromXML(child);
/*  698:     */       } else {
/*  699:1194 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  700:     */       }
/*  701:     */     }
/*  702:1200 */     result = new Point();
/*  703:1201 */     ((Point)result).setLocation(x, y);
/*  704:     */     
/*  705:1203 */     return result;
/*  706:     */   }
/*  707:     */   
/*  708:     */   public Element writeColorUIResource(Element parent, Object o, String name)
/*  709:     */     throws Exception
/*  710:     */   {
/*  711:1223 */     if (DEBUG) {
/*  712:1224 */       trace(new Throwable(), name);
/*  713:     */     }
/*  714:1227 */     this.m_CurrentNode = parent;
/*  715:     */     
/*  716:1229 */     ColorUIResource resource = (ColorUIResource)o;
/*  717:1230 */     Element node = addElement(parent, name, resource.getClass().getName(), false);
/*  718:1231 */     invokeWriteToXML(node, new Color(resource.getRGB()), "color");
/*  719:     */     
/*  720:1233 */     return node;
/*  721:     */   }
/*  722:     */   
/*  723:     */   public Object readColorUIResource(Element node)
/*  724:     */     throws Exception
/*  725:     */   {
/*  726:1252 */     if (DEBUG) {
/*  727:1253 */       trace(new Throwable(), node.getAttribute("name"));
/*  728:     */     }
/*  729:1256 */     this.m_CurrentNode = node;
/*  730:     */     
/*  731:1258 */     Object result = null;
/*  732:1259 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  733:1260 */     Color color = null;
/*  734:1262 */     for (int i = 0; i < children.size(); i++)
/*  735:     */     {
/*  736:1263 */       Element child = (Element)children.get(i);
/*  737:1264 */       String name = child.getAttribute("name");
/*  738:1266 */       if (name.equals("color")) {
/*  739:1267 */         color = (Color)invokeReadFromXML(child);
/*  740:     */       } else {
/*  741:1269 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  742:     */       }
/*  743:     */     }
/*  744:1275 */     result = new ColorUIResource(color);
/*  745:     */     
/*  746:1277 */     return result;
/*  747:     */   }
/*  748:     */   
/*  749:     */   public Element writeFontUIResource(Element parent, Object o, String name)
/*  750:     */     throws Exception
/*  751:     */   {
/*  752:1297 */     if (DEBUG) {
/*  753:1298 */       trace(new Throwable(), name);
/*  754:     */     }
/*  755:1301 */     this.m_CurrentNode = parent;
/*  756:     */     
/*  757:1303 */     FontUIResource resource = (FontUIResource)o;
/*  758:1304 */     Element node = addElement(parent, name, resource.getClass().getName(), false);
/*  759:1305 */     invokeWriteToXML(node, new Font(resource.getName(), resource.getStyle(), resource.getSize()), "color");
/*  760:     */     
/*  761:     */ 
/*  762:1308 */     return node;
/*  763:     */   }
/*  764:     */   
/*  765:     */   public Object readFontUIResource(Element node)
/*  766:     */     throws Exception
/*  767:     */   {
/*  768:1327 */     if (DEBUG) {
/*  769:1328 */       trace(new Throwable(), node.getAttribute("name"));
/*  770:     */     }
/*  771:1331 */     this.m_CurrentNode = node;
/*  772:     */     
/*  773:1333 */     Object result = null;
/*  774:1334 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  775:1335 */     Font font = null;
/*  776:1337 */     for (int i = 0; i < children.size(); i++)
/*  777:     */     {
/*  778:1338 */       Element child = (Element)children.get(i);
/*  779:1339 */       String name = child.getAttribute("name");
/*  780:1341 */       if (name.equals("font")) {
/*  781:1342 */         font = (Font)invokeReadFromXML(child);
/*  782:     */       } else {
/*  783:1344 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  784:     */       }
/*  785:     */     }
/*  786:1350 */     result = new FontUIResource(font);
/*  787:     */     
/*  788:1352 */     return result;
/*  789:     */   }
/*  790:     */   
/*  791:     */   public Element writeBeanInstance(Element parent, Object o, String name)
/*  792:     */     throws Exception
/*  793:     */   {
/*  794:1372 */     if (DEBUG) {
/*  795:1373 */       trace(new Throwable(), name);
/*  796:     */     }
/*  797:1376 */     this.m_CurrentNode = parent;
/*  798:     */     
/*  799:1378 */     BeanInstance beaninst = (BeanInstance)o;
/*  800:1379 */     Element node = addElement(parent, name, beaninst.getClass().getName(), false);
/*  801:     */     
/*  802:1381 */     writeIntToXML(node, this.m_BeanInstances.indexOf(beaninst), "id");
/*  803:1382 */     int w = beaninst.getWidth() / 2;
/*  804:1383 */     int h = beaninst.getHeight() / 2;
/*  805:1388 */     if ((w == 0) && (h == 0))
/*  806:     */     {
/*  807:1389 */       w = 28;
/*  808:1390 */       h = 28;
/*  809:     */     }
/*  810:1392 */     writeIntToXML(node, beaninst.getX() + w, "x");
/*  811:     */     
/*  812:1394 */     writeIntToXML(node, beaninst.getY() + h, "y");
/*  813:1396 */     if ((beaninst.getBean() instanceof BeanCommon))
/*  814:     */     {
/*  815:1398 */       String custName = ((BeanCommon)beaninst.getBean()).getCustomName();
/*  816:1399 */       invokeWriteToXML(node, custName, "custom_name");
/*  817:     */     }
/*  818:1401 */     invokeWriteToXML(node, beaninst.getBean(), "bean");
/*  819:     */     
/*  820:1403 */     return node;
/*  821:     */   }
/*  822:     */   
/*  823:     */   public Object readBeanInstance(Element node)
/*  824:     */     throws Exception
/*  825:     */   {
/*  826:1427 */     if (DEBUG) {
/*  827:1428 */       trace(new Throwable(), node.getAttribute("name"));
/*  828:     */     }
/*  829:1431 */     this.m_CurrentNode = node;
/*  830:     */     
/*  831:1433 */     Object result = null;
/*  832:1434 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  833:1435 */     int id = -1;
/*  834:1436 */     int x = 0;
/*  835:1437 */     int y = 0;
/*  836:1438 */     Object bean = null;
/*  837:1439 */     String customName = null;
/*  838:1441 */     for (int i = 0; i < children.size(); i++)
/*  839:     */     {
/*  840:1442 */       Element child = (Element)children.get(i);
/*  841:1443 */       String name = child.getAttribute("name");
/*  842:1445 */       if (name.equals("id")) {
/*  843:1446 */         id = readIntFromXML(child);
/*  844:1447 */       } else if (name.equals("x")) {
/*  845:1448 */         x = readIntFromXML(child);
/*  846:1449 */       } else if (name.equals("y")) {
/*  847:1450 */         y = readIntFromXML(child);
/*  848:1451 */       } else if (name.equals("custom_name")) {
/*  849:1452 */         customName = (String)invokeReadFromXML(child);
/*  850:1453 */       } else if (name.equals("bean")) {
/*  851:1454 */         bean = invokeReadFromXML(child);
/*  852:     */       } else {
/*  853:1456 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  854:     */       }
/*  855:     */     }
/*  856:1462 */     result = new BeanInstance(this.m_BeanLayout, bean, x, y, new Integer[] { Integer.valueOf(this.m_vectorIndex) });
/*  857:1463 */     BeanInstance beaninst = (BeanInstance)result;
/*  858:1466 */     if ((beaninst.getBean() instanceof Visible))
/*  859:     */     {
/*  860:1467 */       BeanVisual visual = ((Visible)beaninst.getBean()).getVisual();
/*  861:1468 */       visual.setSize(visual.getPreferredSize());
/*  862:1469 */       if (visual.getParent() == null) {
/*  863:1470 */         ((JPanel)beaninst.getBean()).add(visual);
/*  864:     */       }
/*  865:     */     }
/*  866:1474 */     if (((beaninst.getBean() instanceof BeanCommon)) && (customName != null)) {
/*  867:1475 */       ((BeanCommon)beaninst.getBean()).setCustomName(customName);
/*  868:     */     }
/*  869:1479 */     if (id == -1) {
/*  870:1480 */       for (i = 0; i < this.m_BeanInstances.size(); i++) {
/*  871:1481 */         if (this.m_BeanInstances.get(i) == null)
/*  872:     */         {
/*  873:1482 */           id = ((Integer)this.m_BeanInstancesID.get(i)).intValue();
/*  874:1483 */           break;
/*  875:     */         }
/*  876:     */       }
/*  877:     */     }
/*  878:1488 */     i = this.m_BeanInstancesID.indexOf(new Integer(id));
/*  879:     */     
/*  880:     */ 
/*  881:1491 */     this.m_BeanInstances.set(i, result);
/*  882:     */     
/*  883:     */ 
/*  884:1494 */     this.m_CurrentMetaBean = null;
/*  885:     */     
/*  886:1496 */     return result;
/*  887:     */   }
/*  888:     */   
/*  889:     */   public Element writeBeanConnection(Element parent, Object o, String name)
/*  890:     */     throws Exception
/*  891:     */   {
/*  892:1520 */     if (DEBUG) {
/*  893:1521 */       trace(new Throwable(), name);
/*  894:     */     }
/*  895:1524 */     this.m_CurrentNode = parent;
/*  896:     */     
/*  897:1526 */     BeanConnection beanconn = (BeanConnection)o;
/*  898:1527 */     Element node = null;
/*  899:     */     
/*  900:     */ 
/*  901:1530 */     int sourcePos = this.m_BeanInstances.indexOf(beanconn.getSource());
/*  902:1531 */     int targetPos = this.m_BeanInstances.indexOf(beanconn.getTarget());
/*  903:     */     int target;
/*  904:     */     int source;
/*  905:     */     int target;
/*  906:1535 */     if ((sourcePos > -1) && (targetPos > -1))
/*  907:     */     {
/*  908:1536 */       int source = ((Integer)this.m_BeanInstancesID.get(sourcePos)).intValue();
/*  909:1537 */       target = ((Integer)this.m_BeanInstancesID.get(targetPos)).intValue();
/*  910:     */     }
/*  911:     */     else
/*  912:     */     {
/*  913:1539 */       source = -1;
/*  914:1540 */       target = -1;
/*  915:     */     }
/*  916:1544 */     if ((source > -1) && (target > -1))
/*  917:     */     {
/*  918:1545 */       node = addElement(parent, name, beanconn.getClass().getName(), false);
/*  919:     */       
/*  920:1547 */       writeIntToXML(node, source, "source_id");
/*  921:1548 */       writeIntToXML(node, target, "target_id");
/*  922:1549 */       invokeWriteToXML(node, beanconn.getEventName(), "eventname");
/*  923:1550 */       writeBooleanToXML(node, beanconn.isHidden(), "hidden");
/*  924:     */     }
/*  925:1553 */     return node;
/*  926:     */   }
/*  927:     */   
/*  928:     */   public Object readBeanConnection(Element node)
/*  929:     */     throws Exception
/*  930:     */   {
/*  931:1577 */     if (DEBUG) {
/*  932:1578 */       trace(new Throwable(), node.getAttribute("name"));
/*  933:     */     }
/*  934:1581 */     this.m_CurrentNode = node;
/*  935:     */     
/*  936:1583 */     Object result = null;
/*  937:1584 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  938:1585 */     int source = 0;
/*  939:1586 */     int target = 0;
/*  940:1587 */     String event = "";
/*  941:1588 */     boolean hidden = false;
/*  942:1590 */     for (int i = 0; i < children.size(); i++)
/*  943:     */     {
/*  944:1591 */       Element child = (Element)children.get(i);
/*  945:1592 */       String name = child.getAttribute("name");
/*  946:1594 */       if (name.equals("source_id")) {
/*  947:1595 */         source = readIntFromXML(child);
/*  948:1596 */       } else if (name.equals("target_id")) {
/*  949:1597 */         target = readIntFromXML(child);
/*  950:1598 */       } else if (name.equals("eventname")) {
/*  951:1599 */         event = (String)invokeReadFromXML(child);
/*  952:1600 */       } else if (name.equals("hidden")) {
/*  953:1601 */         hidden = readBooleanFromXML(child);
/*  954:     */       } else {
/*  955:1603 */         System.out.println("WARNING: '" + name + "' is not a recognized name for " + node.getAttribute("name") + "!");
/*  956:     */       }
/*  957:     */     }
/*  958:1610 */     int sourcePos = this.m_BeanInstancesID.indexOf(new Integer(source));
/*  959:1611 */     int targetPos = this.m_BeanInstancesID.indexOf(new Integer(target));
/*  960:1615 */     if (this.m_IgnoreBeanConnections)
/*  961:     */     {
/*  962:1616 */       addBeanConnectionRelation(this.m_CurrentMetaBean, sourcePos + "," + targetPos + "," + event + "," + hidden);
/*  963:     */       
/*  964:1618 */       return result;
/*  965:     */     }
/*  966:1622 */     result = createBeanConnection(sourcePos, targetPos, event, hidden);
/*  967:     */     
/*  968:1624 */     return result;
/*  969:     */   }
/*  970:     */   
/*  971:     */   public Element writeBeanLoader(Element parent, Object o, String name)
/*  972:     */     throws Exception
/*  973:     */   {
/*  974:1644 */     if (DEBUG) {
/*  975:1645 */       trace(new Throwable(), name);
/*  976:     */     }
/*  977:1648 */     this.m_CurrentNode = parent;
/*  978:     */     
/*  979:1650 */     weka.gui.beans.Loader loader = (weka.gui.beans.Loader)o;
/*  980:1651 */     Element node = addElement(parent, name, loader.getClass().getName(), false);
/*  981:     */     
/*  982:1653 */     invokeWriteToXML(node, loader.getLoader(), "wrappedAlgorithm");
/*  983:1654 */     invokeWriteToXML(node, loader.getBeanContext(), "beanContext");
/*  984:     */     
/*  985:1656 */     return node;
/*  986:     */   }
/*  987:     */   
/*  988:     */   public Element writeBeanSaver(Element parent, Object o, String name)
/*  989:     */     throws Exception
/*  990:     */   {
/*  991:1676 */     if (DEBUG) {
/*  992:1677 */       trace(new Throwable(), name);
/*  993:     */     }
/*  994:1680 */     this.m_CurrentNode = parent;
/*  995:     */     
/*  996:1682 */     weka.gui.beans.Saver saver = (weka.gui.beans.Saver)o;
/*  997:1683 */     Element node = addElement(parent, name, saver.getClass().getName(), false);
/*  998:1684 */     invokeWriteToXML(node, Boolean.valueOf(saver.getRelationNameForFilename()), "relationNameForFilename");
/*  999:     */     
/* 1000:     */ 
/* 1001:1687 */     invokeWriteToXML(node, saver.getSaverTemplate(), "wrappedAlgorithm");
/* 1002:     */     
/* 1003:1689 */     return node;
/* 1004:     */   }
/* 1005:     */   
/* 1006:     */   public Element writeLoader(Element parent, Object o, String name)
/* 1007:     */     throws Exception
/* 1008:     */   {
/* 1009:1711 */     if (DEBUG) {
/* 1010:1712 */       trace(new Throwable(), name);
/* 1011:     */     }
/* 1012:1715 */     this.m_CurrentNode = parent;
/* 1013:     */     
/* 1014:1717 */     weka.core.converters.Loader loader = (weka.core.converters.Loader)o;
/* 1015:1718 */     Element node = addElement(parent, name, loader.getClass().getName(), false);
/* 1016:1719 */     boolean known = true;
/* 1017:1720 */     File file = null;
/* 1018:1723 */     if ((loader instanceof AbstractFileLoader)) {
/* 1019:1724 */       file = ((AbstractFileLoader)loader).retrieveFile();
/* 1020:     */     } else {
/* 1021:1726 */       known = false;
/* 1022:     */     }
/* 1023:1729 */     if (!known) {
/* 1024:1730 */       System.out.println("WARNING: unknown loader class '" + loader.getClass().getName() + "' - cannot retrieve file!");
/* 1025:     */     }
/* 1026:1734 */     Boolean relativeB = null;
/* 1027:1735 */     if ((loader instanceof FileSourcedConverter))
/* 1028:     */     {
/* 1029:1736 */       boolean relative = ((FileSourcedConverter)loader).getUseRelativePath();
/* 1030:     */       
/* 1031:1738 */       relativeB = new Boolean(relative);
/* 1032:     */     }
/* 1033:1742 */     if ((file == null) || (file.isDirectory()))
/* 1034:     */     {
/* 1035:1743 */       invokeWriteToXML(node, "", "file");
/* 1036:     */     }
/* 1037:     */     else
/* 1038:     */     {
/* 1039:1745 */       String withResourceSeparators = file.getPath().replace(File.pathSeparatorChar, '/');
/* 1040:     */       
/* 1041:1747 */       boolean notAbsolute = (((AbstractFileLoader)loader).getUseRelativePath()) || (((loader instanceof EnvironmentHandler)) && (Environment.containsEnvVariables(file.getPath()))) || (getClass().getClassLoader().getResource(withResourceSeparators) != null) || (!file.exists());
/* 1042:     */       
/* 1043:     */ 
/* 1044:     */ 
/* 1045:     */ 
/* 1046:     */ 
/* 1047:     */ 
/* 1048:1754 */       String path = notAbsolute ? file.getPath() : file.getAbsolutePath();
/* 1049:     */       
/* 1050:     */ 
/* 1051:     */ 
/* 1052:1758 */       path = path.replace('\\', '/');
/* 1053:1759 */       invokeWriteToXML(node, path, "file");
/* 1054:     */     }
/* 1055:1761 */     if (relativeB != null) {
/* 1056:1762 */       invokeWriteToXML(node, relativeB.toString(), "useRelativePath");
/* 1057:     */     }
/* 1058:1765 */     if ((loader instanceof OptionHandler))
/* 1059:     */     {
/* 1060:1766 */       String[] opts = ((OptionHandler)loader).getOptions();
/* 1061:1767 */       invokeWriteToXML(node, opts, "options");
/* 1062:     */     }
/* 1063:1770 */     return node;
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public Object readLoader(Element node)
/* 1067:     */     throws Exception
/* 1068:     */   {
/* 1069:1790 */     if (DEBUG) {
/* 1070:1791 */       trace(new Throwable(), node.getAttribute("name"));
/* 1071:     */     }
/* 1072:1794 */     this.m_CurrentNode = node;
/* 1073:     */     
/* 1074:1796 */     Object result = Class.forName(node.getAttribute("class")).newInstance();
/* 1075:1797 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 1076:1798 */     String file = "";
/* 1077:1799 */     Object relativeB = null;
/* 1078:1800 */     boolean relative = false;
/* 1079:1802 */     for (int i = 0; i < children.size(); i++)
/* 1080:     */     {
/* 1081:1803 */       Element child = (Element)children.get(i);
/* 1082:1804 */       String name = child.getAttribute("name");
/* 1083:1806 */       if (name.equals("file"))
/* 1084:     */       {
/* 1085:1807 */         file = (String)invokeReadFromXML(child);
/* 1086:     */       }
/* 1087:1808 */       else if (name.equals("useRelativePath"))
/* 1088:     */       {
/* 1089:1809 */         relativeB = readFromXML(child);
/* 1090:1810 */         if ((relativeB instanceof Boolean)) {
/* 1091:1811 */           relative = ((Boolean)relativeB).booleanValue();
/* 1092:     */         }
/* 1093:     */       }
/* 1094:     */       else
/* 1095:     */       {
/* 1096:1814 */         readFromXML(result, name, child);
/* 1097:     */       }
/* 1098:     */     }
/* 1099:1818 */     if ((result instanceof FileSourcedConverter)) {
/* 1100:1819 */       ((FileSourcedConverter)result).setUseRelativePath(relative);
/* 1101:     */     }
/* 1102:1823 */     if (file.equals("")) {
/* 1103:1824 */       file = null;
/* 1104:     */     }
/* 1105:1828 */     if (file != null)
/* 1106:     */     {
/* 1107:1829 */       String tempFile = file;
/* 1108:     */       
/* 1109:1831 */       boolean containsEnv = false;
/* 1110:1832 */       containsEnv = Environment.containsEnvVariables(file);
/* 1111:     */       
/* 1112:1834 */       File fl = new File(file);
/* 1113:1839 */       if ((containsEnv) || (fl.exists()) || (getClass().getClassLoader().getResource(file) != null)) {
/* 1114:1841 */         ((AbstractFileLoader)result).setSource(new File(file));
/* 1115:     */       } else {
/* 1116:1844 */         System.out.println("WARNING: The file '" + tempFile + "' does not exist!");
/* 1117:     */       }
/* 1118:     */     }
/* 1119:1849 */     return result;
/* 1120:     */   }
/* 1121:     */   
/* 1122:     */   public Element writeSaver(Element parent, Object o, String name)
/* 1123:     */     throws Exception
/* 1124:     */   {
/* 1125:1872 */     if (DEBUG) {
/* 1126:1873 */       trace(new Throwable(), name);
/* 1127:     */     }
/* 1128:1876 */     this.m_CurrentNode = parent;
/* 1129:     */     
/* 1130:1878 */     weka.core.converters.Saver saver = (weka.core.converters.Saver)o;
/* 1131:1879 */     Element node = addElement(parent, name, saver.getClass().getName(), false);
/* 1132:1880 */     boolean known = true;
/* 1133:1881 */     String prefix = "";
/* 1134:1882 */     String dir = "";
/* 1135:1885 */     if ((saver instanceof AbstractFileSaver))
/* 1136:     */     {
/* 1137:1886 */       ((AbstractFileSaver)saver).retrieveFile();
/* 1138:1887 */       prefix = ((AbstractFileSaver)saver).filePrefix();
/* 1139:1888 */       dir = ((AbstractFileSaver)saver).retrieveDir();
/* 1140:     */       
/* 1141:     */ 
/* 1142:     */ 
/* 1143:1892 */       dir = dir.replace('\\', '/');
/* 1144:     */     }
/* 1145:     */     else
/* 1146:     */     {
/* 1147:1894 */       known = false;
/* 1148:     */     }
/* 1149:1897 */     if (!known) {
/* 1150:1898 */       System.out.println("WARNING: unknown saver class '" + saver.getClass().getName() + "' - cannot retrieve file!");
/* 1151:     */     }
/* 1152:1902 */     Boolean relativeB = null;
/* 1153:1903 */     if ((saver instanceof FileSourcedConverter))
/* 1154:     */     {
/* 1155:1904 */       boolean relative = ((FileSourcedConverter)saver).getUseRelativePath();
/* 1156:     */       
/* 1157:1906 */       relativeB = new Boolean(relative);
/* 1158:     */     }
/* 1159:1910 */     invokeWriteToXML(node, "", "file");
/* 1160:1911 */     invokeWriteToXML(node, dir, "dir");
/* 1161:1912 */     invokeWriteToXML(node, prefix, "prefix");
/* 1162:1923 */     if (relativeB != null) {
/* 1163:1924 */       invokeWriteToXML(node, relativeB.toString(), "useRelativePath");
/* 1164:     */     }
/* 1165:1927 */     if ((saver instanceof OptionHandler))
/* 1166:     */     {
/* 1167:1928 */       String[] opts = ((OptionHandler)saver).getOptions();
/* 1168:1929 */       invokeWriteToXML(node, opts, "options");
/* 1169:     */     }
/* 1170:1932 */     return node;
/* 1171:     */   }
/* 1172:     */   
/* 1173:     */   public Object readSaver(Element node)
/* 1174:     */     throws Exception
/* 1175:     */   {
/* 1176:1953 */     if (DEBUG) {
/* 1177:1954 */       trace(new Throwable(), node.getAttribute("name"));
/* 1178:     */     }
/* 1179:1957 */     this.m_CurrentNode = node;
/* 1180:     */     
/* 1181:1959 */     Object result = Class.forName(node.getAttribute("class")).newInstance();
/* 1182:1960 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 1183:1961 */     String file = null;
/* 1184:1962 */     String dir = null;
/* 1185:1963 */     String prefix = null;
/* 1186:     */     
/* 1187:1965 */     Object relativeB = null;
/* 1188:1966 */     boolean relative = false;
/* 1189:1968 */     for (int i = 0; i < children.size(); i++)
/* 1190:     */     {
/* 1191:1969 */       Element child = (Element)children.get(i);
/* 1192:1970 */       String name = child.getAttribute("name");
/* 1193:1972 */       if (name.equals("file"))
/* 1194:     */       {
/* 1195:1973 */         file = (String)invokeReadFromXML(child);
/* 1196:     */       }
/* 1197:1974 */       else if (name.equals("dir"))
/* 1198:     */       {
/* 1199:1975 */         dir = (String)invokeReadFromXML(child);
/* 1200:     */       }
/* 1201:1976 */       else if (name.equals("prefix"))
/* 1202:     */       {
/* 1203:1977 */         prefix = (String)invokeReadFromXML(child);
/* 1204:     */       }
/* 1205:1978 */       else if (name.equals("useRelativePath"))
/* 1206:     */       {
/* 1207:1979 */         relativeB = readFromXML(child);
/* 1208:1980 */         if ((relativeB instanceof Boolean)) {
/* 1209:1981 */           relative = ((Boolean)relativeB).booleanValue();
/* 1210:     */         }
/* 1211:     */       }
/* 1212:     */       else
/* 1213:     */       {
/* 1214:1984 */         readFromXML(result, name, child);
/* 1215:     */       }
/* 1216:     */     }
/* 1217:1988 */     if ((file != null) && (file.length() == 0)) {
/* 1218:1989 */       file = null;
/* 1219:     */     }
/* 1220:1994 */     if ((dir != null) && (prefix != null))
/* 1221:     */     {
/* 1222:1995 */       ((AbstractFileSaver)result).setDir(dir);
/* 1223:1996 */       ((AbstractFileSaver)result).setFilePrefix(prefix);
/* 1224:     */     }
/* 1225:1999 */     if ((result instanceof FileSourcedConverter)) {
/* 1226:2000 */       ((FileSourcedConverter)result).setUseRelativePath(relative);
/* 1227:     */     }
/* 1228:2004 */     return result;
/* 1229:     */   }
/* 1230:     */   
/* 1231:     */   public Element writeBeanVisual(Element parent, Object o, String name)
/* 1232:     */     throws Exception
/* 1233:     */   {
/* 1234:2024 */     if (DEBUG) {
/* 1235:2025 */       trace(new Throwable(), name);
/* 1236:     */     }
/* 1237:2028 */     this.m_CurrentNode = parent;
/* 1238:     */     
/* 1239:2030 */     BeanVisual visual = (BeanVisual)o;
/* 1240:2031 */     Element node = writeToXML(parent, o, name);
/* 1241:     */     
/* 1242:     */ 
/* 1243:2034 */     invokeWriteToXML(node, visual.getIconPath(), "iconPath");
/* 1244:2035 */     invokeWriteToXML(node, visual.getAnimatedIconPath(), "animatedIconPath");
/* 1245:     */     
/* 1246:2037 */     return node;
/* 1247:     */   }
/* 1248:     */   
/* 1249:     */   public Object readBeanVisual(Element node)
/* 1250:     */     throws Exception
/* 1251:     */   {
/* 1252:2058 */     if (DEBUG) {
/* 1253:2059 */       trace(new Throwable(), node.getAttribute("name"));
/* 1254:     */     }
/* 1255:2062 */     this.m_CurrentNode = node;
/* 1256:     */     
/* 1257:2064 */     Object result = null;
/* 1258:2065 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 1259:2066 */     String text = "";
/* 1260:2067 */     String iconPath = "";
/* 1261:2068 */     String animIconPath = "";
/* 1262:2071 */     for (int i = 0; i < children.size(); i++)
/* 1263:     */     {
/* 1264:2072 */       Element child = (Element)children.get(i);
/* 1265:2073 */       String name = child.getAttribute("name");
/* 1266:2075 */       if (name.equals("text")) {
/* 1267:2076 */         text = (String)invokeReadFromXML(child);
/* 1268:2077 */       } else if (name.equals("iconPath")) {
/* 1269:2078 */         iconPath = (String)invokeReadFromXML(child);
/* 1270:2079 */       } else if (name.equals("animatedIconPath")) {
/* 1271:2080 */         animIconPath = (String)invokeReadFromXML(child);
/* 1272:     */       }
/* 1273:     */     }
/* 1274:2084 */     result = new BeanVisual(text, iconPath, animIconPath);
/* 1275:2087 */     for (i = 0; i < children.size(); i++) {
/* 1276:2088 */       readFromXML(result, node.getAttribute("name"), (Element)children.get(i));
/* 1277:     */     }
/* 1278:2091 */     return result;
/* 1279:     */   }
/* 1280:     */   
/* 1281:     */   protected Vector<Integer> getIDsForBeanInstances(Vector<Object> beans)
/* 1282:     */   {
/* 1283:2108 */     Vector<Integer> result = new Vector();
/* 1284:2110 */     for (int i = 0; i < beans.size(); i++)
/* 1285:     */     {
/* 1286:2111 */       int pos = this.m_BeanInstances.indexOf(beans.get(i));
/* 1287:2112 */       result.add(this.m_BeanInstancesID.get(pos));
/* 1288:     */     }
/* 1289:2115 */     return result;
/* 1290:     */   }
/* 1291:     */   
/* 1292:     */   public Element writeMetaBean(Element parent, Object o, String name)
/* 1293:     */     throws Exception
/* 1294:     */   {
/* 1295:2135 */     if (DEBUG) {
/* 1296:2136 */       trace(new Throwable(), name);
/* 1297:     */     }
/* 1298:2139 */     this.m_CurrentNode = parent;
/* 1299:     */     
/* 1300:2141 */     MetaBean meta = (MetaBean)o;
/* 1301:2142 */     Element node = writeToXML(parent, o, name);
/* 1302:     */     
/* 1303:2144 */     invokeWriteToXML(node, getIDsForBeanInstances(meta.getBeansInInputs()), "inputs_id");
/* 1304:     */     
/* 1305:2146 */     invokeWriteToXML(node, getIDsForBeanInstances(meta.getBeansInOutputs()), "outputs_id");
/* 1306:     */     
/* 1307:     */ 
/* 1308:2149 */     return node;
/* 1309:     */   }
/* 1310:     */   
/* 1311:     */   protected Vector<Object> getBeanInstancesForIDs(Vector<Integer> ids)
/* 1312:     */   {
/* 1313:2166 */     Vector<Object> result = new Vector();
/* 1314:2168 */     for (int i = 0; i < ids.size(); i++)
/* 1315:     */     {
/* 1316:2169 */       int pos = this.m_BeanInstancesID.indexOf(ids.get(i));
/* 1317:2170 */       result.add(this.m_BeanInstances.get(pos));
/* 1318:     */     }
/* 1319:2173 */     return result;
/* 1320:     */   }
/* 1321:     */   
/* 1322:     */   public Object readMetaBean(Element node)
/* 1323:     */     throws Exception
/* 1324:     */   {
/* 1325:2196 */     if (DEBUG) {
/* 1326:2197 */       trace(new Throwable(), node.getAttribute("name"));
/* 1327:     */     }
/* 1328:2200 */     this.m_CurrentNode = node;
/* 1329:     */     
/* 1330:2202 */     Object result = new MetaBean();
/* 1331:2203 */     Vector<Element> children = XMLDocument.getChildTags(node);
/* 1332:2204 */     Vector<Integer> inputs = new Vector();
/* 1333:2205 */     Vector<Integer> outputs = new Vector();
/* 1334:2206 */     Vector<Point> coords = new Vector();
/* 1335:     */     
/* 1336:     */ 
/* 1337:2209 */     this.m_CurrentMetaBean = ((MetaBean)result);
/* 1338:2211 */     for (int i = 0; i < children.size(); i++)
/* 1339:     */     {
/* 1340:2212 */       Element child = (Element)children.get(i);
/* 1341:2213 */       String name = child.getAttribute("name");
/* 1342:2215 */       if (name.equals("associatedConnections")) {
/* 1343:2216 */         ((MetaBean)result).setAssociatedConnections((Vector)invokeReadFromXML(child));
/* 1344:2218 */       } else if (name.equals("inputs_id")) {
/* 1345:2219 */         inputs = (Vector)invokeReadFromXML(child);
/* 1346:2220 */       } else if (name.equals("outputs_id")) {
/* 1347:2221 */         outputs = (Vector)invokeReadFromXML(child);
/* 1348:2222 */       } else if (name.equals("subFlow")) {
/* 1349:2223 */         ((MetaBean)result).setSubFlow((Vector)invokeReadFromXML(child));
/* 1350:2225 */       } else if (name.equals("originalCoords")) {
/* 1351:2226 */         coords = (Vector)invokeReadFromXML(child);
/* 1352:2227 */       } else if (name.equals("inputs")) {
/* 1353:2228 */         System.out.println("INFO: '" + name + "' will be restored later.");
/* 1354:2229 */       } else if (name.equals("outputs")) {
/* 1355:2230 */         System.out.println("INFO: '" + name + "' will be restored later.");
/* 1356:     */       } else {
/* 1357:2232 */         readFromXML(result, name, child);
/* 1358:     */       }
/* 1359:     */     }
/* 1360:2236 */     MetaBean bean = (MetaBean)result;
/* 1361:     */     
/* 1362:     */ 
/* 1363:2239 */     bean.setInputs(getBeanInstancesForIDs(inputs));
/* 1364:2240 */     bean.setOutputs(getBeanInstancesForIDs(outputs));
/* 1365:2241 */     bean.setOriginalCoords(coords);
/* 1366:     */     
/* 1367:2243 */     return result;
/* 1368:     */   }
/* 1369:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.xml.XMLBeans
 * JD-Core Version:    0.7.0.1
 */