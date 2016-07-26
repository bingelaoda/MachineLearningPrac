/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.beans.PropertyChangeListener;
/*  10:    */ import java.beans.PropertyChangeSupport;
/*  11:    */ import java.beans.PropertyEditor;
/*  12:    */ import java.text.SimpleDateFormat;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JLabel;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JTextField;
/*  17:    */ import javax.swing.event.DocumentEvent;
/*  18:    */ import javax.swing.event.DocumentListener;
/*  19:    */ import javax.swing.text.Document;
/*  20:    */ 
/*  21:    */ public class SimpleDateFormatEditor
/*  22:    */   implements PropertyEditor
/*  23:    */ {
/*  24:    */   public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
/*  25:    */   private SimpleDateFormat m_Format;
/*  26:    */   private PropertyChangeSupport m_propSupport;
/*  27:    */   private CustomEditor m_customEditor;
/*  28:    */   
/*  29:    */   private class CustomEditor
/*  30:    */     extends JPanel
/*  31:    */     implements ActionListener, DocumentListener
/*  32:    */   {
/*  33:    */     private static final long serialVersionUID = -4018834274636309987L;
/*  34:    */     private JTextField m_FormatText;
/*  35:    */     private JButton m_DefaultButton;
/*  36:    */     private JButton m_ApplyButton;
/*  37:    */     
/*  38:    */     public CustomEditor()
/*  39:    */     {
/*  40: 88 */       this.m_FormatText = new JTextField(20);
/*  41: 89 */       this.m_DefaultButton = new JButton("Default");
/*  42: 90 */       this.m_ApplyButton = new JButton("Apply");
/*  43:    */       
/*  44: 92 */       this.m_DefaultButton.setMnemonic('D');
/*  45: 93 */       this.m_ApplyButton.setMnemonic('A');
/*  46:    */       
/*  47: 95 */       this.m_FormatText.getDocument().addDocumentListener(this);
/*  48: 96 */       this.m_DefaultButton.addActionListener(this);
/*  49: 97 */       this.m_ApplyButton.addActionListener(this);
/*  50:    */       
/*  51: 99 */       setLayout(new FlowLayout());
/*  52:100 */       add(new JLabel("ISO 8601 Date format"));
/*  53:101 */       add(this.m_FormatText);
/*  54:102 */       add(this.m_DefaultButton);
/*  55:103 */       add(this.m_ApplyButton);
/*  56:    */     }
/*  57:    */     
/*  58:    */     public void actionPerformed(ActionEvent e)
/*  59:    */     {
/*  60:112 */       if (e.getSource() == this.m_DefaultButton) {
/*  61:113 */         defaultFormat();
/*  62:114 */       } else if (e.getSource() == this.m_ApplyButton) {
/*  63:115 */         applyFormat();
/*  64:    */       }
/*  65:    */     }
/*  66:    */     
/*  67:    */     public void defaultFormat()
/*  68:    */     {
/*  69:122 */       this.m_FormatText.setText("yyyy-MM-dd'T'HH:mm:ss");
/*  70:123 */       formatChanged();
/*  71:    */     }
/*  72:    */     
/*  73:    */     protected boolean isValidFormat()
/*  74:    */     {
/*  75:132 */       boolean result = false;
/*  76:    */       try
/*  77:    */       {
/*  78:135 */         new SimpleDateFormat(this.m_FormatText.getText());
/*  79:136 */         result = true;
/*  80:    */       }
/*  81:    */       catch (Exception e) {}
/*  82:142 */       return result;
/*  83:    */     }
/*  84:    */     
/*  85:    */     public void applyFormat()
/*  86:    */     {
/*  87:149 */       if (isValidFormat())
/*  88:    */       {
/*  89:150 */         SimpleDateFormatEditor.this.m_Format = new SimpleDateFormat(this.m_FormatText.getText());
/*  90:151 */         SimpleDateFormatEditor.this.m_propSupport.firePropertyChange(null, null, null);
/*  91:    */       }
/*  92:    */       else
/*  93:    */       {
/*  94:154 */         throw new IllegalArgumentException("Date format '" + this.m_FormatText.getText() + "' is invalid! Cannot execute applyFormat!");
/*  95:    */       }
/*  96:    */     }
/*  97:    */     
/*  98:    */     public void formatChanged()
/*  99:    */     {
/* 100:165 */       this.m_FormatText.setText(SimpleDateFormatEditor.this.m_Format.toPattern());
/* 101:166 */       SimpleDateFormatEditor.this.m_propSupport.firePropertyChange(null, null, null);
/* 102:    */     }
/* 103:    */     
/* 104:    */     public void changedUpdate(DocumentEvent e)
/* 105:    */     {
/* 106:173 */       this.m_ApplyButton.setEnabled(isValidFormat());
/* 107:    */     }
/* 108:    */     
/* 109:    */     public void insertUpdate(DocumentEvent e)
/* 110:    */     {
/* 111:180 */       this.m_ApplyButton.setEnabled(isValidFormat());
/* 112:    */     }
/* 113:    */     
/* 114:    */     public void removeUpdate(DocumentEvent e)
/* 115:    */     {
/* 116:187 */       this.m_ApplyButton.setEnabled(isValidFormat());
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public SimpleDateFormatEditor()
/* 121:    */   {
/* 122:196 */     this.m_propSupport = new PropertyChangeSupport(this);
/* 123:197 */     this.m_customEditor = new CustomEditor();
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setValue(Object value)
/* 127:    */   {
/* 128:206 */     this.m_Format = ((SimpleDateFormat)value);
/* 129:207 */     this.m_customEditor.formatChanged();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Object getValue()
/* 133:    */   {
/* 134:216 */     return this.m_Format;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean isPaintable()
/* 138:    */   {
/* 139:226 */     return true;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void paintValue(Graphics gfx, Rectangle box)
/* 143:    */   {
/* 144:238 */     gfx.drawString(this.m_Format.toPattern(), box.x, box.y + box.height);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String getJavaInitializationString()
/* 148:    */   {
/* 149:247 */     return "new SimpleDateFormat(" + this.m_Format.toPattern() + ")";
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String getAsText()
/* 153:    */   {
/* 154:256 */     return this.m_Format.toPattern();
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setAsText(String text)
/* 158:    */   {
/* 159:265 */     this.m_Format = new SimpleDateFormat(text);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String[] getTags()
/* 163:    */   {
/* 164:274 */     return null;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Component getCustomEditor()
/* 168:    */   {
/* 169:283 */     return this.m_customEditor;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public boolean supportsCustomEditor()
/* 173:    */   {
/* 174:292 */     return true;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void addPropertyChangeListener(PropertyChangeListener listener)
/* 178:    */   {
/* 179:302 */     this.m_propSupport.addPropertyChangeListener(listener);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void removePropertyChangeListener(PropertyChangeListener listener)
/* 183:    */   {
/* 184:312 */     this.m_propSupport.removePropertyChangeListener(listener);
/* 185:    */   }
/* 186:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SimpleDateFormatEditor
 * JD-Core Version:    0.7.0.1
 */