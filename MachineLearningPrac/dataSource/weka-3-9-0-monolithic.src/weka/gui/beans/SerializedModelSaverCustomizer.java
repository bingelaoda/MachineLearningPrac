/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog.ModalityType;
/*   6:    */ import java.awt.FlowLayout;
/*   7:    */ import java.awt.GridBagConstraints;
/*   8:    */ import java.awt.GridBagLayout;
/*   9:    */ import java.awt.Window;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.beans.PropertyChangeListener;
/*  13:    */ import java.beans.PropertyChangeSupport;
/*  14:    */ import java.io.File;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import java.util.ArrayList;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JCheckBox;
/*  20:    */ import javax.swing.JComboBox;
/*  21:    */ import javax.swing.JDialog;
/*  22:    */ import javax.swing.JFileChooser;
/*  23:    */ import javax.swing.JLabel;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import javax.swing.JTextField;
/*  26:    */ import javax.swing.filechooser.FileFilter;
/*  27:    */ import weka.core.Environment;
/*  28:    */ import weka.core.EnvironmentHandler;
/*  29:    */ import weka.core.Tag;
/*  30:    */ import weka.gui.GenericObjectEditor;
/*  31:    */ import weka.gui.PropertySheetPanel;
/*  32:    */ 
/*  33:    */ public class SerializedModelSaverCustomizer
/*  34:    */   extends JPanel
/*  35:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener, EnvironmentHandler
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = -4874208115942078471L;
/*  38: 72 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  39:    */   private SerializedModelSaver m_smSaver;
/*  40: 77 */   private final PropertySheetPanel m_SaverEditor = new PropertySheetPanel();
/*  41: 80 */   private final JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  42:    */   private Window m_parentWindow;
/*  43:    */   private JDialog m_fileChooserFrame;
/*  44:    */   private EnvironmentField m_prefixText;
/*  45:    */   private JTextField m_incrementalSaveSchedule;
/*  46:    */   private JComboBox m_fileFormatBox;
/*  47:    */   private JCheckBox m_relativeFilePath;
/*  48:    */   private JCheckBox m_includeRelationName;
/*  49: 98 */   private Environment m_env = Environment.getSystemWide();
/*  50:    */   private EnvironmentField m_directoryText;
/*  51:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  52:    */   private String m_prefixBackup;
/*  53:    */   private File m_directoryBackup;
/*  54:    */   private boolean m_relativeBackup;
/*  55:    */   private boolean m_relationBackup;
/*  56:    */   private Tag m_formatBackup;
/*  57:    */   
/*  58:    */   public SerializedModelSaverCustomizer()
/*  59:    */   {
/*  60:121 */     setLayout(new BorderLayout());
/*  61:    */     
/*  62:123 */     this.m_fileChooser.setDialogType(1);
/*  63:124 */     this.m_fileChooser.setFileSelectionMode(1);
/*  64:125 */     this.m_fileChooser.setApproveButtonText("Select directory and prefix");
/*  65:    */     
/*  66:127 */     this.m_fileChooser.addActionListener(new ActionListener()
/*  67:    */     {
/*  68:    */       public void actionPerformed(ActionEvent e)
/*  69:    */       {
/*  70:130 */         if (e.getActionCommand().equals("ApproveSelection")) {
/*  71:    */           try
/*  72:    */           {
/*  73:132 */             SerializedModelSaverCustomizer.this.m_smSaver.setPrefix(SerializedModelSaverCustomizer.this.m_prefixText.getText());
/*  74:    */             
/*  75:    */ 
/*  76:135 */             File selectedFile = SerializedModelSaverCustomizer.this.m_fileChooser.getSelectedFile();
/*  77:136 */             SerializedModelSaverCustomizer.this.m_directoryText.setText(selectedFile.toString());
/*  78:    */           }
/*  79:    */           catch (Exception ex)
/*  80:    */           {
/*  81:139 */             ex.printStackTrace();
/*  82:    */           }
/*  83:    */         }
/*  84:143 */         if (SerializedModelSaverCustomizer.this.m_parentWindow != null) {
/*  85:144 */           SerializedModelSaverCustomizer.this.m_fileChooserFrame.dispose();
/*  86:    */         }
/*  87:    */       }
/*  88:    */     });
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setParentWindow(Window parent)
/*  92:    */   {
/*  93:152 */     this.m_parentWindow = parent;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setUpFile()
/*  97:    */   {
/*  98:157 */     removeAll();
/*  99:158 */     this.m_fileChooser.setFileFilter(new FileFilter()
/* 100:    */     {
/* 101:    */       public boolean accept(File f)
/* 102:    */       {
/* 103:161 */         return f.isDirectory();
/* 104:    */       }
/* 105:    */       
/* 106:    */       public String getDescription()
/* 107:    */       {
/* 108:166 */         return "Directory";
/* 109:    */       }
/* 110:169 */     });
/* 111:170 */     this.m_fileChooser.setAcceptAllFileFilterUsed(false);
/* 112:    */     try
/* 113:    */     {
/* 114:173 */       if (!this.m_smSaver.getDirectory().getPath().equals(""))
/* 115:    */       {
/* 116:175 */         String dirStr = this.m_smSaver.getDirectory().toString();
/* 117:176 */         if (Environment.containsEnvVariables(dirStr)) {
/* 118:    */           try
/* 119:    */           {
/* 120:178 */             dirStr = this.m_env.substitute(dirStr);
/* 121:    */           }
/* 122:    */           catch (Exception ex) {}
/* 123:    */         }
/* 124:183 */         File tmp = new File(dirStr);
/* 125:    */         
/* 126:185 */         tmp = new File(tmp.getAbsolutePath());
/* 127:186 */         this.m_fileChooser.setCurrentDirectory(tmp);
/* 128:    */       }
/* 129:    */     }
/* 130:    */     catch (Exception ex)
/* 131:    */     {
/* 132:189 */       System.out.println(ex);
/* 133:    */     }
/* 134:192 */     JPanel innerPanel = new JPanel();
/* 135:193 */     innerPanel.setLayout(new BorderLayout());
/* 136:    */     
/* 137:195 */     JPanel alignedP = new JPanel();
/* 138:196 */     GridBagLayout gbLayout = new GridBagLayout();
/* 139:197 */     alignedP.setLayout(gbLayout);
/* 140:    */     
/* 141:199 */     JLabel prefixLab = new JLabel("Prefix for file name", 4);
/* 142:200 */     prefixLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 143:201 */     GridBagConstraints gbConstraints = new GridBagConstraints();
/* 144:202 */     gbConstraints.anchor = 13;
/* 145:203 */     gbConstraints.fill = 2;
/* 146:204 */     gbConstraints.gridy = 0;
/* 147:205 */     gbConstraints.gridx = 0;
/* 148:206 */     gbLayout.setConstraints(prefixLab, gbConstraints);
/* 149:207 */     alignedP.add(prefixLab);
/* 150:    */     
/* 151:    */ 
/* 152:210 */     this.m_prefixText = new EnvironmentField();
/* 153:211 */     this.m_prefixText.setEnvironment(this.m_env);
/* 154:    */     
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:218 */     this.m_prefixText.setText(this.m_smSaver.getPrefix());
/* 161:219 */     gbConstraints = new GridBagConstraints();
/* 162:220 */     gbConstraints.anchor = 13;
/* 163:221 */     gbConstraints.fill = 2;
/* 164:222 */     gbConstraints.gridy = 0;
/* 165:223 */     gbConstraints.gridx = 1;
/* 166:224 */     gbLayout.setConstraints(this.m_prefixText, gbConstraints);
/* 167:225 */     alignedP.add(this.m_prefixText);
/* 168:    */     
/* 169:227 */     JLabel ffLab = new JLabel("File format", 4);
/* 170:228 */     ffLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 171:229 */     gbConstraints = new GridBagConstraints();
/* 172:230 */     gbConstraints.anchor = 13;
/* 173:231 */     gbConstraints.fill = 2;
/* 174:232 */     gbConstraints.gridy = 1;
/* 175:233 */     gbConstraints.gridx = 0;
/* 176:234 */     gbLayout.setConstraints(ffLab, gbConstraints);
/* 177:235 */     alignedP.add(ffLab);
/* 178:    */     
/* 179:237 */     setUpFileFormatComboBox();
/* 180:238 */     this.m_fileFormatBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/* 181:239 */     gbConstraints = new GridBagConstraints();
/* 182:240 */     gbConstraints.anchor = 13;
/* 183:241 */     gbConstraints.fill = 2;
/* 184:242 */     gbConstraints.gridy = 1;
/* 185:243 */     gbConstraints.gridx = 1;
/* 186:244 */     gbLayout.setConstraints(this.m_fileFormatBox, gbConstraints);
/* 187:245 */     alignedP.add(this.m_fileFormatBox);
/* 188:    */     
/* 189:247 */     JPanel about = this.m_SaverEditor.getAboutPanel();
/* 190:248 */     if (about != null) {
/* 191:249 */       innerPanel.add(about, "North");
/* 192:    */     }
/* 193:251 */     add(innerPanel, "North");
/* 194:    */     
/* 195:253 */     JLabel directoryLab = new JLabel("Directory", 4);
/* 196:254 */     directoryLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 197:255 */     gbConstraints = new GridBagConstraints();
/* 198:256 */     gbConstraints.anchor = 13;
/* 199:257 */     gbConstraints.fill = 2;
/* 200:258 */     gbConstraints.gridy = 2;
/* 201:259 */     gbConstraints.gridx = 0;
/* 202:260 */     gbLayout.setConstraints(directoryLab, gbConstraints);
/* 203:261 */     alignedP.add(directoryLab);
/* 204:    */     
/* 205:263 */     this.m_directoryText = new EnvironmentField();
/* 206:264 */     this.m_directoryText.setEnvironment(this.m_env);
/* 207:    */     
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:272 */     this.m_directoryText.setText(this.m_smSaver.getDirectory().toString());
/* 215:    */     
/* 216:274 */     JButton browseBut = new JButton("Browse...");
/* 217:275 */     browseBut.addActionListener(new ActionListener()
/* 218:    */     {
/* 219:    */       public void actionPerformed(ActionEvent e)
/* 220:    */       {
/* 221:    */         try
/* 222:    */         {
/* 223:280 */           JDialog jf = new JDialog((JDialog)SerializedModelSaverCustomizer.this.getTopLevelAncestor(), "Choose directory", Dialog.ModalityType.DOCUMENT_MODAL);
/* 224:    */           
/* 225:    */ 
/* 226:    */ 
/* 227:284 */           jf.getContentPane().setLayout(new BorderLayout());
/* 228:285 */           jf.getContentPane().add(SerializedModelSaverCustomizer.this.m_fileChooser, "Center");
/* 229:286 */           SerializedModelSaverCustomizer.this.m_fileChooserFrame = jf;
/* 230:287 */           jf.pack();
/* 231:288 */           jf.setVisible(true);
/* 232:    */         }
/* 233:    */         catch (Exception ex)
/* 234:    */         {
/* 235:290 */           ex.printStackTrace();
/* 236:    */         }
/* 237:    */       }
/* 238:294 */     });
/* 239:295 */     JPanel efHolder = new JPanel();
/* 240:296 */     efHolder.setLayout(new BorderLayout());
/* 241:297 */     JPanel bP = new JPanel();
/* 242:298 */     bP.setLayout(new BorderLayout());
/* 243:299 */     bP.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
/* 244:300 */     bP.add(browseBut, "Center");
/* 245:301 */     efHolder.add(bP, "East");
/* 246:302 */     efHolder.add(this.m_directoryText, "Center");
/* 247:303 */     gbConstraints = new GridBagConstraints();
/* 248:304 */     gbConstraints.anchor = 13;
/* 249:305 */     gbConstraints.fill = 2;
/* 250:306 */     gbConstraints.gridy = 2;
/* 251:307 */     gbConstraints.gridx = 1;
/* 252:308 */     gbConstraints.weightx = 5.0D;
/* 253:    */     
/* 254:310 */     gbLayout.setConstraints(efHolder, gbConstraints);
/* 255:311 */     alignedP.add(efHolder);
/* 256:    */     
/* 257:313 */     JLabel saveSchedule = new JLabel("Incremental classifier save schedule", 4);
/* 258:    */     
/* 259:315 */     saveSchedule.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 260:316 */     gbConstraints = new GridBagConstraints();
/* 261:317 */     gbConstraints.anchor = 13;
/* 262:318 */     gbConstraints.fill = 2;
/* 263:319 */     gbConstraints.gridy = 3;
/* 264:320 */     gbConstraints.gridx = 0;
/* 265:321 */     gbLayout.setConstraints(saveSchedule, gbConstraints);
/* 266:322 */     alignedP.add(saveSchedule);
/* 267:323 */     saveSchedule.setToolTipText("How often to save incremental models (<=0 means only at the end of the stream)");
/* 268:    */     
/* 269:    */ 
/* 270:326 */     this.m_incrementalSaveSchedule = new JTextField("" + this.m_smSaver.getIncrementalSaveSchedule(), 5);
/* 271:    */     
/* 272:328 */     gbConstraints = new GridBagConstraints();
/* 273:329 */     gbConstraints.anchor = 13;
/* 274:330 */     gbConstraints.fill = 2;
/* 275:331 */     gbConstraints.gridy = 3;
/* 276:332 */     gbConstraints.gridx = 1;
/* 277:333 */     gbLayout.setConstraints(this.m_incrementalSaveSchedule, gbConstraints);
/* 278:334 */     alignedP.add(this.m_incrementalSaveSchedule);
/* 279:335 */     this.m_incrementalSaveSchedule.setToolTipText("How often to save incremental models (<=0 means only at the end of the stream)");
/* 280:    */     
/* 281:    */ 
/* 282:    */ 
/* 283:339 */     JLabel relativeLab = new JLabel("Use relative file paths", 4);
/* 284:    */     
/* 285:341 */     relativeLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 286:342 */     gbConstraints = new GridBagConstraints();
/* 287:343 */     gbConstraints.anchor = 13;
/* 288:344 */     gbConstraints.fill = 2;
/* 289:345 */     gbConstraints.gridy = 4;
/* 290:346 */     gbConstraints.gridx = 0;
/* 291:347 */     gbLayout.setConstraints(relativeLab, gbConstraints);
/* 292:348 */     alignedP.add(relativeLab);
/* 293:    */     
/* 294:350 */     this.m_relativeFilePath = new JCheckBox();
/* 295:351 */     this.m_relativeFilePath.setSelected(this.m_smSaver.getUseRelativePath());
/* 296:    */     
/* 297:    */ 
/* 298:    */ 
/* 299:    */ 
/* 300:    */ 
/* 301:    */ 
/* 302:    */ 
/* 303:359 */     gbConstraints = new GridBagConstraints();
/* 304:360 */     gbConstraints.anchor = 13;
/* 305:361 */     gbConstraints.fill = 2;
/* 306:362 */     gbConstraints.gridy = 4;
/* 307:363 */     gbConstraints.gridx = 1;
/* 308:364 */     gbLayout.setConstraints(this.m_relativeFilePath, gbConstraints);
/* 309:365 */     alignedP.add(this.m_relativeFilePath);
/* 310:    */     
/* 311:367 */     JLabel relationLab = new JLabel("Include relation name in file name", 4);
/* 312:    */     
/* 313:369 */     relationLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 314:370 */     gbConstraints = new GridBagConstraints();
/* 315:371 */     gbConstraints.anchor = 13;
/* 316:372 */     gbConstraints.fill = 2;
/* 317:373 */     gbConstraints.gridy = 5;
/* 318:374 */     gbConstraints.gridx = 0;
/* 319:375 */     gbLayout.setConstraints(relationLab, gbConstraints);
/* 320:376 */     alignedP.add(relationLab);
/* 321:    */     
/* 322:378 */     this.m_includeRelationName = new JCheckBox();
/* 323:379 */     this.m_includeRelationName.setToolTipText("Include the relation name of the training data used to create the model in the file name.");
/* 324:    */     
/* 325:    */ 
/* 326:382 */     this.m_includeRelationName.setSelected(this.m_smSaver.getIncludeRelationName());
/* 327:    */     
/* 328:384 */     gbConstraints = new GridBagConstraints();
/* 329:385 */     gbConstraints.anchor = 13;
/* 330:386 */     gbConstraints.fill = 2;
/* 331:387 */     gbConstraints.gridy = 5;
/* 332:388 */     gbConstraints.gridx = 1;
/* 333:389 */     gbLayout.setConstraints(this.m_includeRelationName, gbConstraints);
/* 334:390 */     alignedP.add(this.m_includeRelationName);
/* 335:    */     
/* 336:392 */     JButton OKBut = new JButton("OK");
/* 337:393 */     OKBut.addActionListener(new ActionListener()
/* 338:    */     {
/* 339:    */       public void actionPerformed(ActionEvent e)
/* 340:    */       {
/* 341:    */         try
/* 342:    */         {
/* 343:397 */           SerializedModelSaverCustomizer.this.m_smSaver.setPrefix(SerializedModelSaverCustomizer.this.m_prefixText.getText());
/* 344:398 */           SerializedModelSaverCustomizer.this.m_smSaver.setDirectory(new File(SerializedModelSaverCustomizer.this.m_directoryText.getText()));
/* 345:399 */           SerializedModelSaverCustomizer.this.m_smSaver.setIncludeRelationName(SerializedModelSaverCustomizer.this.m_includeRelationName.isSelected());
/* 346:    */           
/* 347:401 */           SerializedModelSaverCustomizer.this.m_smSaver.setUseRelativePath(SerializedModelSaverCustomizer.this.m_relativeFilePath.isSelected());
/* 348:402 */           String schedule = SerializedModelSaverCustomizer.this.m_incrementalSaveSchedule.getText();
/* 349:403 */           if ((schedule != null) && (schedule.length() > 0)) {
/* 350:    */             try
/* 351:    */             {
/* 352:405 */               SerializedModelSaverCustomizer.this.m_smSaver.setIncrementalSaveSchedule(Integer.parseInt(SerializedModelSaverCustomizer.this.m_incrementalSaveSchedule.getText()));
/* 353:    */             }
/* 354:    */             catch (NumberFormatException ex) {}
/* 355:    */           }
/* 356:412 */           Tag selected = (Tag)SerializedModelSaverCustomizer.this.m_fileFormatBox.getSelectedItem();
/* 357:413 */           if (selected != null) {
/* 358:414 */             SerializedModelSaverCustomizer.this.m_smSaver.setFileFormat(selected);
/* 359:    */           }
/* 360:    */         }
/* 361:    */         catch (Exception ex)
/* 362:    */         {
/* 363:417 */           ex.printStackTrace();
/* 364:    */         }
/* 365:420 */         if (SerializedModelSaverCustomizer.this.m_modifyListener != null) {
/* 366:421 */           SerializedModelSaverCustomizer.this.m_modifyListener.setModifiedStatus(SerializedModelSaverCustomizer.this, true);
/* 367:    */         }
/* 368:425 */         SerializedModelSaverCustomizer.this.m_parentWindow.dispose();
/* 369:    */       }
/* 370:428 */     });
/* 371:429 */     JButton CancelBut = new JButton("Cancel");
/* 372:430 */     CancelBut.addActionListener(new ActionListener()
/* 373:    */     {
/* 374:    */       public void actionPerformed(ActionEvent e)
/* 375:    */       {
/* 376:434 */         SerializedModelSaverCustomizer.this.customizerClosing();
/* 377:435 */         SerializedModelSaverCustomizer.this.m_parentWindow.dispose();
/* 378:    */       }
/* 379:438 */     });
/* 380:439 */     JPanel butHolder = new JPanel();
/* 381:440 */     butHolder.setLayout(new FlowLayout());
/* 382:441 */     butHolder.add(OKBut);
/* 383:442 */     butHolder.add(CancelBut);
/* 384:    */     
/* 385:444 */     JPanel holderPanel = new JPanel();
/* 386:445 */     holderPanel.setLayout(new BorderLayout());
/* 387:446 */     holderPanel.add(alignedP, "North");
/* 388:447 */     holderPanel.add(butHolder, "South");
/* 389:448 */     add(holderPanel, "South");
/* 390:    */   }
/* 391:    */   
/* 392:    */   public void setObject(Object object)
/* 393:    */   {
/* 394:458 */     this.m_smSaver = ((SerializedModelSaver)object);
/* 395:459 */     this.m_SaverEditor.setTarget(this.m_smSaver);
/* 396:460 */     this.m_prefixBackup = this.m_smSaver.getPrefix();
/* 397:461 */     this.m_directoryBackup = this.m_smSaver.getDirectory();
/* 398:462 */     this.m_relationBackup = this.m_smSaver.getIncludeRelationName();
/* 399:463 */     this.m_relativeBackup = this.m_smSaver.getUseRelativePath();
/* 400:464 */     this.m_formatBackup = this.m_smSaver.getFileFormat();
/* 401:    */     
/* 402:466 */     setUpFile();
/* 403:    */   }
/* 404:    */   
/* 405:    */   private void setUpFileFormatComboBox()
/* 406:    */   {
/* 407:470 */     this.m_fileFormatBox = new JComboBox();
/* 408:471 */     for (int i = 0; i < SerializedModelSaver.s_fileFormatsAvailable.size(); i++)
/* 409:    */     {
/* 410:472 */       Tag temp = (Tag)SerializedModelSaver.s_fileFormatsAvailable.get(i);
/* 411:473 */       this.m_fileFormatBox.addItem(temp);
/* 412:    */     }
/* 413:476 */     Tag result = this.m_smSaver.validateFileFormat(this.m_smSaver.getFileFormat());
/* 414:477 */     if (result == null) {
/* 415:478 */       this.m_fileFormatBox.setSelectedIndex(0);
/* 416:    */     } else {
/* 417:480 */       this.m_fileFormatBox.setSelectedItem(result);
/* 418:    */     }
/* 419:    */   }
/* 420:    */   
/* 421:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 422:    */   {
/* 423:498 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 424:    */   }
/* 425:    */   
/* 426:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 427:    */   {
/* 428:508 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 429:    */   }
/* 430:    */   
/* 431:    */   public void setEnvironment(Environment env)
/* 432:    */   {
/* 433:518 */     this.m_env = env;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 437:    */   {
/* 438:523 */     this.m_modifyListener = l;
/* 439:    */   }
/* 440:    */   
/* 441:    */   public void customizerClosing()
/* 442:    */   {
/* 443:530 */     this.m_smSaver.setPrefix(this.m_prefixBackup);
/* 444:531 */     this.m_smSaver.setDirectory(this.m_directoryBackup);
/* 445:532 */     this.m_smSaver.setUseRelativePath(this.m_relativeBackup);
/* 446:533 */     this.m_smSaver.setIncludeRelationName(this.m_relationBackup);
/* 447:534 */     this.m_smSaver.setFileFormat(this.m_formatBackup);
/* 448:    */   }
/* 449:    */   
/* 450:    */   static {}
/* 451:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SerializedModelSaverCustomizer
 * JD-Core Version:    0.7.0.1
 */