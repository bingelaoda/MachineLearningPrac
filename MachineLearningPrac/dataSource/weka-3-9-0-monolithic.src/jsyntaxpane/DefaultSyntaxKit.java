/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GraphicsEnvironment;
/*   7:    */ import java.awt.Toolkit;
/*   8:    */ import java.net.URL;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.Arrays;
/*  11:    */ import java.util.HashMap;
/*  12:    */ import java.util.HashSet;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Map.Entry;
/*  16:    */ import java.util.Properties;
/*  17:    */ import java.util.Set;
/*  18:    */ import java.util.WeakHashMap;
/*  19:    */ import java.util.logging.Level;
/*  20:    */ import java.util.logging.Logger;
/*  21:    */ import java.util.regex.Matcher;
/*  22:    */ import java.util.regex.Pattern;
/*  23:    */ import javax.swing.Action;
/*  24:    */ import javax.swing.ActionMap;
/*  25:    */ import javax.swing.BorderFactory;
/*  26:    */ import javax.swing.ImageIcon;
/*  27:    */ import javax.swing.InputMap;
/*  28:    */ import javax.swing.JButton;
/*  29:    */ import javax.swing.JCheckBoxMenuItem;
/*  30:    */ import javax.swing.JEditorPane;
/*  31:    */ import javax.swing.JMenu;
/*  32:    */ import javax.swing.JMenuItem;
/*  33:    */ import javax.swing.JPopupMenu;
/*  34:    */ import javax.swing.JToolBar;
/*  35:    */ import javax.swing.KeyStroke;
/*  36:    */ import javax.swing.text.DefaultEditorKit;
/*  37:    */ import javax.swing.text.Document;
/*  38:    */ import javax.swing.text.EditorKit;
/*  39:    */ import javax.swing.text.Element;
/*  40:    */ import javax.swing.text.View;
/*  41:    */ import javax.swing.text.ViewFactory;
/*  42:    */ import jsyntaxpane.actions.SyntaxAction;
/*  43:    */ import jsyntaxpane.components.SyntaxComponent;
/*  44:    */ import jsyntaxpane.util.Configuration;
/*  45:    */ import jsyntaxpane.util.Configuration.StringKeyMatcher;
/*  46:    */ import jsyntaxpane.util.JarServiceProvider;
/*  47:    */ 
/*  48:    */ public class DefaultSyntaxKit
/*  49:    */   extends DefaultEditorKit
/*  50:    */   implements ViewFactory
/*  51:    */ {
/*  52:    */   public static final String CONFIG_CARETCOLOR = "CaretColor";
/*  53:    */   public static final String CONFIG_SELECTION = "SelectionColor";
/*  54:    */   public static final String CONFIG_COMPONENTS = "Components";
/*  55:    */   public static final String CONFIG_MENU = "PopupMenu";
/*  56:    */   public static final String CONFIG_TOOLBAR = "Toolbar";
/*  57:    */   public static final String CONFIG_TOOLBAR_ROLLOVER = "Toolbar.Buttons.Rollover";
/*  58:    */   public static final String CONFIG_TOOLBAR_BORDER = "Toolbar.Buttons.BorderPainted";
/*  59:    */   public static final String CONFIG_TOOLBAR_OPAQUE = "Toolbar.Buttons.Opaque";
/*  60:    */   public static final String CONFIG_TOOLBAR_BORDER_SIZE = "Toolbar.Buttons.BorderSize";
/*  61: 80 */   private static final Pattern ACTION_KEY_PATTERN = Pattern.compile("Action\\.((\\w|-)+)");
/*  62: 81 */   private static final Pattern DEFAULT_ACTION_PATTERN = Pattern.compile("(DefaultAction.((\\w|-)+)).*");
/*  63:    */   private static Font DEFAULT_FONT;
/*  64: 83 */   private static Set<String> CONTENT_TYPES = new HashSet();
/*  65: 84 */   private static Boolean initialized = Boolean.valueOf(false);
/*  66:    */   private static Map<String, String> abbrvs;
/*  67: 86 */   private static String MENU_MASK_STRING = "control ";
/*  68:    */   private Lexer lexer;
/*  69: 88 */   private static final Logger LOG = Logger.getLogger(DefaultSyntaxKit.class.getName());
/*  70: 89 */   private Map<JEditorPane, List<SyntaxComponent>> editorComponents = new WeakHashMap();
/*  71: 91 */   private Map<JEditorPane, JPopupMenu> popupMenu = new WeakHashMap();
/*  72:    */   private static Map<Class<? extends DefaultSyntaxKit>, Configuration> CONFIGS;
/*  73:    */   private static final String ACTION_MENU_TEXT = "MenuText";
/*  74:    */   
/*  75:    */   static
/*  76:    */   {
/*  77:100 */     if (!initialized.booleanValue()) {
/*  78:101 */       initKit();
/*  79:    */     }
/*  80:103 */     int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
/*  81:104 */     if (menuMask == 512) {
/*  82:105 */       MENU_MASK_STRING = "alt ";
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public DefaultSyntaxKit(Lexer lexer)
/*  87:    */   {
/*  88:116 */     this.lexer = lexer;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void addComponents(JEditorPane editorPane)
/*  92:    */   {
/*  93:125 */     String[] components = getConfig().getPropertyList("Components");
/*  94:126 */     for (String c : components) {
/*  95:127 */       installComponent(editorPane, c);
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void installComponent(JEditorPane pane, String classname)
/* 100:    */   {
/* 101:    */     try
/* 102:    */     {
/* 103:140 */       Class compClass = Class.forName(classname);
/* 104:141 */       SyntaxComponent comp = (SyntaxComponent)compClass.newInstance();
/* 105:142 */       comp.config(getConfig());
/* 106:143 */       comp.install(pane);
/* 107:144 */       if (this.editorComponents.get(pane) == null) {
/* 108:145 */         this.editorComponents.put(pane, new ArrayList());
/* 109:    */       }
/* 110:147 */       ((List)this.editorComponents.get(pane)).add(comp);
/* 111:    */     }
/* 112:    */     catch (InstantiationException ex)
/* 113:    */     {
/* 114:149 */       LOG.log(Level.SEVERE, null, ex);
/* 115:    */     }
/* 116:    */     catch (IllegalAccessException ex)
/* 117:    */     {
/* 118:151 */       LOG.log(Level.SEVERE, null, ex);
/* 119:    */     }
/* 120:    */     catch (ClassNotFoundException ex)
/* 121:    */     {
/* 122:153 */       LOG.log(Level.SEVERE, null, ex);
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void deinstallComponent(JEditorPane pane, String classname)
/* 127:    */   {
/* 128:165 */     for (SyntaxComponent c : (List)this.editorComponents.get(pane)) {
/* 129:166 */       if (c.getClass().getName().equals(classname))
/* 130:    */       {
/* 131:167 */         c.deinstall(pane);
/* 132:168 */         ((List)this.editorComponents.get(pane)).remove(c);
/* 133:169 */         break;
/* 134:    */       }
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean isComponentInstalled(JEditorPane pane, String classname)
/* 139:    */   {
/* 140:182 */     for (SyntaxComponent c : (List)this.editorComponents.get(pane)) {
/* 141:183 */       if (c.getClass().getName().equals(classname)) {
/* 142:184 */         return true;
/* 143:    */       }
/* 144:    */     }
/* 145:187 */     return false;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public boolean toggleComponent(JEditorPane pane, String classname)
/* 149:    */   {
/* 150:199 */     for (SyntaxComponent c : (List)this.editorComponents.get(pane)) {
/* 151:200 */       if (c.getClass().getName().equals(classname))
/* 152:    */       {
/* 153:201 */         c.deinstall(pane);
/* 154:202 */         ((List)this.editorComponents.get(pane)).remove(c);
/* 155:203 */         return false;
/* 156:    */       }
/* 157:    */     }
/* 158:206 */     installComponent(pane, classname);
/* 159:207 */     return true;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void addPopupMenu(JEditorPane editorPane)
/* 163:    */   {
/* 164:216 */     String[] menuItems = getConfig().getPropertyList("PopupMenu");
/* 165:217 */     if ((menuItems == null) || (menuItems.length == 0)) {
/* 166:218 */       return;
/* 167:    */     }
/* 168:220 */     this.popupMenu.put(editorPane, new JPopupMenu());
/* 169:221 */     JMenu stack = null;
/* 170:222 */     for (String menuString : menuItems) {
/* 171:225 */       if (menuString.equals("-"))
/* 172:    */       {
/* 173:226 */         ((JPopupMenu)this.popupMenu.get(editorPane)).addSeparator();
/* 174:    */       }
/* 175:227 */       else if (menuString.startsWith(">"))
/* 176:    */       {
/* 177:228 */         JMenu sub = new JMenu(menuString.substring(1));
/* 178:229 */         ((JPopupMenu)this.popupMenu.get(editorPane)).add(sub);
/* 179:230 */         stack = sub;
/* 180:    */       }
/* 181:231 */       else if (menuString.startsWith("<"))
/* 182:    */       {
/* 183:232 */         Container parent = stack.getParent();
/* 184:233 */         if ((parent instanceof JMenu))
/* 185:    */         {
/* 186:234 */           JMenu jMenu = (JMenu)parent;
/* 187:235 */           stack = jMenu;
/* 188:    */         }
/* 189:    */         else
/* 190:    */         {
/* 191:237 */           stack = null;
/* 192:    */         }
/* 193:    */       }
/* 194:    */       else
/* 195:    */       {
/* 196:240 */         Action action = editorPane.getActionMap().get(menuString);
/* 197:241 */         if (action != null)
/* 198:    */         {
/* 199:    */           JMenuItem menuItem;
/* 200:    */           JMenuItem menuItem;
/* 201:243 */           if (action.getValue("SwingSelectedKey") != null) {
/* 202:244 */             menuItem = new JCheckBoxMenuItem(action);
/* 203:    */           } else {
/* 204:246 */             menuItem = new JMenuItem(action);
/* 205:    */           }
/* 206:249 */           if (action.getValue("MenuText") != null) {
/* 207:250 */             menuItem.setText((String)action.getValue("MenuText"));
/* 208:    */           }
/* 209:252 */           if (stack == null) {
/* 210:253 */             ((JPopupMenu)this.popupMenu.get(editorPane)).add(menuItem);
/* 211:    */           } else {
/* 212:255 */             stack.add(menuItem);
/* 213:    */           }
/* 214:    */         }
/* 215:    */       }
/* 216:    */     }
/* 217:260 */     editorPane.setComponentPopupMenu((JPopupMenu)this.popupMenu.get(editorPane));
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void addToolBarActions(JEditorPane editorPane, JToolBar toolbar)
/* 221:    */   {
/* 222:274 */     String[] toolBarItems = getConfig().getPropertyList("Toolbar");
/* 223:275 */     if ((toolBarItems == null) || (toolBarItems.length == 0))
/* 224:    */     {
/* 225:276 */       toolBarItems = getConfig().getPropertyList("PopupMenu");
/* 226:277 */       if ((toolBarItems == null) || (toolBarItems.length == 0)) {
/* 227:278 */         return;
/* 228:    */       }
/* 229:    */     }
/* 230:281 */     boolean btnRolloverEnabled = getConfig().getBoolean("Toolbar.Buttons.Rollover", true);
/* 231:282 */     boolean btnBorderPainted = getConfig().getBoolean("Toolbar.Buttons.BorderPainted", false);
/* 232:283 */     boolean btnOpaque = getConfig().getBoolean("Toolbar.Buttons.Opaque", false);
/* 233:284 */     int btnBorderSize = getConfig().getInteger("Toolbar.Buttons.BorderSize", 2);
/* 234:285 */     for (String menuString : toolBarItems) {
/* 235:286 */       if ((menuString.equals("-")) || (menuString.startsWith("<")) || (menuString.startsWith(">")))
/* 236:    */       {
/* 237:289 */         toolbar.addSeparator();
/* 238:    */       }
/* 239:    */       else
/* 240:    */       {
/* 241:291 */         Action action = editorPane.getActionMap().get(menuString);
/* 242:292 */         if ((action != null) && (action.getValue("SmallIcon") != null))
/* 243:    */         {
/* 244:293 */           JButton b = toolbar.add(action);
/* 245:294 */           b.setRolloverEnabled(btnRolloverEnabled);
/* 246:295 */           b.setBorderPainted(btnBorderPainted);
/* 247:296 */           b.setOpaque(btnOpaque);
/* 248:297 */           b.setFocusable(false);
/* 249:298 */           b.setBorder(BorderFactory.createEmptyBorder(btnBorderSize, btnBorderSize, btnBorderSize, btnBorderSize));
/* 250:    */         }
/* 251:    */       }
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public ViewFactory getViewFactory()
/* 256:    */   {
/* 257:307 */     return this;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public View create(Element element)
/* 261:    */   {
/* 262:312 */     return new SyntaxView(element, getConfig());
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void install(JEditorPane editorPane)
/* 266:    */   {
/* 267:324 */     super.install(editorPane);
/* 268:    */     
/* 269:326 */     String fontName = getProperty("DefaultFont");
/* 270:327 */     Font font = DEFAULT_FONT;
/* 271:328 */     if (fontName != null) {
/* 272:329 */       font = Font.decode(fontName);
/* 273:    */     }
/* 274:331 */     editorPane.setFont(font);
/* 275:332 */     Configuration conf = getConfig();
/* 276:333 */     Color caretColor = conf.getColor("CaretColor", Color.BLACK);
/* 277:334 */     editorPane.setCaretColor(caretColor);
/* 278:335 */     Color selectionColor = getConfig().getColor("SelectionColor", new Color(10079487));
/* 279:336 */     editorPane.setSelectionColor(selectionColor);
/* 280:337 */     addActions(editorPane);
/* 281:338 */     addComponents(editorPane);
/* 282:339 */     addPopupMenu(editorPane);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void deinstall(JEditorPane editorPane)
/* 286:    */   {
/* 287:344 */     List<SyntaxComponent> l = (List)this.editorComponents.get(editorPane);
/* 288:345 */     for (SyntaxComponent c : (List)this.editorComponents.get(editorPane)) {
/* 289:346 */       c.deinstall(editorPane);
/* 290:    */     }
/* 291:348 */     this.editorComponents.clear();
/* 292:349 */     editorPane.getInputMap().clear();
/* 293:350 */     editorPane.getActionMap().clear();
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void addActions(JEditorPane editorPane)
/* 297:    */   {
/* 298:360 */     InputMap imap = new InputMap();
/* 299:361 */     imap.setParent(editorPane.getInputMap());
/* 300:362 */     ActionMap amap = new ActionMap();
/* 301:363 */     amap.setParent(editorPane.getActionMap());
/* 302:365 */     for (Configuration.StringKeyMatcher m : getConfig().getKeys(ACTION_KEY_PATTERN))
/* 303:    */     {
/* 304:366 */       String[] values = Configuration.COMMA_SEPARATOR.split(m.value);
/* 305:    */       
/* 306:368 */       String actionClass = values[0];
/* 307:369 */       String actionName = m.group1;
/* 308:370 */       SyntaxAction action = createAction(actionClass);
/* 309:    */       
/* 310:    */ 
/* 311:373 */       action.config(getConfig(), "Action." + actionName);
/* 312:    */       
/* 313:375 */       amap.put(actionName, action);
/* 314:377 */       for (int i = 1; i < values.length; i++)
/* 315:    */       {
/* 316:378 */         String keyStrokeString = values[i].replace("menu ", MENU_MASK_STRING);
/* 317:379 */         KeyStroke ks = KeyStroke.getKeyStroke(keyStrokeString);
/* 318:383 */         if (ks == null) {
/* 319:384 */           throw new IllegalArgumentException("Invalid KeyStroke: " + keyStrokeString);
/* 320:    */         }
/* 321:387 */         action.putValue("AcceleratorKey", ks);
/* 322:388 */         imap.put(ks, actionName);
/* 323:    */       }
/* 324:    */     }
/* 325:393 */     for (Configuration.StringKeyMatcher m : getConfig().getKeys(DEFAULT_ACTION_PATTERN))
/* 326:    */     {
/* 327:394 */       String name = m.matcher.group(2);
/* 328:395 */       Action action = editorPane.getActionMap().get(name);
/* 329:396 */       if (action != null) {
/* 330:397 */         configActionProperties(action, name, m.group1);
/* 331:    */       }
/* 332:    */     }
/* 333:413 */     editorPane.setActionMap(amap);
/* 334:414 */     editorPane.setInputMap(0, imap);
/* 335:    */   }
/* 336:    */   
/* 337:    */   private void configActionProperties(Action action, String actionName, String configKey)
/* 338:    */   {
/* 339:420 */     String iconLoc = getConfig().getString(configKey + ".SmallIcon", actionName + ".png");
/* 340:421 */     URL loc = getClass().getResource("/META-INF/images/small-icons/" + iconLoc);
/* 341:422 */     if (loc != null)
/* 342:    */     {
/* 343:423 */       ImageIcon i = new ImageIcon(loc);
/* 344:424 */       action.putValue("SmallIcon", i);
/* 345:    */     }
/* 346:430 */     String name = getProperty(configKey + ".MenuText");
/* 347:431 */     if (action.getValue("Name") == null) {
/* 348:432 */       action.putValue("Name", name);
/* 349:    */     } else {
/* 350:434 */       action.putValue("MenuText", name);
/* 351:    */     }
/* 352:437 */     String shortDesc = getProperty(configKey + ".ToolTip");
/* 353:438 */     if (shortDesc != null) {
/* 354:439 */       action.putValue("ShortDescription", shortDesc);
/* 355:    */     } else {
/* 356:441 */       action.putValue("ShortDescription", name);
/* 357:    */     }
/* 358:    */   }
/* 359:    */   
/* 360:    */   private SyntaxAction createAction(String actionClassName)
/* 361:    */   {
/* 362:446 */     SyntaxAction action = null;
/* 363:    */     try
/* 364:    */     {
/* 365:448 */       Class clazz = Class.forName(actionClassName);
/* 366:449 */       action = (SyntaxAction)clazz.newInstance();
/* 367:    */     }
/* 368:    */     catch (InstantiationException ex)
/* 369:    */     {
/* 370:451 */       throw new IllegalArgumentException("Cannot create action class: " + actionClassName + ". Ensure it has default constructor.", ex);
/* 371:    */     }
/* 372:    */     catch (IllegalAccessException ex)
/* 373:    */     {
/* 374:454 */       throw new IllegalArgumentException("Cannot create action class: " + actionClassName, ex);
/* 375:    */     }
/* 376:    */     catch (ClassNotFoundException ex)
/* 377:    */     {
/* 378:457 */       throw new IllegalArgumentException("Cannot create action class: " + actionClassName, ex);
/* 379:    */     }
/* 380:    */     catch (ClassCastException ex)
/* 381:    */     {
/* 382:460 */       throw new IllegalArgumentException("Cannot create action class: " + actionClassName, ex);
/* 383:    */     }
/* 384:463 */     return action;
/* 385:    */   }
/* 386:    */   
/* 387:    */   public Document createDefaultDocument()
/* 388:    */   {
/* 389:475 */     return new SyntaxDocument(this.lexer);
/* 390:    */   }
/* 391:    */   
/* 392:    */   public static synchronized void initKit()
/* 393:    */   {
/* 394:488 */     String defaultFont = getConfig(DefaultSyntaxKit.class).getString("DefaultFont");
/* 395:489 */     if (defaultFont != null)
/* 396:    */     {
/* 397:490 */       DEFAULT_FONT = Font.decode(defaultFont);
/* 398:    */     }
/* 399:    */     else
/* 400:    */     {
/* 401:492 */       GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 402:493 */       String[] fonts = ge.getAvailableFontFamilyNames();
/* 403:494 */       Arrays.sort(fonts);
/* 404:495 */       if (Arrays.binarySearch(fonts, "Courier New") >= 0) {
/* 405:496 */         DEFAULT_FONT = new Font("Courier New", 0, 12);
/* 406:497 */       } else if (Arrays.binarySearch(fonts, "Courier") >= 0) {
/* 407:498 */         DEFAULT_FONT = new Font("Courier", 0, 12);
/* 408:499 */       } else if (Arrays.binarySearch(fonts, "Monospaced") >= 0) {
/* 409:500 */         DEFAULT_FONT = new Font("Monospaced", 0, 13);
/* 410:    */       }
/* 411:    */     }
/* 412:505 */     Properties kitsForTypes = JarServiceProvider.readProperties("jsyntaxpane/kitsfortypes");
/* 413:506 */     for (Map.Entry e : kitsForTypes.entrySet())
/* 414:    */     {
/* 415:507 */       String type = e.getKey().toString();
/* 416:508 */       String classname = e.getValue().toString();
/* 417:509 */       registerContentType(type, classname);
/* 418:    */     }
/* 419:511 */     initialized = Boolean.valueOf(true);
/* 420:    */   }
/* 421:    */   
/* 422:    */   public static void registerContentType(String type, String classname)
/* 423:    */   {
/* 424:    */     try
/* 425:    */     {
/* 426:527 */       Class c = Class.forName(classname);
/* 427:    */       
/* 428:    */ 
/* 429:530 */       Object kit = c.newInstance();
/* 430:531 */       if (!(kit instanceof EditorKit)) {
/* 431:532 */         throw new IllegalArgumentException("Cannot register class: " + classname + ". It does not extend EditorKit");
/* 432:    */       }
/* 433:535 */       JEditorPane.registerEditorKitForContentType(type, classname);
/* 434:536 */       CONTENT_TYPES.add(type);
/* 435:    */     }
/* 436:    */     catch (InstantiationException ex)
/* 437:    */     {
/* 438:538 */       throw new IllegalArgumentException("Cannot register class: " + classname + ". Ensure it has Default Constructor.", ex);
/* 439:    */     }
/* 440:    */     catch (IllegalAccessException ex)
/* 441:    */     {
/* 442:541 */       throw new IllegalArgumentException("Cannot register class: " + classname, ex);
/* 443:    */     }
/* 444:    */     catch (ClassNotFoundException ex)
/* 445:    */     {
/* 446:543 */       throw new IllegalArgumentException("Cannot register class: " + classname, ex);
/* 447:    */     }
/* 448:    */     catch (RuntimeException ex)
/* 449:    */     {
/* 450:545 */       throw new IllegalArgumentException("Cannot register class: " + classname, ex);
/* 451:    */     }
/* 452:    */   }
/* 453:    */   
/* 454:    */   public static String[] getContentTypes()
/* 455:    */   {
/* 456:555 */     String[] types = (String[])CONTENT_TYPES.toArray(new String[0]);
/* 457:556 */     Arrays.sort(types);
/* 458:557 */     return types;
/* 459:    */   }
/* 460:    */   
/* 461:    */   public void setConfig(Properties config)
/* 462:    */   {
/* 463:566 */     getConfig().putAll(config);
/* 464:    */   }
/* 465:    */   
/* 466:    */   public void setProperty(String key, String value)
/* 467:    */   {
/* 468:576 */     getConfig().put(key, value);
/* 469:    */   }
/* 470:    */   
/* 471:    */   public String getProperty(String key)
/* 472:    */   {
/* 473:587 */     return getConfig().getString(key);
/* 474:    */   }
/* 475:    */   
/* 476:    */   public Configuration getConfig()
/* 477:    */   {
/* 478:595 */     return getConfig(getClass());
/* 479:    */   }
/* 480:    */   
/* 481:    */   public static synchronized Configuration getConfig(Class<? extends DefaultSyntaxKit> kit)
/* 482:    */   {
/* 483:606 */     if (CONFIGS == null)
/* 484:    */     {
/* 485:607 */       CONFIGS = new WeakHashMap();
/* 486:608 */       Configuration defaultConfig = new Configuration(DefaultSyntaxKit.class);
/* 487:609 */       loadConfig(defaultConfig, DefaultSyntaxKit.class);
/* 488:610 */       CONFIGS.put(DefaultSyntaxKit.class, defaultConfig);
/* 489:    */     }
/* 490:613 */     if (CONFIGS.containsKey(kit)) {
/* 491:614 */       return (Configuration)CONFIGS.get(kit);
/* 492:    */     }
/* 493:617 */     Class superKit = kit.getSuperclass();
/* 494:    */     
/* 495:619 */     Configuration defaults = getConfig(superKit);
/* 496:620 */     Configuration mine = new Configuration(kit, defaults);
/* 497:621 */     loadConfig(mine, kit);
/* 498:622 */     CONFIGS.put(kit, mine);
/* 499:623 */     return mine;
/* 500:    */   }
/* 501:    */   
/* 502:    */   public Map<String, String> getAbbreviations()
/* 503:    */   {
/* 504:629 */     if (abbrvs == null)
/* 505:    */     {
/* 506:630 */       String cl = getClass().getName().replace('.', '/').toLowerCase();
/* 507:631 */       abbrvs = JarServiceProvider.readStringsMap(cl + "/abbreviations.properties");
/* 508:    */     }
/* 509:633 */     return abbrvs;
/* 510:    */   }
/* 511:    */   
/* 512:    */   public void addAbbreviation(String abbr, String template)
/* 513:    */   {
/* 514:637 */     if (abbrvs == null) {
/* 515:638 */       abbrvs = new HashMap();
/* 516:    */     }
/* 517:640 */     abbrvs.put(abbr, template);
/* 518:    */   }
/* 519:    */   
/* 520:    */   public String getAbbreviation(String abbr)
/* 521:    */   {
/* 522:644 */     return abbrvs == null ? null : (String)abbrvs.get(abbr);
/* 523:    */   }
/* 524:    */   
/* 525:    */   private static void loadConfig(Configuration conf, Class<? extends EditorKit> kit)
/* 526:    */   {
/* 527:648 */     String url = kit.getName().replace(".", "/") + "/config";
/* 528:649 */     Properties p = JarServiceProvider.readProperties(url);
/* 529:650 */     if (p.size() == 0) {
/* 530:651 */       LOG.info("unable to load configuration for: " + kit + " from: " + url + ".properties");
/* 531:    */     } else {
/* 532:653 */       conf.putAll(p);
/* 533:    */     }
/* 534:    */   }
/* 535:    */   
/* 536:    */   public String getContentType()
/* 537:    */   {
/* 538:659 */     return "text/" + getClass().getSimpleName().replace("SyntaxKit", "").toLowerCase();
/* 539:    */   }
/* 540:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.DefaultSyntaxKit
 * JD-Core Version:    0.7.0.1
 */