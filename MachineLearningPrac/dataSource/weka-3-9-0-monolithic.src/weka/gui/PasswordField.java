/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.awt.event.FocusAdapter;
/*   8:    */ import java.awt.event.FocusEvent;
/*   9:    */ import java.awt.event.KeyAdapter;
/*  10:    */ import java.awt.event.KeyEvent;
/*  11:    */ import java.beans.PropertyChangeListener;
/*  12:    */ import java.beans.PropertyChangeSupport;
/*  13:    */ import java.beans.PropertyEditor;
/*  14:    */ import javax.swing.BorderFactory;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JPasswordField;
/*  18:    */ 
/*  19:    */ public class PasswordField
/*  20:    */   extends JPanel
/*  21:    */   implements PropertyEditor, CustomPanelSupplier
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 8180782063577036194L;
/*  24:    */   protected JPasswordField m_password;
/*  25:    */   protected JLabel m_label;
/*  26: 51 */   protected PropertyChangeSupport m_support = new PropertyChangeSupport(this);
/*  27:    */   
/*  28:    */   public PasswordField()
/*  29:    */   {
/*  30: 54 */     this("");
/*  31:    */   }
/*  32:    */   
/*  33:    */   public PasswordField(String label)
/*  34:    */   {
/*  35: 58 */     setLayout(new BorderLayout());
/*  36: 59 */     this.m_label = new JLabel(label);
/*  37: 61 */     if (label.length() > 0) {
/*  38: 62 */       this.m_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
/*  39:    */     }
/*  40: 64 */     add(this.m_label, "West");
/*  41:    */     
/*  42: 66 */     this.m_password = new JPasswordField();
/*  43: 67 */     this.m_password.addKeyListener(new KeyAdapter()
/*  44:    */     {
/*  45:    */       public void keyReleased(KeyEvent e)
/*  46:    */       {
/*  47: 70 */         super.keyReleased(e);
/*  48: 71 */         PasswordField.this.m_support.firePropertyChange("", null, null);
/*  49:    */       }
/*  50: 73 */     });
/*  51: 74 */     this.m_password.addFocusListener(new FocusAdapter()
/*  52:    */     {
/*  53:    */       public void focusLost(FocusEvent e)
/*  54:    */       {
/*  55: 77 */         super.focusLost(e);
/*  56: 78 */         PasswordField.this.m_support.firePropertyChange("", null, null);
/*  57:    */       }
/*  58: 81 */     });
/*  59: 82 */     add(this.m_password, "Center");
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setLabel(String label)
/*  63:    */   {
/*  64: 92 */     this.m_label.setText(label);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String getText()
/*  68:    */   {
/*  69: 96 */     return new String(this.m_password.getPassword());
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setText(String text)
/*  73:    */   {
/*  74:100 */     this.m_password.setText(text);
/*  75:101 */     this.m_support.firePropertyChange("", null, null);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public JPanel getCustomPanel()
/*  79:    */   {
/*  80:106 */     return this;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Object getValue()
/*  84:    */   {
/*  85:111 */     return getAsText();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setValue(Object value)
/*  89:    */   {
/*  90:116 */     setAsText(value.toString());
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean isPaintable()
/*  94:    */   {
/*  95:121 */     return true;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void paintValue(Graphics gfx, Rectangle box) {}
/*  99:    */   
/* 100:    */   public String getJavaInitializationString()
/* 101:    */   {
/* 102:131 */     return null;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String getAsText()
/* 106:    */   {
/* 107:136 */     return getText();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setAsText(String text)
/* 111:    */     throws IllegalArgumentException
/* 112:    */   {
/* 113:141 */     setText(text);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String[] getTags()
/* 117:    */   {
/* 118:146 */     return null;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public Component getCustomEditor()
/* 122:    */   {
/* 123:151 */     return this;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public boolean supportsCustomEditor()
/* 127:    */   {
/* 128:156 */     return true;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 132:    */   {
/* 133:161 */     if ((pcl != null) && (this.m_support != null)) {
/* 134:162 */       this.m_support.addPropertyChangeListener(pcl);
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 139:    */   {
/* 140:168 */     if ((pcl != null) && (this.m_support != null)) {
/* 141:169 */       this.m_support.removePropertyChangeListener(pcl);
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void setEnabled(boolean enabled)
/* 146:    */   {
/* 147:180 */     this.m_password.setEnabled(enabled);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setEditable(boolean editable)
/* 151:    */   {
/* 152:189 */     this.m_password.setEditable(editable);
/* 153:    */   }
/* 154:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PasswordField
 * JD-Core Version:    0.7.0.1
 */