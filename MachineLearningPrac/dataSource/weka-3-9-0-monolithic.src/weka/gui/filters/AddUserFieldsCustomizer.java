/*   1:    */ package weka.gui.filters;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.beans.PropertyChangeEvent;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.List;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.DefaultListModel;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JComboBox;
/*  15:    */ import javax.swing.JList;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JScrollPane;
/*  18:    */ import javax.swing.event.ListSelectionEvent;
/*  19:    */ import javax.swing.event.ListSelectionListener;
/*  20:    */ import weka.core.Environment;
/*  21:    */ import weka.core.EnvironmentHandler;
/*  22:    */ import weka.filters.unsupervised.attribute.AddUserFields;
/*  23:    */ import weka.filters.unsupervised.attribute.AddUserFields.AttributeSpec;
/*  24:    */ import weka.gui.JListHelper;
/*  25:    */ import weka.gui.beans.BeanCustomizer.ModifyListener;
/*  26:    */ import weka.gui.beans.EnvironmentField;
/*  27:    */ import weka.gui.beans.GOECustomizer;
/*  28:    */ 
/*  29:    */ public class AddUserFieldsCustomizer
/*  30:    */   extends JPanel
/*  31:    */   implements EnvironmentHandler, GOECustomizer
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = -3823417827142174931L;
/*  34: 61 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  35: 62 */   protected Environment m_env = Environment.getSystemWide();
/*  36: 65 */   protected AddUserFields m_filter = null;
/*  37:    */   protected EnvironmentField m_nameField;
/*  38:    */   protected JComboBox m_typeField;
/*  39:    */   protected EnvironmentField m_dateFormatField;
/*  40:    */   protected EnvironmentField m_valueField;
/*  41: 72 */   protected JList m_list = new JList();
/*  42:    */   protected DefaultListModel m_listModel;
/*  43: 75 */   protected JButton m_newBut = new JButton("New");
/*  44: 76 */   protected JButton m_deleteBut = new JButton("Delete");
/*  45: 77 */   protected JButton m_upBut = new JButton("Move up");
/*  46: 78 */   protected JButton m_downBut = new JButton("Move down");
/*  47: 80 */   protected boolean m_dontShowButs = false;
/*  48:    */   
/*  49:    */   public AddUserFieldsCustomizer()
/*  50:    */   {
/*  51: 86 */     setLayout(new BorderLayout());
/*  52:    */   }
/*  53:    */   
/*  54:    */   private void setup()
/*  55:    */   {
/*  56: 90 */     JPanel fieldHolder = new JPanel();
/*  57: 91 */     JPanel namePanel = new JPanel();
/*  58: 92 */     namePanel.setLayout(new BorderLayout());
/*  59: 93 */     namePanel.setBorder(BorderFactory.createTitledBorder("Attribute name"));
/*  60: 94 */     this.m_nameField = new EnvironmentField(this.m_env);
/*  61: 95 */     namePanel.add(this.m_nameField, "Center");
/*  62: 96 */     namePanel.setToolTipText("Name of the new attribute");
/*  63:    */     
/*  64: 98 */     JPanel typePanel = new JPanel();
/*  65: 99 */     typePanel.setLayout(new BorderLayout());
/*  66:100 */     typePanel.setBorder(BorderFactory.createTitledBorder("Attribute type"));
/*  67:101 */     this.m_typeField = new JComboBox();
/*  68:102 */     this.m_typeField.addItem("numeric");
/*  69:103 */     this.m_typeField.addItem("nominal");
/*  70:104 */     this.m_typeField.addItem("string");
/*  71:105 */     this.m_typeField.addItem("date");
/*  72:106 */     typePanel.add(this.m_typeField, "Center");
/*  73:107 */     this.m_typeField.setToolTipText("Attribute type");
/*  74:108 */     typePanel.setToolTipText("Attribute type");
/*  75:    */     
/*  76:110 */     JPanel formatPanel = new JPanel();
/*  77:111 */     formatPanel.setLayout(new BorderLayout());
/*  78:112 */     formatPanel.setBorder(BorderFactory.createTitledBorder("Date format"));
/*  79:113 */     this.m_dateFormatField = new EnvironmentField(this.m_env);
/*  80:114 */     formatPanel.add(this.m_dateFormatField, "Center");
/*  81:115 */     formatPanel.setToolTipText("Date format (date attributes only)");
/*  82:    */     
/*  83:117 */     JPanel valuePanel = new JPanel();
/*  84:118 */     valuePanel.setLayout(new BorderLayout());
/*  85:119 */     valuePanel.setBorder(BorderFactory.createTitledBorder("Attribute value"));
/*  86:120 */     this.m_valueField = new EnvironmentField(this.m_env);
/*  87:121 */     valuePanel.add(this.m_valueField, "Center");
/*  88:122 */     valuePanel.setToolTipText("<html>Constant value (number, string or date)<br>for field. Special value \"now\" can be<br>used for date attributes for the current<br>time stamp</html>");
/*  89:    */     
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:128 */     fieldHolder.add(namePanel);
/*  95:129 */     fieldHolder.add(typePanel);
/*  96:130 */     fieldHolder.add(formatPanel);
/*  97:131 */     fieldHolder.add(valuePanel);
/*  98:    */     
/*  99:133 */     add(fieldHolder, "North");
/* 100:    */     
/* 101:135 */     this.m_list.setVisibleRowCount(5);
/* 102:136 */     this.m_deleteBut.setEnabled(false);
/* 103:137 */     JPanel listPanel = new JPanel();
/* 104:138 */     listPanel.setLayout(new BorderLayout());
/* 105:139 */     JPanel butHolder = new JPanel();
/* 106:140 */     butHolder.setLayout(new GridLayout(1, 0));
/* 107:141 */     butHolder.add(this.m_newBut);
/* 108:142 */     butHolder.add(this.m_deleteBut);
/* 109:143 */     butHolder.add(this.m_upBut);
/* 110:144 */     butHolder.add(this.m_downBut);
/* 111:145 */     this.m_upBut.setEnabled(false);
/* 112:146 */     this.m_downBut.setEnabled(false);
/* 113:    */     
/* 114:148 */     listPanel.add(butHolder, "North");
/* 115:149 */     JScrollPane js = new JScrollPane(this.m_list);
/* 116:150 */     js.setBorder(BorderFactory.createTitledBorder("Attributes to add"));
/* 117:    */     
/* 118:152 */     listPanel.add(js, "Center");
/* 119:153 */     add(listPanel, "Center");
/* 120:    */     
/* 121:155 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 122:    */     {
/* 123:    */       public void valueChanged(ListSelectionEvent e)
/* 124:    */       {
/* 125:158 */         if (!e.getValueIsAdjusting())
/* 126:    */         {
/* 127:159 */           if (!AddUserFieldsCustomizer.this.m_deleteBut.isEnabled()) {
/* 128:160 */             AddUserFieldsCustomizer.this.m_deleteBut.setEnabled(true);
/* 129:    */           }
/* 130:163 */           Object entry = AddUserFieldsCustomizer.this.m_list.getSelectedValue();
/* 131:164 */           if (entry != null)
/* 132:    */           {
/* 133:165 */             AddUserFields.AttributeSpec m = (AddUserFields.AttributeSpec)entry;
/* 134:166 */             AddUserFieldsCustomizer.this.m_nameField.setText(m.getName());
/* 135:167 */             AddUserFieldsCustomizer.this.m_valueField.setText(m.getValue());
/* 136:168 */             String type = m.getType();
/* 137:169 */             String format = "";
/* 138:170 */             if ((type.startsWith("date")) && (type.indexOf(":") > 0))
/* 139:    */             {
/* 140:171 */               format = type.substring(type.indexOf(":") + 1, type.length());
/* 141:172 */               type = type.substring(0, type.indexOf(":"));
/* 142:    */             }
/* 143:175 */             AddUserFieldsCustomizer.this.m_typeField.setSelectedItem(type.trim());
/* 144:176 */             AddUserFieldsCustomizer.this.m_dateFormatField.setText(format);
/* 145:    */           }
/* 146:    */         }
/* 147:    */       }
/* 148:181 */     });
/* 149:182 */     this.m_nameField.addPropertyChangeListener(new PropertyChangeListener()
/* 150:    */     {
/* 151:    */       public void propertyChange(PropertyChangeEvent e)
/* 152:    */       {
/* 153:185 */         Object a = AddUserFieldsCustomizer.this.m_list.getSelectedValue();
/* 154:186 */         if (a != null)
/* 155:    */         {
/* 156:187 */           ((AddUserFields.AttributeSpec)a).setName(AddUserFieldsCustomizer.this.m_nameField.getText());
/* 157:188 */           AddUserFieldsCustomizer.this.m_list.repaint();
/* 158:    */         }
/* 159:    */       }
/* 160:192 */     });
/* 161:193 */     this.m_typeField.addActionListener(new ActionListener()
/* 162:    */     {
/* 163:    */       public void actionPerformed(ActionEvent e)
/* 164:    */       {
/* 165:196 */         Object a = AddUserFieldsCustomizer.this.m_list.getSelectedValue();
/* 166:197 */         if (a != null)
/* 167:    */         {
/* 168:198 */           String type = AddUserFieldsCustomizer.this.m_typeField.getSelectedItem().toString();
/* 169:199 */           if (type.startsWith("date"))
/* 170:    */           {
/* 171:201 */             String format = AddUserFieldsCustomizer.this.m_dateFormatField.getText();
/* 172:202 */             if ((format != null) && (format.length() > 0)) {
/* 173:203 */               type = type + ":" + format;
/* 174:    */             }
/* 175:    */           }
/* 176:206 */           ((AddUserFields.AttributeSpec)a).setType(type);
/* 177:207 */           AddUserFieldsCustomizer.this.m_list.repaint();
/* 178:    */         }
/* 179:    */       }
/* 180:211 */     });
/* 181:212 */     this.m_valueField.addPropertyChangeListener(new PropertyChangeListener()
/* 182:    */     {
/* 183:    */       public void propertyChange(PropertyChangeEvent e)
/* 184:    */       {
/* 185:215 */         Object a = AddUserFieldsCustomizer.this.m_list.getSelectedValue();
/* 186:216 */         if (a != null)
/* 187:    */         {
/* 188:217 */           ((AddUserFields.AttributeSpec)a).setValue(AddUserFieldsCustomizer.this.m_valueField.getText());
/* 189:218 */           AddUserFieldsCustomizer.this.m_list.repaint();
/* 190:    */         }
/* 191:    */       }
/* 192:222 */     });
/* 193:223 */     this.m_newBut.addActionListener(new ActionListener()
/* 194:    */     {
/* 195:    */       public void actionPerformed(ActionEvent e)
/* 196:    */       {
/* 197:226 */         AddUserFields.AttributeSpec a = new AddUserFields.AttributeSpec();
/* 198:227 */         String name = (AddUserFieldsCustomizer.this.m_nameField.getText() != null) && (AddUserFieldsCustomizer.this.m_nameField.getText().length() > 0) ? AddUserFieldsCustomizer.this.m_nameField.getText() : "newAtt";
/* 199:    */         
/* 200:229 */         a.setName(name);
/* 201:230 */         String type = AddUserFieldsCustomizer.this.m_typeField.getSelectedItem().toString();
/* 202:231 */         if ((type.startsWith("date")) && 
/* 203:232 */           (AddUserFieldsCustomizer.this.m_dateFormatField.getText() != null) && (AddUserFieldsCustomizer.this.m_dateFormatField.getText().length() > 0)) {
/* 204:234 */           type = type + ":" + AddUserFieldsCustomizer.this.m_dateFormatField.getText();
/* 205:    */         }
/* 206:237 */         a.setType(type);
/* 207:238 */         String value = AddUserFieldsCustomizer.this.m_valueField.getText() != null ? AddUserFieldsCustomizer.this.m_valueField.getText() : "";
/* 208:    */         
/* 209:240 */         a.setValue(value);
/* 210:241 */         AddUserFieldsCustomizer.this.m_listModel.addElement(a);
/* 211:243 */         if (AddUserFieldsCustomizer.this.m_listModel.size() > 1)
/* 212:    */         {
/* 213:244 */           AddUserFieldsCustomizer.this.m_upBut.setEnabled(true);
/* 214:245 */           AddUserFieldsCustomizer.this.m_downBut.setEnabled(true);
/* 215:    */         }
/* 216:248 */         if (AddUserFieldsCustomizer.this.m_listModel.size() > 0)
/* 217:    */         {
/* 218:249 */           AddUserFieldsCustomizer.this.m_nameField.setEnabled(true);
/* 219:250 */           AddUserFieldsCustomizer.this.m_typeField.setEnabled(true);
/* 220:251 */           AddUserFieldsCustomizer.this.m_dateFormatField.setEnabled(true);
/* 221:252 */           AddUserFieldsCustomizer.this.m_valueField.setEnabled(true);
/* 222:    */         }
/* 223:254 */         AddUserFieldsCustomizer.this.m_list.setSelectedIndex(AddUserFieldsCustomizer.this.m_listModel.size() - 1);
/* 224:    */       }
/* 225:257 */     });
/* 226:258 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 227:    */     {
/* 228:    */       public void actionPerformed(ActionEvent e)
/* 229:    */       {
/* 230:261 */         int selected = AddUserFieldsCustomizer.this.m_list.getSelectedIndex();
/* 231:262 */         if (selected >= 0)
/* 232:    */         {
/* 233:263 */           AddUserFieldsCustomizer.this.m_listModel.removeElementAt(selected);
/* 234:265 */           if (AddUserFieldsCustomizer.this.m_listModel.size() <= 1)
/* 235:    */           {
/* 236:266 */             AddUserFieldsCustomizer.this.m_upBut.setEnabled(false);
/* 237:267 */             AddUserFieldsCustomizer.this.m_downBut.setEnabled(false);
/* 238:    */           }
/* 239:270 */           if (AddUserFieldsCustomizer.this.m_listModel.size() == 0)
/* 240:    */           {
/* 241:271 */             AddUserFieldsCustomizer.this.m_nameField.setEnabled(false);
/* 242:272 */             AddUserFieldsCustomizer.this.m_typeField.setEnabled(false);
/* 243:273 */             AddUserFieldsCustomizer.this.m_dateFormatField.setEnabled(false);
/* 244:274 */             AddUserFieldsCustomizer.this.m_valueField.setEnabled(false);
/* 245:    */           }
/* 246:    */         }
/* 247:    */       }
/* 248:279 */     });
/* 249:280 */     this.m_upBut.addActionListener(new ActionListener()
/* 250:    */     {
/* 251:    */       public void actionPerformed(ActionEvent e)
/* 252:    */       {
/* 253:283 */         JListHelper.moveUp(AddUserFieldsCustomizer.this.m_list);
/* 254:    */       }
/* 255:286 */     });
/* 256:287 */     this.m_downBut.addActionListener(new ActionListener()
/* 257:    */     {
/* 258:    */       public void actionPerformed(ActionEvent e)
/* 259:    */       {
/* 260:290 */         JListHelper.moveDown(AddUserFieldsCustomizer.this.m_list);
/* 261:    */       }
/* 262:    */     });
/* 263:294 */     if (this.m_dontShowButs) {
/* 264:295 */       return;
/* 265:    */     }
/* 266:297 */     addButtons();
/* 267:    */   }
/* 268:    */   
/* 269:    */   private void addButtons()
/* 270:    */   {
/* 271:301 */     JButton okBut = new JButton("OK");
/* 272:302 */     JButton cancelBut = new JButton("Cancel");
/* 273:    */     
/* 274:304 */     JPanel butHolder = new JPanel();
/* 275:305 */     butHolder.setLayout(new GridLayout(1, 2));
/* 276:306 */     butHolder.add(okBut);
/* 277:307 */     butHolder.add(cancelBut);
/* 278:308 */     add(butHolder, "South");
/* 279:    */     
/* 280:310 */     okBut.addActionListener(new ActionListener()
/* 281:    */     {
/* 282:    */       public void actionPerformed(ActionEvent e)
/* 283:    */       {
/* 284:313 */         AddUserFieldsCustomizer.this.closingOK();
/* 285:    */       }
/* 286:316 */     });
/* 287:317 */     cancelBut.addActionListener(new ActionListener()
/* 288:    */     {
/* 289:    */       public void actionPerformed(ActionEvent e)
/* 290:    */       {
/* 291:320 */         AddUserFieldsCustomizer.this.closingCancel();
/* 292:    */       }
/* 293:    */     });
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void setEnvironment(Environment env)
/* 297:    */   {
/* 298:332 */     this.m_env = env;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 302:    */   {
/* 303:341 */     this.m_modifyL = l;
/* 304:    */   }
/* 305:    */   
/* 306:    */   protected void initialize()
/* 307:    */   {
/* 308:348 */     List<AddUserFields.AttributeSpec> specs = this.m_filter.getAttributeSpecs();
/* 309:349 */     this.m_listModel = new DefaultListModel();
/* 310:350 */     this.m_list.setModel(this.m_listModel);
/* 311:352 */     if (specs.size() > 0)
/* 312:    */     {
/* 313:353 */       this.m_upBut.setEnabled(true);
/* 314:354 */       this.m_downBut.setEnabled(true);
/* 315:355 */       for (AddUserFields.AttributeSpec s : specs)
/* 316:    */       {
/* 317:356 */         AddUserFields.AttributeSpec specCopy = new AddUserFields.AttributeSpec(s.toStringInternal());
/* 318:    */         
/* 319:358 */         this.m_listModel.addElement(specCopy);
/* 320:    */       }
/* 321:361 */       this.m_list.repaint();
/* 322:    */     }
/* 323:    */     else
/* 324:    */     {
/* 325:364 */       this.m_nameField.setEnabled(false);
/* 326:365 */       this.m_typeField.setEnabled(false);
/* 327:366 */       this.m_dateFormatField.setEnabled(false);
/* 328:367 */       this.m_valueField.setEnabled(false);
/* 329:    */     }
/* 330:    */   }
/* 331:    */   
/* 332:    */   public void setObject(Object o)
/* 333:    */   {
/* 334:378 */     if ((o instanceof AddUserFields))
/* 335:    */     {
/* 336:379 */       this.m_filter = ((AddUserFields)o);
/* 337:380 */       setup();
/* 338:381 */       initialize();
/* 339:    */     }
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void dontShowOKCancelButtons()
/* 343:    */   {
/* 344:390 */     this.m_dontShowButs = true;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public void closingOK()
/* 348:    */   {
/* 349:398 */     List<AddUserFields.AttributeSpec> specs = new ArrayList();
/* 350:399 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 351:    */     {
/* 352:400 */       AddUserFields.AttributeSpec a = (AddUserFields.AttributeSpec)this.m_listModel.elementAt(i);
/* 353:    */       
/* 354:402 */       specs.add(a);
/* 355:    */     }
/* 356:405 */     if (this.m_modifyL != null) {
/* 357:406 */       this.m_modifyL.setModifiedStatus(this, true);
/* 358:    */     }
/* 359:409 */     this.m_filter.setAttributeSpecs(specs);
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void closingCancel() {}
/* 363:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.filters.AddUserFieldsCustomizer
 * JD-Core Version:    0.7.0.1
 */