/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog.ModalityType;
/*   6:    */ import java.awt.FlowLayout;
/*   7:    */ import java.awt.Frame;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JDialog;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.event.ChangeEvent;
/*  14:    */ import javax.swing.event.ChangeListener;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.gui.arffviewer.ArffPanel;
/*  17:    */ 
/*  18:    */ public class ViewerDialog
/*  19:    */   extends JDialog
/*  20:    */   implements ChangeListener
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 6747718484736047752L;
/*  23:    */   public static final int APPROVE_OPTION = 0;
/*  24:    */   public static final int CANCEL_OPTION = 1;
/*  25: 60 */   protected int m_Result = 1;
/*  26: 63 */   protected JButton m_OkButton = new JButton("OK");
/*  27: 66 */   protected JButton m_CancelButton = new JButton("Cancel");
/*  28: 69 */   protected JButton m_UndoButton = new JButton("Undo");
/*  29: 72 */   protected JButton m_addInstanceButton = new JButton("Add instance");
/*  30: 75 */   protected ArffPanel m_ArffPanel = new ArffPanel();
/*  31:    */   
/*  32:    */   public ViewerDialog(Frame parent)
/*  33:    */   {
/*  34: 83 */     super(parent, Dialog.ModalityType.DOCUMENT_MODAL);
/*  35: 84 */     createDialog();
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected void createDialog()
/*  39:    */   {
/*  40: 93 */     setTitle("Viewer");
/*  41:    */     
/*  42: 95 */     getContentPane().setLayout(new BorderLayout());
/*  43:    */     
/*  44:    */ 
/*  45: 98 */     this.m_ArffPanel.addChangeListener(this);
/*  46: 99 */     getContentPane().add(this.m_ArffPanel, "Center");
/*  47:    */     
/*  48:    */ 
/*  49:102 */     JPanel panel = new JPanel(new FlowLayout(2));
/*  50:103 */     getContentPane().add(panel, "South");
/*  51:104 */     this.m_UndoButton.addActionListener(new ActionListener()
/*  52:    */     {
/*  53:    */       public void actionPerformed(ActionEvent e)
/*  54:    */       {
/*  55:106 */         ViewerDialog.this.undo();
/*  56:    */       }
/*  57:108 */     });
/*  58:109 */     getContentPane().add(panel, "South");
/*  59:110 */     this.m_CancelButton.addActionListener(new ActionListener()
/*  60:    */     {
/*  61:    */       public void actionPerformed(ActionEvent e)
/*  62:    */       {
/*  63:112 */         ViewerDialog.this.m_Result = 1;
/*  64:113 */         ViewerDialog.this.setVisible(false);
/*  65:    */       }
/*  66:115 */     });
/*  67:116 */     this.m_OkButton.addActionListener(new ActionListener()
/*  68:    */     {
/*  69:    */       public void actionPerformed(ActionEvent e)
/*  70:    */       {
/*  71:118 */         ViewerDialog.this.m_Result = 0;
/*  72:119 */         ViewerDialog.this.setVisible(false);
/*  73:    */       }
/*  74:121 */     });
/*  75:122 */     this.m_addInstanceButton.addActionListener(new ActionListener()
/*  76:    */     {
/*  77:    */       public void actionPerformed(ActionEvent e)
/*  78:    */       {
/*  79:125 */         ViewerDialog.this.m_ArffPanel.addInstanceAtEnd();
/*  80:    */       }
/*  81:127 */     });
/*  82:128 */     panel.add(this.m_addInstanceButton);
/*  83:129 */     panel.add(this.m_UndoButton);
/*  84:130 */     panel.add(this.m_OkButton);
/*  85:131 */     panel.add(this.m_CancelButton);
/*  86:    */     
/*  87:133 */     pack();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setInstances(Instances inst)
/*  91:    */   {
/*  92:140 */     this.m_ArffPanel.setInstances(new Instances(inst));
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Instances getInstances()
/*  96:    */   {
/*  97:147 */     return this.m_ArffPanel.getInstances();
/*  98:    */   }
/*  99:    */   
/* 100:    */   protected void setButtons()
/* 101:    */   {
/* 102:154 */     this.m_OkButton.setEnabled(true);
/* 103:155 */     this.m_CancelButton.setEnabled(true);
/* 104:156 */     this.m_UndoButton.setEnabled(this.m_ArffPanel.canUndo());
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean isChanged()
/* 108:    */   {
/* 109:165 */     return this.m_ArffPanel.isChanged();
/* 110:    */   }
/* 111:    */   
/* 112:    */   private void undo()
/* 113:    */   {
/* 114:172 */     this.m_ArffPanel.undo();
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void stateChanged(ChangeEvent e)
/* 118:    */   {
/* 119:179 */     setButtons();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public int showDialog()
/* 123:    */   {
/* 124:188 */     this.m_Result = 1;
/* 125:189 */     setVisible(true);
/* 126:190 */     setButtons();
/* 127:191 */     return this.m_Result;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public int showDialog(Instances inst)
/* 131:    */   {
/* 132:201 */     setInstances(inst);
/* 133:202 */     return showDialog();
/* 134:    */   }
/* 135:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ViewerDialog
 * JD-Core Version:    0.7.0.1
 */