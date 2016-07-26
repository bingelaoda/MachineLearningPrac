/*    1:     */ package org.apache.commons.io;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.FileInputStream;
/*    5:     */ import java.io.FileNotFoundException;
/*    6:     */ import java.io.FileOutputStream;
/*    7:     */ import java.io.IOException;
/*    8:     */ import java.io.InputStream;
/*    9:     */ import java.io.OutputStream;
/*   10:     */ import java.net.URL;
/*   11:     */ import java.util.Collection;
/*   12:     */ import java.util.Date;
/*   13:     */ import java.util.Iterator;
/*   14:     */ import java.util.LinkedList;
/*   15:     */ import java.util.List;
/*   16:     */ import java.util.zip.CRC32;
/*   17:     */ import java.util.zip.CheckedInputStream;
/*   18:     */ import java.util.zip.Checksum;
/*   19:     */ import org.apache.commons.io.filefilter.DirectoryFileFilter;
/*   20:     */ import org.apache.commons.io.filefilter.FalseFileFilter;
/*   21:     */ import org.apache.commons.io.filefilter.FileFilterUtils;
/*   22:     */ import org.apache.commons.io.filefilter.IOFileFilter;
/*   23:     */ import org.apache.commons.io.filefilter.SuffixFileFilter;
/*   24:     */ import org.apache.commons.io.filefilter.TrueFileFilter;
/*   25:     */ import org.apache.commons.io.output.NullOutputStream;
/*   26:     */ 
/*   27:     */ public class FileUtils
/*   28:     */ {
/*   29:     */   public static final long ONE_KB = 1024L;
/*   30:     */   public static final long ONE_MB = 1048576L;
/*   31:     */   public static final long ONE_GB = 1073741824L;
/*   32: 106 */   public static final File[] EMPTY_FILE_ARRAY = new File[0];
/*   33:     */   
/*   34:     */   public static FileInputStream openInputStream(File file)
/*   35:     */     throws IOException
/*   36:     */   {
/*   37: 128 */     if (file.exists())
/*   38:     */     {
/*   39: 129 */       if (file.isDirectory()) {
/*   40: 130 */         throw new IOException("File '" + file + "' exists but is a directory");
/*   41:     */       }
/*   42: 132 */       if (!file.canRead()) {
/*   43: 133 */         throw new IOException("File '" + file + "' cannot be read");
/*   44:     */       }
/*   45:     */     }
/*   46:     */     else
/*   47:     */     {
/*   48: 136 */       throw new FileNotFoundException("File '" + file + "' does not exist");
/*   49:     */     }
/*   50: 138 */     return new FileInputStream(file);
/*   51:     */   }
/*   52:     */   
/*   53:     */   public static FileOutputStream openOutputStream(File file)
/*   54:     */     throws IOException
/*   55:     */   {
/*   56: 163 */     if (file.exists())
/*   57:     */     {
/*   58: 164 */       if (file.isDirectory()) {
/*   59: 165 */         throw new IOException("File '" + file + "' exists but is a directory");
/*   60:     */       }
/*   61: 167 */       if (!file.canWrite()) {
/*   62: 168 */         throw new IOException("File '" + file + "' cannot be written to");
/*   63:     */       }
/*   64:     */     }
/*   65:     */     else
/*   66:     */     {
/*   67: 171 */       File parent = file.getParentFile();
/*   68: 172 */       if ((parent != null) && (!parent.exists()) && 
/*   69: 173 */         (!parent.mkdirs())) {
/*   70: 174 */         throw new IOException("File '" + file + "' could not be created");
/*   71:     */       }
/*   72:     */     }
/*   73: 178 */     return new FileOutputStream(file);
/*   74:     */   }
/*   75:     */   
/*   76:     */   public static String byteCountToDisplaySize(long size)
/*   77:     */   {
/*   78:     */     String displaySize;
/*   79:     */     String displaySize;
/*   80: 192 */     if (size / 1073741824L > 0L)
/*   81:     */     {
/*   82: 193 */       displaySize = String.valueOf(size / 1073741824L) + " GB";
/*   83:     */     }
/*   84:     */     else
/*   85:     */     {
/*   86:     */       String displaySize;
/*   87: 194 */       if (size / 1048576L > 0L)
/*   88:     */       {
/*   89: 195 */         displaySize = String.valueOf(size / 1048576L) + " MB";
/*   90:     */       }
/*   91:     */       else
/*   92:     */       {
/*   93:     */         String displaySize;
/*   94: 196 */         if (size / 1024L > 0L) {
/*   95: 197 */           displaySize = String.valueOf(size / 1024L) + " KB";
/*   96:     */         } else {
/*   97: 199 */           displaySize = String.valueOf(size) + " bytes";
/*   98:     */         }
/*   99:     */       }
/*  100:     */     }
/*  101: 201 */     return displaySize;
/*  102:     */   }
/*  103:     */   
/*  104:     */   public static void touch(File file)
/*  105:     */     throws IOException
/*  106:     */   {
/*  107: 218 */     if (!file.exists())
/*  108:     */     {
/*  109: 219 */       OutputStream out = openOutputStream(file);
/*  110: 220 */       IOUtils.closeQuietly(out);
/*  111:     */     }
/*  112: 222 */     boolean success = file.setLastModified(System.currentTimeMillis());
/*  113: 223 */     if (!success) {
/*  114: 224 */       throw new IOException("Unable to set the last modification time for " + file);
/*  115:     */     }
/*  116:     */   }
/*  117:     */   
/*  118:     */   public static File[] convertFileCollectionToFileArray(Collection files)
/*  119:     */   {
/*  120: 238 */     return (File[])files.toArray(new File[files.size()]);
/*  121:     */   }
/*  122:     */   
/*  123:     */   private static void innerListFiles(Collection files, File directory, IOFileFilter filter)
/*  124:     */   {
/*  125: 252 */     File[] found = directory.listFiles(filter);
/*  126: 253 */     if (found != null) {
/*  127: 254 */       for (int i = 0; i < found.length; i++) {
/*  128: 255 */         if (found[i].isDirectory()) {
/*  129: 256 */           innerListFiles(files, found[i], filter);
/*  130:     */         } else {
/*  131: 258 */           files.add(found[i]);
/*  132:     */         }
/*  133:     */       }
/*  134:     */     }
/*  135:     */   }
/*  136:     */   
/*  137:     */   public static Collection listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
/*  138:     */   {
/*  139: 291 */     if (!directory.isDirectory()) {
/*  140: 292 */       throw new IllegalArgumentException("Parameter 'directory' is not a directory");
/*  141:     */     }
/*  142: 295 */     if (fileFilter == null) {
/*  143: 296 */       throw new NullPointerException("Parameter 'fileFilter' is null");
/*  144:     */     }
/*  145: 300 */     IOFileFilter effFileFilter = FileFilterUtils.andFileFilter(fileFilter, FileFilterUtils.notFileFilter(DirectoryFileFilter.INSTANCE));
/*  146:     */     IOFileFilter effDirFilter;
/*  147:     */     IOFileFilter effDirFilter;
/*  148: 305 */     if (dirFilter == null) {
/*  149: 306 */       effDirFilter = FalseFileFilter.INSTANCE;
/*  150:     */     } else {
/*  151: 308 */       effDirFilter = FileFilterUtils.andFileFilter(dirFilter, DirectoryFileFilter.INSTANCE);
/*  152:     */     }
/*  153: 313 */     Collection files = new LinkedList();
/*  154: 314 */     innerListFiles(files, directory, FileFilterUtils.orFileFilter(effFileFilter, effDirFilter));
/*  155:     */     
/*  156: 316 */     return files;
/*  157:     */   }
/*  158:     */   
/*  159:     */   public static Iterator iterateFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
/*  160:     */   {
/*  161: 338 */     return listFiles(directory, fileFilter, dirFilter).iterator();
/*  162:     */   }
/*  163:     */   
/*  164:     */   private static String[] toSuffixes(String[] extensions)
/*  165:     */   {
/*  166: 350 */     String[] suffixes = new String[extensions.length];
/*  167: 351 */     for (int i = 0; i < extensions.length; i++) {
/*  168: 352 */       suffixes[i] = ("." + extensions[i]);
/*  169:     */     }
/*  170: 354 */     return suffixes;
/*  171:     */   }
/*  172:     */   
/*  173:     */   public static Collection listFiles(File directory, String[] extensions, boolean recursive)
/*  174:     */   {
/*  175:     */     IOFileFilter filter;
/*  176:     */     IOFileFilter filter;
/*  177: 371 */     if (extensions == null)
/*  178:     */     {
/*  179: 372 */       filter = TrueFileFilter.INSTANCE;
/*  180:     */     }
/*  181:     */     else
/*  182:     */     {
/*  183: 374 */       String[] suffixes = toSuffixes(extensions);
/*  184: 375 */       filter = new SuffixFileFilter(suffixes);
/*  185:     */     }
/*  186: 377 */     return listFiles(directory, filter, recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE);
/*  187:     */   }
/*  188:     */   
/*  189:     */   public static Iterator iterateFiles(File directory, String[] extensions, boolean recursive)
/*  190:     */   {
/*  191: 395 */     return listFiles(directory, extensions, recursive).iterator();
/*  192:     */   }
/*  193:     */   
/*  194:     */   public static boolean contentEquals(File file1, File file2)
/*  195:     */     throws IOException
/*  196:     */   {
/*  197: 415 */     boolean file1Exists = file1.exists();
/*  198: 416 */     if (file1Exists != file2.exists()) {
/*  199: 417 */       return false;
/*  200:     */     }
/*  201: 420 */     if (!file1Exists) {
/*  202: 422 */       return true;
/*  203:     */     }
/*  204: 425 */     if ((file1.isDirectory()) || (file2.isDirectory())) {
/*  205: 427 */       throw new IOException("Can't compare directories, only files");
/*  206:     */     }
/*  207: 430 */     if (file1.length() != file2.length()) {
/*  208: 432 */       return false;
/*  209:     */     }
/*  210: 435 */     if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
/*  211: 437 */       return true;
/*  212:     */     }
/*  213: 440 */     InputStream input1 = null;
/*  214: 441 */     InputStream input2 = null;
/*  215:     */     try
/*  216:     */     {
/*  217: 443 */       input1 = new FileInputStream(file1);
/*  218: 444 */       input2 = new FileInputStream(file2);
/*  219: 445 */       return IOUtils.contentEquals(input1, input2);
/*  220:     */     }
/*  221:     */     finally
/*  222:     */     {
/*  223: 448 */       IOUtils.closeQuietly(input1);
/*  224: 449 */       IOUtils.closeQuietly(input2);
/*  225:     */     }
/*  226:     */   }
/*  227:     */   
/*  228:     */   public static File toFile(URL url)
/*  229:     */   {
/*  230: 467 */     if ((url == null) || (!url.getProtocol().equals("file"))) {
/*  231: 468 */       return null;
/*  232:     */     }
/*  233: 470 */     String filename = url.getFile().replace('/', File.separatorChar);
/*  234: 471 */     int pos = 0;
/*  235: 472 */     while ((pos = filename.indexOf('%', pos)) >= 0) {
/*  236: 473 */       if (pos + 2 < filename.length())
/*  237:     */       {
/*  238: 474 */         String hexStr = filename.substring(pos + 1, pos + 3);
/*  239: 475 */         char ch = (char)Integer.parseInt(hexStr, 16);
/*  240: 476 */         filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
/*  241:     */       }
/*  242:     */     }
/*  243: 479 */     return new File(filename);
/*  244:     */   }
/*  245:     */   
/*  246:     */   public static File[] toFiles(URL[] urls)
/*  247:     */   {
/*  248: 503 */     if ((urls == null) || (urls.length == 0)) {
/*  249: 504 */       return EMPTY_FILE_ARRAY;
/*  250:     */     }
/*  251: 506 */     File[] files = new File[urls.length];
/*  252: 507 */     for (int i = 0; i < urls.length; i++)
/*  253:     */     {
/*  254: 508 */       URL url = urls[i];
/*  255: 509 */       if (url != null)
/*  256:     */       {
/*  257: 510 */         if (!url.getProtocol().equals("file")) {
/*  258: 511 */           throw new IllegalArgumentException("URL could not be converted to a File: " + url);
/*  259:     */         }
/*  260: 514 */         files[i] = toFile(url);
/*  261:     */       }
/*  262:     */     }
/*  263: 517 */     return files;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public static URL[] toURLs(File[] files)
/*  267:     */     throws IOException
/*  268:     */   {
/*  269: 530 */     URL[] urls = new URL[files.length];
/*  270: 532 */     for (int i = 0; i < urls.length; i++) {
/*  271: 533 */       urls[i] = files[i].toURL();
/*  272:     */     }
/*  273: 536 */     return urls;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public static void copyFileToDirectory(File srcFile, File destDir)
/*  277:     */     throws IOException
/*  278:     */   {
/*  279: 557 */     copyFileToDirectory(srcFile, destDir, true);
/*  280:     */   }
/*  281:     */   
/*  282:     */   public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate)
/*  283:     */     throws IOException
/*  284:     */   {
/*  285: 580 */     if (destDir == null) {
/*  286: 581 */       throw new NullPointerException("Destination must not be null");
/*  287:     */     }
/*  288: 583 */     if ((destDir.exists()) && (!destDir.isDirectory())) {
/*  289: 584 */       throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
/*  290:     */     }
/*  291: 586 */     copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
/*  292:     */   }
/*  293:     */   
/*  294:     */   public static void copyFile(File srcFile, File destFile)
/*  295:     */     throws IOException
/*  296:     */   {
/*  297: 606 */     copyFile(srcFile, destFile, true);
/*  298:     */   }
/*  299:     */   
/*  300:     */   public static void copyFile(File srcFile, File destFile, boolean preserveFileDate)
/*  301:     */     throws IOException
/*  302:     */   {
/*  303: 629 */     if (srcFile == null) {
/*  304: 630 */       throw new NullPointerException("Source must not be null");
/*  305:     */     }
/*  306: 632 */     if (destFile == null) {
/*  307: 633 */       throw new NullPointerException("Destination must not be null");
/*  308:     */     }
/*  309: 635 */     if (!srcFile.exists()) {
/*  310: 636 */       throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
/*  311:     */     }
/*  312: 638 */     if (srcFile.isDirectory()) {
/*  313: 639 */       throw new IOException("Source '" + srcFile + "' exists but is a directory");
/*  314:     */     }
/*  315: 641 */     if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
/*  316: 642 */       throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
/*  317:     */     }
/*  318: 644 */     if ((destFile.getParentFile() != null) && (!destFile.getParentFile().exists()) && 
/*  319: 645 */       (!destFile.getParentFile().mkdirs())) {
/*  320: 646 */       throw new IOException("Destination '" + destFile + "' directory cannot be created");
/*  321:     */     }
/*  322: 649 */     if ((destFile.exists()) && (!destFile.canWrite())) {
/*  323: 650 */       throw new IOException("Destination '" + destFile + "' exists but is read-only");
/*  324:     */     }
/*  325: 652 */     doCopyFile(srcFile, destFile, preserveFileDate);
/*  326:     */   }
/*  327:     */   
/*  328:     */   private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate)
/*  329:     */     throws IOException
/*  330:     */   {
/*  331: 664 */     if ((destFile.exists()) && (destFile.isDirectory())) {
/*  332: 665 */       throw new IOException("Destination '" + destFile + "' exists but is a directory");
/*  333:     */     }
/*  334: 668 */     FileInputStream input = new FileInputStream(srcFile);
/*  335:     */     try
/*  336:     */     {
/*  337: 670 */       FileOutputStream output = new FileOutputStream(destFile);
/*  338:     */       try
/*  339:     */       {
/*  340: 672 */         IOUtils.copy(input, output);
/*  341:     */       }
/*  342:     */       finally {}
/*  343:     */     }
/*  344:     */     finally
/*  345:     */     {
/*  346: 677 */       IOUtils.closeQuietly(input);
/*  347:     */     }
/*  348: 680 */     if (srcFile.length() != destFile.length()) {
/*  349: 681 */       throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
/*  350:     */     }
/*  351: 684 */     if (preserveFileDate) {
/*  352: 685 */       destFile.setLastModified(srcFile.lastModified());
/*  353:     */     }
/*  354:     */   }
/*  355:     */   
/*  356:     */   public static void copyDirectoryToDirectory(File srcDir, File destDir)
/*  357:     */     throws IOException
/*  358:     */   {
/*  359: 709 */     if (srcDir == null) {
/*  360: 710 */       throw new NullPointerException("Source must not be null");
/*  361:     */     }
/*  362: 712 */     if ((srcDir.exists()) && (!srcDir.isDirectory())) {
/*  363: 713 */       throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
/*  364:     */     }
/*  365: 715 */     if (destDir == null) {
/*  366: 716 */       throw new NullPointerException("Destination must not be null");
/*  367:     */     }
/*  368: 718 */     if ((destDir.exists()) && (!destDir.isDirectory())) {
/*  369: 719 */       throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
/*  370:     */     }
/*  371: 721 */     copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
/*  372:     */   }
/*  373:     */   
/*  374:     */   public static void copyDirectory(File srcDir, File destDir)
/*  375:     */     throws IOException
/*  376:     */   {
/*  377: 744 */     copyDirectory(srcDir, destDir, true);
/*  378:     */   }
/*  379:     */   
/*  380:     */   public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate)
/*  381:     */     throws IOException
/*  382:     */   {
/*  383: 769 */     if (srcDir == null) {
/*  384: 770 */       throw new NullPointerException("Source must not be null");
/*  385:     */     }
/*  386: 772 */     if (destDir == null) {
/*  387: 773 */       throw new NullPointerException("Destination must not be null");
/*  388:     */     }
/*  389: 775 */     if (!srcDir.exists()) {
/*  390: 776 */       throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
/*  391:     */     }
/*  392: 778 */     if (!srcDir.isDirectory()) {
/*  393: 779 */       throw new IOException("Source '" + srcDir + "' exists but is not a directory");
/*  394:     */     }
/*  395: 781 */     if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
/*  396: 782 */       throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
/*  397:     */     }
/*  398: 784 */     doCopyDirectory(srcDir, destDir, preserveFileDate);
/*  399:     */   }
/*  400:     */   
/*  401:     */   private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate)
/*  402:     */     throws IOException
/*  403:     */   {
/*  404: 797 */     if (destDir.exists())
/*  405:     */     {
/*  406: 798 */       if (!destDir.isDirectory()) {
/*  407: 799 */         throw new IOException("Destination '" + destDir + "' exists but is not a directory");
/*  408:     */       }
/*  409:     */     }
/*  410:     */     else
/*  411:     */     {
/*  412: 802 */       if (!destDir.mkdirs()) {
/*  413: 803 */         throw new IOException("Destination '" + destDir + "' directory cannot be created");
/*  414:     */       }
/*  415: 805 */       if (preserveFileDate) {
/*  416: 806 */         destDir.setLastModified(srcDir.lastModified());
/*  417:     */       }
/*  418:     */     }
/*  419: 809 */     if (!destDir.canWrite()) {
/*  420: 810 */       throw new IOException("Destination '" + destDir + "' cannot be written to");
/*  421:     */     }
/*  422: 813 */     File[] files = srcDir.listFiles();
/*  423: 814 */     if (files == null) {
/*  424: 815 */       throw new IOException("Failed to list contents of " + srcDir);
/*  425:     */     }
/*  426: 817 */     for (int i = 0; i < files.length; i++)
/*  427:     */     {
/*  428: 818 */       File copiedFile = new File(destDir, files[i].getName());
/*  429: 819 */       if (files[i].isDirectory()) {
/*  430: 820 */         doCopyDirectory(files[i], copiedFile, preserveFileDate);
/*  431:     */       } else {
/*  432: 822 */         doCopyFile(files[i], copiedFile, preserveFileDate);
/*  433:     */       }
/*  434:     */     }
/*  435:     */   }
/*  436:     */   
/*  437:     */   public static void copyURLToFile(URL source, File destination)
/*  438:     */     throws IOException
/*  439:     */   {
/*  440: 844 */     InputStream input = source.openStream();
/*  441:     */     try
/*  442:     */     {
/*  443: 846 */       FileOutputStream output = openOutputStream(destination);
/*  444:     */       try
/*  445:     */       {
/*  446: 848 */         IOUtils.copy(input, output);
/*  447:     */       }
/*  448:     */       finally {}
/*  449:     */     }
/*  450:     */     finally
/*  451:     */     {
/*  452: 853 */       IOUtils.closeQuietly(input);
/*  453:     */     }
/*  454:     */   }
/*  455:     */   
/*  456:     */   public static void deleteDirectory(File directory)
/*  457:     */     throws IOException
/*  458:     */   {
/*  459: 865 */     if (!directory.exists()) {
/*  460: 866 */       return;
/*  461:     */     }
/*  462: 869 */     cleanDirectory(directory);
/*  463: 870 */     if (!directory.delete())
/*  464:     */     {
/*  465: 871 */       String message = "Unable to delete directory " + directory + ".";
/*  466:     */       
/*  467: 873 */       throw new IOException(message);
/*  468:     */     }
/*  469:     */   }
/*  470:     */   
/*  471:     */   public static void cleanDirectory(File directory)
/*  472:     */     throws IOException
/*  473:     */   {
/*  474: 884 */     if (!directory.exists())
/*  475:     */     {
/*  476: 885 */       String message = directory + " does not exist";
/*  477: 886 */       throw new IllegalArgumentException(message);
/*  478:     */     }
/*  479: 889 */     if (!directory.isDirectory())
/*  480:     */     {
/*  481: 890 */       String message = directory + " is not a directory";
/*  482: 891 */       throw new IllegalArgumentException(message);
/*  483:     */     }
/*  484: 894 */     File[] files = directory.listFiles();
/*  485: 895 */     if (files == null) {
/*  486: 896 */       throw new IOException("Failed to list contents of " + directory);
/*  487:     */     }
/*  488: 899 */     IOException exception = null;
/*  489: 900 */     for (int i = 0; i < files.length; i++)
/*  490:     */     {
/*  491: 901 */       File file = files[i];
/*  492:     */       try
/*  493:     */       {
/*  494: 903 */         forceDelete(file);
/*  495:     */       }
/*  496:     */       catch (IOException ioe)
/*  497:     */       {
/*  498: 905 */         exception = ioe;
/*  499:     */       }
/*  500:     */     }
/*  501: 909 */     if (null != exception) {
/*  502: 910 */       throw exception;
/*  503:     */     }
/*  504:     */   }
/*  505:     */   
/*  506:     */   public static boolean waitFor(File file, int seconds)
/*  507:     */   {
/*  508: 927 */     int timeout = 0;
/*  509: 928 */     int tick = 0;
/*  510:     */     for (;;)
/*  511:     */     {
/*  512: 929 */       if (!file.exists())
/*  513:     */       {
/*  514: 930 */         if (tick++ >= 10)
/*  515:     */         {
/*  516: 931 */           tick = 0;
/*  517: 932 */           if (timeout++ > seconds) {
/*  518: 933 */             return false;
/*  519:     */           }
/*  520:     */         }
/*  521:     */         try
/*  522:     */         {
/*  523: 937 */           Thread.sleep(100L);
/*  524:     */         }
/*  525:     */         catch (InterruptedException ignore) {}catch (Exception ex) {}
/*  526:     */       }
/*  527:     */     }
/*  528: 944 */     return true;
/*  529:     */   }
/*  530:     */   
/*  531:     */   public static String readFileToString(File file, String encoding)
/*  532:     */     throws IOException
/*  533:     */   {
/*  534: 959 */     InputStream in = null;
/*  535:     */     try
/*  536:     */     {
/*  537: 961 */       in = openInputStream(file);
/*  538: 962 */       return IOUtils.toString(in, encoding);
/*  539:     */     }
/*  540:     */     finally
/*  541:     */     {
/*  542: 964 */       IOUtils.closeQuietly(in);
/*  543:     */     }
/*  544:     */   }
/*  545:     */   
/*  546:     */   public static String readFileToString(File file)
/*  547:     */     throws IOException
/*  548:     */   {
/*  549: 979 */     return readFileToString(file, null);
/*  550:     */   }
/*  551:     */   
/*  552:     */   public static byte[] readFileToByteArray(File file)
/*  553:     */     throws IOException
/*  554:     */   {
/*  555: 992 */     InputStream in = null;
/*  556:     */     try
/*  557:     */     {
/*  558: 994 */       in = openInputStream(file);
/*  559: 995 */       return IOUtils.toByteArray(in);
/*  560:     */     }
/*  561:     */     finally
/*  562:     */     {
/*  563: 997 */       IOUtils.closeQuietly(in);
/*  564:     */     }
/*  565:     */   }
/*  566:     */   
/*  567:     */   public static List readLines(File file, String encoding)
/*  568:     */     throws IOException
/*  569:     */   {
/*  570:1013 */     InputStream in = null;
/*  571:     */     try
/*  572:     */     {
/*  573:1015 */       in = openInputStream(file);
/*  574:1016 */       return IOUtils.readLines(in, encoding);
/*  575:     */     }
/*  576:     */     finally
/*  577:     */     {
/*  578:1018 */       IOUtils.closeQuietly(in);
/*  579:     */     }
/*  580:     */   }
/*  581:     */   
/*  582:     */   public static List readLines(File file)
/*  583:     */     throws IOException
/*  584:     */   {
/*  585:1032 */     return readLines(file, null);
/*  586:     */   }
/*  587:     */   
/*  588:     */   public static LineIterator lineIterator(File file, String encoding)
/*  589:     */     throws IOException
/*  590:     */   {
/*  591:1067 */     InputStream in = null;
/*  592:     */     try
/*  593:     */     {
/*  594:1069 */       in = openInputStream(file);
/*  595:1070 */       return IOUtils.lineIterator(in, encoding);
/*  596:     */     }
/*  597:     */     catch (IOException ex)
/*  598:     */     {
/*  599:1072 */       IOUtils.closeQuietly(in);
/*  600:1073 */       throw ex;
/*  601:     */     }
/*  602:     */     catch (RuntimeException ex)
/*  603:     */     {
/*  604:1075 */       IOUtils.closeQuietly(in);
/*  605:1076 */       throw ex;
/*  606:     */     }
/*  607:     */   }
/*  608:     */   
/*  609:     */   public static LineIterator lineIterator(File file)
/*  610:     */     throws IOException
/*  611:     */   {
/*  612:1090 */     return lineIterator(file, null);
/*  613:     */   }
/*  614:     */   
/*  615:     */   public static void writeStringToFile(File file, String data, String encoding)
/*  616:     */     throws IOException
/*  617:     */   {
/*  618:1107 */     OutputStream out = null;
/*  619:     */     try
/*  620:     */     {
/*  621:1109 */       out = openOutputStream(file);
/*  622:1110 */       IOUtils.write(data, out, encoding);
/*  623:     */     }
/*  624:     */     finally
/*  625:     */     {
/*  626:1112 */       IOUtils.closeQuietly(out);
/*  627:     */     }
/*  628:     */   }
/*  629:     */   
/*  630:     */   public static void writeStringToFile(File file, String data)
/*  631:     */     throws IOException
/*  632:     */   {
/*  633:1124 */     writeStringToFile(file, data, null);
/*  634:     */   }
/*  635:     */   
/*  636:     */   public static void writeByteArrayToFile(File file, byte[] data)
/*  637:     */     throws IOException
/*  638:     */   {
/*  639:1139 */     OutputStream out = null;
/*  640:     */     try
/*  641:     */     {
/*  642:1141 */       out = openOutputStream(file);
/*  643:1142 */       out.write(data);
/*  644:     */     }
/*  645:     */     finally
/*  646:     */     {
/*  647:1144 */       IOUtils.closeQuietly(out);
/*  648:     */     }
/*  649:     */   }
/*  650:     */   
/*  651:     */   public static void writeLines(File file, String encoding, Collection lines)
/*  652:     */     throws IOException
/*  653:     */   {
/*  654:1164 */     writeLines(file, encoding, lines, null);
/*  655:     */   }
/*  656:     */   
/*  657:     */   public static void writeLines(File file, Collection lines)
/*  658:     */     throws IOException
/*  659:     */   {
/*  660:1178 */     writeLines(file, null, lines, null);
/*  661:     */   }
/*  662:     */   
/*  663:     */   public static void writeLines(File file, String encoding, Collection lines, String lineEnding)
/*  664:     */     throws IOException
/*  665:     */   {
/*  666:1198 */     OutputStream out = null;
/*  667:     */     try
/*  668:     */     {
/*  669:1200 */       out = openOutputStream(file);
/*  670:1201 */       IOUtils.writeLines(lines, lineEnding, out, encoding);
/*  671:     */     }
/*  672:     */     finally
/*  673:     */     {
/*  674:1203 */       IOUtils.closeQuietly(out);
/*  675:     */     }
/*  676:     */   }
/*  677:     */   
/*  678:     */   public static void writeLines(File file, Collection lines, String lineEnding)
/*  679:     */     throws IOException
/*  680:     */   {
/*  681:1219 */     writeLines(file, null, lines, lineEnding);
/*  682:     */   }
/*  683:     */   
/*  684:     */   public static void forceDelete(File file)
/*  685:     */     throws IOException
/*  686:     */   {
/*  687:1238 */     if (file.isDirectory())
/*  688:     */     {
/*  689:1239 */       deleteDirectory(file);
/*  690:     */     }
/*  691:     */     else
/*  692:     */     {
/*  693:1241 */       if (!file.exists()) {
/*  694:1242 */         throw new FileNotFoundException("File does not exist: " + file);
/*  695:     */       }
/*  696:1244 */       if (!file.delete())
/*  697:     */       {
/*  698:1245 */         String message = "Unable to delete file: " + file;
/*  699:     */         
/*  700:1247 */         throw new IOException(message);
/*  701:     */       }
/*  702:     */     }
/*  703:     */   }
/*  704:     */   
/*  705:     */   public static void forceDeleteOnExit(File file)
/*  706:     */     throws IOException
/*  707:     */   {
/*  708:1261 */     if (file.isDirectory()) {
/*  709:1262 */       deleteDirectoryOnExit(file);
/*  710:     */     } else {
/*  711:1264 */       file.deleteOnExit();
/*  712:     */     }
/*  713:     */   }
/*  714:     */   
/*  715:     */   private static void deleteDirectoryOnExit(File directory)
/*  716:     */     throws IOException
/*  717:     */   {
/*  718:1276 */     if (!directory.exists()) {
/*  719:1277 */       return;
/*  720:     */     }
/*  721:1280 */     cleanDirectoryOnExit(directory);
/*  722:1281 */     directory.deleteOnExit();
/*  723:     */   }
/*  724:     */   
/*  725:     */   private static void cleanDirectoryOnExit(File directory)
/*  726:     */     throws IOException
/*  727:     */   {
/*  728:1292 */     if (!directory.exists())
/*  729:     */     {
/*  730:1293 */       String message = directory + " does not exist";
/*  731:1294 */       throw new IllegalArgumentException(message);
/*  732:     */     }
/*  733:1297 */     if (!directory.isDirectory())
/*  734:     */     {
/*  735:1298 */       String message = directory + " is not a directory";
/*  736:1299 */       throw new IllegalArgumentException(message);
/*  737:     */     }
/*  738:1302 */     File[] files = directory.listFiles();
/*  739:1303 */     if (files == null) {
/*  740:1304 */       throw new IOException("Failed to list contents of " + directory);
/*  741:     */     }
/*  742:1307 */     IOException exception = null;
/*  743:1308 */     for (int i = 0; i < files.length; i++)
/*  744:     */     {
/*  745:1309 */       File file = files[i];
/*  746:     */       try
/*  747:     */       {
/*  748:1311 */         forceDeleteOnExit(file);
/*  749:     */       }
/*  750:     */       catch (IOException ioe)
/*  751:     */       {
/*  752:1313 */         exception = ioe;
/*  753:     */       }
/*  754:     */     }
/*  755:1317 */     if (null != exception) {
/*  756:1318 */       throw exception;
/*  757:     */     }
/*  758:     */   }
/*  759:     */   
/*  760:     */   public static void forceMkdir(File directory)
/*  761:     */     throws IOException
/*  762:     */   {
/*  763:1332 */     if (directory.exists())
/*  764:     */     {
/*  765:1333 */       if (directory.isFile())
/*  766:     */       {
/*  767:1334 */         String message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
/*  768:     */         
/*  769:     */ 
/*  770:     */ 
/*  771:     */ 
/*  772:1339 */         throw new IOException(message);
/*  773:     */       }
/*  774:     */     }
/*  775:1342 */     else if (!directory.mkdirs())
/*  776:     */     {
/*  777:1343 */       String message = "Unable to create directory " + directory;
/*  778:     */       
/*  779:1345 */       throw new IOException(message);
/*  780:     */     }
/*  781:     */   }
/*  782:     */   
/*  783:     */   public static long sizeOfDirectory(File directory)
/*  784:     */   {
/*  785:1359 */     if (!directory.exists())
/*  786:     */     {
/*  787:1360 */       String message = directory + " does not exist";
/*  788:1361 */       throw new IllegalArgumentException(message);
/*  789:     */     }
/*  790:1364 */     if (!directory.isDirectory())
/*  791:     */     {
/*  792:1365 */       String message = directory + " is not a directory";
/*  793:1366 */       throw new IllegalArgumentException(message);
/*  794:     */     }
/*  795:1369 */     long size = 0L;
/*  796:     */     
/*  797:1371 */     File[] files = directory.listFiles();
/*  798:1372 */     if (files == null) {
/*  799:1373 */       return 0L;
/*  800:     */     }
/*  801:1375 */     for (int i = 0; i < files.length; i++)
/*  802:     */     {
/*  803:1376 */       File file = files[i];
/*  804:1378 */       if (file.isDirectory()) {
/*  805:1379 */         size += sizeOfDirectory(file);
/*  806:     */       } else {
/*  807:1381 */         size += file.length();
/*  808:     */       }
/*  809:     */     }
/*  810:1385 */     return size;
/*  811:     */   }
/*  812:     */   
/*  813:     */   public static boolean isFileNewer(File file, File reference)
/*  814:     */   {
/*  815:1403 */     if (reference == null) {
/*  816:1404 */       throw new IllegalArgumentException("No specified reference file");
/*  817:     */     }
/*  818:1406 */     if (!reference.exists()) {
/*  819:1407 */       throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
/*  820:     */     }
/*  821:1410 */     return isFileNewer(file, reference.lastModified());
/*  822:     */   }
/*  823:     */   
/*  824:     */   public static boolean isFileNewer(File file, Date date)
/*  825:     */   {
/*  826:1426 */     if (date == null) {
/*  827:1427 */       throw new IllegalArgumentException("No specified date");
/*  828:     */     }
/*  829:1429 */     return isFileNewer(file, date.getTime());
/*  830:     */   }
/*  831:     */   
/*  832:     */   public static boolean isFileNewer(File file, long timeMillis)
/*  833:     */   {
/*  834:1445 */     if (file == null) {
/*  835:1446 */       throw new IllegalArgumentException("No specified file");
/*  836:     */     }
/*  837:1448 */     if (!file.exists()) {
/*  838:1449 */       return false;
/*  839:     */     }
/*  840:1451 */     return file.lastModified() > timeMillis;
/*  841:     */   }
/*  842:     */   
/*  843:     */   public static boolean isFileOlder(File file, File reference)
/*  844:     */   {
/*  845:1470 */     if (reference == null) {
/*  846:1471 */       throw new IllegalArgumentException("No specified reference file");
/*  847:     */     }
/*  848:1473 */     if (!reference.exists()) {
/*  849:1474 */       throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
/*  850:     */     }
/*  851:1477 */     return isFileOlder(file, reference.lastModified());
/*  852:     */   }
/*  853:     */   
/*  854:     */   public static boolean isFileOlder(File file, Date date)
/*  855:     */   {
/*  856:1493 */     if (date == null) {
/*  857:1494 */       throw new IllegalArgumentException("No specified date");
/*  858:     */     }
/*  859:1496 */     return isFileOlder(file, date.getTime());
/*  860:     */   }
/*  861:     */   
/*  862:     */   public static boolean isFileOlder(File file, long timeMillis)
/*  863:     */   {
/*  864:1512 */     if (file == null) {
/*  865:1513 */       throw new IllegalArgumentException("No specified file");
/*  866:     */     }
/*  867:1515 */     if (!file.exists()) {
/*  868:1516 */       return false;
/*  869:     */     }
/*  870:1518 */     return file.lastModified() < timeMillis;
/*  871:     */   }
/*  872:     */   
/*  873:     */   public static long checksumCRC32(File file)
/*  874:     */     throws IOException
/*  875:     */   {
/*  876:1534 */     CRC32 crc = new CRC32();
/*  877:1535 */     checksum(file, crc);
/*  878:1536 */     return crc.getValue();
/*  879:     */   }
/*  880:     */   
/*  881:     */   public static Checksum checksum(File file, Checksum checksum)
/*  882:     */     throws IOException
/*  883:     */   {
/*  884:1557 */     if (file.isDirectory()) {
/*  885:1558 */       throw new IllegalArgumentException("Checksums can't be computed on directories");
/*  886:     */     }
/*  887:1560 */     InputStream in = null;
/*  888:     */     try
/*  889:     */     {
/*  890:1562 */       in = new CheckedInputStream(new FileInputStream(file), checksum);
/*  891:1563 */       IOUtils.copy(in, new NullOutputStream());
/*  892:     */     }
/*  893:     */     finally
/*  894:     */     {
/*  895:1565 */       IOUtils.closeQuietly(in);
/*  896:     */     }
/*  897:1567 */     return checksum;
/*  898:     */   }
/*  899:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.FileUtils
 * JD-Core Version:    0.7.0.1
 */