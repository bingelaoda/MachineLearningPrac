/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.BorderFactory;
/*   9:    */ import javax.swing.DefaultListModel;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JComboBox;
/*  12:    */ import javax.swing.JLabel;
/*  13:    */ import javax.swing.JList;
/*  14:    */ import javax.swing.JPanel;
/*  15:    */ import javax.swing.JScrollPane;
/*  16:    */ import javax.swing.event.ListSelectionEvent;
/*  17:    */ import javax.swing.event.ListSelectionListener;
/*  18:    */ import weka.core.Attribute;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.WekaException;
/*  21:    */ import weka.gui.EnvironmentField.WideComboBox;
/*  22:    */ import weka.gui.JListHelper;
/*  23:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  24:    */ import weka.knowledgeflow.steps.Join;
/*  25:    */ 
/*  26:    */ public class JoinStepEditorDialog
/*  27:    */   extends StepEditorDialog
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -2648770811063889717L;
/*  30: 50 */   protected JComboBox m_firstKeyFields = new EnvironmentField.WideComboBox();
/*  31: 53 */   protected JComboBox m_secondKeyFields = new EnvironmentField.WideComboBox();
/*  32: 56 */   protected JList m_firstList = new JList();
/*  33: 59 */   protected JList m_secondList = new JList();
/*  34:    */   protected DefaultListModel<String> m_firstListModel;
/*  35:    */   protected DefaultListModel<String> m_secondListModel;
/*  36: 64 */   protected JButton m_addOneBut = new JButton("Add");
/*  37: 67 */   protected JButton m_deleteOneBut = new JButton("Delete");
/*  38: 70 */   protected JButton m_upOneBut = new JButton("Up");
/*  39: 73 */   protected JButton m_downOneBut = new JButton("Down");
/*  40: 76 */   protected JButton m_addTwoBut = new JButton("Add");
/*  41: 79 */   protected JButton m_deleteTwoBut = new JButton("Delete");
/*  42: 82 */   protected JButton m_upTwoBut = new JButton("Up");
/*  43: 85 */   protected JButton m_downTwoBut = new JButton("Down");
/*  44:    */   
/*  45:    */   protected void initialize()
/*  46:    */   {
/*  47: 92 */     this.m_firstListModel = new DefaultListModel();
/*  48: 93 */     this.m_secondListModel = new DefaultListModel();
/*  49: 94 */     this.m_firstList.setModel(this.m_firstListModel);
/*  50: 95 */     this.m_secondList.setModel(this.m_secondListModel);
/*  51:    */     
/*  52: 97 */     String keySpec = ((Join)getStepToEdit()).getKeySpec();
/*  53: 98 */     if ((keySpec != null) && (keySpec.length() > 0))
/*  54:    */     {
/*  55:100 */       keySpec = environmentSubstitute(keySpec);
/*  56:101 */       String[] parts = keySpec.split("@@KS@@");
/*  57:102 */       if (parts.length > 0)
/*  58:    */       {
/*  59:103 */         String[] firstParts = parts[0].trim().split(",");
/*  60:104 */         for (String s : firstParts) {
/*  61:105 */           this.m_firstListModel.addElement(s);
/*  62:    */         }
/*  63:    */       }
/*  64:109 */       if (parts.length > 1)
/*  65:    */       {
/*  66:110 */         String[] secondParts = parts[1].trim().split(",");
/*  67:111 */         for (String s : secondParts) {
/*  68:112 */           this.m_secondListModel.addElement(s);
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   protected void layoutEditor()
/*  75:    */   {
/*  76:124 */     initialize();
/*  77:    */     
/*  78:126 */     JPanel controlHolder = new JPanel();
/*  79:127 */     controlHolder.setLayout(new BorderLayout());
/*  80:    */     
/*  81:    */ 
/*  82:130 */     List<String> connected = ((Join)getStepToEdit()).getConnectedInputNames();
/*  83:131 */     String firstName = connected.get(0) == null ? "<not connected>" : (String)connected.get(0);
/*  84:    */     
/*  85:133 */     String secondName = connected.get(1) == null ? "<not connected>" : (String)connected.get(1);
/*  86:    */     
/*  87:    */ 
/*  88:136 */     JPanel firstSourceP = new JPanel();
/*  89:137 */     firstSourceP.setLayout(new BorderLayout());
/*  90:138 */     firstSourceP.add(new JLabel("First input ", 4), "Center");
/*  91:    */     
/*  92:140 */     firstSourceP.add(new JLabel(firstName, 2), "East");
/*  93:    */     
/*  94:    */ 
/*  95:143 */     JPanel secondSourceP = new JPanel();
/*  96:144 */     secondSourceP.setLayout(new BorderLayout());
/*  97:145 */     secondSourceP.add(new JLabel("Second input ", 4), "Center");
/*  98:    */     
/*  99:147 */     secondSourceP.add(new JLabel(secondName, 2), "East");
/* 100:    */     
/* 101:    */ 
/* 102:150 */     JPanel sourcePHolder = new JPanel();
/* 103:151 */     sourcePHolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/* 104:152 */     sourcePHolder.setLayout(new BorderLayout());
/* 105:153 */     sourcePHolder.add(firstSourceP, "North");
/* 106:154 */     sourcePHolder.add(secondSourceP, "South");
/* 107:155 */     controlHolder.add(sourcePHolder, "North");
/* 108:    */     
/* 109:157 */     this.m_firstList.setVisibleRowCount(5);
/* 110:158 */     this.m_secondList.setVisibleRowCount(5);
/* 111:    */     
/* 112:160 */     this.m_firstKeyFields.setEditable(true);
/* 113:161 */     JPanel listOneP = new JPanel();
/* 114:162 */     this.m_deleteOneBut.setEnabled(false);
/* 115:163 */     listOneP.setLayout(new BorderLayout());
/* 116:164 */     JPanel butOneHolder = new JPanel();
/* 117:165 */     butOneHolder.setLayout(new GridLayout(1, 0));
/* 118:166 */     butOneHolder.add(this.m_addOneBut);
/* 119:167 */     butOneHolder.add(this.m_deleteOneBut);
/* 120:168 */     butOneHolder.add(this.m_upOneBut);
/* 121:169 */     butOneHolder.add(this.m_downOneBut);
/* 122:170 */     this.m_upOneBut.setEnabled(false);
/* 123:171 */     this.m_downOneBut.setEnabled(false);
/* 124:    */     
/* 125:173 */     JPanel fieldsAndButsOne = new JPanel();
/* 126:174 */     fieldsAndButsOne.setLayout(new BorderLayout());
/* 127:175 */     fieldsAndButsOne.add(this.m_firstKeyFields, "North");
/* 128:176 */     fieldsAndButsOne.add(butOneHolder, "South");
/* 129:177 */     listOneP.add(fieldsAndButsOne, "North");
/* 130:178 */     JScrollPane js1 = new JScrollPane(this.m_firstList);
/* 131:    */     
/* 132:180 */     js1.setBorder(BorderFactory.createTitledBorder("First input key fields"));
/* 133:181 */     listOneP.add(js1, "Center");
/* 134:    */     
/* 135:183 */     controlHolder.add(listOneP, "West");
/* 136:    */     
/* 137:185 */     this.m_secondKeyFields.setEditable(true);
/* 138:186 */     JPanel listTwoP = new JPanel();
/* 139:187 */     this.m_deleteTwoBut.setEnabled(false);
/* 140:188 */     listTwoP.setLayout(new BorderLayout());
/* 141:189 */     JPanel butTwoHolder = new JPanel();
/* 142:190 */     butTwoHolder.setLayout(new GridLayout(1, 0));
/* 143:191 */     butTwoHolder.add(this.m_addTwoBut);
/* 144:192 */     butTwoHolder.add(this.m_deleteTwoBut);
/* 145:193 */     butTwoHolder.add(this.m_upTwoBut);
/* 146:194 */     butTwoHolder.add(this.m_downTwoBut);
/* 147:195 */     this.m_upTwoBut.setEnabled(false);
/* 148:196 */     this.m_downTwoBut.setEnabled(false);
/* 149:    */     
/* 150:198 */     JPanel fieldsAndButsTwo = new JPanel();
/* 151:199 */     fieldsAndButsTwo.setLayout(new BorderLayout());
/* 152:200 */     fieldsAndButsTwo.add(this.m_secondKeyFields, "North");
/* 153:201 */     fieldsAndButsTwo.add(butTwoHolder, "South");
/* 154:    */     
/* 155:203 */     listTwoP.add(fieldsAndButsTwo, "North");
/* 156:204 */     JScrollPane js2 = new JScrollPane(this.m_secondList);
/* 157:    */     
/* 158:206 */     js2.setBorder(BorderFactory.createTitledBorder("Second input key fields"));
/* 159:207 */     listTwoP.add(js2, "Center");
/* 160:    */     
/* 161:209 */     controlHolder.add(listTwoP, "East");
/* 162:210 */     add(controlHolder, "Center");
/* 163:    */     try
/* 164:    */     {
/* 165:214 */       if (((Join)getStepToEdit()).getFirstInputStructure() != null)
/* 166:    */       {
/* 167:215 */         this.m_firstKeyFields.removeAllItems();
/* 168:216 */         Instances incoming = ((Join)getStepToEdit()).getFirstInputStructure();
/* 169:217 */         for (int i = 0; i < incoming.numAttributes(); i++) {
/* 170:218 */           this.m_firstKeyFields.addItem(incoming.attribute(i).name());
/* 171:    */         }
/* 172:    */       }
/* 173:222 */       if (((Join)getStepToEdit()).getSecondInputStructure() != null)
/* 174:    */       {
/* 175:223 */         this.m_secondKeyFields.removeAllItems();
/* 176:224 */         Instances incoming = ((Join)getStepToEdit()).getSecondInputStructure();
/* 177:225 */         for (int i = 0; i < incoming.numAttributes(); i++) {
/* 178:226 */           this.m_secondKeyFields.addItem(incoming.attribute(i).name());
/* 179:    */         }
/* 180:    */       }
/* 181:    */     }
/* 182:    */     catch (WekaException ex)
/* 183:    */     {
/* 184:230 */       showErrorDialog(ex);
/* 185:    */     }
/* 186:233 */     this.m_firstList.addListSelectionListener(new ListSelectionListener()
/* 187:    */     {
/* 188:    */       public void valueChanged(ListSelectionEvent e)
/* 189:    */       {
/* 190:237 */         if ((!e.getValueIsAdjusting()) && 
/* 191:238 */           (!JoinStepEditorDialog.this.m_deleteOneBut.isEnabled())) {
/* 192:239 */           JoinStepEditorDialog.this.m_deleteOneBut.setEnabled(true);
/* 193:    */         }
/* 194:    */       }
/* 195:244 */     });
/* 196:245 */     this.m_secondList.addListSelectionListener(new ListSelectionListener()
/* 197:    */     {
/* 198:    */       public void valueChanged(ListSelectionEvent e)
/* 199:    */       {
/* 200:249 */         if ((!e.getValueIsAdjusting()) && 
/* 201:250 */           (!JoinStepEditorDialog.this.m_deleteTwoBut.isEnabled())) {
/* 202:251 */           JoinStepEditorDialog.this.m_deleteTwoBut.setEnabled(true);
/* 203:    */         }
/* 204:    */       }
/* 205:256 */     });
/* 206:257 */     this.m_addOneBut.addActionListener(new ActionListener()
/* 207:    */     {
/* 208:    */       public void actionPerformed(ActionEvent e)
/* 209:    */       {
/* 210:260 */         if ((JoinStepEditorDialog.this.m_firstKeyFields.getSelectedItem() != null) && (JoinStepEditorDialog.this.m_firstKeyFields.getSelectedItem().toString().length() > 0))
/* 211:    */         {
/* 212:262 */           JoinStepEditorDialog.this.m_firstListModel.addElement(JoinStepEditorDialog.this.m_firstKeyFields.getSelectedItem().toString());
/* 213:265 */           if (JoinStepEditorDialog.this.m_firstListModel.size() > 1)
/* 214:    */           {
/* 215:266 */             JoinStepEditorDialog.this.m_upOneBut.setEnabled(true);
/* 216:267 */             JoinStepEditorDialog.this.m_downOneBut.setEnabled(true);
/* 217:    */           }
/* 218:    */         }
/* 219:    */       }
/* 220:272 */     });
/* 221:273 */     this.m_addTwoBut.addActionListener(new ActionListener()
/* 222:    */     {
/* 223:    */       public void actionPerformed(ActionEvent e)
/* 224:    */       {
/* 225:276 */         if ((JoinStepEditorDialog.this.m_secondKeyFields.getSelectedItem() != null) && (JoinStepEditorDialog.this.m_secondKeyFields.getSelectedItem().toString().length() > 0))
/* 226:    */         {
/* 227:279 */           JoinStepEditorDialog.this.m_secondListModel.addElement(JoinStepEditorDialog.this.m_secondKeyFields.getSelectedItem().toString());
/* 228:281 */           if (JoinStepEditorDialog.this.m_secondListModel.size() > 1)
/* 229:    */           {
/* 230:282 */             JoinStepEditorDialog.this.m_upTwoBut.setEnabled(true);
/* 231:283 */             JoinStepEditorDialog.this.m_downTwoBut.setEnabled(true);
/* 232:    */           }
/* 233:    */         }
/* 234:    */       }
/* 235:288 */     });
/* 236:289 */     this.m_deleteOneBut.addActionListener(new ActionListener()
/* 237:    */     {
/* 238:    */       public void actionPerformed(ActionEvent e)
/* 239:    */       {
/* 240:292 */         int selected = JoinStepEditorDialog.this.m_firstList.getSelectedIndex();
/* 241:293 */         if (selected >= 0) {
/* 242:294 */           JoinStepEditorDialog.this.m_firstListModel.remove(selected);
/* 243:    */         }
/* 244:297 */         if (JoinStepEditorDialog.this.m_firstListModel.size() <= 1)
/* 245:    */         {
/* 246:298 */           JoinStepEditorDialog.this.m_upOneBut.setEnabled(false);
/* 247:299 */           JoinStepEditorDialog.this.m_downOneBut.setEnabled(false);
/* 248:    */         }
/* 249:    */       }
/* 250:303 */     });
/* 251:304 */     this.m_deleteTwoBut.addActionListener(new ActionListener()
/* 252:    */     {
/* 253:    */       public void actionPerformed(ActionEvent e)
/* 254:    */       {
/* 255:307 */         int selected = JoinStepEditorDialog.this.m_secondList.getSelectedIndex();
/* 256:308 */         if (selected >= 0) {
/* 257:309 */           JoinStepEditorDialog.this.m_secondListModel.remove(selected);
/* 258:    */         }
/* 259:312 */         if (JoinStepEditorDialog.this.m_secondListModel.size() <= 1)
/* 260:    */         {
/* 261:313 */           JoinStepEditorDialog.this.m_upTwoBut.setEnabled(false);
/* 262:314 */           JoinStepEditorDialog.this.m_downTwoBut.setEnabled(false);
/* 263:    */         }
/* 264:    */       }
/* 265:318 */     });
/* 266:319 */     this.m_upOneBut.addActionListener(new ActionListener()
/* 267:    */     {
/* 268:    */       public void actionPerformed(ActionEvent e)
/* 269:    */       {
/* 270:322 */         JListHelper.moveUp(JoinStepEditorDialog.this.m_firstList);
/* 271:    */       }
/* 272:326 */     });
/* 273:327 */     this.m_upTwoBut.addActionListener(new ActionListener()
/* 274:    */     {
/* 275:    */       public void actionPerformed(ActionEvent e)
/* 276:    */       {
/* 277:330 */         JListHelper.moveUp(JoinStepEditorDialog.this.m_secondList);
/* 278:    */       }
/* 279:333 */     });
/* 280:334 */     this.m_downOneBut.addActionListener(new ActionListener()
/* 281:    */     {
/* 282:    */       public void actionPerformed(ActionEvent e)
/* 283:    */       {
/* 284:337 */         JListHelper.moveDown(JoinStepEditorDialog.this.m_firstList);
/* 285:    */       }
/* 286:340 */     });
/* 287:341 */     this.m_downTwoBut.addActionListener(new ActionListener()
/* 288:    */     {
/* 289:    */       public void actionPerformed(ActionEvent e)
/* 290:    */       {
/* 291:344 */         JListHelper.moveDown(JoinStepEditorDialog.this.m_secondList);
/* 292:    */       }
/* 293:    */     });
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void okPressed()
/* 297:    */   {
/* 298:355 */     StringBuilder b = new StringBuilder();
/* 299:357 */     for (int i = 0; i < this.m_firstListModel.size(); i++)
/* 300:    */     {
/* 301:358 */       if (i != 0) {
/* 302:359 */         b.append(",");
/* 303:    */       }
/* 304:361 */       b.append((String)this.m_firstListModel.get(i));
/* 305:    */     }
/* 306:363 */     b.append("@@KS@@");
/* 307:364 */     for (int i = 0; i < this.m_secondListModel.size(); i++)
/* 308:    */     {
/* 309:365 */       if (i != 0) {
/* 310:366 */         b.append(",");
/* 311:    */       }
/* 312:368 */       b.append((String)this.m_secondListModel.get(i));
/* 313:    */     }
/* 314:371 */     ((Join)getStepToEdit()).setKeySpec(b.toString());
/* 315:    */   }
/* 316:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.JoinStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */