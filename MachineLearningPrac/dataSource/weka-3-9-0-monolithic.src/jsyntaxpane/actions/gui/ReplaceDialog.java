/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.HeadlessException;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.util.regex.PatternSyntaxException;
/*   9:    */ import javax.swing.GroupLayout;
/*  10:    */ import javax.swing.GroupLayout.Alignment;
/*  11:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  12:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JComboBox;
/*  16:    */ import javax.swing.JDialog;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JRootPane;
/*  20:    */ import javax.swing.JToggleButton;
/*  21:    */ import javax.swing.LayoutStyle.ComponentPlacement;
/*  22:    */ import javax.swing.event.CaretEvent;
/*  23:    */ import javax.swing.event.CaretListener;
/*  24:    */ import javax.swing.text.JTextComponent;
/*  25:    */ import jsyntaxpane.actions.ActionUtils;
/*  26:    */ import jsyntaxpane.actions.DocumentSearchData;
/*  27:    */ import jsyntaxpane.components.Markers;
/*  28:    */ import jsyntaxpane.components.Markers.SimpleMarker;
/*  29:    */ import jsyntaxpane.util.SwingUtils;
/*  30:    */ 
/*  31:    */ public class ReplaceDialog
/*  32:    */   extends JDialog
/*  33:    */   implements CaretListener, EscapeListener
/*  34:    */ {
/*  35:    */   private JTextComponent textComponent;
/*  36:    */   private DocumentSearchData dsd;
/*  37: 39 */   private static Markers.SimpleMarker SEARCH_MARKER = new Markers.SimpleMarker(Color.YELLOW);
/*  38:    */   private JButton jBtnNext;
/*  39:    */   private JButton jBtnPrev;
/*  40:    */   private JButton jBtnReplace;
/*  41:    */   private JButton jBtnReplaceAll;
/*  42:    */   private JCheckBox jChkIgnoreCase;
/*  43:    */   private JCheckBox jChkRegex;
/*  44:    */   private JCheckBox jChkWrap;
/*  45:    */   private JComboBox jCmbFind;
/*  46:    */   private JComboBox jCmbReplace;
/*  47:    */   private JLabel jLblFind;
/*  48:    */   private JLabel jLblReplace;
/*  49:    */   private JToggleButton jTglHighlight;
/*  50:    */   
/*  51:    */   public ReplaceDialog(JTextComponent text, DocumentSearchData dsd)
/*  52:    */   {
/*  53: 48 */     super(ActionUtils.getFrameFor(text), false);
/*  54: 49 */     initComponents();
/*  55: 50 */     this.textComponent = text;
/*  56: 51 */     this.dsd = dsd;
/*  57: 52 */     this.textComponent.addCaretListener(this);
/*  58: 53 */     setLocationRelativeTo(text.getRootPane());
/*  59: 54 */     getRootPane().setDefaultButton(this.jBtnNext);
/*  60: 55 */     SwingUtils.addEscapeListener(this);
/*  61: 56 */     this.jBtnReplaceAll.setEnabled((text.isEditable()) && (text.isEnabled()));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void updateHighlights()
/*  65:    */   {
/*  66: 64 */     Markers.removeMarkers(this.textComponent, SEARCH_MARKER);
/*  67: 65 */     if (this.jTglHighlight.isSelected()) {
/*  68: 66 */       Markers.markAll(this.textComponent, this.dsd.getPattern(), SEARCH_MARKER);
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   private void showRegexpError(PatternSyntaxException ex)
/*  73:    */     throws HeadlessException
/*  74:    */   {
/*  75: 71 */     JOptionPane.showMessageDialog(this, "Regexp error: " + ex.getMessage(), "Regular Expression Error", 0);
/*  76:    */     
/*  77: 73 */     this.jCmbFind.requestFocus();
/*  78:    */   }
/*  79:    */   
/*  80:    */   private void updateFinder()
/*  81:    */   {
/*  82: 80 */     String regex = (String)this.jCmbFind.getSelectedItem();
/*  83:    */     try
/*  84:    */     {
/*  85: 82 */       this.dsd.setPattern(regex, this.jChkRegex.isSelected(), this.jChkIgnoreCase.isSelected());
/*  86:    */     }
/*  87:    */     catch (PatternSyntaxException e)
/*  88:    */     {
/*  89: 86 */       showRegexpError(e);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private void initComponents()
/*  94:    */   {
/*  95:100 */     this.jLblFind = new JLabel();
/*  96:101 */     this.jBtnNext = new JButton();
/*  97:102 */     this.jBtnPrev = new JButton();
/*  98:103 */     this.jBtnReplaceAll = new JButton();
/*  99:104 */     this.jChkWrap = new JCheckBox();
/* 100:105 */     this.jChkRegex = new JCheckBox();
/* 101:106 */     this.jChkIgnoreCase = new JCheckBox();
/* 102:107 */     this.jLblReplace = new JLabel();
/* 103:108 */     this.jTglHighlight = new JToggleButton();
/* 104:109 */     this.jCmbReplace = new JComboBox();
/* 105:110 */     this.jCmbFind = new JComboBox();
/* 106:111 */     this.jBtnReplace = new JButton();
/* 107:    */     
/* 108:113 */     setTitle("Find and Replace");
/* 109:114 */     setName("");
/* 110:115 */     setResizable(false);
/* 111:    */     
/* 112:117 */     this.jLblFind.setDisplayedMnemonic('F');
/* 113:118 */     this.jLblFind.setLabelFor(this.jCmbFind);
/* 114:119 */     this.jLblFind.setText("Find");
/* 115:    */     
/* 116:121 */     this.jBtnNext.setMnemonic('N');
/* 117:122 */     this.jBtnNext.setText("Next");
/* 118:123 */     this.jBtnNext.addActionListener(new ActionListener()
/* 119:    */     {
/* 120:    */       public void actionPerformed(ActionEvent evt)
/* 121:    */       {
/* 122:125 */         ReplaceDialog.this.jBtnNextActionPerformed(evt);
/* 123:    */       }
/* 124:128 */     });
/* 125:129 */     this.jBtnPrev.setMnemonic('N');
/* 126:130 */     this.jBtnPrev.setText("Previous");
/* 127:131 */     this.jBtnPrev.addActionListener(new ActionListener()
/* 128:    */     {
/* 129:    */       public void actionPerformed(ActionEvent evt)
/* 130:    */       {
/* 131:133 */         ReplaceDialog.this.jBtnPrevActionPerformed(evt);
/* 132:    */       }
/* 133:136 */     });
/* 134:137 */     this.jBtnReplaceAll.setMnemonic('H');
/* 135:138 */     this.jBtnReplaceAll.setText("Replace All");
/* 136:139 */     this.jBtnReplaceAll.addActionListener(new ActionListener()
/* 137:    */     {
/* 138:    */       public void actionPerformed(ActionEvent evt)
/* 139:    */       {
/* 140:141 */         ReplaceDialog.this.jBtnReplaceAllActionPerformed(evt);
/* 141:    */       }
/* 142:144 */     });
/* 143:145 */     this.jChkWrap.setMnemonic('W');
/* 144:146 */     this.jChkWrap.setText("Wrap around");
/* 145:147 */     this.jChkWrap.setToolTipText("Wrap to beginning when end is reached");
/* 146:    */     
/* 147:149 */     this.jChkRegex.setMnemonic('R');
/* 148:150 */     this.jChkRegex.setText("Regular Expression");
/* 149:    */     
/* 150:152 */     this.jChkIgnoreCase.setMnemonic('I');
/* 151:153 */     this.jChkIgnoreCase.setText("Ignore Case");
/* 152:    */     
/* 153:155 */     this.jLblReplace.setDisplayedMnemonic('R');
/* 154:156 */     this.jLblReplace.setLabelFor(this.jCmbReplace);
/* 155:157 */     this.jLblReplace.setText("Replace");
/* 156:    */     
/* 157:159 */     this.jTglHighlight.setText("Highlight");
/* 158:160 */     this.jTglHighlight.addActionListener(new ActionListener()
/* 159:    */     {
/* 160:    */       public void actionPerformed(ActionEvent evt)
/* 161:    */       {
/* 162:162 */         ReplaceDialog.this.jTglHighlightActionPerformed(evt);
/* 163:    */       }
/* 164:165 */     });
/* 165:166 */     this.jCmbReplace.setEditable(true);
/* 166:    */     
/* 167:168 */     this.jCmbFind.setEditable(true);
/* 168:    */     
/* 169:170 */     this.jBtnReplace.setText("Replace");
/* 170:171 */     this.jBtnReplace.addActionListener(new ActionListener()
/* 171:    */     {
/* 172:    */       public void actionPerformed(ActionEvent evt)
/* 173:    */       {
/* 174:173 */         ReplaceDialog.this.jBtnReplaceActionPerformed(evt);
/* 175:    */       }
/* 176:176 */     });
/* 177:177 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 178:178 */     getContentPane().setLayout(layout);
/* 179:179 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLblFind).addComponent(this.jLblReplace)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jChkRegex).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jChkWrap, -2, 105, -2)).addComponent(this.jCmbFind, 0, 298, 32767).addComponent(this.jCmbReplace, GroupLayout.Alignment.TRAILING, 0, 298, 32767).addComponent(this.jChkIgnoreCase)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jBtnReplace, GroupLayout.Alignment.TRAILING, -1, 98, 32767).addComponent(this.jBtnNext, -1, 98, 32767).addComponent(this.jBtnPrev, -1, 98, 32767).addComponent(this.jTglHighlight, GroupLayout.Alignment.TRAILING, -1, 98, 32767).addComponent(this.jBtnReplaceAll, GroupLayout.Alignment.TRAILING, -1, 98, 32767)).addContainerGap()));
/* 180:    */     
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:    */ 
/* 187:    */ 
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:    */ 
/* 192:    */ 
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:    */ 
/* 197:    */ 
/* 198:    */ 
/* 199:    */ 
/* 200:    */ 
/* 201:    */ 
/* 202:    */ 
/* 203:    */ 
/* 204:204 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLblFind).addComponent(this.jCmbFind, -2, -1, -2).addComponent(this.jBtnNext)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jBtnPrev).addComponent(this.jCmbReplace, -2, -1, -2).addComponent(this.jLblReplace)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jBtnReplace).addGap(3, 3, 3).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jChkWrap, -2, 23, -2).addComponent(this.jChkRegex).addComponent(this.jBtnReplaceAll)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jChkIgnoreCase).addComponent(this.jTglHighlight)).addContainerGap()));
/* 205:    */     
/* 206:    */ 
/* 207:    */ 
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:    */ 
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:    */ 
/* 224:    */ 
/* 225:    */ 
/* 226:    */ 
/* 227:    */ 
/* 228:    */ 
/* 229:    */ 
/* 230:    */ 
/* 231:231 */     pack();
/* 232:    */   }
/* 233:    */   
/* 234:    */   private void jBtnNextActionPerformed(ActionEvent evt)
/* 235:    */   {
/* 236:    */     try
/* 237:    */     {
/* 238:236 */       updateFinder();
/* 239:237 */       if (!this.dsd.doFindNext(this.textComponent)) {
/* 240:238 */         this.dsd.msgNotFound(this.textComponent);
/* 241:    */       }
/* 242:240 */       this.textComponent.requestFocusInWindow();
/* 243:    */     }
/* 244:    */     catch (PatternSyntaxException ex)
/* 245:    */     {
/* 246:242 */       showRegexpError(ex);
/* 247:    */     }
/* 248:    */   }
/* 249:    */   
/* 250:    */   private void jBtnReplaceAllActionPerformed(ActionEvent evt)
/* 251:    */   {
/* 252:    */     try
/* 253:    */     {
/* 254:248 */       updateFinder();
/* 255:249 */       String replacement = (String)this.jCmbReplace.getSelectedItem();
/* 256:250 */       ActionUtils.insertIntoCombo(this.jCmbFind, replacement);
/* 257:251 */       this.dsd.doReplaceAll(this.textComponent, replacement);
/* 258:252 */       this.textComponent.requestFocusInWindow();
/* 259:    */     }
/* 260:    */     catch (PatternSyntaxException ex)
/* 261:    */     {
/* 262:254 */       showRegexpError(ex);
/* 263:    */     }
/* 264:    */   }
/* 265:    */   
/* 266:    */   private void jTglHighlightActionPerformed(ActionEvent evt)
/* 267:    */   {
/* 268:259 */     updateFinder();
/* 269:260 */     updateHighlights();
/* 270:    */   }
/* 271:    */   
/* 272:    */   private void jBtnPrevActionPerformed(ActionEvent evt)
/* 273:    */   {
/* 274:264 */     updateFinder();
/* 275:265 */     this.dsd.doFindPrev(this.textComponent);
/* 276:    */   }
/* 277:    */   
/* 278:    */   private void jBtnReplaceActionPerformed(ActionEvent evt)
/* 279:    */   {
/* 280:269 */     this.dsd.doReplace(this.textComponent, String.valueOf(this.jCmbReplace.getSelectedItem()));
/* 281:    */   }
/* 282:    */   
/* 283:    */   public void caretUpdate(CaretEvent e)
/* 284:    */   {
/* 285:289 */     updateHighlights();
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void escapePressed()
/* 289:    */   {
/* 290:294 */     setVisible(false);
/* 291:    */   }
/* 292:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.ReplaceDialog
 * JD-Core Version:    0.7.0.1
 */