/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Component;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.FlowLayout;
/*    8:     */ import java.awt.GridLayout;
/*    9:     */ import java.awt.Image;
/*   10:     */ import java.awt.Toolkit;
/*   11:     */ import java.awt.event.ActionEvent;
/*   12:     */ import java.awt.event.ActionListener;
/*   13:     */ import java.awt.event.MouseAdapter;
/*   14:     */ import java.awt.event.MouseEvent;
/*   15:     */ import java.awt.event.WindowAdapter;
/*   16:     */ import java.awt.event.WindowEvent;
/*   17:     */ import java.io.BufferedReader;
/*   18:     */ import java.io.File;
/*   19:     */ import java.io.FileReader;
/*   20:     */ import java.io.IOException;
/*   21:     */ import java.io.PrintStream;
/*   22:     */ import java.io.PrintWriter;
/*   23:     */ import java.io.StringWriter;
/*   24:     */ import java.net.URL;
/*   25:     */ import java.net.URLConnection;
/*   26:     */ import java.util.ArrayList;
/*   27:     */ import java.util.Collections;
/*   28:     */ import java.util.Comparator;
/*   29:     */ import java.util.HashMap;
/*   30:     */ import java.util.Iterator;
/*   31:     */ import java.util.LinkedList;
/*   32:     */ import java.util.List;
/*   33:     */ import java.util.Map;
/*   34:     */ import java.util.Set;
/*   35:     */ import java.util.TreeMap;
/*   36:     */ import javax.swing.BorderFactory;
/*   37:     */ import javax.swing.ButtonGroup;
/*   38:     */ import javax.swing.DefaultCellEditor;
/*   39:     */ import javax.swing.DefaultComboBoxModel;
/*   40:     */ import javax.swing.ImageIcon;
/*   41:     */ import javax.swing.JButton;
/*   42:     */ import javax.swing.JCheckBox;
/*   43:     */ import javax.swing.JComboBox;
/*   44:     */ import javax.swing.JEditorPane;
/*   45:     */ import javax.swing.JFrame;
/*   46:     */ import javax.swing.JLabel;
/*   47:     */ import javax.swing.JOptionPane;
/*   48:     */ import javax.swing.JPanel;
/*   49:     */ import javax.swing.JProgressBar;
/*   50:     */ import javax.swing.JRadioButton;
/*   51:     */ import javax.swing.JScrollPane;
/*   52:     */ import javax.swing.JSplitPane;
/*   53:     */ import javax.swing.JTable;
/*   54:     */ import javax.swing.JTextArea;
/*   55:     */ import javax.swing.JTextField;
/*   56:     */ import javax.swing.JToolBar;
/*   57:     */ import javax.swing.ListSelectionModel;
/*   58:     */ import javax.swing.ProgressMonitor;
/*   59:     */ import javax.swing.SwingWorker;
/*   60:     */ import javax.swing.event.HyperlinkEvent;
/*   61:     */ import javax.swing.event.HyperlinkEvent.EventType;
/*   62:     */ import javax.swing.event.HyperlinkListener;
/*   63:     */ import javax.swing.event.ListSelectionEvent;
/*   64:     */ import javax.swing.event.ListSelectionListener;
/*   65:     */ import javax.swing.table.DefaultTableModel;
/*   66:     */ import javax.swing.table.JTableHeader;
/*   67:     */ import javax.swing.table.TableColumn;
/*   68:     */ import javax.swing.table.TableColumnModel;
/*   69:     */ import weka.core.Environment;
/*   70:     */ import weka.core.RepositoryIndexGenerator;
/*   71:     */ import weka.core.Utils;
/*   72:     */ import weka.core.Version;
/*   73:     */ import weka.core.WekaPackageManager;
/*   74:     */ import weka.core.logging.Logger;
/*   75:     */ import weka.core.logging.Logger.Level;
/*   76:     */ import weka.core.packageManagement.Dependency;
/*   77:     */ import weka.core.packageManagement.Package;
/*   78:     */ import weka.core.packageManagement.PackageConstraint;
/*   79:     */ import weka.core.packageManagement.VersionPackageConstraint;
/*   80:     */ 
/*   81:     */ public class PackageManager
/*   82:     */   extends JPanel
/*   83:     */ {
/*   84:     */   private static final long serialVersionUID = -7463821313750352385L;
/*   85:     */   protected static final String PACKAGE_COLUMN = "Package";
/*   86:     */   protected static final String CATEGORY_COLUMN = "Category";
/*   87:     */   protected static final String INSTALLED_COLUMN = "Installed version";
/*   88:     */   protected static final String REPOSITORY_COLUMN = "Repository version";
/*   89:     */   protected static final String LOADED_COLUMN = "Loaded";
/*   90:  83 */   protected JTable m_table = new ETable();
/*   91:     */   protected JSplitPane m_splitP;
/*   92:     */   protected JEditorPane m_infoPane;
/*   93:  93 */   protected JRadioButton m_installedBut = new JRadioButton("Installed");
/*   94:  96 */   protected JRadioButton m_availableBut = new JRadioButton("Available");
/*   95:  99 */   protected JRadioButton m_allBut = new JRadioButton("All");
/*   96: 102 */   protected JButton m_installBut = new JButton("Install");
/*   97: 103 */   protected JCheckBox m_forceBut = new JCheckBox("Ignore dependencies/conflicts");
/*   98: 107 */   protected JButton m_uninstallBut = new JButton("Uninstall");
/*   99: 110 */   protected JButton m_refreshCacheBut = new JButton("Refresh repository cache");
/*  100: 113 */   protected JButton m_toggleLoad = new JButton("Toggle load");
/*  101: 115 */   protected JProgressBar m_progress = new JProgressBar(0, 100);
/*  102: 116 */   protected JLabel m_detailLabel = new JLabel();
/*  103:     */   protected JButton m_backB;
/*  104: 119 */   protected LinkedList<URL> m_browserHistory = new LinkedList();
/*  105:     */   protected static final String BROWSER_HOME = "http://www.cs.waikato.ac.nz/ml/weka/index_home_pm.html";
/*  106:     */   protected JButton m_homeB;
/*  107:     */   protected JToolBar m_browserTools;
/*  108:     */   protected JLabel m_newPackagesAvailableL;
/*  109:     */   protected DefaultTableModel m_model;
/*  110:     */   protected Map<String, List<Object>> m_packageLookupInfo;
/*  111:     */   protected List<Package> m_allPackages;
/*  112:     */   protected List<Package> m_installedPackages;
/*  113:     */   protected List<Package> m_availablePackages;
/*  114: 136 */   protected Map<String, String> m_packageDescriptions = new HashMap();
/*  115: 138 */   protected List<Package> m_searchResults = new ArrayList();
/*  116: 139 */   protected JTextField m_searchField = new JTextField(15);
/*  117: 140 */   protected JLabel m_searchHitsLab = new JLabel("");
/*  118: 143 */   protected int m_sortColumn = 0;
/*  119: 146 */   protected boolean m_reverseSort = false;
/*  120: 149 */   protected JButton m_unofficialBut = new JButton("File/URL");
/*  121: 152 */   protected FileEnvironmentField m_unofficialChooser = new FileEnvironmentField("File/URL", Environment.getSystemWide());
/*  122: 154 */   protected JFrame m_unofficialFrame = null;
/*  123: 156 */   public static boolean s_atLeastOnePackageUpgradeHasOccurredInThisSession = false;
/*  124: 159 */   protected Comparator<Package> m_packageComparator = new Comparator()
/*  125:     */   {
/*  126:     */     public int compare(Package o1, Package o2)
/*  127:     */     {
/*  128: 164 */       String meta1 = "";
/*  129: 165 */       String meta2 = "";
/*  130: 166 */       if (PackageManager.this.m_sortColumn == 0)
/*  131:     */       {
/*  132: 167 */         meta1 = o1.getName();
/*  133: 168 */         meta2 = o2.getName();
/*  134:     */       }
/*  135:     */       else
/*  136:     */       {
/*  137: 170 */         if (o1.getPackageMetaDataElement("Category") != null) {
/*  138: 171 */           meta1 = o1.getPackageMetaDataElement("Category").toString();
/*  139:     */         }
/*  140: 174 */         if (o2.getPackageMetaDataElement("Category") != null) {
/*  141: 175 */           meta2 = o2.getPackageMetaDataElement("Category").toString();
/*  142:     */         }
/*  143:     */       }
/*  144: 179 */       int result = meta1.compareTo(meta2);
/*  145: 180 */       if (PackageManager.this.m_reverseSort) {
/*  146: 181 */         result = -result;
/*  147:     */       }
/*  148: 183 */       return result;
/*  149:     */     }
/*  150:     */   };
/*  151: 187 */   protected boolean m_installing = false;
/*  152:     */   
/*  153:     */   class ProgressPrintStream
/*  154:     */     extends PrintStream
/*  155:     */   {
/*  156:     */     private final PackageManager.Progressable m_listener;
/*  157:     */     
/*  158:     */     public ProgressPrintStream(PackageManager.Progressable listener)
/*  159:     */     {
/*  160: 195 */       super();
/*  161: 196 */       this.m_listener = listener;
/*  162:     */     }
/*  163:     */     
/*  164:     */     public void println(String string)
/*  165:     */     {
/*  166: 201 */       boolean messageOnly = false;
/*  167: 202 */       if (string.startsWith("%%"))
/*  168:     */       {
/*  169: 203 */         string = string.substring(2);
/*  170: 204 */         messageOnly = true;
/*  171:     */       }
/*  172: 206 */       if (!messageOnly)
/*  173:     */       {
/*  174: 207 */         System.out.println(string);
/*  175: 208 */         this.m_listener.makeProgress(string);
/*  176:     */       }
/*  177:     */       else
/*  178:     */       {
/*  179: 210 */         this.m_listener.makeProgressMessageOnly(string);
/*  180:     */       }
/*  181:     */     }
/*  182:     */     
/*  183:     */     public void println(Object obj)
/*  184:     */     {
/*  185: 216 */       println(obj.toString());
/*  186:     */     }
/*  187:     */     
/*  188:     */     public void print(String string)
/*  189:     */     {
/*  190: 221 */       boolean messageOnly = false;
/*  191: 222 */       if (string.startsWith("%%"))
/*  192:     */       {
/*  193: 223 */         string = string.substring(2);
/*  194: 224 */         messageOnly = true;
/*  195:     */       }
/*  196: 227 */       if (!messageOnly)
/*  197:     */       {
/*  198: 228 */         System.out.print(string);
/*  199: 229 */         this.m_listener.makeProgress(string);
/*  200:     */       }
/*  201:     */       else
/*  202:     */       {
/*  203: 231 */         this.m_listener.makeProgressMessageOnly(string);
/*  204:     */       }
/*  205:     */     }
/*  206:     */     
/*  207:     */     public void print(Object obj)
/*  208:     */     {
/*  209: 237 */       print(obj.toString());
/*  210:     */     }
/*  211:     */   }
/*  212:     */   
/*  213:     */   static abstract interface Progressable
/*  214:     */   {
/*  215:     */     public abstract void makeProgress(String paramString);
/*  216:     */     
/*  217:     */     public abstract void makeProgressMessageOnly(String paramString);
/*  218:     */   }
/*  219:     */   
/*  220:     */   class EstablishCache
/*  221:     */     extends SwingWorker<Void, Void>
/*  222:     */     implements PackageManager.Progressable
/*  223:     */   {
/*  224: 248 */     private int m_progressCount = 0;
/*  225: 249 */     private Exception m_error = null;
/*  226:     */     private ProgressMonitor m_progress;
/*  227:     */     
/*  228:     */     EstablishCache() {}
/*  229:     */     
/*  230:     */     public void makeProgress(String progressMessage)
/*  231:     */     {
/*  232: 255 */       this.m_progress.setNote(progressMessage);
/*  233: 256 */       this.m_progressCount += 1;
/*  234: 257 */       this.m_progress.setProgress(this.m_progressCount);
/*  235:     */     }
/*  236:     */     
/*  237:     */     public void makeProgressMessageOnly(String progressMessage)
/*  238:     */     {
/*  239: 262 */       this.m_progress.setNote(progressMessage);
/*  240:     */     }
/*  241:     */     
/*  242:     */     public Void doInBackground()
/*  243:     */     {
/*  244: 267 */       int numPackages = WekaPackageManager.numRepositoryPackages();
/*  245: 268 */       if (numPackages < 0) {
/*  246: 274 */         numPackages = 100;
/*  247:     */       }
/*  248: 276 */       this.m_progress = new ProgressMonitor(PackageManager.this, "Establising cache...", "", 0, numPackages);
/*  249:     */       
/*  250:     */ 
/*  251: 279 */       PackageManager.ProgressPrintStream pps = new PackageManager.ProgressPrintStream(PackageManager.this, this);
/*  252: 280 */       this.m_error = WekaPackageManager.establishCacheIfNeeded(new PrintStream[] { pps });
/*  253:     */       
/*  254: 282 */       PackageManager.this.m_cacheEstablished = true;
/*  255: 283 */       return null;
/*  256:     */     }
/*  257:     */     
/*  258:     */     public void done()
/*  259:     */     {
/*  260: 288 */       this.m_progress.close();
/*  261: 289 */       if (this.m_error != null) {
/*  262: 290 */         PackageManager.this.displayErrorDialog("There was a problem establishing the package\nmeta data cache. We'll try to use the repositorydirectly.", this.m_error);
/*  263:     */       }
/*  264:     */     }
/*  265:     */   }
/*  266:     */   
/*  267:     */   class CheckForNewPackages
/*  268:     */     extends SwingWorker<Void, Void>
/*  269:     */   {
/*  270:     */     CheckForNewPackages() {}
/*  271:     */     
/*  272:     */     public Void doInBackground()
/*  273:     */     {
/*  274: 301 */       Map<String, String> localPackageNameList = WekaPackageManager.getPackageList(true);
/*  275: 304 */       if (localPackageNameList == null) {
/*  276: 306 */         return null;
/*  277:     */       }
/*  278: 309 */       Map<String, String> repositoryPackageNameList = WekaPackageManager.getPackageList(false);
/*  279: 312 */       if (repositoryPackageNameList == null) {
/*  280: 314 */         return null;
/*  281:     */       }
/*  282: 317 */       if (repositoryPackageNameList.keySet().size() < localPackageNameList.keySet().size())
/*  283:     */       {
/*  284: 321 */         PackageManager.RefreshCache r = new PackageManager.RefreshCache(PackageManager.this);
/*  285: 322 */         r.execute();
/*  286:     */         
/*  287: 324 */         return null;
/*  288:     */       }
/*  289: 327 */       StringBuffer newPackagesBuff = new StringBuffer();
/*  290: 328 */       StringBuffer updatedPackagesBuff = new StringBuffer();
/*  291: 330 */       for (String s : repositoryPackageNameList.keySet()) {
/*  292: 331 */         if (!localPackageNameList.containsKey(s)) {
/*  293: 332 */           newPackagesBuff.append(s + "<br>");
/*  294:     */         }
/*  295:     */       }
/*  296: 336 */       for (String localPackage : localPackageNameList.keySet())
/*  297:     */       {
/*  298: 337 */         String localVersion = (String)localPackageNameList.get(localPackage);
/*  299:     */         
/*  300: 339 */         String repoVersion = (String)repositoryPackageNameList.get(localPackage);
/*  301: 340 */         if (repoVersion != null) {
/*  302: 345 */           if (!localVersion.equals(repoVersion)) {
/*  303: 346 */             updatedPackagesBuff.append(localPackage + " (" + repoVersion + ")<br>");
/*  304:     */           }
/*  305:     */         }
/*  306:     */       }
/*  307: 351 */       if ((newPackagesBuff.length() > 0) || (updatedPackagesBuff.length() > 0))
/*  308:     */       {
/*  309: 352 */         String information = "<html><font size=-2>There are new and/or updated packages available on the server (do a cache refresh for more information):";
/*  310: 355 */         if (newPackagesBuff.length() > 0) {
/*  311: 356 */           information = information + "<br><br><b>New:</b><br>" + newPackagesBuff.toString();
/*  312:     */         }
/*  313: 358 */         if (updatedPackagesBuff.length() > 0) {
/*  314: 359 */           information = information + "<br><br><b>Updated:</b><br>" + updatedPackagesBuff.toString() + "<br><br>";
/*  315:     */         }
/*  316: 363 */         information = information + "</font></html>";
/*  317: 364 */         PackageManager.this.m_newPackagesAvailableL.setToolTipText(information);
/*  318: 365 */         PackageManager.this.m_browserTools.add(PackageManager.this.m_newPackagesAvailableL);
/*  319:     */         
/*  320: 367 */         PackageManager.this.m_browserTools.revalidate();
/*  321:     */       }
/*  322: 370 */       return null;
/*  323:     */     }
/*  324:     */   }
/*  325:     */   
/*  326:     */   class RefreshCache
/*  327:     */     extends SwingWorker<Void, Void>
/*  328:     */     implements PackageManager.Progressable
/*  329:     */   {
/*  330: 375 */     private int m_progressCount = 0;
/*  331: 376 */     private Exception m_error = null;
/*  332:     */     
/*  333:     */     RefreshCache() {}
/*  334:     */     
/*  335:     */     public void makeProgress(String progressMessage)
/*  336:     */     {
/*  337: 380 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  338: 381 */       if (progressMessage.startsWith("[Default"))
/*  339:     */       {
/*  340: 384 */         String kbs = progressMessage.replace("[DefaultPackageManager] downloaded ", "");
/*  341:     */         
/*  342: 386 */         kbs = kbs.replace(" KB", "");
/*  343: 387 */         this.m_progressCount = Integer.parseInt(kbs);
/*  344:     */       }
/*  345:     */       else
/*  346:     */       {
/*  347: 389 */         this.m_progressCount += 1;
/*  348:     */       }
/*  349: 391 */       PackageManager.this.m_progress.setValue(this.m_progressCount);
/*  350:     */     }
/*  351:     */     
/*  352:     */     public void makeProgressMessageOnly(String progressMessage)
/*  353:     */     {
/*  354: 396 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  355:     */     }
/*  356:     */     
/*  357:     */     public Void doInBackground()
/*  358:     */     {
/*  359: 401 */       PackageManager.this.m_cacheRefreshInProgress = true;
/*  360: 402 */       int progressUpper = WekaPackageManager.repoZipArchiveSize();
/*  361: 403 */       if (progressUpper == -1) {
/*  362: 405 */         progressUpper = WekaPackageManager.numRepositoryPackages();
/*  363:     */       }
/*  364: 408 */       if (progressUpper < 0) {
/*  365: 414 */         progressUpper = 100;
/*  366:     */       }
/*  367: 418 */       PackageManager.this.m_progress.setMaximum(progressUpper);
/*  368: 419 */       PackageManager.this.m_refreshCacheBut.setEnabled(false);
/*  369: 420 */       PackageManager.this.m_installBut.setEnabled(false);
/*  370: 421 */       PackageManager.this.m_unofficialBut.setEnabled(false);
/*  371: 422 */       PackageManager.this.m_installedBut.setEnabled(false);
/*  372: 423 */       PackageManager.this.m_availableBut.setEnabled(false);
/*  373: 424 */       PackageManager.this.m_allBut.setEnabled(false);
/*  374: 425 */       PackageManager.ProgressPrintStream pps = new PackageManager.ProgressPrintStream(PackageManager.this, this);
/*  375: 426 */       this.m_error = WekaPackageManager.refreshCache(new PrintStream[] { pps });
/*  376: 427 */       PackageManager.this.getAllPackages();
/*  377: 428 */       return null;
/*  378:     */     }
/*  379:     */     
/*  380:     */     public void done()
/*  381:     */     {
/*  382: 433 */       PackageManager.this.m_progress.setValue(PackageManager.this.m_progress.getMinimum());
/*  383: 434 */       if (this.m_error != null)
/*  384:     */       {
/*  385: 435 */         PackageManager.this.displayErrorDialog("There was a problem refreshing the package\nmeta data cache. We'll try to use the repositorydirectly.", this.m_error);
/*  386:     */         
/*  387:     */ 
/*  388: 438 */         PackageManager.this.m_detailLabel.setText("");
/*  389:     */       }
/*  390:     */       else
/*  391:     */       {
/*  392: 440 */         PackageManager.this.m_detailLabel.setText("Cache refresh completed");
/*  393:     */       }
/*  394: 443 */       PackageManager.this.m_installBut.setEnabled(!WekaPackageManager.m_offline);
/*  395: 444 */       PackageManager.this.m_unofficialBut.setEnabled(true);
/*  396: 445 */       PackageManager.this.m_refreshCacheBut.setEnabled(!WekaPackageManager.m_offline);
/*  397: 446 */       PackageManager.this.m_installedBut.setEnabled(true);
/*  398: 447 */       PackageManager.this.m_availableBut.setEnabled(true);
/*  399: 448 */       PackageManager.this.m_allBut.setEnabled(true);
/*  400:     */       
/*  401: 450 */       PackageManager.this.m_availablePackages = null;
/*  402: 451 */       PackageManager.this.updateTable();
/*  403:     */       try
/*  404:     */       {
/*  405: 454 */         PackageManager.this.m_browserTools.remove(PackageManager.this.m_newPackagesAvailableL);
/*  406: 455 */         PackageManager.this.m_browserTools.revalidate();
/*  407:     */       }
/*  408:     */       catch (Exception ex) {}
/*  409: 459 */       PackageManager.this.m_cacheRefreshInProgress = false;
/*  410:     */     }
/*  411:     */   }
/*  412:     */   
/*  413:     */   private void pleaseCloseAppWindowsPopUp()
/*  414:     */   {
/*  415: 464 */     if (!Utils.getDontShowDialog("weka.gui.PackageManager.PleaseCloseApplicationWindows"))
/*  416:     */     {
/*  417: 466 */       JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  418: 467 */       Object[] stuff = new Object[2];
/*  419: 468 */       stuff[0] = "Please close any open Weka application windows\n(Explorer, Experimenter, KnowledgeFlow, SimpleCLI)\nbefore proceeding.\n";
/*  420:     */       
/*  421:     */ 
/*  422:     */ 
/*  423: 472 */       stuff[1] = dontShow;
/*  424:     */       
/*  425: 474 */       JOptionPane.showMessageDialog(this, stuff, "Weka Package Manager", 0);
/*  426: 477 */       if (dontShow.isSelected()) {
/*  427:     */         try
/*  428:     */         {
/*  429: 479 */           Utils.setDontShowDialog("weka.gui.PackageManager.PleaseCloseApplicationWindows");
/*  430:     */         }
/*  431:     */         catch (Exception ex) {}
/*  432:     */       }
/*  433:     */     }
/*  434:     */   }
/*  435:     */   
/*  436:     */   private void toggleLoadStatusRequiresRestartPopUp()
/*  437:     */   {
/*  438: 489 */     if (!Utils.getDontShowDialog("weka.gui.PackageManager.ToggleLoadStatusRequiresRestart"))
/*  439:     */     {
/*  440: 491 */       JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  441: 492 */       Object[] stuff = new Object[2];
/*  442: 493 */       stuff[0] = "Changing a package's load status will require a restart for the change to take affect\n";
/*  443:     */       
/*  444:     */ 
/*  445: 496 */       stuff[1] = dontShow;
/*  446:     */       
/*  447: 498 */       JOptionPane.showMessageDialog(this, stuff, "Weka Package Manager", 0);
/*  448: 501 */       if (dontShow.isSelected()) {
/*  449:     */         try
/*  450:     */         {
/*  451: 503 */           Utils.setDontShowDialog("weka.gui.PackageManager.ToggleLoadStatusRequiresRestart");
/*  452:     */         }
/*  453:     */         catch (Exception ex) {}
/*  454:     */       }
/*  455:     */     }
/*  456:     */   }
/*  457:     */   
/*  458:     */   class UninstallTask
/*  459:     */     extends SwingWorker<Void, Void>
/*  460:     */     implements PackageManager.Progressable
/*  461:     */   {
/*  462:     */     private List<String> m_packageNamesToUninstall;
/*  463: 517 */     private final List<String> m_unsuccessfulUninstalls = new ArrayList();
/*  464: 520 */     private int m_progressCount = 0;
/*  465:     */     
/*  466:     */     UninstallTask() {}
/*  467:     */     
/*  468:     */     public void setPackages(List<String> packageNames)
/*  469:     */     {
/*  470: 523 */       this.m_packageNamesToUninstall = packageNames;
/*  471:     */     }
/*  472:     */     
/*  473:     */     public void makeProgress(String progressMessage)
/*  474:     */     {
/*  475: 528 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  476: 529 */       this.m_progressCount += 1;
/*  477: 530 */       PackageManager.this.m_progress.setValue(this.m_progressCount);
/*  478: 531 */       if (this.m_progressCount == PackageManager.this.m_progress.getMaximum()) {
/*  479: 532 */         PackageManager.this.m_progress.setMaximum(this.m_progressCount + 5);
/*  480:     */       }
/*  481:     */     }
/*  482:     */     
/*  483:     */     public void makeProgressMessageOnly(String progressMessage)
/*  484:     */     {
/*  485: 538 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  486:     */     }
/*  487:     */     
/*  488:     */     public Void doInBackground()
/*  489:     */     {
/*  490: 543 */       PackageManager.this.m_installing = true;
/*  491: 544 */       PackageManager.this.m_installBut.setEnabled(false);
/*  492: 545 */       PackageManager.this.m_unofficialBut.setEnabled(false);
/*  493: 546 */       PackageManager.this.m_uninstallBut.setEnabled(false);
/*  494: 547 */       PackageManager.this.m_refreshCacheBut.setEnabled(false);
/*  495: 548 */       PackageManager.this.m_toggleLoad.setEnabled(false);
/*  496: 549 */       PackageManager.this.m_availableBut.setEnabled(false);
/*  497: 550 */       PackageManager.this.m_allBut.setEnabled(false);
/*  498: 551 */       PackageManager.this.m_installedBut.setEnabled(false);
/*  499:     */       
/*  500: 553 */       PackageManager.ProgressPrintStream pps = new PackageManager.ProgressPrintStream(PackageManager.this, this);
/*  501: 554 */       PackageManager.this.m_progress.setMaximum(this.m_packageNamesToUninstall.size() * 5);
/*  502: 556 */       for (int zz = 0; zz < this.m_packageNamesToUninstall.size(); zz++)
/*  503:     */       {
/*  504: 558 */         String packageName = (String)this.m_packageNamesToUninstall.get(zz);
/*  505:     */         
/*  506: 560 */         boolean explorerPropertiesExist = WekaPackageManager.installedPackageResourceExists(packageName, "Explorer.props");
/*  507: 564 */         if (!PackageManager.this.m_forceBut.isSelected())
/*  508:     */         {
/*  509: 565 */           List<Package> compromised = new ArrayList();
/*  510:     */           List<Package> installedPackages;
/*  511:     */           try
/*  512:     */           {
/*  513: 570 */             installedPackages = WekaPackageManager.getInstalledPackages();
/*  514:     */           }
/*  515:     */           catch (Exception e)
/*  516:     */           {
/*  517: 572 */             e.printStackTrace();
/*  518: 573 */             PackageManager.this.displayErrorDialog("Can't determine which packages are installed!", e);
/*  519:     */             
/*  520:     */ 
/*  521: 576 */             this.m_unsuccessfulUninstalls.add(packageName);
/*  522: 577 */             continue;
/*  523:     */           }
/*  524: 579 */           for (Iterator i$ = installedPackages.iterator(); i$.hasNext();)
/*  525:     */           {
/*  526: 579 */             p = (Package)i$.next();
/*  527:     */             List<Dependency> tempDeps;
/*  528:     */             try
/*  529:     */             {
/*  530: 582 */               tempDeps = p.getDependencies();
/*  531:     */             }
/*  532:     */             catch (Exception e)
/*  533:     */             {
/*  534: 584 */               e.printStackTrace();
/*  535: 585 */               PackageManager.this.displayErrorDialog("Problem determining dependencies for package : " + p.getName(), e);
/*  536:     */               
/*  537:     */ 
/*  538:     */ 
/*  539: 589 */               this.m_unsuccessfulUninstalls.add(packageName);
/*  540:     */             }
/*  541: 590 */             continue;
/*  542: 593 */             for (Dependency d : tempDeps) {
/*  543: 594 */               if (d.getTarget().getPackage().getName().equals(packageName))
/*  544:     */               {
/*  545: 596 */                 compromised.add(p);
/*  546: 597 */                 break;
/*  547:     */               }
/*  548:     */             }
/*  549:     */           }
/*  550:     */           Package p;
/*  551: 602 */           if (compromised.size() > 0)
/*  552:     */           {
/*  553: 603 */             StringBuffer message = new StringBuffer();
/*  554: 604 */             message.append("The following installed packages depend on " + packageName + " :\n\n");
/*  555: 606 */             for (Package p : compromised) {
/*  556: 607 */               message.append("\t" + p.getName() + "\n");
/*  557:     */             }
/*  558: 610 */             message.append("\nDo you wish to proceed?");
/*  559: 611 */             int result = JOptionPane.showConfirmDialog(PackageManager.this, message.toString(), "Weka Package Manager", 0);
/*  560: 616 */             if (result == 1) {
/*  561:     */               continue;
/*  562:     */             }
/*  563:     */           }
/*  564:     */         }
/*  565:     */         try
/*  566:     */         {
/*  567: 626 */           if (explorerPropertiesExist) {
/*  568: 628 */             WekaPackageManager.removeExplorerProps(packageName);
/*  569:     */           }
/*  570: 630 */           WekaPackageManager.uninstallPackage(packageName, true, new PrintStream[] { pps });
/*  571:     */         }
/*  572:     */         catch (Exception e)
/*  573:     */         {
/*  574: 633 */           e.printStackTrace();
/*  575: 634 */           PackageManager.this.displayErrorDialog("Unable to uninstall package: " + packageName, e);
/*  576:     */           
/*  577: 636 */           this.m_unsuccessfulUninstalls.add(packageName);
/*  578:     */         }
/*  579:     */       }
/*  580: 641 */       WekaPackageManager.refreshGOEProperties();
/*  581:     */       
/*  582:     */ 
/*  583: 644 */       return null;
/*  584:     */     }
/*  585:     */     
/*  586:     */     public void done()
/*  587:     */     {
/*  588: 649 */       PackageManager.this.m_progress.setValue(PackageManager.this.m_progress.getMinimum());
/*  589: 650 */       if (this.m_unsuccessfulUninstalls.size() == 0)
/*  590:     */       {
/*  591: 651 */         PackageManager.this.m_detailLabel.setText("Packages removed successfully.");
/*  592: 653 */         if (!Utils.getDontShowDialog("weka.gui.PackageManager.RestartAfterUninstall"))
/*  593:     */         {
/*  594: 655 */           JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  595: 656 */           Object[] stuff = new Object[2];
/*  596: 657 */           stuff[0] = "Weka might need to be restarted for\nthe changes to come into effect.\n";
/*  597:     */           
/*  598:     */ 
/*  599: 660 */           stuff[1] = dontShow;
/*  600:     */           
/*  601: 662 */           JOptionPane.showMessageDialog(PackageManager.this, stuff, "Weka Package Manager", 0);
/*  602: 665 */           if (dontShow.isSelected()) {
/*  603:     */             try
/*  604:     */             {
/*  605: 667 */               Utils.setDontShowDialog("weka.gui.PackageManager.RestartAfterUninstall");
/*  606:     */             }
/*  607:     */             catch (Exception ex) {}
/*  608:     */           }
/*  609:     */         }
/*  610:     */       }
/*  611:     */       else
/*  612:     */       {
/*  613: 675 */         StringBuffer failedPackageNames = new StringBuffer();
/*  614: 676 */         for (String p : this.m_unsuccessfulUninstalls) {
/*  615: 677 */           failedPackageNames.append(p + "\n");
/*  616:     */         }
/*  617: 679 */         PackageManager.this.displayErrorDialog("The following package(s) could not be uninstalled\nfor some reason (check the log)\n" + failedPackageNames.toString(), "");
/*  618:     */         
/*  619:     */ 
/*  620:     */ 
/*  621: 683 */         PackageManager.this.m_detailLabel.setText("Finished uninstalling.");
/*  622:     */       }
/*  623: 686 */       PackageManager.this.m_unofficialBut.setEnabled(true);
/*  624: 687 */       PackageManager.this.m_refreshCacheBut.setEnabled(true);
/*  625: 688 */       PackageManager.this.m_availableBut.setEnabled(true);
/*  626: 689 */       PackageManager.this.m_allBut.setEnabled(true);
/*  627: 690 */       PackageManager.this.m_installedBut.setEnabled(true);
/*  628:     */       
/*  629:     */ 
/*  630: 693 */       PackageManager.this.m_installedPackages = null;
/*  631: 694 */       PackageManager.this.m_availablePackages = null;
/*  632:     */       
/*  633: 696 */       PackageManager.this.m_installing = false;
/*  634: 697 */       PackageManager.this.updateTable();
/*  635: 698 */       if (PackageManager.this.m_table.getSelectedRow() >= 0) {
/*  636: 701 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/*  637:     */       }
/*  638:     */     }
/*  639:     */   }
/*  640:     */   
/*  641:     */   class UnofficialInstallTask
/*  642:     */     extends SwingWorker<Void, Void>
/*  643:     */     implements PackageManager.Progressable
/*  644:     */   {
/*  645:     */     private String m_target;
/*  646: 710 */     private int m_progressCount = 0;
/*  647: 711 */     private boolean m_errorOccurred = false;
/*  648:     */     
/*  649:     */     UnofficialInstallTask() {}
/*  650:     */     
/*  651:     */     public void setTargetToInstall(String target)
/*  652:     */     {
/*  653: 714 */       this.m_target = target;
/*  654:     */     }
/*  655:     */     
/*  656:     */     public void makeProgress(String progressMessage)
/*  657:     */     {
/*  658: 719 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  659: 720 */       this.m_progressCount += 1;
/*  660: 721 */       PackageManager.this.m_progress.setValue(this.m_progressCount);
/*  661: 722 */       if (this.m_progressCount == PackageManager.this.m_progress.getMaximum()) {
/*  662: 723 */         PackageManager.this.m_progress.setMaximum(this.m_progressCount + 5);
/*  663:     */       }
/*  664:     */     }
/*  665:     */     
/*  666:     */     public void makeProgressMessageOnly(String progressMessage)
/*  667:     */     {
/*  668: 729 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  669:     */     }
/*  670:     */     
/*  671:     */     public Void doInBackground()
/*  672:     */     {
/*  673: 734 */       PackageManager.this.m_installing = true;
/*  674: 735 */       PackageManager.this.m_installBut.setEnabled(false);
/*  675: 736 */       PackageManager.this.m_uninstallBut.setEnabled(false);
/*  676: 737 */       PackageManager.this.m_refreshCacheBut.setEnabled(false);
/*  677: 738 */       PackageManager.this.m_toggleLoad.setEnabled(false);
/*  678: 739 */       PackageManager.this.m_unofficialBut.setEnabled(false);
/*  679: 740 */       PackageManager.this.m_availableBut.setEnabled(false);
/*  680: 741 */       PackageManager.this.m_allBut.setEnabled(false);
/*  681: 742 */       PackageManager.this.m_installedBut.setEnabled(false);
/*  682: 743 */       PackageManager.ProgressPrintStream pps = new PackageManager.ProgressPrintStream(PackageManager.this, this);
/*  683: 744 */       PackageManager.this.m_progress.setMaximum(30);
/*  684:     */       
/*  685: 746 */       Package installedPackage = null;
/*  686: 747 */       String toInstall = this.m_target;
/*  687:     */       try
/*  688:     */       {
/*  689: 749 */         toInstall = Environment.getSystemWide().substitute(this.m_target);
/*  690:     */       }
/*  691:     */       catch (Exception ex) {}
/*  692:     */       try
/*  693:     */       {
/*  694: 754 */         if ((toInstall.toLowerCase().startsWith("http://")) || (toInstall.toLowerCase().startsWith("https://")))
/*  695:     */         {
/*  696: 756 */           String packageName = WekaPackageManager.installPackageFromURL(new URL(toInstall), new PrintStream[] { pps });
/*  697:     */           
/*  698: 758 */           installedPackage = WekaPackageManager.getInstalledPackageInfo(packageName);
/*  699:     */         }
/*  700: 760 */         else if (toInstall.toLowerCase().endsWith(".zip"))
/*  701:     */         {
/*  702: 761 */           String packageName = WekaPackageManager.installPackageFromArchive(toInstall, new PrintStream[] { pps });
/*  703:     */           
/*  704: 763 */           installedPackage = WekaPackageManager.getInstalledPackageInfo(packageName);
/*  705:     */         }
/*  706:     */         else
/*  707:     */         {
/*  708: 766 */           PackageManager.this.displayErrorDialog("Unable to install package \nfrom " + toInstall + ". Unrecognized as a URL or zip archive.", (String)null);
/*  709:     */           
/*  710:     */ 
/*  711: 769 */           this.m_errorOccurred = true;
/*  712: 770 */           pps.close();
/*  713: 771 */           return null;
/*  714:     */         }
/*  715:     */       }
/*  716:     */       catch (Exception ex)
/*  717:     */       {
/*  718: 774 */         PackageManager.this.displayErrorDialog("Unable to install package \nfrom " + this.m_target + ". Check the log for error messages.", ex);
/*  719:     */         
/*  720: 776 */         this.m_errorOccurred = true;
/*  721: 777 */         return null;
/*  722:     */       }
/*  723: 780 */       if (installedPackage != null)
/*  724:     */       {
/*  725: 781 */         if (!Utils.getDontShowDialog("weka.gui.PackageManager.RestartAfterUpgrade"))
/*  726:     */         {
/*  727: 783 */           JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  728: 784 */           Object[] stuff = new Object[2];
/*  729: 785 */           stuff[0] = "Weka will need to be restared after installation for\nthe changes to come into effect.\n";
/*  730:     */           
/*  731:     */ 
/*  732: 788 */           stuff[1] = dontShow;
/*  733:     */           
/*  734: 790 */           JOptionPane.showMessageDialog(PackageManager.this, stuff, "Weka Package Manager", 0);
/*  735: 793 */           if (dontShow.isSelected()) {
/*  736:     */             try
/*  737:     */             {
/*  738: 795 */               Utils.setDontShowDialog("weka.gui.PackageManager.RestartAfterUpgrade");
/*  739:     */             }
/*  740:     */             catch (Exception ex) {}
/*  741:     */           }
/*  742:     */         }
/*  743:     */         try
/*  744:     */         {
/*  745: 804 */           File packageRoot = new File(WekaPackageManager.getPackageHome() + File.separator + installedPackage.getName());
/*  746:     */           
/*  747:     */ 
/*  748: 807 */           boolean loadCheck = WekaPackageManager.loadCheck(installedPackage, packageRoot, new PrintStream[] { pps });
/*  749: 810 */           if (!loadCheck) {
/*  750: 811 */             PackageManager.this.displayErrorDialog("Package was installed correctly but could not be loaded. Check log for details", (String)null);
/*  751:     */           }
/*  752:     */         }
/*  753:     */         catch (Exception ex)
/*  754:     */         {
/*  755: 815 */           PackageManager.this.displayErrorDialog("Unable to install package \nfrom " + this.m_target + ".", ex);
/*  756:     */           
/*  757: 817 */           this.m_errorOccurred = true;
/*  758:     */         }
/*  759:     */       }
/*  760: 830 */       return null;
/*  761:     */     }
/*  762:     */     
/*  763:     */     public void done()
/*  764:     */     {
/*  765: 835 */       PackageManager.this.m_progress.setValue(PackageManager.this.m_progress.getMinimum());
/*  766: 836 */       if (this.m_errorOccurred) {
/*  767: 837 */         PackageManager.this.m_detailLabel.setText("Problem installing - check log.");
/*  768:     */       } else {
/*  769: 839 */         PackageManager.this.m_detailLabel.setText("Package installed successfully.");
/*  770:     */       }
/*  771: 842 */       PackageManager.this.m_unofficialBut.setEnabled(true);
/*  772: 843 */       PackageManager.this.m_refreshCacheBut.setEnabled(!WekaPackageManager.m_offline);
/*  773: 844 */       PackageManager.this.m_availableBut.setEnabled(true);
/*  774: 845 */       PackageManager.this.m_allBut.setEnabled(true);
/*  775: 846 */       PackageManager.this.m_installedBut.setEnabled(true);
/*  776:     */       
/*  777:     */ 
/*  778: 849 */       PackageManager.this.m_installedPackages = null;
/*  779: 850 */       PackageManager.this.m_availablePackages = null;
/*  780:     */       
/*  781:     */ 
/*  782: 853 */       PackageManager.this.m_installing = false;
/*  783: 854 */       PackageManager.this.updateTable();
/*  784: 855 */       if (PackageManager.this.m_table.getSelectedRow() >= 0) {
/*  785: 858 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/*  786:     */       }
/*  787:     */     }
/*  788:     */   }
/*  789:     */   
/*  790:     */   class InstallTask
/*  791:     */     extends SwingWorker<Void, Void>
/*  792:     */     implements PackageManager.Progressable
/*  793:     */   {
/*  794:     */     private List<String> m_packageNamesToInstall;
/*  795:     */     private List<Object> m_versionsToInstall;
/*  796: 869 */     private final List<Package> m_unsuccessfulInstalls = new ArrayList();
/*  797: 872 */     private int m_progressCount = 0;
/*  798:     */     
/*  799:     */     InstallTask() {}
/*  800:     */     
/*  801:     */     public void setPackages(List<String> packagesToInstall)
/*  802:     */     {
/*  803: 875 */       this.m_packageNamesToInstall = packagesToInstall;
/*  804:     */     }
/*  805:     */     
/*  806:     */     public void setVersions(List<Object> versionsToInstall)
/*  807:     */     {
/*  808: 879 */       this.m_versionsToInstall = versionsToInstall;
/*  809:     */     }
/*  810:     */     
/*  811:     */     public void makeProgress(String progressMessage)
/*  812:     */     {
/*  813: 884 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  814: 885 */       this.m_progressCount += 1;
/*  815: 886 */       PackageManager.this.m_progress.setValue(this.m_progressCount);
/*  816: 887 */       if (this.m_progressCount == PackageManager.this.m_progress.getMaximum()) {
/*  817: 888 */         PackageManager.this.m_progress.setMaximum(this.m_progressCount + 5);
/*  818:     */       }
/*  819:     */     }
/*  820:     */     
/*  821:     */     public void makeProgressMessageOnly(String progressMessage)
/*  822:     */     {
/*  823: 894 */       PackageManager.this.m_detailLabel.setText(progressMessage);
/*  824:     */     }
/*  825:     */     
/*  826:     */     public Void doInBackground()
/*  827:     */     {
/*  828: 902 */       PackageManager.this.m_installing = true;
/*  829: 903 */       PackageManager.this.m_installBut.setEnabled(false);
/*  830: 904 */       PackageManager.this.m_unofficialBut.setEnabled(true);
/*  831: 905 */       PackageManager.this.m_uninstallBut.setEnabled(false);
/*  832: 906 */       PackageManager.this.m_refreshCacheBut.setEnabled(false);
/*  833: 907 */       PackageManager.this.m_toggleLoad.setEnabled(false);
/*  834: 908 */       PackageManager.this.m_availableBut.setEnabled(false);
/*  835: 909 */       PackageManager.this.m_allBut.setEnabled(false);
/*  836: 910 */       PackageManager.this.m_installedBut.setEnabled(false);
/*  837: 911 */       PackageManager.ProgressPrintStream pps = new PackageManager.ProgressPrintStream(PackageManager.this, this);
/*  838: 912 */       PackageManager.this.m_progress.setMaximum(this.m_packageNamesToInstall.size() * 30);
/*  839: 914 */       for (int zz = 0; zz < this.m_packageNamesToInstall.size(); zz++)
/*  840:     */       {
/*  841: 915 */         Package packageToInstall = null;
/*  842: 916 */         String packageName = (String)this.m_packageNamesToInstall.get(zz);
/*  843: 917 */         Object versionToInstall = this.m_versionsToInstall.get(zz);
/*  844:     */         try
/*  845:     */         {
/*  846: 919 */           packageToInstall = WekaPackageManager.getRepositoryPackageInfo(packageName, versionToInstall.toString());
/*  847:     */         }
/*  848:     */         catch (Exception e)
/*  849:     */         {
/*  850: 923 */           e.printStackTrace();
/*  851: 924 */           PackageManager.this.displayErrorDialog("Unable to obtain package info for package: " + packageName, e);
/*  852:     */           
/*  853:     */ 
/*  854: 927 */           this.m_unsuccessfulInstalls.add(packageToInstall);
/*  855: 928 */           continue;
/*  856:     */         }
/*  857: 932 */         Object specialInstallMessage = packageToInstall.getPackageMetaDataElement("MessageToDisplayOnInstallation");
/*  858: 935 */         if ((specialInstallMessage != null) && (specialInstallMessage.toString().length() > 0))
/*  859:     */         {
/*  860: 937 */           String siM = specialInstallMessage.toString();
/*  861:     */           try
/*  862:     */           {
/*  863: 939 */             siM = Environment.getSystemWide().substitute(siM);
/*  864:     */           }
/*  865:     */           catch (Exception ex) {}
/*  866: 943 */           JOptionPane.showMessageDialog(PackageManager.this, packageToInstall + "\n\n" + siM, "Weka Package Manager", 0);
/*  867:     */         }
/*  868: 947 */         if (!PackageManager.this.m_forceBut.isSelected())
/*  869:     */         {
/*  870:     */           try
/*  871:     */           {
/*  872: 949 */             if (!packageToInstall.isCompatibleBaseSystem())
/*  873:     */             {
/*  874: 950 */               List<Dependency> baseSysDep = packageToInstall.getBaseSystemDependency();
/*  875:     */               
/*  876: 952 */               StringBuffer depList = new StringBuffer();
/*  877: 953 */               for (Dependency bd : baseSysDep) {
/*  878: 954 */                 depList.append(bd.getTarget().toString() + " ");
/*  879:     */               }
/*  880: 957 */               JOptionPane.showMessageDialog(PackageManager.this, "Unable to install package \n" + packageName + " because it requires" + "\n" + depList.toString(), "Weka Package Manager", 0);
/*  881:     */               
/*  882:     */ 
/*  883:     */ 
/*  884:     */ 
/*  885:     */ 
/*  886: 963 */               this.m_unsuccessfulInstalls.add(packageToInstall);
/*  887: 964 */               continue;
/*  888:     */             }
/*  889:     */           }
/*  890:     */           catch (Exception e)
/*  891:     */           {
/*  892: 967 */             e.printStackTrace();
/*  893: 968 */             PackageManager.this.displayErrorDialog("Problem determining dependency on base system for package: " + packageName, e);
/*  894:     */             
/*  895:     */ 
/*  896: 971 */             this.m_unsuccessfulInstalls.add(packageToInstall);
/*  897: 972 */             continue;
/*  898:     */           }
/*  899: 976 */           if (packageToInstall.isInstalled())
/*  900:     */           {
/*  901: 977 */             Package installedVersion = null;
/*  902:     */             try
/*  903:     */             {
/*  904: 979 */               installedVersion = WekaPackageManager.getInstalledPackageInfo(packageName);
/*  905:     */             }
/*  906:     */             catch (Exception e)
/*  907:     */             {
/*  908: 982 */               e.printStackTrace();
/*  909: 983 */               PackageManager.this.displayErrorDialog("Problem obtaining package info for package: " + packageName, e);
/*  910:     */               
/*  911:     */ 
/*  912: 986 */               this.m_unsuccessfulInstalls.add(packageToInstall);
/*  913: 987 */               continue;
/*  914:     */             }
/*  915: 990 */             if (!packageToInstall.equals(installedVersion))
/*  916:     */             {
/*  917: 991 */               int result = JOptionPane.showConfirmDialog(PackageManager.this, "Package " + installedVersion + " is already installed. Replace with " + packageToInstall + "?", "Weka Package Manager", 0);
/*  918: 996 */               if (result == 1)
/*  919:     */               {
/*  920: 999 */                 this.m_unsuccessfulInstalls.add(packageToInstall);
/*  921:1000 */                 continue;
/*  922:     */               }
/*  923:1003 */               if (!Utils.getDontShowDialog("weka.gui.PackageManager.RestartAfterUpgrade"))
/*  924:     */               {
/*  925:1005 */                 JCheckBox dontShow = new JCheckBox("Do not show this message again");
/*  926:     */                 
/*  927:1007 */                 Object[] stuff = new Object[2];
/*  928:1008 */                 stuff[0] = "Weka will need to be restared after installation for\nthe changes to come into effect.\n";
/*  929:     */                 
/*  930:     */ 
/*  931:1011 */                 stuff[1] = dontShow;
/*  932:     */                 
/*  933:1013 */                 JOptionPane.showMessageDialog(PackageManager.this, stuff, "Weka Package Manager", 0);
/*  934:1016 */                 if (dontShow.isSelected()) {
/*  935:     */                   try
/*  936:     */                   {
/*  937:1018 */                     Utils.setDontShowDialog("weka.gui.PackageManager.RestartAfterUpgrade");
/*  938:     */                   }
/*  939:     */                   catch (Exception ex) {}
/*  940:     */                 }
/*  941:     */               }
/*  942:     */             }
/*  943:     */             else
/*  944:     */             {
/*  945:1026 */               int result = JOptionPane.showConfirmDialog(PackageManager.this, "Package " + installedVersion + " is already installed. Install again?", "Weka Package Manager", 0);
/*  946:1030 */               if (result == 1)
/*  947:     */               {
/*  948:1033 */                 this.m_unsuccessfulInstalls.add(packageToInstall);
/*  949:1034 */                 continue;
/*  950:     */               }
/*  951:     */             }
/*  952:     */           }
/*  953:1041 */           Map<String, List<Dependency>> conflicts = new HashMap();
/*  954:     */           
/*  955:1043 */           List<Dependency> dependencies = null;
/*  956:     */           try
/*  957:     */           {
/*  958:1045 */             dependencies = WekaPackageManager.getAllDependenciesForPackage(packageToInstall, conflicts);
/*  959:     */           }
/*  960:     */           catch (Exception e)
/*  961:     */           {
/*  962:1049 */             e.printStackTrace();
/*  963:1050 */             PackageManager.this.displayErrorDialog("Problem determinining dependencies for package: " + packageToInstall.getName(), e);
/*  964:     */             
/*  965:     */ 
/*  966:     */ 
/*  967:1054 */             this.m_unsuccessfulInstalls.add(packageToInstall);
/*  968:1055 */             continue;
/*  969:     */           }
/*  970:1058 */           if (conflicts.size() > 0)
/*  971:     */           {
/*  972:1059 */             StringBuffer message = new StringBuffer();
/*  973:1060 */             message.append("Package " + packageName + " requires the following packages:\n\n");
/*  974:     */             
/*  975:1062 */             Iterator<Dependency> depI = dependencies.iterator();
/*  976:1063 */             while (depI.hasNext())
/*  977:     */             {
/*  978:1064 */               Dependency d = (Dependency)depI.next();
/*  979:1065 */               message.append("\t" + d + "\n");
/*  980:     */             }
/*  981:1068 */             message.append("\nThere are conflicting dependencies:\n\n");
/*  982:1069 */             Set<String> pNames = conflicts.keySet();
/*  983:1070 */             Iterator<String> pNameI = pNames.iterator();
/*  984:1071 */             while (pNameI.hasNext())
/*  985:     */             {
/*  986:1072 */               String pName = (String)pNameI.next();
/*  987:1073 */               message.append("Conflicts for " + pName + "\n");
/*  988:1074 */               List<Dependency> confsForPackage = (List)conflicts.get(pName);
/*  989:1075 */               Iterator<Dependency> confs = confsForPackage.iterator();
/*  990:1076 */               while (confs.hasNext())
/*  991:     */               {
/*  992:1077 */                 Dependency problem = (Dependency)confs.next();
/*  993:1078 */                 message.append("\t" + problem + "\n");
/*  994:     */               }
/*  995:     */             }
/*  996:1082 */             JOptionPane.showConfirmDialog(PackageManager.this, message.toString(), "Weka Package Manager", 0);
/*  997:     */             
/*  998:     */ 
/*  999:     */ 
/* 1000:     */ 
/* 1001:     */ 
/* 1002:1088 */             this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1003:     */           }
/* 1004:     */           else
/* 1005:     */           {
/* 1006:1097 */             List<PackageConstraint> needsUpgrade = new ArrayList();
/* 1007:     */             
/* 1008:1099 */             List<Package> finalListToInstall = new ArrayList();
/* 1009:     */             
/* 1010:1101 */             Iterator<Dependency> depI = dependencies.iterator();
/* 1011:1102 */             boolean depsOk = true;
/* 1012:1103 */             while (depI.hasNext())
/* 1013:     */             {
/* 1014:1104 */               Dependency toCheck = (Dependency)depI.next();
/* 1015:1105 */               if (toCheck.getTarget().getPackage().isInstalled())
/* 1016:     */               {
/* 1017:1106 */                 String toCheckName = toCheck.getTarget().getPackage().getPackageMetaDataElement("PackageName").toString();
/* 1018:     */                 try
/* 1019:     */                 {
/* 1020:1110 */                   Package installedVersion = WekaPackageManager.getInstalledPackageInfo(toCheckName);
/* 1021:1112 */                   if (!toCheck.getTarget().checkConstraint(installedVersion))
/* 1022:     */                   {
/* 1023:1113 */                     needsUpgrade.add(toCheck.getTarget());
/* 1024:1114 */                     Package mostRecent = toCheck.getTarget().getPackage();
/* 1025:1115 */                     if ((toCheck.getTarget() instanceof VersionPackageConstraint)) {
/* 1026:1116 */                       mostRecent = WekaPackageManager.mostRecentVersionWithRespectToConstraint(toCheck.getTarget());
/* 1027:     */                     }
/* 1028:1121 */                     finalListToInstall.add(mostRecent);
/* 1029:     */                   }
/* 1030:     */                 }
/* 1031:     */                 catch (Exception ex)
/* 1032:     */                 {
/* 1033:1124 */                   ex.printStackTrace();
/* 1034:1125 */                   PackageManager.this.displayErrorDialog("An error has occurred while checking package dependencies", ex);
/* 1035:     */                   
/* 1036:     */ 
/* 1037:     */ 
/* 1038:1129 */                   depsOk = false;
/* 1039:1130 */                   break;
/* 1040:     */                 }
/* 1041:     */               }
/* 1042:     */               else
/* 1043:     */               {
/* 1044:     */                 try
/* 1045:     */                 {
/* 1046:1134 */                   Package mostRecent = toCheck.getTarget().getPackage();
/* 1047:1135 */                   if ((toCheck.getTarget() instanceof VersionPackageConstraint)) {
/* 1048:1136 */                     mostRecent = WekaPackageManager.mostRecentVersionWithRespectToConstraint(toCheck.getTarget());
/* 1049:     */                   }
/* 1050:1141 */                   finalListToInstall.add(mostRecent);
/* 1051:     */                 }
/* 1052:     */                 catch (Exception ex)
/* 1053:     */                 {
/* 1054:1143 */                   ex.printStackTrace();
/* 1055:1144 */                   PackageManager.this.displayErrorDialog("An error has occurred while checking package dependencies", ex);
/* 1056:     */                   
/* 1057:     */ 
/* 1058:     */ 
/* 1059:1148 */                   depsOk = false;
/* 1060:1149 */                   break;
/* 1061:     */                 }
/* 1062:     */               }
/* 1063:     */             }
/* 1064:1154 */             if (!depsOk)
/* 1065:     */             {
/* 1066:1156 */               this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1067:     */             }
/* 1068:     */             else
/* 1069:     */             {
/* 1070:1160 */               if (needsUpgrade.size() > 0)
/* 1071:     */               {
/* 1072:1161 */                 StringBuffer temp = new StringBuffer();
/* 1073:1162 */                 for (PackageConstraint pc : needsUpgrade) {
/* 1074:1163 */                   temp.append(pc + "\n");
/* 1075:     */                 }
/* 1076:1165 */                 int result = JOptionPane.showConfirmDialog(PackageManager.this, "The following packages will be upgraded in order to install:\n\n" + temp.toString(), "Weka Package Manager", 0);
/* 1077:1171 */                 if (result == 1)
/* 1078:     */                 {
/* 1079:1174 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1080:1175 */                   continue;
/* 1081:     */                 }
/* 1082:1180 */                 boolean conflictsAfterUpgrade = false;
/* 1083:1181 */                 List<Package> installed = null;
/* 1084:     */                 try
/* 1085:     */                 {
/* 1086:1183 */                   installed = WekaPackageManager.getInstalledPackages();
/* 1087:     */                 }
/* 1088:     */                 catch (Exception e)
/* 1089:     */                 {
/* 1090:1185 */                   e.printStackTrace();
/* 1091:1186 */                   PackageManager.this.displayErrorDialog("Unable to determine what packages are installed!", e);
/* 1092:     */                   
/* 1093:     */ 
/* 1094:1189 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1095:1190 */                   continue;
/* 1096:     */                 }
/* 1097:1192 */                 List<Package> toUpgrade = new ArrayList();
/* 1098:1193 */                 for (PackageConstraint pc : needsUpgrade) {
/* 1099:1194 */                   toUpgrade.add(pc.getPackage());
/* 1100:     */                 }
/* 1101:1202 */                 toUpgrade.add(packageToInstall);
/* 1102:     */                 
/* 1103:1204 */                 StringBuffer tempM = new StringBuffer();
/* 1104:1205 */                 depsOk = true;
/* 1105:1206 */                 for (int i = 0; i < installed.size(); i++)
/* 1106:     */                 {
/* 1107:1207 */                   Package tempP = (Package)installed.get(i);
/* 1108:1208 */                   String tempPName = tempP.getName();
/* 1109:1209 */                   boolean checkIt = true;
/* 1110:1210 */                   for (int j = 0; j < needsUpgrade.size(); j++) {
/* 1111:1211 */                     if (tempPName.equals(((PackageConstraint)needsUpgrade.get(j)).getPackage().getName()))
/* 1112:     */                     {
/* 1113:1213 */                       checkIt = false;
/* 1114:1214 */                       break;
/* 1115:     */                     }
/* 1116:     */                   }
/* 1117:1218 */                   if (checkIt)
/* 1118:     */                   {
/* 1119:1219 */                     List<Dependency> problem = null;
/* 1120:     */                     try
/* 1121:     */                     {
/* 1122:1221 */                       problem = tempP.getIncompatibleDependencies(toUpgrade);
/* 1123:     */                     }
/* 1124:     */                     catch (Exception e)
/* 1125:     */                     {
/* 1126:1223 */                       e.printStackTrace();
/* 1127:1224 */                       PackageManager.this.displayErrorDialog("An error has occurred while checking package dependencies", e);
/* 1128:     */                       
/* 1129:     */ 
/* 1130:1227 */                       depsOk = false;
/* 1131:1228 */                       break;
/* 1132:     */                     }
/* 1133:1230 */                     if (problem.size() > 0)
/* 1134:     */                     {
/* 1135:1231 */                       conflictsAfterUpgrade = true;
/* 1136:     */                       
/* 1137:1233 */                       tempM.append("Package " + tempP.getName() + " will have a compatibility" + "problem with the following packages after upgrading them:\n");
/* 1138:     */                       
/* 1139:     */ 
/* 1140:     */ 
/* 1141:     */ 
/* 1142:1238 */                       Iterator<Dependency> dI = problem.iterator();
/* 1143:1239 */                       while (dI.hasNext()) {
/* 1144:1240 */                         tempM.append("\t" + ((Dependency)dI.next()).getTarget().getPackage() + "\n");
/* 1145:     */                       }
/* 1146:     */                     }
/* 1147:     */                   }
/* 1148:     */                 }
/* 1149:1247 */                 if (!depsOk)
/* 1150:     */                 {
/* 1151:1248 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1152:1249 */                   continue;
/* 1153:     */                 }
/* 1154:1252 */                 if (conflictsAfterUpgrade)
/* 1155:     */                 {
/* 1156:1253 */                   JOptionPane.showConfirmDialog(PackageManager.this, tempM.toString() + "\n" + "Unable to continue with installation.", "Weka Package Manager", 0);
/* 1157:     */                   
/* 1158:     */ 
/* 1159:     */ 
/* 1160:     */ 
/* 1161:     */ 
/* 1162:1259 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1163:1260 */                   continue;
/* 1164:     */                 }
/* 1165:     */               }
/* 1166:1264 */               if (finalListToInstall.size() > 0)
/* 1167:     */               {
/* 1168:1265 */                 StringBuffer message = new StringBuffer();
/* 1169:1266 */                 message.append("To install " + packageName + " the following packages will" + " be installed/upgraded:\n\n");
/* 1170:1268 */                 for (Package p : finalListToInstall) {
/* 1171:1269 */                   message.append("\t" + p + "\n");
/* 1172:     */                 }
/* 1173:1272 */                 int result = JOptionPane.showConfirmDialog(PackageManager.this, message.toString(), "Weka Package Manager", 0);
/* 1174:1277 */                 if (result == 1) {
/* 1175:1280 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1176:     */                 } else {
/* 1177:1283 */                   PackageManager.this.m_progress.setMaximum(PackageManager.this.m_progress.getMaximum() + finalListToInstall.size() * 30);
/* 1178:     */                 }
/* 1179:     */               }
/* 1180:     */               else
/* 1181:     */               {
/* 1182:     */                 try
/* 1183:     */                 {
/* 1184:1291 */                   boolean tempB = WekaPackageManager.installPackages(finalListToInstall, new PrintStream[] { pps });
/* 1185:     */                   
/* 1186:1293 */                   PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession = (PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession) || (tempB);
/* 1187:     */                 }
/* 1188:     */                 catch (Exception e)
/* 1189:     */                 {
/* 1190:1296 */                   e.printStackTrace();
/* 1191:1297 */                   PackageManager.this.displayErrorDialog("An error has occurred while installing dependent packages", e);
/* 1192:     */                   
/* 1193:     */ 
/* 1194:1300 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1195:1301 */                   continue;
/* 1196:     */                 }
/* 1197:     */                 try
/* 1198:     */                 {
/* 1199:1307 */                   boolean tempB = WekaPackageManager.installPackageFromRepository(packageName, versionToInstall.toString(), new PrintStream[] { pps });
/* 1200:     */                   
/* 1201:     */ 
/* 1202:1310 */                   PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession = (PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession) || (tempB);
/* 1203:     */                 }
/* 1204:     */                 catch (Exception e)
/* 1205:     */                 {
/* 1206:1313 */                   e.printStackTrace();
/* 1207:1314 */                   PackageManager.this.displayErrorDialog("Problem installing package: " + packageName, e);
/* 1208:     */                   
/* 1209:1316 */                   this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1210:1317 */                   continue;
/* 1211:     */                 }
/* 1212:     */               }
/* 1213:     */             }
/* 1214:     */           }
/* 1215:     */         }
/* 1216:     */         else
/* 1217:     */         {
/* 1218:     */           try
/* 1219:     */           {
/* 1220:1324 */             boolean tempB = WekaPackageManager.installPackageFromRepository(packageName, versionToInstall.toString(), new PrintStream[] { pps });
/* 1221:     */             
/* 1222:     */ 
/* 1223:1327 */             PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession = (PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession) || (tempB);
/* 1224:     */           }
/* 1225:     */           catch (Exception e)
/* 1226:     */           {
/* 1227:1330 */             e.printStackTrace();
/* 1228:1331 */             PackageManager.this.displayErrorDialog("Problem installing package: " + packageName, e);
/* 1229:     */             
/* 1230:1333 */             this.m_unsuccessfulInstalls.add(packageToInstall);
/* 1231:     */           }
/* 1232:     */         }
/* 1233:     */       }
/* 1234:1346 */       if (!PackageManager.s_atLeastOnePackageUpgradeHasOccurredInThisSession) {
/* 1235:1347 */         WekaPackageManager.refreshGOEProperties();
/* 1236:     */       }
/* 1237:1349 */       return null;
/* 1238:     */     }
/* 1239:     */     
/* 1240:     */     public void done()
/* 1241:     */     {
/* 1242:1354 */       PackageManager.this.m_progress.setValue(PackageManager.this.m_progress.getMinimum());
/* 1243:1355 */       if (this.m_unsuccessfulInstalls.size() == 0)
/* 1244:     */       {
/* 1245:1357 */         PackageManager.this.m_detailLabel.setText("Package(s) installed successfully.");
/* 1246:     */       }
/* 1247:     */       else
/* 1248:     */       {
/* 1249:1359 */         StringBuffer failedPackageNames = new StringBuffer();
/* 1250:1360 */         for (Package p : this.m_unsuccessfulInstalls) {
/* 1251:1361 */           failedPackageNames.append(p.getName() + "\n");
/* 1252:     */         }
/* 1253:1363 */         PackageManager.this.displayErrorDialog("The following package(s) could not be installed\nfor some reason (check the log)\n" + failedPackageNames.toString(), "");
/* 1254:     */         
/* 1255:     */ 
/* 1256:     */ 
/* 1257:1367 */         PackageManager.this.m_detailLabel.setText("Install complete.");
/* 1258:     */       }
/* 1259:1370 */       PackageManager.this.m_unofficialBut.setEnabled(true);
/* 1260:1371 */       PackageManager.this.m_refreshCacheBut.setEnabled(!WekaPackageManager.m_offline);
/* 1261:1372 */       PackageManager.this.m_availableBut.setEnabled(true);
/* 1262:1373 */       PackageManager.this.m_allBut.setEnabled(true);
/* 1263:1374 */       PackageManager.this.m_installedBut.setEnabled(true);
/* 1264:     */       
/* 1265:     */ 
/* 1266:1377 */       PackageManager.this.m_installedPackages = null;
/* 1267:1378 */       PackageManager.this.m_availablePackages = null;
/* 1268:     */       
/* 1269:     */ 
/* 1270:1381 */       PackageManager.this.m_installing = false;
/* 1271:1382 */       PackageManager.this.updateTable();
/* 1272:1383 */       if (PackageManager.this.m_table.getSelectedRow() >= 0) {
/* 1273:1386 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/* 1274:     */       }
/* 1275:     */     }
/* 1276:     */   }
/* 1277:     */   
/* 1278:     */   protected class ComboBoxEditor
/* 1279:     */     extends DefaultCellEditor
/* 1280:     */   {
/* 1281:     */     private static final long serialVersionUID = 5240331667759901966L;
/* 1282:     */     
/* 1283:     */     public ComboBoxEditor()
/* 1284:     */     {
/* 1285:1412 */       super();
/* 1286:     */     }
/* 1287:     */     
/* 1288:     */     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/* 1289:     */     {
/* 1290:1418 */       String packageName = PackageManager.this.m_table.getValueAt(row, PackageManager.this.getColumnIndex("Package")).toString();
/* 1291:     */       
/* 1292:1420 */       List<Object> catAndVers = (List)PackageManager.this.m_packageLookupInfo.get(packageName);
/* 1293:     */       
/* 1294:1422 */       List<Object> repVersions = (List)catAndVers.get(1);
/* 1295:     */       
/* 1296:1424 */       String[] versions = (String[])repVersions.toArray(new String[1]);
/* 1297:1425 */       Component combo = getComponent();
/* 1298:1426 */       if ((combo instanceof JComboBox))
/* 1299:     */       {
/* 1300:1427 */         ((JComboBox)combo).setModel(new DefaultComboBoxModel(versions));
/* 1301:1428 */         ((JComboBox)combo).setSelectedItem(value);
/* 1302:     */       }
/* 1303:     */       else
/* 1304:     */       {
/* 1305:1430 */         System.err.println("Uh oh!!!!!");
/* 1306:     */       }
/* 1307:1432 */       return combo;
/* 1308:     */     }
/* 1309:     */   }
/* 1310:     */   
/* 1311:1436 */   protected boolean m_cacheEstablished = false;
/* 1312:1437 */   protected boolean m_cacheRefreshInProgress = false;
/* 1313:1438 */   public static String PAGE_HEADER = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n<html>\n<head>\n<title>Waikato Environment for Knowledge Analysis (WEKA)</title>\n<!-- CSS Stylesheet -->\n<style>body\n{\nbackground: #ededed;\ncolor: #666666;\nfont: 14px Tahoma, Helvetica, sans-serif;;\nmargin: 5px 10px 5px 10px;\npadding: 0px;\n}\n</style>\n\n</head>\n<body bgcolor=\"#ededed\" text=\"#666666\">\n";
/* 1314:     */   
/* 1315:     */   private static String initialPage()
/* 1316:     */   {
/* 1317:1446 */     StringBuffer initialPage = new StringBuffer();
/* 1318:1447 */     initialPage.append(PAGE_HEADER);
/* 1319:1448 */     initialPage.append("<h1>WEKA Package Manager</h1>\n\n</body></html>\n");
/* 1320:1449 */     return initialPage.toString();
/* 1321:     */   }
/* 1322:     */   
/* 1323:     */   protected class HomePageThread
/* 1324:     */     extends Thread
/* 1325:     */   {
/* 1326:     */     protected HomePageThread() {}
/* 1327:     */     
/* 1328:     */     public void run()
/* 1329:     */     {
/* 1330:     */       try
/* 1331:     */       {
/* 1332:1456 */         PackageManager.this.m_homeB.setEnabled(false);
/* 1333:1457 */         PackageManager.this.m_backB.setEnabled(false);
/* 1334:1458 */         URLConnection conn = null;
/* 1335:1459 */         URL homeURL = new URL("http://www.cs.waikato.ac.nz/ml/weka/index_home_pm.html");
/* 1336:1460 */         weka.core.packageManagement.PackageManager pm = WekaPackageManager.getUnderlyingPackageManager();
/* 1337:1462 */         if (pm.setProxyAuthentication(homeURL)) {
/* 1338:1463 */           conn = homeURL.openConnection(pm.getProxy());
/* 1339:     */         } else {
/* 1340:1465 */           conn = homeURL.openConnection();
/* 1341:     */         }
/* 1342:1472 */         conn.setConnectTimeout(10000);
/* 1343:     */         BufferedReader bi;
/* 1344:1475 */         while (bi.readLine() != null) {}
/* 1345:1479 */         PackageManager.this.m_infoPane.setPage("http://www.cs.waikato.ac.nz/ml/weka/index_home_pm.html");
/* 1346:     */       }
/* 1347:     */       catch (Exception ex) {}finally
/* 1348:     */       {
/* 1349:1483 */         PackageManager.this.m_homeB.setEnabled(true);
/* 1350:1484 */         PackageManager.this.m_backB.setEnabled(true);
/* 1351:     */       }
/* 1352:     */     }
/* 1353:     */   }
/* 1354:     */   
/* 1355:     */   private int getColumnIndex(String columnName)
/* 1356:     */   {
/* 1357:1490 */     return this.m_table.getColumn(columnName).getModelIndex();
/* 1358:     */   }
/* 1359:     */   
/* 1360:     */   public PackageManager()
/* 1361:     */   {
/* 1362:1495 */     if (WekaPackageManager.m_noPackageMetaDataAvailable)
/* 1363:     */     {
/* 1364:1496 */       JOptionPane.showMessageDialog(this, "The package manager is unavailable due to the fact that there is no cached package meta data and we are offline", "Package manager unavailable", 1);
/* 1365:     */       
/* 1366:     */ 
/* 1367:     */ 
/* 1368:     */ 
/* 1369:     */ 
/* 1370:1502 */       return;
/* 1371:     */     }
/* 1372:1505 */     EstablishCache ec = new EstablishCache();
/* 1373:1506 */     ec.execute();
/* 1374:1508 */     while (!this.m_cacheEstablished) {
/* 1375:     */       try
/* 1376:     */       {
/* 1377:1510 */         Thread.sleep(1000L);
/* 1378:     */       }
/* 1379:     */       catch (InterruptedException e1)
/* 1380:     */       {
/* 1381:1512 */         e1.printStackTrace();
/* 1382:     */       }
/* 1383:     */     }
/* 1384:1517 */     getAllPackages();
/* 1385:     */     
/* 1386:1519 */     setLayout(new BorderLayout());
/* 1387:     */     
/* 1388:1521 */     ButtonGroup bGroup = new ButtonGroup();
/* 1389:1522 */     bGroup.add(this.m_installedBut);
/* 1390:1523 */     bGroup.add(this.m_availableBut);
/* 1391:1524 */     bGroup.add(this.m_allBut);
/* 1392:1525 */     this.m_installedBut.setToolTipText("Installed packages");
/* 1393:1526 */     this.m_availableBut.setToolTipText("Available packages compatible with Weka " + Version.VERSION);
/* 1394:     */     
/* 1395:1528 */     this.m_allBut.setToolTipText("All packages");
/* 1396:     */     
/* 1397:1530 */     JPanel butPanel = new JPanel();
/* 1398:1531 */     butPanel.setLayout(new BorderLayout());
/* 1399:     */     
/* 1400:1533 */     JPanel packageDisplayP = new JPanel();
/* 1401:1534 */     packageDisplayP.setLayout(new BorderLayout());
/* 1402:1535 */     JPanel packageDHolder = new JPanel();
/* 1403:1536 */     packageDHolder.setLayout(new FlowLayout());
/* 1404:1537 */     packageDHolder.add(this.m_installedBut);
/* 1405:1538 */     packageDHolder.add(this.m_availableBut);
/* 1406:1539 */     packageDHolder.add(this.m_allBut);
/* 1407:1540 */     packageDisplayP.add(packageDHolder, "South");
/* 1408:1541 */     packageDisplayP.add(this.m_refreshCacheBut, "North");
/* 1409:1542 */     JPanel officialHolder = new JPanel();
/* 1410:1543 */     officialHolder.setLayout(new BorderLayout());
/* 1411:1544 */     officialHolder.setBorder(BorderFactory.createTitledBorder("Official"));
/* 1412:1545 */     officialHolder.add(packageDisplayP, "West");
/* 1413:     */     
/* 1414:1547 */     butPanel.add(officialHolder, "West");
/* 1415:     */     
/* 1416:1549 */     this.m_refreshCacheBut.addActionListener(new ActionListener()
/* 1417:     */     {
/* 1418:     */       public void actionPerformed(ActionEvent e)
/* 1419:     */       {
/* 1420:1552 */         PackageManager.RefreshCache r = new PackageManager.RefreshCache(PackageManager.this);
/* 1421:1553 */         r.execute();
/* 1422:     */       }
/* 1423:1556 */     });
/* 1424:1557 */     JPanel unofficialHolder = new JPanel();
/* 1425:1558 */     unofficialHolder.setLayout(new BorderLayout());
/* 1426:1559 */     unofficialHolder.setBorder(BorderFactory.createTitledBorder("Unofficial"));
/* 1427:1560 */     unofficialHolder.add(this.m_unofficialBut, "North");
/* 1428:1561 */     butPanel.add(unofficialHolder, "East");
/* 1429:     */     
/* 1430:1563 */     JPanel installP = new JPanel();
/* 1431:1564 */     JPanel buttP = new JPanel();
/* 1432:1565 */     buttP.setLayout(new GridLayout(1, 3));
/* 1433:1566 */     installP.setLayout(new BorderLayout());
/* 1434:1567 */     buttP.add(this.m_installBut);
/* 1435:1568 */     buttP.add(this.m_uninstallBut);
/* 1436:1569 */     buttP.add(this.m_toggleLoad);
/* 1437:1570 */     this.m_installBut.setEnabled(false);
/* 1438:1571 */     this.m_uninstallBut.setEnabled(false);
/* 1439:1572 */     this.m_toggleLoad.setEnabled(false);
/* 1440:1573 */     installP.add(buttP, "North");
/* 1441:1574 */     installP.add(this.m_forceBut, "South");
/* 1442:1575 */     this.m_forceBut.setEnabled(false);
/* 1443:     */     
/* 1444:1577 */     officialHolder.add(installP, "East");
/* 1445:     */     
/* 1446:1579 */     this.m_installBut.setToolTipText("Install the selected official package(s) from the list");
/* 1447:     */     
/* 1448:1581 */     this.m_uninstallBut.setToolTipText("Uninstall the selected package(s) from the list");
/* 1449:     */     
/* 1450:1583 */     this.m_toggleLoad.setToolTipText("Toggle installed package(s) load status (note - changes take affect after a restart)");
/* 1451:     */     
/* 1452:1585 */     this.m_unofficialBut.setToolTipText("Install an unofficial package from a file or URL");
/* 1453:     */     
/* 1454:1587 */     this.m_unofficialChooser.resetFileFilters();
/* 1455:1588 */     this.m_unofficialChooser.addFileFilter(new ExtensionFileFilter(".zip", "Package archive file"));
/* 1456:     */     
/* 1457:     */ 
/* 1458:1591 */     this.m_unofficialBut.addActionListener(new ActionListener()
/* 1459:     */     {
/* 1460:     */       public void actionPerformed(ActionEvent e)
/* 1461:     */       {
/* 1462:1594 */         if (PackageManager.this.m_unofficialFrame == null)
/* 1463:     */         {
/* 1464:1595 */           final JFrame jf = new JFrame("Unofficial package install");
/* 1465:1596 */           jf.addWindowListener(new WindowAdapter()
/* 1466:     */           {
/* 1467:     */             public void windowClosing(WindowEvent e)
/* 1468:     */             {
/* 1469:1599 */               jf.dispose();
/* 1470:1600 */               PackageManager.this.m_unofficialBut.setEnabled(true);
/* 1471:1601 */               PackageManager.this.m_unofficialFrame = null;
/* 1472:     */             }
/* 1473:1603 */           });
/* 1474:1604 */           jf.setLayout(new BorderLayout());
/* 1475:1605 */           JButton okBut = new JButton("OK");
/* 1476:1606 */           JButton cancelBut = new JButton("Cancel");
/* 1477:1607 */           JPanel butHolder = new JPanel();
/* 1478:1608 */           butHolder.setLayout(new GridLayout(1, 2));
/* 1479:1609 */           butHolder.add(okBut);
/* 1480:1610 */           butHolder.add(cancelBut);
/* 1481:1611 */           jf.add(PackageManager.this.m_unofficialChooser, "Center");
/* 1482:1612 */           jf.add(butHolder, "South");
/* 1483:1613 */           jf.pack();
/* 1484:1614 */           jf.setVisible(true);
/* 1485:1615 */           PackageManager.this.m_unofficialFrame = jf;
/* 1486:1616 */           PackageManager.this.m_unofficialBut.setEnabled(false);
/* 1487:1617 */           cancelBut.addActionListener(new ActionListener()
/* 1488:     */           {
/* 1489:     */             public void actionPerformed(ActionEvent e)
/* 1490:     */             {
/* 1491:1620 */               if (PackageManager.this.m_unofficialFrame != null)
/* 1492:     */               {
/* 1493:1621 */                 jf.dispose();
/* 1494:1622 */                 PackageManager.this.m_unofficialBut.setEnabled(true);
/* 1495:1623 */                 PackageManager.this.m_unofficialFrame = null;
/* 1496:     */               }
/* 1497:     */             }
/* 1498:1627 */           });
/* 1499:1628 */           okBut.addActionListener(new ActionListener()
/* 1500:     */           {
/* 1501:     */             public void actionPerformed(ActionEvent e)
/* 1502:     */             {
/* 1503:1631 */               String target = PackageManager.this.m_unofficialChooser.getText();
/* 1504:1632 */               PackageManager.UnofficialInstallTask t = new PackageManager.UnofficialInstallTask(PackageManager.this);
/* 1505:1633 */               t.setTargetToInstall(target);
/* 1506:1634 */               t.execute();
/* 1507:1635 */               if (PackageManager.this.m_unofficialFrame != null)
/* 1508:     */               {
/* 1509:1636 */                 jf.dispose();
/* 1510:1637 */                 PackageManager.this.m_unofficialBut.setEnabled(true);
/* 1511:1638 */                 PackageManager.this.m_unofficialFrame = null;
/* 1512:     */               }
/* 1513:     */             }
/* 1514:     */           });
/* 1515:     */         }
/* 1516:     */       }
/* 1517:1645 */     });
/* 1518:1646 */     this.m_toggleLoad.addActionListener(new ActionListener()
/* 1519:     */     {
/* 1520:     */       public void actionPerformed(ActionEvent e)
/* 1521:     */       {
/* 1522:1649 */         int[] selectedRows = PackageManager.this.m_table.getSelectedRows();
/* 1523:1650 */         List<Integer> alteredRows = new ArrayList();
/* 1524:1652 */         if (selectedRows.length > 0)
/* 1525:     */         {
/* 1526:1653 */           List<String> packageNames = new ArrayList();
/* 1527:1654 */           for (int selectedRow : selectedRows)
/* 1528:     */           {
/* 1529:1655 */             String packageName = PackageManager.this.m_table.getValueAt(selectedRow, PackageManager.this.getColumnIndex("Package")).toString();
/* 1530:     */             try
/* 1531:     */             {
/* 1532:1659 */               if (WekaPackageManager.getInstalledPackageInfo(packageName) != null)
/* 1533:     */               {
/* 1534:1661 */                 List<Object> catAndVers = (List)PackageManager.this.m_packageLookupInfo.get(packageName);
/* 1535:1662 */                 if (!catAndVers.get(2).toString().equals("No - check log"))
/* 1536:     */                 {
/* 1537:1663 */                   packageNames.add(packageName);
/* 1538:1664 */                   alteredRows.add(Integer.valueOf(selectedRow));
/* 1539:     */                 }
/* 1540:     */               }
/* 1541:     */             }
/* 1542:     */             catch (Exception ex)
/* 1543:     */             {
/* 1544:1668 */               ex.printStackTrace();
/* 1545:     */             }
/* 1546:     */           }
/* 1547:1673 */           if (packageNames.size() > 0) {
/* 1548:     */             try
/* 1549:     */             {
/* 1550:1675 */               WekaPackageManager.toggleLoadStatus(packageNames);
/* 1551:1676 */               for (String packageName : packageNames)
/* 1552:     */               {
/* 1553:1677 */                 List<Object> catAndVers = (List)PackageManager.this.m_packageLookupInfo.get(packageName);
/* 1554:1678 */                 String loadStatus = catAndVers.get(2).toString();
/* 1555:1679 */                 if (loadStatus.startsWith("Yes")) {
/* 1556:1680 */                   loadStatus = "No - user flagged (pending restart)";
/* 1557:     */                 } else {
/* 1558:1682 */                   loadStatus = "Yes - user flagged (pending restart)";
/* 1559:     */                 }
/* 1560:1685 */                 catAndVers.set(2, loadStatus);
/* 1561:     */               }
/* 1562:1687 */               PackageManager.this.updateTable();
/* 1563:     */             }
/* 1564:     */             catch (Exception e1)
/* 1565:     */             {
/* 1566:1689 */               e1.printStackTrace();
/* 1567:     */             }
/* 1568:     */           }
/* 1569:1692 */           PackageManager.this.toggleLoadStatusRequiresRestartPopUp();
/* 1570:     */         }
/* 1571:     */       }
/* 1572:1696 */     });
/* 1573:1697 */     this.m_installBut.addActionListener(new ActionListener()
/* 1574:     */     {
/* 1575:     */       public void actionPerformed(ActionEvent e)
/* 1576:     */       {
/* 1577:1700 */         int[] selectedRows = PackageManager.this.m_table.getSelectedRows();
/* 1578:1702 */         if (selectedRows.length > 0)
/* 1579:     */         {
/* 1580:1706 */           List<String> packageNames = new ArrayList();
/* 1581:1707 */           List<Object> versions = new ArrayList();
/* 1582:1708 */           StringBuffer confirmList = new StringBuffer();
/* 1583:1709 */           for (int selectedRow : selectedRows)
/* 1584:     */           {
/* 1585:1710 */             String packageName = PackageManager.this.m_table.getValueAt(selectedRow, PackageManager.this.getColumnIndex("Package")).toString();
/* 1586:     */             
/* 1587:     */ 
/* 1588:1713 */             packageNames.add(packageName);
/* 1589:1714 */             Object packageVersion = PackageManager.this.m_table.getValueAt(selectedRow, PackageManager.this.getColumnIndex("Repository version"));
/* 1590:     */             
/* 1591:     */ 
/* 1592:1717 */             versions.add(packageVersion);
/* 1593:1718 */             confirmList.append(packageName + " " + packageVersion.toString() + "\n");
/* 1594:     */           }
/* 1595:1722 */           JTextArea jt = new JTextArea("The following packages will be installed/upgraded:\n\n" + confirmList.toString(), 10, 40);
/* 1596:     */           
/* 1597:     */ 
/* 1598:1725 */           int result = JOptionPane.showConfirmDialog(PackageManager.this, new JScrollPane(jt), "Weka Package Manager", 0);
/* 1599:1729 */           if (result == 0)
/* 1600:     */           {
/* 1601:1730 */             PackageManager.this.pleaseCloseAppWindowsPopUp();
/* 1602:     */             
/* 1603:1732 */             PackageManager.InstallTask task = new PackageManager.InstallTask(PackageManager.this);
/* 1604:1733 */             task.setPackages(packageNames);
/* 1605:1734 */             task.setVersions(versions);
/* 1606:1735 */             task.execute();
/* 1607:     */           }
/* 1608:     */         }
/* 1609:     */       }
/* 1610:1740 */     });
/* 1611:1741 */     this.m_uninstallBut.addActionListener(new ActionListener()
/* 1612:     */     {
/* 1613:     */       public void actionPerformed(ActionEvent e)
/* 1614:     */       {
/* 1615:1746 */         int[] selectedRows = PackageManager.this.m_table.getSelectedRows();
/* 1616:1748 */         if (selectedRows.length > 0)
/* 1617:     */         {
/* 1618:1749 */           List<String> packageNames = new ArrayList();
/* 1619:1750 */           StringBuffer confirmList = new StringBuffer();
/* 1620:1752 */           for (int selectedRow : selectedRows)
/* 1621:     */           {
/* 1622:1753 */             String packageName = PackageManager.this.m_table.getValueAt(selectedRow, PackageManager.this.getColumnIndex("Package")).toString();
/* 1623:     */             
/* 1624:     */ 
/* 1625:1756 */             Package p = null;
/* 1626:     */             try
/* 1627:     */             {
/* 1628:1758 */               p = WekaPackageManager.getRepositoryPackageInfo(packageName);
/* 1629:     */             }
/* 1630:     */             catch (Exception e1)
/* 1631:     */             {
/* 1632:     */               try
/* 1633:     */               {
/* 1634:1764 */                 p = WekaPackageManager.getInstalledPackageInfo(packageName);
/* 1635:     */               }
/* 1636:     */               catch (Exception e2)
/* 1637:     */               {
/* 1638:1766 */                 e2.printStackTrace();
/* 1639:1767 */                 continue;
/* 1640:     */               }
/* 1641:     */             }
/* 1642:1771 */             if (p.isInstalled())
/* 1643:     */             {
/* 1644:1772 */               packageNames.add(packageName);
/* 1645:1773 */               confirmList.append(packageName + "\n");
/* 1646:     */             }
/* 1647:     */           }
/* 1648:1777 */           if (packageNames.size() > 0)
/* 1649:     */           {
/* 1650:1778 */             JTextArea jt = new JTextArea("The following packages will be uninstalled:\n" + confirmList.toString(), 10, 40);
/* 1651:     */             
/* 1652:     */ 
/* 1653:1781 */             int result = JOptionPane.showConfirmDialog(PackageManager.this, new JScrollPane(jt), "Weka Package Manager", 0);
/* 1654:1786 */             if (result == 0)
/* 1655:     */             {
/* 1656:1787 */               PackageManager.this.pleaseCloseAppWindowsPopUp();
/* 1657:1788 */               PackageManager.UninstallTask task = new PackageManager.UninstallTask(PackageManager.this);
/* 1658:1789 */               task.setPackages(packageNames);
/* 1659:1790 */               task.execute();
/* 1660:     */             }
/* 1661:     */           }
/* 1662:     */         }
/* 1663:     */       }
/* 1664:1805 */     });
/* 1665:1806 */     JPanel progressP = new JPanel();
/* 1666:1807 */     progressP.setLayout(new BorderLayout());
/* 1667:1808 */     progressP.setBorder(BorderFactory.createTitledBorder("Install/Uninstall/Refresh progress"));
/* 1668:     */     
/* 1669:1810 */     progressP.add(this.m_progress, "North");
/* 1670:1811 */     progressP.add(this.m_detailLabel, "Center");
/* 1671:1812 */     butPanel.add(progressP, "Center");
/* 1672:     */     
/* 1673:1814 */     JPanel topPanel = new JPanel();
/* 1674:1815 */     topPanel.setLayout(new BorderLayout());
/* 1675:     */     
/* 1676:1817 */     topPanel.add(butPanel, "North");
/* 1677:1818 */     this.m_availableBut.setSelected(true);
/* 1678:     */     
/* 1679:1820 */     this.m_allBut.addActionListener(new ActionListener()
/* 1680:     */     {
/* 1681:     */       public void actionPerformed(ActionEvent e)
/* 1682:     */       {
/* 1683:1823 */         PackageManager.this.m_searchResults.clear();
/* 1684:1824 */         PackageManager.this.m_searchField.setText("");
/* 1685:1825 */         PackageManager.this.m_searchHitsLab.setText("");
/* 1686:1826 */         PackageManager.this.m_table.clearSelection();
/* 1687:1827 */         PackageManager.this.updateTable();
/* 1688:1828 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/* 1689:     */       }
/* 1690:1831 */     });
/* 1691:1832 */     this.m_availableBut.addActionListener(new ActionListener()
/* 1692:     */     {
/* 1693:     */       public void actionPerformed(ActionEvent e)
/* 1694:     */       {
/* 1695:1835 */         PackageManager.this.m_searchResults.clear();
/* 1696:1836 */         PackageManager.this.m_searchField.setText("");
/* 1697:1837 */         PackageManager.this.m_searchHitsLab.setText("");
/* 1698:1838 */         PackageManager.this.m_table.clearSelection();
/* 1699:1839 */         PackageManager.this.updateTable();
/* 1700:1840 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/* 1701:     */       }
/* 1702:1843 */     });
/* 1703:1844 */     this.m_installedBut.addActionListener(new ActionListener()
/* 1704:     */     {
/* 1705:     */       public void actionPerformed(ActionEvent e)
/* 1706:     */       {
/* 1707:1847 */         PackageManager.this.m_searchResults.clear();
/* 1708:1848 */         PackageManager.this.m_searchField.setText("");
/* 1709:1849 */         PackageManager.this.m_searchHitsLab.setText("");
/* 1710:1850 */         PackageManager.this.m_table.clearSelection();
/* 1711:1851 */         PackageManager.this.updateTable();
/* 1712:1852 */         PackageManager.this.updateInstallUninstallButtonEnablement();
/* 1713:     */       }
/* 1714:1855 */     });
/* 1715:1856 */     this.m_model = new DefaultTableModel(new String[] { "Package", "Category", "Installed version", "Repository version", "Loaded" }, 15)
/* 1716:     */     {
/* 1717:     */       private static final long serialVersionUID = -2886328542412471039L;
/* 1718:     */       
/* 1719:     */       public boolean isCellEditable(int row, int col)
/* 1720:     */       {
/* 1721:1864 */         if (col != 3) {
/* 1722:1865 */           return false;
/* 1723:     */         }
/* 1724:1867 */         return true;
/* 1725:     */       }
/* 1726:1871 */     };
/* 1727:1872 */     this.m_table.setSelectionMode(2);
/* 1728:1873 */     this.m_table.setColumnSelectionAllowed(false);
/* 1729:1874 */     this.m_table.setPreferredScrollableViewportSize(new Dimension(550, 200));
/* 1730:1875 */     this.m_table.setModel(this.m_model);
/* 1731:1876 */     if (System.getProperty("os.name").contains("Mac")) {
/* 1732:1877 */       this.m_table.setShowVerticalLines(true);
/* 1733:     */     } else {
/* 1734:1879 */       this.m_table.setShowVerticalLines(false);
/* 1735:     */     }
/* 1736:1881 */     this.m_table.setShowHorizontalLines(false);
/* 1737:1882 */     this.m_table.getColumn("Repository version").setCellEditor(new ComboBoxEditor());
/* 1738:1883 */     this.m_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/* 1739:     */     {
/* 1740:     */       public void valueChanged(ListSelectionEvent e)
/* 1741:     */       {
/* 1742:1887 */         if ((!e.getValueIsAdjusting()) && (!PackageManager.this.m_cacheRefreshInProgress))
/* 1743:     */         {
/* 1744:1888 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/* 1745:1889 */           boolean infoDisplayed = false;
/* 1746:1890 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/* 1747:1891 */             if ((lm.isSelectedIndex(i)) && 
/* 1748:1892 */               (!infoDisplayed))
/* 1749:     */             {
/* 1750:1894 */               PackageManager.this.displayPackageInfo(i);
/* 1751:1895 */               infoDisplayed = true;
/* 1752:1896 */               break;
/* 1753:     */             }
/* 1754:     */           }
/* 1755:1900 */           PackageManager.this.updateInstallUninstallButtonEnablement();
/* 1756:     */         }
/* 1757:     */       }
/* 1758:1904 */     });
/* 1759:1905 */     JTableHeader header = this.m_table.getTableHeader();
/* 1760:1906 */     header.addMouseListener(new MouseAdapter()
/* 1761:     */     {
/* 1762:     */       public void mouseClicked(MouseEvent evt)
/* 1763:     */       {
/* 1764:1909 */         TableColumnModel colModel = PackageManager.this.m_table.getColumnModel();
/* 1765:     */         
/* 1766:     */ 
/* 1767:1912 */         int vColIndex = colModel.getColumnIndexAtX(evt.getX());
/* 1768:1916 */         if ((vColIndex == -1) || (vColIndex > 1)) {
/* 1769:1917 */           return;
/* 1770:     */         }
/* 1771:1920 */         if (vColIndex == PackageManager.this.m_sortColumn) {
/* 1772:1922 */           PackageManager.this.m_reverseSort = (!PackageManager.this.m_reverseSort);
/* 1773:     */         } else {
/* 1774:1924 */           PackageManager.this.m_reverseSort = false;
/* 1775:     */         }
/* 1776:1926 */         PackageManager.this.m_sortColumn = vColIndex;
/* 1777:1927 */         PackageManager.this.updateTable();
/* 1778:     */       }
/* 1779:1930 */     });
/* 1780:1931 */     topPanel.add(new JScrollPane(this.m_table), "Center");
/* 1781:     */     try
/* 1782:     */     {
/* 1783:1942 */       String initialPage = initialPage();
/* 1784:1943 */       this.m_infoPane = new JEditorPane("text/html", initialPage);
/* 1785:     */     }
/* 1786:     */     catch (Exception ex)
/* 1787:     */     {
/* 1788:1945 */       this.m_infoPane = new JEditorPane();
/* 1789:     */     }
/* 1790:1948 */     this.m_infoPane.setEditable(false);
/* 1791:1949 */     this.m_infoPane.addHyperlinkListener(new HyperlinkListener()
/* 1792:     */     {
/* 1793:     */       public void hyperlinkUpdate(HyperlinkEvent event)
/* 1794:     */       {
/* 1795:1952 */         if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
/* 1796:     */           try
/* 1797:     */           {
/* 1798:1954 */             if ((!event.getURL().toExternalForm().endsWith(".zip")) && (!event.getURL().toExternalForm().endsWith(".jar")))
/* 1799:     */             {
/* 1800:1958 */               if (PackageManager.this.m_browserHistory.size() == 0) {
/* 1801:1959 */                 PackageManager.this.m_backB.setEnabled(true);
/* 1802:     */               }
/* 1803:1961 */               PackageManager.this.m_browserHistory.add(PackageManager.this.m_infoPane.getPage());
/* 1804:1962 */               PackageManager.this.m_infoPane.setPage(event.getURL());
/* 1805:     */             }
/* 1806:     */           }
/* 1807:     */           catch (IOException ioe) {}
/* 1808:     */         }
/* 1809:     */       }
/* 1810:1974 */     });
/* 1811:1975 */     JPanel browserP = new JPanel();
/* 1812:1976 */     browserP.setLayout(new BorderLayout());
/* 1813:1977 */     this.m_backB = new JButton(new ImageIcon(loadImage("weka/gui/images/back.gif")));
/* 1814:1978 */     this.m_backB.setToolTipText("Back");
/* 1815:1979 */     this.m_backB.setEnabled(false);
/* 1816:1980 */     this.m_backB.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
/* 1817:1981 */     this.m_homeB = new JButton(new ImageIcon(loadImage("weka/gui/images/home.gif")));
/* 1818:1982 */     this.m_homeB.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
/* 1819:1983 */     this.m_homeB.setToolTipText("Home");
/* 1820:1984 */     this.m_browserTools = new JToolBar();
/* 1821:1985 */     this.m_browserTools.add(this.m_backB);
/* 1822:1986 */     this.m_browserTools.add(this.m_homeB);
/* 1823:     */     
/* 1824:1988 */     this.m_searchField = new JTextField(15);
/* 1825:1989 */     JPanel searchHolder = new JPanel(new BorderLayout());
/* 1826:1990 */     JPanel temp = new JPanel(new BorderLayout());
/* 1827:1991 */     JLabel searchLab = new JLabel("Package search ");
/* 1828:1992 */     searchLab.setToolTipText("Type search terms (comma separated) and hit <Enter>");
/* 1829:     */     
/* 1830:1994 */     temp.add(searchLab, "West");
/* 1831:1995 */     temp.add(this.m_searchField, "Center");
/* 1832:1996 */     searchHolder.add(temp, "West");
/* 1833:1997 */     JButton clearSearchBut = new JButton("Clear");
/* 1834:1998 */     clearSearchBut.addActionListener(new ActionListener()
/* 1835:     */     {
/* 1836:     */       public void actionPerformed(ActionEvent e)
/* 1837:     */       {
/* 1838:2001 */         PackageManager.this.m_searchField.setText("");
/* 1839:2002 */         PackageManager.this.m_searchHitsLab.setText("");
/* 1840:2003 */         PackageManager.this.updateTable();
/* 1841:     */       }
/* 1842:2005 */     });
/* 1843:2006 */     JPanel clearAndHitsHolder = new JPanel(new BorderLayout());
/* 1844:2007 */     clearAndHitsHolder.add(clearSearchBut, "West");
/* 1845:2008 */     clearAndHitsHolder.add(this.m_searchHitsLab, "East");
/* 1846:2009 */     temp.add(clearAndHitsHolder, "East");
/* 1847:2010 */     this.m_browserTools.addSeparator();
/* 1848:2011 */     this.m_browserTools.add(searchHolder);
/* 1849:2012 */     Dimension d = this.m_searchField.getSize();
/* 1850:2013 */     this.m_searchField.setMaximumSize(new Dimension(150, 20));
/* 1851:2014 */     this.m_searchField.setEnabled(this.m_packageDescriptions.size() > 0);
/* 1852:     */     
/* 1853:2016 */     this.m_searchField.addActionListener(new ActionListener()
/* 1854:     */     {
/* 1855:     */       public void actionPerformed(ActionEvent e)
/* 1856:     */       {
/* 1857:2019 */         List<Package> toSearch = PackageManager.this.m_availableBut.isSelected() ? PackageManager.this.m_availablePackages : PackageManager.this.m_allBut.isSelected() ? PackageManager.this.m_allPackages : PackageManager.this.m_installedPackages;
/* 1858:     */         
/* 1859:     */ 
/* 1860:     */ 
/* 1861:     */ 
/* 1862:2024 */         PackageManager.this.m_searchResults.clear();
/* 1863:2025 */         String searchString = PackageManager.this.m_searchField.getText();
/* 1864:2026 */         if ((searchString != null) && (searchString.length() > 0))
/* 1865:     */         {
/* 1866:2027 */           String[] terms = searchString.split(",");
/* 1867:2028 */           for (Package p : toSearch)
/* 1868:     */           {
/* 1869:2029 */             String name = p.getName();
/* 1870:2030 */             String description = (String)PackageManager.this.m_packageDescriptions.get(name);
/* 1871:2031 */             if (description != null) {
/* 1872:2032 */               for (String t : terms) {
/* 1873:2033 */                 if (description.contains(t.trim().toLowerCase()))
/* 1874:     */                 {
/* 1875:2034 */                   PackageManager.this.m_searchResults.add(p);
/* 1876:2035 */                   break;
/* 1877:     */                 }
/* 1878:     */               }
/* 1879:     */             }
/* 1880:     */           }
/* 1881:2041 */           PackageManager.this.m_searchHitsLab.setText(" (Search hits: " + PackageManager.this.m_searchResults.size() + ")");
/* 1882:     */         }
/* 1883:     */         else
/* 1884:     */         {
/* 1885:2044 */           PackageManager.this.m_searchHitsLab.setText("");
/* 1886:     */         }
/* 1887:2046 */         PackageManager.this.updateTable();
/* 1888:     */       }
/* 1889:2049 */     });
/* 1890:2050 */     this.m_browserTools.setFloatable(false);
/* 1891:     */     
/* 1892:     */ 
/* 1893:2053 */     this.m_newPackagesAvailableL = new JLabel(new ImageIcon(loadImage("weka/gui/images/information.gif")));
/* 1894:     */     
/* 1895:     */ 
/* 1896:     */ 
/* 1897:2057 */     Thread homePageThread = new HomePageThread();
/* 1898:     */     
/* 1899:2059 */     homePageThread.setPriority(1);
/* 1900:2060 */     homePageThread.start();
/* 1901:     */     
/* 1902:2062 */     this.m_backB.addActionListener(new ActionListener()
/* 1903:     */     {
/* 1904:     */       public void actionPerformed(ActionEvent e)
/* 1905:     */       {
/* 1906:2065 */         URL previous = (URL)PackageManager.this.m_browserHistory.removeLast();
/* 1907:     */         try
/* 1908:     */         {
/* 1909:2067 */           PackageManager.this.m_infoPane.setPage(previous);
/* 1910:2068 */           if (PackageManager.this.m_browserHistory.size() == 0) {
/* 1911:2069 */             PackageManager.this.m_backB.setEnabled(false);
/* 1912:     */           }
/* 1913:     */         }
/* 1914:     */         catch (IOException ex) {}
/* 1915:     */       }
/* 1916:2076 */     });
/* 1917:2077 */     this.m_homeB.addActionListener(new ActionListener()
/* 1918:     */     {
/* 1919:     */       public void actionPerformed(ActionEvent e)
/* 1920:     */       {
/* 1921:     */         try
/* 1922:     */         {
/* 1923:2081 */           URL back = PackageManager.this.m_infoPane.getPage();
/* 1924:2082 */           if (back != null) {
/* 1925:2083 */             PackageManager.this.m_browserHistory.add(back);
/* 1926:     */           }
/* 1927:2086 */           String initialPage = PackageManager.access$900();
/* 1928:2087 */           PackageManager.this.m_infoPane.setContentType("text/html");
/* 1929:2088 */           PackageManager.this.m_infoPane.setText(initialPage);
/* 1930:2089 */           PackageManager.HomePageThread hp = new PackageManager.HomePageThread(PackageManager.this);
/* 1931:2090 */           hp.setPriority(1);
/* 1932:2091 */           hp.start();
/* 1933:     */         }
/* 1934:     */         catch (Exception ex) {}
/* 1935:     */       }
/* 1936:2097 */     });
/* 1937:2098 */     browserP.add(this.m_browserTools, "North");
/* 1938:2099 */     browserP.add(new JScrollPane(this.m_infoPane), "Center");
/* 1939:     */     
/* 1940:     */ 
/* 1941:2102 */     this.m_splitP = new JSplitPane(0, topPanel, browserP);
/* 1942:2103 */     this.m_splitP.setOneTouchExpandable(true);
/* 1943:     */     
/* 1944:2105 */     add(this.m_splitP, "Center");
/* 1945:     */     
/* 1946:2107 */     updateTable();
/* 1947:2110 */     if (!WekaPackageManager.m_offline)
/* 1948:     */     {
/* 1949:2111 */       System.err.println("Checking for new packages...");
/* 1950:2112 */       CheckForNewPackages cp = new CheckForNewPackages();
/* 1951:2113 */       cp.execute();
/* 1952:     */     }
/* 1953:     */     else
/* 1954:     */     {
/* 1955:2116 */       this.m_installBut.setEnabled(false);
/* 1956:2117 */       this.m_refreshCacheBut.setEnabled(false);
/* 1957:     */     }
/* 1958:     */   }
/* 1959:     */   
/* 1960:     */   private void updateInstallUninstallButtonEnablement()
/* 1961:     */   {
/* 1962:2122 */     boolean enableInstall = false;
/* 1963:2123 */     boolean enableUninstall = false;
/* 1964:2124 */     boolean enableToggleLoadStatus = false;
/* 1965:     */     
/* 1966:2126 */     this.m_unofficialBut.setEnabled(true);
/* 1967:2128 */     if (!this.m_installing)
/* 1968:     */     {
/* 1969:2129 */       int[] selectedRows = this.m_table.getSelectedRows();
/* 1970:2135 */       for (int selectedRow : selectedRows) {
/* 1971:2136 */         if ((!enableInstall) || (!enableUninstall))
/* 1972:     */         {
/* 1973:2137 */           enableInstall = true;
/* 1974:     */           
/* 1975:2139 */           String packageName = this.m_table.getValueAt(selectedRow, getColumnIndex("Package")).toString();
/* 1976:     */           try
/* 1977:     */           {
/* 1978:2143 */             Package p = WekaPackageManager.getRepositoryPackageInfo(packageName);
/* 1979:2145 */             if (!enableUninstall) {
/* 1980:2146 */               enableUninstall = p.isInstalled();
/* 1981:     */             }
/* 1982:2149 */             if (!enableToggleLoadStatus) {
/* 1983:2150 */               enableToggleLoadStatus = p.isInstalled();
/* 1984:     */             }
/* 1985:     */           }
/* 1986:     */           catch (Exception e1)
/* 1987:     */           {
/* 1988:2158 */             enableUninstall = true;
/* 1989:2159 */             enableInstall = false;
/* 1990:     */           }
/* 1991:     */         }
/* 1992:     */       }
/* 1993:     */     }
/* 1994:     */     else
/* 1995:     */     {
/* 1996:2164 */       this.m_unofficialBut.setEnabled(false);
/* 1997:     */     }
/* 1998:2168 */     this.m_installBut.setEnabled((enableInstall) && (!WekaPackageManager.m_offline));
/* 1999:2169 */     this.m_forceBut.setEnabled(enableInstall);
/* 2000:2170 */     this.m_uninstallBut.setEnabled(enableUninstall);
/* 2001:2171 */     this.m_toggleLoad.setEnabled(enableToggleLoadStatus);
/* 2002:     */   }
/* 2003:     */   
/* 2004:     */   private Image loadImage(String path)
/* 2005:     */   {
/* 2006:2175 */     Image pic = null;
/* 2007:2176 */     URL imageURL = getClass().getClassLoader().getResource(path);
/* 2008:2177 */     if (imageURL != null) {
/* 2009:2180 */       pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 2010:     */     }
/* 2011:2183 */     return pic;
/* 2012:     */   }
/* 2013:     */   
/* 2014:     */   private void updateTableForPackageList(List<Package> packageList)
/* 2015:     */   {
/* 2016:2187 */     this.m_table.clearSelection();
/* 2017:2188 */     this.m_model.setRowCount(packageList.size());
/* 2018:2189 */     int row = 0;
/* 2019:2190 */     for (Package p : packageList)
/* 2020:     */     {
/* 2021:2191 */       this.m_model.setValueAt(p.getName(), row, getColumnIndex("Package"));
/* 2022:2192 */       String installedV = "";
/* 2023:2193 */       if (p.isInstalled()) {
/* 2024:     */         try
/* 2025:     */         {
/* 2026:2195 */           Package installed = WekaPackageManager.getInstalledPackageInfo(p.getName());
/* 2027:     */           
/* 2028:2197 */           installedV = installed.getPackageMetaDataElement("Version").toString();
/* 2029:     */         }
/* 2030:     */         catch (Exception ex)
/* 2031:     */         {
/* 2032:2200 */           ex.printStackTrace();
/* 2033:2201 */           displayErrorDialog("An error has occurred while trying to obtain installed package info", ex);
/* 2034:     */         }
/* 2035:     */       }
/* 2036:2206 */       String category = "";
/* 2037:2207 */       if (p.getPackageMetaDataElement("Category") != null) {
/* 2038:2208 */         category = p.getPackageMetaDataElement("Category").toString();
/* 2039:     */       }
/* 2040:2211 */       List<Object> catAndVers = (List)this.m_packageLookupInfo.get(p.getName());
/* 2041:2212 */       Object repositoryV = "-----";
/* 2042:2213 */       if (catAndVers != null)
/* 2043:     */       {
/* 2044:2216 */         List<Object> repVersions = (List)catAndVers.get(1);
/* 2045:2217 */         repositoryV = repVersions.get(0);
/* 2046:     */       }
/* 2047:2222 */       this.m_model.setValueAt(category, row, getColumnIndex("Category"));
/* 2048:2223 */       this.m_model.setValueAt(installedV, row, getColumnIndex("Installed version"));
/* 2049:2224 */       this.m_model.setValueAt(repositoryV, row, getColumnIndex("Repository version"));
/* 2050:2225 */       if (catAndVers != null)
/* 2051:     */       {
/* 2052:2226 */         String loadStatus = (String)catAndVers.get(2);
/* 2053:2227 */         this.m_model.setValueAt(loadStatus, row, getColumnIndex("Loaded"));
/* 2054:     */       }
/* 2055:     */       else
/* 2056:     */       {
/* 2057:2230 */         File packageRoot = new File(WekaPackageManager.getPackageHome().toString() + File.separator + p.getName());
/* 2058:     */         
/* 2059:     */ 
/* 2060:2233 */         boolean loaded = WekaPackageManager.loadCheck(p, packageRoot, new PrintStream[0]);
/* 2061:2234 */         String loadStatus = loaded ? "Yes" : "No - check log";
/* 2062:2235 */         this.m_model.setValueAt(loadStatus, row, getColumnIndex("Loaded"));
/* 2063:     */       }
/* 2064:2237 */       row++;
/* 2065:     */     }
/* 2066:     */   }
/* 2067:     */   
/* 2068:     */   private void updateTable()
/* 2069:     */   {
/* 2070:2243 */     if ((this.m_installedPackages == null) || (this.m_availablePackages == null)) {
/* 2071:2245 */       for (Package p : this.m_allPackages)
/* 2072:     */       {
/* 2073:2246 */         List<Object> catAndVers = (List)this.m_packageLookupInfo.get(p.getName());
/* 2074:2247 */         String loadStatus = catAndVers.get(2).toString();
/* 2075:2248 */         if (p.isInstalled())
/* 2076:     */         {
/* 2077:2249 */           File packageRoot = new File(WekaPackageManager.getPackageHome().toString() + File.separator + p.getName());
/* 2078:     */           
/* 2079:     */ 
/* 2080:2252 */           boolean loaded = WekaPackageManager.loadCheck(p, packageRoot, new PrintStream[0]);
/* 2081:2253 */           boolean userNoLoad = WekaPackageManager.m_doNotLoadList.contains(p.getName());
/* 2082:2255 */           if (!loadStatus.contains("pending")) {
/* 2083:2256 */             loadStatus = userNoLoad ? "No - user flagged" : loaded ? "Yes" : "No - check log";
/* 2084:     */           }
/* 2085:     */         }
/* 2086:2262 */         catAndVers.set(2, loadStatus);
/* 2087:     */       }
/* 2088:     */     }
/* 2089:2266 */     if ((this.m_searchField.getText() != null) && (this.m_searchField.getText().length() > 0))
/* 2090:     */     {
/* 2091:2267 */       updateTableForPackageList(this.m_searchResults);
/* 2092:2268 */       return;
/* 2093:     */     }
/* 2094:2271 */     if (this.m_allBut.isSelected())
/* 2095:     */     {
/* 2096:2272 */       Collections.sort(this.m_allPackages, this.m_packageComparator);
/* 2097:2273 */       updateTableForPackageList(this.m_allPackages);
/* 2098:     */     }
/* 2099:2274 */     else if (this.m_installedBut.isSelected())
/* 2100:     */     {
/* 2101:     */       try
/* 2102:     */       {
/* 2103:2276 */         if (this.m_installedPackages == null) {
/* 2104:2277 */           this.m_installedPackages = WekaPackageManager.getInstalledPackages();
/* 2105:     */         }
/* 2106:2280 */         updateTableForPackageList(this.m_installedPackages);
/* 2107:     */       }
/* 2108:     */       catch (Exception ex)
/* 2109:     */       {
/* 2110:2282 */         ex.printStackTrace();
/* 2111:     */       }
/* 2112:     */     }
/* 2113:     */     else
/* 2114:     */     {
/* 2115:     */       try
/* 2116:     */       {
/* 2117:2286 */         if (this.m_availablePackages == null) {
/* 2118:2287 */           this.m_availablePackages = WekaPackageManager.getAvailableCompatiblePackages();
/* 2119:     */         }
/* 2120:2290 */         updateTableForPackageList(this.m_availablePackages);
/* 2121:     */       }
/* 2122:     */       catch (Exception ex)
/* 2123:     */       {
/* 2124:2292 */         ex.printStackTrace();
/* 2125:     */       }
/* 2126:     */     }
/* 2127:     */   }
/* 2128:     */   
/* 2129:     */   private void displayPackageInfo(int i)
/* 2130:     */   {
/* 2131:2298 */     String packageName = this.m_table.getValueAt(i, getColumnIndex("Package")).toString();
/* 2132:     */     
/* 2133:     */ 
/* 2134:2301 */     boolean repositoryPackage = true;
/* 2135:     */     try
/* 2136:     */     {
/* 2137:2303 */       WekaPackageManager.getRepositoryPackageInfo(packageName);
/* 2138:     */     }
/* 2139:     */     catch (Exception ex)
/* 2140:     */     {
/* 2141:2305 */       repositoryPackage = false;
/* 2142:     */     }
/* 2143:2307 */     String versionURL = WekaPackageManager.getPackageRepositoryURL().toString() + "/" + packageName + "/index.html";
/* 2144:     */     try
/* 2145:     */     {
/* 2146:2312 */       URL back = this.m_infoPane.getPage();
/* 2147:2313 */       if ((this.m_browserHistory.size() == 0) && (back != null)) {
/* 2148:2314 */         this.m_backB.setEnabled(true);
/* 2149:     */       }
/* 2150:2316 */       if (back != null) {
/* 2151:2317 */         this.m_browserHistory.add(back);
/* 2152:     */       }
/* 2153:2320 */       if (repositoryPackage) {
/* 2154:2321 */         this.m_infoPane.setPage(new URL(versionURL));
/* 2155:     */       } else {
/* 2156:     */         try
/* 2157:     */         {
/* 2158:2325 */           Package p = WekaPackageManager.getInstalledPackageInfo(packageName);
/* 2159:2326 */           Map<?, ?> meta = p.getPackageMetaData();
/* 2160:2327 */           Set<?> keys = meta.keySet();
/* 2161:2328 */           StringBuffer sb = new StringBuffer();
/* 2162:2329 */           sb.append(RepositoryIndexGenerator.HEADER);
/* 2163:2330 */           sb.append("<H1>" + packageName + " (Unofficial) </H1>");
/* 2164:2331 */           for (Object k : keys) {
/* 2165:2332 */             if (!k.toString().equals("PackageName"))
/* 2166:     */             {
/* 2167:2333 */               Object value = meta.get(k);
/* 2168:2334 */               sb.append(k + " : " + value + "<p>");
/* 2169:     */             }
/* 2170:     */           }
/* 2171:2337 */           sb.append("</html>\n");
/* 2172:2338 */           this.m_infoPane.setText(sb.toString());
/* 2173:     */         }
/* 2174:     */         catch (Exception e) {}
/* 2175:     */       }
/* 2176:     */     }
/* 2177:     */     catch (Exception ex)
/* 2178:     */     {
/* 2179:2344 */       ex.printStackTrace();
/* 2180:     */     }
/* 2181:2347 */     updateInstallUninstallButtonEnablement();
/* 2182:2348 */     if (this.m_availableBut.isSelected()) {
/* 2183:2349 */       this.m_uninstallBut.setEnabled(false);
/* 2184:     */     }
/* 2185:     */   }
/* 2186:     */   
/* 2187:     */   private void getPackagesAndEstablishLookup()
/* 2188:     */     throws Exception
/* 2189:     */   {
/* 2190:2364 */     this.m_allPackages = WekaPackageManager.getAllPackages();
/* 2191:2365 */     this.m_installedPackages = WekaPackageManager.getInstalledPackages();
/* 2192:     */     
/* 2193:     */ 
/* 2194:2368 */     this.m_packageLookupInfo = new TreeMap();
/* 2195:2371 */     for (Package p : this.m_allPackages)
/* 2196:     */     {
/* 2197:2373 */       String packageName = p.getName();
/* 2198:2374 */       String category = "";
/* 2199:2375 */       if (p.getPackageMetaDataElement("Category") != null) {
/* 2200:2376 */         category = p.getPackageMetaDataElement("Category").toString();
/* 2201:     */       }
/* 2202:2380 */       String loadStatus = "";
/* 2203:2381 */       if (p.isInstalled())
/* 2204:     */       {
/* 2205:2382 */         File packageRoot = new File(WekaPackageManager.getPackageHome().toString());
/* 2206:     */         
/* 2207:2384 */         boolean loaded = WekaPackageManager.loadCheck(p, packageRoot, new PrintStream[0]);
/* 2208:2385 */         loadStatus = loaded ? "Yes" : "No - check log";
/* 2209:     */       }
/* 2210:2388 */       List<Object> versions = WekaPackageManager.getRepositoryPackageVersions(packageName);
/* 2211:     */       
/* 2212:2390 */       List<Object> catAndVers = new ArrayList();
/* 2213:2391 */       catAndVers.add(category);
/* 2214:2392 */       catAndVers.add(versions);
/* 2215:2393 */       catAndVers.add(loadStatus);
/* 2216:2394 */       this.m_packageLookupInfo.put(packageName, catAndVers);
/* 2217:     */     }
/* 2218:2398 */     for (Package p : this.m_allPackages)
/* 2219:     */     {
/* 2220:2399 */       String name = p.getName();
/* 2221:2400 */       File repLatest = new File(WekaPackageManager.WEKA_HOME.toString() + File.separator + "repCache" + File.separator + name + File.separator + "Latest.props");
/* 2222:2404 */       if ((repLatest.exists()) && (repLatest.isFile()))
/* 2223:     */       {
/* 2224:2405 */         String packageDescription = loadPropsText(repLatest);
/* 2225:     */         
/* 2226:2407 */         this.m_packageDescriptions.put(name, packageDescription);
/* 2227:     */       }
/* 2228:     */     }
/* 2229:2413 */     for (Package p : this.m_installedPackages) {
/* 2230:2414 */       if (!this.m_packageDescriptions.containsKey(p.getName()))
/* 2231:     */       {
/* 2232:2415 */         String name = p.getName();
/* 2233:2416 */         File instDesc = new File(WekaPackageManager.PACKAGES_DIR.toString() + File.separator + name + File.separator + "Description.props");
/* 2234:2419 */         if ((instDesc.exists()) && (instDesc.isFile())) {
/* 2235:2420 */           this.m_packageDescriptions.put(name, loadPropsText(instDesc));
/* 2236:     */         }
/* 2237:     */       }
/* 2238:     */     }
/* 2239:     */   }
/* 2240:     */   
/* 2241:     */   private String loadPropsText(File propsToLoad)
/* 2242:     */     throws IOException
/* 2243:     */   {
/* 2244:2427 */     BufferedReader br = new BufferedReader(new FileReader(propsToLoad));
/* 2245:2428 */     StringBuilder builder = new StringBuilder();
/* 2246:2429 */     String line = null;
/* 2247:     */     try
/* 2248:     */     {
/* 2249:2431 */       while ((line = br.readLine()) != null) {
/* 2250:2432 */         if (!line.startsWith("#")) {
/* 2251:2433 */           builder.append(line.toLowerCase()).append("\n");
/* 2252:     */         }
/* 2253:     */       }
/* 2254:     */     }
/* 2255:     */     finally
/* 2256:     */     {
/* 2257:2437 */       br.close();
/* 2258:     */     }
/* 2259:2440 */     return builder.toString();
/* 2260:     */   }
/* 2261:     */   
/* 2262:     */   private void getAllPackages()
/* 2263:     */   {
/* 2264:     */     try
/* 2265:     */     {
/* 2266:2445 */       getPackagesAndEstablishLookup();
/* 2267:     */     }
/* 2268:     */     catch (Exception ex)
/* 2269:     */     {
/* 2270:2449 */       ex.printStackTrace();
/* 2271:2450 */       System.err.println("A problem has occurred whilst trying to get all package information. Trying a cache refresh...");
/* 2272:     */       
/* 2273:2452 */       WekaPackageManager.refreshCache(new PrintStream[] { System.out });
/* 2274:     */       try
/* 2275:     */       {
/* 2276:2455 */         getPackagesAndEstablishLookup();
/* 2277:     */       }
/* 2278:     */       catch (Exception e)
/* 2279:     */       {
/* 2280:2457 */         e.printStackTrace();
/* 2281:     */       }
/* 2282:     */     }
/* 2283:     */   }
/* 2284:     */   
/* 2285:     */   private void displayErrorDialog(String message, Exception e)
/* 2286:     */   {
/* 2287:2463 */     StringWriter sw = new StringWriter();
/* 2288:2464 */     e.printStackTrace(new PrintWriter(sw));
/* 2289:     */     
/* 2290:2466 */     String result = sw.toString();
/* 2291:2467 */     displayErrorDialog(message, result);
/* 2292:     */   }
/* 2293:     */   
/* 2294:     */   private void displayErrorDialog(String message, String stackTrace)
/* 2295:     */   {
/* 2296:2471 */     Object[] options = null;
/* 2297:2473 */     if ((stackTrace != null) && (stackTrace.length() > 0))
/* 2298:     */     {
/* 2299:2474 */       options = new Object[2];
/* 2300:2475 */       options[0] = "OK";
/* 2301:2476 */       options[1] = "Show error";
/* 2302:     */     }
/* 2303:     */     else
/* 2304:     */     {
/* 2305:2478 */       options = new Object[1];
/* 2306:2479 */       options[0] = "OK";
/* 2307:     */     }
/* 2308:2481 */     int result = JOptionPane.showOptionDialog(this, message, "Weka Package Manager", 0, 0, null, options, options[0]);
/* 2309:2486 */     if (result == 1)
/* 2310:     */     {
/* 2311:2487 */       JTextArea jt = new JTextArea(stackTrace, 10, 40);
/* 2312:2488 */       JOptionPane.showMessageDialog(this, new JScrollPane(jt), "Weka Package Manager", 0);
/* 2313:     */     }
/* 2314:     */   }
/* 2315:     */   
/* 2316:     */   public void setInitialSplitPaneDividerLocation()
/* 2317:     */   {
/* 2318:2503 */     this.m_splitP.setDividerLocation(0.4D);
/* 2319:     */   }
/* 2320:     */   
/* 2321:     */   public static void main(String[] args)
/* 2322:     */   {
/* 2323:2507 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 2324:     */     
/* 2325:2509 */     LookAndFeel.setLookAndFeel();
/* 2326:     */     
/* 2327:2511 */     PackageManager pm = new PackageManager();
/* 2328:2513 */     if (!WekaPackageManager.m_noPackageMetaDataAvailable)
/* 2329:     */     {
/* 2330:2514 */       String offline = "";
/* 2331:2515 */       if (WekaPackageManager.m_offline) {
/* 2332:2516 */         offline = " (offline)";
/* 2333:     */       }
/* 2334:2518 */       JFrame jf = new JFrame("Weka Package Manager" + offline);
/* 2335:     */       
/* 2336:2520 */       jf.getContentPane().setLayout(new BorderLayout());
/* 2337:2521 */       jf.getContentPane().add(pm, "Center");
/* 2338:2522 */       jf.addWindowListener(new WindowAdapter()
/* 2339:     */       {
/* 2340:     */         public void windowClosing(WindowEvent e)
/* 2341:     */         {
/* 2342:2525 */           this.val$jf.dispose();
/* 2343:2526 */           System.exit(0);
/* 2344:     */         }
/* 2345:2528 */       });
/* 2346:2529 */       Dimension screenSize = jf.getToolkit().getScreenSize();
/* 2347:2530 */       int width = screenSize.width * 8 / 10;
/* 2348:2531 */       int height = screenSize.height * 8 / 10;
/* 2349:2532 */       jf.setBounds(width / 8, height / 8, width, height);
/* 2350:2533 */       jf.setVisible(true);
/* 2351:2534 */       pm.setInitialSplitPaneDividerLocation();
/* 2352:     */     }
/* 2353:     */   }
/* 2354:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PackageManager
 * JD-Core Version:    0.7.0.1
 */