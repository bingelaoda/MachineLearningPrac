/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dialog.ModalityType;
/*   5:    */ import java.awt.FlowLayout;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JCheckBox;
/*  13:    */ import javax.swing.JDialog;
/*  14:    */ import javax.swing.JFrame;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JRootPane;
/*  18:    */ import weka.gui.sql.event.ResultChangedEvent;
/*  19:    */ import weka.gui.sql.event.ResultChangedListener;
/*  20:    */ 
/*  21:    */ public class SqlViewerDialog
/*  22:    */   extends JDialog
/*  23:    */   implements ResultChangedListener
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -31619864037233099L;
/*  26:    */   protected JFrame m_Parent;
/*  27:    */   protected SqlViewer m_Viewer;
/*  28:    */   protected JPanel m_PanelButtons;
/*  29: 65 */   protected JButton m_ButtonOK = new JButton("OK");
/*  30: 68 */   protected JButton m_ButtonCancel = new JButton("Cancel");
/*  31: 71 */   protected JLabel m_LabelQuery = new JLabel("");
/*  32: 74 */   protected JCheckBox m_CheckBoxSparseData = new JCheckBox("Generate sparse data");
/*  33: 77 */   protected int m_ReturnValue = 2;
/*  34:    */   protected String m_URL;
/*  35:    */   protected String m_User;
/*  36:    */   protected String m_Password;
/*  37:    */   protected String m_Query;
/*  38:    */   
/*  39:    */   public SqlViewerDialog(JFrame parent)
/*  40:    */   {
/*  41: 97 */     super(parent, "SQL-Viewer", Dialog.ModalityType.DOCUMENT_MODAL);
/*  42:    */     
/*  43: 99 */     this.m_Parent = parent;
/*  44:100 */     this.m_URL = "";
/*  45:101 */     this.m_User = "";
/*  46:102 */     this.m_Password = "";
/*  47:103 */     this.m_Query = "";
/*  48:    */     
/*  49:105 */     createDialog();
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected void createDialog()
/*  53:    */   {
/*  54:116 */     final SqlViewerDialog dialog = this;
/*  55:117 */     setLayout(new BorderLayout());
/*  56:    */     
/*  57:    */ 
/*  58:120 */     this.m_Viewer = new SqlViewer(this.m_Parent);
/*  59:121 */     add(this.m_Viewer, "Center");
/*  60:    */     
/*  61:123 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  62:124 */     add(panel2, "South");
/*  63:    */     
/*  64:    */ 
/*  65:127 */     JPanel panel = new JPanel(new FlowLayout(2));
/*  66:128 */     panel2.add(panel, "East");
/*  67:129 */     this.m_ButtonOK.setMnemonic('O');
/*  68:130 */     panel.add(this.m_ButtonOK);
/*  69:131 */     this.m_ButtonOK.addActionListener(new ActionListener()
/*  70:    */     {
/*  71:    */       public void actionPerformed(ActionEvent evt)
/*  72:    */       {
/*  73:133 */         SqlViewerDialog.this.m_ReturnValue = 0;
/*  74:    */         
/*  75:    */ 
/*  76:136 */         SqlViewerDialog.this.m_Viewer.removeResultChangedListener(dialog);
/*  77:137 */         SqlViewerDialog.this.m_Viewer.saveSize();
/*  78:138 */         dialog.dispose();
/*  79:    */       }
/*  80:140 */     });
/*  81:141 */     this.m_ButtonCancel.setMnemonic('C');
/*  82:142 */     panel.add(this.m_ButtonCancel);
/*  83:143 */     this.m_ButtonCancel.addActionListener(new ActionListener()
/*  84:    */     {
/*  85:    */       public void actionPerformed(ActionEvent evt)
/*  86:    */       {
/*  87:145 */         SqlViewerDialog.this.m_ReturnValue = 2;
/*  88:    */         
/*  89:    */ 
/*  90:148 */         SqlViewerDialog.this.m_Viewer.removeResultChangedListener(dialog);
/*  91:149 */         SqlViewerDialog.this.m_Viewer.saveSize();
/*  92:150 */         dialog.dispose();
/*  93:    */       }
/*  94:154 */     });
/*  95:155 */     panel = new JPanel(new FlowLayout(0));
/*  96:156 */     panel2.add(panel, "West");
/*  97:157 */     panel.add(this.m_CheckBoxSparseData);
/*  98:158 */     this.m_CheckBoxSparseData.setMnemonic('s');
/*  99:    */     
/* 100:160 */     addWindowListener(new WindowAdapter()
/* 101:    */     {
/* 102:    */       public void windowClosing(WindowEvent e)
/* 103:    */       {
/* 104:165 */         SqlViewerDialog.this.m_Viewer.saveSize();
/* 105:    */       }
/* 106:169 */     });
/* 107:170 */     panel = new JPanel(new FlowLayout(1));
/* 108:171 */     panel2.add(panel, "Center");
/* 109:172 */     panel.add(this.m_LabelQuery);
/* 110:    */     
/* 111:174 */     pack();
/* 112:175 */     getRootPane().setDefaultButton(this.m_ButtonOK);
/* 113:176 */     setResizable(true);
/* 114:    */     
/* 115:    */ 
/* 116:179 */     this.m_Viewer.addResultChangedListener(this);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setVisible(boolean b)
/* 120:    */   {
/* 121:188 */     if (b) {
/* 122:189 */       this.m_ReturnValue = 2;
/* 123:    */     }
/* 124:191 */     super.setVisible(b);
/* 125:194 */     if (b) {
/* 126:195 */       this.m_Viewer.clear();
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:    */   public int getReturnValue()
/* 131:    */   {
/* 132:205 */     return this.m_ReturnValue;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String getURL()
/* 136:    */   {
/* 137:214 */     return this.m_URL;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String getUser()
/* 141:    */   {
/* 142:223 */     return this.m_User;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getPassword()
/* 146:    */   {
/* 147:232 */     return this.m_Password;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String getQuery()
/* 151:    */   {
/* 152:241 */     return this.m_Query;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public boolean getGenerateSparseData()
/* 156:    */   {
/* 157:250 */     return this.m_CheckBoxSparseData.isSelected();
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void resultChanged(ResultChangedEvent evt)
/* 161:    */   {
/* 162:259 */     this.m_URL = evt.getURL();
/* 163:260 */     this.m_User = evt.getUser();
/* 164:261 */     this.m_Password = evt.getPassword();
/* 165:262 */     this.m_Query = evt.getQuery();
/* 166:263 */     this.m_LabelQuery.setText("Current query: " + this.m_Query);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public static void main(String[] args)
/* 170:    */   {
/* 171:274 */     SqlViewerDialog dialog = new SqlViewerDialog(null);
/* 172:275 */     dialog.setDefaultCloseOperation(2);
/* 173:276 */     dialog.setVisible(true);
/* 174:277 */     System.out.println("ReturnValue = " + dialog.getReturnValue());
/* 175:278 */     if (dialog.getReturnValue() == 0)
/* 176:    */     {
/* 177:279 */       System.out.println("URL      = " + dialog.getURL());
/* 178:280 */       System.out.println("User     = " + dialog.getUser());
/* 179:281 */       System.out.println("Password = " + dialog.getPassword().replaceAll(".", "*"));
/* 180:282 */       System.out.println("Query    = " + dialog.getQuery());
/* 181:    */     }
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.SqlViewerDialog
 * JD-Core Version:    0.7.0.1
 */