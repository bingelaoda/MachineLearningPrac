/*    1:     */ package org.boon.criteria;
/*    2:     */ 
/*    3:     */ import java.lang.reflect.Field;
/*    4:     */ import java.lang.reflect.ParameterizedType;
/*    5:     */ import java.util.Arrays;
/*    6:     */ import java.util.Collection;
/*    7:     */ import java.util.Date;
/*    8:     */ import java.util.HashSet;
/*    9:     */ import java.util.Iterator;
/*   10:     */ import java.util.Map;
/*   11:     */ import java.util.Map.Entry;
/*   12:     */ import java.util.Set;
/*   13:     */ import org.boon.Boon;
/*   14:     */ import org.boon.Exceptions;
/*   15:     */ import org.boon.Str;
/*   16:     */ import org.boon.core.Conversions;
/*   17:     */ import org.boon.core.Typ;
/*   18:     */ import org.boon.core.TypeType;
/*   19:     */ import org.boon.core.Value;
/*   20:     */ import org.boon.core.reflection.BeanUtils;
/*   21:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   22:     */ import org.boon.criteria.internal.Criteria;
/*   23:     */ import org.boon.criteria.internal.Operator;
/*   24:     */ import org.boon.primitive.CharBuf;
/*   25:     */ 
/*   26:     */ public abstract class Criterion<VALUE>
/*   27:     */   extends Criteria
/*   28:     */ {
/*   29:     */   private Object name;
/*   30:     */   private Operator operator;
/*   31:     */   protected VALUE value;
/*   32:     */   protected VALUE value2;
/*   33:     */   protected VALUE[] values;
/*   34:     */   private final int hashCode;
/*   35:     */   private final String toString;
/*   36:     */   private boolean initialized;
/*   37:     */   private Criterion nativeDelegate;
/*   38:     */   private boolean useDelegate;
/*   39:     */   private FieldAccess field;
/*   40:     */   protected Object objectUnderTest;
/*   41:     */   private Map<String, FieldAccess> fields;
/*   42:  76 */   private ThisField fakeField = null;
/*   43:     */   private boolean path;
/*   44:     */   boolean convert1st;
/*   45:     */   boolean convert2nd;
/*   46:     */   
/*   47:     */   public Criterion(String name, Operator operator, VALUE... values)
/*   48:     */   {
/*   49:  81 */     Exceptions.requireNonNull(name, "name cannot be null");
/*   50:  82 */     Exceptions.requireNonNull(operator, "operator cannot be null");
/*   51:  83 */     Exceptions.requireNonNull(values, "values cannot be null");
/*   52:     */     
/*   53:     */ 
/*   54:  86 */     this.path = isPropPath(name);
/*   55:     */     
/*   56:  88 */     this.name = name;
/*   57:  89 */     this.operator = operator;
/*   58:  90 */     setValues(values);
/*   59:  91 */     this.hashCode = doHashCode();
/*   60:  92 */     this.toString = doToString();
/*   61:     */   }
/*   62:     */   
/*   63:     */   public FieldAccess field()
/*   64:     */   {
/*   65: 105 */     if (this.path)
/*   66:     */     {
/*   67: 106 */       FieldAccess field = BeanUtils.idxField(this.objectUnderTest, this.name.toString());
/*   68: 109 */       if (field == null) {
/*   69: 110 */         return fakeField();
/*   70:     */       }
/*   71: 113 */       return field;
/*   72:     */     }
/*   73: 116 */     if ((this.name instanceof Enum)) {
/*   74: 117 */       this.name = Str.camelCaseLower(this.name.toString());
/*   75:     */     }
/*   76: 120 */     FieldAccess field = (FieldAccess)fields().get(this.name);
/*   77:     */     
/*   78: 122 */     return field;
/*   79:     */   }
/*   80:     */   
/*   81:     */   private FieldAccess fakeField()
/*   82:     */   {
/*   83: 127 */     if (this.fakeField == null) {
/*   84: 128 */       this.fakeField = new ThisField(this.name.toString(), this.objectUnderTest);
/*   85:     */     }
/*   86: 131 */     this.fakeField.thisObject = this.objectUnderTest;
/*   87:     */     
/*   88: 133 */     return this.fakeField;
/*   89:     */   }
/*   90:     */   
/*   91:     */   public Object fieldValue()
/*   92:     */   {
/*   93: 143 */     if (!this.path)
/*   94:     */     {
/*   95: 144 */       FieldAccess field1 = field();
/*   96: 145 */       return field1.getValue(this.objectUnderTest);
/*   97:     */     }
/*   98: 147 */     return BeanUtils.atIndex(this.objectUnderTest, this.name.toString());
/*   99:     */   }
/*  100:     */   
/*  101:     */   public int fieldInt()
/*  102:     */   {
/*  103: 152 */     if (!this.path)
/*  104:     */     {
/*  105: 153 */       FieldAccess field1 = field();
/*  106: 154 */       return field1.getInt(this.objectUnderTest);
/*  107:     */     }
/*  108: 156 */     return BeanUtils.idxInt(this.objectUnderTest, this.name.toString());
/*  109:     */   }
/*  110:     */   
/*  111:     */   public short fieldShort()
/*  112:     */   {
/*  113: 164 */     if (!this.path)
/*  114:     */     {
/*  115: 165 */       FieldAccess field1 = field();
/*  116: 166 */       return field1.getShort(this.objectUnderTest);
/*  117:     */     }
/*  118: 168 */     return BeanUtils.idxShort(this.objectUnderTest, this.name.toString());
/*  119:     */   }
/*  120:     */   
/*  121:     */   public long fieldLong()
/*  122:     */   {
/*  123: 177 */     if (!this.path)
/*  124:     */     {
/*  125: 178 */       FieldAccess field1 = field();
/*  126: 179 */       return field1.getLong(this.objectUnderTest);
/*  127:     */     }
/*  128: 181 */     return BeanUtils.idxLong(this.objectUnderTest, this.name.toString());
/*  129:     */   }
/*  130:     */   
/*  131:     */   public float fieldFloat()
/*  132:     */   {
/*  133: 191 */     if (!this.path)
/*  134:     */     {
/*  135: 192 */       FieldAccess field1 = field();
/*  136: 193 */       return field1.getFloat(this.objectUnderTest);
/*  137:     */     }
/*  138: 195 */     return BeanUtils.idxFloat(this.objectUnderTest, this.name.toString());
/*  139:     */   }
/*  140:     */   
/*  141:     */   public double fieldDouble()
/*  142:     */   {
/*  143: 206 */     if (!this.path)
/*  144:     */     {
/*  145: 207 */       FieldAccess field1 = field();
/*  146: 208 */       return field1.getDouble(this.objectUnderTest);
/*  147:     */     }
/*  148: 210 */     return BeanUtils.idxDouble(this.objectUnderTest, this.name.toString());
/*  149:     */   }
/*  150:     */   
/*  151:     */   public boolean fieldBoolean()
/*  152:     */   {
/*  153: 219 */     if (!this.path)
/*  154:     */     {
/*  155: 220 */       FieldAccess field1 = field();
/*  156: 221 */       return field1.getBoolean(this.objectUnderTest);
/*  157:     */     }
/*  158: 223 */     return BeanUtils.idxBoolean(this.objectUnderTest, this.name.toString());
/*  159:     */   }
/*  160:     */   
/*  161:     */   public byte fieldByte()
/*  162:     */   {
/*  163: 233 */     if (!this.path)
/*  164:     */     {
/*  165: 234 */       FieldAccess field1 = field();
/*  166: 235 */       return field1.getByte(this.objectUnderTest);
/*  167:     */     }
/*  168: 237 */     return BeanUtils.idxByte(this.objectUnderTest, this.name.toString());
/*  169:     */   }
/*  170:     */   
/*  171:     */   public char fieldChar()
/*  172:     */   {
/*  173: 243 */     if (!this.path)
/*  174:     */     {
/*  175: 244 */       FieldAccess field1 = field();
/*  176: 245 */       return field1.getChar(this.objectUnderTest);
/*  177:     */     }
/*  178: 247 */     return BeanUtils.idxChar(this.objectUnderTest, this.name.toString());
/*  179:     */   }
/*  180:     */   
/*  181:     */   public Set<Object> valueSet()
/*  182:     */   {
/*  183: 258 */     if (!this.convert1st)
/*  184:     */     {
/*  185: 260 */       HashSet<Object> set = new HashSet(this.values.length);
/*  186: 261 */       FieldAccess field = field();
/*  187: 262 */       Class<?> classType = field.type();
/*  188: 264 */       for (Object v : this.values)
/*  189:     */       {
/*  190: 265 */         v = Conversions.coerce(classType, v);
/*  191: 266 */         set.add(v);
/*  192:     */       }
/*  193: 269 */       this.value = set;
/*  194:     */       
/*  195: 271 */       this.convert1st = true;
/*  196:     */     }
/*  197: 273 */     return (Set)this.value;
/*  198:     */   }
/*  199:     */   
/*  200:     */   public Object value()
/*  201:     */   {
/*  202: 284 */     if (!this.convert1st)
/*  203:     */     {
/*  204: 285 */       FieldAccess field = field();
/*  205: 286 */       if (field != null) {
/*  206: 287 */         switch (1.$SwitchMap$org$boon$core$TypeType[field.typeEnum().ordinal()])
/*  207:     */         {
/*  208:     */         case 1: 
/*  209: 292 */           this.value = Conversions.coerce(field.type(), this.value);
/*  210: 293 */           return new MyNumber(this.value);
/*  211:     */         case 2: 
/*  212:     */         case 3: 
/*  213:     */         case 4: 
/*  214:     */         case 5: 
/*  215:     */         case 6: 
/*  216:     */         case 7: 
/*  217:     */         case 8: 
/*  218:     */         case 9: 
/*  219:     */         case 10: 
/*  220:     */         case 11: 
/*  221:     */         case 12: 
/*  222:     */         case 13: 
/*  223: 308 */           this.value = Conversions.coerce(field.getComponentClass(), this.value);
/*  224: 309 */           break;
/*  225:     */         default: 
/*  226: 312 */           this.value = Conversions.coerce(field.type(), this.value);
/*  227:     */         }
/*  228:     */       }
/*  229: 315 */       this.convert1st = true;
/*  230:     */     }
/*  231: 317 */     return this.value;
/*  232:     */   }
/*  233:     */   
/*  234:     */   public Object value2()
/*  235:     */   {
/*  236: 327 */     if (!this.convert2nd)
/*  237:     */     {
/*  238: 328 */       FieldAccess field = field();
/*  239: 329 */       this.value2 = Conversions.coerce(field.type(), this.value2);
/*  240: 330 */       this.convert2nd = true;
/*  241:     */     }
/*  242: 332 */     return this.value2;
/*  243:     */   }
/*  244:     */   
/*  245:     */   public String getName()
/*  246:     */   {
/*  247: 340 */     return this.name.toString();
/*  248:     */   }
/*  249:     */   
/*  250:     */   public Operator getOperator()
/*  251:     */   {
/*  252: 344 */     return this.operator;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public VALUE getValue()
/*  256:     */   {
/*  257: 349 */     return this.value;
/*  258:     */   }
/*  259:     */   
/*  260:     */   public VALUE[] getValues()
/*  261:     */   {
/*  262: 354 */     return this.values;
/*  263:     */   }
/*  264:     */   
/*  265:     */   public void setValues(VALUE[] values)
/*  266:     */   {
/*  267: 358 */     if (values.length > 0) {
/*  268: 359 */       this.value = values[0];
/*  269:     */     }
/*  270: 361 */     if (values.length > 1) {
/*  271: 362 */       this.value2 = values[1];
/*  272:     */     }
/*  273: 365 */     this.values = values;
/*  274:     */   }
/*  275:     */   
/*  276:     */   private static boolean isPropPath(String prop)
/*  277:     */   {
/*  278: 375 */     return BeanUtils.isPropPath(prop);
/*  279:     */   }
/*  280:     */   
/*  281:     */   public boolean equals(Object o)
/*  282:     */   {
/*  283: 380 */     if (this == o) {
/*  284: 380 */       return true;
/*  285:     */     }
/*  286: 381 */     if (!(o instanceof Criterion)) {
/*  287: 381 */       return false;
/*  288:     */     }
/*  289: 383 */     Criterion criterion = (Criterion)o;
/*  290: 385 */     if (this.name != null ? !this.name.equals(criterion.name) : criterion.name != null) {
/*  291: 385 */       return false;
/*  292:     */     }
/*  293: 386 */     if (this.operator != criterion.operator) {
/*  294: 386 */       return false;
/*  295:     */     }
/*  296: 387 */     if (this.value != null ? !this.value.equals(criterion.value) : criterion.value != null) {
/*  297: 387 */       return false;
/*  298:     */     }
/*  299: 388 */     if (!Arrays.equals(this.values, criterion.values)) {
/*  300: 388 */       return false;
/*  301:     */     }
/*  302: 390 */     return true;
/*  303:     */   }
/*  304:     */   
/*  305:     */   public int hashCode()
/*  306:     */   {
/*  307: 396 */     return this.hashCode;
/*  308:     */   }
/*  309:     */   
/*  310:     */   public int doHashCode()
/*  311:     */   {
/*  312: 400 */     int result = this.name != null ? this.name.hashCode() : 0;
/*  313: 401 */     result = 31 * result + (this.operator != null ? this.operator.hashCode() : 0);
/*  314: 402 */     result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
/*  315: 403 */     result = 31 * result + (this.values != null ? Arrays.hashCode(this.values) : 0);
/*  316: 404 */     return result;
/*  317:     */   }
/*  318:     */   
/*  319:     */   public String toString()
/*  320:     */   {
/*  321: 409 */     return this.toString;
/*  322:     */   }
/*  323:     */   
/*  324:     */   public String doToString()
/*  325:     */   {
/*  326: 414 */     CharBuf builder = CharBuf.create(80);
/*  327: 415 */     builder.add("c{");
/*  328: 416 */     builder.add("\"name\":'");
/*  329: 417 */     builder.add(String.valueOf(this.name));
/*  330: 418 */     builder.add(", \"operator\":");
/*  331: 419 */     builder.add(String.valueOf(this.operator));
/*  332: 420 */     builder.add(", \"set\":");
/*  333: 421 */     builder.add(String.valueOf(this.value));
/*  334: 422 */     builder.add(", \"update\":");
/*  335: 423 */     builder.add(Arrays.toString(this.values));
/*  336: 424 */     builder.add("}");
/*  337: 425 */     return builder.toString();
/*  338:     */   }
/*  339:     */   
/*  340:     */   public boolean isInitialized()
/*  341:     */   {
/*  342: 429 */     return this.initialized;
/*  343:     */   }
/*  344:     */   
/*  345:     */   public void clean()
/*  346:     */   {
/*  347: 435 */     this.field = null;
/*  348: 436 */     this.fields = null;
/*  349: 437 */     this.objectUnderTest = null;
/*  350:     */   }
/*  351:     */   
/*  352:     */   public abstract boolean resolve(Object paramObject);
/*  353:     */   
/*  354:     */   public boolean test(Object o)
/*  355:     */   {
/*  356: 448 */     FieldAccess field = null;
/*  357:     */     try
/*  358:     */     {
/*  359: 452 */       Exceptions.requireNonNull(o, "object under test can't be null");
/*  360:     */       
/*  361: 454 */       this.objectUnderTest = o;
/*  362:     */       
/*  363: 456 */       initIfNeeded();
/*  364: 457 */       if (this.useDelegate)
/*  365:     */       {
/*  366: 459 */         this.nativeDelegate.fields = this.fields;
/*  367: 460 */         this.nativeDelegate.objectUnderTest = this.objectUnderTest;
/*  368: 461 */         return this.nativeDelegate.resolve(o);
/*  369:     */       }
/*  370: 465 */       field = field();
/*  371: 468 */       if ((!this.path) && (field == null) && ((o instanceof Map))) {
/*  372: 469 */         return false;
/*  373:     */       }
/*  374: 472 */       return resolve(o);
/*  375:     */     }
/*  376:     */     catch (Exception ex)
/*  377:     */     {
/*  378: 479 */       return ((Boolean)Exceptions.handle(Typ.bool, Boon.sputl(new Object[] { "In class " + getClass().getName(), "the test method is unable to test the following criteria operator", String.valueOf(getOperator()), Boon.sputs(new Object[] { "The field name is          :          ", getName() }), Boon.sputs(new Object[] { "The value is               :          ", getValue() }), Boon.sputs(new Object[] { "The value type is          :          ", getValue().getClass().getName() }), Boon.sputs(new Object[] { "The object under test      :          ", this.objectUnderTest }), Boon.sputs(new Object[] { "The object under test type :          ", this.objectUnderTest == null ? "null" : this.objectUnderTest.getClass().getName() }), Boon.sputs(new Object[] { "Field                      :          ", field }), Boon.sputs(new Object[] { "Fields                     :          ", this.fields }), Boon.sputs(new Object[0]) }), ex)).booleanValue();
/*  379:     */     }
/*  380:     */   }
/*  381:     */   
/*  382:     */   private Map<String, FieldAccess> fields()
/*  383:     */   {
/*  384: 505 */     if ((this.objectUnderTest instanceof Map)) {
/*  385: 506 */       return BeanUtils.getFieldsFromObject(this.objectUnderTest);
/*  386:     */     }
/*  387: 509 */     if (this.fields == null) {
/*  388: 510 */       this.fields = BeanUtils.getFieldsFromObject(this.objectUnderTest);
/*  389:     */     }
/*  390: 514 */     return this.fields;
/*  391:     */   }
/*  392:     */   
/*  393:     */   private void initForShortValue(short v)
/*  394:     */   {
/*  395: 871 */     this.value = Short.valueOf(v);
/*  396: 873 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  397:     */     {
/*  398:     */     case 1: 
/*  399: 875 */       this.nativeDelegate = ObjectFilter.eqShort(this.name, v);
/*  400: 876 */       break;
/*  401:     */     case 2: 
/*  402: 879 */       this.nativeDelegate = ObjectFilter.notEqShort(this.name, v);
/*  403: 880 */       break;
/*  404:     */     case 3: 
/*  405: 883 */       this.nativeDelegate = ObjectFilter.ltShort(this.name, v);
/*  406: 884 */       break;
/*  407:     */     case 4: 
/*  408: 887 */       this.nativeDelegate = ObjectFilter.lteShort(this.name, v);
/*  409: 888 */       break;
/*  410:     */     case 5: 
/*  411: 891 */       this.nativeDelegate = ObjectFilter.gtShort(this.name, v);
/*  412: 892 */       break;
/*  413:     */     case 6: 
/*  414: 895 */       this.nativeDelegate = ObjectFilter.gteShort(this.name, v);
/*  415: 896 */       break;
/*  416:     */     case 7: 
/*  417: 899 */       this.nativeDelegate = ObjectFilter.inShorts(this.name, Conversions.sarray(this.values));
/*  418: 900 */       break;
/*  419:     */     case 8: 
/*  420: 904 */       this.nativeDelegate = ObjectFilter.betweenShort(this.name, v, Conversions.toShort(this.values[1]));
/*  421:     */       
/*  422: 906 */       break;
/*  423:     */     case 9: 
/*  424: 910 */       this.nativeDelegate = ObjectFilter.notInShorts(this.name, Conversions.sarray(this.values));
/*  425:     */       
/*  426: 912 */       break;
/*  427:     */     default: 
/*  428: 915 */       this.useDelegate = false;
/*  429:     */     }
/*  430:     */   }
/*  431:     */   
/*  432:     */   private void initIfNeeded()
/*  433:     */   {
/*  434: 922 */     if (this.initialized) {
/*  435: 922 */       return;
/*  436:     */     }
/*  437: 923 */     this.initialized = true;
/*  438:     */     
/*  439: 925 */     String name = this.name.toString();
/*  440:     */     
/*  441: 927 */     FieldAccess field = field();
/*  442: 928 */     if (field == null) {
/*  443: 929 */       return;
/*  444:     */     }
/*  445: 932 */     Class type = field.type();
/*  446: 935 */     if ((!type.isPrimitive()) && (type != Typ.date)) {
/*  447: 936 */       return;
/*  448:     */     }
/*  449: 940 */     if (type == Typ.date)
/*  450:     */     {
/*  451: 942 */       if (!(this.value instanceof Date)) {
/*  452: 943 */         initForDate();
/*  453:     */       }
/*  454: 945 */       return;
/*  455:     */     }
/*  456: 949 */     this.useDelegate = true;
/*  457: 952 */     if (type == Typ.intgr)
/*  458:     */     {
/*  459: 953 */       int v = Conversions.toInt(this.value);
/*  460: 954 */       initForInt(v);
/*  461:     */     }
/*  462: 955 */     else if (type == Typ.bt)
/*  463:     */     {
/*  464: 957 */       byte v = Conversions.toByte(this.value);
/*  465:     */       
/*  466: 959 */       initForByte(v);
/*  467:     */     }
/*  468: 961 */     else if (type == Typ.shrt)
/*  469:     */     {
/*  470: 963 */       short v = Conversions.toShort(this.value);
/*  471:     */       
/*  472: 965 */       initForShortValue(v);
/*  473:     */     }
/*  474: 967 */     else if (type == Typ.lng)
/*  475:     */     {
/*  476: 969 */       long v = Conversions.toLong(this.value);
/*  477:     */       
/*  478: 971 */       initForLong(v);
/*  479:     */     }
/*  480: 974 */     else if (type == Typ.flt)
/*  481:     */     {
/*  482: 976 */       float v = Conversions.toFloat(this.value);
/*  483:     */       
/*  484:     */ 
/*  485: 979 */       initForFloat(v);
/*  486:     */     }
/*  487: 981 */     else if (type == Typ.dbl)
/*  488:     */     {
/*  489: 983 */       double v = Conversions.toDouble(this.value);
/*  490:     */       
/*  491: 985 */       initForDouble(v);
/*  492:     */     }
/*  493: 988 */     else if (type == Typ.bln)
/*  494:     */     {
/*  495: 991 */       switch (this.operator)
/*  496:     */       {
/*  497:     */       case EQUAL: 
/*  498: 993 */         this.nativeDelegate = ObjectFilter.eqBoolean(name, Conversions.toBoolean(this.value));
/*  499: 994 */         break;
/*  500:     */       case NOT_EQUAL: 
/*  501: 997 */         this.nativeDelegate = ObjectFilter.notEqBoolean(name, Conversions.toBoolean(this.value));
/*  502: 998 */         break;
/*  503:     */       default: 
/*  504:1002 */         this.useDelegate = false; break;
/*  505:     */       }
/*  506:     */     }
/*  507:1005 */     else if (type == Typ.chr)
/*  508:     */     {
/*  509:1007 */       char v = Conversions.toChar(this.value);
/*  510:1008 */       initForChar(v);
/*  511:     */     }
/*  512:     */   }
/*  513:     */   
/*  514:     */   private void initForChar(char value)
/*  515:     */   {
/*  516:1018 */     this.value = Character.valueOf(value);
/*  517:1020 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  518:     */     {
/*  519:     */     case 1: 
/*  520:1024 */       this.nativeDelegate = ObjectFilter.eqChar(this.name, value);
/*  521:1025 */       break;
/*  522:     */     case 2: 
/*  523:1028 */       this.nativeDelegate = ObjectFilter.notEqChar(this.name, value);
/*  524:1029 */       break;
/*  525:     */     case 3: 
/*  526:1032 */       this.nativeDelegate = ObjectFilter.ltChar(this.name, value);
/*  527:1033 */       break;
/*  528:     */     case 4: 
/*  529:1036 */       this.nativeDelegate = ObjectFilter.lteChar(this.name, value);
/*  530:1037 */       break;
/*  531:     */     case 5: 
/*  532:1040 */       this.nativeDelegate = ObjectFilter.gtChar(this.name, value);
/*  533:1041 */       break;
/*  534:     */     case 6: 
/*  535:1044 */       this.nativeDelegate = ObjectFilter.gteChar(this.name, value);
/*  536:1045 */       break;
/*  537:     */     case 8: 
/*  538:1048 */       this.nativeDelegate = ObjectFilter.betweenChar(this.name, value, Conversions.toChar(this.values[1]));
/*  539:     */       
/*  540:1050 */       break;
/*  541:     */     case 7: 
/*  542:1053 */       this.nativeDelegate = ObjectFilter.inChars(this.name, Conversions.carray(this.values));
/*  543:1054 */       break;
/*  544:     */     case 9: 
/*  545:1058 */       this.nativeDelegate = ObjectFilter.notInChars(this.name, Conversions.carray(this.values));
/*  546:1059 */       break;
/*  547:     */     default: 
/*  548:1062 */       this.useDelegate = false;
/*  549:     */     }
/*  550:     */   }
/*  551:     */   
/*  552:     */   private void initForDouble(double value)
/*  553:     */   {
/*  554:1068 */     this.value = Double.valueOf(value);
/*  555:1070 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  556:     */     {
/*  557:     */     case 1: 
/*  558:1072 */       this.nativeDelegate = ObjectFilter.eqDouble(this.name, value);
/*  559:1073 */       break;
/*  560:     */     case 2: 
/*  561:1076 */       this.nativeDelegate = ObjectFilter.notEqDouble(this.name, value);
/*  562:1077 */       break;
/*  563:     */     case 3: 
/*  564:1080 */       this.nativeDelegate = ObjectFilter.ltDouble(this.name, value);
/*  565:1081 */       break;
/*  566:     */     case 4: 
/*  567:1084 */       this.nativeDelegate = ObjectFilter.lteDouble(this.name, value);
/*  568:1085 */       break;
/*  569:     */     case 5: 
/*  570:1088 */       this.nativeDelegate = ObjectFilter.gtDouble(this.name, value);
/*  571:1089 */       break;
/*  572:     */     case 6: 
/*  573:1092 */       this.nativeDelegate = ObjectFilter.gteDouble(this.name, value);
/*  574:1093 */       break;
/*  575:     */     case 8: 
/*  576:1096 */       this.nativeDelegate = ObjectFilter.betweenDouble(this.name, value, Conversions.toDouble(this.values[1]));
/*  577:     */       
/*  578:1098 */       break;
/*  579:     */     case 7: 
/*  580:1101 */       this.nativeDelegate = ObjectFilter.inDoubles(this.name, Conversions.darray(this.values));
/*  581:     */       
/*  582:1103 */       break;
/*  583:     */     case 9: 
/*  584:1107 */       this.nativeDelegate = ObjectFilter.notInDoubles(this.name, Conversions.darray(this.values));
/*  585:     */       
/*  586:1109 */       break;
/*  587:     */     default: 
/*  588:1112 */       this.useDelegate = false;
/*  589:     */     }
/*  590:     */   }
/*  591:     */   
/*  592:     */   private void initForFloat(float value)
/*  593:     */   {
/*  594:1118 */     this.value = Float.valueOf(value);
/*  595:1120 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  596:     */     {
/*  597:     */     case 1: 
/*  598:1122 */       this.nativeDelegate = ObjectFilter.eqFloat(this.name, value);
/*  599:1123 */       break;
/*  600:     */     case 2: 
/*  601:1126 */       this.nativeDelegate = ObjectFilter.notEqFloat(this.name, value);
/*  602:1127 */       break;
/*  603:     */     case 3: 
/*  604:1130 */       this.nativeDelegate = ObjectFilter.ltFloat(this.name, value);
/*  605:1131 */       break;
/*  606:     */     case 4: 
/*  607:1134 */       this.nativeDelegate = ObjectFilter.lteFloat(this.name, value);
/*  608:1135 */       break;
/*  609:     */     case 5: 
/*  610:1138 */       this.nativeDelegate = ObjectFilter.gtFloat(this.name, value);
/*  611:1139 */       break;
/*  612:     */     case 6: 
/*  613:1142 */       this.nativeDelegate = ObjectFilter.gteFloat(this.name, value);
/*  614:1143 */       break;
/*  615:     */     case 8: 
/*  616:1146 */       this.nativeDelegate = ObjectFilter.betweenFloat(this.name, value, Conversions.toFloat(this.values[1]));
/*  617:     */       
/*  618:1148 */       break;
/*  619:     */     case 7: 
/*  620:1151 */       this.nativeDelegate = ObjectFilter.inFloats(this.name, Conversions.farray(this.values));
/*  621:1152 */       break;
/*  622:     */     case 9: 
/*  623:1156 */       this.nativeDelegate = ObjectFilter.notInFloats(this.name, Conversions.farray(this.values));
/*  624:     */       
/*  625:1158 */       break;
/*  626:     */     default: 
/*  627:1161 */       this.useDelegate = false;
/*  628:     */     }
/*  629:     */   }
/*  630:     */   
/*  631:     */   private void initForLong(long value)
/*  632:     */   {
/*  633:1167 */     this.value = Long.valueOf(value);
/*  634:1169 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  635:     */     {
/*  636:     */     case 1: 
/*  637:1171 */       this.nativeDelegate = ObjectFilter.eqLong(this.name, value);
/*  638:1172 */       break;
/*  639:     */     case 2: 
/*  640:1175 */       this.nativeDelegate = ObjectFilter.notEqLong(this.name, value);
/*  641:1176 */       break;
/*  642:     */     case 3: 
/*  643:1179 */       this.nativeDelegate = ObjectFilter.ltLong(this.name, value);
/*  644:1180 */       break;
/*  645:     */     case 4: 
/*  646:1183 */       this.nativeDelegate = ObjectFilter.lteLong(this.name, value);
/*  647:1184 */       break;
/*  648:     */     case 5: 
/*  649:1187 */       this.nativeDelegate = ObjectFilter.gtLong(this.name, value);
/*  650:1188 */       break;
/*  651:     */     case 6: 
/*  652:1191 */       this.nativeDelegate = ObjectFilter.gteLong(this.name, value);
/*  653:1192 */       break;
/*  654:     */     case 7: 
/*  655:1195 */       this.nativeDelegate = ObjectFilter.inLongs(this.name, Conversions.larray(this.values));
/*  656:1196 */       break;
/*  657:     */     case 8: 
/*  658:1200 */       this.nativeDelegate = ObjectFilter.betweenLong(this.name, value, Conversions.toLong(this.values[1]));
/*  659:     */       
/*  660:1202 */       break;
/*  661:     */     case 9: 
/*  662:1206 */       this.nativeDelegate = ObjectFilter.notInLongs(this.name, Conversions.larray(this.values));
/*  663:     */       
/*  664:1208 */       break;
/*  665:     */     default: 
/*  666:1211 */       this.useDelegate = false;
/*  667:     */     }
/*  668:     */   }
/*  669:     */   
/*  670:     */   private void initForByte(byte value)
/*  671:     */   {
/*  672:1217 */     this.value = Byte.valueOf(value);
/*  673:1219 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  674:     */     {
/*  675:     */     case 1: 
/*  676:1221 */       this.nativeDelegate = ObjectFilter.eqByte(this.name, value);
/*  677:1222 */       break;
/*  678:     */     case 2: 
/*  679:1225 */       this.nativeDelegate = ObjectFilter.notEqByte(this.name, value);
/*  680:1226 */       break;
/*  681:     */     case 3: 
/*  682:1229 */       this.nativeDelegate = ObjectFilter.ltByte(this.name, value);
/*  683:1230 */       break;
/*  684:     */     case 4: 
/*  685:1233 */       this.nativeDelegate = ObjectFilter.lteByte(this.name, value);
/*  686:1234 */       break;
/*  687:     */     case 5: 
/*  688:1237 */       this.nativeDelegate = ObjectFilter.gtByte(this.name, value);
/*  689:1238 */       break;
/*  690:     */     case 6: 
/*  691:1241 */       this.nativeDelegate = ObjectFilter.gteByte(this.name, value);
/*  692:1242 */       break;
/*  693:     */     case 7: 
/*  694:1245 */       this.nativeDelegate = ObjectFilter.inBytes(this.name, Conversions.barray(this.values));
/*  695:1246 */       break;
/*  696:     */     case 9: 
/*  697:1250 */       this.nativeDelegate = ObjectFilter.notInBytes(this.name, Conversions.barray(this.values));
/*  698:     */       
/*  699:1252 */       break;
/*  700:     */     case 8: 
/*  701:1256 */       this.nativeDelegate = ObjectFilter.betweenByte(this.name, value, Conversions.toByte(this.values[1]));
/*  702:     */       
/*  703:1258 */       break;
/*  704:     */     default: 
/*  705:1262 */       this.useDelegate = false;
/*  706:     */     }
/*  707:     */   }
/*  708:     */   
/*  709:     */   private void initForDate()
/*  710:     */   {
/*  711:1267 */     this.value = Conversions.toDate(this.value);
/*  712:1269 */     if (this.operator == Operator.BETWEEN)
/*  713:     */     {
/*  714:1270 */       this.values[0] = Conversions.toDate(this.values[0]);
/*  715:     */       
/*  716:1272 */       this.values[1] = Conversions.toDate(this.values[1]);
/*  717:     */     }
/*  718:     */   }
/*  719:     */   
/*  720:     */   private void initForInt(int v)
/*  721:     */   {
/*  722:1279 */     this.value = Integer.valueOf(v);
/*  723:1282 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[this.operator.ordinal()])
/*  724:     */     {
/*  725:     */     case 1: 
/*  726:1284 */       this.nativeDelegate = ObjectFilter.eqInt(this.name, v);
/*  727:1285 */       break;
/*  728:     */     case 2: 
/*  729:1288 */       this.nativeDelegate = ObjectFilter.notEqInt(this.name, v);
/*  730:1289 */       break;
/*  731:     */     case 3: 
/*  732:1292 */       this.nativeDelegate = ObjectFilter.ltInt(this.name, v);
/*  733:1293 */       break;
/*  734:     */     case 4: 
/*  735:1296 */       this.nativeDelegate = ObjectFilter.lteInt(this.name, v);
/*  736:1297 */       break;
/*  737:     */     case 5: 
/*  738:1300 */       this.nativeDelegate = ObjectFilter.gtInt(this.name, v);
/*  739:1301 */       break;
/*  740:     */     case 6: 
/*  741:1304 */       this.nativeDelegate = ObjectFilter.gteInt(this.name, v);
/*  742:1305 */       break;
/*  743:     */     case 8: 
/*  744:1308 */       this.nativeDelegate = ObjectFilter.betweenInt(this.name, v, Conversions.toInt(this.values[1]));
/*  745:     */       
/*  746:1310 */       break;
/*  747:     */     case 7: 
/*  748:1313 */       this.nativeDelegate = ObjectFilter.inInts(this.name, Conversions.iarray(this.values));
/*  749:1314 */       break;
/*  750:     */     case 9: 
/*  751:1318 */       this.nativeDelegate = ObjectFilter.notInInts(this.name, Conversions.iarray(this.values));
/*  752:     */       
/*  753:1320 */       break;
/*  754:     */     default: 
/*  755:1323 */       this.useDelegate = false;
/*  756:     */     }
/*  757:     */   }
/*  758:     */   
/*  759:     */   static class ThisMap
/*  760:     */     implements Map<String, FieldAccess>
/*  761:     */   {
/*  762:     */     Criterion.ThisField thisField;
/*  763:     */     private final String name;
/*  764:     */     
/*  765:     */     ThisMap(String name, Object thisObject)
/*  766:     */     {
/*  767:1336 */       this.name = name;
/*  768:1337 */       this.thisField = new Criterion.ThisField(name, thisObject);
/*  769:     */     }
/*  770:     */     
/*  771:     */     public int size()
/*  772:     */     {
/*  773:1343 */       return 0;
/*  774:     */     }
/*  775:     */     
/*  776:     */     public boolean isEmpty()
/*  777:     */     {
/*  778:1348 */       return false;
/*  779:     */     }
/*  780:     */     
/*  781:     */     public boolean containsKey(Object key)
/*  782:     */     {
/*  783:1353 */       return false;
/*  784:     */     }
/*  785:     */     
/*  786:     */     public boolean containsValue(Object value)
/*  787:     */     {
/*  788:1358 */       return false;
/*  789:     */     }
/*  790:     */     
/*  791:     */     public FieldAccess get(Object key)
/*  792:     */     {
/*  793:1363 */       return this.thisField;
/*  794:     */     }
/*  795:     */     
/*  796:     */     public FieldAccess put(String key, FieldAccess value)
/*  797:     */     {
/*  798:1369 */       return null;
/*  799:     */     }
/*  800:     */     
/*  801:     */     public FieldAccess remove(Object key)
/*  802:     */     {
/*  803:1374 */       return null;
/*  804:     */     }
/*  805:     */     
/*  806:     */     public void putAll(Map<? extends String, ? extends FieldAccess> m) {}
/*  807:     */     
/*  808:     */     public void clear() {}
/*  809:     */     
/*  810:     */     public Set<String> keySet()
/*  811:     */     {
/*  812:1389 */       return null;
/*  813:     */     }
/*  814:     */     
/*  815:     */     public Collection<FieldAccess> values()
/*  816:     */     {
/*  817:1394 */       return null;
/*  818:     */     }
/*  819:     */     
/*  820:     */     public Set<Map.Entry<String, FieldAccess>> entrySet()
/*  821:     */     {
/*  822:1399 */       return null;
/*  823:     */     }
/*  824:     */   }
/*  825:     */   
/*  826:     */   static class ThisField
/*  827:     */     implements FieldAccess
/*  828:     */   {
/*  829:     */     Object thisObject;
/*  830:     */     private final String name;
/*  831:     */     
/*  832:     */     ThisField(String name, Object thisObject)
/*  833:     */     {
/*  834:1410 */       this.name = name;
/*  835:1411 */       this.thisObject = thisObject;
/*  836:     */     }
/*  837:     */     
/*  838:     */     public boolean injectable()
/*  839:     */     {
/*  840:1418 */       return false;
/*  841:     */     }
/*  842:     */     
/*  843:     */     public boolean requiresInjection()
/*  844:     */     {
/*  845:1423 */       return false;
/*  846:     */     }
/*  847:     */     
/*  848:     */     public boolean isNamed()
/*  849:     */     {
/*  850:1428 */       return false;
/*  851:     */     }
/*  852:     */     
/*  853:     */     public boolean hasAlias()
/*  854:     */     {
/*  855:1433 */       return false;
/*  856:     */     }
/*  857:     */     
/*  858:     */     public String alias()
/*  859:     */     {
/*  860:1438 */       return null;
/*  861:     */     }
/*  862:     */     
/*  863:     */     public String named()
/*  864:     */     {
/*  865:1443 */       return null;
/*  866:     */     }
/*  867:     */     
/*  868:     */     public String name()
/*  869:     */     {
/*  870:1448 */       return null;
/*  871:     */     }
/*  872:     */     
/*  873:     */     public Object getValue(Object obj)
/*  874:     */     {
/*  875:1453 */       if (this.name.equals("this")) {
/*  876:1454 */         return this.thisObject;
/*  877:     */       }
/*  878:1456 */       return BeanUtils.atIndex(this.thisObject, this.name);
/*  879:     */     }
/*  880:     */     
/*  881:     */     public void setValue(Object obj, Object value) {}
/*  882:     */     
/*  883:     */     public void setFromValue(Object obj, Value value) {}
/*  884:     */     
/*  885:     */     public boolean getBoolean(Object obj)
/*  886:     */     {
/*  887:1472 */       return false;
/*  888:     */     }
/*  889:     */     
/*  890:     */     public void setBoolean(Object obj, boolean value) {}
/*  891:     */     
/*  892:     */     public int getInt(Object obj)
/*  893:     */     {
/*  894:1482 */       return 0;
/*  895:     */     }
/*  896:     */     
/*  897:     */     public void setInt(Object obj, int value) {}
/*  898:     */     
/*  899:     */     public short getShort(Object obj)
/*  900:     */     {
/*  901:1492 */       return 0;
/*  902:     */     }
/*  903:     */     
/*  904:     */     public void setShort(Object obj, short value) {}
/*  905:     */     
/*  906:     */     public char getChar(Object obj)
/*  907:     */     {
/*  908:1502 */       return '\000';
/*  909:     */     }
/*  910:     */     
/*  911:     */     public void setChar(Object obj, char value) {}
/*  912:     */     
/*  913:     */     public long getLong(Object obj)
/*  914:     */     {
/*  915:1512 */       return 0L;
/*  916:     */     }
/*  917:     */     
/*  918:     */     public void setLong(Object obj, long value) {}
/*  919:     */     
/*  920:     */     public double getDouble(Object obj)
/*  921:     */     {
/*  922:1522 */       return 0.0D;
/*  923:     */     }
/*  924:     */     
/*  925:     */     public void setDouble(Object obj, double value) {}
/*  926:     */     
/*  927:     */     public float getFloat(Object obj)
/*  928:     */     {
/*  929:1532 */       return 0.0F;
/*  930:     */     }
/*  931:     */     
/*  932:     */     public void setFloat(Object obj, float value) {}
/*  933:     */     
/*  934:     */     public byte getByte(Object obj)
/*  935:     */     {
/*  936:1542 */       return 0;
/*  937:     */     }
/*  938:     */     
/*  939:     */     public void setByte(Object obj, byte value) {}
/*  940:     */     
/*  941:     */     public Object getObject(Object obj)
/*  942:     */     {
/*  943:1552 */       return this.thisObject;
/*  944:     */     }
/*  945:     */     
/*  946:     */     public void setObject(Object obj, Object value) {}
/*  947:     */     
/*  948:     */     public TypeType typeEnum()
/*  949:     */     {
/*  950:1562 */       Object o = getValue(this.thisObject);
/*  951:1563 */       return TypeType.getInstanceType(o);
/*  952:     */     }
/*  953:     */     
/*  954:     */     public boolean isPrimitive()
/*  955:     */     {
/*  956:1568 */       return false;
/*  957:     */     }
/*  958:     */     
/*  959:     */     public boolean isFinal()
/*  960:     */     {
/*  961:1573 */       return false;
/*  962:     */     }
/*  963:     */     
/*  964:     */     public boolean isStatic()
/*  965:     */     {
/*  966:1578 */       return false;
/*  967:     */     }
/*  968:     */     
/*  969:     */     public boolean isVolatile()
/*  970:     */     {
/*  971:1583 */       return false;
/*  972:     */     }
/*  973:     */     
/*  974:     */     public boolean isQualified()
/*  975:     */     {
/*  976:1588 */       return false;
/*  977:     */     }
/*  978:     */     
/*  979:     */     public boolean isReadOnly()
/*  980:     */     {
/*  981:1593 */       return false;
/*  982:     */     }
/*  983:     */     
/*  984:     */     public boolean isWriteOnly()
/*  985:     */     {
/*  986:1598 */       return false;
/*  987:     */     }
/*  988:     */     
/*  989:     */     public Class<?> type()
/*  990:     */     {
/*  991:1603 */       if (this.name.equals("this")) {
/*  992:1604 */         return this.thisObject != null ? this.thisObject.getClass() : Object.class;
/*  993:     */       }
/*  994:1606 */       return Object.class;
/*  995:     */     }
/*  996:     */     
/*  997:     */     public Class<?> declaringParent()
/*  998:     */     {
/*  999:1612 */       return null;
/* 1000:     */     }
/* 1001:     */     
/* 1002:     */     public Object parent()
/* 1003:     */     {
/* 1004:1617 */       return null;
/* 1005:     */     }
/* 1006:     */     
/* 1007:     */     public Field getField()
/* 1008:     */     {
/* 1009:1622 */       return null;
/* 1010:     */     }
/* 1011:     */     
/* 1012:     */     public boolean include()
/* 1013:     */     {
/* 1014:1627 */       return false;
/* 1015:     */     }
/* 1016:     */     
/* 1017:     */     public boolean ignore()
/* 1018:     */     {
/* 1019:1632 */       return false;
/* 1020:     */     }
/* 1021:     */     
/* 1022:     */     public ParameterizedType getParameterizedType()
/* 1023:     */     {
/* 1024:1637 */       return null;
/* 1025:     */     }
/* 1026:     */     
/* 1027:     */     public Class<?> getComponentClass()
/* 1028:     */     {
/* 1029:1642 */       Object o = getValue(this.thisObject);
/* 1030:1643 */       if ((o instanceof Collection))
/* 1031:     */       {
/* 1032:1644 */         Collection c = (Collection)o;
/* 1033:1645 */         if (c.iterator().hasNext()) {
/* 1034:1646 */           return c.iterator().next().getClass();
/* 1035:     */         }
/* 1036:     */       }
/* 1037:1650 */       return Object.class;
/* 1038:     */     }
/* 1039:     */     
/* 1040:     */     public boolean hasAnnotation(String annotationName)
/* 1041:     */     {
/* 1042:1656 */       return false;
/* 1043:     */     }
/* 1044:     */     
/* 1045:     */     public Map<String, Object> getAnnotationData(String annotationName)
/* 1046:     */     {
/* 1047:1661 */       return null;
/* 1048:     */     }
/* 1049:     */     
/* 1050:     */     public boolean isViewActive(String activeView)
/* 1051:     */     {
/* 1052:1666 */       return false;
/* 1053:     */     }
/* 1054:     */     
/* 1055:     */     public void setStaticValue(Object newValue) {}
/* 1056:     */     
/* 1057:     */     public TypeType componentType()
/* 1058:     */     {
/* 1059:1676 */       return null;
/* 1060:     */     }
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   private static class MyNumber
/* 1064:     */     extends Number
/* 1065:     */     implements Comparable<Number>
/* 1066:     */   {
/* 1067:     */     Object value;
/* 1068:     */     
/* 1069:     */     MyNumber(Object value)
/* 1070:     */     {
/* 1071:1686 */       this.value = value;
/* 1072:     */     }
/* 1073:     */     
/* 1074:     */     public int intValue()
/* 1075:     */     {
/* 1076:1691 */       return Conversions.toInt(this.value);
/* 1077:     */     }
/* 1078:     */     
/* 1079:     */     public long longValue()
/* 1080:     */     {
/* 1081:1697 */       return Conversions.toLong(this.value);
/* 1082:     */     }
/* 1083:     */     
/* 1084:     */     public float floatValue()
/* 1085:     */     {
/* 1086:1703 */       return Conversions.toFloat(this.value);
/* 1087:     */     }
/* 1088:     */     
/* 1089:     */     public double doubleValue()
/* 1090:     */     {
/* 1091:1708 */       return Conversions.toDouble(this.value);
/* 1092:     */     }
/* 1093:     */     
/* 1094:     */     public int compareTo(Number number)
/* 1095:     */     {
/* 1096:1713 */       double thisDouble = doubleValue();
/* 1097:     */       
/* 1098:1715 */       double thatValue = number.doubleValue();
/* 1099:1717 */       if (thisDouble > thatValue) {
/* 1100:1718 */         return 1;
/* 1101:     */       }
/* 1102:1719 */       if (thisDouble < thatValue) {
/* 1103:1720 */         return -1;
/* 1104:     */       }
/* 1105:1722 */       return 0;
/* 1106:     */     }
/* 1107:     */   }
/* 1108:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.Criterion
 * JD-Core Version:    0.7.0.1
 */