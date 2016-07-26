/*    1:     */ package org.apache.commons.io;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collection;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.Stack;
/*    8:     */ 
/*    9:     */ public class FilenameUtils
/*   10:     */ {
/*   11:     */   private static final char EXTENSION_SEPARATOR = '.';
/*   12:     */   private static final char UNIX_SEPARATOR = '/';
/*   13:     */   private static final char WINDOWS_SEPARATOR = '\\';
/*   14: 113 */   private static final char SYSTEM_SEPARATOR = File.separatorChar;
/*   15:     */   private static final char OTHER_SEPARATOR;
/*   16:     */   
/*   17:     */   static
/*   18:     */   {
/*   19: 120 */     if (isSystemWindows()) {
/*   20: 121 */       OTHER_SEPARATOR = '/';
/*   21:     */     } else {
/*   22: 123 */       OTHER_SEPARATOR = '\\';
/*   23:     */     }
/*   24:     */   }
/*   25:     */   
/*   26:     */   static boolean isSystemWindows()
/*   27:     */   {
/*   28: 141 */     return SYSTEM_SEPARATOR == '\\';
/*   29:     */   }
/*   30:     */   
/*   31:     */   private static boolean isSeparator(char ch)
/*   32:     */   {
/*   33: 152 */     return (ch == '/') || (ch == '\\');
/*   34:     */   }
/*   35:     */   
/*   36:     */   public static String normalize(String filename)
/*   37:     */   {
/*   38: 197 */     return doNormalize(filename, true);
/*   39:     */   }
/*   40:     */   
/*   41:     */   public static String normalizeNoEndSeparator(String filename)
/*   42:     */   {
/*   43: 243 */     return doNormalize(filename, false);
/*   44:     */   }
/*   45:     */   
/*   46:     */   private static String doNormalize(String filename, boolean keepSeparator)
/*   47:     */   {
/*   48: 254 */     if (filename == null) {
/*   49: 255 */       return null;
/*   50:     */     }
/*   51: 257 */     int size = filename.length();
/*   52: 258 */     if (size == 0) {
/*   53: 259 */       return filename;
/*   54:     */     }
/*   55: 261 */     int prefix = getPrefixLength(filename);
/*   56: 262 */     if (prefix < 0) {
/*   57: 263 */       return null;
/*   58:     */     }
/*   59: 266 */     char[] array = new char[size + 2];
/*   60: 267 */     filename.getChars(0, filename.length(), array, 0);
/*   61: 270 */     for (int i = 0; i < array.length; i++) {
/*   62: 271 */       if (array[i] == OTHER_SEPARATOR) {
/*   63: 272 */         array[i] = SYSTEM_SEPARATOR;
/*   64:     */       }
/*   65:     */     }
/*   66: 277 */     boolean lastIsDirectory = true;
/*   67: 278 */     if (array[(size - 1)] != SYSTEM_SEPARATOR)
/*   68:     */     {
/*   69: 279 */       array[(size++)] = SYSTEM_SEPARATOR;
/*   70: 280 */       lastIsDirectory = false;
/*   71:     */     }
/*   72: 284 */     for (int i = prefix + 1; i < size; i++) {
/*   73: 285 */       if ((array[i] == SYSTEM_SEPARATOR) && (array[(i - 1)] == SYSTEM_SEPARATOR))
/*   74:     */       {
/*   75: 286 */         System.arraycopy(array, i, array, i - 1, size - i);
/*   76: 287 */         size--;
/*   77: 288 */         i--;
/*   78:     */       }
/*   79:     */     }
/*   80: 293 */     for (int i = prefix + 1; i < size; i++) {
/*   81: 294 */       if ((array[i] == SYSTEM_SEPARATOR) && (array[(i - 1)] == '.') && ((i == prefix + 1) || (array[(i - 2)] == SYSTEM_SEPARATOR)))
/*   82:     */       {
/*   83: 296 */         if (i == size - 1) {
/*   84: 297 */           lastIsDirectory = true;
/*   85:     */         }
/*   86: 299 */         System.arraycopy(array, i + 1, array, i - 1, size - i);
/*   87: 300 */         size -= 2;
/*   88: 301 */         i--;
/*   89:     */       }
/*   90:     */     }
/*   91:     */     label455:
/*   92: 307 */     for (int i = prefix + 2; i < size; i++) {
/*   93: 308 */       if ((array[i] == SYSTEM_SEPARATOR) && (array[(i - 1)] == '.') && (array[(i - 2)] == '.') && ((i == prefix + 2) || (array[(i - 3)] == SYSTEM_SEPARATOR)))
/*   94:     */       {
/*   95: 310 */         if (i == prefix + 2) {
/*   96: 311 */           return null;
/*   97:     */         }
/*   98: 313 */         if (i == size - 1) {
/*   99: 314 */           lastIsDirectory = true;
/*  100:     */         }
/*  101: 317 */         for (int j = i - 4; j >= prefix; j--) {
/*  102: 318 */           if (array[j] == SYSTEM_SEPARATOR)
/*  103:     */           {
/*  104: 320 */             System.arraycopy(array, i + 1, array, j + 1, size - i);
/*  105: 321 */             size -= i - j;
/*  106: 322 */             i = j + 1;
/*  107:     */             break label455;
/*  108:     */           }
/*  109:     */         }
/*  110: 327 */         System.arraycopy(array, i + 1, array, prefix, size - i);
/*  111: 328 */         size -= i + 1 - prefix;
/*  112: 329 */         i = prefix + 1;
/*  113:     */       }
/*  114:     */     }
/*  115: 333 */     if (size <= 0) {
/*  116: 334 */       return "";
/*  117:     */     }
/*  118: 336 */     if (size <= prefix) {
/*  119: 337 */       return new String(array, 0, size);
/*  120:     */     }
/*  121: 339 */     if ((lastIsDirectory) && (keepSeparator)) {
/*  122: 340 */       return new String(array, 0, size);
/*  123:     */     }
/*  124: 342 */     return new String(array, 0, size - 1);
/*  125:     */   }
/*  126:     */   
/*  127:     */   public static String concat(String basePath, String fullFilenameToAdd)
/*  128:     */   {
/*  129: 387 */     int prefix = getPrefixLength(fullFilenameToAdd);
/*  130: 388 */     if (prefix < 0) {
/*  131: 389 */       return null;
/*  132:     */     }
/*  133: 391 */     if (prefix > 0) {
/*  134: 392 */       return normalize(fullFilenameToAdd);
/*  135:     */     }
/*  136: 394 */     if (basePath == null) {
/*  137: 395 */       return null;
/*  138:     */     }
/*  139: 397 */     int len = basePath.length();
/*  140: 398 */     if (len == 0) {
/*  141: 399 */       return normalize(fullFilenameToAdd);
/*  142:     */     }
/*  143: 401 */     char ch = basePath.charAt(len - 1);
/*  144: 402 */     if (isSeparator(ch)) {
/*  145: 403 */       return normalize(basePath + fullFilenameToAdd);
/*  146:     */     }
/*  147: 405 */     return normalize(basePath + '/' + fullFilenameToAdd);
/*  148:     */   }
/*  149:     */   
/*  150:     */   public static String separatorsToUnix(String path)
/*  151:     */   {
/*  152: 417 */     if ((path == null) || (path.indexOf('\\') == -1)) {
/*  153: 418 */       return path;
/*  154:     */     }
/*  155: 420 */     return path.replace('\\', '/');
/*  156:     */   }
/*  157:     */   
/*  158:     */   public static String separatorsToWindows(String path)
/*  159:     */   {
/*  160: 430 */     if ((path == null) || (path.indexOf('/') == -1)) {
/*  161: 431 */       return path;
/*  162:     */     }
/*  163: 433 */     return path.replace('/', '\\');
/*  164:     */   }
/*  165:     */   
/*  166:     */   public static String separatorsToSystem(String path)
/*  167:     */   {
/*  168: 443 */     if (path == null) {
/*  169: 444 */       return null;
/*  170:     */     }
/*  171: 446 */     if (isSystemWindows()) {
/*  172: 447 */       return separatorsToWindows(path);
/*  173:     */     }
/*  174: 449 */     return separatorsToUnix(path);
/*  175:     */   }
/*  176:     */   
/*  177:     */   public static int getPrefixLength(String filename)
/*  178:     */   {
/*  179: 486 */     if (filename == null) {
/*  180: 487 */       return -1;
/*  181:     */     }
/*  182: 489 */     int len = filename.length();
/*  183: 490 */     if (len == 0) {
/*  184: 491 */       return 0;
/*  185:     */     }
/*  186: 493 */     char ch0 = filename.charAt(0);
/*  187: 494 */     if (ch0 == ':') {
/*  188: 495 */       return -1;
/*  189:     */     }
/*  190: 497 */     if (len == 1)
/*  191:     */     {
/*  192: 498 */       if (ch0 == '~') {
/*  193: 499 */         return 2;
/*  194:     */       }
/*  195: 501 */       return isSeparator(ch0) ? 1 : 0;
/*  196:     */     }
/*  197: 503 */     if (ch0 == '~')
/*  198:     */     {
/*  199: 504 */       int posUnix = filename.indexOf('/', 1);
/*  200: 505 */       int posWin = filename.indexOf('\\', 1);
/*  201: 506 */       if ((posUnix == -1) && (posWin == -1)) {
/*  202: 507 */         return len + 1;
/*  203:     */       }
/*  204: 509 */       posUnix = posUnix == -1 ? posWin : posUnix;
/*  205: 510 */       posWin = posWin == -1 ? posUnix : posWin;
/*  206: 511 */       return Math.min(posUnix, posWin) + 1;
/*  207:     */     }
/*  208: 513 */     char ch1 = filename.charAt(1);
/*  209: 514 */     if (ch1 == ':')
/*  210:     */     {
/*  211: 515 */       ch0 = Character.toUpperCase(ch0);
/*  212: 516 */       if ((ch0 >= 'A') && (ch0 <= 'Z'))
/*  213:     */       {
/*  214: 517 */         if ((len == 2) || (!isSeparator(filename.charAt(2)))) {
/*  215: 518 */           return 2;
/*  216:     */         }
/*  217: 520 */         return 3;
/*  218:     */       }
/*  219: 522 */       return -1;
/*  220:     */     }
/*  221: 524 */     if ((isSeparator(ch0)) && (isSeparator(ch1)))
/*  222:     */     {
/*  223: 525 */       int posUnix = filename.indexOf('/', 2);
/*  224: 526 */       int posWin = filename.indexOf('\\', 2);
/*  225: 527 */       if (((posUnix == -1) && (posWin == -1)) || (posUnix == 2) || (posWin == 2)) {
/*  226: 528 */         return -1;
/*  227:     */       }
/*  228: 530 */       posUnix = posUnix == -1 ? posWin : posUnix;
/*  229: 531 */       posWin = posWin == -1 ? posUnix : posWin;
/*  230: 532 */       return Math.min(posUnix, posWin) + 1;
/*  231:     */     }
/*  232: 534 */     return isSeparator(ch0) ? 1 : 0;
/*  233:     */   }
/*  234:     */   
/*  235:     */   public static int indexOfLastSeparator(String filename)
/*  236:     */   {
/*  237: 552 */     if (filename == null) {
/*  238: 553 */       return -1;
/*  239:     */     }
/*  240: 555 */     int lastUnixPos = filename.lastIndexOf('/');
/*  241: 556 */     int lastWindowsPos = filename.lastIndexOf('\\');
/*  242: 557 */     return Math.max(lastUnixPos, lastWindowsPos);
/*  243:     */   }
/*  244:     */   
/*  245:     */   public static int indexOfExtension(String filename)
/*  246:     */   {
/*  247: 574 */     if (filename == null) {
/*  248: 575 */       return -1;
/*  249:     */     }
/*  250: 577 */     int extensionPos = filename.lastIndexOf('.');
/*  251: 578 */     int lastSeparator = indexOfLastSeparator(filename);
/*  252: 579 */     return lastSeparator > extensionPos ? -1 : extensionPos;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public static String getPrefix(String filename)
/*  256:     */   {
/*  257: 613 */     if (filename == null) {
/*  258: 614 */       return null;
/*  259:     */     }
/*  260: 616 */     int len = getPrefixLength(filename);
/*  261: 617 */     if (len < 0) {
/*  262: 618 */       return null;
/*  263:     */     }
/*  264: 620 */     if (len > filename.length()) {
/*  265: 621 */       return filename + '/';
/*  266:     */     }
/*  267: 623 */     return filename.substring(0, len);
/*  268:     */   }
/*  269:     */   
/*  270:     */   public static String getPath(String filename)
/*  271:     */   {
/*  272: 649 */     return doGetPath(filename, 1);
/*  273:     */   }
/*  274:     */   
/*  275:     */   public static String getPathNoEndSeparator(String filename)
/*  276:     */   {
/*  277: 676 */     return doGetPath(filename, 0);
/*  278:     */   }
/*  279:     */   
/*  280:     */   private static String doGetPath(String filename, int separatorAdd)
/*  281:     */   {
/*  282: 687 */     if (filename == null) {
/*  283: 688 */       return null;
/*  284:     */     }
/*  285: 690 */     int prefix = getPrefixLength(filename);
/*  286: 691 */     if (prefix < 0) {
/*  287: 692 */       return null;
/*  288:     */     }
/*  289: 694 */     int index = indexOfLastSeparator(filename);
/*  290: 695 */     if ((prefix >= filename.length()) || (index < 0)) {
/*  291: 696 */       return "";
/*  292:     */     }
/*  293: 698 */     return filename.substring(prefix, index + separatorAdd);
/*  294:     */   }
/*  295:     */   
/*  296:     */   public static String getFullPath(String filename)
/*  297:     */   {
/*  298: 727 */     return doGetFullPath(filename, true);
/*  299:     */   }
/*  300:     */   
/*  301:     */   public static String getFullPathNoEndSeparator(String filename)
/*  302:     */   {
/*  303: 757 */     return doGetFullPath(filename, false);
/*  304:     */   }
/*  305:     */   
/*  306:     */   private static String doGetFullPath(String filename, boolean includeSeparator)
/*  307:     */   {
/*  308: 768 */     if (filename == null) {
/*  309: 769 */       return null;
/*  310:     */     }
/*  311: 771 */     int prefix = getPrefixLength(filename);
/*  312: 772 */     if (prefix < 0) {
/*  313: 773 */       return null;
/*  314:     */     }
/*  315: 775 */     if (prefix >= filename.length())
/*  316:     */     {
/*  317: 776 */       if (includeSeparator) {
/*  318: 777 */         return getPrefix(filename);
/*  319:     */       }
/*  320: 779 */       return filename;
/*  321:     */     }
/*  322: 782 */     int index = indexOfLastSeparator(filename);
/*  323: 783 */     if (index < 0) {
/*  324: 784 */       return filename.substring(0, prefix);
/*  325:     */     }
/*  326: 786 */     int end = index + (includeSeparator ? 1 : 0);
/*  327: 787 */     return filename.substring(0, end);
/*  328:     */   }
/*  329:     */   
/*  330:     */   public static String getName(String filename)
/*  331:     */   {
/*  332: 808 */     if (filename == null) {
/*  333: 809 */       return null;
/*  334:     */     }
/*  335: 811 */     int index = indexOfLastSeparator(filename);
/*  336: 812 */     return filename.substring(index + 1);
/*  337:     */   }
/*  338:     */   
/*  339:     */   public static String getBaseName(String filename)
/*  340:     */   {
/*  341: 833 */     return removeExtension(getName(filename));
/*  342:     */   }
/*  343:     */   
/*  344:     */   public static String getExtension(String filename)
/*  345:     */   {
/*  346: 854 */     if (filename == null) {
/*  347: 855 */       return null;
/*  348:     */     }
/*  349: 857 */     int index = indexOfExtension(filename);
/*  350: 858 */     if (index == -1) {
/*  351: 859 */       return "";
/*  352:     */     }
/*  353: 861 */     return filename.substring(index + 1);
/*  354:     */   }
/*  355:     */   
/*  356:     */   public static String removeExtension(String filename)
/*  357:     */   {
/*  358: 884 */     if (filename == null) {
/*  359: 885 */       return null;
/*  360:     */     }
/*  361: 887 */     int index = indexOfExtension(filename);
/*  362: 888 */     if (index == -1) {
/*  363: 889 */       return filename;
/*  364:     */     }
/*  365: 891 */     return filename.substring(0, index);
/*  366:     */   }
/*  367:     */   
/*  368:     */   public static boolean equals(String filename1, String filename2)
/*  369:     */   {
/*  370: 908 */     return equals(filename1, filename2, false, IOCase.SENSITIVE);
/*  371:     */   }
/*  372:     */   
/*  373:     */   public static boolean equalsOnSystem(String filename1, String filename2)
/*  374:     */   {
/*  375: 923 */     return equals(filename1, filename2, false, IOCase.SYSTEM);
/*  376:     */   }
/*  377:     */   
/*  378:     */   public static boolean equalsNormalized(String filename1, String filename2)
/*  379:     */   {
/*  380: 939 */     return equals(filename1, filename2, true, IOCase.SENSITIVE);
/*  381:     */   }
/*  382:     */   
/*  383:     */   public static boolean equalsNormalizedOnSystem(String filename1, String filename2)
/*  384:     */   {
/*  385: 956 */     return equals(filename1, filename2, true, IOCase.SYSTEM);
/*  386:     */   }
/*  387:     */   
/*  388:     */   public static boolean equals(String filename1, String filename2, boolean normalized, IOCase caseSensitivity)
/*  389:     */   {
/*  390: 974 */     if ((filename1 == null) || (filename2 == null)) {
/*  391: 975 */       return filename1 == filename2;
/*  392:     */     }
/*  393: 977 */     if (normalized)
/*  394:     */     {
/*  395: 978 */       filename1 = normalize(filename1);
/*  396: 979 */       filename2 = normalize(filename2);
/*  397:     */     }
/*  398: 981 */     if (caseSensitivity == null) {
/*  399: 982 */       caseSensitivity = IOCase.SENSITIVE;
/*  400:     */     }
/*  401: 984 */     return caseSensitivity.checkEquals(filename1, filename2);
/*  402:     */   }
/*  403:     */   
/*  404:     */   public static boolean isExtension(String filename, String extension)
/*  405:     */   {
/*  406:1000 */     if (filename == null) {
/*  407:1001 */       return false;
/*  408:     */     }
/*  409:1003 */     if ((extension == null) || (extension.length() == 0)) {
/*  410:1004 */       return indexOfExtension(filename) == -1;
/*  411:     */     }
/*  412:1006 */     String fileExt = getExtension(filename);
/*  413:1007 */     return fileExt.equals(extension);
/*  414:     */   }
/*  415:     */   
/*  416:     */   public static boolean isExtension(String filename, String[] extensions)
/*  417:     */   {
/*  418:1022 */     if (filename == null) {
/*  419:1023 */       return false;
/*  420:     */     }
/*  421:1025 */     if ((extensions == null) || (extensions.length == 0)) {
/*  422:1026 */       return indexOfExtension(filename) == -1;
/*  423:     */     }
/*  424:1028 */     String fileExt = getExtension(filename);
/*  425:1029 */     for (int i = 0; i < extensions.length; i++) {
/*  426:1030 */       if (fileExt.equals(extensions[i])) {
/*  427:1031 */         return true;
/*  428:     */       }
/*  429:     */     }
/*  430:1034 */     return false;
/*  431:     */   }
/*  432:     */   
/*  433:     */   public static boolean isExtension(String filename, Collection extensions)
/*  434:     */   {
/*  435:1049 */     if (filename == null) {
/*  436:1050 */       return false;
/*  437:     */     }
/*  438:1052 */     if ((extensions == null) || (extensions.isEmpty())) {
/*  439:1053 */       return indexOfExtension(filename) == -1;
/*  440:     */     }
/*  441:1055 */     String fileExt = getExtension(filename);
/*  442:1056 */     for (Iterator it = extensions.iterator(); it.hasNext();) {
/*  443:1057 */       if (fileExt.equals(it.next())) {
/*  444:1058 */         return true;
/*  445:     */       }
/*  446:     */     }
/*  447:1061 */     return false;
/*  448:     */   }
/*  449:     */   
/*  450:     */   public static boolean wildcardMatch(String filename, String wildcardMatcher)
/*  451:     */   {
/*  452:1087 */     return wildcardMatch(filename, wildcardMatcher, IOCase.SENSITIVE);
/*  453:     */   }
/*  454:     */   
/*  455:     */   public static boolean wildcardMatchOnSystem(String filename, String wildcardMatcher)
/*  456:     */   {
/*  457:1112 */     return wildcardMatch(filename, wildcardMatcher, IOCase.SYSTEM);
/*  458:     */   }
/*  459:     */   
/*  460:     */   public static boolean wildcardMatch(String filename, String wildcardMatcher, IOCase caseSensitivity)
/*  461:     */   {
/*  462:1129 */     if ((filename == null) && (wildcardMatcher == null)) {
/*  463:1130 */       return true;
/*  464:     */     }
/*  465:1132 */     if ((filename == null) || (wildcardMatcher == null)) {
/*  466:1133 */       return false;
/*  467:     */     }
/*  468:1135 */     if (caseSensitivity == null) {
/*  469:1136 */       caseSensitivity = IOCase.SENSITIVE;
/*  470:     */     }
/*  471:1138 */     filename = caseSensitivity.convertCase(filename);
/*  472:1139 */     wildcardMatcher = caseSensitivity.convertCase(wildcardMatcher);
/*  473:1140 */     String[] wcs = splitOnTokens(wildcardMatcher);
/*  474:1141 */     boolean anyChars = false;
/*  475:1142 */     int textIdx = 0;
/*  476:1143 */     int wcsIdx = 0;
/*  477:1144 */     Stack backtrack = new Stack();
/*  478:     */     do
/*  479:     */     {
/*  480:1148 */       if (backtrack.size() > 0)
/*  481:     */       {
/*  482:1149 */         int[] array = (int[])backtrack.pop();
/*  483:1150 */         wcsIdx = array[0];
/*  484:1151 */         textIdx = array[1];
/*  485:1152 */         anyChars = true;
/*  486:     */       }
/*  487:1156 */       while (wcsIdx < wcs.length)
/*  488:     */       {
/*  489:1158 */         if (wcs[wcsIdx].equals("?"))
/*  490:     */         {
/*  491:1160 */           textIdx++;
/*  492:1161 */           anyChars = false;
/*  493:     */         }
/*  494:1163 */         else if (wcs[wcsIdx].equals("*"))
/*  495:     */         {
/*  496:1165 */           anyChars = true;
/*  497:1166 */           if (wcsIdx == wcs.length - 1) {
/*  498:1167 */             textIdx = filename.length();
/*  499:     */           }
/*  500:     */         }
/*  501:     */         else
/*  502:     */         {
/*  503:1172 */           if (anyChars)
/*  504:     */           {
/*  505:1174 */             textIdx = filename.indexOf(wcs[wcsIdx], textIdx);
/*  506:1175 */             if (textIdx == -1) {
/*  507:     */               break;
/*  508:     */             }
/*  509:1179 */             int repeat = filename.indexOf(wcs[wcsIdx], textIdx + 1);
/*  510:1180 */             if (repeat >= 0) {
/*  511:1181 */               backtrack.push(new int[] { wcsIdx, repeat });
/*  512:     */             }
/*  513:     */           }
/*  514:     */           else
/*  515:     */           {
/*  516:1185 */             if (!filename.startsWith(wcs[wcsIdx], textIdx)) {
/*  517:     */               break;
/*  518:     */             }
/*  519:     */           }
/*  520:1192 */           textIdx += wcs[wcsIdx].length();
/*  521:1193 */           anyChars = false;
/*  522:     */         }
/*  523:1196 */         wcsIdx++;
/*  524:     */       }
/*  525:1200 */       if ((wcsIdx == wcs.length) && (textIdx == filename.length())) {
/*  526:1201 */         return true;
/*  527:     */       }
/*  528:1204 */     } while (backtrack.size() > 0);
/*  529:1206 */     return false;
/*  530:     */   }
/*  531:     */   
/*  532:     */   static String[] splitOnTokens(String text)
/*  533:     */   {
/*  534:1219 */     if ((text.indexOf("?") == -1) && (text.indexOf("*") == -1)) {
/*  535:1220 */       return new String[] { text };
/*  536:     */     }
/*  537:1223 */     char[] array = text.toCharArray();
/*  538:1224 */     ArrayList list = new ArrayList();
/*  539:1225 */     StringBuffer buffer = new StringBuffer();
/*  540:1226 */     for (int i = 0; i < array.length; i++) {
/*  541:1227 */       if ((array[i] == '?') || (array[i] == '*'))
/*  542:     */       {
/*  543:1228 */         if (buffer.length() != 0)
/*  544:     */         {
/*  545:1229 */           list.add(buffer.toString());
/*  546:1230 */           buffer.setLength(0);
/*  547:     */         }
/*  548:1232 */         if (array[i] == '?') {
/*  549:1233 */           list.add("?");
/*  550:1234 */         } else if ((list.size() == 0) || ((i > 0) && (!list.get(list.size() - 1).equals("*")))) {
/*  551:1236 */           list.add("*");
/*  552:     */         }
/*  553:     */       }
/*  554:     */       else
/*  555:     */       {
/*  556:1239 */         buffer.append(array[i]);
/*  557:     */       }
/*  558:     */     }
/*  559:1242 */     if (buffer.length() != 0) {
/*  560:1243 */       list.add(buffer.toString());
/*  561:     */     }
/*  562:1246 */     return (String[])list.toArray(new String[list.size()]);
/*  563:     */   }
/*  564:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FilenameUtils
 * JD-Core Version:    0.7.0.1
 */