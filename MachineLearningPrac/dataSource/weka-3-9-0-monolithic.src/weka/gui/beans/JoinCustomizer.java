/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
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
/*  19:    */ import weka.core.Environment;
/*  20:    */ import weka.core.EnvironmentHandler;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.gui.JListHelper;
/*  23:    */ import weka.gui.PropertySheetPanel;
/*  24:    */ 
/*  25:    */ public class JoinCustomizer
/*  26:    */   extends JPanel
/*  27:    */   implements EnvironmentHandler, BeanCustomizer, CustomizerCloseRequester
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 5797383368777382010L;
/*  30: 59 */   protected Environment m_env = Environment.getSystemWide();
/*  31: 60 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  32:    */   protected Join m_join;
/*  33: 64 */   protected JComboBox m_firstKeyFields = new EnvironmentField.WideComboBox();
/*  34: 65 */   protected JComboBox m_secondKeyFields = new EnvironmentField.WideComboBox();
/*  35: 67 */   protected JList m_firstList = new JList();
/*  36: 68 */   protected JList m_secondList = new JList();
/*  37:    */   protected DefaultListModel m_firstListModel;
/*  38:    */   protected DefaultListModel m_secondListModel;
/*  39: 72 */   protected JButton m_addOneBut = new JButton("Add");
/*  40: 73 */   protected JButton m_deleteOneBut = new JButton("Delete");
/*  41: 74 */   protected JButton m_upOneBut = new JButton("Up");
/*  42: 75 */   protected JButton m_downOneBut = new JButton("Down");
/*  43: 77 */   protected JButton m_addTwoBut = new JButton("Add");
/*  44: 78 */   protected JButton m_deleteTwoBut = new JButton("Delete");
/*  45: 79 */   protected JButton m_upTwoBut = new JButton("Up");
/*  46: 80 */   protected JButton m_downTwoBut = new JButton("Down");
/*  47:    */   protected Window m_parent;
/*  48: 84 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  49:    */   
/*  50:    */   public JoinCustomizer()
/*  51:    */   {
/*  52: 87 */     setLayout(new BorderLayout());
/*  53:    */   }
/*  54:    */   
/*  55:    */   private void setup()
/*  56:    */   {
/*  57: 92 */     JPanel aboutAndControlHolder = new JPanel();
/*  58: 93 */     aboutAndControlHolder.setLayout(new BorderLayout());
/*  59:    */     
/*  60: 95 */     JPanel controlHolder = new JPanel();
/*  61: 96 */     controlHolder.setLayout(new BorderLayout());
/*  62:    */     
/*  63:    */ 
/*  64: 99 */     JPanel firstSourceP = new JPanel();
/*  65:100 */     firstSourceP.setLayout(new BorderLayout());
/*  66:101 */     firstSourceP.add(new JLabel("First input ", 4), "Center");
/*  67:    */     
/*  68:103 */     Object firstInput = this.m_join.getFirstInput();
/*  69:104 */     String firstName = "<not connected>";
/*  70:105 */     if ((firstInput != null) && ((firstInput instanceof BeanCommon))) {
/*  71:106 */       firstName = ((BeanCommon)firstInput).getCustomName();
/*  72:    */     }
/*  73:108 */     firstSourceP.add(new JLabel(firstName, 2), "East");
/*  74:    */     
/*  75:    */ 
/*  76:111 */     JPanel secondSourceP = new JPanel();
/*  77:112 */     secondSourceP.setLayout(new BorderLayout());
/*  78:113 */     secondSourceP.add(new JLabel("Second input ", 4), "Center");
/*  79:    */     
/*  80:115 */     Object secondInput = this.m_join.getSecondInput();
/*  81:116 */     String secondName = "<not connected>";
/*  82:117 */     if ((secondInput != null) && ((secondInput instanceof BeanCommon))) {
/*  83:118 */       secondName = ((BeanCommon)secondInput).getCustomName();
/*  84:    */     }
/*  85:120 */     secondSourceP.add(new JLabel(secondName, 2), "East");
/*  86:    */     
/*  87:    */ 
/*  88:123 */     JPanel sourcePHolder = new JPanel();
/*  89:124 */     sourcePHolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
/*  90:125 */     sourcePHolder.setLayout(new BorderLayout());
/*  91:126 */     sourcePHolder.add(firstSourceP, "North");
/*  92:127 */     sourcePHolder.add(secondSourceP, "South");
/*  93:128 */     controlHolder.add(sourcePHolder, "North");
/*  94:    */     
/*  95:    */ 
/*  96:131 */     this.m_firstList.setVisibleRowCount(5);
/*  97:132 */     this.m_secondList.setVisibleRowCount(5);
/*  98:    */     
/*  99:134 */     this.m_firstKeyFields.setEditable(true);
/* 100:135 */     JPanel listOneP = new JPanel();
/* 101:136 */     this.m_deleteOneBut.setEnabled(false);
/* 102:137 */     listOneP.setLayout(new BorderLayout());
/* 103:138 */     JPanel butOneHolder = new JPanel();
/* 104:139 */     butOneHolder.setLayout(new GridLayout(1, 0));
/* 105:140 */     butOneHolder.add(this.m_addOneBut);
/* 106:141 */     butOneHolder.add(this.m_deleteOneBut);
/* 107:142 */     butOneHolder.add(this.m_upOneBut);
/* 108:143 */     butOneHolder.add(this.m_downOneBut);
/* 109:144 */     this.m_upOneBut.setEnabled(false);
/* 110:145 */     this.m_downOneBut.setEnabled(false);
/* 111:    */     
/* 112:147 */     JPanel fieldsAndButsOne = new JPanel();
/* 113:148 */     fieldsAndButsOne.setLayout(new BorderLayout());
/* 114:149 */     fieldsAndButsOne.add(this.m_firstKeyFields, "North");
/* 115:150 */     fieldsAndButsOne.add(butOneHolder, "South");
/* 116:151 */     listOneP.add(fieldsAndButsOne, "North");
/* 117:152 */     JScrollPane js1 = new JScrollPane(this.m_firstList);
/* 118:    */     
/* 119:154 */     js1.setBorder(BorderFactory.createTitledBorder("First input key fields"));
/* 120:155 */     listOneP.add(js1, "Center");
/* 121:    */     
/* 122:157 */     controlHolder.add(listOneP, "West");
/* 123:    */     
/* 124:159 */     this.m_secondKeyFields.setEditable(true);
/* 125:160 */     JPanel listTwoP = new JPanel();
/* 126:161 */     this.m_deleteTwoBut.setEnabled(false);
/* 127:162 */     listTwoP.setLayout(new BorderLayout());
/* 128:163 */     JPanel butTwoHolder = new JPanel();
/* 129:164 */     butTwoHolder.setLayout(new GridLayout(1, 0));
/* 130:165 */     butTwoHolder.add(this.m_addTwoBut);
/* 131:166 */     butTwoHolder.add(this.m_deleteTwoBut);
/* 132:167 */     butTwoHolder.add(this.m_upTwoBut);
/* 133:168 */     butTwoHolder.add(this.m_downTwoBut);
/* 134:169 */     this.m_upTwoBut.setEnabled(false);
/* 135:170 */     this.m_downTwoBut.setEnabled(false);
/* 136:    */     
/* 137:172 */     JPanel fieldsAndButsTwo = new JPanel();
/* 138:173 */     fieldsAndButsTwo.setLayout(new BorderLayout());
/* 139:174 */     fieldsAndButsTwo.add(this.m_secondKeyFields, "North");
/* 140:175 */     fieldsAndButsTwo.add(butTwoHolder, "South");
/* 141:    */     
/* 142:177 */     listTwoP.add(fieldsAndButsTwo, "North");
/* 143:178 */     JScrollPane js2 = new JScrollPane(this.m_secondList);
/* 144:    */     
/* 145:180 */     js2.setBorder(BorderFactory.createTitledBorder("Second input key fields"));
/* 146:181 */     listTwoP.add(js2, "Center");
/* 147:    */     
/* 148:183 */     controlHolder.add(listTwoP, "East");
/* 149:    */     
/* 150:185 */     aboutAndControlHolder.add(controlHolder, "South");
/* 151:186 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 152:187 */     aboutAndControlHolder.add(aboutP, "North");
/* 153:188 */     add(aboutAndControlHolder, "North");
/* 154:191 */     if (this.m_join.getFirstInputStructure() != null)
/* 155:    */     {
/* 156:192 */       this.m_firstKeyFields.removeAllItems();
/* 157:193 */       Instances incoming = this.m_join.getFirstInputStructure();
/* 158:194 */       for (int i = 0; i < incoming.numAttributes(); i++) {
/* 159:195 */         this.m_firstKeyFields.addItem(incoming.attribute(i).name());
/* 160:    */       }
/* 161:    */     }
/* 162:199 */     if (this.m_join.getSecondInputStructure() != null)
/* 163:    */     {
/* 164:200 */       this.m_secondKeyFields.removeAllItems();
/* 165:201 */       Instances incoming = this.m_join.getSecondInputStructure();
/* 166:202 */       for (int i = 0; i < incoming.numAttributes(); i++) {
/* 167:203 */         this.m_secondKeyFields.addItem(incoming.attribute(i).name());
/* 168:    */       }
/* 169:    */     }
/* 170:207 */     this.m_firstList.addListSelectionListener(new ListSelectionListener()
/* 171:    */     {
/* 172:    */       public void valueChanged(ListSelectionEvent e)
/* 173:    */       {
/* 174:211 */         if ((!e.getValueIsAdjusting()) && 
/* 175:212 */           (!JoinCustomizer.this.m_deleteOneBut.isEnabled())) {
/* 176:213 */           JoinCustomizer.this.m_deleteOneBut.setEnabled(true);
/* 177:    */         }
/* 178:    */       }
/* 179:218 */     });
/* 180:219 */     this.m_secondList.addListSelectionListener(new ListSelectionListener()
/* 181:    */     {
/* 182:    */       public void valueChanged(ListSelectionEvent e)
/* 183:    */       {
/* 184:223 */         if ((!e.getValueIsAdjusting()) && 
/* 185:224 */           (!JoinCustomizer.this.m_deleteTwoBut.isEnabled())) {
/* 186:225 */           JoinCustomizer.this.m_deleteTwoBut.setEnabled(true);
/* 187:    */         }
/* 188:    */       }
/* 189:230 */     });
/* 190:231 */     this.m_addOneBut.addActionListener(new ActionListener()
/* 191:    */     {
/* 192:    */       public void actionPerformed(ActionEvent e)
/* 193:    */       {
/* 194:234 */         if ((JoinCustomizer.this.m_firstKeyFields.getSelectedItem() != null) && (JoinCustomizer.this.m_firstKeyFields.getSelectedItem().toString().length() > 0))
/* 195:    */         {
/* 196:236 */           JoinCustomizer.this.m_firstListModel.addElement(JoinCustomizer.this.m_firstKeyFields.getSelectedItem());
/* 197:238 */           if (JoinCustomizer.this.m_firstListModel.size() > 1)
/* 198:    */           {
/* 199:239 */             JoinCustomizer.this.m_upOneBut.setEnabled(true);
/* 200:240 */             JoinCustomizer.this.m_downOneBut.setEnabled(true);
/* 201:    */           }
/* 202:    */         }
/* 203:    */       }
/* 204:245 */     });
/* 205:246 */     this.m_addTwoBut.addActionListener(new ActionListener()
/* 206:    */     {
/* 207:    */       public void actionPerformed(ActionEvent e)
/* 208:    */       {
/* 209:249 */         if ((JoinCustomizer.this.m_secondKeyFields.getSelectedItem() != null) && (JoinCustomizer.this.m_secondKeyFields.getSelectedItem().toString().length() > 0))
/* 210:    */         {
/* 211:252 */           JoinCustomizer.this.m_secondListModel.addElement(JoinCustomizer.this.m_secondKeyFields.getSelectedItem());
/* 212:253 */           if (JoinCustomizer.this.m_secondListModel.size() > 1)
/* 213:    */           {
/* 214:254 */             JoinCustomizer.this.m_upTwoBut.setEnabled(true);
/* 215:255 */             JoinCustomizer.this.m_downTwoBut.setEnabled(true);
/* 216:    */           }
/* 217:    */         }
/* 218:    */       }
/* 219:260 */     });
/* 220:261 */     this.m_deleteOneBut.addActionListener(new ActionListener()
/* 221:    */     {
/* 222:    */       public void actionPerformed(ActionEvent e)
/* 223:    */       {
/* 224:264 */         int selected = JoinCustomizer.this.m_firstList.getSelectedIndex();
/* 225:265 */         if (selected >= 0) {
/* 226:266 */           JoinCustomizer.this.m_firstListModel.remove(selected);
/* 227:    */         }
/* 228:269 */         if (JoinCustomizer.this.m_firstListModel.size() <= 1)
/* 229:    */         {
/* 230:270 */           JoinCustomizer.this.m_upOneBut.setEnabled(false);
/* 231:271 */           JoinCustomizer.this.m_downOneBut.setEnabled(false);
/* 232:    */         }
/* 233:    */       }
/* 234:275 */     });
/* 235:276 */     this.m_deleteTwoBut.addActionListener(new ActionListener()
/* 236:    */     {
/* 237:    */       public void actionPerformed(ActionEvent e)
/* 238:    */       {
/* 239:279 */         int selected = JoinCustomizer.this.m_secondList.getSelectedIndex();
/* 240:280 */         if (selected >= 0) {
/* 241:281 */           JoinCustomizer.this.m_secondListModel.remove(selected);
/* 242:    */         }
/* 243:284 */         if (JoinCustomizer.this.m_secondListModel.size() <= 1)
/* 244:    */         {
/* 245:285 */           JoinCustomizer.this.m_upTwoBut.setEnabled(false);
/* 246:286 */           JoinCustomizer.this.m_downTwoBut.setEnabled(false);
/* 247:    */         }
/* 248:    */       }
/* 249:290 */     });
/* 250:291 */     this.m_upOneBut.addActionListener(new ActionListener()
/* 251:    */     {
/* 252:    */       public void actionPerformed(ActionEvent e)
/* 253:    */       {
/* 254:294 */         JListHelper.moveUp(JoinCustomizer.this.m_firstList);
/* 255:    */       }
/* 256:298 */     });
/* 257:299 */     this.m_upTwoBut.addActionListener(new ActionListener()
/* 258:    */     {
/* 259:    */       public void actionPerformed(ActionEvent e)
/* 260:    */       {
/* 261:302 */         JListHelper.moveUp(JoinCustomizer.this.m_secondList);
/* 262:    */       }
/* 263:305 */     });
/* 264:306 */     this.m_downOneBut.addActionListener(new ActionListener()
/* 265:    */     {
/* 266:    */       public void actionPerformed(ActionEvent e)
/* 267:    */       {
/* 268:309 */         JListHelper.moveDown(JoinCustomizer.this.m_firstList);
/* 269:    */       }
/* 270:312 */     });
/* 271:313 */     this.m_downTwoBut.addActionListener(new ActionListener()
/* 272:    */     {
/* 273:    */       public void actionPerformed(ActionEvent e)
/* 274:    */       {
/* 275:316 */         JListHelper.moveDown(JoinCustomizer.this.m_secondList);
/* 276:    */       }
/* 277:320 */     });
/* 278:321 */     addButtons();
/* 279:    */   }
/* 280:    */   
/* 281:    */   private void addButtons()
/* 282:    */   {
/* 283:325 */     JButton okBut = new JButton("OK");
/* 284:326 */     JButton cancelBut = new JButton("Cancel");
/* 285:    */     
/* 286:328 */     JPanel butHolder = new JPanel();
/* 287:329 */     butHolder.setLayout(new GridLayout(1, 2));
/* 288:330 */     butHolder.add(okBut);
/* 289:331 */     butHolder.add(cancelBut);
/* 290:332 */     add(butHolder, "South");
/* 291:    */     
/* 292:334 */     okBut.addActionListener(new ActionListener()
/* 293:    */     {
/* 294:    */       public void actionPerformed(ActionEvent e)
/* 295:    */       {
/* 296:337 */         JoinCustomizer.this.closingOK();
/* 297:    */         
/* 298:339 */         JoinCustomizer.this.m_parent.dispose();
/* 299:    */       }
/* 300:342 */     });
/* 301:343 */     cancelBut.addActionListener(new ActionListener()
/* 302:    */     {
/* 303:    */       public void actionPerformed(ActionEvent e)
/* 304:    */       {
/* 305:346 */         JoinCustomizer.this.closingCancel();
/* 306:    */         
/* 307:348 */         JoinCustomizer.this.m_parent.dispose();
/* 308:    */       }
/* 309:    */     });
/* 310:    */   }
/* 311:    */   
/* 312:    */   protected void initialize()
/* 313:    */   {
/* 314:355 */     this.m_firstListModel = new DefaultListModel();
/* 315:356 */     this.m_secondListModel = new DefaultListModel();
/* 316:357 */     this.m_firstList.setModel(this.m_firstListModel);
/* 317:358 */     this.m_secondList.setModel(this.m_secondListModel);
/* 318:    */     
/* 319:360 */     String keySpec = this.m_join.getKeySpec();
/* 320:361 */     if ((keySpec != null) && (keySpec.length() > 0))
/* 321:    */     {
/* 322:    */       try
/* 323:    */       {
/* 324:363 */         keySpec = this.m_env.substitute(keySpec);
/* 325:    */       }
/* 326:    */       catch (Exception ex) {}
/* 327:367 */       String[] parts = keySpec.split("@@KS@@");
/* 328:368 */       if (parts.length > 0)
/* 329:    */       {
/* 330:369 */         String[] firstParts = parts[0].trim().split(",");
/* 331:370 */         for (String s : firstParts) {
/* 332:371 */           this.m_firstListModel.addElement(s);
/* 333:    */         }
/* 334:    */       }
/* 335:375 */       if (parts.length > 1)
/* 336:    */       {
/* 337:376 */         String[] secondParts = parts[1].trim().split(",");
/* 338:377 */         for (String s : secondParts) {
/* 339:378 */           this.m_secondListModel.addElement(s);
/* 340:    */         }
/* 341:    */       }
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void setObject(Object bean)
/* 346:    */   {
/* 347:386 */     if ((bean instanceof Join))
/* 348:    */     {
/* 349:387 */       this.m_join = ((Join)bean);
/* 350:388 */       this.m_tempEditor.setTarget(bean);
/* 351:389 */       setup();
/* 352:390 */       initialize();
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public void setParentWindow(Window parent)
/* 357:    */   {
/* 358:396 */     this.m_parent = parent;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 362:    */   {
/* 363:401 */     this.m_modifyL = l;
/* 364:    */   }
/* 365:    */   
/* 366:    */   public void setEnvironment(Environment env)
/* 367:    */   {
/* 368:406 */     this.m_env = env;
/* 369:    */   }
/* 370:    */   
/* 371:    */   private void closingOK()
/* 372:    */   {
/* 373:410 */     StringBuilder b = new StringBuilder();
/* 374:412 */     for (int i = 0; i < this.m_firstListModel.size(); i++)
/* 375:    */     {
/* 376:413 */       if (i != 0) {
/* 377:414 */         b.append(",");
/* 378:    */       }
/* 379:416 */       b.append(this.m_firstListModel.get(i));
/* 380:    */     }
/* 381:418 */     b.append("@@KS@@");
/* 382:419 */     for (int i = 0; i < this.m_secondListModel.size(); i++)
/* 383:    */     {
/* 384:420 */       if (i != 0) {
/* 385:421 */         b.append(",");
/* 386:    */       }
/* 387:423 */       b.append(this.m_secondListModel.get(i));
/* 388:    */     }
/* 389:426 */     this.m_join.setKeySpec(b.toString());
/* 390:    */   }
/* 391:    */   
/* 392:    */   private void closingCancel() {}
/* 393:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.JoinCustomizer
 * JD-Core Version:    0.7.0.1
 */