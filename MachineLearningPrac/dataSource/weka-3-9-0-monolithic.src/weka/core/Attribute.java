/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.text.ParseException;
/*    6:     */ import java.text.SimpleDateFormat;
/*    7:     */ import java.util.ArrayList;
/*    8:     */ import java.util.Date;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.Hashtable;
/*   11:     */ import java.util.List;
/*   12:     */ 
/*   13:     */ public class Attribute
/*   14:     */   implements Copyable, Serializable, RevisionHandler
/*   15:     */ {
/*   16:     */   static final long serialVersionUID = -742180568732916383L;
/*   17:     */   public static final int NUMERIC = 0;
/*   18:     */   public static final int NOMINAL = 1;
/*   19:     */   public static final int STRING = 2;
/*   20:     */   public static final int DATE = 3;
/*   21:     */   public static final int RELATIONAL = 4;
/*   22:     */   public static final int ORDERING_SYMBOLIC = 0;
/*   23:     */   public static final int ORDERING_ORDERED = 1;
/*   24:     */   public static final int ORDERING_MODULO = 2;
/*   25:     */   public static final String ARFF_ATTRIBUTE = "@attribute";
/*   26:     */   public static final String ARFF_ATTRIBUTE_INTEGER = "integer";
/*   27:     */   public static final String ARFF_ATTRIBUTE_REAL = "real";
/*   28:     */   public static final String ARFF_ATTRIBUTE_NUMERIC = "numeric";
/*   29:     */   public static final String ARFF_ATTRIBUTE_STRING = "string";
/*   30:     */   public static final String ARFF_ATTRIBUTE_DATE = "date";
/*   31:     */   public static final String ARFF_ATTRIBUTE_RELATIONAL = "relational";
/*   32:     */   public static final String ARFF_END_SUBRELATION = "@end";
/*   33:     */   protected static final int STRING_COMPRESS_THRESHOLD = 200;
/*   34:     */   protected final String m_Name;
/*   35:     */   protected int m_Type;
/*   36:     */   protected AttributeInfo m_AttributeInfo;
/*   37: 157 */   protected int m_Index = -1;
/*   38: 160 */   protected double m_Weight = 1.0D;
/*   39:     */   protected AttributeMetaInfo m_AttributeMetaInfo;
/*   40:     */   
/*   41:     */   public Attribute(String attributeName)
/*   42:     */   {
/*   43: 174 */     this(attributeName, (ProtectedProperties)null);
/*   44:     */   }
/*   45:     */   
/*   46:     */   public Attribute(String attributeName, ProtectedProperties metadata)
/*   47:     */   {
/*   48: 188 */     this.m_Name = attributeName;
/*   49: 189 */     if (metadata != null) {
/*   50: 190 */       this.m_AttributeMetaInfo = new AttributeMetaInfo(metadata, this);
/*   51:     */     }
/*   52:     */   }
/*   53:     */   
/*   54:     */   public Attribute(String attributeName, String dateFormat)
/*   55:     */   {
/*   56: 206 */     this(attributeName, dateFormat, (ProtectedProperties)null);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public Attribute(String attributeName, String dateFormat, ProtectedProperties metadata)
/*   60:     */   {
/*   61: 224 */     this.m_Name = attributeName;
/*   62: 225 */     this.m_Type = 3;
/*   63: 226 */     this.m_AttributeInfo = new DateAttributeInfo(dateFormat);
/*   64: 227 */     if (metadata != null) {
/*   65: 228 */       this.m_AttributeMetaInfo = new AttributeMetaInfo(metadata, this);
/*   66:     */     }
/*   67:     */   }
/*   68:     */   
/*   69:     */   public Attribute(String attributeName, List<String> attributeValues)
/*   70:     */   {
/*   71: 245 */     this(attributeName, attributeValues, (ProtectedProperties)null);
/*   72:     */   }
/*   73:     */   
/*   74:     */   public Attribute(String attributeName, List<String> attributeValues, ProtectedProperties metadata)
/*   75:     */   {
/*   76: 270 */     this.m_Name = attributeName;
/*   77: 271 */     this.m_AttributeInfo = new NominalAttributeInfo(attributeValues, attributeName);
/*   78: 272 */     if (attributeValues == null) {
/*   79: 273 */       this.m_Type = 2;
/*   80:     */     } else {
/*   81: 275 */       this.m_Type = 1;
/*   82:     */     }
/*   83: 277 */     if (metadata != null) {
/*   84: 278 */       this.m_AttributeMetaInfo = new AttributeMetaInfo(metadata, this);
/*   85:     */     }
/*   86:     */   }
/*   87:     */   
/*   88:     */   public Attribute(String attributeName, Instances header)
/*   89:     */   {
/*   90: 290 */     this(attributeName, header, (ProtectedProperties)null);
/*   91:     */   }
/*   92:     */   
/*   93:     */   public Attribute(String attributeName, Instances header, ProtectedProperties metadata)
/*   94:     */   {
/*   95: 303 */     if (header.numInstances() > 0) {
/*   96: 304 */       throw new IllegalArgumentException("Header for relation-valued attribute should not contain any instances");
/*   97:     */     }
/*   98: 307 */     this.m_Name = attributeName;
/*   99: 308 */     this.m_Type = 4;
/*  100: 309 */     this.m_AttributeInfo = new RelationalAttributeInfo(header);
/*  101: 310 */     if (metadata != null) {
/*  102: 311 */       this.m_AttributeMetaInfo = new AttributeMetaInfo(metadata, this);
/*  103:     */     }
/*  104:     */   }
/*  105:     */   
/*  106:     */   public Object copy()
/*  107:     */   {
/*  108: 324 */     return copy(this.m_Name);
/*  109:     */   }
/*  110:     */   
/*  111:     */   public final Enumeration<Object> enumerateValues()
/*  112:     */   {
/*  113: 335 */     if ((isNominal()) || (isString()))
/*  114:     */     {
/*  115: 336 */       final Enumeration<Object> ee = new WekaEnumeration(((NominalAttributeInfo)this.m_AttributeInfo).m_Values);
/*  116:     */       
/*  117: 338 */       new Enumeration()
/*  118:     */       {
/*  119:     */         public boolean hasMoreElements()
/*  120:     */         {
/*  121: 341 */           return ee.hasMoreElements();
/*  122:     */         }
/*  123:     */         
/*  124:     */         public Object nextElement()
/*  125:     */         {
/*  126: 346 */           Object oo = ee.nextElement();
/*  127: 347 */           if ((oo instanceof SerializedObject)) {
/*  128: 348 */             return ((SerializedObject)oo).getObject();
/*  129:     */           }
/*  130: 350 */           return oo;
/*  131:     */         }
/*  132:     */       };
/*  133:     */     }
/*  134: 355 */     return null;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public final boolean equals(Object other)
/*  138:     */   {
/*  139: 366 */     return equalsMsg(other) == null;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public final int hashCode()
/*  143:     */   {
/*  144: 376 */     return name().hashCode();
/*  145:     */   }
/*  146:     */   
/*  147:     */   public final String equalsMsg(Object other)
/*  148:     */   {
/*  149: 388 */     if (other == null) {
/*  150: 389 */       return "Comparing with null object";
/*  151:     */     }
/*  152: 392 */     if (!other.getClass().equals(getClass())) {
/*  153: 393 */       return "Object has wrong class";
/*  154:     */     }
/*  155: 396 */     Attribute att = (Attribute)other;
/*  156: 397 */     if (!this.m_Name.equals(att.m_Name)) {
/*  157: 398 */       return "Names differ: " + this.m_Name + " != " + att.m_Name;
/*  158:     */     }
/*  159: 401 */     if ((isNominal()) && (att.isNominal()))
/*  160:     */     {
/*  161: 402 */       if (((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size() != ((NominalAttributeInfo)att.m_AttributeInfo).m_Values.size()) {
/*  162: 404 */         return "Different number of labels: " + ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size() + " != " + ((NominalAttributeInfo)att.m_AttributeInfo).m_Values.size();
/*  163:     */       }
/*  164: 408 */       for (int i = 0; i < ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size(); i++) {
/*  165: 409 */         if (!((NominalAttributeInfo)this.m_AttributeInfo).m_Values.get(i).equals(((NominalAttributeInfo)att.m_AttributeInfo).m_Values.get(i))) {
/*  166: 411 */           return "Labels differ at position " + (i + 1) + ": " + ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.get(i) + " != " + ((NominalAttributeInfo)att.m_AttributeInfo).m_Values.get(i);
/*  167:     */         }
/*  168:     */       }
/*  169: 417 */       return null;
/*  170:     */     }
/*  171: 420 */     if ((isRelationValued()) && (att.isRelationValued())) {
/*  172: 421 */       return ((RelationalAttributeInfo)this.m_AttributeInfo).m_Header.equalHeadersMsg(((RelationalAttributeInfo)att.m_AttributeInfo).m_Header);
/*  173:     */     }
/*  174: 424 */     if (type() != att.type()) {
/*  175: 425 */       return "Types differ: " + typeToString(this) + " != " + typeToString(att);
/*  176:     */     }
/*  177: 428 */     return null;
/*  178:     */   }
/*  179:     */   
/*  180:     */   public static String typeToString(Attribute att)
/*  181:     */   {
/*  182: 438 */     return typeToString(att.type());
/*  183:     */   }
/*  184:     */   
/*  185:     */   public static String typeToString(int type)
/*  186:     */   {
/*  187:     */     String result;
/*  188: 450 */     switch (type)
/*  189:     */     {
/*  190:     */     case 0: 
/*  191: 452 */       result = "numeric";
/*  192: 453 */       break;
/*  193:     */     case 1: 
/*  194: 456 */       result = "nominal";
/*  195: 457 */       break;
/*  196:     */     case 2: 
/*  197: 460 */       result = "string";
/*  198: 461 */       break;
/*  199:     */     case 3: 
/*  200: 464 */       result = "date";
/*  201: 465 */       break;
/*  202:     */     case 4: 
/*  203: 468 */       result = "relational";
/*  204: 469 */       break;
/*  205:     */     default: 
/*  206: 472 */       result = "unknown(" + type + ")";
/*  207:     */     }
/*  208: 475 */     return result;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public static String typeToStringShort(Attribute att)
/*  212:     */   {
/*  213: 485 */     return typeToStringShort(att.type());
/*  214:     */   }
/*  215:     */   
/*  216:     */   public static String typeToStringShort(int type)
/*  217:     */   {
/*  218:     */     String result;
/*  219: 497 */     switch (type)
/*  220:     */     {
/*  221:     */     case 0: 
/*  222: 499 */       result = "Num";
/*  223: 500 */       break;
/*  224:     */     case 1: 
/*  225: 503 */       result = "Nom";
/*  226: 504 */       break;
/*  227:     */     case 2: 
/*  228: 507 */       result = "Str";
/*  229: 508 */       break;
/*  230:     */     case 3: 
/*  231: 511 */       result = "Dat";
/*  232: 512 */       break;
/*  233:     */     case 4: 
/*  234: 515 */       result = "Rel";
/*  235: 516 */       break;
/*  236:     */     default: 
/*  237: 519 */       result = "???";
/*  238:     */     }
/*  239: 522 */     return result;
/*  240:     */   }
/*  241:     */   
/*  242:     */   public final int index()
/*  243:     */   {
/*  244: 533 */     return this.m_Index;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public final int indexOfValue(String value)
/*  248:     */   {
/*  249: 546 */     if ((!isNominal()) && (!isString())) {
/*  250: 547 */       return -1;
/*  251:     */     }
/*  252: 549 */     Object store = value;
/*  253: 550 */     if (value.length() > 200) {
/*  254:     */       try
/*  255:     */       {
/*  256: 552 */         store = new SerializedObject(value, true);
/*  257:     */       }
/*  258:     */       catch (Exception ex)
/*  259:     */       {
/*  260: 554 */         System.err.println("Couldn't compress string attribute value - searching uncompressed.");
/*  261:     */       }
/*  262:     */     }
/*  263: 558 */     Integer val = (Integer)((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.get(store);
/*  264: 559 */     if (val == null) {
/*  265: 560 */       return -1;
/*  266:     */     }
/*  267: 562 */     return val.intValue();
/*  268:     */   }
/*  269:     */   
/*  270:     */   public final boolean isNominal()
/*  271:     */   {
/*  272: 574 */     return this.m_Type == 1;
/*  273:     */   }
/*  274:     */   
/*  275:     */   public final boolean isNumeric()
/*  276:     */   {
/*  277: 585 */     return (this.m_Type == 0) || (this.m_Type == 3);
/*  278:     */   }
/*  279:     */   
/*  280:     */   public final boolean isRelationValued()
/*  281:     */   {
/*  282: 596 */     return this.m_Type == 4;
/*  283:     */   }
/*  284:     */   
/*  285:     */   public final boolean isString()
/*  286:     */   {
/*  287: 607 */     return this.m_Type == 2;
/*  288:     */   }
/*  289:     */   
/*  290:     */   public final boolean isDate()
/*  291:     */   {
/*  292: 618 */     return this.m_Type == 3;
/*  293:     */   }
/*  294:     */   
/*  295:     */   public final String name()
/*  296:     */   {
/*  297: 629 */     return this.m_Name;
/*  298:     */   }
/*  299:     */   
/*  300:     */   public final int numValues()
/*  301:     */   {
/*  302: 640 */     if ((!isNominal()) && (!isString()) && (!isRelationValued())) {
/*  303: 641 */       return 0;
/*  304:     */     }
/*  305: 643 */     return ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size();
/*  306:     */   }
/*  307:     */   
/*  308:     */   public final String toString()
/*  309:     */   {
/*  310: 656 */     StringBuffer text = new StringBuffer();
/*  311:     */     
/*  312: 658 */     text.append("@attribute").append(" ").append(Utils.quote(this.m_Name)).append(" ");
/*  313: 660 */     switch (this.m_Type)
/*  314:     */     {
/*  315:     */     case 1: 
/*  316: 662 */       text.append('{');
/*  317: 663 */       Enumeration<Object> enu = enumerateValues();
/*  318: 664 */       while (enu.hasMoreElements())
/*  319:     */       {
/*  320: 665 */         text.append(Utils.quote((String)enu.nextElement()));
/*  321: 666 */         if (enu.hasMoreElements()) {
/*  322: 667 */           text.append(',');
/*  323:     */         }
/*  324:     */       }
/*  325: 670 */       text.append('}');
/*  326: 671 */       break;
/*  327:     */     case 0: 
/*  328: 673 */       text.append("numeric");
/*  329: 674 */       break;
/*  330:     */     case 2: 
/*  331: 676 */       text.append("string");
/*  332: 677 */       break;
/*  333:     */     case 3: 
/*  334: 679 */       text.append("date").append(" ").append(Utils.quote(((DateAttributeInfo)this.m_AttributeInfo).m_DateFormat.toPattern()));
/*  335:     */       
/*  336: 681 */       break;
/*  337:     */     case 4: 
/*  338: 683 */       text.append("relational").append("\n");
/*  339: 684 */       Enumeration<Attribute> enm = ((RelationalAttributeInfo)this.m_AttributeInfo).m_Header.enumerateAttributes();
/*  340: 685 */       while (enm.hasMoreElements()) {
/*  341: 686 */         text.append(enm.nextElement()).append("\n");
/*  342:     */       }
/*  343: 688 */       text.append("@end").append(" ").append(Utils.quote(this.m_Name));
/*  344: 689 */       break;
/*  345:     */     default: 
/*  346: 691 */       text.append("UNKNOWN");
/*  347:     */     }
/*  348: 694 */     return text.toString();
/*  349:     */   }
/*  350:     */   
/*  351:     */   public final int type()
/*  352:     */   {
/*  353: 705 */     return this.m_Type;
/*  354:     */   }
/*  355:     */   
/*  356:     */   public final String getDateFormat()
/*  357:     */   {
/*  358: 716 */     if (isDate()) {
/*  359: 717 */       return ((DateAttributeInfo)this.m_AttributeInfo).m_DateFormat.toPattern();
/*  360:     */     }
/*  361: 719 */     return "";
/*  362:     */   }
/*  363:     */   
/*  364:     */   public final String value(int valIndex)
/*  365:     */   {
/*  366: 732 */     if ((!isNominal()) && (!isString())) {
/*  367: 733 */       return "";
/*  368:     */     }
/*  369: 735 */     Object val = ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.get(valIndex);
/*  370: 738 */     if ((val instanceof SerializedObject)) {
/*  371: 739 */       val = ((SerializedObject)val).getObject();
/*  372:     */     }
/*  373: 741 */     return (String)val;
/*  374:     */   }
/*  375:     */   
/*  376:     */   public final Instances relation()
/*  377:     */   {
/*  378: 753 */     if (!isRelationValued()) {
/*  379: 754 */       return null;
/*  380:     */     }
/*  381: 756 */     return ((RelationalAttributeInfo)this.m_AttributeInfo).m_Header;
/*  382:     */   }
/*  383:     */   
/*  384:     */   public final Instances relation(int valIndex)
/*  385:     */   {
/*  386: 769 */     if (!isRelationValued()) {
/*  387: 770 */       return null;
/*  388:     */     }
/*  389: 772 */     return (Instances)((RelationalAttributeInfo)this.m_AttributeInfo).m_Values.get(valIndex);
/*  390:     */   }
/*  391:     */   
/*  392:     */   public Attribute(String attributeName, int index)
/*  393:     */   {
/*  394: 788 */     this(attributeName);
/*  395: 789 */     this.m_Index = index;
/*  396:     */   }
/*  397:     */   
/*  398:     */   public Attribute(String attributeName, String dateFormat, int index)
/*  399:     */   {
/*  400: 806 */     this(attributeName, dateFormat);
/*  401: 807 */     this.m_Index = index;
/*  402:     */   }
/*  403:     */   
/*  404:     */   public Attribute(String attributeName, List<String> attributeValues, int index)
/*  405:     */   {
/*  406: 826 */     this(attributeName, attributeValues);
/*  407: 827 */     this.m_Index = index;
/*  408:     */   }
/*  409:     */   
/*  410:     */   public Attribute(String attributeName, Instances header, int index)
/*  411:     */   {
/*  412: 843 */     this(attributeName, header);
/*  413: 844 */     this.m_Index = index;
/*  414:     */   }
/*  415:     */   
/*  416:     */   public int addStringValue(String value)
/*  417:     */   {
/*  418: 861 */     if (!isString()) {
/*  419: 862 */       return -1;
/*  420:     */     }
/*  421: 864 */     Object store = value;
/*  422: 866 */     if (value.length() > 200) {
/*  423:     */       try
/*  424:     */       {
/*  425: 868 */         store = new SerializedObject(value, true);
/*  426:     */       }
/*  427:     */       catch (Exception ex)
/*  428:     */       {
/*  429: 870 */         System.err.println("Couldn't compress string attribute value - storing uncompressed.");
/*  430:     */       }
/*  431:     */     }
/*  432: 874 */     Integer index = (Integer)((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.get(store);
/*  433: 875 */     if (index != null) {
/*  434: 876 */       return index.intValue();
/*  435:     */     }
/*  436: 878 */     int intIndex = ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size();
/*  437: 879 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.add(store);
/*  438: 880 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.put(store, new Integer(intIndex));
/*  439: 881 */     return intIndex;
/*  440:     */   }
/*  441:     */   
/*  442:     */   public void setStringValue(String value)
/*  443:     */   {
/*  444: 893 */     if (!isString()) {
/*  445: 894 */       return;
/*  446:     */     }
/*  447: 897 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.clear();
/*  448: 898 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.clear();
/*  449: 899 */     if (value != null) {
/*  450: 900 */       addStringValue(value);
/*  451:     */     }
/*  452:     */   }
/*  453:     */   
/*  454:     */   public int addStringValue(Attribute src, int index)
/*  455:     */   {
/*  456: 921 */     if (!isString()) {
/*  457: 922 */       return -1;
/*  458:     */     }
/*  459: 924 */     Object store = ((NominalAttributeInfo)src.m_AttributeInfo).m_Values.get(index);
/*  460: 925 */     Integer oldIndex = (Integer)((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.get(store);
/*  461: 926 */     if (oldIndex != null) {
/*  462: 927 */       return oldIndex.intValue();
/*  463:     */     }
/*  464: 929 */     int intIndex = ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size();
/*  465: 930 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.add(store);
/*  466: 931 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.put(store, new Integer(intIndex));
/*  467: 932 */     return intIndex;
/*  468:     */   }
/*  469:     */   
/*  470:     */   public int addRelation(Instances value)
/*  471:     */   {
/*  472: 945 */     if (!isRelationValued()) {
/*  473: 946 */       return -1;
/*  474:     */     }
/*  475: 948 */     if (!((RelationalAttributeInfo)this.m_AttributeInfo).m_Header.equalHeaders(value)) {
/*  476: 949 */       throw new IllegalArgumentException("Incompatible value for relation-valued attribute.\n" + ((RelationalAttributeInfo)this.m_AttributeInfo).m_Header.equalHeadersMsg(value));
/*  477:     */     }
/*  478: 952 */     Integer index = (Integer)((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.get(value);
/*  479: 953 */     if (index != null) {
/*  480: 954 */       return index.intValue();
/*  481:     */     }
/*  482: 956 */     int intIndex = ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size();
/*  483: 957 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.add(value);
/*  484: 958 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.put(value, new Integer(intIndex));
/*  485: 959 */     return intIndex;
/*  486:     */   }
/*  487:     */   
/*  488:     */   final void addValue(String value)
/*  489:     */   {
/*  490: 971 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values = ((ArrayList)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.clone()));
/*  491:     */     
/*  492: 973 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable = ((Hashtable)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.clone()));
/*  493:     */     
/*  494: 975 */     forceAddValue(value);
/*  495:     */   }
/*  496:     */   
/*  497:     */   public final Attribute copy(String newName)
/*  498:     */   {
/*  499: 990 */     Attribute copy = new Attribute(newName);
/*  500:     */     
/*  501: 992 */     copy.m_Index = this.m_Index;
/*  502: 993 */     copy.m_Type = this.m_Type;
/*  503: 994 */     copy.m_AttributeInfo = this.m_AttributeInfo;
/*  504: 995 */     copy.m_AttributeMetaInfo = this.m_AttributeMetaInfo;
/*  505:     */     
/*  506: 997 */     return copy;
/*  507:     */   }
/*  508:     */   
/*  509:     */   final void delete(int index)
/*  510:     */   {
/*  511:1012 */     if ((!isNominal()) && (!isString()) && (!isRelationValued())) {
/*  512:1013 */       throw new IllegalArgumentException("Can only remove value of nominal, string or relation- valued attribute!");
/*  513:     */     }
/*  514:1016 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values = ((ArrayList)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.clone()));
/*  515:     */     
/*  516:1018 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.remove(index);
/*  517:1019 */     if (!isRelationValued())
/*  518:     */     {
/*  519:1020 */       Hashtable<Object, Integer> hash = new Hashtable(((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.size());
/*  520:     */       
/*  521:1022 */       Enumeration<Object> enu = ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.keys();
/*  522:1023 */       while (enu.hasMoreElements())
/*  523:     */       {
/*  524:1024 */         Object string = enu.nextElement();
/*  525:1025 */         Integer valIndexObject = (Integer)((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.get(string);
/*  526:1026 */         int valIndex = valIndexObject.intValue();
/*  527:1027 */         if (valIndex > index) {
/*  528:1028 */           hash.put(string, new Integer(valIndex - 1));
/*  529:1029 */         } else if (valIndex < index) {
/*  530:1030 */           hash.put(string, valIndexObject);
/*  531:     */         }
/*  532:     */       }
/*  533:1033 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable = hash;
/*  534:     */     }
/*  535:     */   }
/*  536:     */   
/*  537:     */   final void forceAddValue(String value)
/*  538:     */   {
/*  539:1047 */     Object store = value;
/*  540:1048 */     if (value.length() > 200) {
/*  541:     */       try
/*  542:     */       {
/*  543:1050 */         store = new SerializedObject(value, true);
/*  544:     */       }
/*  545:     */       catch (Exception ex)
/*  546:     */       {
/*  547:1052 */         System.err.println("Couldn't compress string attribute value - storing uncompressed.");
/*  548:     */       }
/*  549:     */     }
/*  550:1056 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.add(store);
/*  551:1057 */     ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.put(store, new Integer(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.size() - 1));
/*  552:     */   }
/*  553:     */   
/*  554:     */   final void setIndex(int index)
/*  555:     */   {
/*  556:1071 */     this.m_Index = index;
/*  557:     */   }
/*  558:     */   
/*  559:     */   final void setValue(int index, String string)
/*  560:     */   {
/*  561:1087 */     switch (this.m_Type)
/*  562:     */     {
/*  563:     */     case 1: 
/*  564:     */     case 2: 
/*  565:1090 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Values = ((ArrayList)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.clone()));
/*  566:     */       
/*  567:1092 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable = ((Hashtable)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.clone()));
/*  568:     */       
/*  569:1094 */       Object store = string;
/*  570:1095 */       if (string.length() > 200) {
/*  571:     */         try
/*  572:     */         {
/*  573:1097 */           store = new SerializedObject(string, true);
/*  574:     */         }
/*  575:     */         catch (Exception ex)
/*  576:     */         {
/*  577:1099 */           System.err.println("Couldn't compress string attribute value - storing uncompressed.");
/*  578:     */         }
/*  579:     */       }
/*  580:1103 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.remove(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.get(index));
/*  581:     */       
/*  582:1105 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.set(index, store);
/*  583:1106 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.put(store, new Integer(index));
/*  584:1107 */       break;
/*  585:     */     default: 
/*  586:1109 */       throw new IllegalArgumentException("Can only set values for nominal or string attributes!");
/*  587:     */     }
/*  588:     */   }
/*  589:     */   
/*  590:     */   final void setValue(int index, Instances data)
/*  591:     */   {
/*  592:1124 */     if (isRelationValued())
/*  593:     */     {
/*  594:1125 */       if (!data.equalHeaders(((RelationalAttributeInfo)this.m_AttributeInfo).m_Header)) {
/*  595:1126 */         throw new IllegalArgumentException("Can't set relational value. Headers not compatible.\n" + data.equalHeadersMsg(((RelationalAttributeInfo)this.m_AttributeInfo).m_Header));
/*  596:     */       }
/*  597:1129 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Values = ((ArrayList)Utils.cast(((NominalAttributeInfo)this.m_AttributeInfo).m_Values.clone()));
/*  598:     */       
/*  599:1131 */       ((NominalAttributeInfo)this.m_AttributeInfo).m_Values.set(index, data);
/*  600:     */     }
/*  601:     */     else
/*  602:     */     {
/*  603:1133 */       throw new IllegalArgumentException("Can only set value for relation-valued attributes!");
/*  604:     */     }
/*  605:     */   }
/*  606:     */   
/*  607:     */   public String formatDate(double date)
/*  608:     */   {
/*  609:1148 */     switch (this.m_Type)
/*  610:     */     {
/*  611:     */     case 3: 
/*  612:1150 */       return ((DateAttributeInfo)this.m_AttributeInfo).m_DateFormat.format(new Date(date));
/*  613:     */     }
/*  614:1152 */     throw new IllegalArgumentException("Can only format date values for date attributes!");
/*  615:     */   }
/*  616:     */   
/*  617:     */   public double parseDate(String string)
/*  618:     */     throws ParseException
/*  619:     */   {
/*  620:1168 */     switch (this.m_Type)
/*  621:     */     {
/*  622:     */     case 3: 
/*  623:1170 */       long time = ((DateAttributeInfo)this.m_AttributeInfo).m_DateFormat.parse(string).getTime();
/*  624:     */       
/*  625:     */ 
/*  626:1173 */       return time;
/*  627:     */     }
/*  628:1175 */     throw new IllegalArgumentException("Can only parse date values for date attributes!");
/*  629:     */   }
/*  630:     */   
/*  631:     */   public final ProtectedProperties getMetadata()
/*  632:     */   {
/*  633:1188 */     if (this.m_AttributeMetaInfo == null) {
/*  634:1189 */       return null;
/*  635:     */     }
/*  636:1191 */     return this.m_AttributeMetaInfo.m_Metadata;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public final int ordering()
/*  640:     */   {
/*  641:1205 */     if (this.m_AttributeMetaInfo == null) {
/*  642:1206 */       return 1;
/*  643:     */     }
/*  644:1208 */     return this.m_AttributeMetaInfo.m_Ordering;
/*  645:     */   }
/*  646:     */   
/*  647:     */   public final boolean isRegular()
/*  648:     */   {
/*  649:1218 */     if (this.m_AttributeMetaInfo == null) {
/*  650:1219 */       return true;
/*  651:     */     }
/*  652:1221 */     return this.m_AttributeMetaInfo.m_IsRegular;
/*  653:     */   }
/*  654:     */   
/*  655:     */   public final boolean isAveragable()
/*  656:     */   {
/*  657:1231 */     if (this.m_AttributeMetaInfo == null) {
/*  658:1232 */       return true;
/*  659:     */     }
/*  660:1234 */     return this.m_AttributeMetaInfo.m_IsAveragable;
/*  661:     */   }
/*  662:     */   
/*  663:     */   public final boolean hasZeropoint()
/*  664:     */   {
/*  665:1245 */     if (this.m_AttributeMetaInfo == null) {
/*  666:1246 */       return true;
/*  667:     */     }
/*  668:1248 */     return this.m_AttributeMetaInfo.m_HasZeropoint;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public final double weight()
/*  672:     */   {
/*  673:1258 */     return this.m_Weight;
/*  674:     */   }
/*  675:     */   
/*  676:     */   public void setWeight(double value)
/*  677:     */   {
/*  678:1269 */     this.m_Weight = value;
/*  679:     */   }
/*  680:     */   
/*  681:     */   public final double getLowerNumericBound()
/*  682:     */   {
/*  683:1279 */     if (this.m_AttributeMetaInfo == null) {
/*  684:1280 */       return -1.797693134862316E+308D;
/*  685:     */     }
/*  686:1282 */     return this.m_AttributeMetaInfo.m_LowerBound;
/*  687:     */   }
/*  688:     */   
/*  689:     */   public final boolean lowerNumericBoundIsOpen()
/*  690:     */   {
/*  691:1292 */     if (this.m_AttributeMetaInfo == null) {
/*  692:1293 */       return true;
/*  693:     */     }
/*  694:1295 */     return this.m_AttributeMetaInfo.m_LowerBoundIsOpen;
/*  695:     */   }
/*  696:     */   
/*  697:     */   public final double getUpperNumericBound()
/*  698:     */   {
/*  699:1305 */     if (this.m_AttributeMetaInfo == null) {
/*  700:1306 */       return 1.7976931348623157E+308D;
/*  701:     */     }
/*  702:1308 */     return this.m_AttributeMetaInfo.m_UpperBound;
/*  703:     */   }
/*  704:     */   
/*  705:     */   public final boolean upperNumericBoundIsOpen()
/*  706:     */   {
/*  707:1318 */     if (this.m_AttributeMetaInfo == null) {
/*  708:1319 */       return true;
/*  709:     */     }
/*  710:1321 */     return this.m_AttributeMetaInfo.m_UpperBoundIsOpen;
/*  711:     */   }
/*  712:     */   
/*  713:     */   public final boolean isInRange(double value)
/*  714:     */   {
/*  715:1333 */     if ((this.m_Type == 3) || (Utils.isMissingValue(value))) {
/*  716:1334 */       return true;
/*  717:     */     }
/*  718:1336 */     if (this.m_Type != 0)
/*  719:     */     {
/*  720:1338 */       int intVal = (int)value;
/*  721:1339 */       if ((intVal < 0) || (intVal >= ((NominalAttributeInfo)this.m_AttributeInfo).m_Hashtable.size())) {
/*  722:1340 */         return false;
/*  723:     */       }
/*  724:     */     }
/*  725:     */     else
/*  726:     */     {
/*  727:1343 */       if (this.m_AttributeMetaInfo == null) {
/*  728:1344 */         return true;
/*  729:     */       }
/*  730:1347 */       if (this.m_AttributeMetaInfo.m_LowerBoundIsOpen)
/*  731:     */       {
/*  732:1348 */         if (value <= this.m_AttributeMetaInfo.m_LowerBound) {
/*  733:1349 */           return false;
/*  734:     */         }
/*  735:     */       }
/*  736:1352 */       else if (value < this.m_AttributeMetaInfo.m_LowerBound) {
/*  737:1353 */         return false;
/*  738:     */       }
/*  739:1356 */       if (this.m_AttributeMetaInfo.m_UpperBoundIsOpen)
/*  740:     */       {
/*  741:1357 */         if (value >= this.m_AttributeMetaInfo.m_UpperBound) {
/*  742:1358 */           return false;
/*  743:     */         }
/*  744:     */       }
/*  745:1361 */       else if (value > this.m_AttributeMetaInfo.m_UpperBound) {
/*  746:1362 */         return false;
/*  747:     */       }
/*  748:     */     }
/*  749:1366 */     return true;
/*  750:     */   }
/*  751:     */   
/*  752:     */   public String getRevision()
/*  753:     */   {
/*  754:1376 */     return RevisionUtils.extract("$Revision: 12593 $");
/*  755:     */   }
/*  756:     */   
/*  757:     */   public static void main(String[] ops)
/*  758:     */   {
/*  759:     */     try
/*  760:     */     {
/*  761:1390 */       new Attribute("length");
/*  762:1391 */       Attribute weight = new Attribute("weight");
/*  763:     */       
/*  764:     */ 
/*  765:1394 */       Attribute date = new Attribute("date", "yyyy-MM-dd HH:mm:ss");
/*  766:     */       
/*  767:1396 */       System.out.println(date);
/*  768:1397 */       double dd = date.parseDate("2001-04-04 14:13:55");
/*  769:1398 */       System.out.println("Test date = " + dd);
/*  770:1399 */       System.out.println(date.formatDate(dd));
/*  771:     */       
/*  772:1401 */       dd = new Date().getTime();
/*  773:1402 */       System.out.println("Date now = " + dd);
/*  774:1403 */       System.out.println(date.formatDate(dd));
/*  775:     */       
/*  776:     */ 
/*  777:1406 */       List<String> my_nominal_values = new ArrayList(3);
/*  778:1407 */       my_nominal_values.add("first");
/*  779:1408 */       my_nominal_values.add("second");
/*  780:1409 */       my_nominal_values.add("third");
/*  781:     */       
/*  782:     */ 
/*  783:1412 */       Attribute position = new Attribute("position", my_nominal_values);
/*  784:     */       
/*  785:     */ 
/*  786:1415 */       System.out.println("Name of \"position\": " + position.name());
/*  787:     */       
/*  788:     */ 
/*  789:1418 */       Enumeration<Object> attValues = position.enumerateValues();
/*  790:1419 */       while (attValues.hasMoreElements())
/*  791:     */       {
/*  792:1420 */         String string = (String)attValues.nextElement();
/*  793:1421 */         System.out.println("Value of \"position\": " + string);
/*  794:     */       }
/*  795:1425 */       Attribute copy = (Attribute)position.copy();
/*  796:     */       
/*  797:     */ 
/*  798:1428 */       System.out.println("Copy is the same as original: " + copy.equals(position));
/*  799:     */       
/*  800:     */ 
/*  801:     */ 
/*  802:1432 */       System.out.println("Index of attribute \"weight\" (should be -1): " + weight.index());
/*  803:     */       
/*  804:     */ 
/*  805:     */ 
/*  806:1436 */       System.out.println("Index of value \"first\" of \"position\" (should be 0): " + position.indexOfValue("first"));
/*  807:     */       
/*  808:     */ 
/*  809:     */ 
/*  810:     */ 
/*  811:1441 */       System.out.println("\"position\" is numeric: " + position.isNumeric());
/*  812:1442 */       System.out.println("\"position\" is nominal: " + position.isNominal());
/*  813:1443 */       System.out.println("\"position\" is string: " + position.isString());
/*  814:     */       
/*  815:     */ 
/*  816:1446 */       System.out.println("Name of \"position\": " + position.name());
/*  817:     */       
/*  818:     */ 
/*  819:1449 */       System.out.println("Number of values for \"position\": " + position.numValues());
/*  820:1453 */       for (int i = 0; i < position.numValues(); i++) {
/*  821:1454 */         System.out.println("Value " + i + ": " + position.value(i));
/*  822:     */       }
/*  823:1458 */       System.out.println(position);
/*  824:1461 */       switch (position.type())
/*  825:     */       {
/*  826:     */       case 0: 
/*  827:1463 */         System.out.println("\"position\" is numeric");
/*  828:1464 */         break;
/*  829:     */       case 1: 
/*  830:1466 */         System.out.println("\"position\" is nominal");
/*  831:1467 */         break;
/*  832:     */       case 2: 
/*  833:1469 */         System.out.println("\"position\" is string");
/*  834:1470 */         break;
/*  835:     */       case 3: 
/*  836:1472 */         System.out.println("\"position\" is date");
/*  837:1473 */         break;
/*  838:     */       case 4: 
/*  839:1475 */         System.out.println("\"position\" is relation-valued");
/*  840:1476 */         break;
/*  841:     */       default: 
/*  842:1478 */         System.out.println("\"position\" has unknown type");
/*  843:     */       }
/*  844:1481 */       ArrayList<Attribute> atts = new ArrayList(1);
/*  845:1482 */       atts.add(position);
/*  846:1483 */       Instances relation = new Instances("Test", atts, 0);
/*  847:1484 */       Attribute relationValuedAtt = new Attribute("test", relation);
/*  848:1485 */       System.out.println(relationValuedAtt);
/*  849:     */     }
/*  850:     */     catch (Exception e)
/*  851:     */     {
/*  852:1487 */       e.printStackTrace();
/*  853:     */     }
/*  854:     */   }
/*  855:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Attribute
 * JD-Core Version:    0.7.0.1
 */