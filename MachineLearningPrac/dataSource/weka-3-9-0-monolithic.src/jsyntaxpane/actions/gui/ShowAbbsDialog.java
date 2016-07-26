/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Set;
/*   8:    */ import javax.swing.GroupLayout;
/*   9:    */ import javax.swing.GroupLayout.Alignment;
/*  10:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  11:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  12:    */ import javax.swing.JDialog;
/*  13:    */ import javax.swing.JEditorPane;
/*  14:    */ import javax.swing.JList;
/*  15:    */ import javax.swing.JScrollPane;
/*  16:    */ import javax.swing.JSplitPane;
/*  17:    */ import javax.swing.event.ListSelectionEvent;
/*  18:    */ import javax.swing.event.ListSelectionListener;
/*  19:    */ import jsyntaxpane.actions.ActionUtils;
/*  20:    */ import jsyntaxpane.util.SwingUtils;
/*  21:    */ 
/*  22:    */ public class ShowAbbsDialog
/*  23:    */   extends JDialog
/*  24:    */   implements EscapeListener
/*  25:    */ {
/*  26:    */   private JEditorPane jEdtAbbr;
/*  27:    */   private JList jLstAbbs;
/*  28:    */   private JScrollPane jScrollPane1;
/*  29:    */   private JScrollPane jScrollPane2;
/*  30:    */   private JSplitPane jSplitPane1;
/*  31:    */   Map<String, String> abbs;
/*  32:    */   
/*  33:    */   public ShowAbbsDialog(JEditorPane parent, Map<String, String> abbs)
/*  34:    */   {
/*  35: 37 */     super(ActionUtils.getFrameFor(parent), true);
/*  36: 38 */     initComponents();
/*  37: 39 */     Object[] abbsList = abbs.keySet().toArray();
/*  38: 40 */     Arrays.sort(abbsList);
/*  39: 41 */     this.jLstAbbs.setListData(abbsList);
/*  40: 42 */     this.abbs = abbs;
/*  41: 43 */     this.jEdtAbbr.setEditorKit(parent.getEditorKit());
/*  42: 44 */     this.jLstAbbs.setSelectedIndex(0);
/*  43: 45 */     SwingUtils.addEscapeListener(this);
/*  44: 46 */     setVisible(true);
/*  45:    */   }
/*  46:    */   
/*  47:    */   private void initComponents()
/*  48:    */   {
/*  49: 59 */     this.jSplitPane1 = new JSplitPane();
/*  50: 60 */     this.jScrollPane1 = new JScrollPane();
/*  51: 61 */     this.jLstAbbs = new JList();
/*  52: 62 */     this.jScrollPane2 = new JScrollPane();
/*  53: 63 */     this.jEdtAbbr = new JEditorPane();
/*  54:    */     
/*  55: 65 */     setDefaultCloseOperation(2);
/*  56: 66 */     setTitle("Abbreviations");
/*  57: 67 */     setLocationByPlatform(true);
/*  58: 68 */     setMinimumSize(new Dimension(600, 300));
/*  59: 69 */     setModal(true);
/*  60: 70 */     setName("dlgShowAbbs");
/*  61:    */     
/*  62: 72 */     this.jSplitPane1.setDividerLocation(150);
/*  63: 73 */     this.jSplitPane1.setDividerSize(3);
/*  64:    */     
/*  65: 75 */     this.jScrollPane1.setPreferredSize(new Dimension(258, 400));
/*  66:    */     
/*  67: 77 */     this.jLstAbbs.setSelectionMode(0);
/*  68: 78 */     this.jLstAbbs.addListSelectionListener(new ListSelectionListener()
/*  69:    */     {
/*  70:    */       public void valueChanged(ListSelectionEvent evt)
/*  71:    */       {
/*  72: 80 */         ShowAbbsDialog.this.jLstAbbsValueChanged(evt);
/*  73:    */       }
/*  74: 82 */     });
/*  75: 83 */     this.jScrollPane1.setViewportView(this.jLstAbbs);
/*  76:    */     
/*  77: 85 */     this.jSplitPane1.setLeftComponent(this.jScrollPane1);
/*  78:    */     
/*  79: 87 */     this.jEdtAbbr.setEditable(false);
/*  80: 88 */     this.jEdtAbbr.setMinimumSize(new Dimension(106, 400));
/*  81: 89 */     this.jScrollPane2.setViewportView(this.jEdtAbbr);
/*  82:    */     
/*  83: 91 */     this.jSplitPane1.setRightComponent(this.jScrollPane2);
/*  84:    */     
/*  85: 93 */     GroupLayout layout = new GroupLayout(getContentPane());
/*  86: 94 */     getContentPane().setLayout(layout);
/*  87: 95 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jSplitPane1, -1, 580, 32767).addContainerGap()));
/*  88:    */     
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:102 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jSplitPane1, -2, 337, -2).addContainerGap(-1, 32767)));
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:110 */     pack();
/* 103:    */   }
/* 104:    */   
/* 105:    */   private void jLstAbbsValueChanged(ListSelectionEvent evt)
/* 106:    */   {
/* 107:114 */     if (!evt.getValueIsAdjusting())
/* 108:    */     {
/* 109:115 */       Object selected = this.jLstAbbs.getSelectedValue();
/* 110:116 */       if (selected != null) {
/* 111:117 */         this.jEdtAbbr.setText((String)this.abbs.get(selected.toString()));
/* 112:    */       }
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void escapePressed()
/* 117:    */   {
/* 118:133 */     setVisible(false);
/* 119:    */   }
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.ShowAbbsDialog
 * JD-Core Version:    0.7.0.1
 */