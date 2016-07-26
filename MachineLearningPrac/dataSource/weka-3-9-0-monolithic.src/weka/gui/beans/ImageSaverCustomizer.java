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
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import javax.swing.JTextArea;
/*  13:    */ import weka.core.Environment;
/*  14:    */ import weka.core.EnvironmentHandler;
/*  15:    */ 
/*  16:    */ public class ImageSaverCustomizer
/*  17:    */   extends JPanel
/*  18:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerClosingListener, CustomizerCloseRequester
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 5215477777077643368L;
/*  21:    */   private ImageSaver m_imageSaver;
/*  22:    */   private FileEnvironmentField m_fileEditor;
/*  23: 58 */   private Environment m_env = Environment.getSystemWide();
/*  24:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  25:    */   private Window m_parent;
/*  26:    */   private String m_fileBackup;
/*  27:    */   
/*  28:    */   public ImageSaverCustomizer()
/*  29:    */   {
/*  30: 70 */     setLayout(new BorderLayout());
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setObject(Object object)
/*  34:    */   {
/*  35: 80 */     this.m_imageSaver = ((ImageSaver)object);
/*  36: 81 */     this.m_fileBackup = this.m_imageSaver.getFilename();
/*  37:    */     
/*  38: 83 */     setup();
/*  39:    */   }
/*  40:    */   
/*  41:    */   private void setup()
/*  42:    */   {
/*  43: 87 */     JPanel holder = new JPanel();
/*  44: 88 */     holder.setLayout(new BorderLayout());
/*  45:    */     
/*  46: 90 */     this.m_fileEditor = new FileEnvironmentField("Filename", this.m_env, 1);
/*  47:    */     
/*  48: 92 */     this.m_fileEditor.resetFileFilters();
/*  49: 93 */     holder.add(this.m_fileEditor, "South");
/*  50:    */     
/*  51: 95 */     String globalInfo = this.m_imageSaver.globalInfo();
/*  52:    */     
/*  53: 97 */     JTextArea jt = new JTextArea();
/*  54: 98 */     jt.setColumns(30);
/*  55: 99 */     jt.setFont(new Font("SansSerif", 0, 12));
/*  56:100 */     jt.setEditable(false);
/*  57:101 */     jt.setLineWrap(true);
/*  58:102 */     jt.setWrapStyleWord(true);
/*  59:103 */     jt.setText(globalInfo);
/*  60:104 */     jt.setBackground(getBackground());
/*  61:105 */     JPanel jp = new JPanel();
/*  62:106 */     jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:110 */     jp.setLayout(new BorderLayout());
/*  67:111 */     jp.add(jt, "Center");
/*  68:    */     
/*  69:113 */     holder.add(jp, "North");
/*  70:    */     
/*  71:115 */     add(holder, "Center");
/*  72:    */     
/*  73:117 */     addButtons();
/*  74:    */     
/*  75:119 */     this.m_fileEditor.setText(this.m_imageSaver.getFilename());
/*  76:    */   }
/*  77:    */   
/*  78:    */   private void addButtons()
/*  79:    */   {
/*  80:123 */     JButton okBut = new JButton("OK");
/*  81:124 */     JButton cancelBut = new JButton("Cancel");
/*  82:    */     
/*  83:126 */     JPanel butHolder = new JPanel();
/*  84:127 */     butHolder.setLayout(new GridLayout(1, 2));
/*  85:128 */     butHolder.add(okBut);butHolder.add(cancelBut);
/*  86:129 */     add(butHolder, "South");
/*  87:    */     
/*  88:131 */     okBut.addActionListener(new ActionListener()
/*  89:    */     {
/*  90:    */       public void actionPerformed(ActionEvent e)
/*  91:    */       {
/*  92:133 */         ImageSaverCustomizer.this.m_imageSaver.setFilename(ImageSaverCustomizer.this.m_fileEditor.getText());
/*  93:135 */         if (ImageSaverCustomizer.this.m_modifyListener != null) {
/*  94:136 */           ImageSaverCustomizer.this.m_modifyListener.setModifiedStatus(ImageSaverCustomizer.this, true);
/*  95:    */         }
/*  96:139 */         if (ImageSaverCustomizer.this.m_parent != null) {
/*  97:140 */           ImageSaverCustomizer.this.m_parent.dispose();
/*  98:    */         }
/*  99:    */       }
/* 100:144 */     });
/* 101:145 */     cancelBut.addActionListener(new ActionListener()
/* 102:    */     {
/* 103:    */       public void actionPerformed(ActionEvent e)
/* 104:    */       {
/* 105:148 */         ImageSaverCustomizer.this.customizerClosing();
/* 106:149 */         if (ImageSaverCustomizer.this.m_parent != null) {
/* 107:150 */           ImageSaverCustomizer.this.m_parent.dispose();
/* 108:    */         }
/* 109:    */       }
/* 110:    */     });
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setEnvironment(Environment env)
/* 114:    */   {
/* 115:162 */     this.m_env = env;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 119:    */   {
/* 120:172 */     this.m_modifyListener = l;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setParentWindow(Window parent)
/* 124:    */   {
/* 125:181 */     this.m_parent = parent;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void customizerClosing()
/* 129:    */   {
/* 130:190 */     this.m_imageSaver.setFilename(this.m_fileBackup);
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ImageSaverCustomizer
 * JD-Core Version:    0.7.0.1
 */