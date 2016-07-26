/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.BufferedWriter;
/*    4:     */ import java.io.FileWriter;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.PrintWriter;
/*    7:     */ import java.io.Serializable;
/*    8:     */ import java.io.StringWriter;
/*    9:     */ import java.lang.management.ManagementFactory;
/*   10:     */ import java.lang.management.ThreadMXBean;
/*   11:     */ import java.text.SimpleDateFormat;
/*   12:     */ import java.util.Date;
/*   13:     */ import java.util.Random;
/*   14:     */ import java.util.logging.FileHandler;
/*   15:     */ import java.util.logging.Handler;
/*   16:     */ import java.util.logging.Level;
/*   17:     */ import java.util.logging.Logger;
/*   18:     */ import java.util.logging.SimpleFormatter;
/*   19:     */ 
/*   20:     */ public class Debug
/*   21:     */   implements Serializable, RevisionHandler
/*   22:     */ {
/*   23:     */   private static final long serialVersionUID = 66171861743328020L;
/*   24:  51 */   public static final Level ALL = Level.ALL;
/*   25:  53 */   public static final Level CONFIG = Level.CONFIG;
/*   26:  55 */   public static final Level FINE = Level.FINE;
/*   27:  57 */   public static final Level FINER = Level.FINER;
/*   28:  59 */   public static final Level FINEST = Level.FINEST;
/*   29:  61 */   public static final Level INFO = Level.INFO;
/*   30:  63 */   public static final Level OFF = Level.OFF;
/*   31:  65 */   public static final Level SEVERE = Level.SEVERE;
/*   32:  67 */   public static final Level WARNING = Level.WARNING;
/*   33:  70 */   protected boolean m_Enabled = true;
/*   34:     */   protected Log m_Log;
/*   35:  76 */   protected Clock m_Clock = new Clock();
/*   36:     */   
/*   37:     */   public static class Clock
/*   38:     */     implements Serializable, RevisionHandler
/*   39:     */   {
/*   40:     */     private static final long serialVersionUID = 4622161807307942201L;
/*   41:     */     public static final int FORMAT_MILLISECONDS = 0;
/*   42:     */     public static final int FORMAT_SECONDS = 1;
/*   43:     */     public static final int FORMAT_HHMMSS = 2;
/*   44: 105 */     public static final Tag[] TAGS_FORMAT = { new Tag(0, "milli-seconds"), new Tag(1, "seconds"), new Tag(2, "hh:mm:ss") };
/*   45: 112 */     public int m_OutputFormat = 1;
/*   46:     */     protected long m_Start;
/*   47:     */     protected long m_Stop;
/*   48:     */     protected boolean m_Running;
/*   49:     */     protected long m_ThreadID;
/*   50:     */     protected boolean m_CanMeasureCpuTime;
/*   51:     */     protected boolean m_UseCpuTime;
/*   52:     */     protected transient ThreadMXBean m_ThreadMonitor;
/*   53:     */     
/*   54:     */     public Clock()
/*   55:     */     {
/*   56: 142 */       this(true);
/*   57:     */     }
/*   58:     */     
/*   59:     */     public Clock(int format)
/*   60:     */     {
/*   61: 153 */       this(true, format);
/*   62:     */     }
/*   63:     */     
/*   64:     */     public Clock(boolean start)
/*   65:     */     {
/*   66: 164 */       this(start, 1);
/*   67:     */     }
/*   68:     */     
/*   69:     */     public Clock(boolean start, int format)
/*   70:     */     {
/*   71: 176 */       this.m_Running = false;
/*   72: 177 */       this.m_Start = 0L;
/*   73: 178 */       this.m_Stop = 0L;
/*   74: 179 */       this.m_UseCpuTime = true;
/*   75: 180 */       setOutputFormat(format);
/*   76: 182 */       if (start) {
/*   77: 183 */         start();
/*   78:     */       }
/*   79:     */     }
/*   80:     */     
/*   81:     */     protected void init()
/*   82:     */     {
/*   83: 190 */       this.m_ThreadMonitor = null;
/*   84: 191 */       this.m_ThreadMonitor = getThreadMonitor();
/*   85:     */       
/*   86:     */ 
/*   87: 194 */       this.m_CanMeasureCpuTime = this.m_ThreadMonitor.isThreadCpuTimeSupported();
/*   88:     */     }
/*   89:     */     
/*   90:     */     public boolean isCpuTime()
/*   91:     */     {
/*   92: 209 */       return (this.m_UseCpuTime) && (this.m_CanMeasureCpuTime);
/*   93:     */     }
/*   94:     */     
/*   95:     */     public void setUseCpuTime(boolean value)
/*   96:     */     {
/*   97: 220 */       this.m_UseCpuTime = value;
/*   98: 224 */       if (this.m_Running)
/*   99:     */       {
/*  100: 225 */         stop();
/*  101: 226 */         start();
/*  102:     */       }
/*  103:     */     }
/*  104:     */     
/*  105:     */     public boolean getUseCpuTime()
/*  106:     */     {
/*  107: 237 */       return this.m_UseCpuTime;
/*  108:     */     }
/*  109:     */     
/*  110:     */     protected ThreadMXBean getThreadMonitor()
/*  111:     */     {
/*  112: 248 */       if (this.m_ThreadMonitor == null)
/*  113:     */       {
/*  114: 249 */         this.m_ThreadMonitor = ManagementFactory.getThreadMXBean();
/*  115: 250 */         if ((this.m_CanMeasureCpuTime) && (!this.m_ThreadMonitor.isThreadCpuTimeEnabled())) {
/*  116: 251 */           this.m_ThreadMonitor.setThreadCpuTimeEnabled(true);
/*  117:     */         }
/*  118: 252 */         this.m_ThreadID = Thread.currentThread().getId();
/*  119:     */       }
/*  120: 255 */       return this.m_ThreadMonitor;
/*  121:     */     }
/*  122:     */     
/*  123:     */     protected long getCurrentTime()
/*  124:     */     {
/*  125:     */       long result;
/*  126:     */       long result;
/*  127: 266 */       if (isCpuTime()) {
/*  128: 267 */         result = getThreadMonitor().getThreadUserTime(this.m_ThreadID) / 1000000L;
/*  129:     */       } else {
/*  130: 269 */         result = System.currentTimeMillis();
/*  131:     */       }
/*  132: 271 */       return result;
/*  133:     */     }
/*  134:     */     
/*  135:     */     public void start()
/*  136:     */     {
/*  137: 281 */       init();
/*  138:     */       
/*  139: 283 */       this.m_Start = getCurrentTime();
/*  140: 284 */       this.m_Stop = this.m_Start;
/*  141: 285 */       this.m_Running = true;
/*  142:     */     }
/*  143:     */     
/*  144:     */     public void stop()
/*  145:     */     {
/*  146: 294 */       this.m_Stop = getCurrentTime();
/*  147: 295 */       this.m_Running = false;
/*  148:     */     }
/*  149:     */     
/*  150:     */     public long getStart()
/*  151:     */     {
/*  152: 304 */       return this.m_Start;
/*  153:     */     }
/*  154:     */     
/*  155:     */     public long getStop()
/*  156:     */     {
/*  157:     */       long result;
/*  158:     */       long result;
/*  159: 315 */       if (isRunning()) {
/*  160: 316 */         result = getCurrentTime();
/*  161:     */       } else {
/*  162: 318 */         result = this.m_Stop;
/*  163:     */       }
/*  164: 320 */       return result;
/*  165:     */     }
/*  166:     */     
/*  167:     */     public boolean isRunning()
/*  168:     */     {
/*  169: 329 */       return this.m_Running;
/*  170:     */     }
/*  171:     */     
/*  172:     */     public void setOutputFormat(int value)
/*  173:     */     {
/*  174: 339 */       if (value == 0) {
/*  175: 340 */         this.m_OutputFormat = value;
/*  176: 341 */       } else if (value == 1) {
/*  177: 342 */         this.m_OutputFormat = value;
/*  178: 343 */       } else if (value == 2) {
/*  179: 344 */         this.m_OutputFormat = value;
/*  180:     */       } else {
/*  181: 346 */         System.out.println("Format '" + value + "' is not recognized!");
/*  182:     */       }
/*  183:     */     }
/*  184:     */     
/*  185:     */     public int getOutputFormat()
/*  186:     */     {
/*  187: 356 */       return this.m_OutputFormat;
/*  188:     */     }
/*  189:     */     
/*  190:     */     public String toString()
/*  191:     */     {
/*  192: 374 */       String result = "";
/*  193: 375 */       long elapsed = getStop() - getStart();
/*  194: 377 */       switch (getOutputFormat())
/*  195:     */       {
/*  196:     */       case 2: 
/*  197: 379 */         long hours = elapsed / 3600000L;
/*  198: 380 */         elapsed %= 3600000L;
/*  199: 381 */         long mins = elapsed / 60000L;
/*  200: 382 */         elapsed %= 60000L;
/*  201: 383 */         long secs = elapsed / 1000L;
/*  202: 384 */         long msecs = elapsed % 1000L;
/*  203: 386 */         if (hours > 0L) {
/*  204: 387 */           result = result + "" + hours + ":";
/*  205:     */         }
/*  206: 389 */         if (mins < 10L) {
/*  207: 390 */           result = result + "0" + mins + ":";
/*  208:     */         } else {
/*  209: 392 */           result = result + "" + mins + ":";
/*  210:     */         }
/*  211: 394 */         if (secs < 10L) {
/*  212: 395 */           result = result + "0" + secs + ".";
/*  213:     */         } else {
/*  214: 397 */           result = result + "" + secs + ".";
/*  215:     */         }
/*  216: 399 */         result = result + Utils.doubleToString(msecs / 1000.0D, 3).replaceAll(".*\\.", "");
/*  217:     */         
/*  218: 401 */         break;
/*  219:     */       case 1: 
/*  220: 404 */         result = Utils.doubleToString(elapsed / 1000.0D, 3) + "s";
/*  221: 405 */         break;
/*  222:     */       case 0: 
/*  223: 408 */         result = "" + elapsed + "ms";
/*  224: 409 */         break;
/*  225:     */       default: 
/*  226: 412 */         result = "<unknown time format>";
/*  227:     */       }
/*  228: 415 */       return result;
/*  229:     */     }
/*  230:     */     
/*  231:     */     public String getRevision()
/*  232:     */     {
/*  233: 424 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  234:     */     }
/*  235:     */   }
/*  236:     */   
/*  237:     */   public static class Timestamp
/*  238:     */     implements Serializable, RevisionHandler
/*  239:     */   {
/*  240:     */     private static final long serialVersionUID = -6099868388466922753L;
/*  241:     */     public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*  242:     */     protected Date m_Stamp;
/*  243:     */     protected String m_Format;
/*  244:     */     protected SimpleDateFormat m_Formatter;
/*  245:     */     
/*  246:     */     public Timestamp()
/*  247:     */     {
/*  248: 460 */       this("yyyy-MM-dd HH:mm:ss");
/*  249:     */     }
/*  250:     */     
/*  251:     */     public Timestamp(String format)
/*  252:     */     {
/*  253: 471 */       this(new Date(), format);
/*  254:     */     }
/*  255:     */     
/*  256:     */     public Timestamp(Date stamp)
/*  257:     */     {
/*  258: 480 */       this(stamp, "yyyy-MM-dd HH:mm:ss");
/*  259:     */     }
/*  260:     */     
/*  261:     */     public Timestamp(Date stamp, String format)
/*  262:     */     {
/*  263: 493 */       this.m_Stamp = stamp;
/*  264: 494 */       setFormat(format);
/*  265:     */     }
/*  266:     */     
/*  267:     */     public void setFormat(String value)
/*  268:     */     {
/*  269:     */       try
/*  270:     */       {
/*  271: 505 */         this.m_Formatter = new SimpleDateFormat(value);
/*  272: 506 */         this.m_Format = value;
/*  273:     */       }
/*  274:     */       catch (Exception e)
/*  275:     */       {
/*  276: 509 */         this.m_Formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  277: 510 */         this.m_Format = "yyyy-MM-dd HH:mm:ss";
/*  278:     */       }
/*  279:     */     }
/*  280:     */     
/*  281:     */     public String getFormat()
/*  282:     */     {
/*  283: 520 */       return this.m_Format;
/*  284:     */     }
/*  285:     */     
/*  286:     */     public Date getStamp()
/*  287:     */     {
/*  288: 529 */       return this.m_Stamp;
/*  289:     */     }
/*  290:     */     
/*  291:     */     public String toString()
/*  292:     */     {
/*  293: 538 */       return this.m_Formatter.format(getStamp());
/*  294:     */     }
/*  295:     */     
/*  296:     */     public String getRevision()
/*  297:     */     {
/*  298: 547 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  299:     */     }
/*  300:     */   }
/*  301:     */   
/*  302:     */   public static class SimpleLog
/*  303:     */     implements Serializable, RevisionHandler
/*  304:     */   {
/*  305:     */     private static final long serialVersionUID = -2671928223819510830L;
/*  306: 567 */     protected String m_Filename = null;
/*  307:     */     
/*  308:     */     public SimpleLog()
/*  309:     */     {
/*  310: 573 */       this(null);
/*  311:     */     }
/*  312:     */     
/*  313:     */     public SimpleLog(String filename)
/*  314:     */     {
/*  315: 583 */       this(filename, true);
/*  316:     */     }
/*  317:     */     
/*  318:     */     public SimpleLog(String filename, boolean append)
/*  319:     */     {
/*  320: 596 */       this.m_Filename = filename;
/*  321:     */       
/*  322: 598 */       Debug.writeToFile(this.m_Filename, "--> Log started", append);
/*  323:     */     }
/*  324:     */     
/*  325:     */     public String getFilename()
/*  326:     */     {
/*  327: 607 */       return this.m_Filename;
/*  328:     */     }
/*  329:     */     
/*  330:     */     public void log(String message)
/*  331:     */     {
/*  332: 618 */       String log = new Debug.Timestamp() + " " + message;
/*  333: 620 */       if (getFilename() != null) {
/*  334: 621 */         Debug.writeToFile(getFilename(), log);
/*  335:     */       }
/*  336: 623 */       System.out.println(log);
/*  337:     */     }
/*  338:     */     
/*  339:     */     public void logSystemInfo()
/*  340:     */     {
/*  341: 633 */       log("SystemInfo:\n" + new SystemInfo().toString());
/*  342:     */     }
/*  343:     */     
/*  344:     */     public String toString()
/*  345:     */     {
/*  346: 644 */       String result = "Filename: " + getFilename();
/*  347:     */       
/*  348: 646 */       return result;
/*  349:     */     }
/*  350:     */     
/*  351:     */     public String getRevision()
/*  352:     */     {
/*  353: 655 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  354:     */     }
/*  355:     */   }
/*  356:     */   
/*  357:     */   public static class Log
/*  358:     */     implements Serializable, RevisionHandler
/*  359:     */   {
/*  360:     */     private static final long serialVersionUID = 1458435732111675823L;
/*  361: 676 */     protected transient Logger m_Logger = null;
/*  362: 679 */     protected String m_Filename = null;
/*  363:     */     protected int m_Size;
/*  364:     */     protected int m_NumFiles;
/*  365: 688 */     protected boolean m_LoggerInitFailed = false;
/*  366:     */     
/*  367:     */     public Log()
/*  368:     */     {
/*  369: 694 */       this(null);
/*  370:     */     }
/*  371:     */     
/*  372:     */     public Log(String filename)
/*  373:     */     {
/*  374: 704 */       this(filename, 1000000, 1);
/*  375:     */     }
/*  376:     */     
/*  377:     */     public Log(String filename, int size, int numFiles)
/*  378:     */     {
/*  379: 716 */       this.m_Filename = filename;
/*  380: 717 */       this.m_Size = size;
/*  381: 718 */       this.m_NumFiles = numFiles;
/*  382:     */     }
/*  383:     */     
/*  384:     */     protected Logger getLogger()
/*  385:     */     {
/*  386: 728 */       if ((this.m_Logger == null) && (!this.m_LoggerInitFailed) && 
/*  387: 729 */         (this.m_Filename != null))
/*  388:     */       {
/*  389: 730 */         this.m_Logger = Logger.getLogger(this.m_Filename);
/*  390: 731 */         Handler fh = null;
/*  391:     */         try
/*  392:     */         {
/*  393: 733 */           fh = new FileHandler(this.m_Filename, this.m_Size, this.m_NumFiles);
/*  394: 734 */           fh.setFormatter(new SimpleFormatter());
/*  395: 735 */           this.m_Logger.addHandler(fh);
/*  396: 736 */           this.m_LoggerInitFailed = false;
/*  397:     */         }
/*  398:     */         catch (Exception e)
/*  399:     */         {
/*  400: 739 */           System.out.println("Cannot init fileHandler for logger:" + e.toString());
/*  401: 740 */           this.m_Logger = null;
/*  402: 741 */           this.m_LoggerInitFailed = true;
/*  403:     */         }
/*  404:     */       }
/*  405: 746 */       return this.m_Logger;
/*  406:     */     }
/*  407:     */     
/*  408:     */     public static Level stringToLevel(String level)
/*  409:     */     {
/*  410:     */       Level result;
/*  411:     */       Level result;
/*  412: 759 */       if (level.equalsIgnoreCase("ALL"))
/*  413:     */       {
/*  414: 760 */         result = Debug.ALL;
/*  415:     */       }
/*  416:     */       else
/*  417:     */       {
/*  418:     */         Level result;
/*  419: 761 */         if (level.equalsIgnoreCase("CONFIG"))
/*  420:     */         {
/*  421: 762 */           result = Debug.CONFIG;
/*  422:     */         }
/*  423:     */         else
/*  424:     */         {
/*  425:     */           Level result;
/*  426: 763 */           if (level.equalsIgnoreCase("FINE"))
/*  427:     */           {
/*  428: 764 */             result = Debug.FINE;
/*  429:     */           }
/*  430:     */           else
/*  431:     */           {
/*  432:     */             Level result;
/*  433: 765 */             if (level.equalsIgnoreCase("FINER"))
/*  434:     */             {
/*  435: 766 */               result = Debug.FINER;
/*  436:     */             }
/*  437:     */             else
/*  438:     */             {
/*  439:     */               Level result;
/*  440: 767 */               if (level.equalsIgnoreCase("FINEST"))
/*  441:     */               {
/*  442: 768 */                 result = Debug.FINEST;
/*  443:     */               }
/*  444:     */               else
/*  445:     */               {
/*  446:     */                 Level result;
/*  447: 769 */                 if (level.equalsIgnoreCase("INFO"))
/*  448:     */                 {
/*  449: 770 */                   result = Debug.INFO;
/*  450:     */                 }
/*  451:     */                 else
/*  452:     */                 {
/*  453:     */                   Level result;
/*  454: 771 */                   if (level.equalsIgnoreCase("OFF"))
/*  455:     */                   {
/*  456: 772 */                     result = Debug.OFF;
/*  457:     */                   }
/*  458:     */                   else
/*  459:     */                   {
/*  460:     */                     Level result;
/*  461: 773 */                     if (level.equalsIgnoreCase("SEVERE"))
/*  462:     */                     {
/*  463: 774 */                       result = Debug.SEVERE;
/*  464:     */                     }
/*  465:     */                     else
/*  466:     */                     {
/*  467:     */                       Level result;
/*  468: 775 */                       if (level.equalsIgnoreCase("WARNING")) {
/*  469: 776 */                         result = Debug.WARNING;
/*  470:     */                       } else {
/*  471: 778 */                         result = Debug.ALL;
/*  472:     */                       }
/*  473:     */                     }
/*  474:     */                   }
/*  475:     */                 }
/*  476:     */               }
/*  477:     */             }
/*  478:     */           }
/*  479:     */         }
/*  480:     */       }
/*  481: 780 */       return result;
/*  482:     */     }
/*  483:     */     
/*  484:     */     public String getFilename()
/*  485:     */     {
/*  486: 789 */       return this.m_Filename;
/*  487:     */     }
/*  488:     */     
/*  489:     */     public int getSize()
/*  490:     */     {
/*  491: 798 */       return this.m_Size;
/*  492:     */     }
/*  493:     */     
/*  494:     */     public int getNumFiles()
/*  495:     */     {
/*  496: 807 */       return this.m_NumFiles;
/*  497:     */     }
/*  498:     */     
/*  499:     */     public void log(Level level, String message)
/*  500:     */     {
/*  501: 817 */       log(level, "", message);
/*  502:     */     }
/*  503:     */     
/*  504:     */     public void log(Level level, String sourceclass, String message)
/*  505:     */     {
/*  506: 828 */       log(level, sourceclass, "", message);
/*  507:     */     }
/*  508:     */     
/*  509:     */     public void log(Level level, String sourceclass, String sourcemethod, String message)
/*  510:     */     {
/*  511: 842 */       Logger logger = getLogger();
/*  512: 844 */       if (logger != null) {
/*  513: 845 */         logger.logp(level, sourceclass, sourcemethod, message);
/*  514:     */       } else {
/*  515: 847 */         System.out.println(message);
/*  516:     */       }
/*  517:     */     }
/*  518:     */     
/*  519:     */     public void logSystemInfo()
/*  520:     */     {
/*  521: 857 */       log(Debug.INFO, "SystemInfo:\n" + new SystemInfo().toString());
/*  522:     */     }
/*  523:     */     
/*  524:     */     public String toString()
/*  525:     */     {
/*  526: 868 */       String result = "Filename: " + getFilename() + ", " + "Size: " + getSize() + ", " + "# Files: " + getNumFiles();
/*  527:     */       
/*  528:     */ 
/*  529:     */ 
/*  530: 872 */       return result;
/*  531:     */     }
/*  532:     */     
/*  533:     */     public String getRevision()
/*  534:     */     {
/*  535: 881 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  536:     */     }
/*  537:     */   }
/*  538:     */   
/*  539:     */   public static class Random
/*  540:     */     extends Random
/*  541:     */     implements Serializable, RevisionHandler
/*  542:     */   {
/*  543:     */     private static final long serialVersionUID = 1256846887618333956L;
/*  544: 902 */     protected boolean m_Debug = false;
/*  545:     */     protected long m_ID;
/*  546:     */     protected static long m_CurrentID;
/*  547: 911 */     protected Debug.Log m_Log = null;
/*  548:     */     
/*  549:     */     public Random()
/*  550:     */     {
/*  551: 917 */       this(false);
/*  552:     */     }
/*  553:     */     
/*  554:     */     public Random(long seed)
/*  555:     */     {
/*  556: 927 */       this(seed, false);
/*  557:     */     }
/*  558:     */     
/*  559:     */     public Random(boolean debug)
/*  560:     */     {
/*  561: 937 */       setDebug(debug);
/*  562: 938 */       this.m_ID = nextID();
/*  563: 939 */       if (getDebug()) {
/*  564: 940 */         printStackTrace();
/*  565:     */       }
/*  566:     */     }
/*  567:     */     
/*  568:     */     public Random(long seed, boolean debug)
/*  569:     */     {
/*  570: 951 */       super();
/*  571: 952 */       setDebug(debug);
/*  572: 953 */       this.m_ID = nextID();
/*  573: 954 */       if (getDebug()) {
/*  574: 955 */         printStackTrace();
/*  575:     */       }
/*  576:     */     }
/*  577:     */     
/*  578:     */     public void setDebug(boolean value)
/*  579:     */     {
/*  580: 964 */       this.m_Debug = value;
/*  581:     */     }
/*  582:     */     
/*  583:     */     public boolean getDebug()
/*  584:     */     {
/*  585: 973 */       return this.m_Debug;
/*  586:     */     }
/*  587:     */     
/*  588:     */     public void setLog(Debug.Log value)
/*  589:     */     {
/*  590: 982 */       this.m_Log = value;
/*  591:     */     }
/*  592:     */     
/*  593:     */     public Debug.Log getLog()
/*  594:     */     {
/*  595: 992 */       return this.m_Log;
/*  596:     */     }
/*  597:     */     
/*  598:     */     protected static long nextID()
/*  599:     */     {
/*  600:1001 */       m_CurrentID += 1L;
/*  601:     */       
/*  602:1003 */       return m_CurrentID;
/*  603:     */     }
/*  604:     */     
/*  605:     */     public long getID()
/*  606:     */     {
/*  607:1012 */       return this.m_ID;
/*  608:     */     }
/*  609:     */     
/*  610:     */     protected void println(String msg)
/*  611:     */     {
/*  612:1022 */       if (getDebug()) {
/*  613:1023 */         if (getLog() != null) {
/*  614:1024 */           getLog().log(Level.INFO, this.m_ID + ": " + msg);
/*  615:     */         } else {
/*  616:1026 */           System.out.println(this.m_ID + ": " + msg);
/*  617:     */         }
/*  618:     */       }
/*  619:     */     }
/*  620:     */     
/*  621:     */     public void printStackTrace()
/*  622:     */     {
/*  623:1037 */       StringWriter writer = new StringWriter();
/*  624:     */       
/*  625:     */ 
/*  626:1040 */       Throwable t = new Throwable();
/*  627:1041 */       t.fillInStackTrace();
/*  628:1042 */       t.printStackTrace(new PrintWriter(writer));
/*  629:     */       
/*  630:1044 */       println(writer.toString());
/*  631:     */     }
/*  632:     */     
/*  633:     */     public boolean nextBoolean()
/*  634:     */     {
/*  635:1054 */       boolean result = super.nextBoolean();
/*  636:1055 */       println("nextBoolean=" + result);
/*  637:1056 */       return result;
/*  638:     */     }
/*  639:     */     
/*  640:     */     public void nextBytes(byte[] bytes)
/*  641:     */     {
/*  642:1065 */       super.nextBytes(bytes);
/*  643:1066 */       println("nextBytes=" + Utils.arrayToString(bytes));
/*  644:     */     }
/*  645:     */     
/*  646:     */     public double nextDouble()
/*  647:     */     {
/*  648:1076 */       double result = super.nextDouble();
/*  649:1077 */       println("nextDouble=" + result);
/*  650:1078 */       return result;
/*  651:     */     }
/*  652:     */     
/*  653:     */     public float nextFloat()
/*  654:     */     {
/*  655:1088 */       float result = super.nextFloat();
/*  656:1089 */       println("nextFloat=" + result);
/*  657:1090 */       return result;
/*  658:     */     }
/*  659:     */     
/*  660:     */     public double nextGaussian()
/*  661:     */     {
/*  662:1101 */       double result = super.nextGaussian();
/*  663:1102 */       println("nextGaussian=" + result);
/*  664:1103 */       return result;
/*  665:     */     }
/*  666:     */     
/*  667:     */     public int nextInt()
/*  668:     */     {
/*  669:1113 */       int result = super.nextInt();
/*  670:1114 */       println("nextInt=" + result);
/*  671:1115 */       return result;
/*  672:     */     }
/*  673:     */     
/*  674:     */     public int nextInt(int n)
/*  675:     */     {
/*  676:1127 */       int result = super.nextInt(n);
/*  677:1128 */       println("nextInt(" + n + ")=" + result);
/*  678:1129 */       return result;
/*  679:     */     }
/*  680:     */     
/*  681:     */     public long nextLong()
/*  682:     */     {
/*  683:1139 */       long result = super.nextLong();
/*  684:1140 */       println("nextLong=" + result);
/*  685:1141 */       return result;
/*  686:     */     }
/*  687:     */     
/*  688:     */     public void setSeed(long seed)
/*  689:     */     {
/*  690:1150 */       super.setSeed(seed);
/*  691:1151 */       println("setSeed(" + seed + ")");
/*  692:     */     }
/*  693:     */     
/*  694:     */     public String toString()
/*  695:     */     {
/*  696:1160 */       return getClass().getName() + ": " + getID();
/*  697:     */     }
/*  698:     */     
/*  699:     */     public String getRevision()
/*  700:     */     {
/*  701:1169 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  702:     */     }
/*  703:     */   }
/*  704:     */   
/*  705:     */   public static class DBO
/*  706:     */     implements Serializable, RevisionHandler
/*  707:     */   {
/*  708:     */     static final long serialVersionUID = -5245628124742606784L;
/*  709:1185 */     public boolean m_verboseOn = false;
/*  710:1188 */     public Range m_outputTypes = new Range();
/*  711:     */     
/*  712:     */     public void setVerboseOn()
/*  713:     */     {
/*  714:1194 */       this.m_verboseOn = true;
/*  715:     */     }
/*  716:     */     
/*  717:     */     public void initializeRanges(int upper)
/*  718:     */     {
/*  719:1203 */       this.m_outputTypes.setUpper(upper);
/*  720:     */     }
/*  721:     */     
/*  722:     */     public boolean outputTypeSet(int num)
/*  723:     */     {
/*  724:1213 */       return this.m_outputTypes.isInRange(num);
/*  725:     */     }
/*  726:     */     
/*  727:     */     public boolean dl(int num)
/*  728:     */     {
/*  729:1224 */       return outputTypeSet(num);
/*  730:     */     }
/*  731:     */     
/*  732:     */     public void setOutputTypes(String list)
/*  733:     */     {
/*  734:1233 */       if (list.length() > 0)
/*  735:     */       {
/*  736:1234 */         this.m_verboseOn = true;
/*  737:     */         
/*  738:1236 */         this.m_outputTypes.setRanges(list);
/*  739:1237 */         this.m_outputTypes.setUpper(30);
/*  740:     */       }
/*  741:     */     }
/*  742:     */     
/*  743:     */     public String getOutputTypes()
/*  744:     */     {
/*  745:1247 */       return this.m_outputTypes.getRanges();
/*  746:     */     }
/*  747:     */     
/*  748:     */     public void dpln(String text)
/*  749:     */     {
/*  750:1257 */       if (this.m_verboseOn) {
/*  751:1258 */         System.out.println(text);
/*  752:     */       }
/*  753:     */     }
/*  754:     */     
/*  755:     */     public void dpln(int debugType, String text)
/*  756:     */     {
/*  757:1270 */       if (outputTypeSet(debugType)) {
/*  758:1271 */         System.out.println(text);
/*  759:     */       }
/*  760:     */     }
/*  761:     */     
/*  762:     */     public void dp(String text)
/*  763:     */     {
/*  764:1282 */       if (this.m_verboseOn) {
/*  765:1283 */         System.out.print(text);
/*  766:     */       }
/*  767:     */     }
/*  768:     */     
/*  769:     */     public void dp(int debugType, String text)
/*  770:     */     {
/*  771:1295 */       if (outputTypeSet(debugType)) {
/*  772:1296 */         System.out.print(text);
/*  773:     */       }
/*  774:     */     }
/*  775:     */     
/*  776:     */     public static void pln(String text)
/*  777:     */     {
/*  778:1307 */       System.out.println(text);
/*  779:     */     }
/*  780:     */     
/*  781:     */     public static void p(String text)
/*  782:     */     {
/*  783:1317 */       System.out.print(text);
/*  784:     */     }
/*  785:     */     
/*  786:     */     public String getRevision()
/*  787:     */     {
/*  788:1326 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  789:     */     }
/*  790:     */   }
/*  791:     */   
/*  792:     */   public Debug()
/*  793:     */   {
/*  794:1334 */     this(null);
/*  795:     */   }
/*  796:     */   
/*  797:     */   public Debug(String filename)
/*  798:     */   {
/*  799:1344 */     this(filename, 1000000, 1);
/*  800:     */   }
/*  801:     */   
/*  802:     */   public Debug(String filename, int size, int numFiles)
/*  803:     */   {
/*  804:1357 */     this.m_Log = newLog(filename, size, numFiles);
/*  805:     */   }
/*  806:     */   
/*  807:     */   public static Level stringToLevel(String level)
/*  808:     */   {
/*  809:1368 */     return Log.stringToLevel(level);
/*  810:     */   }
/*  811:     */   
/*  812:     */   public static Log newLog(String filename, int size, int numFiles)
/*  813:     */   {
/*  814:1380 */     return new Log(filename, size, numFiles);
/*  815:     */   }
/*  816:     */   
/*  817:     */   public void log(String message)
/*  818:     */   {
/*  819:1389 */     log(INFO, message);
/*  820:     */   }
/*  821:     */   
/*  822:     */   public void log(Level level, String message)
/*  823:     */   {
/*  824:1399 */     log(level, "", message);
/*  825:     */   }
/*  826:     */   
/*  827:     */   public void log(Level level, String sourceclass, String message)
/*  828:     */   {
/*  829:1410 */     log(level, sourceclass, "", message);
/*  830:     */   }
/*  831:     */   
/*  832:     */   public void log(Level level, String sourceclass, String sourcemethod, String message)
/*  833:     */   {
/*  834:1422 */     if (getEnabled()) {
/*  835:1423 */       this.m_Log.log(level, sourceclass, sourcemethod, message);
/*  836:     */     }
/*  837:     */   }
/*  838:     */   
/*  839:     */   public void setEnabled(boolean value)
/*  840:     */   {
/*  841:1432 */     this.m_Enabled = value;
/*  842:     */   }
/*  843:     */   
/*  844:     */   public boolean getEnabled()
/*  845:     */   {
/*  846:1441 */     return this.m_Enabled;
/*  847:     */   }
/*  848:     */   
/*  849:     */   public static Clock newClock()
/*  850:     */   {
/*  851:1450 */     return new Clock();
/*  852:     */   }
/*  853:     */   
/*  854:     */   public Clock getClock()
/*  855:     */   {
/*  856:1459 */     return this.m_Clock;
/*  857:     */   }
/*  858:     */   
/*  859:     */   public void startClock()
/*  860:     */   {
/*  861:1466 */     this.m_Clock.start();
/*  862:     */   }
/*  863:     */   
/*  864:     */   public void stopClock(String message)
/*  865:     */   {
/*  866:1477 */     log(message + ": " + this.m_Clock);
/*  867:     */   }
/*  868:     */   
/*  869:     */   public static Random newRandom()
/*  870:     */   {
/*  871:1487 */     return new Random(true);
/*  872:     */   }
/*  873:     */   
/*  874:     */   public static Random newRandom(int seed)
/*  875:     */   {
/*  876:1498 */     return new Random(seed, true);
/*  877:     */   }
/*  878:     */   
/*  879:     */   public static Timestamp newTimestamp()
/*  880:     */   {
/*  881:1507 */     return new Timestamp();
/*  882:     */   }
/*  883:     */   
/*  884:     */   public static String getTempDir()
/*  885:     */   {
/*  886:1516 */     return System.getProperty("java.io.tmpdir");
/*  887:     */   }
/*  888:     */   
/*  889:     */   public static String getHomeDir()
/*  890:     */   {
/*  891:1525 */     return System.getProperty("user.home");
/*  892:     */   }
/*  893:     */   
/*  894:     */   public static String getCurrentDir()
/*  895:     */   {
/*  896:1534 */     return System.getProperty("user.dir");
/*  897:     */   }
/*  898:     */   
/*  899:     */   public static boolean writeToFile(String filename, Object obj)
/*  900:     */   {
/*  901:1546 */     return writeToFile(filename, obj, true);
/*  902:     */   }
/*  903:     */   
/*  904:     */   public static boolean writeToFile(String filename, String message)
/*  905:     */   {
/*  906:1558 */     return writeToFile(filename, message, true);
/*  907:     */   }
/*  908:     */   
/*  909:     */   public static boolean writeToFile(String filename, Object obj, boolean append)
/*  910:     */   {
/*  911:1572 */     return writeToFile(filename, obj.toString(), append);
/*  912:     */   }
/*  913:     */   
/*  914:     */   public static boolean writeToFile(String filename, String message, boolean append)
/*  915:     */   {
/*  916:     */     boolean result;
/*  917:     */     try
/*  918:     */     {
/*  919:1589 */       BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append));
/*  920:1590 */       writer.write(message);
/*  921:1591 */       writer.newLine();
/*  922:1592 */       writer.flush();
/*  923:1593 */       writer.close();
/*  924:1594 */       result = true;
/*  925:     */     }
/*  926:     */     catch (Exception e)
/*  927:     */     {
/*  928:1597 */       result = false;
/*  929:     */     }
/*  930:1600 */     return result;
/*  931:     */   }
/*  932:     */   
/*  933:     */   public static boolean saveToFile(String filename, Object o)
/*  934:     */   {
/*  935:     */     boolean result;
/*  936:1613 */     if (SerializationHelper.isSerializable(o.getClass())) {
/*  937:     */       try
/*  938:     */       {
/*  939:1615 */         SerializationHelper.write(filename, o);
/*  940:1616 */         result = true;
/*  941:     */       }
/*  942:     */       catch (Exception e)
/*  943:     */       {
/*  944:1619 */         boolean result = false;
/*  945:     */       }
/*  946:     */     } else {
/*  947:1623 */       result = false;
/*  948:     */     }
/*  949:1626 */     return result;
/*  950:     */   }
/*  951:     */   
/*  952:     */   public static Object loadFromFile(String filename)
/*  953:     */   {
/*  954:     */     Object result;
/*  955:     */     try
/*  956:     */     {
/*  957:1640 */       result = SerializationHelper.read(filename);
/*  958:     */     }
/*  959:     */     catch (Exception e)
/*  960:     */     {
/*  961:1643 */       result = null;
/*  962:     */     }
/*  963:1646 */     return result;
/*  964:     */   }
/*  965:     */   
/*  966:     */   public String getRevision()
/*  967:     */   {
/*  968:1655 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  969:     */   }
/*  970:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Debug
 * JD-Core Version:    0.7.0.1
 */