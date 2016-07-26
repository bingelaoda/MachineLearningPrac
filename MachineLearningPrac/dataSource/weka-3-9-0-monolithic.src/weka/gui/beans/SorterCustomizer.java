/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.Window;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.KeyAdapter;
/*  10:    */ import java.awt.event.KeyEvent;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.ComboBoxEditor;
/*  13:    */ import javax.swing.DefaultListModel;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JComboBox;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JList;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JScrollPane;
/*  20:    */ import javax.swing.JTextField;
/*  21:    */ import javax.swing.event.ListSelectionEvent;
/*  22:    */ import javax.swing.event.ListSelectionListener;
/*  23:    */ import weka.core.Attribute;
/*  24:    */ import weka.core.Environment;
/*  25:    */ import weka.core.EnvironmentHandler;
/*  26:    */ import weka.core.Instances;
/*  27:    */ import weka.gui.JListHelper;
/*  28:    */ import weka.gui.PropertySheetPanel;
/*  29:    */ 
/*  30:    */ public class SorterCustomizer
/*  31:    */   extends JPanel
/*  32:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerCloseRequester
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = -4860246697276275408L;
/*  35:    */   protected Sorter m_sorter;
/*  36: 68 */   protected Environment m_env = Environment.getSystemWide();
/*  37: 69 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  38: 71 */   protected JComboBox m_attCombo = new JComboBox();
/*  39: 72 */   protected JComboBox m_descending = new JComboBox();
/*  40:    */   protected EnvironmentField m_buffSize;
/*  41:    */   protected FileEnvironmentField m_tempDir;
/*  42:    */   protected Window m_parent;
/*  43: 78 */   protected JList m_list = new JList();
/*  44:    */   protected DefaultListModel m_listModel;
/*  45: 81 */   protected JButton m_newBut = new JButton("New");
/*  46: 82 */   protected JButton m_deleteBut = new JButton("Delete");
/*  47: 83 */   protected JButton m_upBut = new JButton("Move up");
/*  48: 84 */   protected JButton m_downBut = new JButton("Move down");
/*  49: 86 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  50:    */   
/*  51:    */   public SorterCustomizer()
/*  52:    */   {
/*  53: 93 */     setLayout(new BorderLayout());
/*  54:    */   }
/*  55:    */   
/*  56:    */   private void setup()
/*  57:    */   {
/*  58: 97 */     JPanel aboutAndControlHolder = new JPanel();
/*  59: 98 */     aboutAndControlHolder.setLayout(new BorderLayout());
/*  60:    */     
/*  61:100 */     JPanel controlHolder = new JPanel();
/*  62:101 */     controlHolder.setLayout(new BorderLayout());
/*  63:102 */     JPanel fieldHolder = new JPanel();
/*  64:103 */     fieldHolder.setLayout(new GridLayout(0, 2));
/*  65:104 */     JPanel attListP = new JPanel();
/*  66:105 */     attListP.setLayout(new BorderLayout());
/*  67:106 */     attListP.setBorder(BorderFactory.createTitledBorder("Sort on attribute"));
/*  68:107 */     attListP.add(this.m_attCombo, "Center");
/*  69:108 */     this.m_attCombo.setEditable(true);
/*  70:    */     
/*  71:    */ 
/*  72:111 */     this.m_attCombo.setToolTipText("<html>Accepts an attribute name, index or <br> the special string \"/first\" and \"/last\"</html>");
/*  73:    */     
/*  74:    */ 
/*  75:114 */     this.m_descending.addItem("No");
/*  76:115 */     this.m_descending.addItem("Yes");
/*  77:116 */     JPanel descendingP = new JPanel();
/*  78:117 */     descendingP.setLayout(new BorderLayout());
/*  79:118 */     descendingP.setBorder(BorderFactory.createTitledBorder("Sort descending"));
/*  80:119 */     descendingP.add(this.m_descending, "Center");
/*  81:    */     
/*  82:121 */     fieldHolder.add(attListP);fieldHolder.add(descendingP);
/*  83:122 */     controlHolder.add(fieldHolder, "North");
/*  84:    */     
/*  85:124 */     JPanel otherControls = new JPanel();
/*  86:125 */     otherControls.setLayout(new GridLayout(0, 2));
/*  87:126 */     JLabel bufferSizeLab = new JLabel("Size of in-mem streaming buffer", 4);
/*  88:127 */     bufferSizeLab.setToolTipText("<html>Number of instances to sort in memory <br>before writing to a temp file <br>(instance connections only).</html>");
/*  89:    */     
/*  90:    */ 
/*  91:130 */     otherControls.add(bufferSizeLab);
/*  92:131 */     this.m_buffSize = new EnvironmentField(this.m_env);
/*  93:132 */     otherControls.add(this.m_buffSize);
/*  94:    */     
/*  95:134 */     JLabel tempDirLab = new JLabel("Directory for temp files", 4);
/*  96:135 */     tempDirLab.setToolTipText("Will use system tmp dir if left blank");
/*  97:136 */     otherControls.add(tempDirLab);
/*  98:137 */     this.m_tempDir = new FileEnvironmentField("", this.m_env, 0, true);
/*  99:138 */     this.m_tempDir.resetFileFilters();
/* 100:    */     
/* 101:140 */     otherControls.add(this.m_tempDir);
/* 102:    */     
/* 103:142 */     controlHolder.add(otherControls, "South");
/* 104:    */     
/* 105:144 */     aboutAndControlHolder.add(controlHolder, "South");
/* 106:    */     
/* 107:146 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 108:147 */     aboutAndControlHolder.add(aboutP, "North");
/* 109:148 */     add(aboutAndControlHolder, "North");
/* 110:    */     
/* 111:150 */     this.m_list.setVisibleRowCount(5);
/* 112:151 */     this.m_deleteBut.setEnabled(false);
/* 113:152 */     JPanel listPanel = new JPanel();
/* 114:153 */     listPanel.setLayout(new BorderLayout());
/* 115:154 */     JPanel butHolder = new JPanel();
/* 116:155 */     butHolder.setLayout(new GridLayout(1, 0));
/* 117:156 */     butHolder.add(this.m_newBut);butHolder.add(this.m_deleteBut);
/* 118:157 */     butHolder.add(this.m_upBut);butHolder.add(this.m_downBut);
/* 119:158 */     this.m_upBut.setEnabled(false);this.m_downBut.setEnabled(false);
/* 120:    */     
/* 121:160 */     listPanel.add(butHolder, "North");
/* 122:161 */     JScrollPane js = new JScrollPane(this.m_list);
/* 123:162 */     js.setBorder(BorderFactory.createTitledBorder("Sort-by list (rows applied in order)"));
/* 124:    */     
/* 125:164 */     listPanel.add(js, "Center");
/* 126:165 */     add(listPanel, "Center");
/* 127:    */     
/* 128:167 */     addButtons();
/* 129:    */     
/* 130:169 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 131:    */     {
/* 132:    */       public void valueChanged(ListSelectionEvent e)
/* 133:    */       {
/* 134:171 */         if (!e.getValueIsAdjusting())
/* 135:    */         {
/* 136:172 */           if (!SorterCustomizer.this.m_deleteBut.isEnabled()) {
/* 137:173 */             SorterCustomizer.this.m_deleteBut.setEnabled(true);
/* 138:    */           }
/* 139:176 */           Object entry = SorterCustomizer.this.m_list.getSelectedValue();
/* 140:177 */           if (entry != null)
/* 141:    */           {
/* 142:178 */             Sorter.SortRule m = (Sorter.SortRule)entry;
/* 143:179 */             SorterCustomizer.this.m_attCombo.setSelectedItem(m.getAttribute());
/* 144:180 */             if (m.getDescending()) {
/* 145:181 */               SorterCustomizer.this.m_descending.setSelectedIndex(1);
/* 146:    */             } else {
/* 147:183 */               SorterCustomizer.this.m_descending.setSelectedIndex(0);
/* 148:    */             }
/* 149:    */           }
/* 150:    */         }
/* 151:    */       }
/* 152:189 */     });
/* 153:190 */     this.m_newBut.addActionListener(new ActionListener()
/* 154:    */     {
/* 155:    */       public void actionPerformed(ActionEvent e)
/* 156:    */       {
/* 157:192 */         Sorter.SortRule m = new Sorter.SortRule();
/* 158:    */         
/* 159:    */ 
/* 160:195 */         String att = SorterCustomizer.this.m_attCombo.getSelectedItem() != null ? SorterCustomizer.this.m_attCombo.getSelectedItem().toString() : "";
/* 161:    */         
/* 162:197 */         m.setAttribute(att);
/* 163:198 */         m.setDescending(SorterCustomizer.this.m_descending.getSelectedIndex() == 1);
/* 164:    */         
/* 165:200 */         SorterCustomizer.this.m_listModel.addElement(m);
/* 166:202 */         if (SorterCustomizer.this.m_listModel.size() > 1)
/* 167:    */         {
/* 168:203 */           SorterCustomizer.this.m_upBut.setEnabled(true);
/* 169:204 */           SorterCustomizer.this.m_downBut.setEnabled(true);
/* 170:    */         }
/* 171:207 */         SorterCustomizer.this.m_list.setSelectedIndex(SorterCustomizer.this.m_listModel.size() - 1);
/* 172:    */       }
/* 173:210 */     });
/* 174:211 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 175:    */     {
/* 176:    */       public void actionPerformed(ActionEvent e)
/* 177:    */       {
/* 178:213 */         int selected = SorterCustomizer.this.m_list.getSelectedIndex();
/* 179:214 */         if (selected >= 0)
/* 180:    */         {
/* 181:215 */           SorterCustomizer.this.m_listModel.removeElementAt(selected);
/* 182:217 */           if (SorterCustomizer.this.m_listModel.size() <= 1)
/* 183:    */           {
/* 184:218 */             SorterCustomizer.this.m_upBut.setEnabled(false);
/* 185:219 */             SorterCustomizer.this.m_downBut.setEnabled(false);
/* 186:    */           }
/* 187:    */         }
/* 188:    */       }
/* 189:224 */     });
/* 190:225 */     this.m_upBut.addActionListener(new ActionListener()
/* 191:    */     {
/* 192:    */       public void actionPerformed(ActionEvent e)
/* 193:    */       {
/* 194:227 */         JListHelper.moveUp(SorterCustomizer.this.m_list);
/* 195:    */       }
/* 196:230 */     });
/* 197:231 */     this.m_downBut.addActionListener(new ActionListener()
/* 198:    */     {
/* 199:    */       public void actionPerformed(ActionEvent e)
/* 200:    */       {
/* 201:233 */         JListHelper.moveDown(SorterCustomizer.this.m_list);
/* 202:    */       }
/* 203:236 */     });
/* 204:237 */     this.m_attCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 205:    */     {
/* 206:    */       public void keyReleased(KeyEvent e)
/* 207:    */       {
/* 208:239 */         Object m = SorterCustomizer.this.m_list.getSelectedValue();
/* 209:240 */         String text = "";
/* 210:241 */         if (SorterCustomizer.this.m_attCombo.getSelectedItem() != null) {
/* 211:242 */           text = SorterCustomizer.this.m_attCombo.getSelectedItem().toString();
/* 212:    */         }
/* 213:244 */         Component theEditor = SorterCustomizer.this.m_attCombo.getEditor().getEditorComponent();
/* 214:245 */         if ((theEditor instanceof JTextField)) {
/* 215:246 */           text = ((JTextField)theEditor).getText();
/* 216:    */         }
/* 217:248 */         if (m != null)
/* 218:    */         {
/* 219:249 */           ((Sorter.SortRule)m).setAttribute(text);
/* 220:    */           
/* 221:251 */           SorterCustomizer.this.m_list.repaint();
/* 222:    */         }
/* 223:    */       }
/* 224:255 */     });
/* 225:256 */     this.m_attCombo.addActionListener(new ActionListener()
/* 226:    */     {
/* 227:    */       public void actionPerformed(ActionEvent e)
/* 228:    */       {
/* 229:258 */         Object m = SorterCustomizer.this.m_list.getSelectedValue();
/* 230:259 */         Object selected = SorterCustomizer.this.m_attCombo.getSelectedItem();
/* 231:260 */         if ((m != null) && (selected != null))
/* 232:    */         {
/* 233:261 */           ((Sorter.SortRule)m).setAttribute(selected.toString());
/* 234:    */           
/* 235:263 */           SorterCustomizer.this.m_list.repaint();
/* 236:    */         }
/* 237:    */       }
/* 238:267 */     });
/* 239:268 */     this.m_descending.addActionListener(new ActionListener()
/* 240:    */     {
/* 241:    */       public void actionPerformed(ActionEvent e)
/* 242:    */       {
/* 243:270 */         Object m = SorterCustomizer.this.m_list.getSelectedValue();
/* 244:271 */         if (m != null)
/* 245:    */         {
/* 246:272 */           ((Sorter.SortRule)m).setDescending(SorterCustomizer.this.m_descending.getSelectedIndex() == 1);
/* 247:273 */           SorterCustomizer.this.m_list.repaint();
/* 248:    */         }
/* 249:    */       }
/* 250:    */     });
/* 251:    */   }
/* 252:    */   
/* 253:    */   private void addButtons()
/* 254:    */   {
/* 255:280 */     JButton okBut = new JButton("OK");
/* 256:281 */     JButton cancelBut = new JButton("Cancel");
/* 257:    */     
/* 258:283 */     JPanel butHolder = new JPanel();
/* 259:284 */     butHolder.setLayout(new GridLayout(1, 2));
/* 260:285 */     butHolder.add(okBut);butHolder.add(cancelBut);
/* 261:286 */     add(butHolder, "South");
/* 262:    */     
/* 263:288 */     okBut.addActionListener(new ActionListener()
/* 264:    */     {
/* 265:    */       public void actionPerformed(ActionEvent e)
/* 266:    */       {
/* 267:290 */         SorterCustomizer.this.closingOK();
/* 268:    */         
/* 269:292 */         SorterCustomizer.this.m_parent.dispose();
/* 270:    */       }
/* 271:295 */     });
/* 272:296 */     cancelBut.addActionListener(new ActionListener()
/* 273:    */     {
/* 274:    */       public void actionPerformed(ActionEvent e)
/* 275:    */       {
/* 276:298 */         SorterCustomizer.this.closingCancel();
/* 277:    */         
/* 278:300 */         SorterCustomizer.this.m_parent.dispose();
/* 279:    */       }
/* 280:    */     });
/* 281:    */   }
/* 282:    */   
/* 283:    */   protected void closingOK()
/* 284:    */   {
/* 285:309 */     StringBuffer buff = new StringBuffer();
/* 286:310 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 287:    */     {
/* 288:311 */       Sorter.SortRule m = (Sorter.SortRule)this.m_listModel.elementAt(i);
/* 289:    */       
/* 290:    */ 
/* 291:314 */       buff.append(m.toStringInternal());
/* 292:315 */       if (i < this.m_listModel.size() - 1) {
/* 293:316 */         buff.append("@@sort-rule@@");
/* 294:    */       }
/* 295:    */     }
/* 296:320 */     if (this.m_sorter.getSortDetails() != null)
/* 297:    */     {
/* 298:321 */       if (!this.m_sorter.getSortDetails().equals(buff.toString())) {
/* 299:322 */         this.m_modifyL.setModifiedStatus(this, true);
/* 300:    */       }
/* 301:    */     }
/* 302:    */     else {
/* 303:325 */       this.m_modifyL.setModifiedStatus(this, true);
/* 304:    */     }
/* 305:328 */     this.m_sorter.setSortDetails(buff.toString());
/* 306:329 */     if ((this.m_buffSize.getText() != null) && (this.m_buffSize.getText().length() > 0))
/* 307:    */     {
/* 308:330 */       if ((this.m_sorter.getBufferSize() != null) && (!this.m_sorter.getBufferSize().equals(this.m_buffSize.getText()))) {
/* 309:332 */         this.m_modifyL.setModifiedStatus(this, true);
/* 310:    */       }
/* 311:334 */       this.m_sorter.setBufferSize(this.m_buffSize.getText());
/* 312:    */     }
/* 313:337 */     if ((this.m_tempDir.getText() != null) && (this.m_tempDir.getText().length() > 0))
/* 314:    */     {
/* 315:338 */       if ((this.m_sorter.getTempDirectory() != null) && (!this.m_sorter.getTempDirectory().equals(this.m_tempDir.getText()))) {
/* 316:340 */         this.m_modifyL.setModifiedStatus(this, true);
/* 317:    */       }
/* 318:343 */       this.m_sorter.setTempDirectory(this.m_tempDir.getText());
/* 319:    */     }
/* 320:    */   }
/* 321:    */   
/* 322:    */   protected void closingCancel() {}
/* 323:    */   
/* 324:    */   protected void initialize()
/* 325:    */   {
/* 326:358 */     if ((this.m_sorter.getBufferSize() != null) && (this.m_sorter.getBufferSize().length() > 0)) {
/* 327:359 */       this.m_buffSize.setText(this.m_sorter.getBufferSize());
/* 328:    */     }
/* 329:362 */     if ((this.m_sorter.getTempDirectory() != null) && (this.m_sorter.getTempDirectory().length() > 0)) {
/* 330:363 */       this.m_tempDir.setText(this.m_sorter.getTempDirectory());
/* 331:    */     }
/* 332:366 */     String sString = this.m_sorter.getSortDetails();
/* 333:    */     
/* 334:368 */     this.m_listModel = new DefaultListModel();
/* 335:369 */     this.m_list.setModel(this.m_listModel);
/* 336:371 */     if ((sString != null) && (sString.length() > 0))
/* 337:    */     {
/* 338:372 */       String[] parts = sString.split("@@sort-rule@@");
/* 339:374 */       if (parts.length > 0)
/* 340:    */       {
/* 341:375 */         this.m_upBut.setEnabled(true);
/* 342:376 */         this.m_downBut.setEnabled(true);
/* 343:377 */         for (String sPart : parts)
/* 344:    */         {
/* 345:378 */           Sorter.SortRule s = new Sorter.SortRule(sPart);
/* 346:379 */           this.m_listModel.addElement(s);
/* 347:    */         }
/* 348:    */       }
/* 349:383 */       this.m_list.repaint();
/* 350:    */     }
/* 351:387 */     if (this.m_sorter.getConnectedFormat() != null)
/* 352:    */     {
/* 353:388 */       Instances incoming = this.m_sorter.getConnectedFormat();
/* 354:    */       
/* 355:390 */       this.m_attCombo.removeAllItems();
/* 356:391 */       for (int i = 0; i < incoming.numAttributes(); i++) {
/* 357:392 */         this.m_attCombo.addItem(incoming.attribute(i).name());
/* 358:    */       }
/* 359:    */     }
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void setObject(Object o)
/* 363:    */   {
/* 364:401 */     if ((o instanceof Sorter))
/* 365:    */     {
/* 366:402 */       this.m_sorter = ((Sorter)o);
/* 367:403 */       this.m_tempEditor.setTarget(o);
/* 368:404 */       setup();
/* 369:405 */       initialize();
/* 370:    */     }
/* 371:    */   }
/* 372:    */   
/* 373:    */   public void setParentWindow(Window parent)
/* 374:    */   {
/* 375:415 */     this.m_parent = parent;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void setEnvironment(Environment env)
/* 379:    */   {
/* 380:424 */     this.m_env = env;
/* 381:    */   }
/* 382:    */   
/* 383:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 384:    */   {
/* 385:431 */     this.m_modifyL = l;
/* 386:    */   }
/* 387:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SorterCustomizer
 * JD-Core Version:    0.7.0.1
 */