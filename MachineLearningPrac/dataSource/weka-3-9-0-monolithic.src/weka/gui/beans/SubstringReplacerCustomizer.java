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
/*  25:    */ public class SubstringReplacerCustomizer
/*  26:    */   extends JPanel
/*  27:    */   implements EnvironmentHandler, BeanCustomizer, CustomizerCloseRequester
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -1245155414691393809L;
/*  30: 61 */   protected Environment m_env = Environment.getSystemWide();
/*  31: 62 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  32:    */   protected SubstringReplacer m_replacer;
/*  33:    */   protected EnvironmentField m_attListField;
/*  34:    */   protected EnvironmentField m_matchField;
/*  35:    */   protected EnvironmentField m_replaceField;
/*  36: 68 */   protected JCheckBox m_regexCheck = new JCheckBox();
/*  37: 69 */   protected JCheckBox m_ignoreCaseCheck = new JCheckBox();
/*  38: 70 */   protected JList m_list = new JList();
/*  39:    */   protected DefaultListModel m_listModel;
/*  40: 73 */   protected JButton m_newBut = new JButton("New");
/*  41: 74 */   protected JButton m_deleteBut = new JButton("Delete");
/*  42: 75 */   protected JButton m_upBut = new JButton("Move up");
/*  43: 76 */   protected JButton m_downBut = new JButton("Move down");
/*  44:    */   protected Window m_parent;
/*  45: 80 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  46:    */   
/*  47:    */   public SubstringReplacerCustomizer()
/*  48:    */   {
/*  49: 86 */     setLayout(new BorderLayout());
/*  50:    */   }
/*  51:    */   
/*  52:    */   private void setup()
/*  53:    */   {
/*  54: 90 */     JPanel aboutAndControlHolder = new JPanel();
/*  55: 91 */     aboutAndControlHolder.setLayout(new BorderLayout());
/*  56:    */     
/*  57: 93 */     JPanel controlHolder = new JPanel();
/*  58: 94 */     controlHolder.setLayout(new BorderLayout());
/*  59: 95 */     JPanel fieldHolder = new JPanel();
/*  60: 96 */     JPanel attListP = new JPanel();
/*  61: 97 */     attListP.setLayout(new BorderLayout());
/*  62: 98 */     attListP.setBorder(BorderFactory.createTitledBorder("Apply to attributes"));
/*  63: 99 */     this.m_attListField = new EnvironmentField(this.m_env);
/*  64:100 */     attListP.add(this.m_attListField, "Center");
/*  65:101 */     attListP.setToolTipText("<html>Accepts a range of indexes (e.g. '1,2,6-10')<br> or a comma-separated list of named attributes</html>");
/*  66:    */     
/*  67:    */ 
/*  68:104 */     JPanel matchP = new JPanel();
/*  69:105 */     matchP.setLayout(new BorderLayout());
/*  70:106 */     matchP.setBorder(BorderFactory.createTitledBorder("Match"));
/*  71:107 */     this.m_matchField = new EnvironmentField(this.m_env);
/*  72:108 */     matchP.add(this.m_matchField, "Center");
/*  73:109 */     JPanel replaceP = new JPanel();
/*  74:110 */     replaceP.setLayout(new BorderLayout());
/*  75:111 */     replaceP.setBorder(BorderFactory.createTitledBorder("Replace"));
/*  76:112 */     this.m_replaceField = new EnvironmentField(this.m_env);
/*  77:113 */     replaceP.add(this.m_replaceField, "Center");
/*  78:114 */     fieldHolder.add(attListP);
/*  79:115 */     fieldHolder.add(matchP);
/*  80:116 */     fieldHolder.add(replaceP);
/*  81:117 */     controlHolder.add(fieldHolder, "North");
/*  82:    */     
/*  83:119 */     JPanel checkHolder = new JPanel();
/*  84:120 */     checkHolder.setLayout(new GridLayout(0, 2));
/*  85:121 */     JLabel regexLab = new JLabel("Match using a regular expression", 4);
/*  86:    */     
/*  87:123 */     regexLab.setToolTipText("Use a regular expression rather than literal match");
/*  88:    */     
/*  89:125 */     checkHolder.add(regexLab);
/*  90:126 */     checkHolder.add(this.m_regexCheck);
/*  91:127 */     JLabel caseLab = new JLabel("Ignore case when matching", 4);
/*  92:    */     
/*  93:129 */     checkHolder.add(caseLab);
/*  94:130 */     checkHolder.add(this.m_ignoreCaseCheck);
/*  95:    */     
/*  96:132 */     controlHolder.add(checkHolder, "South");
/*  97:    */     
/*  98:134 */     aboutAndControlHolder.add(controlHolder, "South");
/*  99:135 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 100:136 */     aboutAndControlHolder.add(aboutP, "North");
/* 101:137 */     add(aboutAndControlHolder, "North");
/* 102:    */     
/* 103:139 */     this.m_list.setVisibleRowCount(5);
/* 104:140 */     this.m_deleteBut.setEnabled(false);
/* 105:141 */     JPanel listPanel = new JPanel();
/* 106:142 */     listPanel.setLayout(new BorderLayout());
/* 107:143 */     JPanel butHolder = new JPanel();
/* 108:144 */     butHolder.setLayout(new GridLayout(1, 0));
/* 109:145 */     butHolder.add(this.m_newBut);
/* 110:146 */     butHolder.add(this.m_deleteBut);
/* 111:147 */     butHolder.add(this.m_upBut);
/* 112:148 */     butHolder.add(this.m_downBut);
/* 113:149 */     this.m_upBut.setEnabled(false);
/* 114:150 */     this.m_downBut.setEnabled(false);
/* 115:    */     
/* 116:152 */     listPanel.add(butHolder, "North");
/* 117:153 */     JScrollPane js = new JScrollPane(this.m_list);
/* 118:154 */     js.setBorder(BorderFactory.createTitledBorder("Match-replace list (rows applied in order)"));
/* 119:    */     
/* 120:156 */     listPanel.add(js, "Center");
/* 121:157 */     add(listPanel, "Center");
/* 122:    */     
/* 123:159 */     addButtons();
/* 124:    */     
/* 125:161 */     this.m_attListField.addPropertyChangeListener(new PropertyChangeListener()
/* 126:    */     {
/* 127:    */       public void propertyChange(PropertyChangeEvent e)
/* 128:    */       {
/* 129:164 */         Object mr = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 130:165 */         if (mr != null)
/* 131:    */         {
/* 132:166 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setAttsToApplyTo(SubstringReplacerCustomizer.this.m_attListField.getText());
/* 133:    */           
/* 134:168 */           SubstringReplacerCustomizer.this.m_list.repaint();
/* 135:    */         }
/* 136:    */       }
/* 137:172 */     });
/* 138:173 */     this.m_matchField.addPropertyChangeListener(new PropertyChangeListener()
/* 139:    */     {
/* 140:    */       public void propertyChange(PropertyChangeEvent e)
/* 141:    */       {
/* 142:176 */         Object mr = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 143:177 */         if (mr != null)
/* 144:    */         {
/* 145:178 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setMatch(SubstringReplacerCustomizer.this.m_matchField.getText());
/* 146:    */           
/* 147:180 */           SubstringReplacerCustomizer.this.m_list.repaint();
/* 148:    */         }
/* 149:    */       }
/* 150:184 */     });
/* 151:185 */     this.m_replaceField.addPropertyChangeListener(new PropertyChangeListener()
/* 152:    */     {
/* 153:    */       public void propertyChange(PropertyChangeEvent e)
/* 154:    */       {
/* 155:188 */         Object mr = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 156:189 */         if (mr != null)
/* 157:    */         {
/* 158:190 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setReplace(SubstringReplacerCustomizer.this.m_replaceField.getText());
/* 159:    */           
/* 160:192 */           SubstringReplacerCustomizer.this.m_list.repaint();
/* 161:    */         }
/* 162:    */       }
/* 163:196 */     });
/* 164:197 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 165:    */     {
/* 166:    */       public void valueChanged(ListSelectionEvent e)
/* 167:    */       {
/* 168:200 */         if (!e.getValueIsAdjusting())
/* 169:    */         {
/* 170:201 */           if (!SubstringReplacerCustomizer.this.m_deleteBut.isEnabled()) {
/* 171:202 */             SubstringReplacerCustomizer.this.m_deleteBut.setEnabled(true);
/* 172:    */           }
/* 173:205 */           Object entry = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 174:206 */           if (entry != null)
/* 175:    */           {
/* 176:207 */             SubstringReplacerRules.SubstringReplacerMatchRule mr = (SubstringReplacerRules.SubstringReplacerMatchRule)entry;
/* 177:208 */             SubstringReplacerCustomizer.this.m_attListField.setText(mr.getAttsToApplyTo());
/* 178:209 */             SubstringReplacerCustomizer.this.m_matchField.setText(mr.getMatch());
/* 179:210 */             SubstringReplacerCustomizer.this.m_replaceField.setText(mr.getReplace());
/* 180:211 */             SubstringReplacerCustomizer.this.m_regexCheck.setSelected(mr.getRegex());
/* 181:212 */             SubstringReplacerCustomizer.this.m_ignoreCaseCheck.setSelected(mr.getIgnoreCase());
/* 182:    */           }
/* 183:    */         }
/* 184:    */       }
/* 185:217 */     });
/* 186:218 */     this.m_newBut.addActionListener(new ActionListener()
/* 187:    */     {
/* 188:    */       public void actionPerformed(ActionEvent e)
/* 189:    */       {
/* 190:221 */         SubstringReplacerRules.SubstringReplacerMatchRule mr = new SubstringReplacerRules.SubstringReplacerMatchRule();
/* 191:    */         
/* 192:223 */         String atts = SubstringReplacerCustomizer.this.m_attListField.getText() != null ? SubstringReplacerCustomizer.this.m_attListField.getText() : "";
/* 193:    */         
/* 194:225 */         mr.setAttsToApplyTo(atts);
/* 195:226 */         String match = SubstringReplacerCustomizer.this.m_matchField.getText() != null ? SubstringReplacerCustomizer.this.m_matchField.getText() : "";
/* 196:    */         
/* 197:228 */         mr.setMatch(match);
/* 198:229 */         String replace = SubstringReplacerCustomizer.this.m_replaceField.getText() != null ? SubstringReplacerCustomizer.this.m_replaceField.getText() : "";
/* 199:    */         
/* 200:231 */         mr.setReplace(replace);
/* 201:232 */         mr.setRegex(SubstringReplacerCustomizer.this.m_regexCheck.isSelected());
/* 202:233 */         mr.setIgnoreCase(SubstringReplacerCustomizer.this.m_ignoreCaseCheck.isSelected());
/* 203:    */         
/* 204:235 */         SubstringReplacerCustomizer.this.m_listModel.addElement(mr);
/* 205:237 */         if (SubstringReplacerCustomizer.this.m_listModel.size() > 1)
/* 206:    */         {
/* 207:238 */           SubstringReplacerCustomizer.this.m_upBut.setEnabled(true);
/* 208:239 */           SubstringReplacerCustomizer.this.m_downBut.setEnabled(true);
/* 209:    */         }
/* 210:242 */         SubstringReplacerCustomizer.this.m_list.setSelectedIndex(SubstringReplacerCustomizer.this.m_listModel.size() - 1);
/* 211:    */       }
/* 212:245 */     });
/* 213:246 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 214:    */     {
/* 215:    */       public void actionPerformed(ActionEvent e)
/* 216:    */       {
/* 217:249 */         int selected = SubstringReplacerCustomizer.this.m_list.getSelectedIndex();
/* 218:250 */         if (selected >= 0)
/* 219:    */         {
/* 220:251 */           SubstringReplacerCustomizer.this.m_listModel.removeElementAt(selected);
/* 221:253 */           if (SubstringReplacerCustomizer.this.m_listModel.size() <= 1)
/* 222:    */           {
/* 223:254 */             SubstringReplacerCustomizer.this.m_upBut.setEnabled(false);
/* 224:255 */             SubstringReplacerCustomizer.this.m_downBut.setEnabled(false);
/* 225:    */           }
/* 226:    */         }
/* 227:    */       }
/* 228:260 */     });
/* 229:261 */     this.m_upBut.addActionListener(new ActionListener()
/* 230:    */     {
/* 231:    */       public void actionPerformed(ActionEvent e)
/* 232:    */       {
/* 233:264 */         JListHelper.moveUp(SubstringReplacerCustomizer.this.m_list);
/* 234:    */       }
/* 235:267 */     });
/* 236:268 */     this.m_downBut.addActionListener(new ActionListener()
/* 237:    */     {
/* 238:    */       public void actionPerformed(ActionEvent e)
/* 239:    */       {
/* 240:271 */         JListHelper.moveDown(SubstringReplacerCustomizer.this.m_list);
/* 241:    */       }
/* 242:274 */     });
/* 243:275 */     this.m_regexCheck.addActionListener(new ActionListener()
/* 244:    */     {
/* 245:    */       public void actionPerformed(ActionEvent e)
/* 246:    */       {
/* 247:278 */         Object mr = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 248:279 */         if (mr != null)
/* 249:    */         {
/* 250:280 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setRegex(SubstringReplacerCustomizer.this.m_regexCheck.isSelected());
/* 251:    */           
/* 252:282 */           SubstringReplacerCustomizer.this.m_list.repaint();
/* 253:    */         }
/* 254:    */       }
/* 255:286 */     });
/* 256:287 */     this.m_ignoreCaseCheck.addActionListener(new ActionListener()
/* 257:    */     {
/* 258:    */       public void actionPerformed(ActionEvent e)
/* 259:    */       {
/* 260:290 */         Object mr = SubstringReplacerCustomizer.this.m_list.getSelectedValue();
/* 261:291 */         if (mr != null)
/* 262:    */         {
/* 263:292 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setIgnoreCase(SubstringReplacerCustomizer.this.m_ignoreCaseCheck.isSelected());
/* 264:    */           
/* 265:294 */           SubstringReplacerCustomizer.this.m_list.repaint();
/* 266:    */         }
/* 267:    */       }
/* 268:    */     });
/* 269:    */   }
/* 270:    */   
/* 271:    */   private void addButtons()
/* 272:    */   {
/* 273:301 */     JButton okBut = new JButton("OK");
/* 274:302 */     JButton cancelBut = new JButton("Cancel");
/* 275:    */     
/* 276:304 */     JPanel butHolder = new JPanel();
/* 277:305 */     butHolder.setLayout(new GridLayout(1, 2));
/* 278:306 */     butHolder.add(okBut);
/* 279:307 */     butHolder.add(cancelBut);
/* 280:308 */     add(butHolder, "South");
/* 281:    */     
/* 282:310 */     okBut.addActionListener(new ActionListener()
/* 283:    */     {
/* 284:    */       public void actionPerformed(ActionEvent e)
/* 285:    */       {
/* 286:313 */         SubstringReplacerCustomizer.this.closingOK();
/* 287:    */         
/* 288:315 */         SubstringReplacerCustomizer.this.m_parent.dispose();
/* 289:    */       }
/* 290:318 */     });
/* 291:319 */     cancelBut.addActionListener(new ActionListener()
/* 292:    */     {
/* 293:    */       public void actionPerformed(ActionEvent e)
/* 294:    */       {
/* 295:322 */         SubstringReplacerCustomizer.this.closingCancel();
/* 296:    */         
/* 297:324 */         SubstringReplacerCustomizer.this.m_parent.dispose();
/* 298:    */       }
/* 299:    */     });
/* 300:    */   }
/* 301:    */   
/* 302:    */   public void setEnvironment(Environment env)
/* 303:    */   {
/* 304:336 */     this.m_env = env;
/* 305:    */   }
/* 306:    */   
/* 307:    */   protected void initialize()
/* 308:    */   {
/* 309:340 */     String mrString = this.m_replacer.getMatchReplaceDetails();
/* 310:341 */     this.m_listModel = new DefaultListModel();
/* 311:342 */     this.m_list.setModel(this.m_listModel);
/* 312:344 */     if ((mrString != null) && (mrString.length() > 0))
/* 313:    */     {
/* 314:345 */       String[] parts = mrString.split("@@match-replace@@");
/* 315:347 */       if (parts.length > 0)
/* 316:    */       {
/* 317:348 */         this.m_upBut.setEnabled(true);
/* 318:349 */         this.m_downBut.setEnabled(true);
/* 319:350 */         for (String mrPart : parts)
/* 320:    */         {
/* 321:351 */           SubstringReplacerRules.SubstringReplacerMatchRule mr = new SubstringReplacerRules.SubstringReplacerMatchRule(mrPart);
/* 322:    */           
/* 323:353 */           this.m_listModel.addElement(mr);
/* 324:    */         }
/* 325:356 */         this.m_list.repaint();
/* 326:    */       }
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void setObject(Object o)
/* 331:    */   {
/* 332:368 */     if ((o instanceof SubstringReplacer))
/* 333:    */     {
/* 334:369 */       this.m_replacer = ((SubstringReplacer)o);
/* 335:370 */       this.m_tempEditor.setTarget(o);
/* 336:371 */       setup();
/* 337:372 */       initialize();
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 342:    */   {
/* 343:384 */     this.m_modifyL = l;
/* 344:    */   }
/* 345:    */   
/* 346:    */   protected void closingOK()
/* 347:    */   {
/* 348:391 */     StringBuffer buff = new StringBuffer();
/* 349:392 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 350:    */     {
/* 351:393 */       SubstringReplacerRules.SubstringReplacerMatchRule mr = (SubstringReplacerRules.SubstringReplacerMatchRule)this.m_listModel.elementAt(i);
/* 352:    */       
/* 353:    */ 
/* 354:396 */       buff.append(mr.toStringInternal());
/* 355:397 */       if (i < this.m_listModel.size() - 1) {
/* 356:398 */         buff.append("@@match-replace@@");
/* 357:    */       }
/* 358:    */     }
/* 359:402 */     this.m_replacer.setMatchReplaceDetails(buff.toString());
/* 360:403 */     if (this.m_modifyL != null) {
/* 361:404 */       this.m_modifyL.setModifiedStatus(this, true);
/* 362:    */     }
/* 363:    */   }
/* 364:    */   
/* 365:    */   protected void closingCancel() {}
/* 366:    */   
/* 367:    */   public void setParentWindow(Window parent)
/* 368:    */   {
/* 369:422 */     this.m_parent = parent;
/* 370:    */   }
/* 371:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringReplacerCustomizer
 * JD-Core Version:    0.7.0.1
 */