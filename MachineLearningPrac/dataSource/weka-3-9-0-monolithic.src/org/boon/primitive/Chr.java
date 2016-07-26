/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.util.Arrays;
/*    4:     */ import org.boon.Boon;
/*    5:     */ import org.boon.Exceptions;
/*    6:     */ import org.boon.Universal;
/*    7:     */ import org.boon.core.reflection.FastStringUtils;
/*    8:     */ 
/*    9:     */ public class Chr
/*   10:     */ {
/*   11:  44 */   public static final char[] DEFAULT_SPLIT = { ' ', '\t', ',', ':', ';' };
/*   12:  45 */   public static final char[] NEWLINE_CHARS = { '\n', '\r' };
/*   13:  46 */   private static final char[] EMPTY_CHARS = new char[0];
/*   14:     */   
/*   15:     */   public static char[] arrayOfChar(int size)
/*   16:     */   {
/*   17:  55 */     return new char[size];
/*   18:     */   }
/*   19:     */   
/*   20:     */   @Universal
/*   21:     */   public static char[] array(char... array)
/*   22:     */   {
/*   23:  65 */     return array;
/*   24:     */   }
/*   25:     */   
/*   26:     */   @Universal
/*   27:     */   public static char[] chars(String array)
/*   28:     */   {
/*   29:  70 */     return FastStringUtils.toCharArray(array);
/*   30:     */   }
/*   31:     */   
/*   32:     */   @Universal
/*   33:     */   public static int lengthOf(char[] array)
/*   34:     */   {
/*   35:  76 */     return len(array);
/*   36:     */   }
/*   37:     */   
/*   38:     */   @Universal
/*   39:     */   public static int len(char[] array)
/*   40:     */   {
/*   41:  81 */     return array.length;
/*   42:     */   }
/*   43:     */   
/*   44:     */   @Universal
/*   45:     */   public static char atIndex(char[] array, int index)
/*   46:     */   {
/*   47:  87 */     return idx(array, index);
/*   48:     */   }
/*   49:     */   
/*   50:     */   @Universal
/*   51:     */   public static char idx(char[] array, int index)
/*   52:     */   {
/*   53:  92 */     int i = calculateIndex(array, index);
/*   54:     */     
/*   55:  94 */     return array[i];
/*   56:     */   }
/*   57:     */   
/*   58:     */   @Universal
/*   59:     */   public static void atIndex(char[] array, int index, char value)
/*   60:     */   {
/*   61: 100 */     idx(array, index, value);
/*   62:     */   }
/*   63:     */   
/*   64:     */   @Universal
/*   65:     */   public static void idx(char[] array, int index, char value)
/*   66:     */   {
/*   67: 105 */     int i = calculateIndex(array, index);
/*   68:     */     
/*   69: 107 */     array[i] = value;
/*   70:     */   }
/*   71:     */   
/*   72:     */   @Universal
/*   73:     */   public static void atIndex(char[] array, int index, char[] input)
/*   74:     */   {
/*   75: 114 */     idx(array, index, input);
/*   76:     */   }
/*   77:     */   
/*   78:     */   @Universal
/*   79:     */   public static void idx(char[] array, int index, char[] input)
/*   80:     */   {
/*   81: 119 */     int i = calculateIndex(array, index);
/*   82:     */     
/*   83: 121 */     _idx(array, i, input);
/*   84:     */   }
/*   85:     */   
/*   86:     */   @Universal
/*   87:     */   public static char[] sliceOf(char[] array, int startIndex, int endIndex)
/*   88:     */   {
/*   89: 126 */     return slc(array, startIndex, endIndex);
/*   90:     */   }
/*   91:     */   
/*   92:     */   @Universal
/*   93:     */   public static char[] slc(char[] array, int startIndex, int endIndex)
/*   94:     */   {
/*   95: 132 */     int start = calculateIndex(array, startIndex);
/*   96: 133 */     int end = calculateEndIndex(array, endIndex);
/*   97: 134 */     int newLength = end - start;
/*   98: 136 */     if (newLength < 0) {
/*   99: 137 */       return EMPTY_CHARS;
/*  100:     */     }
/*  101: 140 */     char[] newArray = new char[newLength];
/*  102: 141 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  103: 142 */     return newArray;
/*  104:     */   }
/*  105:     */   
/*  106:     */   @Universal
/*  107:     */   public static char[] sliceOf(char[] array, int startIndex)
/*  108:     */   {
/*  109: 148 */     return slc(array, startIndex);
/*  110:     */   }
/*  111:     */   
/*  112:     */   @Universal
/*  113:     */   public static char[] slc(char[] array, int startIndex)
/*  114:     */   {
/*  115: 154 */     int start = calculateIndex(array, startIndex);
/*  116: 155 */     int newLength = array.length - start;
/*  117: 157 */     if (newLength < 0) {
/*  118: 158 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  119:     */     }
/*  120: 164 */     char[] newArray = new char[newLength];
/*  121: 165 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  122: 166 */     return newArray;
/*  123:     */   }
/*  124:     */   
/*  125:     */   @Universal
/*  126:     */   public static char[] endSliceOf(char[] array, int endIndex)
/*  127:     */   {
/*  128: 172 */     return slcEnd(array, endIndex);
/*  129:     */   }
/*  130:     */   
/*  131:     */   @Universal
/*  132:     */   public static char[] slcEnd(char[] array, int endIndex)
/*  133:     */   {
/*  134: 178 */     int end = calculateEndIndex(array, endIndex);
/*  135: 179 */     int newLength = end;
/*  136: 181 */     if (newLength < 0) {
/*  137: 182 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  138:     */     }
/*  139: 188 */     char[] newArray = new char[newLength];
/*  140: 189 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  141: 190 */     return newArray;
/*  142:     */   }
/*  143:     */   
/*  144:     */   @Universal
/*  145:     */   public static boolean in(char value, char[] array)
/*  146:     */   {
/*  147: 195 */     for (char currentValue : array) {
/*  148: 196 */       if (currentValue == value) {
/*  149: 197 */         return true;
/*  150:     */       }
/*  151:     */     }
/*  152: 200 */     return false;
/*  153:     */   }
/*  154:     */   
/*  155:     */   @Universal
/*  156:     */   public static boolean in(char[] values, char[] array)
/*  157:     */   {
/*  158: 206 */     for (char currentValue : array) {
/*  159: 208 */       for (char value : values) {
/*  160: 209 */         if (currentValue == value) {
/*  161: 210 */           return true;
/*  162:     */         }
/*  163:     */       }
/*  164:     */     }
/*  165: 214 */     return false;
/*  166:     */   }
/*  167:     */   
/*  168:     */   @Universal
/*  169:     */   public static boolean in(int value, char[] array)
/*  170:     */   {
/*  171: 221 */     for (int currentValue : array) {
/*  172: 222 */       if (currentValue == value) {
/*  173: 223 */         return true;
/*  174:     */       }
/*  175:     */     }
/*  176: 226 */     return false;
/*  177:     */   }
/*  178:     */   
/*  179:     */   @Universal
/*  180:     */   public static boolean in(char value, int offset, char[] array)
/*  181:     */   {
/*  182: 230 */     for (int index = offset; index < array.length; index++)
/*  183:     */     {
/*  184: 231 */       char currentValue = array[index];
/*  185: 232 */       if (currentValue == value) {
/*  186: 233 */         return true;
/*  187:     */       }
/*  188:     */     }
/*  189: 236 */     return false;
/*  190:     */   }
/*  191:     */   
/*  192:     */   @Universal
/*  193:     */   public static boolean in(char value, int offset, int end, char[] array)
/*  194:     */   {
/*  195: 242 */     for (int index = offset; index < end; index++)
/*  196:     */     {
/*  197: 243 */       char currentValue = array[index];
/*  198: 244 */       if (currentValue == value) {
/*  199: 245 */         return true;
/*  200:     */       }
/*  201:     */     }
/*  202: 248 */     return false;
/*  203:     */   }
/*  204:     */   
/*  205:     */   public static char[] grow(char[] array, int size)
/*  206:     */   {
/*  207: 253 */     char[] newArray = new char[array.length + size];
/*  208: 254 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  209: 255 */     return newArray;
/*  210:     */   }
/*  211:     */   
/*  212:     */   public static char[] grow(char[] array)
/*  213:     */   {
/*  214: 260 */     char[] newArray = new char[(array.length + 1) * 2];
/*  215: 261 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  216: 262 */     return newArray;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public static char[] shrink(char[] array, int size)
/*  220:     */   {
/*  221: 266 */     char[] newArray = new char[array.length - size];
/*  222:     */     
/*  223: 268 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*  224: 269 */     return newArray;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public static char[] compact(char[] array)
/*  228:     */   {
/*  229: 275 */     int nullCount = 0;
/*  230: 276 */     for (char ch : array) {
/*  231: 278 */       if (ch == 0) {
/*  232: 279 */         nullCount++;
/*  233:     */       }
/*  234:     */     }
/*  235: 282 */     char[] newArray = new char[array.length - nullCount];
/*  236:     */     
/*  237: 284 */     int j = 0;
/*  238: 285 */     for (char ch : array) {
/*  239: 287 */       if (ch != 0)
/*  240:     */       {
/*  241: 291 */         newArray[j] = ch;
/*  242: 292 */         j++;
/*  243:     */       }
/*  244:     */     }
/*  245: 294 */     return newArray;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public static char[] remove(char c, char[] array)
/*  249:     */   {
/*  250: 301 */     int removeCount = 0;
/*  251: 302 */     for (char ch : array) {
/*  252: 304 */       if (ch == c) {
/*  253: 305 */         removeCount++;
/*  254:     */       }
/*  255:     */     }
/*  256: 308 */     char[] newArray = new char[array.length - removeCount];
/*  257:     */     
/*  258: 310 */     int j = 0;
/*  259: 311 */     for (char ch : array) {
/*  260: 313 */       if (ch != c)
/*  261:     */       {
/*  262: 317 */         newArray[j] = ch;
/*  263: 318 */         j++;
/*  264:     */       }
/*  265:     */     }
/*  266: 320 */     return newArray;
/*  267:     */   }
/*  268:     */   
/*  269:     */   public static char[] remove(char c, char[] array, int start, int to)
/*  270:     */   {
/*  271: 326 */     int removeCount = 0;
/*  272: 327 */     for (int index = start; index < to; index++)
/*  273:     */     {
/*  274: 328 */       char ch = array[index];
/*  275: 330 */       if (ch == c) {
/*  276: 331 */         removeCount++;
/*  277:     */       }
/*  278:     */     }
/*  279: 334 */     char[] newArray = new char[array.length - removeCount];
/*  280:     */     
/*  281: 336 */     int j = 0;
/*  282: 337 */     for (int index = start; index < to; index++)
/*  283:     */     {
/*  284: 338 */       char ch = array[index];
/*  285: 340 */       if (ch != c)
/*  286:     */       {
/*  287: 344 */         newArray[j] = ch;
/*  288: 345 */         j++;
/*  289:     */       }
/*  290:     */     }
/*  291: 347 */     return newArray;
/*  292:     */   }
/*  293:     */   
/*  294:     */   public static char[][] split(char[] chars)
/*  295:     */   {
/*  296: 352 */     return CharScanner.splitByChars(chars, DEFAULT_SPLIT);
/*  297:     */   }
/*  298:     */   
/*  299:     */   public static char[][] splitLine(char[] chars)
/*  300:     */   {
/*  301: 356 */     return CharScanner.splitByChars(chars, NEWLINE_CHARS);
/*  302:     */   }
/*  303:     */   
/*  304:     */   @Universal
/*  305:     */   public static char[] copy(char[] array)
/*  306:     */   {
/*  307: 362 */     Exceptions.requireNonNull(array);
/*  308: 363 */     char[] newArray = new char[array.length];
/*  309: 364 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  310: 365 */     return newArray;
/*  311:     */   }
/*  312:     */   
/*  313:     */   @Universal
/*  314:     */   public static char[] copy(char[] array, int offset, int length)
/*  315:     */   {
/*  316: 370 */     char[] newArray = new char[length];
/*  317: 371 */     System.arraycopy(array, offset, newArray, 0, length);
/*  318: 372 */     return newArray;
/*  319:     */   }
/*  320:     */   
/*  321:     */   @Universal
/*  322:     */   public static char[] add(char[] array, char v)
/*  323:     */   {
/*  324: 378 */     Exceptions.requireNonNull(array);
/*  325: 379 */     char[] newArray = new char[array.length + 1];
/*  326: 380 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  327: 381 */     newArray[array.length] = v;
/*  328: 382 */     return newArray;
/*  329:     */   }
/*  330:     */   
/*  331:     */   @Universal
/*  332:     */   public static char[] add(char[] array, String str)
/*  333:     */   {
/*  334: 388 */     return add(array, str.toCharArray());
/*  335:     */   }
/*  336:     */   
/*  337:     */   @Universal
/*  338:     */   public static char[] add(char[] array, StringBuilder stringBuilder)
/*  339:     */   {
/*  340: 393 */     return add(array, getCharsFromStringBuilder(stringBuilder));
/*  341:     */   }
/*  342:     */   
/*  343:     */   @Universal
/*  344:     */   public static char[] add(char[] array, char[] array2)
/*  345:     */   {
/*  346: 399 */     char[] newArray = new char[array.length + array2.length];
/*  347: 400 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  348: 401 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/*  349: 402 */     return newArray;
/*  350:     */   }
/*  351:     */   
/*  352:     */   @Universal
/*  353:     */   public static char[] insert(char[] array, int idx, char v)
/*  354:     */   {
/*  355: 408 */     Exceptions.requireNonNull(array);
/*  356: 410 */     if (idx >= array.length) {
/*  357: 411 */       return add(array, v);
/*  358:     */     }
/*  359: 414 */     int index = calculateIndex(array, idx);
/*  360:     */     
/*  361:     */ 
/*  362: 417 */     char[] newArray = new char[array.length + 1];
/*  363: 419 */     if (index != 0) {
/*  364: 422 */       System.arraycopy(array, 0, newArray, 0, index);
/*  365:     */     }
/*  366: 426 */     boolean lastIndex = index == array.length - 1;
/*  367: 427 */     int remainingIndex = array.length - index;
/*  368: 429 */     if (lastIndex) {
/*  369: 432 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  370:     */     } else {
/*  371: 437 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  372:     */     }
/*  373: 441 */     newArray[index] = v;
/*  374: 442 */     return newArray;
/*  375:     */   }
/*  376:     */   
/*  377:     */   @Universal
/*  378:     */   public static char[] insert(char[] array, int fromIndex, String values)
/*  379:     */   {
/*  380: 448 */     return insert(array, fromIndex, values.toCharArray());
/*  381:     */   }
/*  382:     */   
/*  383:     */   @Universal
/*  384:     */   public static char[] insert(char[] array, int fromIndex, StringBuilder values)
/*  385:     */   {
/*  386: 454 */     return insert(array, fromIndex, getCharsFromStringBuilder(values));
/*  387:     */   }
/*  388:     */   
/*  389:     */   @Universal
/*  390:     */   public static char[] insert(char[] array, int fromIndex, char[] values)
/*  391:     */   {
/*  392: 460 */     Exceptions.requireNonNull(array);
/*  393: 462 */     if (fromIndex >= array.length) {
/*  394: 463 */       return add(array, values);
/*  395:     */     }
/*  396: 466 */     int index = calculateIndex(array, fromIndex);
/*  397:     */     
/*  398:     */ 
/*  399: 469 */     char[] newArray = new char[array.length + values.length];
/*  400: 471 */     if (index != 0) {
/*  401: 474 */       System.arraycopy(array, 0, newArray, 0, index);
/*  402:     */     }
/*  403: 478 */     boolean lastIndex = index == array.length - 1;
/*  404:     */     
/*  405: 480 */     int toIndex = index + values.length;
/*  406: 481 */     int remainingIndex = newArray.length - toIndex;
/*  407: 483 */     if (lastIndex) {
/*  408: 486 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  409:     */     } else {
/*  410: 491 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  411:     */     }
/*  412: 495 */     int i = index;
/*  413: 495 */     for (int j = 0; i < toIndex; j++)
/*  414:     */     {
/*  415: 496 */       newArray[i] = values[j];i++;
/*  416:     */     }
/*  417: 498 */     return newArray;
/*  418:     */   }
/*  419:     */   
/*  420:     */   private static char[] getCharsFromStringBuilder(StringBuilder sbuf)
/*  421:     */   {
/*  422: 507 */     int length = sbuf.length();
/*  423: 508 */     char[] array2 = new char[sbuf.length()];
/*  424: 509 */     sbuf.getChars(0, sbuf.length(), array2, 0);
/*  425: 510 */     return array2;
/*  426:     */   }
/*  427:     */   
/*  428:     */   private static int calculateIndex(char[] array, int originalIndex)
/*  429:     */   {
/*  430: 514 */     int length = array.length;
/*  431:     */     
/*  432:     */ 
/*  433:     */ 
/*  434: 518 */     int index = originalIndex;
/*  435: 523 */     if (index < 0) {
/*  436: 524 */       index = length + index;
/*  437:     */     }
/*  438: 535 */     if (index < 0) {
/*  439: 536 */       index = 0;
/*  440:     */     }
/*  441: 538 */     if (index >= length) {
/*  442: 539 */       index = length - 1;
/*  443:     */     }
/*  444: 541 */     return index;
/*  445:     */   }
/*  446:     */   
/*  447:     */   private static int calculateEndIndex(char[] array, int originalIndex)
/*  448:     */   {
/*  449: 546 */     int length = array.length;
/*  450:     */     
/*  451:     */ 
/*  452:     */ 
/*  453: 550 */     int index = originalIndex;
/*  454: 555 */     if (index < 0) {
/*  455: 556 */       index = length + index;
/*  456:     */     }
/*  457: 567 */     if (index < 0) {
/*  458: 568 */       index = 0;
/*  459:     */     }
/*  460: 570 */     if (index > length) {
/*  461: 571 */       index = length;
/*  462:     */     }
/*  463: 573 */     return index;
/*  464:     */   }
/*  465:     */   
/*  466:     */   public static char[] rpad(char[] in, int size, char pad)
/*  467:     */   {
/*  468: 583 */     if (in.length >= size) {
/*  469: 584 */       return in;
/*  470:     */     }
/*  471: 587 */     int index = 0;
/*  472: 588 */     char[] newArray = new char[size];
/*  473: 590 */     for (index = 0; index < in.length; index++) {
/*  474: 591 */       newArray[index] = in[index];
/*  475:     */     }
/*  476: 595 */     for (; index < size; index++) {
/*  477: 596 */       newArray[index] = pad;
/*  478:     */     }
/*  479: 599 */     return newArray;
/*  480:     */   }
/*  481:     */   
/*  482:     */   public static char[] lpad(char[] in, int size, char pad)
/*  483:     */   {
/*  484: 605 */     if (in.length >= size) {
/*  485: 606 */       return in;
/*  486:     */     }
/*  487: 609 */     int delta = size - in.length;
/*  488: 610 */     int index = 0;
/*  489: 611 */     char[] newArray = new char[size];
/*  490: 614 */     for (; index < delta; index++) {
/*  491: 615 */       newArray[index] = pad;
/*  492:     */     }
/*  493: 619 */     for (int index2 = 0; index2 < in.length; index2++)
/*  494:     */     {
/*  495: 620 */       newArray[index] = in[index2];index++;
/*  496:     */     }
/*  497: 623 */     return newArray;
/*  498:     */   }
/*  499:     */   
/*  500:     */   public static char[] underBarCase(char[] in)
/*  501:     */   {
/*  502: 629 */     if ((in == null) || (in.length == 0) || (in.length == 1)) {
/*  503: 630 */       return in;
/*  504:     */     }
/*  505: 633 */     char[] out = null;
/*  506: 634 */     int count = 0;
/*  507:     */     
/*  508: 636 */     boolean wasLower = false;
/*  509: 638 */     for (int index = 0; index < in.length; index++)
/*  510:     */     {
/*  511: 639 */       char ch = in[index];
/*  512:     */       
/*  513:     */ 
/*  514: 642 */       boolean isUpper = Character.isUpperCase(ch);
/*  515: 644 */       if ((wasLower) && (isUpper)) {
/*  516: 645 */         count++;
/*  517:     */       }
/*  518: 648 */       wasLower = Character.isLowerCase(ch);
/*  519:     */     }
/*  520: 652 */     out = new char[in.length + count];
/*  521:     */     
/*  522: 654 */     wasLower = false;
/*  523:     */     
/*  524: 656 */     int index = 0;
/*  525: 656 */     for (int secondIndex = 0; index < in.length; secondIndex++)
/*  526:     */     {
/*  527: 657 */       char ch = in[index];
/*  528:     */       
/*  529:     */ 
/*  530: 660 */       boolean isUpper = Character.isUpperCase(ch);
/*  531: 662 */       if ((wasLower) && (isUpper))
/*  532:     */       {
/*  533: 663 */         out[secondIndex] = '_';
/*  534: 664 */         secondIndex++;
/*  535:     */       }
/*  536: 667 */       if ((ch == ' ') || (ch == '-') || (ch == '\t') || (ch == '.')) {
/*  537: 668 */         out[secondIndex] = '_';
/*  538:     */       } else {
/*  539: 670 */         out[secondIndex] = Character.toUpperCase(ch);
/*  540:     */       }
/*  541: 672 */       wasLower = Character.isLowerCase(ch);index++;
/*  542:     */     }
/*  543: 676 */     return out;
/*  544:     */   }
/*  545:     */   
/*  546:     */   public static char[] camelCase(char[] in, boolean upper)
/*  547:     */   {
/*  548: 682 */     if ((in == null) || (in.length == 0) || (in.length == 1)) {
/*  549: 683 */       return in;
/*  550:     */     }
/*  551: 686 */     char[] out = null;
/*  552: 687 */     int count = 0;
/*  553: 688 */     for (int index = 0; index < in.length; index++)
/*  554:     */     {
/*  555: 689 */       char ch = in[index];
/*  556: 690 */       if ((ch == '_') || (ch == ' ') || (ch == '\t')) {
/*  557: 691 */         count++;
/*  558:     */       }
/*  559:     */     }
/*  560: 695 */     out = new char[in.length - count];
/*  561:     */     
/*  562:     */ 
/*  563: 698 */     boolean upperNext = false;
/*  564:     */     
/*  565: 700 */     int index = 0;
/*  566: 700 */     for (int secondIndex = 0; index < in.length; index++)
/*  567:     */     {
/*  568: 701 */       char ch = in[index];
/*  569: 702 */       if ((ch == '_') || (ch == ' ') || (ch == '\t'))
/*  570:     */       {
/*  571: 703 */         upperNext = true;
/*  572:     */       }
/*  573:     */       else
/*  574:     */       {
/*  575: 705 */         out[secondIndex] = (upperNext ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
/*  576: 706 */         upperNext = false;
/*  577: 707 */         secondIndex++;
/*  578:     */       }
/*  579:     */     }
/*  580: 711 */     if (upper) {
/*  581: 712 */       out[0] = Character.toUpperCase(out[0]);
/*  582:     */     } else {
/*  583: 714 */       out[0] = Character.toLowerCase(out[0]);
/*  584:     */     }
/*  585: 717 */     return out;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public static char[][] split(char[] inputArray, char split)
/*  589:     */   {
/*  590: 723 */     return CharScanner.split(inputArray, split);
/*  591:     */   }
/*  592:     */   
/*  593:     */   public static boolean contains(char[] chars, char c)
/*  594:     */   {
/*  595: 729 */     for (int index = 0; index < chars.length; index++)
/*  596:     */     {
/*  597: 730 */       char ch = chars[index];
/*  598: 731 */       if (ch == c) {
/*  599: 732 */         return true;
/*  600:     */       }
/*  601:     */     }
/*  602: 735 */     return false;
/*  603:     */   }
/*  604:     */   
/*  605:     */   public static boolean contains(char[] chars, char c, int start, int length)
/*  606:     */   {
/*  607: 740 */     int to = length + start;
/*  608: 741 */     for (int index = start; index < to; index++)
/*  609:     */     {
/*  610: 742 */       char ch = chars[index];
/*  611: 743 */       if (ch == c) {
/*  612: 744 */         return true;
/*  613:     */       }
/*  614:     */     }
/*  615: 747 */     return false;
/*  616:     */   }
/*  617:     */   
/*  618:     */   public static void _idx(char[] buffer, int location, byte[] chars)
/*  619:     */   {
/*  620: 751 */     int index2 = 0;
/*  621: 752 */     int endLocation = location + chars.length;
/*  622: 753 */     for (int index = location; index < endLocation; index2++)
/*  623:     */     {
/*  624: 754 */       buffer[index] = ((char)chars[index2]);index++;
/*  625:     */     }
/*  626:     */   }
/*  627:     */   
/*  628:     */   public static void _idx(char[] array, int startIndex, char[] input)
/*  629:     */   {
/*  630: 761 */     System.arraycopy(input, 0, array, startIndex, input.length);
/*  631:     */   }
/*  632:     */   
/*  633:     */   public static void _idx(char[] array, int startIndex, char[] input, int inputLength)
/*  634:     */   {
/*  635: 768 */     System.arraycopy(input, 0, array, startIndex, inputLength);
/*  636:     */   }
/*  637:     */   
/*  638:     */   public static void _idx(char[] buffer, int location, byte[] chars, int start, int end)
/*  639:     */   {
/*  640: 773 */     int index2 = start;
/*  641: 774 */     int endLocation = location + (end - start);
/*  642: 775 */     for (int index = location; index < endLocation; index2++)
/*  643:     */     {
/*  644: 776 */       buffer[index] = ((char)chars[index2]);index++;
/*  645:     */     }
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static char[] trim(char[] buffer, int start, int to)
/*  649:     */   {
/*  650: 785 */     while ((start < to) && (buffer[start] <= ' ')) {
/*  651: 787 */       start++;
/*  652:     */     }
/*  653: 789 */     while ((start < to) && (buffer[(to - 1)] <= ' ')) {
/*  654: 791 */       to--;
/*  655:     */     }
/*  656: 793 */     return (start > 0) || (to < buffer.length) ? Arrays.copyOfRange(buffer, start, to) : buffer;
/*  657:     */   }
/*  658:     */   
/*  659:     */   public static boolean equals(char[] chars1, char[] chars2)
/*  660:     */   {
/*  661: 799 */     if ((chars1 == null) && (chars2 == null)) {
/*  662: 800 */       return true;
/*  663:     */     }
/*  664: 803 */     if ((chars1 == null) || (chars2 == null)) {
/*  665: 804 */       return false;
/*  666:     */     }
/*  667: 807 */     if (chars1.length != chars2.length) {
/*  668: 808 */       return false;
/*  669:     */     }
/*  670: 811 */     for (int index = 0; index < chars1.length; index++) {
/*  671: 812 */       if (chars1[index] != chars2[index]) {
/*  672: 812 */         return false;
/*  673:     */       }
/*  674:     */     }
/*  675: 815 */     return true;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public static boolean equalsNoNullCheck(char[] chars1, char[] chars2)
/*  679:     */   {
/*  680: 820 */     if (chars1.length != chars2.length) {
/*  681: 821 */       return false;
/*  682:     */     }
/*  683: 824 */     for (int index = 0; index < chars1.length; index++) {
/*  684: 825 */       if (chars1[index] != chars2[index]) {
/*  685: 825 */         return false;
/*  686:     */       }
/*  687:     */     }
/*  688: 828 */     return true;
/*  689:     */   }
/*  690:     */   
/*  691:     */   public static char[] add(char[]... strings)
/*  692:     */   {
/*  693: 834 */     int length = 0;
/*  694: 835 */     for (char[] str : strings) {
/*  695: 836 */       if (str != null) {
/*  696: 839 */         length += str.length;
/*  697:     */       }
/*  698:     */     }
/*  699: 841 */     CharBuf builder = CharBuf.createExact(length);
/*  700: 842 */     for (char[] str : strings) {
/*  701: 843 */       if (str != null) {
/*  702: 846 */         builder.add(str);
/*  703:     */       }
/*  704:     */     }
/*  705: 848 */     return builder.toCharArray();
/*  706:     */   }
/*  707:     */   
/*  708:     */   public static char[][] splitLines(char[] chars)
/*  709:     */   {
/*  710: 852 */     return CharScanner.splitLines(chars);
/*  711:     */   }
/*  712:     */   
/*  713:     */   public static char[][] splitComma(char[] chars)
/*  714:     */   {
/*  715: 856 */     return CharScanner.splitComma(chars);
/*  716:     */   }
/*  717:     */   
/*  718:     */   public static void equalsOrDie(char[] ac, char[] bc)
/*  719:     */   {
/*  720: 861 */     char a = '\000';
/*  721: 862 */     char b = '\000';
/*  722:     */     
/*  723: 864 */     int indexOfDiff = -1;
/*  724: 865 */     int indexOfLine = 0;
/*  725: 867 */     for (int index = 0; (index < ac.length) && (index < bc.length); index++)
/*  726:     */     {
/*  727: 868 */       a = ac[index];
/*  728: 869 */       b = bc[index];
/*  729: 871 */       if ((a == '\n') || (b == '\n')) {
/*  730: 872 */         indexOfLine++;
/*  731:     */       }
/*  732: 874 */       if (a != b)
/*  733:     */       {
/*  734: 875 */         indexOfDiff = index;
/*  735: 876 */         break;
/*  736:     */       }
/*  737:     */     }
/*  738: 881 */     if (ac.length != bc.length) {
/*  739: 882 */       indexOfDiff = ac.length < bc.length ? ac.length : bc.length;
/*  740:     */     }
/*  741: 885 */     if (indexOfDiff != -1)
/*  742:     */     {
/*  743: 886 */       CharBuf charBuf = CharBuf.create(ac.length + bc.length + 128);
/*  744: 887 */       charBuf.add("Strings are different. Problem at line ").add(indexOfLine).addLine(".");
/*  745: 888 */       charBuf.add("String A length = ").add(ac.length).addLine().add("<START>").add(ac).add("<END>").addLine().addLine("--- end a ---");
/*  746:     */       
/*  747: 890 */       charBuf.add("String B length = ").add(bc.length).addLine().add("<START>").add(bc).add("<END>").addLine().addLine("--- end b ---");
/*  748:     */       
/*  749:     */ 
/*  750:     */ 
/*  751: 894 */       char[] ac1 = null;
/*  752:     */       try
/*  753:     */       {
/*  754: 896 */         ac1 = sliceOf(ac, indexOfDiff - 20, ac.length - indexOfDiff > 40 ? indexOfDiff + 10 : ac.length + 1);
/*  755:     */       }
/*  756:     */       catch (Exception ex)
/*  757:     */       {
/*  758: 898 */         ac1 = ac;
/*  759:     */       }
/*  760: 902 */       char[] bc1 = null;
/*  761:     */       try
/*  762:     */       {
/*  763: 904 */         bc1 = sliceOf(bc, indexOfDiff - 20, bc.length - indexOfDiff > 40 ? indexOfDiff + 10 : bc.length + 1);
/*  764:     */       }
/*  765:     */       catch (Exception ex)
/*  766:     */       {
/*  767: 906 */         ac1 = bc;
/*  768:     */       }
/*  769: 910 */       CharBuf charBufA = CharBuf.create(ac1.length + 20);
/*  770: 911 */       CharBuf charBufB = CharBuf.create(bc1.length + 20);
/*  771:     */       
/*  772: 913 */       boolean found = false;
/*  773: 914 */       indexOfDiff = 0;
/*  774: 915 */       for (int index = 0; (index < ac1.length) || (index < bc1.length); index++)
/*  775:     */       {
/*  776: 917 */         if (index < ac1.length)
/*  777:     */         {
/*  778: 918 */           a = ac1[index];
/*  779:     */           
/*  780: 920 */           charDescription(a, charBufA);
/*  781:     */         }
/*  782: 922 */         if (index < bc1.length)
/*  783:     */         {
/*  784: 923 */           b = bc1[index];
/*  785:     */           
/*  786: 925 */           charDescription(b, charBufB);
/*  787:     */         }
/*  788: 928 */         if ((a != b) && (!found))
/*  789:     */         {
/*  790: 929 */           found = true;
/*  791: 930 */           indexOfDiff = charBufA.len();
/*  792:     */         }
/*  793:     */       }
/*  794: 936 */       charBuf.puts(new Object[] { multiply('-', 10), "area of concern, line=", Integer.valueOf(indexOfLine), multiply('-', 10) });
/*  795: 937 */       charBuf.addLine(charBufA);
/*  796: 938 */       charBuf.multiply('-', indexOfDiff).addLine("^");
/*  797: 939 */       charBuf.addLine(charBufB);
/*  798: 940 */       charBuf.multiply('-', indexOfDiff).addLine("^");
/*  799:     */       
/*  800:     */ 
/*  801: 943 */       Boon.puts(new Object[] { charBuf });
/*  802: 944 */       Exceptions.die(new Object[] { charBuf });
/*  803:     */     }
/*  804:     */   }
/*  805:     */   
/*  806:     */   private static void charDescription(char a, CharBuf charBufA)
/*  807:     */   {
/*  808: 954 */     if (a == '\n') {
/*  809: 955 */       charBufA.add(" <NEWLINE> ");
/*  810: 956 */     } else if (a == '\r') {
/*  811: 957 */       charBufA.add(" <CARRIAGE_RETURN> ");
/*  812: 958 */     } else if (a == '\t') {
/*  813: 959 */       charBufA.add(" <TAB> ");
/*  814:     */     } else {
/*  815: 961 */       charBufA.add(a);
/*  816:     */     }
/*  817:     */   }
/*  818:     */   
/*  819:     */   public static char[] multiply(char c, int len)
/*  820:     */   {
/*  821: 967 */     char[] out = new char[len];
/*  822: 968 */     for (int index = 0; index < len; index++) {
/*  823: 969 */       out[index] = c;
/*  824:     */     }
/*  825: 971 */     return out;
/*  826:     */   }
/*  827:     */   
/*  828:     */   public static boolean insideOf(char[] startsWith, char[] chars, char[] endsWith)
/*  829:     */   {
/*  830: 976 */     if (startsWith.length + endsWith.length > chars.length) {
/*  831: 977 */       return false;
/*  832:     */     }
/*  833: 982 */     int index = 0;
/*  834: 984 */     if (startsWith.length > 0)
/*  835:     */     {
/*  836: 985 */       if (startsWith[(startsWith.length - 1)] != chars[(startsWith.length - 1)]) {
/*  837: 986 */         return false;
/*  838:     */       }
/*  839: 988 */       if (startsWith[index] == chars[index]) {
/*  840: 989 */         index++;
/*  841:     */       } else {
/*  842: 991 */         return false;
/*  843:     */       }
/*  844:     */     }
/*  845: 996 */     int endIndex = chars.length - 1;
/*  846: 999 */     if (endsWith.length > 0) {
/*  847:1001 */       if (endsWith[(startsWith.length - 1)] == chars[endIndex]) {
/*  848:1002 */         endIndex--;
/*  849:     */       } else {
/*  850:1004 */         return false;
/*  851:     */       }
/*  852:     */     }
/*  853:1011 */     for (; index < startsWith.length - 2; index++) {
/*  854:1012 */       if (chars[index] != startsWith[index]) {
/*  855:1013 */         return false;
/*  856:     */       }
/*  857:     */     }
/*  858:1019 */     for (int i = endsWith.length - 2; i > 0; i--)
/*  859:     */     {
/*  860:1020 */       if (chars[endIndex] != endsWith[i]) {
/*  861:1021 */         return false;
/*  862:     */       }
/*  863:1019 */       endIndex--;
/*  864:     */     }
/*  865:1025 */     return true;
/*  866:     */   }
/*  867:     */   
/*  868:     */   public static boolean isEmpty(char[] messageBodyChars)
/*  869:     */   {
/*  870:1029 */     return (messageBodyChars == null) || (messageBodyChars.length == 0);
/*  871:     */   }
/*  872:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Chr
 * JD-Core Version:    0.7.0.1
 */