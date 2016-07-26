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
/*  15:    */ import java.io.IOException;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JCheckBox;
/*  20:    */ import javax.swing.JDialog;
/*  21:    */ import javax.swing.JFileChooser;
/*  22:    */ import javax.swing.JLabel;
/*  23:    */ import javax.swing.JPanel;
/*  24:    */ import javax.swing.JPasswordField;
/*  25:    */ import javax.swing.JScrollPane;
/*  26:    */ import javax.swing.filechooser.FileFilter;
/*  27:    */ import weka.core.Environment;
/*  28:    */ import weka.core.EnvironmentHandler;
/*  29:    */ import weka.core.converters.DatabaseConverter;
/*  30:    */ import weka.core.converters.DatabaseSaver;
/*  31:    */ import weka.core.converters.FileSourcedConverter;
/*  32:    */ import weka.gui.ExtensionFileFilter;
/*  33:    */ import weka.gui.GenericObjectEditor;
/*  34:    */ import weka.gui.PropertySheetPanel;
/*  35:    */ 
/*  36:    */ public class SaverCustomizer
/*  37:    */   extends JPanel
/*  38:    */   implements BeanCustomizer, CustomizerCloseRequester, EnvironmentHandler
/*  39:    */ {
/*  40:    */   private static final long serialVersionUID = -4874208115942078471L;
/*  41: 75 */   private PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  42:    */   private Saver m_dsSaver;
/*  43: 80 */   private PropertySheetPanel m_SaverEditor = new PropertySheetPanel();
/*  44: 83 */   private JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  45:    */   private Window m_parentWindow;
/*  46:    */   private JDialog m_fileChooserFrame;
/*  47:    */   private EnvironmentField m_dbaseURLText;
/*  48:    */   private EnvironmentField m_userNameText;
/*  49:    */   private JPasswordField m_passwordText;
/*  50:    */   private EnvironmentField m_tableText;
/*  51:    */   private JCheckBox m_truncateBox;
/*  52:    */   private JCheckBox m_idBox;
/*  53:    */   private JCheckBox m_tabBox;
/*  54:    */   private EnvironmentField m_prefixText;
/*  55:    */   private JCheckBox m_relativeFilePath;
/*  56:    */   private JCheckBox m_relationNameForFilename;
/*  57:111 */   private Environment m_env = Environment.getSystemWide();
/*  58:    */   private EnvironmentField m_directoryText;
/*  59:    */   private FileEnvironmentField m_dbProps;
/*  60:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  61:    */   
/*  62:    */   public SaverCustomizer()
/*  63:    */   {
/*  64:123 */     setLayout(new BorderLayout());
/*  65:124 */     this.m_fileChooser.setDialogType(1);
/*  66:125 */     this.m_fileChooser.setFileSelectionMode(1);
/*  67:126 */     this.m_fileChooser.setApproveButtonText("Select directory");
/*  68:127 */     this.m_fileChooser.addActionListener(new ActionListener()
/*  69:    */     {
/*  70:    */       public void actionPerformed(ActionEvent e)
/*  71:    */       {
/*  72:129 */         if (e.getActionCommand().equals("ApproveSelection")) {
/*  73:    */           try
/*  74:    */           {
/*  75:131 */             File selectedFile = SaverCustomizer.this.m_fileChooser.getSelectedFile();
/*  76:132 */             SaverCustomizer.this.m_directoryText.setText(selectedFile.toString());
/*  77:    */           }
/*  78:    */           catch (Exception ex)
/*  79:    */           {
/*  80:140 */             ex.printStackTrace();
/*  81:    */           }
/*  82:    */         }
/*  83:144 */         if (SaverCustomizer.this.m_fileChooserFrame != null) {
/*  84:145 */           SaverCustomizer.this.m_fileChooserFrame.dispose();
/*  85:    */         }
/*  86:    */       }
/*  87:    */     });
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setParentWindow(Window parent)
/*  91:    */   {
/*  92:152 */     this.m_parentWindow = parent;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private void setUpOther()
/*  96:    */   {
/*  97:159 */     removeAll();
/*  98:160 */     add(this.m_SaverEditor, "Center");
/*  99:    */     
/* 100:162 */     JPanel buttonsP = new JPanel();
/* 101:163 */     buttonsP.setLayout(new FlowLayout());
/* 102:    */     JButton ok;
/* 103:165 */     buttonsP.add(ok = new JButton("OK"));
/* 104:    */     JButton cancel;
/* 105:166 */     buttonsP.add(cancel = new JButton("Cancel"));
/* 106:167 */     ok.addActionListener(new ActionListener()
/* 107:    */     {
/* 108:    */       public void actionPerformed(ActionEvent evt)
/* 109:    */       {
/* 110:173 */         SaverCustomizer.this.m_SaverEditor.closingOK();
/* 111:175 */         if (SaverCustomizer.this.m_parentWindow != null) {
/* 112:176 */           SaverCustomizer.this.m_parentWindow.dispose();
/* 113:    */         }
/* 114:    */       }
/* 115:179 */     });
/* 116:180 */     cancel.addActionListener(new ActionListener()
/* 117:    */     {
/* 118:    */       public void actionPerformed(ActionEvent evt)
/* 119:    */       {
/* 120:186 */         SaverCustomizer.this.m_SaverEditor.closingCancel();
/* 121:188 */         if (SaverCustomizer.this.m_parentWindow != null) {
/* 122:189 */           SaverCustomizer.this.m_parentWindow.dispose();
/* 123:    */         }
/* 124:    */       }
/* 125:193 */     });
/* 126:194 */     add(buttonsP, "South");
/* 127:    */     
/* 128:196 */     validate();
/* 129:197 */     repaint();
/* 130:    */   }
/* 131:    */   
/* 132:    */   private void setUpDatabase()
/* 133:    */   {
/* 134:203 */     removeAll();
/* 135:204 */     JPanel db = new JPanel();
/* 136:205 */     GridBagLayout gbLayout = new GridBagLayout();
/* 137:206 */     db.setLayout(gbLayout);
/* 138:    */     
/* 139:208 */     JLabel dbaseURLLab = new JLabel(" Database URL", 4);
/* 140:209 */     dbaseURLLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 141:210 */     GridBagConstraints gbConstraints = new GridBagConstraints();
/* 142:211 */     gbConstraints.anchor = 13;
/* 143:212 */     gbConstraints.fill = 2;
/* 144:213 */     gbConstraints.gridy = 0;gbConstraints.gridx = 0;
/* 145:214 */     gbLayout.setConstraints(dbaseURLLab, gbConstraints);
/* 146:215 */     db.add(dbaseURLLab);
/* 147:    */     
/* 148:217 */     this.m_dbaseURLText = new EnvironmentField();
/* 149:218 */     this.m_dbaseURLText.setEnvironment(this.m_env);
/* 150:    */     
/* 151:    */ 
/* 152:    */ 
/* 153:    */ 
/* 154:223 */     this.m_dbaseURLText.setText(((DatabaseConverter)this.m_dsSaver.getSaverTemplate()).getUrl());
/* 155:224 */     gbConstraints = new GridBagConstraints();
/* 156:225 */     gbConstraints.anchor = 13;
/* 157:226 */     gbConstraints.fill = 2;
/* 158:227 */     gbConstraints.gridy = 0;gbConstraints.gridx = 1;
/* 159:228 */     gbConstraints.weightx = 5.0D;
/* 160:229 */     gbLayout.setConstraints(this.m_dbaseURLText, gbConstraints);
/* 161:230 */     db.add(this.m_dbaseURLText);
/* 162:    */     
/* 163:232 */     JLabel userLab = new JLabel("Username", 4);
/* 164:233 */     userLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 165:234 */     gbConstraints = new GridBagConstraints();
/* 166:235 */     gbConstraints.anchor = 13;
/* 167:236 */     gbConstraints.fill = 2;
/* 168:237 */     gbConstraints.gridy = 1;gbConstraints.gridx = 0;
/* 169:238 */     gbLayout.setConstraints(userLab, gbConstraints);
/* 170:239 */     db.add(userLab);
/* 171:    */     
/* 172:241 */     this.m_userNameText = new EnvironmentField();
/* 173:242 */     this.m_userNameText.setEnvironment(this.m_env);
/* 174:    */     
/* 175:    */ 
/* 176:245 */     this.m_userNameText.setText(((DatabaseConverter)this.m_dsSaver.getSaverTemplate()).getUser());
/* 177:246 */     gbConstraints = new GridBagConstraints();
/* 178:247 */     gbConstraints.anchor = 13;
/* 179:248 */     gbConstraints.fill = 2;
/* 180:249 */     gbConstraints.gridy = 1;gbConstraints.gridx = 1;
/* 181:250 */     gbLayout.setConstraints(this.m_userNameText, gbConstraints);
/* 182:251 */     db.add(this.m_userNameText);
/* 183:    */     
/* 184:253 */     JLabel passwordLab = new JLabel("Password ", 4);
/* 185:254 */     passwordLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 186:255 */     gbConstraints = new GridBagConstraints();
/* 187:256 */     gbConstraints.anchor = 13;
/* 188:257 */     gbConstraints.fill = 2;
/* 189:258 */     gbConstraints.gridy = 2;gbConstraints.gridx = 0;
/* 190:259 */     gbLayout.setConstraints(passwordLab, gbConstraints);
/* 191:260 */     db.add(passwordLab);
/* 192:    */     
/* 193:262 */     this.m_passwordText = new JPasswordField();
/* 194:263 */     this.m_passwordText.setText(((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getPassword());
/* 195:264 */     JPanel passwordHolder = new JPanel();
/* 196:265 */     passwordHolder.setLayout(new BorderLayout());
/* 197:266 */     passwordHolder.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 198:267 */     passwordHolder.add(this.m_passwordText, "Center");
/* 199:    */     
/* 200:    */ 
/* 201:270 */     gbConstraints = new GridBagConstraints();
/* 202:271 */     gbConstraints.anchor = 13;
/* 203:272 */     gbConstraints.fill = 2;
/* 204:273 */     gbConstraints.gridy = 2;gbConstraints.gridx = 1;
/* 205:274 */     gbLayout.setConstraints(passwordHolder, gbConstraints);
/* 206:275 */     db.add(passwordHolder);
/* 207:    */     
/* 208:277 */     JLabel tableLab = new JLabel("Table Name", 4);
/* 209:278 */     tableLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 210:279 */     gbConstraints = new GridBagConstraints();
/* 211:280 */     gbConstraints.anchor = 13;
/* 212:281 */     gbConstraints.fill = 2;
/* 213:282 */     gbConstraints.gridy = 3;gbConstraints.gridx = 0;
/* 214:283 */     gbLayout.setConstraints(tableLab, gbConstraints);
/* 215:284 */     db.add(tableLab);
/* 216:    */     
/* 217:286 */     this.m_tableText = new EnvironmentField();
/* 218:287 */     this.m_tableText.setEnvironment(this.m_env);
/* 219:    */     
/* 220:    */ 
/* 221:290 */     this.m_tableText.setEnabled(!((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getRelationForTableName());
/* 222:291 */     this.m_tableText.setText(((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getTableName());
/* 223:292 */     gbConstraints = new GridBagConstraints();
/* 224:293 */     gbConstraints.anchor = 13;
/* 225:294 */     gbConstraints.fill = 2;
/* 226:295 */     gbConstraints.gridy = 3;gbConstraints.gridx = 1;
/* 227:296 */     gbLayout.setConstraints(this.m_tableText, gbConstraints);
/* 228:297 */     db.add(this.m_tableText);
/* 229:    */     
/* 230:299 */     JLabel tabLab = new JLabel("Use relation name", 4);
/* 231:300 */     tabLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 232:301 */     gbConstraints = new GridBagConstraints();
/* 233:302 */     gbConstraints.anchor = 13;
/* 234:303 */     gbConstraints.fill = 2;
/* 235:304 */     gbConstraints.gridy = 4;gbConstraints.gridx = 0;
/* 236:305 */     gbLayout.setConstraints(tabLab, gbConstraints);
/* 237:306 */     db.add(tabLab);
/* 238:    */     
/* 239:308 */     this.m_tabBox = new JCheckBox();
/* 240:309 */     this.m_tabBox.setSelected(((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getRelationForTableName());
/* 241:310 */     this.m_tabBox.addActionListener(new ActionListener()
/* 242:    */     {
/* 243:    */       public void actionPerformed(ActionEvent e)
/* 244:    */       {
/* 245:312 */         SaverCustomizer.this.m_tableText.setEnabled(!SaverCustomizer.this.m_tabBox.isSelected());
/* 246:    */       }
/* 247:314 */     });
/* 248:315 */     gbConstraints = new GridBagConstraints();
/* 249:316 */     gbConstraints.anchor = 13;
/* 250:317 */     gbConstraints.fill = 2;
/* 251:318 */     gbConstraints.gridy = 4;gbConstraints.gridx = 1;
/* 252:319 */     gbLayout.setConstraints(this.m_tabBox, gbConstraints);
/* 253:320 */     db.add(this.m_tabBox);
/* 254:    */     
/* 255:322 */     JLabel truncLab = new JLabel("Truncate table", 4);
/* 256:323 */     truncLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 257:324 */     gbConstraints = new GridBagConstraints();
/* 258:325 */     gbConstraints.anchor = 13;
/* 259:326 */     gbConstraints.fill = 2;
/* 260:327 */     gbConstraints.gridy = 5;gbConstraints.gridx = 0;
/* 261:328 */     gbLayout.setConstraints(truncLab, gbConstraints);
/* 262:329 */     db.add(truncLab);
/* 263:    */     
/* 264:331 */     this.m_truncateBox = new JCheckBox();
/* 265:332 */     this.m_truncateBox.setSelected(((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getTruncate());
/* 266:333 */     gbConstraints = new GridBagConstraints();
/* 267:334 */     gbConstraints.anchor = 13;
/* 268:335 */     gbConstraints.fill = 2;
/* 269:336 */     gbConstraints.gridy = 5;gbConstraints.gridx = 1;
/* 270:337 */     gbLayout.setConstraints(this.m_truncateBox, gbConstraints);
/* 271:338 */     db.add(this.m_truncateBox);
/* 272:    */     
/* 273:340 */     JLabel idLab = new JLabel("Automatic primary key", 4);
/* 274:341 */     idLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 275:342 */     gbConstraints = new GridBagConstraints();
/* 276:343 */     gbConstraints.anchor = 13;
/* 277:344 */     gbConstraints.fill = 2;
/* 278:345 */     gbConstraints.gridy = 6;gbConstraints.gridx = 0;
/* 279:346 */     gbLayout.setConstraints(idLab, gbConstraints);
/* 280:347 */     db.add(idLab);
/* 281:    */     
/* 282:349 */     this.m_idBox = new JCheckBox();
/* 283:350 */     this.m_idBox.setSelected(((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getAutoKeyGeneration());
/* 284:351 */     gbConstraints = new GridBagConstraints();
/* 285:352 */     gbConstraints.anchor = 13;
/* 286:353 */     gbConstraints.fill = 2;
/* 287:354 */     gbConstraints.gridy = 6;gbConstraints.gridx = 1;
/* 288:355 */     gbLayout.setConstraints(this.m_idBox, gbConstraints);
/* 289:356 */     db.add(this.m_idBox);
/* 290:    */     
/* 291:358 */     JLabel propsLab = new JLabel("DB config props", 4);
/* 292:359 */     propsLab.setToolTipText("The custom properties that the user can use to override the default ones.");
/* 293:360 */     propsLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 294:361 */     gbConstraints = new GridBagConstraints();
/* 295:362 */     gbConstraints.anchor = 13;
/* 296:363 */     gbConstraints.fill = 2;
/* 297:364 */     gbConstraints.gridy = 7;gbConstraints.gridx = 0;
/* 298:365 */     gbLayout.setConstraints(propsLab, gbConstraints);
/* 299:366 */     db.add(propsLab);
/* 300:    */     
/* 301:368 */     this.m_dbProps = new FileEnvironmentField();
/* 302:369 */     this.m_dbProps.setEnvironment(this.m_env);
/* 303:370 */     this.m_dbProps.resetFileFilters();
/* 304:371 */     this.m_dbProps.addFileFilter(new ExtensionFileFilter(".props", "DatabaseUtils property file (*.props)"));
/* 305:    */     
/* 306:373 */     gbConstraints = new GridBagConstraints();
/* 307:374 */     gbConstraints.anchor = 13;
/* 308:375 */     gbConstraints.fill = 2;
/* 309:376 */     gbConstraints.gridy = 7;gbConstraints.gridx = 1;
/* 310:377 */     gbLayout.setConstraints(this.m_dbProps, gbConstraints);
/* 311:378 */     db.add(this.m_dbProps);
/* 312:379 */     File toSet = ((DatabaseSaver)this.m_dsSaver.getSaverTemplate()).getCustomPropsFile();
/* 313:380 */     if (toSet != null) {
/* 314:381 */       this.m_dbProps.setText(toSet.getPath());
/* 315:    */     }
/* 316:383 */     JButton loadPropsBut = new JButton("Load");
/* 317:384 */     loadPropsBut.setToolTipText("Load config");
/* 318:385 */     gbConstraints = new GridBagConstraints();
/* 319:386 */     gbConstraints.anchor = 13;
/* 320:387 */     gbConstraints.fill = 2;
/* 321:388 */     gbConstraints.gridy = 7;gbConstraints.gridx = 2;
/* 322:389 */     gbLayout.setConstraints(loadPropsBut, gbConstraints);
/* 323:390 */     db.add(loadPropsBut);
/* 324:391 */     loadPropsBut.addActionListener(new ActionListener()
/* 325:    */     {
/* 326:    */       public void actionPerformed(ActionEvent e)
/* 327:    */       {
/* 328:393 */         if ((SaverCustomizer.this.m_dbProps.getText() != null) && (SaverCustomizer.this.m_dbProps.getText().length() > 0))
/* 329:    */         {
/* 330:395 */           String propsS = SaverCustomizer.this.m_dbProps.getText();
/* 331:    */           try
/* 332:    */           {
/* 333:397 */             propsS = SaverCustomizer.this.m_env.substitute(propsS);
/* 334:    */           }
/* 335:    */           catch (Exception ex) {}
/* 336:399 */           File propsFile = new File(propsS);
/* 337:400 */           if (propsFile.exists())
/* 338:    */           {
/* 339:401 */             ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setCustomPropsFile(propsFile);
/* 340:402 */             ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).resetOptions();
/* 341:403 */             SaverCustomizer.this.m_dbaseURLText.setText(((DatabaseConverter)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).getUrl());
/* 342:    */           }
/* 343:    */         }
/* 344:    */       }
/* 345:408 */     });
/* 346:409 */     JPanel buttonsP = new JPanel();
/* 347:410 */     buttonsP.setLayout(new FlowLayout());
/* 348:    */     JButton ok;
/* 349:412 */     buttonsP.add(ok = new JButton("OK"));
/* 350:    */     JButton cancel;
/* 351:413 */     buttonsP.add(cancel = new JButton("Cancel"));
/* 352:414 */     ok.addActionListener(new ActionListener()
/* 353:    */     {
/* 354:    */       public void actionPerformed(ActionEvent evt)
/* 355:    */       {
/* 356:416 */         if (SaverCustomizer.this.m_dbProps.getText().length() > 0) {
/* 357:417 */           ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setCustomPropsFile(new File(SaverCustomizer.this.m_dbProps.getText()));
/* 358:    */         }
/* 359:420 */         ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).resetStructure();
/* 360:421 */         ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).resetOptions();
/* 361:422 */         ((DatabaseConverter)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setUrl(SaverCustomizer.this.m_dbaseURLText.getText());
/* 362:423 */         ((DatabaseConverter)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setUser(SaverCustomizer.this.m_userNameText.getText());
/* 363:424 */         ((DatabaseConverter)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setPassword(new String(SaverCustomizer.this.m_passwordText.getPassword()));
/* 364:425 */         if (!SaverCustomizer.this.m_tabBox.isSelected()) {
/* 365:426 */           ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setTableName(SaverCustomizer.this.m_tableText.getText());
/* 366:    */         }
/* 367:428 */         ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setTruncate(SaverCustomizer.this.m_truncateBox.isSelected());
/* 368:429 */         ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setAutoKeyGeneration(SaverCustomizer.this.m_idBox.isSelected());
/* 369:430 */         ((DatabaseSaver)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setRelationForTableName(SaverCustomizer.this.m_tabBox.isSelected());
/* 370:432 */         if (SaverCustomizer.this.m_modifyListener != null) {
/* 371:433 */           SaverCustomizer.this.m_modifyListener.setModifiedStatus(SaverCustomizer.this, true);
/* 372:    */         }
/* 373:436 */         if (SaverCustomizer.this.m_parentWindow != null) {
/* 374:437 */           SaverCustomizer.this.m_parentWindow.dispose();
/* 375:    */         }
/* 376:    */       }
/* 377:440 */     });
/* 378:441 */     cancel.addActionListener(new ActionListener()
/* 379:    */     {
/* 380:    */       public void actionPerformed(ActionEvent evt)
/* 381:    */       {
/* 382:443 */         if (SaverCustomizer.this.m_modifyListener != null) {
/* 383:444 */           SaverCustomizer.this.m_modifyListener.setModifiedStatus(SaverCustomizer.this, false);
/* 384:    */         }
/* 385:447 */         if (SaverCustomizer.this.m_parentWindow != null) {
/* 386:448 */           SaverCustomizer.this.m_parentWindow.dispose();
/* 387:    */         }
/* 388:    */       }
/* 389:452 */     });
/* 390:453 */     JPanel holderP = new JPanel();
/* 391:454 */     holderP.setLayout(new BorderLayout());
/* 392:455 */     holderP.add(db, "North");
/* 393:456 */     holderP.add(buttonsP, "South");
/* 394:    */     
/* 395:    */ 
/* 396:459 */     JPanel about = this.m_SaverEditor.getAboutPanel();
/* 397:460 */     if (about != null) {
/* 398:461 */       add(about, "North");
/* 399:    */     }
/* 400:463 */     add(holderP, "South");
/* 401:    */   }
/* 402:    */   
/* 403:    */   public void setUpFile()
/* 404:    */   {
/* 405:468 */     removeAll();
/* 406:    */     
/* 407:470 */     this.m_fileChooser.setFileFilter(new FileFilter()
/* 408:    */     {
/* 409:    */       public boolean accept(File f)
/* 410:    */       {
/* 411:472 */         return f.isDirectory();
/* 412:    */       }
/* 413:    */       
/* 414:    */       public String getDescription()
/* 415:    */       {
/* 416:475 */         return "Directory";
/* 417:    */       }
/* 418:478 */     });
/* 419:479 */     this.m_fileChooser.setAcceptAllFileFilterUsed(false);
/* 420:    */     try
/* 421:    */     {
/* 422:482 */       if (!this.m_dsSaver.getSaverTemplate().retrieveDir().equals(""))
/* 423:    */       {
/* 424:483 */         String dirStr = this.m_dsSaver.getSaverTemplate().retrieveDir();
/* 425:484 */         if (Environment.containsEnvVariables(dirStr)) {
/* 426:    */           try
/* 427:    */           {
/* 428:486 */             dirStr = this.m_env.substitute(dirStr);
/* 429:    */           }
/* 430:    */           catch (Exception ex) {}
/* 431:    */         }
/* 432:491 */         File tmp = new File(dirStr);
/* 433:492 */         tmp = new File(tmp.getAbsolutePath());
/* 434:493 */         this.m_fileChooser.setCurrentDirectory(tmp);
/* 435:    */       }
/* 436:    */     }
/* 437:    */     catch (Exception ex)
/* 438:    */     {
/* 439:496 */       System.out.println(ex);
/* 440:    */     }
/* 441:499 */     JPanel innerPanel = new JPanel();
/* 442:500 */     innerPanel.setLayout(new BorderLayout());
/* 443:    */     
/* 444:502 */     JPanel alignedP = new JPanel();
/* 445:503 */     GridBagLayout gbLayout = new GridBagLayout();
/* 446:504 */     alignedP.setLayout(gbLayout);
/* 447:    */     
/* 448:506 */     final JLabel prefixLab = new JLabel("Prefix for file name", 4);
/* 449:507 */     prefixLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 450:508 */     GridBagConstraints gbConstraints = new GridBagConstraints();
/* 451:509 */     gbConstraints.anchor = 13;
/* 452:510 */     gbConstraints.fill = 2;
/* 453:511 */     gbConstraints.gridy = 0;gbConstraints.gridx = 0;
/* 454:512 */     gbLayout.setConstraints(prefixLab, gbConstraints);
/* 455:513 */     alignedP.add(prefixLab);
/* 456:    */     
/* 457:515 */     this.m_prefixText = new EnvironmentField();
/* 458:516 */     this.m_prefixText.setEnvironment(this.m_env);
/* 459:517 */     this.m_prefixText.setToolTipText("Prefix for file name (or filename itself if relation name is not used)");
/* 460:    */     
/* 461:    */ 
/* 462:    */ 
/* 463:    */ 
/* 464:    */ 
/* 465:523 */     gbConstraints = new GridBagConstraints();
/* 466:524 */     gbConstraints.anchor = 13;
/* 467:525 */     gbConstraints.fill = 2;
/* 468:526 */     gbConstraints.gridy = 0;gbConstraints.gridx = 1;
/* 469:527 */     gbLayout.setConstraints(this.m_prefixText, gbConstraints);
/* 470:528 */     alignedP.add(this.m_prefixText);
/* 471:    */     try
/* 472:    */     {
/* 473:533 */       this.m_prefixText.setText(this.m_dsSaver.getSaverTemplate().filePrefix());
/* 474:    */       
/* 475:    */ 
/* 476:    */ 
/* 477:    */ 
/* 478:538 */       JLabel relationLab = new JLabel("Relation name for filename", 4);
/* 479:539 */       relationLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 480:540 */       gbConstraints = new GridBagConstraints();
/* 481:541 */       gbConstraints.anchor = 13;
/* 482:542 */       gbConstraints.fill = 2;
/* 483:543 */       gbConstraints.gridy = 1;gbConstraints.gridx = 0;
/* 484:544 */       gbLayout.setConstraints(relationLab, gbConstraints);
/* 485:545 */       alignedP.add(relationLab);
/* 486:    */       
/* 487:547 */       this.m_relationNameForFilename = new JCheckBox();
/* 488:548 */       this.m_relationNameForFilename.setSelected(this.m_dsSaver.getRelationNameForFilename());
/* 489:549 */       if (this.m_dsSaver.getRelationNameForFilename()) {
/* 490:550 */         prefixLab.setText("Prefix for file name");
/* 491:    */       } else {
/* 492:552 */         prefixLab.setText("File name");
/* 493:    */       }
/* 494:554 */       this.m_relationNameForFilename.addActionListener(new ActionListener()
/* 495:    */       {
/* 496:    */         public void actionPerformed(ActionEvent e)
/* 497:    */         {
/* 498:556 */           if (SaverCustomizer.this.m_relationNameForFilename.isSelected()) {
/* 499:557 */             prefixLab.setText("Prefix for file name");
/* 500:    */           } else {
/* 501:560 */             prefixLab.setText("File name");
/* 502:    */           }
/* 503:    */         }
/* 504:565 */       });
/* 505:566 */       gbConstraints = new GridBagConstraints();
/* 506:567 */       gbConstraints.anchor = 13;
/* 507:568 */       gbConstraints.fill = 2;
/* 508:569 */       gbConstraints.gridy = 1;gbConstraints.gridx = 1;
/* 509:570 */       gbConstraints.weightx = 5.0D;
/* 510:571 */       gbLayout.setConstraints(this.m_relationNameForFilename, gbConstraints);
/* 511:572 */       alignedP.add(this.m_relationNameForFilename);
/* 512:    */     }
/* 513:    */     catch (Exception ex) {}
/* 514:576 */     JPanel about = this.m_SaverEditor.getAboutPanel();
/* 515:577 */     if (about != null) {
/* 516:578 */       innerPanel.add(about, "North");
/* 517:    */     }
/* 518:580 */     add(innerPanel, "North");
/* 519:    */     
/* 520:    */ 
/* 521:583 */     JLabel directoryLab = new JLabel("Directory", 4);
/* 522:584 */     directoryLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 523:585 */     gbConstraints = new GridBagConstraints();
/* 524:586 */     gbConstraints.anchor = 13;
/* 525:587 */     gbConstraints.fill = 2;
/* 526:588 */     gbConstraints.gridy = 2;gbConstraints.gridx = 0;
/* 527:589 */     gbLayout.setConstraints(directoryLab, gbConstraints);
/* 528:590 */     alignedP.add(directoryLab);
/* 529:    */     
/* 530:592 */     this.m_directoryText = new EnvironmentField();
/* 531:    */     
/* 532:594 */     this.m_directoryText.setEnvironment(this.m_env);
/* 533:    */     try
/* 534:    */     {
/* 535:601 */       this.m_directoryText.setText(this.m_dsSaver.getSaverTemplate().retrieveDir());
/* 536:    */     }
/* 537:    */     catch (IOException ex) {}
/* 538:606 */     JButton browseBut = new JButton("Browse...");
/* 539:607 */     browseBut.addActionListener(new ActionListener()
/* 540:    */     {
/* 541:    */       public void actionPerformed(ActionEvent e)
/* 542:    */       {
/* 543:    */         try
/* 544:    */         {
/* 545:611 */           JDialog jf = new JDialog((JDialog)SaverCustomizer.this.getTopLevelAncestor(), "Choose directory", Dialog.ModalityType.DOCUMENT_MODAL);
/* 546:    */           
/* 547:613 */           jf.setLayout(new BorderLayout());
/* 548:614 */           jf.getContentPane().add(SaverCustomizer.this.m_fileChooser, "Center");
/* 549:615 */           SaverCustomizer.this.m_fileChooserFrame = jf;
/* 550:616 */           jf.pack();
/* 551:617 */           jf.setVisible(true);
/* 552:    */         }
/* 553:    */         catch (Exception ex)
/* 554:    */         {
/* 555:619 */           ex.printStackTrace();
/* 556:    */         }
/* 557:    */       }
/* 558:623 */     });
/* 559:624 */     JPanel efHolder = new JPanel();
/* 560:625 */     efHolder.setLayout(new BorderLayout());
/* 561:626 */     JPanel bP = new JPanel();bP.setLayout(new BorderLayout());
/* 562:627 */     bP.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
/* 563:628 */     bP.add(browseBut, "Center");
/* 564:629 */     efHolder.add(this.m_directoryText, "Center");
/* 565:630 */     efHolder.add(bP, "East");
/* 566:    */     
/* 567:632 */     gbConstraints = new GridBagConstraints();
/* 568:633 */     gbConstraints.anchor = 13;
/* 569:634 */     gbConstraints.fill = 2;
/* 570:635 */     gbConstraints.gridy = 2;gbConstraints.gridx = 1;
/* 571:636 */     gbLayout.setConstraints(efHolder, gbConstraints);
/* 572:637 */     alignedP.add(efHolder);
/* 573:    */     
/* 574:    */ 
/* 575:640 */     JLabel relativeLab = new JLabel("Use relative file paths", 4);
/* 576:641 */     relativeLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 577:642 */     gbConstraints = new GridBagConstraints();
/* 578:643 */     gbConstraints.anchor = 13;
/* 579:644 */     gbConstraints.fill = 2;
/* 580:645 */     gbConstraints.gridy = 3;gbConstraints.gridx = 0;
/* 581:646 */     gbLayout.setConstraints(relativeLab, gbConstraints);
/* 582:647 */     alignedP.add(relativeLab);
/* 583:    */     
/* 584:649 */     this.m_relativeFilePath = new JCheckBox();
/* 585:650 */     this.m_relativeFilePath.setSelected(((FileSourcedConverter)this.m_dsSaver.getSaverTemplate()).getUseRelativePath());
/* 586:    */     
/* 587:    */ 
/* 588:653 */     this.m_relativeFilePath.addActionListener(new ActionListener()
/* 589:    */     {
/* 590:    */       public void actionPerformed(ActionEvent e)
/* 591:    */       {
/* 592:655 */         ((FileSourcedConverter)SaverCustomizer.this.m_dsSaver.getSaverTemplate()).setUseRelativePath(SaverCustomizer.this.m_relativeFilePath.isSelected());
/* 593:    */       }
/* 594:658 */     });
/* 595:659 */     gbConstraints = new GridBagConstraints();
/* 596:660 */     gbConstraints.anchor = 13;
/* 597:661 */     gbConstraints.fill = 2;
/* 598:662 */     gbConstraints.gridy = 3;gbConstraints.gridx = 1;
/* 599:663 */     gbLayout.setConstraints(this.m_relativeFilePath, gbConstraints);
/* 600:664 */     alignedP.add(this.m_relativeFilePath);
/* 601:    */     
/* 602:666 */     JButton OKBut = new JButton("OK");
/* 603:667 */     OKBut.addActionListener(new ActionListener()
/* 604:    */     {
/* 605:    */       public void actionPerformed(ActionEvent e)
/* 606:    */       {
/* 607:    */         try
/* 608:    */         {
/* 609:670 */           SaverCustomizer.this.m_dsSaver.getSaverTemplate().setFilePrefix(SaverCustomizer.this.m_prefixText.getText());
/* 610:671 */           SaverCustomizer.this.m_dsSaver.getSaverTemplate().setDir(SaverCustomizer.this.m_directoryText.getText());
/* 611:672 */           SaverCustomizer.this.m_dsSaver.setRelationNameForFilename(SaverCustomizer.this.m_relationNameForFilename.isSelected());
/* 612:    */         }
/* 613:    */         catch (Exception ex)
/* 614:    */         {
/* 615:675 */           ex.printStackTrace();
/* 616:    */         }
/* 617:678 */         if (SaverCustomizer.this.m_modifyListener != null) {
/* 618:679 */           SaverCustomizer.this.m_modifyListener.setModifiedStatus(SaverCustomizer.this, true);
/* 619:    */         }
/* 620:682 */         SaverCustomizer.this.m_parentWindow.dispose();
/* 621:    */       }
/* 622:685 */     });
/* 623:686 */     JButton CancelBut = new JButton("Cancel");
/* 624:687 */     CancelBut.addActionListener(new ActionListener()
/* 625:    */     {
/* 626:    */       public void actionPerformed(ActionEvent e)
/* 627:    */       {
/* 628:689 */         if (SaverCustomizer.this.m_modifyListener != null) {
/* 629:690 */           SaverCustomizer.this.m_modifyListener.setModifiedStatus(SaverCustomizer.this, false);
/* 630:    */         }
/* 631:693 */         SaverCustomizer.this.m_parentWindow.dispose();
/* 632:    */       }
/* 633:696 */     });
/* 634:697 */     JPanel butHolder = new JPanel();
/* 635:698 */     butHolder.setLayout(new FlowLayout());
/* 636:699 */     butHolder.add(OKBut);
/* 637:700 */     butHolder.add(CancelBut);
/* 638:701 */     JPanel holder2 = new JPanel();
/* 639:702 */     holder2.setLayout(new BorderLayout());
/* 640:703 */     holder2.add(alignedP, "North");
/* 641:    */     
/* 642:705 */     JPanel optionsHolder = new JPanel();
/* 643:706 */     optionsHolder.setLayout(new BorderLayout());
/* 644:707 */     optionsHolder.setBorder(BorderFactory.createTitledBorder("Other options"));
/* 645:    */     
/* 646:709 */     optionsHolder.add(this.m_SaverEditor, "South");
/* 647:710 */     JScrollPane scroller = new JScrollPane(optionsHolder);
/* 648:    */     
/* 649:    */ 
/* 650:    */ 
/* 651:714 */     innerPanel.add(holder2, "South");
/* 652:715 */     add(scroller, "Center");
/* 653:716 */     add(butHolder, "South");
/* 654:    */   }
/* 655:    */   
/* 656:    */   public void setObject(Object object)
/* 657:    */   {
/* 658:725 */     this.m_dsSaver = ((Saver)object);
/* 659:726 */     this.m_SaverEditor.setTarget(this.m_dsSaver.getSaverTemplate());
/* 660:727 */     if ((this.m_dsSaver.getSaverTemplate() instanceof DatabaseConverter)) {
/* 661:728 */       setUpDatabase();
/* 662:731 */     } else if ((this.m_dsSaver.getSaverTemplate() instanceof FileSourcedConverter)) {
/* 663:732 */       setUpFile();
/* 664:    */     } else {
/* 665:734 */       setUpOther();
/* 666:    */     }
/* 667:    */   }
/* 668:    */   
/* 669:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 670:    */   {
/* 671:745 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 672:    */   }
/* 673:    */   
/* 674:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 675:    */   {
/* 676:754 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 677:    */   }
/* 678:    */   
/* 679:    */   public void setEnvironment(Environment env)
/* 680:    */   {
/* 681:761 */     this.m_env = env;
/* 682:    */   }
/* 683:    */   
/* 684:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 685:    */   {
/* 686:767 */     this.m_modifyListener = l;
/* 687:    */   }
/* 688:    */   
/* 689:    */   static {}
/* 690:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SaverCustomizer
 * JD-Core Version:    0.7.0.1
 */