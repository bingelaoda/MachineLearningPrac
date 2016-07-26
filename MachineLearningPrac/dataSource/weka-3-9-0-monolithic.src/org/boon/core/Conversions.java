/*    1:     */ package org.boon.core;
/*    2:     */ 
/*    3:     */ import java.lang.reflect.Array;
/*    4:     */ import java.math.BigDecimal;
/*    5:     */ import java.math.BigInteger;
/*    6:     */ import java.text.DateFormat;
/*    7:     */ import java.text.ParseException;
/*    8:     */ import java.util.ArrayList;
/*    9:     */ import java.util.Calendar;
/*   10:     */ import java.util.Collection;
/*   11:     */ import java.util.Collections;
/*   12:     */ import java.util.Currency;
/*   13:     */ import java.util.Date;
/*   14:     */ import java.util.HashMap;
/*   15:     */ import java.util.HashSet;
/*   16:     */ import java.util.Iterator;
/*   17:     */ import java.util.LinkedHashSet;
/*   18:     */ import java.util.LinkedList;
/*   19:     */ import java.util.List;
/*   20:     */ import java.util.Map;
/*   21:     */ import java.util.Set;
/*   22:     */ import java.util.SortedMap;
/*   23:     */ import java.util.SortedSet;
/*   24:     */ import java.util.TimeZone;
/*   25:     */ import java.util.TreeMap;
/*   26:     */ import java.util.TreeSet;
/*   27:     */ import java.util.UUID;
/*   28:     */ import java.util.concurrent.ConcurrentHashMap;
/*   29:     */ import java.util.logging.Logger;
/*   30:     */ import org.boon.Boon;
/*   31:     */ import org.boon.Exceptions;
/*   32:     */ import org.boon.IO;
/*   33:     */ import org.boon.Lists;
/*   34:     */ import org.boon.Str;
/*   35:     */ import org.boon.StringScanner;
/*   36:     */ import org.boon.core.reflection.BeanUtils;
/*   37:     */ import org.boon.core.reflection.ClassMeta;
/*   38:     */ import org.boon.core.reflection.ConstructorAccess;
/*   39:     */ import org.boon.core.reflection.FastStringUtils;
/*   40:     */ import org.boon.core.reflection.MapObjectConversion;
/*   41:     */ import org.boon.core.reflection.Reflection;
/*   42:     */ import org.boon.primitive.Arry;
/*   43:     */ 
/*   44:     */ public class Conversions
/*   45:     */ {
/*   46:  53 */   private static final Logger log = Logger.getLogger(Conversions.class.getName());
/*   47:     */   
/*   48:     */   public static BigDecimal toBigDecimal(Object obj)
/*   49:     */   {
/*   50:  57 */     if ((obj instanceof BigDecimal)) {
/*   51:  58 */       return (BigDecimal)obj;
/*   52:     */     }
/*   53:  61 */     if ((obj instanceof Value)) {
/*   54:  62 */       return ((Value)obj).bigDecimalValue();
/*   55:     */     }
/*   56:  63 */     if ((obj instanceof String)) {
/*   57:  64 */       return new BigDecimal((String)obj);
/*   58:     */     }
/*   59:  65 */     if ((obj instanceof Number))
/*   60:     */     {
/*   61:  66 */       double val = ((Number)obj).doubleValue();
/*   62:  67 */       return BigDecimal.valueOf(val);
/*   63:     */     }
/*   64:  70 */     return null;
/*   65:     */   }
/*   66:     */   
/*   67:     */   public static BigInteger toBigInteger(Object obj)
/*   68:     */   {
/*   69:  75 */     if ((obj instanceof BigInteger)) {
/*   70:  76 */       return (BigInteger)obj;
/*   71:     */     }
/*   72:  79 */     if ((obj instanceof Value)) {
/*   73:  80 */       return ((Value)obj).bigIntegerValue();
/*   74:     */     }
/*   75:  81 */     if ((obj instanceof String)) {
/*   76:  82 */       return new BigInteger((String)obj);
/*   77:     */     }
/*   78:  83 */     if ((obj instanceof Number))
/*   79:     */     {
/*   80:  84 */       long val = ((Number)obj).longValue();
/*   81:  85 */       return BigInteger.valueOf(val);
/*   82:     */     }
/*   83:  88 */     return null;
/*   84:     */   }
/*   85:     */   
/*   86:     */   public static int toInt(Object obj)
/*   87:     */   {
/*   88:  94 */     return toInt(obj, -2147483648);
/*   89:     */   }
/*   90:     */   
/*   91:     */   public static int toInt(Object obj, int defaultValue)
/*   92:     */   {
/*   93:  98 */     if (obj.getClass() == Integer.TYPE) {
/*   94:  99 */       return ((Integer)Integer.TYPE.cast(obj)).intValue();
/*   95:     */     }
/*   96: 101 */     if ((obj instanceof Number)) {
/*   97: 102 */       return ((Number)obj).intValue();
/*   98:     */     }
/*   99: 103 */     if (((obj instanceof Boolean)) || (obj.getClass() == Boolean.class))
/*  100:     */     {
/*  101: 104 */       boolean value = toBoolean(obj);
/*  102: 105 */       return value ? 1 : 0;
/*  103:     */     }
/*  104: 106 */     if ((obj instanceof CharSequence)) {
/*  105:     */       try
/*  106:     */       {
/*  107: 108 */         return StringScanner.parseInt(Str.toString(obj));
/*  108:     */       }
/*  109:     */       catch (Exception ex)
/*  110:     */       {
/*  111: 110 */         return defaultValue;
/*  112:     */       }
/*  113:     */     }
/*  114: 113 */     return defaultValue;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public static byte toByte(Object obj)
/*  118:     */   {
/*  119: 119 */     return toByte(obj, (byte)-128);
/*  120:     */   }
/*  121:     */   
/*  122:     */   public static byte toByte(Object obj, byte defaultByte)
/*  123:     */   {
/*  124: 123 */     if (obj.getClass() == Byte.TYPE) {
/*  125: 124 */       return ((Byte)Byte.TYPE.cast(obj)).byteValue();
/*  126:     */     }
/*  127: 125 */     if ((obj instanceof Number)) {
/*  128: 126 */       return ((Number)obj).byteValue();
/*  129:     */     }
/*  130: 128 */     return (byte)toInt(obj, defaultByte);
/*  131:     */   }
/*  132:     */   
/*  133:     */   public static short toShort(Object obj)
/*  134:     */   {
/*  135: 133 */     return toShort(obj, (short)-32768);
/*  136:     */   }
/*  137:     */   
/*  138:     */   public static short toShort(Object obj, short shortDefault)
/*  139:     */   {
/*  140: 138 */     if (obj.getClass() == Short.TYPE) {
/*  141: 139 */       return ((Short)Short.TYPE.cast(obj)).shortValue();
/*  142:     */     }
/*  143: 140 */     if ((obj instanceof Number)) {
/*  144: 141 */       return ((Number)obj).shortValue();
/*  145:     */     }
/*  146: 143 */     return (short)toInt(obj, shortDefault);
/*  147:     */   }
/*  148:     */   
/*  149:     */   public static char toChar(Object obj)
/*  150:     */   {
/*  151: 148 */     return toChar(obj, '\000');
/*  152:     */   }
/*  153:     */   
/*  154:     */   public static char toChar(Object obj, char defaultChar)
/*  155:     */   {
/*  156: 152 */     if (obj.getClass() == Character.TYPE) {
/*  157: 153 */       return ((Character)Character.TYPE.cast(obj)).charValue();
/*  158:     */     }
/*  159: 154 */     if ((obj instanceof Character)) {
/*  160: 155 */       return ((Character)obj).charValue();
/*  161:     */     }
/*  162: 156 */     if ((obj instanceof CharSequence)) {
/*  163: 157 */       return obj.toString().charAt(0);
/*  164:     */     }
/*  165: 158 */     if ((obj instanceof Number)) {
/*  166: 159 */       return (char)toInt(obj);
/*  167:     */     }
/*  168: 160 */     if (((obj instanceof Boolean)) || (obj.getClass() == Boolean.class))
/*  169:     */     {
/*  170: 161 */       boolean value = toBoolean(obj);
/*  171: 162 */       return value ? 'T' : 'F';
/*  172:     */     }
/*  173: 163 */     if (obj.getClass().isPrimitive()) {
/*  174: 164 */       return (char)toInt(obj);
/*  175:     */     }
/*  176: 166 */     String str = toString(obj);
/*  177: 167 */     if (str.length() > 0) {
/*  178: 168 */       return str.charAt(0);
/*  179:     */     }
/*  180: 170 */     return defaultChar;
/*  181:     */   }
/*  182:     */   
/*  183:     */   public static long toLong(Object obj)
/*  184:     */   {
/*  185: 176 */     return toLong(obj, -9223372036854775808L);
/*  186:     */   }
/*  187:     */   
/*  188:     */   public static long toLongOrDie(Object obj)
/*  189:     */   {
/*  190: 181 */     long l = toLong(obj, -9223372036854775808L);
/*  191: 182 */     if (l == -9223372036854775808L) {
/*  192: 183 */       Exceptions.die(new Object[] { "Cannot convert", obj, "into long value", obj });
/*  193:     */     }
/*  194: 185 */     return l;
/*  195:     */   }
/*  196:     */   
/*  197:     */   public static long toLong(Object obj, long longDefault)
/*  198:     */   {
/*  199: 190 */     if ((obj instanceof Long)) {
/*  200: 191 */       return ((Long)obj).longValue();
/*  201:     */     }
/*  202: 194 */     if ((obj instanceof Number)) {
/*  203: 195 */       return ((Number)obj).longValue();
/*  204:     */     }
/*  205: 196 */     if ((obj instanceof CharSequence))
/*  206:     */     {
/*  207: 197 */       String str = Str.toString(obj);
/*  208: 198 */       if (Dates.isJsonDate(str)) {
/*  209: 199 */         return Dates.fromJsonDate(str).getTime();
/*  210:     */       }
/*  211:     */       try
/*  212:     */       {
/*  213: 203 */         return StringScanner.parseLong(str);
/*  214:     */       }
/*  215:     */       catch (Exception ex)
/*  216:     */       {
/*  217: 205 */         return longDefault;
/*  218:     */       }
/*  219:     */     }
/*  220: 207 */     if ((obj instanceof Date)) {
/*  221: 208 */       return ((Date)obj).getTime();
/*  222:     */     }
/*  223: 210 */     return toInt(obj);
/*  224:     */   }
/*  225:     */   
/*  226:     */   public static Currency toCurrency(Object obj)
/*  227:     */   {
/*  228: 216 */     if ((obj instanceof Currency)) {
/*  229: 217 */       return (Currency)obj;
/*  230:     */     }
/*  231: 220 */     if ((obj instanceof String))
/*  232:     */     {
/*  233: 221 */       String str = Str.toString(obj);
/*  234: 222 */       return Currency.getInstance(str);
/*  235:     */     }
/*  236: 225 */     return null;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public static boolean toBoolean(Object value)
/*  240:     */   {
/*  241: 235 */     return toBoolean(value, false);
/*  242:     */   }
/*  243:     */   
/*  244:     */   public static boolean toBoolean(Object obj, boolean defaultValue)
/*  245:     */   {
/*  246: 248 */     if (obj == null) {
/*  247: 249 */       return defaultValue;
/*  248:     */     }
/*  249: 252 */     if ((obj instanceof Boolean)) {
/*  250: 253 */       return ((Boolean)obj).booleanValue();
/*  251:     */     }
/*  252: 254 */     if (((obj instanceof Number)) || (obj.getClass().isPrimitive()))
/*  253:     */     {
/*  254: 255 */       int value = toInt(obj);
/*  255: 256 */       return value != 0;
/*  256:     */     }
/*  257: 257 */     if (((obj instanceof String)) || ((obj instanceof CharSequence)))
/*  258:     */     {
/*  259: 258 */       String str = toString(obj);
/*  260: 259 */       if (str.length() == 0) {
/*  261: 260 */         return false;
/*  262:     */       }
/*  263: 262 */       if (str.equals("false")) {
/*  264: 263 */         return false;
/*  265:     */       }
/*  266: 265 */       return true;
/*  267:     */     }
/*  268: 267 */     if (Boon.isArray(obj)) {
/*  269: 268 */       return Boon.len(obj) > 0;
/*  270:     */     }
/*  271: 269 */     if ((obj instanceof Collection))
/*  272:     */     {
/*  273: 271 */       if (len(obj) > 0)
/*  274:     */       {
/*  275: 272 */         List list = Lists.list((Collection)obj);
/*  276: 273 */         while (list.remove(null)) {}
/*  277: 276 */         return Lists.len(list) > 0;
/*  278:     */       }
/*  279: 278 */       return false;
/*  280:     */     }
/*  281: 281 */     return toBoolean(toString(obj));
/*  282:     */   }
/*  283:     */   
/*  284:     */   public static boolean toBooleanOrDie(Object obj)
/*  285:     */   {
/*  286: 287 */     if (obj == null) {
/*  287: 288 */       Exceptions.die("Can't convert boolean from a null");
/*  288:     */     }
/*  289: 291 */     if ((obj instanceof Boolean)) {
/*  290: 292 */       return ((Boolean)obj).booleanValue();
/*  291:     */     }
/*  292: 293 */     if (((obj instanceof String)) || ((obj instanceof CharSequence)))
/*  293:     */     {
/*  294: 294 */       String str = toString(obj);
/*  295: 295 */       if (str.length() == 0) {
/*  296: 296 */         return false;
/*  297:     */       }
/*  298: 298 */       if (str.equals("false")) {
/*  299: 299 */         return false;
/*  300:     */       }
/*  301: 300 */       if (str.equals("true")) {
/*  302: 301 */         return true;
/*  303:     */       }
/*  304: 303 */       Exceptions.die(new Object[] { "Can't convert string", obj, "to boolean " });
/*  305: 304 */       return false;
/*  306:     */     }
/*  307: 306 */     Exceptions.die(new Object[] { "Can't convert", obj, "to boolean " });
/*  308: 307 */     return false;
/*  309:     */   }
/*  310:     */   
/*  311:     */   public static double toDouble(Object obj)
/*  312:     */   {
/*  313:     */     try
/*  314:     */     {
/*  315: 315 */       if ((obj instanceof Double)) {
/*  316: 316 */         return ((Double)obj).doubleValue();
/*  317:     */       }
/*  318: 317 */       if ((obj instanceof Number)) {
/*  319: 318 */         return ((Number)obj).doubleValue();
/*  320:     */       }
/*  321: 319 */       if ((obj instanceof CharSequence)) {
/*  322:     */         try
/*  323:     */         {
/*  324: 321 */           return Double.parseDouble(((CharSequence)obj).toString());
/*  325:     */         }
/*  326:     */         catch (Exception ex)
/*  327:     */         {
/*  328: 323 */           Exceptions.die(String.format("Unable to convert %s to a double", new Object[] { obj.getClass() }));
/*  329: 324 */           return 4.9E-324D;
/*  330:     */         }
/*  331:     */       }
/*  332:     */     }
/*  333:     */     catch (Exception ex)
/*  334:     */     {
/*  335: 329 */       log.warning(String.format("unable to convert to double and there was an exception %s", new Object[] { ex.getMessage() }));
/*  336:     */     }
/*  337: 334 */     Exceptions.die(String.format("Unable to convert %s to a double", new Object[] { obj.getClass() }));
/*  338: 335 */     return 4.9E-324D;
/*  339:     */   }
/*  340:     */   
/*  341:     */   public static float toFloat(Object obj)
/*  342:     */   {
/*  343: 340 */     if (obj.getClass() == Float.TYPE) {
/*  344: 341 */       return ((Float)obj).floatValue();
/*  345:     */     }
/*  346:     */     try
/*  347:     */     {
/*  348: 345 */       if ((obj instanceof Float)) {
/*  349: 346 */         return ((Float)obj).floatValue();
/*  350:     */       }
/*  351: 347 */       if ((obj instanceof Number)) {
/*  352: 348 */         return ((Number)obj).floatValue();
/*  353:     */       }
/*  354: 349 */       if ((obj instanceof CharSequence)) {
/*  355:     */         try
/*  356:     */         {
/*  357: 351 */           return Float.parseFloat(Str.toString(obj));
/*  358:     */         }
/*  359:     */         catch (Exception ex)
/*  360:     */         {
/*  361: 353 */           Exceptions.die(String.format("Unable to convert %s to a float", new Object[] { obj.getClass() }));
/*  362: 354 */           return 1.4E-45F;
/*  363:     */         }
/*  364:     */       }
/*  365:     */     }
/*  366:     */     catch (Exception ex)
/*  367:     */     {
/*  368: 360 */       log.warning(String.format("unable to convert to float and there was an exception %s", new Object[] { ex.getMessage() }));
/*  369:     */     }
/*  370: 365 */     Exceptions.die(String.format("Unable to convert %s to a float", new Object[] { obj.getClass() }));
/*  371: 366 */     return 1.4E-45F;
/*  372:     */   }
/*  373:     */   
/*  374:     */   public static <T> T coerce(Class<T> clz, Object value)
/*  375:     */   {
/*  376: 372 */     if ((value != null) && 
/*  377: 373 */       (clz == value.getClass())) {
/*  378: 374 */       return value;
/*  379:     */     }
/*  380: 378 */     return coerce(TypeType.getType(clz), clz, value);
/*  381:     */   }
/*  382:     */   
/*  383:     */   public static <T> T createFromArg(Class<T> clz, Object value)
/*  384:     */   {
/*  385: 382 */     if (value == null) {
/*  386: 383 */       return Reflection.newInstance(clz);
/*  387:     */     }
/*  388: 385 */     ClassMeta meta = ClassMeta.classMeta(clz);
/*  389: 386 */     List<ConstructorAccess> constructors = meta.oneArgumentConstructors();
/*  390: 388 */     if (constructors.size() == 0) {
/*  391: 389 */       return null;
/*  392:     */     }
/*  393: 390 */     if (constructors.size() == 1)
/*  394:     */     {
/*  395: 391 */       ConstructorAccess constructorAccess = (ConstructorAccess)constructors.get(0);
/*  396: 392 */       Class<?> arg1Type = constructorAccess.parameterTypes()[0];
/*  397: 393 */       if (arg1Type.isInstance(value)) {
/*  398: 394 */         return constructorAccess.create(new Object[] { value });
/*  399:     */       }
/*  400: 396 */       return constructorAccess.create(new Object[] { coerce(arg1Type, value) });
/*  401:     */     }
/*  402: 399 */     for (ConstructorAccess c : constructors)
/*  403:     */     {
/*  404: 400 */       Class<?> arg1Type = c.parameterTypes()[0];
/*  405: 401 */       if (arg1Type.isInstance(value)) {
/*  406: 402 */         return c.create(new Object[] { value });
/*  407:     */       }
/*  408:     */     }
/*  409: 407 */     for (ConstructorAccess c : constructors)
/*  410:     */     {
/*  411: 408 */       Class<?> arg1Type = c.parameterTypes()[0];
/*  412: 409 */       if (arg1Type.isAssignableFrom(value.getClass())) {
/*  413: 410 */         return c.create(new Object[] { value });
/*  414:     */       }
/*  415:     */     }
/*  416: 414 */     return null;
/*  417:     */   }
/*  418:     */   
/*  419:     */   public static <T> T coerce(TypeType coerceTo, Class<T> clz, Object value)
/*  420:     */   {
/*  421: 418 */     if (value == null)
/*  422:     */     {
/*  423: 419 */       if ((coerceTo != TypeType.INSTANCE) && (!clz.isPrimitive())) {
/*  424: 421 */         return null;
/*  425:     */       }
/*  426: 422 */       if (clz.isPrimitive())
/*  427:     */       {
/*  428: 423 */         if (clz == Boolean.TYPE) {
/*  429: 424 */           return Boolean.valueOf(false);
/*  430:     */         }
/*  431: 426 */         return Integer.valueOf(0);
/*  432:     */       }
/*  433:     */     }
/*  434: 430 */     switch (2.$SwitchMap$org$boon$core$TypeType[coerceTo.ordinal()])
/*  435:     */     {
/*  436:     */     case 1: 
/*  437:     */     case 2: 
/*  438: 433 */       return value.toString();
/*  439:     */     case 3: 
/*  440: 438 */       if ((value instanceof Number)) {
/*  441: 439 */         return value;
/*  442:     */       }
/*  443: 441 */       Double d = Double.valueOf(toDouble(value));
/*  444: 442 */       return d;
/*  445:     */     case 4: 
/*  446:     */     case 5: 
/*  447: 447 */       Integer i = Integer.valueOf(toInt(value));
/*  448: 448 */       return i;
/*  449:     */     case 6: 
/*  450:     */     case 7: 
/*  451: 452 */       Short s = Short.valueOf(toShort(value));
/*  452: 453 */       return s;
/*  453:     */     case 8: 
/*  454:     */     case 9: 
/*  455: 458 */       Byte by = Byte.valueOf(toByte(value));
/*  456: 459 */       return by;
/*  457:     */     case 10: 
/*  458:     */     case 11: 
/*  459: 464 */       Character ch = Character.valueOf(toChar(value));
/*  460: 465 */       return ch;
/*  461:     */     case 12: 
/*  462:     */     case 13: 
/*  463: 469 */       Long l = Long.valueOf(toLong(value));
/*  464: 470 */       return l;
/*  465:     */     case 14: 
/*  466:     */     case 15: 
/*  467: 474 */       Double d = Double.valueOf(toDouble(value));
/*  468: 475 */       return d;
/*  469:     */     case 16: 
/*  470:     */     case 17: 
/*  471: 480 */       Float f = Float.valueOf(toFloat(value));
/*  472: 481 */       return f;
/*  473:     */     case 18: 
/*  474: 485 */       return toDate(value);
/*  475:     */     case 19: 
/*  476: 488 */       return toBigDecimal(value);
/*  477:     */     case 20: 
/*  478: 492 */       return toBigInteger(value);
/*  479:     */     case 21: 
/*  480: 495 */       return toCalendar(toDate(value));
/*  481:     */     case 22: 
/*  482:     */     case 23: 
/*  483: 499 */       return Boolean.valueOf(toBoolean(value));
/*  484:     */     case 24: 
/*  485: 502 */       return toMap(value);
/*  486:     */     case 25: 
/*  487:     */     case 26: 
/*  488:     */     case 27: 
/*  489:     */     case 28: 
/*  490:     */     case 29: 
/*  491:     */     case 30: 
/*  492:     */     case 31: 
/*  493:     */     case 32: 
/*  494:     */     case 33: 
/*  495: 514 */       return toPrimitiveArrayIfPossible(clz, value);
/*  496:     */     case 34: 
/*  497:     */     case 35: 
/*  498:     */     case 36: 
/*  499: 520 */       return toCollection(clz, value);
/*  500:     */     case 37: 
/*  501: 524 */       if ((value instanceof Value)) {
/*  502: 525 */         value = ((Value)value).toValue();
/*  503:     */       }
/*  504: 527 */       if ((value instanceof Map)) {
/*  505: 528 */         return MapObjectConversion.fromMap((Map)value, clz);
/*  506:     */       }
/*  507: 529 */       if ((value instanceof List)) {
/*  508: 530 */         return MapObjectConversion.fromList((List)value, clz);
/*  509:     */       }
/*  510: 531 */       if (clz.isInstance(value)) {
/*  511: 532 */         return value;
/*  512:     */       }
/*  513: 534 */       return createFromArg(clz, value);
/*  514:     */     case 38: 
/*  515: 538 */       return toEnum(clz, value);
/*  516:     */     case 39: 
/*  517: 542 */       return IO.path(value.toString());
/*  518:     */     case 40: 
/*  519: 546 */       return toClass(value);
/*  520:     */     case 41: 
/*  521: 549 */       return toTimeZone(value);
/*  522:     */     case 42: 
/*  523: 554 */       return toUUID(value);
/*  524:     */     case 43: 
/*  525: 557 */       return toCurrency(value);
/*  526:     */     case 44: 
/*  527: 560 */       return value;
/*  528:     */     case 45: 
/*  529: 564 */       return value;
/*  530:     */     }
/*  531: 567 */     return createFromArg(clz, value);
/*  532:     */   }
/*  533:     */   
/*  534:     */   private static UUID toUUID(Object value)
/*  535:     */   {
/*  536: 572 */     return UUID.fromString(value.toString());
/*  537:     */   }
/*  538:     */   
/*  539:     */   public static <T> T coerceWithFlag(Class<T> clz, boolean[] flag, Object value)
/*  540:     */   {
/*  541: 578 */     return coerceWithFlag(TypeType.getType(clz), clz, flag, value);
/*  542:     */   }
/*  543:     */   
/*  544:     */   public static <T> T coerceWithFlag(TypeType coerceTo, Class<T> clz, boolean[] flag, Object value)
/*  545:     */   {
/*  546: 583 */     flag[0] = true;
/*  547: 584 */     if (value == null) {
/*  548: 585 */       return null;
/*  549:     */     }
/*  550: 588 */     if (clz.isInstance(value)) {
/*  551: 589 */       return value;
/*  552:     */     }
/*  553: 592 */     switch (2.$SwitchMap$org$boon$core$TypeType[coerceTo.ordinal()])
/*  554:     */     {
/*  555:     */     case 1: 
/*  556:     */     case 2: 
/*  557: 595 */       return value.toString();
/*  558:     */     case 4: 
/*  559:     */     case 5: 
/*  560: 599 */       Integer i = Integer.valueOf(toInt(value));
/*  561: 600 */       if (i.intValue() == -2147483648) {
/*  562: 601 */         flag[0] = false;
/*  563:     */       }
/*  564: 603 */       return i;
/*  565:     */     case 6: 
/*  566:     */     case 7: 
/*  567: 608 */       Short s = Short.valueOf(toShort(value));
/*  568: 609 */       if (s.shortValue() == -32768) {
/*  569: 610 */         flag[0] = false;
/*  570:     */       }
/*  571: 612 */       return s;
/*  572:     */     case 8: 
/*  573:     */     case 9: 
/*  574: 618 */       Byte by = Byte.valueOf(toByte(value));
/*  575: 619 */       if (by.byteValue() == -128) {
/*  576: 620 */         flag[0] = false;
/*  577:     */       }
/*  578: 622 */       return by;
/*  579:     */     case 10: 
/*  580:     */     case 11: 
/*  581: 627 */       Character ch = Character.valueOf(toChar(value));
/*  582: 628 */       if (ch.charValue() == 0) {
/*  583: 629 */         flag[0] = false;
/*  584:     */       }
/*  585: 631 */       return ch;
/*  586:     */     case 12: 
/*  587:     */     case 13: 
/*  588: 635 */       Long l = Long.valueOf(toLong(value));
/*  589: 636 */       if (l.longValue() == -9223372036854775808L) {
/*  590: 637 */         flag[0] = false;
/*  591:     */       }
/*  592: 640 */       return l;
/*  593:     */     case 14: 
/*  594:     */     case 15: 
/*  595: 644 */       Double d = Double.valueOf(toDouble(value));
/*  596: 645 */       if (d.doubleValue() == 4.9E-324D) {
/*  597: 646 */         flag[0] = false;
/*  598:     */       }
/*  599: 649 */       return d;
/*  600:     */     case 16: 
/*  601:     */     case 17: 
/*  602: 654 */       Float f = Float.valueOf(toFloat(value));
/*  603: 655 */       if (f.floatValue() == 1.4E-45F) {
/*  604: 656 */         flag[0] = false;
/*  605:     */       }
/*  606: 658 */       return f;
/*  607:     */     case 18: 
/*  608: 661 */       return toDate(value);
/*  609:     */     case 19: 
/*  610: 664 */       return toBigDecimal(value);
/*  611:     */     case 20: 
/*  612: 668 */       return toBigInteger(value);
/*  613:     */     case 21: 
/*  614: 671 */       return toCalendar(toDate(value));
/*  615:     */     case 22: 
/*  616:     */     case 23: 
/*  617: 676 */       return Boolean.valueOf(toBooleanOrDie(value));
/*  618:     */     case 24: 
/*  619: 679 */       return toMap(value);
/*  620:     */     case 25: 
/*  621:     */     case 26: 
/*  622:     */     case 27: 
/*  623:     */     case 28: 
/*  624:     */     case 29: 
/*  625:     */     case 30: 
/*  626:     */     case 31: 
/*  627:     */     case 32: 
/*  628:     */     case 33: 
/*  629: 691 */       return toPrimitiveArrayIfPossible(clz, value);
/*  630:     */     case 36: 
/*  631: 694 */       return toCollection(clz, value);
/*  632:     */     case 37: 
/*  633: 697 */       if ((value instanceof Map)) {
/*  634: 698 */         return MapObjectConversion.fromMap((Map)value, clz);
/*  635:     */       }
/*  636: 699 */       if ((value instanceof List)) {
/*  637: 700 */         return MapObjectConversion.fromList((List)value, clz);
/*  638:     */       }
/*  639: 701 */       if (clz.isInstance(value)) {
/*  640: 702 */         return value;
/*  641:     */       }
/*  642: 704 */       ClassMeta meta = ClassMeta.classMeta(clz);
/*  643: 705 */       List<ConstructorAccess> constructors = meta.oneArgumentConstructors();
/*  644: 707 */       if (constructors.size() == 0) {
/*  645: 708 */         return null;
/*  646:     */       }
/*  647: 709 */       if (constructors.size() == 1)
/*  648:     */       {
/*  649: 710 */         ConstructorAccess constructorAccess = (ConstructorAccess)constructors.get(0);
/*  650: 711 */         Class<?> arg1Type = constructorAccess.parameterTypes()[0];
/*  651: 712 */         if (arg1Type.isInstance(value)) {
/*  652: 713 */           return constructorAccess.create(new Object[] { value });
/*  653:     */         }
/*  654: 715 */         return constructorAccess.create(new Object[] { coerce(arg1Type, value) });
/*  655:     */       }
/*  656: 718 */       for (ConstructorAccess c : constructors)
/*  657:     */       {
/*  658: 719 */         Class<?> arg1Type = c.parameterTypes()[0];
/*  659: 720 */         if (arg1Type.isInstance(value)) {
/*  660: 721 */           return c.create(new Object[] { value });
/*  661:     */         }
/*  662:     */       }
/*  663: 726 */       for (ConstructorAccess c : constructors)
/*  664:     */       {
/*  665: 727 */         Class<?> arg1Type = c.parameterTypes()[0];
/*  666: 728 */         if (arg1Type.isAssignableFrom(value.getClass())) {
/*  667: 729 */           return c.create(new Object[] { value });
/*  668:     */         }
/*  669:     */       }
/*  670: 733 */       flag[0] = false;
/*  671: 734 */       break;
/*  672:     */     case 38: 
/*  673: 741 */       return toEnum(clz, value);
/*  674:     */     case 40: 
/*  675: 744 */       return toClass(value);
/*  676:     */     case 41: 
/*  677: 748 */       return toTimeZone(flag, value);
/*  678:     */     case 42: 
/*  679: 752 */       return toUUID(flag, value);
/*  680:     */     case 43: 
/*  681: 755 */       return toCurrency(value);
/*  682:     */     case 44: 
/*  683: 758 */       return value;
/*  684:     */     case 3: 
/*  685:     */     case 34: 
/*  686:     */     case 35: 
/*  687:     */     case 39: 
/*  688:     */     default: 
/*  689: 761 */       flag[0] = false;
/*  690:     */     }
/*  691: 764 */     return null;
/*  692:     */   }
/*  693:     */   
/*  694:     */   private static UUID toUUID(boolean[] flag, Object value)
/*  695:     */   {
/*  696: 768 */     return toUUID(value);
/*  697:     */   }
/*  698:     */   
/*  699:     */   public static TimeZone toTimeZone(boolean[] flag, Object value)
/*  700:     */   {
/*  701: 772 */     String id = value.toString();
/*  702: 773 */     return TimeZone.getTimeZone(id);
/*  703:     */   }
/*  704:     */   
/*  705:     */   public static TimeZone toTimeZone(Object value)
/*  706:     */   {
/*  707: 778 */     String id = value.toString();
/*  708: 779 */     return TimeZone.getTimeZone(id);
/*  709:     */   }
/*  710:     */   
/*  711:     */   public static <T> T coerceClassic(Class<T> clz, Object value)
/*  712:     */   {
/*  713: 785 */     if (value == null) {
/*  714: 786 */       return null;
/*  715:     */     }
/*  716: 789 */     if (clz == value.getClass()) {
/*  717: 790 */       return value;
/*  718:     */     }
/*  719: 793 */     if ((clz == Typ.string) || (clz == Typ.chars)) {
/*  720: 794 */       return value.toString();
/*  721:     */     }
/*  722: 795 */     if ((clz == Typ.integer) || (clz == Typ.intgr))
/*  723:     */     {
/*  724: 796 */       Integer i = Integer.valueOf(toInt(value));
/*  725: 797 */       return i;
/*  726:     */     }
/*  727: 798 */     if ((clz == Typ.longWrapper) || (clz == Typ.lng))
/*  728:     */     {
/*  729: 799 */       Long l = Long.valueOf(toLong(value));
/*  730: 800 */       return l;
/*  731:     */     }
/*  732: 801 */     if ((clz == Typ.doubleWrapper) || (clz == Typ.dbl))
/*  733:     */     {
/*  734: 802 */       Double i = Double.valueOf(toDouble(value));
/*  735: 803 */       return i;
/*  736:     */     }
/*  737: 804 */     if (clz == Typ.date) {
/*  738: 805 */       return toDate(value);
/*  739:     */     }
/*  740: 806 */     if (clz == Typ.bigInteger) {
/*  741: 807 */       return toBigInteger(value);
/*  742:     */     }
/*  743: 808 */     if (clz == Typ.bigDecimal) {
/*  744: 809 */       return toBigDecimal(value);
/*  745:     */     }
/*  746: 810 */     if (clz == Typ.calendar) {
/*  747: 811 */       return toCalendar(toDate(value));
/*  748:     */     }
/*  749: 812 */     if ((clz == Typ.floatWrapper) || (clz == Typ.flt))
/*  750:     */     {
/*  751: 813 */       Float i = Float.valueOf(toFloat(value));
/*  752: 814 */       return i;
/*  753:     */     }
/*  754: 815 */     if (clz == Typ.stringArray)
/*  755:     */     {
/*  756: 816 */       Exceptions.die("Need to fix this");
/*  757: 817 */       return null;
/*  758:     */     }
/*  759: 818 */     if ((clz == Typ.bool) || (clz == Typ.bln))
/*  760:     */     {
/*  761: 819 */       Boolean b = Boolean.valueOf(toBoolean(value));
/*  762: 820 */       return b;
/*  763:     */     }
/*  764: 821 */     if (Typ.isMap(clz)) {
/*  765: 822 */       return toMap(value);
/*  766:     */     }
/*  767: 823 */     if (clz.isArray()) {
/*  768: 824 */       return toPrimitiveArrayIfPossible(clz, value);
/*  769:     */     }
/*  770: 825 */     if (Typ.isCollection(clz)) {
/*  771: 826 */       return toCollection(clz, value);
/*  772:     */     }
/*  773: 827 */     if ((clz != null) && (clz.getPackage() != null) && (!clz.getPackage().getName().startsWith("java")) && (Typ.isMap(value.getClass())) && (Typ.doesMapHaveKeyTypeString(value))) {
/*  774: 829 */       return MapObjectConversion.fromMap((Map)value);
/*  775:     */     }
/*  776: 830 */     if (clz.isEnum()) {
/*  777: 831 */       return toEnum(clz, value);
/*  778:     */     }
/*  779: 834 */     return null;
/*  780:     */   }
/*  781:     */   
/*  782:     */   public static <T extends Enum> T toEnumOld(Class<T> cls, String value)
/*  783:     */   {
/*  784:     */     try
/*  785:     */     {
/*  786: 840 */       return Enum.valueOf(cls, value);
/*  787:     */     }
/*  788:     */     catch (Exception ex) {}
/*  789: 842 */     return Enum.valueOf(cls, value.toUpperCase().replace('-', '_'));
/*  790:     */   }
/*  791:     */   
/*  792:     */   public static <T extends Enum> T toEnum(Class<T> cls, String value)
/*  793:     */   {
/*  794: 848 */     return toEnum(cls, value, null);
/*  795:     */   }
/*  796:     */   
/*  797:     */   public static <T extends Enum> T toEnum(Class<T> cls, String value, Enum defaultEnum)
/*  798:     */   {
/*  799: 853 */     T[] enumConstants = (Enum[])cls.getEnumConstants();
/*  800: 854 */     for (T e : enumConstants) {
/*  801: 855 */       if (e.name().equals(value)) {
/*  802: 856 */         return e;
/*  803:     */       }
/*  804:     */     }
/*  805: 861 */     value = value.toUpperCase().replace('-', '_');
/*  806: 862 */     for (T e : enumConstants) {
/*  807: 863 */       if (e.name().equals(value)) {
/*  808: 864 */         return e;
/*  809:     */       }
/*  810:     */     }
/*  811: 868 */     value = Str.underBarCase(value);
/*  812: 869 */     for (T e : enumConstants) {
/*  813: 870 */       if (e.name().equals(value)) {
/*  814: 871 */         return e;
/*  815:     */       }
/*  816:     */     }
/*  817: 876 */     return defaultEnum;
/*  818:     */   }
/*  819:     */   
/*  820:     */   public static <T extends Enum> T toEnum(Class<T> cls, int value)
/*  821:     */   {
/*  822: 881 */     T[] enumConstants = (Enum[])cls.getEnumConstants();
/*  823: 882 */     for (T e : enumConstants) {
/*  824: 883 */       if (e.ordinal() == value) {
/*  825: 884 */         return e;
/*  826:     */       }
/*  827:     */     }
/*  828: 887 */     return null;
/*  829:     */   }
/*  830:     */   
/*  831:     */   public static <T extends Enum> T toEnumOrDie(Class<T> cls, int value)
/*  832:     */   {
/*  833: 892 */     T[] enumConstants = (Enum[])cls.getEnumConstants();
/*  834: 893 */     for (T e : enumConstants) {
/*  835: 894 */       if (e.ordinal() == value) {
/*  836: 895 */         return e;
/*  837:     */       }
/*  838:     */     }
/*  839: 898 */     Exceptions.die("Can't convert ordinal value " + value + " into enum of type " + cls);
/*  840: 899 */     return null;
/*  841:     */   }
/*  842:     */   
/*  843:     */   public static <T extends Enum> T toEnum(Class<T> cls, Object value)
/*  844:     */   {
/*  845: 905 */     if ((value instanceof Value)) {
/*  846: 906 */       return ((Value)value).toEnum(cls);
/*  847:     */     }
/*  848: 907 */     if ((value instanceof CharSequence)) {
/*  849: 908 */       return toEnum(cls, value.toString());
/*  850:     */     }
/*  851: 909 */     if (((value instanceof Number)) || (value.getClass().isPrimitive()))
/*  852:     */     {
/*  853: 911 */       int i = toInt(value);
/*  854: 912 */       return toEnum(cls, i);
/*  855:     */     }
/*  856: 915 */     if ((value instanceof Collection)) {
/*  857: 916 */       return toEnum(cls, ((Collection)value).iterator().next());
/*  858:     */     }
/*  859: 918 */     Exceptions.die("Can't convert  value " + value + " into enum of type " + cls);
/*  860: 919 */     return null;
/*  861:     */   }
/*  862:     */   
/*  863:     */   public static <T> T toPrimitiveArrayIfPossible(Class<T> clz, Object value)
/*  864:     */   {
/*  865: 925 */     if (clz == Typ.intArray) {
/*  866: 926 */       return iarray(value);
/*  867:     */     }
/*  868: 927 */     if (clz == Typ.byteArray) {
/*  869: 928 */       return barray(value);
/*  870:     */     }
/*  871: 929 */     if (clz == Typ.charArray) {
/*  872: 930 */       return carray(value);
/*  873:     */     }
/*  874: 931 */     if (clz == Typ.shortArray) {
/*  875: 932 */       return sarray(value);
/*  876:     */     }
/*  877: 933 */     if (clz == Typ.longArray) {
/*  878: 934 */       return larray(value);
/*  879:     */     }
/*  880: 935 */     if (clz == Typ.floatArray) {
/*  881: 936 */       return farray(value);
/*  882:     */     }
/*  883: 937 */     if (clz == Typ.doubleArray) {
/*  884: 938 */       return darray(value);
/*  885:     */     }
/*  886: 939 */     if (value.getClass() == clz) {
/*  887: 940 */       return value;
/*  888:     */     }
/*  889: 942 */     int index = 0;
/*  890: 943 */     Object newInstance = Array.newInstance(clz.getComponentType(), Boon.len(value));
/*  891: 944 */     Iterator<Object> iterator = iterator(Typ.object, value);
/*  892: 945 */     while (iterator.hasNext())
/*  893:     */     {
/*  894: 947 */       Object item = iterator.next();
/*  895: 949 */       if (clz.getComponentType().isAssignableFrom(item.getClass()))
/*  896:     */       {
/*  897: 951 */         BeanUtils.idx(newInstance, index, item);
/*  898:     */       }
/*  899:     */       else
/*  900:     */       {
/*  901: 955 */         item = coerce(clz.getComponentType(), item);
/*  902:     */         
/*  903: 957 */         BeanUtils.idx(newInstance, index, item);
/*  904:     */       }
/*  905: 959 */       index++;
/*  906:     */     }
/*  907: 961 */     return newInstance;
/*  908:     */   }
/*  909:     */   
/*  910:     */   public static double[] darray(Object value)
/*  911:     */   {
/*  912: 968 */     if (value.getClass() == Typ.doubleArray) {
/*  913: 969 */       return (double[])value;
/*  914:     */     }
/*  915: 971 */     double[] values = new double[Boon.len(value)];
/*  916: 972 */     int index = 0;
/*  917: 973 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  918: 974 */     while (iterator.hasNext())
/*  919:     */     {
/*  920: 975 */       values[index] = toFloat(iterator.next());
/*  921: 976 */       index++;
/*  922:     */     }
/*  923: 978 */     return values;
/*  924:     */   }
/*  925:     */   
/*  926:     */   public static float[] farray(Object value)
/*  927:     */   {
/*  928: 983 */     if (value.getClass() == Typ.floatArray) {
/*  929: 984 */       return (float[])value;
/*  930:     */     }
/*  931: 986 */     float[] values = new float[Boon.len(value)];
/*  932: 987 */     int index = 0;
/*  933: 988 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  934: 989 */     while (iterator.hasNext())
/*  935:     */     {
/*  936: 990 */       values[index] = toFloat(iterator.next());
/*  937: 991 */       index++;
/*  938:     */     }
/*  939: 993 */     return values;
/*  940:     */   }
/*  941:     */   
/*  942:     */   public static long[] larray(Object value)
/*  943:     */   {
/*  944: 998 */     if (value.getClass() == Typ.shortArray) {
/*  945: 999 */       return (long[])value;
/*  946:     */     }
/*  947:1001 */     long[] values = new long[Boon.len(value)];
/*  948:1002 */     int index = 0;
/*  949:1003 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  950:1004 */     while (iterator.hasNext())
/*  951:     */     {
/*  952:1005 */       values[index] = toLong(iterator.next());
/*  953:1006 */       index++;
/*  954:     */     }
/*  955:1008 */     return values;
/*  956:     */   }
/*  957:     */   
/*  958:     */   public static short[] sarray(Object value)
/*  959:     */   {
/*  960:1013 */     if (value.getClass() == Typ.shortArray) {
/*  961:1014 */       return (short[])value;
/*  962:     */     }
/*  963:1016 */     short[] values = new short[Boon.len(value)];
/*  964:1017 */     int index = 0;
/*  965:1018 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  966:1019 */     while (iterator.hasNext())
/*  967:     */     {
/*  968:1020 */       values[index] = toShort(iterator.next());
/*  969:1021 */       index++;
/*  970:     */     }
/*  971:1023 */     return values;
/*  972:     */   }
/*  973:     */   
/*  974:     */   public static int[] iarray(Object value)
/*  975:     */   {
/*  976:1028 */     if (value.getClass() == Typ.intArray) {
/*  977:1029 */       return (int[])value;
/*  978:     */     }
/*  979:1031 */     int[] values = new int[Boon.len(value)];
/*  980:1032 */     int index = 0;
/*  981:1033 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  982:1034 */     while (iterator.hasNext())
/*  983:     */     {
/*  984:1035 */       values[index] = toInt(iterator.next());
/*  985:1036 */       index++;
/*  986:     */     }
/*  987:1038 */     return values;
/*  988:     */   }
/*  989:     */   
/*  990:     */   public static byte[] barray(Object value)
/*  991:     */   {
/*  992:1043 */     if (value.getClass() == Typ.byteArray) {
/*  993:1044 */       return (byte[])value;
/*  994:     */     }
/*  995:1046 */     byte[] values = new byte[Boon.len(value)];
/*  996:1047 */     int index = 0;
/*  997:1048 */     Iterator<Object> iterator = iterator(Object.class, value);
/*  998:1049 */     while (iterator.hasNext())
/*  999:     */     {
/* 1000:1050 */       values[index] = toByte(iterator.next());
/* 1001:1051 */       index++;
/* 1002:     */     }
/* 1003:1053 */     return values;
/* 1004:     */   }
/* 1005:     */   
/* 1006:     */   public static char[] carray(Object value)
/* 1007:     */   {
/* 1008:1058 */     if (value.getClass() == Typ.charArray) {
/* 1009:1059 */       return (char[])value;
/* 1010:     */     }
/* 1011:1061 */     char[] values = new char[Boon.len(value)];
/* 1012:1062 */     int index = 0;
/* 1013:1063 */     Iterator<Object> iterator = iterator(Typ.object, value);
/* 1014:1064 */     while (iterator.hasNext())
/* 1015:     */     {
/* 1016:1065 */       values[index] = toChar(iterator.next());
/* 1017:1066 */       index++;
/* 1018:     */     }
/* 1019:1068 */     return values;
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   public static Iterator iterator(Object value)
/* 1023:     */   {
/* 1024:1073 */     return iterator(null, value);
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   public static <T> Iterator<T> iterator(Class<T> class1, final Object value)
/* 1028:     */   {
/* 1029:1078 */     if (value == null) {
/* 1030:1079 */       return Collections.EMPTY_LIST.iterator();
/* 1031:     */     }
/* 1032:1082 */     if (Boon.isArray(value))
/* 1033:     */     {
/* 1034:1083 */       int length = Arry.len(value);
/* 1035:     */       
/* 1036:1085 */       new Iterator()
/* 1037:     */       {
/* 1038:1086 */         int i = 0;
/* 1039:     */         
/* 1040:     */         public boolean hasNext()
/* 1041:     */         {
/* 1042:1090 */           return this.i < this.val$length;
/* 1043:     */         }
/* 1044:     */         
/* 1045:     */         public T next()
/* 1046:     */         {
/* 1047:1095 */           T next = BeanUtils.idx(value, this.i);
/* 1048:1096 */           this.i += 1;
/* 1049:1097 */           return next;
/* 1050:     */         }
/* 1051:     */         
/* 1052:     */         public void remove() {}
/* 1053:     */       };
/* 1054:     */     }
/* 1055:1104 */     if (Typ.isCollection(value.getClass())) {
/* 1056:1105 */       return ((Collection)value).iterator();
/* 1057:     */     }
/* 1058:1107 */     return Collections.singleton(value).iterator();
/* 1059:     */   }
/* 1060:     */   
/* 1061:     */   public static <T> T toCollection(Class<T> clz, Object value)
/* 1062:     */   {
/* 1063:1113 */     if (Typ.isList(clz)) {
/* 1064:1114 */       return toList(value);
/* 1065:     */     }
/* 1066:1115 */     if (Typ.isSortedSet(clz)) {
/* 1067:1116 */       return toSortedSet(value);
/* 1068:     */     }
/* 1069:1117 */     if (Typ.isSet(clz)) {
/* 1070:1118 */       return toSet(value);
/* 1071:     */     }
/* 1072:1120 */     return toList(value);
/* 1073:     */   }
/* 1074:     */   
/* 1075:     */   public static List toList(Object value)
/* 1076:     */   {
/* 1077:1126 */     if ((value instanceof List)) {
/* 1078:1127 */       return (List)value;
/* 1079:     */     }
/* 1080:1128 */     if ((value instanceof Collection)) {
/* 1081:1129 */       return new ArrayList((Collection)value);
/* 1082:     */     }
/* 1083:1130 */     if (value == null) {
/* 1084:1131 */       return new ArrayList();
/* 1085:     */     }
/* 1086:1132 */     if ((value instanceof Map)) {
/* 1087:1133 */       return new ArrayList(((Map)value).entrySet());
/* 1088:     */     }
/* 1089:1136 */     ArrayList list = new ArrayList(Boon.len(value));
/* 1090:1137 */     Iterator<Object> iterator = iterator(Typ.object, value);
/* 1091:1138 */     while (iterator.hasNext()) {
/* 1092:1139 */       list.add(iterator.next());
/* 1093:     */     }
/* 1094:1141 */     return list;
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   public static Collection toCollection(Object value)
/* 1098:     */   {
/* 1099:1147 */     if ((value instanceof Collection)) {
/* 1100:1148 */       return (Collection)value;
/* 1101:     */     }
/* 1102:1149 */     if (value == null) {
/* 1103:1150 */       return new ArrayList();
/* 1104:     */     }
/* 1105:1151 */     if ((value instanceof Map)) {
/* 1106:1152 */       return ((Map)value).entrySet();
/* 1107:     */     }
/* 1108:1155 */     ArrayList list = new ArrayList(Boon.len(value));
/* 1109:1156 */     Iterator<Object> iterator = iterator(Typ.object, value);
/* 1110:1157 */     while (iterator.hasNext()) {
/* 1111:1158 */       list.add(iterator.next());
/* 1112:     */     }
/* 1113:1160 */     return list;
/* 1114:     */   }
/* 1115:     */   
/* 1116:     */   public static Set toSet(Object value)
/* 1117:     */   {
/* 1118:1166 */     if ((value instanceof Set)) {
/* 1119:1167 */       return (Set)value;
/* 1120:     */     }
/* 1121:1168 */     if ((value instanceof Collection)) {
/* 1122:1169 */       return new HashSet((Collection)value);
/* 1123:     */     }
/* 1124:1171 */     HashSet set = new HashSet(Boon.len(value));
/* 1125:1172 */     Iterator<Object> iterator = iterator(Typ.object, value);
/* 1126:1173 */     while (iterator.hasNext()) {
/* 1127:1174 */       set.add(iterator.next());
/* 1128:     */     }
/* 1129:1176 */     return set;
/* 1130:     */   }
/* 1131:     */   
/* 1132:     */   public static SortedSet toSortedSet(Object value)
/* 1133:     */   {
/* 1134:1182 */     if ((value instanceof Set)) {
/* 1135:1183 */       return (SortedSet)value;
/* 1136:     */     }
/* 1137:1184 */     if ((value instanceof Collection)) {
/* 1138:1185 */       return new TreeSet((Collection)value);
/* 1139:     */     }
/* 1140:1187 */     TreeSet set = new TreeSet();
/* 1141:1188 */     Iterator<Object> iterator = iterator(Typ.object, value);
/* 1142:1189 */     while (iterator.hasNext()) {
/* 1143:1190 */       set.add(iterator.next());
/* 1144:     */     }
/* 1145:1192 */     return set;
/* 1146:     */   }
/* 1147:     */   
/* 1148:     */   public static Map<String, Object> toMap(Object value)
/* 1149:     */   {
/* 1150:1198 */     return MapObjectConversion.toMap(value);
/* 1151:     */   }
/* 1152:     */   
/* 1153:     */   public static Number toWrapper(long l)
/* 1154:     */   {
/* 1155:1204 */     if ((l >= -2147483648L) && (l <= 2147483647L)) {
/* 1156:1205 */       return toWrapper((int)l);
/* 1157:     */     }
/* 1158:1207 */     return Long.valueOf(l);
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   public static Number toWrapper(int i)
/* 1162:     */   {
/* 1163:1212 */     if ((i >= -128) && (i <= 127)) {
/* 1164:1213 */       return Byte.valueOf((byte)i);
/* 1165:     */     }
/* 1166:1214 */     if ((i >= -32768) && (i <= 32767)) {
/* 1167:1215 */       return Short.valueOf((short)i);
/* 1168:     */     }
/* 1169:1217 */     return Integer.valueOf(i);
/* 1170:     */   }
/* 1171:     */   
/* 1172:     */   public static Object wrapAsObject(boolean i)
/* 1173:     */   {
/* 1174:1222 */     return Boolean.valueOf(i);
/* 1175:     */   }
/* 1176:     */   
/* 1177:     */   public static Object wrapAsObject(byte i)
/* 1178:     */   {
/* 1179:1227 */     return Byte.valueOf(i);
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public static Object wrapAsObject(short i)
/* 1183:     */   {
/* 1184:1231 */     return Short.valueOf(i);
/* 1185:     */   }
/* 1186:     */   
/* 1187:     */   public static Object wrapAsObject(int i)
/* 1188:     */   {
/* 1189:1235 */     return Integer.valueOf(i);
/* 1190:     */   }
/* 1191:     */   
/* 1192:     */   public static Object wrapAsObject(long i)
/* 1193:     */   {
/* 1194:1239 */     return Long.valueOf(i);
/* 1195:     */   }
/* 1196:     */   
/* 1197:     */   public static Object wrapAsObject(double i)
/* 1198:     */   {
/* 1199:1243 */     return Double.valueOf(i);
/* 1200:     */   }
/* 1201:     */   
/* 1202:     */   public static Object wrapAsObject(float i)
/* 1203:     */   {
/* 1204:1247 */     return Float.valueOf(i);
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   public static Object toArrayGuessType(Collection<?> value)
/* 1208:     */   {
/* 1209:1251 */     Class<?> componentType = Reflection.getComponentType(value);
/* 1210:1252 */     Object array = Array.newInstance(componentType, value.size());
/* 1211:     */     
/* 1212:1254 */     Iterator<Object> iterator = value.iterator();
/* 1213:1255 */     int index = 0;
/* 1214:1256 */     while (iterator.hasNext())
/* 1215:     */     {
/* 1216:1257 */       BeanUtils.idx(array, index, iterator.next());
/* 1217:1258 */       index++;
/* 1218:     */     }
/* 1219:1260 */     return array;
/* 1220:     */   }
/* 1221:     */   
/* 1222:     */   public static <T> T[] toArray(Class<T> componentType, Collection<T> collection)
/* 1223:     */   {
/* 1224:1278 */     T[] array = (Object[])Array.newInstance(componentType, collection.size());
/* 1225:1280 */     if (componentType.isAssignableFrom(Typ.getComponentType(collection))) {
/* 1226:1281 */       return collection.toArray(array);
/* 1227:     */     }
/* 1228:1284 */     int index = 0;
/* 1229:1285 */     for (Object o : collection)
/* 1230:     */     {
/* 1231:1286 */       array[index] = coerce(componentType, o);
/* 1232:1287 */       index++;
/* 1233:     */     }
/* 1234:1289 */     return array;
/* 1235:     */   }
/* 1236:     */   
/* 1237:     */   public static <V> V[] array(Class<V> type, Collection<V> array)
/* 1238:     */   {
/* 1239:1298 */     return toArray(type, array);
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   public static Date toDate(Object object)
/* 1243:     */   {
/* 1244:1303 */     if ((object instanceof Date)) {
/* 1245:1304 */       return (Date)object;
/* 1246:     */     }
/* 1247:1305 */     if ((object instanceof Value)) {
/* 1248:1306 */       return ((Value)object).dateValue();
/* 1249:     */     }
/* 1250:1307 */     if ((object instanceof Calendar)) {
/* 1251:1308 */       return ((Calendar)object).getTime();
/* 1252:     */     }
/* 1253:1309 */     if ((object instanceof Long)) {
/* 1254:1310 */       return new Date(((Long)object).longValue());
/* 1255:     */     }
/* 1256:1311 */     if ((object instanceof String))
/* 1257:     */     {
/* 1258:1312 */       String val = (String)object;
/* 1259:1313 */       char[] chars = FastStringUtils.toCharArray(val);
/* 1260:1314 */       if (Dates.isISO8601QuickCheck(chars)) {
/* 1261:1315 */         return Dates.fromISO8601DateLoose(chars);
/* 1262:     */       }
/* 1263:1317 */       return toDateUS(val);
/* 1264:     */     }
/* 1265:1320 */     return null;
/* 1266:     */   }
/* 1267:     */   
/* 1268:     */   public static Calendar toCalendar(Date date)
/* 1269:     */   {
/* 1270:1325 */     Calendar calendar = Calendar.getInstance();
/* 1271:1326 */     calendar.setTime(date);
/* 1272:1327 */     return calendar;
/* 1273:     */   }
/* 1274:     */   
/* 1275:     */   public static Date toDate(Calendar c)
/* 1276:     */   {
/* 1277:1332 */     return c.getTime();
/* 1278:     */   }
/* 1279:     */   
/* 1280:     */   public static Date toDate(long value)
/* 1281:     */   {
/* 1282:1337 */     return new Date(value);
/* 1283:     */   }
/* 1284:     */   
/* 1285:     */   public static Date toDate(Long value)
/* 1286:     */   {
/* 1287:1341 */     return new Date(value.longValue());
/* 1288:     */   }
/* 1289:     */   
/* 1290:     */   public static Date toDate(String value)
/* 1291:     */   {
/* 1292:     */     try
/* 1293:     */     {
/* 1294:1346 */       return toDateUS(value);
/* 1295:     */     }
/* 1296:     */     catch (Exception ex)
/* 1297:     */     {
/* 1298:     */       try
/* 1299:     */       {
/* 1300:1349 */         return DateFormat.getDateInstance(3).parse(value);
/* 1301:     */       }
/* 1302:     */       catch (ParseException e)
/* 1303:     */       {
/* 1304:1351 */         Exceptions.die("Unable to parse date");
/* 1305:     */       }
/* 1306:     */     }
/* 1307:1352 */     return null;
/* 1308:     */   }
/* 1309:     */   
/* 1310:     */   public static Date toDateUS(String string)
/* 1311:     */   {
/* 1312:1361 */     String[] split = StringScanner.splitByChars(string, new char[] { '.', '\\', '/', ':' });
/* 1313:1363 */     if (split.length == 3) {
/* 1314:1364 */       return Dates.getUSDate(toInt(split[0]), toInt(split[1]), toInt(split[2]));
/* 1315:     */     }
/* 1316:1365 */     if (split.length >= 6) {
/* 1317:1366 */       return Dates.getUSDate(toInt(split[0]), toInt(split[1]), toInt(split[2]), toInt(split[3]), toInt(split[4]), toInt(split[5]));
/* 1318:     */     }
/* 1319:1370 */     Exceptions.die(String.format("Not able to parse %s into a US date", new Object[] { string }));
/* 1320:1371 */     return null;
/* 1321:     */   }
/* 1322:     */   
/* 1323:     */   public static Date toEuroDate(String string)
/* 1324:     */   {
/* 1325:1378 */     String[] split = StringScanner.splitByChars(string, new char[] { '.', '\\', '/', ':' });
/* 1326:1380 */     if (split.length == 3) {
/* 1327:1381 */       return Dates.getEuroDate(toInt(split[0]), toInt(split[1]), toInt(split[2]));
/* 1328:     */     }
/* 1329:1382 */     if (split.length >= 6) {
/* 1330:1383 */       return Dates.getEuroDate(toInt(split[0]), toInt(split[1]), toInt(split[2]), toInt(split[3]), toInt(split[4]), toInt(split[5]));
/* 1331:     */     }
/* 1332:1387 */     Exceptions.die(String.format("Not able to parse %s into a Euro date", new Object[] { string }));
/* 1333:1388 */     return null;
/* 1334:     */   }
/* 1335:     */   
/* 1336:     */   public static Collection<Object> createCollection(Class<?> type, int size)
/* 1337:     */   {
/* 1338:1395 */     if (type == List.class) {
/* 1339:1396 */       return new ArrayList(size);
/* 1340:     */     }
/* 1341:1397 */     if (type == SortedSet.class) {
/* 1342:1398 */       return new TreeSet();
/* 1343:     */     }
/* 1344:1399 */     if (type == Set.class) {
/* 1345:1400 */       return new LinkedHashSet(size);
/* 1346:     */     }
/* 1347:1401 */     if (Typ.isList(type)) {
/* 1348:1402 */       return new ArrayList();
/* 1349:     */     }
/* 1350:1403 */     if (Typ.isSortedSet(type)) {
/* 1351:1404 */       return new TreeSet();
/* 1352:     */     }
/* 1353:1405 */     if (Typ.isSet(type)) {
/* 1354:1406 */       return new LinkedHashSet(size);
/* 1355:     */     }
/* 1356:1408 */     return new ArrayList(size);
/* 1357:     */   }
/* 1358:     */   
/* 1359:     */   public static Map<?, ?> createMap(Class<?> type, int size)
/* 1360:     */   {
/* 1361:1416 */     if (type == HashMap.class) {
/* 1362:1417 */       return new HashMap(size);
/* 1363:     */     }
/* 1364:1418 */     if (type == TreeMap.class) {
/* 1365:1419 */       return new TreeMap();
/* 1366:     */     }
/* 1367:1420 */     if (type == SortedMap.class) {
/* 1368:1421 */       return new TreeMap();
/* 1369:     */     }
/* 1370:1422 */     if (type == ConcurrentHashMap.class) {
/* 1371:1423 */       return new ConcurrentHashMap();
/* 1372:     */     }
/* 1373:1425 */     return new HashMap(size);
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public static <TO, FROM> List<TO> mapFilterNulls(Function<FROM, TO> converter, Collection<FROM> fromCollection)
/* 1377:     */   {
/* 1378:1435 */     ArrayList<TO> toList = new ArrayList(fromCollection.size());
/* 1379:1437 */     for (FROM from : fromCollection)
/* 1380:     */     {
/* 1381:1438 */       TO converted = converter.apply(from);
/* 1382:1439 */       if (converted != null) {
/* 1383:1440 */         toList.add(converted);
/* 1384:     */       }
/* 1385:     */     }
/* 1386:1444 */     return toList;
/* 1387:     */   }
/* 1388:     */   
/* 1389:     */   public static Object unifyListOrArray(Object o)
/* 1390:     */   {
/* 1391:1449 */     return unifyListOrArray(o, null);
/* 1392:     */   }
/* 1393:     */   
/* 1394:     */   public static Object unifyListOrArray(Object o, List list)
/* 1395:     */   {
/* 1396:1460 */     if (o == null) {
/* 1397:1461 */       return null;
/* 1398:     */     }
/* 1399:1464 */     boolean isArray = o.getClass().isArray();
/* 1400:1466 */     if ((list == null) && (!isArray) && (!(o instanceof Iterable))) {
/* 1401:1467 */       return o;
/* 1402:     */     }
/* 1403:1470 */     if (list == null) {
/* 1404:1471 */       list = new LinkedList();
/* 1405:     */     }
/* 1406:1475 */     if (isArray)
/* 1407:     */     {
/* 1408:1476 */       int length = Array.getLength(o);
/* 1409:1478 */       for (int index = 0; index < length; index++)
/* 1410:     */       {
/* 1411:1480 */         Object o1 = Array.get(o, index);
/* 1412:1481 */         if (((o1 instanceof Iterable)) || (o.getClass().isArray())) {
/* 1413:1482 */           unifyListOrArray(o1, list);
/* 1414:     */         } else {
/* 1415:1484 */           list.add(o1);
/* 1416:     */         }
/* 1417:     */       }
/* 1418:     */     }
/* 1419:1487 */     else if ((o instanceof Collection))
/* 1420:     */     {
/* 1421:1489 */       Collection i = (Collection)o;
/* 1422:1493 */       for (Object item : i) {
/* 1423:1495 */         if (((item instanceof Iterable)) || (o.getClass().isArray())) {
/* 1424:1496 */           unifyListOrArray(item, list);
/* 1425:     */         } else {
/* 1426:1498 */           list.add(item);
/* 1427:     */         }
/* 1428:     */       }
/* 1429:     */     }
/* 1430:     */     else
/* 1431:     */     {
/* 1432:1506 */       list.add(o);
/* 1433:     */     }
/* 1434:1509 */     return list;
/* 1435:     */   }
/* 1436:     */   
/* 1437:     */   public static Object unifyList(List list)
/* 1438:     */   {
/* 1439:1517 */     return unifyListOrArray(list, null);
/* 1440:     */   }
/* 1441:     */   
/* 1442:     */   public static Object unifyList(Object o, List list)
/* 1443:     */   {
/* 1444:1528 */     if (o == null) {
/* 1445:1529 */       return null;
/* 1446:     */     }
/* 1447:1533 */     if (list == null) {
/* 1448:1534 */       list = new ArrayList();
/* 1449:     */     }
/* 1450:1537 */     if ((o instanceof Iterable))
/* 1451:     */     {
/* 1452:1538 */       Iterable i = (Iterable)o;
/* 1453:1541 */       for (Object item : i) {
/* 1454:1543 */         unifyListOrArray(item, list);
/* 1455:     */       }
/* 1456:     */     }
/* 1457:     */     else
/* 1458:     */     {
/* 1459:1550 */       list.add(o);
/* 1460:     */     }
/* 1461:1553 */     return list;
/* 1462:     */   }
/* 1463:     */   
/* 1464:     */   public static Comparable comparable(Object comparable)
/* 1465:     */   {
/* 1466:1565 */     return (Comparable)comparable;
/* 1467:     */   }
/* 1468:     */   
/* 1469:     */   public Number coerceNumber(Object inputArgument, Class<?> paraType)
/* 1470:     */   {
/* 1471:1570 */     Number number = (Number)inputArgument;
/* 1472:1571 */     if ((paraType == Integer.TYPE) || (paraType == Integer.class)) {
/* 1473:1572 */       return Integer.valueOf(number.intValue());
/* 1474:     */     }
/* 1475:1573 */     if ((paraType == Double.TYPE) || (paraType == Double.class)) {
/* 1476:1574 */       return Double.valueOf(number.doubleValue());
/* 1477:     */     }
/* 1478:1575 */     if ((paraType == Float.TYPE) || (paraType == Float.class)) {
/* 1479:1576 */       return Float.valueOf(number.floatValue());
/* 1480:     */     }
/* 1481:1577 */     if ((paraType == Short.TYPE) || (paraType == Short.class)) {
/* 1482:1578 */       return Short.valueOf(number.shortValue());
/* 1483:     */     }
/* 1484:1579 */     if ((paraType == Byte.TYPE) || (paraType == Byte.class)) {
/* 1485:1580 */       return Byte.valueOf(number.byteValue());
/* 1486:     */     }
/* 1487:1582 */     return null;
/* 1488:     */   }
/* 1489:     */   
/* 1490:     */   public static int lengthOf(Object obj)
/* 1491:     */   {
/* 1492:1587 */     return len(obj);
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   public static int len(Object obj)
/* 1496:     */   {
/* 1497:1591 */     if (Typ.isArray(obj)) {
/* 1498:1592 */       return Arry.len(obj);
/* 1499:     */     }
/* 1500:1593 */     if ((obj instanceof CharSequence)) {
/* 1501:1594 */       return ((CharSequence)obj).length();
/* 1502:     */     }
/* 1503:1595 */     if ((obj instanceof Collection)) {
/* 1504:1596 */       return ((Collection)obj).size();
/* 1505:     */     }
/* 1506:1597 */     if ((obj instanceof Map)) {
/* 1507:1598 */       return ((Map)obj).size();
/* 1508:     */     }
/* 1509:1599 */     if (obj == null) {
/* 1510:1600 */       return 0;
/* 1511:     */     }
/* 1512:1602 */     return 1;
/* 1513:     */   }
/* 1514:     */   
/* 1515:     */   public static Class<?> toClass(Object value)
/* 1516:     */   {
/* 1517:1611 */     if (((value instanceof Class)) || (value == null)) {
/* 1518:1612 */       return (Class)value;
/* 1519:     */     }
/* 1520:1614 */     return toClass(value.toString());
/* 1521:     */   }
/* 1522:     */   
/* 1523:     */   public static Class<?> toClass(String str)
/* 1524:     */   {
/* 1525:     */     try
/* 1526:     */     {
/* 1527:1620 */       return Class.forName(str);
/* 1528:     */     }
/* 1529:     */     catch (ClassNotFoundException ex)
/* 1530:     */     {
/* 1531:1622 */       return (Class)Exceptions.handle(Object.class, ex);
/* 1532:     */     }
/* 1533:     */   }
/* 1534:     */   
/* 1535:     */   public static String toString(Object obj, String defaultValue)
/* 1536:     */   {
/* 1537:1628 */     return obj == null ? defaultValue : obj.toString();
/* 1538:     */   }
/* 1539:     */   
/* 1540:     */   public static String toString(Object obj)
/* 1541:     */   {
/* 1542:1635 */     return obj == null ? "" : obj.toString();
/* 1543:     */   }
/* 1544:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Conversions
 * JD-Core Version:    0.7.0.1
 */