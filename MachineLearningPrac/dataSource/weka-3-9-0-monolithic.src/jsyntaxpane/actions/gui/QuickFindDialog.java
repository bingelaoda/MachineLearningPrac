/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import java.awt.Point;
/*   9:    */ import java.awt.Rectangle;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.awt.event.WindowAdapter;
/*  13:    */ import java.awt.event.WindowEvent;
/*  14:    */ import java.lang.ref.WeakReference;
/*  15:    */ import java.util.regex.Pattern;
/*  16:    */ import java.util.regex.PatternSyntaxException;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.GroupLayout;
/*  19:    */ import javax.swing.GroupLayout.Alignment;
/*  20:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  21:    */ import javax.swing.ImageIcon;
/*  22:    */ import javax.swing.JButton;
/*  23:    */ import javax.swing.JCheckBox;
/*  24:    */ import javax.swing.JDialog;
/*  25:    */ import javax.swing.JLabel;
/*  26:    */ import javax.swing.JTextField;
/*  27:    */ import javax.swing.JToolBar;
/*  28:    */ import javax.swing.JToolBar.Separator;
/*  29:    */ import javax.swing.SwingUtilities;
/*  30:    */ import javax.swing.event.DocumentEvent;
/*  31:    */ import javax.swing.event.DocumentListener;
/*  32:    */ import javax.swing.text.BadLocationException;
/*  33:    */ import javax.swing.text.Document;
/*  34:    */ import javax.swing.text.JTextComponent;
/*  35:    */ import jsyntaxpane.actions.ActionUtils;
/*  36:    */ import jsyntaxpane.actions.DocumentSearchData;
/*  37:    */ import jsyntaxpane.components.Markers;
/*  38:    */ import jsyntaxpane.components.Markers.SimpleMarker;
/*  39:    */ import jsyntaxpane.util.SwingUtils;
/*  40:    */ 
/*  41:    */ public class QuickFindDialog
/*  42:    */   extends JDialog
/*  43:    */   implements DocumentListener, ActionListener, EscapeListener
/*  44:    */ {
/*  45: 48 */   private Markers.SimpleMarker marker = new Markers.SimpleMarker(Color.PINK);
/*  46:    */   private WeakReference<JTextComponent> target;
/*  47:    */   private WeakReference<DocumentSearchData> dsd;
/*  48:    */   private int oldCaretPosition;
/*  49: 57 */   private boolean escaped = false;
/*  50:    */   private JButton jBtnNext;
/*  51:    */   private JButton jBtnPrev;
/*  52:    */   private JCheckBox jChkIgnoreCase;
/*  53:    */   private JCheckBox jChkRegExp;
/*  54:    */   private JCheckBox jChkWrap;
/*  55:    */   private JLabel jLabel1;
/*  56:    */   private JLabel jLblStatus;
/*  57:    */   private JToolBar.Separator jSeparator1;
/*  58:    */   private JToolBar jToolBar1;
/*  59:    */   private JTextField jTxtFind;
/*  60:    */   
/*  61:    */   public QuickFindDialog(JTextComponent target, DocumentSearchData data)
/*  62:    */   {
/*  63: 66 */     super(ActionUtils.getFrameFor(target), false);
/*  64: 67 */     initComponents();
/*  65: 68 */     SwingUtils.addEscapeListener(this);
/*  66: 69 */     this.dsd = new WeakReference(data);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void showFor(final JTextComponent target)
/*  70:    */   {
/*  71: 73 */     this.oldCaretPosition = target.getCaretPosition();
/*  72: 74 */     Container view = target.getParent();
/*  73: 75 */     Dimension wd = getSize();
/*  74: 76 */     wd.width = target.getVisibleRect().width;
/*  75: 77 */     Point loc = new Point(0, view.getHeight());
/*  76: 78 */     setSize(wd);
/*  77: 79 */     setLocationRelativeTo(view);
/*  78: 80 */     SwingUtilities.convertPointToScreen(loc, view);
/*  79: 81 */     setLocation(loc);
/*  80: 82 */     this.jTxtFind.setFont(target.getFont());
/*  81: 83 */     this.jTxtFind.getDocument().addDocumentListener(this);
/*  82: 84 */     WindowAdapter closeListener = new WindowAdapter()
/*  83:    */     {
/*  84:    */       public void windowDeactivated(WindowEvent e)
/*  85:    */       {
/*  86: 88 */         target.getDocument().removeDocumentListener(QuickFindDialog.this);
/*  87: 89 */         Markers.removeMarkers(target, QuickFindDialog.this.marker);
/*  88: 90 */         if (QuickFindDialog.this.escaped) {
/*  89:    */           try
/*  90:    */           {
/*  91: 93 */             Rectangle aRect = target.modelToView(QuickFindDialog.this.oldCaretPosition);
/*  92: 94 */             target.setCaretPosition(QuickFindDialog.this.oldCaretPosition);
/*  93: 95 */             target.scrollRectToVisible(aRect);
/*  94:    */           }
/*  95:    */           catch (BadLocationException ex) {}
/*  96:    */         }
/*  97: 99 */         QuickFindDialog.this.dispose();
/*  98:    */       }
/*  99:101 */     };
/* 100:102 */     addWindowListener(closeListener);
/* 101:103 */     this.target = new WeakReference(target);
/* 102:104 */     Pattern p = ((DocumentSearchData)this.dsd.get()).getPattern();
/* 103:105 */     if (p != null) {
/* 104:106 */       this.jTxtFind.setText(p.pattern());
/* 105:    */     }
/* 106:108 */     this.jChkWrap.setSelected(((DocumentSearchData)this.dsd.get()).isWrap());
/* 107:109 */     setVisible(true);
/* 108:    */   }
/* 109:    */   
/* 110:    */   private void initComponents()
/* 111:    */   {
/* 112:122 */     this.jToolBar1 = new JToolBar();
/* 113:123 */     this.jSeparator1 = new JToolBar.Separator();
/* 114:124 */     this.jLabel1 = new JLabel();
/* 115:125 */     this.jTxtFind = new JTextField();
/* 116:126 */     this.jBtnPrev = new JButton();
/* 117:127 */     this.jBtnNext = new JButton();
/* 118:128 */     this.jChkIgnoreCase = new JCheckBox();
/* 119:129 */     this.jChkRegExp = new JCheckBox();
/* 120:130 */     this.jChkWrap = new JCheckBox();
/* 121:131 */     this.jLblStatus = new JLabel();
/* 122:    */     
/* 123:133 */     setDefaultCloseOperation(2);
/* 124:134 */     setBackground(Color.darkGray);
/* 125:135 */     setName("QuickFindDialog");
/* 126:136 */     setResizable(false);
/* 127:137 */     setUndecorated(true);
/* 128:    */     
/* 129:139 */     this.jToolBar1.setBorder(BorderFactory.createEtchedBorder());
/* 130:140 */     this.jToolBar1.setFloatable(false);
/* 131:141 */     this.jToolBar1.setRollover(true);
/* 132:142 */     this.jToolBar1.add(this.jSeparator1);
/* 133:    */     
/* 134:144 */     this.jLabel1.setLabelFor(this.jTxtFind);
/* 135:145 */     this.jLabel1.setText("Quick Find");
/* 136:146 */     this.jToolBar1.add(this.jLabel1);
/* 137:    */     
/* 138:148 */     this.jTxtFind.setColumns(30);
/* 139:149 */     this.jTxtFind.setMaximumSize(new Dimension(200, 20));
/* 140:150 */     this.jTxtFind.setPreferredSize(new Dimension(150, 20));
/* 141:151 */     this.jToolBar1.add(this.jTxtFind);
/* 142:    */     
/* 143:153 */     this.jBtnPrev.setIcon(new ImageIcon(getClass().getResource("/META-INF/images/small-icons/go-up.png")));
/* 144:154 */     this.jBtnPrev.setFocusable(false);
/* 145:155 */     this.jBtnPrev.setHorizontalTextPosition(0);
/* 146:156 */     this.jBtnPrev.setOpaque(false);
/* 147:157 */     this.jBtnPrev.setVerticalTextPosition(3);
/* 148:158 */     this.jBtnPrev.addActionListener(new ActionListener()
/* 149:    */     {
/* 150:    */       public void actionPerformed(ActionEvent evt)
/* 151:    */       {
/* 152:160 */         QuickFindDialog.this.jBtnPrevActionPerformed(evt);
/* 153:    */       }
/* 154:162 */     });
/* 155:163 */     this.jToolBar1.add(this.jBtnPrev);
/* 156:    */     
/* 157:165 */     this.jBtnNext.setIcon(new ImageIcon(getClass().getResource("/META-INF/images/small-icons/go-down.png")));
/* 158:166 */     this.jBtnNext.setFocusable(false);
/* 159:167 */     this.jBtnNext.setHorizontalTextPosition(0);
/* 160:168 */     this.jBtnNext.setMargin(new Insets(2, 2, 2, 2));
/* 161:169 */     this.jBtnNext.setOpaque(false);
/* 162:170 */     this.jBtnNext.setVerticalTextPosition(3);
/* 163:171 */     this.jBtnNext.addActionListener(new ActionListener()
/* 164:    */     {
/* 165:    */       public void actionPerformed(ActionEvent evt)
/* 166:    */       {
/* 167:173 */         QuickFindDialog.this.jBtnNextActionPerformed(evt);
/* 168:    */       }
/* 169:175 */     });
/* 170:176 */     this.jToolBar1.add(this.jBtnNext);
/* 171:    */     
/* 172:178 */     this.jChkIgnoreCase.setMnemonic('C');
/* 173:179 */     this.jChkIgnoreCase.setText("Ignore Case");
/* 174:180 */     this.jChkIgnoreCase.setFocusable(false);
/* 175:181 */     this.jChkIgnoreCase.setOpaque(false);
/* 176:182 */     this.jChkIgnoreCase.setVerticalTextPosition(3);
/* 177:183 */     this.jToolBar1.add(this.jChkIgnoreCase);
/* 178:184 */     this.jChkIgnoreCase.addActionListener(this);
/* 179:    */     
/* 180:186 */     this.jChkRegExp.setMnemonic('R');
/* 181:187 */     this.jChkRegExp.setText("Reg Exp");
/* 182:188 */     this.jChkRegExp.setFocusable(false);
/* 183:189 */     this.jChkRegExp.setOpaque(false);
/* 184:190 */     this.jChkRegExp.setVerticalTextPosition(3);
/* 185:191 */     this.jToolBar1.add(this.jChkRegExp);
/* 186:192 */     this.jChkRegExp.addActionListener(this);
/* 187:    */     
/* 188:194 */     this.jChkWrap.setMnemonic('W');
/* 189:195 */     this.jChkWrap.setText("Wrap");
/* 190:196 */     this.jChkWrap.setFocusable(false);
/* 191:197 */     this.jChkWrap.setOpaque(false);
/* 192:198 */     this.jChkWrap.setVerticalTextPosition(3);
/* 193:199 */     this.jToolBar1.add(this.jChkWrap);
/* 194:200 */     this.jChkWrap.addActionListener(this);
/* 195:    */     
/* 196:202 */     this.jLblStatus.setFont(this.jLblStatus.getFont().deriveFont(this.jLblStatus.getFont().getSize() - 2.0F));
/* 197:203 */     this.jLblStatus.setForeground(Color.red);
/* 198:204 */     this.jToolBar1.add(this.jLblStatus);
/* 199:    */     
/* 200:206 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 201:207 */     getContentPane().setLayout(layout);
/* 202:208 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jToolBar1, -1, 684, 32767));
/* 203:    */     
/* 204:    */ 
/* 205:    */ 
/* 206:212 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jToolBar1, -2, -1, -2));
/* 207:    */     
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:217 */     pack();
/* 212:    */   }
/* 213:    */   
/* 214:    */   private void jBtnNextActionPerformed(ActionEvent evt)
/* 215:    */   {
/* 216:221 */     if (((DocumentSearchData)this.dsd.get()).doFindNext((JTextComponent)this.target.get())) {
/* 217:222 */       this.jLblStatus.setText(null);
/* 218:    */     } else {
/* 219:224 */       this.jLblStatus.setText("not found");
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   private void jBtnPrevActionPerformed(ActionEvent evt)
/* 224:    */   {
/* 225:229 */     if (((DocumentSearchData)this.dsd.get()).doFindPrev((JTextComponent)this.target.get())) {
/* 226:230 */       this.jLblStatus.setText(null);
/* 227:    */     } else {
/* 228:232 */       this.jLblStatus.setText("not found");
/* 229:    */     }
/* 230:    */   }
/* 231:    */   
/* 232:    */   public void insertUpdate(DocumentEvent e)
/* 233:    */   {
/* 234:251 */     updateFind();
/* 235:    */   }
/* 236:    */   
/* 237:    */   public void removeUpdate(DocumentEvent e)
/* 238:    */   {
/* 239:256 */     updateFind();
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void changedUpdate(DocumentEvent e)
/* 243:    */   {
/* 244:261 */     updateFind();
/* 245:    */   }
/* 246:    */   
/* 247:    */   private void updateFind()
/* 248:    */   {
/* 249:265 */     JTextComponent t = (JTextComponent)this.target.get();
/* 250:266 */     DocumentSearchData d = (DocumentSearchData)this.dsd.get();
/* 251:267 */     String toFind = this.jTxtFind.getText();
/* 252:268 */     if ((toFind == null) || (toFind.isEmpty()))
/* 253:    */     {
/* 254:269 */       this.jLblStatus.setText(null);
/* 255:270 */       return;
/* 256:    */     }
/* 257:    */     try
/* 258:    */     {
/* 259:273 */       d.setWrap(this.jChkWrap.isSelected());
/* 260:274 */       d.setPattern(toFind, this.jChkRegExp.isSelected(), this.jChkIgnoreCase.isSelected());
/* 261:    */       
/* 262:    */ 
/* 263:    */ 
/* 264:    */ 
/* 265:279 */       this.jLblStatus.setText(null);
/* 266:280 */       t.setCaretPosition(this.oldCaretPosition);
/* 267:281 */       if (!d.doFindNext(t)) {
/* 268:282 */         this.jLblStatus.setText("Not found");
/* 269:    */       } else {
/* 270:284 */         this.jLblStatus.setText(null);
/* 271:    */       }
/* 272:    */     }
/* 273:    */     catch (PatternSyntaxException e)
/* 274:    */     {
/* 275:287 */       this.jLblStatus.setText(e.getDescription());
/* 276:    */     }
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void actionPerformed(ActionEvent e)
/* 280:    */   {
/* 281:293 */     if ((e.getSource() instanceof JCheckBox)) {
/* 282:294 */       updateFind();
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void escapePressed()
/* 287:    */   {
/* 288:300 */     this.escaped = true;
/* 289:301 */     setVisible(false);
/* 290:    */   }
/* 291:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.QuickFindDialog
 * JD-Core Version:    0.7.0.1
 */