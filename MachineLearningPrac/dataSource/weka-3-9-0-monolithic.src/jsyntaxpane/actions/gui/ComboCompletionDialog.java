/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.Point;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.awt.Window;
/*   8:    */ import java.awt.event.KeyAdapter;
/*   9:    */ import java.awt.event.KeyEvent;
/*  10:    */ import java.awt.event.MouseAdapter;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.util.List;
/*  13:    */ import java.util.Vector;
/*  14:    */ import java.util.logging.Level;
/*  15:    */ import java.util.logging.Logger;
/*  16:    */ import javax.swing.GroupLayout;
/*  17:    */ import javax.swing.GroupLayout.Alignment;
/*  18:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  19:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  20:    */ import javax.swing.JDialog;
/*  21:    */ import javax.swing.JList;
/*  22:    */ import javax.swing.JScrollPane;
/*  23:    */ import javax.swing.JTextField;
/*  24:    */ import javax.swing.ListModel;
/*  25:    */ import javax.swing.SwingUtilities;
/*  26:    */ import javax.swing.event.DocumentEvent;
/*  27:    */ import javax.swing.event.DocumentListener;
/*  28:    */ import javax.swing.text.BadLocationException;
/*  29:    */ import javax.swing.text.Document;
/*  30:    */ import javax.swing.text.JTextComponent;
/*  31:    */ import jsyntaxpane.actions.ActionUtils;
/*  32:    */ import jsyntaxpane.util.StringUtils;
/*  33:    */ import jsyntaxpane.util.SwingUtils;
/*  34:    */ 
/*  35:    */ public class ComboCompletionDialog
/*  36:    */   extends JDialog
/*  37:    */   implements EscapeListener
/*  38:    */ {
/*  39: 44 */   private String result = null;
/*  40:    */   private JTextComponent target;
/*  41: 49 */   public String escapeChars = ";(= \t\n\r";
/*  42:    */   public List<String> items;
/*  43:    */   private JList jLstItems;
/*  44:    */   private JScrollPane jScrollPane1;
/*  45:    */   private JTextField jTxtItem;
/*  46:    */   
/*  47:    */   public ComboCompletionDialog(JTextComponent target)
/*  48:    */   {
/*  49: 57 */     super(ActionUtils.getFrameFor(target), true);
/*  50: 58 */     initComponents();
/*  51: 59 */     this.jTxtItem.getDocument().addDocumentListener(new DocumentListener()
/*  52:    */     {
/*  53:    */       public void insertUpdate(DocumentEvent e)
/*  54:    */       {
/*  55: 63 */         ComboCompletionDialog.this.refilterList();
/*  56:    */       }
/*  57:    */       
/*  58:    */       public void removeUpdate(DocumentEvent e)
/*  59:    */       {
/*  60: 68 */         ComboCompletionDialog.this.refilterList();
/*  61:    */       }
/*  62:    */       
/*  63:    */       public void changedUpdate(DocumentEvent e)
/*  64:    */       {
/*  65: 73 */         ComboCompletionDialog.this.refilterList();
/*  66:    */       }
/*  67: 76 */     });
/*  68: 77 */     this.jTxtItem.setFocusTraversalKeysEnabled(false);
/*  69: 78 */     this.target = target;
/*  70: 79 */     SwingUtils.addEscapeListener(this);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void displayFor(String abbrev, List<String> items)
/*  74:    */   {
/*  75: 94 */     this.items = items;
/*  76:    */     try
/*  77:    */     {
/*  78: 96 */       Window window = SwingUtilities.getWindowAncestor(this.target);
/*  79: 97 */       Rectangle rt = this.target.modelToView(this.target.getSelectionStart());
/*  80: 98 */       Point loc = new Point(rt.x, rt.y);
/*  81: 99 */       setLocationRelativeTo(window);
/*  82:    */       
/*  83:    */ 
/*  84:102 */       loc = SwingUtilities.convertPoint(this.target, loc, window);
/*  85:    */       
/*  86:104 */       SwingUtilities.convertPointToScreen(loc, window);
/*  87:105 */       setLocation(loc);
/*  88:    */     }
/*  89:    */     catch (BadLocationException ex)
/*  90:    */     {
/*  91:    */       Font font;
/*  92:107 */       Logger.getLogger(ComboCompletionDialog.class.getName()).log(Level.SEVERE, null, ex);
/*  93:    */     }
/*  94:    */     finally
/*  95:    */     {
/*  96:    */       Font font;
/*  97:109 */       Font font = this.target.getFont();
/*  98:110 */       this.jTxtItem.setFont(font);
/*  99:111 */       this.jLstItems.setFont(font);
/* 100:112 */       doLayout();
/* 101:113 */       this.jTxtItem.setText(abbrev);
/* 102:114 */       refilterList();
/* 103:115 */       setVisible(true);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   private void refilterList()
/* 108:    */   {
/* 109:120 */     String prefix = this.jTxtItem.getText();
/* 110:121 */     Vector<String> filtered = new Vector();
/* 111:122 */     Object selected = this.jLstItems.getSelectedValue();
/* 112:123 */     for (String s : this.items) {
/* 113:124 */       if (StringUtils.camelCaseMatch(s, prefix)) {
/* 114:125 */         filtered.add(s);
/* 115:    */       }
/* 116:    */     }
/* 117:128 */     this.jLstItems.setListData(filtered);
/* 118:129 */     if ((selected != null) && (filtered.contains(selected))) {
/* 119:130 */       this.jLstItems.setSelectedValue(selected, true);
/* 120:    */     } else {
/* 121:132 */       this.jLstItems.setSelectedIndex(0);
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   private void initComponents()
/* 126:    */   {
/* 127:145 */     this.jTxtItem = new JTextField();
/* 128:146 */     this.jScrollPane1 = new JScrollPane();
/* 129:147 */     this.jLstItems = new JList();
/* 130:    */     
/* 131:149 */     setDefaultCloseOperation(2);
/* 132:150 */     setResizable(false);
/* 133:151 */     setUndecorated(true);
/* 134:    */     
/* 135:153 */     this.jTxtItem.setBorder(null);
/* 136:154 */     this.jTxtItem.addKeyListener(new KeyAdapter()
/* 137:    */     {
/* 138:    */       public void keyPressed(KeyEvent evt)
/* 139:    */       {
/* 140:156 */         ComboCompletionDialog.this.jTxtItemKeyPressed(evt);
/* 141:    */       }
/* 142:159 */     });
/* 143:160 */     this.jLstItems.setSelectionMode(0);
/* 144:161 */     this.jLstItems.setFocusable(false);
/* 145:162 */     this.jLstItems.addMouseListener(new MouseAdapter()
/* 146:    */     {
/* 147:    */       public void mouseClicked(MouseEvent evt)
/* 148:    */       {
/* 149:164 */         ComboCompletionDialog.this.jLstItemsMouseClicked(evt);
/* 150:    */       }
/* 151:166 */     });
/* 152:167 */     this.jScrollPane1.setViewportView(this.jLstItems);
/* 153:    */     
/* 154:169 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 155:170 */     getContentPane().setLayout(layout);
/* 156:171 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jTxtItem, -1, 375, 32767).addComponent(this.jScrollPane1, -1, 375, 32767));
/* 157:    */     
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:176 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jTxtItem, -2, -1, -2).addGap(0, 0, 0).addComponent(this.jScrollPane1, -1, 111, 32767)));
/* 162:    */     
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:184 */     pack();
/* 170:    */   }
/* 171:    */   
/* 172:    */   private void jTxtItemKeyPressed(KeyEvent evt)
/* 173:    */   {
/* 174:189 */     int i = this.jLstItems.getSelectedIndex();
/* 175:190 */     switch (evt.getKeyCode())
/* 176:    */     {
/* 177:    */     case 27: 
/* 178:192 */       this.result = this.jTxtItem.getText();
/* 179:193 */       this.target.replaceSelection(this.result);
/* 180:194 */       setVisible(false);
/* 181:195 */       return;
/* 182:    */     case 40: 
/* 183:197 */       if (i < this.jLstItems.getModel().getSize() - 1) {
/* 184:198 */         i++;
/* 185:    */       }
/* 186:200 */       this.jLstItems.setSelectedIndex(i);
/* 187:201 */       this.jLstItems.ensureIndexIsVisible(i);
/* 188:202 */       break;
/* 189:    */     case 38: 
/* 190:204 */       if (i > 0) {
/* 191:205 */         i--;
/* 192:    */       }
/* 193:207 */       this.jLstItems.setSelectedIndex(i);
/* 194:208 */       this.jLstItems.ensureIndexIsVisible(i);
/* 195:    */     }
/* 196:212 */     if (this.escapeChars.indexOf(evt.getKeyChar()) >= 0)
/* 197:    */     {
/* 198:213 */       if (this.jLstItems.getSelectedIndex() >= 0) {
/* 199:214 */         this.result = this.jLstItems.getSelectedValue().toString();
/* 200:    */       } else {
/* 201:216 */         this.result = this.jTxtItem.getText();
/* 202:    */       }
/* 203:218 */       char pressed = evt.getKeyChar();
/* 204:221 */       if (pressed != '\n') {
/* 205:222 */         this.result += (pressed == '\t' ? ' ' : pressed);
/* 206:    */       }
/* 207:224 */       this.target.replaceSelection(this.result);
/* 208:225 */       setVisible(false);
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   private void jLstItemsMouseClicked(MouseEvent evt)
/* 213:    */   {
/* 214:230 */     if (evt.getClickCount() == 2)
/* 215:    */     {
/* 216:231 */       String selected = this.jLstItems.getSelectedValue().toString();
/* 217:232 */       this.target.replaceSelection(selected);
/* 218:233 */       setVisible(false);
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void escapePressed()
/* 223:    */   {
/* 224:245 */     setVisible(false);
/* 225:    */   }
/* 226:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.ComboCompletionDialog
 * JD-Core Version:    0.7.0.1
 */