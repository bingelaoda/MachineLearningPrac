/*    1:     */ package org.boon;
/*    2:     */ 
/*    3:     */ import java.math.BigDecimal;
/*    4:     */ import java.math.BigInteger;
/*    5:     */ import java.nio.charset.StandardCharsets;
/*    6:     */ import java.util.Collection;
/*    7:     */ import java.util.List;
/*    8:     */ import org.boon.core.reflection.FastStringUtils;
/*    9:     */ import org.boon.primitive.CharBuf;
/*   10:     */ import org.boon.primitive.CharScanner;
/*   11:     */ import org.boon.primitive.Chr;
/*   12:     */ 
/*   13:     */ public class Str
/*   14:     */ {
/*   15:     */   public static final String EMPTY_STRING = "";
/*   16:     */   
/*   17:     */   @Universal
/*   18:     */   public static int lengthOf(String str)
/*   19:     */   {
/*   20:  61 */     return len(str);
/*   21:     */   }
/*   22:     */   
/*   23:     */   @Universal
/*   24:     */   public static String sliceOf(String str, int start)
/*   25:     */   {
/*   26:  73 */     return slc(str, start);
/*   27:     */   }
/*   28:     */   
/*   29:     */   @Universal
/*   30:     */   public static String sliceOf(String str, int start, int end)
/*   31:     */   {
/*   32:  86 */     return slc(str, start, end);
/*   33:     */   }
/*   34:     */   
/*   35:     */   @Universal
/*   36:     */   public static String endSliceOf(String str, int end)
/*   37:     */   {
/*   38:  98 */     return slcEnd(str, end);
/*   39:     */   }
/*   40:     */   
/*   41:     */   @Universal
/*   42:     */   public static char atIndex(String str, int index)
/*   43:     */   {
/*   44: 110 */     return idx(str, index);
/*   45:     */   }
/*   46:     */   
/*   47:     */   @Universal
/*   48:     */   public static String atIndex(String str, int index, char c)
/*   49:     */   {
/*   50: 123 */     return idx(str, index, c);
/*   51:     */   }
/*   52:     */   
/*   53:     */   @Universal
/*   54:     */   public static int len(String str)
/*   55:     */   {
/*   56: 134 */     return str.length();
/*   57:     */   }
/*   58:     */   
/*   59:     */   @Universal
/*   60:     */   public static String slc(String str, int start)
/*   61:     */   {
/*   62: 148 */     return FastStringUtils.noCopyStringFromChars(Chr.slc(FastStringUtils.toCharArray(str), start));
/*   63:     */   }
/*   64:     */   
/*   65:     */   @Universal
/*   66:     */   public static String slc(String str, int start, int end)
/*   67:     */   {
/*   68: 161 */     return FastStringUtils.noCopyStringFromChars(Chr.slc(FastStringUtils.toCharArray(str), start, end));
/*   69:     */   }
/*   70:     */   
/*   71:     */   @Universal
/*   72:     */   public static String slcEnd(String str, int end)
/*   73:     */   {
/*   74: 173 */     return FastStringUtils.noCopyStringFromChars(Chr.slcEnd(FastStringUtils.toCharArray(str), end));
/*   75:     */   }
/*   76:     */   
/*   77:     */   @Universal
/*   78:     */   public static char idx(String str, int index)
/*   79:     */   {
/*   80: 186 */     int i = calculateIndex(str.length(), index);
/*   81:     */     
/*   82: 188 */     char c = str.charAt(i);
/*   83: 189 */     return c;
/*   84:     */   }
/*   85:     */   
/*   86:     */   @Universal
/*   87:     */   public static String idx(String str, int index, char c)
/*   88:     */   {
/*   89: 203 */     char[] chars = str.toCharArray();
/*   90: 204 */     Chr.idx(chars, index, c);
/*   91: 205 */     return new String(chars);
/*   92:     */   }
/*   93:     */   
/*   94:     */   @Universal
/*   95:     */   public static boolean in(char[] chars, String str)
/*   96:     */   {
/*   97: 216 */     return Chr.in(chars, FastStringUtils.toCharArray(str));
/*   98:     */   }
/*   99:     */   
/*  100:     */   @Universal
/*  101:     */   public static boolean in(char c, String str)
/*  102:     */   {
/*  103: 228 */     return Chr.in(c, FastStringUtils.toCharArray(str));
/*  104:     */   }
/*  105:     */   
/*  106:     */   @Universal
/*  107:     */   public static boolean in(char c, int offset, String str)
/*  108:     */   {
/*  109: 241 */     return Chr.in(c, offset, FastStringUtils.toCharArray(str));
/*  110:     */   }
/*  111:     */   
/*  112:     */   @Universal
/*  113:     */   public static boolean in(char c, int offset, int end, String str)
/*  114:     */   {
/*  115: 255 */     return Chr.in(c, offset, end, FastStringUtils.toCharArray(str));
/*  116:     */   }
/*  117:     */   
/*  118:     */   @Universal
/*  119:     */   public static String add(String str, char c)
/*  120:     */   {
/*  121: 267 */     return FastStringUtils.noCopyStringFromChars(Chr.add(FastStringUtils.toCharArray(str), c));
/*  122:     */   }
/*  123:     */   
/*  124:     */   @Universal
/*  125:     */   public static String add(String str, String str2)
/*  126:     */   {
/*  127: 279 */     return FastStringUtils.noCopyStringFromChars(Chr.add(FastStringUtils.toCharArray(str), FastStringUtils.toCharArray(str2)));
/*  128:     */   }
/*  129:     */   
/*  130:     */   @Universal
/*  131:     */   public static String add(String... strings)
/*  132:     */   {
/*  133: 294 */     int length = 0;
/*  134: 295 */     for (String str : strings) {
/*  135: 296 */       if (str != null) {
/*  136: 299 */         length += str.length();
/*  137:     */       }
/*  138:     */     }
/*  139: 301 */     CharBuf builder = CharBuf.createExact(length);
/*  140: 302 */     for (String str : strings) {
/*  141: 303 */       if (str != null) {
/*  142: 306 */         builder.add(str);
/*  143:     */       }
/*  144:     */     }
/*  145: 308 */     return builder.toString();
/*  146:     */   }
/*  147:     */   
/*  148:     */   public static String addObjects(Object... objects)
/*  149:     */   {
/*  150: 320 */     int length = 0;
/*  151: 321 */     for (Object obj : objects) {
/*  152: 322 */       if (obj != null) {
/*  153: 325 */         length += obj.toString().length();
/*  154:     */       }
/*  155:     */     }
/*  156: 327 */     CharBuf builder = CharBuf.createExact(length);
/*  157: 328 */     for (Object str : objects) {
/*  158: 329 */       if (str != null) {
/*  159: 332 */         builder.add(str.toString());
/*  160:     */       }
/*  161:     */     }
/*  162: 334 */     return builder.toString();
/*  163:     */   }
/*  164:     */   
/*  165:     */   public static String compact(String str)
/*  166:     */   {
/*  167: 343 */     return FastStringUtils.noCopyStringFromChars(Chr.compact(FastStringUtils.toCharArray(str)));
/*  168:     */   }
/*  169:     */   
/*  170:     */   private static int calculateIndex(int length, int originalIndex)
/*  171:     */   {
/*  172: 356 */     int index = originalIndex;
/*  173: 361 */     if (index < 0) {
/*  174: 362 */       index = length + index;
/*  175:     */     }
/*  176: 373 */     if (index < 0) {
/*  177: 374 */       index = 0;
/*  178:     */     }
/*  179: 376 */     if (index >= length) {
/*  180: 377 */       index = length - 1;
/*  181:     */     }
/*  182: 379 */     return index;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public static String[] split(String str)
/*  186:     */   {
/*  187: 389 */     char[][] split = Chr.split(FastStringUtils.toCharArray(str));
/*  188: 390 */     return fromCharArrayOfArrayToStringArray(split);
/*  189:     */   }
/*  190:     */   
/*  191:     */   public static String[] splitLines(String str)
/*  192:     */   {
/*  193: 400 */     char[][] split = Chr.splitLines(FastStringUtils.toCharArray(str));
/*  194: 401 */     return fromCharArrayOfArrayToStringArray(split);
/*  195:     */   }
/*  196:     */   
/*  197:     */   public static String[] splitComma(String str)
/*  198:     */   {
/*  199: 411 */     char[][] split = Chr.splitComma(FastStringUtils.toCharArray(str));
/*  200: 412 */     return fromCharArrayOfArrayToStringArray(split);
/*  201:     */   }
/*  202:     */   
/*  203:     */   public static String[] splitBySpace(String str)
/*  204:     */   {
/*  205: 422 */     char[][] split = CharScanner.splitBySpace(FastStringUtils.toCharArray(str));
/*  206: 423 */     return fromCharArrayOfArrayToStringArray(split);
/*  207:     */   }
/*  208:     */   
/*  209:     */   public static String[] splitByPipe(String str)
/*  210:     */   {
/*  211: 433 */     char[][] split = CharScanner.splitByPipe(FastStringUtils.toCharArray(str));
/*  212: 434 */     return fromCharArrayOfArrayToStringArray(split);
/*  213:     */   }
/*  214:     */   
/*  215:     */   public static String[] fromCharArrayOfArrayToStringArray(char[][] split)
/*  216:     */   {
/*  217: 444 */     String[] results = new String[split.length];
/*  218: 448 */     for (int index = 0; index < split.length; index++)
/*  219:     */     {
/*  220: 449 */       char[] array = split[index];
/*  221:     */       
/*  222: 451 */       results[index] = (array.length == 0 ? "" : FastStringUtils.noCopyStringFromChars(array));
/*  223:     */     }
/*  224: 454 */     return results;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public static String upper(String str)
/*  228:     */   {
/*  229: 464 */     return str.toUpperCase();
/*  230:     */   }
/*  231:     */   
/*  232:     */   public static String lower(String str)
/*  233:     */   {
/*  234: 474 */     return str.toLowerCase();
/*  235:     */   }
/*  236:     */   
/*  237:     */   public static String camelCaseUpper(String in)
/*  238:     */   {
/*  239: 484 */     return camelCase(in, true);
/*  240:     */   }
/*  241:     */   
/*  242:     */   public static String camelCaseLower(String in)
/*  243:     */   {
/*  244: 494 */     return camelCase(in, false);
/*  245:     */   }
/*  246:     */   
/*  247:     */   public static String camelCase(String in)
/*  248:     */   {
/*  249: 504 */     return camelCase(in, false);
/*  250:     */   }
/*  251:     */   
/*  252:     */   public static String camelCase(String inStr, boolean upper)
/*  253:     */   {
/*  254: 515 */     char[] in = FastStringUtils.toCharArray(inStr);
/*  255: 516 */     char[] out = Chr.camelCase(in, upper);
/*  256: 517 */     return FastStringUtils.noCopyStringFromChars(out);
/*  257:     */   }
/*  258:     */   
/*  259:     */   public static boolean insideOf(String start, String inStr, String end)
/*  260:     */   {
/*  261: 529 */     return Chr.insideOf(FastStringUtils.toCharArray(start), FastStringUtils.toCharArray(inStr), FastStringUtils.toCharArray(end));
/*  262:     */   }
/*  263:     */   
/*  264:     */   public static String underBarCase(String inStr)
/*  265:     */   {
/*  266: 538 */     char[] in = FastStringUtils.toCharArray(inStr);
/*  267: 539 */     char[] out = Chr.underBarCase(in);
/*  268: 540 */     return FastStringUtils.noCopyStringFromChars(out);
/*  269:     */   }
/*  270:     */   
/*  271:     */   public static void equalsOrDie(CharSequence a, CharSequence b)
/*  272:     */   {
/*  273: 550 */     char[] ac = FastStringUtils.toCharArray(a);
/*  274: 551 */     char[] bc = FastStringUtils.toCharArray(b);
/*  275: 552 */     Chr.equalsOrDie(ac, bc);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public static void equalsOrDie(String a, String b)
/*  279:     */   {
/*  280: 562 */     if ((a == null) && (b == null)) {
/*  281: 563 */       return;
/*  282:     */     }
/*  283: 565 */     if ((a == null) || (b == null)) {
/*  284: 566 */       Exceptions.die(new Object[] { "Values not equal value a=", a, "value b=", b });
/*  285:     */     }
/*  286: 569 */     char[] ac = FastStringUtils.toCharArray(a);
/*  287: 570 */     char[] bc = FastStringUtils.toCharArray(b);
/*  288: 571 */     Chr.equalsOrDie(ac, bc);
/*  289:     */   }
/*  290:     */   
/*  291:     */   public static String lpad(String inStr, int size, char fill)
/*  292:     */   {
/*  293: 583 */     return new String(Chr.lpad(inStr.toCharArray(), size, fill));
/*  294:     */   }
/*  295:     */   
/*  296:     */   public static String lpad(String inStr, int size)
/*  297:     */   {
/*  298: 594 */     return new String(Chr.lpad(inStr.toCharArray(), size, ' '));
/*  299:     */   }
/*  300:     */   
/*  301:     */   public static String lpad(Object inStr, int size)
/*  302:     */   {
/*  303: 605 */     return new String(Chr.lpad(inStr == null ? "".toCharArray() : inStr.toString().toCharArray(), size, ' '));
/*  304:     */   }
/*  305:     */   
/*  306:     */   public static String lpad(Object inStr)
/*  307:     */   {
/*  308: 615 */     return new String(Chr.lpad(inStr == null ? "".toCharArray() : inStr.toString().toCharArray(), 20, ' '));
/*  309:     */   }
/*  310:     */   
/*  311:     */   public static String zfill(int num, int size)
/*  312:     */   {
/*  313: 626 */     return new String(Chr.lpad(Integer.toString(num).toCharArray(), size, '0'));
/*  314:     */   }
/*  315:     */   
/*  316:     */   public static String rpad(String inStr, int size, char fill)
/*  317:     */   {
/*  318: 639 */     return new String(Chr.rpad(inStr.toCharArray(), size, fill));
/*  319:     */   }
/*  320:     */   
/*  321:     */   public static String rpad(String inStr, int size)
/*  322:     */   {
/*  323: 650 */     return new String(Chr.rpad(inStr.toCharArray(), size, ' '));
/*  324:     */   }
/*  325:     */   
/*  326:     */   public static String rpad(Object obj, int size)
/*  327:     */   {
/*  328: 661 */     if (obj != null) {
/*  329: 662 */       return new String(Chr.rpad(obj.toString().toCharArray(), size, ' '));
/*  330:     */     }
/*  331: 664 */     return new String(Chr.rpad("<NULL>".toCharArray(), size, ' '));
/*  332:     */   }
/*  333:     */   
/*  334:     */   public static String rpad(Object obj)
/*  335:     */   {
/*  336: 675 */     if (obj != null) {
/*  337: 676 */       return new String(Chr.rpad(obj.toString().toCharArray(), 20, ' '));
/*  338:     */     }
/*  339: 678 */     return new String(Chr.rpad("<NULL>".toCharArray(), 20, ' '));
/*  340:     */   }
/*  341:     */   
/*  342:     */   public static String rpad(Object obj, int size, char fill)
/*  343:     */   {
/*  344: 690 */     if (obj != null) {
/*  345: 691 */       return new String(Chr.rpad(obj.toString().toCharArray(), size, fill));
/*  346:     */     }
/*  347: 693 */     return new String(Chr.rpad("<NULL>".toCharArray(), size, fill));
/*  348:     */   }
/*  349:     */   
/*  350:     */   public static String[] split(String input, char split)
/*  351:     */   {
/*  352: 705 */     return StringScanner.split(input, split);
/*  353:     */   }
/*  354:     */   
/*  355:     */   @Universal
/*  356:     */   public static boolean in(String value, String str)
/*  357:     */   {
/*  358: 717 */     return str.contains(value);
/*  359:     */   }
/*  360:     */   
/*  361:     */   public static String lines(String... lines)
/*  362:     */   {
/*  363: 727 */     return join('\n', lines);
/*  364:     */   }
/*  365:     */   
/*  366:     */   public static String linesConvertQuotes(String... lines)
/*  367:     */   {
/*  368: 738 */     for (int index = 0; index < lines.length; index++) {
/*  369: 739 */       lines[index] = lines[index].replace('\'', '"');
/*  370:     */     }
/*  371: 741 */     return join('\n', lines);
/*  372:     */   }
/*  373:     */   
/*  374:     */   public static String join(char delim, String... args)
/*  375:     */   {
/*  376: 752 */     CharBuf builder = CharBuf.create(10 * args.length);
/*  377:     */     
/*  378: 754 */     int index = 0;
/*  379: 755 */     for (String arg : args)
/*  380:     */     {
/*  381: 756 */       builder.add(arg);
/*  382: 757 */       if (index != args.length - 1) {
/*  383: 758 */         builder.add(delim);
/*  384:     */       }
/*  385: 760 */       index++;
/*  386:     */     }
/*  387: 762 */     return builder.toString();
/*  388:     */   }
/*  389:     */   
/*  390:     */   public static String joinObjects(char delim, Object... args)
/*  391:     */   {
/*  392: 772 */     CharBuf builder = CharBuf.create(10 * args.length);
/*  393:     */     
/*  394: 774 */     int index = 0;
/*  395: 775 */     for (Object arg : args)
/*  396:     */     {
/*  397: 776 */       builder.add(arg == null ? "null" : arg.toString());
/*  398: 777 */       if (index != args.length - 1) {
/*  399: 778 */         builder.add(delim);
/*  400:     */       }
/*  401: 780 */       index++;
/*  402:     */     }
/*  403: 782 */     return builder.toString();
/*  404:     */   }
/*  405:     */   
/*  406:     */   public static String join(String... args)
/*  407:     */   {
/*  408: 793 */     CharBuf builder = CharBuf.create(10 * args.length);
/*  409: 795 */     for (String arg : args) {
/*  410: 796 */       builder.add(arg);
/*  411:     */     }
/*  412: 798 */     return builder.toString();
/*  413:     */   }
/*  414:     */   
/*  415:     */   public static String joinCollection(char delim, List<?> args)
/*  416:     */   {
/*  417: 808 */     CharBuf builder = CharBuf.create(10 * args.size());
/*  418:     */     
/*  419: 810 */     int index = 0;
/*  420: 811 */     for (Object arg : args) {
/*  421: 812 */       if (arg != null)
/*  422:     */       {
/*  423: 815 */         builder.add(arg.toString());
/*  424: 816 */         if (index != args.size() - 1) {
/*  425: 817 */           builder.add(delim);
/*  426:     */         }
/*  427: 819 */         index++;
/*  428:     */       }
/*  429:     */     }
/*  430: 821 */     return builder.toString();
/*  431:     */   }
/*  432:     */   
/*  433:     */   public static String joinCollection(String delim, List<?> args)
/*  434:     */   {
/*  435: 833 */     CharBuf builder = CharBuf.create(10 * args.size());
/*  436:     */     
/*  437: 835 */     int index = 0;
/*  438: 836 */     for (Object arg : args) {
/*  439: 837 */       if (arg != null)
/*  440:     */       {
/*  441: 840 */         builder.add(arg.toString());
/*  442: 841 */         if (index != args.size() - 1) {
/*  443: 842 */           builder.add(delim);
/*  444:     */         }
/*  445: 844 */         index++;
/*  446:     */       }
/*  447:     */     }
/*  448: 846 */     return builder.toString();
/*  449:     */   }
/*  450:     */   
/*  451:     */   @Universal
/*  452:     */   public static boolean isEmpty(String str)
/*  453:     */   {
/*  454: 857 */     if (str == null) {
/*  455: 858 */       return true;
/*  456:     */     }
/*  457: 860 */     return str.isEmpty();
/*  458:     */   }
/*  459:     */   
/*  460:     */   @Universal
/*  461:     */   public static boolean isEmpty(Object str)
/*  462:     */   {
/*  463: 867 */     if (str == null) {
/*  464: 868 */       return true;
/*  465:     */     }
/*  466: 870 */     return str.toString().isEmpty();
/*  467:     */   }
/*  468:     */   
/*  469:     */   public static String uncapitalize(String string)
/*  470:     */   {
/*  471: 882 */     StringBuilder rv = new StringBuilder();
/*  472: 883 */     if (string.length() > 0)
/*  473:     */     {
/*  474: 884 */       rv.append(Character.toLowerCase(string.charAt(0)));
/*  475: 885 */       if (string.length() > 1) {
/*  476: 886 */         rv.append(string.substring(1));
/*  477:     */       }
/*  478:     */     }
/*  479: 889 */     return rv.toString();
/*  480:     */   }
/*  481:     */   
/*  482:     */   public static String toString(Object object, String defaultString)
/*  483:     */   {
/*  484: 899 */     if (object == null) {
/*  485: 900 */       return defaultString;
/*  486:     */     }
/*  487: 902 */     return object.toString();
/*  488:     */   }
/*  489:     */   
/*  490:     */   public static String toString(Object object)
/*  491:     */   {
/*  492: 913 */     if (object == null) {
/*  493: 914 */       return "";
/*  494:     */     }
/*  495: 916 */     return object.toString();
/*  496:     */   }
/*  497:     */   
/*  498:     */   public static String str(Object str)
/*  499:     */   {
/*  500: 927 */     return str == null ? "<NULL>" : str.toString();
/*  501:     */   }
/*  502:     */   
/*  503:     */   public static boolean startsWithItemInCollection(String name, Collection<String> startsWithList)
/*  504:     */   {
/*  505: 937 */     for (String startsWith : startsWithList) {
/*  506: 938 */       if (name.startsWith(startsWith)) {
/*  507: 939 */         return true;
/*  508:     */       }
/*  509:     */     }
/*  510: 942 */     return false;
/*  511:     */   }
/*  512:     */   
/*  513:     */   public static String readable(String s)
/*  514:     */   {
/*  515: 946 */     return s.replace("\\n", "\n");
/*  516:     */   }
/*  517:     */   
/*  518:     */   public static String quote(String s)
/*  519:     */   {
/*  520: 956 */     return add(new String[] { "\"", s, "\"" });
/*  521:     */   }
/*  522:     */   
/*  523:     */   public static String singleQuote(String s)
/*  524:     */   {
/*  525: 966 */     return add(new String[] { "'", s, "'" });
/*  526:     */   }
/*  527:     */   
/*  528:     */   public static String doubleQuote(String s)
/*  529:     */   {
/*  530: 976 */     return add(new String[] { "\"", s, "\"" });
/*  531:     */   }
/*  532:     */   
/*  533:     */   public static String str(byte[] bytes)
/*  534:     */   {
/*  535: 984 */     return new String(bytes, StandardCharsets.UTF_8);
/*  536:     */   }
/*  537:     */   
/*  538:     */   public static String num(Number count)
/*  539:     */   {
/*  540: 996 */     if (count == null) {
/*  541: 997 */       return "";
/*  542:     */     }
/*  543: 999 */     if (((count instanceof Double)) || ((count instanceof BigDecimal)))
/*  544:     */     {
/*  545:1000 */       String s = count.toString();
/*  546:1001 */       if ((idx(s, 1) == '.') && (s.length() > 7))
/*  547:     */       {
/*  548:1002 */         s = slc(s, 0, 5);
/*  549:1003 */         return s;
/*  550:     */       }
/*  551:1005 */       return s;
/*  552:     */     }
/*  553:1008 */     if (((count instanceof Integer)) || ((count instanceof Long)) || ((count instanceof Short)) || ((count instanceof BigInteger)))
/*  554:     */     {
/*  555:1009 */       String s = count.toString();
/*  556:1010 */       s = new StringBuilder(s).reverse().toString();
/*  557:     */       
/*  558:1012 */       CharBuf buf = CharBuf.create(s.length());
/*  559:     */       
/*  560:1014 */       int index = 0;
/*  561:1015 */       for (char c : s.toCharArray())
/*  562:     */       {
/*  563:1018 */         index++;
/*  564:     */         
/*  565:1020 */         buf.add(c);
/*  566:1023 */         if (index % 3 == 0) {
/*  567:1024 */           buf.add(',');
/*  568:     */         }
/*  569:     */       }
/*  570:1030 */       if (buf.lastChar() == ',') {
/*  571:1031 */         buf.removeLastChar();
/*  572:     */       }
/*  573:1034 */       s = buf.toString();
/*  574:     */       
/*  575:1036 */       s = new StringBuilder(s).reverse().toString();
/*  576:     */       
/*  577:1038 */       return s;
/*  578:     */     }
/*  579:1042 */     return count.toString();
/*  580:     */   }
/*  581:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Str
 * JD-Core Version:    0.7.0.1
 */