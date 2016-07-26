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
/*   11:     */ public class Flt
/*   12:     */ {
/*   13:     */   public static float[] grow(float[] array, int size)
/*   14:     */   {
/*   15:  48 */     Exceptions.requireNonNull(array);
/*   16:     */     
/*   17:  50 */     float[] newArray = new float[array.length + size];
/*   18:  51 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   19:  52 */     return newArray;
/*   20:     */   }
/*   21:     */   
/*   22:     */   public static float[] grow(float[] array)
/*   23:     */   {
/*   24:  57 */     Exceptions.requireNonNull(array);
/*   25:     */     
/*   26:  59 */     float[] newArray = new float[array.length * 2];
/*   27:  60 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*   28:  61 */     return newArray;
/*   29:     */   }
/*   30:     */   
/*   31:     */   public static float[] shrink(float[] array, int size)
/*   32:     */   {
/*   33:  66 */     Exceptions.requireNonNull(array);
/*   34:     */     
/*   35:  68 */     float[] newArray = new float[array.length - size];
/*   36:     */     
/*   37:  70 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*   38:  71 */     return newArray;
/*   39:     */   }
/*   40:     */   
/*   41:     */   public static float[] compact(float[] array)
/*   42:     */   {
/*   43:  76 */     Exceptions.requireNonNull(array);
/*   44:     */     
/*   45:  78 */     int nullCount = 0;
/*   46:  79 */     for (float ch : array) {
/*   47:  81 */       if (ch == 0.0F) {
/*   48:  82 */         nullCount++;
/*   49:     */       }
/*   50:     */     }
/*   51:  85 */     float[] newArray = new float[array.length - nullCount];
/*   52:     */     
/*   53:  87 */     int j = 0;
/*   54:  88 */     for (float ch : array) {
/*   55:  90 */       if (ch != 0.0F)
/*   56:     */       {
/*   57:  94 */         newArray[j] = ch;
/*   58:  95 */         j++;
/*   59:     */       }
/*   60:     */     }
/*   61:  97 */     return newArray;
/*   62:     */   }
/*   63:     */   
/*   64:     */   public static float[] arrayOfFloat(int size)
/*   65:     */   {
/*   66: 108 */     return new float[size];
/*   67:     */   }
/*   68:     */   
/*   69:     */   @Universal
/*   70:     */   public static float[] array(float... array)
/*   71:     */   {
/*   72: 117 */     Exceptions.requireNonNull(array);
/*   73: 118 */     return array;
/*   74:     */   }
/*   75:     */   
/*   76:     */   @Universal
/*   77:     */   public static int lengthOf(float[] array)
/*   78:     */   {
/*   79: 125 */     return len(array);
/*   80:     */   }
/*   81:     */   
/*   82:     */   @Universal
/*   83:     */   public static int len(float[] array)
/*   84:     */   {
/*   85: 130 */     return array.length;
/*   86:     */   }
/*   87:     */   
/*   88:     */   @Universal
/*   89:     */   public static float atIndex(float[] array, int index)
/*   90:     */   {
/*   91: 136 */     return idx(array, index);
/*   92:     */   }
/*   93:     */   
/*   94:     */   @Universal
/*   95:     */   public static float idx(float[] array, int index)
/*   96:     */   {
/*   97: 141 */     int i = calculateIndex(array, index);
/*   98:     */     
/*   99: 143 */     return array[i];
/*  100:     */   }
/*  101:     */   
/*  102:     */   @Universal
/*  103:     */   public static void atIndex(float[] array, int index, float value)
/*  104:     */   {
/*  105: 150 */     idx(array, index, value);
/*  106:     */   }
/*  107:     */   
/*  108:     */   @Universal
/*  109:     */   public static void idx(float[] array, int index, float value)
/*  110:     */   {
/*  111: 155 */     int i = calculateIndex(array, index);
/*  112:     */     
/*  113: 157 */     array[i] = value;
/*  114:     */   }
/*  115:     */   
/*  116:     */   @Universal
/*  117:     */   public static float[] sliceOf(float[] array, int startIndex, int endIndex)
/*  118:     */   {
/*  119: 163 */     return slc(array, startIndex, endIndex);
/*  120:     */   }
/*  121:     */   
/*  122:     */   @Universal
/*  123:     */   public static float[] slc(float[] array, int startIndex, int endIndex)
/*  124:     */   {
/*  125: 169 */     int start = calculateIndex(array, startIndex);
/*  126: 170 */     int end = calculateEndIndex(array, endIndex);
/*  127: 171 */     int newLength = end - start;
/*  128: 173 */     if (newLength < 0) {
/*  129: 174 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, end index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  130:     */     }
/*  131: 180 */     float[] newArray = new float[newLength];
/*  132: 181 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  133: 182 */     return newArray;
/*  134:     */   }
/*  135:     */   
/*  136:     */   @Universal
/*  137:     */   public static float[] sliceOf(float[] array, int startIndex)
/*  138:     */   {
/*  139: 189 */     return slc(array, startIndex);
/*  140:     */   }
/*  141:     */   
/*  142:     */   @Universal
/*  143:     */   public static float[] slc(float[] array, int startIndex)
/*  144:     */   {
/*  145: 195 */     int start = calculateIndex(array, startIndex);
/*  146: 196 */     int newLength = array.length - start;
/*  147: 198 */     if (newLength < 0) {
/*  148: 199 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(startIndex), Integer.valueOf(array.length) }));
/*  149:     */     }
/*  150: 205 */     float[] newArray = new float[newLength];
/*  151: 206 */     System.arraycopy(array, start, newArray, 0, newLength);
/*  152: 207 */     return newArray;
/*  153:     */   }
/*  154:     */   
/*  155:     */   @Universal
/*  156:     */   public static float[] endOfSlice(float[] array, int endIndex)
/*  157:     */   {
/*  158: 214 */     return slcEnd(array, endIndex);
/*  159:     */   }
/*  160:     */   
/*  161:     */   @Universal
/*  162:     */   public static float[] slcEnd(float[] array, int endIndex)
/*  163:     */   {
/*  164: 220 */     int end = calculateEndIndex(array, endIndex);
/*  165: 221 */     int newLength = end;
/*  166: 223 */     if (newLength < 0) {
/*  167: 224 */       throw new ArrayIndexOutOfBoundsException(String.format("start index %d, length %d", new Object[] { Integer.valueOf(endIndex), Integer.valueOf(array.length) }));
/*  168:     */     }
/*  169: 230 */     float[] newArray = new float[newLength];
/*  170: 231 */     System.arraycopy(array, 0, newArray, 0, newLength);
/*  171: 232 */     return newArray;
/*  172:     */   }
/*  173:     */   
/*  174:     */   @Universal
/*  175:     */   public static boolean in(float value, float[] array)
/*  176:     */   {
/*  177: 237 */     for (float currentValue : array) {
/*  178: 238 */       if (currentValue == value) {
/*  179: 239 */         return true;
/*  180:     */       }
/*  181:     */     }
/*  182: 242 */     return false;
/*  183:     */   }
/*  184:     */   
/*  185:     */   @Universal
/*  186:     */   public static float[] copy(float[] array)
/*  187:     */   {
/*  188: 248 */     Exceptions.requireNonNull(array);
/*  189: 249 */     float[] newArray = new float[array.length];
/*  190: 250 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  191: 251 */     return newArray;
/*  192:     */   }
/*  193:     */   
/*  194:     */   @Universal
/*  195:     */   public static float[] add(float[] array, float v)
/*  196:     */   {
/*  197: 257 */     Exceptions.requireNonNull(array);
/*  198: 258 */     float[] newArray = new float[array.length + 1];
/*  199: 259 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  200: 260 */     newArray[array.length] = v;
/*  201: 261 */     return newArray;
/*  202:     */   }
/*  203:     */   
/*  204:     */   @Universal
/*  205:     */   public static float[] add(float[] array, float[] array2)
/*  206:     */   {
/*  207: 266 */     Exceptions.requireNonNull(array);
/*  208: 267 */     float[] newArray = new float[array.length + array2.length];
/*  209: 268 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  210: 269 */     System.arraycopy(array2, 0, newArray, array.length, array2.length);
/*  211: 270 */     return newArray;
/*  212:     */   }
/*  213:     */   
/*  214:     */   @Universal
/*  215:     */   public static float[] insert(float[] array, int idx, float v)
/*  216:     */   {
/*  217: 277 */     if (idx >= array.length) {
/*  218: 278 */       return add(array, v);
/*  219:     */     }
/*  220: 281 */     int index = calculateIndex(array, idx);
/*  221:     */     
/*  222:     */ 
/*  223: 284 */     float[] newArray = new float[array.length + 1];
/*  224: 286 */     if (index != 0) {
/*  225: 289 */       System.arraycopy(array, 0, newArray, 0, index);
/*  226:     */     }
/*  227: 293 */     boolean lastIndex = index == array.length - 1;
/*  228: 294 */     int remainingIndex = array.length - index;
/*  229: 296 */     if (lastIndex) {
/*  230: 299 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  231:     */     } else {
/*  232: 304 */       System.arraycopy(array, index, newArray, index + 1, remainingIndex);
/*  233:     */     }
/*  234: 308 */     newArray[index] = v;
/*  235: 309 */     return newArray;
/*  236:     */   }
/*  237:     */   
/*  238:     */   @Universal
/*  239:     */   public static float[] insert(float[] array, int fromIndex, float[] values)
/*  240:     */   {
/*  241: 315 */     Exceptions.requireNonNull(array);
/*  242: 317 */     if (fromIndex >= array.length) {
/*  243: 318 */       return add(array, values);
/*  244:     */     }
/*  245: 321 */     int index = calculateIndex(array, fromIndex);
/*  246:     */     
/*  247:     */ 
/*  248: 324 */     float[] newArray = new float[array.length + values.length];
/*  249: 326 */     if (index != 0) {
/*  250: 329 */       System.arraycopy(array, 0, newArray, 0, index);
/*  251:     */     }
/*  252: 333 */     boolean lastIndex = index == array.length - 1;
/*  253:     */     
/*  254: 335 */     int toIndex = index + values.length;
/*  255: 336 */     int remainingIndex = newArray.length - toIndex;
/*  256: 338 */     if (lastIndex) {
/*  257: 341 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  258:     */     } else {
/*  259: 346 */       System.arraycopy(array, index, newArray, index + values.length, remainingIndex);
/*  260:     */     }
/*  261: 350 */     int i = index;
/*  262: 350 */     for (int j = 0; i < toIndex; j++)
/*  263:     */     {
/*  264: 351 */       newArray[i] = values[j];i++;
/*  265:     */     }
/*  266: 353 */     return newArray;
/*  267:     */   }
/*  268:     */   
/*  269:     */   private static int calculateIndex(float[] array, int originalIndex)
/*  270:     */   {
/*  271: 359 */     int length = array.length;
/*  272:     */     
/*  273:     */ 
/*  274:     */ 
/*  275: 363 */     int index = originalIndex;
/*  276: 368 */     if (index < 0) {
/*  277: 369 */       index = length + index;
/*  278:     */     }
/*  279: 380 */     if (index < 0) {
/*  280: 381 */       index = 0;
/*  281:     */     }
/*  282: 383 */     if (index >= length) {
/*  283: 384 */       index = length - 1;
/*  284:     */     }
/*  285: 386 */     return index;
/*  286:     */   }
/*  287:     */   
/*  288:     */   private static int calculateEndIndex(float[] array, int originalIndex)
/*  289:     */   {
/*  290: 392 */     int length = array.length;
/*  291:     */     
/*  292:     */ 
/*  293:     */ 
/*  294: 396 */     int index = originalIndex;
/*  295: 401 */     if (index < 0) {
/*  296: 402 */       index = length + index;
/*  297:     */     }
/*  298: 413 */     if (index < 0) {
/*  299: 414 */       index = 0;
/*  300:     */     }
/*  301: 416 */     if (index > length) {
/*  302: 417 */       index = length;
/*  303:     */     }
/*  304: 419 */     return index;
/*  305:     */   }
/*  306:     */   
/*  307:     */   public static boolean equalsOrDie(float[] expected, float[] got)
/*  308:     */   {
/*  309: 431 */     if (expected.length != got.length) {
/*  310: 432 */       Exceptions.die(new Object[] { "Lengths did not match, expected length", Integer.valueOf(expected.length), "but got", Integer.valueOf(got.length) });
/*  311:     */     }
/*  312: 436 */     for (int index = 0; index < expected.length; index++) {
/*  313: 437 */       if (expected[index] != got[index]) {
/*  314: 438 */         Exceptions.die(new Object[] { "value at index did not match index", Integer.valueOf(index), "expected value", Float.valueOf(expected[index]), "but got", Float.valueOf(got[index]) });
/*  315:     */       }
/*  316:     */     }
/*  317: 444 */     return true;
/*  318:     */   }
/*  319:     */   
/*  320:     */   public static boolean equals(float[] expected, float[] got)
/*  321:     */   {
/*  322: 456 */     if (expected.length != got.length) {
/*  323: 457 */       return false;
/*  324:     */     }
/*  325: 460 */     for (int index = 0; index < expected.length; index++) {
/*  326: 461 */       if (expected[index] != got[index]) {
/*  327: 462 */         return false;
/*  328:     */       }
/*  329:     */     }
/*  330: 465 */     return true;
/*  331:     */   }
/*  332:     */   
/*  333:     */   public static double reduceBy(float[] array, ReduceBy reduceBy)
/*  334:     */   {
/*  335: 485 */     double sum = 0.0D;
/*  336: 486 */     for (float v : array) {
/*  337: 487 */       sum = reduceBy.reduce(sum, v);
/*  338:     */     }
/*  339: 489 */     return sum;
/*  340:     */   }
/*  341:     */   
/*  342:     */   public static double reduceBy(float[] array, int start, int length, ReduceBy reduceBy)
/*  343:     */   {
/*  344: 503 */     double sum = 0.0D;
/*  345: 505 */     for (int index = start; index < length; index++)
/*  346:     */     {
/*  347: 506 */       float v = array[index];
/*  348: 507 */       sum = reduceBy.reduce(sum, v);
/*  349:     */     }
/*  350: 509 */     return sum;
/*  351:     */   }
/*  352:     */   
/*  353:     */   public static double reduceBy(float[] array, int length, ReduceBy reduceBy)
/*  354:     */   {
/*  355: 523 */     double sum = 0.0D;
/*  356: 525 */     for (int index = 0; index < length; index++)
/*  357:     */     {
/*  358: 526 */       float v = array[index];
/*  359: 527 */       sum = reduceBy.reduce(sum, v);
/*  360:     */     }
/*  361: 529 */     return sum;
/*  362:     */   }
/*  363:     */   
/*  364:     */   public static <T> double reduceBy(float[] array, T object)
/*  365:     */   {
/*  366: 543 */     if (object.getClass().isAnonymousClass()) {
/*  367: 544 */       return reduceByR(array, object);
/*  368:     */     }
/*  369:     */     try
/*  370:     */     {
/*  371: 549 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  372: 550 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  373:     */       try
/*  374:     */       {
/*  375: 553 */         double sum = 0.0D;
/*  376: 554 */         for (float v : array) {
/*  377: 555 */           sum = methodHandle.invokeExact(sum, v);
/*  378:     */         }
/*  379: 558 */         return sum;
/*  380:     */       }
/*  381:     */       catch (Throwable throwable)
/*  382:     */       {
/*  383: 560 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  384:     */       }
/*  385: 563 */       return reduceByR(array, object);
/*  386:     */     }
/*  387:     */     catch (Exception ex) {}
/*  388:     */   }
/*  389:     */   
/*  390:     */   public static <T> double reduceBy(float[] array, T object, String methodName)
/*  391:     */   {
/*  392: 581 */     if (object.getClass().isAnonymousClass()) {
/*  393: 582 */       return reduceByR(array, object, methodName);
/*  394:     */     }
/*  395:     */     try
/*  396:     */     {
/*  397: 586 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
/*  398: 587 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  399:     */       try
/*  400:     */       {
/*  401: 590 */         double sum = 0.0D;
/*  402: 591 */         for (float v : array) {
/*  403: 592 */           sum = methodHandle.invokeExact(sum, v);
/*  404:     */         }
/*  405: 595 */         return sum;
/*  406:     */       }
/*  407:     */       catch (Throwable throwable)
/*  408:     */       {
/*  409: 597 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  410:     */       }
/*  411: 600 */       return reduceByR(array, object, methodName);
/*  412:     */     }
/*  413:     */     catch (Exception ex) {}
/*  414:     */   }
/*  415:     */   
/*  416:     */   private static <T> double reduceByR(float[] array, T object)
/*  417:     */   {
/*  418:     */     try
/*  419:     */     {
/*  420: 617 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  421:     */       
/*  422:     */ 
/*  423: 620 */       double sum = 0.0D;
/*  424: 621 */       for (float v : array) {
/*  425: 622 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Float.valueOf(v) })).doubleValue();
/*  426:     */       }
/*  427: 625 */       return sum;
/*  428:     */     }
/*  429:     */     catch (Throwable throwable)
/*  430:     */     {
/*  431: 628 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  432:     */     }
/*  433:     */   }
/*  434:     */   
/*  435:     */   private static <T> double reduceByR(float[] array, T object, String methodName)
/*  436:     */   {
/*  437:     */     try
/*  438:     */     {
/*  439: 645 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  440:     */       
/*  441:     */ 
/*  442: 648 */       double sum = 0.0D;
/*  443: 649 */       for (float v : array) {
/*  444: 650 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Float.valueOf(v) })).doubleValue();
/*  445:     */       }
/*  446: 653 */       return sum;
/*  447:     */     }
/*  448:     */     catch (Throwable throwable)
/*  449:     */     {
/*  450: 656 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  451:     */     }
/*  452:     */   }
/*  453:     */   
/*  454:     */   private static <T> double reduceByR(float[] array, int length, T object, String methodName)
/*  455:     */   {
/*  456:     */     try
/*  457:     */     {
/*  458: 674 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object, methodName);
/*  459:     */       
/*  460:     */ 
/*  461: 677 */       double sum = 0.0D;
/*  462: 678 */       for (int index = 0; index < length; index++)
/*  463:     */       {
/*  464: 679 */         float v = array[index];
/*  465: 680 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Float.valueOf(v) })).doubleValue();
/*  466:     */       }
/*  467: 683 */       return sum;
/*  468:     */     }
/*  469:     */     catch (Throwable throwable)
/*  470:     */     {
/*  471: 686 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  472:     */     }
/*  473:     */   }
/*  474:     */   
/*  475:     */   private static <T> double reduceByR(float[] array, int length, T object)
/*  476:     */   {
/*  477:     */     try
/*  478:     */     {
/*  479: 702 */       Method method = Invoker.invokeReducerLongIntReturnLongMethod(object);
/*  480:     */       
/*  481:     */ 
/*  482: 705 */       double sum = 0.0D;
/*  483: 706 */       for (int index = 0; index < length; index++)
/*  484:     */       {
/*  485: 707 */         float v = array[index];
/*  486: 708 */         sum = ((Double)method.invoke(object, new Object[] { Double.valueOf(sum), Float.valueOf(v) })).doubleValue();
/*  487:     */       }
/*  488: 711 */       return sum;
/*  489:     */     }
/*  490:     */     catch (Throwable throwable)
/*  491:     */     {
/*  492: 714 */       return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  493:     */     }
/*  494:     */   }
/*  495:     */   
/*  496:     */   public static double reduceBy(float[] array, int length, Object object)
/*  497:     */   {
/*  498: 730 */     if (object.getClass().isAnonymousClass()) {
/*  499: 731 */       return reduceByR(array, length, object);
/*  500:     */     }
/*  501:     */     try
/*  502:     */     {
/*  503: 735 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  504: 736 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  505:     */       try
/*  506:     */       {
/*  507: 739 */         double sum = 0.0D;
/*  508: 740 */         for (int index = 0; index < length; index++)
/*  509:     */         {
/*  510: 741 */           float v = array[index];
/*  511: 742 */           sum = methodHandle.invokeExact(sum, v);
/*  512:     */         }
/*  513: 745 */         return sum;
/*  514:     */       }
/*  515:     */       catch (Throwable throwable)
/*  516:     */       {
/*  517: 747 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  518:     */       }
/*  519: 750 */       return reduceByR(array, length, object);
/*  520:     */     }
/*  521:     */     catch (Exception ex) {}
/*  522:     */   }
/*  523:     */   
/*  524:     */   public static double reduceBy(float[] array, int length, Object function, String functionName)
/*  525:     */   {
/*  526: 770 */     if (function.getClass().isAnonymousClass()) {
/*  527: 771 */       return reduceByR(array, length, function, functionName);
/*  528:     */     }
/*  529:     */     try
/*  530:     */     {
/*  531: 775 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(function, functionName);
/*  532: 776 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  533:     */       try
/*  534:     */       {
/*  535: 779 */         double sum = 0.0D;
/*  536: 780 */         for (int index = 0; index < length; index++)
/*  537:     */         {
/*  538: 781 */           float v = array[index];
/*  539: 782 */           sum = methodHandle.invokeExact(sum, v);
/*  540:     */         }
/*  541: 785 */         return sum;
/*  542:     */       }
/*  543:     */       catch (Throwable throwable)
/*  544:     */       {
/*  545: 787 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  546:     */       }
/*  547: 790 */       return reduceByR(array, length, function, functionName);
/*  548:     */     }
/*  549:     */     catch (Exception ex) {}
/*  550:     */   }
/*  551:     */   
/*  552:     */   public static double reduceBy(float[] array, int start, int length, Object object)
/*  553:     */   {
/*  554: 808 */     if (object.getClass().isAnonymousClass()) {
/*  555: 809 */       return reduceByR(array, object);
/*  556:     */     }
/*  557:     */     try
/*  558:     */     {
/*  559: 813 */       ConstantCallSite callSite = Invoker.invokeReducerLongIntReturnLongMethodHandle(object);
/*  560: 814 */       MethodHandle methodHandle = callSite.dynamicInvoker();
/*  561:     */       try
/*  562:     */       {
/*  563: 817 */         double sum = 0.0D;
/*  564: 818 */         for (int index = start; index < length; index++)
/*  565:     */         {
/*  566: 819 */           float v = array[index];
/*  567: 820 */           sum = methodHandle.invokeExact(sum, v);
/*  568:     */         }
/*  569: 823 */         return sum;
/*  570:     */       }
/*  571:     */       catch (Throwable throwable)
/*  572:     */       {
/*  573: 825 */         return ((Long)Exceptions.handle(Long.class, throwable, new Object[] { "Unable to perform reduceBy" })).longValue();
/*  574:     */       }
/*  575: 828 */       return reduceByR(array, object);
/*  576:     */     }
/*  577:     */     catch (Exception ex) {}
/*  578:     */   }
/*  579:     */   
/*  580:     */   public static boolean equalsOrDie(float expected, float got)
/*  581:     */   {
/*  582: 840 */     if (expected != got) {
/*  583: 841 */       return ((Boolean)Exceptions.die(Boolean.class, new Object[] { "Expected was", Float.valueOf(expected), "but we got ", Float.valueOf(got) })).booleanValue();
/*  584:     */     }
/*  585: 843 */     return true;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public static boolean equals(float expected, float got)
/*  589:     */   {
/*  590: 855 */     return expected == got;
/*  591:     */   }
/*  592:     */   
/*  593:     */   public static double sum(float[] values)
/*  594:     */   {
/*  595: 867 */     return sum(values, 0, values.length);
/*  596:     */   }
/*  597:     */   
/*  598:     */   public static float sum(float[] values, int length)
/*  599:     */   {
/*  600: 878 */     return sum(values, 0, length);
/*  601:     */   }
/*  602:     */   
/*  603:     */   public static float sum(float[] values, int start, int length)
/*  604:     */   {
/*  605: 888 */     double sum = 0.0D;
/*  606: 889 */     for (int index = start; index < length; index++) {
/*  607: 890 */       sum += values[index];
/*  608:     */     }
/*  609: 893 */     if (sum < 1.401298464324817E-045D) {
/*  610: 894 */       Exceptions.die(new Object[] { "overflow the sum is too small", Double.valueOf(sum) });
/*  611:     */     }
/*  612: 898 */     if (sum > 3.402823466385289E+038D) {
/*  613: 899 */       Exceptions.die(new Object[] { "overflow the sum is too big", Double.valueOf(sum) });
/*  614:     */     }
/*  615: 902 */     return (float)sum;
/*  616:     */   }
/*  617:     */   
/*  618:     */   public static double bigSum(float[] values)
/*  619:     */   {
/*  620: 916 */     return bigSum(values, 0, values.length);
/*  621:     */   }
/*  622:     */   
/*  623:     */   public static double bigSum(float[] values, int length)
/*  624:     */   {
/*  625: 927 */     return bigSum(values, 0, length);
/*  626:     */   }
/*  627:     */   
/*  628:     */   public static double bigSum(float[] values, int start, int length)
/*  629:     */   {
/*  630: 936 */     double sum = 0.0D;
/*  631: 937 */     for (int index = start; index < length; index++) {
/*  632: 938 */       sum += values[index];
/*  633:     */     }
/*  634: 941 */     return sum;
/*  635:     */   }
/*  636:     */   
/*  637:     */   public static float max(float[] values, int start, int length)
/*  638:     */   {
/*  639: 954 */     float max = 1.4E-45F;
/*  640: 955 */     for (int index = start; index < length; index++) {
/*  641: 956 */       if (values[index] > max) {
/*  642: 957 */         max = values[index];
/*  643:     */       }
/*  644:     */     }
/*  645: 961 */     return max;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static float max(float[] values)
/*  649:     */   {
/*  650: 971 */     return max(values, 0, values.length);
/*  651:     */   }
/*  652:     */   
/*  653:     */   public static float max(float[] values, int length)
/*  654:     */   {
/*  655: 981 */     return max(values, 0, length);
/*  656:     */   }
/*  657:     */   
/*  658:     */   public static float min(float[] values, int start, int length)
/*  659:     */   {
/*  660: 991 */     float min = 3.4028235E+38F;
/*  661: 992 */     for (int index = start; index < length; index++) {
/*  662: 993 */       if (values[index] < min) {
/*  663: 993 */         min = values[index];
/*  664:     */       }
/*  665:     */     }
/*  666: 995 */     return min;
/*  667:     */   }
/*  668:     */   
/*  669:     */   public static float min(float[] values)
/*  670:     */   {
/*  671:1005 */     return min(values, 0, values.length);
/*  672:     */   }
/*  673:     */   
/*  674:     */   public static float min(float[] values, int length)
/*  675:     */   {
/*  676:1015 */     return min(values, 0, length);
/*  677:     */   }
/*  678:     */   
/*  679:     */   public static float mean(float[] values, int start, int length)
/*  680:     */   {
/*  681:1027 */     return (float)meanDouble(values, start, length);
/*  682:     */   }
/*  683:     */   
/*  684:     */   public static float mean(float[] values, int length)
/*  685:     */   {
/*  686:1039 */     return (float)meanDouble(values, 0, length);
/*  687:     */   }
/*  688:     */   
/*  689:     */   public static float mean(float[] values)
/*  690:     */   {
/*  691:1050 */     return (float)meanDouble(values, 0, values.length);
/*  692:     */   }
/*  693:     */   
/*  694:     */   public static float variance(float[] values, int start, int length)
/*  695:     */   {
/*  696:1064 */     return (float)varianceDouble(values, start, length);
/*  697:     */   }
/*  698:     */   
/*  699:     */   private static double meanDouble(float[] values, int start, int length)
/*  700:     */   {
/*  701:1069 */     double mean = bigSum(values, start, length) / length;
/*  702:1070 */     return mean;
/*  703:     */   }
/*  704:     */   
/*  705:     */   public static double varianceDouble(float[] values, int start, int length)
/*  706:     */   {
/*  707:1084 */     double mean = meanDouble(values, start, length);
/*  708:1085 */     double temp = 0.0D;
/*  709:1086 */     for (int index = start; index < length; index++)
/*  710:     */     {
/*  711:1087 */       double a = values[index];
/*  712:1088 */       temp += (mean - a) * (mean - a);
/*  713:     */     }
/*  714:1090 */     return temp / length;
/*  715:     */   }
/*  716:     */   
/*  717:     */   public static float variance(float[] values, int length)
/*  718:     */   {
/*  719:1102 */     return (float)varianceDouble(values, 0, length);
/*  720:     */   }
/*  721:     */   
/*  722:     */   public static float variance(float[] values)
/*  723:     */   {
/*  724:1113 */     return (float)varianceDouble(values, 0, values.length);
/*  725:     */   }
/*  726:     */   
/*  727:     */   public static float standardDeviation(float[] values, int start, int length)
/*  728:     */   {
/*  729:1126 */     double variance = varianceDouble(values, start, length);
/*  730:1127 */     return (float)Math.sqrt(variance);
/*  731:     */   }
/*  732:     */   
/*  733:     */   public static float standardDeviation(float[] values, int length)
/*  734:     */   {
/*  735:1139 */     double variance = varianceDouble(values, 0, length);
/*  736:1140 */     return (float)Math.sqrt(variance);
/*  737:     */   }
/*  738:     */   
/*  739:     */   public static float standardDeviation(float[] values)
/*  740:     */   {
/*  741:1151 */     double variance = varianceDouble(values, 0, values.length);
/*  742:1152 */     return (float)Math.sqrt(variance);
/*  743:     */   }
/*  744:     */   
/*  745:     */   public static float median(float[] values, int start, int length)
/*  746:     */   {
/*  747:1165 */     float[] sorted = new float[length];
/*  748:1166 */     System.arraycopy(values, start, sorted, 0, length);
/*  749:1167 */     Arrays.sort(sorted);
/*  750:1169 */     if (length % 2 == 0)
/*  751:     */     {
/*  752:1170 */       int middle = sorted.length / 2;
/*  753:1171 */       double median = (sorted[(middle - 1)] + sorted[middle]) / 2.0D;
/*  754:1172 */       return (float)median;
/*  755:     */     }
/*  756:1174 */     return sorted[(sorted.length / 2)];
/*  757:     */   }
/*  758:     */   
/*  759:     */   public static float median(float[] values, int length)
/*  760:     */   {
/*  761:1186 */     return median(values, 0, length);
/*  762:     */   }
/*  763:     */   
/*  764:     */   public static float median(float[] values)
/*  765:     */   {
/*  766:1196 */     return median(values, 0, values.length);
/*  767:     */   }
/*  768:     */   
/*  769:     */   public static boolean equals(int start, int end, float[] expected, float[] got)
/*  770:     */   {
/*  771:1209 */     if (expected.length != got.length) {
/*  772:1210 */       return false;
/*  773:     */     }
/*  774:1213 */     for (int index = start; index < end; index++) {
/*  775:1214 */       if (expected[index] != got[index]) {
/*  776:1215 */         return false;
/*  777:     */       }
/*  778:     */     }
/*  779:1218 */     return true;
/*  780:     */   }
/*  781:     */   
/*  782:     */   public static int hashCode(float[] array)
/*  783:     */   {
/*  784:1222 */     if (array == null) {
/*  785:1223 */       return 0;
/*  786:     */     }
/*  787:1226 */     int result = 1;
/*  788:1227 */     for (float item : array)
/*  789:     */     {
/*  790:1229 */       int bits = Float.floatToIntBits(item);
/*  791:1230 */       result = 31 * result + bits;
/*  792:     */     }
/*  793:1233 */     return result;
/*  794:     */   }
/*  795:     */   
/*  796:     */   public static int hashCode(int start, int end, float[] array)
/*  797:     */   {
/*  798:1237 */     if (array == null) {
/*  799:1238 */       return 0;
/*  800:     */     }
/*  801:1241 */     int result = 1;
/*  802:1243 */     for (int index = start; index < end; index++)
/*  803:     */     {
/*  804:1245 */       int bits = Float.floatToIntBits(array[index]);
/*  805:1246 */       result = 31 * result + bits;
/*  806:     */     }
/*  807:1249 */     return result;
/*  808:     */   }
/*  809:     */   
/*  810:     */   public static abstract interface ReduceBy
/*  811:     */   {
/*  812:     */     public abstract double reduce(double paramDouble, float paramFloat);
/*  813:     */   }
/*  814:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.Flt
 * JD-Core Version:    0.7.0.1
 */