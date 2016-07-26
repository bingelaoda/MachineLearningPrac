/*    1:     */ package weka.core.xml;
/*    2:     */ 
/*    3:     */ import java.beans.BeanInfo;
/*    4:     */ import java.beans.Introspector;
/*    5:     */ import java.beans.PropertyDescriptor;
/*    6:     */ import java.io.BufferedInputStream;
/*    7:     */ import java.io.BufferedOutputStream;
/*    8:     */ import java.io.File;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileOutputStream;
/*   11:     */ import java.io.InputStream;
/*   12:     */ import java.io.ObjectInputStream;
/*   13:     */ import java.io.ObjectOutputStream;
/*   14:     */ import java.io.OutputStream;
/*   15:     */ import java.io.PrintStream;
/*   16:     */ import java.io.Reader;
/*   17:     */ import java.io.Writer;
/*   18:     */ import java.lang.reflect.Array;
/*   19:     */ import java.lang.reflect.Constructor;
/*   20:     */ import java.lang.reflect.Method;
/*   21:     */ import java.util.ArrayList;
/*   22:     */ import java.util.Enumeration;
/*   23:     */ import java.util.Hashtable;
/*   24:     */ import java.util.List;
/*   25:     */ import java.util.Vector;
/*   26:     */ import org.w3c.dom.Document;
/*   27:     */ import org.w3c.dom.Element;
/*   28:     */ import weka.core.RevisionHandler;
/*   29:     */ import weka.core.RevisionUtils;
/*   30:     */ import weka.core.Utils;
/*   31:     */ import weka.core.Version;
/*   32:     */ 
/*   33:     */ public class XMLSerialization
/*   34:     */   implements RevisionHandler
/*   35:     */ {
/*   36: 103 */   protected static boolean DEBUG = false;
/*   37: 110 */   protected Element m_CurrentNode = null;
/*   38:     */   public static final String TAG_OBJECT = "object";
/*   39:     */   public static final String ATT_VERSION = "version";
/*   40:     */   public static final String ATT_NAME = "name";
/*   41:     */   public static final String ATT_CLASS = "class";
/*   42:     */   public static final String ATT_PRIMITIVE = "primitive";
/*   43:     */   public static final String ATT_ARRAY = "array";
/*   44:     */   public static final String ATT_NULL = "null";
/*   45:     */   public static final String VAL_YES = "yes";
/*   46:     */   public static final String VAL_NO = "no";
/*   47:     */   public static final String VAL_ROOT = "__root__";
/*   48:     */   public static final String ROOT_NODE = "object";
/*   49:     */   public static final String ATT_PRIMITIVE_DEFAULT = "no";
/*   50:     */   public static final String ATT_ARRAY_DEFAULT = "no";
/*   51:     */   public static final String ATT_NULL_DEFAULT = "no";
/*   52: 167 */   public static final String DOCTYPE = "<!DOCTYPE object\n[\n   <!ELEMENT object (#PCDATA|object)*>\n   <!ATTLIST object name      CDATA #REQUIRED>\n   <!ATTLIST object class     CDATA #REQUIRED>\n   <!ATTLIST object primitive CDATA \"no\">\n   <!ATTLIST object array     CDATA \"no\">   <!-- the dimensions of the array; no=0, yes=1 -->\n   <!ATTLIST object null      CDATA \"no\">\n   <!ATTLIST object version   CDATA \"" + Version.VERSION + "\">\n" + "]\n" + ">";
/*   53: 188 */   public static final List<String> SUPPRESS_PROPERTY_WARNINGS = new ArrayList();
/*   54: 192 */   protected XMLDocument m_Document = null;
/*   55: 195 */   protected PropertyHandler m_Properties = null;
/*   56: 198 */   protected XMLSerializationMethodHandler m_CustomMethods = null;
/*   57: 205 */   protected Hashtable<Class<?>, String> m_ClassnameOverride = null;
/*   58:     */   
/*   59:     */   public XMLSerialization()
/*   60:     */     throws Exception
/*   61:     */   {
/*   62: 214 */     clear();
/*   63:     */   }
/*   64:     */   
/*   65:     */   protected void trace(Throwable t, String msg)
/*   66:     */   {
/*   67: 226 */     if ((DEBUG) && (t.getStackTrace().length > 0)) {
/*   68: 227 */       System.out.println("trace: " + t.getStackTrace()[0] + ": " + msg);
/*   69:     */     }
/*   70:     */   }
/*   71:     */   
/*   72:     */   public void clear()
/*   73:     */     throws Exception
/*   74:     */   {
/*   75: 238 */     this.m_Document = new XMLDocument();
/*   76: 239 */     this.m_Document.setValidating(true);
/*   77: 240 */     this.m_Document.newDocument(DOCTYPE, "object");
/*   78:     */     
/*   79: 242 */     this.m_Properties = new PropertyHandler();
/*   80: 243 */     this.m_CustomMethods = new XMLSerializationMethodHandler(this);
/*   81:     */     
/*   82: 245 */     this.m_ClassnameOverride = new Hashtable();
/*   83:     */     
/*   84:     */ 
/*   85:     */ 
/*   86:     */ 
/*   87: 250 */     this.m_ClassnameOverride.put(File.class, File.class.getName());
/*   88:     */     
/*   89: 252 */     setVersion(Version.VERSION);
/*   90:     */     
/*   91: 254 */     this.m_CurrentNode = null;
/*   92:     */   }
/*   93:     */   
/*   94:     */   private void setVersion(String version)
/*   95:     */   {
/*   96: 265 */     Document doc = this.m_Document.getDocument();
/*   97: 266 */     doc.getDocumentElement().setAttribute("version", version);
/*   98:     */   }
/*   99:     */   
/*  100:     */   public String getVersion()
/*  101:     */   {
/*  102: 279 */     Document doc = this.m_Document.getDocument();
/*  103: 280 */     String result = doc.getDocumentElement().getAttribute("version");
/*  104:     */     
/*  105: 282 */     return result;
/*  106:     */   }
/*  107:     */   
/*  108:     */   private void checkVersion()
/*  109:     */   {
/*  110: 293 */     Version version = new Version();
/*  111: 294 */     String versionStr = getVersion();
/*  112: 295 */     if (versionStr.equals("")) {
/*  113: 296 */       System.out.println("WARNING: has no version!");
/*  114: 297 */     } else if (version.isOlder(versionStr)) {
/*  115: 298 */       System.out.println("WARNING: loading a newer version (" + versionStr + " > " + Version.VERSION + ")!");
/*  116: 300 */     } else if (version.isNewer(versionStr)) {
/*  117: 301 */       System.out.println("NOTE: loading an older version (" + versionStr + " < " + Version.VERSION + ")!");
/*  118:     */     }
/*  119:     */   }
/*  120:     */   
/*  121:     */   protected Hashtable<String, PropertyDescriptor> getDescriptors(Object o)
/*  122:     */     throws Exception
/*  123:     */   {
/*  124: 322 */     Hashtable<String, PropertyDescriptor> result = new Hashtable();
/*  125:     */     
/*  126: 324 */     BeanInfo info = Introspector.getBeanInfo(o.getClass());
/*  127: 325 */     PropertyDescriptor[] desc = info.getPropertyDescriptors();
/*  128: 326 */     for (int i = 0; i < desc.length; i++) {
/*  129: 328 */       if ((desc[i].getReadMethod() != null) && (desc[i].getWriteMethod() != null)) {
/*  130: 331 */         if (!this.m_Properties.isIgnored(desc[i].getDisplayName())) {
/*  131: 336 */           if (!this.m_Properties.isIgnored(o, desc[i].getDisplayName())) {
/*  132: 341 */             if (this.m_Properties.isAllowed(o, desc[i].getDisplayName())) {
/*  133: 345 */               result.put(desc[i].getDisplayName(), desc[i]);
/*  134:     */             }
/*  135:     */           }
/*  136:     */         }
/*  137:     */       }
/*  138:     */     }
/*  139: 349 */     return result;
/*  140:     */   }
/*  141:     */   
/*  142:     */   protected String getPath(Element node)
/*  143:     */   {
/*  144: 362 */     String result = node.getAttribute("name");
/*  145: 364 */     while (node.getParentNode() != node.getOwnerDocument())
/*  146:     */     {
/*  147: 365 */       node = (Element)node.getParentNode();
/*  148: 366 */       result = node.getAttribute("name") + "." + result;
/*  149:     */     }
/*  150: 369 */     return result;
/*  151:     */   }
/*  152:     */   
/*  153:     */   protected String booleanToString(boolean b)
/*  154:     */   {
/*  155: 380 */     if (b) {
/*  156: 381 */       return "yes";
/*  157:     */     }
/*  158: 383 */     return "no";
/*  159:     */   }
/*  160:     */   
/*  161:     */   protected boolean stringToBoolean(String s)
/*  162:     */   {
/*  163: 396 */     if (s.equals("")) {
/*  164: 397 */       return false;
/*  165:     */     }
/*  166: 398 */     if (s.equals("yes")) {
/*  167: 399 */       return true;
/*  168:     */     }
/*  169: 400 */     if (s.equalsIgnoreCase("true")) {
/*  170: 401 */       return true;
/*  171:     */     }
/*  172: 402 */     if (s.replaceAll("[0-9]*", "").equals("")) {
/*  173: 403 */       return Integer.parseInt(s) != 0;
/*  174:     */     }
/*  175: 405 */     return false;
/*  176:     */   }
/*  177:     */   
/*  178:     */   protected Element addElement(Element parent, String name, String classname, boolean primitive)
/*  179:     */   {
/*  180: 421 */     return addElement(parent, name, classname, primitive, 0);
/*  181:     */   }
/*  182:     */   
/*  183:     */   protected Element addElement(Element parent, String name, String classname, boolean primitive, int array)
/*  184:     */   {
/*  185: 437 */     return addElement(parent, name, classname, primitive, array, false);
/*  186:     */   }
/*  187:     */   
/*  188:     */   protected Element addElement(Element parent, String name, String classname, boolean primitive, int array, boolean isnull)
/*  189:     */   {
/*  190:     */     Element result;
/*  191:     */     Element result;
/*  192: 456 */     if (parent == null) {
/*  193: 457 */       result = this.m_Document.getDocument().getDocumentElement();
/*  194:     */     } else {
/*  195: 459 */       result = (Element)parent.appendChild(this.m_Document.getDocument().createElement("object"));
/*  196:     */     }
/*  197: 465 */     result.setAttribute("name", name);
/*  198: 466 */     result.setAttribute("class", classname);
/*  199: 469 */     if (!booleanToString(primitive).equals("no")) {
/*  200: 470 */       result.setAttribute("primitive", booleanToString(primitive));
/*  201:     */     }
/*  202: 474 */     if (array > 1) {
/*  203: 475 */       result.setAttribute("array", Integer.toString(array));
/*  204: 479 */     } else if (!booleanToString(array == 1).equals("no")) {
/*  205: 480 */       result.setAttribute("array", booleanToString(array == 1));
/*  206:     */     }
/*  207: 484 */     if (!booleanToString(isnull).equals("no")) {
/*  208: 485 */       result.setAttribute("null", booleanToString(isnull));
/*  209:     */     }
/*  210: 488 */     return result;
/*  211:     */   }
/*  212:     */   
/*  213:     */   protected String overrideClassname(Object o)
/*  214:     */   {
/*  215: 506 */     String result = o.getClass().getName();
/*  216:     */     
/*  217:     */ 
/*  218: 509 */     Enumeration<Class<?>> enm = this.m_ClassnameOverride.keys();
/*  219: 510 */     while (enm.hasMoreElements())
/*  220:     */     {
/*  221: 511 */       Class<?> currentCls = (Class)enm.nextElement();
/*  222: 512 */       if (currentCls.isInstance(o)) {
/*  223: 513 */         result = (String)this.m_ClassnameOverride.get(currentCls);
/*  224:     */       }
/*  225:     */     }
/*  226: 518 */     return result;
/*  227:     */   }
/*  228:     */   
/*  229:     */   protected String overrideClassname(String classname)
/*  230:     */   {
/*  231: 539 */     String result = classname;
/*  232:     */     
/*  233:     */ 
/*  234: 542 */     Enumeration<Class<?>> enm = this.m_ClassnameOverride.keys();
/*  235: 543 */     while (enm.hasMoreElements())
/*  236:     */     {
/*  237: 544 */       Class<?> currentCls = (Class)enm.nextElement();
/*  238: 545 */       if (currentCls.getName().equals(classname)) {
/*  239: 546 */         result = (String)this.m_ClassnameOverride.get(currentCls);
/*  240:     */       }
/*  241:     */     }
/*  242: 551 */     return result;
/*  243:     */   }
/*  244:     */   
/*  245:     */   protected PropertyDescriptor determineDescriptor(String className, String displayName)
/*  246:     */   {
/*  247: 565 */     PropertyDescriptor result = null;
/*  248:     */     try
/*  249:     */     {
/*  250: 568 */       result = new PropertyDescriptor(displayName, Class.forName(className));
/*  251:     */     }
/*  252:     */     catch (Exception e)
/*  253:     */     {
/*  254: 570 */       result = null;
/*  255:     */     }
/*  256: 573 */     return result;
/*  257:     */   }
/*  258:     */   
/*  259:     */   protected Element writeBooleanToXML(Element parent, boolean o, String name)
/*  260:     */     throws Exception
/*  261:     */   {
/*  262: 591 */     if (DEBUG) {
/*  263: 592 */       trace(new Throwable(), name);
/*  264:     */     }
/*  265: 595 */     this.m_CurrentNode = parent;
/*  266:     */     
/*  267: 597 */     Element node = addElement(parent, name, Boolean.TYPE.getName(), true);
/*  268: 598 */     node.appendChild(node.getOwnerDocument().createTextNode(new Boolean(o).toString()));
/*  269:     */     
/*  270:     */ 
/*  271: 601 */     return node;
/*  272:     */   }
/*  273:     */   
/*  274:     */   protected Element writeByteToXML(Element parent, byte o, String name)
/*  275:     */     throws Exception
/*  276:     */   {
/*  277: 619 */     if (DEBUG) {
/*  278: 620 */       trace(new Throwable(), name);
/*  279:     */     }
/*  280: 623 */     this.m_CurrentNode = parent;
/*  281:     */     
/*  282: 625 */     Element node = addElement(parent, name, Byte.TYPE.getName(), true);
/*  283: 626 */     node.appendChild(node.getOwnerDocument().createTextNode(new Byte(o).toString()));
/*  284:     */     
/*  285:     */ 
/*  286: 629 */     return node;
/*  287:     */   }
/*  288:     */   
/*  289:     */   protected Element writeCharToXML(Element parent, char o, String name)
/*  290:     */     throws Exception
/*  291:     */   {
/*  292: 647 */     if (DEBUG) {
/*  293: 648 */       trace(new Throwable(), name);
/*  294:     */     }
/*  295: 651 */     this.m_CurrentNode = parent;
/*  296:     */     
/*  297: 653 */     Element node = addElement(parent, name, Character.TYPE.getName(), true);
/*  298: 654 */     node.appendChild(node.getOwnerDocument().createTextNode(new Character(o).toString()));
/*  299:     */     
/*  300:     */ 
/*  301: 657 */     return node;
/*  302:     */   }
/*  303:     */   
/*  304:     */   protected Element writeDoubleToXML(Element parent, double o, String name)
/*  305:     */     throws Exception
/*  306:     */   {
/*  307: 675 */     if (DEBUG) {
/*  308: 676 */       trace(new Throwable(), name);
/*  309:     */     }
/*  310: 679 */     this.m_CurrentNode = parent;
/*  311:     */     
/*  312: 681 */     Element node = addElement(parent, name, Double.TYPE.getName(), true);
/*  313: 682 */     node.appendChild(node.getOwnerDocument().createTextNode(new Double(o).toString()));
/*  314:     */     
/*  315:     */ 
/*  316: 685 */     return node;
/*  317:     */   }
/*  318:     */   
/*  319:     */   protected Element writeFloatToXML(Element parent, float o, String name)
/*  320:     */     throws Exception
/*  321:     */   {
/*  322: 703 */     if (DEBUG) {
/*  323: 704 */       trace(new Throwable(), name);
/*  324:     */     }
/*  325: 707 */     this.m_CurrentNode = parent;
/*  326:     */     
/*  327: 709 */     Element node = addElement(parent, name, Float.TYPE.getName(), true);
/*  328: 710 */     node.appendChild(node.getOwnerDocument().createTextNode(new Float(o).toString()));
/*  329:     */     
/*  330:     */ 
/*  331: 713 */     return node;
/*  332:     */   }
/*  333:     */   
/*  334:     */   protected Element writeIntToXML(Element parent, int o, String name)
/*  335:     */     throws Exception
/*  336:     */   {
/*  337: 731 */     if (DEBUG) {
/*  338: 732 */       trace(new Throwable(), name);
/*  339:     */     }
/*  340: 735 */     this.m_CurrentNode = parent;
/*  341:     */     
/*  342: 737 */     Element node = addElement(parent, name, Integer.TYPE.getName(), true);
/*  343: 738 */     node.appendChild(node.getOwnerDocument().createTextNode(new Integer(o).toString()));
/*  344:     */     
/*  345:     */ 
/*  346: 741 */     return node;
/*  347:     */   }
/*  348:     */   
/*  349:     */   protected Element writeLongToXML(Element parent, long o, String name)
/*  350:     */     throws Exception
/*  351:     */   {
/*  352: 759 */     if (DEBUG) {
/*  353: 760 */       trace(new Throwable(), name);
/*  354:     */     }
/*  355: 763 */     this.m_CurrentNode = parent;
/*  356:     */     
/*  357: 765 */     Element node = addElement(parent, name, Long.TYPE.getName(), true);
/*  358: 766 */     node.appendChild(node.getOwnerDocument().createTextNode(new Long(o).toString()));
/*  359:     */     
/*  360:     */ 
/*  361: 769 */     return node;
/*  362:     */   }
/*  363:     */   
/*  364:     */   protected Element writeShortToXML(Element parent, short o, String name)
/*  365:     */     throws Exception
/*  366:     */   {
/*  367: 787 */     if (DEBUG) {
/*  368: 788 */       trace(new Throwable(), name);
/*  369:     */     }
/*  370: 791 */     this.m_CurrentNode = parent;
/*  371:     */     
/*  372: 793 */     Element node = addElement(parent, name, Short.TYPE.getName(), true);
/*  373: 794 */     node.appendChild(node.getOwnerDocument().createTextNode(new Short(o).toString()));
/*  374:     */     
/*  375:     */ 
/*  376: 797 */     return node;
/*  377:     */   }
/*  378:     */   
/*  379:     */   protected boolean isPrimitiveArray(Class<?> c)
/*  380:     */   {
/*  381: 808 */     if (c.getComponentType().isArray()) {
/*  382: 809 */       return isPrimitiveArray(c.getComponentType());
/*  383:     */     }
/*  384: 811 */     return c.getComponentType().isPrimitive();
/*  385:     */   }
/*  386:     */   
/*  387:     */   public Element writeToXML(Element parent, Object o, String name)
/*  388:     */     throws Exception
/*  389:     */   {
/*  390: 849 */     Element node = null;
/*  391: 852 */     if (DEBUG) {
/*  392: 853 */       trace(new Throwable(), name);
/*  393:     */     }
/*  394: 857 */     if (o == null)
/*  395:     */     {
/*  396: 858 */       node = addElement(parent, name, "" + null, false, 0, true);
/*  397: 859 */       return node;
/*  398:     */     }
/*  399: 863 */     Object obj = null;
/*  400:     */     
/*  401:     */ 
/*  402: 866 */     int array = 0;
/*  403: 867 */     if (o.getClass().isArray()) {
/*  404: 868 */       array = Utils.getArrayDimensions(o);
/*  405:     */     }
/*  406:     */     boolean primitive;
/*  407:     */     boolean primitive;
/*  408:     */     String classname;
/*  409: 870 */     if (array > 0)
/*  410:     */     {
/*  411: 871 */       String classname = Utils.getArrayClass(o.getClass()).getName();
/*  412: 872 */       primitive = isPrimitiveArray(o.getClass());
/*  413:     */     }
/*  414:     */     else
/*  415:     */     {
/*  416: 877 */       PropertyDescriptor desc = null;
/*  417: 878 */       if (parent != null) {
/*  418: 879 */         desc = determineDescriptor(parent.getAttribute("class"), name);
/*  419:     */       }
/*  420:     */       boolean primitive;
/*  421: 882 */       if (desc != null) {
/*  422: 883 */         primitive = desc.getPropertyType().isPrimitive();
/*  423:     */       } else {
/*  424: 885 */         primitive = o.getClass().isPrimitive();
/*  425:     */       }
/*  426:     */       String classname;
/*  427: 891 */       if (primitive)
/*  428:     */       {
/*  429: 892 */         classname = desc.getPropertyType().getName();
/*  430:     */       }
/*  431:     */       else
/*  432:     */       {
/*  433: 894 */         obj = o;
/*  434: 895 */         classname = o.getClass().getName();
/*  435:     */       }
/*  436:     */     }
/*  437: 901 */     if ((parent != null) && (!parent.getAttribute("array").equals("")) && (!parent.getAttribute("array").equals("no")) && (stringToBoolean(parent.getAttribute("primitive"))))
/*  438:     */     {
/*  439: 904 */       primitive = true;
/*  440: 905 */       classname = parent.getAttribute("class");
/*  441: 906 */       obj = null;
/*  442:     */     }
/*  443: 910 */     if (obj != null) {
/*  444: 911 */       classname = overrideClassname(obj);
/*  445:     */     } else {
/*  446: 913 */       classname = overrideClassname(classname);
/*  447:     */     }
/*  448: 917 */     node = addElement(parent, name, classname, primitive, array);
/*  449: 920 */     if (array > 0) {
/*  450: 921 */       for (int i = 0; i < Array.getLength(o); i++) {
/*  451: 922 */         invokeWriteToXML(node, Array.get(o, i), Integer.toString(i));
/*  452:     */       }
/*  453:     */     }
/*  454: 928 */     if (primitive)
/*  455:     */     {
/*  456: 929 */       node.appendChild(node.getOwnerDocument().createTextNode(o.toString()));
/*  457:     */     }
/*  458:     */     else
/*  459:     */     {
/*  460: 934 */       Hashtable<String, PropertyDescriptor> memberlist = getDescriptors(o);
/*  461: 936 */       if (memberlist.size() == 0)
/*  462:     */       {
/*  463: 937 */         if (!o.toString().equals(""))
/*  464:     */         {
/*  465: 938 */           String tmpStr = o.toString();
/*  466:     */           
/*  467:     */ 
/*  468: 941 */           tmpStr = tmpStr.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
/*  469:     */           
/*  470:     */ 
/*  471:     */ 
/*  472: 945 */           tmpStr = tmpStr.replaceAll("\n", "&#10;").replaceAll("\r", "&#13;").replaceAll("\t", "&#9;");
/*  473: 948 */           if ((o instanceof File)) {
/*  474: 950 */             tmpStr = tmpStr.replace('\\', '/');
/*  475:     */           }
/*  476: 953 */           node.appendChild(node.getOwnerDocument().createTextNode(tmpStr));
/*  477:     */         }
/*  478:     */       }
/*  479:     */       else
/*  480:     */       {
/*  481: 956 */         Enumeration<String> enm = memberlist.keys();
/*  482: 957 */         while (enm.hasMoreElements())
/*  483:     */         {
/*  484: 958 */           String memberName = ((String)enm.nextElement()).toString();
/*  485: 961 */           if ((!this.m_Properties.isIgnored(memberName)) && (!this.m_Properties.isIgnored(getPath(node) + "." + memberName)) && (!this.m_Properties.isIgnored(o, getPath(node) + "." + memberName)) && 
/*  486:     */           
/*  487:     */ 
/*  488:     */ 
/*  489:     */ 
/*  490:     */ 
/*  491:     */ 
/*  492: 968 */             (this.m_Properties.isAllowed(o, memberName)))
/*  493:     */           {
/*  494: 972 */             PropertyDescriptor desc = (PropertyDescriptor)memberlist.get(memberName);
/*  495: 973 */             Method method = desc.getReadMethod();
/*  496: 974 */             Object member = method.invoke(o, (Object[])null);
/*  497: 975 */             invokeWriteToXML(node, member, memberName);
/*  498:     */           }
/*  499:     */         }
/*  500:     */       }
/*  501:     */     }
/*  502: 981 */     return node;
/*  503:     */   }
/*  504:     */   
/*  505:     */   protected Element invokeWriteToXML(Element parent, Object o, String name)
/*  506:     */     throws Exception
/*  507:     */   {
/*  508:1004 */     Element node = null;
/*  509:1005 */     Method method = null;
/*  510:1006 */     boolean useDefault = false;
/*  511:     */     
/*  512:1008 */     this.m_CurrentNode = parent;
/*  513:1011 */     if (o == null) {
/*  514:1012 */       useDefault = true;
/*  515:     */     }
/*  516:     */     try
/*  517:     */     {
/*  518:1016 */       if (!useDefault)
/*  519:     */       {
/*  520:1017 */         boolean array = o.getClass().isArray();
/*  521:1020 */         if (this.m_CustomMethods.write().contains(name)) {
/*  522:1021 */           method = this.m_CustomMethods.write().get(o.getClass());
/*  523:1024 */         } else if ((!array) && (this.m_CustomMethods.write().contains(o.getClass()))) {
/*  524:1025 */           method = this.m_CustomMethods.write().get(o.getClass());
/*  525:     */         } else {
/*  526:1027 */           method = null;
/*  527:     */         }
/*  528:1030 */         useDefault = method == null;
/*  529:     */       }
/*  530:1034 */       if (!useDefault)
/*  531:     */       {
/*  532:1035 */         Class<?>[] methodClasses = new Class[3];
/*  533:1036 */         methodClasses[0] = Element.class;
/*  534:1037 */         methodClasses[1] = Object.class;
/*  535:1038 */         methodClasses[2] = String.class;
/*  536:1039 */         Object[] methodArgs = new Object[3];
/*  537:1040 */         methodArgs[0] = parent;
/*  538:1041 */         methodArgs[1] = o;
/*  539:1042 */         methodArgs[2] = name;
/*  540:1043 */         node = (Element)method.invoke(this, methodArgs);
/*  541:     */       }
/*  542:     */       else
/*  543:     */       {
/*  544:1047 */         node = writeToXML(parent, o, name);
/*  545:     */       }
/*  546:     */     }
/*  547:     */     catch (Exception e)
/*  548:     */     {
/*  549:1050 */       if (DEBUG) {
/*  550:1051 */         e.printStackTrace();
/*  551:     */       }
/*  552:1054 */       if (this.m_CurrentNode != null)
/*  553:     */       {
/*  554:1055 */         System.out.println("Happened near: " + getPath(this.m_CurrentNode));
/*  555:     */         
/*  556:1057 */         this.m_CurrentNode = null;
/*  557:     */       }
/*  558:1059 */       System.out.println("PROBLEM (write): " + name);
/*  559:     */       
/*  560:1061 */       throw ((Exception)e.fillInStackTrace());
/*  561:     */     }
/*  562:1064 */     return node;
/*  563:     */   }
/*  564:     */   
/*  565:     */   protected Object writePreProcess(Object o)
/*  566:     */     throws Exception
/*  567:     */   {
/*  568:1076 */     return o;
/*  569:     */   }
/*  570:     */   
/*  571:     */   protected void writePostProcess(Object o)
/*  572:     */     throws Exception
/*  573:     */   {}
/*  574:     */   
/*  575:     */   public XMLDocument toXML(Object o)
/*  576:     */     throws Exception
/*  577:     */   {
/*  578:1098 */     clear();
/*  579:1099 */     invokeWriteToXML(null, writePreProcess(o), "__root__");
/*  580:1100 */     writePostProcess(o);
/*  581:1101 */     return this.m_Document;
/*  582:     */   }
/*  583:     */   
/*  584:     */   protected PropertyDescriptor getDescriptorByName(Object o, String name)
/*  585:     */     throws Exception
/*  586:     */   {
/*  587:1118 */     PropertyDescriptor result = null;
/*  588:     */     
/*  589:1120 */     PropertyDescriptor[] desc = Introspector.getBeanInfo(o.getClass()).getPropertyDescriptors();
/*  590:1121 */     for (int i = 0; i < desc.length; i++) {
/*  591:1122 */       if (desc[i].getDisplayName().equals(name))
/*  592:     */       {
/*  593:1123 */         result = desc[i];
/*  594:1124 */         break;
/*  595:     */       }
/*  596:     */     }
/*  597:1128 */     return result;
/*  598:     */   }
/*  599:     */   
/*  600:     */   protected Class<?> determineClass(String name)
/*  601:     */     throws Exception
/*  602:     */   {
/*  603:     */     Class<?> result;
/*  604:     */     Class<?> result;
/*  605:1141 */     if (name.equals(Boolean.TYPE.getName()))
/*  606:     */     {
/*  607:1142 */       result = Boolean.TYPE;
/*  608:     */     }
/*  609:     */     else
/*  610:     */     {
/*  611:     */       Class<?> result;
/*  612:1143 */       if (name.equals(Byte.TYPE.getName()))
/*  613:     */       {
/*  614:1144 */         result = Byte.TYPE;
/*  615:     */       }
/*  616:     */       else
/*  617:     */       {
/*  618:     */         Class<?> result;
/*  619:1145 */         if (name.equals(Character.TYPE.getName()))
/*  620:     */         {
/*  621:1146 */           result = Character.TYPE;
/*  622:     */         }
/*  623:     */         else
/*  624:     */         {
/*  625:     */           Class<?> result;
/*  626:1147 */           if (name.equals(Double.TYPE.getName()))
/*  627:     */           {
/*  628:1148 */             result = Double.TYPE;
/*  629:     */           }
/*  630:     */           else
/*  631:     */           {
/*  632:     */             Class<?> result;
/*  633:1149 */             if (name.equals(Float.TYPE.getName()))
/*  634:     */             {
/*  635:1150 */               result = Float.TYPE;
/*  636:     */             }
/*  637:     */             else
/*  638:     */             {
/*  639:     */               Class<?> result;
/*  640:1151 */               if (name.equals(Integer.TYPE.getName()))
/*  641:     */               {
/*  642:1152 */                 result = Integer.TYPE;
/*  643:     */               }
/*  644:     */               else
/*  645:     */               {
/*  646:     */                 Class<?> result;
/*  647:1153 */                 if (name.equals(Long.TYPE.getName()))
/*  648:     */                 {
/*  649:1154 */                   result = Long.TYPE;
/*  650:     */                 }
/*  651:     */                 else
/*  652:     */                 {
/*  653:     */                   Class<?> result;
/*  654:1155 */                   if (name.equals(Short.TYPE.getName())) {
/*  655:1156 */                     result = Short.TYPE;
/*  656:     */                   } else {
/*  657:1158 */                     result = Class.forName(name);
/*  658:     */                   }
/*  659:     */                 }
/*  660:     */               }
/*  661:     */             }
/*  662:     */           }
/*  663:     */         }
/*  664:     */       }
/*  665:     */     }
/*  666:1161 */     return result;
/*  667:     */   }
/*  668:     */   
/*  669:     */   protected Object getPrimitive(Element node)
/*  670:     */     throws Exception
/*  671:     */   {
/*  672:1181 */     Class<?> cls = determineClass(node.getAttribute("class"));
/*  673:1182 */     Object tmpResult = Array.newInstance(cls, 1);
/*  674:1184 */     if (cls == Boolean.TYPE) {
/*  675:1185 */       Array.set(tmpResult, 0, new Boolean(XMLDocument.getContent(node)));
/*  676:1186 */     } else if (cls == Byte.TYPE) {
/*  677:1187 */       Array.set(tmpResult, 0, new Byte(XMLDocument.getContent(node)));
/*  678:1188 */     } else if (cls == Character.TYPE) {
/*  679:1189 */       Array.set(tmpResult, 0, new Character(XMLDocument.getContent(node).charAt(0)));
/*  680:1191 */     } else if (cls == Double.TYPE) {
/*  681:1192 */       Array.set(tmpResult, 0, new Double(XMLDocument.getContent(node)));
/*  682:1193 */     } else if (cls == Float.TYPE) {
/*  683:1194 */       Array.set(tmpResult, 0, new Float(XMLDocument.getContent(node)));
/*  684:1195 */     } else if (cls == Integer.TYPE) {
/*  685:1196 */       Array.set(tmpResult, 0, new Integer(XMLDocument.getContent(node)));
/*  686:1197 */     } else if (cls == Long.TYPE) {
/*  687:1198 */       Array.set(tmpResult, 0, new Long(XMLDocument.getContent(node)));
/*  688:1199 */     } else if (cls == Short.TYPE) {
/*  689:1200 */       Array.set(tmpResult, 0, new Short(XMLDocument.getContent(node)));
/*  690:     */     } else {
/*  691:1202 */       throw new Exception("Cannot get primitive for class '" + cls.getName() + "'!");
/*  692:     */     }
/*  693:1206 */     Object result = Array.get(tmpResult, 0);
/*  694:     */     
/*  695:1208 */     return result;
/*  696:     */   }
/*  697:     */   
/*  698:     */   public boolean readBooleanFromXML(Element node)
/*  699:     */     throws Exception
/*  700:     */   {
/*  701:1220 */     if (DEBUG) {
/*  702:1221 */       trace(new Throwable(), node.getAttribute("name"));
/*  703:     */     }
/*  704:1224 */     this.m_CurrentNode = node;
/*  705:     */     
/*  706:1226 */     return ((Boolean)getPrimitive(node)).booleanValue();
/*  707:     */   }
/*  708:     */   
/*  709:     */   public byte readByteFromXML(Element node)
/*  710:     */     throws Exception
/*  711:     */   {
/*  712:1238 */     if (DEBUG) {
/*  713:1239 */       trace(new Throwable(), node.getAttribute("name"));
/*  714:     */     }
/*  715:1242 */     this.m_CurrentNode = node;
/*  716:     */     
/*  717:1244 */     return ((Byte)getPrimitive(node)).byteValue();
/*  718:     */   }
/*  719:     */   
/*  720:     */   public char readCharFromXML(Element node)
/*  721:     */     throws Exception
/*  722:     */   {
/*  723:1256 */     if (DEBUG) {
/*  724:1257 */       trace(new Throwable(), node.getAttribute("name"));
/*  725:     */     }
/*  726:1260 */     this.m_CurrentNode = node;
/*  727:     */     
/*  728:1262 */     return ((Character)getPrimitive(node)).charValue();
/*  729:     */   }
/*  730:     */   
/*  731:     */   public double readDoubleFromXML(Element node)
/*  732:     */     throws Exception
/*  733:     */   {
/*  734:1274 */     if (DEBUG) {
/*  735:1275 */       trace(new Throwable(), node.getAttribute("name"));
/*  736:     */     }
/*  737:1278 */     this.m_CurrentNode = node;
/*  738:     */     
/*  739:1280 */     return ((Double)getPrimitive(node)).doubleValue();
/*  740:     */   }
/*  741:     */   
/*  742:     */   public float readFloatFromXML(Element node)
/*  743:     */     throws Exception
/*  744:     */   {
/*  745:1292 */     if (DEBUG) {
/*  746:1293 */       trace(new Throwable(), node.getAttribute("name"));
/*  747:     */     }
/*  748:1296 */     this.m_CurrentNode = node;
/*  749:     */     
/*  750:1298 */     return ((Float)getPrimitive(node)).floatValue();
/*  751:     */   }
/*  752:     */   
/*  753:     */   public int readIntFromXML(Element node)
/*  754:     */     throws Exception
/*  755:     */   {
/*  756:1310 */     if (DEBUG) {
/*  757:1311 */       trace(new Throwable(), node.getAttribute("name"));
/*  758:     */     }
/*  759:1314 */     this.m_CurrentNode = node;
/*  760:     */     
/*  761:1316 */     return ((Integer)getPrimitive(node)).intValue();
/*  762:     */   }
/*  763:     */   
/*  764:     */   public long readLongFromXML(Element node)
/*  765:     */     throws Exception
/*  766:     */   {
/*  767:1328 */     if (DEBUG) {
/*  768:1329 */       trace(new Throwable(), node.getAttribute("name"));
/*  769:     */     }
/*  770:1332 */     this.m_CurrentNode = node;
/*  771:     */     
/*  772:1334 */     return ((Long)getPrimitive(node)).longValue();
/*  773:     */   }
/*  774:     */   
/*  775:     */   public short readShortFromXML(Element node)
/*  776:     */     throws Exception
/*  777:     */   {
/*  778:1346 */     if (DEBUG) {
/*  779:1347 */       trace(new Throwable(), node.getAttribute("name"));
/*  780:     */     }
/*  781:1350 */     this.m_CurrentNode = node;
/*  782:     */     
/*  783:1352 */     return ((Short)getPrimitive(node)).shortValue();
/*  784:     */   }
/*  785:     */   
/*  786:     */   public Object readFromXML(Object o, String name, Element child)
/*  787:     */     throws Exception
/*  788:     */   {
/*  789:1376 */     Object result = o;
/*  790:1377 */     Hashtable<String, PropertyDescriptor> descriptors = getDescriptors(result);
/*  791:1378 */     String methodName = child.getAttribute("name");
/*  792:1381 */     if (this.m_Properties.isIgnored(getPath(child))) {
/*  793:1382 */       return result;
/*  794:     */     }
/*  795:1386 */     if (this.m_Properties.isIgnored(result, getPath(child))) {
/*  796:1387 */       return result;
/*  797:     */     }
/*  798:1391 */     if (!this.m_Properties.isAllowed(result, methodName)) {
/*  799:1392 */       return result;
/*  800:     */     }
/*  801:1395 */     PropertyDescriptor descriptor = (PropertyDescriptor)descriptors.get(methodName);
/*  802:1398 */     if (descriptor == null)
/*  803:     */     {
/*  804:1399 */       if ((!this.m_CustomMethods.read().contains(methodName)) && (!SUPPRESS_PROPERTY_WARNINGS.contains(name + "." + methodName))) {
/*  805:1401 */         System.out.println("WARNING: unknown property '" + name + "." + methodName + "'!");
/*  806:     */       }
/*  807:1404 */       return result;
/*  808:     */     }
/*  809:1407 */     Method method = descriptor.getWriteMethod();
/*  810:1408 */     Object[] methodArgs = new Object[1];
/*  811:1409 */     Object tmpResult = invokeReadFromXML(child);
/*  812:1410 */     Class<?> paramClass = method.getParameterTypes()[0];
/*  813:1413 */     if (paramClass.isArray())
/*  814:     */     {
/*  815:1415 */       if (Array.getLength(tmpResult) == 0) {
/*  816:1416 */         return result;
/*  817:     */       }
/*  818:1418 */       methodArgs[0] = tmpResult;
/*  819:     */     }
/*  820:     */     else
/*  821:     */     {
/*  822:1422 */       methodArgs[0] = tmpResult;
/*  823:     */     }
/*  824:     */     try
/*  825:     */     {
/*  826:1426 */       method.invoke(result, methodArgs);
/*  827:     */     }
/*  828:     */     catch (Exception ex)
/*  829:     */     {
/*  830:1428 */       System.err.println("Warning: error invoking method: " + methodName + " (" + ex.getCause().getMessage() + ")");
/*  831:     */     }
/*  832:1432 */     return result;
/*  833:     */   }
/*  834:     */   
/*  835:     */   protected int[] getArrayDimensions(Element node)
/*  836:     */   {
/*  837:     */     Vector<Element> children;
/*  838:     */     Vector<Element> children;
/*  839:1449 */     if (stringToBoolean(node.getAttribute("array"))) {
/*  840:1450 */       children = XMLDocument.getChildTags(node);
/*  841:     */     } else {
/*  842:1452 */       children = null;
/*  843:     */     }
/*  844:1455 */     if (children != null)
/*  845:     */     {
/*  846:1456 */       Vector<Integer> tmpVector = new Vector();
/*  847:1458 */       if (children.size() > 0)
/*  848:     */       {
/*  849:1460 */         int[] tmp = getArrayDimensions((Element)children.get(0));
/*  850:1463 */         if (tmp != null) {
/*  851:1464 */           for (int i = tmp.length - 1; i >= 0; i--) {
/*  852:1465 */             tmpVector.add(new Integer(tmp[i]));
/*  853:     */           }
/*  854:     */         }
/*  855:1470 */         tmpVector.add(0, new Integer(children.size()));
/*  856:     */       }
/*  857:     */       else
/*  858:     */       {
/*  859:1472 */         tmpVector.add(new Integer(0));
/*  860:     */       }
/*  861:1476 */       int[] result = new int[tmpVector.size()];
/*  862:1477 */       for (int i = 0; i < result.length; i++) {
/*  863:1478 */         result[i] = ((Integer)tmpVector.get(tmpVector.size() - i - 1)).intValue();
/*  864:     */       }
/*  865:     */     }
/*  866:1481 */     int[] result = null;
/*  867:     */     
/*  868:     */ 
/*  869:1484 */     return result;
/*  870:     */   }
/*  871:     */   
/*  872:     */   public Object readFromXML(Element node)
/*  873:     */     throws Exception
/*  874:     */   {
/*  875:1510 */     if (DEBUG) {
/*  876:1511 */       trace(new Throwable(), node.getAttribute("name"));
/*  877:     */     }
/*  878:1514 */     this.m_CurrentNode = node;
/*  879:     */     
/*  880:1516 */     Object result = null;
/*  881:     */     
/*  882:1518 */     String name = node.getAttribute("name");
/*  883:1519 */     String classname = node.getAttribute("class");
/*  884:1520 */     boolean primitive = stringToBoolean(node.getAttribute("primitive"));
/*  885:1521 */     boolean array = stringToBoolean(node.getAttribute("array"));
/*  886:1522 */     boolean isnull = stringToBoolean(node.getAttribute("null"));
/*  887:1525 */     if (isnull) {
/*  888:1526 */       return result;
/*  889:     */     }
/*  890:1529 */     Vector<Element> children = XMLDocument.getChildTags(node);
/*  891:1530 */     Class<?> cls = determineClass(classname);
/*  892:1533 */     if (array)
/*  893:     */     {
/*  894:1534 */       result = Array.newInstance(cls, getArrayDimensions(node));
/*  895:1535 */       for (int i = 0; i < children.size(); i++)
/*  896:     */       {
/*  897:1536 */         Element child = (Element)children.get(i);
/*  898:1537 */         Array.set(result, Integer.parseInt(child.getAttribute("name")), invokeReadFromXML(child));
/*  899:     */       }
/*  900:     */     }
/*  901:1544 */     if (children.size() == 0)
/*  902:     */     {
/*  903:1546 */       if (primitive)
/*  904:     */       {
/*  905:1547 */         result = getPrimitive(node);
/*  906:     */       }
/*  907:     */       else
/*  908:     */       {
/*  909:1551 */         Class<?>[] methodClasses = new Class[1];
/*  910:1552 */         methodClasses[0] = String.class;
/*  911:1553 */         Object[] methodArgs = new Object[1];
/*  912:1554 */         methodArgs[0] = XMLDocument.getContent(node);
/*  913:     */         try
/*  914:     */         {
/*  915:1556 */           Constructor<?> constructor = cls.getConstructor(methodClasses);
/*  916:1557 */           result = constructor.newInstance(methodArgs);
/*  917:     */         }
/*  918:     */         catch (Exception e)
/*  919:     */         {
/*  920:     */           try
/*  921:     */           {
/*  922:1562 */             result = cls.newInstance();
/*  923:     */           }
/*  924:     */           catch (Exception e2)
/*  925:     */           {
/*  926:1565 */             result = null;
/*  927:1566 */             System.out.println("ERROR: Can't instantiate '" + classname + "'!");
/*  928:     */           }
/*  929:     */         }
/*  930:     */       }
/*  931:     */     }
/*  932:     */     else
/*  933:     */     {
/*  934:1574 */       result = cls.newInstance();
/*  935:1575 */       for (int i = 0; i < children.size(); i++) {
/*  936:1576 */         result = readFromXML(result, name, (Element)children.get(i));
/*  937:     */       }
/*  938:     */     }
/*  939:1581 */     return result;
/*  940:     */   }
/*  941:     */   
/*  942:     */   protected Object invokeReadFromXML(Element node)
/*  943:     */     throws Exception
/*  944:     */   {
/*  945:1599 */     boolean useDefault = false;
/*  946:1600 */     Method method = null;
/*  947:1601 */     this.m_CurrentNode = node;
/*  948:     */     try
/*  949:     */     {
/*  950:1605 */       if (stringToBoolean(node.getAttribute("null"))) {
/*  951:1606 */         useDefault = true;
/*  952:     */       }
/*  953:1609 */       if (!useDefault)
/*  954:     */       {
/*  955:1610 */         boolean array = stringToBoolean(node.getAttribute("array"));
/*  956:1613 */         if (this.m_CustomMethods.read().contains(node.getAttribute("name"))) {
/*  957:1614 */           method = this.m_CustomMethods.read().get(node.getAttribute("name"));
/*  958:1617 */         } else if ((!array) && (this.m_CustomMethods.read().contains(determineClass(node.getAttribute("class"))))) {
/*  959:1620 */           method = this.m_CustomMethods.read().get(determineClass(node.getAttribute("class")));
/*  960:     */         } else {
/*  961:1623 */           method = null;
/*  962:     */         }
/*  963:1626 */         useDefault = method == null;
/*  964:     */       }
/*  965:1630 */       if (!useDefault)
/*  966:     */       {
/*  967:1631 */         Class<?>[] methodClasses = new Class[1];
/*  968:1632 */         methodClasses[0] = Element.class;
/*  969:1633 */         Object[] methodArgs = new Object[1];
/*  970:1634 */         methodArgs[0] = node;
/*  971:1635 */         return method.invoke(this, methodArgs);
/*  972:     */       }
/*  973:1639 */       return readFromXML(node);
/*  974:     */     }
/*  975:     */     catch (Exception e)
/*  976:     */     {
/*  977:1642 */       if (DEBUG) {
/*  978:1643 */         e.printStackTrace();
/*  979:     */       }
/*  980:1646 */       if (this.m_CurrentNode != null)
/*  981:     */       {
/*  982:1647 */         System.out.println("Happened near: " + getPath(this.m_CurrentNode));
/*  983:     */         
/*  984:1649 */         this.m_CurrentNode = null;
/*  985:     */       }
/*  986:1651 */       System.out.println("PROBLEM (read): " + node.getAttribute("name"));
/*  987:     */       
/*  988:1653 */       throw ((Exception)e.fillInStackTrace());
/*  989:     */     }
/*  990:     */   }
/*  991:     */   
/*  992:     */   protected Document readPreProcess(Document document)
/*  993:     */     throws Exception
/*  994:     */   {
/*  995:1667 */     return document;
/*  996:     */   }
/*  997:     */   
/*  998:     */   protected Object readPostProcess(Object o)
/*  999:     */     throws Exception
/* 1000:     */   {
/* 1001:1679 */     return o;
/* 1002:     */   }
/* 1003:     */   
/* 1004:     */   public Object fromXML(Document document)
/* 1005:     */     throws Exception
/* 1006:     */   {
/* 1007:1690 */     if (!document.getDocumentElement().getNodeName().equals("object")) {
/* 1008:1691 */       throw new Exception("Expected 'object' as root element, but found '" + document.getDocumentElement().getNodeName() + "'!");
/* 1009:     */     }
/* 1010:1695 */     this.m_Document.setDocument(readPreProcess(document));
/* 1011:1696 */     checkVersion();
/* 1012:1697 */     return readPostProcess(invokeReadFromXML(this.m_Document.getDocument().getDocumentElement()));
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   public Object read(String xml)
/* 1016:     */     throws Exception
/* 1017:     */   {
/* 1018:1711 */     return fromXML(this.m_Document.read(xml));
/* 1019:     */   }
/* 1020:     */   
/* 1021:     */   public Object read(File file)
/* 1022:     */     throws Exception
/* 1023:     */   {
/* 1024:1722 */     return fromXML(this.m_Document.read(file));
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   public Object read(InputStream stream)
/* 1028:     */     throws Exception
/* 1029:     */   {
/* 1030:1733 */     return fromXML(this.m_Document.read(stream));
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public Object read(Reader reader)
/* 1034:     */     throws Exception
/* 1035:     */   {
/* 1036:1744 */     return fromXML(this.m_Document.read(reader));
/* 1037:     */   }
/* 1038:     */   
/* 1039:     */   public void write(String file, Object o)
/* 1040:     */     throws Exception
/* 1041:     */   {
/* 1042:1755 */     toXML(o).write(file);
/* 1043:     */   }
/* 1044:     */   
/* 1045:     */   public void write(File file, Object o)
/* 1046:     */     throws Exception
/* 1047:     */   {
/* 1048:1766 */     toXML(o).write(file);
/* 1049:     */   }
/* 1050:     */   
/* 1051:     */   public void write(OutputStream stream, Object o)
/* 1052:     */     throws Exception
/* 1053:     */   {
/* 1054:1777 */     toXML(o).write(stream);
/* 1055:     */   }
/* 1056:     */   
/* 1057:     */   public void write(Writer writer, Object o)
/* 1058:     */     throws Exception
/* 1059:     */   {
/* 1060:1788 */     toXML(o).write(writer);
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public static void main(String[] args)
/* 1064:     */     throws Exception
/* 1065:     */   {
/* 1066:1797 */     if (args.length > 0) {
/* 1067:1799 */       if (args[0].toLowerCase().endsWith(".xml"))
/* 1068:     */       {
/* 1069:1800 */         System.out.println(new XMLSerialization().read(args[0]).toString());
/* 1070:     */       }
/* 1071:     */       else
/* 1072:     */       {
/* 1073:1805 */         FileInputStream fi = new FileInputStream(args[0]);
/* 1074:1806 */         ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/* 1075:     */         
/* 1076:1808 */         Object o = oi.readObject();
/* 1077:1809 */         oi.close();
/* 1078:     */         
/* 1079:     */ 
/* 1080:1812 */         new XMLSerialization().write(new BufferedOutputStream(new FileOutputStream(args[0] + ".xml")), o);
/* 1081:     */         
/* 1082:     */ 
/* 1083:1815 */         FileOutputStream fo = new FileOutputStream(args[0] + ".exp");
/* 1084:1816 */         ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/* 1085:     */         
/* 1086:1818 */         oo.writeObject(o);
/* 1087:1819 */         oo.close();
/* 1088:     */       }
/* 1089:     */     }
/* 1090:     */   }
/* 1091:     */   
/* 1092:     */   public String getRevision()
/* 1093:     */   {
/* 1094:1831 */     return RevisionUtils.extract("$Revision: 11102 $");
/* 1095:     */   }
/* 1096:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.xml.XMLSerialization
 * JD-Core Version:    0.7.0.1
 */