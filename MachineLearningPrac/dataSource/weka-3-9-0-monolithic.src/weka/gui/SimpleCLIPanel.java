/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Cursor;
/*    6:     */ import java.awt.Font;
/*    7:     */ import java.awt.Frame;
/*    8:     */ import java.awt.Window;
/*    9:     */ import java.awt.event.ActionEvent;
/*   10:     */ import java.awt.event.ActionListener;
/*   11:     */ import java.awt.event.KeyAdapter;
/*   12:     */ import java.awt.event.KeyEvent;
/*   13:     */ import java.awt.event.WindowEvent;
/*   14:     */ import java.io.BufferedOutputStream;
/*   15:     */ import java.io.File;
/*   16:     */ import java.io.FileOutputStream;
/*   17:     */ import java.io.PrintStream;
/*   18:     */ import java.lang.reflect.Method;
/*   19:     */ import java.util.Collections;
/*   20:     */ import java.util.Enumeration;
/*   21:     */ import java.util.HashSet;
/*   22:     */ import java.util.List;
/*   23:     */ import java.util.Properties;
/*   24:     */ import java.util.Vector;
/*   25:     */ import javax.swing.Icon;
/*   26:     */ import javax.swing.ImageIcon;
/*   27:     */ import javax.swing.JFrame;
/*   28:     */ import javax.swing.JInternalFrame;
/*   29:     */ import javax.swing.JMenu;
/*   30:     */ import javax.swing.JMenuBar;
/*   31:     */ import javax.swing.JOptionPane;
/*   32:     */ import javax.swing.JScrollPane;
/*   33:     */ import javax.swing.JTextField;
/*   34:     */ import javax.swing.JTextPane;
/*   35:     */ import javax.swing.text.Document;
/*   36:     */ import weka.Run;
/*   37:     */ import weka.core.Capabilities;
/*   38:     */ import weka.core.CapabilitiesHandler;
/*   39:     */ import weka.core.ClassDiscovery;
/*   40:     */ import weka.core.Defaults;
/*   41:     */ import weka.core.Instances;
/*   42:     */ import weka.core.OptionHandler;
/*   43:     */ import weka.core.Trie;
/*   44:     */ import weka.core.Utils;
/*   45:     */ import weka.gui.knowledgeflow.StepVisual;
/*   46:     */ import weka.gui.scripting.ScriptingPanel;
/*   47:     */ 
/*   48:     */ @PerspectiveInfo(ID="simplecli", title="Simple CLI", toolTipText="Simple CLI for Weka", iconPath="weka/gui/weka_icon_new_small.png")
/*   49:     */ public class SimpleCLIPanel
/*   50:     */   extends ScriptingPanel
/*   51:     */   implements ActionListener, Perspective
/*   52:     */ {
/*   53:     */   private static final long serialVersionUID = 1089039734615114942L;
/*   54:  86 */   protected static String FILENAME = "SimpleCLI.props";
/*   55:  89 */   protected static String PROPERTY_FILE = "weka/gui/" + FILENAME;
/*   56:     */   protected static Properties PROPERTIES;
/*   57:     */   protected GUIApplication m_mainApp;
/*   58:     */   protected Icon m_perspectiveIcon;
/*   59:     */   protected JTextPane m_OutputArea;
/*   60:     */   protected JTextField m_Input;
/*   61:     */   protected Vector<String> m_CommandHistory;
/*   62:     */   protected int m_HistoryPos;
/*   63:     */   protected Thread m_RunThread;
/*   64:     */   protected CommandlineCompletion m_Completion;
/*   65:     */   
/*   66:     */   static
/*   67:     */   {
/*   68:     */     try
/*   69:     */     {
/*   70: 103 */       PROPERTIES = Utils.readProperties(PROPERTY_FILE);
/*   71: 104 */       Enumeration<?> keys = PROPERTIES.propertyNames();
/*   72: 105 */       if (!keys.hasMoreElements()) {
/*   73: 106 */         throw new Exception("Failed to read a property file for the SimpleCLI");
/*   74:     */       }
/*   75:     */     }
/*   76:     */     catch (Exception ex)
/*   77:     */     {
/*   78: 109 */       JOptionPane.showMessageDialog(null, "Could not read a configuration file for the SimpleCLI.\nAn example file is included with the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\" and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "SimpleCLI", 0);
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   public boolean okToBeActive()
/*   83:     */   {
/*   84: 145 */     return true;
/*   85:     */   }
/*   86:     */   
/*   87:     */   public void setMainApplication(GUIApplication main)
/*   88:     */   {
/*   89: 160 */     this.m_mainApp = main;
/*   90:     */   }
/*   91:     */   
/*   92:     */   public GUIApplication getMainApplication()
/*   93:     */   {
/*   94: 165 */     return this.m_mainApp;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public String getPerspectiveID()
/*   98:     */   {
/*   99: 170 */     return "simplecli";
/*  100:     */   }
/*  101:     */   
/*  102:     */   public String getPerspectiveTitle()
/*  103:     */   {
/*  104: 175 */     return "Simple CLI";
/*  105:     */   }
/*  106:     */   
/*  107:     */   public Icon getPerspectiveIcon()
/*  108:     */   {
/*  109: 180 */     if (this.m_perspectiveIcon != null) {
/*  110: 181 */       return this.m_perspectiveIcon;
/*  111:     */     }
/*  112: 184 */     PerspectiveInfo perspectiveA = (PerspectiveInfo)getClass().getAnnotation(PerspectiveInfo.class);
/*  113: 186 */     if ((perspectiveA != null) && (perspectiveA.iconPath() != null) && (perspectiveA.iconPath().length() > 0)) {
/*  114: 188 */       this.m_perspectiveIcon = StepVisual.loadIcon(perspectiveA.iconPath());
/*  115:     */     }
/*  116: 191 */     return this.m_perspectiveIcon;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public String getPerspectiveTipText()
/*  120:     */   {
/*  121: 196 */     return "Simple CLI interface for Weka";
/*  122:     */   }
/*  123:     */   
/*  124:     */   public List<JMenu> getMenus()
/*  125:     */   {
/*  126: 201 */     return null;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public Defaults getDefaultSettings()
/*  130:     */   {
/*  131: 206 */     return null;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public boolean acceptsInstances()
/*  135:     */   {
/*  136: 216 */     return false;
/*  137:     */   }
/*  138:     */   
/*  139:     */   public boolean requiresLog()
/*  140:     */   {
/*  141: 226 */     return false;
/*  142:     */   }
/*  143:     */   
/*  144:     */   class ClassRunner
/*  145:     */     extends Thread
/*  146:     */   {
/*  147:     */     protected Method m_MainMethod;
/*  148:     */     String[] m_CommandArgs;
/*  149:     */     protected boolean m_classIsRun;
/*  150:     */     
/*  151:     */     public ClassRunner(String[] theClass)
/*  152:     */       throws Exception
/*  153:     */     {
/*  154: 262 */       this.m_classIsRun = (theClass == Run.class);
/*  155:     */       
/*  156: 264 */       setDaemon(true);
/*  157: 265 */       Class<?>[] argTemplate = { [Ljava.lang.String.class };
/*  158: 266 */       this.m_CommandArgs = commandArgs;
/*  159: 267 */       if ((this.m_classIsRun) && 
/*  160: 268 */         (!commandArgs[0].equalsIgnoreCase("-h")) && (!commandArgs[0].equalsIgnoreCase("-help")))
/*  161:     */       {
/*  162: 270 */         this.m_CommandArgs = new String[commandArgs.length + 1];
/*  163: 271 */         System.arraycopy(commandArgs, 0, this.m_CommandArgs, 1, commandArgs.length);
/*  164:     */         
/*  165: 273 */         this.m_CommandArgs[0] = "-do-not-prompt-if-multiple-matches";
/*  166:     */       }
/*  167: 276 */       this.m_MainMethod = theClass.getMethod("main", argTemplate);
/*  168: 277 */       if (((this.m_MainMethod.getModifiers() & 0x8) == 0) || ((this.m_MainMethod.getModifiers() & 0x1) == 0)) {
/*  169: 279 */         throw new NoSuchMethodException("main(String[]) method of " + theClass.getName() + " is not public and static.");
/*  170:     */       }
/*  171:     */     }
/*  172:     */     
/*  173:     */     public void run()
/*  174:     */     {
/*  175: 289 */       PrintStream outOld = null;
/*  176: 290 */       PrintStream outNew = null;
/*  177: 291 */       String outFilename = null;
/*  178: 294 */       if (this.m_CommandArgs.length > 2)
/*  179:     */       {
/*  180: 295 */         String action = this.m_CommandArgs[(this.m_CommandArgs.length - 2)];
/*  181: 296 */         if (action.equals(">"))
/*  182:     */         {
/*  183: 297 */           outOld = System.out;
/*  184:     */           try
/*  185:     */           {
/*  186: 299 */             outFilename = this.m_CommandArgs[(this.m_CommandArgs.length - 1)];
/*  187: 302 */             if (outFilename.startsWith("~")) {
/*  188: 303 */               outFilename = outFilename.replaceFirst("~", System.getProperty("user.home"));
/*  189:     */             }
/*  190: 306 */             outNew = new PrintStream(new File(outFilename));
/*  191: 307 */             System.setOut(outNew);
/*  192: 308 */             this.m_CommandArgs[(this.m_CommandArgs.length - 2)] = "";
/*  193: 309 */             this.m_CommandArgs[(this.m_CommandArgs.length - 1)] = "";
/*  194:     */             
/*  195:     */ 
/*  196: 312 */             String[] newArgs = new String[this.m_CommandArgs.length - 2];
/*  197: 313 */             System.arraycopy(this.m_CommandArgs, 0, newArgs, 0, this.m_CommandArgs.length - 2);
/*  198:     */             
/*  199: 315 */             this.m_CommandArgs = newArgs;
/*  200:     */           }
/*  201:     */           catch (Exception e)
/*  202:     */           {
/*  203: 317 */             System.setOut(outOld);
/*  204: 318 */             outOld = null;
/*  205:     */           }
/*  206:     */         }
/*  207:     */       }
/*  208:     */       try
/*  209:     */       {
/*  210: 324 */         Object[] args = { this.m_CommandArgs };
/*  211: 325 */         this.m_MainMethod.invoke(null, args);
/*  212: 326 */         if (isInterrupted()) {
/*  213: 327 */           System.err.println("[...Interrupted]");
/*  214:     */         }
/*  215:     */       }
/*  216:     */       catch (Exception ex)
/*  217:     */       {
/*  218: 330 */         if (ex.getMessage() == null) {
/*  219: 331 */           System.err.println("[...Killed]");
/*  220:     */         } else {
/*  221: 333 */           System.err.println("[Run exception] " + ex.getMessage());
/*  222:     */         }
/*  223:     */       }
/*  224:     */       finally
/*  225:     */       {
/*  226: 336 */         SimpleCLIPanel.this.m_RunThread = null;
/*  227:     */       }
/*  228: 340 */       if (outOld != null)
/*  229:     */       {
/*  230: 341 */         outNew.flush();
/*  231: 342 */         outNew.close();
/*  232: 343 */         System.setOut(outOld);
/*  233: 344 */         System.out.println("Finished redirecting output to '" + outFilename + "'.");
/*  234:     */       }
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   public static class CommandlineCompletion
/*  239:     */   {
/*  240:     */     protected Vector<String> m_Packages;
/*  241:     */     protected Trie m_Trie;
/*  242: 365 */     protected boolean m_Debug = false;
/*  243:     */     
/*  244:     */     public CommandlineCompletion()
/*  245:     */     {
/*  246: 374 */       if (this.m_Packages == null)
/*  247:     */       {
/*  248: 376 */         Vector<String> list = ClassDiscovery.findPackages();
/*  249:     */         
/*  250:     */ 
/*  251: 379 */         HashSet<String> set = new HashSet();
/*  252: 380 */         for (int i = 0; i < list.size(); i++)
/*  253:     */         {
/*  254: 381 */           String[] parts = ((String)list.get(i)).split("\\.");
/*  255: 382 */           for (int n = 1; n < parts.length; n++)
/*  256:     */           {
/*  257: 383 */             String pkg = "";
/*  258: 384 */             for (int m = 0; m <= n; m++)
/*  259:     */             {
/*  260: 385 */               if (m > 0) {
/*  261: 386 */                 pkg = pkg + ".";
/*  262:     */               }
/*  263: 388 */               pkg = pkg + parts[m];
/*  264:     */             }
/*  265: 390 */             set.add(pkg);
/*  266:     */           }
/*  267:     */         }
/*  268: 395 */         this.m_Packages = new Vector();
/*  269: 396 */         this.m_Packages.addAll(set);
/*  270: 397 */         Collections.sort(this.m_Packages);
/*  271:     */         
/*  272: 399 */         this.m_Trie = new Trie();
/*  273: 400 */         this.m_Trie.addAll(this.m_Packages);
/*  274:     */       }
/*  275:     */     }
/*  276:     */     
/*  277:     */     public boolean getDebug()
/*  278:     */     {
/*  279: 410 */       return this.m_Debug;
/*  280:     */     }
/*  281:     */     
/*  282:     */     public void setDebug(boolean value)
/*  283:     */     {
/*  284: 419 */       this.m_Debug = value;
/*  285:     */     }
/*  286:     */     
/*  287:     */     public boolean isClassname(String partial)
/*  288:     */     {
/*  289: 431 */       return partial.replaceAll("[a-zA-Z0-9\\-\\.]*", "").length() == 0;
/*  290:     */     }
/*  291:     */     
/*  292:     */     public String getPackage(String partial)
/*  293:     */     {
/*  294: 446 */       String result = "";
/*  295: 447 */       boolean wasDot = false;
/*  296: 448 */       for (int i = 0; i < partial.length(); i++)
/*  297:     */       {
/*  298: 449 */         char c = partial.charAt(i);
/*  299: 452 */         if ((wasDot) && (c >= 'A') && (c <= 'Z')) {
/*  300:     */           break;
/*  301:     */         }
/*  302: 456 */         if (c == '.')
/*  303:     */         {
/*  304: 457 */           wasDot = true;
/*  305: 458 */           result = result + "" + c;
/*  306:     */         }
/*  307:     */         else
/*  308:     */         {
/*  309: 462 */           wasDot = false;
/*  310: 463 */           result = result + "" + c;
/*  311:     */         }
/*  312:     */       }
/*  313: 468 */       if (result.endsWith(".")) {
/*  314: 469 */         result = result.substring(0, result.length() - 1);
/*  315:     */       }
/*  316: 472 */       return result;
/*  317:     */     }
/*  318:     */     
/*  319:     */     public String getClassname(String partial)
/*  320:     */     {
/*  321: 485 */       String pkg = getPackage(partial);
/*  322:     */       String result;
/*  323:     */       String result;
/*  324: 486 */       if (pkg.length() + 1 < partial.length()) {
/*  325: 487 */         result = partial.substring(pkg.length() + 1);
/*  326:     */       } else {
/*  327: 489 */         result = "";
/*  328:     */       }
/*  329: 492 */       return result;
/*  330:     */     }
/*  331:     */     
/*  332:     */     public Vector<String> getFileMatches(String partial)
/*  333:     */     {
/*  334: 512 */       Vector<String> result = new Vector();
/*  335:     */       
/*  336:     */ 
/*  337: 515 */       boolean caseSensitive = File.separatorChar != '\\';
/*  338: 516 */       if (this.m_Debug) {
/*  339: 517 */         System.out.println("case-sensitive=" + caseSensitive);
/*  340:     */       }
/*  341: 521 */       if (partial.startsWith("~")) {
/*  342: 522 */         partial = System.getProperty("user.home") + partial.substring(1);
/*  343:     */       }
/*  344: 526 */       File file = new File(partial);
/*  345: 527 */       File dir = null;
/*  346: 528 */       String prefix = null;
/*  347: 529 */       if (file.exists())
/*  348:     */       {
/*  349: 531 */         if (file.isDirectory())
/*  350:     */         {
/*  351: 532 */           dir = file;
/*  352: 533 */           prefix = null;
/*  353:     */         }
/*  354:     */         else
/*  355:     */         {
/*  356: 535 */           dir = file.getParentFile();
/*  357: 536 */           prefix = file.getName();
/*  358:     */         }
/*  359:     */       }
/*  360:     */       else
/*  361:     */       {
/*  362: 539 */         dir = file.getParentFile();
/*  363: 540 */         prefix = file.getName();
/*  364:     */       }
/*  365: 543 */       if (this.m_Debug) {
/*  366: 544 */         System.out.println("search in dir=" + dir + ", prefix=" + prefix);
/*  367:     */       }
/*  368: 548 */       if (dir != null)
/*  369:     */       {
/*  370: 549 */         File[] files = dir.listFiles();
/*  371: 550 */         if (files != null) {
/*  372: 551 */           for (int i = 0; i < files.length; i++)
/*  373:     */           {
/*  374: 552 */             String name = files[i].getName();
/*  375:     */             boolean match;
/*  376:     */             boolean match;
/*  377: 555 */             if ((prefix != null) && (caseSensitive))
/*  378:     */             {
/*  379: 556 */               match = name.startsWith(prefix);
/*  380:     */             }
/*  381:     */             else
/*  382:     */             {
/*  383:     */               boolean match;
/*  384: 557 */               if ((prefix != null) && (!caseSensitive)) {
/*  385: 558 */                 match = name.toLowerCase().startsWith(prefix.toLowerCase());
/*  386:     */               } else {
/*  387: 560 */                 match = true;
/*  388:     */               }
/*  389:     */             }
/*  390: 563 */             if (match) {
/*  391: 564 */               if (prefix != null) {
/*  392: 565 */                 result.add(partial.substring(0, partial.length() - prefix.length()) + name);
/*  393: 569 */               } else if ((partial.endsWith("\\")) || (partial.endsWith("/"))) {
/*  394: 570 */                 result.add(partial + name);
/*  395:     */               } else {
/*  396: 572 */                 result.add(partial + File.separator + name);
/*  397:     */               }
/*  398:     */             }
/*  399:     */           }
/*  400:     */         }
/*  401: 578 */         System.err.println("Invalid path: " + partial);
/*  402:     */       }
/*  403: 583 */       if (result.size() > 1) {
/*  404: 584 */         Collections.sort(result);
/*  405:     */       }
/*  406: 588 */       if (this.m_Debug)
/*  407:     */       {
/*  408: 589 */         System.out.println("file matches:");
/*  409: 590 */         for (int i = 0; i < result.size(); i++) {
/*  410: 591 */           System.out.println((String)result.get(i));
/*  411:     */         }
/*  412:     */       }
/*  413: 595 */       return result;
/*  414:     */     }
/*  415:     */     
/*  416:     */     public Vector<String> getClassMatches(String partial)
/*  417:     */     {
/*  418: 615 */       String pkg = getPackage(partial);
/*  419: 616 */       String cls = getClassname(partial);
/*  420: 618 */       if (getDebug()) {
/*  421: 619 */         System.out.println("\nsearch for: '" + partial + "' => package=" + pkg + ", class=" + cls);
/*  422:     */       }
/*  423: 623 */       Vector<String> result = new Vector();
/*  424: 626 */       if (cls.length() == 0)
/*  425:     */       {
/*  426: 627 */         Vector<String> list = this.m_Trie.getWithPrefix(pkg);
/*  427: 628 */         HashSet<String> set = new HashSet();
/*  428: 629 */         for (int i = 0; i < list.size(); i++)
/*  429:     */         {
/*  430: 630 */           String tmpStr = (String)list.get(i);
/*  431: 631 */           if (tmpStr.length() >= partial.length()) {
/*  432: 634 */             if (!tmpStr.equals(partial))
/*  433:     */             {
/*  434: 638 */               int index = tmpStr.indexOf('.', partial.length() + 1);
/*  435: 639 */               if (index > -1) {
/*  436: 640 */                 set.add(tmpStr.substring(0, index));
/*  437:     */               } else {
/*  438: 642 */                 set.add(tmpStr);
/*  439:     */               }
/*  440:     */             }
/*  441:     */           }
/*  442:     */         }
/*  443: 646 */         result.addAll(set);
/*  444: 647 */         if (result.size() > 1) {
/*  445: 648 */           Collections.sort(result);
/*  446:     */         }
/*  447:     */       }
/*  448: 653 */       Vector<String> list = ClassDiscovery.find(Object.class, pkg);
/*  449: 654 */       Trie tmpTrie = new Trie();
/*  450: 655 */       tmpTrie.addAll(list);
/*  451: 656 */       list = tmpTrie.getWithPrefix(partial);
/*  452: 657 */       result.addAll(list);
/*  453: 660 */       if (result.size() > 1) {
/*  454: 661 */         Collections.sort(result);
/*  455:     */       }
/*  456: 665 */       if (this.m_Debug)
/*  457:     */       {
/*  458: 666 */         System.out.println("class/package matches:");
/*  459: 667 */         for (int i = 0; i < result.size(); i++) {
/*  460: 668 */           System.out.println((String)result.get(i));
/*  461:     */         }
/*  462:     */       }
/*  463: 672 */       return result;
/*  464:     */     }
/*  465:     */     
/*  466:     */     public Vector<String> getMatches(String partial)
/*  467:     */     {
/*  468: 682 */       if (isClassname(partial)) {
/*  469: 683 */         return getClassMatches(partial);
/*  470:     */       }
/*  471: 685 */       return getFileMatches(partial);
/*  472:     */     }
/*  473:     */     
/*  474:     */     public String getCommonPrefix(Vector<String> list)
/*  475:     */     {
/*  476: 699 */       Trie trie = new Trie();
/*  477: 700 */       trie.addAll(list);
/*  478: 701 */       String result = trie.getCommonPrefix();
/*  479: 703 */       if (this.m_Debug) {
/*  480: 704 */         System.out.println(list + "\n  --> common prefix: '" + result + "'");
/*  481:     */       }
/*  482: 707 */       return result;
/*  483:     */     }
/*  484:     */   }
/*  485:     */   
/*  486:     */   protected void initialize()
/*  487:     */   {
/*  488: 716 */     super.initialize();
/*  489:     */     
/*  490: 718 */     this.m_CommandHistory = new Vector();
/*  491: 719 */     this.m_HistoryPos = 0;
/*  492: 720 */     this.m_Completion = new CommandlineCompletion();
/*  493:     */   }
/*  494:     */   
/*  495:     */   protected void initGUI()
/*  496:     */   {
/*  497: 728 */     super.initGUI();
/*  498:     */     
/*  499: 730 */     setLayout(new BorderLayout());
/*  500:     */     
/*  501: 732 */     this.m_OutputArea = new JTextPane();
/*  502: 733 */     this.m_OutputArea.setEditable(false);
/*  503: 734 */     this.m_OutputArea.setFont(new Font("Monospaced", 0, 12));
/*  504: 735 */     add(new JScrollPane(this.m_OutputArea), "Center");
/*  505:     */     
/*  506: 737 */     this.m_Input = new JTextField();
/*  507: 738 */     this.m_Input.setFont(new Font("Monospaced", 0, 12));
/*  508: 739 */     this.m_Input.addActionListener(this);
/*  509: 740 */     this.m_Input.setFocusTraversalKeysEnabled(false);
/*  510: 741 */     this.m_Input.addKeyListener(new KeyAdapter()
/*  511:     */     {
/*  512:     */       public void keyPressed(KeyEvent e)
/*  513:     */       {
/*  514: 744 */         SimpleCLIPanel.this.doHistory(e);
/*  515: 745 */         SimpleCLIPanel.this.doCommandlineCompletion(e);
/*  516:     */       }
/*  517: 747 */     });
/*  518: 748 */     add(this.m_Input, "South");
/*  519:     */   }
/*  520:     */   
/*  521:     */   protected void initFinish()
/*  522:     */   {
/*  523: 756 */     super.initFinish();
/*  524:     */     
/*  525: 758 */     System.out.println("\nWelcome to the WEKA SimpleCLI\n\nEnter commands in the textfield at the bottom of \nthe window. Use the up and down arrows to move \nthrough previous commands.\nCommand completion for classnames and files is \ninitiated with <Tab>. In order to distinguish \nbetween files and classnames, file names must \nbe either absolute or start with '." + File.separator + "' or '~/'\n" + "(the latter is a shortcut for the home directory).\n" + "<Alt+BackSpace> is used for deleting the text\n" + "in the commandline in chunks.\n");
/*  526:     */     try
/*  527:     */     {
/*  528: 770 */       runCommand("help");
/*  529:     */     }
/*  530:     */     catch (Exception e) {}
/*  531: 775 */     loadHistory();
/*  532:     */   }
/*  533:     */   
/*  534:     */   public ImageIcon getIcon()
/*  535:     */   {
/*  536: 785 */     return ComponentHelper.getImageIcon("weka_icon_new_48.png");
/*  537:     */   }
/*  538:     */   
/*  539:     */   public String getTitle()
/*  540:     */   {
/*  541: 795 */     return "SimpleCLI";
/*  542:     */   }
/*  543:     */   
/*  544:     */   public JTextPane getOutput()
/*  545:     */   {
/*  546: 806 */     return this.m_OutputArea;
/*  547:     */   }
/*  548:     */   
/*  549:     */   public JMenuBar getMenuBar()
/*  550:     */   {
/*  551: 816 */     return null;
/*  552:     */   }
/*  553:     */   
/*  554:     */   public void runCommand(String commands)
/*  555:     */     throws Exception
/*  556:     */   {
/*  557: 828 */     System.out.println("> " + commands + '\n');
/*  558: 829 */     System.out.flush();
/*  559: 830 */     String[] commandArgs = Utils.splitOptions(commands);
/*  560: 831 */     if (commandArgs.length == 0) {
/*  561: 832 */       return;
/*  562:     */     }
/*  563: 834 */     if (commandArgs[0].equals("java"))
/*  564:     */     {
/*  565: 836 */       commandArgs[0] = "";
/*  566:     */       try
/*  567:     */       {
/*  568: 838 */         if (commandArgs.length == 1) {
/*  569: 839 */           throw new Exception("No class name given");
/*  570:     */         }
/*  571: 841 */         String className = commandArgs[1];
/*  572: 842 */         commandArgs[1] = "";
/*  573: 843 */         if (this.m_RunThread != null) {
/*  574: 844 */           throw new Exception("An object is already running, use \"break\" to interrupt it.");
/*  575:     */         }
/*  576: 847 */         Class<?> theClass = Class.forName(className);
/*  577:     */         
/*  578:     */ 
/*  579:     */ 
/*  580:     */ 
/*  581: 852 */         Vector<String> argv = new Vector();
/*  582: 853 */         for (int i = 2; i < commandArgs.length; i++) {
/*  583: 854 */           argv.add(commandArgs[i]);
/*  584:     */         }
/*  585: 857 */         this.m_RunThread = new ClassRunner(theClass, (String[])argv.toArray(new String[argv.size()]));
/*  586:     */         
/*  587: 859 */         this.m_RunThread.setPriority(1);
/*  588: 860 */         this.m_RunThread.start();
/*  589:     */       }
/*  590:     */       catch (Exception ex)
/*  591:     */       {
/*  592: 862 */         System.err.println(ex.getMessage());
/*  593:     */       }
/*  594:     */     }
/*  595: 865 */     else if (commandArgs[0].equals("capabilities"))
/*  596:     */     {
/*  597:     */       try
/*  598:     */       {
/*  599: 867 */         Object obj = Class.forName(commandArgs[1]).newInstance();
/*  600: 868 */         if ((obj instanceof CapabilitiesHandler))
/*  601:     */         {
/*  602: 869 */           if ((obj instanceof OptionHandler))
/*  603:     */           {
/*  604: 870 */             Vector<String> args = new Vector();
/*  605: 871 */             for (int i = 2; i < commandArgs.length; i++) {
/*  606: 872 */               args.add(commandArgs[i]);
/*  607:     */             }
/*  608: 874 */             ((OptionHandler)obj).setOptions((String[])args.toArray(new String[args.size()]));
/*  609:     */           }
/*  610: 877 */           Capabilities caps = ((CapabilitiesHandler)obj).getCapabilities();
/*  611: 878 */           System.out.println(caps.toString().replace("[", "\n").replace("]", "\n"));
/*  612:     */         }
/*  613:     */         else
/*  614:     */         {
/*  615: 881 */           System.out.println("'" + commandArgs[1] + "' is not a " + CapabilitiesHandler.class.getName() + "!");
/*  616:     */         }
/*  617:     */       }
/*  618:     */       catch (Exception e)
/*  619:     */       {
/*  620: 885 */         System.err.println(e.getMessage());
/*  621:     */       }
/*  622:     */     }
/*  623: 887 */     else if (commandArgs[0].equals("cls"))
/*  624:     */     {
/*  625: 889 */       this.m_OutputArea.setText("");
/*  626:     */     }
/*  627: 890 */     else if (commandArgs[0].equals("history"))
/*  628:     */     {
/*  629: 891 */       System.out.println("Command history:");
/*  630: 892 */       for (int i = 0; i < this.m_CommandHistory.size(); i++) {
/*  631: 893 */         System.out.println((String)this.m_CommandHistory.get(i));
/*  632:     */       }
/*  633: 895 */       System.out.println();
/*  634:     */     }
/*  635: 896 */     else if (commandArgs[0].equals("kill"))
/*  636:     */     {
/*  637: 897 */       if (this.m_RunThread == null)
/*  638:     */       {
/*  639: 898 */         System.err.println("Nothing is currently running.");
/*  640:     */       }
/*  641:     */       else
/*  642:     */       {
/*  643: 900 */         System.out.println("[Kill...]");
/*  644: 901 */         this.m_RunThread.stop();
/*  645: 902 */         this.m_RunThread = null;
/*  646:     */       }
/*  647:     */     }
/*  648: 904 */     else if (commandArgs[0].equals("exit"))
/*  649:     */     {
/*  650: 907 */       Container parent = getParent();
/*  651: 908 */       Container frame = null;
/*  652: 909 */       boolean finished = false;
/*  653: 910 */       while (!finished)
/*  654:     */       {
/*  655: 911 */         if (((parent instanceof JFrame)) || ((parent instanceof Frame)) || ((parent instanceof JInternalFrame)))
/*  656:     */         {
/*  657: 913 */           frame = parent;
/*  658: 914 */           finished = true;
/*  659:     */         }
/*  660: 917 */         if (!finished)
/*  661:     */         {
/*  662: 918 */           parent = parent.getParent();
/*  663: 919 */           finished = parent == null;
/*  664:     */         }
/*  665:     */       }
/*  666: 923 */       if (frame != null) {
/*  667: 924 */         if ((frame instanceof JInternalFrame)) {
/*  668: 925 */           ((JInternalFrame)frame).doDefaultCloseAction();
/*  669:     */         } else {
/*  670: 927 */           ((Window)frame).dispatchEvent(new WindowEvent((Window)frame, 201));
/*  671:     */         }
/*  672:     */       }
/*  673:     */     }
/*  674:     */     else
/*  675:     */     {
/*  676: 933 */       boolean help = (commandArgs.length > 1) && (commandArgs[0].equals("help"));
/*  677: 935 */       if ((help) && (commandArgs[1].equals("java"))) {
/*  678: 936 */         System.out.println("java <classname> <args>\n\nStarts the main method of <classname> with the supplied command line arguments (if any).\nThe command is started in a separate thread, and may be interrupted with the \"break\"\ncommand (friendly), or killed with the \"kill\" command (unfriendly).\nRedirecting can be done with '>' followed by the file to write to, e.g.:\n  java some.Class > ." + File.separator + "some.txt");
/*  679: 946 */       } else if ((help) && (commandArgs[1].equals("break"))) {
/*  680: 947 */         System.out.println("break\n\nAttempts to nicely interrupt the running job, if any. If this doesn't respond in an\nacceptable time, use \"kill\".\n");
/*  681: 951 */       } else if ((help) && (commandArgs[1].equals("kill"))) {
/*  682: 952 */         System.out.println("kill\n\nKills the running job, if any. You should only use this if the job doesn't respond to\n\"break\".\n");
/*  683: 955 */       } else if ((help) && (commandArgs[1].equals("capabilities"))) {
/*  684: 956 */         System.out.println("capabilities <classname> <args>\n\nLists the capabilities of the specified class.\nIf the class is a " + OptionHandler.class.getName() + " then\n" + "trailing options after the classname will be\n" + "set as well.\n");
/*  685: 962 */       } else if ((help) && (commandArgs[1].equals("cls"))) {
/*  686: 963 */         System.out.println("cls\n\nClears the output area.\n");
/*  687: 964 */       } else if ((help) && (commandArgs[1].equals("history"))) {
/*  688: 965 */         System.out.println("history\n\nPrints all issued commands.\n");
/*  689: 966 */       } else if ((help) && (commandArgs[1].equals("exit"))) {
/*  690: 967 */         System.out.println("exit\n\nExits the SimpleCLI program.\n");
/*  691:     */       } else {
/*  692: 970 */         System.out.println("Command must be one of:\n\tjava <classname> <args> [ > file]\n\tkill\n\tcapabilities <classname> <args>\n\tcls\n\thistory\n\texit\n\thelp <command>\n");
/*  693:     */       }
/*  694:     */     }
/*  695:     */   }
/*  696:     */   
/*  697:     */   public void doHistory(KeyEvent e)
/*  698:     */   {
/*  699: 987 */     if (e.getSource() == this.m_Input) {
/*  700: 988 */       switch (e.getKeyCode())
/*  701:     */       {
/*  702:     */       case 38: 
/*  703: 990 */         if (this.m_HistoryPos > 0)
/*  704:     */         {
/*  705: 991 */           this.m_HistoryPos -= 1;
/*  706: 992 */           String command = (String)this.m_CommandHistory.elementAt(this.m_HistoryPos);
/*  707: 993 */           this.m_Input.setText(command);
/*  708:     */         }
/*  709: 994 */         break;
/*  710:     */       case 40: 
/*  711: 997 */         if (this.m_HistoryPos < this.m_CommandHistory.size())
/*  712:     */         {
/*  713: 998 */           this.m_HistoryPos += 1;
/*  714: 999 */           String command = "";
/*  715:1000 */           if (this.m_HistoryPos < this.m_CommandHistory.size()) {
/*  716:1001 */             command = (String)this.m_CommandHistory.elementAt(this.m_HistoryPos);
/*  717:     */           }
/*  718:1003 */           this.m_Input.setText(command);
/*  719:     */         }
/*  720:1004 */         break;
/*  721:     */       }
/*  722:     */     }
/*  723:     */   }
/*  724:     */   
/*  725:     */   public void doCommandlineCompletion(KeyEvent e)
/*  726:     */   {
/*  727:1018 */     if (e.getSource() == this.m_Input) {
/*  728:1019 */       switch (e.getKeyCode())
/*  729:     */       {
/*  730:     */       case 9: 
/*  731:1022 */         if (e.getModifiers() == 0)
/*  732:     */         {
/*  733:1025 */           this.m_Input.setCursor(Cursor.getPredefinedCursor(3));
/*  734:1026 */           this.m_OutputArea.setCursor(Cursor.getPredefinedCursor(3));
/*  735:     */           try
/*  736:     */           {
/*  737:1030 */             String txt = this.m_Input.getText();
/*  738:1033 */             if (txt.trim().startsWith("java "))
/*  739:     */             {
/*  740:1034 */               int pos = this.m_Input.getCaretPosition();
/*  741:1035 */               int nonNameCharPos = -1;
/*  742:1039 */               for (int i = pos - 1; i >= 0; i--) {
/*  743:1040 */                 if ((txt.charAt(i) == '"') || (txt.charAt(i) == ' '))
/*  744:     */                 {
/*  745:1041 */                   nonNameCharPos = i;
/*  746:1042 */                   break;
/*  747:     */                 }
/*  748:     */               }
/*  749:1046 */               if (nonNameCharPos > -1)
/*  750:     */               {
/*  751:1047 */                 String search = txt.substring(nonNameCharPos + 1, pos);
/*  752:     */                 
/*  753:     */ 
/*  754:1050 */                 Vector<String> list = this.m_Completion.getMatches(search);
/*  755:1051 */                 String common = this.m_Completion.getCommonPrefix(list);
/*  756:1054 */                 if ((search.toLowerCase() + File.separator).equals(common.toLowerCase())) {
/*  757:1056 */                   common = search;
/*  758:     */                 }
/*  759:1060 */                 if (common.length() > search.length())
/*  760:     */                 {
/*  761:     */                   try
/*  762:     */                   {
/*  763:1062 */                     this.m_Input.getDocument().remove(nonNameCharPos + 1, search.length());
/*  764:     */                     
/*  765:1064 */                     this.m_Input.getDocument().insertString(nonNameCharPos + 1, common, null);
/*  766:     */                   }
/*  767:     */                   catch (Exception ex)
/*  768:     */                   {
/*  769:1067 */                     ex.printStackTrace();
/*  770:     */                   }
/*  771:     */                 }
/*  772:1071 */                 else if (list.size() > 1)
/*  773:     */                 {
/*  774:1072 */                   System.out.println("\nPossible matches:");
/*  775:1073 */                   for (int i = 0; i < list.size(); i++) {
/*  776:1074 */                     System.out.println("  " + (String)list.get(i));
/*  777:     */                   }
/*  778:     */                 }
/*  779:     */               }
/*  780:     */             }
/*  781:     */           }
/*  782:     */           finally
/*  783:     */           {
/*  784:1083 */             this.m_Input.setCursor(null);
/*  785:1084 */             this.m_OutputArea.setCursor(null);
/*  786:     */           }
/*  787:     */         }
/*  788:1084 */         break;
/*  789:     */       case 8: 
/*  790:1091 */         if (e.getModifiers() == 8)
/*  791:     */         {
/*  792:1092 */           String txt = this.m_Input.getText();
/*  793:1093 */           int pos = this.m_Input.getCaretPosition();
/*  794:     */           
/*  795:     */ 
/*  796:1096 */           int start = pos;
/*  797:1097 */           start--;
/*  798:1098 */           while ((start >= 0) && (
/*  799:1099 */             (txt.charAt(start) == '.') || (txt.charAt(start) == ' ') || (txt.charAt(start) == '\\') || (txt.charAt(start) == '/'))) {
/*  800:1101 */             start--;
/*  801:     */           }
/*  802:1108 */           int newPos = -1;
/*  803:1109 */           for (int i = start; i >= 0; i--) {
/*  804:1110 */             if ((txt.charAt(i) == '.') || (txt.charAt(i) == ' ') || (txt.charAt(i) == '\\') || (txt.charAt(i) == '/'))
/*  805:     */             {
/*  806:1112 */               newPos = i;
/*  807:1113 */               break;
/*  808:     */             }
/*  809:     */           }
/*  810:     */           try
/*  811:     */           {
/*  812:1119 */             this.m_Input.getDocument().remove(newPos + 1, pos - newPos - 1);
/*  813:     */           }
/*  814:     */           catch (Exception ex)
/*  815:     */           {
/*  816:1121 */             ex.printStackTrace();
/*  817:     */           }
/*  818:     */         }
/*  819:     */         break;
/*  820:     */       }
/*  821:     */     }
/*  822:     */   }
/*  823:     */   
/*  824:     */   public void actionPerformed(ActionEvent e)
/*  825:     */   {
/*  826:     */     try
/*  827:     */     {
/*  828:1139 */       if (e.getSource() == this.m_Input)
/*  829:     */       {
/*  830:1140 */         String command = this.m_Input.getText();
/*  831:1141 */         int last = this.m_CommandHistory.size() - 1;
/*  832:1142 */         if ((last < 0) || (!command.equals(this.m_CommandHistory.elementAt(last))))
/*  833:     */         {
/*  834:1143 */           this.m_CommandHistory.addElement(command);
/*  835:1144 */           saveHistory();
/*  836:     */         }
/*  837:1146 */         this.m_HistoryPos = this.m_CommandHistory.size();
/*  838:1147 */         runCommand(command);
/*  839:     */         
/*  840:1149 */         this.m_Input.setText("");
/*  841:     */       }
/*  842:     */     }
/*  843:     */     catch (Exception ex)
/*  844:     */     {
/*  845:1152 */       System.err.println(ex.getMessage());
/*  846:     */     }
/*  847:     */   }
/*  848:     */   
/*  849:     */   protected void loadHistory()
/*  850:     */   {
/*  851:1164 */     int size = Integer.parseInt(PROPERTIES.getProperty("HistorySize", "50"));
/*  852:     */     
/*  853:1166 */     this.m_CommandHistory.clear();
/*  854:1167 */     for (int i = 0; i < size; i++)
/*  855:     */     {
/*  856:1168 */       String cmd = PROPERTIES.getProperty("Command" + i, "");
/*  857:1169 */       if (cmd.length() == 0) {
/*  858:     */         break;
/*  859:     */       }
/*  860:1170 */       this.m_CommandHistory.add(cmd);
/*  861:     */     }
/*  862:1176 */     this.m_HistoryPos = this.m_CommandHistory.size();
/*  863:     */   }
/*  864:     */   
/*  865:     */   protected void saveHistory()
/*  866:     */   {
/*  867:1189 */     int size = Integer.parseInt(PROPERTIES.getProperty("HistorySize", "50"));
/*  868:     */     
/*  869:     */ 
/*  870:1192 */     int from = this.m_CommandHistory.size() - size;
/*  871:1193 */     if (from < 0) {
/*  872:1194 */       from = 0;
/*  873:     */     }
/*  874:1198 */     PROPERTIES.setProperty("HistorySize", "" + size);
/*  875:1199 */     for (int i = from; i < this.m_CommandHistory.size(); i++) {
/*  876:1200 */       PROPERTIES.setProperty("Command" + (i - from), (String)this.m_CommandHistory.get(i));
/*  877:     */     }
/*  878:     */     try
/*  879:     */     {
/*  880:1204 */       String filename = System.getProperties().getProperty("user.home") + File.separatorChar + FILENAME;
/*  881:     */       
/*  882:     */ 
/*  883:1207 */       BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filename));
/*  884:1208 */       PROPERTIES.store(stream, "SimpleCLI");
/*  885:1209 */       stream.close();
/*  886:     */     }
/*  887:     */     catch (Exception e)
/*  888:     */     {
/*  889:1211 */       e.printStackTrace();
/*  890:     */     }
/*  891:     */   }
/*  892:     */   
/*  893:     */   public static void main(String[] args)
/*  894:     */   {
/*  895:1221 */     showPanel(new SimpleCLIPanel(), args);
/*  896:     */   }
/*  897:     */   
/*  898:     */   public void instantiationComplete() {}
/*  899:     */   
/*  900:     */   public void setActive(boolean active) {}
/*  901:     */   
/*  902:     */   public void setLoaded(boolean loaded) {}
/*  903:     */   
/*  904:     */   public void settingsChanged() {}
/*  905:     */   
/*  906:     */   public void setInstances(Instances instances) {}
/*  907:     */   
/*  908:     */   public void setLog(Logger log) {}
/*  909:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SimpleCLIPanel
 * JD-Core Version:    0.7.0.1
 */