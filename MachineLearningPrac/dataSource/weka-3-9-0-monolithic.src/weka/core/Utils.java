/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.beans.BeanInfo;
/*    4:     */ import java.beans.Introspector;
/*    5:     */ import java.beans.MethodDescriptor;
/*    6:     */ import java.io.BufferedReader;
/*    7:     */ import java.io.BufferedWriter;
/*    8:     */ import java.io.File;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileReader;
/*   11:     */ import java.io.FileWriter;
/*   12:     */ import java.io.PrintStream;
/*   13:     */ import java.lang.reflect.Array;
/*   14:     */ import java.lang.reflect.Method;
/*   15:     */ import java.math.RoundingMode;
/*   16:     */ import java.net.URL;
/*   17:     */ import java.text.BreakIterator;
/*   18:     */ import java.text.DecimalFormat;
/*   19:     */ import java.text.DecimalFormatSymbols;
/*   20:     */ import java.util.Enumeration;
/*   21:     */ import java.util.List;
/*   22:     */ import java.util.Properties;
/*   23:     */ import java.util.Random;
/*   24:     */ import java.util.Vector;
/*   25:     */ import weka.Run;
/*   26:     */ import weka.gui.PropertySheetPanel;
/*   27:     */ 
/*   28:     */ public final class Utils
/*   29:     */   implements RevisionHandler
/*   30:     */ {
/*   31:  63 */   public static double log2 = Math.log(2.0D);
/*   32:  68 */   public static double SMALL = 1.0E-006D;
/*   33:  71 */   private static final ThreadLocal<DecimalFormat> DF = new ThreadLocal()
/*   34:     */   {
/*   35:     */     protected DecimalFormat initialValue()
/*   36:     */     {
/*   37:  76 */       DecimalFormat df = new DecimalFormat();
/*   38:  77 */       DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
/*   39:  78 */       dfs.setDecimalSeparator('.');
/*   40:  79 */       dfs.setNaN("NaN");
/*   41:  80 */       dfs.setInfinity("Infinity");
/*   42:  81 */       df.setGroupingUsed(false);
/*   43:  82 */       df.setRoundingMode(RoundingMode.HALF_UP);
/*   44:  83 */       df.setDecimalFormatSymbols(dfs);
/*   45:  84 */       return df;
/*   46:     */     }
/*   47:     */   };
/*   48:     */   
/*   49:     */   public static boolean isMissingValue(double val)
/*   50:     */   {
/*   51:  96 */     return Double.isNaN(val);
/*   52:     */   }
/*   53:     */   
/*   54:     */   public static double missingValue()
/*   55:     */   {
/*   56: 108 */     return (0.0D / 0.0D);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public static <T> T cast(Object x)
/*   60:     */   {
/*   61: 117 */     return x;
/*   62:     */   }
/*   63:     */   
/*   64:     */   public static Properties readProperties(String resourceName)
/*   65:     */     throws Exception
/*   66:     */   {
/*   67: 140 */     Properties defaultProps = new Properties();
/*   68:     */     try
/*   69:     */     {
/*   70: 144 */       Utils utils = new Utils();
/*   71: 145 */       Enumeration<URL> urls = utils.getClass().getClassLoader().getResources(resourceName);
/*   72:     */       
/*   73: 147 */       boolean first = true;
/*   74: 148 */       while (urls.hasMoreElements())
/*   75:     */       {
/*   76: 149 */         URL url = (URL)urls.nextElement();
/*   77: 150 */         if (first)
/*   78:     */         {
/*   79: 151 */           defaultProps.load(url.openStream());
/*   80: 152 */           first = false;
/*   81:     */         }
/*   82:     */         else
/*   83:     */         {
/*   84: 154 */           Properties props = new Properties(defaultProps);
/*   85: 155 */           props.load(url.openStream());
/*   86: 156 */           defaultProps = props;
/*   87:     */         }
/*   88:     */       }
/*   89:     */     }
/*   90:     */     catch (Exception ex)
/*   91:     */     {
/*   92: 160 */       System.err.println("Warning, unable to load properties file(s) from system resource (Utils.java): " + resourceName);
/*   93:     */     }
/*   94: 166 */     int slInd = resourceName.lastIndexOf('/');
/*   95: 167 */     if (slInd != -1) {
/*   96: 168 */       resourceName = resourceName.substring(slInd + 1);
/*   97:     */     }
/*   98: 173 */     Properties userProps = new Properties(defaultProps);
/*   99: 174 */     if (!WekaPackageManager.PROPERTIES_DIR.exists()) {
/*  100: 175 */       WekaPackageManager.PROPERTIES_DIR.mkdir();
/*  101:     */     }
/*  102: 177 */     File propFile = new File(WekaPackageManager.PROPERTIES_DIR.toString() + File.separator + resourceName);
/*  103: 180 */     if (propFile.exists()) {
/*  104:     */       try
/*  105:     */       {
/*  106: 182 */         userProps.load(new FileInputStream(propFile));
/*  107:     */       }
/*  108:     */       catch (Exception ex)
/*  109:     */       {
/*  110: 184 */         throw new Exception("Problem reading user properties: " + propFile);
/*  111:     */       }
/*  112:     */     }
/*  113: 189 */     Properties localProps = new Properties(userProps);
/*  114: 190 */     propFile = new File(resourceName);
/*  115: 191 */     if (propFile.exists()) {
/*  116:     */       try
/*  117:     */       {
/*  118: 193 */         localProps.load(new FileInputStream(propFile));
/*  119:     */       }
/*  120:     */       catch (Exception ex)
/*  121:     */       {
/*  122: 195 */         throw new Exception("Problem reading local properties: " + propFile);
/*  123:     */       }
/*  124:     */     }
/*  125: 199 */     return new EnvironmentProperties(localProps);
/*  126:     */   }
/*  127:     */   
/*  128:     */   public static final double correlation(double[] y1, double[] y2, int n)
/*  129:     */   {
/*  130: 213 */     double av1 = 0.0D;double av2 = 0.0D;double y11 = 0.0D;double y22 = 0.0D;double y12 = 0.0D;
/*  131: 215 */     if (n <= 1) {
/*  132: 216 */       return 1.0D;
/*  133:     */     }
/*  134: 218 */     for (int i = 0; i < n; i++)
/*  135:     */     {
/*  136: 219 */       av1 += y1[i];
/*  137: 220 */       av2 += y2[i];
/*  138:     */     }
/*  139: 222 */     av1 /= n;
/*  140: 223 */     av2 /= n;
/*  141: 224 */     for (i = 0; i < n; i++)
/*  142:     */     {
/*  143: 225 */       y11 += (y1[i] - av1) * (y1[i] - av1);
/*  144: 226 */       y22 += (y2[i] - av2) * (y2[i] - av2);
/*  145: 227 */       y12 += (y1[i] - av1) * (y2[i] - av2);
/*  146:     */     }
/*  147:     */     double c;
/*  148:     */     double c;
/*  149: 229 */     if (y11 * y22 == 0.0D) {
/*  150: 230 */       c = 1.0D;
/*  151:     */     } else {
/*  152: 232 */       c = y12 / Math.sqrt(Math.abs(y11 * y22));
/*  153:     */     }
/*  154: 235 */     return c;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public static String removeSubstring(String inString, String substring)
/*  158:     */   {
/*  159: 247 */     StringBuffer result = new StringBuffer();
/*  160: 248 */     int oldLoc = 0;int loc = 0;
/*  161: 249 */     while ((loc = inString.indexOf(substring, oldLoc)) != -1)
/*  162:     */     {
/*  163: 250 */       result.append(inString.substring(oldLoc, loc));
/*  164: 251 */       oldLoc = loc + substring.length();
/*  165:     */     }
/*  166: 253 */     result.append(inString.substring(oldLoc));
/*  167: 254 */     return result.toString();
/*  168:     */   }
/*  169:     */   
/*  170:     */   public static String replaceSubstring(String inString, String subString, String replaceString)
/*  171:     */   {
/*  172: 269 */     StringBuffer result = new StringBuffer();
/*  173: 270 */     int oldLoc = 0;int loc = 0;
/*  174: 271 */     while ((loc = inString.indexOf(subString, oldLoc)) != -1)
/*  175:     */     {
/*  176: 272 */       result.append(inString.substring(oldLoc, loc));
/*  177: 273 */       result.append(replaceString);
/*  178: 274 */       oldLoc = loc + subString.length();
/*  179:     */     }
/*  180: 276 */     result.append(inString.substring(oldLoc));
/*  181: 277 */     return result.toString();
/*  182:     */   }
/*  183:     */   
/*  184:     */   public static String padLeftAndAllowOverflow(String inString, int length)
/*  185:     */   {
/*  186: 290 */     return String.format("%1$" + length + "s", new Object[] { inString });
/*  187:     */   }
/*  188:     */   
/*  189:     */   public static String padRightAndAllowOverflow(String inString, int length)
/*  190:     */   {
/*  191: 303 */     return String.format("%1$-" + length + "s", new Object[] { inString });
/*  192:     */   }
/*  193:     */   
/*  194:     */   public static String padLeft(String inString, int length)
/*  195:     */   {
/*  196: 317 */     return String.format("%1$" + length + "." + length + "s", new Object[] { inString });
/*  197:     */   }
/*  198:     */   
/*  199:     */   public static String padRight(String inString, int length)
/*  200:     */   {
/*  201: 331 */     return String.format("%1$-" + length + "." + length + "s", new Object[] { inString });
/*  202:     */   }
/*  203:     */   
/*  204:     */   public static String doubleToString(double value, int afterDecimalPoint)
/*  205:     */   {
/*  206: 345 */     ((DecimalFormat)DF.get()).setMaximumFractionDigits(afterDecimalPoint);
/*  207: 346 */     return ((DecimalFormat)DF.get()).format(value);
/*  208:     */   }
/*  209:     */   
/*  210:     */   public static String doubleToString(double value, int width, int afterDecimalPoint)
/*  211:     */   {
/*  212: 361 */     String tempString = doubleToString(value, afterDecimalPoint);
/*  213: 365 */     if (afterDecimalPoint >= width) {
/*  214: 366 */       return tempString;
/*  215:     */     }
/*  216: 370 */     char[] result = new char[width];
/*  217: 371 */     for (int i = 0; i < result.length; i++) {
/*  218: 372 */       result[i] = ' ';
/*  219:     */     }
/*  220:     */     int dotPosition;
/*  221: 375 */     if (afterDecimalPoint > 0)
/*  222:     */     {
/*  223: 377 */       int dotPosition = tempString.indexOf('.');
/*  224: 378 */       if (dotPosition == -1) {
/*  225: 379 */         dotPosition = tempString.length();
/*  226:     */       } else {
/*  227: 381 */         result[(width - afterDecimalPoint - 1)] = '.';
/*  228:     */       }
/*  229:     */     }
/*  230:     */     else
/*  231:     */     {
/*  232: 384 */       dotPosition = tempString.length();
/*  233:     */     }
/*  234: 387 */     int offset = width - afterDecimalPoint - dotPosition;
/*  235: 388 */     if (afterDecimalPoint > 0) {
/*  236: 389 */       offset--;
/*  237:     */     }
/*  238: 393 */     if (offset < 0) {
/*  239: 394 */       return tempString;
/*  240:     */     }
/*  241: 398 */     for (int i = 0; i < dotPosition; i++) {
/*  242: 399 */       result[(offset + i)] = tempString.charAt(i);
/*  243:     */     }
/*  244: 403 */     for (int i = dotPosition + 1; i < tempString.length(); i++) {
/*  245: 404 */       result[(offset + i)] = tempString.charAt(i);
/*  246:     */     }
/*  247: 407 */     return new String(result);
/*  248:     */   }
/*  249:     */   
/*  250:     */   public static Class<?> getArrayClass(Class<?> c)
/*  251:     */   {
/*  252: 418 */     if (c.getComponentType().isArray()) {
/*  253: 419 */       return getArrayClass(c.getComponentType());
/*  254:     */     }
/*  255: 421 */     return c.getComponentType();
/*  256:     */   }
/*  257:     */   
/*  258:     */   public static int getArrayDimensions(Class<?> array)
/*  259:     */   {
/*  260: 434 */     if (array.getComponentType().isArray()) {
/*  261: 435 */       return 1 + getArrayDimensions(array.getComponentType());
/*  262:     */     }
/*  263: 437 */     return 1;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public static int getArrayDimensions(Object array)
/*  267:     */   {
/*  268: 450 */     return getArrayDimensions(array.getClass());
/*  269:     */   }
/*  270:     */   
/*  271:     */   public static String arrayToString(Object array)
/*  272:     */   {
/*  273: 466 */     String result = "";
/*  274: 467 */     int dimensions = getArrayDimensions(array);
/*  275: 469 */     if (dimensions == 0)
/*  276:     */     {
/*  277: 470 */       result = "null";
/*  278:     */     }
/*  279:     */     else
/*  280:     */     {
/*  281: 471 */       if (dimensions == 1) {
/*  282: 472 */         for (int i = 0; i < Array.getLength(array); i++)
/*  283:     */         {
/*  284: 473 */           if (i > 0) {
/*  285: 474 */             result = result + ",";
/*  286:     */           }
/*  287: 476 */           if (Array.get(array, i) == null) {
/*  288: 477 */             result = result + "null";
/*  289:     */           } else {
/*  290: 479 */             result = result + Array.get(array, i).toString();
/*  291:     */           }
/*  292:     */         }
/*  293:     */       }
/*  294: 483 */       for (int i = 0; i < Array.getLength(array); i++)
/*  295:     */       {
/*  296: 484 */         if (i > 0) {
/*  297: 485 */           result = result + ",";
/*  298:     */         }
/*  299: 487 */         result = result + "[" + arrayToString(Array.get(array, i)) + "]";
/*  300:     */       }
/*  301:     */     }
/*  302: 491 */     return result;
/*  303:     */   }
/*  304:     */   
/*  305:     */   public static boolean eq(double a, double b)
/*  306:     */   {
/*  307: 502 */     return (a == b) || ((a - b < SMALL) && (b - a < SMALL));
/*  308:     */   }
/*  309:     */   
/*  310:     */   public static void checkForRemainingOptions(String[] options)
/*  311:     */     throws Exception
/*  312:     */   {
/*  313: 514 */     int illegalOptionsFound = 0;
/*  314: 515 */     StringBuffer text = new StringBuffer();
/*  315: 517 */     if (options == null) {
/*  316: 518 */       return;
/*  317:     */     }
/*  318: 520 */     for (String option : options) {
/*  319: 521 */       if (option.length() > 0)
/*  320:     */       {
/*  321: 522 */         illegalOptionsFound++;
/*  322: 523 */         text.append(option + ' ');
/*  323:     */       }
/*  324:     */     }
/*  325: 526 */     if (illegalOptionsFound > 0) {
/*  326: 527 */       throw new Exception("Illegal options: " + text);
/*  327:     */     }
/*  328:     */   }
/*  329:     */   
/*  330:     */   public static boolean getFlag(char flag, String[] options)
/*  331:     */     throws Exception
/*  332:     */   {
/*  333: 543 */     return getFlag("" + flag, options);
/*  334:     */   }
/*  335:     */   
/*  336:     */   public static boolean getFlag(String flag, String[] options)
/*  337:     */     throws Exception
/*  338:     */   {
/*  339: 558 */     int pos = getOptionPos(flag, options);
/*  340: 560 */     if (pos > -1) {
/*  341: 561 */       options[pos] = "";
/*  342:     */     }
/*  343: 564 */     return pos > -1;
/*  344:     */   }
/*  345:     */   
/*  346:     */   public static String getOption(char flag, String[] options)
/*  347:     */     throws Exception
/*  348:     */   {
/*  349: 580 */     return getOption("" + flag, options);
/*  350:     */   }
/*  351:     */   
/*  352:     */   public static String getOption(String flag, String[] options)
/*  353:     */     throws Exception
/*  354:     */   {
/*  355: 597 */     int i = getOptionPos(flag, options);
/*  356: 599 */     if (i > -1)
/*  357:     */     {
/*  358: 600 */       if (options[i].equals("-" + flag))
/*  359:     */       {
/*  360: 601 */         if (i + 1 == options.length) {
/*  361: 602 */           throw new Exception("No value given for -" + flag + " option.");
/*  362:     */         }
/*  363: 604 */         options[i] = "";
/*  364: 605 */         String newString = new String(options[(i + 1)]);
/*  365: 606 */         options[(i + 1)] = "";
/*  366: 607 */         return newString;
/*  367:     */       }
/*  368: 609 */       if (options[i].charAt(1) == '-') {
/*  369: 610 */         return "";
/*  370:     */       }
/*  371:     */     }
/*  372: 614 */     return "";
/*  373:     */   }
/*  374:     */   
/*  375:     */   public static int getOptionPos(char flag, String[] options)
/*  376:     */   {
/*  377: 626 */     return getOptionPos("" + flag, options);
/*  378:     */   }
/*  379:     */   
/*  380:     */   public static int getOptionPos(String flag, String[] options)
/*  381:     */   {
/*  382: 638 */     if (options == null) {
/*  383: 639 */       return -1;
/*  384:     */     }
/*  385: 642 */     for (int i = 0; i < options.length; i++) {
/*  386: 643 */       if ((options[i].length() > 0) && (options[i].charAt(0) == '-')) {
/*  387:     */         try
/*  388:     */         {
/*  389: 646 */           Double.valueOf(options[i]);
/*  390:     */         }
/*  391:     */         catch (NumberFormatException e)
/*  392:     */         {
/*  393: 649 */           if (options[i].equals("-" + flag)) {
/*  394: 650 */             return i;
/*  395:     */           }
/*  396: 653 */           if (options[i].charAt(1) == '-') {
/*  397: 654 */             return -1;
/*  398:     */           }
/*  399:     */         }
/*  400:     */       }
/*  401:     */     }
/*  402: 660 */     return -1;
/*  403:     */   }
/*  404:     */   
/*  405:     */   public static String quote(String string)
/*  406:     */   {
/*  407: 683 */     boolean quote = false;
/*  408: 686 */     if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1) || (string.indexOf('\'') != -1) || (string.indexOf('"') != -1) || (string.indexOf('\\') != -1) || (string.indexOf('\t') != -1) || (string.indexOf('%') != -1) || (string.indexOf('\036') != -1))
/*  409:     */     {
/*  410: 690 */       string = backQuoteChars(string);
/*  411: 691 */       quote = true;
/*  412:     */     }
/*  413: 696 */     if ((quote == true) || (string.indexOf('{') != -1) || (string.indexOf('}') != -1) || (string.indexOf(',') != -1) || (string.equals("?")) || (string.indexOf(' ') != -1) || (string.equals(""))) {
/*  414: 700 */       string = "'".concat(string).concat("'");
/*  415:     */     }
/*  416: 703 */     return string;
/*  417:     */   }
/*  418:     */   
/*  419:     */   public static String unquote(String string)
/*  420:     */   {
/*  421: 715 */     if ((string.startsWith("'")) && (string.endsWith("'")))
/*  422:     */     {
/*  423: 716 */       string = string.substring(1, string.length() - 1);
/*  424: 718 */       if ((string.indexOf("\\n") != -1) || (string.indexOf("\\r") != -1) || (string.indexOf("\\'") != -1) || (string.indexOf("\\\"") != -1) || (string.indexOf("\\\\") != -1) || (string.indexOf("\\t") != -1) || (string.indexOf("\\%") != -1) || (string.indexOf("\\u001E") != -1)) {
/*  425: 722 */         string = unbackQuoteChars(string);
/*  426:     */       }
/*  427:     */     }
/*  428: 726 */     return string;
/*  429:     */   }
/*  430:     */   
/*  431:     */   public static String backQuoteChars(String string)
/*  432:     */   {
/*  433: 743 */     char[] charsFind = { '\\', '\'', '\t', '\n', '\r', '"', '%', '\036' };
/*  434: 744 */     String[] charsReplace = { "\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"", "\\%", "\\u001E" };
/*  435: 746 */     for (int i = 0; i < charsFind.length; i++) {
/*  436: 747 */       if (string.indexOf(charsFind[i]) != -1)
/*  437:     */       {
/*  438: 748 */         StringBuffer newStringBuffer = new StringBuffer();
/*  439:     */         int index;
/*  440: 749 */         while ((index = string.indexOf(charsFind[i])) != -1)
/*  441:     */         {
/*  442: 750 */           if (index > 0) {
/*  443: 751 */             newStringBuffer.append(string.substring(0, index));
/*  444:     */           }
/*  445: 753 */           newStringBuffer.append(charsReplace[i]);
/*  446: 754 */           if (index + 1 < string.length()) {
/*  447: 755 */             string = string.substring(index + 1);
/*  448:     */           } else {
/*  449: 757 */             string = "";
/*  450:     */           }
/*  451:     */         }
/*  452: 760 */         newStringBuffer.append(string);
/*  453: 761 */         string = newStringBuffer.toString();
/*  454:     */       }
/*  455:     */     }
/*  456: 765 */     return string;
/*  457:     */   }
/*  458:     */   
/*  459:     */   public static String convertNewLines(String string)
/*  460:     */   {
/*  461: 778 */     StringBuffer newStringBuffer = new StringBuffer();
/*  462:     */     int index;
/*  463: 779 */     while ((index = string.indexOf('\n')) != -1)
/*  464:     */     {
/*  465: 780 */       if (index > 0) {
/*  466: 781 */         newStringBuffer.append(string.substring(0, index));
/*  467:     */       }
/*  468: 783 */       newStringBuffer.append('\\');
/*  469: 784 */       newStringBuffer.append('n');
/*  470: 785 */       if (index + 1 < string.length()) {
/*  471: 786 */         string = string.substring(index + 1);
/*  472:     */       } else {
/*  473: 788 */         string = "";
/*  474:     */       }
/*  475:     */     }
/*  476: 791 */     newStringBuffer.append(string);
/*  477: 792 */     string = newStringBuffer.toString();
/*  478:     */     
/*  479:     */ 
/*  480: 795 */     newStringBuffer = new StringBuffer();
/*  481: 796 */     while ((index = string.indexOf('\r')) != -1)
/*  482:     */     {
/*  483: 797 */       if (index > 0) {
/*  484: 798 */         newStringBuffer.append(string.substring(0, index));
/*  485:     */       }
/*  486: 800 */       newStringBuffer.append('\\');
/*  487: 801 */       newStringBuffer.append('r');
/*  488: 802 */       if (index + 1 < string.length()) {
/*  489: 803 */         string = string.substring(index + 1);
/*  490:     */       } else {
/*  491: 805 */         string = "";
/*  492:     */       }
/*  493:     */     }
/*  494: 808 */     newStringBuffer.append(string);
/*  495: 809 */     return newStringBuffer.toString();
/*  496:     */   }
/*  497:     */   
/*  498:     */   public static String revertNewLines(String string)
/*  499:     */   {
/*  500: 822 */     StringBuffer newStringBuffer = new StringBuffer();
/*  501:     */     int index;
/*  502: 823 */     while ((index = string.indexOf("\\n")) != -1)
/*  503:     */     {
/*  504: 824 */       if (index > 0) {
/*  505: 825 */         newStringBuffer.append(string.substring(0, index));
/*  506:     */       }
/*  507: 827 */       newStringBuffer.append('\n');
/*  508: 828 */       if (index + 2 < string.length()) {
/*  509: 829 */         string = string.substring(index + 2);
/*  510:     */       } else {
/*  511: 831 */         string = "";
/*  512:     */       }
/*  513:     */     }
/*  514: 834 */     newStringBuffer.append(string);
/*  515: 835 */     string = newStringBuffer.toString();
/*  516:     */     
/*  517:     */ 
/*  518: 838 */     newStringBuffer = new StringBuffer();
/*  519: 839 */     while ((index = string.indexOf("\\r")) != -1)
/*  520:     */     {
/*  521: 840 */       if (index > 0) {
/*  522: 841 */         newStringBuffer.append(string.substring(0, index));
/*  523:     */       }
/*  524: 843 */       newStringBuffer.append('\r');
/*  525: 844 */       if (index + 2 < string.length()) {
/*  526: 845 */         string = string.substring(index + 2);
/*  527:     */       } else {
/*  528: 847 */         string = "";
/*  529:     */       }
/*  530:     */     }
/*  531: 850 */     newStringBuffer.append(string);
/*  532:     */     
/*  533: 852 */     return newStringBuffer.toString();
/*  534:     */   }
/*  535:     */   
/*  536:     */   public static String[] partitionOptions(String[] options)
/*  537:     */   {
/*  538: 865 */     for (int i = 0; i < options.length; i++) {
/*  539: 866 */       if (options[i].equals("--"))
/*  540:     */       {
/*  541: 867 */         options[(i++)] = "";
/*  542: 868 */         String[] result = new String[options.length - i];
/*  543: 869 */         for (int j = i; j < options.length; j++)
/*  544:     */         {
/*  545: 870 */           result[(j - i)] = options[j];
/*  546: 871 */           options[j] = "";
/*  547:     */         }
/*  548: 873 */         return result;
/*  549:     */       }
/*  550:     */     }
/*  551: 876 */     return new String[0];
/*  552:     */   }
/*  553:     */   
/*  554:     */   public static String unbackQuoteChars(String string)
/*  555:     */   {
/*  556: 894 */     String[] charsFind = { "\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"", "\\%", "\\u001E" };
/*  557:     */     
/*  558: 896 */     char[] charsReplace = { '\\', '\'', '\t', '\n', '\r', '"', '%', '\036' };
/*  559: 897 */     int[] pos = new int[charsFind.length];
/*  560:     */     
/*  561:     */ 
/*  562: 900 */     String str = new String(string);
/*  563: 901 */     StringBuffer newStringBuffer = new StringBuffer();
/*  564: 902 */     while (str.length() > 0)
/*  565:     */     {
/*  566: 904 */       int curPos = str.length();
/*  567: 905 */       int index = -1;
/*  568: 906 */       for (int i = 0; i < pos.length; i++)
/*  569:     */       {
/*  570: 907 */         pos[i] = str.indexOf(charsFind[i]);
/*  571: 908 */         if ((pos[i] > -1) && (pos[i] < curPos))
/*  572:     */         {
/*  573: 909 */           index = i;
/*  574: 910 */           curPos = pos[i];
/*  575:     */         }
/*  576:     */       }
/*  577: 915 */       if (index == -1)
/*  578:     */       {
/*  579: 916 */         newStringBuffer.append(str);
/*  580: 917 */         str = "";
/*  581:     */       }
/*  582:     */       else
/*  583:     */       {
/*  584: 919 */         newStringBuffer.append(str.substring(0, pos[index]));
/*  585: 920 */         newStringBuffer.append(charsReplace[index]);
/*  586: 921 */         str = str.substring(pos[index] + charsFind[index].length());
/*  587:     */       }
/*  588:     */     }
/*  589: 925 */     return newStringBuffer.toString();
/*  590:     */   }
/*  591:     */   
/*  592:     */   public static String[] splitOptions(String quotedOptionString)
/*  593:     */     throws Exception
/*  594:     */   {
/*  595: 940 */     Vector<String> optionsVec = new Vector();
/*  596: 941 */     String str = new String(quotedOptionString);
/*  597:     */     for (;;)
/*  598:     */     {
/*  599: 947 */       i = 0;
/*  600: 948 */       while ((i < str.length()) && (Character.isWhitespace(str.charAt(i)))) {
/*  601: 949 */         i++;
/*  602:     */       }
/*  603: 951 */       str = str.substring(i);
/*  604: 954 */       if (str.length() == 0) {
/*  605:     */         break;
/*  606:     */       }
/*  607: 959 */       if (str.charAt(0) == '"')
/*  608:     */       {
/*  609: 962 */         i = 1;
/*  610: 963 */         while ((i < str.length()) && 
/*  611: 964 */           (str.charAt(i) != str.charAt(0)))
/*  612:     */         {
/*  613: 967 */           if (str.charAt(i) == '\\')
/*  614:     */           {
/*  615: 968 */             i++;
/*  616: 969 */             if (i >= str.length()) {
/*  617: 970 */               throw new Exception("String should not finish with \\");
/*  618:     */             }
/*  619:     */           }
/*  620: 973 */           i++;
/*  621:     */         }
/*  622: 975 */         if (i >= str.length()) {
/*  623: 976 */           throw new Exception("Quote parse error.");
/*  624:     */         }
/*  625: 980 */         String optStr = str.substring(1, i);
/*  626: 981 */         optStr = unbackQuoteChars(optStr);
/*  627: 982 */         optionsVec.addElement(optStr);
/*  628: 983 */         str = str.substring(i + 1);
/*  629:     */       }
/*  630:     */       else
/*  631:     */       {
/*  632: 986 */         i = 0;
/*  633: 987 */         while ((i < str.length()) && (!Character.isWhitespace(str.charAt(i)))) {
/*  634: 988 */           i++;
/*  635:     */         }
/*  636: 992 */         String optStr = str.substring(0, i);
/*  637: 993 */         optionsVec.addElement(optStr);
/*  638: 994 */         str = str.substring(i);
/*  639:     */       }
/*  640:     */     }
/*  641: 999 */     String[] options = new String[optionsVec.size()];
/*  642:1000 */     for (int i = 0; i < optionsVec.size(); i++) {
/*  643:1001 */       options[i] = ((String)optionsVec.elementAt(i));
/*  644:     */     }
/*  645:1003 */     return options;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static String joinOptions(String[] optionArray)
/*  649:     */   {
/*  650:1015 */     String optionString = "";
/*  651:1016 */     for (String element : optionArray) {
/*  652:1017 */       if (!element.equals(""))
/*  653:     */       {
/*  654:1020 */         boolean escape = false;
/*  655:1021 */         for (int n = 0; n < element.length(); n++) {
/*  656:1022 */           if (Character.isWhitespace(element.charAt(n)))
/*  657:     */           {
/*  658:1023 */             escape = true;
/*  659:1024 */             break;
/*  660:     */           }
/*  661:     */         }
/*  662:1027 */         if (escape) {
/*  663:1028 */           optionString = optionString + '"' + backQuoteChars(element) + '"';
/*  664:     */         } else {
/*  665:1030 */           optionString = optionString + element;
/*  666:     */         }
/*  667:1032 */         optionString = optionString + " ";
/*  668:     */       }
/*  669:     */     }
/*  670:1034 */     return optionString.trim();
/*  671:     */   }
/*  672:     */   
/*  673:     */   public static Object forName(Class<?> classType, String className, String[] options)
/*  674:     */     throws Exception
/*  675:     */   {
/*  676:1066 */     if (System.getProperty("weka.test.maventest", "").equalsIgnoreCase("true")) {
/*  677:1067 */       return forNameNoSchemeMatch(classType, className, options);
/*  678:     */     }
/*  679:1070 */     List<String> matches = Run.findSchemeMatch(classType, className, false, true);
/*  680:1072 */     if (matches.size() == 0) {
/*  681:1073 */       throw new Exception("Can't find class called: " + className);
/*  682:     */     }
/*  683:1076 */     if (matches.size() > 1)
/*  684:     */     {
/*  685:1077 */       StringBuffer sb = new StringBuffer("More than one possibility matched '" + className + "':\n");
/*  686:1079 */       for (String s : matches) {
/*  687:1080 */         sb.append("  " + s + '\n');
/*  688:     */       }
/*  689:1082 */       throw new Exception(sb.toString());
/*  690:     */     }
/*  691:1085 */     className = (String)matches.get(0);
/*  692:     */     
/*  693:1087 */     Class<?> c = null;
/*  694:     */     try
/*  695:     */     {
/*  696:1089 */       c = Class.forName(className);
/*  697:     */     }
/*  698:     */     catch (Exception ex)
/*  699:     */     {
/*  700:1091 */       throw new Exception("Can't find class called: " + className);
/*  701:     */     }
/*  702:1094 */     Object o = c.newInstance();
/*  703:1095 */     if (((o instanceof OptionHandler)) && (options != null))
/*  704:     */     {
/*  705:1096 */       ((OptionHandler)o).setOptions(options);
/*  706:1097 */       checkForRemainingOptions(options);
/*  707:     */     }
/*  708:1099 */     return o;
/*  709:     */   }
/*  710:     */   
/*  711:     */   protected static Object forNameNoSchemeMatch(Class classType, String className, String[] options)
/*  712:     */     throws Exception
/*  713:     */   {
/*  714:1133 */     Class c = null;
/*  715:     */     try
/*  716:     */     {
/*  717:1135 */       c = Class.forName(className);
/*  718:     */     }
/*  719:     */     catch (Exception ex)
/*  720:     */     {
/*  721:1137 */       throw new Exception("Can't find class called: " + className);
/*  722:     */     }
/*  723:1139 */     if ((classType != null) && (!classType.isAssignableFrom(c))) {
/*  724:1140 */       throw new Exception(classType.getName() + " is not assignable from " + className);
/*  725:     */     }
/*  726:1143 */     Object o = c.newInstance();
/*  727:1144 */     if (((o instanceof OptionHandler)) && (options != null))
/*  728:     */     {
/*  729:1145 */       ((OptionHandler)o).setOptions(options);
/*  730:1146 */       checkForRemainingOptions(options);
/*  731:     */     }
/*  732:1148 */     return o;
/*  733:     */   }
/*  734:     */   
/*  735:     */   public static String toCommandLine(Object obj)
/*  736:     */   {
/*  737:1162 */     StringBuffer result = new StringBuffer();
/*  738:1164 */     if (obj != null)
/*  739:     */     {
/*  740:1165 */       result.append(obj.getClass().getName());
/*  741:1166 */       if ((obj instanceof OptionHandler)) {
/*  742:1167 */         result.append(" " + joinOptions(((OptionHandler)obj).getOptions()));
/*  743:     */       }
/*  744:     */     }
/*  745:1171 */     return result.toString().trim();
/*  746:     */   }
/*  747:     */   
/*  748:     */   public static double info(int[] counts)
/*  749:     */   {
/*  750:1183 */     int total = 0;
/*  751:1184 */     double x = 0.0D;
/*  752:1185 */     for (int count : counts)
/*  753:     */     {
/*  754:1186 */       x -= xlogx(count);
/*  755:1187 */       total += count;
/*  756:     */     }
/*  757:1189 */     return x + xlogx(total);
/*  758:     */   }
/*  759:     */   
/*  760:     */   public static boolean smOrEq(double a, double b)
/*  761:     */   {
/*  762:1200 */     return (a - b < SMALL) || (a <= b);
/*  763:     */   }
/*  764:     */   
/*  765:     */   public static boolean grOrEq(double a, double b)
/*  766:     */   {
/*  767:1211 */     return (b - a < SMALL) || (a >= b);
/*  768:     */   }
/*  769:     */   
/*  770:     */   public static boolean sm(double a, double b)
/*  771:     */   {
/*  772:1222 */     return b - a > SMALL;
/*  773:     */   }
/*  774:     */   
/*  775:     */   public static boolean gr(double a, double b)
/*  776:     */   {
/*  777:1233 */     return a - b > SMALL;
/*  778:     */   }
/*  779:     */   
/*  780:     */   public static int kthSmallestValue(int[] array, int k)
/*  781:     */   {
/*  782:1245 */     int[] index = initialIndex(array.length);
/*  783:1246 */     return array[index[select(array, index, 0, array.length - 1, k)]];
/*  784:     */   }
/*  785:     */   
/*  786:     */   public static double kthSmallestValue(double[] array, int k)
/*  787:     */   {
/*  788:1258 */     int[] index = initialIndex(array.length);
/*  789:1259 */     return array[index[select(array, index, 0, array.length - 1, k)]];
/*  790:     */   }
/*  791:     */   
/*  792:     */   public static double log2(double a)
/*  793:     */   {
/*  794:1270 */     return Math.log(a) / log2;
/*  795:     */   }
/*  796:     */   
/*  797:     */   public static int maxIndex(double[] doubles)
/*  798:     */   {
/*  799:1282 */     double maximum = 0.0D;
/*  800:1283 */     int maxIndex = 0;
/*  801:1285 */     for (int i = 0; i < doubles.length; i++) {
/*  802:1286 */       if ((i == 0) || (doubles[i] > maximum))
/*  803:     */       {
/*  804:1287 */         maxIndex = i;
/*  805:1288 */         maximum = doubles[i];
/*  806:     */       }
/*  807:     */     }
/*  808:1292 */     return maxIndex;
/*  809:     */   }
/*  810:     */   
/*  811:     */   public static int maxIndex(int[] ints)
/*  812:     */   {
/*  813:1304 */     int maximum = 0;
/*  814:1305 */     int maxIndex = 0;
/*  815:1307 */     for (int i = 0; i < ints.length; i++) {
/*  816:1308 */       if ((i == 0) || (ints[i] > maximum))
/*  817:     */       {
/*  818:1309 */         maxIndex = i;
/*  819:1310 */         maximum = ints[i];
/*  820:     */       }
/*  821:     */     }
/*  822:1314 */     return maxIndex;
/*  823:     */   }
/*  824:     */   
/*  825:     */   public static double mean(double[] vector)
/*  826:     */   {
/*  827:1325 */     double sum = 0.0D;
/*  828:1327 */     if (vector.length == 0) {
/*  829:1328 */       return 0.0D;
/*  830:     */     }
/*  831:1330 */     for (double element : vector) {
/*  832:1331 */       sum += element;
/*  833:     */     }
/*  834:1333 */     return sum / vector.length;
/*  835:     */   }
/*  836:     */   
/*  837:     */   public static int minIndex(int[] ints)
/*  838:     */   {
/*  839:1345 */     int minimum = 0;
/*  840:1346 */     int minIndex = 0;
/*  841:1348 */     for (int i = 0; i < ints.length; i++) {
/*  842:1349 */       if ((i == 0) || (ints[i] < minimum))
/*  843:     */       {
/*  844:1350 */         minIndex = i;
/*  845:1351 */         minimum = ints[i];
/*  846:     */       }
/*  847:     */     }
/*  848:1355 */     return minIndex;
/*  849:     */   }
/*  850:     */   
/*  851:     */   public static int minIndex(double[] doubles)
/*  852:     */   {
/*  853:1367 */     double minimum = 0.0D;
/*  854:1368 */     int minIndex = 0;
/*  855:1370 */     for (int i = 0; i < doubles.length; i++) {
/*  856:1371 */       if ((i == 0) || (doubles[i] < minimum))
/*  857:     */       {
/*  858:1372 */         minIndex = i;
/*  859:1373 */         minimum = doubles[i];
/*  860:     */       }
/*  861:     */     }
/*  862:1377 */     return minIndex;
/*  863:     */   }
/*  864:     */   
/*  865:     */   public static void normalize(double[] doubles)
/*  866:     */   {
/*  867:1388 */     double sum = 0.0D;
/*  868:1389 */     for (double d : doubles) {
/*  869:1390 */       sum += d;
/*  870:     */     }
/*  871:1392 */     normalize(doubles, sum);
/*  872:     */   }
/*  873:     */   
/*  874:     */   public static void normalize(double[] doubles, double sum)
/*  875:     */   {
/*  876:1404 */     if (Double.isNaN(sum)) {
/*  877:1405 */       throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
/*  878:     */     }
/*  879:1407 */     if (sum == 0.0D) {
/*  880:1409 */       throw new IllegalArgumentException("Can't normalize array. Sum is zero.");
/*  881:     */     }
/*  882:1411 */     for (int i = 0; i < doubles.length; i++) {
/*  883:1412 */       doubles[i] /= sum;
/*  884:     */     }
/*  885:     */   }
/*  886:     */   
/*  887:     */   public static double[] logs2probs(double[] a)
/*  888:     */   {
/*  889:1426 */     double max = a[maxIndex(a)];
/*  890:1427 */     double sum = 0.0D;
/*  891:     */     
/*  892:1429 */     double[] result = new double[a.length];
/*  893:1430 */     for (int i = 0; i < a.length; i++)
/*  894:     */     {
/*  895:1431 */       result[i] = Math.exp(a[i] - max);
/*  896:1432 */       sum += result[i];
/*  897:     */     }
/*  898:1435 */     normalize(result, sum);
/*  899:     */     
/*  900:1437 */     return result;
/*  901:     */   }
/*  902:     */   
/*  903:     */   public static double probToLogOdds(double prob)
/*  904:     */   {
/*  905:1450 */     if ((gr(prob, 1.0D)) || (sm(prob, 0.0D))) {
/*  906:1451 */       throw new IllegalArgumentException("probToLogOdds: probability must be in [0,1] " + prob);
/*  907:     */     }
/*  908:1454 */     double p = SMALL + (1.0D - 2.0D * SMALL) * prob;
/*  909:1455 */     return Math.log(p / (1.0D - p));
/*  910:     */   }
/*  911:     */   
/*  912:     */   public static int round(double value)
/*  913:     */   {
/*  914:1467 */     int roundedValue = value > 0.0D ? (int)(value + 0.5D) : -(int)(Math.abs(value) + 0.5D);
/*  915:     */     
/*  916:     */ 
/*  917:1470 */     return roundedValue;
/*  918:     */   }
/*  919:     */   
/*  920:     */   public static int probRound(double value, Random rand)
/*  921:     */   {
/*  922:1485 */     if (value >= 0.0D)
/*  923:     */     {
/*  924:1486 */       double lower = Math.floor(value);
/*  925:1487 */       double prob = value - lower;
/*  926:1488 */       if (rand.nextDouble() < prob) {
/*  927:1489 */         return (int)lower + 1;
/*  928:     */       }
/*  929:1491 */       return (int)lower;
/*  930:     */     }
/*  931:1494 */     double lower = Math.floor(Math.abs(value));
/*  932:1495 */     double prob = Math.abs(value) - lower;
/*  933:1496 */     if (rand.nextDouble() < prob) {
/*  934:1497 */       return -((int)lower + 1);
/*  935:     */     }
/*  936:1499 */     return -(int)lower;
/*  937:     */   }
/*  938:     */   
/*  939:     */   public static void replaceMissingWithMAX_VALUE(double[] array)
/*  940:     */   {
/*  941:1512 */     for (int i = 0; i < array.length; i++) {
/*  942:1513 */       if (isMissingValue(array[i])) {
/*  943:1514 */         array[i] = 1.7976931348623157E+308D;
/*  944:     */       }
/*  945:     */     }
/*  946:     */   }
/*  947:     */   
/*  948:     */   public static double roundDouble(double value, int afterDecimalPoint)
/*  949:     */   {
/*  950:1529 */     double mask = Math.pow(10.0D, afterDecimalPoint);
/*  951:     */     
/*  952:1531 */     return Math.round(value * mask) / mask;
/*  953:     */   }
/*  954:     */   
/*  955:     */   public static int[] sort(int[] array)
/*  956:     */   {
/*  957:1545 */     int[] index = initialIndex(array.length);
/*  958:1546 */     int[] newIndex = new int[array.length];
/*  959:     */     
/*  960:     */ 
/*  961:     */ 
/*  962:1550 */     quickSort(array, index, 0, array.length - 1);
/*  963:     */     
/*  964:     */ 
/*  965:1553 */     int i = 0;
/*  966:1554 */     while (i < index.length)
/*  967:     */     {
/*  968:1555 */       int numEqual = 1;
/*  969:1556 */       for (int j = i + 1; (j < index.length) && (array[index[i]] == array[index[j]]); j++) {
/*  970:1557 */         numEqual++;
/*  971:     */       }
/*  972:1559 */       if (numEqual > 1)
/*  973:     */       {
/*  974:1560 */         int[] helpIndex = new int[numEqual];
/*  975:1561 */         for (int j = 0; j < numEqual; j++) {
/*  976:1562 */           helpIndex[j] = (i + j);
/*  977:     */         }
/*  978:1564 */         quickSort(index, helpIndex, 0, numEqual - 1);
/*  979:1565 */         for (int j = 0; j < numEqual; j++) {
/*  980:1566 */           newIndex[(i + j)] = index[helpIndex[j]];
/*  981:     */         }
/*  982:1568 */         i += numEqual;
/*  983:     */       }
/*  984:     */       else
/*  985:     */       {
/*  986:1570 */         newIndex[i] = index[i];
/*  987:1571 */         i++;
/*  988:     */       }
/*  989:     */     }
/*  990:1574 */     return newIndex;
/*  991:     */   }
/*  992:     */   
/*  993:     */   public static int[] sort(double[] array)
/*  994:     */   {
/*  995:1589 */     int[] index = initialIndex(array.length);
/*  996:1590 */     if (array.length > 1)
/*  997:     */     {
/*  998:1591 */       array = (double[])array.clone();
/*  999:1592 */       replaceMissingWithMAX_VALUE(array);
/* 1000:1593 */       quickSort(array, index, 0, array.length - 1);
/* 1001:     */     }
/* 1002:1595 */     return index;
/* 1003:     */   }
/* 1004:     */   
/* 1005:     */   public static int[] sortWithNoMissingValues(double[] array)
/* 1006:     */   {
/* 1007:1611 */     int[] index = initialIndex(array.length);
/* 1008:1612 */     if (array.length > 1) {
/* 1009:1613 */       quickSort(array, index, 0, array.length - 1);
/* 1010:     */     }
/* 1011:1615 */     return index;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   public static int[] stableSort(double[] array)
/* 1015:     */   {
/* 1016:1629 */     int[] index = initialIndex(array.length);
/* 1017:1631 */     if (array.length > 1)
/* 1018:     */     {
/* 1019:1633 */       int[] newIndex = new int[array.length];
/* 1020:     */       
/* 1021:     */ 
/* 1022:     */ 
/* 1023:1637 */       array = (double[])array.clone();
/* 1024:1638 */       replaceMissingWithMAX_VALUE(array);
/* 1025:1639 */       quickSort(array, index, 0, array.length - 1);
/* 1026:     */       
/* 1027:     */ 
/* 1028:     */ 
/* 1029:1643 */       int i = 0;
/* 1030:1644 */       while (i < index.length)
/* 1031:     */       {
/* 1032:1645 */         int numEqual = 1;
/* 1033:1646 */         for (int j = i + 1; (j < index.length) && (eq(array[index[i]], array[index[j]])); j++) {
/* 1034:1648 */           numEqual++;
/* 1035:     */         }
/* 1036:1650 */         if (numEqual > 1)
/* 1037:     */         {
/* 1038:1651 */           int[] helpIndex = new int[numEqual];
/* 1039:1652 */           for (int j = 0; j < numEqual; j++) {
/* 1040:1653 */             helpIndex[j] = (i + j);
/* 1041:     */           }
/* 1042:1655 */           quickSort(index, helpIndex, 0, numEqual - 1);
/* 1043:1656 */           for (int j = 0; j < numEqual; j++) {
/* 1044:1657 */             newIndex[(i + j)] = index[helpIndex[j]];
/* 1045:     */           }
/* 1046:1659 */           i += numEqual;
/* 1047:     */         }
/* 1048:     */         else
/* 1049:     */         {
/* 1050:1661 */           newIndex[i] = index[i];
/* 1051:1662 */           i++;
/* 1052:     */         }
/* 1053:     */       }
/* 1054:1665 */       return newIndex;
/* 1055:     */     }
/* 1056:1667 */     return index;
/* 1057:     */   }
/* 1058:     */   
/* 1059:     */   public static double variance(double[] vector)
/* 1060:     */   {
/* 1061:1679 */     if (vector.length <= 1) {
/* 1062:1680 */       return (0.0D / 0.0D);
/* 1063:     */     }
/* 1064:1682 */     double mean = 0.0D;
/* 1065:1683 */     double var = 0.0D;
/* 1066:1685 */     for (int i = 0; i < vector.length; i++)
/* 1067:     */     {
/* 1068:1686 */       double delta = vector[i] - mean;
/* 1069:1687 */       mean += delta / (i + 1);
/* 1070:1688 */       var += (vector[i] - mean) * delta;
/* 1071:     */     }
/* 1072:1691 */     var /= (vector.length - 1);
/* 1073:1694 */     if (var < 0.0D) {
/* 1074:1695 */       return 0.0D;
/* 1075:     */     }
/* 1076:1697 */     return var;
/* 1077:     */   }
/* 1078:     */   
/* 1079:     */   public static double sum(double[] doubles)
/* 1080:     */   {
/* 1081:1709 */     double sum = 0.0D;
/* 1082:1711 */     for (double d : doubles) {
/* 1083:1712 */       sum += d;
/* 1084:     */     }
/* 1085:1714 */     return sum;
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public static int sum(int[] ints)
/* 1089:     */   {
/* 1090:1725 */     int sum = 0;
/* 1091:1727 */     for (int j : ints) {
/* 1092:1728 */       sum += j;
/* 1093:     */     }
/* 1094:1730 */     return sum;
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   public static double xlogx(int c)
/* 1098:     */   {
/* 1099:1741 */     if (c == 0) {
/* 1100:1742 */       return 0.0D;
/* 1101:     */     }
/* 1102:1744 */     return c * log2(c);
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   private static int[] initialIndex(int size)
/* 1106:     */   {
/* 1107:1752 */     int[] index = new int[size];
/* 1108:1753 */     for (int i = 0; i < size; i++) {
/* 1109:1754 */       index[i] = i;
/* 1110:     */     }
/* 1111:1756 */     return index;
/* 1112:     */   }
/* 1113:     */   
/* 1114:     */   private static int sortLeftRightAndCenter(double[] array, int[] index, int l, int r)
/* 1115:     */   {
/* 1116:1766 */     int c = (l + r) / 2;
/* 1117:1767 */     conditionalSwap(array, index, l, c);
/* 1118:1768 */     conditionalSwap(array, index, l, r);
/* 1119:1769 */     conditionalSwap(array, index, c, r);
/* 1120:1770 */     return c;
/* 1121:     */   }
/* 1122:     */   
/* 1123:     */   private static void swap(int[] index, int l, int r)
/* 1124:     */   {
/* 1125:1778 */     int help = index[l];
/* 1126:1779 */     index[l] = index[r];
/* 1127:1780 */     index[r] = help;
/* 1128:     */   }
/* 1129:     */   
/* 1130:     */   private static void conditionalSwap(double[] array, int[] index, int left, int right)
/* 1131:     */   {
/* 1132:1789 */     if (array[index[left]] > array[index[right]])
/* 1133:     */     {
/* 1134:1790 */       int help = index[left];
/* 1135:1791 */       index[left] = index[right];
/* 1136:1792 */       index[right] = help;
/* 1137:     */     }
/* 1138:     */   }
/* 1139:     */   
/* 1140:     */   private static int partition(double[] array, int[] index, int l, int r, double pivot)
/* 1141:     */   {
/* 1142:     */     
/* 1143:     */     for (;;)
/* 1144:     */     {
/* 1145:1812 */       if (array[index[(++l)]] >= pivot)
/* 1146:     */       {
/* 1147:1815 */         while (array[index[(--r)]] > pivot) {}
/* 1148:1818 */         if (l >= r) {
/* 1149:1819 */           return l;
/* 1150:     */         }
/* 1151:1821 */         swap(index, l, r);
/* 1152:     */       }
/* 1153:     */     }
/* 1154:     */   }
/* 1155:     */   
/* 1156:     */   private static int partition(int[] array, int[] index, int l, int r)
/* 1157:     */   {
/* 1158:1838 */     double pivot = array[index[((l + r) / 2)]];
/* 1159:1841 */     while (l < r)
/* 1160:     */     {
/* 1161:1842 */       while ((array[index[l]] < pivot) && (l < r)) {
/* 1162:1843 */         l++;
/* 1163:     */       }
/* 1164:1845 */       while ((array[index[r]] > pivot) && (l < r)) {
/* 1165:1846 */         r--;
/* 1166:     */       }
/* 1167:1848 */       if (l < r)
/* 1168:     */       {
/* 1169:1849 */         int help = index[l];
/* 1170:1850 */         index[l] = index[r];
/* 1171:1851 */         index[r] = help;
/* 1172:1852 */         l++;
/* 1173:1853 */         r--;
/* 1174:     */       }
/* 1175:     */     }
/* 1176:1856 */     if ((l == r) && (array[index[r]] > pivot)) {
/* 1177:1857 */       r--;
/* 1178:     */     }
/* 1179:1860 */     return r;
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   private static void quickSort(double[] array, int[] index, int left, int right)
/* 1183:     */   {
/* 1184:1880 */     int diff = right - left;
/* 1185:1882 */     switch (diff)
/* 1186:     */     {
/* 1187:     */     case 0: 
/* 1188:1886 */       return;
/* 1189:     */     case 1: 
/* 1190:1890 */       conditionalSwap(array, index, left, right);
/* 1191:1891 */       return;
/* 1192:     */     case 2: 
/* 1193:1895 */       conditionalSwap(array, index, left, left + 1);
/* 1194:1896 */       conditionalSwap(array, index, left, right);
/* 1195:1897 */       conditionalSwap(array, index, left + 1, right);
/* 1196:1898 */       return;
/* 1197:     */     }
/* 1198:1902 */     int pivotLocation = sortLeftRightAndCenter(array, index, left, right);
/* 1199:     */     
/* 1200:     */ 
/* 1201:1905 */     swap(index, pivotLocation, right - 1);
/* 1202:1906 */     int center = partition(array, index, left, right, array[index[(right - 1)]]);
/* 1203:     */     
/* 1204:1908 */     swap(index, center, right - 1);
/* 1205:     */     
/* 1206:     */ 
/* 1207:1911 */     quickSort(array, index, left, center - 1);
/* 1208:1912 */     quickSort(array, index, center + 1, right);
/* 1209:     */   }
/* 1210:     */   
/* 1211:     */   private static void quickSort(int[] array, int[] index, int left, int right)
/* 1212:     */   {
/* 1213:1932 */     if (left < right)
/* 1214:     */     {
/* 1215:1933 */       int middle = partition(array, index, left, right);
/* 1216:1934 */       quickSort(array, index, left, middle);
/* 1217:1935 */       quickSort(array, index, middle + 1, right);
/* 1218:     */     }
/* 1219:     */   }
/* 1220:     */   
/* 1221:     */   private static int select(double[] array, int[] index, int left, int right, int k)
/* 1222:     */   {
/* 1223:1955 */     int diff = right - left;
/* 1224:1956 */     switch (diff)
/* 1225:     */     {
/* 1226:     */     case 0: 
/* 1227:1960 */       return left;
/* 1228:     */     case 1: 
/* 1229:1964 */       conditionalSwap(array, index, left, right);
/* 1230:1965 */       return left + k - 1;
/* 1231:     */     case 2: 
/* 1232:1969 */       conditionalSwap(array, index, left, left + 1);
/* 1233:1970 */       conditionalSwap(array, index, left, right);
/* 1234:1971 */       conditionalSwap(array, index, left + 1, right);
/* 1235:1972 */       return left + k - 1;
/* 1236:     */     }
/* 1237:1976 */     int pivotLocation = sortLeftRightAndCenter(array, index, left, right);
/* 1238:     */     
/* 1239:     */ 
/* 1240:1979 */     swap(index, pivotLocation, right - 1);
/* 1241:1980 */     int center = partition(array, index, left, right, array[index[(right - 1)]]);
/* 1242:     */     
/* 1243:1982 */     swap(index, center, right - 1);
/* 1244:1985 */     if (center - left + 1 >= k) {
/* 1245:1986 */       return select(array, index, left, center, k);
/* 1246:     */     }
/* 1247:1988 */     return select(array, index, center + 1, right, k - (center - left + 1));
/* 1248:     */   }
/* 1249:     */   
/* 1250:     */   public static File convertToRelativePath(File absolute)
/* 1251:     */     throws Exception
/* 1252:     */   {
/* 1253:2006 */     File result = null;
/* 1254:2009 */     if (File.separator.equals("\\")) {
/* 1255:     */       try
/* 1256:     */       {
/* 1257:2012 */         String fileStr = absolute.getPath();
/* 1258:2013 */         fileStr = fileStr.substring(0, 1).toLowerCase() + fileStr.substring(1);
/* 1259:2014 */         result = createRelativePath(new File(fileStr));
/* 1260:     */       }
/* 1261:     */       catch (Exception e)
/* 1262:     */       {
/* 1263:2017 */         result = createRelativePath(absolute);
/* 1264:     */       }
/* 1265:     */     } else {
/* 1266:2020 */       result = createRelativePath(absolute);
/* 1267:     */     }
/* 1268:2023 */     return result;
/* 1269:     */   }
/* 1270:     */   
/* 1271:     */   protected static File createRelativePath(File absolute)
/* 1272:     */     throws Exception
/* 1273:     */   {
/* 1274:2035 */     File userDir = new File(System.getProperty("user.dir"));
/* 1275:2036 */     String userPath = userDir.getAbsolutePath() + File.separator;
/* 1276:2037 */     String targetPath = new File(absolute.getParent()).getPath() + File.separator;
/* 1277:     */     
/* 1278:2039 */     String fileName = absolute.getName();
/* 1279:2040 */     StringBuffer relativePath = new StringBuffer();
/* 1280:     */     
/* 1281:     */ 
/* 1282:     */ 
/* 1283:     */ 
/* 1284:     */ 
/* 1285:2046 */     int subdir = targetPath.indexOf(userPath);
/* 1286:2047 */     if (subdir == 0)
/* 1287:     */     {
/* 1288:2048 */       if (userPath.length() == targetPath.length())
/* 1289:     */       {
/* 1290:2049 */         relativePath.append(fileName);
/* 1291:     */       }
/* 1292:     */       else
/* 1293:     */       {
/* 1294:2051 */         int ll = userPath.length();
/* 1295:2052 */         relativePath.append(targetPath.substring(ll));
/* 1296:2053 */         relativePath.append(fileName);
/* 1297:     */       }
/* 1298:     */     }
/* 1299:     */     else
/* 1300:     */     {
/* 1301:2056 */       int sepCount = 0;
/* 1302:2057 */       String temp = new String(userPath);
/* 1303:2058 */       while (temp.indexOf(File.separator) != -1)
/* 1304:     */       {
/* 1305:2059 */         int ind = temp.indexOf(File.separator);
/* 1306:2060 */         sepCount++;
/* 1307:2061 */         temp = temp.substring(ind + 1, temp.length());
/* 1308:     */       }
/* 1309:2064 */       String targetTemp = new String(targetPath);
/* 1310:2065 */       String userTemp = new String(userPath);
/* 1311:2066 */       int tcount = 0;
/* 1312:2067 */       while (targetTemp.indexOf(File.separator) != -1)
/* 1313:     */       {
/* 1314:2068 */         int ind = targetTemp.indexOf(File.separator);
/* 1315:2069 */         int ind2 = userTemp.indexOf(File.separator);
/* 1316:2070 */         String tpart = targetTemp.substring(0, ind + 1);
/* 1317:2071 */         String upart = userTemp.substring(0, ind2 + 1);
/* 1318:2072 */         if (tpart.compareTo(upart) != 0)
/* 1319:     */         {
/* 1320:2073 */           if (tcount != 0) {
/* 1321:     */             break;
/* 1322:     */           }
/* 1323:2074 */           tcount = -1; break;
/* 1324:     */         }
/* 1325:2078 */         tcount++;
/* 1326:2079 */         targetTemp = targetTemp.substring(ind + 1, targetTemp.length());
/* 1327:2080 */         userTemp = userTemp.substring(ind2 + 1, userTemp.length());
/* 1328:     */       }
/* 1329:2082 */       if (tcount == -1) {
/* 1330:2084 */         throw new Exception("Can't construct a path to file relative to user dir.");
/* 1331:     */       }
/* 1332:2087 */       if (targetTemp.indexOf(File.separator) == -1) {
/* 1333:2088 */         targetTemp = "";
/* 1334:     */       }
/* 1335:2090 */       for (int i = 0; i < sepCount - tcount; i++) {
/* 1336:2091 */         relativePath.append(".." + File.separator);
/* 1337:     */       }
/* 1338:2093 */       relativePath.append(targetTemp + fileName);
/* 1339:     */     }
/* 1340:2096 */     return new File(relativePath.toString());
/* 1341:     */   }
/* 1342:     */   
/* 1343:     */   private static int select(int[] array, int[] index, int left, int right, int k)
/* 1344:     */   {
/* 1345:2115 */     if (left == right) {
/* 1346:2116 */       return left;
/* 1347:     */     }
/* 1348:2118 */     int middle = partition(array, index, left, right);
/* 1349:2119 */     if (middle - left + 1 >= k) {
/* 1350:2120 */       return select(array, index, left, middle, k);
/* 1351:     */     }
/* 1352:2122 */     return select(array, index, middle + 1, right, k - (middle - left + 1));
/* 1353:     */   }
/* 1354:     */   
/* 1355:     */   public static boolean getDontShowDialog(String dialogName)
/* 1356:     */   {
/* 1357:2137 */     File wekaHome = WekaPackageManager.WEKA_HOME;
/* 1358:2139 */     if (!wekaHome.exists()) {
/* 1359:2140 */       return false;
/* 1360:     */     }
/* 1361:2143 */     File dialogSubDir = new File(wekaHome.toString() + File.separator + "systemDialogs");
/* 1362:2145 */     if (!dialogSubDir.exists()) {
/* 1363:2146 */       return false;
/* 1364:     */     }
/* 1365:2149 */     File dialogFile = new File(dialogSubDir.toString() + File.separator + dialogName);
/* 1366:     */     
/* 1367:     */ 
/* 1368:2152 */     return dialogFile.exists();
/* 1369:     */   }
/* 1370:     */   
/* 1371:     */   public static void setDontShowDialog(String dialogName)
/* 1372:     */     throws Exception
/* 1373:     */   {
/* 1374:2165 */     File wekaHome = WekaPackageManager.WEKA_HOME;
/* 1375:2167 */     if (!wekaHome.exists()) {
/* 1376:2168 */       return;
/* 1377:     */     }
/* 1378:2171 */     File dialogSubDir = new File(wekaHome.toString() + File.separator + "systemDialogs");
/* 1379:2173 */     if ((!dialogSubDir.exists()) && 
/* 1380:2174 */       (!dialogSubDir.mkdir())) {
/* 1381:2175 */       return;
/* 1382:     */     }
/* 1383:2179 */     File dialogFile = new File(dialogSubDir.toString() + File.separator + dialogName);
/* 1384:     */     
/* 1385:2181 */     dialogFile.createNewFile();
/* 1386:     */   }
/* 1387:     */   
/* 1388:     */   public static String getDontShowDialogResponse(String dialogName)
/* 1389:     */     throws Exception
/* 1390:     */   {
/* 1391:2197 */     if (!getDontShowDialog(dialogName)) {
/* 1392:2198 */       return null;
/* 1393:     */     }
/* 1394:2201 */     File wekaHome = WekaPackageManager.WEKA_HOME;
/* 1395:2202 */     File dialogSubDir = new File(wekaHome.toString() + File.separator + "systemDialogs" + File.separator + dialogName);
/* 1396:     */     
/* 1397:     */ 
/* 1398:2205 */     BufferedReader br = new BufferedReader(new FileReader(dialogSubDir));
/* 1399:2206 */     String response = br.readLine();
/* 1400:     */     
/* 1401:2208 */     br.close();
/* 1402:2209 */     return response;
/* 1403:     */   }
/* 1404:     */   
/* 1405:     */   public static void setDontShowDialogResponse(String dialogName, String response)
/* 1406:     */     throws Exception
/* 1407:     */   {
/* 1408:2223 */     File wekaHome = WekaPackageManager.WEKA_HOME;
/* 1409:2225 */     if (!wekaHome.exists()) {
/* 1410:2226 */       return;
/* 1411:     */     }
/* 1412:2229 */     File dialogSubDir = new File(wekaHome.toString() + File.separator + "systemDialogs");
/* 1413:2231 */     if ((!dialogSubDir.exists()) && 
/* 1414:2232 */       (!dialogSubDir.mkdir())) {
/* 1415:2233 */       return;
/* 1416:     */     }
/* 1417:2237 */     File dialogFile = new File(dialogSubDir.toString() + File.separator + dialogName);
/* 1418:     */     
/* 1419:2239 */     BufferedWriter br = new BufferedWriter(new FileWriter(dialogFile));
/* 1420:2240 */     br.write(response + "\n");
/* 1421:2241 */     br.flush();
/* 1422:2242 */     br.close();
/* 1423:     */   }
/* 1424:     */   
/* 1425:     */   public static String[] breakUp(String s, int columns)
/* 1426:     */   {
/* 1427:2263 */     Vector<String> result = new Vector();
/* 1428:2264 */     String punctuation = " .,;:!?'\"";
/* 1429:2265 */     String[] lines = s.split("\n");
/* 1430:2267 */     for (int i = 0; i < lines.length; i++)
/* 1431:     */     {
/* 1432:2268 */       BreakIterator boundary = BreakIterator.getWordInstance();
/* 1433:2269 */       boundary.setText(lines[i]);
/* 1434:2270 */       int boundaryStart = boundary.first();
/* 1435:2271 */       int boundaryEnd = boundary.next();
/* 1436:2272 */       String line = "";
/* 1437:2274 */       while (boundaryEnd != -1)
/* 1438:     */       {
/* 1439:2275 */         String word = lines[i].substring(boundaryStart, boundaryEnd);
/* 1440:2276 */         if (line.length() >= columns)
/* 1441:     */         {
/* 1442:2277 */           if ((word.length() == 1) && 
/* 1443:2278 */             (punctuation.indexOf(word.charAt(0)) > -1))
/* 1444:     */           {
/* 1445:2279 */             line = line + word;
/* 1446:2280 */             word = "";
/* 1447:     */           }
/* 1448:2283 */           result.add(line);
/* 1449:2284 */           line = "";
/* 1450:     */         }
/* 1451:2286 */         line = line + word;
/* 1452:2287 */         boundaryStart = boundaryEnd;
/* 1453:2288 */         boundaryEnd = boundary.next();
/* 1454:     */       }
/* 1455:2290 */       if (line.length() > 0) {
/* 1456:2291 */         result.add(line);
/* 1457:     */       }
/* 1458:     */     }
/* 1459:2295 */     return (String[])result.toArray(new String[result.size()]);
/* 1460:     */   }
/* 1461:     */   
/* 1462:     */   public static String getGlobalInfo(Object object, boolean addCapabilities)
/* 1463:     */   {
/* 1464:2310 */     String gi = null;
/* 1465:2311 */     StringBuilder result = new StringBuilder();
/* 1466:     */     try
/* 1467:     */     {
/* 1468:2313 */       BeanInfo bi = Introspector.getBeanInfo(object.getClass());
/* 1469:2314 */       MethodDescriptor[] methods = bi.getMethodDescriptors();
/* 1470:2315 */       for (MethodDescriptor method : methods)
/* 1471:     */       {
/* 1472:2316 */         String name = method.getDisplayName();
/* 1473:2317 */         Method meth = method.getMethod();
/* 1474:2318 */         if ((name.equals("globalInfo")) && 
/* 1475:2319 */           (meth.getReturnType().equals(String.class)))
/* 1476:     */         {
/* 1477:2320 */           Object[] args = new Object[0];
/* 1478:2321 */           String globalInfo = (String)meth.invoke(object, args);
/* 1479:2322 */           gi = globalInfo;
/* 1480:2323 */           break;
/* 1481:     */         }
/* 1482:     */       }
/* 1483:     */     }
/* 1484:     */     catch (Exception ex) {}
/* 1485:2332 */     int lineWidth = 180;
/* 1486:     */     
/* 1487:2334 */     result.append("<html>");
/* 1488:2336 */     if ((gi != null) && (gi.length() > 0))
/* 1489:     */     {
/* 1490:2338 */       StringBuilder firstLine = new StringBuilder();
/* 1491:2339 */       firstLine.append("<font color=blue>");
/* 1492:2340 */       boolean addFirstBreaks = true;
/* 1493:2341 */       int indexOfDot = gi.indexOf(".");
/* 1494:2342 */       if (indexOfDot > 0)
/* 1495:     */       {
/* 1496:2343 */         firstLine.append(gi.substring(0, gi.indexOf(".")));
/* 1497:2344 */         if (gi.length() - indexOfDot < 3) {
/* 1498:2345 */           addFirstBreaks = false;
/* 1499:     */         }
/* 1500:2347 */         gi = gi.substring(indexOfDot + 1, gi.length());
/* 1501:     */       }
/* 1502:     */       else
/* 1503:     */       {
/* 1504:2349 */         firstLine.append(gi);
/* 1505:2350 */         gi = "";
/* 1506:     */       }
/* 1507:2352 */       firstLine.append("</font>");
/* 1508:2353 */       if ((addFirstBreaks) && (!gi.startsWith("\n\n")))
/* 1509:     */       {
/* 1510:2354 */         if (!gi.startsWith("\n")) {
/* 1511:2355 */           firstLine.append("<br>");
/* 1512:     */         }
/* 1513:2357 */         firstLine.append("<br>");
/* 1514:     */       }
/* 1515:2359 */       result.append(lineWrap(firstLine.toString(), lineWidth));
/* 1516:     */       
/* 1517:2361 */       result.append(lineWrap(gi, lineWidth).replace("\n", "<br>"));
/* 1518:2362 */       result.append("<br>");
/* 1519:     */     }
/* 1520:2365 */     if (addCapabilities)
/* 1521:     */     {
/* 1522:2366 */       if ((object instanceof CapabilitiesHandler))
/* 1523:     */       {
/* 1524:2367 */         if (!result.toString().endsWith("<br><br>")) {
/* 1525:2368 */           result.append("<br>");
/* 1526:     */         }
/* 1527:2370 */         String caps = PropertySheetPanel.addCapabilities("<font color=red>CAPABILITIES</font>", ((CapabilitiesHandler)object).getCapabilities());
/* 1528:     */         
/* 1529:     */ 
/* 1530:2373 */         caps = lineWrap(caps, lineWidth).replace("\n", "<br>");
/* 1531:2374 */         result.append(caps);
/* 1532:     */       }
/* 1533:2377 */       if ((object instanceof MultiInstanceCapabilitiesHandler))
/* 1534:     */       {
/* 1535:2378 */         result.append("<br>");
/* 1536:2379 */         String caps = PropertySheetPanel.addCapabilities("<font color=red>MI CAPABILITIES</font>", ((MultiInstanceCapabilitiesHandler)object).getMultiInstanceCapabilities());
/* 1537:     */         
/* 1538:     */ 
/* 1539:     */ 
/* 1540:2383 */         caps = lineWrap(caps, lineWidth).replace("\n", "<br>");
/* 1541:2384 */         result.append(caps);
/* 1542:     */       }
/* 1543:     */     }
/* 1544:2388 */     result.append("</html>");
/* 1545:2390 */     if (result.toString().equals("<html></html>")) {
/* 1546:2391 */       return null;
/* 1547:     */     }
/* 1548:2393 */     return result.toString();
/* 1549:     */   }
/* 1550:     */   
/* 1551:     */   public static String lineWrap(String input, int maxLineWidth)
/* 1552:     */   {
/* 1553:2408 */     StringBuffer sb = new StringBuffer();
/* 1554:2409 */     BreakIterator biterator = BreakIterator.getLineInstance();
/* 1555:2410 */     biterator.setText(input);
/* 1556:2411 */     int linestart = 0;
/* 1557:2412 */     int previous = 0;
/* 1558:     */     for (;;)
/* 1559:     */     {
/* 1560:2414 */       int next = biterator.next();
/* 1561:2415 */       String toAdd = input.substring(linestart, previous);
/* 1562:2416 */       if (next == -1)
/* 1563:     */       {
/* 1564:2417 */         sb.append(toAdd);
/* 1565:2418 */         break;
/* 1566:     */       }
/* 1567:2420 */       if (next - linestart > maxLineWidth)
/* 1568:     */       {
/* 1569:2421 */         sb.append(toAdd + '\n');
/* 1570:2422 */         linestart = previous;
/* 1571:     */       }
/* 1572:     */       else
/* 1573:     */       {
/* 1574:2424 */         int newLineIndex = toAdd.lastIndexOf('\n');
/* 1575:2425 */         if (newLineIndex != -1)
/* 1576:     */         {
/* 1577:2426 */           sb.append(toAdd.substring(0, newLineIndex + 1));
/* 1578:2427 */           linestart += newLineIndex + 1;
/* 1579:     */         }
/* 1580:     */       }
/* 1581:2430 */       previous = next;
/* 1582:     */     }
/* 1583:2432 */     return sb.toString();
/* 1584:     */   }
/* 1585:     */   
/* 1586:     */   public String getRevision()
/* 1587:     */   {
/* 1588:2442 */     return RevisionUtils.extract("$Revision: 12251 $");
/* 1589:     */   }
/* 1590:     */   
/* 1591:     */   public static void main(String[] ops)
/* 1592:     */   {
/* 1593:2452 */     double[] doublesWithNaN = { 4.5D, 6.7D, (0.0D / 0.0D), 3.4D, 4.8D, 1.2D, 3.4D };
/* 1594:2453 */     double[] doubles = { 4.5D, 6.7D, 6.7D, 3.4D, 4.8D, 1.2D, 3.4D, 6.7D, 6.7D, 3.4D };
/* 1595:2454 */     int[] ints = { 12, 6, 2, 18, 16, 6, 7, 5, 18, 18, 17 };
/* 1596:     */     try
/* 1597:     */     {
/* 1598:2459 */       System.out.println("First option split up:");
/* 1599:2460 */       if (ops.length > 0)
/* 1600:     */       {
/* 1601:2461 */         String[] firstOptionSplitUp = splitOptions(ops[0]);
/* 1602:2462 */         for (String element : firstOptionSplitUp) {
/* 1603:2463 */           System.out.println(element);
/* 1604:     */         }
/* 1605:     */       }
/* 1606:2466 */       System.out.println("Partitioned options: ");
/* 1607:2467 */       String[] partitionedOptions = partitionOptions(ops);
/* 1608:2468 */       for (String partitionedOption : partitionedOptions) {
/* 1609:2469 */         System.out.println(partitionedOption);
/* 1610:     */       }
/* 1611:2471 */       System.out.println("Get position of flag -f: " + getOptionPos('f', ops));
/* 1612:     */       
/* 1613:2473 */       System.out.println("Get flag -f: " + getFlag('f', ops));
/* 1614:2474 */       System.out.println("Get position of option -o: " + getOptionPos('o', ops));
/* 1615:     */       
/* 1616:2476 */       System.out.println("Get option -o: " + getOption('o', ops));
/* 1617:2477 */       System.out.println("Checking for remaining options... ");
/* 1618:2478 */       checkForRemainingOptions(ops);
/* 1619:     */       
/* 1620:     */ 
/* 1621:2481 */       System.out.println("Original array with NaN (doubles): ");
/* 1622:2482 */       for (double element : doublesWithNaN) {
/* 1623:2483 */         System.out.print(element + " ");
/* 1624:     */       }
/* 1625:2485 */       System.out.println();
/* 1626:2486 */       System.out.println("Original array (doubles): ");
/* 1627:2487 */       for (double d : doubles) {
/* 1628:2488 */         System.out.print(d + " ");
/* 1629:     */       }
/* 1630:2490 */       System.out.println();
/* 1631:2491 */       System.out.println("Original array (ints): ");
/* 1632:2492 */       for (int j : ints) {
/* 1633:2493 */         System.out.print(j + " ");
/* 1634:     */       }
/* 1635:2495 */       System.out.println();
/* 1636:2496 */       System.out.println("Correlation: " + correlation(doubles, doubles, doubles.length));
/* 1637:     */       
/* 1638:2498 */       System.out.println("Mean: " + mean(doubles));
/* 1639:2499 */       System.out.println("Variance: " + variance(doubles));
/* 1640:2500 */       System.out.println("Sum (doubles): " + sum(doubles));
/* 1641:2501 */       System.out.println("Sum (ints): " + sum(ints));
/* 1642:2502 */       System.out.println("Max index (doubles): " + maxIndex(doubles));
/* 1643:2503 */       System.out.println("Max index (ints): " + maxIndex(ints));
/* 1644:2504 */       System.out.println("Min index (doubles): " + minIndex(doubles));
/* 1645:2505 */       System.out.println("Min index (ints): " + minIndex(ints));
/* 1646:2506 */       System.out.println("Median (doubles): " + kthSmallestValue(doubles, doubles.length / 2));
/* 1647:     */       
/* 1648:2508 */       System.out.println("Median (ints): " + kthSmallestValue(ints, ints.length / 2));
/* 1649:     */       
/* 1650:     */ 
/* 1651:     */ 
/* 1652:2512 */       System.out.println("Sorted array with NaN (doubles): ");
/* 1653:2513 */       int[] sorted = sort(doublesWithNaN);
/* 1654:2514 */       for (int i = 0; i < doublesWithNaN.length; i++) {
/* 1655:2515 */         System.out.print(doublesWithNaN[sorted[i]] + " ");
/* 1656:     */       }
/* 1657:2517 */       System.out.println();
/* 1658:2518 */       System.out.println("Sorted array (doubles): ");
/* 1659:2519 */       sorted = sort(doubles);
/* 1660:2520 */       for (int i = 0; i < doubles.length; i++) {
/* 1661:2521 */         System.out.print(doubles[sorted[i]] + " ");
/* 1662:     */       }
/* 1663:2523 */       System.out.println();
/* 1664:2524 */       System.out.println("Sorted array (ints): ");
/* 1665:2525 */       sorted = sort(ints);
/* 1666:2526 */       for (int i = 0; i < ints.length; i++) {
/* 1667:2527 */         System.out.print(ints[sorted[i]] + " ");
/* 1668:     */       }
/* 1669:2529 */       System.out.println();
/* 1670:2530 */       System.out.println("Indices from stable sort (doubles): ");
/* 1671:2531 */       sorted = stableSort(doubles);
/* 1672:2532 */       for (int i = 0; i < doubles.length; i++) {
/* 1673:2533 */         System.out.print(sorted[i] + " ");
/* 1674:     */       }
/* 1675:2535 */       System.out.println();
/* 1676:2536 */       System.out.println("Indices from sort (ints): ");
/* 1677:2537 */       sorted = sort(ints);
/* 1678:2538 */       for (int i = 0; i < ints.length; i++) {
/* 1679:2539 */         System.out.print(sorted[i] + " ");
/* 1680:     */       }
/* 1681:2541 */       System.out.println();
/* 1682:2542 */       System.out.println("Normalized array (doubles): ");
/* 1683:2543 */       normalize(doubles);
/* 1684:2544 */       for (double d : doubles) {
/* 1685:2545 */         System.out.print(d + " ");
/* 1686:     */       }
/* 1687:2547 */       System.out.println();
/* 1688:2548 */       System.out.println("Normalized again (doubles): ");
/* 1689:2549 */       normalize(doubles, sum(doubles));
/* 1690:2550 */       for (double d : doubles) {
/* 1691:2551 */         System.out.print(d + " ");
/* 1692:     */       }
/* 1693:2553 */       System.out.println();
/* 1694:     */       
/* 1695:     */ 
/* 1696:2556 */       System.out.println("-4.58: " + doubleToString(-4.57826535D, 2));
/* 1697:2557 */       System.out.println("-6.78: " + doubleToString(-6.78214234D, 6, 2));
/* 1698:     */       
/* 1699:     */ 
/* 1700:2560 */       System.out.println("5.70001 == 5.7 ? " + eq(5.70001D, 5.7D));
/* 1701:2561 */       System.out.println("5.70001 > 5.7 ? " + gr(5.70001D, 5.7D));
/* 1702:2562 */       System.out.println("5.70001 >= 5.7 ? " + grOrEq(5.70001D, 5.7D));
/* 1703:2563 */       System.out.println("5.7 < 5.70001 ? " + sm(5.7D, 5.70001D));
/* 1704:2564 */       System.out.println("5.7 <= 5.70001 ? " + smOrEq(5.7D, 5.70001D));
/* 1705:     */       
/* 1706:     */ 
/* 1707:2567 */       System.out.println("Info (ints): " + info(ints));
/* 1708:2568 */       System.out.println("log2(4.6): " + log2(4.6D));
/* 1709:2569 */       System.out.println("5 * log(5): " + xlogx(5));
/* 1710:2570 */       System.out.println("5.5 rounded: " + round(5.5D));
/* 1711:2571 */       System.out.println("5.55555 rounded to 2 decimal places: " + roundDouble(5.55555D, 2));
/* 1712:     */       
/* 1713:     */ 
/* 1714:     */ 
/* 1715:2575 */       System.out.println("Array-Dimensions of 'new int[][]': " + getArrayDimensions(new int[0][]));
/* 1716:     */       
/* 1717:2577 */       System.out.println("Array-Dimensions of 'new int[][]{{1,2,3},{4,5,6}}': " + getArrayDimensions(new int[][] { { 1, 2, 3 }, { 4, 5, 6 } }));
/* 1718:     */       
/* 1719:2579 */       String[][][] s = new String[3][4][];
/* 1720:2580 */       System.out.println("Array-Dimensions of 'new String[3][4][]': " + getArrayDimensions(s));
/* 1721:     */     }
/* 1722:     */     catch (Exception e)
/* 1723:     */     {
/* 1724:2583 */       e.printStackTrace();
/* 1725:     */     }
/* 1726:     */   }
/* 1727:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Utils
 * JD-Core Version:    0.7.0.1
 */