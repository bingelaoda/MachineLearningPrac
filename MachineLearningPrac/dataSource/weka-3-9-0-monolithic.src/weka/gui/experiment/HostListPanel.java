/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.WindowAdapter;
/*  11:    */ import java.awt.event.WindowEvent;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.DefaultListModel;
/*  15:    */ import javax.swing.JButton;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JList;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JScrollPane;
/*  20:    */ import javax.swing.JTextField;
/*  21:    */ import weka.experiment.RemoteExperiment;
/*  22:    */ 
/*  23:    */ public class HostListPanel
/*  24:    */   extends JPanel
/*  25:    */   implements ActionListener
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 7182791134585882197L;
/*  28:    */   protected RemoteExperiment m_Exp;
/*  29:    */   protected JList m_List;
/*  30: 65 */   protected JButton m_DeleteBut = new JButton("Delete selected");
/*  31: 68 */   protected JTextField m_HostField = new JTextField(25);
/*  32:    */   
/*  33:    */   public HostListPanel(RemoteExperiment exp)
/*  34:    */   {
/*  35: 76 */     this();
/*  36: 77 */     setExperiment(exp);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public HostListPanel()
/*  40:    */   {
/*  41: 84 */     this.m_List = new JList();
/*  42: 85 */     this.m_List.setModel(new DefaultListModel());
/*  43: 86 */     this.m_DeleteBut.setEnabled(false);
/*  44: 87 */     this.m_DeleteBut.addActionListener(this);
/*  45: 88 */     this.m_HostField.addActionListener(this);
/*  46: 89 */     setLayout(new BorderLayout());
/*  47: 90 */     setBorder(BorderFactory.createTitledBorder("Hosts"));
/*  48:    */     
/*  49: 92 */     JPanel topLab = new JPanel();
/*  50: 93 */     GridBagLayout gb = new GridBagLayout();
/*  51: 94 */     GridBagConstraints constraints = new GridBagConstraints();
/*  52: 95 */     topLab.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/*  53:    */     
/*  54: 97 */     topLab.setLayout(gb);
/*  55:    */     
/*  56: 99 */     constraints.gridx = 0;constraints.gridy = 0;constraints.weightx = 5.0D;
/*  57:100 */     constraints.fill = 2;
/*  58:101 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/*  59:102 */     constraints.insets = new Insets(0, 2, 0, 2);
/*  60:103 */     topLab.add(this.m_DeleteBut, constraints);
/*  61:104 */     constraints.gridx = 1;constraints.gridy = 0;constraints.weightx = 5.0D;
/*  62:105 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/*  63:106 */     topLab.add(this.m_HostField, constraints);
/*  64:    */     
/*  65:108 */     add(topLab, "North");
/*  66:109 */     add(new JScrollPane(this.m_List), "Center");
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setExperiment(RemoteExperiment exp)
/*  70:    */   {
/*  71:118 */     this.m_Exp = exp;
/*  72:119 */     this.m_List.setModel(this.m_Exp.getRemoteHosts());
/*  73:120 */     if (((DefaultListModel)this.m_List.getModel()).size() > 0) {
/*  74:121 */       this.m_DeleteBut.setEnabled(true);
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void actionPerformed(ActionEvent e)
/*  79:    */   {
/*  80:132 */     if (e.getSource() == this.m_HostField)
/*  81:    */     {
/*  82:133 */       ((DefaultListModel)this.m_List.getModel()).addElement(this.m_HostField.getText());
/*  83:    */       
/*  84:135 */       this.m_DeleteBut.setEnabled(true);
/*  85:    */     }
/*  86:136 */     else if (e.getSource() == this.m_DeleteBut)
/*  87:    */     {
/*  88:137 */       int[] selected = this.m_List.getSelectedIndices();
/*  89:138 */       if (selected != null) {
/*  90:139 */         for (int i = selected.length - 1; i >= 0; i--)
/*  91:    */         {
/*  92:140 */           int current = selected[i];
/*  93:141 */           ((DefaultListModel)this.m_List.getModel()).removeElementAt(current);
/*  94:142 */           if (((DefaultListModel)this.m_List.getModel()).size() > current) {
/*  95:143 */             this.m_List.setSelectedIndex(current);
/*  96:    */           } else {
/*  97:145 */             this.m_List.setSelectedIndex(current - 1);
/*  98:    */           }
/*  99:    */         }
/* 100:    */       }
/* 101:149 */       if (((DefaultListModel)this.m_List.getModel()).size() == 0) {
/* 102:150 */         this.m_DeleteBut.setEnabled(false);
/* 103:    */       }
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static void main(String[] args)
/* 108:    */   {
/* 109:    */     try
/* 110:    */     {
/* 111:163 */       JFrame jf = new JFrame("Host List Editor");
/* 112:164 */       jf.getContentPane().setLayout(new BorderLayout());
/* 113:165 */       HostListPanel dp = new HostListPanel();
/* 114:166 */       jf.getContentPane().add(dp, "Center");
/* 115:    */       
/* 116:168 */       jf.addWindowListener(new WindowAdapter()
/* 117:    */       {
/* 118:    */         public void windowClosing(WindowEvent e)
/* 119:    */         {
/* 120:170 */           this.val$jf.dispose();
/* 121:171 */           System.exit(0);
/* 122:    */         }
/* 123:173 */       });
/* 124:174 */       jf.pack();
/* 125:175 */       jf.setVisible(true);
/* 126:    */     }
/* 127:    */     catch (Exception ex)
/* 128:    */     {
/* 129:181 */       ex.printStackTrace();
/* 130:182 */       System.err.println(ex.getMessage());
/* 131:    */     }
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.HostListPanel
 * JD-Core Version:    0.7.0.1
 */