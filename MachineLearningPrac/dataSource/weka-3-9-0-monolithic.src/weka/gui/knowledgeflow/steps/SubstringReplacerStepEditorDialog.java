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
/*  21:    */ import weka.gui.beans.SubstringReplacerRules.SubstringReplacerMatchRule;
/*  22:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  23:    */ import weka.knowledgeflow.steps.SubstringReplacer;
/*  24:    */ 
/*  25:    */ public class SubstringReplacerStepEditorDialog
/*  26:    */   extends StepEditorDialog
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 7804721324137987443L;
/*  29:    */   protected EnvironmentField m_attListField;
/*  30:    */   protected EnvironmentField m_matchField;
/*  31:    */   protected EnvironmentField m_replaceField;
/*  32: 68 */   protected JCheckBox m_regexCheck = new JCheckBox();
/*  33: 71 */   protected JCheckBox m_ignoreCaseCheck = new JCheckBox();
/*  34: 74 */   protected JList<SubstringReplacerRules.SubstringReplacerMatchRule> m_list = new JList();
/*  35:    */   protected DefaultListModel<SubstringReplacerRules.SubstringReplacerMatchRule> m_listModel;
/*  36: 81 */   protected JButton m_newBut = new JButton("New");
/*  37: 84 */   protected JButton m_deleteBut = new JButton("Delete");
/*  38: 87 */   protected JButton m_upBut = new JButton("Move up");
/*  39: 90 */   protected JButton m_downBut = new JButton("Move down");
/*  40:    */   
/*  41:    */   protected void initialize()
/*  42:    */   {
/*  43: 96 */     String mrString = ((SubstringReplacer)getStepToEdit()).getMatchReplaceDetails();
/*  44:    */     
/*  45: 98 */     this.m_listModel = new DefaultListModel();
/*  46:    */     
/*  47:100 */     this.m_list.setModel(this.m_listModel);
/*  48:101 */     if ((mrString != null) && (mrString.length() > 0))
/*  49:    */     {
/*  50:102 */       String[] parts = mrString.split("@@match-replace@@");
/*  51:104 */       if (parts.length > 0)
/*  52:    */       {
/*  53:105 */         this.m_upBut.setEnabled(true);
/*  54:106 */         this.m_downBut.setEnabled(true);
/*  55:107 */         for (String mrPart : parts)
/*  56:    */         {
/*  57:108 */           SubstringReplacerRules.SubstringReplacerMatchRule mr = new SubstringReplacerRules.SubstringReplacerMatchRule(mrPart);
/*  58:    */           
/*  59:110 */           this.m_listModel.addElement(mr);
/*  60:    */         }
/*  61:113 */         this.m_list.repaint();
/*  62:    */       }
/*  63:    */     }
/*  64:    */   }
/*  65:    */   
/*  66:    */   protected void layoutEditor()
/*  67:    */   {
/*  68:123 */     initialize();
/*  69:124 */     JPanel mainHolder = new JPanel(new BorderLayout());
/*  70:    */     
/*  71:126 */     JPanel controlHolder = new JPanel();
/*  72:127 */     controlHolder.setLayout(new BorderLayout());
/*  73:128 */     JPanel fieldHolder = new JPanel();
/*  74:129 */     JPanel attListP = new JPanel();
/*  75:130 */     attListP.setLayout(new BorderLayout());
/*  76:131 */     attListP.setBorder(BorderFactory.createTitledBorder("Apply to attributes"));
/*  77:132 */     this.m_attListField = new EnvironmentField(this.m_env);
/*  78:133 */     attListP.add(this.m_attListField, "Center");
/*  79:134 */     attListP.setToolTipText("<html>Accepts a range of indexes (e.g. '1,2,6-10')<br> or a comma-separated list of named attributes</html>");
/*  80:    */     
/*  81:    */ 
/*  82:137 */     JPanel matchP = new JPanel();
/*  83:138 */     matchP.setLayout(new BorderLayout());
/*  84:139 */     matchP.setBorder(BorderFactory.createTitledBorder("Match"));
/*  85:140 */     this.m_matchField = new EnvironmentField(this.m_env);
/*  86:141 */     matchP.add(this.m_matchField, "Center");
/*  87:142 */     JPanel replaceP = new JPanel();
/*  88:143 */     replaceP.setLayout(new BorderLayout());
/*  89:144 */     replaceP.setBorder(BorderFactory.createTitledBorder("Replace"));
/*  90:145 */     this.m_replaceField = new EnvironmentField(this.m_env);
/*  91:146 */     replaceP.add(this.m_replaceField, "Center");
/*  92:147 */     fieldHolder.add(attListP);
/*  93:148 */     fieldHolder.add(matchP);
/*  94:149 */     fieldHolder.add(replaceP);
/*  95:150 */     controlHolder.add(fieldHolder, "North");
/*  96:    */     
/*  97:152 */     JPanel checkHolder = new JPanel();
/*  98:153 */     checkHolder.setLayout(new GridLayout(0, 2));
/*  99:154 */     JLabel regexLab = new JLabel("Match using a regular expression", 4);
/* 100:    */     
/* 101:156 */     regexLab.setToolTipText("Use a regular expression rather than literal match");
/* 102:    */     
/* 103:158 */     checkHolder.add(regexLab);
/* 104:159 */     checkHolder.add(this.m_regexCheck);
/* 105:160 */     JLabel caseLab = new JLabel("Ignore case when matching", 4);
/* 106:    */     
/* 107:162 */     checkHolder.add(caseLab);
/* 108:163 */     checkHolder.add(this.m_ignoreCaseCheck);
/* 109:    */     
/* 110:165 */     controlHolder.add(checkHolder, "South");
/* 111:    */     
/* 112:167 */     mainHolder.add(controlHolder, "North");
/* 113:    */     
/* 114:169 */     this.m_list.setVisibleRowCount(5);
/* 115:170 */     this.m_deleteBut.setEnabled(false);
/* 116:171 */     JPanel listPanel = new JPanel();
/* 117:172 */     listPanel.setLayout(new BorderLayout());
/* 118:173 */     JPanel butHolder = new JPanel();
/* 119:174 */     butHolder.setLayout(new GridLayout(1, 0));
/* 120:175 */     butHolder.add(this.m_newBut);
/* 121:176 */     butHolder.add(this.m_deleteBut);
/* 122:177 */     butHolder.add(this.m_upBut);
/* 123:178 */     butHolder.add(this.m_downBut);
/* 124:179 */     this.m_upBut.setEnabled(false);
/* 125:180 */     this.m_downBut.setEnabled(false);
/* 126:    */     
/* 127:182 */     listPanel.add(butHolder, "North");
/* 128:183 */     JScrollPane js = new JScrollPane(this.m_list);
/* 129:184 */     js.setBorder(BorderFactory.createTitledBorder("Match-replace list (rows applied in order)"));
/* 130:    */     
/* 131:186 */     listPanel.add(js, "Center");
/* 132:    */     
/* 133:188 */     mainHolder.add(listPanel, "Center");
/* 134:189 */     add(mainHolder, "Center");
/* 135:    */     
/* 136:191 */     this.m_attListField.addPropertyChangeListener(new PropertyChangeListener()
/* 137:    */     {
/* 138:    */       public void propertyChange(PropertyChangeEvent e)
/* 139:    */       {
/* 140:194 */         Object mr = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 141:195 */         if (mr != null)
/* 142:    */         {
/* 143:196 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setAttsToApplyTo(SubstringReplacerStepEditorDialog.this.m_attListField.getText());
/* 144:    */           
/* 145:198 */           SubstringReplacerStepEditorDialog.this.m_list.repaint();
/* 146:    */         }
/* 147:    */       }
/* 148:202 */     });
/* 149:203 */     this.m_matchField.addPropertyChangeListener(new PropertyChangeListener()
/* 150:    */     {
/* 151:    */       public void propertyChange(PropertyChangeEvent e)
/* 152:    */       {
/* 153:206 */         Object mr = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 154:207 */         if (mr != null)
/* 155:    */         {
/* 156:208 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setMatch(SubstringReplacerStepEditorDialog.this.m_matchField.getText());
/* 157:    */           
/* 158:210 */           SubstringReplacerStepEditorDialog.this.m_list.repaint();
/* 159:    */         }
/* 160:    */       }
/* 161:214 */     });
/* 162:215 */     this.m_replaceField.addPropertyChangeListener(new PropertyChangeListener()
/* 163:    */     {
/* 164:    */       public void propertyChange(PropertyChangeEvent e)
/* 165:    */       {
/* 166:218 */         Object mr = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 167:219 */         if (mr != null)
/* 168:    */         {
/* 169:220 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setReplace(SubstringReplacerStepEditorDialog.this.m_replaceField.getText());
/* 170:    */           
/* 171:222 */           SubstringReplacerStepEditorDialog.this.m_list.repaint();
/* 172:    */         }
/* 173:    */       }
/* 174:226 */     });
/* 175:227 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 176:    */     {
/* 177:    */       public void valueChanged(ListSelectionEvent e)
/* 178:    */       {
/* 179:230 */         if (!e.getValueIsAdjusting())
/* 180:    */         {
/* 181:231 */           if (!SubstringReplacerStepEditorDialog.this.m_deleteBut.isEnabled()) {
/* 182:232 */             SubstringReplacerStepEditorDialog.this.m_deleteBut.setEnabled(true);
/* 183:    */           }
/* 184:234 */           SubstringReplacerStepEditorDialog.this.checkUpDown();
/* 185:    */           
/* 186:236 */           Object entry = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 187:237 */           if (entry != null)
/* 188:    */           {
/* 189:238 */             SubstringReplacerRules.SubstringReplacerMatchRule mr = (SubstringReplacerRules.SubstringReplacerMatchRule)entry;
/* 190:    */             
/* 191:240 */             SubstringReplacerStepEditorDialog.this.m_attListField.setText(mr.getAttsToApplyTo());
/* 192:241 */             SubstringReplacerStepEditorDialog.this.m_matchField.setText(mr.getMatch());
/* 193:242 */             SubstringReplacerStepEditorDialog.this.m_replaceField.setText(mr.getReplace());
/* 194:243 */             SubstringReplacerStepEditorDialog.this.m_regexCheck.setSelected(mr.getRegex());
/* 195:244 */             SubstringReplacerStepEditorDialog.this.m_ignoreCaseCheck.setSelected(mr.getIgnoreCase());
/* 196:    */           }
/* 197:    */         }
/* 198:    */       }
/* 199:249 */     });
/* 200:250 */     this.m_newBut.addActionListener(new ActionListener()
/* 201:    */     {
/* 202:    */       public void actionPerformed(ActionEvent e)
/* 203:    */       {
/* 204:253 */         SubstringReplacerRules.SubstringReplacerMatchRule mr = new SubstringReplacerRules.SubstringReplacerMatchRule();
/* 205:    */         
/* 206:    */ 
/* 207:256 */         String atts = SubstringReplacerStepEditorDialog.this.m_attListField.getText() != null ? SubstringReplacerStepEditorDialog.this.m_attListField.getText() : "";
/* 208:    */         
/* 209:258 */         mr.setAttsToApplyTo(atts);
/* 210:259 */         String match = SubstringReplacerStepEditorDialog.this.m_matchField.getText() != null ? SubstringReplacerStepEditorDialog.this.m_matchField.getText() : "";
/* 211:    */         
/* 212:261 */         mr.setMatch(match);
/* 213:262 */         String replace = SubstringReplacerStepEditorDialog.this.m_replaceField.getText() != null ? SubstringReplacerStepEditorDialog.this.m_replaceField.getText() : "";
/* 214:    */         
/* 215:264 */         mr.setReplace(replace);
/* 216:265 */         mr.setRegex(SubstringReplacerStepEditorDialog.this.m_regexCheck.isSelected());
/* 217:266 */         mr.setIgnoreCase(SubstringReplacerStepEditorDialog.this.m_ignoreCaseCheck.isSelected());
/* 218:    */         
/* 219:268 */         SubstringReplacerStepEditorDialog.this.m_listModel.addElement(mr);
/* 220:270 */         if (SubstringReplacerStepEditorDialog.this.m_listModel.size() > 1)
/* 221:    */         {
/* 222:271 */           SubstringReplacerStepEditorDialog.this.m_upBut.setEnabled(true);
/* 223:272 */           SubstringReplacerStepEditorDialog.this.m_downBut.setEnabled(true);
/* 224:    */         }
/* 225:275 */         SubstringReplacerStepEditorDialog.this.m_list.setSelectedIndex(SubstringReplacerStepEditorDialog.this.m_listModel.size() - 1);
/* 226:276 */         SubstringReplacerStepEditorDialog.this.checkUpDown();
/* 227:    */       }
/* 228:279 */     });
/* 229:280 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 230:    */     {
/* 231:    */       public void actionPerformed(ActionEvent e)
/* 232:    */       {
/* 233:283 */         int selected = SubstringReplacerStepEditorDialog.this.m_list.getSelectedIndex();
/* 234:284 */         if (selected >= 0)
/* 235:    */         {
/* 236:285 */           SubstringReplacerStepEditorDialog.this.m_listModel.removeElementAt(selected);
/* 237:286 */           SubstringReplacerStepEditorDialog.this.checkUpDown();
/* 238:288 */           if (SubstringReplacerStepEditorDialog.this.m_listModel.size() <= 1)
/* 239:    */           {
/* 240:289 */             SubstringReplacerStepEditorDialog.this.m_upBut.setEnabled(false);
/* 241:290 */             SubstringReplacerStepEditorDialog.this.m_downBut.setEnabled(false);
/* 242:    */           }
/* 243:    */         }
/* 244:    */       }
/* 245:295 */     });
/* 246:296 */     this.m_upBut.addActionListener(new ActionListener()
/* 247:    */     {
/* 248:    */       public void actionPerformed(ActionEvent e)
/* 249:    */       {
/* 250:299 */         JListHelper.moveUp(SubstringReplacerStepEditorDialog.this.m_list);
/* 251:300 */         SubstringReplacerStepEditorDialog.this.checkUpDown();
/* 252:    */       }
/* 253:303 */     });
/* 254:304 */     this.m_downBut.addActionListener(new ActionListener()
/* 255:    */     {
/* 256:    */       public void actionPerformed(ActionEvent e)
/* 257:    */       {
/* 258:307 */         JListHelper.moveDown(SubstringReplacerStepEditorDialog.this.m_list);
/* 259:308 */         SubstringReplacerStepEditorDialog.this.checkUpDown();
/* 260:    */       }
/* 261:311 */     });
/* 262:312 */     this.m_regexCheck.addActionListener(new ActionListener()
/* 263:    */     {
/* 264:    */       public void actionPerformed(ActionEvent e)
/* 265:    */       {
/* 266:315 */         Object mr = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 267:316 */         if (mr != null)
/* 268:    */         {
/* 269:317 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setRegex(SubstringReplacerStepEditorDialog.this.m_regexCheck.isSelected());
/* 270:    */           
/* 271:319 */           SubstringReplacerStepEditorDialog.this.m_list.repaint();
/* 272:    */         }
/* 273:    */       }
/* 274:323 */     });
/* 275:324 */     this.m_ignoreCaseCheck.addActionListener(new ActionListener()
/* 276:    */     {
/* 277:    */       public void actionPerformed(ActionEvent e)
/* 278:    */       {
/* 279:327 */         Object mr = SubstringReplacerStepEditorDialog.this.m_list.getSelectedValue();
/* 280:328 */         if (mr != null)
/* 281:    */         {
/* 282:329 */           ((SubstringReplacerRules.SubstringReplacerMatchRule)mr).setIgnoreCase(SubstringReplacerStepEditorDialog.this.m_ignoreCaseCheck.isSelected());
/* 283:    */           
/* 284:331 */           SubstringReplacerStepEditorDialog.this.m_list.repaint();
/* 285:    */         }
/* 286:    */       }
/* 287:    */     });
/* 288:    */   }
/* 289:    */   
/* 290:    */   protected void checkUpDown()
/* 291:    */   {
/* 292:342 */     if ((this.m_list.getSelectedValue() != null) && (this.m_listModel.size() > 1))
/* 293:    */     {
/* 294:343 */       this.m_upBut.setEnabled(this.m_list.getSelectedIndex() > 0);
/* 295:344 */       this.m_downBut.setEnabled(this.m_list.getSelectedIndex() < this.m_listModel.size() - 1);
/* 296:    */     }
/* 297:    */   }
/* 298:    */   
/* 299:    */   protected void okPressed()
/* 300:    */   {
/* 301:353 */     StringBuilder buff = new StringBuilder();
/* 302:354 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 303:    */     {
/* 304:355 */       SubstringReplacerRules.SubstringReplacerMatchRule mr = (SubstringReplacerRules.SubstringReplacerMatchRule)this.m_listModel.elementAt(i);
/* 305:    */       
/* 306:    */ 
/* 307:    */ 
/* 308:359 */       buff.append(mr.toStringInternal());
/* 309:360 */       if (i < this.m_listModel.size() - 1) {
/* 310:361 */         buff.append("@@match-replace@@");
/* 311:    */       }
/* 312:    */     }
/* 313:365 */     ((SubstringReplacer)getStepToEdit()).setMatchReplaceDetails(buff.toString());
/* 314:    */   }
/* 315:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.SubstringReplacerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */