/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.util.Arrays;
/*    4:     */ import org.boon.Exceptions;
/*    5:     */ import org.boon.core.reflection.FastStringUtils;
/*    6:     */ 
/*    7:     */ public class CharScanner
/*    8:     */ {
/*    9:     */   protected static final int COMMA = 44;
/*   10:     */   protected static final int CLOSED_CURLY = 125;
/*   11:     */   protected static final int CLOSED_BRACKET = 93;
/*   12:     */   protected static final int LETTER_E = 101;
/*   13:     */   protected static final int LETTER_BIG_E = 69;
/*   14:     */   protected static final int DECIMAL_POINT = 46;
/*   15:     */   private static final int SPACE = 32;
/*   16:     */   private static final int PIPE = 124;
/*   17:  69 */   private static final char[][] EMPTY_CHAR_ARRAY_ARRAY = new char[0][0];
/*   18:  71 */   private static int NEWLINE = 10;
/*   19:  73 */   private static int CARRIAGE_RETURN = 13;
/*   20:     */   protected static final int ALPHA_0 = 48;
/*   21:     */   protected static final int ALPHA_1 = 49;
/*   22:     */   protected static final int ALPHA_2 = 50;
/*   23:     */   protected static final int ALPHA_3 = 51;
/*   24:     */   protected static final int ALPHA_4 = 52;
/*   25:     */   protected static final int ALPHA_5 = 53;
/*   26:     */   protected static final int ALPHA_6 = 54;
/*   27:     */   protected static final int ALPHA_7 = 55;
/*   28:     */   protected static final int ALPHA_8 = 56;
/*   29:     */   protected static final int ALPHA_9 = 57;
/*   30:     */   protected static final int MINUS = 45;
/*   31:     */   protected static final int PLUS = 43;
/*   32:     */   protected static final int DOUBLE_QUOTE = 34;
/*   33:     */   protected static final int ESCAPE = 92;
/*   34:     */   
/*   35:     */   public static boolean isDigit(int c)
/*   36:     */   {
/*   37: 102 */     return (c >= 48) && (c <= 57);
/*   38:     */   }
/*   39:     */   
/*   40:     */   public static boolean isDecimalDigit(int c)
/*   41:     */   {
/*   42: 107 */     return (isDigit(c)) || (isDecimalChar(c));
/*   43:     */   }
/*   44:     */   
/*   45:     */   public static boolean isDecimalChar(int currentChar)
/*   46:     */   {
/*   47: 113 */     switch (currentChar)
/*   48:     */     {
/*   49:     */     case 43: 
/*   50:     */     case 45: 
/*   51:     */     case 46: 
/*   52:     */     case 69: 
/*   53:     */     case 101: 
/*   54: 119 */       return true;
/*   55:     */     }
/*   56: 121 */     return false;
/*   57:     */   }
/*   58:     */   
/*   59:     */   public static boolean hasDecimalChar(char[] chars, boolean negative)
/*   60:     */   {
/*   61: 128 */     int index = 0;
/*   62: 130 */     if (negative) {
/*   63: 130 */       index++;
/*   64:     */     }
/*   65: 132 */     for (; index < chars.length; index++) {
/*   66: 133 */       switch (chars[index])
/*   67:     */       {
/*   68:     */       case '+': 
/*   69:     */       case '-': 
/*   70:     */       case '.': 
/*   71:     */       case 'E': 
/*   72:     */       case 'e': 
/*   73: 139 */         return true;
/*   74:     */       }
/*   75:     */     }
/*   76: 142 */     return false;
/*   77:     */   }
/*   78:     */   
/*   79:     */   public static boolean isDigits(char[] inputArray)
/*   80:     */   {
/*   81: 147 */     for (int index = 0; index < inputArray.length; index++)
/*   82:     */     {
/*   83: 148 */       char a = inputArray[index];
/*   84: 149 */       if (!isDigit(a)) {
/*   85: 150 */         return false;
/*   86:     */       }
/*   87:     */     }
/*   88: 153 */     return true;
/*   89:     */   }
/*   90:     */   
/*   91:     */   public static char[][] splitExact(char[] inputArray, char split, int resultsArrayLength)
/*   92:     */   {
/*   93: 159 */     char[][] results = new char[resultsArrayLength][];
/*   94:     */     
/*   95: 161 */     int resultIndex = 0;
/*   96: 162 */     int startCurrentLineIndex = 0;
/*   97: 163 */     int currentLineLength = 1;
/*   98:     */     
/*   99:     */ 
/*  100: 166 */     char c = '\000';
/*  101: 167 */     int index = 0;
/*  102: 169 */     for (; index < inputArray.length; currentLineLength++)
/*  103:     */     {
/*  104: 170 */       c = inputArray[index];
/*  105: 171 */       if (c == split)
/*  106:     */       {
/*  107: 173 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  108:     */         
/*  109: 175 */         startCurrentLineIndex = index + 1;
/*  110:     */         
/*  111: 177 */         currentLineLength = 0;
/*  112: 178 */         resultIndex++;
/*  113:     */       }
/*  114: 169 */       index++;
/*  115:     */     }
/*  116: 182 */     if (c != split)
/*  117:     */     {
/*  118: 184 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  119:     */       
/*  120: 186 */       resultIndex++;
/*  121:     */     }
/*  122: 189 */     int actualLength = resultIndex;
/*  123: 190 */     if (actualLength < resultsArrayLength)
/*  124:     */     {
/*  125: 191 */       int newSize = resultsArrayLength - actualLength;
/*  126: 192 */       results = __shrink(results, newSize);
/*  127:     */     }
/*  128: 194 */     return results;
/*  129:     */   }
/*  130:     */   
/*  131:     */   public static char[][] splitExact(char[] inputArray, int resultsArrayLength, char... delims)
/*  132:     */   {
/*  133: 200 */     char[][] results = new char[resultsArrayLength][];
/*  134:     */     
/*  135: 202 */     int resultIndex = 0;
/*  136: 203 */     int startCurrentLineIndex = 0;
/*  137: 204 */     int currentLineLength = 1;
/*  138:     */     
/*  139:     */ 
/*  140: 207 */     char c = '\000';
/*  141: 208 */     int index = 0;
/*  142: 213 */     for (; index < inputArray.length; currentLineLength++)
/*  143:     */     {
/*  144: 214 */       c = inputArray[index];
/*  145: 217 */       for (int j = 0; j < delims.length; j++)
/*  146:     */       {
/*  147: 218 */         char split = delims[j];
/*  148: 219 */         if (c == split)
/*  149:     */         {
/*  150: 221 */           results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  151:     */           
/*  152: 223 */           startCurrentLineIndex = index + 1;
/*  153:     */           
/*  154: 225 */           currentLineLength = 0;
/*  155: 226 */           resultIndex++;
/*  156: 227 */           break;
/*  157:     */         }
/*  158:     */       }
/*  159: 213 */       index++;
/*  160:     */     }
/*  161: 232 */     if (!Chr.in(c, delims))
/*  162:     */     {
/*  163: 234 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  164:     */       
/*  165: 236 */       resultIndex++;
/*  166:     */     }
/*  167: 240 */     int actualLength = resultIndex;
/*  168: 241 */     if (actualLength < resultsArrayLength)
/*  169:     */     {
/*  170: 242 */       int newSize = resultsArrayLength - actualLength;
/*  171: 243 */       results = __shrink(results, newSize);
/*  172:     */     }
/*  173: 245 */     return results;
/*  174:     */   }
/*  175:     */   
/*  176:     */   public static char[][] splitLines(char[] inputArray)
/*  177:     */   {
/*  178: 252 */     char[][] results = new char[16][];
/*  179:     */     
/*  180: 254 */     int resultIndex = 0;
/*  181: 255 */     int startCurrentLineIndex = 0;
/*  182: 256 */     int currentLineLength = 1;
/*  183:     */     
/*  184:     */ 
/*  185: 259 */     int c = 0;
/*  186: 260 */     int index = 0;
/*  187: 262 */     for (; index < inputArray.length; currentLineLength++)
/*  188:     */     {
/*  189: 263 */       c = inputArray[index];
/*  190: 264 */       if ((c == NEWLINE) || (c == CARRIAGE_RETURN))
/*  191:     */       {
/*  192: 266 */         if (resultIndex == results.length) {
/*  193: 268 */           results = _grow(results);
/*  194:     */         }
/*  195: 272 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength);
/*  196:     */         
/*  197: 274 */         startCurrentLineIndex = index + 1;
/*  198:     */         
/*  199: 276 */         currentLineLength = 0;
/*  200: 277 */         resultIndex++;
/*  201:     */       }
/*  202: 262 */       index++;
/*  203:     */     }
/*  204: 281 */     if ((c != NEWLINE) || (c != CARRIAGE_RETURN)) {
/*  205: 283 */       if (resultIndex < results.length)
/*  206:     */       {
/*  207: 284 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  208:     */         
/*  209: 286 */         resultIndex++;
/*  210:     */       }
/*  211:     */     }
/*  212: 290 */     int actualLength = resultIndex;
/*  213: 291 */     if (actualLength < results.length)
/*  214:     */     {
/*  215: 292 */       int newSize = results.length - actualLength;
/*  216: 293 */       results = __shrink(results, newSize);
/*  217:     */     }
/*  218: 295 */     return results;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public static char[][] split(char[] inputArray, char split)
/*  222:     */   {
/*  223: 301 */     char[][] results = new char[16][];
/*  224:     */     
/*  225: 303 */     int resultIndex = 0;
/*  226: 304 */     int startCurrentLineIndex = 0;
/*  227: 305 */     int currentLineLength = 1;
/*  228:     */     
/*  229:     */ 
/*  230: 308 */     char c = '\000';
/*  231: 309 */     int index = 0;
/*  232: 313 */     for (; index < inputArray.length; currentLineLength++)
/*  233:     */     {
/*  234: 314 */       c = inputArray[index];
/*  235: 315 */       if (c == split)
/*  236:     */       {
/*  237: 317 */         if (resultIndex == results.length) {
/*  238: 319 */           results = _grow(results);
/*  239:     */         }
/*  240: 323 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  241:     */         
/*  242: 325 */         startCurrentLineIndex = index + 1;
/*  243:     */         
/*  244: 327 */         currentLineLength = 0;
/*  245: 328 */         resultIndex++;
/*  246:     */       }
/*  247: 313 */       index++;
/*  248:     */     }
/*  249: 334 */     if (c != split)
/*  250:     */     {
/*  251: 336 */       if (resultIndex == results.length) {
/*  252: 338 */         results = _grow(results);
/*  253:     */       }
/*  254: 340 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  255:     */       
/*  256: 342 */       resultIndex++;
/*  257:     */     }
/*  258: 343 */     else if (index != inputArray.length)
/*  259:     */     {
/*  260: 347 */       if (resultIndex == results.length) {
/*  261: 349 */         results = _grow(results);
/*  262:     */       }
/*  263: 351 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, inputArray.length - index - 1);
/*  264:     */       
/*  265: 353 */       resultIndex++;
/*  266:     */     }
/*  267: 357 */     int actualLength = resultIndex;
/*  268: 358 */     if (actualLength < results.length)
/*  269:     */     {
/*  270: 359 */       int newSize = results.length - actualLength;
/*  271: 360 */       results = __shrink(results, newSize);
/*  272:     */     }
/*  273: 362 */     return results;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public static char[][] splitFrom(char[] inputArray, char split, int from)
/*  277:     */   {
/*  278: 369 */     char[][] results = new char[16][];
/*  279:     */     
/*  280: 371 */     int index = from;
/*  281: 372 */     int resultIndex = 0;
/*  282: 373 */     int startCurrentLineIndex = from;
/*  283: 374 */     int currentLineLength = 1;
/*  284:     */     
/*  285:     */ 
/*  286: 377 */     char c = '\000';
/*  287: 381 */     for (; index < inputArray.length; currentLineLength++)
/*  288:     */     {
/*  289: 382 */       c = inputArray[index];
/*  290: 383 */       if (c == split)
/*  291:     */       {
/*  292: 385 */         if (resultIndex == results.length) {
/*  293: 387 */           results = _grow(results);
/*  294:     */         }
/*  295: 391 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  296:     */         
/*  297: 393 */         startCurrentLineIndex = index + 1;
/*  298:     */         
/*  299: 395 */         currentLineLength = 0;
/*  300: 396 */         resultIndex++;
/*  301:     */       }
/*  302: 381 */       index++;
/*  303:     */     }
/*  304: 402 */     if (c != split)
/*  305:     */     {
/*  306: 404 */       if (resultIndex == results.length) {
/*  307: 406 */         results = _grow(results);
/*  308:     */       }
/*  309: 408 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  310:     */       
/*  311: 410 */       resultIndex++;
/*  312:     */     }
/*  313: 411 */     else if (index != inputArray.length)
/*  314:     */     {
/*  315: 415 */       if (resultIndex == results.length) {
/*  316: 417 */         results = _grow(results);
/*  317:     */       }
/*  318: 419 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, inputArray.length - index - 1);
/*  319:     */       
/*  320: 421 */       resultIndex++;
/*  321:     */     }
/*  322: 425 */     int actualLength = resultIndex;
/*  323: 426 */     if (actualLength < results.length)
/*  324:     */     {
/*  325: 427 */       int newSize = results.length - actualLength;
/*  326: 428 */       results = __shrink(results, newSize);
/*  327:     */     }
/*  328: 430 */     return results;
/*  329:     */   }
/*  330:     */   
/*  331:     */   public static char[][] split(char[] inputArray, char split, int limit)
/*  332:     */   {
/*  333: 437 */     if (inputArray.length == 0) {
/*  334: 438 */       return EMPTY_CHAR_ARRAY_ARRAY;
/*  335:     */     }
/*  336: 441 */     char[][] results = new char[limit + 1][];
/*  337:     */     
/*  338: 443 */     int resultIndex = 0;
/*  339: 444 */     int startCurrentLineIndex = 0;
/*  340: 445 */     int currentLineLength = 1;
/*  341:     */     
/*  342:     */ 
/*  343: 448 */     char c = '\000';
/*  344: 449 */     int index = 0;
/*  345: 452 */     for (; index < inputArray.length; currentLineLength++)
/*  346:     */     {
/*  347: 453 */       c = inputArray[index];
/*  348: 454 */       if (c == split)
/*  349:     */       {
/*  350: 457 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  351:     */         
/*  352: 459 */         startCurrentLineIndex = index + 1;
/*  353:     */         
/*  354: 461 */         resultIndex++;
/*  355: 463 */         if (resultIndex >= limit) {
/*  356:     */           break;
/*  357:     */         }
/*  358: 470 */         currentLineLength = 0;
/*  359:     */       }
/*  360: 452 */       index++;
/*  361:     */     }
/*  362: 478 */     if (c != split)
/*  363:     */     {
/*  364: 480 */       if (resultIndex == results.length) {
/*  365: 482 */         results = _grow(results);
/*  366:     */       }
/*  367: 484 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  368:     */       
/*  369: 486 */       resultIndex++;
/*  370:     */     }
/*  371: 487 */     else if (index != inputArray.length)
/*  372:     */     {
/*  373: 491 */       if (resultIndex == results.length) {
/*  374: 493 */         results = _grow(results);
/*  375:     */       }
/*  376: 495 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, inputArray.length - index - 1);
/*  377:     */       
/*  378: 497 */       resultIndex++;
/*  379:     */     }
/*  380: 501 */     int actualLength = resultIndex;
/*  381: 502 */     if (actualLength < results.length)
/*  382:     */     {
/*  383: 503 */       int newSize = results.length - actualLength;
/*  384: 504 */       results = __shrink(results, newSize);
/*  385:     */     }
/*  386: 506 */     return results;
/*  387:     */   }
/*  388:     */   
/*  389:     */   public static char[][] splitFromStartWithLimit(char[] inputArray, char split, int start, int limit)
/*  390:     */   {
/*  391: 514 */     if (inputArray.length == 0) {
/*  392: 515 */       return EMPTY_CHAR_ARRAY_ARRAY;
/*  393:     */     }
/*  394: 518 */     char[][] results = new char[limit + 1][];
/*  395:     */     
/*  396: 520 */     int resultIndex = 0;
/*  397: 521 */     int startCurrentLineIndex = 0;
/*  398: 522 */     int currentLineLength = 1;
/*  399:     */     
/*  400:     */ 
/*  401: 525 */     char c = '\000';
/*  402: 526 */     int index = start;
/*  403: 529 */     for (; index < inputArray.length; currentLineLength++)
/*  404:     */     {
/*  405: 530 */       c = inputArray[index];
/*  406: 531 */       if (c == split)
/*  407:     */       {
/*  408: 534 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  409:     */         
/*  410: 536 */         startCurrentLineIndex = index + 1;
/*  411:     */         
/*  412: 538 */         resultIndex++;
/*  413: 540 */         if (resultIndex >= limit) {
/*  414:     */           break;
/*  415:     */         }
/*  416: 547 */         currentLineLength = 0;
/*  417:     */       }
/*  418: 529 */       index++;
/*  419:     */     }
/*  420: 555 */     if (c != split)
/*  421:     */     {
/*  422: 557 */       if (resultIndex == results.length) {
/*  423: 559 */         results = _grow(results);
/*  424:     */       }
/*  425: 561 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  426:     */       
/*  427: 563 */       resultIndex++;
/*  428:     */     }
/*  429: 564 */     else if (index != inputArray.length)
/*  430:     */     {
/*  431: 568 */       if (resultIndex == results.length) {
/*  432: 570 */         results = _grow(results);
/*  433:     */       }
/*  434: 572 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, inputArray.length - index - 1);
/*  435:     */       
/*  436: 574 */       resultIndex++;
/*  437:     */     }
/*  438: 578 */     int actualLength = resultIndex;
/*  439: 579 */     if (actualLength < results.length)
/*  440:     */     {
/*  441: 580 */       int newSize = results.length - actualLength;
/*  442: 581 */       results = __shrink(results, newSize);
/*  443:     */     }
/*  444: 583 */     return results;
/*  445:     */   }
/*  446:     */   
/*  447:     */   public static char[][] splitByChars(char[] inputArray, char... delims)
/*  448:     */   {
/*  449: 590 */     char[][] results = new char[16][];
/*  450:     */     
/*  451: 592 */     int resultIndex = 0;
/*  452: 593 */     int startCurrentLineIndex = 0;
/*  453: 594 */     int currentLineLength = 1;
/*  454:     */     
/*  455:     */ 
/*  456: 597 */     char c = '\000';
/*  457: 598 */     int index = 0;
/*  458: 603 */     for (; index < inputArray.length; currentLineLength++)
/*  459:     */     {
/*  460: 605 */       c = inputArray[index];
/*  461: 608 */       for (int j = 0; j < delims.length; j++)
/*  462:     */       {
/*  463: 609 */         char split = delims[j];
/*  464: 610 */         if (c == split)
/*  465:     */         {
/*  466: 612 */           if (resultIndex == results.length) {
/*  467: 614 */             results = _grow(results);
/*  468:     */           }
/*  469: 618 */           results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  470:     */           
/*  471: 620 */           startCurrentLineIndex = index + 1;
/*  472:     */           
/*  473: 622 */           currentLineLength = 0;
/*  474: 623 */           resultIndex++;
/*  475: 624 */           break;
/*  476:     */         }
/*  477:     */       }
/*  478: 603 */       index++;
/*  479:     */     }
/*  480: 629 */     if (!Chr.in(c, delims))
/*  481:     */     {
/*  482: 631 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  483:     */       
/*  484: 633 */       resultIndex++;
/*  485:     */     }
/*  486: 637 */     int actualLength = resultIndex;
/*  487: 638 */     if (actualLength < results.length)
/*  488:     */     {
/*  489: 639 */       int newSize = results.length - actualLength;
/*  490: 640 */       results = __shrink(results, newSize);
/*  491:     */     }
/*  492: 642 */     return results;
/*  493:     */   }
/*  494:     */   
/*  495:     */   public static char[][] splitByCharsFromToDelims(char[] inputArray, int from, int to, char... delims)
/*  496:     */   {
/*  497: 648 */     char[][] results = new char[16][];
/*  498:     */     
/*  499: 650 */     int length = to - from;
/*  500:     */     
/*  501: 652 */     int resultIndex = 0;
/*  502: 653 */     int startCurrentLineIndex = 0;
/*  503: 654 */     int currentLineLength = 1;
/*  504:     */     
/*  505:     */ 
/*  506: 657 */     char c = '\000';
/*  507: 658 */     int index = from;
/*  508: 663 */     for (; index < length; currentLineLength++)
/*  509:     */     {
/*  510: 665 */       c = inputArray[index];
/*  511: 668 */       for (int j = 0; j < delims.length; j++)
/*  512:     */       {
/*  513: 669 */         char split = delims[j];
/*  514: 670 */         if (c == split)
/*  515:     */         {
/*  516: 672 */           if (resultIndex == results.length) {
/*  517: 674 */             results = _grow(results);
/*  518:     */           }
/*  519: 678 */           results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  520:     */           
/*  521: 680 */           startCurrentLineIndex = index + 1;
/*  522:     */           
/*  523: 682 */           currentLineLength = 0;
/*  524: 683 */           resultIndex++;
/*  525: 684 */           break;
/*  526:     */         }
/*  527:     */       }
/*  528: 663 */       index++;
/*  529:     */     }
/*  530: 689 */     if (!Chr.in(c, delims))
/*  531:     */     {
/*  532: 691 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/*  533:     */       
/*  534: 693 */       resultIndex++;
/*  535:     */     }
/*  536: 697 */     int actualLength = resultIndex;
/*  537: 698 */     if (actualLength < results.length)
/*  538:     */     {
/*  539: 699 */       int newSize = results.length - actualLength;
/*  540: 700 */       results = __shrink(results, newSize);
/*  541:     */     }
/*  542: 702 */     return results;
/*  543:     */   }
/*  544:     */   
/*  545:     */   public static char[][] splitByCharsNoneEmpty(char[] inputArray, char... delims)
/*  546:     */   {
/*  547: 708 */     char[][] results = splitByChars(inputArray, delims);
/*  548: 709 */     return compact(results);
/*  549:     */   }
/*  550:     */   
/*  551:     */   public static char[][] splitByCharsNoneEmpty(char[] inputArray, int from, int to, char... delims)
/*  552:     */   {
/*  553: 716 */     char[][] results = splitByCharsFromToDelims(inputArray, from, to, delims);
/*  554: 717 */     return compact(results);
/*  555:     */   }
/*  556:     */   
/*  557:     */   public static char[][] compact(char[][] array)
/*  558:     */   {
/*  559: 722 */     int nullCount = 0;
/*  560: 723 */     for (char[] ch : array) {
/*  561: 725 */       if ((ch == null) || (ch.length == 0)) {
/*  562: 726 */         nullCount++;
/*  563:     */       }
/*  564:     */     }
/*  565: 729 */     char[][] newArray = new char[array.length - nullCount][];
/*  566:     */     
/*  567: 731 */     int j = 0;
/*  568: 732 */     for (char[] ch : array) {
/*  569: 734 */       if ((ch != null) && (ch.length != 0))
/*  570:     */       {
/*  571: 738 */         newArray[j] = ch;
/*  572: 739 */         j++;
/*  573:     */       }
/*  574:     */     }
/*  575: 741 */     return newArray;
/*  576:     */   }
/*  577:     */   
/*  578:     */   private static char[][] _grow(char[][] array)
/*  579:     */   {
/*  580: 747 */     char[][] newArray = new char[array.length * 2][];
/*  581: 748 */     System.arraycopy(array, 0, newArray, 0, array.length);
/*  582: 749 */     return newArray;
/*  583:     */   }
/*  584:     */   
/*  585:     */   private static char[][] __shrink(char[][] array, int size)
/*  586:     */   {
/*  587: 753 */     char[][] newArray = new char[array.length - size][];
/*  588:     */     
/*  589: 755 */     System.arraycopy(array, 0, (char[][])newArray, 0, array.length - size);
/*  590: 756 */     return newArray;
/*  591:     */   }
/*  592:     */   
/*  593: 760 */   static final String MIN_LONG_STR_NO_SIGN = String.valueOf(-9223372036854775808L);
/*  594: 761 */   static final String MAX_LONG_STR = String.valueOf(9223372036854775807L);
/*  595: 764 */   static final String MIN_INT_STR_NO_SIGN = String.valueOf(-2147483648);
/*  596: 765 */   static final String MAX_INT_STR = String.valueOf(2147483647);
/*  597:     */   
/*  598:     */   public static boolean isLong(char[] digitChars)
/*  599:     */   {
/*  600: 772 */     return isLong(digitChars, 0, digitChars.length);
/*  601:     */   }
/*  602:     */   
/*  603:     */   public static boolean isLong(char[] digitChars, int offset, int len)
/*  604:     */   {
/*  605: 777 */     String cmpStr = digitChars[offset] == '-' ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
/*  606: 778 */     int cmpLen = cmpStr.length();
/*  607: 779 */     if (len < cmpLen) {
/*  608: 779 */       return true;
/*  609:     */     }
/*  610: 780 */     if (len > cmpLen) {
/*  611: 780 */       return false;
/*  612:     */     }
/*  613: 782 */     for (int i = 0; i < cmpLen; i++)
/*  614:     */     {
/*  615: 783 */       int diff = digitChars[(offset + i)] - cmpStr.charAt(i);
/*  616: 784 */       if (diff != 0) {
/*  617: 785 */         return diff < 0;
/*  618:     */       }
/*  619:     */     }
/*  620: 788 */     return true;
/*  621:     */   }
/*  622:     */   
/*  623:     */   public static boolean isInteger(char[] digitChars)
/*  624:     */   {
/*  625: 795 */     return isInteger(digitChars, 0, digitChars.length);
/*  626:     */   }
/*  627:     */   
/*  628:     */   public static boolean isInteger(char[] digitChars, int offset, int len)
/*  629:     */   {
/*  630: 801 */     String cmpStr = digitChars[offset] == '-' ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
/*  631: 802 */     int cmpLen = cmpStr.length();
/*  632: 803 */     if (len < cmpLen) {
/*  633: 803 */       return true;
/*  634:     */     }
/*  635: 804 */     if (len > cmpLen) {
/*  636: 804 */       return false;
/*  637:     */     }
/*  638: 806 */     for (int i = 0; i < cmpLen; i++)
/*  639:     */     {
/*  640: 807 */       int diff = digitChars[(offset + i)] - cmpStr.charAt(i);
/*  641: 808 */       if (diff != 0) {
/*  642: 809 */         return diff < 0;
/*  643:     */       }
/*  644:     */     }
/*  645: 812 */     return true;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static int parseInt(char[] digitChars)
/*  649:     */   {
/*  650: 816 */     return parseInt(digitChars, 0, digitChars.length);
/*  651:     */   }
/*  652:     */   
/*  653:     */   public static int parseInt(char[] digitChars, int offset, int to)
/*  654:     */   {
/*  655:     */     try
/*  656:     */     {
/*  657: 828 */       boolean negative = false;
/*  658: 829 */       char c = digitChars[offset];
/*  659: 830 */       if (c == '-')
/*  660:     */       {
/*  661: 831 */         offset++;
/*  662: 832 */         negative = true;
/*  663:     */       }
/*  664:     */       int num;
/*  665: 834 */       if (negative)
/*  666:     */       {
/*  667: 835 */         int num = digitChars[offset] - '0';
/*  668: 836 */         offset++;
/*  669: 836 */         if (offset < to)
/*  670:     */         {
/*  671: 837 */           num = num * 10 + (digitChars[offset] - '0');
/*  672: 838 */           offset++;
/*  673: 838 */           if (offset < to)
/*  674:     */           {
/*  675: 839 */             num = num * 10 + (digitChars[offset] - '0');
/*  676: 840 */             offset++;
/*  677: 840 */             if (offset < to)
/*  678:     */             {
/*  679: 841 */               num = num * 10 + (digitChars[offset] - '0');
/*  680: 842 */               offset++;
/*  681: 842 */               if (offset < to)
/*  682:     */               {
/*  683: 843 */                 num = num * 10 + (digitChars[offset] - '0');
/*  684: 844 */                 offset++;
/*  685: 844 */                 if (offset < to)
/*  686:     */                 {
/*  687: 845 */                   num = num * 10 + (digitChars[offset] - '0');
/*  688: 846 */                   offset++;
/*  689: 846 */                   if (offset < to)
/*  690:     */                   {
/*  691: 847 */                     num = num * 10 + (digitChars[offset] - '0');
/*  692: 848 */                     offset++;
/*  693: 848 */                     if (offset < to)
/*  694:     */                     {
/*  695: 849 */                       num = num * 10 + (digitChars[offset] - '0');
/*  696: 850 */                       offset++;
/*  697: 850 */                       if (offset < to)
/*  698:     */                       {
/*  699: 851 */                         num = num * 10 + (digitChars[offset] - '0');
/*  700: 852 */                         offset++;
/*  701: 852 */                         if (offset < to) {
/*  702: 853 */                           num = num * 10 + (digitChars[offset] - '0');
/*  703:     */                         }
/*  704:     */                       }
/*  705:     */                     }
/*  706:     */                   }
/*  707:     */                 }
/*  708:     */               }
/*  709:     */             }
/*  710:     */           }
/*  711:     */         }
/*  712:     */       }
/*  713:     */       else
/*  714:     */       {
/*  715: 864 */         num = digitChars[offset] - '0';
/*  716: 865 */         offset++;
/*  717: 865 */         if (offset < to)
/*  718:     */         {
/*  719: 866 */           num = num * 10 + (digitChars[offset] - '0');
/*  720: 867 */           offset++;
/*  721: 867 */           if (offset < to)
/*  722:     */           {
/*  723: 868 */             num = num * 10 + (digitChars[offset] - '0');
/*  724: 869 */             offset++;
/*  725: 869 */             if (offset < to)
/*  726:     */             {
/*  727: 870 */               num = num * 10 + (digitChars[offset] - '0');
/*  728: 871 */               offset++;
/*  729: 871 */               if (offset < to)
/*  730:     */               {
/*  731: 872 */                 num = num * 10 + (digitChars[offset] - '0');
/*  732: 873 */                 offset++;
/*  733: 873 */                 if (offset < to)
/*  734:     */                 {
/*  735: 874 */                   num = num * 10 + (digitChars[offset] - '0');
/*  736: 875 */                   offset++;
/*  737: 875 */                   if (offset < to)
/*  738:     */                   {
/*  739: 876 */                     num = num * 10 + (digitChars[offset] - '0');
/*  740: 877 */                     offset++;
/*  741: 877 */                     if (offset < to)
/*  742:     */                     {
/*  743: 878 */                       num = num * 10 + (digitChars[offset] - '0');
/*  744: 879 */                       offset++;
/*  745: 879 */                       if (offset < to)
/*  746:     */                       {
/*  747: 880 */                         num = num * 10 + (digitChars[offset] - '0');
/*  748: 881 */                         offset++;
/*  749: 881 */                         if (offset < to) {
/*  750: 882 */                           num = num * 10 + (digitChars[offset] - '0');
/*  751:     */                         }
/*  752:     */                       }
/*  753:     */                     }
/*  754:     */                   }
/*  755:     */                 }
/*  756:     */               }
/*  757:     */             }
/*  758:     */           }
/*  759:     */         }
/*  760:     */       }
/*  761: 894 */       return negative ? num * -1 : num;
/*  762:     */     }
/*  763:     */     catch (Exception ex)
/*  764:     */     {
/*  765: 896 */       return ((Integer)Exceptions.handle(Integer.TYPE, ex)).intValue();
/*  766:     */     }
/*  767:     */   }
/*  768:     */   
/*  769:     */   public static int parseIntFromToIgnoreDot(char[] digitChars, int offset, int to)
/*  770:     */   {
/*  771: 904 */     boolean negative = false;
/*  772: 905 */     char c = digitChars[offset];
/*  773: 906 */     if (c == '-')
/*  774:     */     {
/*  775: 907 */       offset++;
/*  776: 908 */       negative = true;
/*  777:     */     }
/*  778: 911 */     c = digitChars[offset];
/*  779: 912 */     int num = c - '0';
/*  780: 913 */     offset++;
/*  781: 915 */     for (; offset < to; offset++)
/*  782:     */     {
/*  783: 916 */       c = digitChars[offset];
/*  784: 917 */       if (c != '.') {
/*  785: 918 */         num = num * 10 + (c - '0');
/*  786:     */       }
/*  787:     */     }
/*  788: 923 */     return negative ? num * -1 : num;
/*  789:     */   }
/*  790:     */   
/*  791:     */   public static long parseLongFromToIgnoreDot(char[] digitChars, int offset, int to)
/*  792:     */   {
/*  793: 930 */     boolean negative = false;
/*  794: 931 */     char c = digitChars[offset];
/*  795: 932 */     if (c == '-')
/*  796:     */     {
/*  797: 933 */       offset++;
/*  798: 934 */       negative = true;
/*  799:     */     }
/*  800: 937 */     c = digitChars[offset];
/*  801: 938 */     long num = c - '0';
/*  802: 939 */     offset++;
/*  803: 941 */     for (; offset < to; offset++)
/*  804:     */     {
/*  805: 942 */       c = digitChars[offset];
/*  806: 943 */       if (c != '.') {
/*  807: 944 */         num = num * 10L + (c - '0');
/*  808:     */       }
/*  809:     */     }
/*  810: 949 */     return negative ? num * -1L : num;
/*  811:     */   }
/*  812:     */   
/*  813:     */   public static long parseLong(char[] digitChars, int offset, int to)
/*  814:     */   {
/*  815: 956 */     boolean negative = false;
/*  816: 957 */     char c = digitChars[offset];
/*  817: 958 */     if (c == '-')
/*  818:     */     {
/*  819: 959 */       offset++;
/*  820: 960 */       negative = true;
/*  821:     */     }
/*  822: 963 */     c = digitChars[offset];
/*  823: 964 */     long num = c - '0';
/*  824: 965 */     offset++;
/*  825: 969 */     for (; offset < to; offset++)
/*  826:     */     {
/*  827: 970 */       c = digitChars[offset];
/*  828: 971 */       long digit = c - '0';
/*  829: 972 */       num = num * 10L + digit;
/*  830:     */     }
/*  831: 975 */     return negative ? num * -1L : num;
/*  832:     */   }
/*  833:     */   
/*  834:     */   public static long parseLong(char[] digitChars)
/*  835:     */   {
/*  836: 981 */     return parseLong(digitChars, 0, digitChars.length);
/*  837:     */   }
/*  838:     */   
/*  839:     */   public static Number parseJsonNumber(char[] buffer)
/*  840:     */   {
/*  841: 987 */     return parseJsonNumber(buffer, 0, buffer.length);
/*  842:     */   }
/*  843:     */   
/*  844:     */   public static Number parseJsonNumber(char[] buffer, int from, int to)
/*  845:     */   {
/*  846: 993 */     return parseJsonNumber(buffer, from, to, null);
/*  847:     */   }
/*  848:     */   
/*  849:     */   public static final boolean isNumberDigit(int c)
/*  850:     */   {
/*  851: 999 */     return (c >= 48) && (c <= 57);
/*  852:     */   }
/*  853:     */   
/*  854:     */   public static boolean isDelimiter(int c)
/*  855:     */   {
/*  856:1006 */     return (c == 44) || (c == 125) || (c == 93);
/*  857:     */   }
/*  858:     */   
/*  859:     */   public static Number parseJsonNumber(char[] buffer, int from, int max, int[] size)
/*  860:     */   {
/*  861:1010 */     Number value = null;
/*  862:1011 */     boolean simple = true;
/*  863:1012 */     int digitsPastPoint = 0;
/*  864:     */     
/*  865:1014 */     int index = from;
/*  866:1017 */     if (buffer[index] == '-') {
/*  867:1018 */       index++;
/*  868:     */     }
/*  869:1021 */     boolean foundDot = false;
/*  870:1022 */     for (; index < max; index++)
/*  871:     */     {
/*  872:1023 */       char ch = buffer[index];
/*  873:1024 */       if (isNumberDigit(ch))
/*  874:     */       {
/*  875:1026 */         if (foundDot == true) {
/*  876:1027 */           digitsPastPoint++;
/*  877:     */         }
/*  878:     */       }
/*  879:     */       else
/*  880:     */       {
/*  881:1029 */         if ((ch <= ' ') || (isDelimiter(ch))) {
/*  882:     */           break;
/*  883:     */         }
/*  884:1030 */         if (ch == '.') {
/*  885:1031 */           foundDot = true;
/*  886:1033 */         } else if ((ch == 'E') || (ch == 'e') || (ch == '-') || (ch == '+')) {
/*  887:1034 */           simple = false;
/*  888:     */         } else {
/*  889:1036 */           Exceptions.die("unexpected character " + ch);
/*  890:     */         }
/*  891:     */       }
/*  892:     */     }
/*  893:1041 */     if (digitsPastPoint >= powersOf10.length - 1) {
/*  894:1042 */       simple = false;
/*  895:     */     }
/*  896:1046 */     int length = index - from;
/*  897:1048 */     if ((!foundDot) && (simple))
/*  898:     */     {
/*  899:1049 */       if (isInteger(buffer, from, length)) {
/*  900:1050 */         value = Integer.valueOf(parseInt(buffer, from, index));
/*  901:     */       } else {
/*  902:1052 */         value = Long.valueOf(parseLong(buffer, from, index));
/*  903:     */       }
/*  904:     */     }
/*  905:1055 */     else if ((foundDot) && (simple))
/*  906:     */     {
/*  907:1059 */       if (length < powersOf10.length)
/*  908:     */       {
/*  909:     */         long lvalue;
/*  910:     */         long lvalue;
/*  911:1061 */         if (isInteger(buffer, from, length)) {
/*  912:1062 */           lvalue = parseIntFromToIgnoreDot(buffer, from, index);
/*  913:     */         } else {
/*  914:1064 */           lvalue = parseLongFromToIgnoreDot(buffer, from, index);
/*  915:     */         }
/*  916:1067 */         double power = powersOf10[digitsPastPoint];
/*  917:1068 */         value = Double.valueOf(lvalue / power);
/*  918:     */       }
/*  919:     */       else
/*  920:     */       {
/*  921:1071 */         value = Double.valueOf(Double.parseDouble(new String(buffer, from, length)));
/*  922:     */       }
/*  923:     */     }
/*  924:     */     else {
/*  925:1077 */       value = Double.valueOf(Double.parseDouble(new String(buffer, from, index - from)));
/*  926:     */     }
/*  927:1081 */     if (size != null) {
/*  928:1082 */       size[0] = index;
/*  929:     */     }
/*  930:1085 */     return value;
/*  931:     */   }
/*  932:     */   
/*  933:     */   public static float parseFloat(char[] buffer, int from, int to)
/*  934:     */   {
/*  935:1090 */     return (float)parseDouble(buffer, from, to);
/*  936:     */   }
/*  937:     */   
/*  938:     */   public static float parseFloat(char[] buffer)
/*  939:     */   {
/*  940:1094 */     return (float)parseDouble(buffer, 0, buffer.length);
/*  941:     */   }
/*  942:     */   
/*  943:     */   public static double parseDouble(char[] buffer)
/*  944:     */   {
/*  945:1099 */     return parseDouble(buffer, 0, buffer.length);
/*  946:     */   }
/*  947:     */   
/*  948:     */   public static double parseDouble(char[] buffer, int from, int to)
/*  949:     */   {
/*  950:1104 */     boolean simple = true;
/*  951:1105 */     int digitsPastPoint = 0;
/*  952:     */     
/*  953:1107 */     int index = from;
/*  954:1108 */     boolean negative = false;
/*  955:1112 */     if (buffer[index] == '-')
/*  956:     */     {
/*  957:1113 */       index++;
/*  958:1114 */       negative = true;
/*  959:     */     }
/*  960:1117 */     boolean foundDot = false;
/*  961:1118 */     for (; index < to; index++)
/*  962:     */     {
/*  963:1119 */       char ch = buffer[index];
/*  964:1120 */       if (isNumberDigit(ch))
/*  965:     */       {
/*  966:1122 */         if (foundDot == true) {
/*  967:1123 */           digitsPastPoint++;
/*  968:     */         }
/*  969:     */       }
/*  970:1125 */       else if (ch == '.') {
/*  971:1126 */         foundDot = true;
/*  972:1128 */       } else if ((ch == 'E') || (ch == 'e') || (ch == '-') || (ch == '+')) {
/*  973:1129 */         simple = false;
/*  974:     */       } else {
/*  975:1131 */         Exceptions.die("unexpected character " + ch);
/*  976:     */       }
/*  977:     */     }
/*  978:1136 */     if (digitsPastPoint >= powersOf10.length - 1) {
/*  979:1137 */       simple = false;
/*  980:     */     }
/*  981:1141 */     int length = index - from;
/*  982:     */     double value;
/*  983:     */     double value;
/*  984:1143 */     if ((!foundDot) && (simple))
/*  985:     */     {
/*  986:     */       double value;
/*  987:1144 */       if (isInteger(buffer, from, length)) {
/*  988:1145 */         value = parseInt(buffer, from, index);
/*  989:     */       } else {
/*  990:1147 */         value = parseLong(buffer, from, index);
/*  991:     */       }
/*  992:     */     }
/*  993:     */     else
/*  994:     */     {
/*  995:     */       double value;
/*  996:1150 */       if ((foundDot) && (simple))
/*  997:     */       {
/*  998:     */         double value;
/*  999:1154 */         if (length < powersOf10.length)
/* 1000:     */         {
/* 1001:     */           long lvalue;
/* 1002:     */           long lvalue;
/* 1003:1156 */           if (isInteger(buffer, from, length)) {
/* 1004:1157 */             lvalue = parseIntFromToIgnoreDot(buffer, from, index);
/* 1005:     */           } else {
/* 1006:1159 */             lvalue = parseLongFromToIgnoreDot(buffer, from, index);
/* 1007:     */           }
/* 1008:1162 */           double power = powersOf10[digitsPastPoint];
/* 1009:1163 */           value = lvalue / power;
/* 1010:     */         }
/* 1011:     */         else
/* 1012:     */         {
/* 1013:1166 */           value = Double.parseDouble(new String(buffer, from, length));
/* 1014:     */         }
/* 1015:     */       }
/* 1016:     */       else
/* 1017:     */       {
/* 1018:1172 */         value = Double.parseDouble(new String(buffer, from, index - from));
/* 1019:     */       }
/* 1020:     */     }
/* 1021:1177 */     if ((value == 0.0D) && (negative)) {
/* 1022:1178 */       return -0.0D;
/* 1023:     */     }
/* 1024:1180 */     return value;
/* 1025:     */   }
/* 1026:     */   
/* 1027:1184 */   private static double[] powersOf10 = { 1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 10000000.0D, 100000000.0D, 1000000000.0D, 10000000000.0D, 100000000000.0D, 1000000000000.0D, 10000000000000.0D, 100000000000000.0D, 1000000000000000.0D, 10000000000000000.0D, 1.0E+017D, 1.0E+018D };
/* 1028:     */   
/* 1029:     */   public static int skipWhiteSpace(char[] array)
/* 1030:     */   {
/* 1031:1210 */     for (int index = 0; index < array.length; index++)
/* 1032:     */     {
/* 1033:1212 */       int c = array[index];
/* 1034:1213 */       if (c > 32) {
/* 1035:1215 */         return index;
/* 1036:     */       }
/* 1037:     */     }
/* 1038:1218 */     return index;
/* 1039:     */   }
/* 1040:     */   
/* 1041:     */   public static int skipWhiteSpace(char[] array, int index)
/* 1042:     */   {
/* 1043:1223 */     for (; index < array.length; index++)
/* 1044:     */     {
/* 1045:1224 */       int c = array[index];
/* 1046:1225 */       if (c > 32) {
/* 1047:1227 */         return index;
/* 1048:     */       }
/* 1049:     */     }
/* 1050:1230 */     return index;
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public static int skipWhiteSpace(char[] array, int index, int length)
/* 1054:     */   {
/* 1055:1238 */     for (; index < length; index++)
/* 1056:     */     {
/* 1057:1239 */       int c = array[index];
/* 1058:1240 */       if (c > 32) {
/* 1059:1242 */         return index;
/* 1060:     */       }
/* 1061:     */     }
/* 1062:1245 */     return index;
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   public static char[] readNumber(char[] array, int idx)
/* 1066:     */   {
/* 1067:1249 */     int startIndex = idx;
/* 1068:1252 */     while (isDecimalDigit(array[idx]))
/* 1069:     */     {
/* 1070:1255 */       idx++;
/* 1071:1256 */       if (idx >= array.length) {
/* 1072:     */         break;
/* 1073:     */       }
/* 1074:     */     }
/* 1075:1260 */     return Arrays.copyOfRange(array, startIndex, idx);
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public static char[] readNumber(char[] array, int idx, int len)
/* 1079:     */   {
/* 1080:1268 */     int startIndex = idx;
/* 1081:1271 */     while (isDecimalDigit(array[idx]))
/* 1082:     */     {
/* 1083:1274 */       idx++;
/* 1084:1275 */       if (idx >= len) {
/* 1085:     */         break;
/* 1086:     */       }
/* 1087:     */     }
/* 1088:1279 */     return Arrays.copyOfRange(array, startIndex, idx);
/* 1089:     */   }
/* 1090:     */   
/* 1091:     */   public static int skipWhiteSpaceFast(char[] array)
/* 1092:     */   {
/* 1093:1293 */     for (int index = 0; index < array.length; index++)
/* 1094:     */     {
/* 1095:1295 */       int c = array[index];
/* 1096:1296 */       if (c > 32) {
/* 1097:1298 */         return index;
/* 1098:     */       }
/* 1099:     */     }
/* 1100:1301 */     return index;
/* 1101:     */   }
/* 1102:     */   
/* 1103:     */   public static int skipWhiteSpaceFast(char[] array, int index)
/* 1104:     */   {
/* 1105:1308 */     for (; index < array.length; index++)
/* 1106:     */     {
/* 1107:1309 */       char c = array[index];
/* 1108:1310 */       if (c > ' ') {
/* 1109:1312 */         return index;
/* 1110:     */       }
/* 1111:     */     }
/* 1112:1315 */     return index - 1;
/* 1113:     */   }
/* 1114:     */   
/* 1115:     */   public static String errorDetails(String message, char[] array, int index, int ch)
/* 1116:     */   {
/* 1117:1319 */     CharBuf buf = CharBuf.create(255);
/* 1118:     */     
/* 1119:1321 */     buf.addLine(message);
/* 1120:     */     
/* 1121:     */ 
/* 1122:1324 */     buf.addLine("");
/* 1123:1325 */     buf.addLine("The current character read is " + debugCharDescription(ch));
/* 1124:     */     
/* 1125:     */ 
/* 1126:1328 */     buf.addLine(message);
/* 1127:     */     
/* 1128:1330 */     int line = 0;
/* 1129:1331 */     int lastLineIndex = 0;
/* 1130:1333 */     for (int i = 0; (i < index) && (i < array.length); i++) {
/* 1131:1334 */       if (array[i] == '\n')
/* 1132:     */       {
/* 1133:1335 */         line++;
/* 1134:1336 */         lastLineIndex = i + 1;
/* 1135:     */       }
/* 1136:     */     }
/* 1137:1340 */     int count = 0;
/* 1138:1342 */     for (int i = lastLineIndex; i < array.length; count++)
/* 1139:     */     {
/* 1140:1343 */       if (array[i] == '\n') {
/* 1141:     */         break;
/* 1142:     */       }
/* 1143:1342 */       i++;
/* 1144:     */     }
/* 1145:1349 */     buf.addLine("line number " + (line + 1));
/* 1146:1350 */     buf.addLine("index number " + index);
/* 1147:     */     try
/* 1148:     */     {
/* 1149:1354 */       buf.addLine(new String(array, lastLineIndex, count));
/* 1150:     */     }
/* 1151:     */     catch (Exception ex)
/* 1152:     */     {
/* 1153:     */       try
/* 1154:     */       {
/* 1155:1358 */         int start = index -= 10;
/* 1156:     */         
/* 1157:1360 */         buf.addLine(new String(array, start, index));
/* 1158:     */       }
/* 1159:     */       catch (Exception ex2)
/* 1160:     */       {
/* 1161:1362 */         buf.addLine(new String(array, 0, array.length));
/* 1162:     */       }
/* 1163:     */     }
/* 1164:1365 */     for (int i = 0; i < index - lastLineIndex; i++) {
/* 1165:1366 */       buf.add('.');
/* 1166:     */     }
/* 1167:1368 */     buf.add('^');
/* 1168:     */     
/* 1169:1370 */     return buf.toString();
/* 1170:     */   }
/* 1171:     */   
/* 1172:     */   public static String debugCharDescription(int c)
/* 1173:     */   {
/* 1174:     */     String charString;
/* 1175:1376 */     if (c == 32)
/* 1176:     */     {
/* 1177:1377 */       charString = "[SPACE]";
/* 1178:     */     }
/* 1179:     */     else
/* 1180:     */     {
/* 1181:     */       String charString;
/* 1182:1378 */       if (c == 9)
/* 1183:     */       {
/* 1184:1379 */         charString = "[TAB]";
/* 1185:     */       }
/* 1186:     */       else
/* 1187:     */       {
/* 1188:     */         String charString;
/* 1189:1381 */         if (c == 10) {
/* 1190:1382 */           charString = "[NEWLINE]";
/* 1191:     */         } else {
/* 1192:1385 */           charString = "'" + (char)c + "'";
/* 1193:     */         }
/* 1194:     */       }
/* 1195:     */     }
/* 1196:1388 */     String charString = charString + " with an int value of " + c;
/* 1197:1389 */     return charString;
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public static final boolean isDoubleQuote(int c)
/* 1201:     */   {
/* 1202:1395 */     return c == 34;
/* 1203:     */   }
/* 1204:     */   
/* 1205:     */   public static final boolean isEscape(int c)
/* 1206:     */   {
/* 1207:1400 */     return c == 92;
/* 1208:     */   }
/* 1209:     */   
/* 1210:     */   public static boolean hasEscapeChar(char[] array, int index, int[] indexHolder)
/* 1211:     */   {
/* 1212:1407 */     for (; index < array.length; index++)
/* 1213:     */     {
/* 1214:1408 */       char currentChar = array[index];
/* 1215:1409 */       if (isDoubleQuote(currentChar))
/* 1216:     */       {
/* 1217:1410 */         indexHolder[0] = index;
/* 1218:1411 */         return false;
/* 1219:     */       }
/* 1220:1412 */       if (isEscape(currentChar))
/* 1221:     */       {
/* 1222:1413 */         indexHolder[0] = index;
/* 1223:1414 */         return true;
/* 1224:     */       }
/* 1225:     */     }
/* 1226:1419 */     indexHolder[0] = index;
/* 1227:1420 */     return false;
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public static int findEndQuote(char[] array, int index)
/* 1231:     */   {
/* 1232:1428 */     boolean escape = false;
/* 1233:1430 */     for (; index < array.length; index++)
/* 1234:     */     {
/* 1235:1431 */       char currentChar = array[index];
/* 1236:1432 */       if ((isDoubleQuote(currentChar)) && 
/* 1237:1433 */         (!escape)) {
/* 1238:     */         break;
/* 1239:     */       }
/* 1240:1437 */       if (isEscape(currentChar))
/* 1241:     */       {
/* 1242:1438 */         if (!escape) {
/* 1243:1439 */           escape = true;
/* 1244:     */         } else {
/* 1245:1441 */           escape = false;
/* 1246:     */         }
/* 1247:     */       }
/* 1248:     */       else {
/* 1249:1444 */         escape = false;
/* 1250:     */       }
/* 1251:     */     }
/* 1252:1447 */     return index;
/* 1253:     */   }
/* 1254:     */   
/* 1255:     */   public static char[][] splitComma(char[] inputArray)
/* 1256:     */   {
/* 1257:1454 */     char[][] results = new char[16][];
/* 1258:     */     
/* 1259:1456 */     int resultIndex = 0;
/* 1260:1457 */     int startCurrentLineIndex = 0;
/* 1261:1458 */     int currentLineLength = 1;
/* 1262:     */     
/* 1263:     */ 
/* 1264:1461 */     int c = 0;
/* 1265:1462 */     int index = 0;
/* 1266:1464 */     for (; index < inputArray.length; currentLineLength++)
/* 1267:     */     {
/* 1268:1465 */       c = inputArray[index];
/* 1269:1466 */       if (c == 44)
/* 1270:     */       {
/* 1271:1468 */         if (resultIndex == results.length) {
/* 1272:1470 */           results = _grow(results);
/* 1273:     */         }
/* 1274:1474 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/* 1275:     */         
/* 1276:1476 */         startCurrentLineIndex = index + 1;
/* 1277:     */         
/* 1278:1478 */         currentLineLength = 0;
/* 1279:1479 */         resultIndex++;
/* 1280:     */       }
/* 1281:1464 */       index++;
/* 1282:     */     }
/* 1283:1483 */     if (c != 44)
/* 1284:     */     {
/* 1285:1485 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/* 1286:     */       
/* 1287:1487 */       resultIndex++;
/* 1288:     */     }
/* 1289:1490 */     int actualLength = resultIndex;
/* 1290:1491 */     if (actualLength < results.length)
/* 1291:     */     {
/* 1292:1492 */       int newSize = results.length - actualLength;
/* 1293:1493 */       results = __shrink(results, newSize);
/* 1294:     */     }
/* 1295:1495 */     return results;
/* 1296:     */   }
/* 1297:     */   
/* 1298:     */   public static char[][] splitBySpace(char[] inputArray)
/* 1299:     */   {
/* 1300:1501 */     return splitByChar(32, inputArray);
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public static char[][] splitByChar(int splitChar, char[] inputArray)
/* 1304:     */   {
/* 1305:1507 */     char[][] results = new char[16][];
/* 1306:     */     
/* 1307:1509 */     int resultIndex = 0;
/* 1308:1510 */     int startCurrentLineIndex = 0;
/* 1309:1511 */     int currentLineLength = 1;
/* 1310:     */     
/* 1311:1513 */     int SPLIT_CHAR = splitChar;
/* 1312:     */     
/* 1313:1515 */     int c = 0;
/* 1314:1516 */     int index = 0;
/* 1315:1518 */     for (; index < inputArray.length; currentLineLength++)
/* 1316:     */     {
/* 1317:1519 */       c = inputArray[index];
/* 1318:1520 */       if (c == SPLIT_CHAR)
/* 1319:     */       {
/* 1320:1522 */         if (resultIndex == results.length) {
/* 1321:1524 */           results = _grow(results);
/* 1322:     */         }
/* 1323:1528 */         results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/* 1324:     */         
/* 1325:1530 */         startCurrentLineIndex = index + 1;
/* 1326:     */         
/* 1327:1532 */         currentLineLength = 0;
/* 1328:1533 */         resultIndex++;
/* 1329:     */       }
/* 1330:1518 */       index++;
/* 1331:     */     }
/* 1332:1537 */     if (c != SPLIT_CHAR)
/* 1333:     */     {
/* 1334:1539 */       results[resultIndex] = Chr.copy(inputArray, startCurrentLineIndex, currentLineLength - 1);
/* 1335:     */       
/* 1336:1541 */       resultIndex++;
/* 1337:     */     }
/* 1338:1544 */     int actualLength = resultIndex;
/* 1339:1545 */     if (actualLength < results.length)
/* 1340:     */     {
/* 1341:1546 */       int newSize = results.length - actualLength;
/* 1342:1547 */       results = __shrink(results, newSize);
/* 1343:     */     }
/* 1344:1549 */     return results;
/* 1345:     */   }
/* 1346:     */   
/* 1347:     */   public static int findChar(char c, char[] line)
/* 1348:     */   {
/* 1349:1554 */     int idx = -1;
/* 1350:1555 */     for (int index = 0; index < line.length; index++) {
/* 1351:1556 */       if (line[index] == c)
/* 1352:     */       {
/* 1353:1556 */         idx = index; break;
/* 1354:     */       }
/* 1355:     */     }
/* 1356:1558 */     return idx;
/* 1357:     */   }
/* 1358:     */   
/* 1359:     */   public static int findWhiteSpace(char[] line)
/* 1360:     */   {
/* 1361:1562 */     int idx = -1;
/* 1362:1563 */     for (int index = 0; index < line.length; index++)
/* 1363:     */     {
/* 1364:1565 */       char c = line[index];
/* 1365:1567 */       switch (c)
/* 1366:     */       {
/* 1367:     */       case '\t': 
/* 1368:     */       case '\n': 
/* 1369:     */       case '\r': 
/* 1370:     */       case ' ': 
/* 1371:1572 */         return index;
/* 1372:     */       }
/* 1373:     */     }
/* 1374:1576 */     return idx;
/* 1375:     */   }
/* 1376:     */   
/* 1377:     */   public static int findWhiteSpace(int start, char[] line)
/* 1378:     */   {
/* 1379:1581 */     int idx = -1;
/* 1380:1582 */     for (int index = start; index < line.length; index++)
/* 1381:     */     {
/* 1382:1584 */       char c = line[index];
/* 1383:1586 */       switch (c)
/* 1384:     */       {
/* 1385:     */       case '\t': 
/* 1386:     */       case '\n': 
/* 1387:     */       case '\r': 
/* 1388:     */       case ' ': 
/* 1389:1591 */         return index;
/* 1390:     */       }
/* 1391:     */     }
/* 1392:1595 */     return idx;
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   public static int findChar(char c, int startIndex, char[] line)
/* 1396:     */   {
/* 1397:1600 */     int idx = -1;
/* 1398:1601 */     for (int index = startIndex; index < line.length; index++) {
/* 1399:1602 */       if (line[index] == c)
/* 1400:     */       {
/* 1401:1602 */         idx = index; break;
/* 1402:     */       }
/* 1403:     */     }
/* 1404:1604 */     return idx;
/* 1405:     */   }
/* 1406:     */   
/* 1407:     */   public static int findString(String matchString, int startIndex, char[] line)
/* 1408:     */   {
/* 1409:1609 */     return findChars(FastStringUtils.toCharArray(matchString), startIndex, line);
/* 1410:     */   }
/* 1411:     */   
/* 1412:     */   public static int findString(String matchString, char[] line)
/* 1413:     */   {
/* 1414:1613 */     return findChars(FastStringUtils.toCharArray(matchString), 0, line);
/* 1415:     */   }
/* 1416:     */   
/* 1417:     */   public static int findChars(char[] matchChars, char[] line)
/* 1418:     */   {
/* 1419:1618 */     return findChars(matchChars, 0, line);
/* 1420:     */   }
/* 1421:     */   
/* 1422:     */   public static boolean matchChars(char[] matchChars, int startIndex, char[] line)
/* 1423:     */   {
/* 1424:1623 */     if (line.length - startIndex < matchChars.length) {
/* 1425:1624 */       return false;
/* 1426:     */     }
/* 1427:1627 */     int i = findChar(matchChars[0], startIndex, line);
/* 1428:1629 */     if (i == -1) {
/* 1429:1630 */       return false;
/* 1430:     */     }
/* 1431:1636 */     for (; i < line.length; i++) {
/* 1432:1637 */       for (int j = 0; j < matchChars.length; j++)
/* 1433:     */       {
/* 1434:1638 */         char c = matchChars[j];
/* 1435:1639 */         if (c == line[i])
/* 1436:     */         {
/* 1437:1640 */           if (j + 1 == matchChars.length) {
/* 1438:1641 */             return true;
/* 1439:     */           }
/* 1440:1643 */           i++;
/* 1441:     */         }
/* 1442:     */         else
/* 1443:     */         {
/* 1444:1646 */           i++;
/* 1445:     */           break label92;
/* 1446:     */         }
/* 1447:     */       }
/* 1448:     */     }
/* 1449:     */     label92:
/* 1450:1652 */     return false;
/* 1451:     */   }
/* 1452:     */   
/* 1453:     */   public static int findChars(char[] matchChars, int startIndex, char[] line)
/* 1454:     */   {
/* 1455:1657 */     if (line.length - startIndex < matchChars.length) {
/* 1456:1658 */       return -1;
/* 1457:     */     }
/* 1458:1661 */     int index = findChar(matchChars[0], startIndex, line);
/* 1459:1663 */     if (index == -1) {
/* 1460:1664 */       return -1;
/* 1461:     */     }
/* 1462:1668 */     for (int i = index; i < line.length; i++) {
/* 1463:1672 */       for (int j = 0; j < matchChars.length; j++)
/* 1464:     */       {
/* 1465:1673 */         char c = matchChars[j];
/* 1466:1674 */         if (c == line[i])
/* 1467:     */         {
/* 1468:1676 */           if (j + 1 == matchChars.length) {
/* 1469:1677 */             return index;
/* 1470:     */           }
/* 1471:1679 */           i++;
/* 1472:     */         }
/* 1473:     */         else
/* 1474:     */         {
/* 1475:1682 */           i++;
/* 1476:     */           break label97;
/* 1477:     */         }
/* 1478:     */       }
/* 1479:     */     }
/* 1480:     */     label97:
/* 1481:1688 */     return findChars(matchChars, i, line);
/* 1482:     */   }
/* 1483:     */   
/* 1484:     */   public static char[][] splitByPipe(char[] inputArray)
/* 1485:     */   {
/* 1486:1692 */     return splitByChar(124, inputArray);
/* 1487:     */   }
/* 1488:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.CharScanner
 * JD-Core Version:    0.7.0.1
 */