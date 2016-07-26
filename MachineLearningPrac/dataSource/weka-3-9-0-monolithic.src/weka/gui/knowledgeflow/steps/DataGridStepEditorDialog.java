/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeEvent;
/*   9:    */ import java.beans.PropertyChangeListener;
/*  10:    */ import java.io.StringReader;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.Collections;
/*  13:    */ import java.util.HashMap;
/*  14:    */ import java.util.List;
/*  15:    */ import java.util.Map;
/*  16:    */ import java.util.Map.Entry;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.DefaultListModel;
/*  19:    */ import javax.swing.JButton;
/*  20:    */ import javax.swing.JComboBox;
/*  21:    */ import javax.swing.JList;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JScrollPane;
/*  24:    */ import javax.swing.JTabbedPane;
/*  25:    */ import javax.swing.event.ChangeEvent;
/*  26:    */ import javax.swing.event.ChangeListener;
/*  27:    */ import javax.swing.event.ListSelectionEvent;
/*  28:    */ import javax.swing.event.ListSelectionListener;
/*  29:    */ import weka.core.Attribute;
/*  30:    */ import weka.core.DenseInstance;
/*  31:    */ import weka.core.Instance;
/*  32:    */ import weka.core.Instances;
/*  33:    */ import weka.core.Utils;
/*  34:    */ import weka.gui.EnvironmentField;
/*  35:    */ import weka.gui.JListHelper;
/*  36:    */ import weka.gui.arffviewer.ArffPanel;
/*  37:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  38:    */ import weka.knowledgeflow.steps.DataGrid;
/*  39:    */ 
/*  40:    */ public class DataGridStepEditorDialog
/*  41:    */   extends StepEditorDialog
/*  42:    */ {
/*  43:    */   private static final long serialVersionUID = -7314471130292016602L;
/*  44:    */   protected JTabbedPane m_tabbedPane;
/*  45:    */   protected ArffViewerPanel m_viewerPanel;
/*  46:    */   protected EnvironmentField m_attNameField;
/*  47:    */   protected JComboBox<String> m_attTypeField;
/*  48:    */   protected EnvironmentField m_nominalOrDateFormatField;
/*  49:    */   protected JList<AttDef> m_list;
/*  50:    */   protected DefaultListModel<AttDef> m_listModel;
/*  51:    */   protected JButton m_newBut;
/*  52:    */   protected JButton m_deleteBut;
/*  53:    */   protected JButton m_upBut;
/*  54:    */   protected JButton m_downBut;
/*  55:    */   protected String m_stringInstances;
/*  56:    */   
/*  57:    */   public DataGridStepEditorDialog()
/*  58:    */   {
/*  59: 72 */     this.m_tabbedPane = new JTabbedPane();
/*  60:    */     
/*  61:    */ 
/*  62: 75 */     this.m_viewerPanel = new ArffViewerPanel();
/*  63:    */     
/*  64:    */ 
/*  65: 78 */     this.m_attNameField = new EnvironmentField();
/*  66:    */     
/*  67:    */ 
/*  68: 81 */     this.m_attTypeField = new JComboBox();
/*  69:    */     
/*  70:    */ 
/*  71: 84 */     this.m_nominalOrDateFormatField = new EnvironmentField();
/*  72:    */     
/*  73:    */ 
/*  74:    */ 
/*  75: 88 */     this.m_list = new JList();
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81: 94 */     this.m_newBut = new JButton("New");
/*  82:    */     
/*  83:    */ 
/*  84: 97 */     this.m_deleteBut = new JButton("Delete");
/*  85:    */     
/*  86:    */ 
/*  87:100 */     this.m_upBut = new JButton("Move up");
/*  88:    */     
/*  89:    */ 
/*  90:103 */     this.m_downBut = new JButton("Move down");
/*  91:    */   }
/*  92:    */   
/*  93:    */   protected void initialize()
/*  94:    */   {
/*  95:112 */     this.m_stringInstances = ((DataGrid)getStepToEdit()).getData();
/*  96:113 */     this.m_listModel = new DefaultListModel();
/*  97:114 */     this.m_list.setModel(this.m_listModel);
/*  98:116 */     if ((this.m_stringInstances != null) && (this.m_stringInstances.length() > 0)) {
/*  99:    */       try
/* 100:    */       {
/* 101:118 */         Instances insts = new Instances(new StringReader(this.m_stringInstances));
/* 102:119 */         for (int i = 0; i < insts.numAttributes(); i++)
/* 103:    */         {
/* 104:120 */           Attribute a = insts.attribute(i);
/* 105:121 */           String nomOrDate = "";
/* 106:122 */           if (a.isNominal())
/* 107:    */           {
/* 108:123 */             for (int j = 0; j < a.numValues(); j++) {
/* 109:124 */               nomOrDate = nomOrDate + a.value(j) + ",";
/* 110:    */             }
/* 111:126 */             nomOrDate = nomOrDate.substring(0, nomOrDate.length() - 1);
/* 112:    */           }
/* 113:127 */           else if (a.isDate())
/* 114:    */           {
/* 115:128 */             nomOrDate = a.getDateFormat();
/* 116:    */           }
/* 117:130 */           AttDef def = new AttDef(a.name(), a.type(), nomOrDate);
/* 118:131 */           this.m_listModel.addElement(def);
/* 119:    */         }
/* 120:133 */         this.m_viewerPanel.setInstances(insts);
/* 121:    */       }
/* 122:    */       catch (Exception ex)
/* 123:    */       {
/* 124:135 */         showErrorDialog(ex);
/* 125:    */       }
/* 126:    */     }
/* 127:    */   }
/* 128:    */   
/* 129:    */   protected void layoutEditor()
/* 130:    */   {
/* 131:145 */     initialize();
/* 132:146 */     this.m_upBut.setEnabled(false);
/* 133:147 */     this.m_downBut.setEnabled(false);
/* 134:    */     
/* 135:149 */     JPanel mainHolder = new JPanel(new BorderLayout());
/* 136:    */     
/* 137:151 */     JPanel controlHolder = new JPanel();
/* 138:152 */     controlHolder.setLayout(new BorderLayout());
/* 139:153 */     JPanel fieldHolder = new JPanel();
/* 140:154 */     fieldHolder.setLayout(new GridLayout(1, 0));
/* 141:    */     
/* 142:156 */     JPanel attNameP = new JPanel(new BorderLayout());
/* 143:157 */     attNameP.setBorder(BorderFactory.createTitledBorder("Attribute name"));
/* 144:158 */     attNameP.add(this.m_attNameField, "Center");
/* 145:159 */     JPanel attTypeP = new JPanel(new BorderLayout());
/* 146:160 */     attTypeP.setBorder(BorderFactory.createTitledBorder("Attribute type"));
/* 147:161 */     attTypeP.add(this.m_attTypeField, "Center");
/* 148:162 */     this.m_attTypeField.addItem("numeric");
/* 149:163 */     this.m_attTypeField.addItem("nominal");
/* 150:164 */     this.m_attTypeField.addItem("date");
/* 151:165 */     this.m_attTypeField.addItem("string");
/* 152:166 */     JPanel nomDateP = new JPanel(new BorderLayout());
/* 153:167 */     nomDateP.setBorder(BorderFactory.createTitledBorder("Nominal vals/date format"));
/* 154:    */     
/* 155:169 */     nomDateP.add(this.m_nominalOrDateFormatField, "Center");
/* 156:170 */     fieldHolder.add(attNameP);
/* 157:171 */     fieldHolder.add(attTypeP);
/* 158:172 */     fieldHolder.add(nomDateP);
/* 159:    */     
/* 160:174 */     controlHolder.add(fieldHolder, "North");
/* 161:175 */     mainHolder.add(controlHolder, "North");
/* 162:    */     
/* 163:177 */     this.m_list.setVisibleRowCount(5);
/* 164:178 */     this.m_deleteBut.setEnabled(false);
/* 165:179 */     JPanel listPanel = new JPanel();
/* 166:180 */     listPanel.setLayout(new BorderLayout());
/* 167:181 */     JPanel butHolder = new JPanel();
/* 168:182 */     butHolder.setLayout(new GridLayout(1, 0));
/* 169:183 */     butHolder.add(this.m_newBut);
/* 170:184 */     butHolder.add(this.m_deleteBut);
/* 171:185 */     butHolder.add(this.m_upBut);
/* 172:186 */     butHolder.add(this.m_downBut);
/* 173:    */     
/* 174:188 */     listPanel.add(butHolder, "North");
/* 175:189 */     JScrollPane js = new JScrollPane(this.m_list);
/* 176:190 */     js.setBorder(BorderFactory.createTitledBorder("Attributes"));
/* 177:191 */     listPanel.add(js, "Center");
/* 178:    */     
/* 179:193 */     mainHolder.add(listPanel, "Center");
/* 180:    */     
/* 181:195 */     this.m_tabbedPane.addTab("Attribute definitions", mainHolder);
/* 182:196 */     this.m_tabbedPane.addTab("Data", this.m_viewerPanel);
/* 183:    */     
/* 184:198 */     add(this.m_tabbedPane, "Center");
/* 185:    */     
/* 186:200 */     this.m_attNameField.addPropertyChangeListener(new PropertyChangeListener()
/* 187:    */     {
/* 188:    */       public void propertyChange(PropertyChangeEvent evt)
/* 189:    */       {
/* 190:203 */         DataGridStepEditorDialog.AttDef a = (DataGridStepEditorDialog.AttDef)DataGridStepEditorDialog.this.m_list.getSelectedValue();
/* 191:204 */         if (a != null)
/* 192:    */         {
/* 193:205 */           a.m_name = DataGridStepEditorDialog.this.m_attNameField.getText();
/* 194:206 */           DataGridStepEditorDialog.this.m_list.repaint();
/* 195:    */         }
/* 196:    */       }
/* 197:210 */     });
/* 198:211 */     this.m_nominalOrDateFormatField.addPropertyChangeListener(new PropertyChangeListener()
/* 199:    */     {
/* 200:    */       public void propertyChange(PropertyChangeEvent evt)
/* 201:    */       {
/* 202:215 */         DataGridStepEditorDialog.AttDef a = (DataGridStepEditorDialog.AttDef)DataGridStepEditorDialog.this.m_list.getSelectedValue();
/* 203:216 */         if (a != null)
/* 204:    */         {
/* 205:217 */           a.m_nomOrDate = DataGridStepEditorDialog.this.m_nominalOrDateFormatField.getText();
/* 206:218 */           DataGridStepEditorDialog.this.m_list.repaint();
/* 207:    */         }
/* 208:    */       }
/* 209:222 */     });
/* 210:223 */     this.m_attTypeField.addActionListener(new ActionListener()
/* 211:    */     {
/* 212:    */       public void actionPerformed(ActionEvent e)
/* 213:    */       {
/* 214:226 */         DataGridStepEditorDialog.AttDef a = (DataGridStepEditorDialog.AttDef)DataGridStepEditorDialog.this.m_list.getSelectedValue();
/* 215:227 */         if (a != null)
/* 216:    */         {
/* 217:228 */           String type = DataGridStepEditorDialog.this.m_attTypeField.getSelectedItem().toString();
/* 218:229 */           a.m_type = DataGridStepEditorDialog.AttDef.attStringToType(type);
/* 219:230 */           DataGridStepEditorDialog.this.m_list.repaint();
/* 220:    */         }
/* 221:    */       }
/* 222:234 */     });
/* 223:235 */     this.m_tabbedPane.addChangeListener(new ChangeListener()
/* 224:    */     {
/* 225:    */       public void stateChanged(ChangeEvent e)
/* 226:    */       {
/* 227:238 */         if (DataGridStepEditorDialog.this.m_tabbedPane.getSelectedIndex() == 1) {
/* 228:239 */           DataGridStepEditorDialog.this.handleTabChange();
/* 229:    */         }
/* 230:    */       }
/* 231:243 */     });
/* 232:244 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 233:    */     {
/* 234:    */       public void valueChanged(ListSelectionEvent e)
/* 235:    */       {
/* 236:247 */         if (!e.getValueIsAdjusting())
/* 237:    */         {
/* 238:248 */           if (!DataGridStepEditorDialog.this.m_deleteBut.isEnabled()) {
/* 239:249 */             DataGridStepEditorDialog.this.m_deleteBut.setEnabled(true);
/* 240:    */           }
/* 241:251 */           DataGridStepEditorDialog.this.checkUpDown();
/* 242:    */           
/* 243:253 */           DataGridStepEditorDialog.AttDef entry = (DataGridStepEditorDialog.AttDef)DataGridStepEditorDialog.this.m_list.getSelectedValue();
/* 244:254 */           if (entry != null)
/* 245:    */           {
/* 246:255 */             DataGridStepEditorDialog.this.m_attNameField.setText(entry.m_name);
/* 247:256 */             DataGridStepEditorDialog.this.m_attTypeField.setSelectedItem(Attribute.typeToString(entry.m_type));
/* 248:257 */             DataGridStepEditorDialog.this.m_nominalOrDateFormatField.setText(entry.m_nomOrDate != null ? entry.m_nomOrDate : "");
/* 249:    */           }
/* 250:    */         }
/* 251:    */       }
/* 252:263 */     });
/* 253:264 */     this.m_newBut.addActionListener(new ActionListener()
/* 254:    */     {
/* 255:    */       public void actionPerformed(ActionEvent e)
/* 256:    */       {
/* 257:267 */         DataGridStepEditorDialog.AttDef def = new DataGridStepEditorDialog.AttDef(DataGridStepEditorDialog.this.m_attNameField.getText(), DataGridStepEditorDialog.AttDef.attStringToType(DataGridStepEditorDialog.this.m_attTypeField.getSelectedItem().toString()), DataGridStepEditorDialog.this.m_nominalOrDateFormatField.getText());
/* 258:    */         
/* 259:    */ 
/* 260:    */ 
/* 261:    */ 
/* 262:272 */         DataGridStepEditorDialog.this.m_listModel.addElement(def);
/* 263:273 */         DataGridStepEditorDialog.this.m_list.setSelectedIndex(DataGridStepEditorDialog.this.m_listModel.size() - 1);
/* 264:274 */         DataGridStepEditorDialog.this.checkUpDown();
/* 265:    */       }
/* 266:277 */     });
/* 267:278 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 268:    */     {
/* 269:    */       public void actionPerformed(ActionEvent e)
/* 270:    */       {
/* 271:281 */         int selected = DataGridStepEditorDialog.this.m_list.getSelectedIndex();
/* 272:282 */         if (selected >= 0)
/* 273:    */         {
/* 274:283 */           DataGridStepEditorDialog.this.m_listModel.removeElementAt(selected);
/* 275:    */           
/* 276:285 */           DataGridStepEditorDialog.this.checkUpDown();
/* 277:286 */           if (DataGridStepEditorDialog.this.m_listModel.size() <= 1)
/* 278:    */           {
/* 279:287 */             DataGridStepEditorDialog.this.m_upBut.setEnabled(false);
/* 280:288 */             DataGridStepEditorDialog.this.m_downBut.setEnabled(false);
/* 281:    */           }
/* 282:    */         }
/* 283:    */       }
/* 284:293 */     });
/* 285:294 */     this.m_upBut.addActionListener(new ActionListener()
/* 286:    */     {
/* 287:    */       public void actionPerformed(ActionEvent e)
/* 288:    */       {
/* 289:297 */         JListHelper.moveUp(DataGridStepEditorDialog.this.m_list);
/* 290:298 */         DataGridStepEditorDialog.this.checkUpDown();
/* 291:    */       }
/* 292:301 */     });
/* 293:302 */     this.m_downBut.addActionListener(new ActionListener()
/* 294:    */     {
/* 295:    */       public void actionPerformed(ActionEvent e)
/* 296:    */       {
/* 297:305 */         JListHelper.moveDown(DataGridStepEditorDialog.this.m_list);
/* 298:306 */         DataGridStepEditorDialog.this.checkUpDown();
/* 299:    */       }
/* 300:    */     });
/* 301:    */   }
/* 302:    */   
/* 303:    */   protected void checkUpDown()
/* 304:    */   {
/* 305:316 */     if ((this.m_list.getSelectedValue() != null) && (this.m_listModel.size() > 1))
/* 306:    */     {
/* 307:317 */       this.m_upBut.setEnabled(this.m_list.getSelectedIndex() > 0);
/* 308:318 */       this.m_downBut.setEnabled(this.m_list.getSelectedIndex() < this.m_listModel.size() - 1);
/* 309:    */     }
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void okPressed()
/* 313:    */   {
/* 314:327 */     Instances current = this.m_viewerPanel.getInstances();
/* 315:328 */     if (current != null) {
/* 316:329 */       ((DataGrid)getStepToEdit()).setData(current.toString());
/* 317:    */     } else {
/* 318:331 */       ((DataGrid)getStepToEdit()).setData("");
/* 319:    */     }
/* 320:    */   }
/* 321:    */   
/* 322:    */   protected void handleTabChange()
/* 323:    */   {
/* 324:340 */     ArrayList<Attribute> atts = new ArrayList();
/* 325:341 */     for (AttDef a : Collections.list(this.m_listModel.elements())) {
/* 326:342 */       if (a.m_type == 0)
/* 327:    */       {
/* 328:343 */         atts.add(new Attribute(a.m_name));
/* 329:    */       }
/* 330:344 */       else if (a.m_type == 2)
/* 331:    */       {
/* 332:345 */         atts.add(new Attribute(a.m_name, (List)null));
/* 333:    */       }
/* 334:346 */       else if (a.m_type == 3)
/* 335:    */       {
/* 336:347 */         atts.add(new Attribute(a.m_name, a.m_nomOrDate));
/* 337:    */       }
/* 338:348 */       else if (a.m_type == 1)
/* 339:    */       {
/* 340:349 */         List<String> vals = new ArrayList();
/* 341:350 */         for (String v : a.m_nomOrDate.split(",")) {
/* 342:351 */           vals.add(v.trim());
/* 343:    */         }
/* 344:353 */         atts.add(new Attribute(a.m_name, vals));
/* 345:    */       }
/* 346:354 */       else if (a.m_type != 4) {}
/* 347:    */     }
/* 348:358 */     Instances defInsts = new Instances("DataGrid", atts, 0);
/* 349:    */     
/* 350:    */ 
/* 351:361 */     Instances editInsts = this.m_viewerPanel.getInstances();
/* 352:362 */     if (editInsts != null)
/* 353:    */     {
/* 354:365 */       if (editInsts.numInstances() == 0)
/* 355:    */       {
/* 356:366 */         this.m_viewerPanel.setInstances(defInsts);
/* 357:    */       }
/* 358:    */       else
/* 359:    */       {
/* 360:368 */         Map<Integer, Integer> transferMap = new HashMap();
/* 361:369 */         for (int i = 0; i < editInsts.numAttributes(); i++)
/* 362:    */         {
/* 363:370 */           Attribute eA = editInsts.attribute(i);
/* 364:371 */           Attribute dA = defInsts.attribute(eA.name());
/* 365:372 */           if ((dA != null) && (dA.type() == eA.type())) {
/* 366:373 */             transferMap.put(Integer.valueOf(i), Integer.valueOf(dA.index()));
/* 367:    */           }
/* 368:    */         }
/* 369:377 */         if (transferMap.size() > 0)
/* 370:    */         {
/* 371:378 */           Instances defCopy = new Instances(defInsts, 0);
/* 372:379 */           for (int i = 0; i < editInsts.numInstances(); i++)
/* 373:    */           {
/* 374:380 */             double[] vals = new double[defCopy.numAttributes()];
/* 375:381 */             Instance editInst = editInsts.instance(i);
/* 376:382 */             for (int j = 0; j < vals.length; j++) {
/* 377:383 */               vals[j] = Utils.missingValue();
/* 378:    */             }
/* 379:385 */             for (Map.Entry<Integer, Integer> e : transferMap.entrySet()) {
/* 380:386 */               if (editInst.attribute(((Integer)e.getKey()).intValue()).isNumeric()) {
/* 381:387 */                 vals[((Integer)e.getValue()).intValue()] = editInst.value(((Integer)e.getKey()).intValue());
/* 382:388 */               } else if (editInst.attribute(((Integer)e.getKey()).intValue()).isNominal())
/* 383:    */               {
/* 384:389 */                 if (!editInst.isMissing(((Integer)e.getKey()).intValue()))
/* 385:    */                 {
/* 386:390 */                   int defIndex = defCopy.attribute(((Integer)e.getValue()).intValue()).indexOfValue(editInst.stringValue(((Integer)e.getKey()).intValue()));
/* 387:    */                   
/* 388:    */ 
/* 389:393 */                   vals[((Integer)e.getValue()).intValue()] = (defIndex >= 0 ? defIndex : Utils.missingValue());
/* 390:    */                 }
/* 391:    */               }
/* 392:396 */               else if (editInst.attribute(((Integer)e.getKey()).intValue()).isString())
/* 393:    */               {
/* 394:397 */                 if (!editInst.isMissing(((Integer)e.getKey()).intValue()))
/* 395:    */                 {
/* 396:398 */                   String editVal = editInst.stringValue(((Integer)e.getKey()).intValue());
/* 397:399 */                   vals[((Integer)e.getValue()).intValue()] = defCopy.attribute(((Integer)e.getValue()).intValue()).addStringValue(editVal);
/* 398:    */                 }
/* 399:    */               }
/* 400:402 */               else if (!editInst.attribute(((Integer)e.getKey()).intValue()).isRelationValued()) {}
/* 401:    */             }
/* 402:406 */             defCopy.add(new DenseInstance(1.0D, vals));
/* 403:    */           }
/* 404:408 */           this.m_viewerPanel.setInstances(defCopy);
/* 405:    */         }
/* 406:    */         else
/* 407:    */         {
/* 408:411 */           this.m_viewerPanel.setInstances(defInsts);
/* 409:    */         }
/* 410:    */       }
/* 411:    */     }
/* 412:    */     else {
/* 413:415 */       this.m_viewerPanel.setInstances(defInsts);
/* 414:    */     }
/* 415:    */   }
/* 416:    */   
/* 417:    */   protected static class AttDef
/* 418:    */   {
/* 419:423 */     protected String m_name = "";
/* 420:424 */     protected int m_type = 0;
/* 421:425 */     protected String m_nomOrDate = "";
/* 422:    */     
/* 423:    */     public AttDef(String name, int type, String nomOrDate)
/* 424:    */     {
/* 425:435 */       this.m_name = name;
/* 426:436 */       this.m_type = type;
/* 427:437 */       this.m_nomOrDate = nomOrDate;
/* 428:    */     }
/* 429:    */     
/* 430:    */     public String toString()
/* 431:    */     {
/* 432:447 */       String result = "@attribute " + this.m_name + " " + (this.m_type != 1 ? Attribute.typeToString(this.m_type) : " {");
/* 433:453 */       if (this.m_type == 1) {
/* 434:454 */         result = result + this.m_nomOrDate + "}";
/* 435:455 */       } else if (this.m_type == 3) {
/* 436:456 */         result = result + " " + this.m_nomOrDate;
/* 437:    */       }
/* 438:459 */       return result;
/* 439:    */     }
/* 440:    */     
/* 441:    */     public static int attStringToType(String type)
/* 442:    */     {
/* 443:469 */       if (type.equals("numeric")) {
/* 444:470 */         return 0;
/* 445:    */       }
/* 446:471 */       if (type.equals("nominal")) {
/* 447:472 */         return 1;
/* 448:    */       }
/* 449:473 */       if (type.equals("string")) {
/* 450:474 */         return 2;
/* 451:    */       }
/* 452:475 */       if (type.equals("date")) {
/* 453:476 */         return 3;
/* 454:    */       }
/* 455:477 */       if (type.equals("relational")) {
/* 456:478 */         return 4;
/* 457:    */       }
/* 458:480 */       return 0;
/* 459:    */     }
/* 460:    */   }
/* 461:    */   
/* 462:    */   protected static class ArffViewerPanel
/* 463:    */     extends JPanel
/* 464:    */   {
/* 465:    */     private static final long serialVersionUID = 2965315087365186710L;
/* 466:492 */     protected JButton m_UndoButton = new JButton("Undo");
/* 467:495 */     protected JButton m_addInstanceButton = new JButton("Add instance");
/* 468:498 */     protected ArffPanel m_ArffPanel = new ArffPanel();
/* 469:    */     
/* 470:    */     public ArffViewerPanel()
/* 471:    */     {
/* 472:505 */       setLayout(new BorderLayout());
/* 473:506 */       add(this.m_ArffPanel, "Center");
/* 474:    */       
/* 475:    */ 
/* 476:509 */       JPanel panel = new JPanel(new FlowLayout(2));
/* 477:510 */       panel.add(this.m_addInstanceButton);
/* 478:511 */       panel.add(this.m_UndoButton);
/* 479:    */       
/* 480:513 */       add(panel, "South");
/* 481:514 */       this.m_UndoButton.addActionListener(new ActionListener()
/* 482:    */       {
/* 483:    */         public void actionPerformed(ActionEvent e)
/* 484:    */         {
/* 485:516 */           DataGridStepEditorDialog.ArffViewerPanel.this.undo();
/* 486:    */         }
/* 487:518 */       });
/* 488:519 */       this.m_addInstanceButton.addActionListener(new ActionListener()
/* 489:    */       {
/* 490:    */         public void actionPerformed(ActionEvent e)
/* 491:    */         {
/* 492:522 */           DataGridStepEditorDialog.ArffViewerPanel.this.m_ArffPanel.addInstanceAtEnd();
/* 493:    */         }
/* 494:    */       });
/* 495:    */     }
/* 496:    */     
/* 497:    */     public void setInstances(Instances inst)
/* 498:    */     {
/* 499:531 */       this.m_ArffPanel.setInstances(new Instances(inst));
/* 500:    */     }
/* 501:    */     
/* 502:    */     public Instances getInstances()
/* 503:    */     {
/* 504:538 */       return this.m_ArffPanel.getInstances();
/* 505:    */     }
/* 506:    */     
/* 507:    */     protected void setButtons()
/* 508:    */     {
/* 509:545 */       this.m_UndoButton.setEnabled(this.m_ArffPanel.canUndo());
/* 510:    */     }
/* 511:    */     
/* 512:    */     private void undo()
/* 513:    */     {
/* 514:552 */       this.m_ArffPanel.undo();
/* 515:    */     }
/* 516:    */   }
/* 517:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.DataGridStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */