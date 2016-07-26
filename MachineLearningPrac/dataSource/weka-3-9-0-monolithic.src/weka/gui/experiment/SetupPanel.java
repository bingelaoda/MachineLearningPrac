/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.GridLayout;
/*   8:    */ import java.awt.Insets;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.FocusAdapter;
/*  12:    */ import java.awt.event.FocusEvent;
/*  13:    */ import java.awt.event.KeyAdapter;
/*  14:    */ import java.awt.event.KeyEvent;
/*  15:    */ import java.awt.event.WindowAdapter;
/*  16:    */ import java.awt.event.WindowEvent;
/*  17:    */ import java.beans.PropertyChangeEvent;
/*  18:    */ import java.beans.PropertyChangeListener;
/*  19:    */ import java.beans.PropertyChangeSupport;
/*  20:    */ import java.io.BufferedInputStream;
/*  21:    */ import java.io.BufferedOutputStream;
/*  22:    */ import java.io.File;
/*  23:    */ import java.io.FileInputStream;
/*  24:    */ import java.io.FileOutputStream;
/*  25:    */ import java.io.ObjectInputStream;
/*  26:    */ import java.io.ObjectOutputStream;
/*  27:    */ import java.io.PrintStream;
/*  28:    */ import javax.swing.BorderFactory;
/*  29:    */ import javax.swing.ButtonGroup;
/*  30:    */ import javax.swing.JButton;
/*  31:    */ import javax.swing.JFileChooser;
/*  32:    */ import javax.swing.JFrame;
/*  33:    */ import javax.swing.JOptionPane;
/*  34:    */ import javax.swing.JPanel;
/*  35:    */ import javax.swing.JRadioButton;
/*  36:    */ import javax.swing.JScrollPane;
/*  37:    */ import javax.swing.JTextArea;
/*  38:    */ import javax.swing.filechooser.FileFilter;
/*  39:    */ import weka.core.Utils;
/*  40:    */ import weka.core.xml.KOML;
/*  41:    */ import weka.experiment.Experiment;
/*  42:    */ import weka.experiment.PropertyNode;
/*  43:    */ import weka.experiment.RemoteExperiment;
/*  44:    */ import weka.experiment.ResultListener;
/*  45:    */ import weka.experiment.ResultProducer;
/*  46:    */ import weka.gui.ExtensionFileFilter;
/*  47:    */ import weka.gui.GenericObjectEditor;
/*  48:    */ import weka.gui.PropertyPanel;
/*  49:    */ 
/*  50:    */ public class SetupPanel
/*  51:    */   extends AbstractSetupPanel
/*  52:    */ {
/*  53:    */   private static final long serialVersionUID = 6552671886903170033L;
/*  54:    */   protected Experiment m_Exp;
/*  55: 91 */   protected SetupModePanel m_modePanel = null;
/*  56: 94 */   protected JButton m_OpenBut = new JButton("Open...");
/*  57: 97 */   protected JButton m_SaveBut = new JButton("Save...");
/*  58:100 */   protected JButton m_NewBut = new JButton("New");
/*  59:103 */   protected FileFilter m_ExpFilter = new ExtensionFileFilter(Experiment.FILE_EXTENSION, "Experiment configuration files (*" + Experiment.FILE_EXTENSION + ")");
/*  60:111 */   protected FileFilter m_KOMLFilter = new ExtensionFileFilter(".koml", "Experiment configuration files (*.koml)");
/*  61:119 */   protected FileFilter m_XMLFilter = new ExtensionFileFilter(".xml", "Experiment configuration files (*.xml)");
/*  62:123 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  63:127 */   protected GenericObjectEditor m_RPEditor = new GenericObjectEditor();
/*  64:130 */   protected PropertyPanel m_RPEditorPanel = new PropertyPanel(this.m_RPEditor);
/*  65:133 */   protected GenericObjectEditor m_RLEditor = new GenericObjectEditor();
/*  66:136 */   protected PropertyPanel m_RLEditorPanel = new PropertyPanel(this.m_RLEditor);
/*  67:139 */   protected GeneratorPropertyIteratorPanel m_GeneratorPropertyPanel = new GeneratorPropertyIteratorPanel();
/*  68:142 */   protected RunNumberPanel m_RunNumberPanel = new RunNumberPanel();
/*  69:145 */   protected DistributeExperimentPanel m_DistributeExperimentPanel = new DistributeExperimentPanel();
/*  70:148 */   protected DatasetListPanel m_DatasetListPanel = new DatasetListPanel();
/*  71:151 */   protected JButton m_NotesButton = new JButton("Notes");
/*  72:154 */   protected JFrame m_NotesFrame = new JFrame("Notes");
/*  73:157 */   protected JTextArea m_NotesText = new JTextArea(null, 10, 0);
/*  74:163 */   protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
/*  75:166 */   protected JRadioButton m_advanceDataSetFirst = new JRadioButton("Data sets first");
/*  76:170 */   protected JRadioButton m_advanceIteratorFirst = new JRadioButton("Custom generator first");
/*  77:174 */   ActionListener m_RadioListener = new ActionListener()
/*  78:    */   {
/*  79:    */     public void actionPerformed(ActionEvent e)
/*  80:    */     {
/*  81:177 */       SetupPanel.this.updateRadioLinks();
/*  82:    */     }
/*  83:    */   };
/*  84:    */   
/*  85:    */   public SetupPanel(Experiment exp)
/*  86:    */   {
/*  87:193 */     this();
/*  88:194 */     setExperiment(exp);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public SetupPanel()
/*  92:    */   {
/*  93:202 */     this.m_DistributeExperimentPanel.addCheckBoxActionListener(new ActionListener()
/*  94:    */     {
/*  95:    */       public void actionPerformed(ActionEvent e)
/*  96:    */       {
/*  97:205 */         if (SetupPanel.this.m_DistributeExperimentPanel.distributedExperimentSelected())
/*  98:    */         {
/*  99:206 */           if (!(SetupPanel.this.m_Exp instanceof RemoteExperiment)) {
/* 100:    */             try
/* 101:    */             {
/* 102:208 */               RemoteExperiment re = new RemoteExperiment(SetupPanel.this.m_Exp);
/* 103:209 */               SetupPanel.this.setExperiment(re);
/* 104:    */             }
/* 105:    */             catch (Exception ex)
/* 106:    */             {
/* 107:211 */               ex.printStackTrace();
/* 108:    */             }
/* 109:    */           }
/* 110:    */         }
/* 111:215 */         else if ((SetupPanel.this.m_Exp instanceof RemoteExperiment)) {
/* 112:216 */           SetupPanel.this.setExperiment(((RemoteExperiment)SetupPanel.this.m_Exp).getBaseExperiment());
/* 113:    */         }
/* 114:    */       }
/* 115:221 */     });
/* 116:222 */     this.m_NewBut.setMnemonic('N');
/* 117:223 */     this.m_NewBut.addActionListener(new ActionListener()
/* 118:    */     {
/* 119:    */       public void actionPerformed(ActionEvent e)
/* 120:    */       {
/* 121:226 */         SetupPanel.this.setExperiment(new Experiment());
/* 122:    */       }
/* 123:228 */     });
/* 124:229 */     this.m_SaveBut.setMnemonic('S');
/* 125:230 */     this.m_SaveBut.setEnabled(false);
/* 126:231 */     this.m_SaveBut.addActionListener(new ActionListener()
/* 127:    */     {
/* 128:    */       public void actionPerformed(ActionEvent e)
/* 129:    */       {
/* 130:234 */         SetupPanel.this.saveExperiment();
/* 131:    */       }
/* 132:236 */     });
/* 133:237 */     this.m_OpenBut.setMnemonic('O');
/* 134:238 */     this.m_OpenBut.addActionListener(new ActionListener()
/* 135:    */     {
/* 136:    */       public void actionPerformed(ActionEvent e)
/* 137:    */       {
/* 138:241 */         SetupPanel.this.openExperiment();
/* 139:    */       }
/* 140:243 */     });
/* 141:244 */     this.m_FileChooser.addChoosableFileFilter(this.m_ExpFilter);
/* 142:245 */     if (KOML.isPresent()) {
/* 143:246 */       this.m_FileChooser.addChoosableFileFilter(this.m_KOMLFilter);
/* 144:    */     }
/* 145:248 */     this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
/* 146:249 */     this.m_FileChooser.setFileFilter(this.m_ExpFilter);
/* 147:250 */     this.m_FileChooser.setFileSelectionMode(0);
/* 148:    */     
/* 149:252 */     this.m_GeneratorPropertyPanel.addActionListener(new ActionListener()
/* 150:    */     {
/* 151:    */       public void actionPerformed(ActionEvent e)
/* 152:    */       {
/* 153:255 */         SetupPanel.this.updateRadioLinks();
/* 154:    */       }
/* 155:258 */     });
/* 156:259 */     this.m_RPEditor.setClassType(ResultProducer.class);
/* 157:260 */     this.m_RPEditor.setEnabled(false);
/* 158:261 */     this.m_RPEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 159:    */     {
/* 160:    */       public void propertyChange(PropertyChangeEvent e)
/* 161:    */       {
/* 162:264 */         SetupPanel.this.m_Exp.setResultProducer((ResultProducer)SetupPanel.this.m_RPEditor.getValue());
/* 163:265 */         SetupPanel.this.m_Exp.setUsePropertyIterator(false);
/* 164:266 */         SetupPanel.this.m_Exp.setPropertyArray(null);
/* 165:267 */         SetupPanel.this.m_Exp.setPropertyPath(null);
/* 166:268 */         SetupPanel.this.m_GeneratorPropertyPanel.setExperiment(SetupPanel.this.m_Exp);
/* 167:269 */         SetupPanel.this.repaint();
/* 168:    */       }
/* 169:272 */     });
/* 170:273 */     this.m_RLEditor.setClassType(ResultListener.class);
/* 171:274 */     this.m_RLEditor.setEnabled(false);
/* 172:275 */     this.m_RLEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 173:    */     {
/* 174:    */       public void propertyChange(PropertyChangeEvent e)
/* 175:    */       {
/* 176:278 */         SetupPanel.this.m_Exp.setResultListener((ResultListener)SetupPanel.this.m_RLEditor.getValue());
/* 177:279 */         SetupPanel.this.m_Support.firePropertyChange("", null, null);
/* 178:280 */         SetupPanel.this.repaint();
/* 179:    */       }
/* 180:283 */     });
/* 181:284 */     this.m_NotesFrame.addWindowListener(new WindowAdapter()
/* 182:    */     {
/* 183:    */       public void windowClosing(WindowEvent e)
/* 184:    */       {
/* 185:287 */         SetupPanel.this.m_NotesButton.setEnabled(true);
/* 186:    */       }
/* 187:289 */     });
/* 188:290 */     this.m_NotesFrame.getContentPane().add(new JScrollPane(this.m_NotesText));
/* 189:291 */     this.m_NotesFrame.setSize(600, 400);
/* 190:    */     
/* 191:293 */     this.m_NotesButton.addActionListener(new ActionListener()
/* 192:    */     {
/* 193:    */       public void actionPerformed(ActionEvent e)
/* 194:    */       {
/* 195:296 */         SetupPanel.this.m_NotesButton.setEnabled(false);
/* 196:297 */         SetupPanel.this.m_NotesFrame.setVisible(true);
/* 197:    */       }
/* 198:299 */     });
/* 199:300 */     this.m_NotesButton.setEnabled(false);
/* 200:    */     
/* 201:302 */     this.m_NotesText.setEditable(true);
/* 202:    */     
/* 203:304 */     this.m_NotesText.addKeyListener(new KeyAdapter()
/* 204:    */     {
/* 205:    */       public void keyReleased(KeyEvent e)
/* 206:    */       {
/* 207:307 */         SetupPanel.this.m_Exp.setNotes(SetupPanel.this.m_NotesText.getText());
/* 208:    */       }
/* 209:309 */     });
/* 210:310 */     this.m_NotesText.addFocusListener(new FocusAdapter()
/* 211:    */     {
/* 212:    */       public void focusLost(FocusEvent e)
/* 213:    */       {
/* 214:313 */         SetupPanel.this.m_Exp.setNotes(SetupPanel.this.m_NotesText.getText());
/* 215:    */       }
/* 216:317 */     });
/* 217:318 */     JPanel buttons = new JPanel();
/* 218:319 */     GridBagLayout gb = new GridBagLayout();
/* 219:320 */     GridBagConstraints constraints = new GridBagConstraints();
/* 220:321 */     buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 221:    */     
/* 222:323 */     buttons.setLayout(gb);
/* 223:324 */     constraints.gridx = 0;
/* 224:325 */     constraints.gridy = 0;
/* 225:326 */     constraints.weightx = 5.0D;
/* 226:327 */     constraints.fill = 2;
/* 227:328 */     constraints.gridwidth = 1;
/* 228:329 */     constraints.gridheight = 1;
/* 229:330 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 230:331 */     buttons.add(this.m_OpenBut, constraints);
/* 231:332 */     constraints.gridx = 1;
/* 232:333 */     constraints.gridy = 0;
/* 233:334 */     constraints.weightx = 5.0D;
/* 234:335 */     constraints.gridwidth = 1;
/* 235:336 */     constraints.gridheight = 1;
/* 236:337 */     buttons.add(this.m_SaveBut, constraints);
/* 237:338 */     constraints.gridx = 2;
/* 238:339 */     constraints.gridy = 0;
/* 239:340 */     constraints.weightx = 5.0D;
/* 240:341 */     constraints.gridwidth = 1;
/* 241:342 */     constraints.gridheight = 1;
/* 242:343 */     buttons.add(this.m_NewBut, constraints);
/* 243:    */     
/* 244:345 */     JPanel src = new JPanel();
/* 245:346 */     src.setLayout(new BorderLayout());
/* 246:347 */     src.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Result generator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/* 247:    */     
/* 248:    */ 
/* 249:350 */     src.add(this.m_RPEditorPanel, "North");
/* 250:351 */     this.m_RPEditorPanel.setEnabled(false);
/* 251:    */     
/* 252:353 */     JPanel dest = new JPanel();
/* 253:354 */     dest.setLayout(new BorderLayout());
/* 254:355 */     dest.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Destination"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/* 255:    */     
/* 256:    */ 
/* 257:358 */     dest.add(this.m_RLEditorPanel, "North");
/* 258:359 */     this.m_RLEditorPanel.setEnabled(false);
/* 259:    */     
/* 260:361 */     this.m_advanceDataSetFirst.setEnabled(false);
/* 261:362 */     this.m_advanceIteratorFirst.setEnabled(false);
/* 262:363 */     this.m_advanceDataSetFirst.setToolTipText("Advance data set before custom generator");
/* 263:    */     
/* 264:365 */     this.m_advanceIteratorFirst.setToolTipText("Advance custom generator before data set");
/* 265:    */     
/* 266:367 */     this.m_advanceDataSetFirst.setSelected(true);
/* 267:368 */     ButtonGroup bg = new ButtonGroup();
/* 268:369 */     bg.add(this.m_advanceDataSetFirst);
/* 269:370 */     bg.add(this.m_advanceIteratorFirst);
/* 270:371 */     this.m_advanceDataSetFirst.addActionListener(this.m_RadioListener);
/* 271:372 */     this.m_advanceIteratorFirst.addActionListener(this.m_RadioListener);
/* 272:    */     
/* 273:374 */     JPanel radioButs = new JPanel();
/* 274:375 */     radioButs.setBorder(BorderFactory.createTitledBorder("Iteration control"));
/* 275:376 */     radioButs.setLayout(new GridLayout(1, 2));
/* 276:377 */     radioButs.add(this.m_advanceDataSetFirst);
/* 277:378 */     radioButs.add(this.m_advanceIteratorFirst);
/* 278:    */     
/* 279:380 */     JPanel simpleIterators = new JPanel();
/* 280:381 */     simpleIterators.setLayout(new BorderLayout());
/* 281:    */     
/* 282:383 */     JPanel tmp = new JPanel();
/* 283:384 */     tmp.setLayout(new GridBagLayout());
/* 284:    */     
/* 285:386 */     constraints.gridx = 0;
/* 286:387 */     constraints.gridy = 0;
/* 287:388 */     constraints.weightx = 5.0D;
/* 288:389 */     constraints.fill = 2;
/* 289:390 */     constraints.gridwidth = 1;
/* 290:391 */     constraints.gridheight = 1;
/* 291:392 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 292:393 */     tmp.add(this.m_RunNumberPanel, constraints);
/* 293:    */     
/* 294:395 */     constraints.gridx = 1;
/* 295:396 */     constraints.gridy = 0;
/* 296:397 */     constraints.weightx = 5.0D;
/* 297:398 */     constraints.fill = 2;
/* 298:399 */     constraints.gridwidth = 1;
/* 299:400 */     constraints.gridheight = 2;
/* 300:401 */     tmp.add(this.m_DistributeExperimentPanel, constraints);
/* 301:    */     
/* 302:403 */     JPanel tmp2 = new JPanel();
/* 303:    */     
/* 304:405 */     tmp2.setLayout(new GridBagLayout());
/* 305:    */     
/* 306:407 */     constraints.gridx = 0;
/* 307:408 */     constraints.gridy = 0;
/* 308:409 */     constraints.weightx = 5.0D;
/* 309:410 */     constraints.fill = 2;
/* 310:411 */     constraints.gridwidth = 1;
/* 311:412 */     constraints.gridheight = 1;
/* 312:413 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 313:414 */     tmp2.add(tmp, constraints);
/* 314:    */     
/* 315:416 */     constraints.gridx = 0;
/* 316:417 */     constraints.gridy = 1;
/* 317:418 */     constraints.weightx = 5.0D;
/* 318:419 */     constraints.fill = 2;
/* 319:420 */     constraints.gridwidth = 1;
/* 320:421 */     constraints.gridheight = 1;
/* 321:422 */     tmp2.add(radioButs, constraints);
/* 322:    */     
/* 323:424 */     simpleIterators.add(tmp2, "North");
/* 324:425 */     simpleIterators.add(this.m_DatasetListPanel, "Center");
/* 325:426 */     JPanel iterators = new JPanel();
/* 326:427 */     iterators.setLayout(new GridLayout(1, 2));
/* 327:428 */     iterators.add(simpleIterators);
/* 328:429 */     iterators.add(this.m_GeneratorPropertyPanel);
/* 329:    */     
/* 330:431 */     JPanel top = new JPanel();
/* 331:432 */     top.setLayout(new GridLayout(2, 1));
/* 332:433 */     top.add(dest);
/* 333:434 */     top.add(src);
/* 334:    */     
/* 335:436 */     JPanel notes = new JPanel();
/* 336:437 */     notes.setLayout(new BorderLayout());
/* 337:438 */     notes.add(this.m_NotesButton, "Center");
/* 338:    */     
/* 339:440 */     JPanel p2 = new JPanel();
/* 340:    */     
/* 341:442 */     p2.setLayout(new BorderLayout());
/* 342:443 */     p2.add(iterators, "Center");
/* 343:444 */     p2.add(notes, "South");
/* 344:    */     
/* 345:446 */     JPanel p3 = new JPanel();
/* 346:447 */     p3.setLayout(new BorderLayout());
/* 347:448 */     p3.add(buttons, "North");
/* 348:449 */     p3.add(top, "South");
/* 349:450 */     setLayout(new BorderLayout());
/* 350:451 */     add(p3, "North");
/* 351:452 */     add(p2, "Center");
/* 352:    */   }
/* 353:    */   
/* 354:    */   public String getName()
/* 355:    */   {
/* 356:461 */     return "Advanced";
/* 357:    */   }
/* 358:    */   
/* 359:    */   protected void removeNotesFrame()
/* 360:    */   {
/* 361:468 */     this.m_NotesFrame.setVisible(false);
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setModePanel(SetupModePanel modePanel)
/* 365:    */   {
/* 366:477 */     this.m_modePanel = modePanel;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public boolean setExperiment(Experiment exp)
/* 370:    */   {
/* 371:487 */     boolean iteratorOn = exp.getUsePropertyIterator();
/* 372:488 */     Object propArray = exp.getPropertyArray();
/* 373:489 */     PropertyNode[] propPath = exp.getPropertyPath();
/* 374:    */     
/* 375:491 */     this.m_Exp = exp;
/* 376:492 */     this.m_SaveBut.setEnabled(true);
/* 377:493 */     this.m_RPEditor.setValue(this.m_Exp.getResultProducer());
/* 378:494 */     this.m_RPEditor.setEnabled(true);
/* 379:495 */     this.m_RPEditorPanel.setEnabled(true);
/* 380:496 */     this.m_RPEditorPanel.repaint();
/* 381:497 */     this.m_RLEditor.setValue(this.m_Exp.getResultListener());
/* 382:498 */     this.m_RLEditor.setEnabled(true);
/* 383:499 */     this.m_RLEditorPanel.setEnabled(true);
/* 384:500 */     this.m_RLEditorPanel.repaint();
/* 385:    */     
/* 386:502 */     this.m_NotesText.setText(exp.getNotes());
/* 387:503 */     this.m_NotesButton.setEnabled(true);
/* 388:    */     
/* 389:505 */     this.m_advanceDataSetFirst.setSelected(this.m_Exp.getAdvanceDataSetFirst());
/* 390:506 */     this.m_advanceIteratorFirst.setSelected(!this.m_Exp.getAdvanceDataSetFirst());
/* 391:507 */     this.m_advanceDataSetFirst.setEnabled(true);
/* 392:508 */     this.m_advanceIteratorFirst.setEnabled(true);
/* 393:    */     
/* 394:510 */     exp.setPropertyPath(propPath);
/* 395:511 */     exp.setPropertyArray(propArray);
/* 396:512 */     exp.setUsePropertyIterator(iteratorOn);
/* 397:    */     
/* 398:514 */     this.m_GeneratorPropertyPanel.setExperiment(this.m_Exp);
/* 399:515 */     this.m_RunNumberPanel.setExperiment(this.m_Exp);
/* 400:516 */     this.m_DatasetListPanel.setExperiment(this.m_Exp);
/* 401:517 */     this.m_DistributeExperimentPanel.setExperiment(this.m_Exp);
/* 402:518 */     this.m_Support.firePropertyChange("", null, null);
/* 403:    */     
/* 404:520 */     return true;
/* 405:    */   }
/* 406:    */   
/* 407:    */   public Experiment getExperiment()
/* 408:    */   {
/* 409:530 */     return this.m_Exp;
/* 410:    */   }
/* 411:    */   
/* 412:    */   private void openExperiment()
/* 413:    */   {
/* 414:538 */     int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 415:539 */     if (returnVal != 0) {
/* 416:540 */       return;
/* 417:    */     }
/* 418:542 */     File expFile = this.m_FileChooser.getSelectedFile();
/* 419:545 */     if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter)
/* 420:    */     {
/* 421:546 */       if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION)) {
/* 422:547 */         expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
/* 423:    */       }
/* 424:    */     }
/* 425:550 */     else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/* 426:    */     {
/* 427:551 */       if (!expFile.getName().toLowerCase().endsWith(".koml")) {
/* 428:552 */         expFile = new File(expFile.getParent(), expFile.getName() + ".koml");
/* 429:    */       }
/* 430:    */     }
/* 431:555 */     else if ((this.m_FileChooser.getFileFilter() == this.m_XMLFilter) && 
/* 432:556 */       (!expFile.getName().toLowerCase().endsWith(".xml"))) {
/* 433:557 */       expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
/* 434:    */     }
/* 435:    */     try
/* 436:    */     {
/* 437:562 */       Experiment exp = Experiment.read(expFile.getAbsolutePath());
/* 438:563 */       setExperiment(exp);
/* 439:564 */       System.err.println("Opened experiment:\n" + this.m_Exp);
/* 440:    */     }
/* 441:    */     catch (Exception ex)
/* 442:    */     {
/* 443:566 */       ex.printStackTrace();
/* 444:567 */       JOptionPane.showMessageDialog(this, "Couldn't open experiment file:\n" + expFile + "\nReason:\n" + ex.getMessage(), "Open Experiment", 0);
/* 445:    */     }
/* 446:    */   }
/* 447:    */   
/* 448:    */   private void saveExperiment()
/* 449:    */   {
/* 450:580 */     int returnVal = this.m_FileChooser.showSaveDialog(this);
/* 451:581 */     if (returnVal != 0) {
/* 452:582 */       return;
/* 453:    */     }
/* 454:584 */     File expFile = this.m_FileChooser.getSelectedFile();
/* 455:587 */     if (this.m_FileChooser.getFileFilter() == this.m_ExpFilter)
/* 456:    */     {
/* 457:588 */       if (!expFile.getName().toLowerCase().endsWith(Experiment.FILE_EXTENSION)) {
/* 458:589 */         expFile = new File(expFile.getParent(), expFile.getName() + Experiment.FILE_EXTENSION);
/* 459:    */       }
/* 460:    */     }
/* 461:592 */     else if (this.m_FileChooser.getFileFilter() == this.m_KOMLFilter)
/* 462:    */     {
/* 463:593 */       if (!expFile.getName().toLowerCase().endsWith(".koml")) {
/* 464:594 */         expFile = new File(expFile.getParent(), expFile.getName() + ".koml");
/* 465:    */       }
/* 466:    */     }
/* 467:597 */     else if ((this.m_FileChooser.getFileFilter() == this.m_XMLFilter) && 
/* 468:598 */       (!expFile.getName().toLowerCase().endsWith(".xml"))) {
/* 469:599 */       expFile = new File(expFile.getParent(), expFile.getName() + ".xml");
/* 470:    */     }
/* 471:    */     try
/* 472:    */     {
/* 473:604 */       Experiment.write(expFile.getAbsolutePath(), this.m_Exp);
/* 474:605 */       System.err.println("Saved experiment:\n" + this.m_Exp);
/* 475:    */     }
/* 476:    */     catch (Exception ex)
/* 477:    */     {
/* 478:607 */       ex.printStackTrace();
/* 479:608 */       JOptionPane.showMessageDialog(this, "Couldn't save experiment file:\n" + expFile + "\nReason:\n" + ex.getMessage(), "Save Experiment", 0);
/* 480:    */     }
/* 481:    */   }
/* 482:    */   
/* 483:    */   public void addPropertyChangeListener(PropertyChangeListener l)
/* 484:    */   {
/* 485:621 */     if ((this.m_Support != null) && (l != null)) {
/* 486:622 */       this.m_Support.addPropertyChangeListener(l);
/* 487:    */     }
/* 488:    */   }
/* 489:    */   
/* 490:    */   public void removePropertyChangeListener(PropertyChangeListener l)
/* 491:    */   {
/* 492:633 */     if ((this.m_Support != null) && (l != null)) {
/* 493:634 */       this.m_Support.removePropertyChangeListener(l);
/* 494:    */     }
/* 495:    */   }
/* 496:    */   
/* 497:    */   private void updateRadioLinks()
/* 498:    */   {
/* 499:643 */     this.m_advanceDataSetFirst.setEnabled(this.m_GeneratorPropertyPanel.getEditorActive());
/* 500:    */     
/* 501:645 */     this.m_advanceIteratorFirst.setEnabled(this.m_GeneratorPropertyPanel.getEditorActive());
/* 502:648 */     if (this.m_Exp != null) {
/* 503:649 */       if (!this.m_GeneratorPropertyPanel.getEditorActive()) {
/* 504:650 */         this.m_Exp.setAdvanceDataSetFirst(true);
/* 505:    */       } else {
/* 506:652 */         this.m_Exp.setAdvanceDataSetFirst(this.m_advanceDataSetFirst.isSelected());
/* 507:    */       }
/* 508:    */     }
/* 509:    */   }
/* 510:    */   
/* 511:    */   public static void main(String[] args)
/* 512:    */   {
/* 513:    */     try
/* 514:    */     {
/* 515:665 */       boolean readExp = Utils.getFlag('l', args);
/* 516:666 */       final boolean writeExp = Utils.getFlag('s', args);
/* 517:667 */       final String expFile = Utils.getOption('f', args);
/* 518:668 */       if (((readExp) || (writeExp)) && (expFile.length() == 0)) {
/* 519:669 */         throw new Exception("A filename must be given with the -f option");
/* 520:    */       }
/* 521:671 */       Experiment exp = null;
/* 522:672 */       if (readExp)
/* 523:    */       {
/* 524:673 */         FileInputStream fi = new FileInputStream(expFile);
/* 525:674 */         ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/* 526:    */         
/* 527:676 */         exp = (Experiment)oi.readObject();
/* 528:677 */         oi.close();
/* 529:    */       }
/* 530:    */       else
/* 531:    */       {
/* 532:679 */         exp = new Experiment();
/* 533:    */       }
/* 534:681 */       System.err.println("Initial Experiment:\n" + exp.toString());
/* 535:682 */       final JFrame jf = new JFrame("Weka Experiment Setup");
/* 536:683 */       jf.getContentPane().setLayout(new BorderLayout());
/* 537:684 */       SetupPanel sp = new SetupPanel();
/* 538:    */       
/* 539:686 */       jf.getContentPane().add(sp, "Center");
/* 540:687 */       jf.addWindowListener(new WindowAdapter()
/* 541:    */       {
/* 542:    */         public void windowClosing(WindowEvent e)
/* 543:    */         {
/* 544:690 */           System.err.println("\nFinal Experiment:\n" + this.val$sp.m_Exp.toString());
/* 545:692 */           if (writeExp) {
/* 546:    */             try
/* 547:    */             {
/* 548:694 */               FileOutputStream fo = new FileOutputStream(expFile);
/* 549:695 */               ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/* 550:    */               
/* 551:697 */               oo.writeObject(this.val$sp.m_Exp);
/* 552:698 */               oo.close();
/* 553:    */             }
/* 554:    */             catch (Exception ex)
/* 555:    */             {
/* 556:700 */               ex.printStackTrace();
/* 557:701 */               System.err.println("Couldn't write experiment to: " + expFile + '\n' + ex.getMessage());
/* 558:    */             }
/* 559:    */           }
/* 560:705 */           jf.dispose();
/* 561:706 */           System.exit(0);
/* 562:    */         }
/* 563:708 */       });
/* 564:709 */       jf.pack();
/* 565:710 */       jf.setVisible(true);
/* 566:711 */       System.err.println("Short nap");
/* 567:712 */       Thread.sleep(3000L);
/* 568:713 */       System.err.println("Done");
/* 569:714 */       sp.setExperiment(exp);
/* 570:    */     }
/* 571:    */     catch (Exception ex)
/* 572:    */     {
/* 573:716 */       ex.printStackTrace();
/* 574:717 */       System.err.println(ex.getMessage());
/* 575:    */     }
/* 576:    */   }
/* 577:    */   
/* 578:    */   public void cleanUpAfterSwitch()
/* 579:    */   {
/* 580:725 */     removeNotesFrame();
/* 581:    */   }
/* 582:    */   
/* 583:    */   static {}
/* 584:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.SetupPanel
 * JD-Core Version:    0.7.0.1
 */