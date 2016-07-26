/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.EventQueue;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.event.ItemEvent;
/*   8:    */ import java.awt.event.ItemListener;
/*   9:    */ import java.io.IOException;
/*  10:    */ import java.io.StringReader;
/*  11:    */ import java.util.logging.Level;
/*  12:    */ import java.util.logging.Logger;
/*  13:    */ import javax.swing.DefaultComboBoxModel;
/*  14:    */ import javax.swing.GroupLayout;
/*  15:    */ import javax.swing.GroupLayout.Alignment;
/*  16:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  17:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  18:    */ import javax.swing.JComboBox;
/*  19:    */ import javax.swing.JEditorPane;
/*  20:    */ import javax.swing.JFrame;
/*  21:    */ import javax.swing.JLabel;
/*  22:    */ import javax.swing.JScrollPane;
/*  23:    */ import javax.swing.JToolBar;
/*  24:    */ import javax.swing.LayoutStyle.ComponentPlacement;
/*  25:    */ import javax.swing.event.CaretEvent;
/*  26:    */ import javax.swing.event.CaretListener;
/*  27:    */ import javax.swing.text.EditorKit;
/*  28:    */ import jsyntaxpane.actions.ActionUtils;
/*  29:    */ import jsyntaxpane.actions.CaretMonitor;
/*  30:    */ 
/*  31:    */ public class SyntaxTester
/*  32:    */   extends JFrame
/*  33:    */ {
/*  34:    */   private JComboBox jCmbLangs;
/*  35:    */   private JEditorPane jEdtTest;
/*  36:    */   private JScrollPane jScrollPane1;
/*  37:    */   private JToolBar jToolBar1;
/*  38:    */   private JLabel lblCaretPos;
/*  39:    */   private JLabel lblToken;
/*  40:    */   
/*  41:    */   public SyntaxTester()
/*  42:    */   {
/*  43: 33 */     initComponents();
/*  44: 34 */     this.jCmbLangs.setModel(new DefaultComboBoxModel(DefaultSyntaxKit.getContentTypes()));
/*  45:    */     
/*  46: 36 */     this.jCmbLangs.setSelectedItem("text/java");
/*  47: 37 */     new CaretMonitor(this.jEdtTest, this.lblCaretPos);
/*  48:    */   }
/*  49:    */   
/*  50:    */   private void initComponents()
/*  51:    */   {
/*  52: 49 */     this.lblCaretPos = new JLabel();
/*  53: 50 */     this.jScrollPane1 = new JScrollPane();
/*  54: 51 */     this.jEdtTest = new JEditorPane();
/*  55: 52 */     this.lblToken = new JLabel();
/*  56: 53 */     this.jCmbLangs = new JComboBox();
/*  57: 54 */     this.jToolBar1 = new JToolBar();
/*  58:    */     
/*  59: 56 */     setDefaultCloseOperation(3);
/*  60: 57 */     setTitle("JSyntaxPane Tester");
/*  61:    */     
/*  62: 59 */     this.lblCaretPos.setHorizontalAlignment(11);
/*  63: 60 */     this.lblCaretPos.setText("Caret Position");
/*  64:    */     
/*  65: 62 */     this.jEdtTest.setContentType("");
/*  66: 63 */     this.jEdtTest.setFont(new Font("Monospaced", 0, 13));
/*  67: 64 */     this.jEdtTest.setCaretColor(new Color(153, 204, 255));
/*  68: 65 */     this.jEdtTest.addCaretListener(new CaretListener()
/*  69:    */     {
/*  70:    */       public void caretUpdate(CaretEvent evt)
/*  71:    */       {
/*  72: 67 */         SyntaxTester.this.jEdtTestCaretUpdate(evt);
/*  73:    */       }
/*  74: 69 */     });
/*  75: 70 */     this.jScrollPane1.setViewportView(this.jEdtTest);
/*  76:    */     
/*  77: 72 */     this.lblToken.setFont(new Font("Courier New", 0, 12));
/*  78: 73 */     this.lblToken.setText("Token under cursor");
/*  79:    */     
/*  80: 75 */     this.jCmbLangs.setMaximumRowCount(20);
/*  81: 76 */     this.jCmbLangs.setFocusable(false);
/*  82: 77 */     this.jCmbLangs.addItemListener(new ItemListener()
/*  83:    */     {
/*  84:    */       public void itemStateChanged(ItemEvent evt)
/*  85:    */       {
/*  86: 79 */         SyntaxTester.this.jCmbLangsItemStateChanged(evt);
/*  87:    */       }
/*  88: 82 */     });
/*  89: 83 */     this.jToolBar1.setRollover(true);
/*  90: 84 */     this.jToolBar1.setFocusable(false);
/*  91:    */     
/*  92: 86 */     GroupLayout layout = new GroupLayout(getContentPane());
/*  93: 87 */     getContentPane().setLayout(layout);
/*  94: 88 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jToolBar1, -1, 848, 32767).addComponent(this.jScrollPane1).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(this.jCmbLangs, -2, 135, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 574, 32767).addComponent(this.lblCaretPos, -2, 119, -2).addContainerGap()).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.lblToken, -1, 354, 32767).addGap(484, 484, 484)));
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:103 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jToolBar1, -2, 25, -2).addGap(0, 0, 0).addComponent(this.jScrollPane1, -1, 387, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.lblToken, -2, 19, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.lblCaretPos, -1, 21, 32767).addComponent(this.jCmbLangs, -2, -1, -2)).addContainerGap()));
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:    */ 
/* 118:    */ 
/* 119:    */ 
/* 120:    */ 
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:118 */     pack();
/* 125:    */   }
/* 126:    */   
/* 127:    */   private void jEdtTestCaretUpdate(CaretEvent evt)
/* 128:    */   {
/* 129:122 */     SyntaxDocument sDoc = ActionUtils.getSyntaxDocument(this.jEdtTest);
/* 130:123 */     if (sDoc != null)
/* 131:    */     {
/* 132:124 */       Token t = sDoc.getTokenAt(evt.getDot());
/* 133:125 */       if (t != null)
/* 134:    */       {
/* 135:126 */         CharSequence tData = t.getText(sDoc);
/* 136:127 */         if (t.length > 40) {
/* 137:128 */           tData = tData.subSequence(0, 40);
/* 138:    */         }
/* 139:130 */         this.lblToken.setText(t.toString() + ": " + tData);
/* 140:    */       }
/* 141:    */       else
/* 142:    */       {
/* 143:133 */         this.lblToken.setText("NO Token at cursor");
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   private void jCmbLangsItemStateChanged(ItemEvent evt)
/* 149:    */   {
/* 150:140 */     if (evt.getStateChange() == 1)
/* 151:    */     {
/* 152:141 */       String lang = this.jCmbLangs.getSelectedItem().toString();
/* 153:    */       
/* 154:    */ 
/* 155:    */ 
/* 156:145 */       String oldText = this.jEdtTest.getText();
/* 157:    */       
/* 158:    */ 
/* 159:148 */       this.jEdtTest.setContentType(lang);
/* 160:    */       
/* 161:150 */       this.jToolBar1.removeAll();
/* 162:151 */       EditorKit kit = this.jEdtTest.getEditorKit();
/* 163:152 */       if ((kit instanceof DefaultSyntaxKit))
/* 164:    */       {
/* 165:153 */         DefaultSyntaxKit defaultSyntaxKit = (DefaultSyntaxKit)kit;
/* 166:154 */         defaultSyntaxKit.addToolBarActions(this.jEdtTest, this.jToolBar1);
/* 167:    */       }
/* 168:156 */       this.jToolBar1.validate();
/* 169:    */       try
/* 170:    */       {
/* 171:160 */         this.jEdtTest.read(new StringReader(oldText), lang);
/* 172:    */       }
/* 173:    */       catch (IOException ex)
/* 174:    */       {
/* 175:162 */         Logger.getLogger(SyntaxTester.class.getName()).log(Level.SEVERE, null, ex);
/* 176:    */       }
/* 177:    */     }
/* 178:165 */     this.jEdtTest.requestFocusInWindow();
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static void main(String[] args)
/* 182:    */   {
/* 183:173 */     EventQueue.invokeLater(new Runnable()
/* 184:    */     {
/* 185:    */       public void run()
/* 186:    */       {
/* 187:    */         try
/* 188:    */         {
/* 189:178 */           DefaultSyntaxKit.initKit();
/* 190:179 */           new SyntaxTester().setVisible(true);
/* 191:    */         }
/* 192:    */         catch (Exception e)
/* 193:    */         {
/* 194:181 */           e.printStackTrace();
/* 195:182 */           System.exit(2);
/* 196:    */         }
/* 197:    */       }
/* 198:    */     });
/* 199:    */   }
/* 200:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.SyntaxTester
 * JD-Core Version:    0.7.0.1
 */