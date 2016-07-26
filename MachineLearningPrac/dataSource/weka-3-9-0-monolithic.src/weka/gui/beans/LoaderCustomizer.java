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
/*  15:    */ import java.util.Arrays;
/*  16:    */ import javax.swing.BorderFactory;
/*  17:    */ import javax.swing.JButton;
/*  18:    */ import javax.swing.JDialog;
/*  19:    */ import javax.swing.JFileChooser;
/*  20:    */ import javax.swing.JLabel;
/*  21:    */ import javax.swing.JPanel;
/*  22:    */ import javax.swing.JPasswordField;
/*  23:    */ import javax.swing.JScrollPane;
/*  24:    */ import weka.core.Environment;
/*  25:    */ import weka.core.EnvironmentHandler;
/*  26:    */ import weka.core.converters.DatabaseConverter;
/*  27:    */ import weka.core.converters.DatabaseLoader;
/*  28:    */ import weka.core.converters.FileSourcedConverter;
/*  29:    */ import weka.gui.ExtensionFileFilter;
/*  30:    */ import weka.gui.GenericObjectEditor;
/*  31:    */ import weka.gui.PropertySheetPanel;
/*  32:    */ 
/*  33:    */ public class LoaderCustomizer
/*  34:    */   extends JPanel
/*  35:    */   implements BeanCustomizer, CustomizerCloseRequester, EnvironmentHandler
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = 6990446313118930298L;
/*  38: 72 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  39:    */   private Loader m_dsLoader;
/*  40: 77 */   private final PropertySheetPanel m_LoaderEditor = new PropertySheetPanel();
/*  41: 79 */   private final JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  42:    */   private Window m_parentWindow;
/*  43:    */   private JDialog m_fileChooserFrame;
/*  44:    */   private EnvironmentField m_dbaseURLText;
/*  45:    */   private EnvironmentField m_userNameText;
/*  46:    */   private EnvironmentField m_queryText;
/*  47:    */   private EnvironmentField m_keyText;
/*  48:    */   private JPasswordField m_passwordText;
/*  49:    */   private EnvironmentField m_fileText;
/*  50:103 */   private Environment m_env = Environment.getSystemWide();
/*  51:    */   private FileEnvironmentField m_dbProps;
/*  52:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  53:109 */   private weka.core.converters.Loader m_backup = null;
/*  54:    */   
/*  55:    */   public LoaderCustomizer()
/*  56:    */   {
/*  57:127 */     setLayout(new BorderLayout());
/*  58:    */     
/*  59:    */ 
/*  60:130 */     this.m_fileChooser.setDialogType(0);
/*  61:131 */     this.m_fileChooser.addActionListener(new ActionListener()
/*  62:    */     {
/*  63:    */       public void actionPerformed(ActionEvent e)
/*  64:    */       {
/*  65:134 */         if (e.getActionCommand().equals("ApproveSelection")) {
/*  66:    */           try
/*  67:    */           {
/*  68:136 */             File selectedFile = LoaderCustomizer.this.m_fileChooser.getSelectedFile();
/*  69:    */             
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:141 */             LoaderCustomizer.this.m_fileText.setText(selectedFile.toString());
/*  74:    */           }
/*  75:    */           catch (Exception ex)
/*  76:    */           {
/*  77:151 */             ex.printStackTrace();
/*  78:    */           }
/*  79:    */         }
/*  80:155 */         if (LoaderCustomizer.this.m_fileChooserFrame != null) {
/*  81:156 */           LoaderCustomizer.this.m_fileChooserFrame.dispose();
/*  82:    */         }
/*  83:    */       }
/*  84:    */     });
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setParentWindow(Window parent)
/*  88:    */   {
/*  89:164 */     this.m_parentWindow = parent;
/*  90:    */   }
/*  91:    */   
/*  92:    */   private void setUpOther()
/*  93:    */   {
/*  94:168 */     removeAll();
/*  95:169 */     add(this.m_LoaderEditor, "Center");
/*  96:    */     
/*  97:171 */     JPanel buttonsP = new JPanel();
/*  98:172 */     buttonsP.setLayout(new FlowLayout());
/*  99:    */     JButton ok;
/* 100:174 */     buttonsP.add(ok = new JButton("OK"));
/* 101:    */     JButton cancel;
/* 102:175 */     buttonsP.add(cancel = new JButton("Cancel"));
/* 103:176 */     ok.addActionListener(new ActionListener()
/* 104:    */     {
/* 105:    */       public void actionPerformed(ActionEvent evt)
/* 106:    */       {
/* 107:183 */         LoaderCustomizer.this.m_LoaderEditor.closingOK();
/* 108:    */         try
/* 109:    */         {
/* 110:188 */           LoaderCustomizer.this.m_dsLoader.newStructure(new boolean[] { true });
/* 111:    */         }
/* 112:    */         catch (Exception e)
/* 113:    */         {
/* 114:190 */           e.printStackTrace();
/* 115:    */         }
/* 116:193 */         if (LoaderCustomizer.this.m_parentWindow != null) {
/* 117:194 */           LoaderCustomizer.this.m_parentWindow.dispose();
/* 118:    */         }
/* 119:    */       }
/* 120:197 */     });
/* 121:198 */     cancel.addActionListener(new ActionListener()
/* 122:    */     {
/* 123:    */       public void actionPerformed(ActionEvent evt)
/* 124:    */       {
/* 125:205 */         LoaderCustomizer.this.m_LoaderEditor.closingCancel();
/* 126:207 */         if (LoaderCustomizer.this.m_parentWindow != null) {
/* 127:208 */           LoaderCustomizer.this.m_parentWindow.dispose();
/* 128:    */         }
/* 129:    */       }
/* 130:212 */     });
/* 131:213 */     add(buttonsP, "South");
/* 132:    */     
/* 133:215 */     validate();
/* 134:216 */     repaint();
/* 135:    */   }
/* 136:    */   
/* 137:    */   private void setUpDatabase()
/* 138:    */   {
/* 139:222 */     removeAll();
/* 140:    */     
/* 141:224 */     JPanel db = new JPanel();
/* 142:225 */     GridBagLayout gbLayout = new GridBagLayout();
/* 143:    */     
/* 144:227 */     db.setLayout(gbLayout);
/* 145:    */     
/* 146:229 */     JLabel urlLab = new JLabel("Database URL", 4);
/* 147:230 */     urlLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 148:231 */     GridBagConstraints gbConstraints = new GridBagConstraints();
/* 149:232 */     gbConstraints.anchor = 13;
/* 150:233 */     gbConstraints.fill = 2;
/* 151:234 */     gbConstraints.gridy = 0;
/* 152:235 */     gbConstraints.gridx = 0;
/* 153:236 */     gbLayout.setConstraints(urlLab, gbConstraints);
/* 154:237 */     db.add(urlLab);
/* 155:    */     
/* 156:239 */     this.m_dbaseURLText = new EnvironmentField();
/* 157:240 */     this.m_dbaseURLText.setEnvironment(this.m_env);
/* 158:    */     
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:247 */     this.m_dbaseURLText.setText(((DatabaseConverter)this.m_dsLoader.getLoader()).getUrl());
/* 165:    */     
/* 166:249 */     gbConstraints = new GridBagConstraints();
/* 167:250 */     gbConstraints.anchor = 13;
/* 168:251 */     gbConstraints.fill = 2;
/* 169:252 */     gbConstraints.gridy = 0;
/* 170:253 */     gbConstraints.gridx = 1;
/* 171:254 */     gbConstraints.weightx = 5.0D;
/* 172:255 */     gbLayout.setConstraints(this.m_dbaseURLText, gbConstraints);
/* 173:256 */     db.add(this.m_dbaseURLText);
/* 174:    */     
/* 175:258 */     JLabel userLab = new JLabel("Username", 4);
/* 176:259 */     userLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 177:260 */     gbConstraints = new GridBagConstraints();
/* 178:261 */     gbConstraints.anchor = 13;
/* 179:262 */     gbConstraints.fill = 2;
/* 180:263 */     gbConstraints.gridy = 1;
/* 181:264 */     gbConstraints.gridx = 0;
/* 182:265 */     gbLayout.setConstraints(userLab, gbConstraints);
/* 183:266 */     db.add(userLab);
/* 184:    */     
/* 185:268 */     this.m_userNameText = new EnvironmentField();
/* 186:269 */     this.m_userNameText.setEnvironment(this.m_env);
/* 187:    */     
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:274 */     this.m_userNameText.setText(((DatabaseConverter)this.m_dsLoader.getLoader()).getUser());
/* 192:    */     
/* 193:276 */     gbConstraints = new GridBagConstraints();
/* 194:277 */     gbConstraints.anchor = 13;
/* 195:278 */     gbConstraints.fill = 2;
/* 196:279 */     gbConstraints.gridy = 1;
/* 197:280 */     gbConstraints.gridx = 1;
/* 198:281 */     gbLayout.setConstraints(this.m_userNameText, gbConstraints);
/* 199:282 */     db.add(this.m_userNameText);
/* 200:    */     
/* 201:284 */     JLabel passwordLab = new JLabel("Password ", 4);
/* 202:285 */     passwordLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 203:286 */     gbConstraints = new GridBagConstraints();
/* 204:287 */     gbConstraints.anchor = 13;
/* 205:288 */     gbConstraints.fill = 2;
/* 206:289 */     gbConstraints.gridy = 2;
/* 207:290 */     gbConstraints.gridx = 0;
/* 208:291 */     gbLayout.setConstraints(passwordLab, gbConstraints);
/* 209:292 */     db.add(passwordLab);
/* 210:    */     
/* 211:294 */     this.m_passwordText = new JPasswordField();
/* 212:295 */     this.m_passwordText.setText(((DatabaseLoader)this.m_dsLoader.getLoader()).getPassword());
/* 213:    */     
/* 214:297 */     JPanel passwordHolder = new JPanel();
/* 215:298 */     passwordHolder.setLayout(new BorderLayout());
/* 216:299 */     passwordHolder.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 217:    */     
/* 218:301 */     passwordHolder.add(this.m_passwordText, "Center");
/* 219:    */     
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:306 */     gbConstraints = new GridBagConstraints();
/* 224:307 */     gbConstraints.anchor = 13;
/* 225:308 */     gbConstraints.fill = 2;
/* 226:309 */     gbConstraints.gridy = 2;
/* 227:310 */     gbConstraints.gridx = 1;
/* 228:311 */     gbLayout.setConstraints(passwordHolder, gbConstraints);
/* 229:312 */     db.add(passwordHolder);
/* 230:    */     
/* 231:314 */     JLabel queryLab = new JLabel("Query", 4);
/* 232:315 */     queryLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 233:316 */     gbConstraints = new GridBagConstraints();
/* 234:317 */     gbConstraints.anchor = 13;
/* 235:318 */     gbConstraints.fill = 2;
/* 236:319 */     gbConstraints.gridy = 3;
/* 237:320 */     gbConstraints.gridx = 0;
/* 238:321 */     gbLayout.setConstraints(queryLab, gbConstraints);
/* 239:322 */     db.add(queryLab);
/* 240:    */     
/* 241:324 */     this.m_queryText = new EnvironmentField();
/* 242:325 */     this.m_queryText.setEnvironment(this.m_env);
/* 243:    */     
/* 244:    */ 
/* 245:    */ 
/* 246:    */ 
/* 247:330 */     this.m_queryText.setText(((DatabaseLoader)this.m_dsLoader.getLoader()).getQuery());
/* 248:331 */     gbConstraints = new GridBagConstraints();
/* 249:332 */     gbConstraints.anchor = 13;
/* 250:333 */     gbConstraints.fill = 2;
/* 251:334 */     gbConstraints.gridy = 3;
/* 252:335 */     gbConstraints.gridx = 1;
/* 253:336 */     gbLayout.setConstraints(this.m_queryText, gbConstraints);
/* 254:337 */     db.add(this.m_queryText);
/* 255:    */     
/* 256:339 */     JLabel keyLab = new JLabel("Key columns", 4);
/* 257:340 */     keyLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 258:341 */     gbConstraints = new GridBagConstraints();
/* 259:342 */     gbConstraints.anchor = 13;
/* 260:343 */     gbConstraints.fill = 2;
/* 261:344 */     gbConstraints.gridy = 4;
/* 262:345 */     gbConstraints.gridx = 0;
/* 263:346 */     gbLayout.setConstraints(keyLab, gbConstraints);
/* 264:347 */     db.add(keyLab);
/* 265:    */     
/* 266:349 */     this.m_keyText = new EnvironmentField();
/* 267:350 */     this.m_keyText.setEnvironment(this.m_env);
/* 268:    */     
/* 269:    */ 
/* 270:    */ 
/* 271:    */ 
/* 272:355 */     this.m_keyText.setText(((DatabaseLoader)this.m_dsLoader.getLoader()).getKeys());
/* 273:356 */     gbConstraints = new GridBagConstraints();
/* 274:357 */     gbConstraints.anchor = 13;
/* 275:358 */     gbConstraints.fill = 2;
/* 276:359 */     gbConstraints.gridy = 4;
/* 277:360 */     gbConstraints.gridx = 1;
/* 278:361 */     gbLayout.setConstraints(this.m_keyText, gbConstraints);
/* 279:362 */     db.add(this.m_keyText);
/* 280:    */     
/* 281:364 */     JLabel propsLab = new JLabel("DB config props", 4);
/* 282:365 */     propsLab.setToolTipText("The custom properties that the user can use to override the default ones.");
/* 283:    */     
/* 284:367 */     propsLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 285:368 */     gbConstraints = new GridBagConstraints();
/* 286:369 */     gbConstraints.anchor = 13;
/* 287:370 */     gbConstraints.fill = 2;
/* 288:371 */     gbConstraints.gridy = 5;
/* 289:372 */     gbConstraints.gridx = 0;
/* 290:373 */     gbLayout.setConstraints(propsLab, gbConstraints);
/* 291:374 */     db.add(propsLab);
/* 292:    */     
/* 293:376 */     this.m_dbProps = new FileEnvironmentField();
/* 294:377 */     this.m_dbProps.setEnvironment(this.m_env);
/* 295:378 */     this.m_dbProps.resetFileFilters();
/* 296:379 */     this.m_dbProps.addFileFilter(new ExtensionFileFilter(".props", "DatabaseUtils property file (*.props)"));
/* 297:    */     
/* 298:381 */     gbConstraints = new GridBagConstraints();
/* 299:382 */     gbConstraints.anchor = 13;
/* 300:383 */     gbConstraints.fill = 2;
/* 301:384 */     gbConstraints.gridy = 5;
/* 302:385 */     gbConstraints.gridx = 1;
/* 303:386 */     gbLayout.setConstraints(this.m_dbProps, gbConstraints);
/* 304:387 */     db.add(this.m_dbProps);
/* 305:388 */     File toSet = ((DatabaseLoader)this.m_dsLoader.getLoader()).getCustomPropsFile();
/* 306:389 */     if (toSet != null) {
/* 307:390 */       this.m_dbProps.setText(toSet.getPath());
/* 308:    */     }
/* 309:393 */     JButton loadPropsBut = new JButton("Load");
/* 310:394 */     loadPropsBut.setToolTipText("Load config");
/* 311:395 */     gbConstraints = new GridBagConstraints();
/* 312:396 */     gbConstraints.anchor = 13;
/* 313:397 */     gbConstraints.fill = 2;
/* 314:398 */     gbConstraints.gridy = 5;
/* 315:399 */     gbConstraints.gridx = 2;
/* 316:400 */     gbLayout.setConstraints(loadPropsBut, gbConstraints);
/* 317:401 */     db.add(loadPropsBut);
/* 318:402 */     loadPropsBut.addActionListener(new ActionListener()
/* 319:    */     {
/* 320:    */       public void actionPerformed(ActionEvent e)
/* 321:    */       {
/* 322:405 */         if ((LoaderCustomizer.this.m_dbProps.getText() != null) && (LoaderCustomizer.this.m_dbProps.getText().length() > 0))
/* 323:    */         {
/* 324:406 */           String propsS = LoaderCustomizer.this.m_dbProps.getText();
/* 325:    */           try
/* 326:    */           {
/* 327:408 */             propsS = LoaderCustomizer.this.m_env.substitute(propsS);
/* 328:    */           }
/* 329:    */           catch (Exception ex) {}
/* 330:411 */           File propsFile = new File(propsS);
/* 331:412 */           if (propsFile.exists())
/* 332:    */           {
/* 333:413 */             ((DatabaseLoader)LoaderCustomizer.this.m_dsLoader.getLoader()).setCustomPropsFile(propsFile);
/* 334:    */             
/* 335:415 */             ((DatabaseLoader)LoaderCustomizer.this.m_dsLoader.getLoader()).resetOptions();
/* 336:416 */             LoaderCustomizer.this.m_dbaseURLText.setText(((DatabaseLoader)LoaderCustomizer.this.m_dsLoader.getLoader()).getUrl());
/* 337:    */           }
/* 338:    */         }
/* 339:    */       }
/* 340:422 */     });
/* 341:423 */     JPanel buttonsP = new JPanel();
/* 342:424 */     buttonsP.setLayout(new FlowLayout());
/* 343:    */     JButton ok;
/* 344:426 */     buttonsP.add(ok = new JButton("OK"));
/* 345:    */     JButton cancel;
/* 346:427 */     buttonsP.add(cancel = new JButton("Cancel"));
/* 347:428 */     ok.addActionListener(new ActionListener()
/* 348:    */     {
/* 349:    */       public void actionPerformed(ActionEvent evt)
/* 350:    */       {
/* 351:445 */         if (LoaderCustomizer.this.resetAndUpdateDatabaseLoaderIfChanged()) {
/* 352:    */           try
/* 353:    */           {
/* 354:449 */             LoaderCustomizer.this.m_dsLoader.setDB(true);
/* 355:    */           }
/* 356:    */           catch (Exception ex) {}
/* 357:    */         }
/* 358:453 */         if (LoaderCustomizer.this.m_parentWindow != null) {
/* 359:454 */           LoaderCustomizer.this.m_parentWindow.dispose();
/* 360:    */         }
/* 361:    */       }
/* 362:457 */     });
/* 363:458 */     cancel.addActionListener(new ActionListener()
/* 364:    */     {
/* 365:    */       public void actionPerformed(ActionEvent evt)
/* 366:    */       {
/* 367:461 */         if (LoaderCustomizer.this.m_backup != null) {
/* 368:462 */           LoaderCustomizer.this.m_dsLoader.setLoader(LoaderCustomizer.this.m_backup);
/* 369:    */         }
/* 370:465 */         if (LoaderCustomizer.this.m_parentWindow != null) {
/* 371:466 */           LoaderCustomizer.this.m_parentWindow.dispose();
/* 372:    */         }
/* 373:    */       }
/* 374:470 */     });
/* 375:471 */     JPanel holderP = new JPanel();
/* 376:472 */     holderP.setLayout(new BorderLayout());
/* 377:473 */     holderP.add(db, "North");
/* 378:474 */     holderP.add(buttonsP, "South");
/* 379:    */     
/* 380:    */ 
/* 381:477 */     JPanel about = this.m_LoaderEditor.getAboutPanel();
/* 382:478 */     if (about != null) {
/* 383:479 */       add(about, "North");
/* 384:    */     }
/* 385:481 */     add(holderP, "South");
/* 386:    */   }
/* 387:    */   
/* 388:    */   private boolean resetAndUpdateDatabaseLoaderIfChanged()
/* 389:    */   {
/* 390:485 */     DatabaseLoader dbl = (DatabaseLoader)this.m_dsLoader.getLoader();
/* 391:486 */     String url = dbl.getUrl();
/* 392:487 */     String user = dbl.getUser();
/* 393:488 */     String password = dbl.getPassword();
/* 394:489 */     String query = dbl.getQuery();
/* 395:490 */     String keys = dbl.getKeys();
/* 396:491 */     File propsFile = dbl.getCustomPropsFile();
/* 397:    */     
/* 398:493 */     boolean update = (!url.equals(this.m_dbaseURLText.getText())) || (!user.equals(this.m_userNameText.getText())) || (!Arrays.equals(password.toCharArray(), this.m_passwordText.getPassword())) || (!query.equalsIgnoreCase(this.m_queryText.getText())) || (!keys.equals(this.m_keyText.getText()));
/* 399:499 */     if ((propsFile != null) && (this.m_dbProps.getText().length() > 0)) {
/* 400:500 */       update = (update) || (!propsFile.toString().equals(this.m_dbProps.getText()));
/* 401:    */     } else {
/* 402:502 */       update = (update) || (this.m_dbProps.getText().length() > 0);
/* 403:    */     }
/* 404:505 */     if (update)
/* 405:    */     {
/* 406:506 */       dbl.resetStructure();
/* 407:507 */       dbl.setUrl(this.m_dbaseURLText.getText());
/* 408:508 */       dbl.setUser(this.m_userNameText.getText());
/* 409:509 */       dbl.setPassword(new String(this.m_passwordText.getPassword()));
/* 410:510 */       dbl.setQuery(this.m_queryText.getText());
/* 411:511 */       dbl.setKeys(this.m_keyText.getText());
/* 412:512 */       if ((this.m_dbProps.getText() != null) && (this.m_dbProps.getText().length() > 0)) {
/* 413:513 */         dbl.setCustomPropsFile(new File(this.m_dbProps.getText()));
/* 414:    */       }
/* 415:    */     }
/* 416:517 */     return update;
/* 417:    */   }
/* 418:    */   
/* 419:    */   public void setUpFile()
/* 420:    */   {
/* 421:521 */     removeAll();
/* 422:    */     
/* 423:523 */     boolean currentFileIsDir = false;
/* 424:524 */     File tmp = ((FileSourcedConverter)this.m_dsLoader.getLoader()).retrieveFile();
/* 425:525 */     String tmpString = tmp.toString();
/* 426:526 */     if (Environment.containsEnvVariables(tmpString)) {
/* 427:    */       try
/* 428:    */       {
/* 429:528 */         tmpString = this.m_env.substitute(tmpString);
/* 430:    */       }
/* 431:    */       catch (Exception ex) {}
/* 432:    */     }
/* 433:533 */     File tmp2 = new File(new File(tmpString).getAbsolutePath());
/* 434:535 */     if (tmp2.isDirectory())
/* 435:    */     {
/* 436:536 */       this.m_fileChooser.setCurrentDirectory(tmp2);
/* 437:537 */       currentFileIsDir = true;
/* 438:    */     }
/* 439:    */     else
/* 440:    */     {
/* 441:539 */       this.m_fileChooser.setSelectedFile(tmp2);
/* 442:    */     }
/* 443:542 */     FileSourcedConverter loader = (FileSourcedConverter)this.m_dsLoader.getLoader();
/* 444:543 */     String[] ext = loader.getFileExtensions();
/* 445:544 */     ExtensionFileFilter firstFilter = null;
/* 446:545 */     for (int i = 0; i < ext.length; i++)
/* 447:    */     {
/* 448:546 */       ExtensionFileFilter ff = new ExtensionFileFilter(ext[i], loader.getFileDescription() + " (*" + ext[i] + ")");
/* 449:548 */       if (i == 0) {
/* 450:549 */         firstFilter = ff;
/* 451:    */       }
/* 452:551 */       this.m_fileChooser.addChoosableFileFilter(ff);
/* 453:    */     }
/* 454:553 */     if (firstFilter != null) {
/* 455:554 */       this.m_fileChooser.setFileFilter(firstFilter);
/* 456:    */     }
/* 457:556 */     JPanel about = this.m_LoaderEditor.getAboutPanel();
/* 458:557 */     JPanel northPanel = new JPanel();
/* 459:558 */     northPanel.setLayout(new BorderLayout());
/* 460:559 */     if (about != null) {
/* 461:560 */       northPanel.add(about, "North");
/* 462:    */     }
/* 463:562 */     add(northPanel, "North");
/* 464:    */     
/* 465:564 */     final EnvironmentField ef = new EnvironmentField();
/* 466:565 */     JPanel efHolder = new JPanel();
/* 467:566 */     efHolder.setLayout(new BorderLayout());
/* 468:    */     
/* 469:568 */     ef.setEnvironment(this.m_env);
/* 470:    */     
/* 471:    */ 
/* 472:    */ 
/* 473:    */ 
/* 474:    */ 
/* 475:574 */     this.m_fileText = ef;
/* 476:578 */     if (!currentFileIsDir) {
/* 477:579 */       ef.setText(tmp.toString());
/* 478:    */     }
/* 479:582 */     efHolder.add(ef, "Center");
/* 480:583 */     JButton browseBut = new JButton("Browse...");
/* 481:584 */     browseBut.addActionListener(new ActionListener()
/* 482:    */     {
/* 483:    */       public void actionPerformed(ActionEvent e)
/* 484:    */       {
/* 485:    */         try
/* 486:    */         {
/* 487:589 */           JDialog jf = new JDialog((JDialog)LoaderCustomizer.this.getTopLevelAncestor(), "Choose file", Dialog.ModalityType.DOCUMENT_MODAL);
/* 488:    */           
/* 489:591 */           jf.setLayout(new BorderLayout());
/* 490:    */           
/* 491:593 */           jf.getContentPane().add(LoaderCustomizer.this.m_fileChooser, "Center");
/* 492:594 */           LoaderCustomizer.this.m_fileChooserFrame = jf;
/* 493:595 */           jf.pack();
/* 494:596 */           jf.setVisible(true);
/* 495:    */         }
/* 496:    */         catch (Exception ex)
/* 497:    */         {
/* 498:598 */           ex.printStackTrace();
/* 499:    */         }
/* 500:    */       }
/* 501:602 */     });
/* 502:603 */     JPanel bP = new JPanel();
/* 503:604 */     bP.setLayout(new BorderLayout());
/* 504:605 */     bP.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
/* 505:606 */     bP.add(browseBut, "Center");
/* 506:607 */     efHolder.add(bP, "East");
/* 507:608 */     JPanel alignedP = new JPanel();
/* 508:609 */     alignedP.setBorder(BorderFactory.createTitledBorder("File"));
/* 509:610 */     alignedP.setLayout(new BorderLayout());
/* 510:611 */     JLabel efLab = new JLabel("Filename", 4);
/* 511:612 */     efLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
/* 512:613 */     alignedP.add(efLab, "West");
/* 513:614 */     alignedP.add(efHolder, "Center");
/* 514:    */     
/* 515:616 */     northPanel.add(alignedP, "South");
/* 516:    */     
/* 517:618 */     JPanel butHolder = new JPanel();
/* 518:    */     
/* 519:620 */     butHolder.setLayout(new FlowLayout());
/* 520:621 */     JButton OKBut = new JButton("OK");
/* 521:622 */     OKBut.addActionListener(new ActionListener()
/* 522:    */     {
/* 523:    */       public void actionPerformed(ActionEvent e)
/* 524:    */       {
/* 525:    */         try
/* 526:    */         {
/* 527:626 */           ((FileSourcedConverter)LoaderCustomizer.this.m_dsLoader.getLoader()).setFile(new File(ef.getText()));
/* 528:    */           
/* 529:    */ 
/* 530:    */ 
/* 531:    */ 
/* 532:631 */           LoaderCustomizer.this.m_dsLoader.newFileSelected();
/* 533:    */         }
/* 534:    */         catch (Exception ex)
/* 535:    */         {
/* 536:633 */           ex.printStackTrace();
/* 537:    */         }
/* 538:635 */         if (LoaderCustomizer.this.m_modifyListener != null) {
/* 539:636 */           LoaderCustomizer.this.m_modifyListener.setModifiedStatus(LoaderCustomizer.this, true);
/* 540:    */         }
/* 541:638 */         LoaderCustomizer.this.m_parentWindow.dispose();
/* 542:    */       }
/* 543:641 */     });
/* 544:642 */     JButton CancelBut = new JButton("Cancel");
/* 545:643 */     CancelBut.addActionListener(new ActionListener()
/* 546:    */     {
/* 547:    */       public void actionPerformed(ActionEvent e)
/* 548:    */       {
/* 549:646 */         if (LoaderCustomizer.this.m_modifyListener != null) {
/* 550:647 */           LoaderCustomizer.this.m_modifyListener.setModifiedStatus(LoaderCustomizer.this, false);
/* 551:    */         }
/* 552:650 */         if (LoaderCustomizer.this.m_backup != null) {
/* 553:651 */           LoaderCustomizer.this.m_dsLoader.setLoader(LoaderCustomizer.this.m_backup);
/* 554:    */         }
/* 555:654 */         LoaderCustomizer.this.m_parentWindow.dispose();
/* 556:    */       }
/* 557:657 */     });
/* 558:658 */     butHolder.add(OKBut);
/* 559:659 */     butHolder.add(CancelBut);
/* 560:    */     
/* 561:661 */     JPanel optionsHolder = new JPanel();
/* 562:662 */     optionsHolder.setLayout(new BorderLayout());
/* 563:663 */     optionsHolder.setBorder(BorderFactory.createTitledBorder("Other options"));
/* 564:    */     
/* 565:665 */     optionsHolder.add(this.m_LoaderEditor, "South");
/* 566:666 */     JScrollPane scroller = new JScrollPane(optionsHolder);
/* 567:    */     
/* 568:668 */     add(scroller, "Center");
/* 569:    */     
/* 570:670 */     add(butHolder, "South");
/* 571:    */   }
/* 572:    */   
/* 573:    */   public void setObject(Object object)
/* 574:    */   {
/* 575:680 */     this.m_dsLoader = ((Loader)object);
/* 576:    */     try
/* 577:    */     {
/* 578:683 */       this.m_backup = ((weka.core.converters.Loader)GenericObjectEditor.makeCopy(this.m_dsLoader.getLoader()));
/* 579:    */     }
/* 580:    */     catch (Exception ex) {}
/* 581:689 */     this.m_LoaderEditor.setTarget(this.m_dsLoader.getLoader());
/* 582:    */     
/* 583:691 */     this.m_LoaderEditor.setEnvironment(this.m_env);
/* 584:692 */     if ((this.m_dsLoader.getLoader() instanceof FileSourcedConverter)) {
/* 585:693 */       setUpFile();
/* 586:695 */     } else if ((this.m_dsLoader.getLoader() instanceof DatabaseConverter)) {
/* 587:696 */       setUpDatabase();
/* 588:    */     } else {
/* 589:698 */       setUpOther();
/* 590:    */     }
/* 591:    */   }
/* 592:    */   
/* 593:    */   public void setEnvironment(Environment env)
/* 594:    */   {
/* 595:705 */     this.m_env = env;
/* 596:    */   }
/* 597:    */   
/* 598:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 599:    */   {
/* 600:715 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 601:    */   }
/* 602:    */   
/* 603:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 604:    */   {
/* 605:725 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 606:    */   }
/* 607:    */   
/* 608:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 609:    */   {
/* 610:730 */     this.m_modifyListener = l;
/* 611:    */   }
/* 612:    */   
/* 613:    */   static {}
/* 614:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.LoaderCustomizer
 * JD-Core Version:    0.7.0.1
 */