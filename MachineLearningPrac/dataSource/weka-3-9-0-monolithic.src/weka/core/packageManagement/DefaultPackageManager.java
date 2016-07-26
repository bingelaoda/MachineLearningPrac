/*    1:     */ package weka.core.packageManagement;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedOutputStream;
/*    5:     */ import java.io.BufferedReader;
/*    6:     */ import java.io.ByteArrayOutputStream;
/*    7:     */ import java.io.File;
/*    8:     */ import java.io.FileInputStream;
/*    9:     */ import java.io.FileOutputStream;
/*   10:     */ import java.io.IOException;
/*   11:     */ import java.io.InputStream;
/*   12:     */ import java.io.InputStreamReader;
/*   13:     */ import java.io.ObjectInputStream;
/*   14:     */ import java.io.ObjectOutputStream;
/*   15:     */ import java.io.OutputStream;
/*   16:     */ import java.io.PrintStream;
/*   17:     */ import java.net.URL;
/*   18:     */ import java.net.URLConnection;
/*   19:     */ import java.util.ArrayList;
/*   20:     */ import java.util.Enumeration;
/*   21:     */ import java.util.HashMap;
/*   22:     */ import java.util.Iterator;
/*   23:     */ import java.util.List;
/*   24:     */ import java.util.Map;
/*   25:     */ import java.util.Properties;
/*   26:     */ import java.util.Set;
/*   27:     */ import java.util.zip.ZipEntry;
/*   28:     */ import java.util.zip.ZipFile;
/*   29:     */ import java.util.zip.ZipOutputStream;
/*   30:     */ import weka.core.Defaults;
/*   31:     */ import weka.core.Environment;
/*   32:     */ import weka.core.Settings;
/*   33:     */ import weka.core.Settings.SettingKey;
/*   34:     */ 
/*   35:     */ public class DefaultPackageManager
/*   36:     */   extends PackageManager
/*   37:     */ {
/*   38:     */   protected static final int BUFF_SIZE = 100000;
/*   39:  73 */   protected static final byte[] m_buffer = new byte[100000];
/*   40:     */   protected static final String INSTALLED_PACKAGE_CACHE_FILE = "installedPackageCache.ser";
/*   41:     */   protected static List<Package> s_installedPackageList;
/*   42:  81 */   protected int m_timeout = 5000;
/*   43:     */   protected static final String TIMEOUT_PROPERTY = "weka.packageManager.timeout";
/*   44:     */   
/*   45:     */   public DefaultPackageManager()
/*   46:     */   {
/*   47:  92 */     String timeout = System.getProperty("weka.packageManager.timeout");
/*   48:  93 */     if ((timeout != null) && (timeout.length() > 0)) {
/*   49:     */       try
/*   50:     */       {
/*   51:  95 */         this.m_timeout = Integer.parseInt(timeout);
/*   52:     */       }
/*   53:     */       catch (NumberFormatException e) {}
/*   54:     */     }
/*   55:     */   }
/*   56:     */   
/*   57:     */   public Defaults getDefaultSettings()
/*   58:     */   {
/*   59: 110 */     return new DefaultPMDefaults();
/*   60:     */   }
/*   61:     */   
/*   62:     */   public void applySettings(Settings settings)
/*   63:     */   {
/*   64: 121 */     this.m_timeout = ((Integer)settings.getSetting(settings.getID(), DefaultPMDefaults.SOCKET_TIMEOUT_KEY, Integer.valueOf(5000), Environment.getSystemWide())).intValue();
/*   65:     */   }
/*   66:     */   
/*   67:     */   protected static final class DefaultPMDefaults
/*   68:     */     extends Defaults
/*   69:     */   {
/*   70:     */     protected static final String APP_ID = "defaultpackagemanager";
/*   71: 133 */     protected static final Settings.SettingKey SOCKET_TIMEOUT_KEY = new Settings.SettingKey("defaultpackagemanager.timeout", "Timeout (in ms) for socket comms", "");
/*   72:     */     protected static final int SOCKET_TIMEOUT = 5000;
/*   73:     */     private static final long serialVersionUID = -1428588514991146709L;
/*   74:     */     
/*   75:     */     public DefaultPMDefaults()
/*   76:     */     {
/*   77: 141 */       super();
/*   78: 142 */       this.m_defaults.put(SOCKET_TIMEOUT_KEY, Integer.valueOf(5000));
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   protected File downloadArchive(URL packageURL, String fileExtension, PrintStream... progress)
/*   83:     */     throws Exception
/*   84:     */   {
/*   85: 184 */     String packageArchiveName = packageURL.toString();
/*   86:     */     
/*   87: 186 */     packageArchiveName = packageArchiveName.substring(0, packageArchiveName.lastIndexOf("." + fileExtension) + 3);
/*   88:     */     
/*   89:     */ 
/*   90:     */ 
/*   91: 190 */     packageArchiveName = packageArchiveName.substring(0, packageArchiveName.lastIndexOf('.'));
/*   92:     */     
/*   93:     */ 
/*   94: 193 */     packageArchiveName = packageArchiveName.substring(packageArchiveName.lastIndexOf('/'), packageArchiveName.length());
/*   95:     */     
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99: 198 */     File tmpDownload = File.createTempFile(packageArchiveName, "." + fileExtension);
/*  100: 201 */     for (PrintStream progres : progress)
/*  101:     */     {
/*  102: 202 */       progres.println(packageURL.toString());
/*  103: 203 */       progres.println("[DefaultPackageManager] Tmp file: " + tmpDownload.toString());
/*  104:     */     }
/*  105: 207 */     URLConnection conn = getConnection(packageURL);
/*  106:     */     
/*  107: 209 */     BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());
/*  108:     */     
/*  109: 211 */     BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(tmpDownload));
/*  110:     */     
/*  111:     */ 
/*  112:     */ 
/*  113: 215 */     int totalBytesRead = 0;
/*  114:     */     for (;;)
/*  115:     */     {
/*  116: 217 */       synchronized (m_buffer)
/*  117:     */       {
/*  118: 218 */         int amountRead = bi.read(m_buffer);
/*  119: 219 */         if (amountRead == -1)
/*  120:     */         {
/*  121: 220 */           PrintStream[] arr$ = progress;int len$ = arr$.length;int i$ = 0;
/*  122: 220 */           if (i$ < len$)
/*  123:     */           {
/*  124: 220 */             PrintStream progres = arr$[i$];
/*  125: 221 */             progres.println("[DefaultPackageManager] downloaded " + totalBytesRead / 1000 + " KB");i$++; continue;
/*  126:     */           }
/*  127: 224 */           break;
/*  128:     */         }
/*  129: 226 */         bo.write(m_buffer, 0, amountRead);
/*  130: 227 */         totalBytesRead += amountRead;
/*  131: 228 */         PrintStream[] arr$ = progress;int len$ = arr$.length;int i$ = 0;
/*  132: 228 */         if (i$ < len$)
/*  133:     */         {
/*  134: 228 */           PrintStream progres = arr$[i$];
/*  135: 229 */           progres.println("%%[DefaultPackageManager] downloaded " + totalBytesRead / 1000 + " KB");i$++;
/*  136:     */         }
/*  137:     */       }
/*  138:     */     }
/*  139: 235 */     bi.close();
/*  140: 236 */     bo.close();
/*  141:     */     
/*  142: 238 */     return tmpDownload;
/*  143:     */   }
/*  144:     */   
/*  145:     */   public Package getURLPackageInfo(URL packageURL)
/*  146:     */     throws Exception
/*  147:     */   {
/*  148: 250 */     File downloaded = downloadArchive(packageURL, "zip", new PrintStream[0]);
/*  149:     */     
/*  150:     */ 
/*  151: 253 */     return getPackageArchiveInfo(downloaded);
/*  152:     */   }
/*  153:     */   
/*  154:     */   public Package getRepositoryPackageInfo(String packageName)
/*  155:     */     throws Exception
/*  156:     */   {
/*  157: 268 */     return getRepositoryPackageInfo(packageName, "Latest");
/*  158:     */   }
/*  159:     */   
/*  160:     */   public List<Object> getRepositoryPackageVersions(String packageName)
/*  161:     */     throws Exception
/*  162:     */   {
/*  163: 283 */     if (getPackageRepositoryURL() == null) {
/*  164: 284 */       throw new Exception("[DefaultPackageManager] No package repository set!!");
/*  165:     */     }
/*  166: 287 */     String versionsS = this.m_packageRepository.toString() + "/" + packageName + "/" + "versions.txt";
/*  167:     */     
/*  168:     */ 
/*  169: 290 */     URL packageURL = new URL(versionsS);
/*  170: 291 */     URLConnection conn = getConnection(packageURL);
/*  171:     */     
/*  172: 293 */     BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  173:     */     
/*  174:     */ 
/*  175: 296 */     ArrayList<Object> versions = new ArrayList();
/*  176:     */     String versionNumber;
/*  177: 298 */     while ((versionNumber = bi.readLine()) != null) {
/*  178: 299 */       versions.add(versionNumber.trim());
/*  179:     */     }
/*  180: 302 */     bi.close();
/*  181: 303 */     return versions;
/*  182:     */   }
/*  183:     */   
/*  184:     */   public Package getRepositoryPackageInfo(String packageName, Object version)
/*  185:     */     throws Exception
/*  186:     */   {
/*  187: 318 */     if (getPackageRepositoryURL() == null) {
/*  188: 319 */       throw new Exception("[DefaultPackageManager] No package repository set!!");
/*  189:     */     }
/*  190: 322 */     if (version == null) {
/*  191: 323 */       version = "Latest";
/*  192:     */     }
/*  193: 326 */     String packageS = this.m_packageRepository.toString() + "/" + packageName + "/" + version.toString() + ".props";
/*  194:     */     
/*  195:     */ 
/*  196: 329 */     URL packageURL = new URL(packageS);
/*  197: 330 */     URLConnection conn = getConnection(packageURL);
/*  198:     */     
/*  199: 332 */     BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());
/*  200: 333 */     Properties packageProperties = new Properties();
/*  201: 334 */     packageProperties.load(bi);
/*  202: 335 */     bi.close();
/*  203:     */     
/*  204: 337 */     return new DefaultPackage(this.m_packageHome, this, packageProperties);
/*  205:     */   }
/*  206:     */   
/*  207:     */   private Package getPackageArchiveInfo(File packageArchive)
/*  208:     */     throws Exception
/*  209:     */   {
/*  210: 341 */     return getPackageArchiveInfo(packageArchive.getAbsolutePath());
/*  211:     */   }
/*  212:     */   
/*  213:     */   public Package getPackageArchiveInfo(String packageArchivePath)
/*  214:     */     throws Exception
/*  215:     */   {
/*  216: 354 */     ZipFile zip = new ZipFile(new File(packageArchivePath));
/*  217: 356 */     for (Enumeration e = zip.entries(); e.hasMoreElements();)
/*  218:     */     {
/*  219: 357 */       ZipEntry entry = (ZipEntry)e.nextElement();
/*  220: 358 */       if (entry.getName().endsWith("Description.props"))
/*  221:     */       {
/*  222: 359 */         InputStream is = zip.getInputStream(entry);
/*  223: 360 */         Properties packageProperties = new Properties();
/*  224: 361 */         packageProperties.load(new BufferedInputStream(is));
/*  225: 362 */         is.close();
/*  226:     */         
/*  227: 364 */         DefaultPackage pkg = new DefaultPackage(this.m_packageHome, this, packageProperties);
/*  228:     */         
/*  229:     */ 
/*  230: 367 */         return pkg;
/*  231:     */       }
/*  232:     */     }
/*  233: 371 */     throw new Exception("Unable to find Description file in package archive!");
/*  234:     */   }
/*  235:     */   
/*  236:     */   public Package getInstalledPackageInfo(String packageName)
/*  237:     */     throws Exception
/*  238:     */   {
/*  239: 385 */     File packageDescription = new File(this.m_packageHome.getAbsoluteFile() + File.separator + packageName + File.separator + "Description.props");
/*  240: 389 */     if (!packageDescription.exists()) {
/*  241: 390 */       return null;
/*  242:     */     }
/*  243: 393 */     FileInputStream fis = new FileInputStream(packageDescription);
/*  244:     */     
/*  245: 395 */     Properties packageProperties = new Properties();
/*  246: 396 */     packageProperties.load(fis);
/*  247: 397 */     fis.close();
/*  248:     */     
/*  249: 399 */     DefaultPackage pkg = new DefaultPackage(this.m_packageHome, this, packageProperties);
/*  250:     */     
/*  251: 401 */     return pkg;
/*  252:     */   }
/*  253:     */   
/*  254:     */   protected boolean establishPackageHome()
/*  255:     */   {
/*  256: 410 */     if (this.m_packageHome == null) {
/*  257: 411 */       return false;
/*  258:     */     }
/*  259: 414 */     if (!this.m_packageHome.exists()) {
/*  260: 416 */       if (!this.m_packageHome.mkdir())
/*  261:     */       {
/*  262: 417 */         System.err.println("Unable to create packages directory (" + this.m_packageHome.getAbsolutePath() + ")");
/*  263:     */         
/*  264: 419 */         return false;
/*  265:     */       }
/*  266:     */     }
/*  267: 422 */     return true;
/*  268:     */   }
/*  269:     */   
/*  270:     */   public static void deleteDir(File dir, PrintStream... progress)
/*  271:     */     throws Exception
/*  272:     */   {
/*  273: 429 */     File[] contents = dir.listFiles();
/*  274: 431 */     if (contents.length != 0) {
/*  275: 433 */       for (File f : contents) {
/*  276: 434 */         if (f.isDirectory())
/*  277:     */         {
/*  278: 435 */           deleteDir(f, new PrintStream[0]);
/*  279:     */         }
/*  280:     */         else
/*  281:     */         {
/*  282: 437 */           for (PrintStream progres : progress) {
/*  283: 438 */             progres.println("[DefaultPackageManager] removing: " + f.toString());
/*  284:     */           }
/*  285: 441 */           if (!f.delete())
/*  286:     */           {
/*  287: 442 */             System.err.println("[DefaultPackageManager] can't delete file " + f.toString());
/*  288:     */             
/*  289: 444 */             f.deleteOnExit();
/*  290:     */           }
/*  291:     */         }
/*  292:     */       }
/*  293:     */     }
/*  294: 451 */     if (!dir.delete())
/*  295:     */     {
/*  296: 452 */       System.err.println("[DefaultPackageManager] can't delete directory " + dir.toString());
/*  297:     */       
/*  298: 454 */       dir.deleteOnExit();
/*  299:     */     }
/*  300: 456 */     for (PrintStream progres : progress) {
/*  301: 457 */       progres.println("[DefaultPackageManager] removing: " + dir.toString());
/*  302:     */     }
/*  303:     */   }
/*  304:     */   
/*  305:     */   public void uninstallPackage(String packageName, PrintStream... progress)
/*  306:     */     throws Exception
/*  307:     */   {
/*  308: 473 */     File packageToDel = new File(this.m_packageHome.toString() + File.separator + packageName);
/*  309: 476 */     if (!packageToDel.exists()) {
/*  310: 477 */       throw new Exception("[DefaultPackageManager] Can't remove " + packageName + " because it doesn't seem to be installed!");
/*  311:     */     }
/*  312: 481 */     deleteDir(packageToDel, progress);
/*  313:     */     
/*  314:     */ 
/*  315: 484 */     s_installedPackageList = null;
/*  316: 485 */     deleteInstalledPackageCacheFile();
/*  317:     */   }
/*  318:     */   
/*  319:     */   public String installPackageFromArchive(String packageArchivePath, PrintStream... progress)
/*  320:     */     throws Exception
/*  321:     */   {
/*  322: 500 */     Properties packageProps = (Properties)getPackageArchiveInfo(packageArchivePath).getPackageMetaData();
/*  323:     */     
/*  324:     */ 
/*  325: 503 */     String packageName = packageProps.getProperty("PackageName");
/*  326: 504 */     String additionalLibs = packageProps.getProperty("AdditionalLibs");
/*  327: 505 */     String[] additionalLibURLs = null;
/*  328: 507 */     if ((additionalLibs != null) && (additionalLibs.length() > 0)) {
/*  329: 508 */       additionalLibURLs = additionalLibs.split(",");
/*  330:     */     }
/*  331: 510 */     if (packageName == null) {
/*  332: 511 */       throw new Exception("Unable to find the name of the package in the Description file for " + packageArchivePath);
/*  333:     */     }
/*  334: 515 */     installPackage(packageArchivePath, packageName, progress);
/*  335: 517 */     if (additionalLibURLs != null) {
/*  336: 518 */       installAdditionalLibs(packageName, additionalLibURLs, progress);
/*  337:     */     }
/*  338: 521 */     return packageName;
/*  339:     */   }
/*  340:     */   
/*  341:     */   protected void installAdditionalLibs(String packageName, String[] additionalLibURLs, PrintStream... progress)
/*  342:     */     throws Exception
/*  343:     */   {
/*  344: 537 */     if (!establishPackageHome()) {
/*  345: 538 */       throw new Exception("Unable to install additional libraries because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  346:     */     }
/*  347: 543 */     for (String libU : additionalLibURLs)
/*  348:     */     {
/*  349: 544 */       libU = libU.trim();
/*  350: 546 */       if (libU.trim().length() > 0)
/*  351:     */       {
/*  352: 547 */         URL libURL = new URL(libU.trim());
/*  353:     */         
/*  354: 549 */         File libPath = downloadArchive(libURL, "jar", progress);
/*  355:     */         
/*  356: 551 */         String destName = libU.substring(0, libU.lastIndexOf(".jar") + 3);
/*  357: 552 */         destName = destName.substring(0, destName.lastIndexOf('.'));
/*  358: 553 */         destName = destName.substring(destName.lastIndexOf('/'), destName.length());
/*  359:     */         
/*  360: 555 */         destName = destName + ".jar";
/*  361:     */         
/*  362: 557 */         File destDir = new File(this.m_packageHome, packageName + File.separator + "lib");
/*  363: 559 */         if (!destDir.mkdir()) {}
/*  364: 562 */         File destPath = new File(destDir, destName);
/*  365:     */         
/*  366: 564 */         InputStream input = new BufferedInputStream(new FileInputStream(libPath));
/*  367:     */         
/*  368: 566 */         OutputStream output = new BufferedOutputStream(new FileOutputStream(destPath));
/*  369:     */         
/*  370: 568 */         copyStreams(input, output);
/*  371:     */         
/*  372: 570 */         input.close();
/*  373: 571 */         output.flush();
/*  374: 572 */         output.close();
/*  375:     */       }
/*  376:     */     }
/*  377:     */   }
/*  378:     */   
/*  379:     */   public void installPackages(List<Package> toInstall, PrintStream... progress)
/*  380:     */     throws Exception
/*  381:     */   {
/*  382: 588 */     File[] archivePaths = new File[toInstall.size()];
/*  383: 590 */     for (int i = 0; i < toInstall.size(); i++)
/*  384:     */     {
/*  385: 591 */       Package toDownload = (Package)toInstall.get(i);
/*  386: 592 */       if (toDownload.isInstalled()) {
/*  387: 593 */         for (PrintStream p : progress)
/*  388:     */         {
/*  389: 594 */           p.println("[DefaultPackageManager] cleanining installed package: " + toDownload.getName());
/*  390:     */           
/*  391: 596 */           uninstallPackage(toDownload.getName(), progress);
/*  392:     */         }
/*  393:     */       }
/*  394: 599 */       archivePaths[i] = downloadArchive(toDownload.getPackageURL(), "zip", progress);
/*  395:     */     }
/*  396: 604 */     for (File archivePath : archivePaths) {
/*  397: 605 */       installPackageFromArchive(archivePath.getAbsolutePath(), progress);
/*  398:     */     }
/*  399:     */   }
/*  400:     */   
/*  401:     */   protected static boolean checkDependencies(PackageConstraint toCheck, Map<String, Dependency> lookup, Map<String, List<Dependency>> conflicts)
/*  402:     */     throws Exception
/*  403:     */   {
/*  404: 625 */     boolean ok = true;
/*  405:     */     
/*  406:     */ 
/*  407: 628 */     List<Dependency> deps = toCheck.getPackage().getDependencies();
/*  408: 630 */     for (Dependency p : deps)
/*  409:     */     {
/*  410: 631 */       String depName = p.getTarget().getPackage().getPackageMetaDataElement("PackageName").toString();
/*  411: 634 */       if (!lookup.containsKey(depName))
/*  412:     */       {
/*  413: 636 */         lookup.put(depName, p);
/*  414:     */         
/*  415:     */ 
/*  416: 639 */         ok = checkDependencies(p.getTarget(), lookup, conflicts);
/*  417:     */       }
/*  418:     */       else
/*  419:     */       {
/*  420: 643 */         Dependency checkAgainst = (Dependency)lookup.get(depName);
/*  421: 644 */         PackageConstraint result = checkAgainst.getTarget().checkConstraint(p.getTarget());
/*  422: 646 */         if (result != null)
/*  423:     */         {
/*  424: 647 */           checkAgainst.setTarget(result);
/*  425: 648 */           lookup.put(depName, checkAgainst);
/*  426:     */         }
/*  427:     */         else
/*  428:     */         {
/*  429: 651 */           List<Dependency> conflictList = (List)conflicts.get(depName);
/*  430: 652 */           conflictList.add(p);
/*  431: 653 */           ok = false;
/*  432:     */         }
/*  433:     */       }
/*  434:     */     }
/*  435: 658 */     return ok;
/*  436:     */   }
/*  437:     */   
/*  438:     */   public List<Dependency> getAllDependenciesForPackage(Package target, Map<String, List<Dependency>> conflicts)
/*  439:     */     throws Exception
/*  440:     */   {
/*  441: 681 */     List<Dependency> initialList = target.getDependencies();
/*  442:     */     
/*  443:     */ 
/*  444: 684 */     Map<String, Dependency> lookup = new HashMap();
/*  445: 686 */     for (Dependency d : initialList)
/*  446:     */     {
/*  447: 687 */       lookup.put(d.getTarget().getPackage().getPackageMetaDataElement("PackageName").toString(), d);
/*  448:     */       
/*  449:     */ 
/*  450: 690 */       ArrayList<Dependency> deps = new ArrayList();
/*  451: 691 */       deps.add(d);
/*  452:     */       
/*  453:     */ 
/*  454: 694 */       conflicts.put(d.getTarget().getPackage().getPackageMetaDataElement("PackageName").toString(), deps);
/*  455:     */     }
/*  456: 700 */     for (Dependency d : initialList) {
/*  457: 701 */       checkDependencies(d.getTarget(), lookup, conflicts);
/*  458:     */     }
/*  459: 704 */     List<Dependency> fullList = new ArrayList(lookup.values());
/*  460:     */     
/*  461:     */ 
/*  462:     */ 
/*  463: 708 */     ArrayList<String> removeList = new ArrayList();
/*  464: 709 */     Iterator<String> keyIt = conflicts.keySet().iterator();
/*  465: 711 */     while (keyIt.hasNext())
/*  466:     */     {
/*  467: 712 */       String key = (String)keyIt.next();
/*  468: 713 */       List<Dependency> tempD = (List)conflicts.get(key);
/*  469: 714 */       if (tempD.size() == 1) {
/*  470: 716 */         removeList.add(key);
/*  471:     */       }
/*  472:     */     }
/*  473: 720 */     for (String s : removeList) {
/*  474: 721 */       conflicts.remove(s);
/*  475:     */     }
/*  476: 724 */     return fullList;
/*  477:     */   }
/*  478:     */   
/*  479:     */   public void installPackageFromRepository(String packageName, Object version, PrintStream... progress)
/*  480:     */     throws Exception
/*  481:     */   {
/*  482: 740 */     Package toInstall = getRepositoryPackageInfo(packageName, version);
/*  483: 741 */     if (toInstall.isInstalled()) {
/*  484: 742 */       for (PrintStream p : progress)
/*  485:     */       {
/*  486: 743 */         p.println("[DefaultPackageManager] cleanining installed package: " + toInstall.getName());
/*  487:     */         
/*  488: 745 */         uninstallPackage(toInstall.getName(), progress);
/*  489:     */       }
/*  490:     */     }
/*  491: 749 */     String urlString = toInstall.getPackageMetaDataElement("PackageURL").toString();
/*  492:     */     
/*  493: 751 */     URL packageURL = new URL(urlString);
/*  494:     */     
/*  495: 753 */     installPackageFromURL(packageURL, progress);
/*  496:     */   }
/*  497:     */   
/*  498:     */   public String installPackageFromURL(URL packageURL, PrintStream... progress)
/*  499:     */     throws Exception
/*  500:     */   {
/*  501: 768 */     File downloaded = downloadArchive(packageURL, "zip", progress);
/*  502: 769 */     return installPackageFromArchive(downloaded.getAbsolutePath(), progress);
/*  503:     */   }
/*  504:     */   
/*  505:     */   private static void copyStreams(InputStream input, OutputStream output)
/*  506:     */     throws IOException
/*  507:     */   {
/*  508: 775 */     byte[] data = new byte[1024];
/*  509:     */     int count;
/*  510: 776 */     while ((count = input.read(data, 0, 1024)) != -1) {
/*  511: 777 */       output.write(data, 0, count);
/*  512:     */     }
/*  513:     */   }
/*  514:     */   
/*  515:     */   protected void installPackage(String packageArchivePath, String packageName, PrintStream... progress)
/*  516:     */     throws Exception
/*  517:     */   {
/*  518: 793 */     if (!establishPackageHome()) {
/*  519: 794 */       throw new Exception("Unable to install " + packageArchivePath + " because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  520:     */     }
/*  521: 799 */     File destDir = new File(this.m_packageHome, packageName);
/*  522: 800 */     if (!destDir.mkdir()) {}
/*  523: 809 */     InputStream input = null;
/*  524: 810 */     OutputStream output = null;
/*  525:     */     
/*  526: 812 */     ZipFile zipFile = new ZipFile(packageArchivePath);
/*  527: 813 */     Enumeration enumeration = zipFile.entries();
/*  528: 814 */     while (enumeration.hasMoreElements())
/*  529:     */     {
/*  530: 815 */       ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
/*  531: 816 */       if (zipEntry.isDirectory())
/*  532:     */       {
/*  533: 817 */         new File(destDir, zipEntry.getName()).mkdirs();
/*  534:     */       }
/*  535:     */       else
/*  536:     */       {
/*  537: 820 */         File temp = new File(destDir, zipEntry.getName()).getParentFile();
/*  538: 821 */         if ((temp != null) && (!temp.exists())) {
/*  539: 822 */           temp.mkdirs();
/*  540:     */         }
/*  541: 826 */         for (PrintStream progres : progress) {
/*  542: 827 */           progres.println("[DefaultPackageManager] Installing: " + zipEntry.getName());
/*  543:     */         }
/*  544: 831 */         input = new BufferedInputStream(zipFile.getInputStream(zipEntry));
/*  545: 832 */         File destFile = new File(destDir, zipEntry.getName());
/*  546: 833 */         FileOutputStream fos = new FileOutputStream(destFile);
/*  547: 834 */         output = new BufferedOutputStream(fos);
/*  548: 835 */         copyStreams(input, output);
/*  549: 836 */         input.close();
/*  550: 837 */         output.flush();
/*  551: 838 */         output.close();
/*  552:     */       }
/*  553:     */     }
/*  554: 842 */     s_installedPackageList = null;
/*  555: 843 */     deleteInstalledPackageCacheFile();
/*  556:     */   }
/*  557:     */   
/*  558:     */   private URLConnection getConnection(String urlString)
/*  559:     */     throws IOException
/*  560:     */   {
/*  561: 847 */     URL connURL = new URL(urlString);
/*  562:     */     
/*  563: 849 */     return getConnection(connURL);
/*  564:     */   }
/*  565:     */   
/*  566:     */   private URLConnection getConnection(URL connURL)
/*  567:     */     throws IOException
/*  568:     */   {
/*  569: 853 */     URLConnection conn = null;
/*  570: 856 */     if (setProxyAuthentication(connURL)) {
/*  571: 857 */       conn = connURL.openConnection(this.m_httpProxy);
/*  572:     */     } else {
/*  573: 859 */       conn = connURL.openConnection();
/*  574:     */     }
/*  575: 863 */     conn.setConnectTimeout(this.m_timeout);
/*  576:     */     
/*  577: 865 */     return conn;
/*  578:     */   }
/*  579:     */   
/*  580:     */   private void transToBAOS(BufferedInputStream bi, ByteArrayOutputStream bos)
/*  581:     */     throws Exception
/*  582:     */   {
/*  583:     */     for (;;)
/*  584:     */     {
/*  585: 871 */       synchronized (m_buffer)
/*  586:     */       {
/*  587: 872 */         int amountRead = bi.read(m_buffer);
/*  588: 873 */         if (amountRead == -1) {
/*  589:     */           break;
/*  590:     */         }
/*  591: 876 */         bos.write(m_buffer, 0, amountRead);
/*  592:     */       }
/*  593:     */     }
/*  594: 880 */     bi.close();
/*  595:     */   }
/*  596:     */   
/*  597:     */   private void writeZipEntryForPackage(String packageName, ZipOutputStream zos)
/*  598:     */     throws Exception
/*  599:     */   {
/*  600: 886 */     ZipEntry packageDir = new ZipEntry(packageName + "/");
/*  601: 887 */     zos.putNextEntry(packageDir);
/*  602:     */     
/*  603: 889 */     ZipEntry z = new ZipEntry(packageName + "/Latest.props");
/*  604: 890 */     ZipEntry z2 = new ZipEntry(packageName + "/Latest.html");
/*  605: 891 */     URLConnection conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/Latest.props");
/*  606:     */     
/*  607:     */ 
/*  608:     */ 
/*  609: 895 */     BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());
/*  610: 896 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  611:     */     
/*  612: 898 */     transToBAOS(bi, bos);
/*  613: 899 */     zos.putNextEntry(z);
/*  614: 900 */     zos.write(bos.toByteArray());
/*  615:     */     
/*  616: 902 */     conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/Latest.html");
/*  617:     */     
/*  618:     */ 
/*  619: 905 */     bi = new BufferedInputStream(conn.getInputStream());
/*  620: 906 */     bos = new ByteArrayOutputStream();
/*  621: 907 */     transToBAOS(bi, bos);
/*  622: 908 */     zos.putNextEntry(z2);
/*  623: 909 */     zos.write(bos.toByteArray());
/*  624:     */     
/*  625:     */ 
/*  626: 912 */     z = new ZipEntry(packageName + "/versions.txt");
/*  627: 913 */     conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/versions.txt");
/*  628:     */     
/*  629:     */ 
/*  630: 916 */     bi = new BufferedInputStream(conn.getInputStream());
/*  631: 917 */     bos = new ByteArrayOutputStream();
/*  632: 918 */     transToBAOS(bi, bos);
/*  633: 919 */     zos.putNextEntry(z);
/*  634: 920 */     zos.write(bos.toByteArray());
/*  635:     */     
/*  636:     */ 
/*  637: 923 */     z = new ZipEntry(packageName + "/index.html");
/*  638: 924 */     conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/index.html");
/*  639:     */     
/*  640:     */ 
/*  641: 927 */     bi = new BufferedInputStream(conn.getInputStream());
/*  642: 928 */     bos = new ByteArrayOutputStream();
/*  643: 929 */     transToBAOS(bi, bos);
/*  644: 930 */     zos.putNextEntry(z);
/*  645: 931 */     zos.write(bos.toByteArray());
/*  646:     */     
/*  647:     */ 
/*  648: 934 */     List<Object> versions = getRepositoryPackageVersions(packageName);
/*  649: 935 */     for (Object o : versions)
/*  650:     */     {
/*  651: 936 */       conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/" + o.toString() + ".props");
/*  652:     */       
/*  653:     */ 
/*  654: 939 */       z = new ZipEntry(packageName + "/" + o.toString() + ".props");
/*  655: 940 */       bi = new BufferedInputStream(conn.getInputStream());
/*  656: 941 */       bos = new ByteArrayOutputStream();
/*  657: 942 */       transToBAOS(bi, bos);
/*  658: 943 */       zos.putNextEntry(z);
/*  659: 944 */       zos.write(bos.toByteArray());
/*  660:     */       
/*  661: 946 */       conn = getConnection(this.m_packageRepository.toString() + "/" + packageName + "/" + o.toString() + ".html");
/*  662:     */       
/*  663:     */ 
/*  664: 949 */       z = new ZipEntry(packageName + "/" + o.toString() + ".html");
/*  665: 950 */       bi = new BufferedInputStream(conn.getInputStream());
/*  666: 951 */       bos = new ByteArrayOutputStream();
/*  667: 952 */       transToBAOS(bi, bos);
/*  668: 953 */       zos.putNextEntry(z);
/*  669: 954 */       zos.write(bos.toByteArray());
/*  670:     */     }
/*  671:     */   }
/*  672:     */   
/*  673:     */   public byte[] getRepositoryPackageMetaDataOnlyAsZip(PrintStream... progress)
/*  674:     */     throws Exception
/*  675:     */   {
/*  676: 970 */     if (getPackageRepositoryURL() == null) {
/*  677: 971 */       throw new Exception("[DefaultPackageManager] No package repository set!!");
/*  678:     */     }
/*  679:     */     try
/*  680:     */     {
/*  681: 975 */       String repoZip = this.m_packageRepository.toString() + "/repo.zip";
/*  682: 976 */       URLConnection conn = null;
/*  683: 977 */       conn = getConnection(repoZip);
/*  684:     */       
/*  685: 979 */       BufferedInputStream bi = new BufferedInputStream(conn.getInputStream());
/*  686:     */       
/*  687: 981 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  688:     */       
/*  689:     */ 
/*  690: 984 */       int totalBytesRead = 0;
/*  691:     */       for (;;)
/*  692:     */       {
/*  693: 986 */         synchronized (m_buffer)
/*  694:     */         {
/*  695: 987 */           int amountRead = bi.read(m_buffer);
/*  696: 988 */           if (amountRead == -1)
/*  697:     */           {
/*  698: 989 */             PrintStream[] arr$ = progress;int len$ = arr$.length;int i$ = 0;
/*  699: 989 */             if (i$ < len$)
/*  700:     */             {
/*  701: 989 */               PrintStream progres = arr$[i$];
/*  702: 990 */               progres.println("[DefaultPackageManager] downloaded " + totalBytesRead / 1000 + " KB");i$++; continue;
/*  703:     */             }
/*  704: 993 */             break;
/*  705:     */           }
/*  706: 995 */           bos.write(m_buffer, 0, amountRead);
/*  707: 996 */           totalBytesRead += amountRead;
/*  708: 997 */           PrintStream[] arr$ = progress;int len$ = arr$.length;int i$ = 0;
/*  709: 997 */           if (i$ < len$)
/*  710:     */           {
/*  711: 997 */             PrintStream progres = arr$[i$];
/*  712: 998 */             progres.println("[DefaultPackageManager] downloaded " + totalBytesRead / 1000 + " KB");i$++;
/*  713:     */           }
/*  714:     */         }
/*  715:     */       }
/*  716:1004 */       bi.close();
/*  717:     */       
/*  718:1006 */       return bos.toByteArray();
/*  719:     */     }
/*  720:     */     catch (Exception ex)
/*  721:     */     {
/*  722:1008 */       System.err.println("Unable to download repository zip archve (" + ex.getMessage() + ") - trying legacy routine...");
/*  723:     */     }
/*  724:1010 */     return getRepositoryPackageMetaDataOnlyAsZipLegacy(progress);
/*  725:     */   }
/*  726:     */   
/*  727:     */   public byte[] getRepositoryPackageMetaDataOnlyAsZipLegacy(PrintStream... progress)
/*  728:     */     throws Exception
/*  729:     */   {
/*  730:1025 */     if (getPackageRepositoryURL() == null) {
/*  731:1026 */       throw new Exception("[DefaultPackageManager] No package repository set!!");
/*  732:     */     }
/*  733:1029 */     String packageList = this.m_packageRepository.toString() + "/packageList.txt";
/*  734:1030 */     String packageListWithVersion = this.m_packageRepository.toString() + "/packageListWithVersion.txt";
/*  735:     */     
/*  736:     */ 
/*  737:1033 */     URLConnection conn = null;
/*  738:1034 */     conn = getConnection(packageList);
/*  739:     */     
/*  740:1036 */     BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  741:     */     
/*  742:     */ 
/*  743:1039 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  744:1040 */     ZipOutputStream zos = new ZipOutputStream(bos);
/*  745:     */     String packageName;
/*  746:1044 */     while ((packageName = bi.readLine()) != null)
/*  747:     */     {
/*  748:1045 */       for (PrintStream p : progress) {
/*  749:1046 */         p.println("Fetching meta data for " + packageName);
/*  750:     */       }
/*  751:1048 */       writeZipEntryForPackage(packageName, zos);
/*  752:     */     }
/*  753:1050 */     bi.close();
/*  754:     */     
/*  755:     */ 
/*  756:1053 */     conn = getConnection(packageList);
/*  757:1054 */     ZipEntry z = new ZipEntry("packageList.txt");
/*  758:1055 */     BufferedInputStream bi2 = new BufferedInputStream(conn.getInputStream());
/*  759:1056 */     ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
/*  760:1057 */     transToBAOS(bi2, bos2);
/*  761:1058 */     zos.putNextEntry(z);
/*  762:1059 */     zos.write(bos2.toByteArray());
/*  763:1060 */     bi2.close();
/*  764:     */     
/*  765:     */ 
/*  766:1063 */     conn = getConnection(packageListWithVersion);
/*  767:1064 */     z = new ZipEntry("packageListWithVersion.txt");
/*  768:1065 */     bi2 = new BufferedInputStream(conn.getInputStream());
/*  769:1066 */     bos2 = new ByteArrayOutputStream();
/*  770:1067 */     transToBAOS(bi2, bos2);
/*  771:1068 */     zos.putNextEntry(z);
/*  772:1069 */     zos.write(bos2.toByteArray());
/*  773:1070 */     bi2.close();
/*  774:     */     
/*  775:     */ 
/*  776:1073 */     String imageList = this.m_packageRepository.toString() + "/images.txt";
/*  777:1074 */     conn = getConnection(imageList);
/*  778:1075 */     bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  779:     */     String imageName;
/*  780:1078 */     while ((imageName = bi.readLine()) != null)
/*  781:     */     {
/*  782:1080 */       z = new ZipEntry(imageName);
/*  783:1081 */       URLConnection conn2 = getConnection(this.m_packageRepository.toString() + "/" + imageName);
/*  784:     */       
/*  785:1083 */       bi2 = new BufferedInputStream(conn2.getInputStream());
/*  786:1084 */       bos2 = new ByteArrayOutputStream();
/*  787:1085 */       transToBAOS(bi2, bos2);
/*  788:1086 */       zos.putNextEntry(z);
/*  789:1087 */       zos.write(bos2.toByteArray());
/*  790:1088 */       bi2.close();
/*  791:     */     }
/*  792:1092 */     conn = getConnection(imageList);
/*  793:1093 */     z = new ZipEntry("images.txt");
/*  794:1094 */     bi2 = new BufferedInputStream(conn.getInputStream());
/*  795:1095 */     bos2 = new ByteArrayOutputStream();
/*  796:1096 */     transToBAOS(bi2, bos2);
/*  797:1097 */     zos.putNextEntry(z);
/*  798:1098 */     zos.write(bos2.toByteArray());
/*  799:1099 */     bi2.close();
/*  800:     */     
/*  801:1101 */     zos.close();
/*  802:     */     
/*  803:1103 */     return bos.toByteArray();
/*  804:     */   }
/*  805:     */   
/*  806:     */   public List<Package> getAllPackages(PrintStream... progress)
/*  807:     */     throws Exception
/*  808:     */   {
/*  809:1117 */     ArrayList<Package> allPackages = new ArrayList();
/*  810:1119 */     if (getPackageRepositoryURL() == null) {
/*  811:1120 */       throw new Exception("[DefaultPackageManager] No package repository set!!");
/*  812:     */     }
/*  813:1123 */     String packageList = this.m_packageRepository.toString() + "/packageList.txt";
/*  814:     */     
/*  815:1125 */     URL packageListURL = new URL(packageList);
/*  816:1126 */     URLConnection conn = getConnection(packageListURL);
/*  817:     */     
/*  818:1128 */     BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  819:     */     String packageName;
/*  820:1132 */     while ((packageName = bi.readLine()) != null)
/*  821:     */     {
/*  822:1133 */       Package temp = getRepositoryPackageInfo(packageName);
/*  823:1134 */       allPackages.add(temp);
/*  824:     */     }
/*  825:1137 */     return allPackages;
/*  826:     */   }
/*  827:     */   
/*  828:     */   public List<Package> getAvailablePackages()
/*  829:     */     throws Exception
/*  830:     */   {
/*  831:1148 */     List<Package> allP = getAllPackages(new PrintStream[0]);
/*  832:1149 */     List<Package> available = new ArrayList();
/*  833:1151 */     for (int i = 0; i < allP.size(); i++) {
/*  834:1152 */       if (!((Package)allP.get(i)).isInstalled()) {
/*  835:1153 */         available.add(allP.get(i));
/*  836:     */       }
/*  837:     */     }
/*  838:1157 */     return available;
/*  839:     */   }
/*  840:     */   
/*  841:     */   public List<Package> getInstalledPackages()
/*  842:     */     throws Exception
/*  843:     */   {
/*  844:1168 */     if (!establishPackageHome()) {
/*  845:1169 */       throw new Exception("Unable to get list of installed packages because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  846:     */     }
/*  847:1174 */     if (s_installedPackageList != null) {
/*  848:1175 */       return s_installedPackageList;
/*  849:     */     }
/*  850:1178 */     s_installedPackageList = loadInstalledPackageCache();
/*  851:1179 */     if (s_installedPackageList != null) {
/*  852:1180 */       return s_installedPackageList;
/*  853:     */     }
/*  854:1183 */     List<Package> installedP = new ArrayList();
/*  855:     */     
/*  856:1185 */     File[] contents = this.m_packageHome.listFiles();
/*  857:1187 */     for (File content : contents) {
/*  858:1188 */       if (content.isDirectory())
/*  859:     */       {
/*  860:1189 */         File description = new File(content.getAbsolutePath() + File.separator + "Description.props");
/*  861:1193 */         if (description.exists()) {
/*  862:     */           try
/*  863:     */           {
/*  864:1195 */             Properties packageProperties = new Properties();
/*  865:1196 */             BufferedInputStream bi = new BufferedInputStream(new FileInputStream(description));
/*  866:     */             
/*  867:1198 */             packageProperties.load(bi);
/*  868:1199 */             bi.close();
/*  869:1200 */             bi = null;
/*  870:1201 */             DefaultPackage pkg = new DefaultPackage(this.m_packageHome, this, packageProperties);
/*  871:     */             
/*  872:1203 */             installedP.add(pkg);
/*  873:     */           }
/*  874:     */           catch (Exception ex) {}
/*  875:     */         }
/*  876:     */       }
/*  877:     */     }
/*  878:1211 */     s_installedPackageList = installedP;
/*  879:1212 */     saveInstalledPackageCache(installedP);
/*  880:     */     
/*  881:1214 */     return installedP;
/*  882:     */   }
/*  883:     */   
/*  884:     */   protected void deleteInstalledPackageCacheFile()
/*  885:     */     throws Exception
/*  886:     */   {
/*  887:1218 */     if (!establishPackageHome()) {
/*  888:1219 */       throw new Exception("Unable to delete installed package cache file because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  889:     */     }
/*  890:1224 */     File cache = new File(this.m_packageHome, "installedPackageCache.ser");
/*  891:1225 */     if ((cache.exists()) && 
/*  892:1226 */       (!cache.delete()))
/*  893:     */     {
/*  894:1227 */       System.err.println("Unable to delete installed package cache file '" + cache.toString() + "'");
/*  895:     */       
/*  896:1229 */       cache.deleteOnExit();
/*  897:     */     }
/*  898:     */   }
/*  899:     */   
/*  900:     */   protected void saveInstalledPackageCache(List<Package> cacheToSave)
/*  901:     */     throws Exception
/*  902:     */   {
/*  903:1243 */     if (!establishPackageHome()) {
/*  904:1244 */       throw new Exception("Unable to save installed package cache file because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  905:     */     }
/*  906:1249 */     ObjectOutputStream oos = null;
/*  907:     */     try
/*  908:     */     {
/*  909:1251 */       oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(this.m_packageHome, "installedPackageCache.ser"))));
/*  910:     */       
/*  911:     */ 
/*  912:1254 */       oos.writeObject(cacheToSave);
/*  913:1255 */       oos.flush();
/*  914:     */     }
/*  915:     */     finally
/*  916:     */     {
/*  917:1257 */       if (oos != null) {
/*  918:1258 */         oos.close();
/*  919:     */       }
/*  920:     */     }
/*  921:     */   }
/*  922:     */   
/*  923:     */   protected List<Package> loadInstalledPackageCache()
/*  924:     */     throws Exception
/*  925:     */   {
/*  926:1270 */     if (!establishPackageHome()) {
/*  927:1271 */       throw new Exception("Unable to load installed package cache file because package home (" + this.m_packageHome.getAbsolutePath() + ") can't be established.");
/*  928:     */     }
/*  929:1276 */     List<Package> installedP = null;
/*  930:1277 */     if (new File(this.m_packageHome, "installedPackageCache.ser").exists())
/*  931:     */     {
/*  932:1278 */       ObjectInputStream ois = null;
/*  933:     */       try
/*  934:     */       {
/*  935:1280 */         ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(this.m_packageHome.toString(), "installedPackageCache.ser"))));
/*  936:     */         
/*  937:     */ 
/*  938:     */ 
/*  939:1284 */         installedP = (List)ois.readObject();
/*  940:     */       }
/*  941:     */       finally
/*  942:     */       {
/*  943:1286 */         if (ois != null) {
/*  944:1287 */           ois.close();
/*  945:     */         }
/*  946:     */       }
/*  947:     */     }
/*  948:1292 */     if (installedP != null) {
/*  949:1293 */       for (Package p : installedP) {
/*  950:1294 */         if ((p instanceof DefaultPackage)) {
/*  951:1295 */           ((DefaultPackage)p).setPackageManager(this);
/*  952:     */         }
/*  953:     */       }
/*  954:     */     }
/*  955:1300 */     return installedP;
/*  956:     */   }
/*  957:     */   
/*  958:     */   protected static String padLeft(String inString, int length)
/*  959:     */   {
/*  960:1314 */     return fixStringLength(inString, length, false);
/*  961:     */   }
/*  962:     */   
/*  963:     */   protected static String padRight(String inString, int length)
/*  964:     */   {
/*  965:1328 */     return fixStringLength(inString, length, true);
/*  966:     */   }
/*  967:     */   
/*  968:     */   private static String fixStringLength(String inString, int length, boolean right)
/*  969:     */   {
/*  970:1343 */     if (inString.length() < length) {
/*  971:1344 */       while (inString.length() < length) {
/*  972:1345 */         inString = right ? inString.concat(" ") : " ".concat(inString);
/*  973:     */       }
/*  974:     */     }
/*  975:1347 */     if (inString.length() > length) {
/*  976:1348 */       inString = inString.substring(0, length);
/*  977:     */     }
/*  978:1350 */     return inString;
/*  979:     */   }
/*  980:     */   
/*  981:     */   public static void main(String[] args)
/*  982:     */   {
/*  983:     */     try
/*  984:     */     {
/*  985:1364 */       URL url = new URL(args[0]);
/*  986:1365 */       DefaultPackageManager pm = new DefaultPackageManager();
/*  987:1366 */       pm.downloadArchive(url, args[1], new PrintStream[] { System.out });
/*  988:     */     }
/*  989:     */     catch (Exception ex)
/*  990:     */     {
/*  991:1369 */       ex.printStackTrace();
/*  992:     */     }
/*  993:     */   }
/*  994:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.DefaultPackageManager
 * JD-Core Version:    0.7.0.1
 */