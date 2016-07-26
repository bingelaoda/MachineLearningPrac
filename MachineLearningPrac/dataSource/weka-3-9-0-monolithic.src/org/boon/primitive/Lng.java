/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.lang.invoke.ConstantCallSite;
/*    4:     */ import java.lang.invoke.MethodHandle;
/*    5:     */ import java.lang.reflect.Method;
/*    6:     */ import java.util.Arrays;
/*    7:     */ import org.boon.Exceptions;
/*    8:     */ import org.boon.Universal;
/*    9:     */ import org.boon.core.reflection.Invoker;
/*   10:     */ 
/*   11:     */ public class Lng
/*   12:     */ {
/*   13:     */   public static String str(long value)
/*   14:     */   {
/*   15:  48 */     return String.format("%,d", new Object[] { Long.valueOf(value) });
/*   16:     */   }
/*   17:     */   
/*   18:     */   public static long[] grow(long[] array, int size)
/*   19:     */   {
/*   20:  53 */     Exceptions.requireNonNull(array);
/*   21:     */     
/*   22:  55 */     long[] newArray = new long[array.length + size];
/*   23:  56 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   24:  57 */     return newArray;
/*   25:     */   }
/*   26:     */   
/*   27:     */   public static long[] grow(long[] array)
/*   28:     */   {
/*   29:  62 */     Exceptions.requireNonNull(array);
/*   30:     */     
/*   31:  64 */     long[] newArray = new long[array.length * 2];
/*   32:  65 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   33:  66 */     return newArray;
/*   34:     */   }
/*   35:     */   
/*   36:     */   public static long[] shrink(long[] array, int size)
/*   37:     */   {
/*   38:  71 */     Exceptions.requireNonNull(array);
/*   39:     */     
/*   40:  73 */     long[] newArray = new long[array.length - size];
/*   41:     */     
/*   42:  75 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*   43:  76 */     return newArray;
/*   44:     */   }
/*   45:     */   
/*   46:     */   public static long[] compact(long[] array)
/*   47:     */   {
/*   48:  81 */     Exceptions.requireNonNull(array);
/*   49:     */     
/*   50:  83 */     int nullCount = 0;
/*   51:  84 */     for (long ch : array) {
/*   52:  86 */       if (ch == 0L) {
/*   53:  87 */         nullCount++;
/*   54:     */       }
/*   55:     */     }
/*   56:  90 */     long[] newArray = new long[array.length - nullCount];
/*   57:     */     
/*   58:  92 */     int j = 0;
/*   59:  93 */     for (long ch : array) {
/*   60:  95 */       if (ch != 0L)
/*   61:     */       {
/*   62:  99 */         newArray[j] = ch;
/*   63: 100 */         j++;
/*   64:     */       }
/*   65:     */     }
/*   66: 102 */     return newArray;
/*   67:     */   }
/*   68:     */   
/*   69:     */   public static long[] arrayOfLong(int size)
/*   70:     */   {
/*   71: 113 */     return new long[size];
/*   72:     */   }
/*   73:     */   
/*   74:     */   @Universal
/*   75:     */   public static long[] array(long... array)
/*   76:     */   {
/*   77: 122 */     Exceptions.requireNonNull(array);
/*   78: 123 */     return array;
/*   79:     */   }
/*   80:     */   
/*   81:     */   @Universal
/*   82:     */   public static int len(long[] array)
/*   83:     */   {
/*   84: 129 */     return array.length;
/*   85:     */   }
/*   86:     */   
/*   87:     */   @Universal
/*   88:     */   public static int lengthOf(long[] array)
/*   89:     */   {
/*   90: 135 */     return array.length;
/*   91:     */   }
/*   92:     */   
/*   93:     */   @Universal
/*   94:     */   public static long idx(long[] array, int index)
/*   95:     */   {
/*   96: 141 */     int i = calculateIndex(array, index);
/*   97:     */     
/*   98: 143 */     return array[i];
/*   99:     */   }
/*  100:     */   
/*  101:     */   @Universal
/*  102:     */   public static long atIndex(long[] array, int index)
/*  103:     */   {
/*  104: 149 */     int i = calculateIndex(array, index);
/*  105:     */     
/*  106: 151 */     return array[i];
/*  107:     */   }
/*  108:     */   
/*  109:     */   @Universal
/*  110:     */   public static void idx(long[] array, int index, long value)
/*  111:     */   {
/*  112: 157 */     int i = calculateIndex(array, index);
/*  113:     */     
/*  114: 159 */     array[i] = value;
/*  115:     */   }
/*  116:     */   
/*  117:     */   @Universal
/*  118:     */   public static void atIndex(long[] array, int index, long value)
/*  119:     */   {
/*  120: 165 */     int i = calculateIndex(array, index);
/*  121:     */     
/*  122: 167 */     array[i] = value;
/*  123:     */   }
/*  124:     */   
/*  125:     */   @Universal
/*  126:     */   public static long[] slc(long[] array, int startIndex, int endIndex)
/*  127:     */   {
/*  128: 174 */     int start = calculateIndex(array, startIndex);
/*  129: 175 */     int end = calculateEndIndex(array, endIndex);
/*  130: 176 */     int newLength = end - start;
/*  131: 178 */     if (newLength < 0) {
/*  132: 179 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  133:     */     }
/*  134: 185 */     long[] newArray = new long[newLength];
/*  135: 186 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  136: 187 */     return newArray;
/*  137:     */   }
/*  138:     */   
/*  139:     */   @Universal
/*  140:     */   public static long[] sliceOf(long[] array, int startIndex, int endIndex)
/*  141:     */   {
/*  142: 194 */     int start = calculateIndex(array, startIndex);
/*  143: 195 */     int end = calculateEndIndex(array, endIndex);
/*  144: 196 */     int newLength = end - start;
/*  145: 198 */     if (newLength < 0) {
/*  146: 199 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  147:     */     }
/*  148: 205 */     long[] newArray = new long[newLength];
/*  149: 206 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  150: 207 */     return newArray;
/*  151:     */   }
/*  152:     */   
/*  153:     */   @Universal
/*  154:     */   public static long[] slc(long[] array, int startIndex)
/*  155:     */   {
/*  156: 214 */     int start = calculateIndex(array, startIndex);
/*  157: 215 */     int newLength = array.length - start;
/*  158: 217 */     if (newLength < 0) {
/*  159: 218 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  160:     */     }
/*  161: 224 */     long[] newArray = new long[newLength];
/*  162: 225 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  163: 226 */     return newArray;
/*  164:     */   }
/*  165:     */   
/*  166:     */   @Universal
/*  167:     */   public static long[] sliceOf(long[] array, int startIndex)
/*  168:     */   {
/*  169: 233 */     int start = calculateIndex(array, startIndex);
/*  170: 234 */     int newLength = array.length - start;
/*  171: 236 */     if (newLength < 0) {
/*  172: 237 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  173:     */     }
/*  174: 243 */     long[] newArray = new long[newLength];
/*  175: 244 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  176: 245 */     return newArray;
/*  177:     */   }
/*  178:     */   
/*  179:     */   @Universal
/*  180:     */   public static long[] slcEnd(long[] array, int endIndex)
/*  181:     */   {
/*  182: 251 */     int end = calculateEndIndex(array, endIndex);
/*  183: 252 */     int newLength = end;
/*  184: 254 */     if (newLength < 0) {
/*  185: 255 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  186:     */     }
/*  187: 261 */     long[] newArray = new long[newLength];
/*  188: 262 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  189: 263 */     return newArray;
/*  190:     */   }
/*  191:     */   
/*  192:     */   @Universal
/*  193:     */   public static long[] endSliceOf(long[] array, int endIndex)
/*  194:     */   {
/*  195: 270 */     int end = calculateEndIndex(array, endIndex);
/*  196: 271 */     int newLength = end;
/*  197: 273 */     if (newLength < 0) {
/*  198: 274 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  199:     */     }
/*  200: 280 */     long[] newArray = new long[newLength];
/*  201: 281 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  202: 282 */     return newArray;
/*  203:     */   }
/*  204:     */   
/*  205:     */   @Universal
/*  206:     */   public static boolean in(long value, long[] array)
/*  207:     */   {
/*  208: 287 */     for (long currentValue : array) {
/*  209: 288 */       if (currentValue == value) {
/*  210: 289 */         return true;
/*  211:     */       }
/*  212:     */     }
/*  213: 292 */     return false;
/*  214:     */   }
/*  215:     */   
/*  216:     */   @Universal
/*  217:     */   public static long[] copy(long[] array)
/*  218:     */   {
/*  219: 298 */     Exceptions.requireNonNull(array);
/*  220: 299 */     long[] newArray = new long[array.length];
/*  221: 300 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  222: 301 */     return newArray;
/*  223:     */   }
/*  224:     */   
/*  225:     */   @Universal
/*  226:     */   public static long[] add(long[] array, long v)
/*  227:     */   {
/*  228: 307 */     Exceptions.requireNonNull(array);
/*  229: 308 */     long[] newArray = new long[array.length + 1];
/*  230: 309 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  231: 310 */     newArray[array.length] = v;
/*  232: 311 */     return newArray;
/*  233:     */   }
/*  234:     */   
/*  235:     */   @Universal
/*  236:     */   public static long[] add(long[] array, long[] array2)
/*  237:     */   {
/*  238: 316 */     Exceptions.requireNonNull(array);
/*  239: 317 */     long[] newArray = new long[array.length + array2.length];
/*  240: 318 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  241: 319 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/*  242: 320 */     return newArray;
/*  243:     */   }
/*  244:     */   
/*  245:     */   @Universal
/*  246:     */   public static long[] insert(long[] array, int idx, long v)
/*  247:     */   {
/*  248: 326 */     Exceptions.requireNonNull(array);
/*  249: 328 */     if (idx >= array.length) {
/*  250: 329 */       return add(array, v);
/*  251:     */     }
/*  252: 332 */     int index = calculateIndex(array, idx);
/*  253:     */     
/*  254:     */ 
/*  255: 335 */     long[] newArray = new long[array.length + 1];
/*  256: 337 */     if (index != 0) {
/*  257: 340 */       System.arraycopy(array, 0, newArray, 0, index);
/*  258:     */     }
/*  259: 344 */     boolean lastIndex = index == array.length - 1;
/*  260: 345 */     int remainingIndex = array.length - index;
/*  261: 347 */     if (lastIndex) {
/*  262: 350 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  263:     */     } else {
/*  264: 355 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  265:     */     }
/*  266: 359 */     newArray[index] = v;
/*  267: 360 */     return newArray;
/*  268:     */   }
/*  269:     */   
/*  270:     */   @Universal
/*  271:     */   public static long[] insert(long[] array, int fromIndex, long[] values)
/*  272:     */   {
/*  273: 366 */     Exceptions.requireNonNull(array);
/*  274: 368 */     if (fromIndex >= array.length) {
/*  275: 369 */       return add(array, values);
/*  276:     */     }
/*  277: 372 */     int index = calculateIndex(array, fromIndex);
/*  278:     */     
/*  279:     */ 
/*  280: 375 */     long[] newArray = new long[array.length + values.length];
/*  281: 377 */     if (index != 0) {
/*  282: 380 */       System.arraycopy(array, 0, newArray, 0, index);
/*  283:     */     }
/*  284: 384 */     boolean lastIndex = index == array.length - 1;
/*  285:     */     
/*  286: 386 */     int toIndex = index + values.length;
/*  287: 387 */     int remainingIndex = newArray.length - toIndex;
/*  288: 389 */     if (lastIndex) {
/*  289: 392 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  290:     */     } else {
/*  291: 397 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  292:     */     }
/*  293: 401 */     int i = index;
/*  294: 401 */     for (int j = 0; i < toIndex; j++)
/*  295:     */     {
/*  296: 402 */       newArray[i] = values[j];i++;
/*  297:     */     }
/*  298: 404 */     return newArray;
/*  299:     */   }
/*  300:     */   
/*  301:     */   private static int calculateIndex(long[] array, int originalIndex)
/*  302:     */   {
/*  303: 410 */     int length = array.length;
/*  304:     */     
/*  305: 412 */     Exceptions.requireNonNull(array, "array cannot be null");
/*  306:     */     
/*  307:     */ 
/*  308: 415 */     int index = originalIndex;
/*  309: 420 */     if (index < 0) {
/*  310: 421 */       index = length + index;
/*  311:     */     }
/*  312: 432 */     if (index < 0) {
/*  313: 433 */       index = 0;
/*  314:     */     }
/*  315: 435 */     if (index >= length) {
/*  316: 436 */       index = length - 1;
/*  317:     */     }
/*  318: 438 */     return index;
/*  319:     */   }
/*  320:     */   
/*  321:     */   private static int calculateEndIndex(long[] array, int originalIndex)
/*  322:     */   {
/*  323: 445 */     int length = array.length;
/*  324:     */     
/*  325: 447 */     Exceptions.requireNonNull(array, "array cannot be null");
/*  326:     */     
/*  327:     */ 
/*  328: 450 */     int index = originalIndex;
/*  329: 455 */     if (index < 0) {
/*  330: 456 */       index = length + index;
/*  331:     */     }
/*  332: 467 */     if (index < 0) {
/*  333: 468 */       index = 0;
/*  334:     */     }
/*  335: 470 */     if (index > length) {
/*  336: 471 */       index = length;
/*  337:     */     }
/*  338: 473 */     return index;
/*  339:     */   }
/*  340:     */   
/*  341:     */   public static long reduceBy(long[] array, ReduceBy reduceBy)
/*  342:     */   {
/*  343: 493 */     long sum = 0L;
/*  344: 494 */     for (long v : array) {
/*  345: 495 */       sum = reduceBy.reduce(sum, v);
/*  346:     */     }
/*  347: 497 */     return sum;
/*  348:     */   }
/*  349:     */   
/*  350:     */   public static long reduceBy(long[] array, int start, int length, ReduceBy reduceBy)
/*  351:     */   {
/*  352: 511 */     long sum = 0L;
/*  353: 513 */     for (int index = start; index < length; index++)
/*  354:     */     {
/*  355: 514 */       long v = array[index];
/*  356: 515 */       sum = reduceBy.reduce(sum, v);
/*  357:     */     }
/*  358: 517 */     return sum;
/*  359:     */   }
/*  360:     */   
/*  361:     */   public static long reduceBy(long[] array, int length, ReduceBy reduceBy)
/*  362:     */   {
/*  363: 531 */     long sum = 0L;
/*  364: 533 */     for (int index = 0; index < length; index++)
/*  365:     */     {
/*  366: 534 */       long v = array[index];
/*  367: 535 */       sum = reduceBy.reduce(sum, v);
/*  368:     */     }
/*  369: 537 */     return sum;
/*  370:     */   }
/*  371:     */   
/*  372:     */   public static <T> long reduceBy(long[] array, T object)
/*  373:     */   {
/*  374: 551 */     if (object.getClass().isAnonymousClass()) {
/*  375: 552 */       return reduceByR(array, object);
/*  376:     */     }
/*  377:     */     try
/*  378:     */     {
/*  379: 557 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  380: 558 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  381:     */       try
/*  382:     */       {
/*  383: 561 */         long sum = 0L;
/*  384: 562 */         for (long v : array) {
/*  385: 563 */           sum = methodHandle.invokeExact(sum, v);
/*  386:     */         }
/*  387: 566 */         return sum;
/*  388:     */       }
/*  389:     */       catch (Throwable throwable)
/*  390:     */       {
/*  391: 568 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  392:     */       }
/*  393: 571 */       return reduceByR(array, object);
/*  394:     */     }
/*  395:     */     catch (Exception ex) {}
/*  396:     */   }
/*  397:     */   
/*  398:     */   public static <T> long reduceBy(long[] array, T object, String methodName)
/*  399:     */   {
/*  400: 589 */     if (object.getClass().isAnonymousClass()) {
/*  401: 590 */       return reduceByR(array, object, methodName);
/*  402:     */     }
/*  403:     */     try
/*  404:     */     {
/*  405: 594 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
/*  406: 595 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  407:     */       try
/*  408:     */       {
/*  409: 598 */         long sum = 0L;
/*  410: 599 */         for (long v : array) {
/*  411: 600 */           sum = methodHandle.invokeExact(sum, v);
/*  412:     */         }
/*  413: 603 */         return sum;
/*  414:     */       }
/*  415:     */       catch (Throwable throwable)
/*  416:     */       {
/*  417: 605 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  418:     */       }
/*  419: 608 */       return reduceByR(array, object, methodName);
/*  420:     */     }
/*  421:     */     catch (Exception ex) {}
/*  422:     */   }
/*  423:     */   
/*  424:     */   private static <T> long reduceByR(long[] array, T object)
/*  425:     */   {
/*  426:     */     try
/*  427:     */     {
/*  428: 625 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  429:     */       
/*  430:     */ 
/*  431: 628 */       long sum = 0L;
/*  432: 629 */       for (long v : array) {
/*  433: 630 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Long.valueOf(v) })).longValue();
/*  434:     */       }
/*  435: 633 */       return sum;
/*  436:     */     }
/*  437:     */     catch (Throwable throwable)
/*  438:     */     {
/*  439: 636 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  440:     */     }
/*  441:     */   }
/*  442:     */   
/*  443:     */   private static <T> long reduceByR(long[] array, T object, String methodName)
/*  444:     */   {
/*  445:     */     try
/*  446:     */     {
/*  447: 653 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  448:     */       
/*  449:     */ 
/*  450: 656 */       long sum = 0L;
/*  451: 657 */       for (long v : array) {
/*  452: 658 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Long.valueOf(v) })).longValue();
/*  453:     */       }
/*  454: 661 */       return sum;
/*  455:     */     }
/*  456:     */     catch (Throwable throwable)
/*  457:     */     {
/*  458: 664 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  459:     */     }
/*  460:     */   }
/*  461:     */   
/*  462:     */   private static <T> long reduceByR(long[] array, int length, T object, String methodName)
/*  463:     */   {
/*  464:     */     try
/*  465:     */     {
/*  466: 682 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  467:     */       
/*  468:     */ 
/*  469: 685 */       long sum = 0L;
/*  470: 686 */       for (int index = 0; index < length; index++)
/*  471:     */       {
/*  472: 687 */         long v = array[index];
/*  473: 688 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Long.valueOf(v) })).longValue();
/*  474:     */       }
/*  475: 691 */       return sum;
/*  476:     */     }
/*  477:     */     catch (Throwable throwable)
/*  478:     */     {
/*  479: 694 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  480:     */     }
/*  481:     */   }
/*  482:     */   
/*  483:     */   private static <T> long reduceByR(long[] array, int length, T object)
/*  484:     */   {
/*  485:     */     try
/*  486:     */     {
/*  487: 710 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  488:     */       
/*  489:     */ 
/*  490: 713 */       long sum = 0L;
/*  491: 714 */       for (int index = 0; index < length; index++)
/*  492:     */       {
/*  493: 715 */         long v = array[index];
/*  494: 716 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Long.valueOf(v) })).longValue();
/*  495:     */       }
/*  496: 719 */       return sum;
/*  497:     */     }
/*  498:     */     catch (Throwable throwable)
/*  499:     */     {
/*  500: 722 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  501:     */     }
/*  502:     */   }
/*  503:     */   
/*  504:     */   public static long reduceBy(long[] array, int length, Object object)
/*  505:     */   {
/*  506: 738 */     if (object.getClass().isAnonymousClass()) {
/*  507: 739 */       return reduceByR(array, length, object);
/*  508:     */     }
/*  509:     */     try
/*  510:     */     {
/*  511: 743 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  512: 744 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  513:     */       try
/*  514:     */       {
/*  515: 747 */         long sum = 0L;
/*  516: 748 */         for (int index = 0; index < length; index++)
/*  517:     */         {
/*  518: 749 */           long v = array[index];
/*  519: 750 */           sum = methodHandle.invokeExact(sum, v);
/*  520:     */         }
/*  521: 753 */         return sum;
/*  522:     */       }
/*  523:     */       catch (Throwable throwable)
/*  524:     */       {
/*  525: 755 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  526:     */       }
/*  527: 758 */       return reduceByR(array, length, object);
/*  528:     */     }
/*  529:     */     catch (Exception ex) {}
/*  530:     */   }
/*  531:     */   
/*  532:     */   public static long reduceBy(long[] array, int length, Object function, String functionName)
/*  533:     */   {
/*  534: 778 */     if (function.getClass().isAnonymousClass()) {
/*  535: 779 */       return reduceByR(array, length, function, functionName);
/*  536:     */     }
/*  537:     */     try
/*  538:     */     {
/*  539: 783 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(function, functionName);
/*  540: 784 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  541:     */       try
/*  542:     */       {
/*  543: 787 */         long sum = 0L;
/*  544: 788 */         for (int index = 0; index < length; index++)
/*  545:     */         {
/*  546: 789 */           long v = array[index];
/*  547: 790 */           sum = methodHandle.invokeExact(sum, v);
/*  548:     */         }
/*  549: 793 */         return sum;
/*  550:     */       }
/*  551:     */       catch (Throwable throwable)
/*  552:     */       {
/*  553: 795 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  554:     */       }
/*  555: 798 */       return reduceByR(array, length, function, functionName);
/*  556:     */     }
/*  557:     */     catch (Exception ex) {}
/*  558:     */   }
/*  559:     */   
/*  560:     */   public static long reduceBy(long[] array, int start, int length, Object object)
/*  561:     */   {
/*  562: 816 */     if (object.getClass().isAnonymousClass()) {
/*  563: 817 */       return reduceByR(array, object);
/*  564:     */     }
/*  565:     */     try
/*  566:     */     {
/*  567: 821 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  568: 822 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  569:     */       try
/*  570:     */       {
/*  571: 825 */         long sum = 0L;
/*  572: 826 */         for (int index = start; index < length; index++)
/*  573:     */         {
/*  574: 827 */           long v = array[index];
/*  575: 828 */           sum = methodHandle.invokeExact(sum, v);
/*  576:     */         }
/*  577: 831 */         return sum;
/*  578:     */       }
/*  579:     */       catch (Throwable throwable)
/*  580:     */       {
/*  581: 833 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  582:     */       }
/*  583: 836 */       return reduceByR(array, object);
/*  584:     */     }
/*  585:     */     catch (Exception ex) {}
/*  586:     */   }
/*  587:     */   
/*  588:     */   public static boolean equalsOrDie(long expected, long got)
/*  589:     */   {
/*  590: 848 */     if (expected != got) {
/*  591: 849 */       return ((Boolean)Exceptions.die(Boolean.class, new Object[] { "Expected was", Long.valueOf(expected), "but we got ", Long.valueOf(got) })).booleanValue();
/*  592:     */     }
/*  593: 851 */     return true;
/*  594:     */   }
/*  595:     */   
/*  596:     */   public static boolean equals(long expected, long got)
/*  597:     */   {
/*  598: 863 */     return expected == got;
/*  599:     */   }
/*  600:     */   
/*  601:     */   public static long sum(long[] values)
/*  602:     */   {
/*  603: 874 */     return sum(values, 0, values.length);
/*  604:     */   }
/*  605:     */   
/*  606:     */   public static long sum(long[] values, int length)
/*  607:     */   {
/*  608: 884 */     return sum(values, 0, length);
/*  609:     */   }
/*  610:     */   
/*  611:     */   public static long sum(long[] values, int start, int length)
/*  612:     */   {
/*  613: 893 */     long sum = 0L;
/*  614: 894 */     for (int index = start; index < length; index++) {
/*  615: 895 */       sum += values[index];
/*  616:     */     }
/*  617: 897 */     return sum;
/*  618:     */   }
/*  619:     */   
/*  620:     */   public static long max(long[] values, int start, int length)
/*  621:     */   {
/*  622: 908 */     long max = -9223372036854775808L;
/*  623: 909 */     for (int index = start; index < length; index++) {
/*  624: 910 */       if (values[index] > max) {
/*  625: 911 */         max = values[index];
/*  626:     */       }
/*  627:     */     }
/*  628: 915 */     return max;
/*  629:     */   }
/*  630:     */   
/*  631:     */   public static long max(long[] values)
/*  632:     */   {
/*  633: 925 */     return max(values, 0, values.length);
/*  634:     */   }
/*  635:     */   
/*  636:     */   public static long max(long[] values, int length)
/*  637:     */   {
/*  638: 935 */     return max(values, 0, length);
/*  639:     */   }
/*  640:     */   
/*  641:     */   public static long min(long[] values, int start, int length)
/*  642:     */   {
/*  643: 945 */     long min = 9223372036854775807L;
/*  644: 946 */     for (int index = start; index < length; index++) {
/*  645: 947 */       if (values[index] < min) {
/*  646: 947 */         min = values[index];
/*  647:     */       }
/*  648:     */     }
/*  649: 949 */     return min;
/*  650:     */   }
/*  651:     */   
/*  652:     */   public static long min(long[] values)
/*  653:     */   {
/*  654: 959 */     return min(values, 0, values.length);
/*  655:     */   }
/*  656:     */   
/*  657:     */   public static long min(long[] values, int length)
/*  658:     */   {
/*  659: 969 */     return min(values, 0, length);
/*  660:     */   }
/*  661:     */   
/*  662:     */   public static long mean(long[] values, int start, int length)
/*  663:     */   {
/*  664: 981 */     return Math.round(meanDouble(values, start, length));
/*  665:     */   }
/*  666:     */   
/*  667:     */   public static long mean(long[] values, int length)
/*  668:     */   {
/*  669: 993 */     return Math.round(meanDouble(values, 0, length));
/*  670:     */   }
/*  671:     */   
/*  672:     */   public static long mean(long[] values)
/*  673:     */   {
/*  674:1004 */     return Math.round(meanDouble(values, 0, values.length));
/*  675:     */   }
/*  676:     */   
/*  677:     */   public static long variance(long[] values, int start, int length)
/*  678:     */   {
/*  679:1016 */     return Math.round(varianceDouble(values, start, length));
/*  680:     */   }
/*  681:     */   
/*  682:     */   public static double meanDouble(long[] values, int start, int length)
/*  683:     */   {
/*  684:1028 */     double mean = sum(values, start, length) / length;
/*  685:1029 */     return mean;
/*  686:     */   }
/*  687:     */   
/*  688:     */   public static double varianceDouble(long[] values, int start, int length)
/*  689:     */   {
/*  690:1040 */     double mean = meanDouble(values, start, length);
/*  691:1041 */     double temp = 0.0D;
/*  692:1042 */     for (int index = start; index < length; index++)
/*  693:     */     {
/*  694:1043 */       double a = values[index];
/*  695:1044 */       temp += (mean - a) * (mean - a);
/*  696:     */     }
/*  697:1046 */     return temp / length;
/*  698:     */   }
/*  699:     */   
/*  700:     */   public static long variance(long[] values, int length)
/*  701:     */   {
/*  702:1056 */     return Math.round(varianceDouble(values, 0, length));
/*  703:     */   }
/*  704:     */   
/*  705:     */   public static long variance(long[] values)
/*  706:     */   {
/*  707:1065 */     return Math.round(varianceDouble(values, 0, values.length));
/*  708:     */   }
/*  709:     */   
/*  710:     */   public static long standardDeviation(long[] values, int start, int length)
/*  711:     */   {
/*  712:1077 */     double variance = varianceDouble(values, start, length);
/*  713:1078 */     return (int)Math.round(Math.sqrt(variance));
/*  714:     */   }
/*  715:     */   
/*  716:     */   public static long standardDeviation(long[] values, int length)
/*  717:     */   {
/*  718:1088 */     double variance = varianceDouble(values, 0, length);
/*  719:1089 */     return Math.round(Math.sqrt(variance));
/*  720:     */   }
/*  721:     */   
/*  722:     */   public static int standardDeviation(long[] values)
/*  723:     */   {
/*  724:1099 */     double variance = varianceDouble(values, 0, values.length);
/*  725:1100 */     return (int)Math.round(Math.sqrt(variance));
/*  726:     */   }
/*  727:     */   
/*  728:     */   public static long median(long[] values, int start, int length)
/*  729:     */   {
/*  730:1111 */     long[] sorted = new long[length];
/*  731:1112 */     System.arraycopy(values, start, sorted, 0, length);
/*  732:1113 */     Arrays.sort(sorted);
/*  733:1115 */     if (length % 2 == 0)
/*  734:     */     {
/*  735:1116 */       int middle = sorted.length / 2;
/*  736:1117 */       double median = (sorted[(middle - 1)] + sorted[middle]) / 2.0D;
/*  737:1118 */       return Math.round(median);
/*  738:     */     }
/*  739:1120 */     return sorted[(sorted.length / 2)];
/*  740:     */   }
/*  741:     */   
/*  742:     */   public static long median(long[] values, int length)
/*  743:     */   {
/*  744:1132 */     return median(values, 0, length);
/*  745:     */   }
/*  746:     */   
/*  747:     */   public static long median(long[] values)
/*  748:     */   {
/*  749:1142 */     return median(values, 0, values.length);
/*  750:     */   }
/*  751:     */   
/*  752:     */   public static boolean equalsOrDie(long[] expected, long[] got)
/*  753:     */   {
/*  754:1155 */     if (expected.length != got.length) {
/*  755:1156 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/*  756:     */     }
/*  757:1160 */     for (int index = 0; index < expected.length; index++) {
/*  758:1161 */       if (expected[index] != got[index]) {
/*  759:1162 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Long.valueOf(expected[index]), "but got", Long.valueOf(got[index]) });
/*  760:     */       }
/*  761:     */     }
/*  762:1168 */     return true;
/*  763:     */   }
/*  764:     */   
/*  765:     */   public static boolean equals(long[] expected, long[] got)
/*  766:     */   {
/*  767:1180 */     if (expected.length != got.length) {
/*  768:1181 */       return false;
/*  769:     */     }
/*  770:1184 */     for (int index = 0; index < expected.length; index++) {
/*  771:1185 */       if (expected[index] != got[index]) {
/*  772:1186 */         return false;
/*  773:     */       }
/*  774:     */     }
/*  775:1189 */     return true;
/*  776:     */   }
/*  777:     */   
/*  778:     */   public static boolean equals(int start, int end, long[] expected, long[] got)
/*  779:     */   {
/*  780:1201 */     if (expected.length != got.length) {
/*  781:1202 */       return false;
/*  782:     */     }
/*  783:1205 */     for (int index = start; index < end; index++) {
/*  784:1206 */       if (expected[index] != got[index]) {
/*  785:1207 */         return false;
/*  786:     */       }
/*  787:     */     }
/*  788:1210 */     return true;
/*  789:     */   }
/*  790:     */   
/*  791:     */   public static int hashCode(long[] array)
/*  792:     */   {
/*  793:1214 */     if (array == null) {
/*  794:1215 */       return 0;
/*  795:     */     }
/*  796:1217 */     int result = 1;
/*  797:1218 */     for (long element : array)
/*  798:     */     {
/*  799:1219 */       int elementHash = (int)(element ^ element >>> 32);
/*  800:1220 */       result = 31 * result + elementHash;
/*  801:     */     }
/*  802:1223 */     return result;
/*  803:     */   }
/*  804:     */   
/*  805:     */   public static int hashCode(int start, int end, long[] array)
/*  806:     */   {
/*  807:1227 */     if (array == null) {
/*  808:1228 */       return 0;
/*  809:     */     }
/*  810:1230 */     int result = 1;
/*  811:1232 */     for (int index = start; index < end; index++)
/*  812:     */     {
/*  813:1233 */       long element = array[index];
/*  814:1234 */       int elementHash = (int)(element ^ element >>> 32);
/*  815:1235 */       result = 31 * result + elementHash;
/*  816:     */     }
/*  817:1239 */     return result;
/*  818:     */   }
/*  819:     */   
/*  820:     */   public static abstract interface ReduceBy
/*  821:     */   {
/*  822:     */     public abstract long reduce(long paramLong1, long paramLong2);
/*  823:     */   }
/*  824:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Lng
 * JD-Core Version:    0.7.0.1
 */