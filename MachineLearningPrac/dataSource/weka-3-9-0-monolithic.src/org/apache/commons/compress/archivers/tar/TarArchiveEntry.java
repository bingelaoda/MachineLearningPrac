/*    1:     */ package org.apache.commons.compress.archivers.tar;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.IOException;
/*    5:     */ import java.util.Date;
/*    6:     */ import java.util.Locale;
/*    7:     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*    8:     */ import org.apache.commons.compress.archivers.zip.ZipEncoding;
/*    9:     */ import org.apache.commons.compress.utils.ArchiveUtils;
/*   10:     */ 
/*   11:     */ public class TarArchiveEntry
/*   12:     */   implements TarConstants, ArchiveEntry
/*   13:     */ {
/*   14: 118 */   private String name = "";
/*   15:     */   private int mode;
/*   16: 124 */   private long userId = 0L;
/*   17: 127 */   private long groupId = 0L;
/*   18: 130 */   private long size = 0L;
/*   19:     */   private long modTime;
/*   20:     */   private boolean checkSumOK;
/*   21:     */   private byte linkFlag;
/*   22: 142 */   private String linkName = "";
/*   23: 145 */   private String magic = "";
/*   24: 147 */   private String version = "00";
/*   25:     */   private String userName;
/*   26: 153 */   private String groupName = "";
/*   27: 156 */   private int devMajor = 0;
/*   28: 159 */   private int devMinor = 0;
/*   29:     */   private boolean isExtended;
/*   30:     */   private long realSize;
/*   31:     */   private final File file;
/*   32:     */   public static final int MAX_NAMELEN = 31;
/*   33:     */   public static final int DEFAULT_DIR_MODE = 16877;
/*   34:     */   public static final int DEFAULT_FILE_MODE = 33188;
/*   35:     */   public static final int MILLIS_PER_SECOND = 1000;
/*   36:     */   
/*   37:     */   private TarArchiveEntry()
/*   38:     */   {
/*   39: 186 */     String user = System.getProperty("user.name", "");
/*   40: 188 */     if (user.length() > 31) {
/*   41: 189 */       user = user.substring(0, 31);
/*   42:     */     }
/*   43: 192 */     this.userName = user;
/*   44: 193 */     this.file = null;
/*   45:     */   }
/*   46:     */   
/*   47:     */   public TarArchiveEntry(String name)
/*   48:     */   {
/*   49: 203 */     this(name, false);
/*   50:     */   }
/*   51:     */   
/*   52:     */   public TarArchiveEntry(String name, boolean preserveLeadingSlashes)
/*   53:     */   {
/*   54: 217 */     this();
/*   55:     */     
/*   56: 219 */     name = normalizeFileName(name, preserveLeadingSlashes);
/*   57: 220 */     boolean isDir = name.endsWith("/");
/*   58:     */     
/*   59: 222 */     this.name = name;
/*   60: 223 */     this.mode = (isDir ? 16877 : 33188);
/*   61: 224 */     this.linkFlag = (isDir ? 53 : 48);
/*   62: 225 */     this.modTime = (new Date().getTime() / 1000L);
/*   63: 226 */     this.userName = "";
/*   64:     */   }
/*   65:     */   
/*   66:     */   public TarArchiveEntry(String name, byte linkFlag)
/*   67:     */   {
/*   68: 236 */     this(name, linkFlag, false);
/*   69:     */   }
/*   70:     */   
/*   71:     */   public TarArchiveEntry(String name, byte linkFlag, boolean preserveLeadingSlashes)
/*   72:     */   {
/*   73: 250 */     this(name, preserveLeadingSlashes);
/*   74: 251 */     this.linkFlag = linkFlag;
/*   75: 252 */     if (linkFlag == 76)
/*   76:     */     {
/*   77: 253 */       this.magic = "ustar ";
/*   78: 254 */       this.version = "";
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   public TarArchiveEntry(File file)
/*   83:     */   {
/*   84: 266 */     this(file, file.getPath());
/*   85:     */   }
/*   86:     */   
/*   87:     */   public TarArchiveEntry(File file, String fileName)
/*   88:     */   {
/*   89: 277 */     String normalizedName = normalizeFileName(fileName, false);
/*   90: 278 */     this.file = file;
/*   91: 280 */     if (file.isDirectory())
/*   92:     */     {
/*   93: 281 */       this.mode = 16877;
/*   94: 282 */       this.linkFlag = 53;
/*   95:     */       
/*   96: 284 */       int nameLength = normalizedName.length();
/*   97: 285 */       if ((nameLength == 0) || (normalizedName.charAt(nameLength - 1) != '/')) {
/*   98: 286 */         this.name = (normalizedName + "/");
/*   99:     */       } else {
/*  100: 288 */         this.name = normalizedName;
/*  101:     */       }
/*  102:     */     }
/*  103:     */     else
/*  104:     */     {
/*  105: 291 */       this.mode = 33188;
/*  106: 292 */       this.linkFlag = 48;
/*  107: 293 */       this.size = file.length();
/*  108: 294 */       this.name = normalizedName;
/*  109:     */     }
/*  110: 297 */     this.modTime = (file.lastModified() / 1000L);
/*  111: 298 */     this.userName = "";
/*  112:     */   }
/*  113:     */   
/*  114:     */   public TarArchiveEntry(byte[] headerBuf)
/*  115:     */   {
/*  116: 309 */     this();
/*  117: 310 */     parseTarHeader(headerBuf);
/*  118:     */   }
/*  119:     */   
/*  120:     */   public TarArchiveEntry(byte[] headerBuf, ZipEncoding encoding)
/*  121:     */     throws IOException
/*  122:     */   {
/*  123: 325 */     this();
/*  124: 326 */     parseTarHeader(headerBuf, encoding);
/*  125:     */   }
/*  126:     */   
/*  127:     */   public boolean equals(TarArchiveEntry it)
/*  128:     */   {
/*  129: 337 */     return getName().equals(it.getName());
/*  130:     */   }
/*  131:     */   
/*  132:     */   public boolean equals(Object it)
/*  133:     */   {
/*  134: 349 */     if ((it == null) || (getClass() != it.getClass())) {
/*  135: 350 */       return false;
/*  136:     */     }
/*  137: 352 */     return equals((TarArchiveEntry)it);
/*  138:     */   }
/*  139:     */   
/*  140:     */   public int hashCode()
/*  141:     */   {
/*  142: 362 */     return getName().hashCode();
/*  143:     */   }
/*  144:     */   
/*  145:     */   public boolean isDescendent(TarArchiveEntry desc)
/*  146:     */   {
/*  147: 374 */     return desc.getName().startsWith(getName());
/*  148:     */   }
/*  149:     */   
/*  150:     */   public String getName()
/*  151:     */   {
/*  152: 383 */     return this.name;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public void setName(String name)
/*  156:     */   {
/*  157: 392 */     this.name = normalizeFileName(name, false);
/*  158:     */   }
/*  159:     */   
/*  160:     */   public void setMode(int mode)
/*  161:     */   {
/*  162: 401 */     this.mode = mode;
/*  163:     */   }
/*  164:     */   
/*  165:     */   public String getLinkName()
/*  166:     */   {
/*  167: 410 */     return this.linkName;
/*  168:     */   }
/*  169:     */   
/*  170:     */   public void setLinkName(String link)
/*  171:     */   {
/*  172: 421 */     this.linkName = link;
/*  173:     */   }
/*  174:     */   
/*  175:     */   @Deprecated
/*  176:     */   public int getUserId()
/*  177:     */   {
/*  178: 433 */     return (int)(this.userId & 0xFFFFFFFF);
/*  179:     */   }
/*  180:     */   
/*  181:     */   public void setUserId(int userId)
/*  182:     */   {
/*  183: 442 */     setUserId(userId);
/*  184:     */   }
/*  185:     */   
/*  186:     */   public long getLongUserId()
/*  187:     */   {
/*  188: 452 */     return this.userId;
/*  189:     */   }
/*  190:     */   
/*  191:     */   public void setUserId(long userId)
/*  192:     */   {
/*  193: 462 */     this.userId = userId;
/*  194:     */   }
/*  195:     */   
/*  196:     */   @Deprecated
/*  197:     */   public int getGroupId()
/*  198:     */   {
/*  199: 474 */     return (int)(this.groupId & 0xFFFFFFFF);
/*  200:     */   }
/*  201:     */   
/*  202:     */   public void setGroupId(int groupId)
/*  203:     */   {
/*  204: 483 */     setGroupId(groupId);
/*  205:     */   }
/*  206:     */   
/*  207:     */   public long getLongGroupId()
/*  208:     */   {
/*  209: 493 */     return this.groupId;
/*  210:     */   }
/*  211:     */   
/*  212:     */   public void setGroupId(long groupId)
/*  213:     */   {
/*  214: 503 */     this.groupId = groupId;
/*  215:     */   }
/*  216:     */   
/*  217:     */   public String getUserName()
/*  218:     */   {
/*  219: 512 */     return this.userName;
/*  220:     */   }
/*  221:     */   
/*  222:     */   public void setUserName(String userName)
/*  223:     */   {
/*  224: 521 */     this.userName = userName;
/*  225:     */   }
/*  226:     */   
/*  227:     */   public String getGroupName()
/*  228:     */   {
/*  229: 530 */     return this.groupName;
/*  230:     */   }
/*  231:     */   
/*  232:     */   public void setGroupName(String groupName)
/*  233:     */   {
/*  234: 539 */     this.groupName = groupName;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public void setIds(int userId, int groupId)
/*  238:     */   {
/*  239: 549 */     setUserId(userId);
/*  240: 550 */     setGroupId(groupId);
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void setNames(String userName, String groupName)
/*  244:     */   {
/*  245: 560 */     setUserName(userName);
/*  246: 561 */     setGroupName(groupName);
/*  247:     */   }
/*  248:     */   
/*  249:     */   public void setModTime(long time)
/*  250:     */   {
/*  251: 571 */     this.modTime = (time / 1000L);
/*  252:     */   }
/*  253:     */   
/*  254:     */   public void setModTime(Date time)
/*  255:     */   {
/*  256: 580 */     this.modTime = (time.getTime() / 1000L);
/*  257:     */   }
/*  258:     */   
/*  259:     */   public Date getModTime()
/*  260:     */   {
/*  261: 589 */     return new Date(this.modTime * 1000L);
/*  262:     */   }
/*  263:     */   
/*  264:     */   public Date getLastModifiedDate()
/*  265:     */   {
/*  266: 593 */     return getModTime();
/*  267:     */   }
/*  268:     */   
/*  269:     */   public boolean isCheckSumOK()
/*  270:     */   {
/*  271: 604 */     return this.checkSumOK;
/*  272:     */   }
/*  273:     */   
/*  274:     */   public File getFile()
/*  275:     */   {
/*  276: 613 */     return this.file;
/*  277:     */   }
/*  278:     */   
/*  279:     */   public int getMode()
/*  280:     */   {
/*  281: 622 */     return this.mode;
/*  282:     */   }
/*  283:     */   
/*  284:     */   public long getSize()
/*  285:     */   {
/*  286: 631 */     return this.size;
/*  287:     */   }
/*  288:     */   
/*  289:     */   public void setSize(long size)
/*  290:     */   {
/*  291: 641 */     if (size < 0L) {
/*  292: 642 */       throw new IllegalArgumentException("Size is out of range: " + size);
/*  293:     */     }
/*  294: 644 */     this.size = size;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public int getDevMajor()
/*  298:     */   {
/*  299: 654 */     return this.devMajor;
/*  300:     */   }
/*  301:     */   
/*  302:     */   public void setDevMajor(int devNo)
/*  303:     */   {
/*  304: 665 */     if (devNo < 0) {
/*  305: 666 */       throw new IllegalArgumentException("Major device number is out of range: " + devNo);
/*  306:     */     }
/*  307: 669 */     this.devMajor = devNo;
/*  308:     */   }
/*  309:     */   
/*  310:     */   public int getDevMinor()
/*  311:     */   {
/*  312: 679 */     return this.devMinor;
/*  313:     */   }
/*  314:     */   
/*  315:     */   public void setDevMinor(int devNo)
/*  316:     */   {
/*  317: 690 */     if (devNo < 0) {
/*  318: 691 */       throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
/*  319:     */     }
/*  320: 694 */     this.devMinor = devNo;
/*  321:     */   }
/*  322:     */   
/*  323:     */   public boolean isExtended()
/*  324:     */   {
/*  325: 704 */     return this.isExtended;
/*  326:     */   }
/*  327:     */   
/*  328:     */   public long getRealSize()
/*  329:     */   {
/*  330: 713 */     return this.realSize;
/*  331:     */   }
/*  332:     */   
/*  333:     */   public boolean isGNUSparse()
/*  334:     */   {
/*  335: 722 */     return this.linkFlag == 83;
/*  336:     */   }
/*  337:     */   
/*  338:     */   public boolean isGNULongLinkEntry()
/*  339:     */   {
/*  340: 731 */     return (this.linkFlag == 75) && (this.name.equals("././@LongLink"));
/*  341:     */   }
/*  342:     */   
/*  343:     */   public boolean isGNULongNameEntry()
/*  344:     */   {
/*  345: 741 */     return (this.linkFlag == 76) && (this.name.equals("././@LongLink"));
/*  346:     */   }
/*  347:     */   
/*  348:     */   public boolean isPaxHeader()
/*  349:     */   {
/*  350: 754 */     return (this.linkFlag == 120) || (this.linkFlag == 88);
/*  351:     */   }
/*  352:     */   
/*  353:     */   public boolean isGlobalPaxHeader()
/*  354:     */   {
/*  355: 766 */     return this.linkFlag == 103;
/*  356:     */   }
/*  357:     */   
/*  358:     */   public boolean isDirectory()
/*  359:     */   {
/*  360: 775 */     if (this.file != null) {
/*  361: 776 */       return this.file.isDirectory();
/*  362:     */     }
/*  363: 779 */     if (this.linkFlag == 53) {
/*  364: 780 */       return true;
/*  365:     */     }
/*  366: 783 */     if (getName().endsWith("/")) {
/*  367: 784 */       return true;
/*  368:     */     }
/*  369: 787 */     return false;
/*  370:     */   }
/*  371:     */   
/*  372:     */   public boolean isFile()
/*  373:     */   {
/*  374: 797 */     if (this.file != null) {
/*  375: 798 */       return this.file.isFile();
/*  376:     */     }
/*  377: 800 */     if ((this.linkFlag == 0) || (this.linkFlag == 48)) {
/*  378: 801 */       return true;
/*  379:     */     }
/*  380: 803 */     return !getName().endsWith("/");
/*  381:     */   }
/*  382:     */   
/*  383:     */   public boolean isSymbolicLink()
/*  384:     */   {
/*  385: 813 */     return this.linkFlag == 50;
/*  386:     */   }
/*  387:     */   
/*  388:     */   public boolean isLink()
/*  389:     */   {
/*  390: 823 */     return this.linkFlag == 49;
/*  391:     */   }
/*  392:     */   
/*  393:     */   public boolean isCharacterDevice()
/*  394:     */   {
/*  395: 833 */     return this.linkFlag == 51;
/*  396:     */   }
/*  397:     */   
/*  398:     */   public boolean isBlockDevice()
/*  399:     */   {
/*  400: 843 */     return this.linkFlag == 52;
/*  401:     */   }
/*  402:     */   
/*  403:     */   public boolean isFIFO()
/*  404:     */   {
/*  405: 853 */     return this.linkFlag == 54;
/*  406:     */   }
/*  407:     */   
/*  408:     */   public TarArchiveEntry[] getDirectoryEntries()
/*  409:     */   {
/*  410: 863 */     if ((this.file == null) || (!this.file.isDirectory())) {
/*  411: 864 */       return new TarArchiveEntry[0];
/*  412:     */     }
/*  413: 867 */     String[] list = this.file.list();
/*  414: 868 */     TarArchiveEntry[] result = new TarArchiveEntry[list.length];
/*  415: 870 */     for (int i = 0; i < list.length; i++) {
/*  416: 871 */       result[i] = new TarArchiveEntry(new File(this.file, list[i]));
/*  417:     */     }
/*  418: 874 */     return result;
/*  419:     */   }
/*  420:     */   
/*  421:     */   public void writeEntryHeader(byte[] outbuf)
/*  422:     */   {
/*  423:     */     try
/*  424:     */     {
/*  425: 886 */       writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
/*  426:     */     }
/*  427:     */     catch (IOException ex)
/*  428:     */     {
/*  429:     */       try
/*  430:     */       {
/*  431: 889 */         writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
/*  432:     */       }
/*  433:     */       catch (IOException ex2)
/*  434:     */       {
/*  435: 892 */         throw new RuntimeException(ex2);
/*  436:     */       }
/*  437:     */     }
/*  438:     */   }
/*  439:     */   
/*  440:     */   public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode)
/*  441:     */     throws IOException
/*  442:     */   {
/*  443: 910 */     int offset = 0;
/*  444:     */     
/*  445: 912 */     offset = TarUtils.formatNameBytes(this.name, outbuf, offset, 100, encoding);
/*  446:     */     
/*  447: 914 */     offset = writeEntryHeaderField(this.mode, outbuf, offset, 8, starMode);
/*  448: 915 */     offset = writeEntryHeaderField(this.userId, outbuf, offset, 8, starMode);
/*  449:     */     
/*  450: 917 */     offset = writeEntryHeaderField(this.groupId, outbuf, offset, 8, starMode);
/*  451:     */     
/*  452: 919 */     offset = writeEntryHeaderField(this.size, outbuf, offset, 12, starMode);
/*  453: 920 */     offset = writeEntryHeaderField(this.modTime, outbuf, offset, 12, starMode);
/*  454:     */     
/*  455:     */ 
/*  456: 923 */     int csOffset = offset;
/*  457: 925 */     for (int c = 0; c < 8; c++) {
/*  458: 926 */       outbuf[(offset++)] = 32;
/*  459:     */     }
/*  460: 929 */     outbuf[(offset++)] = this.linkFlag;
/*  461: 930 */     offset = TarUtils.formatNameBytes(this.linkName, outbuf, offset, 100, encoding);
/*  462:     */     
/*  463: 932 */     offset = TarUtils.formatNameBytes(this.magic, outbuf, offset, 6);
/*  464: 933 */     offset = TarUtils.formatNameBytes(this.version, outbuf, offset, 2);
/*  465: 934 */     offset = TarUtils.formatNameBytes(this.userName, outbuf, offset, 32, encoding);
/*  466:     */     
/*  467: 936 */     offset = TarUtils.formatNameBytes(this.groupName, outbuf, offset, 32, encoding);
/*  468:     */     
/*  469: 938 */     offset = writeEntryHeaderField(this.devMajor, outbuf, offset, 8, starMode);
/*  470:     */     
/*  471: 940 */     offset = writeEntryHeaderField(this.devMinor, outbuf, offset, 8, starMode);
/*  472: 943 */     while (offset < outbuf.length) {
/*  473: 944 */       outbuf[(offset++)] = 0;
/*  474:     */     }
/*  475: 947 */     long chk = TarUtils.computeCheckSum(outbuf);
/*  476:     */     
/*  477: 949 */     TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
/*  478:     */   }
/*  479:     */   
/*  480:     */   private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode)
/*  481:     */   {
/*  482: 954 */     if ((!starMode) && ((value < 0L) || (value >= 1L << 3 * (length - 1)))) {
/*  483: 959 */       return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length);
/*  484:     */     }
/*  485: 961 */     return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
/*  486:     */   }
/*  487:     */   
/*  488:     */   public void parseTarHeader(byte[] header)
/*  489:     */   {
/*  490:     */     try
/*  491:     */     {
/*  492: 973 */       parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
/*  493:     */     }
/*  494:     */     catch (IOException ex)
/*  495:     */     {
/*  496:     */       try
/*  497:     */       {
/*  498: 976 */         parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
/*  499:     */       }
/*  500:     */       catch (IOException ex2)
/*  501:     */       {
/*  502: 979 */         throw new RuntimeException(ex2);
/*  503:     */       }
/*  504:     */     }
/*  505:     */   }
/*  506:     */   
/*  507:     */   public void parseTarHeader(byte[] header, ZipEncoding encoding)
/*  508:     */     throws IOException
/*  509:     */   {
/*  510: 996 */     parseTarHeader(header, encoding, false);
/*  511:     */   }
/*  512:     */   
/*  513:     */   private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle)
/*  514:     */     throws IOException
/*  515:     */   {
/*  516:1002 */     int offset = 0;
/*  517:     */     
/*  518:1004 */     this.name = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
/*  519:     */     
/*  520:1006 */     offset += 100;
/*  521:1007 */     this.mode = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
/*  522:1008 */     offset += 8;
/*  523:1009 */     this.userId = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
/*  524:1010 */     offset += 8;
/*  525:1011 */     this.groupId = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
/*  526:1012 */     offset += 8;
/*  527:1013 */     this.size = TarUtils.parseOctalOrBinary(header, offset, 12);
/*  528:1014 */     offset += 12;
/*  529:1015 */     this.modTime = TarUtils.parseOctalOrBinary(header, offset, 12);
/*  530:1016 */     offset += 12;
/*  531:1017 */     this.checkSumOK = TarUtils.verifyCheckSum(header);
/*  532:1018 */     offset += 8;
/*  533:1019 */     this.linkFlag = header[(offset++)];
/*  534:1020 */     this.linkName = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
/*  535:     */     
/*  536:1022 */     offset += 100;
/*  537:1023 */     this.magic = TarUtils.parseName(header, offset, 6);
/*  538:1024 */     offset += 6;
/*  539:1025 */     this.version = TarUtils.parseName(header, offset, 2);
/*  540:1026 */     offset += 2;
/*  541:1027 */     this.userName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
/*  542:     */     
/*  543:1029 */     offset += 32;
/*  544:1030 */     this.groupName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
/*  545:     */     
/*  546:1032 */     offset += 32;
/*  547:1033 */     this.devMajor = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
/*  548:1034 */     offset += 8;
/*  549:1035 */     this.devMinor = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
/*  550:1036 */     offset += 8;
/*  551:     */     
/*  552:1038 */     int type = evaluateType(header);
/*  553:1039 */     switch (type)
/*  554:     */     {
/*  555:     */     case 2: 
/*  556:1041 */       offset += 12;
/*  557:1042 */       offset += 12;
/*  558:1043 */       offset += 12;
/*  559:1044 */       offset += 4;
/*  560:1045 */       offset++;
/*  561:1046 */       offset += 96;
/*  562:1047 */       this.isExtended = TarUtils.parseBoolean(header, offset);
/*  563:1048 */       offset++;
/*  564:1049 */       this.realSize = TarUtils.parseOctal(header, offset, 12);
/*  565:1050 */       offset += 12;
/*  566:1051 */       break;
/*  567:     */     case 3: 
/*  568:     */     default: 
/*  569:1055 */       String prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
/*  570:1060 */       if ((isDirectory()) && (!this.name.endsWith("/"))) {
/*  571:1061 */         this.name += "/";
/*  572:     */       }
/*  573:1063 */       if (prefix.length() > 0) {
/*  574:1064 */         this.name = (prefix + "/" + this.name);
/*  575:     */       }
/*  576:     */       break;
/*  577:     */     }
/*  578:     */   }
/*  579:     */   
/*  580:     */   private static String normalizeFileName(String fileName, boolean preserveLeadingSlashes)
/*  581:     */   {
/*  582:1076 */     String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
/*  583:1078 */     if (osname != null) {
/*  584:1083 */       if (osname.startsWith("windows"))
/*  585:     */       {
/*  586:1084 */         if (fileName.length() > 2)
/*  587:     */         {
/*  588:1085 */           char ch1 = fileName.charAt(0);
/*  589:1086 */           char ch2 = fileName.charAt(1);
/*  590:1088 */           if ((ch2 == ':') && (((ch1 >= 'a') && (ch1 <= 'z')) || ((ch1 >= 'A') && (ch1 <= 'Z')))) {
/*  591:1091 */             fileName = fileName.substring(2);
/*  592:     */           }
/*  593:     */         }
/*  594:     */       }
/*  595:1094 */       else if (osname.contains("netware"))
/*  596:     */       {
/*  597:1095 */         int colon = fileName.indexOf(':');
/*  598:1096 */         if (colon != -1) {
/*  599:1097 */           fileName = fileName.substring(colon + 1);
/*  600:     */         }
/*  601:     */       }
/*  602:     */     }
/*  603:1102 */     fileName = fileName.replace(File.separatorChar, '/');
/*  604:1107 */     while ((!preserveLeadingSlashes) && (fileName.startsWith("/"))) {
/*  605:1108 */       fileName = fileName.substring(1);
/*  606:     */     }
/*  607:1110 */     return fileName;
/*  608:     */   }
/*  609:     */   
/*  610:     */   private int evaluateType(byte[] header)
/*  611:     */   {
/*  612:1120 */     if (ArchiveUtils.matchAsciiBuffer("ustar ", header, 257, 6)) {
/*  613:1121 */       return 2;
/*  614:     */     }
/*  615:1123 */     if (ArchiveUtils.matchAsciiBuffer("", header, 257, 6)) {
/*  616:1124 */       return 3;
/*  617:     */     }
/*  618:1126 */     return 0;
/*  619:     */   }
/*  620:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.tar.TarArchiveEntry
 * JD-Core Version:    0.7.0.1
 */