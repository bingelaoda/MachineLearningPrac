/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.GridLayout;
/*   7:    */ import java.awt.Window;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.KeyAdapter;
/*  11:    */ import java.awt.event.KeyEvent;
/*  12:    */ import java.awt.event.MouseAdapter;
/*  13:    */ import java.awt.event.MouseEvent;
/*  14:    */ import javax.swing.BorderFactory;
/*  15:    */ import javax.swing.ComboBoxEditor;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JCheckBox;
/*  18:    */ import javax.swing.JComboBox;
/*  19:    */ import javax.swing.JLabel;
/*  20:    */ import javax.swing.JOptionPane;
/*  21:    */ import javax.swing.JPanel;
/*  22:    */ import javax.swing.JScrollPane;
/*  23:    */ import javax.swing.JTextField;
/*  24:    */ import javax.swing.JTree;
/*  25:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  26:    */ import javax.swing.tree.DefaultTreeModel;
/*  27:    */ import javax.swing.tree.DefaultTreeSelectionModel;
/*  28:    */ import javax.swing.tree.TreePath;
/*  29:    */ import weka.core.Attribute;
/*  30:    */ import weka.core.Environment;
/*  31:    */ import weka.core.EnvironmentHandler;
/*  32:    */ import weka.core.Instances;
/*  33:    */ import weka.gui.PropertySheetPanel;
/*  34:    */ 
/*  35:    */ public class FlowByExpressionCustomizer
/*  36:    */   extends JPanel
/*  37:    */   implements EnvironmentHandler, BeanCustomizer, CustomizerCloseRequester
/*  38:    */ {
/*  39:    */   private static final long serialVersionUID = -3574741335754017794L;
/*  40: 69 */   protected Environment m_env = Environment.getSystemWide();
/*  41: 70 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  42:    */   protected FlowByExpression m_expression;
/*  43: 73 */   protected JComboBox m_lhsField = new EnvironmentField.WideComboBox();
/*  44: 74 */   protected JComboBox m_operatorCombo = new JComboBox();
/*  45: 75 */   protected JComboBox m_rhsField = new EnvironmentField.WideComboBox();
/*  46: 76 */   protected JCheckBox m_rhsIsAttribute = new JCheckBox("RHS is attribute");
/*  47: 77 */   protected JLabel m_expressionLab = new JLabel();
/*  48: 79 */   protected JComboBox m_trueData = new JComboBox();
/*  49: 80 */   protected JComboBox m_falseData = new JComboBox();
/*  50:    */   protected JTree m_expressionTree;
/*  51:    */   protected DefaultMutableTreeNode m_treeRoot;
/*  52: 85 */   protected JButton m_addExpressionNode = new JButton("Add Expression node");
/*  53: 86 */   protected JButton m_addBracketNode = new JButton("Add bracket node");
/*  54: 87 */   protected JButton m_toggleNegation = new JButton("Toggle negation");
/*  55: 88 */   protected JButton m_andOr = new JButton("And/Or");
/*  56: 89 */   protected JButton m_deleteNode = new JButton("Delete node");
/*  57: 91 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  58:    */   protected Window m_parent;
/*  59:    */   
/*  60:    */   public FlowByExpressionCustomizer()
/*  61:    */   {
/*  62: 99 */     setLayout(new BorderLayout());
/*  63:    */   }
/*  64:    */   
/*  65:    */   private void setup()
/*  66:    */   {
/*  67:103 */     JPanel aboutAndControlHolder = new JPanel();
/*  68:104 */     aboutAndControlHolder.setLayout(new BorderLayout());
/*  69:    */     
/*  70:106 */     JPanel controlHolder = new JPanel();
/*  71:107 */     controlHolder.setLayout(new BorderLayout());
/*  72:108 */     JPanel fieldHolder = new JPanel();
/*  73:109 */     fieldHolder.setLayout(new GridLayout(0, 4));
/*  74:    */     
/*  75:111 */     JPanel lhsP = new JPanel();
/*  76:112 */     lhsP.setLayout(new BorderLayout());
/*  77:113 */     lhsP.setBorder(BorderFactory.createTitledBorder("Attribute"));
/*  78:    */     
/*  79:    */ 
/*  80:116 */     this.m_lhsField.setEditable(true);
/*  81:    */     
/*  82:118 */     lhsP.add(this.m_lhsField, "Center");
/*  83:119 */     lhsP.setToolTipText("<html>Name or index of attribute. also accepts<br>the special labels \"/first\" and \"/last\" to indicate<br>the first and last attribute respecitively</html>");
/*  84:    */     
/*  85:    */ 
/*  86:122 */     this.m_lhsField.setToolTipText("<html>Name or index of attribute. also accepts<br>the special labels \"/first\" and \"/last\" to indicate<br>the first and last attribute respecitively</html>");
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:126 */     JPanel operatorP = new JPanel();
/*  91:127 */     operatorP.setLayout(new BorderLayout());
/*  92:128 */     operatorP.setBorder(BorderFactory.createTitledBorder("Operator"));
/*  93:129 */     this.m_operatorCombo.addItem(" = ");
/*  94:130 */     this.m_operatorCombo.addItem(" != ");
/*  95:131 */     this.m_operatorCombo.addItem(" < ");
/*  96:132 */     this.m_operatorCombo.addItem(" <= ");
/*  97:133 */     this.m_operatorCombo.addItem(" > ");
/*  98:134 */     this.m_operatorCombo.addItem(" >= ");
/*  99:135 */     this.m_operatorCombo.addItem(" isMissing ");
/* 100:136 */     this.m_operatorCombo.addItem(" contains ");
/* 101:137 */     this.m_operatorCombo.addItem(" startsWith ");
/* 102:138 */     this.m_operatorCombo.addItem(" endsWith ");
/* 103:139 */     this.m_operatorCombo.addItem(" regex ");
/* 104:140 */     operatorP.add(this.m_operatorCombo, "Center");
/* 105:    */     
/* 106:142 */     JPanel rhsP = new JPanel();
/* 107:143 */     rhsP.setLayout(new BorderLayout());
/* 108:144 */     rhsP.setBorder(BorderFactory.createTitledBorder("Constant or attribute"));
/* 109:145 */     rhsP.setToolTipText("<html>Constant value to test/check for. If testing<br>against an attribute, then this specifiesan attribute index or name</html>");
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:149 */     this.m_rhsField.setEditable(true);
/* 114:150 */     rhsP.add(this.m_rhsField, "Center");
/* 115:    */     
/* 116:152 */     fieldHolder.add(lhsP);
/* 117:153 */     fieldHolder.add(operatorP);
/* 118:154 */     fieldHolder.add(rhsP);
/* 119:155 */     fieldHolder.add(this.m_rhsIsAttribute);
/* 120:156 */     controlHolder.add(fieldHolder, "South");
/* 121:    */     
/* 122:158 */     JPanel tempPanel = new JPanel();
/* 123:159 */     tempPanel.setLayout(new BorderLayout());
/* 124:160 */     JPanel expressionP = new JPanel();
/* 125:161 */     expressionP.setLayout(new BorderLayout());
/* 126:162 */     expressionP.setBorder(BorderFactory.createTitledBorder("Expression"));
/* 127:163 */     JPanel tempE = new JPanel();
/* 128:164 */     tempE.setLayout(new BorderLayout());
/* 129:165 */     tempE.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
/* 130:166 */     tempE.add(this.m_expressionLab, "Center");
/* 131:167 */     JScrollPane expressionScroller = new JScrollPane(tempE);
/* 132:168 */     expressionP.add(expressionScroller, "Center");
/* 133:169 */     tempPanel.add(expressionP, "South");
/* 134:    */     
/* 135:    */ 
/* 136:172 */     JPanel flowControlP = new JPanel();
/* 137:173 */     flowControlP.setLayout(new GridLayout(2, 2));
/* 138:174 */     flowControlP.add(new JLabel("Send true instances to node", 4));
/* 139:    */     
/* 140:176 */     flowControlP.add(this.m_trueData);
/* 141:177 */     flowControlP.add(new JLabel("Send false instances to node", 4));
/* 142:    */     
/* 143:179 */     flowControlP.add(this.m_falseData);
/* 144:180 */     tempPanel.add(flowControlP, "North");
/* 145:181 */     String falseStepN = this.m_expression.getFalseStepName();
/* 146:182 */     String trueStepN = this.m_expression.getTrueStepName();
/* 147:183 */     Object[] connectedSteps = this.m_expression.m_downstream;
/* 148:184 */     this.m_trueData.addItem("<none>");
/* 149:185 */     this.m_falseData.addItem("<none>");
/* 150:186 */     if ((connectedSteps != null) && (connectedSteps.length > 0))
/* 151:    */     {
/* 152:187 */       if (connectedSteps[0] != null)
/* 153:    */       {
/* 154:188 */         String first = ((BeanCommon)connectedSteps[0]).getCustomName();
/* 155:189 */         this.m_trueData.addItem(first);
/* 156:190 */         this.m_falseData.addItem(first);
/* 157:    */       }
/* 158:192 */       if ((connectedSteps.length > 1) && (connectedSteps[1] != null))
/* 159:    */       {
/* 160:193 */         String second = ((BeanCommon)connectedSteps[1]).getCustomName();
/* 161:194 */         this.m_trueData.addItem(second);
/* 162:195 */         this.m_falseData.addItem(second);
/* 163:    */       }
/* 164:    */     }
/* 165:198 */     if ((falseStepN != null) && (falseStepN.length() > 0)) {
/* 166:199 */       this.m_falseData.setSelectedItem(falseStepN);
/* 167:    */     }
/* 168:201 */     if ((trueStepN != null) && (trueStepN.length() > 0)) {
/* 169:202 */       this.m_trueData.setSelectedItem(trueStepN);
/* 170:    */     }
/* 171:205 */     controlHolder.add(tempPanel, "North");
/* 172:    */     
/* 173:207 */     aboutAndControlHolder.add(controlHolder, "South");
/* 174:208 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 175:209 */     aboutAndControlHolder.add(aboutP, "North");
/* 176:210 */     add(aboutAndControlHolder, "North");
/* 177:    */     
/* 178:212 */     addButtons();
/* 179:    */     
/* 180:214 */     this.m_rhsIsAttribute.addActionListener(new ActionListener()
/* 181:    */     {
/* 182:    */       public void actionPerformed(ActionEvent e)
/* 183:    */       {
/* 184:217 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 185:    */         {
/* 186:218 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 187:219 */           if ((p != null) && 
/* 188:220 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 189:    */           {
/* 190:222 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 191:    */             
/* 192:224 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 193:227 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 194:    */             {
/* 195:228 */               ((FlowByExpression.ExpressionClause)thisNode).m_rhsIsAttribute = FlowByExpressionCustomizer.this.m_rhsIsAttribute.isSelected();
/* 196:    */               
/* 197:    */ 
/* 198:231 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 199:    */               
/* 200:233 */               tmodel.nodeStructureChanged(tNode);
/* 201:    */               
/* 202:235 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 203:    */             }
/* 204:    */           }
/* 205:    */         }
/* 206:    */       }
/* 207:242 */     });
/* 208:243 */     this.m_operatorCombo.addActionListener(new ActionListener()
/* 209:    */     {
/* 210:    */       public void actionPerformed(ActionEvent e)
/* 211:    */       {
/* 212:247 */         if (FlowByExpressionCustomizer.this.m_operatorCombo.getSelectedIndex() > 5)
/* 213:    */         {
/* 214:248 */           FlowByExpressionCustomizer.this.m_rhsIsAttribute.setSelected(false);
/* 215:249 */           FlowByExpressionCustomizer.this.m_rhsIsAttribute.setEnabled(false);
/* 216:    */         }
/* 217:    */         else
/* 218:    */         {
/* 219:251 */           FlowByExpressionCustomizer.this.m_rhsIsAttribute.setEnabled(true);
/* 220:    */         }
/* 221:254 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 222:    */         {
/* 223:255 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 224:256 */           if ((p != null) && 
/* 225:257 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 226:    */           {
/* 227:259 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 228:    */             
/* 229:261 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 230:264 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 231:    */             {
/* 232:265 */               String selection = FlowByExpressionCustomizer.this.m_operatorCombo.getSelectedItem().toString();
/* 233:266 */               FlowByExpression.ExpressionClause.ExpressionType t = FlowByExpression.ExpressionClause.ExpressionType.EQUALS;
/* 234:268 */               for (FlowByExpression.ExpressionClause.ExpressionType et : FlowByExpression.ExpressionClause.ExpressionType.values()) {
/* 235:270 */                 if (et.toString().equals(selection))
/* 236:    */                 {
/* 237:271 */                   t = et;
/* 238:272 */                   break;
/* 239:    */                 }
/* 240:    */               }
/* 241:276 */               ((FlowByExpression.ExpressionClause)thisNode).m_operator = t;
/* 242:277 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 243:    */               
/* 244:279 */               tmodel.nodeStructureChanged(tNode);
/* 245:    */               
/* 246:281 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 247:    */             }
/* 248:    */           }
/* 249:    */         }
/* 250:    */       }
/* 251:288 */     });
/* 252:289 */     this.m_lhsField.addActionListener(new ActionListener()
/* 253:    */     {
/* 254:    */       public void actionPerformed(ActionEvent e)
/* 255:    */       {
/* 256:292 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 257:    */         {
/* 258:293 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 259:294 */           if ((p != null) && 
/* 260:295 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 261:    */           {
/* 262:297 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 263:    */             
/* 264:299 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 265:302 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 266:    */             {
/* 267:303 */               Object text = FlowByExpressionCustomizer.this.m_lhsField.getSelectedItem();
/* 268:304 */               if (text != null)
/* 269:    */               {
/* 270:305 */                 ((FlowByExpression.ExpressionClause)thisNode).m_lhsAttributeName = text.toString();
/* 271:    */                 
/* 272:307 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 273:    */                 
/* 274:309 */                 tmodel.nodeStructureChanged(tNode);
/* 275:    */                 
/* 276:311 */                 FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 277:    */               }
/* 278:    */             }
/* 279:    */           }
/* 280:    */         }
/* 281:    */       }
/* 282:319 */     });
/* 283:320 */     this.m_lhsField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 284:    */     {
/* 285:    */       public void keyReleased(KeyEvent e)
/* 286:    */       {
/* 287:324 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 288:    */         {
/* 289:325 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 290:326 */           if ((p != null) && 
/* 291:327 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 292:    */           {
/* 293:329 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 294:    */             
/* 295:331 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 296:334 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 297:    */             {
/* 298:335 */               String text = "";
/* 299:336 */               if (FlowByExpressionCustomizer.this.m_lhsField.getSelectedItem() != null) {
/* 300:337 */                 text = FlowByExpressionCustomizer.this.m_lhsField.getSelectedItem().toString();
/* 301:    */               }
/* 302:339 */               Component theEditor = FlowByExpressionCustomizer.this.m_lhsField.getEditor().getEditorComponent();
/* 303:341 */               if ((theEditor instanceof JTextField)) {
/* 304:342 */                 text = ((JTextField)theEditor).getText();
/* 305:    */               }
/* 306:344 */               ((FlowByExpression.ExpressionClause)thisNode).m_lhsAttributeName = text;
/* 307:    */               
/* 308:346 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 309:    */               
/* 310:348 */               tmodel.nodeStructureChanged(tNode);
/* 311:    */               
/* 312:350 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 313:    */             }
/* 314:    */           }
/* 315:    */         }
/* 316:    */       }
/* 317:357 */     });
/* 318:358 */     this.m_rhsField.addActionListener(new ActionListener()
/* 319:    */     {
/* 320:    */       public void actionPerformed(ActionEvent e)
/* 321:    */       {
/* 322:361 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 323:    */         {
/* 324:362 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 325:363 */           if ((p != null) && 
/* 326:364 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 327:    */           {
/* 328:366 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 329:    */             
/* 330:368 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 331:371 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 332:    */             {
/* 333:372 */               Object text = FlowByExpressionCustomizer.this.m_rhsField.getSelectedItem();
/* 334:373 */               if (text != null)
/* 335:    */               {
/* 336:374 */                 ((FlowByExpression.ExpressionClause)thisNode).m_rhsOperand = text.toString();
/* 337:    */                 
/* 338:376 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 339:    */                 
/* 340:378 */                 tmodel.nodeStructureChanged(tNode);
/* 341:    */                 
/* 342:380 */                 FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 343:    */               }
/* 344:    */             }
/* 345:    */           }
/* 346:    */         }
/* 347:    */       }
/* 348:388 */     });
/* 349:389 */     this.m_rhsField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 350:    */     {
/* 351:    */       public void keyReleased(KeyEvent e)
/* 352:    */       {
/* 353:393 */         if (FlowByExpressionCustomizer.this.m_expressionTree != null)
/* 354:    */         {
/* 355:394 */           TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 356:395 */           if ((p != null) && 
/* 357:396 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 358:    */           {
/* 359:398 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 360:    */             
/* 361:400 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 362:403 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 363:    */             {
/* 364:404 */               String text = "";
/* 365:405 */               if (FlowByExpressionCustomizer.this.m_rhsField.getSelectedItem() != null) {
/* 366:406 */                 text = FlowByExpressionCustomizer.this.m_rhsField.getSelectedItem().toString();
/* 367:    */               }
/* 368:408 */               Component theEditor = FlowByExpressionCustomizer.this.m_rhsField.getEditor().getEditorComponent();
/* 369:410 */               if ((theEditor instanceof JTextField)) {
/* 370:411 */                 text = ((JTextField)theEditor).getText();
/* 371:    */               }
/* 372:414 */               if (FlowByExpressionCustomizer.this.m_rhsField.getSelectedItem() != null)
/* 373:    */               {
/* 374:415 */                 ((FlowByExpression.ExpressionClause)thisNode).m_rhsOperand = text;
/* 375:    */                 
/* 376:417 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 377:    */                 
/* 378:419 */                 tmodel.nodeStructureChanged(tNode);
/* 379:    */                 
/* 380:421 */                 FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 381:    */               }
/* 382:    */             }
/* 383:    */           }
/* 384:    */         }
/* 385:    */       }
/* 386:    */     });
/* 387:431 */     if (this.m_expression.getConnectedFormat() != null)
/* 388:    */     {
/* 389:432 */       Instances incoming = this.m_expression.getConnectedFormat();
/* 390:    */       
/* 391:434 */       this.m_lhsField.removeAllItems();
/* 392:435 */       this.m_rhsField.removeAllItems();
/* 393:436 */       for (int i = 0; i < incoming.numAttributes(); i++)
/* 394:    */       {
/* 395:437 */         this.m_lhsField.addItem(incoming.attribute(i).name());
/* 396:438 */         this.m_rhsField.addItem(incoming.attribute(i).name());
/* 397:    */       }
/* 398:    */     }
/* 399:442 */     Dimension d = operatorP.getPreferredSize();
/* 400:443 */     lhsP.setPreferredSize(d);
/* 401:444 */     rhsP.setPreferredSize(d);
/* 402:    */   }
/* 403:    */   
/* 404:    */   private void addButtons()
/* 405:    */   {
/* 406:448 */     JButton okBut = new JButton("OK");
/* 407:449 */     JButton cancelBut = new JButton("Cancel");
/* 408:    */     
/* 409:451 */     JPanel butHolder = new JPanel();
/* 410:452 */     butHolder.setLayout(new GridLayout(1, 2));
/* 411:453 */     butHolder.add(okBut);
/* 412:454 */     butHolder.add(cancelBut);
/* 413:455 */     add(butHolder, "South");
/* 414:    */     
/* 415:457 */     okBut.addActionListener(new ActionListener()
/* 416:    */     {
/* 417:    */       public void actionPerformed(ActionEvent e)
/* 418:    */       {
/* 419:460 */         FlowByExpressionCustomizer.this.closingOK();
/* 420:    */         
/* 421:462 */         FlowByExpressionCustomizer.this.m_parent.dispose();
/* 422:    */       }
/* 423:465 */     });
/* 424:466 */     cancelBut.addActionListener(new ActionListener()
/* 425:    */     {
/* 426:    */       public void actionPerformed(ActionEvent e)
/* 427:    */       {
/* 428:469 */         FlowByExpressionCustomizer.this.closingCancel();
/* 429:    */         
/* 430:471 */         FlowByExpressionCustomizer.this.m_parent.dispose();
/* 431:    */       }
/* 432:    */     });
/* 433:    */   }
/* 434:    */   
/* 435:    */   private void updateExpressionLabel()
/* 436:    */   {
/* 437:477 */     StringBuffer buff = new StringBuffer();
/* 438:    */     
/* 439:479 */     FlowByExpression.ExpressionNode root = (FlowByExpression.ExpressionNode)this.m_treeRoot.getUserObject();
/* 440:    */     
/* 441:481 */     root.toStringDisplay(buff);
/* 442:482 */     this.m_expressionLab.setText(buff.toString());
/* 443:    */   }
/* 444:    */   
/* 445:    */   private void setExpressionEditor(FlowByExpression.ExpressionClause node)
/* 446:    */   {
/* 447:486 */     String lhs = node.m_lhsAttributeName;
/* 448:487 */     if (lhs != null) {
/* 449:488 */       this.m_lhsField.setSelectedItem(lhs);
/* 450:    */     }
/* 451:490 */     String rhs = node.m_rhsOperand;
/* 452:491 */     if (rhs != null) {
/* 453:492 */       this.m_rhsField.setSelectedItem(rhs);
/* 454:    */     }
/* 455:494 */     FlowByExpression.ExpressionClause.ExpressionType opp = node.m_operator;
/* 456:495 */     int oppIndex = opp.ordinal();
/* 457:496 */     this.m_operatorCombo.setSelectedIndex(oppIndex);
/* 458:497 */     this.m_rhsIsAttribute.setSelected(node.m_rhsIsAttribute);
/* 459:    */   }
/* 460:    */   
/* 461:    */   private void setupTree()
/* 462:    */   {
/* 463:501 */     JPanel treeHolder = new JPanel();
/* 464:502 */     treeHolder.setLayout(new BorderLayout());
/* 465:503 */     treeHolder.setBorder(BorderFactory.createTitledBorder("Expression tree"));
/* 466:504 */     String expressionString = this.m_expression.getExpressionString();
/* 467:505 */     if ((expressionString == null) || (expressionString.length() == 0)) {
/* 468:506 */       expressionString = "()";
/* 469:    */     }
/* 470:508 */     FlowByExpression.BracketNode root = new FlowByExpression.BracketNode();
/* 471:509 */     root.parseFromInternal(expressionString);
/* 472:510 */     root.setShowAndOr(false);
/* 473:    */     
/* 474:512 */     this.m_treeRoot = root.toJTree(null);
/* 475:    */     
/* 476:514 */     DefaultTreeModel model = new DefaultTreeModel(this.m_treeRoot);
/* 477:515 */     this.m_expressionTree = new JTree(model);
/* 478:516 */     this.m_expressionTree.setEnabled(true);
/* 479:517 */     this.m_expressionTree.setRootVisible(true);
/* 480:518 */     this.m_expressionTree.setShowsRootHandles(true);
/* 481:519 */     DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/* 482:520 */     selectionModel.setSelectionMode(1);
/* 483:521 */     this.m_expressionTree.setSelectionModel(selectionModel);
/* 484:    */     
/* 485:    */ 
/* 486:524 */     this.m_expressionTree.addMouseListener(new MouseAdapter()
/* 487:    */     {
/* 488:    */       public void mouseClicked(MouseEvent e)
/* 489:    */       {
/* 490:527 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 491:528 */         if ((p != null) && 
/* 492:529 */           ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 493:    */         {
/* 494:530 */           DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 495:    */           
/* 496:    */ 
/* 497:533 */           FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 498:536 */           if ((thisNode instanceof FlowByExpression.ExpressionClause)) {
/* 499:537 */             FlowByExpressionCustomizer.this.setExpressionEditor((FlowByExpression.ExpressionClause)thisNode);
/* 500:    */           }
/* 501:    */         }
/* 502:    */       }
/* 503:543 */     });
/* 504:544 */     updateExpressionLabel();
/* 505:    */     
/* 506:546 */     JScrollPane treeView = new JScrollPane(this.m_expressionTree);
/* 507:547 */     treeHolder.add(treeView, "Center");
/* 508:    */     
/* 509:549 */     JPanel butHolder = new JPanel();
/* 510:    */     
/* 511:551 */     butHolder.add(this.m_addExpressionNode);
/* 512:552 */     butHolder.add(this.m_addBracketNode);
/* 513:553 */     butHolder.add(this.m_toggleNegation);
/* 514:554 */     butHolder.add(this.m_andOr);
/* 515:555 */     butHolder.add(this.m_deleteNode);
/* 516:556 */     treeHolder.add(butHolder, "North");
/* 517:    */     
/* 518:558 */     add(treeHolder, "Center");
/* 519:559 */     Dimension d = treeHolder.getPreferredSize();
/* 520:560 */     treeHolder.setPreferredSize(new Dimension(d.width, d.height / 2));
/* 521:    */     
/* 522:562 */     this.m_andOr.addActionListener(new ActionListener()
/* 523:    */     {
/* 524:    */       public void actionPerformed(ActionEvent e)
/* 525:    */       {
/* 526:565 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 527:566 */         if (p != null)
/* 528:    */         {
/* 529:567 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 530:    */           {
/* 531:569 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 532:    */             
/* 533:571 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 534:    */             
/* 535:    */ 
/* 536:574 */             thisNode.setIsOr(!thisNode.isOr());
/* 537:575 */             DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 538:    */             
/* 539:577 */             tmodel.nodeStructureChanged(tNode);
/* 540:    */             
/* 541:579 */             FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 542:    */           }
/* 543:    */         }
/* 544:    */         else {
/* 545:582 */           JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "Please select a node in the tree to alter the boolean operator of", "And/Or", 1);
/* 546:    */         }
/* 547:    */       }
/* 548:590 */     });
/* 549:591 */     this.m_toggleNegation.addActionListener(new ActionListener()
/* 550:    */     {
/* 551:    */       public void actionPerformed(ActionEvent e)
/* 552:    */       {
/* 553:594 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 554:595 */         if (p != null)
/* 555:    */         {
/* 556:596 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 557:    */           {
/* 558:598 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 559:    */             
/* 560:600 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 561:    */             
/* 562:    */ 
/* 563:603 */             thisNode.setNegated(!thisNode.isNegated());
/* 564:604 */             DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 565:    */             
/* 566:606 */             tmodel.nodeStructureChanged(tNode);
/* 567:    */             
/* 568:608 */             FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 569:    */           }
/* 570:    */         }
/* 571:    */         else {
/* 572:611 */           JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "Please select a node in the tree to toggle its negation", "Toggle negation", 1);
/* 573:    */         }
/* 574:    */       }
/* 575:617 */     });
/* 576:618 */     this.m_deleteNode.addActionListener(new ActionListener()
/* 577:    */     {
/* 578:    */       public void actionPerformed(ActionEvent e)
/* 579:    */       {
/* 580:621 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 581:622 */         if (p != null)
/* 582:    */         {
/* 583:623 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 584:    */           {
/* 585:624 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 586:627 */             if (tNode == FlowByExpressionCustomizer.this.m_treeRoot)
/* 587:    */             {
/* 588:628 */               JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "You can't delete the root of the tree!", "Delete node", 1);
/* 589:    */             }
/* 590:    */             else
/* 591:    */             {
/* 592:632 */               FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 593:    */               
/* 594:    */ 
/* 595:635 */               FlowByExpression.BracketNode parentNode = (FlowByExpression.BracketNode)((DefaultMutableTreeNode)tNode.getParent()).getUserObject();
/* 596:    */               
/* 597:    */ 
/* 598:    */ 
/* 599:    */ 
/* 600:640 */               parentNode.removeChild(thisNode);
/* 601:    */               
/* 602:    */ 
/* 603:643 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 604:    */               
/* 605:645 */               tmodel.removeNodeFromParent(tNode);
/* 606:646 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 607:    */             }
/* 608:    */           }
/* 609:    */         }
/* 610:    */         else {
/* 611:650 */           JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "Please select a node in the tree to delete.", "Delete node", 1);
/* 612:    */         }
/* 613:    */       }
/* 614:656 */     });
/* 615:657 */     this.m_addExpressionNode.addActionListener(new ActionListener()
/* 616:    */     {
/* 617:    */       public void actionPerformed(ActionEvent e)
/* 618:    */       {
/* 619:660 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 620:661 */         if (p != null)
/* 621:    */         {
/* 622:662 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 623:    */           {
/* 624:664 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 625:    */             
/* 626:666 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 627:669 */             if ((thisNode instanceof FlowByExpression.BracketNode))
/* 628:    */             {
/* 629:670 */               FlowByExpression.ExpressionClause newNode = new FlowByExpression.ExpressionClause(FlowByExpression.ExpressionClause.ExpressionType.EQUALS, "<att name>", "<value>", false, false);
/* 630:    */               
/* 631:    */ 
/* 632:    */ 
/* 633:    */ 
/* 634:675 */               ((FlowByExpression.BracketNode)thisNode).addChild(newNode);
/* 635:676 */               DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newNode);
/* 636:    */               
/* 637:    */ 
/* 638:679 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 639:    */               
/* 640:681 */               tNode.add(childNode);
/* 641:682 */               tmodel.nodeStructureChanged(tNode);
/* 642:683 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 643:    */             }
/* 644:    */             else
/* 645:    */             {
/* 646:685 */               JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "An expression can only be added to a bracket node.", "Add expression", 1);
/* 647:    */             }
/* 648:    */           }
/* 649:    */         }
/* 650:    */         else {
/* 651:691 */           JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "You must select a bracket node in the tree view to add a new expression to.", "Add expression", 1);
/* 652:    */         }
/* 653:    */       }
/* 654:699 */     });
/* 655:700 */     this.m_addBracketNode.addActionListener(new ActionListener()
/* 656:    */     {
/* 657:    */       public void actionPerformed(ActionEvent e)
/* 658:    */       {
/* 659:703 */         TreePath p = FlowByExpressionCustomizer.this.m_expressionTree.getSelectionPath();
/* 660:704 */         if (p != null)
/* 661:    */         {
/* 662:705 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 663:    */           {
/* 664:707 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 665:    */             
/* 666:709 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 667:712 */             if ((thisNode instanceof FlowByExpression.BracketNode))
/* 668:    */             {
/* 669:713 */               FlowByExpression.BracketNode newNode = new FlowByExpression.BracketNode();
/* 670:    */               
/* 671:715 */               ((FlowByExpression.BracketNode)thisNode).addChild(newNode);
/* 672:716 */               DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newNode);
/* 673:    */               
/* 674:    */ 
/* 675:719 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionCustomizer.this.m_expressionTree.getModel();
/* 676:    */               
/* 677:721 */               tNode.add(childNode);
/* 678:722 */               tmodel.nodeStructureChanged(tNode);
/* 679:723 */               FlowByExpressionCustomizer.this.updateExpressionLabel();
/* 680:    */             }
/* 681:    */             else
/* 682:    */             {
/* 683:725 */               JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "An new bracket node can only be added to an existing bracket node.", "Add bracket", 1);
/* 684:    */             }
/* 685:    */           }
/* 686:    */         }
/* 687:    */         else {
/* 688:733 */           JOptionPane.showMessageDialog(FlowByExpressionCustomizer.this, "You must select an existing bracket node in the tree in order to add a new bracket node.", "Add bracket", 1);
/* 689:    */         }
/* 690:    */       }
/* 691:    */     });
/* 692:    */   }
/* 693:    */   
/* 694:    */   public void setObject(Object o)
/* 695:    */   {
/* 696:746 */     if ((o instanceof FlowByExpression))
/* 697:    */     {
/* 698:747 */       this.m_expression = ((FlowByExpression)o);
/* 699:748 */       this.m_tempEditor.setTarget(o);
/* 700:749 */       setup();
/* 701:750 */       setupTree();
/* 702:    */     }
/* 703:    */   }
/* 704:    */   
/* 705:    */   public void setParentWindow(Window parent)
/* 706:    */   {
/* 707:756 */     this.m_parent = parent;
/* 708:    */   }
/* 709:    */   
/* 710:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 711:    */   {
/* 712:761 */     this.m_modifyL = l;
/* 713:    */   }
/* 714:    */   
/* 715:    */   public void setEnvironment(Environment env)
/* 716:    */   {
/* 717:766 */     this.m_env = env;
/* 718:    */   }
/* 719:    */   
/* 720:    */   private void closingOK()
/* 721:    */   {
/* 722:773 */     if (this.m_treeRoot != null)
/* 723:    */     {
/* 724:774 */       FlowByExpression.ExpressionNode en = (FlowByExpression.ExpressionNode)this.m_treeRoot.getUserObject();
/* 725:    */       
/* 726:776 */       StringBuffer buff = new StringBuffer();
/* 727:777 */       en.toStringInternal(buff);
/* 728:    */       
/* 729:779 */       this.m_expression.setExpressionString(buff.toString());
/* 730:781 */       if ((this.m_trueData.getSelectedItem() != null) && (this.m_trueData.getSelectedItem().toString().length() > 0)) {
/* 731:783 */         this.m_expression.setTrueStepName(this.m_trueData.getSelectedItem().toString());
/* 732:    */       }
/* 733:786 */       if ((this.m_falseData.getSelectedItem() != null) && (this.m_falseData.getSelectedItem().toString().length() > 0)) {
/* 734:788 */         this.m_expression.setFalseStepName(this.m_falseData.getSelectedItem().toString());
/* 735:    */       }
/* 736:    */     }
/* 737:    */   }
/* 738:    */   
/* 739:    */   private void closingCancel() {}
/* 740:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FlowByExpressionCustomizer
 * JD-Core Version:    0.7.0.1
 */