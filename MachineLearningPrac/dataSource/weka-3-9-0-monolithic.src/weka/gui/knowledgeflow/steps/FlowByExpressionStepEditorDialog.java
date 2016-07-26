/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.GridLayout;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.KeyAdapter;
/*  10:    */ import java.awt.event.KeyEvent;
/*  11:    */ import java.awt.event.MouseAdapter;
/*  12:    */ import java.awt.event.MouseEvent;
/*  13:    */ import java.util.List;
/*  14:    */ import javax.swing.BorderFactory;
/*  15:    */ import javax.swing.ComboBoxEditor;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JCheckBox;
/*  18:    */ import javax.swing.JComboBox;
/*  19:    */ import javax.swing.JLabel;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JTextField;
/*  23:    */ import javax.swing.JTree;
/*  24:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  25:    */ import javax.swing.tree.DefaultTreeModel;
/*  26:    */ import javax.swing.tree.DefaultTreeSelectionModel;
/*  27:    */ import javax.swing.tree.TreePath;
/*  28:    */ import weka.core.Attribute;
/*  29:    */ import weka.core.Instances;
/*  30:    */ import weka.core.WekaException;
/*  31:    */ import weka.gui.EnvironmentField.WideComboBox;
/*  32:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  33:    */ import weka.knowledgeflow.StepManager;
/*  34:    */ import weka.knowledgeflow.steps.FlowByExpression;
/*  35:    */ import weka.knowledgeflow.steps.FlowByExpression.BracketNode;
/*  36:    */ import weka.knowledgeflow.steps.FlowByExpression.ExpressionClause;
/*  37:    */ import weka.knowledgeflow.steps.FlowByExpression.ExpressionClause.ExpressionType;
/*  38:    */ import weka.knowledgeflow.steps.FlowByExpression.ExpressionNode;
/*  39:    */ import weka.knowledgeflow.steps.Step;
/*  40:    */ 
/*  41:    */ public class FlowByExpressionStepEditorDialog
/*  42:    */   extends StepEditorDialog
/*  43:    */ {
/*  44:    */   private static final long serialVersionUID = 1545740909421963983L;
/*  45: 57 */   protected JComboBox m_lhsField = new EnvironmentField.WideComboBox();
/*  46: 60 */   protected JComboBox<String> m_operatorCombo = new JComboBox();
/*  47: 63 */   protected JComboBox m_rhsField = new EnvironmentField.WideComboBox();
/*  48: 66 */   protected JCheckBox m_rhsIsAttribute = new JCheckBox("RHS is attribute");
/*  49: 69 */   protected JLabel m_expressionLab = new JLabel();
/*  50: 75 */   protected JComboBox<String> m_trueData = new JComboBox();
/*  51: 81 */   protected JComboBox<String> m_falseData = new JComboBox();
/*  52:    */   protected JTree m_expressionTree;
/*  53:    */   protected DefaultMutableTreeNode m_treeRoot;
/*  54: 90 */   protected JButton m_addExpressionNode = new JButton("Add Expression node");
/*  55: 93 */   protected JButton m_addBracketNode = new JButton("Add bracket node");
/*  56: 96 */   protected JButton m_toggleNegation = new JButton("Toggle negation");
/*  57: 99 */   protected JButton m_andOr = new JButton("And/Or");
/*  58:102 */   protected JButton m_deleteNode = new JButton("Delete node");
/*  59:    */   
/*  60:    */   protected void layoutEditor()
/*  61:    */   {
/*  62:111 */     JPanel outerP = new JPanel(new BorderLayout());
/*  63:112 */     JPanel controlHolder = new JPanel();
/*  64:113 */     controlHolder.setLayout(new BorderLayout());
/*  65:114 */     setupTree(outerP);
/*  66:    */     
/*  67:116 */     JPanel fieldHolder = new JPanel();
/*  68:117 */     fieldHolder.setLayout(new GridLayout(0, 4));
/*  69:    */     
/*  70:119 */     JPanel lhsP = new JPanel();
/*  71:120 */     lhsP.setLayout(new BorderLayout());
/*  72:121 */     lhsP.setBorder(BorderFactory.createTitledBorder("Attribute"));
/*  73:    */     
/*  74:    */ 
/*  75:124 */     this.m_lhsField.setEditable(true);
/*  76:    */     
/*  77:126 */     lhsP.add(this.m_lhsField, "Center");
/*  78:127 */     lhsP.setToolTipText("<html>Name or index of attribute. also accepts<br>the special labels \"/first\" and \"/last\" to indicate<br>the first and last attribute respecitively</html>");
/*  79:    */     
/*  80:    */ 
/*  81:130 */     this.m_lhsField.setToolTipText("<html>Name or index of attribute. also accepts<br>the special labels \"/first\" and \"/last\" to indicate<br>the first and last attribute respecitively</html>");
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:134 */     JPanel operatorP = new JPanel();
/*  86:135 */     operatorP.setLayout(new BorderLayout());
/*  87:136 */     operatorP.setBorder(BorderFactory.createTitledBorder("Operator"));
/*  88:137 */     this.m_operatorCombo.addItem(" = ");
/*  89:138 */     this.m_operatorCombo.addItem(" != ");
/*  90:139 */     this.m_operatorCombo.addItem(" < ");
/*  91:140 */     this.m_operatorCombo.addItem(" <= ");
/*  92:141 */     this.m_operatorCombo.addItem(" > ");
/*  93:142 */     this.m_operatorCombo.addItem(" >= ");
/*  94:143 */     this.m_operatorCombo.addItem(" isMissing ");
/*  95:144 */     this.m_operatorCombo.addItem(" contains ");
/*  96:145 */     this.m_operatorCombo.addItem(" startsWith ");
/*  97:146 */     this.m_operatorCombo.addItem(" endsWith ");
/*  98:147 */     this.m_operatorCombo.addItem(" regex ");
/*  99:148 */     operatorP.add(this.m_operatorCombo, "Center");
/* 100:    */     
/* 101:150 */     JPanel rhsP = new JPanel();
/* 102:151 */     rhsP.setLayout(new BorderLayout());
/* 103:152 */     rhsP.setBorder(BorderFactory.createTitledBorder("Constant or attribute"));
/* 104:153 */     rhsP.setToolTipText("<html>Constant value to test/check for. If testing<br>against an attribute, then this specifiesan attribute index or name</html>");
/* 105:    */     
/* 106:    */ 
/* 107:    */ 
/* 108:157 */     this.m_rhsField.setEditable(true);
/* 109:158 */     rhsP.add(this.m_rhsField, "Center");
/* 110:    */     
/* 111:160 */     fieldHolder.add(lhsP);
/* 112:161 */     fieldHolder.add(operatorP);
/* 113:162 */     fieldHolder.add(rhsP);
/* 114:163 */     fieldHolder.add(this.m_rhsIsAttribute);
/* 115:164 */     controlHolder.add(fieldHolder, "South");
/* 116:    */     
/* 117:166 */     JPanel tempPanel = new JPanel();
/* 118:167 */     tempPanel.setLayout(new BorderLayout());
/* 119:168 */     JPanel expressionP = new JPanel();
/* 120:169 */     expressionP.setLayout(new BorderLayout());
/* 121:170 */     expressionP.setBorder(BorderFactory.createTitledBorder("Expression"));
/* 122:171 */     JPanel tempE = new JPanel();
/* 123:172 */     tempE.setLayout(new BorderLayout());
/* 124:173 */     tempE.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
/* 125:174 */     tempE.add(this.m_expressionLab, "Center");
/* 126:175 */     JScrollPane expressionScroller = new JScrollPane(tempE);
/* 127:176 */     expressionP.add(expressionScroller, "Center");
/* 128:177 */     tempPanel.add(expressionP, "South");
/* 129:    */     
/* 130:    */ 
/* 131:180 */     JPanel flowControlP = new JPanel();
/* 132:181 */     flowControlP.setLayout(new GridLayout(2, 2));
/* 133:182 */     flowControlP.add(new JLabel("Send true instances to node", 4));
/* 134:    */     
/* 135:184 */     flowControlP.add(this.m_trueData);
/* 136:185 */     flowControlP.add(new JLabel("Send false instances to node", 4));
/* 137:    */     
/* 138:187 */     flowControlP.add(this.m_falseData);
/* 139:188 */     tempPanel.add(flowControlP, "North");
/* 140:189 */     String falseStepN = ((FlowByExpression)getStepToEdit()).getFalseStepName();
/* 141:190 */     String trueStepN = ((FlowByExpression)getStepToEdit()).getTrueStepName();
/* 142:    */     
/* 143:192 */     List<String> connectedSteps = ((FlowByExpression)getStepToEdit()).getDownstreamStepNames();
/* 144:    */     
/* 145:194 */     this.m_trueData.addItem("<none>");
/* 146:195 */     this.m_falseData.addItem("<none>");
/* 147:196 */     for (String s : connectedSteps)
/* 148:    */     {
/* 149:197 */       this.m_trueData.addItem(s);
/* 150:198 */       this.m_falseData.addItem(s);
/* 151:    */     }
/* 152:201 */     if ((falseStepN != null) && (falseStepN.length() > 0)) {
/* 153:202 */       this.m_falseData.setSelectedItem(falseStepN);
/* 154:    */     }
/* 155:204 */     if ((trueStepN != null) && (trueStepN.length() > 0)) {
/* 156:205 */       this.m_trueData.setSelectedItem(trueStepN);
/* 157:    */     }
/* 158:208 */     controlHolder.add(tempPanel, "North");
/* 159:209 */     outerP.add(controlHolder, "North");
/* 160:210 */     add(outerP, "Center");
/* 161:    */     
/* 162:212 */     this.m_rhsIsAttribute.addActionListener(new ActionListener()
/* 163:    */     {
/* 164:    */       public void actionPerformed(ActionEvent e)
/* 165:    */       {
/* 166:215 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 167:    */         {
/* 168:216 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 169:217 */           if ((p != null) && 
/* 170:218 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 171:    */           {
/* 172:220 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 173:    */             
/* 174:222 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 175:225 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 176:    */             {
/* 177:226 */               ((FlowByExpression.ExpressionClause)thisNode).setRHSIsAnAttribute(FlowByExpressionStepEditorDialog.this.m_rhsIsAttribute.isSelected());
/* 178:    */               
/* 179:    */ 
/* 180:229 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 181:    */               
/* 182:231 */               tmodel.nodeStructureChanged(tNode);
/* 183:    */               
/* 184:233 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 185:    */             }
/* 186:    */           }
/* 187:    */         }
/* 188:    */       }
/* 189:240 */     });
/* 190:241 */     this.m_operatorCombo.addActionListener(new ActionListener()
/* 191:    */     {
/* 192:    */       public void actionPerformed(ActionEvent e)
/* 193:    */       {
/* 194:245 */         if (FlowByExpressionStepEditorDialog.this.m_operatorCombo.getSelectedIndex() > 5)
/* 195:    */         {
/* 196:246 */           FlowByExpressionStepEditorDialog.this.m_rhsIsAttribute.setSelected(false);
/* 197:247 */           FlowByExpressionStepEditorDialog.this.m_rhsIsAttribute.setEnabled(false);
/* 198:    */         }
/* 199:    */         else
/* 200:    */         {
/* 201:249 */           FlowByExpressionStepEditorDialog.this.m_rhsIsAttribute.setEnabled(true);
/* 202:    */         }
/* 203:252 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 204:    */         {
/* 205:253 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 206:254 */           if ((p != null) && 
/* 207:255 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 208:    */           {
/* 209:257 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 210:    */             
/* 211:259 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 212:262 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 213:    */             {
/* 214:263 */               String selection = FlowByExpressionStepEditorDialog.this.m_operatorCombo.getSelectedItem().toString();
/* 215:264 */               FlowByExpression.ExpressionClause.ExpressionType t = FlowByExpression.ExpressionClause.ExpressionType.EQUALS;
/* 216:266 */               for (FlowByExpression.ExpressionClause.ExpressionType et : FlowByExpression.ExpressionClause.ExpressionType.values()) {
/* 217:268 */                 if (et.toString().equals(selection))
/* 218:    */                 {
/* 219:269 */                   t = et;
/* 220:270 */                   break;
/* 221:    */                 }
/* 222:    */               }
/* 223:274 */               ((FlowByExpression.ExpressionClause)thisNode).setOperator(t);
/* 224:275 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 225:    */               
/* 226:277 */               tmodel.nodeStructureChanged(tNode);
/* 227:    */               
/* 228:279 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 229:    */             }
/* 230:    */           }
/* 231:    */         }
/* 232:    */       }
/* 233:286 */     });
/* 234:287 */     this.m_lhsField.addActionListener(new ActionListener()
/* 235:    */     {
/* 236:    */       public void actionPerformed(ActionEvent e)
/* 237:    */       {
/* 238:290 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 239:    */         {
/* 240:291 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 241:292 */           if ((p != null) && 
/* 242:293 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 243:    */           {
/* 244:295 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 245:    */             
/* 246:297 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 247:300 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 248:    */             {
/* 249:301 */               Object text = FlowByExpressionStepEditorDialog.this.m_lhsField.getSelectedItem();
/* 250:302 */               if (text != null)
/* 251:    */               {
/* 252:303 */                 ((FlowByExpression.ExpressionClause)thisNode).setLHSAttName(text.toString());
/* 253:    */                 
/* 254:305 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 255:    */                 
/* 256:307 */                 tmodel.nodeStructureChanged(tNode);
/* 257:    */                 
/* 258:309 */                 FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 259:    */               }
/* 260:    */             }
/* 261:    */           }
/* 262:    */         }
/* 263:    */       }
/* 264:317 */     });
/* 265:318 */     this.m_lhsField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 266:    */     {
/* 267:    */       public void keyReleased(KeyEvent e)
/* 268:    */       {
/* 269:322 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 270:    */         {
/* 271:323 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 272:324 */           if ((p != null) && 
/* 273:325 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 274:    */           {
/* 275:327 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 276:    */             
/* 277:329 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 278:332 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 279:    */             {
/* 280:333 */               String text = "";
/* 281:334 */               if (FlowByExpressionStepEditorDialog.this.m_lhsField.getSelectedItem() != null) {
/* 282:335 */                 text = FlowByExpressionStepEditorDialog.this.m_lhsField.getSelectedItem().toString();
/* 283:    */               }
/* 284:337 */               Component theEditor = FlowByExpressionStepEditorDialog.this.m_lhsField.getEditor().getEditorComponent();
/* 285:339 */               if ((theEditor instanceof JTextField)) {
/* 286:340 */                 text = ((JTextField)theEditor).getText();
/* 287:    */               }
/* 288:342 */               ((FlowByExpression.ExpressionClause)thisNode).setLHSAttName(text);
/* 289:    */               
/* 290:344 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 291:    */               
/* 292:346 */               tmodel.nodeStructureChanged(tNode);
/* 293:    */               
/* 294:348 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 295:    */             }
/* 296:    */           }
/* 297:    */         }
/* 298:    */       }
/* 299:355 */     });
/* 300:356 */     this.m_rhsField.addActionListener(new ActionListener()
/* 301:    */     {
/* 302:    */       public void actionPerformed(ActionEvent e)
/* 303:    */       {
/* 304:359 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 305:    */         {
/* 306:360 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 307:361 */           if ((p != null) && 
/* 308:362 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 309:    */           {
/* 310:364 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 311:    */             
/* 312:366 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 313:369 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 314:    */             {
/* 315:370 */               Object text = FlowByExpressionStepEditorDialog.this.m_rhsField.getSelectedItem();
/* 316:371 */               if (text != null)
/* 317:    */               {
/* 318:372 */                 ((FlowByExpression.ExpressionClause)thisNode).setRHSOperand(text.toString());
/* 319:    */                 
/* 320:374 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 321:    */                 
/* 322:376 */                 tmodel.nodeStructureChanged(tNode);
/* 323:    */                 
/* 324:378 */                 FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 325:    */               }
/* 326:    */             }
/* 327:    */           }
/* 328:    */         }
/* 329:    */       }
/* 330:386 */     });
/* 331:387 */     this.m_rhsField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 332:    */     {
/* 333:    */       public void keyReleased(KeyEvent e)
/* 334:    */       {
/* 335:391 */         if (FlowByExpressionStepEditorDialog.this.m_expressionTree != null)
/* 336:    */         {
/* 337:392 */           TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 338:393 */           if ((p != null) && 
/* 339:394 */             ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 340:    */           {
/* 341:396 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 342:    */             
/* 343:398 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 344:401 */             if ((thisNode instanceof FlowByExpression.ExpressionClause))
/* 345:    */             {
/* 346:402 */               String text = "";
/* 347:403 */               if (FlowByExpressionStepEditorDialog.this.m_rhsField.getSelectedItem() != null) {
/* 348:404 */                 text = FlowByExpressionStepEditorDialog.this.m_rhsField.getSelectedItem().toString();
/* 349:    */               }
/* 350:406 */               Component theEditor = FlowByExpressionStepEditorDialog.this.m_rhsField.getEditor().getEditorComponent();
/* 351:408 */               if ((theEditor instanceof JTextField)) {
/* 352:409 */                 text = ((JTextField)theEditor).getText();
/* 353:    */               }
/* 354:412 */               if (FlowByExpressionStepEditorDialog.this.m_rhsField.getSelectedItem() != null)
/* 355:    */               {
/* 356:413 */                 ((FlowByExpression.ExpressionClause)thisNode).setRHSOperand(text);
/* 357:    */                 
/* 358:415 */                 DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 359:    */                 
/* 360:417 */                 tmodel.nodeStructureChanged(tNode);
/* 361:    */                 
/* 362:419 */                 FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 363:    */               }
/* 364:    */             }
/* 365:    */           }
/* 366:    */         }
/* 367:    */       }
/* 368:    */     });
/* 369:    */     try
/* 370:    */     {
/* 371:429 */       Instances incomingStructure = getStepToEdit().getStepManager().getIncomingStructureForConnectionType("instance");
/* 372:432 */       if (incomingStructure == null) {
/* 373:433 */         incomingStructure = getStepToEdit().getStepManager().getIncomingStructureForConnectionType("dataSet");
/* 374:    */       }
/* 375:437 */       if (incomingStructure == null) {
/* 376:438 */         incomingStructure = getStepToEdit().getStepManager().getIncomingStructureForConnectionType("trainingSet");
/* 377:    */       }
/* 378:442 */       if (incomingStructure == null) {
/* 379:443 */         incomingStructure = getStepToEdit().getStepManager().getIncomingStructureForConnectionType("testSet");
/* 380:    */       }
/* 381:447 */       if (incomingStructure != null)
/* 382:    */       {
/* 383:448 */         this.m_lhsField.removeAllItems();
/* 384:449 */         this.m_rhsField.removeAllItems();
/* 385:450 */         for (int i = 0; i < incomingStructure.numAttributes(); i++)
/* 386:    */         {
/* 387:451 */           this.m_lhsField.addItem(incomingStructure.attribute(i).name());
/* 388:452 */           this.m_rhsField.addItem(incomingStructure.attribute(i).name());
/* 389:    */         }
/* 390:    */       }
/* 391:    */     }
/* 392:    */     catch (WekaException ex)
/* 393:    */     {
/* 394:456 */       showErrorDialog(ex);
/* 395:    */     }
/* 396:    */   }
/* 397:    */   
/* 398:    */   private void setExpressionEditor(FlowByExpression.ExpressionClause node)
/* 399:    */   {
/* 400:461 */     String lhs = node.getLHSAttName();
/* 401:462 */     if (lhs != null) {
/* 402:463 */       this.m_lhsField.setSelectedItem(lhs);
/* 403:    */     }
/* 404:465 */     String rhs = node.getRHSOperand();
/* 405:466 */     if (rhs != null) {
/* 406:467 */       this.m_rhsField.setSelectedItem(rhs);
/* 407:    */     }
/* 408:469 */     FlowByExpression.ExpressionClause.ExpressionType opp = node.getOperator();
/* 409:470 */     int oppIndex = opp.ordinal();
/* 410:471 */     this.m_operatorCombo.setSelectedIndex(oppIndex);
/* 411:472 */     this.m_rhsIsAttribute.setSelected(node.isRHSAnAttribute());
/* 412:    */   }
/* 413:    */   
/* 414:    */   private void updateExpressionLabel()
/* 415:    */   {
/* 416:476 */     StringBuffer buff = new StringBuffer();
/* 417:    */     
/* 418:478 */     FlowByExpression.ExpressionNode root = (FlowByExpression.ExpressionNode)this.m_treeRoot.getUserObject();
/* 419:    */     
/* 420:480 */     root.toStringDisplay(buff);
/* 421:481 */     this.m_expressionLab.setText(buff.toString());
/* 422:    */   }
/* 423:    */   
/* 424:    */   private void setupTree(JPanel holder)
/* 425:    */   {
/* 426:485 */     JPanel treeHolder = new JPanel();
/* 427:486 */     treeHolder.setLayout(new BorderLayout());
/* 428:487 */     treeHolder.setBorder(BorderFactory.createTitledBorder("Expression tree"));
/* 429:488 */     String expressionString = ((FlowByExpression)getStepToEdit()).getExpressionString();
/* 430:490 */     if ((expressionString == null) || (expressionString.length() == 0)) {
/* 431:491 */       expressionString = "()";
/* 432:    */     }
/* 433:493 */     FlowByExpression.BracketNode root = new FlowByExpression.BracketNode();
/* 434:494 */     root.parseFromInternal(expressionString);
/* 435:495 */     root.setShowAndOr(false);
/* 436:    */     
/* 437:497 */     this.m_treeRoot = root.toJTree(null);
/* 438:    */     
/* 439:499 */     DefaultTreeModel model = new DefaultTreeModel(this.m_treeRoot);
/* 440:500 */     this.m_expressionTree = new JTree(model);
/* 441:501 */     this.m_expressionTree.setEnabled(true);
/* 442:502 */     this.m_expressionTree.setRootVisible(true);
/* 443:503 */     this.m_expressionTree.setShowsRootHandles(true);
/* 444:504 */     DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/* 445:505 */     selectionModel.setSelectionMode(1);
/* 446:506 */     this.m_expressionTree.setSelectionModel(selectionModel);
/* 447:    */     
/* 448:    */ 
/* 449:509 */     this.m_expressionTree.addMouseListener(new MouseAdapter()
/* 450:    */     {
/* 451:    */       public void mouseClicked(MouseEvent e)
/* 452:    */       {
/* 453:512 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 454:513 */         if ((p != null) && 
/* 455:514 */           ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/* 456:    */         {
/* 457:515 */           DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 458:    */           
/* 459:    */ 
/* 460:518 */           FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 461:521 */           if ((thisNode instanceof FlowByExpression.ExpressionClause)) {
/* 462:522 */             FlowByExpressionStepEditorDialog.this.setExpressionEditor((FlowByExpression.ExpressionClause)thisNode);
/* 463:    */           }
/* 464:    */         }
/* 465:    */       }
/* 466:528 */     });
/* 467:529 */     updateExpressionLabel();
/* 468:    */     
/* 469:531 */     JScrollPane treeView = new JScrollPane(this.m_expressionTree);
/* 470:532 */     treeHolder.add(treeView, "Center");
/* 471:    */     
/* 472:534 */     JPanel butHolder = new JPanel();
/* 473:    */     
/* 474:536 */     butHolder.add(this.m_addExpressionNode);
/* 475:537 */     butHolder.add(this.m_addBracketNode);
/* 476:538 */     butHolder.add(this.m_toggleNegation);
/* 477:539 */     butHolder.add(this.m_andOr);
/* 478:540 */     butHolder.add(this.m_deleteNode);
/* 479:541 */     treeHolder.add(butHolder, "North");
/* 480:    */     
/* 481:543 */     holder.add(treeHolder, "Center");
/* 482:544 */     Dimension d = treeHolder.getPreferredSize();
/* 483:545 */     treeHolder.setPreferredSize(new Dimension(d.width, d.height / 2));
/* 484:    */     
/* 485:547 */     this.m_andOr.addActionListener(new ActionListener()
/* 486:    */     {
/* 487:    */       public void actionPerformed(ActionEvent e)
/* 488:    */       {
/* 489:550 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 490:551 */         if (p != null)
/* 491:    */         {
/* 492:552 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 493:    */           {
/* 494:554 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 495:    */             
/* 496:556 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 497:    */             
/* 498:    */ 
/* 499:559 */             thisNode.setIsOr(!thisNode.isOr());
/* 500:560 */             DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 501:    */             
/* 502:562 */             tmodel.nodeStructureChanged(tNode);
/* 503:    */             
/* 504:564 */             FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 505:    */           }
/* 506:    */         }
/* 507:    */         else {
/* 508:568 */           FlowByExpressionStepEditorDialog.this.showInfoDialog("Please select a node in the tree to alter the boolean operator of", "And/Or", false);
/* 509:    */         }
/* 510:    */       }
/* 511:574 */     });
/* 512:575 */     this.m_toggleNegation.addActionListener(new ActionListener()
/* 513:    */     {
/* 514:    */       public void actionPerformed(ActionEvent e)
/* 515:    */       {
/* 516:578 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 517:579 */         if (p != null)
/* 518:    */         {
/* 519:580 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 520:    */           {
/* 521:582 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 522:    */             
/* 523:584 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 524:    */             
/* 525:    */ 
/* 526:587 */             thisNode.setNegated(!thisNode.isNegated());
/* 527:588 */             DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 528:    */             
/* 529:590 */             tmodel.nodeStructureChanged(tNode);
/* 530:    */             
/* 531:592 */             FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 532:    */           }
/* 533:    */         }
/* 534:    */         else {
/* 535:595 */           FlowByExpressionStepEditorDialog.this.showInfoDialog("Please select a node in the tree to toggle its negation", "Toggle negation", false);
/* 536:    */         }
/* 537:    */       }
/* 538:601 */     });
/* 539:602 */     this.m_deleteNode.addActionListener(new ActionListener()
/* 540:    */     {
/* 541:    */       public void actionPerformed(ActionEvent e)
/* 542:    */       {
/* 543:605 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 544:606 */         if (p != null)
/* 545:    */         {
/* 546:607 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 547:    */           {
/* 548:608 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 549:611 */             if (tNode == FlowByExpressionStepEditorDialog.this.m_treeRoot)
/* 550:    */             {
/* 551:612 */               FlowByExpressionStepEditorDialog.this.showInfoDialog("You can't delete the root of the tree!", "Delete node", true);
/* 552:    */             }
/* 553:    */             else
/* 554:    */             {
/* 555:615 */               FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 556:    */               
/* 557:    */ 
/* 558:618 */               FlowByExpression.BracketNode parentNode = (FlowByExpression.BracketNode)((DefaultMutableTreeNode)tNode.getParent()).getUserObject();
/* 559:    */               
/* 560:    */ 
/* 561:    */ 
/* 562:    */ 
/* 563:623 */               parentNode.removeChild(thisNode);
/* 564:    */               
/* 565:    */ 
/* 566:626 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 567:    */               
/* 568:628 */               tmodel.removeNodeFromParent(tNode);
/* 569:629 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 570:    */             }
/* 571:    */           }
/* 572:    */         }
/* 573:    */         else {
/* 574:633 */           FlowByExpressionStepEditorDialog.this.showInfoDialog("Please select a node in the tree to delete.", "Delete node", false);
/* 575:    */         }
/* 576:    */       }
/* 577:638 */     });
/* 578:639 */     this.m_addExpressionNode.addActionListener(new ActionListener()
/* 579:    */     {
/* 580:    */       public void actionPerformed(ActionEvent e)
/* 581:    */       {
/* 582:642 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 583:643 */         if (p != null)
/* 584:    */         {
/* 585:644 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 586:    */           {
/* 587:646 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 588:    */             
/* 589:648 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 590:651 */             if ((thisNode instanceof FlowByExpression.BracketNode))
/* 591:    */             {
/* 592:652 */               FlowByExpression.ExpressionClause newNode = new FlowByExpression.ExpressionClause(FlowByExpression.ExpressionClause.ExpressionType.EQUALS, "<att name>", "<value>", false, false);
/* 593:    */               
/* 594:    */ 
/* 595:    */ 
/* 596:    */ 
/* 597:657 */               ((FlowByExpression.BracketNode)thisNode).addChild(newNode);
/* 598:658 */               DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newNode);
/* 599:    */               
/* 600:    */ 
/* 601:661 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 602:    */               
/* 603:663 */               tNode.add(childNode);
/* 604:664 */               tmodel.nodeStructureChanged(tNode);
/* 605:665 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 606:    */             }
/* 607:    */             else
/* 608:    */             {
/* 609:667 */               FlowByExpressionStepEditorDialog.this.showInfoDialog("An expression can only be added to a bracket node.", "Add expression", false);
/* 610:    */             }
/* 611:    */           }
/* 612:    */         }
/* 613:    */         else {
/* 614:673 */           FlowByExpressionStepEditorDialog.this.showInfoDialog("You must select a bracket node in the tree view to add a new expression to.", "Add expression", false);
/* 615:    */         }
/* 616:    */       }
/* 617:679 */     });
/* 618:680 */     this.m_addBracketNode.addActionListener(new ActionListener()
/* 619:    */     {
/* 620:    */       public void actionPerformed(ActionEvent e)
/* 621:    */       {
/* 622:683 */         TreePath p = FlowByExpressionStepEditorDialog.this.m_expressionTree.getSelectionPath();
/* 623:684 */         if (p != null)
/* 624:    */         {
/* 625:685 */           if ((p.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 626:    */           {
/* 627:687 */             DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/* 628:    */             
/* 629:689 */             FlowByExpression.ExpressionNode thisNode = (FlowByExpression.ExpressionNode)tNode.getUserObject();
/* 630:692 */             if ((thisNode instanceof FlowByExpression.BracketNode))
/* 631:    */             {
/* 632:693 */               FlowByExpression.BracketNode newNode = new FlowByExpression.BracketNode();
/* 633:    */               
/* 634:695 */               ((FlowByExpression.BracketNode)thisNode).addChild(newNode);
/* 635:696 */               DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(newNode);
/* 636:    */               
/* 637:    */ 
/* 638:699 */               DefaultTreeModel tmodel = (DefaultTreeModel)FlowByExpressionStepEditorDialog.this.m_expressionTree.getModel();
/* 639:    */               
/* 640:701 */               tNode.add(childNode);
/* 641:702 */               tmodel.nodeStructureChanged(tNode);
/* 642:703 */               FlowByExpressionStepEditorDialog.this.updateExpressionLabel();
/* 643:    */             }
/* 644:    */             else
/* 645:    */             {
/* 646:705 */               FlowByExpressionStepEditorDialog.this.showInfoDialog("An new bracket node can only be added to an existing bracket node.", "Add bracket", false);
/* 647:    */             }
/* 648:    */           }
/* 649:    */         }
/* 650:    */         else {
/* 651:711 */           FlowByExpressionStepEditorDialog.this.showInfoDialog("You must select an existing bracket node in the tree in order to add a new bracket node.", "Add bracket", false);
/* 652:    */         }
/* 653:    */       }
/* 654:    */     });
/* 655:    */   }
/* 656:    */   
/* 657:    */   protected void okPressed()
/* 658:    */   {
/* 659:724 */     if (this.m_treeRoot != null)
/* 660:    */     {
/* 661:725 */       FlowByExpression.ExpressionNode en = (FlowByExpression.ExpressionNode)this.m_treeRoot.getUserObject();
/* 662:    */       
/* 663:727 */       StringBuffer buff = new StringBuffer();
/* 664:728 */       en.toStringInternal(buff);
/* 665:    */       
/* 666:730 */       ((FlowByExpression)getStepToEdit()).setExpressionString(buff.toString());
/* 667:732 */       if ((this.m_trueData.getSelectedItem() != null) && (this.m_trueData.getSelectedItem().toString().length() > 0)) {
/* 668:734 */         ((FlowByExpression)getStepToEdit()).setTrueStepName(this.m_trueData.getSelectedItem().toString());
/* 669:    */       }
/* 670:738 */       if ((this.m_falseData.getSelectedItem() != null) && (this.m_falseData.getSelectedItem().toString().length() > 0)) {
/* 671:740 */         ((FlowByExpression)getStepToEdit()).setFalseStepName(this.m_falseData.getSelectedItem().toString());
/* 672:    */       }
/* 673:    */     }
/* 674:    */   }
/* 675:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.FlowByExpressionStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */