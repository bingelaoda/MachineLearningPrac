/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Image;
/*   7:    */ import java.awt.Toolkit;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.image.BufferedImage;
/*  11:    */ import java.io.BufferedReader;
/*  12:    */ import java.io.BufferedWriter;
/*  13:    */ import java.io.File;
/*  14:    */ import java.io.FileReader;
/*  15:    */ import java.io.FileWriter;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.net.URL;
/*  18:    */ import java.util.ArrayList;
/*  19:    */ import java.util.List;
/*  20:    */ import javax.imageio.ImageIO;
/*  21:    */ import javax.swing.BorderFactory;
/*  22:    */ import javax.swing.DefaultListModel;
/*  23:    */ import javax.swing.ImageIcon;
/*  24:    */ import javax.swing.JButton;
/*  25:    */ import javax.swing.JCheckBox;
/*  26:    */ import javax.swing.JEditorPane;
/*  27:    */ import javax.swing.JFileChooser;
/*  28:    */ import javax.swing.JList;
/*  29:    */ import javax.swing.JOptionPane;
/*  30:    */ import javax.swing.JPanel;
/*  31:    */ import javax.swing.JScrollPane;
/*  32:    */ import javax.swing.JSplitPane;
/*  33:    */ import javax.swing.JTabbedPane;
/*  34:    */ import javax.swing.JTextArea;
/*  35:    */ import javax.swing.JToolBar;
/*  36:    */ import javax.swing.SwingUtilities;
/*  37:    */ import javax.swing.event.ListSelectionEvent;
/*  38:    */ import javax.swing.event.ListSelectionListener;
/*  39:    */ import jsyntaxpane.DefaultSyntaxKit;
/*  40:    */ import weka.core.Instances;
/*  41:    */ import weka.core.WekaException;
/*  42:    */ import weka.gui.explorer.Explorer;
/*  43:    */ import weka.gui.explorer.PreprocessPanel;
/*  44:    */ import weka.python.PythonSession;
/*  45:    */ 
/*  46:    */ public class PythonPanel
/*  47:    */   extends JPanel
/*  48:    */ {
/*  49:    */   private static final long serialVersionUID = -6294585211141702994L;
/*  50:    */   protected static final String ICON_PATH = "weka/gui/icons/";
/*  51:    */   protected Explorer m_explorer;
/*  52:    */   protected JEditorPane m_scriptEditor;
/*  53: 88 */   protected JList<String> m_pyVarList = new JList(new DefaultListModel());
/*  54: 92 */   protected JButton m_executeScriptBut = new JButton("Execute script");
/*  55: 95 */   protected JButton m_getVarAsText = new JButton("Get text");
/*  56: 98 */   protected JCheckBox m_debug = new JCheckBox("Output debug info to log");
/*  57:104 */   protected JButton m_getVarAsInstances = new JButton("Get instances");
/*  58:107 */   protected JButton m_getVarAsImage = new JButton("Get image");
/*  59:110 */   protected List<JTextArea> m_textOutputBuffers = new ArrayList();
/*  60:113 */   protected List<ImageDisplayer> m_imageOutputBuffers = new ArrayList();
/*  61:117 */   protected final JFileChooser m_fileChooser = new JFileChooser();
/*  62:120 */   protected Logger m_logPanel = new LogPanel(null, true, false, false);
/*  63:123 */   protected JTabbedPane m_outputTabs = new JTabbedPane();
/*  64:126 */   protected JTabbedPane m_imageTabs = new JTabbedPane();
/*  65:129 */   private boolean m_pyAvailable = true;
/*  66:    */   private String m_envEvalResults;
/*  67:    */   private Exception m_envEvalException;
/*  68:    */   
/*  69:    */   private class ImageDisplayer
/*  70:    */     extends JPanel
/*  71:    */   {
/*  72:    */     private static final long serialVersionUID = 4161957589912537357L;
/*  73:    */     private BufferedImage m_image;
/*  74:    */     
/*  75:    */     private ImageDisplayer() {}
/*  76:    */     
/*  77:    */     public void setImage(BufferedImage image)
/*  78:    */     {
/*  79:160 */       this.m_image = image;
/*  80:    */     }
/*  81:    */     
/*  82:    */     public BufferedImage getImage()
/*  83:    */     {
/*  84:169 */       return this.m_image;
/*  85:    */     }
/*  86:    */     
/*  87:    */     public void paintComponent(Graphics g)
/*  88:    */     {
/*  89:179 */       super.paintComponent(g);
/*  90:181 */       if (this.m_image != null)
/*  91:    */       {
/*  92:182 */         int plotWidth = this.m_image.getWidth();
/*  93:183 */         int plotHeight = this.m_image.getHeight();
/*  94:    */         
/*  95:185 */         int ourWidth = getWidth();
/*  96:186 */         int ourHeight = getHeight();
/*  97:    */         
/*  98:    */ 
/*  99:189 */         int x = 0;int y = 0;
/* 100:190 */         if (plotWidth < ourWidth) {
/* 101:191 */           x = (ourWidth - plotWidth) / 2;
/* 102:    */         }
/* 103:193 */         if (plotHeight < ourHeight) {
/* 104:194 */           y = (ourHeight - plotHeight) / 2;
/* 105:    */         }
/* 106:197 */         g.drawImage(this.m_image, x, y, this);
/* 107:    */       }
/* 108:    */     }
/* 109:    */     
/* 110:    */     public Dimension getPreferredSize()
/* 111:    */     {
/* 112:203 */       Dimension d = super.getPreferredSize();
/* 113:204 */       if (this.m_image != null)
/* 114:    */       {
/* 115:205 */         int plotWidth = this.m_image.getWidth();
/* 116:206 */         int plotHeight = this.m_image.getHeight();
/* 117:    */         
/* 118:208 */         d.setSize(plotWidth, plotHeight);
/* 119:    */       }
/* 120:210 */       return d;
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected JSplitPane createEditorAndVarPanel()
/* 125:    */   {
/* 126:220 */     this.m_fileChooser.setAcceptAllFileFilterUsed(true);
/* 127:221 */     this.m_fileChooser.setMultiSelectionEnabled(false);
/* 128:222 */     DefaultSyntaxKit.initKit();
/* 129:223 */     this.m_scriptEditor = new JEditorPane();
/* 130:224 */     JPanel scriptHolder = new JPanel(new BorderLayout());
/* 131:225 */     JToolBar toolBar = new JToolBar();
/* 132:226 */     toolBar.setOrientation(0);
/* 133:227 */     JButton newB = new JButton(new ImageIcon(loadIcon("weka/gui/icons/page_add.png").getImage()));
/* 134:    */     
/* 135:    */ 
/* 136:230 */     toolBar.add(newB);
/* 137:231 */     newB.addActionListener(new ActionListener()
/* 138:    */     {
/* 139:    */       public void actionPerformed(ActionEvent e)
/* 140:    */       {
/* 141:234 */         PythonPanel.this.m_scriptEditor.setText("");
/* 142:    */       }
/* 143:236 */     });
/* 144:237 */     newB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 145:238 */     newB.setToolTipText("New (clear script)");
/* 146:239 */     JButton loadB = new JButton(new ImageIcon(loadIcon("weka/gui/icons/folder_add.png").getImage()));
/* 147:    */     
/* 148:    */ 
/* 149:242 */     loadB.addActionListener(new ActionListener()
/* 150:    */     {
/* 151:    */       public void actionPerformed(ActionEvent e)
/* 152:    */       {
/* 153:245 */         int retVal = PythonPanel.this.m_fileChooser.showOpenDialog(PythonPanel.this);
/* 154:246 */         if (retVal == 0)
/* 155:    */         {
/* 156:248 */           StringBuilder sb = new StringBuilder();
/* 157:    */           try
/* 158:    */           {
/* 159:250 */             BufferedReader br = new BufferedReader(new FileReader(PythonPanel.this.m_fileChooser.getSelectedFile()));
/* 160:    */             String line;
/* 161:254 */             while ((line = br.readLine()) != null) {
/* 162:255 */               sb.append(line).append("\n");
/* 163:    */             }
/* 164:257 */             PythonPanel.this.m_scriptEditor.setText(sb.toString());
/* 165:258 */             br.close();
/* 166:    */           }
/* 167:    */           catch (Exception ex)
/* 168:    */           {
/* 169:260 */             JOptionPane.showMessageDialog(PythonPanel.this, "Couldn't open file '" + PythonPanel.this.m_fileChooser.getSelectedFile() + "'!");
/* 170:    */             
/* 171:262 */             ex.printStackTrace();
/* 172:    */           }
/* 173:    */         }
/* 174:    */       }
/* 175:266 */     });
/* 176:267 */     loadB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 177:268 */     loadB.setToolTipText("Load a script");
/* 178:269 */     toolBar.add(loadB);
/* 179:270 */     JButton saveB = new JButton(new ImageIcon(loadIcon("weka/gui/icons/disk.png").getImage()));
/* 180:    */     
/* 181:272 */     saveB.addActionListener(new ActionListener()
/* 182:    */     {
/* 183:    */       public void actionPerformed(ActionEvent e)
/* 184:    */       {
/* 185:276 */         if ((PythonPanel.this.m_scriptEditor.getText() != null) && (PythonPanel.this.m_scriptEditor.getText().length() > 0))
/* 186:    */         {
/* 187:279 */           int retVal = PythonPanel.this.m_fileChooser.showSaveDialog(PythonPanel.this);
/* 188:280 */           if (retVal == 0) {
/* 189:    */             try
/* 190:    */             {
/* 191:282 */               BufferedWriter bw = new BufferedWriter(new FileWriter(PythonPanel.this.m_fileChooser.getSelectedFile()));
/* 192:    */               
/* 193:    */ 
/* 194:285 */               bw.write(PythonPanel.this.m_scriptEditor.getText());
/* 195:286 */               bw.flush();
/* 196:287 */               bw.close();
/* 197:    */             }
/* 198:    */             catch (Exception ex)
/* 199:    */             {
/* 200:289 */               JOptionPane.showMessageDialog(PythonPanel.this, "Unable to save script file '" + PythonPanel.this.m_fileChooser.getSelectedFile() + "'!");
/* 201:    */               
/* 202:    */ 
/* 203:    */ 
/* 204:293 */               ex.printStackTrace();
/* 205:    */             }
/* 206:    */           }
/* 207:    */         }
/* 208:    */       }
/* 209:298 */     });
/* 210:299 */     saveB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 211:300 */     saveB.setToolTipText("Save script");
/* 212:301 */     toolBar.add(saveB);
/* 213:302 */     scriptHolder.add(toolBar, "North");
/* 214:303 */     scriptHolder.add(new JScrollPane(this.m_scriptEditor), "Center");
/* 215:304 */     scriptHolder.setBorder(BorderFactory.createTitledBorder("Python script"));
/* 216:305 */     JPanel butHolder1 = new JPanel();
/* 217:306 */     butHolder1.add(this.m_executeScriptBut);
/* 218:307 */     butHolder1.add(this.m_debug);
/* 219:308 */     scriptHolder.add(butHolder1, "South");
/* 220:309 */     this.m_scriptEditor.setContentType("text/python");
/* 221:310 */     JPanel varHolder = new JPanel(new BorderLayout());
/* 222:311 */     varHolder.setBorder(BorderFactory.createTitledBorder("Python variables"));
/* 223:312 */     varHolder.add(new JScrollPane(this.m_pyVarList), "Center");
/* 224:313 */     JPanel butHolder2 = new JPanel();
/* 225:314 */     butHolder2.add(this.m_getVarAsText);
/* 226:315 */     if (this.m_explorer != null) {
/* 227:316 */       butHolder2.add(this.m_getVarAsInstances);
/* 228:    */     }
/* 229:318 */     butHolder2.add(this.m_getVarAsImage);
/* 230:319 */     varHolder.add(butHolder2, "South");
/* 231:    */     
/* 232:321 */     JSplitPane pane = new JSplitPane(0, scriptHolder, varHolder);
/* 233:    */     
/* 234:    */ 
/* 235:324 */     this.m_getVarAsText.setEnabled(false);
/* 236:325 */     this.m_getVarAsImage.setEnabled(false);
/* 237:326 */     this.m_getVarAsInstances.setEnabled(false);
/* 238:    */     
/* 239:328 */     pane.setResizeWeight(0.8D);
/* 240:    */     
/* 241:330 */     Dimension d = new Dimension(450, 200);
/* 242:331 */     this.m_scriptEditor.setMinimumSize(d);
/* 243:    */     
/* 244:333 */     return pane;
/* 245:    */   }
/* 246:    */   
/* 247:    */   protected JSplitPane createOutputPanel()
/* 248:    */   {
/* 249:342 */     JPanel outputHolder = new JPanel(new BorderLayout());
/* 250:343 */     outputHolder.add(this.m_outputTabs, "Center");
/* 251:344 */     outputHolder.setBorder(BorderFactory.createTitledBorder("Output"));
/* 252:345 */     JToolBar outputToolBar = new JToolBar();
/* 253:346 */     outputToolBar.setOrientation(0);
/* 254:347 */     JButton saveOutputB = new JButton(new ImageIcon(loadIcon("weka/gui/icons/disk.png").getImage()));
/* 255:    */     
/* 256:349 */     saveOutputB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 257:350 */     saveOutputB.setToolTipText("Save output");
/* 258:351 */     outputToolBar.add(saveOutputB);
/* 259:    */     
/* 260:353 */     saveOutputB.addActionListener(new ActionListener()
/* 261:    */     {
/* 262:    */       public void actionPerformed(ActionEvent e)
/* 263:    */       {
/* 264:356 */         if (PythonPanel.this.m_outputTabs.getTabCount() > 0)
/* 265:    */         {
/* 266:358 */           int retVal = PythonPanel.this.m_fileChooser.showSaveDialog(PythonPanel.this);
/* 267:359 */           if (retVal == 0) {
/* 268:    */             try
/* 269:    */             {
/* 270:361 */               String outputToSave = ((JTextArea)PythonPanel.this.m_textOutputBuffers.get(PythonPanel.this.m_outputTabs.getSelectedIndex())).getText();
/* 271:    */               
/* 272:    */ 
/* 273:364 */               BufferedWriter bw = new BufferedWriter(new FileWriter(PythonPanel.this.m_fileChooser.getSelectedFile()));
/* 274:    */               
/* 275:    */ 
/* 276:367 */               bw.write(outputToSave);
/* 277:368 */               bw.flush();
/* 278:369 */               bw.close();
/* 279:    */             }
/* 280:    */             catch (Exception ex)
/* 281:    */             {
/* 282:371 */               JOptionPane.showMessageDialog(PythonPanel.this, "Unable to save output buffer '" + PythonPanel.this.m_outputTabs.getTitleAt(PythonPanel.this.m_outputTabs.getSelectedIndex()) + "'!");
/* 283:    */               
/* 284:    */ 
/* 285:    */ 
/* 286:    */ 
/* 287:376 */               ex.printStackTrace();
/* 288:    */             }
/* 289:    */           }
/* 290:    */         }
/* 291:    */       }
/* 292:382 */     });
/* 293:383 */     outputHolder.add(outputToolBar, "North");
/* 294:    */     
/* 295:385 */     JPanel imageHolder = new JPanel(new BorderLayout());
/* 296:386 */     imageHolder.add(this.m_imageTabs, "Center");
/* 297:387 */     imageHolder.setBorder(BorderFactory.createTitledBorder("Plots"));
/* 298:388 */     JToolBar imageToolBar = new JToolBar();
/* 299:389 */     imageToolBar.setOrientation(0);
/* 300:390 */     JButton saveImageB = new JButton(new ImageIcon(loadIcon("weka/gui/icons/disk.png").getImage()));
/* 301:    */     
/* 302:392 */     saveImageB.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
/* 303:393 */     saveImageB.setToolTipText("Save image");
/* 304:394 */     imageToolBar.add(saveImageB);
/* 305:395 */     saveImageB.addActionListener(new ActionListener()
/* 306:    */     {
/* 307:    */       public void actionPerformed(ActionEvent e)
/* 308:    */       {
/* 309:398 */         if (PythonPanel.this.m_outputTabs.getTabCount() > 0)
/* 310:    */         {
/* 311:399 */           int retVal = PythonPanel.this.m_fileChooser.showSaveDialog(PythonPanel.this);
/* 312:400 */           if (retVal == 0) {
/* 313:    */             try
/* 314:    */             {
/* 315:402 */               String fileName = PythonPanel.this.m_fileChooser.getSelectedFile().toString();
/* 316:403 */               if (!fileName.toLowerCase().endsWith(".png")) {
/* 317:404 */                 fileName = fileName + ".png";
/* 318:    */               }
/* 319:406 */               ImageIO.write(((PythonPanel.ImageDisplayer)PythonPanel.this.m_imageOutputBuffers.get(PythonPanel.this.m_outputTabs.getSelectedIndex())).getImage(), "png", new File(fileName));
/* 320:    */             }
/* 321:    */             catch (Exception ex)
/* 322:    */             {
/* 323:410 */               JOptionPane.showMessageDialog(PythonPanel.this, "Unable to save image '" + PythonPanel.this.m_imageTabs.getTitleAt(PythonPanel.this.m_imageTabs.getSelectedIndex()) + "'!");
/* 324:    */               
/* 325:    */ 
/* 326:    */ 
/* 327:    */ 
/* 328:415 */               ex.printStackTrace();
/* 329:    */             }
/* 330:    */           }
/* 331:    */         }
/* 332:    */       }
/* 333:420 */     });
/* 334:421 */     imageHolder.add(imageToolBar, "North");
/* 335:422 */     JSplitPane pane = new JSplitPane(0, outputHolder, imageHolder);
/* 336:    */     
/* 337:    */ 
/* 338:425 */     pane.setResizeWeight(0.5D);
/* 339:426 */     pane.setOneTouchExpandable(true);
/* 340:427 */     return pane;
/* 341:    */   }
/* 342:    */   
/* 343:    */   public PythonPanel(boolean displayLogger, Explorer explorer)
/* 344:    */   {
/* 345:440 */     this.m_explorer = explorer;
/* 346:441 */     setLayout(new BorderLayout());
/* 347:442 */     this.m_getVarAsText.setToolTipText("Get the value of the selected variable in textual form");
/* 348:    */     
/* 349:444 */     this.m_getVarAsImage.setToolTipText("Get the value of the selected variable as a png image");
/* 350:    */     
/* 351:    */ 
/* 352:447 */     this.m_getVarAsInstances.setToolTipText("Get the selected variable (must be a DataFrame) as a set of instances. Gets passed to the Preprocess panel of the Explorer");
/* 353:    */     
/* 354:    */ 
/* 355:    */ 
/* 356:    */ 
/* 357:    */ 
/* 358:453 */     this.m_pyAvailable = true;
/* 359:454 */     if (!PythonSession.pythonAvailable()) {
/* 360:    */       try
/* 361:    */       {
/* 362:457 */         if (!PythonSession.initSession("python", this.m_debug.isSelected()))
/* 363:    */         {
/* 364:458 */           this.m_envEvalResults = PythonSession.getPythonEnvCheckResults();
/* 365:459 */           this.m_pyAvailable = false;
/* 366:    */         }
/* 367:    */       }
/* 368:    */       catch (Exception ex)
/* 369:    */       {
/* 370:462 */         this.m_envEvalException = ex;
/* 371:463 */         this.m_pyAvailable = false;
/* 372:464 */         ex.printStackTrace();
/* 373:465 */         logMessage(null, ex);
/* 374:    */       }
/* 375:    */     }
/* 376:469 */     if (!this.m_pyAvailable)
/* 377:    */     {
/* 378:470 */       System.err.println("Python is not available!!\n\n" + this.m_envEvalResults != null ? this.m_envEvalResults : "");
/* 379:473 */       if (this.m_logPanel != null)
/* 380:    */       {
/* 381:474 */         logMessage("Python is not available!!", this.m_envEvalException);
/* 382:475 */         if (this.m_envEvalResults != null) {
/* 383:476 */           logMessage(this.m_envEvalResults, null);
/* 384:    */         }
/* 385:    */       }
/* 386:    */     }
/* 387:481 */     JSplitPane scriptAndVars = createEditorAndVarPanel();
/* 388:482 */     JSplitPane output = createOutputPanel();
/* 389:483 */     JSplitPane pane = new JSplitPane(1, scriptAndVars, output);
/* 390:    */     
/* 391:485 */     pane.setOneTouchExpandable(true);
/* 392:486 */     add(pane, "Center");
/* 393:487 */     if (displayLogger) {
/* 394:488 */       add((JPanel)this.m_logPanel, "South");
/* 395:    */     }
/* 396:491 */     if (this.m_pyAvailable)
/* 397:    */     {
/* 398:492 */       this.m_pyVarList.addListSelectionListener(new ListSelectionListener()
/* 399:    */       {
/* 400:    */         public void valueChanged(ListSelectionEvent e)
/* 401:    */         {
/* 402:495 */           int index = PythonPanel.this.m_pyVarList.getSelectedIndex();
/* 403:496 */           PythonPanel.this.m_getVarAsImage.setEnabled(false);
/* 404:497 */           PythonPanel.this.m_getVarAsInstances.setEnabled(false);
/* 405:498 */           PythonPanel.this.m_getVarAsText.setEnabled(false);
/* 406:499 */           if (index >= 0)
/* 407:    */           {
/* 408:500 */             String entry = (String)PythonPanel.this.m_pyVarList.getSelectedValue();
/* 409:501 */             String varType = entry.split(":")[1].trim();
/* 410:502 */             PythonPanel.this.m_getVarAsText.setEnabled(true);
/* 411:503 */             if (varType.toLowerCase().startsWith("dataframe")) {
/* 412:504 */               PythonPanel.this.m_getVarAsInstances.setEnabled(true);
/* 413:    */             }
/* 414:506 */             if (varType.toLowerCase().startsWith("figure")) {
/* 415:507 */               PythonPanel.this.m_getVarAsImage.setEnabled(true);
/* 416:    */             }
/* 417:    */           }
/* 418:    */         }
/* 419:512 */       });
/* 420:513 */       this.m_executeScriptBut.addActionListener(new ActionListener()
/* 421:    */       {
/* 422:    */         public void actionPerformed(ActionEvent e)
/* 423:    */         {
/* 424:516 */           Runnable newT = new Runnable()
/* 425:    */           {
/* 426:    */             public void run()
/* 427:    */             {
/* 428:    */               try
/* 429:    */               {
/* 430:520 */                 PythonSession session = PythonSession.acquireSession(PythonPanel.this);
/* 431:521 */                 List<String> outAndErr = session.executeScript(PythonPanel.this.m_scriptEditor.getText(), PythonPanel.this.m_debug.isSelected());
/* 432:524 */                 if (((String)outAndErr.get(0)).length() > 0) {
/* 433:525 */                   PythonPanel.this.logMessage((String)outAndErr.get(0), null);
/* 434:    */                 }
/* 435:527 */                 if (((String)outAndErr.get(1)).length() > 0) {
/* 436:528 */                   throw new WekaException((String)outAndErr.get(1));
/* 437:    */                 }
/* 438:530 */                 PythonPanel.this.refreshVarList(session);
/* 439:531 */                 PythonPanel.this.checkDebug(session);
/* 440:532 */                 PythonPanel.this.m_logPanel.statusMessage("OK");
/* 441:    */               }
/* 442:    */               catch (WekaException ex)
/* 443:    */               {
/* 444:534 */                 ex.printStackTrace();
/* 445:535 */                 PythonPanel.this.logMessage(null, ex);
/* 446:536 */                 PythonPanel.this.m_logPanel.statusMessage("An error occurred. See log.");
/* 447:    */               }
/* 448:    */               finally
/* 449:    */               {
/* 450:538 */                 PythonSession.releaseSession(PythonPanel.this);
/* 451:539 */                 PythonPanel.this.m_executeScriptBut.setEnabled(true);
/* 452:540 */                 PythonPanel.this.revalidate();
/* 453:    */               }
/* 454:    */             }
/* 455:543 */           };
/* 456:544 */           PythonPanel.this.m_executeScriptBut.setEnabled(false);
/* 457:545 */           PythonPanel.this.m_logPanel.statusMessage("Executing script...");
/* 458:546 */           SwingUtilities.invokeLater(newT);
/* 459:    */         }
/* 460:549 */       });
/* 461:550 */       this.m_getVarAsInstances.addActionListener(new ActionListener()
/* 462:    */       {
/* 463:    */         public void actionPerformed(ActionEvent e)
/* 464:    */         {
/* 465:    */           try
/* 466:    */           {
/* 467:555 */             PythonSession session = PythonSession.acquireSession(PythonPanel.this);
/* 468:556 */             if (PythonPanel.this.m_logPanel != null) {
/* 469:557 */               session.setLog(PythonPanel.this.m_logPanel);
/* 470:    */             }
/* 471:559 */             String varToGet = (String)PythonPanel.this.m_pyVarList.getSelectedValue();
/* 472:560 */             varToGet = varToGet.split(":")[0].trim();
/* 473:561 */             if (varToGet.length() > 0)
/* 474:    */             {
/* 475:562 */               Instances instances = session.getDataFrameAsInstances(varToGet, PythonPanel.this.m_debug.isSelected());
/* 476:    */               
/* 477:564 */               PythonPanel.this.m_explorer.getPreprocessPanel().setInstances(instances);
/* 478:    */             }
/* 479:566 */             PythonPanel.this.checkDebug(session);
/* 480:    */           }
/* 481:    */           catch (WekaException ex)
/* 482:    */           {
/* 483:568 */             ex.printStackTrace();
/* 484:569 */             PythonPanel.this.logMessage(null, ex);
/* 485:    */           }
/* 486:    */           finally
/* 487:    */           {
/* 488:571 */             PythonSession.releaseSession(PythonPanel.this);
/* 489:    */           }
/* 490:    */         }
/* 491:575 */       });
/* 492:576 */       this.m_getVarAsText.addActionListener(new ActionListener()
/* 493:    */       {
/* 494:    */         public void actionPerformed(ActionEvent e)
/* 495:    */         {
/* 496:    */           try
/* 497:    */           {
/* 498:581 */             PythonSession session = PythonSession.acquireSession(PythonPanel.this);
/* 499:582 */             if (PythonPanel.this.m_logPanel != null) {
/* 500:583 */               session.setLog(PythonPanel.this.m_logPanel);
/* 501:    */             }
/* 502:585 */             String varToGet = (String)PythonPanel.this.m_pyVarList.getSelectedValue();
/* 503:586 */             varToGet = varToGet.split(":")[0].trim();
/* 504:587 */             if (varToGet.length() > 0)
/* 505:    */             {
/* 506:588 */               String result = session.getVariableValueFromPythonAsPlainString(varToGet, PythonPanel.this.m_debug.isSelected());
/* 507:    */               
/* 508:    */ 
/* 509:591 */               JTextArea textArea = PythonPanel.this.getNamedTextOutput(varToGet);
/* 510:592 */               if (textArea == null)
/* 511:    */               {
/* 512:593 */                 textArea = new JTextArea();
/* 513:594 */                 textArea.setFont(PythonPanel.this.m_scriptEditor.getFont());
/* 514:595 */                 textArea.setEditable(false);
/* 515:596 */                 PythonPanel.this.m_outputTabs.addTab(varToGet, new JScrollPane(textArea));
/* 516:597 */                 PythonPanel.this.m_textOutputBuffers.add(textArea);
/* 517:598 */                 PythonPanel.this.m_outputTabs.setTabComponentAt(PythonPanel.this.m_outputTabs.getTabCount() - 1, new CloseableTabTitle(PythonPanel.this.m_outputTabs, null, new CloseableTabTitle.ClosingCallback()
/* 518:    */                 {
/* 519:    */                   public void tabClosing(int tabIndex)
/* 520:    */                   {
/* 521:603 */                     PythonPanel.this.m_outputTabs.remove(tabIndex);
/* 522:604 */                     PythonPanel.this.m_textOutputBuffers.remove(tabIndex);
/* 523:    */                   }
/* 524:    */                 }));
/* 525:    */               }
/* 526:608 */               textArea.setText(result);
/* 527:609 */               PythonPanel.this.setVisibleTab(PythonPanel.this.m_outputTabs, varToGet);
/* 528:610 */               PythonPanel.this.checkDebug(session);
/* 529:    */             }
/* 530:    */           }
/* 531:    */           catch (WekaException ex)
/* 532:    */           {
/* 533:613 */             ex.printStackTrace();
/* 534:614 */             PythonPanel.this.logMessage(null, ex);
/* 535:    */           }
/* 536:    */           finally
/* 537:    */           {
/* 538:616 */             PythonSession.releaseSession(PythonPanel.this);
/* 539:    */           }
/* 540:    */         }
/* 541:620 */       });
/* 542:621 */       this.m_getVarAsImage.addActionListener(new ActionListener()
/* 543:    */       {
/* 544:    */         public void actionPerformed(ActionEvent e)
/* 545:    */         {
/* 546:    */           try
/* 547:    */           {
/* 548:626 */             PythonSession session = PythonSession.acquireSession(PythonPanel.this);
/* 549:627 */             if (PythonPanel.this.m_logPanel != null) {
/* 550:628 */               session.setLog(PythonPanel.this.m_logPanel);
/* 551:    */             }
/* 552:630 */             String varToGet = (String)PythonPanel.this.m_pyVarList.getSelectedValue();
/* 553:631 */             varToGet = varToGet.split(":")[0].trim();
/* 554:632 */             if (varToGet.length() > 0)
/* 555:    */             {
/* 556:633 */               BufferedImage result = session.getImageFromPython(varToGet, PythonPanel.this.m_debug.isSelected());
/* 557:    */               
/* 558:635 */               PythonPanel.ImageDisplayer displayer = PythonPanel.this.getNamedImageOutput(varToGet);
/* 559:636 */               if (displayer == null)
/* 560:    */               {
/* 561:637 */                 displayer = new PythonPanel.ImageDisplayer(PythonPanel.this, null);
/* 562:638 */                 PythonPanel.this.m_imageTabs.addTab(varToGet, new JScrollPane(displayer));
/* 563:639 */                 PythonPanel.this.m_imageOutputBuffers.add(displayer);
/* 564:640 */                 PythonPanel.this.m_imageTabs.setTabComponentAt(PythonPanel.this.m_imageTabs.getTabCount() - 1, new CloseableTabTitle(PythonPanel.this.m_imageTabs, null, new CloseableTabTitle.ClosingCallback()
/* 565:    */                 {
/* 566:    */                   public void tabClosing(int tabIndex)
/* 567:    */                   {
/* 568:645 */                     PythonPanel.this.m_imageTabs.remove(tabIndex);
/* 569:646 */                     PythonPanel.this.m_imageOutputBuffers.remove(tabIndex);
/* 570:    */                   }
/* 571:    */                 }));
/* 572:    */               }
/* 573:650 */               displayer.setImage(result);
/* 574:651 */               displayer.repaint();
/* 575:652 */               PythonPanel.this.setVisibleTab(PythonPanel.this.m_imageTabs, varToGet);
/* 576:653 */               PythonPanel.this.checkDebug(session);
/* 577:    */             }
/* 578:    */           }
/* 579:    */           catch (WekaException ex)
/* 580:    */           {
/* 581:656 */             ex.printStackTrace();
/* 582:657 */             PythonPanel.this.logMessage(null, ex);
/* 583:    */           }
/* 584:    */           finally
/* 585:    */           {
/* 586:659 */             PythonSession.releaseSession(PythonPanel.this);
/* 587:    */           }
/* 588:    */         }
/* 589:    */       });
/* 590:    */     }
/* 591:    */     else
/* 592:    */     {
/* 593:664 */       this.m_executeScriptBut.setEnabled(false);
/* 594:665 */       JTextArea textArea = new JTextArea();
/* 595:666 */       textArea.setFont(this.m_scriptEditor.getFont());
/* 596:667 */       textArea.setEditable(false);
/* 597:668 */       StringBuilder b = new StringBuilder();
/* 598:669 */       b.append("The python environment is not available:\n\n");
/* 599:670 */       if ((this.m_envEvalResults != null) && (this.m_envEvalResults.length() > 0)) {
/* 600:671 */         b.append(this.m_envEvalResults).append("\n\n");
/* 601:    */       }
/* 602:673 */       if (this.m_envEvalException != null) {
/* 603:674 */         b.append(this.m_envEvalException.getMessage());
/* 604:    */       }
/* 605:676 */       textArea.setText(b.toString());
/* 606:677 */       this.m_outputTabs.addTab("Python not available", new JScrollPane(textArea));
/* 607:678 */       this.m_textOutputBuffers.add(textArea);
/* 608:    */     }
/* 609:    */   }
/* 610:    */   
/* 611:    */   protected void setVisibleTab(JTabbedPane tabWidget, String title)
/* 612:    */   {
/* 613:689 */     int index = -1;
/* 614:690 */     for (int i = 0; i < tabWidget.getTabCount(); i++) {
/* 615:691 */       if (tabWidget.getTitleAt(i).equals(title))
/* 616:    */       {
/* 617:692 */         index = i;
/* 618:693 */         break;
/* 619:    */       }
/* 620:    */     }
/* 621:697 */     if (index > -1) {
/* 622:698 */       tabWidget.setSelectedIndex(index);
/* 623:    */     }
/* 624:    */   }
/* 625:    */   
/* 626:    */   protected JTextArea getNamedTextOutput(String name)
/* 627:    */   {
/* 628:709 */     JTextArea result = null;
/* 629:711 */     for (int i = 0; i < this.m_outputTabs.getTabCount(); i++) {
/* 630:712 */       if (this.m_outputTabs.getTitleAt(i).equals(name))
/* 631:    */       {
/* 632:713 */         result = (JTextArea)this.m_textOutputBuffers.get(i);
/* 633:714 */         break;
/* 634:    */       }
/* 635:    */     }
/* 636:717 */     return result;
/* 637:    */   }
/* 638:    */   
/* 639:    */   protected ImageDisplayer getNamedImageOutput(String name)
/* 640:    */   {
/* 641:727 */     ImageDisplayer result = null;
/* 642:729 */     for (int i = 0; i < this.m_imageTabs.getTabCount(); i++) {
/* 643:730 */       if (this.m_imageTabs.getTitleAt(i).equals(name))
/* 644:    */       {
/* 645:731 */         result = (ImageDisplayer)this.m_imageOutputBuffers.get(i);
/* 646:732 */         break;
/* 647:    */       }
/* 648:    */     }
/* 649:735 */     return result;
/* 650:    */   }
/* 651:    */   
/* 652:    */   public void setLogger(Logger log)
/* 653:    */   {
/* 654:744 */     this.m_logPanel = log;
/* 655:745 */     if (!this.m_pyAvailable)
/* 656:    */     {
/* 657:746 */       this.m_logPanel.logMessage("Python is not available!!");
/* 658:747 */       if (this.m_envEvalResults != null) {
/* 659:748 */         this.m_logPanel.logMessage(this.m_envEvalResults);
/* 660:    */       }
/* 661:750 */       if (this.m_envEvalException != null) {
/* 662:751 */         this.m_logPanel.logMessage(this.m_envEvalException.getMessage());
/* 663:    */       }
/* 664:    */     }
/* 665:    */   }
/* 666:    */   
/* 667:    */   public void sendInstancesToPython(Instances instances)
/* 668:    */     throws WekaException
/* 669:    */   {
/* 670:764 */     if (this.m_pyAvailable) {
/* 671:    */       try
/* 672:    */       {
/* 673:767 */         PythonSession session = PythonSession.acquireSession(this);
/* 674:768 */         if (this.m_logPanel != null) {
/* 675:769 */           session.setLog(this.m_logPanel);
/* 676:    */         }
/* 677:771 */         session.instancesToPython(instances, "py_data", this.m_debug.isSelected());
/* 678:772 */         if (this.m_logPanel != null)
/* 679:    */         {
/* 680:773 */           this.m_logPanel.statusMessage("Transferred " + instances.relationName() + " into Python as 'py_data'");
/* 681:    */           
/* 682:775 */           logMessage("Transferred " + instances.relationName() + " into Python as 'py_data'", null);
/* 683:    */         }
/* 684:778 */         refreshVarList(session);
/* 685:779 */         checkDebug(session);
/* 686:    */       }
/* 687:    */       catch (WekaException ex)
/* 688:    */       {
/* 689:781 */         ex.printStackTrace();
/* 690:782 */         logMessage(null, ex);
/* 691:    */       }
/* 692:    */       finally
/* 693:    */       {
/* 694:784 */         PythonSession.releaseSession(this);
/* 695:    */       }
/* 696:    */     }
/* 697:    */   }
/* 698:    */   
/* 699:    */   protected void logMessage(String message, Exception ex)
/* 700:    */   {
/* 701:796 */     if (this.m_logPanel != null)
/* 702:    */     {
/* 703:797 */       if (message != null) {
/* 704:798 */         this.m_logPanel.logMessage(message);
/* 705:    */       }
/* 706:800 */       if (ex != null) {
/* 707:801 */         this.m_logPanel.logMessage(ex.getMessage());
/* 708:    */       }
/* 709:    */     }
/* 710:    */   }
/* 711:    */   
/* 712:    */   protected void checkDebug(PythonSession session)
/* 713:    */     throws WekaException
/* 714:    */   {
/* 715:814 */     if (this.m_debug.isSelected())
/* 716:    */     {
/* 717:815 */       List<String> outAndError = session.getPythonDebugBuffer(true);
/* 718:816 */       if (((String)outAndError.get(0)).length() > 0) {
/* 719:817 */         logMessage((String)outAndError.get(0), null);
/* 720:    */       }
/* 721:819 */       if (((String)outAndError.get(1)).length() > 0) {
/* 722:820 */         logMessage((String)outAndError.get(1), null);
/* 723:    */       }
/* 724:    */     }
/* 725:    */   }
/* 726:    */   
/* 727:    */   protected void refreshVarList(PythonSession session)
/* 728:    */     throws WekaException
/* 729:    */   {
/* 730:833 */     boolean sessionSupplied = session != null;
/* 731:834 */     if (session == null) {
/* 732:835 */       session = PythonSession.acquireSession(this);
/* 733:    */     }
/* 734:    */     try
/* 735:    */     {
/* 736:839 */       ((DefaultListModel)this.m_pyVarList.getModel()).removeAllElements();
/* 737:840 */       this.m_getVarAsInstances.setEnabled(false);
/* 738:841 */       this.m_getVarAsImage.setEnabled(false);
/* 739:842 */       this.m_getVarAsText.setEnabled(false);
/* 740:843 */       List<String[]> variableList = session.getVariableListFromPython(this.m_debug.isSelected());
/* 741:845 */       for (String[] v : variableList)
/* 742:    */       {
/* 743:846 */         String varDescription = v[0] + ": " + v[1];
/* 744:847 */         ((DefaultListModel)this.m_pyVarList.getModel()).addElement(varDescription);
/* 745:    */       }
/* 746:    */     }
/* 747:    */     finally
/* 748:    */     {
/* 749:851 */       if (!sessionSupplied) {
/* 750:852 */         PythonSession.releaseSession(this);
/* 751:    */       }
/* 752:    */     }
/* 753:    */   }
/* 754:    */   
/* 755:    */   public static ImageIcon loadIcon(String iconPath)
/* 756:    */   {
/* 757:864 */     URL imageURL = PythonSession.class.getClassLoader().getResource(iconPath);
/* 758:867 */     if (imageURL != null)
/* 759:    */     {
/* 760:868 */       Image pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 761:869 */       return new ImageIcon(pic);
/* 762:    */     }
/* 763:872 */     throw new IllegalArgumentException("Unable to load icon: " + iconPath);
/* 764:    */   }
/* 765:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PythonPanel
 * JD-Core Version:    0.7.0.1
 */