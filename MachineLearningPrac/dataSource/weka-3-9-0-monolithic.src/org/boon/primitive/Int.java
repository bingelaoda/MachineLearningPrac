/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.lang.invoke.ConstantCallSite;
/*    4:     */ import java.lang.invoke.MethodHandle;
/*    5:     */ import java.lang.reflect.Method;
/*    6:     */ import java.util.Arrays;
/*    7:     */ import java.util.Collection;
/*    8:     */ import java.util.Iterator;
/*    9:     */ import java.util.Map;
/*   10:     */ import org.boon.Exceptions;
/*   11:     */ import org.boon.StringScanner;
/*   12:     */ import org.boon.Universal;
/*   13:     */ import org.boon.collections.IntList;
/*   14:     */ import org.boon.core.reflection.BeanUtils;
/*   15:     */ import org.boon.core.reflection.Invoker;
/*   16:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   17:     */ 
/*   18:     */ public class Int
/*   19:     */ {
/*   20:     */   public static int[] grow(int[] array, int size)
/*   21:     */   {
/*   22:  65 */     int[] newArray = new int[array.length + size];
/*   23:  66 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   24:  67 */     return newArray;
/*   25:     */   }
/*   26:     */   
/*   27:     */   public static int[] grow(int[] array)
/*   28:     */   {
/*   29:  77 */     Exceptions.requireNonNull(array);
/*   30:     */     
/*   31:  79 */     int[] newArray = new int[array.length * 2];
/*   32:  80 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   33:  81 */     return newArray;
/*   34:     */   }
/*   35:     */   
/*   36:     */   public static int[] shrink(int[] array, int size)
/*   37:     */   {
/*   38:  93 */     int[] newArray = new int[array.length - size];
/*   39:     */     
/*   40:  95 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*   41:  96 */     return newArray;
/*   42:     */   }
/*   43:     */   
/*   44:     */   public static int[] compact(int[] array)
/*   45:     */   {
/*   46: 106 */     Exceptions.requireNonNull(array);
/*   47:     */     
/*   48: 108 */     int nullCount = 0;
/*   49: 109 */     for (int ch : array) {
/*   50: 111 */       if (ch == 0) {
/*   51: 112 */         nullCount++;
/*   52:     */       }
/*   53:     */     }
/*   54: 115 */     int[] newArray = new int[array.length - nullCount];
/*   55:     */     
/*   56: 117 */     int j = 0;
/*   57: 118 */     for (int ch : array) {
/*   58: 120 */       if (ch != 0)
/*   59:     */       {
/*   60: 124 */         newArray[j] = ch;
/*   61: 125 */         j++;
/*   62:     */       }
/*   63:     */     }
/*   64: 127 */     return newArray;
/*   65:     */   }
/*   66:     */   
/*   67:     */   public static int[] arrayOfInt(int size)
/*   68:     */   {
/*   69: 138 */     return new int[size];
/*   70:     */   }
/*   71:     */   
/*   72:     */   @Universal
/*   73:     */   public static int[] array(int... array)
/*   74:     */   {
/*   75: 147 */     Exceptions.requireNonNull(array);
/*   76: 148 */     return array;
/*   77:     */   }
/*   78:     */   
/*   79:     */   @Universal
/*   80:     */   public static int lengthOf(int[] array)
/*   81:     */   {
/*   82: 159 */     return len(array);
/*   83:     */   }
/*   84:     */   
/*   85:     */   @Universal
/*   86:     */   public static int len(int[] array)
/*   87:     */   {
/*   88: 170 */     return array.length;
/*   89:     */   }
/*   90:     */   
/*   91:     */   @Universal
/*   92:     */   public static int idx(int[] array, int index)
/*   93:     */   {
/*   94: 182 */     int i = calculateIndex(array, index);
/*   95:     */     
/*   96: 184 */     return array[i];
/*   97:     */   }
/*   98:     */   
/*   99:     */   @Universal
/*  100:     */   public static int atIndex(int[] array, int index)
/*  101:     */   {
/*  102: 196 */     int i = calculateIndex(array, index);
/*  103:     */     
/*  104: 198 */     return array[i];
/*  105:     */   }
/*  106:     */   
/*  107:     */   @Universal
/*  108:     */   public static void idx(int[] array, int index, int value)
/*  109:     */   {
/*  110: 209 */     int i = calculateIndex(array, index);
/*  111:     */     
/*  112: 211 */     array[i] = value;
/*  113:     */   }
/*  114:     */   
/*  115:     */   @Universal
/*  116:     */   public static void atIndex(int[] array, int index, int value)
/*  117:     */   {
/*  118: 223 */     int i = calculateIndex(array, index);
/*  119:     */     
/*  120: 225 */     array[i] = value;
/*  121:     */   }
/*  122:     */   
/*  123:     */   @Universal
/*  124:     */   public static int[] slc(int[] array, int startIndex, int endIndex)
/*  125:     */   {
/*  126: 238 */     int start = calculateIndex(array, startIndex);
/*  127: 239 */     int end = calculateEndIndex(array, endIndex);
/*  128: 240 */     int newLength = end - start;
/*  129: 242 */     if (newLength < 0) {
/*  130: 243 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  131:     */     }
/*  132: 249 */     int[] newArray = new int[newLength];
/*  133: 250 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  134: 251 */     return newArray;
/*  135:     */   }
/*  136:     */   
/*  137:     */   @Universal
/*  138:     */   public static int[] sliceOf(int[] array, int startIndex, int endIndex)
/*  139:     */   {
/*  140: 265 */     int start = calculateIndex(array, startIndex);
/*  141: 266 */     int end = calculateEndIndex(array, endIndex);
/*  142: 267 */     int newLength = end - start;
/*  143: 269 */     if (newLength < 0) {
/*  144: 270 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  145:     */     }
/*  146: 276 */     int[] newArray = new int[newLength];
/*  147: 277 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  148: 278 */     return newArray;
/*  149:     */   }
/*  150:     */   
/*  151:     */   @Universal
/*  152:     */   public static int[] slc(int[] array, int startIndex)
/*  153:     */   {
/*  154: 291 */     int start = calculateIndex(array, startIndex);
/*  155: 292 */     int newLength = array.length - start;
/*  156: 294 */     if (newLength < 0) {
/*  157: 295 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  158:     */     }
/*  159: 301 */     int[] newArray = new int[newLength];
/*  160: 302 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  161: 303 */     return newArray;
/*  162:     */   }
/*  163:     */   
/*  164:     */   @Universal
/*  165:     */   public static int[] sliceOf(int[] array, int startIndex)
/*  166:     */   {
/*  167: 316 */     int start = calculateIndex(array, startIndex);
/*  168: 317 */     int newLength = array.length - start;
/*  169: 319 */     if (newLength < 0) {
/*  170: 320 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  171:     */     }
/*  172: 326 */     int[] newArray = new int[newLength];
/*  173: 327 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  174: 328 */     return newArray;
/*  175:     */   }
/*  176:     */   
/*  177:     */   @Universal
/*  178:     */   public static int[] slcEnd(int[] array, int endIndex)
/*  179:     */   {
/*  180: 341 */     int end = calculateEndIndex(array, endIndex);
/*  181: 342 */     int newLength = end;
/*  182: 344 */     if (newLength < 0) {
/*  183: 345 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  184:     */     }
/*  185: 351 */     int[] newArray = new int[newLength];
/*  186: 352 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  187: 353 */     return newArray;
/*  188:     */   }
/*  189:     */   
/*  190:     */   @Universal
/*  191:     */   public static int[] endSliceOf(int[] array, int endIndex)
/*  192:     */   {
/*  193: 366 */     int end = calculateEndIndex(array, endIndex);
/*  194: 367 */     int newLength = end;
/*  195: 369 */     if (newLength < 0) {
/*  196: 370 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  197:     */     }
/*  198: 376 */     int[] newArray = new int[newLength];
/*  199: 377 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  200: 378 */     return newArray;
/*  201:     */   }
/*  202:     */   
/*  203:     */   @Universal
/*  204:     */   public static boolean in(int value, int[] array)
/*  205:     */   {
/*  206: 389 */     for (int currentValue : array) {
/*  207: 390 */       if (currentValue == value) {
/*  208: 391 */         return true;
/*  209:     */       }
/*  210:     */     }
/*  211: 394 */     return false;
/*  212:     */   }
/*  213:     */   
/*  214:     */   @Universal
/*  215:     */   public static int[] copy(int[] array)
/*  216:     */   {
/*  217: 405 */     int[] newArray = new int[array.length];
/*  218: 406 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  219: 407 */     return newArray;
/*  220:     */   }
/*  221:     */   
/*  222:     */   @Universal
/*  223:     */   public static int[] add(int[] array, int v)
/*  224:     */   {
/*  225: 419 */     int[] newArray = new int[array.length + 1];
/*  226: 420 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  227: 421 */     newArray[array.length] = v;
/*  228: 422 */     return newArray;
/*  229:     */   }
/*  230:     */   
/*  231:     */   @Universal
/*  232:     */   public static int[] add(int[] array, int[] array2)
/*  233:     */   {
/*  234: 433 */     int[] newArray = new int[array.length + array2.length];
/*  235: 434 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  236: 435 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/*  237: 436 */     return newArray;
/*  238:     */   }
/*  239:     */   
/*  240:     */   @Universal
/*  241:     */   public static int[] insert(int[] array, int idx, int v)
/*  242:     */   {
/*  243: 450 */     if (idx >= array.length) {
/*  244: 451 */       return add(array, v);
/*  245:     */     }
/*  246: 454 */     int index = calculateIndex(array, idx);
/*  247:     */     
/*  248:     */ 
/*  249: 457 */     int[] newArray = new int[array.length + 1];
/*  250: 459 */     if (index != 0) {
/*  251: 462 */       System.arraycopy(array, 0, newArray, 0, index);
/*  252:     */     }
/*  253: 466 */     boolean lastIndex = index == array.length - 1;
/*  254: 467 */     int remainingIndex = array.length - index;
/*  255: 469 */     if (lastIndex) {
/*  256: 472 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  257:     */     } else {
/*  258: 477 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  259:     */     }
/*  260: 481 */     newArray[index] = v;
/*  261: 482 */     return newArray;
/*  262:     */   }
/*  263:     */   
/*  264:     */   @Universal
/*  265:     */   public static int[] insert(int[] array, int fromIndex, int[] values)
/*  266:     */   {
/*  267: 495 */     if (fromIndex >= array.length) {
/*  268: 496 */       return add(array, values);
/*  269:     */     }
/*  270: 499 */     int index = calculateIndex(array, fromIndex);
/*  271:     */     
/*  272:     */ 
/*  273: 502 */     int[] newArray = new int[array.length + values.length];
/*  274: 504 */     if (index != 0) {
/*  275: 507 */       System.arraycopy(array, 0, newArray, 0, index);
/*  276:     */     }
/*  277: 511 */     boolean lastIndex = index == array.length - 1;
/*  278:     */     
/*  279: 513 */     int toIndex = index + values.length;
/*  280: 514 */     int remainingIndex = newArray.length - toIndex;
/*  281: 516 */     if (lastIndex) {
/*  282: 519 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  283:     */     } else {
/*  284: 524 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  285:     */     }
/*  286: 528 */     int i = index;
/*  287: 528 */     for (int j = 0; i < toIndex; j++)
/*  288:     */     {
/*  289: 529 */       newArray[i] = values[j];i++;
/*  290:     */     }
/*  291: 531 */     return newArray;
/*  292:     */   }
/*  293:     */   
/*  294:     */   private static int calculateIndex(int[] array, int originalIndex)
/*  295:     */   {
/*  296: 542 */     int length = array.length;
/*  297:     */     
/*  298:     */ 
/*  299: 545 */     int index = originalIndex;
/*  300: 550 */     if (index < 0) {
/*  301: 551 */       index = length + index;
/*  302:     */     }
/*  303: 562 */     if (index < 0) {
/*  304: 563 */       index = 0;
/*  305:     */     }
/*  306: 565 */     if (index >= length) {
/*  307: 566 */       index = length - 1;
/*  308:     */     }
/*  309: 568 */     return index;
/*  310:     */   }
/*  311:     */   
/*  312:     */   private static int calculateEndIndex(int[] array, int originalIndex)
/*  313:     */   {
/*  314: 578 */     int length = array.length;
/*  315:     */     
/*  316:     */ 
/*  317: 581 */     int index = originalIndex;
/*  318: 586 */     if (index < 0) {
/*  319: 587 */       index = length + index;
/*  320:     */     }
/*  321: 598 */     if (index < 0) {
/*  322: 599 */       index = 0;
/*  323:     */     }
/*  324: 601 */     if (index > length) {
/*  325: 602 */       index = length;
/*  326:     */     }
/*  327: 604 */     return index;
/*  328:     */   }
/*  329:     */   
/*  330:     */   public static long reduceBy(int[] array, ReduceBy reduceBy)
/*  331:     */   {
/*  332: 623 */     long sum = 0L;
/*  333: 624 */     for (int v : array) {
/*  334: 625 */       sum = reduceBy.reduce(sum, v);
/*  335:     */     }
/*  336: 627 */     return sum;
/*  337:     */   }
/*  338:     */   
/*  339:     */   public static long reduceBy(int[] array, int start, int length, ReduceBy reduceBy)
/*  340:     */   {
/*  341: 641 */     long sum = 0L;
/*  342: 643 */     for (int index = start; index < length; index++)
/*  343:     */     {
/*  344: 644 */       int v = array[index];
/*  345: 645 */       sum = reduceBy.reduce(sum, v);
/*  346:     */     }
/*  347: 647 */     return sum;
/*  348:     */   }
/*  349:     */   
/*  350:     */   public static long reduceBy(int[] array, int length, ReduceBy reduceBy)
/*  351:     */   {
/*  352: 661 */     long sum = 0L;
/*  353: 663 */     for (int index = 0; index < length; index++)
/*  354:     */     {
/*  355: 664 */       int v = array[index];
/*  356: 665 */       sum = reduceBy.reduce(sum, v);
/*  357:     */     }
/*  358: 667 */     return sum;
/*  359:     */   }
/*  360:     */   
/*  361:     */   public static <T> long reduceBy(int[] array, T object)
/*  362:     */   {
/*  363: 681 */     if (object.getClass().isAnonymousClass()) {
/*  364: 682 */       return reduceByR(array, object);
/*  365:     */     }
/*  366:     */     try
/*  367:     */     {
/*  368: 687 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  369: 688 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  370:     */       try
/*  371:     */       {
/*  372: 691 */         long sum = 0L;
/*  373: 692 */         for (int v : array) {
/*  374: 693 */           sum = methodHandle.invokeExact(sum, v);
/*  375:     */         }
/*  376: 696 */         return sum;
/*  377:     */       }
/*  378:     */       catch (Throwable throwable)
/*  379:     */       {
/*  380: 698 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  381:     */       }
/*  382: 701 */       return reduceByR(array, object);
/*  383:     */     }
/*  384:     */     catch (Exception ex) {}
/*  385:     */   }
/*  386:     */   
/*  387:     */   public static <T> long reduceBy(int[] array, T object, String methodName)
/*  388:     */   {
/*  389: 719 */     if (object.getClass().isAnonymousClass()) {
/*  390: 720 */       return reduceByR(array, object, methodName);
/*  391:     */     }
/*  392:     */     try
/*  393:     */     {
/*  394: 724 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
/*  395: 725 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  396:     */       try
/*  397:     */       {
/*  398: 728 */         long sum = 0L;
/*  399: 729 */         for (int v : array) {
/*  400: 730 */           sum = methodHandle.invokeExact(sum, v);
/*  401:     */         }
/*  402: 733 */         return sum;
/*  403:     */       }
/*  404:     */       catch (Throwable throwable)
/*  405:     */       {
/*  406: 735 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  407:     */       }
/*  408: 738 */       return reduceByR(array, object, methodName);
/*  409:     */     }
/*  410:     */     catch (Exception ex) {}
/*  411:     */   }
/*  412:     */   
/*  413:     */   private static <T> long reduceByR(int[] array, T object)
/*  414:     */   {
/*  415:     */     try
/*  416:     */     {
/*  417: 755 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  418:     */       
/*  419:     */ 
/*  420: 758 */       long sum = 0L;
/*  421: 759 */       for (int v : array) {
/*  422: 760 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Integer.valueOf(v) })).longValue();
/*  423:     */       }
/*  424: 763 */       return sum;
/*  425:     */     }
/*  426:     */     catch (Throwable throwable)
/*  427:     */     {
/*  428: 766 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  429:     */     }
/*  430:     */   }
/*  431:     */   
/*  432:     */   private static <T> long reduceByR(int[] array, T object, String methodName)
/*  433:     */   {
/*  434:     */     try
/*  435:     */     {
/*  436: 783 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  437:     */       
/*  438:     */ 
/*  439: 786 */       long sum = 0L;
/*  440: 787 */       for (int v : array) {
/*  441: 788 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Integer.valueOf(v) })).longValue();
/*  442:     */       }
/*  443: 791 */       return sum;
/*  444:     */     }
/*  445:     */     catch (Throwable throwable)
/*  446:     */     {
/*  447: 794 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  448:     */     }
/*  449:     */   }
/*  450:     */   
/*  451:     */   private static <T> long reduceByR(int[] array, int length, T object, String methodName)
/*  452:     */   {
/*  453:     */     try
/*  454:     */     {
/*  455: 812 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  456:     */       
/*  457:     */ 
/*  458: 815 */       long sum = 0L;
/*  459: 816 */       for (int index = 0; index < length; index++)
/*  460:     */       {
/*  461: 817 */         int v = array[index];
/*  462: 818 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Integer.valueOf(v) })).longValue();
/*  463:     */       }
/*  464: 821 */       return sum;
/*  465:     */     }
/*  466:     */     catch (Throwable throwable)
/*  467:     */     {
/*  468: 824 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  469:     */     }
/*  470:     */   }
/*  471:     */   
/*  472:     */   private static <T> long reduceByR(int[] array, int length, T object)
/*  473:     */   {
/*  474:     */     try
/*  475:     */     {
/*  476: 840 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  477:     */       
/*  478:     */ 
/*  479: 843 */       long sum = 0L;
/*  480: 844 */       for (int index = 0; index < length; index++)
/*  481:     */       {
/*  482: 845 */         int v = array[index];
/*  483: 846 */         sum = ((Long)method.invoke(object, new Object[] { Long.valueOf(sum), Integer.valueOf(v) })).longValue();
/*  484:     */       }
/*  485: 849 */       return sum;
/*  486:     */     }
/*  487:     */     catch (Throwable throwable)
/*  488:     */     {
/*  489: 852 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  490:     */     }
/*  491:     */   }
/*  492:     */   
/*  493:     */   public static long reduceBy(int[] array, int length, Object object)
/*  494:     */   {
/*  495: 868 */     if (object.getClass().isAnonymousClass()) {
/*  496: 869 */       return reduceByR(array, length, object);
/*  497:     */     }
/*  498:     */     try
/*  499:     */     {
/*  500: 873 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  501: 874 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  502:     */       try
/*  503:     */       {
/*  504: 877 */         long sum = 0L;
/*  505: 878 */         for (int index = 0; index < length; index++)
/*  506:     */         {
/*  507: 879 */           int v = array[index];
/*  508: 880 */           sum = methodHandle.invokeExact(sum, v);
/*  509:     */         }
/*  510: 883 */         return sum;
/*  511:     */       }
/*  512:     */       catch (Throwable throwable)
/*  513:     */       {
/*  514: 885 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  515:     */       }
/*  516: 888 */       return reduceByR(array, length, object);
/*  517:     */     }
/*  518:     */     catch (Exception ex) {}
/*  519:     */   }
/*  520:     */   
/*  521:     */   public static long reduceBy(int[] array, int length, Object function, String functionName)
/*  522:     */   {
/*  523: 908 */     if (function.getClass().isAnonymousClass()) {
/*  524: 909 */       return reduceByR(array, length, function, functionName);
/*  525:     */     }
/*  526:     */     try
/*  527:     */     {
/*  528: 913 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(function, functionName);
/*  529: 914 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  530:     */       try
/*  531:     */       {
/*  532: 917 */         long sum = 0L;
/*  533: 918 */         for (int index = 0; index < length; index++)
/*  534:     */         {
/*  535: 919 */           int v = array[index];
/*  536: 920 */           sum = methodHandle.invokeExact(sum, v);
/*  537:     */         }
/*  538: 923 */         return sum;
/*  539:     */       }
/*  540:     */       catch (Throwable throwable)
/*  541:     */       {
/*  542: 925 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  543:     */       }
/*  544: 928 */       return reduceByR(array, length, function, functionName);
/*  545:     */     }
/*  546:     */     catch (Exception ex) {}
/*  547:     */   }
/*  548:     */   
/*  549:     */   public static long reduceBy(int[] array, int start, int length, Object object)
/*  550:     */   {
/*  551: 946 */     if (object.getClass().isAnonymousClass()) {
/*  552: 947 */       return reduceByR(array, object);
/*  553:     */     }
/*  554:     */     try
/*  555:     */     {
/*  556: 951 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  557: 952 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  558:     */       try
/*  559:     */       {
/*  560: 955 */         long sum = 0L;
/*  561: 956 */         for (int index = start; index < length; index++)
/*  562:     */         {
/*  563: 957 */           int v = array[index];
/*  564: 958 */           sum = methodHandle.invokeExact(sum, v);
/*  565:     */         }
/*  566: 961 */         return sum;
/*  567:     */       }
/*  568:     */       catch (Throwable throwable)
/*  569:     */       {
/*  570: 963 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  571:     */       }
/*  572: 966 */       return reduceByR(array, object);
/*  573:     */     }
/*  574:     */     catch (Exception ex) {}
/*  575:     */   }
/*  576:     */   
/*  577:     */   public static boolean equalsOrDie(int expected, int got)
/*  578:     */   {
/*  579: 978 */     if (expected != got) {
/*  580: 979 */       return ((Boolean)Exceptions.die(Boolean.class, new Object[] { "Expected was", Integer.valueOf(expected), "but we got ", Integer.valueOf(got) })).booleanValue();
/*  581:     */     }
/*  582: 981 */     return true;
/*  583:     */   }
/*  584:     */   
/*  585:     */   public static boolean equalsOrDie(int[] expected, int[] got)
/*  586:     */   {
/*  587: 992 */     if (expected.length != got.length) {
/*  588: 993 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/*  589:     */     }
/*  590: 997 */     for (int index = 0; index < expected.length; index++) {
/*  591: 998 */       if (expected[index] != got[index]) {
/*  592: 999 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Integer.valueOf(expected[index]), "but got", Integer.valueOf(got[index]) });
/*  593:     */       }
/*  594:     */     }
/*  595:1005 */     return true;
/*  596:     */   }
/*  597:     */   
/*  598:     */   public static boolean equals(int expected, int got)
/*  599:     */   {
/*  600:1017 */     return expected == got;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public static boolean equals(int[] expected, int[] got)
/*  604:     */   {
/*  605:1029 */     if (expected.length != got.length) {
/*  606:1030 */       return false;
/*  607:     */     }
/*  608:1033 */     for (int index = 0; index < expected.length; index++) {
/*  609:1034 */       if (expected[index] != got[index]) {
/*  610:1035 */         return false;
/*  611:     */       }
/*  612:     */     }
/*  613:1038 */     return true;
/*  614:     */   }
/*  615:     */   
/*  616:     */   public static int sum(int[] values)
/*  617:     */   {
/*  618:1048 */     return sum(values, 0, values.length);
/*  619:     */   }
/*  620:     */   
/*  621:     */   public static int sum(int[] values, int length)
/*  622:     */   {
/*  623:1059 */     return sum(values, 0, length);
/*  624:     */   }
/*  625:     */   
/*  626:     */   public static int sum(int[] values, int start, int length)
/*  627:     */   {
/*  628:1069 */     long sum = 0L;
/*  629:1070 */     for (int index = start; index < length; index++) {
/*  630:1071 */       sum += values[index];
/*  631:     */     }
/*  632:1074 */     if (sum < -2147483648L) {
/*  633:1075 */       Exceptions.die(new Object[] { "overflow the sum is too small", Long.valueOf(sum) });
/*  634:     */     }
/*  635:1079 */     if (sum > 2147483647L) {
/*  636:1080 */       Exceptions.die(new Object[] { "overflow the sum is too big", Long.valueOf(sum) });
/*  637:     */     }
/*  638:1083 */     return (int)sum;
/*  639:     */   }
/*  640:     */   
/*  641:     */   public static long bigSum(int[] values)
/*  642:     */   {
/*  643:1097 */     return bigSum(values, 0, values.length);
/*  644:     */   }
/*  645:     */   
/*  646:     */   public static long bigSum(int[] values, int length)
/*  647:     */   {
/*  648:1108 */     return bigSum(values, 0, length);
/*  649:     */   }
/*  650:     */   
/*  651:     */   public static long bigSum(int[] values, int start, int length)
/*  652:     */   {
/*  653:1117 */     long sum = 0L;
/*  654:1118 */     for (int index = start; index < length; index++) {
/*  655:1119 */       sum += values[index];
/*  656:     */     }
/*  657:1122 */     return sum;
/*  658:     */   }
/*  659:     */   
/*  660:     */   public static int max(int[] values, int start, int length)
/*  661:     */   {
/*  662:1135 */     int max = -2147483648;
/*  663:1136 */     for (int index = start; index < length; index++) {
/*  664:1137 */       if (values[index] > max) {
/*  665:1138 */         max = values[index];
/*  666:     */       }
/*  667:     */     }
/*  668:1142 */     return max;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public static int max(int[] values)
/*  672:     */   {
/*  673:1152 */     return max(values, 0, values.length);
/*  674:     */   }
/*  675:     */   
/*  676:     */   public static int max(int[] values, int length)
/*  677:     */   {
/*  678:1162 */     return max(values, 0, length);
/*  679:     */   }
/*  680:     */   
/*  681:     */   public static int min(int[] values, int start, int length)
/*  682:     */   {
/*  683:1172 */     int min = 2147483647;
/*  684:1173 */     for (int index = start; index < length; index++) {
/*  685:1174 */       if (values[index] < min) {
/*  686:1174 */         min = values[index];
/*  687:     */       }
/*  688:     */     }
/*  689:1176 */     return min;
/*  690:     */   }
/*  691:     */   
/*  692:     */   public static int min(int[] values)
/*  693:     */   {
/*  694:1186 */     return min(values, 0, values.length);
/*  695:     */   }
/*  696:     */   
/*  697:     */   public static int min(int[] values, int length)
/*  698:     */   {
/*  699:1196 */     return min(values, 0, length);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public static int mean(int[] values, int start, int length)
/*  703:     */   {
/*  704:1208 */     return (int)Math.round(meanDouble(values, start, length));
/*  705:     */   }
/*  706:     */   
/*  707:     */   public static int mean(int[] values, int length)
/*  708:     */   {
/*  709:1220 */     return (int)Math.round(meanDouble(values, 0, length));
/*  710:     */   }
/*  711:     */   
/*  712:     */   public static int mean(int[] values)
/*  713:     */   {
/*  714:1231 */     return (int)Math.round(meanDouble(values, 0, values.length));
/*  715:     */   }
/*  716:     */   
/*  717:     */   public static int variance(int[] values, int start, int length)
/*  718:     */   {
/*  719:1245 */     return (int)Math.round(varianceDouble(values, start, length));
/*  720:     */   }
/*  721:     */   
/*  722:     */   private static double meanDouble(int[] values, int start, int length)
/*  723:     */   {
/*  724:1250 */     double mean = bigSum(values, start, length) / length;
/*  725:1251 */     return mean;
/*  726:     */   }
/*  727:     */   
/*  728:     */   public static double varianceDouble(int[] values, int start, int length)
/*  729:     */   {
/*  730:1265 */     double mean = meanDouble(values, start, length);
/*  731:1266 */     double temp = 0.0D;
/*  732:1267 */     for (int index = start; index < length; index++)
/*  733:     */     {
/*  734:1268 */       double a = values[index];
/*  735:1269 */       temp += (mean - a) * (mean - a);
/*  736:     */     }
/*  737:1271 */     return temp / length;
/*  738:     */   }
/*  739:     */   
/*  740:     */   public static int variance(int[] values, int length)
/*  741:     */   {
/*  742:1283 */     return (int)Math.round(varianceDouble(values, 0, length));
/*  743:     */   }
/*  744:     */   
/*  745:     */   public static int variance(int[] values)
/*  746:     */   {
/*  747:1294 */     return (int)Math.round(varianceDouble(values, 0, values.length));
/*  748:     */   }
/*  749:     */   
/*  750:     */   public static int standardDeviation(int[] values, int start, int length)
/*  751:     */   {
/*  752:1307 */     double variance = varianceDouble(values, start, length);
/*  753:1308 */     return (int)Math.round(Math.sqrt(variance));
/*  754:     */   }
/*  755:     */   
/*  756:     */   public static int standardDeviation(int[] values, int length)
/*  757:     */   {
/*  758:1320 */     double variance = varianceDouble(values, 0, length);
/*  759:1321 */     return (int)Math.round(Math.sqrt(variance));
/*  760:     */   }
/*  761:     */   
/*  762:     */   public static int standardDeviation(int[] values)
/*  763:     */   {
/*  764:1332 */     double variance = varianceDouble(values, 0, values.length);
/*  765:1333 */     return (int)Math.round(Math.sqrt(variance));
/*  766:     */   }
/*  767:     */   
/*  768:     */   public static int median(int[] values, int start, int length)
/*  769:     */   {
/*  770:1346 */     int[] sorted = new int[length];
/*  771:1347 */     System.arraycopy(values, start, sorted, 0, length);
/*  772:1348 */     Arrays.sort(sorted);
/*  773:1350 */     if (length % 2 == 0)
/*  774:     */     {
/*  775:1351 */       int middle = sorted.length / 2;
/*  776:1352 */       double median = (sorted[(middle - 1)] + sorted[middle]) / 2.0D;
/*  777:1353 */       return (int)Math.round(median);
/*  778:     */     }
/*  779:1355 */     return sorted[(sorted.length / 2)];
/*  780:     */   }
/*  781:     */   
/*  782:     */   public static int median(int[] values, int length)
/*  783:     */   {
/*  784:1367 */     return median(values, 0, length);
/*  785:     */   }
/*  786:     */   
/*  787:     */   public static int median(int[] values)
/*  788:     */   {
/*  789:1377 */     return median(values, 0, values.length);
/*  790:     */   }
/*  791:     */   
/*  792:     */   public static boolean equals(int start, int end, int[] expected, int[] got)
/*  793:     */   {
/*  794:1390 */     if (expected.length != got.length) {
/*  795:1391 */       return false;
/*  796:     */     }
/*  797:1394 */     for (int index = start; index < end; index++) {
/*  798:1395 */       if (expected[index] != got[index]) {
/*  799:1396 */         return false;
/*  800:     */       }
/*  801:     */     }
/*  802:1399 */     return true;
/*  803:     */   }
/*  804:     */   
/*  805:     */   public static int hashCode(int[] array)
/*  806:     */   {
/*  807:1403 */     if (array == null) {
/*  808:1404 */       return 0;
/*  809:     */     }
/*  810:1406 */     int result = 1;
/*  811:1407 */     for (int element : array) {
/*  812:1409 */       result = 31 * result + element;
/*  813:     */     }
/*  814:1412 */     return result;
/*  815:     */   }
/*  816:     */   
/*  817:     */   public static int hashCode(int start, int end, int[] array)
/*  818:     */   {
/*  819:1416 */     if (array == null) {
/*  820:1417 */       return 0;
/*  821:     */     }
/*  822:1419 */     int result = 1;
/*  823:1421 */     for (int index = start; index < end; index++) {
/*  824:1423 */       result = 31 * result + array[index];
/*  825:     */     }
/*  826:1426 */     return result;
/*  827:     */   }
/*  828:     */   
/*  829:     */   public static long sum(Collection<?> inputList, String propertyPath)
/*  830:     */   {
/*  831:1437 */     if (inputList.size() == 0) {
/*  832:1438 */       return 0L;
/*  833:     */     }
/*  834:1441 */     long sum = 0L;
/*  835:     */     String[] properties;
/*  836:     */     FieldAccess fieldAccess;
/*  837:1443 */     if ((propertyPath.contains(".")) || (propertyPath.contains("[")))
/*  838:     */     {
/*  839:1445 */       properties = StringScanner.splitByDelimiters(propertyPath, ".[]");
/*  840:1447 */       for (Object o : inputList) {
/*  841:1448 */         sum += BeanUtils.getPropertyInt(o, properties);
/*  842:     */       }
/*  843:     */     }
/*  844:     */     else
/*  845:     */     {
/*  846:1453 */       Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(inputList.iterator().next());
/*  847:1454 */       fieldAccess = (FieldAccess)fields.get(propertyPath);
/*  848:1455 */       for (Object o : inputList) {
/*  849:1456 */         sum += fieldAccess.getInt(o);
/*  850:     */       }
/*  851:     */     }
/*  852:1460 */     return sum;
/*  853:     */   }
/*  854:     */   
/*  855:     */   private static double mean(Collection<?> inputList, String propertyPath)
/*  856:     */   {
/*  857:1464 */     double mean = sum(inputList, propertyPath) / inputList.size();
/*  858:1465 */     return Math.round(mean);
/*  859:     */   }
/*  860:     */   
/*  861:     */   public static double variance(Collection<?> inputList, String propertyPath)
/*  862:     */   {
/*  863:1470 */     double mean = mean(inputList, propertyPath);
/*  864:1471 */     double temp = 0.0D;
/*  865:     */     String[] properties;
/*  866:     */     FieldAccess fieldAccess;
/*  867:1476 */     if ((propertyPath.contains(".")) || (propertyPath.contains("[")))
/*  868:     */     {
/*  869:1478 */       properties = StringScanner.splitByDelimiters(propertyPath, ".[]");
/*  870:1480 */       for (Object o : inputList)
/*  871:     */       {
/*  872:1481 */         double a = BeanUtils.getPropertyInt(o, properties);
/*  873:1482 */         temp += (mean - a) * (mean - a);
/*  874:     */       }
/*  875:     */     }
/*  876:     */     else
/*  877:     */     {
/*  878:1487 */       Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject(inputList.iterator().next());
/*  879:1488 */       fieldAccess = (FieldAccess)fields.get(propertyPath);
/*  880:1489 */       for (Object o : inputList)
/*  881:     */       {
/*  882:1490 */         double a = fieldAccess.getInt(o);
/*  883:1491 */         temp += (mean - a) * (mean - a);
/*  884:     */       }
/*  885:     */     }
/*  886:1495 */     return Math.round(temp / inputList.size());
/*  887:     */   }
/*  888:     */   
/*  889:     */   public static double standardDeviation(Collection<?> inputList, String propertyPath)
/*  890:     */   {
/*  891:1507 */     double variance = variance(inputList, propertyPath);
/*  892:1508 */     return Math.round(Math.sqrt(variance));
/*  893:     */   }
/*  894:     */   
/*  895:     */   public static int median(Collection<?> inputList, String propertyPath)
/*  896:     */   {
/*  897:1517 */     return IntList.toIntList(inputList, propertyPath).median();
/*  898:     */   }
/*  899:     */   
/*  900:     */   public static int roundUpToPowerOf2(int number)
/*  901:     */   {
/*  902:     */     int rounded;
/*  903:1527 */     int rounded = (rounded = Integer.highestOneBit(number)) != 0 ? rounded : Integer.bitCount(number) > 1 ? rounded << 1 : number >= 1000 ? 1000 : 1;
/*  904:     */     
/*  905:     */ 
/*  906:     */ 
/*  907:     */ 
/*  908:     */ 
/*  909:1533 */     return rounded;
/*  910:     */   }
/*  911:     */   
/*  912:     */   public static abstract interface ReduceBy
/*  913:     */   {
/*  914:     */     public abstract long reduce(long paramLong, int paramInt);
/*  915:     */   }
/*  916:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Int
 * JD-Core Version:    0.7.0.1
 */