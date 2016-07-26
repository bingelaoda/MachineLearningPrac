/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.Window;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import javax.swing.BorderFactory;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JCheckBox;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.JTextArea;
/*  14:    */ import weka.core.Environment;
/*  15:    */ import weka.core.EnvironmentHandler;
/*  16:    */ 
/*  17:    */ public class TextSaverCustomizer
/*  18:    */   extends JPanel
/*  19:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerClosingListener, CustomizerCloseRequester
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -1012433373647714743L;
/*  22:    */   private TextSaver m_textSaver;
/*  23:    */   private FileEnvironmentField m_fileEditor;
/*  24: 59 */   private final JCheckBox m_append = new JCheckBox("Append to file");
/*  25: 61 */   private Environment m_env = Environment.getSystemWide();
/*  26:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  27:    */   private Window m_parent;
/*  28:    */   private String m_fileBackup;
/*  29:    */   
/*  30:    */   public TextSaverCustomizer()
/*  31:    */   {
/*  32: 73 */     setLayout(new BorderLayout());
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setObject(Object object)
/*  36:    */   {
/*  37: 84 */     this.m_textSaver = ((TextSaver)object);
/*  38: 85 */     this.m_fileBackup = this.m_textSaver.getFilename();
/*  39: 86 */     this.m_append.setSelected(this.m_textSaver.getAppend());
/*  40:    */     
/*  41: 88 */     setup();
/*  42:    */   }
/*  43:    */   
/*  44:    */   private void setup()
/*  45:    */   {
/*  46: 92 */     JPanel holder = new JPanel();
/*  47: 93 */     holder.setLayout(new BorderLayout());
/*  48:    */     
/*  49: 95 */     this.m_fileEditor = new FileEnvironmentField("Filename", this.m_env, 1);
/*  50:    */     
/*  51: 97 */     this.m_fileEditor.resetFileFilters();
/*  52: 98 */     JPanel temp = new JPanel();
/*  53: 99 */     temp.setLayout(new GridLayout(2, 0));
/*  54:100 */     temp.add(this.m_fileEditor);
/*  55:101 */     temp.add(this.m_append);
/*  56:    */     
/*  57:103 */     holder.add(temp, "South");
/*  58:    */     
/*  59:105 */     String globalInfo = this.m_textSaver.globalInfo();
/*  60:    */     
/*  61:107 */     JTextArea jt = new JTextArea();
/*  62:108 */     jt.setColumns(30);
/*  63:109 */     jt.setFont(new Font("SansSerif", 0, 12));
/*  64:110 */     jt.setEditable(false);
/*  65:111 */     jt.setLineWrap(true);
/*  66:112 */     jt.setWrapStyleWord(true);
/*  67:113 */     jt.setText(globalInfo);
/*  68:114 */     jt.setBackground(getBackground());
/*  69:115 */     JPanel jp = new JPanel();
/*  70:116 */     jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/*  71:    */     
/*  72:    */ 
/*  73:119 */     jp.setLayout(new BorderLayout());
/*  74:120 */     jp.add(jt, "Center");
/*  75:    */     
/*  76:122 */     holder.add(jp, "North");
/*  77:    */     
/*  78:124 */     add(holder, "Center");
/*  79:    */     
/*  80:126 */     addButtons();
/*  81:    */     
/*  82:128 */     this.m_fileEditor.setText(this.m_textSaver.getFilename());
/*  83:    */   }
/*  84:    */   
/*  85:    */   private void addButtons()
/*  86:    */   {
/*  87:132 */     JButton okBut = new JButton("OK");
/*  88:133 */     JButton cancelBut = new JButton("Cancel");
/*  89:    */     
/*  90:135 */     JPanel butHolder = new JPanel();
/*  91:136 */     butHolder.setLayout(new GridLayout(1, 2));
/*  92:137 */     butHolder.add(okBut);
/*  93:138 */     butHolder.add(cancelBut);
/*  94:139 */     add(butHolder, "South");
/*  95:    */     
/*  96:141 */     okBut.addActionListener(new ActionListener()
/*  97:    */     {
/*  98:    */       public void actionPerformed(ActionEvent e)
/*  99:    */       {
/* 100:144 */         TextSaverCustomizer.this.m_textSaver.setFilename(TextSaverCustomizer.this.m_fileEditor.getText());
/* 101:145 */         TextSaverCustomizer.this.m_textSaver.setAppend(TextSaverCustomizer.this.m_append.isSelected());
/* 102:147 */         if (TextSaverCustomizer.this.m_modifyListener != null) {
/* 103:148 */           TextSaverCustomizer.this.m_modifyListener.setModifiedStatus(TextSaverCustomizer.this, true);
/* 104:    */         }
/* 105:150 */         if (TextSaverCustomizer.this.m_parent != null) {
/* 106:151 */           TextSaverCustomizer.this.m_parent.dispose();
/* 107:    */         }
/* 108:    */       }
/* 109:155 */     });
/* 110:156 */     cancelBut.addActionListener(new ActionListener()
/* 111:    */     {
/* 112:    */       public void actionPerformed(ActionEvent e)
/* 113:    */       {
/* 114:160 */         TextSaverCustomizer.this.customizerClosing();
/* 115:161 */         if (TextSaverCustomizer.this.m_parent != null) {
/* 116:162 */           TextSaverCustomizer.this.m_parent.dispose();
/* 117:    */         }
/* 118:    */       }
/* 119:    */     });
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void setEnvironment(Environment env)
/* 123:    */   {
/* 124:175 */     this.m_env = env;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 128:    */   {
/* 129:186 */     this.m_modifyListener = l;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setParentWindow(Window parent)
/* 133:    */   {
/* 134:196 */     this.m_parent = parent;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void customizerClosing()
/* 138:    */   {
/* 139:205 */     this.m_textSaver.setFilename(this.m_fileBackup);
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TextSaverCustomizer
 * JD-Core Version:    0.7.0.1
 */