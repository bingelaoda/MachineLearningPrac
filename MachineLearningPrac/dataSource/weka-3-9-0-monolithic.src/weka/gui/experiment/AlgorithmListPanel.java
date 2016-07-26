/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import java.awt.Insets;
/*   9:    */ import java.awt.Point;
/*  10:    */ import java.awt.Toolkit;
/*  11:    */ import java.awt.datatransfer.Clipboard;
/*  12:    */ import java.awt.datatransfer.StringSelection;
/*  13:    */ import java.awt.event.ActionEvent;
/*  14:    */ import java.awt.event.ActionListener;
/*  15:    */ import java.awt.event.MouseAdapter;
/*  16:    */ import java.awt.event.MouseEvent;
/*  17:    */ import java.awt.event.MouseListener;
/*  18:    */ import java.awt.event.WindowAdapter;
/*  19:    */ import java.awt.event.WindowEvent;
/*  20:    */ import java.beans.PropertyChangeEvent;
/*  21:    */ import java.beans.PropertyChangeListener;
/*  22:    */ import java.io.File;
/*  23:    */ import java.io.PrintStream;
/*  24:    */ import javax.swing.BorderFactory;
/*  25:    */ import javax.swing.DefaultListCellRenderer;
/*  26:    */ import javax.swing.DefaultListModel;
/*  27:    */ import javax.swing.JButton;
/*  28:    */ import javax.swing.JFileChooser;
/*  29:    */ import javax.swing.JFrame;
/*  30:    */ import javax.swing.JList;
/*  31:    */ import javax.swing.JMenuItem;
/*  32:    */ import javax.swing.JOptionPane;
/*  33:    */ import javax.swing.JPanel;
/*  34:    */ import javax.swing.JPopupMenu;
/*  35:    */ import javax.swing.JScrollPane;
/*  36:    */ import javax.swing.event.ListSelectionEvent;
/*  37:    */ import javax.swing.event.ListSelectionListener;
/*  38:    */ import javax.swing.filechooser.FileFilter;
/*  39:    */ import weka.classifiers.Classifier;
/*  40:    */ import weka.classifiers.rules.ZeroR;
/*  41:    */ import weka.classifiers.xml.XMLClassifier;
/*  42:    */ import weka.core.OptionHandler;
/*  43:    */ import weka.core.SerializedObject;
/*  44:    */ import weka.core.Utils;
/*  45:    */ import weka.experiment.Experiment;
/*  46:    */ import weka.gui.ExtensionFileFilter;
/*  47:    */ import weka.gui.GenericObjectEditor;
/*  48:    */ import weka.gui.GenericObjectEditor.GOEPanel;
/*  49:    */ import weka.gui.JListHelper;
/*  50:    */ import weka.gui.PropertyDialog;
/*  51:    */ 
/*  52:    */ public class AlgorithmListPanel
/*  53:    */   extends JPanel
/*  54:    */   implements ActionListener
/*  55:    */ {
/*  56:    */   private static final long serialVersionUID = -7204528834764898671L;
/*  57:    */   protected Experiment m_Exp;
/*  58:    */   protected JList m_List;
/*  59:    */   
/*  60:    */   public class ObjectCellRenderer
/*  61:    */     extends DefaultListCellRenderer
/*  62:    */   {
/*  63:    */     private static final long serialVersionUID = -5067138526587433808L;
/*  64:    */     
/*  65:    */     public ObjectCellRenderer() {}
/*  66:    */     
/*  67:    */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*  68:    */     {
/*  69:108 */       Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*  70:    */       
/*  71:110 */       String rep = value.getClass().getName();
/*  72:111 */       int dotPos = rep.lastIndexOf('.');
/*  73:112 */       if (dotPos != -1) {
/*  74:113 */         rep = rep.substring(dotPos + 1);
/*  75:    */       }
/*  76:115 */       if ((value instanceof OptionHandler)) {
/*  77:116 */         rep = rep + " " + Utils.joinOptions(((OptionHandler)value).getOptions());
/*  78:    */       }
/*  79:118 */       setText(rep);
/*  80:119 */       return c;
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:130 */   protected JButton m_AddBut = new JButton("Add new...");
/*  85:133 */   protected JButton m_EditBut = new JButton("Edit selected...");
/*  86:136 */   protected JButton m_DeleteBut = new JButton("Delete selected");
/*  87:139 */   protected JButton m_LoadOptionsBut = new JButton("Load options...");
/*  88:142 */   protected JButton m_SaveOptionsBut = new JButton("Save options...");
/*  89:145 */   protected JButton m_UpBut = new JButton("Up");
/*  90:148 */   protected JButton m_DownBut = new JButton("Down");
/*  91:151 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  92:158 */   protected FileFilter m_XMLFilter = new ExtensionFileFilter(".xml", "Classifier options (*.xml)");
/*  93:162 */   protected boolean m_Editing = false;
/*  94:165 */   protected GenericObjectEditor m_ClassifierEditor = new GenericObjectEditor(true);
/*  95:    */   protected PropertyDialog m_PD;
/*  96:172 */   protected DefaultListModel m_AlgorithmListModel = new DefaultListModel();
/*  97:    */   
/*  98:    */   public AlgorithmListPanel(Experiment exp)
/*  99:    */   {
/* 100:186 */     this();
/* 101:187 */     setExperiment(exp);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public AlgorithmListPanel()
/* 105:    */   {
/* 106:194 */     final AlgorithmListPanel self = this;
/* 107:195 */     this.m_List = new JList();
/* 108:196 */     MouseListener mouseListener = new MouseAdapter()
/* 109:    */     {
/* 110:    */       public void mouseClicked(MouseEvent e)
/* 111:    */       {
/* 112:199 */         final int index = AlgorithmListPanel.this.m_List.locationToIndex(e.getPoint());
/* 113:201 */         if ((e.getClickCount() == 2) && (e.getButton() == 1))
/* 114:    */         {
/* 115:206 */           if (index > -1) {
/* 116:207 */             AlgorithmListPanel.this.actionPerformed(new ActionEvent(AlgorithmListPanel.this.m_EditBut, 0, ""));
/* 117:    */           }
/* 118:    */         }
/* 119:209 */         else if ((e.getClickCount() == 1) && (
/* 120:210 */           (e.getButton() == 3) || ((e.getButton() == 1) && (e.isAltDown()) && (e.isShiftDown()))))
/* 121:    */         {
/* 122:213 */           JPopupMenu menu = new JPopupMenu();
/* 123:    */           
/* 124:    */ 
/* 125:216 */           JMenuItem item = new JMenuItem("Add configuration...");
/* 126:217 */           item.addActionListener(new ActionListener()
/* 127:    */           {
/* 128:    */             public void actionPerformed(ActionEvent e)
/* 129:    */             {
/* 130:220 */               String str = JOptionPane.showInputDialog(AlgorithmListPanel.1.this.val$self, "Configuration (<classname> [<options>])");
/* 131:222 */               if (str != null) {
/* 132:    */                 try
/* 133:    */                 {
/* 134:224 */                   String[] options = Utils.splitOptions(str);
/* 135:225 */                   String classname = options[0];
/* 136:226 */                   options[0] = "";
/* 137:227 */                   Object obj = Utils.forName(Object.class, classname, options);
/* 138:    */                   
/* 139:229 */                   AlgorithmListPanel.this.m_AlgorithmListModel.addElement(obj);
/* 140:230 */                   AlgorithmListPanel.this.updateExperiment();
/* 141:    */                 }
/* 142:    */                 catch (Exception ex)
/* 143:    */                 {
/* 144:232 */                   ex.printStackTrace();
/* 145:233 */                   JOptionPane.showMessageDialog(AlgorithmListPanel.1.this.val$self, "Error parsing commandline:\n" + ex, "Error...", 0);
/* 146:    */                 }
/* 147:    */               }
/* 148:    */             }
/* 149:239 */           });
/* 150:240 */           menu.add(item);
/* 151:242 */           if (AlgorithmListPanel.this.m_List.getSelectedValue() != null)
/* 152:    */           {
/* 153:243 */             menu.addSeparator();
/* 154:    */             
/* 155:245 */             item = new JMenuItem("Show properties...");
/* 156:246 */             item.addActionListener(new ActionListener()
/* 157:    */             {
/* 158:    */               public void actionPerformed(ActionEvent e)
/* 159:    */               {
/* 160:249 */                 AlgorithmListPanel.1.this.val$self.actionPerformed(new ActionEvent(AlgorithmListPanel.this.m_EditBut, 0, ""));
/* 161:    */               }
/* 162:251 */             });
/* 163:252 */             menu.add(item);
/* 164:    */             
/* 165:254 */             item = new JMenuItem("Copy configuration to clipboard");
/* 166:255 */             item.addActionListener(new ActionListener()
/* 167:    */             {
/* 168:    */               public void actionPerformed(ActionEvent e)
/* 169:    */               {
/* 170:258 */                 String str = AlgorithmListPanel.this.m_List.getSelectedValue().getClass().getName();
/* 171:259 */                 if ((AlgorithmListPanel.this.m_List.getSelectedValue() instanceof OptionHandler)) {
/* 172:260 */                   str = str + " " + Utils.joinOptions(((OptionHandler)AlgorithmListPanel.this.m_List.getSelectedValue()).getOptions());
/* 173:    */                 }
/* 174:264 */                 StringSelection selection = new StringSelection(str.trim());
/* 175:265 */                 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 176:    */                 
/* 177:267 */                 clipboard.setContents(selection, selection);
/* 178:    */               }
/* 179:269 */             });
/* 180:270 */             menu.add(item);
/* 181:    */             
/* 182:272 */             item = new JMenuItem("Enter configuration...");
/* 183:273 */             item.addActionListener(new ActionListener()
/* 184:    */             {
/* 185:    */               public void actionPerformed(ActionEvent e)
/* 186:    */               {
/* 187:276 */                 String str = JOptionPane.showInputDialog(AlgorithmListPanel.1.this.val$self, "Configuration (<classname> [<options>])");
/* 188:278 */                 if (str != null) {
/* 189:    */                   try
/* 190:    */                   {
/* 191:280 */                     String[] options = Utils.splitOptions(str);
/* 192:281 */                     String classname = options[0];
/* 193:282 */                     options[0] = "";
/* 194:283 */                     Object obj = Utils.forName(Object.class, classname, options);
/* 195:    */                     
/* 196:285 */                     AlgorithmListPanel.this.m_AlgorithmListModel.setElementAt(obj, index);
/* 197:286 */                     AlgorithmListPanel.this.updateExperiment();
/* 198:    */                   }
/* 199:    */                   catch (Exception ex)
/* 200:    */                   {
/* 201:288 */                     ex.printStackTrace();
/* 202:289 */                     JOptionPane.showMessageDialog(AlgorithmListPanel.1.this.val$self, "Error parsing commandline:\n" + ex, "Error...", 0);
/* 203:    */                   }
/* 204:    */                 }
/* 205:    */               }
/* 206:295 */             });
/* 207:296 */             menu.add(item);
/* 208:    */           }
/* 209:299 */           menu.show(AlgorithmListPanel.this.m_List, e.getX(), e.getY());
/* 210:    */         }
/* 211:    */       }
/* 212:303 */     };
/* 213:304 */     this.m_List.addMouseListener(mouseListener);
/* 214:    */     
/* 215:306 */     this.m_ClassifierEditor.setClassType(Classifier.class);
/* 216:307 */     this.m_ClassifierEditor.setValue(new ZeroR());
/* 217:308 */     this.m_ClassifierEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 218:    */     {
/* 219:    */       public void propertyChange(PropertyChangeEvent e)
/* 220:    */       {
/* 221:311 */         AlgorithmListPanel.this.repaint();
/* 222:    */       }
/* 223:313 */     });
/* 224:314 */     ((GenericObjectEditor.GOEPanel)this.m_ClassifierEditor.getCustomEditor()).addOkListener(new ActionListener()
/* 225:    */     {
/* 226:    */       public void actionPerformed(ActionEvent e)
/* 227:    */       {
/* 228:318 */         Classifier newCopy = (Classifier)AlgorithmListPanel.this.copyObject(AlgorithmListPanel.this.m_ClassifierEditor.getValue());
/* 229:    */         
/* 230:320 */         AlgorithmListPanel.this.addNewAlgorithm(newCopy);
/* 231:    */       }
/* 232:323 */     });
/* 233:324 */     this.m_DeleteBut.setEnabled(false);
/* 234:325 */     this.m_DeleteBut.addActionListener(this);
/* 235:326 */     this.m_AddBut.setEnabled(false);
/* 236:327 */     this.m_AddBut.addActionListener(this);
/* 237:328 */     this.m_EditBut.setEnabled(false);
/* 238:329 */     this.m_EditBut.addActionListener(this);
/* 239:330 */     this.m_LoadOptionsBut.setEnabled(false);
/* 240:331 */     this.m_LoadOptionsBut.addActionListener(this);
/* 241:332 */     this.m_SaveOptionsBut.setEnabled(false);
/* 242:333 */     this.m_SaveOptionsBut.addActionListener(this);
/* 243:334 */     this.m_UpBut.setEnabled(false);
/* 244:335 */     this.m_UpBut.addActionListener(this);
/* 245:336 */     this.m_DownBut.setEnabled(false);
/* 246:337 */     this.m_DownBut.addActionListener(this);
/* 247:    */     
/* 248:339 */     this.m_List.addListSelectionListener(new ListSelectionListener()
/* 249:    */     {
/* 250:    */       public void valueChanged(ListSelectionEvent e)
/* 251:    */       {
/* 252:342 */         AlgorithmListPanel.this.setButtons(e);
/* 253:    */       }
/* 254:345 */     });
/* 255:346 */     this.m_FileChooser.addChoosableFileFilter(this.m_XMLFilter);
/* 256:347 */     this.m_FileChooser.setFileSelectionMode(0);
/* 257:    */     
/* 258:349 */     setLayout(new BorderLayout());
/* 259:350 */     setBorder(BorderFactory.createTitledBorder("Algorithms"));
/* 260:351 */     JPanel topLab = new JPanel();
/* 261:352 */     GridBagLayout gb = new GridBagLayout();
/* 262:353 */     GridBagConstraints constraints = new GridBagConstraints();
/* 263:354 */     topLab.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 264:355 */     topLab.setLayout(gb);
/* 265:    */     
/* 266:357 */     constraints.gridx = 0;
/* 267:358 */     constraints.gridy = 0;
/* 268:359 */     constraints.weightx = 5.0D;
/* 269:360 */     constraints.fill = 2;
/* 270:361 */     constraints.gridwidth = 1;
/* 271:362 */     constraints.gridheight = 1;
/* 272:363 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 273:364 */     topLab.add(this.m_AddBut, constraints);
/* 274:365 */     constraints.gridx = 1;
/* 275:366 */     constraints.gridy = 0;
/* 276:367 */     constraints.weightx = 5.0D;
/* 277:368 */     constraints.gridwidth = 1;
/* 278:369 */     constraints.gridheight = 1;
/* 279:370 */     topLab.add(this.m_EditBut, constraints);
/* 280:371 */     constraints.gridx = 2;
/* 281:372 */     constraints.gridy = 0;
/* 282:373 */     constraints.weightx = 5.0D;
/* 283:374 */     constraints.gridwidth = 1;
/* 284:375 */     constraints.gridheight = 1;
/* 285:376 */     topLab.add(this.m_DeleteBut, constraints);
/* 286:    */     
/* 287:378 */     JPanel bottomLab = new JPanel();
/* 288:379 */     gb = new GridBagLayout();
/* 289:380 */     constraints = new GridBagConstraints();
/* 290:381 */     bottomLab.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 291:382 */     bottomLab.setLayout(gb);
/* 292:    */     
/* 293:384 */     constraints.gridx = 0;
/* 294:385 */     constraints.gridy = 0;
/* 295:386 */     constraints.weightx = 5.0D;
/* 296:387 */     constraints.fill = 2;
/* 297:388 */     constraints.gridwidth = 1;
/* 298:389 */     constraints.gridheight = 1;
/* 299:390 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 300:391 */     bottomLab.add(this.m_LoadOptionsBut, constraints);
/* 301:392 */     constraints.gridx = 1;
/* 302:393 */     constraints.gridy = 0;
/* 303:394 */     constraints.weightx = 5.0D;
/* 304:395 */     constraints.gridwidth = 1;
/* 305:396 */     constraints.gridheight = 1;
/* 306:397 */     bottomLab.add(this.m_SaveOptionsBut, constraints);
/* 307:398 */     constraints.gridx = 2;
/* 308:399 */     constraints.gridy = 0;
/* 309:400 */     constraints.weightx = 5.0D;
/* 310:401 */     constraints.gridwidth = 1;
/* 311:402 */     constraints.gridheight = 1;
/* 312:403 */     bottomLab.add(this.m_UpBut, constraints);
/* 313:404 */     constraints.gridx = 3;
/* 314:405 */     constraints.gridy = 0;
/* 315:406 */     constraints.weightx = 5.0D;
/* 316:407 */     constraints.gridwidth = 1;
/* 317:408 */     constraints.gridheight = 1;
/* 318:409 */     bottomLab.add(this.m_DownBut, constraints);
/* 319:    */     
/* 320:411 */     add(topLab, "North");
/* 321:412 */     add(new JScrollPane(this.m_List), "Center");
/* 322:413 */     add(bottomLab, "South");
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void setExperiment(Experiment exp)
/* 326:    */   {
/* 327:423 */     this.m_Exp = exp;
/* 328:424 */     this.m_AddBut.setEnabled(true);
/* 329:425 */     this.m_List.setModel(this.m_AlgorithmListModel);
/* 330:426 */     this.m_List.setCellRenderer(new ObjectCellRenderer());
/* 331:427 */     this.m_AlgorithmListModel.removeAllElements();
/* 332:428 */     if ((this.m_Exp.getPropertyArray() instanceof Classifier[]))
/* 333:    */     {
/* 334:429 */       Classifier[] algorithms = (Classifier[])this.m_Exp.getPropertyArray();
/* 335:430 */       for (Classifier algorithm : algorithms) {
/* 336:431 */         this.m_AlgorithmListModel.addElement(algorithm);
/* 337:    */       }
/* 338:    */     }
/* 339:434 */     this.m_EditBut.setEnabled(this.m_AlgorithmListModel.size() > 0);
/* 340:435 */     this.m_DeleteBut.setEnabled(this.m_AlgorithmListModel.size() > 0);
/* 341:436 */     this.m_LoadOptionsBut.setEnabled(this.m_AlgorithmListModel.size() > 0);
/* 342:437 */     this.m_SaveOptionsBut.setEnabled(this.m_AlgorithmListModel.size() > 0);
/* 343:438 */     this.m_UpBut.setEnabled(JListHelper.canMoveUp(this.m_List));
/* 344:439 */     this.m_DownBut.setEnabled(JListHelper.canMoveDown(this.m_List));
/* 345:    */   }
/* 346:    */   
/* 347:    */   private void addNewAlgorithm(Classifier newScheme)
/* 348:    */   {
/* 349:448 */     if (!this.m_Editing) {
/* 350:449 */       this.m_AlgorithmListModel.addElement(newScheme);
/* 351:    */     } else {
/* 352:451 */       this.m_AlgorithmListModel.setElementAt(newScheme, this.m_List.getSelectedIndex());
/* 353:    */     }
/* 354:454 */     updateExperiment();
/* 355:    */     
/* 356:456 */     this.m_Editing = false;
/* 357:    */   }
/* 358:    */   
/* 359:    */   private void updateExperiment()
/* 360:    */   {
/* 361:463 */     Classifier[] cArray = new Classifier[this.m_AlgorithmListModel.size()];
/* 362:464 */     for (int i = 0; i < cArray.length; i++) {
/* 363:465 */       cArray[i] = ((Classifier)this.m_AlgorithmListModel.elementAt(i));
/* 364:    */     }
/* 365:467 */     this.m_Exp.setPropertyArray(cArray);
/* 366:    */   }
/* 367:    */   
/* 368:    */   private void setButtons(ListSelectionEvent e)
/* 369:    */   {
/* 370:476 */     if (e.getSource() == this.m_List)
/* 371:    */     {
/* 372:477 */       this.m_DeleteBut.setEnabled(this.m_List.getSelectedIndex() > -1);
/* 373:478 */       this.m_AddBut.setEnabled(true);
/* 374:479 */       this.m_EditBut.setEnabled(this.m_List.getSelectedIndices().length == 1);
/* 375:480 */       this.m_LoadOptionsBut.setEnabled(this.m_List.getSelectedIndices().length == 1);
/* 376:481 */       this.m_SaveOptionsBut.setEnabled(this.m_List.getSelectedIndices().length == 1);
/* 377:482 */       this.m_UpBut.setEnabled(JListHelper.canMoveUp(this.m_List));
/* 378:483 */       this.m_DownBut.setEnabled(JListHelper.canMoveDown(this.m_List));
/* 379:    */     }
/* 380:    */   }
/* 381:    */   
/* 382:    */   public void actionPerformed(ActionEvent e)
/* 383:    */   {
/* 384:495 */     if (e.getSource() == this.m_AddBut)
/* 385:    */     {
/* 386:496 */       this.m_Editing = false;
/* 387:497 */       if (this.m_PD == null)
/* 388:    */       {
/* 389:498 */         int x = getLocationOnScreen().x;
/* 390:499 */         int y = getLocationOnScreen().y;
/* 391:500 */         if (PropertyDialog.getParentDialog(this) != null) {
/* 392:501 */           this.m_PD = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_ClassifierEditor, x, y);
/* 393:    */         } else {
/* 394:504 */           this.m_PD = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_ClassifierEditor, x, y);
/* 395:    */         }
/* 396:507 */         this.m_PD.setVisible(true);
/* 397:    */       }
/* 398:    */       else
/* 399:    */       {
/* 400:509 */         this.m_PD.setVisible(true);
/* 401:    */       }
/* 402:    */     }
/* 403:512 */     else if (e.getSource() == this.m_EditBut)
/* 404:    */     {
/* 405:513 */       if (this.m_List.getSelectedValue() != null)
/* 406:    */       {
/* 407:514 */         this.m_ClassifierEditor.setClassType(Classifier.class);
/* 408:    */         
/* 409:516 */         this.m_ClassifierEditor.setValue(this.m_List.getSelectedValue());
/* 410:517 */         this.m_Editing = true;
/* 411:518 */         if (this.m_PD == null)
/* 412:    */         {
/* 413:519 */           int x = getLocationOnScreen().x;
/* 414:520 */           int y = getLocationOnScreen().y;
/* 415:521 */           if (PropertyDialog.getParentDialog(this) != null) {
/* 416:522 */             this.m_PD = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_ClassifierEditor, x, y);
/* 417:    */           } else {
/* 418:525 */             this.m_PD = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_ClassifierEditor, x, y);
/* 419:    */           }
/* 420:528 */           this.m_PD.setVisible(true);
/* 421:    */         }
/* 422:    */         else
/* 423:    */         {
/* 424:530 */           this.m_PD.setVisible(true);
/* 425:    */         }
/* 426:    */       }
/* 427:    */     }
/* 428:534 */     else if (e.getSource() == this.m_DeleteBut)
/* 429:    */     {
/* 430:536 */       int[] selected = this.m_List.getSelectedIndices();
/* 431:537 */       if (selected != null) {
/* 432:538 */         for (int i = selected.length - 1; i >= 0; i--)
/* 433:    */         {
/* 434:539 */           int current = selected[i];
/* 435:540 */           this.m_AlgorithmListModel.removeElementAt(current);
/* 436:541 */           if (this.m_Exp.getDatasets().size() > current) {
/* 437:542 */             this.m_List.setSelectedIndex(current);
/* 438:    */           } else {
/* 439:544 */             this.m_List.setSelectedIndex(current - 1);
/* 440:    */           }
/* 441:    */         }
/* 442:    */       }
/* 443:548 */       if (this.m_List.getSelectedIndex() == -1)
/* 444:    */       {
/* 445:549 */         this.m_EditBut.setEnabled(false);
/* 446:550 */         this.m_DeleteBut.setEnabled(false);
/* 447:551 */         this.m_LoadOptionsBut.setEnabled(false);
/* 448:552 */         this.m_SaveOptionsBut.setEnabled(false);
/* 449:553 */         this.m_UpBut.setEnabled(false);
/* 450:554 */         this.m_DownBut.setEnabled(false);
/* 451:    */       }
/* 452:557 */       updateExperiment();
/* 453:    */     }
/* 454:558 */     else if (e.getSource() == this.m_LoadOptionsBut)
/* 455:    */     {
/* 456:559 */       if (this.m_List.getSelectedValue() != null)
/* 457:    */       {
/* 458:560 */         int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 459:561 */         if (returnVal == 0) {
/* 460:    */           try
/* 461:    */           {
/* 462:563 */             File file = this.m_FileChooser.getSelectedFile();
/* 463:564 */             if (!file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
/* 464:565 */               file = new File(file.getAbsolutePath() + ".xml");
/* 465:    */             }
/* 466:567 */             XMLClassifier xmlcls = new XMLClassifier();
/* 467:568 */             Classifier c = (Classifier)xmlcls.read(file);
/* 468:569 */             this.m_AlgorithmListModel.setElementAt(c, this.m_List.getSelectedIndex());
/* 469:570 */             updateExperiment();
/* 470:    */           }
/* 471:    */           catch (Exception ex)
/* 472:    */           {
/* 473:572 */             ex.printStackTrace();
/* 474:    */           }
/* 475:    */         }
/* 476:    */       }
/* 477:    */     }
/* 478:576 */     else if (e.getSource() == this.m_SaveOptionsBut)
/* 479:    */     {
/* 480:577 */       if (this.m_List.getSelectedValue() != null)
/* 481:    */       {
/* 482:578 */         int returnVal = this.m_FileChooser.showSaveDialog(this);
/* 483:579 */         if (returnVal == 0) {
/* 484:    */           try
/* 485:    */           {
/* 486:581 */             File file = this.m_FileChooser.getSelectedFile();
/* 487:582 */             if (!file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
/* 488:583 */               file = new File(file.getAbsolutePath() + ".xml");
/* 489:    */             }
/* 490:585 */             XMLClassifier xmlcls = new XMLClassifier();
/* 491:586 */             xmlcls.write(file, this.m_List.getSelectedValue());
/* 492:    */           }
/* 493:    */           catch (Exception ex)
/* 494:    */           {
/* 495:588 */             ex.printStackTrace();
/* 496:    */           }
/* 497:    */         }
/* 498:    */       }
/* 499:    */     }
/* 500:592 */     else if (e.getSource() == this.m_UpBut)
/* 501:    */     {
/* 502:593 */       JListHelper.moveUp(this.m_List);
/* 503:594 */       updateExperiment();
/* 504:    */     }
/* 505:595 */     else if (e.getSource() == this.m_DownBut)
/* 506:    */     {
/* 507:596 */       JListHelper.moveDown(this.m_List);
/* 508:597 */       updateExperiment();
/* 509:    */     }
/* 510:    */   }
/* 511:    */   
/* 512:    */   protected Object copyObject(Object source)
/* 513:    */   {
/* 514:609 */     Object result = null;
/* 515:    */     try
/* 516:    */     {
/* 517:611 */       SerializedObject so = new SerializedObject(source);
/* 518:612 */       result = so.getObject();
/* 519:    */     }
/* 520:    */     catch (Exception ex)
/* 521:    */     {
/* 522:614 */       System.err.println("AlgorithmListPanel: Problem copying object");
/* 523:615 */       System.err.println(ex);
/* 524:    */     }
/* 525:617 */     return result;
/* 526:    */   }
/* 527:    */   
/* 528:    */   public static void main(String[] args)
/* 529:    */   {
/* 530:    */     try
/* 531:    */     {
/* 532:628 */       JFrame jf = new JFrame("Algorithm List Editor");
/* 533:629 */       jf.getContentPane().setLayout(new BorderLayout());
/* 534:630 */       AlgorithmListPanel dp = new AlgorithmListPanel();
/* 535:631 */       jf.getContentPane().add(dp, "Center");
/* 536:632 */       jf.addWindowListener(new WindowAdapter()
/* 537:    */       {
/* 538:    */         public void windowClosing(WindowEvent e)
/* 539:    */         {
/* 540:635 */           this.val$jf.dispose();
/* 541:636 */           System.exit(0);
/* 542:    */         }
/* 543:638 */       });
/* 544:639 */       jf.pack();
/* 545:640 */       jf.setVisible(true);
/* 546:641 */       System.err.println("Short nap");
/* 547:642 */       Thread.sleep(3000L);
/* 548:643 */       System.err.println("Done");
/* 549:644 */       dp.setExperiment(new Experiment());
/* 550:    */     }
/* 551:    */     catch (Exception ex)
/* 552:    */     {
/* 553:646 */       ex.printStackTrace();
/* 554:647 */       System.err.println(ex.getMessage());
/* 555:    */     }
/* 556:    */   }
/* 557:    */   
/* 558:    */   static {}
/* 559:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.AlgorithmListPanel
 * JD-Core Version:    0.7.0.1
 */