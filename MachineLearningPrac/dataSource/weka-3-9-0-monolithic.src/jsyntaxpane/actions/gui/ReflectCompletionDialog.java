/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.Dialog.ModalityType;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.Point;
/*   7:    */ import java.awt.Rectangle;
/*   8:    */ import java.awt.Window;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.ItemEvent;
/*  12:    */ import java.awt.event.ItemListener;
/*  13:    */ import java.awt.event.KeyAdapter;
/*  14:    */ import java.awt.event.KeyEvent;
/*  15:    */ import java.awt.event.MouseAdapter;
/*  16:    */ import java.awt.event.MouseEvent;
/*  17:    */ import java.lang.reflect.Constructor;
/*  18:    */ import java.lang.reflect.Field;
/*  19:    */ import java.lang.reflect.Member;
/*  20:    */ import java.lang.reflect.Method;
/*  21:    */ import java.util.ArrayList;
/*  22:    */ import java.util.List;
/*  23:    */ import java.util.Vector;
/*  24:    */ import java.util.logging.Level;
/*  25:    */ import java.util.logging.Logger;
/*  26:    */ import javax.swing.ComboBoxEditor;
/*  27:    */ import javax.swing.DefaultComboBoxModel;
/*  28:    */ import javax.swing.GroupLayout;
/*  29:    */ import javax.swing.GroupLayout.Alignment;
/*  30:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  31:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  32:    */ import javax.swing.JComboBox;
/*  33:    */ import javax.swing.JDialog;
/*  34:    */ import javax.swing.JList;
/*  35:    */ import javax.swing.JScrollPane;
/*  36:    */ import javax.swing.JTextField;
/*  37:    */ import javax.swing.ListModel;
/*  38:    */ import javax.swing.SwingUtilities;
/*  39:    */ import javax.swing.event.DocumentEvent;
/*  40:    */ import javax.swing.event.DocumentListener;
/*  41:    */ import javax.swing.text.BadLocationException;
/*  42:    */ import javax.swing.text.Document;
/*  43:    */ import javax.swing.text.JTextComponent;
/*  44:    */ import jsyntaxpane.actions.ActionUtils;
/*  45:    */ import jsyntaxpane.util.ReflectUtils;
/*  46:    */ import jsyntaxpane.util.StringUtils;
/*  47:    */ import jsyntaxpane.util.SwingUtils;
/*  48:    */ 
/*  49:    */ public class ReflectCompletionDialog
/*  50:    */   extends JDialog
/*  51:    */   implements EscapeListener
/*  52:    */ {
/*  53:    */   private Class theClass;
/*  54: 57 */   public String escapeChars = ";(= \t\n";
/*  55:    */   public List<Member> items;
/*  56:    */   private final JTextComponent target;
/*  57:    */   private JComboBox jCmbClassName;
/*  58:    */   private JList jLstItems;
/*  59:    */   private JScrollPane jScrollPane1;
/*  60:    */   private JTextField jTxtItem;
/*  61:    */   
/*  62:    */   public ReflectCompletionDialog(JTextComponent target)
/*  63:    */   {
/*  64: 66 */     super(SwingUtilities.getWindowAncestor(target), Dialog.ModalityType.APPLICATION_MODAL);
/*  65: 67 */     initComponents();
/*  66: 68 */     this.target = target;
/*  67: 69 */     this.jTxtItem.getDocument().addDocumentListener(new DocumentListener()
/*  68:    */     {
/*  69:    */       public void insertUpdate(DocumentEvent e)
/*  70:    */       {
/*  71: 73 */         ReflectCompletionDialog.this.refilterList();
/*  72:    */       }
/*  73:    */       
/*  74:    */       public void removeUpdate(DocumentEvent e)
/*  75:    */       {
/*  76: 78 */         ReflectCompletionDialog.this.refilterList();
/*  77:    */       }
/*  78:    */       
/*  79:    */       public void changedUpdate(DocumentEvent e)
/*  80:    */       {
/*  81: 83 */         ReflectCompletionDialog.this.refilterList();
/*  82:    */       }
/*  83: 86 */     });
/*  84: 87 */     this.jTxtItem.setFocusTraversalKeysEnabled(false);
/*  85:    */     
/*  86: 89 */     this.jCmbClassName.getEditor().addActionListener(new ActionListener()
/*  87:    */     {
/*  88:    */       public void actionPerformed(ActionEvent e)
/*  89:    */       {
/*  90: 93 */         ReflectCompletionDialog.this.updateItems();
/*  91:    */       }
/*  92: 95 */     });
/*  93: 96 */     SwingUtils.addEscapeListener(this);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setFonts(Font font)
/*  97:    */   {
/*  98:100 */     this.jTxtItem.setFont(font);
/*  99:101 */     this.jLstItems.setFont(font);
/* 100:102 */     doLayout();
/* 101:    */   }
/* 102:    */   
/* 103:    */   private String getSelection()
/* 104:    */   {
/* 105:    */     String result;
/* 106:    */     String result;
/* 107:107 */     if (this.jLstItems.getSelectedIndex() >= 0)
/* 108:    */     {
/* 109:108 */       Object selected = this.jLstItems.getSelectedValue();
/* 110:    */       String result;
/* 111:109 */       if ((selected instanceof Method))
/* 112:    */       {
/* 113:110 */         result = ReflectUtils.getJavaCallString((Method)selected);
/* 114:    */       }
/* 115:    */       else
/* 116:    */       {
/* 117:    */         String result;
/* 118:111 */         if ((selected instanceof Constructor))
/* 119:    */         {
/* 120:112 */           result = ReflectUtils.getJavaCallString((Constructor)selected);
/* 121:    */         }
/* 122:    */         else
/* 123:    */         {
/* 124:    */           String result;
/* 125:113 */           if ((selected instanceof Field)) {
/* 126:114 */             result = ((Field)selected).getName();
/* 127:    */           } else {
/* 128:116 */             result = selected.toString();
/* 129:    */           }
/* 130:    */         }
/* 131:    */       }
/* 132:    */     }
/* 133:    */     else
/* 134:    */     {
/* 135:119 */       result = this.jTxtItem.getText();
/* 136:    */     }
/* 137:121 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   private void refilterList()
/* 141:    */   {
/* 142:125 */     String prefix = this.jTxtItem.getText();
/* 143:126 */     Vector<Member> filtered = new Vector();
/* 144:127 */     Object selected = this.jLstItems.getSelectedValue();
/* 145:128 */     for (Member m : this.items) {
/* 146:129 */       if (StringUtils.camelCaseMatch(m.getName(), prefix)) {
/* 147:130 */         filtered.add(m);
/* 148:    */       }
/* 149:    */     }
/* 150:133 */     this.jLstItems.setListData(filtered);
/* 151:134 */     if ((selected != null) && (filtered.contains(selected))) {
/* 152:135 */       this.jLstItems.setSelectedValue(selected, true);
/* 153:    */     } else {
/* 154:137 */       this.jLstItems.setSelectedIndex(0);
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   private void initComponents()
/* 159:    */   {
/* 160:150 */     this.jTxtItem = new JTextField();
/* 161:151 */     this.jScrollPane1 = new JScrollPane();
/* 162:152 */     this.jLstItems = new JList();
/* 163:153 */     this.jCmbClassName = new JComboBox();
/* 164:    */     
/* 165:155 */     setDefaultCloseOperation(2);
/* 166:156 */     setName("CompletionDialog");
/* 167:157 */     setResizable(false);
/* 168:158 */     setUndecorated(true);
/* 169:    */     
/* 170:160 */     this.jTxtItem.setBorder(null);
/* 171:161 */     this.jTxtItem.addKeyListener(new KeyAdapter()
/* 172:    */     {
/* 173:    */       public void keyPressed(KeyEvent evt)
/* 174:    */       {
/* 175:163 */         ReflectCompletionDialog.this.jTxtItemKeyPressed(evt);
/* 176:    */       }
/* 177:166 */     });
/* 178:167 */     this.jLstItems.setSelectionMode(0);
/* 179:168 */     this.jLstItems.setCellRenderer(new MembersListRenderer(this));
/* 180:169 */     this.jLstItems.setFocusable(false);
/* 181:170 */     this.jLstItems.addMouseListener(new MouseAdapter()
/* 182:    */     {
/* 183:    */       public void mouseClicked(MouseEvent evt)
/* 184:    */       {
/* 185:172 */         ReflectCompletionDialog.this.jLstItemsMouseClicked(evt);
/* 186:    */       }
/* 187:174 */     });
/* 188:175 */     this.jScrollPane1.setViewportView(this.jLstItems);
/* 189:    */     
/* 190:177 */     this.jCmbClassName.setEditable(true);
/* 191:178 */     this.jCmbClassName.setModel(new DefaultComboBoxModel(new String[] { "Object", "String" }));
/* 192:179 */     this.jCmbClassName.addItemListener(new ItemListener()
/* 193:    */     {
/* 194:    */       public void itemStateChanged(ItemEvent evt)
/* 195:    */       {
/* 196:181 */         ReflectCompletionDialog.this.jCmbClassNameItemStateChanged(evt);
/* 197:    */       }
/* 198:184 */     });
/* 199:185 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 200:186 */     getContentPane().setLayout(layout);
/* 201:187 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTxtItem, -1, 437, 32767).addComponent(this.jScrollPane1, -1, 437, 32767).addComponent(this.jCmbClassName, 0, 437, 32767));
/* 202:    */     
/* 203:    */ 
/* 204:    */ 
/* 205:    */ 
/* 206:    */ 
/* 207:193 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jTxtItem, -2, -1, -2).addGap(0, 0, 0).addComponent(this.jScrollPane1, -1, 156, 32767).addGap(0, 0, 0).addComponent(this.jCmbClassName, -2, -1, -2)));
/* 208:    */     
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:203 */     pack();
/* 218:    */   }
/* 219:    */   
/* 220:    */   private void jTxtItemKeyPressed(KeyEvent evt)
/* 221:    */   {
/* 222:208 */     int i = this.jLstItems.getSelectedIndex();
/* 223:209 */     switch (evt.getKeyCode())
/* 224:    */     {
/* 225:    */     case 27: 
/* 226:211 */       String result = this.jTxtItem.getText();
/* 227:212 */       ActionUtils.insertMagicString(this.target, result);
/* 228:213 */       setVisible(false);
/* 229:214 */       return;
/* 230:    */     case 40: 
/* 231:216 */       i++;
/* 232:217 */       break;
/* 233:    */     case 38: 
/* 234:219 */       i--;
/* 235:220 */       break;
/* 236:    */     case 36: 
/* 237:222 */       i = 0;
/* 238:223 */       break;
/* 239:    */     case 35: 
/* 240:225 */       i = this.jLstItems.getModel().getSize() - 1;
/* 241:226 */       break;
/* 242:    */     case 34: 
/* 243:228 */       i += this.jLstItems.getVisibleRowCount();
/* 244:229 */       break;
/* 245:    */     case 33: 
/* 246:231 */       i -= this.jLstItems.getVisibleRowCount();
/* 247:    */     }
/* 248:235 */     if (this.escapeChars.indexOf(evt.getKeyChar()) >= 0)
/* 249:    */     {
/* 250:236 */       String result = getSelection();
/* 251:237 */       char pressed = evt.getKeyChar();
/* 252:240 */       if (pressed != '\n') {
/* 253:241 */         result = result + (pressed == '\t' ? ' ' : pressed);
/* 254:    */       }
/* 255:243 */       this.target.replaceSelection(result);
/* 256:244 */       setVisible(false);
/* 257:    */     }
/* 258:    */     else
/* 259:    */     {
/* 260:247 */       if (i >= this.jLstItems.getModel().getSize()) {
/* 261:248 */         i = this.jLstItems.getModel().getSize() - 1;
/* 262:    */       }
/* 263:250 */       if (i < 0) {
/* 264:251 */         i = 0;
/* 265:    */       }
/* 266:253 */       this.jLstItems.setSelectedIndex(i);
/* 267:254 */       this.jLstItems.ensureIndexIsVisible(i);
/* 268:    */     }
/* 269:    */   }
/* 270:    */   
/* 271:    */   private void jCmbClassNameItemStateChanged(ItemEvent evt)
/* 272:    */   {
/* 273:259 */     if (evt.getStateChange() == 1) {
/* 274:260 */       updateItems();
/* 275:    */     }
/* 276:    */   }
/* 277:    */   
/* 278:    */   private void jLstItemsMouseClicked(MouseEvent evt)
/* 279:    */   {
/* 280:265 */     if (evt.getClickCount() == 2)
/* 281:    */     {
/* 282:266 */       String selected = getSelection();
/* 283:267 */       this.target.replaceSelection(selected);
/* 284:268 */       setVisible(false);
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   private void updateItems()
/* 289:    */   {
/* 290:273 */     String className = this.jCmbClassName.getEditor().getItem().toString();
/* 291:274 */     if (this.items == null) {
/* 292:275 */       this.items = new ArrayList();
/* 293:    */     } else {
/* 294:277 */       this.items.clear();
/* 295:    */     }
/* 296:280 */     Class aClass = ReflectUtils.findClass(className, ReflectUtils.DEFAULT_PACKAGES);
/* 297:281 */     if (aClass != null)
/* 298:    */     {
/* 299:283 */       this.theClass = aClass;
/* 300:284 */       ReflectUtils.addConstrcutors(aClass, this.items);
/* 301:285 */       ReflectUtils.addMethods(aClass, this.items);
/* 302:286 */       ReflectUtils.addFields(aClass, this.items);
/* 303:287 */       ActionUtils.insertIntoCombo(this.jCmbClassName, className);
/* 304:288 */       this.jTxtItem.requestFocusInWindow();
/* 305:    */     }
/* 306:290 */     refilterList();
/* 307:    */   }
/* 308:    */   
/* 309:    */   public Class getTheClass()
/* 310:    */   {
/* 311:294 */     return this.theClass;
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void setItems(List<Member> items)
/* 315:    */   {
/* 316:302 */     this.items = items;
/* 317:    */   }
/* 318:    */   
/* 319:    */   public void displayFor(JTextComponent target)
/* 320:    */   {
/* 321:    */     try
/* 322:    */     {
/* 323:311 */       int dot = target.getSelectionStart();
/* 324:312 */       Window window = SwingUtilities.getWindowAncestor(target);
/* 325:313 */       Rectangle rt = target.modelToView(dot);
/* 326:314 */       Point loc = new Point(rt.x, rt.y);
/* 327:    */       
/* 328:    */ 
/* 329:317 */       loc = SwingUtilities.convertPoint(target, loc, window);
/* 330:    */       
/* 331:319 */       SwingUtilities.convertPointToScreen(loc, window);
/* 332:320 */       setLocationRelativeTo(window);
/* 333:321 */       setLocation(loc);
/* 334:    */     }
/* 335:    */     catch (BadLocationException ex)
/* 336:    */     {
/* 337:323 */       Logger.getLogger(ReflectCompletionDialog.class.getName()).log(Level.SEVERE, null, ex);
/* 338:    */     }
/* 339:    */     finally
/* 340:    */     {
/* 341:325 */       setFonts(target.getFont());
/* 342:326 */       updateItems();
/* 343:327 */       this.jTxtItem.setText(target.getSelectedText());
/* 344:328 */       setVisible(true);
/* 345:    */     }
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void escapePressed()
/* 349:    */   {
/* 350:340 */     setVisible(false);
/* 351:    */   }
/* 352:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.ReflectCompletionDialog
 * JD-Core Version:    0.7.0.1
 */