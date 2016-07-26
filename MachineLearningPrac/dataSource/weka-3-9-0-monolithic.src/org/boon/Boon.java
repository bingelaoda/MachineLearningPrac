/*    1:     */ package org.boon;
/*    2:     */ 
/*    3:     */ import java.nio.file.Path;
/*    4:     */ import java.util.Collection;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Map;
/*    9:     */ import java.util.Scanner;
/*   10:     */ import java.util.concurrent.atomic.AtomicBoolean;
/*   11:     */ import org.boon.config.ContextConfigReader;
/*   12:     */ import org.boon.core.Conversions;
/*   13:     */ import org.boon.core.Sys;
/*   14:     */ import org.boon.core.Typ;
/*   15:     */ import org.boon.core.reflection.BeanUtils;
/*   16:     */ import org.boon.core.reflection.FastStringUtils;
/*   17:     */ import org.boon.core.reflection.Invoker;
/*   18:     */ import org.boon.core.reflection.MapObjectConversion;
/*   19:     */ import org.boon.core.reflection.Reflection;
/*   20:     */ import org.boon.core.reflection.fields.FieldAccessMode;
/*   21:     */ import org.boon.di.Context;
/*   22:     */ import org.boon.json.JsonFactory;
/*   23:     */ import org.boon.logging.LogLevel;
/*   24:     */ import org.boon.logging.Logging;
/*   25:     */ import org.boon.logging.TerminalLogger;
/*   26:     */ import org.boon.primitive.CharBuf;
/*   27:     */ import org.boon.template.BoonTemplate;
/*   28:     */ import org.boon.template.Template;
/*   29:     */ 
/*   30:     */ public class Boon
/*   31:     */ {
/*   32:     */   public static final String BOON_SYSTEM_CONF_DIR = "BOON_SYSTEM_CONF_DIR";
/*   33:  72 */   private static AtomicBoolean debug = new AtomicBoolean(false);
/*   34:     */   private static final Logger logger;
/*   35:     */   
/*   36:     */   static
/*   37:     */   {
/*   38:  83 */     if (Sys.inContainer()) {
/*   39:  84 */       logger = null;
/*   40:     */     } else {
/*   41:  86 */       logger = configurableLogger(Boon.class);
/*   42:     */     }
/*   43:     */   }
/*   44:     */   
/*   45:     */   public static boolean equals(Object a, Object b)
/*   46:     */   {
/*   47:  99 */     return (a == b) || ((a != null) && (a.equals(b)));
/*   48:     */   }
/*   49:     */   
/*   50:     */   public static void println(String message)
/*   51:     */   {
/*   52: 108 */     Sys.println(message);
/*   53:     */   }
/*   54:     */   
/*   55:     */   public static void println()
/*   56:     */   {
/*   57: 115 */     Sys.println("");
/*   58:     */   }
/*   59:     */   
/*   60:     */   public static void println(Object message)
/*   61:     */   {
/*   62: 125 */     print(message);
/*   63: 126 */     println();
/*   64:     */   }
/*   65:     */   
/*   66:     */   public static void print(String message)
/*   67:     */   {
/*   68: 135 */     Sys.print(message);
/*   69:     */   }
/*   70:     */   
/*   71:     */   public static void print(Object message)
/*   72:     */   {
/*   73: 150 */     if (message == null) {
/*   74: 151 */       print("<NULL>");
/*   75: 152 */     } else if ((message instanceof char[])) {
/*   76: 153 */       print(FastStringUtils.noCopyStringFromChars((char[])message));
/*   77: 154 */     } else if (message.getClass().isArray()) {
/*   78: 155 */       print(Lists.toListOrSingletonList(message).toString());
/*   79:     */     } else {
/*   80: 157 */       print(message.toString());
/*   81:     */     }
/*   82:     */   }
/*   83:     */   
/*   84:     */   public static void puts(Object... messages)
/*   85:     */   {
/*   86: 168 */     for (Object message : messages)
/*   87:     */     {
/*   88: 169 */       print(message);
/*   89: 170 */       if (!(message instanceof Terminal.Escape)) {
/*   90: 170 */         print(Character.valueOf(' '));
/*   91:     */       }
/*   92:     */     }
/*   93: 172 */     println();
/*   94:     */   }
/*   95:     */   
/*   96:     */   public static void putc(Object context, Object... messages)
/*   97:     */   {
/*   98: 186 */     for (Object message : messages)
/*   99:     */     {
/*  100: 187 */       if ((message instanceof CharSequence))
/*  101:     */       {
/*  102: 188 */         String transformedMessage = jstl(message.toString(), context);
/*  103:     */         
/*  104: 190 */         print(message);
/*  105:     */       }
/*  106:     */       else
/*  107:     */       {
/*  108: 192 */         print(message);
/*  109:     */       }
/*  110: 194 */       print(Character.valueOf(' '));
/*  111:     */     }
/*  112: 196 */     println();
/*  113:     */   }
/*  114:     */   
/*  115:     */   public static void puth(Object context, Object... messages)
/*  116:     */   {
/*  117: 209 */     for (Object message : messages)
/*  118:     */     {
/*  119: 210 */       if ((message instanceof CharSequence))
/*  120:     */       {
/*  121: 211 */         String transformedMessage = handlebars(message.toString(), context);
/*  122:     */         
/*  123: 213 */         print(message);
/*  124:     */       }
/*  125:     */       else
/*  126:     */       {
/*  127: 215 */         print(message);
/*  128:     */       }
/*  129: 217 */       print(Character.valueOf(' '));
/*  130:     */     }
/*  131: 219 */     println();
/*  132:     */   }
/*  133:     */   
/*  134:     */   public static void putl(Object... messages)
/*  135:     */   {
/*  136: 233 */     for (Object message : messages)
/*  137:     */     {
/*  138:     */       Iterator iterator;
/*  139: 235 */       if (((message instanceof Collection)) || (Typ.isArray(message))) {
/*  140: 236 */         iterator = Conversions.iterator(message);
/*  141:     */       }
/*  142: 237 */       while (iterator.hasNext())
/*  143:     */       {
/*  144: 238 */         puts(new Object[] { iterator.next() }); continue;
/*  145:     */         
/*  146:     */ 
/*  147:     */ 
/*  148: 242 */         print(message);
/*  149: 243 */         println();
/*  150:     */       }
/*  151:     */     }
/*  152: 245 */     println();
/*  153:     */   }
/*  154:     */   
/*  155:     */   public static String sputl(Object... messages)
/*  156:     */   {
/*  157: 256 */     CharBuf buf = CharBuf.create(100);
/*  158: 257 */     return sputl(buf, messages).toString();
/*  159:     */   }
/*  160:     */   
/*  161:     */   public static String sputs(Object... messages)
/*  162:     */   {
/*  163: 267 */     CharBuf buf = CharBuf.create(80);
/*  164: 268 */     return sputs(buf, messages).toString();
/*  165:     */   }
/*  166:     */   
/*  167:     */   public static CharBuf sputl(CharBuf buf, Object... messages)
/*  168:     */   {
/*  169: 281 */     for (Object message : messages)
/*  170:     */     {
/*  171: 282 */       if (message == null) {
/*  172: 283 */         buf.add("<NULL>");
/*  173: 284 */       } else if (message.getClass().isArray()) {
/*  174: 285 */         buf.add(Lists.toListOrSingletonList(message).toString());
/*  175:     */       } else {
/*  176: 287 */         buf.add(message.toString());
/*  177:     */       }
/*  178: 289 */       buf.add('\n');
/*  179:     */     }
/*  180: 291 */     buf.add('\n');
/*  181:     */     
/*  182: 293 */     return buf;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public static CharBuf sputs(CharBuf buf, Object... messages)
/*  186:     */   {
/*  187: 307 */     int index = 0;
/*  188: 308 */     for (Object message : messages)
/*  189:     */     {
/*  190: 309 */       if (index != 0) {
/*  191: 310 */         buf.add(' ');
/*  192:     */       }
/*  193: 312 */       index++;
/*  194: 314 */       if (message == null) {
/*  195: 315 */         buf.add("<NULL>");
/*  196: 316 */       } else if (message.getClass().isArray()) {
/*  197: 317 */         buf.add(Lists.toListOrSingletonList(message).toString());
/*  198:     */       } else {
/*  199: 319 */         buf.add(message.toString());
/*  200:     */       }
/*  201:     */     }
/*  202: 322 */     buf.add('\n');
/*  203:     */     
/*  204: 324 */     return buf;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public static StringBuilder sputs(StringBuilder buf, Object... messages)
/*  208:     */   {
/*  209: 330 */     int index = 0;
/*  210: 331 */     for (Object message : messages)
/*  211:     */     {
/*  212: 332 */       if (index != 0) {
/*  213: 333 */         buf.append(' ');
/*  214:     */       }
/*  215: 335 */       index++;
/*  216: 337 */       if (message == null) {
/*  217: 338 */         buf.append("<NULL>");
/*  218: 339 */       } else if (message.getClass().isArray()) {
/*  219: 340 */         buf.append(Lists.toListOrSingletonList(message).toString());
/*  220:     */       } else {
/*  221: 342 */         buf.append(message.toString());
/*  222:     */       }
/*  223:     */     }
/*  224: 345 */     buf.append('\n');
/*  225:     */     
/*  226: 347 */     return buf;
/*  227:     */   }
/*  228:     */   
/*  229:     */   public static boolean isArray(Object obj)
/*  230:     */   {
/*  231: 352 */     return Typ.isArray(obj);
/*  232:     */   }
/*  233:     */   
/*  234:     */   public static boolean isStringArray(Object obj)
/*  235:     */   {
/*  236: 356 */     return Typ.isStringArray(obj);
/*  237:     */   }
/*  238:     */   
/*  239:     */   public static int len(Object obj)
/*  240:     */   {
/*  241: 360 */     return Conversions.len(obj);
/*  242:     */   }
/*  243:     */   
/*  244:     */   public static Iterator iterator(Object o)
/*  245:     */   {
/*  246: 365 */     return Conversions.iterator(o);
/*  247:     */   }
/*  248:     */   
/*  249:     */   public static String joinBy(char delim, Object... args)
/*  250:     */   {
/*  251: 372 */     CharBuf builder = CharBuf.create(256);
/*  252: 373 */     int index = 0;
/*  253: 374 */     for (Object arg : args)
/*  254:     */     {
/*  255: 375 */       builder.add(arg.toString());
/*  256: 376 */       if (index != args.length - 1) {
/*  257: 377 */         builder.add(delim);
/*  258:     */       }
/*  259: 379 */       index++;
/*  260:     */     }
/*  261: 381 */     return builder.toString();
/*  262:     */   }
/*  263:     */   
/*  264:     */   public static String joinBy(char delim, Collection<?> collection)
/*  265:     */   {
/*  266: 388 */     CharBuf builder = CharBuf.create(256);
/*  267: 389 */     int index = 0;
/*  268: 390 */     int size = collection.size();
/*  269: 391 */     for (Object arg : collection)
/*  270:     */     {
/*  271: 392 */       builder.add(arg.toString());
/*  272: 393 */       if (index != size - 1) {
/*  273: 394 */         builder.add(delim);
/*  274:     */       }
/*  275: 396 */       index++;
/*  276:     */     }
/*  277: 398 */     return builder.toString();
/*  278:     */   }
/*  279:     */   
/*  280:     */   public static String joinBy(char delim, Iterable<?> iterable)
/*  281:     */   {
/*  282: 406 */     CharBuf builder = CharBuf.create(256);
/*  283: 407 */     int index = 0;
/*  284: 408 */     for (Object arg : iterable)
/*  285:     */     {
/*  286: 409 */       builder.add(arg.toString());
/*  287: 410 */       builder.add(delim);
/*  288: 411 */       index++;
/*  289:     */     }
/*  290: 413 */     if (index > 1) {
/*  291: 414 */       builder.removeLastChar();
/*  292:     */     }
/*  293: 416 */     return builder.toString();
/*  294:     */   }
/*  295:     */   
/*  296:     */   public static List<?> mapBy(Iterable<?> objects, Object function)
/*  297:     */   {
/*  298: 428 */     return Lists.mapBy(objects, function);
/*  299:     */   }
/*  300:     */   
/*  301:     */   public static void each(Iterable<?> objects, Object function)
/*  302:     */   {
/*  303: 438 */     Functional.each(objects, function);
/*  304:     */   }
/*  305:     */   
/*  306:     */   public static String toJson(Object value)
/*  307:     */   {
/*  308: 449 */     return JsonFactory.toJson(value);
/*  309:     */   }
/*  310:     */   
/*  311:     */   public static Object fromJson(String value)
/*  312:     */   {
/*  313: 461 */     return JsonFactory.fromJson(value);
/*  314:     */   }
/*  315:     */   
/*  316:     */   public static <T> T fromJson(String value, Class<T> clazz)
/*  317:     */   {
/*  318: 473 */     return JsonFactory.fromJson(value, clazz);
/*  319:     */   }
/*  320:     */   
/*  321:     */   public static <T> List<T> fromJsonArray(String value, Class<T> clazz)
/*  322:     */   {
/*  323: 485 */     return JsonFactory.fromJsonArray(value, clazz);
/*  324:     */   }
/*  325:     */   
/*  326:     */   public static Object atIndex(Object value, String path)
/*  327:     */   {
/*  328: 498 */     return BeanUtils.idx(value, path);
/*  329:     */   }
/*  330:     */   
/*  331:     */   public static String gets()
/*  332:     */   {
/*  333: 508 */     Scanner console = new Scanner(System.in);
/*  334: 509 */     String input = console.nextLine();
/*  335: 510 */     return input.trim();
/*  336:     */   }
/*  337:     */   
/*  338:     */   public static String jstl(String template, Object context)
/*  339:     */   {
/*  340: 522 */     return BoonTemplate.jstl().replace(template, new Object[] { context }).toString();
/*  341:     */   }
/*  342:     */   
/*  343:     */   public static String handlebars(String template, Object context)
/*  344:     */   {
/*  345: 534 */     return BoonTemplate.template().replace(template, new Object[] { context });
/*  346:     */   }
/*  347:     */   
/*  348:     */   public static String add(String... args)
/*  349:     */   {
/*  350: 542 */     return Str.add(args);
/*  351:     */   }
/*  352:     */   
/*  353:     */   public static String stringAtIndex(Object value, String path)
/*  354:     */   {
/*  355: 553 */     return Conversions.toString(BeanUtils.idx(value, path));
/*  356:     */   }
/*  357:     */   
/*  358:     */   public static Object call(Object value, String method)
/*  359:     */   {
/*  360: 570 */     if ((value instanceof Class)) {
/*  361: 571 */       return Invoker.invoke((Class)value, method, new Object[0]);
/*  362:     */     }
/*  363: 573 */     return Invoker.invoke(value, method, new Object[0]);
/*  364:     */   }
/*  365:     */   
/*  366:     */   public static String sliceOf(String string, int start, int stop)
/*  367:     */   {
/*  368: 587 */     return Str.sliceOf(string, start, stop);
/*  369:     */   }
/*  370:     */   
/*  371:     */   public static String sliceOf(String string, int start)
/*  372:     */   {
/*  373: 592 */     return Str.sliceOf(string, start);
/*  374:     */   }
/*  375:     */   
/*  376:     */   public static String endSliceOf(String string, int end)
/*  377:     */   {
/*  378: 597 */     return Str.endSliceOf(string, end);
/*  379:     */   }
/*  380:     */   
/*  381:     */   public static Context readConfig(String namespace, String path)
/*  382:     */   {
/*  383: 635 */     String localConfigDir = add(new String[] { System.getProperty("user.home"), "/.", Str.camelCaseLower(Str.underBarCase(namespace)) });
/*  384:     */     
/*  385:     */ 
/*  386:     */ 
/*  387:     */ 
/*  388: 640 */     return readConfig(namespace, path, new String[] { "classpath:/", localConfigDir, sysProp("BOON_SYSTEM_CONF_DIR", "/etc/") });
/*  389:     */   }
/*  390:     */   
/*  391:     */   public static Context readConfig(String namespace, String path, String... roots)
/*  392:     */   {
/*  393: 659 */     trace(new Object[] { "readConfig(namespace, path, roots)", "IN", namespace, path, roots });
/*  394: 661 */     if (path.startsWith("/")) {
/*  395: 662 */       path = sliceOf(path, 1);
/*  396:     */     }
/*  397: 666 */     if ((!path.endsWith(".json")) && 
/*  398: 667 */       (!path.endsWith("/"))) {
/*  399: 668 */       path = add(new String[] { path, "/" });
/*  400:     */     }
/*  401: 671 */     ContextConfigReader contextConfigReader = ContextConfigReader.config().namespace(namespace);
/*  402: 673 */     for (String root : roots)
/*  403:     */     {
/*  404: 675 */       if (!root.endsWith("/")) {
/*  405: 676 */         root = add(new String[] { root, "/" });
/*  406:     */       }
/*  407: 680 */       debug(new Object[] { "readConfig", "adding root", root });
/*  408: 681 */       contextConfigReader.resource(add(new String[] { root, path }));
/*  409:     */     }
/*  410: 684 */     trace(new Object[] { "readConfig(namespace, path, roots)", "OUT", namespace, path, roots });
/*  411:     */     
/*  412: 686 */     return contextConfigReader.read();
/*  413:     */   }
/*  414:     */   
/*  415:     */   public static String sysProp(String propertyName, Object defaultValue)
/*  416:     */   {
/*  417: 699 */     return Sys.sysProp(propertyName, defaultValue);
/*  418:     */   }
/*  419:     */   
/*  420:     */   public static boolean hasSysProp(String propertyName)
/*  421:     */   {
/*  422: 704 */     return Sys.hasSysProp(propertyName);
/*  423:     */   }
/*  424:     */   
/*  425:     */   public static void putSysProp(String propertyName, Object value)
/*  426:     */   {
/*  427: 709 */     Sys.putSysProp(propertyName, value);
/*  428:     */   }
/*  429:     */   
/*  430:     */   public static Context readConfig()
/*  431:     */   {
/*  432: 754 */     return readConfig(sysProp("BOON.APP.NAMESPACE", "boon.app"), sysProp("BOON.APP.CONFIG.PATH", "boon/app"));
/*  433:     */   }
/*  434:     */   
/*  435:     */   public static void pressEnterKey(String pressEnterKeyMessage)
/*  436:     */   {
/*  437: 766 */     puts(new Object[] { pressEnterKeyMessage });
/*  438: 767 */     gets();
/*  439:     */   }
/*  440:     */   
/*  441:     */   public static void pressEnterKey()
/*  442:     */   {
/*  443: 775 */     puts(new Object[] { "Press enter key to continue" });
/*  444: 776 */     gets();
/*  445:     */   }
/*  446:     */   
/*  447:     */   public static boolean respondsTo(Object object, String method)
/*  448:     */   {
/*  449: 788 */     if ((object instanceof Class)) {
/*  450: 789 */       return Reflection.respondsTo((Class)object, method);
/*  451:     */     }
/*  452: 791 */     return Reflection.respondsTo(object, method);
/*  453:     */   }
/*  454:     */   
/*  455:     */   public static String resource(String path)
/*  456:     */   {
/*  457: 807 */     if (!IO.exists(IO.path(path))) {
/*  458: 808 */       path = add(new String[] { "classpath:/", path });
/*  459:     */     }
/*  460: 811 */     String str = IO.read(path);
/*  461: 812 */     return str;
/*  462:     */   }
/*  463:     */   
/*  464:     */   public static String resource(Path path)
/*  465:     */   {
/*  466: 827 */     String str = IO.read(path);
/*  467: 828 */     return str;
/*  468:     */   }
/*  469:     */   
/*  470:     */   public static String resourceFromHandleBarsTemplate(String path, Object context)
/*  471:     */   {
/*  472: 841 */     if (!IO.exists(IO.path(path))) {
/*  473: 842 */       path = add(new String[] { "classpath:/", path });
/*  474:     */     }
/*  475: 845 */     String str = IO.read(path);
/*  476: 847 */     if (str != null) {
/*  477: 848 */       str = handlebars(str, context);
/*  478:     */     }
/*  479: 851 */     return str;
/*  480:     */   }
/*  481:     */   
/*  482:     */   public static String resourceFromHandleBarsTemplate(Path path, Object context)
/*  483:     */   {
/*  484: 864 */     String str = IO.read(path);
/*  485: 866 */     if (str != null) {
/*  486: 867 */       str = handlebars(str, context);
/*  487:     */     }
/*  488: 870 */     return str;
/*  489:     */   }
/*  490:     */   
/*  491:     */   public static String resourceFromTemplate(String path, Object context)
/*  492:     */   {
/*  493: 884 */     if (!IO.exists(IO.path(path))) {
/*  494: 885 */       path = add(new String[] { "classpath:/", path });
/*  495:     */     }
/*  496: 888 */     String str = IO.read(path);
/*  497: 890 */     if (str != null) {
/*  498: 891 */       str = jstl(str, context);
/*  499:     */     }
/*  500: 894 */     return str;
/*  501:     */   }
/*  502:     */   
/*  503:     */   public static String resourceFromTemplate(Path path, Object context)
/*  504:     */   {
/*  505: 909 */     String str = IO.read(path);
/*  506: 911 */     if (str != null) {
/*  507: 912 */       str = jstl(str, context);
/*  508:     */     }
/*  509: 915 */     return str;
/*  510:     */   }
/*  511:     */   
/*  512:     */   public static Object jsonResource(String path)
/*  513:     */   {
/*  514: 927 */     if (!IO.exists(IO.path(path))) {
/*  515: 928 */       path = add(new String[] { "classpath:/", path });
/*  516:     */     }
/*  517: 931 */     String str = IO.read(path);
/*  518: 932 */     if (str != null) {
/*  519: 933 */       return fromJson(str);
/*  520:     */     }
/*  521: 935 */     return null;
/*  522:     */   }
/*  523:     */   
/*  524:     */   public static Object jsonResource(Path path)
/*  525:     */   {
/*  526: 948 */     String str = IO.read(path);
/*  527: 949 */     if (str != null) {
/*  528: 950 */       return fromJson(str);
/*  529:     */     }
/*  530: 952 */     return null;
/*  531:     */   }
/*  532:     */   
/*  533:     */   public static Object jsonResourceFromTemplate(String path, Object context)
/*  534:     */   {
/*  535: 967 */     if (!IO.exists(IO.path(path))) {
/*  536: 968 */       path = add(new String[] { "classpath:/", path });
/*  537:     */     }
/*  538: 971 */     String str = IO.read(path);
/*  539: 972 */     if (str != null)
/*  540:     */     {
/*  541: 973 */       str = jstl(str, context);
/*  542: 974 */       return fromJson(str);
/*  543:     */     }
/*  544: 976 */     return null;
/*  545:     */   }
/*  546:     */   
/*  547:     */   public static Object jsonResourceFromTemplate(Path path, Object context)
/*  548:     */   {
/*  549: 992 */     String str = IO.read(path);
/*  550: 993 */     if (str != null)
/*  551:     */     {
/*  552: 994 */       str = jstl(str, context);
/*  553: 995 */       return fromJson(str);
/*  554:     */     }
/*  555: 997 */     return null;
/*  556:     */   }
/*  557:     */   
/*  558:     */   public static Map<String, Object> resourceMap(String path)
/*  559:     */   {
/*  560:1012 */     return (Map)jsonResource(path);
/*  561:     */   }
/*  562:     */   
/*  563:     */   public static Map<String, Object> resourceMap(Path path)
/*  564:     */   {
/*  565:1027 */     return (Map)jsonResource(path);
/*  566:     */   }
/*  567:     */   
/*  568:     */   public static Map<String, Object> resourceMapFromTemplate(String path, Object context)
/*  569:     */   {
/*  570:1043 */     return (Map)jsonResourceFromTemplate(path, context);
/*  571:     */   }
/*  572:     */   
/*  573:     */   public static Map<String, Object> resourceMapFromTemplate(Path path, Object context)
/*  574:     */   {
/*  575:1058 */     return (Map)jsonResourceFromTemplate(path, context);
/*  576:     */   }
/*  577:     */   
/*  578:     */   public static <T> T resourceObject(String path, Class<T> type)
/*  579:     */   {
/*  580:1073 */     return Maps.fromMap(resourceMap(path), type);
/*  581:     */   }
/*  582:     */   
/*  583:     */   public static <T> T resourceObject(Path path, Class<T> type)
/*  584:     */   {
/*  585:1087 */     return Maps.fromMap(resourceMap(path), type);
/*  586:     */   }
/*  587:     */   
/*  588:     */   public static <T> T resourceObjectFromTemplate(String path, Class<T> type, Object context)
/*  589:     */   {
/*  590:1102 */     return Maps.fromMap(resourceMapFromTemplate(path, context), type);
/*  591:     */   }
/*  592:     */   
/*  593:     */   public static <T> T resourceObjectFromTemplate(Path path, Class<T> type, Object context)
/*  594:     */   {
/*  595:1117 */     return Maps.fromMap(resourceMapFromTemplate(path, context), type);
/*  596:     */   }
/*  597:     */   
/*  598:     */   public static List<?> resourceList(String path)
/*  599:     */   {
/*  600:1131 */     return (List)jsonResource(path);
/*  601:     */   }
/*  602:     */   
/*  603:     */   public static List<?> resourceList(Path path)
/*  604:     */   {
/*  605:1145 */     return (List)jsonResource(path);
/*  606:     */   }
/*  607:     */   
/*  608:     */   public static <T> List<T> resourceListFromTemplate(String path, Class<T> listOf, Object context)
/*  609:     */   {
/*  610:1159 */     List<?> list = (List)jsonResourceFromTemplate(path, context);
/*  611:     */     
/*  612:1161 */     return MapObjectConversion.convertListOfMapsToObjects(true, null, FieldAccessMode.FIELD_THEN_PROPERTY.create(true), listOf, list, Collections.EMPTY_SET);
/*  613:     */   }
/*  614:     */   
/*  615:     */   public static <T> List<T> resourceListFromTemplate(Path path, Class<T> listOf, Object context)
/*  616:     */   {
/*  617:1176 */     List<Map> list = (List)jsonResourceFromTemplate(path, context);
/*  618:     */     
/*  619:1178 */     return MapObjectConversion.convertListOfMapsToObjects(listOf, list);
/*  620:     */   }
/*  621:     */   
/*  622:     */   public static <T> List<T> resourceList(String path, Class<T> listOf)
/*  623:     */   {
/*  624:1192 */     List<Map> list = (List)jsonResource(path);
/*  625:     */     
/*  626:1194 */     return MapObjectConversion.convertListOfMapsToObjects(listOf, list);
/*  627:     */   }
/*  628:     */   
/*  629:     */   public static <T> List<T> resourceList(Path path, Class<T> listOf)
/*  630:     */   {
/*  631:1210 */     List<Map> list = (List)jsonResource(path);
/*  632:     */     
/*  633:1212 */     return MapObjectConversion.convertListOfMapsToObjects(listOf, list);
/*  634:     */   }
/*  635:     */   
/*  636:     */   public static List<?> resourceListFromTemplate(String path, Object context)
/*  637:     */   {
/*  638:1227 */     return (List)jsonResourceFromTemplate(path, context);
/*  639:     */   }
/*  640:     */   
/*  641:     */   public static List<?> resourceListFromTemplate(Path path, Object context)
/*  642:     */   {
/*  643:1241 */     return (List)jsonResourceFromTemplate(path, context);
/*  644:     */   }
/*  645:     */   
/*  646:     */   public static String className(Object object)
/*  647:     */   {
/*  648:1253 */     return object == null ? "CLASS<NULL>" : object.getClass().getName();
/*  649:     */   }
/*  650:     */   
/*  651:     */   public static Class<?> cls(Object object)
/*  652:     */   {
/*  653:1264 */     return object == null ? null : object.getClass();
/*  654:     */   }
/*  655:     */   
/*  656:     */   public static String simpleName(Object object)
/*  657:     */   {
/*  658:1274 */     return object == null ? "CLASS<NULL>" : object.getClass().getSimpleName();
/*  659:     */   }
/*  660:     */   
/*  661:     */   public static Logger logger(Class<?> clazz)
/*  662:     */   {
/*  663:1286 */     return new Logger(Logging.logger(clazz));
/*  664:     */   }
/*  665:     */   
/*  666:     */   public static Logger logger(String name)
/*  667:     */   {
/*  668:1298 */     return new Logger(Logging.logger(name));
/*  669:     */   }
/*  670:     */   
/*  671:     */   public static Logger configurableLogger(String name)
/*  672:     */   {
/*  673:1310 */     return new Logger(Logging.configurableLogger(name));
/*  674:     */   }
/*  675:     */   
/*  676:     */   public static Logger configurableLogger(Class<?> clazz)
/*  677:     */   {
/*  678:1322 */     return new Logger(Logging.configurableLogger(clazz.getName()));
/*  679:     */   }
/*  680:     */   
/*  681:     */   public static boolean debugOn()
/*  682:     */   {
/*  683:1332 */     return debug.get();
/*  684:     */   }
/*  685:     */   
/*  686:     */   public static void turnDebugOn()
/*  687:     */   {
/*  688:1340 */     debug.set(true);
/*  689:     */   }
/*  690:     */   
/*  691:     */   public static void turnDebugOff()
/*  692:     */   {
/*  693:1348 */     debug.set(false);
/*  694:     */   }
/*  695:     */   
/*  696:     */   private static Logger _log()
/*  697:     */   {
/*  698:1359 */     if (debugOn()) {
/*  699:1360 */       return new Logger(new TerminalLogger().level(LogLevel.DEBUG));
/*  700:     */     }
/*  701:1362 */     return logger == null ? configurableLogger("BOON.SYSTEM") : logger;
/*  702:     */   }
/*  703:     */   
/*  704:     */   public static boolean logInfoOn()
/*  705:     */   {
/*  706:1373 */     return _log().infoOn();
/*  707:     */   }
/*  708:     */   
/*  709:     */   public static boolean logTraceOn()
/*  710:     */   {
/*  711:1383 */     return _log().traceOn();
/*  712:     */   }
/*  713:     */   
/*  714:     */   public static boolean logDebugOn()
/*  715:     */   {
/*  716:1393 */     return _log().debugOn();
/*  717:     */   }
/*  718:     */   
/*  719:     */   public static void fatal(Object... messages)
/*  720:     */   {
/*  721:1403 */     _log().fatal(messages);
/*  722:     */   }
/*  723:     */   
/*  724:     */   public static void error(Object... messages)
/*  725:     */   {
/*  726:1413 */     _log().error(messages);
/*  727:     */   }
/*  728:     */   
/*  729:     */   public static void warn(Object... messages)
/*  730:     */   {
/*  731:1422 */     _log().warn(messages);
/*  732:     */   }
/*  733:     */   
/*  734:     */   public static void info(Object... messages)
/*  735:     */   {
/*  736:1432 */     _log().info(messages);
/*  737:     */   }
/*  738:     */   
/*  739:     */   public static void debug(Object... messages)
/*  740:     */   {
/*  741:1442 */     _log().debug(messages);
/*  742:     */   }
/*  743:     */   
/*  744:     */   public static void trace(Object... messages)
/*  745:     */   {
/*  746:1451 */     _log().trace(messages);
/*  747:     */   }
/*  748:     */   
/*  749:     */   public static void config(Object... messages)
/*  750:     */   {
/*  751:1461 */     _log().config(messages);
/*  752:     */   }
/*  753:     */   
/*  754:     */   public static void fatal(Throwable t, Object... messages)
/*  755:     */   {
/*  756:1472 */     _log().fatal(t, messages);
/*  757:     */   }
/*  758:     */   
/*  759:     */   public static void error(Throwable t, Object... messages)
/*  760:     */   {
/*  761:1483 */     _log().error(t, messages);
/*  762:     */   }
/*  763:     */   
/*  764:     */   public static void warn(Throwable t, Object... messages)
/*  765:     */   {
/*  766:1494 */     _log().warn(t, messages);
/*  767:     */   }
/*  768:     */   
/*  769:     */   public static void info(Throwable t, Object... messages)
/*  770:     */   {
/*  771:1505 */     _log().info(t, messages);
/*  772:     */   }
/*  773:     */   
/*  774:     */   public static void config(Throwable t, Object... messages)
/*  775:     */   {
/*  776:1516 */     _log().config(t, messages);
/*  777:     */   }
/*  778:     */   
/*  779:     */   public static void debug(Throwable t, Object... messages)
/*  780:     */   {
/*  781:1527 */     _log().debug(t, messages);
/*  782:     */   }
/*  783:     */   
/*  784:     */   public static void trace(Throwable t, Object... messages)
/*  785:     */   {
/*  786:1538 */     _log().trace(t, messages);
/*  787:     */   }
/*  788:     */   
/*  789:     */   public static boolean equalsOrDie(Object expected, Object got)
/*  790:     */   {
/*  791:1544 */     if ((expected == null) && (got == null)) {
/*  792:1545 */       return true;
/*  793:     */     }
/*  794:1548 */     if ((expected == null) && (got != null)) {
/*  795:1548 */       Exceptions.die();
/*  796:     */     }
/*  797:1549 */     if (!expected.equals(got)) {
/*  798:1549 */       Exceptions.die(new Object[] { "Expected was", expected, "but we got", got });
/*  799:     */     }
/*  800:1551 */     return true;
/*  801:     */   }
/*  802:     */   
/*  803:     */   public static boolean equalsOrDie(String message, Object expected, Object got)
/*  804:     */   {
/*  805:1557 */     if ((expected == null) && (got != null)) {
/*  806:1557 */       Exceptions.die(new Object[] { message, "Expected was", expected, "but we got", got });
/*  807:     */     }
/*  808:1558 */     if (!expected.equals(got)) {
/*  809:1558 */       Exceptions.die(new Object[] { message, "Expected was", expected, "but we got", got });
/*  810:     */     }
/*  811:1560 */     return true;
/*  812:     */   }
/*  813:     */   
/*  814:     */   public static String toPrettyJson(Object object)
/*  815:     */   {
/*  816:1565 */     CharBuf buf = CharBuf.createCharBuf();
/*  817:1566 */     return buf.prettyPrintObject(object, false, 0).toString();
/*  818:     */   }
/*  819:     */   
/*  820:     */   public static String toPrettyJsonWithTypes(Object object)
/*  821:     */   {
/*  822:1572 */     CharBuf buf = CharBuf.createCharBuf();
/*  823:1573 */     return buf.prettyPrintObject(object, true, 0).toString();
/*  824:     */   }
/*  825:     */   
/*  826:     */   public static boolean isEmpty(Object object)
/*  827:     */   {
/*  828:1577 */     return len(object) == 0;
/*  829:     */   }
/*  830:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Boon
 * JD-Core Version:    0.7.0.1
 */