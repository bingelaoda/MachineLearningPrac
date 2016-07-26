/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog.ModalityType;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Frame;
/*   8:    */ import java.awt.Toolkit;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.util.regex.Pattern;
/*  13:    */ import javax.swing.Box;
/*  14:    */ import javax.swing.DefaultListModel;
/*  15:    */ import javax.swing.JButton;
/*  16:    */ import javax.swing.JDialog;
/*  17:    */ import javax.swing.JList;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JRootPane;
/*  20:    */ import javax.swing.JScrollPane;
/*  21:    */ import javax.swing.ListModel;
/*  22:    */ 
/*  23:    */ public class ListSelectorDialog
/*  24:    */   extends JDialog
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = 906147926840288895L;
/*  27: 56 */   protected JButton m_SelectBut = new JButton("Select");
/*  28: 59 */   protected JButton m_CancelBut = new JButton("Cancel");
/*  29: 62 */   protected JButton m_PatternBut = new JButton("Pattern");
/*  30:    */   protected JList m_List;
/*  31:    */   protected int m_Result;
/*  32:    */   public static final int APPROVE_OPTION = 0;
/*  33:    */   public static final int CANCEL_OPTION = 1;
/*  34: 77 */   protected String m_PatternRegEx = ".*";
/*  35:    */   
/*  36:    */   public ListSelectorDialog(Frame parentFrame, JList userList)
/*  37:    */   {
/*  38: 87 */     super(parentFrame, "Select items", Dialog.ModalityType.DOCUMENT_MODAL);
/*  39: 88 */     this.m_List = userList;
/*  40: 89 */     this.m_CancelBut.setMnemonic('C');
/*  41: 90 */     this.m_CancelBut.addActionListener(new ActionListener()
/*  42:    */     {
/*  43:    */       public void actionPerformed(ActionEvent e)
/*  44:    */       {
/*  45: 92 */         ListSelectorDialog.this.m_Result = 1;
/*  46: 93 */         ListSelectorDialog.this.setVisible(false);
/*  47:    */       }
/*  48: 95 */     });
/*  49: 96 */     this.m_SelectBut.setMnemonic('S');
/*  50: 97 */     this.m_SelectBut.addActionListener(new ActionListener()
/*  51:    */     {
/*  52:    */       public void actionPerformed(ActionEvent e)
/*  53:    */       {
/*  54: 99 */         ListSelectorDialog.this.m_Result = 0;
/*  55:100 */         ListSelectorDialog.this.setVisible(false);
/*  56:    */       }
/*  57:102 */     });
/*  58:103 */     this.m_PatternBut.setMnemonic('P');
/*  59:104 */     this.m_PatternBut.addActionListener(new ActionListener()
/*  60:    */     {
/*  61:    */       public void actionPerformed(ActionEvent e)
/*  62:    */       {
/*  63:106 */         ListSelectorDialog.this.selectPattern();
/*  64:    */       }
/*  65:109 */     });
/*  66:110 */     Container c = getContentPane();
/*  67:111 */     c.setLayout(new BorderLayout());
/*  68:    */     
/*  69:113 */     Box b1 = new Box(0);
/*  70:114 */     b1.add(this.m_SelectBut);
/*  71:115 */     b1.add(Box.createHorizontalStrut(10));
/*  72:116 */     b1.add(this.m_PatternBut);
/*  73:117 */     b1.add(Box.createHorizontalStrut(10));
/*  74:118 */     b1.add(this.m_CancelBut);
/*  75:119 */     c.add(b1, "South");
/*  76:120 */     c.add(new JScrollPane(this.m_List), "Center");
/*  77:    */     
/*  78:122 */     getRootPane().setDefaultButton(this.m_SelectBut);
/*  79:    */     
/*  80:124 */     pack();
/*  81:    */     
/*  82:    */ 
/*  83:127 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  84:128 */     int width = getWidth() > screen.getWidth() ? (int)screen.getWidth() : getWidth();
/*  85:    */     
/*  86:130 */     int height = getHeight() > screen.getHeight() ? (int)screen.getHeight() : getHeight();
/*  87:    */     
/*  88:132 */     setSize(width, height);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public int showDialog()
/*  92:    */   {
/*  93:142 */     this.m_Result = 1;
/*  94:143 */     int[] origSelected = this.m_List.getSelectedIndices();
/*  95:144 */     setVisible(true);
/*  96:145 */     if (this.m_Result == 1) {
/*  97:146 */       this.m_List.setSelectedIndices(origSelected);
/*  98:    */     }
/*  99:148 */     return this.m_Result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   protected void selectPattern()
/* 103:    */   {
/* 104:156 */     String pattern = JOptionPane.showInputDialog(this.m_PatternBut.getParent(), "Enter a Perl regular expression ('.*' for all)", this.m_PatternRegEx);
/* 105:160 */     if (pattern != null) {
/* 106:    */       try
/* 107:    */       {
/* 108:162 */         Pattern.compile(pattern);
/* 109:163 */         this.m_PatternRegEx = pattern;
/* 110:164 */         this.m_List.clearSelection();
/* 111:165 */         for (int i = 0; i < this.m_List.getModel().getSize(); i++) {
/* 112:166 */           if (Pattern.matches(pattern, this.m_List.getModel().getElementAt(i).toString())) {
/* 113:168 */             this.m_List.addSelectionInterval(i, i);
/* 114:    */           }
/* 115:    */         }
/* 116:    */       }
/* 117:    */       catch (Exception ex)
/* 118:    */       {
/* 119:172 */         JOptionPane.showMessageDialog(this.m_PatternBut.getParent(), "'" + pattern + "' is not a valid Perl regular expression!\n" + "Error: " + ex, "Error in Pattern...", 0);
/* 120:    */       }
/* 121:    */     }
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static void main(String[] args)
/* 125:    */   {
/* 126:    */     try
/* 127:    */     {
/* 128:190 */       DefaultListModel lm = new DefaultListModel();
/* 129:191 */       lm.addElement("one");
/* 130:192 */       lm.addElement("two");
/* 131:193 */       lm.addElement("three");
/* 132:194 */       lm.addElement("four");
/* 133:195 */       lm.addElement("five");
/* 134:196 */       JList jl = new JList(lm);
/* 135:197 */       ListSelectorDialog jd = new ListSelectorDialog(null, jl);
/* 136:198 */       int result = jd.showDialog();
/* 137:199 */       if (result == 0)
/* 138:    */       {
/* 139:200 */         System.err.println("Fields Selected");
/* 140:201 */         int[] selected = jl.getSelectedIndices();
/* 141:202 */         for (int i = 0; i < selected.length; i++) {
/* 142:203 */           System.err.println("" + selected[i] + " " + lm.elementAt(selected[i]));
/* 143:    */         }
/* 144:    */       }
/* 145:    */       else
/* 146:    */       {
/* 147:207 */         System.err.println("Cancelled");
/* 148:    */       }
/* 149:209 */       System.exit(0);
/* 150:    */     }
/* 151:    */     catch (Exception ex)
/* 152:    */     {
/* 153:211 */       ex.printStackTrace();
/* 154:212 */       System.err.println(ex.getMessage());
/* 155:    */     }
/* 156:    */   }
/* 157:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ListSelectorDialog
 * JD-Core Version:    0.7.0.1
 */