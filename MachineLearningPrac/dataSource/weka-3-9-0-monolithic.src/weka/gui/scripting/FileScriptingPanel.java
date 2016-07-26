/*    1:     */ package weka.gui.scripting;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.Dialog;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.Frame;
/*    9:     */ import java.awt.event.ActionEvent;
/*   10:     */ import java.awt.event.WindowEvent;
/*   11:     */ import java.awt.event.WindowListener;
/*   12:     */ import java.io.File;
/*   13:     */ import java.io.PrintStream;
/*   14:     */ import java.util.HashMap;
/*   15:     */ import javax.swing.AbstractAction;
/*   16:     */ import javax.swing.Action;
/*   17:     */ import javax.swing.BorderFactory;
/*   18:     */ import javax.swing.JDialog;
/*   19:     */ import javax.swing.JFileChooser;
/*   20:     */ import javax.swing.JFrame;
/*   21:     */ import javax.swing.JInternalFrame;
/*   22:     */ import javax.swing.JLabel;
/*   23:     */ import javax.swing.JMenu;
/*   24:     */ import javax.swing.JMenuBar;
/*   25:     */ import javax.swing.JMenuItem;
/*   26:     */ import javax.swing.JOptionPane;
/*   27:     */ import javax.swing.JPanel;
/*   28:     */ import javax.swing.JScrollPane;
/*   29:     */ import javax.swing.JTextArea;
/*   30:     */ import javax.swing.JTextPane;
/*   31:     */ import javax.swing.KeyStroke;
/*   32:     */ import javax.swing.event.DocumentEvent;
/*   33:     */ import javax.swing.event.DocumentListener;
/*   34:     */ import javax.swing.event.UndoableEditEvent;
/*   35:     */ import javax.swing.event.UndoableEditListener;
/*   36:     */ import javax.swing.text.Document;
/*   37:     */ import javax.swing.text.JTextComponent;
/*   38:     */ import javax.swing.undo.CannotRedoException;
/*   39:     */ import javax.swing.undo.CannotUndoException;
/*   40:     */ import javax.swing.undo.UndoManager;
/*   41:     */ import weka.core.Utils;
/*   42:     */ import weka.gui.ComponentHelper;
/*   43:     */ import weka.gui.DocumentPrinting;
/*   44:     */ import weka.gui.ExtensionFileFilter;
/*   45:     */ import weka.gui.PropertyDialog;
/*   46:     */ import weka.gui.scripting.event.ScriptExecutionEvent;
/*   47:     */ import weka.gui.scripting.event.ScriptExecutionEvent.Type;
/*   48:     */ import weka.gui.scripting.event.ScriptExecutionListener;
/*   49:     */ import weka.gui.scripting.event.TitleUpdatedEvent;
/*   50:     */ 
/*   51:     */ public abstract class FileScriptingPanel
/*   52:     */   extends ScriptingPanel
/*   53:     */   implements ScriptExecutionListener
/*   54:     */ {
/*   55:     */   private static final long serialVersionUID = 1583670545010241816L;
/*   56:     */   public static final String IMAGES_DIR = "weka/gui/scripting/images";
/*   57:     */   protected JFileChooser m_FileChooser;
/*   58:     */   protected Script m_Script;
/*   59:     */   protected JTextArea m_ScriptArea;
/*   60:     */   protected JTextArea m_OutputArea;
/*   61:     */   protected JLabel m_LabelInfo;
/*   62:     */   protected HashMap<Object, Action> m_Actions;
/*   63:     */   protected NewAction m_NewAction;
/*   64:     */   protected OpenAction m_OpenAction;
/*   65:     */   protected SaveAction m_SaveAction;
/*   66:     */   protected SaveAction m_SaveAsAction;
/*   67:     */   protected PrintAction m_PrintAction;
/*   68:     */   protected ClearOutputAction m_ClearOutputAction;
/*   69:     */   protected ExitAction m_ExitAction;
/*   70:     */   protected UndoAction m_UndoAction;
/*   71:     */   protected RedoAction m_RedoAction;
/*   72:     */   protected Action m_CutAction;
/*   73:     */   protected Action m_CopyAction;
/*   74:     */   protected Action m_PasteAction;
/*   75:     */   protected StartAction m_StartAction;
/*   76:     */   protected StopAction m_StopAction;
/*   77:     */   protected CommandlineArgsAction m_ArgsAction;
/*   78:     */   protected AboutAction m_AboutAction;
/*   79:     */   protected UndoManager m_Undo;
/*   80:     */   protected JTextPane m_TextCode;
/*   81:     */   protected JTextPane m_TextOutput;
/*   82:     */   protected String[] m_Args;
/*   83:     */   
/*   84:     */   public abstract class BasicAction
/*   85:     */     extends AbstractAction
/*   86:     */   {
/*   87:     */     private static final long serialVersionUID = 2821117985661550385L;
/*   88:     */     
/*   89:     */     public BasicAction(String name, String icon, String accel, Character mnemonic)
/*   90:     */     {
/*   91: 108 */       super();
/*   92: 110 */       if ((icon != null) && (icon.length() > 0)) {
/*   93: 111 */         putValue("SmallIcon", ComponentHelper.getImageIcon(icon));
/*   94:     */       }
/*   95: 112 */       if ((accel != null) && (accel.length() > 0)) {
/*   96: 113 */         putValue("AcceleratorKey", KeyStroke.getKeyStroke(accel));
/*   97:     */       }
/*   98: 114 */       if (mnemonic != null) {
/*   99: 115 */         putValue("MnemonicKey", new Integer(mnemonic.charValue()));
/*  100:     */       }
/*  101:     */     }
/*  102:     */   }
/*  103:     */   
/*  104:     */   protected class NewAction
/*  105:     */     extends FileScriptingPanel.BasicAction
/*  106:     */   {
/*  107:     */     private static final long serialVersionUID = -8665722554539726090L;
/*  108:     */     
/*  109:     */     public NewAction()
/*  110:     */     {
/*  111: 135 */       super("New", "new.gif", "ctrl N", Character.valueOf('N'));
/*  112: 136 */       setEnabled(true);
/*  113:     */     }
/*  114:     */     
/*  115:     */     public void actionPerformed(ActionEvent e)
/*  116:     */     {
/*  117: 145 */       FileScriptingPanel.this.m_Script.empty();
/*  118: 146 */       FileScriptingPanel.this.notifyTitleUpdatedListeners(new TitleUpdatedEvent(FileScriptingPanel.this));
/*  119:     */     }
/*  120:     */   }
/*  121:     */   
/*  122:     */   protected class OpenAction
/*  123:     */     extends FileScriptingPanel.BasicAction
/*  124:     */   {
/*  125:     */     private static final long serialVersionUID = -4496148485267789162L;
/*  126:     */     
/*  127:     */     public OpenAction()
/*  128:     */     {
/*  129: 166 */       super("Open...", "open.gif", "ctrl O", Character.valueOf('O'));
/*  130: 167 */       setEnabled(true);
/*  131:     */     }
/*  132:     */     
/*  133:     */     public void actionPerformed(ActionEvent e)
/*  134:     */     {
/*  135: 179 */       if (!FileScriptingPanel.this.checkModified()) {
/*  136: 180 */         return;
/*  137:     */       }
/*  138: 182 */       int retVal = FileScriptingPanel.this.m_FileChooser.showOpenDialog(FileScriptingPanel.this);
/*  139: 183 */       if (retVal != 0) {
/*  140: 184 */         return;
/*  141:     */       }
/*  142: 186 */       boolean ok = FileScriptingPanel.this.m_Script.open(FileScriptingPanel.this.m_FileChooser.getSelectedFile());
/*  143: 187 */       FileScriptingPanel.this.m_TextCode.setCaretPosition(0);
/*  144: 188 */       if (!ok) {
/*  145: 189 */         JOptionPane.showMessageDialog(FileScriptingPanel.this, "Couldn't open file '" + FileScriptingPanel.this.m_FileChooser.getSelectedFile() + "'!");
/*  146:     */       }
/*  147: 193 */       FileScriptingPanel.this.notifyTitleUpdatedListeners(new TitleUpdatedEvent(FileScriptingPanel.this));
/*  148:     */     }
/*  149:     */   }
/*  150:     */   
/*  151:     */   protected class SaveAction
/*  152:     */     extends FileScriptingPanel.BasicAction
/*  153:     */   {
/*  154:     */     private static final long serialVersionUID = -74651145892063975L;
/*  155:     */     protected boolean m_ShowDialog;
/*  156:     */     
/*  157:     */     public SaveAction(String name, boolean showDialog)
/*  158:     */     {
/*  159: 219 */       super(name, showDialog ? "" : "save.gif", showDialog ? "ctrl shift S" : "ctrl S", Character.valueOf(showDialog ? 'a' : 'S'));
/*  160: 220 */       this.m_ShowDialog = showDialog;
/*  161: 221 */       setEnabled(true);
/*  162:     */     }
/*  163:     */     
/*  164:     */     public void actionPerformed(ActionEvent e)
/*  165:     */     {
/*  166:     */       boolean ok;
/*  167:     */       boolean ok;
/*  168: 233 */       if ((this.m_ShowDialog) || (FileScriptingPanel.this.m_Script.getFilename() == null))
/*  169:     */       {
/*  170: 234 */         int retVal = FileScriptingPanel.this.m_FileChooser.showSaveDialog(FileScriptingPanel.this);
/*  171: 235 */         if (retVal != 0) {
/*  172: 236 */           return;
/*  173:     */         }
/*  174: 237 */         ok = FileScriptingPanel.this.m_Script.saveAs(FileScriptingPanel.this.m_FileChooser.getSelectedFile());
/*  175:     */       }
/*  176:     */       else
/*  177:     */       {
/*  178: 240 */         ok = FileScriptingPanel.this.m_Script.save();
/*  179:     */       }
/*  180: 243 */       if (!ok)
/*  181:     */       {
/*  182: 244 */         if (FileScriptingPanel.this.m_Script.getFilename() != null) {
/*  183: 245 */           JOptionPane.showMessageDialog(FileScriptingPanel.this, "Failed to save file '" + FileScriptingPanel.this.m_FileChooser.getSelectedFile() + "'!");
/*  184:     */         } else {
/*  185: 249 */           JOptionPane.showMessageDialog(FileScriptingPanel.this, "Failed to save file!");
/*  186:     */         }
/*  187:     */       }
/*  188:     */       else {
/*  189: 254 */         FileScriptingPanel.this.m_SaveAction.setEnabled(false);
/*  190:     */       }
/*  191: 257 */       FileScriptingPanel.this.notifyTitleUpdatedListeners(new TitleUpdatedEvent(FileScriptingPanel.this));
/*  192:     */     }
/*  193:     */   }
/*  194:     */   
/*  195:     */   protected class PrintAction
/*  196:     */     extends FileScriptingPanel.BasicAction
/*  197:     */   {
/*  198:     */     private static final long serialVersionUID = -6246539539545724632L;
/*  199:     */     
/*  200:     */     public PrintAction()
/*  201:     */     {
/*  202: 277 */       super("Print...", "print.gif", "ctrl P", Character.valueOf('P'));
/*  203: 278 */       setEnabled(true);
/*  204:     */     }
/*  205:     */     
/*  206:     */     public void actionPerformed(ActionEvent e)
/*  207:     */     {
/*  208: 290 */       JTextPane pane = FileScriptingPanel.this.newCodePane();
/*  209: 291 */       pane.setText(FileScriptingPanel.this.m_TextCode.getText());
/*  210: 292 */       DocumentPrinting doc = new DocumentPrinting();
/*  211: 293 */       doc.print(pane);
/*  212:     */     }
/*  213:     */   }
/*  214:     */   
/*  215:     */   protected class ClearOutputAction
/*  216:     */     extends FileScriptingPanel.BasicAction
/*  217:     */   {
/*  218:     */     private static final long serialVersionUID = 47986890456997211L;
/*  219:     */     
/*  220:     */     public ClearOutputAction()
/*  221:     */     {
/*  222: 313 */       super("Clear output", "", "F2", Character.valueOf('C'));
/*  223: 314 */       setEnabled(true);
/*  224:     */     }
/*  225:     */     
/*  226:     */     public void actionPerformed(ActionEvent e)
/*  227:     */     {
/*  228: 323 */       FileScriptingPanel.this.m_TextOutput.setText("");
/*  229:     */     }
/*  230:     */   }
/*  231:     */   
/*  232:     */   protected class ExitAction
/*  233:     */     extends FileScriptingPanel.BasicAction
/*  234:     */   {
/*  235:     */     private static final long serialVersionUID = -5884709836238884180L;
/*  236:     */     
/*  237:     */     public ExitAction()
/*  238:     */     {
/*  239: 344 */       super("Exit", "", "", Character.valueOf('x'));
/*  240: 345 */       setEnabled(true);
/*  241:     */     }
/*  242:     */     
/*  243:     */     public void actionPerformed(ActionEvent e)
/*  244:     */     {
/*  245: 362 */       if (!FileScriptingPanel.this.checkModified()) {
/*  246: 363 */         return;
/*  247:     */       }
/*  248: 365 */       if (PropertyDialog.getParentDialog(FileScriptingPanel.this) != null)
/*  249:     */       {
/*  250: 366 */         Dialog dialog = PropertyDialog.getParentDialog(FileScriptingPanel.this);
/*  251: 367 */         dialog.setVisible(false);
/*  252:     */       }
/*  253: 369 */       else if (PropertyDialog.getParentFrame(FileScriptingPanel.this) != null)
/*  254:     */       {
/*  255: 370 */         JInternalFrame jintframe = PropertyDialog.getParentInternalFrame(FileScriptingPanel.this);
/*  256: 371 */         if (jintframe != null)
/*  257:     */         {
/*  258: 372 */           jintframe.doDefaultCloseAction();
/*  259:     */         }
/*  260:     */         else
/*  261:     */         {
/*  262: 375 */           Frame frame = PropertyDialog.getParentFrame(FileScriptingPanel.this);
/*  263: 376 */           if ((frame instanceof JFrame))
/*  264:     */           {
/*  265: 377 */             JFrame jframe = (JFrame)frame;
/*  266: 378 */             if (jframe.getDefaultCloseOperation() == 1) {
/*  267: 379 */               jframe.setVisible(false);
/*  268: 380 */             } else if (jframe.getDefaultCloseOperation() == 2) {
/*  269: 381 */               jframe.dispose();
/*  270: 382 */             } else if (jframe.getDefaultCloseOperation() == 3) {
/*  271: 383 */               System.exit(0);
/*  272:     */             }
/*  273: 386 */             WindowListener[] listeners = jframe.getWindowListeners();
/*  274: 387 */             WindowEvent event = new WindowEvent(jframe, 202);
/*  275: 388 */             for (int i = 0; i < listeners.length; i++) {
/*  276: 389 */               listeners[i].windowClosed(event);
/*  277:     */             }
/*  278:     */           }
/*  279: 392 */           frame.dispose();
/*  280:     */         }
/*  281:     */       }
/*  282:     */     }
/*  283:     */   }
/*  284:     */   
/*  285:     */   protected class UndoAction
/*  286:     */     extends FileScriptingPanel.BasicAction
/*  287:     */   {
/*  288:     */     private static final long serialVersionUID = 4298096648424808522L;
/*  289:     */     
/*  290:     */     public UndoAction()
/*  291:     */     {
/*  292: 415 */       super("Undo", "undo.gif", "ctrl Z", Character.valueOf('U'));
/*  293: 416 */       setEnabled(false);
/*  294:     */     }
/*  295:     */     
/*  296:     */     public void actionPerformed(ActionEvent e)
/*  297:     */     {
/*  298:     */       try
/*  299:     */       {
/*  300: 426 */         FileScriptingPanel.this.m_Undo.undo();
/*  301:     */       }
/*  302:     */       catch (CannotUndoException ex)
/*  303:     */       {
/*  304: 429 */         System.out.println("Unable to undo: " + ex);
/*  305: 430 */         ex.printStackTrace();
/*  306:     */       }
/*  307: 432 */       updateUndoState();
/*  308: 433 */       FileScriptingPanel.this.m_RedoAction.updateRedoState();
/*  309:     */     }
/*  310:     */     
/*  311:     */     protected void updateUndoState()
/*  312:     */     {
/*  313: 440 */       if (FileScriptingPanel.this.m_Undo.canUndo())
/*  314:     */       {
/*  315: 441 */         setEnabled(true);
/*  316: 442 */         putValue("Name", FileScriptingPanel.this.m_Undo.getUndoPresentationName());
/*  317:     */       }
/*  318:     */       else
/*  319:     */       {
/*  320: 445 */         setEnabled(false);
/*  321: 446 */         putValue("Name", "Undo");
/*  322:     */       }
/*  323:     */     }
/*  324:     */   }
/*  325:     */   
/*  326:     */   protected class RedoAction
/*  327:     */     extends FileScriptingPanel.BasicAction
/*  328:     */   {
/*  329:     */     private static final long serialVersionUID = 4533966901523279350L;
/*  330:     */     
/*  331:     */     public RedoAction()
/*  332:     */     {
/*  333: 467 */       super("Redo", "redo.gif", "ctrl Y", Character.valueOf('R'));
/*  334: 468 */       setEnabled(false);
/*  335:     */     }
/*  336:     */     
/*  337:     */     public void actionPerformed(ActionEvent e)
/*  338:     */     {
/*  339:     */       try
/*  340:     */       {
/*  341: 478 */         FileScriptingPanel.this.m_Undo.redo();
/*  342:     */       }
/*  343:     */       catch (CannotRedoException ex)
/*  344:     */       {
/*  345: 481 */         System.out.println("Unable to redo: " + ex);
/*  346: 482 */         ex.printStackTrace();
/*  347:     */       }
/*  348: 484 */       updateRedoState();
/*  349: 485 */       FileScriptingPanel.this.m_UndoAction.updateUndoState();
/*  350:     */     }
/*  351:     */     
/*  352:     */     protected void updateRedoState()
/*  353:     */     {
/*  354: 492 */       if (FileScriptingPanel.this.m_Undo.canRedo())
/*  355:     */       {
/*  356: 493 */         setEnabled(true);
/*  357: 494 */         putValue("Name", FileScriptingPanel.this.m_Undo.getRedoPresentationName());
/*  358:     */       }
/*  359:     */       else
/*  360:     */       {
/*  361: 497 */         setEnabled(false);
/*  362: 498 */         putValue("Name", "Redo");
/*  363:     */       }
/*  364:     */     }
/*  365:     */   }
/*  366:     */   
/*  367:     */   protected class CommandlineArgsAction
/*  368:     */     extends FileScriptingPanel.BasicAction
/*  369:     */   {
/*  370:     */     private static final long serialVersionUID = -3183470039010826204L;
/*  371:     */     
/*  372:     */     public CommandlineArgsAction()
/*  373:     */     {
/*  374: 519 */       super("Arguments...", "properties.gif", "", Character.valueOf('g'));
/*  375: 520 */       setEnabled(true);
/*  376:     */     }
/*  377:     */     
/*  378:     */     public void actionPerformed(ActionEvent e)
/*  379:     */     {
/*  380: 531 */       String retVal = JOptionPane.showInputDialog(FileScriptingPanel.this, "Please enter the command-line arguments", Utils.joinOptions(FileScriptingPanel.this.m_Args));
/*  381: 535 */       if (retVal == null) {
/*  382: 536 */         return;
/*  383:     */       }
/*  384:     */       try
/*  385:     */       {
/*  386: 539 */         FileScriptingPanel.this.m_Args = Utils.splitOptions(retVal);
/*  387:     */       }
/*  388:     */       catch (Exception ex)
/*  389:     */       {
/*  390: 542 */         FileScriptingPanel.this.m_Args = new String[0];
/*  391: 543 */         ex.printStackTrace();
/*  392: 544 */         JOptionPane.showMessageDialog(FileScriptingPanel.this, "Error setting command-line arguments:\n" + ex, "Error", 0);
/*  393:     */       }
/*  394:     */     }
/*  395:     */   }
/*  396:     */   
/*  397:     */   protected class StartAction
/*  398:     */     extends FileScriptingPanel.BasicAction
/*  399:     */   {
/*  400:     */     private static final long serialVersionUID = -7936456072955996220L;
/*  401:     */     
/*  402:     */     public StartAction()
/*  403:     */     {
/*  404: 569 */       super(FileScriptingPanel.this.m_Script.canExecuteScripts() ? "Start" : "Start (missing classes?)", "run.gif", "ctrl R", Character.valueOf('S'));
/*  405: 570 */       setEnabled(false);
/*  406:     */     }
/*  407:     */     
/*  408:     */     public void actionPerformed(ActionEvent e)
/*  409:     */     {
/*  410: 579 */       if (!FileScriptingPanel.this.checkModified()) {
/*  411: 580 */         return;
/*  412:     */       }
/*  413: 582 */       if (FileScriptingPanel.this.m_Script.getFilename() == null) {
/*  414: 583 */         return;
/*  415:     */       }
/*  416:     */       try
/*  417:     */       {
/*  418: 586 */         FileScriptingPanel.this.m_Script.start(FileScriptingPanel.this.m_Args);
/*  419:     */       }
/*  420:     */       catch (Exception ex)
/*  421:     */       {
/*  422: 589 */         ex.printStackTrace();
/*  423: 590 */         JOptionPane.showMessageDialog(FileScriptingPanel.this, "Error running script:\n" + ex, "Error", 0);
/*  424:     */       }
/*  425:     */     }
/*  426:     */   }
/*  427:     */   
/*  428:     */   protected class StopAction
/*  429:     */     extends FileScriptingPanel.BasicAction
/*  430:     */   {
/*  431:     */     private static final long serialVersionUID = 8764023289575718872L;
/*  432:     */     
/*  433:     */     public StopAction()
/*  434:     */     {
/*  435: 615 */       super("Stop", "stop.gif", "ctrl shift R", Character.valueOf('o'));
/*  436: 616 */       setEnabled(false);
/*  437:     */     }
/*  438:     */     
/*  439:     */     public void actionPerformed(ActionEvent e)
/*  440:     */     {
/*  441:     */       try
/*  442:     */       {
/*  443: 626 */         FileScriptingPanel.this.m_Script.stop();
/*  444:     */       }
/*  445:     */       catch (Exception ex) {}
/*  446:     */     }
/*  447:     */   }
/*  448:     */   
/*  449:     */   protected class AboutAction
/*  450:     */     extends FileScriptingPanel.BasicAction
/*  451:     */   {
/*  452:     */     private static final long serialVersionUID = -6420463480569171227L;
/*  453:     */     
/*  454:     */     public AboutAction()
/*  455:     */     {
/*  456: 650 */       super("About...", "", "F1", Character.valueOf('A'));
/*  457: 651 */       setEnabled(true);
/*  458:     */     }
/*  459:     */     
/*  460:     */     public void actionPerformed(ActionEvent e)
/*  461:     */     {
/*  462:     */       JDialog dialog;
/*  463:     */       JDialog dialog;
/*  464: 662 */       if (PropertyDialog.getParentDialog(FileScriptingPanel.this) != null) {
/*  465: 663 */         dialog = new JDialog(PropertyDialog.getParentDialog(FileScriptingPanel.this), FileScriptingPanel.this.getName());
/*  466:     */       } else {
/*  467: 665 */         dialog = new JDialog(PropertyDialog.getParentFrame(FileScriptingPanel.this), FileScriptingPanel.this.getName());
/*  468:     */       }
/*  469: 666 */       dialog.setTitle((String)getValue("Name"));
/*  470: 667 */       dialog.getContentPane().setLayout(new BorderLayout());
/*  471: 668 */       dialog.getContentPane().add(FileScriptingPanel.this.getAboutPanel());
/*  472: 669 */       dialog.pack();
/*  473: 670 */       dialog.setLocationRelativeTo(FileScriptingPanel.this);
/*  474: 671 */       dialog.setVisible(true);
/*  475:     */     }
/*  476:     */   }
/*  477:     */   
/*  478:     */   protected class ScriptUndoableEditListener
/*  479:     */     implements UndoableEditListener
/*  480:     */   {
/*  481:     */     protected ScriptUndoableEditListener() {}
/*  482:     */     
/*  483:     */     public void undoableEditHappened(UndoableEditEvent e)
/*  484:     */     {
/*  485: 691 */       FileScriptingPanel.this.m_Undo.addEdit(e.getEdit());
/*  486: 692 */       FileScriptingPanel.this.m_UndoAction.updateUndoState();
/*  487: 693 */       FileScriptingPanel.this.m_RedoAction.updateRedoState();
/*  488:     */     }
/*  489:     */   }
/*  490:     */   
/*  491:     */   protected void initialize()
/*  492:     */   {
/*  493: 767 */     super.initialize();
/*  494:     */     
/*  495: 769 */     this.m_FileChooser = new JFileChooser();
/*  496: 770 */     this.m_FileChooser.setAcceptAllFileFilterUsed(true);
/*  497: 771 */     this.m_FileChooser.setMultiSelectionEnabled(false);
/*  498:     */     
/*  499: 773 */     this.m_Undo = new UndoManager();
/*  500: 774 */     this.m_Args = new String[0];
/*  501:     */   }
/*  502:     */   
/*  503:     */   protected void initGUI()
/*  504:     */   {
/*  505: 783 */     super.initGUI();
/*  506:     */     
/*  507: 785 */     setLayout(new BorderLayout(0, 5));
/*  508:     */     
/*  509: 787 */     this.m_TextCode = newCodePane();
/*  510: 788 */     this.m_TextCode.setFont(new Font("monospaced", 0, 12));
/*  511: 789 */     this.m_TextCode.getDocument().addUndoableEditListener(new ScriptUndoableEditListener());
/*  512: 790 */     this.m_TextCode.getDocument().addDocumentListener(new DocumentListener()
/*  513:     */     {
/*  514:     */       public void changedUpdate(DocumentEvent e)
/*  515:     */       {
/*  516: 792 */         update();
/*  517:     */       }
/*  518:     */       
/*  519:     */       public void insertUpdate(DocumentEvent e)
/*  520:     */       {
/*  521: 795 */         update();
/*  522:     */       }
/*  523:     */       
/*  524:     */       public void removeUpdate(DocumentEvent e)
/*  525:     */       {
/*  526: 798 */         update();
/*  527:     */       }
/*  528:     */       
/*  529:     */       protected void update()
/*  530:     */       {
/*  531: 801 */         Document doc = FileScriptingPanel.this.m_TextCode.getDocument();
/*  532: 802 */         FileScriptingPanel.this.m_StartAction.setEnabled((doc.getLength() > 0) && (FileScriptingPanel.this.m_Script.canExecuteScripts()));
/*  533: 803 */         FileScriptingPanel.this.m_SaveAction.setEnabled(true);
/*  534: 804 */         FileScriptingPanel.this.notifyTitleUpdatedListeners(new TitleUpdatedEvent(FileScriptingPanel.this));
/*  535:     */       }
/*  536: 806 */     });
/*  537: 807 */     add(new JScrollPane(this.m_TextCode), "Center");
/*  538:     */     
/*  539: 809 */     JPanel panel = new JPanel(new BorderLayout(0, 5));
/*  540: 810 */     panel.setPreferredSize(new Dimension(50, 200));
/*  541: 811 */     add(panel, "South");
/*  542:     */     
/*  543: 813 */     this.m_TextOutput = new JTextPane();
/*  544: 814 */     panel.add(new JScrollPane(this.m_TextOutput), "Center");
/*  545:     */     
/*  546: 816 */     this.m_LabelInfo = new JLabel(" ");
/*  547: 817 */     this.m_LabelInfo.setBorder(BorderFactory.createLoweredBevelBorder());
/*  548: 818 */     panel.add(this.m_LabelInfo, "South");
/*  549:     */   }
/*  550:     */   
/*  551:     */   protected void initFinish()
/*  552:     */   {
/*  553: 828 */     super.initFinish();
/*  554:     */     
/*  555: 830 */     this.m_Script = newScript(this.m_TextCode.getDocument());
/*  556: 831 */     this.m_Script.addScriptFinishedListener(this);
/*  557: 832 */     ExtensionFileFilter[] filters = this.m_Script.getFilters();
/*  558: 833 */     for (int i = filters.length - 1; i >= 0; i--) {
/*  559: 834 */       this.m_FileChooser.addChoosableFileFilter(filters[i]);
/*  560:     */     }
/*  561: 836 */     this.m_Actions = createActionTable(this.m_TextCode);
/*  562:     */     
/*  563:     */ 
/*  564: 839 */     this.m_NewAction = new NewAction();
/*  565: 840 */     this.m_OpenAction = new OpenAction();
/*  566: 841 */     this.m_SaveAction = new SaveAction("Save", false);
/*  567: 842 */     this.m_SaveAsAction = new SaveAction("Save As...", true);
/*  568: 843 */     this.m_PrintAction = new PrintAction();
/*  569: 844 */     this.m_ClearOutputAction = new ClearOutputAction();
/*  570: 845 */     this.m_ExitAction = new ExitAction();
/*  571:     */     
/*  572:     */ 
/*  573: 848 */     this.m_UndoAction = new UndoAction();
/*  574: 849 */     this.m_RedoAction = new RedoAction();
/*  575: 850 */     this.m_CutAction = updateAction((Action)this.m_Actions.get("cut-to-clipboard"), "Cut", "cut.gif", "ctrl X", Character.valueOf('C'));
/*  576: 851 */     this.m_CopyAction = updateAction((Action)this.m_Actions.get("copy-to-clipboard"), "Copy", "copy.gif", "ctrl C", Character.valueOf('o'));
/*  577: 852 */     this.m_PasteAction = updateAction((Action)this.m_Actions.get("paste-from-clipboard"), "Paste", "paste.gif", "ctrl V", Character.valueOf('P'));
/*  578:     */     
/*  579:     */ 
/*  580: 855 */     this.m_StartAction = new StartAction();
/*  581: 856 */     this.m_StopAction = new StopAction();
/*  582: 857 */     this.m_ArgsAction = new CommandlineArgsAction();
/*  583:     */     
/*  584:     */ 
/*  585: 860 */     this.m_AboutAction = new AboutAction();
/*  586:     */   }
/*  587:     */   
/*  588:     */   protected Action updateAction(Action action, String name, String icon, String accel, Character mnemonic)
/*  589:     */   {
/*  590: 877 */     if (action == null)
/*  591:     */     {
/*  592: 878 */       Action result = (Action)this.m_Actions.get(name);
/*  593: 879 */       return result;
/*  594:     */     }
/*  595: 882 */     Action result = action;
/*  596: 884 */     if ((name != null) && (name.length() > 0)) {
/*  597: 885 */       result.putValue("Name", name);
/*  598:     */     }
/*  599: 886 */     if ((icon != null) && (icon.length() > 0)) {
/*  600: 887 */       result.putValue("SmallIcon", ComponentHelper.getImageIcon(icon));
/*  601:     */     }
/*  602: 888 */     if ((accel != null) && (accel.length() > 0)) {
/*  603: 889 */       result.putValue("AcceleratorKey", KeyStroke.getKeyStroke(accel));
/*  604:     */     }
/*  605: 890 */     if (mnemonic != null) {
/*  606: 891 */       result.putValue("MnemonicKey", new Integer(mnemonic.charValue()));
/*  607:     */     }
/*  608: 893 */     return result;
/*  609:     */   }
/*  610:     */   
/*  611:     */   protected abstract JTextPane newCodePane();
/*  612:     */   
/*  613:     */   protected abstract Script newScript(Document paramDocument);
/*  614:     */   
/*  615:     */   public void scriptFinished(ScriptExecutionEvent e)
/*  616:     */   {
/*  617: 917 */     if (e.getType() == ScriptExecutionEvent.Type.FINISHED) {
/*  618: 918 */       showInfo("Script execution finished");
/*  619: 919 */     } else if (e.getType() == ScriptExecutionEvent.Type.STOPPED) {
/*  620: 920 */       showInfo("Script execution stopped by user");
/*  621: 921 */     } else if (e.getType() == ScriptExecutionEvent.Type.ERROR) {
/*  622: 922 */       showInfo("Script execution failed" + (e.hasAdditional() ? ": " + e.getAdditional() : ""));
/*  623:     */     }
/*  624: 924 */     if (e.getType() != ScriptExecutionEvent.Type.STARTED)
/*  625:     */     {
/*  626: 925 */       this.m_NewAction.setEnabled(true);
/*  627: 926 */       this.m_OpenAction.setEnabled(true);
/*  628: 927 */       this.m_SaveAction.setEnabled(true);
/*  629: 928 */       this.m_SaveAsAction.setEnabled(true);
/*  630: 929 */       this.m_CutAction.setEnabled(true);
/*  631: 930 */       this.m_CopyAction.setEnabled(true);
/*  632: 931 */       this.m_PasteAction.setEnabled(true);
/*  633: 932 */       this.m_StartAction.setEnabled(true);
/*  634: 933 */       this.m_StopAction.setEnabled(false);
/*  635:     */     }
/*  636:     */     else
/*  637:     */     {
/*  638: 936 */       this.m_NewAction.setEnabled(false);
/*  639: 937 */       this.m_OpenAction.setEnabled(false);
/*  640: 938 */       this.m_SaveAction.setEnabled(false);
/*  641: 939 */       this.m_SaveAsAction.setEnabled(false);
/*  642: 940 */       this.m_CutAction.setEnabled(false);
/*  643: 941 */       this.m_CopyAction.setEnabled(false);
/*  644: 942 */       this.m_PasteAction.setEnabled(false);
/*  645: 943 */       this.m_StartAction.setEnabled(false);
/*  646: 944 */       this.m_StopAction.setEnabled(true);
/*  647:     */     }
/*  648:     */   }
/*  649:     */   
/*  650:     */   protected HashMap<Object, Action> createActionTable(JTextComponent comp)
/*  651:     */   {
/*  652: 961 */     HashMap<Object, Action> result = new HashMap();
/*  653: 962 */     Action[] actions = comp.getActions();
/*  654: 963 */     for (int i = 0; i < actions.length; i++)
/*  655:     */     {
/*  656: 964 */       Action action = actions[i];
/*  657: 965 */       result.put(action.getValue("Name"), action);
/*  658:     */     }
/*  659: 968 */     return result;
/*  660:     */   }
/*  661:     */   
/*  662:     */   protected abstract JPanel getAboutPanel();
/*  663:     */   
/*  664:     */   public abstract String getPlainTitle();
/*  665:     */   
/*  666:     */   public String getTitle()
/*  667:     */   {
/*  668: 993 */     String result = getPlainTitle();
/*  669: 995 */     if (this.m_Script.isModified()) {
/*  670: 996 */       result = "*" + result;
/*  671:     */     }
/*  672: 998 */     if (this.m_Script.getFilename() != null) {
/*  673: 999 */       result = result + " [" + this.m_Script.getFilename() + "]";
/*  674:     */     }
/*  675:1001 */     return result;
/*  676:     */   }
/*  677:     */   
/*  678:     */   public JTextPane getOutput()
/*  679:     */   {
/*  680:1011 */     return this.m_TextOutput;
/*  681:     */   }
/*  682:     */   
/*  683:     */   public JMenuBar getMenuBar()
/*  684:     */   {
/*  685:1024 */     JMenuBar result = new JMenuBar();
/*  686:     */     
/*  687:     */ 
/*  688:1027 */     JMenu menu = new JMenu("File");
/*  689:1028 */     menu.setMnemonic('F');
/*  690:1029 */     result.add(menu);
/*  691:     */     
/*  692:     */ 
/*  693:1032 */     JMenuItem menuitem = new JMenuItem(this.m_NewAction);
/*  694:1033 */     menu.add(menuitem);
/*  695:     */     
/*  696:     */ 
/*  697:1036 */     menuitem = new JMenuItem(this.m_OpenAction);
/*  698:1037 */     menu.addSeparator();
/*  699:1038 */     menu.add(menuitem);
/*  700:     */     
/*  701:     */ 
/*  702:1041 */     menuitem = new JMenuItem(this.m_SaveAction);
/*  703:1042 */     menu.add(menuitem);
/*  704:     */     
/*  705:     */ 
/*  706:1045 */     menuitem = new JMenuItem(this.m_SaveAsAction);
/*  707:1046 */     menu.add(menuitem);
/*  708:     */     
/*  709:     */ 
/*  710:1049 */     menuitem = new JMenuItem(this.m_PrintAction);
/*  711:1050 */     menu.addSeparator();
/*  712:1051 */     menu.add(menuitem);
/*  713:     */     
/*  714:     */ 
/*  715:1054 */     menuitem = new JMenuItem(this.m_ClearOutputAction);
/*  716:1055 */     menu.addSeparator();
/*  717:1056 */     menu.add(menuitem);
/*  718:     */     
/*  719:     */ 
/*  720:1059 */     menuitem = new JMenuItem(this.m_ExitAction);
/*  721:1060 */     menu.addSeparator();
/*  722:1061 */     menu.add(menuitem);
/*  723:     */     
/*  724:     */ 
/*  725:1064 */     menu = new JMenu("Edit");
/*  726:1065 */     menu.setMnemonic('E');
/*  727:1066 */     result.add(menu);
/*  728:     */     
/*  729:     */ 
/*  730:1069 */     menuitem = new JMenuItem(this.m_UndoAction);
/*  731:1070 */     menu.add(menuitem);
/*  732:     */     
/*  733:     */ 
/*  734:1073 */     menuitem = new JMenuItem(this.m_RedoAction);
/*  735:1074 */     menu.add(menuitem);
/*  736:     */     
/*  737:     */ 
/*  738:1077 */     menuitem = new JMenuItem(this.m_CutAction);
/*  739:1078 */     menu.addSeparator();
/*  740:1079 */     menu.add(menuitem);
/*  741:     */     
/*  742:     */ 
/*  743:1082 */     menuitem = new JMenuItem(this.m_CopyAction);
/*  744:1083 */     menu.add(menuitem);
/*  745:     */     
/*  746:     */ 
/*  747:1086 */     menuitem = new JMenuItem(this.m_PasteAction);
/*  748:1087 */     menu.add(menuitem);
/*  749:     */     
/*  750:     */ 
/*  751:1090 */     menu = new JMenu("Script");
/*  752:1091 */     menu.setMnemonic('S');
/*  753:1092 */     result.add(menu);
/*  754:     */     
/*  755:     */ 
/*  756:1095 */     menuitem = new JMenuItem(this.m_StartAction);
/*  757:1096 */     menu.add(menuitem);
/*  758:     */     
/*  759:     */ 
/*  760:1099 */     menuitem = new JMenuItem(this.m_StopAction);
/*  761:1100 */     menu.add(menuitem);
/*  762:     */     
/*  763:     */ 
/*  764:1103 */     menuitem = new JMenuItem(this.m_ArgsAction);
/*  765:1104 */     menu.add(menuitem);
/*  766:     */     
/*  767:     */ 
/*  768:1107 */     menu = new JMenu("Help");
/*  769:1108 */     menu.setMnemonic('H');
/*  770:1109 */     result.add(menu);
/*  771:     */     
/*  772:     */ 
/*  773:1112 */     menuitem = new JMenuItem(this.m_AboutAction);
/*  774:1113 */     menu.add(menuitem);
/*  775:     */     
/*  776:1115 */     return result;
/*  777:     */   }
/*  778:     */   
/*  779:     */   protected void showInfo(String msg)
/*  780:     */   {
/*  781:1124 */     if (msg == null) {
/*  782:1125 */       msg = " ";
/*  783:     */     }
/*  784:1126 */     this.m_LabelInfo.setText(msg);
/*  785:     */   }
/*  786:     */   
/*  787:     */   public void open(File file)
/*  788:     */   {
/*  789:1135 */     this.m_Script.open(file);
/*  790:     */   }
/*  791:     */   
/*  792:     */   protected boolean checkModified()
/*  793:     */   {
/*  794:1149 */     boolean result = true;
/*  795:1151 */     if (this.m_Script.isModified())
/*  796:     */     {
/*  797:1152 */       int retVal = JOptionPane.showConfirmDialog(this, "Script not saved - save it now?", "Confirm", 1);
/*  798:1158 */       if (retVal == 0)
/*  799:     */       {
/*  800:1159 */         if (this.m_Script.getFilename() != null) {
/*  801:1160 */           this.m_Script.save();
/*  802:     */         } else {
/*  803:1162 */           this.m_SaveAsAction.actionPerformed(null);
/*  804:     */         }
/*  805:1163 */         result = !this.m_Script.isModified();
/*  806:     */       }
/*  807:1165 */       else if (retVal == 2)
/*  808:     */       {
/*  809:1166 */         result = false;
/*  810:     */       }
/*  811:     */     }
/*  812:1170 */     return result;
/*  813:     */   }
/*  814:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.FileScriptingPanel
 * JD-Core Version:    0.7.0.1
 */