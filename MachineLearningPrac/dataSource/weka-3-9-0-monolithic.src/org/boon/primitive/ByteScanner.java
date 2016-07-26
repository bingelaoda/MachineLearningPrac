/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.util.Arrays;
/*    4:     */ import org.boon.Exceptions;
/*    5:     */ 
/*    6:     */ public class ByteScanner
/*    7:     */ {
/*    8:     */   public static boolean isDigits(char[] inputArray)
/*    9:     */   {
/*   10:  46 */     for (int index = 0; index < inputArray.length; index++)
/*   11:     */     {
/*   12:  47 */       char a = inputArray[index];
/*   13:  48 */       if (!CharScanner.isDigit(a)) {
/*   14:  49 */         return false;
/*   15:     */       }
/*   16:     */     }
/*   17:  52 */     return true;
/*   18:     */   }
/*   19:     */   
/*   20:     */   public static boolean hasDecimalChar(byte[] chars, boolean negative)
/*   21:     */   {
/*   22:  58 */     int index = 0;
/*   23:  60 */     if (negative) {
/*   24:  60 */       index++;
/*   25:     */     }
/*   26:  62 */     for (; index < chars.length; index++) {
/*   27:  63 */       switch (chars[index])
/*   28:     */       {
/*   29:     */       case 43: 
/*   30:     */       case 45: 
/*   31:     */       case 46: 
/*   32:     */       case 69: 
/*   33:     */       case 101: 
/*   34:  69 */         return true;
/*   35:     */       }
/*   36:     */     }
/*   37:  72 */     return false;
/*   38:     */   }
/*   39:     */   
/*   40:     */   public static byte[][] splitExact(byte[] inputArray, int split, int resultsArrayLength)
/*   41:     */   {
/*   42:  80 */     byte[][] results = new byte[resultsArrayLength][];
/*   43:     */     
/*   44:  82 */     int resultIndex = 0;
/*   45:  83 */     int startCurrentLineIndex = 0;
/*   46:  84 */     int currentLineLength = 1;
/*   47:     */     
/*   48:     */ 
/*   49:  87 */     byte c = 0;
/*   50:  88 */     int index = 0;
/*   51:  90 */     for (; index < inputArray.length; currentLineLength++)
/*   52:     */     {
/*   53:  91 */       c = inputArray[index];
/*   54:  92 */       if (c == split)
/*   55:     */       {
/*   56:  94 */         results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*   57:     */         
/*   58:  96 */         startCurrentLineIndex = index + 1;
/*   59:     */         
/*   60:  98 */         currentLineLength = 0;
/*   61:  99 */         resultIndex++;
/*   62:     */       }
/*   63:  90 */       index++;
/*   64:     */     }
/*   65: 103 */     if (c != split)
/*   66:     */     {
/*   67: 105 */       results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*   68:     */       
/*   69: 107 */       resultIndex++;
/*   70:     */     }
/*   71: 110 */     int actualLength = resultIndex;
/*   72: 111 */     if (actualLength < resultsArrayLength)
/*   73:     */     {
/*   74: 112 */       int newSize = resultsArrayLength - actualLength;
/*   75: 113 */       results = __shrink(results, newSize);
/*   76:     */     }
/*   77: 115 */     return results;
/*   78:     */   }
/*   79:     */   
/*   80:     */   public static byte[][] splitExact(byte[] inputArray, int resultsArrayLength, int... delims)
/*   81:     */   {
/*   82: 121 */     byte[][] results = new byte[resultsArrayLength][];
/*   83:     */     
/*   84: 123 */     int resultIndex = 0;
/*   85: 124 */     int startCurrentLineIndex = 0;
/*   86: 125 */     int currentLineLength = 1;
/*   87:     */     
/*   88:     */ 
/*   89: 128 */     byte c = 0;
/*   90: 129 */     int index = 0;
/*   91: 134 */     for (; index < inputArray.length; currentLineLength++)
/*   92:     */     {
/*   93: 135 */       c = inputArray[index];
/*   94: 138 */       for (int j = 0; j < delims.length; j++)
/*   95:     */       {
/*   96: 139 */         int split = delims[j];
/*   97: 140 */         if (c == split)
/*   98:     */         {
/*   99: 142 */           results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  100:     */           
/*  101: 144 */           startCurrentLineIndex = index + 1;
/*  102:     */           
/*  103: 146 */           currentLineLength = 0;
/*  104: 147 */           resultIndex++;
/*  105: 148 */           break;
/*  106:     */         }
/*  107:     */       }
/*  108: 134 */       index++;
/*  109:     */     }
/*  110: 153 */     if (!Byt.inIntArray(c, delims))
/*  111:     */     {
/*  112: 155 */       results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  113:     */       
/*  114: 157 */       resultIndex++;
/*  115:     */     }
/*  116: 161 */     int actualLength = resultIndex;
/*  117: 162 */     if (actualLength < resultsArrayLength)
/*  118:     */     {
/*  119: 163 */       int newSize = resultsArrayLength - actualLength;
/*  120: 164 */       results = __shrink(results, newSize);
/*  121:     */     }
/*  122: 166 */     return results;
/*  123:     */   }
/*  124:     */   
/*  125:     */   public static byte[][] split(byte[] inputArray, int split)
/*  126:     */   {
/*  127: 172 */     byte[][] results = new byte[16][];
/*  128:     */     
/*  129: 174 */     int resultIndex = 0;
/*  130: 175 */     int startCurrentLineIndex = 0;
/*  131: 176 */     int currentLineLength = 1;
/*  132:     */     
/*  133:     */ 
/*  134: 179 */     byte c = 0;
/*  135: 180 */     int index = 0;
/*  136: 182 */     for (; index < inputArray.length; currentLineLength++)
/*  137:     */     {
/*  138: 183 */       c = inputArray[index];
/*  139: 184 */       if (c == split)
/*  140:     */       {
/*  141: 186 */         if (resultIndex == results.length) {
/*  142: 188 */           results = _grow(results);
/*  143:     */         }
/*  144: 192 */         results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  145:     */         
/*  146: 194 */         startCurrentLineIndex = index + 1;
/*  147:     */         
/*  148: 196 */         currentLineLength = 0;
/*  149: 197 */         resultIndex++;
/*  150:     */       }
/*  151: 182 */       index++;
/*  152:     */     }
/*  153: 201 */     if (c != split)
/*  154:     */     {
/*  155: 203 */       results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  156:     */       
/*  157: 205 */       resultIndex++;
/*  158:     */     }
/*  159: 208 */     int actualLength = resultIndex;
/*  160: 209 */     if (actualLength < results.length)
/*  161:     */     {
/*  162: 210 */       int newSize = results.length - actualLength;
/*  163: 211 */       results = __shrink(results, newSize);
/*  164:     */     }
/*  165: 213 */     return results;
/*  166:     */   }
/*  167:     */   
/*  168:     */   public static byte[][] splitByChars(byte[] inputArray, char... delims)
/*  169:     */   {
/*  170: 219 */     byte[][] results = new byte[16][];
/*  171:     */     
/*  172: 221 */     int resultIndex = 0;
/*  173: 222 */     int startCurrentLineIndex = 0;
/*  174: 223 */     int currentLineLength = 1;
/*  175:     */     
/*  176:     */ 
/*  177: 226 */     byte c = 0;
/*  178: 227 */     int index = 0;
/*  179: 232 */     for (; index < inputArray.length; currentLineLength++)
/*  180:     */     {
/*  181: 234 */       c = inputArray[index];
/*  182: 237 */       for (int j = 0; j < delims.length; j++)
/*  183:     */       {
/*  184: 238 */         int split = delims[j];
/*  185: 239 */         if (c == split)
/*  186:     */         {
/*  187: 241 */           if (resultIndex == results.length) {
/*  188: 243 */             results = _grow(results);
/*  189:     */           }
/*  190: 247 */           results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  191:     */           
/*  192: 249 */           startCurrentLineIndex = index + 1;
/*  193:     */           
/*  194: 251 */           currentLineLength = 0;
/*  195: 252 */           resultIndex++;
/*  196: 253 */           break;
/*  197:     */         }
/*  198:     */       }
/*  199: 232 */       index++;
/*  200:     */     }
/*  201: 258 */     if (!Chr.in(c, delims))
/*  202:     */     {
/*  203: 260 */       results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  204:     */       
/*  205: 262 */       resultIndex++;
/*  206:     */     }
/*  207: 266 */     int actualLength = resultIndex;
/*  208: 267 */     if (actualLength < results.length)
/*  209:     */     {
/*  210: 268 */       int newSize = results.length - actualLength;
/*  211: 269 */       results = __shrink(results, newSize);
/*  212:     */     }
/*  213: 271 */     return results;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public static byte[][] splitByCharsFromToDelims(byte[] inputArray, int from, int to, byte... delims)
/*  217:     */   {
/*  218: 278 */     byte[][] results = new byte[16][];
/*  219:     */     
/*  220: 280 */     int length = to - from;
/*  221:     */     
/*  222: 282 */     int resultIndex = 0;
/*  223: 283 */     int startCurrentLineIndex = 0;
/*  224: 284 */     int currentLineLength = 1;
/*  225:     */     
/*  226:     */ 
/*  227: 287 */     int c = 0;
/*  228: 288 */     int index = from;
/*  229: 293 */     for (; index < length; currentLineLength++)
/*  230:     */     {
/*  231: 295 */       c = inputArray[index];
/*  232: 298 */       for (int j = 0; j < delims.length; j++)
/*  233:     */       {
/*  234: 299 */         int split = delims[j];
/*  235: 300 */         if (c == split)
/*  236:     */         {
/*  237: 302 */           if (resultIndex == results.length) {
/*  238: 304 */             results = _grow(results);
/*  239:     */           }
/*  240: 308 */           results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  241:     */           
/*  242: 310 */           startCurrentLineIndex = index + 1;
/*  243:     */           
/*  244: 312 */           currentLineLength = 0;
/*  245: 313 */           resultIndex++;
/*  246: 314 */           break;
/*  247:     */         }
/*  248:     */       }
/*  249: 293 */       index++;
/*  250:     */     }
/*  251: 319 */     if (!Byt.in(c, delims))
/*  252:     */     {
/*  253: 321 */       results[resultIndex] = Byt.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  254:     */       
/*  255: 323 */       resultIndex++;
/*  256:     */     }
/*  257: 327 */     int actualLength = resultIndex;
/*  258: 328 */     if (actualLength < results.length)
/*  259:     */     {
/*  260: 329 */       int newSize = results.length - actualLength;
/*  261: 330 */       results = __shrink(results, newSize);
/*  262:     */     }
/*  263: 332 */     return results;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public static byte[][] splitByCharsNoneEmpty(byte[] inputArray, char... delims)
/*  267:     */   {
/*  268: 338 */     byte[][] results = splitByChars(inputArray, delims);
/*  269: 339 */     return compact(results);
/*  270:     */   }
/*  271:     */   
/*  272:     */   public static byte[][] splitByCharsNoneEmpty(byte[] inputArray, int from, int to, byte... delims)
/*  273:     */   {
/*  274: 346 */     byte[][] results = splitByCharsFromToDelims(inputArray, from, to, delims);
/*  275: 347 */     return compact(results);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public static byte[][] compact(byte[][] array)
/*  279:     */   {
/*  280: 352 */     int nullCount = 0;
/*  281: 353 */     for (byte[] ch : array) {
/*  282: 355 */       if ((ch == null) || (ch.length == 0)) {
/*  283: 356 */         nullCount++;
/*  284:     */       }
/*  285:     */     }
/*  286: 359 */     byte[][] newArray = new byte[array.length - nullCount][];
/*  287:     */     
/*  288: 361 */     int j = 0;
/*  289: 362 */     for (byte[] ch : array) {
/*  290: 364 */       if ((ch != null) && (ch.length != 0))
/*  291:     */       {
/*  292: 368 */         newArray[j] = ch;
/*  293: 369 */         j++;
/*  294:     */       }
/*  295:     */     }
/*  296: 371 */     return newArray;
/*  297:     */   }
/*  298:     */   
/*  299:     */   private static byte[][] _grow(byte[][] array)
/*  300:     */   {
/*  301: 376 */     Exceptions.requireNonNull(array);
/*  302:     */     
/*  303: 378 */     byte[][] newArray = new byte[array.length * 2][];
/*  304: 379 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  305: 380 */     return newArray;
/*  306:     */   }
/*  307:     */   
/*  308:     */   private static byte[][] __shrink(byte[][] array, int size)
/*  309:     */   {
/*  310: 384 */     Exceptions.requireNonNull(array);
/*  311: 385 */     byte[][] newArray = new byte[array.length - size][];
/*  312:     */     
/*  313: 387 */     System.arraycopy(array, 0, newArray, 0, array.length - size);
/*  314: 388 */     return newArray;
/*  315:     */   }
/*  316:     */   
/*  317: 392 */   static final String MIN_INT_STR_NO_SIGN = String.valueOf(-2147483648).substring(1);
/*  318: 393 */   static final String MAX_INT_STR = String.valueOf(2147483647);
/*  319: 396 */   static final String MIN_LONG_STR_NO_SIGN = String.valueOf(-9223372036854775808L).substring(1);
/*  320: 397 */   static final String MAX_LONG_STR = String.valueOf(9223372036854775807L);
/*  321:     */   private static final long L_BILLION = 1000000000L;
/*  322:     */   
/*  323:     */   public static boolean isInteger(byte[] digitChars, int offset, int len)
/*  324:     */   {
/*  325: 402 */     String cmpStr = digitChars[offset] == 45 ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
/*  326: 403 */     int cmpLen = cmpStr.length();
/*  327: 404 */     if (len < cmpLen) {
/*  328: 404 */       return true;
/*  329:     */     }
/*  330: 405 */     if (len > cmpLen) {
/*  331: 405 */       return false;
/*  332:     */     }
/*  333: 407 */     for (int i = 0; i < cmpLen; i++)
/*  334:     */     {
/*  335: 408 */       int diff = digitChars[(offset + i)] - cmpStr.charAt(i);
/*  336: 409 */       if (diff != 0) {
/*  337: 410 */         return diff < 0;
/*  338:     */       }
/*  339:     */     }
/*  340: 413 */     return true;
/*  341:     */   }
/*  342:     */   
/*  343:     */   public static boolean isLong(byte[] digitChars, int offset, int len)
/*  344:     */   {
/*  345: 418 */     String cmpStr = digitChars[offset] == 45 ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
/*  346: 419 */     int cmpLen = cmpStr.length();
/*  347: 420 */     if (len < cmpLen) {
/*  348: 420 */       return true;
/*  349:     */     }
/*  350: 421 */     if (len > cmpLen) {
/*  351: 421 */       return false;
/*  352:     */     }
/*  353: 423 */     for (int i = 0; i < cmpLen; i++)
/*  354:     */     {
/*  355: 424 */       int diff = digitChars[(offset + i)] - cmpStr.charAt(i);
/*  356: 425 */       if (diff != 0) {
/*  357: 426 */         return diff < 0;
/*  358:     */       }
/*  359:     */     }
/*  360: 429 */     return true;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public static int parseInt(byte[] digitChars)
/*  364:     */   {
/*  365: 434 */     return parseIntFromTo(digitChars, 0, digitChars.length);
/*  366:     */   }
/*  367:     */   
/*  368:     */   public static int parseIntFromTo(byte[] digitChars, int offset, int to)
/*  369:     */   {
/*  370:     */     try
/*  371:     */     {
/*  372: 446 */       boolean negative = false;
/*  373: 447 */       int c = digitChars[offset];
/*  374: 448 */       if (c == 45)
/*  375:     */       {
/*  376: 449 */         offset++;
/*  377: 450 */         negative = true;
/*  378:     */       }
/*  379:     */       int num;
/*  380: 452 */       if (negative)
/*  381:     */       {
/*  382: 453 */         int num = digitChars[offset] - 48;
/*  383: 454 */         offset++;
/*  384: 454 */         if (offset < to)
/*  385:     */         {
/*  386: 455 */           num = num * 10 + (digitChars[offset] - 48);
/*  387: 456 */           offset++;
/*  388: 456 */           if (offset < to)
/*  389:     */           {
/*  390: 457 */             num = num * 10 + (digitChars[offset] - 48);
/*  391: 458 */             offset++;
/*  392: 458 */             if (offset < to)
/*  393:     */             {
/*  394: 459 */               num = num * 10 + (digitChars[offset] - 48);
/*  395: 460 */               offset++;
/*  396: 460 */               if (offset < to)
/*  397:     */               {
/*  398: 461 */                 num = num * 10 + (digitChars[offset] - 48);
/*  399: 462 */                 offset++;
/*  400: 462 */                 if (offset < to)
/*  401:     */                 {
/*  402: 463 */                   num = num * 10 + (digitChars[offset] - 48);
/*  403: 464 */                   offset++;
/*  404: 464 */                   if (offset < to)
/*  405:     */                   {
/*  406: 465 */                     num = num * 10 + (digitChars[offset] - 48);
/*  407: 466 */                     offset++;
/*  408: 466 */                     if (offset < to)
/*  409:     */                     {
/*  410: 467 */                       num = num * 10 + (digitChars[offset] - 48);
/*  411: 468 */                       offset++;
/*  412: 468 */                       if (offset < to)
/*  413:     */                       {
/*  414: 469 */                         num = num * 10 + (digitChars[offset] - 48);
/*  415: 470 */                         offset++;
/*  416: 470 */                         if (offset < to) {
/*  417: 471 */                           num = num * 10 + (digitChars[offset] - 48);
/*  418:     */                         }
/*  419:     */                       }
/*  420:     */                     }
/*  421:     */                   }
/*  422:     */                 }
/*  423:     */               }
/*  424:     */             }
/*  425:     */           }
/*  426:     */         }
/*  427:     */       }
/*  428:     */       else
/*  429:     */       {
/*  430: 482 */         num = digitChars[offset] - 48;
/*  431: 483 */         offset++;
/*  432: 483 */         if (offset < to)
/*  433:     */         {
/*  434: 484 */           num = num * 10 + (digitChars[offset] - 48);
/*  435: 485 */           offset++;
/*  436: 485 */           if (offset < to)
/*  437:     */           {
/*  438: 486 */             num = num * 10 + (digitChars[offset] - 48);
/*  439: 487 */             offset++;
/*  440: 487 */             if (offset < to)
/*  441:     */             {
/*  442: 488 */               num = num * 10 + (digitChars[offset] - 48);
/*  443: 489 */               offset++;
/*  444: 489 */               if (offset < to)
/*  445:     */               {
/*  446: 490 */                 num = num * 10 + (digitChars[offset] - 48);
/*  447: 491 */                 offset++;
/*  448: 491 */                 if (offset < to)
/*  449:     */                 {
/*  450: 492 */                   num = num * 10 + (digitChars[offset] - 48);
/*  451: 493 */                   offset++;
/*  452: 493 */                   if (offset < to)
/*  453:     */                   {
/*  454: 494 */                     num = num * 10 + (digitChars[offset] - 48);
/*  455: 495 */                     offset++;
/*  456: 495 */                     if (offset < to)
/*  457:     */                     {
/*  458: 496 */                       num = num * 10 + (digitChars[offset] - 48);
/*  459: 497 */                       offset++;
/*  460: 497 */                       if (offset < to)
/*  461:     */                       {
/*  462: 498 */                         num = num * 10 + (digitChars[offset] - 48);
/*  463: 499 */                         offset++;
/*  464: 499 */                         if (offset < to) {
/*  465: 500 */                           num = num * 10 + (digitChars[offset] - 48);
/*  466:     */                         }
/*  467:     */                       }
/*  468:     */                     }
/*  469:     */                   }
/*  470:     */                 }
/*  471:     */               }
/*  472:     */             }
/*  473:     */           }
/*  474:     */         }
/*  475:     */       }
/*  476: 512 */       return negative ? num * -1 : num;
/*  477:     */     }
/*  478:     */     catch (Exception ex)
/*  479:     */     {
/*  480: 514 */       return ((Integer)Exceptions.handle(Integer.TYPE, ex)).intValue();
/*  481:     */     }
/*  482:     */   }
/*  483:     */   
/*  484:     */   public static int parseIntIgnoreDot(byte[] digitChars, int offset, int len)
/*  485:     */   {
/*  486: 519 */     int num = digitChars[offset] - 48;
/*  487: 520 */     int to = len + offset;
/*  488:     */     
/*  489: 522 */     offset++;
/*  490: 522 */     if (offset < to)
/*  491:     */     {
/*  492: 523 */       num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  493: 524 */       offset++;
/*  494: 524 */       if (offset < to)
/*  495:     */       {
/*  496: 525 */         num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  497: 526 */         offset++;
/*  498: 526 */         if (offset < to)
/*  499:     */         {
/*  500: 527 */           num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  501: 528 */           offset++;
/*  502: 528 */           if (offset < to)
/*  503:     */           {
/*  504: 529 */             num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  505: 530 */             offset++;
/*  506: 530 */             if (offset < to)
/*  507:     */             {
/*  508: 531 */               num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  509: 532 */               offset++;
/*  510: 532 */               if (offset < to)
/*  511:     */               {
/*  512: 533 */                 num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  513: 534 */                 offset++;
/*  514: 534 */                 if (offset < to)
/*  515:     */                 {
/*  516: 535 */                   num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  517: 536 */                   offset++;
/*  518: 536 */                   if (offset < to)
/*  519:     */                   {
/*  520: 537 */                     num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  521: 538 */                     offset++;
/*  522: 538 */                     if (offset < to) {
/*  523: 539 */                       num = digitChars[offset] != 46 ? num * 10 + (digitChars[offset] - 48) : num;
/*  524:     */                     }
/*  525:     */                   }
/*  526:     */                 }
/*  527:     */               }
/*  528:     */             }
/*  529:     */           }
/*  530:     */         }
/*  531:     */       }
/*  532:     */     }
/*  533: 549 */     return num;
/*  534:     */   }
/*  535:     */   
/*  536:     */   public static long parseLong(byte[] digitChars, int offset, int len)
/*  537:     */   {
/*  538: 553 */     int len1 = len - 9;
/*  539: 554 */     long val = parseIntFromTo(digitChars, offset, len1) * 1000000000L;
/*  540: 555 */     return val + parseIntFromTo(digitChars, offset + len1, 9);
/*  541:     */   }
/*  542:     */   
/*  543:     */   public static long parseLongIgnoreDot(byte[] digitChars, int offset, int len)
/*  544:     */   {
/*  545: 559 */     int len1 = len - 9;
/*  546: 560 */     long val = parseIntIgnoreDot(digitChars, offset, len1) * 1000000000L;
/*  547: 561 */     return val + parseIntIgnoreDot(digitChars, offset + len1, 9);
/*  548:     */   }
/*  549:     */   
/*  550:     */   public static Number parseJsonNumber(byte[] buffer)
/*  551:     */   {
/*  552: 567 */     return parseJsonNumber(buffer, 0, buffer.length);
/*  553:     */   }
/*  554:     */   
/*  555:     */   public static Number parseJsonNumber(byte[] buffer, int from, int to)
/*  556:     */   {
/*  557: 573 */     return parseJsonNumber(buffer, from, to, null);
/*  558:     */   }
/*  559:     */   
/*  560:     */   public static Number parseJsonNumber(byte[] buffer, int from, int max, int[] size)
/*  561:     */   {
/*  562: 578 */     Number value = null;
/*  563: 579 */     boolean simple = true;
/*  564: 580 */     int digitsPastPoint = 0;
/*  565:     */     
/*  566: 582 */     int index = from;
/*  567: 585 */     if (buffer[index] == 45) {
/*  568: 586 */       index++;
/*  569:     */     }
/*  570: 589 */     boolean foundDot = false;
/*  571: 590 */     for (; index < max; index++)
/*  572:     */     {
/*  573: 591 */       int ch = buffer[index];
/*  574: 592 */       if (CharScanner.isNumberDigit(ch))
/*  575:     */       {
/*  576: 594 */         if (foundDot == true) {
/*  577: 595 */           digitsPastPoint++;
/*  578:     */         }
/*  579:     */       }
/*  580:     */       else
/*  581:     */       {
/*  582: 597 */         if ((ch <= 32) || (CharScanner.isDelimiter(ch))) {
/*  583:     */           break;
/*  584:     */         }
/*  585: 598 */         if (ch == 46) {
/*  586: 599 */           foundDot = true;
/*  587: 601 */         } else if ((ch == 69) || (ch == 101) || (ch == 45) || (ch == 43)) {
/*  588: 602 */           simple = false;
/*  589:     */         } else {
/*  590: 604 */           Exceptions.die("unexpected character " + ch);
/*  591:     */         }
/*  592:     */       }
/*  593:     */     }
/*  594: 609 */     if (digitsPastPoint >= powersOf10.length - 1) {
/*  595: 610 */       simple = false;
/*  596:     */     }
/*  597: 614 */     int length = index - from;
/*  598: 616 */     if ((!foundDot) && (simple))
/*  599:     */     {
/*  600: 617 */       if (isInteger(buffer, from, length)) {
/*  601: 618 */         value = Integer.valueOf(parseIntFromTo(buffer, from, index));
/*  602:     */       } else {
/*  603: 620 */         value = Long.valueOf(parseLongFromTo(buffer, from, index));
/*  604:     */       }
/*  605:     */     }
/*  606: 623 */     else if ((foundDot) && (simple))
/*  607:     */     {
/*  608: 627 */       if (length < powersOf10.length)
/*  609:     */       {
/*  610:     */         long lvalue;
/*  611:     */         long lvalue;
/*  612: 629 */         if (isInteger(buffer, from, length)) {
/*  613: 630 */           lvalue = parseIntFromToIgnoreDot(buffer, from, index);
/*  614:     */         } else {
/*  615: 632 */           lvalue = parseLongFromToIgnoreDot(buffer, from, index);
/*  616:     */         }
/*  617: 635 */         double power = powersOf10[digitsPastPoint];
/*  618: 636 */         value = Double.valueOf(lvalue / power);
/*  619:     */       }
/*  620:     */       else
/*  621:     */       {
/*  622: 639 */         value = Double.valueOf(Double.parseDouble(new String(buffer, from, length)));
/*  623:     */       }
/*  624:     */     }
/*  625:     */     else {
/*  626: 645 */       value = Double.valueOf(Double.parseDouble(new String(buffer, from, index - from)));
/*  627:     */     }
/*  628: 649 */     if (size != null) {
/*  629: 650 */       size[0] = index;
/*  630:     */     }
/*  631: 653 */     return value;
/*  632:     */   }
/*  633:     */   
/*  634:     */   public static long parseLongFromTo(byte[] digitChars, int offset, int to)
/*  635:     */   {
/*  636: 661 */     boolean negative = false;
/*  637: 662 */     int c = digitChars[offset];
/*  638: 663 */     if (c == 45)
/*  639:     */     {
/*  640: 664 */       offset++;
/*  641: 665 */       negative = true;
/*  642:     */     }
/*  643: 668 */     c = digitChars[offset];
/*  644: 669 */     long num = c - 48;
/*  645: 670 */     offset++;
/*  646: 674 */     for (; offset < to; offset++)
/*  647:     */     {
/*  648: 675 */       c = digitChars[offset];
/*  649: 676 */       long digit = c - 48;
/*  650: 677 */       num = num * 10L + digit;
/*  651:     */     }
/*  652: 680 */     return negative ? num * -1L : num;
/*  653:     */   }
/*  654:     */   
/*  655:     */   public static int parseIntFromToIgnoreDot(byte[] digitChars, int offset, int to)
/*  656:     */   {
/*  657: 688 */     boolean negative = false;
/*  658: 689 */     int c = digitChars[offset];
/*  659: 690 */     if (c == 45)
/*  660:     */     {
/*  661: 691 */       offset++;
/*  662: 692 */       negative = true;
/*  663:     */     }
/*  664: 695 */     c = digitChars[offset];
/*  665: 696 */     int num = c - 48;
/*  666: 697 */     offset++;
/*  667: 699 */     for (; offset < to; offset++)
/*  668:     */     {
/*  669: 700 */       c = digitChars[offset];
/*  670: 701 */       if (c != 46) {
/*  671: 702 */         num = num * 10 + (c - 48);
/*  672:     */       }
/*  673:     */     }
/*  674: 707 */     return negative ? num * -1 : num;
/*  675:     */   }
/*  676:     */   
/*  677:     */   public static long parseLongFromToIgnoreDot(byte[] digitChars, int offset, int to)
/*  678:     */   {
/*  679: 714 */     boolean negative = false;
/*  680: 715 */     int c = digitChars[offset];
/*  681: 716 */     if (c == 45)
/*  682:     */     {
/*  683: 717 */       offset++;
/*  684: 718 */       negative = true;
/*  685:     */     }
/*  686: 721 */     c = digitChars[offset];
/*  687: 722 */     long num = c - 48;
/*  688: 723 */     offset++;
/*  689: 725 */     for (; offset < to; offset++)
/*  690:     */     {
/*  691: 726 */       c = digitChars[offset];
/*  692: 727 */       if (c != 46) {
/*  693: 728 */         num = num * 10L + (c - 48);
/*  694:     */       }
/*  695:     */     }
/*  696: 733 */     return negative ? num * -1L : num;
/*  697:     */   }
/*  698:     */   
/*  699:     */   public static float parseFloat(byte[] buffer, int from, int to)
/*  700:     */   {
/*  701: 744 */     return (float)parseDouble(buffer, from, to);
/*  702:     */   }
/*  703:     */   
/*  704:     */   public static double parseDouble(byte[] buffer)
/*  705:     */   {
/*  706: 749 */     return parseDouble(buffer, 0, buffer.length);
/*  707:     */   }
/*  708:     */   
/*  709:     */   public static double parseDouble(byte[] buffer, int from, int to)
/*  710:     */   {
/*  711: 754 */     boolean simple = true;
/*  712: 755 */     int digitsPastPoint = 0;
/*  713:     */     
/*  714: 757 */     int index = from;
/*  715: 760 */     if (buffer[index] == 45) {
/*  716: 761 */       index++;
/*  717:     */     }
/*  718: 764 */     boolean foundDot = false;
/*  719: 765 */     for (; index < to; index++)
/*  720:     */     {
/*  721: 766 */       int ch = buffer[index];
/*  722: 767 */       if (CharScanner.isNumberDigit(ch))
/*  723:     */       {
/*  724: 769 */         if (foundDot == true) {
/*  725: 770 */           digitsPastPoint++;
/*  726:     */         }
/*  727:     */       }
/*  728: 772 */       else if (ch == 46) {
/*  729: 773 */         foundDot = true;
/*  730: 775 */       } else if ((ch == 69) || (ch == 101) || (ch == 45) || (ch == 43)) {
/*  731: 776 */         simple = false;
/*  732:     */       } else {
/*  733: 778 */         Exceptions.die("unexpected character " + ch);
/*  734:     */       }
/*  735:     */     }
/*  736: 783 */     if (digitsPastPoint >= powersOf10.length - 1) {
/*  737: 784 */       simple = false;
/*  738:     */     }
/*  739: 788 */     int length = index - from;
/*  740:     */     double value;
/*  741:     */     double value;
/*  742: 790 */     if ((!foundDot) && (simple))
/*  743:     */     {
/*  744:     */       double value;
/*  745: 791 */       if (isInteger(buffer, from, length)) {
/*  746: 792 */         value = parseIntFromTo(buffer, from, index);
/*  747:     */       } else {
/*  748: 794 */         value = parseLongFromTo(buffer, from, index);
/*  749:     */       }
/*  750:     */     }
/*  751:     */     else
/*  752:     */     {
/*  753:     */       double value;
/*  754: 797 */       if ((foundDot) && (simple))
/*  755:     */       {
/*  756:     */         double value;
/*  757: 801 */         if (length < powersOf10.length)
/*  758:     */         {
/*  759:     */           long lvalue;
/*  760:     */           long lvalue;
/*  761: 803 */           if (isInteger(buffer, from, length)) {
/*  762: 804 */             lvalue = parseIntFromToIgnoreDot(buffer, from, index);
/*  763:     */           } else {
/*  764: 806 */             lvalue = parseLongFromToIgnoreDot(buffer, from, index);
/*  765:     */           }
/*  766: 809 */           double power = powersOf10[digitsPastPoint];
/*  767: 810 */           value = lvalue / power;
/*  768:     */         }
/*  769:     */         else
/*  770:     */         {
/*  771: 813 */           value = Double.parseDouble(new String(buffer, from, length));
/*  772:     */         }
/*  773:     */       }
/*  774:     */       else
/*  775:     */       {
/*  776: 819 */         value = Double.parseDouble(new String(buffer, from, index - from));
/*  777:     */       }
/*  778:     */     }
/*  779: 824 */     return value;
/*  780:     */   }
/*  781:     */   
/*  782: 827 */   private static double[] powersOf10 = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 10000000.0D, 100000000.0D, 1000000000.0D, 10000000000.0D, 100000000000.0D, 1000000000000.0D, 10000000000000.0D, 100000000000000.0D, 1000000000000000.0D, 10000000000000000.0D, 1.0E+017D, 1.0E+018D };
/*  783:     */   
/*  784:     */   public static double simpleDouble(byte[] buffer, boolean simple, int digitsPastPoint, int startIndex, int endIndex)
/*  785:     */   {
/*  786: 857 */     if (simple)
/*  787:     */     {
/*  788: 859 */       int length = endIndex - startIndex;
/*  789:     */       long value;
/*  790:     */       long value;
/*  791: 861 */       if (isInteger(buffer, startIndex, length)) {
/*  792: 862 */         value = parseIntIgnoreDot(buffer, startIndex, length);
/*  793:     */       } else {
/*  794: 864 */         value = parseLongIgnoreDot(buffer, startIndex, length);
/*  795:     */       }
/*  796: 866 */       if (digitsPastPoint < powersOf10.length)
/*  797:     */       {
/*  798: 867 */         double power = powersOf10[digitsPastPoint];
/*  799: 868 */         return value / power;
/*  800:     */       }
/*  801:     */     }
/*  802: 875 */     return Double.parseDouble(new String(buffer, startIndex, endIndex - startIndex));
/*  803:     */   }
/*  804:     */   
/*  805:     */   public static int skipWhiteSpace(byte[] array, int index)
/*  806:     */   {
/*  807: 881 */     for (; index < array.length; index++)
/*  808:     */     {
/*  809: 882 */       int c = array[index];
/*  810: 883 */       if (c > 32) {
/*  811: 885 */         return index;
/*  812:     */       }
/*  813:     */     }
/*  814: 888 */     return index;
/*  815:     */   }
/*  816:     */   
/*  817:     */   public static int skipWhiteSpace(byte[] array, int index, int length)
/*  818:     */   {
/*  819: 894 */     for (; index < length; index++)
/*  820:     */     {
/*  821: 895 */       int c = array[index];
/*  822: 896 */       if (c > 32) {
/*  823: 898 */         return index;
/*  824:     */       }
/*  825:     */     }
/*  826: 901 */     return index;
/*  827:     */   }
/*  828:     */   
/*  829:     */   public static byte[] readNumber(byte[] array, int idx)
/*  830:     */   {
/*  831: 905 */     int startIndex = idx;
/*  832: 908 */     while (CharScanner.isDecimalDigit(array[idx]))
/*  833:     */     {
/*  834: 911 */       idx++;
/*  835: 912 */       if (idx >= array.length) {
/*  836:     */         break;
/*  837:     */       }
/*  838:     */     }
/*  839: 916 */     return Arrays.copyOfRange(array, startIndex, idx);
/*  840:     */   }
/*  841:     */   
/*  842:     */   public static byte[] readNumber(byte[] array, int idx, int len)
/*  843:     */   {
/*  844: 924 */     int startIndex = idx;
/*  845: 927 */     while (CharScanner.isDecimalDigit(array[idx]))
/*  846:     */     {
/*  847: 930 */       idx++;
/*  848: 931 */       if (idx >= len) {
/*  849:     */         break;
/*  850:     */       }
/*  851:     */     }
/*  852: 935 */     return Arrays.copyOfRange(array, startIndex, idx);
/*  853:     */   }
/*  854:     */   
/*  855:     */   public static int skipWhiteSpaceFast(byte[] array)
/*  856:     */   {
/*  857: 949 */     for (int index = 0; index < array.length; index++)
/*  858:     */     {
/*  859: 951 */       int c = array[index];
/*  860: 952 */       if (c > 32) {
/*  861: 954 */         return index;
/*  862:     */       }
/*  863:     */     }
/*  864: 957 */     return index;
/*  865:     */   }
/*  866:     */   
/*  867:     */   public static int skipWhiteSpaceFast(byte[] array, int index)
/*  868:     */   {
/*  869: 964 */     for (; index < array.length; index++)
/*  870:     */     {
/*  871: 965 */       int c = array[index];
/*  872: 966 */       if (c > 32) {
/*  873: 968 */         return index;
/*  874:     */       }
/*  875:     */     }
/*  876: 971 */     return index - 1;
/*  877:     */   }
/*  878:     */   
/*  879:     */   protected static int encodeNibbleToHexAsciiCharByte(int nibble)
/*  880:     */   {
/*  881: 982 */     switch (nibble)
/*  882:     */     {
/*  883:     */     case 0: 
/*  884:     */     case 1: 
/*  885:     */     case 2: 
/*  886:     */     case 3: 
/*  887:     */     case 4: 
/*  888:     */     case 5: 
/*  889:     */     case 6: 
/*  890:     */     case 7: 
/*  891:     */     case 8: 
/*  892:     */     case 9: 
/*  893: 993 */       return nibble + 48;
/*  894:     */     case 10: 
/*  895:     */     case 11: 
/*  896:     */     case 12: 
/*  897:     */     case 13: 
/*  898:     */     case 14: 
/*  899:     */     case 15: 
/*  900:1000 */       return nibble + 87;
/*  901:     */     }
/*  902:1002 */     Exceptions.die("illegal nibble: " + nibble);
/*  903:1003 */     return -1;
/*  904:     */   }
/*  905:     */   
/*  906:     */   public static void encodeByteIntoTwoAsciiCharBytes(int decoded, byte[] encoded)
/*  907:     */   {
/*  908:1016 */     encoded[0] = ((byte)encodeNibbleToHexAsciiCharByte(decoded >> 4 & 0xF));
/*  909:1017 */     encoded[1] = ((byte)encodeNibbleToHexAsciiCharByte(decoded & 0xF));
/*  910:     */   }
/*  911:     */   
/*  912:     */   public static String errorDetails(String message, byte[] array, int index, int ch)
/*  913:     */   {
/*  914:1022 */     CharBuf buf = CharBuf.create(255);
/*  915:     */     
/*  916:1024 */     buf.addLine(message);
/*  917:     */     
/*  918:     */ 
/*  919:1027 */     buf.addLine("");
/*  920:1028 */     buf.addLine("The current character read is " + CharScanner.debugCharDescription(ch));
/*  921:     */     
/*  922:     */ 
/*  923:1031 */     buf.addLine(message);
/*  924:     */     
/*  925:1033 */     int line = 0;
/*  926:1034 */     int lastLineIndex = 0;
/*  927:1036 */     for (int i = 0; (i < index) && (i < array.length); i++) {
/*  928:1037 */       if (array[i] == 10)
/*  929:     */       {
/*  930:1038 */         line++;
/*  931:1039 */         lastLineIndex = i + 1;
/*  932:     */       }
/*  933:     */     }
/*  934:1043 */     int count = 0;
/*  935:1045 */     for (int i = lastLineIndex; i < array.length; count++)
/*  936:     */     {
/*  937:1046 */       if (array[i] == 10) {
/*  938:     */         break;
/*  939:     */       }
/*  940:1045 */       i++;
/*  941:     */     }
/*  942:1052 */     buf.addLine("line number " + (line + 1));
/*  943:1053 */     buf.addLine("index number " + index);
/*  944:     */     try
/*  945:     */     {
/*  946:1057 */       buf.addLine(new String(array, lastLineIndex, count));
/*  947:     */     }
/*  948:     */     catch (Exception ex)
/*  949:     */     {
/*  950:     */       try
/*  951:     */       {
/*  952:1061 */         int start = index -= 10;
/*  953:     */         
/*  954:1063 */         buf.addLine(new String(array, start, index));
/*  955:     */       }
/*  956:     */       catch (Exception ex2)
/*  957:     */       {
/*  958:1065 */         buf.addLine(new String(array, 0, array.length));
/*  959:     */       }
/*  960:     */     }
/*  961:1068 */     for (int i = 0; i < index - lastLineIndex; i++) {
/*  962:1069 */       buf.add('.');
/*  963:     */     }
/*  964:1071 */     buf.add('^');
/*  965:     */     
/*  966:1073 */     return buf.toString();
/*  967:     */   }
/*  968:     */   
/*  969:     */   public static boolean hasEscapeChar(byte[] array, int index, int[] indexHolder)
/*  970:     */   {
/*  971:1080 */     for (; index < array.length; index++)
/*  972:     */     {
/*  973:1081 */       int currentChar = array[index];
/*  974:1082 */       if (CharScanner.isDoubleQuote(currentChar))
/*  975:     */       {
/*  976:1083 */         indexHolder[0] = index;
/*  977:1084 */         return false;
/*  978:     */       }
/*  979:1085 */       if (CharScanner.isEscape(currentChar))
/*  980:     */       {
/*  981:1086 */         indexHolder[0] = index;
/*  982:1087 */         return true;
/*  983:     */       }
/*  984:     */     }
/*  985:1092 */     indexHolder[0] = index;
/*  986:1093 */     return false;
/*  987:     */   }
/*  988:     */   
/*  989:     */   public static int findEndQuote(byte[] array, int index)
/*  990:     */   {
/*  991:1100 */     boolean escape = false;
/*  992:1102 */     for (; index < array.length; index++)
/*  993:     */     {
/*  994:1103 */       int currentChar = array[index];
/*  995:1104 */       if ((CharScanner.isDoubleQuote(currentChar)) && 
/*  996:1105 */         (!escape)) {
/*  997:     */         break;
/*  998:     */       }
/*  999:1109 */       if (CharScanner.isEscape(currentChar))
/* 1000:     */       {
/* 1001:1110 */         if (!escape) {
/* 1002:1111 */           escape = true;
/* 1003:     */         } else {
/* 1004:1113 */           escape = false;
/* 1005:     */         }
/* 1006:     */       }
/* 1007:     */       else {
/* 1008:1116 */         escape = false;
/* 1009:     */       }
/* 1010:     */     }
/* 1011:1119 */     return index;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public static int findEndQuoteUTF8(byte[] array, int index)
/* 1015:     */   {
/* 1016:1126 */     boolean escape = false;
/* 1017:1128 */     for (; index < array.length; index++)
/* 1018:     */     {
/* 1019:1129 */       int currentChar = array[index];
/* 1020:1130 */       if (currentChar >= 0)
/* 1021:     */       {
/* 1022:1131 */         if ((CharScanner.isDoubleQuote(currentChar)) && 
/* 1023:1132 */           (!escape)) {
/* 1024:     */           break;
/* 1025:     */         }
/* 1026:1136 */         if (CharScanner.isEscape(currentChar))
/* 1027:     */         {
/* 1028:1137 */           if (!escape) {
/* 1029:1138 */             escape = true;
/* 1030:     */           } else {
/* 1031:1140 */             escape = false;
/* 1032:     */           }
/* 1033:     */         }
/* 1034:     */         else {
/* 1035:1143 */           escape = false;
/* 1036:     */         }
/* 1037:     */       }
/* 1038:     */       else
/* 1039:     */       {
/* 1040:1146 */         index = skipUTF8NonCharOrLongChar(currentChar, index);
/* 1041:     */       }
/* 1042:     */     }
/* 1043:1149 */     return index;
/* 1044:     */   }
/* 1045:     */   
/* 1046:     */   private static int skipUTF8NonCharOrLongChar(int c, int index)
/* 1047:     */   {
/* 1048:1158 */     if (c >> 5 == -2) {
/* 1049:1159 */       index++;
/* 1050:1160 */     } else if (c >> 4 == -2) {
/* 1051:1161 */       index += 2;
/* 1052:1162 */     } else if (c >> 3 == -2) {
/* 1053:1163 */       index += 3;
/* 1054:     */     }
/* 1055:1166 */     return index;
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public static boolean hasEscapeCharUTF8(byte[] array, int index, int[] indexHolder)
/* 1059:     */   {
/* 1060:1172 */     for (; index < array.length; index++)
/* 1061:     */     {
/* 1062:1173 */       int currentChar = array[index];
/* 1063:1174 */       if (currentChar >= 0)
/* 1064:     */       {
/* 1065:1176 */         if (CharScanner.isDoubleQuote(currentChar))
/* 1066:     */         {
/* 1067:1177 */           indexHolder[0] = index;
/* 1068:1178 */           return false;
/* 1069:     */         }
/* 1070:1179 */         if (CharScanner.isEscape(currentChar))
/* 1071:     */         {
/* 1072:1180 */           indexHolder[0] = index;
/* 1073:1181 */           return true;
/* 1074:     */         }
/* 1075:     */       }
/* 1076:     */       else
/* 1077:     */       {
/* 1078:1184 */         index = skipUTF8NonCharOrLongChar(currentChar, index);
/* 1079:     */       }
/* 1080:     */     }
/* 1081:1190 */     indexHolder[0] = index;
/* 1082:1191 */     return false;
/* 1083:     */   }
/* 1084:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.ByteScanner
 * JD-Core Version:    0.7.0.1
 */