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
/*   11:     */ public class Dbl
/*   12:     */ {
/*   13:     */   public static double[] grow(double[] array, int size)
/*   14:     */   {
/*   15:  48 */     Exceptions.requireNonNull(array);
/*   16:     */     
/*   17:  50 */     double[] newArray = new double[array.length + size];
/*   18:  51 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   19:  52 */     return newArray;
/*   20:     */   }
/*   21:     */   
/*   22:     */   public static double[] grow(double[] array)
/*   23:     */   {
/*   24:  57 */     Exceptions.requireNonNull(array);
/*   25:     */     
/*   26:  59 */     double[] newArray = new double[array.length * 2];
/*   27:  60 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   28:  61 */     return newArray;
/*   29:     */   }
/*   30:     */   
/*   31:     */   public static double[] shrink(double[] array, int size)
/*   32:     */   {
/*   33:  66 */     Exceptions.requireNonNull(array);
/*   34:     */     
/*   35:  68 */     double[] newArray = new double[array.length - size];
/*   36:     */     
/*   37:  70 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*   38:  71 */     return newArray;
/*   39:     */   }
/*   40:     */   
/*   41:     */   public static double[] compact(double[] array)
/*   42:     */   {
/*   43:  76 */     Exceptions.requireNonNull(array);
/*   44:     */     
/*   45:  78 */     int nullCount = 0;
/*   46:  79 */     for (double ch : array) {
/*   47:  81 */       if (ch == 0.0D) {
/*   48:  82 */         nullCount++;
/*   49:     */       }
/*   50:     */     }
/*   51:  85 */     double[] newArray = new double[array.length - nullCount];
/*   52:     */     
/*   53:  87 */     int j = 0;
/*   54:  88 */     for (double ch : array) {
/*   55:  90 */       if (ch != 0.0D)
/*   56:     */       {
/*   57:  94 */         newArray[j] = ch;
/*   58:  95 */         j++;
/*   59:     */       }
/*   60:     */     }
/*   61:  97 */     return newArray;
/*   62:     */   }
/*   63:     */   
/*   64:     */   public static double[] arrayOfDouble(int size)
/*   65:     */   {
/*   66: 108 */     return new double[size];
/*   67:     */   }
/*   68:     */   
/*   69:     */   @Universal
/*   70:     */   public static double[] array(double... array)
/*   71:     */   {
/*   72: 117 */     Exceptions.requireNonNull(array);
/*   73: 118 */     return array;
/*   74:     */   }
/*   75:     */   
/*   76:     */   @Universal
/*   77:     */   public static int len(double[] array)
/*   78:     */   {
/*   79: 124 */     return array.length;
/*   80:     */   }
/*   81:     */   
/*   82:     */   @Universal
/*   83:     */   public static double idx(double[] array, int index)
/*   84:     */   {
/*   85: 130 */     int i = calculateIndex(array, index);
/*   86:     */     
/*   87: 132 */     return array[i];
/*   88:     */   }
/*   89:     */   
/*   90:     */   @Universal
/*   91:     */   public static void idx(double[] array, int index, double value)
/*   92:     */   {
/*   93: 138 */     int i = calculateIndex(array, index);
/*   94:     */     
/*   95: 140 */     array[i] = value;
/*   96:     */   }
/*   97:     */   
/*   98:     */   @Universal
/*   99:     */   public static double[] sliceOf(double[] array, int startIndex, int endIndex)
/*  100:     */   {
/*  101: 145 */     return slc(array, startIndex, endIndex);
/*  102:     */   }
/*  103:     */   
/*  104:     */   @Universal
/*  105:     */   public static double[] slc(double[] array, int startIndex, int endIndex)
/*  106:     */   {
/*  107: 151 */     int start = calculateIndex(array, startIndex);
/*  108: 152 */     int end = calculateEndIndex(array, endIndex);
/*  109: 153 */     int newLength = end - start;
/*  110: 155 */     if (newLength < 0) {
/*  111: 156 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  112:     */     }
/*  113: 162 */     double[] newArray = new double[newLength];
/*  114: 163 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  115: 164 */     return newArray;
/*  116:     */   }
/*  117:     */   
/*  118:     */   @Universal
/*  119:     */   public static double[] slc(double[] array, int startIndex)
/*  120:     */   {
/*  121: 170 */     int start = calculateIndex(array, startIndex);
/*  122: 171 */     int newLength = array.length - start;
/*  123: 173 */     if (newLength < 0) {
/*  124: 174 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  125:     */     }
/*  126: 180 */     double[] newArray = new double[newLength];
/*  127: 181 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  128: 182 */     return newArray;
/*  129:     */   }
/*  130:     */   
/*  131:     */   @Universal
/*  132:     */   public static double[] endSliceOf(double[] array, int endIndex)
/*  133:     */   {
/*  134: 188 */     return slcEnd(array, endIndex);
/*  135:     */   }
/*  136:     */   
/*  137:     */   @Universal
/*  138:     */   public static double[] slcEnd(double[] array, int endIndex)
/*  139:     */   {
/*  140: 195 */     int end = calculateEndIndex(array, endIndex);
/*  141: 196 */     int newLength = end;
/*  142: 198 */     if (newLength < 0) {
/*  143: 199 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  144:     */     }
/*  145: 205 */     double[] newArray = new double[newLength];
/*  146: 206 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  147: 207 */     return newArray;
/*  148:     */   }
/*  149:     */   
/*  150:     */   @Universal
/*  151:     */   public static boolean in(double value, double[] array)
/*  152:     */   {
/*  153: 212 */     for (double currentValue : array) {
/*  154: 213 */       if (currentValue == value) {
/*  155: 214 */         return true;
/*  156:     */       }
/*  157:     */     }
/*  158: 217 */     return false;
/*  159:     */   }
/*  160:     */   
/*  161:     */   @Universal
/*  162:     */   public static double[] copy(double[] array)
/*  163:     */   {
/*  164: 223 */     double[] newArray = new double[array.length];
/*  165: 224 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  166: 225 */     return newArray;
/*  167:     */   }
/*  168:     */   
/*  169:     */   @Universal
/*  170:     */   public static double[] add(double[] array, double v)
/*  171:     */   {
/*  172: 231 */     double[] newArray = new double[array.length + 1];
/*  173: 232 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  174: 233 */     newArray[array.length] = v;
/*  175: 234 */     return newArray;
/*  176:     */   }
/*  177:     */   
/*  178:     */   @Universal
/*  179:     */   public static double[] add(double[] array, double[] array2)
/*  180:     */   {
/*  181: 239 */     double[] newArray = new double[array.length + array2.length];
/*  182: 240 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  183: 241 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/*  184: 242 */     return newArray;
/*  185:     */   }
/*  186:     */   
/*  187:     */   @Universal
/*  188:     */   public static double[] insert(double[] array, int idx, double v)
/*  189:     */   {
/*  190: 248 */     Exceptions.requireNonNull(array);
/*  191: 250 */     if (idx >= array.length) {
/*  192: 251 */       return add(array, v);
/*  193:     */     }
/*  194: 254 */     int index = calculateIndex(array, idx);
/*  195:     */     
/*  196:     */ 
/*  197: 257 */     double[] newArray = new double[array.length + 1];
/*  198: 259 */     if (index != 0) {
/*  199: 262 */       System.arraycopy(array, 0, newArray, 0, index);
/*  200:     */     }
/*  201: 266 */     boolean lastIndex = index == array.length - 1;
/*  202: 267 */     int remainingIndex = array.length - index;
/*  203: 269 */     if (lastIndex) {
/*  204: 272 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  205:     */     } else {
/*  206: 277 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  207:     */     }
/*  208: 281 */     newArray[index] = v;
/*  209: 282 */     return newArray;
/*  210:     */   }
/*  211:     */   
/*  212:     */   @Universal
/*  213:     */   public static double[] insert(double[] array, int fromIndex, double[] values)
/*  214:     */   {
/*  215: 288 */     if (fromIndex >= array.length) {
/*  216: 289 */       return add(array, values);
/*  217:     */     }
/*  218: 292 */     int index = calculateIndex(array, fromIndex);
/*  219:     */     
/*  220:     */ 
/*  221: 295 */     double[] newArray = new double[array.length + values.length];
/*  222: 297 */     if (index != 0) {
/*  223: 300 */       System.arraycopy(array, 0, newArray, 0, index);
/*  224:     */     }
/*  225: 304 */     boolean lastIndex = index == array.length - 1;
/*  226:     */     
/*  227: 306 */     int toIndex = index + values.length;
/*  228: 307 */     int remainingIndex = newArray.length - toIndex;
/*  229: 309 */     if (lastIndex) {
/*  230: 312 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  231:     */     } else {
/*  232: 317 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  233:     */     }
/*  234: 321 */     int i = index;
/*  235: 321 */     for (int j = 0; i < toIndex; j++)
/*  236:     */     {
/*  237: 322 */       newArray[i] = values[j];i++;
/*  238:     */     }
/*  239: 324 */     return newArray;
/*  240:     */   }
/*  241:     */   
/*  242:     */   private static int calculateIndex(double[] array, int originalIndex)
/*  243:     */   {
/*  244: 330 */     int length = array.length;
/*  245:     */     
/*  246: 332 */     int index = originalIndex;
/*  247: 337 */     if (index < 0) {
/*  248: 338 */       index = length + index;
/*  249:     */     }
/*  250: 349 */     if (index < 0) {
/*  251: 350 */       index = 0;
/*  252:     */     }
/*  253: 352 */     if (index >= length) {
/*  254: 353 */       index = length - 1;
/*  255:     */     }
/*  256: 355 */     return index;
/*  257:     */   }
/*  258:     */   
/*  259:     */   private static int calculateEndIndex(double[] array, int originalIndex)
/*  260:     */   {
/*  261: 361 */     int length = array.length;
/*  262:     */     
/*  263: 363 */     Exceptions.requireNonNull(array, "array cannot be null");
/*  264:     */     
/*  265:     */ 
/*  266: 366 */     int index = originalIndex;
/*  267: 371 */     if (index < 0) {
/*  268: 372 */       index = length + index;
/*  269:     */     }
/*  270: 383 */     if (index < 0) {
/*  271: 384 */       index = 0;
/*  272:     */     }
/*  273: 386 */     if (index > length) {
/*  274: 387 */       index = length;
/*  275:     */     }
/*  276: 389 */     return index;
/*  277:     */   }
/*  278:     */   
/*  279:     */   public static boolean equalsOrDie(double[] expected, double[] got)
/*  280:     */   {
/*  281: 401 */     if (expected.length != got.length) {
/*  282: 402 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/*  283:     */     }
/*  284: 406 */     for (int index = 0; index < expected.length; index++) {
/*  285: 407 */       if (expected[index] != got[index]) {
/*  286: 408 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Double.valueOf(expected[index]), "but got", Double.valueOf(got[index]) });
/*  287:     */       }
/*  288:     */     }
/*  289: 414 */     return true;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public static boolean equals(double[] expected, double[] got)
/*  293:     */   {
/*  294: 426 */     if (expected.length != got.length) {
/*  295: 427 */       return false;
/*  296:     */     }
/*  297: 430 */     for (int index = 0; index < expected.length; index++) {
/*  298: 431 */       if (expected[index] != got[index]) {
/*  299: 432 */         return false;
/*  300:     */       }
/*  301:     */     }
/*  302: 435 */     return true;
/*  303:     */   }
/*  304:     */   
/*  305:     */   public static double reduceBy(double[] array, ReduceBy reduceBy)
/*  306:     */   {
/*  307: 455 */     double sum = 0.0D;
/*  308: 456 */     for (double v : array) {
/*  309: 457 */       sum = reduceBy.reduce(sum, v);
/*  310:     */     }
/*  311: 459 */     return sum;
/*  312:     */   }
/*  313:     */   
/*  314:     */   public static double reduceBy(double[] array, int start, int length, ReduceBy reduceBy)
/*  315:     */   {
/*  316: 473 */     double sum = 0.0D;
/*  317: 475 */     for (int index = start; index < length; index++)
/*  318:     */     {
/*  319: 476 */       double v = array[index];
/*  320: 477 */       sum = reduceBy.reduce(sum, v);
/*  321:     */     }
/*  322: 479 */     return sum;
/*  323:     */   }
/*  324:     */   
/*  325:     */   public static double reduceBy(double[] array, int length, ReduceBy reduceBy)
/*  326:     */   {
/*  327: 493 */     double sum = 0.0D;
/*  328: 495 */     for (int index = 0; index < length; index++)
/*  329:     */     {
/*  330: 496 */       double v = array[index];
/*  331: 497 */       sum = reduceBy.reduce(sum, v);
/*  332:     */     }
/*  333: 499 */     return sum;
/*  334:     */   }
/*  335:     */   
/*  336:     */   public static <T> double reduceBy(double[] array, T object)
/*  337:     */   {
/*  338: 513 */     if (object.getClass().isAnonymousClass()) {
/*  339: 514 */       return reduceByR(array, object);
/*  340:     */     }
/*  341:     */     try
/*  342:     */     {
/*  343: 519 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  344: 520 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  345:     */       try
/*  346:     */       {
/*  347: 523 */         double sum = 0.0D;
/*  348: 524 */         for (double v : array) {
/*  349: 525 */           sum = methodHandle.invokeExact(sum, v);
/*  350:     */         }
/*  351: 528 */         return sum;
/*  352:     */       }
/*  353:     */       catch (Throwable throwable)
/*  354:     */       {
/*  355: 530 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  356:     */       }
/*  357: 533 */       return reduceByR(array, object);
/*  358:     */     }
/*  359:     */     catch (Exception ex) {}
/*  360:     */   }
/*  361:     */   
/*  362:     */   public static <T> double reduceBy(double[] array, T object, String methodName)
/*  363:     */   {
/*  364: 551 */     if (object.getClass().isAnonymousClass()) {
/*  365: 552 */       return reduceByR(array, object, methodName);
/*  366:     */     }
/*  367:     */     try
/*  368:     */     {
/*  369: 556 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
/*  370: 557 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  371:     */       try
/*  372:     */       {
/*  373: 560 */         double sum = 0.0D;
/*  374: 561 */         for (double v : array) {
/*  375: 562 */           sum = methodHandle.invokeExact(sum, v);
/*  376:     */         }
/*  377: 565 */         return sum;
/*  378:     */       }
/*  379:     */       catch (Throwable throwable)
/*  380:     */       {
/*  381: 567 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  382:     */       }
/*  383: 570 */       return reduceByR(array, object, methodName);
/*  384:     */     }
/*  385:     */     catch (Exception ex) {}
/*  386:     */   }
/*  387:     */   
/*  388:     */   private static <T> double reduceByR(double[] array, T object)
/*  389:     */   {
/*  390:     */     try
/*  391:     */     {
/*  392: 587 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  393:     */       
/*  394:     */ 
/*  395: 590 */       double sum = 0.0D;
/*  396: 591 */       for (double v : array) {
/*  397: 592 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Double.valueOf(v) })).doubleValue();
/*  398:     */       }
/*  399: 595 */       return sum;
/*  400:     */     }
/*  401:     */     catch (Throwable throwable)
/*  402:     */     {
/*  403: 598 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  404:     */     }
/*  405:     */   }
/*  406:     */   
/*  407:     */   private static <T> double reduceByR(double[] array, T object, String methodName)
/*  408:     */   {
/*  409:     */     try
/*  410:     */     {
/*  411: 615 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  412:     */       
/*  413:     */ 
/*  414: 618 */       double sum = 0.0D;
/*  415: 619 */       for (double v : array) {
/*  416: 620 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Double.valueOf(v) })).doubleValue();
/*  417:     */       }
/*  418: 623 */       return sum;
/*  419:     */     }
/*  420:     */     catch (Throwable throwable)
/*  421:     */     {
/*  422: 626 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  423:     */     }
/*  424:     */   }
/*  425:     */   
/*  426:     */   private static <T> double reduceByR(double[] array, int length, T object, String methodName)
/*  427:     */   {
/*  428:     */     try
/*  429:     */     {
/*  430: 644 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  431:     */       
/*  432:     */ 
/*  433: 647 */       double sum = 0.0D;
/*  434: 648 */       for (int index = 0; index < length; index++)
/*  435:     */       {
/*  436: 649 */         double v = array[index];
/*  437: 650 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Double.valueOf(v) })).doubleValue();
/*  438:     */       }
/*  439: 653 */       return sum;
/*  440:     */     }
/*  441:     */     catch (Throwable throwable)
/*  442:     */     {
/*  443: 656 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  444:     */     }
/*  445:     */   }
/*  446:     */   
/*  447:     */   private static <T> double reduceByR(double[] array, int length, T object)
/*  448:     */   {
/*  449:     */     try
/*  450:     */     {
/*  451: 672 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  452:     */       
/*  453:     */ 
/*  454: 675 */       double sum = 0.0D;
/*  455: 676 */       for (int index = 0; index < length; index++)
/*  456:     */       {
/*  457: 677 */         double v = array[index];
/*  458: 678 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Double.valueOf(v) })).doubleValue();
/*  459:     */       }
/*  460: 681 */       return sum;
/*  461:     */     }
/*  462:     */     catch (Throwable throwable)
/*  463:     */     {
/*  464: 684 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  465:     */     }
/*  466:     */   }
/*  467:     */   
/*  468:     */   public static double reduceBy(double[] array, int length, Object object)
/*  469:     */   {
/*  470: 700 */     if (object.getClass().isAnonymousClass()) {
/*  471: 701 */       return reduceByR(array, length, object);
/*  472:     */     }
/*  473:     */     try
/*  474:     */     {
/*  475: 705 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  476: 706 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  477:     */       try
/*  478:     */       {
/*  479: 709 */         double sum = 0.0D;
/*  480: 710 */         for (int index = 0; index < length; index++)
/*  481:     */         {
/*  482: 711 */           double v = array[index];
/*  483: 712 */           sum = methodHandle.invokeExact(sum, v);
/*  484:     */         }
/*  485: 715 */         return sum;
/*  486:     */       }
/*  487:     */       catch (Throwable throwable)
/*  488:     */       {
/*  489: 717 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  490:     */       }
/*  491: 720 */       return reduceByR(array, length, object);
/*  492:     */     }
/*  493:     */     catch (Exception ex) {}
/*  494:     */   }
/*  495:     */   
/*  496:     */   public static double reduceBy(double[] array, int length, Object function, String functionName)
/*  497:     */   {
/*  498: 740 */     if (function.getClass().isAnonymousClass()) {
/*  499: 741 */       return reduceByR(array, length, function, functionName);
/*  500:     */     }
/*  501:     */     try
/*  502:     */     {
/*  503: 745 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(function, functionName);
/*  504: 746 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  505:     */       try
/*  506:     */       {
/*  507: 749 */         double sum = 0.0D;
/*  508: 750 */         for (int index = 0; index < length; index++)
/*  509:     */         {
/*  510: 751 */           double v = array[index];
/*  511: 752 */           sum = methodHandle.invokeExact(sum, v);
/*  512:     */         }
/*  513: 755 */         return sum;
/*  514:     */       }
/*  515:     */       catch (Throwable throwable)
/*  516:     */       {
/*  517: 757 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  518:     */       }
/*  519: 760 */       return reduceByR(array, length, function, functionName);
/*  520:     */     }
/*  521:     */     catch (Exception ex) {}
/*  522:     */   }
/*  523:     */   
/*  524:     */   public static double reduceBy(double[] array, int start, int length, Object object)
/*  525:     */   {
/*  526: 778 */     if (object.getClass().isAnonymousClass()) {
/*  527: 779 */       return reduceByR(array, object);
/*  528:     */     }
/*  529:     */     try
/*  530:     */     {
/*  531: 783 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  532: 784 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  533:     */       try
/*  534:     */       {
/*  535: 787 */         double sum = 0.0D;
/*  536: 788 */         for (int index = start; index < length; index++)
/*  537:     */         {
/*  538: 789 */           double v = array[index];
/*  539: 790 */           sum = methodHandle.invokeExact(sum, v);
/*  540:     */         }
/*  541: 793 */         return sum;
/*  542:     */       }
/*  543:     */       catch (Throwable throwable)
/*  544:     */       {
/*  545: 795 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  546:     */       }
/*  547: 798 */       return reduceByR(array, object);
/*  548:     */     }
/*  549:     */     catch (Exception ex) {}
/*  550:     */   }
/*  551:     */   
/*  552:     */   public static boolean equalsOrDie(double expected, double got)
/*  553:     */   {
/*  554: 810 */     if (expected != got) {
/*  555: 811 */       return ((Boolean)Exceptions.die(Boolean.class, new Object[] { "Expected was", Double.valueOf(expected), "but we got ", Double.valueOf(got) })).booleanValue();
/*  556:     */     }
/*  557: 813 */     return true;
/*  558:     */   }
/*  559:     */   
/*  560:     */   public static boolean equals(double expected, double got)
/*  561:     */   {
/*  562: 825 */     return expected == got;
/*  563:     */   }
/*  564:     */   
/*  565:     */   public static double sum(double[] values)
/*  566:     */   {
/*  567: 836 */     return sum(values, 0, values.length);
/*  568:     */   }
/*  569:     */   
/*  570:     */   public static double sum(double[] values, int length)
/*  571:     */   {
/*  572: 847 */     return sum(values, 0, length);
/*  573:     */   }
/*  574:     */   
/*  575:     */   public static double sum(double[] values, int start, int length)
/*  576:     */   {
/*  577: 857 */     double sum = 0.0D;
/*  578: 858 */     for (int index = start; index < length; index++) {
/*  579: 859 */       sum += values[index];
/*  580:     */     }
/*  581: 862 */     if (sum < 1.401298464324817E-045D) {
/*  582: 863 */       Exceptions.die(new Object[] { "overflow the sum is too small", Double.valueOf(sum) });
/*  583:     */     }
/*  584: 867 */     if (sum > 3.402823466385289E+038D) {
/*  585: 868 */       Exceptions.die(new Object[] { "overflow the sum is too big", Double.valueOf(sum) });
/*  586:     */     }
/*  587: 871 */     return sum;
/*  588:     */   }
/*  589:     */   
/*  590:     */   public static double max(double[] values, int start, int length)
/*  591:     */   {
/*  592: 887 */     double max = 1.401298464324817E-045D;
/*  593: 888 */     for (int index = start; index < length; index++) {
/*  594: 889 */       if (values[index] > max) {
/*  595: 890 */         max = values[index];
/*  596:     */       }
/*  597:     */     }
/*  598: 894 */     return max;
/*  599:     */   }
/*  600:     */   
/*  601:     */   public static double max(double[] values)
/*  602:     */   {
/*  603: 904 */     return max(values, 0, values.length);
/*  604:     */   }
/*  605:     */   
/*  606:     */   public static double max(double[] values, int length)
/*  607:     */   {
/*  608: 914 */     return max(values, 0, length);
/*  609:     */   }
/*  610:     */   
/*  611:     */   public static double min(double[] values, int start, int length)
/*  612:     */   {
/*  613: 924 */     double min = 3.402823466385289E+038D;
/*  614: 925 */     for (int index = start; index < length; index++) {
/*  615: 926 */       if (values[index] < min) {
/*  616: 926 */         min = values[index];
/*  617:     */       }
/*  618:     */     }
/*  619: 928 */     return min;
/*  620:     */   }
/*  621:     */   
/*  622:     */   public static double min(double[] values)
/*  623:     */   {
/*  624: 938 */     return min(values, 0, values.length);
/*  625:     */   }
/*  626:     */   
/*  627:     */   public static double min(double[] values, int length)
/*  628:     */   {
/*  629: 948 */     return min(values, 0, length);
/*  630:     */   }
/*  631:     */   
/*  632:     */   private static double mean(double[] values, int start, int length)
/*  633:     */   {
/*  634: 960 */     double mean = sum(values, start, length) / length;
/*  635: 961 */     return mean;
/*  636:     */   }
/*  637:     */   
/*  638:     */   public static double mean(double[] values, int length)
/*  639:     */   {
/*  640: 972 */     return mean(values, 0, length);
/*  641:     */   }
/*  642:     */   
/*  643:     */   public static double mean(double[] values)
/*  644:     */   {
/*  645: 985 */     return mean(values, 0, values.length);
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static double variance(double[] values, int start, int length)
/*  649:     */   {
/*  650: 999 */     return varianceDouble(values, start, length);
/*  651:     */   }
/*  652:     */   
/*  653:     */   public static double varianceDouble(double[] values, int start, int length)
/*  654:     */   {
/*  655:1015 */     double mean = mean(values, start, length);
/*  656:1016 */     double temp = 0.0D;
/*  657:1017 */     for (int index = start; index < length; index++)
/*  658:     */     {
/*  659:1018 */       double a = values[index];
/*  660:1019 */       temp += (mean - a) * (mean - a);
/*  661:     */     }
/*  662:1021 */     return temp / length;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public static double variance(double[] values, int length)
/*  666:     */   {
/*  667:1033 */     return varianceDouble(values, 0, length);
/*  668:     */   }
/*  669:     */   
/*  670:     */   public static double variance(double[] values)
/*  671:     */   {
/*  672:1044 */     return varianceDouble(values, 0, values.length);
/*  673:     */   }
/*  674:     */   
/*  675:     */   public static double standardDeviation(double[] values, int start, int length)
/*  676:     */   {
/*  677:1057 */     double variance = varianceDouble(values, start, length);
/*  678:1058 */     return Math.sqrt(variance);
/*  679:     */   }
/*  680:     */   
/*  681:     */   public static double standardDeviation(double[] values, int length)
/*  682:     */   {
/*  683:1070 */     double variance = varianceDouble(values, 0, length);
/*  684:1071 */     return Math.sqrt(variance);
/*  685:     */   }
/*  686:     */   
/*  687:     */   public static double standardDeviation(double[] values)
/*  688:     */   {
/*  689:1082 */     double variance = varianceDouble(values, 0, values.length);
/*  690:1083 */     return Math.sqrt(variance);
/*  691:     */   }
/*  692:     */   
/*  693:     */   public static double median(double[] values, int start, int length)
/*  694:     */   {
/*  695:1096 */     double[] sorted = new double[length];
/*  696:1097 */     System.arraycopy(values, start, sorted, 0, length);
/*  697:1098 */     Arrays.sort(sorted);
/*  698:1100 */     if (length % 2 == 0)
/*  699:     */     {
/*  700:1101 */       int middle = sorted.length / 2;
/*  701:1102 */       double median = (sorted[(middle - 1)] + sorted[middle]) / 2.0D;
/*  702:1103 */       return median;
/*  703:     */     }
/*  704:1105 */     return sorted[(sorted.length / 2)];
/*  705:     */   }
/*  706:     */   
/*  707:     */   public static double median(double[] values, int length)
/*  708:     */   {
/*  709:1117 */     return median(values, 0, length);
/*  710:     */   }
/*  711:     */   
/*  712:     */   public static double median(double[] values)
/*  713:     */   {
/*  714:1127 */     return median(values, 0, values.length);
/*  715:     */   }
/*  716:     */   
/*  717:     */   public static boolean equals(int start, int end, double[] expected, double[] got)
/*  718:     */   {
/*  719:1140 */     if (expected.length != got.length) {
/*  720:1141 */       return false;
/*  721:     */     }
/*  722:1144 */     for (int index = start; index < end; index++) {
/*  723:1145 */       if (expected[index] != got[index]) {
/*  724:1146 */         return false;
/*  725:     */       }
/*  726:     */     }
/*  727:1149 */     return true;
/*  728:     */   }
/*  729:     */   
/*  730:     */   public static int hashCode(double[] array)
/*  731:     */   {
/*  732:1154 */     if (array == null) {
/*  733:1155 */       return 0;
/*  734:     */     }
/*  735:1158 */     int result = 1;
/*  736:1159 */     for (double element : array)
/*  737:     */     {
/*  738:1160 */       long bits = Double.doubleToLongBits(element);
/*  739:1161 */       result = 31 * result + (int)(bits ^ bits >>> 32);
/*  740:     */     }
/*  741:1163 */     return result;
/*  742:     */   }
/*  743:     */   
/*  744:     */   public static int hashCode(int start, int end, double[] array)
/*  745:     */   {
/*  746:1167 */     if (array == null) {
/*  747:1168 */       return 0;
/*  748:     */     }
/*  749:1170 */     int result = 1;
/*  750:1172 */     for (int index = start; index < end; index++)
/*  751:     */     {
/*  752:1174 */       long bits = Double.doubleToLongBits(array[index]);
/*  753:1175 */       result = 31 * result + (int)(bits ^ bits >>> 32);
/*  754:     */     }
/*  755:1178 */     return result;
/*  756:     */   }
/*  757:     */   
/*  758:     */   public static abstract interface ReduceBy
/*  759:     */   {
/*  760:     */     public abstract double reduce(double paramDouble1, double paramDouble2);
/*  761:     */   }
/*  762:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Dbl
 * JD-Core Version:    0.7.0.1
 */