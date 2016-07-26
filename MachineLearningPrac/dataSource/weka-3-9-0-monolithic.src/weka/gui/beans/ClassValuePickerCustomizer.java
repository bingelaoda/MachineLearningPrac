/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.DefaultComboBoxModel;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JComboBox;
/*  14:    */ import javax.swing.JLabel;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JTextField;
/*  17:    */ import weka.core.Attribute;
/*  18:    */ import weka.core.Instances;
/*  19:    */ 
/*  20:    */ public class ClassValuePickerCustomizer
/*  21:    */   extends JPanel
/*  22:    */   implements BeanCustomizer, CustomizerClosingListener, CustomizerCloseRequester
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 8213423053861600469L;
/*  25: 56 */   private boolean m_displayValNames = false;
/*  26:    */   private ClassValuePicker m_classValuePicker;
/*  27: 60 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  28: 63 */   private final JComboBox m_ClassValueCombo = new EnvironmentField.WideComboBox();
/*  29: 65 */   private final JPanel m_holderP = new JPanel();
/*  30: 67 */   private final JLabel m_messageLabel = new JLabel("No customization possible at present.");
/*  31:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  32: 71 */   private boolean m_modified = false;
/*  33:    */   private Window m_parent;
/*  34:    */   private String m_backup;
/*  35: 76 */   private boolean m_textBoxEntryMode = false;
/*  36:    */   private JTextField m_valueTextBox;
/*  37:    */   
/*  38:    */   public ClassValuePickerCustomizer()
/*  39:    */   {
/*  40: 81 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  41: 82 */     this.m_ClassValueCombo.setEditable(true);
/*  42: 83 */     this.m_ClassValueCombo.setToolTipText("Class label. /first, /last and /<num> can be used to specify the first, last or specific index of the label to use respectively.");
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46: 87 */     setLayout(new BorderLayout());
/*  47: 88 */     add(new JLabel("ClassValuePickerCustomizer"), "North");
/*  48:    */     
/*  49: 90 */     this.m_holderP.setLayout(new BorderLayout());
/*  50: 91 */     this.m_holderP.setBorder(BorderFactory.createTitledBorder("Choose class value"));
/*  51: 92 */     this.m_holderP.setToolTipText("Class label. /first, /last and /<num> can be used to specify the first, last or specific index of the label to use respectively.");
/*  52:    */     
/*  53:    */ 
/*  54: 95 */     this.m_holderP.add(this.m_ClassValueCombo, "Center");
/*  55: 96 */     this.m_ClassValueCombo.addActionListener(new ActionListener()
/*  56:    */     {
/*  57:    */       public void actionPerformed(ActionEvent e)
/*  58:    */       {
/*  59: 99 */         if (ClassValuePickerCustomizer.this.m_classValuePicker != null)
/*  60:    */         {
/*  61:100 */           ClassValuePickerCustomizer.this.m_classValuePicker.setClassValue(ClassValuePickerCustomizer.this.m_ClassValueCombo.getSelectedItem().toString());
/*  62:    */           
/*  63:102 */           ClassValuePickerCustomizer.this.m_modified = true;
/*  64:    */         }
/*  65:    */       }
/*  66:106 */     });
/*  67:107 */     add(this.m_messageLabel, "Center");
/*  68:108 */     addButtons();
/*  69:    */   }
/*  70:    */   
/*  71:    */   private void addButtons()
/*  72:    */   {
/*  73:112 */     JButton okBut = new JButton("OK");
/*  74:113 */     JButton cancelBut = new JButton("Cancel");
/*  75:    */     
/*  76:115 */     JPanel butHolder = new JPanel();
/*  77:116 */     butHolder.setLayout(new GridLayout(1, 2));
/*  78:117 */     butHolder.add(okBut);
/*  79:118 */     butHolder.add(cancelBut);
/*  80:119 */     add(butHolder, "South");
/*  81:    */     
/*  82:121 */     okBut.addActionListener(new ActionListener()
/*  83:    */     {
/*  84:    */       public void actionPerformed(ActionEvent e)
/*  85:    */       {
/*  86:124 */         if (ClassValuePickerCustomizer.this.m_modifyListener != null) {
/*  87:125 */           ClassValuePickerCustomizer.this.m_modifyListener.setModifiedStatus(ClassValuePickerCustomizer.this, ClassValuePickerCustomizer.this.m_modified);
/*  88:    */         }
/*  89:129 */         if (ClassValuePickerCustomizer.this.m_textBoxEntryMode) {
/*  90:130 */           ClassValuePickerCustomizer.this.m_classValuePicker.setClassValue(ClassValuePickerCustomizer.this.m_valueTextBox.getText().trim());
/*  91:    */         }
/*  92:133 */         if (ClassValuePickerCustomizer.this.m_parent != null) {
/*  93:134 */           ClassValuePickerCustomizer.this.m_parent.dispose();
/*  94:    */         }
/*  95:    */       }
/*  96:138 */     });
/*  97:139 */     cancelBut.addActionListener(new ActionListener()
/*  98:    */     {
/*  99:    */       public void actionPerformed(ActionEvent e)
/* 100:    */       {
/* 101:142 */         ClassValuePickerCustomizer.this.m_classValuePicker.setClassValue(ClassValuePickerCustomizer.this.m_backup);
/* 102:    */         
/* 103:144 */         ClassValuePickerCustomizer.this.customizerClosing();
/* 104:145 */         if (ClassValuePickerCustomizer.this.m_parent != null) {
/* 105:146 */           ClassValuePickerCustomizer.this.m_parent.dispose();
/* 106:    */         }
/* 107:    */       }
/* 108:    */     });
/* 109:    */   }
/* 110:    */   
/* 111:    */   private void setupTextBoxSelection()
/* 112:    */   {
/* 113:153 */     this.m_textBoxEntryMode = true;
/* 114:    */     
/* 115:155 */     JPanel holderPanel = new JPanel();
/* 116:156 */     holderPanel.setLayout(new BorderLayout());
/* 117:157 */     holderPanel.setBorder(BorderFactory.createTitledBorder("Specify class label"));
/* 118:    */     
/* 119:159 */     JLabel label = new JLabel("Class label ", 4);
/* 120:160 */     holderPanel.add(label, "West");
/* 121:161 */     this.m_valueTextBox = new JTextField(15);
/* 122:162 */     this.m_valueTextBox.setToolTipText("Class label. /first, /last and /<num> can be used to specify the first, last or specific index of the label to use respectively.");
/* 123:    */     
/* 124:    */ 
/* 125:    */ 
/* 126:166 */     holderPanel.add(this.m_valueTextBox, "Center");
/* 127:167 */     JPanel holder2 = new JPanel();
/* 128:168 */     holder2.setLayout(new BorderLayout());
/* 129:169 */     holder2.add(holderPanel, "North");
/* 130:170 */     add(holder2, "Center");
/* 131:171 */     String existingClassVal = this.m_classValuePicker.getClassValue();
/* 132:172 */     if (existingClassVal != null) {
/* 133:173 */       this.m_valueTextBox.setText(existingClassVal);
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   private void setUpValueSelection(Instances format)
/* 138:    */   {
/* 139:178 */     if ((format.classIndex() < 0) || (format.classAttribute().isNumeric()))
/* 140:    */     {
/* 141:180 */       this.m_messageLabel.setText(format.classIndex() < 0 ? "EROR: no class attribute set" : "ERROR: class is numeric");
/* 142:    */       
/* 143:182 */       return;
/* 144:    */     }
/* 145:185 */     if (!this.m_displayValNames) {
/* 146:186 */       remove(this.m_messageLabel);
/* 147:    */     }
/* 148:189 */     this.m_textBoxEntryMode = false;
/* 149:191 */     if (format.classAttribute().numValues() == 0)
/* 150:    */     {
/* 151:197 */       setupTextBoxSelection();
/* 152:198 */       validate();
/* 153:199 */       repaint();
/* 154:200 */       return;
/* 155:    */     }
/* 156:203 */     String existingClassVal = this.m_classValuePicker.getClassValue();
/* 157:204 */     String existingCopy = existingClassVal;
/* 158:205 */     if (existingClassVal == null) {
/* 159:206 */       existingClassVal = "";
/* 160:    */     }
/* 161:208 */     int classValIndex = format.classAttribute().indexOfValue(existingClassVal);
/* 162:    */     
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:    */ 
/* 169:    */ 
/* 170:    */ 
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:    */ 
/* 180:227 */     String[] attribValNames = new String[format.classAttribute().numValues()];
/* 181:228 */     for (int i = 0; i < attribValNames.length; i++) {
/* 182:229 */       attribValNames[i] = format.classAttribute().value(i);
/* 183:    */     }
/* 184:231 */     this.m_ClassValueCombo.setModel(new DefaultComboBoxModel(attribValNames));
/* 185:232 */     if (attribValNames.length > 0) {
/* 186:234 */       if (classValIndex >= 0)
/* 187:    */       {
/* 188:235 */         this.m_ClassValueCombo.setSelectedIndex(classValIndex);
/* 189:    */       }
/* 190:    */       else
/* 191:    */       {
/* 192:237 */         String toSet = existingCopy != null ? existingCopy : attribValNames[0];
/* 193:238 */         this.m_ClassValueCombo.setSelectedItem(toSet);
/* 194:    */       }
/* 195:    */     }
/* 196:242 */     if (!this.m_displayValNames)
/* 197:    */     {
/* 198:243 */       add(this.m_holderP, "Center");
/* 199:244 */       this.m_displayValNames = true;
/* 200:    */     }
/* 201:246 */     validate();
/* 202:247 */     repaint();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setObject(Object object)
/* 206:    */   {
/* 207:257 */     if (this.m_classValuePicker != (ClassValuePicker)object)
/* 208:    */     {
/* 209:264 */       this.m_classValuePicker = ((ClassValuePicker)object);
/* 210:267 */       if (this.m_classValuePicker.getConnectedFormat() != null) {
/* 211:268 */         setUpValueSelection(this.m_classValuePicker.getConnectedFormat());
/* 212:    */       }
/* 213:270 */       this.m_backup = this.m_classValuePicker.getClassValue();
/* 214:    */     }
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void customizerClosing()
/* 218:    */   {
/* 219:281 */     this.m_classValuePicker.setClassValue(this.m_backup);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 223:    */   {
/* 224:297 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 228:    */   {
/* 229:307 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 230:    */   }
/* 231:    */   
/* 232:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 233:    */   {
/* 234:312 */     this.m_modifyListener = l;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public void setParentWindow(Window parent)
/* 238:    */   {
/* 239:317 */     this.m_parent = parent;
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassValuePickerCustomizer
 * JD-Core Version:    0.7.0.1
 */