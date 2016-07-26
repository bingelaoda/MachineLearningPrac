/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.MouseAdapter;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.awt.event.MouseListener;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.io.File;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.util.Collections;
/*  18:    */ import java.util.Vector;
/*  19:    */ import javax.swing.BorderFactory;
/*  20:    */ import javax.swing.DefaultListModel;
/*  21:    */ import javax.swing.JButton;
/*  22:    */ import javax.swing.JCheckBox;
/*  23:    */ import javax.swing.JFrame;
/*  24:    */ import javax.swing.JList;
/*  25:    */ import javax.swing.JOptionPane;
/*  26:    */ import javax.swing.JPanel;
/*  27:    */ import javax.swing.JScrollPane;
/*  28:    */ import javax.swing.event.ListSelectionEvent;
/*  29:    */ import javax.swing.event.ListSelectionListener;
/*  30:    */ import javax.swing.filechooser.FileFilter;
/*  31:    */ import weka.core.Utils;
/*  32:    */ import weka.core.converters.ConverterUtils;
/*  33:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  34:    */ import weka.core.converters.Saver;
/*  35:    */ import weka.experiment.Experiment;
/*  36:    */ import weka.gui.ConverterFileChooser;
/*  37:    */ import weka.gui.JListHelper;
/*  38:    */ import weka.gui.ViewerDialog;
/*  39:    */ 
/*  40:    */ public class DatasetListPanel
/*  41:    */   extends JPanel
/*  42:    */   implements ActionListener
/*  43:    */ {
/*  44:    */   private static final long serialVersionUID = 7068857852794405769L;
/*  45:    */   protected Experiment m_Exp;
/*  46:    */   protected JList m_List;
/*  47: 79 */   protected JButton m_AddBut = new JButton("Add new...");
/*  48: 82 */   protected JButton m_EditBut = new JButton("Edit selected...");
/*  49: 85 */   protected JButton m_DeleteBut = new JButton("Delete selected");
/*  50: 88 */   protected JButton m_UpBut = new JButton("Up");
/*  51: 91 */   protected JButton m_DownBut = new JButton("Down");
/*  52: 94 */   protected JCheckBox m_relativeCheck = new JCheckBox("Use relative paths");
/*  53:100 */   protected ConverterFileChooser m_FileChooser = new ConverterFileChooser(ExperimenterDefaults.getInitialDatasetsDirectory());
/*  54:    */   
/*  55:    */   public DatasetListPanel(Experiment exp)
/*  56:    */   {
/*  57:110 */     this();
/*  58:111 */     setExperiment(exp);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public DatasetListPanel()
/*  62:    */   {
/*  63:119 */     this.m_List = new JList();
/*  64:120 */     this.m_List.addListSelectionListener(new ListSelectionListener()
/*  65:    */     {
/*  66:    */       public void valueChanged(ListSelectionEvent e)
/*  67:    */       {
/*  68:123 */         DatasetListPanel.this.setButtons(e);
/*  69:    */       }
/*  70:125 */     });
/*  71:126 */     MouseListener mouseListener = new MouseAdapter()
/*  72:    */     {
/*  73:    */       public void mouseClicked(MouseEvent e)
/*  74:    */       {
/*  75:129 */         if (e.getClickCount() == 2)
/*  76:    */         {
/*  77:134 */           int index = DatasetListPanel.this.m_List.locationToIndex(e.getPoint());
/*  78:135 */           if (index > -1) {
/*  79:136 */             DatasetListPanel.this.actionPerformed(new ActionEvent(DatasetListPanel.this.m_EditBut, 0, ""));
/*  80:    */           }
/*  81:    */         }
/*  82:    */       }
/*  83:140 */     };
/*  84:141 */     this.m_List.addMouseListener(mouseListener);
/*  85:    */     
/*  86:    */ 
/*  87:144 */     this.m_FileChooser.setCoreConvertersOnly(true);
/*  88:145 */     this.m_FileChooser.setMultiSelectionEnabled(true);
/*  89:146 */     this.m_FileChooser.setFileSelectionMode(2);
/*  90:    */     
/*  91:148 */     this.m_FileChooser.setAcceptAllFileFilterUsed(false);
/*  92:149 */     this.m_DeleteBut.setEnabled(false);
/*  93:150 */     this.m_DeleteBut.addActionListener(this);
/*  94:151 */     this.m_AddBut.setEnabled(false);
/*  95:152 */     this.m_AddBut.addActionListener(this);
/*  96:153 */     this.m_EditBut.setEnabled(false);
/*  97:154 */     this.m_EditBut.addActionListener(this);
/*  98:155 */     this.m_UpBut.setEnabled(false);
/*  99:156 */     this.m_UpBut.addActionListener(this);
/* 100:157 */     this.m_DownBut.setEnabled(false);
/* 101:158 */     this.m_DownBut.addActionListener(this);
/* 102:159 */     this.m_relativeCheck.setSelected(ExperimenterDefaults.getUseRelativePaths());
/* 103:160 */     this.m_relativeCheck.setToolTipText("Store file paths relative to the start directory");
/* 104:    */     
/* 105:162 */     setLayout(new BorderLayout());
/* 106:163 */     setBorder(BorderFactory.createTitledBorder("Datasets"));
/* 107:164 */     JPanel topLab = new JPanel();
/* 108:165 */     GridBagLayout gb = new GridBagLayout();
/* 109:166 */     GridBagConstraints constraints = new GridBagConstraints();
/* 110:167 */     topLab.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 111:    */     
/* 112:169 */     topLab.setLayout(gb);
/* 113:    */     
/* 114:171 */     constraints.gridx = 0;
/* 115:172 */     constraints.gridy = 0;
/* 116:173 */     constraints.weightx = 5.0D;
/* 117:174 */     constraints.fill = 2;
/* 118:175 */     constraints.gridwidth = 1;
/* 119:176 */     constraints.gridheight = 1;
/* 120:177 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 121:178 */     topLab.add(this.m_AddBut, constraints);
/* 122:179 */     constraints.gridx = 1;
/* 123:180 */     constraints.gridy = 0;
/* 124:181 */     constraints.weightx = 5.0D;
/* 125:182 */     constraints.gridwidth = 1;
/* 126:183 */     constraints.gridheight = 1;
/* 127:184 */     topLab.add(this.m_EditBut, constraints);
/* 128:185 */     constraints.gridx = 2;
/* 129:186 */     constraints.gridy = 0;
/* 130:187 */     constraints.weightx = 5.0D;
/* 131:188 */     constraints.gridwidth = 1;
/* 132:189 */     constraints.gridheight = 1;
/* 133:190 */     topLab.add(this.m_DeleteBut, constraints);
/* 134:    */     
/* 135:192 */     constraints.gridx = 0;
/* 136:193 */     constraints.gridy = 1;
/* 137:194 */     constraints.weightx = 5.0D;
/* 138:195 */     constraints.fill = 2;
/* 139:196 */     constraints.gridwidth = 1;
/* 140:197 */     constraints.gridheight = 1;
/* 141:198 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 142:199 */     topLab.add(this.m_relativeCheck, constraints);
/* 143:    */     
/* 144:201 */     JPanel bottomLab = new JPanel();
/* 145:202 */     gb = new GridBagLayout();
/* 146:203 */     constraints = new GridBagConstraints();
/* 147:204 */     bottomLab.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 148:205 */     bottomLab.setLayout(gb);
/* 149:    */     
/* 150:207 */     constraints.gridx = 0;
/* 151:208 */     constraints.gridy = 0;
/* 152:209 */     constraints.weightx = 5.0D;
/* 153:210 */     constraints.fill = 2;
/* 154:211 */     constraints.gridwidth = 1;
/* 155:212 */     constraints.gridheight = 1;
/* 156:213 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 157:214 */     bottomLab.add(this.m_UpBut, constraints);
/* 158:215 */     constraints.gridx = 1;
/* 159:216 */     constraints.gridy = 0;
/* 160:217 */     constraints.weightx = 5.0D;
/* 161:218 */     constraints.gridwidth = 1;
/* 162:219 */     constraints.gridheight = 1;
/* 163:220 */     bottomLab.add(this.m_DownBut, constraints);
/* 164:    */     
/* 165:222 */     add(topLab, "North");
/* 166:223 */     add(new JScrollPane(this.m_List), "Center");
/* 167:224 */     add(bottomLab, "South");
/* 168:    */   }
/* 169:    */   
/* 170:    */   private void setButtons(ListSelectionEvent e)
/* 171:    */   {
/* 172:234 */     if ((e == null) || (e.getSource() == this.m_List))
/* 173:    */     {
/* 174:235 */       this.m_DeleteBut.setEnabled(this.m_List.getSelectedIndex() > -1);
/* 175:236 */       this.m_EditBut.setEnabled(this.m_List.getSelectedIndices().length == 1);
/* 176:237 */       this.m_UpBut.setEnabled(JListHelper.canMoveUp(this.m_List));
/* 177:238 */       this.m_DownBut.setEnabled(JListHelper.canMoveDown(this.m_List));
/* 178:    */     }
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void setExperiment(Experiment exp)
/* 182:    */   {
/* 183:249 */     this.m_Exp = exp;
/* 184:250 */     this.m_List.setModel(this.m_Exp.getDatasets());
/* 185:251 */     this.m_AddBut.setEnabled(true);
/* 186:252 */     setButtons(null);
/* 187:    */   }
/* 188:    */   
/* 189:    */   protected void getFilesRecursively(File directory, Vector<File> files)
/* 190:    */   {
/* 191:    */     try
/* 192:    */     {
/* 193:265 */       String[] currentDirFiles = directory.list();
/* 194:266 */       for (int i = 0; i < currentDirFiles.length; i++)
/* 195:    */       {
/* 196:267 */         currentDirFiles[i] = (directory.getCanonicalPath() + File.separator + currentDirFiles[i]);
/* 197:    */         
/* 198:269 */         File current = new File(currentDirFiles[i]);
/* 199:270 */         if (this.m_FileChooser.getFileFilter().accept(current)) {
/* 200:271 */           if (current.isDirectory()) {
/* 201:272 */             getFilesRecursively(current, files);
/* 202:    */           } else {
/* 203:274 */             files.addElement(current);
/* 204:    */           }
/* 205:    */         }
/* 206:    */       }
/* 207:    */     }
/* 208:    */     catch (Exception e)
/* 209:    */     {
/* 210:279 */       System.err.println("IOError occured when reading list of files");
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void actionPerformed(ActionEvent e)
/* 215:    */   {
/* 216:290 */     boolean useRelativePaths = this.m_relativeCheck.isSelected();
/* 217:292 */     if (e.getSource() == this.m_AddBut)
/* 218:    */     {
/* 219:294 */       int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 220:295 */       if (returnVal == 0) {
/* 221:296 */         if (this.m_FileChooser.isMultiSelectionEnabled())
/* 222:    */         {
/* 223:297 */           File[] selected = this.m_FileChooser.getSelectedFiles();
/* 224:298 */           for (File element : selected) {
/* 225:299 */             if (element.isDirectory())
/* 226:    */             {
/* 227:300 */               Vector<File> files = new Vector();
/* 228:301 */               getFilesRecursively(element, files);
/* 229:    */               
/* 230:    */ 
/* 231:304 */               Collections.sort(files);
/* 232:306 */               for (int j = 0; j < files.size(); j++)
/* 233:    */               {
/* 234:307 */                 File temp = (File)files.elementAt(j);
/* 235:308 */                 if (useRelativePaths) {
/* 236:    */                   try
/* 237:    */                   {
/* 238:310 */                     temp = Utils.convertToRelativePath(temp);
/* 239:    */                   }
/* 240:    */                   catch (Exception ex)
/* 241:    */                   {
/* 242:312 */                     ex.printStackTrace();
/* 243:    */                   }
/* 244:    */                 }
/* 245:315 */                 this.m_Exp.getDatasets().addElement(temp);
/* 246:    */               }
/* 247:    */             }
/* 248:    */             else
/* 249:    */             {
/* 250:318 */               File temp = element;
/* 251:319 */               if (useRelativePaths) {
/* 252:    */                 try
/* 253:    */                 {
/* 254:321 */                   temp = Utils.convertToRelativePath(temp);
/* 255:    */                 }
/* 256:    */                 catch (Exception ex)
/* 257:    */                 {
/* 258:323 */                   ex.printStackTrace();
/* 259:    */                 }
/* 260:    */               }
/* 261:326 */               this.m_Exp.getDatasets().addElement(temp);
/* 262:    */             }
/* 263:    */           }
/* 264:329 */           setButtons(null);
/* 265:    */         }
/* 266:    */         else
/* 267:    */         {
/* 268:331 */           if (this.m_FileChooser.getSelectedFile().isDirectory())
/* 269:    */           {
/* 270:332 */             Vector<File> files = new Vector();
/* 271:333 */             getFilesRecursively(this.m_FileChooser.getSelectedFile(), files);
/* 272:    */             
/* 273:    */ 
/* 274:336 */             Collections.sort(files);
/* 275:338 */             for (int j = 0; j < files.size(); j++)
/* 276:    */             {
/* 277:339 */               File temp = (File)files.elementAt(j);
/* 278:340 */               if (useRelativePaths) {
/* 279:    */                 try
/* 280:    */                 {
/* 281:342 */                   temp = Utils.convertToRelativePath(temp);
/* 282:    */                 }
/* 283:    */                 catch (Exception ex)
/* 284:    */                 {
/* 285:344 */                   ex.printStackTrace();
/* 286:    */                 }
/* 287:    */               }
/* 288:347 */               this.m_Exp.getDatasets().addElement(temp);
/* 289:    */             }
/* 290:    */           }
/* 291:    */           else
/* 292:    */           {
/* 293:350 */             File temp = this.m_FileChooser.getSelectedFile();
/* 294:351 */             if (useRelativePaths) {
/* 295:    */               try
/* 296:    */               {
/* 297:353 */                 temp = Utils.convertToRelativePath(temp);
/* 298:    */               }
/* 299:    */               catch (Exception ex)
/* 300:    */               {
/* 301:355 */                 ex.printStackTrace();
/* 302:    */               }
/* 303:    */             }
/* 304:358 */             this.m_Exp.getDatasets().addElement(temp);
/* 305:    */           }
/* 306:360 */           setButtons(null);
/* 307:    */         }
/* 308:    */       }
/* 309:    */     }
/* 310:363 */     else if (e.getSource() == this.m_DeleteBut)
/* 311:    */     {
/* 312:365 */       int[] selected = this.m_List.getSelectedIndices();
/* 313:366 */       if (selected != null) {
/* 314:367 */         for (int i = selected.length - 1; i >= 0; i--)
/* 315:    */         {
/* 316:368 */           int current = selected[i];
/* 317:369 */           this.m_Exp.getDatasets().removeElementAt(current);
/* 318:370 */           if (this.m_Exp.getDatasets().size() > current) {
/* 319:371 */             this.m_List.setSelectedIndex(current);
/* 320:    */           } else {
/* 321:373 */             this.m_List.setSelectedIndex(current - 1);
/* 322:    */           }
/* 323:    */         }
/* 324:    */       }
/* 325:377 */       setButtons(null);
/* 326:    */     }
/* 327:378 */     else if (e.getSource() == this.m_EditBut)
/* 328:    */     {
/* 329:380 */       int selected = this.m_List.getSelectedIndex();
/* 330:381 */       if (selected != -1)
/* 331:    */       {
/* 332:382 */         ViewerDialog dialog = new ViewerDialog(null);
/* 333:383 */         String filename = this.m_List.getSelectedValue().toString();
/* 334:    */         try
/* 335:    */         {
/* 336:386 */           ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);
/* 337:387 */           int result = dialog.showDialog(source.getDataSet());
/* 338:    */           
/* 339:    */ 
/* 340:    */ 
/* 341:391 */           source = null;
/* 342:392 */           System.gc();
/* 343:394 */           if ((result == 0) && (dialog.isChanged()))
/* 344:    */           {
/* 345:395 */             result = JOptionPane.showConfirmDialog(this, "File was modified - save changes?");
/* 346:397 */             if (result == 0)
/* 347:    */             {
/* 348:398 */               Saver saver = ConverterUtils.getSaverForFile(filename);
/* 349:399 */               saver.setFile(new File(filename));
/* 350:400 */               saver.setInstances(dialog.getInstances());
/* 351:401 */               saver.writeBatch();
/* 352:    */             }
/* 353:    */           }
/* 354:    */         }
/* 355:    */         catch (Exception ex)
/* 356:    */         {
/* 357:405 */           JOptionPane.showMessageDialog(this, "Error loading file '" + filename + "':\n" + ex.toString(), "Error loading file", 1);
/* 358:    */         }
/* 359:    */       }
/* 360:410 */       setButtons(null);
/* 361:    */     }
/* 362:411 */     else if (e.getSource() == this.m_UpBut)
/* 363:    */     {
/* 364:412 */       JListHelper.moveUp(this.m_List);
/* 365:    */     }
/* 366:413 */     else if (e.getSource() == this.m_DownBut)
/* 367:    */     {
/* 368:414 */       JListHelper.moveDown(this.m_List);
/* 369:    */     }
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static void main(String[] args)
/* 373:    */   {
/* 374:    */     try
/* 375:    */     {
/* 376:426 */       JFrame jf = new JFrame("Dataset List Editor");
/* 377:427 */       jf.getContentPane().setLayout(new BorderLayout());
/* 378:428 */       DatasetListPanel dp = new DatasetListPanel();
/* 379:429 */       jf.getContentPane().add(dp, "Center");
/* 380:430 */       jf.addWindowListener(new WindowAdapter()
/* 381:    */       {
/* 382:    */         public void windowClosing(WindowEvent e)
/* 383:    */         {
/* 384:433 */           this.val$jf.dispose();
/* 385:434 */           System.exit(0);
/* 386:    */         }
/* 387:436 */       });
/* 388:437 */       jf.pack();
/* 389:438 */       jf.setVisible(true);
/* 390:439 */       System.err.println("Short nap");
/* 391:440 */       Thread.sleep(3000L);
/* 392:441 */       System.err.println("Done");
/* 393:442 */       dp.setExperiment(new Experiment());
/* 394:    */     }
/* 395:    */     catch (Exception ex)
/* 396:    */     {
/* 397:444 */       ex.printStackTrace();
/* 398:445 */       System.err.println(ex.getMessage());
/* 399:    */     }
/* 400:    */   }
/* 401:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.DatasetListPanel
 * JD-Core Version:    0.7.0.1
 */