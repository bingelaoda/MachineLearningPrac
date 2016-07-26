/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.beans.PropertyChangeEvent;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.io.File;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import java.util.Vector;
/*  12:    */ import javax.swing.BorderFactory;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JFileChooser;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JOptionPane;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.filechooser.FileFilter;
/*  20:    */ import weka.core.Capabilities;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.core.converters.AbstractFileLoader;
/*  23:    */ import weka.core.converters.AbstractFileSaver;
/*  24:    */ import weka.core.converters.AbstractLoader;
/*  25:    */ import weka.core.converters.AbstractSaver;
/*  26:    */ import weka.core.converters.ArffLoader;
/*  27:    */ import weka.core.converters.ArffSaver;
/*  28:    */ import weka.core.converters.ConverterUtils;
/*  29:    */ import weka.core.converters.FileSourcedConverter;
/*  30:    */ 
/*  31:    */ public class ConverterFileChooser
/*  32:    */   extends JFileChooser
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = -5373058011025481738L;
/*  35:    */   public static final int UNHANDLED_DIALOG = 0;
/*  36:    */   public static final int LOADER_DIALOG = 1;
/*  37:    */   public static final int SAVER_DIALOG = 2;
/*  38:    */   protected ConverterFileChooser m_Self;
/*  39:    */   protected static Vector<ExtensionFileFilter> m_LoaderFileFilters;
/*  40:    */   protected static Vector<ExtensionFileFilter> m_SaverFileFilters;
/*  41:    */   protected int m_DialogType;
/*  42:    */   protected Object m_CurrentConverter;
/*  43:    */   protected JButton m_ConfigureButton;
/*  44:    */   protected PropertyChangeListener m_Listener;
/*  45:    */   protected FileFilter m_LastFilter;
/*  46:    */   protected Capabilities m_CapabilitiesFilter;
/*  47:103 */   protected boolean m_OverwriteWarning = true;
/*  48:106 */   protected boolean m_FileMustExist = true;
/*  49:    */   protected JCheckBox m_CheckBoxOptions;
/*  50:    */   protected JLabel m_LabelOptions;
/*  51:115 */   protected GenericObjectEditor m_Editor = null;
/*  52:    */   protected int m_EditorResult;
/*  53:127 */   protected boolean m_CoreConvertersOnly = false;
/*  54:    */   
/*  55:    */   public static void initDefaultFilters()
/*  56:    */   {
/*  57:137 */     initFilters(true, ConverterUtils.getFileLoaders());
/*  58:138 */     initFilters(false, ConverterUtils.getFileSavers());
/*  59:    */   }
/*  60:    */   
/*  61:    */   public ConverterFileChooser()
/*  62:    */   {
/*  63:146 */     initialize();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public ConverterFileChooser(File currentDirectory)
/*  67:    */   {
/*  68:155 */     super(currentDirectory);
/*  69:156 */     initialize();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public ConverterFileChooser(String currentDirectory)
/*  73:    */   {
/*  74:165 */     super(currentDirectory);
/*  75:166 */     initialize();
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected void initialize()
/*  79:    */   {
/*  80:176 */     this.m_Self = this;
/*  81:    */     
/*  82:178 */     this.m_CheckBoxOptions = new JCheckBox("Invoke options dialog");
/*  83:179 */     this.m_CheckBoxOptions.setMnemonic('I');
/*  84:180 */     this.m_LabelOptions = new JLabel("<html><br>Note:<br><br>Some file formats offer additional<br>options which can be customized<br>when invoking the options dialog.</html>");
/*  85:    */     
/*  86:182 */     JPanel panel = new JPanel(new BorderLayout());
/*  87:183 */     panel.add(this.m_CheckBoxOptions, "North");
/*  88:184 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  89:185 */     panel2.add(this.m_LabelOptions, "North");
/*  90:186 */     panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  91:187 */     panel.add(panel2, "Center");
/*  92:188 */     setAccessory(panel);
/*  93:    */     
/*  94:190 */     this.m_Editor = new GenericObjectEditor(false);
/*  95:191 */     ((GenericObjectEditor.GOEPanel)this.m_Editor.getCustomEditor()).addOkListener(new ActionListener()
/*  96:    */     {
/*  97:    */       public void actionPerformed(ActionEvent e)
/*  98:    */       {
/*  99:195 */         ConverterFileChooser.this.m_EditorResult = 0;
/* 100:196 */         ConverterFileChooser.this.m_CurrentConverter = ConverterFileChooser.this.m_Editor.getValue();
/* 101:    */         try
/* 102:    */         {
/* 103:200 */           ((FileSourcedConverter)ConverterFileChooser.this.m_CurrentConverter).setFile(((FileSourcedConverter)ConverterFileChooser.this.m_CurrentConverter).retrieveFile());
/* 104:    */         }
/* 105:    */         catch (Exception ex) {}
/* 106:    */       }
/* 107:207 */     });
/* 108:208 */     ((GenericObjectEditor.GOEPanel)this.m_Editor.getCustomEditor()).addCancelListener(new ActionListener()
/* 109:    */     {
/* 110:    */       public void actionPerformed(ActionEvent e)
/* 111:    */       {
/* 112:212 */         ConverterFileChooser.this.m_EditorResult = 1;
/* 113:    */       }
/* 114:    */     });
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected Vector<ExtensionFileFilter> filterNonCoreLoaderFileFilters(Vector<ExtensionFileFilter> list)
/* 118:    */   {
/* 119:    */     Vector<ExtensionFileFilter> result;
/* 120:    */     Vector<ExtensionFileFilter> result;
/* 121:231 */     if (!getCoreConvertersOnly())
/* 122:    */     {
/* 123:232 */       result = list;
/* 124:    */     }
/* 125:    */     else
/* 126:    */     {
/* 127:234 */       result = new Vector();
/* 128:235 */       for (int i = 0; i < list.size(); i++)
/* 129:    */       {
/* 130:236 */         ExtensionFileFilter filter = (ExtensionFileFilter)list.get(i);
/* 131:237 */         AbstractLoader loader = ConverterUtils.getLoaderForExtension(filter.getExtensions()[0]);
/* 132:239 */         if (ConverterUtils.isCoreFileLoader(loader.getClass().getName())) {
/* 133:240 */           result.add(filter);
/* 134:    */         }
/* 135:    */       }
/* 136:    */     }
/* 137:245 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected Vector<ExtensionFileFilter> filterNonCoreSaverFileFilters(Vector<ExtensionFileFilter> list)
/* 141:    */   {
/* 142:    */     Vector<ExtensionFileFilter> result;
/* 143:    */     Vector<ExtensionFileFilter> result;
/* 144:262 */     if (!getCoreConvertersOnly())
/* 145:    */     {
/* 146:263 */       result = list;
/* 147:    */     }
/* 148:    */     else
/* 149:    */     {
/* 150:265 */       result = new Vector();
/* 151:266 */       for (int i = 0; i < list.size(); i++)
/* 152:    */       {
/* 153:267 */         ExtensionFileFilter filter = (ExtensionFileFilter)list.get(i);
/* 154:268 */         AbstractSaver saver = ConverterUtils.getSaverForExtension(filter.getExtensions()[0]);
/* 155:269 */         if (ConverterUtils.isCoreFileSaver(saver.getClass().getName())) {
/* 156:270 */           result.add(filter);
/* 157:    */         }
/* 158:    */       }
/* 159:    */     }
/* 160:275 */     return result;
/* 161:    */   }
/* 162:    */   
/* 163:    */   protected Vector<ExtensionFileFilter> filterSaverFileFilters(Vector<ExtensionFileFilter> list)
/* 164:    */   {
/* 165:    */     Vector<ExtensionFileFilter> result;
/* 166:    */     Vector<ExtensionFileFilter> result;
/* 167:292 */     if (this.m_CapabilitiesFilter == null)
/* 168:    */     {
/* 169:293 */       result = list;
/* 170:    */     }
/* 171:    */     else
/* 172:    */     {
/* 173:295 */       result = new Vector();
/* 174:297 */       for (int i = 0; i < list.size(); i++)
/* 175:    */       {
/* 176:298 */         ExtensionFileFilter filter = (ExtensionFileFilter)list.get(i);
/* 177:299 */         AbstractSaver saver = ConverterUtils.getSaverForExtension(filter.getExtensions()[0]);
/* 178:300 */         if (saver.getCapabilities().supports(this.m_CapabilitiesFilter)) {
/* 179:301 */           result.add(filter);
/* 180:    */         }
/* 181:    */       }
/* 182:    */     }
/* 183:306 */     return result;
/* 184:    */   }
/* 185:    */   
/* 186:    */   protected static void initFilters(boolean loader, Vector<String> classnames)
/* 187:    */   {
/* 188:325 */     if (loader) {
/* 189:326 */       m_LoaderFileFilters = new Vector();
/* 190:    */     } else {
/* 191:328 */       m_SaverFileFilters = new Vector();
/* 192:    */     }
/* 193:331 */     for (int i = 0; i < classnames.size(); i++)
/* 194:    */     {
/* 195:332 */       String classname = (String)classnames.get(i);
/* 196:    */       FileSourcedConverter converter;
/* 197:    */       String[] ext;
/* 198:    */       String desc;
/* 199:    */       try
/* 200:    */       {
/* 201:336 */         cls = Class.forName(classname);
/* 202:337 */         converter = (FileSourcedConverter)cls.newInstance();
/* 203:338 */         ext = converter.getFileExtensions();
/* 204:339 */         desc = converter.getFileDescription();
/* 205:    */       }
/* 206:    */       catch (Exception e)
/* 207:    */       {
/* 208:341 */         Class<?> cls = null;
/* 209:342 */         converter = null;
/* 210:343 */         ext = new String[0];
/* 211:344 */         desc = "";
/* 212:    */       }
/* 213:347 */       if (converter != null)
/* 214:    */       {
/* 215:352 */         if (loader) {
/* 216:353 */           for (int n = 0; n < ext.length; n++)
/* 217:    */           {
/* 218:354 */             ExtensionFileFilter filter = new ExtensionFileFilter(ext[n], desc + " (*" + ext[n] + ")");
/* 219:355 */             m_LoaderFileFilters.add(filter);
/* 220:    */           }
/* 221:    */         }
/* 222:358 */         for (int n = 0; n < ext.length; n++)
/* 223:    */         {
/* 224:359 */           ExtensionFileFilter filter = new ExtensionFileFilter(ext[n], desc + " (*" + ext[n] + ")");
/* 225:360 */           m_SaverFileFilters.add(filter);
/* 226:    */         }
/* 227:    */       }
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   protected void initGUI(int dialogType)
/* 232:    */   {
/* 233:377 */     boolean acceptAll = isAcceptAllFileFilterUsed();
/* 234:    */     
/* 235:    */ 
/* 236:380 */     resetChoosableFileFilters();
/* 237:381 */     setAcceptAllFileFilterUsed(acceptAll);
/* 238:    */     Vector<ExtensionFileFilter> list;
/* 239:    */     Vector<ExtensionFileFilter> list;
/* 240:382 */     if (dialogType == 1) {
/* 241:383 */       list = filterNonCoreLoaderFileFilters(m_LoaderFileFilters);
/* 242:    */     } else {
/* 243:385 */       list = filterSaverFileFilters(filterNonCoreSaverFileFilters(m_SaverFileFilters));
/* 244:    */     }
/* 245:387 */     for (int i = 0; i < list.size(); i++) {
/* 246:388 */       addChoosableFileFilter((FileFilter)list.get(i));
/* 247:    */     }
/* 248:390 */     if (list.size() > 0) {
/* 249:391 */       if ((this.m_LastFilter == null) || (!list.contains(this.m_LastFilter))) {
/* 250:392 */         setFileFilter((FileFilter)list.get(0));
/* 251:    */       } else {
/* 252:394 */         setFileFilter(this.m_LastFilter);
/* 253:    */       }
/* 254:    */     }
/* 255:399 */     if (this.m_Listener != null) {
/* 256:400 */       removePropertyChangeListener(this.m_Listener);
/* 257:    */     }
/* 258:402 */     this.m_Listener = new PropertyChangeListener()
/* 259:    */     {
/* 260:    */       public void propertyChange(PropertyChangeEvent evt)
/* 261:    */       {
/* 262:406 */         if (evt.getPropertyName().equals("fileFilterChanged")) {
/* 263:407 */           ConverterFileChooser.this.updateCurrentConverter();
/* 264:    */         }
/* 265:    */       }
/* 266:410 */     };
/* 267:411 */     addPropertyChangeListener(this.m_Listener);
/* 268:414 */     if (dialogType == 1)
/* 269:    */     {
/* 270:415 */       this.m_Editor.setClassType(AbstractFileLoader.class);
/* 271:416 */       this.m_Editor.setValue(new ArffLoader());
/* 272:    */     }
/* 273:    */     else
/* 274:    */     {
/* 275:418 */       this.m_Editor.setClassType(AbstractFileSaver.class);
/* 276:419 */       this.m_Editor.setValue(new ArffSaver());
/* 277:    */     }
/* 278:422 */     updateCurrentConverter();
/* 279:    */   }
/* 280:    */   
/* 281:    */   public void setCapabilitiesFilter(Capabilities value)
/* 282:    */   {
/* 283:432 */     this.m_CapabilitiesFilter = ((Capabilities)value.clone());
/* 284:    */   }
/* 285:    */   
/* 286:    */   public Capabilities getCapabilitiesFilter()
/* 287:    */   {
/* 288:442 */     if (this.m_CapabilitiesFilter != null) {
/* 289:443 */       return (Capabilities)this.m_CapabilitiesFilter.clone();
/* 290:    */     }
/* 291:445 */     return null;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setOverwriteWarning(boolean value)
/* 295:    */   {
/* 296:456 */     this.m_OverwriteWarning = value;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public boolean getOverwriteWarning()
/* 300:    */   {
/* 301:466 */     return this.m_OverwriteWarning;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void setFileMustExist(boolean value)
/* 305:    */   {
/* 306:475 */     this.m_FileMustExist = value;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public boolean getFileMustExist()
/* 310:    */   {
/* 311:484 */     return this.m_FileMustExist;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void setCoreConvertersOnly(boolean value)
/* 315:    */   {
/* 316:495 */     this.m_CoreConvertersOnly = value;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public boolean getCoreConvertersOnly()
/* 320:    */   {
/* 321:506 */     return this.m_CoreConvertersOnly;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public int showDialog(Component parent, String approveButtonText)
/* 325:    */   {
/* 326:519 */     if (this.m_DialogType == 0) {
/* 327:520 */       throw new IllegalStateException("Either use showOpenDialog or showSaveDialog!");
/* 328:    */     }
/* 329:523 */     return super.showDialog(parent, approveButtonText);
/* 330:    */   }
/* 331:    */   
/* 332:    */   public int showOpenDialog(Component parent)
/* 333:    */   {
/* 334:535 */     this.m_DialogType = 1;
/* 335:536 */     this.m_CurrentConverter = null;
/* 336:    */     
/* 337:538 */     initGUI(1);
/* 338:    */     
/* 339:540 */     int result = super.showOpenDialog(parent);
/* 340:    */     
/* 341:542 */     this.m_DialogType = 0;
/* 342:543 */     removePropertyChangeListener(this.m_Listener);
/* 343:546 */     if ((result == 0) && (getSelectedFile().isFile()) && 
/* 344:547 */       ((getFileFilter() instanceof ExtensionFileFilter)))
/* 345:    */     {
/* 346:548 */       String filename = getSelectedFile().getAbsolutePath();
/* 347:549 */       String[] extensions = ((ExtensionFileFilter)getFileFilter()).getExtensions();
/* 348:551 */       if (!filename.endsWith(extensions[0]))
/* 349:    */       {
/* 350:552 */         filename = filename + extensions[0];
/* 351:553 */         setSelectedFile(new File(filename));
/* 352:    */       }
/* 353:    */     }
/* 354:559 */     if ((result == 0) && (getFileMustExist()) && (getSelectedFile().isFile()) && (!getSelectedFile().exists()))
/* 355:    */     {
/* 356:561 */       int retVal = JOptionPane.showConfirmDialog(parent, "The file '" + getSelectedFile() + "' does not exist - please select again!");
/* 357:563 */       if (retVal == 0) {
/* 358:564 */         result = showOpenDialog(parent);
/* 359:    */       } else {
/* 360:566 */         result = 1;
/* 361:    */       }
/* 362:    */     }
/* 363:570 */     if (result == 0)
/* 364:    */     {
/* 365:571 */       this.m_LastFilter = getFileFilter();
/* 366:572 */       configureCurrentConverter(1);
/* 367:575 */       if ((this.m_CheckBoxOptions.isSelected()) && (this.m_CurrentConverter != null))
/* 368:    */       {
/* 369:576 */         this.m_EditorResult = 1;
/* 370:577 */         this.m_Editor.setValue(this.m_CurrentConverter);
/* 371:    */         PropertyDialog pd;
/* 372:    */         PropertyDialog pd;
/* 373:579 */         if (PropertyDialog.getParentDialog(this) != null) {
/* 374:580 */           pd = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_Editor);
/* 375:    */         } else {
/* 376:583 */           pd = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_Editor);
/* 377:    */         }
/* 378:585 */         pd.setVisible(true);
/* 379:586 */         result = this.m_EditorResult;
/* 380:    */       }
/* 381:    */     }
/* 382:590 */     return result;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public int showSaveDialog(Component parent)
/* 386:    */   {
/* 387:601 */     this.m_DialogType = 2;
/* 388:602 */     this.m_CurrentConverter = null;
/* 389:    */     
/* 390:604 */     initGUI(2);
/* 391:    */     
/* 392:606 */     boolean acceptAll = isAcceptAllFileFilterUsed();
/* 393:    */     
/* 394:    */ 
/* 395:    */ 
/* 396:    */ 
/* 397:611 */     FileFilter currentFilter = getFileFilter();
/* 398:612 */     File currentFile = getSelectedFile();
/* 399:613 */     setAcceptAllFileFilterUsed(false);
/* 400:614 */     setFileFilter(currentFilter);
/* 401:615 */     setSelectedFile(currentFile);
/* 402:    */     
/* 403:617 */     int result = super.showSaveDialog(parent);
/* 404:620 */     if ((result == 0) && 
/* 405:621 */       ((getFileFilter() instanceof ExtensionFileFilter)))
/* 406:    */     {
/* 407:622 */       String filename = getSelectedFile().getAbsolutePath();
/* 408:623 */       String[] extensions = ((ExtensionFileFilter)getFileFilter()).getExtensions();
/* 409:625 */       if (!filename.endsWith(extensions[0]))
/* 410:    */       {
/* 411:626 */         filename = filename + extensions[0];
/* 412:627 */         setSelectedFile(new File(filename));
/* 413:    */       }
/* 414:    */     }
/* 415:635 */     currentFilter = getFileFilter();
/* 416:636 */     currentFile = getSelectedFile();
/* 417:637 */     setAcceptAllFileFilterUsed(acceptAll);
/* 418:638 */     setFileFilter(currentFilter);
/* 419:639 */     setSelectedFile(currentFile);
/* 420:    */     
/* 421:641 */     this.m_DialogType = 0;
/* 422:642 */     removePropertyChangeListener(this.m_Listener);
/* 423:645 */     if ((result == 0) && (getOverwriteWarning()) && (getSelectedFile().exists()))
/* 424:    */     {
/* 425:647 */       int retVal = JOptionPane.showConfirmDialog(parent, "The file '" + getSelectedFile() + "' already exists - overwrite it?");
/* 426:649 */       if (retVal == 0) {
/* 427:650 */         result = 0;
/* 428:651 */       } else if (retVal == 1) {
/* 429:652 */         result = showSaveDialog(parent);
/* 430:    */       } else {
/* 431:654 */         result = 1;
/* 432:    */       }
/* 433:    */     }
/* 434:658 */     if (result == 0)
/* 435:    */     {
/* 436:659 */       this.m_LastFilter = getFileFilter();
/* 437:663 */       if (this.m_CheckBoxOptions.isSelected())
/* 438:    */       {
/* 439:664 */         this.m_EditorResult = 1;
/* 440:665 */         this.m_Editor.setValue(this.m_CurrentConverter);
/* 441:    */         PropertyDialog pd;
/* 442:    */         PropertyDialog pd;
/* 443:667 */         if (PropertyDialog.getParentDialog(this) != null) {
/* 444:668 */           pd = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_Editor);
/* 445:    */         } else {
/* 446:671 */           pd = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_Editor);
/* 447:    */         }
/* 448:673 */         pd.setVisible(true);
/* 449:674 */         result = this.m_EditorResult;
/* 450:    */       }
/* 451:    */     }
/* 452:679 */     return result;
/* 453:    */   }
/* 454:    */   
/* 455:    */   public AbstractFileLoader getLoader()
/* 456:    */   {
/* 457:689 */     configureCurrentConverter(1);
/* 458:691 */     if ((this.m_CurrentConverter instanceof AbstractFileSaver)) {
/* 459:692 */       return null;
/* 460:    */     }
/* 461:694 */     return (AbstractFileLoader)this.m_CurrentConverter;
/* 462:    */   }
/* 463:    */   
/* 464:    */   public AbstractFileSaver getSaver()
/* 465:    */   {
/* 466:705 */     configureCurrentConverter(2);
/* 467:707 */     if ((this.m_CurrentConverter instanceof AbstractFileLoader)) {
/* 468:708 */       return null;
/* 469:    */     }
/* 470:710 */     return (AbstractFileSaver)this.m_CurrentConverter;
/* 471:    */   }
/* 472:    */   
/* 473:    */   protected void updateCurrentConverter()
/* 474:    */   {
/* 475:721 */     if (getFileFilter() == null) {
/* 476:722 */       return;
/* 477:    */     }
/* 478:725 */     if (!isAcceptAllFileFilterUsed())
/* 479:    */     {
/* 480:727 */       String[] extensions = ((ExtensionFileFilter)getFileFilter()).getExtensions();
/* 481:    */       Object newConverter;
/* 482:    */       Object newConverter;
/* 483:728 */       if (this.m_DialogType == 1) {
/* 484:729 */         newConverter = ConverterUtils.getLoaderForExtension(extensions[0]);
/* 485:    */       } else {
/* 486:731 */         newConverter = ConverterUtils.getSaverForExtension(extensions[0]);
/* 487:    */       }
/* 488:    */       try
/* 489:    */       {
/* 490:735 */         if (this.m_CurrentConverter == null) {
/* 491:736 */           this.m_CurrentConverter = newConverter;
/* 492:738 */         } else if (!this.m_CurrentConverter.getClass().equals(newConverter.getClass())) {
/* 493:739 */           this.m_CurrentConverter = newConverter;
/* 494:    */         }
/* 495:    */       }
/* 496:    */       catch (Exception e)
/* 497:    */       {
/* 498:743 */         this.m_CurrentConverter = null;
/* 499:744 */         e.printStackTrace();
/* 500:    */       }
/* 501:    */     }
/* 502:    */     else
/* 503:    */     {
/* 504:747 */       this.m_CurrentConverter = null;
/* 505:    */     }
/* 506:    */   }
/* 507:    */   
/* 508:    */   protected void configureCurrentConverter(int dialogType)
/* 509:    */   {
/* 510:760 */     if ((getSelectedFile() == null) || (getSelectedFile().isDirectory())) {
/* 511:761 */       return;
/* 512:    */     }
/* 513:764 */     String filename = getSelectedFile().getAbsolutePath();
/* 514:766 */     if (this.m_CurrentConverter == null)
/* 515:    */     {
/* 516:767 */       if (dialogType == 1) {
/* 517:768 */         this.m_CurrentConverter = ConverterUtils.getLoaderForFile(filename);
/* 518:769 */       } else if (dialogType == 2) {
/* 519:770 */         this.m_CurrentConverter = ConverterUtils.getSaverForFile(filename);
/* 520:    */       } else {
/* 521:772 */         throw new IllegalStateException("Cannot determine loader/saver!");
/* 522:    */       }
/* 523:776 */       if (this.m_CurrentConverter == null) {
/* 524:777 */         return;
/* 525:    */       }
/* 526:    */     }
/* 527:    */     try
/* 528:    */     {
/* 529:782 */       File currFile = ((FileSourcedConverter)this.m_CurrentConverter).retrieveFile();
/* 530:783 */       if ((currFile == null) || (!currFile.getAbsolutePath().equals(filename))) {
/* 531:784 */         ((FileSourcedConverter)this.m_CurrentConverter).setFile(new File(filename));
/* 532:    */       }
/* 533:    */     }
/* 534:    */     catch (Exception e)
/* 535:    */     {
/* 536:787 */       e.printStackTrace();
/* 537:    */     }
/* 538:    */   }
/* 539:    */   
/* 540:    */   public static void main(String[] args)
/* 541:    */     throws Exception
/* 542:    */   {
/* 543:804 */     ConverterFileChooser fc = new ConverterFileChooser();
/* 544:805 */     int retVal = fc.showOpenDialog(null);
/* 545:808 */     if (retVal == 0)
/* 546:    */     {
/* 547:809 */       AbstractFileLoader loader = fc.getLoader();
/* 548:810 */       Instances data = loader.getDataSet();
/* 549:811 */       retVal = fc.showSaveDialog(null);
/* 550:814 */       if (retVal == 0)
/* 551:    */       {
/* 552:815 */         AbstractFileSaver saver = fc.getSaver();
/* 553:816 */         saver.setInstances(data);
/* 554:817 */         saver.writeBatch();
/* 555:    */       }
/* 556:    */       else
/* 557:    */       {
/* 558:819 */         System.out.println("Saving aborted!");
/* 559:    */       }
/* 560:    */     }
/* 561:    */     else
/* 562:    */     {
/* 563:822 */       System.out.println("Loading aborted!");
/* 564:    */     }
/* 565:    */   }
/* 566:    */   
/* 567:    */   static {}
/* 568:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ConverterFileChooser
 * JD-Core Version:    0.7.0.1
 */