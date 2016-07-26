/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.BufferedInputStream;
/*    4:     */ import java.io.BufferedOutputStream;
/*    5:     */ import java.io.BufferedReader;
/*    6:     */ import java.io.BufferedWriter;
/*    7:     */ import java.io.ByteArrayInputStream;
/*    8:     */ import java.io.File;
/*    9:     */ import java.io.FileInputStream;
/*   10:     */ import java.io.FileNotFoundException;
/*   11:     */ import java.io.FileOutputStream;
/*   12:     */ import java.io.FileWriter;
/*   13:     */ import java.io.IOException;
/*   14:     */ import java.io.InputStreamReader;
/*   15:     */ import java.io.ObjectInputStream;
/*   16:     */ import java.io.ObjectOutputStream;
/*   17:     */ import java.io.PrintStream;
/*   18:     */ import java.net.MalformedURLException;
/*   19:     */ import java.net.URI;
/*   20:     */ import java.net.URL;
/*   21:     */ import java.net.URLConnection;
/*   22:     */ import java.text.SimpleDateFormat;
/*   23:     */ import java.util.ArrayList;
/*   24:     */ import java.util.Date;
/*   25:     */ import java.util.Enumeration;
/*   26:     */ import java.util.HashMap;
/*   27:     */ import java.util.HashSet;
/*   28:     */ import java.util.Iterator;
/*   29:     */ import java.util.List;
/*   30:     */ import java.util.Map;
/*   31:     */ import java.util.Properties;
/*   32:     */ import java.util.Set;
/*   33:     */ import java.util.StringTokenizer;
/*   34:     */ import java.util.zip.ZipEntry;
/*   35:     */ import java.util.zip.ZipInputStream;
/*   36:     */ import weka.core.converters.ConverterUtils;
/*   37:     */ import weka.core.logging.Logger;
/*   38:     */ import weka.core.logging.Logger.Level;
/*   39:     */ import weka.core.packageManagement.DefaultPackageManager;
/*   40:     */ import weka.core.packageManagement.Dependency;
/*   41:     */ import weka.core.packageManagement.Package;
/*   42:     */ import weka.core.packageManagement.PackageConstraint;
/*   43:     */ import weka.core.packageManagement.PackageManager;
/*   44:     */ import weka.core.packageManagement.VersionPackageConstraint;
/*   45:     */ import weka.core.packageManagement.VersionPackageConstraint.VersionComparison;
/*   46:     */ import weka.gui.GenericObjectEditor;
/*   47:     */ import weka.gui.GenericPropertiesCreator;
/*   48:     */ import weka.gui.beans.BeansProperties;
/*   49:     */ import weka.gui.beans.KnowledgeFlowApp;
/*   50:     */ import weka.gui.explorer.ExplorerDefaults;
/*   51:     */ 
/*   52:     */ public class WekaPackageManager
/*   53:     */ {
/*   54:  81 */   private static String WEKAFILES_DIR_NAME = "wekafiles";
/*   55:  84 */   public static File WEKA_HOME = new File(System.getProperty("user.home") + File.separator + WEKAFILES_DIR_NAME);
/*   56:  88 */   public static File PACKAGES_DIR = new File(WEKA_HOME.toString() + File.separator + "packages");
/*   57:  92 */   private static String PROPERTIES_DIR_NAME = "props";
/*   58:  95 */   public static File PROPERTIES_DIR = new File(WEKA_HOME.toString() + File.separator + PROPERTIES_DIR_NAME);
/*   59:  99 */   private static PackageManager PACKAGE_MANAGER = PackageManager.create();
/*   60:     */   private static URL REP_URL;
/*   61:     */   private static URL CACHE_URL;
/*   62: 108 */   private static boolean INITIAL_CACHE_BUILD_NEEDED = false;
/*   63: 113 */   private static String PACKAGE_LIST_FILENAME = "packageListWithVersion.txt";
/*   64: 116 */   private static String PRIMARY_REPOSITORY = "http://weka.sourceforge.net/packageMetaData";
/*   65:     */   private static String REP_MIRROR;
/*   66: 126 */   private static boolean USER_SET_REPO = false;
/*   67: 129 */   private static String PACKAGE_MANAGER_PROPS_FILE_NAME = "PackageManager.props";
/*   68:     */   public static boolean m_offline;
/*   69: 136 */   private static boolean m_loadPackages = true;
/*   70:     */   protected static boolean m_wekaHomeEstablished;
/*   71:     */   protected static boolean m_packagesLoaded;
/*   72:     */   protected static final String PACKAGE_LIST_WITH_VERSION_FILE = "packageListWithVersion.txt";
/*   73:     */   protected static final String FORCED_REFRESH_COUNT_FILE = "forcedRefreshCount.txt";
/*   74: 153 */   public static boolean m_initialPackageLoadingInProcess = false;
/*   75:     */   public static boolean m_noPackageMetaDataAvailable;
/*   76:     */   public static Set<String> m_doNotLoadList;
/*   77:     */   
/*   78:     */   static
/*   79:     */   {
/*   80: 162 */     establishWekaHome();
/*   81:     */   }
/*   82:     */   
/*   83:     */   protected static boolean establishWekaHome()
/*   84:     */   {
/*   85: 171 */     if (m_wekaHomeEstablished) {
/*   86: 172 */       return true;
/*   87:     */     }
/*   88:     */     try
/*   89:     */     {
/*   90: 178 */       PluginManager.addFromProperties(WekaPackageManager.class.getClassLoader().getResourceAsStream("weka/PluginManager.props"), true);
/*   91:     */     }
/*   92:     */     catch (Exception ex)
/*   93:     */     {
/*   94: 181 */       log(Logger.Level.WARNING, "[WekaPackageManager] unable to read weka/PluginManager.props");
/*   95:     */     }
/*   96: 187 */     Environment env = Environment.getSystemWide();
/*   97: 188 */     String wh = env.getVariableValue("WEKA_HOME");
/*   98: 189 */     if (wh != null)
/*   99:     */     {
/*  100: 190 */       WEKA_HOME = new File(wh);
/*  101: 191 */       PACKAGES_DIR = new File(wh + File.separator + "packages");
/*  102: 192 */       PROPERTIES_DIR = new File(wh + File.separator + PROPERTIES_DIR_NAME);
/*  103:     */     }
/*  104:     */     else
/*  105:     */     {
/*  106: 194 */       env.addVariableSystemWide("WEKA_HOME", WEKA_HOME.toString());
/*  107:     */     }
/*  108: 197 */     boolean ok = true;
/*  109: 198 */     if (!WEKA_HOME.exists()) {
/*  110: 200 */       if (!WEKA_HOME.mkdir())
/*  111:     */       {
/*  112: 201 */         System.err.println("Unable to create WEKA_HOME (" + WEKA_HOME.getAbsolutePath() + ")");
/*  113:     */         
/*  114: 203 */         ok = false;
/*  115:     */       }
/*  116:     */     }
/*  117: 207 */     if (!PACKAGES_DIR.exists()) {
/*  118: 209 */       if (!PACKAGES_DIR.mkdir())
/*  119:     */       {
/*  120: 210 */         System.err.println("Unable to create packages directory (" + PACKAGES_DIR.getAbsolutePath() + ")");
/*  121:     */         
/*  122: 212 */         ok = false;
/*  123:     */       }
/*  124:     */     }
/*  125: 216 */     m_wekaHomeEstablished = ok;
/*  126: 217 */     PACKAGE_MANAGER.setPackageHome(PACKAGES_DIR);
/*  127:     */     
/*  128: 219 */     m_doNotLoadList = getDoNotLoadList();
/*  129:     */     try
/*  130:     */     {
/*  131: 225 */       String repURL = env.getVariableValue("weka.core.wekaPackageRepositoryURL");
/*  132: 227 */       if ((repURL == null) || (repURL.length() == 0))
/*  133:     */       {
/*  134: 230 */         File repPropsFile = new File(PROPERTIES_DIR.toString() + File.separator + "PackageRepository.props");
/*  135: 233 */         if (repPropsFile.exists())
/*  136:     */         {
/*  137: 234 */           Properties repProps = new Properties();
/*  138: 235 */           repProps.load(new FileInputStream(repPropsFile));
/*  139: 236 */           repURL = repProps.getProperty("weka.core.wekaPackageRepositoryURL");
/*  140:     */         }
/*  141:     */       }
/*  142: 240 */       if ((repURL == null) || (repURL.length() == 0))
/*  143:     */       {
/*  144: 241 */         repURL = PRIMARY_REPOSITORY;
/*  145:     */       }
/*  146:     */       else
/*  147:     */       {
/*  148: 243 */         log(Logger.Level.INFO, "[WekaPackageManager] weka.core.WekaPackageRepositoryURL = " + repURL);
/*  149:     */         
/*  150:     */ 
/*  151:     */ 
/*  152:     */ 
/*  153:     */ 
/*  154: 249 */         USER_SET_REPO = true;
/*  155:     */       }
/*  156: 252 */       REP_URL = new URL(repURL);
/*  157: 253 */       PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/*  158:     */     }
/*  159:     */     catch (MalformedURLException ex)
/*  160:     */     {
/*  161: 255 */       ex.printStackTrace();
/*  162:     */     }
/*  163:     */     catch (IOException ex)
/*  164:     */     {
/*  165: 257 */       ex.printStackTrace();
/*  166:     */     }
/*  167: 260 */     PACKAGE_MANAGER.setBaseSystemName("weka");
/*  168: 261 */     PACKAGE_MANAGER.setBaseSystemVersion(Version.VERSION);
/*  169:     */     
/*  170:     */ 
/*  171: 264 */     File cacheDir = new File(WEKA_HOME.toString() + File.separator + "repCache");
/*  172:     */     try
/*  173:     */     {
/*  174: 267 */       String tempCacheString = "file://" + cacheDir.toString();
/*  175:     */       
/*  176: 269 */       tempCacheString = tempCacheString.replace(" ", "%20");
/*  177: 270 */       tempCacheString = tempCacheString.replace('\\', '/');
/*  178: 271 */       if ((tempCacheString.startsWith("file://")) && (!tempCacheString.startsWith("file:///")))
/*  179:     */       {
/*  180: 273 */         tempCacheString = tempCacheString.substring(7);
/*  181: 274 */         tempCacheString = "file:///" + tempCacheString;
/*  182:     */       }
/*  183: 276 */       URI tempURI = new URI(tempCacheString);
/*  184:     */       
/*  185: 278 */       CACHE_URL = tempURI.toURL();
/*  186:     */     }
/*  187:     */     catch (Exception e)
/*  188:     */     {
/*  189: 280 */       e.printStackTrace();
/*  190:     */     }
/*  191: 283 */     File packagesList = new File(cacheDir.getAbsolutePath() + File.separator + PACKAGE_LIST_FILENAME);
/*  192: 285 */     if (!cacheDir.exists()) {
/*  193: 286 */       if (!cacheDir.mkdir())
/*  194:     */       {
/*  195: 287 */         System.err.println("Unable to create repository cache directory (" + cacheDir.getAbsolutePath() + ")");
/*  196:     */         
/*  197: 289 */         log(Logger.Level.WARNING, "Unable to create repository cache directory (" + cacheDir.getAbsolutePath() + ")");
/*  198:     */         
/*  199:     */ 
/*  200: 292 */         CACHE_URL = null;
/*  201:     */       }
/*  202:     */       else
/*  203:     */       {
/*  204: 295 */         INITIAL_CACHE_BUILD_NEEDED = true;
/*  205:     */       }
/*  206:     */     }
/*  207: 299 */     if (!packagesList.exists()) {
/*  208: 300 */       INITIAL_CACHE_BUILD_NEEDED = true;
/*  209:     */     }
/*  210: 305 */     String offline = env.getVariableValue("weka.packageManager.offline");
/*  211: 306 */     if (offline != null) {
/*  212: 307 */       m_offline = offline.equalsIgnoreCase("true");
/*  213:     */     }
/*  214: 309 */     String loadPackages = env.getVariableValue("weka.packageManager.loadPackages");
/*  215: 311 */     if (loadPackages == null) {
/*  216: 313 */       loadPackages = env.getVariableValue("weka.core.loadPackages");
/*  217:     */     }
/*  218: 316 */     if (loadPackages != null) {
/*  219: 317 */       m_loadPackages = loadPackages.equalsIgnoreCase("true");
/*  220:     */     }
/*  221: 321 */     File generalProps = new File(PROPERTIES_DIR.toString() + File.separator + PACKAGE_MANAGER_PROPS_FILE_NAME);
/*  222: 323 */     if (generalProps.exists())
/*  223:     */     {
/*  224: 324 */       Properties gProps = new Properties();
/*  225:     */       try
/*  226:     */       {
/*  227: 326 */         gProps.load(new FileInputStream(generalProps));
/*  228:     */         
/*  229: 328 */         String repURL = gProps.getProperty("weka.core.wekaPackageRepositoryURL");
/*  230: 330 */         if ((repURL != null) && (repURL.length() > 0))
/*  231:     */         {
/*  232: 331 */           REP_URL = new URL(repURL);
/*  233: 332 */           PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/*  234:     */         }
/*  235: 335 */         offline = gProps.getProperty("weka.packageManager.offline");
/*  236: 336 */         if ((offline != null) && (offline.length() > 0)) {
/*  237: 337 */           m_offline = offline.equalsIgnoreCase("true");
/*  238:     */         }
/*  239: 340 */         loadPackages = gProps.getProperty("weka.packageManager.loadPackages");
/*  240: 341 */         if (loadPackages == null) {
/*  241: 343 */           loadPackages = env.getVariableValue("weka.core.loadPackages");
/*  242:     */         }
/*  243: 345 */         if (loadPackages != null) {
/*  244: 346 */           m_loadPackages = loadPackages.equalsIgnoreCase("true");
/*  245:     */         }
/*  246: 349 */         String pluginManagerDisableList = gProps.getProperty("weka.pluginManager.disable");
/*  247: 351 */         if ((pluginManagerDisableList != null) && (pluginManagerDisableList.length() > 0))
/*  248:     */         {
/*  249: 353 */           List<String> disable = new ArrayList();
/*  250: 354 */           String[] parts = pluginManagerDisableList.split(",");
/*  251: 355 */           for (String s : parts) {
/*  252: 356 */             disable.add(s.trim());
/*  253:     */           }
/*  254: 358 */           PluginManager.addToDisabledList(disable);
/*  255:     */         }
/*  256:     */       }
/*  257:     */       catch (FileNotFoundException e)
/*  258:     */       {
/*  259: 361 */         e.printStackTrace();
/*  260:     */       }
/*  261:     */       catch (IOException e)
/*  262:     */       {
/*  263: 363 */         e.printStackTrace();
/*  264:     */       }
/*  265:     */     }
/*  266: 367 */     if ((INITIAL_CACHE_BUILD_NEEDED) && (m_offline)) {
/*  267: 368 */       m_noPackageMetaDataAvailable = true;
/*  268:     */     }
/*  269: 376 */     PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/*  270: 377 */     PACKAGE_MANAGER.setProxyAuthentication(REP_URL);
/*  271:     */     
/*  272: 379 */     return ok;
/*  273:     */   }
/*  274:     */   
/*  275:     */   protected static void establishMirror()
/*  276:     */   {
/*  277: 386 */     if (m_offline) {
/*  278: 387 */       return;
/*  279:     */     }
/*  280:     */     try
/*  281:     */     {
/*  282: 391 */       String mirrorListURL = "http://www.cs.waikato.ac.nz/ml/weka/packageMetaDataMirror.txt";
/*  283:     */       
/*  284:     */ 
/*  285: 394 */       URLConnection conn = null;
/*  286: 395 */       URL connURL = new URL(mirrorListURL);
/*  287: 397 */       if (PACKAGE_MANAGER.setProxyAuthentication(connURL)) {
/*  288: 398 */         conn = connURL.openConnection(PACKAGE_MANAGER.getProxy());
/*  289:     */       } else {
/*  290: 400 */         conn = connURL.openConnection();
/*  291:     */       }
/*  292: 403 */       conn.setConnectTimeout(10000);
/*  293: 404 */       conn.setReadTimeout(10000);
/*  294:     */       
/*  295: 406 */       BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  296:     */       
/*  297:     */ 
/*  298: 409 */       REP_MIRROR = bi.readLine();
/*  299:     */       
/*  300: 411 */       bi.close();
/*  301: 412 */       if ((REP_MIRROR != null) && (REP_MIRROR.length() > 0)) {
/*  302: 416 */         if ((!REP_MIRROR.equals(PRIMARY_REPOSITORY)) && (!USER_SET_REPO))
/*  303:     */         {
/*  304: 418 */           log(Logger.Level.INFO, "[WekaPackageManager] Package manager using repository mirror: " + REP_MIRROR);
/*  305:     */           
/*  306:     */ 
/*  307:     */ 
/*  308: 422 */           REP_URL = new URL(REP_MIRROR);
/*  309:     */         }
/*  310:     */       }
/*  311:     */     }
/*  312:     */     catch (Exception ex)
/*  313:     */     {
/*  314: 426 */       log(Logger.Level.WARNING, "[WekaPackageManager] The repository meta data mirror file seems to be unavailable (" + ex.getMessage() + ")");
/*  315:     */     }
/*  316:     */   }
/*  317:     */   
/*  318:     */   protected static void log(Logger.Level level, String message)
/*  319:     */   {
/*  320:     */     try
/*  321:     */     {
/*  322: 441 */       File logFile = new File(WEKA_HOME.toString() + File.separator + "weka.log");
/*  323:     */       
/*  324: 443 */       BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
/*  325: 444 */       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*  326: 445 */       String linefeed = System.getProperty("line.separator");
/*  327: 446 */       String m = format.format(new Date()) + " " + level + ": " + message + linefeed;
/*  328:     */       
/*  329: 448 */       writer.write(m);
/*  330: 449 */       writer.flush();
/*  331: 450 */       writer.close();
/*  332:     */     }
/*  333:     */     catch (Exception ex) {}
/*  334:     */   }
/*  335:     */   
/*  336:     */   public static void removeExplorerProps(String installedPackageName)
/*  337:     */   {
/*  338:     */     try
/*  339:     */     {
/*  340: 463 */       Properties expProps = new Properties();
/*  341: 464 */       String explorerProps = getPackageHome().getAbsolutePath() + File.separator + installedPackageName + File.separator + "Explorer.props";
/*  342:     */       
/*  343: 466 */       BufferedInputStream bi = new BufferedInputStream(new FileInputStream(explorerProps));
/*  344:     */       
/*  345: 468 */       expProps.load(bi);
/*  346: 469 */       bi.close();
/*  347: 470 */       bi = null;
/*  348: 471 */       Set<Object> keys = expProps.keySet();
/*  349: 472 */       Iterator<Object> keysI = keys.iterator();
/*  350: 473 */       while (keysI.hasNext())
/*  351:     */       {
/*  352: 474 */         String key = (String)keysI.next();
/*  353: 475 */         if (!key.endsWith("Policy"))
/*  354:     */         {
/*  355: 477 */           String existingVal = ExplorerDefaults.get(key, "");
/*  356: 478 */           String toRemove = expProps.getProperty(key);
/*  357: 479 */           if (existingVal.length() > 0)
/*  358:     */           {
/*  359: 482 */             existingVal = existingVal.replace(toRemove + ",", "");
/*  360:     */             
/*  361:     */ 
/*  362: 485 */             existingVal = existingVal.replace("," + toRemove, "");
/*  363: 486 */             ExplorerDefaults.set(key, existingVal);
/*  364:     */           }
/*  365:     */         }
/*  366:     */       }
/*  367:     */     }
/*  368:     */     catch (Exception ex) {}
/*  369:     */   }
/*  370:     */   
/*  371:     */   protected static void processGenericPropertiesCreatorProps(File propsFile)
/*  372:     */   {
/*  373:     */     try
/*  374:     */     {
/*  375: 501 */       Properties expProps = new Properties();
/*  376: 502 */       BufferedInputStream bi = new BufferedInputStream(new FileInputStream(propsFile));
/*  377:     */       
/*  378: 504 */       expProps.load(bi);
/*  379: 505 */       bi.close();
/*  380: 506 */       bi = null;
/*  381: 507 */       Properties GPCInputProps = GenericPropertiesCreator.getGlobalInputProperties();
/*  382:     */       
/*  383:     */ 
/*  384: 510 */       Set<Object> keys = expProps.keySet();
/*  385: 511 */       Iterator<Object> keysI = keys.iterator();
/*  386: 512 */       while (keysI.hasNext())
/*  387:     */       {
/*  388: 513 */         String key = (String)keysI.next();
/*  389:     */         
/*  390: 515 */         String existingVal = GPCInputProps.getProperty(key, "");
/*  391: 516 */         if (existingVal.length() > 0)
/*  392:     */         {
/*  393: 518 */           String newVal = expProps.getProperty(key);
/*  394: 520 */           if (existingVal.indexOf(newVal) < 0)
/*  395:     */           {
/*  396: 521 */             newVal = existingVal + "," + newVal;
/*  397: 522 */             GPCInputProps.put(key, newVal);
/*  398:     */           }
/*  399:     */         }
/*  400:     */         else
/*  401:     */         {
/*  402: 526 */           String newVal = expProps.getProperty(key);
/*  403: 527 */           GPCInputProps.put(key, newVal);
/*  404:     */         }
/*  405:     */       }
/*  406:     */     }
/*  407:     */     catch (Exception ex) {}
/*  408:     */   }
/*  409:     */   
/*  410:     */   protected static void processExplorerProps(File propsFile)
/*  411:     */   {
/*  412:     */     try
/*  413:     */     {
/*  414: 542 */       Properties expProps = new Properties();
/*  415: 543 */       BufferedInputStream bi = new BufferedInputStream(new FileInputStream(propsFile));
/*  416:     */       
/*  417: 545 */       expProps.load(bi);
/*  418: 546 */       bi.close();
/*  419: 547 */       bi = null;
/*  420: 548 */       Set<Object> keys = expProps.keySet();
/*  421: 549 */       Iterator<Object> keysI = keys.iterator();
/*  422: 550 */       while (keysI.hasNext())
/*  423:     */       {
/*  424: 551 */         String key = (String)keysI.next();
/*  425: 552 */         if (!key.endsWith("Policy"))
/*  426:     */         {
/*  427: 554 */           String existingVal = ExplorerDefaults.get(key, "");
/*  428: 555 */           if (existingVal.length() > 0)
/*  429:     */           {
/*  430: 557 */             String replacePolicy = expProps.getProperty(key + "Policy");
/*  431: 558 */             if ((replacePolicy != null) && (replacePolicy.length() > 0))
/*  432:     */             {
/*  433: 559 */               if (replacePolicy.equalsIgnoreCase("replace"))
/*  434:     */               {
/*  435: 560 */                 String newVal = expProps.getProperty(key);
/*  436: 561 */                 ExplorerDefaults.set(key, newVal);
/*  437:     */               }
/*  438:     */               else
/*  439:     */               {
/*  440: 564 */                 String newVal = expProps.getProperty(key);
/*  441: 567 */                 if (existingVal.indexOf(newVal) < 0)
/*  442:     */                 {
/*  443: 568 */                   newVal = existingVal + "," + newVal;
/*  444: 569 */                   ExplorerDefaults.set(key, newVal);
/*  445:     */                 }
/*  446:     */               }
/*  447:     */             }
/*  448:     */             else
/*  449:     */             {
/*  450: 574 */               String newVal = expProps.getProperty(key);
/*  451: 576 */               if (existingVal.indexOf(newVal) < 0)
/*  452:     */               {
/*  453: 577 */                 newVal = existingVal + "," + newVal;
/*  454: 578 */                 ExplorerDefaults.set(key, newVal);
/*  455:     */               }
/*  456:     */             }
/*  457:     */           }
/*  458:     */           else
/*  459:     */           {
/*  460: 583 */             String newVal = expProps.getProperty(key);
/*  461: 584 */             ExplorerDefaults.set(key, newVal);
/*  462:     */           }
/*  463:     */         }
/*  464:     */       }
/*  465:     */     }
/*  466:     */     catch (Exception ex) {}
/*  467:     */   }
/*  468:     */   
/*  469:     */   protected static void processGUIEditorsProps(File propsFile, boolean verbose)
/*  470:     */   {
/*  471:     */     
/*  472:     */     try
/*  473:     */     {
/*  474: 603 */       Properties editorProps = new Properties();
/*  475: 604 */       BufferedInputStream bi = new BufferedInputStream(new FileInputStream(propsFile));
/*  476:     */       
/*  477: 606 */       editorProps.load(bi);
/*  478: 607 */       bi.close();
/*  479: 608 */       bi = null;
/*  480:     */       
/*  481: 610 */       Enumeration<?> enm = editorProps.propertyNames();
/*  482: 611 */       while (enm.hasMoreElements())
/*  483:     */       {
/*  484: 612 */         String name = enm.nextElement().toString();
/*  485: 613 */         String value = editorProps.getProperty(name, "");
/*  486: 614 */         if (verbose) {
/*  487: 615 */           System.err.println("Registering " + name + " " + value);
/*  488:     */         }
/*  489: 617 */         GenericObjectEditor.registerEditor(name, value);
/*  490:     */       }
/*  491:     */     }
/*  492:     */     catch (Exception ex) {}
/*  493:     */   }
/*  494:     */   
/*  495:     */   protected static void processPluginManagerProps(File propsFile)
/*  496:     */   {
/*  497:     */     try
/*  498:     */     {
/*  499: 632 */       PluginManager.addFromProperties(propsFile);
/*  500:     */     }
/*  501:     */     catch (Exception ex)
/*  502:     */     {
/*  503: 634 */       ex.printStackTrace();
/*  504:     */     }
/*  505:     */   }
/*  506:     */   
/*  507:     */   protected static void loadPackageDirectory(File directory, boolean verbose, List<File> goePropsFiles, boolean avoidTriggeringFullClassDiscovery)
/*  508:     */     throws Exception
/*  509:     */   {
/*  510: 650 */     File[] contents = directory.listFiles();
/*  511: 653 */     for (File content : contents) {
/*  512: 654 */       if ((content.isFile()) && (content.getPath().endsWith(".jar")))
/*  513:     */       {
/*  514: 655 */         if (verbose) {
/*  515: 656 */           System.out.println("[Weka] loading " + content.getPath());
/*  516:     */         }
/*  517: 658 */         ClassloaderUtil.addFile(content.getPath());
/*  518:     */       }
/*  519: 659 */       else if ((content.isDirectory()) && (content.getName().equalsIgnoreCase("lib")))
/*  520:     */       {
/*  521: 662 */         loadPackageDirectory(content, verbose, goePropsFiles, avoidTriggeringFullClassDiscovery);
/*  522:     */       }
/*  523:     */     }
/*  524: 668 */     for (File content : contents) {
/*  525: 669 */       if ((content.isFile()) && (content.getPath().endsWith("Beans.props")))
/*  526:     */       {
/*  527: 673 */         BeansProperties.addToPluginBeanProps(content);
/*  528: 675 */         if (!avoidTriggeringFullClassDiscovery) {
/*  529: 676 */           KnowledgeFlowApp.disposeSingleton();
/*  530:     */         }
/*  531:     */       }
/*  532: 679 */       else if ((content.isFile()) && (content.getPath().endsWith("Explorer.props")) && (!avoidTriggeringFullClassDiscovery))
/*  533:     */       {
/*  534: 683 */         processExplorerProps(content);
/*  535:     */       }
/*  536: 685 */       else if ((content.isFile()) && (content.getPath().endsWith("GUIEditors.props")) && (!avoidTriggeringFullClassDiscovery))
/*  537:     */       {
/*  538: 688 */         processGUIEditorsProps(content, verbose);
/*  539:     */       }
/*  540: 689 */       else if ((content.isFile()) && (content.getPath().endsWith("GenericPropertiesCreator.props")) && (!avoidTriggeringFullClassDiscovery))
/*  541:     */       {
/*  542: 692 */         if (goePropsFiles != null) {
/*  543: 693 */           goePropsFiles.add(content);
/*  544:     */         } else {
/*  545: 695 */           processGenericPropertiesCreatorProps(content);
/*  546:     */         }
/*  547:     */       }
/*  548: 697 */       else if ((content.isFile()) && (content.getPath().endsWith("PluginManager.props")))
/*  549:     */       {
/*  550: 699 */         processPluginManagerProps(content);
/*  551:     */       }
/*  552:     */     }
/*  553:     */   }
/*  554:     */   
/*  555:     */   public static boolean loadCheck(Package toLoad, File packageRoot, PrintStream... progress)
/*  556:     */   {
/*  557:     */     try
/*  558:     */     {
/*  559: 719 */       load = toLoad.isCompatibleBaseSystem();
/*  560:     */     }
/*  561:     */     catch (Exception ex)
/*  562:     */     {
/*  563: 721 */       ex.printStackTrace();
/*  564: 722 */       return false;
/*  565:     */     }
/*  566: 725 */     if (!load)
/*  567:     */     {
/*  568: 726 */       for (PrintStream p : progress) {
/*  569: 727 */         p.println("[Weka] Skipping package " + toLoad.getName() + " because it is not compatible with Weka " + PACKAGE_MANAGER.getBaseSystemVersion().toString());
/*  570:     */       }
/*  571: 731 */       return false;
/*  572:     */     }
/*  573:     */     try
/*  574:     */     {
/*  575: 736 */       Package repoP = getRepositoryPackageInfo(toLoad.getName(), toLoad.getPackageMetaDataElement("Version").toString());
/*  576: 738 */       if (repoP != null)
/*  577:     */       {
/*  578: 739 */         Object disabled = repoP.getPackageMetaDataElement("Disabled");
/*  579: 740 */         if (disabled == null) {
/*  580: 741 */           disabled = repoP.getPackageMetaDataElement("Disable");
/*  581:     */         }
/*  582: 743 */         if ((disabled != null) && (disabled.toString().equalsIgnoreCase("true")))
/*  583:     */         {
/*  584: 744 */           for (PrintStream p : progress) {
/*  585: 745 */             p.println("[Weka] Skipping package " + toLoad.getName() + " because it has been marked as disabled at the repository");
/*  586:     */           }
/*  587: 748 */           return false;
/*  588:     */         }
/*  589:     */       }
/*  590:     */     }
/*  591:     */     catch (Exception ex)
/*  592:     */     {
/*  593: 754 */       return true;
/*  594:     */     }
/*  595: 757 */     boolean load = !m_doNotLoadList.contains(toLoad.getName());
/*  596: 758 */     if (!load)
/*  597:     */     {
/*  598: 759 */       for (PrintStream p : progress) {
/*  599: 760 */         p.println("[Weka] Skipping package " + toLoad.getName() + " because it is has been marked as do not load");
/*  600:     */       }
/*  601: 763 */       return false;
/*  602:     */     }
/*  603: 769 */     if ((!checkForMissingClasses(toLoad, progress)) || (!checkForMissingFiles(toLoad, packageRoot, progress))) {
/*  604: 771 */       return false;
/*  605:     */     }
/*  606: 775 */     if (!checkForUnsetEnvVar(toLoad, progress)) {
/*  607: 776 */       return false;
/*  608:     */     }
/*  609: 779 */     if (m_offline) {
/*  610: 780 */       return true;
/*  611:     */     }
/*  612:     */     try
/*  613:     */     {
/*  614: 785 */       List<Dependency> missing = toLoad.getMissingDependencies();
/*  615: 786 */       if (missing.size() > 0)
/*  616:     */       {
/*  617:     */         PrintStream p;
/*  618: 787 */         for (p : progress)
/*  619:     */         {
/*  620: 788 */           p.println("[Weka] " + toLoad.getName() + " can't be loaded because the following" + " packages are missing:");
/*  621: 791 */           for (Dependency d : missing) {
/*  622: 792 */             p.println(d.getTarget());
/*  623:     */           }
/*  624:     */         }
/*  625: 795 */         return false;
/*  626:     */       }
/*  627:     */     }
/*  628:     */     catch (Exception ex)
/*  629:     */     {
/*  630: 798 */       ex.printStackTrace();
/*  631: 799 */       return false;
/*  632:     */     }
/*  633:     */     try
/*  634:     */     {
/*  635: 804 */       List<Dependency> depends = toLoad.getDependencies();
/*  636: 805 */       for (Dependency d : depends) {
/*  637: 806 */         if (d.getTarget().getPackage().isInstalled())
/*  638:     */         {
/*  639: 807 */           if (!loadCheck(d.getTarget().getPackage(), packageRoot, progress))
/*  640:     */           {
/*  641: 808 */             for (PrintStream p : progress) {
/*  642: 809 */               p.println("[Weka] Can't load " + toLoad.getName() + " because " + d.getTarget() + " can't be loaded.");
/*  643:     */             }
/*  644: 812 */             return false;
/*  645:     */           }
/*  646: 816 */           Package installedD = getInstalledPackageInfo(d.getTarget().getPackage().getName());
/*  647: 818 */           if (!d.getTarget().checkConstraint(installedD))
/*  648:     */           {
/*  649: 819 */             for (PrintStream p : progress) {
/*  650: 820 */               p.println("[Weka] Can't load " + toLoad.getName() + " because the installed " + d.getTarget().getPackage().getName() + " is not compatible (requires: " + d.getTarget() + ")");
/*  651:     */             }
/*  652: 825 */             return false;
/*  653:     */           }
/*  654:     */         }
/*  655:     */       }
/*  656:     */     }
/*  657:     */     catch (Exception ex)
/*  658:     */     {
/*  659: 830 */       ex.printStackTrace();
/*  660: 831 */       return false;
/*  661:     */     }
/*  662: 834 */     return true;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public static boolean checkForUnsetEnvVar(Package toLoad, PrintStream... progress)
/*  666:     */   {
/*  667: 848 */     Object doNotLoadIfUnsetVar = toLoad.getPackageMetaDataElement("DoNotLoadIfEnvVarNotSet");
/*  668:     */     
/*  669:     */ 
/*  670: 851 */     boolean result = true;
/*  671: 852 */     if ((doNotLoadIfUnsetVar != null) && (doNotLoadIfUnsetVar.toString().length() > 0))
/*  672:     */     {
/*  673: 854 */       String[] elements = doNotLoadIfUnsetVar.toString().split(",");
/*  674:     */       
/*  675: 856 */       Environment env = Environment.getSystemWide();
/*  676: 858 */       for (String var : elements) {
/*  677: 859 */         if (env.getVariableValue(var.trim()) == null)
/*  678:     */         {
/*  679: 860 */           for (PrintStream p : progress) {
/*  680: 861 */             p.println("[Weka] " + toLoad.getName() + " can't be loaded because " + "the environment variable " + var + " is not set.");
/*  681:     */           }
/*  682: 865 */           result = false;
/*  683: 866 */           break;
/*  684:     */         }
/*  685:     */       }
/*  686:     */     }
/*  687: 871 */     if (!result)
/*  688:     */     {
/*  689: 873 */       Object doNotLoadMessage = toLoad.getPackageMetaDataElement("DoNotLoadIfEnvVarNotSetMessage");
/*  690: 875 */       if ((doNotLoadMessage != null) && (doNotLoadMessage.toString().length() > 0)) {
/*  691: 877 */         for (PrintStream p : progress)
/*  692:     */         {
/*  693: 878 */           String dnlM = doNotLoadMessage.toString();
/*  694:     */           try
/*  695:     */           {
/*  696: 880 */             dnlM = Environment.getSystemWide().substitute(dnlM);
/*  697:     */           }
/*  698:     */           catch (Exception e) {}
/*  699: 884 */           p.println("[Weka] " + dnlM);
/*  700:     */         }
/*  701:     */       }
/*  702:     */     }
/*  703: 889 */     return result;
/*  704:     */   }
/*  705:     */   
/*  706:     */   public static boolean checkForMissingClasses(Package toLoad, PrintStream... progress)
/*  707:     */   {
/*  708: 903 */     boolean result = true;
/*  709: 904 */     Object doNotLoadIfClassNotInstantiable = toLoad.getPackageMetaDataElement("DoNotLoadIfClassNotPresent");
/*  710: 907 */     if ((doNotLoadIfClassNotInstantiable != null) && (doNotLoadIfClassNotInstantiable.toString().length() > 0))
/*  711:     */     {
/*  712: 910 */       StringTokenizer tok = new StringTokenizer(doNotLoadIfClassNotInstantiable.toString(), ",");
/*  713: 912 */       while (tok.hasMoreTokens())
/*  714:     */       {
/*  715: 913 */         String nextT = tok.nextToken().trim();
/*  716:     */         try
/*  717:     */         {
/*  718: 915 */           Class.forName(nextT);
/*  719:     */         }
/*  720:     */         catch (Exception ex)
/*  721:     */         {
/*  722: 917 */           for (PrintStream p : progress) {
/*  723: 918 */             p.println("[Weka] " + toLoad.getName() + " can't be loaded because " + nextT + " can't be instantiated.");
/*  724:     */           }
/*  725: 921 */           result = false;
/*  726: 922 */           break;
/*  727:     */         }
/*  728:     */       }
/*  729:     */     }
/*  730: 927 */     if (!result)
/*  731:     */     {
/*  732: 929 */       Object doNotLoadMessage = toLoad.getPackageMetaDataElement("DoNotLoadIfClassNotPresentMessage");
/*  733: 931 */       if ((doNotLoadMessage != null) && (doNotLoadMessage.toString().length() > 0)) {
/*  734: 933 */         for (PrintStream p : progress)
/*  735:     */         {
/*  736: 934 */           String dnlM = doNotLoadMessage.toString();
/*  737:     */           try
/*  738:     */           {
/*  739: 936 */             dnlM = Environment.getSystemWide().substitute(dnlM);
/*  740:     */           }
/*  741:     */           catch (Exception e) {}
/*  742: 940 */           p.println("[Weka] " + dnlM);
/*  743:     */         }
/*  744:     */       }
/*  745:     */     }
/*  746: 945 */     return result;
/*  747:     */   }
/*  748:     */   
/*  749:     */   public static boolean checkForMissingFiles(Package toLoad, File packageRoot, PrintStream... progress)
/*  750:     */   {
/*  751: 961 */     boolean result = true;
/*  752:     */     
/*  753: 963 */     Object doNotLoadIfFileMissing = toLoad.getPackageMetaDataElement("DoNotLoadIfFileNotPresent");
/*  754:     */     
/*  755: 965 */     String packageRootPath = packageRoot.getPath() + File.separator;
/*  756: 967 */     if ((doNotLoadIfFileMissing != null) && (doNotLoadIfFileMissing.toString().length() > 0))
/*  757:     */     {
/*  758: 970 */       StringTokenizer tok = new StringTokenizer(doNotLoadIfFileMissing.toString(), ",");
/*  759: 972 */       while (tok.hasMoreTokens())
/*  760:     */       {
/*  761: 973 */         String nextT = tok.nextToken().trim();
/*  762: 974 */         File toCheck = new File(packageRootPath + nextT);
/*  763: 975 */         if (!toCheck.exists())
/*  764:     */         {
/*  765: 976 */           for (PrintStream p : progress) {
/*  766: 977 */             p.println("[Weka] " + toLoad.getName() + " can't be loaded because " + toCheck.getPath() + " appears to be missing.");
/*  767:     */           }
/*  768: 980 */           result = false;
/*  769: 981 */           break;
/*  770:     */         }
/*  771:     */       }
/*  772:     */     }
/*  773: 986 */     if (!result)
/*  774:     */     {
/*  775: 988 */       Object doNotLoadMessage = toLoad.getPackageMetaDataElement("DoNotLoadIfFileNotPresentMessage");
/*  776: 990 */       if ((doNotLoadMessage != null) && (doNotLoadMessage.toString().length() > 0))
/*  777:     */       {
/*  778: 992 */         String dnlM = doNotLoadMessage.toString();
/*  779:     */         try
/*  780:     */         {
/*  781: 994 */           dnlM = Environment.getSystemWide().substitute(dnlM);
/*  782:     */         }
/*  783:     */         catch (Exception ex) {}
/*  784: 998 */         for (PrintStream p : progress) {
/*  785: 999 */           p.println("[Weka] " + dnlM);
/*  786:     */         }
/*  787:     */       }
/*  788:     */     }
/*  789:1004 */     return result;
/*  790:     */   }
/*  791:     */   
/*  792:     */   protected static Set<String> getDoNotLoadList()
/*  793:     */   {
/*  794:1016 */     doNotLoad = new HashSet();
/*  795:1017 */     File doNotLoadList = new File(PACKAGES_DIR.toString() + File.separator + "doNotLoad.ser");
/*  796:1019 */     if ((doNotLoadList.exists()) && (doNotLoadList.isFile()))
/*  797:     */     {
/*  798:1020 */       ObjectInputStream ois = null;
/*  799:     */       try
/*  800:     */       {
/*  801:1023 */         ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(doNotLoadList)));
/*  802:     */         
/*  803:1025 */         return (Set)ois.readObject();
/*  804:     */       }
/*  805:     */       catch (FileNotFoundException ex) {}catch (IOException e)
/*  806:     */       {
/*  807:1028 */         System.err.println("An error occurred while reading the doNotLoad list: " + e.getMessage());
/*  808:     */       }
/*  809:     */       catch (ClassNotFoundException e)
/*  810:     */       {
/*  811:1032 */         System.err.println("An error occurred while reading the doNotLoad list: " + e.getMessage());
/*  812:     */       }
/*  813:     */       finally
/*  814:     */       {
/*  815:1036 */         if (ois != null) {
/*  816:     */           try
/*  817:     */           {
/*  818:1038 */             ois.close();
/*  819:     */           }
/*  820:     */           catch (IOException ex)
/*  821:     */           {
/*  822:1040 */             ex.printStackTrace();
/*  823:     */           }
/*  824:     */         }
/*  825:     */       }
/*  826:     */     }
/*  827:     */   }
/*  828:     */   
/*  829:     */   public static List<String> toggleLoadStatus(List<String> packageNames)
/*  830:     */     throws Exception
/*  831:     */   {
/*  832:1060 */     List<String> unknownPackages = new ArrayList();
/*  833:1061 */     boolean changeMade = false;
/*  834:1062 */     for (String s : packageNames)
/*  835:     */     {
/*  836:1063 */       Package p = PACKAGE_MANAGER.getInstalledPackageInfo(s);
/*  837:1064 */       if (p == null)
/*  838:     */       {
/*  839:1065 */         unknownPackages.add(s);
/*  840:     */       }
/*  841:     */       else
/*  842:     */       {
/*  843:1067 */         if (m_doNotLoadList.contains(s)) {
/*  844:1068 */           m_doNotLoadList.remove(s);
/*  845:1071 */         } else if (loadCheck(p, new File(getPackageHome().toString() + File.separator + s), new PrintStream[0])) {
/*  846:1074 */           m_doNotLoadList.add(s);
/*  847:     */         }
/*  848:1077 */         changeMade = true;
/*  849:     */       }
/*  850:     */     }
/*  851:1081 */     if (changeMade)
/*  852:     */     {
/*  853:1083 */       File doNotLoadList = new File(PACKAGES_DIR.toString() + File.separator + "doNotLoad.ser");
/*  854:     */       
/*  855:1085 */       ObjectOutputStream oos = null;
/*  856:     */       try
/*  857:     */       {
/*  858:1087 */         oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(doNotLoadList)));
/*  859:     */         
/*  860:1089 */         oos.writeObject(m_doNotLoadList);
/*  861:     */       }
/*  862:     */       finally
/*  863:     */       {
/*  864:1091 */         if (oos != null)
/*  865:     */         {
/*  866:1092 */           oos.flush();
/*  867:1093 */           oos.close();
/*  868:     */         }
/*  869:     */       }
/*  870:     */     }
/*  871:1098 */     return unknownPackages;
/*  872:     */   }
/*  873:     */   
/*  874:     */   public static synchronized void loadPackages(boolean verbose)
/*  875:     */   {
/*  876:1107 */     loadPackages(verbose, false, true);
/*  877:     */   }
/*  878:     */   
/*  879:     */   public static synchronized void loadPackages(boolean verbose, boolean avoidTriggeringFullClassDiscovery, boolean refreshGOEProperties)
/*  880:     */   {
/*  881:1124 */     List<File> goePropsFiles = new ArrayList();
/*  882:1125 */     if (!m_loadPackages) {
/*  883:1126 */       return;
/*  884:     */     }
/*  885:1129 */     if (m_packagesLoaded) {
/*  886:1130 */       return;
/*  887:     */     }
/*  888:1133 */     m_packagesLoaded = true;
/*  889:1134 */     m_initialPackageLoadingInProcess = true;
/*  890:1135 */     if (establishWekaHome())
/*  891:     */     {
/*  892:1138 */       File[] contents = PACKAGES_DIR.listFiles();
/*  893:1142 */       if (contents.length > 0) {
/*  894:1143 */         establishCacheIfNeeded(new PrintStream[] { System.out });
/*  895:     */       }
/*  896:1146 */       for (File content : contents) {
/*  897:1147 */         if (content.isDirectory()) {
/*  898:     */           try
/*  899:     */           {
/*  900:1149 */             Package toLoad = getInstalledPackageInfo(content.getName());
/*  901:1154 */             if (toLoad != null)
/*  902:     */             {
/*  903:1156 */               boolean load = loadCheck(toLoad, content, new PrintStream[] { System.err });
/*  904:1158 */               if (load)
/*  905:     */               {
/*  906:1159 */                 if (verbose) {
/*  907:1160 */                   System.out.println("[Weka] loading package " + content.getName());
/*  908:     */                 }
/*  909:1163 */                 loadPackageDirectory(content, verbose, goePropsFiles, avoidTriggeringFullClassDiscovery);
/*  910:     */               }
/*  911:     */             }
/*  912:     */           }
/*  913:     */           catch (Exception ex)
/*  914:     */           {
/*  915:1168 */             ex.printStackTrace();
/*  916:1169 */             System.err.println("[Weka] Problem loading package " + content.getName() + " skipping...");
/*  917:     */           }
/*  918:     */         }
/*  919:     */       }
/*  920:     */     }
/*  921:1175 */     m_initialPackageLoadingInProcess = false;
/*  922:1183 */     if (!avoidTriggeringFullClassDiscovery) {
/*  923:1184 */       for (File f : goePropsFiles) {
/*  924:1185 */         processGenericPropertiesCreatorProps(f);
/*  925:     */       }
/*  926:     */     }
/*  927:1192 */     if (refreshGOEProperties)
/*  928:     */     {
/*  929:1193 */       if (verbose) {
/*  930:1194 */         System.err.println("Refreshing GOE props...");
/*  931:     */       }
/*  932:1196 */       refreshGOEProperties();
/*  933:     */     }
/*  934:     */   }
/*  935:     */   
/*  936:     */   public static void refreshGOEProperties()
/*  937:     */   {
/*  938:1205 */     ClassDiscovery.clearClassCache();
/*  939:1206 */     GenericPropertiesCreator.regenerateGlobalOutputProperties();
/*  940:1207 */     GenericObjectEditor.determineClasses();
/*  941:1208 */     ConverterUtils.initialize();
/*  942:1209 */     KnowledgeFlowApp.disposeSingleton();
/*  943:1210 */     KnowledgeFlowApp.reInitialize();
/*  944:     */   }
/*  945:     */   
/*  946:     */   public static PackageManager getUnderlyingPackageManager()
/*  947:     */   {
/*  948:1219 */     return PACKAGE_MANAGER;
/*  949:     */   }
/*  950:     */   
/*  951:     */   public static int repoZipArchiveSize()
/*  952:     */   {
/*  953:1229 */     int size = -1;
/*  954:     */     try
/*  955:     */     {
/*  956:1232 */       PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/*  957:1233 */       String numPackagesS = PACKAGE_MANAGER.getPackageRepositoryURL().toString() + "/repoSize.txt";
/*  958:     */       
/*  959:1235 */       URLConnection conn = null;
/*  960:1236 */       URL connURL = new URL(numPackagesS);
/*  961:1238 */       if (PACKAGE_MANAGER.setProxyAuthentication(connURL)) {
/*  962:1239 */         conn = connURL.openConnection(PACKAGE_MANAGER.getProxy());
/*  963:     */       } else {
/*  964:1241 */         conn = connURL.openConnection();
/*  965:     */       }
/*  966:1244 */       conn.setConnectTimeout(30000);
/*  967:     */       
/*  968:1246 */       BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/*  969:     */       
/*  970:     */ 
/*  971:1249 */       String n = bi.readLine();
/*  972:     */       try
/*  973:     */       {
/*  974:1251 */         size = Integer.parseInt(n);
/*  975:     */       }
/*  976:     */       catch (NumberFormatException ne)
/*  977:     */       {
/*  978:1253 */         System.err.println("[WekaPackageManager] problem parsing the size of repository zip archive from the server.");
/*  979:     */       }
/*  980:1256 */       bi.close();
/*  981:     */     }
/*  982:     */     catch (Exception ex)
/*  983:     */     {
/*  984:1259 */       ex.printStackTrace();
/*  985:     */     }
/*  986:1262 */     return size;
/*  987:     */   }
/*  988:     */   
/*  989:     */   public static int numRepositoryPackages()
/*  990:     */   {
/*  991:1273 */     if (m_offline) {
/*  992:1274 */       return -1;
/*  993:     */     }
/*  994:1276 */     int numPackages = -1;
/*  995:     */     try
/*  996:     */     {
/*  997:1278 */       PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/*  998:1279 */       String numPackagesS = PACKAGE_MANAGER.getPackageRepositoryURL().toString() + "/numPackages.txt";
/*  999:     */       
/* 1000:1281 */       URLConnection conn = null;
/* 1001:1282 */       URL connURL = new URL(numPackagesS);
/* 1002:1284 */       if (PACKAGE_MANAGER.setProxyAuthentication(connURL)) {
/* 1003:1285 */         conn = connURL.openConnection(PACKAGE_MANAGER.getProxy());
/* 1004:     */       } else {
/* 1005:1287 */         conn = connURL.openConnection();
/* 1006:     */       }
/* 1007:1290 */       conn.setConnectTimeout(30000);
/* 1008:     */       
/* 1009:1292 */       BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 1010:     */       
/* 1011:     */ 
/* 1012:1295 */       String n = bi.readLine();
/* 1013:     */       try
/* 1014:     */       {
/* 1015:1297 */         numPackages = Integer.parseInt(n);
/* 1016:     */       }
/* 1017:     */       catch (NumberFormatException ne)
/* 1018:     */       {
/* 1019:1299 */         System.err.println("[WekaPackageManager] problem parsing number of packages from server.");
/* 1020:     */       }
/* 1021:1302 */       bi.close();
/* 1022:     */     }
/* 1023:     */     catch (Exception ex)
/* 1024:     */     {
/* 1025:1305 */       ex.printStackTrace();
/* 1026:     */     }
/* 1027:1308 */     return numPackages;
/* 1028:     */   }
/* 1029:     */   
/* 1030:     */   public static Map<String, String> getPackageList(boolean local)
/* 1031:     */   {
/* 1032:1323 */     Map<String, String> result = new HashMap();
/* 1033:     */     try
/* 1034:     */     {
/* 1035:1326 */       useCacheOrOnlineRepository();
/* 1036:1328 */       if (!local) {
/* 1037:1329 */         PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/* 1038:     */       }
/* 1039:1332 */       String packageListS = PACKAGE_MANAGER.getPackageRepositoryURL().toString() + "/" + "packageListWithVersion.txt";
/* 1040:     */       
/* 1041:1334 */       URLConnection conn = null;
/* 1042:1335 */       URL connURL = new URL(packageListS);
/* 1043:1337 */       if (PACKAGE_MANAGER.setProxyAuthentication(connURL)) {
/* 1044:1338 */         conn = connURL.openConnection(PACKAGE_MANAGER.getProxy());
/* 1045:     */       } else {
/* 1046:1340 */         conn = connURL.openConnection();
/* 1047:     */       }
/* 1048:1343 */       conn.setConnectTimeout(30000);
/* 1049:     */       
/* 1050:1345 */       BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 1051:     */       
/* 1052:1347 */       String l = null;
/* 1053:1348 */       while ((l = bi.readLine()) != null)
/* 1054:     */       {
/* 1055:1349 */         String[] parts = l.split(":");
/* 1056:1350 */         if (parts.length == 2) {
/* 1057:1351 */           result.put(parts[0], parts[1]);
/* 1058:     */         }
/* 1059:     */       }
/* 1060:1354 */       bi.close();
/* 1061:     */     }
/* 1062:     */     catch (Exception ex)
/* 1063:     */     {
/* 1064:1358 */       result = null;
/* 1065:     */     }
/* 1066:1361 */     return result;
/* 1067:     */   }
/* 1068:     */   
/* 1069:     */   public static Exception establishCacheIfNeeded(PrintStream... progress)
/* 1070:     */   {
/* 1071:1371 */     if (m_offline) {
/* 1072:1372 */       return null;
/* 1073:     */     }
/* 1074:1375 */     if (REP_MIRROR == null) {
/* 1075:1376 */       establishMirror();
/* 1076:     */     }
/* 1077:1379 */     Exception problem = null;
/* 1078:1380 */     if (INITIAL_CACHE_BUILD_NEEDED)
/* 1079:     */     {
/* 1080:1381 */       for (PrintStream p : progress) {
/* 1081:1382 */         p.println("Caching repository metadata, please wait...");
/* 1082:     */       }
/* 1083:1385 */       problem = refreshCache(progress);
/* 1084:     */       
/* 1085:1387 */       INITIAL_CACHE_BUILD_NEEDED = false;
/* 1086:     */     }
/* 1087:     */     else
/* 1088:     */     {
/* 1089:     */       try
/* 1090:     */       {
/* 1091:1392 */         if (checkForForcedCacheRefresh())
/* 1092:     */         {
/* 1093:1393 */           for (PrintStream p : progress) {
/* 1094:1394 */             p.println("Forced repository metadata refresh, please wait...");
/* 1095:     */           }
/* 1096:1396 */           problem = refreshCache(progress);
/* 1097:     */         }
/* 1098:     */       }
/* 1099:     */       catch (MalformedURLException ex)
/* 1100:     */       {
/* 1101:1399 */         problem = ex;
/* 1102:     */       }
/* 1103:     */     }
/* 1104:1403 */     return problem;
/* 1105:     */   }
/* 1106:     */   
/* 1107:     */   protected static boolean checkForForcedCacheRefresh()
/* 1108:     */     throws MalformedURLException
/* 1109:     */   {
/* 1110:1409 */     int refreshCountServer = getForcedRefreshCount(false);
/* 1111:1410 */     if (refreshCountServer > 0)
/* 1112:     */     {
/* 1113:1412 */       int refreshCountLocal = getForcedRefreshCount(true);
/* 1114:1413 */       return refreshCountServer > refreshCountLocal;
/* 1115:     */     }
/* 1116:1416 */     return false;
/* 1117:     */   }
/* 1118:     */   
/* 1119:     */   protected static int getForcedRefreshCount(boolean local)
/* 1120:     */     throws MalformedURLException
/* 1121:     */   {
/* 1122:     */     
/* 1123:1423 */     if (!local) {
/* 1124:1424 */       PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/* 1125:     */     }
/* 1126:1426 */     String refreshCountS = PACKAGE_MANAGER.getPackageRepositoryURL().toString() + "/" + "forcedRefreshCount.txt";
/* 1127:     */     
/* 1128:1428 */     int refreshCount = -1;
/* 1129:1429 */     URLConnection conn = null;
/* 1130:1430 */     URL connURL = new URL(refreshCountS);
/* 1131:     */     try
/* 1132:     */     {
/* 1133:1433 */       if (PACKAGE_MANAGER.setProxyAuthentication(connURL)) {
/* 1134:1434 */         conn = connURL.openConnection(PACKAGE_MANAGER.getProxy());
/* 1135:     */       } else {
/* 1136:1436 */         conn = connURL.openConnection();
/* 1137:     */       }
/* 1138:1439 */       conn.setConnectTimeout(30000);
/* 1139:     */       
/* 1140:1441 */       BufferedReader bi = new BufferedReader(new InputStreamReader(conn.getInputStream()));
/* 1141:     */       
/* 1142:     */ 
/* 1143:1444 */       String countS = bi.readLine();
/* 1144:1445 */       if ((countS != null) && (countS.length() > 0)) {
/* 1145:     */         try
/* 1146:     */         {
/* 1147:1447 */           refreshCount = Integer.parseInt(countS);
/* 1148:     */         }
/* 1149:     */         catch (NumberFormatException ne) {}
/* 1150:     */       }
/* 1151:     */     }
/* 1152:     */     catch (IOException ex) {}
/* 1153:1456 */     return refreshCount;
/* 1154:     */   }
/* 1155:     */   
/* 1156:     */   public static Exception checkForNewPackages(PrintStream... progress)
/* 1157:     */   {
/* 1158:1467 */     if (m_offline) {
/* 1159:1468 */       return null;
/* 1160:     */     }
/* 1161:1471 */     Exception problem = null;
/* 1162:     */     
/* 1163:1473 */     Map<String, String> localPackageNameList = getPackageList(true);
/* 1164:1475 */     if (localPackageNameList == null)
/* 1165:     */     {
/* 1166:1477 */       System.err.println("Local package list is missing, trying a cache refresh to restore...");
/* 1167:     */       
/* 1168:1479 */       refreshCache(progress);
/* 1169:1480 */       localPackageNameList = getPackageList(true);
/* 1170:1481 */       if (localPackageNameList == null) {
/* 1171:1483 */         return null;
/* 1172:     */       }
/* 1173:     */     }
/* 1174:1487 */     Map<String, String> repositoryPackageNameList = getPackageList(false);
/* 1175:1489 */     if (repositoryPackageNameList == null) {
/* 1176:1491 */       return null;
/* 1177:     */     }
/* 1178:1494 */     if (repositoryPackageNameList.keySet().size() != localPackageNameList.keySet().size())
/* 1179:     */     {
/* 1180:1498 */       if (repositoryPackageNameList.keySet().size() < localPackageNameList.keySet().size()) {
/* 1181:1500 */         for (PrintStream p : progress) {
/* 1182:1501 */           p.println("Some packages no longer exist at the repository. Refreshing cache...");
/* 1183:     */         }
/* 1184:     */       } else {
/* 1185:1505 */         for (PrintStream p : progress) {
/* 1186:1506 */           p.println("There are new packages at the repository. Refreshing cache...");
/* 1187:     */         }
/* 1188:     */       }
/* 1189:1510 */       problem = refreshCache(progress);
/* 1190:     */     }
/* 1191:     */     else
/* 1192:     */     {
/* 1193:1513 */       boolean refresh = false;
/* 1194:1514 */       for (String localPackage : localPackageNameList.keySet())
/* 1195:     */       {
/* 1196:1515 */         String localVersion = (String)localPackageNameList.get(localPackage);
/* 1197:     */         
/* 1198:1517 */         String repoVersion = (String)repositoryPackageNameList.get(localPackage);
/* 1199:1518 */         if (repoVersion != null) {
/* 1200:1523 */           if (!localVersion.equals(repoVersion))
/* 1201:     */           {
/* 1202:1524 */             refresh = true;
/* 1203:1525 */             break;
/* 1204:     */           }
/* 1205:     */         }
/* 1206:     */       }
/* 1207:1529 */       if (refresh)
/* 1208:     */       {
/* 1209:1530 */         for (PrintStream p : progress) {
/* 1210:1531 */           p.println("There are newer versions of existing packages at the repository. Refreshing cache...");
/* 1211:     */         }
/* 1212:1534 */         problem = refreshCache(progress);
/* 1213:     */       }
/* 1214:     */     }
/* 1215:1538 */     return problem;
/* 1216:     */   }
/* 1217:     */   
/* 1218:     */   public static Exception refreshCache(PrintStream... progress)
/* 1219:     */   {
/* 1220:1548 */     Exception problem = null;
/* 1221:1549 */     if (CACHE_URL == null) {
/* 1222:1550 */       return null;
/* 1223:     */     }
/* 1224:1553 */     PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/* 1225:1554 */     String cacheDir = WEKA_HOME.toString() + File.separator + "repCache";
/* 1226:     */     try
/* 1227:     */     {
/* 1228:1556 */       for (PrintStream p : progress) {
/* 1229:1557 */         p.println("Refresh in progress. Please wait...");
/* 1230:     */       }
/* 1231:1559 */       byte[] zip = PACKAGE_MANAGER.getRepositoryPackageMetaDataOnlyAsZip(progress);
/* 1232:     */       
/* 1233:     */ 
/* 1234:1562 */       ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip));
/* 1235:     */       
/* 1236:1564 */       byte[] buff = new byte[1024];
/* 1237:     */       ZipEntry ze;
/* 1238:1565 */       while ((ze = zis.getNextEntry()) != null) {
/* 1239:1567 */         if (ze.isDirectory())
/* 1240:     */         {
/* 1241:1568 */           new File(cacheDir, ze.getName()).mkdir();
/* 1242:     */         }
/* 1243:     */         else
/* 1244:     */         {
/* 1245:1571 */           BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(new File(cacheDir, ze.getName())));
/* 1246:     */           for (;;)
/* 1247:     */           {
/* 1248:1574 */             int amountRead = zis.read(buff);
/* 1249:1575 */             if (amountRead == -1) {
/* 1250:     */               break;
/* 1251:     */             }
/* 1252:1579 */             bo.write(buff, 0, amountRead);
/* 1253:     */           }
/* 1254:1581 */           bo.close();
/* 1255:     */         }
/* 1256:     */       }
/* 1257:     */     }
/* 1258:     */     catch (Exception e)
/* 1259:     */     {
/* 1260:1584 */       e.printStackTrace();
/* 1261:     */       
/* 1262:     */ 
/* 1263:     */ 
/* 1264:1588 */       CACHE_URL = null;
/* 1265:     */       try
/* 1266:     */       {
/* 1267:1590 */         DefaultPackageManager.deleteDir(new File(cacheDir), new PrintStream[] { System.out });
/* 1268:     */       }
/* 1269:     */       catch (Exception e1)
/* 1270:     */       {
/* 1271:1592 */         e1.printStackTrace();
/* 1272:     */       }
/* 1273:1595 */       return e;
/* 1274:     */     }
/* 1275:1598 */     return problem;
/* 1276:     */   }
/* 1277:     */   
/* 1278:     */   public static boolean installedPackageResourceExists(String packageName, String resourceName)
/* 1279:     */   {
/* 1280:1611 */     String fullResourcePath = getPackageHome().toString() + File.separator + packageName + File.separator + resourceName;
/* 1281:     */     
/* 1282:     */ 
/* 1283:1614 */     return new File(fullResourcePath).exists();
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   private static void useCacheOrOnlineRepository()
/* 1287:     */   {
/* 1288:1622 */     if (REP_MIRROR == null) {
/* 1289:1623 */       establishMirror();
/* 1290:     */     }
/* 1291:1626 */     if (CACHE_URL != null) {
/* 1292:1627 */       PACKAGE_MANAGER.setPackageRepositoryURL(CACHE_URL);
/* 1293:1628 */     } else if (REP_URL != null) {
/* 1294:1629 */       PACKAGE_MANAGER.setPackageRepositoryURL(REP_URL);
/* 1295:     */     }
/* 1296:     */   }
/* 1297:     */   
/* 1298:     */   public static File getPackageHome()
/* 1299:     */   {
/* 1300:1634 */     return PACKAGE_MANAGER.getPackageHome();
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public static Package mostRecentVersionWithRespectToConstraint(PackageConstraint toCheck)
/* 1304:     */     throws Exception
/* 1305:     */   {
/* 1306:1648 */     Package target = toCheck.getPackage();
/* 1307:1649 */     Package result = null;
/* 1308:     */     
/* 1309:1651 */     List<Object> availableVersions = PACKAGE_MANAGER.getRepositoryPackageVersions(target.getName());
/* 1310:1656 */     for (Object version : availableVersions)
/* 1311:     */     {
/* 1312:1657 */       Package candidate = PACKAGE_MANAGER.getRepositoryPackageInfo(target.getName(), version);
/* 1313:1659 */       if ((toCheck.checkConstraint(candidate)) && (candidate.isCompatibleBaseSystem()))
/* 1314:     */       {
/* 1315:1661 */         result = candidate;
/* 1316:1662 */         break;
/* 1317:     */       }
/* 1318:     */     }
/* 1319:1666 */     if (result == null) {
/* 1320:1667 */       throw new Exception("[WekaPackageManager] unable to find a version of package " + target.getName() + " that meets constraint " + toCheck.toString());
/* 1321:     */     }
/* 1322:1672 */     return result;
/* 1323:     */   }
/* 1324:     */   
/* 1325:     */   public static boolean installPackages(List<Package> toInstall, PrintStream... progress)
/* 1326:     */     throws Exception
/* 1327:     */   {
/* 1328:1686 */     useCacheOrOnlineRepository();
/* 1329:1687 */     List<Boolean> upgrades = new ArrayList();
/* 1330:1688 */     for (Package p : toInstall) {
/* 1331:1689 */       if (p.isInstalled()) {
/* 1332:1690 */         upgrades.add(new Boolean(true));
/* 1333:     */       } else {
/* 1334:1692 */         upgrades.add(new Boolean(false));
/* 1335:     */       }
/* 1336:     */     }
/* 1337:1695 */     PACKAGE_MANAGER.installPackages(toInstall, progress);
/* 1338:1696 */     boolean atLeastOneUpgrade = false;
/* 1339:     */     
/* 1340:1698 */     List<File> gpcFiles = new ArrayList();
/* 1341:1699 */     int i = 0;
/* 1342:1700 */     for (Package p : toInstall)
/* 1343:     */     {
/* 1344:1701 */       boolean isAnUpgrade = ((Boolean)upgrades.get(i++)).booleanValue();
/* 1345:1702 */       if (isAnUpgrade) {
/* 1346:1703 */         atLeastOneUpgrade = true;
/* 1347:     */       }
/* 1348:1706 */       String packageName = p.getName();
/* 1349:1707 */       File packageDir = new File(PACKAGE_MANAGER.getPackageHome().toString() + File.separator + packageName);
/* 1350:     */       
/* 1351:     */ 
/* 1352:1710 */       boolean loadIt = loadCheck(p, packageDir, progress);
/* 1353:1712 */       if ((loadIt & !isAnUpgrade)) {
/* 1354:1713 */         loadPackageDirectory(packageDir, false, gpcFiles, false);
/* 1355:     */       }
/* 1356:     */     }
/* 1357:1717 */     for (File f : gpcFiles) {
/* 1358:1718 */       processGenericPropertiesCreatorProps(f);
/* 1359:     */     }
/* 1360:1721 */     return atLeastOneUpgrade;
/* 1361:     */   }
/* 1362:     */   
/* 1363:     */   public static List<Object> getRepositoryPackageVersions(String packageName)
/* 1364:     */     throws Exception
/* 1365:     */   {
/* 1366:1733 */     useCacheOrOnlineRepository();
/* 1367:1734 */     return PACKAGE_MANAGER.getRepositoryPackageVersions(packageName);
/* 1368:     */   }
/* 1369:     */   
/* 1370:     */   public static URL getPackageRepositoryURL()
/* 1371:     */   {
/* 1372:1743 */     useCacheOrOnlineRepository();
/* 1373:1744 */     return PACKAGE_MANAGER.getPackageRepositoryURL();
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public static List<Package> getAllPackages()
/* 1377:     */     throws Exception
/* 1378:     */   {
/* 1379:1754 */     useCacheOrOnlineRepository();
/* 1380:1755 */     return PACKAGE_MANAGER.getAllPackages(new PrintStream[0]);
/* 1381:     */   }
/* 1382:     */   
/* 1383:     */   public static List<Package> getAvailablePackages()
/* 1384:     */     throws Exception
/* 1385:     */   {
/* 1386:1765 */     useCacheOrOnlineRepository();
/* 1387:1766 */     return PACKAGE_MANAGER.getAvailablePackages();
/* 1388:     */   }
/* 1389:     */   
/* 1390:     */   public static List<Package> getAvailableCompatiblePackages()
/* 1391:     */     throws Exception
/* 1392:     */   {
/* 1393:1781 */     List<Package> allAvail = getAllPackages();
/* 1394:1782 */     List<Package> compatible = new ArrayList();
/* 1395:1784 */     for (Iterator i$ = allAvail.iterator(); i$.hasNext();)
/* 1396:     */     {
/* 1397:1784 */       p = (Package)i$.next();
/* 1398:1785 */       List<Object> availableVersions = PACKAGE_MANAGER.getRepositoryPackageVersions(p.getName());
/* 1399:1791 */       for (Object version : availableVersions)
/* 1400:     */       {
/* 1401:1792 */         Package versionedPackage = getRepositoryPackageInfo(p.getName(), version.toString());
/* 1402:1794 */         if (versionedPackage.isCompatibleBaseSystem())
/* 1403:     */         {
/* 1404:1795 */           if (p.isInstalled())
/* 1405:     */           {
/* 1406:1798 */             Package installed = getInstalledPackageInfo(p.getName());
/* 1407:1799 */             String installedV = installed.getPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY).toString();
/* 1408:     */             
/* 1409:     */ 
/* 1410:1802 */             String versionedV = versionedPackage.getPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY).toString();
/* 1411:     */             
/* 1412:     */ 
/* 1413:1805 */             VersionPackageConstraint.VersionComparison v = VersionPackageConstraint.compare(versionedV, installedV);
/* 1414:1807 */             if (v == VersionPackageConstraint.VersionComparison.GREATERTHAN) {
/* 1415:1808 */               compatible.add(versionedPackage);
/* 1416:     */             }
/* 1417:1810 */             break;
/* 1418:     */           }
/* 1419:1811 */           compatible.add(versionedPackage);
/* 1420:     */           
/* 1421:1813 */           break;
/* 1422:     */         }
/* 1423:     */       }
/* 1424:     */     }
/* 1425:     */     Package p;
/* 1426:1818 */     return compatible;
/* 1427:     */   }
/* 1428:     */   
/* 1429:     */   public static List<Package> getInstalledPackages()
/* 1430:     */     throws Exception
/* 1431:     */   {
/* 1432:1828 */     useCacheOrOnlineRepository();
/* 1433:1829 */     return PACKAGE_MANAGER.getInstalledPackages();
/* 1434:     */   }
/* 1435:     */   
/* 1436:     */   public static List<Dependency> getAllDependenciesForPackage(Package target, Map<String, List<Dependency>> conflicts)
/* 1437:     */     throws Exception
/* 1438:     */   {
/* 1439:1842 */     useCacheOrOnlineRepository();
/* 1440:1843 */     return PACKAGE_MANAGER.getAllDependenciesForPackage(target, conflicts);
/* 1441:     */   }
/* 1442:     */   
/* 1443:     */   public static Package getPackageArchiveInfo(String packageArchivePath)
/* 1444:     */     throws Exception
/* 1445:     */   {
/* 1446:1855 */     useCacheOrOnlineRepository();
/* 1447:1856 */     return PACKAGE_MANAGER.getPackageArchiveInfo(packageArchivePath);
/* 1448:     */   }
/* 1449:     */   
/* 1450:     */   public static Package getInstalledPackageInfo(String packageName)
/* 1451:     */     throws Exception
/* 1452:     */   {
/* 1453:1868 */     useCacheOrOnlineRepository();
/* 1454:1869 */     return PACKAGE_MANAGER.getInstalledPackageInfo(packageName);
/* 1455:     */   }
/* 1456:     */   
/* 1457:     */   public static Package getRepositoryPackageInfo(String packageName)
/* 1458:     */     throws Exception
/* 1459:     */   {
/* 1460:1881 */     useCacheOrOnlineRepository();
/* 1461:1882 */     return PACKAGE_MANAGER.getRepositoryPackageInfo(packageName);
/* 1462:     */   }
/* 1463:     */   
/* 1464:     */   public static Package getRepositoryPackageInfo(String packageName, String version)
/* 1465:     */     throws Exception
/* 1466:     */   {
/* 1467:1895 */     useCacheOrOnlineRepository();
/* 1468:1896 */     return PACKAGE_MANAGER.getRepositoryPackageInfo(packageName, version);
/* 1469:     */   }
/* 1470:     */   
/* 1471:     */   public static boolean installPackageFromRepository(String packageName, String version, PrintStream... progress)
/* 1472:     */     throws Exception
/* 1473:     */   {
/* 1474:1911 */     useCacheOrOnlineRepository();
/* 1475:1912 */     Package toLoad = getRepositoryPackageInfo(packageName);
/* 1476:     */     
/* 1477:     */ 
/* 1478:     */ 
/* 1479:     */ 
/* 1480:     */ 
/* 1481:     */ 
/* 1482:1919 */     boolean isAnUpgrade = toLoad.isInstalled();
/* 1483:     */     
/* 1484:1921 */     Object specialInstallMessage = toLoad.getPackageMetaDataElement("MessageToDisplayOnInstallation");
/* 1485:1923 */     if ((specialInstallMessage != null) && (specialInstallMessage.toString().length() > 0))
/* 1486:     */     {
/* 1487:1925 */       String siM = specialInstallMessage.toString();
/* 1488:     */       try
/* 1489:     */       {
/* 1490:1927 */         siM = Environment.getSystemWide().substitute(siM);
/* 1491:     */       }
/* 1492:     */       catch (Exception ex) {}
/* 1493:1931 */       String message = "**** Special installation message ****\n" + siM + "\n**** Special installation message ****";
/* 1494:1933 */       for (PrintStream p : progress) {
/* 1495:1934 */         p.println(message);
/* 1496:     */       }
/* 1497:     */     }
/* 1498:1938 */     PACKAGE_MANAGER.installPackageFromRepository(packageName, version, progress);
/* 1499:     */     
/* 1500:1940 */     File packageDir = new File(PACKAGE_MANAGER.getPackageHome().toString() + File.separator + packageName);
/* 1501:     */     
/* 1502:     */ 
/* 1503:1943 */     boolean loadIt = checkForMissingClasses(toLoad, progress);
/* 1504:1944 */     if ((loadIt) && (!isAnUpgrade))
/* 1505:     */     {
/* 1506:1945 */       File packageRoot = new File(PACKAGE_MANAGER.getPackageHome() + File.separator + packageName);
/* 1507:     */       
/* 1508:1947 */       loadIt = checkForMissingFiles(toLoad, packageRoot, progress);
/* 1509:1948 */       if (loadIt) {
/* 1510:1949 */         loadPackageDirectory(packageDir, false, null, false);
/* 1511:     */       }
/* 1512:     */     }
/* 1513:1953 */     return isAnUpgrade;
/* 1514:     */   }
/* 1515:     */   
/* 1516:     */   public static String installPackageFromArchive(String packageArchivePath, PrintStream... progress)
/* 1517:     */     throws Exception
/* 1518:     */   {
/* 1519:1966 */     useCacheOrOnlineRepository();
/* 1520:1967 */     Package toInstall = PACKAGE_MANAGER.getPackageArchiveInfo(packageArchivePath);
/* 1521:     */     
/* 1522:     */ 
/* 1523:1970 */     Object specialInstallMessage = toInstall.getPackageMetaDataElement("MessageToDisplayOnInstallation");
/* 1524:1972 */     if ((specialInstallMessage != null) && (specialInstallMessage.toString().length() > 0))
/* 1525:     */     {
/* 1526:1974 */       String siM = specialInstallMessage.toString();
/* 1527:     */       try
/* 1528:     */       {
/* 1529:1976 */         siM = Environment.getSystemWide().substitute(siM);
/* 1530:     */       }
/* 1531:     */       catch (Exception ex) {}
/* 1532:1980 */       String message = "**** Special installation message ****\n" + siM + "\n**** Special installation message ****";
/* 1533:1982 */       for (PrintStream p : progress) {
/* 1534:1983 */         p.println(message);
/* 1535:     */       }
/* 1536:     */     }
/* 1537:1987 */     PACKAGE_MANAGER.installPackageFromArchive(packageArchivePath, progress);
/* 1538:     */     
/* 1539:1989 */     boolean loadIt = checkForMissingClasses(toInstall, progress);
/* 1540:1990 */     if (loadIt)
/* 1541:     */     {
/* 1542:1991 */       File packageRoot = new File(PACKAGE_MANAGER.getPackageHome() + File.separator + toInstall.getName());
/* 1543:     */       
/* 1544:1993 */       loadIt = checkForMissingFiles(toInstall, packageRoot, progress);
/* 1545:1994 */       if (loadIt) {
/* 1546:1995 */         loadPackageDirectory(packageRoot, false, null, false);
/* 1547:     */       }
/* 1548:     */     }
/* 1549:1999 */     return toInstall.getName();
/* 1550:     */   }
/* 1551:     */   
/* 1552:     */   public static String installPackageFromURL(URL packageURL, PrintStream... progress)
/* 1553:     */     throws Exception
/* 1554:     */   {
/* 1555:2012 */     useCacheOrOnlineRepository();
/* 1556:2013 */     String packageName = PACKAGE_MANAGER.installPackageFromURL(packageURL, progress);
/* 1557:     */     
/* 1558:     */ 
/* 1559:2016 */     Package installed = PACKAGE_MANAGER.getInstalledPackageInfo(packageName);
/* 1560:     */     
/* 1561:2018 */     Object specialInstallMessage = installed.getPackageMetaDataElement("MessageToDisplayOnInstallation");
/* 1562:2020 */     if ((specialInstallMessage != null) && (specialInstallMessage.toString().length() > 0))
/* 1563:     */     {
/* 1564:2022 */       String message = "**** Special installation message ****\n" + specialInstallMessage.toString() + "\n**** Special installation message ****";
/* 1565:2025 */       for (PrintStream p : progress) {
/* 1566:2026 */         p.println(message);
/* 1567:     */       }
/* 1568:     */     }
/* 1569:2030 */     boolean loadIt = checkForMissingClasses(installed, progress);
/* 1570:2031 */     if (loadIt)
/* 1571:     */     {
/* 1572:2032 */       File packageRoot = new File(PACKAGE_MANAGER.getPackageHome() + File.separator + installed.getName());
/* 1573:     */       
/* 1574:2034 */       loadIt = checkForMissingFiles(installed, packageRoot, progress);
/* 1575:2035 */       if (loadIt) {
/* 1576:2036 */         loadPackageDirectory(packageRoot, false, null, false);
/* 1577:     */       }
/* 1578:     */     }
/* 1579:2039 */     return packageName;
/* 1580:     */   }
/* 1581:     */   
/* 1582:     */   public static void uninstallPackage(String packageName, boolean updateKnowledgeFlow, PrintStream... progress)
/* 1583:     */     throws Exception
/* 1584:     */   {
/* 1585:2056 */     if (updateKnowledgeFlow)
/* 1586:     */     {
/* 1587:2057 */       File packageToDel = new File(PACKAGE_MANAGER.getPackageHome().toString() + File.separator + packageName);
/* 1588:2059 */       if ((packageToDel.exists()) && (packageToDel.isDirectory()))
/* 1589:     */       {
/* 1590:2060 */         File[] contents = packageToDel.listFiles();
/* 1591:2061 */         for (File content : contents) {
/* 1592:2062 */           if ((content.isFile()) && (content.getPath().endsWith("Beans.props")))
/* 1593:     */           {
/* 1594:2067 */             KnowledgeFlowApp.removeFromPluginBeanProps(content);
/* 1595:2068 */             KnowledgeFlowApp.disposeSingleton();
/* 1596:2069 */             break;
/* 1597:     */           }
/* 1598:     */         }
/* 1599:     */       }
/* 1600:     */     }
/* 1601:2075 */     PACKAGE_MANAGER.uninstallPackage(packageName, progress);
/* 1602:     */   }
/* 1603:     */   
/* 1604:     */   private static void printPackageInfo(Map<?, ?> packageProps)
/* 1605:     */   {
/* 1606:2079 */     Set<?> keys = packageProps.keySet();
/* 1607:2080 */     Iterator<?> i = keys.iterator();
/* 1608:2082 */     while (i.hasNext())
/* 1609:     */     {
/* 1610:2083 */       Object key = i.next();
/* 1611:2084 */       Object value = packageProps.get(key);
/* 1612:2085 */       System.out.println(Utils.padLeft(key.toString(), 11) + ":\t" + value.toString());
/* 1613:     */     }
/* 1614:     */   }
/* 1615:     */   
/* 1616:     */   protected static void printPackageArchiveInfo(String packagePath)
/* 1617:     */     throws Exception
/* 1618:     */   {
/* 1619:2098 */     Map<?, ?> packageProps = getPackageArchiveInfo(packagePath).getPackageMetaData();
/* 1620:     */     
/* 1621:2100 */     printPackageInfo(packageProps);
/* 1622:     */   }
/* 1623:     */   
/* 1624:     */   protected static void printInstalledPackageInfo(String packageName)
/* 1625:     */     throws Exception
/* 1626:     */   {
/* 1627:2111 */     Map<?, ?> packageProps = getInstalledPackageInfo(packageName).getPackageMetaData();
/* 1628:     */     
/* 1629:2113 */     printPackageInfo(packageProps);
/* 1630:     */   }
/* 1631:     */   
/* 1632:     */   protected static void printRepositoryPackageInfo(String packageName, String version)
/* 1633:     */     throws Exception
/* 1634:     */   {
/* 1635:2125 */     Map<?, ?> packageProps = getRepositoryPackageInfo(packageName, version).getPackageMetaData();
/* 1636:     */     
/* 1637:2127 */     printPackageInfo(packageProps);
/* 1638:     */   }
/* 1639:     */   
/* 1640:     */   private static String queryUser()
/* 1641:     */   {
/* 1642:2131 */     BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
/* 1643:     */     
/* 1644:     */ 
/* 1645:2134 */     String result = null;
/* 1646:     */     try
/* 1647:     */     {
/* 1648:2136 */       result = br.readLine();
/* 1649:     */     }
/* 1650:     */     catch (IOException ex) {}
/* 1651:2141 */     return result;
/* 1652:     */   }
/* 1653:     */   
/* 1654:     */   private static void removeInstalledPackage(String packageName, boolean force, PrintStream... progress)
/* 1655:     */     throws Exception
/* 1656:     */   {
/* 1657:2147 */     List<Package> compromised = new ArrayList();
/* 1658:     */     
/* 1659:     */ 
/* 1660:2150 */     List<Package> installedPackages = null;
/* 1661:2151 */     if (!force)
/* 1662:     */     {
/* 1663:2152 */       installedPackages = getInstalledPackages();
/* 1664:2153 */       for (Iterator i$ = installedPackages.iterator(); i$.hasNext();)
/* 1665:     */       {
/* 1666:2153 */         p = (Package)i$.next();
/* 1667:2154 */         List<Dependency> tempDeps = p.getDependencies();
/* 1668:2156 */         for (Dependency d : tempDeps) {
/* 1669:2157 */           if (d.getTarget().getPackage().getName().equals(packageName))
/* 1670:     */           {
/* 1671:2159 */             compromised.add(p);
/* 1672:2160 */             break;
/* 1673:     */           }
/* 1674:     */         }
/* 1675:     */       }
/* 1676:     */       Package p;
/* 1677:2165 */       if (compromised.size() > 0)
/* 1678:     */       {
/* 1679:2166 */         System.out.println("The following installed packages depend on " + packageName + " :\n");
/* 1680:2168 */         for (Package p : compromised) {
/* 1681:2169 */           System.out.println("\t" + p.getName());
/* 1682:     */         }
/* 1683:2172 */         System.out.println("\nDo you wish to proceed [y/n]?");
/* 1684:2173 */         String response = queryUser();
/* 1685:2174 */         if ((response != null) && ((response.equalsIgnoreCase("n")) || (response.equalsIgnoreCase("no")))) {
/* 1686:2176 */           return;
/* 1687:     */         }
/* 1688:     */       }
/* 1689:     */     }
/* 1690:2181 */     if (force) {
/* 1691:2182 */       System.out.println("Forced uninstall.");
/* 1692:     */     }
/* 1693:2185 */     compromised = null;
/* 1694:2186 */     installedPackages = null;
/* 1695:     */     
/* 1696:2188 */     uninstallPackage(packageName, false, progress);
/* 1697:     */   }
/* 1698:     */   
/* 1699:     */   private static void installPackageFromRepository(String packageName, String version, boolean force)
/* 1700:     */     throws Exception
/* 1701:     */   {
/* 1702:2194 */     Package toInstall = null;
/* 1703:     */     try
/* 1704:     */     {
/* 1705:2196 */       toInstall = getRepositoryPackageInfo(packageName, version);
/* 1706:     */     }
/* 1707:     */     catch (Exception ex)
/* 1708:     */     {
/* 1709:2198 */       System.err.println("[WekaPackageManager] Package " + packageName + " at version " + version + " doesn't seem to exist!");
/* 1710:     */       
/* 1711:     */ 
/* 1712:2201 */       return;
/* 1713:     */     }
/* 1714:2205 */     if (!force)
/* 1715:     */     {
/* 1716:2206 */       boolean ok = toInstall.isCompatibleBaseSystem();
/* 1717:2208 */       if (!ok)
/* 1718:     */       {
/* 1719:2209 */         List<Dependency> baseSysDep = toInstall.getBaseSystemDependency();
/* 1720:2210 */         StringBuffer depList = new StringBuffer();
/* 1721:2211 */         for (Dependency bd : baseSysDep) {
/* 1722:2212 */           depList.append(bd.getTarget().toString() + " ");
/* 1723:     */         }
/* 1724:2214 */         System.err.println("Can't install package " + packageName + " because it requires " + depList.toString());
/* 1725:     */         
/* 1726:2216 */         return;
/* 1727:     */       }
/* 1728:2219 */       if (toInstall.isInstalled())
/* 1729:     */       {
/* 1730:2220 */         Package installedVersion = getInstalledPackageInfo(packageName);
/* 1731:2221 */         if (!toInstall.equals(installedVersion))
/* 1732:     */         {
/* 1733:2223 */           System.out.println("Package " + packageName + "[" + installedVersion + "] is already installed. Replace with " + toInstall + " [y/n]?");
/* 1734:     */           
/* 1735:     */ 
/* 1736:2226 */           String response = queryUser();
/* 1737:2227 */           if ((response != null) && ((response.equalsIgnoreCase("n")) || (response.equalsIgnoreCase("no")))) {
/* 1738:2229 */             return;
/* 1739:     */           }
/* 1740:     */         }
/* 1741:     */         else
/* 1742:     */         {
/* 1743:2232 */           System.out.println("Package " + packageName + "[" + installedVersion + "] is already installed. Install again [y/n]?");
/* 1744:     */           
/* 1745:2234 */           String response = queryUser();
/* 1746:2235 */           if ((response != null) && ((response.equalsIgnoreCase("n")) || (response.equalsIgnoreCase("no")))) {
/* 1747:2237 */             return;
/* 1748:     */           }
/* 1749:     */         }
/* 1750:     */       }
/* 1751:2244 */       Map<String, List<Dependency>> conflicts = new HashMap();
/* 1752:     */       
/* 1753:2246 */       List<Dependency> dependencies = getAllDependenciesForPackage(toInstall, conflicts);
/* 1754:2249 */       if (conflicts.size() > 0)
/* 1755:     */       {
/* 1756:2250 */         System.err.println("Package " + packageName + " requires the following packages:\n");
/* 1757:     */         
/* 1758:2252 */         Iterator<Dependency> depI = dependencies.iterator();
/* 1759:2253 */         while (depI.hasNext())
/* 1760:     */         {
/* 1761:2254 */           Dependency d = (Dependency)depI.next();
/* 1762:2255 */           System.err.println("\t" + d);
/* 1763:     */         }
/* 1764:2258 */         System.err.println("\nThere are conflicting dependencies:\n");
/* 1765:2259 */         Set<String> pNames = conflicts.keySet();
/* 1766:2260 */         Iterator<String> pNameI = pNames.iterator();
/* 1767:2261 */         while (pNameI.hasNext())
/* 1768:     */         {
/* 1769:2262 */           String pName = (String)pNameI.next();
/* 1770:2263 */           System.err.println("Conflicts for " + pName);
/* 1771:2264 */           List<Dependency> confsForPackage = (List)conflicts.get(pName);
/* 1772:2265 */           Iterator<Dependency> confs = confsForPackage.iterator();
/* 1773:2266 */           while (confs.hasNext())
/* 1774:     */           {
/* 1775:2267 */             Dependency problem = (Dependency)confs.next();
/* 1776:2268 */             System.err.println("\t" + problem);
/* 1777:     */           }
/* 1778:     */         }
/* 1779:2272 */         System.err.println("Unable to continue with installation.");
/* 1780:2273 */         return;
/* 1781:     */       }
/* 1782:2280 */       List<PackageConstraint> needsUpgrade = new ArrayList();
/* 1783:2281 */       List<Package> finalListToInstall = new ArrayList();
/* 1784:     */       
/* 1785:2283 */       Iterator<Dependency> depI = dependencies.iterator();
/* 1786:2284 */       while (depI.hasNext())
/* 1787:     */       {
/* 1788:2285 */         Dependency toCheck = (Dependency)depI.next();
/* 1789:2286 */         if (toCheck.getTarget().getPackage().isInstalled())
/* 1790:     */         {
/* 1791:2287 */           String toCheckName = toCheck.getTarget().getPackage().getPackageMetaDataElement("PackageName").toString();
/* 1792:     */           
/* 1793:2289 */           Package installedVersion = PACKAGE_MANAGER.getInstalledPackageInfo(toCheckName);
/* 1794:2291 */           if (!toCheck.getTarget().checkConstraint(installedVersion))
/* 1795:     */           {
/* 1796:2292 */             needsUpgrade.add(toCheck.getTarget());
/* 1797:2293 */             Package mostRecent = toCheck.getTarget().getPackage();
/* 1798:2294 */             if ((toCheck.getTarget() instanceof VersionPackageConstraint)) {
/* 1799:2296 */               mostRecent = mostRecentVersionWithRespectToConstraint(toCheck.getTarget());
/* 1800:     */             }
/* 1801:2299 */             finalListToInstall.add(mostRecent);
/* 1802:     */           }
/* 1803:     */         }
/* 1804:     */         else
/* 1805:     */         {
/* 1806:2302 */           Package mostRecent = toCheck.getTarget().getPackage();
/* 1807:2303 */           if ((toCheck.getTarget() instanceof VersionPackageConstraint)) {
/* 1808:2305 */             mostRecent = mostRecentVersionWithRespectToConstraint(toCheck.getTarget());
/* 1809:     */           }
/* 1810:2308 */           finalListToInstall.add(mostRecent);
/* 1811:     */         }
/* 1812:     */       }
/* 1813:2312 */       if (needsUpgrade.size() > 0)
/* 1814:     */       {
/* 1815:2313 */         System.out.println("The following packages will be upgraded in order to install " + packageName);
/* 1816:     */         
/* 1817:     */ 
/* 1818:2316 */         Iterator<PackageConstraint> upI = needsUpgrade.iterator();
/* 1819:2317 */         while (upI.hasNext())
/* 1820:     */         {
/* 1821:2318 */           PackageConstraint tempC = (PackageConstraint)upI.next();
/* 1822:2319 */           System.out.println("\t" + tempC);
/* 1823:     */         }
/* 1824:2322 */         System.out.print("\nOK to continue [y/n]? > ");
/* 1825:2323 */         String response = queryUser();
/* 1826:2324 */         if ((response != null) && ((response.equalsIgnoreCase("n")) || (response.equalsIgnoreCase("no")))) {
/* 1827:2326 */           return;
/* 1828:     */         }
/* 1829:2331 */         boolean conflictsAfterUpgrade = false;
/* 1830:2332 */         List<Package> installed = getInstalledPackages();
/* 1831:2333 */         List<Package> toUpgrade = new ArrayList();
/* 1832:2334 */         upI = needsUpgrade.iterator();
/* 1833:2335 */         while (upI.hasNext()) {
/* 1834:2336 */           toUpgrade.add(((PackageConstraint)upI.next()).getPackage());
/* 1835:     */         }
/* 1836:2344 */         toUpgrade.add(toInstall);
/* 1837:2346 */         for (int i = 0; i < installed.size(); i++)
/* 1838:     */         {
/* 1839:2347 */           Package tempP = (Package)installed.get(i);
/* 1840:2348 */           String tempPName = tempP.getName();
/* 1841:2349 */           boolean checkIt = true;
/* 1842:2350 */           for (int j = 0; j < needsUpgrade.size(); j++) {
/* 1843:2351 */             if (tempPName.equals(((PackageConstraint)needsUpgrade.get(j)).getPackage().getName()))
/* 1844:     */             {
/* 1845:2352 */               checkIt = false;
/* 1846:2353 */               break;
/* 1847:     */             }
/* 1848:     */           }
/* 1849:2357 */           if (checkIt)
/* 1850:     */           {
/* 1851:2358 */             List<Dependency> problem = tempP.getIncompatibleDependencies(toUpgrade);
/* 1852:2360 */             if (problem.size() > 0)
/* 1853:     */             {
/* 1854:2361 */               conflictsAfterUpgrade = true;
/* 1855:     */               
/* 1856:2363 */               System.err.println("Package " + tempP.getName() + " will have a compatibility" + "problem with the following packages after upgrading them:");
/* 1857:     */               
/* 1858:     */ 
/* 1859:2366 */               Iterator<Dependency> dI = problem.iterator();
/* 1860:2367 */               while (dI.hasNext()) {
/* 1861:2368 */                 System.err.println("\t" + ((Dependency)dI.next()).getTarget().getPackage());
/* 1862:     */               }
/* 1863:     */             }
/* 1864:     */           }
/* 1865:     */         }
/* 1866:2374 */         if (conflictsAfterUpgrade)
/* 1867:     */         {
/* 1868:2375 */           System.err.println("Unable to continue with installation.");
/* 1869:2376 */           return;
/* 1870:     */         }
/* 1871:     */       }
/* 1872:2381 */       if (finalListToInstall.size() > 0)
/* 1873:     */       {
/* 1874:2382 */         System.out.println("To install " + packageName + " the following packages will" + " be installed/upgraded:\n\n");
/* 1875:     */         
/* 1876:2384 */         Iterator<Package> i = finalListToInstall.iterator();
/* 1877:2385 */         while (i.hasNext()) {
/* 1878:2386 */           System.out.println("\t" + i.next());
/* 1879:     */         }
/* 1880:2388 */         System.out.print("\nOK to proceed [y/n]? > ");
/* 1881:2389 */         String response = queryUser();
/* 1882:2391 */         if ((response != null) && ((response.equalsIgnoreCase("n")) || (response.equalsIgnoreCase("no")))) {
/* 1883:2393 */           return;
/* 1884:     */         }
/* 1885:     */       }
/* 1886:2400 */       installPackages(finalListToInstall, new PrintStream[] { System.out });
/* 1887:     */       
/* 1888:     */ 
/* 1889:2403 */       installPackageFromRepository(packageName, version, new PrintStream[] { System.out });
/* 1890:     */     }
/* 1891:     */     else
/* 1892:     */     {
/* 1893:2408 */       installPackageFromRepository(packageName, version, new PrintStream[] { System.out });
/* 1894:     */     }
/* 1895:     */   }
/* 1896:     */   
/* 1897:     */   private static void listPackages(String arg)
/* 1898:     */     throws Exception
/* 1899:     */   {
/* 1900:2414 */     if ((m_offline) && ((arg.equalsIgnoreCase("all")) || (arg.equalsIgnoreCase("available"))))
/* 1901:     */     {
/* 1902:2416 */       System.out.println("Running offline - unable to display available or all package information");
/* 1903:     */       
/* 1904:2418 */       return;
/* 1905:     */     }
/* 1906:2421 */     List<Package> packageList = null;
/* 1907:2422 */     useCacheOrOnlineRepository();
/* 1908:2424 */     if (arg.equalsIgnoreCase("all"))
/* 1909:     */     {
/* 1910:2425 */       packageList = PACKAGE_MANAGER.getAllPackages(new PrintStream[0]);
/* 1911:     */     }
/* 1912:2426 */     else if (arg.equals("installed"))
/* 1913:     */     {
/* 1914:2427 */       packageList = PACKAGE_MANAGER.getInstalledPackages();
/* 1915:     */     }
/* 1916:2428 */     else if (arg.equals("available"))
/* 1917:     */     {
/* 1918:2429 */       packageList = PACKAGE_MANAGER.getAvailablePackages();
/* 1919:     */     }
/* 1920:     */     else
/* 1921:     */     {
/* 1922:2431 */       System.err.println("[WekaPackageManager] Unknown argument " + arg);
/* 1923:2432 */       printUsage();
/* 1924:     */       
/* 1925:2434 */       return;
/* 1926:     */     }
/* 1927:2437 */     StringBuffer result = new StringBuffer();
/* 1928:2438 */     result.append("Installed\tRepository\tLoaded\tPackage\n");
/* 1929:2439 */     result.append("=========\t==========\t======\t=======\n");
/* 1930:     */     
/* 1931:2441 */     boolean userOptedNoLoad = false;
/* 1932:2442 */     Iterator<Package> i = packageList.iterator();
/* 1933:2443 */     while (i.hasNext())
/* 1934:     */     {
/* 1935:2444 */       Package p = (Package)i.next();
/* 1936:2445 */       String installedV = "-----    ";
/* 1937:2446 */       String repositoryV = "-----     ";
/* 1938:2447 */       String loaded = "No";
/* 1939:2448 */       if (p.isInstalled())
/* 1940:     */       {
/* 1941:2449 */         Package installedP = getInstalledPackageInfo(p.getName());
/* 1942:2450 */         if (loadCheck(installedP, new File(getPackageHome().toString() + File.separator + p.getName()), new PrintStream[0]))
/* 1943:     */         {
/* 1944:2453 */           loaded = "Yes";
/* 1945:     */         }
/* 1946:2455 */         else if (m_doNotLoadList.contains(installedP.getName()))
/* 1947:     */         {
/* 1948:2456 */           loaded = "No*";
/* 1949:2457 */           userOptedNoLoad = true;
/* 1950:     */         }
/* 1951:2460 */         installedV = installedP.getPackageMetaDataElement("Version").toString() + "    ";
/* 1952:     */         try
/* 1953:     */         {
/* 1954:2463 */           if (!m_offline)
/* 1955:     */           {
/* 1956:2464 */             Package repP = getRepositoryPackageInfo(p.getName());
/* 1957:2465 */             repositoryV = repP.getPackageMetaDataElement("Version").toString() + "     ";
/* 1958:     */           }
/* 1959:     */         }
/* 1960:     */         catch (Exception ex) {}
/* 1961:     */       }
/* 1962:     */       else
/* 1963:     */       {
/* 1964:2472 */         repositoryV = p.getPackageMetaDataElement("Version").toString() + "     ";
/* 1965:     */       }
/* 1966:2475 */       String title = p.getPackageMetaDataElement("Title").toString();
/* 1967:2476 */       result.append(installedV + "\t" + repositoryV + "\t" + loaded + "\t" + p.getName() + ": " + title + "\n");
/* 1968:     */     }
/* 1969:2479 */     if (userOptedNoLoad) {
/* 1970:2480 */       result.append("* User flagged as no load\n");
/* 1971:     */     }
/* 1972:2483 */     System.out.println(result.toString());
/* 1973:     */   }
/* 1974:     */   
/* 1975:     */   private static void printUsage()
/* 1976:     */   {
/* 1977:2487 */     System.out.println("Usage: weka.core.WekaPackageManager [-offline] [option]");
/* 1978:     */     
/* 1979:2489 */     System.out.println("Options:\n\t-list-packages <all | installed | available>\n\t-package-info <repository | installed | archive> <packageName | packageZip>\n\t-install-package <packageName | packageZip | URL> [version]\n\t-uninstall-package packageName\n\t-toggle-load-status packageName [packageName packageName ...]\n\t-refresh-cache");
/* 1980:     */   }
/* 1981:     */   
/* 1982:     */   public static void main(String[] args)
/* 1983:     */   {
/* 1984:2504 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 1985:     */     try
/* 1986:     */     {
/* 1987:2509 */       for (int i = 0; i < args.length; i++) {
/* 1988:2510 */         if (args[i].equals("-offline"))
/* 1989:     */         {
/* 1990:2511 */           m_offline = true;
/* 1991:2512 */           String[] temp = new String[args.length - 1];
/* 1992:2513 */           if (i > 0) {
/* 1993:2514 */             System.arraycopy(args, 0, temp, 0, i);
/* 1994:     */           }
/* 1995:2516 */           System.arraycopy(args, i + 1, temp, i, args.length - (i + 1));
/* 1996:2517 */           args = temp;
/* 1997:     */         }
/* 1998:     */       }
/* 1999:2521 */       establishCacheIfNeeded(new PrintStream[] { System.out });
/* 2000:2522 */       checkForNewPackages(new PrintStream[] { System.out });
/* 2001:2524 */       if ((args.length == 0) || (args[0].equalsIgnoreCase("-h")) || (args[0].equalsIgnoreCase("-help")))
/* 2002:     */       {
/* 2003:2526 */         printUsage();
/* 2004:     */         
/* 2005:2528 */         return;
/* 2006:     */       }
/* 2007:2531 */       if (args[0].equals("-package-info"))
/* 2008:     */       {
/* 2009:2532 */         if (args.length < 3)
/* 2010:     */         {
/* 2011:2533 */           printUsage();
/* 2012:2534 */           return;
/* 2013:     */         }
/* 2014:2537 */         if (args[1].equals("archive"))
/* 2015:     */         {
/* 2016:2538 */           printPackageArchiveInfo(args[2]);
/* 2017:     */         }
/* 2018:2539 */         else if (args[1].equals("installed"))
/* 2019:     */         {
/* 2020:2540 */           printInstalledPackageInfo(args[2]);
/* 2021:     */         }
/* 2022:2541 */         else if (args[1].equals("repository"))
/* 2023:     */         {
/* 2024:2542 */           String version = "Latest";
/* 2025:2543 */           if (args.length == 4) {
/* 2026:2544 */             version = args[3];
/* 2027:     */           }
/* 2028:     */           try
/* 2029:     */           {
/* 2030:2547 */             printRepositoryPackageInfo(args[2], version);
/* 2031:     */           }
/* 2032:     */           catch (Exception ex)
/* 2033:     */           {
/* 2034:2551 */             System.out.println("[WekaPackageManager] Nothing known about package " + args[2] + " at the repository!");
/* 2035:     */           }
/* 2036:     */         }
/* 2037:     */         else
/* 2038:     */         {
/* 2039:2556 */           System.err.println("[WekaPackageManager] Unknown argument " + args[2]);
/* 2040:     */           
/* 2041:2558 */           printUsage();
/* 2042:     */         }
/* 2043:     */       }
/* 2044:2562 */       else if (args[0].equals("-install-package"))
/* 2045:     */       {
/* 2046:2563 */         String targetLowerCase = args[1].toLowerCase();
/* 2047:2564 */         if ((targetLowerCase.startsWith("http://")) || (targetLowerCase.startsWith("https://")))
/* 2048:     */         {
/* 2049:2566 */           URL packageURL = new URL(args[1]);
/* 2050:2567 */           installPackageFromURL(packageURL, new PrintStream[] { System.out });
/* 2051:     */         }
/* 2052:2568 */         else if (targetLowerCase.endsWith(".zip"))
/* 2053:     */         {
/* 2054:2569 */           installPackageFromArchive(args[1], new PrintStream[] { System.out });
/* 2055:     */         }
/* 2056:     */         else
/* 2057:     */         {
/* 2058:2572 */           String version = "Latest";
/* 2059:2573 */           if (args.length == 3) {
/* 2060:2574 */             version = args[2];
/* 2061:     */           }
/* 2062:2576 */           installPackageFromRepository(args[1], version, false);
/* 2063:     */         }
/* 2064:     */       }
/* 2065:     */       else
/* 2066:     */       {
/* 2067:2578 */         if (args[0].equals("-uninstall-package"))
/* 2068:     */         {
/* 2069:2579 */           if (args.length < 2)
/* 2070:     */           {
/* 2071:2580 */             printUsage();
/* 2072:2581 */             return;
/* 2073:     */           }
/* 2074:2584 */           boolean force = false;
/* 2075:2586 */           if ((args.length == 3) && 
/* 2076:2587 */             (args[2].equals("-force"))) {
/* 2077:2588 */             force = true;
/* 2078:     */           }
/* 2079:2592 */           removeInstalledPackage(args[1], force, new PrintStream[] { System.out });
/* 2080:     */           
/* 2081:2594 */           return;
/* 2082:     */         }
/* 2083:2595 */         if (args[0].equals("-list-packages"))
/* 2084:     */         {
/* 2085:2596 */           if (args.length < 2)
/* 2086:     */           {
/* 2087:2597 */             printUsage();
/* 2088:     */             
/* 2089:2599 */             return;
/* 2090:     */           }
/* 2091:2601 */           listPackages(args[1]);
/* 2092:     */         }
/* 2093:2603 */         else if (args[0].equals("-toggle-load-status"))
/* 2094:     */         {
/* 2095:2604 */           if (args.length == 1)
/* 2096:     */           {
/* 2097:2605 */             printUsage();
/* 2098:2606 */             return;
/* 2099:     */           }
/* 2100:2608 */           List<String> toToggle = new ArrayList();
/* 2101:2609 */           for (int i = 1; i < args.length; i++) {
/* 2102:2610 */             toToggle.add(args[i].trim());
/* 2103:     */           }
/* 2104:2612 */           if (toToggle.size() >= 1) {
/* 2105:2613 */             toggleLoadStatus(toToggle);
/* 2106:     */           }
/* 2107:     */         }
/* 2108:2615 */         else if (args[0].equals("-refresh-cache"))
/* 2109:     */         {
/* 2110:2616 */           refreshCache(new PrintStream[] { System.out });
/* 2111:     */         }
/* 2112:     */         else
/* 2113:     */         {
/* 2114:2618 */           System.err.println("Unknown option: " + args[0]);
/* 2115:2619 */           printUsage();
/* 2116:     */         }
/* 2117:     */       }
/* 2118:2622 */       System.exit(0);
/* 2119:     */     }
/* 2120:     */     catch (Exception ex)
/* 2121:     */     {
/* 2122:2624 */       ex.printStackTrace();
/* 2123:     */     }
/* 2124:     */   }
/* 2125:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.WekaPackageManager
 * JD-Core Version:    0.7.0.1
 */