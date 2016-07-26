/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.beans.PropertyChangeEvent;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import javax.swing.BorderFactory;
/*  10:    */ import javax.swing.DefaultListModel;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JCheckBox;
/*  13:    */ import javax.swing.JLabel;
/*  14:    */ import javax.swing.JList;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JScrollPane;
/*  17:    */ import javax.swing.event.ListSelectionEvent;
/*  18:    */ import javax.swing.event.ListSelectionListener;
/*  19:    */ import weka.gui.EnvironmentField;
/*  20:    */ import weka.gui.JListHelper;
/*  21:    */ import weka.gui.beans.SubstringLabelerRules.SubstringLabelerMatchRule;
/*  22:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  23:    */ import weka.knowledgeflow.steps.SubstringLabeler;
/*  24:    */ 
/*  25:    */ public class SubstringLabelerStepEditorDialog
/*  26:    */   extends StepEditorDialog
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 2386951012941540643L;
/*  29:    */   protected EnvironmentField m_matchAttNameField;
/*  30:    */   protected EnvironmentField m_attListField;
/*  31:    */   protected EnvironmentField m_matchField;
/*  32:    */   protected EnvironmentField m_labelField;
/*  33: 71 */   protected JCheckBox m_regexCheck = new JCheckBox();
/*  34: 74 */   protected JCheckBox m_ignoreCaseCheck = new JCheckBox();
/*  35: 80 */   protected JCheckBox m_nominalBinaryCheck = new JCheckBox();
/*  36: 86 */   protected JCheckBox m_consumeNonMatchingCheck = new JCheckBox();
/*  37: 89 */   protected JList<SubstringLabelerRules.SubstringLabelerMatchRule> m_list = new JList();
/*  38:    */   protected DefaultListModel<SubstringLabelerRules.SubstringLabelerMatchRule> m_listModel;
/*  39: 96 */   protected JButton m_newBut = new JButton("New");
/*  40: 99 */   protected JButton m_deleteBut = new JButton("Delete");
/*  41:102 */   protected JButton m_upBut = new JButton("Move up");
/*  42:105 */   protected JButton m_downBut = new JButton("Move down");
/*  43:    */   
/*  44:    */   protected void initialize()
/*  45:    */   {
/*  46:111 */     String mlString = ((SubstringLabeler)getStepToEdit()).getMatchDetails();
/*  47:112 */     this.m_listModel = new DefaultListModel();
/*  48:    */     
/*  49:114 */     this.m_list.setModel(this.m_listModel);
/*  50:115 */     if ((mlString != null) && (mlString.length() > 0))
/*  51:    */     {
/*  52:116 */       String[] parts = mlString.split("@@match-rule@@");
/*  53:118 */       if (parts.length > 0)
/*  54:    */       {
/*  55:119 */         this.m_upBut.setEnabled(true);
/*  56:120 */         this.m_downBut.setEnabled(true);
/*  57:121 */         for (String mPart : parts)
/*  58:    */         {
/*  59:122 */           SubstringLabelerRules.SubstringLabelerMatchRule m = new SubstringLabelerRules.SubstringLabelerMatchRule(mPart);
/*  60:    */           
/*  61:124 */           this.m_listModel.addElement(m);
/*  62:    */         }
/*  63:127 */         this.m_list.repaint();
/*  64:    */       }
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected void layoutEditor()
/*  69:    */   {
/*  70:137 */     initialize();
/*  71:138 */     JPanel mainHolder = new JPanel(new BorderLayout());
/*  72:    */     
/*  73:140 */     JPanel controlHolder = new JPanel();
/*  74:141 */     controlHolder.setLayout(new BorderLayout());
/*  75:142 */     JPanel fieldHolder = new JPanel();
/*  76:143 */     JPanel attListP = new JPanel();
/*  77:144 */     attListP.setLayout(new BorderLayout());
/*  78:145 */     attListP.setBorder(BorderFactory.createTitledBorder("Apply to attributes"));
/*  79:146 */     this.m_attListField = new EnvironmentField(this.m_env);
/*  80:147 */     attListP.add(this.m_attListField, "Center");
/*  81:148 */     attListP.setToolTipText("<html>Accepts a range of indexes (e.g. '1,2,6-10')<br> or a comma-separated list of named attributes</html>");
/*  82:    */     
/*  83:    */ 
/*  84:151 */     JPanel matchP = new JPanel();
/*  85:152 */     matchP.setLayout(new BorderLayout());
/*  86:153 */     matchP.setBorder(BorderFactory.createTitledBorder("Match"));
/*  87:154 */     this.m_matchField = new EnvironmentField(this.m_env);
/*  88:155 */     matchP.add(this.m_matchField, "Center");
/*  89:156 */     JPanel labelP = new JPanel();
/*  90:157 */     labelP.setLayout(new BorderLayout());
/*  91:158 */     labelP.setBorder(BorderFactory.createTitledBorder("Label"));
/*  92:159 */     this.m_labelField = new EnvironmentField(this.m_env);
/*  93:160 */     labelP.add(this.m_labelField, "Center");
/*  94:161 */     fieldHolder.add(attListP);
/*  95:162 */     fieldHolder.add(matchP);
/*  96:163 */     fieldHolder.add(labelP);
/*  97:    */     
/*  98:165 */     controlHolder.add(fieldHolder, "North");
/*  99:    */     
/* 100:167 */     JPanel checkHolder = new JPanel();
/* 101:168 */     checkHolder.setLayout(new GridLayout(0, 2));
/* 102:169 */     JLabel attNameLab = new JLabel("Name of label attribute", 4);
/* 103:    */     
/* 104:171 */     checkHolder.add(attNameLab);
/* 105:172 */     this.m_matchAttNameField = new EnvironmentField(this.m_env);
/* 106:173 */     this.m_matchAttNameField.setText(((SubstringLabeler)getStepToEdit()).getMatchAttributeName());
/* 107:    */     
/* 108:175 */     checkHolder.add(this.m_matchAttNameField);
/* 109:176 */     JLabel regexLab = new JLabel("Match using a regular expression", 4);
/* 110:    */     
/* 111:178 */     regexLab.setToolTipText("Use a regular expression rather than literal match");
/* 112:    */     
/* 113:180 */     checkHolder.add(regexLab);
/* 114:181 */     checkHolder.add(this.m_regexCheck);
/* 115:182 */     JLabel caseLab = new JLabel("Ignore case when matching", 4);
/* 116:    */     
/* 117:184 */     checkHolder.add(caseLab);
/* 118:185 */     checkHolder.add(this.m_ignoreCaseCheck);
/* 119:186 */     JLabel nominalBinaryLab = new JLabel("Make binary label attribute nominal", 4);
/* 120:    */     
/* 121:188 */     nominalBinaryLab.setToolTipText("<html>If the label attribute is binary (i.e. no <br>explicit labels have been declared) then<br>this makes the resulting attribute nominal<br>rather than numeric.</html>");
/* 122:    */     
/* 123:    */ 
/* 124:    */ 
/* 125:192 */     checkHolder.add(nominalBinaryLab);
/* 126:193 */     checkHolder.add(this.m_nominalBinaryCheck);
/* 127:194 */     this.m_nominalBinaryCheck.setSelected(((SubstringLabeler)getStepToEdit()).getNominalBinary());
/* 128:    */     
/* 129:196 */     JLabel consumeNonMatchLab = new JLabel("Consume non-matching instances", 4);
/* 130:    */     
/* 131:198 */     consumeNonMatchLab.setToolTipText("<html>When explicit labels have been defined, consume <br>(rather than output with missing value) instances</html>");
/* 132:    */     
/* 133:    */ 
/* 134:201 */     checkHolder.add(consumeNonMatchLab);
/* 135:202 */     checkHolder.add(this.m_consumeNonMatchingCheck);
/* 136:203 */     this.m_consumeNonMatchingCheck.setSelected(((SubstringLabeler)getStepToEdit()).getConsumeNonMatching());
/* 137:    */     
/* 138:    */ 
/* 139:206 */     controlHolder.add(checkHolder, "South");
/* 140:207 */     mainHolder.add(controlHolder, "North");
/* 141:    */     
/* 142:209 */     this.m_list.setVisibleRowCount(5);
/* 143:210 */     this.m_deleteBut.setEnabled(false);
/* 144:211 */     JPanel listPanel = new JPanel();
/* 145:212 */     listPanel.setLayout(new BorderLayout());
/* 146:213 */     JPanel butHolder = new JPanel();
/* 147:214 */     butHolder.setLayout(new GridLayout(1, 0));
/* 148:215 */     butHolder.add(this.m_newBut);
/* 149:216 */     butHolder.add(this.m_deleteBut);
/* 150:217 */     butHolder.add(this.m_upBut);
/* 151:218 */     butHolder.add(this.m_downBut);
/* 152:219 */     this.m_upBut.setEnabled(false);
/* 153:220 */     this.m_downBut.setEnabled(false);
/* 154:    */     
/* 155:222 */     listPanel.add(butHolder, "North");
/* 156:223 */     JScrollPane js = new JScrollPane(this.m_list);
/* 157:224 */     js.setBorder(BorderFactory.createTitledBorder("Match-list list (rows applied in order)"));
/* 158:    */     
/* 159:226 */     listPanel.add(js, "Center");
/* 160:    */     
/* 161:228 */     mainHolder.add(listPanel, "Center");
/* 162:229 */     add(mainHolder, "Center");
/* 163:    */     
/* 164:231 */     this.m_attListField.addPropertyChangeListener(new PropertyChangeListener()
/* 165:    */     {
/* 166:    */       public void propertyChange(PropertyChangeEvent e)
/* 167:    */       {
/* 168:234 */         Object m = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 169:235 */         if (m != null)
/* 170:    */         {
/* 171:236 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setAttsToApplyTo(SubstringLabelerStepEditorDialog.this.m_attListField.getText());
/* 172:    */           
/* 173:238 */           SubstringLabelerStepEditorDialog.this.m_list.repaint();
/* 174:    */         }
/* 175:    */       }
/* 176:242 */     });
/* 177:243 */     this.m_matchField.addPropertyChangeListener(new PropertyChangeListener()
/* 178:    */     {
/* 179:    */       public void propertyChange(PropertyChangeEvent e)
/* 180:    */       {
/* 181:246 */         Object m = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 182:247 */         if (m != null)
/* 183:    */         {
/* 184:248 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setMatch(SubstringLabelerStepEditorDialog.this.m_matchField.getText());
/* 185:    */           
/* 186:250 */           SubstringLabelerStepEditorDialog.this.m_list.repaint();
/* 187:    */         }
/* 188:    */       }
/* 189:254 */     });
/* 190:255 */     this.m_labelField.addPropertyChangeListener(new PropertyChangeListener()
/* 191:    */     {
/* 192:    */       public void propertyChange(PropertyChangeEvent e)
/* 193:    */       {
/* 194:258 */         Object m = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 195:259 */         if (m != null)
/* 196:    */         {
/* 197:260 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setLabel(SubstringLabelerStepEditorDialog.this.m_labelField.getText());
/* 198:    */           
/* 199:262 */           SubstringLabelerStepEditorDialog.this.m_list.repaint();
/* 200:    */         }
/* 201:    */       }
/* 202:266 */     });
/* 203:267 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 204:    */     {
/* 205:    */       public void valueChanged(ListSelectionEvent e)
/* 206:    */       {
/* 207:270 */         if (!e.getValueIsAdjusting())
/* 208:    */         {
/* 209:271 */           if (!SubstringLabelerStepEditorDialog.this.m_deleteBut.isEnabled()) {
/* 210:272 */             SubstringLabelerStepEditorDialog.this.m_deleteBut.setEnabled(true);
/* 211:    */           }
/* 212:274 */           SubstringLabelerStepEditorDialog.this.checkUpDown();
/* 213:    */           
/* 214:276 */           Object entry = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 215:277 */           if (entry != null)
/* 216:    */           {
/* 217:278 */             SubstringLabelerRules.SubstringLabelerMatchRule m = (SubstringLabelerRules.SubstringLabelerMatchRule)entry;
/* 218:    */             
/* 219:280 */             SubstringLabelerStepEditorDialog.this.m_attListField.setText(m.getAttsToApplyTo());
/* 220:281 */             SubstringLabelerStepEditorDialog.this.m_matchField.setText(m.getMatch());
/* 221:282 */             SubstringLabelerStepEditorDialog.this.m_labelField.setText(m.getLabel());
/* 222:283 */             SubstringLabelerStepEditorDialog.this.m_regexCheck.setSelected(m.getRegex());
/* 223:284 */             SubstringLabelerStepEditorDialog.this.m_ignoreCaseCheck.setSelected(m.getIgnoreCase());
/* 224:    */           }
/* 225:    */         }
/* 226:    */       }
/* 227:289 */     });
/* 228:290 */     this.m_newBut.addActionListener(new ActionListener()
/* 229:    */     {
/* 230:    */       public void actionPerformed(ActionEvent e)
/* 231:    */       {
/* 232:293 */         SubstringLabelerRules.SubstringLabelerMatchRule m = new SubstringLabelerRules.SubstringLabelerMatchRule();
/* 233:    */         
/* 234:    */ 
/* 235:296 */         String atts = SubstringLabelerStepEditorDialog.this.m_attListField.getText() != null ? SubstringLabelerStepEditorDialog.this.m_attListField.getText() : "";
/* 236:    */         
/* 237:298 */         m.setAttsToApplyTo(atts);
/* 238:299 */         String match = SubstringLabelerStepEditorDialog.this.m_matchField.getText() != null ? SubstringLabelerStepEditorDialog.this.m_matchField.getText() : "";
/* 239:    */         
/* 240:301 */         m.setMatch(match);
/* 241:302 */         String label = SubstringLabelerStepEditorDialog.this.m_labelField.getText() != null ? SubstringLabelerStepEditorDialog.this.m_labelField.getText() : "";
/* 242:    */         
/* 243:304 */         m.setLabel(label);
/* 244:305 */         m.setRegex(SubstringLabelerStepEditorDialog.this.m_regexCheck.isSelected());
/* 245:306 */         m.setIgnoreCase(SubstringLabelerStepEditorDialog.this.m_ignoreCaseCheck.isSelected());
/* 246:    */         
/* 247:308 */         SubstringLabelerStepEditorDialog.this.m_listModel.addElement(m);
/* 248:310 */         if (SubstringLabelerStepEditorDialog.this.m_listModel.size() > 1)
/* 249:    */         {
/* 250:311 */           SubstringLabelerStepEditorDialog.this.m_upBut.setEnabled(true);
/* 251:312 */           SubstringLabelerStepEditorDialog.this.m_downBut.setEnabled(true);
/* 252:    */         }
/* 253:315 */         SubstringLabelerStepEditorDialog.this.m_list.setSelectedIndex(SubstringLabelerStepEditorDialog.this.m_listModel.size() - 1);
/* 254:316 */         SubstringLabelerStepEditorDialog.this.checkUpDown();
/* 255:    */       }
/* 256:319 */     });
/* 257:320 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 258:    */     {
/* 259:    */       public void actionPerformed(ActionEvent e)
/* 260:    */       {
/* 261:323 */         int selected = SubstringLabelerStepEditorDialog.this.m_list.getSelectedIndex();
/* 262:324 */         if (selected >= 0)
/* 263:    */         {
/* 264:325 */           SubstringLabelerStepEditorDialog.this.m_listModel.removeElementAt(selected);
/* 265:    */           
/* 266:327 */           SubstringLabelerStepEditorDialog.this.checkUpDown();
/* 267:328 */           if (SubstringLabelerStepEditorDialog.this.m_listModel.size() <= 1)
/* 268:    */           {
/* 269:329 */             SubstringLabelerStepEditorDialog.this.m_upBut.setEnabled(false);
/* 270:330 */             SubstringLabelerStepEditorDialog.this.m_downBut.setEnabled(false);
/* 271:    */           }
/* 272:    */         }
/* 273:    */       }
/* 274:335 */     });
/* 275:336 */     this.m_upBut.addActionListener(new ActionListener()
/* 276:    */     {
/* 277:    */       public void actionPerformed(ActionEvent e)
/* 278:    */       {
/* 279:339 */         JListHelper.moveUp(SubstringLabelerStepEditorDialog.this.m_list);
/* 280:340 */         SubstringLabelerStepEditorDialog.this.checkUpDown();
/* 281:    */       }
/* 282:343 */     });
/* 283:344 */     this.m_downBut.addActionListener(new ActionListener()
/* 284:    */     {
/* 285:    */       public void actionPerformed(ActionEvent e)
/* 286:    */       {
/* 287:347 */         JListHelper.moveDown(SubstringLabelerStepEditorDialog.this.m_list);
/* 288:348 */         SubstringLabelerStepEditorDialog.this.checkUpDown();
/* 289:    */       }
/* 290:351 */     });
/* 291:352 */     this.m_regexCheck.addActionListener(new ActionListener()
/* 292:    */     {
/* 293:    */       public void actionPerformed(ActionEvent e)
/* 294:    */       {
/* 295:355 */         Object m = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 296:356 */         if (m != null)
/* 297:    */         {
/* 298:357 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setRegex(SubstringLabelerStepEditorDialog.this.m_regexCheck.isSelected());
/* 299:    */           
/* 300:359 */           SubstringLabelerStepEditorDialog.this.m_list.repaint();
/* 301:    */         }
/* 302:    */       }
/* 303:363 */     });
/* 304:364 */     this.m_ignoreCaseCheck.addActionListener(new ActionListener()
/* 305:    */     {
/* 306:    */       public void actionPerformed(ActionEvent e)
/* 307:    */       {
/* 308:367 */         Object m = SubstringLabelerStepEditorDialog.this.m_list.getSelectedValue();
/* 309:368 */         if (m != null)
/* 310:    */         {
/* 311:369 */           ((SubstringLabelerRules.SubstringLabelerMatchRule)m).setIgnoreCase(SubstringLabelerStepEditorDialog.this.m_ignoreCaseCheck.isSelected());
/* 312:    */           
/* 313:371 */           SubstringLabelerStepEditorDialog.this.m_list.repaint();
/* 314:    */         }
/* 315:    */       }
/* 316:    */     });
/* 317:    */   }
/* 318:    */   
/* 319:    */   protected void checkUpDown()
/* 320:    */   {
/* 321:382 */     if ((this.m_list.getSelectedValue() != null) && (this.m_listModel.size() > 1))
/* 322:    */     {
/* 323:383 */       this.m_upBut.setEnabled(this.m_list.getSelectedIndex() > 0);
/* 324:384 */       this.m_downBut.setEnabled(this.m_list.getSelectedIndex() < this.m_listModel.size() - 1);
/* 325:    */     }
/* 326:    */   }
/* 327:    */   
/* 328:    */   protected void okPressed()
/* 329:    */   {
/* 330:393 */     StringBuilder buff = new StringBuilder();
/* 331:394 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 332:    */     {
/* 333:395 */       SubstringLabelerRules.SubstringLabelerMatchRule mr = (SubstringLabelerRules.SubstringLabelerMatchRule)this.m_listModel.elementAt(i);
/* 334:    */       
/* 335:    */ 
/* 336:    */ 
/* 337:399 */       buff.append(mr.toStringInternal());
/* 338:400 */       if (i < this.m_listModel.size() - 1) {
/* 339:401 */         buff.append("@@match-rule@@");
/* 340:    */       }
/* 341:    */     }
/* 342:405 */     ((SubstringLabeler)getStepToEdit()).setMatchDetails(buff.toString());
/* 343:    */   }
/* 344:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.SubstringLabelerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */