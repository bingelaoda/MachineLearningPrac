/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.HashSet;
/*    8:     */ import java.util.Iterator;
/*    9:     */ import java.util.List;
/*   10:     */ import java.util.Properties;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.classifiers.UpdateableClassifier;
/*   13:     */ import weka.clusterers.UpdateableClusterer;
/*   14:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   15:     */ 
/*   16:     */ public class Capabilities
/*   17:     */   implements Cloneable, Serializable, RevisionHandler
/*   18:     */ {
/*   19:     */   static final long serialVersionUID = -5478590032325567849L;
/*   20:     */   public static final String PROPERTIES_FILE = "weka/core/Capabilities.props";
/*   21:     */   protected static Properties PROPERTIES;
/*   22:     */   private static final int ATTRIBUTE = 1;
/*   23:     */   private static final int CLASS = 2;
/*   24:     */   private static final int ATTRIBUTE_CAPABILITY = 4;
/*   25:     */   private static final int CLASS_CAPABILITY = 8;
/*   26:     */   private static final int OTHER_CAPABILITY = 16;
/*   27:     */   protected CapabilitiesHandler m_Owner;
/*   28:     */   protected HashSet<Capability> m_Capabilities;
/*   29:     */   protected HashSet<Capability> m_Dependencies;
/*   30:     */   
/*   31:     */   public static enum Capability
/*   32:     */   {
/*   33: 104 */     NOMINAL_ATTRIBUTES(5, "Nominal attributes"),  BINARY_ATTRIBUTES(5, "Binary attributes"),  UNARY_ATTRIBUTES(5, "Unary attributes"),  EMPTY_NOMINAL_ATTRIBUTES(5, "Empty nominal attributes"),  NUMERIC_ATTRIBUTES(5, "Numeric attributes"),  DATE_ATTRIBUTES(5, "Date attributes"),  STRING_ATTRIBUTES(5, "String attributes"),  RELATIONAL_ATTRIBUTES(5, "Relational attributes"),  MISSING_VALUES(4, "Missing values"),  NO_CLASS(8, "No class"),  NOMINAL_CLASS(10, "Nominal class"),  BINARY_CLASS(10, "Binary class"),  UNARY_CLASS(10, "Unary class"),  EMPTY_NOMINAL_CLASS(10, "Empty nominal class"),  NUMERIC_CLASS(10, "Numeric class"),  DATE_CLASS(10, "Date class"),  STRING_CLASS(10, "String class"),  RELATIONAL_CLASS(10, "Relational class"),  MISSING_CLASS_VALUES(8, "Missing class values"),  ONLY_MULTIINSTANCE(16, "Only multi-Instance data");
/*   34:     */     
/*   35: 149 */     private int m_Flags = 0;
/*   36:     */     private String m_Display;
/*   37:     */     
/*   38:     */     private Capability(int flags, String display)
/*   39:     */     {
/*   40: 161 */       this.m_Flags = flags;
/*   41: 162 */       this.m_Display = display;
/*   42:     */     }
/*   43:     */     
/*   44:     */     public boolean isAttribute()
/*   45:     */     {
/*   46: 171 */       return (this.m_Flags & 0x1) == 1;
/*   47:     */     }
/*   48:     */     
/*   49:     */     public boolean isClass()
/*   50:     */     {
/*   51: 180 */       return (this.m_Flags & 0x2) == 2;
/*   52:     */     }
/*   53:     */     
/*   54:     */     public boolean isAttributeCapability()
/*   55:     */     {
/*   56: 189 */       return (this.m_Flags & 0x4) == 4;
/*   57:     */     }
/*   58:     */     
/*   59:     */     public boolean isOtherCapability()
/*   60:     */     {
/*   61: 198 */       return (this.m_Flags & 0x10) == 16;
/*   62:     */     }
/*   63:     */     
/*   64:     */     public boolean isClassCapability()
/*   65:     */     {
/*   66: 207 */       return (this.m_Flags & 0x8) == 8;
/*   67:     */     }
/*   68:     */     
/*   69:     */     public String toString()
/*   70:     */     {
/*   71: 217 */       return this.m_Display;
/*   72:     */     }
/*   73:     */   }
/*   74:     */   
/*   75: 231 */   protected Exception m_FailReason = null;
/*   76: 234 */   protected int m_MinimumNumberInstances = 1;
/*   77:     */   protected boolean m_Test;
/*   78:     */   protected boolean m_InstancesTest;
/*   79:     */   protected boolean m_AttributeTest;
/*   80:     */   protected boolean m_MissingValuesTest;
/*   81:     */   protected boolean m_MissingClassValuesTest;
/*   82:     */   protected boolean m_MinimumNumberInstancesTest;
/*   83:     */   
/*   84:     */   public Capabilities(CapabilitiesHandler owner)
/*   85:     */   {
/*   86: 262 */     setOwner(owner);
/*   87:     */     
/*   88: 264 */     this.m_Capabilities = new HashSet();
/*   89: 265 */     this.m_Dependencies = new HashSet();
/*   90: 268 */     if (doNotCheckCapabilities()) {
/*   91: 269 */       return;
/*   92:     */     }
/*   93: 273 */     if (PROPERTIES == null) {
/*   94:     */       try
/*   95:     */       {
/*   96: 275 */         PROPERTIES = Utils.readProperties("weka/core/Capabilities.props");
/*   97:     */       }
/*   98:     */       catch (Exception e)
/*   99:     */       {
/*  100: 277 */         e.printStackTrace();
/*  101: 278 */         PROPERTIES = new Properties();
/*  102:     */       }
/*  103:     */     }
/*  104: 282 */     this.m_Test = Boolean.parseBoolean(PROPERTIES.getProperty("Test", "true"));
/*  105: 283 */     this.m_InstancesTest = ((Boolean.parseBoolean(PROPERTIES.getProperty("InstancesTest", "true"))) && (this.m_Test));
/*  106:     */     
/*  107: 285 */     this.m_AttributeTest = ((Boolean.parseBoolean(PROPERTIES.getProperty("AttributeTest", "true"))) && (this.m_Test));
/*  108:     */     
/*  109: 287 */     this.m_MissingValuesTest = ((Boolean.parseBoolean(PROPERTIES.getProperty("MissingValuesTest", "true"))) && (this.m_Test));
/*  110:     */     
/*  111: 289 */     this.m_MissingClassValuesTest = ((Boolean.parseBoolean(PROPERTIES.getProperty("MissingClassValuesTest", "true"))) && (this.m_Test));
/*  112:     */     
/*  113: 291 */     this.m_MinimumNumberInstancesTest = ((Boolean.parseBoolean(PROPERTIES.getProperty("MinimumNumberInstancesTest", "true"))) && (this.m_Test));
/*  114: 294 */     if (((owner instanceof UpdateableClassifier)) || ((owner instanceof UpdateableClusterer))) {
/*  115: 296 */       setMinimumNumberInstances(0);
/*  116:     */     }
/*  117:     */   }
/*  118:     */   
/*  119:     */   public boolean doNotCheckCapabilities()
/*  120:     */   {
/*  121: 307 */     if ((getOwner() != null) && ((getOwner() instanceof CapabilitiesIgnorer))) {
/*  122: 308 */       return ((CapabilitiesIgnorer)getOwner()).getDoNotCheckCapabilities();
/*  123:     */     }
/*  124: 310 */     return false;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public Object clone()
/*  128:     */   {
/*  129: 323 */     Capabilities result = new Capabilities(this.m_Owner);
/*  130: 324 */     result.assign(this);
/*  131:     */     
/*  132: 326 */     return result;
/*  133:     */   }
/*  134:     */   
/*  135:     */   public void assign(Capabilities c)
/*  136:     */   {
/*  137: 337 */     if (doNotCheckCapabilities()) {
/*  138: 338 */       return;
/*  139:     */     }
/*  140: 341 */     for (Capability cap : Capability.values())
/*  141:     */     {
/*  142: 343 */       if (c.handles(cap)) {
/*  143: 344 */         enable(cap);
/*  144:     */       } else {
/*  145: 346 */         disable(cap);
/*  146:     */       }
/*  147: 349 */       if (c.hasDependency(cap)) {
/*  148: 350 */         enableDependency(cap);
/*  149:     */       } else {
/*  150: 352 */         disableDependency(cap);
/*  151:     */       }
/*  152:     */     }
/*  153: 356 */     setMinimumNumberInstances(c.getMinimumNumberInstances());
/*  154:     */   }
/*  155:     */   
/*  156:     */   public void and(Capabilities c)
/*  157:     */   {
/*  158: 368 */     if (doNotCheckCapabilities()) {
/*  159: 369 */       return;
/*  160:     */     }
/*  161: 372 */     for (Capability cap : Capability.values())
/*  162:     */     {
/*  163: 374 */       if ((handles(cap)) && (c.handles(cap))) {
/*  164: 375 */         this.m_Capabilities.add(cap);
/*  165:     */       } else {
/*  166: 377 */         this.m_Capabilities.remove(cap);
/*  167:     */       }
/*  168: 380 */       if ((hasDependency(cap)) && (c.hasDependency(cap))) {
/*  169: 381 */         this.m_Dependencies.add(cap);
/*  170:     */       } else {
/*  171: 383 */         this.m_Dependencies.remove(cap);
/*  172:     */       }
/*  173:     */     }
/*  174: 388 */     if (c.getMinimumNumberInstances() > getMinimumNumberInstances()) {
/*  175: 389 */       setMinimumNumberInstances(c.getMinimumNumberInstances());
/*  176:     */     }
/*  177:     */   }
/*  178:     */   
/*  179:     */   public void or(Capabilities c)
/*  180:     */   {
/*  181: 402 */     if (doNotCheckCapabilities()) {
/*  182: 403 */       return;
/*  183:     */     }
/*  184: 406 */     for (Capability cap : Capability.values())
/*  185:     */     {
/*  186: 408 */       if ((handles(cap)) || (c.handles(cap))) {
/*  187: 409 */         this.m_Capabilities.add(cap);
/*  188:     */       } else {
/*  189: 411 */         this.m_Capabilities.remove(cap);
/*  190:     */       }
/*  191: 414 */       if ((hasDependency(cap)) || (c.hasDependency(cap))) {
/*  192: 415 */         this.m_Dependencies.add(cap);
/*  193:     */       } else {
/*  194: 417 */         this.m_Dependencies.remove(cap);
/*  195:     */       }
/*  196:     */     }
/*  197: 421 */     if (c.getMinimumNumberInstances() < getMinimumNumberInstances()) {
/*  198: 422 */       setMinimumNumberInstances(c.getMinimumNumberInstances());
/*  199:     */     }
/*  200:     */   }
/*  201:     */   
/*  202:     */   public boolean supports(Capabilities c)
/*  203:     */   {
/*  204: 436 */     if (doNotCheckCapabilities()) {
/*  205: 437 */       return true;
/*  206:     */     }
/*  207: 442 */     boolean result = true;
/*  208: 444 */     for (Capability cap : Capability.values()) {
/*  209: 445 */       if ((c.handles(cap)) && (!handles(cap)))
/*  210:     */       {
/*  211: 446 */         result = false;
/*  212: 447 */         break;
/*  213:     */       }
/*  214:     */     }
/*  215: 451 */     return result;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public boolean supportsMaybe(Capabilities c)
/*  219:     */   {
/*  220: 466 */     if (doNotCheckCapabilities()) {
/*  221: 467 */       return true;
/*  222:     */     }
/*  223: 472 */     boolean result = true;
/*  224: 474 */     for (Capability cap : Capability.values()) {
/*  225: 475 */       if ((c.handles(cap)) && (!handles(cap)) && (!hasDependency(cap)))
/*  226:     */       {
/*  227: 476 */         result = false;
/*  228: 477 */         break;
/*  229:     */       }
/*  230:     */     }
/*  231: 481 */     return result;
/*  232:     */   }
/*  233:     */   
/*  234:     */   public void setOwner(CapabilitiesHandler value)
/*  235:     */   {
/*  236: 490 */     this.m_Owner = value;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public CapabilitiesHandler getOwner()
/*  240:     */   {
/*  241: 499 */     return this.m_Owner;
/*  242:     */   }
/*  243:     */   
/*  244:     */   public void setMinimumNumberInstances(int value)
/*  245:     */   {
/*  246: 508 */     if (value >= 0) {
/*  247: 509 */       this.m_MinimumNumberInstances = value;
/*  248:     */     }
/*  249:     */   }
/*  250:     */   
/*  251:     */   public int getMinimumNumberInstances()
/*  252:     */   {
/*  253: 519 */     return this.m_MinimumNumberInstances;
/*  254:     */   }
/*  255:     */   
/*  256:     */   public Iterator<Capability> capabilities()
/*  257:     */   {
/*  258: 529 */     return this.m_Capabilities.iterator();
/*  259:     */   }
/*  260:     */   
/*  261:     */   public Iterator<Capability> dependencies()
/*  262:     */   {
/*  263: 539 */     return this.m_Dependencies.iterator();
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void enable(Capability c)
/*  267:     */   {
/*  268: 556 */     if (doNotCheckCapabilities()) {
/*  269: 557 */       return;
/*  270:     */     }
/*  271: 561 */     if (c == Capability.NOMINAL_ATTRIBUTES) {
/*  272: 562 */       enable(Capability.BINARY_ATTRIBUTES);
/*  273: 563 */     } else if (c == Capability.BINARY_ATTRIBUTES) {
/*  274: 564 */       enable(Capability.UNARY_ATTRIBUTES);
/*  275: 565 */     } else if (c == Capability.UNARY_ATTRIBUTES) {
/*  276: 566 */       enable(Capability.EMPTY_NOMINAL_ATTRIBUTES);
/*  277: 569 */     } else if (c == Capability.NOMINAL_CLASS) {
/*  278: 570 */       enable(Capability.BINARY_CLASS);
/*  279:     */     }
/*  280: 573 */     this.m_Capabilities.add(c);
/*  281:     */   }
/*  282:     */   
/*  283:     */   public void enableDependency(Capability c)
/*  284:     */   {
/*  285: 590 */     if (doNotCheckCapabilities()) {
/*  286: 591 */       return;
/*  287:     */     }
/*  288: 595 */     if (c == Capability.NOMINAL_ATTRIBUTES) {
/*  289: 596 */       enableDependency(Capability.BINARY_ATTRIBUTES);
/*  290: 597 */     } else if (c == Capability.BINARY_ATTRIBUTES) {
/*  291: 598 */       enableDependency(Capability.UNARY_ATTRIBUTES);
/*  292: 599 */     } else if (c == Capability.UNARY_ATTRIBUTES) {
/*  293: 600 */       enableDependency(Capability.EMPTY_NOMINAL_ATTRIBUTES);
/*  294: 603 */     } else if (c == Capability.NOMINAL_CLASS) {
/*  295: 604 */       enableDependency(Capability.BINARY_CLASS);
/*  296:     */     }
/*  297: 607 */     this.m_Dependencies.add(c);
/*  298:     */   }
/*  299:     */   
/*  300:     */   public void enableAllClasses()
/*  301:     */   {
/*  302: 619 */     if (doNotCheckCapabilities()) {
/*  303: 620 */       return;
/*  304:     */     }
/*  305: 623 */     for (Capability cap : Capability.values()) {
/*  306: 624 */       if (cap.isClass()) {
/*  307: 625 */         enable(cap);
/*  308:     */       }
/*  309:     */     }
/*  310:     */   }
/*  311:     */   
/*  312:     */   public void enableAllClassDependencies()
/*  313:     */   {
/*  314: 639 */     if (doNotCheckCapabilities()) {
/*  315: 640 */       return;
/*  316:     */     }
/*  317: 643 */     for (Capability cap : Capability.values()) {
/*  318: 644 */       if (cap.isClass()) {
/*  319: 645 */         enableDependency(cap);
/*  320:     */       }
/*  321:     */     }
/*  322:     */   }
/*  323:     */   
/*  324:     */   public void enableAllAttributes()
/*  325:     */   {
/*  326: 659 */     if (doNotCheckCapabilities()) {
/*  327: 660 */       return;
/*  328:     */     }
/*  329: 663 */     for (Capability cap : Capability.values()) {
/*  330: 664 */       if (cap.isAttribute()) {
/*  331: 665 */         enable(cap);
/*  332:     */       }
/*  333:     */     }
/*  334:     */   }
/*  335:     */   
/*  336:     */   public void enableAllAttributeDependencies()
/*  337:     */   {
/*  338: 679 */     if (doNotCheckCapabilities()) {
/*  339: 680 */       return;
/*  340:     */     }
/*  341: 683 */     for (Capability cap : Capability.values()) {
/*  342: 684 */       if (cap.isAttribute()) {
/*  343: 685 */         enableDependency(cap);
/*  344:     */       }
/*  345:     */     }
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void enableAll()
/*  349:     */   {
/*  350: 696 */     if (doNotCheckCapabilities()) {
/*  351: 697 */       return;
/*  352:     */     }
/*  353: 700 */     enableAllAttributes();
/*  354: 701 */     enableAllAttributeDependencies();
/*  355: 702 */     enableAllClasses();
/*  356: 703 */     enableAllClassDependencies();
/*  357: 704 */     enable(Capability.MISSING_VALUES);
/*  358: 705 */     enable(Capability.MISSING_CLASS_VALUES);
/*  359:     */   }
/*  360:     */   
/*  361:     */   public void disable(Capability c)
/*  362:     */   {
/*  363: 721 */     if (doNotCheckCapabilities()) {
/*  364: 722 */       return;
/*  365:     */     }
/*  366: 726 */     if (c == Capability.NOMINAL_ATTRIBUTES) {
/*  367: 727 */       disable(Capability.BINARY_ATTRIBUTES);
/*  368: 728 */     } else if (c == Capability.BINARY_ATTRIBUTES) {
/*  369: 729 */       disable(Capability.UNARY_ATTRIBUTES);
/*  370: 730 */     } else if (c == Capability.UNARY_ATTRIBUTES) {
/*  371: 731 */       disable(Capability.EMPTY_NOMINAL_ATTRIBUTES);
/*  372: 734 */     } else if (c == Capability.NOMINAL_CLASS) {
/*  373: 735 */       disable(Capability.BINARY_CLASS);
/*  374: 736 */     } else if (c == Capability.BINARY_CLASS) {
/*  375: 737 */       disable(Capability.UNARY_CLASS);
/*  376: 738 */     } else if (c == Capability.UNARY_CLASS) {
/*  377: 739 */       disable(Capability.EMPTY_NOMINAL_CLASS);
/*  378:     */     }
/*  379: 742 */     this.m_Capabilities.remove(c);
/*  380:     */   }
/*  381:     */   
/*  382:     */   public void disableDependency(Capability c)
/*  383:     */   {
/*  384: 758 */     if (doNotCheckCapabilities()) {
/*  385: 759 */       return;
/*  386:     */     }
/*  387: 763 */     if (c == Capability.NOMINAL_ATTRIBUTES) {
/*  388: 764 */       disableDependency(Capability.BINARY_ATTRIBUTES);
/*  389: 765 */     } else if (c == Capability.BINARY_ATTRIBUTES) {
/*  390: 766 */       disableDependency(Capability.UNARY_ATTRIBUTES);
/*  391: 767 */     } else if (c == Capability.UNARY_ATTRIBUTES) {
/*  392: 768 */       disableDependency(Capability.EMPTY_NOMINAL_ATTRIBUTES);
/*  393: 771 */     } else if (c == Capability.NOMINAL_CLASS) {
/*  394: 772 */       disableDependency(Capability.BINARY_CLASS);
/*  395: 773 */     } else if (c == Capability.BINARY_CLASS) {
/*  396: 774 */       disableDependency(Capability.UNARY_CLASS);
/*  397: 775 */     } else if (c == Capability.UNARY_CLASS) {
/*  398: 776 */       disableDependency(Capability.EMPTY_NOMINAL_CLASS);
/*  399:     */     }
/*  400: 779 */     this.m_Dependencies.remove(c);
/*  401:     */   }
/*  402:     */   
/*  403:     */   public void disableAllClasses()
/*  404:     */   {
/*  405: 791 */     if (doNotCheckCapabilities()) {
/*  406: 792 */       return;
/*  407:     */     }
/*  408: 795 */     for (Capability cap : Capability.values()) {
/*  409: 796 */       if (cap.isClass()) {
/*  410: 797 */         disable(cap);
/*  411:     */       }
/*  412:     */     }
/*  413:     */   }
/*  414:     */   
/*  415:     */   public void disableAllClassDependencies()
/*  416:     */   {
/*  417: 811 */     if (doNotCheckCapabilities()) {
/*  418: 812 */       return;
/*  419:     */     }
/*  420: 815 */     for (Capability cap : Capability.values()) {
/*  421: 816 */       if (cap.isClass()) {
/*  422: 817 */         disableDependency(cap);
/*  423:     */       }
/*  424:     */     }
/*  425:     */   }
/*  426:     */   
/*  427:     */   public void disableAllAttributes()
/*  428:     */   {
/*  429: 831 */     if (doNotCheckCapabilities()) {
/*  430: 832 */       return;
/*  431:     */     }
/*  432: 835 */     for (Capability cap : Capability.values()) {
/*  433: 836 */       if (cap.isAttribute()) {
/*  434: 837 */         disable(cap);
/*  435:     */       }
/*  436:     */     }
/*  437:     */   }
/*  438:     */   
/*  439:     */   public void disableAllAttributeDependencies()
/*  440:     */   {
/*  441: 851 */     if (doNotCheckCapabilities()) {
/*  442: 852 */       return;
/*  443:     */     }
/*  444: 855 */     for (Capability cap : Capability.values()) {
/*  445: 856 */       if (cap.isAttribute()) {
/*  446: 857 */         disableDependency(cap);
/*  447:     */       }
/*  448:     */     }
/*  449:     */   }
/*  450:     */   
/*  451:     */   public void disableAll()
/*  452:     */   {
/*  453: 868 */     if (doNotCheckCapabilities()) {
/*  454: 869 */       return;
/*  455:     */     }
/*  456: 872 */     disableAllAttributes();
/*  457: 873 */     disableAllAttributeDependencies();
/*  458: 874 */     disableAllClasses();
/*  459: 875 */     disableAllClassDependencies();
/*  460: 876 */     disable(Capability.MISSING_VALUES);
/*  461: 877 */     disable(Capability.MISSING_CLASS_VALUES);
/*  462: 878 */     disable(Capability.NO_CLASS);
/*  463:     */   }
/*  464:     */   
/*  465:     */   public Capabilities getClassCapabilities()
/*  466:     */   {
/*  467: 891 */     Capabilities result = new Capabilities(getOwner());
/*  468: 893 */     for (Capability cap : Capability.values()) {
/*  469: 894 */       if ((cap.isClassCapability()) && 
/*  470: 895 */         (handles(cap))) {
/*  471: 896 */         result.m_Capabilities.add(cap);
/*  472:     */       }
/*  473:     */     }
/*  474: 901 */     return result;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public Capabilities getAttributeCapabilities()
/*  478:     */   {
/*  479: 914 */     Capabilities result = new Capabilities(getOwner());
/*  480: 916 */     for (Capability cap : Capability.values()) {
/*  481: 917 */       if ((cap.isAttributeCapability()) && 
/*  482: 918 */         (handles(cap))) {
/*  483: 919 */         result.m_Capabilities.add(cap);
/*  484:     */       }
/*  485:     */     }
/*  486: 924 */     return result;
/*  487:     */   }
/*  488:     */   
/*  489:     */   public Capabilities getOtherCapabilities()
/*  490:     */   {
/*  491: 935 */     Capabilities result = new Capabilities(getOwner());
/*  492: 937 */     for (Capability cap : Capability.values()) {
/*  493: 938 */       if ((cap.isOtherCapability()) && 
/*  494: 939 */         (handles(cap))) {
/*  495: 940 */         result.m_Capabilities.add(cap);
/*  496:     */       }
/*  497:     */     }
/*  498: 945 */     return result;
/*  499:     */   }
/*  500:     */   
/*  501:     */   public boolean handles(Capability c)
/*  502:     */   {
/*  503: 957 */     if (doNotCheckCapabilities()) {
/*  504: 958 */       return true;
/*  505:     */     }
/*  506: 961 */     return this.m_Capabilities.contains(c);
/*  507:     */   }
/*  508:     */   
/*  509:     */   public boolean hasDependency(Capability c)
/*  510:     */   {
/*  511: 974 */     if (doNotCheckCapabilities()) {
/*  512: 975 */       return false;
/*  513:     */     }
/*  514: 978 */     return this.m_Dependencies.contains(c);
/*  515:     */   }
/*  516:     */   
/*  517:     */   public boolean hasDependencies()
/*  518:     */   {
/*  519: 989 */     if (doNotCheckCapabilities()) {
/*  520: 990 */       return false;
/*  521:     */     }
/*  522: 993 */     return this.m_Dependencies.size() > 0;
/*  523:     */   }
/*  524:     */   
/*  525:     */   public Exception getFailReason()
/*  526:     */   {
/*  527:1004 */     if (doNotCheckCapabilities()) {
/*  528:1005 */       return null;
/*  529:     */     }
/*  530:1008 */     return this.m_FailReason;
/*  531:     */   }
/*  532:     */   
/*  533:     */   protected String createMessage(String msg)
/*  534:     */   {
/*  535:1021 */     String result = "";
/*  536:1023 */     if (getOwner() != null) {
/*  537:1024 */       result = getOwner().getClass().getName();
/*  538:     */     } else {
/*  539:1026 */       result = "<anonymous>";
/*  540:     */     }
/*  541:1029 */     result = result + ": " + msg;
/*  542:     */     
/*  543:1031 */     return result;
/*  544:     */   }
/*  545:     */   
/*  546:     */   public boolean test(Attribute att)
/*  547:     */   {
/*  548:1044 */     return test(att, false);
/*  549:     */   }
/*  550:     */   
/*  551:     */   public boolean test(Attribute att, boolean isClass)
/*  552:     */   {
/*  553:1059 */     if (doNotCheckCapabilities()) {
/*  554:1060 */       return true;
/*  555:     */     }
/*  556:1070 */     boolean result = true;
/*  557:1073 */     if (!this.m_AttributeTest) {
/*  558:1074 */       return result;
/*  559:     */     }
/*  560:     */     String errorStr;
/*  561:     */     String errorStr;
/*  562:1078 */     if (isClass) {
/*  563:1079 */       errorStr = "class";
/*  564:     */     } else {
/*  565:1081 */       errorStr = "attributes";
/*  566:     */     }
/*  567:     */     Capability cap;
/*  568:     */     Capability cap;
/*  569:     */     Capability cap;
/*  570:     */     Capability cap;
/*  571:1084 */     switch (att.type())
/*  572:     */     {
/*  573:     */     case 1: 
/*  574:     */       Capability capEmpty;
/*  575:     */       Capability capBinary;
/*  576:     */       Capability capUnary;
/*  577:     */       Capability capEmpty;
/*  578:1086 */       if (isClass)
/*  579:     */       {
/*  580:1087 */         Capability cap = Capability.NOMINAL_CLASS;
/*  581:1088 */         Capability capBinary = Capability.BINARY_CLASS;
/*  582:1089 */         Capability capUnary = Capability.UNARY_CLASS;
/*  583:1090 */         capEmpty = Capability.EMPTY_NOMINAL_CLASS;
/*  584:     */       }
/*  585:     */       else
/*  586:     */       {
/*  587:1092 */         cap = Capability.NOMINAL_ATTRIBUTES;
/*  588:1093 */         capBinary = Capability.BINARY_ATTRIBUTES;
/*  589:1094 */         capUnary = Capability.UNARY_ATTRIBUTES;
/*  590:1095 */         capEmpty = Capability.EMPTY_NOMINAL_ATTRIBUTES;
/*  591:     */       }
/*  592:1098 */       if ((!handles(cap)) || (att.numValues() <= 2)) {
/*  593:1100 */         if ((!handles(capBinary)) || (att.numValues() != 2)) {
/*  594:1102 */           if ((!handles(capUnary)) || (att.numValues() != 1)) {
/*  595:1104 */             if ((!handles(capEmpty)) || (att.numValues() != 0))
/*  596:     */             {
/*  597:1108 */               if (att.numValues() == 0)
/*  598:     */               {
/*  599:1109 */                 this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle empty nominal " + errorStr + "!"));
/*  600:     */                 
/*  601:1111 */                 result = false;
/*  602:     */               }
/*  603:1113 */               if (att.numValues() == 1)
/*  604:     */               {
/*  605:1114 */                 this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle unary " + errorStr + "!"));
/*  606:     */                 
/*  607:1116 */                 result = false;
/*  608:     */               }
/*  609:1117 */               else if (att.numValues() == 2)
/*  610:     */               {
/*  611:1118 */                 this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle binary " + errorStr + "!"));
/*  612:     */                 
/*  613:1120 */                 result = false;
/*  614:     */               }
/*  615:     */               else
/*  616:     */               {
/*  617:1122 */                 this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle multi-valued nominal " + errorStr + "!"));
/*  618:     */                 
/*  619:1124 */                 result = false;
/*  620:     */               }
/*  621:     */             }
/*  622:     */           }
/*  623:     */         }
/*  624:     */       }
/*  625:1126 */       break;
/*  626:     */     case 0: 
/*  627:1129 */       if (isClass) {
/*  628:1130 */         cap = Capability.NUMERIC_CLASS;
/*  629:     */       } else {
/*  630:1132 */         cap = Capability.NUMERIC_ATTRIBUTES;
/*  631:     */       }
/*  632:1135 */       if (!handles(cap))
/*  633:     */       {
/*  634:1136 */         this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle numeric " + errorStr + "!"));
/*  635:     */         
/*  636:1138 */         result = false;
/*  637:     */       }
/*  638:     */       break;
/*  639:     */     case 3: 
/*  640:1143 */       if (isClass) {
/*  641:1144 */         cap = Capability.DATE_CLASS;
/*  642:     */       } else {
/*  643:1146 */         cap = Capability.DATE_ATTRIBUTES;
/*  644:     */       }
/*  645:1149 */       if (!handles(cap))
/*  646:     */       {
/*  647:1150 */         this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle date " + errorStr + "!"));
/*  648:     */         
/*  649:1152 */         result = false;
/*  650:     */       }
/*  651:     */       break;
/*  652:     */     case 2: 
/*  653:1157 */       if (isClass) {
/*  654:1158 */         cap = Capability.STRING_CLASS;
/*  655:     */       } else {
/*  656:1160 */         cap = Capability.STRING_ATTRIBUTES;
/*  657:     */       }
/*  658:1163 */       if (!handles(cap))
/*  659:     */       {
/*  660:1164 */         this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle string " + errorStr + "!"));
/*  661:     */         
/*  662:1166 */         result = false;
/*  663:     */       }
/*  664:     */       break;
/*  665:     */     case 4: 
/*  666:     */       Capability cap;
/*  667:1171 */       if (isClass) {
/*  668:1172 */         cap = Capability.RELATIONAL_CLASS;
/*  669:     */       } else {
/*  670:1174 */         cap = Capability.RELATIONAL_ATTRIBUTES;
/*  671:     */       }
/*  672:1177 */       if (!handles(cap))
/*  673:     */       {
/*  674:1178 */         this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle relational " + errorStr + "!"));
/*  675:     */         
/*  676:1180 */         result = false;
/*  677:     */       }
/*  678:     */       break;
/*  679:     */     default: 
/*  680:1187 */       this.m_FailReason = new UnsupportedAttributeTypeException(createMessage("Cannot handle unknown attribute type '" + att.type() + "'!"));
/*  681:     */       
/*  682:     */ 
/*  683:1190 */       result = false;
/*  684:     */     }
/*  685:1193 */     return result;
/*  686:     */   }
/*  687:     */   
/*  688:     */   public boolean test(Instances data)
/*  689:     */   {
/*  690:1208 */     return test(data, 0, data.numAttributes() - 1);
/*  691:     */   }
/*  692:     */   
/*  693:     */   public boolean test(Instances data, int fromIndex, int toIndex)
/*  694:     */   {
/*  695:1231 */     if (doNotCheckCapabilities()) {
/*  696:1232 */       return true;
/*  697:     */     }
/*  698:1246 */     if (!this.m_InstancesTest) {
/*  699:1247 */       return true;
/*  700:     */     }
/*  701:1251 */     if ((this.m_Capabilities.size() == 0) || ((this.m_Capabilities.size() == 1) && (handles(Capability.NO_CLASS)))) {
/*  702:1253 */       System.err.println(createMessage("No capabilities set!"));
/*  703:     */     }
/*  704:1257 */     if (toIndex - fromIndex < 0)
/*  705:     */     {
/*  706:1258 */       this.m_FailReason = new WekaException(createMessage("No attributes!"));
/*  707:1259 */       return false;
/*  708:     */     }
/*  709:1264 */     boolean testClass = (data.classIndex() > -1) && (data.classIndex() >= fromIndex) && (data.classIndex() <= toIndex);
/*  710:1268 */     for (int i = fromIndex; i <= toIndex; i++)
/*  711:     */     {
/*  712:1269 */       Attribute att = data.attribute(i);
/*  713:1272 */       if (i != data.classIndex()) {
/*  714:1277 */         if (!test(att)) {
/*  715:1278 */           return false;
/*  716:     */         }
/*  717:     */       }
/*  718:     */     }
/*  719:1283 */     if ((!handles(Capability.NO_CLASS)) && (data.classIndex() == -1))
/*  720:     */     {
/*  721:1284 */       this.m_FailReason = new UnassignedClassException(createMessage("Class attribute not set!"));
/*  722:     */       
/*  723:1286 */       return false;
/*  724:     */     }
/*  725:1290 */     if ((handles(Capability.NO_CLASS)) && (data.classIndex() > -1))
/*  726:     */     {
/*  727:1291 */       Capabilities cap = getClassCapabilities();
/*  728:1292 */       cap.disable(Capability.NO_CLASS);
/*  729:1293 */       Iterator<Capability> iter = cap.capabilities();
/*  730:1294 */       if (!iter.hasNext())
/*  731:     */       {
/*  732:1295 */         this.m_FailReason = new WekaException(createMessage("Cannot handle any class attribute!"));
/*  733:     */         
/*  734:1297 */         return false;
/*  735:     */       }
/*  736:     */     }
/*  737:1301 */     if ((testClass) && (!handles(Capability.NO_CLASS)))
/*  738:     */     {
/*  739:1302 */       Attribute att = data.classAttribute();
/*  740:1303 */       if (!test(att, true)) {
/*  741:1304 */         return false;
/*  742:     */       }
/*  743:1311 */       if (this.m_MissingClassValuesTest)
/*  744:     */       {
/*  745:1312 */         if (!handles(Capability.MISSING_CLASS_VALUES)) {
/*  746:1313 */           for (i = 0; i < data.numInstances(); i++) {
/*  747:1314 */             if (data.instance(i).classIsMissing())
/*  748:     */             {
/*  749:1315 */               this.m_FailReason = new WekaException(createMessage("Cannot handle missing class values!"));
/*  750:     */               
/*  751:1317 */               return false;
/*  752:     */             }
/*  753:     */           }
/*  754:     */         }
/*  755:1321 */         if (this.m_MinimumNumberInstancesTest)
/*  756:     */         {
/*  757:1322 */           int hasClass = 0;
/*  758:1324 */           for (i = 0; i < data.numInstances(); i++) {
/*  759:1325 */             if (!data.instance(i).classIsMissing()) {
/*  760:1326 */               hasClass++;
/*  761:     */             }
/*  762:     */           }
/*  763:1331 */           if (hasClass < getMinimumNumberInstances())
/*  764:     */           {
/*  765:1332 */             this.m_FailReason = new WekaException(createMessage("Not enough training instances with class labels (required: " + getMinimumNumberInstances() + ", provided: " + hasClass + ")!"));
/*  766:     */             
/*  767:     */ 
/*  768:     */ 
/*  769:     */ 
/*  770:     */ 
/*  771:1338 */             return false;
/*  772:     */           }
/*  773:     */         }
/*  774:     */       }
/*  775:     */     }
/*  776:1346 */     if ((this.m_MissingValuesTest) && 
/*  777:1347 */       (!handles(Capability.MISSING_VALUES)))
/*  778:     */     {
/*  779:1348 */       boolean missing = false;
/*  780:1349 */       for (i = 0; i < data.numInstances(); i++)
/*  781:     */       {
/*  782:1350 */         Instance inst = data.instance(i);
/*  783:1352 */         if ((inst instanceof SparseInstance)) {
/*  784:1353 */           for (int m = 0; m < inst.numValues(); m++)
/*  785:     */           {
/*  786:1354 */             int n = inst.index(m);
/*  787:1357 */             if (n >= fromIndex)
/*  788:     */             {
/*  789:1360 */               if (n > toIndex) {
/*  790:     */                 break;
/*  791:     */               }
/*  792:1365 */               if (n != inst.classIndex()) {
/*  793:1369 */                 if (inst.isMissing(n))
/*  794:     */                 {
/*  795:1370 */                   missing = true;
/*  796:1371 */                   break;
/*  797:     */                 }
/*  798:     */               }
/*  799:     */             }
/*  800:     */           }
/*  801:     */         }
/*  802:1375 */         for (int n = fromIndex; n <= toIndex; n++) {
/*  803:1377 */           if (n != inst.classIndex()) {
/*  804:1381 */             if (inst.isMissing(n))
/*  805:     */             {
/*  806:1382 */               missing = true;
/*  807:1383 */               break;
/*  808:     */             }
/*  809:     */           }
/*  810:     */         }
/*  811:1388 */         if (missing)
/*  812:     */         {
/*  813:1389 */           this.m_FailReason = new NoSupportForMissingValuesException(createMessage("Cannot handle missing values!"));
/*  814:     */           
/*  815:1391 */           return false;
/*  816:     */         }
/*  817:     */       }
/*  818:     */     }
/*  819:1398 */     if ((this.m_MinimumNumberInstancesTest) && 
/*  820:1399 */       (data.numInstances() < getMinimumNumberInstances()))
/*  821:     */     {
/*  822:1400 */       this.m_FailReason = new WekaException(createMessage("Not enough training instances (required: " + getMinimumNumberInstances() + ", provided: " + data.numInstances() + ")!"));
/*  823:     */       
/*  824:     */ 
/*  825:     */ 
/*  826:1404 */       return false;
/*  827:     */     }
/*  828:1409 */     if (handles(Capability.ONLY_MULTIINSTANCE))
/*  829:     */     {
/*  830:1411 */       if (data.numAttributes() != 3)
/*  831:     */       {
/*  832:1412 */         this.m_FailReason = new WekaException(createMessage("Incorrect Multi-Instance format, must be 'bag-id, bag, class'!"));
/*  833:     */         
/*  834:1414 */         return false;
/*  835:     */       }
/*  836:1418 */       if ((!data.attribute(0).isNominal()) || (!data.attribute(1).isRelationValued()) || (data.classIndex() != data.numAttributes() - 1))
/*  837:     */       {
/*  838:1421 */         this.m_FailReason = new WekaException(createMessage("Incorrect Multi-Instance format, must be 'NOMINAL att, RELATIONAL att, CLASS att'!"));
/*  839:     */         
/*  840:1423 */         return false;
/*  841:     */       }
/*  842:1427 */       if ((getOwner() instanceof MultiInstanceCapabilitiesHandler))
/*  843:     */       {
/*  844:1428 */         MultiInstanceCapabilitiesHandler handler = (MultiInstanceCapabilitiesHandler)getOwner();
/*  845:1429 */         Capabilities cap = handler.getMultiInstanceCapabilities();
/*  846:     */         boolean result;
/*  847:     */         boolean result;
/*  848:1431 */         if ((data.numInstances() > 0) && (data.attribute(1).numValues() > 0)) {
/*  849:1432 */           result = cap.test(data.attribute(1).relation(0));
/*  850:     */         } else {
/*  851:1434 */           result = cap.test(data.attribute(1).relation());
/*  852:     */         }
/*  853:1437 */         if (!result)
/*  854:     */         {
/*  855:1438 */           this.m_FailReason = cap.m_FailReason;
/*  856:1439 */           return false;
/*  857:     */         }
/*  858:     */       }
/*  859:     */     }
/*  860:1445 */     return true;
/*  861:     */   }
/*  862:     */   
/*  863:     */   public void testWithFail(Attribute att)
/*  864:     */     throws Exception
/*  865:     */   {
/*  866:1459 */     test(att, false);
/*  867:     */   }
/*  868:     */   
/*  869:     */   public void testWithFail(Attribute att, boolean isClass)
/*  870:     */     throws Exception
/*  871:     */   {
/*  872:1473 */     if (!test(att, isClass)) {
/*  873:1474 */       throw this.m_FailReason;
/*  874:     */     }
/*  875:     */   }
/*  876:     */   
/*  877:     */   public void testWithFail(Instances data, int fromIndex, int toIndex)
/*  878:     */     throws Exception
/*  879:     */   {
/*  880:1491 */     if (!test(data, fromIndex, toIndex)) {
/*  881:1492 */       throw this.m_FailReason;
/*  882:     */     }
/*  883:     */   }
/*  884:     */   
/*  885:     */   public void testWithFail(Instances data)
/*  886:     */     throws Exception
/*  887:     */   {
/*  888:1506 */     if (!test(data)) {
/*  889:1507 */       throw this.m_FailReason;
/*  890:     */     }
/*  891:     */   }
/*  892:     */   
/*  893:     */   public String toString()
/*  894:     */   {
/*  895:1522 */     StringBuffer result = new StringBuffer();
/*  896:     */     
/*  897:     */ 
/*  898:1525 */     Vector<Capability> sorted = new Vector(this.m_Capabilities);
/*  899:1526 */     Collections.sort(sorted);
/*  900:1527 */     result.append("Capabilities: " + sorted.toString() + "\n");
/*  901:     */     
/*  902:     */ 
/*  903:1530 */     sorted = new Vector(this.m_Dependencies);
/*  904:1531 */     Collections.sort(sorted);
/*  905:1532 */     result.append("Dependencies: " + sorted.toString() + "\n");
/*  906:     */     
/*  907:     */ 
/*  908:1535 */     result.append("min # Instance: " + getMinimumNumberInstances() + "\n");
/*  909:     */     
/*  910:1537 */     return result.toString();
/*  911:     */   }
/*  912:     */   
/*  913:     */   public String toSource(String objectname)
/*  914:     */   {
/*  915:1549 */     return toSource(objectname, 0);
/*  916:     */   }
/*  917:     */   
/*  918:     */   public String toSource(String objectname, int indent)
/*  919:     */   {
/*  920:1569 */     StringBuffer result = new StringBuffer();
/*  921:     */     
/*  922:1571 */     String capsName = Capabilities.class.getName();
/*  923:1572 */     String capName = Capability.class.getName().replaceAll("\\$", ".");
/*  924:     */     
/*  925:1574 */     String indentStr = "";
/*  926:1575 */     for (int i = 0; i < indent; i++) {
/*  927:1576 */       indentStr = indentStr + " ";
/*  928:     */     }
/*  929:1580 */     result.append(indentStr + capsName + " " + objectname + " = new " + capsName + "(this);\n");
/*  930:     */     
/*  931:     */ 
/*  932:1583 */     List<Capability> capsList = new ArrayList();
/*  933:1584 */     boolean hasNominalAtt = false;
/*  934:1585 */     boolean hasBinaryAtt = false;
/*  935:1586 */     boolean hasUnaryAtt = false;
/*  936:1587 */     boolean hasNominalClass = false;
/*  937:     */     
/*  938:1589 */     result.append("\n");
/*  939:1590 */     for (Capability cap : Capability.values()) {
/*  940:1592 */       if (handles(cap))
/*  941:     */       {
/*  942:1593 */         if (cap == Capability.NOMINAL_ATTRIBUTES) {
/*  943:1594 */           hasNominalAtt = true;
/*  944:     */         }
/*  945:1596 */         if (cap == Capability.NOMINAL_CLASS) {
/*  946:1597 */           hasNominalClass = true;
/*  947:     */         }
/*  948:1599 */         if (cap == Capability.BINARY_ATTRIBUTES) {
/*  949:1600 */           hasBinaryAtt = true;
/*  950:     */         }
/*  951:1602 */         if (cap == Capability.UNARY_ATTRIBUTES) {
/*  952:1603 */           hasUnaryAtt = true;
/*  953:     */         }
/*  954:1605 */         if (cap == Capability.EMPTY_NOMINAL_ATTRIBUTES) {}
/*  955:1607 */         capsList.add(cap);
/*  956:     */       }
/*  957:     */     }
/*  958:1611 */     for (Capability cap : capsList) {
/*  959:1612 */       if (((cap != Capability.BINARY_ATTRIBUTES) || (!hasNominalAtt)) && ((cap != Capability.UNARY_ATTRIBUTES) || (!hasBinaryAtt)) && ((cap != Capability.EMPTY_NOMINAL_ATTRIBUTES) || (!hasUnaryAtt)) && ((cap != Capability.BINARY_CLASS) || (!hasNominalClass)))
/*  960:     */       {
/*  961:1618 */         result.append(indentStr + objectname + ".enable(" + capName + "." + cap.name() + ");\n");
/*  962:1621 */         if (hasDependency(cap)) {
/*  963:1622 */           result.append(indentStr + objectname + ".enableDependency(" + capName + "." + cap.name() + ");\n");
/*  964:     */         }
/*  965:     */       }
/*  966:     */     }
/*  967:1628 */     result.append("\n");
/*  968:     */     
/*  969:     */ 
/*  970:1631 */     result.append("\n");
/*  971:1632 */     result.append(indentStr + objectname + ".setMinimumNumberInstances(" + getMinimumNumberInstances() + ");\n");
/*  972:     */     
/*  973:     */ 
/*  974:1635 */     result.append("\n");
/*  975:     */     
/*  976:1637 */     return result.toString();
/*  977:     */   }
/*  978:     */   
/*  979:     */   public static Capabilities forInstances(Instances data)
/*  980:     */     throws Exception
/*  981:     */   {
/*  982:1650 */     return forInstances(data, false);
/*  983:     */   }
/*  984:     */   
/*  985:     */   public static Capabilities forInstances(Instances data, boolean multi)
/*  986:     */     throws Exception
/*  987:     */   {
/*  988:1672 */     Capabilities result = new Capabilities(null);
/*  989:1675 */     if (data.classIndex() == -1)
/*  990:     */     {
/*  991:1676 */       result.enable(Capability.NO_CLASS);
/*  992:     */     }
/*  993:     */     else
/*  994:     */     {
/*  995:1678 */       switch (data.classAttribute().type())
/*  996:     */       {
/*  997:     */       case 1: 
/*  998:1680 */         if (data.classAttribute().numValues() == 1) {
/*  999:1681 */           result.enable(Capability.UNARY_CLASS);
/* 1000:1682 */         } else if (data.classAttribute().numValues() == 2) {
/* 1001:1683 */           result.enable(Capability.BINARY_CLASS);
/* 1002:     */         } else {
/* 1003:1685 */           result.enable(Capability.NOMINAL_CLASS);
/* 1004:     */         }
/* 1005:1687 */         break;
/* 1006:     */       case 0: 
/* 1007:1690 */         result.enable(Capability.NUMERIC_CLASS);
/* 1008:1691 */         break;
/* 1009:     */       case 2: 
/* 1010:1694 */         result.enable(Capability.STRING_CLASS);
/* 1011:1695 */         break;
/* 1012:     */       case 3: 
/* 1013:1698 */         result.enable(Capability.DATE_CLASS);
/* 1014:1699 */         break;
/* 1015:     */       case 4: 
/* 1016:1702 */         result.enable(Capability.RELATIONAL_CLASS);
/* 1017:1703 */         break;
/* 1018:     */       default: 
/* 1019:1706 */         throw new UnsupportedAttributeTypeException("Unknown class attribute type '" + data.classAttribute() + "'!");
/* 1020:     */       }
/* 1021:1711 */       for (int i = 0; i < data.numInstances(); i++) {
/* 1022:1712 */         if (data.instance(i).classIsMissing())
/* 1023:     */         {
/* 1024:1713 */           result.enable(Capability.MISSING_CLASS_VALUES);
/* 1025:1714 */           break;
/* 1026:     */         }
/* 1027:     */       }
/* 1028:     */     }
/* 1029:1720 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 1030:1722 */       if (i != data.classIndex()) {
/* 1031:1726 */         switch (data.attribute(i).type())
/* 1032:     */         {
/* 1033:     */         case 1: 
/* 1034:1728 */           result.enable(Capability.UNARY_ATTRIBUTES);
/* 1035:1729 */           if (data.attribute(i).numValues() == 2) {
/* 1036:1730 */             result.enable(Capability.BINARY_ATTRIBUTES);
/* 1037:1731 */           } else if (data.attribute(i).numValues() > 2) {
/* 1038:1732 */             result.enable(Capability.NOMINAL_ATTRIBUTES);
/* 1039:     */           }
/* 1040:     */           break;
/* 1041:     */         case 0: 
/* 1042:1737 */           result.enable(Capability.NUMERIC_ATTRIBUTES);
/* 1043:1738 */           break;
/* 1044:     */         case 3: 
/* 1045:1741 */           result.enable(Capability.DATE_ATTRIBUTES);
/* 1046:1742 */           break;
/* 1047:     */         case 2: 
/* 1048:1745 */           result.enable(Capability.STRING_ATTRIBUTES);
/* 1049:1746 */           break;
/* 1050:     */         case 4: 
/* 1051:1749 */           result.enable(Capability.RELATIONAL_ATTRIBUTES);
/* 1052:1750 */           break;
/* 1053:     */         default: 
/* 1054:1753 */           throw new UnsupportedAttributeTypeException("Unknown attribute type '" + data.attribute(i).type() + "'!");
/* 1055:     */         }
/* 1056:     */       }
/* 1057:     */     }
/* 1058:1759 */     boolean missing = false;
/* 1059:1760 */     for (i = 0; i < data.numInstances(); i++)
/* 1060:     */     {
/* 1061:1761 */       Instance inst = data.instance(i);
/* 1062:1763 */       if ((inst instanceof SparseInstance)) {
/* 1063:1764 */         for (int m = 0; m < inst.numValues(); m++)
/* 1064:     */         {
/* 1065:1765 */           int n = inst.index(m);
/* 1066:1768 */           if (n != inst.classIndex()) {
/* 1067:1772 */             if (inst.isMissing(n))
/* 1068:     */             {
/* 1069:1773 */               missing = true;
/* 1070:1774 */               break;
/* 1071:     */             }
/* 1072:     */           }
/* 1073:     */         }
/* 1074:     */       }
/* 1075:1778 */       for (int n = 0; n < data.numAttributes(); n++) {
/* 1076:1780 */         if (n != inst.classIndex()) {
/* 1077:1784 */           if (inst.isMissing(n))
/* 1078:     */           {
/* 1079:1785 */             missing = true;
/* 1080:1786 */             break;
/* 1081:     */           }
/* 1082:     */         }
/* 1083:     */       }
/* 1084:1791 */       if (missing)
/* 1085:     */       {
/* 1086:1792 */         result.enable(Capability.MISSING_VALUES);
/* 1087:1793 */         break;
/* 1088:     */       }
/* 1089:     */     }
/* 1090:1798 */     if ((multi) && 
/* 1091:1799 */       (data.numAttributes() == 3) && (data.attribute(0).isNominal()) && (data.attribute(1).isRelationValued()) && (data.classIndex() == data.numAttributes() - 1))
/* 1092:     */     {
/* 1093:1802 */       Capabilities multiInstance = new Capabilities(null);
/* 1094:1803 */       multiInstance.or(result.getClassCapabilities());
/* 1095:1804 */       multiInstance.enable(Capability.NOMINAL_ATTRIBUTES);
/* 1096:1805 */       multiInstance.enable(Capability.RELATIONAL_ATTRIBUTES);
/* 1097:1806 */       multiInstance.enable(Capability.ONLY_MULTIINSTANCE);
/* 1098:1807 */       result.assign(multiInstance);
/* 1099:     */     }
/* 1100:1811 */     return result;
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public static void main(String[] args)
/* 1104:     */     throws Exception
/* 1105:     */   {
/* 1106:1839 */     if (args.length == 0)
/* 1107:     */     {
/* 1108:1840 */       System.out.println("\nUsage: " + Capabilities.class.getName() + " -file <dataset> [-c <class index>]\n");
/* 1109:     */       
/* 1110:1842 */       return;
/* 1111:     */     }
/* 1112:1846 */     String tmpStr = Utils.getOption("file", args);
/* 1113:1847 */     if (tmpStr.length() == 0) {
/* 1114:1848 */       throw new Exception("No file provided with option '-file'!");
/* 1115:     */     }
/* 1116:1850 */     String filename = tmpStr;
/* 1117:     */     
/* 1118:     */ 
/* 1119:1853 */     tmpStr = Utils.getOption("c", args);
/* 1120:     */     int classIndex;
/* 1121:     */     int classIndex;
/* 1122:1854 */     if (tmpStr.length() != 0)
/* 1123:     */     {
/* 1124:     */       int classIndex;
/* 1125:1855 */       if (tmpStr.equals("first"))
/* 1126:     */       {
/* 1127:1856 */         classIndex = 0;
/* 1128:     */       }
/* 1129:     */       else
/* 1130:     */       {
/* 1131:     */         int classIndex;
/* 1132:1857 */         if (tmpStr.equals("last")) {
/* 1133:1858 */           classIndex = -2;
/* 1134:     */         } else {
/* 1135:1860 */           classIndex = Integer.parseInt(tmpStr) - 1;
/* 1136:     */         }
/* 1137:     */       }
/* 1138:     */     }
/* 1139:     */     else
/* 1140:     */     {
/* 1141:1863 */       classIndex = -3;
/* 1142:     */     }
/* 1143:1867 */     ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);
/* 1144:     */     Instances data;
/* 1145:     */     Instances data;
/* 1146:1868 */     if (classIndex == -3)
/* 1147:     */     {
/* 1148:1869 */       data = source.getDataSet();
/* 1149:     */     }
/* 1150:     */     else
/* 1151:     */     {
/* 1152:     */       Instances data;
/* 1153:1870 */       if (classIndex == -2) {
/* 1154:1871 */         data = source.getDataSet(source.getStructure().numAttributes() - 1);
/* 1155:     */       } else {
/* 1156:1873 */         data = source.getDataSet(classIndex);
/* 1157:     */       }
/* 1158:     */     }
/* 1159:1877 */     Capabilities cap = forInstances(data);
/* 1160:1878 */     System.out.println("File: " + filename);
/* 1161:1879 */     System.out.println("Class index: " + (data.classIndex() == -1 ? "not set" : new StringBuilder().append("").append(data.classIndex() + 1).toString()));
/* 1162:     */     
/* 1163:1881 */     System.out.println("Capabilities:");
/* 1164:1882 */     Iterator<Capability> iter = cap.capabilities();
/* 1165:1883 */     while (iter.hasNext()) {
/* 1166:1884 */       System.out.println("- " + iter.next());
/* 1167:     */     }
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public String getRevision()
/* 1171:     */   {
/* 1172:1895 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 1173:     */   }
/* 1174:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Capabilities
 * JD-Core Version:    0.7.0.1
 */