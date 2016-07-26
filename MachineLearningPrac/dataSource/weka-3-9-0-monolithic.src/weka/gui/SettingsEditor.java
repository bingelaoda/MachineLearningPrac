/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Dialog;
/*   7:    */ import java.awt.Dimension;
/*   8:    */ import java.awt.Graphics;
/*   9:    */ import java.awt.GridBagConstraints;
/*  10:    */ import java.awt.GridBagLayout;
/*  11:    */ import java.awt.Rectangle;
/*  12:    */ import java.awt.Window;
/*  13:    */ import java.awt.event.HierarchyEvent;
/*  14:    */ import java.awt.event.HierarchyListener;
/*  15:    */ import java.beans.PropertyChangeEvent;
/*  16:    */ import java.beans.PropertyChangeListener;
/*  17:    */ import java.beans.PropertyEditor;
/*  18:    */ import java.beans.PropertyEditorManager;
/*  19:    */ import java.io.File;
/*  20:    */ import java.io.IOException;
/*  21:    */ import java.io.PrintStream;
/*  22:    */ import java.util.ArrayList;
/*  23:    */ import java.util.LinkedHashMap;
/*  24:    */ import java.util.LinkedList;
/*  25:    */ import java.util.List;
/*  26:    */ import java.util.Map;
/*  27:    */ import java.util.Map.Entry;
/*  28:    */ import java.util.Set;
/*  29:    */ import javax.swing.BorderFactory;
/*  30:    */ import javax.swing.BoxLayout;
/*  31:    */ import javax.swing.JCheckBox;
/*  32:    */ import javax.swing.JComboBox;
/*  33:    */ import javax.swing.JComponent;
/*  34:    */ import javax.swing.JDialog;
/*  35:    */ import javax.swing.JLabel;
/*  36:    */ import javax.swing.JOptionPane;
/*  37:    */ import javax.swing.JPanel;
/*  38:    */ import javax.swing.JScrollPane;
/*  39:    */ import javax.swing.JTabbedPane;
/*  40:    */ import javax.swing.SwingUtilities;
/*  41:    */ import weka.classifiers.Classifier;
/*  42:    */ import weka.clusterers.Clusterer;
/*  43:    */ import weka.core.Environment;
/*  44:    */ import weka.core.Settings;
/*  45:    */ import weka.core.Settings.SettingKey;
/*  46:    */ 
/*  47:    */ public class SettingsEditor
/*  48:    */   extends JPanel
/*  49:    */ {
/*  50:    */   private static final long serialVersionUID = 1453121012707399758L;
/*  51:    */   protected GUIApplication m_ownerApp;
/*  52:    */   protected Settings m_settings;
/*  53: 79 */   protected JTabbedPane m_settingsTabs = new JTabbedPane();
/*  54:    */   protected PerspectiveSelector m_perspectiveSelector;
/*  55: 82 */   List<SingleSettingsEditor> m_perspectiveEditors = new ArrayList();
/*  56:    */   
/*  57:    */   public SettingsEditor(Settings settings, GUIApplication ownerApp)
/*  58:    */   {
/*  59: 86 */     setLayout(new BorderLayout());
/*  60: 87 */     this.m_settings = settings;
/*  61: 88 */     this.m_ownerApp = ownerApp;
/*  62: 89 */     GenericObjectEditor.registerEditors();
/*  63: 90 */     PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
/*  64: 92 */     if (this.m_ownerApp.getPerspectiveManager().getLoadedPerspectives().size() > 0) {
/*  65: 93 */       setupPerspectiveSelector();
/*  66:    */     }
/*  67: 95 */     setupPerspectiveSettings();
/*  68: 96 */     add(this.m_settingsTabs, "Center");
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void applyToSettings()
/*  72:    */   {
/*  73:100 */     if (this.m_perspectiveSelector != null) {
/*  74:101 */       this.m_perspectiveSelector.applyToSettings();
/*  75:    */     }
/*  76:104 */     for (SingleSettingsEditor editor : this.m_perspectiveEditors) {
/*  77:105 */       editor.applyToSettings();
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   protected void setupPerspectiveSelector()
/*  82:    */   {
/*  83:110 */     this.m_perspectiveSelector = new PerspectiveSelector();
/*  84:111 */     this.m_settingsTabs.addTab("Perspectives", this.m_perspectiveSelector);
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected void setupPerspectiveSettings()
/*  88:    */   {
/*  89:117 */     if ((this.m_settings.getSettings(this.m_ownerApp.getApplicationID()) != null) && (this.m_settings.getSettings(this.m_ownerApp.getApplicationID()).size() > 1))
/*  90:    */     {
/*  91:119 */       Map<Settings.SettingKey, Object> appSettings = this.m_settings.getSettings(this.m_ownerApp.getApplicationID());
/*  92:    */       
/*  93:121 */       SingleSettingsEditor appEditor = new SingleSettingsEditor(appSettings);
/*  94:122 */       this.m_settingsTabs.addTab("General", appEditor);
/*  95:123 */       this.m_perspectiveEditors.add(appEditor);
/*  96:    */     }
/*  97:127 */     Perspective mainPers = this.m_ownerApp.getMainPerspective();
/*  98:128 */     String mainTitle = mainPers.getPerspectiveTitle();
/*  99:129 */     String mainID = mainPers.getPerspectiveID();
/* 100:130 */     SingleSettingsEditor mainEditor = new SingleSettingsEditor(this.m_settings.getSettings(mainID));
/* 101:    */     
/* 102:132 */     this.m_settingsTabs.addTab(mainTitle, mainEditor);
/* 103:133 */     this.m_perspectiveEditors.add(mainEditor);
/* 104:    */     
/* 105:135 */     List<Perspective> availablePerspectives = this.m_ownerApp.getPerspectiveManager().getLoadedPerspectives();
/* 106:    */     
/* 107:137 */     List<String> availablePerspectivesIDs = new ArrayList();
/* 108:138 */     List<String> availablePerspectiveTitles = new ArrayList();
/* 109:139 */     for (int i = 0; i < availablePerspectives.size(); i++)
/* 110:    */     {
/* 111:140 */       Perspective p = (Perspective)availablePerspectives.get(i);
/* 112:141 */       availablePerspectivesIDs.add(p.getPerspectiveID());
/* 113:142 */       availablePerspectiveTitles.add(p.getPerspectiveTitle());
/* 114:    */     }
/* 115:145 */     Set<String> settingsIDs = this.m_settings.getSettingsIDs();
/* 116:146 */     for (String settingID : settingsIDs) {
/* 117:147 */       if (availablePerspectivesIDs.contains(settingID))
/* 118:    */       {
/* 119:148 */         int indexOfP = availablePerspectivesIDs.indexOf(settingID);
/* 120:    */         
/* 121:    */ 
/* 122:151 */         Map<Settings.SettingKey, Object> settingsForID = this.m_settings.getSettings(settingID);
/* 123:153 */         if ((settingsForID != null) && (settingsForID.size() > 0))
/* 124:    */         {
/* 125:154 */           SingleSettingsEditor perpEditor = new SingleSettingsEditor(settingsForID);
/* 126:    */           
/* 127:156 */           this.m_settingsTabs.addTab((String)availablePerspectiveTitles.get(indexOfP), perpEditor);
/* 128:    */           
/* 129:158 */           this.m_perspectiveEditors.add(perpEditor);
/* 130:    */         }
/* 131:    */       }
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public static SingleSettingsEditor createSingleSettingsEditor(Map<Settings.SettingKey, Object> settingsToEdit)
/* 136:    */   {
/* 137:172 */     GenericObjectEditor.registerEditors();
/* 138:173 */     PropertyEditorManager.registerEditor(Color.class, ColorEditor.class);
/* 139:174 */     return new SingleSettingsEditor(settingsToEdit);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static int showSingleSettingsEditor(Settings settings, String settingsID, String settingsName, JComponent parent)
/* 143:    */     throws IOException
/* 144:    */   {
/* 145:193 */     return showSingleSettingsEditor(settings, settingsID, settingsName, parent, 600, 300);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public static int showSingleSettingsEditor(Settings settings, String settingsID, String settingsName, JComponent parent, int width, int height)
/* 149:    */     throws IOException
/* 150:    */   {
/* 151:215 */     SingleSettingsEditor sse = createSingleSettingsEditor(settings.getSettings(settingsID));
/* 152:    */     
/* 153:217 */     sse.setPreferredSize(new Dimension(width, height));
/* 154:    */     
/* 155:219 */     JOptionPane pane = new JOptionPane(sse, -1, 2);
/* 156:    */     
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:233 */     String os = System.getProperty("os.name").toLowerCase();
/* 170:234 */     String suppressSwingDropSupport = System.getProperty("suppressSwingDropSupport", "false");
/* 171:    */     
/* 172:236 */     boolean nix = (os.contains("nix")) || (os.contains("nux")) || (os.contains("aix"));
/* 173:237 */     if ((!nix) || (suppressSwingDropSupport.equalsIgnoreCase("true"))) {
/* 174:238 */       pane.addHierarchyListener(new HierarchyListener()
/* 175:    */       {
/* 176:    */         public void hierarchyChanged(HierarchyEvent e)
/* 177:    */         {
/* 178:240 */           Window window = SwingUtilities.getWindowAncestor(this.val$pane);
/* 179:241 */           if ((window instanceof Dialog))
/* 180:    */           {
/* 181:242 */             Dialog dialog = (Dialog)window;
/* 182:243 */             if (!dialog.isResizable()) {
/* 183:244 */               dialog.setResizable(true);
/* 184:    */             }
/* 185:    */           }
/* 186:    */         }
/* 187:    */       });
/* 188:    */     }
/* 189:250 */     JDialog dialog = pane.createDialog(parent, settingsName + " Settings");
/* 190:    */     
/* 191:252 */     dialog.show();
/* 192:253 */     Object resultO = pane.getValue();
/* 193:    */     
/* 194:    */ 
/* 195:    */ 
/* 196:    */ 
/* 197:    */ 
/* 198:    */ 
/* 199:    */ 
/* 200:261 */     int result = -1;
/* 201:262 */     if (resultO == null) {
/* 202:263 */       result = -1;
/* 203:264 */     } else if ((resultO instanceof Integer)) {
/* 204:265 */       result = ((Integer)resultO).intValue();
/* 205:    */     }
/* 206:268 */     if (result == 0)
/* 207:    */     {
/* 208:269 */       sse.applyToSettings();
/* 209:270 */       settings.saveSettings();
/* 210:    */     }
/* 211:273 */     if (result == 0)
/* 212:    */     {
/* 213:274 */       sse.applyToSettings();
/* 214:275 */       settings.saveSettings();
/* 215:    */     }
/* 216:278 */     return result;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static int showApplicationSettingsEditor(Settings settings, GUIApplication application)
/* 220:    */     throws IOException
/* 221:    */   {
/* 222:293 */     SettingsEditor settingsEditor = new SettingsEditor(settings, application);
/* 223:    */     
/* 224:295 */     settingsEditor.setPreferredSize(new Dimension(800, 350));
/* 225:    */     
/* 226:297 */     JOptionPane pane = new JOptionPane(settingsEditor, -1, 2);
/* 227:    */     
/* 228:    */ 
/* 229:    */ 
/* 230:    */ 
/* 231:    */ 
/* 232:    */ 
/* 233:    */ 
/* 234:    */ 
/* 235:    */ 
/* 236:    */ 
/* 237:    */ 
/* 238:    */ 
/* 239:    */ 
/* 240:311 */     String os = System.getProperty("os.name").toLowerCase();
/* 241:312 */     String suppressSwingDropSupport = System.getProperty("suppressSwingDropSupport", "false");
/* 242:    */     
/* 243:314 */     boolean nix = (os.contains("nix")) || (os.contains("nux")) || (os.contains("aix"));
/* 244:315 */     if ((!nix) || (suppressSwingDropSupport.equalsIgnoreCase("true"))) {
/* 245:316 */       pane.addHierarchyListener(new HierarchyListener()
/* 246:    */       {
/* 247:    */         public void hierarchyChanged(HierarchyEvent e)
/* 248:    */         {
/* 249:318 */           Window window = SwingUtilities.getWindowAncestor(this.val$pane);
/* 250:319 */           if ((window instanceof Dialog))
/* 251:    */           {
/* 252:320 */             Dialog dialog = (Dialog)window;
/* 253:321 */             if (!dialog.isResizable()) {
/* 254:322 */               dialog.setResizable(true);
/* 255:    */             }
/* 256:    */           }
/* 257:    */         }
/* 258:    */       });
/* 259:    */     }
/* 260:328 */     JDialog dialog = pane.createDialog((JComponent)application, application.getApplicationName() + " Settings");
/* 261:    */     
/* 262:330 */     dialog.show();
/* 263:331 */     Object resultO = pane.getValue();
/* 264:332 */     int result = -1;
/* 265:333 */     if (resultO == null) {
/* 266:334 */       result = -1;
/* 267:335 */     } else if ((resultO instanceof Integer)) {
/* 268:336 */       result = ((Integer)resultO).intValue();
/* 269:    */     }
/* 270:339 */     if (result == 0)
/* 271:    */     {
/* 272:340 */       settingsEditor.applyToSettings();
/* 273:341 */       settings.saveSettings();
/* 274:    */     }
/* 275:344 */     return result;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public static class SingleSettingsEditor
/* 279:    */     extends JPanel
/* 280:    */     implements PropertyChangeListener
/* 281:    */   {
/* 282:    */     private static final long serialVersionUID = 8896265984902770239L;
/* 283:    */     protected Map<Settings.SettingKey, Object> m_perspSettings;
/* 284:352 */     protected Map<Settings.SettingKey, PropertyEditor> m_editorMap = new LinkedHashMap();
/* 285:    */     
/* 286:    */     public SingleSettingsEditor(Map<Settings.SettingKey, Object> pSettings)
/* 287:    */     {
/* 288:356 */       this.m_perspSettings = pSettings;
/* 289:357 */       setLayout(new BorderLayout());
/* 290:    */       
/* 291:359 */       JPanel scrollablePanel = new JPanel();
/* 292:360 */       JScrollPane scrollPane = new JScrollPane(scrollablePanel);
/* 293:361 */       scrollPane.setBorder(BorderFactory.createEmptyBorder());
/* 294:362 */       add(scrollPane, "Center");
/* 295:    */       
/* 296:364 */       GridBagLayout gbLayout = new GridBagLayout();
/* 297:    */       
/* 298:366 */       scrollablePanel.setLayout(gbLayout);
/* 299:367 */       setVisible(false);
/* 300:    */       
/* 301:369 */       int i = 0;
/* 302:370 */       for (Map.Entry<Settings.SettingKey, Object> prop : pSettings.entrySet())
/* 303:    */       {
/* 304:371 */         Settings.SettingKey settingName = (Settings.SettingKey)prop.getKey();
/* 305:372 */         if (!settingName.getKey().equals(PerspectiveManager.VISIBLE_PERSPECTIVES_KEY.getKey()))
/* 306:    */         {
/* 307:377 */           Object settingValue = prop.getValue();
/* 308:378 */           List<String> pickList = ((Settings.SettingKey)prop.getKey()).getPickList();
/* 309:    */           
/* 310:380 */           PropertyEditor editor = null;
/* 311:381 */           if (((settingValue instanceof String)) && (pickList != null) && (pickList.size() > 0))
/* 312:    */           {
/* 313:384 */             SettingsEditor.PickList pEditor = new SettingsEditor.PickList(pickList);
/* 314:385 */             pEditor.setValue(prop.getValue());
/* 315:386 */             editor = pEditor;
/* 316:    */           }
/* 317:389 */           Class<?> settingClass = settingValue.getClass();
/* 318:391 */           if (editor == null) {
/* 319:392 */             editor = PropertyEditorManager.findEditor(settingClass);
/* 320:    */           }
/* 321:398 */           if ((settingValue instanceof File))
/* 322:    */           {
/* 323:400 */             String dialogType = settingName.getMetadataElement("java.io.File.dialogType", "0");
/* 324:    */             
/* 325:402 */             String fileType = settingName.getMetadataElement("java.io.File.fileSelectionMode", "2");
/* 326:    */             
/* 327:    */ 
/* 328:    */ 
/* 329:406 */             int dType = Integer.parseInt(dialogType);
/* 330:407 */             int fType = Integer.parseInt(fileType);
/* 331:    */             
/* 332:409 */             editor = new FileEnvironmentField("", Environment.getSystemWide(), dType, fType == 1);
/* 333:    */           }
/* 334:413 */           if (editor == null)
/* 335:    */           {
/* 336:419 */             if ((settingValue instanceof Integer)) {
/* 337:420 */               settingClass = Integer.TYPE;
/* 338:421 */             } else if ((settingValue instanceof Float)) {
/* 339:422 */               settingClass = Float.TYPE;
/* 340:423 */             } else if ((settingValue instanceof Double)) {
/* 341:424 */               settingClass = Double.TYPE;
/* 342:425 */             } else if ((settingValue instanceof Boolean)) {
/* 343:426 */               settingClass = Boolean.TYPE;
/* 344:427 */             } else if ((settingValue instanceof Long)) {
/* 345:428 */               settingClass = Long.TYPE;
/* 346:429 */             } else if ((settingValue instanceof Short)) {
/* 347:430 */               settingClass = Short.TYPE;
/* 348:431 */             } else if ((settingValue instanceof Byte)) {
/* 349:432 */               settingClass = Byte.TYPE;
/* 350:    */             }
/* 351:438 */             editor = PropertyEditorManager.findEditor(settingClass);
/* 352:    */           }
/* 353:445 */           if (editor == null) {
/* 354:446 */             if ((settingValue instanceof Classifier))
/* 355:    */             {
/* 356:447 */               editor = PropertyEditorManager.findEditor(Classifier.class);
/* 357:448 */               settingClass = Classifier.class;
/* 358:    */             }
/* 359:449 */             else if ((settingValue instanceof Clusterer))
/* 360:    */             {
/* 361:450 */               editor = PropertyEditorManager.findEditor(Clusterer.class);
/* 362:451 */               settingClass = Clusterer.class;
/* 363:    */             }
/* 364:    */           }
/* 365:455 */           if (editor == null) {
/* 366:456 */             while ((settingClass != null) && (editor == null))
/* 367:    */             {
/* 368:458 */               Class<?>[] interfaces = settingClass.getInterfaces();
/* 369:459 */               if (interfaces != null) {
/* 370:460 */                 for (Class intf : interfaces)
/* 371:    */                 {
/* 372:461 */                   editor = PropertyEditorManager.findEditor(intf);
/* 373:462 */                   if (editor != null)
/* 374:    */                   {
/* 375:463 */                     settingClass = intf;
/* 376:464 */                     break;
/* 377:    */                   }
/* 378:    */                 }
/* 379:    */               }
/* 380:468 */               if (editor == null)
/* 381:    */               {
/* 382:469 */                 settingClass = settingClass.getSuperclass();
/* 383:470 */                 if (settingClass != null) {
/* 384:471 */                   editor = PropertyEditorManager.findEditor(settingClass);
/* 385:    */                 }
/* 386:    */               }
/* 387:    */             }
/* 388:    */           }
/* 389:477 */           if (editor != null)
/* 390:    */           {
/* 391:478 */             if ((editor instanceof GenericObjectEditor)) {
/* 392:479 */               ((GenericObjectEditor)editor).setClassType(settingClass);
/* 393:    */             }
/* 394:481 */             editor.addPropertyChangeListener(this);
/* 395:482 */             editor.setValue(settingValue);
/* 396:483 */             JComponent view = null;
/* 397:485 */             if ((editor.isPaintable()) && (editor.supportsCustomEditor()))
/* 398:    */             {
/* 399:486 */               view = new PropertyPanel(editor);
/* 400:    */             }
/* 401:487 */             else if ((editor.supportsCustomEditor()) && ((editor.getCustomEditor() instanceof JComponent)))
/* 402:    */             {
/* 403:489 */               view = (JComponent)editor.getCustomEditor();
/* 404:    */             }
/* 405:490 */             else if (editor.getTags() != null)
/* 406:    */             {
/* 407:491 */               view = new PropertyValueSelector(editor);
/* 408:    */             }
/* 409:492 */             else if (editor.getAsText() != null)
/* 410:    */             {
/* 411:493 */               view = new PropertyText(editor);
/* 412:    */             }
/* 413:    */             else
/* 414:    */             {
/* 415:495 */               System.err.println("Warning: Property \"" + settingName + "\" has non-displayabale editor.  Skipping.");
/* 416:    */               
/* 417:497 */               continue;
/* 418:    */             }
/* 419:500 */             this.m_editorMap.put(settingName, editor);
/* 420:    */             
/* 421:502 */             JLabel propLabel = new JLabel(settingName.getDescription(), 4);
/* 422:504 */             if (((Settings.SettingKey)prop.getKey()).getToolTip().length() > 0)
/* 423:    */             {
/* 424:505 */               propLabel.setToolTipText(((Settings.SettingKey)prop.getKey()).getToolTip());
/* 425:506 */               view.setToolTipText(((Settings.SettingKey)prop.getKey()).getToolTip());
/* 426:    */             }
/* 427:508 */             propLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
/* 428:509 */             GridBagConstraints gbConstraints = new GridBagConstraints();
/* 429:510 */             gbConstraints.anchor = 13;
/* 430:511 */             gbConstraints.fill = 2;
/* 431:512 */             gbConstraints.gridy = i;
/* 432:513 */             gbConstraints.gridx = 0;
/* 433:514 */             gbLayout.setConstraints(propLabel, gbConstraints);
/* 434:515 */             scrollablePanel.add(propLabel);
/* 435:    */             
/* 436:517 */             JPanel newPanel = new JPanel();
/* 437:518 */             newPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
/* 438:519 */             newPanel.setLayout(new BorderLayout());
/* 439:520 */             newPanel.add(view, "Center");
/* 440:521 */             gbConstraints = new GridBagConstraints();
/* 441:522 */             gbConstraints.anchor = 17;
/* 442:523 */             gbConstraints.fill = 1;
/* 443:524 */             gbConstraints.gridy = i;
/* 444:525 */             gbConstraints.gridx = 1;
/* 445:526 */             gbConstraints.weightx = 100.0D;
/* 446:527 */             gbLayout.setConstraints(newPanel, gbConstraints);
/* 447:528 */             scrollablePanel.add(newPanel);
/* 448:    */             
/* 449:530 */             i++;
/* 450:    */           }
/* 451:    */           else
/* 452:    */           {
/* 453:532 */             System.err.println("SettingsEditor can't find an editor for: " + settingClass.toString());
/* 454:    */           }
/* 455:    */         }
/* 456:    */       }
/* 457:537 */       Dimension dim = scrollablePanel.getPreferredSize();
/* 458:538 */       dim.height += 20;
/* 459:539 */       dim.width += 20;
/* 460:540 */       scrollPane.setPreferredSize(dim);
/* 461:541 */       validate();
/* 462:    */       
/* 463:543 */       setVisible(true);
/* 464:    */     }
/* 465:    */     
/* 466:    */     public void applyToSettings()
/* 467:    */     {
/* 468:547 */       for (Map.Entry<Settings.SettingKey, Object> e : this.m_perspSettings.entrySet())
/* 469:    */       {
/* 470:549 */         Settings.SettingKey settingKey = (Settings.SettingKey)e.getKey();
/* 471:550 */         if (!settingKey.getKey().equals(PerspectiveManager.VISIBLE_PERSPECTIVES_KEY.getKey()))
/* 472:    */         {
/* 473:554 */           PropertyEditor editor = (PropertyEditor)this.m_editorMap.get(settingKey);
/* 474:555 */           if (editor != null)
/* 475:    */           {
/* 476:556 */             Object newSettingValue = editor.getValue();
/* 477:557 */             this.m_perspSettings.put(settingKey, newSettingValue);
/* 478:    */           }
/* 479:    */         }
/* 480:    */       }
/* 481:    */     }
/* 482:    */     
/* 483:    */     public void propertyChange(PropertyChangeEvent evt)
/* 484:    */     {
/* 485:564 */       repaint();
/* 486:565 */       revalidate();
/* 487:    */     }
/* 488:    */   }
/* 489:    */   
/* 490:    */   protected class PerspectiveSelector
/* 491:    */     extends JPanel
/* 492:    */   {
/* 493:    */     private static final long serialVersionUID = -4765015948030757897L;
/* 494:572 */     protected List<JCheckBox> m_perspectiveChecks = new ArrayList();
/* 495:573 */     protected JCheckBox m_toolBarVisibleOnStartup = new JCheckBox("Perspective toolbar visible on start up");
/* 496:    */     
/* 497:    */     public PerspectiveSelector()
/* 498:    */     {
/* 499:577 */       setLayout(new BorderLayout());
/* 500:    */       
/* 501:579 */       List<Perspective> availablePerspectives = SettingsEditor.this.m_ownerApp.getPerspectiveManager().getLoadedPerspectives();
/* 502:581 */       if (availablePerspectives.size() > 0)
/* 503:    */       {
/* 504:582 */         PerspectiveManager.SelectedPerspectivePreferences userSelected = new PerspectiveManager.SelectedPerspectivePreferences();
/* 505:    */         
/* 506:584 */         userSelected = (PerspectiveManager.SelectedPerspectivePreferences)SettingsEditor.this.m_settings.getSetting(SettingsEditor.this.m_ownerApp.getApplicationID(), PerspectiveManager.VISIBLE_PERSPECTIVES_KEY, userSelected, Environment.getSystemWide());
/* 507:    */         
/* 508:    */ 
/* 509:    */ 
/* 510:588 */         JPanel p = new JPanel();
/* 511:589 */         p.setLayout(new BoxLayout(p, 1));
/* 512:590 */         if (!userSelected.getPerspectivesToolbarAlwaysHidden())
/* 513:    */         {
/* 514:591 */           p.add(this.m_toolBarVisibleOnStartup);
/* 515:592 */           this.m_toolBarVisibleOnStartup.setSelected(userSelected.getPerspectivesToolbarVisibleOnStartup());
/* 516:    */         }
/* 517:596 */         for (Perspective perspective : availablePerspectives)
/* 518:    */         {
/* 519:597 */           String pName = perspective.getPerspectiveTitle();
/* 520:598 */           JCheckBox jb = new JCheckBox(pName);
/* 521:599 */           jb.setSelected(userSelected.getUserVisiblePerspectives().contains(pName));
/* 522:    */           
/* 523:601 */           this.m_perspectiveChecks.add(jb);
/* 524:602 */           p.add(jb);
/* 525:    */         }
/* 526:605 */         add(p, "Center");
/* 527:    */       }
/* 528:    */     }
/* 529:    */     
/* 530:    */     public void applyToSettings()
/* 531:    */     {
/* 532:610 */       LinkedList<String> selectedPerspectives = new LinkedList();
/* 533:611 */       for (JCheckBox c : this.m_perspectiveChecks) {
/* 534:612 */         if (c.isSelected()) {
/* 535:613 */           selectedPerspectives.add(c.getText());
/* 536:    */         }
/* 537:    */       }
/* 538:616 */       PerspectiveManager.SelectedPerspectivePreferences newPrefs = new PerspectiveManager.SelectedPerspectivePreferences();
/* 539:    */       
/* 540:618 */       newPrefs.setUserVisiblePerspectives(selectedPerspectives);
/* 541:619 */       newPrefs.setPerspectivesToolbarVisibleOnStartup(this.m_toolBarVisibleOnStartup.isSelected());
/* 542:    */       
/* 543:621 */       SettingsEditor.this.m_settings.setSetting(SettingsEditor.this.m_ownerApp.getApplicationID(), PerspectiveManager.VISIBLE_PERSPECTIVES_KEY, newPrefs);
/* 544:    */     }
/* 545:    */   }
/* 546:    */   
/* 547:    */   protected static class PickList
/* 548:    */     extends JPanel
/* 549:    */     implements PropertyEditor
/* 550:    */   {
/* 551:    */     private static final long serialVersionUID = 3505647427533464230L;
/* 552:632 */     protected JComboBox<String> m_list = new JComboBox();
/* 553:    */     
/* 554:    */     public PickList(List<String> list)
/* 555:    */     {
/* 556:635 */       for (String item : list) {
/* 557:636 */         this.m_list.addItem(item);
/* 558:    */       }
/* 559:639 */       setLayout(new BorderLayout());
/* 560:640 */       add(this.m_list, "Center");
/* 561:    */     }
/* 562:    */     
/* 563:    */     public void setValue(Object value)
/* 564:    */     {
/* 565:645 */       this.m_list.setSelectedItem(value.toString());
/* 566:    */     }
/* 567:    */     
/* 568:    */     public Object getValue()
/* 569:    */     {
/* 570:650 */       return this.m_list.getSelectedItem();
/* 571:    */     }
/* 572:    */     
/* 573:    */     public boolean isPaintable()
/* 574:    */     {
/* 575:655 */       return false;
/* 576:    */     }
/* 577:    */     
/* 578:    */     public void paintValue(Graphics gfx, Rectangle box) {}
/* 579:    */     
/* 580:    */     public String getJavaInitializationString()
/* 581:    */     {
/* 582:665 */       return null;
/* 583:    */     }
/* 584:    */     
/* 585:    */     public String getAsText()
/* 586:    */     {
/* 587:670 */       return null;
/* 588:    */     }
/* 589:    */     
/* 590:    */     public void setAsText(String text)
/* 591:    */       throws IllegalArgumentException
/* 592:    */     {}
/* 593:    */     
/* 594:    */     public String[] getTags()
/* 595:    */     {
/* 596:680 */       return null;
/* 597:    */     }
/* 598:    */     
/* 599:    */     public Component getCustomEditor()
/* 600:    */     {
/* 601:685 */       return this;
/* 602:    */     }
/* 603:    */     
/* 604:    */     public boolean supportsCustomEditor()
/* 605:    */     {
/* 606:690 */       return true;
/* 607:    */     }
/* 608:    */     
/* 609:    */     public void addPropertyChangeListener(PropertyChangeListener listener) {}
/* 610:    */     
/* 611:    */     public void removePropertyChangeListener(PropertyChangeListener listener) {}
/* 612:    */   }
/* 613:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SettingsEditor
 * JD-Core Version:    0.7.0.1
 */