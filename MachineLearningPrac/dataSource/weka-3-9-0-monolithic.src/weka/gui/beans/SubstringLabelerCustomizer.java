/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeEvent;
/*   9:    */ import java.beans.PropertyChangeListener;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.DefaultListModel;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JCheckBox;
/*  14:    */ import javax.swing.JLabel;
/*  15:    */ import javax.swing.JList;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JScrollPane;
/*  18:    */ import javax.swing.event.ListSelectionEvent;
/*  19:    */ import javax.swing.event.ListSelectionListener;
/*  20:    */ import weka.core.Environment;
/*  21:    */ import weka.core.EnvironmentHandler;
/*  22:    */ import weka.gui.JListHelper;
/*  23:    */ import weka.gui.PropertySheetPanel;
/*  24:    */ 
/*  25:    */ public class SubstringLabelerCustomizer
/*  26:    */   extends JPanel
/*  27:    */   implements EnvironmentHandler, BeanCustomizer, CustomizerCloseRequester
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 7636584212353183751L;
/*  30: 60 */   protected Environment m_env = Environment.getSystemWide();
/*  31: 61 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  32:    */   protected SubstringLabeler m_labeler;
/*  33:    */   protected EnvironmentField m_matchAttNameField;
/*  34:    */   protected EnvironmentField m_attListField;
/*  35:    */   protected EnvironmentField m_matchField;
/*  36:    */   protected EnvironmentField m_labelField;
/*  37: 68 */   protected JCheckBox m_regexCheck = new JCheckBox();
/*  38: 69 */   protected JCheckBox m_ignoreCaseCheck = new JCheckBox();
/*  39: 70 */   protected JCheckBox m_nominalBinaryCheck = new JCheckBox();
/*  40: 71 */   protected JCheckBox m_consumeNonMatchingCheck = new JCheckBox();
/*  41: 73 */   protected JList m_list = new JList();
/*  42:    */   protected DefaultListModel m_listModel;
/*  43: 76 */   protected JButton m_newBut = new JButton("New");
/*  44: 77 */   protected JButton m_deleteBut = new JButton("Delete");
/*  45: 78 */   protected JButton m_upBut = new JButton("Move up");
/*  46: 79 */   protected JButton m_downBut = new JButton("Move down");
/*  47:    */   protected Window m_parent;
/*  48: 83 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  49:    */   
/*  50:    */   public SubstringLabelerCustomizer()
/*  51:    */   {
/*  52: 86 */     setLayout(new BorderLayout());
/*  53:    */   }
/*  54:    */   
/*  55:    */   private void setup()
/*  56:    */   {
/*  57: 90 */     JPanel aboutAndControlHolder = new JPanel();
/*  58: 91 */     aboutAndControlHolder.setLayout(new BorderLayout());
/*  59:    */     
/*  60: 93 */     JPanel controlHolder = new JPanel();
/*  61: 94 */     controlHolder.setLayout(new BorderLayout());
/*  62: 95 */     JPanel fieldHolder = new JPanel();
/*  63: 96 */     JPanel attListP = new JPanel();
/*  64: 97 */     attListP.setLayout(new BorderLayout());
/*  65: 98 */     attListP.setBorder(BorderFactory.createTitledBorder("Apply to attributes"));
/*  66: 99 */     this.m_attListField = new EnvironmentField(this.m_env);
/*  67:100 */     attListP.add(this.m_attListField, "Center");
/*  68:101 */     attListP.setToolTipText("<html>Accepts a range of indexes (e.g. '1,2,6-10')<br> or a comma-separated list of named attributes</html>");
/*  69:    */     
/*  70:    */ 
/*  71:104 */     JPanel matchP = new JPanel();
/*  72:105 */     matchP.setLayout(new BorderLayout());
/*  73:106 */     matchP.setBorder(BorderFactory.createTitledBorder("Match"));
/*  74:107 */     this.m_matchField = new EnvironmentField(this.m_env);
/*  75:108 */     matchP.add(this.m_matchField, "Center");
/*  76:109 */     JPanel labelP = new JPanel();
/*  77:110 */     labelP.setLayout(new BorderLayout());
/*  78:111 */     labelP.setBorder(BorderFactory.createTitledBorder("Label"));
/*  79:112 */     this.m_labelField = new EnvironmentField(this.m_env);
/*  80:113 */     labelP.add(this.m_labelField, "Center");
/*  81:114 */     fieldHolder.add(attListP);
/*  82:115 */     fieldHolder.add(matchP);
/*  83:116 */     fieldHolder.add(labelP);
/*  84:117 */     controlHolder.add(fieldHolder, "North");
/*  85:    */     
/*  86:119 */     JPanel checkHolder = new JPanel();
/*  87:120 */     checkHolder.setLayout(new GridLayout(0, 2));
/*  88:121 */     JLabel attNameLab = new JLabel("Name of label attribute", 4);
/*  89:    */     
/*  90:123 */     checkHolder.add(attNameLab);
/*  91:124 */     this.m_matchAttNameField = new EnvironmentField(this.m_env);
/*  92:125 */     this.m_matchAttNameField.setText(this.m_labeler.getMatchAttributeName());
/*  93:126 */     checkHolder.add(this.m_matchAttNameField);
/*  94:127 */     JLabel regexLab = new JLabel("Match using a regular expression", 4);
/*  95:    */     
/*  96:129 */     regexLab.setToolTipText("Use a regular expression rather than literal match");
/*  97:    */     
/*  98:131 */     checkHolder.add(regexLab);
/*  99:132 */     checkHolder.add(this.m_regexCheck);
/* 100:133 */     JLabel caseLab = new JLabel("Ignore case when matching", 4);
/* 101:    */     
/* 102:135 */     checkHolder.add(caseLab);
/* 103:136 */     checkHolder.add(this.m_ignoreCaseCheck);
/* 104:137 */     JLabel nominalBinaryLab = new JLabel("Make binary label attribute nominal", 4);
/* 105:    */     
/* 106:139 */     nominalBinaryLab.setToolTipText("<html>If the label attribute is binary (i.e. no <br>explicit labels have been declared) then<br>this makes the resulting attribute nominal<br>rather than numeric.</html>");
/* 107:    */     
/* 108:    */ 
/* 109:    */ 
/* 110:143 */     checkHolder.add(nominalBinaryLab);
/* 111:144 */     checkHolder.add(this.m_nominalBinaryCheck);
/* 112:145 */     this.m_nominalBinaryCheck.setSelected(this.m_labeler.getNominalBinary());
/* 113:146 */     JLabel consumeNonMatchLab = new JLabel("Consume non-matching instances", 4);
/* 114:    */     
/* 115:148 */     consumeNonMatchLab.setToolTipText("<html>When explicit labels have been defined, consume <br>(rather than output with missing value) instances</html>");
/* 116:    */     
/* 117:    */ 
/* 118:151 */     checkHolder.add(consumeNonMatchLab);
/* 119:152 */     checkHolder.add(this.m_consumeNonMatchingCheck);
/* 120:153 */     this.m_consumeNonMatchingCheck.setSelected(this.m_labeler.getConsumeNonMatching());
/* 121:    */     
/* 122:155 */     controlHolder.add(checkHolder, "South");
/* 123:    */     
/* 124:157 */     aboutAndControlHolder.add(controlHolder, "South");
/* 125:158 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 126:159 */     aboutAndControlHolder.add(aboutP, "North");
/* 127:160 */     add(aboutAndControlHolder, "North");
/* 128:    */     
/* 129:162 */     this.m_list.setVisibleRowCount(5);
/* 130:163 */     this.m_deleteBut.setEnabled(false);
/* 131:164 */     JPanel listPanel = new JPanel();
/* 132:165 */     listPanel.setLayout(new BorderLayout());
/* 133:166 */     JPanel butHolder = new JPanel();
/* 134:167 */     butHolder.setLayout(new GridLayout(1, 0));
/* 135:168 */     butHolder.add(this.m_newBut);
/* 136:169 */     butHolder.add(this.m_deleteBut);
/* 137:170 */     butHolder.add(this.m_upBut);
/* 138:171 */     butHolder.add(this.m_downBut);
/* 139:172 */     this.m_upBut.setEnabled(false);
/* 140:173 */     this.m_downBut.setEnabled(false);
/* 141:    */     
/* 142:175 */     listPanel.add(butHolder, "North");
/* 143:176 */     JScrollPane js = new JScrollPane(this.m_list);
/* 144:177 */     js.setBorder(BorderFactory.createTitledBorder("Match-list list (rows applied in order)"));
/* 145:    */     
/* 146:179 */     listPanel.add(js, "Center");
/* 147:180 */     add(listPanel, "Center");
/* 148:    */     
/* 149:182 */     addButtons();
/* 150:    */     
/* 151:184 */     this.m_attListField.addPropertyChangeListener(new PropertyChangeListener()
/* 152:    */     {
/* 153:    */       public void propertyChange(PropertyChangeEvent e)
/* 154:    */       {
/* 155:187 */         Object m = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 156:188 */         if (m != null)
/* 157:    */         {
/* 158:189 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setAttsToApplyTo(SubstringLabelerCustomizer.this.m_attListField.getText());
/* 159:    */           
/* 160:191 */           SubstringLabelerCustomizer.this.m_list.repaint();
/* 161:    */         }
/* 162:    */       }
/* 163:195 */     });
/* 164:196 */     this.m_matchField.addPropertyChangeListener(new PropertyChangeListener()
/* 165:    */     {
/* 166:    */       public void propertyChange(PropertyChangeEvent e)
/* 167:    */       {
/* 168:199 */         Object m = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 169:200 */         if (m != null)
/* 170:    */         {
/* 171:201 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setMatch(SubstringLabelerCustomizer.this.m_matchField.getText());
/* 172:    */           
/* 173:203 */           SubstringLabelerCustomizer.this.m_list.repaint();
/* 174:    */         }
/* 175:    */       }
/* 176:207 */     });
/* 177:208 */     this.m_labelField.addPropertyChangeListener(new PropertyChangeListener()
/* 178:    */     {
/* 179:    */       public void propertyChange(PropertyChangeEvent e)
/* 180:    */       {
/* 181:211 */         Object m = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 182:212 */         if (m != null)
/* 183:    */         {
/* 184:213 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setLabel(SubstringLabelerCustomizer.this.m_labelField.getText());
/* 185:    */           
/* 186:215 */           SubstringLabelerCustomizer.this.m_list.repaint();
/* 187:    */         }
/* 188:    */       }
/* 189:219 */     });
/* 190:220 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 191:    */     {
/* 192:    */       public void valueChanged(ListSelectionEvent e)
/* 193:    */       {
/* 194:223 */         if (!e.getValueIsAdjusting())
/* 195:    */         {
/* 196:224 */           if (!SubstringLabelerCustomizer.this.m_deleteBut.isEnabled()) {
/* 197:225 */             SubstringLabelerCustomizer.this.m_deleteBut.setEnabled(true);
/* 198:    */           }
/* 199:228 */           Object entry = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 200:229 */           if (entry != null)
/* 201:    */           {
/* 202:230 */             SubstringLabelerRules.SubstringLabelerMatchRule m = (SubstringLabelerRules.SubstringLabelerMatchRule)entry;
/* 203:231 */             SubstringLabelerCustomizer.this.m_attListField.setText(m.getAttsToApplyTo());
/* 204:232 */             SubstringLabelerCustomizer.this.m_matchField.setText(m.getMatch());
/* 205:233 */             SubstringLabelerCustomizer.this.m_labelField.setText(m.getLabel());
/* 206:234 */             SubstringLabelerCustomizer.this.m_regexCheck.setSelected(m.getRegex());
/* 207:235 */             SubstringLabelerCustomizer.this.m_ignoreCaseCheck.setSelected(m.getIgnoreCase());
/* 208:    */           }
/* 209:    */         }
/* 210:    */       }
/* 211:240 */     });
/* 212:241 */     this.m_newBut.addActionListener(new ActionListener()
/* 213:    */     {
/* 214:    */       public void actionPerformed(ActionEvent e)
/* 215:    */       {
/* 216:244 */         SubstringLabelerRules.SubstringLabelerMatchRule m = new SubstringLabelerRules.SubstringLabelerMatchRule();
/* 217:    */         
/* 218:246 */         String atts = SubstringLabelerCustomizer.this.m_attListField.getText() != null ? SubstringLabelerCustomizer.this.m_attListField.getText() : "";
/* 219:    */         
/* 220:248 */         m.setAttsToApplyTo(atts);
/* 221:249 */         String match = SubstringLabelerCustomizer.this.m_matchField.getText() != null ? SubstringLabelerCustomizer.this.m_matchField.getText() : "";
/* 222:    */         
/* 223:251 */         m.setMatch(match);
/* 224:252 */         String label = SubstringLabelerCustomizer.this.m_labelField.getText() != null ? SubstringLabelerCustomizer.this.m_labelField.getText() : "";
/* 225:    */         
/* 226:254 */         m.setLabel(label);
/* 227:255 */         m.setRegex(SubstringLabelerCustomizer.this.m_regexCheck.isSelected());
/* 228:256 */         m.setIgnoreCase(SubstringLabelerCustomizer.this.m_ignoreCaseCheck.isSelected());
/* 229:    */         
/* 230:258 */         SubstringLabelerCustomizer.this.m_listModel.addElement(m);
/* 231:260 */         if (SubstringLabelerCustomizer.this.m_listModel.size() > 1)
/* 232:    */         {
/* 233:261 */           SubstringLabelerCustomizer.this.m_upBut.setEnabled(true);
/* 234:262 */           SubstringLabelerCustomizer.this.m_downBut.setEnabled(true);
/* 235:    */         }
/* 236:265 */         SubstringLabelerCustomizer.this.m_list.setSelectedIndex(SubstringLabelerCustomizer.this.m_listModel.size() - 1);
/* 237:    */       }
/* 238:268 */     });
/* 239:269 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 240:    */     {
/* 241:    */       public void actionPerformed(ActionEvent e)
/* 242:    */       {
/* 243:272 */         int selected = SubstringLabelerCustomizer.this.m_list.getSelectedIndex();
/* 244:273 */         if (selected >= 0)
/* 245:    */         {
/* 246:274 */           SubstringLabelerCustomizer.this.m_listModel.removeElementAt(selected);
/* 247:276 */           if (SubstringLabelerCustomizer.this.m_listModel.size() <= 1)
/* 248:    */           {
/* 249:277 */             SubstringLabelerCustomizer.this.m_upBut.setEnabled(false);
/* 250:278 */             SubstringLabelerCustomizer.this.m_downBut.setEnabled(false);
/* 251:    */           }
/* 252:    */         }
/* 253:    */       }
/* 254:283 */     });
/* 255:284 */     this.m_upBut.addActionListener(new ActionListener()
/* 256:    */     {
/* 257:    */       public void actionPerformed(ActionEvent e)
/* 258:    */       {
/* 259:287 */         JListHelper.moveUp(SubstringLabelerCustomizer.this.m_list);
/* 260:    */       }
/* 261:290 */     });
/* 262:291 */     this.m_downBut.addActionListener(new ActionListener()
/* 263:    */     {
/* 264:    */       public void actionPerformed(ActionEvent e)
/* 265:    */       {
/* 266:294 */         JListHelper.moveDown(SubstringLabelerCustomizer.this.m_list);
/* 267:    */       }
/* 268:297 */     });
/* 269:298 */     this.m_regexCheck.addActionListener(new ActionListener()
/* 270:    */     {
/* 271:    */       public void actionPerformed(ActionEvent e)
/* 272:    */       {
/* 273:301 */         Object m = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 274:302 */         if (m != null)
/* 275:    */         {
/* 276:303 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setRegex(SubstringLabelerCustomizer.this.m_regexCheck.isSelected());
/* 277:    */           
/* 278:305 */           SubstringLabelerCustomizer.this.m_list.repaint();
/* 279:    */         }
/* 280:    */       }
/* 281:309 */     });
/* 282:310 */     this.m_ignoreCaseCheck.addActionListener(new ActionListener()
/* 283:    */     {
/* 284:    */       public void actionPerformed(ActionEvent e)
/* 285:    */       {
/* 286:313 */         Object m = SubstringLabelerCustomizer.this.m_list.getSelectedValue();
/* 287:314 */         if (m != null)
/* 288:    */         {
/* 289:315 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setIgnoreCase(SubstringLabelerCustomizer.this.m_ignoreCaseCheck.isSelected());
/* 290:    */           
/* 291:317 */           SubstringLabelerCustomizer.this.m_list.repaint();
/* 292:    */         }
/* 293:    */       }
/* 294:    */     });
/* 295:    */   }
/* 296:    */   
/* 297:    */   private void addButtons()
/* 298:    */   {
/* 299:325 */     JButton okBut = new JButton("OK");
/* 300:326 */     JButton cancelBut = new JButton("Cancel");
/* 301:    */     
/* 302:328 */     JPanel butHolder = new JPanel();
/* 303:329 */     butHolder.setLayout(new GridLayout(1, 2));
/* 304:330 */     butHolder.add(okBut);
/* 305:331 */     butHolder.add(cancelBut);
/* 306:332 */     add(butHolder, "South");
/* 307:    */     
/* 308:334 */     okBut.addActionListener(new ActionListener()
/* 309:    */     {
/* 310:    */       public void actionPerformed(ActionEvent e)
/* 311:    */       {
/* 312:337 */         SubstringLabelerCustomizer.this.closingOK();
/* 313:    */         
/* 314:339 */         SubstringLabelerCustomizer.this.m_parent.dispose();
/* 315:    */       }
/* 316:342 */     });
/* 317:343 */     cancelBut.addActionListener(new ActionListener()
/* 318:    */     {
/* 319:    */       public void actionPerformed(ActionEvent e)
/* 320:    */       {
/* 321:346 */         SubstringLabelerCustomizer.this.closingCancel();
/* 322:    */         
/* 323:348 */         SubstringLabelerCustomizer.this.m_parent.dispose();
/* 324:    */       }
/* 325:    */     });
/* 326:    */   }
/* 327:    */   
/* 328:    */   protected void initialize()
/* 329:    */   {
/* 330:354 */     String mString = this.m_labeler.getMatchDetails();
/* 331:355 */     this.m_listModel = new DefaultListModel();
/* 332:356 */     this.m_list.setModel(this.m_listModel);
/* 333:358 */     if ((mString != null) && (mString.length() > 0))
/* 334:    */     {
/* 335:359 */       String[] parts = mString.split("@@match-rule@@");
/* 336:361 */       if (parts.length > 0)
/* 337:    */       {
/* 338:362 */         this.m_upBut.setEnabled(true);
/* 339:363 */         this.m_downBut.setEnabled(true);
/* 340:364 */         for (String mPart : parts)
/* 341:    */         {
/* 342:365 */           SubstringLabelerRules.SubstringLabelerMatchRule m = new SubstringLabelerRules.SubstringLabelerMatchRule(mPart);
/* 343:    */           
/* 344:367 */           this.m_listModel.addElement(m);
/* 345:    */         }
/* 346:370 */         this.m_list.repaint();
/* 347:    */       }
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   public void setObject(Object o)
/* 352:    */   {
/* 353:382 */     if ((o instanceof SubstringLabeler))
/* 354:    */     {
/* 355:383 */       this.m_labeler = ((SubstringLabeler)o);
/* 356:384 */       this.m_tempEditor.setTarget(o);
/* 357:385 */       setup();
/* 358:386 */       initialize();
/* 359:    */     }
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void setParentWindow(Window parent)
/* 363:    */   {
/* 364:397 */     this.m_parent = parent;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 368:    */   {
/* 369:408 */     this.m_modifyL = l;
/* 370:    */   }
/* 371:    */   
/* 372:    */   public void setEnvironment(Environment env)
/* 373:    */   {
/* 374:418 */     this.m_env = env;
/* 375:    */   }
/* 376:    */   
/* 377:    */   protected void closingOK()
/* 378:    */   {
/* 379:425 */     StringBuffer buff = new StringBuffer();
/* 380:426 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 381:    */     {
/* 382:427 */       SubstringLabelerRules.SubstringLabelerMatchRule m = (SubstringLabelerRules.SubstringLabelerMatchRule)this.m_listModel.elementAt(i);
/* 383:    */       
/* 384:    */ 
/* 385:430 */       buff.append(m.toStringInternal());
/* 386:431 */       if (i < this.m_listModel.size() - 1) {
/* 387:432 */         buff.append("@@match-rule@@");
/* 388:    */       }
/* 389:    */     }
/* 390:436 */     this.m_labeler.setMatchDetails(buff.toString());
/* 391:437 */     this.m_labeler.setNominalBinary(this.m_nominalBinaryCheck.isSelected());
/* 392:438 */     this.m_labeler.setConsumeNonMatching(this.m_consumeNonMatchingCheck.isSelected());
/* 393:439 */     this.m_labeler.setMatchAttributeName(this.m_matchAttNameField.getText());
/* 394:441 */     if (this.m_modifyL != null) {
/* 395:442 */       this.m_modifyL.setModifiedStatus(this, true);
/* 396:    */     }
/* 397:    */   }
/* 398:    */   
/* 399:    */   protected void closingCancel() {}
/* 400:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringLabelerCustomizer
 * JD-Core Version:    0.7.0.1
 */