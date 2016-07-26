/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.FontMetrics;
/*   8:    */ import java.awt.Frame;
/*   9:    */ import java.awt.Graphics;
/*  10:    */ import java.awt.GridLayout;
/*  11:    */ import java.awt.Insets;
/*  12:    */ import java.awt.Point;
/*  13:    */ import java.awt.Rectangle;
/*  14:    */ import java.awt.event.ActionEvent;
/*  15:    */ import java.awt.event.ActionListener;
/*  16:    */ import java.awt.event.MouseAdapter;
/*  17:    */ import java.awt.event.MouseEvent;
/*  18:    */ import java.awt.event.MouseListener;
/*  19:    */ import java.awt.event.WindowAdapter;
/*  20:    */ import java.awt.event.WindowEvent;
/*  21:    */ import java.beans.PropertyChangeEvent;
/*  22:    */ import java.beans.PropertyChangeListener;
/*  23:    */ import java.beans.PropertyChangeSupport;
/*  24:    */ import java.beans.PropertyEditor;
/*  25:    */ import java.beans.PropertyEditorManager;
/*  26:    */ import java.io.File;
/*  27:    */ import java.io.PrintStream;
/*  28:    */ import java.lang.reflect.Array;
/*  29:    */ import javax.swing.DefaultListCellRenderer;
/*  30:    */ import javax.swing.DefaultListModel;
/*  31:    */ import javax.swing.JButton;
/*  32:    */ import javax.swing.JLabel;
/*  33:    */ import javax.swing.JList;
/*  34:    */ import javax.swing.JOptionPane;
/*  35:    */ import javax.swing.JPanel;
/*  36:    */ import javax.swing.JScrollPane;
/*  37:    */ import javax.swing.ListCellRenderer;
/*  38:    */ import javax.swing.event.ListSelectionEvent;
/*  39:    */ import javax.swing.event.ListSelectionListener;
/*  40:    */ import weka.core.SerializedObject;
/*  41:    */ import weka.filters.Filter;
/*  42:    */ 
/*  43:    */ public class GenericArrayEditor
/*  44:    */   implements PropertyEditor
/*  45:    */ {
/*  46:    */   private final CustomEditor m_customEditor;
/*  47:    */   
/*  48:    */   private class CustomEditor
/*  49:    */     extends JPanel
/*  50:    */   {
/*  51:    */     private static final long serialVersionUID = 3914616975334750480L;
/*  52: 81 */     private final PropertyChangeSupport m_Support = new PropertyChangeSupport(GenericArrayEditor.this);
/*  53: 85 */     private final JLabel m_Label = new JLabel("Can't edit", 0);
/*  54: 89 */     private final JList m_ElementList = new JList();
/*  55: 92 */     private Class<?> m_ElementClass = String.class;
/*  56:    */     private DefaultListModel m_ListModel;
/*  57:    */     private PropertyEditor m_ElementEditor;
/*  58:101 */     private final JButton m_DeleteBut = new JButton("Delete");
/*  59:104 */     private final JButton m_EditBut = new JButton("Edit");
/*  60:107 */     private final JButton m_UpBut = new JButton("Up");
/*  61:110 */     private final JButton m_DownBut = new JButton("Down");
/*  62:113 */     private final JButton m_AddBut = new JButton("Add");
/*  63:116 */     private PropertyEditor m_Editor = new GenericObjectEditor();
/*  64:    */     private PropertyDialog m_PD;
/*  65:122 */     private final ActionListener m_InnerActionListener = new ActionListener()
/*  66:    */     {
/*  67:    */       public void actionPerformed(ActionEvent e)
/*  68:    */       {
/*  69:127 */         if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_DeleteBut)
/*  70:    */         {
/*  71:128 */           int[] selected = GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedIndices();
/*  72:129 */           if (selected != null)
/*  73:    */           {
/*  74:130 */             for (int i = selected.length - 1; i >= 0; i--)
/*  75:    */             {
/*  76:131 */               int current = selected[i];
/*  77:132 */               GenericArrayEditor.CustomEditor.this.m_ListModel.removeElementAt(current);
/*  78:133 */               if (GenericArrayEditor.CustomEditor.this.m_ListModel.size() > current) {
/*  79:134 */                 GenericArrayEditor.CustomEditor.this.m_ElementList.setSelectedIndex(current);
/*  80:    */               }
/*  81:    */             }
/*  82:137 */             GenericArrayEditor.CustomEditor.this.m_Support.firePropertyChange("", null, null);
/*  83:    */           }
/*  84:    */         }
/*  85:139 */         else if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_EditBut)
/*  86:    */         {
/*  87:140 */           if ((GenericArrayEditor.CustomEditor.this.m_Editor instanceof GenericObjectEditor)) {
/*  88:141 */             ((GenericObjectEditor)GenericArrayEditor.CustomEditor.this.m_Editor).setClassType(GenericArrayEditor.CustomEditor.this.m_ElementClass);
/*  89:    */           }
/*  90:    */           try
/*  91:    */           {
/*  92:144 */             GenericArrayEditor.CustomEditor.this.m_Editor.setValue(GenericObjectEditor.makeCopy(GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedValue()));
/*  93:    */           }
/*  94:    */           catch (Exception ex)
/*  95:    */           {
/*  96:148 */             GenericArrayEditor.CustomEditor.this.m_Editor.setValue(GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedValue());
/*  97:    */           }
/*  98:150 */           if (GenericArrayEditor.CustomEditor.this.m_Editor.getValue() != null)
/*  99:    */           {
/* 100:151 */             int x = GenericArrayEditor.CustomEditor.this.getLocationOnScreen().x;
/* 101:152 */             int y = GenericArrayEditor.CustomEditor.this.getLocationOnScreen().y;
/* 102:153 */             if (PropertyDialog.getParentDialog(GenericArrayEditor.CustomEditor.this) != null) {
/* 103:154 */               GenericArrayEditor.CustomEditor.this.m_PD = new PropertyDialog(PropertyDialog.getParentDialog(GenericArrayEditor.CustomEditor.this), GenericArrayEditor.CustomEditor.this.m_Editor, x, y);
/* 104:    */             } else {
/* 105:158 */               GenericArrayEditor.CustomEditor.this.m_PD = new PropertyDialog(PropertyDialog.getParentFrame(GenericArrayEditor.CustomEditor.this), GenericArrayEditor.CustomEditor.this.m_Editor, x, y);
/* 106:    */             }
/* 107:162 */             GenericArrayEditor.CustomEditor.this.m_PD.setVisible(true);
/* 108:163 */             GenericArrayEditor.CustomEditor.this.m_ListModel.set(GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedIndex(), GenericArrayEditor.CustomEditor.this.m_Editor.getValue());
/* 109:    */             
/* 110:165 */             GenericArrayEditor.CustomEditor.this.m_Support.firePropertyChange("", null, null);
/* 111:    */           }
/* 112:    */         }
/* 113:167 */         else if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_UpBut)
/* 114:    */         {
/* 115:168 */           JListHelper.moveUp(GenericArrayEditor.CustomEditor.this.m_ElementList);
/* 116:169 */           GenericArrayEditor.CustomEditor.this.m_Support.firePropertyChange("", null, null);
/* 117:    */         }
/* 118:170 */         else if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_DownBut)
/* 119:    */         {
/* 120:171 */           JListHelper.moveDown(GenericArrayEditor.CustomEditor.this.m_ElementList);
/* 121:172 */           GenericArrayEditor.CustomEditor.this.m_Support.firePropertyChange("", null, null);
/* 122:    */         }
/* 123:173 */         else if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_AddBut)
/* 124:    */         {
/* 125:174 */           int selected = GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedIndex();
/* 126:175 */           Object addObj = GenericArrayEditor.CustomEditor.this.m_ElementEditor.getValue();
/* 127:    */           try
/* 128:    */           {
/* 129:179 */             SerializedObject so = new SerializedObject(addObj);
/* 130:180 */             addObj = so.getObject();
/* 131:181 */             if (selected != -1) {
/* 132:182 */               GenericArrayEditor.CustomEditor.this.m_ListModel.insertElementAt(addObj, selected);
/* 133:    */             } else {
/* 134:184 */               GenericArrayEditor.CustomEditor.this.m_ListModel.addElement(addObj);
/* 135:    */             }
/* 136:186 */             GenericArrayEditor.CustomEditor.this.m_Support.firePropertyChange("", null, null);
/* 137:    */           }
/* 138:    */           catch (Exception ex)
/* 139:    */           {
/* 140:188 */             JOptionPane.showMessageDialog(GenericArrayEditor.CustomEditor.this, "Could not create an object copy", null, 0);
/* 141:    */           }
/* 142:    */         }
/* 143:    */       }
/* 144:    */     };
/* 145:197 */     private final ListSelectionListener m_InnerSelectionListener = new ListSelectionListener()
/* 146:    */     {
/* 147:    */       public void valueChanged(ListSelectionEvent e)
/* 148:    */       {
/* 149:202 */         if (e.getSource() == GenericArrayEditor.CustomEditor.this.m_ElementList) {
/* 150:204 */           if (GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedIndex() != -1)
/* 151:    */           {
/* 152:205 */             GenericArrayEditor.CustomEditor.this.m_DeleteBut.setEnabled(true);
/* 153:206 */             GenericArrayEditor.CustomEditor.this.m_EditBut.setEnabled(GenericArrayEditor.CustomEditor.this.m_ElementList.getSelectedIndices().length == 1);
/* 154:    */             
/* 155:208 */             GenericArrayEditor.CustomEditor.this.m_UpBut.setEnabled(JListHelper.canMoveUp(GenericArrayEditor.CustomEditor.this.m_ElementList));
/* 156:209 */             GenericArrayEditor.CustomEditor.this.m_DownBut.setEnabled(JListHelper.canMoveDown(GenericArrayEditor.CustomEditor.this.m_ElementList));
/* 157:    */           }
/* 158:    */           else
/* 159:    */           {
/* 160:213 */             GenericArrayEditor.CustomEditor.this.m_DeleteBut.setEnabled(false);
/* 161:214 */             GenericArrayEditor.CustomEditor.this.m_EditBut.setEnabled(false);
/* 162:215 */             GenericArrayEditor.CustomEditor.this.m_UpBut.setEnabled(false);
/* 163:216 */             GenericArrayEditor.CustomEditor.this.m_DownBut.setEnabled(false);
/* 164:    */           }
/* 165:    */         }
/* 166:    */       }
/* 167:    */     };
/* 168:223 */     private final MouseListener m_InnerMouseListener = new MouseAdapter()
/* 169:    */     {
/* 170:    */       public void mouseClicked(MouseEvent e)
/* 171:    */       {
/* 172:227 */         if ((e.getSource() == GenericArrayEditor.CustomEditor.this.m_ElementList) && 
/* 173:228 */           (e.getClickCount() == 2))
/* 174:    */         {
/* 175:233 */           int index = GenericArrayEditor.CustomEditor.this.m_ElementList.locationToIndex(e.getPoint());
/* 176:234 */           if (index > -1) {
/* 177:235 */             GenericArrayEditor.CustomEditor.this.m_InnerActionListener.actionPerformed(new ActionEvent(GenericArrayEditor.CustomEditor.this.m_EditBut, 0, ""));
/* 178:    */           }
/* 179:    */         }
/* 180:    */       }
/* 181:    */     };
/* 182:    */     
/* 183:    */     public CustomEditor()
/* 184:    */     {
/* 185:248 */       setLayout(new BorderLayout());
/* 186:249 */       add(this.m_Label, "Center");
/* 187:250 */       this.m_DeleteBut.addActionListener(this.m_InnerActionListener);
/* 188:251 */       this.m_EditBut.addActionListener(this.m_InnerActionListener);
/* 189:252 */       this.m_UpBut.addActionListener(this.m_InnerActionListener);
/* 190:253 */       this.m_DownBut.addActionListener(this.m_InnerActionListener);
/* 191:254 */       this.m_AddBut.addActionListener(this.m_InnerActionListener);
/* 192:255 */       this.m_ElementList.addListSelectionListener(this.m_InnerSelectionListener);
/* 193:256 */       this.m_ElementList.addMouseListener(this.m_InnerMouseListener);
/* 194:257 */       this.m_AddBut.setToolTipText("Add the current item to the list");
/* 195:258 */       this.m_DeleteBut.setToolTipText("Delete the selected list item");
/* 196:259 */       this.m_EditBut.setToolTipText("Edit the selected list item");
/* 197:260 */       this.m_UpBut.setToolTipText("Move the selected item(s) one up");
/* 198:261 */       this.m_DownBut.setToolTipText("Move the selected item(s) one down");
/* 199:    */     }
/* 200:    */     
/* 201:    */     private class EditorListCellRenderer
/* 202:    */       implements ListCellRenderer
/* 203:    */     {
/* 204:    */       private final Class<?> m_EditorClass;
/* 205:    */       private final Class<?> m_ValueClass;
/* 206:    */       
/* 207:    */       public EditorListCellRenderer(Class<?> editorClass)
/* 208:    */       {
/* 209:283 */         this.m_EditorClass = editorClass;
/* 210:284 */         this.m_ValueClass = valueClass;
/* 211:    */       }
/* 212:    */       
/* 213:    */       public Component getListCellRendererComponent(final JList list, Object value, int index, final boolean isSelected, boolean cellHasFocus)
/* 214:    */       {
/* 215:    */         try
/* 216:    */         {
/* 217:302 */           final PropertyEditor e = (PropertyEditor)this.m_EditorClass.newInstance();
/* 218:303 */           if ((e instanceof GenericObjectEditor)) {
/* 219:305 */             ((GenericObjectEditor)e).setClassType(this.m_ValueClass);
/* 220:    */           }
/* 221:307 */           e.setValue(value);
/* 222:308 */           new JPanel()
/* 223:    */           {
/* 224:    */             private static final long serialVersionUID = -3124434678426673334L;
/* 225:    */             
/* 226:    */             public void paintComponent(Graphics g)
/* 227:    */             {
/* 228:315 */               Insets i = getInsets();
/* 229:316 */               Rectangle box = new Rectangle(i.left, i.top, getWidth() - i.right, getHeight() - i.bottom);
/* 230:    */               
/* 231:318 */               g.setColor(isSelected ? list.getSelectionBackground() : list.getBackground());
/* 232:    */               
/* 233:320 */               g.fillRect(0, 0, getWidth(), getHeight());
/* 234:321 */               g.setColor(isSelected ? list.getSelectionForeground() : list.getForeground());
/* 235:    */               
/* 236:323 */               e.paintValue(g, box);
/* 237:    */             }
/* 238:    */             
/* 239:    */             public Dimension getPreferredSize()
/* 240:    */             {
/* 241:329 */               Font f = getFont();
/* 242:330 */               FontMetrics fm = getFontMetrics(f);
/* 243:331 */               return new Dimension(0, fm.getHeight());
/* 244:    */             }
/* 245:    */           };
/* 246:    */         }
/* 247:    */         catch (Exception ex) {}
/* 248:335 */         return null;
/* 249:    */       }
/* 250:    */     }
/* 251:    */     
/* 252:    */     private void updateEditorType(Object o)
/* 253:    */     {
/* 254:349 */       this.m_ElementEditor = null;
/* 255:350 */       this.m_ListModel = null;
/* 256:351 */       removeAll();
/* 257:352 */       if ((o != null) && (o.getClass().isArray()))
/* 258:    */       {
/* 259:353 */         Class<?> elementClass = o.getClass().getComponentType();
/* 260:354 */         PropertyEditor editor = PropertyEditorManager.findEditor(elementClass);
/* 261:355 */         Component view = null;
/* 262:356 */         ListCellRenderer lcr = new DefaultListCellRenderer();
/* 263:357 */         if (editor != null)
/* 264:    */         {
/* 265:358 */           if ((editor instanceof GenericObjectEditor)) {
/* 266:359 */             ((GenericObjectEditor)editor).setClassType(elementClass);
/* 267:    */           }
/* 268:366 */           if (Array.getLength(o) > 0) {
/* 269:367 */             editor.setValue(GenericArrayEditor.makeCopy(Array.get(o, 0)));
/* 270:369 */           } else if ((editor instanceof GenericObjectEditor)) {
/* 271:370 */             ((GenericObjectEditor)editor).setDefaultValue();
/* 272:    */           } else {
/* 273:    */             try
/* 274:    */             {
/* 275:373 */               if ((editor instanceof FileEditor)) {
/* 276:374 */                 editor.setValue(new File("-NONE-"));
/* 277:    */               } else {
/* 278:376 */                 editor.setValue(elementClass.newInstance());
/* 279:    */               }
/* 280:    */             }
/* 281:    */             catch (Exception ex)
/* 282:    */             {
/* 283:379 */               this.m_ElementEditor = null;
/* 284:380 */               System.err.println(ex.getMessage());
/* 285:381 */               add(this.m_Label, "Center");
/* 286:382 */               this.m_Support.firePropertyChange("", null, null);
/* 287:383 */               validate();
/* 288:384 */               return;
/* 289:    */             }
/* 290:    */           }
/* 291:389 */           if ((editor.isPaintable()) && (editor.supportsCustomEditor()))
/* 292:    */           {
/* 293:390 */             view = new PropertyPanel(editor);
/* 294:391 */             lcr = new EditorListCellRenderer(editor.getClass(), elementClass);
/* 295:    */           }
/* 296:392 */           else if (editor.getTags() != null)
/* 297:    */           {
/* 298:393 */             view = new PropertyValueSelector(editor);
/* 299:    */           }
/* 300:394 */           else if (editor.getAsText() != null)
/* 301:    */           {
/* 302:395 */             view = new PropertyText(editor);
/* 303:    */           }
/* 304:    */         }
/* 305:398 */         if (view == null)
/* 306:    */         {
/* 307:399 */           System.err.println("No property editor for class: " + elementClass.getName());
/* 308:    */         }
/* 309:    */         else
/* 310:    */         {
/* 311:402 */           this.m_ElementEditor = editor;
/* 312:    */           try
/* 313:    */           {
/* 314:404 */             this.m_Editor = ((PropertyEditor)editor.getClass().newInstance());
/* 315:    */           }
/* 316:    */           catch (InstantiationException e1)
/* 317:    */           {
/* 318:407 */             e1.printStackTrace();
/* 319:    */           }
/* 320:    */           catch (IllegalAccessException e1)
/* 321:    */           {
/* 322:410 */             e1.printStackTrace();
/* 323:    */           }
/* 324:414 */           this.m_ListModel = new DefaultListModel();
/* 325:415 */           this.m_ElementClass = elementClass;
/* 326:416 */           for (int i = 0; i < Array.getLength(o); i++) {
/* 327:417 */             this.m_ListModel.addElement(Array.get(o, i));
/* 328:    */           }
/* 329:419 */           this.m_ElementList.setCellRenderer(lcr);
/* 330:420 */           this.m_ElementList.setModel(this.m_ListModel);
/* 331:421 */           if (this.m_ListModel.getSize() > 0)
/* 332:    */           {
/* 333:422 */             this.m_ElementList.setSelectedIndex(0);
/* 334:    */           }
/* 335:    */           else
/* 336:    */           {
/* 337:424 */             this.m_DeleteBut.setEnabled(false);
/* 338:425 */             this.m_EditBut.setEnabled(false);
/* 339:    */           }
/* 340:427 */           this.m_UpBut.setEnabled(JListHelper.canMoveDown(this.m_ElementList));
/* 341:428 */           this.m_DownBut.setEnabled(JListHelper.canMoveDown(this.m_ElementList));
/* 342:    */           
/* 343:    */ 
/* 344:    */ 
/* 345:    */ 
/* 346:    */ 
/* 347:    */ 
/* 348:    */ 
/* 349:    */ 
/* 350:    */ 
/* 351:    */ 
/* 352:    */ 
/* 353:    */ 
/* 354:    */ 
/* 355:442 */           JPanel panel = new JPanel();
/* 356:443 */           panel.setLayout(new BorderLayout());
/* 357:444 */           panel.add(view, "Center");
/* 358:445 */           panel.add(this.m_AddBut, "East");
/* 359:446 */           add(panel, "North");
/* 360:447 */           add(new JScrollPane(this.m_ElementList), "Center");
/* 361:448 */           JPanel panel2 = new JPanel();
/* 362:449 */           panel2.setLayout(new GridLayout(1, 4));
/* 363:450 */           panel2.add(this.m_DeleteBut);
/* 364:451 */           panel2.add(this.m_EditBut);
/* 365:452 */           panel2.add(this.m_UpBut);
/* 366:453 */           panel2.add(this.m_DownBut);
/* 367:454 */           add(panel2, "South");
/* 368:455 */           this.m_ElementEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 369:    */           {
/* 370:    */             public void propertyChange(PropertyChangeEvent e)
/* 371:    */             {
/* 372:459 */               GenericArrayEditor.CustomEditor.this.repaint();
/* 373:    */             }
/* 374:    */           });
/* 375:    */         }
/* 376:    */       }
/* 377:468 */       if (this.m_ElementEditor == null) {
/* 378:469 */         add(this.m_Label, "Center");
/* 379:    */       }
/* 380:471 */       this.m_Support.firePropertyChange("", null, null);
/* 381:472 */       validate();
/* 382:    */     }
/* 383:    */   }
/* 384:    */   
/* 385:    */   public GenericArrayEditor()
/* 386:    */   {
/* 387:477 */     this.m_customEditor = new CustomEditor();
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void setValue(Object o)
/* 391:    */   {
/* 392:489 */     this.m_customEditor.updateEditorType(o);
/* 393:    */   }
/* 394:    */   
/* 395:    */   public Object getValue()
/* 396:    */   {
/* 397:500 */     if (this.m_customEditor.m_ListModel == null) {
/* 398:501 */       return null;
/* 399:    */     }
/* 400:504 */     int length = this.m_customEditor.m_ListModel.getSize();
/* 401:505 */     Object result = Array.newInstance(this.m_customEditor.m_ElementClass, length);
/* 402:506 */     for (int i = 0; i < length; i++) {
/* 403:507 */       Array.set(result, i, this.m_customEditor.m_ListModel.elementAt(i));
/* 404:    */     }
/* 405:509 */     return result;
/* 406:    */   }
/* 407:    */   
/* 408:    */   public String getJavaInitializationString()
/* 409:    */   {
/* 410:523 */     return "null";
/* 411:    */   }
/* 412:    */   
/* 413:    */   public boolean isPaintable()
/* 414:    */   {
/* 415:534 */     return true;
/* 416:    */   }
/* 417:    */   
/* 418:    */   public void paintValue(Graphics gfx, Rectangle box)
/* 419:    */   {
/* 420:546 */     FontMetrics fm = gfx.getFontMetrics();
/* 421:547 */     int vpad = (box.height - fm.getHeight()) / 2;
/* 422:548 */     String rep = this.m_customEditor.m_ListModel.getSize() + " " + this.m_customEditor.m_ElementClass.getName();
/* 423:    */     
/* 424:550 */     gfx.drawString(rep, 2, fm.getAscent() + vpad + 2);
/* 425:    */   }
/* 426:    */   
/* 427:    */   public String getAsText()
/* 428:    */   {
/* 429:560 */     return null;
/* 430:    */   }
/* 431:    */   
/* 432:    */   public void setAsText(String text)
/* 433:    */   {
/* 434:572 */     throw new IllegalArgumentException(text);
/* 435:    */   }
/* 436:    */   
/* 437:    */   public String[] getTags()
/* 438:    */   {
/* 439:582 */     return null;
/* 440:    */   }
/* 441:    */   
/* 442:    */   public boolean supportsCustomEditor()
/* 443:    */   {
/* 444:592 */     return true;
/* 445:    */   }
/* 446:    */   
/* 447:    */   public Component getCustomEditor()
/* 448:    */   {
/* 449:602 */     return this.m_customEditor;
/* 450:    */   }
/* 451:    */   
/* 452:    */   public void addPropertyChangeListener(PropertyChangeListener l)
/* 453:    */   {
/* 454:612 */     this.m_customEditor.m_Support.addPropertyChangeListener(l);
/* 455:    */   }
/* 456:    */   
/* 457:    */   public void removePropertyChangeListener(PropertyChangeListener l)
/* 458:    */   {
/* 459:622 */     this.m_customEditor.m_Support.removePropertyChangeListener(l);
/* 460:    */   }
/* 461:    */   
/* 462:    */   public static Object makeCopy(Object source)
/* 463:    */   {
/* 464:    */     Object result;
/* 465:    */     try
/* 466:    */     {
/* 467:635 */       result = GenericObjectEditor.makeCopy(source);
/* 468:    */     }
/* 469:    */     catch (Exception e)
/* 470:    */     {
/* 471:637 */       result = null;
/* 472:    */     }
/* 473:640 */     return result;
/* 474:    */   }
/* 475:    */   
/* 476:    */   public static void main(String[] args)
/* 477:    */   {
/* 478:    */     try
/* 479:    */     {
/* 480:651 */       GenericObjectEditor.registerEditors();
/* 481:    */       
/* 482:653 */       GenericArrayEditor ce = new GenericArrayEditor();
/* 483:    */       
/* 484:655 */       Filter[] initial = new Filter[0];
/* 485:    */       
/* 486:    */ 
/* 487:    */ 
/* 488:    */ 
/* 489:    */ 
/* 490:    */ 
/* 491:662 */       PropertyDialog pd = new PropertyDialog((Frame)null, ce, 100, 100);
/* 492:663 */       pd.setSize(200, 200);
/* 493:664 */       pd.addWindowListener(new WindowAdapter()
/* 494:    */       {
/* 495:    */         public void windowClosing(WindowEvent e)
/* 496:    */         {
/* 497:667 */           System.exit(0);
/* 498:    */         }
/* 499:669 */       });
/* 500:670 */       ce.setValue(initial);
/* 501:671 */       pd.setVisible(true);
/* 502:    */     }
/* 503:    */     catch (Exception ex)
/* 504:    */     {
/* 505:674 */       ex.printStackTrace();
/* 506:675 */       System.err.println(ex.getMessage());
/* 507:    */     }
/* 508:    */   }
/* 509:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GenericArrayEditor
 * JD-Core Version:    0.7.0.1
 */