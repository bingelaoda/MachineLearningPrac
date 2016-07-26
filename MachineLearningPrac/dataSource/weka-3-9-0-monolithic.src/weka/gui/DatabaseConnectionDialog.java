/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.Frame;
/*   7:    */ import java.awt.GridLayout;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.WindowAdapter;
/*  11:    */ import java.awt.event.WindowEvent;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JDialog;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JPasswordField;
/*  19:    */ import javax.swing.JRootPane;
/*  20:    */ import javax.swing.JTextField;
/*  21:    */ 
/*  22:    */ public class DatabaseConnectionDialog
/*  23:    */   extends JDialog
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -1081946748666245054L;
/*  26:    */   protected JTextField m_DbaseURLText;
/*  27:    */   protected JLabel m_DbaseURLLab;
/*  28:    */   protected JTextField m_UserNameText;
/*  29:    */   protected JLabel m_UserNameLab;
/*  30:    */   protected JPasswordField m_PasswordText;
/*  31:    */   protected JLabel m_PasswordLab;
/*  32:    */   protected JCheckBox m_DebugCheckBox;
/*  33:    */   protected JLabel m_DebugLab;
/*  34:    */   protected int m_returnValue;
/*  35:    */   
/*  36:    */   public DatabaseConnectionDialog(Frame parentFrame)
/*  37:    */   {
/*  38: 79 */     this(parentFrame, "", "");
/*  39:    */   }
/*  40:    */   
/*  41:    */   public DatabaseConnectionDialog(Frame parentFrame, String url, String uname)
/*  42:    */   {
/*  43: 90 */     this(parentFrame, url, uname, true);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public DatabaseConnectionDialog(Frame parentFrame, String url, String uname, boolean debug)
/*  47:    */   {
/*  48:102 */     super(parentFrame, "Database Connection Parameters", true);
/*  49:103 */     DbConnectionDialog(url, uname, debug);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getURL()
/*  53:    */   {
/*  54:112 */     return this.m_DbaseURLText.getText();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String getUsername()
/*  58:    */   {
/*  59:121 */     return this.m_UserNameText.getText();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getPassword()
/*  63:    */   {
/*  64:130 */     return new String(this.m_PasswordText.getPassword());
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean getDebug()
/*  68:    */   {
/*  69:139 */     return this.m_DebugCheckBox.isSelected();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public int getReturnValue()
/*  73:    */   {
/*  74:148 */     return this.m_returnValue;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void DbConnectionDialog(String url, String uname)
/*  78:    */   {
/*  79:158 */     DbConnectionDialog(url, uname, true);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void DbConnectionDialog(String url, String uname, boolean debug)
/*  83:    */   {
/*  84:170 */     JPanel DbP = new JPanel();
/*  85:171 */     if (debug) {
/*  86:172 */       DbP.setLayout(new GridLayout(5, 1));
/*  87:    */     } else {
/*  88:174 */       DbP.setLayout(new GridLayout(4, 1));
/*  89:    */     }
/*  90:176 */     this.m_DbaseURLText = new JTextField(url, 50);
/*  91:177 */     this.m_DbaseURLLab = new JLabel(" Database URL", 2);
/*  92:178 */     this.m_DbaseURLLab.setFont(new Font("Monospaced", 0, 12));
/*  93:179 */     this.m_DbaseURLLab.setDisplayedMnemonic('D');
/*  94:180 */     this.m_DbaseURLLab.setLabelFor(this.m_DbaseURLText);
/*  95:    */     
/*  96:182 */     this.m_UserNameText = new JTextField(uname, 25);
/*  97:183 */     this.m_UserNameLab = new JLabel(" Username    ", 2);
/*  98:184 */     this.m_UserNameLab.setFont(new Font("Monospaced", 0, 12));
/*  99:185 */     this.m_UserNameLab.setDisplayedMnemonic('U');
/* 100:186 */     this.m_UserNameLab.setLabelFor(this.m_UserNameText);
/* 101:    */     
/* 102:188 */     this.m_PasswordText = new JPasswordField(25);
/* 103:189 */     this.m_PasswordLab = new JLabel(" Password    ", 2);
/* 104:190 */     this.m_PasswordLab.setFont(new Font("Monospaced", 0, 12));
/* 105:191 */     this.m_PasswordLab.setDisplayedMnemonic('P');
/* 106:192 */     this.m_PasswordLab.setLabelFor(this.m_PasswordText);
/* 107:    */     
/* 108:194 */     this.m_DebugCheckBox = new JCheckBox();
/* 109:195 */     this.m_DebugLab = new JLabel(" Debug       ", 2);
/* 110:196 */     this.m_DebugLab.setFont(new Font("Monospaced", 0, 12));
/* 111:197 */     this.m_DebugLab.setDisplayedMnemonic('P');
/* 112:198 */     this.m_DebugLab.setLabelFor(this.m_DebugCheckBox);
/* 113:    */     
/* 114:200 */     JPanel urlP = new JPanel();
/* 115:201 */     urlP.setLayout(new FlowLayout(0));
/* 116:202 */     urlP.add(this.m_DbaseURLLab);
/* 117:203 */     urlP.add(this.m_DbaseURLText);
/* 118:204 */     DbP.add(urlP);
/* 119:    */     
/* 120:206 */     JPanel usernameP = new JPanel();
/* 121:207 */     usernameP.setLayout(new FlowLayout(0));
/* 122:208 */     usernameP.add(this.m_UserNameLab);
/* 123:209 */     usernameP.add(this.m_UserNameText);
/* 124:210 */     DbP.add(usernameP);
/* 125:    */     
/* 126:212 */     JPanel passwordP = new JPanel();
/* 127:213 */     passwordP.setLayout(new FlowLayout(0));
/* 128:214 */     passwordP.add(this.m_PasswordLab);
/* 129:215 */     passwordP.add(this.m_PasswordText);
/* 130:216 */     DbP.add(passwordP);
/* 131:218 */     if (debug)
/* 132:    */     {
/* 133:219 */       JPanel debugP = new JPanel();
/* 134:220 */       debugP.setLayout(new FlowLayout(0));
/* 135:221 */       debugP.add(this.m_DebugLab);
/* 136:222 */       debugP.add(this.m_DebugCheckBox);
/* 137:223 */       DbP.add(debugP);
/* 138:    */     }
/* 139:226 */     JPanel buttonsP = new JPanel();
/* 140:227 */     buttonsP.setLayout(new FlowLayout());
/* 141:    */     JButton ok;
/* 142:229 */     buttonsP.add(ok = new JButton("OK"));
/* 143:    */     JButton cancel;
/* 144:230 */     buttonsP.add(cancel = new JButton("Cancel"));
/* 145:231 */     ok.setMnemonic('O');
/* 146:232 */     ok.addActionListener(new ActionListener()
/* 147:    */     {
/* 148:    */       public void actionPerformed(ActionEvent evt)
/* 149:    */       {
/* 150:234 */         DatabaseConnectionDialog.this.m_returnValue = 0;
/* 151:235 */         DatabaseConnectionDialog.this.dispose();
/* 152:    */       }
/* 153:237 */     });
/* 154:238 */     cancel.setMnemonic('C');
/* 155:239 */     cancel.addActionListener(new ActionListener()
/* 156:    */     {
/* 157:    */       public void actionPerformed(ActionEvent evt)
/* 158:    */       {
/* 159:241 */         DatabaseConnectionDialog.this.m_returnValue = -1;
/* 160:242 */         DatabaseConnectionDialog.this.dispose();
/* 161:    */       }
/* 162:246 */     });
/* 163:247 */     addWindowListener(new WindowAdapter()
/* 164:    */     {
/* 165:    */       public void windowClosing(WindowEvent e)
/* 166:    */       {
/* 167:249 */         System.err.println("Cancelled!!");
/* 168:250 */         DatabaseConnectionDialog.this.m_returnValue = -1;
/* 169:    */       }
/* 170:253 */     });
/* 171:254 */     DbP.add(buttonsP);
/* 172:255 */     getContentPane().add(DbP, "Center");
/* 173:256 */     pack();
/* 174:257 */     getRootPane().setDefaultButton(ok);
/* 175:258 */     setResizable(false);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public static void main(String[] args)
/* 179:    */   {
/* 180:265 */     DatabaseConnectionDialog dbd = new DatabaseConnectionDialog(null, "URL", "username");
/* 181:266 */     dbd.setVisible(true);
/* 182:267 */     System.out.println(dbd.getReturnValue() + ":" + dbd.getUsername() + ":" + dbd.getPassword() + ":" + dbd.getURL());
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.DatabaseConnectionDialog
 * JD-Core Version:    0.7.0.1
 */