/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Toolkit;
/*   6:    */ import java.awt.datatransfer.Clipboard;
/*   7:    */ import java.awt.datatransfer.StringSelection;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import javax.swing.DefaultListModel;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JFrame;
/*  13:    */ import javax.swing.JLabel;
/*  14:    */ import javax.swing.JList;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JScrollPane;
/*  17:    */ import javax.swing.event.ListSelectionEvent;
/*  18:    */ import javax.swing.event.ListSelectionListener;
/*  19:    */ import weka.gui.ComponentHelper;
/*  20:    */ 
/*  21:    */ public class InfoPanel
/*  22:    */   extends JPanel
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -7701133696481997973L;
/*  25:    */   protected JFrame m_Parent;
/*  26:    */   protected JList m_Info;
/*  27:    */   protected DefaultListModel m_Model;
/*  28:    */   protected JButton m_ButtonClear;
/*  29:    */   protected JButton m_ButtonCopy;
/*  30:    */   
/*  31:    */   public InfoPanel(JFrame parent)
/*  32:    */   {
/*  33: 78 */     this.m_Parent = parent;
/*  34: 79 */     createPanel();
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected void createPanel()
/*  38:    */   {
/*  39: 89 */     setLayout(new BorderLayout());
/*  40: 90 */     setPreferredSize(new Dimension(0, 80));
/*  41:    */     
/*  42:    */ 
/*  43: 93 */     this.m_Model = new DefaultListModel();
/*  44: 94 */     this.m_Info = new JList(this.m_Model);
/*  45: 95 */     this.m_Info.setCellRenderer(new InfoPanelCellRenderer());
/*  46: 96 */     this.m_Info.addListSelectionListener(new ListSelectionListener()
/*  47:    */     {
/*  48:    */       public void valueChanged(ListSelectionEvent e)
/*  49:    */       {
/*  50: 98 */         InfoPanel.this.setButtons(e);
/*  51:    */       }
/*  52:100 */     });
/*  53:101 */     add(new JScrollPane(this.m_Info), "Center");
/*  54:    */     
/*  55:    */ 
/*  56:104 */     JPanel panel = new JPanel(new BorderLayout());
/*  57:105 */     add(panel, "East");
/*  58:106 */     this.m_ButtonClear = new JButton("Clear");
/*  59:107 */     this.m_ButtonClear.addActionListener(new ActionListener()
/*  60:    */     {
/*  61:    */       public void actionPerformed(ActionEvent e)
/*  62:    */       {
/*  63:109 */         InfoPanel.this.clear();
/*  64:    */       }
/*  65:111 */     });
/*  66:112 */     panel.add(this.m_ButtonClear, "North");
/*  67:    */     
/*  68:    */ 
/*  69:115 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  70:116 */     panel.add(panel2, "Center");
/*  71:117 */     this.m_ButtonCopy = new JButton("Copy");
/*  72:118 */     this.m_ButtonCopy.addActionListener(new ActionListener()
/*  73:    */     {
/*  74:    */       public void actionPerformed(ActionEvent e)
/*  75:    */       {
/*  76:120 */         InfoPanel.this.copyToClipboard();
/*  77:    */       }
/*  78:122 */     });
/*  79:123 */     panel2.add(this.m_ButtonCopy, "North");
/*  80:    */   }
/*  81:    */   
/*  82:    */   protected void setButtons(ListSelectionEvent e)
/*  83:    */   {
/*  84:131 */     if ((e == null) || (e.getSource() == this.m_Info))
/*  85:    */     {
/*  86:132 */       this.m_ButtonClear.setEnabled(this.m_Model.getSize() > 0);
/*  87:133 */       this.m_ButtonCopy.setEnabled(this.m_Info.getSelectedIndices().length == 1);
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setFocus()
/*  92:    */   {
/*  93:141 */     this.m_Info.requestFocus();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void clear()
/*  97:    */   {
/*  98:148 */     this.m_Model.clear();
/*  99:149 */     setButtons(null);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean copyToClipboard()
/* 103:    */   {
/* 104:161 */     if (this.m_Info.getSelectedIndices().length != 1) {
/* 105:162 */       return false;
/* 106:    */     }
/* 107:164 */     StringSelection selection = new StringSelection(((JLabel)this.m_Info.getSelectedValue()).getText());
/* 108:165 */     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 109:166 */     clipboard.setContents(selection, selection);
/* 110:167 */     return true;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void append(String msg, String icon)
/* 114:    */   {
/* 115:177 */     append(new JLabel(msg, ComponentHelper.getImageIcon(icon), 2));
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void append(Object msg)
/* 119:    */   {
/* 120:185 */     if ((msg instanceof String))
/* 121:    */     {
/* 122:186 */       append(msg.toString(), "empty_small.gif");
/* 123:187 */       return;
/* 124:    */     }
/* 125:190 */     this.m_Model.addElement(msg);
/* 126:191 */     this.m_Info.setSelectedIndex(this.m_Model.getSize() - 1);
/* 127:192 */     this.m_Info.ensureIndexIsVisible(this.m_Info.getSelectedIndex());
/* 128:    */     
/* 129:194 */     setButtons(null);
/* 130:    */   }
/* 131:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.InfoPanel
 * JD-Core Version:    0.7.0.1
 */