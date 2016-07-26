/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import java.lang.ref.WeakReference;
/*   7:    */ import javax.swing.GroupLayout;
/*   8:    */ import javax.swing.GroupLayout.Alignment;
/*   9:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  10:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JComboBox;
/*  13:    */ import javax.swing.JDialog;
/*  14:    */ import javax.swing.JOptionPane;
/*  15:    */ import javax.swing.JRootPane;
/*  16:    */ import javax.swing.LayoutStyle.ComponentPlacement;
/*  17:    */ import javax.swing.text.Document;
/*  18:    */ import javax.swing.text.JTextComponent;
/*  19:    */ import jsyntaxpane.actions.ActionUtils;
/*  20:    */ import jsyntaxpane.util.SwingUtils;
/*  21:    */ 
/*  22:    */ public class GotoLineDialog
/*  23:    */   extends JDialog
/*  24:    */   implements EscapeListener
/*  25:    */ {
/*  26:    */   private static final String PROPERTY_KEY = "GOTOLINE_DIALOG";
/*  27:    */   private WeakReference<JTextComponent> text;
/*  28:    */   private JButton jBtnOk;
/*  29:    */   private JComboBox jCmbLineNumbers;
/*  30:    */   
/*  31:    */   private GotoLineDialog(JTextComponent text)
/*  32:    */   {
/*  33: 37 */     super(ActionUtils.getFrameFor(text), false);
/*  34: 38 */     initComponents();
/*  35: 39 */     this.text = new WeakReference(text);
/*  36: 40 */     setLocationRelativeTo(text.getRootPane());
/*  37: 41 */     getRootPane().setDefaultButton(this.jBtnOk);
/*  38: 42 */     text.getDocument().putProperty("GOTOLINE_DIALOG", this);
/*  39: 43 */     SwingUtils.addEscapeListener(this);
/*  40:    */   }
/*  41:    */   
/*  42:    */   private void initComponents()
/*  43:    */   {
/*  44: 55 */     this.jCmbLineNumbers = new JComboBox();
/*  45: 56 */     this.jBtnOk = new JButton();
/*  46:    */     
/*  47: 58 */     setTitle("Goto Line");
/*  48: 59 */     setModal(true);
/*  49: 60 */     setName("");
/*  50: 61 */     setResizable(false);
/*  51:    */     
/*  52: 63 */     this.jCmbLineNumbers.setEditable(true);
/*  53: 64 */     this.jCmbLineNumbers.addActionListener(new ActionListener()
/*  54:    */     {
/*  55:    */       public void actionPerformed(ActionEvent evt)
/*  56:    */       {
/*  57: 66 */         GotoLineDialog.this.jCmbLineNumbersActionPerformed(evt);
/*  58:    */       }
/*  59: 69 */     });
/*  60: 70 */     this.jBtnOk.setAction(this.jCmbLineNumbers.getAction());
/*  61: 71 */     this.jBtnOk.setText("Go");
/*  62: 72 */     this.jBtnOk.addActionListener(new ActionListener()
/*  63:    */     {
/*  64:    */       public void actionPerformed(ActionEvent evt)
/*  65:    */       {
/*  66: 74 */         GotoLineDialog.this.jBtnOkActionPerformed(evt);
/*  67:    */       }
/*  68: 77 */     });
/*  69: 78 */     GroupLayout layout = new GroupLayout(getContentPane());
/*  70: 79 */     getContentPane().setLayout(layout);
/*  71: 80 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jCmbLineNumbers, -2, 104, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jBtnOk, -1, 47, 32767).addContainerGap()));
/*  72:    */     
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80: 89 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jCmbLineNumbers, -2, -1, -2).addComponent(this.jBtnOk, -2, 26, -2)).addContainerGap(-1, 32767)));
/*  81:    */     
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90: 99 */     pack();
/*  91:    */   }
/*  92:    */   
/*  93:    */   private void setTextPos()
/*  94:    */   {
/*  95:103 */     Object line = this.jCmbLineNumbers.getSelectedItem();
/*  96:104 */     if (line != null) {
/*  97:    */       try
/*  98:    */       {
/*  99:106 */         int lineNr = Integer.parseInt(line.toString());
/* 100:107 */         ActionUtils.insertIntoCombo(this.jCmbLineNumbers, line);
/* 101:108 */         ActionUtils.setCaretPosition((JTextComponent)this.text.get(), lineNr, 0);
/* 102:109 */         setVisible(false);
/* 103:    */       }
/* 104:    */       catch (NumberFormatException ex)
/* 105:    */       {
/* 106:111 */         JOptionPane.showMessageDialog(this, "Invalid Number: " + line, "Number Error", 0);
/* 107:    */       }
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   private void jCmbLineNumbersActionPerformed(ActionEvent evt)
/* 112:    */   {
/* 113:118 */     if (evt.getActionCommand().equals("comboBoxEdited")) {
/* 114:119 */       setTextPos();
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   private void jBtnOkActionPerformed(ActionEvent evt)
/* 119:    */   {
/* 120:124 */     setTextPos();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void showForEditor(JTextComponent text)
/* 124:    */   {
/* 125:137 */     GotoLineDialog dlg = null;
/* 126:138 */     if (text.getDocument().getProperty("GOTOLINE_DIALOG") == null) {
/* 127:139 */       dlg = new GotoLineDialog(text);
/* 128:    */     } else {
/* 129:141 */       dlg = (GotoLineDialog)text.getDocument().getProperty("GOTOLINE_DIALOG");
/* 130:    */     }
/* 131:143 */     dlg.jCmbLineNumbers.requestFocusInWindow();
/* 132:144 */     dlg.setVisible(true);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void escapePressed()
/* 136:    */   {
/* 137:150 */     setVisible(false);
/* 138:    */   }
/* 139:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.GotoLineDialog
 * JD-Core Version:    0.7.0.1
 */