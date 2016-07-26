/*    1:     */ package org.apache.commons.io;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedReader;
/*    5:     */ import java.io.ByteArrayInputStream;
/*    6:     */ import java.io.CharArrayWriter;
/*    7:     */ import java.io.File;
/*    8:     */ import java.io.IOException;
/*    9:     */ import java.io.InputStream;
/*   10:     */ import java.io.InputStreamReader;
/*   11:     */ import java.io.OutputStream;
/*   12:     */ import java.io.OutputStreamWriter;
/*   13:     */ import java.io.PrintWriter;
/*   14:     */ import java.io.Reader;
/*   15:     */ import java.io.StringWriter;
/*   16:     */ import java.io.Writer;
/*   17:     */ import java.util.ArrayList;
/*   18:     */ import java.util.Collection;
/*   19:     */ import java.util.Iterator;
/*   20:     */ import java.util.List;
/*   21:     */ import org.apache.commons.io.output.ByteArrayOutputStream;
/*   22:     */ 
/*   23:     */ public class IOUtils
/*   24:     */ {
/*   25:     */   public static final char DIR_SEPARATOR_UNIX = '/';
/*   26:     */   public static final char DIR_SEPARATOR_WINDOWS = '\\';
/*   27:  97 */   public static final char DIR_SEPARATOR = File.separatorChar;
/*   28:     */   public static final String LINE_SEPARATOR_UNIX = "\n";
/*   29:     */   public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
/*   30:     */   public static final String LINE_SEPARATOR;
/*   31:     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*   32:     */   
/*   33:     */   static
/*   34:     */   {
/*   35: 112 */     StringWriter buf = new StringWriter(4);
/*   36: 113 */     PrintWriter out = new PrintWriter(buf);
/*   37: 114 */     out.println();
/*   38: 115 */     LINE_SEPARATOR = buf.toString();
/*   39:     */   }
/*   40:     */   
/*   41:     */   public static void closeQuietly(Reader input)
/*   42:     */   {
/*   43:     */     try
/*   44:     */     {
/*   45: 141 */       if (input != null) {
/*   46: 142 */         input.close();
/*   47:     */       }
/*   48:     */     }
/*   49:     */     catch (IOException ioe) {}
/*   50:     */   }
/*   51:     */   
/*   52:     */   public static void closeQuietly(Writer output)
/*   53:     */   {
/*   54:     */     try
/*   55:     */     {
/*   56: 159 */       if (output != null) {
/*   57: 160 */         output.close();
/*   58:     */       }
/*   59:     */     }
/*   60:     */     catch (IOException ioe) {}
/*   61:     */   }
/*   62:     */   
/*   63:     */   public static void closeQuietly(InputStream input)
/*   64:     */   {
/*   65:     */     try
/*   66:     */     {
/*   67: 177 */       if (input != null) {
/*   68: 178 */         input.close();
/*   69:     */       }
/*   70:     */     }
/*   71:     */     catch (IOException ioe) {}
/*   72:     */   }
/*   73:     */   
/*   74:     */   public static void closeQuietly(OutputStream output)
/*   75:     */   {
/*   76:     */     try
/*   77:     */     {
/*   78: 195 */       if (output != null) {
/*   79: 196 */         output.close();
/*   80:     */       }
/*   81:     */     }
/*   82:     */     catch (IOException ioe) {}
/*   83:     */   }
/*   84:     */   
/*   85:     */   public static byte[] toByteArray(InputStream input)
/*   86:     */     throws IOException
/*   87:     */   {
/*   88: 217 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*   89: 218 */     copy(input, output);
/*   90: 219 */     return output.toByteArray();
/*   91:     */   }
/*   92:     */   
/*   93:     */   public static byte[] toByteArray(Reader input)
/*   94:     */     throws IOException
/*   95:     */   {
/*   96: 235 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*   97: 236 */     copy(input, output);
/*   98: 237 */     return output.toByteArray();
/*   99:     */   }
/*  100:     */   
/*  101:     */   public static byte[] toByteArray(Reader input, String encoding)
/*  102:     */     throws IOException
/*  103:     */   {
/*  104: 259 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*  105: 260 */     copy(input, output, encoding);
/*  106: 261 */     return output.toByteArray();
/*  107:     */   }
/*  108:     */   
/*  109:     */   /**
/*  110:     */    * @deprecated
/*  111:     */    */
/*  112:     */   public static byte[] toByteArray(String input)
/*  113:     */     throws IOException
/*  114:     */   {
/*  115: 277 */     return input.getBytes();
/*  116:     */   }
/*  117:     */   
/*  118:     */   public static char[] toCharArray(InputStream is)
/*  119:     */     throws IOException
/*  120:     */   {
/*  121: 296 */     CharArrayWriter output = new CharArrayWriter();
/*  122: 297 */     copy(is, output);
/*  123: 298 */     return output.toCharArray();
/*  124:     */   }
/*  125:     */   
/*  126:     */   public static char[] toCharArray(InputStream is, String encoding)
/*  127:     */     throws IOException
/*  128:     */   {
/*  129: 320 */     CharArrayWriter output = new CharArrayWriter();
/*  130: 321 */     copy(is, output, encoding);
/*  131: 322 */     return output.toCharArray();
/*  132:     */   }
/*  133:     */   
/*  134:     */   public static char[] toCharArray(Reader input)
/*  135:     */     throws IOException
/*  136:     */   {
/*  137: 338 */     CharArrayWriter sw = new CharArrayWriter();
/*  138: 339 */     copy(input, sw);
/*  139: 340 */     return sw.toCharArray();
/*  140:     */   }
/*  141:     */   
/*  142:     */   public static String toString(InputStream input)
/*  143:     */     throws IOException
/*  144:     */   {
/*  145: 358 */     StringWriter sw = new StringWriter();
/*  146: 359 */     copy(input, sw);
/*  147: 360 */     return sw.toString();
/*  148:     */   }
/*  149:     */   
/*  150:     */   public static String toString(InputStream input, String encoding)
/*  151:     */     throws IOException
/*  152:     */   {
/*  153: 381 */     StringWriter sw = new StringWriter();
/*  154: 382 */     copy(input, sw, encoding);
/*  155: 383 */     return sw.toString();
/*  156:     */   }
/*  157:     */   
/*  158:     */   public static String toString(Reader input)
/*  159:     */     throws IOException
/*  160:     */   {
/*  161: 398 */     StringWriter sw = new StringWriter();
/*  162: 399 */     copy(input, sw);
/*  163: 400 */     return sw.toString();
/*  164:     */   }
/*  165:     */   
/*  166:     */   /**
/*  167:     */    * @deprecated
/*  168:     */    */
/*  169:     */   public static String toString(byte[] input)
/*  170:     */     throws IOException
/*  171:     */   {
/*  172: 414 */     return new String(input);
/*  173:     */   }
/*  174:     */   
/*  175:     */   /**
/*  176:     */    * @deprecated
/*  177:     */    */
/*  178:     */   public static String toString(byte[] input, String encoding)
/*  179:     */     throws IOException
/*  180:     */   {
/*  181: 433 */     if (encoding == null) {
/*  182: 434 */       return new String(input);
/*  183:     */     }
/*  184: 436 */     return new String(input, encoding);
/*  185:     */   }
/*  186:     */   
/*  187:     */   public static List readLines(InputStream input)
/*  188:     */     throws IOException
/*  189:     */   {
/*  190: 456 */     InputStreamReader reader = new InputStreamReader(input);
/*  191: 457 */     return readLines(reader);
/*  192:     */   }
/*  193:     */   
/*  194:     */   public static List readLines(InputStream input, String encoding)
/*  195:     */     throws IOException
/*  196:     */   {
/*  197: 478 */     if (encoding == null) {
/*  198: 479 */       return readLines(input);
/*  199:     */     }
/*  200: 481 */     InputStreamReader reader = new InputStreamReader(input, encoding);
/*  201: 482 */     return readLines(reader);
/*  202:     */   }
/*  203:     */   
/*  204:     */   public static List readLines(Reader input)
/*  205:     */     throws IOException
/*  206:     */   {
/*  207: 500 */     BufferedReader reader = new BufferedReader(input);
/*  208: 501 */     List list = new ArrayList();
/*  209: 502 */     String line = reader.readLine();
/*  210: 503 */     while (line != null)
/*  211:     */     {
/*  212: 504 */       list.add(line);
/*  213: 505 */       line = reader.readLine();
/*  214:     */     }
/*  215: 507 */     return list;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public static LineIterator lineIterator(Reader reader)
/*  219:     */   {
/*  220: 540 */     return new LineIterator(reader);
/*  221:     */   }
/*  222:     */   
/*  223:     */   public static LineIterator lineIterator(InputStream input, String encoding)
/*  224:     */     throws IOException
/*  225:     */   {
/*  226: 575 */     Reader reader = null;
/*  227: 576 */     if (encoding == null) {
/*  228: 577 */       reader = new InputStreamReader(input);
/*  229:     */     } else {
/*  230: 579 */       reader = new InputStreamReader(input, encoding);
/*  231:     */     }
/*  232: 581 */     return new LineIterator(reader);
/*  233:     */   }
/*  234:     */   
/*  235:     */   public static InputStream toInputStream(String input)
/*  236:     */   {
/*  237: 594 */     byte[] bytes = input.getBytes();
/*  238: 595 */     return new ByteArrayInputStream(bytes);
/*  239:     */   }
/*  240:     */   
/*  241:     */   public static InputStream toInputStream(String input, String encoding)
/*  242:     */     throws IOException
/*  243:     */   {
/*  244: 612 */     byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
/*  245: 613 */     return new ByteArrayInputStream(bytes);
/*  246:     */   }
/*  247:     */   
/*  248:     */   public static void write(byte[] data, OutputStream output)
/*  249:     */     throws IOException
/*  250:     */   {
/*  251: 630 */     if (data != null) {
/*  252: 631 */       output.write(data);
/*  253:     */     }
/*  254:     */   }
/*  255:     */   
/*  256:     */   public static void write(byte[] data, Writer output)
/*  257:     */     throws IOException
/*  258:     */   {
/*  259: 649 */     if (data != null) {
/*  260: 650 */       output.write(new String(data));
/*  261:     */     }
/*  262:     */   }
/*  263:     */   
/*  264:     */   public static void write(byte[] data, Writer output, String encoding)
/*  265:     */     throws IOException
/*  266:     */   {
/*  267: 673 */     if (data != null) {
/*  268: 674 */       if (encoding == null) {
/*  269: 675 */         write(data, output);
/*  270:     */       } else {
/*  271: 677 */         output.write(new String(data, encoding));
/*  272:     */       }
/*  273:     */     }
/*  274:     */   }
/*  275:     */   
/*  276:     */   public static void write(char[] data, Writer output)
/*  277:     */     throws IOException
/*  278:     */   {
/*  279: 696 */     if (data != null) {
/*  280: 697 */       output.write(data);
/*  281:     */     }
/*  282:     */   }
/*  283:     */   
/*  284:     */   public static void write(char[] data, OutputStream output)
/*  285:     */     throws IOException
/*  286:     */   {
/*  287: 717 */     if (data != null) {
/*  288: 718 */       output.write(new String(data).getBytes());
/*  289:     */     }
/*  290:     */   }
/*  291:     */   
/*  292:     */   public static void write(char[] data, OutputStream output, String encoding)
/*  293:     */     throws IOException
/*  294:     */   {
/*  295: 742 */     if (data != null) {
/*  296: 743 */       if (encoding == null) {
/*  297: 744 */         write(data, output);
/*  298:     */       } else {
/*  299: 746 */         output.write(new String(data).getBytes(encoding));
/*  300:     */       }
/*  301:     */     }
/*  302:     */   }
/*  303:     */   
/*  304:     */   public static void write(String data, Writer output)
/*  305:     */     throws IOException
/*  306:     */   {
/*  307: 763 */     if (data != null) {
/*  308: 764 */       output.write(data);
/*  309:     */     }
/*  310:     */   }
/*  311:     */   
/*  312:     */   public static void write(String data, OutputStream output)
/*  313:     */     throws IOException
/*  314:     */   {
/*  315: 783 */     if (data != null) {
/*  316: 784 */       output.write(data.getBytes());
/*  317:     */     }
/*  318:     */   }
/*  319:     */   
/*  320:     */   public static void write(String data, OutputStream output, String encoding)
/*  321:     */     throws IOException
/*  322:     */   {
/*  323: 806 */     if (data != null) {
/*  324: 807 */       if (encoding == null) {
/*  325: 808 */         write(data, output);
/*  326:     */       } else {
/*  327: 810 */         output.write(data.getBytes(encoding));
/*  328:     */       }
/*  329:     */     }
/*  330:     */   }
/*  331:     */   
/*  332:     */   public static void write(StringBuffer data, Writer output)
/*  333:     */     throws IOException
/*  334:     */   {
/*  335: 828 */     if (data != null) {
/*  336: 829 */       output.write(data.toString());
/*  337:     */     }
/*  338:     */   }
/*  339:     */   
/*  340:     */   public static void write(StringBuffer data, OutputStream output)
/*  341:     */     throws IOException
/*  342:     */   {
/*  343: 848 */     if (data != null) {
/*  344: 849 */       output.write(data.toString().getBytes());
/*  345:     */     }
/*  346:     */   }
/*  347:     */   
/*  348:     */   public static void write(StringBuffer data, OutputStream output, String encoding)
/*  349:     */     throws IOException
/*  350:     */   {
/*  351: 871 */     if (data != null) {
/*  352: 872 */       if (encoding == null) {
/*  353: 873 */         write(data, output);
/*  354:     */       } else {
/*  355: 875 */         output.write(data.toString().getBytes(encoding));
/*  356:     */       }
/*  357:     */     }
/*  358:     */   }
/*  359:     */   
/*  360:     */   public static void writeLines(Collection lines, String lineEnding, OutputStream output)
/*  361:     */     throws IOException
/*  362:     */   {
/*  363: 896 */     if (lines == null) {
/*  364: 897 */       return;
/*  365:     */     }
/*  366: 899 */     if (lineEnding == null) {
/*  367: 900 */       lineEnding = LINE_SEPARATOR;
/*  368:     */     }
/*  369: 902 */     for (Iterator it = lines.iterator(); it.hasNext();)
/*  370:     */     {
/*  371: 903 */       Object line = it.next();
/*  372: 904 */       if (line != null) {
/*  373: 905 */         output.write(line.toString().getBytes());
/*  374:     */       }
/*  375: 907 */       output.write(lineEnding.getBytes());
/*  376:     */     }
/*  377:     */   }
/*  378:     */   
/*  379:     */   public static void writeLines(Collection lines, String lineEnding, OutputStream output, String encoding)
/*  380:     */     throws IOException
/*  381:     */   {
/*  382:     */     Iterator it;
/*  383: 929 */     if (encoding == null)
/*  384:     */     {
/*  385: 930 */       writeLines(lines, lineEnding, output);
/*  386:     */     }
/*  387:     */     else
/*  388:     */     {
/*  389: 932 */       if (lines == null) {
/*  390: 933 */         return;
/*  391:     */       }
/*  392: 935 */       if (lineEnding == null) {
/*  393: 936 */         lineEnding = LINE_SEPARATOR;
/*  394:     */       }
/*  395: 938 */       for (it = lines.iterator(); it.hasNext();)
/*  396:     */       {
/*  397: 939 */         Object line = it.next();
/*  398: 940 */         if (line != null) {
/*  399: 941 */           output.write(line.toString().getBytes(encoding));
/*  400:     */         }
/*  401: 943 */         output.write(lineEnding.getBytes(encoding));
/*  402:     */       }
/*  403:     */     }
/*  404:     */   }
/*  405:     */   
/*  406:     */   public static void writeLines(Collection lines, String lineEnding, Writer writer)
/*  407:     */     throws IOException
/*  408:     */   {
/*  409: 961 */     if (lines == null) {
/*  410: 962 */       return;
/*  411:     */     }
/*  412: 964 */     if (lineEnding == null) {
/*  413: 965 */       lineEnding = LINE_SEPARATOR;
/*  414:     */     }
/*  415: 967 */     for (Iterator it = lines.iterator(); it.hasNext();)
/*  416:     */     {
/*  417: 968 */       Object line = it.next();
/*  418: 969 */       if (line != null) {
/*  419: 970 */         writer.write(line.toString());
/*  420:     */       }
/*  421: 972 */       writer.write(lineEnding);
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   public static int copy(InputStream input, OutputStream output)
/*  426:     */     throws IOException
/*  427:     */   {
/*  428: 999 */     long count = copyLarge(input, output);
/*  429:1000 */     if (count > 2147483647L) {
/*  430:1001 */       return -1;
/*  431:     */     }
/*  432:1003 */     return (int)count;
/*  433:     */   }
/*  434:     */   
/*  435:     */   public static long copyLarge(InputStream input, OutputStream output)
/*  436:     */     throws IOException
/*  437:     */   {
/*  438:1022 */     byte[] buffer = new byte[4096];
/*  439:1023 */     long count = 0L;
/*  440:1024 */     int n = 0;
/*  441:1025 */     while (-1 != (n = input.read(buffer)))
/*  442:     */     {
/*  443:1026 */       output.write(buffer, 0, n);
/*  444:1027 */       count += n;
/*  445:     */     }
/*  446:1029 */     return count;
/*  447:     */   }
/*  448:     */   
/*  449:     */   public static void copy(InputStream input, Writer output)
/*  450:     */     throws IOException
/*  451:     */   {
/*  452:1049 */     InputStreamReader in = new InputStreamReader(input);
/*  453:1050 */     copy(in, output);
/*  454:     */   }
/*  455:     */   
/*  456:     */   public static void copy(InputStream input, Writer output, String encoding)
/*  457:     */     throws IOException
/*  458:     */   {
/*  459:1074 */     if (encoding == null)
/*  460:     */     {
/*  461:1075 */       copy(input, output);
/*  462:     */     }
/*  463:     */     else
/*  464:     */     {
/*  465:1077 */       InputStreamReader in = new InputStreamReader(input, encoding);
/*  466:1078 */       copy(in, output);
/*  467:     */     }
/*  468:     */   }
/*  469:     */   
/*  470:     */   public static int copy(Reader input, Writer output)
/*  471:     */     throws IOException
/*  472:     */   {
/*  473:1104 */     long count = copyLarge(input, output);
/*  474:1105 */     if (count > 2147483647L) {
/*  475:1106 */       return -1;
/*  476:     */     }
/*  477:1108 */     return (int)count;
/*  478:     */   }
/*  479:     */   
/*  480:     */   public static long copyLarge(Reader input, Writer output)
/*  481:     */     throws IOException
/*  482:     */   {
/*  483:1125 */     char[] buffer = new char[4096];
/*  484:1126 */     long count = 0L;
/*  485:1127 */     int n = 0;
/*  486:1128 */     while (-1 != (n = input.read(buffer)))
/*  487:     */     {
/*  488:1129 */       output.write(buffer, 0, n);
/*  489:1130 */       count += n;
/*  490:     */     }
/*  491:1132 */     return count;
/*  492:     */   }
/*  493:     */   
/*  494:     */   public static void copy(Reader input, OutputStream output)
/*  495:     */     throws IOException
/*  496:     */   {
/*  497:1156 */     OutputStreamWriter out = new OutputStreamWriter(output);
/*  498:1157 */     copy(input, out);
/*  499:     */     
/*  500:     */ 
/*  501:1160 */     out.flush();
/*  502:     */   }
/*  503:     */   
/*  504:     */   public static void copy(Reader input, OutputStream output, String encoding)
/*  505:     */     throws IOException
/*  506:     */   {
/*  507:1188 */     if (encoding == null)
/*  508:     */     {
/*  509:1189 */       copy(input, output);
/*  510:     */     }
/*  511:     */     else
/*  512:     */     {
/*  513:1191 */       OutputStreamWriter out = new OutputStreamWriter(output, encoding);
/*  514:1192 */       copy(input, out);
/*  515:     */       
/*  516:     */ 
/*  517:1195 */       out.flush();
/*  518:     */     }
/*  519:     */   }
/*  520:     */   
/*  521:     */   public static boolean contentEquals(InputStream input1, InputStream input2)
/*  522:     */     throws IOException
/*  523:     */   {
/*  524:1217 */     if (!(input1 instanceof BufferedInputStream)) {
/*  525:1218 */       input1 = new BufferedInputStream(input1);
/*  526:     */     }
/*  527:1220 */     if (!(input2 instanceof BufferedInputStream)) {
/*  528:1221 */       input2 = new BufferedInputStream(input2);
/*  529:     */     }
/*  530:1224 */     int ch = input1.read();
/*  531:1225 */     while (-1 != ch)
/*  532:     */     {
/*  533:1226 */       int ch2 = input2.read();
/*  534:1227 */       if (ch != ch2) {
/*  535:1228 */         return false;
/*  536:     */       }
/*  537:1230 */       ch = input1.read();
/*  538:     */     }
/*  539:1233 */     int ch2 = input2.read();
/*  540:1234 */     return ch2 == -1;
/*  541:     */   }
/*  542:     */   
/*  543:     */   public static boolean contentEquals(Reader input1, Reader input2)
/*  544:     */     throws IOException
/*  545:     */   {
/*  546:1254 */     if (!(input1 instanceof BufferedReader)) {
/*  547:1255 */       input1 = new BufferedReader(input1);
/*  548:     */     }
/*  549:1257 */     if (!(input2 instanceof BufferedReader)) {
/*  550:1258 */       input2 = new BufferedReader(input2);
/*  551:     */     }
/*  552:1261 */     int ch = input1.read();
/*  553:1262 */     while (-1 != ch)
/*  554:     */     {
/*  555:1263 */       int ch2 = input2.read();
/*  556:1264 */       if (ch != ch2) {
/*  557:1265 */         return false;
/*  558:     */       }
/*  559:1267 */       ch = input1.read();
/*  560:     */     }
/*  561:1270 */     int ch2 = input2.read();
/*  562:1271 */     return ch2 == -1;
/*  563:     */   }
/*  564:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.IOUtils
 * JD-Core Version:    0.7.0.1
 */