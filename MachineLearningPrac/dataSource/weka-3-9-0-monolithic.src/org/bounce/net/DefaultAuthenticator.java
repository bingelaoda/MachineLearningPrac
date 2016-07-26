/*   1:    */ package org.bounce.net;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.FlowLayout;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.WindowAdapter;
/*  10:    */ import java.awt.event.WindowEvent;
/*  11:    */ import java.net.Authenticator;
/*  12:    */ import java.net.PasswordAuthentication;
/*  13:    */ import javax.swing.Box;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JDialog;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JPasswordField;
/*  20:    */ import javax.swing.JRootPane;
/*  21:    */ import javax.swing.JTextField;
/*  22:    */ import javax.swing.border.EmptyBorder;
/*  23:    */ import org.bounce.FormLayout;
/*  24:    */ 
/*  25:    */ public class DefaultAuthenticator
/*  26:    */   extends Authenticator
/*  27:    */ {
/*  28: 65 */   protected String TITLE = "Enter Username and Password";
/*  29: 66 */   protected String DESCRIPTION = "Please provide your username and password.";
/*  30: 67 */   protected String USERNAME = "Username:";
/*  31: 68 */   protected String PASSWORD = "Password:";
/*  32: 69 */   protected String HOST = "Site:";
/*  33: 70 */   protected String REALM = "Realm:";
/*  34: 71 */   protected String OK_BUTTON = "OK";
/*  35: 72 */   protected String CANCEL_BUTTON = "Cancel";
/*  36: 74 */   private JDialog dialog = null;
/*  37: 76 */   private boolean okPressed = false;
/*  38: 78 */   private JFrame parent = null;
/*  39: 80 */   private JPasswordField passwordField = null;
/*  40: 81 */   private JTextField usernameField = null;
/*  41: 82 */   private JLabel hostField = null;
/*  42: 83 */   private JLabel realmField = null;
/*  43:    */   
/*  44:    */   public DefaultAuthenticator(JFrame paramJFrame)
/*  45:    */   {
/*  46: 91 */     this.parent = paramJFrame;
/*  47:    */   }
/*  48:    */   
/*  49:    */   private JDialog getDialog()
/*  50:    */   {
/*  51: 95 */     if (this.dialog == null)
/*  52:    */     {
/*  53: 96 */       Dimension localDimension = new Dimension(350, 210);
/*  54:    */       
/*  55: 98 */       this.dialog = new JDialog(this.parent);
/*  56: 99 */       this.dialog.setTitle(this.TITLE);
/*  57:100 */       this.dialog.setModal(true);
/*  58:101 */       this.dialog.setResizable(false);
/*  59:    */       
/*  60:103 */       JPanel localJPanel1 = new JPanel(new FormLayout(5, 5));
/*  61:104 */       localJPanel1.setBorder(new EmptyBorder(10, 10, 10, 10));
/*  62:    */       
/*  63:106 */       JLabel localJLabel = new JLabel(this.DESCRIPTION);
/*  64:107 */       localJLabel.setFont(localJLabel.getFont().deriveFont(0));
/*  65:    */       
/*  66:109 */       localJPanel1.add(localJLabel, FormLayout.FULL_FILL);
/*  67:    */       
/*  68:111 */       localJPanel1.add(Box.createVerticalStrut(10), FormLayout.FULL_FILL);
/*  69:    */       
/*  70:113 */       localJPanel1.add(new JLabel(this.HOST), FormLayout.LEFT);
/*  71:114 */       this.hostField = new JLabel();
/*  72:115 */       this.hostField.setFont(this.hostField.getFont().deriveFont(0));
/*  73:116 */       localJPanel1.add(this.hostField, FormLayout.RIGHT_FILL);
/*  74:    */       
/*  75:118 */       localJPanel1.add(new JLabel(this.REALM), FormLayout.LEFT);
/*  76:119 */       this.realmField = new JLabel();
/*  77:120 */       this.realmField.setFont(this.realmField.getFont().deriveFont(0));
/*  78:121 */       localJPanel1.add(this.realmField, FormLayout.RIGHT_FILL);
/*  79:    */       
/*  80:123 */       localJPanel1.add(new JLabel(this.USERNAME), FormLayout.LEFT);
/*  81:124 */       this.usernameField = new JTextField();
/*  82:125 */       localJPanel1.add(this.usernameField, FormLayout.RIGHT_FILL);
/*  83:    */       
/*  84:127 */       localJPanel1.add(new JLabel(this.PASSWORD), FormLayout.LEFT);
/*  85:128 */       this.passwordField = new JPasswordField();
/*  86:129 */       localJPanel1.add(this.passwordField, FormLayout.RIGHT_FILL);
/*  87:    */       
/*  88:131 */       JButton localJButton1 = new JButton(this.CANCEL_BUTTON);
/*  89:132 */       localJButton1.setMnemonic('C');
/*  90:133 */       localJButton1.setFont(localJButton1.getFont().deriveFont(0));
/*  91:134 */       localJButton1.addActionListener(new ActionListener()
/*  92:    */       {
/*  93:    */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/*  94:    */         {
/*  95:137 */           DefaultAuthenticator.this.okPressed = false;
/*  96:138 */           DefaultAuthenticator.this.dialog.setVisible(false);
/*  97:    */         }
/*  98:141 */       });
/*  99:142 */       JButton localJButton2 = new JButton(this.OK_BUTTON);
/* 100:143 */       localJButton2.setMnemonic('O');
/* 101:144 */       localJButton2.addActionListener(new ActionListener()
/* 102:    */       {
/* 103:    */         public void actionPerformed(ActionEvent paramAnonymousActionEvent)
/* 104:    */         {
/* 105:147 */           DefaultAuthenticator.this.okPressed = true;
/* 106:148 */           DefaultAuthenticator.this.dialog.setVisible(false);
/* 107:    */         }
/* 108:151 */       });
/* 109:152 */       this.dialog.getRootPane().setDefaultButton(localJButton2);
/* 110:    */       
/* 111:154 */       JPanel localJPanel2 = new JPanel(new FlowLayout(1, 0, 0));
/* 112:155 */       localJPanel2.setBorder(new EmptyBorder(10, 0, 3, 0));
/* 113:156 */       localJPanel2.add(localJButton2);
/* 114:157 */       localJPanel2.add(localJButton1);
/* 115:    */       
/* 116:159 */       JPanel localJPanel3 = new JPanel(new BorderLayout());
/* 117:    */       
/* 118:161 */       localJPanel3.add(localJPanel1, "Center");
/* 119:162 */       localJPanel3.add(localJPanel2, "South");
/* 120:    */       
/* 121:164 */       this.dialog.addWindowListener(new WindowAdapter()
/* 122:    */       {
/* 123:    */         public void windowClosing(WindowEvent paramAnonymousWindowEvent)
/* 124:    */         {
/* 125:167 */           DefaultAuthenticator.this.okPressed = false;
/* 126:168 */           DefaultAuthenticator.this.dialog.setVisible(false);
/* 127:    */         }
/* 128:171 */       });
/* 129:172 */       this.dialog.setContentPane(localJPanel3);
/* 130:173 */       this.dialog.setDefaultCloseOperation(1);
/* 131:174 */       this.dialog.setSize(localDimension);
/* 132:    */     }
/* 133:177 */     return this.dialog;
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected PasswordAuthentication getPasswordAuthentication()
/* 137:    */   {
/* 138:187 */     JDialog localJDialog = getDialog();
/* 139:    */     
/* 140:189 */     this.passwordField.setText("");
/* 141:190 */     this.usernameField.setText("");
/* 142:191 */     this.hostField.setText(getRequestingHost());
/* 143:192 */     this.realmField.setText(getRequestingPrompt());
/* 144:    */     
/* 145:194 */     localJDialog.setLocationRelativeTo(this.parent);
/* 146:195 */     localJDialog.setVisible(true);
/* 147:197 */     if (this.okPressed) {
/* 148:198 */       return new PasswordAuthentication(this.usernameField.getText(), this.passwordField.getPassword());
/* 149:    */     }
/* 150:202 */     return null;
/* 151:    */   }
/* 152:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.bounce.net.DefaultAuthenticator
 * JD-Core Version:    0.7.0.1
 */